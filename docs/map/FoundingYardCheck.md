# FoundingYardCheck.java - 599 lines · 26 methods · 5 constants · harnesses

`ham/citybuildersim/FoundingYardCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The founding yard (PT1-M4, 2026-10-12): the works department's free 400
> points a month are retired and every city founds with one Builders' Yard
> standing, staffed and paid like any other, so the builders earn only for the
> work their own people do - and keep the one building that builds, which they
> cannot sell.
> 
> WHY. PT1's month 183 was the finding: $377k of a month at a 72% margin with
> 0 workers in the sector's posts. BuildingManager.BASE_CONSTRUCTION was 400
> points of building work with no posts behind them, paid to Construction as
> revenue. Jerus: "not meant ... ya sure fix, perhaps a starting depot or
> something instead". The constant is gone; Game.run() and newGame() place the
> yard (Game.foundingYard()); the builders' last building that builds is not
> sold by either of the retirement rules nor bought out (Construction.
> mayRetire(), Game.demolishable()). A yard and not the Construction Depot,
> because the depot's 50 posts were a founding village's whole workforce: idle,
> its builders laid 37 of them off and every planner counted them as spoken for
> (Sector.staffing()), so a founded village stood at 146 people with no shop
> in twenty years, where main's opened its first at month 13. The yard is the
> depot at two fifths (20 posts, 160 points); section 6 is the proof.
> 
> What this has to prove:
>   1. THE FOUNDING STANDS ONE YARD, through both doors (run() and newGame()):
>      standing, none on site, no depot, the builders', the capacity its
>      nameplate and no other points, and the land office holds its ground.
>   2. NO YARD, NO POINTS: with the yard retired by fiat the builders'
>      capacity and output are 0, and houses ordered owe what they owed, are
>      not finished and are not recognised, month after month.
>   3. MONTH 1 IS THE FIRST-MONTH CONVENTION, and month 2 on is worked by the
>      crews the city has: the fill reads 1 until the first labour market,
>      the yard works month 1 at its nameplate; from then the output is the
>      nameplate x the posts offered share x the fill x the road and work
>      ratios; and a founded city saved before its first month loads with one
>      yard, its capacity and its ground.
>   4. A FOUNDING TOWN BUILDS, AND EARNS ONLY FOR WORK ITS CREWS DO: the
>      founding village stands within TOWN_MONTHS; once crews come the sites
>      get work every month while anything is owed (T1-C's trap); a month
>      whose crews are 0 has no output and recognises nothing; the builders'
>      payroll is positive.
>   5. THE LAST BUILDING THAT BUILDS IS NEVER SOLD: neither the
>      spare-capacity rule nor the distress rule sells the yard, nor does the
>      city buy it out; with a Construction Depot standing beside it, both
>      sell one of the two and leave the other.
>   6. A FOUNDED VILLAGE HAS HANDS FOR ITS SHOPS, WHERE A DEPOT WOULD HAVE HELD
>      THEM: a hands-off village founded on the yard opens its first shop
>      within FIRST_SHOP_MONTHS, its builders idle at the core crew and holding
>      posts back; its twin, founded the same way with a Construction Depot
>      in the yard's place, holds more posts back in its idle months and opens
>      none in the same months.
>   7. THE LAST BUILDER IS NOT BOUGHT OUT IN ONE WINDOW: a yard or depot the
>      city has bought out to demolish stands until the next month starts,
>      and counted as standing it let the city buy out the depot and then the
>      yard in the same window and leave no builders. With a yard and a
>      depot standing, in either order the first buy-out is taken; in the
>      same window the other is not demolishable, its buy-out is refused and
>      pays nothing, and the builders may not retire it; after the month the
>      first is gone and the other stands, the builders' capacity its
>      nameplate.
>   8. MONEY IS CONSERVED: the money audit every month played here.
> 
> Every fixture causes its condition and says so in an assertion labelled
> ... (1 more lines in the source)

**Uses:** [Game](Game.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (10), [Construction](Construction.md) (10), [BuildingManager](BuildingManager.md) (10), [Good](Good.md) (6), [Founding](Founding.md) (6), [JobType](JobType.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (1), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 210 | 1. THE FOUNDING STANDS ONE YARD |
| 239 | 2. NO YARD, NO POINTS |
| 271 | 3. MONTH 1, AND MONTH 2 ON |
| 351 | 4. A FOUNDING TOWN BUILDS |
| 424 | 5. THE LAST BUILDING THAT BUILDS IS NEVER SOLD |
| 464 | 6. HANDS FOR ITS SHOPS, WHERE A DEPOT WOULD HAVE HELD THEM |
| 535 | 7. THE LAST BUILDER IS NOT BOUGHT OUT IN ONE WINDOW |
| 589 | 8. MONEY IS CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 108 | `FoundingYardCheck.YARD` | `Game.FOUNDING_YARD` | The builders' yard: what every city is founded with. |
| 111 | `FoundingYardCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the village's own order, and the second builders' building of sections 5, 6 and 7. |
| 114 | `FoundingYardCheck.BANK` | `"Commercial Bank"` | The founding bank, whose ground the yard's is added to. |
| 117 | `FoundingYardCheck.NO_YARD_MONTHS` | `3` | The months a city with no yard is played: long enough for a month's work to show it is not done. |
| 135 | `FoundingYardCheck.FIRST_SHOP_MONTHS` | `36` | The months a hands-off founded village is given to open its first shop: three years. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `static int fails` |  |
| 75 | `static PrintStream out` |  |
| 76 | `static PrintStream quiet` |  |
| 138 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 139 | `static int monthsAudited` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 528 | **type** `public class FoundingYardCheck` | The founding yard (PT1-M4, 2026-10-12): the works department's free 400 points a month are retired and every city founds with one Builders' Yard standing, staffed and paid like any other, so the builders earn only for... |
| 78 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 83 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 89 | 5 | `static void check(String label, long actual, long expected)` |  |
| 95 | 5 | `static void quietly(Runnable r)` |  |
| 101 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 124 | 5 | `static int townMonths(Game g)` | The months the founding village is given to stand: the two founding months nobody is there to work, then its points at the core crew alone (Construction.IDLE_PAYROLL_FLOOR of the yard's nameplate a month, the least th... |
| 142 | 7 | `static void month(Game g)` | One month, audited. |
| 151 | 1 | **type** `record City(Game game, GameFiles files)` | A city and the folder it is saved into. |
| 154 | 6 | `static City founded(Path root, String name, boolean byNewGame)` | A new city in a folder of its own, founded through run() or through newGame(). |
| 162 | 5 | `static double villagePoints(Game g)` | The founding village's points: each building's construction points times its count. |
| 169 | 9 | `static boolean orderVillage(Game g)` | Orders the founding village, line by line, the treasury paying; returns whether every order was taken. |
| 179 | 4 | `static int villageCount(String name)` |  |
| 185 | 5 | `static long outputFormula(Game g)` | The output the model says the builders give now: nameplate x posts offered share x fill x road ratio x work ratio. |
| 191 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. THE FOUNDING STANDS ONE YARD (lines 210-238)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 26 | `static void standing(Path root)` |  |

### 2. NO YARD, NO POINTS (lines 239-270)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 29 | `static void noYard(Path root)` |  |

### 3. MONTH 1, AND MONTH 2 ON (lines 271-350)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 77 | `static void monthOne(Path root)` |  |

### 4. A FOUNDING TOWN BUILDS (lines 351-423)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 62 | `static void town(Path root)` |  |
| 417 | 6 | `static boolean villageStands(BuildingManager bm)` | Whether every building of the founding village stands (the depot line is the village's own, beside the founding yard). |

### 5. THE LAST BUILDING THAT BUILDS IS NEVER SOLD (lines 424-463)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 37 | `static void lastYard(Path root)` |  |

### 6. HANDS FOR ITS SHOPS, WHERE A DEPOT WOULD HAVE HELD THEM (lines 464-534)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 3 | `static int shops(BuildingManager bm)` | How many of the village's two small food shops stand. |
| 472 | 3 | **type** `record Village(Game game, int firstShop, int idleMonths, long withheldInIdle, long mostWithheld, long people)` | One hands-off village, played FIRST_SHOP_MONTHS months: when its first shop stood, and what its builders held back while idle. |
| 473 | 1 | `double meanWithheld()` _(in FoundingYardCheck.Village)_ |  |
| 482 | 31 | `static Village playHandsOff(Path root, String name, boolean depotInstead)` | A hands-off founded city (run(), no orders, Founding.defaults()), its yard retired and a Construction Depot placed in its stead before month 1 when depotInstead, played FIRST_SHOP_MONTHS months. |
| 514 | 20 | `static void handsForShops(Path root)` |  |

### 7. THE LAST BUILDER IS NOT BOUGHT OUT IN ONE WINDOW (lines 535-588)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 5 | `static void lastBuilderWindow(Path root)` | A yard or depot the city has bought out to demolish stands until the next month starts (Game.closeDemolished()), so a count that took it for standing left the city a window in which it could buy out the depot and then... |
| 550 | 38 | `static void buyOutWindow(Path root, String name, boolean depotFirst)` | One order: the first building is bought out, then the other, the builders' last, is tried in the same window. |

### 8. MONEY IS CONSERVED (lines 589-599)

| line | len | member | says |
|---:|---:|---|---|
| 591 | 8 | `static void conserved()` |  |

