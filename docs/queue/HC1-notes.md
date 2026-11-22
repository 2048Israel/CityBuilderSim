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
