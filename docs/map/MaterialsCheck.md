# MaterialsCheck.java - 1,178 lines · 26 methods · 17 constants · harnesses

`ham/citybuildersim/MaterialsCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The materials chains, checked: the shared machinery every chain stands on
> (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.5-2.8 and 4), and each
> chain's section as its batch comes (I3 onwards).
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
>      stockable one; one sector in the game has captive goods, Iron & Steel
>      (its scrap, coke and pig iron, since 0.7.123 - none until then).
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
> 
>   7. IDLE, THEN SHED: a kind of its works whose last one earns below
>      nothing counts a month idle, a working kind none; at IDLE_MONTHS that
>      kind alone may be sold, the spare-capacity rule's measure its
> ... (59 more lines in the source)

**Uses:** [Good](Good.md) (170), [SpreadPlanner](SpreadPlanner.md) (90), [OldMetal](OldMetal.md) (45), [BuildingsTemplate](BuildingsTemplate.md) (35), [HeavyIndustry](HeavyIndustry.md) (32), [Sector](Sector.md) (31), [Sectors](Sectors.md) (28), [Game](Game.md) (28), [BuildingManager](BuildingManager.md) (21), [ProcessingSector](ProcessingSector.md) (19), [GameFiles](GameFiles.md) (18), [Trade](Trade.md) (11), [GameVersion](GameVersion.md) (11), [BusinessInvestment](BusinessInvestment.md) (9), [Markets](Markets.md) (8), [GoodsMarket](GoodsMarket.md) (8), [MoneyAudit](MoneyAudit.md) (7), [BuildingType](BuildingType.md) (6), [UtilitiesHandler](UtilitiesHandler.md) (6), [JobType](JobType.md) (6), [Statement](Statement.md) (4), [Mining](Mining.md) (4), [Resource](Resource.md) (3), [PayTier](PayTier.md) (3), [Founding](Founding.md) (1), [LandManager](LandManager.md) (1), [ExtractionSector](ExtractionSector.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 241 | 1. outValue and inValue |
| 292 | 2. the whole-cost gate and the four cases |
| 357 | 3. captive use |
| 417 | 4. the works' own power |
| 485 | 5. consumables |
| 523 | 6. a processing sector's plan |
| 576 | 7. idle, then shed |
| 638 | 8. Mining on the extraction base |
| 676 | 9. the iron rows against the study |
| 755 | 10. a save from before the split |
| 807 | 11. the integrated route's rows against the study |
| 912 | 12. old steel, and the 70% rule |
| 1099 | 13. captive coke and pig iron |
| 1146 | 14. the works' own power sold |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 142 | `MaterialsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 144 | `MaterialsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 679 | `MaterialsCheck.IRON_ROWS` | `{ Mining.IRON_MINE, HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL }` | The iron rows of batch I3 (0.7.120), by their permanent ids: the Iron Mine, the Electric Arc Furnace, the Steel Mini-Mill. |
| 682 | `MaterialsCheck.STUDY_CASES` | `{ { 2.7, 2.7, 4.1, 4.1 }, { - 0.5, - 0.1, 0.7, 0.2 }, { - 0.5, 0.1, 1.0, 0.4 } }` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 685 | `MaterialsCheck.STUDY_YEARS` | `{ 2.2, 4.4, 3.5 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 688 | `MaterialsCheck.STUDY_RUNS` | `{ 469, 187, 708 }` | ...and their running cost a month, D$ thousands, the study's 2.4 "Runs" (the mine's with its diesel in it). |
| 691 | `MaterialsCheck.STUDY_DIESEL` | `.0006` | The study's price of a litre of diesel in its runs, D$ thousands: the oil mockups' D$0.60 (its 1.5). |
| 694 | `MaterialsCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its cases to a tenth of a point, its years to a tenth. |
| 810 | `MaterialsCheck.ROUTE_ROWS` | `{ HeavyIndustry.SCRAP_YARD, HeavyIndustry.PELLETISING_PLANT, HeavyIndustry.CO...` | The integrated route's rows of batch I4 (0.7.123), by their permanent ids, in the study's 2.4 order. |
| 814 | `MaterialsCheck.ROUTE_CASES` | `{ { 1.5, 1.5, 4.7, 4.7 }, { 0.1, 1.8, 3.7, 2.0 }, { - 0.2, 0.7, 2.2, 1.3 }, {...` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 818 | `MaterialsCheck.ROUTE_YEARS` | `{ 1.0, 1.2, 1.7, 0.8, 1.1, 1.1, 1.7 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 821 | `MaterialsCheck.ROUTE_RUNS` | `{ 255, 1000, 1250, 868, 11700, 25600, 5100 }` | ...their running cost a month, D$ thousands, the study's 2.4 "Runs" (only the power each buys from the grid in it, the study's 1.6). |
| 824 | `MaterialsCheck.ROUTE_COSTS` | `{ 35_000, 270_000, 264_000, 54_000, 1_350_000, 1_800_000, 1_200_000 }` | ...their all-in cost, D$ thousands (the study's 2.4). |
| 827 | `MaterialsCheck.ROUTE_POSTS` | `{ 40, 150, 200, 150, 1200, 1800, 400 }` | ...their posts (the study's 2.4). |
| 830 | `MaterialsCheck.ROUTE_KW` | `{ 550, 5750, 2200, 2050, 51400, 51600, 77000 }` | ...their draw, kW (the study's 2.4 and 1.6). |
| 833 | `MaterialsCheck.ROUTE_GEN` | `{ 0, 0, 8000, 0, 60000, 25000, 0 }` | ...and what they make, kW: the Coke Ovens', the Blast Furnace's and the Oxygen Steel Shop's power from their gas (the study's 2.4 and 1.6). |
| 836 | `MaterialsCheck.ROUTE_HECTARES` | `{ 1.9, 15, 9, 3.7, 60, 84, 46 }` | ...and their ground, hectares (the study's 2.4). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 140 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 138 | 1041 | **type** `public class MaterialsCheck` | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.5-2.8 and 4), and each chain's section as its batch comes (I3 onwards). |
| 146 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 151 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 156 | 5 | `static void quietly(Runnable r)` |  |
| 163 | 3 | `static boolean near(double a, double b)` | Two money figures agree to MoneyAudit's tolerance at their size. |
| 172 | 17 | **type** `static final class Works extends ProcessingSector` | A processing sector of the fixture's own (no ProcessingSector stands in the game until batch I3 moves Heavy Industry onto it): Heavy Industry's key, so it reads that sector's buildings, and the goods it is told. |
| 173 | 5 | `Works(Good[] made, Good[] used)` _(in MaterialsCheck.Works)_ |  |
| 180 | 5 | `Map<String, Double> extras()` _(in MaterialsCheck.Works)_ | Its extras as a save writes them. |
| 187 | 1 | `void load(Map<String, Double> m)` _(in MaterialsCheck.Works)_ | ...and read back. |
| 191 | 7 | `static Works works(BuildingManager b, Markets m, Game g, Good[] made, Good[] used)` | A fixture's works, wired as Sectors wires a sector: to buildings and markets, and to a game when there is one. |
| 200 | 3 | `static Game town(String label)` | A town to stand works in: founded, the Wealthy preset's cash and ground, and the houses, shops, power and water to run them. |
| 205 | 15 | `static Game town(GameFiles files)` | ...in a save folder of the caller's, for a section that saves the town and loads its twin. |
| 221 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. outValue and inValue (lines 241-291)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 48 | `static void values()` |  |

### 2. the whole-cost gate and the four cases (lines 292-356)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 62 | `static void wholeCost()` |  |

### 3. captive use (lines 357-416)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 57 | `static void captive()` |  |

### 4. the works' own power (lines 417-484)

| line | len | member | says |
|---:|---:|---|---|
| 420 | 14 | `static Game powerTown(GameFiles files, int months)` | The power town: section 4's and 5's, bakeries handed power of their own and consumables, run `months`. |
| 435 | 49 | `static void power() throws Exception` |  |

### 5. consumables (lines 485-522)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 35 | `static void consumables()` |  |

### 6. a processing sector's plan (lines 523-575)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 50 | `static void plan()` |  |

### 7. idle, then shed (lines 576-637)

| line | len | member | says |
|---:|---:|---|---|
| 578 | 59 | `static void idleThenShed()` |  |

### 8. Mining on the extraction base (lines 638-675)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 35 | `static void mining()` |  |

### 9. the iron rows against the study (lines 676-754)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 58 | `static void ironRows()` |  |

### 10. a save from before the split (lines 755-806)

| line | len | member | says |
|---:|---:|---|---|
| 757 | 49 | `static void olderSave() throws Exception` |  |

### 11. the integrated route's rows against the study (lines 807-911)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 73 | `static void routeRows()` |  |

### 12. old steel, and the 70% rule (lines 912-1098)

| line | len | member | says |
|---:|---:|---|---|
| 914 | 184 | `static void oldSteel() throws Exception` |  |

### 13. captive coke and pig iron (lines 1099-1145)

| line | len | member | says |
|---:|---:|---|---|
| 1101 | 44 | `static void captiveRoute()` |  |

### 14. the works' own power sold (lines 1146-1178)

| line | len | member | says |
|---:|---:|---|---|
| 1148 | 30 | `static void routePower()` |  |

