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

## Verification before review

`CopperBufferCheck` is registered in AllChecks. Its real-market controls
cover domestic and captive demand, profitable buffer replenishment,
existing-stock sales, feed/output identity, and the expenditure identity.
The unchanged-main reproduction passes the preservation controls and fails
the loss-prevention assertions. The final-model run passes all.

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

The three revised assertions in the existing copper/cement checks also fail
with whole-main Sector and CopperRefining classes substituted, with their
original towns, prices, months and no-demand premises retained. The cement
home-purchase/production controls pass on both models. Logs:
`target/cpb1-changed-sections{,-main}.log`.

The picked set is ALL because
Sector is shared by more than the pick fanout and AllChecks adds a harness;
no `batch-check.sh --pick` is run in this worktree.

`batch-check.sh --fast`: exit 0, compile and regenerated maps green, zero
firm Stale findings (187 soft). Log: `target/cpb1-fast.log`. The named run
includes CopperBufferCheck, CopperRefiningCheck (including its played seed 1),
CementConcreteCheck, MaterialsCheck, WoodProductsCheck, RefineryCheck,
ManufacturingCheck, GdpCheck, BooksCheck, SectorStatementCheck,
SectorFlowCheck, HistoryCheck and TreasuryCheck. LongPlaytest's final seed
runs also execute its MoneyFixtures and monthly MoneyAudit assertions.

The final named run is `target/cpb1-named-final.log`, with its kept suite
output at `target/cpb1-named-final-suite.txt`. Common-judge exit 3: only FF1's
pre-existing CopperRefiningCheck assertion that the Rotterdam run cuts at
least one order (0 months). Every other named check passes. Copper Refining's
seed-1 child plays all 2,400 months, exits 0, and stands zero Rod Mills (8.6
minutes). The first run's CL7 child finding is absent on this final path.
The final `batch-check.sh --fast` also exits 0 (136-second compile), maps
regenerated, zero firm Stale findings and 187 soft:
`target/cpb1-fast-final.log`. Ship record parsing by
`sh tools/known-reds.sh CPB1 docs/queue/CPB1-ship.md` exits 0.

## Seed windows and trace

Both requested runs were made once on the final model, with the shared Earth
and world files set through JDK_JAVA_OPTIONS. LongPlaytest source is unchanged.
Logs and monthly CSVs: `target/cpb1-seeds/seed{3,9}{.log,-month.csv}`.

| seed | played to | window | negative GDP months | lowest GDP in window | old failing month, now GDP |
|---|---:|---|---:|---:|---:|
| 3 | 760 | 700–760 (61 months) | 0 | 38,654.695 at 712 | 735: 43,690.273 |
| 9 | 1530 | 1490–1530 (41 months) | 0 | 241,012.025 at 1491 | 1514: 266,836.765 |

Neither run has a negative-GDP finding anywhere. Both exit 1 solely for the
existing industry/home placement class tracked by CL7: seed 3 at 760,
industry 0.82 km versus homes 0.58 km; seed 9 at 1530, 1.09 versus 0.70 km.
Both monthly money checks and all final MoneyFixtures pass. The original
0.9.102 negative seeded totals are historical replays, not a baseline rerun;
the reduced fixture independently proves main 0.9.140 still has the defect.

Correction after REV-CPB1-1: the previously claimed month-47 default trace
move was an invalid comparison of policies. The default prefix through month
60 exits 0 (`target/cpb1-default60.log` and `target/cpb1-default60-month.csv`),
but the retained 0.9.140 `AUTO2-0.csv` uses the autopilot, while the default
LongPlaytest holds 3% by hand. Their policy rates first diverge at month 46:
AUTO2 13.9709079% versus default 3%; FX is already 0.708041023 versus 0.820237.
The GDP difference at 47 cannot establish a CPB1 model change. The prior
seed comparisons to AUTO2 have the same policy mismatch and are not used as
evidence of a model change. The seed-window GDP readings above remain valid.

An observing probe follows LongPlaytest's default founding and actions, with
no edits to LongPlaytest. The only building opening at 47 is a Boutique in
Luxury Retail; no cement or wood works stand then. It reproduces the exact
default GDP (138.929554161 at 47, 136.984000010 at 48). The targeted old-stock-
production mutation has identical GDP and openings through 51. Logs:
`target/cpb1-openings{,-old-production}.log`. The orchestrator was notified
of the comparison error before the ensemble; its explicit ensemble GO stands.
Running that same observer with `playtest.autopilot=true` reproduces the
retained AUTO2 baseline at 47 (138.921867088) and 48 (136.830974208), to its
CSV precision. Thus the month-47 GDP move was the preceding policy/FX change,
not a CPB1 production change when the Boutique completed. Luxury Retail's
Boutique completion is common to both policy paths. The matched-policy log
is `target/cpb1-openings-auto2.log`; main was not rerun.

The ensemble will compare like policies with the newest committed baseline,
currently `docs/ensemble/0.9.140.md` (snapshot 10cc0ca20), without rerunning
main for a baseline.

## REV-CPB1-1 mends

The orchestrator calls for findings 2 and 3 to be mended here, and finding 1
queued as CPB2. The ship file queues `docs/queue/CPB2.md`: the allocated-
marginal-cost export-spare bypass and NaN export prices for goods with no
world buyer are pre-existing uncovered paths, intentionally not mended here.

The original 500-tonne loss case remains. In the same recycler town, a
separate profitable cathode band now causes a positive market remainder:
3,169.247573112 tonnes needed, 500 recovered, and 2,669.247573112 bid and
actually bought. The cathode made uses the sum at the recipe ratio. A shadow
CopperRefining whose scrap bid drops the remainder fails both the bid/bought
and actual feed/output assertions, with the profitable premise still passing
(`target/cpb1-review-mutation/sections.log`, exit 1). Focused sections pass
without that mutation (`target/cpb1-review-sections.log`).

CopperBufferCheck's domestic-demand control now clears the real month and
checks the stock cover produced and its exact received anode. Its existing-
stock control also clears the month, asserting the excess stock exported,
the retained dump-line stock and no fresh cathode production or feed bought.
Both pass in `target/cpb1-review-buffer.log`.

## Orchestrator review and reserved runs

Provisional mends only. Review mends are a13a31b37. Main 0e32d5330 (HW1-V,
0.9.141) is merged in f42cc74bc; only generated indexes conflicted and were
regenerated. Merged `batch-check.sh --fast` exits 0 (219-second compile),
zero firm Stale findings, 187 soft (`target/cpb1-merged-fast.log`). The named
review run is `target/cpb1-review-named.log`: exit 3, only the inherited FF1
CopperRefiningCheck line. The revised controls and the full played child pass
(child exit 0, 16.1 minutes).

The orchestrator's 22:34 instruction supplies main's retained default trace,
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-FB4-10cc0ca2/trace-*.csv`, and
requires an eight-file byte comparison before the ensemble. The complete
default LongPlaytest on the merged model exits 0, with its new traces at
`target/cpb1-merged-trace/trace-*.csv` and report/status in the same directory.
All eight differ byte for byte (`target/cpb1-trace-compare.log`):

| file | first month | first column | main | CPB1 |
|---|---:|---|---|---|
| trace-month.csv | 657 | bankPaidForPaper | 0.000 | -0.000 |
| trace-bank.csv | 942 | deposits | 2344699.653 | 2344699.654 |
| trace-bonds.csv | 948 | postedBuy | 14018.242 | 14018.235 |
| trace-house.csv | 954 | reAssets | 1853516.931 | 1853516.930 |
| trace-build.csv | 970 | consSalesTax | 4525.419 | 4525.418 |
| trace-labour.csv | 978 | wage2 | 5.16909 | 5.16908 |
| trace-pop.csv | 1027 | deaths | 19.128 | 19.127 |
| trace-borrow.csv | 1061 | face | 71842.828 | 71846.618 |

The first byte difference is signed zero at 657; the first nonzero displayed
numeric difference is deposits at 942. The complete run and all money
fixtures pass. Main remains 0e32d5330 at the pre-ensemble fetch. The
orchestrator was sent this comparison, and its conditional ensemble GO is met.
The ensemble runs once on this merged model. The ALL dry run still requires
a machine slot after DM1-P's; merge main again first if it advances.

The seed-window results above are on CPB1's 231f16270 base plus its own model.
The ship file forwards the bounded-run placement class to the already queued
CL7 with a scoped LongPlaytest row; it does not cover negative GDP.
