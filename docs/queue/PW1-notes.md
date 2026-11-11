# PW1 - power by the merit order at the market's price ({next}, 2026-11-11)

The brief is PW1's row in `QUEUE.md` and [the electricity study](../studies/spec-electricity.md) §2.2-2.7 and §3,
Jerus's Q1 (2026-11-02: "Market price"), with its prototype `docs/studies/electricity/Dispatch.java`. The row folds
in PW5 (substations) and PW8 (homes pay for their power). A model batch: Opus, with Sonnet slices for the code.

## 0. Scope (decided at the start)

PW1's core ships here. **PW5 and PW8 go under Pending** in the ship file:

- PW5 (substations, one per 100 MW of peak, by the auto-builder, their cost the wires' part of the bills) is a new
  `buildings.json` row and an AutoBuilder rule that changes every city's builds; the wires' cost as a building wants
  the bills PW1 measures first, so it is priced from them, not guessed beside them.
- PW8 (a household power bill) touches `HouseholdBalance`, which HL1 (the households' books closed) is changing in a
  session beside this one.
- The south of the equator's season flip (spec 2.2) waits for PW3: nothing in the tree hands the dispatch the city's
  latitude yet. Every city reads the northern calendar.

## 1. The design (as built in slices A-F)

- **The month**: `PowerDispatch` (new, pure) cuts it in three load blocks (base 50% of hours at 0.904 of the mean,
  shoulder 40% at 1.07, peak 10% at 1.20), each in a windy and a calm half. In each half-block: the works' surplus
  first (must-run), the wind at its block's share x (1 +/- swing) with the excess curtailed, the hydro levelling the
  residual above a level found by halving (its month of water under its nameplate cap), then the fuelled rows by
  marginal cost. A half-block's price is the last fuelled row's marginal; a short one clears at VOLL ($3,000/MWh).
- **The struck book** (the timing): `updateServices()` runs twice a month and on load, so the dispatch is pure. The
  dispatch at the end of month t-1 is struck in month t's `SimulationEngine.updateEconomy()` (`strikePower()`): its
  ratio is month t's energy ratio, its prices what month t's power is billed at when the statements strike at the top
  of month t+1, its fuel what the utility buys from the market just before that strike and pays in t+1's
  `servicesNet`. One dispatch for the ratio, the bills and the fuel. Saved (`DataSave.powerBooks`); a load hands the
  struck ratio and prices, never re-strikes.
- **The bills**: commerce and light industry pay the energy price plus both wires (AEO2026 table 8: 1.885¢
  transmission, 4.053¢ distribution); Heavy Industry and Mining the energy plus transmission. The works' surplus is
  paid its half-blocks' prices. Until the first strike, the founding tariff (D$10 a kW a month) for all three.
- **The fuel**: the Coal Power Plant (id 9) is 325 MW at 8,490 Btu/kWh, burning thermal coal at 25.12 GJ/t (0.357
  t/MWh) bought through `Markets.draw` as the city (the mines' stock first, the world's for the rest), plus $9.37/MWh
  variable upkeep paid abroad; hydro $1.71/MWh. The audit's two TRADE lines: `utilities FuelImports`, `utilities
  PlantUpkeep`; the local coal is the treasury paying Coal Mining, pool to pool.
- **The advice**: the build advice plans power against the calm peak: a Wind Farm counts `WIND_FIRM` (0.33) of its
  average, the demand `PLAN_PEAK` (1.20 / 0.95) of the mean; with yellow at 0.75 about 12% over July's peak block.
  Without it the advisor's wind cities brown out in every calm half-block. `unit()`, `built()`, `figure()` and
  `supplyDemand()` carry it, so the Build ring's power reads firm supply over the peak's draw (the advice's own
  figure), and a Wind Farm's card its firm kW. The NEEDS YOU row (`CityNeeds`) reads the same pair, worded "81% served
  at the calm peak" (`CityNeeds.AT_THE_CALM_PEAK`). On the average it read a city of wind farms clear while its calm
  half-blocks were half served, and the Build screen's cards, which the rows drive, never offered power (slice F).
- **The playtest's player buys power as the advice says** (`LongPlaytest.powerByNeed()`), as it buys roads: the advice's
  card and the next in its ranking, each valued at the output lost at the month's ratio or at the peak's share
  served, whichever is larger. Before, it offered a Wind Farm and a Coal Power Plant by one throttle at the same value, the
  wind farm listed first: on 1395e06c its city built 103 Wind Farms and no coal by month 2,400, and from month 960 was
  short in 2-3 calm half-blocks in 164 of 240 sampled months, its energy at the cap.
- **The fuel at the margin** (`Game`, the fuel price source): the import price if the city imports the coal, the
  export netback if it exports it, its own market's price if its own mines sell it, else the import price.
  `SpreadPlanner.cityValue()`, the refiners' first unit's worth, priced a city's coal at D$1.82 a tonne (the export
  netback of a good it trades none of) while its plant would import at about D$75.

- **The screen** (Services, the power statement and the utility's books): the three prices and the energy's a MWh,
  "Still at founding" until a month clears, the coal burned, the wind and the works' surplus turned away and the load
  not served (in how many of the six half-blocks), each line only when there is some; the books carry the power
  bought, the fuel at home and abroad and the upkeep abroad, and the net is the model's `getElectricityIncome()`. The
  build card's power sold reads "at the works' price". The power screen of its own (the blocks, the series) is PW6's.

- **The founding grid** (`BASE_GRID_KW`, 10,000 kW, "for nothing", spec 1.2) stays free: a fuelled row at marginal 0. A town
  it covers clears at an energy price of 0 and pays the wires alone; its works' surplus is paid nothing. Three fixtures
  (MoneyCheck, MaterialsCheck's power town, WoodProductsCheck 4) raise the homes' draw so the coal plant is the margin.

## 2. Measured

(to come: the default run's prices, the coal bought, how often VOLL fires, the margin the plants earn, the works shed)

## 3. Found on the way

- **ReloadCheck's `.mapStamp` red is main's** (a fault in the map, not this batch's code). A month after a load, the
  reloaded map lays rail on plots the live map leaves bare (kind 5, `TilePainter.RAIL`), with the same builds in both and
  the two maps byte-equal at the load. There are no reserves and no mined sites in either city. On PW1 it is district 3,
  6 plots, at month 1205. Clean main (0a5b74ef) shows the same at month 2105 (district 11, 2 plots), once the probe follows
  ReloadCheck past its last checkpoint. PW1 changes the probe city's builds, so the fault shows at 1205. The probe is in
  the orchestrator's notes folder, `PW1-map-rail-after-load/`. A fix batch is queued in the ship file.
