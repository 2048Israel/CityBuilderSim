package ham.citybuildersim;

import java.io.PrintStream;
import java.util.*;

/**
 * Scratch, not in the build (FD1): the default playtest, every building a sector sells and every order, by type, so the
 * sold-and-bought-again churn can be counted: for each sale by a sector, the next order of the same type
 * and the months between. Freight's own state is logged at each of its sales and orders.
 * It needs a scratch copy of LongPlaytest compiled ahead of target/classes, with `static java.util.function.Consumer<Game> probeHook;`
 * after `static boolean layoutOn;` and `if (probeHook != null) probeHook.accept(g);` after run()'s `g.simulateMonths(1);`;
 * FD1Ens runs it on an ensemble run (tools/ensemble/Ensemble.java compiled beside it).
 *   java -Dplaytest.months=4000 -cp "<scratch classes>;target/classes;<jars>" ham.citybuildersim.FD1Probe out.txt
 */
public class FD1Probe {
    static PrintStream log;
    static int[] qty, uc;
    static Set<String> seen = new HashSet<>();
    /** type name -> months sold (by a sector, not the city) */
    static Map<String, List<int[]>> sales = new TreeMap<>();   // {month, qty}
    static Map<String, List<int[]>> orders = new TreeMap<>();  // {month, qty}
    static Map<String, String> sectorOf = new HashMap<>();
    static int lastMonth;

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FD1Probe::month;
        Runtime.getRuntime().addShutdownHook(new Thread(FD1Probe::summary));
        LongPlaytest.main(new String[0]);
    }

    static void month(Game g) {
        try {
            int month = g.getMonth();
            lastMonth = month;
            BuildingManager bm = g.getBuildingManager();
            List<BuildingsTemplate> ts = bm.getTemplates();
            int max = 0;
            for (BuildingsTemplate t : ts) max = Math.max(max, t.getId());
            int[] q = new int[max + 1];
            int[] u = bm.getUnderConstructionById();
            for (BuildingsTemplate t : ts) { q[t.getId()] = bm.getQuantity(t.getId()); sectorOf.put(t.getName(), t.getSector()); }
            // this month's sales by type
            Map<String, Integer> soldNow = new HashMap<>();
            for (DemolitionLog.Entry e : g.getDemolitionLog().all()) {
                if (e.month < month - 1) continue;
                if (!seen.add(e.month + "|" + e.building + "|" + e.quantity + "|" + e.sector)) continue;
                if (e.sector != null && e.sector.startsWith("City")) continue;
                soldNow.merge(e.building, e.quantity, Integer::sum);
                sales.computeIfAbsent(e.building, k -> new ArrayList<>()).add(new int[] { e.month, e.quantity });
                if ("Freight".equals(e.sector)) {
                    Sector fr = g.getSectors().byKey(Sectors.FREIGHT);
                    double[] dc = fr.retirementDemandAndCapacity(g);
                    log.printf("SOLD m%d %s x%d (%s): depots %d, terminals %d, spurs %d, lines %d, on order %d; tonnes %,.0f, capacity %,.0f, cash %,.0f, losses %d; %s%n",
                            e.month, e.building, e.quantity, e.sector, q[141], q[64], q[62], q[63], bm.getUnderConstructionBySector("Freight"),
                            dc[0], dc[1], fr.getCash(), g.getBusinessInvestment().getLossMonths("Freight"), g.getLastInvestment("Freight"));
                    ham.citybuildersim.sectors.Freight f = (ham.citybuildersim.sectors.Freight) fr;
                    log.printf("   PROCEEDS m%d: plot %,.0f%n", e.month, e.proceeds);
                    log.printf("   BOOKS m%d: net %,.0f; rail bill %,.0f, rail running %,.0f, rail allowed %,.0f; trucks bill %,.0f, trucks allowed %,.0f; interest %,.0f, bonds %,.0f, payroll %,.0f%n", e.month, fr.getNetIncome(), f.getHaulageBilled(), f.getRunningCost(), f.getAllowedRevenue(), f.haulage().getBill(), f.haulage().getAllowed(), fr.getInterestExpense(), fr.getBondsPayable(), fr.getPayroll());
                }
            }
            if (qty != null && uc != null) {
                for (BuildingsTemplate t : ts) {
                    int id = t.getId();
                    int sold = soldNow.getOrDefault(t.getName(), 0);
                    int completed = q[id] - qty[id] + sold;
                    int started = (id < u.length ? u[id] : 0) - (id < uc.length ? uc[id] : 0) + Math.max(0, completed);
                    if (started > 0) {
                        orders.computeIfAbsent(t.getName(), k -> new ArrayList<>()).add(new int[] { month, started });
                        if ("Freight".equals(t.getSector()) && t.getTruckRoom() <= 0) {
                            Sector fr = g.getSectors().byKey(Sectors.FREIGHT);
                            double[] dc = fr.retirementDemandAndCapacity(g);
                            log.printf("   PRICE m%d: %s costs %,.0f%n", month, t.getName(), g.getBusinessInvestment().getCostOf(t, 1));
                            log.printf("ORDER m%d %s x%d: depots %d, terminals %d, spurs %d, lines %d; tonnes %,.0f, capacity %,.0f, cash %,.0f; %s%n",
                                    month, t.getName(), started, q[141], q[64], q[62], q[63], dc[0], dc[1], fr.getCash(), g.getLastInvestment("Freight"));
                        }
                    }
                }
            }
            qty = q;
            uc = u.clone();
        } catch (Exception ex) {
            log.println("m" + g.getMonth() + " threw " + ex);
            ex.printStackTrace(log);
        }
    }

    static void summary() {
        log.println("=== SUMMARY to m" + lastMonth + " ===");
        int[] horizons = { 12, 60, 120 };
        for (var e : sales.entrySet()) {
            String type = e.getKey();
            List<int[]> os = orders.getOrDefault(type, List.of());
            int n = 0, units = 0, same = 0;
            int[] within = new int[horizons.length];
            StringBuilder lags = new StringBuilder();
            for (int[] s : e.getValue()) {
                n++;
                units += s[1];
                int next = -1;
                for (int[] o : os) if (o[0] >= s[0]) { next = o[0]; break; }
                if (next < 0) { lags.append(" m").append(s[0]).append(":never"); continue; }
                int lag = next - s[0];
                if (lag == 0) same++;
                for (int h = 0; h < horizons.length; h++) if (lag <= horizons[h]) within[h]++;
                lags.append(" m").append(s[0]).append(":").append(lag);
            }
            log.printf("TYPE %s (%s): sold %d times (%d units), ordered %d times; next order within 12/60/120 months: %d/%d/%d, same month %d;%s%n",
                    type, sectorOf.get(type), n, units, os.size(), within[0], within[1], within[2], same, lags);
        }
    }
}
