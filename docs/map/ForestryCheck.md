# ForestryCheck.java - 704 lines · 21 methods · 22 constants · harnesses

`ham/citybuildersim/ForestryCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1,
> 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research.md
> - 1.3, 5 and 10's Q13 to Q15; the zoning study's - spec-zoning.md - 2, 3 and
> 8.4, and Jerus's A12): the fifth materials chain's cutting industry, on the
> end of Sectors.KEYS after Copper Refining.
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
> CoalMiningCheck to I8's CopperMiningCheck), and forestry is the chain that
> is not a mine: no sites and no cutting limit (Q14), a crew's room set by
> how long the forest it may cut would last (E), a forest that grows back
> (Q13) and is planted on the ground the city zones for it (Q15, B), and a
> building's clearing sold as a salvage cut (Z7). ZoningCheck keeps the law
> and the clearing; this keeps what Forestry makes of them.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: Forestry the twenty-third key, after
>      Copper Refining (Wood Products after it since 0.7.140); an ExtractionSector on the forest making sawlogs
>      and pulpwood; what is as long as the keys grew with them; the two
>      goods at the study's 5.3 prices, freight three eighths of each wedge,
>      by the cubic metre at 0.9 t, flow goods; Resource.FOREST priced as
>      pulpwood at 2% in the ground, so an offer's forest is in its price,
>      and bare ground still means no deposit (LandMarket.bareGround()).
> 
>   2. THE FOUR ROWS (ids 129 to 132, at parity): the study's 5.4 - cost,
>      posts, what each cuts, its running cost - and its 5.5 table's four
>      cases to its rounding; the Woodlot a tenth of the Tree Plantation in
>      ground and growth (A12: 167 m3 a month), and losing in every case.
> 
>   3. E, THE CREWS' RULE: a crew only while the cuttable forest lasts
>      FOREST_CREW_MONTHS of every crew's cut with it - a 10 km2 forest gets
>      none, a Logging Camp needs 17.5 km2, a Harvesting Operation 109 km2 -
>      and in a town the plan holds with E's words under it and picks among
>      the crews E allows past it.
> 
>   4. THE CUT: a crew's month takes its two goods out of the cuttable
>      forest together, E rising by it and W exact; the forest law's
>      protected wood never cut, a forest running short holding both goods
>      alike; spare once it is gone.
> 
>   5. REGROWTH: E falls by FOREST_REGROWTH and the planted stands' growth -
>      a plantation's 1,000 ha at PLANTED_M3_A_HA_A_YEAR, a woodlot's 100 -
>      never below nothing, so a stand never grows timber past the forest's
>      full stock, and W stays exact; with no stand the month is what it was.
> 
>   6. PLANTING: only on Forestry's zone - no zone, no plantation - a Tree
>      Plantation where it holds 1,000 ha, a Woodlot where it holds 100 but
>      not 1,000, nothing under 100; only while the cut exceeds the
>      regrowth; at the zone's price, from Forestry's cash, never borrowed.
> 
>   7. THE SALVAGE CUT: a building on open forest clears its standing wood,
>      which Forestry sells that month, half sawlogs and half pulpwood,
>      billed by the builders at its crews' running cost a cubic metre (a
>      Harvesting Operation's with none standing); none lost - a clearing
>      after the month's market and across a save is sold the month after;
>      W exact.
> 
>   8. WHERE THE FOREST SETTLES (Q13): the study's mockup 4 - 25.4M m3 cut by
>      75,000 a month settle at 29.1% standing, and at 40% with 40%
>      protected - by the regrowth rule's own constant.
> ... (8 more lines in the source)

**Uses:** [Forestry](Forestry.md) (80), [Good](Good.md) (53), [LandManager](LandManager.md) (25), [Sectors](Sectors.md) (21), [Game](Game.md) (16), [BuildingManager](BuildingManager.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (9), [SpreadPlanner](SpreadPlanner.md) (6), [GameFiles](GameFiles.md) (6), [Zoning](Zoning.md) (5), [Resource](Resource.md) (4), [LandMarket](LandMarket.md) (4), [LandParcel](LandParcel.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Equity](Equity.md) (3), [MoneyAudit](MoneyAudit.md) (3), [CityLand](CityLand.md) (3), [JobType](JobType.md) (3), [Sector](Sector.md) (3), [BuildingType](BuildingType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Traffic](Traffic.md) (2), [Ports](Ports.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [PayTier](PayTier.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [World](World.md) (1), [Markets](Markets.md) (1), [ExtractionSector](ExtractionSector.md) (1), [Bank](Bank.md) (1)... and 4 more

**Used by (4):** [BuildCardCheck](BuildCardCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 133 | 1. the sector on the registry |
| 209 | 2. the four rows against the study |
| 305 | 3. E, the crews' rule |
| 406 | 4. the cut |
| 448 | 5. regrowth |
| 485 | 6. planting |
| 568 | 7. the salvage cut |
| 621 | 8. where the forest settles |
| 644 | 9. a played city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `ForestryCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 87 | `ForestryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it. |
| 123 | `ForestryCheck.F` | `Resource.FOREST` |  |
| 126 | `ForestryCheck.M3_A_KM2` | `World.FOREST_M3_PER_KM2` | Timber a square kilometre of forest holds: the world's (World.FOREST_M3_PER_KM2, Canada's 137 m3 a hectare). |
| 136 | `ForestryCheck.GOODS` | `{ Good.SAWLOGS, Good.PULPWOOD }` | Wood's two goods, the study's 5.3: import and export price, D$ thousands a cubic metre. |
| 138 | `ForestryCheck.STUDY_IMPORT` | `{.110,.090 }` | ...each one's import price (pulpwood's widened from 70, the study's 1.2)... |
| 140 | `ForestryCheck.STUDY_EXPORT` | `{.065,.035 }` | ...and its export price. |
| 142 | `ForestryCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 144 | `ForestryCheck.GREEN_WOOD` | `.9` | Green roundwood, tonnes a cubic metre (spec 2.3, est.). |
| 146 | `ForestryCheck.IN_GROUND` | `.02` | The share of forest's pulpwood price its standing timber sells for in the ground (the study's 8). |
| 212 | `ForestryCheck.ROWS` | `{ Forestry.LOGGING_CAMP, Forestry.HARVESTING_OPERATION, Forestry.TREE_PLANTAT...` | Forestry's rows, by their permanent ids, in the study's 5.4 order and the woodlot after. |
| 215 | `ForestryCheck.STUDY_COSTS` | `{ 3_000, 15_000, 2_000 }` | The study's 5.4 for the first three: cost, D$ thousands all in. |
| 217 | `ForestryCheck.STUDY_POSTS` | `{ 11, 18, 5 }` | ...posts. |
| 219 | `ForestryCheck.STUDY_SAWLOGS` | `{ 1_000, 6_250, 0 }` | ...sawlogs a month, m3 (and as much pulpwood). |
| 221 | `ForestryCheck.STUDY_RUNS` | `{ 61, 217, 24 }` | ...its running cost a month, D$ thousands ("Runs"). |
| 223 | `ForestryCheck.STUDY_TABLE` | `{ { 1.3, 1.3, 4.6, 4.6 }, { 2.7, 2.7, 6.9, 6.9 }, { - 1.2, - 1.2, - 1.2, - 1....` | The study's 5.5 table, % of the cost a month, cases A to D. |
| 225 | `ForestryCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 227 | `ForestryCheck.PLANTATION_HA` | `1_000, WOODLOT_HA = 100` | A plantation's hectares (the study's 5.4) and a woodlot's (A12). |
| 229 | `ForestryCheck.SINCE` | `{ 1900, 1973, 1900, 1900 }` | The year each first existed: 1900 - standing before the start - but the Harvesting Operation, 1973 (research-design.md E14, PIKA 75). |
| 404 | `ForestryCheck.TOWN_KM2` | `1_000` | The ground a forest town is restated to hold past its founding, km2: enough for a Harvesting Operation's 109 km2 of forest at the default world's share. |
| 624 | `ForestryCheck.MOCKUP_FULL` | `25_400_000, MOCKUP_CUT = 75_000` | The study's mockup 4 (its 5.2): the natural woods' full stock, m3, and the month's cut. |
| 626 | `ForestryCheck.MOCKUP_SETTLES` | `.291, MOCKUP_PROTECTED =.40` | ...where they settle: 29.1% standing, and at 40% with 40% protected (spec-zoning 3.3; spec 2.11). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 624 | **type** `public class ForestryCheck` | Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research.md - 1.3, 5 and 10's Q13 to Q15; the zoning study's - spec-zoning.md ... |
| 89 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 94 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 99 | 5 | `static void quietly(Runnable r)` |  |
| 106 | 1 | `static boolean same(double a, double b)` | Two wood figures the same to a part in a billion of the larger: one rounding, not a cubic metre. |
| 108 | 14 | `public static void main(String[] args) throws Exception` |  |
| 129 | 3 | `static boolean w(LandManager land)` | W for the forest: the world's total equals what no city owns, what remains and what was taken out, to a rounding. |

### 1. the sector on the registry (lines 133-208)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 60 | `static void sector()` |  |

### 2. the four rows against the study (lines 209-304)

| line | len | member | says |
|---:|---:|---|---|
| 231 | 73 | `static void rows()` |  |

### 3. E, the crews' rule (lines 305-405)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 55 | `static void crewsRule()` |  |
| 368 | 6 | `static void protect(Game g, double cuttable)` | Sets the forest law's protected share - kept as large as the protected share, so the law keeps no working forest past it - to leave `cuttable` cubic metres that may be cut, or the whole forest. |
| 381 | 3 | `static Game forestTown(String label)` | A town to cut forest in: the Wealthy preset's cash and the founding default's works, its ground restated round the default world's founding site to 4,000 km2 more (LandManager.setOwnedSqFt()), forest and bare ground b... |
| 386 | 16 | `static Game forestTown(GameFiles files)` | ...in these files, so a harness can load what it saves. |

### 4. the cut (lines 406-447)

| line | len | member | says |
|---:|---:|---|---|
| 408 | 39 | `static void theCut()` |  |

### 5. regrowth (lines 448-484)

| line | len | member | says |
|---:|---:|---|---|
| 450 | 27 | `static void regrowth()` |  |
| 479 | 5 | `static LandManager office(double km2)` | A land office on its own whose ground is drawn round the default world's founding site to this many square kilometres. |

### 6. planting (lines 485-567)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 66 | `static void planting()` |  |
| 555 | 6 | `static void cutAMonth(Game g)` | The month's cut, as the market's first step makes it: both goods' produceFlow(). |
| 563 | 4 | `static int onOrder(BuildingManager b)` | Planted stands on site. |

### 7. the salvage cut (lines 568-620)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 50 | `static void salvage()` |  |

### 8. where the forest settles (lines 621-643)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 15 | `static void settles()` |  |

### 9. a played city (lines 644-704)

| line | len | member | says |
|---:|---:|---|---|
| 646 | 58 | `static void played()` |  |

