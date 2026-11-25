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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Runs every harness, one JVM each, and says which failed.
 *
 * WHY: harnesses exit their JVMs. GF1 bounds children after CD9's 62-minute
 * JavaFX hang, and MG1 gates extra starts on free commit, RAM and averaged CPU.
 * Quiet modes keep logs and timings, forwarding Timing properties and tagging
 * every failing assertion through the common judge (FW7, TM1).
 */
public class AllChecks {

    /** Every harness: the gate and nightly selections together cover this registry. */
    static final String[] HARNESSES = {
        "CopperBufferCheck", "UndemandedFeedCheck", "GroundInputCheck", "BuildingDataCheck", "BrandingCheck", "CreditsCheck", "PackagingCheck", "MoneySignCheck", "NewGameCheck", "WorldCheck", "GridCheck", "CalendarCheck", "BooksCheck", "WaterCheck", "HydroCheck", "PowerDispatchCheck", "SubstationCheck", "PowerPriceCheck", "PowerPlantsCheck", "PowerPlanCheck", "WaterPowerCheck", "PowerViewCheck",
        "EarthCheck", "WorldDataCheck", "NationsCheck", "WorldMeasuresCheck", "RankingsCheck", "WorldMarketCheck", "WorldMoneysCheck", "RealDollarCheck", "PartnersCheck", "VaultMoneysCheck", "PegAnchorCheck", "InterestAbroadCheck", "GoldPriceCheck", "GoldStandardCheck", "EarthTerrainCheck", "EarthFoundingCheck", "EarthDepositsCheck", "GlobeCheck",
        "PolicyCheck", "PolicyOpenCheck", "MoneyDatesCheck", "LandCheck", "SlopeCheck", "LandClassCheck", "LandmassCheck", "LandNationsCheck", "BridgeCheck", "RunWorksCheck", "ZoningCheck", "ZoningViewCheck", "FarmlandReserveCheck", "MiningCheck", "InvestCheck", "CreditCheck", "MortgageCheck", "HousingDoorCheck", "CreditCrunchCheck", "OrderDoorCheck",
        "ConstructionControlCheck", "DeathCareFirstCheck", "BuildersFirstCheck", "GapOrderCheck", "ConstructionTrapCheck", "ForeignCrewsCheck", "FoundingYardCheck", "BuildAdviceCheck", "BuildCardCheck", "RoadCheck", "ChildcareCheck", "AutoBuildCheck", "EssentialWorksCheck", "WorksFinanceCheck", "IslandLinkCheck", "SectorFlowCheck", "PolicyPreviewCheck", "AskProceedsCheck",
        "RestructureCheck", "BuybackCheck", "CallCheck", "FloatLinkCheck", "DeskCheck", "GdpCheck", "HistoryCheck",
        "BankCheck", "ForeignCheck", "CurrencyCheck", "ExchangeRateCheck", "InterventionCheck", "CurrencyRegimeCheck", "PassThroughCheck", "ForeignDebtCheck", "DebtExchangeCheck", "DebtMoneysCheck", "CapitalFlowCheck", "CarryTradeCheck", "EquityCheck", "ExchangeCheck", "ShortSellingCheck", "MarketCheck", "MonetaryCheck", "ExpectationsCheck", "CurveCheck", "GroceryCheck", "SupplierCreditCheck", "CentralBankCheck", "FundCheck", "FundLedgerCheck", "FormatsCheck", "HoldersCheck", "BondCheck", "OrderBookCheck",
        "DenominationCheck", "PriceLevelCheck", "DeterminismCheck", "StepsCheck",
            "HouseholdCheck", "CardCheck", "DebtStatisticsCheck", "HouseholdFlowCheck", "CapitalGainsCheck", "HouseholdStageCheck", "PopulationCheck", "LabourCheck", "EducationCheck", "HealthCheck",
        "InfrastructureCheck", "ReadPathCheck", "RobustnessCheck", "SaveFileCheck", "ReloadCheck",
        "SaveSlotCheck", "ScaleCheck", "BuildingStackCheck", "MapCheck", "GatewayCheck", "MainLineCheck", "RailDrawCheck", "JunctionCheck", "PlanCheck", "OrderSearchCheck", "SkipReportCheck", "InboxCheck", "PlaytestFixCheck", "BuildMenuCheck", "KeptCheck", "KeptTimingCheck", "ChartFitCheck", "ToastCheck", "MoneyWordsCheck", "ClockCheck",
        "ConsumptionCheck", "SectorStatementCheck", "TreasuryCheck", "HousingCheck", "OutsideCheck", "SicknessCheck", "CausesOfDeathCheck", "MedicineCheck", "HouseholdMemoryCheck", "DeathRecordCheck", "CrimeCheck", "BusinessServicesCheck", "DistressBleedCheck", "ManufacturingCheck", "AgricultureCheck", "FoodIndustryCheck", "FoodProcessingCheck", "TradeCostCheck", "RailCheck", "RailDepotCheck", "HaulageCheck", "CarCheck", "VanCheck", "RestaurantsCheck", "LuxuryRetailCheck", "OilCheck", "RefineryCheck", "RefineryViewCheck", "OilViewCheck", "WellCheck", "PortCheck", "QuayRailCheck", "QuayCraneCheck", "LandlordPortCheck", "HomeRailCheck", "FreightViewCheck", "TradeBandCheck", "MaterialsCheck", "HydrogenSteelCheck", "CoalMiningCheck", "QuarryingCheck", "CementConcreteCheck", "CopperMiningCheck", "SulphideLeachCheck", "GoldMiningCheck", "CopperRefiningCheck", "ForestryCheck", "WoodProductsCheck", "IndustryViewCheck", "YearBookCheck", "ChartCheck", "TimingCheck", "ObservationCheck", "StaleCheck", "KnownRedsCheck", "EnsembleCheck", "TruckRouteCheck", "AuctionCheck", "LongPlaytest",
        "IslandNetCheck", "IslandRailCheck", "CrossingCheck", "BridgeMapCheck", "WantedPostsCheck", "PartnerCallsCheck", "Forms1900Check", "EquipmentCheck", "FittingCheck", "DriverlessTruckCheck", "FieldOperatorsCheck", "ElectrolyserCheck", "RecipeCheck", "HistoricalRecipeCheck", "HistoricalLicenceCheck", "LicenceFeesCheck", "PublicLicenceCheck", "ResearchCheck", "DiscoveryCheck", "PrizeCheck", "ResearchPreviewCheck", "ResearchViewCheck", "WageLadderCheck", "ArrivalMixCheck", "BasketYearCheck", "Village1900Check",
        "ReplayCheck", "StaffingDrawCheck", "StaffingTieCheck", "OwnerCheck",
        "HousingInventoryCheck",
        "GovernmentViewCheck", "GovernmentPlayerCheck", "BankFailureCheck", "ER1Check", "PostwarRecipeCheck", "ModernEngineeringCheck", "ModernRowsCheck", "YoungBankCheck", "NewUnitCheck", "FitoutCheck", "FB13Check", "GroundSkipCheck",
        "ForeignChoiceCheck",
        "SchoolingLadderCheck", "SchoolingPageCheck"
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

    /** Non-heap room is 512 MB per harness, above SP1's measured 103 MB at startup and allowing compiler, classes and JavaFX growth (MG1). */
    static final int NON_HEAP_MB = 512;

    /** Keep 4 GB of RAM available, above the 2.9 GB left when two KeptChecks drove this 32 GB PC to 91% RAM (MG1). */
    static final double AVAILABLE_RAM_GB = 4;

    /** Start beside another harness below 80% CPU, leaving room before MG1's measured 85Ã¢â‚¬â€œ94% contention. */
    static final double CPU_PCT = 80;

    /** Sample every two seconds so held starts can resume without busy waiting (MG1). */
    static final int RESOURCE_SECONDS = 2;

    /** Average three CPU samples, covering six seconds between starts (MG1). */
    static final int CPU_SAMPLES = 3;

    /** GB5: the harnesses that start the JavaFX toolkit (ui.FollowBench.startJavaFx; TimingCheck 10 keeps the list whole). */
    static final List<String> DISPLAY = List.of("ChartFitCheck", "KeptCheck", "KeptTimingCheck", "MapCheck", "MoneyWordsCheck", "NewUnitCheck", "ReplayCheck", "StepsCheck", "ToastCheck");

    /** Longest first when no previous run's seconds can be read (HA1, 2026-11-12 medians). */
    static final List<String> LONGEST_FIRST = List.of("KeptCheck", "KeptTimingCheck", "CentralBankCheck", "MapCheck", "GlobeCheck", "ShortSellingCheck", "LandmassCheck", "LongPlaytest", "CopperRefiningCheck", "EarthTerrainCheck");

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
                if (haveFx || !(name.equals("BuildMenuCheck") || name.equals("KeptCheck") || name.equals("KeptTimingCheck") || name.equals("StepsCheck") || name.equals("ChartFitCheck") || name.equals("ToastCheck") || name.equals("MoneyWordsCheck"))) order.add(name);
        }
        int atOnce = parallel(logged, timingOnly);
        if (atOnce > 1) order.sort(longestFirst(TIMES, Path.of(System.getProperty("user.home"), ".cbs-gate-times.tsv")));
        Set<Process> live = ConcurrentHashMap.newKeySet();
        AtomicInteger held = new AtomicInteger();   // starts that waited at least once
        int[] heldBy = new int[4];
        CpuWindow cpu = new CpuWindow();
        ScheduledExecutorService sampler = Executors.newSingleThreadScheduledExecutor(r -> { Thread th = new Thread(r); th.setDaemon(true); return th; });
        sampler.scheduleAtFixedRate(() -> cpu.add(systemCpu()), 0, RESOURCE_SECONDS, TimeUnit.SECONDS);
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
                    int seen = 0;
                    while (!live.isEmpty()) {
                        Resources reading = readResources(() -> resources(live), cpu::average);
                        int block = startBlock(reading);
                        if (block == 0) break;
                        if (seen == 0) held.incrementAndGet();
                        if ((seen & (1 << block)) == 0) {
                            heldBy[block]++;
                            System.out.println("memory: " + name + " start held Ã¢â‚¬â€ " + holdLine(reading, block));
                            seen |= 1 << block;
                        }
                        live.wait(RESOURCE_SECONDS * 1000L);
                    }
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
        sampler.shutdownNow();
        if (logged) {
            times.sort(Comparator.comparingDouble((String x) -> Double.parseDouble(x.substring(0, x.indexOf('	')))).reversed());
            Files.write(TIMES, times, StandardCharsets.UTF_8);
        }
        System.out.println();
        System.out.println(summaryLine(ran, failed, (System.currentTimeMillis() - started) / 1000.0));
        String display = System.getProperty(ham.citybuildersim.ui.FollowBench.DISPLAY_PROPERTY, "");   // GB5: the gate box's runs
        if (!display.isEmpty()) System.out.println(displayLine(display, order));
        if (held.get() > 0) System.out.printf("memory: %d start(s) held until resources allowed Ã¢â‚¬â€ free commit %d, available RAM %d, CPU %d%n", held.get(), heldBy[1], heldBy[2], heldBy[3]);
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

    /** Injectable machine readings in GiB and percent, with each running child's private commitment. */
    record Resources(double freeCommit, double availableRam, double cpu, double[] held) {
        double needed() {
            double full = (HEAP_MB + NON_HEAP_MB) / 1024.0;
            double need = full;
            for (double committed : held) need += Math.max(0, full - committed);
            return need;
        }
    }

    /** MG1's pure admission rule holds extra starts on unknown readings and always lets an empty run progress. */
    static int startBlock(Resources r) {
        if (r.held.length == 0) return 0;
        if (!(r.freeCommit >= r.needed())) return 1;
        if (!(r.availableRam >= AVAILABLE_RAM_GB)) return 2;
        if (!(r.cpu >= 0 && r.cpu < CPU_PCT)) return 3;
        return 0;
    }

    static String holdLine(Resources r, int block) {
        return switch (block) {
            case 1 -> String.format(Locale.ROOT, "free commit %.2f GB, need %.2f GB including running growth", r.freeCommit, r.needed());
            case 2 -> String.format(Locale.ROOT, "available RAM %.2f GB, need %.2f GB", r.availableRam, AVAILABLE_RAM_GB);
            default -> String.format(Locale.ROOT, "CPU %.1f%%, need below %.1f%% over %d seconds", r.cpu, CPU_PCT, RESOURCE_SECONDS * CPU_SAMPLES);
        };
    }

    /** A rolling window holds extra starts until its first complete CPU window. */
    static class CpuWindow {
        final double[] samples = new double[CPU_SAMPLES];
        int count;
        synchronized void add(double value) { samples[count++ % samples.length] = value; }
        synchronized double average() {
            if (count < samples.length) return Double.NaN;
            double sum = 0;
            for (double value : samples) {
                if (!(value >= 0)) return Double.NaN;
                sum += value;
            }
            return sum / samples.length;
        }
    }

    static double systemCpu() {
        return ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean os
                ? os.getCpuLoad() * 100 : Double.NaN;
    }

    /** Read CPU last so a slow memory probe cannot admit a child using an earlier CPU window (REV-KD1-1). */
    static Resources readResources(Supplier<Resources> memory, DoubleSupplier cpu) {
        Resources r = memory.get();
        return new Resources(r.freeCommit, r.availableRam, cpu.getAsDouble(), r.held);
    }

    /** Windows exposes available commitment as swap space, Linux names its limit and charge in meminfo. */
    static Resources resources(Set<Process> live) {
        double gb = 1024.0 * 1024 * 1024;
        double[] held = new double[live.size()];
        double commit = Double.NaN, ram = Double.NaN;
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                String ids = String.join(",", live.stream().map(p -> Long.toString(p.pid())).toList());
                Process probe = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command",
                        "Get-Process -Id " + ids + " -ErrorAction SilentlyContinue | ForEach-Object { $_.PrivateMemorySize64 }")
                        .redirectError(ProcessBuilder.Redirect.DISCARD).start();
                try {
                    if (probe.waitFor(5, TimeUnit.SECONDS)) {
                        String output = new String(probe.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
                        int i = 0;
                        for (String line : output.lines().toList()) held[i++] = Double.parseDouble(line.strip()) / gb;
                    }
                } finally { if (probe.isAlive()) endTree(probe); }
                if (ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean os) {
                    commit = os.getFreeSwapSpaceSize() / gb;
                    ram = os.getFreeMemorySize() / gb;
                }
            } else if (Files.isRegularFile(Path.of("/proc/meminfo"))) {
                // Read child commitment first, then free commit, so growth during the reading cannot be counted twice.
                int i = 0;
                for (Process p : live) {
                    for (String line : Files.readAllLines(Path.of("/proc", Long.toString(p.pid()), "status"))) {
                        if (line.startsWith("RssAnon:")) held[i] = Double.parseDouble(line.split("\\s+")[1]) / (1024 * 1024);
                    }
                    i++;
                }
                Map<String, Double> memory = new HashMap<>();
                for (String line : Files.readAllLines(Path.of("/proc/meminfo"))) {
                    String[] parts = line.split("\\s+");
                    memory.put(parts[0], Double.parseDouble(parts[1]) / (1024 * 1024));
                }
                commit = memory.get("CommitLimit:") - memory.get("Committed_AS:");
                ram = memory.get("MemAvailable:");
            }
        } catch (IOException | RuntimeException e) {
            // Missing child figures reserve its full size, unknown machine figures hold extra starts.
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return new Resources(commit, ram, Double.NaN, held);
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

    /** GB5: under the summary where -Dcbs.display names a display: the display harnesses the run started, none of which skips there. */
    static String displayLine(String display, List<String> started) {
        List<String> ran = DISPLAY.stream().filter(started::contains).toList();
        return "display: " + display + ": " + (ran.isEmpty() ? "no display harness in this run" : ran.size() + " display harness"
                + (ran.size() == 1 ? "" : "es") + " started, none can skip here (red if the toolkit cannot start): " + String.join(", ", ran));
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

    /** Lists known and new reds without resembling the suite summary used by shell callers. */
    static String againstLine(Path record, Set<String> known, List<String> knownOnly, List<String> newRed) {
        String file = record.toString().replace('\\', '/');
        if (known == null) return "No known reds read (" + file + " not found): every red is untagged";
        if (record.equals(GATE)) file += " + " + KnownReds.PARTS;
        List<String> parts = new ArrayList<>();
        if (!knownOnly.isEmpty()) parts.add("known red only - " + String.join(", ", knownOnly));
        if (!newRed.isEmpty()) parts.add("NEW red - " + String.join(", ", newRed));
        return "Against " + file + ": " + String.join("; ", parts);
    }

    /** Starts a heap-bounded harness with forwarded properties and UTF-8 logged output (SP1, TM3). */
    static List<String> command(String java, String classpath, String name, boolean logged) {
        List<String> c = new ArrayList<>(List.of(java, "-Xmx" + HEAP_MB + "m", "-Xms" + HEAP_START_MB + "m"));
        // WHY (FS5): soft-only renderer images otherwise survive by heap headroom and wall time, including orphan locked pool entries.
        if (name.equals("KeptCheck")) c.add("-XX:SoftRefLRUPolicyMSPerMB=0");
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

    /** Keeps each failing section and assertion, or the last ten lines after an unlabelled death, replacing invalid UTF-8. */
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
