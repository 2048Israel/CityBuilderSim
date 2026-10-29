# RE1 - Real Estate's workers, once the stall they cause is found

Queued 2026-10-29 by the orchestrator, from HD1 (item 2), on the HD1 session's
recommendation. Jerus, 2026-10-28: "gotta add workers to real estate". The
landlords let every home in the city and employ nobody: their three buildings
(ids 0, 1, 6) have no `jobs`, against $57B a month of rent and $48.4B of
operating income in his San Francisco city at m2,852.

## What HD1 built (commit fec704a7, reverted in f3704d8d)

- **The posts**, by the National Apartment Association's "Best Practices:
  Staffing" (naahq.org, 2020): one full-time office employee and one full-time
  maintenance employee per 100 units, the next once a property passes 51 units
  toward the next hundred (its 2019 survey: about one employee per 45 units on
  site). So Studio Apartments (80 units) and Low-Rise Apartments (63) take 2
  DIPLOMA posts each (the BLS handbook's entry education for both jobs is a
  high school diploma); a House (1 unit) none - its repairs are already the
  builders' order. In `buildings.json` and `BuildingManager`'s templates
  (`BuildingDataCheck` compares the two).
- **Homes before workers.** `Sector.ordersOnlyWhatItCanStaff()`, false for
  `RealEstate`: `RealEstate.plan()` never asked the staffing gate, and the
  build card (`BuildCard.gate`, its marks and its staffable figure) no longer
  shows one for a landlord's block. Capacity and rent never read staffing.
- `RealEstate.rentBreakEven()` carries the payroll.
- HousingCheck section 6 ("the landlords build before anyone can staff them")
  and the fixes below. All of it is in fec704a7: take its files but
  `HistorySave`, `YearBook`, `ui/HistoryScreen` and SaveFileCheck's section
  13b, the bonds series, which shipped in HD1.

## What it broke - prove the cause first

**The stall.** The ensemble (80 runs against 0.8.68): medians held (AUTO2
final population 255,458 against 255,250) but seed 4 stalls. AUTO2 seed 4,
final population: main 249,634; HD1's coupon alone 241,339; with the posts
44,157 (25,028 at m1200, 26,217 at m1800). ZERO_SLACK seed 4: 30,090 against
the baseline's lowest seed at 133,876. Seeds 7 and 0 reach 40,000 people at
m1,789 and m1,401 (the baseline's latest, m1,471).

Seed 4 parts from the run without posts at about m1,100: at m1,200 it has
25,030 people against 34,430, rent 3.27 against 2.45 (a person of capacity, a
month), the price index 10.6 against 7.7 and the D$ at 8.6 per US$ against 5.6.

**Taking the payroll out of `rentBreakEven()`** (one run, the posts kept): the
stall still begins at m1,100 and holds to about m1,500 (25,395 people), rent
at m1,200 is still 3.25, and the city then recovers to 145,033. So the floor
explains much of the damage but not the onset. The reading to prove: the
rents rise for another reason that the posts start (the landlords' own posts in
`plan()`'s latent demand, about 2.8% more on a 160-head block; their wages in
the lender's net operating income; or 2 DIPLOMA posts a block taking workers
other sectors need), and the floor then holds them over the affordability
yardstick, so migrants are priced out. Seed 4 under AUTO2 is the fixture: the
Ensemble driver, `ham.citybuildersim.Ensemble AUTO2 4 <csv>`, about a minute,
stalls with the posts and does not without them. One run per candidate, each
switched off alone, finds which.

**The harnesses it moved** (the full suite with the posts, 2026-10-29):
- **ScaleCheck**, section 9: the 1,000x copy's treasury misses K times the
  city's by 7-10% in its first month (main 0.2-0.6%, the coupon alone
  0.6-2.7%), and no fixture month from 828 to 964 passes. One month of the
  K = 1 and K = 1000 loads compared line by line: the landlords' profit and
  payroll scale exactly, and the paper auction, subsidies and arrears match;
  the treasury's miss mirrors Retail's pool (about -8.3k against +8.8k a
  K), an internal flow not found. Section 6's fixture month (807) needs its
  rescan as well: on the posts' trajectory the town is growing and calm only
  at 832-848 and 965-977 (the pre-scan; a scratch scanner did it in a minute).
- **HealthCheck**: "...and the households who skipped a bill ate with it".
- **OrderSearchCheck**: its three fixture lines (the copy and the twin judged
  orders past Game.COUNTDOWN_SLICES).
- **SaveFileCheck**: its fund fixture's city has its bank fail at m153 (the
  landlords' net income about +580 to -280 under the squeeze, $39B written
  off); HD1 re-caused it by recapitalising a failed bank before the fixture
  put it under its minimum. That may be a balance finding, not a fixture one.

## Scope

`buildings.json` and `BuildingManager` (ids unchanged), `RealEstate`,
`Sector`, `BuildCard`, whatever the stall's cause is; HousingCheck section 6
back; the harnesses above at their causes. Every trace moves: the ensemble,
seed 4 above all. A version (`{next}`).
