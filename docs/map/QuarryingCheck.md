# QuarryingCheck.java - 638 lines · 13 methods · 18 constants · harnesses

`ham/citybuildersim/QuarryingCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Quarrying, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1,
> 2.3, 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-research.md
> - 1.4, 6 and 8): the third materials chain's digging industry, on
> Sectors.KEYS after Coal Mining (its end when it came).
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md, "adding
> things"; I6's CoalMiningCheck the same), and stone brings the one market
> rule new to the game (the study's 6.1, Jerus's Q17): a limestone quarry's
> limestone no buyer here takes sells as aggregate, one way - and with it
> the packing that says which quarry is on which field.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE CARRY: a limestone quarry with no buyer sells its tonnes as
>      aggregate, tonne for tonne - nothing booked on limestone, the tonnes
>      shipped at aggregate's export price - the ground it drew still
>      limestone's, E rising on limestone alone and W exact; with a buyer
>      (a Lime Kiln) limestone clears first, the kiln takes its limestone
>      at home and only the rest is carried.
> 
>   2. THE SECTOR ON THE REGISTRY: Sectors.KEYS has Quarrying and Cement &
>      Concrete after Coal Mining (Q21's order) - its end when they came,
>      copper's two after them since 0.7.133; an
>      ExtractionSector on stone making limestone, aggregate and blocks;
>      what grew with the keys - Equity's companies, the households' share
>      block, the audit's pools, the bank's year of sheets. Resource.STONE
>      is priced: AGGREGATE its good, LIMESTONE or AGGREGATE by type, 1/400
>      of aggregate's export price in the ground - in an offer's price, and
>      no longer bare ground. The three goods are the study's 6.3.
> 
>   3. THE THREE ROWS ARE THE STUDY'S (ids 115 to 117, at parity): cost,
>      posts, draw, stone, sites, type, dig, ground, consumables, and their
>      four cases. The study gives stone's returns in words (its 6.5), so
>      the comparison is worked from its own figures - each case's stone at
>      the 6.3 prices, less the 6.4 running cost, over the 6.4 cost - and the
>      model's cases (SpreadPlanner.caseEarns()) are those to its rounding;
>      then the returns it states: 1.6-1.7% exporting at D$10, about 10%
>      replacing imports at D$40.
> 
>   4. A HARD-ROCK QUARRY MAKES AGGREGATE ONLY, and the Dimension Stone
>      Quarry stands only on hard rock.
> 
>   5. THE PACKING: quarries on ANY go onto limestone first (fits(),
>      LandManager.packed()) - a works on a type before them, the larger
>      first - and a quarry's sites on limestone make limestone, on hard rock
>      aggregate (getCapacity(), getPipeline(), madeBy()); the order gate
>      (Game.hasDepositFor()) agrees.
> 
>   6. THE DIG BY TYPE: each good comes out of its type's ground at its dig
>      (blocks three tonnes of rock a tonne), W exact; with limestone worked
>      out a quarry on ANY digs hard rock; the blocks are spare once hard
>      rock is.
> 
>   7. THE PLAN: one more quarry's estimate is its increment to the packing,
>      its limestone past the city's room for it valued as aggregate (the
>      carry); the plan orders the best on the whole cost of those that
>      fit, staff and pay - or says why none.
> 
>   8. A PLAYED MONTH closes the money audit.
> 
> ... (3 more lines in the source)

**Uses:** [Good](Good.md) (135), [Quarrying](Quarrying.md) (34), [Sectors](Sectors.md) (15), [Deposit](Deposit.md) (13), [BuildingManager](BuildingManager.md) (13), [Game](Game.md) (10), [LandManager](LandManager.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (9), [World](World.md) (9), [Equity](Equity.md) (7), [MoneyAudit](MoneyAudit.md) (5), [CityLand](CityLand.md) (5), [Sector](Sector.md) (4), [LandCheck](LandCheck.md) (4), [SpreadPlanner](SpreadPlanner.md) (4), [Resource](Resource.md) (3), [Markets](Markets.md) (3), [GoodsMarket](GoodsMarket.md) (3), [MaterialsCheck](MaterialsCheck.md) (3), [Traffic](Traffic.md) (3), [Ports](Ports.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [CementConcrete](CementConcrete.md) (2), [BuildingType](BuildingType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [LandMarket](LandMarket.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [JobType](JobType.md) (2), [ExtractionSector](ExtractionSector.md) (1), [Bank](Bank.md) (1)... and 4 more

**Used by (1):** [CementConcreteCheck](CementConcreteCheck.md)

## Sections

| line | section |
|---:|---|
| 121 | 1. the carry |
| 221 | 2. the sector on the registry, after Coal Mining |
| 292 | 3. the three rows against the study |
| 395 | 4. hard rock makes aggregate only |
| 423 | 5. the packing |
| 469 | 6. the dig by type |
| 518 | 7. the plan |
| 560 | the fixture |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `QuarryingCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 85 | `QuarryingCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 117 | `QuarryingCheck.STONE` | `Resource.STONE` | Stone, the resource its quarries dig. |
| 119 | `QuarryingCheck.LIME` | `Deposit.FieldType.LIMESTONE, HARD = Deposit.FieldType.HARD_ROCK` | Stone's two types of field (Resource.typesOf()): limestone, and hard rock. |
| 295 | `QuarryingCheck.ROWS` | `{ Quarrying.QUARRY, Quarrying.LARGE_QUARRY, Quarrying.DIMENSION_STONE_QUARRY }` | Quarrying's rows, by their permanent ids, in the study's 6.4 order. |
| 298 | `QuarryingCheck.STUDY_COSTS` | `{ 15_000, 90_000, 6_000 }` | The study's 6.4: each one's cost, D$ thousands all in. |
| 301 | `QuarryingCheck.STUDY_POSTS` | `{ 22, 125, 17 }` | ...its posts. |
| 304 | `QuarryingCheck.STUDY_RUNS` | `{ 163, 981, 104 }` | ...its running cost a month, D$ thousands. |
| 307 | `QuarryingCheck.STUDY_MAKES` | `{ 40_000, 250_000, 1_000 }` | ...what it makes a month, t: stone, stone, blocks. |
| 310 | `QuarryingCheck.STUDY_SITES` | `{ 1, 3, 1 }` | ...the sites it takes, and of which type (null for either; the study's 6.4). |
| 312 | `QuarryingCheck.STUDY_TYPES` | `{ null, null, HARD }` | ...and the type of field each stands on (null for either). |
| 315 | `QuarryingCheck.BLOCKS_DIG` | `3` | ...and the tonnes of rock a tonne of blocks takes (spec 2.4: "dimension stone 3"); stone is a tonne a tonne. |
| 318 | `QuarryingCheck.CRUSH_KWH` | `1.7` | The research's crushing, kWh a tonne of stone dug: 1.7, the middle of the study's 6.2 "1.6-1.8" [S18]. |
| 321 | `QuarryingCheck.HOURS` | `730` | The hours of a month the study reads its kW at: its 2.2, "3,700 kW for 6,000 t a month is 450 kWh a tonne" - 450 x 6,000 / 3,700, 730 to the hour. |
| 324 | `QuarryingCheck.STUDY_ROUNDING` | `.05, STUDY_ROUNDING_POINT =.5` | The study's rounding: its returns to a tenth of a point, "about 10%" to the point. |
| 327 | `QuarryingCheck.STUDY_EXPORTING_LOW` | `1.6, STUDY_EXPORTING_HIGH = 1.7, STUDY_REPLACING = 10` | The returns the study's 6.5 states, % of the cost a month: the Quarry's and the Large Quarry's exporting at D$10, and replacing imports at D$40. |
| 634 | `QuarryingCheck.FOUND_KM2` | `.3` | The dry ground a fixture town is founded on round its field, km2: the default city's rings (CityLand.found()). |
| 637 | `QuarryingCheck.MAX_RING` | `6` | How many cells round the founding site's the fixture looks for its field in. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |
| 631 | `static String lastSearch` | The last fixture's search, for its report: each field tried, its sites, the town's of the type and of the other. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 560 | **type** `public class QuarryingCheck` | Quarrying, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-research.md - 1.4, 6 and 8): the third materials chain's digging industry, on... |
| 87 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 92 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 97 | 5 | `static void quietly(Runnable r)` |  |
| 103 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the carry (lines 121-220)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 97 | `static void carry()` |  |

### 2. the sector on the registry, after Coal Mining (lines 221-291)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 68 | `static void sector()` |  |

### 3. the three rows against the study (lines 292-394)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 65 | `static void rows()` |  |

### 4. hard rock makes aggregate only (lines 395-422)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 25 | `static void hardRock()` |  |

### 5. the packing (lines 423-468)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 43 | `static void packing()` |  |

### 6. the dig by type (lines 469-517)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 46 | `static void dig()` |  |

### 7. the plan (lines 518-559)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 39 | `static void plan()` |  |

### the fixture (lines 560-638)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 34 | `static Game stoneTown(String label, Deposit.FieldType type, int limestoneSites)` | A town to quarry in: MaterialsCheck's and CoalMiningCheck's (the Wealthy preset's cash and ground, homes, shops, power and water), its land founded instead on the stone field of `type` nearest the default founding sit... |
| 606 | 23 | `static List<Deposit> nearest(World w, Deposit.FieldType type)` | The stone fields of a type whose centre plot is dry ground, nearest a world's founding site first, cell ring by cell ring to MAX_RING; hard rock's of at least four sites, so the packing has room. |

