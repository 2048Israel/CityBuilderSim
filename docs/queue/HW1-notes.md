# HW1 — The auto city builds its works when they are needed

Jerus's rule: essential works never wait for their own payback. On
2026-11-13 about 16:15 he raised the done condition: "with automatic building
on, technically, all services and everything the government builds should
be near or around 100% with occasional blips, no?" This includes every
professional school. The run records months below 90%, below 20%, longest
spell below 90%, and the last pass's cause for each deficient month.

## Investigation and calls

The old highway advice explicitly exempts highways from full service until
unserved traffic fills six tenths of one highway. Police are sized to the
crime threshold rather than all required officers. Professional schools
wait for a NEEDS YOU row (half a first school's seats). Schools and police
can be held by their own payback. Essential orders also face the old
twelve-month cap: streets and highways share their site map, so either
kind on site can make the other wait indefinitely.

★ Automatic essential works use the full staffed supply/demand target with
the player's slack. Manual advice retains its listing thresholds. Schools
are eligible at the thirty-percent useful-demand threshold, including the professional schools.
Roads, all schools, police and cells join power, water and care as essential.

★ Essential orders are no longer capped to twelve months of builder output.
They are sized ahead of their projected opening and paid through the normal
cash/debt/land path. If the full order is unaffordable it is reduced; if even
one cannot be paid for, the ranking tries other buildings. No work is bought
without the money for both its order and its land. Cash-anyway and the
player's off default retain their existing settings.

The checkout at c5c7a460 has no known-reds entry naming HW1. PO1 owns
AutoBuildCheck and BuildAdviceCheck; HW1 does not edit either. PO1 has been
mailed about their old assertions that schools must face payback holds.

## Later instructions and valuation

Jerus also said: "its just it shouldnt be measure on payback, it should be
measured also on the losses it prevents no? cause if the economy is being
halted?" His power instruction was "also, too many windfarms when in reality
the coal power plant is more optimal, the auto builder is too short sighted".

He qualified the school target: "careful with colleges and above, cause
technically in a city of 100 poeple they will show 0% coverage, even tho its
not needed", then gave the threshold: "like for example if college has 500
seats, then it 150 people wanting it, it may be realy worth it".

★ The first useful demand is 30% of the smallest building's nameplate,
including every higher school and any other oversized essential work.
The same warranted() predicate governs the automatic target, suggestions
and the playtest's service shares. Higher-school NEEDS YOU listing uses
30% too. Below it the service has no actionable shortage.

★ WorksValue compares twenty years (the existing LIFE_MONTHS), at the real
debt rate and the city's own twenty-year population trend. Annual midpoints
value demand after opening; benefits and running are discounted for actual
construction delay. Power's count now plans twenty years ahead, rather
than the short business planning window. Automatic advice prices land
instead of preferring any count that happens to fit free land.

★ Prevented output is a marginal estimate on each sector's booked value
added (revenue less inputs, power and water), divided by its observed
affected factor. The candidate changes only that factor; the other
shortages remain. No output is invented for a zero observed factor.
Power and water use their delivered shares. Roads use the model's
throughputFor() with each sector's actual traffic mix. General care uses
Health's 18%-to-3% absence curve. Police use crimesAt() and the existing
violent-share and time-off constants. Death care reduces the actual
unburied sickness penalty. Theft is an internal transfer, not newly
created city output. No speculative multiplier prices school qualifications
or the mortality benefits of childcare/senior care.

★ Payroll stays a running cost. The model's staffed wages and payslip tax/
contribution rates return part of that payroll to the treasury; only that
return is credited, rather than counting the whole wage bill twice as a
benefit. Posts, unfilled posts and monthly payroll accompany the sum.
Migration's job draw is not assigned an invented second dollar multiplier.
The current power templates and utility flow have no coal-fuel purchase,
mine-sourcing or pollution charge: these are zero in the comparison,
not invented transactions. Maintenance, water, power draw and wages are
the costs the simulation actually charges. This limitation is reported
rather than claiming that coal mines already supply a plant.

The Build suggestion tooltip exposes the same component sum. Essential
service never faces the payback veto; non-essential payback adds prevented
output to its net return. Saved payback holds retain and redenominate this
new figure (a missing field from an older save is zero).

## Measured baseline

The original model at c5c7a460, instrumented only, completed the default
playtest and money fixtures with exit 0. Its m2400 served shares reproduced
AW1: streets 37.0023%, highways 1.85575%, police 56.1412%.
Highways were under 20% for 2,037 months, streets for 140, police for zero.
The pass counts are highways: AHEAD 820, WAITING/BUILDERS 1,483,
ORDERED/BUILDERS 7, ORDERED/NONE 3, NOTHING 16, HELD/GROUND 75.
Streets: AHEAD 589, ORDERED/BUILDERS 198, WAITING/BUILDERS 1,510,
ORDERED/NONE 32, HELD/GROUND 75. Police: AHEAD 1,865,
HELD/PAYBACK 366, HELD/CASH 34, ORDERED/NONE 2,
ORDERED/BUILDERS 22, WAITING/BUILDERS 89, HELD/GROUND 26.
Log: target/checks/hw1-before.log.

A separate diagnostic snapshot of the same original four source files,
compiled into target/hw1-before-classes, counts its plant orders without
changing that model: 22 Wind Farms and 14 Coal Power Plants. It completed
with exit 0. Its final one-plant comparisons at m2405, evaluated with the
new component sum at that city's own prices, are:

| plant | order | land | running PV | prevented output PV | payroll returned PV | net PV | posts | monthly payroll | lead |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Coal Power Plant | $199.4B | $29.2B | $49.5B | $0 | $2.5B | $275.6B | 68 | $92.0M | 28.3 mo |
| Wind Farm | $4.2B | $25.4B | $2.5B | $0 | $271.3M | $31.9B | 7 | $9.7M | 9.0 mo |
| Hydro Station | $4.1B | $6.3B | $2.0B | $0 | $196.8M | $12.2B | 5 | $7.1M | 9.9 mo |

All those posts are filled. The city is fully powered, so another plant
claims no prevented shortage loss. Coal's net cost per nameplate kW here is
about $984k against wind's $3.94M. Hydro availability still depends on river
slots. These are nominal figures within the baseline city, not a constant
price comparison of two different playtest paths. Log:
target/checks/hw1-before-plants.log. This was a diagnostic default playtest,
not a rerun of the monetary ensemble baseline.

## Verification before the final model snapshot

The new EssentialWorksCheck passes cash-funded highways with and without
free land, a street already on site, schools and police in a deficit, and
positive prevented output under rationing. A saturated grid claims zero
prevented rationing loss. The card exposes land, jobs, payroll and losses.
BuildCardCheck and SaveFileCheck pass. Targeted old-law checks expose
fourteen AutoBuildCheck assertions, three BuildAdviceCheck assertions and
three RoadCheck assertions that HW1 must update. None is filed as unrelated
or waived. The two PO1-owned files remain untouched until PO1 lands.
Log: target/checks/hw1-targeted.log, exit 4. All fast checks so far compile,
regenerate maps and report zero firm Stale findings.

The initial service-target trial removed all old builder WAITING holds but
retained long periods while works were on site. Adding the full valuation
and twenty-year power plan kept power above 99% throughout its trial; all
money checks passed. Water, highways and some schools still had long
shortage spells. These are remaining faults against Jerus's strengthened
bar, not approved blips. No readiness marker is justified by those trials.
Final model diagnostics and the one monetary ensemble are recorded below.
