# RN1-H — the homes' headroom a margin over demand

**Final evidence (2026-11-19).** The frozen model `ecfdcfae8`, over
HL1-R main `ddcb3400e` (0.9.156, including SZ1), completes all 80 ensemble
runs: PASS, all ten M1/F1 hard criteria, 24/26 overall against 24/26 on
0.9.155, no changed verdicts, missing runs or short runs. The paired main
AUTO2 16 also completes all m2400 on exact clean `ddcb3400e`, with zero
audit failures, unread audit months or findings. Economics are mixed:
pooled median people rise, but eight seed pairs shrink and the median
paired percentage change is −1.82%, including a −96.10% outlier. Run-median
vacancy rises from 2.78% to 3.03%; endpoint median vacancy is nearly flat;
landlords' relative debt and capital-refusal months rise. Full figures
and both directions are recorded below and in the ensemble artifact.
The orchestrator accepted the evidence and gave readiness go at 06:54;
the marker removal follows the evidence commit, alone in its own commit.
The default city's separate retained-bakery layout finding stays queued
as RN1-H-M; the ensemble does not arm that visual watch. Earlier HL1-only
comparisons below are historical.

**Returned gate, 2026-11-19.** The first gate stopped on 16 new lines
in four harnesses. The provisional marker was restored and pushed first
at `23a7e844b`. Eight separate named runs on RN1-H and exact main
`ddcb3400e` reproduce all branch failures; main is green (three exit 0,
KeptCheck exit 3 for its existing FS5 maturity fixture). Measurements
show missing played-fixture premises, with the underlying rules holding.
All sixteen lines, including exit and failure totals, are named in the
ship file under FS12, FS10 and FS4. No production or harness source changes
follow the final ensemble. The orchestrator's 11:35 instruction permits
readiness after these records and re-gating with the kept suite.

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

## SZ1, HL1-R and the map finding (2026-11-19)

Merged SZ1 main `3c4661a9d` at `39acf3894`. No source conflicts; the two
generated-index conflicts were regenerated. `--fast` exits 0 (compile
57 s), then HousingCheck exits 0: equal demand/capacity 1,080 orders nine
Houses, exactly 54 places. No housing fixture or model was changed.

The one authorized traced LongPlaytest finishes all 2,404 rows through
m2405. Its MoneyFixtures and household assertions pass, and there are
zero unplaced households. It exits 1 (wrapper 4, 677 s) for one layout
finding, `an old works moved`, at m2400: district (0,0), Industrial Bakery,
one of the one boxes kept at m601 no longer stands there. The district
now draws four, against one then; its model count was two then and no
lower than one since. None moved off a run. This is not HL1-M's separate
rail-geometry discrepancy and is not a flaky-suite exception.

All eight CSVs, `report.txt`, original `suite.txt`, `snapshot.txt` and
SHA-256 hashes are preserved at
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-RN1-H-39acf389/`, prefix
`trace`. The original red log is retained unmodified. Against the earlier
RN1-H/HL1 trace, the first house-row numerical split is m59 in the rates:
SZ1's funding lots move this path too. The new path ends at 951,153
people; its median door vacancy is 3.41%, with at least 303 built months
whose positive head gap is within the reserve, on the same conservative
test above. This is not a comparison with current main.

The orchestrator's 03:51 go classifies the bakery case as a real layout
fault found in passing, authorizes the retained hard finding to be queued
as its own row, and gives the heavy slot to RN1-H. `RN1-H-M` was verified
unused by `sh tools/batch-id.sh`; its causal follow-up brief records the
m601/m2400 counts and full trace path. CityMap, DistrictPlan and the watch
are byte-unchanged from main in this batch.

The common judge initially saw only `(no FAIL line)` because the finding
section printed no FAIL token and the later money-fixture footer was
green. A generic approval would hide unrelated findings. On the same
explicit instruction, take only LongPlaytest's change from HS1 commit
`b338ee4ba`, through `git show ... -- <LongPlaytest.java> | git apply`,
byte for byte. It adds FAIL before each finding, with HS1's comments;
CopperRefiningCheck's companion hunk remains HS1's. This changes only
the report, not the model, finding, watch or CSVs. The ship file names
the exact resulting one-bakery FAIL line to RN1-H-M. The old cached log
has not been relabelled as a newly passed run; the gate runs its pick
fresh with the precise name available.

Before the final ensemble, merged main `ddcb3400e` (HL1-R) at `524a943b5`:
Business Services' kept share is capped by seats, and the orchestrator
reports main's default trace identical. That merged `--fast` exits 0
(49 s); another `--fast` after the report change also exits 0 (49 s).
The main trace supplied for SZ1 is preserved at
`C:/Users/Jerus/AppData/Local/Temp/cbs-kept-SZ1/SZ1-after-HL1-trace/`,
prefix `run`, and applies after HL1-R too. The paired main AUTO2 subset
will use `ddcb3400e`, never the older `6b0aa202e` reference. The model and
driver stay frozen for the one final 80 and paired 16, run one ensemble
at a time with identical `ensemble.extra` paths.

## Final 80-run ensemble (2026-11-19)

On the orchestrator's explicit heavy-slot go, one final full ensemble ran
on clean source `ecfdcfae8`, over main `ddcb3400e`, with five workers, seeds
0 through 15 and all five policies. The primary baseline is the newest
committed full ensemble, `docs/ensemble/0.9.155.md`; main's paired AUTO2
subset is additional evidence, not a second full baseline. Both snapshots
use earth-2.bin and world-1.bin from the main data directory, with identical
`ensemble.extra` getters for homes, occupied homes, both rents, landlord
principal, mortgage-capital-refusal markers, doubled and unplaced families.
The exact flags and model snapshot are retained with the raw run.

The ensemble exits 0: 80/80 complete, no missing or short runs; all ten
M1/F1 hard criteria PASS, zero audit failures or unread audit months, zero
ensemble findings in every policy. All 64 switched runs match their AUTO2
twin before the switch. The total score is 24/26, the same as 0.9.155, with
no changed verdict. A1 and A2 remain informational failures. H1 remains
retired. The generated baseline is committed as `docs/ensemble/RN1-H.md`;
only its publication version heading and introductory model version are
replaced by `0.9.157`. Its summary JSON and numeric results are preserved.

| Score | RN1-H | Baseline 0.9.155 |
|---|---|---|
| A1, annual inflation | 2.98%, 8/16 in band; FAIL | 3.14%; FAIL |
| A2, longest high-inflation spell | median 28 months, range 12–53; FAIL | 26, range 14–47; FAIL |
| A3, deflation spell; final/peak people | median 0 months; 100%; PASS | 0; 100%; PASS |
| S1, ZERO_SLACK excess inflation | 3.01%; all 16 within currency-fall allowance; PASS | 3.16%; PASS |
| H1, early/late excess; change | 8.90% / 3.17%; −8.15 points; RETIRED | 5.97% / 2.30%; −7.35 points; RETIRED |
| H2, excess after autopilot returns | −0.87%, 15/16 below second-half excess; PASS | −0.63%; PASS |
| K1, shock's peak index excess | 17.02%; PASS | 16.75%; PASS |
| K2, inflation; excess over twin | 1.81%; −0.17 points; PASS | 1.97%; −0.27 points; PASS |
| R1, high-rate inflation excess | −4.26 points; PASS | −4.04 points; PASS |
| R2, unemployment excess | +0.77 points; PASS | +0.68 points; PASS |
| R3, final high-rate inflation excess | −3.93 points, none above +1 point; PASS | −4.37 points; PASS |
| T1, reach 40,000 people | 16/16, median m788; PASS | 16/16, m760; PASS |
| T2, AUTO2 final people | 3,101,332; PASS | 2,381,553; PASS |
| T2, ZERO_SLACK final people | 2,025,150; PASS | 1,621,756; PASS |
| T2, ZERO_HOT final people | 6,728; PASS | 7,366; PASS |
| T2, SHOCK final people | 46,532; PASS | 40,465; PASS |
| T2, HIGH final people | 27,684; PASS | 27,653; PASS |
| M1 and F1 | all ten hard criteria PASS | all ten PASS |
| Before-switch pairing | 64/64 identical | 64/64 identical |

★ Keep the numerical housing diagnostics additional to the standard
score: rents are divided by the price index, landlord principal is shown
against annualized endpoint GDP and against prices and population, and
the door's capital refusals count marked months, not inferred loan flows.
Those measures must be read beside people and vacancies. No monetary
audit tolerance or finding was changed. The default visual map watch is
not armed in the existing ensemble; its RN1-H-M finding remains a separate
real fault, retained in the ship file and the default trace.

## Paired main AUTO2 16 and the housing reading (2026-11-19)

After the full 80 finished and its command exited 0, the one paired subset
ran on a clean detached main checkout at full commit
`ddcb3400e605899755f923c85fe8e332faf50398`. Its `--fast` exits 0 (47 s), with
zero tree changes, then AUTO2 seeds 0–15 run once with five workers and
the identical extra getters and data flags. The subset exits 0; all sixteen
end at m2400, all money-audit rows are read and pass, and findings are zero.
The subset scorer lists the 64 deliberately unrun policies as missing;
it is additional paired evidence, not a second 80-run baseline. There are
no missing or short runs in RN1-H's primary full ensemble.

★ Report both pooled medians and the change within each seed. A higher
pooled median alone would misstate these results: eight cities grow more
and eight grow less, with median paired population change −1.82%, range
−96.10% to +165.43%. The full sixteen-pair tables are appended after the
standard score and its unchanged summary JSON in `docs/ensemble/RN1-H.md`.

| AUTO2, 16 seeds, m2400 unless stated | Main ddcb3400e | RN1-H ecfdcfae8 |
|---|---:|---:|
| Pooled median population | 2,381,553 | 3,101,332 |
| Population range | 1,383,251–5,395,142 | 152,930–5,006,382 |
| Median endpoint door vacancy | 3.18% | 3.16% |
| Median of each city's vacancy median, m240 onward | 2.78% | 3.03% |
| Median family rent / price index | 0.006580 | 0.006681 |
| Median studio rent / price index | 0.007140 | 0.007228 |
| Median landlord principal / annualized endpoint GDP | 2.03% | 2.80% |
| Median real landlord principal per person, trace units | 0.026912 | 0.035311 |
| Median months marked refused for mortgage capital, whole run | 22 | 36 |
| Range of those marked months | 0–108 | 12–110 |
| Maximum unplaced households, whole run | 0 | 0 |
| Maximum doubled households, whole run, not normalized for city size | 372,663 | 271,428 |

Nominal landlord principal is higher in twelve pairs; the thirteen-pair
count above refers to principal divided by annualized GDP.
Endpoint vacancy is higher in nine pairs, family rent/index in eight,
studio rent/index in ten, landlord debt/GDP in thirteen and capital-marked
months in ten. All sixteen endpoint GDP denominators are positive on
both sides. The reserve is a desired capacity margin, not a guarantee of
vacant doors: growth, segment mix, granularity, land, rent and the bank's
door still bind. Endpoint vacancy does not rise uniformly, and neither
real rent falls uniformly. Capital-refusal counts are months with a
positive end marker after any successful trim, not inferred loan flows;
they do not include the default advisor's separate down-payment reason.

The extreme is seed 14: main finishes with 3,917,855 people and 3.24% door
vacancy; RN1-H with 152,930 and no vacant doors. It reaches 40,000 people
at m1500 against main's m768 and records 46 capital-marked months against
six. This is slow growth, not a final population collapse: RN1-H's AUTO2
final/peak population is 100% in every seed. The paired table reports the
outlier without attributing its whole path to any one credit refusal.
Maximum unplaced households are zero in every one of the full 80 runs;
doubled households remain present and are recorded in the raw diagnostics.

The default SZ1 path is a different observation, before the final HL1-R
merge: at m2400 RN1-H on `39acf3894` has 935,838 people against the supplied
SZ1 main path's 1,650,471; door vacancy 3.93% against 2.71%, run vacancy
medians 3.41% against 1.71%. Its m240-onward advisor counts include 74
capital holds against zero, and five down-payment holds against 63.
Main's trace was reported unchanged after HL1-R; RN1-H's own pre-HL1-R
path is not relabelled as a fresh final-model default run. The frozen-model
paired comparison above is direct. Both observations remain in the record.

The report-only HS1 LongPlaytest hunk is three added lines and one removed
line (`git diff --numstat origin/main -- <LongPlaytest.java>`): its exact
authorized FAIL prefix and two comments, with no watch or model change.
No scripts, runner or scoring criteria changed. The metadata pick after
that hunk is 88 of 225, not ALL; no dry run is owed. The gate runs its pick
fresh. No full `batch-check.sh --pick` was run beside either ensemble.

Raw evidence is preserved at
`C:/Users/Jerus/AppData/Local/Temp/cbs-ensemble-RN1-H-ecfdcfae8/`:
`RN1-H/` is the full 80, `RN1-H-main-ddcb3400/` the paired 16. Both include
their clean model snapshot, compiled classes, logs, CSVs, times and scores.
The archive also holds `paired-summary.json`, its tables, the analysis
helper and command logs. `SHA256SUMS.txt` covers the preserved files.
The artifact names the data paths explicitly. The model source remains
`ecfdcfae8b93db0b4db3a970c7a8cf1021065287`; later commits record evidence
and publication notes only. No kept dry run exists or is required.

### Why the observed vacancy is below the margin

The 5% dial is a margin of head capacity over latent jobs demand. Even a
static, fully funded, divisible capacity target implies 5/105 = 4.76%
vacant capacity, not 5% vacant doors. The ensemble diagnostic counts doors;
different dwelling capacities, household sizes and studio/family demand
make that a different basis. The planner counts on-site capacity, while
jobs can keep growing during construction. It retains its segment choice,
land check, rent hurdle and the lender's debt-service, down-payment and
capital tests. It seeks a target; it does not enforce a vacancy floor.

Those limits are observed in the default trace: the SZ1 RN1-H path has
565 trimmed builds, 74 capital holds and five down-payment holds from
m240 onward. At m600 a proposed block's rent covers only 0.82 times its
real mortgage test against the lender's 1.20; at m1800 it needs 5,570 m²
and has 5.62 m² free. These are the labelled pre-HL1-R default observations,
not new final-model ensemble measurements. The final ensemble records
capital marks but not every planner reason or head-capacity target.
It does not isolate which constraint accounts for the exact 3.03%
median. That attribution remains unmeasured; the corrected formula is
proved directly by the equal-demand HousingCheck and its old-reading
mutation, while the default trace counts actual reserve builds.

At 06:54 the orchestrator accepted the full score and mixed paired
economics and instructed: commit the tables, drop the marker in its own
commit, check origin and report ready. He will gate at once on a fresh
suite because the pick is not ALL. No model or driver change follows the
final ensemble; only these evidence and publication records change.

## Stopped gate and its eight controls (2026-11-19)

The first gate's kept directory is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RN1-H-S9n4H4Rz/`.
It tested branch `4860daec68f9f75d2ea52bcd3bec97e03e406c88` on main
`ddcb3400e605899755f923c85fe8e332faf50398`. The suite completed before
the later power cut; its log, unnamed list and hashes are retained intact.
Nothing was executed or changed inside that kept directory.

At the orchestrator's instruction, run one harness per batch-check call,
one JVM workload at a time, with the explicit earth-2.bin/world-1.bin paths
and cbs.parallel=1. Main is a new detached checkout of exact ddcb3400e;
the RN1-H source is the same model as the ensemble and first gate.

| Named harness | RN1-H wrapper exit | ddcb3400e wrapper exit | New branch assertions |
|---|---:|---:|---:|
| AutoBuildCheck | 4 | 0 | 1 |
| ExchangeRateCheck | 4 | 0 | 4 |
| PassThroughCheck | 4 | 0 | 2 |
| KeptCheck | 4 | 3, existing known red only | 1 |

Main's KeptCheck section 44 is green. Its sole red is the existing FS5
`fixture: the settled lot watched, B:2732, runs 0 months, past the 3 months
its page is watched`; it is already covered by the registry. It is not
the new covered-funding-run failure. Both branch and main named runs
finish with unchanged tracked trees and zero firm Stale findings.

### AutoBuildCheck 4: FS12

The 600-month city has made 49 orders on RN1-H; main's city has made 30.
The next 36 months make zero more on RN1-H and twelve on main. Both saved
and live twins agree to the cent, all buildings and the log included,
with worst replay drift zero. The original fixture gives both cities
50,000,000 square feet and withRoom()'s extra tax room; it does not repay
their accumulated debt or cause a particular new order.

A diagnostic using the unchanged decades() and savedAndLoaded() fixtures
reads debt/GDP 3.75284594975 at m600 and 4.22553605211 at m636, against
the model's 2.40 limit. Its held line asks for Death care and explicitly
holds because the city owes 375%, then 422%, of annual GDP. Orders remain
49. The existing pass-reader counters after replay are all zero:
unexplained, falseHolds, underReserve, overLimit and unpaid. This is the
builder honoring its limit, not refusing an order it promises to make.
★ FS12 should cause a service shortage and debt/funding room before the
save and then demand the same positive order in both twins; keep the
replay identity. Do not move the measured month until a lucky order.

### ExchangeRateCheck 8b: FS10

The natural path at m175, the eve of suspension, already has FLOAT,
zero vault reserves, one break, cause VAULT, rate 2.68136087720 and hold
zero. At m176 it remains Float, rate 2.53615686892; its hold is zero
against float news -0.055675 logs. Both move-decomposition identities
still pass. The missing old peg makes both opening fixture assertions
false without a failed rate decomposition.

At m318 the vault is still zero, rate 2.19752537036, one break. The m319
return opens the new dated episode, then exhaustion breaks it in the same
month: two breaks, VAULT, zero reserves, FLOAT at 2.612783528664. Comparing
that float with the stored par 1 does not test an active peg's par.
ForeignAccounts.hearMoneyMonth(), read through Where, re-arms a new
episode; breakTheHold() records its exhaustion.

For a controlled component comparison, arrange reserves through
ForeignAccounts.buyReserves(1e12) at m318 and forget the setup's dealing,
then take the unchanged month's step. At m319 it is PEG, Canada gold
true, rate and par exactly 1, vault 455,815,745,195 and breaks still one.
This stock-arranged component control tests the dated hold, not a fiscal
flow or money-audit fixture. An earlier attempted treasury top-up bought
zero and was not treated as a funded control; that raw attempt is kept.
★ FS10 must cause enough reserves to keep the episode alive, and retain
the separate controlled exhaustion case. The four assertions and their
exit/summary lines share this missing solvency premise, not a new peg bug.

### PassThroughCheck 3 and 4: FS10

The 12-month goods-rate step is correct in all 36 samples, as are market
prices. The diagnostic reads steady/weak struck rates 0.711066393582 /
0.730774294144, but the same groceries component 1.25111334851. Both
retailers have floor = opening price = 0.0100322435936, clearing prices
0.0282459064660 / 0.0281748960653 above CLEARING_CAP (1.5) times that
floor, and identical shelf 0.0150382539224 and charged price
0.0150488309849. Both actually sell 1,544 baskets. Thus the common
opening-floor/cap binds, not the imported food's marginal cost: a positive
groceries response in this particular twelfth month was not caused.

The one-for-one trace has zero lag error over every compared month and
zero excess months. Its largest D$ gap is the final sample, t120:
-1.31321457634 logs. Goods t120 are -1.29550903678, exactly D$ t119.
There is no t121 goods sample to reach the last D$ peak. The printed
129.55% against 131.32% is the correct lag, not failed transmission.
★ FS10 should cause a cost-sensitive shelf and a held peak or complete
lagged horizon, retaining the month-by-month identity. PassThroughCheck
belongs to FS10 in flaky-suite.md section 6.9, including its shop response.

### KeptCheck 44: FS4

Main has cash -48,349,463; its helper finds one House, gap 48,820,336,
that both market offers cover four times over. RN1-H's played city has
cash 599,140,323. Its short-run fixture succeeds with 100,000 Crude
Pipelines, gap 57,890,862,074, with bond/bill bids 3,335,388,820 /
3,081,727,366; its covered-run search returns null. The helper only checks
counts 1, 10, ... 100,000,000 and stops a building's search at its first
positive gap whose quotes are short. This search is not a cause of a
covered run in every possible city's cash, land and lending state.
All tested kept/new page identities continue to pass.
★ Cause the cash gap and both offers on an order the model permits,
rather than relying on this played city's decade-sized candidates. This
is section 44, below 55, so FS4, not FS5. It does not establish a loan or
funding-page fault. Its assertion, exit 1 and 1 FAILED are all named.

### Publication records and reuse

All 16 exact unnamed lines from the stopped gate are transferred through
the ship file: AutoBuildCheck 3 to FS12, ExchangeRateCheck 6 to FS10,
PassThroughCheck 4 to FS10, KeptCheck 3 to FS4. These sweeps are already
queued; no new queue row or broad red-ok is used. The separate real
RN1-H-M layout finding remains under its own queued fix.

The diagnostic Java class and scripts live only in ignored target/;
their class files use a separate diagnostic-classes directory. The
production and harness sources remain byte-identical to ready 4860daec6.
No new ensemble is warranted, and no suite was rerun: only the eight
requested named controls and the narrow diagnostics above. The complete
stopped suite is copied into an owned rejudge directory; the gate's
original hashes and files remain untouched.

Raw named outputs, diagnostics, helper source, original unnamed list,
suite log and provenance are preserved at
`C:/Users/Jerus/AppData/Local/Temp/cbs-RN1-H-gate-mends-4860daec6/`.

The copied full suite was rejudged with CBS_REJUDGE=--rejudge and the
combined fork registry plus ship rows. It exits 3: known red only in 15
harnesses, no new unnamed line. known-reds.sh validates the ship records.
Only the ship file and these notes change; the gate can reuse its complete
kept run, as authorized at 11:35. The owned main control checkout is removed
after its clean status and complete archived controls are checked.
