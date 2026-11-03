package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

/**
 * The blow-up study's second look (spec-blowup-7200.md): the wealth study's run
 * again, and from -Dblowup.from on every sector whose month moved more than
 * -Dblowup.gdp months of GDP, line by line, with the overdraft the restructure
 * forgave it. Not game code; the city is in GameFiles.scratch().
 *
 *   java ... ham.citybuildersim.SectorProbe SEED MONTHS OUT.txt
 */
public class SectorProbe {
    static PrintWriter w;
    static final int FROM = Integer.getInteger("blowup.from", 7380);
    static final double GDP = Double.parseDouble(System.getProperty("blowup.gdp", "0.5"));

    static String f(double v) { return String.format(Locale.ROOT, "%.4g", v); }

    static void month(Game g) {
        if (g.getMonth() < FROM) return;
        double gdp = g.getEconomyManager().getMonthGdp();
        double bar = Math.abs(gdp) * GDP;
        w.println("== month " + g.getMonth() + " gdpMonth " + f(gdp) + " index " + f(g.getPriceIndex().getIndex())
                + " fx " + f(g.getForeignAccounts().getRate()) + " policy " + f(g.getDebtManager().getPolicyRate())
                + " forgiven " + f(g.getEconomyManager().getOverdraftForgiven()));
        for (Sector s : g.getSectors().all()) {
            Sector.Statement st = s.statement();
            double forgiven = g.getEconomyManager().getOverdraftForgivenThisMonth(s.key());
            double big = Math.max(Math.abs(s.getCash()), Math.max(forgiven, Math.max(Math.abs(st.revenue),
                    Math.max(Math.abs(st.inputs), Math.abs(st.preTaxIncome)))));
            for (double v : st.capitalBySupplier.values()) big = Math.max(big, Math.abs(v));
            if (big < bar) continue;
            w.println("  " + s.key() + " cash " + f(s.getCash()) + " forgiven " + f(forgiven)
                    + " revenue " + f(st.revenue) + " inputs " + f(st.inputs) + " payroll " + f(st.payroll)
                    + " localSales " + f(st.localSales) + " toHouseholds " + f(st.salesToHouseholds) + " exports " + f(st.exports)
                    + " other " + f(st.otherRevenue) + " localPurchases " + f(st.localPurchases) + " imports " + f(st.imports)
                    + " interest " + f(st.interest) + " preTax " + f(st.preTaxIncome) + " profitTax " + f(st.profitTax)
                    + " paidEarlier " + f(st.paidEarlier) + " borrowing " + f(st.borrowingCosts) + " foreignIncome " + f(st.foreignIncome));
            dump("    bySupplier", st.purchasesBySupplier, bar);
            dump("    capitalBySupplier", st.capitalBySupplier, bar);
            dump("    otherParts", st.otherParts, bar);
            dump("    otherInputs", st.otherInputs, bar);
            split("    sold", st.sold, bar);
            split("    bought", st.bought, bar);
        }
        w.flush();
    }

    static void split(String label, Map<Good, Sector.Split> m, double bar) {
        for (Map.Entry<Good, Sector.Split> e : m.entrySet()) {
            Sector.Split x = e.getValue();
            if (Math.abs(x.total()) >= bar) w.println(label + " " + e.getKey() + " home " + f(x.atHome) + " abroad " + f(x.abroad));
        }
    }

    static void dump(String label, Map<String, Double> m, double bar) {
        for (Map.Entry<String, Double> e : m.entrySet()) {
            if (Math.abs(e.getValue()) >= bar) w.println(label + " " + e.getKey() + " " + f(e.getValue()));
        }
    }

    public static void main(String[] args) throws Exception {
        w = new PrintWriter(args[2]);
        WealthRun.hook = SectorProbe::month;
        WealthRun.main(new String[] {args[0], args[1], args[2] + ".wealth.csv"});
        w.close();
    }
}
