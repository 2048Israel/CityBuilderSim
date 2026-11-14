package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Runs every harness, one JVM each, and says which failed.
 *
 * WHY: each harness calls System.exit(), so it needs its own JVM. Hand-running
 * the growing list left checks unrun. GF1 bounds each child's life after CD9's
 * JavaFX thread survived an OutOfMemoryError for 62 minutes.
 *
 * -q prints verdicts, -f only failures, keeping whole logs in target/checks.
 * Other arguments select by substring, --timing selects Timing.HARNESSES.
 * The exit counts failed harnesses. JavaFX checks skip without its classpath.
 * -f tags each red against the same record as batch-check.sh (FW7), prints at
 * every failing assertion so the judge sees every red. Timing properties are forwarded (TM1).
 * Logged soft runs share PARALLEL slots, memory-gated and heap-bounded (SP1),
 * hard timing and plain output run serially. TIMES keeps each run's duration.
 */
public class AllChecks {

    /** Every harness: the gate and nightly selections together cover this registry. */
    static final String[] HARNESSES = {
        "BuildingDataCheck", "BrandingCheck", "CreditsCheck", "MoneySignCheck", "NewGameCheck", "WorldCheck", "GridCheck", "CalendarCheck", "BooksCheck", "WaterCheck", "HydroCheck",
        "EarthCheck", "WorldDataCheck", "NationsCheck", "WorldMeasuresCheck", "RankingsCheck", "WorldMarketCheck", "WorldMoneysCheck", "RealDollarCheck", "PartnersCheck", "VaultMoneysCheck", "PegAnchorCheck", "InterestAbroadCheck", "GoldPriceCheck", "GoldStandardCheck", "EarthTerrainCheck", "EarthFoundingCheck", "EarthDepositsCheck", "GlobeCheck",
        "PolicyCheck", "PolicyDatesCheck", "LandCheck", "SlopeCheck", "LandClassCheck", "LandmassCheck", "LandNationsCheck", "BridgeCheck", "RunWorksCheck", "ZoningCheck", "ZoningViewCheck", "MiningCheck", "InvestCheck", "CreditCheck", "MortgageCheck",
        "ConstructionControlCheck", "DeathCareFirstCheck", "BuildersFirstCheck", "ConstructionTrapCheck", "FoundingYardCheck", "BuildAdviceCheck", "BuildCardCheck", "RoadCheck", "ChildcareCheck", "AutoBuildCheck", "EssentialWorksCheck", "IslandLinkCheck", "SectorFlowCheck", "PolicyPreviewCheck",
        "RestructureCheck", "BuybackCheck", "CallCheck", "FloatLinkCheck", "DeskCheck", "GdpCheck", "HistoryCheck",
        "BankCheck", "ForeignCheck", "CurrencyCheck", "ExchangeRateCheck", "InterventionCheck", "CurrencyRegimeCheck", "PassThroughCheck", "ForeignDebtCheck", "DebtExchangeCheck", "DebtMoneysCheck", "CapitalFlowCheck", "CarryTradeCheck", "EquityCheck", "ExchangeCheck", "ShortSellingCheck", "MarketCheck", "MonetaryCheck", "ExpectationsCheck", "CurveCheck", "GroceryCheck", "SupplierCreditCheck", "CentralBankCheck", "FundCheck", "FundLedgerCheck", "HoldersCheck", "BondCheck", "OrderBookCheck",
        "DenominationCheck", "PriceLevelCheck", "DeterminismCheck",
        "HouseholdCheck", "PopulationCheck", "LabourCheck", "EducationCheck", "HealthCheck",
        "InfrastructureCheck", "ReadPathCheck", "RobustnessCheck", "SaveFileCheck", "ReloadCheck",
        "SaveSlotCheck", "ScaleCheck", "MapCheck", "GatewayCheck", "MainLineCheck", "RailDrawCheck", "JunctionCheck", "PlanCheck", "OrderSearchCheck", "SkipReportCheck", "InboxCheck", "PlaytestFixCheck", "BuildMenuCheck", "KeptCheck", "ChartFitCheck", "ToastCheck", "MoneyWordsCheck", "ClockCheck",
        "ConsumptionCheck", "SectorStatementCheck", "TreasuryCheck", "HousingCheck", "OutsideCheck", "SicknessCheck", "CausesOfDeathCheck", "HouseholdMemoryCheck", "DeathRecordCheck", "CrimeCheck", "BusinessServicesCheck", "ManufacturingCheck", "AgricultureCheck", "FoodProcessingCheck", "TradeCostCheck", "RailCheck", "RailDepotCheck", "HaulageCheck", "CarCheck", "VanCheck", "RestaurantsCheck", "OilCheck", "RefineryCheck", "RefineryViewCheck", "OilViewCheck", "WellCheck", "PortCheck", "QuayRailCheck", "QuayCraneCheck", "LandlordPortCheck", "HomeRailCheck", "FreightViewCheck", "TradeBandCheck", "MaterialsCheck", "CoalMiningCheck", "QuarryingCheck", "CementConcreteCheck", "CopperMiningCheck", "CopperRefiningCheck", "ForestryCheck", "WoodProductsCheck", "IndustryViewCheck", "YearBookCheck", "ChartCheck", "TimingCheck", "ObservationCheck", "StaleCheck", "KnownRedsCheck", "EnsembleCheck", "TruckRouteCheck", "AuctionCheck", "LongPlaytest",
        "IslandNetCheck", "IslandRailCheck", "CrossingCheck", "BridgeMapCheck", "WantedPostsCheck", "PartnerCallsCheck", "Forms1900Check", "EquipmentCheck", "FittingCheck", "RecipeCheck", "ResearchCheck", "DiscoveryCheck", "PrizeCheck", "ResearchPreviewCheck", "ResearchViewCheck", "WageLadderCheck", "ArrivalMixCheck", "BasketYearCheck",
        "ReplayCheck", "StaffingDrawCheck", "OwnerCheck",
        "GovernmentViewCheck", "GovernmentPlayerCheck"
    };

    /** How many harnesses a soft, logged run runs at once (SP1); memory, not the cores, bounds it. */
    static final int PARALLEL = 3;

    /** The property that sets it for one run: -Dcbs.parallel=1 runs them one at a time (given on the java command line or in JDK_JAVA_OPTIONS). */
    static final String PARALLEL_PROPERTY = "cbs.parallel";

    /** The most heap a harness JVM may take, its -Xmx (SP1): measured 2026-10-27 under a 4 GB ceiling, no harness committed more than 1.5 GB (SaveSlotCheck) or kept more than 0.6 GB after a collection (MapCheck), so 2 GB changes no harness's run and stops one taking the 8 GB a JVM was allowed. */
    static final int HEAP_MB = 2048;

    /** The heap a harness JVM starts with, its -Xms (SP1): the JVM's default is a 64th of the RAM, 508 MB committed at once whether used or not (measured 2026-10-27: BooksCheck 616 MB of commit by default, 167 MB with 64). */
    static final int HEAP_START_MB = 64;

    /** A harness gets four times KeptCheck's usual 435 seconds before its process tree is ended (GF1, CD9's hung FX thread). */
    static final int HARNESS_SECONDS = 4 * 435;

    /** No harness is started beside another of this run while the machine's commit charge is over this percent (SP1): the first three, started at 79.8%, took the PC to 85.9% within a minute as they grew (2026-10-27), and the batches stop a run at 85; a suite died with CreateProcess error 1450 at 43 of 54 GB on 2026-10-21. */
    static final int COMMIT_PCT = 75;

    /** Longest first when no previous run's seconds can be read (HA1, 2026-11-12 medians). */
    static final List<String> LONGEST_FIRST = List.of("KeptCheck", "CentralBankCheck", "MapCheck", "GlobeCheck", "ShortSellingCheck", "LandmassCheck", "LongPlaytest", "EarthTerrainCheck");

    /** Runs nightly, and at the gate only when a direct pick names it (HA1). */
    static final List<String> NIGHTLY = List.of(
            "KeptCheck", "GlobeCheck", "ShortSellingCheck", "LandmassCheck", "EducationCheck",
            "OrderSearchCheck", "MainLineCheck", "BridgeMapCheck", "AuctionCheck", "GatewayCheck",
            "HomeRailCheck", "TruckRouteCheck", "PrizeCheck", "IslandLinkCheck", "CopperRefiningCheck");

    /** Where -q and -f keep each harness's whole output. */
    static final Path LOGS = Paths.get("target", "checks");

    /** Where -q and -f write each harness's seconds and exit, a line each, longest first (GT1: no gate said where its forty minutes went). */
    static final Path TIMES = LOGS.resolve("times.tsv");

    /** A harness's section title, "--- what it proves ---". */
    static final Pattern SECTION = Pattern.compile("^\\s*---\\s.*\\s---\\s*$");

    /** A harness's closing count, "N FAILED": printed, but not under the last section's title. */
    static final Pattern SUMMARY = Pattern.compile("^\\s*\\d+ FAILED\\s*$");

    /** A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. */
    static final Pattern FAILING = Pattern.compile("\\bFAIL(ED)?\\b|Exception|^\\S*Error\\b|^Caused by");

    /** The line failingLines prints when it left failing lines out: "... 52 failing lines in all: <log>". */
    static final Pattern IN_ALL = Pattern.compile("^\\.\\.\\. [0-9]+ failing lines in all: ");

    /** Main's recorded reds, one line each after the "#" lines: what ship.sh writes at every gate. */
    static final Path GATE = Paths.get("docs", "notes", "gate.txt");

    /** The system property that names the record when it is not GATE; batch-check.sh gives it the record it judges against. */
    static final String KNOWN_PROPERTY = "cbs.knownReds";

    /** The line a log says before the last lines of a harness that died with no failing line of its own. */
    static final String NO_FAIL_LINE = "(no FAIL line; its last lines:)";

    /** The tag -f puts before a failing line that is in the record; the same width as NEW_TAG. */
    static final String KNOWN_TAG = "[known] ";

    /** The tag -f puts before a failing line that is not in the record. */
    static final String NEW_TAG = "[NEW]   ";

    public static void main(String[] args) throws Exception {
        boolean quiet = false, failuresOnly = false, timingOnly = false, gate = false, nightly = false, observations = false;
        List<String> only = new ArrayList<>();
        for (String a : args) {
            if (a.equals("-q")) quiet = true;
            else if (a.equals("-f")) failuresOnly = true;
            else if (a.equals("--timing")) timingOnly = true;
            else if (a.equals("--gate")) gate = true;
            else if (a.equals("--nightly")) nightly = true;
            else if (a.equals("--observations")) observations = true;
            else only.add(a.toLowerCase());
        }
        boolean logged = quiet || failuresOnly;   // both keep each harness's output in LOGS, so a verdict can be read from it
        if (logged) Files.createDirectories(LOGS);
        Path record = recordFile();
        Set<String> known = logged ? knownReds(record) : null;

        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        boolean haveFx = classpath.toLowerCase().contains("javafx");

        int failed = 0, ran = 0, soft = 0, observed = 0;
        List<String> times = new ArrayList<>();   // "seconds<TAB>name<TAB>exit", for TIMES
        List<String> softIn = new ArrayList<>(), observedIn = new ArrayList<>();
        List<String> knownOnly = new ArrayList<>(), newRed = new ArrayList<>();
        long started = System.currentTimeMillis();
        List<String> picked = new ArrayList<>(), order = new ArrayList<>();   // asked for, in HARNESSES order; and what starts, in the order it starts
        for (String name : HARNESSES) {
            if (observations && !Observation.HARNESSES.contains(name) && (!nightly || !NIGHTLY.contains(name))) continue;
            if (!selected(name, timingOnly, gate, nightly && !observations, only)) continue;
            picked.add(name);
            if (haveFx || !(name.equals("BuildMenuCheck") || name.equals("KeptCheck") || name.equals("ChartFitCheck") || name.equals("ToastCheck") || name.equals("MoneyWordsCheck"))) order.add(name);
        }
        int atOnce = parallel(logged, timingOnly);
        if (atOnce > 1) order.sort(longestFirst(TIMES, Path.of(System.getProperty("user.home"), ".cbs-gate-times.tsv")));
        Set<Process> live = ConcurrentHashMap.newKeySet();   // what runs, and the lock a start waits on for the commit charge to fall
        AtomicInteger held = new AtomicInteger();   // starts that waited at least once
        Runtime.getRuntime().addShutdownHook(new Thread(() -> live.forEach(AllChecks::endTree)));
        ExecutorService pool = Executors.newFixedThreadPool(atOnce, r -> { Thread th = new Thread(r); th.setDaemon(true); return th; });
        Map<String, Future<double[]>> runs = new HashMap<>();   // each: its exit code and its own seconds, from its start
        for (String name : order) {
            runs.put(name, pool.submit(() -> {
                ProcessBuilder pb = new ProcessBuilder(command(java, classpath, name, logged));
                pb.redirectErrorStream(true);
                if (logged) pb.redirectOutput(LOGS.resolve(name + ".log").toFile());
                else pb.inheritIO();
                Process p;
                long t;
                synchronized (live) {   // look and start under one lock, so two starts never pass on one reading; a run with nothing running always starts
                    boolean waited = false;
                    while (!live.isEmpty() && commitPct() > COMMIT_PCT) { waited = true; live.wait(2000); }
                    if (waited) held.incrementAndGet();
                    p = pb.start();
                    t = System.currentTimeMillis();
                    live.add(p);
                }
                try {
                    return new double[] {waitForHarness(p, HARNESS_SECONDS, logged ? LOGS.resolve(name + ".log") : null),
                            (System.currentTimeMillis() - t) / 1000.0};
                } finally {
                    if (p.isAlive()) endTree(p);
                    synchronized (live) { live.remove(p); live.notifyAll(); }
                }
            }));
        }
        for (String name : picked) {
            Future<double[]> run = runs.get(name);
            if (run == null) {
                System.out.printf("%-24s skipped (needs JavaFX on the classpath)%n", name);
                continue;
            }
            Path log = LOGS.resolve(name + ".log");
            int code = (int) run.get()[0];
            double seconds = run.get()[1];
            ran++;
            times.add(String.format(Locale.ROOT, "%.1f	%s	%d", seconds, name, code));
            if (code != 0) failed++;
            if (logged) {
                int n = softLines(log);
                if (n > 0) { soft += n; softIn.add(name + " " + n); }
                List<String> readings = observationLines(log);
                int misses = (int) readings.stream().filter(line -> line.startsWith("OBSERVED - ")).count();
                if (misses > 0) { observed += misses; observedIn.add(name + " " + misses); }
                for (String line : readings) System.out.println(name + " " + line);
            }
            if (code == 0) {
                if (!failuresOnly) System.out.println(header(name, code, seconds));
                continue;
            }
            if (!logged) {   // plain mode: the header, nothing read
                System.out.println(header(name, code, seconds));
                continue;
            }
            List<String> printed;
            try {
                printed = failingLines(log);
            } catch (IOException e) {   // the one line said instead is a red line of its own, never in the record: a NEW red
                printed = List.of("(its log could not be read: " + e + "; it is " + log + ")");
            }
            for (String line : report(name, code, seconds, printed, failuresOnly, known)) System.out.println(line);
            if (known != null) {
                if (newCount(redLines(name, code, printed), known) == 0) knownOnly.add(name);
                else newRed.add(name);
            }
        }
        pool.shutdown();
        if (logged) {
            times.sort(Comparator.comparingDouble((String x) -> Double.parseDouble(x.substring(0, x.indexOf('	')))).reversed());
            Files.write(TIMES, times, StandardCharsets.UTF_8);
        }
        System.out.println();
        System.out.println(summaryLine(ran, failed, (System.currentTimeMillis() - started) / 1000.0));
        if (held.get() > 0) System.out.printf("memory: %d start(s) held until the commit charge fell under %d%% (AllChecks.COMMIT_PCT)%n", held.get(), COMMIT_PCT);
        if (soft > 0) System.out.printf("%d timing assertion%s SOFT, not counted (%s): %s%n",
                soft, soft == 1 ? "" : "s", Timing.forwarded(), String.join(", ", softIn));
        if (observed > 0) System.out.printf("%d path assertion%s OBSERVED, not counted: %s%n",
                observed, observed == 1 ? "" : "s", String.join(", ", observedIn));
        if (logged && failed > 0) {
            System.out.println(againstLine(record, known, knownOnly, newRed));
            System.out.println("Whole output of each: " + LOGS + File.separator + "NAME.log");
        }
        System.exit(failed);
    }

    /** Gate defaults exclude the nightly tier; only exact direct picks add its members. */
    static boolean selected(String name, boolean timing, boolean gate, boolean nightly, List<String> only) {
        if (timing && !Timing.HARNESSES.contains(name) || nightly && !NIGHTLY.contains(name)) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        if (gate) return !NIGHTLY.contains(name) || only.contains(lower);
        return only.isEmpty() || only.stream().anyMatch(lower::contains);
    }

    /** Previous local seconds, else the last gate's; malformed rows cannot stop a suite. */
    static Comparator<String> longestFirst(Path local, Path gate) {
        Map<String, Double> seconds = new HashMap<>();
        for (Path file : List.of(local, gate)) {
            try {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String[] row = line.split("\t");
                    if (row.length != 3) continue;
                    try {
                        double value = Double.parseDouble(row[0]);
                        if (Double.isFinite(value) && value >= 0) seconds.put(row[1], value);
                    } catch (NumberFormatException ignored) { }
                }
            } catch (IOException ignored) { }
            if (!seconds.isEmpty()) break;
        }
        return Comparator.<String>comparingDouble(n -> seconds.getOrDefault(n, -1.0)).reversed()
                .thenComparingInt(n -> LONGEST_FIRST.contains(n) ? LONGEST_FIRST.indexOf(n) : LONGEST_FIRST.size());
    }

    /** Waits at most the harness's limit, recording a timeout as a failing assertion (GF1). */
    static int waitForHarness(Process process, int seconds, Path log) throws InterruptedException, IOException {
        if (process.waitFor(seconds, TimeUnit.SECONDS)) return process.exitValue();
        endTree(process);
        String line = "FAIL: timed out after " + seconds + " s" + System.lineSeparator();
        if (log == null) System.out.print(line);
        else Files.writeString(log, line, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
        return 1;
    }

    /** Ends the descendants while their parent still owns them, then the harness itself (GF1, KD1 item 1). */
    static void endTree(Process process) {
        List<ProcessHandle> descendants = process.descendants().toList();
        for (ProcessHandle child : descendants.reversed()) child.destroyForcibly();
        process.destroyForcibly();
        for (ProcessHandle child : descendants) child.onExit().completeOnTimeout(null, 5, TimeUnit.SECONDS).join();
        process.onExit().completeOnTimeout(null, 5, TimeUnit.SECONDS).join();
    }

    /**
     * How many harnesses this run runs at once (SP1): one when its output is
     * not logged (plain mode inherits it, and would interleave), for --timing,
     * and when timing is hard; else the property or PARALLEL, at most half the
     * cores and at least one.
     */
    static int parallel(boolean logged, boolean timingOnly) {
        if (!logged || timingOnly || !Timing.soft()) return 1;
        int n = PARALLEL;
        try {
            int named = Integer.parseInt(System.getProperty(PARALLEL_PROPERTY, "").strip());
            if (named > 0) n = named;
        } catch (NumberFormatException e) {   // not a number: PARALLEL
        }
        return Math.max(1, Math.min(n, Runtime.getRuntime().availableProcessors() / 2));
    }

    /**
     * The machine's commit charge in percent (SP1): on Windows the commit limit
     * and what is left of it, as Task Manager shows it; elsewhere swap, which
     * only a starved machine fills. 0 when there is no figure.
     */
    static double commitPct() {
        if (!(ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean os)) return 0;
        long total = os.getTotalSwapSpaceSize();
        return total <= 0 ? 0 : 100.0 * (total - os.getFreeSwapSpaceSize()) / total;
    }

    /** The first line of a harness's entry: its name, its verdict as a number or OK, and how long it took. */
    static String header(String name, int code, double seconds) {
        return String.format("%-24s %s  (%.1fs)", name, code == 0 ? "OK" : "FAILED - exit " + code, seconds);
    }

    /** The line under the last harness: how many ran and how they ended. */
    static String summaryLine(int ran, int failed, double seconds) {
        return String.format("%d harness%s run in %.0fs: %d passed, %d failed",
                ran, ran == 1 ? "" : "es", seconds, ran - failed, failed);
    }

    /** The named combined record, or the default gate and repository parts. */
    static Path recordFile() {
        String named = System.getProperty(KNOWN_PROPERTY);
        return named == null || named.isBlank() ? GATE : Paths.get(named);
    }

    /** Reads the record and adds repository parts when the default gate was requested. */
    static Set<String> knownReds(Path file) throws IOException {
        Set<String> known = KnownReds.read(file);
        if (file.equals(GATE)) {
            Set<String> parts = KnownReds.read(KnownReds.PARTS);
            if (parts != null) {
                if (known == null) known = new LinkedHashSet<>();
                known.addAll(parts);
            }
        }
        return known;
    }

    /** How a red line is cut of its blanks: batch-check.sh's reds_of strips spaces and tabs from the front and spaces, tabs and returns from the back. */
    private static String trimBlanks(String s) {
        int from = 0, to = s.length();
        while (from < to && (s.charAt(from) == ' ' || s.charAt(from) == '\t')) from++;
        while (to > from && (s.charAt(to - 1) == ' ' || s.charAt(to - 1) == '\t' || s.charAt(to - 1) == '\r')) to--;
        return s.substring(from, to);
    }

    /** Maps printed lines to reds, preserving section and diagnostic-tail boundaries. */
    private static String[] redOf(String name, List<String> printed) {
        String[] red = new String[printed.size()];
        boolean noFailLine = false;
        for (int i = 0; i < red.length; i++) {
            if (noFailLine) break;
            String p = printed.get(i);
            if (SECTION.matcher(p).matches()) continue;
            String t = trimBlanks(p);
            if (t.equals(NO_FAIL_LINE)) { red[i] = name + " | (no FAIL line)"; noFailLine = true; continue; }
            if (IN_ALL.matcher(t).find()) t = t.substring(0, t.indexOf(": "));   // the log's path differs by machine
            if (!t.isEmpty()) red[i] = name + " | " + t;
        }
        return red;
    }

    /** The exit and failing lines of one harness, in public-log order. */
    static List<String> redLines(String name, int code, List<String> printed) {
        List<String> red = new ArrayList<>();
        red.add(name + " | exit " + code);
        for (String r : redOf(name, printed)) if (r != null) red.add(r);
        return red;
    }

    /** How many of the red lines are not in the record. */
    static int newCount(List<String> red, Set<String> known) {
        return KnownReds.fresh(red, known).size();
    }

    /** The quiet verdict and optional tagged lines, judged by KnownReds. */
    static List<String> report(String name, int code, double seconds, List<String> printed, boolean lines, Set<String> known) {
        String[] redOf = redOf(name, printed);
        Set<String> freshLines = known == null ? Set.of() : new LinkedHashSet<>(KnownReds.fresh(redLines(name, code, printed), known));
        String verdict = "";
        if (known != null) {
            List<String> red = redLines(name, code, printed);
            int fresh = newCount(red, known);
            verdict = fresh == 0 ? "  known red" : "  NEW red: " + fresh + " of " + red.size() + " red lines";
        }
        List<String> out = new ArrayList<>();
        out.add(header(name, code, seconds) + verdict);
        if (lines) {
            for (int i = 0; i < printed.size(); i++) {
                String tag = redOf[i] == null || known == null ? "" : freshLines.contains(redOf[i]) ? NEW_TAG : KNOWN_TAG;
                out.add("    " + tag + printed.get(i));
            }
        }
        return out;
    }

    /**
     * The one line under the summary that says which harnesses were known reds
     * only and which had a NEW red; with no record, that none was read. It has
     * no "harness" in it and does not end in "0 failed": batch-check.sh and
     * ship.sh find the summary by those.
     */
    static String againstLine(Path record, Set<String> known, List<String> knownOnly, List<String> newRed) {
        String file = record.toString().replace('\\', '/');
        if (known == null) return "No known reds read (" + file + " not found): every red is untagged";
        if (record.equals(GATE)) file += " + " + KnownReds.PARTS;
        List<String> parts = new ArrayList<>();
        if (!knownOnly.isEmpty()) parts.add("known red only - " + String.join(", ", knownOnly));
        if (!newRed.isEmpty()) parts.add("NEW red - " + String.join(", ", newRed));
        return "Against " + file + ": " + String.join("; ", parts);
    }

    /**
     * How a harness is started: the JVM and classpath this one was, under -Xmx
     * HEAP_MB and -Xms HEAP_START_MB (SP1), the timing
     * properties (the mode, and the file the figures go to, TM3) if they are set here (a child JVM inherits no system property, only
     * JDK_JAVA_OPTIONS), and when its output is kept (-q and -f) in UTF-8
     * whatever the machine's own charset is - on Windows a redirected
     * System.out is Cp1252, and the first non-ASCII label stopped the run
     * (2026-10-13, MapCheck on the PC).
     */
    static List<String> command(String java, String classpath, String name, boolean logged) {
        List<String> c = new ArrayList<>(List.of(java, "-Xmx" + HEAP_MB + "m", "-Xms" + HEAP_START_MB + "m"));
        Observation.forwardTo(c);
        c.add("-D" + Observation.HARNESS_PROPERTY + "=" + name);
        if (Timing.forwarded() != null) c.add(Timing.forwarded());
        if (Timing.forwardedTsv() != null) c.add(Timing.forwardedTsv());   // TM3: the file the figures go to
        if (logged) c.addAll(List.of("-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8"));
        c.addAll(List.of("-cp", classpath, "ham.citybuildersim." + name));
        return c;
    }

    /** PO1: legacy console bytes are replaced, as in softLines and failingLines. */
    static List<String> observationLines(Path log) {
        try {
            return new String(Files.readAllBytes(log), StandardCharsets.UTF_8).lines()
                    .filter(line -> line.startsWith("OBSERVED - ") || line.startsWith("SKIPPED - ")).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    /** How many of a log's assertion lines went SOFT (Timing.SOFT_LINE); a log that cannot be read has none. */
    static int softLines(Path log) {
        try {
            int n = 0;
            for (String line : new String(Files.readAllBytes(log), StandardCharsets.UTF_8).split("\\R"))
                if (Timing.SOFT_LINE.matcher(line).find()) n++;
            return n;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * What -f prints of a failed harness's log: each failing line, after the
     * title of the section it is in (once per section); a log with no failing line in it - a harness that died some other
     * way - gives its last ten lines instead. A byte that is not UTF-8 is read
     * as a replacement character, never thrown.
     */
    static List<String> failingLines(Path log) throws IOException {
        List<String> lines = List.of(new String(Files.readAllBytes(log), StandardCharsets.UTF_8).split("\\R"));
        List<String> out = new ArrayList<>();
        String section = null, printed = null;
        int matched = 0;
        for (String line : lines) {
            if (SECTION.matcher(line).matches()) { section = line.strip(); continue; }
            if (!FAILING.matcher(line).find()) continue;
            matched++;
            if (section != null && !section.equals(printed) && !SUMMARY.matcher(line).matches()) { out.add(section); printed = section; }
            out.add(line.strip());
        }
        if (matched == 0) {
            out.add("(no FAIL line; its last lines:)");
            for (String line : lines.subList(Math.max(0, lines.size() - 10), lines.size())) out.add(line);
        }
        return out;
    }
}
