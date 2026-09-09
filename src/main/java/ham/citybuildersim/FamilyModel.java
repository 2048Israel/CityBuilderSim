package ham.citybuildersim;

/**
 * How the city's people are arranged into households, and what each earns.
 *
 * ==================== THIS IS A PLACEHOLDER ====================
 *
 * It rebuilds every month, it is saved, and it is displayed. It changes NOTHING.
 * `PopulationCheck` asserts a city with it running is identical to one without.
 * See the note in PopulationCohorts about why that assertion exists.
 *
 * ==============================================================
 *
 * WHAT IT DOES
 *
 * Two inputs: the age pyramid (how many babies, children, teens, adults,
 * seniors) and the employed job mix (how many adults are in each pay tier). Out
 * of those it assembles households - a count for every FamilyStructure crossed
 * with every PayTier.
 *
 * ALLOCATION, NOT SIMULATION. Nobody is tracked. The model takes the totals and
 * arranges them into the shapes that fit, in a fixed order, drawing people down
 * until it runs out. Run it twice on the same inputs and you get the same
 * answer, which is what makes it safe to rebuild from scratch every month
 * instead of carrying households across the tick - and carrying them would be a
 * far larger change than this is meant to be.
 *
 * THE ORDER IS THE MODEL, and it is worth being honest that it is a choice
 * rather than a derivation:
 *
 *   1. Seniors first, into their own households, because they cannot be in any
 *      other shape and so cannot compete for anything.
 *   2. Then the shapes with dependants, largest first, so children actually end
 *      up in families rather than being left over after every adult has been
 *      packed into a childless couple.
 *   3. Then couples, then single adults, to soak up whoever is left.
 *
 * Doing it in any other order changes the answer. Largest-first is the version
 * that leaves no orphans, which is the property worth having.
 *
 * WHAT IT DOES NOT DO, so this list is on the record rather than discovered:
 *
 *   - No multi-generational households. Seniors live alone or as couples.
 *   - No mixed-tier couples, per the design: one tier per household.
 *   - Unemployed adults are given a tier by proportion rather than left out,
 *     because a household with no tier has nowhere to sit in the matrix.
 *   - Households do not persist. Nobody has a family they keep.
 */
public class FamilyModel {

    /**
     * How many households of each shape, at each pay tier.
     *
     * Retired shapes carry no tier, so their whole row sits at index 0 by
     * convention and the other tiers stay empty. Stated because a screen reading
     * a senior row across all six tiers would otherwise look broken.
     */
    private final double[][] households =
            new double[FamilyStructure.values().length][PayTier.values().length];

    /** Adults left over with no household, which should be zero. */
    private double unhoused;

    /* ----------------------------- reading ----------------------------- */

    public double get(FamilyStructure shape, PayTier tier) {
        return households[shape.ordinal()][tier.ordinal()];
    }

    public double totalOf(FamilyStructure shape) {
        double sum = 0;
        for (double v : households[shape.ordinal()]) sum += v;
        return sum;
    }

    public double totalOf(PayTier tier) {
        double sum = 0;
        for (double[] row : households) sum += row[tier.ordinal()];
        return sum;
    }

    /* =====================================================================
       READING THE TIER COLUMN HONESTLY

       totalOf(PayTier) sums a whole column, and the column includes the SENIOR
       rows - they carry no tier, so by convention their entire count sits under
       index 0. Harmless for a display that shows the matrix, and badly wrong for
       anything that treats a column as "the households that earn at this tier":
       every pensioner in the city would be counted as an unskilled earner with
       no wages, which would drag the unskilled cash-flow statement into a
       deficit that belongs to somebody else.

       So anything doing arithmetic on a tier uses these instead.
       ===================================================================== */

    /** Households with an earner at this tier. Excludes the retired rows. */
    public double workingHouseholdsIn(PayTier tier) {
        double sum = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            sum += households[shape.ordinal()][tier.ordinal()];
        }
        return sum;
    }

    /** Everybody living in those households - earners, partners and children. */
    public double peopleIn(PayTier tier) {
        double sum = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            sum += households[shape.ordinal()][tier.ordinal()] * shape.size();
        }
        return sum;
    }

    /** Households with nobody of working age in them. */
    public double retiredHouseholds() {
        double sum = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) sum += totalOf(shape);
        }
        return sum;
    }

    public double retiredPeople() {
        double sum = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) sum += totalOf(shape) * shape.size();
        }
        return sum;
    }

    public double totalHouseholds() {
        double sum = 0;
        for (double[] row : households) for (double v : row) sum += v;
        return sum;
    }

    public double getUnhousedAdults() {
        return unhoused;
    }

    /** People per household, the figure a housing model would eventually want. */
    public double averageHouseholdSize() {
        double people = 0;
        double count = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            double n = totalOf(shape);
            people += n * shape.size();
            count += n;
        }
        return count > 0 ? people / count : 0;
    }

    /* ----------------------------- building ----------------------------- */

    /**
     * Rebuilds every household from the current pyramid and job mix.
     *
     * @param cohorts   the age pyramid
     * @param jobsByTier how many filled jobs sit in each pay tier
     */
    public void rebuild(PopulationCohorts cohorts, double[] jobsByTier) {

        for (double[] row : households) java.util.Arrays.fill(row, 0);
        unhoused = 0;
        doubledUp = 0;
        pricedOutShares = 0;
        stillUnplaced = 0;

        double babies   = cohorts.get(AgeBand.BABY);
        double children = cohorts.get(AgeBand.CHILD);
        double teens    = cohorts.get(AgeBand.TEEN);
        double adults   = cohorts.get(AgeBand.ADULT);
        double seniors  = cohorts.get(AgeBand.SENIOR);

        if (adults <= 0 && seniors <= 0) {
            return;
        }

        /* ---- 1. seniors, who compete for nothing ---- */
        double seniorCouples = seniors * .55 / 2;
        double seniorSingles = seniors - seniorCouples * 2;
        households[FamilyStructure.SENIOR_COUPLE.ordinal()][0] = seniorCouples;
        households[FamilyStructure.SENIOR_ALONE.ordinal()][0]  = Math.max(0, seniorSingles);

        /*
         * ---- 2. what share of adults sits in each tier ----
         *
         * Adults outnumber filled jobs - some are not working, and one household
         * in two has only one earner even when it has two adults. Rather than
         * inventing an employment model here, the TIER MIX is taken from the
         * jobs and applied to all adults: if 70% of filled jobs are unskilled,
         * 70% of households are unskilled households. Crude, and honest about
         * being crude; it is a placeholder for something that will eventually
         * know who actually works.
         */
        double[] tierShare = new double[PayTier.values().length];
        double totalJobs = 0;
        for (double j : jobsByTier) totalJobs += j;

        if (totalJobs > 0) {
            for (int i = 0; i < tierShare.length; i++) {
                tierShare[i] = jobsByTier[i] / totalJobs;
            }
        } else {
            tierShare[PayTier.UNSKILLED.ordinal()] = 1;   // a city with no jobs yet
        }

        /* ---- 3. households with dependants, largest first ---- */
        double[] remaining = { babies, children, teens, adults };

        FamilyStructure[] order = formableShapes();

        for (int i = 0; i < order.length; i++) {
            FamilyStructure shape = order[i];

            double possible = capacityFor(shape, remaining);
            if (possible <= 0) continue;

            /*
             * HALF OF WHAT IS POSSIBLE, for every shape but the last.
             *
             * The throttle used to apply only to shapes with dependants, on the
             * reasoning that the danger was a large family eating every child.
             * That was half the danger. COUPLE has no dependants and two earners,
             * so it sorted ahead of SINGLE_ADULT and took every remaining adult -
             * leaving a city with essentially no single adults at all, which is
             * both wrong on its face and quietly disabled the flatshare valve in
             * squeeze(), since it has nothing to convert.
             *
             * So: every shape takes half, and the LAST one in the order mops up
             * whatever is left. That is what the order was always meant to mean.
             * Single adults sort last - fewest dependants, fewest earners - which
             * is the right place for the mop-up shape, because one adult is the
             * only household that can absorb an odd number of people.
             */
            boolean last = (i == order.length - 1);
            double take = last ? possible : possible * .5;

            place(shape, take, tierShare, remaining);
        }

        /* ---- 4. whoever is left over ---- */
        unhoused = Math.max(0, remaining[AgeBand.ADULT.ordinal()]);
    }

    /* =====================================================================
       WHEN THERE ARE NOT ENOUGH HOMES

       One household, one home. If the households the city has formed outnumber
       its front doors, somebody has to double up - and Jerus's rule is that
       NOBODY IS HOMELESS, so the model crowds instead of counting casualties.

       Two valves, in order, because they are different degrees of bad:

         1. SINGLE ADULTS SHARE, five to a home. What actually happens first
            when housing is tight, and the cheapest to bear - people who would
            rather live alone take a flatshare. Five singles who needed five
            homes now need one, so each share saves four.

         2. FAMILIES DOUBLE UP, two households to a home. The last resort, and
            it needs to exist: a city with few single adults has nothing to give
            under valve one, and without a second valve the model would have to
            invent homelessness at exactly the moment it was told not to.

       Both are visible. `getSharedHouseholds()` and `getDoubledUpHouseholds()`
       are the housing shortage, expressed as the two things a city does about
       it rather than as a number of people sleeping outside.
       ===================================================================== */

    private double doubledUp;

    public double getDoubledUpHouseholds() { return doubledUp; }

    public double getSharedHouseholds() {
        return totalOf(FamilyStructure.SHARED_ADULTS);
    }

    /** Homes actually occupied, counting doubled-up households as one home. */
    public double homesNeeded() {
        return Math.max(0, totalHouseholds() - doubledUp);
    }

    /**
     * The fewest homes this household mix could crowd into before somebody would
     * genuinely have nowhere to go.
     *
     * DERIVED FROM THE TWO VALVES rather than typed in, which matters: it is the
     * number migration stops at, so a made-up constant here would be a made-up
     * population ceiling. Run both valves to exhaustion - every single adult into
     * a flatshare, then the maximum doubling squeeze() permits - and this is what
     * is left:
     *
     *   after flatshares:  households - singles * 4/5
     *   after doubling:    half of that, since squeeze() caps doubling at half
     *
     * Below this figure the model would have to invent homelessness, which it has
     * been told not to do. So migration is damped to zero as the city approaches
     * it, and PopulationCheck asserts the two agree - the crowding floor and the
     * point arrivals stop are the same line, or the guarantee is a fiction.
     *
     * Safe to call before or after squeeze(): singles already converted are no
     * longer counted as convertible, and the households they became are already
     * out of the total, so the answer is the same either way.
     */
    public double minimumHomesTolerable() {
        double singles = totalOf(FamilyStructure.SINGLE_ADULT);
        double afterSharing = totalHouseholds() - singles * .8;
        return Math.max(0, afterSharing / 2);
    }

    /**
     * Crowds households until they fit the homes available.
     *
     * @param homesAvailable front doors the city has
     */
    /* =====================================================================
       PUTTING HOUSEHOLDS BEHIND DOORS THAT FIT

       squeeze() counted doors. It did not care what was behind them, so a
       family of six could live in a studio as long as the city had eighty
       spare studios somewhere - which is how Studio Apartments came to be a
       building with no niche at all: they housed anybody, badly, and lost to a
       House on price every time.

       A home now has a SIZE (BuildingsTemplate.homeSize) and a household has
       to fit it:

         - a household no bigger than its unit lives there comfortably;
         - a bigger one CROWDS, which is what "nobody is homeless" has always
           meant here and what the crowding pressure already measures;
         - and a one- or two-person flat REFUSES a dependant outright. Jerus's
           rule, and the one hard no in the model: you cannot crowd a child
           into a studio, so a city that builds only studios cannot house
           families at all.

       Largest households first, into the smallest unit that fits. Largest
       first because otherwise a city's singles take every House before a
       family gets one; smallest that fits because a family in a four-person
       home while couples queue for it is a waste the market would not make.
       ===================================================================== */

    /** What the landlords can bill for, in person-equivalents. See rentWeight(). */
    private double rentWeight;

    /** Households living somewhere too small for them. */
    private double crowdedHouseholds;

    /** Households a studio turned away because they have a child. */
    private double refusedByStudio;

    /**
     * The rent base: what the let homes add up to, in people of capacity.
     *
     * THE FLAT SETS THE PRICE, FULL STOP. Jerus: "the building earns its rent
     * regardless - if a couple is living in a mansion it is paying a mansion's
     * price." Which is what a lease is. The first version weighted the charge
     * half toward the household in it, on the theory that a big flat with a
     * small tenant would discount - and that is a market story, not a lease. It
     * also quietly made a household's rent depend on its own size again, which
     * is the exact thing the per-door fix had just removed.
     *
     * So a home's rent is its size and nothing else. What an oversized building
     * loses is not price, it is OCCUPANCY: an empty flat earns nothing at all,
     * because house() only counts a unit somebody is actually in.
     */
    public double rentWeight()          { return rentWeight; }

    /**
     * Puts back the weight the month was actually billed on.
     *
     * FOR THE LOAD PATH, and it is the seventh sighting of the same bug class.
     * house() runs during updateWorkforce, part way through a month, against
     * the homes that existed THEN; buildings finish later in the same month, so
     * by the time the save is written the stock has moved on. Re-running the
     * match on the load path therefore produces a different answer from the one
     * the landlords were paid - a stale figure recomputed from the state the
     * month ended in, which is the thing this codebase keeps being caught by.
     * Carried instead. SaveFileCheck measures the difference at eight cents on
     * $482,860 and refuses it, which is what that assertion is for.
     */
    public void setRentWeight(double weight) { this.rentWeight = weight; }
    public double getCrowdedHouseholds(){ return crowdedHouseholds; }
    public double getRefusedByStudio()  { return refusedByStudio; }

    /** What one let home of this size bills, whoever is in it. */
    static double rentWeightOf(int unitSize) {
        return Math.max(0, unitSize);
    }

    /**
     * What one more home of this size would earn, in person-equivalents.
     *
     * WHAT MAKES A STUDIO WORTH BUILDING, OR NOT. The investment advisor used
     * to value a residential building at capacity x rentPrice - the rent it
     * would collect if it were full - which is a number about the building and
     * not about the city. So a city of families kept being offered studios,
     * and the studios kept losing on price, and backlog I1 ("strictly
     * dominated at every land price") was really this: the advisor was pricing
     * a flat nobody in that city could live in.
     *
     * Priced on the biggest household that is currently crowded or homeless and
     * COULD take this unit. An empty flat nobody needs earns nothing, and a
     * flat that gets a family out of a crowded one earns what a family pays.
     */
    public double marginalRentWeight(int unitSize) {
        if (unitSize <= 0) return 0;

        for (FamilyStructure shape : FamilyStructure.values()) {
            if (totalOf(shape) <= 0) continue;
            if (unitSize <= 2 && shape.dependants() > 0) continue;   // the studio rule
            return rentWeightOf(unitSize);   // somebody fits: it bills its size
        }
        return 0;                            // nobody fits: it bills nothing
    }

    /**
     * Matches households to homes by size, and reports what would not fit.
     *
     * @param homesBySize count of finished homes, indexed by unit size
     * @return households with nowhere at all, which squeeze() then crowds
     */
    public double house(int[] homesBySize) {
        rentWeight = 0;
        crowdedHouseholds = 0;
        refusedByStudio = 0;

        if (homesBySize == null || homesBySize.length < 2) return totalHouseholds();

        double[] free = new double[homesBySize.length];
        for (int s = 1; s < homesBySize.length; s++) free[s] = homesBySize[s];
        int widest = homesBySize.length - 1;

        // Largest households first.
        FamilyStructure[] order = FamilyStructure.values().clone();
        java.util.Arrays.sort(order, (x, y) -> Integer.compare(y.size(), x.size()));

        double unplaced = 0;

        for (FamilyStructure shape : order) {
            double need = totalOf(shape);
            if (need <= 0) continue;

            boolean hasDependants = shape.dependants() > 0;

            // 1. the smallest unit that actually fits.
            for (int s = shape.size(); s <= widest && need > 0; s++) {
                double take = Math.min(need, free[s]);
                if (take <= 0) continue;
                free[s] -= take;
                need -= take;
                rentWeight += take * rentWeightOf(s);
            }

            // 2. crowd into whatever is left, biggest first - but never a child
            //    into a studio.
            for (int s = widest; s >= 1 && need > 0; s--) {
                if (hasDependants && s <= 2) continue;
                double take = Math.min(need, free[s]);
                if (take <= 0) continue;
                free[s] -= take;
                need -= take;
                crowdedHouseholds += take;
                rentWeight += take * rentWeightOf(s);
            }

            if (need > 0) {
                // What is left could not be housed at all. If the only empty
                // doors are studios and this household has a child, say so:
                // that is a different problem from a city with no doors, and
                // the player fixes it with a different building.
                double studiosFree = 0;
                for (int s = 1; s <= Math.min(2, widest); s++) studiosFree += free[s];
                if (hasDependants && studiosFree > 0) {
                    refusedByStudio += Math.min(need, studiosFree);
                }
                unplaced += need;
            }
        }
        return unplaced;
    }

    public void squeeze(int homesAvailable) {
        doubledUp = 0;
        if (homesAvailable <= 0) return;
        double excess = totalHouseholds() - homesAvailable;
        squeezeUnplaced(excess);
        noteUnplaced(Math.max(0, totalHouseholds() - homesAvailable));
    }

    /**
     * The same two valves, on households house() could not place.
     *
     * Split out because "how many households have nowhere" is a different
     * question once homes have sizes: a city can have a thousand empty studios
     * and still not house a family, and a count of spare doors cannot say that.
     */
    public void squeezeUnplaced(double excess) {

        doubledUp = 0;
        stillUnplaced = 0;
        if (excess <= 0) return;

        /* ---- valve one: singles move in together, five to a home ---- */
        int singles = FamilyStructure.SINGLE_ADULT.ordinal();
        int shared  = FamilyStructure.SHARED_ADULTS.ordinal();

        double singleCount = totalOf(FamilyStructure.SINGLE_ADULT);
        if (singleCount > 0) {

            // Each share turns five households into one, so it absorbs four.
            double sharesNeeded = excess / 4;
            double sharesPossible = singleCount / 5;
            double shares = Math.min(sharesNeeded, sharesPossible);

            if (shares > 0) {
                // Move the tier mix across with them rather than inventing one.
                for (PayTier tier : PayTier.values()) {
                    double fromTier = households[singles][tier.ordinal()];
                    double portion = singleCount > 0 ? fromTier / singleCount : 0;
                    double moved = shares * 5 * portion;
                    households[singles][tier.ordinal()] -= moved;
                    households[shared][tier.ordinal()] += moved / 5;
                }
                excess -= shares * 4;
            }
        }

        /*
         * VALVE TWO IS NOT RUN HERE. It used to be, and it double-counted:
         * this method is handed what the FIRST match could not place, then
         * valve one turns five singles into one flatshare and frees four doors,
         * and the match is run again - so most of that first figure gets housed
         * after all. Doubling every one of them up as well pinned doubledUp at
         * its ceiling from month forty-two onward and left the genuine leftover
         * with nowhere to go, which LongPlaytest reported as twelve households
         * sleeping outside in a city with spare homes.
         *
         * noteUnplaced() runs valve two, once, against the final match.
         */
    }

    /**
     * Households both valves failed to place. Zero in a city that works.
     *
     * THE HONEST VERSION OF "SOMEBODY IS SLEEPING OUTSIDE". LongPlaytest used
     * to ask whether households outnumbered homes, which was the right question
     * while a home was a home - and stopped being one on 2026-09-07, when homes
     * grew sizes. A city can have five hundred spare studios and still not
     * house a family, and it can equally have one household more than it has
     * doors and place every one of them, because five singles went into one
     * flatshare. Counting doors answers neither. This does.
     */
    private double stillUnplaced;
    public double getStillUnplaced() { return stillUnplaced; }

    /**
     * Records what the FINAL match left over, after both valves have run.
     *
     * The valves change the household shapes - five singles become one
     * flatshare - so the match has to be run again afterwards, and it is that
     * second answer that says whether anybody is actually without a home.
     * Doubling up needs no door of its own, so it absorbs whatever is left.
     */
    public void noteUnplaced(double left) {
        if (left <= 0) { stillUnplaced = 0; return; }

        /*
         * DOUBLING UP NEEDS NO DOOR OF ITS OWN, which is what makes it the last
         * resort: a household with nowhere moves in with one that has
         * somewhere. So whatever the final match could not place is absorbed
         * here, up to the same half-the-city ceiling squeeze() has always used,
         * and only what is past THAT is somebody sleeping outside.
         *
         * This matters more since homes got sizes. A city can have five hundred
         * spare studios and a queue of families, and the families are not
         * homeless - they are crammed in with each other, which is a different
         * and much commoner failure, and the one the player fixes by building
         * the right building rather than more buildings.
         */
        /*
         * Half the households, PLUS ONE for rounding.
         *
         * The pyramid holds fractions of people and the homes are integers, so
         * a city sitting exactly on its crowding floor - every home doubled,
         * which is where migration damping parks it - comes out a tenth of a
         * household over the ceiling and reads as somebody sleeping outside.
         * Measured at 509.261 against a ceiling of 509.131 in a city of 1,018
         * households. The allowance is the same one LongPlaytest's own audit
         * already made for the same reason; it belongs here, where the number
         * is decided, rather than in the harness reading it.
         */
        double ceiling = totalHouseholds() / 2 + 1;
        double room = Math.max(0, ceiling - doubledUp);
        double absorbed = Math.min(left, room);
        doubledUp += absorbed;
        stillUnplaced = Math.max(0, left - absorbed);
    }

    /* =====================================================================
       AND WHEN THEY CANNOT AFFORD ONE

       squeeze() forms flatshares because the city has run out of front doors.
       This forms them because a wage has run out of rent, which is the commoner
       reason and the one the model had no answer to at all: before this, an
       unskilled single adult who could not cover a home and a basket went on
       not covering them, month after month, until the credit ran out and they
       started going hungry. In life they get a flatmate.

       A LEVEL, NOT A RATE. rebuild() wipes every household each month, so
       nothing here can accumulate: the share of a tier's singles who are
       sharing has to be a function of how badly that tier is priced out, every
       month, from scratch. A tier a fifth short of a home puts a fifth of its
       singles in flatshares; one that cannot cover any of it puts nearly all of
       them there.

       Kept apart from the shortage valve deliberately. `getSharedHouseholds()`
       was the housing shortage expressed as a behaviour, and mixing a second
       cause into the same number would have destroyed the one reading it had.
       getPricedOutShares() is the poverty half, counted separately.
       ===================================================================== */

    /** Not everybody doubles up, however dear the rent. Somebody always holds out. */
    public static final double MAX_SHARING = .85;

    private double pricedOutShares;

    /** Flatshares formed because a wage could not cover a home, not because there was none. */
    public double getPricedOutShares() { return pricedOutShares; }

    /**
     * @param pressure per tier, 0-1, from HouseholdAccounts.livingAlonePressure()
     */
    public void shareByAffordability(double[] pressure) {
        pricedOutShares = 0;
        if (pressure == null || pressure.length != PayTier.values().length) return;

        int singles = FamilyStructure.SINGLE_ADULT.ordinal();
        int shared  = FamilyStructure.SHARED_ADULTS.ordinal();

        for (PayTier tier : PayTier.values()) {
            int t = tier.ordinal();
            double share = Math.max(0, Math.min(MAX_SHARING, pressure[t]));
            if (share <= 0) continue;

            double alone = households[singles][t];
            if (alone <= 0) continue;

            // Five to a home, so five singles become one household.
            double moving = alone * share;
            households[singles][t] -= moving;
            households[shared][t]  += moving / 5;
            pricedOutShares += moving / 5;
        }
    }

    /** How many of this shape the remaining people could fill. */
    private double capacityFor(FamilyStructure shape, double[] remaining) {
        double limit = Double.MAX_VALUE;
        for (AgeBand b : AgeBand.values()) {
            if (b == AgeBand.SENIOR) continue;
            int need = shape.membersOf(b);
            if (need <= 0) continue;
            limit = Math.min(limit, remaining[b.ordinal()] / need);
        }
        return limit == Double.MAX_VALUE ? 0 : Math.max(0, limit);
    }

    /** Commits a number of households of one shape, split across the tiers. */
    private void place(FamilyStructure shape, double count,
                       double[] tierShare, double[] remaining) {

        if (count <= 0) return;

        for (PayTier tier : PayTier.values()) {
            households[shape.ordinal()][tier.ordinal()] += count * tierShare[tier.ordinal()];
        }

        for (AgeBand b : AgeBand.values()) {
            if (b == AgeBand.SENIOR) continue;
            remaining[b.ordinal()] -= count * shape.membersOf(b);
        }
    }

    /**
     * Working-age shapes, most dependants first, then most adults.
     *
     * Sorted rather than relying on the enum's declaration order, because the
     * order IS the model - see the class comment - and leaving it implicit in
     * how somebody happened to type the enum is exactly the kind of thing that
     * changes behaviour when a new shape is added in the middle.
     */
    private static FamilyStructure[] byDependantsDescending() {
        FamilyStructure[] all = FamilyStructure.values().clone();
        java.util.Arrays.sort(all, (x, y) -> {
            int byDeps = Integer.compare(y.dependants(), x.dependants());
            return byDeps != 0 ? byDeps : Integer.compare(y.earners(), x.earners());
        });
        return all;
    }

    /**
     * The shapes rebuild() may actually form, in the order it forms them.
     *
     * Two are excluded, for different reasons:
     *
     *   - SENIOR_ALONE and SENIOR_COUPLE are placed by hand in step one, out of
     *     a band nothing else draws from.
     *   - SHARED_ADULTS is never formed by choice. Single adults prefer to live
     *     alone; a flatshare only appears in squeeze(), when there are not enough
     *     homes, which is what makes its existence a signal rather than a shape.
     *
     * Returned as a list rather than filtered inside the loop because the LAST
     * entry is now load-bearing - it is the shape that takes the remainder - and
     * "last after the skips" is not something a loop with continues can say.
     */
    private static FamilyStructure[] formableShapes() {
        java.util.List<FamilyStructure> out = new java.util.ArrayList<>();
        for (FamilyStructure shape : byDependantsDescending()) {
            if (shape.isRetired()) continue;
            if (shape == FamilyStructure.SHARED_ADULTS) continue;
            out.add(shape);
        }
        return out.toArray(new FamilyStructure[0]);
    }

    /* ----------------------------- saving ----------------------------- */

    /**
     * Flattened row by row.
     *
     * Rebuilt from scratch every month from the pyramid and the jobs, so strictly
     * this is derived state and need not be saved at all. It is saved anyway for
     * one reason: a reloaded city should look identical to the one that was
     * saved on the very first frame, not after the next tick has run. This
     * codebase has been caught by that gap before - a reloaded save took several
     * months to settle back to its real numbers.
     */
    public double[] toSaveArray() {
        double[] out = new double[households.length * PayTier.values().length + 4];
        int i = 0;
        for (double[] row : households) {
            for (double v : row) out[i++] = v;
        }
        out[i++] = unhoused;
        out[i++] = doubledUp;
        /*
         * The two placement counters, appended 2026-09-09. They are what
         * separates "this city has no doors" from "this city built the wrong
         * shape of door", and nothing could tell the difference on a freshly
         * loaded save because both came back as zero. refusedByStudio in
         * particular is the whole answer to why a city can hold fifteen
         * thousand empty flats and ten thousand doubled-up families at once.
         */
        out[i++] = crowdedHouseholds;
        out[i] = refusedByStudio;
        return out;
    }

    /**
     * Puts the households back.
     *
     * TWO LENGTHS ACCEPTED, deliberately. The array grew by two on 2026-09-09
     * and refusing every older save whole would have thrown away the entire
     * household mix of every city written before that date to gain two counters
     * those cities never had. A short array restores what it carries and leaves
     * the two at zero, which is exactly the state those saves loaded in anyway.
     * Anything that is neither length is still refused whole.
     */
    public void restore(double[] saved) {
        int base = households.length * PayTier.values().length + 2;
        if (saved == null || (saved.length != base && saved.length != base + 2)) {
            return;   // refused whole, per the standing rule on state arrays
        }
        int i = 0;
        for (double[] row : households) {
            for (int t = 0; t < row.length; t++) row[t] = saved[i++];
        }
        unhoused = saved[i++];
        doubledUp = saved[i++];
        if (saved.length == base + 2) {
            crowdedHouseholds = saved[i++];
            refusedByStudio   = saved[i];
        }
    }

    public void reset() {
        for (double[] row : households) java.util.Arrays.fill(row, 0);
        unhoused = 0;
        doubledUp = 0;
    }
}
