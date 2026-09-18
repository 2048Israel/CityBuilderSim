# GameVersion.java - 608 lines · 4 methods · 4 constants · model

`ham/citybuildersim/GameVersion.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> What build this is, and what shape its saves are.
> 
> TWO NUMBERS, ON PURPOSE
> 
> VERSION is for people: it goes in the window title and in every save, so a
> bug report or a broken save says which build produced it. Bump it whenever
> something ships.
> 
> SAVE_FORMAT is for the loader, and it changes far more rarely - only when the
> save's SHAPE changes in a way an older build could not read correctly. It
> exists to catch one specific accident: opening a save from a newer build in
> an older one. Without it the older build reads the fields it recognises,
> silently ignores the rest, and hands back a city missing whatever the newer
> build added - which looks like a working load right up until something is
> quietly gone.
> 
> Older saves are still read. That direction is safe: every field added since
> has a sensible default, and the load path already handles the pre-slot,
> pre-flow and pre-land formats.
> 
> KEEPING IT IN SYNC
> 
> THIS IS THE ONLY PLACE IT IS WRITTEN (2026-09-14). "Build EXE.bat" reads the
> line below with findstr and sets APPVER from it, so jpackage stamps the exe
> with whatever is here and cannot disagree.
> 
> It used to be typed in both, with a note here asking whoever changed one to
> remember the other. That is a hope rather than a mechanism, and it went out
> of step the first time it mattered - 0.5.1 in the source, 0.5.0 on the exe.
> What the parse depends on: one line declaring the field, with a quoted
> literal ending in a semicolon. findstr matches on the declaration keywords
> through the equals sign rather than on the field name alone, which is why
> prose here can mention VERSION without being taken for the declaration - an
> earlier wording of this very comment was a second match, and survived only
> because it happened to carry no equals sign.

**Used by (7):** [Game](Game.md), [GameLog](GameLog.md), [RobustnessCheck](RobustnessCheck.md), [SaveHeader](SaveHeader.md), [SaveSlotCheck](SaveSlotCheck.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 281 | · 18  Education carries its pipeline (2026-09-06). Until now `Education` |
| 300 | · NOT 20: the inbox, and thirty-one new graph series (2026-09-08). |
| 379 | · 20  The unit of construction material changed meaning (2026-09-10). |
| 409 | · NOT 21: the sectors' savings abroad (2026-09-10, OutwardInvestment). |
| 420 | · NOT 21 EITHER: the households' cells and the share register |
| 431 | · NOR THIS: the exchange and the households' dollars abroad (2026-09-11). |
| 448 | · 21  THE SECTOR TEMPLATE (2026-09-11), and the first CLEAN BREAK. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `GameVersion.VERSION` | `"0.6.7"` | 0.6.7 - THE FIFTEENTH SECTOR, AND A MEAL OUT IS FOOD. |
| 576 | `GameVersion.SAVE_FORMAT` | `27` |  |
| 579 | `GameVersion.FIRST_SECTOR_FORMAT` | `21` | The first format a sector can be read out of. |
| 581 | `GameVersion.NAME` | `"CityBuilderSim"` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 569 | **type** `public final class GameVersion` | What build this is, and what shape its saves are. |

### 18  Education carries its pipeline (2026-09-06). Until now `Education` (lines 281-299)

### NOT 20: the inbox, and thirty-one new graph series (2026-09-08). (lines 300-378)

### 20  The unit of construction material changed meaning (2026-09-10). (lines 379-408)

### NOT 21: the sectors' savings abroad (2026-09-10, OutwardInvestment). (lines 409-419)

### NOT 21 EITHER: the households' cells and the share register (lines 420-430)

### NOR THIS: the exchange and the households' dollars abroad (2026-09-11). (lines 431-447)

### 21  THE SECTOR TEMPLATE (2026-09-11), and the first CLEAN BREAK. (lines 448-608)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 1 | `private GameVersion()` |  |
| 586 | 3 | `public static String title()` | For the window title. |
| 596 | 3 | `public static boolean isFromNewerBuild(int saveFormat)` | True when a save claims a format this build does not know how to read. |
| 605 | 3 | `public static boolean isFromBeforeSectors(int saveFormat)` | True when a save predates the sector template and so carries nothing this build can read a sector out of. |

