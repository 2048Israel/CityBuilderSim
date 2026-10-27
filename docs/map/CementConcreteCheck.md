# CementConcreteCheck.java - 354 lines · 11 methods · 12 constants · harnesses

`ham/citybuildersim/CementConcreteCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md
> 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-research.md
> - 1.2, 6 and 7): stone's processing industry, on Sectors.KEYS after
> Quarrying (its end when it came).
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
> CoalMiningCheck), and this one is ProcessingSector's rule on stone: its
> rows, its four cases against the study, who buys what it makes and takes,
> and the one thing the carry adds to how a works is weighed - limestone
> the quarries ship as aggregate is limestone the city exported.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: a ProcessingSector after Quarrying,
>      making cement and lime and using limestone, aggregate and thermal
>      coal, Iron & Steel's group; nothing captive; cement in a silo.
> 
>   2. THE THREE ROWS ARE THE STUDY'S (ids 118 to 120, at parity): cost,
>      posts, draw (cement 91 kWh a tonne; the kiln at the Pelletising
>      Plant's ratio), ground (the Steel Foundry's per D$), road load,
>      stock, consumables; and their four cases, worked from the study's own
>      figures (its 6.4 rows at its 6.3 prices) as I6's were, to its
>      rounding; then the returns its 6.5 and 1.2 state - cement plants
>      0.1-0.3% exporting and 1.5-1.9% replacing imports, the kiln 1.6% on
>      limestone at its export price and 5.7% replacing imported lime, the
>      kiln's case A 0.2% and the cement plants' below nothing.
> 
>   3. THE BUYERS (spec 2.12): limestone to the blast furnaces and the kilns
>      and cement plants; lime to the oxygen shop; thermal coal to the kilns
>      and cement plants, Coal Mining's where the city digs it; aggregate to
>      the cement plants alone; cement and dimension stone to nobody here
>      until the construction upgrade - they ship.
> 
>   4. LIMESTONE CARRIED IS LIMESTONE EXPORTED (ProcessingSector.values()):
>      with the quarries carrying limestone into aggregate, a works' limestone
>      is weighed at the export price for as much as was carried, the import
>      price past it - a kiln's appraisal that figure to the bit; with none
>      carried, SpreadPlanner.values() unchanged.
> 
>   5. THE PLAN: the best earnings on the whole cost of the works that pass
>      every gate (SpreadPlanner.best()), or the refusal of the best fed.
> 
>   6. A PLAYED MONTH: a Cement Plant beside a quarry buys the quarry's
>      limestone at home, puts its cement in its silo or ships it, and the
>      money audit closes.

**Uses:** [Good](Good.md) (62), [Sectors](Sectors.md) (20), [SpreadPlanner](SpreadPlanner.md) (20), [CementConcrete](CementConcrete.md) (9), [BuildingManager](BuildingManager.md) (8), [ProcessingSector](ProcessingSector.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Game](Game.md) (4), [Quarrying](Quarrying.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [QuarryingCheck](QuarryingCheck.md) (3), [Sector](Sector.md) (3), [Markets](Markets.md) (2), [BuildingType](BuildingType.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [JobType](JobType.md) (2), [Deposit](Deposit.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [PayTier](PayTier.md) (1), [GoodsMarket](GoodsMarket.md) (1), [MoneyAudit](MoneyAudit.md) (1)

## Sections

| line | section |
|---:|---|
| 95 | 1. the sector |
| 116 | 2. the three rows against the study |
| 220 | 3. the buyers |
| 252 | 4. limestone carried is limestone exported |
| 319 | 6. a played month |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `CementConcreteCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 66 | `CementConcreteCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 119 | `CementConcreteCheck.ROWS` | `{ CementConcrete.CEMENT_PLANT, CementConcrete.LARGE_CEMENT_PLANT, CementConcr...` | Cement & Concrete's rows, by their permanent ids, in the study's 6.4 order. |
| 122 | `CementConcreteCheck.STUDY_COSTS` | `{ 120_000, 500_000, 75_000 }` | The study's 6.4: each one's cost, D$ thousands all in. |
| 125 | `CementConcreteCheck.STUDY_POSTS` | `{ 60, 150, 45 }` | ...its posts. |
| 128 | `CementConcreteCheck.STUDY_RUNS` | `{ 501, 1_730, 298 }` | ...its running cost a month, D$ thousands. |
| 131 | `CementConcreteCheck.STUDY_MAKES` | `{ 25_000, 125_000, 18_000 }` | ...what it makes a month, t: cement, cement, lime. |
| 134 | `CementConcreteCheck.STUDY_USES` | `{ { 30_000, 3_750, 3_350 }, { 150_000, 18_750, 16_750 }, { 32_400, 0, 3_600 } }` | ...and what it takes, t a month: limestone, aggregate, thermal coal. |
| 137 | `CementConcreteCheck.CEMENT_KWH` | `91` | The research's cement, kWh a tonne: the study's 6.2, 91 [S31]. |
| 140 | `CementConcreteCheck.HOURS` | `QuarryingCheck.HOURS` | The hours of a month the study reads its kW at (its 2.2): QuarryingCheck's. |
| 143 | `CementConcreteCheck.SINCE` | `{ 1900, 1902, 1900 }` | The year each first existed: 1900 - standing before the start - but the Large Cement Plant, Edison's long kilns at New Village in 1902 (research-1900.md 1.2). |
| 146 | `CementConcreteCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 295 | **type** `public class CementConcreteCheck` | Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-research.md - 1.2, 6 and 7): stone's processing industry, on Sectors.KEYS ... |
| 68 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 73 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 78 | 5 | `static void quietly(Runnable r)` |  |
| 84 | 10 | `public static void main(String[] args) throws Exception` |  |

### 1. the sector (lines 95-115)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 18 | `static void sector()` |  |

### 2. the three rows against the study (lines 116-219)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 71 | `static void rows()` |  |

### 3. the buyers (lines 220-251)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 29 | `static void buyers()` |  |

### 4. limestone carried is limestone exported (lines 252-318)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 50 | `static void carriedIsExported()` |  |
| 306 | 5 | `static SpreadPlanner.Candidate weigh(CementConcrete c, BuildingsTemplate t, SpreadPlanner.Values v, BusinessInvestment plans, G...` | One works of `c` weighed at `v`, as ProcessingSector weighs it: its freight in its earnings and its vans in its cost. |
| 313 | 1 | **type** `record MarketsCheckValues(double limeIn)` | What a tonne of limestone used costs the city now (the values' in()). |
| 315 | 3 | `static MarketsCheckValues valuesOf(Game g)` |  |

### 6. a played month (lines 319-354)

| line | len | member | says |
|---:|---:|---|---|
| 321 | 33 | `static void playedMonth()` |  |

