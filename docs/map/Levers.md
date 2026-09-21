# Levers.java - 66 lines · 8 methods · 0 constants · interface

`ham/citybuildersim/ui/Levers.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The pieces a policy lever is drawn with: its head, the would-be rows that
> show a staged change against today's figure, and the arithmetic of snapping
> a slider to its step. The slider itself and the apply bar stay with the
> policy screen, because they read and write the staged set.

**Uses:** [Palette](Palette.md) (6)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 50 | **type** `public final class Levers` | The pieces a policy lever is drawn with: its head, the would-be rows that show a staged change against today's figure, and the arithmetic of snapping a slider to its step. |
| 20 | 15 | `public static VBox leverHead(String figure, String what)` | The current setting, big, with the sentence that says what it governs. |
| 36 | 3 | `public static double snapped(double value, double min, double step)` |  |
| 40 | 3 | `public static boolean moved(double value, double from, double step)` |  |
| 44 | 1 | `public static VBox wouldHead()` |  |
| 47 | 3 | `public static String arrow(boolean staged, String now, String then)` | "now  \u2192  then", or just now when nothing staged reaches this row. |
| 52 | 3 | `public static HBox wouldBe(String label, String before, String after, String tone)` | One line of a preview: what it reads now, and what it would read. |
| 56 | 3 | `public static VBox wouldTotal(String label, String before, String after, String tone)` |  |
| 61 | 5 | `public static Label previewCaveat(String basis)` | The sentence every preview on this tab ends with, because it is always true. |

