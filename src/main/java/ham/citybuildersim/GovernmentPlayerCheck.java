package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * The test player's annual wealth policy: both directions, boundaries, caps, the first eligible January and the actual skip/reload path.
 *
 * WHY (GV1/GV2). The study's December modulo missed January's newly opened levers (R0's dates; since OP1 every lever is open from the
 * founding, and the player's first move is month 13's), and a driver that only reviewed its skip's endpoint
 * could cross years without ever reviewing policy. The fixtures cause each balance directly; the last section plays through two actual
 * Januaries, saving and loading between them, and counts how often taxes really move. Every city and its slot 10 live in scratch data.
 */
public final class GovernmentPlayerCheck {
    private static final PrintStream out = System.out;
    private static int fails;

    private static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.println(label + (ok ? " OK" : " FAIL"));
    }

    private static void same(String label, double actual, double expected) {
        boolean ok = Double.isFinite(actual) && Math.abs(actual - expected) < 1e-10;
        if (!ok) fails++;
        out.printf("%s %.12g expected %.12g %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    private static Game city(String label) {
        return new Game(GameFiles.scratch(label));
    }

    /** A fixture's date, set on the city as its month (the policy hears no month since OP1). */
    private static void at(Game g, int month) throws ReflectiveOperationException {
        Field field = Game.class.getDeclaredField("month");
        field.setAccessible(true);
        field.setInt(g, month);
    }

    private static void history(Game g, int count, double revenue) throws ReflectiveOperationException {
        HistorySave history = new HistorySave();
        for (int m = 1; m <= count; m++) history.appendMonth(m, name -> name.equals("revenue") ? revenue : 0);
        Field field = Game.class.getDeclaredField("historySave");
        field.setAccessible(true);
        field.set(g, history);
    }

    private static void wealth(Game g, double years) {
        GovernmentView view = GovernmentView.of(g);
        g.setCashForTest(years * view.annualRevenue() - view.fundCash() - view.fundHoldings() - view.vault() + view.debt());
        same("fixture causes the requested years of wealth", GovernmentView.of(g).wealthYears(), years);
    }

    private static void rates(Game g, double profit, double sales, double pension, double ei, int withdrawal) {
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        tax.setProfitTaxRate(profit);
        tax.setSalesTaxRate(sales);
        tax.setPensionReplacement(pension);
        tax.setEiBenefitRate(ei);
        g.setFundWithdrawal(withdrawal * TreasuryFund.WITHDRAWAL_STEP);
    }

    private static void levers(Game g, double profit, double sales, double pension, double ei, int withdrawal) {
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        same("profit tax", tax.getProfitTaxRate(), profit);
        same("sales tax", tax.getSalesTaxRate(), sales);
        same("pension replacement", tax.getPensionReplacement(), pension);
        same("EI replacement", tax.getEiBenefitRate(), ei);
        same("fund withdrawal steps", g.getFund().getWithdrawalSteps(), withdrawal);
    }

    private static void directionsAndCaps() throws Exception {
        out.println("--- wealth moves all three lever families in both directions, to their caps ---");
        Game g = city("government-player-caps");
        history(g, GovernmentView.REVENUE_MONTHS, 1_000_000);
        at(g, 13);
        rates(g, .20, .10, .50, .65, 4);
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("a rich January changes policy", GovernmentPlayer.step(g));
        levers(g, .20 - GovernmentPlayer.PROFIT_STEP, .10 - GovernmentPlayer.SALES_STEP,
                .50 + GovernmentPlayer.TRANSFER_STEP, .65 + GovernmentPlayer.TRANSFER_STEP, TreasuryFund.MAX_WITHDRAWAL_STEPS);
        check("another invocation in the same January changes nothing", !GovernmentPlayer.step(g));

        at(g, 25);
        rates(g, GovernmentPlayer.POOR_PROFIT_MAX - .01, GovernmentPlayer.POOR_SALES_MAX - .01,
                GovernmentPlayer.POOR_PENSION_MIN + .02, GovernmentPlayer.POOR_EI_MIN + .02, 2);
        wealth(g, GovernmentView.SOUND_MIN / 2);
        check("a poor January changes policy", GovernmentPlayer.step(g));
        levers(g, GovernmentPlayer.POOR_PROFIT_MAX, GovernmentPlayer.POOR_SALES_MAX,
                GovernmentPlayer.POOR_PENSION_MIN, GovernmentPlayer.POOR_EI_MIN, 1);
        at(g, 37);
        check("next poor January reduces the fund one more literal step", GovernmentPlayer.step(g));
        levers(g, GovernmentPlayer.POOR_PROFIT_MAX, GovernmentPlayer.POOR_SALES_MAX,
                GovernmentPlayer.POOR_PENSION_MIN, GovernmentPlayer.POOR_EI_MIN, 0);
        at(g, 49);
        check("poor-side floors and ceilings stop further moves", !GovernmentPlayer.step(g));

        at(g, 61);
        rates(g, GovernmentPlayer.PROFIT_STEP / 2, GovernmentPlayer.SALES_STEP / 2,
                TaxPolicy.MAX_REPLACEMENT - .01, TaxPolicy.MAX_EI_BENEFIT - .01, 0);
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("rich-side caps take a partial last step", GovernmentPlayer.step(g));
        levers(g, 0, 0, TaxPolicy.MAX_REPLACEMENT, TaxPolicy.MAX_EI_BENEFIT, TreasuryFund.MAX_WITHDRAWAL_STEPS);
        at(g, 73);
        check("rich-side caps stop further moves", !GovernmentPlayer.step(g));

        at(g, 85);
        rates(g, TaxPolicy.MAX_INCOME_TAX, TaxPolicy.MAX_INCOME_TAX, 0, 0, 0);
        wealth(g, -1);
        check("poor policy never raises a zero promise or cuts a tax already above its ceiling", !GovernmentPlayer.step(g));
        levers(g, TaxPolicy.MAX_INCOME_TAX, TaxPolicy.MAX_INCOME_TAX, 0, 0, 0);
    }

    private static void calendarAndBoundaries() throws Exception {
        out.println("--- only a new completed January outside the band moves policy ---");
        Game g = city("government-player-calendar");
        history(g, GovernmentView.REVENUE_MONTHS, 1_000_000);
        rates(g, .20, .10, .50, .65, 4);
        at(g, 1);
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("founding January does not act even with seeded history", !GovernmentPlayer.step(g));
        at(g, 12);
        check("December does not act", !GovernmentPlayer.step(g));
        at(g, 13);
        wealth(g, GovernmentView.RICH_MIN);
        check("exactly two years stays in the band", !GovernmentPlayer.step(g));
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("an already reviewed January cannot act after its wealth changes", !GovernmentPlayer.step(g));
        at(g, 25);
        wealth(g, GovernmentView.SOUND_MIN);
        check("exactly half a year stays in the band", !GovernmentPlayer.step(g));
        at(g, 37);
        wealth(g, (GovernmentView.SOUND_MIN + GovernmentView.RICH_MIN) / 2);
        check("inside the band does nothing", !GovernmentPlayer.step(g));
        at(g, 49);
        history(g, GovernmentView.REVENUE_MONTHS - 1, 1_000_000);
        check("less than a full year of revenue does nothing", !GovernmentPlayer.step(g));
        history(g, GovernmentView.REVENUE_MONTHS, 1_000_000);
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("an unmeasurable January was still reviewed only once", !GovernmentPlayer.step(g));
        at(g, 61);
        history(g, GovernmentView.REVENUE_MONTHS, 0);
        check("zero annual revenue does nothing", !GovernmentPlayer.step(g));
        at(g, 73);
        history(g, GovernmentView.REVENUE_MONTHS, 1_000_000);
        wealth(g, GovernmentView.RICH_MIN + 1);
        check("a later measurable January can act", GovernmentPlayer.step(g));
    }

    private static void openFromFounding() throws Exception {
        out.println("--- every lever this player moves is open from the founding, and moves in the first eligible January ---");
        Game g = city("government-player-dates");
        // The founding's levers (OP1): the income taxes at the founding rate, every promise at zero, as Game opens them.
        g.getEconomyManager().getTaxPolicy().openAtFounding();
        history(g, GovernmentView.REVENUE_MONTHS, 1_000_000);
        at(g, 13);
        wealth(g, GovernmentView.RICH_MIN + 1);
        GovernmentPlayer.step(g);
        for (PolicyDates.Lever lever : new PolicyDates.Lever[] { PolicyDates.Lever.PROFIT_TAX, PolicyDates.Lever.SALES_TAX,
                PolicyDates.Lever.PENSION_REPLACEMENT, PolicyDates.Lever.EI }) {
            double expected = switch (lever) {
                case PROFIT_TAX -> TaxPolicy.DEFAULT_INCOME_TAX - GovernmentPlayer.PROFIT_STEP;
                case SALES_TAX -> TaxPolicy.DEFAULT_INCOME_TAX - GovernmentPlayer.SALES_STEP;
                default -> GovernmentPlayer.TRANSFER_STEP;
            };
            same(lever.label() + " moves in the first eligible January, month 13", rate(g, lever), expected);
        }
    }

    private static double rate(Game g, PolicyDates.Lever lever) {
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        return switch (lever) {
            case PROFIT_TAX -> tax.getProfitTaxRate();
            case SALES_TAX -> tax.getSalesTaxRate();
            case PENSION_REPLACEMENT -> tax.getPensionReplacement();
            case EI -> tax.getEiBenefitRate();
            default -> throw new IllegalArgumentException(lever.toString());
        };
    }

    private static void fixtureRunnerKeepsPolicy() {
        out.println("--- borrowed fixture months retain the supplied policy across January ---");
        Game g = LongPlaytest.probeCity(GameFiles.scratch("government-fixture-policy"), null);
        g.setCashForTest(1e12);
        rates(g, .25, .10, .50, .65, 0);
        int before = g.getMonth();
        LongPlaytest.run(g, 12);
        check("the fixture crosses a January with measurable wealth above the band",
                CityCalendar.yearOf(g.getMonth()) > CityCalendar.yearOf(before)
                        && GovernmentView.of(g).wealthYears() > GovernmentView.RICH_MIN);
        levers(g, .25, .10, .50, .65, 0);
    }

    private static void skipsAndReload() throws Exception {
        out.println("--- actual skips cross each January once, and a loaded January is not replayed ---");
        GameFiles files = GameFiles.scratch("government-player-play");
        Game g = new Game(files);
        g.run();
        g.setCashForTest(1e12);
        rates(g, .25, .10, .50, .65, 0);
        LongPlaytest.villageBuild(g, "House", 40);
        LongPlaytest.villageBuild(g, "Convenience Store", 3);
        LongPlaytest.villageBuild(g, "Mixed Farm", 2);
        int changes = 0;
        LongPlaytest.runPlayer(g, 12);
        same("the first twelve-month skip finishes in January", g.getMonth(), 13);
        check("the played city really is above the band", GovernmentView.of(g).wealthYears() > GovernmentView.RICH_MIN);
        if (Math.abs(g.getEconomyManager().getTaxPolicy().getProfitTaxRate() - (.25 - GovernmentPlayer.PROFIT_STEP)) < 1e-10) changes++;
        same("one annual tax move inside the first long skip", changes, 1);
        check("the scratch January save succeeds", g.saveGame(10, "Government player scratch").ok);
        Game loaded = new Game(files);
        loaded.loadGameSave(10);
        double saved = loaded.getEconomyManager().getTaxPolicy().getProfitTaxRate();
        same("the loaded January retains its policy", saved, .25 - GovernmentPlayer.PROFIT_STEP);
        LongPlaytest.runPlayer(loaded, 1);
        same("resuming a loaded January runs February without another annual move", loaded.getEconomyManager().getTaxPolicy().getProfitTaxRate(), saved);
        LongPlaytest.runPlayer(loaded, 11);
        same("the second skip reaches the next January", loaded.getMonth(), 25);
        if (Math.abs(loaded.getEconomyManager().getTaxPolicy().getProfitTaxRate() - (saved - GovernmentPlayer.PROFIT_STEP)) < 1e-10) changes++;
        same("two actual annual tax moves over two played years", changes, 2);
        check("an immediate repeated review after the skip does nothing", !GovernmentPlayer.step(loaded));
    }

    public static void main(String[] args) throws Exception {
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            try {
                directionsAndCaps();
                calendarAndBoundaries();
                openFromFounding();
                fixtureRunnerKeepsPolicy();
                skipsAndReload();
            } finally {
                System.setOut(out);
            }
        }
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
