# RT3 — BusinessInvestment's retirement in a falling city

**Count-only completion authorized.** Jerus's decision at about 23:35 on
2026-11-12 was "Yes, count only", in answer to "Should RT3 ship as the
retirement count only?" Lanekeeper relayed it in
`20261112-233628-081-lanekeeper-4bcf`. The completion section at the end
records the main-rule checks and final verdict. Earlier cap and policy work
below records the investigation that preceded this decision.

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

## Policy test (Jerus, 19:40)

Investigation requested by lanekeeper in `20261112-194215-755-lanekeeper-c313`.
RT3 remains held. The merged branch contains the existing candidate.
This investigation exercises temporary model variants under `target`.

### Rebuild and checks

Fetched and merged `origin/main` at `2d1b7df3` (0.9.97, including KS1, DM2,
BS1 and GDP1). The merge is `48908105`. Generated-document conflicts took
main and were regenerated by the fast check in `b7c454c5`. Both commits
were pushed before these policy trials.

`./batch-check.sh --fast` passed. Compilation took 44 seconds and the maps
were regenerated. Stale reported zero firm and 181 soft findings.
The log is `target/checks/rt3-rebuild-fast.txt`.

The merged candidate completed AUTO2 seed 11 through m903. GDP at m902 is
**+25,003.153327401** in the model's stored money units, with an empty findings
list. GDP1's forecast fix removes the negative-GDP observation from the
earlier seed-11 replay. This targeted replay took 40.3 seconds. Its log and
CSV are `target/checks/rt3-policy2-seed11.log` and
`target/checks/rt3-policy2-seed11.csv`. The earlier 80-run ensemble remains
a record of the pre-merge model.

### Starting states and replay method

Each trial starts after m2391 has completed. The rate rule is enabled there
and first acts in m2392. GovernmentPlayer is considered after every subsequent
completed month and acts at its eligible January reviews. Main's retirement
rule and the cap are switched only for the forward interval. Every trial
uses the merged 0.9.97 model, the full saved revenue history and the same
Earth/world files used by the batch.

| state | history source | people | posts | out of work | initial policy rate |
|---|---|---:|---:|---:|---:|
| WG2 original state | 99279a93 / 0.9.91 | 311,150 | 166,822 | 15.657% | 7.579461% |
| Earlier held fixture | 40ea73b0 model, 9b1c4948 fixture / 0.9.94 | 37,554 | 21,085 | 13.467% | 11.155004% |
| Rebuilt current fixture | b7c454c5 / 0.9.97 | 55,040 | 31,323 | 12.307% | 11.295202% |

The rebuilt fixture has moved to 31,323 posts and an 11.295202% rate at
m2391. The earlier held fixture is included to answer the question about
its 11.155004% rate.
WG2's prehistory uses its original holding-based retirement pace. The two
held fixtures use the existing RT3 cap through m2391. Each forward rule
then starts from the same captured city for its policy comparisons.

All three snapshots include DataSave, HistorySave and the map sidecar.
The source fixture's allocated-land counter is also carried across.
A normal load derives allocation from the buildings and left these fixtures
with zero free ground, stopping the construction growth seen in situ.
The diagnostic restores the exact captured allocation counter and asserts
that available ground, population, posts, policy rate and revenue history
match the source before applying policy. This preserves the fixture's
land accounting for the comparison. The initial runs without this correction
were discarded.

| state | source allocated land, sq ft | source free land, sq ft | standing/site footprint, sq ft | snapshot SHA-256 prefix |
|---|---:|---:|---:|---|
| WG2 original state | 113,813,544.000 | 520,069,911.267 | 873,813,544.000 | `39df46892aeabfd6` |
| Earlier held fixture | 35,815,938.000 | 42,236,405.605 | 795,861,000.000 | `6a419817eb49657d` |
| Rebuilt current fixture | 46,110,938.000 | 29,626,088.474 | 806,156,000.000 | `c22a7367a6ef7dfe` |

### Posts, unemployment and Retail at m2404

Each checkpoint gives posts, then unemployed workers and their share of the
workforce. Retail gives payroll / revenue at m2404, both in trillions of
stored money units per month. These stored units are thousands of currency.

#### WG2 original state

| retirement | policy from m2391 | m2404 posts / out | m2416 posts / out | m2450 posts / out | Retail payroll / revenue, m2404 |
|---|---|---:|---:|---:|---:|
| main | neither | 167,443 / 22,492 (11.85%) | 55,105 / 128,689 (70.08%) | 9,521 / 67,412 (88.10%) | 5.7925 / 0.9117 |
| main | rate rule | 167,443 / 22,495 (11.86%) | 55,124 / 128,336 (70.02%) | 10,236 / 58,393 (85.69%) | 5.8122 / 0.8671 |
| main | GovernmentPlayer | 167,443 / 22,492 (11.85%) | 55,104 / 128,544 (70.06%) | 9,521 / 66,637 (87.98%) | 5.7928 / 0.9153 |
| main | both | 167,443 / 22,495 (11.86%) | 55,292 / 128,375 (69.97%) | 18,916 / 73,828 (80.18%) | 5.8125 / 0.8748 |
| cap | neither | 167,443 / 22,492 (11.85%) | 79,400 / 105,109 (57.02%) | 10,331 / 63,229 (86.52%) | 5.7925 / 0.9117 |
| cap | rate rule | 167,443 / 22,495 (11.86%) | 80,141 / 104,387 (56.63%) | 20,235 / 74,928 (79.40%) | 5.8122 / 0.8671 |
| cap | GovernmentPlayer | 167,443 / 22,492 (11.85%) | 80,482 / 104,110 (56.45%) | 9,875 / 65,499 (87.44%) | 5.7928 / 0.9153 |
| cap | both | 167,443 / 22,495 (11.86%) | 80,866 / 103,738 (56.26%) | 19,431 / 58,173 (75.83%) | 5.8125 / 0.8748 |

#### Earlier held fixture

| retirement | policy from m2391 | m2404 posts / out | m2416 posts / out | m2450 posts / out | Retail payroll / revenue, m2404 |
|---|---|---:|---:|---:|---:|
| main | neither | 21,735 / 3,216 (12.95%) | 22,335 / 3,115 (12.29%) | 24,029 / 3,904 (14.01%) | 8.5843 / 1.7169 |
| main | rate rule | 21,735 / 3,216 (12.95%) | 22,335 / 3,116 (12.30%) | 24,029 / 3,855 (13.87%) | 8.5904 / 1.7438 |
| main | GovernmentPlayer | 21,735 / 3,216 (12.95%) | 22,335 / 3,115 (12.29%) | 24,029 / 3,681 (13.32%) | 8.5840 / 1.7056 |
| main | both | 21,735 / 3,216 (12.95%) | 22,335 / 3,125 (12.33%) | 24,029 / 4,128 (14.70%) | 8.5900 / 1.7308 |
| cap | neither | 21,735 / 3,216 (12.95%) | 22,335 / 3,115 (12.29%) | 24,029 / 3,904 (14.01%) | 8.5843 / 1.7169 |
| cap | rate rule | 21,735 / 3,216 (12.95%) | 22,335 / 3,116 (12.30%) | 24,029 / 3,855 (13.87%) | 8.5904 / 1.7438 |
| cap | GovernmentPlayer | 21,735 / 3,216 (12.95%) | 22,335 / 3,115 (12.29%) | 24,029 / 3,681 (13.32%) | 8.5840 / 1.7056 |
| cap | both | 21,735 / 3,216 (12.95%) | 22,335 / 3,125 (12.33%) | 24,029 / 4,128 (14.70%) | 8.5900 / 1.7308 |

#### Rebuilt current fixture

| retirement | policy from m2391 | m2404 posts / out | m2416 posts / out | m2450 posts / out | Retail payroll / revenue, m2404 |
|---|---|---:|---:|---:|---:|
| main | neither | 31,973 / 4,356 (12.02%) | 32,573 / 4,866 (13.02%) | 34,273 / 4,769 (12.24%) | 1.3369 / 0.3195 |
| main | rate rule | 32,011 / 4,377 (12.06%) | 32,611 / 4,872 (13.02%) | 34,330 / 4,652 (11.95%) | 1.3415 / 0.3290 |
| main | GovernmentPlayer | 31,973 / 4,356 (12.02%) | 32,573 / 4,866 (13.02%) | 34,273 / 4,769 (12.24%) | 1.3369 / 0.3195 |
| main | both | 32,011 / 4,314 (11.90%) | 32,611 / 4,948 (13.20%) | 34,330 / 4,895 (12.50%) | 1.3416 / 0.3263 |
| cap | neither | 31,973 / 4,356 (12.02%) | 32,573 / 4,866 (13.02%) | 34,273 / 4,769 (12.24%) | 1.3369 / 0.3195 |
| cap | rate rule | 32,011 / 4,377 (12.06%) | 32,611 / 4,872 (13.02%) | 34,330 / 4,652 (11.95%) | 1.3415 / 0.3290 |
| cap | GovernmentPlayer | 31,973 / 4,356 (12.02%) | 32,573 / 4,866 (13.02%) | 34,273 / 4,769 (12.24%) | 1.3369 / 0.3195 |
| cap | both | 32,011 / 4,314 (11.90%) | 32,611 / 4,948 (13.20%) | 34,330 / 4,895 (12.50%) | 1.3416 / 0.3263 |

### Policy-rate paths

All rates below are annual percentages. Trials with neither policy or only
GovernmentPlayer retain their initial rate throughout. The monthly series
are retained in the JSONL files. The selected months show the first response,
the approach to the fall, the zero-rate interval and the requested endpoints.

#### WG2 original state rates

| retirement / policy | 2391 | 2392 | 2396 | 2402 | 2404 | 2406 | 2416 | 2450 | interval low / high and first month |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| main / rate rule | 7.579 | 19.341 | 25.432 | 31.616 | 14.109 | 0.000 | 0.000 | 17.434 | 0.000 at 2406 / 63.623 at 2441 |
| main / both | 7.579 | 19.341 | 25.432 | 31.616 | 14.344 | 0.000 | 0.000 | 7.354 | 0.000 at 2406 / 38.692 at 2440 |
| cap / rate rule | 7.579 | 19.341 | 25.432 | 31.616 | 14.109 | 0.000 | 0.000 | 23.711 | 0.000 at 2406 / 48.040 at 2445 |
| cap / both | 7.579 | 19.341 | 25.432 | 31.616 | 14.344 | 0.000 | 0.000 | 24.014 | 0.000 at 2406 / 34.282 at 2445 |

#### Earlier held fixture rates

| retirement / policy | 2391 | 2392 | 2396 | 2402 | 2404 | 2406 | 2416 | 2450 | interval low / high and first month |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| main / rate rule | 11.155 | 4.794 | 5.718 | 9.184 | 10.705 | 11.129 | 4.266 | 10.704 | 0.512 at 2422 / 11.155 at 2391 |
| main / both | 11.155 | 4.794 | 5.718 | 9.184 | 7.996 | 6.756 | 0.000 | 9.117 | 0.000 at 2414 / 11.155 at 2391 |
| cap / rate rule | 11.155 | 4.794 | 5.718 | 9.184 | 10.705 | 11.129 | 4.266 | 10.704 | 0.512 at 2422 / 11.155 at 2391 |
| cap / both | 11.155 | 4.794 | 5.718 | 9.184 | 7.996 | 6.756 | 0.000 | 9.117 | 0.000 at 2414 / 11.155 at 2391 |

#### Rebuilt current fixture rates

| retirement / policy | 2391 | 2392 | 2396 | 2402 | 2404 | 2406 | 2416 | 2450 | interval low / high and first month |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| main / rate rule | 11.295 | 0.000 | 0.632 | 3.895 | 6.143 | 7.474 | 1.012 | 7.596 | 0.000 at 2392 / 11.295 at 2391 |
| main / both | 11.295 | 0.000 | 0.632 | 3.895 | 5.056 | 4.803 | 0.000 | 7.075 | 0.000 at 2392 / 11.295 at 2391 |
| cap / rate rule | 11.295 | 0.000 | 0.632 | 3.895 | 6.143 | 7.474 | 1.012 | 7.596 | 0.000 at 2392 / 11.295 at 2391 |
| cap / both | 11.295 | 0.000 | 0.632 | 3.895 | 5.056 | 4.803 | 0.000 | 7.075 | 0.000 at 2392 / 11.295 at 2391 |

### GovernmentPlayer's actual moves

The review months are 2401, 2413, 2425, 2437 and 2449. The table lists
reviews that actually moved a lever. Each resulting setting is profit tax /
sales tax / pension replacement / EI replacement, all percentages, followed
by fund-withdrawal steps. A step withdraws 0.25% of fund value a month.
An absent review held every setting.

| state | retirement / policy | month | wealth in annual-revenue years | resulting tax and transfer percentages / withdrawal steps |
|---|---|---:|---:|---|
| WG2 original state | main / GovernmentPlayer | 2401 | 4.214 | 10 / 13 / 5 / 5 / 40 |
| WG2 original state | main / GovernmentPlayer | 2413 | 3.990 | 5 / 11 / 10 / 10 / 40 |
| WG2 original state | main / GovernmentPlayer | 2437 | -1.060 | 10 / 13 / 10 / 10 / 39 |
| WG2 original state | main / GovernmentPlayer | 2449 | -1.251 | 15 / 15 / 10 / 10 / 38 |
| WG2 original state | main / both | 2401 | 2.350 | 10 / 13 / 5 / 5 / 40 |
| WG2 original state | main / both | 2413 | 7.044 | 5 / 11 / 10 / 10 / 40 |
| WG2 original state | main / both | 2425 | 16.061 | 0 / 9 / 15 / 15 / 40 |
| WG2 original state | main / both | 2437 | -1.024 | 5 / 11 / 15 / 15 / 39 |
| WG2 original state | main / both | 2449 | -0.398 | 10 / 13 / 15 / 15 / 38 |
| WG2 original state | cap / GovernmentPlayer | 2401 | 4.214 | 10 / 13 / 5 / 5 / 40 |
| WG2 original state | cap / GovernmentPlayer | 2413 | 4.010 | 5 / 11 / 10 / 10 / 40 |
| WG2 original state | cap / GovernmentPlayer | 2425 | 4.289 | 0 / 9 / 15 / 15 / 40 |
| WG2 original state | cap / GovernmentPlayer | 2437 | -0.802 | 5 / 11 / 15 / 15 / 39 |
| WG2 original state | cap / GovernmentPlayer | 2449 | -1.180 | 10 / 13 / 15 / 15 / 38 |
| WG2 original state | cap / both | 2401 | 2.350 | 10 / 13 / 5 / 5 / 40 |
| WG2 original state | cap / both | 2413 | 7.340 | 5 / 11 / 10 / 10 / 40 |
| WG2 original state | cap / both | 2425 | 22.675 | 0 / 9 / 15 / 15 / 40 |
| WG2 original state | cap / both | 2437 | 128.817 | 0 / 7 / 20 / 20 / 40 |
| WG2 original state | cap / both | 2449 | -0.534 | 5 / 9 / 20 / 20 / 39 |
| Earlier held fixture | main / GovernmentPlayer | 2401 | 2.025 | 10 / 13 / 5 / 5 / 40 |
| Earlier held fixture | main / GovernmentPlayer | 2413 | -0.650 | 15 / 15 / 5 / 5 / 39 |
| Earlier held fixture | main / GovernmentPlayer | 2425 | -0.602 | 20 / 15 / 5 / 5 / 38 |
| Earlier held fixture | main / GovernmentPlayer | 2437 | -0.576 | 25 / 15 / 5 / 5 / 37 |
| Earlier held fixture | main / GovernmentPlayer | 2449 | -0.383 | 30 / 15 / 5 / 5 / 36 |
| Earlier held fixture | main / both | 2401 | 4.654 | 10 / 13 / 5 / 5 / 40 |
| Earlier held fixture | main / both | 2413 | 4.048 | 5 / 11 / 10 / 10 / 40 |
| Earlier held fixture | main / both | 2425 | 15.050 | 0 / 9 / 15 / 15 / 40 |
| Earlier held fixture | main / both | 2437 | 6.810 | 0 / 7 / 20 / 20 / 40 |
| Earlier held fixture | main / both | 2449 | -0.997 | 5 / 9 / 20 / 20 / 39 |
| Earlier held fixture | cap / GovernmentPlayer | 2401 | 2.025 | 10 / 13 / 5 / 5 / 40 |
| Earlier held fixture | cap / GovernmentPlayer | 2413 | -0.650 | 15 / 15 / 5 / 5 / 39 |
| Earlier held fixture | cap / GovernmentPlayer | 2425 | -0.602 | 20 / 15 / 5 / 5 / 38 |
| Earlier held fixture | cap / GovernmentPlayer | 2437 | -0.576 | 25 / 15 / 5 / 5 / 37 |
| Earlier held fixture | cap / GovernmentPlayer | 2449 | -0.383 | 30 / 15 / 5 / 5 / 36 |
| Earlier held fixture | cap / both | 2401 | 4.654 | 10 / 13 / 5 / 5 / 40 |
| Earlier held fixture | cap / both | 2413 | 4.048 | 5 / 11 / 10 / 10 / 40 |
| Earlier held fixture | cap / both | 2425 | 15.050 | 0 / 9 / 15 / 15 / 40 |
| Earlier held fixture | cap / both | 2437 | 6.810 | 0 / 7 / 20 / 20 / 40 |
| Earlier held fixture | cap / both | 2449 | -0.997 | 5 / 9 / 20 / 20 / 39 |
| Rebuilt current fixture | main / GovernmentPlayer | 2401 | -0.122 | 20 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | main / GovernmentPlayer | 2413 | 0.334 | 25 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | main / GovernmentPlayer | 2437 | 0.189 | 30 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | main / both | 2401 | 15.777 | 10 / 13 / 5 / 5 / 40 |
| Rebuilt current fixture | main / both | 2413 | 12.379 | 5 / 11 / 10 / 10 / 40 |
| Rebuilt current fixture | main / both | 2425 | 17.910 | 0 / 9 / 15 / 15 / 40 |
| Rebuilt current fixture | main / both | 2437 | 22.747 | 0 / 7 / 20 / 20 / 40 |
| Rebuilt current fixture | main / both | 2449 | -0.227 | 5 / 9 / 20 / 20 / 39 |
| Rebuilt current fixture | cap / GovernmentPlayer | 2401 | -0.122 | 20 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | cap / GovernmentPlayer | 2413 | 0.334 | 25 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | cap / GovernmentPlayer | 2437 | 0.189 | 30 / 15 / 0 / 0 / 0 |
| Rebuilt current fixture | cap / both | 2401 | 15.777 | 10 / 13 / 5 / 5 / 40 |
| Rebuilt current fixture | cap / both | 2413 | 12.379 | 5 / 11 / 10 / 10 / 40 |
| Rebuilt current fixture | cap / both | 2425 | 17.910 | 0 / 9 / 15 / 15 / 40 |
| Rebuilt current fixture | cap / both | 2437 | 22.747 | 0 / 7 / 20 / 20 / 40 |
| Rebuilt current fixture | cap / both | 2449 | -0.227 | 5 / 9 / 20 / 20 / 39 |

### Player levers and automatic cities

The player can set the policy rate manually or hand it to the rule on
Policy > Money. A live city created through `Game.newGame()` enables rate
autopilot by default. The annual GovernmentPlayer response is a harness
player. Live Game does not invoke it. A player can make its EI, pension,
fund withdrawal, profit-tax and sales-tax moves through the policy and fund
screens. Those five levers are open by m2391 in these cities.

KeptCheck's city has rate autopilot off and does not call GovernmentPlayer.
Default LongPlaytest calls GovernmentPlayer through `runPlayer`; its rate
autopilot flag defaults off unless `-Dplaytest.autopilot=true` is supplied.
Ensemble's AUTO2 city has both rate autopilot and GovernmentPlayer enabled.
These entry points therefore have different default policy settings.

### Recommendation

★ Recommend demand measured at the city's usual employment as the next
retirement design to test. WG2's original city still loses 66.9% of its
opening posts by m2416 under main's rule with both policies, reaching
55,292 posts and 69.97% out of work. The cap with both policies reaches
80,866 posts and 56.26% out of work, a 51.5% loss. By m2450 every tested
combination has lost over 87% of WG2's opening posts. Policy alone and
the cap both fail the requested one-fifth-loss bound on this starting state.

The earlier held and rebuilt fixtures keep their posts under either forward
retirement rule, including the controls with neither policy. Their history
before m2391 already put them on a different path. Their survival cannot
establish that enabling policy at m2391 cures WG2's original collapse.

The rate rule initially raises WG2's rate from 7.58% to 19.34%, then to
31.62% at m2402, before reaching zero at m2406. It is an inflation response
and therefore does not provide an immediate cut in that city. GovernmentPlayer
uses the government's wealth rule. It expands transfers and cuts taxes when
rich and can raise taxes when poor. In WG2 at m2404, Retail still pays
5.79-5.81 trillion in wages against 0.87-0.92 trillion in revenue across all
policies. These levers leave that operating gap large.

The usual-employment demand experiment would test the retirement feedback
directly, asking whether lost payroll makes viable capacity appear surplus.
Its outcome remains unverified. Keep policy-enabled cases in that comparison
and require success on WG2's same starting state. RT3 remains held for Jerus's
decision on that experiment.

### Evidence and scope

The 24 preserved-state forward trials each record every month from 2391 through 2450.
Their traces follow `target/checks/rt3-policy2-{wg2,held,rebuilt}-{main,cap}-{none,rate,government,both}.jsonl`. Snapshot stems are
`target/checks/rt3-policy2-{wg2,held,rebuilt}-state`. The temporary driver,
formula switch and capture classes are under `target/rt3-policy2-*`.
The final run log is `target/checks/rt3-policy2-preserved-run.log`.

The preserved-state current-fixture control was checked against an
uninterrupted in-situ KeptCheck continuation. All 60 observations agree
exactly on posts, unemployed workers, unemployment rate, policy rate,
available land, Retail payroll and Retail revenue. The in-situ trace is
`target/checks/rt3-policy2-live-rebuilt-cap-none.jsonl` and the comparison
result is `target/checks/rt3-policy2-replay-validation.txt`.

The forward-run summary records exit 0 for all three sets of eight trials.
The source captures also exited 0. Full KeptCheck UI checks encountered while
reaching the snapshots serve as fixture preparation for this investigation.
The final batch gate and full ensemble await the held batch's release.


## Count-only completion (Jerus, 23:35)

Jerus answered "Yes, count only" when asked "Should RT3 ship as the
retirement count only?" Lanekeeper's instruction is
`20261112-233628-081-lanekeeper-4bcf`, 2026-11-12.

The diagnosed fall had power 15% served and streets 1% served. Automatic
building had been off since m1412 after KeptCheck section 38's fixture,
and debt stood at 924% of GDP against a 240% limit. AW1 now starts the
standalone test city and ensemble with automatic works and cash permission.
Its implementation and recovery investigation are in [AW1's notes](AW1-notes.md).

### Scope and retained measure

Merged main `b5008d8693cb56d16ca18cad095f2670d3a582e9` (0.9.98). The merge
is `107d0910` and the count-only cleanup is `98031178`. Generated-document
conflicts took main and the final check regenerated their indexes.
The merge's conflict-comment tail had followed its required author footer.
A lease-protected branch update corrected that commit message and rebuilt
its child with identical trees. The correction record is
`target/checks/rt3-count-footer-correction.txt`.

BusinessInvestment.java is byte-identical to main. AgricultureCheck and
FoodProcessingCheck return to main's holding-based expected quantities.
Main's rule needs neither cap edit. InvestCheck is also exactly main's.
The cap-specific section returned 17 retired shops against its expected 5
under main's rule. That assertion encoded the withdrawn cap and the section
was dropped. The initial named check's exit 4 comes from that single
assertion. Its logs are `target/checks/rt3-count-initial.txt`,
`target/checks/rt3-count-initial-suite.txt` and
`target/checks/InvestCheck.log`.

KeptCheck's window was green on main's rule. It went from 93,154 posts at
m2391 to 94,572 at m2416, with 93,154 the minimum. A second instrumented
continuation counted the actual private retirement flow during m2392-2416.
It retired **zero buildings and zero posts**. The calendar guard therefore
passes without causing retirement, so it was dropped. KeptCheck is exactly
main's. The two diagnostic logs are `target/checks/rt3-count-kept.log` and
`target/checks/rt3-count-kept-cause.log`. Both window probes exited 0.

LongPlaytest retains the printed measurement. Each sector's trailing
12-month window sums posts in buildings actually retired by the spare and
distress rules, divided by its building posts before the window began.
It counts windows above one fifth, contiguous episodes per sector, and the
largest share and its month. A bank branch sold through Retail belongs to the bank and is excluded
from Retail's count. The measurement adds no
model assertion.

The default run reports **646 of 33,699** eligible sector-year windows
above one fifth, **34 episodes**, and a maximum of **150.00%** in Heavy
Industry at m741. Episodes by sector are Agriculture 1, Business Services 2,
Cement & Concrete 1, Copper Refining 4, Freight 1, Heavy Industry 9,
Industry 5, Manufacturing 1, Materials 2, Retail 2 and Wood Products 6.
The share uses actual retirement over the year against its opening holding,
including capacity opened during that year.

### ScaleCheck and known reds

ScaleCheck passes in full on the merged main-rule branch. Every one of the
ten entries assigned to RT3 is green. Its log ends "All checks passed."
The log is `target/checks/ScaleCheck.log`, also covered by the initial
named check's suite log. No entry needed a replacement batch or queued row.

### Default trace and ensemble

Compiled main's exact LongPlaytest source from `b5008d86` into a temporary
class directory over the same main-identical model classes. Ran main and
the count-only harness with their default settings and separate trace
prefixes. Each completed at m2405 with exit 0 and no findings.

All eight CSV traces are byte-identical, covering month, borrowing, housing,
population, bank, bonds, construction and labour. The normal reports differ
only in the added count and wall-time readings. Main took 173 seconds and
the branch 172 seconds. The layout-window wall times were 27.0 and 26.6
seconds. These are soft observations.

The comparison and SHA-256 values are in
`target/checks/rt3-count-trace-comparison.txt`. Run logs are
`target/checks/rt3-count-{main,branch}.log` and the traces use those same
prefixes. The run wrapper is `target/checks/rt3-count-traces-run.txt`.

The byte-identical default traces meet EN1's ensemble-skip condition.
The current baseline is [0.9.98](../ensemble/0.9.98.md). The withdrawn
cap's `docs/ensemble/RT3.md` was removed because the unversioned publisher
requires a batch ensemble file to have a versioned ship record.


### Final picked check

`./batch-check.sh --pick` completed with **exit 3** on 2026-11-13.
It selected **69 of 201** harness names. The runner's substring matching
expanded those filters to **73**, completed in **1,541 seconds**, with 68 passing and five
known-red harnesses. Every reported red is covered by the repository's
existing records. The five are MonetaryCheck, CentralBankCheck, MapCheck,
PlanCheck and KeptCheck. ScaleCheck and InvestCheck passed.

Compilation passed in 34 seconds. Maps regenerated. Stale reported zero
firm findings and 181 soft findings. Sixteen timing assertions were soft.
The check log is `target/checks/rt3-count-pick.txt`; its preserved suite
log is `target/checks/rt3-count-pick-suite.txt`. The Java source tested is
the source in `bd05dc91`, whose Java diff from main is the printed count
in LongPlaytest alone. Readiness uses the picked-check verdict under
`docs/batch.md` step 4.

Map regeneration crossed midnight and updated 565 generation dates in
otherwise unchanged indexes. Those date-only differences were restored,
with the comparison recorded in `target/checks/rt3-count-date-cleanup.txt`.
The substantive generated index changes are retained. The ship record uses
Jerus's requested no-tag harness heading and the 2026-11-12 decision date.
