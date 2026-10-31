## changelog
### {next} — CD8, the city borrows in any of the eight moneys — 2026-10-31, ready, see docs/queue/CD8-notes.md
The city sells bills and bonds abroad in any of the vault's eight moneys, not only US$ (Jerus, 2026-10-23): each on its
own money's curve - its central bank's policy rate (XR4), the city's one country premium and the term premium - sold to
the world as dollar paper is. A piece keeps its books in its own money and is read in D$ at the month's rate; its
coupons and face are paid at that rate, CD7's exchange line moves it by the D$'s move and its money's cross, the buyback
prices it on its money's curve, a piece falling due rolls into paper in its money, and proceeds held as reserves go into
the money borrowed (★). Borrow › Abroad picks the money, each one's fair yield beside it and the risk in words; the book
tags each piece with its money. A money with no rate this month is not offered. DebtMoneysCheck (new). No save key and
no format bump (the piece's money rides in its saved record). The default playtest's trace is byte-identical to main's,
so no ensemble; the suite exits 3, main's recorded reds only.
## pending
| CD8-C | Eye-check CD8's screens ({next}): a city with the world file - Finances › Borrow › Abroad shows "IN WHICH MONEY", eight chips each with its fair yield at the term chosen (a money with no rate drawn spent, "no rate"), today's rate and the risk lines under them, the ask, its steps, the minimum and the quote in the chosen money's mark, "AND THE EUROS" for a euro sale; after one, the book tags the piece "€ BOND", its page says "Sold with the D$ at ... per €", the world holds it "€x · all of it", and the debt card abroad reads "THE DEBT ABROAD". A city without the world file shows US$ alone and one line why. | Not looked at in the running game | docs/queue/CD8-notes.md §1 |
| CD8-F | The fixed issue cost is a D$ figure taken off US$ proceeds in every dollar quote (`Game.costOfIssuance()` is `FIXED_ISSUE_COST` at the price level, in D$; `quoteForeign()` subtracts it from the US$ cash), on main since the cost was fixed; CD8 converts it into the money for the other seven and left the US$ as it was so the trace stays. Converting it for the US$ moves the trace. | A small batch of its own with the ensemble. **Recommended: queue it as a small fix batch** | docs/queue/CD8-notes.md §3 |
| CD8-R | The old stock's revaluation reads the D$'s move alone: `ForeignAccounts.takeForeignDebt()`'s `lastRevaluation` and its lifetime sum (the playtest's "the currency did $x to that debt over the run", the debt card's chip, ForeignDebtCheck, CurrencyCheck) are the US$ stock times the D$'s move, so with paper in another money they miss its cross; CD7's line, the one booked, has it. | Whether to retire it for CD7's line or give it the crosses. **Recommended: retire it for CD7's line** (one figure for one thing) | docs/queue/CD8-notes.md §3 |
