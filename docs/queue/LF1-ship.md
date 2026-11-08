## changelog
### {next} — LF1, the short's lending fee on the lenders' books and four edges of EM2's short book — 2026-11-08, ready, see docs/queue/LF1-notes.md
The world's lending fee for the shares it is short (EM2) reached its lenders with no record: the households' part went from the world
into their savings with no line, so the current account never saw it, and the fund's part touched its cash alone, so its income, its
"Company shares" gain and its ledger left it out. Now the households' is an audit pair like ForeignInterest's (`+ households
LendingFee` INCOME, `- households LendingFeeBanked` DOMESTIC), and the fund's has income counters (the month's and the life's, saved)
and a LENDING_FEE ledger row, in FundView's income and kinds and a line on the Fund page. EM2's four edges are fixed, each caused
by a fixture first (ShortSellingCheck 8): the fee and the lent book stop at what the lenders hold; emigrants are capped at the shares
plus the short; a short at a company with no shares in issue takes the shares it sold with it; a short at a company at minFair is still
called in. No short opens in a real run, so the trace is unmoved (not run through the ensemble); no format bump (new tail slots).
## queued
| LF1b | The cells' share trades with the world as a FINANCIAL/DOMESTIC pair in MoneyAudit, as BondMarket books the cells' bonds: the financial account leaves their shares out today; moves the exchange rate and the trace, so the size first and the ensemble (LF1's notes, 2) | [LF1b](docs/queue/LF1b.md) |
