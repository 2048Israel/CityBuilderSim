# HA1 - the gate's harnesses: the longest first, a nightly tier, five merges

Jerus, 2026-11-12, on the harness audit (question box, his answers): the
runner "Longest first", "Skip soft timed sections", "Re-measure
SaveSlotCheck"; KeptCheck "Nightly + when picked (Recommended)"; the heavy
harnesses with no catch of their own "The 14 heavy (Recommended)"; "Merge the
five" and "Drop ConversionCheck + dead code". He also asked for a deeper look
at harnesses whose reds come from the city's path, not a fault. That is a
second round of calls, not part of this batch.

## What the game wants

A full-suite gate takes about 18 minutes instead of 38, and every assertion
still runs: at the gate, or every night on main's tip. A harness moved to
the nightly still runs at the gate whenever the batch's diff touches a class
it names.

WHY: measured 2026-11-12 from `checks/times.tsv` in four full gate runs (GR1,
GR2, DM2 twice: suite 2,161-2,661 s, 5,177-6,245 s of harness time). The
red record is 2026-10-15 to 11-12: 214 batch notes and 159 gate records.
The audit's tables (every harness: time, catches, reds, overlap, the call) are
in the session notes `harness-audit.md`. The figures the batch needs are
here.

- The suite runs three at a time (`AllChecks.PARALLEL`). `LONGEST_FIRST` was
  measured 2026-10-27 and does not name KeptCheck (1,079 s today) or
  CentralBankCheck (451 s), so KeptCheck starts about 1,200 s in and finishes
  alone. A simulation of the three slots gives 2,281 s, as the gates did.
  Started longest first by today's times, the same 201 harnesses take
  1,873 s, the floor (their sum / 3).
- KeptCheck: 5 catches of a batch's own fault in four weeks (KP2-KP5, GV1),
  each from a diff the pick names; 68 reds on no fault (13 latent, 48 timing,
  7 its own bugs).
- Fourteen harnesses of 43-216 s, 1,254 s together, caught no fault of a
  batch's own in four weeks: GlobeCheck, ShortSellingCheck, LandmassCheck,
  EducationCheck, OrderSearchCheck, MainLineCheck, BridgeMapCheck,
  AuctionCheck, GatewayCheck, HomeRailCheck, TruckRouteCheck, PrizeCheck,
  IslandLinkCheck, CopperRefiningCheck.
- Simulated suite: 1,873 s with the order; 1,514 s with KeptCheck moved;
  1,096 s with the fourteen as well; about 1,074 s with the merges.

## What to build

1. **The order.** A run of more than one at a time starts the longest
   first, by the seconds of the last `times.tsv` it can read (the run's own
   `target/checks`, else the gate's), and falls back to `LONGEST_FIRST` when
   there is none. `LONGEST_FIRST` is refreshed from today's medians: KeptCheck,
   CentralBankCheck, MapCheck, GlobeCheck, ShortSellingCheck, LandmassCheck,
   LongPlaytest, EarthTerrainCheck. This replaces Pending's CB1-L: delete its
   row.
2. **Soft timed sections are skipped.** Under `-Dcbs.timing=soft`, a section
   whose only verdict is a clock (`Timing.within`) does not run its rounds.
   It prints one line that it was skipped as soft. Hard runs (the nightly,
   `--timing`) run it as now. First measure each of the 18 timing harnesses'
   suite seconds with and without, and put the figures in the notes.
   TimingCheck holds the rule.
3. **SaveSlotCheck's clock.** Its one timing line, CLOCK_MS 25 ms, reads about
   29 ms on main and stood red in 109 of 159 gate records. Re-measure it on
   the gate box (Nov 14-15) on a quiet machine, and set the budget from that
   with the margin the other budgets use. Until the box runs, leave it. The
   budget is a premise: Jerus approved this one re-measure. No other budget
   moves.
4. **The nightly tier.** One list in `AllChecks`: KeptCheck and the fourteen
   above. The gate runs a listed harness only when `tools/pick.sh` names it
   by its direct rule: a changed class it names, under the fan-out. Its
   `ALL` fallback never names one. `tools/nightly.sh` runs the whole list
   every night on main's tip. On a red it names the merges since its last
   green run, as GR1's timing does. A harness keeps every assertion it has.
5. **Five merges.** Each section of its own moves word for word into the named
   harness. Only an assertion that another harness already makes, on the same
   kind of fixture, goes. The notes list each dropped label beside its twin.
   - MoneyCheck into LongPlaytest's audit (its prospering and bankrupt
     two-city fixture).
   - ConservationCheck into SaveFileCheck ("three loads into one Game double
     nothing").
   - SectorBooksCheck into SectorStatementCheck.
   - TruckCheck into TruckRouteCheck.
   - RestructureCheck's sections 5b-5d into BuybackCheck. The rest of
     RestructureCheck stays.
6. **ConversionCheck and the dead conversion code go.** `GridConversion`,
   `LandConversion` and `LegacyLand` (1,278 lines) serve the load path for
   formats at or below `LandConversion.LAST_LANES_FORMAT`.
   `OLDEST_LOADABLE_FORMAT` = `SAVE_FORMAT` = 51 refuses all of those, so the
   path is unreachable. Before cutting, check what `CityLand`, `LandSellers`,
   `EarthTerrainCheck` and `LandCheck` use from these classes.
7. **Six assertions that only read a run go** (Jerus, 2026-11-12, second
   round: "The 6, keep the fuzz (Recommended)"). Line numbers are of
   2026-11-12.
   - PassThroughCheck L626: the price index's mean gap, "the economy
     amplifying a small difference"; red in 19 gate records.
   - PassThroughCheck L702 (`over5 > 0`) and L703 ("moved in 90% of
     months"): shares of one 240-month run. The rule stays asserted exactly
     in its sections 2 and 3.
   - BuildingDataCheck L448 (`data.size() == 152`): every new building
     restates it. Unique ids and the card's and categories' checks stay.
   - InterestAbroadCheck L852 (`saved.length == 156`): SaveFileCheck L3956
     asserts the same.
   OrderSearchCheck's sections 1-2 (its fuzz over a long run's orders, which
   found EP1's and FR2's faults) stay, in the nightly tier.

## Done means

- A full-suite gate run on the PC is measured against the 38 minutes, its
  figure in the notes, and every harness in `AllChecks.HARNESSES` runs at the
  gate or in the nightly.
- `tools/pick.sh` names a nightly-tier harness for a diff to a class it names.
  A selftest causes this with a change to a class only GlobeCheck names.
- A nightly run on main runs the tier, and a red names the merge range.

## One in, one out

The order, the tier and the soft skip add to `AllChecks`, `pick.sh`,
`ship.sh` and `nightly.sh`. The batch removes as many lines from the same
machinery: the hand list's history comment, the CB1-L row, and whatever the
tier makes redundant in `ship.sh`'s FULL rule. The notes give
`git diff --numstat origin/main` over those files.
