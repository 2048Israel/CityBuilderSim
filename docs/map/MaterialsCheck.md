# MaterialsCheck.java - 1,537 lines · 31 methods · 27 constants · harnesses

`ham/citybuildersim/MaterialsCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The materials chains, checked: the shared machinery every chain stands on
> (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), and
> iron's sections as its batches came (I3 to I5) - coal, stone and copper
> have harnesses of their own (CoalMiningCheck, QuarryingCheck,
> CementConcreteCheck, CopperMiningCheck, CopperRefiningCheck).
> 
> WHY. Five chains - iron redone, coal, stone, copper and wood - share one
> planner, two sector bases, a captive good, a works' own power and the
> consumables it buys abroad, and none of them built a works until batch I3
> brought the first rows. So the machinery is held here on fixtures that cause
> each condition, before any chain leans on it (the playtest, which had none
> of it, stayed the same to the byte at I2); and from I3 each chain's rows are
> held to the study's arithmetic, at parity, before they move a city.
> 
> WHAT THIS HAS TO PROVE (the spec's 4, MaterialsCheck's I2 bullets):
> 
>   1. THE CITY'S VALUE OF A WORKS' OUTPUT AND FEED, SPLIT AT THE MONTH'S
>      TRADE (SpreadPlanner.outValue(), inValue()): made here, the net import
>      price for as much as the city imported and the net export price past
>      it; used here, the net export price for as much as it exported and the
>      net import price past it; the first unit's worth cityValue()'s, to the
>      bit; a price the world does not quote, the local price; the game's
>      values the market's own tally.
> 
>   2. THE WHOLE-COST GATE REFUSES A CASH-RICH SECTOR'S CASE A (decision A):
>      a works whose case A - feed imported, output exported - earns
>      something, but half the hurdle on its whole cost, in a sector holding
>      that cost in its till: the rule to 0.7.111 (ON_ITS_BORROWING) passes
>      it, the rule in force (ON_ITS_WHOLE_COST, the default) refuses it at
>      the money; and the four cases are the study's arithmetic, the works'
>      consumables and power in them.
> 
>   3. CAPTIVE USE BOOKS NO SALE: a ProcessingSector's good it both makes and
>      uses goes to its own need first, min(made, needed); the market sees
>      the net - bid() and what is made for the market - and the ledger
>      books sales of the rest and no purchase of it, a flow good and a
>      stockable one; three sectors in the game have captive goods, Iron &
>      Steel (its scrap, coke and pig iron, since 0.7.123 - none until then),
>      Copper Refining (its anode and cathode, since 0.7.133) and Wood
>      Products (its residues and pulp, since 0.7.140).
> 
>   4. A WORKS' OWN POWER IS BILLED NET AND THE CITY PAYS THE SURPLUS: in a
>      town whose bakeries are handed power of their own, each building buys
>      its draw past what it makes and sells the grid what it makes past its
>      draw; the sector's bill is its net draw at the ratio and the tariff,
>      its statement's powerSold the surplus at the tariff, in its revenue;
>      the grid's consumption is the net draws, its generation the plants'
>      and the works' surplus; the utility pays the statements' figure out of
>      its income; the audit closes; powerSold crosses a save.
> 
>   5. CONSUMABLES GO TO TRADE: a works' consumables, world money a month at
>      nameplate, are bought at the operating rate and the exchange factor as
>      a service from the world - an import named "Consumables" on the input
>      line - and the audit sees them leave in TRADE.
> 
>   6. A PROCESSING SECTOR'S PLAN: a candidate earns what it would make less
>      what it would use, at outValue() and inValue(), less its running and
>      standing costs and its consumables and power, to the bit; the last
>      one standing earns the mirror; the investors' test reads that
>      estimate.
> ... (88 more lines in the source)

**Uses:** [Good](Good.md) (207), [SpreadPlanner](SpreadPlanner.md) (95), [BuildingsTemplate](BuildingsTemplate.md) (46), [OldMetal](OldMetal.md) (45), [Game](Game.md) (40), [HeavyIndustry](HeavyIndustry.md) (32), [Sector](Sector.md) (31), [Sectors](Sectors.md) (30), [ProcessingSector](ProcessingSector.md) (27), [BuildingManager](BuildingManager.md) (27), [Mining](Mining.md) (22), [Resource](Resource.md) (20), [GameFiles](GameFiles.md) (19), [BusinessInvestment](BusinessInvestment.md) (14), [Deposit](Deposit.md) (12), [Trade](Trade.md) (11), [GameVersion](GameVersion.md) (11), [Markets](Markets.md) (8), [GoodsMarket](GoodsMarket.md) (8), [UtilitiesHandler](UtilitiesHandler.md) (8), [JobType](JobType.md) (8), [MoneyAudit](MoneyAudit.md) (7), [BuildingType](BuildingType.md) (7), [LandManager](LandManager.md) (5), [Statement](Statement.md) (4), [PayTier](PayTier.md) (4), [LandCheck](LandCheck.md) (4), [Traffic](Traffic.md) (3), [MiningCheck](MiningCheck.md) (3), [Founding](Founding.md) (2)... and 6 more

**Used by (10):** [BuildCardCheck](BuildCardCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [ForestryCheck](ForestryCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [QuarryingCheck](QuarryingCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 274 | 1. outValue and inValue |
| 325 | 2. the whole-cost gate and the four cases |
| 390 | 3. captive use |
| 459 | 4. the works' own power |
| 527 | 5. consumables |
| 565 | 6. a processing sector's plan |
| 618 | 7. idle, then shed |
| 680 | 8. Mining on the extraction base |
| 718 | 9. the iron rows against the study |
| 797 | 10. a save from before the split |
| 850 | 11. the integrated route's rows against the study |
| 955 | 12. old steel, and the 70% rule |
| 1145 | 13. captive coke and pig iron |
| 1204 | 14. the works' own power sold |
| 1237 | 15. Iron Mining's rows against the study |
| 1335 | 16. a mine fits its field |
| 1416 | 17. the dig and the retirement by type |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 172 | `MaterialsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 174 | `MaterialsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 721 | `MaterialsCheck.IRON_ROWS` | `{ Mining.IRON_MINE, HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL }` | The iron rows of batch I3 (0.7.120), by their permanent ids: the Iron Mine, the Electric Arc Furnace, the Steel Mini-Mill. |
| 724 | `MaterialsCheck.STUDY_CASES` | `{ { 2.7, 2.7, 4.1, 4.1 }, { - 0.5, - 0.1, 0.7, 0.2 }, { - 0.5, 0.1, 1.0, 0.4 } }` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 727 | `MaterialsCheck.STUDY_YEARS` | `{ 2.2, 4.4, 3.5 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 730 | `MaterialsCheck.STUDY_RUNS` | `{ 469, 187, 708 }` | ...and their running cost a month, D$ thousands, the study's 2.4 "Runs" (the mine's with its diesel in it). |
| 733 | `MaterialsCheck.STUDY_DIESEL` | `.0006` | The study's price of a litre of diesel in its runs, D$ thousands: the oil mockups' D$0.60 (its 1.5). |
| 736 | `MaterialsCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its cases to a tenth of a point, its years to a tenth. |
| 853 | `MaterialsCheck.ROUTE_ROWS` | `{ HeavyIndustry.SCRAP_YARD, HeavyIndustry.PELLETISING_PLANT, HeavyIndustry.CO...` | The integrated route's rows of batch I4 (0.7.123), by their permanent ids, in the study's 2.4 order. |
| 857 | `MaterialsCheck.ROUTE_CASES` | `{ { 1.5, 1.5, 4.7, 4.7 }, { 0.1, 1.8, 3.7, 2.0 }, { - 0.2, 0.7, 2.2, 1.3 }, {...` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 861 | `MaterialsCheck.ROUTE_YEARS` | `{ 1.0, 1.2, 1.7, 0.8, 1.1, 1.1, 1.7 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 864 | `MaterialsCheck.ROUTE_RUNS` | `{ 255, 1000, 1250, 868, 11700, 25600, 5100 }` | ...their running cost a month, D$ thousands, the study's 2.4 "Runs" (only the power each buys from the grid in it, the study's 1.6). |
| 867 | `MaterialsCheck.ROUTE_COSTS` | `{ 35_000, 270_000, 264_000, 54_000, 1_350_000, 1_800_000, 1_200_000 }` | ...their all-in cost, D$ thousands (the study's 2.4). |
| 870 | `MaterialsCheck.ROUTE_POSTS` | `{ 40, 150, 200, 150, 1200, 1800, 400 }` | ...their posts (the study's 2.4). |
| 873 | `MaterialsCheck.ROUTE_KW` | `{ 550, 5750, 2200, 2050, 51400, 51600, 77000 }` | ...their draw, kW (the study's 2.4 and 1.6). |
| 876 | `MaterialsCheck.ROUTE_GEN` | `{ 0, 0, 8000, 0, 60000, 25000, 0 }` | ...and what they make, kW: the Coke Ovens', the Blast Furnace's and the Oxygen Steel Shop's power from their gas (the study's 2.4 and 1.6). |
| 879 | `MaterialsCheck.ROUTE_HECTARES` | `{ 1.9, 15, 9, 3.7, 60, 84, 46 }` | ...and their ground, hectares (the study's 2.4). |
| 1240 | `MaterialsCheck.MINE_ROWS` | `{ Mining.LARGE_OPEN_PIT, Mining.MAGNETITE_MINE, Mining.LARGE_MAGNETITE }` | Iron Mining's rows of batch I5 (0.7.124), by their permanent ids, in the study's 2.4 order: the Large Open-Pit Mine, the Magnetite Mine & Concentrator, the Large Magnetite Complex. |
| 1243 | `MaterialsCheck.MINE_CASES` | `{ { 3.7, 3.7, 5.2, 5.2 }, { 1.2, 1.2, 2.2, 2.2 }, { 2.2, 2.2, 3.3, 3.3 } }` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 1246 | `MaterialsCheck.MINE_YEARS` | `{ 2.0, 2.9, 2.7 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 1249 | `MaterialsCheck.MINE_RUNS` | `{ 5_420, 951, 9_580 }` | ...their running cost a month, D$ thousands, the study's 2.4 "Runs". |
| 1252 | `MaterialsCheck.MINE_COSTS` | `{ 1_000_000, 60_000, 1_100_000 }` | ...their all-in cost, D$ thousands (the study's 2.4). |
| 1255 | `MaterialsCheck.MINE_POSTS` | `{ 400, 120, 500 }` | ...their posts (the study's 2.4). |
| 1258 | `MaterialsCheck.MINE_KW` | `{ 3_000, 3_000, 52_000 }, MINE_WATER = { 300, 400, 7_900 }` | ...their draw, kW, and water, units a month (the study's 2.4). |
| 1261 | `MaterialsCheck.MINE_MAKES` | `{ 500_000, 20_000, 400_000 }, MINE_ROCK = { 500_000, 60_000, 1_200_000 }` | ...what each makes a month, t of ore or concentrate, and the t of rock it digs for it (the study's 2.4: 60,000 t for the small magnetite works' 20,000, 1.2 Mt for the large one's 400,000; the hematite pit ships its or... |
| 1264 | `MaterialsCheck.MINE_TYPES` | `{ Deposit.FieldType.HEMATITE, Deposit.FieldType.MAGNETITE, Deposit.FieldType....` | ...the type of field each stands on, and the sites it takes (the study's 1.3, 2.4: twenty sites for a large mine, one for a small). |
| 1266 | `MaterialsCheck.MINE_SITES` | `{ 20, 1, 20 }` | ...and the sites each takes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 170 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 168 | 1370 | **type** `public class MaterialsCheck` | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), and iron's sections as its batches came (I3 to I5) - coal, stone and co... |
| 176 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 181 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 186 | 5 | `static void quietly(Runnable r)` |  |
| 193 | 3 | `static boolean near(double a, double b)` | Two money figures agree to MoneyAudit's tolerance at their size. |
| 202 | 17 | **type** `static final class Works extends ProcessingSector` | A processing sector of the fixture's own (no ProcessingSector stands in the game until batch I3 moves Heavy Industry onto it): Heavy Industry's key, so it reads that sector's buildings, and the goods it is told. |
| 203 | 5 | `Works(Good[] made, Good[] used)` _(in MaterialsCheck.Works)_ |  |
| 210 | 5 | `Map<String, Double> extras()` _(in MaterialsCheck.Works)_ | Its extras as a save writes them. |
| 217 | 1 | `void load(Map<String, Double> m)` _(in MaterialsCheck.Works)_ | ...and read back. |
| 221 | 7 | `static Works works(BuildingManager b, Markets m, Game g, Good[] made, Good[] used)` | A fixture's works, wired as Sectors wires a sector: to buildings and markets, and to a game when there is one. |
| 230 | 3 | `static Game town(String label)` | A town to stand works in: founded, the Wealthy preset's cash and ground, and the houses, shops, power and water to run them. |
| 235 | 15 | `static Game town(GameFiles files)` | ...in a save folder of the caller's, for a section that saves the town and loads its twin. |
| 251 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. outValue and inValue (lines 274-324)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 48 | `static void values()` |  |

### 2. the whole-cost gate and the four cases (lines 325-389)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 62 | `static void wholeCost()` |  |

### 3. captive use (lines 390-458)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 66 | `static void captive()` |  |

### 4. the works' own power (lines 459-526)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 14 | `static Game powerTown(GameFiles files, int months)` | The power town: section 4's and 5's, bakeries handed power of their own and consumables, run `months`. |
| 477 | 49 | `static void power() throws Exception` |  |

### 5. consumables (lines 527-564)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 35 | `static void consumables()` |  |

### 6. a processing sector's plan (lines 565-617)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 50 | `static void plan()` |  |

### 7. idle, then shed (lines 618-679)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 59 | `static void idleThenShed()` |  |

### 8. Mining on the extraction base (lines 680-717)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 35 | `static void mining()` |  |

### 9. the iron rows against the study (lines 718-796)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 58 | `static void ironRows()` |  |

### 10. a save from before the split (lines 797-849)

| line | len | member | says |
|---:|---:|---|---|
| 799 | 50 | `static void olderSave() throws Exception` |  |

### 11. the integrated route's rows against the study (lines 850-954)

| line | len | member | says |
|---:|---:|---|---|
| 881 | 73 | `static void routeRows()` |  |

### 12. old steel, and the 70% rule (lines 955-1144)

| line | len | member | says |
|---:|---:|---|---|
| 957 | 187 | `static void oldSteel() throws Exception` |  |

### 13. captive coke and pig iron (lines 1145-1203)

| line | len | member | says |
|---:|---:|---|---|
| 1147 | 56 | `static void captiveRoute()` |  |

### 14. the works' own power sold (lines 1204-1236)

| line | len | member | says |
|---:|---:|---|---|
| 1206 | 30 | `static void routePower()` |  |

### 15. Iron Mining's rows against the study (lines 1237-1334)

| line | len | member | says |
|---:|---:|---|---|
| 1268 | 66 | `static void mineRows()` |  |

### 16. a mine fits its field (lines 1335-1415)

| line | len | member | says |
|---:|---:|---|---|
| 1337 | 56 | `static void mineFits()` |  |
| 1402 | 6 | `static void handWorkers(Game g)` | A fixture city made ready to plan before its next month is played: workers handed to it as InvestCheck hands a shop's - twenty thousand, with diplomas, college and university among them - and the investors told the gr... |
| 1410 | 5 | `static Game.BuildResult quietlyBuild(Game g, BuildingsTemplate t)` | buildStack() quietly, as the Build tab places one. |

### 17. the dig and the retirement by type (lines 1416-1537)

| line | len | member | says |
|---:|---:|---|---|
| 1418 | 119 | `static void mineTypes()` |  |

