# BuildingStackCheck.java - 668 lines · 14 methods · 5 constants · harnesses

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
>     and saved and loaded whole, while a draw of infinite units (Math.round's Long.MAX_VALUE) takes nothing;
> 10. a template has ONE stack, however it is ordered, played, saved and loaded, and a shed takes the count it asks of it, up to what
>     stands, through Game's own closing and shedding paths, leaving the emptied stack in place and the books each keeps (the count, the
>     ground, the posts, the salvage) at what was scrapped and no more (SB1).
> 
> WHY (SB1, the premise HS1's notes queued and this section proves false). BuildingManager.retire() scraps from the first stack of the
> template it finds and returns, and HS1's OutsideCheck fixture, closing a works, read that as a fault: "a template built in several
> stacks would be only partly closed". No template is. addStack() is the one place a stack is made, and it merges an order into the stack
> of the template's name, so a second order, a building wave, an order on site and every load (Game's restore goes through addStack too)
> add to the one stack; ids are unique (BuildingDataCheck), and a save keys a stack by its id. retire()'s early return is exact, and a
> loop over more stacks would have no city to change. Section 10 is the finding and its tripwire: the day a second stack of one template
> can stand, its stack count goes red here, and retire() is the first thing to mend.
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

**Uses:** [Game](Game.md) (39), [BuildingsStacks](BuildingsStacks.md) (17), [BuildingManager](BuildingManager.md) (14), [GameFiles](GameFiles.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (13), [LongPlaytest](LongPlaytest.md) (12), [ScaleCheck](ScaleCheck.md) (7), [JobType](JobType.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [Markets](Markets.md) (4), [BusinessInvestment](BusinessInvestment.md) (3), [BuildCardCheck](BuildCardCheck.md) (2), [OldMetal](OldMetal.md) (2), [DemolitionLog](DemolitionLog.md) (1), [TempDirs](TempDirs.md) (1), [Landmasses](Landmasses.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Good](Good.md) (1), [Investor](Investor.md) (1), [ConstructionControl](ConstructionControl.md) (1)

## Sections

| line | section |
|---:|---|
| 121 | · the fixture: a young town, saved with its houses on site and again once they stand |
| 151 | · 1. a stack takes orders past 2^31 - 1 |
| 191 | · 2. the unfitted count rides with the stack |
| 203 | · 3. the sums: homes, posts and household places |
| 230 | · 4. ScaleCheck.scale(): the copy's House stack past 2^31 |
| 256 | · 5. the same from a save written with an order on site |
| 276 | · 6. an order carries the loaded copy past 2^31 - 1, and its save reads back whole |
| 314 | · 7. Game.buildStack on a copy just under the ceiling: its quote, its ground and its invoice (SQ1b) |
| 386 | · 8. a month's draw past 2^31 units: the books it feeds read it whole |
| 440 | · 10. a template has one stack, and a shed takes what it asks of it (SB1) |
| 444 | 10. A TEMPLATE HAS ONE STACK, AND A SHED TAKES WHAT IT ASKS OF IT (SB1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `BuildingStackCheck.FIXTURE_MONTHS` | `3` | The fixture's months before it is saved with its houses finished: a young town, which keeps the run short. |
| 71 | `BuildingStackCheck.ORDER_HOUSES` | `40` | The House order the fixture's town has on site when its first save is written. |
| 74 | `BuildingStackCheck.ROOM_BELOW` | `100` | How far below the ceiling section 6's copy stands, so that one order of the next size passes it. |
| 447 | `BuildingStackCheck.PLAYED_MONTHS` | `24` | Months the played city runs in section 10, a stack count read at the end of each. |
| 453 | `BuildingStackCheck.FIXTURE_CASH` | `1e6` | What section 10's fixtures are given to pay a demolition and a plot with: enough for them, and small enough that a double still tells the plot's price from the treasury (a 1e12 added to a treasury of millions lost the... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `static int fails` |  |
| 64 | `static PrintStream out` |  |
| 65 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 608 | **type** `public class BuildingStackCheck` | A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through the save and the load, through ScaleCheck's scaler, and in the sums a city is read by. |
| 76 | 5 | `static void same(String label, long actual, long expected)` |  |
| 82 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 5 | `static void near(String label, double actual, double expected, double tolerance)` | A figure read against the one it should be, relative to the larger of the two or to one; both printed (SB1). |
| 95 | 3 | `static DemolitionLog.Entry newest(Game g)` | The newest line of a city's demolition log (the log lists newest first). |
| 99 | 5 | `static void quietly(Runnable r)` |  |
| 105 | 13 | `public static void main(String[] args) throws Exception` |  |
| 119 | 324 | `static void run(Path root) throws Exception` |  |

### 10. A TEMPLATE HAS ONE STACK, AND A SHED TAKES WHAT IT ASKS OF IT (SB1) (lines 444-668)

| line | len | member | says |
|---:|---:|---|---|
| 456 | 6 | `static int mostPerId(java.util.List<Integer> ids)` | The most stacks any one id has among these (SB1): the tripwire's own reading, proved on lists it can tell apart. |
| 464 | 5 | `static int mostStacksOfOne(BuildingManager bm)` | The most stacks any one template has in this manager. |
| 471 | 5 | `static long postsOf(BuildingManager bm)` | Every post the buildings offer, over every kind of job (BuildingManager.getTotalJobs(JobType)). |
| 478 | 5 | `static Game cityOn(Path root, String label)` | A fixture city on a folder of its own, founded the way the playtest's is. |
| 485 | 11 | `static long shed(Game g, java.lang.reflect.Method retire, java.lang.reflect.Method investor, BuildingsTemplate t, long count) t...` | Game's own shedding path, called as Game calls it: a decision of the sector's template and a count, the sector's investor selling, the distress rule not the one deciding. |
| 497 | 164 | `static void oneStackPerTemplate(Path root) throws Exception` |  |
| 663 | 5 | `static BuildingsStacks stackOf(Game g, String name)` | A game's stack of the template named, or null: each game's templates are its own, so a stack is found by the game's own. |

