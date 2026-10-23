# Timing.java - 119 lines · 9 methods · 6 constants · model

`ham/citybuildersim/Timing.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The one helper every timing assertion in the suite goes through: a figure
> on a clock against its budget, hard as the harness wrote it, or soft while
> another batch's checks or Jerus's game share the machine.
> 
> <p>WHY (batch TM1, 2026-10-18): batches on the PC took turns under "one JVM
> at a time on the machine", and waited on each other's suites and on
> Jerus's game, though 14 cores and 32 GB fit two suites easily. What
> clashed was only the timing assertions: EarthTerrainCheck 8 measured 116 ms
> against its 80 ms with the game running, 27.8 ms without it. So under
> {@code -Dcbs.timing=soft} a timing figure that misses its budget is not a
> failure: the assertion's line says SOFT, with the figure and the budget,
> and the harness does not count it. Without the property every assertion is
> exactly as before, and no budget moved. The ship gate runs the timing
> harnesses alone and hard ({@code AllChecks --timing}), so a budget is still
> held where it means something.
> 
> <p>How a harness uses it: the timing part of a condition is
> {@code Timing.within(figure, budget)} in place of {@code figure <= budget},
> and the harness's own line prints {@code Timing.verdict(ok)} in place of
> {@code ok ? "OK" : "FAIL"}. A soft miss answers true and waits for the next
> verdict, which then reads "SOFT - 116.2 ms, budget 80 ms". A condition whose
> other part fails is FAIL as ever: only a clock is ever soft. TimingCheck
> holds the mechanism, and holds that no harness compares a figure against a
> {@code *_MS} or {@code *_NS} dial except through here.

**Used by (14):** [AllChecks](AllChecks.md), [EarthCheck](EarthCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [GridCheck](GridCheck.md), [IslandNetCheck](IslandNetCheck.md), [KeptCheck](KeptCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [TimingCheck](TimingCheck.md), [UiProbe](UiProbe.md), [WorldCheck](WorldCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `Timing.PROPERTY` | `"cbs.timing"` | The system property that makes timing soft: {@code -Dcbs.timing=soft}; anything else, or none, is hard. |
| 44 | `Timing.SOFT` | `"soft"` | The property's value that makes timing soft. |
| 47 | `Timing.HARD` | `"hard"` | The property's value the gate passes to say hard out loud (any value but SOFT is hard). |
| 50 | `Timing.HARNESSES` | `List.of("WorldCheck", "MapCheck", "ScaleCheck", "PlanCheck", "EarthCheck", "G...` | The harnesses that hold a timing assertion: what {@code AllChecks --timing} runs, and the ship gate (TimingCheck holds it to the sources). |
| 55 | `Timing.SOFT_LINE` | `Pattern.compile("\\sSOFT - ")` | A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. |
| 58 | `Timing.misses` | `new ArrayList<>()` | The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 84 | **type** `public final class Timing` | The one helper every timing assertion in the suite goes through: a figure on a clock against its budget, hard as the harness wrote it, or soft while another batch's checks or Jerus's game share the machine. |
| 38 | 1 | `private Timing()` |  |
| 61 | 3 | `static boolean soft()` | Whether timing is soft this run: the property read, every time, here and nowhere else. |
| 66 | 3 | `public static boolean within(double figure, double budget)` | A figure in milliseconds within its budget (at most it): {@code figure <= budget}, or soft. |
| 71 | 3 | `static boolean within(double figure, double budget, String unit)` | A figure within its budget (at most it), in the unit named: {@code figure <= budget}, or soft. |
| 76 | 3 | `static boolean below(double figure, double budget)` | A figure in milliseconds below its budget (strictly): {@code figure < budget}, or soft. |
| 86 | 6 | `private static boolean judge(boolean held, double figure, double budget, String unit)` | The timing half of an assertion: true when it held; when it missed, true all the same if timing is soft, the miss kept for the next verdict. |
| 94 | 6 | `private static String figures(double v)` | A figure as the verdict prints it: a whole number bare (a budget of 80 is "80"), else a tenth from 1 up, whole from 100, and three significant places below 1 ("0.3", "0.000123"), never an exponent. |
| 108 | 5 | `public static String verdict(boolean ok)` | What an assertion's line ends with: FAIL when it failed, SOFT with each timing miss's figure and budget when it held only because timing is soft, OK otherwise. |
| 115 | 4 | `static String forwarded()` | The argument a child JVM is given so it times as this one does: {@code -Dcbs.timing=...}, or null when the property is not set here. |

