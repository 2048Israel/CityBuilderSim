# SB1 - `retire()` scraps from every stack of the template: the premise is false

Closed as a harness only, untagged, no model change (the orchestrator's call, 2026-11-20 13:03, after the
finding below). A template has one stack in every city the model can make, so `retire()`'s early return is
exact and there is nothing for a loop over more stacks to change.

## Found

- **One creator of a stack.** `BuildingManager.addStack` (5123) is the only place a `BuildingsStacks` is made
  (`new BuildingsStacks` at 5147, `stacks.add` at 5149; `stacks` is private and `getStacksView` read-only), and
  before it makes one it walks the list for a stack of the template's *name* (5133) and adds the order to it,
  finished (`addQuantity`) or on site (`startConstruction`). `clearStacks` (8696) and `orderStacks` (5095) only
  empty and reorder.
- **Every other way in goes through it.** The founding's bank branch and yard (`Game` 919, 1005), a player's
  or the planner's order (10199, 10503, 11389) and the load (18184, 21568; the empties the save's stack order
  names, 21592, only where `getStack` is null). A save keys a stack by its id, so a list read cannot hold two.
- **Ids and names are unique** in the 155 templates of `buildings.json` (`BuildingDataCheck` 905-916 asserts the
  ids); `retire` matches by id, `addStack` by name, `getStack` by `equals`: one stack either way.
- **Where the premise came from.** HS1's `OutsideCheck` closing fixture (537-541, `HS1-notes.md` 82) read
  `retire()`'s `return scrapped` inside the loop as a fault ("a template built in several stacks would be only
  partly closed"). It never measured a second stack: the fixture's loop over every stack by hand is harmless and
  what moved its numbers was the depots (490 jobs lost), as that note says. The row's "after SZ1" expected
  lots to make more: SZ1's lots are bond lots (`BondMarket.issueLot`), and `BuildingsStacks.Lot` (R1e, an
  order's frozen equipment) lives inside one stack. Neither makes a second.
- OutsideCheck's comment is left as it is (a conditional, true, and editing a long harness only to reword
  it would widen the pick); `HS1-notes.md` is the record.

## What was built

`BuildingStackCheck` section 10 (the registered harness, 10.5 s). A new `SB1Check` would need a line in
`AllChecks.HARNESSES` (`tools/pick.sh` says an unregistered harness exits 1), and `AllChecks` picks ALL.
It is the finding and its tripwire: the day a second stack of one template can stand, it goes red.

1. The tripwire's own reading, `mostPerId`, is proved on lists it can tell apart ({3,5,3} gives 2, {3,5,8} 1, none 0).
2. Every template of the catalogue (155) ordered finished, on site and finished again: 155 stacks, each 8 standing
   and 4 on site.
3. A played city (`LongPlaytest.founding()`, `cityOn`), 24 months with House ordered in three waves, shops and a
   bakery beside: the stack count read at the end of every month, never more than one stack of a template (up to
   8 stacks stood; the city shed once by itself in the run). Saved and loaded: the same stacks in the same order,
   each of its quantity and on-site count; one stack each after the load and two more months.
4. The closing path, through `Game.demolish` and the next month's `closeDemolished`: six Police Stations, two
   ordered demolished stand until the month starts, then four stand; the log has the two; their steel is in the old
   metal; the other four ordered, none stand, the emptied stack stays in the list, and a further
   `retire` of it scraps none. Old metal is read as a ratio (the four's steel is twice the two's), not by the
   model's constants.
5. The shedding path, `Game.retire(Decision, Investor, boolean)` (private; called as `BankFailureCheck` 85 does):
   a shed of three of a shop stack that stands at eight (two more on site) scraps three; the stack, the ground
   back to the land office (3 times the plot), the posts (3 times the template's), the treasury's payment for the
   plot at the zone's price (priced before the release), the log line (three, the proceeds) and the salvage
   (three buildings, the share of their material) are each three's. Asked fifty more than stands it scraps the five
   that stand, leaves the stack in place emptied and the two on site on site, and books the five; asked of the
   emptied stack it scraps none and books nothing (no cash, ground, log line or salvage).

## Checks

- `./batch-check.sh BuildingStackCheck`: green, section 10's 46 lines OK, none FAIL.
- **Mutation, both at once, run and restored** (`target/sb1-mut.txt`): `addStack` never merging (`&& false` in the
  name match) and `retire` returning the asked count, not the scrapped. 18 lines went red, among them: the stacks
  465 against 155, "no template has two" 7 against 1, the loaded city 9 stacks against 17, the over-ask 55
  against 5, the emptied stack scraping 4 against 0, and the books of the emptied shed. So the tripwire and each
  book are not vacuous. (Sections 1 to 9 went red on the first mutation too: their counts read the merge.)
- The default trace: no game class changed, so it is the trace of main, not moved; the 80-run ensemble is not owed.
- A first run was red on a fixture of mine, not the model: 1e12 added to the treasury lost the plot's price's last
  four digits (a double of that size has ulps of 1e-4), so the fixture cash is `FIXTURE_CASH = 1e6`; and the log
  lists newest first, so the harness reads `get(0)`.

## Calls

| | call | why |
|---|---|---|
| ★ | closed as premise false, no `retire()` loop | a loop over stacks no city has changes no city; `getQuantity`, `getStack`, `countByName` and `demolishable` read one stack too, so a guard on `retire` alone would be a half guard for a state the model cannot reach (the orchestrator, 13:03) |
| ★ | no reflection-forged second stack | the orchestrator's call: a harness that builds a state the model cannot reach proves the forge, not the city |
| ★ | section 10 of `BuildingStackCheck`, not a new `SB1Check` | registering a harness edits `AllChecks`, whose pick is ALL; `BuildingStackCheck` is registered and is about stacks |
| ★ | `Game.retire` called by reflection | it is private and every shedding caller (the investment pass, the distress rule, a worn well, a branch, a lab) is a rule of its own that decides the count; the books are what `retire` does with it. Precedent `BankFailureCheck` 85 |
| | OutsideCheck unchanged | see Found |

## Left for Jerus

Nothing. The SB1 row leaves the queue with this merge (the changelog paragraph carries the reason).
