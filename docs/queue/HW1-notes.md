# HW1 — The auto city builds its works when they are needed

**HELD for Jerus. The ensemble lost three baseline PASS criteria. No ship file.**

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
