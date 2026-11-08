## changelog
### {next} — CD9, the foreign debt's follow-ups — 2026-11-08, ready, see docs/queue/CD9-notes.md
A money under zero sells under zero. Each money's curve abroad floors at its own rate, not the city's 0.5%, so the
franc and the euro in their negative years sell at a yield under zero where the chip read -0.6% and the sale was struck
at 0.5%; the coupon floors at 0% (`DebtManager.MIN_FOREIGN_COUPON`) and the paper sells over par, as real zero-coupon
issues did (CD8-F). The fixed issue cost is converted into US$ as for the other seven (CD8-I). One revaluation: the old
D$-only figure, which missed the crosses, is retired for CD7's booked line, which the playtest, the Trade screen, the
debt card's chip and the Summary now read (CD8-R). EARNED's interest carries the month's coupons abroad, saved as
`cityForeignInterest`, so the walk from EARNED to the budget leaves nothing of them (CD7-E). No format bump. Found at
the gate: the fund's closed lots fold drew all 5,562 lots of an old fund, over a gigabyte, and lists the newest 200 now
(`FundScreen.CLOSED_FOLD_ROWS`); KeptCheck sells its 20-year bond first, for the sections that draw it. The ensemble:
23/26, as 0.9.54; ZERO_SLACK's median city 332,066 people against 349,699.

## pending
| CD9-G | The Government screen's "...of which paid abroad" row says "in dollars" for coupons paid in any of the eight moneys; the figure is right, in D$ ({next}) | a screen's words outside CD9's brief, found on the way | [CD9 notes §3](docs/queue/CD9-notes.md) |
| CD9-K | KeptCheck's sales assume a bond market that takes six sales of three times the smallest deal; in its city on CD9 merged with 0.9.54 the market takes one (months 2,335 and 2,941), so section 69's Overview and section 71's Spending debt fold go red; the month's GDP read D$-51.7T there, not looked into ({next}) | the fixture cannot cause its premise in a thin market; found at CD9's gate | [CD9 notes §3](docs/queue/CD9-notes.md) |
