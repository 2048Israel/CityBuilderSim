package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

/**
 * The month reads nothing of the machine it runs on: the same city, played in JVMs set up as opposite machines, comes out the same to the bit.
 *
 * WHY. BA1's notes said "the same code gave another city twice under load": FloatLinkCheck's played town at month 125 instead of 61, SaveFileCheck's
 * at 1,067 people instead of 909. DT1 (2026-11-05) traced it, and it was not the machine: the two odd runs were the only two probe runs started
 * without -Dcbs.earth and -Dcbs.world, in a worktree with no data/ folder, so WorldData.locate() found no world file, the city had no world market
 * (Game.getWorldMarket() null: it trades on the literals) and it was another city. Nothing printed that the file was missing. Rerun with and without
 * the properties, the numbers came back to the digit every time. DT1 also played the model under opposite JVM settings - identity hashes
 * (-XX:hashCode=2), -Xint, C1 only, one processor or thirty-two, a common pool of one, soft references cleared at every collection, the serial
 * collector, an autosave every twelve months against one (the save clock) - and found it bit for bit the same. This turns both findings into the
 * suite's: a harness that holds the month to that, with a control that shows the comparison tells two cities apart and that the world file is an
 * input to the month, and (WorldData.locate()) a notice on stderr when the file is not found, so a run without it says so.
 *
 * WHAT THIS HAS TO PROVE. The parent starts three child JVMs of itself, one after the other (never at once), each plays the same fixtures and writes
 * one fingerprint line a month, to the bit, to a file the parent reads:
 *
 *   A, the machine as AllChecks gives it, on the main thread; B, the opposite machine (identity hash 1 for every object, one processor, a common
 *   pool of one, C1 alone, soft references cleared at every collection, the serial collector, an autosave as often as the city is due one) on a
 *   thread of its own; C, the control, in a folder with no data/ and no world file named.
 *
 *   1. THE FIXTURE CAUSES THE CONDITIONS. The children report what they ran under (hashes, processors, pool, collector, soft reference, C1, the
 *      thread, the autosaves, the world market); a comparison between two machines that were the same machine proves nothing.
 *   2. THE SAME CITY ON BOTH MACHINES, BIT FOR BIT: FloatLinkCheck's played town, 130 months one at a time, and the Earth city
 *      LongPlaytest.earthCity plays (Medellin, 240 months), A's every line equal to B's. A difference names the first month and the first field.
 *   3. A CITY AFTER ANOTHER IN THE SAME JVM: A plays the town twice, the second equal to the first month by month (no state left by the first).
 *   4. THE CONTROL: WITHOUT THE WORLD FILE IT IS ANOTHER CITY. C's town has no world market, says so on stderr (the notice), and parts from A's;
 *      so the world file is an input to the month, and the comparison of 2 and 3 can tell two cities apart. Only where the parent has a world
 *      file to take away.
 */
public class DeterminismCheck {

    static int fails = 0;
    static PrintStream out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-104s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The months FloatLinkCheck's played town is played, one at a time, a fingerprint after each (and one before the first). */
    static final int TOWN_MONTHS = 130;

    /** The months the Earth city is played (LongPlaytest.earthCity); 8 s on the PC. */
    static final int EARTH_MONTHS = 240;

    /** How long a child JVM may run before the check kills it and fails: the slowest (B, on one processor with C1 alone) takes a few minutes. */
    static final long CHILD_MINUTES = 15;

    /** The flags that make a JVM the opposite machine: every identity hash 1, one processor, a common pool of one, C1 alone, soft references cleared at each collection, the serial collector. */
    static final List<String> OPPOSITE_MACHINE = List.of("-XX:+UnlockExperimentalVMOptions", "-XX:hashCode=2", "-XX:ActiveProcessorCount=1",
            "-Djava.util.concurrent.ForkJoinPool.common.parallelism=1", "-XX:TieredStopAtLevel=1", "-XX:SoftRefLRUPolicyMSPerMB=0", "-XX:+UseSerialGC");

    /** The line a child begins its fingerprint file's field names with, and the prefixes of the rest. */
    static final String HEAD = "head", TOWN1 = "town1", TOWN2 = "town2", EARTH = "earth", REPORT = "report";

    /* ================================================================== */

    public static void main(String[] args) throws Exception {
        if (args.length >= 3 && args[0].equals("--child")) {
            child(args[1], Path.of(args[2]));
            return;
        }
        out = System.out;
        long start = System.nanoTime();

        boolean worldHere = false;
        Path world = WorldData.locate();
        if (world != null && Files.isRegularFile(world)) worldHere = true;

        Result a = runChild("A", worldHere);
        Result b = runChild("B", worldHere);
        Result c = worldHere ? runChild("C", true) : null;

        out.println("\n--- 1. the fixture causes the conditions: the machines the children ran on ---");
        theMachines(a, b, worldHere);

        out.println("\n--- 2. the same city on both machines, bit for bit: the played town, " + TOWN_MONTHS + " months, and the Earth city, " + EARTH_MONTHS + " ---");
        sameCity(a, b);

        out.println("\n--- 3. a city after another in the same JVM: A's second town equals its first ---");
        afterAnother(a);

        out.println("\n--- 4. the control: without the world file it is another city ---");
        theControl(a, c, worldHere);

        out.printf("%nDeterminismCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =================================================================
       THE PARENT: three children, one after the other
       ================================================================= */

    /** What a child kept: its report, the field names, and the fingerprint lines of each city. */
    static final class Result {
        final String which;
        boolean ran;
        Path log;
        final Map<String, String> report = new LinkedHashMap<>();
        List<String> head = List.of();
        final List<String[]> town1 = new ArrayList<>(), town2 = new ArrayList<>(), earth = new ArrayList<>();

        Result(String which) { this.which = which; }

        String get(String key) { return report.getOrDefault(key, ""); }

        boolean is(String key) { return get(key).equals("true"); }

        int num(String key) {
            try { return Integer.parseInt(get(key)); } catch (NumberFormatException e) { return -1; }
        }

        void read(Path file) throws IOException {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] f = line.split("\t", -1);
                switch (f[0]) {
                    case HEAD -> head = Arrays.asList(Arrays.copyOfRange(f, 1, f.length));
                    case TOWN1 -> town1.add(Arrays.copyOfRange(f, 1, f.length));
                    case TOWN2 -> town2.add(Arrays.copyOfRange(f, 1, f.length));
                    case EARTH -> earth.add(Arrays.copyOfRange(f, 1, f.length));
                    case REPORT -> report.put(f[1], f.length > 2 ? f[2] : "");
                    default -> { }
                }
            }
        }
    }

    /** A child JVM started as AllChecks starts a harness, set up as machine A, B or C (the header says how); one after the other, never at once. */
    static Result runChild(String which, boolean worldHere) throws Exception {
        boolean control = which.equals("C");
        String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        // The control runs in a folder of its own, so its classpath must not depend on the parent's.
        if (control) classpath = Arrays.stream(classpath.split(File.pathSeparator)).map(e -> Path.of(e).toAbsolutePath().toString()).collect(Collectors.joining(File.pathSeparator));
        List<String> cmd = new ArrayList<>(AllChecks.command(javaExe, classpath, "DeterminismCheck", true));
        int at = cmd.indexOf("-cp");
        List<String> flags = new ArrayList<>();
        if (which.equals("B")) flags.addAll(OPPOSITE_MACHINE);
        for (String key : new String[] { "cbs.earth", WorldData.PROPERTY }) {
            String v = System.getProperty(key);
            if (v == null) continue;
            if (control && key.equals(WorldData.PROPERTY)) continue;   // the control is told no world file
            flags.add("-D" + key + "=" + v);
        }
        cmd.addAll(at, flags);
        Path dir = TempDirs.create("determinism-child-" + which);
        Path log = dir.resolve(which + ".log");
        Path file = dir.resolve(which + ".tsv");
        cmd.addAll(List.of("--child", which, file.toString()));
        out.println("   child " + which + (which.equals("A") ? " (the machine as AllChecks gives it)" : which.equals("B") ? " (the opposite machine)" : " (the control, no world file)") + " ...");
        long t0 = System.nanoTime();
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        // The child's output goes to a file, not through a pipe this thread reads to the end: a read to the end returns only when the child exits,
        // so the wait below could never time out on a child that hangs (as PriceLevelCheck's children).
        pb.redirectOutput(log.toFile());
        if (control) {
            // A folder with no data/ in it, and no option that would hand the child the parent's world file through the environment.
            pb.directory(TempDirs.create("determinism-nodata").toFile());
            pb.environment().remove("JDK_JAVA_OPTIONS");
            pb.environment().remove("JAVA_TOOL_OPTIONS");
        }
        Process p = pb.start();
        Result r = new Result(which);
        r.log = log;
        boolean done = p.waitFor(CHILD_MINUTES, TimeUnit.MINUTES);
        if (!done) {
            p.destroyForcibly();
            p.waitFor(1, TimeUnit.MINUTES);
            check("child " + which + " finishes in " + CHILD_MINUTES + " minutes (it was killed; its output so far: " + log + ")", false);
            return r;
        }
        int exit = p.exitValue();
        if (Files.isRegularFile(file)) r.read(file);
        out.printf("   child %s: exit %d, %d + %d town lines, %d Earth lines, %.0f s (its output: %s)%n", which, exit, r.town1.size(), r.town2.size(), r.earth.size(),
                (System.nanoTime() - t0) / 1e9, log);
        check("child " + which + " exits 0 and leaves its fingerprint file", exit == 0 && !r.town1.isEmpty());
        r.ran = exit == 0 && !r.town1.isEmpty();
        return r;
    }

    /* ------------------------------------------------------------------ */

    /** Section 1: the report of each child says the conditions were there. */
    static void theMachines(Result a, Result b, boolean worldHere) {
        out.println("   A: identity hashes " + a.get("hash1") + ", " + a.get("hash2") + "; " + a.get("processors") + " processors, common pool " + a.get("pool")
                + "; collectors " + a.get("collectors") + "; soft reference kept " + a.get("softKept") + "; C1 alone " + a.get("c1Only")
                + "; autosaves " + a.get("autosaves1") + "; thread " + a.get("thread") + "; world market " + a.get("worldMarket"));
        out.println("   B: identity hashes " + b.get("hash1") + ", " + b.get("hash2") + "; " + b.get("processors") + " processors, common pool " + b.get("pool")
                + "; collectors " + b.get("collectors") + "; soft reference kept " + b.get("softKept") + "; C1 alone " + b.get("c1Only")
                + "; autosaves " + b.get("autosaves1") + "; thread " + b.get("thread") + "; world market " + b.get("worldMarket"));
        check("B's identity hashes are 1 and 1 (-XX:hashCode=2)", b.get("hash1").equals("1") && b.get("hash2").equals("1"));
        check("A's identity hashes are not: two new objects have two hashes", !a.get("hash1").isEmpty() && !a.get("hash1").equals(a.get("hash2")));
        check("B sees one processor and a common pool of one", b.num("processors") == 1 && b.num("pool") == 1);
        int cores = a.num("processors");
        if (cores > 1) {
            check("A sees more than one processor (" + cores + ")", cores > 1);
            if (cores >= 3) check("A's common pool is more than one (" + a.get("pool") + ")", a.num("pool") > 1);
            else out.println("   this machine has " + cores + " processors: A's common pool is " + a.get("pool") + ", not more than one; the pool is not asserted");
            check("A's collector is not the serial one (" + a.get("collectors") + ")", !a.get("collectors").isEmpty() && !a.get("collectors").contains("Copy"));
        } else {
            out.println("   this machine has one processor: A is not the opposite of B in processors, pool or collector; those three are not asserted");
        }
        check("B's collector is the serial one (" + b.get("collectors") + ")", b.get("collectors").contains("Copy") && b.get("collectors").contains("MarkSweepCompact"));
        check("B's soft reference is cleared by collection (the second of two)", b.get("softKept").equals("false"));
        check("A's soft reference is kept through two collections", a.is("softKept"));
        check("B was started with C1 alone (-XX:TieredStopAtLevel=1) and A was not", b.is("c1Only") && a.get("c1Only").equals("false"));
        check("A's town wrote one autosave, in " + TOWN_MONTHS + " months (the save clock never due again)", a.num("autosaves1") == 1);
        int due = TOWN_MONTHS / Game.AUTOSAVE_MONTHS - 1;
        check("B's town wrote at least " + due + " (an autosave as often as one is due, every " + Game.AUTOSAVE_MONTHS + " months): " + b.get("autosaves1"), b.num("autosaves1") >= due);
        check("A's fixtures ran on the main thread (" + a.get("thread") + ")", a.get("thread").equals("main"));
        check("B's fixtures ran on a thread that is not main (" + b.get("thread") + ")", !b.get("thread").isEmpty() && !b.get("thread").equals("main"));
        if (worldHere) check("A's and B's towns had a world market", a.is("worldMarket") && b.is("worldMarket"));
        else out.println("   no world file is named here (-D" + WorldData.PROPERTY + "=<path> or " + WorldData.DATA_FILE + "): section 4 did not run");
    }

    /** Section 2: A's lines equal B's, every month. */
    static void sameCity(Result a, Result b) {
        String diff = firstDifference(a.head, a.town1, b.town1);
        check("A's town and B's are the same city, all " + a.town1.size() + " lines, to the bit" + (diff == null ? "" : ": " + diff), diff == null && a.town1.size() == TOWN_MONTHS + 1);
        if (!a.is("earthRan") && !b.is("earthRan")) {
            out.println("   earth-2.bin not named: the Earth city of section 2 did not run");
            return;
        }
        check("both machines played the Earth city", a.is("earthRan") && b.is("earthRan"));
        diff = firstDifference(a.head, a.earth, b.earth);
        check("A's Earth city and B's are the same city, to the bit" + (diff == null ? "" : ": " + diff), diff == null && !a.earth.isEmpty());
    }

    /** Section 3: A's second town equals its first. */
    static void afterAnother(Result a) {
        String diff = firstDifference(a.head, a.town1, a.town2);
        check("A's second town equals its first, all " + a.town2.size() + " lines" + (diff == null ? "" : ": " + diff), diff == null && a.town2.size() == TOWN_MONTHS + 1);
        check("A's second town wrote one autosave as its first did", a.num("autosaves2") == a.num("autosaves1"));
    }

    /** Section 4: the town without the world file is another city, and says so. */
    static void theControl(Result a, Result c, boolean worldHere) throws IOException {
        if (!worldHere || c == null) {
            out.println("   no world file to take away: the control did not run");
            return;
        }
        check("C's town had no world market", c.report.containsKey("worldMarket") && !c.is("worldMarket"));
        check("C's own notice fired (WorldData.locate() said so once)", c.is("noticeFired"));
        boolean said = false;
        if (c.log != null && Files.isRegularFile(c.log)) {
            for (String line : Files.readAllLines(c.log, StandardCharsets.UTF_8)) if (line.startsWith("world-1.bin not named")) said = true;
        }
        check("C's log has the line that begins \"world-1.bin not named\" (stderr)", said);
        String diff = firstDifference(a.head, a.town1, c.town1);
        check("C's town differs from A's: the world file is an input to the month" + (diff == null ? "" : "; it parts at " + diff), diff != null);
    }

    /** The first month and field two runs' lines part at, with both values; null if they are the same lines. */
    static String firstDifference(List<String> head, List<String[]> x, List<String[]> y) {
        int n = Math.min(x.size(), y.size());
        for (int i = 0; i < n; i++) {
            String[] p = x.get(i), q = y.get(i);
            for (int f = 0; f < Math.max(p.length, q.length); f++) {
                String u = f < p.length ? p[f] : "(none)", v = f < q.length ? q[f] : "(none)";
                if (u.equals(v)) continue;
                String name = f < head.size() ? head.get(f) : "field " + f;
                String month = p.length > 0 ? p[0] : "?";
                return "line " + i + " (month " + month + "), " + name + ": " + show(u) + " against " + show(v);
            }
        }
        if (x.size() != y.size()) return x.size() + " lines against " + y.size();
        return null;
    }

    /** A hexadecimal double with its decimal beside it. */
    static String show(String s) {
        if (!s.startsWith("0x") && !s.startsWith("-0x")) return s;
        try { return s + " (" + Double.parseDouble(s) + ")"; } catch (NumberFormatException e) { return s; }
    }

    /* =================================================================
       THE CHILD: play the fixtures, write the fingerprints and the report
       ================================================================= */

    /** What one town gave: the field names, a line a month, how many autosaves it printed, whether it had a world market. */
    record Town(List<String> head, List<String> lines, int autosaves, boolean worldMarket) { }

    static void child(String which, Path outFile) throws Exception {
        List<String> file = new ArrayList<>();
        Map<String, String> report = new LinkedHashMap<>();
        Throwable[] died = new Throwable[1];
        Runnable job = () -> {
            try { fixtures(which, file, report); } catch (Throwable t) { died[0] = t; }
        };
        if (which.equals("B")) {
            Thread t = new Thread(job, "determinism-B-fixtures");
            t.start();
            t.join();
        } else {
            job.run();
        }
        // What the JVM is, read after the fixtures so a soft reference has had a collection or more to go by.
        Object o1 = new Object(), o2 = new Object();
        report.put("hash1", Integer.toString(System.identityHashCode(o1)));
        report.put("hash2", Integer.toString(System.identityHashCode(o2)));
        report.put("processors", Integer.toString(Runtime.getRuntime().availableProcessors()));
        report.put("pool", Integer.toString(java.util.concurrent.ForkJoinPool.getCommonPoolParallelism()));
        report.put("collectors", ManagementFactory.getGarbageCollectorMXBeans().stream().map(g -> g.getName()).collect(Collectors.joining(",")));
        // Two collections: the first only advances the VM's soft-reference clock (a reference made since the last collection is "recent" to it, whatever
        // SoftRefLRUPolicyMSPerMB says); at the second, with 0 ms a megabyte, B clears it and A, with a second a megabyte of the free heap, keeps it.
        SoftReference<byte[]> soft = new SoftReference<>(new byte[1 << 20]);
        System.gc();
        System.gc();
        report.put("softKept", Boolean.toString(soft.get() != null));
        report.put("c1Only", Boolean.toString(ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:TieredStopAtLevel=1")));
        report.put("noticeFired", Boolean.toString(WorldData.NOT_NAMED_SAID.get()));
        for (Map.Entry<String, String> e : report.entrySet()) file.add(REPORT + "\t" + e.getKey() + "\t" + e.getValue());
        Files.write(outFile, file, StandardCharsets.UTF_8);
        if (died[0] != null) {
            died[0].printStackTrace();
            System.exit(2);
        }
        System.exit(0);
    }

    /** The fixtures of one child: the town (twice in A), then the Earth city (A and B, where the Earth file is named). */
    static void fixtures(String which, List<String> file, Map<String, String> report) throws Exception {
        report.put("thread", Thread.currentThread().getName());
        // A: never due again after the first; B: always due; the control, like A.
        LongSupplier clock;
        if (which.equals("B")) {
            long[] tick = new long[1];
            clock = () -> (tick[0] += 1_000_000_000_000L);
        } else {
            clock = () -> 0L;
        }
        Town one = playTown("determinism-" + which + "-1", clock);
        file.add(HEAD + "\t" + String.join("\t", one.head));
        for (String line : one.lines) file.add(TOWN1 + "\t" + line);
        report.put("autosaves1", Integer.toString(one.autosaves));
        report.put("worldMarket", Boolean.toString(one.worldMarket));
        if (which.equals("A")) {
            Town two = playTown("determinism-A-2", () -> 0L);
            for (String line : two.lines) file.add(TOWN2 + "\t" + line);
            report.put("autosaves2", Integer.toString(two.autosaves));
        }
        boolean earthHere = !which.equals("C") && EarthSite.gameData().here();
        report.put("earthRan", Boolean.toString(earthHere));
        if (earthHere) {
            PrintStream real = System.out;
            PrintStream silent = new PrintStream(OutputStream.nullOutputStream());
            try {
                LongPlaytest.findings.clear();
                Game g = LongPlaytest.earthCity("medellin", EARTH_MONTHS, GameFiles.scratch("determinism-" + which + "-earth"), silent);
                System.setOut(real);
                g.awaitSaves();
                file.add(EARTH + "\t" + String.join("\t", fingerprint(g).values()));
            } finally {
                System.setOut(real);
            }
        }
    }

    /** FloatLinkCheck's played town with the firms free to order (DT1's probe, shape freestep), played TOWN_MONTHS months one at a time. */
    static Town playTown(String label, LongSupplier clock) {
        List<String> lines = new ArrayList<>();
        List<String> head;
        int[] autosaves = new int[1];
        GameFiles files = GameFiles.scratch(label);
        Game city = new Game(files);
        city.useSaveClockForChecks(clock);
        city.holdCausesOfDeathOff();
        PrintStream real = System.out;
        PrintStream counting = new PrintStream(OutputStream.nullOutputStream()) {
            @Override public void println(String x) { if (x != null && x.contains("Autosave")) autosaves[0]++; }
        };
        System.setOut(counting);
        try {
            city.run();
            city.getEconomyManager().getTaxPolicy().reset();
            city.getEconomyManager().getTaxPolicy().openAll();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.buildStack(FloatLinkCheck.template(city, "Gravel Road"), 6, true);
            city.buildStack(FloatLinkCheck.template(city, "Construction Depot"), 2, true);
            city.buildStack(FloatLinkCheck.template(city, "House"), 300, false);
            city.buildStack(FloatLinkCheck.template(city, "Convenience Store"), 6, false);
            city.buildStack(FloatLinkCheck.template(city, "Industrial Bakery"), 2, false);
            city.buildStack(FloatLinkCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(FloatLinkCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(FloatLinkCheck.template(city, "Commercial Bank"), 1, false);
            Map<String, String> first = fingerprint(city);
            head = new ArrayList<>(first.keySet());
            lines.add(String.join("\t", first.values()));
            for (int i = 0; i < TOWN_MONTHS; i++) {
                city.simulateMonths(1);
                lines.add(String.join("\t", fingerprint(city).values()));
            }
        } finally {
            System.setOut(real);
        }
        city.awaitSaves();
        return new Town(head, lines, autosaves[0], city.getWorldMarket() != null);
    }

    /**
     * What a city is, in one line a month, to the bit: the month, the people, the city's cash, the bank's cash and equity, what the households have
     * banked, the price index, the city's debt outstanding, the stacks standing and those under construction, and every sector's cash in Sectors.KEYS
     * order. Only getters that return a field or sum fields; nothing here moves the city.
     */
    static Map<String, String> fingerprint(Game c) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("month", Integer.toString(c.getMonth()));
        m.put("population", Long.toString(c.getPopulationManager().getPopulation()));
        m.put("cash", hex(c.getCash()));
        m.put("bankCash", hex(c.getBank().getCash()));
        m.put("bankEquity", hex(c.getBank().equity()));
        m.put("householdDeposits", hex(c.getBank().getHouseholdDeposits()));
        m.put("priceIndex", hex(c.getPriceIndex().getIndex()));
        m.put("debtOutstanding", hex(c.getDebtManager().getAllPrincipal()));
        m.put("stacks", Integer.toString(c.getBuildingManager().getStackCount()));
        m.put("underConstruction", Integer.toString(c.getBuildingManager().getUnderConstruction()));
        for (Sector s : c.getSectors().all()) m.put("cash:" + s.key(), hex(s.getCash()));
        return m;
    }

    static String hex(double x) { return Double.toHexString(x); }
}
