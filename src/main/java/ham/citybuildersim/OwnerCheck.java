package ham.citybuildersim;

import java.util.Locale;

/**
 * The owner families (BO1): one two-adult household per company that has
 * listed and not gone broke, holding the company's founding stake alone.
 *
 * Jerus, 2026-11-02, on the households study: business owners are "few in
 * nature and very rich", "one per listed company", "No job, lives of
 * dividends or sells or whatever"; "the others still have shares, they just
 * dont get the initial ownership". Every fixture here is a bare balance and a
 * bare register, the cause set by the fixture and not stood next to.
 *
 *   1. THE LINE OFF: a bare balance that never switched the line on gets
 *      today's founders' grant, by grown-ups, every non-empty cell its share.
 *   2. THE LINE ON, NO OWNER HOUSEHOLD YET: the listing waits (listIfUnlisted
 *      says false, nothing is in issue), the family is reserved, and a
 *      company with a book and no owners raises nothing at its first offering.
 *   3. THE LINE ON, WITH HOUSEHOLDS: the founders' shares go to the owner cell
 *      alone, each family an equal part of every stake; the row, the label and
 *      the save of the line.
 *   4. EVERYONE ELSE STILL BUYS: an offering is taken up by the ordinary cells
 *      and never by the owner cell, even when it is the richest.
 *   5. THE OWNERS TAKE NO JOB: their adults come out of the adult pool with
 *      the outside adults (FamilyModel) and out of the labour force
 *      (PopulationManager).
 *
 *   6. THE LINE SELLS A TWENTIETH A YEAR: the owner cell offers OWNER_SELL_A_MONTH
 *      of each stake at the market, as its own seller class, and nothing else:
 *      not as a household under its cushion, and not a company at the floor.
 *   7. A YEAR AT THE FLOOR SENDS THE FAMILY BACK: OWNER_BROKE_MONTHS running,
 *      a month off the floor starting the count again; a failed company at once.
 *      A family reserved for a company that never lists is let go once the
 *      owner cell holds families.
 *   8. THE LEAVING FAMILY TAKES ITS MONEY, NOT THE SHARES: the line's total of
 *      each company is what it was, the savings and money abroad go to the
 *      cell that grew.
 *   9. A REAL CITY: the playtest's city with the line on, 120 months, counted.
 *  10. A SAVE FROM BEFORE THE OWNER FAMILIES LOADS ITS ROWS: main's statement
 *      and row array, one row short, fed to the readers; the marked figures
 *      come back where the save put them, the owners' row empty.
 */
public class OwnerCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Relative to the larger of the two, so a figure of any size is held to the same 1e-9. */
    static void close(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(1, Math.max(Math.abs(actual), Math.abs(expected)));
        if (!ok) fails++;
        System.out.printf("%-96s %14.6f  expected %14.6f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    /** The owners the fixture's census names: set by a section, read by the balance's outside census. */
    static final double[] owners = new double[1];

    /** A bare balance in today's dollar: a hundred unskilled couples and forty skilled single adults, owners as the census says. */
    static HouseholdBalance town(boolean lineOn) {
        HouseholdBalance hb = new HouseholdBalance();
        hb.seedConstants(PriceLevel.FACTOR);
        hb.setOwnerLine(lineOn);
        hb.setOutsideCensus(c -> c instanceof OwnerHousehold ? owners[0] : 0);
        month(hb);
        return hb;
    }

    /** One month's census, with the owner count in owners[0]. */
    static void month(HouseholdBalance hb) {
        month(hb, 100);
    }

    /** ...with this many unskilled couples (the town's ordinary working cell) beside the forty single adults. */
    static void month(HouseholdBalance hb, int couples) {
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = couples;
        mix[FamilyStructure.SINGLE_ADULT.ordinal()][PayTier.SKILLED.ordinal()] = 40;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 100 * 4;      // the row's take-home stays that of a hundred couples, so one more household shares it
        income[PayTier.SKILLED.ordinal()] = 40 * 6;
        double[] none = new double[HouseholdBalance.ROWS];
        double[] spent = new double[HouseholdBalance.ROWS];
        hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], income, 0, none, spent, .25, .05, 1);
    }

    static Equity register() {
        Equity e = new Equity();
        e.seedConstants(PriceLevel.FACTOR);
        return e;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        int IND = Equity.indexOf(Sectors.INDUSTRY);
        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        double W = DebtManager.WORLD_BASE_RATE;
        double book = 20_000;

        /* ============ 1. the line off: today's grant ============ */
        System.out.println("--- the line off: today's grant ---");
        {
            owners[0] = 0;
            HouseholdBalance hb = town(false);
            Equity e = register();
            double founders = book / e.foundingPrice();
            check("fixture: the line was never switched on", !hb.isOwnerLine() && hb.ownerFamilies() == 0);
            check("a company with equity and no owners lists", e.listIfUnlisted(IND, book, hb));
            close("the founders' shares are all in the households' hands", hb.sharesHeld(IND), founders, 1e-9);
            close("...and the register issued exactly them", e.getShares(IND), founders, 1e-9);
            Household couple = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            Household single = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
            close("a couple holds twice a single adult's stake: by grown-ups, as before", couple.shares[IND], 2 * single.shares[IND], 1e-9);
            check("...and every non-empty cell holds some", couple.shares[IND] > 0 && single.shares[IND] > 0);
            check("...and the owner cell, empty, holds none", hb.owners().shares[IND] == 0 && hb.owners().isEmpty());
            check("...and no family was reserved", !hb.hasOwnerFamily(IND) && hb.ownerFamilies() == 0);
        }

        /* ============ 2. the line on, no owner household yet: the listing waits ============ */
        System.out.println("\n--- the line on, no owner household yet: the listing waits ---");
        owners[0] = 0;
        HouseholdBalance hb = town(true);
        Equity e = register();
        {
            check("fixture: the line is on and the owner cell is empty", hb.isOwnerLine() && hb.owners().isEmpty());
            check("a company with equity does not list: its family does not exist yet", !e.listIfUnlisted(IND, book, hb));
            close("...the register has no shares in issue", e.getShares(IND), 0, 1e-12);
            close("...and no household holds any", hb.sharesHeld(IND), 0, 1e-12);
            check("...but its family is reserved: the census forms it next month",
                    hb.ownerFamilies() == 1 && hb.hasOwnerFamily(IND) && !hb.hasOwnerFamily(RETAIL));

            double raised = e.offer(RETAIL, 5_000, book, hb, W);
            close("an offering by a company with a book and no owners raises nothing", raised, 0, 1e-12);
            check("...opens no offering and issues no share",
                    e.getOfferings(RETAIL) == 0 && e.getOfferedThisMonth(RETAIL) == 0 && e.getShares(RETAIL) == 0);
            check("...and reserves its family too", hb.ownerFamilies() == 2 && hb.hasOwnerFamily(RETAIL));
        }

        /* ============ 3. the line on, with households: the founders' shares to the owners alone ============ */
        System.out.println("\n--- the line on, with households: the founders' shares to the owners alone ---");
        double founders = book / e.foundingPrice();
        Household ownerCell = hb.owners();
        {
            owners[0] = hb.ownerFamilies();     // what the census names after the month: the reserved count
            month(hb);
            close("fixture: the census formed the families the grant reserved", ownerCell.households(), 2, 1e-12);
            check("a company that waited for its family lists", e.listIfUnlisted(IND, book, hb));
            close("the owners' stake times their households is the founders' shares",
                    ownerCell.shares[IND] * ownerCell.households(), founders, 1e-9);
            boolean others = true;
            for (Household c : hb.cells()) if (c != ownerCell && c.shares[IND] != 0) others = false;
            check("every other cell holds none of the company", others);
            close("the households hold what the register issued", hb.sharesHeld(IND), e.getShares(IND), 1e-9);
            close("...which is the founders' shares, and nothing was raised", e.getShares(IND), founders, 1e-9);

            check("the owner cell is its own row, after the prisoners",
                    Household.OWNER_ROW == Household.PRISON_ROW + 1 && ownerCell.row() == Household.OWNER_ROW
                    && Household.ROWS == Household.OWNER_ROW + 1);
            check("...and the rows an older save carries are pinned: ten before the prisons, eleven before the owners (section 10 reads them)",
                    Household.ROWS_BEFORE_PRISON == Household.RETIRED_ROW + 4
                    && Household.ROWS_BEFORE_OWNER == Household.RETIRED_ROW + 5);
            check("...two adults, no wage, no job to split an income by",
                    ownerCell.grownUps() == 2 && ownerCell.size() == 2 && ownerCell.earningWeight() == 0 && ownerCell.tier() == null);
            check("...which keeps its bonds and money abroad but not the households' share trading",
                    ownerCell.canInvest() && !ownerCell.tradesShares());
            check("...and every other cell trades shares by the households' rules",
                    hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED).tradesShares() && hb.prisoners().tradesShares() == hb.prisoners().canInvest());
            check("...the books name the row", new HouseholdAccounts().getRowLabel(HouseholdAccounts.OWNERS).equals("Owner families"));

            // The line goes through a save and back.
            java.util.Map<String, Integer> line = hb.ownerLineForSave();
            check("the line for the save names the companies with a family, in company order",
                    line.size() == 2 && new java.util.ArrayList<>(line.keySet()).equals(java.util.List.of(
                            Equity.COMPANIES[Math.min(IND, RETAIL)], Equity.COMPANIES[Math.max(IND, RETAIL)])));
            HouseholdBalance back = town(true);
            java.util.Map<String, Integer> saved = new java.util.LinkedHashMap<>(line);
            saved.put(Equity.COMPANIES[IND], 7);
            saved.put("No Such Company", 3);
            back.restoreOwnerLine(saved);
            check("a line restored keeps its families and its months at the floor, and skips a key it has no company for",
                    back.ownerFamilies() == 2 && back.hasOwnerFamily(IND) && back.ownerLineForSave().get(Equity.COMPANIES[IND]) == 7);
            back.restoreOwnerLine(null);
            check("a save from before BO1 restores an empty line", back.ownerFamilies() == 0 && !back.hasOwnerFamily(IND));
        }

        /* ============ 4. everyone else still buys ============ */
        System.out.println("\n--- everyone else still buys ---");
        {
            Household couple = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            Household single = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
            couple.savings = 1_000;
            single.savings = 800;
            ownerCell.savings = 5_000_000;    // the richest cell in town, by far
            double ownerSharesBefore = ownerCell.shares[IND], ownerSavingsBefore = ownerCell.savings;
            double coupleSharesBefore = couple.shares[IND], singleSharesBefore = single.shares[IND];
            check("fixture: the owner cell is the richest and would qualify by the households' rules but for the line",
                    ownerCell.savings > couple.savings && ownerCell.canInvest() && !ownerCell.isEmpty());
            double taken = hb.subscribe(IND, 10_000_000, 1.0);
            check("an offering is taken up", taken > 0);
            check("the owner cell buys nothing: its shares unchanged", ownerCell.shares[IND] == ownerSharesBefore);
            check("...and its savings unchanged, richest as it is", ownerCell.savings == ownerSavingsBefore);
            check("an ordinary cell with savings past the cushion buys: the couples",
                    couple.shares[IND] > coupleSharesBefore && couple.savings < 1_000);
            check("...and the single adults", single.shares[IND] > singleSharesBefore && single.savings < 800);
            close("...and the money paid is the money taken", taken,
                    (1_000 - couple.savings) * couple.households() + (800 - single.savings) * single.households(), 1e-9);
        }

        /* ============ 5. the owners take no job ============ */
        System.out.println("\n--- the owners take no job ---");
        {
            int tiers = PayTier.values().length;
            PopulationCohorts c = new PopulationCohorts();
            double[] pyr = new double[AgeBand.values().length + 3];
            for (AgeBand b : AgeBand.values()) pyr[b.ordinal()] = 18_000 * PopulationCohorts.equilibriumShare(b);
            c.restore(PopulationCohorts.saveBands(), pyr);
            double[] jobs = new double[tiers];
            jobs[PayTier.UNSKILLED.ordinal()] = 700;
            jobs[PayTier.SKILLED.ordinal()] = 300;
            double allAdults = c.get(AgeBand.ADULT);
            int N = 7;

            FamilyModel f = new FamilyModel();
            f.rebuild(c, jobs, 0, 0, 2.0 * N);
            check("the families count the owner families: two adults each", Math.abs(f.getOwnerFamilies() - N) < 1e-12
                    && Math.abs(f.getOwnerAdults() - 2.0 * N) < 1e-12);
            close("...they are outside the families, with the outside adults", f.getOutsideAdults(), 2.0 * N, 1e-12);
            close("...so the families hold the adult cohort less them", inFamilies(f) + f.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel three = new FamilyModel();
            three.rebuild(c, jobs, 100, 200, 2.0 * N);
            close("with prisoners and the out of work, all three are outside the families", three.getOutsideAdults(), 100 + 200 + 2.0 * N, 1e-9);
            close("...and the families hold the rest", inFamilies(three) + three.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel clamped = new FamilyModel();
            clamped.rebuild(c, jobs, 100, 200, 1e12);
            close("more owner adults than the adults left are clamped to what is left", clamped.getOwnerAdults(), allAdults - 300, 1e-9);
            close("...the adult cohort is still the families' and the outside's together",
                    inFamilies(clamped) + clamped.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel old = new FamilyModel();
            old.rebuild(c, jobs, 100, 200);
            check("the four-argument form is the owner adults at nothing",
                    old.getOwnerAdults() == 0 && old.getOwnerFamilies() == 0 && old.getOutsideAdults() == 300);

            PopulationManager pm = new PopulationManager();
            pm.setWagesPerType();
            long[] posts = new long[JobType.values().length];
            posts[JobType.NO_DIPLOMA.ordinal()] = 50;
            pm.updateJobs(posts);
            pm.applyPopulation(200, 100);
            double without = pm.getLabourForce();
            double bandsWithout = sum(pm.workforceByBand());
            pm.setOwners(2.0 * N);
            check("fixture: a labour force larger than the owners' adults", without > 2.0 * N);
            close("the labour force is lower by exactly the owners' adults", pm.getLabourForce(), without - 2.0 * N, 1e-12);
            close("...and so is the workforce by band, taken off every band in proportion", sum(pm.workforceByBand()), bandsWithout - 2.0 * N, 1e-9);
            pm.setOwners(1e9);
            close("more owners than the labour force leaves it at zero, never below", pm.getLabourForce(), 0, 1e-12);
            check("...and no band below zero", java.util.Arrays.stream(pm.workforceByBand()).allMatch(v -> v >= 0));
        }

        sells(IND, RETAIL);
        sendsBack(IND, RETAIL);
        takesMoneyNotShares(IND, RETAIL);
        realCity();
        oldSaves();

        System.out.println(fails == 0 ? "\nOwnerCheck: all OK" : "\nOwnerCheck: " + fails + " FAIL");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============ 6. the line sells a twentieth a year ============ */

    /** The exchange's first month with the line on and one family holding every founders' share of one company. */
    static final class Sale {
        Exchange x;
        Equity reg;
        HouseholdBalance hb;
        Household owner;
        double holding, book;
        String who;
    }

    /**
     * @param floor   minFair lifted over the company's fair value, as ShortSellingCheck's worthless company is: the exchange's floor
     * @param control a working cell with shares in the company, in the same state as the owner cell: under its cushion, with a take-home
     */
    static Sale sale(int company, boolean floor, boolean control) {
        Sale s = new Sale();
        owners[0] = 1;
        s.hb = town(true);
        s.reg = register();
        s.book = 20_000;
        s.reg.listIfUnlisted(company, s.book, s.hb);        // the owner cell is not empty: the founders' shares go to it
        s.owner = s.hb.owners();
        s.holding = s.owner.shares[company] * s.owner.households();
        s.owner.savings = 0;
        s.owner.disposable = 4;                             // under its cushion, as an ordinary cell that would sell
        s.who = Exchange.CELL + s.owner.key();
        if (control) {
            Household couple = s.hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            couple.shares[company] = 5;
            couple.savings = 0;
            couple.disposable = 4;
        }
        s.x = ExchangeCheck.exchange();
        if (floor) s.x.seedConstants(Exchange.MIN_FAIR / (10 * s.reg.foundingPrice()));
        ExchangeCheck.step(s.x, s.reg, s.hb, ExchangeCheck.bankWith(100_000), new ExchangeCheck.Firms(),
                ExchangeCheck.bookOf(company, s.book), DebtManager.WORLD_BASE_RATE, 0, 1);
        return s;
    }

    static void sells(int IND, int RETAIL) {
        System.out.println("\n--- the line sells a twentieth a year ---");
        double month = OwnerHousehold.OWNER_SELL_A_MONTH, year = OwnerHousehold.OWNER_SELL_A_YEAR;
        close("a month's sale, twelve times over, is a year's: (1 - OWNER_SELL_A_MONTH)^12 = 1 - OWNER_SELL_A_YEAR",
                Math.pow(1 - month, 12), 1 - year, 1e-12);
        check("...and a month's is a twelfth of a year's and a little more (the compounding), not a twelfth", month > year / 12 && month < year / 12 * 1.05);

        Sale a = sale(IND, false, false);
        double founders = a.book / a.reg.foundingPrice();
        check("fixture: the exchange is open, the company is above its floor and has not traded", a.x.isOpen() && !a.x.atFloor(IND) && !a.x.hasTraded(IND));
        close("fixture: the owner cell holds every founders' share", a.holding, founders, 1e-9);
        close("fixture: ...and the register has issued no other", a.reg.getShares(IND), founders, 1e-9);
        check("fixture: the owner cell is under its cushion and has a take-home, so an ordinary cell would sell",
                a.owner.disposable > 0 && Exchange.shortOfCushion(a.owner) > 0);
        close("after one month the line has offered OWNER_SELL_A_MONTH of its holding at the price (no trade yet: fair value)",
                a.x.getLifePostedSellBy(Exchange.BY_OWNERS), month * a.holding * a.x.fair(IND), 1e-9);
        close("...that many shares rest on the book as the owner cell's ask",
                a.x.bookOf(IND).resting(a.who, OrderBook.Side.SELL), month * a.holding, 1e-9);
        check("...as a price-taker: its ask at the market, which is fair value before a trade, not a price of its own",
                !Double.isNaN(a.x.bestAsk(IND)) && a.x.bestAsk(IND) >= a.x.fair(IND) - 1e-12);
        close("the owner cell posts nothing as a household, though it is under its cushion",
                a.x.getLifePostedSellBy(Exchange.BY_HOUSEHOLDS), 0, 1e-12);
        check("...nor by the households' rules anywhere else: no rebalancing and no sale on value counted",
                a.x.getLifeRebalanceSold() == 0 && a.x.getLifeValueSold() == 0);
        check("...and the seller class is its own: the other classes posted nothing of it",
                a.x.getLifePostedSellBy(Exchange.BY_DESK) == 0 && a.x.getLifePostedSellBy(Exchange.BY_FUND) == 0);

        Sale c = sale(IND, false, true);
        check("control: a working cell with shares in the same state (under its cushion, a take-home) does post as a household",
                c.x.getLifePostedSellBy(Exchange.BY_HOUSEHOLDS) > 0 && c.x.getLifeRebalanceSold() > 0);
        close("...and the owner cell offers just what it did without it", c.x.getLifePostedSellBy(Exchange.BY_OWNERS),
                a.x.getLifePostedSellBy(Exchange.BY_OWNERS), 1e-12);

        Sale f = sale(IND, true, false);
        check("fixture: the company is at the exchange's floor (minFair over its fair value)", f.x.atFloor(IND) && f.x.fair(IND) > f.reg.foundingPrice());
        close("the line is holding it still and offers none of a company at the floor", f.holding, founders, 1e-9);
        close("...nothing is posted by the owners", f.x.getLifePostedSellBy(Exchange.BY_OWNERS), 0, 1e-12);
        check("...and nothing rests as its ask", f.x.bookOf(IND).resting(f.who, OrderBook.Side.SELL) == 0);

        Exchange.State st = a.x.toState();
        Exchange back = ExchangeCheck.exchange();
        check("the owners' offered and filled go through the save and back: appended to the life array, after the value sold",
                st.life.length == 31 && st.life[29] == a.x.getLifePostedSellBy(Exchange.BY_OWNERS) && back.restore(st)
                && back.getLifePostedSellBy(Exchange.BY_OWNERS) == a.x.getLifePostedSellBy(Exchange.BY_OWNERS)
                && back.getLifeFilledSellBy(Exchange.BY_OWNERS) == a.x.getLifeFilledSellBy(Exchange.BY_OWNERS));
        double[] old = java.util.Arrays.copyOf(st.life, 29);
        st.life = old;
        Exchange older = ExchangeCheck.exchange();
        check("...and a save from before reads them as nothing",
                older.restore(st) && older.getLifePostedSellBy(Exchange.BY_OWNERS) == 0 && older.getLifeFilledSellBy(Exchange.BY_OWNERS) == 0);
    }

    /* ============ 7. a year at the floor sends the family back ============ */

    /** Every company in issue, for the fixtures whose families were reserved by a grant alone: the floor is all they are tallied on. */
    static final java.util.function.IntPredicate ALL_LISTED = c -> true;

    static void sendsBack(int IND, int RETAIL) {
        System.out.println("\n--- a year at the floor sends the family back ---");
        int broke = OwnerHousehold.OWNER_BROKE_MONTHS;
        owners[0] = 0;
        HouseholdBalance hb = town(true);
        hb.grantFounders(IND, 100);
        hb.grantFounders(RETAIL, 100);
        check("fixture: two companies have a family", hb.ownerFamilies() == 2 && hb.hasOwnerFamily(IND) && hb.hasOwnerFamily(RETAIL));
        for (int m = 1; m < broke; m++) hb.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        check("after OWNER_BROKE_MONTHS - 1 months at the floor the family is still there",
                hb.hasOwnerFamily(IND) && hb.ownerFamilies() == 2 && hb.ownerLineForSave().get(Equity.COMPANIES[IND]) == broke - 1);
        hb.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        check("after OWNER_BROKE_MONTHS it is gone: the line is down one", !hb.hasOwnerFamily(IND) && hb.ownerFamilies() == 1);
        check("...a company off the floor keeps its family, and its months stay nothing", hb.hasOwnerFamily(RETAIL) && hb.ownerLineForSave().get(Equity.COMPANIES[RETAIL]) == 0);
        hb.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        check("...and a family that has left is not counted again", hb.ownerFamilies() == 1 && !hb.hasOwnerFamily(IND));

        HouseholdBalance run = town(true);
        run.grantFounders(IND, 100);
        for (int m = 1; m < broke; m++) run.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        run.tallyOwnersAtFloor(c -> false, ALL_LISTED);
        check("a month off the floor at OWNER_BROKE_MONTHS - 1 starts the run again",
                run.hasOwnerFamily(IND) && run.ownerLineForSave().get(Equity.COMPANIES[IND]) == 0);
        for (int m = 1; m < broke; m++) run.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        check("...it then takes a full OWNER_BROKE_MONTHS more: OWNER_BROKE_MONTHS - 1 are not enough", run.hasOwnerFamily(IND));
        run.tallyOwnersAtFloor(c -> c == IND, ALL_LISTED);
        check("...and the OWNER_BROKE_MONTHS-th sends it back", !run.hasOwnerFamily(IND) && run.ownerFamilies() == 0);

        HouseholdBalance bank = town(true);
        bank.grantFounders(Equity.BANK, 100);
        bank.grantFounders(IND, 100);
        bank.ownerCompanyFailed(Equity.BANK);
        check("a company that fails takes its family back at once: no month at the floor needed",
                !bank.hasOwnerFamily(Equity.BANK) && bank.ownerFamilies() == 1 && bank.hasOwnerFamily(IND));
        bank.ownerCompanyFailed(Equity.BANK);
        bank.ownerCompanyFailed(RETAIL);
        check("...a company with no family has none to send back, and the others are not touched", bank.ownerFamilies() == 1 && bank.hasOwnerFamily(IND));

        HouseholdBalance off = town(false);
        java.util.Map<String, Integer> line = new java.util.LinkedHashMap<>();
        line.put(Equity.COMPANIES[IND], broke - 1);
        off.restoreOwnerLine(line);
        for (int m = 0; m < 3 * broke; m++) off.tallyOwnersAtFloor(c -> true, ALL_LISTED);
        check("with the line off nothing moves: a family on the books keeps its months however long the floor",
                !off.isOwnerLine() && off.hasOwnerFamily(IND) && off.ownerLineForSave().get(Equity.COMPANIES[IND]) == broke - 1);
        HouseholdBalance none = town(false);
        none.tallyOwnersAtFloor(c -> true, ALL_LISTED);
        check("...and a line off with no family keeps none", none.ownerFamilies() == 0);

        // A family reserved for a company that never lists (the stranger's review, 2026-11-10): waiting while the cell is empty, let go once it is not.
        owners[0] = 0;
        HouseholdBalance wait = town(true);
        check("fixture: the owner cell is empty, so a grant reserves the family and issues nothing", !wait.grantFounders(IND, 100) && wait.owners().isEmpty());
        for (int m = 0; m < broke; m++) wait.tallyOwnersAtFloor(c -> true, c -> false);
        check("a company not in issue waits for its family while the cell is empty, and no floor is counted",
                wait.hasOwnerFamily(IND) && wait.ownerLineForSave().get(Equity.COMPANIES[IND]) == 0);
        HouseholdBalance lists = town(true);
        lists.grantFounders(IND, 100);
        owners[0] = wait.ownerFamilies();
        month(wait);
        month(lists);
        check("fixture: the census formed the family, so the cell holds one", !wait.owners().isEmpty() && !lists.owners().isEmpty());
        check("...the company that lists into it now keeps its family", lists.grantFounders(IND, 100));
        lists.tallyOwnersAtFloor(c -> false, c -> c == IND);
        check("...and its months at the floor are nothing", lists.hasOwnerFamily(IND) && lists.ownerLineForSave().get(Equity.COMPANIES[IND]) == 0);
        wait.tallyOwnersAtFloor(c -> false, c -> false);
        check("the company still not in issue once its family is there lets the family go", !wait.hasOwnerFamily(IND) && wait.ownerFamilies() == 0);
    }

    /* ============ 8. the leaving family takes its money, not the shares ============ */

    /**
     * A town with four owner families, savings and money abroad in the owner cell and its shares in two companies, a working couple
     * cell with some of its own, and a second month's census of these owner and couple counts.
     */
    static HouseholdBalance leaving(int IND, int RETAIL, int ownersNext, int couplesNext) {
        owners[0] = 4;
        HouseholdBalance hb = town(true);
        hb.grantFounders(IND, 800);            // 200 each
        hb.grantFounders(RETAIL, 400);         // 100 each
        Household o = hb.owners();
        o.savings = 50_000;
        o.abroad = 8_000;
        Household couple = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        couple.savings = 1_000;
        couple.shares[IND] = 10;
        owners[0] = ownersNext;
        month(hb, couplesNext);
        return hb;
    }

    static void takesMoneyNotShares(int IND, int RETAIL) {
        System.out.println("\n--- the leaving family takes its money, not the shares ---");
        HouseholdBalance stay = leaving(IND, RETAIL, 4, 100);             // the twin: nobody moves
        HouseholdBalance go = leaving(IND, RETAIL, 3, 101);               // a family leaves the line, a household joins the couples
        Household so = stay.owners(), o = go.owners();
        Household sc = stay.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED), c = go.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        check("fixture: the line is three families now, the couples a household more, and the twin's are as they were",
                Math.abs(o.households() - 3) < 1e-12 && Math.abs(c.households() - 101) < 1e-12
                && Math.abs(so.households() - 4) < 1e-12 && Math.abs(sc.households() - 100) < 1e-12);
        check("fixture: the owner cell was not emptied, which is a cell that shrinks and stays", !o.isEmpty());
        close("the line's total of the first company is what it was", o.shares[IND] * o.households(), 800, 1e-9);
        close("...and of the second", o.shares[RETAIL] * o.households(), 400, 1e-9);
        close("...each family left holds a third more of the first: 200 x 4 / 3", o.shares[IND], 200.0 * 4 / 3, 1e-9);
        close("...and of the second: 100 x 4 / 3", o.shares[RETAIL], 100.0 * 4 / 3, 1e-9);
        close("the twin's owners hold what they did", so.shares[IND] * so.households(), 800, 1e-9);
        close("the couples' shares are theirs alone: the stakes of the family that left did not pass to them (the total, 101 households)",
                c.shares[IND] * c.households(), 10 * 100, 1e-9);
        close("...and none of the second company, which they never held", c.shares[RETAIL] * c.households(), 0, 1e-9);
        close("no share left the city with the family", go.getSharesTakenAway(IND) + go.getSharesTakenAway(RETAIL), 0, 1e-9);
        close("...the households hold, all together, the first company's shares as before", go.sharesHeld(IND), stay.sharesHeld(IND), 1e-9);

        close("each family left has the savings each had: the leaver took the average", o.savings, so.savings, 1e-9);
        close("...and the money abroad", o.abroad, so.abroad, 1e-9);
        double leaver = so.savings;
        close("fixture: the savings a family had are the owner cell's per-household figure", leaver, 50_000, 1e-9);
        close("the couples, a household more, hold the leaver's savings besides their own (total against the twin's)",
                c.savings * c.households() - sc.savings * sc.households(), leaver, 1e-9);
        close("...and its money abroad", c.abroad * c.households() - sc.abroad * sc.households(), so.abroad, 1e-9);

        // The last family: the cell empties, and its stakes go to the households as any emptied cell's do.
        owners[0] = 1;
        HouseholdBalance one = town(true);
        one.grantFounders(IND, 800);
        Household oo = one.owners();
        oo.savings = 50_000;
        double heldBefore = one.sharesHeld(IND);
        owners[0] = 0;
        month(one, 101);
        check("when the last family goes the cell is empty and holds nothing", one.owners().isEmpty() && one.owners().shares[IND] == 0 && one.owners().savings == 0);
        close("...its stake passed to the households, which hold what they and the line held (today's rule for a cell that empties)",
                one.sharesHeld(IND) + one.getSharesTakenAway(IND), heldBefore, 1e-9);
    }

    /* ============ 9. a real city: one family per listed company ============ */

    /**
     * The most of a company with no family the owner cell may hold, as a part of the shares in issue: the line's stakes come with a family,
     * and what is over is the crumbs of the pool a growing cell claims from the cells that shrank (a thousandth of the issue, at most).
     */
    static final double CRUMB = 1e-3;

    /** The game's log while a city runs: kept, and read for the families that rejoin the population and why. */
    static final java.io.ByteArrayOutputStream LOG = new java.io.ByteArrayOutputStream();

    static void realCity() {
        System.out.println("\n--- a real city: one family per listed company ---");
        final int months = 120;
        long started = System.currentTimeMillis();
        java.io.PrintStream real = System.out, was = LongPlaytest.out;
        java.io.PrintStream capture = new java.io.PrintStream(LOG, true);
        boolean flag = LongPlaytest.AUTOBUILD;
        Game g = new Game(GameFiles.scratch("ownercheck-run"), LongPlaytest.founding());
        HouseholdBalance hb = g.getHouseholdBalance();
        hb.setOwnerLine(true);                    // the city's own dial is off (OwnerHousehold.IN_CITIES)
        LongPlaytest.AUTOBUILD = true;
        LongPlaytest.out = capture;
        System.setOut(capture);

        int n = Equity.COMPANIES.length;
        Tally t = new Tally(n);
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
            g.getAutoBuilder().setOn(true, g.getDecisions());
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            t.play(g, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            t.play(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            t.play(g, 5);
            LongPlaytest.advise(g);
            t.play(g, 6);
            t.play(g, months - g.getMonth());
        } finally {
            LongPlaytest.AUTOBUILD = flag;
            LongPlaytest.out = was;
            System.setOut(real);
        }
        double seconds = (System.currentTimeMillis() - started) / 1000.0;
        Exchange ex = g.getExchange();
        System.out.printf("   %d months in %.0f s, %,d people at the end%n", g.getMonth(), seconds, g.getPopulationManager().getPopulation());
        System.out.printf("   families formed %d, dropped %d (at the floor a year %d, a company that failed %d), re-formed %d; %d at the end%n",
                t.formed, t.dropped, t.droppedAtFloor, t.droppedFailed, t.reformed, hb.ownerFamilies());
        System.out.printf("   months the owner cell was empty: %d of %d (%d before its first family)%n",
                t.emptyMonths, g.getMonth(), t.emptyBefore);
        System.out.printf("   the owners offered $%,.0f at the market and sold $%,.0f of it (%.1f%%); the households offered $%,.0f%n",
                ex.getLifePostedSellBy(Exchange.BY_OWNERS), ex.getLifeFilledSellBy(Exchange.BY_OWNERS),
                ex.getLifePostedSellBy(Exchange.BY_OWNERS) > 0 ? 100 * ex.getLifeFilledSellBy(Exchange.BY_OWNERS) / ex.getLifePostedSellBy(Exchange.BY_OWNERS) : 0,
                ex.getLifePostedSellBy(Exchange.BY_HOUSEHOLDS));

        System.out.println("   in issue with no family (their first shares were not founders'): " + (t.otherwise.isEmpty() ? "none" : t.otherwise + " (the month each was first seen)"));
        if (!t.gap.isEmpty()) System.out.println("   the largest stake the owners held in a company without a family: " + t.gap);
        if (!t.disagreement.isEmpty()) System.out.println("   first census disagreement: " + t.disagreement);
        check("the city played the run", g.getMonth() >= months);
        check("the line formed families: a company listed", t.formed > 0);
        check("every month, the line's count is the companies that have a family", t.countsAgreed);
        check("from the first month the owner cell is not empty, the owners hold no real stake (under CRUMB of the shares) in a company without a family, unless it was dropped:"
                + " what they hold is the crumbs of the pool a growing cell claims", t.crumbs < CRUMB);
        check("the owner families' adults are out of the labour force: it is lower by exactly the census's owner adults, every month",
                t.outOfTheLabourForce);
        check("...the family model holds out the same adults, and the cell holds the families the census before it counted (the households step first)", t.censusAgreed);
        check("the owners hold a positive stake in every company whose family exists and is in issue", t.positiveStake);
        check("the owners offered something at the market in the run, and what they sold never exceeded what they offered",
                ex.getLifePostedSellBy(Exchange.BY_OWNERS) > 0
                && ex.getLifeFilledSellBy(Exchange.BY_OWNERS) <= ex.getLifePostedSellBy(Exchange.BY_OWNERS) * (1 + 1e-9));
        check("every family that left said why, once, in the game's log", t.logged == t.dropped);
    }

    /** What a real run counts, month by month. */
    static final class Tally {
        final int n;
        final boolean[] had, leftOnce;
        int formed, dropped, droppedAtFloor, droppedFailed, reformed, emptyMonths, emptyBefore, logged;
        boolean cellEverHeld;
        /** The owner families the census counted last month. */
        double lastCensus;
        /** The first month each check failed, said in the log: what the run found, not only that it did. */
        String gap = "", disagreement = "";
        /** Companies in issue with no family, never granted to founders: the bank (its desk issues its shares), a company that offered on a book of nothing. */
        final java.util.TreeMap<String, Integer> otherwise = new java.util.TreeMap<>();
        /** The most of a company without a family the owners ever held, as a part of its shares in issue. */
        double crumbs;
        boolean countsAgreed = true, outOfTheLabourForce = true, censusAgreed = true, positiveStake = true;

        Tally(int n) {
            this.n = n;
            had = new boolean[n];
            leftOnce = new boolean[n];
        }

        void play(Game g, int months) {
            for (int i = 0; i < months; i++) {
                LongPlaytest.run(g, 1);
                audit(g);
            }
        }

        void audit(Game g) {
            HouseholdBalance hb = g.getHouseholdBalance();
            Household cell = hb.owners();
            Equity reg = g.getEquity();
            PopulationManager pm = g.getPopulationManager();

            int with = 0;
            for (int c = 0; c < n; c++) if (hb.hasOwnerFamily(c)) with++;
            if (with != hb.ownerFamilies()) countsAgreed = false;

            // Who formed and who left since last month; a re-formed family is one that left and came back.
            for (int c = 0; c < n; c++) {
                boolean has = hb.hasOwnerFamily(c);
                if (has && !had[c]) {
                    if (leftOnce[c]) { reformed++; leftOnce[c] = false; } else formed++;
                } else if (!has && had[c]) {
                    dropped++;
                    leftOnce[c] = true;
                }
                had[c] = has;
            }
            // The game's log says why each left (HouseholdBalance.tallyOwnersAtFloor, ownerCompanyFailed).
            String log = LOG.toString();
            LOG.reset();
            for (String line : log.split("\\R")) {
                if (!line.contains("rejoins the working population")) continue;
                logged++;
                if (line.contains("floor")) droppedAtFloor++;
                else if (line.contains("failed")) droppedFailed++;
            }

            if (cell.isEmpty()) {
                emptyMonths++;
                if (!cellEverHeld) emptyBefore++;
            } else {
                cellEverHeld = true;
            }
            if (cellEverHeld) {
                for (int c = 0; c < n; c++) {
                    boolean noFamily = !hb.hasOwnerFamily(c) && !leftOnce[c];
                    // A family is reserved by the same grant that gives the line its shares, so a company the owners hold has one, unless it left.
                    if (noFamily && cell.shares[c] > 0 && reg.getShares(c) > 0) {
                        double part = cell.shares[c] * cell.households() / reg.getShares(c);
                        if (part > crumbs) {
                            crumbs = part;
                            gap = String.format("month %d: the owners hold %.6f shares (%.2e of those in issue) of %s, which has no family", g.getMonth(),
                                    cell.shares[c] * cell.households(), part, Equity.COMPANIES[c]);
                        }
                    }
                    // ...and a company in issue with no family got its first shares otherwise than as founders' shares.
                    if (noFamily && reg.getShares(c) > 0) otherwise.putIfAbsent(Equity.COMPANIES[c], g.getMonth());
                }
            }

            // The census's owner adults: two a family, held out of the labour force by exactly them; the family model holds out the same.
            // The households' step runs at the top of the month, before the census (Game.startOfMonthUpdate), so the cell holds the
            // families the census before it counted.
            double ownerAdults = pm.getOwners();
            FamilyModel fm = g.getFamilies();
            if (Math.abs(ownerAdults - fm.getOwnerAdults()) > 1e-9 * Math.max(1, ownerAdults)
                    || Math.abs(cell.households() - lastCensus) > 1e-9 * Math.max(1, lastCensus)) {
                if (censusAgreed) disagreement = String.format("month %d: the labour force leaves out %.2f owner adults, the family model %.2f, the cell holds %.2f households and the last census counted %.2f",
                        g.getMonth(), ownerAdults, fm.getOwnerAdults(), cell.households(), lastCensus);
                censusAgreed = false;
            }
            lastCensus = fm.getOwnerFamilies();
            double force = pm.getLabourForce();
            pm.setOwners(0);
            double without = pm.getLabourForce();
            pm.setOwners(ownerAdults);
            if (force > 0 && Math.abs(force + ownerAdults - without) > 1e-9 * Math.max(1, without)) outOfTheLabourForce = false;
            if (Math.abs(pm.getOwners() - ownerAdults) > 0) outOfTheLabourForce = false;

            if (!cell.isEmpty()) {
                for (int c = 0; c < n; c++) {
                    if (hb.hasOwnerFamily(c) && reg.getShares(c) > 0 && !(cell.shares[c] > 0)) positiveStake = false;
                }
            }
        }
    }

    /** The adults the family matrix holds. */
    static double inFamilies(FamilyModel f) {
        double adults = 0;
        for (FamilyStructure s : FamilyStructure.values()) adults += f.totalOf(s) * s.membersOf(AgeBand.ADULT);
        return adults;
    }

    static double sum(double[] v) {
        double s = 0;
        for (double x : v) s += x;
        return s;
    }

    /* ============ 10. a save from before the owner families loads its rows ============ */

    /**
     * Main's shapes fed to the two readers keyed to the rows: the statement
     * and the row array of a 0.9.88 save, each row array one row short (the
     * owner row cut from today's). The stranger's review, 2026-11-10: both
     * readers refused them whole, and a constants-only line passed with both
     * faults standing.
     */
    static void oldSaves() {
        System.out.println("\n--- a save from before the owner families loads its rows ---");
        final int now = Household.ROWS, old = Household.ROWS_BEFORE_OWNER, P = Household.PRISON_ROW;

        // The statement: today's state with every figure marked by its place, then the owner row cut from each row array.
        double[] full = new HouseholdAccounts().getStatementState();
        int arrays = 20, scalars = full.length - arrays * now;
        for (int k = 0; k < full.length; k++) full[k] = k + 1;
        double[] cut = new double[scalars + arrays * old];
        double[] expected = full.clone();
        System.arraycopy(full, 0, cut, 0, scalars);
        for (int j = 0; j < arrays; j++) {
            System.arraycopy(full, scalars + j * now, cut, scalars + j * old, old);
            for (int r = old; r < now; r++) expected[scalars + j * now + r] = 0;
        }
        HouseholdAccounts back = new HouseholdAccounts();
        check("fixture: a 0.9.88 statement is the scalars and twenty row arrays of eleven rows (" + cut.length + " numbers)",
                scalars == 19 && cut.length == 19 + 20 * 11 && old == 11);
        check("the statement from before the owner families is read, not refused", back.restoreStatement(cut));
        check("...every figure where the save put it, and the owners' row empty", java.util.Arrays.equals(back.getStatementState(), expected));
        close("...the households' fuel, the last scalar", back.getFuel(), full[scalars - 1], 0);
        close("...the prisoners' fuel, the last row the save carries in its last array", back.getRowFuel(P), full[scalars + (arrays - 1) * now + P], 0);
        close("...and the owners' wages none", back.getRowWages(Household.OWNER_ROW), 0, 0);

        // The row array: the two halves of the hunger and the prisoners' row, marked.
        double[] rows = new double[old * 8 + 5];
        rows[P] = 3.5;                 // the prisoners' savings
        rows[old * 7 + P] = 9;         // ...and their subsistence
        rows[old * 8 + 3] = 0.25;      // the share the shops handed over
        rows[old * 8 + 4] = 17;        // the hungry at full shelves
        HouseholdBalance hb = new HouseholdBalance();
        hb.restore(rows, null);
        close("the row array from before the owner families: the delivered share comes back", hb.getDeliveredShare(), 0.25, 0);
        close("...and the hungry at full shelves", hb.getHungryAtFullShelves(), 17, 0);
        close("...the prisoners' savings from their own row", hb.prisoners().savings(), 3.5, 0);
        close("...and their subsistence, seven blocks on: the save's rows, not today's", hb.prisoners().subsistence(), 9, 0);
        close("...and the owners' cell empty", hb.owners().savings(), 0, 0);
        double[] working = new double[old * 8 + 3];
        working[P] = 2.5;
        HouseholdBalance hb3 = new HouseholdBalance();
        hb3.restore(working, null);
        close("...the shape from before the hunger's halves, eleven rows, read the same way", hb3.prisoners().savings(), 2.5, 0);
    }
}
