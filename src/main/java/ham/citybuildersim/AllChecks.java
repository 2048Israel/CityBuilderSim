package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
 * stop on it. BuildMenuCheck and KeptCheck need JavaFX on the classpath and are skipped,
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
 */
public class AllChecks {

    /** In the order they are cheapest to fail. LongPlaytest last: it is the slow one. */
    static final String[] HARNESSES = {
        "BuildingDataCheck", "NewGameCheck", "WorldCheck", "GridCheck", "ConversionCheck", "CalendarCheck", "BooksCheck", "WaterCheck",
        "EarthCheck", "EarthTerrainCheck", "EarthFoundingCheck", "EarthDepositsCheck", "GlobeCheck",
        "PolicyCheck", "LandCheck", "ZoningCheck", "ZoningViewCheck", "MiningCheck", "InvestCheck", "CreditCheck", "MortgageCheck",
        "ConstructionControlCheck", "DeathCareFirstCheck", "BuildersFirstCheck", "BuildAdviceCheck", "BuildCardCheck", "RoadCheck", "ChildcareCheck", "AutoBuildCheck", "SectorFlowCheck", "PolicyPreviewCheck",
        "RestructureCheck", "BuybackCheck", "ConservationCheck", "MoneyCheck", "GdpCheck", "HistoryCheck",
        "BankCheck", "ForeignCheck", "CurrencyCheck", "ExchangeRateCheck", "CurrencyRegimeCheck", "PassThroughCheck", "ForeignDebtCheck", "CapitalFlowCheck", "CarryTradeCheck", "EquityCheck", "ExchangeCheck", "MonetaryCheck", "ExpectationsCheck", "CurveCheck", "GroceryCheck", "SupplierCreditCheck", "CentralBankCheck", "FundCheck", "FundLedgerCheck", "HoldersCheck", "BondCheck", "OrderBookCheck",
        "DenominationCheck",
        "HouseholdCheck", "PopulationCheck", "LabourCheck", "EducationCheck", "HealthCheck",
        "InfrastructureCheck", "ReadPathCheck", "RobustnessCheck", "SaveFileCheck",
        "SaveSlotCheck", "ScaleCheck", "MapCheck", "GatewayCheck", "MainLineCheck", "RailDrawCheck", "JunctionCheck", "PlanCheck", "OrderSearchCheck", "SkipReportCheck", "InboxCheck", "BuildMenuCheck", "KeptCheck", "ClockCheck",
        "ConsumptionCheck", "SectorBooksCheck", "SectorStatementCheck", "TreasuryCheck", "HousingCheck", "OutsideCheck", "SicknessCheck", "HouseholdMemoryCheck", "DeathRecordCheck", "CrimeCheck", "BusinessServicesCheck", "ManufacturingCheck", "AgricultureCheck", "FoodProcessingCheck", "TradeCostCheck", "RailCheck", "RailDepotCheck", "HaulageCheck", "CarCheck", "VanCheck", "RestaurantsCheck", "OilCheck", "RefineryCheck", "RefineryViewCheck", "OilViewCheck", "WellCheck", "PortCheck", "LandlordPortCheck", "HomeRailCheck", "FreightViewCheck", "TradeBandCheck", "MaterialsCheck", "CoalMiningCheck", "QuarryingCheck", "CementConcreteCheck", "CopperMiningCheck", "CopperRefiningCheck", "ForestryCheck", "WoodProductsCheck", "IndustryViewCheck", "YearBookCheck", "ChartCheck", "TimingCheck", "StaleCheck", "EnsembleCheck", "TruckCheck", "TruckRouteCheck", "AuctionCheck", "LongPlaytest"
    };

    /** The most lines -f prints for one failed harness; the rest stays in its log. */
    static final int FAIL_LINES = 40;

    /** Where -f keeps each harness's whole output. */
    static final Path LOGS = Paths.get("target", "checks");

    /** A harness's section title, "--- what it proves ---". */
    static final Pattern SECTION = Pattern.compile("^\\s*---\\s.*\\s---\\s*$");

    /** A harness's closing count, "N FAILED": printed, but not under the last section's title. */
    static final Pattern SUMMARY = Pattern.compile("^\\s*\\d+ FAILED\\s*$");

    /** A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. */
    static final Pattern FAILING = Pattern.compile("\\bFAIL(ED)?\\b|Exception|^\\S*Error\\b|^Caused by");

    public static void main(String[] args) throws Exception {
        boolean quiet = false, failuresOnly = false, timingOnly = false;
        List<String> only = new ArrayList<>();
        for (String a : args) {
            if (a.equals("-q")) quiet = true;
            else if (a.equals("-f")) failuresOnly = true;
            else if (a.equals("--timing")) timingOnly = true;
            else only.add(a.toLowerCase());
        }
        if (failuresOnly) Files.createDirectories(LOGS);

        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        boolean haveFx = classpath.toLowerCase().contains("javafx");

        int failed = 0, ran = 0, soft = 0;
        List<String> softIn = new ArrayList<>();
        long started = System.currentTimeMillis();
        for (String name : HARNESSES) {
            if (timingOnly && !Timing.HARNESSES.contains(name)) continue;
            if (!only.isEmpty()) {
                boolean wanted = false;
                for (String o : only) if (name.toLowerCase().contains(o)) wanted = true;
                if (!wanted) continue;
            }
            if ((name.equals("BuildMenuCheck") || name.equals("KeptCheck")) && !haveFx) {
                System.out.printf("%-24s skipped (needs JavaFX on the classpath)%n", name);
                continue;
            }
            long t = System.currentTimeMillis();
            ProcessBuilder pb = new ProcessBuilder(command(java, classpath, name, failuresOnly));
            pb.redirectErrorStream(true);
            Path log = LOGS.resolve(name + ".log");
            if (failuresOnly) {
                pb.redirectOutput(log.toFile());
            } else if (quiet) {
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            } else {
                pb.inheritIO();
            }
            int code = pb.start().waitFor();
            ran++;
            if (code != 0) failed++;
            if (failuresOnly) {
                int n = softLines(log);
                if (n > 0) { soft += n; softIn.add(name + " " + n); }
            }
            if (failuresOnly && code == 0) continue;
            System.out.printf("%-24s %s  (%.1fs)%n", name, code == 0 ? "OK" : "FAILED - exit " + code,
                    (System.currentTimeMillis() - t) / 1000.0);
            if (failuresOnly) {
                try {
                    for (String line : failingLines(log)) System.out.println("    " + line);
                } catch (IOException e) {
                    System.out.println("    (its log could not be read: " + e + "; it is " + log + ")");
                }
            }
        }
        System.out.printf("%n%d harness%s run in %.0fs: %d passed, %d failed%n",
                ran, ran == 1 ? "" : "es", (System.currentTimeMillis() - started) / 1000.0,
                ran - failed, failed);
        if (soft > 0) System.out.printf("%d timing assertion%s SOFT, not counted (%s): %s%n",
                soft, soft == 1 ? "" : "s", Timing.forwarded(), String.join(", ", softIn));
        if (failuresOnly && failed > 0) System.out.println("Whole output of each: " + LOGS + File.separator + "NAME.log");
        System.exit(failed);
    }

    /**
     * How a harness is started: the JVM and classpath this one was, the timing
     * property if it is set here (a child JVM inherits no system property, only
     * JDK_JAVA_OPTIONS), and under -f its output in UTF-8 whatever the
     * machine's own charset is - on Windows a redirected System.out is Cp1252,
     * and the first non-ASCII label stopped the run (2026-10-13, MapCheck on
     * the PC).
     */
    static List<String> command(String java, String classpath, String name, boolean failuresOnly) {
        List<String> c = new ArrayList<>(List.of(java));
        if (Timing.forwarded() != null) c.add(Timing.forwarded());
        if (failuresOnly) c.addAll(List.of("-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8"));
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
