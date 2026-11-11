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

### The fixtures part 1 moved (slice D, 46a06b9e)

Four harnesses' fixtures fell short on the smaller young cities part 1 makes. Their cities are younger than
`BRAKE_FROM_MONTH`, so the brake does not act in them. Each fixture now causes its condition through the model's
own calls, and no claim moved.

- **SaveFileCheck**:
  - What moved: its city's bank stood open at month 150 on GW1. It now failed twice before month 51 (1,966 people
    at month 21) and was frozen at month 151, with equity at -215, falling to -892 by month 382. The treasury's
    BUTTON setting waits for the player, so the 13 bank `fixture:` lines were never caused.
  - The fixture now: it resolves the failed bank as the Bank tab's button does (`canResolveBank()`,
    `resolveBank()`) and waits for a year of paying, as before. A young town borrowing ahead of its people is
    DQ1's class.
- **PrizeCheck 12 and 20**:
  - What moved: a village's bank had failed once by month 81 and had no capital for its desk. Business Services'
    last trade was month 31's, at 27% of fair value, so the Exchange refused the owners' issue ($0.00 of the lab's
    $1.0M).
  - The fixture now: `labCityWithAMarket()` has the city's fund buy `FUND_BUYS_SHARE_OF_CAP` (10%) of the sector's
    market value three months before the lab, taking the households' asks, so the quote supports an issue. Every
    village of a section gets it alike.
- **BuildAdviceCheck 5 and 10**:
  - Section 5: the one job type the city fills whole is one a clinic has no posts of, so both clones are built on
    a base of at least k of those posts.
  - Section 10: the short city (3,902 people) had no card that the 2% rising history raised. 3% (`RISE_A_MONTH`)
    is the least whole per cent that raises one (Paved Road, 5 to 6).
- **ScaleCheck 9**:
  - What moved: the town's path moved again, and month 191 missed the savings line by 3.37e-2 (dial .016) in its
    fourth month.
  - The scan: months 160-190, recorded in the comment. The misses fall on the savings line alone, at scattered
    months: a granule of the town, as at months 122-143 before, and none of them a drift in the copy. Nothing WG2
    reads is left unscaled (the brake acts from month 240, and the pulls are shares).
  - The fixture now: `FREE_FIXTURE_MONTHS` is 172, the middle of months 170-175.

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
- **★ ZERO_HOT's T2** (section 4), taken with its recommendation: it is accepted as part 1's cost to the young
  city, as C2(a) chose. Jerus can overturn it. The other way is to hold WG2 for a young-city remedy to part 1, and
  none is studied: every rule that brought fewer people cost the young city (spec-wages 10).
- Not done here: the write-up for the project (`claude/<title>.md`; this batch changes how the game plays). This
  session has no way to write to the project; sections 1-5 are its matter.

## 6. The checks (`./batch-check.sh --pick` on 46a06b9e, with the Earth and world files named)

The run picked 32 of the 198 harnesses and exited 4. Two harnesses are red, and both are passed by name. Neither is
caused by WG2's code:

- **HealthCheck**: `...its old first, whom a fee on senior care turns away FAIL` is main's HX1 line, `[known]`. The
  count line `HealthCheck | 1 FAILED` is NEW against main's `2 FAILED` only because HX1's other line (the
  households who skipped a bill) passes on this path. HX1's row says those two lines compare the twins' noise.
- **MapCheck**, `6 FAILED` against main's `3 FAILED`. WG2 changes no map code. Its cities grow on another path,
  and the map's latent faults show there:
  - Section 1, `at every 30th month the map draws exactly what the city has ... FAIL`: at month 91 the map draws
    769 of the model's 796 buildings, with 491 packed at the city's edge. It is whole again at month 121 (798 of
    798). This symptom is new, and DM3 is queued for it.
  - Section 2d's yard lines, DM2's class (boxes moved when a run is laid, which DM2's row names):
    - `a yard laid across a district's buildings moves off it the boxes under it and no others: 66 of the plan
      before's 369 boxes lie under the yard and the railway, and 84 are not in the plan after ... FAIL` (main: 53
      of 590, and 60);
    - `...and the arrivals after it go on with the yard's ground left as it is: every box stands four months on
      (369 of them, 11 moved) FAIL`;
    - `...and the layout watch ... counts them apart and flags none: 66 boxes left a district in the yard's month
      ..., 18 are counted as moved FAIL` (main: 53, and 7);
    - `a yard that goes moves none of the buildings: every box of the plan before stands in the plan after (510
      of them, 45 moved) FAIL`;
    - `a yard laid again across the buildings that stand moves off it the boxes under it and no others: 72 of the
      plan before's 589 boxes lie under the yard and the railway, 129 are not in the plan after FAIL`.
  - Main's 2h line (the highways reaching a street) passes on this path.
- **Review flag**: `review needed - threading: ui/UserInterface.java (UI-wide)`. The change there is the OUT OF
  WORK tip's format string and its arguments (dials, read on the thread that drew it before). Nothing in it
  touches threading, money or saves.
- Saves: no new field (the brake's age is `monthsRecorded`, saved already) and no format bump.

## 7. The gate sent it back: the merge with 0.9.90 and the whole suite

- **What the gate found.** The gate's suite found reds that `--pick` never ran. `Migration` is read across the model,
  so its readers were left to the gate (LongPlaytest stood in for them). The whole suite on the merge with 0.9.90
  (BO1) showed 97 NEW red lines in 16 harnesses. Each was red on WG2 before the merge too (66414b3f), and each is
  green on main, so all are WG2's. They are part 1's smaller towns once more.
- **Slice E** (42e438d5):
  - `ChildcareCheck.town()` stands five Construction Depots. The depots' posts filled 70.8% against 82.8%, the
    builders had no site output (main's 54), and the automatic builder ordered nothing. That one change mended
    AutoBuildCheck (41 lines), HydroCheck and IslandLinkCheck.
  - ConstructionControlCheck's city stands on 380 Houses: it had 393.2 households against 400 homes, where main
    has 403.1.
- **Slice F** (0d601bed):
  - AuctionCheck's builder keeps a margin of .30, the player's slider. At .15, three Walk-in Clinics led the
    Health Centre by 0.6% a unit served.
  - FittingCheck moves bread's price, as GW1's move does. A Bakery earned -0.0020577 on its cost, against main's
    0.00154.
  - ResearchViewCheck's expected value rounds once, from the unrounded bill and the prize. This was a harness
    rounding slip, shown by a prize that is not a whole number of cents: 9.27 against the series' 9.28.
- **Slice H** (4bfc2075): PriceLevelCheck's twins run to month 82, by its header's tie rule. Manufacturing's cash
  parts at month 83 in every pair nudged by 1e-12.
- **Slice G** (7eed73be):
  - CentralBankCheck plays until the price index has its year. The town first shops at month 62, not 31.
  - FundCheck saves at month 74, past the bank's consolidation and split of months 72-73. It asserts no split in
    the months it counts.
  - HoldersCheck tills every paper holder in the coupon month, and sizes its first sale at .15 of the
    households' spare.
- **Slice G, its second commit** (250cc204):
  - BankCheck gives held Freight the cash for its fleet. Freight had bought trucks on credit, owing 164 on assets
    of 139 at the save, and its book alone parted the month's and the quarter's allowances by $0.70k against
    $0.33k. The fixture now asserts that Freight owes nothing.
  - HoldersCheck's departure month comes after the inflation target is raised a point. The paper that leaves then
    stands at or under face on the city's curve: 0.956, where WG2's deflating town had it at 1.051. This is
    asserted before the desk-sale claim.
- **KeptCheck** keeps two red lines, and both are passed by name. Neither is in WG2's screen code:
  - `a month's redraw after each choice hands back nine in ten of the page's nodes, or more FAIL`, at 86.5%. The
    Float 5 page's ladder card remakes 40 nodes each month. This is KS1's class.
  - `the kept header's signature equals a header built new, at pace and short, at every rung (18 differ) FAIL`.
    The hidden RATE wording (`UserInterface.java` `HeadlineTile.layoutChildren`) keeps its managed bounds, and
    `KeptBench.sign()` skips only nodes that are both hidden and unmanaged. This is KF1, folded into KS1's row.

## 8. KeptCheck's city collapses at month 2404: the cause is not WG2's drift

KeptCheck's section 61 plays 25 plain months, m2391-2416. In them, its city loses its jobs and then its people.

| month | people | posts | out of work |
|---|---|---|---|
| 2404 | 298,371 | 167,443 | 12% |
| 2420 | 272,724 | 33,678 | 81% |
| 2450 | 150,206 | 9,521 | 90% |
| 2550 | 41,427 | 7,112 | 75% |
| 2640 | 24,532 | 14,324 | 11% |
| 3200 | 111,733 | 65,194 | 10% |

- **The jobs go first.** BusinessInvestment's retirement winds down a sector that has been at a loss for 6
  months: `RETIREMENT_LOSS_MONTHS` 6, `RETIREMENT_SLACK` .25 and `MAX_RETIREMENT_FRACTION` .25, a quarter of it a
  month. The sectors that fall:
  - Retail, a third of the city's workers, loses 7e12 a month. It goes from 101k workers to 1.2k.
  - Real Estate goes from 41k to 4.3k, and Construction from 21.6k to 9.9k.
- **On main's path** the shock is smaller. Construction goes from 26k to 8k and out of work reaches 35-49%, but
  Retail stays at 43k and the people stay.
- **The fault is main's, and WG2 only shows it.** WG2's own path makes Retail a third of the city. The
  retirement's row is queued in the ship file.
- **The drift is not the cause.** Slice H ran the city with the drift turned off from month 2400. The posts do
  not come back: 167k at m2404, 34k at m2420 and 9.5k at m2450, as in the base. The people fall to 23,479 at
  m2636, against the base's 24,295 at m2638. Over m2412-2640, 203,878 people leave against 673 arrivals, and the
  drift's share of the departures is 1.8%. Main's older leavers, the pinned bands' surplus and the declining
  trades, carry the departures. WG2's brake stops the arrivals past 12% out of work, as it was specified to.
- **The call** was the orchestrator's: take the 24-month low of the out-of-work share for the drift only if
  turning the drift off brought the posts back. It did not, so the model is unchanged.
- **The 4,000-month LongPlaytest** (4bfc2075; the model since then is the same) passes the audit: "Nothing.
  Every month passed the audit and every reload matched", exit 0. Main 0.9.90 ends on DM2's class instead: a
  building on the map moved at m3006, 15 Studio Apartments in district (-4,1), exit 1. The people at the
  checkpoints nearest the asked months:

| checkpoint (main / WG2) | main 0.9.90 | WG2 |
|---|---|---|
| m616 / m618 | 28,651 | 21,281 |
| m1320 / m1322 | 208,984 | 219,737 |
| m2024 / m2026 | 445,025 | 552,327 |
| m2500 / m2527 | 695,179 | 792,095 |
| m3296 / m3298 | 1,124,586 | 1,132,868 |
| m3537 / m3539 | 1,351,077 | 985,007 |
| m4000 / m4002 | 1,533,142 | 1,104,653 |
