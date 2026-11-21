# PW1-R - PW1's review follow-ups

PW1 (power by the merit order at the market's price) had a stranger's review that said SHIP (Opus, xhigh,
2026-11-11): no money made or lost, the month order and load path agree, no keys or ids moved. Its follow-ups, in
order of weight. A model batch; 1 and 2 move the trace, so an ensemble.

1. **The bills ignore the season** (should fix). The dispatch serves draw x SEASON x 720 kWh, but each class is
   billed draw x ratio x price x 720 (UtilitiesHandler.java ~600 and ~805, Sector.java ~843): the energy and both
   wires on the year's mean kWh. July is billed 16% under, March and April 14% over, while the fuel is bought on the
   kWh served; the utility's margin takes the gap. Bill the kWh served. Done means a PowerPriceCheck section in July
   and in April where each class's bill equals its served kWh at the price.
2. **The planner prices power at the wrong tariff** (should fix). `Recipes.pricesOf()` (~147) prices power at
   `getPricePerWatt()`, the commerce price with both wires, for Heavy Industry works now billed the industry price;
   `SpreadPlanner.ownCosts` (~264) values all of a works' own generation at `tariff()` (the industry price) where its
   surplus is paid `worksTariff()`. On PW1's notes' month 1,201, $8.68 against $5.25: a Pulp Mill's power ~65% too
   dear to the planner. Price each class's power at its own bill, and own generation at what its surplus is paid.
3. **powerBooks has no SaveFileCheck line** (should fix): the only round trip is in memory (PowerDispatchCheck
   section 10), so the Gson read-back of `Book` is unchecked (ReloadCheck only if its city burns coal at the save).
4. Lows: `Good.valueOf` on a saved fuel name throws on an unknown name (Game.java ~6222, ~16380), and
   `buyPowerFuel` has no `getMarkets()==null` guard; the plants' coal moves no freight (the utility has no buyer's
   row); the struck book of month t prices month t-1's load (July on June's, 1.088 against 1.193) - say so where
   the lag is described; BuildCardCheck ~1290's POWER row checks `BuildAdvice.served()` against itself, and the ring
   assertion ~1336 assumes no wind farms without checking it; ScaleCheck ~196's run of holding months is 174-180 of
   160-200 now (watch).
