## changelog
### {next} — CD7, the city's foreign debt revalued — 2026-10-31, ready, see docs/queue/CD7-notes.md
The city's dollar paper now books what the D$ does to it (Jerus, 2026-10-23: "both as you said, but queud right with
the WN batches"): at each month's reprice, every dollar piece's carrying value in US$ times the move from the rate it
was last valued at, WN4's rule for the vault, as an "Exchange gain (loss) on foreign debt" in the budget's result below
the operating lines. It moves no cash: the treasury bridge takes it out again, the EARNED steps name it, and the
rollover's netting and the fund's pay-in and floor leave it out (★). The book shows each dollar line's D$ move since
it was sold, green or red, in D$ and as a % of what it is carried at, with the total and the month's line; the
Government and Fund tabs show the line. Saved: a government slot, the history's debtExchange series and each sale's
rate. DebtExchangeCheck (new) pins the D$ 10% weaker, then back. The suite has only main's known reds; the trace moved,
and the ensemble scores 24 of 26, every figure as 0.8.81's.
## pending
| CD7-E | The EARNED-to-budget residual misses the dollar paper's coupon ({next}): with dollar paper outstanding `Game.getEarnedResidual()` is about the dollar coupon each month (−377 D$k in DebtExchangeCheck's city), because EARNED's interest (`EconomyManager.getExpenses()`) is the domestic coupon; main has it too, and CD7's exchange line, in both the balance and the steps, leaves it as it was | A follow-up, not CD7's brief. Recommended: put the dollar coupon in EARNED's interest beside the domestic one, so the residual is nothing in a month nobody moved a dial | docs/queue/CD7-notes.md §3 |
| CD7-C | Eye-check CD7's screens ({next}): a city with dollar paper after a month the D$ moved - Finances › The book shows a "D$'s move since sold" column (green or red, D$ and %), its total in the foot and two lines on the summary card, and a dollar line's page "Sold with the D$ at ..., now ..."; Government shows "Exchange gain (loss) on foreign debt" under the budget's lines, as a bridge step and in the statement; Fund says what "This year's surplus so far" leaves out. A book with no dollar paper looks as before | Not looked at in the running game | docs/queue/CD7-notes.md §1 |
