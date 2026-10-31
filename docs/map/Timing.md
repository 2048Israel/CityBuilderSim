# Timing.java - 215 lines · 14 methods · 8 constants · model

`ham/citybuildersim/Timing.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> 
> <p>WHY (batch TM3, 2026-10-27): a budget measured once on a PC that also runs
> Jerus's game and other sessions measures the load as much as the code. HY1,
> which touched nothing MapCheck times, stopped at the gate on three of its
> lines; main's own run of one of them had read 75.5 ms against 58 earlier the
> same day. So the gate no longer judges a timing red by one run: it times the
> red harness again with main's classes and the merge's in turn, in the same
> minutes, and the red counts against the batch only if the batch's median
> figure is more than a margin over main's (ship.sh and tools/timing-pair.awk,
> which hold the dial). To read the figures, never a label's prose, every
> assertion's line goes through {@link #verdict(boolean, String)}, which
> appends its figures to the file {@link #TSV_PROPERTY} names.

**Used by (17):** [AllChecks](AllChecks.md), [BridgeCheck](BridgeCheck.md), [BridgeMapCheck](BridgeMapCheck.md), [EarthCheck](EarthCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [GridCheck](GridCheck.md), [IslandNetCheck](IslandNetCheck.md), [KeptCheck](KeptCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [TimingCheck](TimingCheck.md), [UiProbe](UiProbe.md), [WorldCheck](WorldCheck.md), [WorldMarketCheck](WorldMarketCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `Timing.PROPERTY` | `"cbs.timing"` | The system property that makes timing soft: {@code -Dcbs.timing=soft}; anything else, or none, is hard. |
| 63 | `Timing.SOFT` | `"soft"` | The property's value that makes timing soft. |
| 66 | `Timing.HARD` | `"hard"` | The property's value the gate passes to say hard out loud (any value but SOFT is hard). |
| 69 | `Timing.HARNESSES` | `List.of("WorldCheck", "MapCheck", "ScaleCheck", "PlanCheck", "EarthCheck", "G...` | The harnesses that hold a timing assertion: what {@code AllChecks --timing} runs, and the ship gate (TimingCheck holds it to the sources). |
| 75 | `Timing.SOFT_LINE` | `Pattern.compile("\\sSOFT - ")` | A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. |
| 87 | `Timing.TSV_PROPERTY` | `"cbs.timing.tsv"` | The system property that names the file every timing assertion appends its figure to, {@code -Dcbs.timing.tsv=target/checks/timing.tsv} (TM3); none, and nothing is written. |
| 90 | `Timing.misses` | `new ArrayList<>()` | The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. |
| 96 | `Timing.readings` | `new ArrayList<>()` | The figures judged since the last verdict. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 55 | 161 | **type** `public final class Timing` | The one helper every timing assertion in the suite goes through: a figure on a clock against its budget, hard as the harness wrote it, or soft while another batch's checks or Jerus's game share the machine. |
| 57 | 1 | `private Timing()` |  |
| 93 | 1 | **type** `private record Reading(double figure, double budget, String unit, boolean held)` | One figure judged since the last verdict, kept for the file's row (only while the file is named). |
| 99 | 3 | `static boolean soft()` | Whether timing is soft this run: the property read, every time, here and nowhere else. |
| 104 | 3 | `public static boolean within(double figure, double budget)` | A figure in milliseconds within its budget (at most it): {@code figure <= budget}, or soft. |
| 109 | 3 | `static boolean within(double figure, double budget, String unit)` | A figure within its budget (at most it), in the unit named: {@code figure <= budget}, or soft. |
| 114 | 3 | `static boolean below(double figure, double budget)` | A figure in milliseconds below its budget (strictly): {@code figure < budget}, or soft. |
| 124 | 7 | `private static boolean judge(boolean held, double figure, double budget, String unit)` | The timing half of an assertion: true when it held; when it missed, true all the same if timing is soft, the miss kept for the next verdict. |
| 133 | 6 | `private static String figures(double v)` | A figure as the verdict prints it: a whole number bare (a budget of 80 is "80"), else a tenth from 1 up, whole from 100, and three significant places below 1 ("0.3", "0.000123"), never an exponent. |
| 147 | 6 | `public static String verdict(boolean ok)` | What an assertion's line ends with: FAIL when it failed, SOFT with each timing miss's figure and budget when it held only because timing is soft, OK otherwise. |
| 161 | 4 | `public static String verdict(boolean ok, String label)` | {@link #verdict(boolean)}, and when {@link #TSV_PROPERTY} names a file the figures judged since the last verdict written there under this label, the harness's own (the class that called it). |
| 167 | 4 | `private static String tsv()` | The file the figures go to, or null when none is named. |
| 173 | 24 | `private static void record(String label)` | Appends the pending figures as rows (TSV_PROPERTY says the columns), under an exclusive lock, so harnesses running at once (AllChecks.PARALLEL, SP1) never interleave their rows. |
| 199 | 4 | `private static String plain(double v)` | A figure for the file: six significant places, never an exponent; NaN and the infinities as Java words them. |
| 205 | 4 | `static String forwarded()` | The argument a child JVM is given so it times as this one does: {@code -Dcbs.timing=...}, or null when the property is not set here. |
| 211 | 4 | `static String forwardedTsv()` | The argument a child JVM is given so it writes its figures to the same file: {@code -Dcbs.timing.tsv=...}, or null when none is named here. |

