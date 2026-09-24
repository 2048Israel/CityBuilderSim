# CalendarCheck.java - 268 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/CalendarCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The date on the status bar, and the log of what the city has finished.
> 
> Both are new and both are the kind of thing that looks obviously right and is
> quietly off by one. The calendar in particular has two independent chances to
> be wrong - the epoch and the modulo - and they cancel out at exactly the point
> anyone would eyeball it (month 1), so it is checked at the boundaries of every
> year it touches rather than at a couple of convenient months.

**Uses:** [CityCalendar](CityCalendar.md) (41), [BuildLog](BuildLog.md) (10), [GameFiles](GameFiles.md) (2), [Game](Game.md) (2), [DemolitionLog](DemolitionLog.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 42 | · 1. the epoch |
| 64 | · 2. every boundary in the first year |
| 82 | · 3. the far end and the bad end |
| 89 | · days, which only the clock uses |
| 142 | · 4. "in N months" |
| 152 | · 5. the build log |
| 202 | · 6. through a real month |
| 229 | · 7. one press is one month (0.7.1) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 252 | **type** `public class CalendarCheck` | The date on the status bar, and the log of what the city has finished. |
| 21 | 6 | `static void check(String label, long actual, long expected)` |  |
| 28 | 6 | `static void same(String label, String actual, String expected)` |  |
| 35 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 40 | 220 | `public static void main(String[] args) throws Exception` |  |
| 261 | 7 | `static void cleanUp(Path root)` |  |

