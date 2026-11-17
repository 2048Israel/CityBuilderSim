# FB3 - reloads that read as they were saved (2026-11-17)

Four rows in one batch (docs/queue/FB3.md): HZ1, R0f-S and RT2-L are the load path and their
harnesses; FW6-C is the restores that ReloadCheck causes, the draws list and the seller-priced
markets. Built on batch/FB3 from origin/main 69a29638 (0.9.134). HZ1, R0f-S and RT2-L were built here;
FW6-C was built by a Sonnet slice to a written brief, and its diff reviewed here. No `SAVE_FORMAT` bump:
every new save key is appended or optional, and an older save reads as it did.

## HZ1 - the hot money placed by name

SaveFileCheck placed the hot money's arrival through raw slots of `CapitalFlows.toSaveArray()`
([0], [3], [16]), and its loop held the stock at half its target through [0]. Two named setters
replace both: `CapitalFlows.setStockForTest()` and `placeArrivalForTest()`, which adds to the stock,
the arrivals since founding and the month's arrivals. These are the same fields the old slots wrote,
so the fixture is unchanged to the bit. SaveFileCheck is green.

## R0f-S - the early city's want, carried whole

**Cause:** the load put Retail's spending capacity and wanted spend back after the rebuild only
when one of them was over zero (`if (carriedRetailCapacity > 0 || carriedRetailWant > 0)`, at both
restore sites in `readTheSave`). A city saved in its second month, when its shops want nothing,
came back wanting what the rebuild struck. On 0.9.134, MoneyDatesCheck's exhausted-gold town comes
back with a want of 0.0625 against a saved 0 (R0f measured 0.0259 on c0120d77). On today's path the
next month's rent, cash, savings and foreign account still match: the difference no longer carries
forward, so the old twin line was green on main.

**Fix:** both restore sites apply the carried figures always, zeros included. Every save a build
can open carries both, because they have been in Retail's extras since format 21 and the oldest
loadable format is 55.

**Check:** MoneyDatesCheck section 4 adds three lines:
- a fixture line: the town is saved in a month its shops want nothing;
- the loaded shops' want and means are the saved ones, to the bit;
- after the next month, its rent, the city's cash and the households' savings are its twin's,
  beside the whole foreign-account twin line, which is kept as it was.

Run on main's `Game.java` (the branch's classes with main's Game compiled over them), the seam line
is red ("after load: Retail wantedSpend 0 != 0.0625..."; MoneyDatesCheck 1 failure). It is green on
the branch. SaveFileCheck's R0f line is unchanged and green.

## RT2-L - an older save's contracts, read once the wages are struck

**Cause:** `readTheSave` inferred an older save's contracts (`inferContracts`, for a save with no
`contractRecords`) before `rebuildSimulationState()` strikes the wages. At that point
`depotWageBillPerPoint(true)` read no wage, the builders' index was 1, and the labour was priced at
the founding ladder. InvestCheck's House stands either side of the new rental property rebate's
line at the two labours: the old load read .0000625, against the loaded city's rule of 0.

**Fix:** the inference moves to the end of `readTheSave`, after every price is back. Payer, value,
units and allowance are unchanged, because none of them reads wages. Nothing in the load reads a
contract: the readers are the month (its dues, the city's sites) and the screens.

**Check:** InvestCheck keeps its line and adds a premise line, so the save fixture itself causes
the threshold crossing. The builders' wages must have moved since founding (index 1.1256), and at
the founding ladder's labour (the reloaded city's BuildersWages set to it, through the model's own
rule) the House's rebate is .0000625070953440 against today's 0. On main's `Game.java` the gate's
line is red ("the landlords get back 0.000063 ... at today's price: 0.000000", 1 FAILED) and the
premise holds. On the branch both are green.

## FW6-C - the restores no harness caused, the draws list, the seller-priced months

1. **The draws list crosses a save.** A new `Markets.State.draws` list (rows of seller, buyer,
   units, price) is written only when a market has draws, and `GoodsMarket.restoreDraws()` restores
   it into the draws list alone; the trades list stays "not counted" after a load. A hand order made
   between the presses now keeps its seller's row after a load.
2. **The seller-priced markets begin their month.** These are groceries, housing, building work,
   luxury retail and meals: `Markets.startSellerPricedMonth()`, called in `Game.nextMonth()` before
   `startOfMonthUpdate()`, whose luxury counter makes the earliest seller-priced fill of a press.
   Their trades and tally used to grow for the city's life. Their readers in the model
   (`BusinessInvestment.forecast`, BuildAdvice, `tallyThePartners`) read traded markets or
   import and export fills only, so no model figure moves.
3. **The hot money's peaks** (`peakStock`, `peakSpread`) ride at the end of the CapitalFlows save
   array (slots 24 and 25). The Trade screen's card prints "the most that was ever here", and a load
   used to restart it.
4. **ReloadCheck:**
   - 3c compares the units drawn since the strike.
   - 3f's fixture requires a skip with an outbreak and one with months sick: 10 skips, 3 with an
     outbreak, 5 with months sick.
   - New 3g (the stop's words, whether it runs, the months left and the peaks), 3h (Quarrying's
     carry, read right after the load because `produceFlow()` zeroes it) and 3i (each market's draws,
     row by row, and the hand order's seller row a month on).
   - A caused point: after the four checkpoints the live city gets a stop
     (`CapitalFlows.startStopForTest`), a hand order of half the most-held good, and a 100 t
     limestone carry (`Quarrying.setCarriedForTest`), because no checkpoint carried any. It is
     reloaded through the same path. The checkpoints alone show no stop, no carry and no draws; each
     fixture line says so.
   - 3j: a seller-priced market's trades of one month are off its list the next (groceries,
     housing, luxury and meals sold at all five points). Building work is named as never filled:
     its points are the crews' own.
5. **Mutations** (the slice's runs; each restored byte for byte): with each removed, a named line
   went red.
   - `restoreStopReason`: 3g, and section 2's `capitalFlowsStopReason` at 365 and 1205.
   - The carry's restore: 3h.
   - The drawn argument: 3c.
   - `restoreDraws`: 3i, the draws and the seller row a month on.
   - The peaks' restore: 3g, and section 2's `capitalFlows[*]`.
   - `startSellerPricedMonth()`: 3j at all five points.
6. **Left without a save** (no reader in the model or on a screen):
   - the bond book's issued and repaid so far (the ulp in `creditMoved`, inside ReloadCheck's
     billionth);
   - `renewedLifetime` and `monthsPayoutHeld` (read by the playtest's own end-of-run summary, which
     never loads);
   - the `carried*` load figures, read on the load path alone.

   FW6's ★1, the map after a load, was settled by CC1-M: `mapStamp` is compared since.

## The checks

`./batch-check.sh SaveFileCheck MoneyDatesCheck InvestCheck ReloadCheck`: see the mends line below.
`sh tools/pick.sh origin/main HEAD`: ALL 216 harnesses (Game.java, 180 readers), "review not needed".

**The named harnesses** on 98967c03 (`./batch-check.sh SaveFileCheck MoneyDatesCheck InvestCheck ReloadCheck`,
both data files named): exit 0, "suite OK - 4 harnesses"; Stale 0 firm; the maps regenerated.

**The trace:** the default playtest traced (`-Dplaytest.trace`, both data files named), one JVM at a time:
main 69a29638, built from a scratch tree, against `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-69a29638/`, and
the branch at 98967c03 against `.../cbs-trace-FB3-98967c03/`. All eight trace files are byte-identical, and the
reports differ only in run time, the layout watch's seconds and the clock's timestamps. Both runs printed "All checks
passed." and exited 0. The trace did not move, so no ensemble was run (EN1). The seller-priced months (FW6-C 2) move
no model figure, as the readers above say.
