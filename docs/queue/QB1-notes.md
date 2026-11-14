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

Validation and fixture attribution are in progress.

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
No source changes after that commit.

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
all source remains the reviewed `a32e4873`. Three workers were used while
the suite and attribution replay shared this PC. The dispatcher was briefly
paused to start the missing-policy identity proof, then resumed as soon as
AUTO2's difference was established. The extra partial ZERO_SLACK seed 2
proof process was stopped by its own PIDs; its `NO DONE` progress line is
that interrupted proof, and the ordinary runner will replace its partial
CSV in the single completed 80-run set. No completed run is discarded.
