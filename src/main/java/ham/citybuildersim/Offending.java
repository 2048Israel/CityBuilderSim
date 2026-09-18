package ham.citybuildersim;

/**
 * Who is at risk of offending, sorted by reason, and the thefts handed to
 * them.
 *
 * ==================== WHO IS AT RISK OF OFFENDING (2026-09-11) ====================
 *
 * Every adult at liberty, sorted once into Jerus's groups, cell by cell -
 * because the cells are the only place the reasons meet: the pool knows
 * who is out of work, the housing match who has no door and who is
 * crowded, and the household books who is going short. Where a group
 * overlaps another, the higher weight counts:
 *
 *   no home        the evicted; the families and the seekers with no door  5
 *   past EI        out of work with a home, EI run out                      4
 *   short of money of what is left, the share that cannot buy a basket     3
 *   on EI          out of work with a home, still drawing                   2
 *   crowded        of what is left, the share doubled up or flatsharing     2
 *   no reason      everybody else                                          .1
 *
 * The retired, the orphans and the prisoners are nobody's offenders: the
 * adult band is 18 to 70, and a prisoner is not at liberty.
 *
 * Each cell's weight - its adults times their weights, plus what the
 * police are missing - is what the thefts are handed back by.
 *
 * ==================== WHERE IT CAME FROM ====================
 *
 * This was Game's "WHO IS AT RISK OF OFFENDING" section until 2026-09-18,
 * when it moved out with its text intact: crimeCauses() as causes(), the
 * two static helpers it sorts with, steal() under its own name, and
 * unhousedShareOfCity(). It holds no month flows of its own - the figures
 * live on Crime - so Game keeps only unhousedShareOfCity() as a delegation,
 * because that one is public, and calls the two from the crime step of the
 * month in the same place it always did.
 *
 * WHY IT LEFT. Game.java was 8,200 lines, and a session reading who offends
 * had to carry the month, the save and the treasury with it. A
 * mechanic that has the shape of a class - its own month, its own figures,
 * one place it is called from - is its own file since 2026-09-18, and Game
 * keeps what every reader goes through: the getters, and the order of the
 * month. The interface went the same way the same day. See the project's
 * splitting-game.md.
 */
public final class Offending {

    /**
     * Was Game.crimeCauses(); the body is that one, through Game's getters.
     *
     * @param coverage   this month's police coverage
     * @param cellWeight filled in, per cell in the balance's order
     */
    Crime.Causes causes(Game game, double coverage, double[] cellWeight) {
        FamilyModel families = game.getFamilies();
        HouseholdBalance householdBalance = game.getHouseholdBalance();

        Crime.Causes city = new Crime.Causes();
        if (families == null) return city;
        double fewPolice = Crime.NO_POLICE_WEIGHT * (1 - Math.max(0, Math.min(1, coverage)));
        double doubled = families.doubledUpShare();
        java.util.List<Household> cells = householdBalance.cells();
        for (int i = 0; i < cells.size(); i++) {
            Household c = cells.get(i);
            double h = c.households();
            if (!(h > 0)) continue;
            double shortShare = shortOfMoney(c);
            Crime.Causes cell = new Crime.Causes();

            if (c instanceof UnemployedHousehold u) {
                double noDoor = u.status() == UnemployedHousehold.Status.UNHOUSED ? 1
                        : families.seekerUnhousedShare(FamilyModel.Seeker.UNEMPLOYED);
                double housed = h * (1 - noDoor);
                cell.add(Crime.Cause.NO_HOME, h * noDoor);
                if (u.status() == UnemployedHousehold.Status.OFF_EI) {
                    cell.add(Crime.Cause.PAST_EI, housed);
                } else if (u.status() == UnemployedHousehold.Status.ON_EI) {
                    // Crowded weighs what EI does, so only going short lifts a claimant.
                    cell.add(Crime.Cause.SHORT_OF_MONEY, housed * shortShare);
                    cell.add(Crime.Cause.ON_EI, housed * (1 - shortShare));
                }
            } else if (c instanceof StudentHousehold) {
                sortHoused(cell, h, families.seekerUnhousedShare(FamilyModel.Seeker.STUDENT),
                        shortShare, families.seekerCrowdedShare(FamilyModel.Seeker.STUDENT));
            } else if (c.shape() != null && !c.isRetired()) {
                sortHoused(cell, h * c.shape().membersOf(AgeBand.ADULT),
                        families.unhousedShareOf(c.shape()), shortShare,
                        c.shape() == FamilyStructure.SHARED_ADULTS ? 1 : doubled);
            } else {
                continue;
            }

            double weight = cell.weighted() + fewPolice * cell.adults();
            if (cellWeight != null && i < cellWeight.length) cellWeight[i] = weight;
            if (c instanceof UnemployedHousehold) cell.addPool(weight);
            city.add(cell);
        }
        return city;
    }

    /** A cell's adults with a door or without: no home, then short of money, then crowded, then no reason. */
    private static void sortHoused(Crime.Causes into, double adults, double noDoor,
                                   double shortShare, double crowded) {
        double gone = Math.max(0, Math.min(1, noDoor));
        into.add(Crime.Cause.NO_HOME, adults * gone);
        double housed = adults * (1 - gone);
        into.add(Crime.Cause.SHORT_OF_MONEY, housed * shortShare);
        double rest = housed * (1 - shortShare);
        into.add(Crime.Cause.CROWDED, rest * crowded);
        into.add(Crime.Cause.NO_CAUSE, rest * (1 - crowded));
    }

    /** How far one of a cell's households is from a basket it can afford, 0-1: last month's plan against subsistence. */
    private static double shortOfMoney(Household c) {
        return c.subsistence() > 0
                ? Math.max(0, Math.min(1, 1 - c.planned() / c.subsistence())) : 0;
    }

    /**
     * THE THEFTS: taken half from the households by what they have saved and
     * half from the businesses by what is in their tills, and handed to the
     * offenders' households by how much of the crime is theirs. Jerus: the
     * stolen money "goes to the offenders". What the businesses lose leaves
     * the audit's pools for the households, like a wage; what the households
     * lose stays among them.
     *
     * Was Game.steal(); the body is that one, through Game's getters.
     */
    void steal(Game game, double[] offenderWeight) {
        Crime crime = game.getCrime();
        HouseholdBalance householdBalance = game.getHouseholdBalance();
        EconomyManager economyManager = game.getEconomyManager();

        double wanted = crime.getTheftWanted();
        double weight = 0;
        for (double w : offenderWeight) weight += Math.max(0, w);
        if (!(wanted > 0) || !(weight > 0)) {
            crime.recordStolen(0, 0);
            return;
        }
        double fromHomes = householdBalance.takeFromSavings(wanted * Crime.FROM_HOUSEHOLDS);

        double till = 0;
        for (String s : Sectors.KEYS) till += Math.max(0, economyManager.getSectorCash(s));
        double fromBusinesses = Math.min(wanted * (1 - Crime.FROM_HOUSEHOLDS), till);
        if (fromBusinesses > 0) {
            for (String s : Sectors.KEYS) {
                double held = Math.max(0, economyManager.getSectorCash(s));
                if (held <= 0) continue;
                double taken = fromBusinesses * held / till;
                economyManager.setSectorCash(s, economyManager.getSectorCash(s) - taken);
                economyManager.recordStolen(s, taken);
            }
        }
        householdBalance.creditByWeight(fromHomes + fromBusinesses, offenderWeight);
        crime.recordStolen(fromHomes, fromBusinesses);
    }

    /** The share of the city with no home: the unhoused, and the orphans. Was Game.unhousedShareOfCity(), which now delegates here. */
    public double unhousedShareOfCity(Game game) {
        FamilyModel families = game.getFamilies();

        double people = game.getCohorts().total();
        if (people <= 0) return 0;
        double without = families.getOrphansTotal() + game.getUnemployment().getUnhoused();
        for (double v : families.unhousedPeopleByBand()) without += v;
        return Math.min(1, without / people);
    }
}
