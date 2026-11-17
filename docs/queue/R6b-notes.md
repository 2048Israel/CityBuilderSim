# R6b — Research: the near-future rows — notes

Batch R6b, 2026-11-16/17, built headless from batch/R6b (cut from main aeac6e5a, 0.9.130, after CU2). The
brief: `docs/studies/research-near-future.md` section 10 (with 3, 9 and 11) and QUEUE's R6b row, on R6's
recommended calls ★1, ★2, ★6 and ★7, ★3 overturned (Jerus, 2026-11-09: "Keep to 2060"; the 2030-2060 tier
is R6-T's). Coding went to four Sonnet slices (the mark; FE1; FE2; FE4), each diff reviewed and committed here.

## The mark and its dials

- `research.json` learns two optional keys, `"projected": true` and `"window": "2027-2030"` (its readme says
  so); `ResearchCatalog.Entry` reads them (`projected()`, `window()`, `mark()`, `dateWords()`), and a row
  without them is history. Gson leaves a missing key alone and a save holds codes: no `SAVE_FORMAT` change.
- `ResearchCatalog.HISTORY_ENDS` = November 2026, the month the catalogue was last read against the world.
  `ResearchCheck` 11 holds every row to it: dated after it, projected with a window holding its year; on or
  before it, history with no window. FE4 (June 2028) is projected, 2027-2030; FE1 (May 2025) and FE2 (Nov 2025)
  are history.
- `ResearchCatalog.NEAR_FUTURE` = January 2025 (the study's rule 6): a row dated from it on is the near future,
  `isNearFuture()`, `nearFuture(code)`; `ResearchCheck` 12 holds those to the F codes and back (★7).
- **Where it shows** (the study's 10.2): the inbox's "From abroad" line (`entry.dateWords()`: "The world has it
  now (2028, Boden, Sweden; projected, 2027-2030)") and its "Found here" line; the research screen's timeline
  dots and found-here rows (`whenWords`), its City History flags (`ResearchView.flagEntries`), and the build
  card's origin and New strip (`BuildScreen.originPlace`), all through `ResearchView.marked()`.
  `ResearchPreview` and `YearBook` print no row's date or world text, so neither changed.
- **The default city's words**: it never knows a near-future row (★1 below), so `knownAtFounding()` there is
  the catalogue less the near future's and the research pages say "founded knowing all 91 of the catalogue's
  discoveries dated before January 2025; the near future's 3 come only to a city founded the 1900 way"
  (`ResearchView.foundedKnowingWords`); `stillToCome()` is 0 there.

## The rows, what each gives and where

- **FE1, the driverless truck** (`research.json` FE1, May 2025, Dallas to Houston, fee). The Truck Depot (139)
  carries a second piece, the driverless kit, ending a new form `Equipment.Form.CAB_DRIVERS`. `Haulage` keeps
  a driven share of the depots' truck room (not saved; `EquipmentForms` sets it each month and after a load
  from the kit's counts, 1 in a city founded today's way): `tonnesATruck()` blends `TONNES_A_TRUCK_MONTH`'s one
  shift (`SHIFT_HOURS` 10) with `DRIVERLESS_HOURS` 20, `driverPostsATruck()` blends `DRIVERS_A_TRUCK` with
  `OVERSIGHT_A_TRUCK` 0.1, and the capacity, the fleet's need, the drivers struck (`strikeDrivers()`) and the
  quote's estimates read them; at a share of 1 each is main's own expression, to the bit. The kit's gain is
  `EquipmentFit.driverGain()`: the drivers' wages less the oversight's, and the capital month
  (`Haulage.truckCapitalMonth()`) of the trucks no longer needed; `LicenceFees` bills FE1's fee on it unchanged.
  `FreightView`'s plant plan reads the live figures; the trucks' info text names both tonnages.
- **FE2, sulphide heap leaching** (Nov 2025, Johnson Camp, Arizona; needs E12). A new template, the Sulphide
  Leach & SX-EW Plant (153, Copper Mining): the oxide plant's 1,000 t of cathode and 3,500 t of acid on one
  SULPHIDE site, its dig `CopperMining.SULPHIDE_LEACH_DIG` = 1 / `SULPHIDE_LEACH_RECOVERY` (.85), its water 1,876
  units (71 L a kg, sourced). It runs on `CopperMining`'s own digs by (type, dig) with no change; the page
  shows its cathode apart from the oxide plants'.
- **FE4, hydrogen-reduced iron** (projected June 2028, Boden, Sweden; needs PE1 and E4). A new template, the
  Hydrogen Steel Works (154, Heavy Industry, `HeavyIndustry.HYDROGEN_WORKS`): pellets (1/0.65 t a tonne) and
  power (`HYDROGEN_MWH_A_TONNE` 3.48 over `HydroSites.KWH_PER_UNIT`, 483,333 units) into 100,000 t of steel, no
  coke, coal, limestone or scrap. Heavy Industry now uses PELLETS, so its pelletising plants' pellets go to the
  works first (captive); with no works the captive need is 0 and the arithmetic is a made-only good's
  (`HydrogenSteelCheck` 5, to the bit). The sector page gains a "pellets, bought" block at 0, as every other
  good it uses has.
- **Named for their owners' batches** (the ship file's pending): FE3 and FE6 after PW2 and PW4, FE5 after a
  storage batch, FM1 and FM2 after R2f and the gap's mRNA row. Nothing of PW's or FM2's was built.

## The calls (balance), each taken on the recommended way

- **★1 A city founded today's way never knows a near-future row** (from `NEAR_FUTURE`): `Game`'s template rule
  and `EquipmentForms`' driven share. Others: (b) on its date in every city; (c) known from month 1, as every
  other row is there. Why: research, recipes and the research screens run only in a city founded the 1900 way;
  `buildings.json`'s figures are today's, and a near-future row is not; the default playtest, the gate's
  audited city, stays today's to the bit.
- **★2 FE1's hours: `DRIVERLESS_HOURS` 20 against the 10-hour shift** (est.; unpublished, the study's 3.2).
  Others: (b) 24; (c) hold the row. Why: the hours-of-service rule (49 CFR 395.3) binds the cab, not the truck;
  loading, fuelling and upkeep remain.
- **★3 FE1's oversight: `OVERSIGHT_A_TRUCK` 0.1 a truck** (est., unpublished). Others: (b) none; (c) a third.
  Why: remote assistance and terminal staff are real posts; the builders publish no ratio.
- **★4 FE1's kit: half a truck's price for each truck the depot keeps** (MACHINERY 802.24, est.). Others: a
  truck's whole price; a tenth. Why: nothing on price is published (the study's 3.2); half a truck is a large outlay that pays over its 120 months only where drivers cost much.
- **★5 FE1's wear: a driverless truck wears by the month as a driven one does** (the game's calendar life).
  Other: by the tonne, so it wears twice as fast. Why: the game prices a truck's life in months
  (`Sector.VAN_LIFE_MONTHS`) everywhere; the gain then counts the capital of the trucks no longer needed.
- **★6 FE2's recovery: 85%, the builder's ceiling** (est.). Other: the oxide plant's 72% (`LEACH_DIG` 1.4).
  Why: the row is the sulphide process the builder sells; its stated ceiling is the only figure.
- **★7 FE2 is kind "building", size "recipe"**: a process on an existing plant's frame (the convention for a
  row that opens a building but is a recipe-sized find). Other: size "building".
- **★8 FE4 is one integrated works, pellets and power into steel.** Others: (b) a new DRI good and an arc
  furnace recipe; (c) pig iron as a stand-in. Why: the study's 3.48 MWh is a tonne of liquid steel, the game
  has no DRI good, and a new good moves `Good`'s order and every market's.
- **★9 FE4's figures: 0.4 of a Blast Furnace plus 0.8 of a Large Mini-Mill** (cost, posts, ground, water, est.).
- **★10 FE4's date: the study's projection, June 2028**, window 2027-2030 (Argus, N8, refused the read; the
  figure the study had stands).

## What the checks showed

(filled at the end)

## The trace

(filled at the end)
