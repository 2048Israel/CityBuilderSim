# PowerDispatch.java - 288 lines · 17 methods · 19 constants · model

`ham/citybuildersim/PowerDispatch.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> One month's merit order for the city's grid: the rows that make power, the load a month asks of them cut in six half-blocks,
> and the price each half-block clears at.
> 
> WHY. The utility used to set the month's power by one sum - production over consumption, capped at 1 - so a Coal Power Plant
> burned nothing, a Wind Farm counted as if the wind never stopped, a short month was short by the same fraction at every hour,
> and the tariff was a constant (D$10 a kW a month). The electricity study (docs/studies/spec-electricity.md, sections 2.2, 2.5,
> 2.6, 2.7; prototype in docs/studies/electricity) cuts the month in three load blocks, each in a windy and a calm
> half, runs the works' surplus first, wind next, hydro levelling the load with its month of water, and the fuelled rows by
> their marginal cost, and prices each half-block at the last row it ran (or at VOLL when it is left short). Jerus took that
> study with the price the market clears (his Q1, 2026-11-02: "Market price"), so the tariff follows from the fleet and the
> fuel's price. This class is the model alone: no Game, no I/O; UtilitiesHandler builds the rows, strikes the book, and bills.
> 
> Power in kW (a half-block's average), energy in kWh, money in the city's, per kWh; a fixture with no calendar passes season 1.

**Uses:** [Good](Good.md) (5), [PriceLevel](PriceLevel.md) (3)

**Used by (16):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildingsTemplate](BuildingsTemplate.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [HydroCheck](HydroCheck.md), [NewUnitCheck](NewUnitCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [UtilitiesHandler](UtilitiesHandler.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 27 | FIGURES |
| 88 | THE ROWS |
| 165 | THE CALENDAR AND THE WEATHER |
| 180 | THE MONTH |

## Enum constants

| line | constant | says |
|---:|---|---|
| 91 | `PowerDispatch.Kind.MUST_RUN` |  |
| 91 | `PowerDispatch.Kind.WIND` |  |
| 91 | `PowerDispatch.Kind.HYDRO` |  |
| 91 | `PowerDispatch.Kind.FUELLED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `PowerDispatch.MONTH_HOURS` | `720` | Hours in the game's month [D: a row's production1 is its month's kWh over 720, the average kW; HY1's arithmetic]. |
| 33 | `PowerDispatch.BASE` | `0, SHOULDER = 1, PEAK = 2` | The three load blocks, base to peak [D: spec 2.2]. |
| 36 | `PowerDispatch.BLOCKS` | `3, HALVES = 6` | Three blocks, each in two halves (the first windy, the second calm): six half-blocks, half-block s being block s / 2 [D: spec 2.2]. |
| 39 | `PowerDispatch.BLOCK_HOURS` | `{ 0.50, 0.40, 0.10 }` | The blocks' share of a month's hours, base to peak [E: the 10/40/50 cut, spec 2.2; the prototype, line 28]. |
| 42 | `PowerDispatch.BLOCK_LOAD` | `{ 0.904, 1.07, 1.20 }` | The blocks' load over the month's mean [D: EIA-930 2025, the lower 48, each month's own hours cut 10/40/50 and averaged: 1.20, 1.07, 0.90; the base set to 0.904 so the energy sums to the month's: 0.5 x 0.904 + 0.4 x 1... |
| 45 | `PowerDispatch.SEASON` | `{ 1.076, 0.998, 0.879, 0.880, 0.919, 1.088, 1.193, 1.119, 1.015, 0.911, 0.907...` | Each calendar month's mean over the year's [D: EIA-930 2025, the lower 48, the prototype, line 36], January first. |
| 48 | `PowerDispatch.WIND_BLOCK` | `{ 1.081, 0.948, 0.806 }` | Wind's output over its mean in each block, base to peak [M: EIA-930 2025, the lower 48; the prototype, line 95]. |
| 51 | `PowerDispatch.WIND_SWING` | `{ 0.64, 0.61, 0.59 }` | How far wind swings inside a block, over the block's average: the windy half at (1 + swing), the calm half at (1 - swing) [M: EIA-930 2025, the coefficient of variation of hourly output inside each load block, median ... |
| 54 | `PowerDispatch.GRID_LOSSES` | `0.05` | The share of the grid lost between the plant and the meter [S: EIA FAQ 105, about 5%; spec 2.7]. |
| 57 | `PowerDispatch.VOLL_A_KWH` | `3.0e-3 * PriceLevel.FACTOR` | The most a short half-block clears at, $3,000 a MWh [E: the value of lost load, spec 6], in thousands of founding dollars a kWh (the game's money is thousands: today's tariff .01 x FACTOR is D$10). |
| 60 | `PowerDispatch.TRANSMISSION_A_KWH` | `0.01885e-3 * PriceLevel.FACTOR` | The long wires' charge a kWh delivered, 1.885 cents [S: AEO2026 table 8, 2025; spec 2.6-2.7], in thousands of founding dollars. |
| 63 | `PowerDispatch.DISTRIBUTION_A_KWH` | `0.04053e-3 * PriceLevel.FACTOR` | The street wires' charge a kWh delivered, 4.053 cents [S: AEO2026 table 8, 2025; spec 2.6-2.7], in thousands of founding dollars. |
| 66 | `PowerDispatch.MJ_PER_BTU` | `0.001055056` | Megajoules in a Btu, so a heat rate in Btu a kWh is MJ a kWh [D: 1,055.056 J a Btu]. |
| 69 | `PowerDispatch.COAL_MJ_PER_TONNE` | `25_120` | Megajoules in a tonne of thermal coal [E: the Pink Sheet's Newcastle grade, 6,000 kcal/kg = 25.12 GJ a tonne, spec 2.5]. |
| 72 | `PowerDispatch.PLAN_PEAK` | `BLOCK_LOAD [ PEAK ] /(1 - GRID_LOSSES)` | The draw a firm kW must cover in the peak block, losses in [D: BLOCK_LOAD at the peak over (1 - GRID_LOSSES)]: what the build advice plans power against. |
| 75 | `PowerDispatch.WIND_FIRM` | `windFirm()` | What a wind farm's average delivers in its calmest half-block [D: the least of WIND_BLOCK x (1 - WIND_SWING) over the blocks: 0.806 x 0.41 = 0.330]. |
| 185 | `PowerDispatch.SHORT_KW` | `1e-9` | A half-block left with more than this much unserved is short, kW. |
| 188 | `PowerDispatch.LEVEL_STEPS` | `40` | Halvings of the water's level, as the prototype loops them. |
| 191 | `PowerDispatch.LEVEL_EPS` | `1e-6` | The level's resolution, kW. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `public final String name` |  |
| 96 | `public final Kind kind` |  |
| 98 | `public final double kw` | MUST_RUN and FUELLED: kW offered in every half-block; WIND: its average kW; HYDRO: its cap, kW. |
| 100 | `public final double waterKwh` | HYDRO: its month of water, kWh; 0 for the rest. |
| 102 | `public final double marginal` | FUELLED: city money a kWh it costs to run (fuel + variable upkeep); 0 for the rest. |
| 104 | `public final Good fuel` | FUELLED with a fuel; null for the rest. |
| 106 | `public final double fuelPerKwh` | Units of fuel it burns a kWh; 0 without. |
| 108 | `public final double upkeepPerKwh` | Its variable upkeep, thousands of the WORLD's money a kWh (paid abroad later); 0 without. |
| 110 | `public final double[] gen` | kW it ran in each half-block (set by dispatch()). |
| 112 | `public double kwh, fuelUnits, earned` | The month's kWh, fuel units, and kWh x its half-blocks' prices (set by dispatch()). |
| 140 | `private List<Integer> meritOrder` | The actual dispatch order, including idle rows; observation only, never read by the dispatch. |
| 157 | `public final double[] load` | Per half-block: the load asked (kW, losses in), what was left unserved (kW), and the price it cleared at (city money a kWh). |
| 158 | `public double askedKwh, servedKwh, curtailedKwh` |  |
| 160 | `public double ratio` | servedKwh / askedKwh, 1 when nothing is asked. |
| 162 | `public double energyPrice` | City money a kWh DELIVERED: the sum over half-blocks of served kWh x price, over servedKwh, over (1 - GRID_LOSSES); 0 when nothing is served. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 266 | **type** `public final class PowerDispatch` | One month's merit order for the city's grid: the rows that make power, the load a month asks of them cut in six half-blocks, and the price each half-block clears at. |
| 25 | 1 | `private PowerDispatch()` |  |

### FIGURES (lines 27-87)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 5 | `private static double windFirm()` |  |
| 84 | 3 | `public static double heatContentMJ(Good g)` | MJ of fuel energy in a unit of a good (HHV), NaN for a good the model does not yet burn (PW2 adds the rest). |

### THE ROWS (lines 88-164)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1 | **type** `public enum Kind` | How a row is dispatched: the works' surplus first, wind at its weather, hydro levelling the load, the rest by marginal cost. |
| 94 | 42 | **type** `public static final class Row` | One row of the month's fleet: what it offers and, after dispatch(), what it ran. |
| 114 | 5 | `private Row(String name, Kind kind, double kw, double waterKwh, double marginal, Good fuel, double fuelPerKwh, double upkeepPer...` _(in PowerDispatch.Row)_ |  |
| 121 | 1 | `public static Row mustRun(String name, double kw)` _(in PowerDispatch.Row)_ | The works' surplus: it takes the load first, whatever the price. |
| 124 | 1 | `public static Row wind(String name, double averageKw)` _(in PowerDispatch.Row)_ | Wind at its average kW; the weather (windAt) shapes it. |
| 127 | 3 | `public static Row hydro(String name, double capKw, double waterKwh, double upkeepPerKwh)` _(in PowerDispatch.Row)_ | Hydro under a cap with a month of water to spend where the load is highest. |
| 132 | 3 | `public static Row fuelled(String name, double kw, double marginal, Good fuel, double fuelPerKwh, double upkeepPerKwh)` _(in PowerDispatch.Row)_ | A fuelled row, run by marginal cost, each half-block up to its kW. |
| 138 | 26 | **type** `public static final class Month` | What a month's dispatch came to. |
| 143 | 1 | `public List<Integer> getMeritOrder()` _(in PowerDispatch.Month)_ | Immutable indices into the supplied rows, in the actual dispatch order, including idle rows. |
| 146 | 10 | `public Month copy()` _(in PowerDispatch.Month)_ | Detached saved flow, so load need not dispatch an already completed month again. |

### THE CALENDAR AND THE WEATHER (lines 165-179)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 1 | `public static double halfHours(int s)` | Hours in half-block s: its block's share of the month, halved. |
| 171 | 3 | `public static double season(int monthOfYear)` | The calendar month's load over the year's mean, for 1..12; 1 for anything else (a fixture with no calendar). |
| 176 | 3 | `public static double windAt(int s)` | Wind's output over its average in half-block s: its block's share, windy in the first half and calm in the second. |

### THE MONTH (lines 180-288)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 1 | `private static double nonNegative(double x)` |  |
| 200 | 3 | `public static Month dispatch(List<Row> rows, double drawKw, double season, double voll)` | One month's merit order: drawKw the grid's draw (kW, before losses), season the calendar month's load over the year's mean, voll what a short half-block clears at. |
| 205 | 76 | `public static Month dispatch(List<Row> rows, double drawKw, double season, double voll, double carryKw)` | The same dispatch, with the city's wires limiting generation before fuel or water is spent. |
| 283 | 5 | `private static double use(double[] residual, double cap, double level)` | The water kWh a hydro row of this cap runs above this level of the residual, half-block by half-block. |

