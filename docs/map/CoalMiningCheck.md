# CoalMiningCheck.java - 624 lines · 15 methods · 17 constants · harnesses

`ham/citybuildersim/CoalMiningCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Coal Mining, checked (0.7.126, batch I6; docs/studies/spec-materials.md 2.1,
> 2.4, 2.10, 4 and 5's I6 row; the study's - materials-chains-research.md -
> 1.3, 1.4 and 3): the second materials chain's digging industry, on
> Sectors.KEYS after the refinery (its end when it came).
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md, "adding
> things": a <Name>Check modelled on RestaurantsCheck), and coal brings the
> one rule MaterialsCheck's shared machinery does not have: a coking mine's
> coal is raw until a preparation plant washes it, a stream inside the
> sector and not a good, so its two coals come off one flow. MaterialsCheck
> keeps the machinery every chain shares; this keeps coal's own.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE FLOW (CoalMining.flow(), pure): the study's 3.3 - 500,000 t of
>      raw coal through a plant washing 500,000 is 275,000 t of coking coal,
>      100,000 of thermal and 125,000 of spoil, to the tonne; every tonne in
>      comes out; raw past the plants' wash sells as thermal tonne for tonne,
>      and a wash past the raw washes only the raw.
> 
>   2. THE SECTOR ON THE REGISTRY: Sectors.KEYS has Coal Mining after the
>      refinery, eighteenth - its end when it came, stone's two and copper's
>      two after it since (Jerus's Q21: iron, coal, stone, copper, wood); an ExtractionSector on coal making both coals on diesel; and
>      what is as long as the keys grew with them - Equity's companies (the
>      keys and the bank), the households' share block, the audit's pools,
>      the bank's year of sheets. Resource.COAL is priced: THERMAL_COAL for
>      both its types, 1/200 of thermal coal's export price in the ground.
> 
>   3. THE FOUR ROWS ARE THE STUDY'S (ids 111 to 114, at parity): their
>      cost, posts, draw, coal, wash, sites, ground and consumables; and
>      their four cases, A to D (SpreadPlanner.Case). The study gives coal's
>      returns in words, not a table (its 3.4), so the comparison is worked
>      from its own figures: each case's coal at the study's 3.2 prices, less
>      the study's 3.3 running cost (its diesel in it at D$0.60), over the
>      study's cost - and the model's four cases (caseEarns() for a mine; a
>      plant's on the flow's increment) are those to its rounding. Then the
>      returns the study states: a surface mine 3.3% and a large one 3.6%
>      exporting, a longwall alone 0.8%, the pair 2.4% (two longwalls to the
>      plant's 500,000 t), the plant alone 19%.
> 
>   4. A LONGWALL ALONE SELLS ITS RAW COAL AS THERMAL, TONNE FOR TONNE: the
>      sector's capacity is the flow's - an Underground Coal Mine standing
>      with no plant makes 250,000 t of thermal coal and no coking; with a
>      plant 137,500 t of coking and 50,000 of thermal; a plant on site is
>      the pipeline's difference.
> 
>   5. THE TYPES AND THE FIT: coal's fields are thermal or coking at I1's
>      shares (85% and 15%, within two points of the fields on the default
>      world), on Earth leaning to coking three to one in fold belts and one
>      to two on shields (E3); a surface mine stands only on thermal, the
>      large one on four free sites of one thermal field, the longwall only
>      on coking (fits()).
> 
>   6. THE DIG BY TYPE: the month's coal is the lift - each kind's nameplate
>      at the rate, held to its type's ground - what the market is priced on
>      and the month makes; at the month's end each type's ground gives up
>      that coal at its dig (1/.95 at the surface, 1/.80 underground), E by
>      type rising by it and W exact; a type running short holds its mines
>      to what is left, and a mine over a worked-out type is spare.
> 
> ... (11 more lines in the source)

**Uses:** [Good](Good.md) (70), [CoalMining](CoalMining.md) (53), [Resource](Resource.md) (36), [Deposit](Deposit.md) (24), [World](World.md) (14), [Geology](Geology.md) (14), [Sectors](Sectors.md) (13), [BuildingManager](BuildingManager.md) (13), [Game](Game.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [LandManager](LandManager.md) (7), [Equity](Equity.md) (6), [CityLand](CityLand.md) (5), [SpreadPlanner](SpreadPlanner.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [MoneyAudit](MoneyAudit.md) (4), [LandCheck](LandCheck.md) (3), [Markets](Markets.md) (2), [BuildingType](BuildingType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [LandMarket](LandMarket.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [JobType](JobType.md) (2), [Founding](Founding.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [ExtractionSector](ExtractionSector.md) (1), [Bank](Bank.md) (1), [PayTier](PayTier.md) (1), [GameFiles](GameFiles.md) (1), [Sector](Sector.md) (1)

**Used by (2):** [IndustryViewCheck](IndustryViewCheck.md), [LandCheck](LandCheck.md)

## Sections

| line | section |
|---:|---|
| 124 | 1. the flow |
| 151 | 2. the sector on the registry, after the refinery |
| 201 | 3. the four rows against the study |
| 349 | 4. a longwall alone sells its raw coal as thermal |
| 376 | 5. the types and the fit |
| 479 | 6. the dig by type |
| 528 | 7. the plan |
| 590 | 8. a played month |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `CoalMiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 92 | `CoalMiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 127 | `CoalMiningCheck.STUDY_RAW` | `500_000, STUDY_COKING = 275_000, STUDY_THERMAL = 100_000, STUDY_SPOIL = 125_000` | The study's 3.3: a plant's 500,000 t of raw coal, and what comes of it. |
| 204 | `CoalMiningCheck.ROWS` | `{ CoalMining.SURFACE_MINE, CoalMining.LARGE_SURFACE_MINE, CoalMining.UNDERGRO...` | Coal Mining's rows, by their permanent ids, in the study's 3.3 order. |
| 208 | `CoalMiningCheck.STUDY_COSTS` | `{ 120_000, 950_000, 800_000, 150_000 }` | The study's 3.3: each one's cost, D$ thousands all in. |
| 211 | `CoalMiningCheck.STUDY_POSTS` | `{ 80, 350, 400, 50 }` | ...its posts. |
| 214 | `CoalMiningCheck.STUDY_KW` | `{ 800, 6_000, 29_000, 4_000 }` | ...its draw, kW. |
| 217 | `CoalMiningCheck.STUDY_RUNS` | `{ 1_070, 5_570, 5_770, 774 }` | ...its running cost a month, D$ thousands (the surface mine's "with 230,000 L diesel" in it, at the study's D$0.60). |
| 220 | `CoalMiningCheck.STUDY_MAKES` | `{ 100_000, 800_000, 250_000, 500_000 }` | ...the coal each mine makes a month, t (the longwall's raw), and the plant's wash. |
| 223 | `CoalMiningCheck.STUDY_SITES` | `{ 1, 4, 1, 0 }` | ...the sites each stands on, and of which type (the study's 1.3 and 3.3; the plant none). |
| 225 | `CoalMiningCheck.STUDY_TYPES` | `{ Deposit.FieldType.THERMAL, Deposit.FieldType.THERMAL, Deposit.FieldType.COK...` | ...and the type of field each stands on (null for the plant, which stands on none). |
| 228 | `CoalMiningCheck.SURFACE_DIG` | `1 /.95, UNDERGROUND_DIG = 1 /.80` | ...the tonnes of seam a tonne of coal takes: 95% of the seam recovered at the surface, 80% by a longwall (the study's 3.1; spec 2.4). |
| 231 | `CoalMiningCheck.STUDY_DIESEL_LITRES` | `230_000, STUDY_DIESEL =.0006` | ...the surface mine's diesel, litres a month (the study's 3.3), and its price in the study's runs, D$ thousands a litre (its 1.5). |
| 234 | `CoalMiningCheck.STUDY_PLANT_HECTARES` | `19` | ...the plant's ground, hectares (the study's 3.3). |
| 237 | `CoalMiningCheck.STUDY_ROUNDING` | `.05, STUDY_ROUNDING_POINT =.5` | The study's rounding: its returns to a tenth of a point, the plant's 19% to the point. |
| 240 | `CoalMiningCheck.STUDY_SURFACE` | `3.3, STUDY_LARGE = 3.6, STUDY_ALONE =.8, STUDY_PAIR = 2.4, STUDY_PLANT = 19` | The returns the study's 3.4 states, % of the cost a month: a surface mine and a large one exporting, a longwall alone, the pair, the plant alone. |
| 379 | `CoalMiningCheck.TYPE_WITHIN` | `.02` | How far coal's thermal share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 539 | **type** `public class CoalMiningCheck` | Coal Mining, checked (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.4, 2.10, 4 and 5's I6 row; the study's - materials-chains-research.md - 1.3, 1.4 and 3): the second materials chain's digging industry, on... |
| 94 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 99 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 104 | 5 | `static void quietly(Runnable r)` |  |
| 110 | 13 | `public static void main(String[] args) throws Exception` |  |

### 1. the flow (lines 124-150)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 21 | `static void flow()` |  |

### 2. the sector on the registry, after the refinery (lines 151-200)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 47 | `static void sector()` |  |

### 3. the four rows against the study (lines 201-348)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 95 | `static void rows()` |  |
| 339 | 9 | `static Map<Good, Double> flowIncrement(double surface, double raw, double washing, double wash)` | What `wash` t of plants add to a flow of `surface` thermal and `raw` raw coal already washing `washing`: {good: tonnes}. |

### 4. a longwall alone sells its raw coal as thermal (lines 349-375)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 24 | `static void rawAsThermal()` |  |

### 5. the types and the fit (lines 376-478)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 40 | `static void typesAndFit() throws Exception` |  |
| 430 | 25 | `static Game coalTown(String label, int thermalSites)` | A town to dig coal in: MaterialsCheck's (the Wealthy preset's cash and ground, homes, shops, power and water), its land founded instead on the coking coal field nearest the default founding site on dry ground - so it ... |
| 457 | 21 | `static Deposit nearestCoking(World w)` | The coking coal field nearest a world's founding site whose centre plot is dry ground, cell ring by cell ring. |

### 6. the dig by type (lines 479-527)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 46 | `static void dig()` |  |

### 7. the plan (lines 528-589)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 59 | `static void plan()` |  |

### 8. a played month (lines 590-624)

| line | len | member | says |
|---:|---:|---|---|
| 592 | 32 | `static void playedMonth()` |  |

