# RN1-H — the homes' headroom a margin over demand

**Current mends (2026-11-19).** Main `6b0aa202e` (HL1, 0.9.154) is merged
at `e293af05d`. The fast check, all 18 prior picked names and additional
HousingDoorCheck are green under the exit 0/3 rule. Both the merged and
fresh-main LongPlaytests exit 0; the numerical housing split remains at
m21. Their complete traces are preserved outside the worktrees, with the
fresh main reference shared for HS1. The provisional marker remains:
the 80-run ensemble and paired AUTO2 16 await the explicit go after SZ1.
No pick or dry run was made under that hold. The latest comparison and
all provenance are in the final section below.

## What changed

The housing planner read `TARGET_HEADROOM` backwards. At demand 100 and
capacity 100 it held; it only opened an order above demand 105, and then
sized to bare demand. `RealEstate.plan()` now targets demand multiplied by
`1 + BusinessInvestment.TARGET_HEADROOM`, less standing and already ordered
capacity. Its trigger, quantity and `latentHeadShortfall()` agree. At 100
heads of demand the target is 105 places, a 5% margin over demand and about
4.76% vacancy as a fraction of capacity, before building granularity and
changes in jobs. A block's own jobs remain demand, as before.

★ Keep the existing segment choice, family-door shortage, land limit, rent
hurdle and lender's door. The new buffer is the capacity the plan seeks;
it does not waive the financing or construction rules. HN1's smaller-order
retry still applies after the plan reaches that door. No saves, money
transfers or threading changed. The advisor now calls the gap capacity
needed for demand plus headroom, since vacant reserve homes are not
unhoused people.

## Every reader of the constant

The search covered all Java sources, including the `BuildAdvice.SLACK`
alias and its callers. With demand 100 a margin over demand means 105
capacity; with demand 106 it means 111.3.

| Reader | Reading before RN1-H | Decision |
|---|---|---|
| `RealEstate.plan()` and `latentHeadShortfall()` | Held at 100/100; trigger above 105 demand on 100 capacity; quantity filled bare demand | Correct both shortfalls and quantity, homes only |
| `BuildAdvice.SLACK` | Alias; `Ahead.scale()` and `wanted()` multiply projected demand by `1 + slack` | Already a margin over demand; unchanged |
| `ui/SectorScreen` | Displays the dial as "builds to 5% spare" | Describes a spare-capacity margin, without doing arithmetic; unchanged. The homes now match it; the other planners' thresholds are in the call below |
| `BusinessInvestment` retirement | Keeps at least `demand * (1 + TARGET_HEADROOM)` | Already a margin over demand; unchanged |
| `BusinessInvestment` maker expansion | Waits for projected demand over output times 1.05, then sizes to demand less output | Not spare supply over demand; unchanged pending the call below |
| `LuxuryRetail.plan()` | Waits for a queue over counters times 1.05, then sizes to queue less counters | A queue threshold; `LuxuryRetailCheck` expressly tests that hysteresis; unchanged |
| `Restaurants.plan()` | Same queue threshold against seats | Not spare seats over demand; unchanged pending the call |
| `Retail.plan()` food and `planStations()` petrol | Forecast must exceed effective supply times 1.05; size to bare forecast | Not spare supply over demand; unchanged pending the call |
| `FoodIndustryCheck` | Its maker fixture mirrors the demand-over-output threshold | Not a spare-supply target; unchanged with that planner |
| `LuxuryRetailCheck` | Queue-at-threshold yield calculation and orders use counters times 1.05 | Explicit queue hysteresis, not spare counters; unchanged |
| `RailDepotCheck` | Sets offered freight at capacity divided by 1.05 and protects demand times 1.05 from retirement | Already a margin over demand; unchanged |
| `HousingCheck`, `DiscoveryCheck` after RN1-H | Capacity target and research-job capacity delta include demand times 1.05 | Margin over demand, asserted against the constant |

`BuildAdvice.SLACK`'s explicit callers in `AutoBuildCheck`,
`BuildAdviceCheck`, `ChildcareCheck`, `ER1Check`, `HydroCheck`, `LandCheck`,
`RoadCheck` and `LongPlaytest` pass its demand multiplier; those uses are
already a margin over demand. `LongPlaytest` and `LandCheck`'s land line is the inverse
`1 / (growthFactor * (1 + SLACK))`, reserving land beyond projected use;
`ER1Check` prices that reserved ground. `GameVersion` has only historical
prose describing these advice/land uses and is untouched.

★ Mailed the full audit before changing any other planner. The
orchestrator accepted homes only (bus message
`20261118-165728-473-orchestrator-fb0c`). One Pending call covers the other
thresholds: at 100 supply and 106 demand they order to 106, not 111.3.
Recommended: give these economic queue/output thresholds their own named
dial and retain their current hysteresis until Jerus chooses whether those
markets should also plan spare supply. This avoids changing their tested
premise merely to make one constant's sentence uniform.

## The regression

`HousingCheck` section 7 builds a scratch city from catalogue quantities.
★ Use the existing housing harness, which already checks the planner,
rent and the identity of the doors, rather than adding another harness
and changing the global registry.
It chooses integer stores, studios and Houses whose actual jobs support
exactly the home's capacity, including the founding endowment, and whose
headroom is divisible by one House. Both segments have their doors; no
family-door shortage can cause the order instead. Rents pass the existing
build hurdle and the planner has land. It asks the actual planner and
credit-check shortfall rather than a copied planning function.

The fixture has demand 1,080 and capacity 1,080. The target, against the
constant, is 1,134. It orders 9 Houses, exactly 54 places. With eight Houses
on site it orders only the ninth; at all nine on site it orders nothing.
The credit check agrees at every step. At the completed target and above
it there is no further order. Houses have no jobs, so these boundary
assertions keep demand fixed. No rounding tolerance can hide an extra
building.

## Checks and shipping

- Initial `./batch-check.sh --fast`: exit 0; no firm Stale findings.
- First final-reading `HousingCheck`: exit 0, all 15 new assertions pass.
- Putting both old shortfall formulas back makes `HousingCheck` exit 4:
  the credit gap, the order at equality and the House fixture assertions
  fail. It orders zero at 1,080/1,080. The corrected formulas were restored
  before the checks on model commit `9dd451a79`.
- Final-reading `HousingCheck`, `MortgageCheck` and `HousingDoorCheck`:
  exit 0 each, one name per call. HousingDoorCheck sees seven trimmed
  orders, including one initially refused for capital, none left marked
  refused after its smaller build; zero unplaced households in its window.
- Final `./batch-check.sh --fast`: exit 0; compile, regenerated maps,
  Stale (zero firm findings) and tree checks complete.
- All 18 picked names are green under the contract's exit 0/3 rule.
  Exit 0: `DiscoveryCheck` after its expected-delta mend,
  `DriverlessTruckCheck`, `HouseholdCheck`, `HousingCheck`, `InvestCheck`,
  `LabourCheck`, `LongPlaytest`, `MortgageCheck`, `NewGameCheck`,
  `ReadPathCheck`, `ResearchViewCheck`, `SaveFileCheck`,
  `StaffingDrawCheck`, `TimingCheck` and `WageLadderCheck`.
  Additional `HousingDoorCheck` exits 0.
- Exit 3, main-known only: `PopulationCheck`'s played city does not fill
  after a decade; `RoadCheck`'s busy-street life-cost/net-plan choice;
  `PrizeCheck`'s lab till exceeds the six-month cushion premise. Each
  assertion and its tally is already covered by main's common judge; no
  new known-red row is needed. `RoadCheck`'s name also selects
  `InterestAbroadCheck`, which passes. The final fixture-borrower calls
  explicitly use `-Dcbs.parallel=1` as well as one name per invocation.
- All detailed logs and each named call's `suite.txt` are kept under
  `target/rn1-h/`; the two serial drivers' latest per-name exit codes are
  in `target/rn1-h/check-results.txt`. Its earlier DiscoveryCheck exit 4
  precedes the harness expectation mend; the final entry is exit 0.
- The picked checks are run by name, one call at a time, while the
  orchestrator's heavy slot is occupied. The initial 15-name pick grows
  to 18 after the DiscoveryCheck mend: its fixture borrowers add
  `DriverlessTruckCheck`, `PrizeCheck` and `ResearchViewCheck`.
- The first picked `DiscoveryCheck` exposes its old credit-gap expectation:
  section 20 compares a seven-job research grant with the same grant held
  out, expecting `7 * 2.25 = 15.75` more capacity. The new target requires
  that demand plus `TARGET_HEADROOM`, or 16.5375. Updated just that expected
  delta and label against the constant, keeping the research fixture and
  its jobs unchanged. Mailed the orchestrator before the harness edit;
  he agreed (`20261118-171110-631-orchestrator-ad97`). The source and dial
  search finds no constant for 2.25: both housing methods write that
  jobs-to-heads factor directly, so the assertion retains it and adds the
  model's `TARGET_HEADROOM` rather than the measured number.
- Initial pick: 15 of 222 harnesses; after DiscoveryCheck, 18. Review not needed — no money, saves
  or threading in the diff. No ALL pick and no publisher edit, so no dry
  run is owed under the orchestrator's instructions.
- The ensemble waits for HL1's run and the orchestrator's explicit go.
  The orchestrator's follow-up (`20261118-170656-383-orchestrator-cb42`)
  specifies the committed baseline for the 80-run scores and, for the
  extra housing metrics the baseline lacks, a paired AUTO2 subset: all
  16 seeds on this batch's merge base with the same `ensemble.extra`
  paths, in a temporary worktree removed afterwards. That subset and the
  80 run one at a time within the heavy slot. HL1's unshipped model is not
  a baseline.
- Before the provisional handoff, merged main `79e336def`. Its advance
  from the tested base `36157fb19` changes only `QUEUE.md` and the new
  `docs/queue/SZ1.md`; the tested model and harness sources are unchanged.
  Recomputed committed pick: the same 18 of 222. Merge newer main again
  before the ensemble if it moves.

No process file changed. No save slot was used. No calls remain beyond the
other planners' shared-dial question recorded in the ship file.

## The default played city before HL1

`LongPlaytest` on model `2947d74d3` passes: "Nothing. Every month passed
the audit and every reload matched." Its 2,404 trace rows are kept at
`target/rn1-h/trace/rn1-h-*.csv`, with the report in
`target/rn1-h/LongPlaytest.log`. There are no unplaced households in any
month. This trace moves numerically, not merely in runtime or the new
advisor wording: the first house-row difference against the supplied
FB4 trace is m21 (92 homes against 91, capacity 652 against 646).

The comparison here is the supplied
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-FB4-10cc0ca2/`, commit
`10cc0ca2`. It predates HN1 and other model changes. **It establishes that
the trace moved; it is not a main-versus-RN1-H counterfactual.** The
orchestrator's paired AUTO2 main subset will isolate this batch for the
ensemble's extra housing readings.

| Default trace reading | RN1-H | FB4 reference |
|---|---:|---:|
| Population at m2400 | 1,172,036 | 1,320,884 |
| Median door vacancy, m240 onward | 3.45% | 2.82% |
| Door vacancy at m2400 | 2.57% | 3.74% |
| Family rent at m2400, nominal trace units | 44.378 | 72.008 |
| Studio rent at m2400, nominal trace units | 49.676 | 75.870 |
| Price index at m2400 | 6,559.835 | 9,501.031 |
| Landlords' principal at m2400, nominal D$ billions | 752.751 | 220.869 |
| Landlords' assets at m2400, nominal D$ billions | 10,120.401 | 13,321.013 |
| Built months, m240 onward | 524 | 676 |
| Trimmed orders, m240 onward | 411 | 524 |
| Held for capital, m240 onward | 18 | 212 |
| Held for down payment, m240 onward | 7 | 28 |

Vacancy counts empty doors: homes less the occupied households (seekers
less doubling up and unplaced), divided by homes. The plan's five percent
is spare head capacity over jobs-derived demand, so it does not promise
exactly five percent of doors vacant. The default city's final population
is 1,185,657 against FB4's 1,339,820, both at m2405. The nominal rent and
debt figures travel with different price levels; they should not be read
without the index and the population beside them.

**The mechanic in a real run:** at least 55 built months from m240 onward
have a reported positive head gap small enough to be reserve capacity
rather than a raw head shortage, first m260, 261, 263, 267 and 268. This
is a conservative count from the actual advisor order, allowing its
rounding: for reported gap `G`, standing capacity `C`, headroom `h` and
nonnegative capacity on site `Q`, `G + 0.5 <= h*C` ensures the unrounded
gap `(1+h)*D-C-Q` is within the margin. Family-door needs may coexist;
this counts orders whose head gap is in the buffer, not a claim that
every such order has no other reason to build. The lower bound avoids
mistaking current jobs alone for all the funded jobs the plan sees.

## HL1 merged and checked again (2026-11-19)

The orchestrator resumed RN1-H on main `6b0aa202e` (HL1, 0.9.154).
Merged it at `e293af05d`. No source conflicts; the conflicts in the two
generated indexes were resolved by taking main and regenerating from the
merged source. `./batch-check.sh --fast` exits 0 (compile 69 s, no firm
Stale finding). The 600 index changes after midnight are date headers
only: all 1,200 added/removed lines contain the old or new generated date.
No RN1-H model or harness code was changed to pass the merge.

All 18 prior picked names and additional `HousingDoorCheck` were run
again, one name per call and `-Dcbs.parallel=1`. Every name exits 0 except
`PopulationCheck` and `PrizeCheck`, whose existing main-known assertions
give exit 3. `RoadCheck` now exits 0 (including the additionally selected
`InterestAbroadCheck`). Logs and each named call's suite verdict are in
`target/rn1-h/hl1-merge/`. The pick script, a pick run, a dry run and the
ensemble have not been run under the orchestrator's heavy-slot hold.

The merged `LongPlaytest` exits 0, including HL1's household-book checks:
"Nothing. Every month passed the audit and every reload matched." Its
suite time is 1,217 s. Its 2,402 housing rows end at m2403, with zero
unplaced households throughout.
The eight closed CSVs, report, source/base provenance and SHA-256 manifest
are preserved outside this worktree at
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-RN1-H-e293af05/`, prefix
`trace`, so removing the worktree will not remove the evidence.

The orchestrator reported that HL1's earlier trace was lost when its
worktree was removed. He authorized one fresh named `LongPlaytest` on a
clean detached checkout of exact main `6b0aa202e`, with the same data,
whose shared prefix is
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-6b0aa202/trace`.
This runs after the merged named driver exits; no two own harnesses run
at once. The first completed rows already confirm the same numerical
housing split at m21: RN1-H has 92 homes / 652 capacity, main 91 / 646.
The fresh main call exits 0 too (suite 1,337 s): every month passed its
audit and every reload matched. Its 2,404 housing rows end at m2405. All eight closed
CSVs, `report.txt`, `suite.txt`, clean-source `snapshot.txt`, `exit.txt`
and `sha256.txt` are preserved in that shared directory, and the completed
path was mailed to the orchestrator for HS1. The detached checkout is
only a build workspace; the shared evidence does not depend on it.

This is now a comparison with exact current main, unlike the older FB4
comparison above. Both use the same data and default playtest; only the
homes' planner differs in the game code. Stock and price readings below
use the common m2400; run medians and counts are labelled separately.

| Default trace reading | RN1-H on HL1 | Main 6b0aa202e |
|---|---:|---:|
| Population | 2,969,817 | 2,453,909 |
| Homes | 735,568 | 586,714 |
| Median door vacancy, m240 onward | 2.63% | 2.61% |
| Door vacancy at m2400 | 5.35% | 2.72% |
| Family rent, nominal trace units | 74.732 | 40.511 |
| Studio rent, nominal trace units | 63.623 | 43.798 |
| Price index | 9,763.688 | 4,752.529 |
| Family rent / price index | 0.007654 | 0.008524 |
| Studio rent / price index | 0.006516 | 0.009216 |
| Landlords' principal, nominal D$ billions | 1,145.983 | 305.480 |
| Landlords' principal / assets | 3.40% | 2.16% |
| Built months, m240 onward | 883 | 841 |
| Trimmed orders, m240 onward | 762 | 586 |
| Held for capital, m240 onward | 14 | 25 |
| Held for down payment, m240 onward | 1 | 31 |
| Unplaced households, maximum over the run | 0 | 0 |

The vacancy medians and order/refusal counts cover each completed trace
(RN1-H m240-2403, main m240-2405); the stock, price, debt and population
readings are all at the same m2400. At that month, rents are lower relative
to the price index, while nominal principal and the principal/assets ratio rise. The
plan's buffer is used in at least 447 built months on the merged path,
using the same conservative head-gap test above. This one city is larger;
the paired AUTO2 16 and full 80 are still owed before judging the spread
across cities. Neither test's finding or audit bound was changed.

The comparison JSON is also preserved in the RN1-H trace archive as
`comparison-main-6b0aa202.json`. The orchestrator's hold remains: no
ensemble, pick or dry run until the explicit go after SZ1's ensemble.
The temporary main checkout was removed only after confirming its exact
owned path, that its only changes were 600 generated date headers, and
that the complete shared trace and report were preserved outside it.
