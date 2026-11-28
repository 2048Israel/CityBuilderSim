# PowerDispatch.java - 484 lines · 34 methods · 42 constants · model

`ham/citybuildersim/PowerDispatch.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (4), [PriceLevel](PriceLevel.md) (3)

**Used by (26):** [AdviceValueOracle](AdviceValueOracle.md), [AshbourneCheck](AshbourneCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildingsTemplate](BuildingsTemplate.md), [CityNeeds](CityNeeds.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [HydroCheck](HydroCheck.md), [NewUnitCheck](NewUnitCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [ScaleCheck](ScaleCheck.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [TownGridCheck](TownGridCheck.md), [UtilitiesHandler](UtilitiesHandler.md), [WaterPowerCheck](WaterPowerCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md)

## Sections

| line | section |
|---:|---|
| 27 | FIGURES |
| 100 | THE YEAR, THE PLACE AND THE CREDIT (PW3, 2026-11-23) |
| 267 | THE ROWS |
| 354 | THE CALENDAR AND THE WEATHER |
| 374 | THE MONTH |

## Enum constants

| line | constant | says |
|---:|---|---|
| 270 | `PowerDispatch.Kind.MUST_RUN` |  |
| 270 | `PowerDispatch.Kind.WIND` |  |
| 270 | `PowerDispatch.Kind.HYDRO` |  |
| 270 | `PowerDispatch.Kind.FUELLED` |  |
| 270 | `PowerDispatch.Kind.SOLAR` |  |

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
| 75 | `PowerDispatch.PLAN_PEAK_SEASON` | `PLAN_PEAK * season(peakMonth())` | The draw a firm kW must cover in the peak block of the year's highest month, losses in [D: PLAN_PEAK times SEASON's highest, July's 1.193 on the north's calendar, so 1.263 x 1.193 = 1.507 of the mean draw; the south's... |
| 78 | `PowerDispatch.WIND_FIRM` | `windFirm()` | What a wind farm's average delivers in its calmest half-block [D: the least of WIND_BLOCK x (1 - WIND_SWING) over the blocks: 0.806 x 0.41 = 0.330]. |
| 92 | `PowerDispatch.SOLAR_BLOCK` | `{ 0.680, 1.136, 2.054 }` | The sun's output over its mean in each block, base to peak [M: EIA-930 2025, the lower 48, the study's vblocks.py; spec 2.4; the prototype, line 96]. |
| 95 | `PowerDispatch.SOLAR_SWING` | `{ 1.0, 1.0, 0.66 }` | How far the sun swings inside a block, over the block's average, the first half sunny and the second dark (the wind's halves) [M: EIA-930 2025, the coefficient of variation of hourly solar output inside each load bloc... |
| 98 | `PowerDispatch.SOLAR_FIRM` | `firm(SOLAR_BLOCK, SOLAR_SWING)` | What a solar farm's average delivers in its darkest half-block [D: the least of SOLAR_BLOCK x (1 - SOLAR_SWING) over the blocks: the base's and the shoulder's dark halves, 0]; the reserve test credits the sun by the p... |
| 108 | `PowerDispatch.WIND_MONTH_CF` | `{ 37.4, 37.2, 43.1, 40.7, 32.1, 32.2, 27.5, 23.8, 23.1, 33.8, 35.4, 39.2 }` | Wind's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: a month's index is its figure over the twelve's mean. |
| 111 | `PowerDispatch.SOLAR_MONTH_CF` | `{ 16.2, 18.7, 23.6, 27.5, 29.3, 31.9, 31.9, 29.7, 26.7, 22.4, 16.9, 13.1 }` | Solar photovoltaic's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: the US fleet's months, a generated world's. |
| 114 | `PowerDispatch.HYDRO_MONTH_CF` | `{ 34.7, 35.1, 38.2, 40.8, 42.8, 39.8, 34.2, 32.3, 26.1, 24.9, 28.5, 36.1 }` | Conventional hydro's capacity factor in each calendar month, January first, percent [S: EIA, Electric Power Monthly, table 6.07.B, 2025; spec 2.4]: a station's month of water is its average times the month's index. |
| 117 | `PowerDispatch.PEAK_HOUR` | `760_649.0 / 491_034.0` | The year's peak hour over its mean [D: EIA-930 2025, the lower 48, 760,649 / 491,034 MW; spec 2.2]. |
| 120 | `PowerDispatch.PLAN_PEAK_HOUR` | `PEAK_HOUR /(1 - GRID_LOSSES)` | The draw the firm supply must cover, losses in [D: PEAK_HOUR over (1 - GRID_LOSSES)]: what the reserve test plans power against (PLAN_PEAK, the peak block's, stays the substations'). |
| 123 | `PowerDispatch.RESERVE` | `0.15` | The firm supply's margin over the peak hour the reserve test asks [E: NERC's reference margin level, cited as evidence; spec 2.8, 6]: power's NEEDS YOU line is 1 / (1 + RESERVE). |
| 126 | `PowerDispatch.WIND_PEAK_CREDIT` | `0.730` | Wind's output in the year's 100 highest-load hours over its average [M: the study's dispatch of EIA-930 2025, 0.247 of nameplate over 33.8%; spec 2.4]: its credit alone. |
| 129 | `PowerDispatch.SOLAR_PEAK_CREDIT` | `2.621` | Solar's output in the year's 100 highest-load hours over its average [M: the study's dispatch of EIA-930 2025, 0.629 of nameplate over 24.0%; spec 2.4]: its credit alone, at the US fleet's place. |
| 132 | `PowerDispatch.CREDIT_FLOOR` | `0.1` | What a variable row's credit falls to, over its credit alone, once the variable rows make CREDIT_HALF of the city's energy [E: the study's straight line, spec 2.4; NREL's and ERCOT's direction as evidence only]. |
| 135 | `PowerDispatch.CREDIT_HALF` | `0.5` | The variable rows' share of the city's energy at which their credit has fallen to CREDIT_FLOOR [E: spec 2.4]. |
| 138 | `PowerDispatch.SOLAR_BAND_DEGREES` | `10` | The latitude bands' centres the sun's tables are read at, degrees from the equator either way [D: PVGIS's places averaged in 10-degree bands, docs/studies/electricity/pvgis.py]. |
| 141 | `PowerDispatch.SOLAR_PLACE_FIXED` | `new double[] { 0.6537, 0.7379, 0.7714, 0.7921, 0.7396, 0.6128, 0.5027, 0.3959 }` | A fixed rack's yield a kWp in each band, 0 to 70 degrees, over the tracked US fleet's [S: PVGIS 5.3 (c) European Communities, CC BY 4.0, ERA5, 14% losses; the US reference seven places at 32.8-35.5 N, 2,073.6 kWh a kW... |
| 144 | `PowerDispatch.SOLAR_PLACE_TRACKED` | `new double[] { 0.8359, 0.9611, 0.9930, 1.0011, 0.8909, 0.7125, 0.5910, 0.4825 }` | A single-axis tracker's yield a kWp in each band, 0 to 70 degrees, over the tracked US fleet's [S: PVGIS 5.3 (c) European Communities, CC BY 4.0; out/pvgis.txt]. |
| 147 | `PowerDispatch.SOLAR_US_FIXED` | `0.7940` | A fixed rack's yield at the US fleet's places over a tracker's there [S: PVGIS 5.3, out/pvgis.txt]: a generated world's farm before the tracker. |
| 150 | `PowerDispatch.SOLAR_SHAPE_FIXED` | `new double[][] { { 1.030, 1.037, 1.080, 1.035, 0.957, 0.936, 0.992, 0.982, 1....` | A fixed rack's months in each band, on the north's calendar, each over the twelve's mean [S: PVGIS 5.3, out/pvgis.txt; the south's places turned six months]. |
| 161 | `PowerDispatch.SOLAR_SHAPE_TRACKED` | `new double[][] { { 1.035, 1.044, 1.090, 1.039, 0.957, 0.936, 1.006, 0.996, 1....` | A tracker's months in each band, on the north's calendar, each over the twelve's mean [S: PVGIS 5.3, out/pvgis.txt]. |
| 247 | `PowerDispatch.FUEL_OIL_MJ_PER_LITRE` | `0.150e6 * MJ_PER_BTU / 3.785412` | Megajoules in a litre of fuel oil [D: 40 CFR 98 subpart C table C-1, residual fuel oil No. |
| 250 | `PowerDispatch.DIESEL_MJ_PER_LITRE` | `0.138e6 * MJ_PER_BTU / 3.785412` | Megajoules in a litre of diesel [D: 40 CFR 98 subpart C table C-1, distillate fuel oil No. |
| 253 | `PowerDispatch.PELLETS_MJ_PER_TONNE` | `17.48e6 * MJ_PER_BTU / 0.90718474 * 0.92` | Megajoules in a tonne of wood pellets [E: 40 CFR 98 table C-1's wood and wood residuals, 17.48 MMBtu a short ton dry, 20.33 GJ a tonne, at 8% moisture (x 0.92) = 18.70 GJ; no government figure for pellets was found; s... |
| 379 | `PowerDispatch.SHORT_KW` | `1e-9` | A half-block left with more than this much unserved is short, kW. |
| 382 | `PowerDispatch.LEVEL_STEPS` | `40` | Halvings of the water's level, as the prototype loops them. |
| 385 | `PowerDispatch.LEVEL_EPS` | `1e-6` | The level's resolution, kW. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 274 | `public final String name` |  |
| 275 | `public final Kind kind` |  |
| 277 | `public final double kw` | MUST_RUN and FUELLED: kW offered in every half-block; WIND and SOLAR: its average kW; HYDRO: its cap, kW. |
| 279 | `public final double waterKwh` | HYDRO: its month of water, kWh; 0 for the rest. |
| 281 | `public final double marginal` | FUELLED: city money a kWh it costs to run (fuel + variable upkeep); 0 for the rest. |
| 283 | `public final Good fuel` | FUELLED with a fuel; null for the rest. |
| 285 | `public final double fuelPerKwh` | Units of fuel it burns a kWh; 0 without. |
| 287 | `public final double upkeepPerKwh` | Its variable upkeep, thousands of the WORLD's money a kWh (paid abroad later); 0 without. |
| 289 | `public final double[] gen` | kW it ran in each half-block (set by dispatch()). |
| 291 | `public double kwh, fuelUnits, earned` | The month's kWh, fuel units, and kWh x its half-blocks' prices (set by dispatch()). |
| 327 | `private List<Integer> meritOrder` | The actual dispatch order, including idle rows; observation only, never read by the dispatch. |
| 344 | `public final double[] load` | Per half-block: the load asked (kW, losses in), what was left unserved (kW), and the price it cleared at (city money a kWh). |
| 345 | `public double askedKwh, servedKwh, curtailedKwh` |  |
| 347 | `public double ratio` | servedKwh / askedKwh, 1 when nothing is asked. |
| 349 | `public double energyPrice` | City money a kWh DELIVERED: the sum over half-blocks of served kWh x price, over servedKwh, over (1 - GRID_LOSSES); 0 when nothing is served. |
| 351 | `public double season` | The season the month was dispatched at, its load over the year's mean (SEASON at the city's calendar): a kW drawn is served season x MONTH_HOURS kWh, which the bills charge (PW1-R); 1 in a save from before it, which b... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 462 | **type** `public final class PowerDispatch` | One month's merit order for the city's grid: the rows that make power, the load a month asks of them cut in six half-blocks, and the price each half-block clears at. |
| 25 | 1 | `private PowerDispatch()` |  |

### FIGURES (lines 27-99)

| line | len | member | says |
|---:|---:|---|---|
| 80 | 3 | `private static double windFirm()` |  |
| 85 | 5 | `private static double firm(double[] block, double[] swing)` | A variable row's least share of its average over the six half-blocks: its block's share x (1 - its swing), the calmest or darkest. |

### THE YEAR, THE PLACE AND THE CREDIT (PW3, 2026-11-23) (lines 100-266)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 4 | `public static int northern(int monthOfYear, double lat)` | The calendar month on the north's calendar for a city at this latitude: six months on south of the equator [spec 2.2]; the north's at the equator and in a generated world (NaN); a month outside 1..12 as given. |
| 178 | 1 | `public static double season(int monthOfYear, double lat)` | The calendar month's load over the year's mean for a city at this latitude: its own season. |
| 181 | 6 | `private static double index(double[] cf, int northMonth)` | A month's capacity factor over the twelve's mean, for 1..12 on the north's calendar; 1 for anything else. |
| 189 | 1 | `public static double windMonth(int monthOfYear, double lat)` | Wind's output in the calendar month over its average, for a city at this latitude. |
| 192 | 1 | `public static double hydroMonth(int monthOfYear, double lat)` | A hydro station's water in the calendar month over its average, for a city at this latitude. |
| 195 | 6 | `private static double band(double[] table, double lat)` | A table read at \|lat\| between its bands' centres, held at its ends. |
| 203 | 4 | `public static double solarYear(double lat, boolean tracked)` | A solar farm's year at this latitude over its production1 (the tracked US fleet's 24.0%): on a fixed rack or tracked; a generated world's (NaN) the US fleet's. |
| 209 | 8 | `private static double solarShape(int northMonth, double lat, boolean tracked)` | ...its month on the north's calendar over its own year there: the band's shape, or a generated world's the US fleet's (EIA). |
| 219 | 3 | `public static double solarMonth(int monthOfYear, double lat, boolean tracked)` | A solar farm's output in the calendar month over its production1, for a city at this latitude: its place's year times its month there. |
| 224 | 5 | `public static int peakMonth()` | The month of the year's highest load, on the north's calendar: SEASON's highest. |
| 231 | 4 | `public static double creditFall(double share)` | What a variable row's credit alone keeps at this share of the city's energy from the variable rows: 1 at none, falling in a straight line to CREDIT_FLOOR at CREDIT_HALF, and held there. |
| 237 | 1 | `public static double windCredit(double share)` | A wind farm's firm kW over its production1 at this share: its peak hours' output, falling with the share. |
| 240 | 5 | `public static double solarCredit(double lat, boolean tracked, double share)` | A solar farm's firm kW over its production1 at this latitude and share: its peak hours' output at the US fleet's place, scaled by its output in the peak month here over the US fleet's then, falling with the share. |
| 256 | 10 | `public static double heatContentMJ(Good g)` | MJ of fuel energy in a unit of a good (HHV, the unit the good is sold in), NaN for a good no row burns: thermal coal (PW1), fuel oil, diesel and wood pellets (PW2). |

### THE ROWS (lines 267-353)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 1 | **type** `public enum Kind` | How a row is dispatched: the works' surplus first, wind and sun at their weather, hydro levelling the load, the rest by marginal cost. |
| 273 | 50 | **type** `public static final class Row` | One row of the month's fleet: what it offers and, after dispatch(), what it ran. |
| 293 | 5 | `private Row(String name, Kind kind, double kw, double waterKwh, double marginal, Good fuel, double fuelPerKwh, double upkeepPer...` _(in PowerDispatch.Row)_ |  |
| 300 | 1 | `public static Row mustRun(String name, double kw)` _(in PowerDispatch.Row)_ | The works' surplus: it takes the load first, whatever the price. |
| 303 | 1 | `public static Row wind(String name, double averageKw)` _(in PowerDispatch.Row)_ | Wind at its average kW; the weather (windAt) shapes it. |
| 306 | 1 | `public static Row solar(String name, double averageKw)` _(in PowerDispatch.Row)_ | The sun at its average kW (PW2); the day and the weather (solarAt) shape it. |
| 309 | 3 | `public static Row hydro(String name, double capKw, double waterKwh, double upkeepPerKwh)` _(in PowerDispatch.Row)_ | Hydro under a cap with a month of water to spend where the load is highest. |
| 314 | 3 | `public static Row fuelled(String name, double kw, double marginal, Good fuel, double fuelPerKwh, double upkeepPerKwh)` _(in PowerDispatch.Row)_ | A fuelled row, run by marginal cost, each half-block up to its kW. |
| 319 | 3 | `public Row offering(double kw, double waterKwh)` _(in PowerDispatch.Row)_ | The same row offering another kW and month of water, nothing run yet: a dispatch run again on it leaves this one as it ran (PW1-L). |
| 325 | 28 | **type** `public static final class Month` | What a month's dispatch came to. |
| 330 | 1 | `public List<Integer> getMeritOrder()` _(in PowerDispatch.Month)_ | Immutable indices into the supplied rows, in the actual dispatch order, including idle rows. |
| 333 | 10 | `public Month copy()` _(in PowerDispatch.Month)_ | Detached saved flow, so load need not dispatch an already completed month again. |

### THE CALENDAR AND THE WEATHER (lines 354-373)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 1 | `public static double halfHours(int s)` | Hours in half-block s: its block's share of the month, halved. |
| 360 | 3 | `public static double season(int monthOfYear)` | The calendar month's load over the year's mean, for 1..12; 1 for anything else (a fixture with no calendar). |
| 365 | 3 | `public static double windAt(int s)` | Wind's output over its average in half-block s: its block's share, windy in the first half and calm in the second. |
| 370 | 3 | `public static double solarAt(int s)` | The sun's output over its average in half-block s (PW2): its block's share, sunny in the first half and dark in the second. |

### THE MONTH (lines 374-484)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 1 | `private static double nonNegative(double x)` |  |
| 394 | 3 | `public static Month dispatch(List<Row> rows, double drawKw, double season, double voll)` | One month's merit order: drawKw the grid's draw (kW, before losses), season the calendar month's load over the year's mean, voll what a short half-block clears at. |
| 399 | 78 | `public static Month dispatch(List<Row> rows, double drawKw, double season, double voll, double carryKw)` | The same dispatch, with the city's wires limiting generation before fuel or water is spent. |
| 479 | 5 | `private static double use(double[] residual, double cap, double level)` | The water kWh a hydro row of this cap runs above this level of the residual, half-block by half-block. |

