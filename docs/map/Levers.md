# Levers.java - 79 lines · 9 methods · 0 constants · interface

`ham/citybuildersim/ui/Levers.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The pieces a policy lever is drawn with: its head, the would-be rows that
> show a staged change against today's figure, and the arithmetic of snapping
> a slider to its step. The dial itself is Ladder (0.7.6), which knows
> nothing of the staged set; the wiring to it and the apply bar stay with
> the policy screen, because they read and write that set.

**Uses:** [Palette](Palette.md) (11)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 62 | **type** `public final class Levers` | The pieces a policy lever is drawn with: its head, the would-be rows that show a staged change against today's figure, and the arithmetic of snapping a slider to its step. |
| 21 | 15 | `public static VBox leverHead(String figure, String what)` | The current setting, big, with the sentence that says what it governs. |
| 38 | 10 | `public static VBox leverHead(String figure, String shown, String whole)` | The same, in two layers (0.7.21): one short line under the figure, and the whole of it behind an (i). |
| 49 | 3 | `public static double snapped(double value, double min, double step)` |  |
| 53 | 3 | `public static boolean moved(double value, double from, double step)` |  |
| 57 | 1 | `public static VBox wouldHead()` |  |
| 60 | 3 | `public static String arrow(boolean staged, String now, String then)` | "now  \u2192  then", or just now when nothing staged reaches this row. |
| 65 | 3 | `public static HBox wouldBe(String label, String before, String after, String tone)` | One line of a preview: what it reads now, and what it would read. |
| 69 | 3 | `public static VBox wouldTotal(String label, String before, String after, String tone)` |  |
| 74 | 5 | `public static Label previewCaveat(String basis)` | The sentence every preview on this tab ends with, because it is always true. |

