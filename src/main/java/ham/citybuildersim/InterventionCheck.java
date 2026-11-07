package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1,
 * 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as it
 * always was, and logarithmic past it, INTERVENTION_IMPACT x s0 x (1 + ln(s /
 * s0)), a sale the mirror. Not part of the game.
 *
 * WHY THIS EXISTS. Jerus's Rotterdam (2026-11-02): the treasury bought dollars
 * worth 4.5 years of the city's output in one month, and ForeignAccounts, THE
 * FLOAT, step 5 added INTERVENTION_IMPACT x dealt / annual GDP to the felt
 * interventions, linear without end: 2.0 x 4.5 = 9.0 in logs, the D$ x8,100
 * from that line and x11,600 in the month. A rule fitted to the IMF's 1.7-2.0%
 * per 1% of a year's GDP (R33) was being asked about a purchase 450 times the
 * size it was fitted on. The study (docs/studies/spec-gold-money.md, 4.5) bound
 * it, and Jerus chose the bound ("Rate running away (Recommended)"): linear to
 * a tenth of a year's output and logarithmic past it, so each further dollar
 * moves the rate less, and Rotterdam's 4.5 years moves it 2.0 x 0.10 x (1 + ln
 * 45) = 0.961 in logs, the D$ x2.6. What must not move is every figure at or
 * under the bound: the gate checks the default playtest's trace bit for bit,
 * so section 1 asserts the old expression's bits and section 5 asserts that a
 * real run's defence never reaches the bound.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   1. BELOW THE BOUND NOTHING MOVES, BIT FOR BIT. interventionMove(), its
 *      inverse interventionLocalFor() and dealEffect() equal the old
 *      expressions to the bit on sizes up to and at INTERVENTION_LINEAR, both
 *      signs; a purchase of 9% of a year's GDP moves the rate by exactly
 *      INTERVENTION_IMPACT x the purchase over a year's GDP.
 *   2. PAST IT THE RULE IS LOGARITHMIC. At 2, 10 and 45 times the bound the
 *      move is INTERVENTION_IMPACT x s0 x (1 + ln k); it is continuous at the
 *      bound and so is its slope; it only rises, each further dollar moves it
 *      less, and a sale is the mirror of a purchase.
 *   3. THE INVERSE. interventionLocalFor(interventionMove(x)) returns x from a
 *      millionth of a year's output to a hundred years', both signs, and 0 with
 *      no GDP; the Lean defence in a fall it takes more than the bound to take
 *      back sells interventionLocalFor(absorption() x fall) and lifts the D$ by
 *      exactly the share of the fall it took back.
 *   4. ROTTERDAM'S CASE. Twins on one seed, one buys 4.5 years of output: the
 *      D$ moves by interventionMove(), I x s0 x (1 + ln 45), the rate x2.6 and
 *      not x8,100 - on a bare currency, and on a played town under Float.
 *   5. HOW OFTEN IT FIRES IN A REAL RUN. The default town, Lean, nobody
 *      dealing, played 240 months: no reprice's defence passes the bound, the
 *      premise of the byte-identical claim.
 *
 *   6. THE MONTH'S DEALING IS ONE SUM (XI2, the model Trade's warning reads).
 *      Two pieces of 9% of a year's output each are under the bound and 18%
 *      together are over it: dealtYears() and yearsOfOutputWith() read the sum;
 *      dealEffectOnTop() is the rule of the sum less the rule of what is dealt,
 *      less than the piece alone moves it, and the effects of the pieces add to
 *      the rule of their sum, which is what the reprice feels. Under a peg the
 *      rate is held at the peg, the float's own rate behind it moves by the rule
 *      of the sum (against a twin that dealt nothing) and the vault sells the
 *      dollars to hold it; under a band the rate stops at the weak edge once the
 *      move would carry it past, and moves inside the edges when it would not.
 *
 * Numbering is the running order. Money is in thousands, as everywhere.
 *
 * @author Jerus
 */
public class InterventionCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %,.12f != %,.12f%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Within a share of the expected figure, for sizes that run over many orders. */
    static void relative(String label, double actual, double expected, double share) {
        boolean ok = Math.abs(actual - expected) <= share * Math.abs(expected);
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %.17g != %.17g%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Equal to the bit. */
    static void same(String label, double actual, double expected) {
        boolean ok = Double.compare(actual, expected) == 0;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %.17g != %.17g%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Runs a piece of work with the game's narration sent nowhere. */
    static void quietly(Runnable work) {
        PrintStream was = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(was); }
    }

    /** The dials, by their short names. */
    static final double IMPACT = ForeignAccounts.INTERVENTION_IMPACT, LINEAR = ForeignAccounts.INTERVENTION_LINEAR;

    /** The market's seed in the sections that draw one (ExchangeRateCheck's). */
    static final long SEED = ExchangeRateCheck.SEED;

    /** Months of the real run in section 5. */
    static final int RUN_MONTHS = 240;

    /** The year a played town trades before its test month in section 4. */
    static final int YEAR = 12;

    /** What Rotterdam bought in a month, in years of the city's output (Jerus, 2026-11-02). */
    static final double ROTTERDAM_YEARS = 4.5;

    static double moveAt(double share, double annual) { return ForeignAccounts.interventionMove(share * annual, annual); }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        CurrencyRegimeCheck.quiet = quiet;
        long started = System.currentTimeMillis();

        belowTheBoundNothingMoves();
        pastItTheRuleIsLogarithmic();
        theInverse();
        rotterdam();
        howOftenItFires();
        theMonthsDealingIsOneSum();

        out.printf("%nran in %.1f s%n", (System.currentTimeMillis() - started) / 1000.0);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 1. below the bound nothing moves, to the bit ================= */

    static void belowTheBoundNothingMoves() {
        out.println("--- 1. below the bound nothing moves, to the bit ---");

        final double[] annuals = { 1, 12 * ExchangeRateCheck.GDP, 7.3e5, 1e9 };
        final double[] shares = { 1e-9, 1e-4, .01, .05, Math.nextDown(LINEAR), LINEAR };
        final double[] signs = { 1, -1 };

        int checked = 0, apart = 0;
        String first = "";
        for (double annual : annuals) for (double share : shares) for (double sign : signs) {
            double local = sign * share * annual;
            double now = ForeignAccounts.interventionMove(local, annual), was = IMPACT * local / annual;
            checked++;
            if (Double.compare(now, was) != 0) {
                apart++;
                if (first.isEmpty()) first = String.format(" (first: %.17g of %.17g gives %.17g, was %.17g)", local, annual, now, was);
            }
        }
        assertTrue("interventionMove() is INTERVENTION_IMPACT x local / annual to the bit on " + checked
                + " sizes up to the bound, both signs" + first, apart == 0);

        // The inverse, for a lift up to what the bound itself gives.
        final double[] fractions = { 1e-9, 1e-3, .25, .5, Math.nextDown(1.0), 1 };
        checked = apart = 0;
        first = "";
        for (double annual : annuals) for (double fraction : fractions) for (double sign : signs) {
            double lift = sign * fraction * IMPACT * LINEAR;
            double now = ForeignAccounts.interventionLocalFor(lift, annual), was = lift * annual / IMPACT;
            checked++;
            if (Double.compare(now, was) != 0) {
                apart++;
                if (first.isEmpty()) first = String.format(" (first: lift %.17g on %.17g gives %.17g, was %.17g)", lift, annual, now, was);
            }
        }
        assertTrue("interventionLocalFor() is lift x annual / INTERVENTION_IMPACT to the bit on " + checked
                + " lifts up to INTERVENTION_IMPACT x the bound, both signs" + first, apart == 0);

        // The model's own getter, off its own trailing GDP.
        ForeignAccounts fx = ExchangeRateCheck.calm();
        double year = 12 * fx.getGdpTrailing();
        checked = apart = 0;
        first = "";
        for (double share : shares) for (double sign : signs) {
            double local = sign * share * year;
            double now = fx.dealEffect(local), was = IMPACT * local / (12 * fx.getGdpTrailing());
            checked++;
            if (Double.compare(now, was) != 0) {
                apart++;
                if (first.isEmpty()) first = String.format(" (first: %.17g gives %.17g, was %.17g)", local, now, was);
            }
        }
        assertTrue("dealEffect() is INTERVENTION_IMPACT x local / (12 x gdpTrailing) to the bit on " + checked + " sizes up to the bound" + first,
                apart == 0);

        // A currency that has bought 9% of a year's GDP: the month feels exactly the old figure.
        ForeignAccounts buy = ExchangeRateCheck.calm();
        buy.buyReserves(.9 * LINEAR * 12 * buy.getGdpTrailing());
        double pending = buy.getDealtSinceReprice();
        assertTrue("fixture: the treasury has bought 90% of the bound in dollars, pending",
                Math.abs(buy.yearsOfOutput(pending) - .9 * LINEAR) < 1e-12 && pending > 0);
        ExchangeRateCheck.month(buy);
        double annual = 12 * buy.getGdpTrailing();
        same("that month's move from interventions is exactly INTERVENTION_IMPACT x the purchase over a year's GDP", buy.getMoveIntervention(),
                IMPACT * pending / annual);
        same("...and what is felt is that, nothing having been felt before", buy.getInterventionFelt(), buy.getMoveIntervention());
    }

    /* ================= 2. past it the rule is logarithmic ================= */

    static void pastItTheRuleIsLogarithmic() {
        out.println("\n--- 2. past the bound the rule is logarithmic ---");

        final double annual = 12 * ExchangeRateCheck.GDP;
        for (double k : new double[] { 2, 10, 45 }) {
            double want = IMPACT * LINEAR * (1 + Math.log(k));
            double got = moveAt(k * LINEAR, annual);
            close(String.format("%.0f times the bound moves the rate INTERVENTION_IMPACT x s0 x (1 + ln %.0f) = %.4f logs, x%.2f", k, k, want,
                    Math.exp(want)), got, want, 1e-12);
            assertTrue(String.format("...which is less than the linear rule's %.4f logs (x%.1f)", IMPACT * k * LINEAR, Math.exp(IMPACT * k * LINEAR)),
                    got < IMPACT * k * LINEAR);
        }

        // Continuous at the bound, from both sides.
        final double eps = 1e-6, atBound = IMPACT * LINEAR;
        close("just under the bound it is INTERVENTION_IMPACT x the bound", moveAt(LINEAR * (1 - eps), annual), atBound, 2 * eps * atBound);
        close("...exactly at the bound it is that", moveAt(LINEAR, annual), atBound, 1e-15);
        close("...and just over it, the same: the rule has no step", moveAt(LINEAR * (1 + eps), annual), atBound, 2 * eps * atBound);

        // ...and its slope too: INTERVENTION_IMPACT per unit of share on both sides, then IMPACT / k out at k times the bound.
        double h = eps * LINEAR;
        double left = (moveAt(LINEAR - h, annual) - moveAt(LINEAR - 2 * h, annual)) / h;
        double right = (moveAt(LINEAR + 2 * h, annual) - moveAt(LINEAR + h, annual)) / h;
        close("the slope just under the bound is INTERVENTION_IMPACT", left, IMPACT, 1e-5);
        close("...and just over it the same: no kink", right, IMPACT, 1e-5);
        for (double k : new double[] { 2, 10 }) {
            double hk = eps * k * LINEAR;
            double slope = (moveAt(k * LINEAR + hk, annual) - moveAt(k * LINEAR - hk, annual)) / (2 * hk);
            close(String.format("at %.0f times the bound each further dollar moves the rate 1/%.0f as much as under it", k, k), slope, IMPACT / k,
                    1e-4 * IMPACT / k);
        }

        // It only rises, and each further dollar moves it less.
        int falls = 0, steps = 0;
        double previous = Double.NEGATIVE_INFINITY;
        for (double share = 1e-6; share <= 100; share *= Math.pow(10, 1 / 40.0)) {
            double move = moveAt(share, annual);
            if (!(move > previous)) falls++;
            previous = move;
            steps++;
        }
        assertTrue("from a millionth of a year's output to a hundred years' the move strictly rises (" + steps + " sizes)", falls == 0);

        final double step = .05;
        int dearer = 0, strict = 0, pastBound = 0;
        double before = moveAt(step, annual) - moveAt(0, annual);
        for (int j = 1; j < 200; j++) {
            double increment = moveAt((j + 1) * step, annual) - moveAt(j * step, annual);
            if (increment > before + 1e-12) dearer++;
            if (j * step >= LINEAR) {
                pastBound++;
                if (!(increment < before)) strict++;
            }
            before = increment;
        }
        assertTrue("equal steps of " + step + " of a year move the rate no more than the step before (200 steps)", dearer == 0);
        assertTrue("...and past the bound each moves it strictly less than the step before (" + pastBound + " steps)", strict == 0);

        // A sale is the mirror of a purchase.
        int unequal = 0, mirrored = 0;
        for (double share = 1e-6; share <= 100; share *= Math.pow(10, 1 / 8.0)) {
            mirrored++;
            if (Double.compare(ForeignAccounts.interventionMove(-share * annual, annual), -ForeignAccounts.interventionMove(share * annual, annual)) != 0) {
                unequal++;
            }
        }
        assertTrue("a sale of any size moves the rate by exactly minus what the purchase does (" + mirrored + " sizes)", unequal == 0);
        ForeignAccounts fx = ExchangeRateCheck.calm();
        double year = 12 * fx.getGdpTrailing();
        same("...and dealEffect() of a sale of 4.5 years of output is minus a purchase's", fx.dealEffect(-ROTTERDAM_YEARS * year), -fx.dealEffect(ROTTERDAM_YEARS * year));
        close("...which is INTERVENTION_IMPACT x s0 x (1 + ln 45) of the model's own trailing GDP", fx.dealEffect(ROTTERDAM_YEARS * year),
                IMPACT * LINEAR * (1 + Math.log(ROTTERDAM_YEARS / LINEAR)), 1e-12);
    }

    /* ================= 3. the inverse ================= */

    static void theInverse() {
        out.println("\n--- 3. the inverse, and the defence that needs it ---");

        final double annual = 12 * ExchangeRateCheck.GDP;
        final double[] years = { 1e-6, 1e-4, .01, .05, LINEAR, LINEAR * (1 + 1e-9), .3, 1, ROTTERDAM_YEARS, 10, 100 };
        int off = 0, tried = 0;
        double worst = 0;
        for (double y : years) for (double sign : new double[] { 1, -1 }) {
            double local = sign * y * annual;
            double back = ForeignAccounts.interventionLocalFor(ForeignAccounts.interventionMove(local, annual), annual);
            double error = Math.abs(back - local) / Math.abs(local);
            worst = Math.max(worst, error);
            tried++;
            if (error > 1e-12) off++;
        }
        out.printf("   %d sizes from a millionth of a year's output to a hundred years', both signs: the worst round trip off by %.1e%n", tried, worst);
        assertTrue("interventionLocalFor(interventionMove(x)) returns x, to a part in 1e12, on every one", off == 0);
        relative("...Rotterdam's 4.5 years in particular", ForeignAccounts.interventionLocalFor(
                ForeignAccounts.interventionMove(ROTTERDAM_YEARS * annual, annual), annual), ROTTERDAM_YEARS * annual, 1e-12);
        assertTrue("with no GDP both functions say 0",
                ForeignAccounts.interventionMove(5, 0) == 0 && ForeignAccounts.interventionLocalFor(1, 0) == 0
                        && ForeignAccounts.interventionMove(5, -1) == 0 && ForeignAccounts.interventionLocalFor(1, Double.NaN) == 0);
        ForeignAccounts none = new ForeignAccounts();
        assertTrue("...and a currency with no trailing GDP deals no effect and counts no years", none.dealEffect(100) == 0 && none.yearsOfOutput(100) == 0);

        /* ---- the Lean defence in a fall it takes more than the bound to take back ---- */
        // A vault of six months of imports (the capacity there is), a chronic deficit four months of output a month so the deficit's cap
        // is out of the way, and the widest real rate gap there is behind a fall of about a third.
        final double vault = 10_000, imports = vault / ForeignAccounts.COMFORTABLE_COVER, exports = imports / 2, gdp = 1_000, deficit = 4_000;
        final double capitalOut = deficit - (imports - exports);
        final double gap = -ForeignAccounts.GAP_CAP;
        ForeignAccounts fx = CurrencyCheck.defended(exports, imports, gdp, vault, capitalOut, capitalOut);
        fx.setRealRateDifferential(gap);
        double capacity = fx.absorption(), r0 = fx.getRate(), year = 12 * fx.getGdpTrailing();
        double fall0 = CurrencyCheck.firstFall(fx, gap);
        double lift0 = capacity * fall0;
        double need = ForeignAccounts.interventionLocalFor(lift0, year) / r0;
        close("fixture: the month's own accounts show a deficit of US$4M", fx.monthDeficitUsd(), deficit, 1e-9);
        close("fixture: the vault is at six months' cover, so its capacity is the most there is", capacity, ForeignAccounts.MAX_ABSORPTION, 1e-12);
        assertTrue("fixture: the city trades enough for the market to price it", fx.gate() == 1);
        assertTrue(String.format("fixture: taking back absorption() of the fall (%.4f of %.4f) is more than INTERVENTION_IMPACT x the bound (%.4f)",
                lift0, fall0, IMPACT * LINEAR), fall0 > 0 && lift0 > IMPACT * LINEAR);
        assertTrue("fixture: the dollars that takes are more than the bound of a year's output and under the deficit's cap and the vault",
                need * r0 > LINEAR * year && need < capacity * deficit && need < vault);
        assertTrue("fixture: nothing has been dealt, so nothing is felt yet", fx.getDealtSinceReprice() == 0 && fx.getInterventionFelt() == 0);

        fx.repriceCurrency();
        close("fixture: the month's move before the defence leans is the fixture's figures'", fx.getLastWouldBeMove(), fall0, 1e-12);
        double sold = fx.getDefenceUsd(), fetched = fx.getDefenceLocal();
        out.printf("   a fall of %.4f, %.4f of it to take back: sold US$%,.2fk, %.1f%% of a year's output (the linear rule would sell US$%,.2fk)%n",
                fall0, lift0, sold, 100 * fetched / year, lift0 * year / IMPACT / r0);
        close("the defence sells what takes back absorption() of the fall at the bounded rule: interventionLocalFor(absorption() x fall)",
                sold, need, 1e-9);
        assertTrue("...which is past the bound of a year's output", fetched > LINEAR * year);
        assertTrue("...and more than the linear rule would have sold, each dollar moving the rate less", sold > lift0 * year / IMPACT / r0);
        close("...at the month's rate, which is what it fetches", fetched, sold * r0, 1e-9);
        close("the sale lifted the D$ by interventionMove() of what it fetched, nothing having been felt before",
                fx.getMoveIntervention(), -ForeignAccounts.interventionMove(fetched, year), 1e-12);
        close("...which is exactly the share of the fall it set out to take back", -fx.getMoveIntervention(), lift0, 1e-9);
        assertTrue("...so the D$ still falls, by what the defence could not take back", fx.getLastMove() > 0
                && Math.abs(fx.getLastMove() - (fall0 - lift0)) < 1e-9);
    }

    /* ================= 4. Rotterdam's case ================= */

    static void rotterdam() {
        out.println("\n--- 4. Rotterdam's case: 4.5 years of output in a month ---");

        // (a) A bare currency: two on one seed, one buys 4.5 years of output.
        ForeignAccounts control = ExchangeRateCheck.still(ExchangeRateCheck.TRADE, ExchangeRateCheck.TRADE, ExchangeRateCheck.GDP, SEED);
        ForeignAccounts buyer = ExchangeRateCheck.still(ExchangeRateCheck.TRADE, ExchangeRateCheck.TRADE, ExchangeRateCheck.GDP, SEED);
        double bought = ROTTERDAM_YEARS * 12 * buyer.getGdpTrailing();
        buyer.buyReserves(bought);
        double pending = buyer.getDealtSinceReprice();
        assertTrue("fixture: the twins have one seed and one vault, and one has bought 4.5 years of output in dollars, pending",
                control.getMarketSeed() == buyer.getMarketSeed() && control.getReservesUsd() < buyer.getReservesUsd()
                        && Math.abs(buyer.yearsOfOutput(pending) - ROTTERDAM_YEARS) < 1e-9 && control.getDealtSinceReprice() == 0);
        ExchangeRateCheck.month(control);
        ExchangeRateCheck.month(buyer);
        double annual = 12 * buyer.getGdpTrailing();
        double expected = IMPACT * LINEAR * (1 + Math.log(ROTTERDAM_YEARS / LINEAR));
        close("the buyer's move from interventions less the control's is interventionMove() of the purchase",
                buyer.getMoveIntervention() - control.getMoveIntervention(), ForeignAccounts.interventionMove(pending, annual), 1e-12);
        close("...which is INTERVENTION_IMPACT x s0 x (1 + ln 45), " + String.format("%.4f", expected) + " logs",
                buyer.getMoveIntervention() - control.getMoveIntervention(), expected, 1e-9);
        double ratio = buyer.getRate() / control.getRate();
        out.printf("   4.5 years of output bought in a month: the D$ x%.2f (the linear rule: x%,.0f)%n", ratio, Math.exp(IMPACT * ROTTERDAM_YEARS));
        close("the log of the rates' ratio is that too: the rate x2.6, with nothing else apart", Math.log(ratio), expected, 1e-9);
        assertTrue("...and far under the linear rule's x" + String.format("%,.0f", Math.exp(IMPACT * ROTTERDAM_YEARS)),
                ratio < Math.exp(IMPACT * ROTTERDAM_YEARS) / 100);
        close("dealEffect() says the same before the press", buyer.dealEffect(pending), expected, 1e-9);

        // (b) A played town, Float so that no defence muddies it: the same purchase through the game.
        Founding founding = Founding.defaults();
        Game quietTown = CurrencyRegimeCheck.town("xi1-rotterdam-control", founding);
        Game spender = CurrencyRegimeCheck.town("xi1-rotterdam-buyer", founding);
        CurrencyRegimeCheck.play(quietTown, YEAR);
        CurrencyRegimeCheck.play(spender, YEAR);
        quietly(() -> {
            quietTown.setCurrencyRegime(ForeignAccounts.Regime.FLOAT);
            spender.setCurrencyRegime(ForeignAccounts.Regime.FLOAT);
        });
        ForeignAccounts fq = quietTown.getForeignAccounts(), fs = spender.getForeignAccounts();
        assertTrue("fixture: two towns on one founding, a year played, both on Float, alike to the bit in their rate, their felt interventions and their output",
                fq.getRegime() == ForeignAccounts.Regime.FLOAT && fs.getRegime() == ForeignAccounts.Regime.FLOAT
                        && Double.compare(fq.getRate(), fs.getRate()) == 0 && Double.compare(fq.getInterventionFelt(), fs.getInterventionFelt()) == 0
                        && Double.compare(fq.getGdpTrailing(), fs.getGdpTrailing()) == 0 && fq.getGdpTrailing() > 0);
        double target = ROTTERDAM_YEARS * 12 * fs.getGdpTrailing();
        double cashBefore = spender.getCash();
        spender.setCashForTest(cashBefore + target);
        double[] spent = new double[1];
        quietly(() -> spent[0] = spender.buyForeignCurrency(target));
        assertTrue("fixture: the buyer was handed the cash and spent all of it: 4.5 years of its output",
                Math.abs(spent[0] - target) <= 1e-9 * target && Math.abs(fs.yearsOfOutput(fs.getDealtSinceReprice()) - ROTTERDAM_YEARS) < 1e-9);
        CurrencyRegimeCheck.play(quietTown, 1);
        CurrencyRegimeCheck.play(spender, 1);
        // takeMonth() sets the trailing GDP before the reprice reads it (Game.nextMonth), so the figure after the month is the one the reprice used.
        double annualTown = 12 * fs.getGdpTrailing();
        double felt = fs.getInterventionFelt() - fq.getInterventionFelt();
        out.printf("   the same through the game: the buyer's intervention felt %.4f logs against the control's %.4f; the D$ x%.2f (the linear rule: x%,.0f)%n",
                fs.getInterventionFelt(), fq.getInterventionFelt(), fs.getRate() / fq.getRate(), Math.exp(IMPACT * ROTTERDAM_YEARS));
        close("the buyer's interventions felt less the control's are interventionMove() of what it spent, over the year's output the reprice used",
                felt, ForeignAccounts.interventionMove(spent[0], annualTown), 1e-12);
        close("...which is INTERVENTION_IMPACT x s0 x (1 + ln 45) within the output's own month of drift", felt, expected,
                .05 * expected);
        assertTrue("...and nothing like the linear rule's " + String.format("%.1f", IMPACT * ROTTERDAM_YEARS) + " logs",
                felt < IMPACT * spent[0] / annualTown / 5);
    }

    /* ================= 5. how often it fires in a real run ================= */

    static void howOftenItFires() {
        out.println("\n--- 5. how often it fires in a real run ---");

        Game city = CurrencyRegimeCheck.town("xi1-default-lean", Founding.defaults());
        ForeignAccounts fx = city.getForeignAccounts();
        assertTrue("fixture: the default town is on Lean", fx.getRegime() == ForeignAccounts.Regime.LEAN);

        int sold = 0, past = 0, dealt = 0, repriced = 0, largestMonth = 0, firstUnweighed = 0;
        double largest = 0;
        for (int m = 1; m <= RUN_MONTHS; m++) {
            CurrencyRegimeCheck.play(city, 1);
            double year = 12 * fx.getGdpTrailing();
            if (year <= 0) { if (firstUnweighed == 0) firstUnweighed = m; continue; }
            repriced++;
            if (fx.getBoughtThisMonth() > 0 || fx.getSoldThisMonth() > 0) dealt++;
            double share = fx.getDefenceLocal() / year;
            if (fx.getDefenceUsd() > 0) sold++;
            if (share > LINEAR) past++;
            if (share > largest) { largest = share; largestMonth = m; }
        }
        out.printf("   %d months: the lean sold in %d of them; its largest sale was %.2f%% of a year's output (month %d), against the bound's %.0f%%;"
                + " %d months passed the bound%n", RUN_MONTHS, sold, largest * 100, largestMonth, LINEAR * 100, past);
        assertTrue("fixture: the D$ was repriced against a year of output in every month but the first (" + repriced + " of " + RUN_MONTHS + ", the first unweighed: month " + firstUnweighed + ")",
                repriced >= RUN_MONTHS - 1 && (repriced == RUN_MONTHS || firstUnweighed == 1));
        assertTrue("fixture: nobody dealt dollars in any month (" + dealt + " did)", dealt == 0);
        assertTrue("fixture: the lean sold in some month, or the claim means nothing (" + sold + " of " + RUN_MONTHS + ")", sold > 0);
        assertTrue("no reprice's defence passed INTERVENTION_LINEAR of a year's output: the bound changes no figure of the default run ("
                + past + " of " + RUN_MONTHS + ")", past == 0);
    }

    /* ================= 6. the month's dealing is one sum (XI2) ================= */

    /** The standard trade a currency settles on, the regimes' fixture (CurrencyRegimeCheck.book()), in this regime, quiet, with a vault no sale empties. */
    static ForeignAccounts held(ForeignAccounts.Regime regime) {
        return CurrencyRegimeCheck.book(CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.DEEP, 0, regime);
    }

    static void theMonthsDealingIsOneSum() {
        out.println("\n--- 6. the month's dealing is one sum, and the regime's part in it (XI2) ---");

        final double each = .09;
        assertTrue("fixture: each piece (" + each + " of a year's output) is under the bound and the two together are over it",
                each < LINEAR && 2 * each > LINEAR);

        // THE FLOAT: two pieces cross the bound together.
        ForeignAccounts fx = held(ForeignAccounts.Regime.FLOAT);
        double annual = 12 * fx.getGdpTrailing();
        double piece = each * annual;
        close("nothing dealt yet: a piece is its own share of a year's output", fx.yearsOfOutputWith(piece), each, 1e-12);
        same("...and a piece on top of nothing moves the rate as the deal alone does, to the bit", fx.dealEffectOnTop(piece), fx.dealEffect(piece));
        double first = fx.dealEffectOnTop(piece);
        fx.buyReserves(piece);
        close("one piece dealt: dealtYears() is its share", fx.dealtYears(), each, 1e-12);
        close("...and a second on top brings the month's dealing to the two together", fx.yearsOfOutputWith(piece), 2 * each, 1e-12);
        assertTrue("...which is past the bound though neither piece is", Math.abs(fx.yearsOfOutputWith(piece)) > LINEAR);
        double second = fx.dealEffectOnTop(piece);
        assertTrue(String.format("the second piece moves the rate less than it would alone (%.4f against %.4f)", second, fx.dealEffect(piece)),
                second < fx.dealEffect(piece));
        close("...by the rule of the sum less the rule of what is dealt", second,
                ForeignAccounts.interventionMove(2 * piece, annual) - ForeignAccounts.interventionMove(piece, annual), 1e-12);
        close("...so the two pieces' effects add to the rule of their sum", first + second, ForeignAccounts.interventionMove(2 * piece, annual), 1e-12);
        assertTrue("...which is less than the rule of each alone, twice", first + second < 2 * fx.dealEffect(piece));
        close("a sale of the same size on top of the first piece takes back exactly what it moved", fx.dealEffectOnTop(-piece), -first, 1e-12);
        fx.buyReserves(piece);
        close("dealtYears() reads both pieces", fx.dealtYears(), 2 * each, 1e-12);
        CurrencyRegimeCheck.month(fx, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        double annualNow = 12 * fx.getGdpTrailing();
        close("the reprice feels the sum: the rule of the two pieces together", fx.getInterventionFelt(),
                ForeignAccounts.interventionMove(2 * piece, annualNow), 1e-9);
        close("...and dealtYears() is clear again", fx.dealtYears(), 0, 1e-12);

        // A PEG: the rate is held, the float's own rate behind it moves, the vault sells to hold it.
        ForeignAccounts peg = held(ForeignAccounts.Regime.PEG), pegTwin = held(ForeignAccounts.Regime.PEG);
        double pegAnnual = 12 * peg.getGdpTrailing(), pegPiece = each * pegAnnual;
        peg.buyReserves(pegPiece);
        assertTrue("a peg holds the deal: the regime keeps its move off the rate", peg.regimeHoldsDeal(pegPiece));
        close("...and the rate after it is the peg", peg.rateAfterDeal(pegPiece), peg.getPegRate(), 1e-12);
        peg.buyReserves(pegPiece);
        double rateBefore = peg.getRate();
        CurrencyRegimeCheck.month(peg, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        CurrencyRegimeCheck.month(pegTwin, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        close("under the peg the rate stays at the peg after the two pieces", peg.getRate(), peg.getPegRate(), 1e-9);
        double pegAnnualNow = 12 * peg.getGdpTrailing();
        close("...while the float's own rate behind it moved by the rule of the sum, against a twin that dealt nothing",
                Math.log(peg.getShadowRate() / pegTwin.getShadowRate()), ForeignAccounts.interventionMove(2 * pegPiece, pegAnnualNow), 1e-9);
        assertTrue(String.format("...and the vault sold dollars to hold the peg (US$%,.1fk against the twin's US$%,.1fk)",
                peg.getDefenceUsd(), pegTwin.getDefenceUsd()), peg.getDefenceUsd() > pegTwin.getDefenceUsd());
        relative("...as many as the dealt money buys at the rate before it (the rule's inverse of its own move)",
                peg.getDefenceUsd() - pegTwin.getDefenceUsd(), 2 * pegPiece / rateBefore, 1e-3);

        // A BAND: the rate stops at the weak edge when the move would carry it past, and moves inside the edges when it would not.
        ForeignAccounts band = held(ForeignAccounts.Regime.BAND);
        double bandAnnual = 12 * band.getGdpTrailing(), bandPiece = each * bandAnnual;
        band.buyReserves(bandPiece);
        double carried = band.getRate() * Math.exp(band.dealEffectOnTop(bandPiece));
        assertTrue(String.format("a band: on top of one piece another would carry the rate to %.3f, past the weak edge %.3f", carried, band.bandWeakEdge()),
                carried > band.bandWeakEdge());
        assertTrue("...so the regime holds it", band.regimeHoldsDeal(bandPiece));
        close("...and the rate after it is the weak edge", band.rateAfterDeal(bandPiece), band.bandWeakEdge(), 1e-9);
        band.buyReserves(bandPiece);
        CurrencyRegimeCheck.month(band, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        relative("the reprice held the rate at the weak edge", band.getRate(), band.bandWeakEdge(), 1e-9);
        assertTrue("...with the float's own rate beyond it", band.getShadowRate() > band.bandWeakEdge());

        ForeignAccounts inside = held(ForeignAccounts.Regime.BAND);
        double insideAnnual = 12 * inside.getGdpTrailing(), big = .11 * insideAnnual, small = .01 * insideAnnual;
        inside.buyReserves(big);
        double moved = inside.getRate() * Math.exp(inside.dealEffectOnTop(small));
        assertTrue(String.format("a band: on top of 11%% dealt a further 1%% moves the rate to %.3f, inside the edges %.3f to %.3f", moved,
                inside.bandStrongEdge(), inside.bandWeakEdge()), moved < inside.bandWeakEdge() && moved > inside.bandStrongEdge());
        assertTrue("...so the regime holds nothing of it", !inside.regimeHoldsDeal(small));
        close("...and the rate after it is the rate times that move", inside.rateAfterDeal(small), moved, 1e-12);

        // The other regimes hold nothing, and the factor is the move either way round.
        assertTrue("under the float the regime holds none of a deal", !held(ForeignAccounts.Regime.FLOAT).regimeHoldsDeal(piece));
        close("dealFactor() of a sale is the factor of its move, one or more", fx.dealFactor(-piece),
                Math.exp(Math.abs(fx.dealEffectOnTop(-piece))), 1e-12);
        assertTrue("...and the move of a sale is dearer, a purchase's weaker", fx.dealEffectOnTop(-piece) < 0 && fx.dealEffectOnTop(piece) > 0);
    }
}
