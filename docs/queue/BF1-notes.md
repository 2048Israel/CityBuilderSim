# BF1 — The hands-off bank fails on the sawmills

Built from main e5642145 (SX1, 0.9.101), on batch/BF1. HW1's named
files are kept out of this batch.

Readiness of 502afa0b is withdrawn for REV-BF1-1. The results on model
272bef8f below are superseded; they are retained as history only. The three
review findings and fresh verification are recorded at the end.

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
