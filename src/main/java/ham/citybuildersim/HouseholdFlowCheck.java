package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * Money follows people: the households' balance carries each month's own flows
 * - the retiring, the ageing on, the dead and the leavers - with the stocks
 * they hold, before the census pool nets what is left (HS1).
 *
 * WHY, AND WHAT THE OLD READING GOT WRONG. The pool sees only each cell's net
 * change, so a retiring couple's savings stayed in their pay row for whoever
 * joined it, a household coming into the retired row was handed whatever the
 * strangers leaving other rows released, the survivor of a senior couple kept
 * half, and a leaver's money stayed in the city whenever any cell grew. Every
 * fixture below CAUSES one flow and puts a competing claimant in the same
 * tier, a cell that grows by exactly the households the flow takes out of the
 * row, so a pool that read the net change alone would hand the money to the
 * wrong cell. Sections 1 to 7 run the month's census alone
 * (HouseholdBalance.censusOnly()), with no month settled and nobody arriving
 * with a buffer, so every figure is exact; section 8 counts the flows in a
 * real city's months.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. A QUIET MONTH MOVES NOTHING. A balance holding stocks, no flow set and
 *      a census equal to its counts comes out with every stock's total as it
 *      was and every flow record at nought.
 *
 *   2. A RETIREMENT AND AN AGEING ON CARRY THE CELL'S AVERAGE, stock by stock,
 *      into the retired cell of the SAME FORMER TIER, and the cell they leave
 *      keeps its average on fewer households.
 *
 *   3. A PARTNER'S DEATH LEAVES THE SURVIVOR EVERYTHING: the household goes
 *      whole to the alone cell of its row and tier, a working couple with a
 *      child to the single parents; nothing is written off and no estate
 *      passes.
 *
 *   4. AN ESTATE REACHES THE TIER'S WORKING HOUSEHOLDS, the debt settled
 *      from the savings, the shortfall and the student loan written off, the
 *      rest passed in kind by grown-ups - and, with no heir of the tier, to
 *      every working cell.
 *
 *   5. A LEAVER TAKES THE AVERAGE OUT of the city, stock by stock, into the
 *      record of each, and the evicted who give up leave the cell they were
 *      taken from.
 *
 *   6. EVERY STOCK IS CONSERVED, all the flows at once on one mixed fixture,
 *      with a reshuffle no flow explains: what is held after, and what left
 *      the city, is what was held before.
 *
 *   7. THE ORPHANS WEIGH BY THEIR PEOPLE (OH1): a shrinking orphan cell's
 *      stocks go to the city and no longer vanish, and a growing one claims
 *      from the pool and makes nothing.
 *
 *   8. COUNTED IN A REAL RUN: the flows move savings in a city's months, and
 *      the pool is left what no flow explains.
 */
public class HouseholdFlowCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** How close two figures must be, relative to the larger of the want and one: a stock is carried as an average times a count, so it is exact to the cent far inside this. */
    static final double TOLERANCE = 1e-9;

    static void check(String label, double got, double want) {
        boolean ok = Math.abs(got - want) <= TOLERANCE * Math.max(1, Math.abs(want));
        if (!ok) fails++;
        out.printf("%-72s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", got, want);
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One stock across every cell: per household times households. */
    static double across(HouseholdBalance b, ToDoubleFunction<Household> perHousehold) {
        double total = 0;
        for (Household c : b.cells()) total += perHousehold.applyAsDouble(c) * c.households();
        return total;
    }

    /** One cell's total of a stock. */
    static double total(Household c, ToDoubleFunction<Household> stock) {
        return stock.applyAsDouble(c) * c.households();
    }

    /** Every company's shares in one cell, a household's. */
    static double sharesOf(Household c) {
        double sum = 0;
        for (double s : c.shares) sum += s;
        return sum;
    }

    /** The stocks the conservation reads, named: the paper is left out (a bare fixture's leavers' paper has no record). */
    static final String[] STOCK_NAMES = {"savings", "debt", "shares, every company", "dollars abroad", "student loans", "cars"};
    static final List<ToDoubleFunction<Household>> STOCKS = List.of(
            c -> c.savings, c -> c.debt, HouseholdFlowCheck::sharesOf, c -> c.abroad, c -> c.studentDebt, c -> c.cars);

    static final String[] KINDS = {"retiring", "ageing on", "partner dies", "dying alone", "leaving"};

    /** What the cell holds, a household's of each stock. */
    static Household holds(Household c, double savings, double debt, double shares, double abroad,
                           double loans, double cars, double paper) {
        c.savings = savings;
        c.debt = debt;
        c.shares[0] = shares;
        c.abroad = abroad;
        c.studentDebt = loans;
        c.cars = cars;
        c.paper = paper;
        return c;
    }

    /** A cell holding some of everything, in proportion to k. */
    static Household fill(Household c, double k) {
        return holds(c, 100 * k, 10 * k, 3 * k, 2 * k, k, .5 * k, 4 * k);
    }

    /** What the city holds of each stock in STOCKS. */
    static double[] held(HouseholdBalance b) {
        double[] h = new double[STOCKS.size()];
        for (int k = 0; k < h.length; k++) h[k] = across(b, STOCKS.get(k));
        return h;
    }

    static double sharesTakenAway(HouseholdBalance b) {
        double sum = 0;
        for (int k = 0; k < Equity.COMPANIES.length; k++) sum += b.getSharesTakenAway(k);
        return sum;
    }

    /** What left the city of each stock in STOCKS, the estates' repayments among the savings and the debt. */
    static double[] left(HouseholdBalance b) {
        return new double[] {
            b.getTakenAway() + b.getEstateRepaid(),
            b.getWrittenOff() + b.getEstateRepaid(),
            sharesTakenAway(b),
            b.getAbroadTakenAway(),
            b.getStudentDebtTakenAway(),
            b.getCarsTakenAway() };
    }

    /** True when no cell of the shape, but the ones named, holds a household or a stock. */
    static boolean nothingElseIn(HouseholdBalance b, FamilyStructure shape, Household... except) {
        for (Household c : b.cells(shape)) {
            boolean named = false;
            for (Household e : except) named |= e == c;
            if (!named && (c.households() > 0 || b.holdsAnything(c))) return false;
        }
        return true;
    }

    /**
     * A fixture: a balance, and the census the month ends on. A cell put in
     * with cell() is expected to be as many households at the census; expect()
     * says otherwise. A retired shape's census is one total, at tier slot 0,
     * as FamilyModel counts it; the balance splits it across the former
     * tiers. An outside cell (the out of work, the students, the orphans) is
     * expected as it stands unless expectOutside() says otherwise.
     */
    static final class Town {
        final HouseholdBalance bal = new HouseholdBalance();
        private final double[][] census = new double[FamilyStructure.values().length][PayTier.values().length];
        private final Map<Household, Double> outside = new IdentityHashMap<>();

        /** A family cell of this many households, expected to be as many at the census. */
        Household cell(FamilyStructure shape, PayTier tier, double households) {
            Household c = bal.cell(shape, tier);
            c.households = households;
            if (shape.isRetired()) census[shape.ordinal()][0] += households;
            else census[shape.ordinal()][tier.ordinal()] = households;
            return c;
        }

        /** The census's count for a working cell. */
        Town expect(FamilyStructure shape, PayTier tier, double households) {
            census[shape.ordinal()][tier.ordinal()] = households;
            return this;
        }

        /** ...a retired shape's total, every former tier together. */
        Town expectRetired(FamilyStructure shape, double total) {
            census[shape.ordinal()][0] = total;
            return this;
        }

        /** An outside cell of this many households, expected to be as many. */
        Household outsideCell(Household c, double households) {
            c.households = households;
            return c;
        }

        /** The census's count for an outside cell. */
        Town expectOutside(Household c, double households) {
            outside.put(c, households);
            return this;
        }

        /** Every count the census holds, scaled: the reshuffles no flow explains. */
        Town drift(double factor) {
            for (double[] row : census) for (int t = 0; t < row.length; t++) row[t] *= factor;
            for (Household c : bal.cells()) {
                if (c.shape() == null) outside.putIfAbsent(c, c.households() * factor);
            }
            return this;
        }

        /** The month's census alone, as the balance reads it. */
        void run() {
            bal.setOutsideCensus(c -> outside.getOrDefault(c, c.households()));
            bal.censusOnly((shape, tier) -> census[shape.ordinal()][tier.ordinal()]);
        }
    }

    /** True for a figure that is nought to the tolerance. */
    static boolean isZero(double x) {
        return Math.abs(x) <= TOLERANCE;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ==================== 1. a quiet month moves nothing ==================== */
        out.println("--- a quiet month moves nothing ---");

        Town calm = new Town();
        fill(calm.cell(FamilyStructure.COUPLE, PayTier.SKILLED, 100), 3);
        fill(calm.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 40), 1);
        fill(calm.cell(FamilyStructure.COUPLE_CHILD, PayTier.COLLEGE, 25), 5);
        fill(calm.cell(FamilyStructure.SENIOR_COUPLE, PayTier.UNSKILLED, 30), 2);
        fill(calm.cell(FamilyStructure.SENIOR_ALONE, PayTier.COLLEGE, 20), 4);
        fill(calm.cell(FamilyStructure.ELDER_ALONE, PayTier.SKILLED, 10), 6);
        fill(calm.cell(FamilyStructure.ELDER_COUPLE, PayTier.PROFESSIONAL, 6), 7);
        fill(calm.outsideCell(calm.bal.unemployed(UnemployedHousehold.Status.OFF_EI), 12), 1);
        fill(calm.outsideCell(calm.bal.unemployed(UnemployedHousehold.Status.ON_EI), 15), 2);
        fill(calm.outsideCell(calm.bal.students(), 9), 3);
        fill(calm.outsideCell(calm.bal.orphans(AgeBand.CHILD), 8), 1);

        double[] before = held(calm.bal);
        double paperBefore = across(calm.bal, c -> c.paper);
        int cellCount = calm.bal.cells().size();
        double[] households = new double[cellCount];
        double[] savingsEach = new double[cellCount];
        for (int i = 0; i < cellCount; i++) {
            households[i] = calm.bal.cells().get(i).households();
            savingsEach[i] = calm.bal.cells().get(i).savings;
        }
        assertTrue("fixture: the balance holds savings, debt, shares, abroad, loans and cars",
                before[0] > 0 && before[1] > 0 && before[2] > 0 && before[3] > 0 && before[4] > 0 && before[5] > 0);

        calm.run();

        double[] after = held(calm.bal);
        for (int k = 0; k < after.length; k++) {
            check("no flow, no change: the city's " + STOCK_NAMES[k], after[k], before[k]);
        }
        check("...and its paper", across(calm.bal, c -> c.paper), paperBefore);
        boolean stayed = true;
        for (int i = 0; i < cellCount; i++) {
            Household c = calm.bal.cells().get(i);
            stayed &= Math.abs(c.households() - households[i]) <= TOLERANCE * Math.max(1, households[i])
                    && Math.abs(c.savings - savingsEach[i]) <= TOLERANCE * Math.max(1, savingsEach[i]);
        }
        assertTrue("...every cell with its households and its savings a household", stayed);
        boolean quietRecords = true;
        for (int kind = 0; kind < HouseholdBalance.FLOW_KINDS; kind++) {
            quietRecords &= isZero(calm.bal.getFlowHouseholds(kind)) && isZero(calm.bal.getFlowSavings(kind));
        }
        assertTrue("...and every flow record at nought", quietRecords);
        assertTrue("...the estates' and the leavers' records at nought",
                isZero(calm.bal.getEstateRepaid()) && isZero(calm.bal.getEstateWrittenOff())
                        && isZero(calm.bal.getEstatePassed()) && isZero(calm.bal.getTakenAway())
                        && isZero(calm.bal.getWrittenOff()) && isZero(calm.bal.getPoolSavings()));

        /* ======= 2. a retirement and an ageing on carry the average ======= */
        out.println("\n--- a retirement and an ageing on carry the cell's average into the former tier ---");

        // A tenth of the adults retire. The couples are the households the claimant's row would be paid out of.
        final double RETIRE = .1;
        final double PROFESSIONALS = 100, SKILLED_COUPLES = 100;
        Town retiring = new Town();
        Household professionals = holds(retiring.cell(FamilyStructure.COUPLE, PayTier.PROFESSIONAL, PROFESSIONALS),
                1000, 50, 4, 30, 20, 1.5, 7);
        Household skilled = holds(retiring.cell(FamilyStructure.COUPLE, PayTier.SKILLED, SKILLED_COUPLES),
                300, 0, 0, 0, 0, 0, 0);
        Household claimant = retiring.cell(FamilyStructure.SINGLE_ADULT, PayTier.PROFESSIONAL, 0);
        double leaving = RETIRE * PROFESSIONALS, leavingSkilled = RETIRE * SKILLED_COUPLES;
        retiring.expect(FamilyStructure.COUPLE, PayTier.PROFESSIONAL, PROFESSIONALS - leaving)
                .expect(FamilyStructure.COUPLE, PayTier.SKILLED, SKILLED_COUPLES - leavingSkilled)
                .expect(FamilyStructure.SINGLE_ADULT, PayTier.PROFESSIONAL, 20)
                .expectRetired(FamilyStructure.SENIOR_COUPLE, leaving + leavingSkilled);
        retiring.bal.setFlows(RETIRE, 0, 0, 0, 0, 0, 0);
        retiring.run();

        Household seniorProf = retiring.bal.cell(FamilyStructure.SENIOR_COUPLE, PayTier.PROFESSIONAL);
        Household seniorSkilled = retiring.bal.cell(FamilyStructure.SENIOR_COUPLE, PayTier.SKILLED);
        check("the professionals' retirees are households of the senior couples' professional cell",
                seniorProf.households(), leaving);
        check("...holding ten times a professional couple's savings",
                total(seniorProf, c -> c.savings), leaving * 1000);
        check("...ten times its debt", total(seniorProf, c -> c.debt), leaving * 50);
        check("...its shares", total(seniorProf, c -> c.shares[0]), leaving * 4);
        check("...its dollars abroad", total(seniorProf, c -> c.abroad), leaving * 30);
        check("...its student loans", total(seniorProf, c -> c.studentDebt), leaving * 20);
        check("...its cars", total(seniorProf, c -> c.cars), leaving * 1.5);
        check("...and its paper", total(seniorProf, c -> c.paper), leaving * 7);
        check("the skilled retirees are households of the skilled senior cell",
                seniorSkilled.households(), leavingSkilled);
        check("...holding ten times a skilled couple's savings",
                total(seniorSkilled, c -> c.savings), leavingSkilled * 300);
        assertTrue("no other retired cell holds a household or a stock",
                nothingElseIn(retiring.bal, FamilyStructure.SENIOR_COUPLE, seniorProf, seniorSkilled)
                        && nothingElseIn(retiring.bal, FamilyStructure.SENIOR_ALONE)
                        && nothingElseIn(retiring.bal, FamilyStructure.ELDER_ALONE)
                        && nothingElseIn(retiring.bal, FamilyStructure.ELDER_COUPLE));
        check("the professional couples keep their households less the retirees",
                professionals.households(), PROFESSIONALS - leaving);
        check("...and their savings a household", professionals.savings, 1000);
        check("...their debt", professionals.debt, 50);
        check("...their shares", professionals.shares[0], 4);
        check("...and their cars", professionals.cars, 1.5);
        check("the claimant's row, grown by the households that came in, holds no savings",
                claimant.savings * claimant.households(), 0);
        check("...though it has its households", claimant.households(), 20);
        check("the retiring households, counted", retiring.bal.getFlowHouseholds(HouseholdBalance.RETIRING),
                leaving + leavingSkilled);
        check("...and the savings they carried",
                retiring.bal.getFlowSavings(HouseholdBalance.RETIRING), leaving * 1000 + leavingSkilled * 300);
        check("...the pool handed nobody any", retiring.bal.getPoolSavings(), 0);

        // A quarter of the seniors living alone age on: into the elder cells of the tier they are in.
        final double AGE_ON = .25;
        Town ageing = new Town();
        Household seniorSk = holds(ageing.cell(FamilyStructure.SENIOR_ALONE, PayTier.SKILLED, 40), 600, 20, 2, 0, 0, 1, 0);
        Household seniorUn = holds(ageing.cell(FamilyStructure.SENIOR_ALONE, PayTier.UNSKILLED, 40), 100, 0, 0, 0, 0, 0, 0);
        double agedSk = AGE_ON * 40, agedUn = AGE_ON * 40;
        ageing.expectRetired(FamilyStructure.SENIOR_ALONE, 80 - agedSk - agedUn)
                .expectRetired(FamilyStructure.ELDER_ALONE, agedSk + agedUn);
        ageing.bal.setFlows(0, AGE_ON, 0, 0, 0, 0, 0);
        ageing.run();

        Household elderSk = ageing.bal.cell(FamilyStructure.ELDER_ALONE, PayTier.SKILLED);
        Household elderUn = ageing.bal.cell(FamilyStructure.ELDER_ALONE, PayTier.UNSKILLED);
        check("the skilled seniors who age on are households of the skilled elder cell", elderSk.households(), agedSk);
        check("...holding a skilled senior's savings", total(elderSk, c -> c.savings), agedSk * 600);
        check("...its debt", total(elderSk, c -> c.debt), agedSk * 20);
        check("...its shares", total(elderSk, c -> c.shares[0]), agedSk * 2);
        check("...and its car", total(elderSk, c -> c.cars), agedSk);
        check("the unskilled who age on are the unskilled elder cell's", elderUn.households(), agedUn);
        check("...holding an unskilled senior's savings", total(elderUn, c -> c.savings), agedUn * 100);
        check("the seniors left keep their savings a household",
                seniorSk.savings, 600);
        check("...the unskilled theirs", seniorUn.savings, 100);
        check("the seniors left are the ones who did not age on", seniorSk.households(), 40 - agedSk);
        check("the ageing households, counted", ageing.bal.getFlowHouseholds(HouseholdBalance.AGEING_ON), agedSk + agedUn);
        check("...and the savings they carried",
                ageing.bal.getFlowSavings(HouseholdBalance.AGEING_ON), agedSk * 600 + agedUn * 100);

        /* ======= 3. a partner's death leaves the survivor everything ======= */
        out.println("\n--- a partner's death leaves the survivor everything ---");

        // A tenth of the seniors, and of the adults, die: one of a couple, in each couple that loses one.
        final double DIES = .1;
        final double SENIOR_COUPLES = 100, WORKING_COUPLES = 100;
        Town widowing = new Town();
        Household couples = holds(widowing.cell(FamilyStructure.SENIOR_COUPLE, PayTier.SKILLED, SENIOR_COUPLES),
                800, 0, 2, 0, 0, 1, 3);
        Household parentsCouples = holds(widowing.cell(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, WORKING_COUPLES),
                300, 20, 1, 0, 0, 1, 0);
        // A death is one person, in a different couple each: 20 of the 200 seniors die, and 20 couples become 20 alone.
        double widowed = DIES * SENIOR_COUPLES * 2;
        double widowedWorking = DIES * WORKING_COUPLES * 2;
        widowing.expectRetired(FamilyStructure.SENIOR_COUPLE, SENIOR_COUPLES - widowed)
                .expectRetired(FamilyStructure.SENIOR_ALONE, widowed)
                .expect(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, WORKING_COUPLES - widowedWorking)
                .expect(FamilyStructure.SINGLE_PARENT, PayTier.UNSKILLED, widowedWorking);
        widowing.bal.setFlows(0, 0, DIES, DIES, 0, 0, 0);
        widowing.run();

        Household widows = widowing.bal.cell(FamilyStructure.SENIOR_ALONE, PayTier.SKILLED);
        check("the senior survivors are households of the skilled senior alone cell", widows.households(), widowed);
        check("...each with the couple's whole savings", widows.savings, 800);
        check("...its shares", widows.shares[0], 2);
        check("...its car", widows.cars, 1);
        check("...and its paper", widows.paper, 3);
        check("the couples left keep 800 each too", couples.savings, 800);
        check("...on the households that did not lose a partner", couples.households(), SENIOR_COUPLES - widowed);
        Household singleParents = widowing.bal.cell(FamilyStructure.SINGLE_PARENT, PayTier.UNSKILLED);
        assertTrue("a working couple with a child leaves a single parent, not a single adult",
                FamilyStructure.COUPLE_CHILD.dependants() > 0
                        && widowing.bal.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED).households() == 0);
        check("the working survivors are households of the single parents", singleParents.households(), widowedWorking);
        check("...each with the couple's whole savings", singleParents.savings, 300);
        check("...its debt", singleParents.debt, 20);
        check("...and its car", singleParents.cars, 1);
        check("the working couples keep 300 each", parentsCouples.savings, 300);
        check("nothing is written off", widowing.bal.getWrittenOff(), 0);
        check("...no estate passes", widowing.bal.getEstatePassed(), 0);
        check("...none repays the bank", widowing.bal.getEstateRepaid(), 0);
        check("the partners' deaths, counted in households",
                widowing.bal.getFlowHouseholds(HouseholdBalance.PARTNER_DIES), widowed + widowedWorking);
        check("...and the savings that moved with the survivors",
                widowing.bal.getFlowSavings(HouseholdBalance.PARTNER_DIES), widowed * 800 + widowedWorking * 300);
        check("...the pool handed nobody any", widowing.bal.getPoolSavings(), 0);

        /* ======= 4. an estate reaches the tier's working households ======= */
        out.println("\n--- an estate reaches the tier's working households ---");

        // Elders living alone die. Their college estate has heirs of its tier; the senior professionals' has none.
        final double ELDER_DIES = .2;
        final double COLLEGE_ELDERS = 50, PROFESSIONAL_ELDERS = 10;
        Town estates = new Town();
        Household collegeElders = holds(estates.cell(FamilyStructure.ELDER_ALONE, PayTier.COLLEGE, COLLEGE_ELDERS),
                500, 100, 2, 0, 20, 0, 0);
        Household proElders = holds(estates.cell(FamilyStructure.ELDER_ALONE, PayTier.SENIOR_PROFESSIONAL, PROFESSIONAL_ELDERS),
                50, 200, 5, 0, 0, 0, 0);
        Household heirCouples = holds(estates.cell(FamilyStructure.COUPLE, PayTier.COLLEGE, 30), 100, 0, 1, 0, 0, 0, 0);
        Household heirSingles = holds(estates.cell(FamilyStructure.SINGLE_ADULT, PayTier.COLLEGE, 40), 60, 0, 1, 0, 0, 0, 0);
        Household strangers = holds(estates.cell(FamilyStructure.COUPLE, PayTier.SKILLED, 30), 70, 0, 1, 0, 0, 0, 0);
        double deadCollege = ELDER_DIES * COLLEGE_ELDERS, deadPro = ELDER_DIES * PROFESSIONAL_ELDERS;
        estates.expectRetired(FamilyStructure.ELDER_ALONE, COLLEGE_ELDERS + PROFESSIONAL_ELDERS - deadCollege - deadPro);
        double sharesBefore = across(estates.bal, HouseholdFlowCheck::sharesOf);
        double savingsBefore = across(estates.bal, c -> c.savings);
        double heirsSavingsBefore = total(heirCouples, c -> c.savings) + total(heirSingles, c -> c.savings);
        estates.bal.setFlows(0, 0, 0, 0, ELDER_DIES, 0, 0);
        estates.run();

        double repaid = deadCollege * Math.min(500, 100) + deadPro * Math.min(50, 200);
        double writtenOff = deadPro * (200 - 50);
        double passed = deadCollege * (500 - 100);
        double heirWeight = 30 * 2 + 40 * 1;                      // grown-ups of the college working households
        double allWeight = heirWeight + 30 * 2;                   // ...and of every working household
        check("the estates repay the bank out of the dead's savings", estates.bal.getEstateRepaid(), repaid);
        check("...it is in the month's total repaid", estates.bal.totalRepaid(), repaid);
        check("the debt their savings did not cover is written off", estates.bal.getEstateWrittenOff(), writtenOff);
        check("...and is in the written off", estates.bal.getWrittenOff(), writtenOff);
        check("the savings passed to the heirs are what the debts left", estates.bal.getEstatePassed(), passed);
        check("...each college couple's household gains its grown-ups' share",
                heirCouples.savings, 100 + passed * 2 / heirWeight);
        check("...each college single adult's household gains its share",
                heirSingles.savings, 60 + passed * 1 / heirWeight);
        check("...and the heirs hold exactly what was passed",
                total(heirCouples, c -> c.savings) + total(heirSingles, c -> c.savings) - heirsSavingsBefore, passed);
        check("the skilled couples, another tier, gain no savings", strangers.savings, 70);
        check("the student loans are written off with the leavers'",
                estates.bal.getStudentDebtTakenAway(), deadCollege * 20);
        check("the elders left keep their savings a household", collegeElders.savings, 500);
        check("...their debt", collegeElders.debt, 100);
        check("...on the households that did not die", collegeElders.households(), COLLEGE_ELDERS - deadCollege);
        check("...and the senior professionals theirs", proElders.households(), PROFESSIONAL_ELDERS - deadPro);
        check("the estates, counted in households", estates.bal.getFlowHouseholds(HouseholdBalance.DYING_ALONE),
                deadCollege + deadPro);
        check("...and the savings the dead held",
                estates.bal.getFlowSavings(HouseholdBalance.DYING_ALONE), deadCollege * 500 + deadPro * 50);
        check("the college estate's shares go to the college working households, by grown-ups",
                heirCouples.shares[0], 1 + deadCollege * 2 * 2 / heirWeight + deadPro * 5 * 2 / allWeight);
        check("...a single adult's household gains half a couple's",
                heirSingles.shares[0], 1 + deadCollege * 2 * 1 / heirWeight + deadPro * 5 * 1 / allWeight);
        check("the senior professionals' estate has no heir of its tier: every working cell shares it, by grown-ups",
                strangers.shares[0], 1 + deadPro * 5 * 2 / allWeight);
        check("the shares are conserved across the city", across(estates.bal, HouseholdFlowCheck::sharesOf), sharesBefore);
        check("...and none left it", sharesTakenAway(estates.bal), 0);
        check("the savings after, with what repaid the bank, are the savings before",
                across(estates.bal, c -> c.savings) + estates.bal.getEstateRepaid(), savingsBefore);

        /* ======= 5. a leaver takes the average out ======= */
        out.println("\n--- a leaver takes the average out of the city ---");

        // A tenth of everyone leaves. The single adults the census adds are the claimant the pool would pay out of the leavers'.
        final double LEAVE = .1;
        Town leavers = new Town();
        Household leavingCouples = holds(leavers.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED, 100),
                200, 40, 1, 10, 5, 1, 0);
        Household leavingSeniors = holds(leavers.cell(FamilyStructure.SENIOR_ALONE, PayTier.UNSKILLED, 50),
                900, 0, 0, 0, 0, 0, 0);
        Household newcomers = leavers.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 0);
        double goneCouples = LEAVE * 100, goneSeniors = LEAVE * 50;
        leavers.expect(FamilyStructure.COUPLE, PayTier.UNSKILLED, 100 - goneCouples)
                .expectRetired(FamilyStructure.SENIOR_ALONE, 50 - goneSeniors)
                .expect(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 10);
        leavers.bal.setFlows(0, 0, 0, 0, 0, LEAVE, 0);
        leavers.run();

        check("the savings that left the city are the leavers' averages",
                leavers.bal.getTakenAway(), goneCouples * 200 + goneSeniors * 900);
        check("...the debt, written off", leavers.bal.getWrittenOff(), goneCouples * 40);
        check("...the shares", leavers.bal.getSharesTakenAway(0), goneCouples * 1);
        check("...the dollars abroad", leavers.bal.getAbroadTakenAway(), goneCouples * 10);
        check("...the student loans", leavers.bal.getStudentDebtTakenAway(), goneCouples * 5);
        check("...and the cars", leavers.bal.getCarsTakenAway(), goneCouples * 1);
        check("the cells they left keep their averages: couples", leavingCouples.savings, 200);
        check("...the seniors", leavingSeniors.savings, 900);
        check("...on the households that stayed", leavingCouples.households(), 100 - goneCouples);
        check("the newcomers hold their households but none of the leavers' savings",
                newcomers.savings * newcomers.households(), 0);
        check("the leavers, counted in households", leavers.bal.getFlowHouseholds(HouseholdBalance.LEAVING),
                goneCouples + goneSeniors);
        check("...and the savings they took", leavers.bal.getFlowSavings(HouseholdBalance.LEAVING),
                goneCouples * 200 + goneSeniors * 900);

        // The evicted who give up leave the cell Unemployment took them from. Alone: the share leaving applies to this cell too.
        Town evicted = new Town();
        UnemployedHousehold offEi = evicted.bal.unemployed(UnemployedHousehold.Status.OFF_EI);
        holds(evicted.outsideCell(offEi, 20), 30, 0, 0, 0, 0, 0, 0);
        final double EVICTED = 4;
        evicted.expectOutside(offEi, 20 - EVICTED);
        evicted.bal.setFlows(0, 0, 0, 0, 0, 0, EVICTED);
        evicted.run();
        check("the evicted who gave up leave: households out of the cell off benefits",
                evicted.bal.getFlowHouseholds(HouseholdBalance.LEAVING), EVICTED);
        check("...taking the cell's savings with them", evicted.bal.getTakenAway(), EVICTED * 30);
        check("...the cell keeps its average", offEi.savings, 30);
        check("...on the households that stayed", offEi.households(), 20 - EVICTED);

        /* ======= 6. every stock is conserved ======= */
        out.println("\n--- every stock is conserved, all the flows at once ---");

        Town mix = new Town();
        holds(mix.cell(FamilyStructure.COUPLE, PayTier.PROFESSIONAL, 100), 1000, 50, 4, 30, 20, 1.5, 7);
        holds(mix.cell(FamilyStructure.COUPLE, PayTier.SKILLED, 100), 300, 10, 1, 0, 0, 1, 2);
        holds(mix.cell(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, 100), 300, 20, 1, 0, 0, 1, 0);
        holds(mix.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED, 100), 200, 40, 1, 10, 5, 1, 0);
        holds(mix.cell(FamilyStructure.COUPLE, PayTier.COLLEGE, 30), 100, 0, 1, 0, 0, 1, 1);
        holds(mix.cell(FamilyStructure.SINGLE_ADULT, PayTier.COLLEGE, 40), 60, 5, 1, 0, 0, 0, 0);
        holds(mix.cell(FamilyStructure.COUPLE_BABY, PayTier.SKILLED, 50), 400, 30, 2, 0, 0, 1, 0);
        holds(mix.cell(FamilyStructure.COUPLE_CHILD, PayTier.SKILLED, 20), 200, 0, 0, 0, 0, 1, 0);
        holds(mix.cell(FamilyStructure.SENIOR_COUPLE, PayTier.SKILLED, 100), 800, 0, 2, 0, 0, 1, 3);
        holds(mix.cell(FamilyStructure.SENIOR_ALONE, PayTier.UNSKILLED, 50), 900, 0, 0, 0, 0, 0, 0);
        holds(mix.cell(FamilyStructure.ELDER_ALONE, PayTier.COLLEGE, 50), 500, 100, 2, 0, 20, 0, 0);
        holds(mix.cell(FamilyStructure.ELDER_ALONE, PayTier.SENIOR_PROFESSIONAL, 10), 50, 200, 5, 0, 0, 0, 0);
        holds(mix.cell(FamilyStructure.ELDER_COUPLE, PayTier.UNSKILLED, 12), 250, 20, 1, 5, 0, .5, 0);
        holds(mix.outsideCell(mix.bal.unemployed(UnemployedHousehold.Status.ON_EI), 15), 80, 5, 1, 0, 2, 0, 0);
        holds(mix.outsideCell(mix.bal.unemployed(UnemployedHousehold.Status.OFF_EI), 20), 30, 0, 0, 0, 0, 0, 0);
        holds(mix.outsideCell(mix.bal.students(), 12), 40, 0, 0, 0, 8, 0, 0);
        holds(mix.outsideCell(mix.bal.orphans(AgeBand.CHILD), 10), 5, 0, 3, 0, 0, 0, 0);
        // The census's own reshuffles, no flow explains them: everyone's count drifts, and a baby's couple grows a child.
        final double DRIFT = .98;
        mix.drift(DRIFT);
        mix.expect(FamilyStructure.SINGLE_ADULT, PayTier.PROFESSIONAL, 20)
                .expect(FamilyStructure.SINGLE_PARENT, PayTier.UNSKILLED, 20)
                .expect(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, 10)
                .expect(FamilyStructure.COUPLE_BABY, PayTier.SKILLED, 40)
                .expect(FamilyStructure.COUPLE_CHILD, PayTier.SKILLED, 30);

        double[] heldBefore = held(mix.bal);
        mix.bal.setFlows(.1, .1, .1, .1, .2, .05, 4);
        mix.run();
        double[] heldAfter = held(mix.bal);
        double[] gone = left(mix.bal);
        for (int k = 0; k < heldAfter.length; k++) {
            check("what is held of " + STOCK_NAMES[k] + ", and what left, is what was held",
                    heldAfter[k] + gone[k], heldBefore[k]);
        }
        boolean everyFlowRan = true;
        for (int kind = 0; kind < HouseholdBalance.FLOW_KINDS; kind++) {
            everyFlowRan &= mix.bal.getFlowHouseholds(kind) > 0;
        }
        assertTrue("fixture: every kind of flow moved households", everyFlowRan);
        assertTrue("...money left the city and an estate repaid the bank",
                mix.bal.getTakenAway() > 0 && mix.bal.getEstateRepaid() > 0 && mix.bal.getWrittenOff() > 0);
        assertTrue("...and the pool still had a reshuffle to net", mix.bal.getPoolSavings() > 0);
        check("no savings stranded by the census", mix.bal.getCensusStranded(), 0);
        check("...and none made by it", mix.bal.getCensusCreated(), 0);

        /* ======= 7. the orphans weigh by their people ======= */
        out.println("\n--- the orphans weigh by their people ---");

        // Ten households of orphans shrink to four, and the unskilled single adults grow by the six: the households the pool pays the released out to.
        Town fading = new Town();
        Household children = holds(fading.outsideCell(fading.bal.orphans(AgeBand.CHILD), 10), 5, 0, 3, 0, 0, 0, 0);
        Household singles = fading.bal.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        final double ORPHANS_LEFT = 6;
        fading.expectOutside(children, 10 - ORPHANS_LEFT)
                .expect(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED, ORPHANS_LEFT);
        double fadingShares = across(fading.bal, HouseholdFlowCheck::sharesOf);
        fading.run();

        check("the register is whole: the shares across the city are as they were",
                across(fading.bal, HouseholdFlowCheck::sharesOf), fadingShares);
        check("...the shrinking orphans keep theirs, per household", children.shares[0], 3);
        check("...and the households that grew hold the shares the orphans released",
                total(singles, c -> c.shares[0]), ORPHANS_LEFT * 3);
        check("...none left the city", sharesTakenAway(fading.bal), 0);
        check("the released savings reached them too", total(singles, c -> c.savings), ORPHANS_LEFT * 5);
        check("no savings are stranded in a pool nobody is handed", fading.bal.getCensusStranded(), 0);

        // Five households of orphans arrive, holding something, while the unskilled couples with a child shrink by five.
        Town arriving = new Town();
        Household youngsters = holds(arriving.outsideCell(arriving.bal.orphans(AgeBand.CHILD), 4), 5, 0, 1, 0, 0, 0, 0);
        Household fathers = holds(arriving.cell(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, 20), 200, 0, 4, 0, 0, 0, 0);
        final double ORPHANS_ARRIVE = 5;
        arriving.expectOutside(youngsters, 4 + ORPHANS_ARRIVE)
                .expect(FamilyStructure.COUPLE_CHILD, PayTier.UNSKILLED, 20 - ORPHANS_ARRIVE);
        double youngSavings = total(youngsters, c -> c.savings), youngShares = total(youngsters, c -> c.shares[0]);
        arriving.run();

        double released = ORPHANS_ARRIVE * 200;
        check("a growing orphan cell makes no savings", arriving.bal.getCensusCreated(), 0);
        check("...it claimed from the pool: the savings it gained are what the pool handed it",
                total(youngsters, c -> c.savings) - youngSavings, arriving.bal.getPoolSavings());
        assertTrue("...and it gained some", arriving.bal.getPoolSavings() > 0);
        check("...what it gained and what left the city are what the couples released",
                arriving.bal.getPoolSavings() + arriving.bal.getTakenAway(), released);
        check("...the shares likewise: the cell's gain and what left are what the couples released",
                total(youngsters, c -> c.shares[0]) - youngShares + arriving.bal.getSharesTakenAway(0),
                ORPHANS_ARRIVE * 4);
        check("the couples keep their savings a household", fathers.savings, 200);

        /* ======= 8. counted in a real run ======= */
        out.println("\n--- counted in a real run ---");

        // A founded city, as RestaurantsCheck builds one: a temporary folder, never a save slot.
        Path dir = TempDirs.create("householdflowcheck");
        GameFiles files = new GameFiles(dir.resolve("data"), dir.resolve("no-legacy"));
        Game game = new Game(files);
        quietly(game::run);
        quietly(() -> {
            game.getGovernmentInvestor().spend(-4_000_000 * PriceLevel.FACTOR);
            game.getLandManager().setOwnedSqFt(game.getLandManager().getOwnedSqFt() + 20_000_000L);
            LongPlaytest.build(game, "House", 60);
            LongPlaytest.build(game, "Convenience Store", 4);
            LongPlaytest.build(game, "Mixed Farm", 3);
            LongPlaytest.build(game, "Coal Power Plant", 1);
            LongPlaytest.build(game, "Water Treatment Plant", 1);
            LongPlaytest.build(game, "Paved Road", 6);
            game.simulateMonths(3);
        });

        /** Months counted: five years, enough for a pyramid's retirements and deaths to come round. */
        final int MONTHS = 60;
        double[] movedHouseholds = new double[HouseholdBalance.FLOW_KINDS];
        double[] movedSavings = new double[HouseholdBalance.FLOW_KINDS];
        double pooled = 0, heirsGot = 0, bankGot = 0, lost = 0;
        for (int m = 0; m < MONTHS; m++) {
            quietly(() -> game.simulateMonths(1));
            HouseholdBalance books = game.getHouseholdBalance();
            for (int kind = 0; kind < HouseholdBalance.FLOW_KINDS; kind++) {
                movedHouseholds[kind] += books.getFlowHouseholds(kind);
                movedSavings[kind] += books.getFlowSavings(kind);
            }
            pooled += books.getPoolSavings();
            heirsGot += books.getEstatePassed();
            bankGot += books.getEstateRepaid();
            lost += books.getEstateWrittenOff();
        }
        double byFlow = 0;
        for (double s : movedSavings) byFlow += s;
        out.printf("   %d months: what the flows carried, and what the pool handed what no flow explained%n", MONTHS);
        out.printf("   %-14s %16s %20s%n", "flow", "households", "savings carried");
        for (int kind = 0; kind < HouseholdBalance.FLOW_KINDS; kind++) {
            out.printf("   %-14s %,16.2f %,20.2f%n", KINDS[kind], movedHouseholds[kind], movedSavings[kind]);
        }
        out.printf("   %-14s %16s %,20.2f%n", "the pool", "", pooled);
        out.printf("   the flows moved %.1f%% of the savings that changed cells, the pool the rest%n",
                byFlow + pooled > 0 ? 100 * byFlow / (byFlow + pooled) : 0.0);
        HouseholdBalance last = game.getHouseholdBalance();
        out.printf("   estates: %,.2f of savings passed to heirs, %,.2f repaid the bank, %,.2f written off%n",
                heirsGot, bankGot, lost);
        out.printf("   net worth a household against a working one's: the retired %.2f, seniors alone %.2f,"
                + " elder couples %.2f%n", last.getRetiredToWorkingNetWorth(),
                last.getSeniorsAloneToWorkingNetWorth(), last.getElderCouplesToWorkingNetWorth());

        assertTrue("the retirements and the deaths moved households in the city's months",
                movedHouseholds[HouseholdBalance.RETIRING] + movedHouseholds[HouseholdBalance.PARTNER_DIES]
                        + movedHouseholds[HouseholdBalance.DYING_ALONE] > 0);
        assertTrue("...and moved savings with them",
                movedSavings[HouseholdBalance.RETIRING] + movedSavings[HouseholdBalance.PARTNER_DIES]
                        + movedSavings[HouseholdBalance.DYING_ALONE] > 0);

        out.println();
        if (fails == 0) {
            out.println("Money follows people: each flow carries its cell's average, and nothing is made or lost.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }
}
