# PackagingCheck.java - 335 lines · 9 methods · 1 constants · harnesses

`ham/citybuildersim/PackagingCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The built image carries Temurin's runtime with its notices, Microsoft's terms for the C runtime
> files, the libraries' texts and the complete corresponding source archives, unchanged, and a
> build that lacks any of them stops before the zip (CR1-J).
> 
> WHY THIS EXISTS. The sold exe shipped Oracle's runtime, whose No-Fee Terms bar fees for a product
> bundled with it, and OpenJFX's binaries without the source GPL v2 section 3 asks for. Jerus chose
> (2026-11-17) Temurin and the source archives beside the exe. Build EXE.bat's copy steps fail
> silently, so the build ends in tools.Packaging's check; this harness holds that check to fixture
> images that cause each failure, holds the pins to the binaries the build ships, and holds the
> batch file to the order that makes the check a gate. No network: a fetch is tried only from an
> address that refuses it.
> 
> What it has to prove:
> 
>   1. A COMPLETE IMAGE PASSES. Staged from a cache holding every pinned archive, with Temurin's
>      runtime and notices, the check finds nothing.
>   2. A MISSING OR CHANGED SOURCE ARCHIVE STOPS THE BUILD. Gone from the image, changed in it,
>      absent from or changed in the cache, or unfetchable: each is a problem naming the archive,
>      and a failed fetch leaves no partial file.
>   3. A LOST NOTICE OR TERMS FILE STOPS THE BUILD. Microsoft's notice or terms, a library's
>      licence changed, a data attribution, the runtime's NOTICE, the README, a dangling
>      "Please see" in the runtime's legal folder: each is a problem.
>   4. A RUNTIME THAT IS NOT THE PINNED TEMURIN STOPS THE BUILD. Another vendor or version, Oracle's
>      No-Fee Terms, a non-GPL java.base licence, another OpenJFX build, a Microsoft library the
>      notice does not name.
>   5. THE PACKAGING JDK IS HELD TO THE PINS: vendor, vendor version and Java version, exactly.
>   6. THE PINS ARE THE SHIPPED BINARIES'. OpenJFX's pinned build is the one in the dependency jars,
>      pom.xml's version; the probes read the vendor from a real runtime.
>   7. BUILD EXE.BAT NAMES ITS JDK AND CHECKS THE IMAGE BEFORE THE ZIP. No jpackage from PATH or
>      Program Files; Temurin's own jlink and jpackage; fetch before jpackage, stage and verify
>      after it and before the zip, and a failed image removed.

**Uses:** [Packaging](Packaging.md) (35), [Credits](Credits.md) (11), [TempDirs](TempDirs.md) (5)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `PackagingCheck.CREDITS` | `Packaging : : credit` | The jar's credits texts, as Build EXE.bat's java -cp reads them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 55 | 281 | **type** `public class PackagingCheck` | The built image carries Temurin's runtime with its notices, Microsoft's terms for the C runtime files, the libraries' texts and the complete corresponding source archives, unchanged, and a build that lacks any of them... |
| 59 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 65 | 8 | `static boolean names(List<String> problems, String...words)` | True when some problem contains every one of the words. |
| 78 | 12 | `static Properties fixturePins(Path cache) throws IOException` | Pins of two small fixture archives, the rest as the real pins have them. |
| 92 | 4 | `static Packaging.Probes pinnedProbes(Properties pins)` | Probes that say the runtime is the pinned Temurin and the jar's OpenJFX the pinned build. |
| 98 | 30 | `static Path image(Properties pins) throws IOException` | An image as Build EXE.bat leaves it before staging: runtime, notices, legal folder, a jar, the data's credits. |
| 130 | 1 | **type** `record Fixture(Path cache, Path image, Properties pins)` | A complete, staged fixture image and its cache and pins. |
| 132 | 8 | `static Fixture staged() throws IOException` |  |
| 141 | 3 | `static List<String> verify(Fixture f) throws IOException` |  |
| 145 | 182 | `public static void main(String[] args) throws Exception` |  |
| 328 | 7 | `static void deleteTree(Path root) throws IOException` |  |

