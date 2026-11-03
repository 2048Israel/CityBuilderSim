# BD1 - the bank's equity parts part from its equity after it buys the city's paper ({next}, 2026-11-02)

Found by FC1: SectorStatementCheck on FC1's path, month 134 the bank buys D$1,113M of the city's paper, month 135 its
unearned discount falls 7.06 with no named cause, and "its equity's parts are its equity" is off by 7.05 in 226 of 479
bank-months (BK1's class; the money audit held). FC1 played the harness with its builders' price off meanwhile; BD1
turns it back on (`BuildingManager.buildersShortOn = false` deleted from SectorStatementCheck).

## 1. The release, named

Reproduced on FC1's tip with FC1 on. The city's path depends on `data/world-1.bin`, which a worktree lacks, so a
worktree's run is not the gate's. With the main checkout's files named, the gate's path shows the same fault in months
134-135 at a smaller scale: 0.0287, against 7.06 on FC1's own path. The debt book's figure for the bank's unearned discount
(`DebtManager.bankUnearnedDiscount()`, re-read at every `Game.refreshBank()`) moves twice with no cause booked:

- **A line reopened at a premium.** `Game.issueLongBond()` -> `Debt.reopen()`: the premium eats the line's
  `discountLeft`, the bank's settled share with it (66.18 face for 67.81: 0.6915 -> 0, the bank's 0.028709 gone),
  after the month's last refresh. The next month's refresh released it with no cause. BK1 measures only across the settle.
- **The desk buying paper off a line with a sale still unsettled.** The desk hands the bank `Debt.unaccretedOn()` (the
  whole line pro rata), while the book counts the settled part over the settled face. The book's figure rose 0.0248 more
  than the bank carried. BK1's settle then re-blended it back and booked +0.0248 that the equity never saw.

## 2. The mend

A re-strike carried at once (`Bank.restrikeCarriedDiscount()`): it books `discountRestruck` and moves the carried
discount by the same, so the equity moves where the release happens, as a treasury buyback does between the presses.
It is measured as what the bank's trade moved its carried discount by, less what the book's figure moved by
(`Game.restrikeBankDiscountCarried()`). Sites: `issueLongBond()`'s reopen, the desk's three purchases, the treasury's
buyback, the call. The central bank's dial is left alone: it trades only pieces with nothing owed at the settle, where
the two figures agree. BK1's settle re-strike is unchanged: the same month's refresh realizes it with the settle's paper.
Every other move of a line's face was read and moves nothing of the bank's figure: the central bank's trades with the
households, firms and world leave the bank's face alone; its rolls at issue are BK1's own call; principal is paid in
the month after the settle, when no sale is unsettled. Routed to retained earnings and not income, as BK1's (its
javadoc's reasons: it is an artifact of the pooled discount, not an economic gain); BK2, queued, gives each holder its
own discount and makes both re-strikes zero.

No save field: the carried figure is re-derived at every refresh and on load, and `discountRestruck` is already saved
with the month's lines. No format bump.

## 3. The proof: HoldersCheck 13

Section 13, each part in the city and in its twin (`restrikeOffForTest`, which books neither re-strike, as main did):

- **13a, a premium reopening.** Section 12's fixture with its first sale made four points up (`PREMIUM_RISE`, a new
  third argument; section 12 passes nothing and is unchanged): near the policy floor a cut from where the city stands
  barely moves the twenty-year yield, less than a coupon step, and the first try sold at a discount. Taken back down,
  the same line sells 108.71 face for 109.02; its discount left 1.862 -> 1.555, the bank's share 0.7746 -> 0.6466, the
  0.128 re-struck at once, its unearned discount the book's, movement and split nothing, the statement's lines the
  twin's. The month played: the settle re-struck -0.204 (the bank, the residual buyer, paid over face); residual and
  split nothing; the twin's left -0.0762 = 0.128 - 0.204 unexplained, as main did.
- **13b, the desk on a line with a sale unsettled** (reached by the households' `sellPaperForSpread()`, no hook): the
  sale's discount per face 0.0357 against the settled part's 0.0119; the desk bought 6.74 face, was handed 0.1558 of
  discount while the book rose 0.0803; the 0.0755 re-struck at once; the month's settle re-struck -0.1218; the twin
  left -0.0463 unexplained. Both parts: the city's figures (net, tax, dividends, paper gains, cash, the bank's equity)
  the twin's bit for bit, at once and over the month.
