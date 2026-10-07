# SaveSlotCheck.java - 315 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/SaveSlotCheck.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> Verifies the slot system: ten saves plus an autosave, the version stamp, and
> the labels the menu is drawn from.
> 
> The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
> slots and quietly writes them all to the same file, or draws slot 3's label
> from slot 7's city, is worse than a single save - it invites a player to
> spread a hundred hours across ten slots that were never really there.
> 
> AND THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52). Until 0.7.51 the twelfth
> month's autosave was written near the top of Game.nextMonth(), after the
> calendar had turned and before the month ran: the file said month N, its
> history ended at N - 1, and a city loaded from it never ran N. Each of the
> three autosaves - the twelfth month's, the one before a skip and the one on
> quit - is loaded here and played on: its month is its history's last, and
> the history has no gap and no month twice.

**Uses:** [GameFiles](GameFiles.md) (23), [Game](Game.md) (20), [GameVersion](GameVersion.md) (8), [SaveHeader](SaveHeader.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2)

## Sections

| line | section |
|---:|---|
| 55 | · 1. eleven distinct files |
| 79 | · 2. empty means empty |
| 91 | · 3. three cities that do not touch |
| 132 | · 4. names |
| 152 | · 5. the version stamp |
| 179 | · 6. each slot round-trips into its own city |
| 197 | · 7. the autosave |
| 247 | · 7b. the autosave holds a whole month (0.7.52) |
| 261 | · 8. histories are per slot |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 293 | **type** `public class SaveSlotCheck` | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 43 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 50 | 228 | `public static void main(String[] args) throws Exception` |  |
| 280 | 3 | `static void wholeMonth(String which, Game city, GameFiles files)` | The autosave as `city` stands now: wholeMonth(which, city, files, city.getMonth()). |
| 289 | 18 | `static void wholeMonth(String which, Game city, GameFiles files, int month)` | Loads the autosave and plays two single months on (single, so neither writes it again): it carries `month`, its history ends at that month, and after the two its history runs one month at a time to the city's. |
| 308 | 7 | `static void cleanUp(Path root)` |  |

