# WoodProductsCheck.java - 636 lines · 16 methods · 30 constants · harnesses

`ham/citybuildersim/WoodProductsCheck.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

> Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md
> 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's -
> materials-chains-research.md - 1.2, 1.6 and 5.3 to 5.6): wood's processing
> industry, the twenty-fourth key, on Forestry's logs.
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
> CoalMiningCheck to I9's ForestryCheck), and wood's mills add what the
> other chains' works do not all have together: a by-product with two
> buyers inside the sector (the sawmills' residues, the pulp mill's and the
> pellet mill's), a second captive good one step on (the pulp mill's pulp,
> the paper mill's), a works that makes more power than it draws and sells
> it (the pulp mill's liquor), and a feed the city cuts itself (Forestry's
> logs) bought before the world's.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: Wood Products the twenty-fourth and last
>      key, after Forestry; a ProcessingSector making lumber, residues,
>      panels, pulp, paper and pellets of sawlogs, pulpwood, residues, pulp
>      and fuel oil, Iron & Steel's group; residues and pulp captive, lumber
>      its planning good; what is as long as the keys grew with them; the six
>      goods at the study's 5.3 prices, freight three eighths of each wedge,
>      lumber, panels and residues by the cubic metre at their tonnes, the
>      five stockable on HELD's end; the buyers (spec 2.12) - the logs, the
>      residues and the pulp its own, the fuel oil the paper mill's, the rest
>      nobody's here, shipped.
> 
>   2. THE SIX ROWS (ids 133 to 138, at parity): the study's 5.4 and 5.6 -
>      cost, posts, what each makes and uses, its running cost, the pulp
>      mill's 13,500 kW and the paper mill's 4,900 kW, 1,450 water units and
>      19 ha - the spec's 2.2 for the rest (power by the research's kWh a
>      unit, water, ground, road load, stock), and their four cases, the
>      study's 5.5 table to its rounding; then what the study says of them -
>      a cubic metre of residues worth more to a pulp mill than to a pellet
>      mill, the paper mill paying only where it replaces imports.
> 
>   3. THE CHAIN, CAPTIVE: a sawmill, a pulp mill and a paper mill standing -
>      the sawmill's residues to the pulp mill first, at the rate (min of
>      what is made and what is needed), the pulp mill's pulp to the paper
>      mill the same, nothing booked for either; the rest of the residues and
>      the fuel oil bought, the lumber, the rest of the pulp and the paper
>      shipped; the audit closes.
> 
>   4. THE PULP MILL'S POWER SOLD (spec 2.8): it buys none from the grid and
>      sells its 13,500 kW at the tariff, in its revenue; the city's utility
>      pays it, and the grid counts it as generation.
> 
>   5. FEED FROM FORESTRY BEFORE IMPORTS: a sawmill beside a Logging Camp
>      buys the camp's sawlogs at home and imports only what the camp does
>      not cut; beside a Harvesting Operation it imports none, and the rest
>      of the logs ship.
> 
>   6. THE PLAN AT FOUNDING: on imported feed for export - every mill's case
>      A, at the founding's prices, its lorries and repairs with it - no mill
>      clears the whole-cost hurdle (the study's 1.2), so the plan orders none
>      and says so.
> 
>   7. A PLAYED CITY, saved and reloaded the same: the mills' yards on HELD,
>      the sector's cash and stock, a month on.
> 
> ... (2 more lines in the source)

**Uses:** [Good](Good.md) (154), [WoodProducts](WoodProducts.md) (29), [JobType](JobType.md) (12), [Sectors](Sectors.md) (11), [SpreadPlanner](SpreadPlanner.md) (10), [BuildingManager](BuildingManager.md) (9), [NationalAccounts](NationalAccounts.md) (8), [Sector](Sector.md) (8), [MaterialsCheck](MaterialsCheck.md) (8), [Game](Game.md) (7), [Traffic](Traffic.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (6), [MoneyAudit](MoneyAudit.md) (5), [Ports](Ports.md) (5), [Equity](Equity.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [Forestry](Forestry.md) (3), [BuildingType](BuildingType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [PayTier](PayTier.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [GameFiles](GameFiles.md) (2), [Markets](Markets.md) (1), [ProcessingSector](ProcessingSector.md) (1), [Bank](Bank.md) (1), [Statement](Statement.md) (1), [ForestryCheck](ForestryCheck.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 116 | 1. the sector |
| 211 | 2. the six rows against the study |
| 407 | 3. the chain, captive |
| 463 | 4. the pulp mill's power sold |
| 492 | 5. feed from Forestry before imports |
| 540 | 6. the plan at founding |
| 569 | 7. a played city, saved and reloaded |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `WoodProductsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 83 | `WoodProductsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 119 | `WoodProductsCheck.GOODS` | `{ Good.LUMBER, Good.PANELS, Good.RESIDUES, Good.PULP, Good.PAPER, Good.WOOD_P...` | Wood's six goods, in Good's order (the spec's 2.3). |
| 121 | `WoodProductsCheck.STUDY_IMPORT` | `{.340,.420,.045,.760, 1.050,.220 }` | The study's 5.3: each one's import price, D$ thousands a cubic metre or a tonne. |
| 123 | `WoodProductsCheck.STUDY_EXPORT` | `{.250,.260,.015,.600,.880,.150 }` | ...and its export price. |
| 125 | `WoodProductsCheck.STUDY_UNIT` | `{ "cubic metre", "cubic metre", "cubic metre", "tonne", "tonne", "tonne" }` | ...its unit. |
| 127 | `WoodProductsCheck.TONNES_A_M3` | `{.5,.62,.9, Double.NaN, Double.NaN, Double.NaN }` | ...the tonnes a cubic metre of it weighs (spec 2.3, est.: sawn lumber 0.5, OSB 0.62 [W35], chips green as the logs, 0.9); NaN for one by the tonne. |
| 129 | `WoodProductsCheck.STOCKABLE` | `{ true, true, false, true, true, true }` | ...and whether it keeps in a yard (spec 2.3: all but the residues). |
| 131 | `WoodProductsCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 214 | `WoodProductsCheck.ROWS` | `{ WoodProducts.SAWMILL, WoodProducts.LARGE_SAWMILL, WoodProducts.PANEL_MILL, ...` | The rows, by their permanent ids, in the study's 5.4 order. |
| 218 | `WoodProductsCheck.STUDY_COSTS` | `{ 15_000, 150_000, 140_000, 260_000, 150_000, 30_000 }` | The study's 5.4: each one's cost, D$ thousands all in. |
| 221 | `WoodProductsCheck.STUDY_POSTS` | `{ 35, 130, 100, 200, 120, 30 }` | ...its posts. |
| 224 | `WoodProductsCheck.STUDY_MADE` | `{ { Good.LUMBER, Good.RESIDUES }, { Good.LUMBER, Good.RESIDUES }, { Good.PANE...` | ...what it makes a month. |
| 227 | `WoodProductsCheck.STUDY_MAKES` | `{ { 2_080, 1_640 }, { 20_020, 15_785 }, { 15_000 }, { 11_000 }, { 5_500 }, { ...` | ...and how much of each, a month (cubic metres or tonnes). |
| 230 | `WoodProductsCheck.STUDY_FEED` | `{ { Good.SAWLOGS }, { Good.SAWLOGS }, { Good.PULPWOOD }, { Good.PULPWOOD, Goo...` | ...what it takes a month. |
| 233 | `WoodProductsCheck.STUDY_USES` | `{ { 4_000 }, { 38_500 }, { 27_750 }, { 28_000, 21_500 }, { 5_775, 850_000 }, ...` | ...and how much of each (the paper mill's fuel in litres, the study's 5.6). |
| 236 | `WoodProductsCheck.STUDY_RUNS` | `{ 177, 844, 1_360, 2_170, 1_380, 183 }` | ...its running cost a month, D$ thousands, the study's 5.4 "Runs" - power bought from the grid only (its 2.4), the paper mill's fuel in it. |
| 239 | `WoodProductsCheck.STUDY_TABLE` | `{ { - 0.5, 0.7, 2.3, 1.1 }, { 0.1, 1.3, 2.8, 1.6 }, { 0.0, 1.1, 2.8, 1.7 }, {...` | The study's 5.5 table, % of the cost a month, cases A to D. |
| 243 | `WoodProductsCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 246 | `WoodProductsCheck.PULP_GEN` | `13_500` | The pulp mill's power past its own draw, kW (the study's 1.6: 11,000 t at 0.9 MWh a tonne [W42]). |
| 249 | `WoodProductsCheck.PAPER_KW` | `4_900, PAPER_WATER = 1_450, PAPER_HA = 19` | The paper mill's draw, kW, its water units and its ground, ha (the study's 5.6). |
| 252 | `WoodProductsCheck.PULP_WATER` | `11_600` | The pulp mill's water units (the study's 5.4). |
| 255 | `WoodProductsCheck.PAPER_POSTS` | `{ 50, 45, 20, 5 }` | The paper mill's posts by tier (the study's 5.4: 50/45/20/5). |
| 258 | `WoodProductsCheck.SAWMILL_KWH` | `104` | The sawmills' power, kWh a cubic metre of lumber: the middle of the research's 99-109 [W27]. |
| 260 | `WoodProductsCheck.OSB_KWH` | `207` | ...the panel mill's, a cubic metre of OSB [W34]. |
| 262 | `WoodProductsCheck.HOURS` | `730` | The study's month in hours (its 2.2: the Steel Mini-Mill's 3,700 kW for 6,000 t is 450 kWh a tonne). |
| 265 | `WoodProductsCheck.HEAVY` | `{ 54, 35, 9, 2 }, PULP_AND_PAPER = { 42, 37, 17, 4 }` | The posts' families (spec 2.2), no diploma / diploma / college / university, %: the heavy plants' and pulp and paper's. |
| 268 | `WoodProductsCheck.SQ_FT_A_HA` | `107_639.104` | Square feet in a hectare. |
| 628 | `WoodProductsCheck.RELOAD_RESIDUAL_K` | `.01` | A reloaded city's known residual a month on in money, D$ thousands (QUEUE.md's FW6): ten dollars, past the 2.8 measured. |
| 631 | `WoodProductsCheck.RELOAD_RESIDUAL` | `1e-6` | ...and in what a yard holds, a share of it: a millionth. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 560 | **type** `public class WoodProductsCheck` | Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's - materials-chains-research.md - 1.2, 1.6 and 5.3 to 5.6): wood's processing ind... |
| 85 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 90 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 95 | 5 | `static void quietly(Runnable r)` |  |
| 101 | 1 | `static boolean same(double a, double b)` |  |
| 103 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the sector (lines 116-210)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 77 | `static void sector()` |  |

### 2. the six rows against the study (lines 211-406)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 106 | `static void rows()` |  |
| 378 | 13 | `static int[] split(int n, double[] fam)` | Posts split by a family's shares by largest remainder, ties to the less skilled (spec 2.2, O4-4). |
| 393 | 7 | `static double residueWorth(BuildingsTemplate t, double tariff, double water)` | What a works earns at parity in case B with its residues at nothing, a cubic metre of the residues it takes, D$ thousands. |
| 401 | 5 | `static double maxOf(double[][] pct, int c)` |  |

### 3. the chain, captive (lines 407-462)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 53 | `static void chain()` |  |

### 4. the pulp mill's power sold (lines 463-491)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 26 | `static void power()` |  |

### 5. feed from Forestry before imports (lines 492-539)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 45 | `static void feed()` |  |

### 6. the plan at founding (lines 540-568)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 26 | `static void planAtFounding()` |  |

### 7. a played city, saved and reloaded (lines 569-636)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 55 | `static void played()` |  |
| 633 | 3 | `static boolean close(double a, double b)` |  |

