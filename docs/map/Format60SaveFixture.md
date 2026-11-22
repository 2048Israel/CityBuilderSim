# Format60SaveFixture.java - 113 lines · 5 methods · 2 constants · model

`ham/citybuildersim/Format60SaveFixture.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> A native format-60 city and history, refused before either file or a running city is changed.
> WHY. Restamping a current save or testing the floor predicate alone does not exercise the actual old shape.
> The two compressed resources are captured once on main 01e6c2d4b (0.9.188, format 60), without JSON edits.

**Uses:** [Game](Game.md) (11), [GameFiles](GameFiles.md) (3), [GameVersion](GameVersion.md) (3), [ReadPathCheck](ReadPathCheck.md) (2), [BuildingManager](BuildingManager.md) (1), [HistorySave](HistorySave.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [SaveHeader](SaveHeader.md) (1), [PriceLevel](PriceLevel.md) (1)

**Used by (3):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SubstationCheck](SubstationCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 21 | `Format60SaveFixture.JSON` | `new Gson()` |  |
| 22 | `Format60SaveFixture.SAVE` | `"format60-save.json.gz", HISTORY = "format60-history.json.gz"` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 94 | **type** `final class Format60SaveFixture` | A native format-60 city and history, refused before either file or a running city is changed. |
| 24 | 7 | `private static void quietly(Runnable work)` |  |
| 32 | 6 | `private static void unpack(String name, Path destination) throws IOException` |  |
| 39 | 5 | `private static String state(Game game)` |  |
| 46 | 43 | `static boolean checks(boolean readPaths)` | Shared by the save, substation and real-read harnesses; no save slot outside the scratch folder is touched. |
| 91 | 22 | `public static void main(String[] args) throws Exception` | Capture only on the actual old executable; never create an old fixture by relabelling a new save. |

