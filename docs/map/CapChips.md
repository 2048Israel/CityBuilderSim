# CapChips.java - 190 lines · 8 methods · 0 constants · interface

`ham/citybuildersim/ui/CapChips.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The highest yield a sale will take, as three chips and a yield box: fair plus
> a point (the default), a yield the player names, or Fill it (CD2, D28).
> 
> WHY (CD2, slice 9): Borrow had these chips as a block inside FinancesScreen,
> bound to the one field the page keeps. The rollover's card, automatic
> building's panel and the build screen's funding card each carry the same
> three chips for a cap of their own (Jerus, 2026-10-12: "each its own"), so
> the block became a piece: it reads and sets the cap through two lambdas and
> keeps its own typing, so each screen binds it to whichever cap it owns and
> the four look and behave alike. Nothing here is the model's: the cap is
> Auction.Cap and the words for it are CapSetting's and the card's.
> 
> One instance for each cap on a screen. The yield box is the house's search
> box, kept through a month's redraw: a screen calls noteFocus() before it
> rebuilds, so a box that had the focus takes it back.

**Uses:** [Auction](Auction.md) (19), [FinancesScreen](FinancesScreen.md) (6), [Palette](Palette.md) (6)

**Used by (2):** [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final Supplier<Auction.Cap> cap` |  |
| 35 | `private final Consumer<Auction.Cap> setCap` |  |
| 36 | `private final Runnable redraw` |  |
| 37 | `private final Runnable redrawSoon` |  |
| 40 | `private String typed` | What is typed in the yield box, in per cent, and not yet set; null while the box shows the cap as it stands. |
| 42 | `private String refused` | What the last yield that could not be read says, under the chips until the cap is next set; null for none. |
| 44 | `private TextField field` | The yield box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing. |
| 45 | `private boolean typing` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 32 | 159 | **type** `final class CapChips` | The highest yield a sale will take, as three chips and a yield box: fair plus a point (the default), a yield the player names, or Fill it (CD2, D28). |
| 53 | 6 | `CapChips(Supplier<Auction.Cap> cap, Consumer<Auction.Cap> setCap, Runnable redraw, Runnable redrawSoon)` |  |
| 61 | 3 | `void noteFocus()` | Called as a screen starts to rebuild: whether the yield box has the focus now, so the new box takes it back. |
| 66 | 8 | `String[] names()` | The chips' names, in order: the default tail, a named yield, Fill it. |
| 82 | 52 | `List<Node> controls(String title, String info, double fair, boolean exact)` | The block: a caption with its (i), the three chips, the box for a yield in per cent, and what could not be read. |
| 136 | 3 | `static String yieldText(Auction.Cap cap)` | A named yield as the box shows it, in per cent to three places: 0.04602 is "4.602". |
| 141 | 11 | `static double fromWords(String typed)` | A typed yield, in per cent ("4.5" or "4.5%"), as the yield it names; NaN when it is not one between nothing and a hundred per cent. |
| 154 | 3 | `static String refusal(String typed)` | What the box says under the chips when an entry cannot be read. |
| 165 | 25 | `private void commit(TextField box, boolean enter)` | What is in the yield box, set as the cap - or, when it cannot be read, said under the chips with the cap left as it was; an empty box, or the cap's own figure left in it, changes nothing. |

