package ham.citybuildersim;

import ham.citybuildersim.tools.Packaging;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

/**
 * The built image carries Temurin's runtime with its notices, Microsoft's terms for the C runtime
 * files, the libraries' texts and the complete corresponding source archives, unchanged, and a
 * build that lacks any of them stops before the zip (CR1-J).
 *
 * WHY THIS EXISTS. The sold exe shipped Oracle's runtime, whose No-Fee Terms bar fees for a product
 * bundled with it, and OpenJFX's binaries without the source GPL v2 section 3 asks for. Jerus chose
 * (2026-11-17) Temurin and the source archives beside the exe. Build EXE.bat's copy steps fail
 * silently, so the build ends in tools.Packaging's check; this harness holds that check to fixture
 * images that cause each failure, holds the pins to the binaries the build ships, and holds the
 * batch file to the order that makes the check a gate. No network: a fetch is tried only from an
 * address that refuses it.
 *
 * What it has to prove:
 *
 *   1. A COMPLETE IMAGE PASSES. Staged from a cache holding every pinned archive, with Temurin's
 *      runtime and notices, the check finds nothing.
 *   2. A MISSING OR CHANGED SOURCE ARCHIVE STOPS THE BUILD. Gone from the image, changed in it,
 *      absent from or changed in the cache, or unfetchable: each is a problem naming the archive,
 *      and a failed fetch leaves no partial file.
 *   3. A LOST NOTICE OR TERMS FILE STOPS THE BUILD. Microsoft's notice or terms, a library's
 *      licence changed, a data attribution, the runtime's NOTICE, the README, a dangling
 *      "Please see" in the runtime's legal folder: each is a problem.
 *   4. A RUNTIME THAT IS NOT THE PINNED TEMURIN STOPS THE BUILD. Another vendor or version, Oracle's
 *      No-Fee Terms, a non-GPL java.base licence, another OpenJFX build, a Microsoft library the
 *      notice does not name.
 *   5. THE PACKAGING JDK IS HELD TO THE PINS: vendor, vendor version and Java version, exactly, and
 *      the temurin-build commit its release file names (BUILD_SOURCE), whose archive ships too.
 *   6. THE PINS ARE THE SHIPPED BINARIES', AND AGREE WITH THEMSELVES. OpenJFX's pinned build is the
 *      one in the dependency jars, pom.xml's version; the probes read the vendor from a real
 *      runtime; each archive's file and URL name its version, so a version bumped alone fails.
 *   7. BUILD EXE.BAT NAMES ITS JDK AND CHECKS THE IMAGE BEFORE THE ZIP. No jpackage from PATH or
 *      Program Files; Temurin's own jlink and jpackage; fetch before jpackage, stage and verify
 *      after it and before the zip; every stop from the runtime step on removes the image and this
 *      version's zip; no "if errorlevel"; the exe's copyright the game's; its jlink line the README's.
 */
public class PackagingCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    /** True when some problem contains every one of the words. */
    static boolean names(List<String> problems, String... words) {
        for (String p : problems) {
            boolean all = true;
            for (String w : words) if (!p.contains(w)) all = false;
            if (all) return true;
        }
        return false;
    }

    /** The jar's credits texts, as Build EXE.bat's java -cp reads them. */
    static final Function<String, String> CREDITS = Packaging::credit;

    /** Pins of two small fixture archives, the rest as the real pins have them. */
    static Properties fixturePins(Path cache) throws IOException {
        Properties real = Packaging.pins();
        Properties pins = new Properties();
        pins.putAll(real);
        for (String a : Credits.archives(real)) {
            Path file = cache.resolve(real.getProperty(a + ".file"));
            Files.writeString(file, "the " + a + " source, a fixture\n", StandardCharsets.UTF_8);
            pins.setProperty(a + ".sha256", Packaging.sha256(file));
            // An address that refuses the fetch, the real URL's path kept, so the URL still names its version.
            pins.setProperty(a + ".url", "http://127.0.0.1:9" + java.net.URI.create(real.getProperty(a + ".url")).getRawPath());
        }
        return pins;
    }

    /** Probes that say the runtime is the pinned Temurin and the jar's OpenJFX the pinned build. */
    static Packaging.Probes pinnedProbes(Properties pins) {
        return new Packaging.Probes(rt -> Set.of(pins.getProperty("runtime.implementor"), pins.getProperty("runtime.vendorVersion")),
                jar -> pins.getProperty("openjfx.version"));
    }

    /** An image as Build EXE.bat leaves it before staging: runtime, notices, legal folder, a jar, the data's credits. */
    static Path image(Properties pins) throws IOException {
        Path image = TempDirs.create("packaging-image");
        Path runtime = image.resolve("runtime");
        Files.createDirectories(runtime.resolve("bin"));
        Files.writeString(runtime.resolve("release"), "JAVA_VERSION=\"" + pins.getProperty("runtime.javaVersion") + "\"\nMODULES=\"java.base java.desktop\"\n");
        Files.writeString(runtime.resolve("NOTICE"), "# Notices for Eclipse Temurin\n");
        Path base = runtime.resolve("legal").resolve("java.base");
        Path desktop = runtime.resolve("legal").resolve("java.desktop");
        Files.createDirectories(base);
        Files.createDirectories(desktop);
        Files.writeString(base.resolve("LICENSE"), CREDITS.apply(Credits.OPENJFX));
        Files.writeString(base.resolve("zlib.md"), "## zlib\n\nthe zlib licence\n");
        Files.writeString(desktop.resolve("LICENSE"), "Please see ..\\java.base\\LICENSE\r\n");
        Files.writeString(desktop.resolve("freetype.md"), "## FreeType\n");
        for (String dll : List.of("vcruntime140.dll", "vcruntime140_1.dll", "msvcp140.dll", "ucrtbase.dll", "api-ms-win-crt-runtime-l1-1-0.dll", "java.dll"))
            Files.write(runtime.resolve("bin").resolve(dll), new byte[] {'M', 'Z'});
        Files.createDirectories(image.resolve("app"));
        try (OutputStream out = Files.newOutputStream(image.resolve("app").resolve("CityBuilderSim-1.0-SNAPSHOT-executable.jar"));
                JarOutputStream jar = new JarOutputStream(out)) {
            for (String entry : List.of("msvcp140_2.dll", "glass.dll", "api-ms-win-core-file-l1-1-0.dll")) {
                jar.putNextEntry(new JarEntry(entry));
                jar.write(new byte[] {'M', 'Z'});
                jar.closeEntry();
            }
        }
        Files.createDirectories(image.resolve("data"));
        Files.writeString(image.resolve("data").resolve("earth-2-ATTRIBUTION.txt"), CREDITS.apply(Credits.EARTH), StandardCharsets.UTF_8);
        Files.writeString(image.resolve("data").resolve("world-ATTRIBUTION.txt"), CREDITS.apply(Credits.NATIONS), StandardCharsets.UTF_8);
        return image;
    }

    /** A complete, staged fixture image and its cache and pins. */
    record Fixture(Path cache, Path image, Properties pins) { }

    static Fixture staged() throws IOException {
        Path cache = TempDirs.create("packaging-cache");
        Properties pins = fixturePins(cache);
        Path image = image(pins);
        List<String> staged = Packaging.stage(cache, image, pins, CREDITS);
        if (!staged.isEmpty()) throw new IllegalStateException("staging the fixture: " + staged);
        return new Fixture(cache, image, pins);
    }

    static List<String> verify(Fixture f) throws IOException {
        return Packaging.verify(f.image(), f.pins(), CREDITS, pinnedProbes(f.pins()));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== PackagingCheck: the image ships Temurin, every notice and terms file and the source archives, or no build (CR1-J) ===");

        System.out.println("--- a complete image passes ---");
        Fixture f = staged();
        List<String> clean = verify(f);
        check("a complete image has no problem", clean.isEmpty(), clean.toString());
        Properties pins = f.pins();
        for (String a : Credits.archives(pins)) {
            String name = pins.getProperty(a + ".file");
            check("source/" + name + " is staged unchanged", Packaging.pinned(f.image().resolve(Credits.SOURCE_FOLDER).resolve(name), pins.getProperty(a + ".sha256")), null);
        }
        check("source/README.txt is the pins' README", Files.readString(f.image().resolve("source").resolve("README.txt")).equals(Credits.sourceReadme(pins)), null);
        for (String resource : Packaging.legalFiles())
            check("legal/" + resource.substring("/credits/".length()) + " is the jar's text", CREDITS.apply(resource) != null
                    && CREDITS.apply(resource).equals(Files.readString(f.image().resolve("legal").resolve(resource.substring("/credits/".length())))), null);
        check("the image's Microsoft libraries are found in runtime/bin and in the jar", Packaging.microsoftLibraries(f.image()).size() == 7
                && Packaging.microsoftLibraries(f.image()).contains("app/CityBuilderSim-1.0-SNAPSHOT-executable.jar!/msvcp140_2.dll"),
                Packaging.microsoftLibraries(f.image()).toString());

        System.out.println("--- a missing or changed source archive stops the build ---");
        for (String a : Credits.archives(pins)) {
            String name = pins.getProperty(a + ".file");
            Fixture g = staged();
            Files.delete(g.image().resolve("source").resolve(name));
            check("without source/" + name + " the image fails, naming it", names(verify(g), name, "not there"), verify(g).toString());
            g = staged();
            Files.writeString(g.image().resolve("source").resolve(name), "changed\n");
            check("a changed source/" + name + " fails its SHA-256", names(verify(g), name, "differs"), verify(g).toString());
            Fixture h = staged();
            Files.delete(h.cache().resolve(name));
            Path other = TempDirs.create("packaging-image");
            List<String> stage = Packaging.stage(h.cache(), other, h.pins(), CREDITS);
            check("staging from a cache without " + name + " fails", names(stage, name, "is not in"), stage.toString());
            check("and the image it left fails the check", names(Packaging.verify(other, h.pins(), CREDITS, pinnedProbes(h.pins())), name, "not there"), null);
            Files.writeString(h.cache().resolve(name), "changed\n");
            stage = Packaging.stage(h.cache(), TempDirs.create("packaging-image"), h.pins(), CREDITS);
            check("staging from a cache holding a changed " + name + " fails", names(stage, name, "differs from its pin"), stage.toString());
            Files.delete(h.cache().resolve(name));
            List<String> fetched = Packaging.fetch(h.cache(), h.pins());
            check("an archive that cannot be fetched is a problem naming it", names(fetched, name, "could not be fetched"), fetched.toString());
            check("and no partial download is left in the cache", !Files.exists(h.cache().resolve(name + ".download")) && !Files.exists(h.cache().resolve(name)), null);
        }
        List<String> inPlace = Packaging.fetch(f.cache(), f.pins());
        check("a cache holding every pinned archive fetches nothing and passes", inPlace.isEmpty(), inPlace.toString());

        System.out.println("--- a lost notice or terms file stops the build ---");
        for (String lost : List.of("legal/microsoft/NOTICE.txt", "legal/microsoft/LICENSE-vc-runtime-2015-2022.txt", "legal/microsoft/LICENSE-windows-sdk.txt", "legal/openjfx/LICENSE",
                "data/world-ATTRIBUTION.txt", "data/earth-2-ATTRIBUTION.txt", "runtime/NOTICE", "source/README.txt", "legal/source-archives.properties")) {
            Fixture g = staged();
            Files.delete(g.image().resolve(lost));
            List<String> p = verify(g);
            check("without " + lost + " the image fails", names(p, lost.equals("runtime/NOTICE") ? "runtime/NOTICE" : lost), p.toString());
        }
        Fixture changed = staged();
        Files.writeString(changed.image().resolve("legal/gson/LICENSE"), "a shorter licence\n");
        check("a changed library licence fails", names(verify(changed), "legal/gson/LICENSE", "differs"), null);
        Fixture dangling = staged();
        Files.writeString(dangling.image().resolve("runtime/legal/java.desktop/ASSEMBLY_EXCEPTION"), "Please see ..\\java.base\\ASSEMBLY_EXCEPTION\r\n");
        check("a \"Please see\" naming a file the legal folder lacks fails", names(verify(dangling), "java.desktop/ASSEMBLY_EXCEPTION", "lacks"), verify(dangling).toString());
        Fixture outside = staged();
        Files.writeString(outside.image().resolve("runtime/legal/java.desktop/LICENSE"), "Please see ..\\..\\release\r\n");
        check("a \"Please see\" naming a file outside the legal folder fails", names(verify(outside), "outside the legal folder"), null);
        Fixture noLegal = staged();
        deleteTree(noLegal.image().resolve("runtime/legal"));
        check("an image without runtime/legal fails", names(verify(noLegal), "legal", "not there"), verify(noLegal).toString());

        System.out.println("--- a runtime that is not the pinned Temurin stops the build ---");
        Fixture oracle = staged();
        List<String> p = Packaging.verify(oracle.image(), oracle.pins(), CREDITS,
                new Packaging.Probes(rt -> Set.of("Oracle Corporation", "21.0.4+8-LTS-274"), jar -> oracle.pins().getProperty("openjfx.version")));
        check("a runtime whose java.base names another vendor fails", names(p, oracle.pins().getProperty("runtime.implementor")), p.toString());
        check("and one without the pinned vendor version fails", names(p, oracle.pins().getProperty("runtime.vendorVersion")), null);
        p = Packaging.verify(oracle.image(), oracle.pins(), CREDITS, new Packaging.Probes(rt -> null, jar -> oracle.pins().getProperty("openjfx.version")));
        check("a runtime that cannot be read fails", names(p, "runtime.implementor"), null);
        Fixture old = staged();
        Files.writeString(old.image().resolve("runtime/release"), "JAVA_VERSION=\"21.0.4\"\n");
        check("a runtime/release of another Java version fails", names(verify(old), "JAVA_VERSION 21.0.4"), verify(old).toString());
        Fixture nftc = staged();
        Files.writeString(nftc.image().resolve("runtime/legal/java.base/LICENSE"), "Java SE Development Kit 21 - Oracle " + Packaging.NFTC + " (NFTC)\n");
        p = verify(nftc);
        check("Oracle's No-Fee Terms in the runtime's legal folder fail", names(p, Packaging.NFTC), p.toString());
        check("and a java.base licence that is not GPL v2 fails", names(p, "not the GNU GPL"), null);
        Fixture fx = staged();
        p = Packaging.verify(fx.image(), fx.pins(), CREDITS, new Packaging.Probes(pinnedProbes(fx.pins()).runtimeStrings(), jar -> "22+30"));
        check("an OpenJFX build other than the pinned one fails", names(p, "22+30", fx.pins().getProperty("openjfx.version")), p.toString());
        Fixture concrt = staged();
        Files.write(concrt.image().resolve("runtime/bin/concrt140.dll"), new byte[] {'M', 'Z'});
        check("a Microsoft library the notice does not name fails", names(verify(concrt), "concrt140.dll", "audit"), verify(concrt).toString());
        Fixture twoJars = staged();
        Files.write(twoJars.image().resolve("app/other.jar"), new byte[0]);
        check("an app folder of more than the game's jar fails", names(verify(twoJars), "2 jars"), null);
        check("a jar that cannot be opened is named, its libraries not skipped unseen", names(verify(twoJars), "app/other.jar cannot be opened"), null);
        Fixture bumped = staged();
        Properties ahead = new Properties();
        ahead.putAll(bumped.pins());
        ahead.setProperty("runtime.version", "21.0.14+7");
        ahead.setProperty("runtime.vendorVersion", "Temurin-21.0.14+7");
        ahead.setProperty("runtime.javaVersion", "21.0.14");
        p = Packaging.verify(bumped.image(), ahead, CREDITS, pinnedProbes(ahead));
        check("a runtime version bumped in the pins alone, its archive's file and URL left behind, fails", names(p, "runtime.file", "21.0.14+7")
                && names(p, "runtime.url", "21.0.14+7"), p.toString());
        ahead.setProperty("runtime.vendorVersion", "Temurin-21.0.13+9");
        check("and a vendor version that is not the runtime version's fails", names(Packaging.pinProblems(ahead), "runtime.vendorVersion"), null);
        check("the real pins agree with themselves", Packaging.pinProblems(Packaging.pins()).isEmpty(), Packaging.pinProblems(Packaging.pins()).toString());

        System.out.println("--- the packaging JDK is held to the pins ---");
        Path jdk = TempDirs.create("packaging-jdk");
        String pinnedRelease = "IMPLEMENTOR=\"" + pins.getProperty("runtime.implementor") + "\"\nIMPLEMENTOR_VERSION=\""
                + pins.getProperty("runtime.vendorVersion") + "\"\nJAVA_VERSION=\"" + pins.getProperty("runtime.javaVersion") + "\"\n";
        String buildLines = "BUILD_SOURCE=\"git:" + pins.getProperty("build.version") + "\"\nBUILD_SOURCE_REPO=\"" + pins.getProperty("build.repo") + "\"\n";
        Files.writeString(jdk.resolve("release"), pinnedRelease + buildLines);
        check("the pinned Temurin passes", Packaging.checkJdk(jdk, pins).isEmpty(), Packaging.checkJdk(jdk, pins).toString());
        Files.writeString(jdk.resolve("release"), pinnedRelease + "BUILD_SOURCE=\"git:0000000000000000000000000000000000000000\"\nBUILD_SOURCE_REPO=\""
                + pins.getProperty("build.repo") + "\"\n");
        p = Packaging.checkJdk(jdk, pins);
        check("a JDK built by other scripts than the pinned temurin-build commit fails", names(p, "BUILD_SOURCE is \"git:0000"), p.toString());
        Files.writeString(jdk.resolve("release"), pinnedRelease);
        check("and one whose release file names no build scripts fails", names(Packaging.checkJdk(jdk, pins), "BUILD_SOURCE is absent"), null);
        Files.writeString(jdk.resolve("release"), "IMPLEMENTOR=\"Oracle Corporation\"\nJAVA_VERSION=\"21.0.4\"\n");
        p = Packaging.checkJdk(jdk, pins);
        check("Oracle's JDK fails on its IMPLEMENTOR", names(p, "IMPLEMENTOR is \"Oracle Corporation\""), p.toString());
        check("and its version", names(p, "JAVA_VERSION is \"21.0.4\""), null);
        Files.writeString(jdk.resolve("release"), "IMPLEMENTOR=\"Eclipse Adoptium\"\nIMPLEMENTOR_VERSION=\"Temurin-21.0.14+7\"\nJAVA_VERSION=\"21.0.14\"\n");
        p = Packaging.checkJdk(jdk, pins);
        check("another Temurin fails, since its source is not the pinned one", names(p, "Temurin-21.0.14+7") && names(p, "pin the source archive"), p.toString());
        Files.delete(jdk.resolve("release"));
        check("a folder with no release file fails", names(Packaging.checkJdk(jdk, pins), "no release file"), null);
        String named = System.getenv("CBS_PACKAGING_JDK");
        Path pc = Path.of(named != null ? named : System.getProperty("user.home") + "/Tools/jdk-21-temurin");
        if (Files.isRegularFile(pc.resolve("release"))) {
            p = Packaging.checkJdk(pc, Packaging.pins());
            check("this PC's packaging JDK, " + pc + ", is the pinned Temurin", p.isEmpty(), p.toString());
        } else {
            System.out.println("  (no packaging JDK at " + pc + ": skipped)");
        }

        System.out.println("--- the pins are the shipped binaries' ---");
        Properties real = Packaging.pins();
        check("the pins name OpenJFX's archive, the runtime's and its build scripts', in that order", Credits.archives(real).equals(List.of("openjfx", "runtime", "build")), Credits.archives(real).toString());
        for (String a : Credits.archives(real)) {
            check(a + "'s SHA-256 is 64 hex digits", real.getProperty(a + ".sha256", "").matches("[0-9a-f]{64}"), real.getProperty(a + ".sha256"));
            check(a + "'s origin is an https URL", real.getProperty(a + ".url", "").startsWith("https://"), real.getProperty(a + ".url"));
            check(a + "'s file, version and description are given", !real.getProperty(a + ".file", "").isBlank()
                    && !real.getProperty(a + ".version", "").isBlank() && !real.getProperty(a + ".what", "").isBlank(), null);
        }
        check("the runtime's archive is the vendor version's", real.getProperty("runtime.vendorVersion").endsWith("-" + real.getProperty("runtime.version"))
                && real.getProperty("runtime.version").startsWith(real.getProperty("runtime.javaVersion") + "+"), real.getProperty("runtime.vendorVersion"));
        check("OpenJFX's archive is the openjdk/jfx tag of its version", real.getProperty("openjfx.url").contains("/openjdk/jfx/")
                && real.getProperty("openjfx.url").contains(real.getProperty("openjfx.version").replace("+", "%2B")), real.getProperty("openjfx.url"));
        check("the build scripts' archive is the pinned repository at its commit", real.getProperty("build.version", "").matches("[0-9a-f]{40}")
                && real.getProperty("build.url", "").startsWith(real.getProperty("build.repo", "?").replaceAll("\\.git$", "") + "/archive/" + real.getProperty("build.version")),
                real.getProperty("build.url"));
        String readme = Credits.sourceReadme(real);
        check("the README gives the jlink command the runtime is made with", real.getProperty("runtime.jlink") != null
                && readme.contains(real.getProperty("runtime.jlink")), null);
        Path pom = Path.of("pom.xml");
        if (Files.exists(pom)) {
            String javafx = XPathFactory.newInstance().newXPath().evaluate("/project/properties/javafx.version",
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom.toFile()));
            check("OpenJFX's pinned build is pom.xml's version " + javafx, real.getProperty("openjfx.version").startsWith(javafx + "+"), real.getProperty("openjfx.version"));
        } else {
            System.out.println("  (no pom.xml here: skipped)");
        }
        Path fxJar = null;
        for (String entry : System.getProperty("java.class.path").split(Pattern.quote(File.pathSeparator)))
            if (Path.of(entry).getFileName().toString().matches("javafx-base-.*-(?:win|linux|mac)\\.jar")) fxJar = Path.of(entry);
        check("the classpath carries OpenJFX's base jar", fxJar != null, null);
        if (fxJar != null) {
            String build = Packaging.fxVersion(fxJar);
            check("its build is the pinned " + real.getProperty("openjfx.version") + ", whose source ships", real.getProperty("openjfx.version").equals(build), build);
        }
        Set<String> strings = Packaging.runtimeStrings(Path.of(System.getProperty("java.home")));
        check("the probe reads this JVM's vendor, " + System.getProperty("java.vendor") + ", from its java.base", strings != null
                && strings.contains(System.getProperty("java.vendor")), null);
        String vendorVersion = System.getProperty("java.vendor.version");
        if (vendorVersion != null) check("and its vendor version, " + vendorVersion, strings != null && strings.contains(vendorVersion), null);

        System.out.println("--- Build EXE.bat names its JDK and checks the image before the zip ---");
        Path batFile = Path.of("Build EXE.bat");
        if (Files.exists(batFile)) {
            String bat = Files.readString(batFile).replace("\r", "");
            check("it never asks PATH for jpackage", !bat.matches("(?is).*\\bwhere\\s+jpackage\\b.*"), null);
            check("it never falls back to Program Files' JDK", !bat.contains("Program Files\\Java"), null);
            check("it checks the JDK's release file for Eclipse Adoptium", bat.contains("findstr /b /c:\"IMPLEMENTOR=\\\"Eclipse Adoptium\\\"\" \"%JDK%\\release\""), null);
            check("and for Java 21", bat.contains("findstr /b /r /c:\"JAVA_VERSION=.21\\.\" \"%JDK%\\release\""), null);
            check("it runs the named JDK's own jlink and jpackage", bat.contains("\"%JDK%\\bin\\jlink.exe\"") && bat.contains("\"%JDK%\\bin\\jpackage.exe\"")
                    && bat.contains("--runtime-image \"build\\runtime\""), null);
            int jdkCheck = bat.indexOf("Packaging jdk"), fetch = bat.indexOf("Packaging fetch"), jpackage = bat.indexOf("\"%JDK%\\bin\\jpackage.exe\""),
                    stage = bat.indexOf("Packaging stage"), verify = bat.indexOf("Packaging verify"), zip = bat.indexOf("tar -a -c"),
                    unfit = bat.indexOf(":unfit");
            check("the JDK is held to the pins and the archives fetched before jpackage", jdkCheck > 0 && fetch > jdkCheck && jpackage > fetch, null);
            check("the image is staged and checked after jpackage and before the zip", stage > jpackage && verify > stage && zip > verify, null);
            check("a failed stage or check goes to the removal of the image", bat.contains("Packaging stage \"%SOURCES%\" \"dist\\%APPNAME%\"\nif not \"%ERRORLEVEL%\"==\"0\" goto unfit")
                    && bat.contains("Packaging verify \"dist\\%APPNAME%\"\nif not \"%ERRORLEVEL%\"==\"0\" goto unfit")
                    && unfit > zip && bat.indexOf("rmdir /s /q \"dist\\%APPNAME%\"", unfit) > unfit, null);
            check("and of this version's zip, an earlier build's left by none", bat.indexOf("del /q \"dist\\%APPNAME%-%APPVER%.zip\"", unfit) > unfit
                    && bat.indexOf("exit /b 1", unfit) > bat.indexOf("del /q \"dist\\%APPNAME%-%APPVER%.zip\"", unfit), null);
            check("a failed fetch stops the build", bat.substring(fetch).startsWith("Packaging fetch \"%SOURCES%\"\nif not \"%ERRORLEVEL%\"==\"0\" (")
                    && bat.indexOf("exit /b 1", fetch) < jpackage, null);
            // The CR1-J review: "if errorlevel 1" passes a negative code (a crash's 0xC0000005), and a stop
            // after the previous build is cleared left an exe without its source or notices.
            check("no exit code is read with \"if errorlevel\", which a negative code passes", !bat.matches("(?is).*\\bif\\s+(not\\s+)?errorlevel\\b.*"), null);
            int runtimeStep = bat.indexOf("rem ------------------------------ the runtime");
            check("from the runtime step on, every stop goes through :unfit", runtimeStep > bat.indexOf("does not look like a previous build")
                    && !bat.substring(runtimeStep, unfit).contains("exit /b 1"), null);
            check("jpackage, jlink and the NOTICE copy each go to :unfit when they fail", bat.substring(runtimeStep, unfit).split("goto unfit", -1).length - 1 >= 7, null);
            check("the exe carries the game's copyright, Credits.COPYRIGHT", bat.contains("--copyright \"" + Credits.COPYRIGHT + "\""), null);
            java.util.regex.Matcher modules = Pattern.compile("set \"MODULES=([^\"]+)\"").matcher(bat);
            java.util.regex.Matcher jlinkLine = Pattern.compile("\"%JDK%\\\\bin\\\\jlink\\.exe\" ([^\n]+)").matcher(bat);
            String asRun = modules.find() && jlinkLine.find()
                    ? "jlink " + jlinkLine.group(1).replace("%MODULES%", modules.group(1)).replace("\"build\\runtime\"", "runtime") : null;
            check("its jlink line is the pins' runtime.jlink, which the README gives", asRun != null && asRun.equals(real.getProperty("runtime.jlink")), asRun);
            check("nopause neither pauses nor starts the game", bat.contains("if /i \"%~1\"==\"nopause\" (\n    set \"PAUSE=rem\"\n    set \"LAUNCH=\"")
                    && bat.contains("if not defined LAUNCH exit /b 0") && bat.indexOf("if not defined LAUNCH exit /b 0") < bat.indexOf("start \"\" /D"), null);
            check("every other pause is %PAUSE%", bat.split("\n\\s*pause\\s*\n", -1).length == 2, null);
        } else {
            System.out.println("  (no Build EXE.bat here: skipped)");
        }

        System.out.println(fails == 0 ? "PackagingCheck: all ok" : "PackagingCheck: " + fails + " FAILED");
        System.exit(fails);
    }

    static void deleteTree(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> all = new ArrayList<>(walk.toList());
            java.util.Collections.reverse(all);
            for (Path p : all) Files.delete(p);
        }
    }
}
