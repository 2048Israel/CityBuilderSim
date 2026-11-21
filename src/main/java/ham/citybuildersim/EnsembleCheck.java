package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Checks the ensemble runner, driver compilation, policy names and stored baseline.
 *
 * WHY (EN1): an out-of-tree driver could break against LongPlaytest unnoticed
 * until a batch needed its score. Fixtures prove both compiler verdicts, policy
 * agreement and baseline parsing, including missing fields and malformed JSON.
 * Run at the repository root, with -Dtools and -Ddocs overriding those folders.
 */
public class EnsembleCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-68s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** An assertion that has a reason to print when it fails. */
    static void assertTrue(String label, boolean ok, String whyNot) {
        assertTrue(label, ok);
        if (!ok && whyNot != null) System.out.println("      " + whyNot);
    }

    /** The folder the runner lives in. */
    static Path toolsDir() {
        return Paths.get(System.getProperty("tools", "tools"), "ensemble");
    }

    /** The folder the stored baselines live in. */
    static Path baselineDir() {
        return Paths.get(System.getProperty("docs", "docs"), "ensemble");
    }

    public static void main(String[] args) throws Exception {

        if (!Files.isDirectory(Paths.get(System.getProperty("src", "src/main/java")))) {
            // the same signal StaleCheck takes: run outside the repository there is no runner to look for
            System.out.println("--- no source tree ---");
            System.out.println("skipped: run from the repository root");
            System.out.println("\nAll checks passed.");
            return;
        }

        runnerIsThere();
        driverCompiles();
        List<String> policies = samePolicies();
        baselineReads(policies);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ==================== 1. the runner is in the tree ==================== */

    static void runnerIsThere() throws IOException {
        System.out.println("--- the runner is in the tree ---");
        Path dir = toolsDir();
        for (String name : List.of("ensemble.sh", "score.py", "Ensemble.java", "README.md")) {
            Path p = dir.resolve(name);
            assertTrue("tools/ensemble/" + name + " is there and not empty",
                    Files.isRegularFile(p) && Files.size(p) > 0, "looked at " + p.toAbsolutePath());
        }
    }

    /* ==================== 2. the driver compiles against this tree ======== */

    static void driverCompiles() throws IOException {
        System.out.println("\n--- the driver compiles against this tree ---");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("SKIP: no Java compiler here (a JRE, not a JDK) - the driver was not compiled");
            return;
        }

        Path root = TempDirs.create("ensemble-compile");
        try {
            Path fixtures = Files.createDirectories(root.resolve("fixtures"));

            // the fixtures first: they prove the helper can say no, and can say yes
            Path broken = fixtures.resolve("EnsembleCheckBroken.java");
            Files.writeString(broken, BROKEN, StandardCharsets.UTF_8);
            List<String> brokenProblems = compileProblems(compiler, List.of(broken), root.resolve("out-broken"));
            assertTrue("a source calling a method no class has does not compile", !brokenProblems.isEmpty());
            assertTrue("...and the compiler names the method it could not find",
                    brokenProblems.stream().anyMatch(s -> s.contains("noSuchMethodForEnsembleCheck")),
                    String.join("\n      ", brokenProblems));

            Path sound = fixtures.resolve("EnsembleCheckSound.java");
            Files.writeString(sound, SOUND, StandardCharsets.UTF_8);
            List<String> soundProblems = compileProblems(compiler, List.of(sound), root.resolve("out-sound"));
            assertTrue("a source calling a real class of the tree compiles", soundProblems.isEmpty(),
                    String.join("\n      ", soundProblems));

            // the driver itself, against the classpath this harness runs on
            Path driver = toolsDir().resolve("Ensemble.java");
            List<String> problems = compileProblems(compiler, List.of(driver), root.resolve("out-driver"));
            assertTrue("the driver compiles against the classes this harness runs on",
                    problems.isEmpty(), firstOf(problems, 5));
            if (problems.isEmpty()) {
                assertTrue("...and leaves its class behind",
                        Files.isRegularFile(root.resolve("out-driver/ham/citybuildersim/Ensemble.class")));
            }
        } finally {
            cleanUp(root);
        }
    }

    /** Compiles against this JVM's classpath, returning English diagnostics or an empty list on success. */
    static List<String> compileProblems(JavaCompiler compiler, List<Path> sources, Path outDir) throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path s : sources) {
            if (!Files.isRegularFile(s)) problems.add(s + " is not there");
        }
        if (!problems.isEmpty()) return problems;
        Files.createDirectories(outDir);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        boolean ok;
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
            List<String> options = List.of("-d", outDir.toString(),
                    "-cp", System.getProperty("java.class.path"),
                    "-encoding", "UTF-8", "-proc:none");
            ok = compiler.getTask(null, fm, diagnostics, options, null, fm.getJavaFileObjectsFromPaths(sources)).call();
        }
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR) continue;
            String file = d.getSource() == null ? "?" : Paths.get(d.getSource().toUri()).getFileName().toString();
            problems.add(file + ":" + d.getLineNumber() + ": " + d.getMessage(Locale.ENGLISH).replace('\n', ' '));
        }
        if (!ok && problems.isEmpty()) problems.add("the compiler reported failure and no error");
        return problems;
    }

    /** The first few of a list, one to a line, for a failing assertion to print. */
    static String firstOf(List<String> lines, int n) {
        if (lines.isEmpty()) return null;
        String head = String.join("\n      ", lines.subList(0, Math.min(n, lines.size())));
        return lines.size() > n ? head + "\n      (" + (lines.size() - n) + " more)" : head;
    }

    /* ==================== 3. the same policies ============================ */

    static final Pattern POLICY_ENUM = Pattern.compile("\\benum\\s+Policy\\s*\\{([^}]*)\\}");
    static final Pattern POLICIES_LIST = Pattern.compile("(?m)^POLICIES\\s*=\\s*\\[([^\\]]*)\\]");
    /** A name in double or single quotes, as either half lists its policies. */
    static final Pattern QUOTED = Pattern.compile("\"([^\"]*)\"|'([^']*)'");

    /** The constants of the driver's Policy enum, in the order written; empty when the source has none. */
    static List<String> driverPolicies(String source) {
        List<String> out = new ArrayList<>();
        if (source == null) return out;
        Matcher m = POLICY_ENUM.matcher(source);
        if (!m.find()) return out;
        String body = m.group(1).replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\\n]*", "");
        int semi = body.indexOf(';');
        if (semi >= 0) body = body.substring(0, semi);
        for (String piece : body.split(",")) {
            Matcher id = Pattern.compile("^\\s*([A-Za-z_][A-Za-z0-9_]*)").matcher(piece);
            if (id.find()) out.add(id.group(1));
        }
        return out;
    }

    /** The strings of the scorer's POLICIES list, in the order written; empty when it has none. */
    static List<String> scorerPolicies(String source) {
        List<String> out = new ArrayList<>();
        if (source == null) return out;
        Matcher m = POLICIES_LIST.matcher(source);
        if (!m.find()) return out;
        Matcher q = QUOTED.matcher(m.group(1));
        while (q.find()) out.add(q.group(1) != null ? q.group(1) : q.group(2));
        return out;
    }

    /** Null when the two sources name the same policies, else why not. */
    static String policyProblem(String driverSource, String scorerSource) {
        List<String> d = driverPolicies(driverSource);
        List<String> s = scorerPolicies(scorerSource);
        if (d.isEmpty()) return "no enum Policy constants found in the driver";
        if (s.isEmpty()) return "no POLICIES list found in the scorer";
        List<String> onlyD = new ArrayList<>(d);
        onlyD.removeAll(s);
        List<String> onlyS = new ArrayList<>(s);
        onlyS.removeAll(d);
        if (!onlyD.isEmpty() || !onlyS.isEmpty())
            return "the driver alone has " + onlyD + ", the scorer alone has " + onlyS;
        List<String> sd = new ArrayList<>(d), ss = new ArrayList<>(s);
        Collections.sort(sd);
        Collections.sort(ss);
        if (!sd.equals(ss)) return "a policy is listed twice in one of them: driver " + d + ", scorer " + s;
        return null;
    }

    static String read(Path p) {
        try {
            return Files.readString(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /** Section 3; returns the driver's policies, which section 4 holds the baseline to. */
    static List<String> samePolicies() {
        System.out.println("\n--- the driver and the scorer name the same policies ---");

        // the reader first, on texts that cause each condition
        String enumSrc = "class E { enum Policy { ALPHA, BETA, GAMMA } }";
        String listPy = "POLICIES = [\"ALPHA\", \"BETA\", \"GAMMA\"]\nOTHER = [\"X\"]\n";
        assertTrue("the driver's constants are read from an enum",
                driverPolicies(enumSrc).equals(List.of("ALPHA", "BETA", "GAMMA")));
        assertTrue("the scorer's strings are read from its list",
                scorerPolicies(listPy).equals(List.of("ALPHA", "BETA", "GAMMA")));
        assertTrue("the same names in a different order are the same policies",
                policyProblem(enumSrc, "POLICIES = ['GAMMA', 'ALPHA', 'BETA']") == null);
        assertTrue("a policy the scorer lacks is a problem and is named",
                String.valueOf(policyProblem(enumSrc, "POLICIES = [\"ALPHA\", \"BETA\"]")).contains("GAMMA"));
        assertTrue("a policy the driver lacks is a problem and is named",
                String.valueOf(policyProblem(enumSrc, "POLICIES = [\"ALPHA\", \"BETA\", \"GAMMA\", \"DELTA\"]")).contains("DELTA"));
        assertTrue("a driver with no enum is a problem", policyProblem("class E { }", listPy) != null);
        assertTrue("a scorer with no list is a problem", policyProblem(enumSrc, "x = 1") != null);

        // then the tree's own
        String driver = read(toolsDir().resolve("Ensemble.java"));
        String scorer = read(toolsDir().resolve("score.py"));
        List<String> policies = driverPolicies(driver);
        System.out.println("(the driver names " + policies + "; the scorer names " + scorerPolicies(scorer) + ")");
        assertTrue("the driver names at least one policy", !policies.isEmpty());
        assertTrue("the scorer names at least one policy", !scorerPolicies(scorer).isEmpty());
        String problem = policyProblem(driver, scorer);
        assertTrue("the driver's policies are the scorer's", problem == null, problem);
        return policies;
    }

    /* ==================== 4. the baseline reads =========================== */

    static final Pattern BASELINE_NAME = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.md");
    /** A stored score's verdict line: "criteria passed: N / M". */
    static final Pattern CRITERIA = Pattern.compile("(?m)^criteria passed:\\s*(\\d+)\\s*/\\s*(\\d+)\\b");
    static final Pattern JSON_BLOCK = Pattern.compile("(?s)```json\\s*\\R(.*?)\\R\\s*```");

    /** The three numbers of x.y.z, or null when it is not one. */
    static int[] versionOf(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(s);
        if (!m.matches()) return null;
        return new int[] {Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))};
    }

    /** Negative when a is the older version, by the numbers and not by the letters ("0.8.9" is older than "0.8.11"). */
    static int compareVersions(String a, String b) {
        int[] x = versionOf(a), y = versionOf(b);
        for (int i = 0; i < 3; i++) if (x[i] != y[i]) return Integer.compare(x[i], y[i]);
        return 0;
    }

    /** The file name of the newest baseline among these names, or null when none is named x.y.z.md. */
    static String newestBaseline(List<String> names) {
        return newestBaseline(names, null);
    }

    /** EN2's untagged, data-backed baseline supersedes older literal-world scores until a later version is scored. */
    static String newestBaseline(List<String> names, String en2Version) {
        String best = null;
        for (String n : names) {
            Matcher m = BASELINE_NAME.matcher(n);
            if (!m.matches()) continue;
            String v = n.substring(0, n.length() - ".md".length());
            if (best == null || compareVersions(v, best.substring(0, best.length() - ".md".length())) > 0) best = n;
        }
        if (names.contains("EN2.md") && versionOf(en2Version) != null
                && (best == null || compareVersions(en2Version, best.substring(0, best.length() - 3)) >= 0)) return "EN2.md";
        return best;
    }

    private static boolean isNumber(JsonElement e) {
        return e != null && e.isJsonPrimitive() && ((JsonPrimitive) e).isNumber();
    }

    private static boolean isInteger(JsonElement e) {
        if (!isNumber(e)) return false;
        double d = e.getAsDouble();
        return d == Math.rint(d) && !Double.isInfinite(d);
    }

    /** Checks the verdict and JSON seeds, takeoffs and per-policy population, finding and audit fields. */
    static String baselineProblem(String text, List<String> policies) {
        if (text == null) return "the page cannot be read";
        if (policies == null || policies.isEmpty()) return "no policies to hold the page to";

        Matcher c = CRITERIA.matcher(text);
        if (!c.find()) return "no \"criteria passed: N/M\" line";
        long passed = Long.parseLong(c.group(1)), of = Long.parseLong(c.group(2));
        if (of < 1 || passed > of) return "\"criteria passed: " + passed + "/" + of + "\" cannot be a verdict";

        Matcher b = JSON_BLOCK.matcher(text);
        if (!b.find()) return "no json block";
        JsonElement root;
        try {
            root = JsonParser.parseString(b.group(1));
        } catch (JsonParseException e) {
            return "the json block does not parse: " + e.getMessage();
        }
        if (!root.isJsonObject()) return "the json block is not an object";
        JsonObject o = root.getAsJsonObject();
        if (!isInteger(o.get("seeds")) || o.get("seeds").getAsInt() < 1) return "\"seeds\" is not an integer of 1 or more";
        if (!isInteger(o.get("takeoffs"))) return "\"takeoffs\" is not an integer";
        if (o.get("policies") == null || !o.get("policies").isJsonObject()) return "\"policies\" is not an object";
        JsonObject ps = o.getAsJsonObject("policies");
        for (String p : policies) {
            if (!ps.has(p) || !ps.get(p).isJsonObject()) return "policy " + p + " is missing from \"policies\"";
            JsonObject one = ps.getAsJsonObject(p);
            if (!isNumber(one.get("final_pop_median")) || one.get("final_pop_median").getAsDouble() <= 0)
                return p + ": \"final_pop_median\" is not a number above 0";
            if (!isNumber(one.get("runs_with_findings"))) return p + ": \"runs_with_findings\" is not a number";
            if (!isNumber(one.get("audit_months_failed"))) return p + ": \"audit_months_failed\" is not a number";
        }
        return null;
    }

    /** A baseline page with these two parts of it: the verdict line and the json. */
    static String canned(String verdictLine, String json) {
        return "# Ensemble baseline: 9.9.9\n\nSome prose.\n\n| a | b |\n|---|---|\n| 1 | 2 |\n\n"
                + verdictLine + "\ntime per run (median s): AUTO2 1.0\n\n## summary.json\n\nWhat the next run reads.\n\n"
                + "```json\n" + json + "\n```\n";
    }

    /** A canned page's verdict line, for the reader's fixtures. */
    static final String VERDICT = "criteria passed: 5/6 (vacuous, not counted: 0); missing runs: none; short runs: none";
    /** ...and its JSON block, sound as it stands; a fixture edits it into a page the reader should refuse. */
    static final String JSON = """
            {
             "seeds": 4,
             "policies": {
              "ALPHA": {"final_pop_median": 1000.5, "runs_with_findings": 0, "audit_months_failed": 0},
              "BETA": {"final_pop_median": 20.0, "runs_with_findings": 1, "audit_months_failed": 2}
             },
             "takeoffs": 3
            }""";

    /** Changes canned JSON, failing the fixture if its replacement finds nothing. */
    static String withJson(String from, String to) {
        if (!JSON.contains(from)) {
            fails++;
            System.out.printf("%-68s %s%n", "the fixture finds " + from.trim() + " to change", "FAIL");
        }
        return canned(VERDICT, JSON.replace(from, to));
    }

    static void baselineReads(List<String> policies) throws IOException {
        System.out.println("\n--- the baseline is there and reads ---");

        // the reader on canned pages: each rejection has its own cause
        List<String> two = List.of("ALPHA", "BETA");
        assertTrue("a sound canned page is accepted", baselineProblem(canned(VERDICT, JSON), two) == null,
                baselineProblem(canned(VERDICT, JSON), two));
        String missing = baselineProblem(canned(VERDICT, JSON), List.of("ALPHA", "BETA", "GAMMA"));
        assertTrue("a page missing one policy is rejected, and the policy is named",
                missing != null && missing.contains("GAMMA"), missing);
        assertTrue("a page with no json block is rejected",
                baselineProblem(canned(VERDICT, "").replace("```json", "```text"), two) != null);
        assertTrue("a page with no verdict line is rejected",
                baselineProblem(canned("criteria are fine", JSON), two) != null);
        assertTrue("a page whose json does not parse is rejected",
                baselineProblem(canned(VERDICT, JSON.replace("\"takeoffs\": 3", "\"takeoffs\": ")), two) != null);
        assertTrue("a page with no seeds is rejected",
                baselineProblem(withJson("\"seeds\": 4,", ""), two) != null);
        assertTrue("a page with 0 seeds is rejected",
                baselineProblem(withJson("\"seeds\": 4", "\"seeds\": 0"), two) != null);
        assertTrue("a page with a fractional seed count is rejected",
                baselineProblem(withJson("\"seeds\": 4", "\"seeds\": 4.5"), two) != null);
        assertTrue("a page with no take-offs is rejected",
                baselineProblem(withJson(",\n \"takeoffs\": 3", ""), two) != null);
        assertTrue("a page whose median population is 0 is rejected",
                baselineProblem(withJson("1000.5", "0"), two) != null);
        assertTrue("a page whose median population is text is rejected",
                baselineProblem(withJson("1000.5", "\"1000.5\""), two) != null);
        assertTrue("a page with no findings count is rejected",
                baselineProblem(withJson("\"runs_with_findings\": 1, ", ""), two) != null);
        assertTrue("a page with no audit count is rejected",
                baselineProblem(withJson(", \"audit_months_failed\": 2", ""), two) != null);
        assertTrue("a page held to no policies is rejected, not passed", baselineProblem(canned(VERDICT, JSON), List.of()) != null);

        // the version order is the numbers' and not the letters'
        assertTrue("0.8.9 is older than 0.8.11, though 9 sorts after 1", compareVersions("0.8.9", "0.8.11") < 0);
        assertTrue("0.10.0 is newer than 0.9.9", compareVersions("0.10.0", "0.9.9") > 0);
        assertTrue("the newest of several pages is the highest number",
                "0.8.11.md".equals(newestBaseline(List.of("0.8.9.md", "0.8.11.md", "0.8.10.md", "README.md", "0.9.md"))));
        assertTrue("a folder with no page named x.y.z.md has no newest", newestBaseline(List.of("README.md", "notes.txt")) == null);
        assertTrue("EN2 supersedes older and same-version scores", "EN2.md".equals(newestBaseline(List.of("0.9.110.md", "EN2.md"), "0.9.110")));
        assertTrue("a later data-backed version supersedes EN2", "0.9.111.md".equals(newestBaseline(List.of("0.9.111.md", "EN2.md"), "0.9.110")));
        assertTrue("EN2 needs a valid model version", newestBaseline(List.of("EN2.md"), "broken") == null);

        // the tree's own
        List<String> names = new ArrayList<>();
        Path dir = baselineDir();
        if (Files.isDirectory(dir)) {
            try (Stream<Path> list = Files.list(dir)) {
                list.forEach(p -> names.add(p.getFileName().toString()));
            }
        }
        String en2 = read(dir.resolve("EN2.md"));
        Matcher header = Pattern.compile("(?m)^# Ensemble baseline: (\\d+\\.\\d+\\.\\d+)$").matcher(en2 == null ? "" : en2);
        String en2Version = header.find() ? header.group(1) : null;
        if (en2 != null) assertTrue("EN2 names its model version", en2Version != null);
        String newest = newestBaseline(names, en2Version);
        assertTrue("docs/ensemble holds at least one page named x.y.z.md", newest != null, "looked in " + dir.toAbsolutePath());
        if (newest == null) return;
        String version = newest.equals("EN2.md") ? en2Version : newest.substring(0, newest.length() - ".md".length());
        System.out.println("(the newest baseline is " + newest + "; the game is " + GameVersion.VERSION + ")");
        assertTrue("the newest baseline is not newer than the game", compareVersions(version, GameVersion.VERSION) <= 0);
        String problem = baselineProblem(read(dir.resolve(newest)), policies);
        assertTrue("the newest baseline reads, with every policy in it", problem == null, problem);
    }

    /* ------------------------------------------------------------------
       THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT
       ------------------------------------------------------------------ */

    /** Calls a method that no class has: the change the guard exists for, made by hand. */
    private static final String BROKEN = """
            package ham.citybuildersim;

            final class EnsembleCheckBroken {
                static void call() { LongPlaytest.noSuchMethodForEnsembleCheck(); }
            }
            """;

    /** Calls a class that is there. */
    private static final String SOUND = """
            package ham.citybuildersim;

            final class EnsembleCheckSound {
                static Class<?> call() { return LongPlaytest.class; }
            }
            """;

    static void cleanUp(Path root) {
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }
}
