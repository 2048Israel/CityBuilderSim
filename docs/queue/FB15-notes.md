# FB15 - HN1's three leftovers (2026-11-20 to 11-21)

The batch's record beside its brief, `FB15.md`: the three Pending rows HN1 left (HN1-A, HN1-B, HN1-C), each
built the recommended way, each with a fixture that causes it and fails on main's code. Built by one Claude
session (Opus 5.5) on `batch/FB15`, cut from origin/main ac224abf3 (0.9.172), merged with origin/main a9bfcd070
(0.9.174, OE1 and FS4) at 5eb55e7c5. Line numbers are this branch's.

## 1. HN1-A: a landlord's order at the door's own price

- **Found.** There are two prices. The planner's is `BusinessInvestment.getCostOf()`: the materials at the
  builders' price, plus `groundCostOf`. The door's is what `buildFor()` charges: `quoteBuild(t, n).total`
  (materials beyond the yard at the market's draw, plus bitumen and prep), plus
  `landManager.priceFor(...)`. The two differ while the yard is short of the order's materials:
  - seed 12 at m219: a Low-Rise block at 803.84 against 805.05 (+0.15%), the yard holding 142 of 377;
  - seed 13 at m31-m40: a House at +3.4% to +4.9%, the yard empty.

  `consider()` raised equity to the plan's down payment exactly, so an order sized at that edge failed at
  the door, even for one.
- **Built.** `Game.doorPriceOf(t, n)` (`Game.java:7743`) is the door's price, read as the door reads it.
  `orderCostOf()` (`:7737`) gives that price for a landlord's home and the planner's for anything else.
  The landlord's home is now priced at the door in four places:
  - `consider()`'s equity raise (`:7053`);
  - its ban check (`:7129`);
  - `lenderReads()`' cost function (`:7791`);
  - `financingOf()` (`:7762`), which feeds the build card.
- **The fixture**, OrderDoorCheck 1-2. The fixture: the ensemble's AUTO2 seed 12, played from m200 to the
  first month-end where the landlord is at the edge, with ground for three blocks bought through
  `Game.buyLandBlock()`. The edge means:
  - the till is empty and the register raises nothing;
  - the lender passes one block and two;
  - the door's price is over the plan's.

  The checks:
  - an order of one, and then of two, is built whole on an insured mortgage at the door's price;
  - the owners put in exactly the down payment at that price;
  - the build card's lender test reads the same price.

  Five lines are red on main (`target/fb15/odc-main.txt` on the PC): main's order of one is held "for the
  15% down payment", and its order of two is trimmed to one.
- **In a played city.** In HousingDoorCheck's seed 12 window (m431-900), main's door cut nine orders for
  the down payment (m600-m706, the family segment 341-963 doors short). FB15's door cuts none.

## 2. HN1-B: a project's order the lender says no to, asked again of half

- **Found.** On main, `consider()`'s project path ran `largestSliceThatCarries`, then `buildFor(payer)`,
  then the lender's `canBorrow`/`credit.canFundProject` (all or nothing). It held the whole order on the
  bank's capital even when the till paid for part of it. The fixture shows this on main at m200: six
  Convenience Stores, the till holding 1.5 of them, and the line "Holding: the bank is rebuilding its
  capital".
- **Built.** The banner is "THE DOOR'S NO IS TO THE ORDER HERE TOO" (`Game.java:7175`).
  - `lenderWatched()` (`:7993`) wraps the payer. It records the lender's no, and whether the part built
    borrowed.
  - The order is halved while the lender says no. A part built takes back the capital marks: the project
    form of `takeBackCapitalRefusal` (`BusinessDebtManager.java:2684`, five lines, approved by mail; MC2's
    parts of the file untouched), so the playtest's "refused a building's loan" count is not inflated.
  - The line says why the order was trimmed (`projectDoorTrimmed()`, `:8020`), as `doorTrimmed()` does
    for the landlord.
- **The fixture**, OrderDoorCheck 3. The bank's capital rule is at 0% growth, and the shopkeeper's till at
  the door holds 1.5 stores. The order of six is now built at one, paid from the till at the door's price,
  says "(trimmed from 6 - the bank is short of capital for a loan, so what its own funds buy outright)",
  and is not left marked. The control, a till of half a store, is still held, in the bank's words. Three
  lines are red on main. The take-back line alone is red with the 3-argument take-back (`odc-tb.txt`).
- **How often it fires** (contract: count it in a real run). Seed 12 to m900, counting month-ends whose
  last line says so:
  - FB15: 15 month-ends show a project order built in part outright (Retail 7, Restaurants 6, Materials
    2), and 3 one trimmed while borrowing;
  - main: none.

  "Holding: the bank is rebuilding its capital" still stands where even one is beyond the till (Automotive
  81 month-ends, Freight 57).

## 3. HN1-C: the ensemble's stop looks once more

- **Found: why HN1's xargs outlived both passes.** Git Bash hands a Windows program an environment value
  `/c/...` as `C:/...`, so `ENS_A`, the Git Bash form, reached `stop.ps1` as the Windows form. The
  shells and xargs are MSYS processes, and their Win32 command lines show `/c/...`, so no shell or xargs
  ever matched. Only the JVMs, which write `C:/...`, were stopped.
- **Built.** `stop.ps1` now builds the Git Bash form itself from `ENS_B`. After its two passes it looks
  once more:
  - nothing left: "nothing of run L is left", exit 0;
  - otherwise one "left: PID Name CommandLine" per survivor, then "N of run L outlived the stop: stop it
    again before a resume", exit 1.

  `ensemble.sh --stop` passes the exit through, and the README says so.
- **The fixture**, `sh tools/ensemble/stop-selftest.sh`. A `tail -f` in a run folder under `target/` (a
  /tmp folder shows as /tmp) must be stopped and the stop must say nothing is left. A parent outside the
  folder that restarts it must leave a survivor that is named, exit 1. Main's `stop.ps1` gives three
  FAILs, the new one none.
- **One in, one out.** `git diff --numstat origin/main -- tools/ensemble/`: +55 -15. The two headers are
  back to their old length. The 29 lines of `stop-selftest.sh` are HN1-C's fixture (a harness is the
  finding), and nothing in the ensemble's tools was stale to take out against them.

## 4. Harnesses the change moved

- **BuildCardCheck** priced the card's down payment at the planner's price. The card reads the door's
  since HN1-A, so the fixture now does too (`BuildCardCheck.java:2209`). Green.
- **HousingDoorCheck 1.** Its fixture was HN1-A's bug: on main, its nine cut orders were all "its own
  funds put the 15% down on N". The section now counts the segment-short orders, and asserts the door cut
  or held none of them for the down payment (red on main, nine). On the branch, seed 12's city has no
  month in the window with the segment short, so the fixture line is red: a known red under FS1.
- **CreditCrunchCheck 2-3 and MortgageCheck 12** are played cities that moved.
  - Traced on seed 13 (`target/fb15/p13-*.txt`): the two runs are identical to m33. From m34 the lender
    tests a House at the door's price (+4.9%), and the down payment passes 6 where main's passed 7. At
    m40, main's door cut 4 to 2 while FB15 built 5.
  - By m120 the cities are still close (340 and 327 households, 255 and 258 homes). Then main's yard
    holds 1,363 materials against 930, and its wave of blocks at m122-126 (+127 homes) never comes on
    the branch.
  - At m150: 930 households against 483. HN1-A alone does it: the output is identical with HN1-B's
    halving switched off, and MortgageCheck's and HousingDoorCheck's reds stay with it off too.

  Path chaos, not a trend: the ensemble is the measure. Six lines, known reds under FS1 (the brief's rule
  for a moved played city).

## 5. The calls

- ★1 **The halving follows any lender's no on the project path**, not only the bank's capital. A ban, a
  failed bank or the default point now build what the till buys outright, and the line names the reason
  ("it may not borrow", "the bank has failed", "a loan would leave it owing past ..."). Before, these read
  "Could not build - needs land" or similar. Recommended: keep. The till's own money breaks no lender's rule.
- ★2 **The take-back covers the project mark**, a new 4-argument form beside the 3-argument one (the
  orchestrator's yes).
- ★3 **The build card prices a landlord's home at the door** (`financingOf()`, `lenderTest()`), so the
  card and the order agree. BuildCardCheck follows.
- ★4 **`consider()`'s ban check reads `orderCostOf(decision, 1)`**, so a landlord may not borrow for what
  the door would charge.
- ★5 **OrderDoorCheck drives `consider()` by reflection between months**, after the month's settle,
  with the hooks of `BusinessInvestment.OrderWatch`. It buys its ground (`buyLandBlock()`), so it causes
  the edge rather than waiting for a played month that has the ground too.
- ★6 **The moved fixtures go under FS1**, as the brief says, rather than re-seeded here.

## 6. The checks

- `./batch-check.sh --fast`: exit 0, on the branch and on the merge.
- Named harnesses, one per call, on the branch:
  - green: OrderDoorCheck, BuildCardCheck, CreditCheck, InvestCheck, BondCheck, BuildersFirstCheck,
    ConstructionTrapCheck, DenominationCheck, FittingCheck, PrizeCheck, RefineryCheck, ReloadCheck,
    SaveFileCheck;
  - main's known red only: BankCheck, YoungBankCheck;
  - the six moved lines above: CreditCrunchCheck, MortgageCheck, HousingDoorCheck.

  On the merge: OrderDoorCheck and BuildCardCheck green; CreditCrunchCheck, MortgageCheck and
  HousingDoorCheck red on the rows filed only.
- `sh tools/ensemble/stop-selftest.sh`: all passed.
- `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed). Review: needed - money (the brief).
- The ensemble and the dry run: below, when run.
