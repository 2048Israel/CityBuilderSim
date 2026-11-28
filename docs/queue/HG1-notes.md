# HG1 - an empty shop starves a town, and the screens say nothing: notes

Builder: Claude (Opus), headless, bus HG1, branch batch/HG1 cut from origin/main bbdb5fc08.
Brief: docs/queue/HG1.md. His save copy: C:\Users\Jerus\AppData\Local\Temp\cbs-kept-hunger\ (left as it is);
the work ran on a copy in C:\Users\Jerus\AppData\Local\Temp\hg1-scratch\ (scratch drivers HgProbe, HgShops, HgStaff there).

## 1. The cause, on the copy (month 79, 0.9.219, format 67)

- **There is no shop.** The brief's "one shop stands" is the Commercial Bank: COMMERCIAL, no sector, coverage 0.
  Retail owns no building, none on site, in 79 months. getStoreCoverage() is 0, so expectedBaskets() is 0, so
  supplierCreditLimit() is 0, and recentUse() stocks for min(coverage, population) = 0: the zero cash, zero credit
  and empty pantry are all downstream of no building. Retail's operating rate is 0.873 (health alone), its fill 1.
- **Retail.plan() is held by the staffing gate**, every month: "the city could staff 13% of a Small Grocery Store now
  and 64% with a year's arrivals; it wants 80%". The Convenience Store (3 no-diploma, 3 diploma posts live) reaches
  0.622 with the draw, the Small Grocery (20/11/2) 0.636, the Filling Station 0.789. None reaches MIN_STAFFABLE_TO_ORDER.
- **Why: the diploma band is over-posted from the founding.** The city was founded the 1900 way (foundedUnfitted):
  15 people and 47 posts at month 2, and posts above the workforce every month since (591 for 516 at month 77).
  At month 79 the bands are NONE 385 posts / 389 workers, DIPLOMA 144 / 129, COLLEGE 76 / 11. The 1900 arrival mix
  (ArrivalMix) brings mostly no-diploma workers, the city's other employers hold the diploma band, so its spare
  (supply - posts) is -14.7 and every shop format has diploma posts. Unemployment 0.9%.
- **The loop.** The gate counts only spare workers (and a year's draw) -> the band is over-posted, so no shop
  passes -> no shop, no coverage, no expected sale, no supplier credit, no stock -> nothing sold, 97.5% hungry ->
  and nothing in the hunger frees a worker or moves the gate: the farms keep theirs, arrivals keep coming at 20 a
  month with the 1900 mix, and the other employers' posts keep the diploma band short. It holds at zero for ever.
- **But a shop built here would be staffed.** The fill is one rate per tier across the city
  (Sector.updateJobFillRate, PopulationManager.fillByBand): every post of a tier fills at the tier's rate, the
  shop's with them. A Convenience Store's posts joined to the bands fill at about 0.94; the gate refuses it only
  because it treats the posts already standing as owning the short band's workers.
- **The suspects.** 40806e6f8 (Retail.getCurrentGrocerySales, 11-24) reads the pending ledger right: it is 0
  because nothing is sold. CBK3's credit rules (0.9.219) are not involved: the supplier credit is 0 because the
  expected sale is 0, and no shop ever asked the bank. Neither is the cause.
- **Why harness cities base their index**: they are founded the default way, whose arrivals hold the diploma
  ("universal high school", Migration.composeArrivals()), so the diploma band has spare workers and a shop passes.
- **Without the batch his city would have got a shop on its own at about month 92**, once its unemployment rose
  enough to free the diploma band (scratch HgProbe on the pre-mend classes). A young 1900 city escapes the old gate
  at about month 22 the same way (HungerCheck's Trap, the hunt on the mutated classes below).

## 2. What was built

- **A shop the town goes short of is staffed at the fill** (`Sector`, banner "A BUILDING THE TOWN GOES WITHOUT IS
  STAFFED AT THE FILL"; `Sector.unitsAtTheFill`, `Retail.basketsShort`, `Retail.townGoesShort`). For such a template the gate reads
  the larger of its ordinary share and `Sector.shareAtTheFill`: the template's posts joined to the city's posts,
  each band filled by `PopulationManager.fillByBand`, the share of its posts that fill. Only Retail overrides the
  hook, only for a template with coverage (a shop, not its Filling Station), and only while its shops, standing and
  on site, at their nameplate coverage serve fewer baskets than the households need (`groceriesNeeded()`): the town
  goes without shops, not without staff (the second dry run, section 4). And only for an order no larger than the
  gap: the fewest units whose nameplate closes it (`unitsAtTheFill`); an order past it is read the ordinary way,
  whole (REV-HG1's second look). Everything else keeps the 80%.
- **The index bases without groceries** (`PriceIndex.takeMonth`, comment "THE OTHER HALF NEED NOT BE GROCERIES").
  A shopping month is rent priced and paid, and either groceries bought at a shelf price or any outside good bought
  at a price (`goodsBought`: meals, luxury, new cars, petrol, lamp oil, home coal). It is never the city's own fees
  (SERVICES), which would close the BOTH HALVES loop again. Groceries join a basket based without them after a
  whole year of sales (`groceriesJoin`): the basket relinks at the level it stood at, so the level never jumps.
- **FOOD on NEEDS YOU** (`CityNeeds.food`, `Kind.FOOD`, `Go.SHOPS`, `Go.FOOD_PROMISE`, `HUNGER_YELLOW` .04,
  `HUNGER_RED` .20). The row reads the households' hunger. Its door is Build > Shops when the shelves are the
  larger half (short of stock at least priced out), else Policy > Promises > Food. `SummaryScreen.goTo` and
  `BuildScreen.goOf` map the doors.
- **HungerCheck** (in AllChecks): six sections, one per check in the brief except his city (below), and the gap.
- **No saved field, no format bump**: the index's new rule reads only what it already saves.
- **No held-figure writer**: the mend reads `fillByBand` and the posts, and Retail's order goes through
  `addStack`, which already raises the held posts, the vacancy and the Warrant.

## 3. The calls

- ★ **Staff a shop at the fill, not lower the 80%.** "THE SHARE STAYS AT 80%" (GW0/GW1: lowering it built what
  cities could not keep). The fill is the city's. A new employer hires from other employers too: job-to-job moves
  were nearly two-fifths of US new jobs 1994-2003 (Fallick & Fleischman, FEDS 2004-34,
  https://www.federalreserve.gov/pubs/feds/2004/200434/200434abs.html). So a shop's posts fill at its tier's rate
  like everyone's. Recommendation taken: only for the building the town goes without, and only while it does.
- ★ **Only Retail, only shops.** The hook takes the template, so Retail's Filling Station keeps its ordinary gate.
  Other sectors whose absence starves the town (none found) could override it later.
- ★ **"Short" is read at the nameplate, not the operating rate** (found by the second dry run). Read at the
  operating rate, a town with no spare workers stayed "short" however many shops stood, because the shops were
  short of staff, and every further shop staffed at the fill spread the same workers thinner: GroundSkipCheck's
  played town fell from 3,127 people at m245 to 1,623, flat, with shop coverage 2,400 for 1,449 baskets needed and
  unemployment 0. That is the GW0/GW1 lesson again. A shop short of staff is not a shop the town goes without; the
  ordinary 80% gate is right for it. HungerCheck 1 now causes it (Retail's staff thinned to 5% of each band: the
  nameplate covers, the operating rate does not; not short, not staged) and it goes red with the old reading
  (`target/HungerCheck-oprate.log`, 3 FAIL).
- ★ **Only the units that close the gap** (REV-HG1's second look, its P1). The exception first covered the whole
  order while the town was short: the planner sizes an order at the operating rate (a need of 1,000, coverage
  960 at 50% asks for three 480-coverage stores where one closes the gap), and all three were read at the fill and
  could poach workers. Now `Sector.staffing()` lifts an order only if its count is no more than
  `unitsAtTheFill(t)`, the ceiling of `basketsShort()` over the template's coverage. An order past it is read the
  ordinary way, whole, not split into a lifted part and an ordinary part: one share per order, as the gate has
  always read. `staffableCount()` still binary-searches, and the test stays monotone (the ordinary pass, or a
  count within the gap that passes at the fill; both hold for every smaller count), so a bulk order is cut back
  to the gap's units when the ordinary reading refuses it. HungerCheck 5 causes it.
- ★ **The basing reads rent and any bought outside good, never fees.** The brief's BOTH HALVES: the base needs a
  real outside price. Groceries keep their place: a city that buys them bases on them as before (HungerCheck 4e).
- ★ **Groceries join after a whole year of sales**: WEIGHT_MONTHS of them, at their share of the trailing year. A
  month without a sale holds the join off. The relink is chained, so the level carries over.
- ★ **FOOD's lines.** The 4% and the 20% are gameplay choices, when to list the row and when to colour it red;
  the sources are analogies only, not a measure of the same thing (REV-HG1). The game's hunger is the share of
  people who went without a basket this month; USDA's and IPC's are household shares over a survey's year or an
  area's assessment. Listed from 4%: USDA ERS, very low food security, 4.1% of US households in 2019 (ERR-275,
  https://ers.usda.gov/publications/99281). Red from 20%: the famine line, IPC Phase 5, a fifth of households with
  an extreme lack of food (https://www.unicefusa.org/stories/what-is-famine). The reading says which half.
- ★ **The priced-out half stays.** On his copy, after the shelves fill, 8-27% stay hungry because they cannot pay.
  They are the people with no income in a 1900 city with no promise made: the model's poverty, not a shelf. FOOD
  reads it yellow (red at m103, 27%) with the food promise's door, which is the lever. Not mended here: it is his
  to decide by playing.
- **No change to 40806e6f8 or CBK3**: neither is involved (section 1).

## 4. The checks and their results

- **His city** (scratch HgFinal on the final classes, d9a09ead9, his copy in hg1-scratch, 48 months,
  `target/hgfinal2.txt`):
  - Shops were ordered at once: 3 Convenience Stores stood at m81, coverage 1,440 for about 1,000 baskets, the
    gap's units. On the first, unlimited gate (`target/hgfinal.txt`) 5 stood by m84, 2,400. The shelves were full
    by m86: short of stock 0.896 -> 0.142 at m83, 0.030 at m84, 0.000 from m86, with two months near 3% as the
    town outgrew them, before the fourth store (1,920 by m110).
  - Hunger went 0.975 -> 0.230 at m83 and 0.086 at m85, then 8-27% (priced out, above).
  - The index counts down from m80 (37 -> 0), bases at m103, and gives its first rate at m116: +0.7%, then down
    to -4.8% at m119 and back to +1.4% by m127. The rule's rate moves off 2.00% (to 0, then 1.1% at m127).
  - FOOD reads red with Build > Shops to m83, then yellow with the food promise (red at m103); NEEDS YOU lists it
    every month.
  - The money audit's residual stays under 1.6e-11 every month.
- **HungerCheck** green (target/checks/HungerCheck.log):
  1. The trap caused: a 1900 city at month 16, diploma spare -5.39, ordinary share 0.738, Retail's 0.809. The
     station is not lifted, and the gate is ordinary once the shops cover.
  2. Played: ordered +1, standing +3, stocked +3, sold +4, short of stock under 4% at +5 (bound 12).
  3. The screens: red with Build > Shops; red with the food promise at a 20x shelf price; a fed default city at
     level 0 and not listed; every one of 75 months watched follows the thresholds.
  4. The index, (a)-(e).
  5. The gap, on d9a09ead9: the trapped town at month 16 needs 74.4 baskets and has no shop, so the gap takes
     one Convenience Store or 4 stalls of `STALL_COVERAGE` 20 (a Convenience Store's posts on a smaller floor).
     Shares, ordinary / at the fill / Retail's: 4 stalls, within the gap, 0.625 / 0.719 / 0.719; 5 stalls, past
     it, 0.592 / 0.700 / 0.592; 8 stalls 0.547 / 0.658 / 0.547. A count of three Convenience Stores is cut to 1;
     the planner orders Convenience Store x1.
  6. The money audit, every month played.
- **Red without the limit** (a mutation, reverted: the lift for any count while the town is short;
  `target/HungerCheck-nolimit.log`): 2 FAIL, section 5's two orders past the gap, read 0.700 and 0.658 against
  the ordinary 0.592 and 0.547. Everything else stays green.
- **Red without the mends** (a mutation, reverted, run before section 5 was added: Retail's hook off, the basing
  back to groceries only, no join, no FOOD row; `target/HungerCheck-mutated.log`): 29 FAIL, in sections 1 (4),
  2 (1), 3 (5) and 4 (19). The money audit (then section 5, now 6) stays green, as it should. Without the mend the hunt finds month 22, where the band has freed itself (spare
  +1.54, ordinary 0.840), so the trap's premises fail.
- **Named harnesses green, one JVM at a time** (`target/named1.txt`, exit 0): StaffingDrawCheck,
  StaffingTieCheck, GroceryCheck, SupplierCreditCheck, Forms1900Check, ExpectationsCheck, BuildAdviceCheck and
  HungerCheck. `--fast`: Stale 0 firm findings.
- **On the limit's mend** (d9a09ead9, one JVM at a time): HungerCheck green; FoodProcessingCheck green;
  GroundSkipCheck known red only (main's LP-G2 line); Village1900Check (0.451) and CreditCrunchCheck (its m270
  premise) only the two lines named with HT1-H, as before the mend. `--fast`: Stale 0 firm findings.
- **The pick is ALL** (AllChecks changed): the whole suite runs in the dry run.
- **The ensemble**, owed because the default trace can move (Retail's gate in tight-labour months; basing on
  meals before groceries). It ran after REV-HG1's SHIP, on the gate box: label HG1, classes of bcc373d55, 80 of 80
  runs, queued behind ASH4d's (the orchestrator's call), scored against `docs/ensemble/0.9.219.md`, committed as
  `docs/ensemble/HG1.md`. **Batch verdict PASS: hard 10 of 10** (M1 0 months, F1 0 of 16 in every policy),
  24 of 26 criteria. The changed verdicts are all information:
  - AUTO2 A1 (inflation m240-end): FAIL -> PASS, median 2.93% (2.90%), 12 of 16 in band.
  - ZERO_HOT H2 (inflation after the autopilot's return, minus the twin): VACUOUS -> PASS, median -0.37%.
  - HIGH R2 (unemployment minus the twin): PASS -> FAIL, median +0.30 points against +0.50 (it was +0.52).
  - T2 final population, ZERO_HOT, SHOCK and HIGH: FAIL -> PASS. Medians: 17,232 (13,698); 208,032 (197,311);
    79,502 (86,550).
  HIGH R2 is the one info row to go red: high rates raise unemployment over the twin a little less (0.30 points
  against 0.52), within the baseline's spread (-0.94 to 7.00). The run is kept in `target/ensemble/HG1/` here and
  on the box.
  That run was on the first, wider gate (short at the operating rate). The gate's reading changed after the second
  dry run, so the ensemble ran again as HG1b on e5d178da4 (the nameplate reading); it was stopped unfinished when
  REV-HG1's second look narrowed the gate once more, to the gap's units. It runs again as **HG1c** on the box, on
  d9a09ead9 (the limit), and `docs/ensemble/HG1.md` becomes its baseline.
- **The dry runs.**
  1. Box run cbs-gate-HG1-xSgI3JNV (18:08 UTC) stopped at the records, before the suite: "{next} remains in added
     source". Four of HG1's own comment labels (Sector's banner, Retail, PriceIndex, CityNeeds) carried it; they
     read "(HG1, 2026-11-28)" now, comments only.
  2. Box run cbs-gate-HG1-UdX8lstP (18:41 UTC), the suite on the merge: four NEW reds, each traced here by
     removing one mend at a time and by origin/main's own classes in a scratch worktree, one JVM at a time
     (`target/four-*.txt`, `target/two-*.txt`, `target/cc-mainindex.txt`):
     - **FoodProcessingCheck** ("a Meat Works on a farm's floor is an ordinary business", $-61.42 a month) and
       **GroundSkipCheck**'s two premises (its town growing, its ground holding HORIZON months' growth): the gate,
       read at the operating rate (the call above). **Mended** by the nameplate reading: both green, the town at
       m245 3,127 people and growing as on main; GroundSkipCheck's one red left is main's own LP-G2 line, which
       known-reds already passes.
     - **Village1900Check**, "today's twin's homes ask a household alone under 1.5 times TARGET_RENT_BURDEN":
       0.451 against the line 0.45 (main 0.443; the 1900 village 0.82-0.91). Green with the gate's mend removed,
       red with it, read either way: today's twin (founded the default way, played 60 months) goes without a shop
       in its first year, so HG1 staffs its first shop sooner and the played path moves a tenth of a point
       across a knife edge. A moved played city, not a fault: named in the ship file with HT1-H.
     - **CreditCrunchCheck** 4's fixture premise (on the box "the prior bank offer issues new shares in a listed
       bank at its native traded price"; here, on the final classes, "after the native household issue means the
       next slice still lacks mortgage capital"): green with origin/main's PriceIndex, red with HG1's, the gate's
       mend on or off. Its city is played to m270. The likely route (inferred, not traced month by month): a
       default city's months before its first shop now count to the basing on rent and meals, so its index bases
       sooner and the rule reads a real rate sooner. The m270 city is another (the bank's leverage 0.0303 against 0.0399, the share price 0.0026 against
       0.0016), and CBK3's knife-edge (the bank in its band, an issue the quote supports, and the next slice short
       of capital after the households' issue) is not found there. The index doing what the brief asks; the
       fixture finds its condition in the played city instead of causing it: named with HT1-H, whose row is
       exactly that class.

## 5. Where it meets other batches

- **ASH3, section 4 (finding 6)**: "Retail held back a Small Grocery Store it could staff at only 61% (2 college
  posts at an 11% fill)". With HG1, while the town goes short, that store is read at the fill and passes. ASH3's
  other half (Retail weighing every format, and a short shelf raising its price) is not built here.
- **ASH4** (HouseholdBalance.cushion, Household.everyday): HG1 reads only `groceriesNeeded()` and the hunger
  getters, so no conflict is expected on merge.

## 6. Left over

- **The founding year goes hungry** (queued as HG2). In HungerCheck's default-founded city nearly the whole town
  goes hungry from about month 4 until its first shop sells, at about month 15-17. FOOD now says so, red, with
  Build > Shops. Its cause was not traced here; it is not the staffing gate (the default arrivals hold the
  diploma). It is pre-existing on main.
- **The FOOD reading at level 0** can say "2% go hungry, the shops cannot serve them". That is true, and the row
  is not listed.

## For the manual

Not this batch's to write. For when an edition comes:
- NEEDS YOU has a FOOD row, listed from 4% of the people going hungry and red from 20%. It says whether the shops
  cannot serve them (Build > Shops) or they cannot pay (Policy > Promises > Food).
- The inflation tile's "rate in ~N mo" now counts down in a city whose shops sell no groceries, on rent and what
  else the city buys. Groceries join the basket after a year of sales.

## Logs kept

target/named1.txt, target/hgfinal.txt, target/hgfinal2.txt, target/HungerCheck-mutated.log, target/HungerCheck-oprate.log,
target/HungerCheck-nolimit.log, target/HG1-ens.txt and target/ensemble/HG1/, target/HG1b-ens.txt (stopped) and
target/HG1c-ens.txt, in this worktree.
