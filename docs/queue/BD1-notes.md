# BD1 - the bank's equity parts part from its equity after it buys the city's paper (0.9.6, 2026-11-02)

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
(`Game.restrikeBankDiscountCarried()`). Sites: `issueLongBond()`'s reopen and the desk's three purchases. Not the
treasury's buyback or the call (the gate's finding, section 6): a settled piece's two figures move together there
(`Debt.retirePart()` scales the discount with the face; `canCall()` takes no piece with a sale unsettled), and a piece
bought back whole before its settle must not be re-struck. The central bank's dial is left alone: it trades only
pieces with nothing owed at the settle, where the two figures agree. BK1's settle re-strike is unchanged: the same
month's refresh realizes it with the settle's paper.
Every other move of a line's face was read and moves nothing of the bank's figure: the central bank's trades with the
households, firms and world leave the bank's face alone; its rolls at issue are BK1's own call; principal is paid in
the month after the settle, when no sale is unsettled. Routed to retained earnings and not income, as BK1's (its
javadoc's reasons: it is an artifact of the pooled discount, not an economic gain); BK2, queued, gives each holder its
own discount and makes both re-strikes zero.

No save field: the carried figure is re-derived at every refresh and on load, and `discountRestruck` is already saved
with the month's lines. No format bump.

## 3. The proof: HoldersCheck 13

Section 13, each part in the city and in its twin (`restrikeOffForTest`, which books neither re-strike: main before
BK1, not main):

- **13a, a premium reopening.** Section 12's fixture with its first sale made four points up (`PREMIUM_RISE`, a new
  third argument; section 12 passes nothing and is unchanged): near the policy floor a cut from where the city stands
  barely moves the twenty-year yield, less than a coupon step, and the first try sold at a discount. Taken back down,
  the same line sells 108.71 face for 109.02; its discount left 1.862 -> 1.555, the bank's share 0.7746 -> 0.6466, the
  0.128 re-struck at once, its unearned discount the book's, movement and split nothing, the statement's lines the
  twin's. The month played: the settle re-struck -0.204 (the bank, the residual buyer, paid over face); residual and
  split nothing; the twin's left -0.0762 = 0.128 - 0.204 unexplained, as main did before BK1.
- **13b, the desk on a line with a sale unsettled** (reached by the households' `sellPaperForSpread()`, no hook): the
  sale's discount per face 0.0357 against the settled part's 0.0119; the desk bought 6.74 face, was handed 0.1558 of
  discount while the book rose 0.0803; the 0.0755 re-struck at once; the month's settle re-struck -0.1218; the twin
  left -0.0463 unexplained. Both parts: the city's figures (net, tax, dividends, paper gains, cash, the bank's equity)
  the twin's bit for bit, at once and over the month.
- **13c, a piece bought back whole before its settle** (the gate's finding): section 12's city, a year's bill of a
  fifth of the bank's book sold between the presses (12.35 face, 0.678 of discount; the bank's book 58.3 and its
  unearned discount 0.698 cover both) and bought straight back whole. Nothing re-struck; two months on the bank's
  split is where it began, in the city and in the twin; the figures the twin's. On the tree the gate held, it failed
  as the reviewer said: -0.678 re-struck at the buyback, and still unexplained two months on.

## 4. The checks, the trace and the ensemble

- **SectorStatementCheck with FC1 on**: "its equity's parts are its equity", 479 checked, 0 off by more than a cent, on
  both paths: the gate's (both data files named, where it was off by 0.0287) and a worktree's with none (FC1's own,
  7.06, 226 of 479). All checks passed on both.
- **`--pick`** (both data files named, built on DZ1's tip 61d75496): **exit 0**, 39 harnesses green; one timing line
  SOFT (SaveSlotCheck 1), not counted. Stale: 0 firm. Game.java left to the gate's suite (142 readers).
- **The trace**: the default playtest (2,400 months, both data files named) on BD1 and on DZ1's tip: seven of the eight
  files byte-identical. `t-bank.csv` differs in one row of 2,404, month 133, where a reopening at a premium released
  the bank's 0.029: its equity 68.736 -> 68.765 and the three read off it (leverage, capital ratio, capacity), because
  BD1 books the release where it happens and DZ1 waited for the next refresh. Every row after is the same; the city's
  path did not move.
- **The ensemble** (the trace moved, EN1): 80 of 80 runs on BD1's model (0b04409c, both data files named, so on
  LC3's world file as the baseline), against `docs/ensemble/0.9.2.md` (FC1's): **22/26, as the baseline**, its four
  reds the same. Every scored measure's median and range is the baseline's; only info lines moved (AUTO2's share of
  months at a 0% policy rate 28.19% -> 28.12%; ZERO_SLACK's twin figures, the twin's peak yoy 8.52% -> 9.61%), the
  few cities where the bank's capacity is read between the release and the refresh. Run again on the mended model
  (5880531a, section 6): its table the same, line for line, and the trace byte-identical to the first. That run is
  committed as `docs/ensemble/BD1.md`.
- **After the mend** (section 6): `--pick` exit 0 again, 40 harnesses (HoldersCheck's 13c among them), one timing
  line SOFT (SaveSlotCheck 1).

## 5. For the next batches

- BK2 (queued) gives each holder its own discount: both re-strikes, BK1's at the settle and BD1's at the issue and the
  trades, are then zero, and `restrikeBankDiscountCarried()` and its four calls go with BK1's.
- A reopened line with a sale unsettled, bought back whole: the bank sells back the whole line's discount pro rata and
  the settle pays the sale, which leaves the unsettled sale's own discount unexplained, as on main. BK2's too.
- **Main's, found while proving the mend (★1):** where the bank's book does NOT cover what it sells back, a piece
  bought back whole before its settle opens the bank's split by the sale's proceeds for good. BuybackCheck section 5's
  city (its bank holds none of the city's paper): `Bank.sellPaperBack()` clamps the face and the discount it takes off
  at nothing and books the whole price as a gain, and the settle then pays the proceeds (`bank.lend()`) for paper that
  is gone. The long round trip leaves 4,532 unexplained, the bill's 5,034, the same on main 0.9.4 as on BD1. The money
  audit holds; only the bank's own books part. The mend is the buyback's, not BD1's: a buyback before the settle
  should cancel the sale for the bank (offer only its paid face, `bankPrincipalPaid()`, and take the unsettled face
  off what the settle charges it) rather than sell back face it never paid for.

## 6. The gate's finding (HOLD, 2026-11-03)

The stranger's review: a treasury buyback of a whole piece with a sale unsettled (`quoteBuyback()` allows only the
whole of such a piece). The bank offers `bankPrincipal()`, which includes the face it has not paid for, and
`sellPaperBack()` takes that face and the piece's whole discount D off its book. On main the settle then still pays
the proceeds S and the refresh puts the book back, F - S - D = 0 for a new piece. BD1's re-strike at the buyback
booked -D and restored the carried figure, so the refresh no longer took D back: D unexplained for good. Confirmed by
13c (above) before mending. The reviewer's fix was to skip the re-strike when the piece has a sale unsettled; since a
settled buyback and the call can book only rounding (both figures move together there), BD1 instead takes the
re-strike off the buyback and the call altogether: the same result with less code, and SectorStatementCheck, not a
re-strike, would show any later mismatch there. HoldersCheck 13's twin labels now say "main before BK1": the twin
switches off both re-strikes.
- A premium reopening that sells over par: the bank, the settle's residual buyer, books what it paid over face as
  BK1's re-strike loss at the settle (13a: -0.204), not a premium amortized over the line's life. That too is the
  pooled discount's, and BK2's to mend.
