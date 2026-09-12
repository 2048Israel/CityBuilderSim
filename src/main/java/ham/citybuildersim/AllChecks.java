package ham.citybuildersim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
 *     java -cp <classes> ham.citybuildersim.AllChecks Labour Money   a subset, by name
 *
 * Exit status is the number of harnesses that failed, so a build script can
 * stop on it. BuildMenuCheck needs JavaFX on the classpath and is skipped,
 * with a note, when it is not there.
 */
public class AllChecks {

    /** In the order they are cheapest to fail. LongPlaytest last: it is the slow one. */
    static final String[] HARNESSES = {
        "BuildingDataCheck", "NewGameCheck", "CalendarCheck", "BooksCheck", "WaterCheck",
        "PolicyCheck", "LandCheck", "MiningCheck", "InvestCheck", "CreditCheck",
        "RestructureCheck", "ConservationCheck", "MoneyCheck", "GdpCheck", "HistoryCheck",
        "BankCheck", "ForeignCheck", "ForeignDebtCheck", "CapitalFlowCheck", "EquityCheck", "ExchangeCheck", "MonetaryCheck",
        "DenominationCheck",
        "HouseholdCheck", "PopulationCheck", "LabourCheck", "EducationCheck", "HealthCheck",
        "InfrastructureCheck", "ReadPathCheck", "RobustnessCheck", "SaveFileCheck",
        "SaveSlotCheck", "SkipReportCheck", "InboxCheck", "BuildMenuCheck",
        "SectorBooksCheck", "TreasuryCheck", "HousingCheck", "OutsideCheck", "SicknessCheck", "HouseholdMemoryCheck", "DeathRecordCheck", "CrimeCheck", "LongPlaytest"
    };

    public static void main(String[] args) throws Exception {
        boolean quiet = false;
        List<String> only = new ArrayList<>();
        for (String a : args) {
            if (a.equals("-q")) quiet = true; else only.add(a.toLowerCase());
        }

        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        boolean haveFx = classpath.toLowerCase().contains("javafx");

        int failed = 0, ran = 0;
        long started = System.currentTimeMillis();
        for (String name : HARNESSES) {
            if (!only.isEmpty()) {
                boolean wanted = false;
                for (String o : only) if (name.toLowerCase().contains(o)) wanted = true;
                if (!wanted) continue;
            }
            if (name.equals("BuildMenuCheck") && !haveFx) {
                System.out.printf("%-24s skipped (needs JavaFX on the classpath)%n", name);
                continue;
            }
            long t = System.currentTimeMillis();
            ProcessBuilder pb = new ProcessBuilder(java, "-cp", classpath, "ham.citybuildersim." + name);
            pb.redirectErrorStream(true);
            if (quiet) {
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            } else {
                pb.inheritIO();
            }
            int code = pb.start().waitFor();
            ran++;
            if (code != 0) failed++;
            System.out.printf("%-24s %s  (%.1fs)%n", name, code == 0 ? "OK" : "FAILED - exit " + code,
                    (System.currentTimeMillis() - t) / 1000.0);
        }
        System.out.printf("%n%d harness%s run in %.0fs: %d passed, %d failed%n",
                ran, ran == 1 ? "" : "es", (System.currentTimeMillis() - started) / 1000.0,
                ran - failed, failed);
        System.exit(failed);
    }
}
