# CD9 - the foreign debt's follow-ups: the world's curves, the issue cost, one revaluation, the coupon in EARNED

Queued 2026-10-31, settling four Pending rows at once, since all four are the
foreign paper's books in `DebtManager`, `ForeignAccounts` and `Game`'s quotes:
CD8-F on Jerus's word, "Real rates abroad (Recommended)"; CD8-I, CD8-R and
CD7-E the recommended way, on his "Take all (Recommended)" (BP1 item 9). The
findings are in `CD8-notes.md` §3 and `CD7-notes.md` §3.

## What it does

1. **Each money's curve floors at its own money's rate (CD8-F).**
   `DebtManager.worldCurveAt()` builds a `YieldCurve` whose short rate is
   floored at `DebtManager.MIN_RATE`, the city's 0.5%, so a yen, franc or euro
   sale prices at max(0.5%, bank rate + premium) + term, while the Borrow
   chip, the quote's rate-before and the buyback's line show the unfloored
   rate (no money error: the sale and the buyback both use the floored curve).
   On Historical the yen is at or under 0.5% 2000-2025, the franc at -0.75%
   2015-22 and the euro negative 2014-22, so the chip can read -0.6% where the
   sale is struck at 0.5%. A world curve floors at its own money's rate, not
   the city's: negative yields allowed, as those moneys had them, and the
   coupon floored at 0%, as real zero-coupon issues were (sold over par for a
   negative yield). The chip, the quote and the strike then agree.
   A harness section (DebtMoneysCheck, which does not see it today): a money
   whose rate is under zero sells at a yield under zero with a 0% coupon and a
   price over par, the chip's rate the struck one; a money over 0.5% prices
   as before to the cent; both floors asserted against their constants.
2. **The issue cost in US$ too (CD8-I).** `Game.costOfIssuance()` is
   `FIXED_ISSUE_COST` at the price level, in D$, and `quoteForeign()` takes it
   off the US$ cash unconverted, on main since the cost was fixed. CD8
   converts it into the money for the other seven; convert it for the US$ as
   well. It moves the trace.
3. **One revaluation figure (CD8-R).** `ForeignAccounts.takeForeignDebt()`'s
   `lastRevaluation` and its lifetime sum are the US$ stock times the D$'s
   move alone, so with paper in another money they miss its cross. Retire them
   for CD7's booked exchange line, one figure for one thing: the playtest's
   "the currency did $x to that debt over the run", the debt card's chip,
   ForeignDebtCheck and CurrencyCheck read CD7's line, their premises
   unmoved.
4. **The foreign coupon in EARNED's interest (CD7-E).** With foreign paper
   outstanding, `Game.getEarnedResidual()` is about its coupon each month
   (-377 D$k in DebtExchangeCheck's city), because EARNED's interest
   (`EconomyManager.getExpenses()`) is the domestic coupon alone. Put every
   money's coupon, in D$, beside it, so the residual is nothing in a month
   nobody moved a dial; a section in DebtExchangeCheck's city asserts it.

## Scope and checks

Items 2 and 3 move the trace: the full suite and the ensemble, its run
committed as `docs/ensemble/CD9.md`. No save change is expected; if a retired
field is saved, its key goes and an old save's is ignored (Gson). Small
pieces: Sonnet slices, one per item or one for all, reviewed by Opus.
