# CalendarCheck.java - 313 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/CalendarCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The date on the status bar, and the log of what the city has finished.
> 
> FS17: section 6 CAUSES its premise - it plays until the six houses it orders are in the log, inside the months the model's own queue gives
> them (the points owed on site over the capacity a month), where it used to play a fixed 24 months and read the log.
> 
> Both are new and both are the kind of thing that looks obviously right and is
> quietly off by one. The calendar in particular has two independent chances to
> be wrong - the epoch and the modulo - and they cancel out at exactly the point
> anyone would eyeball it (month 1), so it is checked at the boundaries of every
> year it touches rather than at a couple of convenient months.

**Uses:** [CityCalendar](CityCalendar.md) (47), [BuildLog](BuildLog.md) (10), [Game](Game.md) (3), [GameFiles](GameFiles.md) (2), [TempDirs](TempDirs.md) (1), [DemolitionLog](DemolitionLog.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 50 | · 1. the epoch |
| 74 | · 2. every boundary in the first year |
| 94 | · 3. the far end and the bad end |
| 101 | · days, which only the clock uses |
| 159 | · 4. "in N months" |
| 169 | · 5. the build log |
| 219 | · 6. through a real month |
| 265 | · 7. one press is one month (0.7.1) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 294 | **type** `public class CalendarCheck` | The date on the status bar, and the log of what the city has finished. |
| 24 | 6 | `static void check(String label, long actual, long expected)` |  |
| 31 | 6 | `static void same(String label, String actual, String expected)` |  |
| 38 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 44 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month: the epoch's January is month 1. |
| 48 | 248 | `public static void main(String[] args) throws Exception` |  |
| 298 | 7 | `static long housesLogged(Game g)` | FS17: the houses the city's own log holds, every month it kept. |
| 306 | 7 | `static void cleanUp(Path root)` |  |

