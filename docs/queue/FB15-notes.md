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
  the down payment (m600-m706, the family segment 341-963 doors short). FB15's door cuts none of the 68 it
  passes with the segment short (section 4).

## 2. HN1-B: a project's order refused for the bank's capital or a ban, asked again of half

- **Found.** On main, `consider()`'s project path ran `largestSliceThatCarries`, then `buildFor(payer)`,
  then the lender's `canBorrow`/`credit.canFundProject` (all or nothing). It held the whole order on the
  bank's capital even when the till paid for part of it. The fixture shows this on main at m200: six
  Convenience Stores, the till holding 1.5 of them, and the line "Holding: the bank is rebuilding its
  capital".
- **Built.** The banner is "THE DOOR'S NO IS TO THE ORDER HERE TOO" (`Game.java:7175`).
  - `lenderWatched()` (`:7993`) wraps the payer. It records the lender's no, and whether the part built
    borrowed.
  - The order is halved while the lender's no is a ban or the bank's capital rule (`halfMayPass()`), and
    each try's no is read again. A ban is sized to the till already (the slice, `Game.java:7510`), so the
    halving catches only the door's wedge over the plan. Past the default point or with the bank shut the
    order holds, as on main (REV-FB15, mend 3): there the till is the sector's last money. The shortfall
    desk lends nothing then either, and an order in flight blocks the distress sale.
  - A part built takes back the capital marks: the project form of `takeBackCapitalRefusal`
    (`BusinessDebtManager.java:2684`, five lines, approved by mail; MC2's parts of the file untouched), so
    the playtest's "refused a building's loan" count is not inflated. The default point's mark
    (`refusedProjectAtDefaultPoint`, read by `LongPlaytest.java:698`) is never left beside a part built. A
    ban is tested before the default point. A capital no means the default point passed, and a smaller try
    reads the same till against it (`buildFor()` asks the lender before it spends).
  - Left as it is (REV-FB15-2, P3). `projectRefusal()` reads the lender's marks, and they last the month
    (`BusinessDebtManager.java:2629-2631`). So after an earlier order's capital no in the same sector, a
    later order refused at the debt line is asked again of half too. The lender tests the line on each
    half, so nothing is built that its tests refuse. Mending it means taking each try's own reason from
    `canFundProject()`, and the review asked for that only with the default trace shown byte-identical. That
    takes a 2,400-month run on each side, so the gap is noted rather than mended.
  - The line says why the order was trimmed (`projectDoorTrimmed()`, `:8020`), as `doorTrimmed()` does
    for the landlord.
- **The fixture**, OrderDoorCheck 3. The bank's capital rule is at 0% growth, and the shopkeeper's till at
  the door holds 1.5 stores. The order of six is now built at one, paid from the till at the door's price,
  says "(trimmed from 6 - the bank is short of capital for a loan, so what its own funds buy outright)",
  and is not left marked. The control, a till of half a store, is still held, in the bank's words. Three
  lines are red on main. The take-back line alone is red with the 3-argument take-back (`odc-tb.txt`). Since
  mend 3, a third case: with the bank shut and the till holding 1.5 stores, the order is held whole, the till
  untouched, as on main. Its line is main's too: "Could not build Convenience Store - needs 2,790 m², 0.0127
  km² free". A shut bank leaves no mark, so the hold falls through to the land's words with the ground
  there. That was so before FB15, and it is a finding for the queue, not mended here.
- **How often it fires** (contract: count it in a real run). Seed 12 to m900, counting month-ends whose
  last line says so:
  - FB15: 15 month-ends show a project order built in part outright (Retail 7, Restaurants 6, Materials
    2), and 3 one trimmed while borrowing;
  - main: none.

  All 18 were trimmed for the bank's capital (`target/fb15/p12-fix.txt`), so mend 3 leaves the count as it
  is.

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
- **Guarded (REV-FB15, mend 2; REV-FB15-2, 3).** With `ENS_A` empty, main's script matched every command line
  on the PC, because in PowerShell 5.1 `[regex]::Escape($null)` is the empty pattern. And on main
  `ensemble.sh --stop .` (or `..`) passed an ancestor of the runs as `ENS_B`, which matched every run or all
  of `target/`. Now `ensemble.sh --stop` refuses a label that is `.`, `..` or holds a slash, and `stop.ps1`
  refuses an empty `ENS_LABEL` or an `ENS_B` that is not `C:/.../target/ensemble/<label>`. Both stop
  nothing and exit 2.
- **The fixture**, `sh tools/ensemble/stop-selftest.sh`, ten checks:
  - A `tail -f` in a run folder under `target/ensemble/` (a /tmp folder shows as /tmp) must be stopped,
    and the stop must say nothing is left.
  - A parent outside the folder that restarts it must leave a survivor that is named, exit 1.
  - An empty label and folder, `.` with `target/ensemble`, and the label with `target` must stop nothing,
    exit 2. These are read on a copy whose `Stop-Process` only prints what it would stop. They run only
    where `stop.ps1` holds its refusal; otherwise they FAIL and are not run, so the empty case never runs
    against main's script.
  - `ensemble.sh --stop` with `.`, `..` and `a/b` is refused, exit 2. It runs with `PATH=/usr/bin`, so
    it could stop nothing anyway.

  On main, the three live checks fail, as they did in the first form of the self-test (no Git Bash form,
  so the `tail -f` is never matched). The refusal cases are read on main and not run: main has no
  refusal, and its `--stop .` hands `stop.ps1` all of `target/ensemble` as `ENS_B`.
- **One in, one out.** `git diff --numstat origin/main -- tools/ensemble/`: +84 -16. The two headers are
  back to their old length. The 52 lines of `stop-selftest.sh` are HN1-C's fixture and the review's
  refusal cases (a harness is the finding), and nothing in the ensemble's tools was stale to take out
  against them.

## 4. Harnesses the change moved

- **BuildCardCheck** priced the card's down payment at the planner's price. The card reads the door's
  since HN1-A, so the fixture now does too (`BuildCardCheck.java:2209`). Green.
- **HousingDoorCheck 1-2** (REV-FB15, mend 1). On main, the section's nine cut orders were all "its own
  funds put the 15% down on N": HN1-A's bug. The section counts the orders passed while the segment is
  short, and asserts the door cut or held none of them for the down payment (red on main, nine). On the
  branch the fixture line was red, and the harness caused it, not the city:
  - `ask()` named the ordered building by matching the lender's price of one against the planner's.
  - Since HN1-A that price is the door's, so on the branch every order read as no building: no segment
    short and no door price. That also left section 2 watching nothing.

  It now matches either price (`HousingDoorCheck.java:285`). Seed 12's window then holds 68 orders with
  the segment short: 57 written whole, 9 cut and 2 held, all for the bank's capital, none for the down
  payment. Both lines are green, so no row is filed.

  The review asked for homes retired as `cutToTheValve()` retires them, and two tries show why that went
  unused:
  - cut at m430, seed 12's city fell to 7,279 people by m900, with 4 orders in the window;
  - cut at m900, with twelve months more, it placed no order in them.

  The same fix went into OrderSearchCheck's `carryOfTheOrder()`, where an unnamed order fails its fixture.
- **CreditCrunchCheck 3-4** (mend 1). The trim line watched the played crunch. Since HN1-A, that crunch
  asks for one block at a time while the bank is short, and an order of one has no half to trim to. Main's
  seed 13 asked for up to 6 then (m190-194: 6, 6, 5, 5, 4). Its 7 trims, the three the log lists (m181,
  m191, m217), were orders of 2, 6 and 5 blocks of about 600 each, whose issue (243, 149, 882) covered a
  fraction of the cost.

  Section 4 (`theShortIssue()`, `CreditCrunchCheck.java:386`) now causes the case at the crunch's end, at
  m270 with the bank still in its band: an order of six blocks, a till of 1.5 blocks, and ground for six.
  - The door's price of the six is 2,807.5 against the plan's 2,791.8 (+0.56%, the yard short).
  - It watches what `consider()` did (REV-FB15-2, mend 1). The lender read the order at 2,807.5, the door's
    price. The owners were offered the 2,222.7 the till lacked at that price, and put in 211.4. On main's
    classes this line is red: the lender read the plan's price, and the offer was 5,116.6 of the 5,190.1 the
    till lacked at the door's (`target/fb15/ccc-main2.txt`). Main's city at m270 also has no ground for six.
  - The door's first no was for the bank's capital. It built 1 outright, "(trimmed from 6 - the bank is
    short of capital for a mortgage, so what its own funds buy outright)", and left it unmarked.

  Green. The played crunch's trims are still printed, and its "left marked" line still holds.
- **CreditCrunchCheck 2 and MortgageCheck 12** are played cities that moved.
  - Traced on seed 13 (`target/fb15/p13-*.txt`): the two runs are identical to m33. From m34 the lender
    tests a House at the door's price (+4.9%), and the down payment passes 6 where main's passed 7. At
    m40, main's door cut 4 to 2 while FB15 built 5.
  - By m120 the cities are still close (340 and 327 households, 255 and 258 homes). Then main's yard
    holds 1,363 materials against 930, and its wave of blocks at m122-126 (+127 homes) never comes on
    the branch.
  - At m150: 930 households against 483. HN1-A alone does it: the output is identical with HN1-B's
    halving switched off, and MortgageCheck's reds stay with it off too.

  Path chaos, not a trend, is the reading: the ensemble is the measure (section 7). These four lines are
  known reds under FS9, the bank and its borrowers, queued (`docs/studies/flaky-suite/by-harness.md:53`):
  - MortgageCheck 12's two;
  - CreditCrunchCheck 2's two. The bank rations in 111 of the 120 crunch months, against main's 120, and
    stands in its band in 119.

## 5. The calls

- ★1 **The halving follows a ban and the bank's capital rule only** (REV-FB15, mend 3; first built for
  any lender's no). Past the default point or with the bank shut the order holds whole, as on main:
  nothing else stops the till going there (`buildFor()`, the shortfall desk, the order in flight blocking
  the distress sale). The line names the reason: "the bank is short of capital for a loan" or "it may not
  borrow".
- ★2 **The take-back covers the project mark**, a new 4-argument form beside the 3-argument one (the
  orchestrator's yes).
- ★3 **The build card prices a landlord's home at the door** (`financingOf()`, `lenderTest()`), so the
  card and the order agree. BuildCardCheck follows.
- ★4 **`consider()`'s ban check reads `orderCostOf(decision, 1)`**, so a landlord may not borrow for what
  the door would charge.
- ★5 **OrderDoorCheck drives `consider()` by reflection between months**, after the month's settle,
  with the hooks of `BusinessInvestment.OrderWatch`. It buys its ground (`buyLandBlock()`), so it causes
  the edge rather than waiting for a played month that has the ground too.
- ★6 **The moved fixtures go under the sweep that holds them**, FS9 (FS1, which the brief named, has
  shipped), rather than re-seeded here. FB15's own premises are caused instead (REV-FB15): CreditCrunchCheck
  4's order, OrderDoorCheck's edge. HousingDoorCheck's needed only its harness to read the door's price.
- ★7 **CreditCrunchCheck 4's order is six blocks on a till of 1.5** (`ORDER`, `TILL`), main's largest ask
  while its bank was short; the till buys one whatever the issue brings, so the trim has a part to build.

## 6. The checks

- `./batch-check.sh --fast`: exit 0, on the branch, on the merge, and on the mends.
- Named harnesses, one per call. Before the mends:
  - green: OrderDoorCheck, BuildCardCheck, CreditCheck, InvestCheck, BondCheck, BuildersFirstCheck,
    ConstructionTrapCheck, DenominationCheck, FittingCheck, PrizeCheck, RefineryCheck, ReloadCheck,
    SaveFileCheck;
  - main's known red only: BankCheck, YoungBankCheck.

  On the mends: OrderDoorCheck and HousingDoorCheck green; CreditCrunchCheck and MortgageCheck red on the
  FS9 rows only.
- `sh tools/ensemble/stop-selftest.sh`: all passed (five; ten on REV-FB15-2's mends).
- On REV-FB15-2's mends: `--fast` exit 0; CreditCrunchCheck red on the two FS9 rows only, section 4 green.
- On the merge of AP1 (0.9.175, "the playtest unmoved", so the ensemble at 7c355a19f stands): `--fast` exit 0,
  OrderDoorCheck green.
- `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed). Review: needed - money (the brief).
- The second ensemble and the dry run: below, when run.

## 7. The ensemble: HN1-A's measure

**The first run.** On the gate box, label FB15, at e478638e3 (HN1-A and HN1-B as first built), against
`docs/ensemble/0.9.174.md`:
- 25 of 26 criteria pass, 10 of 10 hard, no verdict changed. The one red is AUTO2 A2 (info), which fails on
  0.9.174 too. M1 is 0, and F1 is 0/16 on AUTO2 and SHOCK alike.
- T1: AUTO2's 16 seeds all reach 40,000 people, at m749 [634 to 1,508] against m853 [490 to 2,058].
- AUTO2 T2, the final population: 2,077,789 [34,353 to 4,471,796] against 2,226,328 [382,368 to 2,768,537].
  The median is 6.7% under 0.9.174's, past the 5% line, and the spread is wider at both ends.
- AUTO2 A3, final over peak: 100% [77.00% to 100%] against [99.15% to 100%].
- SHOCK T2: 40,291 [28,677 to 109,823] against 38,260 [32,485 to 144,607], 5.3% over.

Per-seed final populations (0.9.174's baseline has none):

| seed | AUTO2 | SHOCK |
|---|---|---|
| 0 | 4,471,796 | 61,763 |
| 1 | 2,534,748 | 38,904 |
| 2 | 34,353 | 40,043 |
| 3 | 1,510,970 | 43,542 |
| 4 | 3,804,049 | 109,823 |
| 5 | 3,731,744 | 45,451 |
| 6 | 2,057,015 | 31,333 |
| 7 | 1,947,424 | 40,539 |
| 8 | 1,824,859 | 28,677 |
| 9 | 2,850,968 | 44,543 |
| 10 | 2,857,661 | 57,127 |
| 11 | 3,640,517 | 49,346 |
| 12 | 1,143,487 | 35,948 |
| 13 | 2,098,563 | 30,440 |
| 14 | 1,836,939 | 34,849 |
| 15 | 1,691,978 | 36,769 |

AUTO2 seed 2 is the low end, and it is far under 0.9.174's lowest (382,368). It peaks at 44,616 near m800, then
holds at about 31,000 from m800 to m2400 with unemployment at 11.5-12.7%, and ends at 34,353 (A3's 77%).
Its CSV has no housing column, so this run cannot say whether housing holds it. The orchestrator's test for
the extra runs (AUTO2's or SHOCK's median more than 5% under 0.9.174's) is met by AUTO2's -6.7%. REV-FB15 asks
for them in any case: the mended commit, and AUTO2 and SHOCK on 0.9.174, with `unplaced` and `homes` added.

**The second run.** On the gate box, label FB15b, at 7c355a19f (REV-FB15's mends; REV-FB15-2's since are
harnesses and tools only), against `docs/ensemble/0.9.174.md`, with
`-Densemble.extra=unplaced=getFamilies.getStillUnplaced;homes=getBuildingManager.getTotalHomes`:
- The score is the first run's: 25 of 26, 10 of 10 hard, no verdict changed, the same T1 and T2. The 66
  columns the two runs share are byte-identical in all 80 CSVs, so REV-FB15's mends (mend 3's narrower
  halving among them) changed nothing these runs reach.
- `unplaced` is 0 in every month of all 80 runs. A getter this tree lacks would read NaN, not 0.
- Committed as `docs/ensemble/FB15.md`, with the version token in its heading and first line.

**0.9.174 again, for homes and unplaced.** On the gate box, label FB15-174, at 29f1e6a3f (the tag), AUTO2
and SHOCK, seeds 0-15, with the same two extras. It reproduces `0.9.174.md`'s T1 (m853 [490 to 2,058]) and
both T2s exactly. Medians [min to max] at the last month, 0.9.174 against FB15b:

| | 0.9.174 | FB15 |
|---|---|---|
| AUTO2 homes | 599,425 [99,566 to 758,260] | 551,461 [12,527 to 1,203,630] |
| AUTO2 homes per 1,000 people | 269.0 [258.2 to 273.9] | 269.4 [261.9 to 364.7] |
| SHOCK homes | 11,739 [8,663 to 38,196] | 11,808 [7,404 to 29,624] |
| SHOCK homes per 1,000 people | 265.6 [248.1 to 351.3] | 283.4 [258.0 to 316.1] |
| unplaced, any month of any run | 0 | 0 |

- No city on either side leaves a family unplaced in any month. Homes per person hold on AUTO2 and rise on
  SHOCK. AUTO2's total is 8.0% lower because its population is 6.7% lower; the homes follow the people.
- Seed by seed, the two versions are different cities, not one city moved. AUTO2 seed 2 is 2,720,143 on
  0.9.174 and 34,353 here, and seed 8 is 382,368 there and 1,824,859 here. Each side has one low seed, so
  a per-seed loss is no measure of HN1-A.
- Seed 2's stall is not housing: it ends with 364.7 homes per 1,000 people, the most of any AUTO2 run, and
  nothing unplaced. Its unemployment at 11.5-12.7% from m800 is the first run's reading.
