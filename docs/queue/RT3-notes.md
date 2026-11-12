# RT3 — BusinessInvestment's retirement in a falling city

**Held for Jerus's decision.** Lanekeeper's instruction
`20261112-122907-214-lanekeeper-f43d`, 2026-11-12, requires every criterion
passed by the baseline to pass. AUTO2 F1 and ZERO_SLACK F1 regress. The
full gate awaits that decision. The complete instruction was read at 12:46.

## Model and calls

★ The built candidate applies MAX_RETIREMENT_FRACTION to the retireable excess, after
keeping demand and TARGET_HEADROOM. Round up to a whole building so a small,
genuinely spare holding can leave. The old holding-based pace could remove a
modest surplus in one month. Lost payroll then reduced demand for the next
retirement. Each sector already supplies its own demand measure. An estimate
of demand at usual employment would require an additional counterfactual for
those different measures. Distress uses its solvency test and holding-based
pace.

★ Final recommendation is to hold this candidate and review the usual-
employment demand option before acceptance. The same-state diagnostic below
shows that the cap slows the original collapse while still losing 52.4% of
posts. Its successful full-history replay depends on earlier path changes.
This supersedes the initial recommendation of the excess cap. The latter was
chosen for its direct relation to spare capacity and existing sector measures.

The whole surplus can span several building kinds. AgricultureCheck and
FoodProcessingCheck keep their two-kind fixtures and calculate the permitted
quantity from that surplus. InvestCheck exercises modest and whole surpluses,
headroom, the building granule, orders in flight and the rolling counter.

★ Use the row's one-fifth loss measure for KeptCheck's regression guard. It
checks the minimum posts throughout m2391-2416 against four fifths of the
opening posts, and asserts that the window was reached. The calendar is
explicit because section 61 starts at m2377 on current main's path.

LongPlaytest counts retired building posts over trailing twelve-month windows
against each sector's standing posts before the window began. Both retirement
rules contribute. The flow includes retirements of replacement capacity. It reports
overlapping windows above one fifth, separate episodes per sector and the
largest annual share. The counter lives beside the plant counts. Closed bank
branches sold through Retail are excluded from Retail's posts because their
operator is the bank.

## WG2's path

Archived source at `99279a93` was built under `target/rt3-wg2`, with the same
Earth/world files as the batch. A temporary KeptCheck probe stops at m2391
and plays 25 plain months. Its only model variants are the retirement formula
and, for one run, disabling `Migration.arrivalBrakeNow()` from m2391. The cap
is applied throughout the repaired city's history. The old-rule control
reproduces WG2's documented m2404 exactly.

| rule | people at m2391 | posts m2391 | posts m2404 | posts m2416 | minimum posts in the window |
|---|---:|---:|---:|---:|---:|
| old holding pace | 311,150 | 166,822 | 167,443 | 55,105 | 55,105 |
| excess pace | 40,434 | 22,824 | 23,474 | 24,074 | 22,824 |
| excess pace, brake off from m2391 | 40,434 | 22,824 | 23,474 | 24,076 | 22,824 |

The repaired path grows differently before the test window and reaches it as
a smaller city. At m2416 it has 43,028 residents and 11.89% out of work. With
the brake disabled it has 95,848 and 61.00% out of work, while its posts hold.
Logs are `target/checks/rt3-wg2-old.txt`, `rt3-wg2-cap.txt` and
`rt3-wg2-brake-off.txt`, with all 26 monthly observations. Each probe exited 0.
A further diagnostic applies the cap only from m2391 to the old city's state.
It starts with the same 311,150 people and 166,822 posts as the old control,
then ends m2416 with 287,581 people and 79,400 posts, its minimum over the
window. Out of work reaches 57.02%. Retail falls to 24,981 posts, Real Estate
keeps 41,402 and Construction has 9,926. The cap reduces the original loss
from 67.0% to 52.4%. This candidate therefore fails to keep the old city's
posts when introduced at the onset of this decline. Its log is
`target/checks/rt3-wg2-late.txt`.

## Checks

- Initial `./batch-check.sh --fast` compiled, regenerated the indexes and
  reported zero firm Stale findings.
- One intermediate compile caught a guard inserted into the wrong helper.
  The guard was moved before the named harnesses ran.
- `InvestCheck` and `ScaleCheck` pass. ScaleCheck's fixtures and tolerances
  are retained. Worst tracking differences are people 5.16e-5, cash 1.14e-3,
  savings 1.32e-2, deposits 1.00e-2 and prices 1.26e-4. Every RT3-assigned
  ScaleCheck assertion passes.
- The first named run, `target/checks/rt3-first.txt`, exits 4 from five
  KeptCheck assertions discussed below. Its section 61 grows from 20,385
  posts to 21,635 over m2377-2402. Twenty timing assertions are soft. Maps
  regenerate and Stale reports zero firm findings.
- Final `./batch-check.sh --pick` compiles and selects 99 of 201 harnesses.
  Its KeptCheck executes the explicit guard and passes. Throughout
  m2391-2416 posts run from **21,085 to 22,335**, minimum **21,085**.
  The exact log line is
  `the city keeps four fifths of its posts throughout m2391-2416 (RT3, 21085 to 22335, least 21085) OK`.
  The pick is **incomplete**. Following lanekeeper's short held-batch
  wrap-up, its own process tree was stopped after this guard passed. The
  outer wrapper emitted `exit-0` after the forced stop. That wrapper status
  is not a completed-check verdict. `target/checks/rt3-pick.txt` is explicitly
  marked STOPPED/incomplete, and the partial logs remain in `target/checks`.
  The initial complete named run above supplies the recorded KeptCheck reds.

## Ensemble and GDP finding

The changed retirement path requires the ensemble. `tools/ensemble/ensemble.sh
RT3` completes all 80 runs, exit 0, on the model snapshot `40ea73b0`, in
19.6 minutes. That BusinessInvestment model is the batch's model. The kept
baseline is `docs/ensemble/RT3.md`, using `{next}`. It scores **22/26** against
**23/26** in `docs/ensemble/0.9.93.md`. A2 and A3 remain red. HIGH R3 turns
green. AUTO2 and ZERO_SLACK F1 turn red from the same seed 11, m902 finding.

GDP there is -431.688583, with a passing money audit and no retirement in
m899-903. The accounts read C 8,921.046, construction 0, inventory -236.426,
G 3,919.509 and NX -13,035.817. Raw imports are 54,963.980 and exports
43,010.260. Copper Refining accounts for 26,713.959 of raw imports. Its
struck statement carries zero exports and 27,566.151 local sales. At the
end-of-month probe, its pending ledger carries 26,756.764 exports and its
output row has shipped 4,621.895 t of copper rod. Those are different phases
and require reconciliation before assigning the missing output to a term.
No additional non-food held-good inventory explains the gap. This is recorded
for proposed follow-up **GDP1**, an accounts/flow-timing finding in existing code. The retirement
change exposes the city state. The root cause of the negative GDP remains
unresolved. The struck statement and the pending ledger represent different
phases, so their difference alone establishes no missing export. The diagnostic logs are
`target/checks/rt3-gdp.txt`, `rt3-gdp-sectors.txt`, `rt3-gdp-stocks.txt` and
`rt3-gdp-ledger.txt`. Each diagnostic plays seed 11 only through m903.

## KeptCheck findings

The first run finds five screen assertions outside the retirement guard.
The drawing code in these sections is the existing code. Proposed follow-up **KS3** covers
these changing-state cases, with the following precise evidence.

- Government Overview, section 69, starts with 412 nodes. Its eventful
  months add at most 59 and hand back 373 on average. The 90% handback and
  same-top checks pass. The separate per-month new-node ceiling fails
  because 59 exceeds 41.2. The changing Government rows need attribution.
- The Profit page's held-card fixture has two dials before and after its
  attempted insertion. The required extra card never appears. Its three
  card-insertion/held-slider assertions consequently fail. This fixture
  needs to cause the extra payer card before testing its retained slider.
- Household money retains all 75 tooltips that are present on both draws.
  One retained node loses its tooltip. The same page with a cell open and
  its other variants pass. `tipsAcrossMonth()` explicitly removes a kept
  tooltip when the next draw does not tip that node. The conditional content
  needs attribution to distinguish a valid disappearing explanation from
  missing tooltip retention.

## Default LongPlaytest count

The final pick's 2,400-month playtest reports **1,893 of 26,275** trailing
sector-year windows above one fifth, **133 episodes**, and a largest annual
retirement flow of **300%**, Heavy Industry at m1082. Gross retirements can
exceed the year's opening posts when replacement capacity opens during it.
The sector episode counts are Agriculture 2, Automotive 3, Business Services
6, Cement & Concrete 7, Construction 3, Copper Refining 7, Food Processing 9,
Freight 7, Heavy Industry 12, Industry 11, Manufacturing 7, Materials 8,
Real Estate 9, Restaurants 9, Retail 25 and Wood Products 8.

The money audit and six reloads pass. Its layout findings are CL6's
`industry stands farther out than the homes` and DM2's `an old works moved`,
both at m2400. The latter is a Construction Depot in district (0,0), one of
two drawn at m601, with two drawn at m2400 and the model's type count at least
six in between. The full log is `target/checks/LongPlaytest.log`.

## Ensemble verdict changes

These are all three criteria whose PASS/FAIL verdict differs from 0.9.93.


### HIGH R3

```text
0.9.93
| HIGH | R3 | inflation m660-720 minus twin | -2.15% [-6.38% to 2.84%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (5 above +1 pt) |
RT3
| HIGH | R3 | inflation m660-720 minus twin | -2.50% [-6.39% to 1.32%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
```

The upper-tail seed count moves from five to one, crossing R3's two-seed
limit. The median annualised index-growth difference over m660-720 moves
from -2.15 to -2.50 points. Over the whole m600-720 intervention, HIGH R1
reads -3.53 points against -2.91 in the baseline, and R2's unemployment cost
rises from 0.83 to 1.20 points. The changed capacity/employment histories
produce a stronger measured disinflation response relative to AUTO2 twins.
The reduction in late inflation outliers is the direct reason R3 turns green. The remaining outlier is seed 12,
whose m660-720 annualised inflation is -0.904% under HIGH and -2.226%
under AUTO2, a difference of +1.322 points.

### AUTO2 F1

```text
0.9.93
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
RT3
| AUTO2 | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
```

Seed 11 reaches the negative m902 GDP described above. Each criterion
counts cities with a finding, and its permitted baseline count is zero.
One finding therefore turns this verdict red. The two policies share this
part of the city's history, so the same accounting observation fails both.

### ZERO_SLACK F1

```text
0.9.93
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
RT3
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
```

Seed 11 reaches the negative m902 GDP described above. Each criterion
counts cities with a finding, and its permitted baseline count is zero.
One finding therefore turns this verdict red. The two policies share this
part of the city's history, so the same accounting observation fails both.

## Gate status

Lanekeeper holds the batch on the ensemble and refers acceptance to Jerus.
The branch carries the candidate, baseline and evidence for that review.
A launcher run and readiness marker await the decision.

## Policy in the fall (Jerus's question)

Investigation of 2026-11-12. The maximum permitted floor cut does not prevent this collapse under either retirement rule. Floor pinning with spare people begins with the first sharp job loss at m2407. It is absent at m2391 in the original city. The interest-rate autopilot is off and the annual GovernmentPlayer is inactive in these KeptCheck histories.

The controls replay archived WG2 source `99279a93` and the held RT3 candidate `9b1c4948`, with the prescribed Earth and world files. The original control reproduces 167,443 posts at m2404 and 9,521 posts with 150,206 residents at m2450. The current RT3 control reproduces its calendar guard, 21,085 posts at m2391 and 22,335 at m2416. For the matched WG2 comparison, every run uses the original retirement rule through m2391. The cap and floor interventions then start from that same state. All months after m2391 are plain clock months through m2450.

Money is recorded in the model's stored units, each representing 1,000 nominal local currency units. Financial tables divide those stored figures by 10^12, matching the scale of the cited 7e12 loss. Wage and price tables divide them by 10^6. Counts are rounded filled-post equivalents where the allocation uses a fill rate.

The floor and the four skill bands

The real floor is 0.08955678489670532 in founding money, equal to the going-rate anchor in these fixtures. Its tie is PRICES and its adjustment is zero. KeptCheck.main() sets the floor to the going rate for its WG1 fixture. A newly founded city normally starts at LabourMarket.DEFAULT_FLOOR_SHARE, 85% of that anchor. In every control observation over m2380-2420, the cash floor equals `cashGoingRate()`. Actual wages by band are below. University is a range because its licensed professions have different wages.

| history | month | cash floor | NONE wage | DIPLOMA wage | COLLEGE wage | UNIVERSITY wage |
|---|---|---|---|---|---|---|
| WG2 original | 2380 | 41.936 | 41.936 | 44.291 | 68.741 | 100.482 to 195.077 |
| WG2 original | 2391 | 47.890 | 47.890 | 50.592 | 79.012 | 115.984 to 229.108 |
| WG2 original | 2404 | 55.928 | 55.928 | 59.506 | 93.329 | 135.081 to 272.817 |
| WG2 original | 2416 | 56.445 | 56.445 | 57.245 | 100.191 | 141.877 to 282.216 |
| WG2 original | 2420 | 54.431 | 54.431 | 54.431 | 99.847 | 140.734 to 280.441 |
| current RT3 history | 2380 | 656.537 | 656.537 | 764.904 | 1,244.944 | 1,488.973 to 3,268.476 |
| current RT3 history | 2391 | 691.029 | 691.029 | 801.335 | 1,282.439 | 1,580.805 to 3,416.081 |
| current RT3 history | 2404 | 731.149 | 731.149 | 838.439 | 1,321.702 | 1,677.893 to 3,568.986 |
| current RT3 history | 2416 | 771.630 | 771.630 | 878.159 | 1,371.000 | 1,771.726 to 3,732.280 |
| current RT3 history | 2420 | 782.557 | 782.557 | 891.404 | 1,388.127 | 1,801.564 to 3,786.408 |

For comparison, `baseWage(job)` is the going wage before scarcity and licence multipliers. At m2391 its values are as follows, on the same million-unit scale.

| band | WG2 going base | current RT3 going base |
|---|---|---|
| NONE | 47.890 | 691.029 |
| DIPLOMA | 62.285 | 898.737 |
| COLLEGE | 76.126 | 1,098.457 |
| UNIVERSITY | 113.497 to 215.921 | 1,637.699 to 3,115.623 |

The screen condition is exactly `isPinned(band) && surplusInBand(band) > 0 && isPaidTheFloor(band)`. The last test allows LabourMarket.PINNED_TOLERANCE, 2%, above the cash floor. At m2391 the original NONE band is paid the floor and has zero surplus. DIPLOMA has 35,249 spare people and pays 50.592 million against the 47.890 million floor, 5.64% above it. Neither therefore qualifies for PINNED_WORDS at that month.

| history | band | qualifying months / 41 | months | spare people at m2416 | workers paid floor at m2416 |
|---|---|---|---|---|---|
| WG2 original | NONE | 14 | 2407-2420 | 41,974 | 11,567 |
| WG2 original | DIPLOMA | 5 | 2416-2420 | 86,715 | 42,719 |
| WG2 original | COLLEGE | 0 | - | 0 | 0 |
| WG2 original | UNIVERSITY | 0 | - | 0 | 0 |
| WG2 with cap from m2391 | NONE | 14 | 2407-2420 | 35,524 | 18,214 |
| WG2 with cap from m2391 | DIPLOMA | 5 | 2416-2420 | 69,585 | 60,367 |
| WG2 with cap from m2391 | COLLEGE | 0 | - | 0 | 0 |
| WG2 with cap from m2391 | UNIVERSITY | 0 | - | 0 | 0 |
| current RT3 history | NONE | 39 | 2380-2420 except 2409 and 2419 | 397 | 9,680 |
| current RT3 history | DIPLOMA | 0 | - | 2,718 | 0 |
| current RT3 history | COLLEGE | 0 | - | 0 | 0 |
| current RT3 history | UNIVERSITY | 0 | - | 0 | 0 |

The original city grows from 167,443 posts at m2404 to 167,510 at m2406, then drops to 155,821 at m2407. NONE first qualifies at that drop. DIPLOMA first qualifies at m2416. COLLEGE and UNIVERSITY have no qualifying observation in the requested window.

The private sectors with workers in a qualifying band are below. These count the jobs actually paid the floor in that band. The residual includes employers outside the private-sector table, including city services and the bank.

| employer | original m2416 | original m2420 | cap m2416 | current RT3 m2391 |
|---|---|---|---|---|
| Agriculture | 0 | 0 | 0 | 4 |
| Construction | 6,801 | 5,406 | 9,926 | 3,745 |
| Freight | 233 | 232 | 233 | 0 |
| Luxury Retail | 0 | 0 | 0 | 2 |
| Real Estate | 31,382 | 18,230 | 41,402 | 0 |
| Restaurants | 43 | 19 | 43 | 32 |
| Retail | 13,801 | 6,946 | 24,951 | 4,334 |
| private total | 52,260 | 30,833 | 76,555 | 8,117 |
| other employers | 2,026 | 2,026 | 2,026 | 688 |
| all employers | 54,286 | 32,859 | 78,581 | 8,805 |

At original m2391, 57,029 workers are paid the floor, including Retail 40,435, Construction 14,959, Freight 121, Restaurants 362 and Cement & Concrete 96. Those workers are in NONE, whose spare count is zero then. In the current RT3 city, NONE already qualifies at m2391: 962 people spare and 8,805 workers paid the floor. Its main private employers at the floor are Retail 4,334 and Construction 3,745.

Policy during the fall

Across all 41 control months, the original policy rate is held at 7.579461253557312% and the current RT3 rate at 11.155004104547162%. Both have autopilot=false, INFLATION aim, STANDARD strictness and a 2% inflation target. The central bank's QE target, its previous target and its shares of paper and term paper are all zero. Thus the configured inflation rule supplies no automatic policy-rate move in this interval.

The exchange-rate regime is LEAN. Its bounds are 1e-9 and 1e9 local currency per US dollar. Every observation in both controls is strictly inside those bounds. The observed exchange rates and the government's monthly accounts follow. The debt-gains column is retired-paper gains plus exchange gains on foreign debt, which enter the reported balance.

| history | month | YoY prices | FX / 10^6 | revenue / 10^12 | expenses / 10^12 | debt gains / 10^12 | balance / 10^12 |
|---|---|---|---|---|---|---|---|
| WG2 original | 2380 | 12.70% | 22.469 | 9.876051 | 1.255575 | 0.000000 | 8.620476 |
| WG2 original | 2391 | 12.89% | 26.081 | 11.590577 | 1.823633 | -0.859941 | 8.907003 |
| WG2 original | 2404 | 1.63% | 25.301 | 13.557634 | 0.773435 | 1.766109 | 14.550309 |
| WG2 original | 2416 | -45.33% | 16.608 | 12.739507 | 2.527393 | 0.309585 | 10.521699 |
| WG2 original | 2420 | -45.35% | 15.698 | 10.565721 | 1.866314 | -0.214057 | 8.485350 |
| current RT3 history | 2380 | 2.51% | 369.998 | 42.586075 | 42.699289 | 6.960644 | 6.847430 |
| current RT3 history | 2391 | 3.20% | 376.934 | 43.283807 | 43.903749 | -5.784431 | -6.404373 |
| current RT3 history | 2404 | 5.83% | 336.048 | 49.423159 | 49.550153 | 20.482197 | 20.355203 |
| current RT3 history | 2416 | 1.17% | 342.178 | 53.698910 | 54.353360 | -2.989367 | -3.643817 |
| current RT3 history | 2420 | -1.00% | 356.825 | 55.269541 | 54.459147 | -12.211921 | -11.401527 |

At original m2404, government services consumed are 0.175052e12 and total budget expenses are 0.773435e12. In the current RT3 control, the corresponding amounts are 1.221929e12 and 49.550153e12. The balance includes the debt gains shown above.

GovernmentPlayer has zero invocations in both entire fixture histories, including zero in m2380-2420. `LongPlaytest.probeRun()` calls `run(g, 1)`, whose government-policy flag is false. KeptCheck then uses the clock's `toggleNextMonth()`. The annual response belongs to `runPlayer()`. The observed settings are profit tax 15%, sales tax 15%, pension replacement 0%, EI benefit 0% and one fund-withdrawal step, 0.25% of fund value per month. The original decision log does record standing automatic bank rescues at m2412 and m2420, labelled $123.3Q and $162.8Q respectively. The current control has no decision-log entry during this window.

Retail's loss and prices

The cited original m2404 loss reconciles from the struck Sector.Statement as follows. Expenses carry negative signs. The utilities are given in raw stored units because they are tiny beside the other entries.

| entry | amount / 10^12 |
|---|---|
| Revenue | 0.911687 |
| Inputs | -0.658263 |
| Wages | -5.792550 |
| Maintenance | -0.160544 |
| Interest | -1.050080 |
| Property tax | -0.185677 |
| Sales tax | -0.105888 |
| Borrowing-cost amortisation | -0.066847 |
| Separate recurring rent | 0 |
| Foreign income | 0 |
| Profit tax | 0 |
| Net income | -7.108162 |

Electricity is 4.749189 raw stored units and water 26.148769. The sum including those utilities is -7.108162e+12. Retail owns its shops. Its statement carries property tax and financing costs, with zero separate recurring rent expense. Household rent prices are included below because they affect the customers' budget.

Of the 0.911687e12 revenue at m2404, groceries contribute 0.414244e12 and petrol 0.497444e12. Wages are 6.35 times total revenue. Holding the other entries fixed and removing the entire wage bill leaves a 1.315613e12 loss. The floor intervention lowers the actual m2404 wage bill to 5.358291e12, a 7.50% reduction, and the loss to 6.677139e12. Retirement still sees sustained losses. The loss counter counts consecutive negative months, and the spare-capacity test sizes retirement from demand and capacity. A smaller loss therefore continues to qualify for retirement.

| month | basket charged | basket landed cost | shelf price floor | clearing price | average household rent |
|---|---|---|---|---|---|
| 2380 | 6.270 | 3.719 | 6.264 | 0.727 | 3.973 |
| 2391 | 6.967 | 4.217 | 7.071 | 1.007 | 4.482 |
| 2404 | 8.344 | 5.093 | 8.492 | 1.017 | 4.355 |
| 2416 | 8.674 | 4.990 | 8.519 | 3.858 | 1.442 |
| 2420 | 8.561 | 4.860 | 8.418 | 7.157 | 1.094 |

At m2404 the shops can hand over 116,461 baskets and demand at the price is 50,191. The shelf's calculated clearing price is 1.017 million, against a cost-based price floor of 8.492 million. The loss therefore combines a large payroll with a small sales margin and substantial financing costs. On the current RT3 history at m2404, Retail revenue is 1.716898e12, wages 8.584264e12, interest 26.325084e12 and net income -35.788035e12. Financing is its largest expense on that separate history.

The requested floor intervention

The floor is lowered at m2391 before the next month. Its base is set to `getMinSettable()`, 0.02238919622417633, a quarter of the going anchor, and its adjustment to the permitted minimum -0.9. That combination produces the lowest accepted cash floor, 2.5% of the previous floor, a 97.5% cut. The visible base dial alone reaches 25% of the anchor. The intervention uses the additional public adjustment setting to test the full model bound. Its PRICES tie continues to apply.

In the original state, the immediate cash floor falls from 47,890,205.738392 to 1,197,255.143460 stored units. In the current RT3 state it falls from 691,029,212.048797 to 17,275,730.301220. Actual wages walk 12% of the remaining target gap per month. Their market minimum remains 70% of each going base, so this legal-floor cut does not produce a 97.5% wage cut. Every reported intervention has zero workers paid the legal floor from m2392 through m2450.

Each cell below gives posts and out of work. The WG2 cap begins at m2391 on the original state, matching the retirement comparison already in these notes. The current RT3 control and cut use its separate history with the cap operating from the founding.

| replay | m2404 posts / out of work | m2416 posts / out of work | m2450 posts / out of work |
|---|---|---|---|
| WG2 original rule, original floor | 167,443 / 11.85% | 55,105 / 70.08% | 9,521 / 89.72% |
| WG2 original rule, lower floor | 167,443 / 11.82% | 55,107 / 70.19% | 9,521 / 89.65% |
| WG2 cap from m2391, original floor | 167,443 / 11.85% | 79,400 / 57.02% | 10,331 / 86.52% |
| WG2 cap from m2391, lower floor | 167,443 / 11.82% | 79,363 / 57.19% | 10,356 / 87.19% |
| current RT3 history, original floor | 21,735 / 12.95% | 22,335 / 12.29% | 24,029 / 14.01% |
| current RT3 history, lower floor | 21,735 / 12.95% | 22,335 / 12.29% | 24,029 / 13.68% |

At m2416 the cut leaves 287,189 residents under the original rule, against 286,025 in its control, and 288,752 under the cap, against 287,581. The city therefore retains some additional people while its posts stay near the control counts. By m2450 the original-rule populations are 148,515 with the cut and 150,206 in the control. The cap populations are 129,104 and 124,229 respectively.

The initial qualification probes found no band meeting the full screen condition at m2391 in the original state and therefore made no cut. They are the `old-cut` and `cap-cut` files. The reported `old-lowered`, `cap-lowered` and `current-lowered` runs apply the requested m2391 intervention proactively, in advance of the original city's later pinning. Their cut records are present and their accepted floor ratio is exactly 0.025.

The control and intervention JSONL files are `target/checks/rt3-policy-{old,cap,current,old-lowered,cap-lowered,current-lowered}.jsonl`. Each has all 71 monthly observations from m2380 through m2450. All six reported runs exit 0. Temporary probe sources and runner scripts are under `target/rt3-wg2`, `target/rt3-policy-current`, `target/rt3-policy-cut2` and `target/checks/rt3-policy-*.sh`. The probe records the exact screen predicates, worker allocation, struck statements, policy controls and decision-log entries. Committed changes for this investigation are confined to these notes.
