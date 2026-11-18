# CPB1 — the cathode buffer at a loss

Base: main 231f16270, 0.9.140. Built by CPB1 on `batch/CPB1`.

## Finding and mend

Main still reproduces the brief's loss exactly. `CopperBufferCheck` founds
each city through `new Game(GameFiles.scratch(...))` and `newGame()`, places
one real Electrolytic Refinery, leaves its yard empty and cathode demand at
zero, and sets anode's world prices to 11/10 and cathode's to 10/9. Before
any model change: planned cathode 9,700, order share 1, anode bid 9,750,
input cost per cathode 11.056701031. `Markets.clearMonth`, `strikeSectors`
and `updateNationalAccounts` book raw imports 107,264.328309080, held cathode
87,300 and GDP −19,964.328309080. Log: `target/cpb1-main-fixture.log`.

★ Gate only the undemanded buffer in `Sector.getPlannedOutput`, against
`flowCostPerUnit` and the output's net export price. This reuses the current
landed feed, captive-input and co-product costing already used for flow
orders. It avoids the sector-wide cost allocation in marginal cost, and
does not turn the investment planner into a monthly production veto.
Positive local demand still gets `STOCK_MONTHS` cover. Profitable buffer
replenishment still targets `getStockCapacity * DUMP_THRESHOLD`.

★ Include the captive part in a stockable good's forecast order fraction,
and subtract that part from the ordered output available to the shed.
Otherwise suppressing the spare buffer also suppresses feed needed by an
active downstream Rod Mill. The harness causes a partially captive refinery
and proves the anode/cathode identity through the real clearing, with no
unprofitable surplus. Full-rate fractions remain exactly 1.

★ Stock production follows the buildings' actual run shares (the same
nameplate-weighted calculation as flow co-products), including committed
feed, rather than just the output good's speculative order fraction.
`Sector.committedInput` defaults to zero. Copper Refining supplies only the
recovered scrap its existing monthly purchase commits; a building's floor
applies only if that building uses that input. An Electrolytic Refinery
standing alongside the scrap refinery therefore stays idle. The purchase
still settles in its original flow-production phase, at its existing price
and allocation. These are no new save fields: the preview is local to a
clearing and is refreshed before every order decision.

The copper declaration hook is necessary because the city buys recovered
copper outside the goods market before cathode production, even with no
demand. Cutting that output without using its paid feed would break the
input/output identity. Its marginal-cost forecast now uses this clearing's
anticipated purchase, rather than last month's settled purchase; otherwise
last month's cheap feed could authorize this month's dear imported scrap.
The harness tests both a mixed pair of refinery recipes and a standalone
scrap refinery whose next month has no recovered feed.

The mended empty-yard clearing has no anode bid, import or cathode production.
Raw imports and GDP are respectively 14.328309080 and −14.328309080: the
separately booked consumables still cost money. No accounting rule was
changed to hide this. A forced feed purchase and held cathode still produce
negative GDP equal to held output less imported feed. Existing stock above
the model's dump line still exports at a loss.

The required source lookups were rerun with `Where` on this base.
`Markets.planOrders` is not a method: `Markets.clearMonth` calls each
`Sector.planOrders` before it clears goods. No copper-specific model rule,
save field, balance constant or version edit is needed. No Pending row in
QUEUE names CPB1.

## Verification in progress

`CopperBufferCheck` is registered in AllChecks. Its real-market controls
cover domestic and captive demand, profitable buffer replenishment,
existing-stock sales, feed/output identity, and the expenditure identity.
The unchanged-main reproduction passes the preservation controls and fails
the loss-prevention assertions. The final-model run currently passes all.

Mutation runs keep the final fixture and its premise fixed, compiling shadow
model classes under `target/cpb1-mutations/`. Main's Sector and CopperRefining
fail nine assertions, including all four primary loss-prevention assertions
and the partially captive identity. Removing only the captive order
correction fails two; restoring the old stock-production cap fails two;
removing the committed-input floor fails the paid-feed identity; reverting
the recovered-feed cost preview fails the standalone next-month assertion.
Each variant exits 1; logs are the respective `fixture.log` files in `main`,
`no-captive-order`, `no-captive-production`, `no-committed-input`, and
`stale-recovered-feed`. No model source is changed by mutation runs.

The first named run found three affected old assumptions. Their original
fixtures are retained, with the corrected behavior asserted, rather than
raising prices or adding a buyer to let them pass:

- CopperRefiningCheck's original empty-yard WN3 full-rate example actually
  has no demand and a negative feed spread: it now asserts no feed or output.
  CopperBufferCheck separately causes a profitable full-rate buffer and
  checks production and the feed order to the bit. The old full-rate chain
  control remains unchanged.
- Its original partial recovered-scrap case still buys all 500 recovered
  tonnes and landfills none. It now proves they become cathode at the real
  recipe ratio, without importing the loss-making remainder of full intake.
  Bid rounding is compared with the harness's existing `near` helper.
- CementConcreteCheck's original month-two quarry/plant has cost 0.001801
  versus cement's 0.001684 before the order and makes no speculative buffer.
  That is now a regression assertion. Its first operating quarry supply then
  lowers the feed quote to 0.000752 versus cement's 0.001688; the identical
  town makes its first profitable buffer in month three, buying limestone at
  home. The original home-purchase, production and audit assertions remain.

Two other CopperRefiningCheck reds in that first run are the already recorded
FF1 (no cut observed in the Rotterdam run) and CL7 (seed 1's distant industry
finding in its child playtest). No CPB1 cover is used for them.

Named checks, seed windows, final trace/ensemble and orchestrator review/gate
results will be recorded here as they finish. The picked set is ALL because
Sector is shared by more than the pick fanout and AllChecks adds a harness;
no `batch-check.sh --pick` is run in this worktree.

`batch-check.sh --fast`: exit 0, compile and regenerated maps green, zero
firm Stale findings (187 soft). Log: `target/cpb1-fast.log`. The named run
includes CopperBufferCheck, CopperRefiningCheck (including its played seed 1),
CementConcreteCheck, MaterialsCheck, WoodProductsCheck, RefineryCheck,
ManufacturingCheck, GdpCheck, BooksCheck, SectorStatementCheck,
SectorFlowCheck, HistoryCheck and TreasuryCheck. LongPlaytest's final seed
runs also execute its MoneyFixtures and monthly MoneyAudit assertions.
