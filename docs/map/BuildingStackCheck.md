# BuildingStackCheck.java - 424 lines · 6 methods · 3 constants · harnesses

`ham/citybuildersim/BuildingStackCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
>  7. below the ceiling nothing reads differently (section 1);
>  8. an order through Game.buildStack (not BuildingManager.addStack) on a copy whose House stack stands just under the
>     ceiling, its material past 2^31 units: its quote, the ground it takes, the cash it charges, the receipt, the yard's
>     share delivered to the sites, the material the sites owe, and the invoice of a run of two, whose second order is quoted
>     on the yard the first leaves;
>  9. a month's material draw past 2^31 units is drawn (the sites' and a repair's, yard first), booked in materialsConsumed,
>     and saved and loaded whole, while a draw of infinite units (Math.round's Long.MAX_VALUE) takes nothing.
> 
> WHY (SQ1b, the two notes of SQ1's review). Section 6 ordered through BuildingManager.addStack, so the quote, the ground and the
> charge of Game.buildStack had no harness past the ceiling; and Game.drawMaterials rounded a month's draw into an int, where a
> month's draw at ten-billion scale passes 2^31 units (this fixture's own month draws tens of millions, so section 9 drives the draw
> there): the whole draw wrapped to a negative count and went unbought and unbooked, and the yard, materialsConsumed and the run's
> invoice carried the same int. They are a long since SQ1b (an old save's int reads
> into one, so no format bump); the harness is red with any of them an int again.
> 
> The orders here are int-sized, as the game's are; a stack passes the ceiling by two of them, or by the
> scaler. Written so that it compiles against main's int stack, where it is red: the sums wrap.

**Uses:** [Game](Game.md) (26), [BuildingsStacks](BuildingsStacks.md) (14), [GameFiles](GameFiles.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (8), [ScaleCheck](ScaleCheck.md) (7), [BuildingManager](BuildingManager.md) (6), [LongPlaytest](LongPlaytest.md) (5), [JobType](JobType.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [Markets](Markets.md) (4), [BuildCardCheck](BuildCardCheck.md) (2), [TempDirs](TempDirs.md) (1), [Landmasses](Landmasses.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Good](Good.md) (1)

## Sections

| line | section |
|---:|---|
| 98 | · the fixture: a young town, saved with its houses on site and again once they stand |
| 128 | · 1. a stack takes orders past 2^31 - 1 |
| 168 | · 2. the unfitted count rides with the stack |
| 180 | · 3. the sums: homes, posts and household places |
| 207 | · 4. ScaleCheck.scale(): the copy's House stack past 2^31 |
| 233 | · 5. the same from a save written with an order on site |
| 253 | · 6. an order carries the loaded copy past 2^31 - 1, and its save reads back whole |
| 291 | · 7. Game.buildStack on a copy just under the ceiling: its quote, its ground and its invoice (SQ1b) |
| 363 | · 8. a month's draw past 2^31 units: the books it feeds read it whole |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingStackCheck.FIXTURE_MONTHS` | `3` | The fixture's months before it is saved with its houses finished: a young town, which keeps the run short. |
| 60 | `BuildingStackCheck.ORDER_HOUSES` | `40` | The House order the fixture's town has on site when its first save is written. |
| 63 | `BuildingStackCheck.ROOM_BELOW` | `100` | How far below the ceiling section 6's copy stands, so that one order of the next size passes it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 53 | `static PrintStream out` |  |
| 54 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 50 | 375 | **type** `public class BuildingStackCheck` | A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through the save and the load, through ScaleCheck's scaler, and in the sums a city is read by. |
| 65 | 5 | `static void same(String label, long actual, long expected)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 5 | `static void quietly(Runnable r)` |  |
| 82 | 13 | `public static void main(String[] args) throws Exception` |  |
| 96 | 321 | `static void run(Path root) throws Exception` |  |
| 419 | 5 | `static BuildingsStacks stackOf(Game g, String name)` | A game's stack of the template named, or null: each game's templates are its own, so a stack is found by the game's own. |

