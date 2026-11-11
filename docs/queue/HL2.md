# HL2 - the households' own faults mended

HL1 (2026-11-11) closed the households' books: every dollar a household gains or loses is now a line,
and `LongPlaytest.audit()` asserts the identity every month. To close it HL1 had to NAME nine lines
that are not flows at all but faults on the household side, money made or lost with nobody on the
other end (`MoneyAudit.households()`, the "own" lines; `HouseholdBalance.getUnfunded()` and the rest).
It mended none of them, and it found one more rule that is wrong. This batch mends them, and the own
lines go to nought. Notes: `docs/queue/HL1-notes.md`.

## What is there (the final HL1 model, months 240-900, seeds 0 and 1; GDP 8.86M and 4.16M)

| the fault | seed 0 | seed 1 | where |
|---|---|---|---|
| **Unfunded bills**: the sellers book a sale in full, the household that could not fund it "went without" (`Household.settle`, `unfunded`) and paid nothing: money in a till that no savings held | 2,232 (227 months) | 29,770, 0.72% of GDP (613 months) | `Household.java` ~985 |
| **Cells too small to settle** (under `EMPTY_CELL`, half a household): the row's income, fees and shopping are split over every cell, then the small ones are skipped: their income is paid by the pools and reaches nobody, their rent, fees and shopping are booked and charged to nobody | income 4,921; rent, fees, shopping 425 | 6,253; 454 | `HouseholdBalance.advanceMonth` ~1140 |
| **Public wages struck twice**: the treasury and the bank pay the payrolls stored in `advanceDemographics`, the households are handed the live payroll at the next sync, the fill having moved between | 393 (377 months) | 216 (435 months) | `Game.syncHouseholdAccounts`, `PopulationManager.getTotalWage()` |
| **The census's orphans**: a shrinking cell with no grown-ups leaves its savings in a pool nobody claims, a growing one multiplies its own | under 1 | under 1 | `HouseholdBalance.moveStock` (HS1 rewrites it: leave these two lines to HS1, and say so there) |
| **A bond's face no cell holds**: the same clamps move every bond stock, and nothing tells the bond's record (`CorporateBond.households`), so the cells' own face drifts from it; the issuer then pays principal and coupons on the record and the cells are credited their own (`- households BondFaceOrphaned`). No check compares the two for a company's bond (`LongPlaytest` does for the city's paper) | 0 | 0 (the ensemble: every bond 1e-6 short on seed 10, one bond 0.0006 short of 3.52 on seed 14) | `BondMarket.redeemMaturing`, `payCoupons`; `BondMarket.recountHouseholds()` is the load's mend of the same drift |
| **Discharged over a debt they could repay**: with no income any debt is "at the ceiling" (`Household.discharge()`, 2026-09-11), whatever the household has saved. Of the households discharged, 97% and 88% sat in cells whose savings covered the debt (mean savings of a discharging cell 2,845 against a debt of 1.11; 10,605 against 64.58). Each month 4% of such a cell is marked bankrupt, locked out for a year, and a quarter of them leave the city: 994 and 3,862 households gone in 661 months over debts their savings covered. (Until HL1 4% of the cell's savings were destroyed each month too: that was the leak, 30,218 and 117,105; HL1 made the savings repay the debt, 4% a month.) | 3,864 of 3,978 household-months | 13,619 of 15,448 | `Household.java` ~1342 |

Outside the month's window, so in no own line: `restrikeMoneyConstants()` (nextMonth #6, before the
audit's window opens) re-strikes the pension and the wage tax after the treasury paid the old figure
and before the households are handed the new one (pensions 1,139 over 527 months, seed 0, on HL1-A's
path).

## What to do

1. **A household whose savings cover its debt repays it and is not discharged.** In `discharge()`:
   with `savings >= debt` the whole debt is repaid out of savings (`repaid`, the channel HL1 used) and
   nobody is bankrupt, locked out or leaving. Only a household that cannot repay is discharged, its
   savings going to the bank first (HL1's rule, kept). ★ below.
2. **An unfunded bill is not a sale.** ★ below.
3. **Split over the cells that settle.** `splitIncome`, `splitByPeople` and the rent's shares weigh
   only the cells `advanceMonth` will settle (not `isEmpty()`), so a row's whole take-home, fees,
   shopping and rent land on households that are struck. A row with no settling cell hands its money
   back by a line, not to nobody (find whether one exists in a real run first: count it).
4. **One figure for the public payrolls.** The households are handed the payrolls the payers paid
   (the stored figures the audit's lines read), not a second strike of them.
5. **The window opens before the restrike**, or the restrike moves inside it: the pools' and the
   households' opening figures are read before `restrikeMoneyConstants()`, and a chained assertion in
   `LongPlaytest.audit()` (held at this open == held at the last strike, but for what the player did
   between presses) keeps it so.
6. **The lines no measured city exercised** each get a caused month in `MoneyCheck`, the households'
   identity asserted in it: schools' fees and payroll, research, the bank's operating cost,
   student grants and loans, food assistance, fuel duty, a buyback, the central bank buying of
   households, the fund's shares. (HL1 found two such lines a press out of step the moment a city
   exercised them, the yards' old metal in the ensemble and the contributions and premiums off the
   payslips in `BondCheck`'s town, and carries both now, `MoneyCheck` 16 and its old-metal fixtures:
   any of these may be the same, and only a caused month says.)
7. **A bond's record follows its cells.** After the census the bond's households' face is the
   cells' own (`recountHouseholds()`'s rule, as after a load), what was stranded named as a line
   (whose it becomes: the issuer's debt forgiven, or the world's face, as a leaver's is), and
   `LongPlaytest.audit()` compares the two records every month as it does the city's paper.
8. Then the own lines are asserted at nought (`Households.own` within the tolerance) and the
   accumulators that are now always zero are deleted, with their labels. The ensemble (the trace
   moves: fewer bankruptcies, fewer leavers).

## Calls

- ★1 **Who bears an unfunded bill.** Recommended: nobody is sold to. The code's own comment says the
  household "went without"; so the unfunded part of the month's shopping is taken off the sellers'
  sales (Retail's first, as food is what the hunger measure reads), at the strike that books them or
  as a return in the next. The other ways: the bank lends it past the ceiling (the ceiling then means
  nothing), or the city pays it as assistance (a new transfer nobody chose).
- ★2 **Solvent households are not bankrupt.** Recommended, as item 1. It lowers bankruptcies by about
  nine in ten and the leavers with them (WG2's verdict on the out of work reads the people who leave),
  so the People page's figures move; the other way keeps the rule and only stops at a hair of debt.
- Recommended without a call: 3, 4, 5, 6, 7 (the stranded face to the world's, as a leaver's goes).

## Done when

`MoneyAudit.Households.own` is nought every month of `LongPlaytest` and the ensemble's 80 runs, the
caused months of item 6 are in `MoneyCheck`, `BankCheck`'s discharge section asserts that a family
whose savings cover its debt is not discharged, and the notes give the bankruptcies and leavers
before and after on two seeds.
