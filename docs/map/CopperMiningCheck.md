# CopperMiningCheck.java - 525 lines · 13 methods · 21 constants · harnesses

`ham/citybuildersim/CopperMiningCheck.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> Copper Mining, checked (0.7.133, batch I8; docs/studies/spec-materials.md
> 2.1, 2.3, 2.4, 4 and 5's I8 row; the study's - materials-chains-research.md -
> 1.3, 1.4, 4 and 8): the fourth materials chain's digging industry, on the end
> of Sectors.KEYS after Cement & Concrete.
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
> CoalMiningCheck, I7's QuarryingCheck), and copper digs its two types into
> two different goods: a sulphide field's ore floated into concentrate, a
> flow good dug as the month makes it, and an oxide field's leached into
> cathode, a stockable good dug at the month's end. CopperRefiningCheck
> keeps what the processing side adds.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: copper's two keys on Sectors.KEYS after
>      Cement & Concrete (Jerus's Q21: iron, coal, stone, copper, wood; wood's
>      after them since 0.7.138), Copper Mining first; an ExtractionSector on copper
>      making concentrate and cathode and using acid; and what is as long as
>      the keys grew with them. Resource.COPPER priced as cathode, a sulphide
>      field's good concentrate and an oxide field's cathode, 1/100 of
>      cathode's export price in the ground; copper's seven goods at the
>      study's 4.2 prices, freight three eighths of each wedge, cathode the
>      one stockable good, on NationalAccounts.HELD's end.
> 
>   2. THE THREE ROWS ARE THE STUDY'S (ids 121 to 123, at parity): cost,
>      posts, draw, what each makes, its site (type, sites and dig), ground,
>      road load, stock and consumables; and their four cases, A to D, the
>      study's 4.4 table to its rounding. The study's 4.3 gives no running
>      cost, so each row's is backed out of its own table - the middle of the
>      range on which all four returns round to it (STUDY_RUNS) - and the
>      consumables are that less wages, power and water.
> 
>   3. THE TYPES AND THE FIT: copper's fields are sulphide 80% and oxide 20%
>      (I1's draw, within two points on the default world); a Copper Mine &
>      Mill stands on one sulphide site, the Large Copper Mine on eight of
>      one sulphide field, the leach plant only on oxide (fits()).
> 
>   4. A SULPHIDE MINE MAKES CONCENTRATE, DIGGING ITS OWN TYPE: the month's
>      concentrate its nameplate at the rate, the sulphide ground giving up
>      CONC_DIG a tonne as it is made, the oxide untouched, W exact by type.
> 
>   5. AN OXIDE FIELD'S LEACH PLANT MAKES CATHODE: the month's cathode is the
>      lift - its nameplate at the rate, held to the oxide ground - what the
>      market is priced on and the month makes; at the month's end the oxide
>      gives up LEACH_DIG a tonne, W exact; it bids for its acid; a type
>      running short holds it to what is left, and worked out it is spare.
> 
>   6. THE PLAN: ExtractionSector's - the best earnings on the whole cost of
>      the buildings that fit, their freight in it.
> 
>   7. A PLAYED MONTH: a mine and mill and a leach plant standing, a month
>      on - concentrate shipped, cathode made, acid bought, the ground given
>      up by type - and the audit closes.
> 
> Every fixture causes its condition: a town founded on the oxide field
> nearest the default site (oxide the rarer type), its sulphide ground by
> fiat (the first type), the buildings placed by hand.

**Uses:** [Good](Good.md) (61), [CopperMining](CopperMining.md) (31), [World](World.md) (14), [Deposit](Deposit.md) (13), [Sectors](Sectors.md) (11), [BuildingManager](BuildingManager.md) (10), [Game](Game.md) (8), [Traffic](Traffic.md) (7), [Ports](Ports.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (7), [LandManager](LandManager.md) (7), [Equity](Equity.md) (5), [CityLand](CityLand.md) (5), [LandCheck](LandCheck.md) (5), [MoneyAudit](MoneyAudit.md) (4), [SpreadPlanner](SpreadPlanner.md) (4), [Resource](Resource.md) (3), [Sector](Sector.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [BuildingType](BuildingType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [LandMarket](LandMarket.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [JobType](JobType.md) (2), [Founding](Founding.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [Markets](Markets.md) (1), [ExtractionSector](ExtractionSector.md) (1), [Bank](Bank.md) (1), [NationalAccounts](NationalAccounts.md) (1)... and 3 more

**Used by (2):** [CopperRefiningCheck](CopperRefiningCheck.md), [IndustryViewCheck](IndustryViewCheck.md)

## Sections

| line | section |
|---:|---|
| 112 | 1. the sector on the registry |
| 190 | 2. the three rows against the study |
| 286 | 3. the types and the fit |
| 386 | 4. a sulphide mine makes concentrate |
| 424 | 5. an oxide field's leach plant makes cathode |
| 458 | 6. the plan |
| 488 | 7. a played month |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `CopperMiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 76 | `CopperMiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 108 | `CopperMiningCheck.CU` | `Resource.COPPER` | Copper, the resource its mines and leach plants dig. |
| 110 | `CopperMiningCheck.SULPHIDE` | `Deposit.FieldType.SULPHIDE, OXIDE = Deposit.FieldType.OXIDE` | Copper's two types of field (Resource.typesOf()): sulphide, floated at the mine, and oxide, leached. |
| 115 | `CopperMiningCheck.GOODS` | `{ Good.COPPER_CONC, Good.ANODE, Good.COPPER, Good.COPPER_SCRAP, Good.COPPER_R...` | Copper's goods, the study's 4.2: import and export price, D$ thousands a tonne. |
| 117 | `CopperMiningCheck.STUDY_IMPORT` | `{ 2.800, 10.150, 10.500, 8.700, 10.750,.160, 17.000 }` | ...each one's import price, D$ thousands a tonne (the study's 4.2)... |
| 119 | `CopperMiningCheck.STUDY_EXPORT` | `{ 2.575, 9.650, 10.000, 7.300, 10.250,.070, 13.000 }` | ...and its export price. |
| 122 | `CopperMiningCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 193 | `CopperMiningCheck.ROWS` | `{ CopperMining.MINE_AND_MILL, CopperMining.LARGE_MINE, CopperMining.LEACH_PLA...` | Copper Mining's rows, by their permanent ids, in the study's 4.3 order. |
| 196 | `CopperMiningCheck.STUDY_COSTS` | `{ 160_000, 2_000_000, 250_000 }` | The study's 4.3: each one's cost, D$ thousands all in. |
| 199 | `CopperMiningCheck.STUDY_POSTS` | `{ 120, 450, 150 }` | ...its posts. |
| 202 | `CopperMiningCheck.STUDY_KW` | `{ 5_500, 60_000, 6_000 }` | ...its draw, kW. |
| 205 | `CopperMiningCheck.STUDY_MAKES` | `{ 2_200, 26_400, 1_000 }` | ...what it makes a month, t: concentrate, concentrate, cathode. |
| 208 | `CopperMiningCheck.STUDY_ACID` | `3_500` | ...the acid the leach plant takes, t a month. |
| 211 | `CopperMiningCheck.STUDY_SITES` | `{ 1, 8, 1 }` | ...the sites each stands on, and of which type (its 4.3 and 1.3: "an oxide copper field takes a leach plant"). |
| 213 | `CopperMiningCheck.STUDY_TYPES` | `{ SULPHIDE, SULPHIDE, OXIDE }` | ...and the type of field each stands on. |
| 216 | `CopperMiningCheck.STUDY_TABLE` | `{ { 2.6, 2.6, 2.9, 2.9 }, { 2.8, 2.8, 3.1, 3.1 }, { 3.1, 3.2, 3.4, 3.3 } }` | The study's 4.4 table, % of the cost a month, cases A to D. |
| 219 | `CopperMiningCheck.STUDY_RUNS` | `{ 1_512.5, 11_950, 1_722.5 }` | Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table. |
| 222 | `CopperMiningCheck.SINCE` | `{ 1900, 1906, 1968 }` | The year each first existed: 1900 - standing before the start - but the Large Copper Mine, the open-pit porphyry mine at Bingham Canyon in 1906 (research-1900.md 1.2, PE8), and the leach plant, SX-EW at Bluebird in 19... |
| 225 | `CopperMiningCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 289 | `CopperMiningCheck.TYPE_WITHIN` | `.02` | How far copper's sulphide share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `static int fails` |  |
| 361 | `static String lastSearch` | The last town's field, for its report. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 70 | 456 | **type** `public class CopperMiningCheck` | Copper Mining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 4 and 5's I8 row; the study's - materials-chains-research.md - 1.3, 1.4, 4 and 8): the fourth materials chain's digging industry... |
| 78 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 83 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 88 | 5 | `static void quietly(Runnable r)` |  |
| 94 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the sector on the registry (lines 112-189)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 65 | `static void sector()` |  |

### 2. the three rows against the study (lines 190-285)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 58 | `static void rows()` |  |

### 3. the types and the fit (lines 286-385)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 33 | `static void typesAndFit()` |  |
| 333 | 26 | `static Game copperTown(String label, int sulphideSites)` | A town to dig copper in: the Wealthy preset's cash and the founding default's works, founded on the oxide field nearest the default site whose centre plot is dry ground - so it holds an oxide field whole (CityLand.fou... |
| 364 | 21 | `static Deposit nearest(World w, Deposit.FieldType type)` | The copper field of a type nearest a world's founding site whose centre plot is dry ground, cell ring by cell ring. |

### 4. a sulphide mine makes concentrate (lines 386-423)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 35 | `static void sulphide()` |  |

### 5. an oxide field's leach plant makes cathode (lines 424-457)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 31 | `static void oxide()` |  |

### 6. the plan (lines 458-487)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 27 | `static void plan()` |  |

### 7. a played month (lines 488-525)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 35 | `static void playedMonth()` |  |

