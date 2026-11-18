package ham.citybuildersim.tools;

import ham.citybuildersim.Credits;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Build EXE.bat's source archives and its check of the built image (CR1-J): the packaging JDK
 * checked against the pins, the complete corresponding sources fetched once into a cache outside
 * git and staged beside the exe, and the finished image held to every notice, terms file and
 * archive it must carry before anything is zipped.
 *
 *     java -cp target\CityBuilderSim-1.0-SNAPSHOT-executable.jar ham.citybuildersim.tools.Packaging jdk    <JDK folder>
 *     java -cp ...                                               ham.citybuildersim.tools.Packaging fetch  <cache folder>
 *     java -cp ...                                               ham.citybuildersim.tools.Packaging stage  <cache folder> <image folder>
 *     java -cp ...                                               ham.citybuildersim.tools.Packaging verify <image folder>
 *
 * Each exits 0 when all is well and 1, after a plain sentence for each lack, when not. The pins
 * are the jar's copy of tools/credits/source-archives.properties, the same the Credits page reads.
 *
 * WHY THIS EXISTS. The sold exe carried Oracle's runtime under the No-Fee Terms, which bar fees for
 * a product bundled with it, and OpenJFX under the GPL, whose section 3 asks for the complete
 * source to go with the binaries; the libraries' notices reached the Credits page (CR1-L) but the
 * runtime's, the source and Microsoft's terms for the C runtime files did not (CR1-J). A step in a
 * batch file that copies an archive fails silently, so the build ends in this check, which
 * PackagingCheck holds on fixtures: a missing archive, a changed one, a lost notice or a runtime
 * from another vendor each stop the build before the zip.
 */
public final class Packaging {

    private Packaging() { }

    /** A file name that is one of Microsoft's C or C++ runtime libraries, by its family's name. */
    public static final Pattern MICROSOFT_DLL = Pattern.compile("(?i)(?:msvcp|vcruntime|ucrtbase|api-ms-win-|concrt|vccorlib|vcomp|mfc)[^/\\\\]*\\.dll");

    /** The Microsoft files tools/credits/microsoft/NOTICE.txt names and whose terms were audited; another needs a new audit. */
    public static final Pattern MICROSOFT_COVERED = Pattern.compile("(?i)(?:msvcp140(?:_[12])?|vcruntime140(?:_1)?|ucrtbase|api-ms-win-[a-z0-9-]+)\\.dll");

    /** The words that open the GNU GPL, version 2, as OpenJDK's legal/java.base/LICENSE carries it. */
    public static final String GPL2 = "Version 2, June 1991";

    /** The words that mark Oracle's No-Fee Terms and Conditions, which bar the sold game; a runtime carrying them is refused. */
    public static final String NFTC = "No-Fee Terms and Conditions";

    /** The README written beside the archives. */
    public static final String README = "README.txt";

    /** How a check reads what is inside the built binaries; PackagingCheck's fixtures replace it. */
    public record Probes(Function<Path, Set<String>> runtimeStrings, Function<Path, String> fxVersion) { }

    /** The real probes: the runtime's java.base version constants, and the jar's OpenJFX build. */
    public static final Probes REAL = new Probes(Packaging::runtimeStrings, Packaging::fxVersion);

    public static void main(String[] args) throws Exception {
        String command = args.length == 0 ? "" : args[0];
        Properties pins = pins();
        List<String> problems = new ArrayList<>();
        switch (command) {
            case "jdk" -> { need(args, 2); problems = checkJdk(Path.of(args[1]), pins); }
            case "fetch" -> { need(args, 2); problems = fetch(Path.of(args[1]), pins); }
            case "stage" -> { need(args, 3); problems = stage(Path.of(args[1]), Path.of(args[2]), pins, Packaging::credit); }
            case "verify" -> { need(args, 2); problems = verify(Path.of(args[1]), pins, Packaging::credit, REAL); }
            default -> {
                System.out.println("usage: Packaging jdk <JDK> | fetch <cache> | stage <cache> <image> | verify <image>");
                System.exit(2);
            }
        }
        for (String p : problems) System.out.println("  " + p);
        if (!problems.isEmpty()) System.out.println("Packaging " + command + ": " + problems.size() + " problem" + (problems.size() == 1 ? "" : "s") + ".");
        else System.out.println("Packaging " + command + ": all in place.");
        System.exit(problems.isEmpty() ? 0 : 1);
    }

    private static void need(String[] args, int n) {
        if (args.length < n) {
            System.out.println("Packaging " + args[0] + ": missing an argument");
            System.exit(2);
        }
    }

    /** The jar's pins; the build stops without them. */
    public static Properties pins() {
        String text = credit(Credits.SOURCE_PINS);
        if (text == null) {
            System.out.println("Packaging: the jar carries no " + Credits.SOURCE_PINS + " - rebuild the project.");
            System.exit(1);
        }
        return Credits.properties(text);
    }

    /** A credits text from this jar, as the Credits page reads it; null when the jar lacks it. */
    public static String credit(String resource) {
        try (InputStream in = Packaging.class.getResourceAsStream(resource)) {
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /** The classpath texts the folder beside the exe carries, as legal/ followed by their path under credits/. */
    public static List<String> legalFiles() {
        List<String> out = new ArrayList<>();
        out.add(Credits.GSON);
        out.addAll(Credits.OPENJFX_FILES);
        out.addAll(Credits.MICROSOFT_FILES);
        out.add("/credits/sources.properties");
        out.add(Credits.SOURCE_PINS);
        return out;
    }

    /** The attribution files beside the data, each with the jar's copy it must equal (Build EXE.bat copies them). */
    public static final Map<String, String> DATA_NOTICES = Map.of(
            "data/earth-2-ATTRIBUTION.txt", Credits.EARTH,
            "data/world-ATTRIBUTION.txt", Credits.NATIONS);

    /* ----- the packaging JDK ----- */

    /** A release file's KEY="value" lines. */
    public static Map<String, String> release(Path file) throws IOException {
        Map<String, String> out = new LinkedHashMap<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            String value = line.substring(eq + 1).trim();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) value = value.substring(1, value.length() - 1);
            out.put(line.substring(0, eq).trim(), value);
        }
        return out;
    }

    /** The packaging JDK is the one whose source the pins name: vendor, vendor version and Java version, exactly. */
    public static List<String> checkJdk(Path jdk, Properties pins) throws IOException {
        List<String> problems = new ArrayList<>();
        Path file = jdk.resolve("release");
        if (!Files.isRegularFile(file)) {
            problems.add(jdk + " has no release file, so it is not a JDK this build can name.");
            return problems;
        }
        Map<String, String> release = release(file);
        expect(problems, "its IMPLEMENTOR", release.get("IMPLEMENTOR"), pins.getProperty("runtime.implementor"));
        expect(problems, "its IMPLEMENTOR_VERSION", release.get("IMPLEMENTOR_VERSION"), pins.getProperty("runtime.vendorVersion"));
        expect(problems, "its JAVA_VERSION", release.get("JAVA_VERSION"), pins.getProperty("runtime.javaVersion"));
        // The scripts that built it (the CR1-J review): the temurin-build commit its release file names.
        expect(problems, "its BUILD_SOURCE", release.get("BUILD_SOURCE"), "git:" + pins.getProperty("build.version"));
        expect(problems, "its BUILD_SOURCE_REPO", release.get("BUILD_SOURCE_REPO"), pins.getProperty("build.repo"));
        if (!problems.isEmpty()) problems.add("The source archives pinned in tools/credits/source-archives.properties are for "
                + pins.getProperty("runtime.vendorVersion") + ": package with that JDK, or pin the source archives of this one.");
        problems.addAll(pinProblems(pins));
        return problems;
    }

    /**
     * The pins agree with themselves: each archive's file and URL name its version (a "+" written
     * "_" in a file name, "%2B" in a URL), and the runtime's vendor and Java versions are its
     * version's. A version bumped alone, the file and URL left behind, is a problem (the CR1-J review).
     */
    public static List<String> pinProblems(Properties pins) {
        List<String> problems = new ArrayList<>();
        for (String a : Credits.archives(pins)) {
            String v = pins.getProperty(a + ".version", ""), file = pins.getProperty(a + ".file", ""), url = pins.getProperty(a + ".url", "");
            if (v.isBlank()) { problems.add("the pins give " + a + " no version."); continue; }
            if (!file.contains(v) && !file.contains(v.replace('+', '_')))
                problems.add("the pinned " + a + ".file, " + file + ", does not name its version " + v + ".");
            if (!url.contains(v.replace("+", "%2B")))
                problems.add("the pinned " + a + ".url, " + url + ", does not name its version " + v + ".");
        }
        String version = pins.getProperty("runtime.version", "");
        if (!pins.getProperty("runtime.vendorVersion", "").endsWith("-" + version))
            problems.add("the pinned runtime.vendorVersion, " + pins.getProperty("runtime.vendorVersion") + ", is not runtime.version " + version + "'s.");
        if (!version.startsWith(pins.getProperty("runtime.javaVersion", "?") + "+"))
            problems.add("the pinned runtime.javaVersion, " + pins.getProperty("runtime.javaVersion") + ", is not runtime.version " + version + "'s.");
        return problems;
    }

    private static void expect(List<String> problems, String what, String got, String want) {
        if (want == null || !want.equals(got)) problems.add(what + " is " + (got == null ? "absent" : "\"" + got + "\"") + ", not \"" + want + "\".");
    }

    /* ----- the archives ----- */

    /** A file's SHA-256 in lower-case hex, read as a stream. */
    public static String sha256(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1 << 16];
            for (int n; (n = in.read(buffer)) > 0; ) md.update(buffer, 0, n);
            return HexFormat.of().formatHex(md.digest());
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** True when the file is there and its SHA-256 is the pin's. */
    public static boolean pinned(Path file, String sha) throws IOException {
        return Files.isRegularFile(file) && sha256(file).equalsIgnoreCase(sha);
    }

    /** Fetches each pinned archive the cache lacks or holds changed, through the system's proxy settings; a download whose hash differs is thrown away. */
    public static List<String> fetch(Path cache, Properties pins) throws IOException {
        List<String> problems = new ArrayList<>();
        Files.createDirectories(cache);
        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                .proxy(ProxySelector.getDefault()).connectTimeout(Duration.ofSeconds(30)).build();
        for (String a : Credits.archives(pins)) {
            String name = pins.getProperty(a + ".file"), sha = pins.getProperty(a + ".sha256"), url = pins.getProperty(a + ".url");
            Path file = cache.resolve(name);
            if (pinned(file, sha)) {
                System.out.println("  " + name + " is in place in " + cache + ".");
                continue;
            }
            Path part = cache.resolve(name + ".download");
            System.out.println("  " + name + (Files.exists(file) ? " differs from its pin, so it is fetched again" : " is fetched") + " from " + url + " ...");
            try {
                HttpResponse<Path> response = http.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofMinutes(30)).build(),
                        HttpResponse.BodyHandlers.ofFile(part));
                if (response.statusCode() != 200) {
                    problems.add(name + " could not be fetched: the server answered " + response.statusCode() + ".");
                } else if (!pinned(part, sha)) {
                    problems.add(name + " was fetched but its SHA-256 is " + sha256(part) + ", not the pinned " + sha + ", so it was thrown away.");
                } else {
                    Files.move(part, file, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("  " + name + " is in place, " + Files.size(file) / 1_000_000 + " MB.");
                }
            } catch (IOException | InterruptedException e) {
                problems.add(name + " could not be fetched (" + e + "); put it in " + cache + " by hand from " + url + ".");
            } finally {
                Files.deleteIfExists(part);
            }
        }
        return problems;
    }

    /**
     * Copies each pinned archive from the cache into the image's source folder with the README, and
     * the jar's legal texts into its legal folder; an archive missing from the cache or changed there
     * is a problem, and nothing is copied for it.
     */
    public static List<String> stage(Path cache, Path image, Properties pins, Function<String, String> credits) throws IOException {
        List<String> problems = new ArrayList<>();
        Path source = image.resolve(Credits.SOURCE_FOLDER);
        Files.createDirectories(source);
        for (String a : Credits.archives(pins)) {
            String name = pins.getProperty(a + ".file");
            Path file = cache.resolve(name);
            if (!Files.isRegularFile(file)) { problems.add(name + " is not in " + cache + ": run Packaging fetch first."); continue; }
            if (!pinned(file, pins.getProperty(a + ".sha256"))) { problems.add(name + " in " + cache + " differs from its pin."); continue; }
            Files.copy(file, source.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.writeString(source.resolve(README), Credits.sourceReadme(pins), StandardCharsets.UTF_8);
        for (String resource : legalFiles()) {
            String text = credits.apply(resource);
            if (text == null) { problems.add("the jar lacks " + resource + "."); continue; }
            Path to = image.resolve(Credits.LEGAL_FOLDER).resolve(resource.substring("/credits/".length()));
            Files.createDirectories(to.getParent());
            Files.writeString(to, text, StandardCharsets.UTF_8);
        }
        return problems;
    }

    /* ----- the check of the built image ----- */

    /**
     * Everything the image must carry before it is zipped: Temurin's runtime and its notices with
     * every reference inside its legal folder, no Oracle No-Fee Terms, Microsoft's terms for every
     * Microsoft library it carries, the libraries' and data's texts beside the exe as the jar holds
     * them, each pinned source archive unchanged with its README, and OpenJFX's build the archive's.
     */
    public static List<String> verify(Path image, Properties pins, Function<String, String> credits, Probes probes) throws IOException {
        List<String> problems = new ArrayList<>(pinProblems(pins));
        Path runtime = image.resolve("runtime");

        // The runtime: Temurin, the pinned version.
        Path releaseFile = runtime.resolve("release");
        if (!Files.isRegularFile(releaseFile)) {
            problems.add("runtime/release is not there.");
        } else {
            String version = release(releaseFile).get("JAVA_VERSION");
            if (!pins.getProperty("runtime.javaVersion", "").equals(version))
                problems.add("runtime/release says JAVA_VERSION " + version + ", not the pinned " + pins.getProperty("runtime.javaVersion") + ".");
        }
        Set<String> strings = probes.runtimeStrings().apply(runtime);
        for (String key : List.of("runtime.implementor", "runtime.vendorVersion")) {
            String want = pins.getProperty(key);
            if (strings == null || !strings.contains(want))
                problems.add("the runtime's java.base does not name \"" + want + "\" (" + key + "): it is not the runtime whose source is pinned.");
        }
        if (!Files.isRegularFile(runtime.resolve("NOTICE"))) problems.add("runtime/NOTICE, the runtime distribution's notice, is not there.");

        // Its legal folder, whole, every reference inside it, under the GPL.
        List<String> legalProblems = new ArrayList<>();
        List<Credits.LegalFile> legal = Credits.runtimeLegal(runtime, legalProblems);
        for (String p : legalProblems) problems.add("runtime notices: " + p);
        if (legal != null) {
            Credits.LegalFile licence = legal.stream().filter(f -> f.name().equals("legal/java.base/LICENSE")).findFirst().orElse(null);
            if (licence == null) problems.add("runtime/legal/java.base/LICENSE is not there.");
            else if (!licence.text().contains(GPL2)) problems.add("runtime/legal/java.base/LICENSE is not the GNU GPL, version 2.");
            for (Credits.LegalFile f : legal) if (f.text().contains(NFTC)) problems.add("runtime/" + f.name() + " carries Oracle's " + NFTC + ", which bar a sold game.");
        }

        // Microsoft's libraries: each one carried is one the notice names.
        List<String> microsoft = microsoftLibraries(image, problems);
        for (String dll : microsoft) {
            String name = dll.substring(dll.lastIndexOf('/') + 1);
            if (!MICROSOFT_COVERED.matcher(name).matches())
                problems.add(dll + " is a Microsoft library the notice does not name: audit its terms and add it to tools/credits/microsoft.");
        }

        // The texts beside the exe, each as the jar holds it.
        for (String resource : legalFiles()) {
            String want = credits.apply(resource);
            Path file = image.resolve(Credits.LEGAL_FOLDER).resolve(resource.substring("/credits/".length()));
            same(problems, image, file, want, resource);
        }
        for (Map.Entry<String, String> e : DATA_NOTICES.entrySet()) same(problems, image, image.resolve(e.getKey()), credits.apply(e.getValue()), e.getValue());

        // The source archives, unchanged, and their README.
        Path source = image.resolve(Credits.SOURCE_FOLDER);
        for (String a : Credits.archives(pins)) {
            String name = pins.getProperty(a + ".file");
            Path file = source.resolve(name);
            if (!Files.isRegularFile(file)) problems.add(Credits.SOURCE_FOLDER + "/" + name + " is not there: no build ships without its source.");
            else if (!pinned(file, pins.getProperty(a + ".sha256"))) problems.add(Credits.SOURCE_FOLDER + "/" + name + " differs from its pinned SHA-256.");
        }
        if (Credits.archives(pins).isEmpty()) problems.add("the pins name no source archive.");
        same(problems, image, source.resolve(README), Credits.sourceReadme(pins), "the README the pins make");

        // OpenJFX's build in the jar is the one whose source is pinned.
        List<Path> jars = new ArrayList<>();
        Path app = image.resolve("app");
        if (Files.isDirectory(app)) try (Stream<Path> s = Files.list(app)) { s.filter(p -> p.toString().endsWith(".jar")).forEach(jars::add); }
        if (jars.size() != 1) problems.add("app/ holds " + jars.size() + " jars, not the game's one.");
        else {
            String fx = probes.fxVersion().apply(jars.get(0));
            if (!pins.getProperty("openjfx.version", "").equals(fx))
                problems.add("the jar's OpenJFX is build " + fx + ", not the pinned " + pins.getProperty("openjfx.version") + " whose source ships.");
        }
        return problems;
    }

    /** A file beside the exe holds the jar's text exactly. */
    private static void same(List<String> problems, Path image, Path file, String want, String what) throws IOException {
        String name = image.relativize(file).toString().replace('\\', '/');
        if (want == null) problems.add("the jar lacks " + what + ", so " + name + " cannot be checked.");
        else if (!Files.isRegularFile(file)) problems.add(name + " is not there.");
        else if (!Files.readString(file, StandardCharsets.UTF_8).equals(want)) problems.add(name + " differs from " + what + ".");
    }

    /** Microsoft's libraries the image carries: in the runtime's bin, and inside the game's jar. */
    public static List<String> microsoftLibraries(Path image) throws IOException {
        return microsoftLibraries(image, new ArrayList<>());
    }

    /** The same, a jar that cannot be opened named in {@code problems}: its libraries cannot be seen, so the image is not fit. */
    public static List<String> microsoftLibraries(Path image, List<String> problems) throws IOException {
        List<String> out = new ArrayList<>();
        Path bin = image.resolve("runtime").resolve("bin");
        if (Files.isDirectory(bin)) try (Stream<Path> s = Files.walk(bin)) {
            s.filter(Files::isRegularFile).filter(p -> MICROSOFT_DLL.matcher(p.getFileName().toString()).matches())
                    .forEach(p -> out.add("runtime/" + image.resolve("runtime").relativize(p).toString().replace('\\', '/')));
        }
        Path app = image.resolve("app");
        if (Files.isDirectory(app)) try (Stream<Path> s = Files.list(app)) {
            for (Path jar : s.filter(p -> p.toString().endsWith(".jar")).toList()) {
                try (JarFile j = new JarFile(jar.toFile())) {
                    j.stream().map(e -> e.getName()).filter(n -> MICROSOFT_DLL.matcher(n.substring(n.lastIndexOf('/') + 1)).matches())
                            .forEach(n -> out.add("app/" + jar.getFileName() + "!/" + n));
                } catch (IOException e) {
                    problems.add("app/" + jar.getFileName() + " cannot be opened (" + e.getMessage() + "), so the Microsoft libraries in it cannot be checked.");
                }
            }
        }
        return out;
    }

    /* ----- the probes ----- */

    /**
     * The string constants of java.lang.VersionProps in a runtime image's java.base, read through
     * that image's own jrt file system: its vendor ("Eclipse Adoptium") and vendor version
     * ("Temurin-21.0.13+9") among them. The image's release file names neither, since jlink writes
     * only JAVA_VERSION and MODULES there. Null when the image cannot be read.
     */
    public static Set<String> runtimeStrings(Path runtime) {
        try (FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"), Map.of("java.home", runtime.toAbsolutePath().toString()))) {
            return constants(Files.readAllBytes(fs.getPath("/modules/java.base/java/lang/VersionProps.class")));
        } catch (Exception e) {
            System.out.println("  the runtime at " + runtime + " could not be read: " + e);
            return null;
        }
    }

    /** A class file's UTF-8 constants. */
    public static Set<String> constants(byte[] classFile) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(classFile));
        if (in.readInt() != 0xCAFEBABE) throw new IOException("not a class file");
        in.readUnsignedShort();
        in.readUnsignedShort();
        int count = in.readUnsignedShort();
        Set<String> out = new LinkedHashSet<>();
        for (int i = 1; i < count; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1 -> out.add(in.readUTF());
                case 3, 4 -> in.readInt();
                case 5, 6 -> { in.readLong(); i++; }
                case 7, 8, 16, 19, 20 -> in.readUnsignedShort();
                case 9, 10, 11, 12, 17, 18 -> in.readInt();
                case 15 -> { in.readUnsignedByte(); in.readUnsignedShort(); }
                default -> throw new IOException("constant tag " + tag);
            }
        }
        return out;
    }

    /** OpenJFX's build in a jar, as its own VersionInfo says ("21+31"); null when the jar has none. */
    public static String fxVersion(Path jar) {
        try (URLClassLoader loader = new URLClassLoader(new URL[] {jar.toUri().toURL()}, null)) {
            Class<?> c = Class.forName("com.sun.javafx.runtime.VersionInfo", true, loader);
            return (String) c.getMethod("getRuntimeVersion").invoke(null);
        } catch (Exception | LinkageError e) {
            System.out.println("  OpenJFX's build in " + jar + " could not be read: " + e);
            return null;
        }
    }
}
