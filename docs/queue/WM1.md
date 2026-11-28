# WM1 - the window's margin call is judged when it can be made

From ASH4 (its ensemble ASH4e on the merged tip `a5bd4d878`, main 0.9.219 with CBK3; `docs/queue/ASH4-notes.md`,
section 4). One path, AUTO2 seed 3, raised LongPlaytest's "the window lent beyond eligible collateral after haircuts"
at month 148: window $9,888.30, collateral $8,974.84. ZERO_SLACK, SHOCK and HIGH seed 3 replay AUTO2's path until
their policies start, so the ensemble counts it four times. The cause is CBK3's audit and the month's order, reached
on ASH4's path. ASH4's diff (Household, HouseholdBalance, Exchange) touches no bank, window, peg or curve code:
`git diff origin/main` is empty for Bank, CentralBank, DebtManager, Game, ForeignAccounts and LongPlaytest.

## What happens

1. **The window is capped when it is struck, and only then.** `CentralBank.settleWindow` (CentralBank.java:362),
   `advanceToBank` (~327) and the interest refinance in `chargeWindow` (~390) hold the window to
   `Bank.windowCreditLimit()` (Bank.java:3536): business loans x0.70, family loans x0.15, the bank's city paper x0.95,
   times WINDOW_LIMIT (1.00). Month 148's last settle (Game.java ~14982, "CBK3: trading and hot withdrawals follow the
   first settle") struck the window at $9,888.30 under a cap of $19,610.98 (business $7,117.19, family $12.48, the
   city paper $15,396.92, three LongTermBonds of about $13,984 face, two of them 200 and 227 months long).
2. **The money audit is struck next** (~14985), and nothing after it may move a pool.
3. **Then the peg breaks** (`foreign.repriceCurrency()`, ~15060): the vault is empty (US$1,460 at m143, $229 at m147,
   0 at m148), the regime goes PEG to FLOAT, the rate 1.00 to 2.0076. With the tie gone the curve's expected path
   switches from the US-tied 1.5% to the inflation rule's on struck inflation of 18.6% (DebtManager.java ~2387-2389,
   priced in `bankCollateralValue`, ~2435); the policy rate is 24.4% at m149. The bank's city paper is worth $4,200.98,
   down 73%, so the cap is $8,974.84.
4. **The audit reads the window against the repriced cap** (LongPlaytest.java ~1553-1570, `auditBankFunding`) and
   flags it. Nothing was lent past the cap: the collateral fell under a loan already made.
5. **The margin call is made, a month on.** m149's first settle cuts the window to the new cap, $8,324.45, and the
   bank repays $1,563.85 out of its reserves (deferred 0). Had reserves been short, the unpaid part would have become
   `windowDeferred`, which the audit leaves out.

So the invariant holds at each settle, not by construction: any month whose reprice cuts the collateral below a
window struck before it meets it. Main's own seed 3 breaks its peg by the vault too, at m153, but its window of $4,288
was covered by its business loans alone ($6,820 x0.70). ASH4's path breaks it five months sooner (the vault $1,460 at
m143 against main's $1,938) with a window about twice main's (deposits $11.7k against $14.0k at m148).

Real central banks mark collateral to market daily and call margin: the borrower posts more or repays. The game
makes the call at the next month's settle. A same-month call after the reprice cannot be made without moving a pool
past the struck audit, and no haircut of 5% covers a 73% fall.

## The mend

The audit judges what the window can be held to:
- the window against the cap of the month's last settle (struck before the reprice), within a cent; and
- the next month's first settle meets any shortfall from a reprice: the window then at or under the new cap, the
  unpaid part (`windowDeferred`) owed and counted.

Keep the haircuts and the settles as CBK3 wrote them. If the builder finds a same-month call can be made before the
audit is struck (the reprice moved ahead of the last settle without moving any other figure), that is the better
mend, but it moves the month's order and every trace, and needs the ensemble.

## The check

BankRulesCheck already reads `LongPlaytest.findings` for this label (~685). Cause it on a fixture: a peg with struck
inflation about 0.2 (`strikeExpectations`) and the vault drained so this month's reprice breaks the peg; the bank
holding long city bonds, its window above business x0.70 plus the bonds' post-break value x0.95. One
`simulateMonths(1)`. Assert:
- the peg broke that month and the cap fell below the window (the fixture);
- LongPlaytest's audit does not flag the window that month (red on main);
- at the next month's settle `getAdvancesToBank() - getWindowDeferred() <= windowCreditLimit()` plus a cent, and what
  was repaid or deferred is the shortfall, to the cent.

The run itself is a probe, not a check: `ham.citybuildersim.Ensemble AUTO2 3 out.csv` with `-Densemble.months=150`,
wrapped so that every read of `Bank.windowCreditLimit()` and LongPlaytest's `auditBankFunding` prints the window, the
deferred part and the cap's three parts. On `a5bd4d878` it reads window $9,888.30 against $8,974.84 at m148, in about
20 seconds.
