# CreditsCheck.java - 177 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/CreditsCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and
> licence, and says where a file is when this build lacks it (CR1, 2026-11-09).
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

**Uses:** [Credits](Credits.md) (23)

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 139 | **type** `public class CreditsCheck` | The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and licence, and says where a file is when this build lacks it (CR1, 2026-11-09). |
| 43 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 48 | 3 | `static String read(Path p) throws IOException` |  |
| 53 | 5 | `static List<String> lines(String text)` | The text's non-blank lines, in order, with any \r gone. |
| 59 | 118 | `public static void main(String[] args) throws Exception` |  |

