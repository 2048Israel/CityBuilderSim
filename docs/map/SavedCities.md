# SavedCities.java - 304 lines · 22 methods · 4 constants · model

`ham/citybuildersim/SavedCities.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read only,
> the autosave (slot 0) left out, each as a Rankings.City at its last save (now(played)) or at the active city's age (at(month, played)), the ones that never
> got that far (stoppedAt(month, played)), and how many were left off the board and why (left()). A pure model in the *View manner: no JavaFX, no Game, and
> it never writes a file. A snapshot is immutable, so the screen reads one on a worker thread (refresh) and hands it to the clock's thread.
> 
> THE CITY IN PLAY (LB4). A city loaded from slot 3 is also slot 3 on disk: the same name, the city as it was last saved. Compared at the active city's age
> it would nearly tie with itself, so a slot whose founding (name, world seed and founding cash) is the city in play's (Played) is marked inPlay in now() and left out of
> at() and stoppedAt(). A caller that has no city in play passes Played.NONE, which matches nothing.
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

**Uses:** [Rankings](Rankings.md) (12), [GameFiles](GameFiles.md) (8), [HistorySave](HistorySave.md) (5), [CityMeasures](CityMeasures.md) (4), [AgeBand](AgeBand.md) (3), [GameLog](GameLog.md) (2), [SaveHeader](SaveHeader.md) (2), [Denomination](Denomination.md) (2), [WorldEconomy](WorldEconomy.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [Game](Game.md) (1), [Currency](Currency.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [LandManager](LandManager.md) (1)

**Used by (4):** [KeptBench](KeptBench.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [UiProbe](UiProbe.md)

## Sections

| line | section |
|---:|---|
| 113 | · reading |
| 242 | · answering |

## Enum constants

| line | constant | says |
|---:|---|---|
| 86 | `SavedCities.Why.OLDER_BUILD` |  |
| 86 | `SavedCities.Why.NEWER_BUILD` |  |
| 86 | `SavedCities.Why.DAMAGED` |  |
| 86 | `SavedCities.Why.NO_HISTORY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `SavedCities.FIRST_SLOT` | `1` | The first slot read: slot 0 is the autosave, a copy of a numbered city, and is left out. |
| 42 | `SavedCities.KEPT` | `keptSeries()` | The history series a slot keeps: what the measures read, and the three that make the pivot (fxRate and fxParity are among the first). |
| 60 | `SavedCities.Played.NONE` | `new Played(null, 0L, 0.0)` | No city in play: matches no slot. |
| 103 | `SavedCities.NONE` | `new SavedCities(List.of(), 0)` | The snapshot before any slot was read: no cities, nothing left out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 105 | `private final List<Entry> entries` |  |
| 106 | `private final int reads` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 269 | **type** `public final class SavedCities` | The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read only, the autosave (slot 0) left out, each as a Rankings.City at its la... |
| 44 | 7 | `private static Set<String> keptSeries()` |  |
| 58 | 15 | **type** `public record Played(String name, long seed, double foundingCash)` | The city in play, by its founding: its name, its world seed (LB4) and the cash it was founded with (LB5). |
| 63 | 3 | `public static Played of(Game g)` _(in SavedCities.Played)_ | The game's city, by its founding. |
| 68 | 4 | `boolean matches(String slotName, Long slotSeed, Double slotCash)` _(in SavedCities.Played)_ | True when this is a city in play and a slot with this name, this seed and this founding cash (null: the header has none) is its founding's. |
| 75 | 1 | **type** `public record Stopped(int slot, String name, int lastMonth)` | A saved city that never got as far as an age: its slot, its name and the last month of its history ("stopped at Feb 2031": CityCalendar.formatShort). |
| 78 | 6 | **type** `public record LeftOut(int olderBuild, int newerBuild, int damaged, int noHistory)` | The saved files left off the board and why, counted: a save from an older build, from a newer one, one that cannot be read, and one with no history. |
| 80 | 3 | `public int total()` _(in SavedCities.LeftOut)_ | All of them. |
| 86 | 1 | **type** `enum Why` | Why a slot is not a city on the board; null for an empty slot and for a city. |
| 89 | 1 | **type** `record Stamp(Path save, long saveSize, long saveTime, long historySize, long historyTime)` | Both files of a slot as the disk had them: the path and each file's size and modified time (-1 for a file that is not there). |
| 92 | 6 | **type** `private record Holding(String name, Long worldSeed, Double foundingCash, String currency, double foundingRa...` | What a readable slot keeps: the city as its last save has it, the slim history behind it, and what an earlier month needs of the header. |
| 94 | 3 | `boolean isTheCityIn(Played played)` _(in SavedCities.Holding)_ | True when this slot's founding is the city in play's (LB5: name, seed and founding cash). |
| 100 | 1 | **type** `private record Entry(int slot, Stamp stamp, Holding kept, Why left)` | One slot as read: its stamp, and either the city it keeps, or why it is left off, or neither (an empty slot). |
| 108 | 4 | `private SavedCities(List<Entry> entries, int reads)` |  |

### reading (lines 113-241)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 3 | `public static SavedCities read(GameFiles files)` | Every slot of the files read afresh: NONE.refresh(files). |
| 124 | 15 | `public SavedCities refresh(GameFiles files)` | A new snapshot of the files, reusing every slot of this one whose save and history files have the size and modified time they had (and the path), so that only the slot saved since is read again. |
| 141 | 3 | `public int reads()` | How many slots the refresh that made this snapshot parsed from disk (the rest were reused or empty): 0 when nothing was saved since the last. |
| 145 | 3 | `private Entry entryOf(int slot)` |  |
| 154 | 4 | `private static Stamp stampOf(GameFiles files, int slot)` | Both files of a slot as the disk has them now. |
| 159 | 7 | `private static long sizeOf(Path file)` |  |
| 167 | 7 | `private static long timeOf(Path file)` |  |
| 179 | 8 | `private static Entry readSafely(GameFiles files, int slot, Stamp stamp)` | One slot, and whatever goes wrong in it stays in it (LB3's gate review): a slot whose header or history will not decode, or that runs the heap out, is DAMAGED and logged, and the other slots are read as before, so one... |
| 194 | 33 | `private static Entry readSlot(GameFiles files, int slot, Stamp stamp)` | One slot: the header through GameFiles.readHeader, then the history file read WHOLE (Files.readAllBytes, the handle closed) and only then parsed, skipping every series but the kept ones and keeping those as primitive ... |
| 228 | 3 | `private static Entry left(int slot, Stamp stamp, Why why)` |  |
| 233 | 8 | `private static CityMeasures.Extras extrasOf(SaveHeader header)` | What the history does not keep, from the header: the people in each age band (PopulationCohorts.restore by name) and the city's dry ground in square kilometres (LandManager.km2). |

### answering (lines 242-304)

| line | len | member | says |
|---:|---:|---|---|
| 248 | 8 | `public List<Rankings.Saved> now(Played played)` | The saved cities at their last saved month, in slot order: their figures from the whole history and the header's land and bands, in the units of their last save. |
| 262 | 11 | `public List<Rankings.Saved> at(int month, Played played)` | The saved cities at game month `month` (D3, the active city's age), in slot order: each read from its history at that month, its rate, parity and price level the series' there, its figures CityMeasures.at with Extras.... |
| 275 | 8 | `public List<Stopped> stoppedAt(int month, Played played)` | The saved cities whose history ends before game month `month`: "stopped at Feb 2031" (CityCalendar.formatShort(lastMonth)), no figure and no row. |
| 285 | 13 | `public LeftOut left()` | The saves left off the board and why, counted; an empty slot is no save and is not counted. |
| 300 | 4 | `HistorySave history(int slot)` | The slim history a slot keeps, or null when the slot is not a city on the board: for a harness that holds it to the file's. |

