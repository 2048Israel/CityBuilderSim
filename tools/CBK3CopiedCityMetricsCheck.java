package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The same getter-only observer for both CBK3 copied-city continuations.
 * Uses only APIs present at b2f50a9ef/352aaf3b2; compile into a separate
 * observer directory and put it before either model's classes on the classpath.
 * No player, policy, rescue, fixture or funding setting is changed.
 */
public final class CBK3CopiedCityMetricsCheck {
    private static final PrintStream OUT = System.out;
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    private static void quietly(Runnable action) {
        PrintStream before = System.out;
        System.setOut(QUIET);
        try { action.run(); } finally { System.setOut(before); }
    }

    private static String number(double value) { return Double.toString(value); }

    private static void observe(Game g, double remitted, double revenue, int openingFailures) {
        Bank b = g.getBank();
        String share = Double.isFinite(remitted) && Double.isFinite(revenue) && revenue > 0
                ? number(remitted / revenue) : "unavailable";
        String[] values = {
            Integer.toString(g.getMonth()), Integer.toString(b.monthsInYear()), Boolean.toString(b.knowsYearAgo()),
            number(b.capitalRatio()), number(b.netInterestMargin()), number(b.netInterestMarginOverYear()),
            number(b.returnOnEquityOverYear()), number(g.preferredOfferSize()),
            Boolean.toString(g.isPreferredOfferPending()), number(b.averageOverYear(Bank.Line.EQUITY)),
            number(b.averageEarningAssetsOverYear()), number(b.getWeightedBook()),
            number(b.getBook()), number(b.getDeposits()), number(b.fundingRate()), number(b.depositRate()),
            number(remitted), number(revenue), share,
            Integer.toString(b.getFailures()), Integer.toString(b.getFailures() - openingFailures)
        };
        OUT.println("CBK3_METRICS," + String.join(",", values));
    }

    public static void main(String[] args) throws Exception {
        String supplied = System.getProperty("cbs.cbk3.evidence", "");
        if (supplied.isBlank()) throw new IllegalArgumentException("Name the approved copied evidence folder with -Dcbs.cbk3.evidence");
        Path source = Path.of(supplied);
        Path root = TempDirs.create("cbk3-copied-city-metrics");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = null;
        try {
            Files.createDirectories(files.savesDirectory());
            for (String name : new String[]{"autosave.json", "autosave-history.json", "autosave-map.bin"})
                Files.copy(source.resolve(name), files.savesDirectory().resolve(name));
            g = new Game(files);
            Game city = g;
            quietly(() -> city.loadGameSave(GameFiles.AUTOSAVE_SLOT));
            if (g.getLoadFailure() != null || g.getMonth() != 133)
                throw new AssertionError("The original evidence must load successfully at month 133 on scratch");
            int openingFailures = g.getBank().getFailures();
            double remitted = 0, revenue = 0;
            OUT.println("CBK3_METRICS,month,history_months,year_ago_known,risk_capital_ratio,nim_month_annual_rate,nim_year_annual_rate,"
                    + "roe_year_annual_rate,preferred_ask_k,preferred_pending,roe_average_equity_k,nim_average_earning_assets_k,"
                    + "rwa_k,book_k,deposits_k,funding_rate,savers_rate,remitted_sum_k,revenue_sum_k,remittance_share,failures,new_failures");
            observe(g, remitted, revenue, openingFailures);
            for (int played = 1; played <= 120; played++) {
                int before = g.getMonth();
                quietly(city::toggleNextMonth);
                if (g.getMonth() != before + 1) throw new AssertionError("Evidence month did not advance from " + before);
                MoneyAudit.Result audit = g.getLastMoneyAudit();
                if (audit == null || !Double.isFinite(audit.residual) || !Double.isFinite(audit.moved())
                        || Math.abs(audit.residual) > MoneyAudit.tolerance(audit.moved()))
                    throw new AssertionError("Evidence money audit at month " + g.getMonth() + ": " + audit);
                remitted += g.getCentralBank().getRemitted();
                revenue += g.getEconomyManager().getNationalAccounts().getTotalRevenue();
                // A pending offer is a state, not a fresh flow: retain each month without summing offers.
                observe(g, remitted, revenue, openingFailures);
            }
            if (g.getMonth() != 253) throw new AssertionError("The original 120-month horizon must end at month 253");
        } finally {
            if (g != null) g.awaitSaves();
            LongPlaytest.cleanUp(root);
        }
    }
}
