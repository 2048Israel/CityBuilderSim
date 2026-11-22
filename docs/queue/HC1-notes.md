# HC1 — Credit cards and household debt statistics

Started 2026-11-22 from `origin/main` 16ee7adde (0.9.186); HC2 is folded into this batch.

## Built

Work in progress. The card balance remains per household; `revolving` is its holder share,
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

## Checks and remaining work

Initial `./batch-check.sh --fast` passed on the base. `CardCheck` passed its closed
forms, mechanisms and 24 real-city months; both pooled and household money residuals
were below tolerance by more than eight orders of magnitude. Its scratch-slot-10
save retained every card field, all current statistics and the annual history.
`DebtStatisticsCheck` passed the grid/bisection/draw, weighted ratios and group checks.
Existing harness checks are in progress. Save/read-path integration, old discharge premises, the full pick,
the stranger's money/saves review, ensemble and ALL dry run remain.

HS3 owns `ui/PeopleScreen` concurrently. Any card/debt section waits for HS3 to ship
and a merge of main first; otherwise these notes will name that deferred page work.
