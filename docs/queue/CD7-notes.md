# CD7 - the city's foreign debt revalued ({next}, 2026-10-31)

*The batch's record. The brief is `docs/queue/CD7.md` (Jerus, 2026-10-23: "both as you said, but queud right with the
WN batches"). Lane A's second batch on the night of 2026-10-30, built on main after WN4 (0.8.81). Opus wrote the
briefs, reviewed each diff and committed; two Sonnet 5.5 slices at xhigh wrote the code (A the model, the save and
the harness; B the screens).*

## 1. What it is

- **The exchange gain or loss on the city's foreign-currency paper**, month by month
  (`DebtManager.revalueForeign(rate)`): at the reprice, each dollar piece's carrying value in US$ times the rate it
  was last valued at less the rate now, summed - the rule `ForeignAccounts.revalueVault()` values the vault by. Per
  piece at its own last rate, so a piece sold between presses moves from the day it was booked. A gain is positive
  (the D$ rose, the debt costs fewer D$). Domestic paper moves nothing.
- **In the budget** (`NationalAccounts.getDebtExchangeGain()`): "Exchange gain (loss) on foreign debt", in the result
  below the operating lines as the gain on debt retired is (`getBalance()`), not in the totals. Saved as the
  government block's slot 37 (38 long), and the history's `debtExchange` series.
- **When**: the budget is struck at step 69 of the month and the D$ reprices at step 136, so the line is booked after
  the strike, in the month of the move: the strike opens it at nothing, the reprice sets it, and the treasury month
  (step 152), the history (159) and the save read it with its month. A load books nothing (the load path's
  `setExchangeRate()` stays a plain setter).
- **What it does not move**: no cash and no pool (MoneyAudit is struck before the reprice and sees nothing); coupons
  and principal are paid at the month's rate as before. The treasury bridge takes it back out as not cash
  (`Game.getTreasuryNotCash()`), and the EARNED-to-budget steps name it (`getEarnedToBudget()`), so both foot.
- **The cash rules leave it out** (★2): the rollover's netting (`surplusOverLastYear()`), the fund's pay-in
  (`surplusThisYearSoFar()`) and the fund's floor (`monthOfSpending()`) read the surplus less the `debtExchange`
  series.
- **The book shows it** (CD6's green and red): each dollar line's exchange since its sale, D$ and as a share of what
  it is carried at (`BookDesk.Line.exchangeSinceSold()`, `exchangeShare()`: −10% is a D$ that has lost a tenth since
  the sale), the book's total, and the month's line. Each sale keeps the rate it was made at (`Debt.Sale.getRate()`,
  `Debt.soldAtRate()`, the proceeds-weighted mean); a save without it reads 0, unknown, and shows nothing.
- **Screens**: Government - the line under the budget's operating lines (the balance block and the Spending page),
  a step in the bridge from the budget to the cash ("Exchange gain (loss) on foreign debt", with a door to the
  book) and a row in its statement, and the year's paid-out netting the series (`spendingYear()`, as CD3's ★5 did
  for the retired gain). Finances, the book: a column "D$'s move since sold", green or red, D$ and %, shown only
  when the book holds dollar paper (a book of home paper is the twelve columns it was), its total in the foot, the
  summary card's two lines (since the sales, and this month in the budget), and a dollar line's page ("Sold with
  the D$ at ..., now ..."). Fund: under "This year's surplus so far", what it leaves out
  (`Game.debtExchangeThisYearSoFar()`). Not looked at in the running game: the screen-check session's.
- **Harness**: `DebtExchangeCheck` (new, 10 sections, 85 assertions): dollar paper and domestic paper outstanding,
  the D$ pinned 10% weaker before a real press (`ForeignAccounts.pinRate()`), then back: the line equals the carrying
  value times the move to the cent, a loss then a gain, the domestic paper adds nothing, the balance carries it, the
  bridge's residuals are a quiet month's, MoneyAudit passes as in a quiet month, the book reads −(1 − before/after),
  the cash rules leave it out, and across a save and load the line, the series and each sale's rate return with no
  move booked at the load. SaveFileCheck 12b, ReadPathCheck and DeskCheck cover the new keys.
- **No format bump**: the new keys are additive and a missing one reads as no move, which is right.

## 2. The calls (★; each takes the recommended way, BP1)

1. ★ **The line is booked in the month of the move, after the budget's strike.** The alternative was to carry it to
   the next month's strike, as a buyback's gain is: the budget would then show the move a month after the Trade
   tab's "the currency moved it this month by". **Recommended: the month of the move** (taken).
2. ★ **The rollover and the fund leave it out** (the brief's item 2, CD3-C): a currency gain is no cash to repay or
   pay in with and a loss takes none, and the line can be large (a 10% fall on dollar debt of a year's revenue is a
   tenth of a year's revenue) and reverses. The gain on debt retired stays in them as CD3-C decided. Every other
   reader of the balance takes it with the result: the student grant on the surplus-share basis, CityNeeds' pension
   gate, the policy preview. **Recommended: leave it out of the three cash rules** (taken).
3. ★ **The Trade tab's figure stays on what is owed at face** (`ForeignAccounts.takeForeignDebt()`, the net foreign
   position, the risk-off's exposure), while the budget books on what is carried: the two differ by the discount's
   share. Moving the position to carrying would move the D$'s own path. **Recommended: keep** (taken).

## 3. Found on the way

- **The EARNED-to-budget residual misses the dollar coupon** on main too: with dollar paper outstanding
  `getEarnedResidual()` is about the dollar coupon each month (−377 D$k in the harness's city), because EARNED's
  interest is the domestic coupon. Not CD7's (the exchange line is in both the balance and the steps); pending as
  CD7-E.

## 4. The checks, the trace and the ensemble

- **The suite** (`./batch-check.sh`, both data files named): exit 3, main's recorded reds only - PassThroughCheck
  ("...and so does the price index (mean gap 0.68% against 0.51%)") and ScaleCheck's seven fixture lines
  (`docs/notes/gate.txt`), word for word; one timing line SOFT (MapCheck 1), not counted.
- **The trace**: the default playtest's city holds dollar paper during the run ("the currency did $64,534,077k to
  that debt over the run"), so its budget's result moves in those months and the trace was taken as moved.
- **The ensemble**: `docs/ensemble/CD7.md`, 80 of 80 runs on de41645d (both data files named, 2 at once, 16 minutes),
  against `docs/ensemble/0.8.81.md`: **24 of 26**, every criterion's figure the same as 0.8.81's to the digit
  (A2 and S1 red as on main; no money audit failed): nothing the ensemble scores moved, the three cash rules leaving
  the line out.

## 5. For the next batches

- **CD8** (borrowing in eight moneys): `revalueForeign()` is per piece at its own last rate; a piece in another money
  needs its own rate in D$ (`ForeignAccounts.getMoneyUsd(k)` × the D$ rate) passed per piece, the sum the same line.
  `Debt.Sale.getRate()` is D$ per unit of the piece's money.

## 6. One in, one out

Not a process batch: no script, check rule or session rule changed.
