# SulphideLeachCheck.java - 404 lines · 18 methods · 8 constants · harnesses

`ham/citybuildersim/SulphideLeachCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> The Sulphide Leach & SX-EW Plant, checked (R6b, FE2: sulphide heap leaching, Johnson Camp, Arizona, Nov 2025): the near-future row that lets a sulphide
> field - four copper fields in five - yield cathode at the mine, with no concentrator and no smelter, in a city that knows it.
> 
> WHY. Copper Mining's machinery has kept a kind of dig by (type, dig) since 0.7.133 (ExtractionSector.digs()), so a plant of the oxide plant's figures
> on a sulphide site should run on it with no new code - but a city founded today's way must never know it (R6b's call 1) and a city founded the 1900
> way must know it from FE2's month and not before, and a building the sector cannot see (digs() reads the candidates, which are the known ones) would
> make nothing at all. So this harness proves the row, the rule that knows it, what it does standing and played, and what the planner makes of it.
> 
> What this has to prove:
>   1. THE ROW. Template 153 stands on one sulphide site at SULPHIDE_LEACH_DIG (better than the oxide plant's LEACH_DIG: the builder's 85% against 72%),
>      makes cathode and no concentrate, takes acid and costs, staffs, powers and takes land as the oxide plant does, is dated "FE2" (near-future), and its
>      water is 71 L a kg of its cathode in the game's unit of 10,000 US gallons.
>   2. KNOWN BY THE RESEARCH. A city founded the 1900 way does not have it among Copper Mining's candidates the month before FE2's and does at it; a city
>      founded today's way never does, and refuses an order for it, past FE2's month.
>   3. STANDING AND PLAYED. Built on a sulphide field of a 1900 city at FE2's month it makes its nameplate of cathode at the sector's rate and bids for its
>      acid; at the month's end the sulphide ground gives up SULPHIDE_LEACH_DIG a tonne of it and the oxide ground nothing, W exact; a played month makes
>      cathode, buys acid abroad (no smelter stands), makes no concentrate, and the audit closes; the sector's page says each kind of plant's cathode
>      under its own ground (CopperMining.ownLines()).
>   4. THE PLAN. In a town founded on a sulphide field (no oxide ground, so the oxide plant is not in the race) the planner's earnings on the whole cost,
>      153's and the Mine & Mill's, are printed, and it orders whichever its own rule ranks first - at the sector's own money gate and with the gate set
>      aside; 153's estimate is the maker's less its consumables and freight, to the bit.
>   5. A PLAYED COUNT. That town played two years from FE2's month with the planner free, its money gate its own and then set aside (RefineryCheck's lever):
>      the 153s ordered and built are counted and printed, none asserted; a city founded today's way, played the same months, orders none; the audit
>      closes and W holds by type in each.
> 
> Every fixture causes its condition: a town founded on the copper field nearest the default site of the type the section wants (CopperMiningCheck.
> nearest(): oxide, with its sulphide ground by fiat, for 2 and 3, which then show the oxide ground untouched; sulphide for 4 and 5), the 1900 town's
> clock set at FE2's month by ResearchCheck.atMonth() and the buildings placed by hand.

**Uses:** [CopperMining](CopperMining.md) (40), [Good](Good.md) (24), [Game](Game.md) (21), [BuildingsTemplate](BuildingsTemplate.md) (12), [BuildingManager](BuildingManager.md) (11), [Sectors](Sectors.md) (11), [BusinessInvestment](BusinessInvestment.md) (6), [Deposit](Deposit.md) (5), [LandCheck](LandCheck.md) (5), [ResearchCatalog](ResearchCatalog.md) (4), [CopperMiningCheck](CopperMiningCheck.md) (4), [LandManager](LandManager.md) (4), [CityCalendar](CityCalendar.md) (4), [Formats](Formats.md) (3), [MaterialsCheck](MaterialsCheck.md) (3), [Sector](Sector.md) (3), [MoneyAudit](MoneyAudit.md) (3), [SpreadPlanner](SpreadPlanner.md) (3), [Resource](Resource.md) (2), [Founding](Founding.md) (2), [World](World.md) (2), [CityLand](CityLand.md) (2), [CopperRefining](CopperRefining.md) (2), [ResearchCheck](ResearchCheck.md) (1), [GameFiles](GameFiles.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

## Sections

| line | section |
|---:|---|
| 132 | 1. the row |
| 167 | 2. known by the research |
| 201 | 3. built on a sulphide field |
| 290 | 4. the plan |
| 365 | 5. a played count |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `SulphideLeachCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 47 | `SulphideLeachCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 76 | `SulphideLeachCheck.CU` | `Resource.COPPER` |  |
| 77 | `SulphideLeachCheck.SULPHIDE` | `Deposit.FieldType.SULPHIDE, OXIDE = Deposit.FieldType.OXIDE` |  |
| 80 | `SulphideLeachCheck.LITRES_A_KG` | `71` | The builder's water, litres a kilogram of cathode (Johnson Camp: "71 litres per kilogram copper", the source of the row's water). |
| 82 | `SulphideLeachCheck.KG_A_TONNE` | `1000` | Kilograms in a tonne, the unit of the row's nameplate. |
| 84 | `SulphideLeachCheck.LITRES_A_WATER_UNIT` | `37_854` | The game's unit of water, litres: 10,000 US gallons (UtilitiesHandler's units; WaterCheck's 37.854 m3) - the model keeps no constant for it. |
| 86 | `SulphideLeachCheck.PLAYED_MONTHS` | `24` | The months section 5 plays from FE2's: two years. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 364 | **type** `public class SulphideLeachCheck` | The Sulphide Leach & SX-EW Plant, checked (R6b, FE2: sulphide heap leaching, Johnson Camp, Arizona, Nov 2025): the near-future row that lets a sulphide field - four copper fields in five - yield cathode at the mine, w... |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 54 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 59 | 5 | `static void quietly(Runnable r)` |  |
| 65 | 10 | `public static void main(String[] args) throws Exception` |  |
| 89 | 1 | `static int fe2()` | FE2's month in the game's count, from the catalogue. |
| 92 | 3 | `static void at(Game g, int month)` | The clock set at a month (ResearchCheck.atMonth()), its checked exception the harness's own. |
| 101 | 24 | `static Game town1900(String label, Deposit.FieldType founded, int sulphideSites)` | CopperMiningCheck.copperTown() for a city founded the 1900 way (Founding.withUnfitted(true)) with its clock at FE2's month before its works are built, so that the whole catalogue is known: founded on the copper field ... |
| 127 | 4 | `static long held(BuildingManager b, BuildingsTemplate t)` | The standing and the ordered of a template: what is built plus what is under construction. |

### 1. the row (lines 132-166)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 32 | `static void theRow()` |  |

### 2. known by the research (lines 167-200)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 31 | `static void knownByTheResearch()` |  |

### 3. built on a sulphide field (lines 201-289)

| line | len | member | says |
|---:|---:|---|---|
| 203 | 80 | `static void standingAndPlayed()` |  |
| 286 | 4 | `static String pageValue(CopperMining cm, Game g, String label)` | The value of the line of a sector's page with the label, or null when the page has none. |

### 4. the plan (lines 290-364)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 1 | **type** `record Ranking(BuildingsTemplate best, double leach, double mill, String each)` | What the planner's own rule ranks first among Copper Mining's candidates that fit and pass: the best earnings on the whole cost, and each one's. |
| 296 | 18 | `static Ranking rank(Game g)` | CopperMiningCheck.plan()'s loop, over the candidates that fit and are staffed and, for `best`, pass the sector's money gate. |
| 316 | 7 | `static double dearest(Game g)` | The dearest of Copper Mining's candidates' whole cost: what the sector's till is topped to when its money gate is set aside. |
| 329 | 5 | `static void freeTheGate(Game g)` | The sector's money gate set aside, as RefineryCheck sets its planner's: ON_ITS_BORROWING, with the dearest unit's cost in the sector's till, so the order's path is what is tested and not whether a mine's earnings clea... |
| 335 | 29 | `static void thePlan()` |  |

### 5. a played count (lines 365-404)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 15 | `static long[] played(Game g, boolean freed)` | The plants held (built and ordered) after the played months, in a city: the sulphide leach plant, the mine and mill, the oxide leach plant. |
| 384 | 20 | `static void aPlayedCount()` |  |

