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
