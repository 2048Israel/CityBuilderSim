# BA1 - Every business may build ahead ({next}, 2026-11-03)

**WHERE IT STANDS (2026-11-04, lane 2; no ship file):**
1. On main at 68fc54ce (CB1 0.9.9 merged in). The brake is in and committed: **dear contracts** (section 1, ★).
2. **The ensemble holds** (section 4): `docs/ensemble/BA1.md`, **22/26, as 0.9.6's 22/26** (BA1 without the brake:
   20/26). A1 passes again (14/16 in band) and R2 now passes. A3 fails on one seed's 13-month deflation spell (the
   bar is 12). A2, S1 and R3 failed on 0.9.6 too. AUTO2's busts are 14 in 11 seeds (FC1-lc3 16 in 13, BA1 without the
   brake 25). People at m240/600/1200 are 3,872 / 14,673 / 82,850 (FC1-lc3 4,027 / 14,681 / 67,166), and the final
   population median is 616,952 (0.9.6 335,674). The queue from m240 is p50 5.2, p90 43.6, p99 79 months (58 without
   the brake), with the premium on in 44% of months.
3. **Left: the fixtures, two findings, then `--pick`.** A named run of the 14 harnesses most likely touched, on the
   brake and the world files (AllChecks -f, 414 s), passed 8: InvestCheck, OrderSearchCheck, BuildCardCheck,
   WantedPostsCheck, ConstructionTrapCheck, BuildersFirstCheck, RefineryCheck and CentralBankCheck (CB1 mended it).
   Red:
   - **fixtures on a moved path, mended (Sonnet slice, reviewed; each green alone):**
     - RailDepotCheck: the played city is on the goods' literals, where the plant lands ore and coal and ships pellets
       (about 131,000 t of bulk against the sidings' 100,000). On the world file's band it ran no month, and the mines'
       ore alone was 75,489 t.
     - OilCheck 5 and 19: the diesel tanks are filled before the month (in 19 every fourth month). The railway, with the
       freight orders uncapped, now draws about 1.19M L against a shelf of 0.95M, so it imported the rest and the vans
       got none at home.
     - SaveFileCheck: the hot money's stock is set to half last month's target before each waited month, so money
       arrives. Its city never had the target over the stock, so the loop ran its whole 240 months to month 392, where
       the bank had failed and the bonds and mortgages were gone. That emptied every fixture after it.
   - **finding, NewGameCheck 11** ("by no more than one issue granule plus the fees"): not a fixture. The solver
     returns the smallest face that covers, a granule at a time, but proceeds jump by more than a granule where the
     coupon rounds to an eighth (the sawtooth the harness's own later comment names). This gap, 3,770.17k, lands on
     a jump: one granule down is 9.67k short, and the face that covers is 52.43k over against a bound of 2.90k. Moving
     the fixture off the jump would hide an overshoot of up to about 1.4% of the borrowing. ★ Taken as recommended
     (the orchestrator, 2026-11-04 11:58; ★ calls below).
   - **finding, PriceLevelCheck 8 and 9** (the price-level twins part). Without the brake (a switch tried and taken out)
     only 9 parts, slightly, from month 25 (cash 5e-6 apart at month 33): the kind of tie section 6's town had, which
     11-03 moved to TOWN_MONTHS 33. With it 8 parts too: in MapCheck's played city heavy industry's books run 6 months
     in one twin and 3 in the other, so one order clears in one twin only. Find the test an order sits on the edge of
     (`DEAR CONTRACTS` multiplies `carryOf()`; the halving searches and Mortgage's down-payment edge are where 11-03's
     tie was). Do not move the months to dodge it without naming the tie.
   - **finding, ScaleCheck** "a copy 1,000 times over tracks its city": people 1.59e-2 against 1.50e-2, unemployment
     3.80e-2 against 3.30e-2. It passes without the brake. The cause, read in `BuildersShort.meanPremium()`: building i
     of an order is priced at the queue where it starts (`fromMonths + i * stepMonths`), one building's points a step.
     The city's one plant pays the premium at the head of its span; the copy's thousand, each a thousandth of the step,
     pay the mean across the same span, which is more on a rising curve. So the price was never invariant to scale for
     a large building, and the brake counts the gap twice. The fix that keeps it invariant: price each building at its
     own span's mean (start plus half a step, or the integral over it), which ends "one building is priced exactly as
     FC1 priced it" (ConstructionTrapCheck 12 asserts it). ★ Taken as recommended (the orchestrator, 2026-11-04
     11:58; ★ calls below).
   - not run: MapCheck and LongPlaytest (11-03: a map building moved at month 1809, main's known plan bug, which main's
     own nightly shows at 3010-3013).
   Then `./batch-check.sh --pick` on the world files (JDK_JAVA_OPTIONS naming the main checkout's data/earth-2.bin and
   data/world-1.bin; without them PriceLevelCheck 6 and the map harnesses read another world), then the ship file.
4. The sector screen's planner line names dear contracts too (SectorScreen, "No business is held to one order").
5. Next, once the PC is back, in order:
   - build the two ★ calls below (one Sonnet slice);
   - rerun ScaleCheck, ConstructionTrapCheck and NewGameCheck;
   - rerun the ensemble, since the span call moves the price of a large building, and commit it over
     `docs/ensemble/BA1.md`;
   - trace PriceLevelCheck 8/9's tie to its edge, with no months moved to dodge it;
   - run `--pick` (95 of 170 harnesses, LongPlaytest for Game.java), then write the ship file. On the solver: if its
     overshoot is a cost a player sees, a `## queued` row in the ship file.

**★ calls (taken as recommended by the orchestrator, 2026-11-04 11:58; not built yet):**
1. **NewGameCheck 11 asserts what the solver keeps:** the face one granule down does not cover. The overshoot on the
   coupon's eighth sawtooth, up to about 1.4% of the borrowing, is written there as a known property of the solver, not
   a bound it keeps.
2. **Each building of an order is priced at its own span's mean** (`BuildersShort.meanPremium()`): the queue its
   points stand in, from its start to a step past it, not the queue at its start. So the price is invariant to scale,
   and ScaleCheck's K-times copy pays what its city pays. ConstructionTrapCheck 12's "one building is priced exactly as
   FC1 priced it" is restated as "one building pays the mean of FC1's curve over its own span". The reason: FC1 priced
   at the head of the span, which undercharges a large building on a rising curve. A copy of a thousand buildings,
   each a thousandth of the span, paid the true mean. Dear contracts count that multiple twice, so the gap took
   ScaleCheck past its tolerance.

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
