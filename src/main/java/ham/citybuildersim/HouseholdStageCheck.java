package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleFunction;

/**
 * The two adult stages: the young, 18-34, and the established, 35-69, each
 * with a pyramid compartment, households and books of its own (HS2).
 *
 * WHY, AND WHAT ONE BUCKET GOT WRONG. The pyramid held the adults as one band
 * of fifty-two years drained at 1/624 a month, so an eighteen-year-old was as
 * likely to retire this month as a sixty-nine-year-old, a city of young
 * migrants retired its first pensioners in its first month, and a couple of
 * twenty-five and a couple with a teen drew on one pool of adults and one
 * pool of money. Now the young move up at 1/204 and the established retire at
 * 1/420, both at the adult mortality (docs/studies/spec-households.md 3.4 and
 * its 5 for HS2). Every fixture below CAUSES its flow, and every figure is
 * asserted against the dials (AdultStage, FamilyModel's YOUNG_SHARE_OF_
 * shares), never a number; put one adult bucket back and section 1 fails.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. RESIDENCE OF 204 AND 420 MONTHS. A pyramid of young adults alone,
 *      nobody dying or born, spends the young's span among them on average,
 *      and one of established adults the established's; a city of young
 *      adults retires nobody in its first month; the teens who turn 18 are
 *      young, the young who turn 35 established, the retirements the
 *      established's alone; both stages die at the adult mortality; the
 *      adults are the two stages' sum through the month, the arrivals, the
 *      leavers and the save; and a settled city's shares chain the two.
 *
 *   2. AGEING CARRIES THE MONEY. A young household turning 35 moves whole to
 *      the established cell of its shape and tier carrying every stock it
 *      holds at its cell's average, with a competing claimant of its own
 *      stage that a pool reading the net change would have handed it; the
 *      young retire none and the established at their own share; every
 *      stock is conserved. In a real city's months the young move up at the
 *      pyramid's share and carry savings, and the households' identity
 *      (HL1, MoneyAudit.households()) closes every month.
 *
 *   3. NO TEEN SHAPE AMONG THE YOUNG. The families' young form only the
 *      seven shapes AdultStage.forms() names, house no teen, and take the
 *      dials' shares of the babies and the children; the books keep no young
 *      cell for any other shape.
 *
 *   4. THE POOL'S GROUP IS (TIER, STAGE). The census pool hands a young
 *      cell's released money to the young of its tier first, the
 *      established's to the established, and either to the city only when
 *      its own stage has no claimant.
 *
 *   5. 108 WORKING CELLS: 11 shapes established and 7 young at each of the
 *      six tiers, each young cell its own key and every tier's young their
 *      own stock group.
 */
public class HouseholdStageCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** How close two figures must be, relative to the larger of the want and one: every figure here is a product of a few dials and counts. */
    static final double TOLERANCE = 1e-9;

    static void check(String label, double got, double want) {
        boolean ok = Math.abs(got - want) <= TOLERANCE * Math.max(1, Math.abs(want));
        if (!ok) fails++;
        out.printf("%-80s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", got, want);
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-80s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-80s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Every band's mortality times nought: nobody dies, so a stage's only outflow is its own. */
    static double[] nobodyDies() {
        return new double[AgeBand.values().length];
    }

    /** True for a figure nought to the tolerance. */
    static boolean isZero(double x) {
        return Math.abs(x) <= TOLERANCE;
    }

    /** One stock of a cell, every household's. */
    static double total(Household c, ToDoubleFunction<Household> stock) {
        return stock.applyAsDouble(c) * c.households();
    }

    /**
     * A fixture: a balance, and the census the month ends on, cell by cell.
     * A cell put in with cell() is expected at the census as given; a retired
     * shape's census is one total (expectRetired()), as FamilyModel counts it.
     * The outside cells are as they stand.
     */
    static final class Books {
        final HouseholdBalance bal = new HouseholdBalance();
        private final Map<Household, Double> census = new IdentityHashMap<>();
        private final Map<FamilyStructure, Double> retired = new java.util.EnumMap<>(FamilyStructure.class);

        Household cell(FamilyStructure shape, PayTier tier, AdultStage stage, double households, double expected) {
            Household c = bal.cell(shape, tier, stage);
            c.households = households;
            census.put(c, expected);
            return c;
        }

        Books expectRetired(FamilyStructure shape, double total) {
            retired.put(shape, total);
            return this;
        }

        /** The month's census alone, by stage, as the balance reads it. */
        void run() {
            bal.setOutsideCensus(Household::households);
            bal.censusOnly((HouseholdBalance.Census) (shape, tier, stage) -> {
                if (shape.isRetired()) {
                    return stage == AdultStage.ESTABLISHED && tier == PayTier.values()[0]
                            ? retired.getOrDefault(shape, 0.0) : 0;
                }
                Household c = bal.cell(shape, tier, stage);
                return c == null ? 0 : census.getOrDefault(c, c.households());
            });
        }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        AdultStage young = AdultStage.YOUNG, established = AdultStage.ESTABLISHED;
        double upRate = young.monthlyOutflowRate(), retireRate = established.monthlyOutflowRate();

        /* ==================== 1. residence of 204 and 420 months ==================== */
        out.println("--- residence of 204 and 420 months ---");

        check("the young's span is 18 to 35, in months", young.spanMonths(), 12 * (young.getToAge() - young.getFromAge()));
        check("...the established's 35 to 70", established.spanMonths(),
                12 * (established.getToAge() - established.getFromAge()));
        check("...and the two are the adult band's whole span, one after the other",
                young.spanMonths() + established.spanMonths(), 1 / AgeBand.ADULT.monthlyOutflowRate());
        assertTrue("...the young's next stage the established, the established's none",
                young.next() == established && established.next() == null);

        /*
         * The mean residence: with nobody dying, born or arriving, the
         * person-months a stage's people spend in it, over the people it
         * started with, is its span - each month's opening balance summed
         * until a part in a million million is left.
         */
        for (AdultStage s : AdultStage.values()) {
            double start = 1000;
            PopulationCohorts p = HouseholdMemoryCheck.staged(0, 0, 0, start, s == young ? start : 0);
            double personMonths = 0;
            int months = 0;
            while (p.get(s) > start * 1e-12 && months < 100 * s.spanMonths()) {
                personMonths += p.get(s);
                p.advanceMonth(nobodyDies(), 0);
                months++;
            }
            check("a " + s.getLabel().toLowerCase() + "'s mean months in the stage, nobody dying, is its span ("
                    + months + " months run)", personMonths / start, s.spanMonths());
        }

        // One month of a pyramid of both stages, nobody dying, so every move is the stages' own.
        PopulationCohorts both = HouseholdMemoryCheck.staged(0, 0, 600, 1000, 400);
        double youngOpen = both.get(young), estOpen = both.get(established);
        both.advanceMonth(nobodyDies(), 0);
        check("the young move up at the young's rate", both.promotedShare(young), upRate);
        check("...the established retire at the established's", both.promotedShare(established), retireRate);
        check("...the band's retirements are the established's alone",
                both.getPromoted(AgeBand.ADULT), both.getPromoted(established));
        check("the young who turn 35 are established: the established gained them",
                both.get(established), estOpen - both.getPromoted(established) + both.getPromoted(young));
        check("the teens who turn 18 are young: the young gained them",
                both.get(young), youngOpen - both.getPromoted(young) + both.getPromoted(AgeBand.TEEN));
        assertTrue("fixture: teens turned 18 and the young moved up",
                both.getPromoted(AgeBand.TEEN) > 0 && both.getPromoted(young) > 0);

        PopulationCohorts youngCity = HouseholdMemoryCheck.staged(0, 0, 0, 1000, 1000);
        youngCity.advanceMonth();
        check("a city of young adults retires nobody in its first month (one bucket retired 1/624 of them)",
                youngCity.getPromoted(AgeBand.ADULT), 0);
        check("...and nobody reached the seniors", youngCity.get(AgeBand.SENIOR), 0);

        PopulationCohorts dying = HouseholdMemoryCheck.staged(500, 500, 500, 1000, 400);
        dying.advanceMonth();
        double mortality = AgeBand.ADULT.monthlyMortality();
        check("the young die at the adult mortality", dying.getDeaths(young) / 400, mortality);
        check("...the established the same", dying.getDeaths(established) / 600, mortality);
        check("...and the two stages' dead are the band's", dying.getDeaths(young) + dying.getDeaths(established),
                dying.getDeaths(AgeBand.ADULT));
        check("the adults are the two stages after the month", dying.get(AgeBand.ADULT),
                dying.get(young) + dying.get(established));
        double youngShareBefore = dying.get(young) / dying.get(AgeBand.ADULT);
        dying.migrate(700);
        check("...after the arrivals", dying.get(AgeBand.ADULT), dying.get(young) + dying.get(established));
        check("...who arrive in the stages' own proportions", dying.get(young) / dying.get(AgeBand.ADULT),
                youngShareBefore);
        dying.leave(AgeBand.ADULT, 300);
        check("...after the leavers", dying.get(AgeBand.ADULT), dying.get(young) + dying.get(established));
        check("...who leave in the stages' own proportions", dying.get(young) / dying.get(AgeBand.ADULT),
                youngShareBefore);
        PopulationCohorts back = new PopulationCohorts();
        back.restore(PopulationCohorts.saveBands(), dying.toSaveArray());
        check("...and the young come back from the save", back.get(young), dying.get(young));
        check("...the established with them", back.get(established), dying.get(established));

        double eqY = PopulationCohorts.equilibriumShare(young), eqE = PopulationCohorts.equilibriumShare(established);
        check("a settled city's two stages are its adults", eqY + eqE, PopulationCohorts.equilibriumShare(AgeBand.ADULT));
        check("...chained: the established to the young as the young's rate to the established's way out",
                eqE / eqY, upRate / (retireRate + mortality));

        /* ==================== 2. ageing carries the money ==================== */
        out.println("\n--- ageing carries the money ---");

        /*
         * A young couple with a child, ten stage-spans of households, ten of
         * whom turn 35 this month; the established's cell of the same shape
         * and tier, which takes them in; and a competing claimant of the
         * young's own group, a young couple growing by exactly the grown-ups
         * who leave, whom a pool reading the net change would hand the
         * leavers' money. The claimant's own young move up beside them, into
         * the established couples. The established's own retirements run at
         * their share, ten of ten spans, into the retired cell the census
         * says. Every cell ends above Household.EMPTY_CELL, so none is folded.
         */
        Books ageing = new Books();
        PayTier tier = PayTier.SKILLED;
        double youngH = 10 * young.spanMonths(), estH = 10 * established.spanMonths(), rivalH = 2.5 * young.spanMonths();
        double moving = youngH * upRate, retiring = estH * retireRate, rivalUp = rivalH * upRate;
        Household from = HouseholdFlowCheck.holds(
                ageing.cell(FamilyStructure.COUPLE_CHILD, tier, young, youngH, youngH - moving), 300, 20, 2, 5, 10, 1, 3);
        Household into = HouseholdFlowCheck.holds(
                ageing.cell(FamilyStructure.COUPLE_CHILD, tier, established, estH, estH - retiring + moving),
                900, 40, 6, 15, 0, 1.5, 8);
        Household rival = HouseholdFlowCheck.holds(
                ageing.cell(FamilyStructure.COUPLE, tier, young, rivalH, rivalH - rivalUp + moving), 200, 0, 1, 0, 0, 1, 0);
        Household rivalInto = ageing.cell(FamilyStructure.COUPLE, tier, established, 0, rivalUp);
        // The retirees, two grown-ups a household, land in the retired shape the census says: one pensioner a household.
        ageing.expectRetired(FamilyStructure.SENIOR_ALONE, retiring * 2);
        assertTrue("fixture: every cell the month ends with is above the empty cell's half household",
                Math.min(Math.min(moving, rivalUp), retiring * 2) > HouseholdBalance.EMPTY_CELL);

        // What a household of each held, in HouseholdFlowCheck.STOCKS' order.
        double[] fromEach = { 300, 20, 2, 5, 10, 1 }, intoEach = { 900, 40, 6, 15, 0, 1.5 };
        double rivalSavings = total(rival, c -> c.savings);
        double[] heldBefore = HouseholdFlowCheck.held(ageing.bal);
        // Every adult's retiring share at the one bucket's rate: the young's cell would retire 2,040/624 at it.
        ageing.bal.setFlows(AgeBand.ADULT.monthlyOutflowRate(), 0, 0, 0, 0, 0, 0, retireRate, upRate);
        ageing.run();

        HouseholdBalance b = ageing.bal;
        check("the young moved up at their share, of each young cell: 10 of 2,040 and 2.5 of 510",
                b.getFlowHouseholds(HouseholdBalance.MOVING_UP), moving + rivalUp);
        check("...carrying each cell's average savings", b.getFlowSavings(HouseholdBalance.MOVING_UP),
                moving * 300 + rivalUp * 200);
        check("the established retired at their own share; the young retired none",
                b.getFlowHouseholds(HouseholdBalance.RETIRING), retiring);
        for (int k = 0; k < HouseholdFlowCheck.STOCKS.size(); k++) {
            ToDoubleFunction<Household> stock = HouseholdFlowCheck.STOCKS.get(k);
            check("the established's cell holds its own " + HouseholdFlowCheck.STOCK_NAMES[k]
                            + " less the retirees', and the young's who moved up",
                    total(into, stock), (estH - retiring) * intoEach[k] + moving * fromEach[k]);
            check("...the young's cell keeps its average of " + HouseholdFlowCheck.STOCK_NAMES[k],
                    stock.applyAsDouble(from), fromEach[k]);
        }
        check("...the paper carried with them", total(into, c -> c.paper), (estH - retiring) * 8 + moving * 3);
        check("the young claimant of the same tier was handed nothing: it keeps its own, less its own who moved up",
                total(rival, c -> c.savings), rivalSavings - rivalUp * 200);
        check("...whose savings the established couples now hold", total(rivalInto, c -> c.savings), rivalUp * 200);
        check("...and the pool had nothing to net", b.getPoolSavings(), 0);
        double[] heldAfter = HouseholdFlowCheck.held(b), gone = HouseholdFlowCheck.left(b);
        for (int k = 0; k < heldAfter.length; k++) {
            check("what is held of " + HouseholdFlowCheck.STOCK_NAMES[k] + ", and what left, is what was held",
                    heldAfter[k] + gone[k], heldBefore[k]);
        }

        /*
         * ...and in a real city's months: the default city as the probes found
         * it, on scratch files, never a save slot. A founded city of a few
         * houses has every working cell under half a household, folded every
         * month; this one has young of its own.
         */
        Game game = LongPlaytest.probeCity(GameFiles.scratch("householdstagecheck"), null);

        /** Months counted: two years, every one a moving up. */
        final int MONTHS = 24;
        double movedUp = 0, movedUpSavings = 0, worstShare = 0, worstResidual = 0;
        int breaches = 0, shareMonths = 0;
        for (int m = 0; m < MONTHS; m++) {
            HouseholdBalance books = game.getHouseholdBalance();
            double youngHouseholds = 0;
            for (Household c : books.cells()) {
                if (c instanceof WorkingHousehold w && w.stage() == young) youngHouseholds += c.households();
            }
            double share = game.getCohorts().promotedShare(young);
            quietly(() -> game.simulateMonths(1));
            double up = books.getFlowHouseholds(HouseholdBalance.MOVING_UP);
            movedUp += up;
            movedUpSavings += books.getFlowSavings(HouseholdBalance.MOVING_UP);
            if (youngHouseholds > 0) {
                shareMonths++;
                worstShare = Math.max(worstShare, Math.abs(up / youngHouseholds - share));
            }
            MoneyAudit.Households hh = game.getLastHouseholdsAudit();
            worstResidual = Math.max(worstResidual, Math.abs(hh.residual));
            if (Math.abs(hh.residual) > MoneyAudit.CENT && hh.relative() > 1e-7) breaches++;
        }
        out.printf("   %d months: %,.2f households moved up carrying %,.2f of savings; the young %,.0f households of %,.0f%n",
                MONTHS, movedUp, movedUpSavings, game.getFamilies().workingHouseholdsOf(young),
                game.getFamilies().workingHouseholdsOf(young) + game.getFamilies().workingHouseholdsOf(established));
        assertTrue("the young moved up in the city's months, and carried savings", movedUp > 0 && movedUpSavings > 0);
        report("...each month at the pyramid's share of the young's households", shareMonths == MONTHS
                        && worstShare <= TOLERANCE,
                String.format("worst %.3g off the share, %d months of %d with young", worstShare, shareMonths, MONTHS));
        report("the households' identity (HL1) closed every month", breaches == 0,
                String.format("%d breaches, worst residual %,.4f", breaches, worstResidual));

        /* ==================== 3. no teen shape among the young ==================== */
        out.println("\n--- no teen shape among the young ---");

        Set<FamilyStructure> youngShapes = java.util.EnumSet.of(FamilyStructure.SINGLE_ADULT, FamilyStructure.COUPLE,
                FamilyStructure.SINGLE_PARENT, FamilyStructure.COUPLE_BABY, FamilyStructure.COUPLE_CHILD,
                FamilyStructure.COUPLE_BABY_CHILD, FamilyStructure.SHARED_ADULTS);
        boolean formsAsNamed = true, establishedAll = true, noTeenShape = true;
        for (FamilyStructure s : FamilyStructure.values()) {
            formsAsNamed &= young.forms(s) == youngShapes.contains(s);
            establishedAll &= established.forms(s) == !s.isRetired();
            if (s.membersOf(AgeBand.TEEN) > 0) noTeenShape &= !young.forms(s);
        }
        assertTrue("the young form the seven shapes the study names and no other", formsAsNamed);
        assertTrue("...the established every working shape", establishedAll);
        assertTrue("...and no shape with a teen in it is young", noTeenShape);

        double[] jobs = new double[PayTier.values().length];
        jobs[PayTier.UNSKILLED.ordinal()] = 600;
        jobs[PayTier.SKILLED.ordinal()] = 300;
        jobs[PayTier.values().length - 1] = 100;
        double babies = 800, children = 900, teens = 650;
        FamilyModel families = new FamilyModel();
        families.rebuild(HouseholdMemoryCheck.staged(babies, children, teens, 6000, 2400), jobs);
        double elsewhere = 0, youngTeens = 0, estTeenShapes = 0;
        double[] housedYoung = new double[AgeBand.values().length], housedAll = new double[AgeBand.values().length];
        for (FamilyStructure s : FamilyStructure.values()) {
            if (s.isRetired()) continue;
            for (PayTier t : PayTier.values()) {
                double y = families.get(s, t, young), all = families.get(s, t);
                if (!young.forms(s)) elsewhere += y;
                youngTeens += y * s.membersOf(AgeBand.TEEN);
                if (s.membersOf(AgeBand.TEEN) > 0) estTeenShapes += families.get(s, t, established);
                for (AgeBand band : AgeBand.values()) {
                    housedYoung[band.ordinal()] += y * s.membersOf(band);
                    housedAll[band.ordinal()] += all * s.membersOf(band);
                }
            }
        }
        check("the families' young hold no household of a shape they do not form", elsewhere, 0);
        check("...and house no teen", youngTeens, 0);
        assertTrue("fixture: the young formed households and the established the teens' shapes",
                families.workingHouseholdsOf(young) > 0 && estTeenShapes > 0);
        AgeBand[] kids = { AgeBand.BABY, AgeBand.CHILD };
        double[] bandOf = { babies, children };
        for (int k = 0; k < kids.length; k++) {
            AgeBand band = kids[k];
            double share = FamilyModel.youngShareOf(band);
            boolean allHoused = Math.abs(housedAll[band.ordinal()] - bandOf[k]) <= TOLERANCE * bandOf[k];
            if (allHoused) {
                check("every " + band.name().toLowerCase() + " housed: the young house the dial's share of them",
                        housedYoung[band.ordinal()], share * bandOf[k]);
            } else {
                report("the young house no more than the dial's share of the " + band.name().toLowerCase(),
                        housedYoung[band.ordinal()] <= share * bandOf[k] * (1 + TOLERANCE),
                        String.format("%,.2f of %,.2f, the share %,.2f", housedYoung[band.ordinal()], bandOf[k], share * bandOf[k]));
            }
        }
        check("the teens' dial is none", FamilyModel.youngShareOf(AgeBand.TEEN), 0);

        HouseholdBalance shapes = new HouseholdBalance();
        boolean cellsAsFormed = true;
        for (FamilyStructure s : FamilyStructure.values()) {
            if (s.isRetired()) continue;
            for (PayTier t : PayTier.values()) {
                Household c = shapes.cell(s, t, young);
                cellsAsFormed &= (c != null) == young.forms(s)
                        && (c == null || c instanceof WorkingHousehold w && w.stage() == young && w.shape() == s && w.tier() == t);
            }
        }
        assertTrue("the books keep a young cell exactly where the young form the shape", cellsAsFormed);

        /* ==================== 4. the pool's group is (tier, stage) ==================== */
        out.println("\n--- the pool's group is (tier, stage) ---");

        /*
         * No flows: the census's own reshuffle alone. A cell of singles
         * shrinks by 20 households, twenty grown-ups; two couples' cells of
         * the same tier, one of each stage, grow by 10, twenty grown-ups
         * each. A group by tier alone would split the singles' money
         * between them; by (tier, stage) the singles' own stage takes it all.
         */
        for (AdultStage s : AdultStage.values()) {
            AdultStage other = s == young ? established : young;
            Books pool = new Books();
            PayTier t = s == young ? PayTier.SKILLED : PayTier.PROFESSIONAL;
            double each = s == young ? 500 : 800;
            pool.cell(FamilyStructure.SINGLE_ADULT, t, s, 40, 20).savings = each;
            Household mine = pool.cell(FamilyStructure.COUPLE, t, s, 10, 20);
            Household theirs = pool.cell(FamilyStructure.COUPLE, t, other, 10, 20);
            mine.savings = 100;
            theirs.savings = 100;
            double before = pool.bal.totalSavings();
            pool.run();
            String who = s.getLabel().toLowerCase();
            check("the " + who + "'s singles' money goes to their own stage's couples of the tier",
                    total(mine, c -> c.savings), 10 * 100 + 20 * each);
            check("...and none to the other stage's, growing as much in the same tier",
                    total(theirs, c -> c.savings), 10 * 100);
            check("...nothing made or lost", pool.bal.totalSavings(), before);
        }

        // A stage with no claimant of its own: the young's singles shrink, only the established's couples of the tier grow.
        Books city = new Books();
        city.cell(FamilyStructure.SINGLE_ADULT, PayTier.COLLEGE, young, 30, 20).savings = 600;
        Household onlyClaimant = city.cell(FamilyStructure.COUPLE, PayTier.COLLEGE, established, 5, 10);
        city.run();
        check("with no claimant of its own stage the young's money reaches the city, and the established's couples",
                total(onlyClaimant, c -> c.savings), 10 * 600);

        /* ==================== 5. 108 working cells ==================== */
        out.println("\n--- 108 working cells ---");

        int tiers = PayTier.values().length, formsYoung = 0, formsEst = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            if (young.forms(s)) formsYoung++;
            if (established.forms(s)) formsEst++;
        }
        int working = 0, youngCells = 0;
        Set<String> keys = new HashSet<>();
        Set<Integer> youngGroups = new HashSet<>(), otherGroups = new HashSet<>();
        boolean groupByTier = true;
        for (Household c : shapes.cells()) {
            keys.add(c.key());
            if (c instanceof WorkingHousehold w) {
                working++;
                if (w.stage() == young) {
                    youngCells++;
                    youngGroups.add(c.stockGroup());
                    groupByTier &= c.stockGroup() == WorkingHousehold.youngGroup(w.tier())
                            && c.key().endsWith(young.keySuffix());
                    continue;
                }
            }
            otherGroups.add(c.stockGroup());
        }
        check("the working cells are the established's shapes and the young's, at every tier",
                working, (formsEst + formsYoung) * tiers);
        check("...a hundred and eight of them", working, 108);
        check("...forty-two young", youngCells, formsYoung * tiers);
        check("every cell its own key", keys.size(), shapes.cellCount());
        assertTrue("each young cell keyed as young and in its tier's young stock group", groupByTier);
        check("...a young stock group a tier", youngGroups.size(), tiers);
        youngGroups.retainAll(otherGroups);
        assertTrue("...shared with no established, retired or outside cell", youngGroups.isEmpty());
        check("the stock groups: the rows, the retired's tiers and the young's tiers",
                Household.STOCK_GROUPS, Household.ROWS + 2 * tiers);

        out.println();
        if (fails == 0) {
            out.println("Two adult stages: the young move up at 1/204 and the established retire at 1/420, each on books of its own.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }
}
