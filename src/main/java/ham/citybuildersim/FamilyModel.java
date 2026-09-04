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
    public void squeeze(int homesAvailable) {

        doubledUp = 0;
        if (homesAvailable <= 0) return;

        double excess = totalHouseholds() - homesAvailable;
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

        /* ---- valve two: whoever is left doubles up ---- */
        if (excess > 0) {
            doubledUp = Math.min(excess, totalHouseholds() / 2);
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
        double[] out = new double[households.length * PayTier.values().length + 2];
        int i = 0;
        for (double[] row : households) {
            for (double v : row) out[i++] = v;
        }
        out[i++] = unhoused;
        out[i] = doubledUp;
        return out;
    }

    public void restore(double[] saved) {
        int expected = households.length * PayTier.values().length + 2;
        if (saved == null || saved.length != expected) {
            return;   // refused whole, per the standing rule on state arrays
        }
        int i = 0;
        for (double[] row : households) {
            for (int t = 0; t < row.length; t++) row[t] = saved[i++];
        }
        unhoused = saved[i++];
        doubledUp = saved[i];
    }

    public void reset() {
        for (double[] row : households) java.util.Arrays.fill(row, 0);
        unhoused = 0;
        doubledUp = 0;
    }
}
