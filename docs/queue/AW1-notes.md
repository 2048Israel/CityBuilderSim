# AW1 — The auto city always builds its works

## Investigation

Slot 10 was read directly from its JSON. It is 0.9.91, m2391, automatic
building off, last pass m1412, nine orders and nine buildings, debt limit
240%, cash-anyway off. Its decision log records on at m1410, off m1411,
on m1412, off m1413. KeptCheck's section 38 performs precisely these four
Build-screen interactions through KeptBench.buildAuto, then restores the
original off setting. The default LongPlaytest starts with automatic
building off and uses its scored advisor. The ensemble copies that founding.
AutoBuilder.pass never switches itself off. The m1412 pass was a UI fixture's
temporary exercise, followed by the fixture's restoration.

The checkout's known-reds file contains no entry naming AW1.

Initial batch-check --fast passed, with zero firm stale findings.
The two later --fast runs also passed with zero firm stale findings.

## Calls and implementation

★ Recommend automatic building on with "Build from cash anyway" for the
test city's founding. Players select automatic building and its cash policy
through the Build controls. Both standalone LongPlaytest and the ensemble
now call the same founding method. The explicit playtest.autobuild=false
property selects the scored advisor for a controlled comparison. Harnesses
borrowing the test player retain their own fixture setup.

The existing AutoBuilder already buys an order's land and pays for both
from cash over the limit with that switch on. AW1 exercises this production
path. Its new fixture retires power and streets, consumes their free ground,
puts debt beyond the limit, supplies cash, and checks that both orders and
the land purchases happen without new borrowing.

The new fixture's first run exposed two missing premises. After retirement
the tiny town's base grid still met its power demand, and its next month's
GDP put its debt below the default limit. The fixture now sets the utility
load to ten times the base nameplate and the limit to zero after the town's
month settles. Its assertion prints the cash, debt ratio, limit, free land
and both services' supply and demand. The isolated section passed all three
assertions with power 7,597/100,000 and streets 400/5,995, zero free land,
cash 1,004,035,514 and debt at 200% against the 0% limit.

★ Recommend leaving the optional surplus-power stimulus off pending a
paired measure of employment, migration, treasury running balance and
service coverage. Jerus's slot-9 recovery combines switching on, cash
permission, land, and a higher debt limit. It supplies evidence for meeting
the city's unmet needs. A stimulus comparison needs a city with spare
power, rich cash and unemployment before either twin orders surplus works.
This batch records the option for that separate measurement.

The allocated-land load repair is queued as AW2 through AW1's ship file.
Its brief and the RT3 policy-test evidence are in AW1.md's allocated-land
follow-up section.

## Before the change

The instrumented original default completed 2,404 audited months with no
findings. Automatic building was off in all 2,404. Months with it on, over
the debt limit and ordering nothing were zero. Any essential service was
under a fifth served in all 2,404. This count includes each basic school,
whose coverage was zero throughout. Childcare was under a fifth for 2,179
months, senior care 2,156, highways 1,799, power 72 and general care 60.
At m2400 power served 67.2%, streets 100%, highways 81.9%, general care
18.7%, childcare 0.53%, senior care 0.92%, elementary 0%, police 31.6%.
The full service snapshots every 120 months are retained in
target/aw1-before-playtest.log. Measurements use BuildAdvice's staffed
supply over current demand, capped at full, with zero demand fully served.

## Automatic default playtest

The standalone run completed 2,404 audited months with no findings and every
reload matching. Automatic building was off for zero months. There were
zero months with no order while on and over the debt limit. It placed
1,467 orders for 3,043 buildings and bought 25 land offers.
At m2400 power, general care, childcare, senior care and each basic school
were fully served. Streets served 37.0%, highways 1.86%, police 56.1%.
Power never fell below 88.5%, general care below 78.9%, childcare below
60.1%, senior care below 53.4%, and police below 21.8%.

Any essential service was under a fifth served in all 2,404 months because
the professional schools include unmet law and business places. Individually,
elementary was under a fifth for 259 months, middle 282, high school 347,
highways 2,037 and streets 140. Highway coverage is worse on this growth
path, despite construction, and the measure makes that visible. The final
report also names payback holds on schools and police. The required debt
permission is the cash setting, exercised separately by section 9.
Full service snapshots and existing findings are retained in
target/checks/aw1-final-playtest.log.


## Final ensemble

The final model snapshot is 4dc30c7f. The complete ensemble ran once against
docs/ensemble/0.9.97.md, with all 80 cities finishing and none short, in
28.5 minutes. An earlier snapshot attempt collided with recompilation and
failed before any city ran. It was restarted after compilation finished.
The score is 24/26 against 0.9.97's 23/26. Every one of the baseline's 23
PASS criteria is still PASS, checked by policy and criterion identity.
A2 and A3 are the two continuing failures. Every monetary audit passed
and all 80 cities had zero LongPlaytest findings. AUTO2 median final
population is 1,538,083 against the baseline's 229,288. Median inflation
from m240 is 2.79% against 2.59%, with 15 of 16 seeds in band in both.

The baseline is committed as docs/ensemble/AW1.md with the next-version
token. Monthly service shares, settings and cumulative counters are retained
in target/ensemble/AW1/*.csv. The monthly counter for no order over the debt
limit includes an active pass that ordered nothing for any reason while
over the limit. It measures co-occurrence. Each row below spans sixteen
seeds. Service cells show median months under a fifth and median final
staffed supply over current demand.

| policy | cities | automatic off, months min–max | debt idle, months min–max | any service under a fifth, months min–max |
|---|---:|---:|---:|---:|
| AUTO2 | 16 | 0–0 | 0–195 | 2399–2399 |
| ZERO_SLACK | 16 | 0–0 | 0–195 | 2039–2039 |
| ZERO_HOT | 16 | 0–0 | 0–1 | 401–401 |
| SHOCK | 16 | 0–0 | 0–195 | 839–839 |
| HIGH | 16 | 0–0 | 0–195 | 719–719 |

Each service cell gives median months below a fifth, followed by median final served share.

| service | AUTO2 | ZERO_SLACK | ZERO_HOT | SHOCK | HIGH |
|---|---:|---:|---:|---:|---:|
| power | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% |
| water | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% |
| streets | 235 / 41.7% | 236 / 25.1% | 0 / 100.0% | 0 / 53.1% | 0 / 100.0% |
| through traffic | 2108 / 6.6% | 1802.5 / 0.9% | 401 / 0.0% | 839 / 0.0% | 701 / 64.0% |
| general care | 0 / 100.0% | 0 / 91.6% | 0 / 100.0% | 0 / 99.7% | 0 / 100.0% |
| childcare | 0 / 89.1% | 0 / 68.8% | 0 / 100.0% | 0 / 97.7% | 0 / 100.0% |
| senior care | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% |
| death care | 21 / 100.0% | 8 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% |
| elementary | 252.5 / 100.0% | 252.5 / 85.2% | 226 / 100.0% | 252.5 / 100.0% | 252.5 / 100.0% |
| middle school | 256.5 / 100.0% | 256.5 / 82.8% | 244 / 100.0% | 256.5 / 97.8% | 256.5 / 100.0% |
| high school | 314.5 / 77.2% | 314.5 / 65.6% | 300.5 / 100.0% | 314.5 / 97.5% | 314.5 / 100.0% |
| college | 360.5 / 100.0% | 360.5 / 100.0% | 400 / 0.0% | 360.5 / 100.0% | 360.5 / 100.0% |
| university | 1787 / 100.0% | 1789 / 100.0% | 400 / 0.0% | 838 / 0.0% | 718 / 0.0% |
| medical school | 1544 / 100.0% | 1544 / 100.0% | 357 / 0.0% | 795 / 0.0% | 675 / 0.0% |
| law school | 2398 / 0.0% | 2038 / 0.0% | 400 / 0.0% | 838 / 0.0% | 718 / 0.0% |
| business school | 2393 / 0.0% | 2033 / 0.0% | 395 / 0.0% | 833 / 0.0% | 713 / 0.0% |
| institute of technology | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% | 0 / 100.0% |
| police | 0 / 54.0% | 0 / 53.0% | 0 / 100.0% | 0 / 65.3% | 0 / 69.3% |

## Final selected checks

batch-check --pick selected 71 harnesses and ran 75 including the timing
dependencies, in 2,075 seconds. Seventy passed. MonetaryCheck,
CentralBankCheck, MapCheck, PlanCheck and KeptCheck had only their recorded
known reds. There were zero NEW red lines and 29 soft timing assertions.
AutoBuildCheck passed every existing section and AW1's three new assertions.
LongPlaytest passed. Maps regenerated and Stale reported zero firm findings.
The full log is target/aw1-pick.log and the suite log target/checks/suite.txt.

The outer PowerShell invocation normalized Bash's nonzero exit to 1.
Rejudging the saved suite through Git Bash with an explicit PowerShell
exit of LASTEXITCODE returned 3, confirming the known-red-only verdict.
The fresh-clone dry-run command propagates that exit explicitly.

## Kept fresh-clone dry run

sh tools/gate.sh AW1 --dry-run finished with exit 0 on 2026-11-12 at
23:28 local time. Its check suite returned 3, with 75 harnesses run in
1,975 seconds, 70 passing and the same five recorded known-red harnesses.
There were 15 soft timing assertions, zero firm Stale findings and zero
unnamed red lines. The complete check phase took 2,166 seconds.

The retained run directory is
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-AW1-builder-f0f3e5f6.
Its ship.log, checks/, checks.complete, branch.commit and main.commit are
kept for the publisher to reuse. The tested branch commit is
f0f3e5f6d33e8ba1a40ccddb20589eecdee889fe and the tested main commit is
4f442ab9cf46c07b71784dea0479459710d52640. The final metadata update records
this verdict and retained run in AW1-notes.md and AW1-ship.md.
