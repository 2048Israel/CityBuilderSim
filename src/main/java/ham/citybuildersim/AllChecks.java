package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Runs every harness, one JVM each, and says which failed.
 *
 * Twenty-nine harnesses and no runner meant "all checks pass" was a claim
 * made by whoever had the patience to run twenty-nine main() methods by hand
 * that day - and a harness nobody ran is a harness that does not exist. Each
 * one calls System.exit() with its verdict, so they cannot share a JVM; this
 * spawns them with the JVM and classpath it was itself started with.
 *
 *     java -cp <classes> ham.citybuildersim.AllChecks            everything
 *     java -cp <classes> ham.citybuildersim.AllChecks -q         verdicts only
 *     java -cp <classes> ham.citybuildersim.AllChecks -f         failures only
 *     java -cp <classes> ham.citybuildersim.AllChecks Labour Money   a subset, by name
 *     java -cp <classes> ham.citybuildersim.AllChecks --timing   the timing harnesses alone (Timing.HARNESSES)
 *
 * Exit status is the number of harnesses that failed, so a build script can
 * stop on it. BuildMenuCheck, KeptCheck and ChartFitCheck need JavaFX on the classpath and are skipped,
 * with a note, when it is not there.
 *
 * -f is for an AI session, where every line printed is read and paid for
 * (2026-10-13). -q printed a line for each of a hundred-odd harnesses and,
 * for a red one, only "FAILED", so the session ran it again in full to find
 * the assertion and carried its thousands of lines from then on. -f keeps
 * each harness's whole output in target/checks/NAME.log, says nothing of a
 * harness that passed, and for one that failed prints its failing lines
 * under the section they are in - at most FAIL_LINES of them - and where
 * the rest is.
 *
 * Timing (TM1, 2026-10-18): -Dcbs.timing=soft given to this JVM is given to
 * every harness it starts, where a timing figure over its budget is SOFT and
 * not counted (Timing). -f counts the SOFT lines in every log, a passing
 * harness's too, and says how many under the summary. --timing runs only the
 * harnesses that time something: what the ship gate runs, hard.
 *
 * Known reds (FW7, 2026-10-25): HealthCheck was once a recorded red of main's
 * and a second assertion in it went red; -q threw each harness's output away
 * and printed "HealthCheck FAILED", the same as the day before, so nobody saw
 * it. -q now keeps each output in target/checks/NAME.log as -f does, and both
 * read main's record of its reds (docs/notes/gate.txt as ship.sh writes it,
 * or the file named by -Dcbs.knownReds, which is what batch-check.sh gives
 * it) and say of every failed harness "known red" when each of its red lines
 * is in the record, "NEW red: N of M red lines" when not. -f also tags each
 * failing line [known] or [NEW], and a last line names the harnesses of each
 * kind. A red line is what batch-check.sh's reds_of reads from this output,
 * word for word (redLines), so the two judges count the same. With no record
 * nothing is tagged and -f prints as it did before. KnownRedsCheck causes both
 * cases with a fixture of its own.
 */
public class AllChecks {

    /** In the order they are cheapest to fail. LongPlaytest last: it is the slow one. */
    static final String[] HARNESSES = {
        "BuildingDataCheck", "IconCheck", "NewGameCheck", "WorldCheck", "GridCheck", "ConversionCheck", "CalendarCheck", "BooksCheck", "WaterCheck", "HydroCheck",
        "EarthCheck", "EarthTerrainCheck", "EarthFoundingCheck", "EarthDepositsCheck", "GlobeCheck",
        "PolicyCheck", "LandCheck", "SlopeCheck", "LandClassCheck", "LandmassCheck", "BridgeCheck", "RunWorksCheck", "ZoningCheck", "ZoningViewCheck", "MiningCheck", "InvestCheck", "CreditCheck", "MortgageCheck",
        "ConstructionControlCheck", "DeathCareFirstCheck", "BuildersFirstCheck", "FoundingYardCheck", "BuildAdviceCheck", "BuildCardCheck", "RoadCheck", "ChildcareCheck", "AutoBuildCheck", "SectorFlowCheck", "PolicyPreviewCheck",
        "RestructureCheck", "BuybackCheck", "CallCheck", "FloatLinkCheck", "DeskCheck", "ConservationCheck", "MoneyCheck", "GdpCheck", "HistoryCheck",
        "BankCheck", "ForeignCheck", "CurrencyCheck", "ExchangeRateCheck", "CurrencyRegimeCheck", "PassThroughCheck", "ForeignDebtCheck", "CapitalFlowCheck", "CarryTradeCheck", "EquityCheck", "ExchangeCheck", "MonetaryCheck", "ExpectationsCheck", "CurveCheck", "GroceryCheck", "SupplierCreditCheck", "CentralBankCheck", "FundCheck", "FundLedgerCheck", "HoldersCheck", "BondCheck", "OrderBookCheck",
        "DenominationCheck",
        "HouseholdCheck", "PopulationCheck", "LabourCheck", "EducationCheck", "HealthCheck",
        "InfrastructureCheck", "ReadPathCheck", "RobustnessCheck", "SaveFileCheck", "ReloadCheck",
        "SaveSlotCheck", "ScaleCheck", "MapCheck", "GatewayCheck", "MainLineCheck", "RailDrawCheck", "JunctionCheck", "PlanCheck", "OrderSearchCheck", "SkipReportCheck", "InboxCheck", "PlaytestFixCheck", "BuildMenuCheck", "KeptCheck", "ChartFitCheck", "ClockCheck",
        "ConsumptionCheck", "SectorBooksCheck", "SectorStatementCheck", "TreasuryCheck", "HousingCheck", "OutsideCheck", "SicknessCheck", "HouseholdMemoryCheck", "DeathRecordCheck", "CrimeCheck", "BusinessServicesCheck", "ManufacturingCheck", "AgricultureCheck", "FoodProcessingCheck", "TradeCostCheck", "RailCheck", "RailDepotCheck", "HaulageCheck", "CarCheck", "VanCheck", "RestaurantsCheck", "OilCheck", "RefineryCheck", "RefineryViewCheck", "OilViewCheck", "WellCheck", "PortCheck", "LandlordPortCheck", "HomeRailCheck", "FreightViewCheck", "TradeBandCheck", "MaterialsCheck", "CoalMiningCheck", "QuarryingCheck", "CementConcreteCheck", "CopperMiningCheck", "CopperRefiningCheck", "ForestryCheck", "WoodProductsCheck", "IndustryViewCheck", "YearBookCheck", "ChartCheck", "TimingCheck", "StaleCheck", "KnownRedsCheck", "EnsembleCheck", "TruckCheck", "TruckRouteCheck", "AuctionCheck", "LongPlaytest",
        "IslandNetCheck", "CrossingCheck", "BridgeMapCheck"
    };

    /** The most lines -f prints for one failed harness; the rest stays in its log. */
    static final int FAIL_LINES = 40;

    /** Where -q and -f keep each harness's whole output. */
    static final Path LOGS = Paths.get("target", "checks");

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
        boolean quiet = false, failuresOnly = false, timingOnly = false;
        List<String> only = new ArrayList<>();
        for (String a : args) {
            if (a.equals("-q")) quiet = true;
            else if (a.equals("-f")) failuresOnly = true;
            else if (a.equals("--timing")) timingOnly = true;
            else only.add(a.toLowerCase());
        }
        boolean logged = quiet || failuresOnly;   // both keep each harness's output in LOGS, so a verdict can be read from it
        if (logged) Files.createDirectories(LOGS);
        Path record = recordFile();
        Set<String> known = logged ? knownReds(record) : null;

        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        boolean haveFx = classpath.toLowerCase().contains("javafx");

        int failed = 0, ran = 0, soft = 0;
        List<String> softIn = new ArrayList<>();
        List<String> knownOnly = new ArrayList<>(), newRed = new ArrayList<>();
        long started = System.currentTimeMillis();
        for (String name : HARNESSES) {
            if (timingOnly && !Timing.HARNESSES.contains(name)) continue;
            if (!only.isEmpty()) {
                boolean wanted = false;
                for (String o : only) if (name.toLowerCase().contains(o)) wanted = true;
                if (!wanted) continue;
            }
            if ((name.equals("BuildMenuCheck") || name.equals("KeptCheck") || name.equals("ChartFitCheck")) && !haveFx) {
                System.out.printf("%-24s skipped (needs JavaFX on the classpath)%n", name);
                continue;
            }
            long t = System.currentTimeMillis();
            ProcessBuilder pb = new ProcessBuilder(command(java, classpath, name, logged));
            pb.redirectErrorStream(true);
            Path log = LOGS.resolve(name + ".log");
            if (logged) {
                pb.redirectOutput(log.toFile());
            } else {
                pb.inheritIO();
            }
            int code = pb.start().waitFor();
            ran++;
            if (code != 0) failed++;
            if (logged) {
                int n = softLines(log);
                if (n > 0) { soft += n; softIn.add(name + " " + n); }
            }
            double seconds = (System.currentTimeMillis() - t) / 1000.0;
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
        System.out.println();
        System.out.println(summaryLine(ran, failed, (System.currentTimeMillis() - started) / 1000.0));
        if (soft > 0) System.out.printf("%d timing assertion%s SOFT, not counted (%s): %s%n",
                soft, soft == 1 ? "" : "s", Timing.forwarded(), String.join(", ", softIn));
        if (logged && failed > 0) {
            System.out.println(againstLine(record, known, knownOnly, newRed));
            System.out.println("Whole output of each: " + LOGS + File.separator + "NAME.log");
        }
        System.exit(failed);
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

    /**
     * The record this run reads: the file named by -Dcbs.knownReds when that
     * is set, else GATE.
     */
    static Path recordFile() {
        String named = System.getProperty(KNOWN_PROPERTY);
        return named == null || named.isBlank() ? GATE : Paths.get(named);
    }

    /**
     * Main's recorded reds, as batch-check.sh reads them: the lines of the
     * file, each exactly as written, but for the lines that start with # and
     * the blank ones. Null when there is no such file, so that nothing is
     * tagged. A byte that is not UTF-8 is read as a replacement character,
     * never thrown.
     */
    static Set<String> knownReds(Path file) throws IOException {
        if (!Files.isRegularFile(file)) return null;
        Set<String> known = new LinkedHashSet<>();
        for (String line : new String(Files.readAllBytes(file), StandardCharsets.UTF_8).split("\\R")) {
            if (line.startsWith("#") || line.isBlank()) continue;
            known.add(line);
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

    /**
     * For each printed line of a failed harness (failingLines, untagged), the
     * red line it makes, or null when it makes none: a section title, a
     * blank, and every line after the "(no FAIL line...)" one, which are the
     * harness's last lines and no failures. The one walk that redLines counts
     * and report tags from, so the two cannot disagree.
     */
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

    /**
     * The red lines of one failed harness, the same lines batch-check.sh's
     * reds_of makes of the log: "Name | exit N", then "Name | <line>" for each
     * failing line printed, a section title and the last lines of a harness
     * with no failing line of its own left out.
     */
    static List<String> redLines(String name, int code, List<String> printed) {
        List<String> red = new ArrayList<>();
        red.add(name + " | exit " + code);
        for (String r : redOf(name, printed)) if (r != null) red.add(r);
        return red;
    }

    /** How many of the red lines are not in the record. */
    static int newCount(List<String> red, Set<String> known) {
        int n = 0;
        for (String r : red) if (!known.contains(r)) n++;
        return n;
    }

    /**
     * What -q and -f print of one failed harness. The header, then its verdict
     * when there is a record (known is not null): "known red" when every red
     * line is in it, else "NEW red: N of M red lines". When lines is true (-f)
     * the failing lines under it, indented four spaces, each red one tagged
     * [known] or [NEW]; with no record, untagged as -f always printed them.
     */
    static List<String> report(String name, int code, double seconds, List<String> printed, boolean lines, Set<String> known) {
        String[] redOf = redOf(name, printed);
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
                String tag = redOf[i] == null || known == null ? "" : known.contains(redOf[i]) ? KNOWN_TAG : NEW_TAG;
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
        List<String> parts = new ArrayList<>();
        if (!knownOnly.isEmpty()) parts.add("known red only - " + String.join(", ", knownOnly));
        if (!newRed.isEmpty()) parts.add("NEW red - " + String.join(", ", newRed));
        return "Against " + file + ": " + String.join("; ", parts);
    }

    /**
     * How a harness is started: the JVM and classpath this one was, the timing
     * properties (the mode, and the file the figures go to, TM3) if they are set here (a child JVM inherits no system property, only
     * JDK_JAVA_OPTIONS), and when its output is kept (-q and -f) in UTF-8
     * whatever the machine's own charset is - on Windows a redirected
     * System.out is Cp1252, and the first non-ASCII label stopped the run
     * (2026-10-13, MapCheck on the PC).
     */
    static List<String> command(String java, String classpath, String name, boolean logged) {
        List<String> c = new ArrayList<>(List.of(java));
        if (Timing.forwarded() != null) c.add(Timing.forwarded());
        if (Timing.forwardedTsv() != null) c.add(Timing.forwardedTsv());   // TM3: the file the figures go to
        if (logged) c.addAll(List.of("-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8"));
        c.addAll(List.of("-cp", classpath, "ham.citybuildersim." + name));
        return c;
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
     * title of the section it is in (once per section), at most FAIL_LINES in
     * all; a log with no failing line in it - a harness that died some other
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
            if (out.size() >= FAIL_LINES) continue;
            if (section != null && !section.equals(printed) && !SUMMARY.matcher(line).matches()) { out.add(section); printed = section; }
            out.add(line.strip());
        }
        if (matched == 0) {
            out.add("(no FAIL line; its last lines:)");
            for (String line : lines.subList(Math.max(0, lines.size() - 10), lines.size())) out.add(line);
        } else if (out.size() >= FAIL_LINES) {
            out.add("... " + matched + " failing lines in all: " + log);
        }
        return out;
    }
}
