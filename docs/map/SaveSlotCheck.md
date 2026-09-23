# SaveSlotCheck.java - 264 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/SaveSlotCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Verifies the slot system: ten saves plus an autosave, the version stamp, and
> the labels the menu is drawn from.
> 
> The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
> slots and quietly writes them all to the same file, or draws slot 3's label
> from slot 7's city, is worse than a single save - it invites a player to
> spread a hundred hours across ten slots that were never really there.

**Uses:** [GameFiles](GameFiles.md) (20), [Game](Game.md) (16), [GameVersion](GameVersion.md) (8), [SaveHeader](SaveHeader.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2)

## Sections

| line | section |
|---:|---|
| 47 | · 1. eleven distinct files |
| 71 | · 2. empty means empty |
| 83 | · 3. three cities that do not touch |
| 124 | · 4. names |
| 144 | · 5. the version stamp |
| 171 | · 6. each slot round-trips into its own city |
| 189 | · 7. the autosave |
| 239 | · 8. histories are per slot |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 250 | **type** `public class SaveSlotCheck` | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. |
| 19 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 24 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 35 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 42 | 214 | `public static void main(String[] args) throws Exception` |  |
| 257 | 7 | `static void cleanUp(Path root)` |  |

