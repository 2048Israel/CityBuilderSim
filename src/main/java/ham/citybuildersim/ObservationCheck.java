package ham.citybuildersim;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the path-observation contract with a premise deliberately held off.
 *
 * WHY (PO1): a soft premise must never make its unrun law pass, and nightly
 * must retain the same miss without turning it into a correctness failure.
 */
public final class ObservationCheck {
    static int fails;

    static void check(String label, boolean ok) {
        System.out.println("  " + label + ": " + (ok ? "OK" : "FAIL"));
        if (!ok) fails++;
    }

    /** A miniature harness whose missed premise protects an unsafe dependent law. */
    public static final class Fixture {
        public static void main(String[] args) {
            int laws = 0;
            if (Observation.reached("played city has paper", false, "paper's coupon and waterfall")) {
                laws++;
                throw new AssertionError("unreached paper was read");
            }
            if (Observation.reached("constructed book has paper", true, "book identity")) laws++;
            boolean identity = args.length == 0;
            System.out.println("  independent book identity: " + (identity ? "OK" : "FAIL"));
            System.out.println("laws run " + laws);
            System.exit(identity ? 0 : 1);
        }
    }

    public static void main(String[] args) throws Exception {
        PrintStream out = System.out;
        String mode = System.getProperty(Observation.PROPERTY), file = System.getProperty(Observation.TSV_PROPERTY);
        Path root = TempDirs.create("observation-check");
        try {
            for (String value : List.of("soft", "hard")) {
                Path rows = root.resolve(value + ".tsv");
                System.setProperty(Observation.PROPERTY, value);
                System.setProperty(Observation.TSV_PROPERTY, rows.toString());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
                int laws = 0;
                if (Observation.reached("held off", false, "dependent law")) laws++;
                boolean held = Observation.reached("caused", true);
                System.setOut(out);
                String log = bytes.toString(StandardCharsets.UTF_8);
                check(value + " returns the actual premise and skips only its dependent law", laws == 0 && held);
                check(value + " prints the miss, skipped law and reason", log.contains("OBSERVED - not reached: held off")
                        && log.contains("SKIPPED - dependent law because not reached: held off"));
                check(value + " records holds and misses only in the nightly mode", value.equals("hard")
                        ? Files.readString(rows).contains("\theld off\tMISSED\n") && Files.readString(rows).contains("\tcaused\tREACHED\n")
                        : !Files.exists(rows));
                List<String> command = AllChecks.command(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                        System.getProperty("java.class.path"), "ObservationCheck$Fixture", true);
                check(value + " forwards the observation mode and record", command.contains("-Dcbs.observations=" + value)
                        && command.contains("-Dcbs.observations.tsv=" + rows));
                Path logFile = root.resolve(value + ".log");
                Process child = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(logFile.toFile()).start();
                int exit = AllChecks.waitForHarness(child, 60, logFile);
                log = Files.readString(logFile);
                check(value + " child exits zero, names the missed premise and runs its independent law", exit == 0
                        && log.contains("OBSERVED - not reached: played city has paper") && log.contains("laws run 1"));
                command = new ArrayList<>(command);
                command.add("break-independent-law");
                child = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(logFile.toFile()).start();
                check(value + " independent correctness failure still counts", AllChecks.waitForHarness(child, 60, logFile) == 1);
            }
            check("every observation harness is registered", List.of(AllChecks.HARNESSES).containsAll(Observation.HARNESSES));
        } finally {
            System.setOut(out);
            if (mode == null) System.clearProperty(Observation.PROPERTY); else System.setProperty(Observation.PROPERTY, mode);
            if (file == null) System.clearProperty(Observation.TSV_PROPERTY); else System.setProperty(Observation.TSV_PROPERTY, file);
        }
        System.out.println(fails == 0 ? "All passed." : fails + " FAILED");
        System.exit(fails);
    }
}
