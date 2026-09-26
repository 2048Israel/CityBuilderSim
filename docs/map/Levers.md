# Levers.java - 67 lines · 8 methods · 0 constants · interface

`ham/citybuildersim/ui/Levers.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The pieces a policy lever is drawn with: its head, the would-be rows that
> show a staged change against today's figure, and the arithmetic of snapping
> a slider to its step. The dial itself is Ladder (0.7.6), which knows
> nothing of the staged set; the wiring to it and the apply bar stay with
> the policy screen, because they read and write that set.

**Uses:** [Palette](Palette.md) (6)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 50 | **type** `public final class Levers` | The pieces a policy lever is drawn with: its head, the would-be rows that show a staged change against today's figure, and the arithmetic of snapping a slider to its step. |
| 21 | 15 | `public static VBox leverHead(String figure, String what)` | The current setting, big, with the sentence that says what it governs. |
| 37 | 3 | `public static double snapped(double value, double min, double step)` |  |
| 41 | 3 | `public static boolean moved(double value, double from, double step)` |  |
| 45 | 1 | `public static VBox wouldHead()` |  |
| 48 | 3 | `public static String arrow(boolean staged, String now, String then)` | "now  \u2192  then", or just now when nothing staged reaches this row. |
| 53 | 3 | `public static HBox wouldBe(String label, String before, String after, String tone)` | One line of a preview: what it reads now, and what it would read. |
| 57 | 3 | `public static VBox wouldTotal(String label, String before, String after, String tone)` |  |
| 62 | 5 | `public static Label previewCaveat(String basis)` | The sentence every preview on this tab ends with, because it is always true. |

