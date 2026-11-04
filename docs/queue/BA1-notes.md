# BA1 - Every business may build ahead ({next}, 2026-11-03)

**WHERE IT STANDS (2026-11-04, 14:40; no ship file yet):**
1. On main at 7cfcb666 (WN5b 0.9.10 merged in). The brake is dear contracts, as the ensemble of the morning measured it
   (c0c7afaa; section 1, ★). Since then the model has changed by three rounding edges only (item 3).
2. **★ calls (the orchestrator took both as recommended at 11:58):**
   - **NewGameCheck 11: built.** It asserts what the solver keeps: the face one granule down does not cover. It prints
     the overshoot as a known property of the solver: 52.43k over a gap of 3,770.17k, 1.39%.
   - **Each building priced at its span's mean: built, measured, withdrawn.** It made ScaleCheck worse (people 3.72e-2
     apart against 1.59e-2), and the 5-billion copy's "an order of more than a million buildings" fixture failed. The
     cause of ScaleCheck's miss was never the price (item 4): its miss is the same to the digit with the brake reading
     the queue's premium instead of the order's own. So BuildersShort and ConstructionTrapCheck 12 are as they were.
     Withdrawn on that evidence; one building is still priced as FC1 priced it.
3. **PriceLevelCheck traced to its edges, three of them, each a hair of rounding that one price level read and the
   other did not. All three are fixed and no months were moved; the four twins hold to 1e-9 (worst 7e-14):**
   - Section 8, month 23: a staffing share of 1.3e-18 at today's prices and 0 at the factor. Manufacturing keeps the held
     template with the largest share, so it held a Machine Works at one level and a Fabrication Shop at the other. Now
     a spare within `Sector.SPARE_ROUNDING` (1e-12) of the band's posts is none.
   - Section 9, month 26: one cell's debt of 1.1e-16 since month 4 at today's prices, 0 at the factor. `Exchange.mayBuy()`
     read any debt above nothing, so the cell sat out a share offering at one level only (the mechanism the harness's
     own note describes at month 56). Now it reads a debt as `HouseholdBalance.owes()` does, above the balance's floor.
   - Section 9, month 29: the household waterfall's tail a hair above zero (3e-21), borrowed as a debt. It was a draw of
     the level exactly 1: the factor, the factor nudged by 1e-12 and today's nudged by 1e-12 ran equal through month 33,
     and only 1.0 parted. Now a remainder within `Household.SHORT_ROUNDING` (1e-12) of the month's gap is covered, the
     twin of the clamp `fundShortfall()` already had for a hair below zero.
4. **ScaleCheck 9 is its scaler, not the brake.** `scale()` multiplies `constructionProgressById` by K, but a stack's
   progress is held against its current unit only. So the copy's one current unit holds K units' worth and finishes
   hundreds at once (1,000 roads on site fall to 547 in month 1, against the city's 1). Build-ahead saves the grown
   city with far more on site. The fixture mend: read the copies from the grown city once its sites are empty, every
   sector held (Sonnet slice, with MapCheck 2h).
5. **`--pick` on 8d1659f8 (the morning's model, main merged; 13:53, 95 of 170 harnesses, LongPlaytest for Game.java), red only in:**
   NewGameCheck and PriceLevelCheck (mended since), ScaleCheck (item 4), and MapCheck 2h, whose second scenario no
   longer makes the crossing a cloverleaf site (a moved path; the same slice). LongPlaytest is green; on 11-03 a map
   building moved at month 1809.
6. Next: the ensemble on the final model (run BA1c, which replaces `docs/ensemble/BA1.md`), the slice's two mends
   reviewed, `--pick` again, the ship file.

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

- `--pick`: (filled in below when it finishes).
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

### The run: `docs/ensemble/BA1.md`

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
