# EquipmentCheck.java - 1,087 lines · 44 methods · 3 constants · harnesses

`ham/citybuildersim/EquipmentCheck.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> Equipment as data, the count of buildings still without it on each stack, R0c's 1900 forms set from that count, and (slice B) what a piece
> would earn and cost and whether it pays, read on demand.
> 
> WHAT THIS HAS TO PROVE (R1 slice A, 2026-11-05; docs/queue/R1-notes.md calls 2 to 9; research-design 2.1-2.2). R0c built the steam, the
> farms' horses and the carts' horses as shares of a sector that nothing sets; R1 fits equipment building by building, and "the forms read one
> number" (R0c's call 3). This part is the state, off: a piece of equipment is data on a template, a stack counts the buildings without it, and
> EquipmentForms sets the shares from the counts where the Policy hears the month. In every played city no building is unfitted, so nothing
> moves. What is checkable:
> 
>   1. THE DATA IS READ: every template the notes name carries its piece, its since, ends, buys, work and life read back from buildings.json
>      (the file read here with Gson, its figures not typed twice), no other template carries one, the rules the figures were struck by hold
>      on the template's own electricity, construction points, land and truck room, the tractor's code is never known, the drive is known
>      from 1900; and a bad entry in the file (no id, a second of an id, an unknown form or good, no since, a life of 0) is reported and
>      skipped, the good ones beside it read
>   2. A PLAYED CITY HAS NONE UNFITTED: after SETTLE and twelve months no stack has an unfitted building, every steam share and both horse
>      shares are 0 and EquipmentForms has driven none
>   3. THE COUNTS SET THE FORMS: unfitted bakeries set Industry's steam share to the share of its grid draw, unfitted farms Agriculture's
>      horse share to the share of its land, an unfitted depot Haulage's horse share to the share of its truck room, each as the harness
>      works it out from the stacks and the templates, and no other sector's
>   4. BACK TO 0: the counts set to 0 put every share driven back to 0 the next month, and it stays 0 a month later
>   5. A SHARE SET BY HAND STANDS: with no count anywhere, a share set by hand is the same after a month (Forms1900Check's way)
>   6. THE COUNT FOLLOWS THE STACK: a count set above what stands is what stands; a scrapped building is an unfitted one first; a building
>      finished (bought, increased, or built on site) leaves the count as it was
>   7. THE SAVE: a city with none unfitted writes no key; a city with counts writes them by template id, and the one loaded from it has the
>      same counts and, before any month is played, the shares the counts set, equal to the saved city's
> 
> R1 slice B (EquipmentFit, research-design 2.2 steps 1 to 4: what a piece adds a month, what it costs, whether it pays over its life and
> passes the money gate; each piece's verdict, read on demand, never in the month):
> 
>   8. THE RULE'S ARITHMETIC: the annuity factor is the months at a rate of 0 and the closed form (and the sum, and BuildAdvice's own factor)
>      at a positive one; a gain just over cost / factor pays and just under does not, a gain just over the hurdle passes the gate and just
>      under does not, each with the other step comfortably met; a gain of nothing never pays; FITTED and NOT_KNOWN price nothing; the cost is
>      the goods at their prices and the work at the point's; the steam gain is the coal less the power, and crosses 0 at the price where
>      they are equal
>   9. THE STEAM VERDICT AT THE CITY'S PRICES: with a Bakery unfitted, the verdict's gain and cost are the pure core's at the prices the
>      model's getters give, its rate and hurdle the debt manager's and the investment's on the whole cost, its kind what judge() says; a
>      coal price just over the break-even (solved from the verdict's own figures) pays and just under does not, in the core and in the city
>  10. THE TRUCK'S, THE TRACTOR'S AND THE RANK: a depot unfitted has a finite gain from Haulage's teams less its trucks' running costs (the
>      trucks' capital left out) and a cost of its trucks at the city's price; an unfitted Grain Farm's tractor is NOT_KNOWN; a stack with none
>      unfitted is FITTED; rank() lists the pieces that pay and no other, best earnings on cost first, ties by template then piece id in any
>      order they come, and a sector with none unfitted ranks none
>  11. THE VERDICT CHANGES NOTHING: twin cities with the same counts, one asked every verdict and every rank every month for six months,
>      are equal to the bit in the treasury, every sector's books and rates, the trucks, the shares and the counts
> 
> Every fixture CAUSES its condition: the counts are set on stacks that stand, in the towns Forms1900Check builds (farms and bakeries) and in
> HaulageCheck's city with a depot built. The figures are asserted against the stacks' own quantities and the templates' own getters, never
> numbers copied from the brief.

**Uses:** [EquipmentFit](EquipmentFit.md) (158), [Game](Game.md) (50), [BuildingsStacks](BuildingsStacks.md) (27), [Good](Good.md) (27), [Equipment](Equipment.md) (22), [BuildingsTemplate](BuildingsTemplate.md) (17), [Sector](Sector.md) (13), [Haulage](Haulage.md) (12), [Forms1900Check](Forms1900Check.md) (11), [GameFiles](GameFiles.md) (7), [SpreadPlanner](SpreadPlanner.md) (6), [BuildAdvice](BuildAdvice.md) (5), [BuildingManager](BuildingManager.md) (4), [BuildingCatalog](BuildingCatalog.md) (2), [Agriculture](Agriculture.md) (2), [HydroSites](HydroSites.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [JobType](JobType.md) (2), [HaulageCheck](HaulageCheck.md) (1), [CityCalendar](CityCalendar.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Freight](Freight.md) (1)

## Sections

| line | section |
|---:|---|
| 108 | · the shares the counts should set, worked out here from the stacks and the templates |
| 201 | 1. the data is read |
| 368 | 2. a played city has none unfitted |
| 385 | 3. the counts set the forms |
| 426 | 4. back to 0 |
| 449 | 5. a share set by hand stands |
| 470 | 6. the count follows the stack |
| 541 | 7. the save |
| 605 | R1 slice B: the payback rule and each piece's verdict (EquipmentFit) |
| 618 | 8. the rule's arithmetic |
| 743 | 9. the steam verdict at the city's prices |
| 858 | 10. the truck's, the tractor's and the rank |
| 1026 | 11. the verdict changes nothing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `EquipmentCheck.DRIVE` | `"electric-drive", TRUCK = "motor-truck", TRACTOR = "tractor"` |  |
| 204 | `EquipmentCheck.DRIVES` | `{ "Meat Works", "Snack & Oils Plant", "Bottling Plant", "Industrial Bakery", ...` | The templates that carry each piece, by name, as the notes say (R1-notes calls 2 and 3). |
| 210 | `EquipmentCheck.FARMS` | `{ "Mixed Farm", "Grain Farm", "Livestock Farm" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 75 | `static int fails` |  |
| 76 | `static PrintStream out` |  |
| 77 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 1015 | **type** `public class EquipmentCheck` | Equipment as data, the count of buildings still without it on each stack, R0c's 1900 forms set from that count, and (slice B) what a piece would earn and cost and whether it pays, read on demand. |
| 81 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 86 | 1 | `static void check(String label, boolean ok)` |  |
| 89 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance (and an absolute floor for a figure that is nothing). |
| 94 | 5 | `static void quietly(Runnable r)` |  |
| 100 | 1 | `static void play(Game g, int months)` |  |
| 102 | 1 | `static void step(Game g, int months)` |  |
| 104 | 3 | `static BuildingsStacks stack(Game g, String template)` |  |

### the shares the counts should set, worked out here from the stacks and the templates (lines 108-200)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 10 | `static double steamShare(Game g, String sectorKey)` | The unfitted part of a sector's grid draw: each stack's unfitted buildings (of the piece that ends the steam) at their electricity over all its buildings' at theirs. |
| 123 | 10 | `static double farmShare(Game g)` | The unfitted part of the farms' land. |
| 135 | 10 | `static double cartShare(Game g)` | The unfitted part of the depots' truck room. |
| 147 | 4 | `static boolean anyUnfitted(Game g)` | Whether any stack of the city is without any piece. |
| 153 | 5 | `static double maxSteam(Game g)` | The largest steam share any sector has. |
| 160 | 5 | `static double maxSteamExcept(Game g, String key)` | The largest steam share any sector has but the one named. |
| 166 | 27 | `public static void main(String[] args) throws Exception` |  |
| 195 | 5 | `static Game cartTown(String label)` | HaulageCheck's city on the goods' literals, with two Truck Depots standing (Haulage's carts are the depots' trucks). |

### 1. the data is read (lines 201-367)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 7 | `static JsonObject jsonPiece(JsonObject building, String pieceId)` | The piece of this id as the file says it, on the building of this id, or null. |
| 221 | 146 | `static void sectionOne() throws Exception` |  |

### 2. a played city has none unfitted (lines 368-384)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 14 | `static void sectionTwo(Game g)` |  |

### 3. the counts set the forms (lines 385-425)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 38 | `static void sectionThree(Game g, Game c)` |  |

### 4. back to 0 (lines 426-448)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 20 | `static void sectionFour(Game g, Game c)` |  |

### 5. a share set by hand stands (lines 449-469)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 18 | `static void sectionFive(Game g)` |  |

### 6. the count follows the stack (lines 470-540)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 52 | `static void sectionSix(Game g)` |  |
| 526 | 5 | `static GameFiles.Result save(Game g, String name)` | A city saved to slot 1 of its own scratch folder, quietly (never a real slot: the game was made with GameFiles.scratch()). |
| 532 | 8 | `static boolean throwsOnPut(BuildingsStacks s)` |  |

### 7. the save (lines 541-604)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 61 | `static void sectionSeven(Game g, Game none)` |  |

### R1 slice B: the payback rule and each piece's verdict (EquipmentFit) (lines 605-617)

| line | len | member | says |
|---:|---:|---|---|
| 608 | 1 | `static double cityPrice(Game g, Good good)` | The city's price of a good, as the planners read it. |
| 611 | 3 | `static Equipment piece(String since, Equipment.Form ends, Map<Good, Double> buys, double work, int life)` | A piece for a pure-core fixture: it buys what it is given, its work and its life are the figures asked about. |
| 616 | 1 | `static int yearOf(Game g)` | The year a game's month is in, as the verdict reads it. |

### 8. the rule's arithmetic (lines 618-742)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 122 | `static void sectionEight(Game g)` |  |

### 9. the steam verdict at the city's prices (lines 743-857)

| line | len | member | says |
|---:|---:|---|---|
| 746 | 16 | `static boolean priceCoalAt(Game g, double target)` | Moves the city's price of coal to `target`, by the world's prices on its market: the price planners read (cityValue()) is affine in them, found from two probes and refined; false if it will not move. |
| 764 | 1 | `static double gainNeeded(EquipmentFit.Verdict v)` | The gain a verdict's piece needs to pay: the greater of the line (cost over factor) and the hurdle. |
| 767 | 3 | `static double breakEvenCoal(Game g, BuildingsTemplate t, EquipmentFit.Verdict v)` | The coal price at which a drive on this building just pays, from a verdict's own figures. |
| 771 | 86 | `static void sectionNine(Game g)` |  |

### 10. the truck's, the tractor's and the rank (lines 858-1025)

| line | len | member | says |
|---:|---:|---|---|
| 861 | 12 | `static List<EquipmentFit.Verdict> payingPieces(Game g, Sector sector)` | The harness's own list of the pieces that pay on a sector's stacks, from verdict() alone, in the order it comes. |
| 874 | 114 | `static void sectionTen(Game g, Game c)` |  |
| 990 | 5 | `static List<String> rankIds(Game g, Sector s)` | The ids of a sector's ranked verdicts, template and piece: for a failure's detail. |
| 997 | 3 | `static EquipmentFit.Verdict fixed(int templateId, String pieceId, double earningsOnCost)` | A verdict that pays, with the figures a ranking reads: its template, piece and earnings on cost. |
| 1001 | 5 | `static EquipmentFit.Verdict sortedFirst(List<EquipmentFit.Verdict> list)` |  |
| 1008 | 5 | `static<T> List<List<T>> permutations(List<T> items)` | Every order a list of distinct items can come in. |
| 1014 | 11 | `private static<T> void permute(List<T> items, int k, List<List<T>> all)` |  |

### 11. the verdict changes nothing (lines 1026-1087)

| line | len | member | says |
|---:|---:|---|---|
| 1029 | 6 | `static void askEverything(Game g)` | Asks the city every verdict and every rank there is: each piece of each stack, fitted or not, and each sector's ranking. |
| 1037 | 11 | `static String formsAndCounts(Game g)` | The shares and counts the forms and the stacks hold, as text: for the twins' comparison beside Forms1900Check's snapshot. |
| 1050 | 12 | `static String twinsAfter(Game asked, Game left, int months, boolean carts)` | Plays two twin cities for `months` (the cart towns by toggleNextMonth, the others by simulateMonths, as their sections do), asking the first everything before and after each month; the first month they differ in, or n... |
| 1063 | 24 | `static void sectionEleven()` |  |

