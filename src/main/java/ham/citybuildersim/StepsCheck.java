package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;
import javafx.application.Platform;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Magnitude controls follow the figure and bond lots follow the month's money.
 *
 * WHAT THIS HAS TO PROVE (SZ1):
 * 1. Empty, small, large and billion-sized figures follow one leading-digit
 *    rule, with no duplicate step and no step below the caller's least.
 * 2. Every instrument's dial is priced at 1900, reference and hyperinflated
 *    levels, with positive round lots and the minimum-issue cap caused.
 * 3. The city's live lot API reads its price index, currency unit and minimum.
 * 4. Real presses on a large building card and a large fund transfer move
 *    large figures, and the card's next labels follow the new magnitude.
 * Restoring the old fixed lots or fixed UI steps must fail this harness.
 */
public final class StepsCheck {
    static int fails;
    static PrintStream out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-88s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected) {
        check(label + " (" + actual + " against " + expected + ")",
                Math.abs(actual - expected) <= Math.max(Math.abs(expected), 1) * 1e-12);
    }

    static void steps(String label, double value, double least, double... expected) {
        check(label, Arrays.equals(MagnitudeSteps.of(value, least), expected));
    }

    static boolean round(double lot) {
        if (!(lot > 0) || !Double.isFinite(lot)) return false;
        double leading = lot / Math.pow(10, Math.floor(Math.log10(lot)));
        return Math.abs(leading - 1) < 1e-10 || Math.abs(leading - 2) < 1e-10 || Math.abs(leading - 5) < 1e-10;
    }

    static <T> T fx(java.util.concurrent.Callable<T> call) throws Exception {
        FutureTask<T> task = new FutureTask<>(call);
        Platform.runLater(task);
        return task.get(60, TimeUnit.SECONDS);
    }

    /** Every component changes equally, so the model's actual basket has this price ratio regardless of its weights. */
    static void uniformPrices(Game game, double level, int month) {
        double[] prices = new double[PriceIndex.COMPONENTS], spends = new double[PriceIndex.COMPONENTS];
        double[] fees = new double[PriceIndex.FEE_LINES], feeSpends = new double[PriceIndex.FEE_LINES];
        Arrays.fill(prices, level);
        Arrays.fill(fees, level);
        Arrays.fill(spends, 1);
        Arrays.fill(feeSpends, 1);
        game.getPriceIndex().takeMonth(prices, spends, fees, feeSpends, month);
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        out.println("--- 1. one magnitude rule, including the least step ---");
        steps("zero has just its least step", 0, 1, 1);
        steps("seven steps by one", 7, 1, 1);
        steps("23,000 steps by 10,000 and 1,000", 23_000, 1, 10_000, 1_000);
        steps("3.4 billion steps by a billion and a tenth of one", 3.4e9, 1, 1e9, 1e8);
        steps("the least step replaces both small steps without a duplicate", 7, 50, 50);
        steps("the fine step respects a fractional least", 7, .25, 1, .25);
        steps("an empty money field keeps its own least", 0, KeptBench.fundMoneyStep(), KeptBench.fundMoneyStep());
        close("a count cannot wrap negative when increased", MagnitudeSteps.count(Long.MAX_VALUE, 1e18), Long.MAX_VALUE);
        close("a count cannot step below zero", MagnitudeSteps.count(7, -10), 0);

        out.println("--- 2. every lot dial, price levels and an active cap ---");
        String[] kinds = { "Note", "Term", "Serial", "Float", "Linked" };
        double[] dials = { BondMarket.BILL_LOT, BondMarket.BOND_LOT, BondMarket.AMORTIZING_LOT,
                           BondMarket.FLOATING_LOT, BondMarket.LINKED_LOT };
        for (int k = 0; k < kinds.length; k++) {
            String kind = kinds[k];
            close(kind + " selects its own dial", BondMarket.referenceLot(kind), dials[k]);
            for (double level : new double[] { PriceLevel.FACTOR, 1, 1e9 }) {
                double priced = dials[k] * level;
                double lot = BondMarket.issueLot(kind, level, priced * 2);
                check(kind + " at level " + level + " is positive and 1–2–5", round(lot));
                check(kind + " follows its dial at level " + level, lot <= priced && lot > priced / 2.5);
                double minimum = priced * .0037;
                double capped = BondMarket.issueLot(kind, level, minimum);
                check(kind + " cap fixture is below the unconstrained lot", minimum < lot);
                check(kind + " capped lot is round and no larger than the minimum", round(capped) && capped <= minimum && capped > minimum / 2.5);
            }
        }
        close("rounding just below a decade goes down", MagnitudeSteps.roundDown125(Math.nextDown(10.0)), 5);

        out.println("--- 3. the live city supplies its month's lot, with no saved lot ---");
        Game priceCity = new Game(GameFiles.scratch("steps-price-binding"));
        for (int month = 1; month <= PriceIndex.SETTLING_MONTHS; month++) uniformPrices(priceCity, 1, month);
        check("fixture: real shopping bases the basket after its own settling dial", priceCity.getPriceIndex().isBased());
        double largest = Arrays.stream(dials).max().orElseThrow();
        double level = priceCity.minimumIssueSize() / (largest * PriceLevel.FACTOR) / 100;
        uniformPrices(priceCity, level, PriceIndex.SETTLING_MONTHS + 1);
        close("fixture: the actual price index, rather than just a lot argument, moves", priceCity.getPriceIndex().getIndex(), level);
        double[] lowLots = Arrays.stream(kinds).mapToDouble(priceCity::issueLot).toArray();
        check("fixture: even the largest dial stays strictly below the cap after a tenfold price rise",
                largest * PriceLevel.FACTOR * level * 10 < priceCity.minimumIssueSize());
        uniformPrices(priceCity, level * 10, PriceIndex.SETTLING_MONTHS + 2);
        close("fixture: every price in the actual index rises tenfold", priceCity.getPriceIndex().getIndex(), level * 10);
        for (int k = 0; k < kinds.length; k++) {
            close(kinds[k] + " live lot follows the month's changed index", priceCity.issueLot(kinds[k]), lowLots[k] * 10);
        }

        Game game = new Game(GameFiles.scratch("steps-check"));
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            try { game.run(); } finally { System.setOut(out); }
        }
        for (String kind : kinds) {
            double priced = PriceLevel.FACTOR * game.getPriceIndex().getIndex() / game.getDenomination().getUnit();
            close(kind + " live local lot", game.issueLot(kind), BondMarket.issueLot(kind, priced, game.minimumIssueSize()));
            double usd = BondMarket.issueLot(game, kind, "USD");
            check(kind + " live dollar lot is round and within its own minimum", round(usd) && usd <= game.minimumIssueSizeIn("USD"));
        }

        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            try {
                game.getAutoBuilder().setOn(false, null);
                game.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
                game.setRolloverCap(Auction.Cap.FILL);
                game.handleTBillLogic(game.minimumIssueSize() * 100, 1, game.issueLot("Note"), Auction.Cap.FILL);
                game.setCashForTest(0);
            } finally { System.setOut(out); }
        }
        Rollover.Plan plan = game.rolloverPlan();
        check("fixture: one month's paper really falls due and needs a rollover", plan.due() > 0 && plan.issues().size() == 1);
        if (plan.issues().size() == 1) {
            Rollover.Issue issue = plan.issues().get(0);
            DebtQuote expected = game.quoteTBill(issue.cash(), issue.term(), game.issueLot("Note"), Auction.Cap.FILL);
            close("the real rollover plan rounds at the live lot", issue.face(), expected.faceValue());
            int before = game.getDebtManager().getAuctionTally().auctionSales();
            try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
                System.setOut(quiet);
                try { game.toggleNextMonth(); } finally { System.setOut(out); }
            }
            int sales = game.getDebtManager().getAuctionTally().auctionSales() - before;
            check("the month's real rollover fires the auction (" + sales + " sales)", sales > 0 && game.getRollover().getLastIssued() > 0);
            close("the real sale books the plan's live-lot face", game.getRollover().getLastIssued(), expected.faceValue());
        }

        out.println("--- 4. actual building-card and fund-step presses at large figures ---");
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            out.println("UI presses skipped: no display");
        } else {
            java.util.concurrent.CountDownLatch started = new java.util.concurrent.CountDownLatch(1);
            Platform.setImplicitExit(false);
            Platform.startup(started::countDown);
            if (!started.await(60, TimeUnit.SECONDS)) throw new IllegalStateException("JavaFX startup timed out");
            try {
                fx(() -> {
                    KeptBench bench = new KeptBench(game);
                    String building = "Walk-in Clinic";
                    KeptBench.Page page = bench.build("SZ1 large card", BuildAdvice.HEALTHCARE, Map.of(building, 23_000L));
                    bench.home(page);
                    bench.drawKept(page);
                    int card = bench.buildCardNames().indexOf(building);
                    check("fixture has the large clinic order on its actual card", card >= 0 && bench.buildOrdered(building) == 23_000);
                    if (card >= 0) {
                        check("the large card offers +10k", "+10k".equals(bench.buildStepLabel(card, true)));
                        check("the large card's button accepts a real press", bench.buildStep(card, bench.buildStepLabel(card, true)));
                        close("that press adds 10,000 buildings", bench.buildOrdered(building), 33_000);
                        check("the fine card step offers +1k", "+1k".equals(bench.buildStepLabel(card, false)));
                        for (int i = 0; i < 7; i++) bench.buildStep(card, bench.buildStepLabel(card, true));
                        close("eight large presses cross the next decade", bench.buildOrdered(building), 103_000);
                        check("the same card refreshes its leading step to +100k", "+100k".equals(bench.buildStepLabel(card, true)));
                    }
                    KeptBench.Page fund = bench.finances("SZ1 fund", "The city's fund", "Rules & cash");
                    bench.home(fund);
                    bench.fundAsk(3.4e9);
                    bench.drawKept(fund);
                    double[] by = MagnitudeSteps.of(bench.fundAsk(), KeptBench.fundMoneyStep());
                    int presses = 0;
                    for (String label : bench.fundStepLabels()) {
                        double before = bench.fundAsk();
                        check("fund step label can be pressed: " + label, bench.pressChip(label));
                        double moved = bench.fundAsk() - before;
                        check("the fund press moves a leading unit or its tenth", moved == by[0] || moved == by[1]);
                        presses++;
                    }
                    check("the actual fund has exactly two positive magnitude steps", presses == by.length);
                    return null;
                });
            } finally { Platform.exit(); }
        }
        out.println("StepsCheck: " + fails + " failures");
        System.exit(fails);
    }
}
