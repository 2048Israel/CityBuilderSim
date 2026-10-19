# FR6 - the railway carries the city's own bulk, and the port yard: notes

*0.8.8, 2026-10-19. The batch is `docs/studies/spec-freight-and-infrastructure.md` §7's FR6 row: §3.3's railway, D4 and D7 (Jerus, 2026-10-12: "all the freighting just do rec"; D4 "after FR2 is measured, and D7, the port yard, rides with it"). The brief, with every number and its source, is [FR6.md](FR6.md). Built on main at aa37699 (0.8.7: FR2, FR7 and FR3). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; Claude wrote the brief, the map's fix (§3), the version and these notes. No save format change (40): the shares in force and the month's figures are new extras keys, which a save without them reads as 0, a month of trucks.*

## 1. What shipped

- **The city's own bulk on the railway's room** (D4, `Freight.haul()`): of every BULK tonne bought at home, the siding's part (`RAIL_ROAD_RELIEF` .75) is offered the railway after the boundary's bulk and goods are dealt, on what the track or the sets and the stations and sidings leave (`Freight.dealHome()`). The share it takes is in force for the month (`homeShare`), saved, and billed at the next haul "Rail freight" at **the railway's quote x Haulage's quote** a tonne (§3.3: the city leg's own rate); the trucks carry the rest (`Haulage.legTonnes()`, six arguments).
- **The port yard** (D7): with a station (a Rail Depot or a Rail Terminal) and a sea terminal standing (`hasPortYard()`), `PORT_YARD_SHARE` (.45, Montreal's rail on the quay, R70) of the BULK tonnes across the berths joins the same offer at the same share (`quayShare`), quay to door by rail.
- **The railway's own side**: the city tonnes burn `CITY_LITRES_A_TONNE` (.22 L, a third of a truck's), join the revenue its quote is struck over (the same allowed revenue over more tonnes), and enter neither its tightness nor its planner (★2).
- **The planner's weighing** (`Haulage.legPrice()`): a works is weighed with the truck's part of its legs at Haulage's steady quote plus the railed part at the railway's quote on it; `nameplateNeed()` and the trucks' need read the same weights, so Haulage buys toward fewer trucks.
- **The map** (§3): a reset rail net clears its hub (`CityRuns.Net.reset()`), so the map is the same map across a save after the last station goes.
- **Harnesses**: HomeRailCheck (new); HaulageCheck's bill mirror on the six-argument legs; LongPlaytest's "Home rail" line.

## 2. ★ calls

1. **★1 The siding's .75 is the railed part of a home tonne**, the number FR2 uses for the boundary's railed tonnes, not a second cap.
2. **★2 The city's own bulk fills the railway's room; it orders no track and makes no scarcity.** The planner ordering track for it is FR6-C's if Jerus wants it.
3. **★3 `PORT_YARD_SHARE` .45, Montreal's**: the one source that is rail on the quay (R70); Rotterdam's 41% left to rail and barge (R67) and Long Beach's quarter (R69) are boxes inland.
4. **★4 The port yard is a station and a berth standing**, not a district test (the model does not read the map) and not a building; the stations' room sizes it in the deal. The spec's "Rail Terminal" predates FR7's Rail Depot.
5. **★5 One share for home and quay tonnes**, dealt together pro rata, as the boundary's bulk is one share for the city.
6. **★6 The map's trains stay the boundary's**: `railIn`/`railOut` count crossing tonnes only; the city's own unit trains on the map are FR4's or FR5's.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the model (Freight, Haulage, HaulageCheck's mirror) | Sonnet | nothing. Its two additions kept: the city bill rescaled in a redenomination (`redenominateExtras`), and the step-4 revenue guarded against a NaN quote. It type-checked into a scratch folder while the baseline suite ran from `target/classes` |
| 2-3, HomeRailCheck and LongPlaytest's line | Sonnet | TBD |
| the playtests' reload | Claude | **a reset rail net clears its hub.** The default playtest flagged "the city map across a save" once, at m1186, after its last Rail Depot went: `Net.reset()` kept the hub (365320, 373800) and the net read back from the save had a fresh net's 0, 0. A latent FR7 edge - main resets its net too (0 depots at m3000 in its default run), at months no reload lands on; FR6's path put a reload there. Map only, so no model trace moves; found with a probe copy of LongPlaytest that names the part of the map that differs (outside the tree) |

## 4. What moved in the trace

Against main at aa37699 (0.8.7). TBD final.

**The trucks and the railway over the run** (on slice 1's model, b53980e): the default city's trucks carry 7.14 Gt of city legs over the run against main's 10.59, are short of the legs in 870 months against 1,213 (72 under 90% against 230), and peak at 4,146 trucks against 5,176; the Earth city's carry 3.78 Gt against 13.03, short in 505 months against 1,109, peaking at 1,817 trucks against 5,758. The railway keeps its stations and lays more spurs (default 15 at the end against 1; Earth 6 against 0).

**The cities**: the trace is main's to the month until the railway first has room past the boundary's tonnes, after the first Rail Depot (default m860, Earth m756); the checkpoints part between m769 and m1071 on both. The default city ends at 677,355 people against 701,791 (-3.5%) with a GDP of $6.9T a month against $6.6T; the Earth city at 588,872 against 769,819 (-23.5%), $5.7T against $6.5T, its dry bulk across the berths 2.13 Gt over the run against 10.40 (14 Bulk Berths and no Bulk Terminal against 4 and 1). A single run swings a long way with its path (FR2-notes §4); the ensemble (§5) is the measure, and its AUTO2 median is 4% larger than main's.

## 5. The ensemble

The 16-seed x 5-policy ensemble (the workbench zip's runner through FR3's copy of `runpc.sh`, split into nine runners at once, outside the tree), scored by its own `score.py` against FR3's final (`fr3b`, d42c009: main's model at aa37699), on a class snapshot of slice 1's model (b53980e; the later slices are a harness, a report line and the map). 80 runs, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch.

| criterion | main (FR3's fr3b) | FR6 (b53980e) |
|---|---|---|
| A1 inflation m240-end | 2.26%, pass | 2.23%, pass |
| A2 longest yoy > 10% spell | 15 [6-19], FAIL | 15 [4-19], FAIL |
| A3 deflation; final/peak population | 2 [0-8], pass | 1 [0-11], pass |
| S1 0% from m1800 minus twin | 2.27%, spell 26, FAIL | 2.17%, spell 21, FAIL |
| H1 / H2 ZERO_HOT | pass / FAIL (9/16 below) | the same, to the digit |
| K1 / K2 SHOCK, R1 / R2 / R3 HIGH | pass, pass; pass, pass, FAIL (5 above +1) | the same, to the digit |
| T1 AUTO2 seeds to 40,000 | 16/16 at m1,429 | 16/16 at m1,424 |
| T2 AUTO2 final population | 214,456 [171,367-299,656] | 222,716 [153,748-275,720] |
| T2 ZERO_SLACK | 143,922 [89,444-199,570] | 145,306 [87,140-172,777] |
| T2 ZERO_HOT / SHOCK / HIGH | 3,616 / 11,804 / 3,911 | the same |
| criteria | 22/26 | 22/26 |

**What moved, and why.** Nothing in the three 720-month policies: their young cities never have railway room past the boundary's tonnes, and FR6 moves nothing until they do. The two 2,400-month policies move within their spread and end a little larger at the median (AUTO2 +3.9%, ZERO_SLACK +1.0%), the AUTO2 spread wider both ways (the smallest 153,748 against 171,367, the largest 275,720 against 299,656). The four reds are main's.

## 6. The suite

TBD.

## 7. Not built, and what is left

- **Not this batch's**: the planner ordering track for the city's own bulk (★2); a Port Rail Yard building, and the yard on the quay drawn (★4); the city's own unit trains on the map (★6); every screen of spec §6 - Freight's division strip with the home and quay tonnes (FR4, which reads `getHomeRailed()`, `getQuayRailed()`, `getHomeBill()`, `getHomeShare()`).
- **For Jerus (FR6-C)**: the six calls; the Earth playtest's smaller city on this path (-23.5%, its dry bulk by sea a fifth of main's) against the ensemble's larger median; whether the railway should build for the city's own bulk.
