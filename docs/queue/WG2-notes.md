# WG2 - senior care's excess, and the out-of-work verdict ({next}, 2026-11-10)

The brief is WG2's row in `QUEUE.md` and [the wages study](../studies/spec-wages.md) §3.4 and §6, Jerus's call C2(a)
(2026-11-02: "Cut the excess, honest label (Recommended)"), with the staffing-gate study's C4(a) (settled by the
orchestrator 2026-11-09): the brake and the pull rule apply to `Migration.drawFor()` too. Lane B, after GW1 (0.9.89).
A model batch: Opus, with Sonnet slices for the code.

## 1. The rule

- **The pulls lift the homes' draw only** (part 1): `target = JOB_WEIGHT x jobs x residents a job x min(1, rent x
  crime) + HOME_WEIGHT x homes x senior x rent x crime` (`Migration.jobsPull()`, `homesPull()`). Senior care's
  x1.30 lifted the whole target before, the jobs' draw with it: 1.46 workers a post in a city with full care, the
  21% of his cities (spec-wages §2.4).
- **Arrivals slow past `Migration.HEALTHY_OUT_OF_WORK` (.06) and stop at `ARRIVALS_STOP_OUT_OF_WORK` (.12)**, a
  straight line between (`arrivalBrake()`, part 2), read off the month's own unemployment as migration is struck.
- **The jobless past the healthy rate leave** (part 3): `(unemployed - .06 x labour force) x SURPLUS_DEPARTURE_RATE`
  a month, less those the pinned bands sent. Two choices the prototype left open, taken here: the excess is counted
  on the **labour force** (what `getUnemploymentRate()` divides by; the prototype multiplied by the workforce, which
  counts the students and prisoners and overstates it), and the leavers are spread on each band's **surplus over its
  own posts** (`surplusInBand()`: who is out of work at their level), not on the city's band share.
- **Parts 2 and 3 from the city's 240th month** (`Migration.BRAKE_FROM_MONTH`, `brakeApplies()`,
  `arrivalBrakeNow()`): before it the young city's share swings with every works that opens, and a brake read off
  it cost the city two thirds of its people at month 240 (section 3). The age is the months `recordWages()` has
  counted, saved with the rest.
- **The gate's draw reads both** (GW0-C C4a): `drawFor()`'s jobs' pull is `jobsPull()`, and the people it draws are
  braked after the room cap, as the month's arrivals are. No new saved figure: the pulls are saved
  (`LAST_SCALARS`), and the brake reads the city's unemployment, which a load restores.
- Part 4 (vacancies drawing people) is left out, as measured (spec-wages table 2). `TARGET_LABOUR_SLACK` stays .125.

## 2. The words

- The verdict: green 3-7%, amber to 12%, red past it (`PeopleScreen.OUT_OF_WORK_HIGH` .07, `_FAR` =
  `Migration.ARRIVALS_STOP_OUT_OF_WORK`), the header's tip with what migration does at 6% and 12% and the cities
  beside it (spec-wages S4, the mockup's wording), "once the city is 20 years old" from the dial. SummaryScreen's unemployed line read a bare `.15`; it reads the
  dial now.
- The People screen's draw card drew `(jobs + homes) x the pulls`; it draws each half with its own pulls (the
  "jobs and homes" box went: nothing else read `getLastDrawBeforePulls()` on the screen). Its why-sentence gains
  "slowing" and "stalled" (the brake under 1, and at 0), put before the plain "leaving": past 6% the jobless drift
  away every month, so a "leaving" first would have hidden the brake in every month it bites. "Leaving" keeps its
  own place when a trade is dying; with none dying it names the drift (or crime and empty pockets under 6%), and
  "settled" no longer says a shortage of work sends nobody away. Its (i) says what the pulls do now.
- Senior care's words ("draws 30% more people to the city") say the homes' draw: Inbox, BuildScreen (two),
  PeopleScreen's care line, ServicesScreen's line, scale and info, Healthcare's comment.
- The six cities' sources: `docs/studies/licences.md` (a subsection under 1) and `tools/world/ATTRIBUTION.txt` (a
  block). The OGL's and Statistics Canada's credit lines are those licences' own stated forms; the study recorded
  no page address for Tokyo's or the ONS's figure, and the rows say so.

### The fixtures the brake moved

- **StaffingDrawCheck**: its village was every adult out of work, so the brake read 0 and 22 assertions drew
  nothing. `seat()` now employs a no-diploma base so the village sits at or under 6% (asserted, `drawIsClear`), with
  the spare share unchanged; sections 8-9 multiply by the brake; section 10 is new (the pull rule: senior care
  draws the same to the bit; the brake: whole, the middle, past the stop nothing and the order refused).
- **HealthCheck 9** restated: caring minus plain is `HOME_WEIGHT x homes x SENIOR_CARE_PULL` (.30), the other pulls
  premised at 1. Its shelf section (L448) lost its premise when the brake acted from month 1 (the played city
  1,655 people against 2,094, the shops' hands no longer binding); the fixture now stocks the shelf x10, so the
  condition is caused, not inherited. From month 240 its 36-month city has 1,872; with senior care lifting the
  jobs' draw in the target again it has 1,901, so part 1 there is 29 people, and the rest of the gap to 2,094 was
  not measured (2,094 may be from before GW1).
- **PopulationCheck**'s WG2 section (three parts, each fixture causing it); its slack premise held as written.
  The braked and leaving fixtures are old cities (`oldMigration()`: `BRAKE_FROM_MONTH` months of steady wages,
  asserted); young twins past the stop and at 20% out of work assert the brake and the drift do not act, and the
  boundary at 239 and 240 months is asserted. StaffingDrawCheck 10 ages its villages the same way (`makeOld()`) and
  adds (c): a young village past the stop draws the unbraked draw, whole, and passes on it.
- Not covered by a fixture: the leavers' fallback to the band shares when no band has a surplus over its posts
  (unemployment past 6% with no band over its posts is a contradiction the model should not reach).

## 3. The young city, measured first

The ensemble's 16 AUTO2 cities (`tools/ensemble/ensemble.sh`, 2,400 months each), each seed's people over GW1's
on the same seed (main's run, `docs/ensemble/0.9.89.md`'s CSVs), medians, with the seeds smaller than their twin;
the share out of work as the seeds' mean over the span, medians. Variants as uncommitted prototypes of slice A's
`Migration`, switched by properties, on a copy of its classes.

| variant | people m120 | m240 | m480 | m1,200 | m2,400 | out of work m24-240 | m240-600 | from m600 |
|---|---|---|---|---|---|---|---|---|
| GW1 (main) | 1 | 1 | 1 | 1 | 1 | 6.0% | 12.9% | 10.7% |
| **WG2 as first written** | 0.36 (16 smaller) | 0.29 (16) | 0.40 (16) | 0.68 (12) | 0.96 (10) | 10.6% | 8.4% | 8.5% |
| parts 2-3 without part 1 (to read the cause) | 0.47 (16) | 0.49 (16) | 0.78 (14) | 1.05 (6) | 1.14 (4) | 10.3% | 8.4% | 8.3% |
| the brake from 8% to 16% | 0.37 (16) | 0.31 (16) | 0.61 (15) | 0.84 (11) | 1.00 (8) | 10.7% | 9.0% | 10.2% |
| **the brake and the drift from month 240** | 0.76 (15) | 0.68 (15) | 0.79 (14) | 0.88 (9) | 1.00 (7) | 5.4% | 8.9% | 8.6% |

- **As first written the young city paid**, as the wages study feared and the gate study (GW0-C C4a: "with the draw
  first WG2's aim costs the young city nothing") did not: 0.29x at month 240, and more of it out of work, not less
  (10.6% against 6.0%). A village of a few hundred swings from 0% to 48% out of work as each works opens and the
  next waits (seed 0, months 12-48: 36%, 0%, 32%, 48%, 0%); a brake read off the month's own share stops arrivals
  in every spike, and the gate's draw, braked with it, stops counting the workers who would come.
- **Why the gate study saw no cost.** Its `d12aim` was `wages.brake=.06/.12,wages.leave=true` on the 12-month draw
  (`docs/studies/staffing-gate/out.md`, row 19): parts 2-3, not part 1. Without part 1 here the young city is still
  0.49x at month 240, so part 1 is not the whole difference. One difference left between the two is that GW1's
  shipped draw caps an order's people at the room migration has left (`lastRoom`), which the prototype did not;
  it is not shown to be the cause. Not chased further: the fallback the wages study named first answers it.
- **The call (a recommendation taken, spec-wages section 6: "a brake that starts later (from month 240, or from 8%
  to 16%) is the first thing WG2 tries").** 8%-16% does nothing for the young city (its spikes pass any stop) and
  leaves the grown one at 10.2%. From month 240 the young city keeps 0.76x and 0.68x, is *less* out of work than
  GW1's (5.4%), and the grown city takes the whole of the cut (8.9% and 8.6% against 12.9% and 10.7%), the final
  city level. `Migration.BRAKE_FROM_MONTH` (240), on the months `recordWages()` counts (saved: no new field).
- **What is left of the young city's gap is part 1**, by construction: before month 240 nothing else of WG2 acts.
  Senior care no longer lifts the jobs' draw (x1.30 in a village with its care home), which C2(a) accepted:
  "smaller in its young decades".
- AQ1's seed (AUTO2-2, GW1: 50.1% out of work in months 560-700, a 12-month deflation spell ending at month 632):
  14.0% and 7 months as first written; 12.2% and 3 months from month 240. A3 passes in every seed in both.

## 4. The ensemble (`docs/ensemble/WG2.md`, 843ebffb, 80 of 80 runs)

24 of 26, against GW1's 24 of 26. The AUTO2 runs are byte-identical to the month-240 prototype's (section 3).

| measure (median [min to max]) | GW1 (0.9.89) | WG2 |
|---|---|---|
| AUTO2 inflation m240-end, a year (A1) | 2.08% | 2.24% |
| AUTO2 young city, inflation m240-600 | 2.74% | 3.18% |
| AUTO2 founding m120-240, longest spell of yoy > 10% | 13 [0 to 27] | 4 [0 to 20] |
| A2: longest spell of yoy > 10% from m240 (< 12 in every seed) | 10 [7 to 24], FAIL | 10 [8 to 26], FAIL |
| A3: longest spell of yoy < -10% from m240 (< 12 in every seed) | 1 [0 to 12], FAIL | 2 [0 to 8], PASS |
| H2: ZERO_HOT m342-402 minus its twin | 0.13%, 15/16 | -0.27%, 13/16 |
| AUTO2 bank, months under `CAPITAL_RATIO` to m600 | 2 [0 to 21] | 0 [0 to 8] |
| AUTO2 out of work m24-240; m240-600; from m600 | 6.0%; 12.9%; 10.7% | 5.4%; 8.9%; 8.6% |
| AUTO2 final population (T2) | 618,792 | 592,212 |
| ZERO_SLACK final population | 409,599 | 402,986 |
| ZERO_HOT final population (T2, at least 75% of GW1's) | 12,898 | 9,518, FAIL (73.8%) |
| SHOCK final population | 34,660 | 28,526 |
| HIGH final population | 14,570 | 11,957 |

- **AQ1 clears on this run.** Its seed, AUTO2-2, peaks at 12.2% out of work in months 560-700 (GW1: 50.1%) and its
  longest spell under -10% a year is 3 months (GW1: 12); A3 passes in every seed. The town is a different town by
  then (18,684 people at month 600 against 20,534), so this is the bust not happening, not the bust explained:
  AQ1's question (what laid off 30 points in ten months) is unanswered, and the row is the orchestrator's to close
  or keep.
- **ZERO_HOT's T2 is new red**, 73.8% of GW1's against the 75% floor. ZERO_HOT's city ends at month 402, a young
  city, and tracks its AUTO2 twin (96.5% of it): this is part 1's cost to the young decades, which C2(a) accepted
  ("smaller in its young decades"). It is above 0.9.83's 9,262; GW1 had raised it 39%. Not moved: T2 is the
  ensemble's premise.

## 5. Calls

- Taken, with its recommendation (spec-wages section 6): the brake and the drift from month 240 (section 3).
- **★ ZERO_HOT's T2** (section 4): accept it as part 1's cost to the young city, as C2(a) chose (recommended), or
  hold WG2 for a young-city remedy to part 1 (none is studied: every rule that brought fewer people cost the young
  city, spec-wages 10).
- Not done here: the write-up for the project (`claude/<title>.md`; this batch changes how the game plays). This
  session has no way to write to the project; sections 1-5 are its matter.
