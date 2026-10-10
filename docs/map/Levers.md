# Levers.java - 145 lines · 3 methods · 1 constants · interface

`ham/citybuildersim/ui/Levers.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36)
> - the dial on a card with what it does beside it, which every dial on the
> Policy tab, since 0.7.38 the fare on Infrastructure and since 0.7.48 the
> fund's withdrawal on Finances are drawn with -
> and the arithmetic of snapping a slider to its step. The dial itself is
> Ladder (0.7.6), which knows nothing of the staged set; the wiring to it
> and the apply bar stay with the policy screen, because they read and
> write that set. The lever's head, the would-be rows and the preview's
> caveat sentence, which the cards replaced, went in 0.7.38 with their
> last caller.

**Uses:** [Palette](Palette.md) (17), [Ladder](Ladder.md) (1)

**Used by (3):** [FundScreen](FundScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 36 | THE DIAL CARD (0.7.36) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `Levers.EFFECTS` | `420` | How wide the effects column is held beside the ladder. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 26 | 120 | **type** `public final class Levers` | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on the Policy tab, since 0.7.38 the fare on Infrastructure and since 0.7.4... |
| 28 | 3 | `public static double snapped(double value, double min, double step)` |  |
| 32 | 3 | `public static boolean moved(double value, double from, double step)` |  |

### THE DIAL CARD (0.7.36) (lines 36-145)

| line | len | member | says |
|---:|---:|---|---|
| 61 | 4 | **type** `public record DialCard(String svg, String colour, String name, String info, String reading, String status, ...` | One dial card: its icon and the icon's colour; its name and the (i) behind it (null: none); the reading in large type and a line of status under it (either null: none); anything that sits over the ladder - its chips, ... |
| 75 | 70 | `public static VBox dialCard(DialCard d, double ladderWidth, boolean stacked)` | The card: the dial at the left `ladderWidth` wide and its effects at the right, or - `stacked`, in a narrow column - the effects under the dial. |

