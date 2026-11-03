## changelog
### {next} — BD1, the bank's discount re-struck where an issue or a trade releases it — 2026-11-03, ready, see docs/queue/BD1-notes.md
The bank's equity is its parts again after it buys the city's paper. Its unearned discount on that paper is re-read
from the debt book at every refresh, and twice the book's figure moved with nothing to book it: a line reopened at a
premium let the proceeds over face eat the line's discount, the bank's settled share with it; and the desk, the
treasury's buyback and the call handed the bank the whole line's discount while the book counts the settled part's,
which differ whenever a sale is unsettled. The next refresh moved its equity with no cause (SectorStatementCheck on
FC1's path: 7.06 in month 135, the parts off in 226 of 479 bank-months). The bank now re-strikes it where it happens
(Bank.restrikeCarriedDiscount()), a named cause routed to retained earnings as BK1's is, its carried discount on the
book's at once. HoldersCheck 13 proves both in a city and its twin; SectorStatementCheck plays FC1's builders' price
again, 0 of 479 off. The trace moved in one row of the bank's; the ensemble passes 22/26, as 0.9.2.
