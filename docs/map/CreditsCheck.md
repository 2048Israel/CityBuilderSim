# CreditsCheck.java - 462 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/CreditsCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and
> licence, the libraries' licences and notices, the Java runtime's legal folder, Microsoft's terms for
> its runtime files and where the source is, and says where a file is when this build lacks it
> (CR1, CR1-L, CR1-J).
> 
> WHY THIS EXISTS. The game is sold, and the licences of its data (CC BY among them) and of its
> type (the SIL Open Font License) ask that the credit reach the player. The page reads the files
> from the jar, which pom.xml copies from tools/; Build EXE.bat copies the same files beside the
> exe. A third attribution file added to the batch file and not to the page, a page cut down from
> its file, or a font added without its licence would each leave a credit unshown without any
> error. Files are read from the working directory (the repository), as BrandingCheck reads
> them; elsewhere those sections say so and are skipped.
> 
> What it has to prove:
> 
>   1. EVERY FILE THE EXE SHIPS AS A CREDIT IS ON THE PAGE, IN FULL. Each ATTRIBUTION copy in
>      Build EXE.bat has a section whose text is the file under tools/, exactly, and whose
>      besideExe is the copy's destination.
>   2. THE FONTS' NOTICE AND LICENCE. Every font in resources/fonts/ is IBM Plex, OFL.txt carries
>      IBM's copyright and the licence's name, the notice says both, and the fonts' section is
>      the notice over the licence in full.
>   3. A BUILD WITHOUT A FILE SAYS WHERE THE FILE IS. A section whose text is null is missing(),
>      the others are not, and the note names the file beside the exe.
>   4. THE PARAGRAPHS KEEP THE WHOLE TEXT. Cut on blank lines, no non-blank line is lost or
>      moved; a blank run is one cut and a \r goes.
>   5. EACH SHIPPED FILE NAMES THE PAGE. Both files say "Credits page", where the game shows them.
>   6. THE LIBRARIES FOLLOW THE FONTS, WITH THEIR RELEASE TEXTS IN FULL. The fallback copies'
>      hashes and versions are pinned to upstream releases and pom.xml. The jars lack legal
>      files; if that changes, re-audit and take their texts from the dependency instead.
>   7. REVISITING THE PAGE REUSES ITS TEXTS. All visits share an immutable section list;
>      the injected reader remains independent for missing-file fixtures.
>   8. THE JAVA RUNTIME'S NOTICES ARE ITS OWN (CR1-J). The runtime's NOTICE and every file of
>      its legal folder, java.base's first, in full; jlink's "Please see" notes kept and resolved
>      inside the folder, and one naming a file it lacks, or outside it, marks the section missing.
>   9. MICROSOFT'S TERMS FOR ITS RUNTIME FILES. The notice and Microsoft's two pinned licences in
>      full, the C++ files under Visual Studio's, the C runtime under the SDK's, the notice naming
>      every Microsoft library in OpenJFX's Windows graphics jar (skipped where Maven gives no -win jar).
>  10. WHERE THE SOURCE IS. The section is the README the pins make, naming each archive.
>  11. THE GAME'S OWN COPYRIGHT FIRST (the CR1-J review). Microsoft's Distributable Code terms ask
>      for the program's copyright notice: the page opens with it, and the exe carries the same.

**Uses:** [Credits](Credits.md) (110), [GameVersion](GameVersion.md) (3), [TempDirs](TempDirs.md) (2), [Packaging](Packaging.md) (2)

## Fields (state)

| line | field | says |
|---:|---|---|
| 68 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 66 | 397 | **type** `public class CreditsCheck` | The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and licence, the libraries' licences and notices, the Java runtime's legal folder, Microsoft's terms for its runtime files an... |
| 70 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 75 | 3 | `static String read(Path p) throws IOException` |  |
| 80 | 5 | `static List<String> lines(String text)` | The text's non-blank lines, in order, with any \r gone. |
| 87 | 60 | `static void libraryNotices(List<Credits.Section> sections, boolean inRepository) throws Exception` | The release copies reach the page unchanged, and a dependency upgrade cannot silently keep old notices. |
| 149 | 12 | `static void dependencyNotices() throws IOException` | The audited jars omit legal files; a new NOTICE or licence must be taken from the jar, not ignored. |
| 163 | 3 | `static Credits.Section section(List<Credits.Section> sections, String title)` | A section by title, or null. |
| 168 | 62 | `static void runtimeNotices(List<Credits.Section> sections) throws IOException` | The runtime's notices are its own legal folder's, every file in full, every "Please see" inside it (CR1-J). |
| 232 | 89 | `static void microsoftAndSource(List<Credits.Section> sections, boolean inRepository) throws IOException` | Microsoft's notice and terms, and where the source is, as the jar and the pins hold them (CR1-J). |
| 322 | 7 | `static byte[] sha256(byte[] bytes)` |  |
| 330 | 132 | `public static void main(String[] args) throws Exception` |  |

