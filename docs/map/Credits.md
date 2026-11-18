# Credits.java - 345 lines · 18 methods · 16 constants · model

`ham/citybuildersim/Credits.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The Credits page's sections: the Earth data's attribution, the nations' attribution, the fonts'
> and libraries' notices with their licences, the Java runtime's legal folder, Microsoft's runtime
> libraries' terms and where the source code is, each read as the exe ships it, and the text of each
> cut into paragraphs for the screen to draw (CR1, CR1-L, CR1-J). No JavaFX; the page is
> UserInterface.showCreditsMenu(), which computes nothing of its own.
> 
> WHY THIS EXISTS. The two attribution files (tools/earth/ATTRIBUTION-2.txt, tools/world/ATTRIBUTION.txt)
> said "the Credits page", and the game had none. It is sold, and the licences of what it is made
> from, CC BY and the SIL Open Font License among them, ask that the credit reach the player, not
> only a file beside the exe. The page reads the same files the exe ships (pom.xml puts them in the
> jar as credits/, the fonts' licence is already in fonts/). The libraries' jars omit legal files,
> so tools/credits holds their upstream release texts, pinned to the dependencies (CR1-L). The Java
> runtime's notices are not copied into the jar: they are read from the runtime the game runs on,
> which in the exe is the runtime Build EXE.bat linked, so the page cannot drift from it (CR1-J).
> There is one source of each text and a page that drifts from the file is not possible.
> CreditsCheck holds that; PackagingCheck holds the built image to the same files.

**Used by (4):** [CreditsCheck](CreditsCheck.md), [Packaging](Packaging.md), [PackagingCheck](PackagingCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 168 | · the Java runtime's legal folder (CR1-J) |
| 273 | · the source archives (CR1-J) |
| 315 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `Credits.EARTH` | `"/credits/earth/ATTRIBUTION-2.txt"` | The Earth data's attribution, in the jar (pom.xml copies tools/earth/ATTRIBUTION-2.txt there). |
| 50 | `Credits.NATIONS` | `"/credits/world/ATTRIBUTION.txt"` | The nations' attribution, in the jar (pom.xml copies tools/world/ATTRIBUTION.txt there). |
| 53 | `Credits.FONTS` | `"/fonts/OFL.txt"` | The fonts' licence in full, the SIL Open Font License 1.1 with IBM's copyright line, as it sits beside the fonts. |
| 56 | `Credits.GSON` | `"/credits/gson/LICENSE"` | Gson's Apache 2.0 licence, copied from its upstream release because its jar omits it. |
| 59 | `Credits.OPENJFX` | `"/credits/openjfx/LICENSE"` | OpenJFX's GPL v2 licence including the Classpath Exception, copied from its upstream release. |
| 62 | `Credits.OPENJFX_FILES` | `List.of(OPENJFX, "/credits/openjfx/ADDITIONAL_LICENSE_INFO", "/credits/openjf...` | OpenJFX's licence and notices, including the third-party code in the bundled graphics module. |
| 67 | `Credits.MICROSOFT` | `"/credits/microsoft/NOTICE.txt"` | The notice that names Microsoft's runtime files the game carries and the terms they come under (CR1-J). |
| 70 | `Credits.MICROSOFT_FILES` | `List.of(MICROSOFT, "/credits/microsoft/LICENSE-vc-runtime-2015-2022.txt")` | The notice and Microsoft's licence terms for the Visual C++ 2015-2022 runtime, in full. |
| 74 | `Credits.SOURCE_PINS` | `"/credits/source-archives.properties"` | The source archives' pins: each archive's file, version, origin and SHA-256 (CR1-J); tools.Packaging stages them. |
| 77 | `Credits.SOURCE_FOLDER` | `"source"` | The folder beside the exe that holds the complete corresponding source archives and their README. |
| 80 | `Credits.LEGAL_FOLDER` | `"legal"` | The folder beside the exe that holds a copy of tools/credits, the libraries' and Microsoft's texts. |
| 83 | `Credits.RUNTIME_LEGAL` | `"runtime/legal"` | The runtime's legal folder as the exe ships it, beside the exe; the page reads it from java.home. |
| 86 | `Credits.FONT_NOTICE` | `"The type is IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., " +...` | The line above the fonts' licence: what the type is, whose, and the licence it is used under. |
| 90 | `Credits.MICROSOFT_LEAD` | `"Microsoft's C and C++ runtime libraries, which the Java runtime and OpenJFX'...` | The line above Microsoft's notice and terms. |
| 101 | `Credits.Loaded.SECTIONS` | `List.copyOf(sections(Credits : : readResource, javaHome()))` | The shared snapshot of the page's sections and complete legal texts. |
| 181 | `Credits.REFERENCE` | `Pattern.compile("\\A\\s*Please see(\\S+)\\s*\\z")` | jlink's note in place of a file another module already ships: on Windows, where it makes no links, "Please see ..\java.base\LICENSE"; the path is relative to the module's folder. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 310 | **type** `public final class Credits` | The Credits page's sections: the Earth data's attribution, the nations' attribution, the fonts' and libraries' notices with their licences, the Java runtime's legal folder, Microsoft's runtime libraries' terms and whe... |
| 38 | 1 | `private Credits()` |  |
| 41 | 4 | **type** `public record Section(String title, String resource, String besideExe, String lead, String text)` | One block of the page: its heading, where its text is, the file that ships beside the exe, the line above it, and the text itself. |
| 43 | 1 | `public boolean missing()` _(in Credits.Section)_ | True when this build carries no copy of the text; the page then says where the file is. |
| 94 | 3 | `public static List<Section> sections()` | The page's sections, in order, loaded once; later menu visits do no archive or disk I/O. |
| 99 | 4 | **type** `private static final class Loaded` | The first visit loads the immutable texts; JVM class initialization safely publishes them. |
| 105 | 3 | `static Path javaHome()` | The runtime the game runs on: in the exe, its own runtime folder; from the IDE, the JDK. |
| 110 | 3 | `static List<Section> sections(Function<String, String> reader)` | The page through any reader of a classpath path (null for a file this build lacks), over this JVM's runtime; a harness's fixture. |
| 115 | 23 | `static List<Section> sections(Function<String, String> reader, Path runtime)` | The same over a runtime folder of the harness's choosing, whose legal/ and NOTICE are read. |
| 140 | 10 | `static String readAll(Function<String, String> reader, List<String> paths)` | All the named files in order, with their names; missing if any one is absent, rather than showing a partial licence. |
| 152 | 9 | `static String readResource(String path)` | A classpath file as UTF-8 text, unchanged but for a leading byte-order mark; null when it is not there or cannot be read. |
| 163 | 4 | `static String text(byte[] bytes)` | Bytes as UTF-8 text, unchanged but for a leading byte-order mark. |

### the Java runtime's legal folder (CR1-J) (lines 168-272)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 1 | **type** `public record LegalFile(String name, String text, String refersTo)` | One file the runtime ships as a notice: its name under the runtime folder ("NOTICE", or legal/module/file), its text in full, and the file it refers to, as a name of the same kind, when it is one of jlink's "Please se... |
| 190 | 49 | `public static List<LegalFile> runtimeLegal(Path runtime, List<String> problems)` | The notices a runtime folder ships: its NOTICE, when it has one (Temurin's distribution notice, which Build EXE.bat puts beside the linked runtime's release file), then every file under legal/, java.base's first and t... |
| 241 | 8 | `private static String readFile(Path file, List<String> problems)` | A file's text, or null with a problem added. |
| 251 | 8 | `static String joined(List<LegalFile> files)` | Each file under its name, in order, as the page shows them. |
| 261 | 11 | `static String runtimeLead(List<LegalFile> files)` | The line above the runtime's notices: whose runtime, which version, and how many files follow. |

### the source archives (CR1-J) (lines 273-314)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 9 | `public static Properties properties(String text)` | A properties text as Properties. |
| 287 | 5 | `public static List<String> archives(Properties pins)` | The archives the pins name, in their order ("archives=openjfx,runtime"). |
| 297 | 17 | `public static String sourceReadme(Properties pins)` | The README beside the archives, and the page's section: what the archives are, why they ship, and each one's file, version, origin and SHA-256. |

### the screen (lines 315-345)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 16 | `public static List<String> paragraphs(String text)` | The text cut on its blank lines (empty or whitespace), each paragraph keeping its own line breaks, with \r removed and no blank paragraph at either end; empty for null. |
| 340 | 5 | `public static String missingNote(Section s)` | What the page says for a section this build lacks: where the file is. |

