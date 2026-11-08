## changelog
### {next} — CD9, the foreign debt's follow-ups — 2026-11-08, ready, see docs/queue/CD9-notes.md
A money under zero sells under zero. Each money's curve abroad floors at its own rate, not the city's 0.5%, so the
franc and the euro in their negative years sell at a yield under zero where the chip read -0.6% and the sale was struck
at 0.5%; the coupon floors at 0% (`DebtManager.MIN_FOREIGN_COUPON`) and the paper sells over par, as real zero-coupon
issues did, so the chip, the quote and the strike agree (CD8-F). The fixed issue cost, a D$ figure, is converted into
US$ as it already was for the other seven (CD8-I). One revaluation: the old D$-only figure, which missed the crosses,
is retired for CD7's booked line, which the playtest's "the currency did $x to that debt", the Trade screen, the debt
card's chip and the Summary now read (CD8-R). EARNED's interest carries the month's coupons abroad, saved as
`cityForeignInterest`, so the walk from EARNED to the budget leaves nothing of them: D$0.0000k of a D$552.67k coupon in
DebtExchangeCheck's city, where it left all of it (CD7-E). No format bump. The ensemble: 24/26 against 0.9.50's 23/26;
ZERO_SLACK moved, its median city 358,951 people against 356,096.

## pending
| CD9-G | The Government screen's "...of which paid abroad" row says "in dollars" for coupons paid in any of the eight moneys; the figure is right, in D$ ({next}) | a screen's words outside CD9's brief, found on the way | [CD9 notes §3](docs/queue/CD9-notes.md) |
