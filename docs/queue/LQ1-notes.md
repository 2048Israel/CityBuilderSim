# LQ1 - the luxury queue's size: notes

The batch is docs only: `docs/studies/luxury-budget.md`, its probe, run and
report scripts, two prototype patches, the real figures with their sources and
the runs' tables in `docs/studies/luxury-budget/`, the study's row in
`docs/studies/README.md` and four rows in `docs/studies/licences.md`. No code,
no harness, no version (no {next}; ships untagged). The brief is the queue's LQ1
row, from LX1's notes: on 0.9.138 Luxury Retail's margin is 10-30% of the
business sectors' value added, and at BEA's payroll share of it (LX1) the shops
employ a quarter or more of the old city's workers. What should the budget be?

## What was read

- **The budget:** `Household.plan()` (L1037-1190): `fromWealth`, `want`, the
  surplus, `luxuryWant` (L1135) and `mealWant` (L1170), and the comment "AND THE
  FORTUNE ALSO ASKS FOR WATCHES".
- **The dials and the queue:** `HouseholdBalance`'s `WEALTH_SPENT_A_MONTH`,
  `LUXURY_SHARE_OF_SURPLUS`, `MEAL_SHARE_OF_SURPLUS`, `MEAL_SHARE_OF_WEALTH`,
  `MARGINAL_PROPENSITY` and `SHARE_CUSHION_MONTHS`, and its
  `luxuriesWanted`, `luxuryBudgetOf`, `takeLuxuries` and `cells()`.
- **The shops:** `LuxuryRetail`'s header, `MARGIN_FLOOR` and
  `MARGIN_CEILING`, `strikeMargin`, `plan` and `estimatedMonthlyProfit`.
- **The planner's cost:** `BusinessInvestment`'s `getCostOf`, `totalCostOf`,
  `orderSize` and `TARGET_HEADROOM`.
- **The buildings and the good:** `buildings.json` ids 69 and 70,
  `BuildingManager`'s built-ins (L1714-1760), `Good.LUXURIES`, and
  `consumption.json`, which has no luxury line.
- **The readers of the dials:** `LuxuryRetailCheck`, whose settled margin
  comes from the dials (L87), and `ui/SectorScreen.java` L6785-6790.
- **The accounts:** `HouseholdAccounts.getDisposableIncome` (it excludes
  investment income) and `NationalAccounts`.
- **The studies:** spec-labour-share (LS1) and its run.sh, which this
  study's run.sh adapts; LX1's notes (REV-LX1-1's Department Store);
  spec-wealth 3.6, which keeps `WEALTH_SPENT_A_MONTH` as the pile's bound.
- **The real figures:** a research agent fetched them, each with its
  licence (`luxury-budget/real/sources.md`). I checked the CSVs against the
  downloads and added BLS's 441 and 445 rows (`employment.csv`).

## What was found and decided

**The runs.** Main 0.9.139 (`c471e961f`):
- seed 0 to m3996, seed 1 to m2400 and Rotterdam to m2400, through
  `LuxuryProbe` hooked after `g.simulateMonths(1)` in a copy of
  `LongPlaytest`;
- each prototype on the same three, and seed 0 to m4000.

At most three ran side by side, with no ensemble and no suite.

**The budget is the wealth term.** It is 99.7-99.9% of the budget:
- 60% of disposable income at seed 0's median (41-69% by run), and 132% in
  its old city;
- 13-19% of GDP (36% in the old city), against BEA's other goods' 12.8% of
  DPI and 9.5% of GDP.

The gates pass 98-100% of it. The surplus term is nil for anyone with a
fortune, because `want` carries the wealth term too. It is a flat share of
wealth: the unskilled spend 29-45% of their income at the counter, as the
college-trained do, and the retired seven times theirs in the old city.

**Why it runs high.** Two factors multiply:
- the counter takes the whole wealth effect, where other goods are about
  0.41 of the US's (2.4 times);
- the pile is 14.7 years of disposable income against the Fed's 7.9 (1.9
  times).

Together, 4.6 against the 4.7 measured. The rate, 4% a year, is the
literature's and spec-wealth's bound: kept.

**The shops' margin settles at 2.66**, a fixed point of the floor, the
ceiling and `TARGET_HEADROOM`. A gross margin of 62%, against the specialty
stores' 1.92 (48%, ARTS 2022). Their value added is 8-11% of GDP against
BEA's other retail and general merchandise's 4.0%. LX1's payroll share holds
(35-42%).

**Prototype 1, the share at 0.4:** the budget and the shops come in line,
and the pile grows to 1.3-2.4 times main's net worth over GDP by m2400.
Decided: no cut without a second drain (★2).

**Prototype 2, the ceiling at 2.5,** settling at 1.89, with the coverage
re-struck so payroll over margin holds. The shops' workers fall 23-45% and
their value added 13-35%; the spend and the pile stay within the seeds'
noise. The share of the people at work stays in 38-47%, main's own range.
Decided: LQ2.

**The old city** (seed 0, m3996 on each): <!-- OLD CITY NOTES -->

**The Department Store** covers 62% of a Boutique's pieces a dollar, with
more ground a piece and the same posts a piece, so it is never chosen. By
the Census it has no edge in costs: $223k of sales a worker against a
jeweller's $427k. Decided: leave it (★3).

**The heading.** Two em-dashes, as `docs/batch.md` step 5 and MED1's and
MA1's ship files have it; the brief wrote one.

**main merged in** (MA1, KR1) before the licence rows went in, so they
follow MA1's rows.

## The calls

Each is in the study's section 8 with the other ways in plain words, and
each is recommended (a).

- **★1 The counter is BEA's other goods** (14% of spending), not jewelry and
  watches (0.5%). Why: the goods whose share rises with income are the
  dial's own definition, and the shops' posts and margin are then the
  specialty stores'.
- **★2 All of the wealth term stays at the counter for now;** LQ3 studies
  the second drain, and the cut follows it. Why: measured, a cut alone grows
  the pile; spec-wealth keeps the rate.
- **★3 The Department Store is left unchosen.** Why: the Census gives it no
  edge in costs, and the accounts would not change. Way (b), a scale edge,
  has its batch's shape in the study, unqueued.

## Batches queued (the ship file's `## queued`)

- **LQ2**, the margin at the specialty stores' (★1 (a)), one builder:
  - `MARGIN_CEILING` 2.5 and its comment;
  - the Boutique's and the Department Store's coverage and stock, in
    `buildings.json` and the built-ins;
  - `SectorScreen`'s `%.0f`;
  - `LuxuryRetailCheck`, and the probe rerun.

  Pick likely ALL, so a dry run. Not beside QN1.
- **LQ3**, the fortune's other spending, a study (★2 (a)), after LQ2.
  Docs only, beside any.
- **Pending LQ1-K:** the three calls, which LQ2 and LQ3 take as recommended
  unless Jerus says otherwise.

## Process

- No dry run: the pick is NONE (docs only).
- `./batch-check.sh --fast` is the check: <!-- FAST -->
- **The incident.** Seed 0's first run printed "line 53: unexpected EOF"
  because I edited `run.sh` while its bash was reading it. Its Java had
  finished ("All checks passed"; the CSV runs to m3996). Since then the runs
  go from a copy, `target/lq1/runner/run.sh`.
- **The exits.** All three seed 0 runs exit 0, and so does main's seed 1.
  The rest exit 1 on the playtest's own watches:
  - "industry stands farther out than the homes" at m2400, CL7's class:
    Rotterdam on main and on prototype 2, and seed 1 on prototype 1;
  - spells of "households with no home", HN1's class: seed 1 on prototype 2
    (m937-1046), and Rotterdam on prototype 2 (m619-651) and prototype 1
    (m724-824).

  The probe only reads, and these are the paths' branches. LQ2's brief says
  to trace the second if its own playtest shows it.
