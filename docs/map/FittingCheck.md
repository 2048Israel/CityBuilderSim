# FittingCheck.java - 1,087 lines · 33 methods · 3 constants · harnesses

`ham/citybuildersim/FittingCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city
> founded the 1900 way, what a building that finishes comes with, and the fitting itself - ordered, paid, bought and worked, ranked with the
> sector's buildings, and the player's Fit.
> 
> WHAT THIS HAS TO PROVE (R1b, 2026-11-05; docs/queue/R1b-notes.md; research-design 2.2 step 6; R1's stranger's review). R1 made a piece
> of equipment the count of a stack's buildings still without it and set R0c's 1900 forms from the counts at the top of every month and on a
> load. This part makes the counts rest honestly, lets a city start with them full, and says what a new building is given. What is checkable:
> 
>   1. THE SHARES AT REST: a month changes the weights the counts are shares of (bakeries on site finish in it), so the shares the top of the
>      month set are not the counts' at its end; the city rests, between two presses, in the shares of the counts as the month left them, the
>      loaded twin of a save written there holds the same shares and drives the same forms before any further press, and one more press on
>      both, through the player's path with the auto-builder on (whose pass reads the shares first), leaves the treasury, every sector's cash
>      and the shares equal to the bit
>   2. CLEARED ON A NEW GAME AND ON A LOAD: a Game with counts drives forms; a save with no counts loaded into it drives the same as the same
>      save loaded into a fresh Game; clear() forgets what was driven; a new game on the same object drives none and has every share at 0
>   3. THE 1900 FOUNDING, OFF BY DEFAULT: Founding.defaults() is not unfitted and a default founding unfits nothing; a founding withUnfitted(true)
>      is, every other choice kept through every withX, saved with the founding (a default city writes no key) and read back; the buildings it
>      stands up are unfitted for every piece their templates carry (the tractor's too), the forms set from them before the first press, and
>      the flag and the counts come back after a save and a load. The founding's own buildings (the bank, the builders' yard) carry no piece,
>      so the harness places a village by hand after newGame(), as the playtest does, and says unfitTheFounding() over it: a village placed
>      without construction is not hooked, and comes as placed until that is said
>   4. WHAT A NEW BUILDING COMES WITH: in a city founded the 1900 way, bakeries ordered on site that finish come fitted with the electric
>      drive when it pays for a new building at the city's coal price (the count unchanged) and without it when it does not (the count up by
>      what finished), a farm finishing has no tractor before PE14's June 1917 (NOT_KNOWN), and the order costs what the model quotes for it in either city,
>      the template's whole cost at that city's builders (R1c: the two cities' queues and wages are each its own, so it is no longer the same
>      figure in both); in a default city every building finishes fitted, at either price
>   5. THE ORDER RANKED WITH THE BUILDINGS (slice B): at a coal price where the drive pays, the choice is the fitting when the plan builds
>      nothing, the fitting when it earns more on its cost than the building the plan would put up and the building when it earns less, and
>      the plan's own Decision, unchanged, when the drive does not pay, when no stack is unfitted, and when the sector has a fitting on site;
>      and one month of the real round orders the fitting
>   6. THE MONEY AND THE GOODS: Industry's cash falls by the total (or the shortfall is borrowed), the market's draw is the piece's units,
>      the builders' fitting revenue is the total, and the total is the draws' cost plus the work at the price a point
>   7. THE WORK: the sites' output falls by exactly the points the fittings took, after the repairs; a piece whose work is more than a
>      month's output takes several months and its count falls only in the month its points are done; jobs are worked in the order placed
>   8. ONE AT A TIME: a sector with a fitting on site orders no second in the round, and rank() leaves out a stack whose unfitted are all on site
>   9. THE DEPOT'S TRUCKS: a motor-truck fitting on a Truck Depot raises Freight's trucks held by the piece's TRUCKS, the builders' draw of
>      TRUCKS is the same units
>  10. THE PLAYER'S FIT: the city pays and puts a "City" job on site even where the verdict is NOT_YET; refused for the tractor, for a stack
>      with every unfitted building on site, and for a treasury short of the total, with nothing drawn and nothing paid
>  11. THE SAVE: no job, no key; the jobs saved and loaded are the same, and the twin and the loaded city one month on are equal to the bit
>  12. A DEFAULT CITY ORDERS NONE: a town founded the default way played 24 months has no job ever on site and no Decision with a piece
> 
> Every fixture CAUSES its condition: the weights move because bakeries on site finish in the month asked about (the harness asserts that the
> unfitted share of Industry's draw differs between the month's top and its end); the coal price is moved across the break-even solved from
> the verdict's own figures (EquipmentCheck 9's way), on the goods' literals so that the month does not hand the world's band back; the held
> sectors build nothing on their own. Figures are asserted against the stacks' own quantities and counts, the templates' getters and the model's
> verdicts, never numbers copied from the brief.

**Uses:** [Game](Game.md) (57), [EquipmentFit](EquipmentFit.md) (54), [EquipmentCheck](EquipmentCheck.md) (48), [BuildingsStacks](BuildingsStacks.md) (20), [Founding](Founding.md) (20), [Good](Good.md) (16), [Sector](Sector.md) (13), [BusinessInvestment](BusinessInvestment.md) (13), [Equipment](Equipment.md) (12), [GameFiles](GameFiles.md) (8), [Fittings](Fittings.md) (8), [Forms1900Check](Forms1900Check.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (7), [DataSave](DataSave.md) (6), [SpreadPlanner](SpreadPlanner.md) (5), [Sectors](Sectors.md) (4), [MoneyAudit](MoneyAudit.md) (4), [GoodsMarket](GoodsMarket.md) (3), [BuildingManager](BuildingManager.md) (2), [Currency](Currency.md) (2), [World](World.md) (2), [Nations](Nations.md) (2), [Construction](Construction.md) (2), [MaterialsCheck](MaterialsCheck.md) (1), [Agriculture](Agriculture.md) (1), [Landmasses](Landmasses.md) (1), [Trade](Trade.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [Freight](Freight.md) (1)

**Used by (7):** [ArrivalMixCheck](ArrivalMixCheck.md), [BasketYearCheck](BasketYearCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [KeptCheck](KeptCheck.md), [RecipeCheck](RecipeCheck.md), [ResearchViewCheck](ResearchViewCheck.md), [WageLadderCheck](WageLadderCheck.md)

## Sections

| line | section |
|---:|---|
| 136 | 1. the shares at rest |
| 203 | 2. cleared on a new game and on a load |
| 250 | 3. the 1900 founding, off by default |
| 384 | 4. what a new building comes with |
| 520 | the fixtures of 5 to 12 |
| 575 | 5. the order ranked with the buildings |
| 669 | 6. the money and the goods |
| 753 | 7. the work |
| 861 | 8. one at a time |
| 896 | 9. the depot's trucks |
| 929 | 10. the player's Fit |
| 997 | 11. the save |
| 1065 | 12. a default city orders none |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `FittingCheck.DRIVE` | `EquipmentCheck.DRIVE, TRUCK = EquipmentCheck.TRUCK, TRACTOR = EquipmentCheck....` |  |
| 391 | `FittingCheck.BAKERY_MONTHS` | `60` | The months a pair of Bakeries is given to finish: 36 until R0d, whose 1900 village on 1901's wages (WageLadder) grows more slowly - 1,242 people at month 14 against 1,780 - and so builds more slowly, its second pair s... |
| 408 | `FittingCheck.ORDER_COST` | `{ Double.NaN, Double.NaN }, ORDER_QUOTED = { Double.NaN, Double.NaN }, ORDER_...` | What the last order cost, by city: [the city founded the 1900 way, the default one]; what the model quoted for it just before (Game.quoteBuildOn(), which buildStack() charges); the quote's sticker, the template's whol... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 63 | `static PrintStream out` |  |
| 64 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 1028 | **type** `public class FittingCheck` | Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city founded the 1900 way, what a building that finishes comes with, and the fi... |
| 68 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 73 | 1 | `static void check(String label, boolean ok)` |  |
| 76 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance (and an absolute floor for a figure that is nothing). |
| 81 | 5 | `static void quietly(Runnable r)` |  |
| 88 | 1 | `static void step(Game g, int months)` | One press of the clock, as the player's button makes it (toggleNextMonth(): the auto-builder's pass first), quietly. |
| 90 | 1 | `static BuildingsStacks stack(Game g, String template)` |  |
| 93 | 7 | `static String shares(Game g)` | Every sector's steam share, the farms' horse share and the carts', in one line: what a city rests in. |
| 102 | 7 | `static String firstCashDifference(Game a, Game b)` | The first sector whose cash differs between two cities to the bit, or null. |
| 110 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. the shares at rest (lines 136-202)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 64 | `static void sectionOne()` |  |

### 2. cleared on a new game and on a load (lines 203-249)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 44 | `static void sectionTwo()` |  |

### 3. the 1900 founding, off by default (lines 250-383)

| line | len | member | says |
|---:|---:|---|---|
| 253 | 22 | `static Game village(String label, Founding founding, boolean literals, boolean depots)` | A village the way the playtest places one after newGame(): roads, homes, groceries, farms, bakeries, power and water, placed without construction (and, if asked, depots). |
| 277 | 15 | `static String unfittedWholly(Game g, int[] counted)` | Whether every piece of every template on every stack is unfitted for the whole stack, and no stack without a piece has a count; the first fault, or null. |
| 293 | 90 | `static void sectionThree() throws Exception` |  |

### 4. what a new building comes with (lines 384-519)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 8 | `static int pressUntilOpen(Game g, BuildingsStacks s, int most)` | Presses the clock until nothing is on site on this stack (at most `most` months); how many buildings opened over them. |
| 412 | 10 | `static Game builtVillage(String label, Founding founding)` | A village on the goods' literals (the moved prices hold through the months), the builders' depots standing and the sectors that would build on their own held. |
| 423 | 78 | `static void newBuildings(String label, boolean unfittedCity, int which)` |  |
| 502 | 17 | `static void sectionFour()` |  |

### the fixtures of 5 to 12 (lines 520-574)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 12 | `static Game fitTown(String label, int unfitted)` | A default-founded village on the goods' literals (a moved price holds through the months) with the builders' depots standing, at least `unfitted` Bakeries standing and that many lacking the electric drive, and a month... |
| 541 | 3 | `static void holdAll(Game g)` | Holds every sector: none asks its plan or fits anything in the round, so that the fixture's only orders are the ones the harness places. |
| 546 | 3 | `static double breakEven(Game g, BuildingsStacks bakery)` | The coal price at which the drive on this stack's building just pays, from the verdict's own figures (EquipmentCheck 9's way): the stack must have a building unfitted. |
| 551 | 3 | `static int afterRepairs(Game g)` | The builders' month after the repairs, before the fittings take theirs: what getBuildingOutput() reads with no job on site. |
| 556 | 5 | `static double expectedCost(Game g, Equipment piece)` | What the quote of a piece's goods at the city's market comes to, plus its work at the builders' price a point: what ordering it should cost. |
| 563 | 11 | `static double[] buildersDrew(Game g, Good good, int from)` | What the market recorded of the builders' draws of a good since `from` trades ago: {units, cost}. |

### 5. the order ranked with the buildings (lines 575-668)

| line | len | member | says |
|---:|---:|---|---|
| 577 | 91 | `static void sectionFive()` |  |

### 6. the money and the goods (lines 669-752)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 81 | `static void sectionSix()` |  |

### 7. the work (lines 753-860)

| line | len | member | says |
|---:|---:|---|---|
| 755 | 105 | `static void sectionSeven()` |  |

### 8. one at a time (lines 861-895)

| line | len | member | says |
|---:|---:|---|---|
| 863 | 32 | `static void sectionEight()` |  |

### 9. the depot's trucks (lines 896-928)

| line | len | member | says |
|---:|---:|---|---|
| 898 | 30 | `static void sectionNine()` |  |

### 10. the player's Fit (lines 929-996)

| line | len | member | says |
|---:|---:|---|---|
| 931 | 65 | `static void sectionTen()` |  |

### 11. the save (lines 997-1064)

| line | len | member | says |
|---:|---:|---|---|
| 1000 | 8 | `static String stateLine(Game g)` | Every sector's cash and the treasury, the counts and the jobs, as one line: two cities are alike when their lines are equal. |
| 1009 | 55 | `static void sectionEleven() throws Exception` |  |

### 12. a default city orders none (lines 1065-1087)

| line | len | member | says |
|---:|---:|---|---|
| 1067 | 20 | `static void sectionTwelve()` |  |

