# SaveHeader.java - 110 lines · 19 methods · 0 constants · model

`ham/citybuildersim/SaveHeader.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> Just enough of a save to label it on the slot list.
> 
> WHY THIS IS NOT A SEPARATE FILE
> 
> The obvious way to show "Month 213 - 1,240 people" on a menu is to write a
> small .meta file beside each save. That is two files per slot which have to
> agree, and the day they stop agreeing the menu lies about what is in a slot -
> which is the one thing a save menu must never do.
> 
> So this parses the save itself. gson fills whichever fields it recognises and
> ignores the rest, so these field names are deliberately IDENTICAL to the ones
> in DataSave: the same JSON deserialises into either class, and the header can
> never describe a different city from the file it came from.
> 
> The cost is reading the whole file to show one line. At roughly 15 KB a save
> and eleven slots, that is nothing.

**Uses:** [GameVersion](GameVersion.md) (2), [Founding](Founding.md) (1)

**Used by (9):** [Game](Game.md), [GameFiles](GameFiles.md), [ReplayCheck](ReplayCheck.md), [SaveDump](SaveDump.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `private int month` | Field names must match DataSave exactly - see above. |
| 25 | `private long population` |  |
| 26 | `private double cash` |  |
| 28 | `private String slotName` |  |
| 29 | `private String gameVersion` |  |
| 30 | `private int saveFormat` |  |
| 31 | `private long savedAt` |  |
| 34 | `private String cityName` | The city's name (0.7.10), DataSave's founding record's; absent on an older save. |
| 40 | `private Long worldSeed` | The world's seed (DataSave's key of that name; LB4): with the name, the founding a slot shares with the city in play. |
| 48 | `private String currencyPlural` | What the Rankings board reads of a saved city beside its history (LB3, SavedCities): the money's name, the world's prices, the money's unit, and the land and the people by age band as they stood at the last save. |
| 49 | `private double[] worldEconomy` |  |
| 50 | `private double[] denomination` |  |
| 51 | `private double[] cohorts` |  |
| 52 | `private String[] bandNames` |  |
| 54 | `private double landOwned` | The city's dry ground in square feet (DataSave.landOwned); 0 on a save from before land existed. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 90 | **type** `public class SaveHeader` | Just enough of a save to label it on the slot list. |
| 56 | 1 | `public int getMonth()` |  |
| 57 | 1 | `public long getPopulation()` |  |
| 58 | 1 | `public double getCash()` |  |
| 59 | 1 | `public String getSlotName()` |  |
| 60 | 1 | `public String getGameVersion()` |  |
| 61 | 1 | `public int getSaveFormat()` |  |
| 62 | 1 | `public long getSavedAt()` |  |
| 64 | 1 | `public Long getWorldSeed()` | The world's seed as saved, or null on a save that does not carry it (LB4: such a slot is never the city in play). |
| 67 | 1 | `public String getCurrencyPlural()` | The city's money in the plural ("Arden dollars"), or null on a save that does not carry it. |
| 69 | 1 | `public double[] getWorldEconomy()` | WorldEconomy.toSaveArray(), or null: WorldEconomy.restore reads it. |
| 71 | 1 | `public double[] getDenomination()` | Denomination.toSaveArray() (the unit and the reforms), or null: Denomination.restore reads it. |
| 73 | 1 | `public double[] getCohorts()` | The pyramid, PopulationCohorts.toSaveArray(), or null: PopulationCohorts.restore reads it with getBandNames(). |
| 75 | 1 | `public String[] getBandNames()` | The age bands the cohorts were written with, or null on a save from before they travelled. |
| 77 | 1 | `public double getLandOwned()` | The city's dry ground in square feet as last saved; 0 when the save does not know. |
| 79 | 3 | `public boolean hasName()` |  |
| 88 | 3 | `public String getCityName()` | The city in the slot, by name - for the slot list. |
| 93 | 3 | `public boolean isFromNewerBuild()` | True when this file was written by a build that knows more than this one. |
| 102 | 3 | `public boolean isNoLongerReadable()` | True when this file is older than this build loads, below GameVersion.OLDEST_LOADABLE_FORMAT (0.7.108; until then, before the sector template only) - refused as a newer build's file is. |
| 107 | 3 | `public boolean isReadableHere()` | Loadable by this build: neither from a newer one nor older than it reads. |

