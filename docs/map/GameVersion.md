# GameVersion.java - 8,316 lines · 6 methods · 4 constants · model

`ham/citybuildersim/GameVersion.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> Older saves are read down to OLDEST_LOADABLE_FORMAT, and refused below it
> the same way (0.7.108). Until the game is published a change that would
> make an older save load wrongly bumps SAVE_FORMAT and raises that floor to
> it, rather than writing a conversion; a field added with a sensible default
> needs neither, since an older save reads it absent.
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

**Used by (19):** [BridgeCheck](BridgeCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthSite](EarthSite.md), [EarthTerrainCheck](EarthTerrainCheck.md), [EnsembleCheck](EnsembleCheck.md), [Game](Game.md), [GameLog](GameLog.md), [LandmassCheck](LandmassCheck.md), [MaterialsCheck](MaterialsCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveHeader](SaveHeader.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [UserInterface](UserInterface.md), [WellCheck](WellCheck.md), [YearBook](YearBook.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 7372 | `GameVersion.VERSION` | `"0.8.72"` | Bump on release. |
| 8214 | `GameVersion.SAVE_FORMAT` | `48` | The save shape. |
| 8256 | `GameVersion.OLDEST_LOADABLE_FORMAT` | `48` | The oldest save format this build loads: a batch that bumps SAVE_FORMAT because an older save would load wrongly raises this to the new format in the same batch, so that save is refused cleanly (isNoLongerReadable()) ... |
| 8264 | `GameVersion.NAME` | `"Civic Ledger"` | The game's name, on the title screen and in the window's title (BR1, 2026-10-27: Jerus chose Civic Ledger, the name itch.io and the releases already wore). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 8275 | **type** `public final class GameVersion` | What build this is, and what shape its saves are. |
| 8266 | 1 | `private GameVersion()` |  |
| 8269 | 3 | `public static String title()` | For the window title. |
| 8279 | 3 | `public static boolean isFromNewerBuild(int saveFormat)` | True when a save claims a format this build does not know how to read. |
| 8288 | 3 | `public static boolean isNoLongerReadable(int saveFormat)` | True when a save is older than this build loads, below OLDEST_LOADABLE_FORMAT: refused as a newer build's is, its file left as it was, the player told in noLongerReadable()'s words. |
| 8297 | 6 | `public static String fromNewerBuild(String savedBy)` | What the player is told about a save from a newer build (FW1): which build wrote it, so they know the build that opens it, as noLongerReadable() names the older one. |
| 8310 | 6 | `public static String noLongerReadable(String savedBy)` | What the load screen's slot list, a refused load and the log tell the player when a save is older than this build loads: the build that saved the city, which still opens it, and this one, which cannot. |

