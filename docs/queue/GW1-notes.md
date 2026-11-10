# GW1 - the gate counts the draw ({next}, 2026-11-10)

The brief is GW1's row in `QUEUE.md` and [the staffing-gate study](../studies/spec-staffing-gate.md) §5-§6, its four
calls settled on their recommendations by the orchestrator (BP1, 2026-11-09): C1a count the draw, C2a a 12-month
window, C3a WG2's aim as Jerus took it, C4a GW1 before WG2. Lane B, after WS1 (0.9.86). A model batch: Opus, with
Sonnet slices for the code.

## 1. The rule

- **Beside each band's spare workers, the gate counts the workers migration would bring for the order's own posts
  within `Sector.GATE_DRAW_MONTHS` (12) months** (`Migration.drawFor()`). The posts raise migration's target by the
  jobs' draw (`JOB_WEIGHT` x the posts x last month's residents a job x the senior, rent and crime pulls, as
  `monthlyNet()` builds the target); a gap closes at `ARRIVAL_RATE` x last month's crowding a month, so a year
  closes 1 - (1 - that)^12 of it; the city's workers per resident turn people into workers; they split by band as
  `composeArrivals()` splits a month's arrivals, with the order's posts added to each band's open posts.
- **Each band takes its spare first, then the drawn workers carried down from its own band and the bands above.** A
  drawn worker may take a post at or below their band, never above. At the going wage the draw lands in the
  diploma band (graduate bands draw only on a premium, `reach()`), so a building whose graduate posts are more
  than a fifth is still refused for them.
- **The share stays at 80% and the band test is untouched** (a band nobody here can fill and no migrant comes for).
- **No new saved field.** The draw reads migration's last-month scalars, saved since 0.7.27 (`LAST_SCALARS`). The
  year's arrival mix of a 1900 city is passed in (`ArrivalMix.at(game.getMonth())`) rather than read from
  `Migration.yearMix`, which Game pushes only at the month's migration step (after the planners) and never saves:
  read from the field, a loaded 1900 city's first planners would have read none and parted from its twin
  (StaffingDrawCheck 6 asserts the twin).
- **A share the draw filled is snapped to `Sector.DRAWN_SHARE_GRID` (1e-9).** Where every band's chance is whole
  (`Migration.opportunity()` at 1) the draw grows with the posts, so two templates whose posts only the draw fills
  share alike but for the last bit, and the bit is the price level's. Construction keeps the held template with the
  larger share: DenominationCheck's twins held a Construction Depot in one and a Builders' Yard in the other (game
  month 123), the unskilled wage parted by 3e-4 the month after through the posts WantedPosts prices (LD1), and the
  reformed city was 4e-6 off its twin a year on. Snapped, the tie is a tie at every price level and the first template
  keeps it. A share the spare alone filled is the ratio of counts it was, unsnapped (StaffingDrawCheck 2 asserts it to
  the bit). The same family as BA1's `SHARE_ROUNDING` (a Machine Works against a Fabrication Shop).
- **No brake yet.** Main has no WG2 brake, so the draw reads the pulls as they are: the study's `draw12` row. WG2's
  row already says its brake and its pull rule apply to `drawFor()` too.
- **Who follows.** Every reader of `Staffing.share` (every planner, `staffableCount`, the build card, the advisor,
  WantedPosts' choice of hold) reads the gate's share. `Staffing.shareNow` keeps the spare-only share and
  `Staffing.drawn()` the drawn workers per band.

## 2. The words

- The hold: "the city could staff 31% of a Blast Furnace now and 64% with a year's arrivals; it wants 80%", or
  "..., now or with a year's arrivals; ..." when the two round equal. One sentence (`Staffing.why()`), so the build
  card's gate, the sector screen's "so" line and the planners' hold reasons all say it.
- The build card's posts line: "the city could staff 31% now, 64% with a year's arrivals" (or "all of it with a
  year's arrivals"). `BuildCard.Figures` gains `staffableWithArrivals`; `staffable` is the spare alone. An office's
  bar of the posts it could not staff reads the gate's share, so the bar and the gate agree.

## 3. The checks

`StaffingDrawCheck` (new), each fixture causing its case:

1. A village with room (crowding 1): a Sawmill the spare fills 43% of passes at 89% on 21.1 drawn workers, the
   total equal to the formula from migration's getters.
2. No room (the city's people far past its doors, crowding 0): nothing is drawn, the share is the spare's to the
   bit, and the order the spare refuses is refused.
3. The pulls (main's stand-in for WG2's brake, which will add its section): rent at twice the target halves the
   draw, crime cuts it by its pull, and at ten times the rent the order that passed on the draw is refused.
4. The Shared Services Centre in a town with no graduates: 17% now, 30% with the draw (its diploma posts' share
   exactly), refused by the share; with university arrivals held closed, the band test refuses as before.
5. The words name both counts, and one where the draw is nothing.
6. A loaded city's gate is its twin's, to the bit, for a default city and one founded the 1900 way (whose twin's
   Migration holds no year's mix).
7. A played village, 120 months: of 12,240 asks the draw opened 6,745, the spare alone passed 2,599 and 2,896
   were refused; in a month of no room it opens none.

## 4. The harnesses that named the gate

Run on the gate alone (slice A), nine of the 25 harnesses that read the gate went red. Each was read for a premise of
"only the spare". None was a finding against the rule; each fixture was made to cause its condition again, and no
assertion moved:

- **WantedPostsCheck** caused its holds in a village with room, where the draw now opens them: they are caused in a
  city with no room (`noRoom()`, crowding 0), where the gate is the spare's to the bit. Its reprice section asserts on
  `shareNow`, the spare it is about, and that the draw moves the college band. Its repeat hold is a real one again.
- **InvestCheck** (BA1's spare rule) asserts on `shareNow`, the spare the rule reads.
- **BuildCardCheck 6** causes the card's staffing gate on a Shared Services Centre (210 of 300 posts graduate, which the
  draw does not bring) instead of a Rail Terminal the draw now staffs.
- **RefineryCheck** holds every sector but Refining, as its fixture held the ones it named before the draw let the rest
  build. **PrizeCheck** hands the lab village its college heads (`LAB_COLLEGE_HEADS_MORE`, 200).
  **PolicyPreviewCheck** holds Heavy Industry (a Blast Furnace ordered at month 60 sank the bank's profit) and causes
  the not-joined market by handing the city unskilled workers.
- **BankCheck 19** holds Wood Products, Cement & Concrete and Materials beside Manufacturing: their works came earlier,
  and their defaults put the town's bank in resolution (month 44), where `Bank.branchesToClose()` is 0 by its rule.
- **LabourCheck** runs the doctor-short city on until its labourers are paid the going rate (its unskilled posts
  outran them for ten years). **HousingCheck** places the held dial's glut (homes for twice the seekers) where its
  city's people used to leave under the 15% dial: with the draw they stay and grow.

Run on the whole of GW1 (the gate and its snap), 21 more harnesses went red beside main's recorded reds. Read the
same way, each fixture was made to cause its condition on the draw's path, with the numbers in a `GW1:` comment:

- **SaveFileCheck** ordered the warned city's depots from the first city's template. A stack keeps the instance it
  was ordered with and `BuildingManager.stackOfKey()` finds it by identity, so the month the draw put the depots in
  the planners' first group, `plan()` threw. The harness orders from the city's own template, as the game always
  does. Its bank (in resolution from month 151) is waited for until it has paid a year running before the squeeze,
  and the month's hot money is placed (40, the order the loop's months brought before), since the history's world
  rate (23% at months 227-242 against the city's 3.6%) leaves no spread for any to arrive for.
- **CurveCheck** holds Manufacturing and Wood Products (a plant on a bank loan before month 26 had the bank at the
  central bank's window and the curve flat) and runs on to a month where Strict and Standard advise apart (the
  founding was deflating at month 54). **PassThroughCheck** holds every sector in both twins, as its `month()` says
  nobody builds. **MonetaryCheck** plays the founding on until the groceries shelf has moved under a point a month
  for a year (34 months, to month 59), then runs the dial rows over the same 36 months: the shelf based at month 38
  fell 20-28% as the food came ahead of the people, and the window measured that, not the dial. Six settled months
  (month 53) gave the ladder but failed the month-late rows (0.109 against the 0.100 allowance, the town still
  building and its mortgages fixed at the hold month); twelve were taken with both seen, and both are in the
  `SETTLED_MONTHS` comment. **CurrencyRegimeCheck** waits for a price rate read before it pegs, then holds the peg
  town's smoothed inflation on the target (`placeInBand()`): on this path the young town's inflation passes through
  the band in five short spells of 296 months, so no wait gives its credibility fixture a run of months.
- **BondCheck** 7c runs on a city of its own with Construction held (it borrowed in the young town). **FloatLinkCheck**
  hands the driven index over at the linkers' base (prices fell 3.9% after they were sold). **GdpCheck** hands the
  town back the cost of the sheds it places. **HoldersCheck** waits for the savers' rate to stand under the bond's
  fair yield, sizes the two-year bond to its bidders, and reopens section 12 six points up instead of four. Its
  reopening fixture holds every sector: the businesses' borrowing had the bank insolvent at the first sale (equity
  -5,787, its room to bid nothing), the firms and the world paid in full and the bank, the residual buyer, took
  3.2e-14 of the face. Held, it takes 23.6% of the first sale, as before GW1. Its four fixtures that "the bank took
  part" now ask for over `MATERIAL_PART` (1%) of the face, so rounding can no longer pass them. With the bank's
  share real, "a fresh line's settle re-strikes nothing" passes too. `restrikeDiscount` still books float noise
  (3.55e-15) where a take is rounding, because it skips only a gain of exactly 0; that is harmless and not queued.
- **AutoBuildCheck** turns the pass off while its clinics open. **FittingCheck** prices bread to put a Bakery between
  the two drives, and the coal at half the break-even. **OilCheck** places two Grain Farms. **YearBookCheck** holds
  every sector (the posts had outrun the workers and nobody was out of work). **HealthCheck** adds two clinics at the
  drain (the free twin peaked at 25,609 people against 25,000 places). **DeathRecordCheck** reads the children with
  the out of work at the wave's peak (the city had taken them all back into work within the two years).
- **MapCheck** 2c and 2h cut their blocks from the founded ground, and its land-office claims are read on a map the
  city fits at the opening zoom. **HomeRailCheck** plays the railway's share on to fractional (60 months, not 12).

**Red, and not GW1's to mend.** Each is a fault of main's that GW1's path is the first to reach, or a class main
already records, and each claim fails while its fixture holds:

- **DeathRecordCheck**, "the orphan section does not fill behind it": the closed town's orphans peak at 27.7 against
  its open twin's 21.5, where the claim allows one more. The family builder places children at half of what is
  possible a shape (`FamilyModel` `take = possible * .5`, a bare literal) and leaves about one child in seven, as its
  class note says. The wave had kept the closed town's children out of the builder (1.3 orphans); when the city took
  all 319 back into work in one month, their 149 children came back to it together. Then the family memory
  (`REFORMING_EACH_MONTH`, .01) kept 99% of last month's households, which already held every adult, so the
  leftover children waited months for an adult free to take them. On main the wave never ended inside the two
  years, so the claim never saw its end. Queued (`OX1`).
- **HealthCheck**, "a dear fee buries more of its people" and "its old first" (deaths 15.60 against 15.59 a year
  per 1,000; the old 101.07 against 101.02): the beds' fixture, main's recorded red, now holds, and these claims run
  for the first time on a city they can be read on. The fixture's drain zeroes savings and shuts credit, so a cell
  can spend its income after rent and a basket and nothing more (`Household.affordCare`). Every cell either clears
  fifteen times the bill or cannot clear one: coverage 85.69% against 85.57%. The claims then compare noise, the
  twins' size and age (the dear twin is 4 points less hungry, and no code path explains it). Queued
  (`HX1`): a drain that leaves cells between one bill and fifteen, and a fixture line that the dear fee
  turns away a material share.
- **PlanCheck**, T junctions only in an estate cell: a shore street (`DistrictPlan.shore()`, `ROLE_SHORE`) crosses
  an estate cell's east-west line at frame plot (71,48), estate cell 10 of district (0,0), month 121, a + where only
  Ts may be. MapCheck 1's city shows the same. Queued (`PQ1`).
- **SlopeCheck**, "a building on the map moved": in district (-1,0), months 610-611, a purchase of 384 plots took
  the wall way from 1 try and 13 clear to 0 and 0, and 10 boxes moved. This is DM2's class, whose row names
  SlopeCheck 9's jump after a land purchase.
- **MapCheck**, "a yard that goes moves none of the buildings": main's recorded line, with new numbers (2,560
  boxes, 144 moved). DM2's class.
- **PriceLevelCheck**: main's recorded class (BS1: the corporate issue search stops within 1e-9). Its lines are new:
  the twin parts from month 105, with 239 lines failing against main's 356.
- **KeptCheck**: main's recorded lines; only the counts in them moved.

## 5. Measured

The 80-run ensemble on 99429bf8 (the snapped share), `docs/ensemble/GW1.md`, against main's newest baseline
(`docs/ensemble/0.9.83.md`): 24 of 25 criteria pass, as 25 of 26 did on main. A2 fails as it fails on main, H2 is
vacuous (below), H1 is retired (Jerus, CD1).

| measure (median [min to max]) | 0.9.83 | GW1 |
|---|---|---|
| AUTO2 inflation m240-end, a year (A1) | 2.23% | 2.06% |
| AUTO2 young city, inflation m240-600 | 3.39% | 2.52% |
| AUTO2 founding m120-240, longest spell of yoy > 10% | 9 months | 8 months |
| AUTO2 longest spell of yoy > 10% from m240 (A2, < 12 in every seed) | 11 [6 to 16] | 15 [0 to 30] |
| AUTO2 founding m120-240, months of yoy < -10% | 0 | 1 [0 to 19] |
| AUTO2 final population | 581,556 | 737,604 |
| ZERO_SLACK final population | 389,740 | 539,172 |
| SHOCK final population | 29,630 | 56,563 |
| HIGH final population | 13,722 | 24,096 |
| ZERO_SLACK unemployment in the window: run; twin | 7.12%; 7.84% | 4.04%; 4.40% |

The study's case holds: cities that stalled for want of the hands a building would bring now build it, grow faster
(a quarter larger at the end under AUTO2, three quarters under HIGH) and carry less unemployment, and the young city's
inflation is lower. Two things are worse or gone. **A2**, main's own red, is longer: the median city's worst spell of
inflation over 10% after month 240 is 15 months against 11, the worst seed's 30 against 16 (the first GW1 run, on
the unsnapped share, measured 11 [2 to 17], so part of it is the seeds' spread). **H2 is vacuous**: the ZERO_HOT
city at 0% no longer runs hot in its second half (its excess over the twin -0.14% against 4.69% on main), so there
is no burst for the autopilot's return to stop; H1, the premise that it ran hot, was retired on CD1. The first run
(c3ca14fb, before the snap) scored 25 of 26 with A2 its one red; the snap moved paths wherever a held Construction
order had tied, so the ensemble was run again.

HousingCheck shows the same thing as ZERO_HOT from the other side: a city whose people left under a 15% dial now
stays and grows under it (384 people at month 121, 569 at 169).

The founding now has spells of deflation (the median seed has a month of a year's prices down more than 10%, the
worst 19 months), as MonetaryCheck's and CurveCheck's foundings show: the businesses build ahead of the people, the supply
comes first and the shelves fall to it.

## 6. Not done

- **WG2's brake.** StaffingDrawCheck 3 tests the pulls main has; WG2 adds its brake's section, and its row says the
  brake and its pull rule apply to `drawFor()` as they apply to the month's arrivals.
- **A2 and H2.** GW1 lengthens A2's spells and leaves H2 nothing to test (section 5). Neither is mended here: A2 is
  main's red, and whether ZERO_HOT should still run hot is a question for the criteria, not the gate.
- **Not measured:** the draw with WantedPosts' pricing of the refused posts (LD1) turned off, through which the
  twins' tie travelled before the snap.
- **The reds above** go to their rows: DM2 (SlopeCheck, MapCheck), BS1 (PriceLevelCheck), and the rows this batch
  queues (its ship file).
