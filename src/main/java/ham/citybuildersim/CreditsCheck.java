package ham.citybuildersim;

import ham.citybuildersim.tools.Packaging;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

/**
 * The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and
 * licence, the libraries' licences and notices, the Java runtime's legal folder, Microsoft's terms for
 * its runtime files and where the source is, and says where a file is when this build lacks it
 * (CR1, CR1-L, CR1-J).
 *
 * WHY THIS EXISTS. The game is sold, and the licences of its data (CC BY among them) and of its
 * type (the SIL Open Font License) ask that the credit reach the player. The page reads the files
 * from the jar, which pom.xml copies from tools/; Build EXE.bat copies the same files beside the
 * exe. A third attribution file added to the batch file and not to the page, a page cut down from
 * its file, or a font added without its licence would each leave a credit unshown without any
 * error. Files are read from the working directory (the repository), as BrandingCheck reads
 * them; elsewhere those sections say so and are skipped.
 *
 * What it has to prove:
 *
 *   1. EVERY FILE THE EXE SHIPS AS A CREDIT IS ON THE PAGE, IN FULL. Each ATTRIBUTION copy in
 *      Build EXE.bat has a section whose text is the file under tools/, exactly, and whose
 *      besideExe is the copy's destination.
 *   2. THE FONTS' NOTICE AND LICENCE. Every font in resources/fonts/ is IBM Plex, OFL.txt carries
 *      IBM's copyright and the licence's name, the notice says both, and the fonts' section is
 *      the notice over the licence in full.
 *   3. A BUILD WITHOUT A FILE SAYS WHERE THE FILE IS. A section whose text is null is missing(),
 *      the others are not, and the note names the file beside the exe.
 *   4. THE PARAGRAPHS KEEP THE WHOLE TEXT. Cut on blank lines, no non-blank line is lost or
 *      moved; a blank run is one cut and a \r goes.
 *   5. EACH SHIPPED FILE NAMES THE PAGE. Both files say "Credits page", where the game shows them.
 *   6. THE LIBRARIES FOLLOW THE FONTS, WITH THEIR RELEASE TEXTS IN FULL. The fallback copies'
 *      hashes and versions are pinned to upstream releases and pom.xml. The jars lack legal
 *      files; if that changes, re-audit and take their texts from the dependency instead.
 *   7. REVISITING THE PAGE REUSES ITS TEXTS. All visits share an immutable section list;
 *      the injected reader remains independent for missing-file fixtures.
 *   8. THE JAVA RUNTIME'S NOTICES ARE ITS OWN (CR1-J). The runtime's NOTICE and every file of
 *      its legal folder, java.base's first, in full; jlink's "Please see" notes kept and resolved
 *      inside the folder, and one naming a file it lacks, or outside it, marks the section missing.
 *   9. MICROSOFT'S TERMS FOR ITS RUNTIME FILES. The notice and Microsoft's pinned licence text in
 *      full, the notice naming every Microsoft library in OpenJFX's Windows graphics jar.
 *  10. WHERE THE SOURCE IS. The section is the README the pins make, naming each archive.
 */
public class CreditsCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static String read(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    /** The text's non-blank lines, in order, with any \r gone. */
    static List<String> lines(String text) {
        List<String> out = new ArrayList<>();
        for (String line : text.replace("\r", "").split("\n", -1)) if (!line.isBlank()) out.add(line);
        return out;
    }

    /** The release copies reach the page unchanged, and a dependency upgrade cannot silently keep old notices. */
    static void libraryNotices(List<Credits.Section> sections, boolean inRepository) throws Exception {
        System.out.println("--- the libraries' release notices and licences ---");
        check("the libraries follow the fonts, one section each, then the runtime, Microsoft's libraries and the source",
                sections.stream().map(Credits.Section::title).toList().equals(List.of("The Earth", "The nations", "The fonts", "Gson", "OpenJFX",
                        "The Java runtime", "Microsoft's runtime libraries", "The source code")), null);
        String provenance = Credits.readResource("/credits/sources.properties");
        check("the jar carries the legal texts' release provenance", provenance != null, null);
        if (provenance == null) return;
        Properties sources = new Properties();
        sources.load(new StringReader(provenance));
        List<String> paths = new ArrayList<>(List.of(Credits.GSON));
        paths.addAll(Credits.OPENJFX_FILES);
        for (String path : paths) {
            String text = Credits.readResource(path);
            check(path + " is present and nonempty", text != null && !text.isBlank(), null);
            if (text == null) continue;
            String relative = path.substring("/credits/".length());
            String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
            check(path + " matches the unmodified upstream release text", hash.equals(sources.getProperty("sha256." + relative)), hash);
            if (inRepository) {
                Path source = Path.of("tools", "credits", relative);
                check(path + " matches its single source copy", Files.exists(source) && text.equals(read(source)), source.toString());
            }
        }
        for (String resource : List.of(Credits.GSON, Credits.OPENJFX)) {
            Credits.Section section = sections.stream().filter(s -> s.resource().equals(resource)).findFirst().orElse(null);
            check(resource + " has a section with all its texts", section != null && !section.missing(), null);
            if (section == null || section.missing()) continue;
            if (resource.equals(Credits.GSON)) {
                check("Gson shows its Apache licence exactly", section.text().equals(Credits.readResource(Credits.GSON))
                        && section.text().contains("Apache License") && section.lead().contains("Version 2.0"), null);
            } else {
                List<String> files = new ArrayList<>();
                for (String path : Credits.OPENJFX_FILES) files.add(path.substring(path.lastIndexOf('/') + 1) + "\n\n" + Credits.readResource(path));
                check("OpenJFX shows every release notice exactly, in order", section.text().equals(String.join("\n\n", files)), null);
                check("OpenJFX includes GPL v2 and the Classpath Exception", section.text().contains("Version 2, June 1991")
                        && section.text().contains("\"CLASSPATH\" EXCEPTION TO THE GPL"), null);
                check("the JPEG credit is given explicitly", section.lead().contains("This software is based in part on the work of the Independent JPEG Group."), null);
            }
        }
        for (String missing : paths) {
            String resource = missing.equals(Credits.GSON) ? Credits.GSON : Credits.OPENJFX;
            Credits.Section section = Credits.sections(path -> path.equals(missing) ? null : Credits.readResource(path)).stream()
                    .filter(s -> s.resource().equals(resource)).findFirst().orElseThrow();
            check("missing " + missing + " marks the whole library section missing", section.missing(), null);
            check("its missing note identifies the library", Credits.missingNote(section).contains(section.title()), null);
        }
        if (inRepository) {
            var pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Path.of("pom.xml").toFile());
            var xpath = XPathFactory.newInstance().newXPath();
            check("Gson's legal release version is pinned to pom.xml", sources.getProperty("gson.version").equals(xpath.evaluate(
                    "/project/dependencies/dependency[artifactId='gson']/version", pom)), sources.getProperty("gson.source"));
            check("OpenJFX's legal release version is pinned to pom.xml", sources.getProperty("openjfx.version").equals(xpath.evaluate(
                    "/project/properties/javafx.version", pom)), sources.getProperty("openjfx.source"));
            check("both OpenJFX dependencies use the audited version", "${javafx.version}".equals(xpath.evaluate(
                    "/project/dependencies/dependency[artifactId='javafx-controls']/version", pom)) && "${javafx.version}".equals(xpath.evaluate(
                    "/project/dependencies/dependency[artifactId='javafx-fxml']/version", pom)), null);
            dependencyNotices();
        }
    }

    /** The audited jars omit legal files; a new NOTICE or licence must be taken from the jar, not ignored. */
    static void dependencyNotices() throws IOException {
        for (String entry : System.getProperty("java.class.path").split(Pattern.quote(File.pathSeparator))) {
            Path path = Path.of(entry);
            if (!path.getFileName().toString().matches("(?:gson|javafx-(?:base|controls|fxml|graphics))-.*\\.jar")) continue;
            try (JarFile jar = new JarFile(path.toFile())) {
                List<String> legal = jar.stream().map(e -> e.getName()).filter(n -> !n.endsWith("/"))
                        .filter(n -> n.matches("(?i)(?:.*/)?(?:LICENSE|NOTICE|COPYING|COPYRIGHT|ASSEMBLY_EXCEPTION|ADDITIONAL_LICENSE_INFO)(?:[._-].*)?")
                                || n.matches("(?i)(?:.*/)?legal/.*")).toList();
                check(path.getFileName() + " still omits legal files; fallback applies", legal.isEmpty(), legal.toString());
            }
        }
    }

    /** A section by title, or null. */
    static Credits.Section section(List<Credits.Section> sections, String title) {
        return sections.stream().filter(s -> s.title().equals(title)).findFirst().orElse(null);
    }

    /** The runtime's notices are its own legal folder's, every file in full, every "Please see" inside it (CR1-J). */
    static void runtimeNotices(List<Credits.Section> sections) throws IOException {
        System.out.println("--- the Java runtime's notices are its own legal folder's, in full ---");
        Path runtime = TempDirs.create("credits-runtime");
        Path base = runtime.resolve("legal").resolve("java.base"), desktop = runtime.resolve("legal").resolve("java.desktop");
        Files.createDirectories(base);
        Files.createDirectories(desktop);
        Files.writeString(runtime.resolve("NOTICE"), "# Notices for the fixture runtime\n");
        Files.writeString(base.resolve("LICENSE"), "The GNU General Public License (GPL)\n\nVersion 2, June 1991\n");
        Files.writeString(base.resolve("zlib.md"), "## zlib v1.3\n\nthe zlib licence\n");
        Files.writeString(desktop.resolve("LICENSE"), "Please see ..\\java.base\\LICENSE\r\n");
        Files.writeString(desktop.resolve("freetype.md"), "## FreeType\n\nthe FreeType licence\n");
        List<String> problems = new ArrayList<>();
        List<Credits.LegalFile> files = Credits.runtimeLegal(runtime, problems);
        check("a whole legal folder is read with no problem", files != null && problems.isEmpty(), problems.toString());
        if (files != null) {
            check("NOTICE first, then java.base's files, then the other modules', each by name", files.stream().map(Credits.LegalFile::name).toList()
                    .equals(List.of("NOTICE", "legal/java.base/LICENSE", "legal/java.base/zlib.md", "legal/java.desktop/LICENSE", "legal/java.desktop/freetype.md")),
                    files.stream().map(Credits.LegalFile::name).toList().toString());
            Credits.LegalFile ref = files.get(3);
            check("jlink's \"Please see\" note is kept as written", ref.text().equals("Please see ..\\java.base\\LICENSE\r\n"), null);
            check("and resolved to the file it names, inside the folder", "legal/java.base/LICENSE".equals(ref.refersTo()), ref.refersTo());
            check("a file that is not a note refers to nothing", files.stream().filter(x -> x != ref).allMatch(x -> x.refersTo() == null), null);
        }
        Credits.Section s = section(Credits.sections(Credits::readResource, runtime), "The Java runtime");
        check("the page's runtime section is that folder's files under their names, in full", files != null && s != null && !s.missing()
                && s.text().equals(Credits.joined(files)) && s.text().contains("legal/java.desktop/freetype.md\n\n## FreeType\n\nthe FreeType licence\n"), null);
        check("its lead names this JVM's vendor and counts the files", s != null && s.lead().contains(System.getProperty("java.vendor")) && s.lead().contains("5 files"),
                s == null ? null : s.lead());
        check("it names the runtime folder beside the exe", s != null && Credits.RUNTIME_LEGAL.equals(s.besideExe()), null);

        Files.writeString(desktop.resolve("ASSEMBLY_EXCEPTION"), "Please see ..\\java.base\\ASSEMBLY_EXCEPTION\r\n");
        problems.clear();
        check("a note naming a file the folder lacks makes it unreadable", Credits.runtimeLegal(runtime, problems) == null
                && problems.stream().anyMatch(p -> p.contains("ASSEMBLY_EXCEPTION") && p.contains("lacks")), problems.toString());
        s = section(Credits.sections(Credits::readResource, runtime), "The Java runtime");
        check("and the page marks the whole section missing, naming runtime/legal", s != null && s.missing() && Credits.missingNote(s).contains(Credits.RUNTIME_LEGAL), null);
        Files.writeString(desktop.resolve("ASSEMBLY_EXCEPTION"), "Please see ..\\..\\NOTICE\r\n");
        problems.clear();
        check("a note naming a file outside the folder makes it unreadable", Credits.runtimeLegal(runtime, problems) == null
                && problems.stream().anyMatch(p -> p.contains("outside")), problems.toString());
        Files.delete(desktop.resolve("ASSEMBLY_EXCEPTION"));
        Path bare = TempDirs.create("credits-runtime-bare");
        problems.clear();
        check("a runtime without a legal folder has no notices to show", Credits.runtimeLegal(bare, problems) == null && !problems.isEmpty(), null);

        Path home = Path.of(System.getProperty("java.home"));
        problems.clear();
        List<Credits.LegalFile> real = Credits.runtimeLegal(home, problems);
        check("this JVM's legal folder is read whole", real != null, problems.toString());
        Credits.Section page = section(sections, "The Java runtime");
        check("the page shows this JVM's runtime notices", page != null && !page.missing(), null);
        if (real != null && page != null && !page.missing()) {
            Set<String> names = new HashSet<>();
            for (Credits.LegalFile x : real) names.add(x.name());
            check("java.base's LICENSE is among them", names.contains("legal/java.base/LICENSE"), null);
            check("every \"Please see\" in it names a file shown", real.stream().allMatch(x -> x.refersTo() == null || names.contains(x.refersTo())), null);
            check("every file is on the page in full", real.stream().allMatch(x -> page.text().contains(x.name() + "\n\n" + x.text())), real.size() + " files");
            long count = 0;
            try (Stream<Path> walk = Files.walk(home.resolve("legal"))) { count = walk.filter(Files::isRegularFile).count(); }
            check("none of the folder's files is left out", real.stream().filter(x -> x.name().startsWith("legal/")).count() == count, count + " files on disk");
        }
    }

    /** Microsoft's notice and terms, and where the source is, as the jar and the pins hold them (CR1-J). */
    static void microsoftAndSource(List<Credits.Section> sections, boolean inRepository) throws IOException {
        System.out.println("--- Microsoft's runtime libraries: the notice and Microsoft's terms in full ---");
        Credits.Section ms = section(sections, "Microsoft's runtime libraries");
        check("the page has Microsoft's section, not missing", ms != null && !ms.missing(), null);
        String notice = Credits.readResource(Credits.MICROSOFT);
        String terms = Credits.readResource(Credits.MICROSOFT_FILES.get(1));
        Properties sources = Credits.properties(String.valueOf(Credits.readResource("/credits/sources.properties")));
        if (ms != null && !ms.missing() && notice != null && terms != null) {
            check("it shows the notice and the terms exactly, in order, under their names", ms.text().equals(Credits.readAll(Credits::readResource, Credits.MICROSOFT_FILES)), null);
            check("the terms are Microsoft's for the Visual C++ 2015 - 2022 runtime", terms.contains("MICROSOFT VISUAL C++ 2015 - 2022 RUNTIME")
                    && terms.contains("EULA ID: " + sources.getProperty("microsoft.version")), sources.getProperty("microsoft.version"));
            String hash = HexFormat.of().formatHex(sha256(terms.getBytes(StandardCharsets.UTF_8)));
            check("the terms are the pinned text of Microsoft's document", hash.equals(sources.getProperty("sha256.microsoft/LICENSE-vc-runtime-2015-2022.txt"))
                    && sources.getProperty("microsoft.source", "").startsWith("https://visualstudio.microsoft.com/"), hash);
            check("the notice gives Microsoft's copyright and points at the terms", notice.contains("Copyright (c) Microsoft Corporation.")
                    && notice.contains("LICENSE-vc-runtime-2015-2022.txt"), null);
            check("the lead says whose libraries they are", Credits.MICROSOFT_LEAD.equals(ms.lead()), null);
            if (inRepository) {
                Path source = Path.of("tools", "credits", "microsoft");
                check("the jar's notice and terms are tools/credits/microsoft's", Files.exists(source.resolve("NOTICE.txt"))
                        && notice.equals(read(source.resolve("NOTICE.txt"))) && terms.equals(read(source.resolve("LICENSE-vc-runtime-2015-2022.txt"))), null);
                List<String> dlls = new ArrayList<>();
                for (String entry : System.getProperty("java.class.path").split(Pattern.quote(File.pathSeparator))) {
                    Path path = Path.of(entry);
                    if (!path.getFileName().toString().matches("javafx-graphics-.*-win\\.jar")) continue;
                    try (JarFile jar = new JarFile(path.toFile())) {
                        jar.stream().map(e -> e.getName()).filter(n -> Packaging.MICROSOFT_DLL.matcher(n.substring(n.lastIndexOf('/') + 1)).matches()).forEach(dlls::add);
                    }
                }
                check("OpenJFX's Windows graphics jar carries Microsoft libraries", !dlls.isEmpty(), dlls.size() + " found");
                List<String> unnamed = new ArrayList<>();
                for (String dll : dlls) {
                    String name = dll.substring(dll.lastIndexOf('/') + 1);
                    boolean named = name.startsWith("api-ms-win-") ? notice.contains("api-ms-win-*.dll") : notice.contains(name);
                    if (!named || !Packaging.MICROSOFT_COVERED.matcher(name).matches()) unnamed.add(name);
                }
                check("the notice names every one of them, and the image check covers it", unnamed.isEmpty(), unnamed.toString());
            }
        }
        for (String missing : Credits.MICROSOFT_FILES) {
            Credits.Section without = section(Credits.sections(path -> path.equals(missing) ? null : Credits.readResource(path)), "Microsoft's runtime libraries");
            check("without " + missing + " the whole section is missing", without != null && without.missing(), null);
            check("and its note names the legal folder beside the exe", without != null && Credits.missingNote(without).contains(Credits.LEGAL_FOLDER + "/microsoft"), null);
        }

        System.out.println("--- the source code: where the complete source is ---");
        Credits.Section src = section(sections, "The source code");
        String pins = Credits.readResource(Credits.SOURCE_PINS);
        check("the jar carries the source archives' pins", pins != null, null);
        if (pins != null && src != null) {
            Properties p = Credits.properties(pins);
            check("the section is the README tools.Packaging writes beside the archives", !src.missing() && src.text().equals(Credits.sourceReadme(p)), null);
            for (String a : Credits.archives(p)) {
                check("it names " + p.getProperty(a + ".file") + ", its version, origin and SHA-256", src.text().contains(p.getProperty(a + ".file") + "\n")
                        && src.text().contains(", version " + p.getProperty(a + ".version") + "\n") && src.text().contains("from " + p.getProperty(a + ".url"))
                        && src.text().contains("SHA-256 " + p.getProperty(a + ".sha256")), null);
            }
            check("it says the archives are in the folder " + Credits.SOURCE_FOLDER + " beside the exe", src.text().contains("in the folder " + Credits.SOURCE_FOLDER + " beside CityBuilderSim.exe"), null);
            check("and why: GPL v2 section 3", src.text().contains("section 3") && src.text().contains("version 2"), null);
            if (inRepository) check("the jar's pins are tools/credits'", pins.equals(read(Path.of("tools", "credits", "source-archives.properties"))), null);
        }
        Credits.Section none = section(Credits.sections(path -> path.equals(Credits.SOURCE_PINS) ? null : Credits.readResource(path)), "The source code");
        check("without the pins the section is missing, naming source/README.txt", none != null && none.missing() && Credits.missingNote(none).contains("source/README.txt"), null);
    }

    static byte[] sha256(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== CreditsCheck: the Credits page carries the data, fonts and libraries' notices and licences, in full (CR1, CR1-L) ===");

        Path bat = Path.of("Build EXE.bat");
        Path fonts = Path.of("src", "main", "resources", "fonts");
        boolean inRepository = Files.exists(bat) && Files.isDirectory(fonts);
        if (!inRepository) System.out.println("  (no Build EXE.bat in " + Path.of("").toAbsolutePath() + ": not run from the repository, the file sections are skipped)");
        List<Credits.Section> sections = Credits.sections();
        final String mark = "\u00a9";

        System.out.println("--- later visits reuse the loaded notices ---");
        check("another visit receives the same loaded sections", sections == Credits.sections(), null);
        boolean immutable = false;
        try {
            sections.set(0, sections.get(0));
        } catch (UnsupportedOperationException expected) {
            immutable = true;
        }
        check("a caller cannot change the cached sections", immutable, null);

        System.out.println("--- every file the exe ships as a credit is on the page, in full ---");
        if (inRepository) {
            Matcher m = Pattern.compile("(?im)^\\s*copy\\s+(?:/\\w+\\s+)*\"([^\"]*ATTRIBUTION[^\"]*\\.txt)\"\\s+\"([^\"]+)\"").matcher(read(bat));
            int found = 0;
            List<String[]> copies = new ArrayList<>();
            while (m.find()) copies.add(new String[] {m.group(1).replace('\\', '/'), m.group(2).replace('\\', '/')});
            check("Build EXE.bat copies at least two ATTRIBUTION files beside the exe", copies.size() >= 2, copies.size() + " found");
            for (String[] c : copies) {
                String source = c[0], dest = c[1].substring(c[1].lastIndexOf("/data/") + 1);
                String resource = "/credits/" + (source.startsWith("tools/") ? source.substring("tools/".length()) : source);
                Credits.Section s = null;
                for (Credits.Section each : sections) if (each.resource().equals(resource)) s = each;
                check(source + " has a section on the page, reading " + resource, s != null, null);
                if (s == null) continue;
                check(source + "'s section is not missing", !s.missing(), s.title());
                if (!s.missing()) {
                    String file = read(Path.of(source));
                    check(source + "'s section shows the file exactly", s.text().equals(file),
                            s.text().length() + " characters on the page, " + file.length() + " in the file");
                }
                check(source + "'s section names " + dest + " as the file beside the exe", dest.equals(s.besideExe()), String.valueOf(s.besideExe()));
                found++;
            }
            check("every one of them was matched to a section", found == copies.size(), found + " of " + copies.size());
        } else {
            System.out.println("  (skipped)");
        }

        System.out.println("--- the fonts' notice and licence ---");
        Credits.Section fontsSection = null;
        for (Credits.Section s : sections) if (s.resource().equals(Credits.FONTS)) fontsSection = s;
        check("the page has a fonts section reading " + Credits.FONTS, fontsSection != null, null);
        check("the notice says 2017 IBM Corp.", Credits.FONT_NOTICE.contains("2017 IBM Corp."), null);
        check("the notice names the SIL Open Font License, Version 1.1", Credits.FONT_NOTICE.contains("SIL Open Font License, Version 1.1"), null);
        if (fontsSection != null) {
            check("the fonts' section leads with the notice", Credits.FONT_NOTICE.equals(fontsSection.lead()), String.valueOf(fontsSection.lead()));
            check("the fonts' section is not missing", !fontsSection.missing(), null);
        }
        if (inRepository) {
            int count = 0;
            List<String> strangers = new ArrayList<>();
            try (Stream<Path> files = Files.list(fonts)) {
                for (Path f : (Iterable<Path>) files::iterator) {
                    String name = f.getFileName().toString();
                    if (!name.endsWith(".ttf") && !name.endsWith(".otf")) continue;
                    count++;
                    if (!name.startsWith("IBMPlex")) strangers.add(name);
                }
            }
            check("there is at least one font in resources/fonts/", count >= 1, count + " found");
            check("every font there is IBM Plex, whose licence the page carries", strangers.isEmpty(), strangers.toString());
            Path ofl = fonts.resolve("OFL.txt");
            check("OFL.txt is beside the fonts", Files.exists(ofl), null);
            if (Files.exists(ofl)) {
                String licence = read(ofl);
                check("OFL.txt carries Copyright " + mark + " 2017 IBM Corp.", licence.contains("Copyright " + mark + " 2017 IBM Corp."), null);
                check("OFL.txt names the SIL Open Font License, Version 1.1", licence.contains("SIL Open Font License, Version 1.1"), null);
                if (fontsSection != null && !fontsSection.missing()) {
                    check("the fonts' section shows OFL.txt exactly", fontsSection.text().equals(licence),
                            fontsSection.text().length() + " characters on the page, " + licence.length() + " in the file");
                }
            }
        }

        System.out.println("--- a build without a file says where the file is ---");
        List<Credits.Section> without = Credits.sections(path -> path.equals(Credits.NATIONS) ? null : Credits.readResource(path));
        for (Credits.Section s : without) {
            boolean gone = s.resource().equals(Credits.NATIONS);
            check(s.title() + (gone ? " is missing when its file is not read" : " is not missing"), s.missing() == gone, null);
            if (gone) {
                String note = Credits.missingNote(s);
                check("and its note names " + s.besideExe(), s.besideExe() != null && note.contains(s.besideExe()), note);
            }
        }

        System.out.println("--- the paragraphs keep the whole text ---");
        for (Credits.Section s : sections) {
            if (s.missing()) {
                check(s.title() + " has no text here, so no paragraphs to hold", Credits.paragraphs(s.text()).isEmpty(), null);
                continue;
            }
            List<String> paragraphs = Credits.paragraphs(s.text());
            List<String> kept = new ArrayList<>();
            boolean blankParagraph = false;
            for (String p : paragraphs) {
                kept.addAll(lines(p));
                if (p.isBlank() || p.contains("\r")) blankParagraph = true;
            }
            check(s.title() + " loses no non-blank line and moves none", kept.equals(lines(s.text())),
                    kept.size() + " lines in " + paragraphs.size() + " paragraphs, " + lines(s.text()).size() + " in the text");
            check(s.title() + " has no blank paragraph and no \\r in one", !blankParagraph, null);
        }
        List<String> fixture = Credits.paragraphs("a\nb\n\n\nc\r\n");
        check("\"a\\nb\\n\\n\\nc\\r\\n\" is [\"a\\nb\", \"c\"]", fixture.equals(List.of("a\nb", "c")), fixture.toString());
        check("a null text has no paragraphs", Credits.paragraphs(null).isEmpty(), null);

        System.out.println("--- each shipped file names the page ---");
        if (inRepository) {
            for (String file : new String[] {"tools/earth/ATTRIBUTION-2.txt", "tools/world/ATTRIBUTION.txt"}) {
                check(file + " says \"Credits page\"", Files.exists(Path.of(file)) && read(Path.of(file)).contains("Credits page"), null);
            }
        } else {
            System.out.println("  (skipped)");
        }

        libraryNotices(sections, inRepository);
        runtimeNotices(sections);
        microsoftAndSource(sections, inRepository);

        System.out.println(fails == 0 ? "CreditsCheck: all ok" : "CreditsCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
