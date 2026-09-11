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

    /* =====================================================================
       THE HOUSEHOLDS REMEMBER (2026-09-11)

       Jerus: "that way when the model rebuilds it has a reference to try and
       keep but still allow change." Until this, rebuild() built every
       household from nothing every month: the same pyramid and the same jobs
       gave the same families, but nothing a family had been last month
       counted, and the money pool moved wallets between shapes that nobody
       had left.

       Now the builder keeps what still fits. Last month's formed households
       - by shape and pay tier, BEFORE the housing valves turned singles into
       flatshares and doubled families up, because those are answers to this
       month's doors and payslips - are re-placed first, less the share that
       re-forms on its own; any shape the pyramid no longer has the people
       for shrinks by the shortfall; and only the people left over are built
       by the old rule. The pay tiers are then refitted to the jobs, which the
       books require. Jerus's calls: keep what still fits, 1% a month, every
       cell. See claude/the-households-remember.md.

       OFF IN A BARE MODEL. Game switches it on. A FamilyModel a harness builds
       rebuilds from nothing, exactly as the population and housing fixtures
       were written against.
       ===================================================================== */

    /** The share of households that re-form on their own each month. Jerus: 1%. */
    public static final double REFORMING_EACH_MONTH = .01;

    private boolean remembers;
    private final double[][] formed =
            new double[FamilyStructure.values().length][PayTier.values().length];
    private boolean haveFormed;
    private double lastKept, lastReformed, lastNoLongerFit, lastNew;

    /** Switches the memory on; Game does, a bare model does not. */
    public void rememberHouseholds(boolean on) { remembers = on; }
    public boolean remembersHouseholds()       { return remembers; }

    /** Whether there is a record to keep from - false in a new city and a save from before. */
    public boolean hasRecord() { return haveFormed; }

    /** What the builder formed last month, before the valves. The reference. */
    public double getFormed(FamilyStructure shape, PayTier tier) {
        return formed[shape.ordinal()][tier.ordinal()];
    }

    /** Households kept from last month's record this month. */
    public double getLastKept()        { return lastKept; }
    /** Households that re-formed on their own, at REFORMING_EACH_MONTH. */
    public double getLastReformed()    { return lastReformed; }
    /** Households the pyramid no longer had the people for: a death, a child grown, a lost job. */
    public double getLastNoLongerFit() { return lastNoLongerFit; }
    /** Households the builder formed from the people left over. */
    public double getLastNew()         { return lastNew; }

    /* =====================================================================
       THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11)

       Jerus: "we are to add a new household structure called unemployed...
       these will just sum up by age." Every adult used to be put in a family
       at the tier mix of the filled jobs, so the out-of-work shared a
       tier's wages. Now the families are built from the adults who WORK, and
       the rest - the unemployed and the full-time students - are handed to
       the balance as households of their own (UnemployedHousehold,
       StudentHousehold). This class still decides where they LIVE: they are
       one-adult households looking for a door, they share five to a home
       with their own kind when priced out or when doors run short, and what
       no valve can place has no home.

       AND THE CHILDREN THE BUILDER LEAVES OUT. The half-of-what's-possible
       throttle takes a half share of each shape in turn, and only three
       shapes hold a baby and three a teen, so about one child in seven is in
       no household in every city - at full employment, at the equilibrium
       pyramid. Jerus, shown it: "they're the orphans." They are counted
       here, by band, and handed to the balance as OrphanHousehold.
       ===================================================================== */

    /** Adults this month's families were NOT built from: the out of work and the students. */
    private double outsideAdults;

    /** Children no family holds, by band. Babies, children and teens; the adult slots stay zero. */
    private final double[] orphans = new double[AgeBand.values().length];

    /** The groups of one-adult households the matrix does not hold but the housing match does. */
    public enum Seeker {
        UNEMPLOYED("Out of work"), STUDENT("Students");
        private final String label;
        Seeker(String label) { this.label = label; }
        public String label() { return label; }
    }

    private static final int SEEKERS = Seeker.values().length;

    /** One-adult households of each group looking for a door this month. */
    private final double[] seekers = new double[SEEKERS];

    /** ...of whom this many live five to a home, with their own kind only. People, not homes. */
    private final double[] seekersSharing = new double[SEEKERS];

    /** ...and this many doubled up two to a door, with their own kind only. People. */
    private final double[] seekersDoubled = new double[SEEKERS];

    /** ...and this many with no door at all after both valves. People. */
    private final double[] seekersUnhoused = new double[SEEKERS];

    /** What the last match left unplaced, by group, before the valves. People. */
    private final double[] seekersUnplaced = new double[SEEKERS];

    /** ...the same, in the households the match counts - a flatshare is one. */
    private final double[] seekersUnplacedHomes = new double[SEEKERS];

    /** Households of each shape the final match and the doubling valve could not place. */
    private final double[] unhousedByShape = new double[FamilyStructure.values().length];

    /** Households of each shape the last match left unplaced. */
    private final double[] unplacedByShape = new double[FamilyStructure.values().length];

    /** Adults outside the families this month. */
    public double getOutsideAdults() { return outsideAdults; }

    /** Children of this band in no household. Zero for adults and seniors. */
    public double getOrphans(AgeBand band) { return orphans[band.ordinal()]; }

    public double getOrphansTotal() {
        double sum = 0;
        for (double v : orphans) sum += v;
        return sum;
    }

    /**
     * Tells the match who else wants a door this month, before house().
     *
     * @param unemployedHoused out-of-work adults who still have a home to keep
     *                         - the evicted are not looking, they cannot pay
     * @param students         full-time students
     */
    public void setSeekers(double unemployedHoused, double students) {
        seekers[Seeker.UNEMPLOYED.ordinal()] = Math.max(0, unemployedHoused);
        seekers[Seeker.STUDENT.ordinal()] = Math.max(0, students);
        java.util.Arrays.fill(seekersSharing, 0);
        java.util.Arrays.fill(seekersDoubled, 0);
        java.util.Arrays.fill(seekersUnhoused, 0);
        java.util.Arrays.fill(seekersUnplaced, 0);
    }

    public double getSeekers(Seeker g)         { return seekers[g.ordinal()]; }
    public double getSeekersSharing(Seeker g)  { return seekersSharing[g.ordinal()]; }
    public double getSeekersDoubled(Seeker g)  { return seekersDoubled[g.ordinal()]; }
    public double getSeekersUnhoused(Seeker g) { return seekersUnhoused[g.ordinal()]; }

    /** A group's households as doors see them: one each alone, a fifth each sharing. */
    private double seekerHouseholds(int g) {
        double sharing = Math.min(seekers[g], seekersSharing[g]);
        return (seekers[g] - sharing) + sharing / 5;
    }

    /**
     * What one household of the group pays of a door's rent: all of it alone,
     * a fifth sharing, half doubled up, none with no door. The rent the
     * landlords are paid does not move - this only says who pays it.
     */
    public double seekerDoorShare(Seeker g) {
        int i = g.ordinal();
        if (seekers[i] <= 0) return 1;
        // The doors the group is actually behind, over the group: a door each
        // alone (a host and the one doubled up with them split it), a fifth
        // sharing, none outside. The cell is an average, so is this.
        return seekerDoors(i) / seekers[i];
    }

    /** Doors a group is behind: alone, and a fifth of one for each sharer. */
    private double seekerDoors(int g) {
        double sharing = Math.min(seekers[g], seekersSharing[g]);
        double alone = Math.max(0, seekers[g] - sharing - seekersDoubled[g] - seekersUnhoused[g]);
        return alone + sharing / 5;
    }

    /** Every household that wants a door: the family matrix, and the seekers outside it. */
    public double householdsSeekingDoors() {
        double total = totalHouseholds();
        for (int g = 0; g < SEEKERS; g++) total += seekerHouseholds(g);
        return total;
    }

    /** The share of a group with no door. */
    public double seekerUnhousedShare(Seeker g) {
        int i = g.ordinal();
        return seekers[i] > 0 ? Math.min(1, seekersUnhoused[i] / seekers[i]) : 0;
    }

    /** Households of this shape with no door after both valves. */
    public double getUnhoused(FamilyStructure shape) { return unhousedByShape[shape.ordinal()]; }

    /** The share of a shape's households with no door; they pay no rent. */
    public double unhousedShareOf(FamilyStructure shape) {
        double n = totalOf(shape);
        return n > 0 ? Math.min(1, unhousedByShape[shape.ordinal()] / n) : 0;
    }

    /**
     * People with no door, by band: the families both valves failed, and the
     * seekers their own kind's valves failed. The evicted are not here - they
     * are not looking for a door; see Unemployment.
     */
    public double[] unhousedPeopleByBand() {
        double[] out = new double[AgeBand.values().length];
        for (FamilyStructure shape : FamilyStructure.values()) {
            double n = unhousedByShape[shape.ordinal()];
            if (n <= 0) continue;
            for (AgeBand b : AgeBand.values()) out[b.ordinal()] += n * shape.membersOf(b);
        }
        for (int g = 0; g < SEEKERS; g++) out[AgeBand.ADULT.ordinal()] += seekersUnhoused[g];
        return out;
    }

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
        rebuild(cohorts, jobsByTier, 0);
    }

    /**
     * @param outsideAdults adults the families are NOT built from: the
     *                      unemployed and the full-time students, who are
     *                      households of their own since 2026-09-11
     */
    public void rebuild(PopulationCohorts cohorts, double[] jobsByTier, double outsideAdults) {

        for (double[] row : households) java.util.Arrays.fill(row, 0);
        unhoused = 0;
        doubledUp = 0;
        pricedOutShares = 0;
        stillUnplaced = 0;
        java.util.Arrays.fill(orphans, 0);
        java.util.Arrays.fill(unhousedByShape, 0);
        java.util.Arrays.fill(unplacedByShape, 0);

        double babies   = cohorts.get(AgeBand.BABY);
        double children = cohorts.get(AgeBand.CHILD);
        double teens    = cohorts.get(AgeBand.TEEN);
        double allAdults = cohorts.get(AgeBand.ADULT);
        this.outsideAdults = Math.max(0, Math.min(allAdults, outsideAdults));
        // THE FAMILIES ARE THE PEOPLE WHO WORK, and their dependants. See the
        // note on the people outside the families, above.
        double adults   = allAdults - this.outsideAdults;
        double seniors  = cohorts.get(AgeBand.SENIOR);

        boolean keep = remembers && haveFormed;
        lastKept = 0; lastReformed = 0; lastNoLongerFit = 0; lastNew = 0;

        if (adults <= 0 && seniors <= 0) {
            orphans[AgeBand.BABY.ordinal()]  = Math.max(0, babies);
            orphans[AgeBand.CHILD.ordinal()] = Math.max(0, children);
            orphans[AgeBand.TEEN.ordinal()]  = Math.max(0, teens);
            if (keep) {
                double had = 0;
                for (double[] row : formed) for (double v : row) had += v;
                lastReformed = had * REFORMING_EACH_MONTH;
                lastNoLongerFit = had - lastReformed;
            }
            recordFormed();
            return;
        }

        /* ---- 1. seniors, who compete for nothing ---- */
        /*
         * KEPT FIRST, when there is a record: last month's couples and seniors
         * alone, less the share that re-forms, as far as there are seniors for
         * them. A death leaves too few, and both shrink by the shortfall; the
         * survivors go back with everybody else who is left over.
         */
        double keptCouples = 0, keptAlone = 0;
        if (keep) {
            double hadCouples = formed[FamilyStructure.SENIOR_COUPLE.ordinal()][0];
            double hadAlone   = formed[FamilyStructure.SENIOR_ALONE.ordinal()][0];
            keptCouples = hadCouples * (1 - REFORMING_EACH_MONTH);
            keptAlone   = hadAlone * (1 - REFORMING_EACH_MONTH);
            double need = 2 * keptCouples + keptAlone;
            double fits = need > seniors && need > 0 ? Math.max(0, seniors) / need : 1;
            lastReformed += (hadCouples + hadAlone) * REFORMING_EACH_MONTH;
            lastNoLongerFit += (keptCouples + keptAlone) * (1 - fits);
            keptCouples *= fits;
            keptAlone *= fits;
            lastKept += keptCouples + keptAlone;
        }
        double leftSeniors = Math.max(0, seniors - 2 * keptCouples - keptAlone);
        double seniorCouples = leftSeniors * .55 / 2;
        double seniorSingles = leftSeniors - seniorCouples * 2;
        lastNew += seniorCouples + Math.max(0, seniorSingles);
        households[FamilyStructure.SENIOR_COUPLE.ordinal()][0] = keptCouples + seniorCouples;
        households[FamilyStructure.SENIOR_ALONE.ordinal()][0]  = keptAlone + Math.max(0, seniorSingles);

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

        /*
         * ---- 2b. last month's working households, kept ----
         *
         * Every cell less the share that re-forms. Then the people check: a
         * band the kept households need more of than the pyramid has - the
         * babies who turned six, the adults who lost their jobs and left the
         * families - shrinks every shape that needs it by the shortfall, and a
         * shape short of two bands shrinks by the worse. That is always
         * enough: each band's need falls to at most what exists. What it
         * frees goes back to the pool below.
         */
        double keptBefore = 0;
        if (keep) {
            double[][] kept = new double[FamilyStructure.values().length][PayTier.values().length];
            double[] need = new double[remaining.length];
            for (FamilyStructure shape : order) {
                for (PayTier tier : PayTier.values()) {
                    double had = formed[shape.ordinal()][tier.ordinal()];
                    lastReformed += had * REFORMING_EACH_MONTH;
                    kept[shape.ordinal()][tier.ordinal()] = had * (1 - REFORMING_EACH_MONTH);
                    keptBefore += kept[shape.ordinal()][tier.ordinal()];
                }
                double count = 0;
                for (double v : kept[shape.ordinal()]) count += v;
                for (int b = 0; b < remaining.length; b++) need[b] += count * shape.membersOf(AgeBand.values()[b]);
            }
            double[] fits = new double[remaining.length];
            for (int b = 0; b < remaining.length; b++) {
                fits[b] = need[b] > remaining[b] && need[b] > 0 ? Math.max(0, remaining[b]) / need[b] : 1;
            }
            double keptAfter = 0;
            for (FamilyStructure shape : order) {
                double f = 1;
                for (int b = 0; b < remaining.length; b++) {
                    if (shape.membersOf(AgeBand.values()[b]) > 0) f = Math.min(f, fits[b]);
                }
                double count = 0;
                for (PayTier tier : PayTier.values()) {
                    double v = kept[shape.ordinal()][tier.ordinal()] * f;
                    households[shape.ordinal()][tier.ordinal()] += v;
                    count += v;
                }
                keptAfter += count;
                for (int b = 0; b < remaining.length; b++) {
                    remaining[b] -= count * shape.membersOf(AgeBand.values()[b]);
                }
            }
            for (int b = 0; b < remaining.length; b++) remaining[b] = Math.max(0, remaining[b]);
            lastNoLongerFit += keptBefore - keptAfter;
            lastKept += keptAfter;
            keptBefore = keptAfter;
        }

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

        if (keep) {
            double working = 0;
            for (FamilyStructure shape : order) for (double v : households[shape.ordinal()]) working += v;
            lastNew += Math.max(0, working - keptBefore);
            fitTiers(order, tierShare);
        } else {
            // The seniors were counted where they were formed.
            for (FamilyStructure shape : order) for (double v : households[shape.ordinal()]) lastNew += v;
        }

        /* ---- 4. whoever is left over ---- */
        unhoused = Math.max(0, remaining[AgeBand.ADULT.ordinal()]);

        /*
         * ---- 5. and the children nobody took: the orphan section ----
         *
         * Jerus: "they dont even get ei, and its named the orphan section,
         * yes they get sick and die for now." The builder is left as it is;
         * what it leaves is who they are.
         */
        orphans[AgeBand.BABY.ordinal()]  = Math.max(0, remaining[AgeBand.BABY.ordinal()]);
        orphans[AgeBand.CHILD.ordinal()] = Math.max(0, remaining[AgeBand.CHILD.ordinal()]);
        orphans[AgeBand.TEEN.ordinal()]  = Math.max(0, remaining[AgeBand.TEEN.ordinal()]);

        recordFormed();
    }

    /** What this month's builder formed, before the valves: next month's reference. */
    private void recordFormed() {
        for (int s = 0; s < households.length; s++) {
            System.arraycopy(households[s], 0, formed[s], 0, households[s].length);
        }
        haveFormed = true;
    }

    /**
     * THE PAY TIERS FOLLOW THE JOBS, and each shape keeps its total.
     *
     * The books split each tier's wage bill over that tier's households, so
     * the households in a tier have to be the jobs' mix of all of them -
     * today's builder does that by construction, splitting every shape by the
     * same mix. A kept household cannot stay in a tier whose jobs went, so the
     * matrix is refitted: iterative proportional fitting, the columns to the
     * jobs' mix and the rows back to each shape's total, seeded by the kept
     * pattern - which is what decides WHICH shapes move when the jobs do.
     *
     * A tier with jobs and nobody in it is seeded with the city's shape mix;
     * a shape that finds every one of its tiers emptied is seeded with the
     * jobs' mix. Both are the fresh builder's answer for a cell with no past.
     */
    private void fitTiers(FamilyStructure[] shapes, double[] tierShare) {
        int tiers = PayTier.values().length;
        double[] row = new double[shapes.length];
        double total = 0;
        for (int s = 0; s < shapes.length; s++) {
            for (double v : households[shapes[s].ordinal()]) row[s] += v;
            total += row[s];
        }
        if (total <= 0) return;
        double[] target = new double[tiers];
        for (int t = 0; t < tiers; t++) target[t] = total * tierShare[t];

        for (int t = 0; t < tiers; t++) {
            double col = 0;
            for (FamilyStructure shape : shapes) col += households[shape.ordinal()][t];
            if (col <= 0 && target[t] > 0) {
                for (int s = 0; s < shapes.length; s++) {
                    households[shapes[s].ordinal()][t] = row[s] / total * target[t];
                }
            }
        }

        for (int pass = 0; pass < 500; pass++) {
            for (int t = 0; t < tiers; t++) {
                double col = 0;
                for (FamilyStructure shape : shapes) col += households[shape.ordinal()][t];
                double f = col > 0 ? target[t] / col : 0;
                for (FamilyStructure shape : shapes) households[shape.ordinal()][t] *= f;
            }
            double worst = 0;
            for (int s = 0; s < shapes.length; s++) {
                double[] cells = households[shapes[s].ordinal()];
                double sum = 0;
                for (double v : cells) sum += v;
                if (sum <= 0) {
                    for (int t = 0; t < tiers; t++) cells[t] = row[s] * tierShare[t];
                } else {
                    double f = row[s] / sum;
                    for (int t = 0; t < tiers; t++) cells[t] *= f;
                }
            }
            for (int t = 0; t < tiers; t++) {
                double col = 0;
                for (FamilyStructure shape : shapes) col += households[shape.ordinal()][t];
                worst = Math.max(worst, Math.abs(col - target[t]));
            }
            if (worst <= 1e-9 * Math.max(1, total)) break;
        }
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

    /** Households with no door of their own, living in somebody else's: the families', and the seekers' with their own kind. */
    public double getDoubledUpHouseholds() {
        double total = doubledUp;
        for (double v : seekersDoubled) total += v;
        return total;
    }

    public double getSharedHouseholds() {
        return totalOf(FamilyStructure.SHARED_ADULTS);
    }

    /** Homes actually occupied, counting doubled-up households as one home. */
    public double homesNeeded() {
        double seekerHomes = 0;
        for (int g = 0; g < SEEKERS; g++) {
            seekerHomes += seekerHouseholds(g) - seekersDoubled[g];
        }
        return Math.max(0, totalHouseholds() + seekerHomes - doubledUp);
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
        // The seekers crowd the same way the singles do - with their own kind.
        double seekerHomes = 0, seekerAlone = 0;
        for (int g = 0; g < SEEKERS; g++) {
            seekerHomes += seekerHouseholds(g);
            seekerAlone += seekers[g] - Math.min(seekers[g], seekersSharing[g]);
        }
        double singles = totalOf(FamilyStructure.SINGLE_ADULT) + seekerAlone;
        double afterSharing = totalHouseholds() + seekerHomes - singles * .8;
        double floor = Math.max(0, afterSharing / 2);
        return floor + doorsThatCannotHelp();
    }

    /**
     * Doors the city has that the households who need one cannot enter.
     *
     * THE VALVE ARITHMETIC ABOVE COUNTS HOUSEHOLDS AND THE CITY COUNTS DOORS,
     * and those were the same question until 2026-09-07, when a home got a
     * SIZE and a studio got the one hard no in the model: a household with a
     * dependant cannot go in it, however empty it is. The floor was never told.
     *
     * So a city with eighty studio doors and sixty family doors reported a
     * floor as though all hundred and forty could take anybody, migration
     * damped against that figure, and families kept arriving to a city whose
     * only spare rooms were ones they were not allowed in. Measured: 10.552
     * households with nowhere at all across months 25-35, in a city that the
     * floor said had room.
     *
     * IT WAS INVISIBLE UNTIL SOMETHING BUILT A STUDIO. No city ever did -
     * CommercialHandler.targetFor() explains why - so for three days every run
     * had studioDoors = 0, this term was identically zero, and the bug sat
     * behind a branch nothing opened. The fix to the rent opened it on the
     * first run.
     *
     * What this adds is the shortfall in FAMILY doors specifically: households
     * that need one, crowded as far as squeeze() allows, against the doors
     * that will take them. Every unit of that shortfall is a door the city
     * owns and cannot use, so the floor rises by it and arrivals stop at the
     * point somebody would actually be sleeping outside - which is what the
     * floor is for. Studio households are not counted the same way, because a
     * single adult CAN take a family door when the studios run out and
     * house() really does let them; the no runs one way only.
     *
     * Zero before house() has ever been called, which is the old behaviour and
     * the right one: with no door census there is nothing to say.
     */
    private double doorsThatCannotHelp() {
        if (lastHomesBySize == null) return 0;

        double needFamilyDoor = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (needsFamilyDoor(shape)) needFamilyDoor += totalOf(shape);
        }
        // Doubling is the only valve open to them - a household with a child
        // cannot be dissolved into a flatshare.
        double after = needFamilyDoor / 2;

        double familyDoors = 0;
        for (int size = STUDIO_MAX_SIZE + 1; size < lastHomesBySize.length; size++) {
            familyDoors += lastHomesBySize[size];
        }
        return Math.max(0, after - familyDoors);
    }

    /**
     * The door census house() was last handed. Kept only so the crowding floor
     * can tell a studio from a family door - see doorsThatCannotHelp().
     */
    private int[] lastHomesBySize;

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

    /* =====================================================================
       TWO SEGMENTS, BECAUSE A STUDIO AND A THREE-BED ARE NOT THE SAME GOOD

       A door that cannot take a child is a different product from one that
       can, and pooling them into one rent hid the only fact that mattered:
       measured on slot 7, the city needed 15,270 doors of size three or more,
       owned 5,107, and was sitting on about 25,000 spare studios. One blended
       price said the housing market was roughly balanced. It was not balanced;
       it was two markets, one in famine and one in glut.

       THE LINE IS THE STUDIO RULE, which already exists and is the one hard no
       in the model: a household with a dependant cannot take a unit of size
       two or less. So the split is

           STUDIO segment  units of size 1-2,  adults-only households of 1-2
           FAMILY segment  units of size 3+,   everybody else

       and every household falls in exactly one of them - a single parent is a
       two-person household in the FAMILY segment, because a child is what puts
       it there.

       SUBSTITUTION IS NOT IN THE PRICE, DELIBERATELY. A single adult really
       can take a house when the studios run out, and house() below really does
       let them - but that shows up as OCCUPANCY, not as a blended price. If a
       spare house quietly softened the studio price, a city short of family
       doors and long on studios would read as balanced again, which is the
       exact reading this split exists to break.
       ===================================================================== */

    /** The part of rentWeight billed on units of size 1-2. */
    private double studioRentWeight;

    /** ...and on units of size 3 and up. The two always sum to rentWeight. */
    private double familyRentWeight;

    public double studioRentWeight() { return studioRentWeight; }
    public double familyRentWeight() { return familyRentWeight; }

    /** Whether a household of this shape needs a door a child is allowed in. */
    public static boolean needsFamilyDoor(FamilyStructure shape) {
        return shape.dependants() > 0 || shape.size() > STUDIO_MAX_SIZE;
    }

    /** The largest unit that counts as a studio. The studio rule's own number. */
    public static final int STUDIO_MAX_SIZE = 2;

    /** Households that could live in a studio: adults only, one or two of them - the seekers outside the families among them. */
    public double studioSeekers() {
        double total = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (!needsFamilyDoor(shape)) total += totalOf(shape);
        }
        for (int g = 0; g < SEEKERS; g++) total += seekerHouseholds(g);
        return total;
    }

    /** Households that need a door of size three or more. */
    public double familySeekers() {
        double total = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (needsFamilyDoor(shape)) total += totalOf(shape);
        }
        return total;
    }

    /**
     * The PEOPLE in each segment, as opposed to the households.
     *
     * What turns a head count into a door count, and the advisor needs it: a
     * hundred more residents is fifty more doors if they are couples and
     * twenty-eight if they are families, and building for the wrong one of
     * those is how a city ends up with three studios for every family door.
     */
    public double studioSeekerHeads() { return seekerHeads(false); }

    public double familySeekerHeads() { return seekerHeads(true); }

    private double seekerHeads(boolean family) {
        double total = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (needsFamilyDoor(shape) == family) total += totalOf(shape) * shape.size();
        }
        if (!family) for (double v : seekers) total += v;
        return total;
    }

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

    /**
     * Puts back BOTH weights the month was billed on. Same reason as above.
     *
     * The one-argument version survives for the older save that carries only
     * the total: it leaves the split where the reloaded match put it, which is
     * the best available answer when the save does not carry one.
     */
    public void setRentWeight(double studio, double family) {
        this.studioRentWeight = Math.max(0, studio);
        this.familyRentWeight = Math.max(0, family);
        this.rentWeight = this.studioRentWeight + this.familyRentWeight;
    }
    public double getCrowdedHouseholds(){ return crowdedHouseholds; }
    public double getRefusedByStudio()  { return refusedByStudio; }

    /** What one let home of this size bills, whoever is in it. */
    public static double rentWeightOf(int unitSize) {
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
        studioRentWeight = 0;
        familyRentWeight = 0;
        crowdedHouseholds = 0;
        refusedByStudio = 0;

        if (homesBySize == null || homesBySize.length < 2) return totalHouseholds();
        lastHomesBySize = homesBySize.clone();

        double[] free = new double[homesBySize.length];
        for (int s = 1; s < homesBySize.length; s++) free[s] = homesBySize[s];
        int widest = homesBySize.length - 1;

        // Largest households first.
        FamilyStructure[] order = FamilyStructure.values().clone();
        java.util.Arrays.sort(order, (x, y) -> Integer.compare(y.size(), x.size()));

        double unplaced = 0;
        java.util.Arrays.fill(unplacedByShape, 0);
        java.util.Arrays.fill(seekersUnplaced, 0);
        java.util.Arrays.fill(seekersUnplacedHomes, 0);

        for (FamilyStructure shape : order) {
            double own = totalOf(shape);

            /*
             * THE SEEKERS QUEUE WITH THE SHAPE THEY LOOK LIKE. An out-of-work
             * adult or a student living alone is a one-adult household, the
             * same door as a single adult who works, so they are matched in
             * the same pass, pro rata - a landlord lets the flat, not the
             * payslip. Five of them sharing are matched with the working
             * flatshares, one door between five.
             */
            double[] extra = new double[SEEKERS];
            double extraTotal = 0;
            for (int g = 0; g < SEEKERS; g++) {
                double sharing = Math.min(seekers[g], seekersSharing[g]);
                if (shape == FamilyStructure.SINGLE_ADULT) extra[g] = seekers[g] - sharing;
                else if (shape == FamilyStructure.SHARED_ADULTS) extra[g] = sharing / 5;
                extraTotal += extra[g];
            }

            double need = own + extraTotal;
            if (need <= 0) continue;
            double asked = need;

            boolean hasDependants = shape.dependants() > 0;

            /*
             * 1. the smallest unit that actually fits.
             *
             * THE STUDIO RULE APPLIES HERE TOO, and for two weeks it did not.
             * The guard sat only on step 2, the crowding pass, on the reading
             * that a studio refuses a child because a child would be CROWDED
             * into it. But a single parent and one child is a two-person
             * household, so step 2 was never reached: the pair fitted a
             * two-person flat exactly and walked straight in, through a door
             * buildings.json marks adultsOnly.
             *
             * Found by HousingCheck's studio-only fixture, which handed the
             * match 235 studios and 225 households and got 104 refusals where
             * 180 households had a child in them. The missing 76 were the
             * single parents.
             */
            int smallest = hasDependants
                    ? Math.max(shape.size(), STUDIO_MAX_SIZE + 1)
                    : shape.size();

            for (int s = smallest; s <= widest && need > 0; s++) {
                double take = Math.min(need, free[s]);
                if (take <= 0) continue;
                free[s] -= take;
                need -= take;
                bill(s, take);
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
                bill(s, take);
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

                // Who it was that went without, pro rata over the pass.
                unplacedByShape[shape.ordinal()] = need * own / asked;
                for (int g = 0; g < SEEKERS; g++) {
                    if (extra[g] <= 0) continue;
                    double households = need * extra[g] / asked;
                    seekersUnplacedHomes[g] += households;
                    seekersUnplaced[g] += shape == FamilyStructure.SHARED_ADULTS
                            ? households * 5 : households;
                }
            }
        }
        return unplaced;
    }

    /**
     * Books a let: its weight to the whole, and to the segment the DOOR is in.
     *
     * The door, not the tenant. A single adult who ends up in a three-bed
     * because the studios were full is renting a family unit and pays the
     * family price - which is also what makes the family segment's price the
     * thing that stops that happening, rather than a discount that hides it.
     */
    private void bill(int unitSize, double homes) {
        double weight = homes * rentWeightOf(unitSize);
        rentWeight += weight;
        if (unitSize <= STUDIO_MAX_SIZE) studioRentWeight += weight;
        else                             familyRentWeight += weight;
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
        java.util.Arrays.fill(seekersDoubled, 0);
        java.util.Arrays.fill(seekersUnhoused, 0);
        if (excess <= 0) return;

        /*
         * EACH WITH ITS OWN KIND. Jerus: "only employed can double up with
         * employed, unemployed can double up with unemployed." So the excess
         * is split by who it was, and each group's valve works on its own.
         * The families' excess is what the match left of the matrix; the
         * seekers' is theirs.
         */
        double seekersExcess = 0;
        for (int g = 0; g < SEEKERS; g++) {
            if (seekersUnplaced[g] <= 0) continue;
            double alone = Math.max(0, seekers[g] - Math.min(seekers[g], seekersSharing[g]));
            double shares = Math.min(seekersUnplaced[g] / 4, alone / 5);
            if (shares > 0) seekersSharing[g] += shares * 5;
            seekersExcess += seekersUnplacedHomes[g];
        }
        excess = Math.max(0, excess - seekersExcess);
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
        java.util.Arrays.fill(unhousedByShape, 0);
        java.util.Arrays.fill(seekersDoubled, 0);
        java.util.Arrays.fill(seekersUnhoused, 0);

        /*
         * THE SEEKERS FIRST, EACH WITH ITS OWN KIND: two to a door, up to half
         * the group - the families' ceiling - and whoever is past that has no
         * door. What they were is taken off what the matrix has to absorb.
         */
        double seekersLeft = 0, seekersStill = 0;
        for (int g = 0; g < SEEKERS; g++) {
            double unplacedPeople = seekersUnplaced[g];
            if (unplacedPeople <= 0) continue;
            seekersLeft += seekersUnplacedHomes[g];
            double ceiling = seekers[g] / 2;
            double absorbed = Math.min(unplacedPeople, ceiling);
            seekersDoubled[g] = absorbed;
            seekersUnhoused[g] = Math.max(0, unplacedPeople - absorbed);
            seekersStill += seekersUnhoused[g];
        }
        left = Math.max(0, left - seekersLeft);

        if (left <= 0) { stillUnplaced = seekersStill; return; }

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
        double matrixStill = Math.max(0, left - absorbed);
        stillUnplaced = matrixStill + seekersStill;

        // Who has no door, by shape, pro rata over what the last match left.
        double unplacedTotal = 0;
        for (double v : unplacedByShape) unplacedTotal += v;
        if (matrixStill > 0 && unplacedTotal > 0) {
            for (int s = 0; s < unhousedByShape.length; s++) {
                unhousedByShape[s] = matrixStill * unplacedByShape[s] / unplacedTotal;
            }
        }
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

    /**
     * The seekers' own affordability valve: the share of each group living
     * alone who share five to a home rather than go short. The same level,
     * the same ceiling, as shareByAffordability() - and with their own kind.
     *
     * @param pressure by Seeker, 0-1: how badly one of them cannot afford a
     *                 door and a basket on what they live on
     */
    public void shareSeekersByAffordability(double[] pressure) {
        if (pressure == null || pressure.length != SEEKERS) return;
        for (int g = 0; g < SEEKERS; g++) {
            double share = Math.max(0, Math.min(MAX_SHARING, pressure[g]));
            double housed = Math.max(0, seekers[g] - seekersUnhoused[g] - seekersDoubled[g]);
            seekersSharing[g] = Math.max(Math.min(seekersSharing[g], housed), housed * share);
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
    /** What the people outside the families add to the save: see toSaveArray(). */
    private static final int OUTSIDE_SLOTS =
            1 + AgeBand.values().length + 4 * Seeker.values().length + FamilyStructure.values().length;

    /** ...and what the households remember: the formed matrix, whether there is one, the month's four counts. */
    private static final int MEMORY_SLOTS =
            FamilyStructure.values().length * PayTier.values().length + 1 + 4;

    public double[] toSaveArray() {
        double[] out = new double[households.length * PayTier.values().length + 5 + OUTSIDE_SLOTS
                + MEMORY_SLOTS];
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
        out[i++] = refusedByStudio;
        /*
         * ...and the residual, appended 2026-09-10, because it stopped being a
         * report and became an input.
         *
         * stillUnplaced is what BOTH VALVES failed to place, which is a
         * two-pass figure: match by size, run the valves on what would not go,
         * match again. The load path deliberately runs ONE pass on the
         * already-squeezed matrix, and for as long as nothing read the number
         * back that was fine - it is a screen figure and the two passes agree
         * to within a household.
         *
         * Since Migration.crowdingFactor() asks it whether the city is full,
         * "to within a household" is a live city and a reloaded one taking
         * different numbers of arrivals, and SaveFileCheck measured it: $5.26
         * apart after a single month. A flow cannot be reconstructed from the
         * state a month ended in - eighth sighting - so it is carried.
         */
        out[i++] = stillUnplaced;
        /*
         * ...AND THE PEOPLE OUTSIDE THE FAMILIES, appended 2026-09-11. Derived
         * every month like the matrix, and carried for the matrix's reason:
         * the first frame of a reloaded city shows the orphans and the
         * seekers the save was struck against, and the seekers' door shares
         * are what the next month's rent is split by.
         */
        out[i++] = outsideAdults;
        for (double v : orphans) out[i++] = v;
        for (int g = 0; g < SEEKERS; g++) {
            out[i++] = seekers[g];
            out[i++] = seekersSharing[g];
            out[i++] = seekersDoubled[g];
            out[i++] = seekersUnhoused[g];
        }
        for (double v : unhousedByShape) out[i++] = v;
        /*
         * ...AND WHAT THE HOUSEHOLDS REMEMBER, appended 2026-09-11. Not
         * derived: last month's formed households are the reference the next
         * rebuild keeps from, and a city that forgot them on a load would be
         * rebuilt from nothing the month after.
         */
        for (double[] cells : formed) for (double v : cells) out[i++] = v;
        out[i++] = haveFormed ? 1 : 0;
        out[i++] = lastKept;
        out[i++] = lastReformed;
        out[i++] = lastNoLongerFit;
        out[i++] = lastNew;
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
        if (saved == null || (saved.length != base && saved.length != base + 2
                && saved.length != base + 3 && saved.length != base + 3 + OUTSIDE_SLOTS
                && saved.length != base + 3 + OUTSIDE_SLOTS + MEMORY_SLOTS)) {
            return;   // refused whole, per the standing rule on state arrays
        }
        carriedUnplaced = -1;
        int i = 0;
        for (double[] row : households) {
            for (int t = 0; t < row.length; t++) row[t] = saved[i++];
        }
        unhoused = saved[i++];
        doubledUp = saved[i++];
        if (saved.length >= base + 2) {
            crowdedHouseholds = saved[i++];
            refusedByStudio   = saved[i++];
        }
        if (saved.length >= base + 3) {
            carriedUnplaced = Math.max(0, saved[i++]);
        }
        outsideAdults = 0;
        java.util.Arrays.fill(orphans, 0);
        java.util.Arrays.fill(seekers, 0);
        java.util.Arrays.fill(seekersSharing, 0);
        java.util.Arrays.fill(seekersDoubled, 0);
        java.util.Arrays.fill(seekersUnhoused, 0);
        java.util.Arrays.fill(unhousedByShape, 0);
        for (double[] cells : formed) java.util.Arrays.fill(cells, 0);
        haveFormed = false;
        lastKept = 0; lastReformed = 0; lastNoLongerFit = 0; lastNew = 0;
        if (saved.length >= base + 3 + OUTSIDE_SLOTS) {
            outsideAdults = saved[i++];
            for (int b = 0; b < orphans.length; b++) orphans[b] = saved[i++];
            for (int g = 0; g < SEEKERS; g++) {
                seekers[g] = saved[i++];
                seekersSharing[g] = saved[i++];
                seekersDoubled[g] = saved[i++];
                seekersUnhoused[g] = saved[i++];
            }
            for (int s = 0; s < unhousedByShape.length; s++) unhousedByShape[s] = saved[i++];
        }
        if (saved.length == base + 3 + OUTSIDE_SLOTS + MEMORY_SLOTS) {
            for (double[] cells : formed) for (int t = 0; t < cells.length; t++) cells[t] = Math.max(0, saved[i++]);
            haveFormed = saved[i++] > .5;
            lastKept = saved[i++];
            lastReformed = saved[i++];
            lastNoLongerFit = saved[i++];
            lastNew = saved[i];
        }
    }

    /**
     * What the save said was left with nowhere, or -1 on a save from before it
     * was carried. See adoptCarriedUnplaced().
     */
    private double carriedUnplaced = -1;

    /**
     * The saved residual wins over the load path's one-pass re-derivation.
     *
     * Game re-runs house() after a restore so rentWeight and the two segment
     * weights are live - that pass has to happen and it sets stillUnplaced as
     * a side effect, from ONE pass where the live figure came from two. This
     * puts the carried figure back over the top of it. A save written before
     * 2026-09-10 carries nothing and keeps the re-derived value, which is the
     * behaviour those saves already had.
     */
    public void adoptCarriedUnplaced() {
        if (carriedUnplaced >= 0) stillUnplaced = carriedUnplaced;
    }

    public void reset() {
        for (double[] row : households) java.util.Arrays.fill(row, 0);
        unhoused = 0;
        doubledUp = 0;
        outsideAdults = 0;
        java.util.Arrays.fill(orphans, 0);
        java.util.Arrays.fill(seekers, 0);
        java.util.Arrays.fill(seekersSharing, 0);
        java.util.Arrays.fill(seekersDoubled, 0);
        java.util.Arrays.fill(seekersUnhoused, 0);
        java.util.Arrays.fill(seekersUnplaced, 0);
        java.util.Arrays.fill(seekersUnplacedHomes, 0);
        java.util.Arrays.fill(unhousedByShape, 0);
        java.util.Arrays.fill(unplacedByShape, 0);
        for (double[] cells : formed) java.util.Arrays.fill(cells, 0);
        haveFormed = false;
        lastKept = 0; lastReformed = 0; lastNoLongerFit = 0; lastNew = 0;
    }
}
