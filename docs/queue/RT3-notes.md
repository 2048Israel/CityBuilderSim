# RT3 ? BusinessInvestment's retirement in a falling city

**Held for Jerus's decision.** Lanekeeper's instruction
`20261112-122907-214-lanekeeper-f43d`, 2026-11-12, requires every criterion
passed by the baseline to pass. AUTO2 F1 and ZERO_SLACK F1 regress. The
full gate awaits that decision. The complete instruction was read at 12:46.

## Model and calls

? The built candidate applies MAX_RETIREMENT_FRACTION to the retireable excess, after
keeping demand and TARGET_HEADROOM. Round up to a whole building so a small,
genuinely spare holding can leave. The old holding-based pace could remove a
modest surplus in one month. Lost payroll then reduced demand for the next
retirement. Each sector already supplies its own demand measure. An estimate
of demand at usual employment would require an additional counterfactual for
those different measures. Distress uses its solvency test and holding-based
pace.

? Final recommendation is to hold this candidate and review the usual-
employment demand option before acceptance. The same-state diagnostic below
shows that the cap slows the original collapse while still losing 52.4% of
posts. Its successful full-history replay depends on earlier path changes.
This supersedes the initial recommendation of the excess cap. The latter was
chosen for its direct relation to spare capacity and existing sector measures.

The whole surplus can span several building kinds. AgricultureCheck and
FoodProcessingCheck keep their two-kind fixtures and calculate the permitted
quantity from that surplus. InvestCheck exercises modest and whole surpluses,
headroom, the building granule, orders in flight and the rolling counter.

? Use the row's one-fifth loss measure for KeptCheck's regression guard. It
checks the minimum posts throughout m2391?2416 against four fifths of the
opening posts, and asserts that the window was reached. The calendar is
explicit because section 61 starts at m2377 on current main's path.

LongPlaytest counts retired building posts over trailing twelve-month windows
against each sector's standing posts before the window began. Both retirement
rules contribute. New hires do not conceal the retirement flow. It reports
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
  posts to 21,635 over m2377?2402. Twenty timing assertions are soft. Maps
  regenerate and Stale reports zero firm findings.
- Final `./batch-check.sh --pick` is running, selecting 99 of 201 harnesses.
  Its log is `target/checks/rt3-pick.txt`.

## Ensemble and GDP finding

The changed retirement path requires the ensemble. `tools/ensemble/ensemble.sh
RT3` completes all 80 runs, exit 0, on the model snapshot `40ea73b0`, in
19.6 minutes. That BusinessInvestment model is the batch's model. The kept
baseline is `docs/ensemble/RT3.md`, using `{next}`. It scores **22/26** against
**23/26** in `docs/ensemble/0.9.93.md`. A2 and A3 remain red. HIGH R3 turns
green. AUTO2 and ZERO_SLACK F1 turn red from the same seed 11, m902 finding.

GDP there is -431.688583, with a passing money audit and no retirement in
m899?903. The accounts read C 8,921.046, construction 0, inventory -236.426,
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
limit. The median annualised index-growth difference over m660?720 moves
from -2.15 to -2.50 points. Over the whole m600?720 intervention, HIGH R1
reads -3.53 points against -2.91 in the baseline, and R2's unemployment cost
rises from 0.83 to 1.20 points. The changed capacity/employment histories
produce a stronger measured disinflation response relative to AUTO2 twins.
The reduction in late inflation outliers is the direct reason R3 turns green.

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
