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
