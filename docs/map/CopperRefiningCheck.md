# CopperRefiningCheck.java - 538 lines · 13 methods · 15 constants · harnesses

`ham/citybuildersim/CopperRefiningCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md
> 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's -
> materials-chains-research.md - 1.2, 4, 7 and 10's Q11 and Q12): copper's
> processing industry, the twenty-second key, and the Wire & Cable Works it feeds in
> Manufacturing.
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I7's
> CementConcreteCheck), and copper's works add three things the other
> chains do not: a co-product another industry buys (the smelter's acid, the
> leach plants'), a chain four works long inside one sector with two
> captive goods, and the city's old copper bought before the market's scrap.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: a ProcessingSector, the twenty-second key,
>      after Copper Mining (Forestry after it since 0.7.138); making cathode, anode, acid and rod of concentrate,
>      anode, cathode and scrap, Iron & Steel's group; anode and cathode
>      captive; cathode its planning good. Manufacturing makes wire and cable
>      of rod, its planning good still fabricated steel.
> 
>   2. THE FIVE ROWS ARE THE STUDY'S (ids 124 to 128, at parity): cost,
>      posts, draw, makes and uses, ground, road load, stock, consumables,
>      the scrap refinery's old copper; and their four cases, the study's 4.4
>      table to its rounding, each row's running cost backed out of its own
>      table (the scrap refinery's from its stated -0.02% in case A); then
>      what the study says of them - the smelter loses on imported
>      concentrate, imported scrap just breaks even, the rod mill's 11% a thin
>      premium on a big tonnage, the wire works built to replace imports and
>      not to export.
> 
>   3. THE CHAIN BY ITS RATIOS: concentrate to anode and acid (0.9 t of acid
>      a tonne of concentrate [K27]), anode to cathode, cathode to rod, rod to
>      wire and cable; and a played chain - a smelter, a refinery and a rod
>      mill standing beside a wire works - its anode and cathode captive at
>      the rate (min of what is made and what is needed), its rod sold at
>      home to the wire works, the rest of the rod and the wire shipped.
> 
>   4. THE SMELTER'S ACID SOLD, THE LEACH PLANT'S BOUGHT: a smelter beside a
>      leach plant makes 29,700 t of acid at the rate, the leach plant buys
>      its 3,500 t of it at home, and the rest ships.
> 
>   5. OLD COPPER: a car scrapped leaves COPPER_A_CAR of copper, a demolished
>      building COPPER_A_MATERIALS_UNIT a unit of its material, each at
>      RECOVERY, in a pool of its own beside the steel's; a Scrap Copper
>      Refinery buys it at PAY_SHARE of COPPER_SCRAP's export price, as much as
>      its intake at the rate and no more than arose, pro rata, the rest to
>      landfill; its bid for scrap the need past it; the households paid into
>      their savings and a selling sector by its other revenue, on the
>      refinery's input line; the audit closes.
> 
>   6. THE PLANNER WEIGHS THE OLD COPPER: one more scrap refinery's scrap is
>      the old copper past what those standing take, at what it pays, and the
>      rest at the market's value; with none arisen, the market's alone, to
>      the bit.
> 
>   7. THE WIRE & CABLE WORKS' ESTIMATE (Manufacturing): the maker's less its
>      insulation at the exchange factor and its freight; the sector's older
>      rows the maker's less their city legs (since FR2, as every maker's), to
>      the bit.

**Uses:** [Good](Good.md) (92), [OldMetal](OldMetal.md) (29), [Sectors](Sectors.md) (23), [CopperRefining](CopperRefining.md) (22), [SpreadPlanner](SpreadPlanner.md) (18), [BuildingManager](BuildingManager.md) (15), [Sector](Sector.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (10), [Manufacturing](Manufacturing.md) (7), [CopperMiningCheck](CopperMiningCheck.md) (7), [Game](Game.md) (7), [ProcessingSector](ProcessingSector.md) (6), [JobType](JobType.md) (6), [MaterialsCheck](MaterialsCheck.md) (5), [BusinessInvestment](BusinessInvestment.md) (4), [Trade](Trade.md) (3), [BuildingType](BuildingType.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [MoneyAudit](MoneyAudit.md) (2), [CopperMining](CopperMining.md) (2), [Markets](Markets.md) (1), [PayTier](PayTier.md) (1), [Statement](Statement.md) (1)

## Sections

| line | section |
|---:|---|
| 111 | 1. the sector |
| 164 | 2. the five rows against the study |
| 279 | 3. the chain by its ratios |
| 336 | 4. the smelter's acid sold, the leach plant's bought |
| 369 | 5. old copper |
| 458 | 6. the planner weighs the old copper |
| 511 | 7. the wire works' estimate |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `CopperRefiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 80 | `CopperRefiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 167 | `CopperRefiningCheck.ROWS` | `{ CopperRefining.SMELTER, CopperRefining.ELECTROLYTIC_REFINERY, CopperRefinin...` | The rows, by their permanent ids, in the study's 4.3 order: Copper Refining's four and Manufacturing's wire works. |
| 171 | `CopperRefiningCheck.STUDY_COSTS` | `{ 500_000, 180_000, 280_000, 45_000, 110_000 }` | The study's 4.3: each one's cost, D$ thousands all in. |
| 174 | `CopperRefiningCheck.STUDY_POSTS` | `{ 700, 200, 200, 80, 400 }` | ...its posts. |
| 177 | `CopperRefiningCheck.STUDY_KW` | `{ 13_400, 6_000, 5_500, 1_100, 2_500 }` | ...its draw, kW. |
| 180 | `CopperRefiningCheck.STUDY_MADE` | `{ { Good.ANODE, Good.ACID }, { Good.COPPER }, { Good.COPPER }, { Good.COPPER_...` | ...what it makes a month, t. |
| 182 | `CopperRefiningCheck.STUDY_MAKES` | `{ { 9_750, 29_700 }, { 9_700 }, { 4_000 }, { 8_000 }, { 2_000 } }` | ...and how many tonnes of each, a month. |
| 185 | `CopperRefiningCheck.STUDY_FEED` | `{ Good.COPPER_CONC, Good.ANODE, Good.COPPER_SCRAP, Good.COPPER, Good.COPPER_R...` | ...and what it takes, t a month. |
| 187 | `CopperRefiningCheck.STUDY_USES` | `{ 33_000, 9_750, 4_400, 8_016, 2_000 }` | ...and how many tonnes of it, a month. |
| 190 | `CopperRefiningCheck.STUDY_TABLE` | `{ { - 0.3, 1.2, 2.7, 1.2 }, { - 1.9, 0.8, 3.5, 0.8 }, { 0.0, 2.2, 2.9, 0.7 },...` | The study's 4.4 table, % of the cost a month, cases A to D. |
| 194 | `CopperRefiningCheck.STUDY_RUNS` | `{ 5_253, 1_460, 1_776, 846, 4_820 }` | Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table - but the scrap refinery's, from the -0.02% ... |
| 197 | `CopperRefiningCheck.STUDY_SCRAP_A` | `- 0.02, STUDY_SCRAP_A_ROUNDING =.005` | The scrap refinery's case A as the study states it (its 4.4: "imported scrap just breaks even (case A, -0.02%)"), and that figure's rounding. |
| 200 | `CopperRefiningCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 282 | `CopperRefiningCheck.ACID_A_TONNE` | `.9` | The acid a smelter makes a tonne of concentrate: 0.9 t (the study's 4.1, [K27]). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 465 | **type** `public class CopperRefiningCheck` | Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's - materials-chains-research.md - 1.2, 4, 7 and 10's Q11 and Q12): copper's processing indu... |
| 82 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 87 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 92 | 5 | `static void quietly(Runnable r)` |  |
| 98 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the sector (lines 111-163)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 50 | `static void sector()` |  |

### 2. the five rows against the study (lines 164-278)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 76 | `static void rows()` |  |

### 3. the chain by its ratios (lines 279-335)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 51 | `static void chain()` |  |

### 4. the smelter's acid sold, the leach plant's bought (lines 336-368)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 30 | `static void acid()` |  |

### 5. old copper (lines 369-457)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 86 | `static void oldCopper()` |  |

### 6. the planner weighs the old copper (lines 458-510)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 31 | `static void plannerWeighsOldCopper()` |  |
| 493 | 10 | `static SpreadPlanner.Values oldValues(SpreadPlanner.Values plain, double spare, double price)` | The planner's values with `spare` t of old copper at `price` in front of the market's scrap (CopperRefining, THE OLD COPPER), built by hand. |
| 505 | 5 | `static SpreadPlanner.Candidate weigh(CopperRefining cr, BuildingsTemplate t, SpreadPlanner.Values v, BusinessInvestment plans, ...` | One works of `cr` weighed at `v`, as ProcessingSector weighs it: its freight in its earnings and its vans in its cost. |

### 7. the wire works' estimate (lines 511-538)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 25 | `static void wireWorksEstimate()` |  |

