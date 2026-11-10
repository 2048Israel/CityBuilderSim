# Credits.java - 100 lines · 7 methods · 4 constants · model

`ham/citybuildersim/Credits.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The Credits page's sections: the Earth data's attribution, the nations' attribution and the
> fonts' notice with its licence, each read from the jar as the exe ships it, and the text of each
> cut into paragraphs for the screen to draw (CR1, 2026-11-09). No JavaFX; the page is
> UserInterface.showCreditsMenu(), which computes nothing of its own.
> 
> WHY THIS EXISTS. The two attribution files (tools/earth/ATTRIBUTION-2.txt, tools/world/ATTRIBUTION.txt)
> said "the Credits page", and the game had none. It is sold, and the licences of what it is made
> from, CC BY and the SIL Open Font License among them, ask that the credit reach the player, not
> only a file beside the exe. The page reads the same files the exe ships (pom.xml puts them in the
> jar as credits/, the fonts' licence is already in fonts/), so there is one source of each text
> and a page that drifts from the file is not possible. CreditsCheck holds that.

**Used by (2):** [CreditsCheck](CreditsCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Credits.EARTH` | `"/credits/earth/ATTRIBUTION-2.txt"` | The Earth data's attribution, in the jar (pom.xml copies tools/earth/ATTRIBUTION-2.txt there). |
| 37 | `Credits.NATIONS` | `"/credits/world/ATTRIBUTION.txt"` | The nations' attribution, in the jar (pom.xml copies tools/world/ATTRIBUTION.txt there). |
| 40 | `Credits.FONTS` | `"/fonts/OFL.txt"` | The fonts' licence in full, the SIL Open Font License 1.1 with IBM's copyright line, as it sits beside the fonts. |
| 43 | `Credits.FONT_NOTICE` | `"The type is IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., " +...` | The line above the fonts' licence: what the type is, whose, and the licence it is used under. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 78 | **type** `public final class Credits` | The Credits page's sections: the Earth data's attribution, the nations' attribution and the fonts' notice with its licence, each read from the jar as the exe ships it, and the text of each cut into paragraphs for the ... |
| 25 | 1 | `private Credits()` |  |
| 28 | 4 | **type** `public record Section(String title, String resource, String besideExe, String lead, String text)` | One block of the page: its heading, where its text is, the file that ships beside the exe, the line above it, and the text itself. |
| 30 | 1 | `public boolean missing()` _(in Credits.Section)_ | True when this build carries no copy of the text; the page then says where the file is. |
| 47 | 3 | `public static List<Section> sections()` | The page's sections, in order, read from the jar. |
| 52 | 7 | `static List<Section> sections(Function<String, String> reader)` | The same through any reader of a classpath path to its text (null for a file this build lacks); a harness's fixture. |
| 61 | 10 | `static String readResource(String path)` | A classpath file as UTF-8 text, unchanged but for a leading byte-order mark; null when it is not there or cannot be read. |
| 77 | 16 | `public static List<String> paragraphs(String text)` | The text cut on its blank lines (empty or whitespace), each paragraph keeping its own line breaks, with \r removed and no blank paragraph at either end; empty for null. |
| 95 | 5 | `public static String missingNote(Section s)` | What the page says for a section this build lacks: where the file is. |

