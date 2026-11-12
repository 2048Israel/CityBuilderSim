# MoneyWordsCheck.java - 344 lines · 17 methods · 5 constants · harnesses

`ham/citybuildersim/MoneyWordsCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The words of every page, read off the nodes, in a city whose money is written after the figure (FM1). Not part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-11-02: the "$" was a literal in a hundred places, so a money with another sign ("2.6M kr") was
> right on the screens that asked the money for its sign and wrong on every one that typed the character. MoneySignCheck holds
> the writer; this holds the screens. It draws every page ui/KeptBench can draw - the rail's tabs and their pages, People, City
> History, the header, the rail and the three panels - for a city founded with the Krona (KRO, "kr", after the figure), reads every
> word off every node (a label's text and its graphic's, a text, a field's text and prompt, a tooltip, a tab, a choice) and says
> which of them still hold a "$" that is not the mark of a foreign money. What a canvas draws is not a node and is not read.
> 
> What it proves, a section each; every one a fixture that causes the condition:
> 
>   1. The fixture: the same pages of a "$" city show "$", and the Krona's show " kr" - a page that draws nothing would pass
>      section 2 without having been looked at.
>   2. On the Krona's city no string holds a "$" except inside a foreign mark (US$, C$, A$: ForeignAccounts.MONEY_MARK, and
>      Currency.FOREIGN_SYMBOL). Each string is printed once, with the page it was first read on, as one FAIL line.
> 
> The city is the playtest's village founded by hand with the money (LongPlaytest.founding() and its first builds) and played
> MONTHS months, no more: the pages need figures, not a history.

**Uses:** [KeptBench](KeptBench.md) (15), [LongPlaytest](LongPlaytest.md) (13), [Currency](Currency.md) (9), [Game](Game.md) (7), [Founding](Founding.md) (1), [GameFiles](GameFiles.md) (1), [Sectors](Sectors.md) (1), [Sector](Sector.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 130 | · reading the words |
| 311 | · · 1 |
| 324 | · · 2 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `MoneyWordsCheck.MONTHS` | `24` | The months the two cities are played to before their pages are drawn: enough for a bank, a ledger and a trade, and for the pages to hold figures. |
| 70 | `MoneyWordsCheck.AT_LEAST` | `500` | The fewest distinct strings holding a city's own sign that its pages must show for the fixture to count as drawn: a page that drew nothing would pass section 2 unread. |
| 73 | `MoneyWordsCheck.PRINTED` | `60` | The most offending strings section 2 prints, each as a FAIL line; the rest are only counted. |
| 76 | `MoneyWordsCheck.AROUND` | `70` | How much of an offending string is printed round the "$": a window, so a long paragraph still says where. |
| 78 | `MoneyWordsCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private static int fails, checks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 291 | **type** `public class MoneyWordsCheck` | The words of every page, read off the nodes, in a city whose money is written after the figure (FM1). |
| 58 | 1 | `static void check(String label, boolean ok)` |  |
| 60 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 80 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 87 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 102 | 1 | `static Currency krona()` | The Krona: the money of section 2's city, written "2.6M kr". |
| 105 | 24 | `static Game city(String label, Currency money)` | The playtest's village founded with `money` (the founding's own when null), its first builds and months played, the game's narration off. |

### reading the words (lines 130-344)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 29 | `static void words(Node n, Set<String> out, Map<Node, Boolean> seen)` | Every word on a node and under it: a label's text and graphic, a text, a field's text and prompt, a tooltip, a tab, a choice. |
| 163 | 3 | `private static void add(Set<String> out, String s)` |  |
| 168 | 1 | **type** `record Named(String name, Callable<List<Node>> draw)` | A page: the name the lines call it, and how it is drawn - the nodes it left on the bench. |
| 171 | 8 | `static Named page(KeptBench bench, String name, KeptBench.Page p, Runnable after)` | One of KeptBench's pages, on the page it opens on and drawn from nothing kept: what `after` sets, then the nodes. |
| 181 | 45 | `static List<Named> pages(Game g, KeptBench bench)` | Every page KeptBench can draw, for the city it is built on. |
| 228 | 21 | `static Map<String, Set<String>> read(Game g, String label) throws Exception` | What a city's pages say, page by page: each page once, the strings in the order they were read; a page that cannot be drawn is told on its own line. |
| 251 | 5 | `static Set<String> distinct(Map<String, Set<String>> said)` | The distinct strings of every page. |
| 258 | 6 | `static List<String> foreignMarks()` | The foreign moneys' marks that hold a "$": the one a vault's figures are written with, and ForeignAccounts' others. |
| 266 | 11 | `static int stray(String s, List<String> marks)` | Where in the string a "$" is that is not inside a foreign mark, or -1. |
| 279 | 4 | `static String showing(String s, int at)` | A string for a line: where the stray sign is, a window of it, its newlines shown. |
| 284 | 16 | `public static void main(String[] args) throws Exception` |  |
| 301 | 43 | `static void run() throws Exception` |  |

