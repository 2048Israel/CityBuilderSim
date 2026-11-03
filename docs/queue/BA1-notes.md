# BA1 - Every business may build ahead ({next}, 2026-11-03)

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
    the price and the interest on the wait ration orders.
  - The Construction tab's QUEUE mark is now the knee where the price of a point starts to rise
    (`BuildersShort.KNEE_MONTHS`, 9 months), on a bar to 33 months, where the premium is half its ceiling.

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

(filled in below when it finishes)

## 5. Left for later

- `OrderWatch.mortgaged` is not told the template or the carry. OrderSearchCheck's lender twin finds the building by
  matching its costs to the bit, which works; passing them would be cleaner.
- `getCostOf()` and `quoteBuild()` differ for one House (13.46 against 13.36). This predates BA1, and BA1 did not
  look into it.
