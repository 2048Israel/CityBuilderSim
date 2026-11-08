# LF1c - LF1's review follow-ups (the short's lending fee)

Queued 2026-11-08 by the orchestrator, from the stranger's review at LF1's gate (money; verdict SHIP +
FOLLOW-UP). None can reach a player's money or save today: each needs a short open, which a real run never
opens. A small fix batch: Sonnet whole (`docs/batch.md`, 3).

1. **The Fund screen's words.** `ui/FundScreen.java:946`: `act()` has no case for `LENDING_FEE`, so the
   fund's record shows the raw "LENDING_FEE" on the row. `:924`: the row `sharesGone` writes reads
   "Written off: <company>, its face gone", a bond's wording, for shares. `:431` and `:674`: a share lot's
   income is labelled "dividends" but now holds the lending fees too. Say what each is; KeptCheck or the
   screen's own check for the words.
2. **A wipe with no line.** `Exchange.cancelShortHeld` wipes the world's `foreignShares` and closes its short
   for nothing with no VALUATION line, where the bank resolution declares its own (`+ bank OwnersWipedAbroad`,
   MoneyAudit 846/1183); so `ForeignAccounts`' `forgiven` misses it (the position figure only; EM2's existing
   close-for-nothing is undeclared too). Declare both, with a fixture that causes each.
3. **Edge 3's fixture in the month's order.** ShortSellingCheck 8 runs retire, then the step, then list;
   the month runs `payDividends`' `listIfUnlisted` (month-order 112) before the step (113), so in play the
   wipe fires only for a company whose book is at or under 0. Make the fixture follow the month's order (a
   company with no book), or say in the section which order it proves and why.

The trace should not move. Harnesses: ShortSellingCheck, FundLedgerCheck, KeptCheck (`--pick`).
