# FW4 - automatic building in a young city, and PlanCheck's save section (0.8.42)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md`, FOUND ON THE
WAY IN 0.7.87 TO 0.7.92 ("Automatic building is fragile"; RD4's §7).

## 1. A young auto-built city

**Measured.** `LongPlaytest -Dplaytest.autobuild=true -Dplaytest.months=1000`
over seeds 0 to 39 on main at 0.8.40 (with PT1-M4's founding Builders' Yard):
every city lives. The populations at month 1,000 run from 66,270 (seed 31) to
209,195 (seed 21), the median about 127,000, and at month 256 from 1,723 to
about 2,100. No seed ends at the hundreds or low thousands of a stalled city.
The advisor's own refusals for money are rare: an Industrial Bakery once or
twice in 29 seeds and a House twice. **Young cities that stall: 0 of 40
before, and 0 of 40 after**, since FW4 changes no model code.

**The cause, and where it went.** The 2,626-people run was on RD4's tree
(0.7.90, dropped 2026-10-09). 0.7.89 itself cleared the same month-60 order
with $0.28M to spare. Until 0.7.101, AutoBuilder held the whole measure when
an order could not be paid at a debt limit of 60% of a young city's year of
GDP. P2 (0.7.101, `9b884266`) made a refused order pass to the next in the
ranking, put the default limit at 240%, and gated on debt to GDP alone. That
was the fix; FW4 adds the harness that holds it.

**AutoBuildCheck 7, "a young city refused for a small sum keeps building".**
A young city is played as section 3 plays its own, to month 60 (1,704 people).
Its limit is pinned at its own ratio (70%, `nearTheLimit()`) so that no bond
fits. A saved copy at the reserve names the first order its pass holds for the
money: 1 Small Childcare Centre at $1.9M. The city's cash is then set to the
reserve plus that cost less `SHORT` ($280,000). It asserts:
- (a) the pass refuses it for the money (HELD, DEBT), nothing ordered ahead of it;
- (b) the refusal does not stop the pass: all 11 kept measures ranked after it
  have their step that month;
- (c) with the limit back at `DEFAULT_DEBT_LIMIT`, the refused building is
  ordered in month 61, and the city has 2,175 people 24 months on.

The first draft also left the limit pinned for 24 months, and that failed:
the young city's year of GDP falls from $195M to $81M after its founding
settles, so the pin is passed within a month, and a city over the limit builds
nothing by design. That run is now an info line. A side observation for
Jerus: this village, at the default limit, orders only that Childcare Centre
and one Gravel Road in 100 months. Every school and the police are held for
the payback (0.7.104), so an auto-built village depends on the player for its
jobs, as the playtest's player provides them.

## 2. PlanCheck with a save named

**His saves no longer load.** Slots 1-9 and the autosave are SAVE_FORMAT 21
to 44, and main reads 45 at the oldest. So it was reproduced on a played city:
the default playtest at month 4,000 (765,819 people, 25 districts), kept by
`-Dplaytest.keep` into a scratch folder and renamed `autosave.*`.

**Reproduced.** 16 of 25 districts keep every check. One network, the +
floor, reach, none on a street, surface and the estate lines hold in all 25;
"none beside a highway" holds in only 16.

**The cause: FX7's as-built rule (0.8.24, DistrictPlan "THE RUNS DATED"),
not the plan's placement and not the missing chain.**
- Every placement keeps the verge. Every `Builder.put` was counted against
  the highway ground the plan had at that moment: 0 of about 17,000 in the
  single plans and 0 of 19,205 in the chain land beside a highway.
- The same plans with the runs undated (`fixedLog` null) fail no district.
  With the runs dated, the 9 that fail are exactly the 9 with highway records,
  and every highway plot touching an offending box was laid by a record
  (months 1,680 to 3,583).
- A box standing when a highway is laid later stays "on it or beside it"
  (FX7's ★1). Section 9 tests every box against today's highways.
- The game's chain (all PlanJobs, run in order) fails the same 9 districts
  (384 boxes), so the 0.7.89 note's guess, "single plans without the chain's
  joins", is not today's cause. The city's edge packing
  (`CityMap.freeGround`) already excludes today's verge.
- Of the single plans' 233 boxes, 87 stand under a highway and 146 only on
  its verge.

**Not fixed, on purpose.** Moving buildings off a later run is Jerus's call,
already queued as MF1 item 1 ("Buildings move off"). MF1 as written moves
only the boxes under a run, which leaves the 146 on the verge and section 9
red. Restating the check would excuse those boxes, and the only cheap form
(count only the highways at the first batch's month) is blind to a box that
arrived after a later highway. That would weaken the premise, so it was not
done. The question is written into MF1 item 1 and under Pending as FW4-C.
Section 9's "each planned in no more than 40 ms" read 40.51 ms; that is the
known soft miss on this two-core machine.

## Overlap with the Earth lane

None. FW4 changes only `AutoBuildCheck` (section 7), and touches nothing in
the auto-builder's site choice that ET2 and ET3 work on.

## Checks

`./batch-check.sh` after the merge of main (0.8.41): see the batch's report.
Main's own red is MapCheck 8 ("its streets one network", FW11's). No model
change, so both playtests' traces cannot move and the ensemble was not run.
