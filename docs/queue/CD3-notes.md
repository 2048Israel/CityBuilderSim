# CD3 - the books: notes

*0.8.29, 2026-10-23. The batch is `docs/studies/spec-city-debt.md` §8's CD3
row and the §5 it points to: effective interest a line, blended at a
reopening; carrying value; the budget's interest line, the gain or loss on
retirement, the balance at carrying value and the fair-value note; dollar
paper amortised too (D10, D16, D26). Its screen is the books on the
Government tab and Finances (D31). Built on main at 914850ef (0.8.28).*

## 1. What it does

- **The model (`Debt`, THE CITY'S BOOKS).** Every piece carries a carrying
  value and an effective rate, in the currency it is written in. At the sale
  the carrying value is what the treasury received, and the effective rate is
  the yield at which the piece's remaining payments are worth that
  (`effectiveRateOf()`, bisected on [-50%, 500%] so a deep premium solves).
  Every month, before the payments, `amortise()` books
  carrying x rate / 12 as the expense, the coupon as its cash part and the
  rest as the discount wearing off, then steps the carrying value by the
  expense less the payment. The last month puts its rounding into the expense,
  so the piece closes to nothing. The convention is `presentValue()`'s, so
  the recurrence lands on the face just before the last payment by
  construction. A reopening, the central bank's add-on and its rollover
  reopening add what the sale raised and solve the blended rate again on the
  whole schedule (D16).
- **Dollar paper** (D26, §10's "a dollar piece's discount never accretes")
  keeps its books in dollars. Its D$ figures go through `inLocal()`, at the
  rate the paper is valued at, which is the month's actual D$
  (`DebtManager.setExchangeRate(foreign.getRate())`), not the goods' struck
  rate (AN1). What the D$ does to a dollar carrying value is a revaluation,
  not interest. The founding land bond opens its books at its face.
- **The bank's side is unchanged.** `accrete()` and `Bank.takeDiscount()`
  keep the bank's straight-line discount income (§10: the bank's books are
  another study's), so the trace does not move through the bank (★1).
- **The budget (`NationalAccounts`).** "Interest on the city's paper"
  (`getInterestExpense()`) is the coupons (`getInterestPaidInCash()`) plus
  the discount wearing off (`getInterestNotCash()`). "Gain (loss) on debt
  retired" (`getRetiredGain()`) is carrying value less price
  (`Game.repurchaseGain()`). A buyback between two presses adds its gain to
  the month in progress (`retiredGainSoFar`, saved), which the tick's
  strike books. Both are in the balance (§5.4). `EconomyManager.interest`,
  the coupon owed to the bank, carries cash only.
- **The bridge.** `Game.getTreasuryNotCash()` (the discount amortised less
  the gain) is a row of its own:
  change = surplus + not-cash + raised - repaid + journal + residual.
  `getEarnedToBudget()` has the two new steps.
- **The balance.** `DebtManager.getCarryingValue()` (the books),
  `getFairValue()` (the market's value, a note that is never booked) and
  `Game.getNetPositionAtCarrying()` beside `getNetPosition()`, which the
  model still reads at face.
- **The screens (D31).** The Government tab: the bridge's steps "Interest that
  was no cash" and "Gain (loss) on paper bought back"; the line "Interest on
  the city's paper" split into its coupons and the discount wearing off on
  the debt card; a bill's discount "wears off as interest"; the gain as its
  own line under Spending when it is not zero; the debt grid's "carried"
  column. Finances: THE BALANCE "Owed, as carried" with
  "at face · carried · worth today (a note: what the market would pay; not on
  the books)"; LAST MONTH with the coupon and the discount; each piece's
  "carried · effective r%" on The book, the grid's two columns, and the
  buyback's gain "against what it is carried at". No colour on a gain or a
  loss.
- **The save.** Each piece's books ride with it through Gson. The government
  block has two new slots (33, 34), the treasury array has the not-cash row,
  and `DataSave.retiredGainPending` was added. `SAVE_FORMAT` 44 and
  `OLDEST_LOADABLE_FORMAT` 44, because an older save's pieces carry no
  carrying value and would book their interest wrongly. There is no
  conversion code.

## 2. How the money closes

The audit counts cash, and the new figures are none. The amortisation and the
gain are entries on the government's books, so they move no pool.
MoneyCheck's new section proves it on a city that sells a bill, an amortizing
bond and a ten-year line under par, reopens the line over par and buys a
piece back at a loss. For 36 months every audit closes, and the books'
interest less the amortisation equals the month's cash coupons, holder by
holder: households, firms, the world, the central bank, and the bank's
carried share. Nothing is counted twice and nothing leaks. On the treasury's
side, TreasuryCheck 9 holds the bridge footing with the not-cash row through
30 played months and through a buyback saved and loaded between the presses.

## 3. ★ calls for Jerus

1. **★1 The bank keeps straight-line.** The city and the bank amortise the same
   discount on two schedules: the city's by effective interest, the bank's
   straight-line (`accrete()`). Moving the bank too would change its income,
   its tax and its dividends, so it was left for a study of the bank's books (§10).
2. **★2 The effective interest is in the balance the model reads.** The
   student grants' surplus share (`treasurySurplus`) and `AutoBuilder.costs()`
   (`getTotalExpenses()`) now see the discount wearing off. This is how the
   trace moves.
3. **★3 The gain on retirement is in the balance**, below the operating lines
   (§5.4), so a buyback's gain or loss reaches the same readers for a month.
   CD4's reverse auction replaces the buyback itself.
4. **★4 Two screens still read the coupon:** Finances' Debt service page and
   the hub (`getMonthlyCoupon()`), a cash measure. Left as cash; CD6's desk
   decides.
5. **★5 `GovernmentScreen.spendingYear()`** derives a year's paid-out as
   revenue less the surplus series, so it is off by the gain in a month that
   has one. The fix is a History key of its own.

## 4. The checks, the trace and the ensemble

**TreasuryCheck 9** (new part, 113 assertions) covers:
- 9a: the study's calm 10-year, D$300M of 3½% at 99.148, effective 3.602%, months 1, 24, 60 and 119 at D$893k, 894k, 896k and 900k, closing to nothing;
- 9b: a premium, a bill and an amortizing bond;
- 9c: a reopened line and the add-on;
- 9d: dollar paper, in two twin cities with the D$ moved by half in one, read at the D$ and not at the struck rate;
- 9e: a played city's not-cash row, every month;
- 9f: a buyback through a save;
- 9g: the balance and the fair value, which moves with the curve while the carrying value does not.

**MoneyCheck**: the paper on its books (§2). Twelve mutations of the model each turned a section red. Three statements were restated to the new books with their premises kept: TreasuryCheck's bridge foots with the not-cash row, its first-coupon timing reads the cash coupon, and GdpCheck's "it is the coupon" reads `getInterestPaidInCash()`, with the line asserted as the coupon plus the amortisation.

**The suite**: the baseline on main was 131 of 131. The final run is below (§6).

**The ensemble** (label CD3, 2 runners beside CL5, 80 of 80 in 10.9 minutes),
against `docs/ensemble/0.8.21.md`, scored **24/26** (H1 retired), the same as
0.8.21. Its two reds, A2 (the longest spell over 10%: 10 months [6 to 31],
the same) and S1 (2.46% over the twin against 2.47%, a 57-month spell), are
0.8.21's, so neither is new. AUTO2's final population was 210,443 against
215,378 (T2 passes), T1 16/16, and no run had a finding. It is stored as
`docs/ensemble/0.8.29.md`.

## 5. Who wrote what

Sonnet 5.5 at xhigh wrote each slice: the model and the save, the harness,
and the screens (the screen slice was run twice, because the first run died
on the PC's network outage before editing anything). Claude (Opus 5.5) wrote
the design and the briefs, reviewed each diff, restated the three harness
lines, fixed `getNotePrincipal()`'s comment, and wrote the record.

## 6. The final checks

See the commit that adds this line's figures.
