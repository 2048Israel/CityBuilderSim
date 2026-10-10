package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.IntToDoubleFunction;

/**
 * The three order searches against the countdowns they replaced (0.7.54).
 *
 * WHY. Three loops sized an order one building at a time: the investment
 * desk's trim (Game.consider()), the landlords' mortgage (Mortgage.decide())
 * and the order's size (BusinessInvestment.orderSize()). An order grows with
 * the city, so the month did too: 4.1 s on average at 1.1 billion people and
 * 14.8 s at worst (the project's spec-scale.md, section 4). Each is a search
 * now. The size and the mortgage halve, because their tests are proved to
 * hold on a run from one (BusinessInvestment, THE WAIT GROWS WITH THE ORDER;
 * Mortgage, THE LANDLORD'S ORDER IS A RUN FROM ONE). The trim's test is not,
 * so it asks the countdown's own first Game.COUNTDOWN_SLICES slices, then
 * doubles down and halves (Game, THE LARGEST SLICE, WITHOUT COUNTING TO IT).
 *
 * THE COUNTDOWNS ARE KEPT HERE, as they stood in 0.7.53 (sizedByCount(),
 * decidedByCount(), investedByCount()), and asked the same question at the
 * same moment through BusinessInvestment.OrderWatch: every order the game
 * decides, before anything is built.
 *
 * What this has to prove:
 *   1. over a long run of the playtest's city, and a stretch of it at a dear
 *      policy rate, every order is the countdown's: the same size, the same
 *      trim, and for a refusal the same rate at the whole order, the same
 *      "at prime" and the same facts for one building; and no order asks the
 *      bond desk more than Game.deskCallsMost() times;
 *   2. the same in that city a thousand times over, copied at COPY_MONTH:
 *      its orders run past a hundred thousand, and trims deeper than the
 *      counted slices are found by the search;
 *   3. the trim's search on its own (Game.largestSlice()): the countdown's
 *      answer at every boundary of a test that holds on a run from one, at
 *      every order to 300 and at the int limit, within deskCallsMost() asks;
 *      and on a test that does not, the countdown's answer whenever the
 *      countdown would have stopped inside the counted slices, and otherwise
 *      a slice that passes with the next one failing;
 *   4. Mortgage.decide() against its count on a grid of tills, incomes,
 *      rates and costs, the yard running out part-way included.
 */
public class OrderSearchCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** Months the playtest's city plays with every order watched... */
    static final int LONG_MONTHS = 1200;

    /** ...then this many more with the policy rate held at DEAR_RATE, so the mortgage lender trims and refuses. */
    static final int DEAR_MONTHS = 120;
    /** ...and the policy rate those months are held at. */
    static final double DEAR_RATE = .15;

    /**
     * The copy of that city: the month it is copied at, how many times over,
     * and how many months it plays. At month 400 the city is growing and its
     * copy orders: 28 orders judged on their interest in 24 months, 22 of
     * them trimmed by more than Game.COUNTDOWN_SLICES.
     */
    static final int COPY_MONTH = 400;
    /** ...how many times over it is copied... */
    static final long COPY_TIMES = 1000;
    /** ...and the months the copy plays. */
    static final int COPY_MONTHS = 24;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-84s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void same(String label, long actual, long expected) {
        boolean ok = actual == expected;
        if (!ok) fails++;
        out.printf("%-84s %s  %,d against %,d%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("ordersearchcheck");
        try {
            run(root);
            search();
            grid();
        } finally {
            ScaleCheck.cleanUp(root);
        }
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void run(Path root) throws Exception {

        /* ============== 1. a long run, every order watched ============== */
        out.println("\n--- 1. " + LONG_MONTHS + " months of the playtest's city: every order is the countdown's ---");
        GameFiles files = new GameFiles(root.resolve("long"), root.resolve("long-no-legacy"));
        Game g = new Game(files, LongPlaytest.founding());
        Watch w = new Watch(g);
        g.watchOrders(w);
        playtestCity(g, COPY_MONTH, quiet);
        quietly(() -> g.saveGame(10, "ordersearch"));
        playtestRhythm(g, LONG_MONTHS, quiet);
        out.printf("      month %d, %,d people%n", g.getMonth(), g.getPopulationManager().getPopulation());
        quietly(() -> {
            PrintStream was = LongPlaytest.out;
            LongPlaytest.out = quiet;
            try {
                for (int i = 0; i < DEAR_MONTHS; i++) {
                    g.getDebtManager().takeTheDial(DEAR_RATE);
                    g.simulateMonths(1);
                }
            } finally {
                LongPlaytest.out = was;
            }
        });
        out.printf("      ...and %d months at a %.0f%% policy rate: month %d, %,d people%n",
                DEAR_MONTHS, DEAR_RATE * 100, g.getMonth(), g.getPopulationManager().getPopulation());
        w.report("the long run");
        assertTrue("fixture: the run sized orders, and judged them on their interest, refusing some",
                w.sized > 0 && w.invested > 0 && w.refused > 0);
        assertTrue("fixture: ...and bought on a mortgage, trimmed and refused some", w.mortgaged > 0
                && w.mortgagedTrimmed > 0 && w.mortgagedRefused > 0);
        w.verdicts("the long run");

        /* ============== 2. the same city, a thousand times over ============== */
        out.println("\n--- 2. that city at month " + COPY_MONTH + ", " + COPY_TIMES + " times over, " + COPY_MONTHS
                + " months: the search finds the deep trims, and every order is the countdown's ---");
        GameFiles big = new GameFiles(root.resolve("big"), root.resolve("big-no-legacy"));
        ScaleCheck.scale(files.saveFile(10), big.saveFile(10), COPY_TIMES);
        Files.copy(files.historyFile(10), big.historyFile(10));
        Game copy = new Game(big);
        quietly(() -> copy.loadGameSave(10));
        assertTrue("the copy loads", copy.getLoadFailure() == null && copy.getMonth() == COPY_MONTH);
        Watch wc = new Watch(copy);
        copy.watchOrders(wc);
        quietly(() -> copy.simulateMonths(COPY_MONTHS));
        out.printf("      month %d, %,d people%n", copy.getMonth(), copy.getPopulationManager().getPopulation());
        wc.report("the copy");
        assertTrue("fixture: the copy judged orders of more than Game.COUNTDOWN_SLICES on their interest, and trimmed some",
                wc.largestInvested > Game.COUNTDOWN_SLICES && wc.trimmed > 0);
        assertTrue("fixture: ...some by more than the counted slices, so the search found them", wc.searched > 0);
        wc.verdicts("the copy");
    }

    /**
     * The default playtest's city (LongPlaytest.main's seed 0): its founding
     * by hand, then its rhythm to `months` (playtestRhythm()), all of it told
     * to `quiet`. ScaleCheck grows its free copies' city here too.
     */
    static void playtestCity(Game g, int months, PrintStream quiet) {
        PrintStream was = LongPlaytest.out, real = System.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
                g.setFundDial(LongPlaytest.FUND_DIAL);
                LongPlaytest.villageBuild(g, "House", 40);
                LongPlaytest.villageBuild(g, "Convenience Store", 3);
                LongPlaytest.villageBuild(g, "Mixed Farm", 2);
                LongPlaytest.run(g, 3);
                LongPlaytest.villageBuild(g, "House", 20);
                LongPlaytest.run(g, 4);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                LongPlaytest.run(g, 5);
                LongPlaytest.advise(g);
                LongPlaytest.run(g, 6);
                LongPlaytest.ensureSchools(g);
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(real);
        }
        playtestRhythm(g, months, quiet);
    }

    /** ...and the playtest's rhythm to `months`: a skip of its lengths, the schools, the advisor's moves and two months. */
    static void playtestRhythm(Game g, int months, PrintStream quiet) {
        PrintStream was = LongPlaytest.out, real = System.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            {
                int stop = 0;
                while (g.getMonth() < months) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    LongPlaytest.run(g, Math.min(skip, months - g.getMonth()));
                    if (g.getMonth() >= months) break;
                    LongPlaytest.ensureSchools(g);
                    for (int move = 0; move < LongPlaytest.movesPerLook() && g.getMonth() < months; move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                    LongPlaytest.run(g, Math.min(2, months - g.getMonth()));
                }
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(real);
        }
    }

    /* ============== 3. the trim's search on its own ============== */

    static void search() {
        out.println("\n--- 3. Game.largestSlice() on its own: the countdown's answer, within deskCallsMost() asks ---");
        int[] asks = { 0 };
        int boundaries = 0, wrong = 0, over = 0, most = 0;
        String first = null;
        for (int q = 1; q <= 300; q++) {
            for (int b = 0; b <= q; b++) {
                final int line = b;
                asks[0] = 0;
                int found = Game.largestSlice(q, n -> { asks[0]++; return n <= line; });
                boundaries++;
                most = Math.max(most, asks[0]);
                if (asks[0] > Game.deskCallsMost(q)) over++;
                if (found != b) { wrong++; if (first == null) first = "order " + q + ", line " + b + ": found " + found; }
            }
        }
        int[] big = { Integer.MAX_VALUE, 1_000_000_000, 123_456_789, 65_536, 65_537 };
        for (int q : big) {
            for (long b : new long[] { 0, 1, 2, q / 3, q / 2, q - Game.COUNTDOWN_SLICES - 1, q - Game.COUNTDOWN_SLICES,
                    q - Game.COUNTDOWN_SLICES + 1, q - 1000, q - 1, q }) {
                if (b < 0) continue;
                final long line = b;
                asks[0] = 0;
                int found = Game.largestSlice(q, n -> { asks[0]++; return n <= line; });
                boundaries++;
                most = Math.max(most, asks[0]);
                if (asks[0] > Game.deskCallsMost(q)) over++;
                if (found != b) { wrong++; if (first == null) first = "order " + q + ", line " + b + ": found " + found; }
            }
        }
        out.printf("      %,d orders, each at every line from none to all for orders to 300, and eleven lines at five"
                + " orders to the int limit; at most %d asks (deskCallsMost of the int limit: %d)%n",
                boundaries, most, Game.deskCallsMost(Integer.MAX_VALUE));
        if (first != null) out.println("      first that differs: " + first);
        same("on a test that holds on a run from one, the search finds the countdown's slice every time", wrong, 0);
        same("...and never asks more than deskCallsMost() times", over, 0);

        // A test that does not hold on a run from one: a run, a gap, and a slice that passes above it.
        java.util.Random rng = new java.util.Random(54);
        int tried = 0, insideCounted = 0, insideWrong = 0, boundaryWrong = 0, differed = 0;
        for (int i = 0; i < 20_000; i++) {
            int q = 1 + rng.nextInt(i % 2 == 0 ? 40 : 5000);
            java.util.BitSet pass = new java.util.BitSet(q + 2);
            pass.set(1, 1 + rng.nextInt(q + 1));
            for (int j = rng.nextInt(4); j > 0; j--) pass.set(1 + rng.nextInt(q));
            int byCount = 0;
            for (int n = q; n >= 1; n--) if (pass.get(n)) { byCount = n; break; }
            int found = Game.largestSlice(q, pass::get);
            tried++;
            if (q - byCount < Game.COUNTDOWN_SLICES) {
                insideCounted++;
                if (found != byCount) insideWrong++;
            } else {
                if (found != byCount) differed++;
                if (found > 0 ? !pass.get(found) || (found < q && pass.get(found + 1)) : byCount != 0 && found != 0) boundaryWrong++;
            }
        }
        out.printf("      %,d tests that pass, fail and pass again: %,d decided inside the counted slices; of the rest,"
                + " %,d found another slice than the countdown's%n", tried, insideCounted, differed);
        assertTrue("fixture: some were decided inside the counted slices, and some below them",
                insideCounted > 0 && insideCounted < tried);
        same("where the countdown stops inside Game.COUNTDOWN_SLICES, the search stops where it does", insideWrong, 0);
        same("...and below them it finds a slice that passes with the next one up failing", boundaryWrong, 0);
    }

    /* ============== 4. Mortgage.decide() on a grid ============== */

    static void grid() {
        out.println("\n--- 4. Mortgage.decide() against its count, on a grid of tills, incomes, rates and costs ---");
        int cases = 0, differ = 0, trimmed = 0, refusedShort = 0, refusedTest = 0, kinked = 0;
        long readBySearch = 0, readByCount = 0;
        String first = null;
        double premium = 1 + Mortgage.premiumRate();
        for (double a : new double[] { 300, 1234.5 })
        for (double b : new double[] { 0, 50 })
        for (double m : new double[] { 1, 3 })
        for (double y : new double[] { 0, 10, 100 }) {
            IntToDoubleFunction costOf = n -> a * n + b * Math.max(0, m * n - y);
            double c1 = costOf.applyAsDouble(1);
            double[] tills = { -50, 0, 1, Mortgage.ownFundsFor(c1) - .01, Mortgage.ownFundsFor(c1),
                    .3 * c1, .2 * costOf.applyAsDouble(5), costOf.applyAsDouble(20),
                    .17 * costOf.applyAsDouble(100), 1e6 };
            for (double rate : new double[] { 0, .04, .09 }) {
                double one = Mortgage.payment(Mortgage.loanFor(c1) * premium, rate, Mortgage.MORTGAGE_AMORTIZATION_MONTHS);
                for (double noiTimes : new double[] { -1, 0, .5, 1.2, 1.5, 3, 10 })
                for (double cash : tills)
                for (int asked : new int[] { 1, 2, 3, 7, 16, 50, 200 }) {
                    double noi = noiTimes * one;
                    long[] reads = { 0 };
                    IntToDoubleFunction counted = n -> { reads[0]++; return costOf.applyAsDouble(n); };
                    Mortgage.Decision found = Mortgage.decide(asked, counted, cash, noi, rate);
                    readBySearch += reads[0];
                    reads[0] = 0;
                    Mortgage.Decision byCount = decidedByCount(asked, counted, cash, noi, rate);
                    readByCount += reads[0];
                    cases++;
                    if (!found.equals(byCount)) {
                        differ++;
                        if (first == null) first = String.format("asked %d, cash %.2f, noi %.4f, rate %.2f: %s against %s",
                                asked, cash, noi, rate, found, byCount);
                    }
                    if (byCount.quantity() > 0 && byCount.quantity() < asked) trimmed++;
                    if (byCount.quantity() == 0) { if (byCount.shortOfDown()) refusedShort++; else refusedTest++; }
                    if (b > 0 && m * byCount.quantity() > y && byCount.quantity() > 0) kinked++;
                }
            }
        }
        out.printf("      %,d orders: %,d trimmed, %,d held for the down payment, %,d declined by the lender,"
                + " %,d past the yard; %,d costs read against the count's %,d%n",
                cases, trimmed, refusedShort, refusedTest, kinked, readBySearch, readByCount);
        if (first != null) out.println("      first that differs: " + first);
        assertTrue("fixture: the grid trims, holds for the down payment, declines, and runs past the yard",
                trimmed > 0 && refusedShort > 0 && refusedTest > 0 && kinked > 0);
        same("every order on the grid is the count's: its size, what trimmed it and the facts for one", differ, 0);
        assertTrue("...reading fewer costs than the count", readBySearch < readByCount);
    }

    /* =====================================================================
       THE WATCH: every order the game decides, asked of the countdown too
       ===================================================================== */

    static final class Watch implements BusinessInvestment.OrderWatch {
        final Game g;
        int sized, sizedDiffer, largestNeeded, mostWaits;
        int invested, investedDiffer, trimmed, refused, searched, deepest, largestInvested;
        int mostDeskCalls, mostCountDeskCalls, overBound;
        int mortgaged, mortgagedDiffer, mortgagedTrimmed, mortgagedRefused;
        String firstDiffer;

        Watch(Game g) { this.g = g; }

        @Override
        public void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead) {
            sized++;
            largestNeeded = Math.max(largestNeeded, needed);
            mostWaits = Math.max(mostWaits, waitsRead);
            int byCount = sizedByCount(g.getBusinessInvestment(), t, needed, siteOutput);
            if (byCount != deliverable) {
                sizedDiffer++;
                note(String.format("month %d, %s: sized %d against the count's %d", g.getMonth(), t.getName(), deliverable, byCount));
            }
        }

        @Override
        public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found) {
            invested++;
            largestInvested = Math.max(largestInvested, d.quantity);
            Game.Afford byCount = investedByCount(g, d, cash, perUnitProfit);
            if (byCount.quantity() > 0 && byCount.quantity() < d.quantity) {
                trimmed++;
                deepest = Math.max(deepest, d.quantity - byCount.quantity());
            }
            if (byCount.quantity() == 0) refused++;
            if (d.quantity - byCount.quantity() >= Game.COUNTDOWN_SLICES) searched++;
            mostDeskCalls = Math.max(mostDeskCalls, found.deskCalls());
            mostCountDeskCalls = Math.max(mostCountDeskCalls, byCount.deskCalls());
            if (found.deskCalls() > Game.deskCallsMost(d.quantity)) overBound++;
            boolean sameAnswer = found.quantity() == byCount.quantity() && found.atPrime() == byCount.atPrime()
                    && Double.compare(found.firstRate(), byCount.firstRate()) == 0;
            if (!sameAnswer) {
                investedDiffer++;
                note(String.format("month %d, %s %,d %s: %s against the count's %s", g.getMonth(), d.sector,
                        d.quantity, d.template.getName(), found, byCount));
            }
        }

        @Override
        public void mortgaged(int asked, IntToDoubleFunction costOf, double cash, double noiPerUnit,
                              double annualRate, Mortgage.Decision found) {
            mortgaged++;
            Mortgage.Decision byCount = decidedByCount(asked, costOf, cash, noiPerUnit, annualRate);
            if (byCount.quantity() > 0 && byCount.quantity() < asked) mortgagedTrimmed++;
            if (byCount.quantity() == 0) mortgagedRefused++;
            if (!found.equals(byCount)) {
                mortgagedDiffer++;
                note(String.format("month %d, a mortgage on %,d: %s against the count's %s", g.getMonth(), asked, found, byCount));
            }
        }

        void note(String s) { if (firstDiffer == null) firstDiffer = s; }

        void report(String what) {
            out.printf("      %s: %,d orders sized (the largest needed %,d; at most %d waits read),%n", what, sized, largestNeeded, mostWaits);
            out.printf("      %,d judged on their interest (the largest %,d; %,d trimmed, the deepest by %,d; %,d refused;"
                    + " %,d trimmed past the counted slices), %,d on a mortgage (%,d trimmed, %,d refused)%n",
                    invested, largestInvested, trimmed, deepest, refused, searched, mortgaged, mortgagedTrimmed, mortgagedRefused);
            out.printf("      the bond desk asked at most %,d times an order, where the countdown asked it %,d times%n",
                    mostDeskCalls, mostCountDeskCalls);
            if (firstDiffer != null) out.println("      first that differs: " + firstDiffer);
        }

        void verdicts(String what) {
            same("every order's size in " + what + " is the count's", sizedDiffer, 0);
            same("every trim and refusal in " + what + " is the countdown's, with its rate and its \"at prime\"", investedDiffer, 0);
            same("every mortgage in " + what + " is the count's, with what trimmed it and the facts for one", mortgagedDiffer, 0);
            same("no order in " + what + " asked the bond desk more than Game.deskCallsMost() times", overBound, 0);
        }
    }

    /* =====================================================================
       THE COUNTDOWNS, AS THEY STOOD IN 0.7.53
       ===================================================================== */

    /** BusinessInvestment.orderSize()'s count: up from one until the wait passes MAX_ORDER_MONTHS. */
    static int sizedByCount(BusinessInvestment plans, BuildingsTemplate template, int needed, double siteOutput) {
        int deliverable = 0;
        if (template.getConstructionPoints() > 0 && siteOutput > 0) {
            for (int n = 1; n <= needed; n++) {
                if (plans.leadTime(template, n, siteOutput) > BusinessInvestment.MAX_ORDER_MONTHS) break;
                deliverable = n;
            }
        } else {
            deliverable = needed;
        }
        return deliverable;
    }

    /** Mortgage.decide()'s count: down from what was asked. */
    static Mortgage.Decision decidedByCount(int asked, IntToDoubleFunction costOf,
                                            double cash, double noiPerUnit, double annualRate) {
        String trimmedBy = null;
        double ownForOne = Double.NaN, coverageOfOne = Double.NaN;
        boolean shortForOne = false;
        for (int n = asked; n >= 1; n--) {
            double cost = costOf.applyAsDouble(n);
            if (cash >= cost) return new Mortgage.Decision(n, trimmedBy, false, ownForOne, coverageOfOne);
            double own = Mortgage.ownFundsFor(cost);
            if (n == 1) ownForOne = own;
            if (!(cash > 0) || cash < own) {
                if (n == 1) shortForOne = true;
                if (trimmedBy == null) trimmedBy = Mortgage.Decision.DOWN_PAYMENT;
                continue;
            }
            double coverage = Mortgage.coverage(noiPerUnit * n, cost - cash, annualRate);
            if (n == 1) coverageOfOne = coverage;
            if (coverage >= Mortgage.MORTGAGE_DEBT_COVERAGE) return new Mortgage.Decision(n, trimmedBy, false, ownForOne, coverageOfOne);
            if (trimmedBy == null) trimmedBy = Mortgage.Decision.LENDERS_TEST;
        }
        return new Mortgage.Decision(0, trimmedBy, shortForOne, ownForOne, coverageOfOne);
    }

    /** Game.consider()'s countdown: down from the whole order, the bond desk asked for every slice that borrows. */
    static Game.Afford investedByCount(Game g, BusinessInvestment.Decision decision, double cash, double perUnitProfit) {
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        boolean banned = credit.isBorrowingBlocked(decision.sector);
        int affordable = 0, deskCalls = 0;
        boolean atPrime = false;
        double firstRate = Double.NaN;
        for (int n = decision.quantity; n >= 1; n--) {
            double cost = plans.getCostOf(decision.template, n);
            double borrowed = Math.max(cost - cash, 0);
            if (banned && borrowed > 0) continue;
            double rate = credit.projectRate(decision.sector, borrowed);
            if (n == decision.quantity) firstRate = rate;
            BusinessDebtManager.Plan plan = null;
            if (borrowed > 0) {
                plan = g.getBondMarket().plan(decision.sector, borrowed, rate, credit.projectLoanRoom(decision.sector, borrowed),
                        credit.projectBondRoom(decision.sector, borrowed), borrowed, g.getMonth());
                deskCalls++;
            }
            double tested = plan != null && plan.hasBond() && plan.covers() ? plan.blendedRate() : rate;
            if (plans.servicesItsOwnDebt(perUnitProfit * n, borrowed, tested)) {
                affordable = n;
                break;
            }
            if (plans.servicesItsOwnDebt(perUnitProfit * n, borrowed,
                    credit.getPrimeRate() + credit.getRecordSurcharge(decision.sector))) {
                atPrime = true;
            }
        }
        return new Game.Afford(affordable, atPrime, firstRate, deskCalls);
    }
}
