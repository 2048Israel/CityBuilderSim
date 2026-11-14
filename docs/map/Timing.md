# Timing.java - 208 lines · 16 methods · 8 constants · model

`ham/citybuildersim/Timing.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The suite's clock verdicts and the soft skip for work whose only verdict is time.
> 
> WHY (TM1, HA1): concurrent runs measure the PC's load as well as the code;
> the old gate spent 38 minutes doing clock-only rounds it did not judge.
> Soft runs skip those sections before their rounds. Mixed correctness
> sections still run; only a missed clock becomes SOFT and uncounted.
> Hard runs retain all rounds and budgets; nightly timing judges regressions.
> 
> A timing condition uses {@code Timing.within(figure, budget)} and prints
> through {@code Timing.verdict(ok, label)}. A non-clock failure stays FAIL,
> including beside a soft miss; NaN stays a failure. TimingCheck holds this
> contract and the source rules. {@link #TSV_PROPERTY} records each labelled
> figure, budget and unit for the nightly's paired comparisons (GR1).

**Used by (24):** [AllChecks](AllChecks.md), [BridgeCheck](BridgeCheck.md), [BridgeMapCheck](BridgeMapCheck.md), [EarthCheck](EarthCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [GridCheck](GridCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [IslandNetCheck](IslandNetCheck.md), [KeptCheck](KeptCheck.md), [LandNationsCheck](LandNationsCheck.md), [MapCheck](MapCheck.md), [Observation](Observation.md), [PartnerCallsCheck](PartnerCallsCheck.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [QuayCraneCheck](QuayCraneCheck.md), [ReplayCheck](ReplayCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [TimingCheck](TimingCheck.md), [UiProbe](UiProbe.md), [WorldCheck](WorldCheck.md), [WorldMarketCheck](WorldMarketCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `Timing.PROPERTY` | `"cbs.timing"` | The system property that makes timing soft: {@code -Dcbs.timing=soft}; anything else, or none, is hard. |
| 40 | `Timing.SOFT` | `"soft"` | The property's value that makes timing soft. |
| 43 | `Timing.HARD` | `"hard"` | The property's value the gate passes to say hard out loud (any value but SOFT is hard). |
| 46 | `Timing.HARNESSES` | `List.of("WorldCheck", "MapCheck", "ScaleCheck", "PlanCheck", "EarthCheck", "G...` | The harnesses that hold a timing assertion: what {@code AllChecks --timing} runs, and the ship gate (TimingCheck holds it to the sources). |
| 52 | `Timing.SOFT_LINE` | `Pattern.compile("\\sSOFT - ")` | A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. |
| 64 | `Timing.TSV_PROPERTY` | `"cbs.timing.tsv"` | The system property that names the file every timing assertion appends its figure to, {@code -Dcbs.timing.tsv=target/checks/timing.tsv} (TM3); none, and nothing is written. |
| 67 | `Timing.misses` | `new ArrayList<>()` | The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. |
| 73 | `Timing.readings` | `new ArrayList<>()` | The figures judged since the last verdict. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 32 | 177 | **type** `public final class Timing` | The suite's clock verdicts and the soft skip for work whose only verdict is time. |
| 34 | 1 | `private Timing()` |  |
| 70 | 1 | **type** `private record Reading(double figure, double budget, String unit, boolean held)` | One figure judged since the last verdict, kept for the file's row (only while the file is named). |
| 76 | 3 | `static boolean soft()` | Whether timing is soft this run: the property read, every time, here and nowhere else. |
| 81 | 5 | `static boolean skip(String section)` | A section with only a clock's verdict does no rounds in a soft suite (HA1). |
| 88 | 3 | `public static boolean within(double figure, double budget)` | A figure in milliseconds within its budget (at most it): {@code figure <= budget}, or soft. |
| 93 | 3 | `static boolean within(double figure, double budget, String unit)` | A figure within its budget (at most it), in the unit named: {@code figure <= budget}, or soft. |
| 98 | 3 | `static boolean below(double figure, double budget)` | A figure in milliseconds below its budget (strictly): {@code figure < budget}, or soft. |
| 108 | 7 | `private static boolean judge(boolean held, double figure, double budget, String unit)` | The timing half of an assertion: true when it held; when it missed, true all the same if timing is soft, the miss kept for the next verdict. |
| 117 | 6 | `private static String figures(double v)` | A figure as the verdict prints it: a whole number bare (a budget of 80 is "80"), else a tenth from 1 up, whole from 100, and three significant places below 1 ("0.3", "0.000123"), never an exponent. |
| 131 | 6 | `public static String verdict(boolean ok)` | What an assertion's line ends with: FAIL when it failed, SOFT with each timing miss's figure and budget when it held only because timing is soft, OK otherwise. |
| 145 | 4 | `public static String verdict(boolean ok, String label)` | {@link #verdict(boolean)}, and when {@link #TSV_PROPERTY} names a file the figures judged since the last verdict written there under this label, the harness's own (the class that called it). |
| 151 | 4 | `private static String tsv()` | The file the figures go to, or null when none is named. |
| 157 | 22 | `private static void record(String label)` | Appends the pending figures as rows (TSV_PROPERTY says the columns), under an exclusive lock, so harnesses running at once (AllChecks.PARALLEL, SP1) never interleave their rows. |
| 181 | 9 | `static synchronized void append(Path path, String rows)` | Locked UTF-8 rows shared by timing and path observations (PO1). |
| 192 | 4 | `private static String plain(double v)` | A figure for the file: six significant places, never an exponent; NaN and the infinities as Java words them. |
| 198 | 4 | `static String forwarded()` | The argument a child JVM is given so it times as this one does: {@code -Dcbs.timing=...}, or null when the property is not set here. |
| 204 | 4 | `static String forwardedTsv()` | The argument a child JVM is given so it writes its figures to the same file: {@code -Dcbs.timing.tsv=...}, or null when none is named here. |

