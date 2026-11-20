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
| (a) + (b) | | | | | | |

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
