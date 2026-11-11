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

## 1. The design (as built in slices A-E)

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
  Without it the advisor's wind cities brown out in every calm half-block.

## 2. Measured

(to come: the default run's prices, the coal bought, how often VOLL fires, the margin the plants earn, the works shed)
