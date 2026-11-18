# R6d — Research: the speculative rows with something behind them — notes

Batch R6d, 2026-11-18, built headless from batch/R6d (cut from main 36157fb19, 0.9.151; main's two queue
commits since, FS1 and SZ1, merged). The brief: `docs/studies/research-speculative.md` section 7 and QUEUE's R6d
row, on R6-T's recommended calls (★1-★4, taken: Jerus said nothing else). One session, no slices: the code is
three small edits in classes R6b wrote the pattern of, and the two harnesses are R6b's, copied to these rows.

## R2-R: shipped, so FE8 is built

The study (2026-11-16) said R2-R mends `EquipmentFit.gainOf()`, "which has no gain in hands". It did that the
same day: R1d (0.9.126, `docs/queue/R1d-notes.md`, call 7: "the tractor's gain is the horse form's feed and hands
less its fuel (R2-R)", and "R2-R: done (★7)") and `EquipmentFit.gainOf()` now judges the tractor by
`RecipeChanges.netGainForRefit`, whose `horseCostAt()` prices the hands. No Pending row for R2-R is left (QUEUE
and the changelog name none), so FE8 is built here, not held. The study's FE8 sentence is brought up to date.

## The pages, opened again at the build (2026-11-18)

Neither row's event has come (FE10's: the department's next status; FE8's: the 2030 demonstration), so no row
moves to history and none is dropped. Each page still says what the study says:

- **X8, the energy department's PEM targets** (energy.gov, "last updated May 29, 2026"): the system row reads
  "Energy Efficiency kWh/kg H2 (% LHV) 55 (61%) 51 (65%) 46 (72%)" (2022 status, 2026, ultimate) and the stack
  life "40,000 80,000 80,000" hours. The table gives the ultimate figure no year, as the study said. The year
  2031 is the programme plan's (X8's sister page, the multi-year plan, "last updated June 4, 2026"): "Hydrogen
  production cost of $2 per kilogram by 2026 and $1 per kilogram by 2031". That page has no efficiency target.
  FE10 stays 2031 + 3 = 2034, window 2031-2037.
- **X4, Precision Farming Dealer, 26 May 2022**: "Cafiero says Deere has planned out a fully autonomous corn and
  soybean production system by 2030." and "spring tillage, planting, spraying, harvest and fall tillage all done
  autonomously". It says nothing of operators or of watching.
- **X5, Western Producer, 14 March 2024**: Aaron Wetzel, vice-president of production systems, "by 2030, we want to
  have and demonstrate a fully autonomous production system for corn and soybean producers"; the 8R tillage
  tractor "on a limited basis" from 2023. It gives no first-season count and nothing on watching.
- **X6, SAE Media, 6 January 2025**: "Deere's goal is to have a fully autonomous farming system for corn and soybeans
  in the U.S. by 2030"; "35 Gen2-equipped large tractors were operated by about 15 customers for the first
  season"; the chief technology officer: "There is human engagement with the machine, there has to be", through
  "an operation center through a mobile device". One difference from the study's wording: the page calls those
  customers paid pilot customers (50,000+ acres), with a "limited launch" for production customers from 2025, so
  "sold" is the limited launch of 2023-2025 and not a full release. The row's date stands: the whole season is
  not shown, 2030 + 3 = 2033. A search for a 2026 restatement or change of the 2030 target found none.

## What was built

- **The mark**: `research.json` FE8 (2033, window 2030-2036, needs PE14 and PE17) and FE10 (2034, window 2031-2037,
  needs FE4), both `projected`, patented, the study's work figures (29,081 and 30,623 researcher-years), after FE4
  in the file. `HISTORY_ENDS`, `NEAR_FUTURE`, the screens' marks, the default city's words: R6b's, unchanged. A city
  founded today's way never knows either row (`Game.knows()` by `ResearchCatalog.nearFuture()`).
- **FE10, the electrolyser** (a second piece, the only one, on the Hydrogen Steel Works 154, ending the new form
  `Equipment.Form.ELECTROLYSERS`). `HeavyIndustry`: `HYDROGEN_KG_A_TONNE` 54, `ELECTROLYSIS_KWH_A_KG` 55 and
  `ELECTROLYSIS_KWH_A_KG_FE10` 46 give `HYDROGEN_MWH_SAVED_A_TONNE` 0.486 (the study's 2.97 against 2.484 MWh), and
  `unitsSavedAWorks()` is that on a works' steel in the game's units (67,500 of its 483,333). A works that has
  the piece draws that much less: `EquipmentForms.setElectrolysers()` counts them (the works standing less those
  lacking the piece, where the piece is known, 0 in a city founded today's way) and `HeavyIndustry` overrides
  `setElectricityConsumption()` to take the saving off the buildings' sum, remembering the sum so that a count set
  after `EconomyManager`'s own setting takes it off the same figure (the draw's override is Heavy Industry's, so
  `Sector` and `EconomyManager`, HL1's, are untouched). Its gain is `EquipmentFit.electrolyserGain()`, the units
  saved at the unit's price.
- **FE8, the autonomy kit** (a piece on the Mixed Farm 52 and the Grain Farm 53, ending
  `Equipment.Form.FIELD_OPERATORS`). `Agriculture`: `machinesOf(t)` (a tractor and a combine a template carries:
  the Mixed Farm 1, the Grain Farm 2), `OPERATORS_A_MACHINE` 1 and `WATCH_A_MACHINE` 0.1, the operated share
  (`setOperatedShare()`, 1 until set), `autonomousMachines()` (the machines times the share not operated, at most
  the share not on horses), and the posts: `postsPerTier()` strikes the operators from NO_DIPLOMA and adds the
  watch to DIPLOMA, rounded once on the sector's whole as the horse form's are, and `postsPastBuildings()` is the
  difference, so the labour market is offered the posts the payroll is struck on (negative past the buildings'
  for the first time). `EquipmentForms.setOperated()` sets the share from the farms lacking the kit, weighted by
  machines, 1 in a city founded today's way. Its gain is `EquipmentFit.operatorGain()`: each machine's operator
  at the NO_DIPLOMA wage less its watch at the DIPLOMA wage.
- **Saves: format 56, and the floor with it**, by the rule (an old save would load wrongly): a format-55 save holds
  no unfitted count for either piece, the load path reads a missing count as fitted, and from FE8's month (2033) the
  farms of a 1900 city would run without operators, from FE10's (2034) the works would draw less, with no kit
  bought and no fee paid. No field is added, no conversion written, `GameVersion`'s 56 block says so.

## What the checks showed

- **`ElectrolyserCheck`** (new, 71 assertions green; AllChecks line): the row and its dials, 0.486 to the bit and
  against 2.97 and 2.484; the entry (window holds 2034, FE4 dated before it, the piece known from its month and
  not before: NOT_KNOWN the month the window opens, the month before FE10, priced at it; a city that finds it
  earlier knows it from the month it finds it, rent free); the works' draw with two of three fitted, after
  `EconomyManager` sets it again, after the forms are said again, after two played months, and with none fitted
  (to the bit); the verdict against the model's own arithmetic; and (REV-R6d P1, section 7) the utility's demand. **Where it pays**: the piece costs 8.00% of a works'
  whole cost (the planner's 68,673.64), gains 13.10 a month at the model's own tariff (0.000259) for a cost of
  5,492.99 over 110 months (earns back 1,382.38): NOT_YET; its break-even tariff is 0.001029, 3.97 times the
  model's own (read off the verdict's cost, factor and hurdle, asserted PAYS a quarter above it and NOT_YET a fifth
  below); a city before the window and one founded today's way draw the buildings' figure to the bit.
- **`FieldOperatorsCheck`** (new, 70 green; AllChecks line): the dials, the posts at 1, 0 and a half (operators from
  the hands, the watch to DIPLOMA, the city's own count of posts and the payroll with them, a farm on horses
  striking none), the kit NOT_KNOWN the month before FE8 and priced at it (gain `operatorGain()`, a Grain Farm's
  two machines, a Mixed Farm's one, PAYS exactly by `EquipmentFit`'s rule: 0.0246 a month on 1.7876, earns back
  2.1161, hurdle 0.0132), the machine-weighted share from the farms' counts, a city founded today's way (even with
  counts put on by hand), and a played count (REV-R6d P2, below): the 1900 village 24 months from FE8, the kit asserted to pay on the Mixed Farm
  and then required to be fitted, with its effect on the posts and the payroll.
- **Each assertion fails with its cause removed.** Seventeen mutations of the model (thirteen first, REV-R6d's four below), each compiled apart and run
  against the harness that should notice (the first FAIL line of each in brackets): the saving not taken off the
  draw (the works' draw); the electrolyser count ignoring how the city was founded (today's way); the count
  ignoring whether the piece is known (the month before FE10); the 46 typed 45 (the dials); the gain not priced
  (the gain); the operators not struck (the posts); the share ignoring how the city was founded (a default city);
  a machine counted as a tractor only (the dials); no clamp for farms on horses (the horse farm); the watch left
  out of `operatorGain()` (the gain written out); the kit read where it is not known (a count-less farm the month
  before FE8); the posts past the buildings' not following the autonomy (the city's count); the share not weighted
  by machines (the farms' counts). All thirteen red.
- **REV-R6d (Codex), two P1s, both mended** (2026-11-18; the rest of the review passed: the floor 56 is justified, both pieces
  use DataSave's existing unfitted-map paths, the eight trace CSVs hash identical, the sources support the figures; the code does not date
  the DOE's 46 kWh a kilogram, whose page gives it no year, and Purdue's 10% is labelled an assumption).
  1. **FE10 cut the works' bill but not the utility's demand.** `ServicesManager` summed the template's unchanged `powerBought()` for the
     grid's demand and the draw it invoices, so `UtilitiesHandler.updateEnergyRatio()` rationed the grid against power the fitted works no
     longer draw. Now `ServicesManager` holds the saving (`setElectrolyserSaving()`), taken off both sums (`lessElectrolysers()`: the
     draw itself, to the bit, while none is saved), and `EquipmentForms` hands it at every pass (`tellTheGrid()`, also on the early return)
     as `HeavyIndustry.getGridUnitsSaved()`, the saving net of the sector's steam share, as the sector's own draw is. `Game` and `Sector`
     (HL1's) are untouched: the hook is pushed from `EquipmentForms`, which owns the counts. `ElectrolyserCheck` 7 causes it: six works
     in a 1900 city at FE10's month, the utility's demand, invoiced draw and the sector's bill each fall by six savings (67,500 units each);
     the grid's supply is then written to the midpoint of the two demands (2,702,198 units against 2,499,698 fitted and 2,904,698 not) and
     the utility's own division gives the ratio, never forced: **1.0000 with the piece on the works and 0.9303 with it off**, the same
     supply. With the old reading put back (`lessElectrolysers()` returning the draw) five assertions fail, including both ratios; with
     the saving handed as 0, or as twice, the first demand line fails. A city before the window and one founded today's way are handed 0.
  2. **`FieldOperatorsCheck`'s played count skipped every fitting assertion on a NOT_YET.** It now builds a paying fixture and asserts it:
     one month is played first (as section 3 does, so the wages are struck), and the kit on the **Mixed Farm** must PAY in that month,
     by the verdict's own gain, cost and life; section 3's village must PAY too. Then 24 months are played and the city is required to
     have fitted it (every Mixed Farm has it, the operated share fell below 1 in 23 months, equal to the farms still lacking the kit),
     the operators struck from the posts the farms offer (67 -> 65, the two Mixed Farms' machines: the posts equal the buildings' less `autonomousMachines()`),
     the month's wage pass struck on them (the posts offered equal the posts the farms say, tier by tier), and the payroll at full
     staffing equal to the wage rates over those posts, less the buildings' posts' at the same rates, by exactly the operators' wages less
     the watch's. With the kit's life put at 1 month in a copy of `buildings.json`, seven assertions fail, the first the fixture's PAYS.
     **The Grain Farm's kit does not pay in that played village** (cost 21.48 against 2.44 earned back, where section 3's own village
     has it PAY at 1.79 against 2.12): the village is a 1900 city with its clock set to 2033 and a month played there, and the price of
     machinery moves with that clock, a twelve-fold difference in the kit's cost, not its gain. It is printed, not asserted, and no Grain
     Farm is fitted in the 24 months: the fixture, not the model, so the Mixed Farm is the paying one. In a city that reaches 2033 by its own
     months the price level and the wages move together; that is the one thing a played count of this kit cannot show here.
  **Does (1) reach the default city's power? No**: a city founded today's way has no works counted with the piece, so the saving handed
  `ServicesManager` is 0 and `lessElectrolysers()` returns its argument; `ElectrolyserCheck` 6 asserts the saving is 0 there and in
  the city before the window. The other change on that path, `setWattsConsumption(getTotalDouble(gridDraw))` where `updateHandlerDouble` was,
  is the same call. The trace is not run again: it could not move (the eight CSVs of the first run stand).
- **A finding of the pick's own harness**: `HydrogenSteelCheck` 4 (a works placed by hand with no count in a 1900
  city) went red on the first version: a count-less stack reads "fitted", so the works saved power in FE4's month,
  years before FE10. Both forms now read a piece only where it is known (`EquipmentForms.known()`): a works or a
  farm with no count saves nothing before the piece's month, and both harnesses assert it from both sides.
- Inventories the rows move, each moved to the new truth: `ResearchCheck` (96 rows, ENGINEERING 52, FE 5, needs 32
  to 27 entries, the near future's five, the windows), `EquipmentCheck` (the pieces that end a form: 23 to 26, and
  the Hydrogen Steel Works named among the templates that carry one).
- **Run alone, green**: `ElectrolyserCheck`, `FieldOperatorsCheck`, `ResearchCheck` (235), `EquipmentCheck`,
  `HydrogenSteelCheck`, `DriverlessTruckCheck` (66), `Forms1900Check`, `FittingCheck`, `ResearchViewCheck` (219),
  `DiscoveryCheck` (285), `ResearchPreviewCheck` (94), `SaveFileCheck`, `ReadPathCheck`, `AgricultureCheck`,
  `BuildingDataCheck`, `MaterialsCheck`, `BuildCardCheck`. `KeptCheck` hit the ten-minute tool limit run alone (the
  suite's own timing is the gate's); its part that ran showed only the `KEPT_*_MS` timing lines (hard when run alone, as R6b's notes
  say of the Build Overview's) and nothing about these rows; the gate runs it in the suite. `./batch-check.sh --fast`: exit 0, compile OK, maps regenerated, Stale 0 firm.

## The trace

The branch's default playtest (2,400 months, `-Dplaytest.trace`, at 2c936deb1 with main's queue commits merged
after, no model change) against main's, made fresh at 36157fb19 (the FB4 reference was weeks of merges old), both
into `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-36157fb1/` and `cbs-trace-R6d/`: **all eight CSVs byte-identical**
(bank, bonds, build, house, labour, month, pop 2,402 lines each, borrow 39), and the two reports differ in the run's
seconds and its clock only. A city founded today's way reaches 2031 and 2034 in the playtest and never knows either
row: its `EquipmentForms` shares are 1 and 0, its posts and draw the buildings'. The trace does not move; no ensemble.

## The calls (estimates, each with what it is not)

- **R6-T's ★1-★4**, taken: the staged allowance (FE8 2030 + 3, FE10 2031 + 3), physical figures only (46 kWh a
  kilogram, posts; no cost target is a row's figure), the mark as `projected` + `window`, the rows ending in 2035.
- **FE8's operator: one NO_DIPLOMA post a machine** (est.). Others: the whole farm crew; a share of the land's
  posts. Why: the Grain Farm's three NO_DIPLOMA posts are its tractor, its combine and a hand, the Mixed Farm's two
  its tractor and a hand.
- **FE8's watch: 0.1 of a DIPLOMA post a machine** (est., the study's "a stated assumption or held share"). Others:
  none; a third. Why: Deere says a person stays engaged, through an operation centre, and gives no ratio; a
  university's model (a podcast of 2 February 2026) assumes six minutes an hour, 10%, an assumption and no target;
  FE1's oversight is the same tenth.
- **FE8's kit: a quarter of the farm's own machines' price** (MACHINERY 0.1 on the Mixed Farm, 6.022 on the Grain
  Farm: a quarter of its tractor 1.7 and combine 22.388), life 120 months (FE1's kit's), work 0 (est.; Deere
  publishes no price). Others: a tenth; the machine's whole price. It pays at the city's wages (above).
- **FE10's piece: new stacks, 8% of a works' whole cost** (MACHINERY 26,200, est.), life 110 months (80,000 hours
  at 730 a month, the page's stack life), work 0. Why 8%: the electrolysers are about a fifth of such a works
  (est.) and the stack two fifths of the system's price (the study's sheet of the same page: $100 of $250 a kW at
  the 2026 target). Others: the whole system, 20% (it would pay only at about ten times the tariff); a tenth of
  the 8%. **The row does not pay at the model's own tariff and pays at four times it**: the model's electricity is
  cheap (a works' power bill at the model's tariff is 125.2 a month, R6b's figure, against its 532.57 earnings), so in a played city the piece is fitted only
  where power is dear, as FE4's works pays only on cheap power and imported steel. A call for Jerus if he wants the
  electrolyser fitted by default: the price is the lever.
- **FE10's saving: 0.486 MWh a tonne of steel, taking a tonne of steel for a tonne of iron** (the study's "2.48 MWh
  against 2.97", 54 kg a tonne of iron). The works' other draw (the shaft furnace and the arc furnace, 0.51 MWh) is
  unchanged.
- **FE8 needs the tractor and the combine** (PE14, PE17); **FE10 needs FE4**, the works it is fitted to.
- **A farm on horses has no tractor to fit**: the machines that work by themselves are at most the share not on
  horses (`min(1 - operated, 1 - horse)`). A kit on a farm whose combine is not yet fitted still strikes the
  combine's operator: simplified (FE8's year, 2033, has the combine since 1939).
- **The format**: 56, floor 56 (above).

## Left for Jerus

- The four estimates above (the watch share, the kit's price, the electrolyser's price and life); none is
  published, each is one number in `buildings.json` or `Agriculture`.
- To look at, on a 1900 city in 2033-2035: the Grain Farm's and the Mixed Farm's equipment lists show the
  "Autonomy kit", the Hydrogen Steel Works' the "46 kWh electrolyser", both through the existing equipment screens
  (data-driven, no screen was changed). Eye check, not pending.
- The other six rows of the tier (FE7, FE9, FE11, FE12, FE13, FM3) wait on their owners' batches (R6-T-P).
- Nothing queued, nothing pending: the brief is whole.

## The pick and the review

`sh tools/pick.sh origin/main HEAD`: **ALL 224 harnesses** (`AllChecks` changed), review **needed - saves:
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT**. A dry run is owed (the orchestrator's go).

## Merged again (after REV-R6d's mends)

Main moved to 608bf7025 (0.9.152: LM1-D, the founding's water widened to six blocks, and BW2's queue row) and is merged in; the five
generated indexes took main's side in the conflict and were regenerated (`--fast`: exit 0, Stale 0 firm). The traces above were made on
36157fb19, before LM1-D, and compare this batch's change alone to main as it was; LM1-D's own is its notes'. After the merge,
alone and green: `ElectrolyserCheck` 71, `FieldOperatorsCheck` 70, `HydrogenSteelCheck`, `DriverlessTruckCheck` 66, `ResearchCheck` 235,
`EquipmentCheck`, `WaterCheck`, `Forms1900Check`, `HydroCheck`. The pick is still ALL 224, the review line the same: saves.
