package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the expected price level, the wages' half-and-half indexing, the money constants struck at the level, the currency's anchored drift and its UIP level, and the anchor through a save. Not part of the game.
 *
 * WHY THIS EXISTS. Until 0.7.42 nothing in the city read the inflation
 * target and the money constants sat at founding money for good, so a push
 * ended as a level step and the autopilot ran a city at about 0% a year
 * whatever its target (the project's inflation-research.md). The anchor is
 * the design that replaced that - spec-inflation.md, sections 2.1-2.5, batch
 * P2 - and every rule in it is a recurrence with a constant in it, so each
 * is asserted against its own constants, month by month, on a fixture that
 * causes the condition.
 *
 * What it has to prove, a section each:
 *
 *   1. Before the basket is based the expected level is 1, expected and
 *      smoothed inflation are the target, credibility waits at KSEED - on a
 *      bare index and in a founding city, whose constants are at founding.
 *   2. On target, credibility climbs a GAIN_MONTHS-th of the way to KMAX a
 *      month, to the bit, and never past it; the month's step (0.7.45) is
 *      nothing the month the anchor is seeded and the move it made after.
 *   3. A miss nobody leans against costs a LOSS_MONTHS-th of the way to KMIN
 *      times the miss's share of MISS_SCALE past TOLERANCE, both short of a
 *      full miss and past it; a rate at the rule's advice costs nothing; a
 *      rate halfway costs half; each month's step is the move it made, a
 *      fall in the months it fell (NEEDS YOU's PRICES row, 0.7.45).
 *   4. Expected inflation never goes under EXPECTED_FLOOR, in a deflation
 *      where credibility and recent inflation would put it there.
 *   5. The expected level compounds at expected inflation every month: a
 *      year at it is the year's growth.
 *   6. Wages: in steady inflation that is expected they grow at it, not at
 *      twice it, and lag the index by two years of it; a level step nobody
 *      expected passes a forty-eighth of its log a month and all of it in
 *      the end.
 *   7. In a city: every Pe-struck constant is founding / (unit / the struck
 *      level), the fare is the dial at the level, a FIXED grant at the
 *      expected level, the labour in a cost split at the level; a reform
 *      divides them all at once, and the month after strikes them at the new
 *      unit and the reformed twin agrees with its unreformed self.
 *   8. The currency: the rate's own pressure is zero at uipLevel(), a real
 *      gap holds the rate at a level rather than a drift, the anchored drift
 *      moves an unpushed rate by its twelfth root and a pinned one not at
 *      all, and a city hands the currency its anchored drift - nothing
 *      before the basket is based.
 *   9. A save carries the anchor whole and the next month is the same
 *      month, the month's step in its eighth slot (0.7.45), which a save of
 *      seven slots reads as nothing; a save from before 0.7.42 is seeded -
 *      credibility KSEED, the year's inflation smoothed, the level 1, its
 *      constants at founding, its step nothing.
 *
 * @author Jerus
 */
public class ExpectationsCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-100s FAIL  %.12f != %.12f%n", label, actual, expected);
        } else {
            out.printf("%-100s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("expectations");

        beforeTheBase(root);
        onTarget();
        aMiss();
        theFloor();
        theLevel();
        wages();
        constants(root);
        theCurrency(root);
        aSave(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------ fixtures ------------------------------ */

    /** The target the bare fixtures aim at: not the default, so nothing reads the default by accident. */
    static final double TARGET = .03;

    /** A month's basket at this price level, both halves spent alike. */
    static void price(PriceIndex index, double level, int month) {
        index.takeMonth(.3 * level, .4 * level, 100, 100, month);
    }

    /** A basket based on a steady price: SETTLING_MONTHS of shopping at 1. Returns the month it was based in. */
    static int based(PriceIndex index) {
        int m = 0;
        while (!index.isBased()) price(index, 1, ++m);
        return m;
    }

    /** One press of a city, as the playtest steps it: a broke city steps rather than skips. */
    static void press(Game g) {
        int before = g.getMonth();
        g.simulateMonths(1);
        if (g.getMonth() == before) g.toggleNextMonth();
    }

    /** The playtest's founding (MonetaryCheck's), played on to this month. */
    static Game city(Path root, String label, int toMonth) {
        Game[] g = new Game[1];
        quietly(() -> {
            g[0] = MonetaryCheck.founding(root, label);
            while (g[0].getMonth() < toMonth) press(g[0]);
        });
        return g[0];
    }

    /* ================= 1. before the basket is based ================= */
    static void beforeTheBase(Path root) {
        out.println("--- 1. before the basket is based ---");
        PriceIndex index = new PriceIndex();
        Expectations x = new Expectations();
        x.reset();
        for (int m = 1; m < PriceIndex.SETTLING_MONTHS; m++) {
            price(index, 1 + m * .01, m);
            x.takeMonth(index, TARGET, .05, .045, .06);
        }
        check("fixture: a basket shopped for twenty-three months, prices rising, not based yet", !index.isBased());
        check("before the basket is based the expected level is founding's, 1", x.getExpectedLevel() == 1);
        check("...expected inflation is the target", x.getExpectedInflation() == TARGET);
        check("...and so is smoothed inflation", x.getSmoothedInflation() == TARGET);
        check("...credibility waits at KSEED, and nobody leans", x.getCredibility() == Expectations.KSEED && x.getLean() == 0);
        check("...and nothing moves: the month's step in credibility is nothing (0.7.45)", x.getCredibilityStep() == 0);

        Game founding = city(root, "x1", 0);
        Expectations fx = founding.getExpectations();
        double unit = founding.getDenomination().getUnit();
        out.printf("   the founding at month %d: based %s, expected %.4f, level %.4f%n", founding.getMonth(),
                founding.getPriceIndex().isBased(), fx.getExpectedInflation(), fx.getExpectedLevel());
        check("fixture: the founding city has played its first two years, its basket not yet based",
                founding.getMonth() > 12 && !founding.getPriceIndex().isBased());
        check("...where it expects its target and founding's prices",
                fx.getExpectedInflation() == founding.getDebtManager().getInflationTarget()
                        && fx.getExpectedLevel() == 1 && fx.getStruckLevel() == 1);
        check("...so its constants are struck at founding money",
                founding.getSectors().retail().getOpeningSellPrice() == ham.citybuildersim.sectors.Retail.OPENING_SELL_PRICE / unit
                        && founding.getHealthcare().feeAtOne(CareType.GENERAL) == Healthcare.GENERAL_FEE / unit);
        check("...and its currency is handed no drift", founding.anchoredDrift() == 0
                && founding.getForeignAccounts().getExpectedDrift() == 0);
    }

    /* ================= 2. on target ================= */
    static void onTarget() {
        out.println("\n--- 2. on target, credibility is won at GAIN_MONTHS ---");
        PriceIndex index = new PriceIndex();
        int m = based(index);
        Expectations x = new Expectations();
        x.reset();
        double monthly = Math.pow(1 + TARGET, 1.0 / 12);
        double level = 1, worst = 0, highest = 0, afterFiveYears = Double.NaN, worstStep = 0;
        boolean onTarget = true;
        x.takeMonth(index, TARGET, .04, .04, .04);
        double start = x.getCredibility();
        boolean seededStill = x.getCredibilityStep() == 0;
        for (int k = 1; k <= 600; k++) {
            level *= monthly;
            price(index, level, ++m);
            double was = x.getCredibility();
            x.takeMonth(index, TARGET, .04, .04, .04);
            worstStep = Math.max(worstStep, Math.abs(x.getCredibilityStep() - (x.getCredibility() - was)));
            onTarget &= Math.abs(x.getSmoothedInflation() - TARGET) <= Expectations.TOLERANCE;
            worst = Math.max(worst, Math.abs(x.getCredibility() - (was + (Expectations.KMAX - was) / Expectations.GAIN_MONTHS)));
            highest = Math.max(highest, x.getCredibility());
            if (k == 60) afterFiveYears = x.getCredibility();
        }
        out.printf("   from KSEED %.2f (%.4f the based month): %.4f five years on, %.6f after fifty (KMAX %.2f)%n",
                Expectations.KSEED, start, afterFiveYears, x.getCredibility(), Expectations.KMAX);
        check("fixture: fifty years with smoothed inflation inside TOLERANCE of the target", onTarget);
        close("every month on target, credibility climbed a GAIN_MONTHS-th of the way to KMAX", worst, 0, 0);
        check("...and never past it", highest <= Expectations.KMAX);
        close("...five years recovering (1 - 1/GAIN_MONTHS)^60 of the distance",
                (Expectations.KMAX - afterFiveYears) / (Expectations.KMAX - start),
                Math.pow(1 - 1.0 / Expectations.GAIN_MONTHS, 60), 1e-12);
        check("the month the anchor is seeded records no move (0.7.45)", seededStill);
        close("...and every month after, its step is the move credibility made: what NEEDS YOU's PRICES row reads",
                worstStep, 0, 0);
    }

    /* ================= 3. a miss ================= */

    /** Plays a basket rising at `annual` past a based start, with the rate set by `policy` (neutral, the rule's advice): returns {worst deviation from the recurrence, months of a part-share miss, months of a full miss, months the lean was the one asked for, credibility at the end}. */
    static double[] miss(double annual, java.util.function.DoubleBinaryOperator policy, double wantLean) {
        PriceIndex index = new PriceIndex();
        int m = based(index);
        Expectations x = new Expectations();
        x.reset();
        DebtManager rule = new DebtManager();
        rule.setInflationTarget(TARGET);
        double neutral = rule.ruleRate(TARGET, TARGET);
        double monthly = Math.pow(1 + annual, 1.0 / 12), level = 1;
        double worst = 0;
        int part = 0, full = 0, leaned = 0, stepOff = 0, falls = 0;
        for (int k = 0; k < 240; k++) {
            if (k > 0) { level *= monthly; price(index, level, ++m); }
            double inflation = index.hasRate() ? index.inflation() : TARGET;
            double advice = rule.advisedPolicyRate(inflation);
            double was = x.getCredibility();
            x.takeMonth(index, TARGET, policy.applyAsDouble(neutral, advice), neutral, advice);
            double off = Math.abs(x.getSmoothedInflation() - TARGET);
            if (k > 0 && x.getCredibilityStep() != x.getCredibility() - was) stepOff++;
            if (x.getCredibilityStep() < 0) falls++;
            if (off <= Expectations.TOLERANCE) continue;
            double share = Math.min(1, (off - Expectations.TOLERANCE) / Expectations.MISS_SCALE);
            if (share < 1) part++; else full++;
            if (Math.abs(x.getLean() - wantLean) < 1e-12) leaned++;
            double expected = was - (was - Expectations.KMIN) / Expectations.LOSS_MONTHS * share * (1 - x.getLean());
            worst = Math.max(worst, Math.abs(x.getCredibility() - Math.max(Expectations.KMIN, expected)));
        }
        return new double[] { worst, part, full, leaned, x.getCredibility(), stepOff, falls };
    }

    static void aMiss() {
        out.println("\n--- 3. a miss nobody leans against costs credibility; leaning against it does not ---");
        double[] alone = miss(.10, (neutral, advice) -> neutral, 0);
        out.printf("   ten per cent a year at the neutral rate: %d months a part miss, %d a full one, credibility %.4f at the end%n",
                (int) alone[1], (int) alone[2], alone[4]);
        check("fixture: inflation ran off target, through a part-share miss and past a full one", alone[1] > 0 && alone[2] > 0);
        check("fixture: ...with the rate at neutral, so nobody leaned", alone[3] == alone[1] + alone[2]);
        close("every month off target, credibility fell a LOSS_MONTHS-th of the way to KMIN times the miss's share", alone[0], 0, 0);
        check("...and ended nearer KMIN than KSEED", alone[4] - Expectations.KMIN < Expectations.KSEED - alone[4]);
        check("...each month's step the move it made, a fall in the months it fell (0.7.45: NEEDS YOU's PRICES row)",
                alone[5] == 0 && alone[6] > 0);

        double[] fought = miss(.10, (neutral, advice) -> advice, 1);
        check("fixture: the same miss, with the rate at the rule's advice: leaning all the way, every month",
                fought[1] + fought[2] > 0 && fought[3] == fought[1] + fought[2]);
        close("...so the miss cost nothing (the recurrence at a lean of one)", fought[0], 0, 0);
        check("...and no month's step is a fall", fought[5] == 0 && fought[6] == 0);
        check("...and credibility held where the months on target had taken it", fought[4] >= Expectations.KSEED);

        double[] half = miss(.10, (neutral, advice) -> neutral + .5 * (advice - neutral), .5);
        check("fixture: the rate halfway from neutral to the advice: a lean of one half", half[3] == half[1] + half[2]);
        close("...which costs half (the recurrence at the lean the month read)", half[0], 0, 1e-15);
        check("...ending between the two", alone[4] < half[4] && half[4] < fought[4]);
    }

    /* ================= 4. the floor ================= */
    static void theFloor() {
        out.println("\n--- 4. expected inflation has a floor ---");
        PriceIndex index = new PriceIndex();
        int m = based(index);
        Expectations x = new Expectations();
        x.reset();
        DebtManager rule = new DebtManager();
        rule.setInflationTarget(TARGET);
        double neutral = rule.ruleRate(TARGET, TARGET), monthly = Math.pow(.80, 1.0 / 12), level = 1;
        boolean floored = true;
        int under = 0;
        for (int k = 0; k < 240; k++) {
            if (k > 0) { level *= monthly; price(index, level, ++m); }
            double inflation = index.hasRate() ? index.inflation() : TARGET;
            x.takeMonth(index, TARGET, neutral, neutral, rule.advisedPolicyRate(inflation));
            double raw = x.getCredibility() * TARGET + (1 - x.getCredibility()) * x.getSmoothedInflation();
            if (raw < Expectations.EXPECTED_FLOOR) {
                under++;
                floored &= x.getExpectedInflation() == Expectations.EXPECTED_FLOOR;
            }
            floored &= x.getExpectedInflation() >= Expectations.EXPECTED_FLOOR;
        }
        out.printf("   prices falling a fifth a year: smoothed %.2f%%, credibility %.3f, expected %.2f%%%n",
                x.getSmoothedInflation() * 100, x.getCredibility(), x.getExpectedInflation() * 100);
        check("fixture: credibility and recent inflation would expect less than the floor, for months", under > 12);
        check("expected inflation never went under EXPECTED_FLOOR, and sat on it", floored);
    }

    /* ================= 5. the level ================= */
    static void theLevel() {
        out.println("\n--- 5. the expected level compounds at expected inflation ---");
        PriceIndex index = new PriceIndex();
        int m = based(index);
        Expectations x = new Expectations();
        x.reset();
        double monthly = Math.pow(1.06, 1.0 / 12), level = 1, worst = 0;
        x.takeMonth(index, TARGET, .04, .04, .04);
        check("fixture: the based month compounds the level once, from 1",
                x.getExpectedLevel() == Math.pow(1 + x.getExpectedInflation(), 1.0 / 12));
        double yearFrom = x.getExpectedLevel(), yearGrowth = 1;
        for (int k = 1; k <= 120; k++) {
            level *= monthly;
            price(index, level, ++m);
            double was = x.getExpectedLevel();
            x.takeMonth(index, TARGET, .04, .04, .04);
            worst = Math.max(worst, Math.abs(x.getExpectedLevel() - was * Math.pow(1 + x.getExpectedInflation(), 1.0 / 12)));
            if (k <= 12) yearGrowth *= Math.pow(1 + x.getExpectedInflation(), 1.0 / 12);
            if (k == 12) {
                close("a year at the expected inflations the year read grows the level by their product",
                        x.getExpectedLevel() / yearFrom, yearGrowth, 1e-12);
            }
        }
        close("every month the level moved by (1 + expected inflation)^(1/12)", worst, 0, 0);
        check("...and expected inflation moved off the target toward the six per cent being lived",
                x.getExpectedInflation() > TARGET + .01 && x.getExpectedInflation() < .06);
    }

    /* ================= 6. wages ================= */
    static void wages() {
        out.println("\n--- 6. wages: half what people expect, half the chase ---");
        double g = Math.pow(1.03, 1.0 / 12) - 1;
        LabourMarket steady = new LabourMarket();
        double index = 1;
        // As the month runs it: the wages read the index the last month
        // published, and then this month's prices move.
        for (int k = 0; k < 2_400; k++) {
            steady.updateCostOfLiving(index, g);
            index *= 1 + g;
        }
        double was = steady.getCostOfLiving();
        steady.updateCostOfLiving(index, g);
        index *= 1 + g;
        out.printf("   three per cent, expected: wages %.6f against the index %.6f (two years of it: %.6f)%n",
                steady.getCostOfLiving(), index, Math.pow(1 + g, -24));
        close("in steady inflation people expect, wages grow at it a month - not twice it",
                steady.getCostOfLiving() / was, 1 + g, 1e-12);
        close("...and lag the index the month publishes by two years of it", steady.getCostOfLiving() / index,
                Math.pow(1 + g, -24), 1e-9);

        LabourMarket step = new LabourMarket();
        step.updateCostOfLiving(1.1, 0);
        close("a level step nobody expected passes (1 - EXPECTED_SHARE) x DRIFT_PER_MONTH of its log the first month",
                Math.log(step.getCostOfLiving()),
                Math.log(1.1) * (1 - LabourMarket.EXPECTED_SHARE) * LabourMarket.DRIFT_PER_MONTH, 1e-15);
        for (int k = 0; k < 2_400; k++) step.updateCostOfLiving(1.1, 0);
        close("...and all of it in the end: pass-through COST_OF_LIVING_PASS_THROUGH", step.getCostOfLiving(),
                1 + .1 * LabourMarket.COST_OF_LIVING_PASS_THROUGH, 1e-9);
    }

    /* ================= 7. the constants ================= */

    /** A city at founding, before any month has struck anything: its catalogue and its ground are the founding figures. */
    static Game fresh(Path root) {
        Game[] g = new Game[1];
        quietly(() -> {
            g[0] = new Game(new GameFiles(root.resolve("fresh"), root.resolve("no-legacy")));
            g[0].run();
        });
        return g[0];
    }

    /** The Pe-struck constants a city holds, in order, for twins to be compared by. */
    static double[] constantsOf(Game g, String template) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(template);
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        return new double[] {
                g.getSectors().retail().getOpeningSellPrice(),
                g.getLandManager().getMarket().getBasePricePerSqFt(),
                t.getCashCost(), t.getUpkeep(),
                g.getHealthcare().feeAtOne(CareType.GENERAL),
                g.getEducation().feeAtOne(EducationType.UNIVERSITY),
                tax.pensionPerSeniorAt(1),
                g.getEquity().foundingPrice(),
                g.getBank().getPaidInPerBranch(),
                g.getBank().accountFee(1),
                tax.chargedFare() };
    }

    /** The largest relative gap between each of `scaled` times `by` and its `against` - a House has no upkeep, so a zero against a zero is no gap. */
    static double worstRelative(double[] scaled, double by, double[] against) {
        double worst = 0;
        for (int i = 0; i < scaled.length; i++) {
            worst = Math.max(worst, Math.abs(scaled[i] * by - against[i]) / Math.max(Math.abs(against[i]), Double.MIN_NORMAL));
        }
        return worst;
    }

    static void constants(Path root) {
        out.println("\n--- 7. the money constants, struck at the expected level ---");
        final String house = "House";
        Game atFounding = fresh(root);
        BuildingsTemplate founded = atFounding.getBuildingManager().getTemplateByName(house);
        double groundThen = atFounding.getLandManager().getMarket().getBasePricePerSqFt();
        Game a = city(root, "x7a", 48), b = city(root, "x7b", 48);
        Expectations x = a.getExpectations();
        double unit = a.getDenomination().getUnit(), level = x.getStruckLevel(), struck = unit / level;
        out.printf("   month %d: expected level %.6f, struck at %.6f, unit %.0f%n", a.getMonth(),
                x.getExpectedLevel(), level, unit);
        check("fixture: the basket is based and the level the month struck at is off founding",
                a.getPriceIndex().isBased() && Math.abs(level - 1) > 1e-4 && a.getBank().accountFee(1) > 0);
        BuildingsTemplate t = a.getBuildingManager().getTemplateByName(house);
        TaxPolicy tax = a.getEconomyManager().getTaxPolicy();
        check("the shelf's opening price is OPENING_SELL_PRICE / (unit / level)",
                a.getSectors().retail().getOpeningSellPrice() == ham.citybuildersim.sectors.Retail.OPENING_SELL_PRICE / struck);
        check("...a template's cash cost and upkeep its founding figures the same way",
                t.getCashCost() == founded.getCashCost() / struck && t.getUpkeep() == founded.getUpkeep() / struck);
        check("...the ground's local anchor",
                a.getLandManager().getMarket().getBasePricePerSqFt() == groundThen / struck);
        check("...the care fee and a university's tuition",
                a.getHealthcare().feeAtOne(CareType.GENERAL) == Healthcare.GENERAL_FEE / struck
                        && a.getEducation().feeAtOne(EducationType.UNIVERSITY) == Education.foundingTuition(EducationType.UNIVERSITY) / struck);
        check("...the pension's wage base, a founding share's par, the bank's paid-in capital and its account fee",
                tax.pensionPerSeniorAt(1) == PayTier.UNSKILLED.getMonthlyWage() / struck
                        && a.getEquity().foundingPrice() == Equity.FOUNDING_PRICE / struck
                        && a.getBank().getPaidInPerBranch() == Bank.PAID_IN_PER_BRANCH / struck
                        && a.getBank().accountFee(1) == Bank.ACCOUNT_FEE / struck);
        check("the fare a rider is charged is the dial, in founding money, at the level",
                tax.chargedFare() == tax.getTransitFare() * level && tax.getExpectedLevel() == level);
        BuildingManager bm = a.getBuildingManager();
        double labour = bm.labourCost(t), then = labour / bm.buildersWageIndex();
        check("fixture: the builders are paid, so the cost has a labour part", labour > 0 && bm.getExpectedLevel() == level);
        close("the labour in a cost is the founding labour, taken out at the level and put back at today's wages",
                bm.nonMaterialCost(t), t.getCashCost() - then * level + labour, 1e-9 * t.getCashCost());
        check("...never more than the cash over the level", then <= t.getCashCost() / level + 1e-12);

        // A reform, between the presses, on the twin.
        double[] before = constantsOf(b, house);
        boolean[] reformed = new boolean[1];
        quietly(() -> reformed[0] = b.reformCurrencyForTest(100));
        check("fixture: the twin's currency was reformed, a hundred to one", reformed[0] && b.getDenomination().getUnit() == 100 * unit);
        close("a reform divides every one of them by a hundred at once",
                worstRelative(constantsOf(b, house), 100, before), 0, 1e-12);
        quietly(() -> { press(a); press(b); });
        Expectations bx = b.getExpectations();
        double bStruck = b.getDenomination().getUnit() / bx.getStruckLevel();
        BuildingsTemplate bt = b.getBuildingManager().getTemplateByName(house);
        check("the month after, the reformed city strikes them at its new unit: founding / (unit / level)",
                b.getSectors().retail().getOpeningSellPrice() == ham.citybuildersim.sectors.Retail.OPENING_SELL_PRICE / bStruck
                        && bt.getCashCost() == founded.getCashCost() / bStruck
                        && b.getBank().accountFee(1) == Bank.ACCOUNT_FEE / bStruck);
        close("...at the level its unreformed self struck, a ratio a reform does not move",
                bx.getStruckLevel() / a.getExpectations().getStruckLevel(), 1, 1e-9);
        close("...so every constant is its unreformed twin's over a hundred",
                worstRelative(constantsOf(b, house), 100, constantsOf(a, house)), 0, 1e-9);
    }

    /* ================= 8. the currency ================= */
    static void theCurrency(Path root) {
        out.println("\n--- 8. the currency: a level for a real gap, and the anchored drift ---");
        final double gap = .03, trade = 3_000, gdp = 6_000;
        ForeignAccounts fx = CurrencyCheck.settled(trade, trade, gdp);
        double uip = Math.exp(-gap / ForeignAccounts.EXPECTED_REVERSION);
        fx.setRealRateDifferential(gap);
        close("uipLevel() is e^(-gap / EXPECTED_REVERSION)", fx.uipLevel(), uip, 1e-15);
        fx.setParity(1 / uip, 1, 0, 0);
        close("at that distance from parity the rate's own pressure is nothing", fx.ratePressure(), 0, 1e-12);
        fx.setParity(.9 / uip, 1, 0, 0);
        check("...nearer parity than that, it strengthens the currency on toward it", fx.ratePressure() < 0);
        fx.setParity(1.1 / uip, 1, 0, 0);
        check("...further from parity, it weakens it back", fx.ratePressure() > 0);
        out.printf("   a %.0f-point real gap: the currency %.1f%% from parity where investors expect nothing more%n",
                gap * 100, (1 / uip - 1) * 100);

        ForeignAccounts held = CurrencyCheck.settled(trade, trade, gdp);
        held.setParity(1, 1, 0, 0);
        held.setRealRateDifferential(gap);
        double last = held.getRate(), lastMove = 0;
        for (int k = 0; k < 3_000; k++) {
            held.repriceCurrency();
            lastMove = Math.abs(held.getRate() / last - 1);
            last = held.getRate();
        }
        out.printf("   held at the gap for 250 years: the rate %.6f, its last month's move %.2e (uipLevel %.6f, parity 1)%n",
                held.getRate(), lastMove, uip);
        check("a real gap held for good moves the rate to a level and stops, between uipLevel() and parity",
                lastMove < 1e-12 && held.getRate() > uip && held.getRate() < 1);

        ForeignAccounts drifting = CurrencyCheck.settled(trade, trade, gdp);
        drifting.setParity(1, 1, 0, 0);
        drifting.setRealRateDifferential(0);
        drifting.setExpectedDrift(.024);
        double r0 = drifting.getRate();
        drifting.repriceCurrency();
        close("with no push, the anchored drift moves the rate by its twelfth root, before the parity pull",
                drifting.getRate(), CurrencyCheck.pulled(r0 * Math.pow(1.024, 1.0 / 12), drifting.getParity()), 1e-15);
        ForeignAccounts pinned = CurrencyCheck.settled(trade, trade, gdp);
        pinned.pinRate(1.1);
        pinned.setExpectedDrift(.05);
        pinned.repriceCurrency();
        check("...and a pinned currency not at all", pinned.getRate() == 1.1);

        Game c = city(root, "x8", 48);
        Expectations x = c.getExpectations();
        double drift = (1 + x.getCredibility() * c.getDebtManager().getInflationTarget())
                / (1 + c.getWorldEconomy().realisedInflation()) - 1;
        out.printf("   a city at month %d: credibility %.4f, target %.1f%%, the world %.2f%%: drift %.3f%% a year%n",
                c.getMonth(), x.getCredibility(), c.getDebtManager().getInflationTarget() * 100,
                c.getWorldEconomy().realisedInflation() * 100, drift * 100);
        check("fixture: the city's basket is based", c.getPriceIndex().isBased());
        check("a city's anchored drift is the credible part of its target against the world's inflation",
                c.anchoredDrift() == drift);
        check("...and it is what the month handed its currency", c.getForeignAccounts().getExpectedDrift() == drift);
    }

    /* ================= 9. a save ================= */
    static void aSave(Path root) throws Exception {
        out.println("\n--- 9. the anchor through a save, and a save from before it ---");
        Game c = city(root, "x9", 50);
        Expectations x = c.getExpectations();
        GameFiles files = c.getGameFiles();
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = c.saveGame(10, "the anchor").ok);
        check("fixture: a city past its base month saved", ok[0] && c.getPriceIndex().isBased());
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        Expectations y = back.getExpectations();
        check("the anchor reloads whole: credibility, smoothed and expected inflation, both levels, the lean",
                java.util.Arrays.equals(x.toSaveArray(), y.toSaveArray()));
        check("...and the month's move in credibility, the eighth slot (0.7.45)",
                x.toSaveArray().length == Expectations.SAVE_SLOTS
                        && Double.compare(x.getCredibilityStep(), y.getCredibilityStep()) == 0);
        Expectations seven = new Expectations();
        double[] slots = x.toSaveArray();
        slots[7] = -.01;
        seven.restore(java.util.Arrays.copyOf(slots, 7));
        check("...a save from before it, seven slots, reads with no move: nothing fell the month it loads",
                seven.getCredibilityStep() == 0 && seven.getCredibility() == x.getCredibility());
        check("...and the constants struck at its level",
                java.util.Arrays.equals(constantsOf(c, "House"), constantsOf(back, "House")));
        quietly(() -> { press(c); press(back); });
        check("...and the next month is the same month",
                java.util.Arrays.equals(x.toSaveArray(), y.toSaveArray())
                        && java.util.Arrays.equals(constantsOf(c, "House"), constantsOf(back, "House")));

        // A save from before 0.7.42: the same file with no anchor in it.
        quietly(() -> ok[0] = c.saveGame(10, "the anchor").ok);
        com.google.gson.JsonObject json = com.google.gson.JsonParser
                .parseString(Files.readString(files.saveFile(10))).getAsJsonObject();
        check("fixture: the save carries the anchor under its own key", json.has("expectations"));
        json.remove("expectations");
        Files.writeString(files.saveFile(10), new com.google.gson.Gson().toJson(json));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Expectations z = old.getExpectations();
        PriceIndex index = old.getPriceIndex();
        double target = old.getDebtManager().getInflationTarget();
        double smoothed = index.hasRate() ? index.inflation() : target;
        double unit = old.getDenomination().getUnit();
        out.printf("   seeded: credibility %.2f, smoothed %.4f, expected %.4f, level %.1f (the saved city's %.4f)%n",
                z.getCredibility(), z.getSmoothedInflation(), z.getExpectedInflation(), z.getExpectedLevel(),
                x.getExpectedLevel());
        check("an older save is seeded: credibility KSEED, the year's inflation smoothed",
                z.getCredibility() == Expectations.KSEED && z.getSmoothedInflation() == smoothed);
        check("...expected inflation struck from the two",
                z.getExpectedInflation() == Math.max(Expectations.EXPECTED_FLOOR,
                        Expectations.KSEED * target + (1 - Expectations.KSEED) * smoothed));
        check("...its month's move in credibility at nothing (0.7.45)", z.getCredibilityStep() == 0);
        check("...and the level at 1, so nothing jumps: its constants are at founding money",
                z.getExpectedLevel() == 1 && z.getStruckLevel() == 1
                        && old.getSectors().retail().getOpeningSellPrice() == ham.citybuildersim.sectors.Retail.OPENING_SELL_PRICE / unit
                        && old.getBank().accountFee(1) == Bank.ACCOUNT_FEE / unit);
    }
}
