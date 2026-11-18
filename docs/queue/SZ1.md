# SZ1 - steps and lots that scale with the city and the price level

Jerus, 2026-11-18: "you know how alot of buttons are say +10 or +100, say in
building, well as the city grows, that becomes a hassle cause you are building
+10,000 if you get what i mean, also at the start of the game, the bond blocks
of 100m are literally impossible they are too much, shouldnt block size be
rounded but inflation adjusted? so that even with super high inflation they are
still a thing. additionally, in the citieis fund to withdraw or put its also
hard to withdraw when the numbers are really big, and other buttons im sure";
"cause like 100m is the bond min size, but in 1900 thats a too big amount".
Placed second in the queue at his word ("near next if possible").

## What is wrong (read 2026-11-18 on main 36157fb19)

1. **The bond lots are fixed in nominal money.** `FinancesScreen.INSTRUMENTS`
   (lines 214-239) gives each kind a `rounding` in thousands: bills 1,000
   ($1M), amortizing 10,000 ($10M), bonds, floaters and linkers 100,000
   ($100M). The ask is rounded to it (`borrowAsk`, line 5345) and passed to
   the quote and the sale (`kit.rounding()`, lines 5229-5231;
   `executeDebtLogic`, line 7099). The minimum issue already scales: 5% of a
   year's tax, floored at `Game.MINIMUM_ISSUE_FLOOR` = 50 x
   `PriceLevel.FACTOR` (EP1, `Game.minimumIssueSize()`, line 14822). The lot
   does not, so a city founded in 1900 cannot ask for a bond it could carry,
   and under high inflation the lot shrinks to dust. `askSteps(ask, lot)`
   (line 5589) already steps by the ask's leading digit, never under a lot,
   so the ask's ± scales; the lot is what doesn't.
2. **The build card's steppers are fixed counts.** `BuildScreen`'s "+10" and
   "+100" (lines 1170 and 2363-2367; ten stepper uses in the file), and the
   ones in `LandScreen` (2) and `Pieces` (2): a city ordering 23,000 of a
   thing presses +100 two hundred times.
3. **The fund's money steps are fixed.** `FundScreen.MONEY_STEPS` =
   {1,000, 10,000, 100,000, 1,000,000} (line 119; used at 2037, 2149, 2625):
   putting in or taking out a fund in the billions takes hundreds of presses.
4. **Others like them**, which Jerus has not looked for: sweep `ui/` for
   fixed steps on a magnitude (money, a count, an area). A step on a share or
   a rate (`Ladder`'s percentages, `TreasuryFund.WITHDRAWAL_STEP`) is not a
   magnitude and stays.

## What is wanted

- **One rule for every magnitude step**, pure and in one place (a small
  class or `Pieces`): the steps follow the figure in hand, one unit of its
  leading digit and a tenth of that, as `askSteps` does, never under the
  thing's own least step. At an empty field, the least step. Every stepper in
  2-4 on it; `askSteps` folds into it.
- **The lot priced at the month, rounded** (Jerus: "rounded but inflation
  adjusted"): each kind's lot is today's figure above at the month's price
  level, rounded to 1, 2 or 5 x 10^n, so it stays a round number in 1900, at
  today's prices and after a hyperinflation. The lots become dials, one
  sentence each.
- ★1 (recommended): **the lot never above the minimum issue**, rounded down
  to 1-2-5, so the smallest issue worth arranging can always be asked; the
  alternative is the price level alone, which still leaves a tiny 1900 city's
  bond lot above what it can carry.
- The (i)s that state a lot or a step (`LOTS_INFO`, `PROCEEDS_INFO`, the
  build card's and the fund's) say the new rule; `KeptBench` line 2040 presses
  steppers by label ("+10", "+100") and follows the new labels.
- **A harness** (say `StepsCheck`): the rule at 0, at 7, at 23,000 and at
  3.4e9 and its least step; the bond lot at 1900's level, today's and a
  hyperinflated month, each round and positive; ★1's cap; a build card's and
  a fund's steppers at a large figure step by the large figure.

## Where it touches, and the gate

- Files: `ui/FinancesScreen`, `ui/BuildScreen`, `ui/FundScreen`,
  `ui/LandScreen`, `ui/Pieces`, `ui/KeptBench`, the new harness and its
  `AllChecks` line; the lot's dials in the model's class if they move there
  (`Game` beside `minimumIssueSize()`, or the bond market's).
- **Moves the playtest?** The steppers do not: they are the player's
  buttons. The lot does only if the model's own sales (the rollover, the
  auto-builder's funding, CD2's) pass a lot; find out first. If they do, the
  playtest moves and the ensemble applies; if only the screens pass it, it
  does not.
- **The gate** (`docs/contract.md` 6): game classes, so the whole suite. No
  `SAVE_FORMAT` (nothing saved changes; check no pending order keeps a lot).
  A stranger's review only if the lot reaches the model's bond sales;
  otherwise the review line decides.
