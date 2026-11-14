# HW1 current verification

The current production model is 3b252292 on merged main e5642145
(0.9.101). All eight affected harnesses and all eight causal controls
pass. Its full default run exits 0 with zero findings. The full pick and
once-only final ensemble are running. The ship record is provisional.

The current service table and financial comparison appear under
“Verified default on the REV-HW1-3 model” below. Earlier readiness, gate,
ensemble and candidate measurements are historical and superseded.
A fresh kept dry run is required for readiness.

# HW1 — The auto city builds its works when they are needed

**Readiness withdrawn for REV-HW1-1, send-back 1 of 3.**
The 03ec72c9 readiness and its verification below are historical.
The ship file was removed and pushed first at ded05d79.
The earlier held candidate below is retained as history; the resumed model
and final verification are recorded under the resumed-completion section.

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

## Final candidate default playtest

Model snapshot 80445b2c completed the standalone default and its monetary
fixtures with exit 0, every reload matching, and no playtest finding.
Log: target/checks/hw1-final-playtest.log. All measured service shares were
100% at m2400. This does not meet the strengthened done condition over the
whole run: highways, death care and cells still have long shortages.

The following comparison applies the SAME 30% useful-demand threshold to
both models. Its baseline is the original four model/driver files from
c5c7a460, with only observational service counters changed in a separate
class directory. That diagnostic completed with exit 0, retaining the
original m2400 model state and plant orders. Log:
target/checks/hw1-before-demand.log. The original raw highway count was
2,037 months under 20%; it is 1,429 once sub-threshold demand is excluded.
The change of denominator is explicit, rather than treating an unneeded
school as a recovered service. Each triplet below is months under 90%,
months under 20%, and longest continuous spell under 90%.
| service | before <90% / <20% / longest <90% | HW1 <90% / <20% / longest <90% | first completion quote near longest spell start |
|---|---:|---:|---:|
| Power | 5 / 0 / 5 | 49 / 0 / 30 | 28.0 |
| Water | 0 / 0 / 0 | 248 / 0 / 41 | 43.0 |
| Streets | 1560 / 140 / 1240 | 43 / 0 / 36 | 23.0 |
| Through traffic | 1626 / 1429 / 1320 | 490 / 30 / 245 | 10.0 |
| General care | 4 / 0 / 4 | 1 / 0 / 1 | 1.0 |
| Childcare | 1094 / 0 / 391 | 8 / 0 / 8 | 9.0 |
| Senior care | 160 / 0 / 28 | 13 / 0 / 4 | 12.0 |
| Death care | 15 / 11 / 6 | 119 / 109 / 109 | 1.0 |
| Elementary | 663 / 90 / 560 | 5 / 5 / 5 | 4.0 |
| Middle school | 721 / 109 / 577 | 3 / 3 / 3 | 2.0 |
| High school | 1396 / 121 / 1097 | 7 / 7 / 7 | 6.0 |
| College | 166 / 86 / 86 | 64 / 16 / 35 | 5.0 |
| University | 242 / 242 / 186 | 173 / 31 / 142 | 147.0 |
| Medical school | 305 / 305 / 305 | 95 / 95 / 95 | 81.0 |
| Law school | 0 / 0 / 0 | 0 / 0 / 0 | n/a |
| Business school | 0 / 0 / 0 | 0 / 0 / 0 | n/a |
| Institute of technology | 0 / 0 / 0 | 0 / 0 / 0 | n/a |
| Police | 1847 / 0 / 1847 | 14 / 12 / 12 | 10.0 |
| Cells | 1 / 0 / 1 | 170 / 170 / 170 | 109.0 |

The last column quotes the first pending building's completion near the
start of the longest spell. It is not the completion of an entire large
stack, and quoted shares can change as other sites enter or staffing moves.
These long spells are not accepted as short blips.

The deficient-month counts by last pass explain their immediate constraints:

- Power: 48 AHEAD (capacity on site), one ordering month. Water: 225 AHEAD,
  four ordering months, nineteen GROUND holds.
- Streets: forty AHEAD and three ordering months. Highways: 459 AHEAD,
  thirty-one ordering months (two reduced for ground).
- General care: one AHEAD. Childcare: eight AHEAD. Senior care: eight AHEAD
  and five ordering months.
- Death care: ten AHEAD, three ordering months (two reduced for ground),
  106 GROUND holds.
- Elementary: one newly warranted month before an offer, one ordering month,
  three AHEAD. Middle: one before an offer, one ordering, one AHEAD.
  High: one before an offer, one ordering, five AHEAD.
- College: one before an offer, two ordering, sixty-one AHEAD. University:
  one before an offer, one ordering, 171 AHEAD. Medical: one before an offer,
  one ordering, ninety-three AHEAD.
- Police: thirteen AHEAD and one ordering month. Cells: 169 AHEAD and one
  ordering month. Law, business and the institute never fell below 90% once
  warranted; below the threshold their service is not short.

AHEAD counts all relevant capacity on site, not capacity already open.
Completing those sites earlier, or supplying a smaller early work while a
large one is pending, would close the long construction gaps. GROUND means
groundFor() could not cover the order: its existing refusal covers either
over-allocation of linked ground or no sufficient bare-ground offers.
That distinction is not retained by Step, so this run does not establish
which land sub-cause applied in each deficient month. The production path
would need that evidence before claiming the land problem repaired.
It must cover a useful order's ground and respect actual cash/debt funding.

Automatic building never switched off. The final run made 725 orders for
5,235 buildings and bought five land offers through the auto-builder.
No order while on and over the debt limit occurred in 58 months.
There were no PAYBACK or WAITING/BUILDERS steps for the measured roads
or police. Total road/police step counts still include 454 highway,
318 street and 436 police GROUND holds while their existing supply was
already at least 90%; those are not deficient-service months.

The actual power orders were 116 Wind Farms and eight Coal Power Plants,
versus twenty-two and fourteen on the original model. Thus the wind concern
is NOT established as solved by this candidate. Each actual order's
component sum is in the final log's "power choice" lines. The final
one-plant figures at m2405, at that run's own nominal prices, are:

| plant | order | land | running PV | prevented output PV | payroll returned PV | net PV | posts | monthly payroll | lead |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Coal Power Plant | $155.2B | $28.6B | $39.6B | $1.2T | $1.9B | -$1.0T | 68 | $71.7M | 25.7 mo |
| Wind Farm | $3.3B | $24.9B | $2.0B | $124.8B | $207.6M | -$94.8B | 7 | $7.4M | 7.1 mo |
| Hydro Station | $3.2B | $6.2B | $1.6B | $69.8B | $151.7M | -$59.0B | 5 | $5.4M | 7.8 mo |

All those posts are filled. These are rounded present values, so the
displayed components need not add exactly at their printed precision.
The automatic ranking still divides net value by lifetime units served.
Whether that density ranking is the right whole-plan comparison, and why
the final path chooses more wind, remain unresolved against Jerus's request.
No extra fuel or pollution transactions were added.

The final isolated EssentialWorksCheck also establishes actual debt above
the zero limit and a scarce-cash order that affords one Wind Farm but not
a second or any other useful plant. It orders that one without new debt.
The first draft over-specified Cut.CASH; the full requested count also
lacked ground, so the truthful cut is GROUND. The assertion now checks the
affordable work, invoice and unchanged debt rather than forcing the wrong
single cause. This harness-only change was compiled into an isolated class
directory, leaving the ensemble's model snapshot unchanged.
Log: target/checks/hw1-extended-fixture.log, exit 0.

## Lanekeeper's requested breakdown (2026-11-13)

The extra diagnostic at target/checks/hw1-diagnostic.log uses the identical
80445b2c model with observational output compiled into a separate directory.
It completed with exit 0. Its service counters and 116/8 plant-order counts
match the final playtest. No balance data changed.

Highways' longest spell is m1313–m1557, 245 months: 223 AHEAD/NONE and
twenty-two ORDERED/NONE. There is NO ground or cash hold in this spell.
The minimum spendable cash is 539,177.505k and linked ground is never
over-allocated. This is construction and continuing demand growth: the
target credits capacity on site while real traffic waits for it to open.
Earlier completed capacity, an earlier small work bridging a long project,
or sufficient construction throughput would close it. Merely removing the
old twelve-month hold does not close this gap.

Death care's longest spell is m2010–m2118, 109 months: 106 HELD/GROUND,
two ORDERED/GROUND and one AHEAD/NONE. There is NO cash hold, and no linked
ground over-allocation. The exact refusal says "no bare ground on offer
to buy for it"; only 123–326 m² are free, against 2,790 m² for one
Crematorium. Minimum spendable cash is 172,000,418.942k.
This is the bare-ground-only purchasing rule, rather than AW2's allocated
land load issue. A sufficient buildable offer available to that rule, or a
policy allowing another buildable parcel when no bare one exists, plus
the actual funeral work, would close it. The two reduced orders do not
erase the 106 months that were refused.

Cells' longest spell is m1606–m1775, 170 months: 169 AHEAD/NONE and one
ORDERED/NONE. There is NO ground or cash hold; linked ground is never
over-allocated and minimum spendable cash is 1,355,760.017k.
It is pending prison capacity counted as available by the target. The
initial first-completion quote is 109 months, shorter than the spell.
The prison needs to open earlier; more completed construction capacity or
prioritising that work would close the gap. It is not a payback refusal.

### Which term makes wind win

The catalogue already favours wind in upfront cost and construction work.
Coal's cashCost is 1,071,816 for 280,000 kW: about $3,827.91/kW at the
catalogue's reference scale. Wind's is 23,240 for 8,100 kW:
$2,869.14/kW. Coal requires 0.42857 construction points/kW, wind 0.22222.
Wind therefore starts about 25% cheaper per nameplate kW and needs about
half the construction work. Coal's land is much better: 7.143 sq ft/kW
against wind's 215.111. Coal has 68 posts, wind seven, but a coal plant's
worth of wind has more total posts: 0.864 posts/MW against coal's 0.243.
The model gives coal no separate job-draw bonus. Its migration system
responds to the aggregate posts, not a saved count of arrivals per plant.

The following is an observed, active-demand comparison at m1862. These
one-plant reads are after that month's pass, with the new power order
removed from the site map; they are not a reconstruction of the exact
earlier land/queue invoice. They use twenty years, annual midpoints,
the real discount rate, the population trend, and the automatic 15% margin.
"Used kW" measures physical shortage relieved with NO reserve in demand;
"ranking served kW" is the existing denominator, which includes reserve.
All monetary figures are that city's own nominal local money.

One-plant figures at observed order month 1862, per average kW of physical shortage relieved over twenty years (same city and nominal prices):

| plant | used kW | ranking served kW | build/kW | running PV/kW | land sqft/kW | land PV/kW | avoided output PV/kW | payroll returned PV/kW | net PV/kW | jobs / unfilled | lead |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Coal Power Plant | 279365.091 | 279360.091 | $57.386k | $6.852k | 7.159 | $5.554k | $5.409M | $270.170 | -$5.339M | 68 / 0.170 | 161.668 mo |
| Wind Farm | 7607.972 | 8102.597 | $44.796k | $13.092k | 229.023 | $177.676k | $7.092M | $1.229k | -$6.858M | 7 / 0.000 | 47.821 mo |
| Hydro Station | 4267.657 | 4538.636 | $76.191k | $18.834k | 102.070 | $79.186k | $7.035M | $1.590k | -$6.863M | 5 / 0.000 | 52.818 mo |

The ranking uses net PV divided by the ranking served-kW column, which includes the service reserve:
- Coal Power Plant: -$5.339M per ranking served kW; monthly payroll $3.482M.
- Wind Farm: -$6.439M per ranking served kW; monthly payroll $350.462k.
- Hydro Station: -$6.453M per ranking served kW; monthly payroll $256.685k.

At the final month, physical used-kW / reserve-inclusive ranking served-kW / avoided output PV:
- Coal Power Plant: 0.000000 / 279980.000000 kW; $23.812T.
- Wind Farm: 0.000000 / 8098.666667 kW; $1.011T.
- Hydro Station: 0.000000 / 4533.333333 kW; $578.721B.


At that active-demand point, wind's avoided-output credit is $7.092M per
physically used kW against coal's $5.409M: the roughly $1.68M advantage
dwarfs wind's roughly $0.17M extra land per used kW. Wind opens in 47.821
months against coal's 161.668. The discounted avoided-output term,
along with cheaper capital and faster construction, beats coal's land and
running-cost advantages in this score. That is the mechanism favouring
wind; the 116-to-eight whole-run count also includes different order sizes,
cash constraints and dates, so it cannot be attributed to one final
one-plant comparison alone. Hydro is included as a template comparison;
the actual advice also requires free river slots.

Workers are shown as posts and unfilled posts: coal has 67.830 forecast
filled posts at this observation, wind seven and hydro five. Those are
not measured new immigrants; the model does not attribute fungible worker
arrivals to one plant. The valuation credits only actual staffed payroll
returned through the payslip rates. Plant coal fuel and pollution cost
NOTHING in today's model. There is no local-mine fuel purchase or pollution
health charge to include. These omissions and the catalogue's relative
capital/work figures are balance questions for Jerus; this batch changes
none of buildings.json.

★ The diagnostic also exposes a valuation error, not a balance decision:
prevented-output credit uses demand padded by slack. At the final state
each one-plant calculation relieves ZERO physical shortage over the
twenty-year projection, yet all claim prevented output against the reserve.
A reserve is not lost work. The higher wind credit per used unit therefore
cannot be accepted as a sound economic comparison. A future repair must
price physical lost output separately from the reserve target, then judge
whole plans consistently; it must not make coal win by changing balance
data to compensate for this error.

The earlier final one-plant table uses the Build card's manual 5% margin;
this extra diagnostic uses the automatic 15% margin. They share WorksValue
but do NOT show the same numeric prediction because their inputs differ.
That discrepancy also remains open against Jerus's card requirement.

## Why T2 lost its three passing rows

Lanekeeper held HW1 for Jerus after the ensemble failed and requested this
account comparison. This is diagnostic work on the unchanged models, not
another scored ensemble or a change to balance data.

The original 0.9.98 CSVs and immutable classes are retained at
../codex-AW1/target/ensemble/AW1 (snapshot 4dc30c7f). The HW1 CSVs and
classes are at target/ensemble/HW1 (80445b2c). For each failed policy I
selected the seed nearest the median matched-seed ratio of HW1 final
population to baseline final population: ZERO_HOT seed 2, SHOCK seed 5,
HIGH seed 5. Both seeds' AUTO2 twins provide the controls.

The ten extra account-only runs use those original class snapshots, with a
driver recording capital, land, revenue, expenses, cash interest, central
bank interest, total treasury liabilities, cumulative automatic spending/
borrowing, tax rates, home capacity and actual migration. All ten finish
with exit 0. Every monthly population, cash, paper debt, order count, index,
GDP, unemployment, hunger and FX value matches the corresponding original
CSV exactly. Source, driver, CSVs and logs are retained in
target/hw1-accounts-src, target/hw1-accounts-driver and target/hw1-accounts.
Validation: target/checks/hw1-account-validate.txt.
Analysis: target/checks/hw1-account-summary.txt,
target/checks/hw1-t2-paths.txt and target/checks/hw1-auto-controls.txt.

A lasting gap below means at least 1% below baseline for twelve consecutive
monthly observations. SHOCK and HIGH are the SAME path through m600.
Their gap starts well before either policy intervention.

| policy / seed | lasting gap starts | baseline → HW1 population then | baseline → HW1 final population | final month |
|---|---:|---:|---:|---:|
| ZERO_HOT / 2 | m78 | 1,827 → 1,797 | 10,863 → 6,655 | 402 |
| SHOCK / 5 | m426 | 7,296 → 7,165 | 72,122 → 28,816 | 840 |
| HIGH / 5 | m426 | 7,296 → 7,165 | 32,096 → 19,878 | 720 |

ZERO_HOT seed 2 is ahead at m66 (1,756 against 1,503), then loses people.
At m77 its net migration is -25.197 against baseline +19.257; at m78
it is -26.294 against +8.080. The immediate population account is migration:
HW1 has 3.834 arrivals and 30.128 departures at m78, baseline 17.003 and
8.923. Unemployment is 9.51% against 2.83%. The model's arrival brake
starts above Migration.HEALTHY_OUT_OF_WORK (6%) and reaches zero at 12%;
the weaker job draw and higher unemployment reduce arrivals and the
decline/drift path increases departures. Home space is not the constraint:
HW1 has capacity 2,914 against 2,392, and a smaller population.

Taxes are ALL ZERO at m78 in both cities: this early loss is not a tax hike.
The cash account is -13.825k against +0.624k. Monthly revenue/expenses are
29.576/42.192k against 19.241/17.672k, with no capital or land invoice that
month. Total treasury liabilities are 846.495k against 732.899k, but cash
interest is LOWER (1.778k against 4.026k). It is the recurring budget and
the labour/migration path at the first persistent loss, not a new lump
construction invoice or higher coupon bill that month.

Through m78, dividing each month's flow by its consumer price index,
HW1 has spent 3,234.914k on capital against 3,044.396k, 81.340k on land
against 110.892k, and 887.534k on all other expenses against 464.683k.
These are CPI-adjusted sums, not GDP-deflator estimates. Recurring expense
nearly doubles while capital rises only about 6%. At m240 the candidate's
automatic spending is still flat and migration -0.974 against +189.336.
It never makes up that early population gap by the short m402 endpoint.
By then it actually holds MORE cash (81,331k against 17,737k), LESS
treasury debt (81,908k against 165,067k), and has bought fewer automatic
works in cumulative money (111,200k against 360,220k). A claim that its
final population is smaller simply because it spent more cash on works
would be false. Its final profit/sales/wage rates are 25%/13%/15%
against 30%/15%/15%; lower end taxes do not restore the missed arrivals.

SHOCK and HIGH seed 5 first develop the lasting deficit at m426, before
their m600 policy change. The same migration account carries it:
HW1 net migration is -10.662 against +49.375, from 32.400 arrivals and
43.062 departures against 49.375 and zero. Job draw is 5,765 against
7,096; unemployment is 8.36% against zero. HW1 has MORE home capacity,
8,658 against 6,922. At this point the government has much less liquidity:
cash is 176.598k against 7,129.769k, and all treasury liabilities
41,674.837k against 12,702.229k. In units of one month's own nominal GDP,
that is cash 0.070 versus 1.235 and debt 16.545 versus 2.199, so this is
not just a different price level.

Through m426, the CPI-adjusted accounts show what the money went to:

| recorded flows m2–m426, k after division by each month's CPI | baseline | HW1 |
|---|---:|---:|
| capital | 16,367.797 | 13,766.131 |
| land | 956.237 | 7,060.727 |
| all recurring expenses (including interest) | 12,359.274 | 56,247.359 |
| cash interest on city paper, included above | 2,386.086 | 16,371.472 |
| central bank interest, included above | 450.820 | 1,177.009 |
| revenue | 26,282.145 | 55,216.433 |

There is LESS capital spending, seven times the land spending and more
than four times recurring expenses, including almost seven times the
cash interest. At m426 paper interest is 154.492k/month against 101.404k,
and central bank interest 67.279k against zero. The public balance-sheet
path has changed, even though automatic works' cumulative invoice is
only 22,424k against 60,535k. It is not valid to attribute this to the
capital invoice alone.

Taxes differ here: HW1 profit/sales/wage rates are 30%/15%/15%, baseline
25%/13%/15%. GovernmentPlayer adjusts profit and sales rates at the
January wealth review; the wage rate is unchanged. The old
getIncomeTaxRate() is merely an alias for profit tax, not a combined
payslip rate, so the diagnostic does not mislabel it as another income
burden. Higher profit and sales rates, a weaker job draw, higher hunger
(13.53% against 6.57%) and the migration brake accompany the constrained
budget. The recorded policy rate is LOWER, 13.61% against 20.95%, at
m426: the pre-policy decline cannot be blamed on the later 15% HIGH dial.

At m600 the candidate already has only 8,066 people against 18,177.
Both then carry the same 30%/15%/15% tax rates. SHOCK ends with
net migration -78.175/month against +120.344 and unemployment 9.27%
against 5.05%, keeping the employment/arrival gap open after the import
shock. Against its OWN AUTO2 twin at m840, baseline SHOCK has 1.134 times
the population, HW1 SHOCK only 0.829 times. That is a worse shock outcome
in this matched example, over and above the smaller city entering m600.

HIGH must be described differently: at m720 the candidate has positive
net migration +85.016 against baseline +73.908 and cash equal to 5.503
months of its GDP against 0.760. Its rates are still 30%/15%/15%, baseline
10%/7%/15%. Yet HW1 HIGH is 1.249 times its OWN AUTO2 twin's population
at that date, compared with baseline HIGH at 0.887 times its own twin.
For this representative seed the cross-version T2 miss is carried into
the short HIGH window; it is not evidence that this high-rate treatment
caused a further population collapse relative to HW1 AUTO2.

AUTO2 has 2,400 months to recover. Seed 2 ends at 3,402,735 against
1,547,823. Seed 5 is still badly behind at m1200 (30,276 against 216,965),
but ends at 1,672,787 against 1,465,948. Across all seeds its median is
2,156,835 against baseline 1,538,083, so AUTO2 T2 passes. The shorter
policies end at m402, m720 and m840 while these missed-growth and policy
interactions still matter. That does not excuse their failed T2 criteria.

★ These matched traces establish the account and migration paths, not a
unique attribution to one changed line. The batch changes demand
eligibility, order size/timing, staffing bills, land purchases and ranking
together, and GovernmentPlayer responds to the changed balance sheet.
A mechanism-isolating repair would need controlled comparisons rather
than declaring either "too much building" or "taxes alone" the cause.
No such model repair was attempted after the instruction to hold.

## Final ensemble and held disposition

The single scored final-model ensemble is target/ensemble/HW1, snapshot
80445b2c, 80 of 80 cities complete, none missing or short, 32.5 minutes.
Its result is committed as docs/ensemble/HW1.md with the next-version
token, as the batch contract requires. It is a FAILED candidate result,
not a new published baseline. The latest published baseline remains
docs/ensemble/0.9.98.md.

The score is 21/26 against baseline 24/26. Comparing policy and criterion
identity retains twenty-one of the twenty-four baseline PASS criteria
(and the separate pairing check). It loses these three T2 rows:

| policy | 0.9.98 median final population (PASS) | HW1 median final population | required minimum | verdict |
|---|---:|---:|---:|---|
| ZERO_HOT | 10,574 | 6,528 | 7,930.50 | FAIL |
| SHOCK | 103,965 | 34,224 | 77,973.75 | FAIL |
| HIGH | 55,503 | 29,300 | 41,627.25 | FAIL |

AUTO2 inflation is 2.78% (12 of 16 seeds in band), against 2.79% (15 of 16).
Every policy has zero money-audit failures, zero unread audits and zero
LongPlaytest findings. A2 and A3 remain failures; no formerly failed
criterion improves to compensate for the three lost PASS rows.
Official score: target/ensemble/HW1/score.md.
Exact comparison: target/checks/hw1-regressions.txt.
The runner exits 0 because all cities finished and scoring succeeded;
that is not a green regression verdict.

The final batch-check --pick selected ALL 197, ran 190 harnesses in 2,353
seconds, 177 passing and thirteen failing. The suite summary lists four
known-red-only harnesses and nine with NEW assertions; the common judge
reports 67 NEW red lines and exits 4. There are sixteen soft timing
assertions, zero firm Stale findings, and maps regenerated.
Log: target/checks/hw1-pick.log; complete sections: target/checks/suite.txt
and target/checks/NAME.log. The launcher's shortened display initially
showed five names; the complete suite summary is authoritative.
These new reds are not waived or filed against unrelated batches.
AutoBuildCheck and BuildAdviceCheck were not edited while owned by PO1.
Additional assertion repair and model repair stop with the held result.

Lanekeeper's 2026-11-13 17:23 instruction is "HW1 is held for Jerus".
The requested plant, spell and T2 account notes are complete above.
No model changes were made after the final ensemble snapshot.
No provisional ship file was pushed, no fresh-clone dry run was attempted,
and there is no kept gate-run directory or pair of dry-run commits.
The publisher's queue, changelog, gate record and GameVersion.VERSION
were not edited. No real ship command or save-slot write was performed.

## Resumed to completion, 2026-11-13

Jerus, about 21:30: to "Finish HW1: fix its reserve error and adapt its
assertions?": "yes". The earlier hold and failed candidate remain historical
evidence. This resume uses EI1's M1/F1-only ensemble ship stops and GG1's
provisional marker. Main at 97eaa608 merged; only generated indexes
conflicted, resolved to main then regenerated. The required initial
--fast passed with zero firm Stale findings (hw1-resume-fast.log).

★ Keep the slack in sizing, remove it entirely from the valuation API.
Physical relief and avoided output use demand without reserve. Rank the
whole candidate order's net present value, not a negative per-unit density.
Store that value on the suggestion for its card; no second margin-dependent
recalculation. Coal's construction balance, fuel and pollution remain
Jerus's calls; buildings.json is unchanged.

## Assertion adaptations on the resumed model

The original towns, building balances, land prices, river slots and
demographic bands are retained. Cash-cover twins fund the expanded remit
as detailed below; debt-law fixtures keep their limits. The targeted run
hw1-resume-targeted2.log passes all seven selected/dependent harnesses,
exit 0; zero firm Stale findings.

The former AutoBuildCheck failures were adapted by assertion identity:

| former line / section | final law or observation | reason |
|---|---|---|
| first order equals card cut to twelve months (2) | same automatic-target card and full essential count | HW1 removes that cap for essentials; helper now uses maintained() and the automatic target |
| fixture ordered payback-held work (3) | essential remit has zero payback-vetoed orders | every kept staffed measure is now essential |
| first school held for payback (5) | the same missing warranted school is ordered, no payback weighed | essential floor supersedes the old revenue test |
| inbox explains that school's payback refusal (5) | no invented payback refusal | no refusal was made |
| fiscal-room twin weighs a paying school (5) | the same twin orders without weighing payback | both deficits and room obey the essential rule |
| first school held cash-only (5) | ordinary permitted-debt funding, no first-kind cash-only veto | essential first orders follow the common funding rule |
| overview offers held school (6) | overview selection is an Observation; the category's same school card stays a hard premise | the unchanged town selects other top-three needs; its school is still orderable |
| card carries held-payback text (6) | no payback text on the fulfilled essential card | the old disagreement was removed |
| card repeats payback figures (6) | no appraisal retained for this essential order | no payback was weighed |
| inbox repeats those figures (6) | inbox contains no invented payback refusal | no refusal exists |
| saved/loaded card repeats hold (6) | save/load preserves absence of the hold | the order still runs through the same scratch-save path |
| room makes next pass order the school (6) | room does not add a veto to the work already on site | it was correctly ordered on the first pass |
| building it orders has no hold line (6) | same law, with the pre-order category card retained | the post-order advice no longer needs to offer an already covered school |
| school is non-essential on a deficit (8) | school is essential and ordered without payback on that same deficit | Jerus extended the essential floor |

AutoBuildCheck's independent hand-written funding/ranking helpers now
use the same full-service target and first-kind exception as production.
The former FW4 requirement that the PARTICULAR refused template return
within six months is a path observation; it returns in eleven on this
model while sixteen other orders occur. A hard law still requires that
restoring the limit resumes orders and the city grows. It is not a waiver
of the debt or funding arithmetic.

BuildAdviceCheck's three lines now assert (1) at most MAX_SUGGESTIONS
warranted orders, rather than exactly three regardless of eligibility;
(2) every omitted need is covered on site, cannot be helped, or falls below
the useful-demand threshold, including the remaining list when fewer than
three are offered; (3) exact whole-plan net value AND informational cost per
physical unit. Its arithmetic recomputes invoice, land, delayed running,
payroll tax return and physical relief independently of the suggestion's
stored fields, at the model's constants.

RoadCheck keeps the busy/quiet traffic and all land-price fixtures. Raw
paved-versus-gravel lifetime expense remains checked. The selected card is
now the least net WHOLE plan; transit may answer a street need. Paving's
informational figure uses physical relief and delayed net value. The test
player must execute the chosen paving OR chosen new-build action and its
exact count; it no longer must choose the pre-HW1 paved/paving names.
The per-candidate comparison checks total plan value within the same
closing tier, not negative value divided by units.

The additional ChildcareCheck reds were the same obsolete density
ranking: its fixed small/medium/large demand cities now compare whole-plan
net present value, and retain the separate physical-unit figure check.
HydroCheck exposed a real sizing bug: a NaN quoted lead (no builders)
propagated into the new twenty-year horizon and erased valid advice.
The horizon now treats a stalled lead as zero, as opening() already did.
The two-slot fixture still orders no more than two and reports its gap;
the hundred-slot fixture keeps its slots and checks the count and honest
closing flag at the NEW long horizon, which need not be closable in a
hundred stations. No hydro staffing or river premise was changed.

## Further resumed checks and fixture calls

The broad run hw1-resume-broad.log completed: LongPlaytest,
PriceLevelCheck, SaveFileCheck, ReadPathCheck, BuildCardCheck and
BuildMenuCheck have no NEW red; zero firm Stale findings. BuildMenuCheck's
four research-lab descriptions are already in main's exact gate record.
IslandLinkCheck, AuctionCheck and two additional MapCheck laws needed
attribution. The following changes retain the cities and causal premises.

| harness / old line | adaptation | reason |
|---|---|---|
| IslandLinkCheck bridge first-pass link | wait for the first actual ground-short order; assert the cue | 30% warranted demand need not arrive in the village's first month |
| bridge on site | unchanged law, reached at that cue | no bridge is required before a work needs ground |
| bridge log and totals | unchanged law at that cue | count and spending remain exact |
| bridge step sentence / null next link | unchanged law at that cue | still requires the correct island and no duplicate advice |
| next ground-short bridge pass | call the pass's link line twice with ground cut while the bridge is on site | the new full order covers the village for the entire 120-month cue window; waiting that window accidentally completed the bridge before section 7 |
| port first-pass link | same actual-cue wait | the first month's demand is no longer guaranteed warranted |
| port on each landmass | unchanged law at that cue | still two terminals, none standing |
| two port log entries | unchanged law at that cue | exact orders retained |
| next ground-short port pass | call the link line twice with ground cut and terminals on site | cause the duplicate-order premise directly, without waiting until the terminals stand |
| debt-zero link hold | wait for actual cue, pin reserve cash before each pass | preserves zero debt, no spendable cash and low linked ground; tests the actual funding hold |
| AuctionCheck fulfilled streets ask | select its waited-order log entry | a later essential streets order in the same pass is now legitimate |
| its bond cash | keep exact funding law on that entry | the former loop selected the last subsequent order instead |
| its waited notice | same waited-order entry and original two-month notice | original ask is still fulfilled after two months |
| cash fills all asks without debt | fund the subsequent essential orders as well | old extra D$1M did not cover the new full remit; original cash-covered asks still borrow nothing more |
| earlier bond cash in streets entry | select the one-month waited entry | later orders must not overwrite the ask entry under test |
| raised paper not sold twice | bound each pass's sales by repriced cost minus cash already raised, plus one granule per ask | the old one-granule TOTAL bound mistook normal repricing (D$5.18k) for duplicate issuance |
| cash restored fills asks | fund the subsequent essential remit too | retain no-new-debt assertion with sufficient cash for all the work it covers |
| general-care ask has money raised | PO1 Observation of cash raised, with hard existence and covered-service premises | whole-plan ranking chooses the smaller Walk-in Clinic; its initial sale is below the minimum issue and raises zero |
| covered care ask withdrawn / cannot unsell | hard withdrawal law uses that saved ask's actual building and tests the truthful raised-or-zero cash sentence | an unsold ask says nothing was borrowed; positive cash still must remain in the treasury |

The corrected IslandLinkCheck run hw1-island-final.log passes.
AuctionCheck passes in hw1-links-auctions.log; its only observation is the
zero-raised general-care ask. No money, duplicate-order, save or land law
was converted to an observation.

## Default playtest after the reserve correction

The final model's default run, target/checks/LongPlaytest.log, passes.
Its 2,404 recorded service months include:

| service | months below 90% | longest consecutive spell | below 20% |
|---|---:|---:|---:|
| Highways / through traffic | 297 | 175 | 7 |
| Death care | 31 | 29 | 15 |
| Cells | 59 | 56 | 59 |

Highway deficient months: 249 AHEAD (capacity already on site), 29 ORDERED,
19 HELD/GROUND. Death care: 3 AHEAD, 1 ORDERED, 27 HELD/GROUND.
Cells: 58 AHEAD, 1 ORDERED. These are information for Jerus under EI1.
The first work's quoted lead near the start of the longest spell is
35 months for highways, 1 for death care, 55 for cells. AHEAD counts
on-site work; it does not claim that today's operating service is adequate.

The automatic builder orders **19 Coal Power Plants and 7 Wind Farms**,
against the held candidate's 8 coal / 116 wind. At m2405 a spare single
coal plant, wind farm and hydro station all claim zero physical relief
and zero prevented output loss. Coal's long construction lead, fuel bill
and pollution are Jerus's recorded balance calls, not errors to tune away;
buildings.json remains untouched.

UI checked by eye from a JavaFX KeptBench rendering of the scratch town,
including its power-short twin (target/hw1-build-screen.png). Reserve is
plainly labelled in the advice sizing line. The tooltip uses the exact
suggestion's stored whole-plan components; the one-work cost tooltip and
automatic valuation use the same reserve-free function. Different counts
may have different total costs, and the displayed reserve is the sizing
input, not an avoided-loss credit.

## Map renderer follow-up DM3-R

★ Keep the two newly reached renderer laws hard and queue their mending
separately; do not convert them to path observations. MapCheck's city and
drawn-raster assertions are untouched. Its changed construction path exposes
the raster discrepancy in the old painter/seam/network code.

The isolated fixture target/hw1-debug/MapCause.java runs only
MapCheck.playedCity() and its raster laws, then dumps each district's
input and plan accounting. At m121 the model has 979 buildings, all 979
drawn, and 52 paved-road plots. District (0,0)'s input is 52 paved plots,
its plan accounts for all 52 and has zero leftover, joins and parted
counts. District (0,-1) has zero road budget and does not surface its
south seam. The raster has only 50.5 paved plots, in two street components,
437 road plots total; the largest component has 385 plots. One other
component has a route across the city's owned ground and is not joined.
The first combined “draws exactly what the city has” line is already
registered to DM3; its missing-surface cause here is different from
DM3's earlier missing buildings.

Attribution: CityMap, DistrictPlan, TilePainter, StreetCurves,
BuildingVisual and MapCheck have zero source diff from main 97eaa608.
For a direct check I compiled CityMap, DistrictPlan, TilePainter and
StreetCurves FROM origin/main into target/hw1-main-map/classes and replayed
the isolated played-city input with those classes ahead of the branch
classes. The two failures, district budgets and raster figures match
exactly. Logs: target/checks/hw1-map-cause.log (branch) and
target/checks/hw1-main-map.log (map code compiled from main). This is a
small attribution fixture, not a baseline suite. No renderer or drawing
law is changed by HW1.

DM3-R's done condition: reproduce this input or reduce it to a causal
seam/curve fixture; find where the painter drops 1.5 plots that the plan
accounts for and why the owned-ground route leaves one street piece
unjoined; fix both without weakening MapCheck's kind-by-kind surface
tolerance or connectivity law. Rerun the causal fixture and MapCheck,
including its existing seam, curve, edge and network fixtures.

## Final resumed ensemble against 0.9.98

The once-only final-model run is target/ensemble/HW1-final, snapshot
d9137605, 80/80 cities complete, no missing or short runs, 20.1 minutes.
The model has not changed since that snapshot; later source changes are
harness adaptations only. docs/ensemble/HW1.md now contains THIS run's
baseline, with the publisher's next-version token, replacing the held
candidate's earlier file. The old run remains in target/ensemble/HW1.

Official score: **20/26** versus baseline **24/26**.
Official ship verdict under EI1: **PASS, 10/10 hard M1/F1 criteria**.
All five policies have zero failed or unread money audits and zero
LongPlaytest findings. Every other changed verdict is information:

| policy / criterion | 0.9.98 | final HW1 | threshold | verdict change |
|---|---|---|---|---|
| AUTO2 A1 inflation | median 2.79%, 15/16 in band | median 2.91%, 11/16 in band | median 2% ±1%, at least 12/16 in band | PASS → FAIL (info) |
| ZERO_HOT T2 final population | 10,574 | 7,810 | at least 7,930.50 | PASS → FAIL (info) |
| SHOCK T2 final population | 103,965 | 32,098 | at least 77,973.75 | PASS → FAIL (info) |
| HIGH T2 final population | 55,503 | 26,698 | at least 41,627.25 | PASS → FAIL (info) |

No other verdict changed. AUTO2 final population median is 1,977,750
versus 1,538,083; 15/16 AUTO2 cities reach 40,000 versus 16/16, still
passing T1's baseline-minus-two threshold. A2 and A3 remain FAIL as on
0.9.98. These readings are not changed or hidden to admit the branch.
The earlier requirement to stop for lost T2 PASS rows was explicitly
superseded by Jerus's completion approval and EI1. Log:
target/checks/hw1-final-ensemble.log; full score with every baseline
figure beside the final figure: target/ensemble/HW1-final/score.md.

Jerus's explicit balance call on resumption includes the coal plant's
**162-month build quote**, its fuel and its pollution. These are retained
as his balance decisions; HW1 does not shorten that lead or edit
buildings.json to force a preferred plant mix. Actual quotes vary with
the city's builders and queue, as the recorded automatic orders show.

## Government allocation follow-up KS1-Gov

★ Retain the hard allocation law and queue this untouched screen's
transient-row retention separately. HW1 does not change GovernmentScreen,
Kept, KeptBench or KeptCheck: their complete diff from main 97eaa608 is
empty. All Government correctness and unchanged-redraw laws still pass.

The full pick reaches the Government Overview at m2343. Its first draw
makes 378 nodes; over 25 months it hands back 375 on average, makes none
in one month, and creates 47 at the most, exceeding the unchanged
tenth-of-first-draw floor (37.8). This is the only KeptCheck failure.

A diagnostic copy in target/hw1-kept-prefix/KeptCheck.java runs the exact
original fixture prefix, stopping after the Overview's 25-month loop
instead of rerunning the full red harness. It records node identities
before each monthly draw and prints the new labels when the floor is
crossed. target/checks/hw1-kept-prefix.log reproduces the exact 47/378
failure at **m2364**: new labels include Arrears paid down (in the list
and bridge), Raised by issuing paper, Principal repaid and Everything
else. At m2365, **39** new nodes include Land sold, Central bank
remittance and Advanced by the central bank (printed). The unchanged
draw immediately after each creates zero nodes. The diagnostic's
controlled exit 0 is not a passed allocation law.

GovernmentScreen.overviewPage's eventful lists and bridgeCard and its treasury steps
draw these optional rows in scopes. They leave when absent, then create
new nodes when present again; simultaneous returning flow rows exceed
the old 17-of-320 example in KeptCheck's comment. This is rendering
allocation on changed financial content, not a new physical valuation
or money-book entry. No allocation floor is weakened or made observed.

KS1-Gov's done condition: retain or reuse the transient Overview list and
bridge row nodes across these m2364/m2365 transitions (or a causal small
fixture with the same transitions), preserving truthful disappearance,
the same page as a fresh draw and zero nodes on an unchanged draw.
Keep the existing 90% retention and tenth-of-first-draw creation laws,
then run the causal fixture and KeptCheck. The ship entry is narrowly
scoped to this exact 47-of-378 line, not other pages' allocation limits.

## Final pick and the post-verdict diagnostic

The final --pick selected ALL 198. AllChecks ran 191 harnesses in
3,183 seconds: 188 passed, three failed (MapCheck, KeptCheck and the
already-known DeathRecordCheck). The eight initially NEW lines are
MapCheck's two traced raster laws, its exit/count and a post-verdict
exception, plus KeptCheck's traced allocation line and its exit/count.
No NEW red in HW1's automatic-building, advice, road, funding, save or
changed model laws remains. Eight timing lines are soft; ten path lines are observations.
Stale has zero firm findings. Log: target/checks/hw1-resume-pick.log.

The extra MapCheck line is exactly:
Exception in thread "Thread-1" java.lang.NullPointerException.
It is printed AFTER “3 FAILED”, with no stack frames, after JavaFX
startup and the final canvas snapshot. MapCheck.main ends by
System.exit(fails == 0 ? 0 : 1), with no Platform.exit; all of that source
and the renderer are identical to main. It is a post-verdict toolkit
shutdown diagnostic; its precise internal cause cannot be proved from
the truncated line. It does not originate in an HW1 model stack.
DM3-R also owns investigating this diagnostic and making that harness
exit cleanly after its JavaFX work. Its ship entry matches only this
exception line in MapCheck, not arbitrary exceptions or other harnesses.

★ Restore only the 566 generated files whose complete contents differ
from the already committed regenerated indexes solely by the overnight
generated-date stamp. The source maps, dials and assertion index bodies
are identical; their semantic updates were already committed. This keeps
an unrelated midnight timestamp sweep out of HW1. No generator or
index content is changed.

The saved run was rejudged against the traced ship entries with
CBS_REJUDGE=1 ./batch-check.sh --judge target/checks/suite.txt:
**exit 3, known red only in three harnesses**. Log:
target/checks/hw1-final-rejudge.log. The first --judge call deliberately
retained the old NEW tags and rejected their changed counts; the
documented CBS_REJUDGE mode is required when the registry changes.
The raw pick's exit 4 is retained in its log; this common-judge result
is the final verdict on that exact completed run.

## Fresh-clone dry run — passed

Run: sh tools/gate.sh HW1 --dry-run.
Directory: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-HW1-i4QT6Xez
(Git Bash: /tmp/cbs-gate-HW1-i4QT6Xez).
Tested branch: 4d66f1ac7bf4cb629a3987c07d03304b4b795f01.
Tested main: 97eaa608246941c14d40df13386154f5f0bed89f.
The launcher merged the branch in its fresh clone, selected ALL 198,
and materialized provisional version 0.9.100. The branch keeps the
publisher-owned files unchanged. Ship log: that directory's ship.log;
launcher log: target/checks/hw1-final-gate.log.

**Launcher exit 0**, 2026-11-14 01:12:52 local. The check stage took
3,265 seconds and returned **exit 3, known red only**. Its 191 harnesses
ran in 3,159 seconds: 188 passed, three failed, exactly DeathRecordCheck,
KeptCheck and MapCheck. There are no NEW hard red lines. Stale has zero
firm findings (186 soft); 17 timing lines are soft, ten path lines are
observations, and three starts waited for the memory ceiling.
The gate's retained checks are in that directory's checks/ folder;
checks/suite.txt has the complete verdict. The final gate log confirms
the tree was restored and refs.before equals refs.after. No real merge,
tag or push of main occurred.

The run retains branch.commit, main.commit, checks.complete and
checks.sha256 for reuse. The ship file names this exact directory.
After the tested revision only HW1-notes.md and HW1-ship.md advance.
The notes are pushed first; removing the provisional marker is the
final ship-only readiness commit, pushed last. The publisher-owned
files and all implementation files remain as tested.

## REV-HW1-1 send-back, 2026-11-14

The orchestrator identified nine faults after readiness. The ship file is
withdrawn while each is mended. Funded withdrawal, service-specific
recovery, monthly duplicate-link coverage, professional eligibility and
card/automatic parity will be hard causal fixtures. The model will bound
pending coverage by its first quoted completion, price filled-post wages,
apply the sickness cap to care benefits, and share automatic sizing with
the cards. Each regression will be checked against a missing or broken
mend and its exact failure recorded.

The final combined model must include GD4 from main before the final pick
and once-only final-model ensemble. GD4's matching Government allocation
entry will transfer from KS1-UI to KS1-Gov with a same-part ship row.
The next ship file will be provisional for a fresh dry run, then pushed
last after the green result.

### Send-back mends and causal checks

★ Pending service now has a fixed, saved first-completion date. Monthly
passes adopt existing sites in older saves. A physically short service
stops counting its overdue pending capacity and can place more work.
Orders placed after expiry receive a fresh quote. The quote includes
preparation and queue shares, and paving is already on the Paved Road
stack. A site may still cover a reserve-only gap while operating service
is sufficient. The ordinary card and automatic path share this same
pending map.

★ Running wages use filled posts throughout BuildCard, BuildAdvice and
WorksValue. Health exposes the same capped sum its monthly update uses,
including outbreak, influenza, hunger, injury, unburied and unhoused
sickness. Care's change to baseline also changes the unhoused cause.
Care, police and death-care benefits use only the resulting work-ratio
improvement. The manual defaults and listing now use the player's
automatic reserve and full-service target, including the same remit.

REV-HW1-1's assertion repairs keep these premises hard.

1. AuctionCheck's general-care ask must have positive raised cash before
   withdrawal. The same town, cap, margin and passes now reach this
   funded premise. Removed the unsold-ask alternative and observation.
   Added exact pass cash and principal identities so a refund or erased
   bond fails even when other orders happen.
2. AutoBuildCheck's restored debt limit must recover General care's own
   order within the existing six months, and operating general-care
   capacity must increase while the town grows. The first refused
   measure is explicitly asserted to be General care. Unrelated orders
   satisfy neither check. With the deadline mend the order returns in
   six months. Before it, the hard six-month check read eleven months.
3. IslandLinkCheck again advances real months with the first bridge or
   terminals pending. Before each pass, street load causes an actual
   shortage beyond all pending street capacity and ground is cut low.
   The existing cue requirement remains one later ground-short pass.
   Counts and monthly-reset link steps must show no duplicate link.
4. EssentialWorksCheck causes eligible medical-school demand on both
   sides of the 30%-of-smallest-school threshold. The upper case has no
   school and must be warranted, in remit, and unmaintained. Removing
   eligibility from this case cannot pass through maintained()'s
   unwarranted shortcut.
5. Card parity now compares BuildAdvice's normal card default against
   AutoBuilder.pass's actual building, count and recorded full-plan
   value. The player's margin is set to 35% so a hard-coded default is
   exposed. The full card order must fit both cash and ground.
6. The overdue fixture starts with physical power short, enough pending
   wind, and a finite first-completion quote within three years. A real
   later road order is prioritised ahead of wind. Normal monthly passes
   must withhold extra power until that fixed date and order afterward,
   including after serialising/restoring AutoBuilder.State. The test
   includes the first pass strictly after the deadline because Game
   runs automatic building before incrementing its month.
7. An independently counted, partially staffed plant must price upkeep
   plus filled-post wages. Payroll and discounted running must use
   that same sum.
8. Founding care remains in the capped-care fixture. It is explicitly
   short and the proposed order covers the population. Independent
   Health monthly updates stay capped before and after, so prevented
   output must be zero. A second uncapped comparison includes unhoused
   sickness and must equal the independent model's change.
9. The card's margin, full-service count and whole-plan value must equal
   those of the actual automatic order, as in item 5.

Focused run target/checks/hw1-sb1-pending-monthly.log compiled and passed
AuctionCheck, AutoBuildCheck and IslandLinkCheck. Its sole failure was the
new deadline test stopping one pass early, corrected at the real monthly
boundary. target/checks/hw1-sb1-essential-final.log then passed every
EssentialWorksCheck assertion. --fast compiled with zero firm Stale
findings, 186 soft.

Negative controls are scratch source/class shadows under ignored
target/hw1-sendback1, leaving production files intact.

- On the pre-mend model, the strengthened funded-withdrawal section fails
  its positive-cash premise and withdrawal law. Log
  target/checks/hw1-sb1-funded-before.log.
- Skipping General care only after the default debt limit is restored
  leaves the original refusal premises intact and fails its own-service
  recovery as never. Log target/checks/hw1-sb1-recovery-mutant.log.
- Retaining the previous month's link steps fails both later monthly
  bridge/terminal assertions. This catches the reset coverage that direct
  same-month link calls lost. Log target/checks/hw1-sb1-link-mutant.log.
- Changing professional eligibility to 50% fails the upper 30% case.
  Log target/checks/hw1-sb1-school-mutant.log.
- Before the model mends, hard filled-post costs, capped care and the
  uncapped unhoused comparison fail, and the card's three actual-path
  parity checks fail. The initial negative-control JVM used a 1.5-GB
  heap and exhausted it drawing the fixture's unnecessarily large
  land endowment. The endowment is reduced from 100,000 to 1,000 km²,
  preserving the hard full-order-fits premise and reducing fixture cost.
  The corrected negative-control log is
  target/checks/hw1-sb1-value-parity-before2.log.
- Without the deadline mend, its saved-state and post-deadline-order
  assertions fail. Log target/checks/hw1-sb1-hard-before-pending.log.

The preliminary pre-GD4 default run is
target/checks/hw1-sb1-premerge-playtest.log, exit 0. Highways' longest
below-90% spell is 84 months, death care 3 and cells 0, against the
withdrawn run's 175, 29 and 56. Its plant orders are 42 wind and one coal.
These preliminary figures will be superseded by the final combined run.
No buildings.json balance values were changed.

### Lead-aware ordering and the later instructions

The orchestrator's 02:28 instruction required diagnosis of the remaining
84-month highway spell, and 02:34 required the same for power.
The snapshot diagnostic target/checks/hw1-sb1-highway-trace.log showed
a six-month AHEAD forecast even when a new highway's first opening was
quoted at 74–93 months. At m2211 a highway was ordered with operating
capacity 4,050, demand 3,402 and its first completion near m2304.
Demand reached 6,416 by m2280 while that capacity still stood at 4,050.

★ The start decision now follows the chosen work's actual order lead.
The full plan is weighed before AHEAD can stop an essential order.
A candidate with no immediate count still participates as its next unit,
so a long lead can trigger an early order and a cheap short-lead work can
wait for its own opening. Sizing converges on the quote of the entire
final order. An initial small count's shorter quote cannot size a much
larger, later-opening order. The default card and automatic pass use the
same procedure. Open asks are reweighed through that procedure too.

★ Twenty years remains the economic comparison horizon. It is no longer
the amount of future capacity bought in the current order. Current
orders meet the demand projected at their whole-order opening plus the
existing planning horizon and the player's reserve. Buying twenty years'
forecast supply at once produced much larger capital orders, fiscal
refusals and worse service in the diagnostic trial. The full twenty-year
cost, prevented output, payroll and tax sum remains in WorksValue.

★ Pending power is phased into the physical-loss comparison as its
construction output can finish units, including site preparation.
Previously an unfinished coal plant counted as producing immediately
throughout an earlier wind plan's comparison and erased that wind's
prevented losses. A faster work can now be ordered when the operating
grid's forecast needs it before the existing plant can open. A new faster
order brings the saved first-completion date forward. It cannot postpone
a still-live promise. Expired capacity also stops covering a forecast
reserve gap before it becomes a physical shortage.

The model deliberately has no weather-driven intermittency.
BuildingManager's wind definition already uses a 40% capacity factor,
8,100 units, just as coal's capacity factor is baked in. Staff fill,
construction and retirement affect delivered output. The orchestrator
agreed at 02:40 to price this model and leave weather and balance data
alone. No buildings.json values changed.

The early-deadline fixture now supplies both population histories with
the same controlled growth and housing room. Its highway is covered
today and by the old six-month forecast, but short at its construction
lead. Normal card and automatic paths must order early. Its final card
projection must match the full order's own quote. Removing repeated
sizing fails that exact invariant while the other premises pass
(target/checks/hw1-sb1-resizing-mutant.log). The pre-mend model fails the
early order (hw1-sb1-anticipate-before.log).

The new power fixture has operating power covered now, projected need
before pending coal opens, and an earlier wind opportunity. It requires
positive prevented output, the actual early wind order and a saved
first-completion date advanced from coal's quote. All pass in
hw1-sb1-forward-power.log. The pre-mend model fails the actual order
with its forecast premise reached (hw1-sb1-forward-power-before.log).
The separate constant-demand, physically short fixture failed both
prevented-loss and early-order assertions before the mends
(hw1-sb1-interim-before2.log). The first old-class attempt referenced a
new helper and failed linkage, so it was replaced by an independent
one-coal completion quote before drawing this conclusion.

Additional BuildAdviceCheck adaptations keep their conditions hard.

- Section 3 counts the complete essential-service target for the chosen
  count and one fewer. The old road utilisation listing threshold differs
  from that target. The quote, staffing and ground identities are retained.
- Section 5 retains equal capital price, equal total posts and the higher
  staffed output of the clone with fewer scarce posts. It now requires
  the least independently recomputed whole-plan sum across alternatives.
  Filled-post payroll makes the less staffed mix cheaper to run, so the
  old unconditional preference for fewer scarce posts is no longer a law.
- Section 8 requires the player's automatic reserve exactly. It retains
  full projected coverage and no smaller count than the old listing rule.
  The selected larger unit's integer rounding can absorb the margin.
  The same city's walk-in-only fallback card supplies the hard premise
  that margin/projection adds at least one building. No observation
  replaces any of these checks.

The young refusal fixture reached a saved 60% debt limit on the new path.
AutoBuilder.restore intentionally migrates that legacy default to 240%,
so the loaded probe could borrow while the original was at its cap.
Its existing pin helper now chooses the next ordinary slider step when
the step would equal that migration sentinel. The same young, care-short,
cash-short, bounded-recovery premise is thereby preserved through load.
A finer-issue-rounding trial did not address the cause and was reverted.

Five focused harnesses pass in
target/checks/hw1-sb1-reserve-deadline-checks.log, exit 0.
LongPlaytest now records the last month and the exact pass outcomes of
each service's longest below-90% spell, so final cash, ground, debt and
pending explanations can be tied to that spell.

Pre-GD4 diagnostic snapshots, superseded by the combined final run

| trial | power minimum | power longest below 90% | highway longest | wind / coal |
|---|---|---|---|---|
| withdrawn ready model | 89.9958% | 1 | 175 | 7 / 19 |
| first deadline, filled-post and cap mends | 75.7802% | 55 | 84 | 42 / 1 |
| earliest-work lead forecast | 81.2094% | 17 | 6 | 108 / 14 |
| chosen lead with twenty years' capacity sized now | 41.0985% | 180 | 84 | 25 / 7 |
| timing pending power, opening-sized orders | 85.6769% | 21 | 27 | 49 / 21 |
| forecast deadline with faster quote advancement | 93.5346% | 0 | 65 | 79 / 10 |

The last trial is target/checks/hw1-sb1-reserve-deadline-playtest.log,
exit 0, with no findings. Its power is full at the end. Its highway's
longest spell ends at m1593; the final combined run will identify its own
spell and exact refusal counts. Its cells' 231-month spell has an initial
272-month first-completion quote. The timing-power trial had two map
round-trip findings at its last save, to investigate if repeated on the
combined model. The separate chosen-lead trial interrupted by compiling
over live target/classes is invalid. All later diagnostic defaults run
immutable class snapshots.

GD4 arrived on main at ef5cfe58, 0.9.100, at 03:18.
The implementation is being merged before the final pick and ensemble.

### Combined-model verification starts

Merged origin/main ef5cfe58 as 9c822096. Only generated indexes conflicted.
They were regenerated from the combined source. Both EssentialWorksCheck
and GoldMiningCheck remain registered. The merged --fast run is exit 0,
target/checks/hw1-sb1-merged-fast.log.

Transferred GD4's exact KeptCheck Government part containing 40 of 384
from KS1-UI to KS1-Gov, with its recorded cause and the same-part ship row.
The older 47-of-378 path is assigned to that same queued fix. Other
KS1-UI faults keep their own entries.

The final interim-power fixture retains two distinct hard conditions in
the same city. Constant physical demand is short and pending coal covers
the whole horizon, so any positive wind benefit comes before coal opens.
Then controlled growth starts from current operating coverage, requiring
an early actual wind order and its earlier saved deadline. The current
model passes and the old model fails both the constant-demand benefit
and early-order laws. Logs are hw1-sb1-final-interim.log and
hw1-sb1-final-interim-before.log. The full pick now runs on this combined
model. The once-only ensemble will follow the completed pick.

## REV-HW1-2, provisional review at 03:38

The provisional review found five further faults. The ensemble has not
started. The obsolete ALL pick was stopped by its own verified sh process
38232 and descendant process IDs. Other check sessions were left running.
Its log is target/checks/hw1-sb1-final-pick.log and is incomplete.

The required mends are a zero-help fresh-water candidate, queue-aware
first-unit and pending-power completion, power benefit windows shorter
than the yearly sample, converged paving order sizing, and a real scratch
save/load with an unbroken twin compared order by order. Each needs its
own causal regression before the replacement pick and once-only final
ensemble. The ship file remains marked provisional.

### REV-HW1-2 mends and isolated controls

All five cases now reach their premises and pass. The named verification
is target/checks/hw1-rev2-named.log, exit 0, six harnesses. Stale reports
zero firm findings and 186 soft. AutoBuildCheck's two existing path
observations remain outside these hard assertions.

1. A zero count is seeded with one candidate only when its second result
   says the target was already satisfied. A {0,0} no-help result is
   discarded. The water fixture starts below the fresh-water cap with
   pending plants that exhaust it. Additional fresh capacity cannot reduce
   the shortage, including its own water draw and the pooled utility staff
   effect. The fresh-only card must be absent and the normal card must
   choose a closing desalination plan.
2. BuildingManager.siteUnitMonths keeps the existing crew weight and asks
   its first-group, priority and overtime quotation machinery for the
   points through the requested unit. An earlier site's whole wait stays
   in front. AutoBuilder's first-completion quote and WorksValue's pending
   power use that same unit quotation. Ordinary unit spacing is cached
   within an evaluation. Rushed unit completion uses the quotation itself.
   The fixture puts enough priority roads before twenty coal plants that
   even the whole yard cannot finish those roads inside the valued
   horizon. The first coal quote must include that delay, and coal must
   erase none of the earlier wind benefit within that horizon.
3. ★ Power benefits use monthly samples across the same twenty-year
   horizon. Current sector output potential and supply components are
   reused within that one evaluation while pending capacity is unchanged.
   An explicitly caused one-to-six-month wind opportunity must have
   positive physical relief and avoided loss.
4. Paving repeats its count against the final whole-order quote, using
   the shared full-service target on the normal card path. Its twenty
   gravel roads have coherent goods traffic and available construction
   output. The projected count must grow, and its quoted opening and
   demand projection must match that final count.
5. The test saves the live city midway through construction, copies its
   complete scratch directory into an independent scratch directory,
   then loads through Game.loadGameSave and DataSave. It compares the
   exact saved deadline and every later automatic order's month, service,
   building, wanted count, placed count and cut against the unbroken run.
   The replay retains ChildcareCheck.town's deliberately unsaved sector
   holds. Both paths use ordinary floating rates. The original and loaded
   traces match, including the twenty-five-wind order after expiry.

The replay is run from the frozen checkpoint after the unbroken run.
The first implementation reused AutoBuildCheck.loaded, whose fixture
pins an external exchange rate, and omitted the town's unsaved sector
holds. That produced twenty-five wind on one path and twenty-six on the
other. Direct load and reinstating the same holds preserve this
fixture's premises. No production save/load change was needed for the
deadline field.

The initial paving setup had inconsistent raw traffic and stream
breakdown, and too many gravel roads for the construction output.
The replacement has twenty existing roads, coherent goods traffic and
thirty depots, with the same growing-order premise checked hard.
The initial water setup compared gross supply despite the plant's own
water use and staff-pool effects. It now asserts the unchanged-or-worse
physical shortage, with utility staffing refreshed from the actual
standing stock before applying the cap.

A diagnostic printf used a floating formatter for an integer construction
output. It threw in the before-month hook, so the diagnostic replay did
not advance and its unbounded loop produced a large partial log.
That diagnostic process tree, rooted at PID 31992, was stopped. The
invalid log retains only its first 8 KiB and an explanation. The actual
test has bounded month loops and hard advance checks. Independent
scratch journals also keep the two replays' recordings distinct.

Each missing-mend control is an ignored source/class shadow. Its premise
passes and its hard result fails, with all other model changes retained.

| control log under target/checks | omitted mend | hard failures |
|---|---|---|
| hw1-rev2-water-mutant.log | accept {0,0} as a closing one-unit plan | fresh-only offer and normal desalination choice |
| hw1-rev2-queue-mutant.log | scale the whole site wait by target points | first coal wait and earlier wind's benefit |
| hw1-rev2-short-mutant.log | annual power midpoint | sub-six-month relief and loss credit |
| hw1-rev2-paving-mutant.log | stop after the preliminary paving quote | final-count opening and projection |
| hw1-rev2-reload-mutant.log | ignore pendingUntil during restore | saved deadline and later order-by-order equality |

Every control exits 1. The final production prefixes pass. The real
reload log is hw1-rev2-reload-true-twin.log. The cap, queue, short-window
and paving cases are also included in the green named run. No assertion
was converted into an observation for these mends. No building balance
data was edited.

### Overview entry-point review

The provisional full pick was stopped before its completed verdict after
finding that the overview's use of the automatic remit could return a
suggestion with no NEEDS YOU row. BuildScreen.suggestionCard requires that
row. ★ Keep the overview's row requirement while retaining the automatic
remit's ordering, full-service target and shared reserve for cards it can
show. Category and automatic advice still cover the complete remit.
The parity fixture's same funded power-short city now also supplies an
empty measured-row list and requires no overview cards. Without the guard,
the power suggestion has a null row and this assertion fails. The stopped
run is target/checks/hw1-rev2-final-pick.log, no completed verdict. Its
verified shell tree rooted at PID 40092 was stopped, leaving other sessions.

The overview guard's production run, hw1-rev2-overview-named.log, exits 0.
The isolated ff2a8ac4 BuildAdvice shadow fails only the new empty-row-list
assertion while the funded exact-order/value parity still passes. Its log
is hw1-rev2-overview-mutant.log, expected exit 1.

The final source wording pass also replaces a retained payback tooltip's
old per-unit ranking description with the actual whole-plan ranking, and
corrects lifePerServed's informational-method comment. These are wording
changes. The final pick's compiled model is d5e18010, and the fresh-clone
gate will compile and check this wording with the final records.

## Combined diagnostic default after both reviews

This supersedes the withdrawn readiness and every preliminary trial above.
The d5e18010 model includes GD4 main ef5cfe58. Its immutable default log
is target/checks/hw1-rev2-final-default.log, copied from the final pick.
All 2,404 service months had automatic building enabled. The default and
its audit/reload fixtures pass, with no LongPlaytest findings.

| service | below 90% months | longest below 90% | last month of longest | below 20% months | first-unit quote near spell start |
|---|---:|---:|---:|---:|---:|
| Power | 8 | 8 | 1569 | 0 | 19.0 |
| Water | 172 | 144 | 1925 | 0 | 227.0 |
| Streets | 52 | 39 | 58 | 0 | 38.0 |
| Through traffic | 24 | 13 | 1598 | 7 | 23.0 |
| General care | 29 | 27 | 1658 | 0 | 22.0 |
| Childcare | 0 | 0 | — | 0 | — |
| Senior care | 17 | 10 | 903 | 0 | NaN |
| Death care | 2 | 1 | 715 | 1 | NaN |
| Elementary | 61 | 43 | 367 | 61 | 12.0 |
| Middle school | 13 | 13 | 370 | 13 | 15.0 |
| High school | 22 | 22 | 391 | 22 | 25.0 |
| College | 26 | 26 | 436 | 26 | 29.0 |
| University | 51 | 44 | 1621 | 44 | 28.0 |
| Medical school | 108 | 108 | 1743 | 59 | 57.0 |
| Law school | 0 | 0 | — | 0 | — |
| Business school | 0 | 0 | — | 0 | — |
| Institute of technology | 0 | 0 | — | 0 | — |
| Police | 4 | 4 | 616 | 4 | 3.0 |
| Cells | 161 | 161 | 2054 | 161 | 167.0 |

Quotes are months to the first completed unit, measured near the spell
start. NaN means no finite first-unit quotation was recorded there.
The minimum physical service is 67.9202% for power and 0% for through
traffic. Highway longest is now 13 months, against 23 quoted. Death care
has two deficient months in separate one-month spells. Cells remain
unserved for 161 months, against a 167-month first-unit quote.

The longest-spell outcome counts, preserved exactly from the run, are

```text
...pass outcomes during that longest spell: {Streets={AHEAD/NONE=36, ORDERED/NONE=2, ORDERED/DEBT=1}, Elementary={not offered=1, HELD/CASH=29, HELD/DEBT=1, ORDERED/NONE=1, AHEAD/NONE=11}, Middle school={not offered=1, ORDERED/NONE=1, AHEAD/NONE=11}, High school={not offered=1, ORDERED/NONE=1, AHEAD/NONE=20}, College={not offered=1, ORDERED/NONE=1, AHEAD/NONE=24}, Police={AHEAD/NONE=3, ORDERED/NONE=1}, Through traffic={AHEAD/NONE=12, ORDERED/NONE=1}, Death care={AHEAD/NONE=1}, Senior care={AHEAD/NONE=1, HELD/CASH=9}, Water={AHEAD/NONE=141, ORDERED/NONE=3}, Power={AHEAD/NONE=4, ORDERED/NONE=4}, University={not offered=1, ORDERED/NONE=2, AHEAD/NONE=41}, General care={AHEAD/NONE=25, ORDERED/NONE=2}, Medical school={not offered=1, ORDERED/NONE=8, AHEAD/NONE=99}, Cells={AHEAD/NONE=160, ORDERED/NONE=1}}
```

The automatic orders include 65 Wind Farms and 9 Coal Power Plants.
The withdrawn candidate had 7 wind and 19 coal. Those counts describe
different simulated paths. The final horizon uses timed commissioning
and physical monthly relief, filled-post wages, capped health effects
and the shared reserve sizing rule. No balance data was changed.

Representative actual-order component sums from this final run are

```text
power choice m589 1 Wind Farm: 20-year present value: order $8.0M + land $4.3M + running $2.7M - prevented output loss $234.3M - payroll returned $226,387 = -$219.4M. Physical relief 1733.9/month; 7 posts (0 unfilled); payroll $8,491/month; opens 12.1 months.
```
```text
power choice m1563 1 Coal Power Plant: 20-year present value: order $6.3B + land $867.5M + running $1.0B - prevented output loss $138.9B - payroll returned $30.7M = -$130.8B. Physical relief 108878.2/month; 68 posts (2 unfilled); payroll $1.3M/month; opens 16.6 months.
```
```text
power choice m1566 1 Coal Power Plant: 20-year present value: order $6.9B + land $1.0B + running $957.1M - prevented output loss $0.00 - payroll returned $29.2M = $8.8B. Physical relief 0.0/month; 68 posts (1 unfilled); payroll $1.3M/month; opens 39.0 months.
```
```text
...power comparison at m2405, one Coal Power Plant: 20-year present value: order $108.3B + land $13.8B + running $20.7B - prevented output loss $7.9B - payroll returned $944.9M = $134.0B. Physical relief 100.0/month; 68 posts (0 unfilled); payroll $36.2M/month; opens 40.7 months.
```
```text
...power comparison at m2405, one Wind Farm: 20-year present value: order $2.3B + land $12.0B + running $1.0B - prevented output loss $0.00 - payroll returned $102.1M = $15.2B. Physical relief 0.0/month; 7 posts (0 unfilled); payroll $3.7M/month; opens 10.6 months.
```
```text
...power comparison at m2405, one Hydro Station: 20-year present value: order $2.2B + land $3.0B + running $801.5M - prevented output loss $0.00 - payroll returned $73.6M = $5.9B. Physical relief 0.0/month; 5 posts (0 unfilled); payroll $2.7M/month; opens 14.6 months.
```

## Additional service timing review and SX1 merge

The 05:43 instruction requires a timing trace for medical school,
university, general care and elementary before the ensemble. Power's
minimum also needs its cause. The default above supplies the diagnostic
path. The incomplete ALL run in hw1-rev2-final-pick2.log was stopped by
its own verified script process 33148 and descendants after its launcher
shell was ended. Its default is retained, and it supplies no final suite
verdict. No ensemble has started on the reviewed model. SX1 main e5642145
will be merged before the replacement final pick and ensemble.

### Timing investigation requested at 05:43

The bounded read-only replay is target/checks/hw1-service-timing.log.
It runs the immutable d5e18010 classes and stops at m1761. PRE is before
the automatic pass, POST follows the completed month. Saved deadlines,
operating supply/demand, pending quantities and actual order steps are
printed. Its exit 0 means the diagnostic reached its bound.

* Elementary first becomes useful at m325. Passes m325–353 hold for cash,
  m354 holds for debt, and m355 funds one school. It opens at m368.
  The 43-month deficient spell includes the 29 cash and one debt holds.
* University is useful at m1578 and ordered on that pass. Its fixed
  completion promise is m1608. The changing shared queue pushes its live
  quote beyond that date. At expiry, pass m1609 orders a second university
  and the first opens at m1622. There is no funding hold in this spell.
* Medical school is useful at m1636 and ordered on that pass. The fixed
  promise is m1694. Additional funded orders occur at m1651, m1663, m1676,
  m1693, m1696, m1720 and m1725. The first school opens at m1696 and its
  staffed places cover about 43% of demand. Full service follows later
  units. Every expired promise triggers another order. There is no cash,
  debt or ground hold in that spell.
* General care orders a hospital at m1585 while operating coverage is
  119%, with a promise of m1650. Coverage falls below 90% at m1632.
  Pending hospital capacity then suppresses faster clinics until expiry.
  This is an automatic planning fault.
* The power trough is m1562. Demand rises from 110,926 W to 188,193 W
  while supply moves from 128,895 W to 127,821 W. An exact inventory replay,
  target/checks/hw1-service-load.log, identifies the first Large Mini-Mill
  opening with 77,000 W of new draw. Smaller openings account for the rest.
  The population-only power forecast misses that known pending step.

### Further model mends and fixture premises

★ The earlier-work check applies to every essential service. A pending
hospital can no longer suppress a clinic that opens in an earlier month.
Both quotation comparisons use completion months, preventing a fraction
of a month from falsely claiming an earlier monthly service result.

★ The power projection includes electrical demand from already ordered
non-power works at their quoted commissioning dates. WorksValue reuses its
queue-aware completion schedule for these loads. The count and valuation
paths use the same physical demand. The power valuation caches the schedule
within its own evaluation. Current physical service remains read from the
utility books.

Current useful-demand eligibility is retained while the combined replay
is checked. The school trace orders on each first currently-useful pass.
Its delays include shifting construction shares and insufficient staffed
places after the first unit opens. The generic faster-service mend also
changes that queue, so the new default must supply the final measurements.

The first generic-only shadow run is
target/checks/hw1-all-service-lead-playtest.log, exit 0 and zero findings.
It has zero general-care and power deficient months, but longer funding
and highway spells on its changed path. It is a diagnostic trial, before
the completion-month comparison and pending-load mend.

The new care fixture initially forgot the model's base care capacity.
Subtracting that base when choosing its standing clinics causes the
original intended operating shortage. Its hard premise now verifies the
shortage, sufficient pending hospital and an earlier available clinic.
The old power-only guard fails both the offered-card and actual care-order
laws while this premise passes.

The deadline replay forces a fixed operating power shortage before each
pass. With the new model, unrelated service growth could create a larger
future requirement at the priority-delayed 90,000-month construction quote,
so an additional order before the first deadline was legitimate. Its
controlled-load premise now also fixes the BusinessInvestment forecast to
the current population before every pass on both twins. The real monthly
passes, priority stall, full save/load, saved deadline, hard no-early-order
law, own-service late order and complete later-order comparison remain.
No rate pin or save-field substitution is used.

The corrected six-harness run is
target/checks/hw1-service-mends-named2.log, exit 0 with zero firm Stale
findings and 186 soft. The earlier named log has two fixture failures
described above and is superseded. The combined default uses an immutable
classes snapshot at target/hw1-sendback1/service-model-classes.

The final isolated controls retain every other mend and pass their caused
premises. Each deliberately exits 1.

| log under target/checks | omitted mend | hard failures |
|---|---|---|
| hw1-service-care-mutant.log | earlier work only for power | offered interim care and actual own-service order |
| hw1-service-load-mutant.log | pending non-power electrical loads | timed physical demand, anticipatory card and actual power order |
| hw1-service-reload-mutant.log | restoring the saved pending date | exact restored date and later order-by-order equality |

The production named run passes the same three cases. The unchanged hard
no-early-order, past-deadline-order and continuous-shortage assertions also
pass with the fixed-load replay premise.

## School funding investigation requested at 06:16

The exact e00ac829 model replay is
target/checks/hw1-school-funding2.log. It stops at m363. Its PRE rows
are the actual start of each automatic pass, and STEP rows retain every
funded work and every school hold. No automatic order of any service is
placed during passes m194–357. The last earlier order is childcare at
m166. Both schools fund at m358, followed by childcare at m359.

Elementary is held for cash at m194–356 and debt at m357. Middle school
is held for cash at m278–356 and debt at m357. During m194–357, cash is
nonpositive in 156 of 164 passes, spendable cash is nonpositive in 163,
and Debt/GDP exceeds the unchanged 240% limit in 163. The two exceptions
also fail the existing funding rule.

* At m349, cash is $2,287.79k and the existing cash reserve is $196.32k.
  Spendable cash is $2,091.47k, below one elementary school at $2,230.86k
  and one middle school at $2,383.27k. Debt/GDP is 307.46%, so borrowing
  is unavailable.
* At m357, cash is $203.13k and reserve is $528.59k. Debt is $28,076.36k
  against annual GDP $12,572.16k, or 223.32%. Remaining headroom below
  240% is $2,096.83k. Even the elementary invoice is $2,250.01k, and its
  bond also needs to retain the reserve. Its complete required issue
  exceeds the limit. No other service borrows on that pass.

The following 164 completed months, m195–358, cover those held passes.
The budget and treasury bridge record these nominal local-thousand totals.

| flow | amount |
|---|---:|
| Cash budget surplus | -6,835.69 |
| Paper raised | 8,102.39 |
| Principal repaid | 6,144.73 |
| Budget revenue | 88,303.58 |
| Budget expenses | 95,946.10 |
| Cash interest | 7,133.80 |
| Advanced by the central bank (printed) | 15,142.61 |
| Repaid the central bank | -9,849.55 |
| Bought reserves | -0.21 |

The bridge attributes the funding gap to the existing fiscal cash deficit
and debt service, with central-bank advances and paper issuance financing
it. The automatic priority order diverts no money during the school holds.
★ Keep the author’s borrowing limit, cash reserve, eligibility and balance
data. Their effect is recorded here for Jerus.

Each held-pass opening follows. Cash, spendable cash, debt and annual GDP
are nominal local thousands. The debt limit is 240% on every row.

| pass month | cash | spendable | debt | annual GDP | Debt/GDP | limit |
|---|---:|---:|---:|---:|---:|---:|
| 194 | -147.56 | -147.56 | 20,188.58 | 7,930.99 | 254.55% | 240% |
| 195 | -148.73 | -148.73 | 20,324.93 | 7,977.08 | 254.79% | 240% |
| 196 | -151.60 | -151.60 | 20,467.01 | 8,023.21 | 255.10% | 240% |
| 197 | -157.87 | -157.87 | 20,626.66 | 8,070.48 | 255.58% | 240% |
| 198 | -157.11 | -157.11 | 20,786.61 | 8,117.30 | 256.08% | 240% |
| 199 | -151.94 | -151.94 | 20,929.78 | 8,163.81 | 256.37% | 240% |
| 200 | -152.88 | -152.88 | 21,075.65 | 8,210.64 | 256.69% | 240% |
| 201 | -152.94 | -152.94 | 21,221.65 | 8,256.97 | 257.02% | 240% |
| 202 | -152.48 | -152.48 | 21,364.88 | 8,302.42 | 257.33% | 240% |
| 203 | -153.60 | -153.60 | 21,510.09 | 8,349.60 | 257.62% | 240% |
| 204 | -153.82 | -153.82 | 21,656.53 | 8,395.26 | 257.96% | 240% |
| 205 | -91.65 | -91.65 | 21,741.03 | 8,442.07 | 257.53% | 240% |
| 206 | -76.97 | -76.97 | 21,808.95 | 8,464.50 | 257.65% | 240% |
| 207 | -84.67 | -84.67 | 21,886.38 | 8,482.06 | 258.03% | 240% |
| 208 | -81.07 | -81.07 | 21,954.07 | 8,496.46 | 258.39% | 240% |
| 209 | -78.22 | -78.22 | 22,010.35 | 8,508.22 | 258.70% | 240% |
| 210 | -65.64 | -65.64 | 22,029.28 | 8,517.29 | 258.64% | 240% |
| 211 | -83.28 | -83.28 | 22,090.11 | 8,524.58 | 259.13% | 240% |
| 212 | -85.95 | -85.95 | 22,154.96 | 8,530.67 | 259.71% | 240% |
| 213 | -84.51 | -84.51 | 22,217.16 | 8,534.99 | 260.31% | 240% |
| 214 | -87.41 | -87.41 | 22,283.34 | 8,537.31 | 261.01% | 240% |
| 215 | -90.48 | -90.48 | 22,356.41 | 8,536.37 | 261.90% | 240% |
| 216 | -90.25 | -90.25 | 22,425.64 | 8,535.00 | 262.75% | 240% |
| 217 | -99.36 | -99.36 | 22,517.26 | 8,530.50 | 263.96% | 240% |
| 218 | -83.41 | -83.41 | 22,568.40 | 8,505.34 | 265.34% | 240% |
| 219 | -90.09 | -90.09 | 22,634.53 | 8,482.35 | 266.84% | 240% |
| 220 | -91.92 | -91.92 | 22,702.52 | 8,460.92 | 268.32% | 240% |
| 221 | -88.41 | -88.41 | 22,761.95 | 8,438.85 | 269.73% | 240% |
| 222 | -92.25 | -92.25 | 22,828.53 | 8,419.35 | 271.14% | 240% |
| 223 | -92.88 | -92.88 | 22,894.76 | 8,400.36 | 272.55% | 240% |
| 224 | -93.41 | -93.41 | 22,963.37 | 8,381.67 | 273.97% | 240% |
| 225 | -94.34 | -94.34 | 23,031.34 | 8,360.73 | 275.47% | 240% |
| 226 | -94.44 | -94.44 | 23,099.94 | 8,340.31 | 276.97% | 240% |
| 227 | -99.47 | -99.47 | 23,174.40 | 8,299.58 | 279.22% | 240% |
| 228 | -101.84 | -101.84 | 23,254.10 | 8,252.44 | 281.78% | 240% |
| 229 | -104.70 | -104.70 | 23,337.21 | 8,218.88 | 283.95% | 240% |
| 230 | -103.68 | -103.68 | 23,417.54 | 8,215.30 | 285.05% | 240% |
| 231 | -102.29 | -102.29 | 23,496.43 | 8,213.76 | 286.06% | 240% |
| 232 | -102.29 | -102.29 | 23,575.57 | 8,212.78 | 287.06% | 240% |
| 233 | -101.64 | -101.64 | 23,652.92 | 8,212.02 | 288.03% | 240% |
| 234 | -102.11 | -102.11 | 23,730.85 | 8,210.42 | 289.03% | 240% |
| 235 | -102.90 | -102.90 | 23,809.13 | 8,208.86 | 290.04% | 240% |
| 236 | -102.64 | -102.64 | 23,886.77 | 8,206.26 | 291.08% | 240% |
| 237 | -103.58 | -103.58 | 23,965.14 | 8,205.98 | 292.04% | 240% |
| 238 | -103.37 | -103.37 | 24,043.17 | 8,205.82 | 293.00% | 240% |
| 239 | -104.13 | -104.13 | 24,121.53 | 8,225.96 | 293.24% | 240% |
| 240 | -103.89 | -103.89 | 24,199.78 | 8,252.12 | 293.26% | 240% |
| 241 | -41.66 | -41.66 | 24,215.80 | 8,264.39 | 293.01% | 240% |
| 242 | -37.42 | -37.42 | 24,227.20 | 8,299.85 | 291.90% | 240% |
| 243 | -73.03 | -73.03 | 24,270.28 | 8,333.53 | 291.24% | 240% |
| 244 | -76.91 | -76.91 | 24,315.45 | 8,339.60 | 291.57% | 240% |
| 245 | -76.34 | -76.34 | 24,360.56 | 8,347.71 | 291.82% | 240% |
| 246 | -75.84 | -75.84 | 24,404.36 | 8,355.16 | 292.09% | 240% |
| 247 | -76.62 | -76.62 | 24,448.92 | 8,362.87 | 292.35% | 240% |
| 248 | -75.20 | -75.20 | 24,491.45 | 8,370.55 | 292.59% | 240% |
| 249 | -74.90 | -74.90 | 24,532.36 | 8,377.91 | 292.82% | 240% |
| 250 | -74.82 | -74.82 | 24,489.59 | 8,385.45 | 292.05% | 240% |
| 251 | -74.40 | -74.40 | 24,527.17 | 8,393.26 | 292.22% | 240% |
| 252 | -74.00 | -74.00 | 24,565.33 | 8,401.19 | 292.40% | 240% |
| 253 | -32.76 | -32.76 | 24,561.87 | 8,408.98 | 292.09% | 240% |
| 254 | -30.97 | -30.97 | 24,555.84 | 8,388.88 | 292.72% | 240% |
| 255 | -71.70 | -71.70 | 24,589.80 | 8,372.68 | 293.69% | 240% |
| 256 | -76.84 | -76.84 | 24,628.62 | 8,360.29 | 294.59% | 240% |
| 257 | -76.45 | -76.45 | 24,667.28 | 8,349.42 | 295.44% | 240% |
| 258 | -73.74 | -73.74 | 24,702.71 | 8,339.60 | 296.21% | 240% |
| 259 | -74.05 | -74.05 | 24,738.70 | 8,329.95 | 296.99% | 240% |
| 260 | -73.05 | -73.05 | 24,773.31 | 8,321.35 | 297.71% | 240% |
| 261 | -72.71 | -72.71 | 24,807.33 | 8,313.58 | 298.40% | 240% |
| 262 | -72.93 | -72.93 | 24,842.12 | 8,307.04 | 299.05% | 240% |
| 263 | -72.97 | -72.97 | 24,876.27 | 8,302.08 | 299.64% | 240% |
| 264 | -72.46 | -72.46 | 24,910.61 | 8,298.22 | 300.19% | 240% |
| 265 | -71.37 | -71.37 | 24,943.18 | 8,296.78 | 300.64% | 240% |
| 266 | -76.17 | -76.17 | 24,982.07 | 8,274.42 | 301.92% | 240% |
| 267 | -79.12 | -79.12 | 25,024.61 | 8,240.52 | 303.68% | 240% |
| 268 | -33.65 | -33.65 | 25,023.44 | 8,238.09 | 303.75% | 240% |
| 269 | -28.54 | -28.54 | 25,016.70 | 8,266.15 | 302.64% | 240% |
| 270 | -72.17 | -72.17 | 25,054.24 | 8,298.64 | 301.91% | 240% |
| 271 | -74.88 | -74.88 | 25,095.03 | 8,306.72 | 302.11% | 240% |
| 272 | -73.43 | -73.43 | 25,135.05 | 8,316.70 | 302.22% | 240% |
| 273 | -73.84 | -73.84 | 25,176.55 | 8,329.75 | 302.25% | 240% |
| 274 | -72.53 | -72.53 | 25,217.41 | 8,345.27 | 302.18% | 240% |
| 275 | -70.89 | -70.89 | 25,256.46 | 8,360.69 | 302.09% | 240% |
| 276 | -1,211.30 | -1,211.30 | 25,316.18 | 8,379.34 | 302.13% | 240% |
| 277 | -74.22 | -74.22 | 25,344.56 | 8,399.84 | 301.73% | 240% |
| 278 | -68.33 | -68.33 | 25,397.61 | 8,439.32 | 300.94% | 240% |
| 279 | -69.70 | -69.70 | 25,438.49 | 8,485.36 | 299.79% | 240% |
| 280 | -70.75 | -70.75 | 25,480.76 | 8,527.71 | 298.80% | 240% |
| 281 | -68.62 | -68.62 | 25,521.22 | 8,539.92 | 298.85% | 240% |
| 282 | -68.35 | -68.35 | 25,562.93 | 8,549.66 | 298.99% | 240% |
| 283 | -66.01 | -66.01 | 25,601.71 | 8,583.75 | 298.26% | 240% |
| 284 | -67.27 | -67.27 | 25,642.88 | 8,619.43 | 297.50% | 240% |
| 285 | -65.73 | -65.73 | 25,682.26 | 8,656.09 | 296.70% | 240% |
| 286 | -64.19 | -64.19 | 25,722.98 | 8,692.36 | 295.93% | 240% |
| 287 | -64.93 | -64.93 | 25,765.85 | 8,729.76 | 295.15% | 240% |
| 288 | -64.00 | -64.00 | 25,808.13 | 8,765.46 | 294.43% | 240% |
| 289 | -63.67 | -63.67 | 25,851.15 | 8,802.06 | 293.69% | 240% |
| 290 | -57.10 | -57.10 | 25,886.59 | 8,857.48 | 292.26% | 240% |
| 291 | -53.86 | -53.86 | 25,919.97 | 8,921.47 | 290.53% | 240% |
| 292 | -52.03 | -52.03 | 25,951.66 | 8,986.21 | 288.79% | 240% |
| 293 | -50.41 | -50.41 | 25,981.55 | 9,049.07 | 287.12% | 240% |
| 294 | -53.51 | -53.51 | 26,015.91 | 9,110.02 | 285.57% | 240% |
| 295 | -18.86 | -18.86 | 26,030.96 | 9,172.71 | 283.79% | 240% |
| 296 | -15.59 | -15.59 | 26,047.25 | 9,263.66 | 281.18% | 240% |
| 297 | -14.84 | -14.84 | 26,062.35 | 9,351.57 | 278.70% | 240% |
| 298 | -60.44 | -60.44 | 26,118.42 | 9,438.99 | 276.71% | 240% |
| 299 | -71.35 | -71.35 | 26,189.92 | 9,496.61 | 275.78% | 240% |
| 300 | -70.79 | -70.79 | 26,261.55 | 9,555.08 | 274.84% | 240% |
| 301 | -72.75 | -72.75 | 26,335.30 | 9,605.71 | 274.16% | 240% |
| 302 | -71.31 | -71.31 | 26,407.61 | 9,637.87 | 274.00% | 240% |
| 303 | -70.87 | -70.87 | 26,479.51 | 9,663.47 | 274.02% | 240% |
| 304 | -60.98 | -60.98 | 26,531.23 | 9,685.24 | 273.93% | 240% |
| 305 | -67.20 | -67.20 | 26,599.28 | 9,701.54 | 274.18% | 240% |
| 306 | -56.93 | -56.93 | 26,643.89 | 9,711.37 | 274.36% | 240% |
| 307 | -73.73 | -73.73 | 26,718.38 | 9,718.29 | 274.93% | 240% |
| 308 | -79.34 | -79.34 | 26,790.42 | 9,668.39 | 277.09% | 240% |
| 309 | -76.37 | -76.37 | 26,856.69 | 9,621.21 | 279.14% | 240% |
| 310 | -72.58 | -72.58 | 26,917.93 | 9,575.50 | 281.11% | 240% |
| 311 | -63.68 | -63.68 | 26,962.51 | 9,554.64 | 282.19% | 240% |
| 312 | -43.19 | -43.19 | 26,964.31 | 9,531.26 | 282.90% | 240% |
| 313 | -47.67 | -47.67 | 26,974.22 | 9,512.96 | 283.55% | 240% |
| 314 | -8.41 | -8.41 | 26,907.23 | 9,513.67 | 282.83% | 240% |
| 315 | -20.70 | -20.70 | 26,866.32 | 9,510.83 | 282.48% | 240% |
| 316 | -42.12 | -42.12 | 26,866.25 | 9,505.81 | 282.63% | 240% |
| 317 | -5,075.67 | -5,075.67 | 26,962.85 | 9,503.76 | 283.71% | 240% |
| 318 | -56.87 | -56.87 | 26,926.19 | 9,506.38 | 283.24% | 240% |
| 319 | -47.34 | -47.34 | 27,016.55 | 9,509.76 | 284.09% | 240% |
| 320 | -62.43 | -62.43 | 27,069.57 | 9,536.54 | 283.85% | 240% |
| 321 | -63.75 | -63.75 | 27,123.29 | 9,558.87 | 283.75% | 240% |
| 322 | 130.35 | 0.00 | 26,904.55 | 9,576.66 | 280.94% | 240% |
| 323 | -62.83 | -62.83 | 26,820.33 | 9,596.33 | 279.49% | 240% |
| 324 | -64.07 | -64.07 | 26,867.38 | 9,614.97 | 279.43% | 240% |
| 325 | -64.34 | -64.34 | 26,916.96 | 9,629.75 | 279.52% | 240% |
| 326 | -65.19 | -65.19 | 26,935.73 | 9,586.87 | 280.96% | 240% |
| 327 | -92.90 | -92.90 | 27,015.47 | 9,553.37 | 282.78% | 240% |
| 328 | -95.57 | -95.57 | 27,101.75 | 9,521.29 | 284.64% | 240% |
| 329 | -90.92 | -90.92 | 27,177.27 | 9,491.40 | 286.34% | 240% |
| 330 | -73.98 | -73.98 | 27,212.93 | 9,459.65 | 287.67% | 240% |
| 331 | -90.35 | -90.35 | 27,286.17 | 9,428.03 | 289.42% | 240% |
| 332 | -80.97 | -80.97 | 27,336.37 | 9,396.06 | 290.93% | 240% |
| 333 | -86.31 | -86.31 | 27,398.11 | 9,363.92 | 292.59% | 240% |
| 334 | -90.30 | -90.30 | 27,468.02 | 9,331.80 | 294.35% | 240% |
| 335 | -85.20 | -85.20 | 27,524.66 | 9,299.77 | 295.97% | 240% |
| 336 | -88.77 | -88.77 | 27,589.80 | 9,263.16 | 297.84% | 240% |
| 337 | -92.30 | -92.30 | 27,662.02 | 9,226.55 | 299.81% | 240% |
| 338 | -100.82 | -100.82 | 27,763.79 | 9,243.47 | 300.36% | 240% |
| 339 | -89.43 | -89.43 | 27,828.31 | 9,255.24 | 300.68% | 240% |
| 340 | -101.46 | -101.46 | 27,920.93 | 9,263.94 | 301.39% | 240% |
| 341 | -99.91 | -99.91 | 28,007.05 | 9,268.11 | 302.19% | 240% |
| 342 | -103.01 | -103.01 | 28,098.65 | 9,272.27 | 303.04% | 240% |
| 343 | -104.20 | -104.20 | 28,191.24 | 9,275.25 | 303.94% | 240% |
| 344 | -104.54 | -104.54 | 28,282.18 | 9,280.35 | 304.75% | 240% |
| 345 | -106.87 | -106.87 | 28,378.14 | 9,285.19 | 305.63% | 240% |
| 346 | -104.56 | -104.56 | 28,467.06 | 9,290.58 | 306.41% | 240% |
| 347 | -107.08 | -107.08 | 28,560.57 | 9,294.51 | 307.28% | 240% |
| 348 | -107.67 | -107.67 | 28,653.95 | 9,302.99 | 308.01% | 240% |
| 349 | 2,287.79 | 2,091.47 | 28,638.90 | 9,314.74 | 307.46% | 240% |
| 350 | -100.53 | -100.53 | 26,446.37 | 9,311.00 | 284.03% | 240% |
| 351 | 171.09 | 0.00 | 27,108.77 | 9,621.77 | 281.74% | 240% |
| 352 | 153.24 | 0.00 | 27,560.12 | 9,938.43 | 277.31% | 240% |
| 353 | 67.05 | 0.00 | 27,958.07 | 10,085.44 | 277.21% | 240% |
| 354 | -40.23 | -40.23 | 27,979.59 | 10,304.53 | 271.53% | 240% |
| 355 | 80.75 | 0.00 | 28,038.05 | 10,778.59 | 260.13% | 240% |
| 356 | 121.59 | 0.00 | 28,019.78 | 11,525.58 | 243.11% | 240% |
| 357 | 203.13 | 0.00 | 28,076.36 | 12,572.16 | 223.32% | 240% |

### Remaining completion overruns on the mended model

The exact replay is target/checks/hw1-service-final-timing.log, bounded at
m1851. This uses the same immutable model as e00ac829.

* University is first useful at m1716 and ordered on that pass, with a
  fixed promise of m1743. The remaining quote read after that first month
  is 25 months. Shared construction changes leave nine months quoted at
  m1740. Pass m1744 places the additional university after expiry. The
  first opens at m1747. No funding hold or late first order occurs.
* Medical school is first useful at m1752 and ordered on that pass, with a
  fixed promise of m1839. The remaining quote after its first month is 86.
  Pass m1840 adds a school after expiry and the first opens at m1842,
  restoring 97% staffed coverage. There is no funding hold.
* The highway is ordered at m1794, before operating service falls below
  90% at m1800. Its fixed promise is m1810. The first-unit quote measured
  at that shortage's start is ten remaining months. Service recovers at
  m1811, and another highway is ordered. The eleven-month spell includes
  the completed-month sampling boundary.
* Death care first falls short at completed m620 after the pass had read
  adequate service. Pass m620 orders a crematorium with a one-month quote.
  Work and the changing burial backlog leave the three completed months
  m620–622 deficient. Additional ovens restore service at m623. This is
  a short blip.

These actual order dates distinguish a quote overrun from a late order.
The saved promises expire and the additional orders are placed. First-unit
quotes near a spell's start are remaining waits, while its length includes
every completed deficient month. They are retained as information beside
the complete timelines.

## Final model service measurements on SX1 main

The completed e00ac829 model default is
target/checks/hw1-service-projection-playtest.log, exit 0. It supersedes
the d5e18010 diagnostic and the generic-only trial above. Every default
audit and reload passes, with zero findings over 2,404 service months.

| service | below 90% months | longest below 90% | last month of longest | below 20% months | first-unit quote near spell start |
|---|---:|---:|---:|---:|---:|
| Power | 0 | 0 | — | 0 | — |
| Water | 257 | 131 | 2120 | 0 | 230.0 |
| Streets | 16 | 16 | 37 | 0 | 17.0 |
| Through traffic | 23 | 11 | 1810 | 3 | 10.0 |
| General care | 0 | 0 | — | 0 | — |
| Childcare | 0 | 0 | — | 0 | — |
| Senior care | 1 | 1 | 1776 | 0 | NaN |
| Death care | 3 | 3 | 622 | 3 | 1.0 |
| Elementary | 167 | 167 | 360 | 167 | 3.0 |
| Middle school | 84 | 84 | 361 | 84 | 3.0 |
| High school | 6 | 6 | 366 | 6 | 3.0 |
| College | 44 | 23 | 430 | 23 | 23.0 |
| University | 31 | 31 | 1746 | 31 | 25.0 |
| Medical school | 90 | 90 | 1841 | 90 | 86.0 |
| Law school | 0 | 0 | — | 0 | — |
| Business school | 0 | 0 | — | 0 | — |
| Institute of technology | 0 | 0 | — | 0 | — |
| Police | 3 | 3 | 496 | 3 | 1.0 |
| Cells | 220 | 209 | 2312 | 220 | 287.0 |

Power stays at least 100% served throughout. General care has a 95.7824%
minimum and no deficient month. Through traffic has a 0% founding
minimum and three months below a fifth. Its longest below-90% spell
falls from the withdrawn 175 months to eleven. Death care falls from
29 months to three. Cells remain a 209-month build-time spell, within
their first-unit quote of 287. Water is 131 against 230 quoted.
School funding and remaining queue overruns are traced above.

The automatic builder orders 36 wind farms and 8 coal plants. The
withdrawn candidate ordered 7 wind and 19 coal. Their paths differ.
The following actual-order and end-city component sums use the final
physical monthly valuation, with filled-post costs and timed loads.

```text
power choice m420 1 Wind Farm: 20-year present value: order $6.4M + land $219,016 + running $1.2M - prevented output loss $11.7M - payroll returned $88,898 = -$4.0M. Physical relief 218.7/month; 7 posts (1 unfilled); payroll $4,956/month; opens 18.7 months.
```
```text
power choice m1636 1 Coal Power Plant: 20-year present value: order $16.2B + land $1.5B + running $3.2B - prevented output loss $40.0B - payroll returned $127.6M = -$19.1B. Physical relief 5753.9/month; 68 posts (1 unfilled); payroll $4.5M/month; opens 61.9 months.
```
```text
...power comparison at m2405, one Coal Power Plant: 20-year present value: order $1.2T + land $68.6B + running $198.5B - prevented output loss $0.00 - payroll returned $12.7B = $1.5T. Physical relief 0.0/month; 68 posts (0 unfilled); payroll $607.7M/month; opens 145.6 months.
```
```text
...power comparison at m2405, one Wind Farm: 20-year present value: order $24.2B + land $59.7B + running $13.5B - prevented output loss $0.00 - payroll returned $1.6B = $95.9B. Physical relief 0.0/month; 7 posts (0 unfilled); payroll $62.1M/month; opens 48.1 months.
```
```text
...power comparison at m2405, one Hydro Station: 20-year present value: order $24.1B + land $14.9B + running $10.5B - prevented output loss $0.00 - payroll returned $1.1B = $48.3B. Physical relief 0.0/month; 5 posts (0 unfilled); payroll $45.2M/month; opens 52.8 months.
```

## Same-month comparison with main e5642145

The 06:24 request asks for the same default months on exact main.
An isolated git archive of e5642145 was compiled separately. The bounded
read-only replay is target/checks/hw1-main-funding2.log. The matching
HW1 replay is target/checks/hw1-school-funding3.log. Both stop at m363.
This is the specifically requested partial default comparison. The
published ensemble baseline remains the source for ensemble scoring.

Cumulative automatic order, spend and borrowing counters are logged after
every pass, including funded reoffers. They confirm zero HW1 orders in
m194–357 and 23 main orders. Main reaches at least 90% physical elementary
service at m261, and has it in 97 of those 164 months. HW1 reaches it at
m361 and has none in that window.

HW1 takes more early debt and remains fiscally constrained. Its earlier
works contribute to the squeeze. Before m194, its 12 automatic orders
place 14 buildings for $6,544.62k and borrow $5,065.14k. Main places 17
buildings in 13 orders for $4,048.60k and borrows $2,707.95k. HW1’s
m84 light-rail order for street relief is the largest added invoice,
$2,791.72k plus $6.02k land. The full-service road comparison selects it
within the existing street-advice alternatives. This carries subsequent
staff costs and debt service while later school funding is constrained.

At m194, HW1 debt is $20,188.58k against annual GDP $7,930.99k, or 254.55%.
Main debt is $2,937.46k against $5,872.47k, or 50.02%. At m357 the ratios
are 223.32% and 24.51%, with cash $203.13k and $9,898.30k respectively.
The final full-service rule is retained. The fiscal cost and slower
early schooling remain visible for Jerus.

### Automatic works before m194

Invoices, land and borrowing are nominal local thousands. Spend counters
hold order invoices, while ground purchases have their separate column.

| model | building | orders | units | invoices | land | borrowed |
|---|---|---:|---:|---:|---:|---:|
| HW1 | Paved Road | 5 | 7 | 3,106.46 | 16.23 | 1,583.72 |
| HW1 | Home Care Service | 1 | 1 | 32.99 | 0.00 | 0.00 |
| HW1 | Small Childcare Centre | 3 | 3 | 231.14 | 6.21 | 349.04 |
| HW1 | Community Health Centre | 1 | 1 | 313.88 | 3.07 | 257.34 |
| HW1 | Walk-in Clinic | 1 | 1 | 68.44 | 3.76 | 59.44 |
| HW1 | Light Rail Line | 1 | 1 | 2,791.72 | 6.02 | 2,815.61 |
| HW1 total | all | 12 | 14 | 6,544.62 | 35.30 | 5,065.14 |
| main | Wind Farm | 1 | 1 | 802.34 | 5.56 | 0.00 |
| main | Gravel Road | 3 | 5 | 803.07 | 82.94 | 662.32 |
| main | Small Childcare Centre | 3 | 3 | 215.22 | 7.92 | 167.15 |
| main | Walk-in Clinic | 2 | 2 | 174.25 | 8.18 | 117.54 |
| main | Home Care Service | 1 | 1 | 26.81 | 0.00 | 0.00 |
| main | Paved Road | 3 | 5 | 2,026.91 | 48.32 | 1,760.94 |
| main total | all | 13 | 17 | 4,048.60 | 152.92 | 2,707.95 |

### Same-month treasury and school service

Cash and Debt/GDP are measured at the pass opening. Orders are the
change in the complete automatic order counter during that pass.
Elementary is physical staffed service at that same recorded month,
clamped to 100%. Cash is nominal local thousands. Both debt limits are 240%.

| month | HW1 cash | main cash | HW1 Debt/GDP | main Debt/GDP | HW1 orders | main orders | HW1 elementary | main elementary |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| 194 | -147.56 | 35.43 | 254.55% | 50.02% | 0 | 0 | 0.0% | 0.0% |
| 195 | -148.73 | 33.30 | 254.79% | 49.76% | 0 | 0 | 0.0% | 0.0% |
| 196 | -151.60 | 33.33 | 255.10% | 49.75% | 0 | 0 | 0.0% | 0.0% |
| 197 | -157.87 | 33.27 | 255.58% | 49.63% | 0 | 0 | 0.0% | 0.0% |
| 198 | -157.11 | 32.85 | 256.08% | 49.49% | 0 | 0 | 0.0% | 0.0% |
| 199 | -151.94 | 33.14 | 256.37% | 49.36% | 0 | 0 | 0.0% | 0.0% |
| 200 | -152.88 | 32.62 | 256.69% | 49.05% | 0 | 0 | 0.0% | 0.0% |
| 201 | -152.94 | 32.85 | 257.02% | 48.76% | 0 | 0 | 0.0% | 0.0% |
| 202 | -152.48 | 32.82 | 257.33% | 48.48% | 0 | 0 | 0.0% | 0.0% |
| 203 | -153.60 | 32.73 | 257.62% | 48.22% | 0 | 0 | 0.0% | 0.0% |
| 204 | -153.82 | 32.38 | 257.96% | 47.96% | 0 | 0 | 0.0% | 0.0% |
| 205 | -91.65 | 71.38 | 257.53% | 47.73% | 0 | 0 | 0.0% | 0.0% |
| 206 | -76.97 | 86.13 | 257.65% | 47.53% | 0 | 0 | 0.0% | 0.0% |
| 207 | -84.67 | 85.36 | 258.03% | 47.34% | 0 | 0 | 0.0% | 0.0% |
| 208 | -81.07 | 84.82 | 258.39% | 47.17% | 0 | 0 | 0.0% | 0.0% |
| 209 | -78.22 | 84.02 | 258.70% | 47.02% | 0 | 0 | 0.0% | 0.0% |
| 210 | -65.64 | 82.26 | 258.64% | 46.88% | 0 | 0 | 0.0% | 0.0% |
| 211 | -83.28 | 82.26 | 259.13% | 46.76% | 0 | 0 | 0.0% | 0.0% |
| 212 | -85.95 | 81.61 | 259.71% | 46.68% | 0 | 0 | 0.0% | 0.0% |
| 213 | -84.51 | 80.94 | 260.31% | 46.61% | 0 | 0 | 0.0% | 0.0% |
| 214 | -87.41 | 79.89 | 261.01% | 46.57% | 0 | 0 | 0.0% | 0.0% |
| 215 | -90.48 | 79.00 | 261.90% | 46.56% | 0 | 0 | 0.0% | 0.0% |
| 216 | -90.25 | 77.70 | 262.75% | 46.58% | 0 | 0 | 0.0% | 0.0% |
| 217 | -99.36 | 76.92 | 263.96% | 46.63% | 0 | 0 | 0.0% | 0.0% |
| 218 | -83.41 | 80.01 | 265.34% | 46.94% | 0 | 0 | 0.0% | 0.0% |
| 219 | -90.09 | 79.71 | 266.84% | 47.27% | 0 | 0 | 0.0% | 0.0% |
| 220 | -91.92 | 79.35 | 268.32% | 47.60% | 0 | 0 | 0.0% | 0.0% |
| 221 | -88.41 | 78.37 | 269.73% | 47.93% | 0 | 0 | 0.0% | 0.0% |
| 222 | -92.25 | 78.40 | 271.14% | 48.26% | 0 | 0 | 0.0% | 0.0% |
| 223 | -92.88 | 77.61 | 272.55% | 48.58% | 0 | 0 | 0.0% | 0.0% |
| 224 | -93.41 | 77.55 | 273.97% | 48.87% | 0 | 0 | 0.0% | 0.0% |
| 225 | -94.34 | 77.59 | 275.47% | 49.17% | 0 | 0 | 0.0% | 0.0% |
| 226 | -94.44 | 76.91 | 276.97% | 49.47% | 0 | 0 | 0.0% | 0.0% |
| 227 | -99.47 | 67.53 | 279.22% | 50.00% | 0 | 0 | 0.0% | 0.0% |
| 228 | -101.84 | 67.30 | 281.78% | 50.56% | 0 | 0 | 0.0% | 0.0% |
| 229 | -104.70 | 70.63 | 283.95% | 50.95% | 0 | 0 | 0.0% | 0.0% |
| 230 | -103.68 | 71.53 | 285.05% | 51.10% | 0 | 0 | 0.0% | 0.0% |
| 231 | -102.29 | 72.86 | 286.06% | 51.24% | 0 | 0 | 0.0% | 0.0% |
| 232 | -102.29 | 72.02 | 287.06% | 51.40% | 0 | 0 | 0.0% | 0.0% |
| 233 | -101.64 | 71.19 | 288.03% | 51.58% | 0 | 0 | 0.0% | 0.0% |
| 234 | -102.11 | 72.23 | 289.03% | 51.76% | 0 | 0 | 0.0% | 0.0% |
| 235 | -102.90 | 70.78 | 290.04% | 51.97% | 0 | 0 | 0.0% | 0.0% |
| 236 | -102.64 | 69.66 | 291.08% | 52.21% | 0 | 0 | 0.0% | 0.0% |
| 237 | -103.58 | 69.08 | 292.04% | 52.46% | 0 | 0 | 0.0% | 0.0% |
| 238 | -103.37 | 73.00 | 293.00% | 52.72% | 0 | 0 | 0.0% | 0.0% |
| 239 | -104.13 | 77.73 | 293.24% | 52.21% | 0 | 0 | 0.0% | 0.0% |
| 240 | -103.89 | 66.84 | 293.26% | 52.17% | 0 | 0 | 0.0% | 0.0% |
| 241 | -41.66 | 95.56 | 293.01% | 52.32% | 0 | 0 | 0.0% | 0.0% |
| 242 | -37.42 | 79.59 | 291.90% | 52.57% | 0 | 0 | 0.0% | 0.0% |
| 243 | -73.03 | 83.94 | 291.24% | 52.82% | 0 | 0 | 0.0% | 0.0% |
| 244 | -76.91 | 147.71 | 291.57% | 53.05% | 0 | 0 | 0.0% | 0.0% |
| 245 | -76.34 | 212.19 | 291.82% | 53.26% | 0 | 0 | 0.0% | 0.0% |
| 246 | -75.84 | 275.80 | 292.09% | 53.46% | 0 | 0 | 0.0% | 0.0% |
| 247 | -76.62 | 338.22 | 292.35% | 53.66% | 0 | 0 | 0.0% | 0.0% |
| 248 | -75.20 | 399.86 | 292.59% | 53.85% | 0 | 0 | 0.0% | 0.0% |
| 249 | -74.90 | 460.98 | 292.82% | 54.02% | 0 | 0 | 0.0% | 0.0% |
| 250 | -74.82 | 863.44 | 292.05% | 80.59% | 0 | 0 | 0.0% | 0.0% |
| 251 | -74.40 | 1,084.51 | 292.22% | 82.79% | 0 | 1 | 0.0% | 0.0% |
| 252 | -74.00 | 1,088.00 | 292.40% | 80.34% | 0 | 0 | 0.0% | 0.0% |
| 253 | -32.76 | 1,462.08 | 292.09% | 71.63% | 0 | 0 | 0.0% | 0.0% |
| 254 | -30.97 | 2,234.19 | 292.72% | 61.74% | 0 | 0 | 0.0% | 0.0% |
| 255 | -71.70 | 2,580.68 | 293.69% | 56.40% | 0 | 0 | 0.0% | 0.0% |
| 256 | -76.84 | 2,985.59 | 294.59% | 51.84% | 0 | 2 | 0.0% | 0.0% |
| 257 | -76.45 | 1,453.12 | 295.44% | 49.21% | 0 | 0 | 0.0% | 0.0% |
| 258 | -73.74 | 1,730.57 | 296.21% | 46.55% | 0 | 0 | 0.0% | 0.0% |
| 259 | -74.05 | 1,989.19 | 296.99% | 43.53% | 0 | 0 | 0.0% | 0.0% |
| 260 | -73.05 | 2,256.55 | 297.71% | 41.37% | 0 | 1 | 0.0% | 0.0% |
| 261 | -72.71 | 1,643.25 | 298.40% | 39.79% | 0 | 0 | 0.0% | 100.0% |
| 262 | -72.93 | 1,918.37 | 299.05% | 37.74% | 0 | 0 | 0.0% | 100.0% |
| 263 | -72.97 | 2,148.32 | 299.64% | 37.07% | 0 | 0 | 0.0% | 100.0% |
| 264 | -72.46 | 2,533.14 | 300.19% | 35.55% | 0 | 1 | 0.0% | 100.0% |
| 265 | -71.37 | 523.25 | 300.64% | 38.17% | 0 | 2 | 0.0% | 100.0% |
| 266 | -76.17 | 408.89 | 301.92% | 41.69% | 0 | 0 | 0.0% | 100.0% |
| 267 | -79.12 | 639.42 | 303.68% | 43.06% | 0 | 0 | 0.0% | 100.0% |
| 268 | -33.65 | 841.07 | 303.75% | 44.53% | 0 | 0 | 0.0% | 100.0% |
| 269 | -28.54 | 1,057.61 | 302.64% | 44.72% | 0 | 0 | 0.0% | 100.0% |
| 270 | -72.17 | 1,251.02 | 301.91% | 45.67% | 0 | 0 | 0.0% | 100.0% |
| 271 | -74.88 | 730.68 | 302.11% | 47.63% | 0 | 0 | 0.0% | 100.0% |
| 272 | -73.43 | 893.94 | 302.22% | 50.06% | 0 | 0 | 0.0% | 100.0% |
| 273 | -73.84 | 1,140.34 | 302.25% | 50.89% | 0 | 0 | 0.0% | 100.0% |
| 274 | -72.53 | 1,342.37 | 302.18% | 51.44% | 0 | 0 | 0.0% | 100.0% |
| 275 | -70.89 | 1,577.33 | 302.09% | 50.06% | 0 | 0 | 0.0% | 100.0% |
| 276 | -1,211.30 | 1,654.48 | 302.13% | 52.53% | 0 | 0 | 0.0% | 100.0% |
| 277 | -74.22 | 1,739.12 | 301.73% | 52.56% | 0 | 0 | 0.0% | 100.0% |
| 278 | -68.33 | 1,827.09 | 300.94% | 53.15% | 0 | 0 | 0.0% | 100.0% |
| 279 | -69.70 | 1,919.25 | 299.79% | 54.45% | 0 | 0 | 0.0% | 100.0% |
| 280 | -70.75 | 2,010.34 | 298.80% | 56.46% | 0 | 0 | 0.0% | 100.0% |
| 281 | -68.62 | 2,246.04 | 298.85% | 56.80% | 0 | 1 | 0.0% | 100.0% |
| 282 | -68.35 | 444.44 | 298.99% | 55.93% | 0 | 0 | 0.0% | 100.0% |
| 283 | -66.01 | 910.16 | 298.26% | 52.82% | 0 | 0 | 0.0% | 100.0% |
| 284 | -67.27 | 1,252.56 | 297.50% | 48.45% | 0 | 0 | 0.0% | 100.0% |
| 285 | -65.73 | 1,650.63 | 296.70% | 46.02% | 0 | 0 | 0.0% | 100.0% |
| 286 | -64.19 | 1,918.09 | 295.93% | 47.90% | 0 | 1 | 0.0% | 100.0% |
| 287 | -64.93 | 387.54 | 295.15% | 53.74% | 0 | 0 | 0.0% | 100.0% |
| 288 | -64.00 | 893.70 | 294.43% | 51.51% | 0 | 1 | 0.0% | 100.0% |
| 289 | -63.67 | 808.35 | 293.69% | 76.11% | 0 | 0 | 0.0% | 100.0% |
| 290 | -57.10 | 1,382.32 | 292.26% | 69.65% | 0 | 0 | 0.0% | 100.0% |
| 291 | -53.86 | 2,120.88 | 290.53% | 64.25% | 0 | 0 | 0.0% | 100.0% |
| 292 | -52.03 | 2,304.17 | 288.79% | 61.65% | 0 | 0 | 0.0% | 100.0% |
| 293 | -50.41 | 2,588.29 | 287.12% | 58.63% | 0 | 0 | 0.0% | 100.0% |
| 294 | -53.51 | 2,747.79 | 285.57% | 58.89% | 0 | 0 | 0.0% | 100.0% |
| 295 | -18.86 | 2,874.49 | 283.79% | 60.27% | 0 | 0 | 0.0% | 100.0% |
| 296 | -15.59 | 3,000.31 | 281.18% | 62.90% | 0 | 0 | 0.0% | 100.0% |
| 297 | -14.84 | 3,131.49 | 278.70% | 65.94% | 0 | 0 | 0.0% | 100.0% |
| 298 | -60.44 | 3,262.34 | 276.71% | 65.47% | 0 | 0 | 0.0% | 100.0% |
| 299 | -71.35 | 3,393.47 | 275.78% | 65.48% | 0 | 0 | 0.0% | 100.0% |
| 300 | -70.79 | 3,528.15 | 274.84% | 67.96% | 0 | 0 | 0.0% | 100.0% |
| 301 | -72.75 | 3,664.06 | 274.16% | 70.81% | 0 | 0 | 0.0% | 100.0% |
| 302 | -71.31 | 3,805.00 | 274.00% | 76.75% | 0 | 0 | 0.0% | 100.0% |
| 303 | -70.87 | 3,949.81 | 274.02% | 80.59% | 0 | 0 | 0.0% | 100.0% |
| 304 | -60.98 | 4,096.92 | 273.93% | 79.90% | 0 | 1 | 0.0% | 100.0% |
| 305 | -67.20 | 1,714.66 | 274.18% | 83.29% | 0 | 1 | 0.0% | 100.0% |
| 306 | -56.93 | 2,026.52 | 274.36% | 81.03% | 0 | 1 | 0.0% | 100.0% |
| 307 | -73.73 | 2,529.45 | 274.93% | 75.54% | 0 | 0 | 0.0% | 100.0% |
| 308 | -79.34 | 3,670.18 | 277.09% | 71.95% | 0 | 0 | 0.0% | 100.0% |
| 309 | -76.37 | 4,059.76 | 279.14% | 67.49% | 0 | 0 | 0.0% | 100.0% |
| 310 | -72.58 | 4,437.87 | 281.11% | 63.37% | 0 | 0 | 0.0% | 100.0% |
| 311 | -63.68 | 4,836.95 | 282.19% | 60.00% | 0 | 0 | 0.0% | 100.0% |
| 312 | -43.19 | 5,220.99 | 282.90% | 56.85% | 0 | 1 | 0.0% | 100.0% |
| 313 | -47.67 | 3,630.84 | 283.55% | 52.78% | 0 | 1 | 0.0% | 100.0% |
| 314 | -8.41 | 3,885.96 | 282.83% | 49.21% | 0 | 0 | 0.0% | 100.0% |
| 315 | -20.70 | 4,791.56 | 282.48% | 46.84% | 0 | 1 | 0.0% | 100.0% |
| 316 | -42.12 | 4,930.13 | 282.63% | 44.65% | 0 | 2 | 0.0% | 100.0% |
| 317 | -5,075.67 | 3,648.14 | 283.71% | 42.35% | 0 | 0 | 0.0% | 100.0% |
| 318 | -56.87 | 3,988.12 | 283.24% | 40.51% | 0 | 0 | 0.0% | 100.0% |
| 319 | -47.34 | 4,303.92 | 284.09% | 40.30% | 0 | 0 | 0.0% | 100.0% |
| 320 | -62.43 | 4,692.00 | 283.85% | 39.21% | 0 | 0 | 0.0% | 100.0% |
| 321 | -63.75 | 4,899.55 | 283.75% | 39.96% | 0 | 0 | 0.0% | 100.0% |
| 322 | 130.35 | 5,106.28 | 280.94% | 40.44% | 0 | 0 | 0.0% | 100.0% |
| 323 | -62.83 | 5,289.24 | 279.49% | 40.62% | 0 | 0 | 0.0% | 100.0% |
| 324 | -64.07 | 5,469.63 | 279.43% | 41.74% | 0 | 0 | 0.0% | 100.0% |
| 325 | -64.34 | 5,623.11 | 279.52% | 43.13% | 0 | 1 | 0.0% | 100.0% |
| 326 | -65.19 | 5,678.43 | 280.96% | 45.42% | 0 | 0 | 0.0% | 100.0% |
| 327 | -92.90 | 4,175.60 | 282.78% | 39.26% | 0 | 0 | 0.0% | 100.0% |
| 328 | -95.57 | 4,299.43 | 284.64% | 40.15% | 0 | 0 | 0.0% | 100.0% |
| 329 | -90.92 | 4,429.57 | 286.34% | 41.72% | 0 | 0 | 0.0% | 100.0% |
| 330 | -73.98 | 4,559.28 | 287.67% | 41.57% | 0 | 0 | 0.0% | 100.0% |
| 331 | -90.35 | 4,693.06 | 289.42% | 42.89% | 0 | 0 | 0.0% | 100.0% |
| 332 | -80.97 | 4,825.58 | 290.93% | 44.62% | 0 | 0 | 0.0% | 100.0% |
| 333 | -86.31 | 4,959.58 | 292.59% | 44.68% | 0 | 0 | 0.0% | 100.0% |
| 334 | -90.30 | 5,093.38 | 294.35% | 43.76% | 0 | 0 | 0.0% | 100.0% |
| 335 | -85.20 | 5,228.82 | 295.97% | 43.89% | 0 | 0 | 0.0% | 100.0% |
| 336 | -88.77 | 5,363.67 | 297.84% | 44.36% | 0 | 0 | 0.0% | 100.0% |
| 337 | -92.30 | 5,502.70 | 299.81% | 44.67% | 0 | 0 | 0.0% | 100.0% |
| 338 | -100.82 | 5,654.44 | 300.36% | 44.20% | 0 | 0 | 0.0% | 100.0% |
| 339 | -89.43 | 5,801.39 | 300.68% | 44.17% | 0 | 0 | 0.0% | 100.0% |
| 340 | -101.46 | 5,947.92 | 301.39% | 43.60% | 0 | 0 | 0.0% | 100.0% |
| 341 | -99.91 | 6,094.93 | 302.19% | 43.47% | 0 | 0 | 0.0% | 100.0% |
| 342 | -103.01 | 6,249.66 | 303.04% | 43.97% | 0 | 0 | 0.0% | 100.0% |
| 343 | -104.20 | 6,398.40 | 303.94% | 43.53% | 0 | 0 | 0.0% | 100.0% |
| 344 | -104.54 | 6,549.16 | 304.75% | 43.27% | 0 | 0 | 0.0% | 100.0% |
| 345 | -106.87 | 6,697.50 | 305.63% | 42.77% | 0 | 0 | 0.0% | 100.0% |
| 346 | -104.56 | 6,845.34 | 306.41% | 42.40% | 0 | 1 | 0.0% | 100.0% |
| 347 | -107.08 | 1,313.93 | 307.28% | 42.14% | 0 | 0 | 0.0% | 100.0% |
| 348 | -107.67 | 1,638.14 | 308.01% | 40.77% | 0 | 0 | 0.0% | 100.0% |
| 349 | 2,287.79 | 2,556.87 | 307.46% | 36.37% | 0 | 1 | 0.0% | 100.0% |
| 350 | -100.53 | 3,842.69 | 284.03% | 32.32% | 0 | 0 | 0.0% | 100.0% |
| 351 | 171.09 | 4,091.67 | 281.74% | 32.01% | 0 | 0 | 0.0% | 100.0% |
| 352 | 153.24 | 4,466.20 | 277.31% | 30.34% | 0 | 0 | 0.0% | 100.0% |
| 353 | 67.05 | 4,587.46 | 277.21% | 29.39% | 0 | 1 | 0.0% | 100.0% |
| 354 | -40.23 | 8,519.07 | 271.53% | 28.13% | 0 | 1 | 0.0% | 100.0% |
| 355 | 80.75 | 8,765.21 | 260.13% | 26.72% | 0 | 0 | 0.0% | 100.0% |
| 356 | 121.59 | 9,226.91 | 243.11% | 25.55% | 0 | 0 | 0.0% | 100.0% |
| 357 | 203.13 | 9,898.30 | 223.32% | 24.51% | 0 | 0 | 0.0% | 100.0% |

## Provisional spending regression review at 06:47

The orchestrator requires a model mend for the early rail spending and
future essential funding, before the ensemble. The incomplete ALL run in
target/checks/hw1-service-final-pick.log was stopped by its verified script
PID 25640 and descendants. It supplies no final suite verdict.

The bounded exact m84 choice probe is target/checks/hw1-rail-choice.log.
Operating street capacity is 6,400 against load 4,993, or 128% served,
cash is −$17.21k, debt $1,445.57k and annual GDP $2,901.74k. The rail plan
opens in 5.5 months and projects demand 11.5 months out. Its comparison
claims $26.7M prevented output over twenty years against $2.8M order and
$4.3M running, making net value −$19.6M.

A paved-road-only card can wait for its shorter lead, so it offers nothing
yet. The long-horizon comparison prices only each present order against
twenty years with no further capacity additions. That overvalues the
large rail alternative against a sequence of later cheap streets.
Financing interest is also absent from the displayed cost components.
These faults require complete maintained-service comparisons, including
future staged capital and financing, plus across-service need/loss
priority. The actual order remains sized to its opening requirement.
Eligibility and building balance data stay as authored. No ensemble or
fresh gate has started on this reviewed candidate.

## Funding and complete-plan mend after the 06:47 instruction

The rail regression was caused by this batch. The early comparison priced
one small street order against one rail order, then credited twenty years
of rail relief against a city that never built another street. It omitted
new borrowing costs and repeatedly credited the same fare pool to every
additional transit unit.

★ Compare maintained service across the twenty-year horizon. Each candidate
includes later units of that type required by the unchanged reserve rule,
their quotes, opportunity land, actual purchased land and their financing.
Actual orders still buy the capacity required at their opening. Later monthly
passes place later additions. Quotes for future additions use today's terms
and the existing life trend, so these are comparable projections.

★ Charge new financing from the actual capped bond quote, including issue
spread and coupon costs. A short quote prices the full required financing
at its observed terms, while the existing automatic payment tests still
enforce the debt limit before placing an order. Future cash requirements
use available cash first. Coupon cash flows are discounted with expected
inflation included in the nominal discount rate.

★ Rank currently missing essential service by its actual preventable output
loss, then its missing share. Reserve expansion follows current shortages.
A cash or debt hold on the highest current shortage protects funds from
lower-priority proposals. Lower-priority conditional asks release their
earmarked proceeds when a higher current shortage needs the cash. The debt
and raised cash remain booked.

Whole-fleet running costs use each fleet's actual traffic wear and its total
incremental rider pool. Additional transit units share that pool. Wages
continue to use filled posts. Eligibility and buildings.json were untouched.

### Hard fixture adaptations for the funding rule

- EssentialWorksCheck's limited-power-cash and 35%-reserve parity fixtures now
  explicitly serve streets first. Their original power and cash premises
  remain hard. The new loss priority otherwise correctly spends their cash
  on the incidental street shortage before reaching power.
- AuctionCheck section 10 retains real sold-bond proceeds and outstanding
  debt. It divides the existing funded street reservation between street
  and general care through the supported saved OpenAsk state. This causes
  the legacy two-funded-ask premise without creating cash or debt. The
  original hard funded-care withdrawal and retained-money assertions remain.
- BuildAdviceCheck staffing alternatives and land-price figures now use the
  cash and land left by earlier cards. Their hard least-plan and exact stored
  figure laws remain. Financing depends on this residual treasury.
- BuildAdviceCheck's Paved-at-every-price path changes to a hard minimum
  complete maintained-service plan at each original land price. Street
  access, no free ground, feasible service and Paved's least-ground premise
  remain. Relieved traffic changes gravel repairs during the sequence.

The exact stored-figure law caught a query-order fault. A bond quote refreshes
the debt market's GDP, tax and cash inputs. Earlier candidates could therefore
use a stale discount rate before a later candidate requested financing.
BuildAdvice.lifeRate now refreshes those derived market inputs through the
existing Game method before reading the curve. This places no orders and
moves no cash or debt. Diagnostic logs are
target/checks/hw1-advice-finance-diag2.log and
target/checks/hw1-advice-finance-diag3.log.

Focused checks before the market-input mend passed the new staging, financing,
fleet running, current-loss ranking, protected funding, limited-power and
card/automatic parity fixtures. RoadCheck's focused advice/offered sections
and AuctionCheck section 10 passed. The three advice-prefix reds were kept
hard and diagnosed above. Full verification follows the completed mend.

### Causal controls and exact early replay

The five isolated omitted-mend controls each exited 1 while their fixture
premises passed. The kept model passes all five sections.

| Omitted mend | Hard law that failed | Log under target/checks |
|---|---|---|
| Later staged additions | Future units and capital included | hw1-finance-control-staging.log |
| New borrowing charges | Issue and coupon costs included | hw1-finance-control-financing.log |
| Monetary-loss ranking | Higher-loss streets precede power and use the cash | hw1-finance-control-ranking.log |
| Protected essential funding | School hold preserves funds and card labels the care hold | hw1-finance-control-protection.log |
| Whole-fleet running | Shared fares counted once and relieved-traffic wear used | hw1-finance-control-running.log |

A sixth control removes the market-input refresh alone. The unchanged equal
posts/prices and land-price premises pass, while both hard least-plan and
bit-exact stored-value assertions fail. Its log is
target/checks/hw1-finance-control-market.log. The kept advice prefix exits 0
in target/checks/hw1-finance-advice-fixed.log.

The revised model's exact default rhythm through m363 is recorded in
target/checks/hw1-finance-funding-final.log, using only a LongPlaytest
diagnostic hook over the immutable compiled production model. It places
no order at m84, with cash −$5.91k and debt $1,495.86k, 49.72% of GDP.
Elementary is ordered at m182 and m184, and first reaches 90% at m185.
Its longest eligible spell below 90% through m363 is three months,
m182–184. In the original comparison window m194–357 it is never below
90%, while main is below 90% for 67 consecutive months, m194–260.

| Same pre-pass month | Revised HW1 debt, thousands | HW1 debt/GDP | Main debt, thousands | Main debt/GDP |
|---|---:|---:|---:|---:|
| 194 | 5,495.06 | 74.14% | 2,937.46 | 50.02% |
| 357 | 28,944.75 | 173.17% | 7,941.02 | 24.51% |

The larger later HW1 debt remains information for Jerus. The m363 run has
zero money or finding failures. It orders no rail line through m363.
The full default run and final ensemble will give the complete horizon.

The first six-harness verification found two further harness assumptions.
AutoBuildCheck's decades reader dereferenced every held building, although
a protected-funding hold names a measure and has no proposed building.
Its hard reason test now requires an earlier essential operating shortage,
an actual monetary hold or partial order, and no payable alternative for
that earlier service. Other held-building tests retain their original law.

IslandLinkCheck's monthly shortage hook changed only total traffic.
A pending transit line switches the network to its traffic-stream inputs,
so this did not cause the claimed shortage. The hook now sets goods traffic
in the breakdown as well as the total, after cutting ground. All later
passes remain actual monthly passes, and the hard cue, duplicate-link and
eventual-linking assertions remain.

The market-input fast check exits 0 with zero firm Stale findings.
The first finance six-harness run kept EssentialWorksCheck,
BuildAdviceCheck, RoadCheck and AuctionCheck green. After the hard
reason-reader and traffic-stream fixture repairs, AutoBuildCheck and
IslandLinkCheck both pass in target/checks/hw1-finance-monthly-verify.log,
exit 0. That run retains the already documented single AutoBuildCheck
path observation. Stale has zero firm and 186 soft findings.

The focused crossing replay passes a real later ground-short month with
one cue and zero duplicate link steps. The original bridge then stands
at m254 after 249 further months. The omitted traffic-stream setup in
target/checks/hw1-link-finance-fixed.log fails the monthly cue and
completion laws, while its original owned-island, first-bridge and
initial-link premises pass. The kept replay is
target/checks/hw1-link-finance-fixed2.log.

## REV-HW1-3 at 09:23, provisional model under repair

The orchestrator's review of e142e4b7 found seven further faults. The
incomplete ALL pick in target/checks/hw1-finance-final-pick.log was stopped
by its verified script PID 29536, created at 09:14:09, and descendants.
It supplies no final verdict. No ensemble has started.

The superseded model's full default log,
target/checks/hw1-finance-default-final.log, exits 0 with zero findings.
Highway/death/cell longest lows are 27/3/100 months against first quotes
35/4/111, and its plant mix is 35 wind and 7 coal. Streets have a 76-month
cash hold. These are historical candidate measurements and will be
replaced after this review's model mends.

The seven accepted repairs are ask-aware cash availability without double
subtraction, actual whole-parcel and foreign-vault purchase cash, remaining
quoted benchmark maturity, actual capped financing quotes for each later
addition, changed repair costs of existing gravel roads, saved funding
reasons cleared when automation is off, and an independent arithmetic
oracle in the advice checks. Each requires its own causal regression check.

### Seven mends implemented

1. The funding pre-pass now tests the actual existing ask's current price
   against ask-aware spendable cash. The common canPay implementation takes
   the ask as an argument. Unreserved cash is counted once. The new
   WorksFinanceCheck causes the 100-free, 40-lower-reserved, 90-higher-cost
   proportions with a real empty capped auction. The bank has no risk room,
   household/firm deposit alternatives exceed the cap and the foreign
   bidders are in a saved sudden-stop state.
2. Whole-parcel selection is shared with the order path. The value's ground
   projection retains unused bought ground and excludes already selected
   offers. Its cumulative parcel bill uses the actual vault top-up rule, so
   foreign reserves are spent once across the projected sequence. The
   independent fixtures exercise both local-cash and funded-vault purchases.
   ★ When no offered parcel covers future ground, its valuation uses the
   office estimate. The actual order path still refuses unavailable ground.
3. Financing reads the quoted auction's remaining months, including a
   reopened benchmark's maturity. The fixture pays twelve real coupons
   from a twenty-year benchmark, then verifies the 228-month quote and the
   independently discounted charge.
4. Each later addition's financing reads a capped quote for its own required
   cash. ★ Equal amounts within one unchanged-city valuation reuse the
   identical quote. No quote survives a valuation call. All future prices
   and financing terms are projections at today's market inputs.
5. Whole-network running also charges the changed repair bill of retained
   gravel roads. The existing shared-fare assertion includes that separate
   wear change. A new hard fixture stands four gravel roads and puts their
   traffic above the model's quiet threshold before a paved-road addition.
   Its independent repair sum must match the value.
6. Funding reasons are saved in AutoBuilder.State as an independently copied
   map. Turning automation off clears them. The hard fixture performs a
   real scratch save/load, compares the card's reason after reload, switches
   both builders off, then back on before another pass. Both cards must
   stay clear until a new pass makes a new hold.
7. BuildAdviceCheck.valueFigure now calls the fixture-only
   AdviceValueOracle, which reads quotes, stocks, traffic and health inputs
   and independently sums staged costs, coupon schedules, running,
   physical relief and payroll returns. It never reads WorksValue's result
   or calls its factories. The original bit-exact comparison remains.
   The oracle counts pending units from their individual queue quotes.
   The separate component fixtures retain independent monetary arithmetic.

Focused advice, road and funded-auction sections pass. The new whole-parcel,
vault, remaining-maturity and later-capped-quote checks pass. The dedicated
reservation fixture initially exposed that a cap is floored at fair, so a
zero-yield cap alone still sells paper. Its explicit no-bidder conditions
now cause the intended empty auction. This corrects the fixture while
retaining the original stated cash proportions.

The kept finance-focused run exits 0 in
target/checks/hw1-rev3-finance-focus3.log. Its capped gap quote raises zero,
the higher-loss street order is placed, the lower funded reservation is
released, and debt is unchanged. The new existing-stock wear and actual
save/load card tests pass in target/checks/hw1-rev3-focus-essential2.log.
Advice, road and funded-auction prefixes also pass.

The first omitted-mend control attempt overlapped Maven clearing
target/classes. A missing-class failure invalidated that attempt. Controls
are rerun against the immutable rev3-model-classes snapshot after compile.
No missing-class exit is counted as a causal regression result.

### REV-HW1-3 causal verification passed

Every control below exits 1 with all fixture premises passing. The kept
model exits 0 on each same section. Logs are under target/checks.

| Omitted mend | Hard failure | Control log |
|---|---|---|
| Ask-aware availability | Higher-loss order missing and lower reservation retained | hw1-rev3-control-reservation.log |
| Actual parcel/vault cash | Financing differs from the actual purchase amount | hw1-rev3-control-parcel.log |
| Remaining maturity | Initial financing includes coupons past maturity | hw1-rev3-control-remaining.log |
| Later capped quotes | Complete-plan value differs from independent cash flows | hw1-rev3-control-later.log |
| Existing gravel wear | Existing-stock repair change omitted | hw1-rev3-control-wear.log |
| Saved funding reason | Actual loaded card loses the reason | hw1-rev3-control-reload.log |
| Clearing funding reason off | Re-enabling before a pass revives a stale reason | hw1-rev3-control-off.log |
| Independent oracle | Deliberately wrong initial capital fails exact value and least-plan laws | hw1-rev3-control-oracle.log |

The corresponding kept logs are hw1-rev3-kept-<name>.log. The deliberate
one-unit capital error passes the old factory-against-factory helper,
exit 0 in hw1-rev3-control-self.log, and fails the independent oracle.
This specifically demonstrates the repaired oracle's ability to detect
a production arithmetic error.

The affected-harness run exits 0 in
target/checks/hw1-rev3-named-final.log. Its eight harnesses all pass,
with zero firm Stale findings and 186 soft. The production model is
3b252292. Its new full default run is in progress over an immutable
class snapshot, with a read-only monthly financial hook. The earlier
e142 default and incomplete pick remain superseded.

## Verified default on the REV-HW1-3 model

The log is target/checks/hw1-rev3-default-final.log, over the immutable
3b252292 compiled production model. Its LongPlaytest diagnostic copy
uses the existing read-only pre/post automatic-pass hook. It records
2,404 audited months, six successful reloads, zero findings and exit 0.
All nineteen measured services are fully served at m2400. The normal
production LongPlaytest also runs in the full pick.

| Service | Months below 90% | Months below 20% | Longest below 90% | Months of longest | First quoted lead near its start |
|---|---:|---:|---:|---|---:|
| Power | 0 | 0 | 0 | — | — |
| Water | 172 | 0 | 70 | 1726–1795 | 118.0 |
| Streets | 16 | 0 | 16 | 22–37 | 17.0 |
| Through traffic | 36 | 21 | 21 | 1285–1305 | 2.0 |
| General care | 0 | 0 | 0 | — | — |
| Childcare | 0 | 0 | 0 | — | — |
| Senior care | 11 | 0 | 5 | 1645–1649 | 4.0 |
| Death care | 3 | 3 | 3 | 545–547 | 1.0 |
| Elementary | 3 | 3 | 3 | 182–184 | 2.0 |
| Middle school | 3 | 3 | 3 | 186–188 | 2.0 |
| High school | 15 | 15 | 15 | 203–217 | 8.0 |
| College | 17 | 17 | 17 | 217–233 | 18.0 |
| University | 104 | 75 | 104 | 1675–1778 | 117.0 |
| Medical school | 107 | 79 | 107 | 1675–1781 | 137.0 |
| Law school | 0 | 0 | 0 | — | — |
| Business school | 0 | 0 | 0 | — | — |
| Institute of technology | 0 | 0 | 0 | — | — |
| Police | 6 | 6 | 6 | 474–479 | 6.0 |
| Cells | 163 | 163 | 163 | 1938–2100 | 96.0 |

Eligibility uses the same 30%-of-smallest-work rule as automatic building.
Below-threshold schools contribute a served reading of one. “Through
traffic” is highways. The near-start quote is a first-unit completion
quote rounded up.

Highways' longest spell, m1285–1305, contains eighteen debt holds, two
pending-capacity passes and an order at m1303. The near-start quote is
two months. This is a funding delay followed by a short build. There are
98 months with no automatic order while over the debt limit, beside
448 on the superseded e142 candidate.

Cells' first jail is ordered at m1938 with a near-start 96-month quote.
The second is ordered at m2036 after the original promise expires.
Service recovers at m2101. The 163-month construction spell remains
information for Jerus. Pending capacity does cause a fresh order after
expiry, as the hard save/load deadline fixture independently proves.

Water's longest spell is seventy months against 118 quoted. University
and medical school have 104 and 107 months against 117 and 137 quoted,
with five orders each during their longest spells. Death care has three
months below 90%. Eligibility/calendar and staffing transitions can put
a short blip a month or two beyond a first quote. Every pass-reason count
remains in the log.

The former 76-month street cash freeze is resolved. Streets' only sixteen
low months are the founding spell, against seventeen quoted. Elementary's
only low spell is m182–184, with first 90% service at m185. In the common
m194–357 window HW1 has no elementary month below 90%, while main has
67 consecutive, m194–260.

| Pre-pass month | HW1 cash, thousands | HW1 debt, thousands | HW1 debt/GDP | Main cash, thousands | Main debt, thousands | Main debt/GDP |
|---|---:|---:|---:|---:|---:|---:|
| 84 | -5.91 | 1,495.86 | 49.72% | — | — | — |
| 194 | 108.99 | 5,495.06 | 74.14% | 35.43 | 2,937.46 | 50.02% |
| 357 | 152.11 | 24,480.58 | 113.35% | 9,898.30 | 7,941.02 | 24.51% |

No automatic order is placed at m84. The higher later debt is recorded
beside the earlier service recovery. Main's figures come from the already
recorded exact e5642145 replay, target/checks/hw1-main-funding2.log.
Its ensemble baseline is retained from the repository.

### Verified plant choices and quote sums

Actual automatic orders total 145 wind farms and 23 coal plants.
The superseded e142 candidate ordered 35 and 7. The original before-HW1
run recorded earlier ordered 22 and 14. Each played path retains its
own model identification.

The end-city one-plant comparisons have zero physical shortage and zero
prevented-output credit. Their rounded components follow.

- one Coal Power Plant: 20-year maintained-service present value: order $64.7B + land $8.9B + 0 later works and land $0.00 + new financing $0.00 + running $16.4B - prevented output loss $0.00 - payroll returned $785.9M = $89.2B. Physical relief 0.0/month; initial 68 posts (0 unfilled); initial payroll $28.6M/month; opens 16.0 months.
- one Wind Farm: 20-year maintained-service present value: order $1.4B + land $7.7B + 0 later works and land $0.00 + new financing $0.00 + running $792.6M - prevented output loss $0.00 - payroll returned $82.1M = $9.8B. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $2.9M/month; opens 4.3 months.
- one Hydro Station: 20-year maintained-service present value: order $1.3B + land $1.9B + 0 later works and land $0.00 + new financing $0.00 + running $632.4M - prevented output loss $0.00 - payroll returned $59.7M = $3.8B. Physical relief 0.0/month; initial 5 posts (0 unfilled); initial payroll $2.1M/month; opens 4.8 months.

Coal's 162-month founding quote and fuel/pollution treatment are Jerus's
balance calls. The utility books price coal fuel and plant emissions at
zero. All charged capital, land, filled posts, upkeep, repairs, utility
bills, finance and health effects enter the comparison.

Each actual-order quote below is discounted at that order's own date
and prices. Later additions are comparison projections placed by later
real passes. Chronological components are retained individually.

- power choice m440 1 Wind Farm: 20-year maintained-service present value: order $5.1M + land $10.2M + 0 later works and land $0.00 + new financing $7.3M + running $1.1M - prevented output loss $112.5M - payroll returned $87,565 = -$88.9M. Physical relief 1755.8/month; initial 7 posts (0 unfilled); initial payroll $4,491/month; opens 14.7 months.
- power choice m605 1 Wind Farm: 20-year maintained-service present value: order $6.5M + land $17.2M + 1 later works and land $13.8M + new financing $0.00 + running $2.5M - prevented output loss $280.3M - payroll returned $210,396 = -$240.6M. Physical relief 2024.1/month; initial 7 posts (0 unfilled); initial payroll $7,055/month; opens 5.5 months.
- power choice m712 1 Wind Farm: 20-year maintained-service present value: order $10.2M + land $39.6M + 1 later works and land $28.2M + new financing $3.3M + running $3.7M - prevented output loss $444.1M - payroll returned $326,956 = -$359.4M. Physical relief 2622.2/month; initial 7 posts (0 unfilled); initial payroll $11,450/month; opens 12.1 months.
- power choice m1194 1 Wind Farm: 20-year maintained-service present value: order $33.7M + land $182.4M + 1 later works and land $84.0M + new financing $136.7M + running $9.8M - prevented output loss $321.8M - payroll returned $821,966 = $124.1M. Physical relief 644.7/month; initial 7 posts (0 unfilled); initial payroll $35,218/month; opens 2.6 months.
- power choice m1217 1 Wind Farm: 20-year maintained-service present value: order $41.8M + land $193.4M + 1 later works and land $144.5M + new financing $327.1M + running $16.1M - prevented output loss $709.7M - payroll returned $1.3M = $11.9M. Physical relief 1157.9/month; initial 7 posts (0 unfilled); initial payroll $42,476/month; opens 3.0 months.
- power choice m1245 1 Wind Farm: 20-year maintained-service present value: order $45.4M + land $170.3M + 1 later works and land $146.4M + new financing $217.2M + running $20.0M - prevented output loss $632.7M - payroll returned $1.7M = -$35.1M. Physical relief 1072.9/month; initial 7 posts (0 unfilled); initial payroll $49,286/month; opens 3.4 months.
- power choice m1410 1 Wind Farm: 20-year maintained-service present value: order $42.1M + land $134.6M + 0 later works and land $0.00 + new financing $0.00 + running $12.7M - prevented output loss $574.1M - payroll returned $1.1M = -$385.8M. Physical relief 1382.5/month; initial 7 posts (0 unfilled); initial payroll $40,105/month; opens 16.8 months.
- power choice m1416 1 Wind Farm: 20-year maintained-service present value: order $41.4M + land $146.0M + 0 later works and land $0.00 + new financing $0.00 + running $13.6M - prevented output loss $0.00 - payroll returned $1.2M = $199.8M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $39,502/month; opens 11.6 months.
- power choice m1431 1 Wind Farm: 20-year maintained-service present value: order $35.6M + land $230.1M + 0 later works and land $0.00 + new financing $0.00 + running $12.0M - prevented output loss $0.00 - payroll returned $1.0M = $276.7M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $38,965/month; opens 8.3 months.
- power choice m1443 2 Wind Farm: 20-year maintained-service present value: order $90.7M + land $384.4M + 0 later works and land $0.00 + new financing $838.9M + running $19.2M - prevented output loss $1.0B - payroll returned $1.6M = $292.3M. Physical relief 3976.2/month; initial 14 posts (0 unfilled); initial payroll $81,468/month; opens 14.7 months.
- power choice m1451 1 Wind Farm: 20-year maintained-service present value: order $45.3M + land $177.1M + 0 later works and land $0.00 + new financing $999.6M + running $9.9M - prevented output loss $0.00 - payroll returned $814,017 = $1.2B. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $43,005/month; opens 12.8 months.
- power choice m1458 1 Wind Farm: 20-year maintained-service present value: order $50.7M + land $177.2M + 0 later works and land $0.00 + new financing $508.6M + running $10.6M - prevented output loss $0.00 - payroll returned $861,972 = $746.1M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $45,364/month; opens 13.6 months.
- power choice m1465 1 Wind Farm: 20-year maintained-service present value: order $51.4M + land $172.4M + 0 later works and land $0.00 + new financing $58.5M + running $11.5M - prevented output loss $0.00 - payroll returned $944,055 = $292.8M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $47,752/month; opens 14.8 months.
- power choice m1483 1 Wind Farm: 20-year maintained-service present value: order $57.4M + land $138.5M + 0 later works and land $0.00 + new financing $414.9M + running $13.8M - prevented output loss $0.00 - payroll returned $1.1M = $623.4M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $54,205/month; opens 14.0 months.
- power choice m1501 1 Wind Farm: 20-year maintained-service present value: order $59.1M + land $155.1M + 0 later works and land $0.00 + new financing $153.6M + running $21.3M - prevented output loss $0.00 - payroll returned $1.8M = $387.3M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $59,167/month; opens 12.4 months.
- power choice m1606 1 Coal Power Plant: 20-year maintained-service present value: order $3.7B + land $285.3M + 0 later works and land $0.00 + new financing $946.3M + running $710.7M - prevented output loss $31.7B - payroll returned $22.9M = -$26.1B. Physical relief 22311.3/month; initial 68 posts (1 unfilled); initial payroll $870,515/month; opens 27.0 months.
- power choice m1607 1 Wind Farm: 20-year maintained-service present value: order $78.9M + land $248.0M + 1 later works and land $318.1M + new financing $296.1M + running $56.2M - prevented output loss $0.00 - payroll returned $4.8M = $992.6M. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $89,522/month; opens 8.7 months.
- power choice m1617 2 Wind Farm: 20-year maintained-service present value: order $169.4M + land $485.5M + 0 later works and land $0.00 + new financing $0.00 + running $62.6M - prevented output loss $0.00 - payroll returned $5.3M = $712.1M. Physical relief 0.0/month; initial 14 posts (0 unfilled); initial payroll $183,728/month; opens 10.1 months.
- power choice m1626 3 Wind Farm: 20-year maintained-service present value: order $275.2M + land $792.2M + 0 later works and land $0.00 + new financing $0.00 + running $99.4M - prevented output loss $0.00 - payroll returned $8.5M = $1.2B. Physical relief 0.0/month; initial 21 posts (0 unfilled); initial payroll $279,362/month; opens 14.8 months.
- power choice m1676 13 Wind Farm: 20-year maintained-service present value: order $1.7B + land $3.2B + 0 later works and land $0.00 + new financing $1.9B + running $431.6M - prevented output loss $0.00 - payroll returned $37.1M = $7.2B. Physical relief 0.0/month; initial 91 posts (0 unfilled); initial payroll $1.4M/month; opens 49.6 months.
- power choice m1726 1 Coal Power Plant: 20-year maintained-service present value: order $8.1B + land $238.1M + 0 later works and land $0.00 + new financing $1.9B + running $305.9M - prevented output loss $21.9B - payroll returned $9.3M = -$11.3B. Physical relief 58310.1/month; initial 68 posts (2 unfilled); initial payroll $1.3M/month; opens 288.5 months.
- power choice m1858 9 Wind Farm: 20-year maintained-service present value: order $2.4B + land $3.9B + 18 later works and land $8.5B + new financing $0.00 + running $929.2M - prevented output loss $31.1B - payroll returned $86.9M = -$15.4B. Physical relief 9744.7/month; initial 63 posts (2 unfilled); initial payroll $2.5M/month; opens 29.7 months.
- power choice m1927 1 Coal Power Plant: 20-year maintained-service present value: order $16.7B + land $1.4B + 2 later works and land $27.4B + new financing $0.00 + running $2.8B - prevented output loss $984.8B - payroll returned $90.0M = -$936.6B. Physical relief 302219.2/month; initial 68 posts (6 unfilled); initial payroll $2.9M/month; opens 113.7 months.
- power choice m1938 1 Coal Power Plant: 20-year maintained-service present value: order $18.3B + land $1.3B + 2 later works and land $24.9B + new financing $0.00 + running $2.2B - prevented output loss $558.2B - payroll returned $72.6M = -$511.4B. Physical relief 221104.6/month; initial 68 posts (7 unfilled); initial payroll $3.1M/month; opens 129.3 months.
- power choice m1939 1 Coal Power Plant: 20-year maintained-service present value: order $18.5B + land $1.4B + 1 later works and land $10.3B + new financing $0.00 + running $1.5B - prevented output loss $124.9B - payroll returned $48.6M = -$93.3B. Physical relief 54325.2/month; initial 68 posts (6 unfilled); initial payroll $3.2M/month; opens 133.1 months.
- power choice m1940 1 Wind Farm: 20-year maintained-service present value: order $383.8M + land $1.2B + 8 later works and land $12.7B + new financing $0.00 + running $652.1M - prevented output loss $0.00 - payroll returned $57.0M = $14.9B. Physical relief 0.0/month; initial 7 posts (0 unfilled); initial payroll $346,555/month; opens 38.6 months.
- power choice m1980 68 Wind Farm: 20-year maintained-service present value: order $41.7B + land $170.5B + 64 later works and land $77.5B + new financing $301.7B + running $4.5B - prevented output loss $1.3T - payroll returned $384.9M = -$660.6B. Physical relief 454449.9/month; initial 476 posts (28 unfilled); initial payroll $34.9M/month; opens 151.1 months.
- power choice m1982 5 Coal Power Plant: 20-year maintained-service present value: order $150.7B + land $4.4B + 0 later works and land $0.00 + new financing $165.6B + running $839.5M - prevented output loss $114.1B - payroll returned $26.1M = $207.4B. Physical relief 467037.9/month; initial 340 posts (39 unfilled); initial payroll $23.9M/month; opens 500.2 months.
- power choice m1985 5 Coal Power Plant: 20-year maintained-service present value: order $155.9B + land $4.5B + 0 later works and land $0.00 + new financing $157.5B + running $476.5M - prevented output loss $0.00 - payroll returned $14.7M = $318.3B. Physical relief 0.0/month; initial 340 posts (39 unfilled); initial payroll $24.7M/month; opens 528.1 months.
- power choice m1987 5 Coal Power Plant: 20-year maintained-service present value: order $159.1B + land $9.2B + 0 later works and land $0.00 + new financing $252.7B + running $354.6M - prevented output loss $0.00 - payroll returned $11.0M = $421.3B. Physical relief 0.0/month; initial 340 posts (39 unfilled); initial payroll $25.3M/month; opens 538.6 months.
- power choice m1989 3 Coal Power Plant: 20-year maintained-service present value: order $97.3B + land $2.3B + 0 later works and land $0.00 + new financing $114.4B + running $185.4M - prevented output loss $0.00 - payroll returned $5.8M = $214.1B. Physical relief 0.0/month; initial 204 posts (23 unfilled); initial payroll $15.5M/month; opens 536.2 months.
- power choice m1992 32 Wind Farm: 20-year maintained-service present value: order $22.4B + land $23.0B + 0 later works and land $0.00 + new financing $35.9B + running $1.2B - prevented output loss $0.00 - payroll returned $101.6M = $82.4B. Physical relief 0.0/month; initial 224 posts (12 unfilled); initial payroll $18.8M/month; opens 154.1 months.
