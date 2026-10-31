# ST1 - why ScaleCheck's town stalls since HD2: a study (2026-10-31)

Jerus, 2026-10-31, settling HD2-S: "Study the stall first". The brief is
`docs/queue/ST1.md`; the probe and its switches are in `docs/queue/ST1/`.

## 1. The answer

**The stall is the fixture's, not HD2's.** Section 6's town never grew by
its own plan, before HD2 or after it. Its "growing" months on 0.8.75 were a
public-works boom: from m1,066 the advisor ordered four civic builds in 110
months, paid from a 3.0M treasury. The town grew while the builders worked,
from 3,173 people to 8,104 at m1,200. It then fell back to 5,254 by m2,500,
with the treasury under nothing from m1,165. On HD2 the town is ahead of
0.8.75's until m730. At m731 it is 8% bigger, so it crosses the advisor's
police threshold early: two stations, standing from m737. Their running
costs, and a smaller tax take, leave 390k instead of 3.0M for the m1,066
build, and the cluster of builds that made the boom never forms.

No HD2 candidate, switched off alone, brings the take-off back: not the coupon
kept abroad, not the tax and dividend on it, not the advisor's police, not the
running costs. No real city is on this path either: the ensemble's slowest
young city on HD2 had 15,178 people at m900 against the fixture's 3,542.

Underneath, the town has a floor that is the game's own, and it is the same on
0.8.75. From m30 the builders sit at their core crew with 203 posts laid off,
13-40% of the town is unemployed, the landlords hold "housing ahead of jobs",
and nobody arrives. The staffing gate holds the founding town: switched off
from m0, the town is 35,000 by m900. That is LD1's and IC1's ground (§5).

## 2. The probe

`ST1Probe` builds the town as section 6 does (`playOnLiterals`, the rate
pinned, `OrderSearchCheck.playtestCity`'s calls), but one month at a time in a
`TempDirs` folder. Each month it writes 66 figures to a CSV, and the advisor's
moves, each sector's decision and the staffing bands to a TSV. It equals
`playtestCity` itself to the last digit at m200 and m1,200 on both trees
(`ST1Reference`). On main the town has 2,789 people and a treasury of
-8,553.32 at m1,200, as section 6 found. A run to m2,500 takes 30 seconds.

`switches.patch` adds `ST1.off(key)`, armed from `-Dst1.from`. The keys:
- `coupon`: no coupon.
- `keep`: HD1's way. The coupon goes into the till and the dollars do not grow.
- `taxed`: the coupon is kept out of pre-tax income, and the dollars still grow.
- `police`: the advisor never offers a police station.
- `upkeep`: the treasury pays no running costs for care, schools, safety or transit.
- `upkeepsafety`: no safety bill alone.
- `gate`: `MIN_STAFFABLE_TO_ORDER` is read as 0.
- `gateall`: the gate always passes.
- `idle` / `withheld`: XS1's idle builder's crews, counted as spare.

With no key set, the patched tree gives main's run exactly. To rerun:
`sh docs/queue/ST1/matrix.sh 2500 base= old=075 police=police gate900=gate@900`.
`run.sh`'s header says how to set up the two trees.

## 3. The candidates, one at a time (main 0.8.89, armed from m0, to m2,500)

| run | m600 | m731 | m900 | m1,075 | m1,200 | m1,500 | end | homes m1,200 | treasury m900 | dry from | tills m600 | police |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| main | 3,203 | 3,417 | 3,542 | 3,365 | 2,789 | 2,771 | 2,033 | 4,920 | 956,901 | m1,124 | 99,276 | 2 |
| 0.8.75 | 3,165 | 3,151 | 3,726 | 3,742 | 8,104 | 7,956 | 5,254 | 9,266 | 2,227,422 | m1,165 | 123,398 | 3 |
| no coupon | 3,266 | 3,104 | 3,507 | 3,805 | 4,150 | 2,741 | 2,773 | 5,492 | 594,628 | m1,091 | 173,783 | 1 |
| coupon to the till (keep) | 3,203 | 3,417 | 3,519 | 3,362 | 2,801 | 2,744 | 2,002 | 4,920 | 955,015 | m1,122 | 100,151 | 2 |
| coupon untaxed (taxed) | 3,266 | 3,309 | 3,709 | 3,811 | 3,233 | 3,186 | 2,963 | 5,480 | 87 | m875 | 182,045 | 2 |
| no police advised | 3,203 | 3,417 | 3,525 | 3,162 | 3,557 | 2,966 | 2,162 | 5,164 | 1,645,576 | m1,200 | 99,276 | 0 |
| no running costs | 3,203 | 3,417 | 3,542 | 3,442 | 3,990 | 3,142 | 2,618 | 5,240 | 1,781,136 | m1,711 | 99,276 | 2 |
| no safety bill | 3,203 | 3,417 | 3,542 | 3,409 | 3,688 | 2,962 | 2,442 | 5,270 | 1,553,655 | m1,203 | 99,276 | 2 |

What the table shows:
- **`keep` runs almost on main's line** (2,801 at m1,200). It puts HD1's
  coupon cash back in the till, so the till paying the tax and dividend on a
  coupon kept abroad is not the stall's cause.
- **The till's cash at m600** (HD2's "20% less") moves with the dollars bought
  and recalled each month. It is not a measure of what businesses can spend.
- **Without police or without running costs** the treasury holds 1.6-1.8M at
  m900, yet the town tops out at 3,557-3,990 and falls back.

## 4. Where HD2's town parts from 0.8.75's

**To m730, HD2's town is ahead.**
- Population at m731: 3,417 against 3,151.
- Treasury at m730: 1.42M against 1.37M.
- GDP m2-730: 10.0M against 9.8M.
- Both build a Vehicle Works, HD2's at m727 and 0.8.75's at m733.

**At m731 the police throttle passes for HD2's town** (the crime cost saved by
a station is more than its running cost) and not for 0.8.75's: two stations,
at m737 and m743. 0.8.75's town takes roads and clinics at m731-732. HD2's
takes the same at m733 and m760.

**m731 to m1,066, the treasury:**
- 0.8.75 gains 1.63M; HD2 loses 1.03M, a gap of 2.66M.
- The stations' running costs: 1.29M.
- Tax: 1.03M less (GDP 8.7M against 12.0M; jobs 1,056 against 1,398 by m1,060).
- The rest: 0.34M.

**At m1,066 the advisor finds the dead unburied in both towns.**

On 0.8.75, three cemeteries at m1,066-1,068 cost 2.34M (the treasury goes from
3,001,667 to 664,110):
- The builders get 14.5-17.8 months of work.
- Unemployment falls from 454 to 234, and 47-105 migrants arrive a month.
- Then come three police stations at m1,083 (none stood yet), buses at m1,112
  and a health centre at m1,177, each another order. 8,104 people at m1,200.

On HD2, two crematoria and a cemetery cost 134k:
- 10-14 months of work, and 114 migrants a month for a few months.
- 3,803 people at m1,100.
- Then the advisor offers only bakeries ("mill"): the police already stand.
- The town falls back.

**Switching off the police or the running costs gives HD2's town the treasury**
(1.78M or 2.04M at m1,066) **but not the cluster:**
- The cemetery order comes, and the town reaches 3,811 or 4,248.
- No police order follows at m1,083 (switched off, or already standing), so it
  falls back.

**The boom needed three things at once:** a big treasury, and civic needs that
fell due together. HD2 moved one threshold by a month's luck of size.

## 5. The staffing gate and the idle crews (for LD1 and IC1)

**In the stalled months the gate does not bind.**
- From m600 to m1,200 it refuses about one sector a month: Manufacturing's
  Machine Works, at 78% staffable against the 80% it wants.
- Switched off from m600, m731 or m900 (`gate`, `gateall`, `idle`,
  `withheld`), the run equals main's to the digit (m600: within 1%).
  Manufacturing then holds on profit: "steel at $744.28 a tonne and the wage
  bill leave nothing in it".
- At m1,000, 670 of 1,674 workers are unemployed. The town lacks jobs, not
  workers.

**At the founding the gate decides the town's size.**

From m20 to m60 it holds these, while 196 people (29%) are unemployed at m60
and the DIPLOMA band has 402 spare:

| sector | building | staffable |
|---|---|---|
| Filling stations | Filling Station | 33% |
| Industry | Bakery | 48% |
| Manufacturing | Fabrication Shop | 0% |
| Restaurants | Restaurant | 8% |
| Retail | Small Grocery Store | 4% |
| Construction | Builders' Yard (11.2 months of work queued) | 0% |

Over m0-600 it held:

| sector | months held |
|---|---|
| Manufacturing | 420 |
| Agriculture | 310 |
| Restaurants | 212 |

Through m30-m450:
- The bands' wages stay flat: NO_DIPLOMA 4-5, DIPLOMA 4-5, COLLEGE_HEALTH 6-9.
- Arrivals fall to none by m300.

**XS1's signature fits the founding.** The builders sit at their core crew from
m30, with 203 posts laid off against 196 unemployed at m60. XS1's test (core
crew, laid-off posts at least half the unemployed, unemployment at least 10%)
holds 232 of the first 300 months and 209 of the next 300. It holds the same
on 0.8.75 (232, 207). On main it holds again in every month from m1,500.

**Switched off from the founding:**

| run | population | the rest |
|---|---|---|
| `gate` from m0 | 4,705 at m200, 34,105 at m900 | the COLLEGE posts reach 257 against 49 workers by m120, and the college wage rises from 6 to 15 |
| `idle` from m0 | 12,173 at m1,200 | the trap is down to 43 months in all |

Neither switch helps when armed later:

| run | population | treasury dry from |
|---|---|---|
| `gate` from m60 | 5,846 at m731 | m772 |
| `gate` from m300 | 4,087 at m731 | m856 |
| `idle` from m300 | 4,094 at m1,075, then back down | - |

So for LD1 this is Jerus's loop, in the founding town: the gate refuses for
want of workers while nobody's wage moves and no migrant comes. A warning:
both releases later bust. The gate-free town falls from 35,015 at m1,500 to
11,276 at m2,000 (its homes from 43,828 to 19,780, its treasury dry from
m2,048). The idle-free town falls from 12,083 at m1,500 to 6,018 at the end.
LD1's fix should be read for an overbuild bust as well as for the start.

## 6. The ensemble's young cities

All 16 of HD2's run (0.8.76) and of 0.8.83's (XR4-final), on the world's band
with the rate floating:

| | m600 | m900 | treasury m900 |
|---|---|---|---|
| HD2's AUTO2 seeds | 4,217-12,688 (median 7,332) | 15,178 at the least | 4.85-6.46M and growing |
| 0.8.83's AUTO2 seeds | 8,332-16,110 (median 11,318) | - | - |
| the fixture | 3,203 | 3,542 | 957k |

- **None is on the fixture's path.**
- **HD2 did slow the young cities.** AUTO2's 40,000 people came at m1,156
  (1,076-1,429) against 0.8.75's m1,101 (874-1,154): 55 months later at the
  median, 275 at the slowest.
- **0.8.88 is back to m1,066.** Its outlier, seed 11 at m2,256, is XS1's trap,
  which IC1 frees.
- A player's town would not sit on the literals with the rate pinned, and the
  real runs say it grows.

## 7. The calls (★; each the recommended way, BP1)

- ★ **No model fix from this study (Recommended).** HD2's coupon, the tax and
  dividend on it, the police choice and the running costs are not the cause,
  one by one. The town's floor is the founding gate and the idle crews, which
  LD1 and IC1 already take.
- ★ **ST2 queued, after LD1 and IC1 (Recommended).** The steps:
  1. Rescan section 6's town on their model, with the scratch scanner of
     HD2's and EM2's notes. With the gate off from m0 it grows steadily from
     m200 to m900.
  2. If a month holds every assertion, `FREE_FIXTURE_MONTHS` moves to it.
  3. If none does, section 6 gets a town of its own plan: a founding and
     builds that grow it without the advisor's treasury-paid civic builds.

  Either way ScaleCheck's premise is unmoved. Until then section 6 stays red,
  named at the gate, as HD2-S has it. The alternative is a town of its own
  plan now. It would be built on a founding that LD1 is about to change, and
  likely done twice.

Docs and probes only: no model change, no trace moved, no ensemble.
