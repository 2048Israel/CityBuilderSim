# R1g — the 1900 city grows: notes

Brief: `docs/queue/R1g.md`. Builder: Claude (Opus), headless, 2026-11-19, on
`batch/R1g` from origin/main 33a626d9f (0.9.156), merged to d05201e11
(0.9.157, RN1-H) before the harness was run.

## 1. The cause

**The 1900 city stalls on two things at once: 1901's wages pay for today's
homes (the rent), and today's posts meet 1901's workers (the skills).
Relieve either alone and it still stalls; relieve both and it grows at
about today's pace. Neither the dated money, nor the 1900 research, nor the
unfitted equipment is what holds it.** The harness is `Village1900Check`
(section 3 below); the controls are runs of the 2,400-month playtest with
one facet changed in throwaway copies of the classes (`target/r1g/exp`,
never committed).

### What the 1900 playtest does (0.9.156, `-Dplaytest.founded=1900 -Dplaytest.trace`)

| month | today's city | the 1900 city |
|---|---|---|
| 60 | 814 | 476 |
| 240 | 1,465 | 561 |
| 900 | 34,860 | 486 |
| 1200 | 39,038 | 480 |
| 2400 | 1,650,471 | 990 |

- 462 people arrive in months 13-54, then **nobody for 2,276 months**
  (to about month 2330). The posts sit at 349-381 from month 40 to 2257,
  about 56% filled.
- The treasury hoards ($9.3M at month 1023, $257M at the end) and borrows
  nothing until month 1037 (US$129k abroad). Construction is idle from
  month 43 (`consUtil` 0). The automatic works report every service
  "served 1.0"; there are no school posts in 2,400 months.
- The sectors' words (`Game.getLastInvestments()`, the probe below) at
  months 60, 120 and 240: **staffing** holds Manufacturing ("could staff 0%
  of a Wire & Cable Works now and 35% with a year's arrivals; it wants
  80%"), Agriculture, Luxury Retail, Wood Products, Retail and Restaurants;
  **money** holds Heavy Industry's Blast Furnace and Cement's Lime Kiln
  (returns under 1.25 times the interest, not cash); the Copper Smelter
  would lose money; Business Services lacks 39 licences; Freight has no
  rail; Real Estate says "housing ahead of jobs". Today's city orders a
  Fabrication Shop at month 31, a Fabrication Works at 37, a Vehicle Works
  at 43 and a Contact Centre at 52.

### The rent: 1901's wages pay for today's homes

A home's required rent is its cost per household times
`RealEstate.LANDLORD_YIELD` (`RealEstate.rentRequired(boolean)`, L420), the
cost being the builders' non-material cost, the materials at the
construction material price, and the plot (`EconomyManager.repriceHousingCosts()`,
L588). Nothing in it is dated: the 1900 city's family home requires
0.0088 a month at month 25 against today's 0.0086. Its households earn
1901's wages (`WageLadder.Y1901`, about a quarter of today's real wage):
household wage 9.1 at month 25 against today's 27.5, 9.9 against 45.6 at
month 40, 12.5 against 81.2 at month 120. So rent takes about 0.65 of
take-home at month 40 (an affordability pull of 0.46), 0.61 at month 60 and
0.94 at month 240, against `TARGET_RENT_BURDEN` 0.30
(`RealEstate.java:123`); today's city pays at or under the target (pull
1.00, 1.00, 0.91). `Migration.affordabilityPull()` (L307) cuts the city the
conditions support to 416 at month 40 and 474 at month 55 against 477
people, and 354 at month 240: **under the population, so nobody comes.**
(`Game.java:15556` hands the burden to the migration each month.)

### The skills: today's posts meet 1901's workers

- 1901's arrivals are 73% without a diploma, 23% with one (the model's
  diploma band is "a trade": the 1913 knot's mechanics, clerks and traders,
  `ArrivalMix.java:90`); today's are none without one (`ArrivalMix.TODAY`).
  A worker takes a post at or below their band, never above.
- At month 30 the 1900 village has posts [265, 157, 21, 5] by band (none,
  diploma, college, university) for a workforce of [170.5, 55.7, 4.8,
  0.05]; today's twin [295, 169, 18, 5] for [0, 389, 22.5, 1.5]. The
  diploma posts are mostly the Construction Depots' (75 for 5) and the
  Bakery's (48).
- R2d's 1900 recipes add the cutting crews (`buildings.json` L1761, "R2d
  mapping estimate: DIPLOMA posts are the cutting crew"): the 1900
  Fabrication Shop wants [68, 248, 5, 0] against today's [68, 62, 5, 0]. At
  month 30 the 1900 village could staff 0.294 of one, today's twin 0.975;
  the gate is `Sector.MIN_STAFFABLE_TO_ORDER` 0.80 (`Sector.java:262`).
  Every exporter's first plant is refused the same way.
- The native-born get no diploma: there is no school (no posts in 2,400
  months; `AutoBuilder.kept()` builds one only when warranted and with
  somebody to teach), so the 1900 city's diploma workers die out: 59 at
  month 60, 36.7 at 240, 16.8 at 600, 6.4 at 900, 2.3 at 1200, 0.3 at 1800.
- So labourers stand idle beside vacant diploma posts, and from month 240
  the arrival brake (`Migration.BRAKE_FROM_MONTH`, L461; arrivals stop at
  `ARRIVALS_STOP_OUT_OF_WORK` 12% out of work, L458) shuts the door the
  rent had left ajar: the rent-relieved control below runs 13-36% out of
  work from month 240 to 1200.

### The controls (0.9.156, the 2,400-month 1900 playtest, one facet changed)

| run | m60 | m240 | m600 | m900 | m1200 | m2400 |
|---|---|---|---|---|---|---|
| the 1900 city | 476 | 561 | 604 | 486 | 480 | 990 |
| today's arrival mix (`ArrivalMix.at()` returns `TODAY`) | 345 | 396 | 378 | 342 | 1,117 | 2,344 |
| today's recipes (`Recipes.choose/apply` skipped) | 354 | 442 | 372 | 355 | 350 | 578 |
| today's recipes and mix | 486 | 1,618 | 1,971 | 1,785 | 1,713 | 7,009 |
| the rent relieved (`affordabilityPull` 1) | 984 | 1,156 | 867 | 1,152 | 894 | 44,960 |
| the rent relieved and today's mix | 812 | 654 | 4,182 | 20,701 | 51,530 | 1,157,156 |
| today's city (main) | 814 | 1,465 | 9,624 | 34,860 | 39,038 | 1,650,471 |

- Today's mix alone: diplomas are plentiful (the diploma wage falls to
  0.044 from 0.092 at month 40) but rent still takes over twice the target
  (pull 0.47), so nobody comes from month 40.
- Today's recipes and mix: the first 20 years match today's, then the
  native-born grow up without diplomas (466 diploma workers at month 241,
  57 at 901), the school posts come only at month 421 (48 posts, 25
  filled), and the staffing gate holds every exporter at 0% "even with a
  year's arrivals" from month 420 to 600.
- The rent relieved alone: 984 people by month 60, then the skills hold it
  (diploma workers 149 at month 240, 56 at 600; 13-36% out of work, the
  brake shut).
- Both relieved: it grows, and the schools come at month 600 (196 posts),
  as today's do (900 at month 600).
- R1d's control (1901 on today's wages) grew faster early and then shrank:
  today's wages ease the rent but not the skills.

### What it is not

- **The dated money.** The 1900 treasury holds its cash and owes nothing at
  month 60 (the harness) and borrows nothing until month 1037: no build
  is refused for want of the city's credit. OP1 (every lever open from the
  first month) does not touch the cause.
- **The 1900 research and the unfitted equipment.** Both controls that
  grow keep the 1900 research and the equipment unfitted.
- **The owners' cash.** The money holds are returns under 1.25 times the
  interest on two works; the refusals that bind are staffing and the
  migration target.

### The harness: `Village1900Check` (0.9.157)

The playtest's village (`LongPlaytest.founding()`), founded the 1900 way
beside its twin founded today's way, each founded and played as
`LongPlaytest.main()` does (its switches, the automatic works on before the
village is laid, the founding's months by hand, then its rhythm of a skip,
a look and two months) to month 60, on scratch folders. 19 assertions,
green on 0.9.157 in about 20 seconds:

| at month 60 (the gate at 30) | the 1900 village | today's twin |
|---|---|---|
| people | 471 | 908 |
| rent as a share of take-home (target 0.30) | 0.612 | 0.231 |
| affordability pull | 0.490 | 1.000 |
| the city the conditions support | 462 | 1,280 |
| diploma posts / diploma workers | 141 / 58.3 | 194 / 443.1 |
| Fabrication Shop: diploma posts wanted | 248 | 62 |
| Fabrication Shop: share the city could staff at month 30 (gate 0.80) | 0.231 | 1.000 |
| arrivals in months 56-60 | 0.0 | 38.3 |
| treasury: cash / owed | 253.5 / 0 | 4.5 / 265.4 |

Manufacturing's word in the 1900 village at month 60: "could staff 5% of a
Wire & Cable Works now and 40% with a year's arrivals; it wants 80%". The
1900 village's 471 at month 60 is the 0.9.156 trace's 476 within RN1-H's
move; today's twin's 908 against the 0.9.156 trace's 814 is RN1-H (homes
planned at demand x 1.05). The 0.9.157 traces themselves wait for the gate.

The first fixture advised every month (`LongPlaytest.probeCity()` and the
advisor each month, the probes' way): its today's city reached 3,095 at
month 60 against the trace's 814, so it was not the playtest, and the
harness follows `main()`'s rhythm instead.

## 2. The fix proposed (★ a design call, Jerus's)

Both halves read `Game.foundedUnfitted()` and nothing else, so **today's
city does not move** (the default trace bit for bit); neither rests on a
dated lever, so OP1 does not change it.

- ★ **The rent: the 1900 home.** research-1900 5.3 gives the 1900 family's
  rent as 25% of its staple budget ($2.37 of $9.37 a week, [CYB18]) and
  5.2-5.3 its home: no running water, bathroom, electricity or central
  heat, households of six or more. The game builds today's home and lets
  it to 1901's wages, 0.61 of take-home. Proposed: in a city founded the
  1900 way the homes are built to the year's standard through `Recipes`'
  1900 path (as the plants' recipes are): the residential templates' cash
  cost and materials scaled so a 1901 household's rent is the study's 25%
  at the founding, returning to today's home along the year as the wages
  rise toward today's ladder. Alternatives: more households to a home (the
  study's large households, a capacity change), or the home costed at the
  city's wage level outright.
- ★ **The skills: the apprentices.** The model's diploma band is "a trade"
  in 1901 (`ArrivalMix`'s 1913 knot), but a native-born worker gains a band
  only from a school (`PopulationManager`: "Everyone starts unskilled
  because, until education buildings exist, the only source of a skill is
  somebody who already had one moving in"), and a 1900 city has none, so
  its tradesmen die out. Proposed: in a city founded the 1900 way, the
  young coming into work without a school learn a trade at the year's
  diploma share (`ArrivalMix.at(month)`, 0.23 in 1901, 0.43 in 1953), as
  the year's arrivals bring it; a school's graduates count instead once
  there is one. Only if measured necessary: R2d's 1900 cutting crews
  ("R2d mapping estimate: DIPLOMA posts are the cutting crew") as machine
  hands without a diploma.
- **Files:** `Recipes.java` (and `buildings.json` if the homes' recipes are
  data, as R2d's are), `PopulationManager.java`, `ArrivalMix.java` (a
  getter), `Village1900Check` turned to the fix, failing with it taken out.
  None is HS1's or QN1's (`RealEstate` is QN1's and is not touched).
- **Measured before it is built:** both candidates as controls on the
  2,400-month 1900 playtest, beside "the rent relieved and today's mix"
  (812 / 20,701 / 51,530 / 1,157,156 at months 60, 900, 1200, 2400), once
  the gate is free.

## 3. What was built (on the orchestrator's go, 13:24 and 13:33)

Both halves behind their own switch, default on, each read only in a city
founded the 1900 way (`Game.foundedUnfitted()`):

- **(a) the 1900 home**, `Recipes.HOMES_1900` (`-Dr1g.home=off` takes it
  out): `Recipes.apply()` calls `dateHomes(game)` after its
  `foundedUnfitted()` return, which strikes every residential template once
  (`datedHomes`, a weak identity set, kept by `clear()` so no template is
  dated twice) at `homeScale()` of today's: cash cost and upkeep through
  `setCashCost`/`setUpkeep` (so the monthly re-strike keeps them), materials
  and points rounded, at least 1.
- ★ **`homeScale()` = 0.2892**: the first knot's shares (.73066 / .23184 /
  .03 / .0075) times each band's wage on `WageLadder.Y1901`, over the same on
  `TODAY`, at most 1. A 1901 household's home costs the share of its wage
  that today's home costs today's household (EP1's `pricedAtFoundingLevel`,
  "a house costs the same share of a wage that it did"). Read against the
  study: the rent at month 60 comes to 0.236 of take-home, beside
  research-1900 5.3's 25% ($2.37 of $9.37), with no figure fitted to it.
- ★ **The land is not scaled**: the lot is the city's land market's
  (`LandMarket`), priced by what is built on it, not the home's recipe.
- ★ **The scale is fixed at the founding**, not walked along the year as
  proposed: the city's wages are laid once from `WageLadder.of(founding)`
  (`Game.java:1300`) and move only by the market after, so there is no
  dated wage for the scale to follow; the re-strike already re-prices the
  home's labour part at the city's own wages. If a later batch dates the
  wages along the year, the scale should follow them.
- **(b) the apprentices**, `PopulationManager.APPRENTICES_1900`
  (`-Dr1g.apprentices=off`): `apprentice(comingOfAge, tradeShare,
  schooled)` adds `max(0, comingOfAge × tradeShare − schooled)` to the
  diploma band's skilled heads. The one Game line, after
  `applyBandFlow(education.getGraduates())`, passes the month's new adults
  (`cohorts.getPromoted(TEEN)`), `ArrivalMix.tradeShareAt(month)` (new: the
  knots' diploma share by `at()`'s look-up) and the month's diploma
  graduates, so a school's graduates count instead of, not on top of, the
  apprentices.
- ★ **After 2021 the trade share stays the last knot's, 0.68** (1/1.47), not
  zero: `at()` hands over to TODAY there, but TODAY has no school-less
  trade figure, and a city founded in 1900 that reaches 2021 has schools
  whose graduates net the apprentices out anyway.
- ★ **R2d's cutting crews are left as they are**: measured first; built only
  if the pace runs show the diploma posts still unfilled.
- **`Village1900Check`** sections 5-7: (5) the House at the scale, its
  materials and points, the land equal, the home dated, the rent at most
  1.5 × TARGET_RENT_BURDEN and under the stalled village's, people still
  arriving at the end of five years; (6) apprentices made, equal to the
  coming-of-age × trade share less the graduates, more diploma workers than
  the stalled village, none with the switch off; (7) today's city with both
  switches on and both off: the same people, cash, GDP, rent burden,
  workers by band and House, undated, and no apprentice.

### Checks

| check | result |
|---|---|
| `./batch-check.sh Village1900Check`, both on (52450660b + f57009450) | All checks passed; home village 973 people at m60 (stalled 471), rent 0.236 (stalled 0.612, today 0.231), 41.4 arrivals in the last 5 months; 4.31 apprentices in five years, as expected; 63.2 diploma workers (stalled 58.3) |
| the same, `-Dr1g.home=off -Dr1g.apprentices=off` | 7 FAILED: section 5's five, section 6's two; section 7 passes (it sets both switches itself) |
| on 0.9.158 (merge 2e6a856f2), both on | All checks passed; home village 1,011 at m60, rent 0.209 |
| on 0.9.159 (merge d455a6eb4), both on, before the restatement below | 1 FAILED: "nobody arrives in the 1900 village in the last 5 months", 2.6 (HS1's households' money moved the stalled village: 504 at m60, not 471) |
| on 0.9.159 with `STALLED` (3397d8191), both on | All checks passed; home village 1,000 at m60 (stalled 504), rent 0.258 (stalled 0.613, today 0.232), 35.9 arrivals in the last 5 months against today's twin's 35.5; 4.40 apprentices, as expected; 67.4 diploma workers (stalled 62.5) |
| the same, both off | 7 FAILED: section 5's five (its arrivals check now 2.6 against 35.5), section 6's two |

★ **The stall restated against today's twin** (`Village1900Check.STALLED`,
a tenth): on 0.9.159 the stalled village draws 2.6 people in the quiet
months, not none, so "nobody arrives" is no longer the finding; it is
"under a tenth of what today's twin draws" (2.6 of 35.5, 0.07), and section
5's "people still arrive" became "at least a tenth of today's twin's"
(35.9 of 35.5), so that check still fails with the home off (it passed on
2.6 ≥ 1). The premise, that the stalled village has all but stopped drawing
anyone while today's twin still draws, is what it was.

## 4. The pace (0.9.160, the 2,400-month playtest, R1d's table re-measured)

Each run is one LongPlaytest with `-Dplaytest.trace`, one JVM at a time,
never beside a gate's suite (`target/r1g/pacechain.sh`, which ended and
re-ran any run a gate began beside). `main` is origin/main's own four
classes (`Game`, `Recipes`, `PopulationManager`, `ArrivalMix`) compiled over
the branch's, the rest being the same bytes. The rent at month 60 is
`0.30 / affordPull` from the trace where the pull is under 1; where it is 1
the burden is at most 0.30 and the figure in brackets is
`Village1900Check`'s, which follows the same founding and rhythm. The
diploma fill is `filled1 / posts1` in the labour trace. The runs were
measured three times as main moved under them (QN1 0.9.158, HS1 0.9.159,
R1e 0.9.160); this is the last, and the earlier logs are kept in
`target/r1g/v158`, `v159`.

| run | m60 | m900 | m1200 | m2400 | rent at m60 | diploma fill m60 / m900 / m1200 / m2400 |
|---|---|---|---|---|---|---|
| today (default; main and branch the same) | 909 | 32,154 | 40,217 | 1,658,853 | ≤ .30 (.232) | 1.00 / 1.00 / 1.00 / 1.00 |
| 1900 on main | 482 | 592 | 574 | 559 | .623 | .41 / .06 / .03 / .00 |
| (a) the 1900 home alone | 1,334 | 836 | 7,163 | 781,449 | .332 | 1.00 / .48 / 1.00 / 1.00 |
| (b) the apprentices alone | 489 | 1,350 | 2,136 | 6,029 | .625 | .45 / 1.00 / 1.00 / 1.00 |
| (a) + (b), mended (f772ed866) | 1,334 | 5,608 | 22,336 | 902,664 | .335 | 1.00 / 1.00 / 1.00 / 1.00 |

The months between, for the shape: today 1,298 at m240, 8,210 at m600,
91,838 at m1500, 448,089 at m1800, 1,045,627 at m2100; the 1900 city on main
564, 611, 562, 561, 561; (a) 1,702, 730, 20,755, 96,109, 439,075; (b) 578,
818, 4,806, 5,770, 5,513.

- **Each half lifts the other's limit.** (a) alone clears the rent (the pull
  1 from m600) but meets the skills: at m600 its diploma posts are 54 filled
  of 153 and the city falls from 1,702 to 730; it grows again from m1200.
  (b) alone fills the diploma posts from m900 (263 of 263) but the rent
  holds it: the pull stays .34 to .42 (a burden of about .75) through m1200.
- **(a) at m240** has 52% unemployment and a pull of .72 (burden .42): the
  first arrivals outrun the jobs before the rent settles.
- **Runtime:** the 1900 run on main took 5 minutes, (b) 11, (a) 126, and
  today's 16, all beside other sessions' harnesses. A growing 1900 city runs
  far slower per person than today's (781,449 people in 126 minutes against
  1,658,853 in 16); that matters if LongPlaytest's default becomes 1900
  (step 4), and is to be measured before it does.

**Today's city does not move, to the bit** (the orchestrator's 13:33
condition), measured twice: on 0.9.158 and on 0.9.160 (main at 0.9.159,
which R1e's changelog states is the default city's 0.9.160 byte for byte)
the default run on main and on the branch wrote the same eight trace files
(`cmp`: bank, bonds, borrow, build, house, labour, month and pop, 2,405
lines each but borrow's); the logs differ in one object hash (a
`LandParcel@` in a works decision's toString) and the runtimes. Every run
exits 1 on main's own reds: the default city's "an old works moved" at
month 2400, the 1900 city on main's five households' unsettled-line bounds
at month 2402. Neither is R1g's.

**A home is dated once, through loads.** `datedHomes` is a weak identity
set on the Game's own `Recipes` (final, one per Game). A load or a new game
rebuilds the BuildingManager (`buildWorld()`, "made a new BuildingManager"),
so the loaded city's templates are new objects at today's costs: a 1900
save's are dated once by the load path's `recipes.apply`, a default save's
not at all. No template is copied (`NewUnit`, QN1, holds the template
itself), and no template cost is saved.

**Why the grown 1900 city is slow (main's, found by R1g's runs).** Four
thread dumps of the (a)+(b) run at month 1679 (330,249 people, 19 months in
9 minutes) all stand in the same frames: `LongPlaytest.runPlayer` →
`Game.nextMonth` → `AutoBuilder.step` → `BuildAdvice.suggestFor` → `count` →
`supplyDemand` → `figure` → `WorksValue.pendingPowerLoad` → `pendingLoads` →
`NewUnit.of` (QN1, 0.9.158) → `EquipmentFit.fittedForNew` → `chosenForNew` →
`priced` → `pricedAsStanding` → `gainOf` → `RecipeChanges.netGainForSet` →
`gainForSet` → `netDifferenceAt` → `feesAt` / `valueAt` → `absencesAt` →
`RecipeChange.knownAt` → `Research.knownFrom` → `ResearchCatalog.knownFrom`.
Every advice count re-fits every pending site, piece by piece, against every
recipe change's research date; and `ResearchCatalog.knownFrom` reads a code
that is not a year through a thrown `NumberFormatException`. Only a city
founded the 1900 way carries recipe changes and fits, and none grew before
R1g, so no harness or trace has met it. Not R1g's code: proposed for the
queue (★ the orchestrator's call), and to be fixed before LongPlaytest's
default becomes 1900 (step 4), whose 2,400 months would otherwise take
hours. The dumps are `target/r1g/dump1-4.txt`.

**The mend (the orchestrator's 23:27: "mend it in R1g ... as narrowly as
you can, with no behaviour change").** `ResearchCatalog.knownFrom` keeps
each "since" it has read in `KNOWN_FROM` (37308d679): the catalogue is the
packaged file's, read once (`Holder.INSTANCE`) and never changed, so the
month a given "since" is known from never changes, and a code that is not a
year is read once instead of through a thrown exception every call. The
answers are the same, so the city is: the proof is today's trace against
main's, byte for byte, and the mended (a)+(b) trace against the stopped
reference's (`target/r1g/ref/pboth-*`, months 1 to about 1830, ended at 00:33 for OP1's dry run) over every complete line.

The cache alone made the run 3.5 times faster (m1482 in 9 minutes against
the reference's 27; still 58 months in 9 minutes at month 1757, so m2400
some two hours off): not normal time. Six thread dumps of that run at month
1757 stood five times inside `EquipmentFit.priced` under `NewUnit.of`, the
cost now spread through the fit itself. So the second mend:

- ★ **A new unit's fit held while one piece of advice reads the city**
  (`NewUnit.heldWhile`, f772ed866), narrower than the orchestrator's "the
  fit memoised per month": `BuildAdvice.suggestFor()` (pure by its own
  javadoc: it reads the city and quotes) runs inside `heldWhile`, and in it
  `NewUnit.of(game, t)` keeps each template's fitted unit from its first ask
  to the reading's end, so `WorksValue.pendingLoads()` and
  `pendingPowerLoad()` stop re-fitting every building under way at every
  count `BuildAdvice.count()` tries. A month's prices can move between
  readings (an order queues points, the scarcity premium rises), so the
  hold ends with the reading, and outside one `of()` is fresh as before. A
  unit is a value (final fields, unmodifiable maps). A city founded the
  default way returns `standing(t)` before the hold and never reaches it.
  `suggestionFor` is the old body, unchanged.
- **The proof** (`target/r1g/proof.sh`, `proof-ref.txt`): the mended
  (a)+(b) run on 0.9.160 + both mends against the reference, over every
  complete line the reference wrote: bank to month 1852, bonds 1875, build
  1841, house 1876, labour 1833, month 1850, pop 1828, all byte for byte
  (borrow's buffer had not flushed). The cache-only run was the same over
  every line it and the reference both held (to about month 1700) before
  it was stopped for the memo. Today's city: below, on 0.9.161, main
  against the branch with both mends.
- **The time:** the mended (a)+(b) ran its 2,400 months in 25 minutes,
  exit 0 (the reference: 144 minutes to month 1850, stopped; today's city:
  16 minutes). That is the run in the 0.9.160 table above.

## 5. The pace on 0.9.161 (OP1), with both mends: the table for the call

The same six runs, one JVM at a time, on the merge of 0.9.161 (26d9ae502)
with both mends and the harness (ed05e89f0); `main` is origin/main's seven
differing classes (`ArrivalMix`, `BuildAdvice`, `Game`, `NewUnit`,
`PopulationManager`, `Recipes`, `ResearchCatalog`) over the branch's.
OP1 moves both cities (every lever open from founding): today's at m2400 is
556,655, not 1,658,853.

| run | m60 | m900 | m1200 | m2400 | rent at m60 | diploma fill m60 / m900 / m1200 / m2400 | minutes |
|---|---|---|---|---|---|---|---|
| today (default; main and branch the same) | 1,234 | 28,369 | 32,330 | 556,655 | ≤ .30 (.232) | 1.00 / 1.00 / 1.00 / 1.00 | 10 |
| 1900 on main | 566 | 643 | 646 | 2,764 | .607 | .42 / .14 / .19 / 1.00 | 11 |
| (a) the 1900 home alone | 853 | 3,901 | 24,797 | 949,447 | ≤ .30 (.222) | .96 / 1.00 / 1.00 / 1.00 | 25 |
| (b) the apprentices alone | 570 | 1,142 | 2,118 | 25,174 | .603 | .45 / .99 / 1.00 / 1.00 | 5 |
| (a) + (b) | 944 | 30,117 | 110,986 | 983,979 | ≤ .30 | .86 / 1.00 / 1.00 / 1.00 | 24 |
| (a) + (b) with the walk back (`-Dr1g.walk=on`, 5bb95f367) | 926 | 5,679 | 16,012 | 630,765 | ≤ .30 (the home alone .233) | .83 / 1.00 / 1.00 / 1.00 | 12 |

Between, at m240 / m600 / m1500 / m1800 / m2100: today 2,668 / 7,905 /
32,473 / 46,024 / 171,829; the 1900 city on main 737 / 575 / 1,451 / 1,011
/ 1,620; (a) 3,716 / 2,890 / 112,804 / 312,864 / 617,497; (b) 816 / 852 /
6,287 / 9,843 / 12,906; (a)+(b) 1,656 / 6,805 / 267,106 / 647,332 /
777,523; with the walk 1,775 / 2,388 / 32,830 / 203,825 / 514,349.

- **Today's city does not move, to the bit, with both mends:** main and the
  branch wrote the same eight trace files (2,402 lines each but borrow's
  62) and the same log but its runtimes and object hashes; both exit 0.
- **The 1900 city with both halves outgrows today's from m900** (30,117
  against 28,369; 983,979 against 556,655 at m2400). ★ One cause to weigh:
  the home's scale is fixed at the founding (§3), so a 1900 city's homes
  stay 0.29 of today's cost while its wages move with its market; the
  proposal's other form, the scale walked back to 1 along the years (by
  the knots, to 2021), would bring the late city toward today's. Jerus's
  call, with the pace.
- (b) alone still stalls on the rent (the pull .50 at m60), (a) alone on
  nothing that holds it long: on 0.9.161 its diploma posts are filled by
  m900 (on 0.9.160 they fell to .48 there). Together they are fastest
  early (6,805 at m600 against (a)'s 2,890), with 26% unemployment at m240
  and the pull .72 at m600 as the arrivals run ahead.
- **Exits:** today's and (a)+(b) exit 0. The 1900 city on main exits 1 on
  four reds at its last months (industry farther out than the homes at
  m2400; the households' own line, UnsettledIncome, UnsettledRent and
  UnsettledFees past their bounds at m2402); (a) and (b) on one of the
  same, UnsettledFees at the last month (m2405, m2402). That is main's
  family (HS1's households' money in a 1900 city), not R1g's, and R1g's
  (a)+(b) does not meet it.

### The walk back (the orchestrator's 05:49 mail; 5bb95f367)

Built behind `Recipes.HOME_WALK` (`-Dr1g.walk=on`, off by default, so
nothing above moves): each month's `apply()` sets every home to
`homeScaleAt(month)`, which is `homeScale()` (0.289) to January 1901 (the
first knot), 1 from January 2021 (the last), and linear by month between,
because `WageLadder` has no ladder between 1901's and today's. The cash cost
and upkeep move by the ratio of the new scale to the old one, through the
same setters, so the re-strike holds them. Materials and points are set from
the catalogue's own figures, and nothing else changes. The table's last row
is (a)+(b) with the walk: one JVM, exit 1, 12 minutes.

- **The walk brings the late city back toward today's** (630,765 against
  556,655 at m2400, where the held home gives 983,979). The cost is the
  century in between: 5,679 at m900 and 16,012 at m1200, against today's
  28,369 and 32,330.
- ★ **The walk outruns the city's wages.** The 1900 city's wages are laid
  once from 1901's ladder and then move with its market, but the walked
  home climbs to today's cost on a straight line by month. The
  affordability pull stays under 1 from m240 to past m1800 (.61 to .93, its
  lowest .606 at m686), so rent is about .30/.66 ≈ .45 of take-home in the
  middle years. With the held home the pull sits at 1.00 from m960. The
  harness shows the same at m60: rent .233 with the walk against .222 held
  (the home alone; the house struck at 0.312 = `homeScaleAt(60)`).
- **Diploma fill** is as with the held home: .83 at m60, then full.
  Unemployment is 35% at m240 and 9% at m600, then none by m900.
- **Exit 1 on one red:** the households' own line, UnsettledFees past its
  bound at m2404. That is main's family, the same red (a) and (b) met at
  their last month (§5, Exits), not the walk's.
- **If the walk is kept:** Village1900Check section 5's "struck at
  homeScale()" becomes `datedScale()` against `homeScaleAt(month)`. With the
  walk on it fails as it stands, at 0.312386 against 0.289185, which is the
  walk working. A section asserting the walk, and failing with it off, goes
  in with that call.

### (c) The walk paced by the city's wages (the orchestrator's 06:12 mail; 36dc96a82)

Built behind `Recipes.HOME_WAGE_WALK` (`-Dr1g.walk=wage`, off by default).
Each month's `apply()` sets every home to `homeScaleOfWages(game)`. That is
`homeScale()`'s own sum, 1901's arrivals by band, with the market's wages
this month in place of 1901's ladder, in founding money: each job's wage
over the cost of living, times the ladder's anchor over the going rate,
which a reform moves together. It is divided by the same household on
today's ladder and capped at 1. The run was stopped at m2292 for BO1-S's
gate (35 minutes); the full run waits for it.

| run | m60 | m900 | m1200 | m2280 | rent at m60 | diploma fill m60 / m900 / m1200 / m2268 |
|---|---|---|---|---|---|---|
| today | 1,234 | 28,369 | 32,330 | 329,495 | ≤ .30 (.232) | 1.00 throughout |
| (a)+(b) held | 944 | 30,117 | 110,986 | 888,960 | ≤ .30 | .86 / 1.00 / 1.00 / 1.00 |
| (a)+(b) calendar walk | 926 | 5,679 | 16,012 | 556,732 | ≤ .30 | .83 / 1.00 / 1.00 / 1.00 |
| (a)+(b) wage walk (c) | 618 | 9,097 | 44,259 | 1,593,103 | ≈ .32 (pull .93) | .95 / 1.00 / 1.00 / 1.00 |

Between, at m240 / m600 / m1500 / m1800 / m2100: 2,202 / 5,347 / 303,813 /
786,032 / 1,163,383.

- ★ **The index runs ahead of the households' pay.** It prices the
  market's posted wages (their tightness multiples and premia, the likely
  cause, not separated here), so the home is
  dear for what the households take home. The required rent is .15–.19 of
  the unskilled wage from m240 to m1440, against .11–.15 held. The pull is
  under 1 in 916 of the months from m960 to m2292, at its lowest .62 at
  m701, so rent does not hold near .30. In the village's first year the
  tight market lifts it most: the required rent at m13 is 0.00425 against
  0.00252 held.
- My heads-up (06:16) expected (c) to sit near the held row, because the
  real wages over the price index stay at .22–.24 of today's. That was
  wrong: by the market's wages over its cost of living the index rises well
  above 0.29. The index a .30 rent wants is the households' take-home, not
  the posts' wage.
- **Late, it is the largest city of all:** 1,593,103 at m2280 against
  888,960 held, 556,732 with the calendar walk and 329,495 today. Its pull
  is back at 1.00 by m2160.
- **Exits:** none read; the run was stopped (143). Unemployment is 39% at
  m240 and none from m600.

### The fifteen harnesses that read the mended code (0.9.161, both halves on)

Run one at a time on `./batch-check.sh <name>`, with JVMs free between
gates. NewUnitCheck, FitoutCheck, ResearchCheck, EquipmentCheck,
RecipeCheck, HistoricalRecipeCheck, PostwarRecipeCheck,
HistoricalLicenceCheck, LicenceFeesCheck (43/0), PublicLicenceCheck (68/0),
ElectrolyserCheck, StaffingDrawCheck and ReplayCheck pass. EssentialWorksCheck
has its two known reds and nothing new (exit 3).

ResearchViewCheck had one new red: "the Bakery's electric drive (known from
a year) is, with no discovery behind it". That is its research town's
guarantee (b), the drive unfitted on the Bakery at month 14. With the 1900
home on, the village fits it by then: a probe shows fitted 1, unfitted 0,
cash 21,941 against 20,017, and an Industrial Bakery standing. With the home
off it stays unfitted. The model is not at fault: a fitting that pays is a
sector's order (`EquipmentFit.choose()`). The fixture relied on the order not
coming. The town now holds the Bakery's sector, as it already held the
farms' and the mills'. A held sector skips the whole round, building,
fitting and retiring (`Game`'s investment loop). The harness passes with both
halves on, and with both off. KeptCheck, the other borrower of
`researchTown()`, shows only main's known reds (exit 3).

## 6. Jerus's pick: the home's cost moves with wages (0.9.174)

Jerus, 2026-11-20 (the orchestrator's 23:25 mail): the 1900 home stays, and
"its cost moves with WAGES", option (3): not held, and not by the calendar.
Measure it on the wage index and on the households' take-home pay, and take
whichever keeps rent nearer .30. His other words, "perhaps try once people
mortgages exist? idk", are the ship file's Pending row R1g-H: revisit the 1900
home once HO1 (owning a home) ships.

### The six rows (0.9.174, merge ec7bf996a; the playtest's 1900 city, 2,400 months)

The rent is the households' rent over their take-home
(`HouseholdAccounts.getRentBurden()`), read each month from a measuring copy
of LongPlaytest that stays in `target/`, out of the branch.

| the home | pop m240 / m900 / m1200 / m2400 | rent: mean, mean \|b−.30\|, peak |
|---|---|---|
| ★ pay, trailing 12-month mean, set monthly | 1,228 / 14,805 / 51,439 / 1,852,269 | .307, .034, .450 (m529) |
| pay, set each January | 1,259 / 3,456 / 5,656 / 150,677 | .300, .050, .444 |
| pay, set monthly | 1,230 / 2,567 / 5,281 / 6,037 | .308, .047, .421 |
| wage index, set monthly | 1,140 / 4,486 / 6,027 / 7,105 | .315, .048, .460 |
| held (`homeScale()`, .289) | 1,603 / 8,573 / 32,280 / 1,989,152 | .243, .058, .339 |
| today's city | 1,647 / 5,663 / 31,921 / 1,479,891 | .283, .048, .459 |

- **★ The trailing mean.** It grows like the held home (ahead of it from
  m900 to m1500: 237,274 against 233,580 at m1500), and its rent is the
  nearest .30 of every row. A month's pay jumps. A home re-priced by every
  jump (or by a year's jumps in one January step) churned the landlords: on
  the monthly walks their mortgages passed their assets (pay, m600: 3,456
  against 3,129) and Real Estate lost over a fifth of its posts in a
  trailing year eight times.
- **The pay index over the wage index.** Set monthly, the pay's rent is the
  nearer .30 (mean .308 against .315, mean |b−.30| .047 against .048): the
  take-home a rent is paid from, where the wage index weighs the posts'
  wages by 1901's household. Both indexes stall the city set monthly.
- **My 00:15 guess, the level, was half wrong.** Both indexes climb from
  .289 to about .52 by m13 (the young village's tight market bids pay up)
  and stay near .37 to m240, so the walk's homes cost more than the held
  home's for twenty years. But the mean's scale is higher still (.350 over
  m240–1440 against .289 held, .329 for the monthly pay walk), and it
  grows. The churn is what stalls the city, not the level.
- **Exits.** The mean's one red, households' UnsettledFees at m2405, is the
  held home's too (69.3 ppm held, 86.1 the mean, against a bound of 30):
  the trace is below. The January walk adds the CL7 finding (industry
  farther out than the homes) and UnsettledIncome.

### What was built (8eaf2dc1e)

- `Recipes.HOME_WALK`, on unless `-Dr1g.walk=off` (the held home, the
  fallback): each month's `apply()` sets every home to the mean of the last
  `PAY_MONTHS` (12) months' `homeScaleOfPay()`, capped at 1. A game month
  takes the pay once; the founding's second `apply()` and a load's take
  nothing more.
- **The dating set from the catalogue's figures, never from last month's**
  (`BuildingsTemplate.setFoundingCosts()`, a new setter, and
  `getFoundingUpkeep()`): each re-pricing sets the founding cash cost and
  upkeep to the undated figures times the scale. Times a ratio each month,
  as the measuring runs did it, the cost drifted a bit a month, and a
  loaded city's fresh templates stood a bit off the unsaved city's
  (Village1900Check 8 caught it).
- **The months saved.** `DataSave`'s `homePayIndices` and `homePayMonth`,
  written in the save path beside the recipes' choices and put back before
  the load path's `apply()`. The old-saves rule: a save without them (every
  save from before R1g, and every city founded the default way) starts the
  walk afresh at the loaded month, its window that month's pay alone, filled
  over the next eleven months. A 1900 city saved before R1g had no dated
  homes, so nothing in it reads wrongly; no format bump. SaveFileCheck
  passes.
- **The variants out.** The calendar walk (5bb95f367), the wage index
  (36dc96a82), the monthly pay walk (866f23d4b) and the January and mean
  tests (9241667a1) are measured above and live in those commits.
  `homeScaleOfPay()` and `todaysHousehold()` stay; `homeScaleAt()` and
  `homeScaleOfWages()` go.

### Village1900Check (sections 1–8)

| check | result |
|---|---|
| `./batch-check.sh Village1900Check` on 8eaf2dc1e | All checks passed |
| the same, `-Dr1g.walk=off` | 4 FAILED, all section 8's: the window is empty, no pay taken, the House at homeScale() (0.2892 against 0.2892); sections 1–7 pass |
| `./batch-check.sh SaveFileCheck` | All checks passed |

- **Section 8, the walk:**
  - each month takes the households' pay once, the window growing to
    `PAY_MONTHS` and holding there;
  - the pay it takes is `homeScaleOfPay()` as the month before ended;
  - the House stands at the mean of the last twelve, off `homeScale()` by
    more than a hundredth;
  - saved on its scratch folder (slot 10, never a player's) and loaded
    into a fresh Game, the village holds the same months, scale and cost to
    the bit, and a month on it stands where the unsaved village does.
- **Section 5** asserts the House at the scale it stands at
  (`Recipes.datedScale()`), `homeScale()` with the walk off.
- **Section 2's control restated.** Today's twin paid 0.232 of take-home
  at m60 on 0.9.159 and pays 0.331 on 0.9.174, over the .30 target. That
  is main's move, not R1g's (today's city is never dated). The check that
  it pays no more than the target became "under `DEAR_RENT` times the
  target, the line the 1900 village's households are over". The finding,
  that 1901's wages pay for today's homes, is what it was.
- **The harness found one of mine.** My first build reset the window in
  `choose()` every month (a sed that matched twice). Section 8 read a
  window of one month, every month, and failed; the measured runs above
  came from a snapshot of the classes before it.

### The m2405 red: households' UnsettledFees, traced (PB1)

The line is HL1's (`HouseholdBalance.java:1274`,
`lastUnsettledFees += sumOfRows(rowFees) - settledFees`): the fees a row
was billed for treatment and tuition that went to a cell too small to
settle. LongPlaytest holds its MEAN MONTHLY SHARE (the month's amount over
what moved that month, every month weighed alike) under
`OWN_BOUND_UNSETTLED_FEES`, 3e-5 (`LongPlaytest.java:1368-1369`): three
times the larger of the gate's and the nightly's figures for TODAY'S city,
measured 2026-11-11 (7.77 ppm). HL2, on the queue, splits the fees over the
cells that settle and deletes the bound.

| the run (measuring copy, the line each month) | m1–240 | m241–480 | the run's mean |
|---|---|---|---|
| the 1900 city, R1g's final code (8eaf2dc1e) | 523.5 ppm | 239.1 ppm | 126.8 ppm to m1686 (86% of it from the first 480 months) |
| the 1900 city, R1g's switches off (`-Dr1g.home=off -Dr1g.apprentices=off`) | 404.4 | 286.7 | (to m480) |
| today's city (`-Dplaytest.founded=today`) | 96.0 | 36.8 | 21.8 ppm on 0.9.174 (today174) |
| the held home, the mean (2,400 months) | | | 69.3, 86.1 |

- **Not R1g's code.** With R1g's switches off, the 1900 city's share is
  the same size. It is 4–8 times today's in the same months, where the
  households' unsettled income is 1.4–2.7 times today's: the 1900 path's
  fees, against what moves, are what is large, at the same size of town.
- **Why the gate sees it now.** R1g moved the gate's playtest to the 1900
  city (step 4). The bound was set on today's city, and the 1900 city's
  first forty years, a village of under 2,000, carry the mean past it.
- **Passed by name to PB1** (the ship file's `## queued` and `## known-reds`):
  why the 1900 city's fees go unsettled at four to eight times today's
  share, and LongPlaytest's own-line bounds set again from the city the
  gate now plays, unless HL2 has deleted them first.
- **The run's time.** The 1900 playtest is the slower one: 38 minutes on
  this PC for 2,400 months (the mean, 1,852,269 people) against 18 for
  today's (1,479,891). Late months run at about 80 per 9 minutes.


## 7. Recovery and the reload mend (2026-11-21, R1gc2)

Recovered the same `batch/R1g` worktree at af8864834, preserving its
uncommitted Sector/ReloadCheck mends and generated indexes. The original
launch brief is archived in `C:/Users/Jerus/cbs/sessions/R1g/in-1.txt` and
the old worker's last work in `out-3.jsonl`. The accepted decisions stand:
the home's cost follows the households' trailing twelve-month mean pay,
and the founding screen and playtest default to 1900. No pace experiment
was repeated. The recovery source mend is 47ae0d63f; main's GB5 and TN1
were then merged at 469f69263 (main 5790d0506, 0.9.178). Only generated
indexes conflicted; they were regenerated. TN1 records a byte-identical
default trace; the newest ensemble baseline remains `docs/ensemble/0.9.177.md`.

**The captive fault, approved by the orchestrator at 05:02.**
Sector.produceStock subtracted the output row's captive use from an order
after a farm's horse share fell to zero. Both production paths now set
that figure to zero when captive(g) is false. The original trace found
4.398 tonnes retained after the last horse month, and the loaded farm
made 4.128116 tonnes more crop because its fresh row held no old feed.
The fault also occurred with R1g's switches off. This changes the 1900
transition. Today's founding has no horse share; ProcessingSector's
other captive test is membership in its makes/uses sets, fixed for that
founding. Thus the reset does not move today's founding's trace. This is
a source-based conclusion, not a newly run 2,400-month comparison; the
earlier bit-for-bit comparisons in sections 4–5 stand for the preceding
model changes.

Village1900Check section 9 causes the last-horse transition on its scratch
village. Every standing tractor is marked unfitted; the model's recipes
and equipment forms are applied; a crop must have positive captive feed.
All tractors are then fitted and both passes applied again; the horse
share must be zero, and the same output row's next crop must have no
captive feed. The first fixture omitted the equipment-forms pass and
failed its own zero-horse premise; the normal pass fixed that premise.
The completed harness passes all nine sections. An isolated class overlay
then ran the same transition with current Sector (exit 0) and with Sector
from af8864834 (exit 1): both premises pass on the old class, and only the
last assertion fails, retaining 3.326254965 tonnes. No worktree source was
replaced for this negative proof.

**ReloadCheck 3b is caused, with its assertion retained.** FS17 has shipped,
so there is no by-name row for it. The preserved ground/yard/salvage mend
was necessary but insufficient. A diagnostic on the exact checkpoint
sequence found the city at m1208 with cash -4583.4374: LongPlaytest.build
returned false, and no House was on site. Its borrowing guard refuses that
treasury. The fixture now gives it the model's Founding.WEALTHY_CASH,
requires the normal House order to succeed, and works it for a normal
month before the save. Construction.beforeBank reports the month just
closed, so that draw is on the next statement. All other FS17 conditions
are placed after the working month. The order size and by-hand assertion
are unchanged. ReloadCheck passes completely, including the caused point,
all checkpoints, both subsequent months, the skips, Earth and autosave.

**The other moved premises.** HousingDoorCheck's 1900 city has one red:
the watched window finds no order the bank would refuse for its capital.
Today's founding passes on the same economic code. Its other housing,
trim and household cash-identity assertions pass; the one row is FS11.
CreditCrunchCheck has seven reds on 1900, two already on main under FS9.
Its five additional assertions all pass with today's founding (which has
only those two known reds). At m270 the 1900 yard covers all six blocks,
so the door and plan are both 930.4; the bank funds the whole order on an
insured mortgage instead of trimming it. The fixture's supposed whole-order
equity ask is -84.7, while the actual down-payment issue offers 157.6.
Thus the extra price/issue/trim assertions lack their stated premises,
not a working lender rule. Those five rows are FS9. No bank or business
debt code was changed. The obsolete pre-FB15 HousingDoor red is not added.

The ship file also adds the two history lines approved at 05:02 under
RN2, beside main's two save lines. The earlier caused point at m1211
(`target/r1g/reload-mend.txt`) showed unsaved RealEstate.ownedCapacity
leaving the loaded stranded share at 0.24110154570507586 while the live
share diluted to 0.24020725213792052, changing rent and the price index,
then the money path. The current final ReloadCheck run does not reach
that gap; the two rows preserve the already traced, approved transfer.

**Review as a stranger.** Read the entire saved source delta against main:
the pay window's save/load order, exact catalogue-based costs, apprentice
netting, pure advice hold, catalogue cache, captive reset and causing
fixtures. Corrected a Founding javadoc that still said the screen opened
on All fitted. The pay window restores before Recipes.apply and takes one
index per month; the advice cache is thread-local and removed in finally;
the catalogue cache is concurrent. This builder pass does not replace the
fresh stranger review owed for money, saves and threading.

### Recovery checks and the remaining gates

Every game JVM named the main worktree's earth-2.bin and world-1.bin with
C:/ paths. Named harness calls were sequential. An early HousingDoor call
was stopped while ReloadCheck's post-check wrapper was finishing and then
rerun; only completed results below count. The 10:48 orchestrator mail
explicitly allowed focused laptop checks beside a box gate, superseding
the old prompt's global gate-file hold. The 11:08 hold reserved the laptop
for FS5; the already-started final ReloadCheck was owned to exit, and no
new long check was started after that hold.

| check | completed result and evidence |
|---|---|
| --fast, before and after the main merge | green, compile/maps/Stale (0 firm), `target/r1g/recovery-fast-merge.txt` |
| Village1900Check | all nine sections green, including saved mean equality and the last horse, `recovery-Village1900Check.txt` |
| isolated captive transition | current Sector exit 0; pre-mend Sector exit 1 with both premises satisfied, `recovery-captive-positive.txt`, `recovery-captive-negative.txt` |
| ReloadCheck on 0.9.177 | all green, 88 s, `recovery-ReloadCheck-v177.log` |
| ReloadCheck after merging 0.9.178 | all green, 93 s, including by-hand 3b, `recovery-ReloadCheck.log` |
| HousingDoorCheck | 1900: one FS11 premise red; today's control: green, `recovery-HousingDoorCheck.log`, `recovery-today-HousingDoorCheck.log` |
| CreditCrunchCheck | 1900: seven reds (two main FS9, five added FS9); today's control: only the two main FS9 reds, `recovery-CreditCrunchCheck.log`, `recovery-today-CreditCrunchCheck.log` |

All log names without a directory above are under `target/r1g/`.
The old failed dry run `cbs-gate-R1g-A8QKMhXJ` predates the mends and is not
reused: no gate-run line. A read-only look at its kept suite recovered the
box's LongPlaytest time, 248.9 seconds, against section 6's 38 minutes on
this PC. No new full pick, long playtest, ensemble or dry run was run.
The pick is ALL; provisional readiness stays in place for the fresh
review, the final ensemble against the newest baseline after the next
approved model/base merge (currently 0.9.177), and the orchestrator's gate.
MC2's model is due to ship first; do not run
the ensemble ahead of its merge or reopen Jerus's accepted pace choice.


The project write-up is [The 1900 city grows](../../claude/the-1900-city-grows.md).
Record validation passed strict UTF-8/LF, the changelog's twelve-line limit,
the provisional marker and absence of a stale gate-run, known-red row syntax,
and coverage of all eight captured HDC/CCC failing assertions by main's
registry plus this ship file. These are focused results; the ALL gate
remains owed after review and the approved final ensemble.

## 8. REV-R1gc2-1: gross school diplomas before degree outflow

The fresh review returned NOT SHIP with one P2, an R1g fault. Game passed
Education.getGraduates()[DIPLOMA] to apprentice(). That is net movement:
college and university completions take existing diploma-holders out of
the band. Those departures do not undo the school leavers' diplomas and
must not create more apprentice places. The review found no further issue
in the pay window/save/month order, cache lifetime, captive/3b mends or
supplied named-red dispositions.

Game now passes Education.getNewDiplomas(), the gross school-leaver flow.
The preceding PopulationManager.applyBandFlow(education.getGraduates())
is retained, so degree completions still move people out of the diploma
band and into the degree bands. PopulationManager's parameter documentation
now says gross completions. The existing school-less apprentice control
is retained; its accounting read now uses the gross getter too. Today's
founding still bypasses the apprentice call. The model delta is fewer
extra qualifications in 1900 cities where schools and degree courses
finish together; no saved field or format changes.

Village1900Check section 10 is a causing control through the actual Game
month, including advanceDemographics and its production qualification
call. Two identical 1900 school towns use EducationCheck's short funded
school fixture and real course-queue restore helpers. After five setup
months, each has college and university cohorts due to finish in its next
month. Schools charge no tuition, and both towns start that month with
identical skilled heads. One runs with apprentices on, one off. The test
requires positive gross school diplomas covering a positive apprentice
quota, positive college and university completions, and net diploma
movement below that quota. It also requires identical gross/degree flows
in both controls, zero extra apprentices and exactly equal skilled heads.
Thus the expectation is zero, not a copy of apprentice()'s formula.

The measured month, m7: gross school diplomas 10.300621; college and
university completions together 18.559080; net diploma movement -8.258459;
apprentice quota 2.169946. All five fixture premises pass. Current production
wiring adds zero apprentices and matches the school-only control to the bit.

**Negative proof, only production wiring reverted.** A generated copy of
the current Game.java replaces just the apprentice argument with the old
getGraduates()[DIPLOMA] expression. It is compiled into an isolated class
directory, ahead of the same current test and other model classes; no
worktree source is replaced. All five premises still pass, but the old
call adds 2.169945527808767 apprentices and the diploma heads become
851.8747566130132 against the control's 849.7048110852045. College and
university heads remain equal. Both semantic assertions fail. The current
proof exits 0 and the old-wiring-only proof exits 1 (two failed assertions).

Evidence: `target/r1g/review-qualification-positive.txt`,
`review-qualification-negative.txt`, and `review-qualification-negative.diff`
in that directory (a one-line production-argument diff). The short driver
is `R1gSchoolQualificationProof`, calling only the committed section 10;
`target/r1g/review-qualification-negative.sh` builds and runs the paired
proofs. Compilation and both proof JVMs together took 9.1 seconds. Every
JVM named the C:/ main-worktree earth/world paths. `--fast` regenerates
the indexes for the final source. Full Village1900Check, ReloadCheck and
all other long checks remain held for FS5's software slot; section 7's
long-harness results predate this review mend. No by-name waiver is added
for this own-code fault. The marker remains, and a fresh delta review is
requested before the MC2 merge and final ensemble on the approved slot.

The final `./batch-check.sh --fast` exited 0: compile 51 seconds, indexes
regenerated, Stale zero firm findings (`review-qualification-final-fast.txt`).
Source/record whitespace and UTF-8/LF validation passed; the regenerated
month-order retains its canonical 25 two-space Markdown hard breaks.
Known-red syntax and the provisional marker also passed validation.

### REV-R1gc2-2: SHIP on the immutable source

Reviewer **REV-R1gc2-2** returned **SHIP, no blocking findings**, on
`8a1348fb82a60caf9dda6409d8907628fb95e475` (orchestrator mail
20261121-115125-749-orchestrator-975e, 2026-11-21 11:51). The reviewer
accepted gross getNewDiplomas wiring, retained applyBandFlow degree outflow,
and the actual-Game positive/revert causal control; there is no new
save/thread delta. The source, provisional marker and all existing checks
and proofs are preserved. This update records the verdict only; no checks
are replayed.

The same mail reports MC2's publication gate stopped on one new Kept
paper-fixture premise, with no publication and main still 5790d0506
(0.9.178). MC2's builder is tracing the smallest proof. The final ensemble
and dry run remain held for the orchestrator's current-main/model ordering
GO after that trace, avoiding a duplicate ensemble if MC2 can ship through
records-only reuse. No extra long check or foreground wait is started,
and Jerus's accepted pace/design decisions remain settled.


## 9. Main 0.9.179 merged; final ensemble held for PW1

On the orchestrator's 12:08 go, merged main
`2b049188bcc8ba1b693beef9303409826936487e` (MC2, 0.9.179) at
`6368efd5118040b5904c3a8f23d059f8ed0894d0` in the same batch/R1g worktree. Only dials,
harnesses and map README indexes conflicted; all were regenerated.
All 17 R1g source files, seven saved proof files and the ship file match
their pre-merge SHA-256 manifest byte for byte; the d5eb review records
were preserved and the provisional marker remains. The incoming source
is Bank, BusinessDebtManager, BankFailureCheck and the published version.
There is no source overlap with the approved R1g changes.

MC2's reviewed lending/mortgage allowance inside the bank's band, including
after settlement, is inherited through the existing funding APIs. No
manual money/save/thread integration edits or save-format change were
needed. `./batch-check.sh --fast` exited 0: compile 55 seconds,
indexes regenerated, Stale zero firm findings. Evidence:
`target/r1g/main179-fast.txt`, `main179-fast.exit` and
`main179-preserve.json`; data paths used the main worktree's C:/ files.
No standalone Village/Reload or other check was replayed.

The current baseline is `docs/ensemble/0.9.179.md`. PW1 is the next model
mover and has its ensemble go on that baseline. R1g's final ensemble stays
held until PW1 publishes; then merge its new main and compare the newest
baseline on the orchestrator's explicit ordering go. Dry run, full pick,
playtest and other long JVMs remain held. No foreground wait is started.

## 10. Main 0.9.180 merged; the final box ensemble

On the orchestrator's 13:36 go, main d1d70f0aee8312bb86c888f8cf9831c71322754b
(PW1, 0.9.180) merged at bb34ad4b5e9a533ddc929a59458c8601d56a49bf. All
conflicts were generated indexes and were regenerated. No source conflict
or manual semantic money/save/thread integration edit was needed. A
normalized source-delta comparison preserves every approved R1g addition
and removal relative to the new main; AllChecks' Village1900Check insertion
is checked structurally alongside PW1's new harnesses. The existing proof
files, review records and ship file were preserved, including the marker.
Evidence is `target/r1g/main180-preserve.json` and `main180-verify.py`.

PW1's struck power book, class-specific tariff reads and fuel settlement
are inherited as published. R1g's saved pay window still restores before
Recipes.apply, and its captive reset and gross-diploma wiring remain in
place. Recipe/fit pricing reads the published class tariff without widening
the advice cache's lifetime or changing its pure reading. No new save
conversion or threading change is introduced by this integration.
`./batch-check.sh --fast` exited 0: compile 40 seconds, indexes regenerated,
Stale zero firm findings (`target/r1g/main180-fast.txt`, `.exit`). No
Village/Reload or other standalone long check was replayed.

The authorized final-model ensemble uses the unused label R1gc2-final180,
80 runs (sixteen seeds under AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK and HIGH),
eight at once on the gate box, against `docs/ensemble/0.9.180.md`. The label
was checked unused both locally and across the box's cbs folders; the
run/copy destinations resolve inside this worktree's target/ensemble.
The published helper retains gate/nightly priority. Local Java, javac and
Maven commands are guarded in this invocation, so an automatic box-down or
box-full fallback cannot start a laptop-heavy run while FS5 is pending.
The source is frozen and pushed before the snapshot; results and actual
snapshot provenance are recorded after completion and home scoring.

WG3's separate request for a future Game education-finance hook was routed
to the orchestrator, not added to this frozen model; its unmerged Education
API and policy work are outside this run. The marker stays in place. The
final baseline must be committed as docs/ensemble/R1g.md before asking for
dry-run go; no dry run, full pick or standalone playtest is authorized yet.

## 11. Final .180 box ensemble completed; F1 blocks shipping

The one authorized final-model ensemble ran on the gate box from
2026-11-21T17:46:14Z to 18:01:37Z, label `R1gc2-final180`, snapshot
`627174320f11063976b6f05abdf68cfe72e1adc9`, against
`docs/ensemble/0.9.180.md`. It completed all 80 runs, eight at once, with
none missing or short and all 64 before-switch twins identical. The
published helper copied every result home and scored it: exit **4**,
batch **FAIL**, hard criteria **8/10**. No laptop fallback occurred.
The recorded host is gatebox, OpenJDK 21.0.13 LTS, with the box main
worktree's earth-2.bin and world-1.bin paths. Box models took 14.9 minutes;
end-to-end run, copy and score took 15 minutes 23 seconds. Summed run time
is 117.9 minutes; median seconds are AUTO2 203.5, ZERO_SLACK 154.4,
ZERO_HOT 11.6, SHOCK 39.3 and HIGH 31.2.

All five **M1 PASS**, with zero failed and zero unread money-audit months.
**F1 FAIL** in AUTO2 and ZERO_SLACK, each 1/16 runs versus the baseline's
0/16. Both are seed 1: the first negative monthly GDP is at month 1135,
GDP -360.892347, population 11,379; the first negative annual GDP is at
month 1145. These findings precede ZERO_SLACK's policy switch at month
1800 and are identical in its AUTO2 twin. The money audit passes with
residual zero at both finding months. The other three F1 rows pass.
The saved findings establish the failing path; its cause has not been
traced, and no attribution, waiver or assertion change is made.

The score retains every informational reading: 12/25 criteria pass,
with one vacuous and one retired row excluded. AUTO2 A3, SHOCK K2,
HIGH R1/R2/R3 and all five T2 rows changed from PASS to FAIL; ZERO_HOT
H2 changed from PASS to VACUOUS. AUTO2 A2 remains FAIL. AUTO2 A1 is PASS
at 2.92% median annual inflation (12/16 in band); ZERO_SLACK S1 is PASS
with 1.39% median excess (16/16 within); SHOCK K1 is PASS at 16.68%.
T1 passes at 14/16 reaching 40,000, median month 1286 versus baseline
16/16, month 758. Final-population medians, alongside .180, are:

| policy | R1g final population | .180 final population |
|---|---:|---:|
| AUTO2 | 1,466,969 | 2,532,050 |
| ZERO_SLACK | 750,928 | 1,841,309 |
| ZERO_HOT | 2,000 | 6,740 |
| SHOCK | 10,520 | 36,560 |
| HIGH | 4,918 | 29,621 |

`docs/ensemble/R1g.md` preserves the official baseline's complete table,
snapshot provenance and summary JSON; only the heading/introduction's
version is changed to the publisher's {next} token. Full evidence is in
`target/ensemble/R1gc2-final180/`; invocation, exit, timing, local fallback
guard and compact finding evidence are in `target/r1g/final180-*`.
Read-only validation confirms 80 CSVs and timings, completion lengths,
all four finding rows, exact summary JSON, the source freeze, existing
proof hashes and provisional marker. Source remains unchanged from the
reviewed .180 integration. No Village/Reload replay, second ensemble,
new harness, standalone playtest or dry run has started.

Shipping is blocked on F1. Request the orchestrator's GO and slot for the
smallest seed-1 GDP component trace around month 1135 before any new run
or model change. Dry-run GO is not requested while the hard failures stand.

## 12. Saved-evidence GDP trace and one proposed bounded diagnostic

On the orchestrator's 14:12 instruction, inspected saved files and source
only. No JVM, model mend or new run was started. The orchestrator owns
R1g's move to Pending. The source, SHIP review, proofs, failed final
baseline and provisional marker remain frozen.

### What the saved run proves

AUTO2 seed 1 and ZERO_SLACK seed 1 have 1,799 identical rows in all 67
non-phase columns, months 2 through 1800 inclusive. Their phase labels
are `auto` and `pre`; the held-rate switch has not happened at either
finding. This is one shared failing path, not two independent policy
failures. The retained CSVs have 68 columns and no GDP components or
annual GDP column. Selected actual readings are:

| month | monthly GDP | trailing 12 monthly GDPs, summed from CSV | population |
|---|---:|---:|---:|
| 1133 | 2548.959930 | 28260.247870 | 11394 |
| 1134 | 1308.731190 | 27292.729880 | 11387 |
| 1135 | -360.892347 | 24690.692113 | 11379 |
| 1144 | -382.774105 | 112.829002 | 11318 |
| 1145 | -268.652449 | -2704.783377 | 11304 |

The first monthly flag is at 1135. The monthly readings from 1135 through
1145 are all negative; the annual flag at 1145 is consistent with their
trailing sum becoming negative. These are sums of serialized nine-digit
readings, not recovered full-precision annual values; their distance from
zero makes the two annual signs unambiguous. The annual flag does not
require a separate annual-calculation fault to explain it. Both saved
finding months have audit_ok 1 and audit_residual 0.

The saved state also shows stress before the first negative GDP: Retail's
coverage and delivered-share columns are zero at 1131 already, hunger is
about .998, and served power alternates between 1 and about .058-.070 in
this window. At 1135 power is 1; at 1145 it is .0584890373. These are
correlations and month-end readings, not GDP components or proof of which
flow went missing. Works remains on with 38 cumulative orders at both
finding months. No causal attribution follows from those counters.

The two retained seed-1 logs contain only 120-month progress lines and
DONE, with no GDP details or exception. `Ensemble.step()` calls the
actual `LongPlaytest.runPlayer(g, 1)`, then writes the CSV
(`tools/ensemble/Ensemble.java:228`). It silences both System.out and
LongPlaytest.out. `LongPlaytest.flag()` keeps the first detail in its
in-memory Finding (worstSize starts at negative infinity), but the driver
writes only new finding names and counts, not that detail. Thus the
formatted GDP breakdown at `LongPlaytest.java:1689` was not retained on
disk. `findings_new` counts flags in that month; `finding` contains names
newly seen that month. Filtering the latter gives the four first-name
rows across these twins. The ensemble omits the playtest's periodic
save/reload (`Ensemble.java:390`); the copied evidence has no checkpoint
at the failing month from which the missing flows can be reconstructed.

### Formula and month order

`EconomyManager.getMonthGdp()` returns NationalAccounts.getGdp directly
(line 2554). NationalAccounts.update at lines 482-698 strikes:

    GDP = Cgoods + Chousing + Iconstruction + Ivaluables
          + Ifood + Imaterials + Iluxuries + Iheld + G
          + exports - importsFood - importsMaterials - importsRaw

Cgoods sums household sales at Retail, Luxury Retail, Restaurants and
Refining; Chousing is Real Estate's household sales. G includes utility
payroll, gross care/safety/school costs, research, transit and public IP.
Each inventory term is (current units + written-off units - previous
units) times the price supplied for this strike. Food uses a volume
index at fixed world weights and a current local-price deflator;
materials use stock units and the local price; luxuries use landed cost;
HELD sums stock and pantry by its 25 goods at heldPrice. The construction
yard is excluded from that inventory term because construction work is
recognized at contract value. NationalAccounts updates its unit baselines
and appends this GDP to its rolling history; getAnnualGdp (line 1355)
sums its last twelve entries. Its existing relative cancellation guard
does not floor negative output. No guard or threshold is changed here.

The month counter advances first (Game.java:13769). At line 14016,
startOfMonthUpdate advances the world and exchange prices, reads the
previous ledger's recognized construction and fitting work, buys the
struck power book's fuel, strikes sector statements, and hands actual
stocks, write-offs, imports and exports to updateNationalAccounts
(14877-14950). SimulationEngine then advances sites, recognizes their
work for the next strike, moves people and updates the economy/services;
finalUpdateEconomy follows (14017-14018). CSV rows and the audit are read
after this sequence. Consequently an arbitrary end-of-month market price
or current production row cannot substitute for the inputs at the GDP
strike. The NationalAccounts component getters retain the actual struck
values and are the right first diagnostic.

One concrete candidate to measure is the inherited PW1 channel:
utilityImports is fuel bought abroad plus variable upkeep
(Game.java:14910), and EconomyManager adds it to raw imports (2341).
The utilities' explicit contribution to G is payroll (14921); other
final spending and inventories may embody the utility's intermediate
inputs. Static inspection establishes these entries, not the amounts or
whether output is omitted. Other candidates include negative inventory
investment and construction/import matching. The saved totals cannot
choose among them, identify a good/sector, or assign the fault to R1g,
MC2, PW1 or a latent path. Main's zero-F1 aggregate baseline is not a
component-level counterfactual for this 1900 path.

### One proposed run, awaiting GO

Use **one AUTO2 seed-1 JVM on gatebox**, from the retained immutable
`R1gc2-final180` classes and driver (snapshot 627174320), OpenJDK 21.0.13,
with the same box-main earth/world paths. No recompilation, model change,
second policy, main run or scorer is needed. Use the existing driver's
`-Densemble.months=1145` and `-Densemble.extra=...` support with a fresh
diagnostic output label `R1gc2-gdp1-1145` checked unused before launch.
Run from the original founding; retain the complete prefix for identity
checking, and extract diagnostic months **1122-1145 inclusive** (24 rows).
The hard end is month **1145**, sufficient for both findings and their
annual window. This is a targeted diagnostic, not a replacement ensemble.

The 29 extra scalar fields are: annual GDP; C goods/housing; construction
and valuables investment; total inventory investment and its food,
materials, luxuries and HELD parts; G; NX; food/material/raw imports and
exports; the measured food/material/luxury unit baselines and their
write-off quantities; utilityImports as passed to the accounts; utility
fuel paid abroad/locally and upkeep paid; city crude imports/exports; and
public IP imports. They use existing no-argument getters, except the
three retained EconomyManager scalar inputs read by the driver's existing
field adapter. Exact paths are prepared in
`target/r1g/gdp-proposed-extra.txt` (not executed). The normal 68 columns,
money audits and finding names remain present. Array-valued HELD units,
per-good prices and each sector's import decomposition are not supported
by that scalar adapter and are outside this smallest first trace.

Gate/nightly priority must be clear on the orchestrator's GO; no laptop
fallback, since FS5 owns the next laptop diagnostic. Own the single
foreground process to exit, with a **180-second wall limit** including
founding. Stop at 1145, an exception/stuck month, or that limit; preserve
output and report, with no retry, extension or second run. Compare all
original columns with saved AUTO2 seed 1 through 1145 before attributing
any component; a mismatch or missing/nonfinite extra value makes the
diagnostic inconclusive. If identical, reconcile the component identity
within CSV serialization precision, locate the term causing the
1133-1135 fall, and reconcile annual GDP at 1144/1145. Return those
readings for the next decision without a model mend, waiver or dry run.

Static evidence and extraction code are `target/r1g/gdp-static-evidence.py`,
`gdp-static-evidence.json` and `gdp-static-window.csv`. Read-only Python
comparison and source inspection were the only work beyond recording
these notes. This bounded run remains a proposal pending explicit GO.

## 13. Component trace prepared; execution slot held

The orchestrator adopted section 12's scope at 14:24, but held execution
while WG3's eight-JVM box ensemble is active. This turn prepared static
access verification and a CSV checker only. No JVM, compilation, scorer,
remote job, sleep or job-poll loop was started. R1g remains Pending under
the orchestrator's df12 records change; return needs Jerus. Completion
of WG3 does not itself release this trace: wait for the explicit message.

`target/r1g/gdp-verify-paths.py` walks all 29 accessor chains against source
bytes from frozen 627174320. It verifies Game's two root getters,
EconomyManager.getNationalAccounts, ServicesManager.getUtilitiesHandler,
each final no-argument double getter or declared private double field,
the pure-return NA getters and annual-history sum. The existing driver
first resolves no-argument methods, then fields through superclasses,
uses setAccessible(true), and accepts Number.doubleValue. This proves
declarations and adapter support statically; runtime access is still a
premise of the future run and a missing value makes it inconclusive.
The machine-readable paths, declarations, lines, bodies and phase labels
are in `target/r1g/gdp-field-catalog.json`.

**22 retained NA readings:** gdp_annual; c_goods, c_housing;
i_construction, i_valuables, i_stock, i_food, i_materials, i_luxuries,
i_held; g_services, nx; imports_food, imports_materials, imports_raw,
exports; measured_food_units, measured_material_units,
measured_luxury_units; written_off_food, written_off_materials,
written_off_luxuries. These are the actual stored GDP components,
inventory measurements/write-offs, or the sum of the NA history. The
measured-unit getters are the baseline after the accounting strike has
advanced it, rather than an independently sampled end-of-month stock.

**Seven supporting inputs read at month end:** utility_imports_in_na,
utility_fuel_imported, utility_fuel_local, utility_upkeep_paid,
city_crude_imports, city_crude_exports and public_ip_imports. The first
and the two crude inputs use the declared EconomyManager private fields;
the rest use no-argument getters. These are last-set/last-paid views
outside the retained NA component block, observed after the rest of the
month. Reset, load and currency-scaling paths can also write such state.
They may help interpret a component, but they cannot alone prove that a
flow was omitted or that the amount read is the strike's original input.
The checker labels these separately and makes no causal attribution.

`target/r1g/gdp-prefix-check.py` pins the original AUTO2-1.csv and approved
EXTRA spec by SHA-256. It requires exactly the existing 68 columns plus
the 29 extras in the driver's order and the complete 1144-row prefix,
months 2-1145. Every original field, including phase, flags and blank
values, must match the retained CSV exactly. Every extra must be present
and finite in every row. A failed premise returns INCONCLUSIVE before
any component identity or annual reconciliation is attempted.

Only with those premises met, the checker extracts 1122-1145, checks
inventory=sum of its four parts, NX=exports-imports and the full GDP
identity in each of those 24 rows, and compares annual GDP at 1144/1145
with the respective twelve original monthly readings. Precision bounds
come from the driver's %.9g serialization (half a ninth-significant-digit
unit per reading), double-addition error and GDP's existing relative
cancellation guard; this changes no model threshold. It reports the
1133-to-1134 and 1134-to-1135 component changes only after reconciliation.
Its positive status is MATCHED_COMPONENT_READINGS, never a ship verdict
or causal claim. The future process exit/log/time limit also must be
accepted before treating any checker output as a completed diagnostic.

Fifteen Python-only synthetic CSV controls passed: consistent readings;
original-column mutation; absent, blank, NaN and infinite extras;
duplicate month; truncated prefix; changed accessor spec; broken monthly,
inventory, NX and annual identities; and readings just inside/outside
the serialization bound. Failed prefix/finite premises were proved to
produce no identity results. These are checker controls, not a seed-1
model run. Results are `target/r1g/gdp-checker-controls.json`; fixture
filenames explicitly say synthetic. The checker SHA-256 is
`96a718c228900da956206ec082158a1909733a708eece32396868ae4a69ffe7f`;
the field catalog's is
`3c6dbef6b6cd470b1b6d882304042f71795f51bd9273b769603fa142ff1b7322`.

The retained reference SHA-256 is
`b50d0144f17257a7a8ee84f118577ec4a73bc1deda69382e820a5c46020f44e4`;
the approved 29-path spec SHA-256 is
`6a247dad2fd70cbf4a8c883e09ca2dad12207fe140baf6403f83785a4f9ff1da`.
The future output label remains R1gc2-gdp1-1145; no actual diagnostic
output has been created. Check it unused on the box when the slot is
released. Keep the original immutable classes/driver, data and JDK;
one AUTO2 seed-1 JVM, end 1145/exception/stuck/180 seconds total, no retry
or extension, no laptop fallback. All source, prior reviews/proofs,
baseline and provisional marker remain frozen. Await explicit slot GO.

## 14. One bounded component trace completed; construction term identified

The orchestrator released the box slot at 14:38 after WG3's ensemble
was terminal. Ran exactly the adopted diagnostic, label
`R1gc2-gdp1-1145`, using the original 627174320 classes and driver without
compiling or scoring. Preflight verified the retained source HEAD and
clean source/driver, original snapshot, dependency files, JDK release
21.0.13+9, original data paths and data mtimes before that snapshot,
unused label across the box, and output containment. No gate, nightly or
other JVM was active/announced; nonblocking heavy, ensemble and box locks
were acquired. Original bytecode trees were hashed before and after:
2,395 model classes and four driver classes unchanged. No fallback ran.

The single JVM, PID 1921652, started **2026-11-21T18:44:57.179404Z** and
exited **0** at **18:45:43.550164Z**, **46.370757 seconds** including
founding. The 180-second total timeout was not reached. Its terminal
line is DONE AUTO2 seed 1 months 1145 of 1145, with no exception or stuck
month. The supervisor waited for real exit, released its locks and
copied all diagnostic records home except its private tmp folder.
The local foreground session also exited 0. No owned job remains, and
the orchestrator was told the trace was terminal before analysis.

The pinned checker (SHA-256 96a718c228900da956206ec082158a1909733a708eece32396868ae4a69ffe7f)
returned **MATCHED_COMPONENT_READINGS**: all **1,144 rows**, months
**2-1145**, match **all 68 original columns exactly**; every one of the
29 extras is finite in every row. All 72 window identities (inventory,
NX and GDP in months 1122-1145) and both annual identities passed their
serialization/floating precision bounds. The maximum absolute window
identity residual is 0.0000137. This validates the recorded component
readings, not a shipping verdict or a model mend.

### The struck GDP identity, 1133-1135

| struck NA component | m1133 | m1134 | m1135 |
|---|---:|---:|---:|
| C goods | 91.7734056 | 95.6487074 | 93.8596883 |
| C housing | 1456.48665 | 1481.82657 | 1472.89221 |
| I construction | 3735.29957 | 3615.84480 | 0 |
| I inventories | 27.4921481 | -676.059792 | 44.8876654 |
| I valuables | 0 | 0 | 0 |
| G services | 2175.95783 | 2167.51867 | 2164.69979 |
| NX | -4938.04966 | -5376.04776 | -4137.23171 |
| GDP | 2548.95993 | 1308.73119 | -360.892347 |

The 1133-to-1134 GDP fall is 1240.22874: inventory contributes
-703.5519401, NX -437.99810, construction -119.45477, G -8.43916,
and the two consumption lines together +29.2152218.

The 1134-to-1135 GDP fall is 1669.623537. Its largest negative term is
**I construction -3615.84480**, disappearing entirely. Inventory improves
by +720.9474574 and NX by +1238.81605; goods consumption changes
-1.7890191, housing -8.93436 and G -2.81888. Construction remains exactly
zero in every captured month 1135-1145. Inventory's food, materials and
luxuries parts are zero at these three strike points; the displayed
inventory amounts are HELD. The first negative monthly GDP is therefore
not caused by a new negative inventory swing at that crossing.

At 1135 the struck trade components are imports food 77.9512665,
materials **3474.95812**, raw 808.965714 and exports 224.643397. Their net
is the recorded NX -4137.23171 within serialization precision. Material
imports continue while the recognized construction contribution is zero:
3861.81953 at 1145, whose total monthly GDP is -268.652449. The trace
locates the accounting component responsible for the crossing; it does
not establish why construction recognition stopped, what those imports
served, or whether that pairing is a missing flow, timing error, repair
cost, fitting/site distinction or an upstream/batch behavioral effect.
Those questions still require the production-path trace. No own-versus-
latent attribution, correction or waiver is asserted.

### Annual readings and limits

| month | actual NA annual GDP | sum of twelve original CSV GDPs | residual | precision bound |
|---|---:|---:|---:|---:|
| 1144 | 112.829003 | 112.8290019 | +0.0000011 | 0.000015050007 |
| 1145 | -2704.78338 | -2704.7833771 | -0.0000029 | 0.000015050005 |

Thus the annual finding follows the same monthly deficit window. The
22 retained NA readings above remain distinct from the seven supporting
end-state inputs. All seven supporting inputs are zero throughout
1122-1145. That gives no positive measurement supporting the earlier
utility-import hypothesis, and these end-state zeros alone cannot prove
that the strike saw no utility/trade flow. The cause is narrowed to the
construction-recognition/material-import channel, not finally assigned.

Evidence is `target/ensemble/R1gc2-gdp1-1145/`: full 97-column CSV,
log, terminal, original snapshot, command, JDK/data/bytecode provenance,
exact extra paths, checked.json and checked.window.csv. Invocation and
terminal validation are `target/r1g/gdp1-*`; the launcher created exactly
one JVM, with a kill-and-wait timeout and no retry. CSV SHA-256:
`e789995361092b8a915cde4e988314fd059b22e1e9caf51348543ff39d516785`.
Checker report SHA-256:
`fe321d9ce9df83ea7e5d94926be0167d16334e2957bd906aaeb972ce41105bc7`.
The failed full ensemble baseline is preserved unchanged. No model,
review, proof or ship-marker change, second policy/run, clamp, threshold,
dry run or ready action followed this trace. R1g stays Pending.

Recommended next step is static tracing of the actual construction
recognized/fitting values into Game.startOfMonthUpdate and the builders'
material-import ledger, including repair work, to explain the 1134/1135
split. No further JVM or source mend is authorized by this completed GO;
report the component evidence and await the orchestrator's next instruction.

## 15. Static construction/import ledger trace on frozen 627174320

On the 14:51 GO, traced production source and retained matched components
only. No JVM, compiler, scorer, monthly run or box access was started;
WG3's dry run owns the box. Source links below pin the diagnostic's
immutable commit, not newer main. R1g remains Pending and return to Queued
still needs Jerus. This is a notes-only update.

**What the observed zero means.** Game reads
`construction.getRecognisedThisMonth() + getFittingThisMonth()` before
banking, and passes that sum to NA as constructionWorkDone. Thus m1135's
Iconst=0 is that sum, not the builders' entire sales or work/cost account.
Recognised work includes a capped draw on the order book, signed material
escalation, and overtime; fitting receipts are nonnegative but escalation
refunds can be negative. Zero does not prove zero site points, no fitting
order, or an empty order book: signed cancellation remains possible.

**What the observed 3474.95812 means.** The NA getter named
importsMaterials receives **Construction.statement().imports**, the
builder's entire import money line. Sector.bookPurchase adds every imported
good and bookImportedService adds imported services. It is not a measurement
of imported Good.MATERIALS alone. Section 14's shorthand "material imports
continue" means this NA line; it cannot assign those costs to repairs,
sites, bitumen, equipment or foreign crews. The separate Game material
unit/bill counters do not supply this NA argument in current source.

### Source-linked flow table

| channel | add/order and consumption boundary | reset/strike boundary | GDP treatment and implication |
|---|---|---|---|
| Site order | [Construction.bill:271][r1g-bill] adds cash to unearnedRevenue and points to backlog, not earned revenue. Game books each owner's contract and delivers any free yard material at the order. | No whole-book monthly reset; work, cancellations and refunds consume the book. | The order's cash/budget capital spending is not itself NA investment. Contract value is earned as delivered. |
| Site work | [BuildingsStacks.advanceConstruction:489][r1g-stack] apportions materialsDue and revenueDue by applied/owed points, or takes the remainder at completion. [BuildingManager:5571][r1g-advance] sums them. | Both stack and manager clear their due accumulators at each advance; takeMaterialsDue/takeRevenueDue/takeContractsDue consume them once. | Physical site progress and the contract slice share an advance; no stack receives more points than it owes. No-work/empty-book/refund cases need their actual values to distinguish. |
| Site purchase and recognition | [SimulationEngine:101][r1g-sim] draws the due materials, settles foreign work, calls recogniseSiteWork, then settles material escalation and control events. | drawSiteMaterials first clears siteDrawBillable; [Construction.recogniseWork:385][r1g-recognise] clears recognised/escalation/overtime/salvage-cut counters, takes max(0,min(earned,unearned)), then adds the contract revenue. | Contract work plus signed escalation plus paid overtime becomes the next strike's Iconst. Local/imported material is an intermediate input embodied in that work, not a second final purchase. |
| Draw routing | [Game.drawMaterials:9706][r1g-draw] rounds units, takes yard only when yardFirst, then salvage, then Markets.draw for the balance. Maintenance uses yardFirst=true; sites use false because their yard share was delivered at order. | It adds materialsConsumed and the physical MATERIALS import counters; those counters are cleared after NA at 14969-70, and materialsConsumed at finalUpdateEconomy 15438. | Salvage already paid for is an input/cost without another current import; local and world purchases are recorded by the market. These Game counters are narrower than NA importsMaterials and their later values belong to another window. |
| Goods and services ledger | [Markets.draw:483][r1g-market] calls buyer.bookPurchase for each local/world fill. [Sector:2186][r1g-purchase] puts world cost in pending.imports and bought[good].abroad. bookImportedService at 2570 adds import money and a named otherInputs entry. | [Sector.strike:2674][r1g-strike] copies pending imports, per-good splits and named inputs into Statement. bank at 2744 calls beforeBank, then clears pending and input rows. Statement maps remain the struck window. | Construction's total includes physical materials, bitumen/equipment/fuel where bought, and imported service charges. Summing all otherInputs as imports would be wrong: some names are local services or stock paid earlier. |
| Repairs | [EconomyManager.chargeMaintenance:1175][r1g-maint] recomputes sector, city and bank bills/materials/points from the standing templates. [Game:8664][r1g-charge] draws all repair materials before the strike, pays the city's available share and records the unpaid part as owed. | chargeMaintenance clears and replaces its totals. receiveMaintenance resets repairsThisMonth on invocation and books bill minus unpaid city repairs; Game's nonpositive-bill early return bypasses that call. | [Construction.receiveMaintenance:505][r1g-repairs] explicitly excludes repairs from Iconst as intermediate upkeep of existing assets. Positive repair imports can therefore persist with Iconst=0. Amount, owner and purpose are absent from the saved components. |
| Crew priority | [Game:1686][r1g-fitwork] computes round(max(0,gross construction output minus maintenance points)), then gives fitting jobs priority over sites. Foreign crews have their own bounded offer/settlement at 8565/8586. | Points, due work and crew needs are refreshed during the simulation pass, after the current GDP strike. | Maintenance/fittings can leave no domestic site points; this is a source-permitted route, not a measured explanation of m1134. Month-end capacity getters would describe the next advance rather than prove the points available to the previous one. |
| Equipment fitting | [Game.fit:1619][r1g-fit] draws goods through the builder's ledger, pays goods plus work/tax, calls earnFitting and queues the fitting job. | earnFitting accumulates fittingThisMonth. NA reads it before [Construction.beforeBank:597][r1g-beforebank] clears it; Statement.otherParts already freezes "Equipment fitted". Working the job later consumes points without earning that capital twice. | The model books the fitting as capital at the order, once. Its later physical labour need not create another Iconst amount. A month-end getFittingThisMonth may already be a new window and is unsuitable for splitting the old NA sum. |
| Foreign contractor | Construction.payForeignCrews at 320 books FOREIGN_CREWS through bookImportedService after the site advance. | Each settlement replaces its points/bill, including zero for no contractor; the service amount banks with the same ledger. | An intermediate imported service embedded in site output. It is included in the NA line called materials imports, despite having no material units. |
| Road bitumen | [Game.drawBitumen:9605][r1g-bitumen] books Good.BITUMEN as the order is placed; [processBuildOrder:10705][r1g-order] includes it in the quoted contract, whose cash goes to bill/unearnedRevenue. | The imported cost enters the ledger immediately; contract recognition follows site delivery. | This is a source-permitted timing candidate: ordinary material is drawn with progress, bitumen is drawn at order. The NA inventory loop measures sector stock/pantry, not a separate site-material account. The retained data cannot say this seed's builder imported bitumen. |
| NA read | [Game:14877][r1g-na-read] reads recognised+fitting, then strikes/banks sectors, then [EconomyManager:2344][r1g-na-import] reads Construction.statement.imports and passes the saved work sum to NA. | NA.update at 482 stores Iconst and appends GDP; simulation later replaces live construction work for the next strike. Private investment/orders run at 15010 after the GDP measurement. | The read-before-bank ordering preserves fitting investment. No reset between these reads explains the zero by itself. Struck Statement.otherParts is the existing safe decomposition; current live construction getters would be one window later. |
| Inventory and government | [NationalAccounts:517][r1g-inventory] measures producer materials and all HELD stock/pantry changes, explicitly excluding the construction yard. [Game:14921][r1g-g] supplies G from service payroll/gross costs, research/transit and public IP. | City repair spending is separately set on the government budget block and counted by [NA.getTotalExpenses:1487][r1g-budget]. It is not added by that G expression. | Producer inventory is unsold output; a builder's intermediate repair bill is not new investment. A public-repair GDP coverage gap is a candidate because its budget expense is separate from G, not proof that any part of this seed's 3474.95812 is that flow. C/G and budget expense must not be conflated. |

The ordering is therefore: the month/policy prices the templates; current
repair bills/draws are added to the pending window; the prior simulation's
work and intervening fitting receipts are read; the ledger is struck and
banked (fitting clears); NA takes the held work sum and struck imports;
private orders follow; simulation advances sites and resets/earns the next
work sum, then finalUpdateEconomy clears its material-use counter. Neither
zero inventoryMaterials (the producer's stock change) nor zero later
utility/trade input copies tells which builder purchase made the import line.

### Comparison with inherited .180

Static byte/body comparison against d1d70f0aee8312bb86c888f8cf9831c71322754b
found seven whole files unchanged: Construction, EconomyManager,
NationalAccounts, SimulationEngine, Markets, BuildingManager and
BuildingsStacks. Twenty selected Game/Sector methods, including their
overloads, also match exactly: the table's main order/draw/repair/fitting/
recognition/strike/bank methods. Evidence:
`target/r1g/ledger-static-compare.py` and `ledger-static-compare.json`.
The JSON SHA-256 is
`c2eab129b57476ccf05d1943ccf7061e9ad850df0fec2ccfd996acb8b970bd84`.

R1g does change inputs to that inherited machinery. Its
[Recipes.dateHomes:369][r1g-date] reprices residential template cash/upkeep,
material units and construction points from the saved twelve-month pay
mean. Those current template figures feed new quotes, maintenance sums
and loose-site point calculations. The apprentice call changes available
trade skills; the default playtest founding changes from today to 1900;
the captive reset changes production after a good stops being captive.
R1g's Game diff adds the apprenticeship and save/load pay-window wiring,
not a construction-ledger reset or NA argument change. There is no static
merge adaptation in the traced methods. This does not establish a latent
fault: changed inputs and a different population/stock path are not a
counterfactual control and cannot justify a waiver.

### Missing evidence and one smallest additional capture proposal

The 22 struck NA readings prove the aggregate identity and crossing, but
not the builder's signed work components or import composition. The
seven end-state zeros still cannot prove absence of strike flows. Missing
are (a) the struck Building work recognised/Material escalation/Overtime/
Equipment fitted/Repairs billed parts, (b) the struck imported money by
good and by imported-service name, and (c) the point/queue/credit cause if
the work parts really are all zero. No current data establishes repair
crowding, a refund cancellation, a bitumen order, or omitted public upkeep
as the cause at 1135.

Propose **one read-only Statement sidecar capture**, not a model change or
a second policy/control town. A separately compiled diagnostic copy of
the driver would serialize, after each completed step at **1133, 1134 and
1135 only**, Construction.statement's imports, otherRevenue, **otherParts**,
**bought** (Good -> atHome/abroad) and **otherInputs**, plus the retained
struck getDrawnLocal/getDrawnImported units. These maps are already frozen
at strike; their direct serialization needs no reconstructed flow or new
Game field. Preserve the original cached model and four driver classes;
compile only the diagnostic driver into a separate target directory if
that future compile/run GO is explicitly granted. The existing scalar
EXTRA adapter cannot address map keys, so more dotted scalar paths alone
would not capture these categories.

Keep the identical original CSV plus existing 29 extras and pinned prefix
checker, one AUTO2 seed 1 from the same frozen model/JDK/data, ending at
1145 or exception/stuck/180 seconds total, with no retry or fallback.
End 1145 retains the already validated checker/annual premises; the new
observations themselves are only three sidecar rows. Proposed fresh label
R1gc2-ledger1-1145 would be checked unused on a future released box slot,
after WG3's dry run. Require the full original prefix first, then reconcile
the four named capital parts to NA Iconst, per-good abroad amounts plus
explicit imported-service names to the builder's import total, and the
revenue parts to its otherRevenue. Keep unclassified residuals explicit;
do not call all otherInputs imported. If any premise or identity fails,
return INCONCLUSIVE. Even a matched capture would classify the crossing's
ledger channel before assigning a missing-flow fault; it would not prove
the earlier point/credit cause. This is the sole proposed next execution,
not prepared or run under the present static-only GO.

[r1g-bill]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/sectors/Construction.java#L271
[r1g-stack]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingsStacks.java#L489
[r1g-advance]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L5571
[r1g-sim]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/SimulationEngine.java#L101
[r1g-recognise]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/sectors/Construction.java#L385
[r1g-draw]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L9706
[r1g-market]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Markets.java#L483
[r1g-purchase]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Sector.java#L2186
[r1g-strike]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Sector.java#L2674
[r1g-maint]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L1175
[r1g-charge]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L8664
[r1g-repairs]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/sectors/Construction.java#L505
[r1g-fitwork]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L1686
[r1g-fit]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L1619
[r1g-beforebank]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/sectors/Construction.java#L597
[r1g-bitumen]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L9605
[r1g-order]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L10705
[r1g-na-read]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L14877
[r1g-na-import]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L2344
[r1g-inventory]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/NationalAccounts.java#L517
[r1g-g]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L14921
[r1g-budget]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/NationalAccounts.java#L1487
[r1g-date]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Recipes.java#L369

## 16. One read-only struck-Statement sidecar completed

On the orchestrator's 15:11 execution GO, ran the sole adopted sidecar
diagnostic, label `R1gc2-ledger1-1145`. Before compiling or running, checked
the unused label and safe output paths, gate/nightly/JVM absence and
nonblocking priority locks, frozen 627 source, exact original model/driver
tree pins, JDK 21.0.13+9 executable pin and the prior diagnostic's data
hashes. No laptop fallback or queue wait occurred.

Compiled only an ignored diagnostic DRIVER COPY with `-proc:none`,
`-implicit:none`, an explicit empty sourcepath, and classpath comprising
the retained original model classes plus its dependencies. The original
2,395 model classes and four cached driver classes were not compiler
outputs; their full tree hashes matched the prior run before compilation,
after compilation and after execution. Exactly four diagnostic classes
were produced in their separate target directory, also unchanged by the
run. No Java source-tree file was edited.

The driver patch adds one post-completed-step observer and a helper that
directly serializes only the approved fields at 1133/1134/1135. Static
phase verification: Sector.strike copies otherParts and otherInputs and
deep-copies bought (copyOf at 2154); bank clears pending rather than these
Statement maps. Construction.beforeBank keeps drawn material units before
clearing input rows. Restore/currency-scaling paths can restore or rescale
retained records rather than create new flows; the driver has no reload,
and original-prefix/component identities remain mandatory. No live
recognized/fitting/capacity getter was substituted for a struck value.
The original seven supporting end-state scalar inputs remain noncausal
alone. Source/patch/phase proof and hashes are preserved in the output.

The allowed driver compile took **0.363848 seconds**. The **single model
JVM**, PID 2035330, started **2026-11-21T19:19:34.070708Z** and exited
**0** at **19:20:20.493386Z**, DONE at month 1145. Model elapsed was
46.422693 seconds; **total including compile was 46.832208 seconds**, below
the one 180-second total limit. No retry, extension or exception/stuck
month occurred. The owner waited for real exit, released locks, copied
the records home and closed its foreground session. The orchestrator was
notified of terminal/homecopy/checker completion so it could release WG3.

The pinned 96a718 prefix checker passed: all 1,144 rows/68 original columns
match exactly, all 29 extras finite, all original component/annual
identities reconciled. Only then did the sidecar checker accept exactly
three rows, the approved fields/finite numeric maps and retained-unit
values. All twelve new identities pass: four capital parts -> NA Iconst;
per-good abroad plus explicit imported services -> builder imports;
builder imports -> NA importsMaterials; all revenue parts -> otherRevenue.
Zero capital keys omitted by nameOtherRevenue's documented nonzero-only
rule count as zero, not as a missing map.

### Actual struck construction categories

| retained category | m1133 | m1134 | m1135 |
|---|---:|---:|---:|
| Building work recognised | 3440.355346431233 | 3418.1332965423267 | 0 |
| Material escalation | 294.94422186432763 | 197.71149960249033 | 0 |
| Overtime on rushed sites | 0 | 0 | 0 |
| Equipment fitted | 0 | 0 | 0 |
| Repairs billed | 947.067417937857 | 1058.868254413499 | 1064.4066729404876 |
| otherRevenue | 4682.366986233417 | 4674.713050558315 | 1064.4066729404876 |
| MATERIALS bought abroad = entire builder import bill | 4716.41992589238 | 5499.175293891376 | 3474.9581216832403 |
| MATERIALS bought locally, money and units | 0 | 0 | 0 |
| Retained MATERIALS units imported | 281 | 325 | 203 |
| Haulage, otherInputs; not classified as an import | 1.085495915264489 | 1.2853540531108545 | 0.8253459526555808 |

No other bought-good entry exists in any of the three snapshots. Explicit
imported-service names Foreign crews, Consumables and IP charges are
absent (zero); all three unclassified import residuals are exactly zero.
The Haulage entry is kept separate: the import identity closes without
counting it. All named revenue is capital work/escalation plus repairs,
with the entire m1135 revenue made up of repairs. The m1133/m1134 capital
sums reconcile to the recorded 3735.29957/3615.84480; the m1135 sum is
exactly zero. The largest capital-projection residual is 0.000003855183,
within its CSV precision bound; revenue-part residuals are at most
0.00000000000103.

This rules out the previously possible signed capital-part cancellation,
bitumen/imported-service composition, and fitting-order capital receipts
in these captured strikes. It establishes **zero capital parts with a
positive physical MATERIALS import bill and repair-only builder revenue
at 1135**. It does not prove which draw consumed those 203 units or why
earlier site points/book credit ceased earning capital. Import cost less
repair receipts is 2410.5514487427527; this is a subtraction of observed
amounts, not an identified omitted flow. The implied average paid per
imported unit is 17.1180203038583, not a captured market quote. The full
repair bill, its quote price, city unpaid repair share/arrears and point/
credit inputs were not captured. The original seven end-state zeros do
not fill those gaps. No own-versus-latent cause or waiver follows.

Evidence: `target/ensemble/R1gc2-ledger1-1145/` retains the original
97-column CSV, three-line statements.jsonl, both checker reports, log,
terminal, compiler command/log, exact driver source/patch, diagnostic
bytecode and original/custom/JDK/data provenance. Local preparation and
checkers are `target/r1g/ledger1-*`. Custom source SHA-256:
`f54584793e39ee93d501af1e547649032edc506c759f348a1f84ffbbcf47ffe5`;
patch SHA-256:
`953021bc8e431a843c82fa382da7972bd4ad4d15354fa4bb35a2174a35707950`;
raw sidecar SHA-256:
`f6293d0f90d87c1be4ff591bc2f7141a247e5e4a0b0ce47626cbf74eaec0a957`;
sidecar checker report SHA-256:
`6539c6981fa7ca37d05fc3482dbdb8430d4bc53cb5ff98028b707b86cddd461d`.

Smallest next decision: a static check of the repair bill's quote-price
binding versus the actual imported-material price, and the unpaid-city-
repair deduction, before choosing a short causing fixture. This needs no
further seed-length run. No additional execution or production mend was
started after this capture. Source, reviews, saved proofs, failed full
baseline and provisional marker remain frozen; F1 stays hard and R1g
Pending unchanged. No dry/ready/reclassification action was taken.

## 17. Static repair quote/payment trace; one short fixture proposed

The 15:27 GO authorizes this static trace only. No compiler, JVM, new
monthly run or box job was started. Source remains frozen 627174320;
the sidecar's measured distinction is preserved: m1135 capital parts zero,
repair receipts 1064.4066729404876, imported MATERIALS 3474.9581216832403
for 203 units. Their subtraction is not an omitted flow, and those 203
units are not automatically all allocated to repairs.

### Source-linked formulas and phase ownership

Let r_i be the template's annual repair share: its own
repairsPerYearAt(street utilisation), or RealEstate.MAINTENANCE_PER_YEAR
where it has none. For owner o, define L_o = sum of standing quantities
times r_i/12 times BuildingManager.nonMaterialCost(template), and M_o
the same weighted sum of template construction-material units. These are
definitions of the production sums, not a fixture implementation/oracle.

| item | actual formula/binding | owner and phase |
|---|---|---|
| Template labour/cash part | [BuildingManager.nonMaterialCost:165][r1g-repair-labour] uses template cash and construction points, replacing the founding labour share with today's builders' wage index. Upkeep is not read by this repair formula. | Templates/BuildingManager own the inputs. Template upkeep is a separate operating-cost input; it must not be conflated with repair bills. |
| Repair quantity | [EconomyManager.repaired/maintenanceBillFor:932][r1g-repair-formula] computes L_o and M_o, using own shares/traffic where declared. maintenanceMaterialsTotal/PointsTotal similarly sum all buildings' materials/points. | chargeMaintenance overwrites totals/maps before the draw. Material need is fractional; the later draw rounds units. |
| Repair quote price | [BuildingManager:269][r1g-repair-price] sets p_quote = Good.MATERIALS.worldImportPrice() * e. The getter at 5077 returns this stored materialsCost. | It is an anchored world-price constant times the struck conversion, **not the current local price or market import band**. Game passes this getter to chargeMaintenance at 8668. |
| Shared currency/world factor | [EconomyManager:286/338][r1g-repair-exchange] sets e = struckRate * worldLevel; struckRate follows spot via PASS_THROUGH. It supplies e to both Markets and BuildingManager. Game calls strikeExchangeRate before the repair charge. | The factor is synchronized at this phase. The remaining quote-vs-draw difference can come from the market band/freight/local mix, not an assumed separate spot-vs-smoothed FX binding. |
| Billed amount | B_o = (L_o + M_o * max(0,p_quote)) / (1 - buildersSalesRate). The rate is clamped to TaxPolicy.MAX_INCOME_TAX by the production accessor. B = sum sector bills + city-owned-category bills + bank-branch bill. | [chargeMaintenance:1175][r1g-maint] clears and sets the owner totals and each sector's maintenanceExpense. The bank's branch repairs are separate from its operating upkeep. |
| Actual material draw | [drawMaterials:9706][r1g-draw] requests round(M_total), takes yard, salvage at its prior cost basis, local maker stock, then the world. [Markets.draw:483][r1g-market] prices the domestic fills at localPrice and imported fills at importPrice. | The draw happens in full before the city payment decision. Its ledger costs/quantities are the actual purchases, not M_total*p_quote. Yard/salvage and rounding also prevent receipt/import subtraction from identifying a missing amount. |
| Actual import price | [GoodsMarket.importPrice:324][r1g-repair-import] is (worldImportNow + freightChange) * e. netImportPrice adds domestic haulage; ordinary draw records the boundary importPrice and the haulage is billed separately. | The market owns the current band/freight and actual fill split. landedPrice is a quote selector; draw follows actual held stock. These amounts were not captured as repair-specific prices at m1135. |
| Private owner and bank payment | Private maintenanceExpense enters Sector.strike's operating income and settles through bank(). The bank's own bankMaintenanceDue is paid by bank.payRunning at Game:14111, then cleared at 14117. | There is no private unpaid-share subtraction from the builder's repair receipts in this path. A private negative till/credit problem is handled elsewhere. The bank payment happens after the GDP strike. |
| City paid/unpaid split | [treasuryPays/discretionaryRoom:20038][r1g-repair-pay] pays P = min(B_city,room), with room=max(0,cash) if ceilingBound or existing arrears, otherwise max(0,cash+headroom). CITY_REPAIRS is discretionary/nonpurchase, so U=B_city-P accrues arrears. | [chargeBuildingMaintenance:8695-8743][r1g-charge] has already consumed material when it computes U, records cityMaintenancePaid=P and calls receiveMaintenance(B-U). A ceiling dial of zero gives no headroom; the fixture must assert actual room rather than assume ceilingBound when advances are zero. |
| Builder receipt/struck part | [receiveMaintenance:505][r1g-repairs] resets repairsThisMonth and books R=B-U if positive. Sector.strike freezes "Repairs billed" from it. | A nonpositive full bill returns before this call; no reset is implied on that early-return path. For the positive-bill channel under discussion, the receipt is full private+bank bills plus paid city share. |
| Later arrear payment | [payDownArrears:20075][r1g-repair-arrears] transfers cash directly to the payee's till, records arrearsPaidTo and reduces the claim. CITY_REPAIRS resolves to Construction. | It is a later cash transfer/arrears report, not another receiveMaintenance call or new capital output. Its budget line is separate from the original repair period. |
| GDP/budget read | [Game:14877/14921][r1g-na-read] takes recognized+fitting for Iconst and service costs for G; [EconomyManager:2344][r1g-na-import] supplies all Construction imports to NX. Repair receipts are neither a separate I nor C term. cityRepairs/arrearsPaid are stored on the [government budget block][r1g-budget], not added by that G expression. | The struck import is included regardless of the city's payment. The NA material-inventory term measures producer stock, not repair material already consumed. C/G, capital output, inventories and the cash budget remain distinct. |

The two candidate mechanisms are therefore different. A market import
band/freight above the anchored quote can make actual purchase cost exceed
the material part of the bill; a cheaper domestic fill can reverse the
direction. The same gross draw can also coexist with a smaller R when U
is positive. Either can produce a receipt/import disparity in this source.
Neither, by itself, establishes this seed's inputs or explains why all
capital parts became zero. Holding physical purchases and all other
struck inputs fixed, changing the quote or paid/unpaid receipt alone does
not add a GDP term: changing an actual import price changes NX, while
changing R alone does not. Adding the measured 2410.55145 difference to
GDP would therefore have no demonstrated accounting basis.

At m1135 we lack p_quote/e at the charge, full B and B_city, actual P/U,
repair-specific material need, yard/salvage contribution and the split of
the 203 units between possible uses. The retained avg paid 17.1180203 is
an aggregate observed ratio, not p_quote or a repair-only rate. The seven
supporting end-state zeros supply none of those missing phase inputs.
The source has no explicit public-repair addition to G, but the captured
data still does not measure that category's contribution to the seed's
negative GDP or authorize reclassification/a waiver.

### R1g versus inherited .180

The static comparison now covers the quote/exchange/market/payment chain:
BuildingManager, EconomyManager, GoodsMarket, Markets, TreasuryLine,
Construction and NationalAccounts match .180 whole-file; fifteen selected
Game/Sector/template method names and their overloads also match, including
treasuryPays, discretionaryRoom, owe/payDownArrears, chargeBuildingMaintenance,
strike/bank and repair-rate accessors. Evidence is
`target/r1g/repair-static-compare.py` and `.json`, the latter SHA-256
`01baeef6ecbab390289bfb0fef58eadd3c505eeea13f7c2294be4e22f6d52c3c`.
Prior comparison artifacts were not rewritten.

R1g.dateHomes changes residential template cash/upkeep/materials/points
from the saved pay mean before this machinery reads templates. Cash/points
therefore change L, materials change M, and points change repair crew
need; dated upkeep is a separate input, not a new maintenance-bill term.
R1g does not change the anchored p_quote, market draw prices, unpaid-city
deduction or GDP argument wiring. This is a mechanical comparison, not
proof of latent ownership from unchanged source or a changed population.

### One proposed short causing fixture: paid versus unpaid public repairs

Propose one ignored diagnostic fixture against frozen 627, with **two
scratch cities and one actual simulateMonths(1) press each**, one JVM on
a future released box slot, 30 seconds total/no retry. No seed-length
run, diagnostic or fixture code is prepared/executed by this static GO.

Use GameFiles.scratch, the existing explicit LongPlaytest.played(null)
founding and Game.run. Arrange the same minimal standing public-road
stock in both through BuildingManager.clearStacks and
addStack(pavedRoad,N,true), with the template selected from the catalogue.
Use N=ceil(12/pavedRoad.repairsPerYear()) so the model's annual share
produces whole material units rather than a deliberately fractional
rounding case; assert the actual maintenance quantity is positive and
rounds to itself. Zero the yard with setConstructionMaterials(0); assert
salvage and local MATERIALS stock zero, no site contracts/fitting jobs,
no private/bank premises, and no other goods/imported-service purchases.
Hold every sector with holdSector, holdBranches, and setOn(false,
g.getDecisions()) on the auto-builder. Those are existing fixture/model
APIs, not hooks or substitute formulas.

Set both advances ceilings to zero through setAdvancesCeilingMonths(0).
Use the existing package fixture API setCashForTest: Founding.WEALTHY_CASH
in the paid arm and zero in the unpaid arm. Assert the paid arm's actual
discretionaryRoom covers the city repair bill, the unpaid arm's room is
zero, and neither has pre-existing arrears. The real monthly path must
compute the quote, perform the draw, pay/refuse the city share, freeze the
statement and pass the actual G/I/NX arguments. No fixture copy of the
GDP formula or Game's government-cost expression is allowed.

Hard premises after the press: equal standing templates/rates, stored
quote/struck exchange binding and physical repair need across arms;
positive equal retained MATERIALS units/cost, no capital parts, no other
purchase route, full paid city share versus zero paid share, and exact
refused CITY_REPAIRS arrears in the unpaid arm. Reconcile struck repair
receipts with actual B-U in each arm and their difference with the city
share. Keep these measured payment semantics separate from the production
law assertion **NationalAccounts.getGdp() >= 0** in each arm, using the
existing zero bound. Read/compare the actual NA components; do not require
today's G=0 as a premise, so a legitimate future G-wiring mend can pass.
Equal physical repairs should not acquire different output merely from
which arm can pay. A failure establishes only this controlled channel;
the original seed's earlier point/credit cause remains separate. If the
premises fail or the law does not fail, report the fixture as not causing
the targeted red, without retuning, weakening or claiming root cause.

This is the sole proposed next execution. Source, reviews, prior proofs,
failed full baseline and marker remain frozen. R1g stays Pending; no
model mend, waiver, reclassification, dry or ready action is authorized.

[r1g-repair-labour]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L165
[r1g-repair-formula]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L932
[r1g-repair-price]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L269
[r1g-repair-exchange]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L286
[r1g-repair-import]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/GoodsMarket.java#L324
[r1g-repair-pay]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L20038
[r1g-repair-arrears]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L20075

## 18. Paid/unpaid public-road fixture prepared; no execution

Orchestrator's 2026-11-21 15:45 instruction
`20261121-154502-557-orchestrator-99ca` authorizes target-only preparation
from reviewed section 17/c06357452. The following is a proposed diagnostic,
not a compiled check, a measurement or a mend. No compiler, JVM, monthly
step, BOX access or BOX-job polling was performed. WG4eaa's slot is left
alone. R1g remains Pending; only Jerus can return it to Queued.

### Reviewable artifacts and pins

All new code is ignored under `target/r1g/repair-fixture-prep/`:

| Artifact | Purpose | SHA-256 |
| --- | --- | --- |
| `R1gPublicRepairProof.java` | Actual standalone driver, including the two proposed observers | `52dc30eea5fbe63d68fd9a7450a56a06249e3bf90c1ccd48f5eba39508d8894e` |
| `R1gPublicRepairProof.patch` | Unified source diff from /dev/null; no production patch | `cee4250d2f242f9d094392e45ebce67b6726a16a133b525b4084ef34403c7bc0` |
| `check_fixture.py` | Independent raw-data premises/identities/original-law check | `108fdd45c47ed21d1fd715cbd727dcb3f1099ebf09b59b40aa30fa0a4cde7349` |
| `prepare_manifest.py` | Static declarations/order checks and provenance manifest builder | `8069de7587e54f7dd0aa426b3232087661d1c032c942b6e225f015784ee1fd66` |
| `manifest.json` | Exact proposed classpath, source/resource hashes, observer paths and bounded scope | `201c0cbd90118941e5883e429ad56abe2e39a89ee995badc18b8b05ca6383654` |

The static preparation script passes, including Python syntax parsing,
ignored-path validation, comparison of 23 relevant production files with
frozen 627174320, and declaration checks for every dotted measurement
path. It does not compile or type-check Java. The files' byte hashes match
the manifest. No `fixture.json` exists and the proposed local output label
is unused; this is not an assertion about BOX occupancy or label state.

Expected model provenance is the same immutable
`627174320f11063976b6f05abdf68cfe72e1adc9` and retained ledger-trace pins:
2395 model classes, canonical tree SHA-256
`fcc468f43f860a2ec4d015eb76b5c907f65e30e925ab91e954d4ffde651c01c9`;
four original cached Ensemble driver classes, tree SHA-256
`e74f4e8aa6edd719b00594bbe436c9923469d4bf09625e1edb4c349f605374ff`.
Use JDK 21.0.13+9 at `/home/jerus/cbs/jdk-21.0.13+9/bin/java`, executable
SHA-256 `9cad3f2620d3ef2ed10b26f922af7e773d54fdaa05f615d6d6df014f3fec4155`.
Earth/world remain `/home/jerus/cbs/main/data/earth-2.bin` and `world-1.bin`,
SHA-256 respectively
`c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b` and
`fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1`.
These are retained expectations, not a fresh BOX verification.

The manifest pins the ten-entry original Maven classpath (Gson 2.10.1,
JavaFX 21 and Monocle 21.0.2), its string SHA-256
`fbc5cc674bb11cb14aad28499288e73f3b9c61dd8aa685c7fcc059f8fc10c033`,
23 frozen source files and all five JSON resources. Six dependency byte
hashes are available from identical local Maven coordinates. The four
Linux JavaFX classifier jars are absent locally: their bytes are **not
newly verified or historically byte-pinned here**. A released preflight
must acquire/record those four hashes, verify the other six against their
pins, and freeze all ten before/after compilation and execution. Any
disagreement with retained model/driver/JDK/data expectations stops before
compile/launch. This dependency gap and Java compilation remain explicit
review/preflight limits; no BOX access was used to conceal them.

### Original path and smallest proposed target observer

The existing `autoBuildProbeForTest` is before the monthly work
([Game:13733][r1g-fixture-autoprobe]); `settleProbeForTest` is after the
simulation/NA work ([Game:14024][r1g-fixture-settleprobe]). Neither exposes
the required repair prices, pending ledger and payment room at the charge.
The proposed target-only `ObservedGame extends Game` has exactly two
overrides, both existing nonfinal package methods in the same package:

| Observer | Exact boundary and original delegation | Evidence limit |
| --- | --- | --- |
| `drawMaterials(double,boolean)` | When armed around the one original monthly call, detach raw observations immediately before and after `super.drawMaterials`; record caller, requested units, yardFirst and all five actual Draw return values. The maintenance caller reaches this after EconomyManager.chargeMaintenance, before the city payment. Each armed/unarmed route delegates once and returns the original Draw. | The charge-phase bill/need belongs to this press. Before-city snapshots outside this boundary can contain initial/stale totals. No quote or flow is rebuilt. |
| `treasuryPays(TreasuryLine,double,String)` | Observe every treasury request/return, room, cash, arrears and budget context immediately before/after `super.treasuryPays`; add physical snapshot for CITY_REPAIRS. The original two-argument entry delegates to this virtual overload. Each route calls super once and returns the actual paid amount. | At return, EconomyManager.cityRepairsPaid still precedes its caller's setter. Use returnedPaid for P; read the struck Repairs billed part after completion for R. End-state cityPaid is supporting context. |

Observer failures are recorded as issues, never replaced with zero; the
original method still executes. Gson preserves nonfinite numbers for the
independent finite gate. No model hook/field, source-tree code, phase copy,
ledger mutation or override of run/nextMonth/simulateMonths is proposed.
The raw pending ledger is detached into JSON at observation, so later
reset/settlement cannot rewrite an earlier snapshot.

Static hold-path checks find no conflict that substitutes the repair path.
[holdSector:117][r1g-fixture-holds] marks investment/retirement holds;
holdBranches/planBank and Game.closeBranches guard opening/closing only.
[AutoBuilder.pass:1249][r1g-fixture-auto] returns inside that pass when off;
Game.nextMonth continues into startOfMonthUpdate and simulation. The
unmodified charge contains no hold/autobuilder guard and orders
chargeMaintenance -> drawMaterials -> treasuryPays -> receiveMaintenance.
The unmodified startOfMonthUpdate orders that charge before strikeSectors
and updateNationalAccounts, followed by private investment. Holding these
routes therefore does not bypass the actual draw/payment/strike under
test. It does not stop all trading, freight, policy, bank or budget work.

### Prescribed pair and exact measurements

Use two fresh GameFiles.scratch cities, both original
LongPlaytest.played(null) founding, standard/1900 (guarded), Game.run and
seedMarket(g,1). In each, select actual catalogue Paved Road, obtain its
positive repairsPerYear, set N=ceil(12/share), clearStacks/addStack(road,N,
true), and setConstructionMaterials(0). Hold every sector and branches,
turn the autobuilder off, set central-bank advance ceiling months to zero.
The sole designed arm difference is setCashForTest(Founding.WEALTHY_CASH)
versus setCashForTest(0). There is one original simulateMonths(1) call per
arm, not a manual sequence or a copied bill/GDP formula. Model-derived N
and actual catalogue inputs stay fixed; no tuning is permitted after a
failed premise or a noncausing result.

| Phase | Raw fields and hard checks |
| --- | --- |
| Setup and completed state | Month/completed count; whole standing catalogue quantities, cashCost/upkeep/materials/points/repair share; sites, fittings, legacy-instance count; yard, salvage/cost, construction backlog and unearned revenue. Require only Paved Road standing, no sites/fittings/legacy instances, no salvage/pre-existing arrears, one original step and successful owned-scratch cleanup. |
| Immediately before/after real maintenance draw | Quote/exchange; current local, boundary-import and import-plus-domestic-haulage prices; actual total materials/points need; full/city/bank bills and every private-sector bill; every sector's MATERIALS stock/pantry; builder pending ledger and raw local/import rows. Require one chargeBuildingMaintenance draw, yardFirst, positive whole need, no yard/salvage/local stock route, no earlier builder purchase, Btotal=Bcity>0, private/bank bills zero. Require actual Draw units=need, local units/cost zero, imported units=need, and import cost bound to observed boundary price within original MoneyAudit.CENT. |
| Every treasury payment, especially CITY_REPAIRS | Requested amount, payee/line, returned P; before/after cash/discretionaryRoom, arrears by line/total, refused/paid counters; CB headroom/ceiling/bound/treasury and bank advances/trailing revenue, total debt; bank cash/operating cost, fund withdrawal, cityPaid. Require one city request matching observed Bcity; paid arm actual room covers/full payment; unpaid actual room=0/P=0; immediate CITY_REPAIRS arrear increment equals refused amount. No assumption that a zero ceiling alone proves room zero. |
| Original struck Construction statement | Entire Statement DTO, all bought goods/local/abroad values, imports, otherInputs, purchasesBySupplier, otherRevenue/otherParts and retained local/imported drawn units. Require only MATERIALS bought abroad at the actual draw cost, all other input/supplier purchases zero, four capital parts zero, retained units matching draw; actual Repairs billed=observed Btotal-refused and entire otherRevenue equals that receipt. There is no haulage/imported-service whitelist. |
| Actual NA and original money audit | Read GDP, C goods/housing, I construction/inventories/valuables, G, NX, imports materials/food/raw, exports and inventory food/materials/luxuries/held. Require Iconst zero and NA builder imports equal actual draw as isolation/wiring premises. Preserve the production GDP>=0 law without a G=0 premise. Use the unchanged cached Ensemble.Adapter.auditResidual with original raw residual/relative/postAuditDrift; absent/nonfinite or failed original audit cannot establish a GDP cause. |
| Other cash-dependent outcomes | Before/after and every-payment budget context includes the original seven-slot treasuryMonthToSave vector (opening, closing, raised, repaid, surplus, recorded, noncash), all 40 governmentToSave budget slots, TreasuryJournal's last-month entries, DecisionLog.toState, treasury residual/unexplained, debt/CB/bank/fund fields. Preserve both full payment tapes and report every changed ending budget/decision field. Cash may change other policies, transfers or spending; equality is neither presumed nor used as a premise. |

Across arms the checker requires identical actual standing catalogue,
quote/exchange, need, owner bills, all observed material prices and actual
Draw fills/cost. It separately checks receipt difference against the
measured city share, and reports actual changes in every captured NA
component, including G/I/NX. It does **not** assert receipt difference is
GDP or subtract imports/receipts to invent final spending. Government
budget slots and the post-step live builder ledger are context at their
named phases, not substitutes for the retained strike.

All numbers must be finite. Any missing field, observer/setup issue,
phase/cardinality failure or isolation mismatch yields INCONCLUSIVE.
Only a completely matched pair with the original audit passing can be
labelled a GDP-law failure in this controlled fixture. A matched pair with
nonnegative GDP is NOT_CAUSING_GDP_LAW_FAILURE; do not alter stock, price,
holds or thresholds to manufacture a red. The checker records other
budget differences before testing isolation, so a rejected fixture still
retains rather than hides those changes.

Static feasibility remains conditional. clearStacks clears stacks only;
it does not erase legacy instances or repair bank asset history. Those
measured premises/original audit may reject the proposed city. Freight
still runs and may create a nonzero Haulage/supplier route; that is a hard
failure, not permission to skip freight or whitelist a service. Likewise
other cash receipts/payments may prevent the unpaid arm from having zero
room at the repair boundary. None of those premises is claimed true from
source inspection. If they fail, stop with the raw conflict and no
scenario adjustment. These controls cannot allocate the original seed's
203 units, prove its missing phase values or establish its earlier
point/credit cause, even if a current controlled GDP law fails.

### Proposed execution scope, lifecycle and stop rule; separate GO required

Propose **one BOX model JVM**, label `R1gc2-repair-pay-2press`, at most the
two specified fresh cities/one press each, **30 seconds total**, no retry,
extension or laptop fallback. There is no execution launcher/GO here.
After root review and slot release, first require gate/nightly priority
clear, reachable/free BOX, label entirely unused and safe output, and all
provenance checks above. Occupied/unreachable/mismatch means STOP before
any compiler/model launch. WG4eaa is not polled by this preparation.

Compile only this driver into a separate ignored diagnostic directory
using JDK21 javac, -proc:none/-implicit:none, an explicit empty sourcepath
and classpath of the original four cached driver classes, original 2395
model classes and pinned dependencies. Expected new output is only the
outer driver and ObservedGame class. Never compile/rewrite model or cached
driver classes. Recheck their complete hashes and dependencies before and
after. The manifest records the exact proposed argv/properties; execution
also requires an explicit driver GO property and the original model code
source. The driver atomically creates its guarded output label and refuses
any existing directory. Logs must be kept outside that label until then.

The 30-second total is the single proposed compile/run budget, with the
real owned foreground process terminated on exception/stuck/deadline and
no extra press or retry. Own it to real exit, record terminal/provenance,
homecopy the raw output/logs and run only the static checker after exit.
Timeout/partial/missing output is inconclusive. No timing success is
claimed before a separately authorized run.

Each scratch path is verified below this JVM's dedicated
java.io.tmpdir/citybuildersim-tmp/PID before cleanup. Original
LongPlaytest.cleanUp runs in finally per arm; remaining files become an
issue and original TempDirs shutdown cleanup is a backstop. No slot path
or old evidence directory is used. Source, reviews, earlier proofs and
captures, hard failed full baseline and provisional ship marker remain
frozen. This notes-only preparation does not authorize a model/harness
mend, reclassification, waiver, dry, ensemble or readiness change.

[r1g-fixture-autoprobe]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L13733
[r1g-fixture-settleprobe]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L14024
[r1g-fixture-holds]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BusinessInvestment.java#L117
[r1g-fixture-auto]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/AutoBuilder.java#L1249

## 19. One reviewed BOX fixture completed; hard yard premise failed

Root reviewed the complete prepared driver/checker and the actual
delegation/API/cleanup/hold path, then authorized exactly this attempt in
`20261121-162441-926-orchestrator-6c94` (2026-11-21 16:24). The reviewed
section 18 source/checker/manifest hashes were unchanged. No driver,
checker, model or harness edit was made to obtain a passing premise.

The prepared checker reports **INCONCLUSIVE: yard/salvage route: paid**,
exit 2. The raw paid and unpaid cities both started the press with yard=0
but had 36 units in the yard immediately before the real repair draw.
The requested repair need was 450; the draw consumed that yard and
imported 414. Salvage/cost remained zero. This violates the prescribed
zero-yard/all-import isolation, so the negative raw GDP readings below
do not establish a matched causing fixture. No premise was relaxed, no
service whitelisted and no second attempt/extra press was made.

### Read-only preflight and actual bounded execution

The first BOX work was read-only: unused safe job/output labels, immutable
627 HEAD/source status, all approved source/resource hashes (including the
five runtime JSON resources), retained snapshot metadata, 2395 model and
four cached Ensemble-driver class hashes, data hashes, original classpath
and native `~/cbs/gate.env`. Native java and javac both resolved to
`/home/jerus/cbs/jdk-21.0.13+9/bin/`; release/build and the pinned Java
executable matched. No version JVM, Maven, local javac/JVM or fallback was
used. No gate/nightly/JVM was announced, and existing heavy/ensemble/
nightly/box locks were acquired nonblocking/exclusive. Priority/occupancy
was rechecked before compilation and before the model; no wait/retry was
used. Root had released the slot after WG's named check was terminal.

All six dependency bytes matched their locally pinned identical Maven
coordinates. The following four hashes were **new current measurements
before compilation**, not claims of historical byte identity:

| Linux JavaFX 21 classifier jar | Newly measured SHA-256 |
| --- | --- |
| `javafx-controls-21-linux.jar` | `5174110cdbd70e5724796b894334fe50dc8d0aebe73a5a8654f693212b904bda` |
| `javafx-graphics-21-linux.jar` | `8309b4376cc246442e6ddf1f0bc5fff5091438a3b0eb059dd5095b9c1770f0ba` |
| `javafx-base-21-linux.jar` | `0d365b0457faa610cc6e3807514eff9a12e624319768814a6d6efa4a02103364` |
| `javafx-fxml-21-linux.jar` | `8dca88e4b17ca5c89ab8b2753a0c50ae0a94a0794cfabd9de2aa70c6be9922bc` |

All ten were pinned before compilation and identical after execution.
The read-only preflight is separate from the single 30-second
compile-plus-model allowance; it did not increase that allowance.

| Actual stage on BOX | Terminal evidence |
| --- | --- |
| Isolated javac, PID 2235384 | Started 20:31:57.716550Z; ended 20:31:58.080246Z; exit 0; 0.363835362 s. Explicit empty sourcepath, -proc:none/-implicit:none, original driver/model/dependencies on classpath; no model/cached-driver compilation. |
| One model JVM, PID 2235417 | Started 20:31:58.127074Z; ended 20:31:59.241682Z; exit 1; 1.114747139 s. Exactly the two prescribed fresh standard/played1900 cities, each completed one original step from month 1 to 2. |
| Shared compile/model bound | 1.525293977 s total by monotonic clock, limit 30; not timed out, no extension/retry. Model exit 1 reflects its original-law result, not a timeout or a compile failure. |
| Controller and cleanup | Terminal 20:31:59.241719Z; both children reaped, no owned process alive, no scratch leftovers. Locks closed, foreground controller/SSH exited, then homecopy completed. |

Only two fixture class files were produced and remained unchanged:
`R1gPublicRepairProof.class`, SHA-256
`a614dfc906c415885c7cb5a7483b17d4a0213bdeb1bf4ddcbe14b13ecbdfd036`,
and `$ObservedGame.class`, SHA-256
`5b25155e9a002e60276822b9e5bd1b7be5eda42e3a593712c43f5488c53ae539`.
The original 2395+4 classes, JDK java/javac/release, native profile, all
ten dependencies, data and approved source/source-resource/runtime-resource
hashes were identical after the JVM exit. The guarded driver output is
under frozen worktree `target/diagnostics/R1gc2-repair-pay-2press`; sources,
new classes, logs and provenance are in its separate
`target/r1g/R1gc2-repair-pay-2press-job` directory. No real save slots or
earlier diagnostic directories were used.

Homecopy is `target/diagnostics/R1gc2-repair-pay-2press-job/` locally,
including the byte-identical raw fixture, source/patch/checker/manifest,
commands, classes, logs and terminal/provenance. The copy exited normally;
tar reported file times about 0.28 s ahead of the laptop, so cross-host
wall-clock stamps are not an elapsed-time measure. The foreground order
and monotonic compile/model bound above are the timing evidence. The
prepared checker alone judged the fixture after homecopy. Subsequent
`inspect_raw.py` only extracts retained raw facts for this report; it does
not replace that verdict or perform another semantic check.

### Retained raw measurements after the failed premise

These are observations, not later premises promoted to a passing pair.
Both cities had the same 600 Paved Roads from the actual catalogue/rate;
both completed one step, setup/observer issues were empty, legacy instance
counts were zero and their scratch folders were removed. The checker's
finite/missing/setup gates ran before the reported yard failure. It did
not proceed to endorse the downstream isolation/identity assertions.

| Measured value at the named phase | Paid arm | Unpaid arm |
| --- | --- | --- |
| Yard at setup -> before repair -> after repair | 0 -> 36 -> 0 | 0 -> 36 -> 0 |
| Salvage units/cost before/after repair | 0/0 | 0/0 |
| Actual repair need; returned market units/local/imported | 450; 414/0/414 | 450; 414/0/414 |
| Quote; exchange at draw | 0.46700362180103044; 1.0023637413062465 | Same observed values |
| Current local / boundary import / import-with-domestic-haulage prices | 0.3261316444215281 / 0.5701588727476038 / 0.5701588727476038 | Same observed values |
| Actual draw local cost / import cost | 0 / 236.04577331750798 | Same observed values |
| Full / city / bank bill, every private owner bill | 411.6733347794672 / 411.6733347794672 / 0; all private bills 0 | Same observed bills |
| City payment-boundary room; P; refused/U; immediate city arrears increment | 64708.65960744605; 411.6733347794672; 0; 0 | 0; 0; 411.6733347794672; 411.6733347794672 |
| Struck Repairs billed / entire otherRevenue | 411.6733347794672 / 411.6733347794672 | 0 / 0 (empty otherParts) |
| Struck capital parts | None | None |
| Struck bought goods and imports; retained drawn local/import units | Only MATERIALS abroad 236.04577331750798; imports same; 0/414 | Same observed amounts |
| Struck otherInputs / purchasesBySupplier | Empty / empty | Empty / empty |
| Actual NA G / Iconst / Iinventories / Ivaluables | 0 / 0 / 0 / 0 | 0 / 0 / 0 / 0 |
| Actual NA NX / material imports / GDP | -236.04577331750798 / 236.04577331750798 / -236.04577331750798 | Same observed readings |
| Other captured NA components | C goods/housing, food/raw imports, exports, food/materials/luxuries/held inventory components all 0 | Same observed readings |
| Original GDP>=0 law | False | False |
| Original cached audit result; raw residual/relative; post-audit drift | 0; -5.684341886080802e-14 / 2.3731192278022524e-16; 0 | 0; 0 / 0; 0 |

The zero G reading is retained as a fact, never a premise. The original
GDP bound and money-audit rules were unchanged. All captured NA component
differences between these arms are zero, even though their repair receipts
and cash outcomes differ. This does not turn receipt difference into GDP
or authorize any spending reclassification. The origin/phase of the 36
units appearing between setup and the repair boundary is not measured by
these two observers, and is not attributed here. Their presence is enough
to retain the hard INCONCLUSIVE verdict. It supplies no allocation of the
original seed's 203 units or its earlier point/credit cause.

Other cash-dependent outcomes were observed rather than assumed equal.
Each full payment tape has 14 entries. CITY_REPAIRS is its only positive
request; all 13 other requested/paid amounts are zero in both arms. Ending
governmentToSave has three changed slots: taxBusiness 17.081484186755873
versus 0, taxSales 61.751000216920076 versus 0, cityRepairs
411.6733347794672 versus 0; the other 37 slots are zero in both arms.
CB trailing revenue is 78.83248440367595 versus 0. Paid cash/room moves
from 64708.65960744605 at the city request to 64296.98627266658 just after
payment and 64375.81875707026 at completion; unpaid remains 0. Ending
CITY_REPAIRS arrears/refused-this-month are 0 versus 411.6733347794672.
The paid treasury vector is [64708.65960744605,64375.81875707026,0,0,
-332.84085037579126,1,0], unpaid [0,0,0,0,0,1,0]. Treasury residual and
unexplained are 2.1032064978498966e-12 versus 0. Ending paid-arrears counter,
CB headroom/ceiling/treasury and bank advances, bank cash/operating cost and
debt are all 0 in both; ceilingBound is false in both. Fund withdrawal is
0.0025 in both; ending journal lists are empty and decision lists equal.
These reported tax/budget changes are not presumed absent to isolate the
city payment. Full before/after boundary vectors and tapes remain in the
raw capture and checker's report.

### Evidence and disposition

| Homecopied artifact | SHA-256 |
| --- | --- |
| `fixture.json` | `b040bdcb47be7f567b2d071287f403c5bd3c45767e210d10e755fefd4263d6dd` |
| `fixture-checked.json` | `d51d33da32e05d09a65432fbb8e5eeee57f9494ca589fb9813a6f506b0c79dea` |
| `terminal.json` | `e93f2b6dedee3b6bfaeed9575b1698bb5ce9770eacf73a3770ecc79a1bcadfd7` |
| `provenance.json` | `192d4fd2b66b43ff40da00847b79ab3cd683e85e8931a062eb68097c34939093` |
| `raw-facts.json` (read-only extraction, no replacement verdict) | `0a28dd25dde242c4942346e60e5380f00ef6b26ae363536f4b1f38e870970d08` |
| `model.log` | `06421a622b8a01c08268b749e871196caddb01dcd32c88a18ef4c065bb32f243` |

The foreground controller and local wrapper remain ignored under the prep
directory, hashes respectively
`295197daace481d0088b793e78dfccf47888f1022e7714d4a0e5500b8405b69c`
and `8c90106d760e8c013e487ebc619b11ac5e4a1a6804fc76e4a64455220897088e`.
Their complete stdout/exit/checker/homecopy records are retained there.
Root was mailed the real terminal/homecopy and release of ownership as
soon as it was observed. No JVM/controller remains owned. The attempt is
finished with its hard failed premise intact, not a ready or a waiver.
Source, reviews, prior proofs/captures, failed full baseline and provisional
marker remain frozen. No model/harness mend, rerun, reclassification, dry,
ensemble or readiness action followed. R1g remains Pending; return to
Queued remains Jerus's decision.

## 20. Static yard-origin and public-road repair disposition

Orchestrator accepted section 19/5732080ae exactly as INCONCLUSIVE and
authorized only saved/source analysis in
`20261121-163940-489-orchestrator-c24f`. The old driver, checker, captures,
verdict and terminal pins remain immutable. No alternate checker/rejudge,
compile, JVM, BOX access/poll or model/harness edit occurred in this step.

### The observed 0 -> 36 -> 0 has a logged source

The exact monthly call is
[Game.nextMonth:13760][r1g-yard-tick] ->
[updateConstructionCost:15485][r1g-yard-call] ->
[BuildingManager.finalUpdateBuildings:5069][r1g-yard-add]. Despite the
middle method's name, its sole action is that last call. The last method
prints production, executes
`constructionMaterials += getConstructionMaterialsProduction()`, then
prints the new total. The [production getter:7524][r1g-yard-production]
returns **BASE_MATERIALS=36**, without a stack, worker, cash, trade, recipe,
autobuilder or investment-hold condition. This is the works yard's own
free output, distinct from a Materials sector plant's market output.
The call is before month++ and before startOfMonthUpdate/repair pricing.
simulateMonths suppresses reports, but this mutation and these printlns
are unconditional.

The retained model.log contains exactly the emitted pairs at lines 3/4
and 44/45: "Construction Materials Produced: 36", then "Total Available:
36". The complete file hash remains the section 19 pin. These are emitted
at the actual production/addition, not reconstructed from an ending yard.
The logged pair, unique production call/binding and entry yard=0 identify
the monthly 36-unit addition in each prescribed arm. No new observation
is needed to explain this fixture's refill.

| Phase/binding in frozen 627 | Retained evidence and what it establishes | What it does not establish |
| --- | --- | --- |
| Fresh [GameFiles.scratch:107][r1g-yard-scratch], Game.run/initialize | Driver uses fresh dedicated scratch/no-legacy roots and original played1900 founding. [run:769 / initialize:801][r1g-yard-startup] seeds the yard with BASE_MATERIALS during initialization; the driver subsequently clearStacks/addStack and setConstructionMaterials(0). Captured setup yard is 0 in both arms. | The startup seed is real, but was overwritten before the observed press. It is not the later 36-unit flow. No month was loaded from an old save. |
| Original simulateMonths(1) -> nextMonth prelude | Exactly one completed 1->2 press per arm. The unmodified nextMonth calls updateConstructionCost once at 13760, still before month++ at 13769. | Turning off reports or holding investment does not turn off this yard production. |
| finalUpdateBuildings/getConstructionMaterialsProduction | Literal log pairs at 3/4 and 44/45 bracket the source addition of the constant 36. Game's captured yard getter [8760][r1g-yard-getter] delegates to this same BuildingManager field. | Template.setConstructionMaterials, DataSave.setConstructionMaterials and a sector's MATERIALS stock are different objects/fields; their similar names are not the observed yard binding. |
| Pre-repair draw | Observer fires after chargeMaintenance, before the original draw: yard36, salvage/cost0, requested materials450, only Paved Road standing, no local MATERIALS stock/pantry. | A zero stock snapshot elsewhere does not prove that no trade or stock flow happened between snapshots. It is not the reason for attributing the refill. |
| [Game.drawMaterials:9706][r1g-draw] / [takeFromYard:8773][r1g-yard-take] | The real call has yardFirst=true. Its rule takes min(round(450),36), then salvage and Markets.draw for the remainder; post-draw yard=0 and returned Draw records 414 imported/0 local units at the captured price. The entry/exit observations plus this exact method explain the 36 consumed in this event. | Draw's returned units are the market fill, not the total 450 after including yard. The original zero-yard/all-import assertion remains failed. This is not a reconstruction from a whole month's ending stock. |
| [deliverYardToSites:9824][r1g-yard-sites] and construction controls | deliverYardToSites takes from the same yard and credits sites; handleConstructionMaterials only calls takeFromYard. Neither produces yard stock. Fixture sites/fittings are empty and only the maintenance draw is retained. | These APIs do not justify adding a second 36 or treating a transfer as purchased/imported material. General site spending cannot be inferred absent from an unrelated ending stock. |
| Legacy/load/reset setters | BuildingManager's only field writes besides addition/take are setConstructionMaterials and resetBuildingManager's assignment of BASE_MATERIALS. Game's startup seed and load setter at 21354 bind to those; the monthly path here calls neither reset nor load. Fresh driver/source and setup observations identify the active path. | An initialized fresh scratch city is not a resumed legacy city. Legacy-instance counts0 reinforce that setup, but are not a substitute for tracing the load/reset calls. |
| Retirement/demolition | [retire:6775][r1g-yard-retire] can sell recovered material through [sellMaterialToTheBuilders:8180][r1g-yard-salvage] -> Construction.addSalvage. It may also write off sector stock and settle land/cash. It does not write BuildingManager.constructionMaterials. | The zero salvage snapshots alone would not prove no earlier salvage transfer. Its distinct destination, not endpoint subtraction, rules it out as the direct source of this yard addition. |
| Materials production/market trade | Sector stock/pantry and [Markets.draw:483][r1g-market] are separate inventory/purchase ledgers. The actual draw bought only 414 imported MATERIALS; local fill0 and other bought/input/supplier maps are empty in the retained strike. | A plant's trade, an import receipt or cash payment is not this unconditional yard output. No original-seed use/allocation follows from this fixture. |

The complete private-yard-field scan is retained in the new static
artifact: getter, monthly addition, setter, take/debit and reset are the
only code references that read/write that field. The scan distinguishes
the yard from catalogue material requirements and sector goods. No
ending-stock subtraction is used to assign an unlogged origin.

### Public Paved Road repair: ledger and NA disposition in this code

The actual catalogue/standing snapshot is 600 Paved Roads, material input
450, points4000, own annual repair share.02, cashCost139.77070475208345
and upkeep0 at the observed price level. [Paved Road:1426][r1g-yard-road]
is INFRASTRUCTURE, included among
[CITY_MAINTAINED:868][r1g-yard-owner]. The code's repair costing is a
quote for inputs at its own rates, not a measurement of delivered labour
or a replacement for the actual draw cost.

| Item/source | Ledger/phase treatment and retained facts | Direct NA disposition / limit |
| --- | --- | --- |
| Repair need and quote: [repaired/maintenanceBillFor:931][r1g-yard-bill] and [maintenanceMaterialsTotal/maintenancePointsTotal:1155][r1g-yard-need] | Template inputs weighted by the monthly repair shares determine requested 450 materials and 4000 points in both arms. The nonmaterial quote uses nonMaterialCost; materials use the stored quote0.46700362180103044, with the builder sales-tax gross-up. Full B=city B=411.6733347794672; bank/private bills0. | These are demand and quoted bill inputs. A positive point need or nonmaterial charge is not a saved quantity of repair work delivered, payroll paid, or final spending. |
| Physical material: original draw/yard/Markets | The real event takes 36 newly produced yard units and imports414 at boundary0.5701588727476038, import cost236.04577331750798, local cost0; salvage/cost0. Free yard addition/take has no purchase or cash booking. Actual purchase is recorded in the builder's pending bought/import ledger. | No invented cost is assigned to the free36. Imported cost reaches NX through the strike; no assumption that the quoted material part equals the actual import bill. |
| Private and bank owners: [chargeMaintenance:1175][r1g-yard-charge] | Private sectors receive maintenanceExpense, which enters Sector.strike's operating costs. Bank branch repair due is separate and later paid with bank running costs. Both are zero here. | Their usual treatment is an operating/intermediate expense in these books; it does not establish the classification or service value of city-owned road repairs. |
| City payment/refusal: [Game.chargeBuildingMaintenance:8664][r1g-charge] and [treasuryPays:20038][r1g-repair-pay] | Full material draw precedes payment. Paid arm has room64708.65960744605/P411.6733347794672/U0; unpaid room0/P0/U411.6733347794672 and that immediate CITY_REPAIRS arrear. Later payDownArrears transfers cash to the payee without a new repair order. | City P is recorded in [budget cityRepairs:963][r1g-yard-budget], later arrears payment separately. These fiscal cash lines are not the same variable as G and do not themselves measure service output. |
| Repair receipt: [receiveMaintenance:505][r1g-repairs], [nameOtherRevenue:959][r1g-yard-parts], [Sector.strike:2674][r1g-yard-strike] | The builder books R=B-U, resets/names repairsThisMonth, freezes otherRevenue/otherParts. R411.6733347794672 in paid, R0 in unpaid; four capital parts absent in each. Banking later settles taxes/cash and clears pending, while the Statement remains retained. | Construction documents repairs apart from new investment. R is payment-dependent and therefore cannot simply serve as an accrual/delivered-service measure independent of the P/U control. It has no direct C/G/I argument here. |
| Labour/repair service versus site work: [buildingOutputAfterRepairs:1686][r1g-yard-work] / [getMaintenancePoints:8498][r1g-yard-points] | Later site/fitting capacity is max(0, construction output minus maintenance point demand). chargeBuildingMaintenance does not gate its bill/draw on this capacity. Retained Construction.Statement payroll/electricity/water/own-maintenance are all0 in both arms; the end-of-press log reports Construction Capacity0. The 4000 is demanded points, not a recorded completed-repair quantity. | Payroll is an operating expense and not another term to add to expenditure GDP. The code has no distinct actual delivered-public-repair-service recognition record at this charge; an ending capacity or a price-component estimate cannot create that missing flow. |
| Current construction I: [Game:14877/14911][r1g-na-read] | Recognised construction work plus fitting income is read before strike. Repairs billed remains separate. | NationalAccounts.investmentConstruction receives that work only; actual Iconst0 in both arms. Adding gross R here would mix repair and new-capital definitions, not merely fill a measured gap. |
| Material inventory I: [EconomyManager:2269][r1g-yard-stock] and [NationalAccounts:516][r1g-yard-inventory] | Material volume is sectors' MATERIALS stock, with struck write-offs; the yard is deliberately excluded because contracts already embody their material. Source comments warn against counting it again. | Neither the free yard addition nor its repair draw is directly an invMaterials input. Actual inventory and valuables I components0 here. Crediting yard value and a contract/service total without a defined boundary risks duplicate value. |
| Government consumption G: [Game:14911][r1g-na-read] -> [NationalAccounts:644][r1g-yard-g] | The supplied governmentServices expression names utility payroll, healthcare, crime, schools, research, transit and public IP. It does not supply public-road repair B/P/U/R. Budget cityRepairs is updated separately. | Actual G0 in both arms is an observation under the failed premise, not an assertion or permission to invent a road-service spending term. The absence of that explicit argument is a code fact, not a completed accounting/causal verdict. |
| Imports/NX and GDP: [EconomyManager:2345][r1g-na-import] | All Construction.Statement.imports, not a repair-only category, feeds materialImports; here the retained bought map contains only MATERIALS abroad236.04577331750798. | NX=-236.04577331750798, GDP same; all other captured NA terms0 in each arm. The original GDP>=0 law is false and cached money audits0 in each. These remain raw observations after INCONCLUSIVE, not a second causing verdict. |
| Taxes/bank/fiscal consequences | Paid builder operatingIncome175.62756146195923, salesTax61.751000216920076 and profitTax17.081484186755873; unpaid operatingIncome-236.04577331750798 and those taxes0. All14 payment tapes and ending-budget differences remain as section19 records. | Cash conservation/audit0 and named tax receipts do not prove final-output coverage in GDP. Nor does subtracting an import bill from R identify missing final spending. |

The source's generic receiveMaintenance comment describes house repairs
as intermediate consumption. It is not a source-proven instruction to
classify every city-owned road service identically or to change a national
accounts rule here. Conversely, the missing explicit public-road G
argument does not justify adding R, B, wages or a receipt/import residual
without an independently defined and measured recognition/value boundary.
No proposed term or model change is made by this analysis.

### What is still controlled; what is still missing

The retained checkpoints prove equality across these two arms of road
catalogue/quantity, quote/exchange, requested materials/points, yard36 at
draw, salvage0, local-stock snapshots, actual market fills/cost, owner
bills and the complete captured NA components. The original production
and draw events explain the free36 equally in each arm; the single
designed control remains initial treasury cash. Full city P/U/refusal and
immediate arrears are observed, not imputed. The tax and budget differences
are actual unequal outcomes of the pair; section19's payment tapes and
all fiscal differences remain part of the evidence, not discarded to
claim equality. No other-purchase route appears in the retained builder
Statement. These facts survive the yard failure as observations; they do
not waive it or make the prescribed all-import pair pass.

For this short pair the 36's origin is no longer missing. What is not
recorded is a delivered repair-service quantity/value independent of
payment. Point demand4000 and quoted nonmaterial cost are not that flow;
the struck zero payroll is not evidence of positive work delivered. For
the original m1135 seed we additionally still lack purpose-tagged repair
draw need/yard/salvage/local/import price/cost, owner bill and P/U at the
charge, and a link from those events to the 203 imported units in its
aggregate strike. The short fixture cannot supply those historical
observations, even when its source paths are identical.

### Mechanical comparison and one next decision

New evidence `target/r1g/yard-static-trace.py` / `.json` compares inherited
.180 d1d70f0a against frozen627. The report SHA-256 is
`b275c13986cc5489b5488daf033fd5ff7365a5f2270573fe573535ace0784736`.
Nine complete files match (BuildingManager, EconomyManager,
NationalAccounts, SimulationEngine, Markets, GoodsMarket, GameFiles,
TempDirs, Construction). Twenty-one selected method names/overloads match,
including Game.run/initialize/newGame, simulateMonths/nextMonth,
updateConstructionCost/startOfMonthUpdate, drawing, retirement/demolition,
repair/site-capacity and Sector booking/strike/bank. The saved original
log/capture/checker/terminal hashes were verified while reading; earlier
comparison artifacts were not rewritten.

R1g's [Recipes.dateHomes:369][r1g-yard-homes] is new. It targets only the
Real Estate sector's capacity-bearing templates and adjusts their founding
cash/upkeep/materials/points from the approved pay-mean scale. Paved Road
is infrastructure, so it does not enter that guard. Yard BASE_MATERIALS,
road material450/points4000/repair.02 and the repair/draw/payment/NA
functions are unchanged. Changed residential recipes can still change
repair demand, site demand and the later played population; that is a
mechanical input relationship, not a counterfactual attribution from a
different population, an inherited-fault waiver or a reason to rerun .180.

**One recommendation: require the missing repair-recognition phase before
any GDP-mend proposal or new causing fixture.** The gap opens in
Game.chargeBuildingMaintenance after the real draw and before the
treasuryPays/receiveMaintenance split: those boundaries expose demand,
inputs and payment, not an actual service-completion event. Later
buildingOutputAfterRepairs subtracts demand from site capacity without
retaining completed repair work. Resolve statically which phase delivers
the public-road service and how its value is recognized independently of
P/U; any corresponding owner/purpose record must distinguish delivered
work from quoted cost/requested points and retain its material-use link
through the strike. This record does not select a new completion time or
valuation rule. Do not substitute payment-dependent R or an inventory
endpoint for that missing flow. Original-seed attribution would still
require its own missing purpose-tagged phase evidence. This is one
evidence/definition prerequisite, not an execution proposal, an instruction
to add a final-spending term or permission to revise/replay the failed
zero-yard fixture.

No compile/JVM/BOX work, model/harness/checker mend, retry, reclassification,
waiver, dry, ensemble or readiness action follows. Source/reviews/proofs,
failed full baseline, entire prescribed pair, fourteen-payment tapes per
arm, old verdict and marker are preserved. R1g stays Pending; Jerus alone
returns it to Queued.

[r1g-yard-tick]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L13760
[r1g-yard-call]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L15485
[r1g-yard-add]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L5069
[r1g-yard-production]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L7524
[r1g-yard-scratch]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/GameFiles.java#L107
[r1g-yard-startup]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L769
[r1g-yard-getter]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L8760
[r1g-yard-take]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L8773
[r1g-yard-sites]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L9824
[r1g-yard-retire]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L6775
[r1g-yard-salvage]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L8180
[r1g-yard-road]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/BuildingManager.java#L1426
[r1g-yard-owner]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L868
[r1g-yard-bill]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L931
[r1g-yard-need]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L1155
[r1g-yard-charge]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L1175
[r1g-yard-budget]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/NationalAccounts.java#L963
[r1g-yard-parts]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/sectors/Construction.java#L959
[r1g-yard-strike]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Sector.java#L2674
[r1g-yard-work]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L1686
[r1g-yard-points]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Game.java#L8498
[r1g-yard-stock]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/EconomyManager.java#L2269
[r1g-yard-inventory]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/NationalAccounts.java#L516
[r1g-yard-g]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/NationalAccounts.java#L644
[r1g-yard-homes]: https://github.com/2048Israel/CityBuilderSim/blob/627174320f11063976b6f05abdf68cfe72e1adc9/src/main/java/ham/citybuildersim/Recipes.java#L369

## 2026-11-24: the restart

R1gb (Claude, Opus), on the orchestrator's return of R1g to Queued (contract
section 6), from 8732c443d (cut from 0.9.180). Sections 12-20 above stay as
the record of the earlier attempts; this section is the batch from here.

### Main merged

- origin/main b17b75d42 (0.9.200) merged at cd7ef6360. No source file
  conflicted; the 25 generated indexes that did were taken from main and
  regenerated on the merged tree (b29f6f527). `--fast` green.
- The save format stays main's (65). R1g's one saved field, the 1900 home's
  walk (`DataSave.homePayIndices`, `homePayMonth`), needs no bump, as in
  section 6: Gson leaves a missing key alone, and no older save reads it
  wrongly. When R6e-rest (66) or RES3 (67) ships first, the format is
  theirs, merged.

### The negative GDP: the cause, the fixture, the mend

**What the kept trace says** (`target/ensemble/R1gc2-gdp1-1145`, and the
builders' struck statements in `R1gc2-ledger1-1145/statements.jsonl`,
0.9.180, AUTO2 seed 1): at month 1135 the treasury went from +11,629 to
-68,211 and building stopped (I construction 3,616 to 0). That month the
builders' statement holds 3,474.96 of MATERIALS bought abroad, 203 units at
that month's quote of about 17.1 (the 0.4658 literal times the inflated
world level, a helper's static trace), against revenue of 1,064.41, all
"Repairs billed". The builders book as revenue only what was paid
(`receiveMaintenance(bill - owed)`), and the broke treasury paid none of
the city's bill.

**The component.** The repair bill on the city's own buildings
(`EconomyManager.CITY_MAINTAINED`: electricity, water, infrastructure,
healthcare, education, safety, ports), struck by
`Game.chargeBuildingMaintenance()`, was in no final term. G (the
`updateNationalAccounts` call in `Game`) summed the services' payroll and
upkeep, research, transit and the public licence, and not the repairs.
`EconomyManager.updateNationalAccounts` takes off as imports every material
the builders bought abroad (`construction.statement().imports`), the
repairs' included. So every month the city's repair materials were
subtracted with nothing against them. While the 1900 city built, its
investment hid this; when the works stopped, GDP fell below zero.

**Reality.** A month's GDP below zero is an accounting fault, not an
economy. A government's repairs to its roads and buildings are part of its
final consumption: its non-market output is valued at the sum of its costs,
the repairs it buys included (SNA 2008, ch. 9, government final consumption
expenditure; ordinary maintenance and repairs are intermediate consumption
of the producer, here the city). They are recorded when the service is
delivered, paid or not (accrual).

**The fixture** (GdpCheck, the new section "the city's repairs to its own
roads are government consumption, not negative output (R1g)"): a city of
600 Paved Roads only, enough for one road's repair a month (450 units of
material against the yard's own `BASE_MATERIALS`, 36), with the yard empty
and every sector, branch, auto-builder and advance held. It is played once
paid (`WEALTHY_CASH`) and once owed (no cash, no advances).
- Without the mend (6ae15f7a3's Game.java reverted): G 0.00, NX -236.05
  and GDP -236.05 in both arms; 7 FAILED.
- With the mend: G 411.67, the bill, paid or owed; GDP 175.63, the
  builders' value added, the bill less what they bought abroad; green.
- The earlier attempts' section 19 fixture measured the same city and the
  same -236.05. Its stop on the yard's 36 free units was a premise of its
  own, not the model's: the yard is used first, the rest is bought abroad.

**The mend** (6ae15f7a3): G adds `economyManager.getCityMaintenanceBill()`,
the month's bill, struck by `chargeBuildingMaintenance()` above the call,
in the same strike as the imports it matches. It is one term.

**One check restated, not weakened.** GdpCheck's "G counts the schools' and
transit's cost" (0.7.49) read G as the services alone. With the repairs, G
read 97.01 against 57.38, because its town of services has roads, plants
and a school to repair. It now adds the month's bill by the model's own
getter, beside a fixture assertion that the bill is above 0: "G counts the
schools' and transit's cost, and the city's repairs (R1g)". Its town is
unchanged.

- ★ **Accrual, not cash.** G counts the bill, not what the treasury paid.
  Recommended, and taken: the material is drawn and imported in full
  either way (`chargeBuildingMaintenance` draws before payment), and real
  accounts record the service when delivered. Counting by cash would leave
  the fault in every broke month, which is when it struck.
- **Left as they are, and named.**
  - The bank's branch repairs: their imported material is in the imports
    and the repair is in no final term, as HL1's bank upkeep was before
    HL1 took it out of the imports. A few branches; not this failure.
  - A helper's static trace found that the repair draw counts every stack,
    while the bills count only buildings a sector owns, the city's
    categories and the branches. A building of another category with no
    owning sector would be drawn for and billed to nobody. Nothing shows
    that it matters; the ensemble's F1 decides.

**The trace on today's merged code** (the brief's step 2; label
`R1g-probe1`, on the gate box, 6ae15f7a3, `SEEDS=1 POLICIES="AUTO2
ZERO_SLACK" -Densemble.months=1200`, 2.1 min). On today's main the seed-1
1900 city no longer stalls: 1,164,169 people at month 1130, against 11,379
on 0.9.180. Monthly and annual GDP are positive in all 1,199 months of both
runs, the money audit passes in every month, and neither run has a finding.
The old failing month is therefore not reached on the merged code. The
fixture above is the proof of the mend; the full ensemble is the proof of
the city. The two-run subset scores nothing (`score.py` needs the policy
set) and is not committed.

### The harnesses on the merged code (one at a time on the laptop)

- **GdpCheck**: green (above).
- **Village1900Check** (R1g's own): three red on the merge, all in the
  stall's proof (sections 2-4, the village played with R1g's switches off
  beside today's twin). Identical figures with the GDP mend reverted, so
  main moved them, not the mend.
  - A helper's trace found two moves. The unfixed village's rent share
    fell from 0.769 to 0.717, so its pull rose from 0.390 to 0.418 and the
    city its conditions support went from 279 to 308 against 291 people.
    Today's twin shrank, from 749 to 588 people at month 60, and could
    staff 0.639 of a Fabrication Shop at month 30, against 0.935 before.
    No single main commit was pinned; the 0.9.177-0.9.200 bisect was not
    run.
  - The causes still show, and every one of their assertions passes: rent
    0.717 of take-home, diploma posts 141 for 34.7 workers, the shop held
    at the gate, no exporter, the money not the cause.
  - ★ Restated, three reds only (1c9081731), with no other label and no
    dial value moved:
    - **Section 2:** the rent binds. Before the pulls
      (`Migration.getLastDrawBeforePulls()`), its jobs and homes support
      732 for 291, over the new dial `UNPULLED` (2) times its people. The
      room it is left to grow is 0.059, against the twin's 0.225.
    - **Section 3:** the skills, at the gate month and by the diploma
      band. The village has 32.6 diploma workers for its shop's 248 posts;
      the twin has 150.3 for its 62. This is what the gate claim stood
      for; the twin's 0.639 is a lack of hands, which is main's.
    - **Section 4:** the draw against its cause. The village draws 0.276
      of the twin's late arrivals, under its rent pull's share, 0.460.
      The old "under `STALLED` (0.1)" held while the village sat on its
      target and drew none; on today's main a trickle closes the 17-person
      gap. Growth since founding was tried first and is no measure: cities
      start empty, and 291 against 588 is 0.49. `STALLED` stays section
      5's floor.
  - Green (exit 0).
- **CreditCrunchCheck**: one new red, "no month of the crunch leaves more
  than one household with no home (LongPlaytest's watch)", 9.10 at m151.
  Its fixture asserts all pass: FS9 made them cause what they read.
  - Its played crunch replays `LongPlaytest.played(null)`, the 1900 city
    since R1g. On the same tree with `-Dplaytest.founded=today` it is
    green: the owners put in 3,089 against 1,162, the bank is short in 51
    crunch months against 116, and nobody goes without a home.
  - ★ Named at first under HT1-H; REV-R1g (P1) moved it here, and it is
    restated in R1g: see "The review's mends" below. The known-reds row
    is dropped.
- **HousingDoorCheck**: exit 3, its one red the FS11 row as on 11-21. FS11
  is still queued.
- **ReloadCheck**: green (exit 0). The two RN2 rows are dropped: RN2 has
  shipped, its save lines with it.
- The five 11-21 CreditCrunchCheck fixture rows under FS9 are dropped:
  they pass on the merged code.
- **LongPlaytest** (the PB1 row, households' UnsettledFees) is not run
  here (38 minutes on the 1900 path); the dry run judges it.

### The review's mends (REV-R1g, 2026-11-25)

REV-R1g: money, saves and threading sound; SHIP after one P1. The three
items, mended on the laptop one harness at a time:

- **P1, CreditCrunchCheck section 3** (restated in R1g, the HT1-H row
  dropped). m151 first, as the review asked. It is the crunch's first
  month, right after the cut at m150. The cut left room for 12.2
  households as `Room.households()` counts the arrivals' mix (175 homes
  retired from 226.3). But in m151, with nothing built and no household
  arriving (371, falling to 358 by m171), 9.10 had no door. The doubling
  valve was at its ceiling (186 of 371 doubled up, half the households
  plus one), and no seeker was among them. So the 9.10 is the cut's. The
  bank, short of capital, wrote nothing for twenty months; the households
  fell only as people left (4.1 at m170). The owners' first block was
  mortgaged at m171 (trimmed from 4) and stood at m172, and from then 0.00
  to m270.
  - ★ The law restated, the premise unmoved: no month leaves more with no
    home than the crunch's first; every month over `WATCH` (one,
    LongPlaytest's watch) is one where the landlords asked and the bank
    was short; and once the owners' first homes stand none is over
    `WATCH`. Kept: the owners put money in (1,162), homes were built (306
    to 507). Today's city (-Dplaytest.founded=today, 11-24) held 0.00, so
    all three hold there.
  - Reality, from the source the review gave (The City of Calgary's First
    Boom and Bust, Alberta's Real Estate Heritage, archived 2010-12-08).
    In the 1905-1909 shortage some newcomers lived in tents; in the 1913
    bust building permits fell from over $20M (1912) to $8.5M, then
    "This in turn created high unemployment." The page does not say, as
    the review put it, that 1913's arrivals were homeless; the banner
    cites what it does say.
  - The 1900 run's log is kept at `target/r1g-cc-1900.log`, green.
    `Month` records the seekers with no door, so the log shows the 9.10's
    kind.
- **P2, Village1900Check**: section 5's draw is now "more arrivals at the
  end of the five years than the stalled village" (51.6 against 13.1;
  today's twin 47.4). The `STALLED` floor and its dial are gone: the
  unfixed village drew 0.276 of the twin's, over it. With
  `-Dr1g.home=off` the check fails, 13.1 against 13.1
  (`target/r1g-v-homeoff.log`). Section 4 and header item 4 are retitled
  "the draw": they prove `drawShare < pullShare`, not a stall.
- **P3, a note**: `Recipes.HOMES_1900`, `Recipes.HOME_WALK` and
  `PopulationManager.APPRENTICES_1900` are mutable statics, the R1g
  switches. They become per-Game before any in-JVM parallel run. Today
  the suite runs each harness in its own process (`AllChecks`'
  ProcessBuilder), so two Games with different switches share a JVM only
  inside Village1900Check. It plays one at a time, setting `HOMES_1900` and
  `APPRENTICES_1900` per play and putting them back; `HOME_WALK` is read
  from `-Dr1g.walk` alone.

These are harness mends: the model is 0103d9c4b's, so the ensemble
started on it stands.

That ensemble (0103d9c4b against 0.9.201) failed F1, below: the model
moved again after it, and the ensemble runs again on the final model.

### The second negative GDP: the contractor's points at his price (2026-11-25)

**The ensemble's red.** On 0103d9c4b F1 was hard red in all five
policies, seeds 4-7 alone: one month, month 15, GDP -51.0 (a probe of 20
months, `target/ensemble/R1g-gdp15c`, classes of b2af94e0a, AUTO2 seed 4).

**The component.** The foreign contractor's bill. In the probe's month 14
the pooled contractor (FC1, FC2) built 400 points for the builders at his
book's price, 0.42 a point, and billed 168.0; it is struck into month 15's
accounts as an imported service (`bookImportedService(FOREIGN_CREWS, ...)`),
taken off GDP with the materials. Against it, I construction rose only to
133.0: every site earns its points at its own rate, the value of its
contracts by the points it owes, about 0.29 a point on the village's
sites (its roads earn under 0.1). His crews spread over the sites as the
month is (`BuildingManager.plan()`), so his points were earned at the
sites' blended rate and billed at his. The 1900 village reaches it
because its own crews are a handful: he built most of the month. A city
with crews of its own never let his share be most of a month, and FC2's
own harnesses priced his book near the sites' rate.

**Reality.** Which price does his work cost the city? His: the builders
import it at the price his book holds (Game.settleForeignCrews()). Which
does the site earn? Before the mend, the site's blended rate. The payers
whose orders claimed his book paid his price in their quotes (REV-FC2:
the quote is the base times one plus his share), so the premium is
theirs, paid up front and held in their contracts. A general contractor
passing a subcontract through recognises revenue by the input method
(IFRS 15 B18-B19, ASC 606-10-55-20: progress measured by the costs put in
place against the costs expected): the month the dear subcontract work
goes in is the month its price is recognised. So the month he builds,
the sites earn his points at his price, out of the contracts that paid
for it, and no more over the site's life.

**The mend** (3b74e6cf8; `BuildingManager`, THE CONTRACTOR'S POINTS
EARNED AT HIS PRICE). After the sites and the demolitions have earned
their own: his points built, as Game.settleForeignCrews() strikes them
(past the city's own month and its overtime, never more than he
offered), billed at his price; less what his share of the month's points
earned at the sites' rates; the rest, the premium, is earned now out of
the home contracts of the sites that claim his book, by claim
(`BuildingsStacks.earnAhead()`). Those contracts hold it less after, so
it is earned sooner, never more: the value, not the units, the allowance
or the points, comes off. No claim on any site and it goes by the points
the month built on each. The contracts' value is the ceiling, and a
month that earned more than his bill gives nothing back. The dues carry
it to the payers (no units, no allowance: no escalation on it).

A first form (8dc2b8dc1) earned his share of each site's points at his
price site by site: the cheap sites' values ran out at his price, so the
sites earned 156.6 of 168.0, 78.8 of 88.1 and 62.4 of 88.1, and seeds 4-7
still went to -0.4 and -4.8 at months 15 and 16
(`target/ensemble/R1g-gdp15d`). It also turned two of ForeignDebtCheck's
fixture lines red. The premium form replaced it.

**The fixture.** ForeignCrewsCheck section 9 (header item 9), restated
for REV-R1g2's P2 below: a calm city whose landlords' Houses have
finished, the city's cheap work (`CHEAP`, 15 Home Care Service) ordered
at home under the threshold, then section 7's Coal Power Plant, its
86,923 points past the threshold handed him at its quote's 0.3987 a
point. He offers 4,139 points and the plant takes 1,450 a month at most
(its 60-month build, PW2), so his crews go to the cheap sites too. A
twin runs with `-Dr1g.hisprice=off` (`BuildingManager.HIS_PRICE`). Off,
the builders recognise less work than they pay him for (955.9 against
1,650.2: the fault). On, the sites earn 1,650.2 for his points, his
bill; the month's work is at least it; his bill and the points owed are
the twin's; what the sites earned past the twin's, 699.46, is what their
contracts hold less than the twin's, to 1e-9; and the cheap sites'
contracts fell alike in both: the premium came out of the plant's
contract, whose payer paid his price.

**The probe on the box** (`target/ensemble/R1g-gdp15e`, classes of
3b74e6cf8, AUTO2 seeds 4-7, 60 months): no negative month on any seed;
months 15 and 16 at +15.3 and +18.3 (seed 4; seeds 5-7 within 1.0), the
sites earn exactly his bill (168.0, 88.1, 88.1), I construction 199.3 at
month 15. M1 and F1 pass on the four runs.

**The harnesses, one at a time on the laptop, on 3b74e6cf8:**
ForeignCrewsCheck, ForeignChoiceCheck (each stack's dues sum to its
revenue), ConstructionTrapCheck, ConstructionControlCheck (the FC2b
handovers), ForeignCheck, PegAnchorCheck, GdpCheck, Village1900Check and
CreditCrunchCheck green; ForeignDebtCheck red only on main's known
serial-schedule line (`docs/notes/gate.txt`).

`BuildingManager.HIS_PRICE` is a mutable static, as P3's are: set only
inside ForeignCrewsCheck section 9, put back in a `finally`.

### REV-R1g2's mends (2026-11-25)

The money review of the mend (REV-R1g2): SHIP, no P1. Output is what was
paid over the contract's life (SNA 2008 6.112), no money is made or lost,
and the order and the saves are right. Two small ones:

- **P2, ForeignCrewsCheck section 9**: its `dearer()` raised his price
  with one point no quote paid, so the 7,434 it earned ahead was the
  landlords' money for their own work, not a premium anyone paid. It now
  causes the fault with an order quoted dear: the plant (the fixture
  above). `DEARER` and `dearer()` are gone. It adds "the twins owe the
  same points" and "the cheap sites' contracts fell alike".
- **P3, `BuildingsStacks.earnAhead()`**: what it may take is capped by
  `getHomeContractValue()`, which is the lesser of the home contracts'
  sum and the site's value, while it shared the take by that capped
  figure. Were the site's value below its contracts' sum, the dues would
  have summed past what the site earned. It now shares the take by what
  the contracts hold (39bcef763), so the dues sum to the take exactly. In
  every city played the two agree, so the model is the one reviewed.

**A bound found writing P2, put to the orchestrator.** The floor compares
his bill with what his share of the month earned, and that includes the
material allowance priced into the contracts (`bookContract`; the
escalation in `Game.settleSiteContracts()` adds only the material less
the allowance). So in a month the premium binds, his points earn his
bill with their material inside it, and the builders' value added on
them reads minus their material, not zero. The fix is to set the premium
at his bill less what his share earned net of its allowance. It would
bind in most months he builds, since his price is above the sites' own
price net of material. Mended below, as the orchestrator ordered.

### His points' material apart (the orchestrator, 2026-11-25; cc4c5b9d1)

**The order.** "Mend now; stop this ensemble." A point the contractor
builds alone should read value added near nothing: its output is his
bill plus its material, the material is intermediate, and his bill is
the import. So the premium is his bill less what his share of the month
earned net of its material allowance. The ensemble begun on 067b3f5c6
was stopped.

**The mend.** `BuildingsStacks.getLastHomeAllowance()`: the material
allowance in the month's home dues, never more than the home part
earned. `BuildingManager` sums it over the stacks as `homeAllowance`; a
demolition draws none. In `earnAtHisPrice()` his share's earnings at
the sites' rates are now `max(0, homeEarned - homeAllowance) x share`,
and the premium is his bill less that. His share's allowance,
`homeAllowance x share`, stays on top as `getForeignMaterial()`. So the
sites earn his points at his bill plus their material allowance. The
builders' value added on them is his margin, which is nil, because the
city passes his price through. It no longer reads minus their material.
The escalation in `Game.settleSiteContracts()` still settles what the
material cost past the allowance.

**Reality.** Under the input method (IFRS 15 B18-B19), a builder that
passes a subcontract through recognises the subcontract's cost and the
materials put in place as they go in. The month's output on that work is
the subcontract plus the material. The material is intermediate
consumption and the subcontract is the import, so the work adds nothing
but the builder's margin.

**The fixture.** ForeignCrewsCheck section 9 adds two checks. "His
points drew material" is the premise. "Value added is his margin, nil,
not minus their material" asserts that the month's recognised work is at
least his bill plus that material. On cc4c5b9d1: billed 1,650.17; the
sites earned 1,650.17 for his points, plus 189.94 of material; the
month's work was 1,845.34 (955.94 with the switch off); premium 889.40.
Under the old form (`onSites = homeEarned x share`) the work was
1,655.4, under the 1,840.1 bound, so the check went red (FAIL), as the
order asked. The rest of section 9 holds unchanged.

**CreditCrunchCheck, moved with the played city (seed 13, 270 months).**
- *The cut fixture.* Retiring homes by their dwellings left the placement
  room at 6.4, under `MARGIN` 12: on the moved city one retire freed more
  room than a home's dwellings. The cut now steps by the room the last
  retire freed, and the bound "less than one home's room more" is that
  room (`lastHomeRoom`). On this run: 240.9 to 12.6, the last home's
  room 1.24.
- *Section 4's order.* At `TO` the played city's policy rate is 0.2631
  (the autopilot's inflation rule on 18% inflation; see REV-R1g3 below), its insured
  rate 0.2707, and the lender tests at 0.2381. It passed 1 of the 6,
  which the till buys outright, so the door never asked the bank and the
  capital trim the section proves never happened. On the reviewed form
  it tested at 0.1102 and passed 2. The section is about the capital
  trim, so the lender's side is now caused: the insured rate is held at
  `ORDER_RATE` .08 for the order (tested .0474). The lender passes 3,
  and the door says "Built 1 Low-Rise Apartments (trimmed from 6 - the
  bank is short of capital for a mortgage, so what its own funds buy
  outright)". The premise is now asserted as "the lender's test passes
  more than the till buys outright" (passed > 1). A 26% policy rate at
  month 270 of one played city is a move of the city, not of the door;
  the ensemble judges the rates.
- *Section 3's banner* now quotes the new run: 15.1 households with no
  home at m151 (178 of 355 doubled up), the first block mortgaged at
  m189 and standing at m190. Its law holds.

**The harnesses, one at a time on the laptop, on cc4c5b9d1:**
ForeignCrewsCheck, CreditCrunchCheck, ForeignChoiceCheck,
ConstructionTrapCheck, ConstructionControlCheck, ForeignCheck,
PegAnchorCheck, GdpCheck and Village1900Check are green. ForeignDebtCheck
is red only on main's known serial-schedule line. `--fast` is green.

### REV-R1g3's mends (2026-11-25)

The narrow review of the material hunk (REV-R1g3): SHIP, no P1, and the
go for the fresh ensemble. Two P2s, mended beside it:

- **ForeignCrewsCheck section 9's upper side** (6c6535e25). "Work >= bill
  + material" holds even with the material counted twice. Now the
  month's work past his bill and his points' material is also asserted
  to be at most the city's own share of the twin's work (one less his
  share of the points, times the twin's work less its escalation) plus
  the month's escalation. On HEAD it holds at equality: 5.2314 against
  4.3642 + 0.8672, since this fixture hands nothing abroad. To test the
  check, `onSites` was mutated for one run to take off the allowance
  twice. The line went red at 200.58 against 4.67 - 10.22 (that run's
  log: `target/r1g-fc-twice.log`), and the source was put back.
- **CreditCrunchCheck's held rate: reality.** A helper traced the m270
  rate (logs `target/r1g-rate-on.log`, `target/r1g-rate-off.log`).
  - *The rate.* The played inflation at m270 is 0.1802. The currency has
    floated since about m130, with the aim at inflation and the
    autopilot on, so the rate is `DebtManager.advisedPolicyRate()`, the
    autopilot's Taylor rule: 0.03 + 1.5 x (0.1754 - 0.02) = 0.2631 on
    m269's inflation, and the insured rate is 0.76 points over it. The
    note above first called it the currency's rate, which was wrong. The
    rate is real for that inflation (US 1981: fed funds about 19% on CPI
    about 11%), so it is not a defect, and the held .08 stays an honest
    fixture for the section's subject, the capital trim. The check now
    prints the played inflation beside the expected (0.1802 and 0.0326),
    and `ORDER_RATE`'s sentence says this.
  - *The path.* Over m150-270 the rate swings. It is past 0.15 in 13
    months (first m160-169, peaking at 0.2318 at m163, then again from
    m268), and at 0 in 32 months, while inflation is negative in 32. The
    m160 spell is rent, after the check's own cut of homes at m150. The
    m268-270 spell is groceries up 40% since m257 (the shelf at its cap
    of 1.5 times its floor price, 91-97% of baskets delivered), plus
    luxury up 44% in one step at m266.
  - *R1g or main.* With `-Dr1g.hisprice=off` the paths part from m132,
    the m160 spell stays (a peak of 0.2241), and m270 reads 0.1277 on
    6.8% inflation. R1g's switch moves when the swings come, not whether
    they come. The rule, the shelf's pricing (`Retail.repriceShelf`) and
    the index (`PriceIndex`) are main's, and none of them is in R1g's
    diff.
  - *Seen in passing, main's.* The index held luxury at 1.4262 for over
    100 months while no counter sold any, as the counter's own price
    drifted to 2.04. It then caught up in one step, worth up to 6.6 of
    the 18 points of inflation at its 0.15 weight cap. The rule answers
    that, and a grocery shortage, as if they were demand. That is put to
    the orchestrator, not mended here.

### The fresh ensemble on the material form (2026-11-25; a3fc9ab0c)

On the gate box: label R1g, classes of a3fc9ab0c (cc4c5b9d1's model;
6c6535e25 and a10e8d86d change only harnesses and notes), 80 of 80 runs
in 29.0 minutes, scored against `docs/ensemble/0.9.202.md`, which is
main's today-founded city. Its baseline is now `docs/ensemble/R1g.md`
(the `{next}` token in its heading and introduction).

**The hard criteria pass 10/10.** M1 is 0 months in every policy. F1 is
0/16 in every policy: the seed-1 negative GDP that failed AUTO2 and
ZERO_SLACK on the .180 run (section 11) is gone. The probe on seeds 4-7
(`target/ensemble/R1g-gdp15f`, AUTO2, 60 months) shows no negative month.
The sites earn exactly his bill in every month he builds, with his
points' material on top (seed 4: 400 points at m14-18, billed 168.0 then
88.1, material 16.3 to 26.4; GDP 25.8 to 100.1).

**Against the .180 R1g run (section 11), what moved.** Every final
population rose: AUTO2 1,466,969 to 6,262,849; ZERO_SLACK 750,928 to
4,317,140; ZERO_HOT 2,000 to 13,344; SHOCK 10,520 to 367,962; HIGH 4,918
to 104,940. T1 went from 14/16 at m1,286 to 16/16 at m576. AUTO2 A3,
SHOCK K2, HIGH R1, R2 and R3 went from FAIL to PASS, and ZERO_HOT H2 from
VACUOUS to PASS (13/16 below). This is the 1900 city growing, the
batch's "Done when". Much of the rise is main's own: between the two
runs main moved from 0.9.180 to 0.9.202, and its own AUTO2 final rose
from 2,532,050 to 9,174,482 (3.6 times). The 1900 city's share of
main's rose from 58% to 68% (AUTO2). The batch's own part is the
restart's mends (2026-11-24 on): the negative GDP's two causes, mended
so that F1 holds, and the contractor's work earned at his price with
its material on top. The ensemble does not separate the two parts, and
no run of R1g on 0.9.202 without those mends was made.

**Against main's today-founded city, why the falls are real.** Every T2
reads FAIL, because main's baseline is a city founded today and this
branch founds in 1900 by default (`LongPlaytest.FOUNDED_1900`). The
finals are 68% of main's (AUTO2), 59% (ZERO_SLACK), 25% (ZERO_HOT), 24%
(SHOCK) and 15% (HIGH). T1 is reached at m576 against main's m325. A
village of forty-odd houses with a handful of crews, on the 1900 world's
prices and population, grows later than a city founded with today's.
The stress policies hold rates at 0% or 15%, or shock the world, and
they cost a small young town more than a grown one. That is the gap
T2's 75% bound reads, and no row says the town is broken.

**A1 (info) moved from PASS to FAIL.** Inflation m240-end is a 2.96%
median, and 10/16 seeds are in the 1-3% band, against 12/16 on the .180
run and 16/16 on main (median 2.65%). The six out of band run warm (the
highest 3.56%). It is not traced across the ensemble. In the one played
city traced (CreditCrunchCheck's seed 13, REV-R1g3 above), the warm
months were supply-side: the grocery shelf sat at its cap, and main's
held luxury index caught up in one step. A2 stays FAIL as on main (13
months, the same median as main).

### The first dry run's reds (2026-11-25; cbs-gate-R1g-EyG2IaHs)

The dry run on 09f7dd4db exited 1 with six reds not named in the ship file.
Each one, and what was done about it:

- **KeptCheck and GroundSkipCheck.** Since R1g, `probeCity()` founds the
  1900 city through `played()`. The subject of these two checks is the city
  a player founds the default way today, so they now play
  `LongPlaytest.probeTodayCity()` (agreed with the orchestrator).
  GroundSkipCheck now reads only LP-G2's known line.
- **KeptCheck on today's city** still read two reds, both caused by R1g.
  - The cause: under his price, today's city's Industry is assessed at
    about a seventieth of main's. Its property-tax bill is $45k a month
    against $3.3M at month 1,444, while the city is larger (10,811 people
    against 9,919 at month 605).
  - `-Dr1g.hisprice=off` clears both reds, and main is clean.
  - Section 51, rungs 0 and 5, "no month in twelve moved the figures".
    The swing was three of Industry's $1,000 steps, and that cannot move
    the list's KEPT, which is printed in $100k steps.
    Mend ★: the list's view now swings `listMover()`, the business largest
    by assessed value (Real Estate here), and `businessMoving(..., list)`
    sizes its swing on the larger of the list's KEPT step and the
    business's own. The two business views still swing the plain business
    (`sectorKinds`, unchanged). Each view puts its swing back before the
    next. On the PC (run 7, `target/r1g-kept-run7.txt`): Real Estate swung
    between 1.5% and 10% a year, and all of section 51 passed.
    When it stands still, the line now says why (`stood()`): the KEPT
    watched, last month's and this, and how many cards moved.
  - Section 63 ran out of memory after "all of the issuer's matches can be
    reached". The live set at that point was 2.14 GB of JavaFX nodes:
    175,513 Labels, 70,631 `Kept$Row` and 21,076 `FundScreen$Found`
    (main had 1,326 MiB live).
    Mend: the issuer pressed through is now the one with the fewest matches
    that still fill one page and leave a partial one. Run 7 searched
    Restaurants, 170 matches (5 pages of 40), and all of section 63 ran.
    The full check's live heap was 1,407 MiB of 2,048 (the bound is 1,536).
- **KeptCheck's reds left on today's city** (run 7, on the PC, 4 FAILED).
  The fourth is main's known HT1-H row, "fixture: the fund holds a bond".
  The other three are of the same class, and the ship file names them
  under HT1-H, the queued row for fixtures that find their condition in
  the played city instead of causing it. PW3's moved city is named there
  on main the same way. R1g touches no fund or Kept screen code; its only
  interface change is FoundingScreen's three lines.
  - "every row that folds opened on Portfolio", two lines. The Portfolio
    had no row that folds, because the fund held no bond that lasts
    (B:2858 bought through the model, and none held). That is the fixture
    main's row already names.
  - "nonrepeating content blocks still hand back at least three in four
    of the nodes drawn again". One reading, bond 2713's book: the month
    after handed back 74.8% of the nodes it drew again. The condition, a
    block of the month's content, is a reading the played city happened
    to give. On main's city the gate's box passes it.
- **WorldMoneysCheck.** Caused by R1g. A loaded city shared out the premium
  by the claims rescaled from the saved points, and two months on it parted
  from its twin by an ulp.
  - Mend: `DataSave.abroadClaimById` now saves the claims as they are.
    There is no format bump: an old save lacks the field and reads the
    rescaled claims as before.
  - The premium now weighs only the sites with points owed, as
    `liveClaims()` does.
- **ShortSellingCheck section 9.** His price moved the 48-month town, and
  the month before the purchase paid out $9.43 (main: $0.87) against the
  purchase's $59.33. The premise is restated at
  `OVER_THE_MONTH` (5) times the month before's flows. The sale's money is
  now read net of its capital-gains tax ($1.01), as a real sale's gain is
  taxed.
- **SaveFileCheck.** The 1-ulp `familyRentWeight` is main's latent rebuild,
  and main's known-reds row owns it under FRW1. Main is now 0.9.203, and on
  the merge this check is green.
- **MapCheck.** The repairs booked as G move section 1's 120-month city,
  which draws one + junction pair nearer than JUNCTION_APART. This is named
  in the ship file's known-reds under FS6, MapCheck's queued sweep row.
- **This session's checks (R1gd, on c21cc77e4's merge plus b8e9fe4bf).**
  `./batch-check.sh --fast` green; WorldMoneysCheck, ShortSellingCheck and
  SaveFileCheck green on the PC; GroundSkipCheck exit 3 (LP-G2's known
  line alone). KeptCheck was not run again here: the PC's KeptCheck slot
  was RES3's, and run 7 above was of this code. It runs in the dry run.

### Main 0.9.204 (WG3) merged, and the playtest on the merged 1900 city (2026-11-25)

- Merged origin/main at ee0384007 (WG3, 0.9.204) as 8f87b238f. Only the
  generated docs conflicted; they were taken from main and regenerated.
- The playtest on the merged 1900 city, on the PC. Under the suite
  (`./batch-check.sh LongPlaytest`) it ran out of AllChecks' 1,740 s at
  m2220 of 2,400, the PC being slower than the box (the 1900 city's 38
  minutes, above). Run directly (`java -Xmx2048m ... LongPlaytest`,
  `target/r1gd-lp-direct.txt`), it ends "All checks passed".
  - WG3's no-home finding (households with no home, months 380-394) does
    not show: no FAIL line in the run.
  - The households' UnsettledFees read a mean month of 23.2 ppm against
    `OWN_BOUND_UNSETTLED_FEES` (30), against 86.1 before the merge: the
    known-reds row under PB1 does not fire on the merged city. ★ The row
    stays until the dry run shows the box's playtest green too (the box
    may part from the PC's path), then it goes; PB1 stays queued, its
    early months' share of fees being the finding.
- KeptCheck on the merged code, on the PC once RES3 released the slot
  (`target/r1gd-kept.txt`): exit 3, known red only. The same four lines
  as run 7: main's HT1-H fixture row and the three this ship file names
  under HT1-H. Section 63 searched Restaurants (170 matches); the live
  heap after release was 1,406.8 MiB of 2,048 (the bound 1,536). The box's
  software renderer keeps more on the heap (FS5: 175-340 MiB), so the dry
  run's heap line is the one to watch.

### The second dry run (2026-11-25; cbs-gate-R1g-PcIeOrFf)

The dry run on the box ran R1g at 46b24b126 with main 4183928ca (0.9.204
with BX1). It exited 1 with three harnesses red and not named. The run is
kept on the box at `/home/jerus/cbs/runs/cbs-gate-R1g-PcIeOrFf` and copied
home to `%TEMP%\cbs-gate-R1g-PcIeOrFf`. The ship file's first line names
it.

- **PackagingCheck** is main's. BX1's zip line calls Windows' own
  `tar.exe`, and the check looked for a bare `tar -a -c`. BX2 (main
  dc573ee27) mends the check, and R1g merged it as 6fe14c0ba. Nothing of
  R1g's is involved.
- **RefineryCheck**, the fixture "an Oil Refinery standing on its wells'
  crude in a town of 600 houses". It is red on the PC too. A helper traced
  it with instrumented runs.
  - The order is placed at m18 on every commit.
  - What fails is the premise `imported == 0`: 9.09e-13 barrels (2^-40)
    of crude were bought from the world. The cause is `Markets.java:401`,
    where `got = bids[j] * fill / bid` with `fill == bid` rounds one step
    low, and the step is booked as an import.
  - It is green on main ee0384007 and on R1g 09f7dd4db. It is red on
    R1g c21cc77e4 (main 0.9.203 merged), dd000d256 and 46b24b126.
  - WG3 is not involved. R1g's path parts from main's in the last digits
    from m5, and the merged path lands the m18 bid on such a value.
  - Taking the whole bid when `fill >= bid` turns it green, but it moves
    every clearing's last digits.
  - ★ This is a real defect, main's, found in passing. R1g touches no
    `Markets.java`. It is queued as MKR1 in the ship file, and the
    fixture's line is named under MKR1. It is not mended here: a mend
    would be model code beyond the plan, and would need a new review, the
    suite and the ensemble.
- **KeptCheck**, "the full kept check's objects leave a quarter of
  AllChecks.HEAP_MB free". On the box's software renderer the objects
  read 1,562.3 MiB against 1,536. Main's gates read 1,464.8 (PW1-R) and
  1,492.8 (PW3); R1g's 1900 city read 1,553.1 at the first dry run. The
  PC's Direct3D reads 1,406.8 on the same code.
  - A helper played the same 600 months with no screen on R1g 6fe14c0ba
    (`probeTodayCity()`) and on main dc573ee27 (`probeCity()`). The
    model held 85.5 MiB against 82.1 after GC; the class histograms
    differ by 3.4 MiB in all, the largest `OrderBook$Order` at 0.9 MiB.
    R1g's model keeps nothing growing.
  - The difference is the pages KeptCheck builds for R1g's today's city:
    10,811 people at m605 against main's 9,919, and its own businesses
    and fund.
  - ★ Named under HT1-H, the queued row for harness premises that the
    played city sets. Its other KeptCheck lines are named there the same
    way.
- KeptCheck's other reds (the four of run 7) are named as before; no
  other KeptCheck line was red on the box.
- **The playtest** on the box reads "All checks passed": UnsettledFees'
  mean month is 23.2 ppm against 30, as on the PC. The LongPlaytest row
  under PB1 is dropped from the ship file (the orchestrator's go: "only
  if the box's playtest agrees"). PB1 stays queued, with that figure
  added.
- The queued row is MKR1, not MR1: MR1 is taken (QUEUE.md, Manufacturing's
  re-order after a bleed sale; the orchestrator, 2026-11-25). The kept run
  could not be reused (BX2 moved PackagingCheck.java and pick.sh), so the
  gate-run line is dropped and a fresh dry run follows.
