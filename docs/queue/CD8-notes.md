# CD8 notes ({next}, 2026-10-31): the city borrows in any of the eight moneys

The brief is `docs/queue/CD8.md` (Jerus, 2026-10-23, in CD7.md's words). Built on main at f39416d5 (WN4, CD7 and XR4
merged; main's later commits merged in before the suite). Two slices on Sonnet 5.5 at xhigh, each reviewed here as a
stranger's: the model and its harness (d1f86ed8), then the screen (aac50f74, with the totals' words and
`DebtManager.getForeignPrincipalLocalIn()` added in review).

## 1. What it built

- **A piece's money.** `Debt.money` is the code of the money a foreign piece is written in, one of `WorldMoneys.EIGHT`;
  null for a US$ piece and for paper at home, so a dollar piece saves exactly as before and an older save's reads as
  US$ (`getMoney()`). Debts are saved whole by Gson: no new DataSave key, no HistorySave series, **no format bump**.
- **Each money's curve** (`DebtManager`, THE OTHER SEVEN MONEYS): `foreignCurveRate(code, months)` = that money's
  world rate (XR4's policy rate, `Game.getPolicyRates().rate(k)`; WORLD_BASE_RATE quieted or without rates) + the
  city's one country premium + the term premium, the way `foreignCurveRate(months)` builds the US$'s on `worldRate()`.
  Game hands the eight's rates (`setMoneyRates`, in `handTheUsRate()`) and their US$ per unit (`setMoneyUsd`, beside the
  vault's in `handTheMoneysToTheVault()`, and at the load from the vault's restored rates); neither is saved.
  `offers(code)`: the US$ always, another money in a month the world handed it a rate.
- **Its books, its money, its line.** A piece is carried in its own money and read in D$ at the D$'s rate times its
  money's US$ per unit (`rateOf(code)`, `Game.localPer(code)`); proceeds, coupons and principal are converted at the
  month's rate through the same treasury lines (FOREIGN_INTEREST, FOREIGN_PRINCIPAL), so MoneyAudit closes as before.
  CD7's exchange line sums each piece at its own rate (`revalueForeign`: the D$'s move and the crosses' together, the
  pieces learning the month's crosses at the close). `getForeignPrincipalUsd()`, the coupon and the dues abroad are US$
  at the month's crosses; the country premium and the window read the whole foreign book in D$, every money together.
- **Sale, buyback, rollover.** `Game.quoteForeign(type, money, ...)` / `handleForeignLogic(type, money, ...)` walk the
  same fixed point on the money's curve; the buyback prices the piece on its money's curve (`marketValue`,
  `valueOfAPoint`, the honest line); a piece falling due rolls into paper in its money (`Rollover.Issue.money`, "a 3-month
  bill in euros"), at home if its money has no rate that month, as dollar paper does when the window shuts. The old
  US$-only signatures delegate with "USD".
- **The screen** (Finances): Borrow › Abroad has "IN WHICH MONEY", one chip a money with its fair yield at the term
  chosen (a money with no rate drawn spent), today's rate and the risk in words under them ("If the D$ falls against
  the euro, every coupon and the face cost more D$"), and what the city already owes in it; the ask, its steps and
  presets, the minimum (`Game.minimumIssueSizeIn()`), the quote, "AND THE EUROS" and the result page in the chosen
  money. A new ask is carried across when the money changes, at the moneys' US$ rates. The book tags each piece with its
  money's mark ("€ BOND"), names it "in euros", and its page, holders and sales read its money; the totals abroad (the
  ladder's edge, the desk's column and dots, the debt card, "THE DEBT ABROAD" once another money is owed) say every
  money. Every US$ word stays as it was where only US$ is owed. Not looked at in the running game: CD8-C.
- **The harness**: `DebtMoneysCheck` (ten sections, 158 assertions): US$ only without the world file; each of the eight's
  curve against its bank's rate; a euro bond and a yen bill sold, paid a month, moved by the D$ pinned 10% weaker and by
  the crosses alone, a euro sale held as reserves, the euro bond bought back at fair, a euro bill rolled into a euro
  bill, over a save and a load; US$ paper as it was, to the bit.

## 2. The calls (★; each takes the recommended way, BP1)

1. ★ **Held as reserves goes into the money borrowed**, the US$ included (a held US$ bond bought by the reserve mix
   before): a matched asset, the debt and what it bought moving together, which is what holding the proceeds is for.
   The default playtest never holds, so the trace is unmoved. **Recommended: keep.**
2. ★ **One country premium for the city, whatever it owes in**: the city's credit is the same in any money, and the
   premium reads its whole foreign book in D$, so a city deep in euro paper pays more on a new US$ bond too.
   **Recommended: keep.**
3. ★ **A money with no rate this month is not offered**, its paper valued at its last good rate and rolled at home.
   **Recommended: keep.**
4. ★ **The curve book stays the US$'s** (`recordCurve`, `worldCurveMonthsAgo`, the desk's "then" curve): the other
   seven are struck when read, not kept month by month. **Recommended: keep**; WN6's screens can keep more if they draw it.

## 3. Found on the way

- **The fixed issue cost is a D$ figure taken off US$ proceeds** (`Game.costOfIssuance()` is `FIXED_ISSUE_COST` at the
  price level, in D$), in every US$ quote on main. CD8 converts it into the money for the other seven and leaves the
  US$ as it was, so the trace stays. Pending as CD8-F: converting it moves the trace, so its own small batch with the
  ensemble.
- **The old stock's revaluation reads the D$'s move alone**: `ForeignAccounts.takeForeignDebt()`'s `lastRevaluation`
  (the playtest's "the currency did $x to that debt over the run", ForeignDebtCheck, CurrencyCheck) is the US$ stock
  times the D$'s move; with paper in another money it misses the crosses. CD7's line, the one booked, has them.
  Pending as CD8-R.
- A one-month bill rolled into a one-month bill is sold and falls due in the same press, so it never shows on the
  books (main's, not CD8's); the harness rolls a three-month euro bill.

## 4. The checks, the trace and the ensemble

- **The trace**: the default playtest (4,002 months, both data files named) traced on CD8's model and on main at
  f39416d5: all eight trace files byte-identical, the log identical but for its two run-time lines (89 s against
  100 s). Both exit 1 on the same two map findings, word for word (main's, XR4-M). **No ensemble.**
- **The suite** (`./batch-check.sh`, both data files named, main 03206ff4 merged in): **exit 3**, main's recorded reds
  only (`docs/notes/gate.txt`), word for word - PassThroughCheck ("...and so does the price index (mean gap 0.68%
  against 0.51%)"), ScaleCheck's seven fixture lines, LongPlaytest's two map findings (XR4-M); one timing line SOFT
  (SaveSlotCheck 1), not counted. Stale: 0 firm.

## 5. For the next batches

- **WN6** (the screens): each money's curve is `DebtManager.foreignCurveRate(code, months)`; what the city owes in a
  money `getForeignPrincipalIn(code)`; a piece's money `Debt.getMoney()`, its mark `ForeignAccounts.MONEY_MARK[k]`.
- **WN4b** (the D$ pegged to another of the eight): paper in the anchor's money carries no exchange risk under the peg
  but its cross; `rateOf(code)` reads it.

## 6. One in, one out

Not a process batch: no script, check rule or session rule changed.
