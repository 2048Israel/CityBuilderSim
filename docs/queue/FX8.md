# FX8

**XR2's loose ends off the default path.** These were found by the
orchestrator's stranger review of XR2 (0.8.22) before it shipped. None of them
moves the default playtest, where every new branch is off under Lean.
2026-10-22.

## What to fix

1. **The "central bank keeps nothing" identity** (equity - vault = remittance
   due - loss carried) is false by `CentralBank.getDollarsBoughtLifetime()`
   whenever the central bank pays the strong edge in new money (★6, XR2's
   default payer). It is asserted at `CentralBankCheck.java:235` and
   `LongPlaytest.java:1178`, never under a regime, so nothing is red. Restate
   it with `+ getDollarsBoughtLifetime()`, make CurrencyRegimeCheck §9 and
   §11 assert it under a peg the central bank pays, and correct
   `CentralBank.java:78`'s banner ("bought the vault with its own cash, not
   with money made here").
2. **`ForeignAccounts.setRegime()` keeps doubtShare** (about line 1130). Leaving
   a peg between presses lets the hot money leave at the old hazard for one
   more month, against Game's "0 under every other regime" (about L10103).
   Clear it on leaving a peg, with a section that causes it.
3. **`CapitalFlows.doubtDeparted` is not saved**, and neither are `arrived`
   or `departed`. PolicyScreen.java:3630 shows it the month after ("of which
   ... left on doubt"), so a load drops that clause. Save them (a flow cannot
   be rebuilt from the state a month ended in), with SaveFileCheck and
   ReadPathCheck.
4. **HoldersCheck's array length** reads `size() >= 15` (HoldersCheck.java:1388).
   Make it `==` the current length, so the next append is caught.
5. **`lifetimeIntervention`** counts the central bank's defence sales but not
   its new-money purchases at the edge. Count them, or say in its sentence
   why not.

## For Jerus (add to XR2-C, not this batch's to decide)

- A peg pays the month's balance, not the pressure: in a surplus month with
  a weak shadow it buys dollars (ForeignAccounts.java:941), and a band's weak
  edge holds for free in a surplus month (L902).
- While the dial is tied under a peg, the curve's expected path still prices
  the inflation rule (`DebtManager.pathFor`, L1799).

The trace should not move. Say so if it does.
