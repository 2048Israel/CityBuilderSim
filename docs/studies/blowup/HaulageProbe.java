package ham.citybuildersim;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;

/**
 * The blow-up study's third look (spec-blowup-7200.md): the wealth study's run
 * again, and from -Dblowup.from on Haulage's price rule term by term each month
 * (Haulage.closeMonth's reprice: the running costs, the rate base, the trucks
 * and what they cost, the fleet account, the quote). Read by reflection; no
 * code of the game's changes. The city is in GameFiles.scratch().
 *
 *   java ... ham.citybuildersim.HaulageProbe SEED MONTHS OUT.csv
 */
public class HaulageProbe {
    static PrintWriter w;
    static boolean header;
    static final int FROM = Integer.getInteger("blowup.from", 7370);
    /** ...and before FROM, a row every this many months (0: none). */
    static final int EVERY = Integer.getInteger("blowup.every", 0);

    static double field(Object o, String name) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return ((Number) f.get(o)).doubleValue();
    }

    static double call(Object o, String name) throws Exception {
        Method m = o.getClass().getDeclaredMethod(name);
        m.setAccessible(true);
        return ((Number) m.invoke(o)).doubleValue();
    }

    static void month(Game g) {
        if (g.getMonth() < FROM && (EVERY <= 0 || g.getMonth() % EVERY != 0)) return;
        try {
            Freight fr = g.getSectors().freight();
            Method hm = Freight.class.getDeclaredMethod("haulage");
            hm.setAccessible(true);
            Haulage h = (Haulage) hm.invoke(fr);
            String[] names = { "month", "gdpMonth", "quote", "steadyQuote", "allowed", "capacityInForce", "carried", "tonnes",
                    "tightness", "bill", "fleetOwed", "worn", "diesel", "trucks", "truckPrice", "trucksBought",
                    "buildingsValue", "landValue", "haulagePayroll", "maintenance", "propertyTax", "freightCash",
                    "freightRevenue", "freightPreTax", "freightDebt", "freightBlocked", "need" };
            BusinessDebtManager bd = g.getEconomyManager().getBusinessDebtManager();
            double[] v = {
                    g.getMonth(), g.getEconomyManager().getMonthGdp(),
                    field(h, "quote"), field(h, "steadyQuote"), field(h, "allowed"), field(h, "capacityInForce"),
                    field(h, "carried"), field(h, "tonnes"), field(h, "tightness"), field(h, "bill"),
                    field(h, "fleetOwed"), field(h, "worn"), field(h, "diesel"), h.trucks(),
                    call(fr, "truckPrice"), call(fr, "trucksBought"),
                    fr.getBuildingsValue(), fr.getLandValue(), call(fr, "haulagePayroll"),
                    fr.getMaintenanceExpense(), fr.getPropertyTaxExpense(), fr.getCash(),
                    fr.statement().revenue, fr.statement().preTaxIncome, bd.getPrincipal(fr.key()),
                    bd.isBorrowingBlocked(fr.key()) ? 1 : 0, h.getNeed() };
            if (!header) { w.println(String.join(",", names)); header = true; }
            StringBuilder b = new StringBuilder();
            for (double x : v) { if (b.length() > 0) b.append(','); b.append(String.format(Locale.ROOT, "%.6g", x)); }
            w.println(b);
            w.flush();
        } catch (Exception e) {
            w.println("probe failed: " + e);
            w.flush();
        }
    }

    public static void main(String[] args) throws Exception {
        w = new PrintWriter(args[2]);
        WealthRun.hook = HaulageProbe::month;
        WealthRun.main(new String[] {args[0], args[1], args[2] + ".wealth.csv"});
        w.close();
    }
}
