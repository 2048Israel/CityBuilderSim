package ham.citybuildersim;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The Credits page's sections: the Earth data's attribution, the nations' attribution, the fonts'
 * and libraries' notices with their licences, the Java runtime's legal folder, Microsoft's runtime
 * libraries' terms and where the source code is, each read as the exe ships it, and the text of each
 * cut into paragraphs for the screen to draw (CR1, CR1-L, CR1-J). No JavaFX; the page is
 * UserInterface.showCreditsMenu(), which computes nothing of its own.
 *
 * WHY THIS EXISTS. The two attribution files (tools/earth/ATTRIBUTION-2.txt, tools/world/ATTRIBUTION.txt)
 * said "the Credits page", and the game had none. It is sold, and the licences of what it is made
 * from, CC BY and the SIL Open Font License among them, ask that the credit reach the player, not
 * only a file beside the exe. The page reads the same files the exe ships (pom.xml puts them in the
 * jar as credits/, the fonts' licence is already in fonts/). The libraries' jars omit legal files,
 * so tools/credits holds their upstream release texts, pinned to the dependencies (CR1-L). The Java
 * runtime's notices are not copied into the jar: they are read from the runtime the game runs on,
 * which in the exe is the runtime Build EXE.bat linked, so the page cannot drift from it (CR1-J).
 * There is one source of each text and a page that drifts from the file is not possible.
 * CreditsCheck holds that; PackagingCheck holds the built image to the same files.
 */
public final class Credits {

    private Credits() { }

    /** One block of the page: its heading, where its text is, the file that ships beside the exe, the line above it, and the text itself. */
    public record Section(String title, String resource, String besideExe, String lead, String text) {
        /** True when this build carries no copy of the text; the page then says where the file is. */
        public boolean missing() { return text == null; }
    }

    /** The Earth data's attribution, in the jar (pom.xml copies tools/earth/ATTRIBUTION-2.txt there). */
    public static final String EARTH = "/credits/earth/ATTRIBUTION-2.txt";

    /** The nations' attribution, in the jar (pom.xml copies tools/world/ATTRIBUTION.txt there). */
    public static final String NATIONS = "/credits/world/ATTRIBUTION.txt";

    /** The fonts' licence in full, the SIL Open Font License 1.1 with IBM's copyright line, as it sits beside the fonts. */
    public static final String FONTS = "/fonts/OFL.txt";

    /** Gson's Apache 2.0 licence, copied from its upstream release because its jar omits it. */
    public static final String GSON = "/credits/gson/LICENSE";

    /** OpenJFX's GPL v2 licence including the Classpath Exception, copied from its upstream release. */
    public static final String OPENJFX = "/credits/openjfx/LICENSE";

    /** OpenJFX's licence and notices, including the third-party code in the bundled graphics module. */
    public static final List<String> OPENJFX_FILES = List.of(OPENJFX,
            "/credits/openjfx/ADDITIONAL_LICENSE_INFO", "/credits/openjfx/ASSEMBLY_EXCEPTION",
            "/credits/openjfx/jpeg_fx.md", "/credits/openjfx/mesa3d.md");

    /** The notice that names Microsoft's runtime files the game carries and the terms they come under (CR1-J). */
    public static final String MICROSOFT = "/credits/microsoft/NOTICE.txt";

    /** The notice and Microsoft's licence terms in full: the Visual C++ 2015-2022 runtime's, and the Windows SDK's, whose Distributable Code the Universal C Runtime is. */
    public static final List<String> MICROSOFT_FILES = List.of(MICROSOFT,
            "/credits/microsoft/LICENSE-vc-runtime-2015-2022.txt", "/credits/microsoft/LICENSE-windows-sdk.txt");

    /** The source archives' pins: each archive's file, version, origin and SHA-256 (CR1-J); tools.Packaging stages them. */
    public static final String SOURCE_PINS = "/credits/source-archives.properties";

    /** The folder beside the exe that holds the complete corresponding source archives and their README. */
    public static final String SOURCE_FOLDER = "source";

    /** The folder beside the exe that holds a copy of tools/credits, the libraries' and Microsoft's texts. */
    public static final String LEGAL_FOLDER = "legal";

    /** The runtime's legal folder as the exe ships it, beside the exe; the page reads it from java.home. */
    public static final String RUNTIME_LEGAL = "runtime/legal";

    /** The line above the fonts' licence: what the type is, whose, and the licence it is used under. */
    public static final String FONT_NOTICE = "The type is IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., "
            + "with Reserved Font Name \"Plex\", used under the SIL Open Font License, Version 1.1, which follows in full.";

    /** The line above Microsoft's notice and terms. */
    public static final String MICROSOFT_LEAD = "Microsoft's C and C++ runtime libraries, which the Java runtime and OpenJFX's "
            + "graphics library carry. Their notice and Microsoft's licence terms for them, the Visual C++ runtime's and "
            + "the Windows SDK's, follow in full.";

    /**
     * The game's own copyright notice. The CR1-J review: Microsoft's Distributable Code terms ask the
     * program that carries the code to display its valid copyright notice. The name is the one the
     * exe has shown since the itch.io build (jpackage's --vendor); Build EXE.bat passes this as
     * --copyright, and the page's first section says it.
     */
    public static final String COPYRIGHT = "Copyright (c) 2026 Jerus";

    /** The page's first section: whose the game is, and that the works below are their authors'. */
    public static final String GAME_NOTICE = GameVersion.NAME + " is " + COPYRIGHT + ". The game's own code, rules, "
            + "tables, art and words are Jerus's. The data, type, libraries and runtime in the sections below are their "
            + "authors' and are used under the licences shown with them.";

    /** The page's sections, in order, loaded once; later menu visits do no archive or disk I/O. */
    public static List<Section> sections() {
        return Loaded.SECTIONS;
    }

    /** The first visit loads the immutable texts; JVM class initialization safely publishes them. */
    private static final class Loaded {
        /** The shared snapshot of the page's sections and complete legal texts. */
        private static final List<Section> SECTIONS = List.copyOf(sections(Credits::readResource, javaHome()));
    }

    /** The runtime the game runs on: in the exe, its own runtime folder; from the IDE, the JDK. */
    static Path javaHome() {
        return Path.of(System.getProperty("java.home"));
    }

    /** The page through any reader of a classpath path (null for a file this build lacks), over this JVM's runtime; a harness's fixture. */
    static List<Section> sections(Function<String, String> reader) {
        return sections(reader, javaHome());
    }

    /** The same over a runtime folder of the harness's choosing, whose legal/ and NOTICE are read. */
    static List<Section> sections(Function<String, String> reader, Path runtime) {
        List<Section> out = new ArrayList<>();
        out.add(new Section(GameVersion.NAME, null, null, null, GAME_NOTICE));
        out.add(new Section("The Earth", EARTH, "data/earth-2-ATTRIBUTION.txt", null, reader.apply(EARTH)));
        out.add(new Section("The nations", NATIONS, "data/world-ATTRIBUTION.txt", null, reader.apply(NATIONS)));
        out.add(new Section("The fonts", FONTS, null, FONT_NOTICE, reader.apply(FONTS)));
        out.add(new Section("Gson", GSON, null,
                "Gson, used under the Apache License, Version 2.0, which follows in full.", reader.apply(GSON)));
        out.add(new Section("OpenJFX", OPENJFX, null,
                "OpenJFX, used under the GNU General Public License, version 2, with the Classpath Exception. "
                + "Its licence and notices follow in full. This software is based in part on the work of the Independent JPEG Group.",
                readAll(reader, OPENJFX_FILES)));
        List<String> problems = new ArrayList<>();
        List<LegalFile> legal = runtimeLegal(runtime, problems);
        for (String p : problems) System.out.println("Credits: the runtime's notices: " + p);
        out.add(new Section("The Java runtime", RUNTIME_LEGAL, RUNTIME_LEGAL, runtimeLead(legal), legal == null ? null : joined(legal)));
        out.add(new Section("Microsoft's runtime libraries", MICROSOFT, LEGAL_FOLDER + "/microsoft", MICROSOFT_LEAD,
                readAll(reader, MICROSOFT_FILES)));
        String pins = reader.apply(SOURCE_PINS);
        out.add(new Section("The source code", SOURCE_PINS, SOURCE_FOLDER + "/README.txt",
                "Where the complete source code of OpenJFX and of the Java runtime is, with the scripts that built them.",
                pins == null ? null : sourceReadme(properties(pins))));
        return out;
    }

    /** All the named files in order, with their names; missing if any one is absent, rather than showing a partial licence. */
    static String readAll(Function<String, String> reader, List<String> paths) {
        StringBuilder out = new StringBuilder();
        for (String path : paths) {
            String text = reader.apply(path);
            if (text == null) return null;
            if (out.length() > 0) out.append("\n\n");
            out.append(path.substring(path.lastIndexOf('/') + 1)).append("\n\n").append(text);
        }
        return out.toString();
    }

    /** A classpath file as UTF-8 text, unchanged but for a leading byte-order mark; null when it is not there or cannot be read. */
    static String readResource(String path) {
        try (InputStream in = Credits.class.getResourceAsStream(path)) {
            if (in == null) return null;
            return text(in.readAllBytes());
        } catch (IOException e) {
            System.out.println("Credits: " + path + " could not be read: " + e.getMessage());
            return null;
        }
    }

    /** Bytes as UTF-8 text, unchanged but for a leading byte-order mark. */
    static String text(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        return text.startsWith("﻿") ? text.substring(1) : text;
    }

    /* ----- the Java runtime's legal folder (CR1-J) ----- */

    /**
     * One file the runtime ships as a notice: its name under the runtime folder ("NOTICE", or
     * legal/module/file), its text in full, and the file it refers to, as a name of the same kind,
     * when it is one of jlink's "Please see" notes (null otherwise).
     */
    public record LegalFile(String name, String text, String refersTo) { }

    /**
     * jlink's note in place of a file another module already ships: on Windows, where it makes no
     * links, "Please see ..\java.base\LICENSE"; the path is relative to the module's folder.
     */
    static final Pattern REFERENCE = Pattern.compile("\\A\\s*Please see (\\S+)\\s*\\z");

    /**
     * The notices a runtime folder ships: its NOTICE, when it has one (Temurin's distribution notice,
     * which Build EXE.bat puts beside the linked runtime's release file), then every file under
     * legal/, java.base's first and the rest by module, each module's by name. Null when legal/ is
     * absent or empty, when a file cannot be read, or when a "Please see" note names a file outside
     * legal/ or one it lacks; each of those is added to problems as a sentence.
     */
    public static List<LegalFile> runtimeLegal(Path runtime, List<String> problems) {
        Path legal = runtime.resolve("legal");
        if (!Files.isDirectory(legal)) {
            problems.add(legal + " is not there");
            return null;
        }
        List<Path> files;
        try (Stream<Path> walk = Files.walk(legal)) {
            files = new ArrayList<>(walk.filter(Files::isRegularFile).toList());
        } catch (IOException | UncheckedIOException e) {
            // The walk reports a folder it cannot read mid-stream, unchecked; either way the page says so rather than fail to load.
            problems.add(legal + " could not be listed: " + e.getMessage());
            return null;
        }
        if (files.isEmpty()) {
            problems.add(legal + " holds no file");
            return null;
        }
        files.sort(Comparator.comparing((Path p) -> !legal.relativize(p).getName(0).toString().equals("java.base"))
                .thenComparing(p -> legal.relativize(p).toString().replace('\\', '/')));
        List<LegalFile> out = new ArrayList<>();
        Path notice = runtime.resolve("NOTICE");
        boolean ok = true;
        if (Files.isRegularFile(notice)) {
            String text = readFile(notice, problems);
            if (text == null) ok = false;
            else out.add(new LegalFile("NOTICE", text, null));
        }
        for (Path file : files) {
            String name = "legal/" + legal.relativize(file).toString().replace('\\', '/');
            String text = readFile(file, problems);
            if (text == null) { ok = false; continue; }
            String refersTo = null;
            Matcher m = REFERENCE.matcher(text);
            if (m.matches()) {
                Path target = file.getParent().resolve(m.group(1).replace('\\', '/')).normalize();
                if (!target.startsWith(legal.normalize())) {
                    problems.add(name + " refers to " + m.group(1) + ", outside the legal folder");
                    ok = false;
                } else if (!Files.isRegularFile(target)) {
                    problems.add(name + " refers to " + m.group(1) + ", which the legal folder lacks");
                    ok = false;
                } else {
                    refersTo = "legal/" + legal.normalize().relativize(target).toString().replace('\\', '/');
                }
            }
            out.add(new LegalFile(name, text, refersTo));
        }
        return ok ? List.copyOf(out) : null;
    }

    /** A file's text, or null with a problem added. */
    private static String readFile(Path file, List<String> problems) {
        try {
            return text(Files.readAllBytes(file));
        } catch (IOException e) {
            problems.add(file + " could not be read: " + e.getMessage());
            return null;
        }
    }

    /** Each file under its name, in order, as the page shows them. */
    static String joined(List<LegalFile> files) {
        StringBuilder out = new StringBuilder();
        for (LegalFile f : files) {
            if (out.length() > 0) out.append("\n\n");
            out.append(f.name()).append("\n\n").append(f.text());
        }
        return out.toString();
    }

    /** The line above the runtime's notices: whose runtime, which version, and how many files follow. */
    static String runtimeLead(List<LegalFile> files) {
        String vendor = System.getProperty("java.vendor", "?");
        String version = System.getProperty("java.vendor.version");
        if (version == null || version.isBlank()) version = System.getProperty("java.runtime.version", "?");
        String lead = "The Java runtime the game runs on, " + vendor + "'s " + version + ". ";
        if (files == null) return lead + "Its licence and notices could not all be read here.";
        long references = files.stream().filter(f -> f.refersTo() != null).count();
        return lead + "Its licence and notices, the " + files.size() + " files of its runtime folder's NOTICE and legal folder, follow in full, "
                + "java.base's first." + (references == 0 ? "" : " A file that says \"Please see\" another names one of these, "
                + "shown in full under its own name.");
    }

    /* ----- the source archives (CR1-J) ----- */

    /** A properties text as Properties. */
    public static Properties properties(String text) {
        Properties p = new Properties();
        try {
            p.load(new StringReader(text));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }

    /** The archives the pins name, in their order ("archives=openjfx,runtime"). */
    public static List<String> archives(Properties pins) {
        List<String> out = new ArrayList<>();
        for (String a : pins.getProperty("archives", "").split(",")) if (!a.isBlank()) out.add(a.trim());
        return out;
    }

    /**
     * The README beside the archives, and the page's section: what the archives are, why they ship,
     * and each one's file, version, origin and SHA-256. tools.Packaging writes the same text.
     */
    public static String sourceReadme(Properties pins) {
        StringBuilder out = new StringBuilder();
        out.append("The complete corresponding source code\n\n");
        out.append("OpenJFX and the Java runtime are free software under the GNU General Public License, version 2, "
                + "with the Classpath Exception. As section 3 of that licence asks, the game carries their complete "
                + "corresponding source code, with the scripts that built them, unchanged from their publishers' releases, "
                + "in the folder " + SOURCE_FOLDER + " beside CityBuilderSim.exe:");
        for (String a : archives(pins)) {
            out.append("\n\n").append(pins.getProperty(a + ".file")).append('\n')
                    .append(pins.getProperty(a + ".what")).append(", version ").append(pins.getProperty(a + ".version")).append('\n')
                    .append("from ").append(pins.getProperty(a + ".url")).append('\n')
                    .append("SHA-256 ").append(pins.getProperty(a + ".sha256"));
        }
        String jlink = pins.getProperty("runtime.jlink");
        if (jlink != null) out.append("\n\nThe folder runtime beside CityBuilderSim.exe is Temurin's JDK, version ")
                .append(pins.getProperty("runtime.version")).append(", cut down by its own jlink, unchanged otherwise, with this command:\n\n")
                .append(jlink).append("\n\nand the JDK's NOTICE copied into it. jpackage, from the same JDK, then made "
                + "CityBuilderSim.exe around that runtime and the game's jar.");
        out.append("\n\nEach archive is the publisher's own, byte for byte; its SHA-256 above lets anyone check it "
                + "against the publisher's copy.\n");
        return out.toString();
    }

    /* ----- the screen ----- */

    /**
     * The text cut on its blank lines (empty or whitespace), each paragraph keeping its own line breaks,
     * with \r removed and no blank paragraph at either end; empty for null. The screen draws one label
     * for each, since one label holding twenty kilobytes is slow to lay out.
     */
    public static List<String> paragraphs(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) return out;
        StringBuilder paragraph = new StringBuilder();
        for (String line : text.replace("\r", "").split("\n", -1)) {
            if (line.isBlank()) {
                if (paragraph.length() > 0) out.add(paragraph.toString());
                paragraph.setLength(0);
            } else {
                if (paragraph.length() > 0) paragraph.append('\n');
                paragraph.append(line);
            }
        }
        if (paragraph.length() > 0) out.add(paragraph.toString());
        return out;
    }

    /** What the page says for a section this build lacks: where the file is. */
    public static String missingNote(Section s) {
        return s.besideExe() == null
                ? "This build carries no complete copy of the licence and notices for " + s.title() + "."
                : "This build carries no copy of it; it ships beside the exe as " + s.besideExe() + ".";
    }
}
