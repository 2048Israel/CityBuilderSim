# QB1 — The test player borrows at the quoted rate

Started from main `916b7c16` (HW1, 0.9.104), 2026-11-14.
Brief: CC1-S-notes section 7; Jerus took all recommended decisions.

★ Compare the sale's actual home and foreign quotes by default, retaining
`-Dplaytest.quotedBorrowing=false` as the previous-path counterfactual.
The existing quote selection and the `ABROAD_SHARE` threshold stay intact.
No save format or player UI changes are needed.

The starting known-red registry contains no QB1-owned rows. Existing PX1,
BF1-M and DM3-R map/planner failures remain separate from QB1's findings.
WN5d's CityLand, LandMap, LandMarket, LandSellers, ui/MapView and
LandNationsCheck are outside this batch's edits.

Implementation and fixture attribution are complete; the ensemble and coordinated dry run remain.

## The moved fixtures

★ ScaleCheck's own-town copy moves from month 172 to month 225. The old
borrowing rule passes at 172 (`target/checks/qb1-scale-main-rule.log`).
With quotes on, the savings and deposit misses are both 0.0355, over the
unchanged 0.016 and 0.026 floors (`target/checks/qb1-moved.txt`, initial run).
The temporary scan used the same town, foreign-rate pin, death hold, scale
copies, free sectors and held-sector tracking as the harness. Months
168–176, 180, 190, 200, 210, 160, 150, 165 and 155 did not qualify.
Month 220 had no numeric red but missed a million-building interest-order
premise, so it was rejected. Month 225 qualifies: 10,524 people, growing
over the previous year, ground free and affordable rents for all six months;
both free cities exercise the large-order and interest premises. The held
copy's worst savings miss is 0.00893 and deposits 0.00745. No assertion,
threshold, plan or mechanic changed. Scan logs: `target/checks/qb1-scale-probe{,2,3}.log`.

AutoBuildCheck 7 already causes its shortage on current main (FR8/IC1/GW1):
turn the pass off while clinics on site open, then retire the standing
general-care clinics. With quoted borrowing it reaches m64, 2,512 people,
zero care buildings, supply 1,200 versus demand 2,512. Its first refused
order is its own general care, held for DEBT with the shortfall SHORT;
the pass reaches all 14 later measures, and restoring the limit recovers
that service's own order one month later and grows the city. All existing
assertions pass. Raising the road slack as the old brief suggested is no
longer needed; preserve this explicit shortage and its stronger recovery law.

OrderSearchCheck's existing month-500 fixture and complete harness pass with
the switch on; retain it, since re-picking a qualifying fixture adds no
coverage. The initial focused logs are retained under `target/checks/`.

## The older brief against today's main

★ Keep CentralBankCheck's seed-7 shape (43 houses, four founding months),
OrderSearchCheck's month-500 copy, PlanCheck and HomeRailCheck unchanged.
The requested re-picks were conditional on the switch moving their premises;
only ScaleCheck needs one on HW1 main. The initial focused run exits 4 with
exactly ScaleCheck's two assertion reds and their count/exit lines; all five
other harnesses pass. Log: `target/checks/qb1-moved.txt`.

CentralBankCheck's probe has 18,529 people at m600; very strict/Standard/very
loose inflation over the horizon is 4.641%/5.120%/6.670%. Its preview, rule
identities, strict-policy set and every assertion pass. The deliberate world
price fall still causes both sides of the loose rule (13 months under target
in the first 24). No premise is loosened to choose this city.

PlanCheck's played fixture plans eight districts in four samples through m121;
its every-30-month law and avenues law both pass (312 avenue plots in 11
open cells). The complete harness passes, including its drawn-city law.
No new planner failure is present to fix or transfer to PX1, BF1-M or DM3-R.

HomeRailCheck passes in full. Its real run plays 1,255 months: a station stands
498 months from m698; the yard 227 from m999; positive home share 135 months
from m699, 517,183 home tonnes, billed 38,919k and burning 2,481,008 litres.
Zero months have a home share without room, or a quay share without a yard,
at the haul or month's end. FR8's existing check uses room at the haul as
well as month-end because later distress retirement can remove the station.
That recorded timing fault from the older brief is already repaired on main;
the quoted path now satisfies the unmodified law. Freight and every planner
and map class are untouched by QB1. None of WN5d's reserved files changed.

Fast check: exit 0, compile/maps/tree green, no firm stale findings.
Initial focused run: only the two now-repicked ScaleCheck laws red; one
soft PlanCheck timing, one AutoBuildCheck played-path observation.

## Full default trace beside main

The CC1-S brief's month-33 divergence predates HW1 and the intervening model
changes. On today's main, two complete default runs reach m2404 with all
eight CSVs **byte-identical** (bank, bonds, borrow, build, house, labour,
month and population): `target/qb1-main-*.csv` uses
`-Dplaytest.quotedBorrowing=false`, the otherwise unchanged main rule;
`target/qb1-final-*.csv` uses the new default. There are 2,404 monthly rows
and 43 borrowing rows each. `cmp` returns 0 for each of the eight pairs.
`target/checks/qb1-trace-comparison.json` retains the row comparison.

Both full reports exit 1 solely for the already registered DM3-R movement:
m2388–2389, district (-1,-1), 12 of 4,301 boxes move. Both pass all money
audit fixtures and every reload. Reports are
`target/checks/qb1-trace-{main,final}.log`. No new default finding is filed.
The ensemble is nevertheless run once because QB1's explicit brief requires
it, and the own-town harness trace does change.

Final named check `./batch-check.sh ScaleCheck ForeignDebtCheck`: exit 0,
both complete harnesses green, maps regenerated, no firm stale findings.
The source diff was reviewed as a stranger's; no relaxed assertions or
unrelated source edits. Orchestrator read the complete source diff through
`a32e4873` and reported no findings (cbs-bus, 2026-11-14 19:28 Halifax).
The later KeptCheck fixture repair is recorded below; model code stays unchanged.

## Why the full ensemble is still required

★ The default city is unchanged, but the ensemble is not. Orchestrator's
2026-11-14 19:48 instruction allowed stopping only if one completed seed
under every policy had identical scorer figures and verdicts to HW1's kept
0.9.104-model run. AUTO2 seed 2 already fails that identity test: both have
2,399 CSV rows, equal through m1055, then at m1056 FX is 3.41398193 on QB1
against 3.42566130 on HW1 and treasury debt 182563.836 against 182657.215.
Population, prices and inflation part in m1057. So all 80 must finish.
Comparison: `target/checks/qb1-ensemble-comparison.json`; main's kept run:
`../codex-HW1/target/ensemble/HW1-bf1-final`, snapshot `a0efd26d`.

The final-model run is `target/ensemble/QB1`, immutable snapshot `35690378`;
the model source remains the reviewed `a32e4873`; KeptCheck does not enter the driver. Three workers were used while
the suite and attribution replay shared this PC. The dispatcher was briefly
paused to start the missing-policy identity proof, then resumed as soon as
AUTO2's difference was established. The extra partial ZERO_SLACK seed 2
proof process was stopped by its own PIDs; its `NO DONE` progress line is
that interrupted proof, and the ordinary runner replaced its partial CSV and completed ZERO_SLACK seed 2
(422.9 seconds) in the same 80-run set. No completed run is discarded.

## KeptCheck's moved debt-picture fixture

The first selected suite finds only two new assertions, both section 64:
PathChart and BuybackLadder redraw in zero of six watched months. The census
found each picture, but both watches start with zero pictures on the page.
This is QB1's moved fixture, not a UI fault to waive: the unchanged main
prefix through section 64 with `quotedBorrowing=false` passes both (one
picture, six redraws and six pictures handed back each). Its only two reds
are the already registered KS1-UI Your rate signature and Borrow Columns
retention lines. Evidence: `target/checks/qb1-kept-main-prefix.log`, exit 2.
The prefix is main's KeptCheck with an early stop after section 64 and
diagnostic prints only; every earlier action and assertion remains.

On the quoted path all six sellPaper sales at m2085 are refused. The census
at m2200 selects an existing InflationLinkedBond due m2212. Other picture
watches consume eighteen months before PathChart, whose watch begins m2218;
Close it now begins m2224. Their captured identity has matured even though
the live book now has replacement paper. The complete unchanged quoted
prefix reproduces exactly those two reds, and captures the city before the
paper sales and before the census. `target/checks/qb1-kept-quoted-capture.log`,
exit 2; state retained in `target/qb1-kept-fixture/`.

★ Each independent debt-picture watch now selects the live book's longest
paper at its own start and asserts that its maturity is beyond the entire
PICTURE_MONTHS window. Its label, page, month hand, six-month watch, 90%
retention law, active redraw, no wasted repaint, quiet second redraw and
fresh-page equality laws all stay intact. The city and financial transactions
do not change. No extra months are played. All other picture kinds are untouched.

The same captured m2200 city replayed with the original section 64 fails
exactly the two original lines (`target/checks/qb1-money-before.log`, exit 2).
With the repair, the same replay passes every section-64 assertion and both
lifetime guards (`target/checks/qb1-money-after.log`, exit 0): one picture
each, all six redraw months, all six pictures handed back, zero wasted
repaints and zero page differences. The full quoted prefix itself is the
before-fix regression fixture; the existing full harness retains it.

The first capture attempt and initial pick's maps/stale tail collided with
an in-place compile, so their class-load errors are discarded, not waived.
The completed diagnostic uses the ensemble's immutable model classes; the
final selected suite ran with source and classes frozen; its accepted result is below. The final
ensemble does not read KeptCheck, and its model snapshot is unchanged.

## Final selected suite

`./batch-check.sh --pick` on final source `7ccd34f3`: **exit 3, accepted**.
It selects 76 of 202 harnesses; the runner reports 82 run, 80 passed and two
registered-red harnesses. No new assertion red remains. Compile, regenerated
maps, tree and Stale pass; zero firm stale findings (186 soft prose findings).
Logs: `target/checks/qb1-final-pick.txt`, `qb1-final-suite.txt` and
`qb1-final-known-reds.txt`. The complete repaired KeptCheck says ALL OK,
including both lifetime guards and every section after 64.

The two registered-red harnesses remain separate: MapCheck's three drawing
laws (the every-30-month summary under DM3, connectivity under BF1-M, surface
under DM3-R), and LongPlaytest's already reproduced twelve-box movement at
m2389 under DM3-R. No new known-red entry or transfer is needed. There are
13 soft timings (SaveSlotCheck one, MapCheck eleven, PlanCheck one) and four
played-path observations (AutoBuildCheck one, MonetaryCheck one, MapCheck two).
Two starts waited for the PC's commit charge to fall below the suite's 75%
bar. These are information; their hard correctness laws retain their verdicts.

At 21:00 Halifax, with my selected suite finished, the PC had about 10 GB
physical and 23 GB virtual memory free and 20 logical processors. The requested runtime increase left only three observed active workers.
The verified dispatcher alone was paused while ZERO_SLACK seeds 6, 7 and 8
finished, reaching 25 completed runs; then that drained dispatcher was ended.
`JOBS=5 RESUME=1 tools/ensemble/ensemble.sh QB1` resumes the same immutable
snapshot with all 25 complete CSVs/times retained and 55 runs left. Five active
workers (ZERO_SLACK 9–13) were verified. No completed simulation is repeated.
The first segment's incomplete score is not a batch verdict; the final score
will replace it after all 80 finish. Other sessions and MemWatch are untouched.

## Published main carried before the dry run

Orchestrator's 21:07 Halifax message says WN5d shipped as 0.9.105, main
`3ca9a8bc`, with the default trace unchanged, and explicitly says QB1's
ensemble stays valid. As instructed, merged that main into QB1 as
`b5f88c1882ce5077d060d7ec9a680f6a3c7ee5fe`. Only the generated harness index
and map README conflicted; regenerated them from the combined source.
`./batch-check.sh --fast` on the merge exits 0 (38-second compile, maps,
zero firm Stale findings). Log: `target/checks/qb1-merge-fast.txt`.

★ Carry the already completed selected check and immutable ensemble model;
the coordinated fresh-clone dry run will check the combined branch in full.
No WN5d source file received a bespoke hunk. Against new origin/main, QB1's
only source differences remain ForeignDebtCheck's comment, KeptCheck's
fixture, LongPlaytest's default and ScaleCheck's month. Published QUEUE,
changelog, gate record and GameVersion enter solely from canonical main.
