# Packaging.java - 454 lines · 21 methods · 7 constants · tools

`ham/citybuildersim/tools/Packaging.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Build EXE.bat's source archives and its check of the built image (CR1-J): the packaging JDK
> checked against the pins, the complete corresponding sources fetched once into a cache outside
> git and staged beside the exe, and the finished image held to every notice, terms file and
> archive it must carry before anything is zipped.
> 
>     java -cp target\CityBuilderSim-1.0-SNAPSHOT-executable.jar ham.citybuildersim.tools.Packaging jdk    <JDK folder>
>     java -cp ...                                               ham.citybuildersim.tools.Packaging fetch  <cache folder>
>     java -cp ...                                               ham.citybuildersim.tools.Packaging stage  <cache folder> <image folder>
>     java -cp ...                                               ham.citybuildersim.tools.Packaging verify <image folder>
> 
> Each exits 0 when all is well and 1, after a plain sentence for each lack, when not. The pins
> are the jar's copy of tools/credits/source-archives.properties, the same the Credits page reads.
> 
> WHY THIS EXISTS. The sold exe carried Oracle's runtime under the No-Fee Terms, which bar fees for
> a product bundled with it, and OpenJFX under the GPL, whose section 3 asks for the complete
> source to go with the binaries; the libraries' notices reached the Credits page (CR1-L) but the
> runtime's, the source and Microsoft's terms for the C runtime files did not (CR1-J). A step in a
> batch file that copies an archive fails silently, so the build ends in this check, which
> PackagingCheck holds on fixtures: a missing archive, a changed one, a lost notice or a runtime
> from another vendor each stop the build before the zip.

**Uses:** [Credits](Credits.md) (26)

**Used by (2):** [CreditsCheck](CreditsCheck.md), [PackagingCheck](PackagingCheck.md)

## Sections

| line | section |
|---:|---|
| 145 | · the packaging JDK |
| 208 | · the archives |
| 289 | · the check of the built image |
| 404 | · the probes |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `Packaging.MICROSOFT_DLL` | `Pattern.compile("(?i)(?:msvcp\|vcruntime\|ucrtbase\|api-ms-win-\|concrt\|vccorlib\|...` | A file name that is one of Microsoft's C or C++ runtime libraries, by its family's name. |
| 66 | `Packaging.MICROSOFT_COVERED` | `Pattern.compile("(?i)(?:msvcp140(?:_[12])?\|vcruntime140(?:_1)?\|ucrtbase\|api-m...` | The Microsoft files tools/credits/microsoft/NOTICE.txt names and whose terms were audited; another needs a new audit. |
| 69 | `Packaging.GPL2` | `"Version 2, June 1991"` | The words that open the GNU GPL, version 2, as OpenJDK's legal/java.base/LICENSE carries it. |
| 72 | `Packaging.NFTC` | `"No-Fee Terms and Conditions"` | The words that mark Oracle's No-Fee Terms and Conditions, which bar the sold game; a runtime carrying them is refused. |
| 75 | `Packaging.README` | `"README.txt"` | The README written beside the archives. |
| 81 | `Packaging.REAL` | `new Probes(Packaging : : runtimeStrings, Packaging : : fxVersion)` | The real probes: the runtime's java.base version constants, and the jar's OpenJFX build. |
| 141 | `Packaging.DATA_NOTICES` | `Map.of("data/earth-2-ATTRIBUTION.txt", Credits.EARTH, "data/world-ATTRIBUTION...` | The attribution files beside the data, each with the jar's copy it must equal (Build EXE.bat copies them). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 397 | **type** `public final class Packaging` | Build EXE.bat's source archives and its check of the built image (CR1-J): the packaging JDK checked against the pins, the complete corresponding sources fetched once into a cache outside git and staged beside the exe,... |
| 60 | 1 | `private Packaging()` |  |
| 78 | 1 | **type** `public record Probes(Function<Path, Set<String>> runtimeStrings, Function<Path, String> fxVersion)` | How a check reads what is inside the built binaries; PackagingCheck's fixtures replace it. |
| 83 | 19 | `public static void main(String[] args) throws Exception` |  |
| 103 | 6 | `private static void need(String[] args, int n)` |  |
| 111 | 8 | `public static Properties pins()` | The jar's pins; the build stops without them. |
| 121 | 7 | `public static String credit(String resource)` | A credits text from this jar, as the Credits page reads it; null when the jar lacks it. |
| 130 | 9 | `public static List<String> legalFiles()` | The classpath texts the folder beside the exe carries, as legal/ followed by their path under credits/. |

### the packaging JDK (lines 145-207)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 11 | `public static Map<String, String> release(Path file) throws IOException` | A release file's KEY="value" lines. |
| 161 | 19 | `public static List<String> checkJdk(Path jdk, Properties pins) throws IOException` | The packaging JDK is the one whose source the pins name: vendor, vendor version and Java version, exactly. |
| 186 | 17 | `public static List<String> pinProblems(Properties pins)` | The pins agree with themselves: each archive's file and URL name its version (a "+" written "_" in a file name, "%2B" in a URL), and the runtime's vendor and Java versions are its version's. |
| 204 | 3 | `private static void expect(List<String> problems, String what, String got, String want)` |  |

### the archives (lines 208-288)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 10 | `public static String sha256(Path file) throws IOException` | A file's SHA-256 in lower-case hex, read as a stream. |
| 223 | 3 | `public static boolean pinned(Path file, String sha) throws IOException` | True when the file is there and its SHA-256 is the pin's. |
| 228 | 33 | `public static List<String> fetch(Path cache, Properties pins) throws IOException` | Fetches each pinned archive the cache lacks or holds changed, through the system's proxy settings; a download whose hash differs is thrown away. |
| 267 | 21 | `public static List<String> stage(Path cache, Path image, Properties pins, Function<String, String> credits) throws IOException` | Copies each pinned archive from the cache into the image's source folder with the README, and the jar's legal texts into its legal folder; an archive missing from the cache or changed there is a problem, and nothing i... |

### the check of the built image (lines 289-403)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 71 | `public static List<String> verify(Path image, Properties pins, Function<String, String> credits, Probes probes) throws IOException` | Everything the image must carry before it is zipped: Temurin's runtime and its notices with every reference inside its legal folder, no Oracle No-Fee Terms, Microsoft's terms for every Microsoft library it carries, th... |
| 370 | 6 | `private static void same(List<String> problems, Path image, Path file, String want, String what) throws IOException` | A file beside the exe holds the jar's text exactly. |
| 378 | 3 | `public static List<String> microsoftLibraries(Path image) throws IOException` | Microsoft's libraries the image carries: in the runtime's bin, and inside the game's jar. |
| 383 | 20 | `public static List<String> microsoftLibraries(Path image, List<String> problems) throws IOException` | The same, a jar that cannot be opened named in {@code problems}: its libraries cannot be seen, so the image is not fit. |

### the probes (lines 404-454)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 8 | `public static Set<String> runtimeStrings(Path runtime)` | The string constants of java.lang.VersionProps in a runtime image's java.base, read through that image's own jrt file system: its vendor ("Eclipse Adoptium") and vendor version ("Temurin-21.0.13+9") among them. |
| 422 | 21 | `public static Set<String> constants(byte[] classFile) throws IOException` | A class file's UTF-8 constants. |
| 445 | 9 | `public static String fxVersion(Path jar)` | OpenJFX's build in a jar, as its own VersionInfo says ("21+31"); null when the jar has none. |

