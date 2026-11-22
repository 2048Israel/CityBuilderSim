# HC1 — Credit cards and household debt statistics

Started 2026-11-22 from `origin/main` 16ee7adde (0.9.186); HC2 is folded into this batch.

## Built

The card balance remains per household; `revolving` is its holder share,
and `lateDebt` and `lateShare` are subsets of that balance and those holders. `shutOut`
excludes only the share charged off. Closed forms use the model's existing normal CDF.

The bank's agreed neutral card rate is 21.35%, with 3% minimum or interest plus 1%
principal, capped at 35%. Unexpected bills hit a sixth of eligible nonholders each
month at 0.75 months of take-home. Arrears use the balance and holder tails, annualised;
the late stock cures at 15% a month or charges off with the Fed ratio's hazard.

## Calls taken

- ★ `debt` includes `lateDebt`; `revolving` includes `lateShare`. Subsets make the
  existing loan book, bank cash channels and money identities count every dollar once.
- ★ The charge-off hazard is the exact sourced `12 × 2.85 / 3.82`, rather than its
  rounded nine-month description. Exclusion decays by `1 / LOCKOUT_MONTHS`, as the
  study's one-stock approximation requires.
- ★ The bank's rate ladder and motoring's credit-cost quote use its card APR.
  Settlement continues to receive the bank's underlying household funding price
  and adds the card spread once, then the holders' own risk and the cap.
- ★ Four new position slots are followed by ten settled figures (minimum paid,
  emergency spending, net write-off, disposable income, bankruptcies, minimum due,
  deposit interest, student principal repaid, student interest and applied APR). The contract
  requires a flow read between months to be saved, not reconstructed from closing stocks.
- ★ Emergency repairs/replacements are brokered imported services on Business Services'
  ledger. The paid bill funds one imported input and its sales-tax remittance; it has no
  invented domestic capacity or operating margin. Imported VAT is charged and credited
  through the existing ledger. The pending sale is carried in the household transit
  adjustment, as cars, luxuries and meals already are.
  A named `householdServices` receipt is saved in `SectorState`'s pending ledger and
  struck into the statement's saved `otherParts`. It sells no physical seat-service
  good; the three established service goods remain export-only. Its pending counter
  clears and redenominates with its ledger.
- ★ Cars still borrow on the card. Car debt is therefore not added as a second debt book;
  separate car loans and owner mortgages report zero until their mechanics exist (HO1
  owns household mortgages). Student living loans retain their treasury channel;
  unexpected bills use the bank card, subject to the same income/access test.
- ★ Total holders assume independent kinds; total balances use the SCF total-debt spread,
  the study's stated approximation. Per-adult amounts divide each cell by its actual
  grown-ups, then weight by holder adults. Quantiles locate a 32-point log-CDF interval
  and refine just that interval to avoid interpolation errors for mixed cell means.
- ★ Annual debt surveys have their own year axis, with city, tier, stage and tier-by-stage
  groups. They are not monthly history series and do not enter its old-month folding.
  An explicit untiered group carries students and other residents with no pay tier,
  so the tier groups still foot to the whole city.
- ★ The share late 60+ days is the single late stock's stationary survival estimate:
  the late share times two months surviving cure and charge-off. It is explicitly an
  estimate, not six newly introduced saved delinquency buckets.
- ★ Debt service uses the settled contractual minimum due and the student instalment
  and interest. It excludes voluntary extra repayment and asset seizure. Saving the
  minimum due avoids re-pricing an opening-month bill from the closing balance.
- ★ Disposable income for the statistics includes the cell's take-home and net property
  income, plus its actual deposit interest receipt. Deposit interest is saved per cell:
  an allocation reconstructed from closing savings would change it after a payment.
  Assets include marked financial holdings and cars at the used market's price, or its
  own floor against the new-car quote while none has traded. Homes await HO1.
- ★ The over-40 burden shares have household and adult population denominators,
  and household-debtor and adult-debtor denominators separately. Bankruptcy counts
  also expose the grown-ups affected; a two-adult household contributes two adults.
  Aggregate debt/income, debt/assets and debt-service ratios are unchanged by splitting
  each household's dollars evenly among its grown-ups.
- ★ Shares, paper, bonds and money abroad use the debt-free, unexcluded share's
  cash budget. Keeping their former whole-cell “any debt” gate would bar every
  neighbour once even a tiny tail carried a card. The existing relative debt floor
  still ignores arithmetic dust. A raw indebted fixture without a supplied holder
  share is conservatively ineligible; no saved position is converted or invented.

## Findings mended while checking

The emergency draw and the existing waterfall originally each reused the bank's
interest exception to its capital rule. `BankCheck` caught the doubled allowance;
the ceiling now applies to cumulative debt, and `CardCheck` causes both the zero-growth
and positive-growth edges with both draws in the same settlement.

The minimum's priority edge reserves available cash before food; food may then
draw the card. A repayment and an advance can now occur in the same month.
The existing bank growth test therefore measures net advances, includes the loan
fee, and excludes recoveries seized later in the month from recyclable principal.
Its bound is the exact growth allowance plus interest, and a new exact loan-book
identity checks every lending, fee, repayment and loss on both constrained books.

The probability above a line is evaluated through the normal's direct tail, as
the partial balance tail already is. Subtracting a CDF rounded to one could erase
holders while a small balance tail remained. `CardCheck` holds a nine-sigma tail
positive and checks it against the model's direct normal tail.

The first emergency-services pass imported the whole receipt and then remitted sales
tax as well, losing that tax without a margin. `BankCheck`'s slice town caught Business
Services failing in month 5. The receipt now reserves the exact effective sales-tax
share, with the rest imported. The harness checks both a zero and positive tax rate.

Cars posted their finance directly to the balance, so both new and used car purchases
needed to post their borrowing share there too. They still use their existing bank cash
and fee channels. The bank's fee comparison now includes the two cities' measured
emergency purchases and draws; its cent-level cash identity and exact fee tests remain.

The exact current-statistics save comparison found the asset ratio valuing dollars
abroad at the household settlement's opening exchange rate, while a loaded city used
the saved closing rate. Asset valuation now reads the closing rate directly, separate
from the settlement's rate. The exact save assertion remains, and passes. Student
principal and interest are saved as settled flows too, rather than re-priced from the
remaining student balance during the load path's plan.

`HouseholdCheck`'s conservation premises now undo all of the real monthly cash and
loan flows when they inspect the census's opening position. Emergency savings draws
belong in the existing `drawn` counter; seizures and net charge-offs belong in those
opening identities too. A birthday or lease still loses no migration money, while
the late pipeline's independent charge-off is counted separately. Arrivals still
bring no debt, departures still take exactly their prior savings and leave exactly
their prior debt, and five singles still bring five wallets. The cent-level checks
are unchanged. Its old “nobody borrows while the cell has savings” and whole-rich-cell
“owes nothing” premises are replaced by the holding-share outcomes HC1 requires.

The populated save's short-sale premise found that remaining whole-cell investment
gate: its played city had no eligible buyer, even though only a share of each cell
held cards. The gate and cash budgets now apply to the free share. `CardCheck`
causes partial debt, complete debt, partial exclusion and raw debt without a
distribution on the same cash budget; it preserves the no-borrowed-cash rule.

## Saves

The shipped base uses format 60. HC1 raises `SAVE_FORMAT` and
`OLDEST_LOADABLE_FORMAT` to 61 together: a balance without a holder share would read
wrongly. The loader refuses old saves; no conversion is introduced.

## Main and the household page

Merged main's record-only `abc8fd4f5` in `eae43e9d7`, then HS3's shipped
`52c79806b` (0.9.187) in `bc101892c`. The latter changes the household page
and read-only stage getters; it changes no model trace. Generated-map conflicts
were kept locally and regenerated from the merged source by the next fast check.

Only after HS3 shipped, HC1 added the debt survey below the Household money page's
existing sections. Its chips select household/adult, tier, stage, and current month
or the latest saved yearly survey. Three cards lead with holding a card balance,
required debt service, and late balances; details hold every kind's holding share,
median, mean, late shares and net charge-offs, then the income/assets and burden
ratios and bankruptcy/forced-sale counts. The captions name the approximations and
the future books. The open household's own ledger shows its holding/late shares,
minimum due, minimum principal paid, emergency bills and net loss. Credit exclusion
is shown and counted as a share; a neighbour's charge-off no longer labels the
whole cell unable to borrow. Bankruptcy counts are not displayed as lost dollars.

## Checks and remaining work

The model's `./batch-check.sh --fast` passed (53 s compile; Stale 0 firm,
199 soft). Named checks ran one JVM at a time with the prescribed Earth/world
files and `cbs.parallel=1`. Raw logs are `target/hc1-<Harness>-direct.log`.

`CardCheck` passed the closed forms against seeded draws, the minimum's two
branches and priority, both sides of the savings/access/capital edges, partial
arrears, charge-offs, seizures and exclusion, census transport, named emergency
receipts at zero and positive tax, and 24 real-city months. Emergency bills,
card draws, minimums, late stock and charge-offs each fired in all 24 measured
months. Worst pooled/household residuals were respectively 3.82e-9 and 1.67e-9
of their existing tolerances. Its scratch-slot-10 save retained every card field,
the pending and struck service receipts, exact current statistics and yearly history.
Closing holders were 7.736% of households; late households 0.312%, late balances
4.611%, and annualised net charge-offs 0.851%.

`DebtStatisticsCheck`, `HouseholdCheck`, `BankCheck`, `CarCheck`,
`HouseholdFlowCheck`, `HouseholdStageCheck`, `BusinessServicesCheck`,
`SaveFileCheck` and `DenominationCheck` passed. The statistics checks compare
mixture medians with bisection and draws, household/adult weights, tier/stage
footings, signed disposable-income contributions and burden edges. SaveFileCheck
retains exact statistics and all card stocks and flows including the applied APR.
DenominationCheck retains the existing full-city money and view checks.

The final integrated `--fast` passed (57 s compile; regenerated maps; Stale
0 firm, 199 soft), in `target/hc1-fast-integrated.txt`. `MoneyWordsCheck` passed
43 assertions in 18.5 s: actual household/adult holder medians, selected tier and
stage ratios, untiered/outside-family populations, and the saved yearly view after
the current book changes. It also retains HS3's stages, captions, estates and tax
lines, identical kept/fresh pages, scroller position and retained disclosure nodes.
Its fixture closes a real year through normal simulation before selecting that
survey; two initial fixture attempts without a completed year were rejected,
and their hung JavaFX process trees were ended by their own verified PIDs.

For an eye check: People › Household money › Household debt, try a tier and
stage in each counting unit, open details and compare current versus yearly.
Opening a grid cell should show its own card minimum and the fraction excluded,
while a partial exclusion still leaves a monetary credit-room figure.

An early SaveFileCheck invocation with a mistyped world path is excluded from
these results; the correct-path rerun passed. An incidental substring pick of
`BankCheck` also started CentralBankCheck; that run was ended by its own verified
process-tree PID, and is not a check result. The subsequently named direct BankCheck
passed. No conservation or save tolerance was loosened.

The final pick at model/page commit `dfbccb916` is `ALL 243 harnesses`, because
`AllChecks.java` changes. The review line is `needed - money: Bank.java,
DebtStatistics.java, ui/BankScreen.java; saves: HistorySave.java, +SAVE_FORMAT,
+OLDEST_LOADABLE_FORMAT`. Its raw output is `target/hc1-pick-final.txt`.
The laptop ran only fast and the named harnesses; the ALL pick belongs to the
gate box after review, as the batch's machine instructions require.

The integrated `ReadPathCheck` passed: repeated city, tier, stage and survey
statistics reads leave the city's saved state and existing read paths unchanged.
The stranger's money/saves review, final-model ensemble and ALL dry run remain.
The card changes alter the default trace by construction; the 80-run ensemble is
owed after the review's go, on the gate box, followed by the ALL dry run there.
No dry-run log or gate-run reuse is claimed yet.

## REV-HC1, the five findings on ee3e14849

The orchestrator returned five P1s on 2026-11-22. The focused `CardCheck`
cases were first compiled against the reviewed production source, before
any model mend. `target/hc1-review-before-CardCheck.log` records ten failing
assertions: inherited borrower status/loss, a second departure loss of
2.730017783249555, loss of surviving arrears, inherited exclusion and the
rejected 40% financed in full. That same run's default-city next-month
continuation already passed; P5 is added coverage, not a claimed failure
of that default fixture.

1. **Estates.** `Stock.borrowerStatus()` marks all four card positions.
   Estate settlement still repays/writes off the deceased's debt, but those
   positions enter no heir/custody pool. A partner's surviving household and
   other actual life flows still carry their own positions. The native
   dying-alone fixture checks no inherited late debt, holder share or
   exclusion; charging off the heir's book loses exactly zero.
2. **Bankruptcy departures.** ★ The charge-off queues its own departing
   share and that share's cash remaining after seizure. Both are saved
   positions, appended after the ten settled figures (16 HC1 slots total).
   Cash redenominates; the departing share does not. The next census removes
   that debt-free/excluded share first, from its source cell, re-averages
   survivors and only then moves other life flows and the anonymous pools.
   It takes zero card/late debt or active holders and one unit of exclusion
   per departing household. Other assets retain the existing transfer/tax
   path, with only the captured post-seizure cash available to leave.
   ★ The demographic push uses the source families' actual heads, age bands
   and adult stages. Only other net movers use proportional migration.
   A common bound keeps whole source families inside the live age/stage
   counts; migration's admission cap scales those same sources and cash.
   The harness checks unchanged loan/late books, no second write-off,
   unchanged neighbours, source children/stage and capped post-seizure cash.
3. **Access.** Ordinary residual bills are weighted by the accepted share
   before the bank draws; their rejected share remains unfunded. Newly
   holding shares use the fraction of accepted demand actually funded.
   Student living loans retain their own override. Emergency credit was
   already weighted, so its private bank draw is not weighted twice. The
   input case income 1/spending 1.5 now leaves 0.2 unfunded and advances 0.3
   to the bottom tier; partial and full exclusion are checked too.
4. **Authentic old save.** `fixtures/hc1-format60` contains a native save and
   history produced by shipped `.188` source `01e6c2d4b` in an isolated
   detached worktree, through scratch slot 10. Its producer, exact hashes
   and 128-slot layout are recorded beside the compressed bytes. Nothing
   is relabelled or converted. Both CardCheck and SaveFileCheck verify
   provenance, refusal, identical save/history bytes and the running loaded
   twin's complete saved state, history, managers, month and cash. ReadPathCheck
   runs this same native fixture against a running loaded city too. The existing
   loader already refused format 60 correctly; this mends the missing real
   fixture, with no invented loader change or claim that refusal was broken.
5. **Continuation.** Both the normal card city and the populated save's
   original/reloaded twins advance one native month. The checks compare
   exact card positions and pending departures, minimums, emergency-service
   receipts/imports, household loan book and bank capital. The default
   fixture and the populated fixture both pass to the bit, including capital.
   The populated fixture is loaded afresh: its earlier `back` reference was
   later deliberately mutated by the separate fund/rescue cases.

The focused CardCheck passes all three model mends, the native old-save
refusal and 24 measured months with every card mechanism firing. Its worst
pooled/household residuals are 7.25e-9 / 2.80e-9 of the unchanged tolerances;
closing holders 7.337%, late households 0.293%, late balances 4.596%, annual
net charge-offs 0.895%. Final checks and the review handoff remain. After
the review SHIP, merge main and use `docs/ensemble/0.9.188.md` as ordered.

The broad named run passed BankCheck, DebtStatisticsCheck, HouseholdFlowCheck,
BusinessServicesCheck, CarCheck, DenominationCheck, MoneyWordsCheck (43 assertions)
and ReadPathCheck. HouseholdCheck's movement cases were also letting prior
bankruptcy departures occur inside a separately claimed birthday/promotion/arrival
case. ★ Their held-census preparation now actually settles those queued departures
and replacement arrivals before taking the next case's opening position; it does
not erase a pending queue or change a monetary tolerance. The poor cohort now
follows its surviving households and scales its total wage accordingly: keeping
100 on the census had replaced each discharged leaver with a newcomer bringing
the opening buffer. At the same income 1, rent .60 and basket .25 per head, it
now eats short in 283 of the 300 measured months. The rich fixture resets its own
separate census to 100. HouseholdCheck passes again. HouseholdStageCheck's expected
moving-up count now uses the young survivors after the prior bankruptcy departures;
its original bound and household money identity pass.

P5's strict continuation first exposed a fixture inconsistency, not a missing
card saved field. The populated fixture injects new ports shares with
`Ports.restore` between ticks but had not applied them to the live market band.
The loader correctly applies them (`Game.rebuildSimulationState`, calling
`Freight.reapplyBand`); Freight's next native haul therefore bought diesel at
different opening import prices. Its import was 12.19055100829861 live versus
12.029371725183253 loaded. The 0.161179283115357 import difference changed GDP,
then bond demand, the primary issue's fee and capital by about 1.4e-8.
★ Immediately after injecting the fixture's ports state, apply its shares through
`Freight.reapplyBand`, as both the native haul and loader do. The continuation
also checks the opening diesel import price, the complete opening saved state
(except its timestamp), then advances each city one native month and compares
all requested closing fields with exact equality. The populated continuation now
passes, with no new production save field and no tolerance change. An attempted
business-quote snapshot did not affect the failure and was removed, as was all
temporary tracing. No production quote or national-accounts logic was changed.

The orchestrator's 10:53 and 11:42 coordination says RES2/PW5 also have the native
format-60 fixture. Ours was already built, so it stays until integration; the
second ship consolidates to one fixture and bumps save format/floor from the
first ship's 61 to 62. That merge follows the review SHIP, before the ensemble.

Final delta check at model/harness commit `5a5f01392`:

- `./batch-check.sh --fast`: exit 0; compile 55s; Stale 0 firm, 199 soft.
- Named harnesses, run one JVM at a time: SaveFileCheck, CardCheck, BankCheck,
  HouseholdCheck, DebtStatisticsCheck, HouseholdFlowCheck, HouseholdStageCheck,
  BusinessServicesCheck, CarCheck, DenominationCheck, MoneyWordsCheck and
  ReadPathCheck all exit 0. MoneyWordsCheck: 43 assertions, 0 failed.
- SaveFileCheck's populated format-61 continuation and CardCheck's native
  continuation both pass exact equality. Native format-60 refusal also passes
  against the running loaded twin in SaveFileCheck, CardCheck and ReadPathCheck.
- Raw logs: `target/hc1-review-fast.txt`, `target/hc1-review-named.txt`,
  `target/hc1-review-suite-status.txt`, and each `target/hc1-<Harness>-direct.log`.
- `sh tools/pick.sh origin/main HEAD`: ALL 243 harnesses; review needed, money
  (Bank.java, DebtStatistics.java, ui/BankScreen.java), saves (HistorySave.java,
  SAVE_FORMAT, OLDEST_LOADABLE_FORMAT). `known-reds.sh HC1` exits 0; no HC1
  known red is claimed. No conservation, save, or harness tolerance was weakened.

The final fetch found RES2 shipped as `7e484c4da`, 0.9.189, format/floor 61.
Per the review order, it is not merged before the delta review. After SHIP,
merge main and bump HC1 from the shipped value to format/floor 62, retaining
one authentic format-60 fixture. Then run the ensemble against
`docs/ensemble/0.9.188.md` and the ALL dry run on the gate box. The provisional
marker stays until those steps are green. No ensemble, dry-run result, or ready
claim is made at this mends handoff.

## SHIP review and main integration, 2026-11-22

REV-HC1's delta review of `bc137e8be` is SHIP: all five findings mended;
SaveFileCheck's ports-band fixture completion loosens nothing (orchestrator
12:47 mail). This authorizes main integration and the ensemble. The same mail
explicitly requires a new `HC1 dry run?` handoff and an ended turn before the
ALL dry run; its reply is the dry-run go.

Merged shipped `7e484c4da` (0.9.189) as `fb0407f`. Money and housing read
coverage are combined. RES2's exact format-60 pair and enhanced helper are
retained as the one shared fixture: native schema/hashes, refusal at the floor,
fresh and running loaded cities, and repeated reads all remain checked.

★ Both format and floor are now 62, from shipped RES2's 61. Its old
128-slot household cells have no card holder/late/excluded shares or pending
discharged departures, so reading that layout as HC1's would be wrong. No
conversion is added. A second genuine pair was produced by the actual shipped
`7e484c4da` classes, using the committed `tools/fixtures/CardFormat61Fixture.java`
producer, scratch slot 10 and twelve native months (closing month 31). It has
RES2 housing inventory, 141 old 128-slot cells and no HC2 surveys. Provenance and
hashes are in `fixtures/hc1-format61/README.md`. Its detached source worktree was
removed after production. The one refusal helper now checks both actual old
formats, with unchanged file streams and the running loaded city/history;
ReadPathCheck adds repeated reads. The current format-62 continuation remains
an exact next-native-month comparison, including bank capital.

The integrated fast check passes (compile 54s; Stale 0 firm, 199 soft).
Focused integration checks are running serially: CardCheck, SaveFileCheck,
ReadPathCheck, HousingInventoryCheck and ExchangeCheck. Logs are
`target/hc1-integrated-fast.txt`, `target/hc1-integrated-named.txt` and
`target/hc1-integrated-suite-status.txt`.

The five focused integration harnesses initially left eight ExchangeCheck
assertions red. The saver fixture had struck a native household month and then
reset cash alone, retaining new card debt/holder shares. It no longer represented
its stated debt-free savers, reducing the buying budgets. ★ Establish these
exchange-boundary positions with `censusOnly`, then the fixture's stated cash and
take-home. No card balances are erased or defaults disabled. The two-cell
rebalance setup uses the same boundary construction.

The native cell-to-cell sale case still runs the real household waterfall. Its
old 18-spending/4-income/12-cash input now has a distributed savings shortfall
and a separate income-scaled shock, so it is not the asserted pure 2 shortfall.
★ Cause that exact ordinary gap with zero opening cash, zero income and
spending 2; increase the buyer's input cash to 1,000 so its bid covers the
seller's current card-priced reservation. The original checks of sold quantity
at the model's borrowing rate, all cash/holding transfers, exactly 2 raised,
zero debt, zero outside-pool flow and the register remain, with every original
numerical tolerance unchanged. The earlier emigrant wait check passes too.
All eight ExchangeCheck assertions now pass. Production card/exchange logic did
not change. Its shared helper's BankCheck and MarketCheck consumers also pass;
ShortSellingCheck's native 1,000-month audit is still running at this checkpoint.
The fast check passes (0 firm/199 soft); CardCheck, SaveFileCheck, ReadPathCheck,
HousingInventoryCheck, ExchangeCheck, BankCheck and MarketCheck are green.

The current pick is ALL 244 harnesses. The ensemble uses label
`HC1-final-189-20261122`, baseline `docs/ensemble/0.9.188.md`, and no extra
`JAVA_OPTS` (the baseline names data properties only). Laptop data properties
are omitted from its call so GB3 uses the box's matching copies. PW5's ensemble
is running on the box; HC1 is to queue behind it. The provisional marker stays.

## Final-model ensemble, 2026-11-22

The ensemble is PASS, exit 0: 80/80 complete, none missing or short; all ten
M1/F1 hard criteria pass. Every policy has zero money-audit failed months and
zero finding runs. It scored **25/26**, beside baseline `.188` **23/26**, whose
hard score was also 10/10. AUTO2 T2 and ZERO_SLACK T2 change from FAIL to PASS;
A2 remains the informational failure. No criterion or threshold changed.

| policy | .188 median final population | HC1 median final population | .188 / HC1 median seconds |
|---|---:|---:|---:|
| AUTO2 | 2,470,539.5 | 4,772,827.5 | 179.6 / 226.2 |
| ZERO_SLACK | 1,277,612.5 | 3,061,157.5 | 144.4 / 172.2 |
| ZERO_HOT | 7,582.5 | 9,164.5 | 18.4 / 18.9 |
| SHOCK | 40,935.5 | 103,159.5 | 44.4 / 46.9 |
| HIGH | 31,616.5 | 32,997.5 | 35.6 / 37.5 |

This is label `HC1-final-189-20261122`, the frozen pushed model
`d325c74c863f3e823a65aae7fe180bca7d240b02`, on gatebox Java 21.0.13, eight
runs at once, using its own Earth/world copies. It first waited behind PW5's
ensemble, then yielded after 23 completions to PW5's dry run. It resumed the
same snapshot and retained all completed CSVs; it is one final-model run, not
a new model or a rerun of completed cities. Raw files are in
`target/ensemble/HC1-final-189-20261122/`, controller log
`target/hc1-ensemble.txt`, box folder
`/home/jerus/cbs/tools/d325c74c863f3e823a65aae7fe180bca7d240b02/target/ensemble/HC1-final-189-20261122`.
The generated baseline is committed as `docs/ensemble/HC1.md`, with the
publisher's next-version token in its heading and introductory model line.

Final local checks are all green. The integration group passes CardCheck,
SaveFileCheck, ReadPathCheck, HousingInventoryCheck and the repaired
ExchangeCheck. The shared fixture's consumers BankCheck, MarketCheck and
ShortSellingCheck pass too. ShortSellingCheck's native cities with shorts on
and off both pass their money audits and share-register checks; the on run's
worst audit residual is 0.000001 and worst relative register drift 1.08e-14.
The final fast check compiles in 52s with 0 firm / 199 soft Stale findings.
Logs are `target/hc1-exchange-fixtures-fast.txt`,
`target/hc1-exchange-fixture-run.txt`,
`target/hc1-exchange-shared-bank-run.txt`, and
`target/hc1-shared-fixture-checks.txt`, plus each direct named log.

A final fetch still names main `7e484c4da`, .189. The pick remains ALL 244,
with the already-SHIP money/saves review. No HC1 known red is claimed.
The next step is the separately required `HC1 dry run?` question and ended
turn; no HC1 dry run has begun and the provisional marker remains.

## First ALL dry run and mends, 2026-11-22

On the 13:54 slot go, main was still `7e484c4da`. The ALL244 dry run kept
`/home/jerus/cbs/runs/cbs-gate-HC1-x8baF9O4`, copied to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-HC1-x8baF9O4`, of branch
`2d5b1dd6afd74235fa0db9e1d450c4636903b4d6` and main
`7e484c4da555ac6492a6e55d6101e1a5976de65f`. Compilation passed in 8s;
the suite ended after 1268s, exit4, and the launcher exit1: 86 unnamed report
lines across 17 harnesses. The kept files are read only. The provisional
marker stayed. A dry-run result was mailed at 14:21; this stopped run is a
finding, not a reusable green verdict.

The orchestrator's 14:23 clarification assigns a moved played/load premise
whose sweep shipped to HT1-H, widened on main `db9b1fd99`; a still-queued
sweep remains its owner. Only a premise may use that route; broken laws are
HC1 mends. `db9b1fd99` is merged (QUEUE-only change). A fresh delta review,
then a final-model ensemble if the trace moves, and a new dry-run slot ask
are required. No failed run is called ready.

Direct mends so far, with the original assertions/bounds retained:

- EquityCheck's offering-boundary savers are now actually debt-free, without
  a preceding shock month followed by a cash-only reset. The all-owing case
  explicitly has holder share1. All five offering assertions now pass.
- OutsideCheck's living shortfall uses the model's closed-form buffer tail.
  Its government loan still covers the entire ordinary gap; the bank funds
  only the separate unexpected bill, checked by its saving draw and fee.
  The exact grant, no-unfunded-spending and treasury-loan checks remain.
- OwnerCheck observes owner-to-couple reclassification at the census
  boundary, before differently counted households settle another month's
  minima/shocks. The exact50,000 cash-transfer identity passes unchanged.
- SectorStatementCheck's imports identity now includes the household service's
  named foreign input, alongside named non-goods services. Its strict sum is
  retained; 3,000 statements and all expanded lines pass.
- ★ The pure service broker's receipt, imported bill and reserved VAT
  mathematically leave no margin. Floating cancellation could leave a few
  ulps and list a phantom company in one currency unit only. Sector.bank
  normalizes only that arithmetic dust (8 ulps of the posted magnitude),
  only with household services and no physical sales/payroll/utilities,
  maintenance, finance/property costs, foreign income or scrap sale. It
  preserves real positive/negative margins beyond roundoff. CardCheck causes
  zero margins and both real signs through receipts and tax inputs across
  currency scales; all are green. PriceLevelCheck's entire strict scale
  comparison now passes, versus eight failures in the kept run. No money
  audit, save or comparison tolerance changed. This is a model mend; the
  previously recorded d325c74c8 ensemble predates it and is not claimed as
  the new final model's verdict.

CardCheck, PriceLevelCheck, EquityCheck, OutsideCheck, OwnerCheck and
SectorStatementCheck pass on these mends. Additional controlled input mends
and the trace of remaining played/load premises are in progress.

The controlled HoldersCheck waterfall now settles zero income against its
20/40 bills. That causes the original paper-then-shares sale without making
an unrelated positive-income emergency loan in the first settlement. Its
unchanged 24-share-sale identity passes; four later played-paper premises
still fail and are traced separately. PrizeCheck's `creditOwners` now funds
the same debt-free participating share that `subscribe` accepts, weighted
by eligible households, rather than rejecting every household in a cell
with any card debt. The required funding amount and every lab/loan limit
are unchanged; all 286 assertions pass.

The complete GoldStandardCheck passes on the broker mend (38.4s), including
the two lines red in the stopped dry run. Those lines will not be entered
as known reds. Fast compilation and stale checking pass (0 firm findings).

The broker mend also clears CreditCheck's cash-funded banned retailer
(1 judged order, standing/on-site shops 1 to 3), CreditCrunchCheck's entire
120-month in-band premise, and FundCheck's pro-rata withdrawal: both
holdings sell/ask 0.6945 of their units. FundCheck's sole remaining red is
the already recorded `a limit posts at its limit`, owned by HT1-H. The m48
DeathRecordCheck peak comparison also passes again (closed peak5.04,
open4.33); only the already named OX2 m60 arrivals/wave-end class remains.
None of these cleared lines gets a new known-red row.

Historical first-handoff dispositions below: REV-HC1-2 rejected the Health
classification as HC1's own fault. That FS11 row is withdrawn; see the
REV-HC1-2 mends below. The other dispositions were accepted.

Remaining played-city premises, with no assertion, bound or model rule
changed (owners follow the orchestrator's 2026-11-22 14:23 instruction):

- **HoldersCheck, HT1-H (sweep FS14 shipped).** The issue is at m62:
  household face842.56, bank591.09 of1433.94. In m63/64/65 the households sell
  the desk0.0192076244907/0.0180655548265/0.0104836902922 of paper and departures
  take0.0134858057490/0.0117305865189/0.00608432876162. In m66 departures still
  take0.000894270969785. The bank retains face0.0163088514814 at m64 and
  0.00946825577805 at m65; the central bank reaches100% at m66. Thus the
  quiet-desk, clear-bank-before-all-paper, next-household-step and
  middle-with-household-paper premises all fail together. The pre-window
  `lastDecliningShare==0` and mean cash cushion do not pin later card-driven
  departures or the lognormal buffer tail. Bank-first ordering, exact
  holder transfers, the two books and every money audit pass. The sweep
  must cause a quiet desk; the four assertions stand unchanged.
- **CurrencyCheck, HT1-H (FS10 shipped).** At m41 the stores have supplied
  only13 indexed months; `hasRate=false`, `monthsUntilRate=1`. The annual
  inflation function correctly returns0 until its window is ready. Replacing
  one price with `now/1.45` does not create a year, so the fixture's45%, the
  next rate0 and past25% expectations are not caused. The city retains its
  3% rate while no annual rate is available. The pure unrestricted advice
  rule passes. The three original assertions remain.
- **EducationCheck, HT1-H (FS16 shipped).** At m301 there are7717 college
  jobs,1258 own diploma workers and8767 in the qualification queue. The
  opportunity read is0.90, correctly nearer the queue's reading. The two
  hypothetical readings differ by about0.10, below the fixture's required
  0.15; its queue>1.5*own premise and the actual queue-read law pass. The
  equilibrium post count is a played-city premise, as the section's own
  history explains; no posts, queue or threshold were changed.
- **HealthCheck, FS11 (still queued).** In its96-month fee twins ending
  m145, care served99.999778% at x1 versus99.983661% at x15, a0.016117
  percentage-point gap rather than the required five points. The end lift
  is14.8% of people in each twin; both have12 clinics and every month has
  enough beds. The actual high-fee-lower-service, care-book and mortality
  rules pass. Its between-month `shutOut=1` and mean savings lift no longer
  guarantee the whole native month is closed to credit: chargeOff releases
  a twelfth of the excluded share before the next plan, and the within-cell
  buffer is spread. This is the fee fixture's failed native cash-room
  premise, left to its queued affordability sweep; the material-turnaway
  assertion is retained, not relabelled as a passing observation.

★ The synthetic 7c history-size fixture must grow its new yearly payload
along with its monthly payload. `HistorySave.appendMonth` is expressly
harness-only; on each added year it now repeats the last debt survey's
immutable groups at that year, as the fixture already repeats each
monthly series' last value. SaveSlotCheck asserts the exact added survey
count. No native recording, saved schema, old-save refusal, clock limit
or original file-size ratio is changed. This mends the 7x-versus-33x
fixture rather than approving that failed size assertion. Checks are in
progress on this last fixture mend.

The padded-history count and original file-size bound now pass:62.3MB
versus1.89MB, both file and axis33x. The direct 2GiB run's only remaining
SaveSlotCheck red is timing (due-month median86.2ms, ordinary-month24.3ms).
The stopped gate measured28.0ms due versus8.7ms ordinary and, through the
repository's normal `-Dcbs.timing=soft` mode, reported this existing25ms
budget as SOFT. It was not a NEW gate red. The normal named batch run is
still owed here; no known-red approval or budget change substitutes for it.
All snapshot, whole-file, atomicity, history-load and failed-write checks
pass. SaveFileCheck passes again on the final monetary model, including
the native60/61 refusals and exact next-month continuation.

Final named validation of the delta:

- `./batch-check.sh SaveSlotCheck`: exit0, all save assertions pass; one
  existing timing assertion SOFT (83.3ms median versus25ms budget). The
  survey-count and33x byte-growth assertions both pass. No timing budget
  changed and no timing row was added to known-reds.
- `./batch-check.sh HistoryCheck`: exit0, the history/fold/snapshot checks
  pass after the harness-only annual-padding addition.
- Exact-class named runs, one JVM at a time, pass CardCheck,
  PriceLevelCheck, EquityCheck, OutsideCheck, OwnerCheck,
  SectorStatementCheck, PrizeCheck, GoldStandardCheck, CreditCheck,
  CreditCrunchCheck, DenominationCheck, BusinessServicesCheck, BankCheck,
  HouseholdFlowCheck, DebtStatisticsCheck, SaveFileCheck and ReadPathCheck.
  The repeated HoldersCheck/EducationCheck/HealthCheck runs retain only
  the4/1/1 native-premise lines traced above; CurrencyCheck retains its
  three missing-index-year lines. FundCheck and DeathRecordCheck retain
  only their main-recorded HT1-H and OX2 lines respectively. The common
  judge will use narrow rows, never a whole-harness approval.
- The full named KeptCheck, a final fast check and the final pick are in
  progress before the provisional review record is pushed.

KeptCheck's full current-model run finds another FS5 retention line in
section63, the city's fund. After opening the closed-lots fold the month
creates315 nodes and retains1776,84.9% rather than90%; every choice's
comparison to a fresh page passes. Added labels include closed lots2277,
2278,2293,2300,2303,2305,2307,2308,2311,2313,2315,2316. Its window starts
with Portfolio retained at m2130. The failing redraw is m2190, derived
from that printed start plus25 retained months,25 fresh-page comparison
months and the10 choice-months through the closed-fold opening. `arrive`
and `fundHeld` advance no month. The live closed-lot composition changes
the allocations; the fund renderer is unchanged in HC1. FS5, still
pending/queued, owns this retained-page class under the orchestrator's
pin instruction; no financial law, fresh-page equality or90% bound is
relaxed. The old generic Bank Overview known-red fragment happens to
match the old wording here too. To keep this finding distinct, the
diagnostic label now prefixes `page.name()` (no predicate or timing
change), with a fund-specific ship row. That label-only change is compiled
in the last fast check; the complete ongoing UI run exercised the same
predicate and data before that diagnostic prefix was added.

The stopped run's Policy/Taxes Profit retention line clears on the broker
mend: the full current run retains193 of194 nodes, versus195 of224 in the
stopped run. The original90% requirement passes; no known-red row is added
for it. This is further trace evidence of the phantom company's effect.

`./batch-check.sh KeptCheck` completes, exit3: only the Fund retention
finding just traced and the main-recorded empty-paper premise remain.
The latter still reads0 paper; its existing owner is FS5. The live heap
after release is1326.3MiB of2048MiB and the quarter-free check passes on
Windows. Policy Profit's193/194 retention was at m3355. All kept-versus-
fresh comparisons, including household money, pass; KRT1 reports0
nonrepeating place/size differences. The diagnostic page-name prefix
added during that run changes only the emitted label; final compilation
and common-judge validation of the remaining premise rows follow it.

### Delta handoff after the stopped ALL run

The final common-judge calls each name one harness:
`CurrencyCheck`, `HoldersCheck`, `EducationCheck`, `HealthCheck` all exit3,
with only the3/4/1/1 exact premise lines above. `--fast` then exits0:
compilation, generated maps and Stale pass (0 firm,199 soft findings).
The last KeptCheck code change only prefixes its diagnostic label; it is
compiled in these final calls. No assertion predicate, budget, money-audit
tolerance or save/continuation comparison was weakened.

`tools/known-reds.sh HC1 docs/queue/HC1-ship.md` validates the10 narrow
new rows (the9 premise lines and the fund-specific FS5 retention line).
FundCheck's existing limit and DeathRecordCheck's existing OX2 line stay
in main's record. The old stopped run remains evidence, never a green
run to reuse. Its corrected PriceLevel, Equity, Outside, Owner,
SectorStatement, Prize, SaveSlot size, Holders waterfall, Gold, Credit,
CreditCrunch, Fund pro-rata, Death m48 and Policy Profit lines are not
approved as known reds.

The final fetch still finds main at `db9b1fd99`, already merged by
`4c7b3cfc5`. The pick is **ALL244 harnesses**,116 changed files against
that main (generated maps included). Its review floor reads:
`money: Bank.java, DebtStatistics.java, ui/BankScreen.java; saves:
HistorySave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT`. The fresh
delta review must also inspect the Sector.bank monetary normalization
and the pin traces; the HistorySave delta is its harness-only padding,
with no new saved layout after format62. No production threading changes.

The GG1 marker remains provisional. Mail `HC1 mends at` for the required
fresh stranger's delta review and end the turn. On its SHIP, merge main
again, replace the historical d325c74c8 ensemble with one80-run ensemble
on the final monetary model against the instructed newest baseline
(.188 at the last go), and stop on M1/F1 as required. Then ask for a new
ALL dry-run slot before starting it. No second dry run or readiness is
claimed in this handoff.

### REV-HC1-2 mends, 2026-11-22

The review of a77513b71 is NOT SHIP. It accepts the broker normalization,
synthetic history padding, other fixture mends and the Holders/Currency/
Education/Fund pins. Both P1s are owned here; this is the single mend pass.

P1: the HealthCheck FS11 disposition was wrong. HC1 replaced the old
whole-cell lockout with an exclusion share that ages before the next
plan. Resetting that share between months closes settlement's draws but
does not hold the lender closed at planning, so the fixture's own native
cash-room premise was broken by HC1. The FS11 ship row is removed.

★ Supply an explicit harness-only closed lender, independently of the
borrower's exclusion stock. `HouseholdBalance.setCardLenderOpenForHarness`
sets every preconstructed cell, empty/incoming cells included. The closed
input makes both access and credit room zero, for existing holders too;
it changes no care equation, stock, charge-off/expiry rule or measured
counter. The game never supplies this override and defaults to open.
It is fixture configuration like the census/market inputs, not a saved
borrower position; a fixture loading another city must reapply it. No
native saved field/layout or format changes. CardCheck causes a positive
ordinary gap, exclusion ageing, a former holder and reopening, and checks
both sides of the lender input. HealthCheck uses the closed lender for
the entire native window, without rewriting borrower status; it counts
all96 months and prints card advances and the largest planned card room.
The original five-point turnaway requirement and all care/mortality/
money assertions stand. First compile caught a diagnostic local-name
collision; corrected before the named checks.

P2: OutsideCheck's read-back identity did not establish the allocation it
claimed. The expected card draw is now independently
`CARD_SHOCK_CHANCE * Bank.cardAccess(.5,null,0) * shortfall(.5 *
CARD_SHOCK_MONTHS, savingsAfterLiving, SAVINGS_SIGMA)`. The savings left
are the input opening buffer less the closed-form living savings draw.
The savings draw is separately compared with living draw plus the shock's
closed-form savings share; the card stock is compared with this expected
draw times(1+LOAN_FEE), not the model's borrowed/fee counters. Student
borrowing and the treasury's20-household transfer retain their independent
ordinary-gap expectations. Checks on these mends follow.

OutsideCheck passes the independent expectations: card draw0.0358,
savings draw0.4839, card stock including fee0.0362, treasury loan20.8172
(the log prints four decimals). CardCheck's closed/reopened lender edges,
SaveFileCheck, ReadPathCheck and HouseholdCheck pass; fast has0 firm
findings. The first native closed-lender HealthCheck run proves96/96
months of zero advances and zero planned card room in all twins. It still
misses the five-point turnaway condition:99.999671% served at x1 versus
98.390284% at x15 (1.609387 points). That is still owned and not approved.
Further per-cell diagnostics trace the lifted cells' retained cash/assets;
the old lift assumed a whole wallet's draw, while HC1 keeps the spread's
unspent tail. No five-point bound has been moved.
