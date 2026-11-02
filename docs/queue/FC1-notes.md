# FC1 - Builders short: the price of a point rises, and foreign crews come when none are free ({next}, 2026-11-02)

Jerus's report on his own city (2026-11-01, `docs/queue/FC1.md`): every crew repairing, nothing built, no way out.
The month's repairs take the builders' points before the sites (`Game.getBuildingOutput()`); when they take them
all, the builders' own depot (their plan: "the repairs take every crew it has") waits for points the repairs never
leave, and the price of a point did not read the queue.

## 1. What was built

- **`BuildersShort`** (new, ~100 lines, pure): the dials and the arithmetic.
  - `KNEE_MONTHS` = `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` (9): no premium at or under it.
  - `HALF_MONTHS` 24: half the ceiling that far past the knee.
  - `CEILING` 0.8: the most the premium adds, with no points left for sites.
  - `FOREIGN_FLOOR` 0.05: under this share of the builders' month (every post offered) the sites' points count as
    none, and the foreign contractor takes the orders.
  - `FOREIGN_PRICE` 2.0: its price, a multiple of the city's price before any premium.
  - `FOREIGN_CAPACITY_SHARE` 0.25: its month as a share of the builders', never under the largest depot's points.
  - The premium is `CEILING / (1 + 1/x²)`, x = (months − knee) / HALF_MONTHS: no slope at the knee, the ceiling at
    infinity, never NaN. The months are the queue over the sites' points at every post; infinite with none.
- **The price.** `BuildingManager.orderPriceMultiplier()`: the contractor's price while it takes orders, else
  1 + the premium; exactly 1 in a bare manager (no Game's months to read) and while the builders have no month at all
  (a founding's first). Every order's work carries it, not its material, land or repairs:
  - `Game.quoteBuild()` and every part of the sticker (the work, a foothill's preparation, a run's works, a span,
    deep water), so the parts still sum;
  - a run's invoice prices each order on the queue the orders before it leave (`GroundAhead.queue`), so the invoice
    and the placing agree to the bit (BuildCardCheck 9);
  - demolition, restart and paving quotes;
  - `BusinessInvestment.totalCostOf()`, so the planners see the price they will pay.
  - `nonMaterialPricePerPoint()` (what a depot earns, `Construction.estimatedMonthlyProfit()`) is now
    `basePricePerPoint()` × (1 + premium): the premium pays for the builders' own depots. The contractor's multiple
    never enters it.
- **The foreign contractor** (`Game`, THE FOREIGN CREWS; `sectors.Construction`, THE FOREIGN CONTRACTOR'S BOOK).
  - While it takes orders, `Construction.bill()` puts every order's points on its book. A depot the builders serve
    first is kept off it (`bill(amount, points, t)`), because the contractor builds those as the builders' own.
  - Each month `foreignCrewsOffer()` offers the sites its book plus, while it takes orders, the builders' depots on
    site, up to its capacity. SimulationEngine adds that to the sites' output, and the BUILDERS' OWN DEPOTS FIRST rule
    serves the depots off the top as ever.
  - After the advance, `settleForeignCrews()` pays it for what the sites took past the city's own month, at its price
    struck before the advance. The builders act as general contractor: they earn every order as before and book the
    contractor's bill with `bookImportedService("Foreign crews", ...)`, an import of services. That puts it on the
    money audit's "- Construction Imports" TRADE line and the current account. One log line a month it builds.
  - It stops taking orders once the sites' points are back over the floor, and works off its book.
  - Its book and month are saved only when nonzero, so a city that never met the trap saves what it always did.
    There is no SAVE_FORMAT bump: an old save has no keys and reads 0.
- **The screens.**
  - The build card adds a line under its price row: `builders short: +27% on the work · ≈ 31.0 months queued` (WARN),
    or `foreign crews: ×2.0 on the work, paid abroad` (BAD), with a tooltip built from the constants.
  - The Construction page's gauge has a fifth cell, PRICE OF A POINT. Once the contractor has built or booked
    anything, a FOREIGN CREWS line says what it built last month, what was paid abroad and what is still its own.
  - NEEDS YOU's BUILDERS row reads `all on repairs: foreign crews at ×2.0` (severe) while the contractor takes
    orders, and `short: +NN% on every order` past half the ceiling.
  - LongPlaytest's build trace was left alone: it is a fixed-column CSV, and its price column already carries the
    premium.
- **`ConstructionTrapCheck`** (new, 8 sections, 134 assertions; README 169 harnesses). The fixture is
  DeathCareFirstCheck's city with 53 standing roads, one at a time past the model's own figure, until at month 37
  the repairs take 441.6 of a 458-point month. The sites then get 16 points against a floor of 22.9; one road fewer
  left them 23. The harness shows:
  - A House's quote at five states: ×1.000 at 7.2 months queued, ×1.173 at 21.6, ×1.432 at 35, ×1.743 at 95, and the
    contractor's ×2.000 under the floor. The planners' cost rises with it, and an order raises the next one's price.
  - The builders' depot is built in one month out of the contractor's 400 points, and its bill is the builders' import
    to the cent.
  - The city climbs out the next month. Its plan opens 10 more depots (nameplate 560 to 4,720) while the contractor
    works off a 30,600-point book over 26 months.
  - The bills (6,966.21) equal the imports. A twin that never meets the trap pays nothing abroad, at a multiple of
    exactly 1. The book is saved and loaded to the bit.
- **Harnesses the change reached.**
  - BuildersFirstCheck 6: the new town's founding months (sites 0 of a 130-point month) are now the trap in miniature,
    and the landlords do not order at ×2, so the fixture puts 10 of their Houses on site in both twins before the
    comparison, as DeathCareFirstCheck's fixture does. The claim tested is unchanged (61 homes against 59).
  - PriceLevelCheck 9: the town's horizon goes from 55 to 34 by the header's own recipe (35 before LC3's world file,
    which parts it a month sooner). From month 35, today's town parts from the factor's, the factor nudged by 1e-12 and
    today's nudged by 1e-12, which all run equal: a tie in a planner's cost (`totalCostOf`, the work times the
    multiple), not a price the factor missed.
  - ConstructionControlCheck: a restart's and a demolition's work are expected at the builders' multiple.
- **The founding card** (`Founding.whatItBuys(..., multiple)`, through `Game.whatItBuys()`) prices the village order
  by order on the queue it builds, and each first work on the queue the village leaves, at the founded city's
  multiple. After the village the queue is 10.6 months at the yard's 156 points, ×1.0034, which is what the build
  card quotes; NewGameCheck holds the two equal.
- **The full suite then found fourteen harnesses whose founded towns FC1's price moves.** Through a new town's first
  years its queue reads hundreds of months at one yard's pace, so most orders carry a premium near the ceiling. Each
  red was proved FC1's (green with FC1 off), then mended so the fixture causes its condition again, the claim
  unchanged:
  - MortgageCheck 9: the works stand, so its House is priced under the knee.
  - BuybackCheck 8: the rate is pushed until the line is under its books.
  - ForeignCheck: the programme is bought whole in both cities.
  - NewGameCheck 11: the probe asks a granule and a half down, off a floating-point edge the new path landed on.
  - LabourCheck: the town is stood free, as its depots and roads already were.
  - ManufacturingCheck 5: its planners are held, as its section 6 holds the mills. The town has no depot, so before
    FC1 their orders were never built (the trap). With the contractor they are (5,400 t of fabricating capacity
    where the section's two shops are 2,400).
  - AuctionCheck 10: eight standing depots, and a guard that the builders are not short. Its repairs ate the sites'
    month to nothing by month 39, the contractor took the orders, and an ask struck at 442.85 was placed at 612.15.
  - ScaleCheck: `FREE_FIXTURE_MONTHS` 718 to 717 by its own documented scan (34 months scanned; only 716 and 717
    hold every line). ST3 is still the fix.
  - DeathRecordCheck 3, **for the gate's eye**: the claim ("the orphan section does not fill behind it") is now
    measured against a twin whose mill never closes, not against the year before's most plus one. Under FC1 this
    town's orphans drift upward on their own: 17.47 at the peak with the closing, 19.07 in the open twin. The twin is
    the exact control the old bound stood in for; the old rule would still show about +245 over it.
- **Five harnesses play their towns without FC1**, through a JVM-wide test switch, `BuildingManager.buildersShortOn`
  (harnesses only; AllChecks runs each in its own JVM). Each has a comment with the figures. In every one the claim
  is not about building, and FC1 moves the town onto a path that would need every section retuned or that reaches a
  defect elsewhere:
  - HoldersCheck: its town was itself caught in the builders' trap (no depot ever built, 206 people). FC1 frees it
    and it booms (a price index of 6 by month 120, the 2-year yield 17%), while the sections read who holds the
    city's paper on a calm curve.
  - MonetaryCheck: its 52,000-point hospital, ordered only to keep the crews busy, puts every other order near the
    premium's ceiling, so the premise's steady town is not steady (the 40% row moved 0.134 against a 0.100 allowance).
  - DenominationCheck: its hot town's reformed twin parts at month 170, by 1e-9 then chaotically, through an
    absolute tolerance outside FC1 (FC1's multiple and queue are equal in both twins then). That is a real finding,
    queued as DZ1 (`docs/queue/DZ1.md`) to find the tolerance and turn FC1 back on there.
  - SectorStatementCheck: on FC1's path the bank buys D$1,113M of city paper in month 134. In 135 its unearned
    discount falls 7.06 with no named cause, and "its equity's parts are its equity" is off by 7.05 in 226 of 479
    bank-months. That is a bank statement defect (BK1's class; the money audit holds), queued as BD1.
  - MapCheck 2h: on FC1's path, 12 and 23 standing boxes move in the district plan four months after a reserve
    (0 without FC1), though the plan says what stands never moves. A planner defect, queued as DM1. The switch covers
    section 1's played city (2h's scenarios are cut from it) through 2h, and is back on for every later section.
- **After LC3 merged** (0.9.1 and its re-baked world-1.bin), the full suite on that file found two more, both FC1's
  (green with FC1 off), and both mended as above:
  - PriceLevelCheck 9: the same tie a month sooner, so 35 became 34.
  - OrderSearchCheck 2: under FC1 the playtest's town grows more slowly through its founding. Its copy has 2.5M
    people at month 524 against 10.4M with FC1 off, though both towns are near 15,000 by month 1200. The twin's
    largest order is 4 Filling Stations, which no till trims past the counted slices. FR8 already sets the twin's
    tills; the twin's order is now handed to the search as at least `TWIN_LEAST_ORDER` (64) of its kind. It is built
    16 and 32 on the two tills, both the countdown's answer, and an order of 64 or more goes as the twin decided it.

## 2. The calls (★; each takes the recommended way, settled by the orchestrator on Jerus's "Run both tonight")

- ★ Foreign crews are a last resort at about twice the local price, above the scarcity ceiling
  (`FOREIGN_PRICE` 2.0 against 1 + `CEILING` 1.8): a city never prefers them.
- ★ Repairs keep their place before sites: nothing in the repairs changed, and their bill carries no premium.
- The premium reads ALL the work queued against the builders' site output: the points owed on site over what the
  repairs leave the sites, every post offered (the gauge's QUEUE, the builders' planner's months). Each order is
  priced on the queue as it stands with the orders before it that month, not counting its own points.
- Every dollar figure goes through `PriceLevel.FACTOR`: FC1 adds no money constant. The premium and the contractor's
  price are ratios on `nonMaterialCost()`/`basePricePerPoint()`, which already move with the level and wages.
- For the gate: five harnesses (HoldersCheck, MonetaryCheck, DenominationCheck, SectorStatementCheck, MapCheck) play
  with FC1 off (section 1, why). The alternatives were retuning every section to a town FC1 sends elsewhere, or
  mending a bank statement and a district planner overnight, outside this batch. The three defects are queued
  (DZ1, BD1, DM1), each to turn FC1 back on in its harness. The switch is static and harness-only; the ensemble and
  ConstructionTrapCheck cover FC1 itself, and DeathRecordCheck's new twin control is the one changed measurement.
- Decided here, no star:
  - The knee is the builders' own expansion threshold, 9 months, so the price and their planner read one queue.
  - The contractor keeps building what it took after the city's crews recover, as a contract, at its price.
  - The city's own crews count first, and the contractor is paid only for points past their month.

## 3. The trap, counted (the 16 AUTO2 seeds, before and after)

On main (`FC1-before`: AUTO2 only, the builders' figures added as `-Densemble.extra` columns, not a baseline) the
trap - the sites' points at every post under 5% of the builders' month with work owed - was met in **0 of 38,384**
city-months (also 0 at 15%). The only months with no points for sites are each city's month 2, before any crew
exists. Months queued from month 240: median 1.95, p75 9.2, p90 20.9, p99 72. So Jerus's trap needs his own city's
"specific sequence of events"; `ConstructionTrapCheck` causes it.

After (`FC1`, the same columns): the trap was met in **0 of 38,384** months, and the contractor never took or built
anything. The premium was on in 27.1% of months from month 240, averaging +4.9% (median +9% when on, p90 +55%, at
most +76%). The queue barely moved: median 2.16 months, p75 10.0, p90 21.2, p99 67.

On LC3's world file (`FC1-lc3`, the merged tree, the same columns): again **0 of 38,384**, and no foreign book. The
premium was on in 26.1% of months from month 240, averaging +4.7% (median +9.0% when on, p90 +55%, at most +77%).
The queue: median 1.89 months, p75 9.5, p90 21.0, p99 65.

The playtest's trace moved (the premium is on through every city's founding queue), so the full ensemble ran twice.
On the world file before LC3's, against `docs/ensemble/0.8.95.md`, it passed 23/26: the baseline's three reds, each
nearer its line (A2 20 to 13 months, S1's spell 107 to 65). After LC3 merged, on its world file, against LC3's
`docs/ensemble/0.9.1.md` (22/26), it passed **22/26**: the baseline's four reds, three nearer their line and S1
further. That run is committed as `docs/ensemble/FC1.md`.

| measure | 0.9.1 (LC3) | FC1 |
|---|---|---|
| A1 (AUTO2 seeds in band; median) | 16/16, 2.40% | 16/16, 2.26% |
| A2 (longest spell of yoy > 10% from m240) | 16 [12-33] | 14 [7-29] |
| S1 (median excess; longest spell) | 2.03%; 86 | 2.11%; 102 |
| R2 (unemployment minus twin, median) | -1.22 | -1.15 |
| R3 (seeds above +1 pt) | 7 | 4 |
| T1 (month 40,000 people reached) | m968 | m970 |
| T2 (AUTO2 final population) | 345,461 | 335,674 |
| young city's inflation, m240-600 | 4.16% | 3.73% |
| M1 / F1 | 0 / 0 | 0 / 0 |

For the gate's eye: S1 reads the 0% policy rate held from month 1800, long after any founding queue. On the earlier
file FC1 moved it the other way (65 against 107), so it reads as a seed draw, not FC1's.

## 4. Checks

- `./batch-check.sh`, the whole of it (the suite, the maps, Stale, the tree), before LC3 merged: **exit 0**. All
  170 green in 2,025 s. On the merged tree with LC3's world file, 168 were green, and two NEW reds (PriceLevelCheck,
  OrderSearchCheck) were proved FC1's and mended (section 1); each was then green alone. Timing assertions were
  soft, as every side-by-side run's are (timing is the gate's). Stale had 0 firm findings, and the maps are
  regenerated in the batch.
- The first whole run, before the mends, found fourteen harnesses NEW red (57 lines): the towns FC1's price moves,
  section 1. Each was proved FC1's by running it with FC1 off, then mended or played without FC1 as said there.
- ConstructionTrapCheck is new (134 assertions); README says 169 harnesses.
- The ensemble's second run (section 3) ran on the merged tree's model (b4ba0e66). After it, only OrderSearchCheck
  (a harness) moved.

## 5. For the next batches

- **BA1** (every business may build ahead, no cap on orders) relies on this price.
  - Each order is priced on the queue with the orders before it, a run's invoice included, and the planners'
    `totalCostOf()` carries the multiple.
  - What BA1 must still show is that the planners' gates actually turn an order down at the multiplied cost (their
    payback and interest tests read `getCostOf()`), and that the premium, not the cap, now bounds the queue.
- The contractor's capacity floor is one Construction Depot's points (400). A city whose repairs exceed its builders'
  month by many depots climbs out a depot a month, as the builders' plan orders them (MAX_CONCURRENT_ORDERS).

## 6. The manual (for the orchestrator: section 9, a new subsection after "Two sites go first")

### When the builders are short

**The trap** (FC1, 2026-11-02). Jerus found his own city with every crew repairing. The month's repairs come off the
builders' points before the sites, and when they take them all, nothing on site advances. That includes the depot the
builders ordered to get out ("the repairs take every crew it has"), which waits for points the repairs never leave.
Two things now answer it (`BuildersShort`).

**The price of a point rises with the queue.** Every order's work is priced at the builders' multiple; its material,
its land and the repairs are not. The queue is read at the pace the repairs leave the sites, every post offered:

- At nine months or less (the months the builders expand at), there is no premium.
- Past that, a premium rises smoothly: half its ceiling 24 months past the knee, and +80% with no points left for
  sites.

Each order is priced on the queue as it stands with the orders before it, so every order makes the next one dearer.
The planners see the same price and back off while the builders are short. The builders earn what pays for their own
depots, since a depot's profit reads the premium. The build card says so under its price, and the Construction page's
gauge in PRICE OF A POINT.

**Foreign crews when none are left.** Once the sites get under 5% of the builders' month, a foreign contractor takes
every new order at twice the city's price of a point. That is above the premium's ceiling, so no city prefers it.

- It builds the builders' own depots on site first, at a quarter of the builders' month and never less than a depot
  a month.
- The builders stay the general contractor: the order's money goes into their book as any order's does, and they pay
  the contractor for the points it built past their own month, at its price. That is an import of services: the
  money leaves through the current account.
- It stops taking orders once the sites have points again, and works off what it took.
- The Construction page names it, and NEEDS YOU's BUILDERS row reads *all on repairs* while it takes orders.

In `ConstructionTrapCheck`'s city the repairs take 441.6 of a 458-point month. The contractor builds the builders'
depot in a month, and the city is out of the trap the next. Its builders then open ten more depots while the
contractor works off its 30,600-point book over 26 months. In the 16-seed ensemble no city met the trap, before FC1
or after. The premium was on in a quarter of their months, +5% on average.
