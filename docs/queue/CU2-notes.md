# CU2 — World prices chained — notes

Batch CU2, 2026-11-16, built headless from batch/CU2 (cut from main 08c8727d, 0.9.128). The brief:
`docs/studies/spec-world-prices.md` section 9 (with 3 and 8), the prototype in `docs/studies/world-prices/`,
CU1's `spec-copper-margin.md` 4 and 7, and QUEUE's CU2 row with DS1 folded in.

## What was built

- **`WorldChain`** (new): forty rows, each made good on its works' inputs at their shares of its price in
  `buildings.json` at the literals' mid-band prices, and the order the January solves in, inputs first.
- **`WorldMarket`'s January, chained.** A root's start is moved onto the year's level, solved, then
  anchored (a bracketed Illinois search, `ANCHOR_TOL` 1e-10) so its reference band meets its path (★3b). A
  made good is Σ share × the cheapest maker's landed price of each input, less the copper charge (floored at
  `CHARGE_LEAVES` of its cost), plus its own part solved on the manufactures index (MUV). Its supply follows
  its sales, and `resolve` scales each maker's supply. The calibration runs in two passes. The plain pass
  fixes the freight scales. The chained pass solves a made good's own part from nothing at the reference level,
  as the cold January does, so 2025 is bit-identical to a January.
- **The copper charge as money** (★5b). Concentrate carries the whole charge and anode its refining part
  (`REFINING_PART` .36). In the calibration it is .054 of the reference copper price (★6a); otherwise
  Nations' swing × the MUV level. `Nations.copperChargeSwing`: middle 5.5%, about 3-8%, large peaks nine years
  apart, a slower turn beside them. The Historical path peaks in 1997, 2006, 2015 and 2024; a drawn path has its
  phase from its seed. The record table `CHARGE_RECORD` is empty (★4b). **★4 is Jerus's and stays open as
  WP1-C**: if he reads the benchmark as facts, CU2's code takes a table of 27 numbers and nothing else.
- **The guard** (decided, not a call): a pass whose error is not finite puts back the last finite plain step
  and resets the accelerator. `WorldMarket.guards()` counts it. It fired **0 times** on drawn world 9,
  every January 1900-2170. Main left the numbers there in 2165.
- **Saves:** `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` go to **54**. The saved prices and scales are now the
  chained solve's, so an older save would read wrongly. The loader refuses it, as the standing rule says.

## The calls

★1 (b) the works' recipes, forty goods; ★2 (a) the cheapest maker's landed price; ★3 (b) the reference band
anchor, wire at 70% rod; ★5 (b) money; ★6 (a) the literals stand for 2024's 5.4%; ★7 (a) accept Copper
Refining with no works that pays on imports; ★8 (a) a study of Manufacturing's and Automotive's margins (see
the ensemble below). ★4 is built as (b) and waits on Jerus as WP1-C.

## Six rows' shares are the file's, not the study's table

WorldMarketCheck's recipe section computes each works' input cost over its sales at mid-band prices
(`RECIPE_TOL` .03). It found rows off the study's table. Two causes:

- The prototype counted pulpwood and residues at 0.9 of their price (tonnes to the cubic metre: freight, not money).
- It priced the Blast Furnace's coke and the kilns' limestone off the file's.

Six rows, ten shares in all, were set to the file's:

| works | input | the study | CU2 (the file) |
|---|---|---|---|
| Blast Furnace (pig iron) | coke | .33 | .29 |
| Cement Plant | limestone | .34 | .39 |
| Cement Plant | coal | .12 | .11 |
| Cement Plant | aggregate | .04 | .05 |
| Lime Kiln | limestone | .21 | .23 |
| Lime Kiln | coal | .07 | .06 |
| Panel Mill | pulpwood | .31 | .34 |
| Pulp Mill | pulpwood | .21 | .23 |
| Pulp Mill | residues | .08 | .09 |
| Wood Pellet Mill | residues | .32 | .36 |

A row whose every share is within .015 of the file stands as the study ran it. The Blast Furnace's iron share,
.36, is the same in both.

## The checks

- **WorldMarketCheck** (slice 2), all passing, about 30 s.
  - Restated: a root's band meets its path; a made good is its inputs plus its own part; every maker clears.
    The reference city still sees the literals to the bit, but for concentrate and anode, which are off their
    literals by the year's charge (by design, ★6a).
  - New: rod on cathode and every works near its literal, for Historical and four drawn worlds, two sites,
    every fifth year 1900-2200; the recipe table against `buildings.json`; the guard (a NaN-start fixture
    causes it; drawn world 9 to 2170, 0 fires); the copper charge.
  - Timings on the full file: the Base 885 ms, a cold January 31.6 ms, a warm one 25.7 ms (median).
- **CopperRefiningCheck 10** (new, 13 s). A Large Copper Mine, a smelter and a refinery in a town on the
  world market, played 1900-1940.
  - Copper Refining's revenue over its inputs is above 0 in every year after the founding year. The founding
    year is printed, not held: D$-98, the works ramping on a town of a few hundred.
  - The smelter's margin is above 0 every January; the least is .0767 of cathode's price.
  - The margin follows the charge's treatment part: correlation .98 (`CYCLE_SEEN` .95), 1.28 of its range
    (`RANGE_KEPT` .5).
- **CopperRefiningCheck 11** (new). Seed 1's 2,400 months in a child JVM, as DeterminismCheck starts its own,
  stand at most `ROD_MILLS_STANDING` (1) Rod Mill. Branch: **0** (main's 363). It took 2.8 min alone;
  `ROD_CHILD_MINUTES` is 28. It is listed in AllChecks' `LONGEST_FIRST`. LongPlaytest's summary prints a
  "copper works at the end" line for it.
- **Section 9's Rotterdam city** passes, but all twenty of its cuts are of fabricated steel, none of rod: the
  label's rod is not what it exercises on the chained prices. The assertion is left as it is; a note for the
  next batch that touches it.
- **SaveFileCheck, mended fixture.** On the chained prices the fund section's bank stands failed at the save
  (in resolution, equity -3,420 against a minimum of 3,507), and a failed bank asks nobody for preferred
  shares. The fixture now resolves it as the Bank tab's button does, the model's own call, as the WG2 note
  above it does, before putting it under its minimum. A new assertion states it is open. All pass.
- **WorldMoneysCheck (WN4), one assertion restated.** "In the base year every price is the base year's"
  was the unchained solve's identity. Chained, 2024 moves goods: a root by its anchor (up to 1.1%),
  concentrate by the year's charge (2024 is a peak of the swing, 7.0% against the literals' 5.4%: −0.079 in
  ln) and anode −0.028. What stands, and is asserted now, is that each good is one number across every maker
  (measured spread exactly 0), so no nation's price moves against another's. The move is printed as a
  finding. On the full file the widest base-year terms of trade is Eritrea's −0.078; on the fixture, 0.0024.

## The pick: fixtures the chained prices moved

The pick (`./batch-check.sh --pick`, ALL, since WorldMarket is shared) found NEW reds in nine harnesses. The
model code they read is not CU2's: the chained prices changed the fixture cities. Each fixture now causes its
condition again, with its premise unchanged.

- **BuildCardCheck** (the money gate). The pellet plant now earns (main: it loses D$79.6k a month), so the
  planner's words name its bar, and the bar holds BA1's interest on the wait for the plant. The check now
  asks `City.money(t, ...)` for the plant, as the planner does. Before, the two calls agreed only because
  neither named a bar.
- **HaulageCheck.** The growth town's investors raised a Manufacturing works of their own in its sixteen
  months. The town now holds Manufacturing.
- **DeathRecordCheck** (LD3). The town stands 351 out of work at month 60, and its open twin's count moves as
  far. The wave is now read against the twin in the same month: 155 more out of work at the peak. Its two OX1
  lines stay red, as on main.
- **AutoBuildCheck** (the walk). Childcare ranks the Large Childcare Centre (D$276k) second, above the Small
  one (D$71k) that main ranks second. The fixture looked one card down. It now walks the ranking as the pass
  does (`AutoBuilder`'s skip): with the cash for one Small Childcare Centre, the pass refuses the Childcare
  Centre and the Large one and orders the Small.
- **ResearchViewCheck and KeptCheck 96** (the research town). The village's two Mixed Farms and the two
  Pulp Mills the fixture stands were retired by month 14 (main kept a farm and both mills). The town now
  holds their sectors, so they neither build nor retire. ResearchViewCheck: all 217 ok, including main's
  recorded FB10 line, which the same fixture had left red.
- **PriceLevelCheck 8** (the played twin). The twins parted at month 107 through `HistorySave.roundSig`,
  which rounded to six decimal figures at both price levels, so on two grids. The bank's share-price history
  rounded apart, and the warrant buyback read two volatilities (`Game.bankVolatility`): 1.9e-12 in the rate
  at month 107, and 1.2e-9 in processed meat's imports by month 114, past `TWIN_REAL_TOLERANCE` (1e-9).
  `roundSig` now rounds the value at today's prices and scales it back, as PartnerTrade's money grid does
  (DZ1). This is a **model change**: at the factor, every series recorded to six figures moves by its last
  digit (the wage, the rent, and the food, materials, ore, ground and share prices). Worst real gaps now
  5.7e-14, 3.1e-11, 3.2e-13 and 1.5e-13 in sections 6-9; HistoryCheck passes.

- **WageLadderCheck 6** (the played 1900 village), **restated, and Jerus's call CU2-W**. "Its unskilled wage
  still under half of today's after two years' scarcity" held the share main's village reached (.34 at month
  24, .45 at its most), not a bound the model keeps. On the chained prices the village grows to 2,501 people
  in the two years (main's 1,358, already 689 against 519 at month 3). Its unskilled band reaches the curve's
  ceiling (`MAX_MULTIPLE`, 4) at its tightest and stands at 4.84 posts a head at month 24 (multiple 2.20). The
  wage reaches .60 of today's at month 13 and .556 at month 24, 2.33 times 1901's base (.238 of today's); the
  cost of living stays 1. The old `roundSig` gives the same .556, so the cause is the prices. The check now
  asserts the cause, month by month (REV-CU2-1): the going rate and the ratio are 1901's, the band's multiple
  is the curve's at its tightness, and the wage moves `ADJUST_RATE` of the way to their product (worst miss
  0). The share is printed. Whether a 1900 village may earn it is CU2-W, in the ship file's pending rows.

Passed by name, in code CU2 does not touch, each with its fix queued (the ship file's `## known-reds`):

- **MapCheck**, "at the widest view every pixel a highway or the track crosses is drawn as it, and no
  other" → **FB9**. The chained prices grow a different played city (256 plots across, main's 296). At 1,024
  plots a pixel, the harness's own run pixels and `MapTiles.lastRun()` disagree on it.
- **KeptCheck**, "no month makes more than a tenth of the nodes the first draw made (the most made in one:
  45 of 400; 3 of 25 months made none)" on the Government Overview → **FB2**. Main records the same page's
  class at 47 of 378 and 40 of 384: the unchanged Government code recreates transient fiscal rows. (Before the
  `roundSig` fix the played city differed, and the red was instead Policy, Promises' Food page, 12 quiet months
  of 25; it passes now.)

## The review (REV-CU2-1, Opus): SHIP once its first finding was mended

1. **WageLadderCheck 6's restated bound** was `LabourMarket`'s own clamp, true by construction. Mended: the
   check asserts the cause (above), and CU2-W puts the wage to Jerus, the recommendation first.
2. **WorldMarketCheck's guard run** asserted nothing when the guard fired (`assertTrue(..., true)`), and
   `WorldMarket.guards()` counts the process's every January, whoever solved it. Mended: the count is a
   printed reading. The started-from-NaN solve keeps its `put >= 1`.
3. **`roundSig`'s `r x FACTOR`** is not a short decimal, so each six-figure value writes about 17 digits.
   Measured on a default playtest's history file: (to come).

## WN4: what moved

The Historical path's ln terms of trade at 2100 (sequential Januaries from 1900, the default seed):

| nation | CU2 | the study's chained prototype | main (the study) |
|---|---|---|---|
| DR Congo | +0.074 | +0.08 | −0.44 |
| Zambia | +0.283 | +0.29 | −0.41 |
| Chile | +0.276 | +0.29 | +0.03 |
| Peru | +0.117 | | |
| Germany | +0.019 | | |
| United States | −0.000 | | |
| Japan | +0.098 | | |
| China | +0.075 | | |

Germany and the United States move by 0.05 or less, as the study said; Japan and China by more (Japan +0.098).
The copper nations sit well below zero before 2000 on both builds (the path's cheap copper). The guard fired
0 times in the 201 Januaries. WorldMoneysCheck passes (16 s).

## The trace (seed 0, the default playtest, against main 08c8727d)

The trace moves from the first month: GDP is first more than 1% off main's at month 2, the rate at month 19.

| month | people main / CU2 | GDP D$ main / CU2 | rate main / CU2 | price index main / CU2 | GDP in US$ main / CU2 |
|---|---|---|---|---|---|
| 12 | 150 / 141 | 21.0 / 47.4 | 0.785 / 0.785 | 1 / 1 | 26.8 / 60.4 |
| 120 | 2,432 / 1,474 | 816 / 263 | 1.26 / 1.80 | 1.76 / 2.10 | 647 / 146 |
| 600 | 3,522 / 14,670 | 1,481 / 19,910 | 1.17 / 5.46 | 5.29 / 22.7 | 1,270 / 3,646 |
| 1200 | 33,240 / 24,855 | 451k / 424k | 37.8 / 46.9 | 241 / 276 | 11,940 / 9,046 |
| 1800 | 286,027 / 111,714 | 65.2M / 35.2M | 44.9 / 113.4 | 1,448 / 2,816 | 1.45M / 0.31M |
| 2400 | 1,376,222 / 1,297,969 | 2.47B / 7.56B | 130.7 / 983.5 | 9,665 / 64,330 | 18.9M / 7.7M |

The city ends about the same size, in a money that fell much further: the D$ at 983.5 to the dollar, not 130.7.

## Copper works at month 2400

| seed | Copper Smelter | Electrolytic Refinery | Scrap Copper Refinery | Rod Mill | Wire & Cable Works |
|---|---|---|---|---|---|
| 0 | 3 | 1 | 4 | 2 | 4 |
| 1 | 0 | 0 | 0 (one building since m2341) | 0 | 1 |
| 2 | 5 | 2 | 5 | 0 | 0 |

On main, seed 0 stood 61 Wire & Cable Works and seed 1 363 Rod Mills (the study). The default playtest no
longer exercises a large wire industry. CopperRefiningCheck's own fixtures still build every works.

## DS1: which gate holds a processing sector, and whether any can be left this way

- **Seed 0 on main 0.9.128 is not dead.** Copper Refining ends with $12.1T cash, $15.6T assets, no debt, a
  6.93% rate and two restructures. The limit bound it for 190 sector-months, and it was in default for 8
  months from m1540. DS1's picture (31.30% from m768) came from an older build.
- **Seed 1, CU2: no Rod Mill.** The sector stands empty to m2340. A Scrap Copper Refinery is ordered at m2341
  on a one-month spike in its estimate ($2.09B a month at m2340, held; then $3.20B, 1.55% a month, built on
  $83.6B of bonds and loans at 8.6-11.2%). The next month its own borrowing has raised the hurdle, and
  `SpreadPlanner.passesMoney` holds every further build. At m2400 the refinery is still building, and the
  sector owes $127.8B on $141.8B of assets.
- **Seed 2, CU2: the trap under the cycle.** Five smelters were built from m1598 (about 2033), at the charge's
  peak (0.078 in 2032 on Random 48213), on estimates of 0.4-0.8% a month. At the trough (2044, 0.038) the
  appraisal reads "would lose $140M a month": the gross margin is about 4% of revenue, and interest outruns it.
  At m2400 the sector owes $2.95T on $1.93T of assets, at 30.44%, with no cash and an 82-month loss streak.
  The MONEY gate holds every candidate. Its hurdle carries `waitCarry` over the builders' lead time:
  $3.3e42k at m2400, a real figure for a centuries-long wait at 30%, not an overflow.
- **The answer.** The gate (`SpreadPlanner.passesMoney` → `servicesItsOwnDebt`) and the credit (prime plus
  the leverage spread, the record surcharge, concentration) do not know which sector they hold. Any
  processing sector can be left this way. The planner reads one month's margin as lasting, with no view of a
  cycle, and its own borrowing raises the rate every later build must clear. Queued as **CY1** (below).

## The ensemble

(to come: `docs/ensemble/CU2.md`, the 80 runs against the newest baseline; whether Fabrication and Automotive
still carry the chained city, which decides ★8's MA1)

## The dry run

(to come: the run directory and the two commits)

## For Jerus

★4 (WP1-C) is the one call left: CU2 ships the game's own swing, and the record's 27 numbers drop into
`Nations.CHARGE_RECORD` if he reads the benchmark as facts. Saves before format 54 no longer open. A write-up
goes to the project, since the batch changes how the game plays: the made goods follow their inputs, and
copper works stop paying on imports.
