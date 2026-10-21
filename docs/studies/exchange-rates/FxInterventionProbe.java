package ham.citybuildersim;

import java.io.PrintStream;
import java.io.OutputStream;
import java.nio.file.*;

/** Probe, not a harness: does the treasury's purchase or sale of reserves move the D$? Three twins from one fixture. */
public class FxInterventionProbe {
    static PrintStream out = System.out;
    static PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static Game city(String label) throws Exception {
        Path root = Files.createTempDirectory("fxprobe-" + label);
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
            city.simulateMonths(36);
        } finally { System.setOut(out); }
        return city;
    }

    public static void main(String[] a) throws Exception {
        Game ctl = city("ctl"), buy = city("buy"), sell = city("sell");
        double cash = buy.getCash();
        double vault = sell.getForeignAccounts().getReservesUsd();
        out.printf("m%d: rate %.5f %.5f %.5f | cash %,.0f vault US$%,.0f | trade/mo exp %,.0f imp %,.0f%n",
                ctl.getMonth(), ctl.getForeignAccounts().getRate(), buy.getForeignAccounts().getRate(), sell.getForeignAccounts().getRate(),
                cash, vault, ctl.getForeignAccounts().monthlyExports(), ctl.getForeignAccounts().monthlyImports());
        double spent = buy.buyForeignCurrency(cash * 0.5);
        double got = sell.sellForeignCurrency(sell.getForeignAccounts().getReserves() * 0.9);
        out.printf("buy twin spent D$%,.0f on dollars (%.1f months of imports); sell twin sold D$%,.0f of the vault%n",
                spent, spent / Math.max(1, ctl.getForeignAccounts().monthlyImports()), got);
        for (int m = 1; m <= 24; m++) {
            System.setOut(quiet);
            try { ctl.simulateMonths(1); buy.simulateMonths(1); sell.simulateMonths(1); } finally { System.setOut(out); }
            ForeignAccounts c = ctl.getForeignAccounts(), b = buy.getForeignAccounts(), s = sell.getForeignAccounts();
            if (m <= 6 || m % 6 == 0)
                out.printf("+%2d  control %.5f | bought %.5f (%+.3f%%) | sold %.5f (%+.3f%%) | pressure c %+.4f b %+.4f s %+.4f%n", m,
                        c.getRate(), b.getRate(), 100 * (b.getRate() / c.getRate() - 1), s.getRate(), 100 * (s.getRate() / c.getRate() - 1),
                        c.getLastPressure(), b.getLastPressure(), s.getLastPressure());
        }
    }
}
