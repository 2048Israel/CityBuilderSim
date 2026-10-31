# SavedCities.java - 239 lines · 18 methods · 3 constants · model

`ham/citybuildersim/SavedCities.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read only,
> the autosave (slot 0) left out, each as a Rankings.City at its last save (now()) or at the active city's age (at(month)), the ones that never got that
> far (stoppedAt(month)), and how many were left off the board and why (left()). A pure model in the *View manner: no JavaFX, no Game, and it never
> writes a file. A snapshot is immutable, so the screen reads one on a worker thread (refresh) and hands it to the clock's thread.
> 
> WHY. Each save is its own world: its own money (its last reform may have lopped zeros off it), its own rate, parity and price level, its own history.
> The board compares them only through the pivot, so each city is handed over in the units of its LAST save, history and header rescaled together, and
> the pivot divides the unit out (Rankings.savedCell): a reform never moves a rank. A history file is up to 26 MB on the PC, nine of them are 200 MB of
> numbers, and the board needs a few dozen of the series: so a slot keeps a slim history (HistorySave.slim, CityMeasures.SERIES and the three that make
> the pivot), never the file, and a snapshot is keyed per slot by both files' size and modified time, so that a refresh reads again only the slot that was
> saved since. The header supplies what the history does not keep (the money's name, the founding rate, the land and the people by age band) and is
> decoded by the classes that own each field (WorldEconomy, Denomination, PopulationCohorts, LandManager), never by array index.
> 
> AT AN AGE. A city at month m has its figures from CityMeasures.at with Extras.UNKNOWN: the history keeps neither the land nor the bands, so area, density
> and the measures over the bands are absent at an earlier month (never wrong). Its rate, parity and price level are the series at m; where a history
> did not record the price level (usPrices, from LB3) the money figures are absent for that city rather than guessed.

**Uses:** [Rankings](Rankings.md) (12), [HistorySave](HistorySave.md) (6), [GameFiles](GameFiles.md) (6), [CityMeasures](CityMeasures.md) (4), [AgeBand](AgeBand.md) (3), [SaveHeader](SaveHeader.md) (2), [Denomination](Denomination.md) (2), [WorldEconomy](WorldEconomy.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [GameLog](GameLog.md) (1), [Currency](Currency.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [LandManager](LandManager.md) (1)

**Used by (2):** [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md)

## Sections

| line | section |
|---:|---|
| 81 | · reading |
| 187 | · answering |

## Enum constants

| line | constant | says |
|---:|---|---|
| 59 | `SavedCities.Why.OLDER_BUILD` |  |
| 59 | `SavedCities.Why.NEWER_BUILD` |  |
| 59 | `SavedCities.Why.DAMAGED` |  |
| 59 | `SavedCities.Why.NO_HISTORY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `SavedCities.FIRST_SLOT` | `1` | The first slot read: slot 0 is the autosave, a copy of a numbered city, and is left out. |
| 37 | `SavedCities.KEPT` | `keptSeries()` | The history series a slot keeps: what the measures read, and the three that make the pivot (fxRate and fxParity are among the first). |
| 71 | `SavedCities.NONE` | `new SavedCities(List.of(), 0)` | The snapshot before any slot was read: no cities, nothing left out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `private final List<Entry> entries` |  |
| 74 | `private final int reads` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 209 | **type** `public final class SavedCities` | The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read only, the autosave (slot 0) left out, each as a Rankings.City at its la... |
| 39 | 7 | `private static Set<String> keptSeries()` |  |
| 48 | 1 | **type** `public record Stopped(int slot, String name, int lastMonth)` | A saved city that never got as far as an age: its slot, its name and the last month of its history ("stopped at Feb 2031": CityCalendar.formatShort). |
| 51 | 6 | **type** `public record LeftOut(int olderBuild, int newerBuild, int damaged, int noHistory)` | The saved files left off the board and why, counted: a save from an older build, from a newer one, one that cannot be read, and one with no history. |
| 53 | 3 | `public int total()` _(in SavedCities.LeftOut)_ | All of them. |
| 59 | 1 | **type** `enum Why` | Why a slot is not a city on the board; null for an empty slot and for a city. |
| 62 | 1 | **type** `record Stamp(Path save, long saveSize, long saveTime, long historySize, long historyTime)` | Both files of a slot as the disk had them: the path and each file's size and modified time (-1 for a file that is not there). |
| 65 | 1 | **type** `private record Holding(String name, String currency, double foundingRate, HistorySave history, int lastMont...` | What a readable slot keeps: the city as its last save has it, the slim history behind it, and what an earlier month needs of the header. |
| 68 | 1 | **type** `private record Entry(int slot, Stamp stamp, Holding kept, Why left)` | One slot as read: its stamp, and either the city it keeps, or why it is left off, or neither (an empty slot). |
| 76 | 4 | `private SavedCities(List<Entry> entries, int reads)` |  |

### reading (lines 81-186)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 3 | `public static SavedCities read(GameFiles files)` | Every slot of the files read afresh: NONE.refresh(files). |
| 92 | 15 | `public SavedCities refresh(GameFiles files)` | A new snapshot of the files, reusing every slot of this one whose save and history files have the size and modified time they had (and the path), so that only the slot saved since is read again. |
| 109 | 3 | `public int reads()` | How many slots the refresh that made this snapshot parsed from disk (the rest were reused or empty): 0 when nothing was saved since the last. |
| 113 | 3 | `private Entry entryOf(int slot)` |  |
| 117 | 4 | `private static Stamp stampOf(GameFiles files, int slot)` |  |
| 122 | 7 | `private static long sizeOf(Path file)` |  |
| 130 | 7 | `private static long timeOf(Path file)` |  |
| 139 | 33 | `private static Entry readSlot(GameFiles files, int slot, Stamp stamp)` | One slot: the header through GameFiles.readHeader, then the history file as Game.loadHistory reads it (Gson, through a Reader), cut to the kept series. |
| 173 | 3 | `private static Entry left(int slot, Stamp stamp, Why why)` |  |
| 178 | 8 | `private static CityMeasures.Extras extrasOf(SaveHeader header)` | What the history does not keep, from the header: the people in each age band (PopulationCohorts.restore by name) and the city's dry ground in square kilometres (LandManager.km2). |

### answering (lines 187-239)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 5 | `public List<Rankings.Saved> now()` | The saved cities at their last saved month, in slot order: their figures from the whole history and the header's land and bands, in the units of their last save. |
| 201 | 10 | `public List<Rankings.Saved> at(int month)` | The saved cities at game month `month` (D3, the active city's age), in slot order: each read from its history at that month, its rate, parity and price level the series' there, its figures CityMeasures.at with Extras.... |
| 213 | 5 | `public List<Stopped> stoppedAt(int month)` | The saved cities whose history ends before game month `month`: "stopped at Feb 2031" (CityCalendar.formatShort(lastMonth)), no figure and no row. |
| 220 | 13 | `public LeftOut left()` | The saves left off the board and why, counted; an empty slot is no save and is not counted. |
| 235 | 4 | `HistorySave history(int slot)` | The slim history a slot keeps, or null when the slot is not a city on the board: for a harness that holds it to the file's. |

