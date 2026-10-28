# FR8 - the trucks' share dealt per industry: notes ({next}, 2026-10-28)

The brief is `docs/queue/FR8.md` (Jerus, 2026-10-20, on FR2's ★5: "pool per
industry"). Built by the batch agent (Opus) with two Sonnet slices.

## 1. What shipped

- **The deal** (`Haulage.deal()`, at the haul, after the city's share): when the
  trucks do not carry the whole need, each sector is first served its standing
  demand - the room it was dealt at the last haul, no more than it asks now -
  the smallest first; the growth over that, the newest demand, is served last,
  pro rata, from the room left (`getNewestShare()`). A sector's share is what it
  was served over its need, a literal 1 when it was served whole; one that asks
  nothing is dealt a literal 1. A giant works landing shortens its own sector,
  not the town's.
- **The readers**: `Sector.getHaulageRatio()` reads `Haulage.getShare(this)`, and
  `billMonth()` bills each sector its legs at its own share, the sum scaled to
  what the trucks could carry. `getShare()` stays the city's (the fleet over the
  whole need), for the screens and the order. The order, the price, the depots and
  the fleet account do not change; the need's total is bit for bit main's.
- **Saved** in Freight's extras: `haul.share.<sector>`, `haul.standing.<sector>`
  (tonnes, scaled by ScaleCheck's copy), `haul.newestShare`, `haul.standingShare`.
  A save from before FR8 has none and deals its first month as the one share it
  ran at, so no format bump.
- **The Freight page's note** says the newest demand runs slower, not every works.
- **LongPlaytest** counts the months a sector ran short of what it was carried the
  month before (`getStandingShare() < 1`) and the worst any sector's own share was.
- **HaulageCheck 11** (the deal): a giant works in a full fleet (its sector alone
  short, every other at a literal 1 where the one share would have run it at the
  city's), the converse, the growth pro rata and a first works waiting a haul,
  the standing demand cut smallest first, and the deal saved and reloaded to the
  bit. Its fixtures were mended where the rule moved them (§3).

## 2. ★ calls (each taken the recommended way, BP1)

1. **★1 The standing demand served by size, the smallest first** (ties in
   `Sectors.KEYS` order), the brief's one choice. A fleet short of what the
   sectors were carried then cuts the largest standing demands, where the cut is
   the smallest part of a sector's own legs, and leaves the most sectors whole.
   `KEYS` is an append-only registry with no economic meaning: in its order the
   cut would always land on the industries added last (Wood Products, Forestry),
   whatever their size.
2. **★2 The standing demand is the room a sector was dealt at the last haul, at
   nameplate**, not the tonnes it actually moved. The need is the plant (FR2's
   ★4), and "pool per industry" is each industry keeping the trucks its plant was
   dealt; it is also the only reading under which the brief's own premise holds,
   "the others at the share they had". Its cost: room kept for a sector's whole
   plant sits idle while it runs below nameplate, and the newest demand gets only
   the trucks past it. The alternative (standing = last month's carried tonnes)
   pools that slack with the newcomer, but every sector below nameplate then reads
   a share under 1 whenever any works grows - the one share's symptom, milder.
3. **★3 A sector that asks nothing at the haul is dealt a literal 1**, and its
   first works, standing after the haul, waits for the next one, as every growth
   waits a haul. The slice's first draft dealt it the newest share: HaulageCheck's
   mine selling all its ore at home (no legs of its own) ran at the floor in months
   the fleet was short.
4. **★4 HomeRailCheck's real run counts a home share "with no room" only if no
   station or spur stood at the haul that struck it either.** On FR8's path
   Freight's distress rule (`Game.runRetirement()`) scrapped its one Rail Depot
   at m1160, after the haul, and the month-end read found a share struck over a
   depot that had gone. Main's run never scraps a station. The model keeps the
   share for the month by design (shares struck at the haul, FR2's ★9); whether a
   scrapped station should end the month's rail shares at once is left (§7).

## 3. The slices, and what the review changed

Slice 1 (Sonnet, xhigh): the deal, the readers, the save, LongPlaytest's count
and HaulageCheck 11. Slice 2 (Sonnet, xhigh): MapCheck's three fixtures (§6).
The review of slice 1 changed: ★3 (a sector asking nothing read the
newest share, 0 in a short month with no growth), which restored section 3's
bitwise "the mine ... was billed nothing" (the slice had loosened it to a
residue of 1e-12); 11b, 11c and 11e to the literal 1; HomeRailCheck (★4).
Mended where the rule moved them, premises kept: `predict()` bills each sector
at its own share with one cap; `setState()` deals every sector the share given;
section 6 reads the plant's and the shop's own shares, and strikes a fleet of
none before the shop's half fleet so the shop is caused short (under the deal a
half fleet after a .9 one cut only the largest sector).

## 4. What moved in the trace

Against main at b49f4f08 (0.8.62), the model at e1f535df. Both runs: "Nothing.
Every month passed the audit and every reload matched."

| default playtest | main | FR8 |
|---|---|---|
| end | m4000: 765,819 people, GDP $4.4T a month at a price index of 712 | m4005: 544,112, $21.2T at 5,175 (real GDP -34%) |
| m1071 / m2022 | 44,440 / 224,441 | 17,165 / 100,461 |
| the city's share short | 780 months, 44 under 90%, worst 18% (m48) | 699 months, 35 under 90%, worst 25% (m27) |
| **a sector other than the newest demand short** | **780** (the one share: every bulk sector in every short month) | **67** (the fleet short of what the sectors were carried the month before) |
| worst any sector's own share | the city's, 18% | 0% (Materials, m1516: a new works' sector in a full fleet runs at the floor) |
| trucks, depot places at most | 1,121, 1,100 | 1,404, 1,400 |

One run swings a long way with the city's path (FR2's drafts ended this run at
30,614 to 795,408); the ensemble is the measure.

## 5. The ensemble

`tools/ensemble/ensemble.sh fr8`, 2 runners, 80 of 80 runs in 13.9 min, on
e1f535df, scored against `docs/ensemble/0.8.57.md`; committed as
`docs/ensemble/FR8.md`. **23 of 25 criteria pass and no red is new**: A2 and S1
were red in the baseline too (A2 10 months [3 to 31] against 9 [0 to 24]; S1's
median 1.88% against 1.09%, longest spell 63 months against 52).
Growth is within the noise: AUTO2's final population 246,893 [199,706 to 317,402]
against the baseline's 258,838 [91,767 to 334,102] (95%; the band narrowed, the
lowest seed doubled); ZERO_SLACK 159,060 against 171,863; 40,000 people in 16 of
16 seeds at m1,243 [1,110 to 1,480] against m1,189 [890 to 1,438]. No seed stalls.

## 6. The suite

`./batch-check.sh --pick ReadPathCheck ScaleCheck`, on main 0.8.63 merged in
(CC1-M; its trace is main's, so the ensemble stands), LongPlaytest picked in
Sector.java's place. HomeRailCheck went red on FR8's path and is mended (★4).
**MapCheck went red on 7 lines**, all fixtures that caused their condition only
on main's path: its played city reaches month 121 with 13 land purchases
against 20. The orchestrator's ruling (a fixture must cause its condition on
any path; assertions and budgets unchanged, section 8 left to FW11) was carried
out by a second Sonnet slice; each fixture now does this:

- **2d, "fixture: Elevated Highways are laid in the district and reach a street"
  and "highways laid in a district's buildings move none of them"**: the
  highways start at the dry crossing of their lattice nearest the site that the
  city owns, and FR8's founding district owns none (the hub went to (80,-16) in
  the district beside, and sixty months laid nothing in the district). The
  fixture now hands the city the unowned blocks in a square round the dry
  crossing nearest the site (`highwayCrossing()`, `handGroundRound()`, as 2c
  hands blocks), runs a month on that ground, and lays the highways as before:
  56 plots among 397 boxes, 38 within a plot, none moved. A new fixture line
  says the square is the city's.
- **11, "every month redraws the pictures of the districts whose plots moved"**:
  the played city entered the window broke (cash -900 at m121; main's held
  25,764) and its advisor built nothing for 120 months. The run now hands it
  `PICTURE_CASH_MONTHS` (3) months of its GDP at each advisor look when it holds
  less (`handCash()`): 102 district-months with counts moved, 101 pictures
  redrawn. This is not a stall of FR8's: played on without the cash, main's city
  sits on an empty treasury at about 2,200 people from m145 to m277 too, and
  FR8's grows out of it by m361 (4,050 people against main's 3,819).
- **14e, the four picked-block lines** ("the city's buying the offer over a
  picked block drops the block it owns whole...", "fixture: one of its blocks may
  be picked", "a draft bought through Game.buyLandParcel()...", "...and the offer
  whose ground it bit left the shelf"): the city stood 16 plots under its next
  block level, so every offer's purchase changed the blocks' size and the picks
  dropped for that reason. The buyer is now an offer whose purchase, and a block
  after it, keep the level (`ownBlocksKeepingLevel()`); with none, the city first
  buys the largest standing offer through the office (`buyTheLargestOffer()`)
  and the level is read again. A new fixture line asserts the level is kept.

The gate's full suite (2026-10-28 11:55) found **4 more NEW red lines** in
harnesses the `--pick` did not run, all fixture lines (each asserts that its
fixture caused the condition its section tests); the assertions are untouched:

- **AutoBuildCheck 7, "fixture: at the reserve its pass holds an order for the
  money, none ordered ahead of it"**: the played young city at month 60 is 1,659
  people with every service ahead of its need and only the schools and the police
  held, for their payback; scanned on to month 240 at every look it never held an
  order for the money (debt 70% to 210% of a year's GDP, no unmet need). The pass
  had nothing to refuse. The city's general care is now retired
  (`retireServing()`, as `deficitTown()` does to a town), the pass has the Walk-in
  Clinic to place ($3.0M for one) and refuses it for $0.28M as the section
  always meant; its twin orders it a month on and has 2,329 people 24 months
  later against 1,659. A new fixture line asserts the city is short of the care
  (0 standing, a supply of 1,200 against a demand of 1,659). Nothing is a finding:
  a stalled young city with nothing to build is the path's, not a rule the model
  stopped applying.
- **OrderSearchCheck 2, the copy** ("the copy judged orders of more than
  COUNTDOWN_SLICES on their interest, and decided some below the counted slices"):
  at month 400 FR8's copy judged 145 orders and refused none (FW9's: 40 of 99);
  the orders earn more than their interest on this path. The copy's months are now
  held at `COPY_RATE` = `DEAR_RATE`, 15%, as section 1's dear stretch is (the
  interest test made to bind, as the twin's is): 167 judged, 115 refused, 40
  decided below the counted slices. The month is not moved.
- **OrderSearchCheck 2, the twin** ("the twin trimmed orders of more than
  COUNTDOWN_SLICES on their interest" and "...some by more than the counted
  slices"): of the twin's 128 orders only three were over the counted slices and
  none was trimmed; the one with a till that was part of its cost, a Construction
  Materials Plant of 98 on 49 slices of till, was built whole at 40% (it is
  trimmed only at the dial's top, by 24, and only on this path: at month 300 no
  order is, at 500 one is, by 71). The trim needs an order whose till is part of
  its cost, which a path may or may not give, so `tillsAsTheLine()` causes it: it
  puts the twin's largest order to the twin's own search (`Game.sliceFor()`,
  which books nothing) with a till of a quarter and of a half of its cost and an
  order that earns nothing, told to the watch as the game tells it and asked of
  the countdown too: built to 24 and to 49 of 98, and the twin's verdict lines
  (the search is the countdown's) hold over them.

The full suite after these three mends (`./batch-check.sh`, 2026-10-28): 150
harnesses, exit 0; MapCheck's one timing assertion is soft there and not counted.

## 7. Not built, and what is left

- **Not this batch's**: the screens' per-sector shares (FR4 reads the city's;
  `getShare(Sector)`, `getStanding()` and `sectorShares()` are there for it).
  The manual's sentence "One share serves the whole city, so a giant works landing
  slows every bulk sector" is stale until its next edition.
- **Left (FR8-C)**: a station scrapped after the haul leaves the month's home,
  quay and boundary rail shares in force (★4).
