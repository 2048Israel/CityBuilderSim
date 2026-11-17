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

## REV-FB3-1 - the mends

1. **The capital flows array's length is the model's.** `CapitalFlows.PEAK_STOCK_SLOT` (24),
   `PEAK_SPREAD_SLOT` (25) and `SAVE_SLOTS` (26) name the peaks' slots and the array's length;
   `restore()` reads the peaks by them. CurrencyRegimeCheck's `SAVE_SLOTS` is `CapitalFlows.SAVE_SLOTS`,
   where it was a bare 24 (red on 6ef3b419: `theDoubtTakesTheHotMoneyHome`), with its label and javadoc
   naming FB3's two peaks. HoldersCheck's line now says "CapitalFlows.SAVE_SLOTS (26) long", where it
   said "24 slots long", and asserts the length against the constant.
2. **The draws close before the month's saves.** `getMarkets().closeMonth()` moved in `Game.nextMonth()`
   from the method's last line to just after `reconcileMap()`, before `reportSaves()`, the autosave and
   the recording. A save carries the draws since FW6-C, and the autosave was filled before the month
   closed them. A city loaded from it got the month's draws back: the next strike rowed them on the
   sellers again, and the next clearing filled them into the new month's trades, so `tallyThePartners`
   counted last month's drawn imports twice. Nothing between the old place and the new draws or reads
   the draws. `reportSaves()`, the autosave's fill and `recordForReplay()` only write, so no figure of
   the month moves, and a recording's month now holds what a save between the presses holds. The other
   autosaves are all taken between the presses, after the close: before and after a skip, on quit and
   on close. `Markets.drawsClosedLastMonth()` counts what the close forgot (not saved; the fixture's
   witness).
   - **ReloadCheck section 5:** the live Danzik, after the caused point, played a month at a time
     under SaveSlotCheck's save clock until its own autosave fell due. The game wrote slot 0 of the
     scratch folder at the month's end ("Autosave - month 1229"). The city was loaded from that slot,
     the draws compared right after the load, and both cities run a month, history and save compared.
   - **Red on 6ef3b419**, proven with ReloadCheck on 6ef3b419's Game.java over the branch's classes:
     - the draws right after the load differ (MATERIALS, CARS and PETROL one draw each, DIESEL nine);
     - a month on, the save's partner trade differs in 11 places, the imports doubled (goodUnits[23]
       unitsIn 5,794,273 against 11,595,611).
   - Green on the branch (below).
3. **The hand order books its buyer.** ReloadCheck's caused hand order now draws as the player's
   orders do (`Game.drawMaterials()`, `drawBitumen()`, `fit()`): `Markets.draw(good, construction,
   null, ...)`, which books Construction's purchase, where it drew for `Trade.CITY` with no buyer. A
   good the builders make is passed over, so they never buy from themselves. A literal Game order was
   not taken: materials come off the yard first, and bitumen is held only where refiners stand, so
   neither is sure to leave a seller's row at the caused point, which is the row 3i asks for.

## The dry run

`sh tools/gate.sh FB3 --dry-run` on 614b8a79 over main 69a29638 (run `/tmp/cbs-gate-FB3-CP19hJD1`), beside LX1's
ensemble: the suite in 3,285 s, gate exit 1, three unnamed reds, none in FB3's code.

- **LandmassCheck**, "home filled by a fixture": 1,439 buildings drawn on the island; linked free 12,178,110 sq ft
  of 12,094,868. AQ1's dry run on the same main read the same to the digit, and main 69a29638's own build, run
  alone here, fails it word for word. AQ1's ship named it under FF1.
- **CopperRefiningCheck**, "...the rule cut an order in at least one month of the run": 0 months. It was red in
  AQ1's dry run too, and AQ1's ship named it under FF1.
- **LandNationsCheck**, "a real level-12 seller split yields to the next hover within eight frames": 10,001.66 ms,
  the next hover's measure never began inside the harness's 10 s `await`. Run alone on the branch it was green at
  3.42 ms, with the harness at "All checks passed.". This is a wall-clock line in a correctness harness that misses
  with an ensemble beside. It is named in the ship file under QW1, queued there.

Main then moved to 0.9.135 (AQ1, fe7102e4). It was merged here: its saved operating-loss streak sits in DataSave,
Game's save and load lines and SaveFileCheck, beside FB3's own lines, with no conflict but the generated docs.

## The checks

`./batch-check.sh SaveFileCheck MoneyDatesCheck InvestCheck ReloadCheck CurrencyRegimeCheck HoldersCheck SaveSlotCheck ReplayCheck`
on 4b9181cf (the mends; the last two read the month-end autosave and the recording, which moved): exit 0, "suite OK -
8 harnesses" (SaveSlotCheck's one timing line soft, as every non-gate run). ReloadCheck 5: 12 draws closed, 69
compared the same right after the load, 348,913 history numbers and 94,842 save figures the same a month on.

**On the merge with 0.9.135** (c0d51fd9): `./batch-check.sh SaveFileCheck ReloadCheck ReadPathCheck DistressBleedCheck
MoneyDatesCheck InvestCheck CurrencyRegimeCheck`, exit 0, "suite OK - 7 harnesses"; Stale 0 firm; the maps regenerated.
The gate runs the suite fresh on the merge, so there is no second dry run.
`sh tools/pick.sh origin/main HEAD`: ALL 216 harnesses (Game.java, 180 readers), "review not needed".

**The named harnesses** on 98967c03 (`./batch-check.sh SaveFileCheck MoneyDatesCheck InvestCheck ReloadCheck`,
both data files named): exit 0, "suite OK - 4 harnesses"; Stale 0 firm; the maps regenerated.

**The trace:** the default playtest traced (`-Dplaytest.trace`, both data files named), one JVM at a time:
main 69a29638, built from a scratch tree, against `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-69a29638/`, and
the branch at 98967c03 against `.../cbs-trace-FB3-98967c03/`, and after the mends at 4b9181cf against
`.../cbs-trace-FB3-mends/`. Both times all eight trace files are byte-identical, and the
reports differ only in run time, the layout watch's seconds and the clock's timestamps. Every run printed "All checks
passed." and exited 0. The trace did not move, so no ensemble was run (EN1). The seller-priced months (FW6-C 2) move
no model figure, as the readers above say, and nor does the draws' earlier close (REV-FB3-1 2).
