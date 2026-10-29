package ham.citybuildersim;

import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 * Scratch, not in the build (batch MF1, item 6, FX7's star 4): the default playtest with a line for each month the railway's
 * yards were taken back or laid (KEEP / TAKEN / LAID, from CityRuns), and beside them the model's own side - the Rail Depots
 * standing, on order, the Rail Terminals, and each Rail building the city retired - which is where the relays come from: the
 * Freight sector sells its only Rail Depot and orders another, and the map follows the count (m1782 ... m4000). It needs
 * scratch copies of two classes, compiled ahead of target/classes: LongPlaytest with the FX7 hook (`static
 * java.util.function.Consumer<Game> probeHook;` after `static boolean layoutOn;`, and `if (probeHook != null)
 * probeHook.accept(g);` after `if (layoutOn) layoutMonth(g);`), and CityRuns with `public static StringBuilder TRACE;` that
 * keepYards() fills with a KEEP line and a TAKEN line for each yard it takes up, and addYard() with a LAID line. With
 * -Dprobe.snap=N it also lists the yards standing every N months. After MF1 the patched CityRuns also traces an IDLE line each time the
 * set of idle yards changes (the file with the fix is patched the same way: TAKEN in takeYards(), LAID in addYard(), IDLE in keepYards()).
 *   java -cp "<scratch classes>;target/classes;<jars>" ham.citybuildersim.MF1YardProbe <out.txt>
 */
public class MF1YardProbe {
    static PrintStream log;
    static int snapEvery = Integer.getInteger("probe.snap", 0);
    static int months, taken, laid;
    static java.util.Map<String, Integer> relays = new java.util.TreeMap<>();
    static java.util.Map<String, Integer> laidTimes = new java.util.TreeMap<>();

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        CityRuns.TRACE = new StringBuilder();
        LongPlaytest.probeHook = MF1YardProbe::month;
        LongPlaytest.main(new String[0]);
        log.println("TOTAL months watched " + months + ", yards taken up " + taken + ", laid " + laid);
        for (var e : laidTimes.entrySet()) log.println("box " + e.getKey() + " laid " + e.getValue() + " times, taken up " + relays.getOrDefault(e.getKey(), 0));
    }

    static String sequence(Game g, CityMap m) throws Exception {
        Method hs = CityMap.class.getDeclaredMethod("haveSequence");
        hs.setAccessible(true);
        int[] seq = (int[]) hs.invoke(m);
        StringBuilder sb = new StringBuilder();
        int run = 0, last = -2;
        for (int i = 0; i <= seq.length; i++) {
            int t = i < seq.length ? seq[i] : -3;
            if (t == last) { run++; continue; }
            if (run > 0) sb.append(' ').append(last < 0 ? "?" : LongPlaytest.nameOf(g, last)).append('*').append(run);
            last = t;
            run = 1;
        }
        return sb.toString().trim();
    }

    static int lastQty = -1, lastOrd = -1;
    static java.util.Set<String> seen = new java.util.HashSet<>();

    /** The model's own side of the depot: its count standing, its orders on site, and each retirement of a Rail building (the log keeps every entry). */
    static void model(Game g, int month) {
        BuildingManager bm = g.getBuildingManager();
        int qty = bm.getQuantity(BuildingVisual.RAIL_DEPOT), ord = bm.getUnderConstructionById()[BuildingVisual.RAIL_DEPOT];
        int terminals = bm.getQuantity(64);
        java.util.List<DemolitionLog.Entry> all = g.getDemolitionLog().all();
        StringBuilder sb = new StringBuilder();
        for (DemolitionLog.Entry e : all) {
            if (!e.building.contains("Rail") || e.month < month - 1 || !seen.add(e.month + "|" + e.building + "|" + e.quantity)) continue;
            sb.append(" [").append(e.building).append(" x").append(e.quantity).append(" ").append(e.sector).append(" m").append(e.month).append("]");
        }
        if (qty != lastQty || ord != lastOrd || sb.length() > 0) {
            Sector fr = g.getSectors().byKey(Sectors.FREIGHT);
            double[] dc = fr.retirementDemandAndCapacity(g);
            log.println("MODEL m" + month + ": Rail Depots standing " + qty + ", on order " + ord + ", Rail Terminals " + terminals + ", Spurs " + bm.getQuantity(62) + ", Lines " + bm.getQuantity(63)
                    + (sb.length() > 0 ? String.format(", retired:%s (tonnes asked %,.0f, capacity %,.0f, cash %,.0f)", sb, dc[0], dc[1], fr.getCash()) : "") + "; Freight: " + g.getLastInvestment("Freight"));
            lastQty = qty;
            lastOrd = ord;
        }
    }

    static long lastNet = -1;
    static boolean lastStarted;

    /** The railway net's plots month by month: when it moves, and whether it has been reset (started false). */
    static void net(CityMap m, int month) {
        long plots = m.runs().rail().plots();
        boolean started = m.runs().rail().started;
        if (plots != lastNet || started != lastStarted) {
            log.println("NET m" + month + ": railway plots " + lastNet + " -> " + plots + ", started " + started + ", arms " + m.runs().rail().arms());
            lastNet = plots;
            lastStarted = started;
        }
    }

    static void month(Game g) {
        int month = g.getMonth();
        try {
            months++;
            model(g, month);
            net(g.getCityMap(), month);
            String ev = CityRuns.TRACE.toString();
            CityRuns.TRACE.setLength(0);
            CityMap m = g.getCityMap();
            if (!ev.isEmpty()) {
                for (String line : ev.split("\n")) {
                    if (line.contains("TAKEN")) {
                        taken++;
                        String box = line.substring(line.indexOf("box ") + 4, line.indexOf(" asked"));
                        relays.merge(box, 1, Integer::sum);
                    } else if (line.contains("LAID")) {
                        laid++;
                        String box = line.substring(line.indexOf("box ") + 4, line.indexOf(" asked"));
                        laidTimes.merge(box, 1, Integer::sum);
                    }
                }
                log.println("m" + month + ": yards " + m.runs().yards().size() + " (active " + m.runs().yardsActive() + "), short " + m.runs().yardsShort() + ", the model's sequence: " + sequence(g, m));
                log.print(ev);
            }
            if (snapEvery > 0 && month % snapEvery == 0) {
                StringBuilder sb = new StringBuilder("m" + month + " yards:");
                for (int i = 0; i < m.runs().yards().size(); i++) {
                    long[] y = m.runs().yards().get(i);
                    int[] a = m.runs().yardAsked(i);
                    sb.append(' ').append(i).append(':').append(java.util.Arrays.toString(y)).append("/").append(a[0]).append('x').append(a[1]);
                }
                log.println(sb);
            }
        } catch (Exception e) {
            log.println("m" + month + " threw " + e);
            e.printStackTrace(log);
        }
    }
}
