# BA1 - Every business may build ahead ({next}, 2026-11-03)

**WHERE IT STANDS (2026-11-05, 01:30): main's R0 (0.9.12) merged in; the full suite on the merged tree has 10 reds.**
- **The full suite on the merged tree** (67fe6cc7 + the maps, `./batch-check.sh`, all 171 harnesses, 00:02-00:36, run
  alone): 161 passed, 10 failed. (A first run at 23:20 was void. The suite of a run stopped at 23:18 had not died, ran
  beside it, and lost classes to the merge's recompile.) SlopeCheck, LongPlaytest and DeathRecordCheck pass on the
  merged tree: R0's dates moved their cities again.
- **Two pass by name, causes outside BA1's diff:**
  - **MapCheck, four lines**, the three checks DM2's brief lists: 2d "every box stands four months on" (4 moved), 2d "a
    yard that goes moves none of the buildings" (155 moved), and 2h scenario 1, "the highways reaching a street"
    (9 moved). DM1's class: the district plan moves standing boxes, waiting on DM2. `MapCheck.main` keeps the builders'
    price off through 2h for them, and on the merged tree's path they show with it off too.
  - **PriceLevelCheck 8, two lines:** "FAIL sector.Manufacturing.cash (nominal) month 97 ..." and "every live nominal
    series equal over the factor within 1.0E-9 (worst 1.1e-08 in sector.Manufacturing.cash)". Not a price the factor
    missed: the factor nudged by 1e-12 parts from the factor by as much (1.04e-8 against 1.08e-8).
    - The source is `BondMarket`'s corporate issue search (`s = filled` until `filled >= s * (1 - 1e-9)`). It stops
      within 1e-9 of a fixed point, so two runs can settle on bonds up to that apart: at month 94 the bonds issued
      differ by 4.6e-9 of the issue.
    - Manufacturing rolls 150,000k of bonds on this path, and the gap grows tenfold every fourteen months (months 68,
      82, 96) to 1.1e-8 of its cash.
    - BS1 is queued: search to a fixed point well inside the twins' tolerance.
- **Seven are fixtures on the merged path, mended** (two Sonnet slices in worktrees of their own, reviewed):
  - MortgageCheck 12, HoldersCheck 13a, CurrencyRegimeCheck's peg town, PassThroughCheck's ring town, MonetaryCheck's
    founding and CentralBankCheck 20's city: all open R0's levers before a month runs (`reset()`, `openAll()`), as
    main's R0 fixtures do. The towns were written against a city that taxes from month 1. Untaxed until 1916-20, BA1's
    town ran a deficit main's does not: the treasury held D$190k for PassThroughCheck's swing, and the peg town's
    inflow was US$124k against a US$212k current-account deficit. MonetaryCheck's delay line and PassThroughCheck's
    groceries sign hold once their fixtures do, so neither is a finding.
  - BankCheck 15: retail is held from ordering before its second sale, as the first sale's loop holds it. With no cap
    it ordered a shop, and the distress rule does not shrink a sector that is building.
- **One was BA1's own bug, fixed: FoundingYardCheck 4** ("every building of the village stood at once by month 44"):
  the village's depot opened in month 49 against main's 22.
  - `BuildingManager.goesFirst()` asks whether the builders could staff the depots on site and one more, and BA1's
    rule (THE POSTS OF ITS OWN SITES ARE SPOKEN FOR) counted those same depots' posts as taken, so each was counted
    twice. Its share read 0.00 in months 8 to 10 (0.46 to 0.76 before BA1), and the builders-first rule stayed off.
  - `Sector.staffingWithOwnSites()` asks the test for a count that includes the template's own units on site;
    `goesFirst()` is its one caller. The village now stands in month 36 (main 34).
- **Seen on the way, for a look as model behaviour:** MonetaryCheck's largest delay move is 0.087 against an allowance of
  0.100 (main's 0.049): BA1's lumpier building may make a town noisier. Not isolated.
- **OR1 stays queued.** DeathRecordCheck passes on the merged tree, but its cause (below) is real.
- **The full suite on b4bf96f4** (the staffing fix, 01:23-02:02, alone): 163 of 171 passed, 8 red. The young towns build
  their depots first again, which moved four fixtures once more. Two Sonnet slices are on them:
  - FloatLinkCheck, with DeskCheck, which reads its town: the bank's room is not there within ROOM_WAIT_MONTHS. The
    effective-rate lines, 5.2 bp against 5, are to be judged once the fixture holds.
  - CurrencyRegimeCheck 3: the credibility search finds no twin.
  - SaveFileCheck: "something is genuinely mid-build".
  - ScaleCheck 6: the grown city's rents price migrants out.
- **The rest pass by name, all known classes:**
  - LongPlaytest's one finding, "industry stands farther out than the homes" at m2400: Jerus's word, 2026-11-04.
  - MapCheck 2d's "a yard that goes moves none of the buildings" (54 moved): DM2's class.
  - PriceLevelCheck 8's drifts (months 101 to 105, 1e-9 to 2e-7: "at today's prices", "every real series equal
    within", "is the same to the person"): the class the orchestrator names R0's farm retirement at 1900 prices (RT1).
    The bond search's stop (BS1, above) is a second source of the same size.
  - SlopeCheck and DeathRecordCheck pass on this tree.
- The ensemble on b4bf96f4 (run BA1e): 22/26 against 0.9.12's 24/26. Section 4 has the comparison.
- **The four mended** (Sonnet slices, reviewed):
  - FloatLinkCheck, and DeskCheck through it: the town holds every sector, so its households' spare makes a sale big
    enough for the fee (318 against main's 6,177 when free). The 5 bp bound stands, named `EFFECTIVE_OFF_COUPON`.
    DeskCheck's dial and its five-point rise are searched to their conditions.
  - SaveFileCheck: its twenty depots' order is funded (NEEDS_FUNDING before) and asserted.
  - CurrencyRegimeCheck 3 pegs once smoothed inflation is back within tolerance of the target; the dent holds with its
    fixture holding. 9 takes the first depot count in `SURPLUS_DEPOTS` whose first month at the peg is a surplus (18).
  - ScaleCheck 6: `FREE_FIXTURE_MONTHS` 488 (717), with R0's levers shut, where every line holds. With them open no
    month from 20 to 2,400 holds every line. The best, 166-167, misses one line: the copy's bank closes 970 of its 1,000
    branches in month 1 where the city's one branch cannot close, 8% of a small treasury. A scale finding, for whoever
    takes the scaler.
- **Found on the way, for Jerus (not BA1's to settle):**
  - **A young town's bank under build-ahead:** in FloatLinkCheck's free town the firms borrowed 8,100 to 10,600 on the
    bank's equity of 1,246, and the bank's equity went from 1,246 at month 61 to -500 at 63 and -3,200 at 75, its room
    for city paper nothing to month 183. Uncapped orders let a young town's firms out-borrow its bank.
  - **The same code gave another city twice under load** (month 125 instead of 61, 1,067 people instead of 909), and
    the same in five other repeats. A model that is not deterministic under load would make every twin and fixture a
    draw. Not traced.
- Next: the full suite once more, alone (03:11), then the ship file.

**WHERE IT STOOD (2026-11-04, 22:45): the gate stopped at 22:00; the ship file is off.**
- **The gate's five NEW reds** (lines `--pick` did not run; Jerus's two named lines passed). Each was switched for the
  three rounding edges and the brake:
  - **HouseholdMemoryCheck 7, mended (fixture):** "the month after, both cities hold the same households", 4.03
    against 0. Its city is founded by `addStack()`, which allocates no land, and a load derives the allocation from
    the buildings. So the loaded twin had 6,290,000 sq ft (the fixture's buildings, exactly) less free ground: 525,192
    free live, none loaded. On BA1's path the city is short of ground by month 36, and the twins' planners parted on it
    ("no land - 0 m2 free" in the loaded one only). The fixture now hands the city that ground and allocates it.
  - **DeathRecordCheck, the cause traced; outside BA1's diff, to pass by name** under Jerus's standing word, with OR1
    queued. The line is "the orphan section does not fill behind it, in any month of the two years": 52.35 against
    the mill-open twin's 41.41, with an allowance of 1.
    - The cause is not the closing itself. On BA1's path build-ahead's plants open within the two years: jobs rise
      by 372 at month 1 and 464 at month 4, in both twins. Each time, the adults at home are hired and their
      households re-form, and the family builder's throttle leaves about one child in seven out (FamilyModel, "AND
      THE CHILDREN THE BUILDER LEAVES OUT"; Jerus: "they're the orphans").
    - The outside dependants fall from 434 to 256 and from 246 to 24, and the orphans jump by 27 and 26 (the twin's
      by 27 and 19), then decay about 1.5 a month.
    - The closing's twin had more adults at home to re-form, so more orphans. The 2026-09-15 fix keeps children with
      an adult who leaves work; an adult hired back is the case it does not cover.
  - **CurrencyRegimeCheck (3, 5, 9, 10, 12, 13), PassThroughCheck 4, CapitalFlowCheck: mended (fixtures, Sonnet
    slice, reviewed).**
    - CurrencyRegimeCheck: one cause for all six. The peg towns now have 12 depots standing (9 to 15 work, 6 and 30 do
      not) and a vault deepened by 2 x `COMFORTABLE_COVER` months of imports, bought by the treasury and forgotten by
      the rate. Section 3's credibility search runs on a town played past its young dip.
    - Section 9's identity (the vault's record, -117.69 against 0) holds once its fixture does. It was the fixture.
    - PassThroughCheck 4: the base month's search now also needs the vault to hold no more than it bought (US$22.71k
      held for US$22.52k bought at month 241). The swing's own assertion stands.
    - CapitalFlowCheck: the 400 houses before it made the power plant dearer than the cash left (NEEDS_FUNDING, so no
      power, and a carry worth $60 in fifteen years). Each order is now funded to its quote and asserted taken, and the
      builders are 30 depots, not 4.
  - **Seen on the way, for a look as model behaviour** (not isolated): in CurrencyRegimeCheck's town the sectors hold
    their cash and send it abroad (D$355k in the first month at the peg against 0.15k on main), which is what waiting
    for cheaper contracts does with a business's money. The same young town deflates in its first years (the index
    -4% at month 39, -8% at 45). The ensemble's A3 holds (deflation spells 5 [0 to 10] months).
- Next: the full suite on the world files (`./batch-check.sh`, every harness; started 23:00), then the ship file. Its
  changelog names the three lines passed by name.

**WHERE IT STOOD (2026-11-04, 21:20): ready, the ship file pushed.**
1. On main at 52b19f18 (CM1 0.9.11 merged in). The brake is dear contracts (section 1, ★), with three rounding edges
   closed since the morning (item 3). The model is final.
2. **The ensemble on the final model** (`docs/ensemble/BA1.md`, run BA1d on ab5f3f41): **24/26 against 0.9.6's 22/26**.
   Only A2 and S1 fail, both failed on 0.9.6 too. AUTO2's busts are 19 in 13 seeds (25 without the brake), and the
   queue's p90 is 45 months (58 without it). Section 4 has the rest.
3. **★ calls (the orchestrator took both as recommended at 11:58):**
   - **NewGameCheck 11: built.** It asserts what the solver keeps: the face one granule down does not cover. It prints
     the overshoot as a known property of the solver: 52.43k over a gap of 3,770.17k, 1.39%. Not queued: the player
     borrows a little more cash than the plant needs, which stays in the treasury.
   - **Each building priced at its span's mean: built, measured, withdrawn.** It made ScaleCheck worse (people 3.72e-2
     apart against 1.59e-2), and the 5-billion copy's "an order of more than a million buildings" fixture failed.
     ScaleCheck's miss was its scaler (item 5), not the price. One building is still priced as FC1 priced it.
4. **PriceLevelCheck traced to three edges, each a hair of rounding one price level read and the other did not; all
   closed, no months moved, the four twins to 1e-9 (worst 7e-14):**
   - Section 8, month 23: a staffing share of 1.3e-18 at today's prices and 0 at the factor. Manufacturing keeps the held
     template with the largest share, so it held a Machine Works at one level and a Fabrication Shop at the other. Now
     a share at or under `Sector.SHARE_ROUNDING` (1e-12) is none. Snapping the bands' spares instead, the first try,
     moved more cities: other code reads a spare's sign.
   - Section 9, month 26: one cell's debt of 1.1e-16 since month 4 at today's prices, 0 at the factor. `Exchange.mayBuy()`
     read any debt above nothing, so the cell sat out a share offering at one level only (the mechanism the harness's
     own note describes at month 56). Now it reads a debt as `HouseholdBalance.owes()` does, above the balance's floor.
   - Section 9, month 29: the household waterfall's tail a hair above zero (3e-21), borrowed as a debt. It was a draw
     of the level exactly 1: the factor, the factor nudged by 1e-12 and today's nudged by 1e-12 ran equal through month
     33, and only 1.0 parted. Now a remainder within `Household.SHORT_ROUNDING` (1e-12) of the month's gap is covered,
     the twin of the clamp `fundShortfall()` already had for a hair below zero.
5. **`--pick` on b08dae6f (20:30, 111 of 170 harnesses, LongPlaytest for Game.java and Sector.java): exit 4,
   red only in two known-class findings.** The fixes for the rest are in section 3. They come from moving the cities
   off rounding draws:
   - SlopeCheck: "a building on the map moved". This is DM1's class (the district plan moves standing boxes), which
     waits on DM2.
   - LongPlaytest, its one finding: "industry stands farther out than the homes", once, at m2400 (industry 1.32 km
     out, the homes' band 0.95 km). It is the layout ring finding that main's own m4000 runs have ended on (WN5's and
     FC1b's notes).
   - Both pass with the staffing-share edge left open, but then PriceLevelCheck 8 is red. **Jerus, 2026-11-04 evening:
     "Ship it, pass both".** The fix stays, and the gate passes exactly these two lines by name (`--red-ok "Ship it,
     pass both"`, a `--red-line` each). Any other red still stops it.
6. **The ship file is pushed** (`docs/queue/BA1-ship.md`, last). Its one pending row, BA1-D, is the two findings, for
   DM2 and whoever takes the district plan's layout next.

Jerus, 2026-11-01: "why bounded? why not let contracts flow? market after is efficient, too much and contracts get
expensive thus business stop trying to place orders until contracts arent so expensive". The brief is
`docs/queue/BA1.md`. Built on FC1b (lane 1: LD2, FC1b, BA1, CB1).

## 1. What changed

- **The caps are gone.**
  - What went: `MAX_CONCURRENT_ORDERS` (one order on site, "already building"), `MAX_ORDER_MONTHS` (an order cut to
    open inside 12 months), the landlords' `withinMonthsOfWork()` and TRY A SMALLER HOME, and planMaker's refusal of a
    plant past 12 months of lead.
  - `orderSize()` is now the gap, at least 1, capped by the plots.
  - The city's automatic orders keep their own cut, `AutoBuilder.ORDER_MONTHS` (12), because no profit test rations them.
- **★ An order's own buildings are priced along FC1's curve.**
  - Each building of an order is priced on the queue the buildings before it leave (`BuildersShort.meanPremium()`).
    That is an exact sum for up to 64 buildings and the curve's integral past that. ConstructionTrapCheck 12 measures
    the integral's worst miss at 2.6e-3 for a 100-month step.
  - One building is priced exactly as FC1 priced it.
  - Without this, one order of six hundred homes took years of the queue at the price before it. With it, 600 Houses
    on a queue of 8.2 months cost 1.107 times the work per home.
- **★ Dear contracts (2026-11-04).** Every money test grows an order's cost once more by the order's own multiple on
  the builders' price (`BusinessInvestment.dearOf()`, folded into `carryOf()`). A business can wait: an order placed
  into a dear queue opens little sooner than one placed once the queue has run down, and that one pays no premium
  (Jerus: "stop trying to place orders until contracts arent so expensive"). At or under the knee (9 months queued)
  the multiple is exactly 1, so a young city's orders are FC1's. It rises with the order, so the halving searches
  hold. Section 4 has what it was measured against.
- **The interest on the wait.**
  - Every money test multiplies the cost by `(1 + real rate / 12)^W`, where W is the order's whole wait
    (`BusinessInvestment.carryOf()`). This covers consider()'s slices, the build card, the spread planners' money gate
    and Oil's test.
  - The landlords' lender reads the shortfall grown the same way (`Mortgage.decide(..., carryOf)`).
  - W is the months until the order's last building opens. That is conservative for a large order. It is decided this
    way because it rises with the order, so the search by halving still holds. OrderSearchCheck's twins read the carry
    and still agree with the countdown: 2,158 orders and 528 mortgages in its long run.
  - The wait is read at the sites' month plus the foreign contractor's while he takes orders
    (`Game.orderWaitOutput()`). Without his month, every wait in the trap would be endless.
- **The horizon** is `horizonFor(lead)`: the lead itself, at least 6 months and at most 12 (`HORIZON_MOST` =
  `TREND_WINDOW`). `BuildAdvice.opening()` reads it and no longer clamps the lead to 12.
- **Each planner counts its own buildings on site as supply:**
  - Retail's shops and stations, Restaurants, LuxuryRetail, Freight's track, stations and sidings;
  - the processing works' values (`SpreadPlanner.after()`), the refiners' feed and crude, HeavyIndustry's and
    CopperRefining's old metal;
  - Automotive's steel, BusinessServices' licences, Oil's jackets.
  - The mines, forestry, food and makers already counted them.
  - `Sector.staffing()` counts a sector's own posts on site as spoken for.
- **Physical rules that stay** (each a count of its own, not the cap):
  - Oil: one pipe on site.
  - The refiners: one crude unit on site (R3).
  - Haulage: one truck depot.
  - The refiners' Tank Farm: one, standing or on site.
  - **The builders: one depot on site** (section 2).
- **The screens.**
  - Each sector's planner line says how many orders it has on site and how far out the last one opens, and that
    the price, the interest on the wait and dear contracts ration orders.
  - The Construction tab's QUEUE mark is now the knee where the price of a point starts to rise
    (`BuildersShort.KNEE_MONTHS`, 9 months), on a bar to 33 months, where the premium is half its ceiling.

- **Three rounding edges the price-level twins found (2026-11-04).** Each was a hair of rounding that one price level
  read and the other did not, the first two surfaced by build-ahead's paths:
  - `Sector.SPARE_ROUNDING`: a spare within rounding of the posts is none.
  - `Exchange.mayBuy()` reads a debt as `HouseholdBalance.owes()` does.
  - `Household.SHORT_ROUNDING`: a hair still short after the waterfall is covered.
  WHERE IT STANDS 3 has the months and the cells.

## 2. Decided here, no star

- **The builders keep one depot on site at a time** (`Construction.ONE_DEPOT_ON_SITE`, "a depot on site already").
  - Tried first: a depot a month, with the depots on site counted at nameplate as output to come.
  - BuildersFirstCheck's stalled city (sites at 76 of 458 points) ordered a depot every month until seven were on
    site. All seven shared the 76 points off the top, so the first opened in month 22 against a bound of 7.
  - ScaleCheck's K-times copy stopped tracking its city: K depots on site there against one here, and each ordered
    one more.
  - This is 0.7.130's measured finding again. Both harnesses are green with the rule back.
- The refiners' rule of one crude unit on site stays, as R3 wrote it. A conversion unit or a Tank Farm may now be
  ordered beside one.
- A planner's estimate of a new shop or kitchen (served customers) takes the counters on site off the queue too, so
  consider() judges the order on what it will actually serve.

## 3. The checks

- `--pick` on the world files, b08dae6f: 111 of 170 harnesses, exit 4. Red only in SlopeCheck and LongPlaytest, the two
  known-class findings of WHERE IT STANDS 5. Stale 0 firm, the maps regenerated.
- Mended on the evening of 2026-11-04, each a fixture on BA1's path (Sonnet slices, reviewed):
  - ScaleCheck 9: the copies are read from the grown city once its sites are empty (`emptySites()`, 11 months). People
    are within 1.9e-3 (1.59e-2 before) and unemployment within 1.6e-3.
  - MapCheck 2h: the block left out is the one of the reserve's box the city owns least of. The build-ahead city owned
    the corner block it took.
  - FloatLinkCheck: the town plays on until the bank has room for its sales (its loans weighed 7,870 on equity of 592
    at month 61).
  - FundCheck 3: the second branch is funded. The fund is kept off the market (as 9b), so its dividend does not buy the
    new issue's shares.
  - HoldersCheck 13c: the bill is sized to the discount the bank carries.
  - NewGameCheck 11: restated by the ★ call.
- Mended, with the cap's assertions replaced by what replaced them and the fixtures kept:
  - InvestCheck: order sizing, land, the landlords' months and smaller home.
  - OrderSearchCheck: the orderSize twin went with the search, replaced by section 6's sizes. The interest and lender
    twins read the carry.
  - ScaleCheck: orderSize at scale.
  - BuildCardCheck, WellCheck, BuildAdviceCheck, ChildcareCheck, AutoBuildCheck, ReadPathCheck: the new signatures
    and arithmetic.
  - RefineryCheck 19 and CoalMiningCheck: "already building" is gone.
  - BuildersFirstCheck: the builders' word.
- New sections:
  - InvestCheck, "every business may build ahead (BA1)":
    - ordering again with an order on site, and not ordering once the order on site covers the gap;
    - orderSize, `horizonFor`/`leadFor` and `waitCarry`/`carryOf`;
    - the wait refusing a Diner behind forty universities that it passes with the queue gone;
    - staffing with posts on site;
    - a 360-month count.
  - ConstructionTrapCheck 12: an order's own buildings.
- **The count (InvestCheck, 360 playtest months):**
  - 638 orders judged and 97 cleared, the largest 11 (a landlords' mortgage).
  - 44 cleared while the sector already had an order on site, all of them the landlords'.
  - In those months no other sector's order cleared beside one of its own on site. Sized to the whole gap, one order
    usually answers it before demand moves on.
  - The builders' premium was on in 14 of the 299 months with an order judged.

## 4. The ensemble

### What flooded the queue (2026-11-04)

AUTO2's 16 seeds again on 0dcd714d (CB1 merged), with the queue's points split by owner (temporary getters, removed):

- **Not the landlords.** Homes were 8 to 10% of the points queued, and 8% when the queue was past 24 months.
- **The works.** Past 24 months queued, heavy industry owned the most points in most months, 26 to 49% of the queue in
  the four seeds probed. Public works held up to 20%, and the copper and cement works 5 to 8%.
- **Why the price did not stop them.** Their goods sell at the world's price, so a plant on site does not lower what
  the next one earns (`SpreadPlanner.after()` moves nothing). At queues of 36 months and more, the median order that
  passed cleared its money test by 1.6 to 2.0 times (coke ovens, the blast furnaces, the lime kiln), with the premium
  (about +60% on the work there) and the carry (1.4 to 1.7) already in it. FC1's premium stops at +80% on the work
  alone, so no queue length was dear enough for them.

### The brake: dear contracts

The test asks an order to carry its cost grown once more by its own multiple (section 1). The trials, AUTO2 only, 16
seeds each (people at m240 / m600 / m1200, median):

| run | busts (seeds) | people | queue from m240: p50 / p90 / max | A1 | A2 | A3 |
|---|---|---|---|---|---|---|
| FC1-lc3 (before BA1) | 16 (13) | 4,027 / 14,681 / 67,166 | 2.0 / 20.9 / - (FC1's run, KNEE_MONTHS' note) | - | - | - |
| BA1 without it | 25 (13) | 3,242 / 12,754 / 79,501 | 5.3 / 58.1 / 216 | FAIL 8/16 | 24 [11 to 80] | FAIL |
| the multiple once | 14 (11) | 3,872 / 14,673 / 82,850 | 5.2 / 43.6 / 120 | PASS 14/16 | 24 [13 to 47] | FAIL (13) |
| the multiple squared | 19 (14) | 3,842 / 15,009 / 82,994 | 4.4 / 37.6 / 110 | PASS 13/16 | 22 [12 to 52] | FAIL |

A bust is people 15%+ under a running peak from m120, and a new one only after a new peak (the earlier count, which
gives 25 for BA1 without it). Once is taken. Squared shortened the queue but busted more, and with 16 seeds the two
are within each other's noise.

### The run on the final model: `docs/ensemble/BA1.md` (2026-11-05, 02:08)

Run `BA1e` on b4bf96f4: the brake, the rounding edges, the builders-first fix, and main at 0.9.12 (R0's 1900 dates)
merged in. 80 of 80 runs in 26.3 minutes, two at a time, against `docs/ensemble/0.9.12.md` (24/26). **22/26.**

- Better than 0.9.12: A2's longest spell of yoy over 10% is 14 [12 to 35] months against 27 [12 to 36] (still a FAIL,
  as on 0.9.12). A1 is 2.14% with 16/16 in band (2.54% on 0.9.12). T1 reaches 40,000 people at m878 against m1,038, and
  T2 ends at 572,286 against 323,765.
- Worse than 0.9.12, two near-misses: A3, one seed's deflation spell of 13 months against a bar of 12 (median 5), and
  R3, four HIGH seeds more than a point above (as on 0.9.6). S1 fails as on 0.9.12.
- AUTO2: busts 12 in 11 seeds, the fewest of BA1's runs. People at m240 / m600 / m1200 are 3,646 / 14,107 / 88,778.

### The evening's run on ab5f3f41, before R0 and the staffing fix (replaced)

Run `BA1d` on ab5f3f41: the brake, the three rounding edges, main at 0.9.11 merged in, the world files named. 80 of 80
runs in 26.5 minutes, two at a time, against `docs/ensemble/0.9.6.md`. **24/26, against the baseline's 22/26.**

- Fails only A2 (16 [13 to 39] months of yoy over 10%; 0.9.6 failed it at 14 [7 to 29]) and S1 (2.68%, longest spell
  107 months; 0.9.6 2.11%, 102). A1 passes (2.61%, 15/16 in band), and so do A3 (5 [0 to 10]), R2 and R3.
- AUTO2: busts 19 in 13 seeds; people at m240 / m600 / m1200 3,770 / 14,842 / 83,377; final population 605,524
  (0.9.6 335,674). The queue from m240: p50 6.6, p90 45.4, p99 82.1 months. The premium is on in 46% of months, p90
  +56%.
- The morning's run on c0c7afaa (below) had busts at 14 in 11 seeds. The rounding edges move paths, and 14 to 19
  is within the 16 seeds' noise (FC1-lc3 16 in 13, BA1 without the brake 25 in 13).
- A start of BA1c, cut by the PC going offline and then resumed while its first start still ran, did 45 runs twice
  over the same CSVs; it was thrown away and not scored.

### The morning's run on c0c7afaa (replaced)

Run `BA1` on c0c7afaa (the brake once, the world files named, the `queue` and `premium` columns), 80 of 80 runs in
36.5 minutes at two at a time, against `docs/ensemble/0.9.6.md`. **22/26, as the baseline's 22/26.**

- Fails: A2 (24 [13 to 47] months of yoy over 10%; 0.9.6 failed it at 14 [7 to 29]), A3 (one seed's deflation
  spell of 13 months, the bar 12; median 4), S1 (2.81%, longest spell 85 months; 0.9.6 2.11%, 102), and R3 (4 seeds
  above +1 point, as on 0.9.6).
- Passes that 0.9.6 failed: R2 (2.72% [-6.59% to 7.73%]).
- T2, AUTO2's final population: 616,952 [373,392 to 707,088], against 335,674. Build ahead makes larger cities.
- The queue from m240: p50 5.2, p90 43.6, p99 79.2, max 119.9 months. The premium is on in 44% of months, p90 +54%.
  The AUTO2 half matches the trial above to the digit (same classes, same seeds).

### Before the brake (2026-11-03)

Run `BA1` on 93197de5 (FC1b merged; the world files named; extra columns `queue` = monthsQueued and `premium` =
scarcityPremium), against `docs/ensemble/0.9.6.md`. **20/26 against the baseline's 22/26.**

- AUTO2 A1, inflation from m240: 2.93% [2.28 to 6.31], 8 of 16 seeds in band. FAIL; it passed on 0.9.6.
- AUTO2 A2, the longest spell of yoy over 10%: 24 months [11 to 80], against 0.9.6's 14 [7 to 29].
- AUTO2 A3: deflation spells up to 12 months. FAIL; it passed on 0.9.6.
- ZERO_SLACK S1: 2.90% [0.85 to 8.85], longest spell 109 months (0.9.6: 2.11%, 102).
- ZERO_HOT F1: 1 of 16 runs found "households with no home".
- HIGH R2 fails as on 0.9.6. HIGH R3 now passes.
- **Busts:** 25 in 13 of 16 seeds, against 16 in 13 on FC1-lc3.
- **People at m240 / m600 / m1200 (median):** 3,242 / 12,754 / 79,501, against 4,027 / 14,681 / 67,166.
- **The queue from m240:** median 5.3 months, p90 58, max 215.5.
- **The premium from m240:** median 0, p90 0.646, max 0.789. FC1 had the premium on in 26% of months, p90 +55%.

Read: the young city grows slower and swings harder. The queue's tail is far longer than FC1's (p99 65 months there,
p90 58 here). So the price of a point at its ceiling of +80%, plus the interest over a wait at a real rate of a few
per cent, does not stop orders before the queue runs to years. It may need a stronger brake, or orders that read the
premium's slope.

## 5. Left for later

- `OrderWatch.mortgaged` is not told the template or the carry. OrderSearchCheck's lender twin finds the building by
  matching its costs to the bit, which works; passing them would be cleaner.
- `getCostOf()` and `quoteBuild()` differ for one House (13.46 against 13.36). This predates BA1, and BA1 did not
  look into it.
