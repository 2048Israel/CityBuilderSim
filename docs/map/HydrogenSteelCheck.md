# HydrogenSteelCheck.java - 510 lines · 22 methods · 5 constants · harnesses

`ham/citybuildersim/HydrogenSteelCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> The Hydrogen Steel Works, checked (R6b, FE4: hydrogen-reduced iron, Boden, Sweden, projected June 2028): the near-future row that makes steel from
> pellets and the grid's power alone, no coke, no limestone, no scrap, in a city that knows it.
> 
> WHY. The game has no DRI good, so R6b made the shaft furnace and the arc furnace one integrated works in Heavy Industry (154), its figures composed as 0.4 of
> a Blast Furnace plus 0.8 of a Large Mini-Mill (estimates); Iron & Steel now uses pellets, so a Pelletising Plant's pellets go to the works first (captive,
> ProcessingSector) and a city that has no works must play exactly as it did. And a row whose cost is its power is only worth building where power is cheap:
> that has to be a property of the planner's own arithmetic, not a number typed here. So this harness proves the row, the rule that knows it, where the planner
> orders it and where it does not, the pellets that go to it first, and that nothing moves without it.
> 
> What this has to prove:
>   1. THE ROW. 154 makes steel and nothing else, takes pellets at 1 / 0.65 a tonne of it, draws HYDROGEN_MWH_A_TONNE a tonne in the game's units (a unit is
>      HydroSites.KWH_PER_UNIT kWh) to within a unit's rounding, takes no coke, coking or thermal coal, limestone, lime, pig iron, scrap or ore, is dated "FE4"
>      (a near-future row), and Heavy Industry uses pellets as well as makes them.
>   2. KNOWN BY THE RESEARCH. A city founded the 1900 way does not have it among Heavy Industry's candidates (what the planner reads) the month before FE4's
>      and does at it, or at the month its own research finds FE4; a city founded today's way never does, past FE4's month, and refuses an order for it.
>   3. WHERE IT PAYS, AND WHERE IT DOES NOT. Cities the same in everything but the price of the grid's power - the model keeps one tariff, the utility's
>      (UtilitiesHandler), and no lever on it by city, so a fixture sets it (EconomyManager.setPricePerWatt()), the break-even read off the planner's own
>      figures at the model's own price and at none - the city's fabricators having imported steel (so 154 would replace imports, the study's case D; with
>      steel exported and no pellets of the city's own it earns under the hurdle on any power at all): on the model's own tariff the planner's earnings on
>      154's whole cost clear Heavy Industry's own hurdle at its own gate and the plan orders it, ranked first of the works that pass; in the city whose power
>      is dearer, as far past the break-even as the model's own is under it, they do not, and the two cities' earnings differ by exactly the power the planner
>      bills at the two prices; and on the model's own power in a city that imports no steel it does not clear either. Printed: the earnings, the hurdles, the prices.
>   4. CAPTIVE PELLETS. The plants' pellets go to the works first, min(made, needed): in the works of the real rows the market bids only the rest and no
>      purchase or sale is booked for what the works took; in a 1900 city at FE4's month with both placed by hand a played month makes steel, the works' pellets
>      are the plant's first (the captive figure the sector reports, the bid only the rest), and the works' power is drawn at the template's figure.
>   5. NOTHING MOVES WITHOUT THE WORKS. With a Pelletising Plant and no 154 the sector's captive pellets are exactly 0 and its pellets go to the market whole:
>      the same month's figures, to the bit, as a sector that only makes pellets (the arithmetic the sector had before it used them), in a bare sector and in a
>      played city.
>   6. A PLAYED COUNT. The 1900 city on the model's own tariff played two years - from FE4's month, and from its founding with FE4 found by its own research
>      (a stable economy) - with its plants standing, the planner's money gate its own and then set aside: the 154s ordered and built are counted and printed,
>      none asserted, with the planner's last word; a city founded today's way, played the same months, builds none. The tariff cannot be held cheap across
>      played months (the game sets it from the utility each month), so the cheaper-power count is section 3's plan, not a played one.
> 
> Every fixture causes its condition: the 1900 city's clock is set at FE4's month (ResearchCheck.atMonth()) before its works are built, so the whole catalogue
> is known; the works and the plant are placed by hand where a section is about them; the labour is handed (MaterialsCheck.handWorkers()) as the plan fixtures
> of the materials chains hand it, because the plan under test is the planner's own, not a labour market's.

**Uses:** [Good](Good.md) (76), [HeavyIndustry](HeavyIndustry.md) (45), [Game](Game.md) (25), [SpreadPlanner](SpreadPlanner.md) (18), [BuildingManager](BuildingManager.md) (16), [MaterialsCheck](MaterialsCheck.md) (14), [Sector](Sector.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (11), [Sectors](Sectors.md) (7), [CityCalendar](CityCalendar.md) (6), [BusinessInvestment](BusinessInvestment.md) (4), [Markets](Markets.md) (4), [MoneyAudit](MoneyAudit.md) (4), [ResearchCatalog](ResearchCatalog.md) (3), [ResearchCheck](ResearchCheck.md) (3), [Founding](Founding.md) (2), [HydroSites](HydroSites.md) (2), [GoodsMarket](GoodsMarket.md) (2), [LicenceFees](LicenceFees.md) (2), [GameFiles](GameFiles.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Trade](Trade.md) (1), [Statement](Statement.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

**Used by (1):** [ElectrolyserCheck](ElectrolyserCheck.md)

## Sections

| line | section |
|---:|---|
| 133 | 1. the row |
| 167 | 2. known by the research |
| 216 | 3. where it pays, and where it does not |
| 322 | 4. captive pellets |
| 411 | 5. nothing moves without the works |
| 460 | 6. a played count |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `HydrogenSteelCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 56 | `HydrogenSteelCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 87 | `HydrogenSteelCheck.PELLET_FE` | `.65` | The iron the row's pellets carry, 65% Fe: the row's own comment (1 / 0.65 t of pellets a tonne of steel); the model keeps no constant for it. |
| 89 | `HydrogenSteelCheck.PLAYED_MONTHS` | `24` | The months section 6 plays from FE4's: two years. |
| 91 | `HydrogenSteelCheck.PLANTS` | `2` | The Pelletising Plants section 3 and 4 stand: enough for the works' pellets twice over at nameplate. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 461 | **type** `public class HydrogenSteelCheck` | The Hydrogen Steel Works, checked (R6b, FE4: hydrogen-reduced iron, Boden, Sweden, projected June 2028): the near-future row that makes steel from pellets and the grid's power alone, no coke, no limestone, no scrap, i... |
| 58 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 63 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 68 | 5 | `static void quietly(Runnable r)` |  |
| 74 | 11 | `public static void main(String[] args) throws Exception` |  |
| 94 | 1 | `static int fe4()` | FE4's month in the game's count, from the catalogue. |
| 97 | 3 | `static void at(Game g, int month)` | The clock set at a month (ResearchCheck.atMonth()), its checked exception the harness's own. |
| 106 | 1 | `static Game town1900(String label)` | A city founded the 1900 way with its clock at FE4's month before its works are built, so that the whole catalogue is known: the Wealthy preset's cash and ground, houses, shops, enough power and water for the works (a ... |
| 109 | 17 | `static Game town(String label, boolean atFe4)` | ...or, with `atFe4` false, left at its founding month with its own research having found FE4 and the two it needs (E4, PE1): the second way a 1900 city comes to know the row, in an economy not thrown 128 years. |
| 128 | 4 | `static long held(BuildingManager b, BuildingsTemplate t)` | The standing and the ordered of a template: what is built plus what is under construction. |

### 1. the row (lines 133-166)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 31 | `static void theRow()` |  |

### 2. known by the research (lines 167-215)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 4 | `static boolean weighed(HeavyIndustry heavy, Game g, BuildingsTemplate t)` | Whether a template is among the candidates a sector's planner weighs. |
| 175 | 40 | `static void knownByTheResearch()` |  |

### 3. where it pays, and where it does not (lines 216-321)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 3 | **type** `record Figure(SpreadPlanner.Candidate c, double hurdle, double tariff)` | What the planner reads of 154 in a city: its candidate, the hurdle on its whole cost at Heavy Industry's own gate, and the tariff the planner bills its power at. |
| 220 | 1 | `boolean clears()` _(in HydrogenSteelCheck.Figure)_ |  |
| 223 | 7 | `static Figure figure(Game g)` |  |
| 238 | 11 | `static Game paysTown(String label, double tariff, boolean importsSteel)` | A city for section 3: the 1900 town at FE4's month, its labour handed, the grid billing `tariff` (NaN: the model's own, the utility's) and, with `importsSteel`, the city's fabricators having bought the works' whole na... |
| 251 | 6 | `static String ranked(List<SpreadPlanner.Candidate> all)` | The candidates the planner ranks, best earnings on the whole cost first, as text. |
| 258 | 63 | `static void wherePays()` |  |

### 4. captive pellets (lines 322-410)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 86 | `static void capturedPellets()` |  |

### 5. nothing moves without the works (lines 411-459)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 46 | `static void nothingMoves()` |  |

### 6. a played count (lines 460-510)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 5 | `static double dearest(Game g)` | The dearest of Heavy Industry's candidates' whole cost: what the sector's till is topped to when its money gate is set aside. |
| 470 | 15 | `static long played(Game g, boolean freed)` | The 154s held (built and ordered) after the played months, with the money gate its own or set aside (RefineryCheck's lever: ON_ITS_BORROWING and the dearest cost in the till). |
| 486 | 24 | `static void aPlayedCount()` |  |

