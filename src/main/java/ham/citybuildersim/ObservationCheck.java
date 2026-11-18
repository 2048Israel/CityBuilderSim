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
        static final class Delegate {
            static boolean paper() {
                return Observation.reached("played city has paper", false, "paper's coupon and waterfall");
            }
        }

        public static void main(String[] args) {
            int laws = 0;
            if (Delegate.paper()) {
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
                        && command.contains("-Dcbs.observations.tsv=" + rows)
                        && command.contains("-D" + Observation.HARNESS_PROPERTY + "=ObservationCheck$Fixture"));
                Path logFile = root.resolve(value + ".log");
                Process child = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(logFile.toFile()).start();
                int exit = AllChecks.waitForHarness(child, 60, logFile);
                log = Files.readString(logFile);
                check(value + " child exits zero, names the missed premise and runs its independent law", exit == 0
                        && log.contains("OBSERVED - not reached: played city has paper") && log.contains("laws run 1"));
                if (value.equals("hard")) check("a delegated reading belongs to the running harness",
                        Files.readString(rows).contains("ObservationCheck$Fixture\tplayed city has paper\tMISSED\n"));
                command = new ArrayList<>(command);
                command.add("break-independent-law");
                child = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(logFile.toFile()).start();
                check(value + " independent correctness failure still counts", AllChecks.waitForHarness(child, 60, logFile) == 1);
            }
            Path legacy = root.resolve("legacy.log");
            byte[] prefix = "OBSERVED - not reached: legacy ".getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream legacyBytes = new ByteArrayOutputStream();
            legacyBytes.write(prefix);
            legacyBytes.write(0x96);
            legacyBytes.write("\nSKIPPED - dependent law\nfinished\n".getBytes(StandardCharsets.UTF_8));
            Files.write(legacy, legacyBytes.toByteArray());
            List<String> echoes = AllChecks.observationLines(legacy);
            check("a legacy bad byte is replaced and observation echoing completes",
                    echoes.size() == 2 && echoes.get(0).endsWith("\uFFFD") && echoes.get(1).startsWith("SKIPPED - "));
            PriceLevelCheck.Child full = new PriceLevelCheck.Child();
            full.series.put("pop", new PriceLevelCheck.Series('R', new double[]{1, 2, 3, 4}));
            full.counts.put("lanes", new double[]{0, 0, 1, 1});
            PriceLevelCheck.Child prefixOnly = full.prefix(1);
            prefixOnly.series.get("pop").v[0] = 99;
            prefixOnly.counts.get("lanes")[0] = 99;
            check("nudge comparison copies preserve full-run series and cut-off counts",
                    full.series.get("pop").v.length == 4 && full.series.get("pop").v[0] == 1
                            && full.counts.get("lanes").length == 4 && full.counts.get("lanes")[0] == 0
                            && prefixOnly.series.get("pop").v.length == 2 && prefixOnly.counts.get("lanes").length == 2);
            check("every observation harness is registered", List.of(AllChecks.HARNESSES).containsAll(Observation.HARNESSES));
            java.util.Set<String> users = new java.util.TreeSet<>();
            try (var sources = Files.list(Path.of("src/main/java/ham/citybuildersim"))) {
                for (Path source : sources.filter(p -> p.getFileName().toString().endsWith("Check.java")).toList()) {
                    if (source.getFileName().toString().equals("ObservationCheck.java")) continue;
                    if (Files.readString(source).contains("Observation.reached("))
                        users.add(source.getFileName().toString().replace(".java", ""));
                }
            }
            check("every source with a path reading is selected for nightly observations",
                    users.equals(new java.util.TreeSet<>(Observation.HARNESSES)));
        } finally {
            System.setOut(out);
            if (mode == null) System.clearProperty(Observation.PROPERTY); else System.setProperty(Observation.PROPERTY, mode);
            if (file == null) System.clearProperty(Observation.TSV_PROPERTY); else System.setProperty(Observation.TSV_PROPERTY, file);
        }
        System.out.println(fails == 0 ? "All passed." : fails + " FAILED");
        System.exit(fails);
    }
}
