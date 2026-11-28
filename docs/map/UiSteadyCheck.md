# UiSteadyCheck.java - 181 lines · 5 methods · 2 constants · harnesses

`ham/citybuildersim/UiSteadyCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The window holds still: a redraw with nothing changed changes nothing on screen, and a month keeps the player's place.
> 
> WHY (UX1, 2026-11-27). Jerus, on the interface rework: "the ui work watches out for scrolling issues, and issues
> were the screen flickers or expands without stopping". KeptCheck holds each kept page against the same page built
> new; this holds the whole window - the header, the rail, the page and its panels, as the player has them - on every
> page and sub-page the picture tool walks (ui.PagePictures), and it is the guard of every UI batch after UX1. Its
> reds on the pages as UX1 found them are the audit's stability faults, each a known red owned by the UX row that
> mends that page (docs/queue/UX1-notes.md).
> 
> WHAT THIS HAS TO PROVE, on the playtest's young town in the real window at 1920x1080:
> 
>   0. THE MEASURE SEES A FAULT. On a scene of its own, a still redraw shows nothing, and each fault caused once - a
>      node put in another's place, a box made taller, the scroller moved - is seen. Without this a green below
>      could be a measure that sees nothing.
> 
>   1. AN UNCHANGED REDRAW MOVES NOTHING. Every page drawn again with no month between keeps every node where it
>      was, to half a pixel, and every scroller where it stood. A node that moves when nothing changed is the
>      flicker the player sees.
> 
>   2. AN UNCHANGED REDRAW MAKES NOTHING NEW. The same node objects are on the scene after as before: none made,
>      none let go. A node rebuilt is a node styled and painted from nothing, which is a flicker even where it
>      lands in the same place (Kept's rules).
> 
>   3. NOTHING GROWS. Three unchanged redraws in a row leave the page's own content the same size, and the
>      window's root is never wider or taller than the window: "expands without stopping".
> 
>   4. A MONTH KEEPS THE PLACE. The page's scroller set a third of the way down, a month played and the page
>      redrawn as the clock does, the scroller is at the same pixel (Kept: "the scroll never moves").
> 
> Every page is named on its line, so a red is one page's and a known red is scoped to it.

**Uses:** [SteadyBench](SteadyBench.md) (21), [GameFiles](GameFiles.md) (2), [GamePrefs](GamePrefs.md) (2), [Game](Game.md) (1), [FollowBench](FollowBench.md) (1), [LongPlaytest](LongPlaytest.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `UiSteadyCheck.PLACE` | `1.0 / 3` | The fraction of its run a page's scroller is set to before the month (section 4). |
| 61 | `UiSteadyCheck.REDRAWS` | `3` | Unchanged redraws in a row for section 3. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 45 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 140 | **type** `public class UiSteadyCheck` | The window holds still: a redraw with nothing changed changes nothing on screen, and a month keeps the player's place. |
| 47 | 4 | `static void check(String label, boolean ok)` |  |
| 52 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 63 | 15 | `public static void main(String[] args) throws Exception` |  |
| 79 | 96 | `static void run() throws Exception` |  |
| 177 | 4 | `static String line(String label, boolean ok, String detail)` | One assertion's line, counted, for a section printed after the walk. |

