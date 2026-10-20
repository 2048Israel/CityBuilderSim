# CoalMining.java - 445 lines · 23 methods · 7 constants · sectors

`ham/citybuildersim/sectors/CoalMining.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

> Coal mines and the plants that wash their coal - Coal Mining (0.7.126,
> batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the
> study's 3): the second chain's digging industry, the first sector added on
> the end of Sectors.KEYS since the refinery (Jerus's Q21, 2026-10-08: iron,
> coal, stone, copper, wood).
> 
> WHY. The integrated route (0.7.123, batch I4) brought the coals as goods -
> THERMAL_COAL for the pellet plants and the blast furnaces, COKING_COAL for
> the coke ovens - and nobody in the city made either, so every tonne was
> imported. The world's coal lies in fields of two types (the study's 1.4):
> thermal, sub-bituminous, sold as dug (85% of fields), and coking,
> bituminous, which must be washed to sell as coking coal (15%). So a mine
> stands on its type (ExtractionSector, BY TYPE AND SIZE), and a coking
> mine's coal is RAW until a Coal Preparation Plant washes it - a separate
> building, Jerus's Q9 - which is this class's one rule of its own.
> 
> THE FOUR BUILDINGS (the study's 3.3; ids 111 to 114):
>   - the SURFACE COAL MINE, 100,000 t of thermal coal a month on one thermal
>     site, on diesel;
>   - the LARGE SURFACE MINE, a dragline pit, 800,000 t on four sites of one
>     thermal field;
>   - the UNDERGROUND COAL MINE, a longwall, 250,000 t of RAW coal on one
>     coking site;
>   - the COAL PREPARATION PLANT, which washes 500,000 t of raw coal a month
>     (its template's `wash`) and buys and makes no good of its own.
> 
> THE FLOW (flow(), pure; the spec's 2.10). Raw coal is not a good - it is a
> stream inside the sector, as the refinery's cuts are (RefineryFlow), so
> there is nothing to price it at and nobody else may buy it:
>   raw     = what the mines on COKING sites make;
>   washed  = min(raw, the plants' wash);
>   COKING  = COKING_YIELD x washed (.55);
>   THERMAL = what the other mines make + THERMAL_YIELD x washed (.20, the
>             middlings) + (raw - washed), sold as thermal tonne for tonne;
>   spoil   = the rest of what was washed (.25).
> The study's figures: 500,000 t raw through one plant is 275,000 t of coking
> coal, 100,000 of thermal and 125,000 of spoil. getCapacity() reads the flow
> at nameplate, so an Underground Coal Mine with no plant beside it sells its
> raw coal as thermal, tonne for tonne (0.8% a month on its cost, the study's
> 3.4), and with one the pair earns 2.4%.
> 
> THE GROUND. Both coals are stockable, so the flow goods' groundLimit()
> never sees them: a month's coal is what the mines can dig at the sector's
> rate (lift(): each type's ask, times its dig - 1/.95 tonnes of seam a
> tonne at the surface, 1/.80 underground - held to what its type still has
> in the ground), the flow of THAT the figure the market is priced on and
> the plant makes (Sector.madeAtRate()), and the same dig taken out of the
> ground at the end of the month (endOfMonth(): LandManager.extract(r, type,
> t), so W holds by type).
> 
> THE PLAN. A mine is ExtractionSector's: the best earnings on the whole cost
> among the mines that fit a field of their type, their freight in it. And
> the sector builds a preparation plant beside its coking mines (the study's
> 3.4) - first, whenever raw coal standing or on site goes unwashed and the
> plant would staff and pay (planWash()). What one more of any of the four
> would earn is its flow's increment (madeBy()): the thermal a plant stops
> selling raw is a loss to it, the coking coal it makes its gain.
> 
> Who buys: Iron & Steel's coke ovens the coking coal, its pellet plants and
> blast furnaces the thermal (I4); Cement & Concrete's kilns and cement
> ... (2 more lines in the source)

**Uses:** [Good](Good.md) (28), [BusinessInvestment](BusinessInvestment.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Resource](Resource.md) (5), [Deposit](Deposit.md) (4), [Game](Game.md) (4), [LandManager](LandManager.md) (2), [Formats](Formats.md) (2), [BuildCard](BuildCard.md) (2), [ExtractionSector](ExtractionSector.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [SpreadPlanner](SpreadPlanner.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (9):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 119 | · the flow |
| 192 | · the ground |
| 284 | · the estimate |
| 344 | · the plan |
| 398 | · the page |
| 428 | THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `CoalMining.SURFACE_MINE` | `111` | The Surface Coal Mine's permanent id in buildings.json (0.7.126): 100,000 t of thermal coal a month on one thermal site. |
| 91 | `CoalMining.LARGE_SURFACE_MINE` | `112` | The Large Surface Mine's (0.7.126): a dragline pit, 800,000 t of thermal coal a month on four sites of one thermal field. |
| 94 | `CoalMining.UNDERGROUND_MINE` | `113` | The Underground Coal Mine's (0.7.126): a longwall, 250,000 t of raw coal a month on one coking site. |
| 97 | `CoalMining.PREPARATION_PLANT` | `114` | The Coal Preparation Plant's (0.7.126): washes 500,000 t of raw coal a month. |
| 100 | `CoalMining.COKING_YIELD` | `.55` | The coking coal a tonne of raw coal washes into: .55, the study's 3.3 (275,000 t of 500,000). |
| 103 | `CoalMining.THERMAL_YIELD` | `.20` | ...the thermal coal it washes into, the middlings: .20, the study's 3.3 (100,000 t of 500,000). |
| 106 | `CoalMining.SPOIL_YIELD` | `.25` | ...and the spoil, the rest, to the tip: .25, the study's 3.3 (125,000 t of 500,000). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 361 | **type** `public final class CoalMining extends ExtractionSector` | Coal mines and the plants that wash their coal - Coal Mining (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the study's 3): the second chain's digging industry, the first sector added on ... |
| 108 | 10 | `public CoalMining()` |  |

### the flow (lines 119-191)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 6 | **type** `public record Flow(double surface, double raw, double wash, double washed, double coking, double thermal, d...` | A month of the sector's coal (pure): the surface mines' thermal, the coking mines' raw, the plants' wash, what of the raw was washed, and the two coals and the spoil that came of it, in tonnes. |
| 128 | 3 | `public double of(Good g)` _(in CoalMining.Flow)_ | The tonnes of a good it makes: COKING_COAL or THERMAL_COAL; nothing of any other. |
| 140 | 6 | `public static Flow flow(double surface, double raw, double wash)` | The flow (pure; the spec's 2.10): `surface` t of thermal coal as dug, `raw` t of a coking field's raw coal, through plants that wash `wash` t: washed = min(raw, wash); COKING_YIELD of it coking, THERMAL_YIELD thermal,... |
| 148 | 4 | `public static boolean digsRaw(BuildingsTemplate t)` | Whether a building digs a coking field's raw coal: it stands on a COKING site. |
| 154 | 4 | `static double[] streamsOf(BuildingsTemplate t)` | A building's part of the flow's streams at nameplate: {surface, raw, wash}. |
| 160 | 10 | `double[] streams(boolean onSite)` | The streams of the plant standing, and with `onSite` what is on site too: {surface, raw, wash} at nameplate. |
| 172 | 4 | `public Flow flow()` | The month's flow at nameplate, of the plant standing: what getCapacity() reads. |
| 179 | 4 | `public double getCapacity(Good g)` | Nameplate of either coal a month: the flow's (0.7.126) - with no plant standing a coking mine's raw coal is thermal, tonne for tonne. |
| 186 | 5 | `public double getPipeline(Good g)` | ...and what the buildings on site will add to it, which counts as supply for the planner: the flow with them less the flow without, never below nothing (a plant on site makes coking coal of what was thermal). |

### the ground (lines 192-283)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 1 | **type** `record Kind(Deposit.FieldType type, double dig, boolean raw, double capacity)` | One kind of dig its mines make: the type of field they stand on (null for ANY), the tonnes of field a tonne takes, whether its coal is raw, and their nameplate. |
| 198 | 16 | `List<Kind> kinds()` | Its mines standing, by the type of field they stand on and their dig, each kind's nameplate, in the catalogue's order. |
| 221 | 18 | `double[] dug(List<Kind> kinds)` | What each kind of its mines digs this month (pure): its nameplate at the sector's rate, held to what its type still has in the ground - the kinds on one type sharing it in proportion to the seam they ask for - in tonn... |
| 247 | 11 | `public Flow lift()` | The month's coal at the sector's rate, as far as the ground holds it (pure): each kind's dig (dug()) through the flow, the plants washing their wash at the same rate. |
| 261 | 4 | `protected double madeAtRate(Good g)` | Either coal this month: the lift's, the ground's limit in it (Sector.madeAtRate()). |
| 272 | 11 | `public void endOfMonth(Game game)` | The month's coal out of the ground, once every good is made: each kind's dig (dug(), the figures the month's coal was made of) taken out of its type's ground at its dig, so E and W hold by type to the tonne. |

### the estimate (lines 284-343)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 11 | `public Map<Good, Double> madeBy(BuildingsTemplate t)` | What one more of `t` adds to the flow at nameplate, on top of what stands and is on site: {good: tonnes}, a tonne a plant stops selling raw as thermal a negative tonne of thermal. |
| 315 | 12 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more of its buildings would clear a month: ExtractionSector's estimate on what it adds to the flow (madeBy()), not its template's makes - its gains through BusinessInvestment.estimatedMakerProfit(), the therm... |
| 335 | 8 | `public double lostValue(Good g, double units, BusinessInvestment plans)` | What `units` of a good the sector would no longer make were worth to it: the mirror of estimatedMakerProfit()'s split - off what it sends abroad first, at the net export price (its make past the planners' forecast of ... |

### the plan (lines 344-397)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 4 | `public double unwashed()` | The raw coal its coking mines standing and on site make past what its plants standing and on site wash, a month at nameplate. |
| 358 | 4 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to open another mine, or a plant to wash the raw coal: the plant first (planWash()), whenever raw coal goes unwashed and one would staff and pay; otherwise ExtractionSector's rule for a mine. |
| 372 | 25 | `BusinessInvestment.Decision planWash(BusinessInvestment plans, Game game)` | A preparation plant beside the coking mines (the study's 3.4: "the sector builds one beside every coking mine"): with no order in flight and raw coal standing or on site that no plant washes, the plant that earns most... |

### the page (lines 398-427)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 26 | `public List<Line> ownLines(Game game)` |  |

### THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and (lines 428-445)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 3 | `public boolean isWeighed(BuildingsTemplate t)` | Whether the plan weighs a building of its: a mine that lifts its coal, and since its plants are planned too (planWash()), a plant that washes it. |
| 441 | 4 | `protected String groundRefusal(BuildingsTemplate t)` | A wash plant's ground is the raw coal: planWash() weighs one only while raw coal standing or on site goes unwashed (unwashed()); a mine's is ExtractionSector's. |

