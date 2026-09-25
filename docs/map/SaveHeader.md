# SaveHeader.java - 71 lines · 12 methods · 0 constants · model

`ham/citybuildersim/SaveHeader.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Used by (6):** [Game](Game.md), [GameFiles](GameFiles.md), [SaveDump](SaveDump.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `private int month` | Field names must match DataSave exactly - see above. |
| 25 | `private int population` |  |
| 26 | `private double cash` |  |
| 28 | `private String slotName` |  |
| 29 | `private String gameVersion` |  |
| 30 | `private int saveFormat` |  |
| 31 | `private long savedAt` |  |
| 34 | `private String cityName` | The city's name (0.7.10), DataSave's founding record's; absent on an older save. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 51 | **type** `public class SaveHeader` | Just enough of a save to label it on the slot list. |
| 36 | 1 | `public int getMonth()` |  |
| 37 | 1 | `public int getPopulation()` |  |
| 38 | 1 | `public double getCash()` |  |
| 39 | 1 | `public String getSlotName()` |  |
| 40 | 1 | `public String getGameVersion()` |  |
| 41 | 1 | `public int getSaveFormat()` |  |
| 42 | 1 | `public long getSavedAt()` |  |
| 44 | 3 | `public boolean hasName()` |  |
| 53 | 3 | `public String getCityName()` | The city in the slot, by name - for the slot list. |
| 58 | 3 | `public boolean isFromNewerBuild()` | True when this file was written by a build that knows more than this one. |
| 63 | 3 | `public boolean isFromBeforeSectors()` | True when this file predates the sector template - the one older shape that is refused. |
| 68 | 3 | `public boolean isReadableHere()` | Loadable by this build: neither from a newer one nor from before the sectors. |

