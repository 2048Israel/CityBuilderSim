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
