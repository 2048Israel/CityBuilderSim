# OG1 - a planner orders to the gap: notes

Built 2026-11-22 by Claude (Opus 5.5, headless) on `batch/OG1`, cut from origin/main 827bc03f7 (0.9.181).

## 1. What was measured (main's classes, before any change)

The probe is `OG1Probe` (kept as `docs/queue/OG1-evidence/OG1Probe.java.txt`; it compiles into `ham.citybuildersim` against the classes): it records every month the builders' Decision
words, the backlog in months (`getRemainingConstructionPoints() / getBuildingOutputAtEveryPost()`), the
premium, the foreign crews, the depots on site, the best depot's staffing share now and with the draw, the
plots; and for every sector its last investment line, its market's demand trend, imports and exports, its
capacity and pipeline, and what it asked of `consider()` against what it got (`OrderWatch.invested`).

### 1a. Slot 10's copy (m1204, 421,561 people, Bergen, founded the 1900 way), 48 months

Copied into a `GameFiles.scratch` folder and loaded headless; slot 10 itself never written.

- **The builders' plan held "a depot on site already" all 48 months**: 739 depots were on site at the load
  (ordered before it; their plan orders one at a time), opening about 15 a month. The backlog went
  117.1 -> 69.1 months (52.9M -> 44.9M points) while the sites went 451,546 -> 649,952 a month, and the
  people 421,561 -> 619,469. So the long backlog does shorten on its own here, by the depots already on site.
- **Behind it, staffing**: 0.0% out of work every month; the best depot's share 0.66 -> 0.57 with a year's
  draw (0.30 now) against `MIN_STAFFABLE_TO_ORDER` 0.80. The builders' posts 88.5% -> 90.4% filled.
- **The size of the queue is labour, not depots.** A depot is 400 points to build, 400 a month made, 50
  posts. 52.9M points to the knee in a year is about 11,000 depots, 550,000 posts, in a city of 421,561.
  The premium was 0.76 of the ceiling's 0.80: the price is rationing the orders, as BA1 meant.
- **Every maker but one is held by staffing**, the words "the city could staff 25-38% now and 61-74% with a
  year's arrivals; it wants 80%" (Automotive, Copper Mining, Food Processing, Freight, Materials, Mining,
  Restaurants, Heavy Industry 35 of 48, Wood Products 21): the cause in his city is staffing.
- **Manufacturing is the one-a-month maker there**: it ordered one Fabrication Works in 38 of 48 months
  (41 asked, 36 found by `consider()`).
- **Housing is held by the landlords' own funds**, not one a month nor the builders' queue: Real Estate built
  every month (47 of 48) and each line reads "Built 4-9 Low-Rise Apartments on an insured mortgage (trimmed
  from 186-2,061 - its own funds put the 15% down on 4-9)". That is the finance gate the real-estate study's
  probe pointed at (planner demand of 142 at m605 there): for study/real-estate, not this batch.

### 1b. The playtest (`LongPlaytest.probeCity` and the playtest's own loop), 2,424 months

- Unemployment about 10%, the depot's share 1.00: depots are staffable.
- Past 9 months of backlog the builders' plan read: built one depot 205 months, "a depot on site already"
  203, no land 71, staffing 7. 685 months past 9 (470 with over 100,000 people); 18 spells of six months or
  more, the longest m1775-m1863 (89 months, max 31.4), then
  m2140 (81 months, max 33.1), m2238 (73, max 49.1), m1534 (66, max 39.6), m1886 (50); maxima 15 to 49 months.
- In m1775-m1863 the builders went 37,040 -> 58,240 points (one 400-point depot a month) against sites of
  15,000-28,000 a month and a queue of 320,000-785,000 points; the premium reached 0.37.
- One-a-month makers, longest runs of consecutive "Built 1": Manufacturing 441 months ordering one (65
  running), Construction 284 (42), Food Processing 214 (21; imports of its planning good 48,000-98,700 a
  month from m1890, 4-8 plants short), Automotive 121 (21). Freight 20 (5), Heavy Industry 7, Cement 5.
  Business Services never ordered (licences); the mines, Oil, Coal and Forestry held on deposits, money or
  staff, never one a month.

## 2. What changed

- **The builders order to the work coming in** (`Construction.depotsForTheQueue()`, `sitesMonthWanted()`).
  The four conditions stand as they were (a depot of theirs on site holds the plan; 9 months of backlog;
  staffing at `MIN_STAFFABLE_TO_ORDER`; land). Past them the order is no longer one: it is the gap between
  the sites' month and a target, in depots of the template's nameplate at the share of its posts the city
  could staff. The target is the work a typical month orders of the builders, plus the queue past the knee
  over `QUEUE_WORKED_OFF_MONTHS` (24), and never more than `1 + BuildersShort.CEILING` (1.8) times that
  typical month. A batch's points take at most the premium's share of the sites' month
  (`BuildersShort.premium()`, the queue's own price for its wait). The order is at least one, and no more
  than the city could staff together (`staffableCount()`) or has plots for.
- **The work coming in is recorded and saved** (`BusinessInvestment`, THE WORK THE CITY SENDS THE
  BUILDERS). Each month it is the points owed on everyone else's sites as the advance begins, less what
  they owed when the last advance ended (`SimulationEngine`, either side of `advanceConstruction()`). That
  is the orders placed between the two: the businesses', the city's and the player's, with the builders'
  own depots and yards left out, so an order of depots is never read as work that calls for more. It keeps
  `TREND_WINDOW` (12) months. The typical month is their median and needs `MIN_MONTHS_OF_SITE_WORK` (6)
  months; before then the order is one depot, as before.
  - Saved as `DataSave.buildersWork` and `buildersOwedAfterAdvance`, read in the load path beside the
    population trend. No format bump: an old save has neither, starts the record again, and orders one
    depot an order for six months (saves never matter, `docs/contract.md` 1).
- **Food Processing orders as many plants as the city already eats**. `fillFor(unit, before)` counts the
  n - 1 plants before the n-th, and the plan orders the largest n whose n-th is `FILLED` (.85). That is
  cut to the plots and to what the city could staff together (`BusinessInvestment.gapOrder()`, found by
  halving with `largestThatPasses()`). `consider()` then rations it as BA1 meant.
- **Everything else still orders one.**
- `GapOrderCheck`, new: seven sections, 34 assertions (sections 5 and 6 caused, after REV-OG1, see 4); registered in `AllChecks` beside
  `BuildersFirstCheck`, and its line is in `docs/tree.md`.

## 3. Why this does not repeat 0.7.130

0.7.130 measured two failures of depots ordered "sized to the queue". Alone, the depots sat in the queue
and the city collapsed. With the depots served first, the builders ran away to ten times the depots.

- **The first cut repeated the runaway, and was thrown out.** It sized the depots to bring the queue to
  the knee in 12 months, capped at a month of the sites. On the playtest it reached **7,771 depots at m2400
  to main's 299, 3.72 a thousand people to 0.56**, its sites doing 1.7 million points a month with nothing
  queued. The cause:
  - The depots, served first, open inside the month, so the plan ordered again every month on the same
    long queue.
  - A queue-sized target keeps the builders growing until the queue is under the knee at their new pace.
    The depots outlive that queue, and a subsidised builder never sells one (`planRetirement()` needs six
    loss months).
  - One playtest move put 18 million points on site at m2374, and the builders grew to clear it, then
    stood idle.
  - It also churned the makers. Sized to their supply bound (the spare workers, the steel), Manufacturing
    found 8,107 plants and Automotive 5,928, to main's 502 and 339. Manufacturing stood at 7.1 million a
    month to main's 1.4.
- **Not the runaway: the target is the work coming in, not the queue.** A spike is not built for. The
  median does not see one month's 18 million (`GapOrderCheck` 2), and the target can never pass 1.8 times
  the typical month, however long the queue (`GapOrderCheck` 1). The 860,000 points a playtest move put on
  site at m1088 (195 months of the sites) met a typical month of 0 and got one depot a month, as main
  does. It cleared in 92 months.
- **Not the collapse: one order is never months of the sites.** An order takes at most the premium's share
  of one month (at most 0.8). Served first, it opens inside the month, and the rest of the queue keeps the
  rest. That bounds one order, not a run of them. Once the depots open, none of theirs is left on site, and
  next month's plan may order again on what is still a long queue. So back-to-back batches can each take up
  to 80% of the sites' month until the gate shuts (the queue under the knee, or the share under a depot).
  Counted on the playtest from the probe's rows, each order's points against that month's sites:
  - The longest run of consecutive months each ordering several was 7, m1846-m1852 (5, 4, 6, 30, 18, 3 and
    10 depots). Each took at most 28.5% of the month's sites (m1849), 10.5% on average.
  - 29 months of 2,424 ordered several. The most one month took was 36.1%: 73 depots at m2250, in the
    boom below.
  - The longest run of months each ordering at all was 24, from m1094. That is the floor of one depot
    clearing m1088's spike, each at most 7.8% of the sites. Main's longest such run was 42 months from
    m1886, at most 2.1%.
  - On slot 10's copy there is no run: the builders ordered nothing in 48 months (below).
  In the stalled city (sites 77 a month) the share is under one depot, so the trap orders one
  (`GapOrderCheck` 6, `BuildersFirstCheck`).
- **The playtest, 2,424 months, main against OG1:**

  | | main | OG1 | OG1's first cut |
  |---|---|---|---|
  | months past the knee | 685 | 438 | 353 |
  | ...with over 100,000 people | 470 | 224 | 200 |
  | depot orders / depots ordered | 295 / 295 | 133 / 891 (29 of several, largest 84) | 70 / 7,766 |
  | people at m1920 / m2160 / m2400 | 255,224 / 354,423 / 531,045 | 296,878 / 398,221 / 629,491 | 1,204,230 / 1,506,287 / 2,086,638 |
  | depots per 1,000 people at m2400 | 0.56 | 1.43 | 3.72 |
  | Food Processing found (orders of several) | 224 (0) | 442 (58, largest 53) | 986 (83) |

  The big-city spells shorten. In main, m1534 lasted 66 months (max 39.6), m2140 81 (33.1) and m2238 73
  (49.1). In OG1, m1534 lasted 25 (14.6), m2140 had none past 9, and m2238 lasted 41 (42.5). The one
  overshoot is a boom: from m2250 to m2268 the typical month rose from 51,000 to 190,000 points. The
  builders matched it, 152,000 -> 345,000 nameplate, and stood idle when the city stalled near 610,000
  from m2280. That is a construction cycle, at most 1.8 times the work coming in, not depots ordered on
  their own queue.
- **Slot 10's copy, his city, plays as main and gains nothing from this batch.** 48 months on the branch
  end on the same city to the point: 619,469 people at m1252 on both, the backlog 117.1 -> 67.0 months on
  both.
  - The builders held on the 739 depots on site at the load every month (124 left at m1252). The record
    started empty on the load (a save from before OG1).
  - Beyond those depots, the plan is refused at the staffing gate (`staffing.passes()`, `Construction.plan()`
    line 818): the depot's share is 0.57 against `MIN_STAFFABLE_TO_ORDER` 0.80, at 0.0% out of work.
  - OG1's sizing is reached only by an order that has passed that gate, so it never runs in his city.
    Food Processing never ordered there either (staffing).
  - So neither collapse nor runaway can start in his city from this batch, and nothing shortens his backlog
    that main would not. Whether the gate should give is his call (6).

## 4. The checks

- `./batch-check.sh --fast`: exit 0 (compile, maps regenerated and committed, Stale 0 firm findings).
- One at a time, green: `BuildersFirstCheck`, `ScaleCheck`, `ConstructionTrapCheck`,
  `FoodProcessingCheck`, `SaveFileCheck`, `InvestCheck`, and `GapOrderCheck` (34 assertions).
- `ReloadCheck`: exit 3, only main's known red (RN2's `strandedDebtShare`, `docs/notes/known-reds.txt`).
  Its "every figure in the save is the live city's" a month and two months on finds nothing else, so the
  two new saved fields read back to the point.
- `GapOrderCheck` against one a month: sections 5 and 7 assert more than one, so the old planners fail
  them. Section 5 has 5 depots for a 43.9-month queue, in a town ordering 7,200 points a month on sites of
  3,817; section 7 has 6 Bottling Plants for six plants' drinks.
- ...and without the ration: with the premium's share taken out of `depotsForTheQueue()` (a local edit,
  reverted), three assertions fail. Section 5 orders 23 depots, 9,200 points against a share of 2,074,
  and section 6's stalled city builds 11. So the cut is what holds both.
- **Caused, after REV-OG1.** In sections 5 and 6 a year of the city's own clinic orders makes the typical
  month (7,200 and 2,400 points read, the same as ordered). Penitentiaries ordered after the last month
  make section 5's queue. The expectations are the brief's bounds read off the city (the premium's share,
  the work ordered a month, the staffing and the plots), not the planner's own sum.
  - Section 6 now asserts first that the work coming in asks for two depots or more and that the city could
    staff two. Before, its record was empty, so the guard returned one before the cut was reached.
  - Section 7 is the harness's one restore. A town eating six Bottling Plants' drinks is about 30,000
    people (60,000 kg a plant, about 12 a person), a playtest rather than a fixture. So the market's demand
    is set, and the expectation is the six it states, once the fixture shows the plots and the staffing
    for six.
- **The ensemble**, `OG1-final`: on the gate box, the classes of 359913171, scored against
  `docs/ensemble/0.9.181.md`; its baseline is `docs/ensemble/OG1.md`.
  - Batch verdict PASS: hard criteria 10 of 10, 24 of 26 in all. M1 is 0 months and F1 0 of 16 runs in
    every policy.
  - One verdict changed, AUTO2 T2 (info), PASS -> FAIL. The final population's median is 1,818,590
    [78,580 to 4,870,275] against 2,532,050 [28,685 to 4,208,078]: 72% of the baseline against the 75%
    line, with the range wider at both ends. Not traced, since it is info; the playtest's own seed gained
    (629,491 against 531,045 at m2400).
  - The other policies' T2 pass (ZERO_SLACK 85%; ZERO_HOT, SHOCK and HIGH at or above the baseline).
  - A2: 13 months [7 to 26] against 15 [4 to 36], FAIL in both. T1: 16 of 16 seeds, at m771 against m758.
  - FB16, merged since (0.9.182), does not move the playtest (the orchestrator), so the run counts on the
    merged model.
- The pick (`sh tools/pick.sh origin/main HEAD`): ALL, since `AllChecks` changed. Review: needed, for
  saves (`DataSave`). REV-OG1 found money and saves clean, and one P1 and three P2 (the four mends above
  and in 3, 5 and 6). It said SHIP on the delta 359913171..ab4b414e5. The suite runs at the dry run; its log is `target/OG1-dry.txt` when it has run.

## 5. The calls

- ★ **The builders' target is the work coming in, not the queue standing**: measured, see 3. Sized to the
  queue (the brief's first reading, "the backlog for the builders") the depots ran away as 0.7.130 said.
- ★ **The typical month is the median of a year**: robust to a spike, which the mean is not (the mean of
  a year with one 18-million month reads 1.5 million a month for a year). A city whose orders are lumpy
  reads low and gets one depot an order, as before. That is the side to err on, since depots are not sold.
- ★ **`QUEUE_WORKED_OFF_MONTHS` = 24, and the cap at `1 + BuildersShort.CEILING`**. A queue of 21 months
  at a pace the work keeps up with is met by builders half again as large. The cap is the dearest the wait
  gets, so the builders never stand more than 80% over the work a typical month brings. Recommended; the
  ensemble judges it.
- ★ **A batch takes at most the premium's share of the sites' month**: the brief's "rationed by
  BuildersShort's premium on the queue". It is gentle by design: under about 20 months of queue a town's
  order stays at one. The first cut's cap, a whole month of the sites, let consecutive batches take every
  site point for months (m2259-m2267, m2377-m2387 of that run).
- ★ **Manufacturing, Automotive, Business Services, Freight, Oil, Refining, the mines and Forestry still
  order one.**
  - Manufacturing and Automotive sell to the world: they meet no gap of their own, and sized to their
    supply bound they churned (above).
  - Business Services never ordered in either run (licences).
  - Freight, Oil and Refining keep physical one-at-a-time rules (a line, a pipe, a crude unit).
  - The mines and Forestry are held on deposits, never one a month in either run.
  - `ProcessingSector`'s order is `SpreadPlanner`'s, which is CY1's (not beside).
- ★ **No change to `MIN_STAFFABLE_TO_ORDER`.** In his city (slot 10) the cause is labour, with the figures
  in 1a: 0.0% out of work and the depot's share 0.66 -> 0.57 against 0.80. The gate refuses the order
  before it is sized. `staffableCount()` trims only an order that has passed, so this batch does not reach
  his city at all. Whether the gate should give is his call (6).
- **Housing in his city is held by the landlords' own funds** ("its own funds put the 15% down on 4-9",
  from 186-2,061 asked): for study/real-estate.

## 6. Left for Jerus

- **The labour gate in his city.** Slot 10 plays as main under OG1. Its builders' order is refused at
  `MIN_STAFFABLE_TO_ORDER` (0.80) every month no depot of theirs is on site. On the copy, m1204-m1252:
  - 0.0% out of work;
  - the best depot's share 0.66 -> 0.57 with a year's draw (0.30 now);
  - the builders' posts 88.5% -> 90.4% filled;
  - the backlog 117.1 -> 67.0 months, worked off by the 739 depots already on site.
  His choice is to leave the gate (the queue is the city's labour, and the premium prices the wait at
  0.76 of its 0.80 ceiling) or to lower it for the builders, which this batch has not measured. The
  brief said not to loosen it blind, so it is untouched.
- **The dials in 5** (`QUEUE_WORKED_OFF_MONTHS`, the 1.8 cap, the premium's share). The ensemble passes
  them (4). Its one changed verdict, AUTO2's final population at 72% of the baseline (info), is his to
  weigh.

## 7. Where things are

- The probe: `docs/queue/OG1-evidence/OG1Probe.java.txt`; every 120 months of the three playtest runs:
  `docs/queue/OG1-evidence/playtest-every-120-months.csv`.
- The runs themselves (local, not committed): `target/og1/play-main`, `play-og1-cut1`, `play-og1`,
  `slot-main`, `slot-og1`.
