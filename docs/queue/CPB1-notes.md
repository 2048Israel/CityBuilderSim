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

Mutation runs keep the final fixture and its premise fixed, compiling only a
shadow Sector class under `target/cpb1-mutations/`. Main's Sector fails five
assertions, including all four loss-prevention assertions and the partially
captive identity. Removing only the captive order correction fails two;
removing only the captive production correction fails the partially captive
identity. Each variant exits 1; logs are the respective `fixture.log` files
in `main`, `no-captive-order`, and `no-captive-production`. No model source
is changed by the mutation runs.

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
