# ReplayGraphs.java - 92 lines · 6 methods · 1 constants · model

`ham/citybuildersim/ReplayGraphs.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The faint lines behind the city in a replay (TR1, 2026-10-31), without the toolkit: a recorded series read once from the
> recording's own history, each point a recorded month, and what a line is at the month on screen - how many of its points are
> drawn, how high each stands against the highest the recording ever reaches, and the figure at its head. ui/ReplayGraphs
> (ReplayMode's canvas) draws them; ReplayCheck 5 holds the arithmetic.
> 
> WHY IT EXISTS (TR1): the trailer's 0:54-1:35 shot is the city growing fast with its GDP and population rising faintly behind
> it. A line has to rise as time plays, so it is drawn from the recording's first month to the month on screen and scaled to the
> whole recording - the highest point of any recorded month, not of the months drawn so far, which would always touch the top.

**Uses:** [HistorySave](HistorySave.md) (1)

**Used by (2):** [ReplayCheck](ReplayCheck.md), [ReplayGraphLayer](ReplayGraphLayer.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 21 | `ReplayGraphs.YEAR` | `12` | The months a year of output is taken over: 12 - the header's own "a year", and it takes the month-to-month wobble out of a line that is meant to be soft. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 77 | **type** `public final class ReplayGraphs` | The faint lines behind the city in a replay (TR1, 2026-10-31), without the toolkit: a recorded series read once from the recording's own history, each point a recorded month, and what a line is at the month on screen ... |
| 18 | 1 | `private ReplayGraphs()` |  |
| 24 | 20 | **type** `public record Line(String key, int[] months, double[] values, double max)` | One series: {@code months} ascending and {@code values} beside them, both only for the months of the recording; {@code max} the highest value among them (0 for none). |
| 27 | 5 | `public int drawn(int month)` _(in ReplayGraphs.Line)_ | How many points are at or before {@code month}: the ones drawn when that month is on screen. |
| 34 | 4 | `public double head(int month)` _(in ReplayGraphs.Line)_ | The value at the last point drawn at {@code month}; 0 when none is. |
| 40 | 3 | `public double share(int i)` _(in ReplayGraphs.Line)_ | Point i's height against the recording's highest: 0 to 1 (0 for a line with no height at all). |
| 46 | 3 | `public static double across(int month, int first, int last)` | Where a month stands across the recording, 0 at its first and 1 at its last (0 for a recording of one month). |
| 56 | 36 | `public static Line of(HistorySave master, String key, int first, int last, boolean annual)` | The series {@code key} of the recording's history over its months {@code first} to {@code last}. |

