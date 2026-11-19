# BuildingStackCheck.java - 285 lines · 6 methods · 3 constants · harnesses

`ham/citybuildersim/BuildingStackCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through
> the save and the load, through ScaleCheck's scaler, and in the sums a city is read by.
> 
> WHY (SQ1, found by ST3). BuildingsStacks kept its quantity, its on-site count and its unfitted counts in
> an int, and a building type's units merge into one stack. Jerus's plans are cities of 5 and 10 billion
> people (ScaleCheck.FREE_PEOPLE); a town housed in Houses (6 people each) holds about 0.2 Houses a person,
> so at 10 billion its House stack is about 2e9, against an int's 2,147,483,647. ST3 met the wall in
> ScaleCheck's ten-billion copy: where the stack times K passed it the copy did not load ("the copy loads"
> FAIL), and near it the copy's largest order was one building. The counts are a long since SQ1; an old
> save's int reads into one, so there is no format bump.
> 
> What this has to prove:
>  1. a stack driven past 2^31 - 1 by an order, one on site and one standing, reads back whole, and
>     scrapping floors at none;
>  2. its unfitted count rides with it, never more than stand;
>  3. the manager's sums, a city's homes, posts and household places, are its quantity times the
>     template's, worked out apart in BigInteger;
>  4. a city copied K times over by ScaleCheck.scale() so that its House stack passes 2^31 loads, and the
>     stack, its homes, its posts and its places are K times the city's;
>  5. ...and its on-site count too, from a save written with an order on site;
>  6. an order placed on the loaded copy carries it past 2^31, and a save of it reads back whole;
>  7. below the ceiling nothing reads differently (section 1).
> 
> The orders here are int-sized, as the game's are; a stack passes the ceiling by two of them, or by the
> scaler. Written so that it compiles against main's int stack, where it is red: the sums wrap.

**Uses:** [Game](Game.md) (15), [BuildingsStacks](BuildingsStacks.md) (13), [GameFiles](GameFiles.md) (10), [ScaleCheck](ScaleCheck.md) (6), [LongPlaytest](LongPlaytest.md) (5), [BuildingManager](BuildingManager.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [JobType](JobType.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | · the fixture: a young town, saved with its houses on site and again once they stand |
| 115 | · 1. a stack takes orders past 2^31 - 1 |
| 155 | · 2. the unfitted count rides with the stack |
| 167 | · 3. the sums: homes, posts and household places |
| 194 | · 4. ScaleCheck.scale(): the copy's House stack past 2^31 |
| 220 | · 5. the same from a save written with an order on site |
| 240 | · 6. an order carries the loaded copy past 2^31 - 1, and its save reads back whole |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `BuildingStackCheck.FIXTURE_MONTHS` | `3` | The fixture's months before it is saved with its houses finished: a young town, which keeps the run short. |
| 47 | `BuildingStackCheck.ORDER_HOUSES` | `40` | The House order the fixture's town has on site when its first save is written. |
| 50 | `BuildingStackCheck.ROOM_BELOW` | `100` | How far below the ceiling section 6's copy stands, so that one order of the next size passes it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |
| 40 | `static PrintStream out` |  |
| 41 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 249 | **type** `public class BuildingStackCheck` | A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through the save and the load, through ScaleCheck's scaler, and in the sums a city is read by. |
| 52 | 5 | `static void same(String label, long actual, long expected)` |  |
| 58 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 63 | 5 | `static void quietly(Runnable r)` |  |
| 69 | 13 | `public static void main(String[] args) throws Exception` |  |
| 83 | 195 | `static void run(Path root) throws Exception` |  |
| 280 | 5 | `static BuildingsStacks stackOf(Game g, String name)` | A game's stack of the template named, or null: each game's templates are its own, so a stack is found by the game's own. |

