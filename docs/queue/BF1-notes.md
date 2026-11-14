# BF1 — The hands-off bank fails on the sawmills

Built from main e5642145 (SX1, 0.9.101), on batch/BF1. HW1's named
files are kept out of this batch.

Readiness remains provisional while the reviewed model completes its
ensemble and fresh-clone gate. All five review findings have causal
fixtures and mends; the last economic change is 85fb890a. Its hands-off
count is 1/24 failures, against 5/24 on current SX1 (LD2's historical 8/24),
with all 2,880 monthly MoneyAudits and bank equity bridges closed.

The earlier readiness of 502afa0b was withdrawn. Results on 272bef8f and
its ensemble below are superseded history. Later review and verification
sections record each replacement. Ordinary insured mortgages retain zero
risk weight but finite leverage; liquidation claims persist in the existing
instrument save JSON and repay when proceeds enter cash.

## Evidence before the mends

- LD2's historical count was 8/24; the current SX1 tree is 5/24 (seeds
  7, 10, 14, 18, 23). The original Ld2Bank probe is retained in the tree;
  BF1 also audits all 120 months, including months after a bank fails.
- BankFailureCheck --fixtures on unchanged model: five failed laws,
  all fixture premises green. The mill stocks its entire low-rate output
  under DUMP_THRESHOLD with no local demand; exports get only the spare
  past that speculative stock target. Closing sells land/material but
  leaves loans; the default slice is not a whole-borrower resolution.
  New growth above the bank's current target has an infinite allowance,
  and the allowance struck before defaults is reused after losses.
- Historical source 31f675b6 was compiled under target/bf1-historic, using
  scratch GameFiles only. Seed 4 m48→49: 14,444.25931 shares become
  14.44426, Exchange.getSplit=.001. The world's sales transfer to the
  desk before the consolidation. Its fair value/mark falls at the same
  time. This disproves LD2's inference that shares disappeared without
  a split. SX1 moves when this happens; its split settlement is preserved.

## Calls

- ★ Exportable stock left after local buyers should also reach the world's
  buyers, using the existing net-bid/marginal-cost and dump-line rules.
  The planner's export-profit premise must agree with actual sales.
- ★ Sale proceeds repay the closing borrower's bank loans in rank order;
  an emptied borrower enters the existing resolution and exclusion rules.
  Remaining productive assets or work on site permit carrying the loans.
- ★ Growth commitments must fit the bank's own capital target after losses
  and consume one shared allowance; working-capital promises keep their
  existing treatment. No rule equates total lending with paid-in capital.
- The apparent share-count loss is tested in split-adjusted units. A fair
  valuation loss is not repaired by handing investors their book equity.

## Checks and records

Pre-mend logs are target/checks/bf1-fixtures-before.log,
bf1-count-before.log, bf1-before-seeds.log and bf1-ld2-shares.log.

## Implementation review

The final sales path keeps the existing stock target: Markets clears local
orders, then ProcessingSector.shipUnsoldStock sells the shed's remainder to the world
at its net-bid marginal-cost/dump-line rule. The following production fills
the shed again. This fixes the missing buyer, rather than removing inventories;
WoodProductsCheck's positive-yard premise passes unchanged. The revised mill
fixture explicitly makes a first month's stock and asks for its second-month
export; it checks the same deadlock without assuming zero stock or immediate
sales from newly produced stock.

settleRetirement pays cash against interim claims first, then ordinary bank
claims pro rata; repayments enter the existing cash settlement and debt
roll-forward. If no plant or work on site remains, the refreshed borrower
goes through the existing backstop even if its only assets were sale cash.
A closure inside an existing default episode resolves the remainder without
counting a second episode. A partial retirement carries the unpaid balance.

Bank.projectHeadroom supplies a finite shared growth budget using the existing
headroom, capitalTarget and leverageTarget. Defaults beyond their sector's
allowance reduce it before replacements can be financed. Uninsured projects
read it; insured mortgages and working-capital lines retain their own rules.
No save format change: the closure handoff is consumed within the turn, the
growth allowance is struck at the desk's existing monthly reprice, and the
repayment/save records already carry these flows.

★ Failure frequency is an observation, as the brief requests; a bank taking
real credit losses is not guaranteed survival. An initial exploratory assertion
of universal survival was too strong and is not the model's rule. It is replaced
by the bank's equity roll-forward at MoneyAudit's own tolerance, alongside the
24-seed count and hard causal fixtures. The remaining failures are reported,
not hidden behind a special seed or a rescue.

The final low-fill mill fixture includes a wage bill and imported feed. With
Sector and Markets replaced by the unchanged e5642145 versions (all other
fixes left on), all its premises remain green and three sales/invoice laws
fail; the BF1 versions pass. Logs: target/checks/bf1-stock-before.log and
bf1-final-fixtures.log. Closing and capital refusal were independently red
before their mends in bf1-fixtures-before.log.

The first full pick's default 2,400-month LongPlaytest is green. Its economic
trace differs from the stored SX1 gate's LongPlaytest.log beyond runtime
(first served-works reading and the market/stock series differ), so the final
model must run the ensemble against docs/ensemble/0.9.101.md.

The first full pick exposed a missing ordering constraint: warehouse exports
could sell stock after the warehouse had been demolished, ahead of the old
end-of-market cap. GdpCheck's unchanged write-off fixtures caught it. Stock
past the surviving room is now written off before the market or the closing
borrower's credit reading. Production adds any further write-off to the same
existing flow. The initial BF1 ensemble snapshot was stopped by its own run
folder/PIDs and is superseded; the final model will use a new label.

The changed path also exposes KeptCheck 55's Bank Overview tag churn: 9 months
allocate one label and 6 allocate two, most saying "defaulting". Its unchanged
25-month prefix reproduces 8 quiet months. Pieces.rungRow now retains its status
label hidden and unmanaged when absent. This is a UI reuse fix; it does not
change what the player sees or weaken the assertion. Temporary instrumentation
is isolated under target/bf1-kept-classes and is not in the source.

## Corrections from the first full pick

The generic warehouse exporter reached food and refinery policies which were
not the processing planner's contract. The final exporter is ProcessingSector's:
Wood Products, Copper Refining, Cement & Concrete and Heavy Industry. Sector's
hook defaults to no shipment, preserving other sectors' sales policies.
Consequently GdpCheck, SaveFileCheck, RefineryCheck, GoldMiningCheck and SlopeCheck
pass, including the original food-conservation law. No HW1 source or fixture
is changed. The temporary GDP restocking change was removed.

The narrower model's first price-level twin diverged physically at month 39.
The first differing trade was an elder household's Industry sale at month 16:
its bid was one ULP below its ask at the factor, and equal at today's prices.
★ OrderBook treats prices inside MoneyAudit's existing relative floating-point
allowance (floor zero) as equal for crossing, priority and depth. The original
book fails ten new denomination/crossing/FIFO/depth laws; the corrected book
passes them. A real gap beyond the allowance still does not trade. This
removes the physical separation and most PriceLevelCheck failures.

An independent caused repayment fixture also finds that a one-ULP negative
cash tail can draw three months of the sector's losses as fresh credit.
★ EconomyManager normalizes that repayment tail using MoneyAudit's existing
allowance before asset sales and credit. The fixture removes alternative
liquid assets and asserts a positive pre-tax loss: old EconomyManager fails
both no-buffer/empty-till laws; the real-shortfall control still draws the
unchanged BUFFER_MONTHS allowance. Logs: bf1-round-before.log and
bf1-round-after.log. This rounding fix alone did not cure the twin path.

The first full pick also exposed three fixture assumptions. BankCheck's 200x
capital injection is topped up if subsequent lending means its reserves no
longer dwarf the live book; the 100x premise is unchanged. CurrencyRegimeCheck
keeps searching for its actual gain-branch window, stopping at the first hold,
with a 600-month cap (the probe reaches its first full window at month 173);
the gain/dent laws and inflation tolerance are unchanged. DebtExchangeCheck
compares the fall month's residual with a no-move counterfactual in the same
month, whose opening month, cash and debt must match, rather than assuming
ordinary residuals equal in successive months. Both financial controls passed
in isolated runs without relaxing a tolerance.

## Price-level regressions traced to money floors

After the crossing repair, the first remaining non-roundoff cash difference
is Construction at month 44. A temporary setter trace locates it in the bond
market, not production or retirement: its face dust still has the old
1e-9 nominal value at the founding price level. Small household-company
trades are omitted in different denominations. Scaling that floor removes
the five nominal-series failures; only two Truck quantity lines remain at
the 1e-9 comparison boundary.

★ The bond face, foreign-move, household-move, fair-price and offering-tail
floors are money constants. They now enter through PriceLevel.atFounding,
then retain their existing currency-reform scaling. PriceLevelCheck includes
all five anchors in its hard constant comparison. Its tolerance, nudge,
growth, count, history and cutoff laws are unchanged. BankFailureCheck's
above/below bond-face fixture fails both above-floor trades on the old
market, at units 1 and 100, while its below-floor controls pass.
Its numeric identities now also use MoneyAudit's own floor-zero tolerance.
The exploratory 3273cbab ensemble snapshot was stopped by its own run
folder/PIDs; a new final-model label will supersede it.

The two remaining Truck misses were about 2.5e-10 of a truck, just over
1e-9 of that month's small fleet gap. The attempted historical-peak
comparison was rejected in REV-BF1-1: despite keeping the literal tolerance,
it widened the effective real-series band. That decision is withdrawn.
The real law is restored to the current value, and the synthetic [100, 1]
fixture must reject 5e-8 at its second point.

## Money review

Retirement debits the actual sector till, reduces instrument principal and
records REPAID in the existing ledger; the bank receives that repayment at
its monthly settle. It does not book repayment as capital spending or as a
creditor write-off. Interim seniority and mortgage-repaid flows are retained.
The emptied borrower's remaining loss uses the existing backstop, including
bondholders, insurance and exclusion accounting. Early inventory write-off
uses the existing output loss flow, which production adds to rather than
overwrites. Growth room is a commitment limit, not a money movement; pending
net lending is deducted before the post-default budget is used. Export
invoices use Trade.WORLD and the existing market booking, not a direct till
credit. Order fills still settle at the resting price. No save field or
save-format change is needed for these within-turn handoffs.

On final source f00e0496, PriceLevelCheck passes the full horizons: default
60/60, built 120/120, played 120/120 and town 120/120. None of these cities is
trimmed by the nudge guard. That preflight's ALL pick and ensemble followed
from that source; the final 272bef8f evidence below supersedes them. The focused denomination and order-book checks,
including all 2,880 BankFailureCheck monthly audits, passed before this pick.

## Corrections from the f00e0496 full run

The first final-model ensemble exposed an own regression: households with no
home in AUTO2 seeds 7, 10 and 13 (and the same pre-switch ZERO_SLACK paths).
M1 stayed green. The new budget had also reached insured mortgages, contrary
to Bank.RISK_INSURED_MORTGAGE = 0 and the mortgage desk's explicit exemption
while the risk-based rule binds. ★ Preserve that exemption; the leverage rule
still rations insured mortgages through capitalRoom as before. A caused
fixture with no uninsured project budget fails the insured loan before this
correction and passes after it; switching on leverage rationing still refuses
that identical loan. Logs: bf1-insurance-before.log and bf1-insurance-after.log.
The obsolete ensemble was stopped through its run-folder/PID stop command.
Its F1 failures are not waived. A full seed-10 replay is checking the correction.

BankCheck's top-up capital entered cash, not the reserve sheet read immediately
after it. Let one press lodge it before the unchanged 100x premise is read.
The identical financial fixture then has 170x reserves and passes all laws;
bf1-bank-press-final.log exits 0.

KeptCheck 64 finds an optional 'reopened' phrase retaining a numeric label's
alignment and 134-pixel width, where the fresh phrase is 44 pixels wide.
FinancesScreen.lineCell now gives its terms their own scope so an added phrase
cannot borrow the next numeric cell's label. The unchanged fresh/kept equality
failed twice before the correction; an identical prefix replay checks it after.

Corrected AUTO2 seeds 7, 10 and 13 each complete all 2,400 months with no
finding and no money-audit miss (bf1-insurance-seed7/10/13.csv/.log). The corrected 24-city
BankFailureCheck count also passes every audit and bank equity bridge
(bf1-insurance-count.log). The final ensemble snapshot is 7e29b76e, label BF1-7e29,
against docs/ensemble/0.9.101.md. The model changes here stop short of HW1's named files.

On corrected source 7e29b76e, PriceLevelCheck again passes its complete
60/120/120/120-month horizons, without trimming. The full ALL pick is
target/checks/bf1-pick-7e29.txt. The obsolete f00e ALL process supplied no
passing verdict: jcmd verified its BF1 user.dir before its PID tree alone
was stopped. Relevant logs are in target/bf1-f00e-preflight. The 768 MB
display diagnostic exhausted its heap; the identical old-model prefix
replay uses the harness's normal 2 GB and soft timing, with its correctness
assertions unchanged.

## Unrelated final map reds

The final-source replay with main's unchanged mapper reproduces the network
and junction failures too (bf1-main-mapper-7e29.log): 625 road plots plus a
67-plot piece, with the city's ground joining them, despite a connected
district plan. Bounds and reproduction are in docs/queue/BF1-map.md, queued
as BF1-M through the ship file. Closest + 4 against LATTICE 8 remains PX1;
the existing full map-count oracle row remains DM3. No map source or oracle
is changed. The ship file names the two newly reached assertion parts;
there is no BF1-owned known-red row to transfer.

## 7e29 preflight ensemble and display control

The final ensemble, target/ensemble/BF1-7e29 (snapshot 7e29b76e), completed
80/80 runs, none short or missing, in 27.1 minutes. Exit 0: all 10 hard M1/F1
criteria pass, with zero audit failures/unread months and zero runs with a
finding in every policy. BF1 scores 23/26 against SX1's 24/26. All changed
verdicts below are informational, as Jerus specified:

| criterion | SX1 baseline | BF1 | verdict change |
|---|---|---|---|
| AUTO2 A1, yearly inflation m240–end | 2.89% median, 11/16 in band | 2.82%, 15/16 | FAIL → PASS |
| SHOCK T2, final population | 115,870 | 77,432, below 75% of baseline | PASS → FAIL |
| HIGH T2, final population | 63,992 | 35,130, below 75% of baseline | PASS → FAIL |

The committed baseline is docs/ensemble/BF1.md, carrying the next-version
token only in its heading/model line. Main was not rerun for a baseline.
Score and raw CSVs remain under target/ensemble/BF1-7e29.

The old-model (f00e classes) identical Kept prefix through section 64,
using the normal 2 GB and soft timing, now has zero DeskCurve fresh/kept
differences. It exits 1 only for the existing KF63 retention case; the
previous source had two DeskCurve equality misses on this exact prefix.
Log: target/checks/bf1-finances-prefix-2g.log. The final full KeptCheck also
finishes with only the existing KF63 line. Both scope fixes are verified
without changing its signature, pixel or retention laws.

## Corrections from the completed 7e29 preflight

The 7e29 full pick also found one own stock-policy regression: early
warehouse loss ran before Refining's existing O1 liquidation. OilCheck 11's
unchanged fixture caught 15.9M litres being written off instead of sold.
★ Refining defers that early loss to its existing produceStock, which ships
past-room oil before any cap; oil does not spoil and its tanks are emptied
before demolition. OilCheck and the unchanged GDP/stock laws now pass.

CallCheck's first-call equality found another money arithmetic issue:
principal times 100 divided by 100 can differ by one ULP from principal.
★ Divide CallTree.CALL_PRICE by 100 first. A call at par is then exactly
principal; the existing call/holder/audit fixture passes without a tolerance
change.

PrizeCheck's next-month ordering fixture was missing two of its own
conditions: the village could staff only 70% of a lab against the model's
80% gate, and its eligible owners could put up only 547 of the 990 needed.
★ Use the existing extra-college-head helper, and give eligible owner cells
a liquidity buffer derived from lab cost/stake and SHARE_OF_EXCESS, equally
in all three controls. The fixture still asserts a positive owner raise,
no loan, the one-field/month limit and no order one prize-hair under.
Its isolated section 12 now passes (bf1-prize-section12.log).

StaffingDrawCheck's saved 1900 city has 638 people of room and no single
building that needs all of it. ★ Retain the same month, door census and
one-building comparisons, and also ask the first doubled order quantity
that binds the room. Both quantities compare every sector/template against
the saved twin to the bit. Its original cap/formula and all save laws pass.

The seven-harness run bf1-last-causes.txt passes Oil, Call, StaffingDraw,
GDP, BankFailure and PriceLevel; it caught the owner-liquidity condition
then corrected in the isolated Prize control. BankFailure stays 0/24 with
all 2,880 audits. These last model corrections supersede the 7e29 ensemble
as final evidence: that preflight score is retained above, and the final
model must run its own full ensemble before readiness.
The extra PlanCheck line is the same PX1 + floor, not a new map-law waiver.

## Final model ensemble

Snapshot 272bef8f, label BF1-272b, completes 80/80 runs with no missing or
short run in 33.7 minutes. Exit 0: M1/F1 pass 10/10, every policy has zero
audit failures/unread months and zero of 16 runs with a finding. Its final
score is 23/26 against SX1 0.9.101 at 24/26. The final baseline, replacing
the 7e29 preflight artifact, is docs/ensemble/BF1.md. Raw CSVs and score
are under target/ensemble/BF1-272b. The final full pick is
target/checks/bf1-pick-release.txt and has exited 3.

The complete changed-verdict rows (all informational) are:

| policy | id | class | measure | BF1 median [range] | threshold | BF1 verdict | SX1 value | SX1 verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.82% [2.42% to 3.37%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.89% [2.49% to 3.36%] | FAIL |
| SHOCK | T2 | info | final population | 77,432 [9,054 to 142,217] | ≥ 75.00% of baseline 115,870* | FAIL | 115,870 [12,480 to 168,445] | PASS |
| HIGH | T2 | info | final population | 35,130 [2,463 to 78,200] | ≥ 75.00% of baseline 63,992* | FAIL | 63,992 [6,402 to 142,318] | PASS |

## Final full pick

The final --pick selects ALL 200 and exits 3: 193 harnesses run in 2,551 s,
189 pass and four have known reds only: MapCheck (DM3/BF1-M/PX1), PlanCheck
(PX1), KeptCheck (KF63) and DeathRecordCheck (OX1). Every owned regression
is green, including Oil, Call, Prize, StaffingDraw, bank/credit, GDP, save
and full price-level checks. Logs: target/checks/bf1-pick-release.txt and
target/checks/suite.txt. Maps regenerate; Stale reports zero firm findings
(185 soft); tree validation passes. Fifteen timing assertions are soft and
eight path readings are observations; no correctness failure is reclassified
as timing. The runner held 26 starts until commit charge fell under its
existing 75% threshold on the shared PC.

No scripts or checking machinery change. AllChecks' harness registration
is one line in and one out; the remaining harness edits are domain fixtures.
Generated maps, dials, harness index and month order are committed with the
final baseline before the fresh-clone gate.

## Kept fresh-clone dry run and readiness

The required versioned dry run, sh tools/gate.sh BF1 --dry-run, exits 0.
Its suite exits 3: 193 harnesses run in 2,230 s, 189 pass and four have only
the documented known reds (MapCheck, PlanCheck, KeptCheck, DeathRecordCheck).
The publisher's complete check phase takes 2,315 s. There are no unnamed
reds; maps regenerate, Stale has zero firm findings (185 soft), and tree
validation passes. The dry run tests version 0.9.102 and publishes nothing.

Kept directory:
/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-BF1/target/bf1-gate-release

Its identities are:

- branch: 5c4bf6d8c655994c1ff8407be6370c643c6bd9ff;
- main: e56421457c98e0c5d115ebdbd9e701dcdd7a4fc6.

The run retains ship.log, checks/suite.txt, checks.sha256, checks.complete,
branch.commit, main.commit and the selection. The launcher's wrapper log is
target/checks/bf1-gate-release.txt. The ship file names this directory for
reuse. After this kept run only these notes and BF1-ship.md advance; source,
generated indexes and the final 272bef8f ensemble baseline stay unchanged.
The provisional marker is removed last and the ship file is pushed last.

## REV-BF1-1: readiness withdrawn

The review mail was missed before 502afa0b was marked ready. Readiness is withdrawn. The three findings require model changes, so the previous pick, ensemble and gate describe the previous model only. The bank bond allowance, approximate order-book ordering and real-series relative comparison will each receive a failing-before assertion and a repair before all three release checks are rerun.

REV-BF1-1 failing-before evidence: target/checks/bf1-review-before-bank.txt
has two capital failures with a positive actual bond purchase (73,767.70)
and room wrongly unchanged at 98,357.11. The fixture funds a bank with a
substantial loan book so that underwriting fees cannot conceal the capital
used. The order book counterexample fails submit and depth on both sides
(four assertions). The isolated comparison fixture fails its required
rejection of the 5e-8 discrepancy after a peak. Their after logs show the
repairs pass. ★ Keep approximate FIFO priority and scan past a noncrossing
order, because numerical equivalence bands overlap and are not transitive.
★ Both bond issue bids and secondary settlement use the same live room as
project loans; pending loans are subtracted before their bank settle, while
immediate bond/carry/securities claims are read from the bank's books.

The Truck cause is now isolated rather than excused by the comparison.
The f00e classes reproduce both m113 Truck lines at a 1.00459965935e-9
pointwise relative gap with the restored comparison. Changing only
f00e's incorrect uninsured-budget restriction on insured mortgages to
the 7e29 exemption makes both lines pass all 120 months: worst
1.01515966488e-11 at m94, identical to the final model. That is the
model repair which had already removed the Truck failures; keeping the
historical-peak comparison after it was unnecessary and wrong.
The insured-mortgage fixture in BankFailureCheck causes zero-risk lending
with zero uninsured room and retains the leverage refusal control.
Logs: bf1-review-truck-f00e.txt, bf1-review-truck-insured-only.txt and
bf1-review-truck-120.txt, all under target/checks. The isolated old
BondMarket control also fails the added reverse pending-loan/bond law
(bf1-review-before-bond-bid.txt), while the final fixture passes.
The complete restored-law PriceLevelCheck exits 0. Its existing nudge
guard reaches 60/120/116/120 months; the direct Truck control above checks
all 120 played months without that cutoff. No comparison tolerance,
quantity floor, nominal grid rule or count equality is widened.

★ The live allowance stops treating loans as pending immediately after
Game refreshes the bank's settled loan book. Otherwise secondary bond
orders would subtract loans already in that book a second time.
A separate fixture leaves positive room after a loan settles and proves
that it is counted once, with later credit still charged once. A no-op
settlement mutation fails that assertion (bf1-review-settled-control.txt);
the final fixture passes (bf1-review-settled-fixture.txt). Its counters
reset with the audit month and scale with redenomination; they are
within-turn state. The e632 pick was stopped at verified BF1 AllChecks
parent PID 2972 before this model correction; it supplies no release
verdict. No review-run ensemble had started.

## REV-BF1-2

Received during validation of 8148417b; its ALL process was stopped by
verified BF1 parent PID 29832 (JDK child 20068). No ensemble had started.
The review confirmed the OrderBook and pointwise PriceLevel repairs.
Its three requested cases are now caused in BankFailureCheck.

- The settlement fixture now actually buys a bond from the capital left
  after the bank's loan-book refresh. A current-source no-op settlement
  mutation fails both the room law and this purchase law (two failures in
  bf1-rev2-settlement-control.txt).
- A bank above target, with leverage binding and infinite old monthly
  growth, accepts an over-large insured mortgage before the mend. ★ Insured
  mortgages retain their zero risk weight but read finite exposure room
  while leverage binds, net of all pending loans. An affordable insured
  mortgage remains allowed even with zero uninsured room. Forecast losses
  apply before the book settles; settled books are read once afterward.
- A last Oil Refinery's retained diesel covers its loan, but the 60% asset
  target would not. Before the mend the creditor loses money. ★ Carry that
  claim against liquid inventory at net export proceeds, a floor before
  applying any asset haircut, until O1's existing liquidation line sells
  it. The fixture also runs that line and verifies the export invoice
  covers the carried claim. Whole-borrower closure is judged once; its
  surviving claim does not take another firms' default slice in the same
  month. Perishable stock still receives no such floor.

The before fixture has three failures with all premises green;
bf1-rev2-before.txt and bf1-rev2-after.txt are under target/checks.
Records from the earlier picks and ensembles remain superseded.

The refinery premise explicitly refreshes credit assets after demolition,
so its comparison with RESTRUCTURE_TARGET is against the retained-stock
balance sheet. Independent current-source mutations confirm the causes:
removing only the oil recovery floor fails both creditor laws; restoring
only the old mortgage guard fails the above-target leverage refusal.
The strengthened normal fixture passes. Logs:
bf1-rev2-refreshed-fixture.txt, bf1-rev2-floor-control.txt and
bf1-rev2-guard-control.txt under target/checks.

## REV-BF1-3 — follow the claim through banked cash

The prior fixture stopped at the invoice; the extended live-city fixture
fails on the old model when the loan survives and takes a later haircut
(bf1-rev3-before.txt). No pick or ensemble was running when this arrived.
The earlier named run completed six harnesses, exit 0, but is superseded.

★ A last-plant closure marks each surviving bank loan as a retirement
claim. The marker is saved on BusinessDebt: DataSave's instrument JSON
writes it, and Game's subtype Gson load reads it. The fixture saves and
reloads the whole scratch city between closure and liquidation. Old
unmarked loans remain ordinary loans, so no format bump or conversion is
needed; SAVE_FORMAT and VERSION are unchanged.

★ Each credit asset refresh values the retired oil and its pending sale
invoices at net realizable proceeds. The saved loan marker preserves the
closed-claim judgment until cash is banked. Positive cash repays marked
claims, in the existing seniority order, at credit settlement before
new borrowing or another default judgment can reuse it. The monthly
closed-borrower cache can clear without losing the saved obligation.

The real month sequence matters: stock is liquidated at the bottom of
one month, and its invoice is banked in the following month. The fixture
therefore follows both months, live and reloaded, asserting that the
invoice covers the surviving claim while it waits, that the bullet is
then repaid in full, and that neither turn substitutes a haircut for
repayment. It starts after founding prices have been struck; its loan is
midway between RESTRUCTURE_TARGET of the current inventory and net export
proceeds, and funds the waiting interest separately. All premises are
explicit. The after fixture passes; removing only the cash repayment
call fails both repayment/haircut pairs (bf1-rev3-no-pay-control.txt).
Logs are under target/checks; only GameFiles.scratch slot 10 is used.

The final-model named run, bf1-rev3-named.txt, exits 0: BankFailureCheck,
SaveFileCheck, ReadPathCheck and OilCheck all pass; maps regenerate and
Stale has zero firm findings (185 soft). Model b8898a07 counts 0/24 bank
failures, 2,880 closed MoneyAudits, 210 pellet-sale months and 485
split/consolidation events. The a720 intermediate count was 1/24;
its seed-13 trace starts with Copper Refining losses while new project
room is zero (bf1-rev2-seed13.txt). It is superseded by the completed
retirement-cash repair and the final 0/24 count.

## REV-BF1-4 — maturity while the invoice waits

The b8898a07 ALL pick was stopped at verified BF1 JDK PID 2084,
parent 18868, before changing the model. No ensemble had started.

The refinery fixture now runs both its original bullet and an aged
bullet with exactly one month left: its saved maturity is the pending
invoice month. Before the mend the latter fails eight laws, live and
reloaded, because maturity removes the marked loan and refinancing
replaces it with an ordinary loan. All fixture premises are green
(bf1-rev4-before.txt).

★ Keep a marked retired bullet on its liquidation path at maturity,
rather than refinance its principal while proceeds wait. Its remaining
clock stops at zero; the same claim and interest rate stand, interest
continues to accrue, and the existing cash repayment clears it when the
invoice is banked. Ordinary bullets and mortgage terms keep their rules.
The fixture asserts that the maturing claim stays registered and marked,
does not draw replacement credit, takes no haircut while the invoice
waits, and is fully repaid from banked cash. Both live and whole-city
save/reload cases pass (bf1-rev4-after.txt).

Final source 29064793: the required four named checks pass, exit 0
(bf1-rev4-named.txt). The mature-claim mend changes the count to **1/24**,
with 2,880 closed audits, 210 pellet-sale months and 476 split/consolidation
events. The ALL run independently reproduces that count. My first bus
message incorrectly carried forward b889's 0/24; it was corrected before
readiness.

The remaining case is seed 14 at game month 120. The final-source trace,
bf1-final-seed14.txt, shows Manufacturing closing with assets and debt
both ending at zero, a 340.7879 creditor loss, and bank equity moving from
520.52 to -43.92; the next turn reaches -256.81 after Industry/Freight
losses. Wood Products has zero assets, debt and creditor loss there.
The bank's equity roll-forward residual stays around 1e-12. This is
asset/credit and desk risk, not the unsold-pellet/repeated replacement-loan
fault. ★ Count remaining failures as outcomes, rather than promising that
a risk-taking bank can never fail; the caused laws and model constants
remain hard. Current SX1 was 5/24 and LD2's historical report was 8/24.

## REV-BF1-5 — remove the written-off claim

Stopped the 29064793 pick at verified BF1 JDK PID 33436, parent 13544.
No ensemble had started. A marked bullet with one month left is written
down to zero, with zero interest and no cash available for a later sweep.
Before the mend it remains in the loan list and Loans running after
maturity: two failed assertions (bf1-rev5-before.txt). At maturity the
liquidation path now removes a claim whose principal and interest are
both zero. The same fixture passes, while the positive maturing-claim
save/reload fixture still retains its real claim until repayment
(bf1-rev5-after.txt). No counter or tolerance is changed.

## Final ALL findings after the model review

The 85fb890a full pick exits 4 (bf1-final-pick-85fb.txt): one
OrderSearchCheck premise and two KeptCheck comparisons are new reds.
The bank's 24-seed count remains 1/24 with all audits closed.

★ Cause the copy's below-counted search explicitly using tillsAsTheLine,
the existing control already applied to its twin. This copy's natural
interest orders all pass whole or are refused, so they supply no deep
boundary. The added zero-earnings cash-boundary cases exhaust the lower
search and are checked against the same countdown; the twin still finds
positive deep trims. No assertion, tolerance or countdown is weakened.
OrderSearchCheck then passes (bf1-ordersearch-after.txt).

KeptCheck: Search "#3200" on Bonds retains a 1466-wide body against a
fresh 1454-wide body. Partners has one 62-pixel difference at month 3169
with equal signatures. An unchanged normal prefix through Trade reproduces
the Bonds bounds failure but not the Partners pixel failure
(bf1-kept-review-prefix.txt). Focused UI diagnosis is in progress; no
release verdict or ensemble is taken from the red ALL run.

The natural copy is now printed before controlled cases: 14 interest
decisions: seven large Retail orders and two one-building Manufacturing
orders pass whole; five one-building Manufacturing orders are refused. At m502, 5,677 stores cost 1,099,890 before carry
1.5829 against 1,551,777 cash and 12,739,571 project room. Monthly profit
per unit is 5.956, so the whole order carries. At m508, 2,235 stores cost
445,712 before carry 1.5394, covered by 869,342 cash even with zero new
project room. These orders provide no natural interior size boundary.

This is the changed model's order composition/finances, not a search
error. A same-input 24-month ablation giving the copy infinite project
room still makes exactly the same 14 decisions and zero deep trims.
The new ceiling is not clipping these measured decisions. BF1's earlier
economic path produced this profitable Retail expansion and one-building
Manufacturing refusals, unlike main's old marginal orders. The natural
zero stays printed as information, separately from controlled searches;
the numerical laws stay hard. Logs: bf1-search-cause-copy.txt and
bf1-search-cause-unlimited.txt under target/checks.

Main UI controls reproduce both Kept reds exactly: main FinancesScreen
and Pieces ahead of BF1's model, with FundScreen/PartnersPage unchanged.
Bonds has the same 1466/1454 bounds and Partners the same 62 pixels at
m3169. BF1's FinancesScreen repair is not their cause. Per the 13:11
orchestrator mail, the ship file queues BF1-K and records these unrelated
parts. No UI source or pixel tolerance changes. BF1-kept.md records the
causes, coordinates, frozen inputs and triangle prototype.

The 4eebee8a full pick runs 194 harnesses in 2,635 s: 189 pass;
MapCheck, PlanCheck, DeathRecordCheck and LongPlaytest are known reds.
The sole new failure is KeptCheck's 1,740-second process deadline,
stopping at Policy section 76, while the host also runs HW1's suite and
five ensemble JVMs. The same 85fb economic/UI code completed KeptCheck
in the prior full run; its two functional differences are reproduced
with main UI and filed above. Timeout evidence is copied under
target/bf1-pick-timeout; launcher log bf1-final-pick-4eeb.txt.
No timeout exception is filed. The complete pick and fresh-clone gate
still must finish before readiness. The one final-economic-model
ensemble is run next while the host is busy; the test sequence then
returns to the full pick and gate under lower contention.


## Reviewed model: standalone Kept completion

The orchestrator's 14:24 instruction replaces the planned repeated whole
ALL with KeptCheck alone, then the final ensemble and the decisive fresh
dry-run suite. KeptCheck on d13d7cb8 completes all 100 sections with exit 3:
only the two main-UI findings queued as BF1-K fail. No timeout, numerical
band or node-retention exception is introduced. The prior loaded-host
timeout remains evidence, not a waiver. Standalone logs are retained in
target/bf1-reviewed-kept (bf1-kept-reviewed-alone.txt and KeptCheck.log).
The suite wrapper regenerates maps with no changed generated documents;
Stale remains 0 firm, 185 soft.
