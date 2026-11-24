# PW2 - the power plants and their fuels

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-23, on `batch/PW2` cut from origin/main
b34277adc (0.9.193, PW1-W, SAVE_FORMAT 63). The row: QUEUE.md's PW2, [spec-electricity §2.3, §2.5, §5](../studies/spec-electricity.md#5-the-batches).

## 0. Scope

- **The rows (§2.3).** Five new: the Oil-Fired Plant (167), the Diesel Station (168), the Oil-Fired Turbine (169),
  the Biomass Plant (170) and the Solar Farm (171). The Coal Power Plant (9) and the Wind Farm (41) refigured to the
  study's table. The Hydro Station (144) kept, given its nameplate and variable upkeep.
- **Each fuel from the market by the MWh (§2.5).** Fuel oil and diesel from Refining, pellets from Wood Products,
  the rest imported, through PW1's fuel path: every bill from a real payer to a real payee, booked once.
- **The cooling-water gate.** NO_WATER beside NO_RIVER: a steam plant stands only where the city owns sea, a lake
  or a river.
- **The build-time floor.** A plant stands no sooner than its months, whatever the crews (`BuildingManager`, the
  narrow change: the site's monthly cap in the crews' rule and the wait's floor).
- **Each row from its year (Q2, Jerus: "the rec, later research will change this").** Every ELECTRICITY row has a
  `since`; the build menu, the advice and the automatic builder offer a row only from it.
- **PW7 (natural gas) is not in it.** It needs the world-1.bin re-bake that WD1 and EP1b hold for Jerus (the
  orchestrator agreed, 2026-11-23). It goes to Pending as its own row (the ship file), with E25 and E31 (§2, ★2).

## 1. What changed

- **`PowerDispatch`.** The sun: `Kind.SOLAR` (appended), `Row.solar()`, `solarAt(s)` from `SOLAR_BLOCK`
  {0.680, 1.136, 2.054} and `SOLAR_SWING` {1.0, 1.0, 0.66} (EIA-930 2025 by load block, the study's `vblocks.py`),
  dispatched after the wind and before hydro and the fuelled rows (the steady pass now runs must-run, wind, sun).
  Its weighted average over the month is 0.9998 (the wind's 1.0003). `SOLAR_FIRM`, the darkest half-block's share,
  is 0. The fuels' heat contents: `FUEL_OIL_MJ_PER_LITRE` (41.81, No. 6 at 0.150 MMBtu a gallon),
  `DIESEL_MJ_PER_LITRE` (38.46, No. 2 at 0.138) and `PELLETS_MJ_PER_TONNE` (18,700, wood at 17.48 MMBtu a dry short
  ton at 8% moisture), all from 40 CFR 98 table C-1; `heatContentMJ()` is a switch over the goods, NaN for a good no
  row burns, so PW7's gas is one more case.
- **`BuildingsTemplate`.** `cooling()` and `buildMonths()` (buildings.json's `cooling` and `buildMonths`, read
  by `BuildingCatalog`), `isSolar()`, and `credit()`, the share of a row's average the advice counts as firm
  (`WIND_FIRM` for the wind, `SOLAR_FIRM` for the sun, 1 otherwise).
- **`UtilitiesHandler`.** `Book.solarKwh` (struck from the SOLAR rows, copied with the book, restored with the
  pending month), the solar row from a solar template.
- **`Game`.** ELECTRICITY rows known by their `since` (`knows()`); `hasCoolingFor(t)` (true for a row without
  cooling, else sea or fresh water owned); `BuildResult.NO_WATER` (appended), refused after NO_RIVER in
  `buildFor()`, `buildStack()` and the run's refusal chain.
- **The card, Build and the bench.** `BuildCard.VerdictKind.NO_WATER`; Build's press ("No water to cool it" / the
  Land office ›), its verdict, its run stop and a page, NO WATER TO COOL IT, that names the rows that need none.
  `KeptBench` draws it, so KeptCheck section 44 draws it.
- **The advice and the automatic builder.** `BuildAdvice.unit()` is 0 for a steam plant without water and counts
  each row by `credit()`; `AutoBuilder`'s first-cost and pending-capacity loops skip it.
- **`BuildingManager`.** The build-time floor (sub-banner "the build-time floor (PW2)"): `siteCap()` (its
  buildings' points over their months, `monthCap()`, and all the preparation the site still owes) and `capped()` in
  `siteShares()` and `plan()`, `floorWait()` (the buildings' own points, the preparation left to the crews) beside
  the crews' wait in `waitWith()` and `siteMonths()`; `ruleWait()`, the quotes' held-steady share, counts a site the
  floor holds at its cap (★3). The twins: the coal plant and the wind farm refigured, the hydro station's nameplate
  and upkeep, the five new rows, each with its banner and sources.
- **`buildings.json`.** Rows 167-171; rows 9 and 41 refigured (the coal recipes' production1 deltas scaled with
  it); every ELECTRICITY row dated; `nextId` 172 (informational: RES3 takes 156-166).
- **`Founding.FIRST_WORKS`** opens with the Oil-Fired Plant (★6).
- **`WaterCheck`** reads the coal plant's water as 0 (★5); **`BuildingDataCheck`** reads the new fields and years.
- **`DataSave.usedCarPrice`** (`Motoring.restoreUsedCarPrice()`): the month's used-car price, saved (★11).
- **`PowerPlantsCheck`** (new, after PowerPriceCheck in `AllChecks`), §4.

The rows (HY1's rule: cash = all-in $k x 23,240/31,800; points = cash x 120,000/1,071,816; materials 1.85% of cash;
production1 = nameplate x availability x 730/720):

| id | row | $/kW | MW | runs | fuel, Btu/kWh | fuel a MWh | cooling | months | acres | posts | since |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 9 | Coal Power Plant | 3,271 | 325 | 0.85 | thermal coal 8,490 | 0.357 t | yes | 60 | 200 | 68 | 1900 |
| 167 | Oil-Fired Plant | 3,054 | 100 | 0.85 | fuel oil 11,227 | 283 L | yes | 48 | 30 | 20 | 1900 |
| 168 | Diesel Station | 1,810 | 21 | 0.90 | diesel 10,847 | 298 L | no | 30 | 10 | 3 | 1910 |
| 169 | Oil-Fired Turbine | 2,275 | 100 | 0.90 | diesel 9,447 | 260 L | no | 24 | 10 | 6 | 1949 |
| 170 | Biomass Plant | 4,843 | 50 | 0.85 | pellets 13,300 | 0.750 t | yes | 60 | 100 | 25 | 1970 |
| 41 | Wind Farm | 1,712 | 20 | 0.338 | the wind | - | no | 36 | 40 | 2 | E16 (Dec 1980) |
| 171 | Solar Farm | 1,484 | 50 | 0.24 | the sun | - | no | 24 | 333 | 4 | 1982 |

## 2. The calls (★)

- ★1 **PW7 behind a seam, not in the batch.** Natural gas needs the world file's gas price, which waits on the
  re-bake WD1 and EP1b hold for Jerus. Nothing in PW2 assumes it: the dispatch, the fuel path and the money are
  generic over goods, `heatContentMJ()` takes one more case, and the gate is a row's own `cooling` flag, so a gas
  row sets its own (a combined cycle wants water, a gas turbine none). The orchestrator agreed (bus, 2026-11-23).
- ★2 **E25 and E31 wait with PW7 (and PW3).** The orchestrator: "E25 and E31 go in with your solar farm if they
  fit; if not, they go in PW7's row". They do not fit: the solar farm is the study's fixed-tilt row at 24%, and a
  tracker's or a tandem module's gain is a capacity factor by place and year, which is PW3's (its row already
  takes them "if PW2 shipped without them"). So they go in PW7's Pending row, and PW3 keeps them too.
- ★3 **The build-time floor caps the site, not each building, and not its preparation.** A stack is pooled and finishes front first, as every
  stack does, so a site's monthly work is capped at its points on site over its months: one building stands in
  exactly its months with crews to spare (PowerPlantsCheck 5: not in 29, in 30 for the diesel station); an order of
  n stands whole in its months, its first building at about M/n. Tracking each building's own months would need
  per-building progress in `BuildingsStacks`, a saved field and a reshaping of the sites that FC2 also works in;
  the brief asked for a narrow change. The cap is a site's demand in the crews' water-filling, so what a capped
  site cannot take goes to the others; a building with no months is uncapped, to the bit. A site's preparation (a
  foothill pad, the works, a bridge: owed as progress below nothing) is uncapped: capped with the buildings, a wind
  farm on unprepared ground was quoted 418 months, and stood in them (PowerPlantsCheck 5: a station whose ground owes
  ten times its points stands in its 30). The quotes' own share, `ruleWait()`, is the weight rule held steady "before
  any cap" by design, since a site finishing frees output for a month; a floor-held site is held for its whole build,
  so the quote counts it at its cap and shares what is left by weight (PowerPlantsCheck 5, both sides of the output at
  which the cap binds; with none held, to the bit). Without it the founding's 48-month oil-fired plant would have
  weighed against every other quote for its whole build. What this leaves (REV-PW2): a second plant added to a site
  as the first finishes stands in about half its months (a coal plant in about 30 of 60). The floor per building is
  queued as PW2-F (the ship file).
- ★4 **Own load and road load scaled from the coal plant's per MW** where the study gives none: the posts in the
  table above are the study's (its ★ estimates); a fuelled row's own load and road load are the coal plant's per MW
  of nameplate (15 and 120 for 325 MW), rounded, and the solar farm's the wind farm's (1 and 5), a field of panels
  being a field of turbines' kind of site.
- ★5 **The coal plant's water is 0, not 400.** The study's steam plants draw once-through cooling from owned water
  (the gate) and nothing from the city's supply: a cooling tower would consume 500 gallons a MWh, about 10,085
  units a month for the refigured plant, past the city's fresh limit. WaterCheck's figure moves to 0 for that
  reason, a deliberate data change; its premise (a template's draw is its file's) is unmoved.
- ★6 **`FIRST_WORKS` opens with the Oil-Fired Plant.** The wind farm is now 1980's and the diesel station 1910's,
  the hydro station needs a river and the refigured coal plant costs about $777M in 1900, so the oil-fired plant
  is the 1900 city's first power it can buy outright (NewGameCheck 11: one in cash, the rest on a bond).
- ★7 **The biomass plant from 1970, not E20.** The study dates its row by the year; E20's entry carries a licence
  fee that the plant's own row should not owe, so the row's `since` is the year.
- ★8 **The advice stays fuel-blind, and values the sun at nothing, until PW3.** The advice ranks plants by their
  firm kW and their price a kW, not their fuel (PW1-P, which PW3 takes); `SOLAR_FIRM` is 0, so neither the advice
  nor the automatic builder orders a solar farm until PW3 credits it by the peak hours. A player can.
- ★9 **The coal plant's equipment (MACHINERY) is not rescaled** with its cash: the equipment a plant stands on is
  PE's, and the refigure touches the study's figures alone.
- ★10 **The wind farm's two posts are turbine technicians** (`DIPLOMA` 2), not a diploma and an engineer. The study
  gives the count (wind's 21.8 jobs a TWh), not the trades; BLS's wind turbine service technicians enter with a
  postsecondary nondegree award. Found by WorksFinanceCheck's oracle: the utilities' production is their base times
  the pooled fill of all their posts, so in a town with engineering 1.7% filled one engineering post in two drew the
  pool down by more than the farm adds (supply 207,956 kW with the farm against 208,670 without), and the advice
  rightly proposed none. The solar farm keeps one engineer in four; the advice never orders it (★8).
- ★11 **The month's used-car price is saved** (`DataSave.usedCarPrice`). Motoring's figure was never saved, so a
  loaded city marked the households' cars at the showroom's floor until its next month (live 0.726, loaded 0.0), and
  SaveFileCheck's HC2 line parted in `debtToAssets` by 3.7e-9 (3.5992070226573874e-10 against 3.59920703606054e-10).
  Main's fixture city sold no used car in the month it was saved; PW2's does. Its owner, FS17, has shipped, so the
  gap is mended here, the narrow way: one field, written and restored.
- ★12 **The coal plant buys its ground; the starting land is not enlarged.** Jerus (desk chat, 2026-11-23): "Well
  you just buy more land no? Obviously its not going to fit". The study's 200 acres stay, as do the 30 blocks, and
  LandCheck's premise that the starting land holds the coal plant is retired (§4).

## 3. Old saves

Saved state added: `Book.solarKwh` (in the power books; absent in an old save, read as 0: an old city had no sun),
`PowerDispatch.Kind.SOLAR` (appended; a record's kind is saved by name), `Game.BuildResult.NO_WATER` (appended) and
`DataSave.usedCarPrice` (absent in an old save, read as 0: a month with no used car sold, the floor, as every load
read it before).
The new template fields are the catalogue's, not the save's. No field is read differently: **no format bump owed**
(SAVE_FORMAT stays 63). PowerPlantsCheck 7 round-trips a book with and without `solarKwh`, and PowerPlantsCheck 2
(REV-PW2) round-trips the power books (`UtilitiesHandler.PowerState`, through Gson as DataSave writes the save) of a
town whose solar farm ran: the pending month's completed dispatch carries its SOLAR row, the kind by name and the
kWh to the bit, and the books read back write the same JSON.

An old city with a coal plant loads it on the refigured footprint, 8,712,000 sq ft where it was saved at 2,000,000.
`readTheSave()` books the allocated ground as the saved figure plus the change in the standing buildings' footprint
(`allocated + (built - saved footprint)`), so each old coal plant leaves the city about 6.7M sq ft over-full. It is
over-full consistently booked, as any city whose buildings stand on more than it owns (`AutoBuilder.overFull()`
holds its orders until ground is bought). No saved figure is read differently (REV-PW2).

## 4. The checks

New, **PowerPlantsCheck** (sections 1 to 8, about 165 s): each new row's figures against its dials and the
refigured coal plant and wind farm; the sun dispatched after the wind and by its hours; each fuel bought by the MWh
from its seller (fuel oil and diesel from Refining, pellets from Wood Products) or the import, paid once, from a
real payer to a real payee, in the money audit and the Trade tab; the cooling gate on both sides (owned sea, lake
or river; none, NO_WATER); the build-time floor on both sides (the diesel station not in 29 months and in 30, an
order of n, a site's preparation uncapped, the quote of a held site above and below the output at which its cap
binds); the turbine against the diesel station on both sides of their merit edge; each row known from its year
and research's lever; a book with and without `solarKwh` round-trips.

Harnesses PW2 moved, each mended by a fixture that causes its condition; no premise moved:

- **WaterCheck**: the coal plant's draw is its file's, now 0 (★5).
- **EssentialWorksCheck, WorksFinanceCheck, DeathCareFirstCheck, GapOrderCheck, ScaleCheck** (its own town): each
  fixture's town weighs a wind farm, which is now 1980's; `EssentialWorksCheck.knowWind(g)` has the town's research
  find it (`Research.restore`, the lever a player's research pulls).
- **EssentialWorksCheck** (the interim power): the fixture needs the coal plant dearer than the wind farm; the
  Oil-Fired Plant is set out of reach (`setSince(2100)`) and the wind farm priced at half its cash, so the
  condition is caused, not found (the comment gives the per-kW figures: the refigured coal plant 3% the dearer).
- **WorksFinanceCheck**: the depot owed three quarters of its points, not half, so it owes more than a month: the
  refigured coal plant, cheaper to repair, left the town's month at 201 points, past half the depot's 400. Its
  oracle also found ★10 (the wind farm's posts), mended in the model.
- **PowerDispatchCheck**: the fuel with no heat content is crude, which no row burns (PW2 gave fuel oil, diesel
  and pellets theirs).
- **BuildCardCheck, ResearchCheck**: the power plants whose year has not come are counted with the near future's,
  on no card and not in the candidates; ResearchCheck's `POWER_YEARS` asserts each row's year (1910, 1949, 1970,
  1982).
- **BuildAdviceCheck**: its short city orders no police station, the city it was calibrated on. Until PW2 the
  treasury refused its two (NEEDS_FUNDING: the coal plant at 1,071,816 left too little), so its crime was red; the
  refigured plant at 776,914 left room, the stations stood, and NEEDS YOU listed three needs, not five (the
  javadoc's NO POLICE, CAUSED paragraph).
- **SaveFileCheck**: the used-car price crosses the loader (★11), a new line; the HC2 line it parted is green.
- **BuybackCheck, CurrencyCheck, HoldersCheck 11: no coal plant, the town each was written against.** Each orders
  a Coal Power Plant on the Wealthy treasury after its list; main refused it (NEEDS_FUNDING at 1,071,816: probed on
  main for BuybackCheck's and HoldersCheck's lists), and PW2's refigured plant at 776,914 is bought, a 60-month site
  that took the crews from the houses (252 of 300 by month 61, against 300 by month 37) and the bank's room to bid
  (its equity -1,415). Without the order BuybackCheck's town is main's to the figure through month 97, and
  CurrencyCheck's defence month is main's to the cent. HoldersCheck 11 holds only two sectors, so PW2's other moves
  still reach its town (its auction cleared at 6.199%, main's at 3.813%); its lines are green.
- **CurrencyRegimeCheck.town(): no coal plant (the first dry run's red, GoldStandardCheck 7).** The same class:
  main refused the list's coal plant (six sites, 49,012 cash after the list) and PW2's was bought (site B9, 1,332
  left). GoldStandardCheck 7 then buys GOLD_LOCAL (40,000) of gold, but `buyForeignCurrency()` spends at most the
  cash, so the vault held a fraction, the cover ran about 30 times thinner (225 against 6,821 at month 3), and the
  town broke off Gold on its cover at month 96 (world month 95, 1908), before the 1933 the section is about. Without
  the order the town is main's: March 1933 at world month 398, cover 8.963, lowest 8.802, as main's export prints.
  Section 7 now asserts as a fixture that the treasury paid all of GOLD_LOCAL, so a short purchase is caught at its
  cause (the orchestrator: "makes the check stricter. No review is owed for it"). The town's other borrowers,
  CurrencyRegimeCheck and InterventionCheck, are green on it. GoldStandardCheck was not among my rounds (they ran
  about half the harnesses, below); the dry run found it.
- **EquipmentCheck 3: the month orders no fitting (the dry runs).** An electric drive on the counted town's bakeries
  pays (EquipmentFit), on main as on PW2, so the observed month orders one. On main its job stays on site 6 points
  short and the counts hold; on PW2 the builders have the room (its coal plant cheaper, 86,983 points over 60
  months) and finish it, so an Industrial Bakery was fitted in the month observed and the steam share read 0.5
  against 0.8125. A sector fits one at a time (EquipmentFit.choose()), so the fixture now holds a job of
  Industry's on site, too long to finish, and asserts it is there: the counts hold because the fixture makes them.
  The held job is cleared after the month, so sections 4, 6 and 7 play on main's town (REV-PW2b).
- **PlanCheck 1 and 10 (b): the boxes those plans were made from.** The prototype's plan (digests from 0.7.90)
  and 0.8.11's comb, pinned in section 10 (b), were made from a catalogue in which the Coal Power Plant (id 9)
  stood on 2,000,000 sq ft; PW2's 200 acres made its box over four times larger and moved every box after it
  (32,907 plots of buildings against the table's 32,217). Those plans draw it on its ground of then
  (PlanCheck.typesAsDrawn(), as GatewayCheck 21 holds the terminals' ground from before SL1); every other type,
  the planner and the rest of section 10 are today's (REV-PW2b). On today's ground (a)'s SQUARED fixture no
  longer caused its condition: its roads, 0.7 of his, sat on the ladder's step, squaring 1 of 17 homes cells on
  the old ground and none of 16 on the new (0.75 squares 10 of 17 on the new, 11 of 17 on the old, no boulevards), so
  SQUARED_ROADS is 0.75, as its sentence says ("most of the homes cells and not ... boulevards"), and (b)'s
  pinned plan keeps 0.8.11's 0.7 (SQUARED_ROADS_0811). Sections 1 and 10 green.
- **HousingCheck** (the landlords build before anyone can staff them): the yard's materials set to none, as main's
  city had them. The refigured coal plants are placed on less material (14,373 each, not 19,848), and 36 were left
  after the two years: a House's 10 come from the yard and a Studio's 320 do not, so the House's price fell to 8.5
  against 13.8 and outscored the blocks. With none in the yard the landlords order six Studios, main's line.
- **Not mended where main built the plant too.** BankCheck's tab city and InvestCheck's priced town place the coal
  plant at once (main built it as well: probed), and FloatLinkCheck's town builds it (main's under construction at
  month 62). Those stay as they are and are known reds below.
- **Timing soft.** The suite runs `-Dcbs.timing=soft` (GR1); MapCheck and KeptTimingCheck are green so.

The known reds (the ship file's rows), each a moved played city, none a PW2 mechanism:

- **ScaleCheck** 6, "within 60 months of month 225, a month it is growing" (HT1-H, which already names this premise
  for RES3): with main's wind farm restored, PW2's town matches main's to the person through month 168; with the
  refigured farm (20 MW at 33.8%, two posts, $1,712 a kW, 36 months), which keepAhead's plan builds, it stands at
  5,790 at month 225 (main 8,394) with about 470 out of work and shrinking; the one growing month with ground the
  search struck priced its migrants out.
- **KeptCheck** 89, two fixture lines, "Household money" and "the Rankings tab with the city's drawer open": every
  cell the grid opens ... the families held (1 closed; the month alone would have closed 1). Main's own KHT1 class
  (its rows for "all of them" and "every measure open", FS5): holding the families' counts does not hold the
  job-bound grid, on LongPlaytest.probeCity's 600 months, which PW2's founding power moved. The tooltip lines pass.
- **BankCheck** 13's tab city, two lines (HT1-H; FS9 shipped): the loss month is read against the played month's
  income, which the fixture does not cause. The claim at twice retail's assets was $539.98k and the month kept
  $10.98k after $32.47k written off (main: $379.74k, -$14.78k after $25.27k). Unattended, the bank's desk books a
  one-month trading gain of about $90k, in month 19 on PW2's town and in month 20 on main's.
- **InvestCheck**, FB3's rebate line (HT1-H; FS9 shipped): the founding ladder's House stands either side of the
  rebate's line at main's wage index (1.127794: 0.0000628650 at the ladder, nothing by the rule); PW2's town stands
  at 1.124593, where the ladder reads nothing too.
- **FloatLinkCheck**, "the played sale covers its fixed fee" (HT1-H; FS14 shipped): the town's tax is its coal
  plant's site, and PW2's plant is cheaper and held to its 60 months, so at month 62 the town takes 153.1 a month
  against main's 241.7; the sale capped at a third of a year's tax (612) is under the least the fee needs (653).
- **DeskCheck**, the same line seven times (HT1-H; FS14 shipped): it is FloatLinkCheck's own assertion
  (FloatLinkCheck.java:741) in FloatLinkCheck.readyCity(), on the town above. Each of DeskCheck's seven sections
  asks readyCity() for its city and returns on its null, so their laws are unasserted, not broken; DeskCheck counts
  FloatLinkCheck's fails as its own.

Main's latent faults, which PW2's moved fixture cities are the first to read (queued as PW2-T and PW2-B, §6):

- **RailDrawCheck 29**, the T line (PW2-T): the iron city draws a fourth plan siding on PW2 (three on main), leaving
  the main line at (8, 23) of tile (11239, 11611). The line's plots from (8, 24) down are RAIL in the painter's
  input and not replaced by an arc, yet their middles are not track at 8 px (the colour of the plots either side);
  main's export draws those plots the same, pixel for pixel. PW2 changes no map or painter code.
- **PlanCheck 14 (g)**, a box on a band (PW2-B): PW2's coal plant moved the dense city's rim (15,121 districts
  against main's 14,876, every 37th planned), and one district now in the sample, (7, -25), plans a one-plot Real
  Estate box at (11, 168) on a plot of a SHORE curve's 8-plot band. DistrictPlan is main's, unchanged.

**LandCheck, Jerus's call (desk chat, 2026-11-23 ~13:05): "Well you just buy more land no? Obviously its not going
to fit".** LandCheck asserted that the starting land's 30 blocks hold a power plant (it named the Coal Power Plant),
that power and water fit with under 3 blocks to spare, and that a materials plant then does not. The study gives the
coal plant 200 acres for 325 MW (spec-electricity 2.3; 87 blocks), and the call keeps both it and the 30 blocks: the
premise that the starting land holds the coal plant is retired. Restated in two parts. (1) The first plant the
starting land holds is the Oil-Fired Plant (30 acres); with water it leaves 9 of the 30 blocks, a materials plant
fits, and then the land binds (a second oil-fired plant does not). (2) New, coalBuysItsGround(): a coal plant ordered
on a founded city's own ground is refused NO_LAND, with nothing placed, bought or paid. No offer covers 200 acres
(the founding's are about 7 acres each), and the 24 standing hold less, so the city buys offer by offer as
AutoBuilder's THE GROUND ITS ORDERS NEED picks them (AutoBuilder.shortcutOffer(): the most bare ground a dollar),
the place's next offer listed after each, until the plant fits (18 offers, 6.16M sq ft). It pays the land office usd x rate once for each:
its pool alone falls, the office is owed the dollars once, the month's budget carries it once and the audit closes.
Then the same order is placed, a site on. A finding, not mended here (the orchestrator places it): AutoBuilder.groundFor() plans from the
listing standing, so it returns null for a 200-acre order in a new city, and the auto-builder holds a coal plant
there for want of bare ground on offer. The founding's first plant is the oil-fired one (★6).

The suite, one harness at a time on the PC (rounds 1 to 4, 2026-11-23, timing soft from round 4): red only by
the rows above and main's known reds (CurrencyCheck, DeathRecordCheck, Forms1900Check, FundCheck, HoldersCheck,
RoadCheck). Not every harness: their logs hold about half the registry, so GoldStandardCheck, DeskCheck,
EquipmentCheck, PlanCheck and RailDrawCheck were first run by the dry runs (§6), the first full runs of the
suite. LandCheck was restated to Jerus's call and rerun.

## 5. The trace

Traces move (the founding's first works, the coal plant's price and months, the wind's year), so the ensemble
applies, after the stranger's money review. A 1,200-month playtest probe (2026-11-23, not a gate): power 100%
served throughout; the founding grid's 10 MW carried the town to about month 700, when the automatic builder ordered
two diesel stations, and two more at month 1,162.

The ensemble (`docs/ensemble/PW2.md`, 80 of 80 runs of 2c8cadec1 on the gate box, 2026-11-23, against 0.9.192 after
REV-PW2's SHIP): batch verdict PASS, hard criteria 10/10. M1: no month's audit failed in any policy. F1: no run
has a LongPlaytest finding (0.9.192: one in AUTO2, ZERO_SLACK, SHOCK and HIGH each, "households with no home"), so four
F1 verdicts go FAIL to PASS. 24 of 26 criteria pass; the two that fail, AUTO2 A2 (the longest spell of yoy over 10%
from m240, 12 months [7 to 37] against 14) and SHOCK T2 (the final population, 44,720 against 67,498), are info and
failed in the baseline too. The first start's ssh to the box dropped with the PC's network at 14:36 after all 80 runs
had finished; the restart kept them and scored here.

## 6. The dry run

The first, on the gate box (`cbs-gate-PW2-j2acbMKF`, 238f74368, 2026-11-23): stopped on 44 unnamed lines in five
harnesses (its log prints the first twelve; `unnamed.txt` holds them all, which I read only after mailing that
GoldStandardCheck alone was red). GoldStandardCheck's 21 were the batch's own (§4, CurrencyRegimeCheck.town());
mended, the three harnesses on the town green one at a time, and run again on the orchestrator's go.

The second (`cbs-gate-PW2-TRRNqZJM`, 52cf45e41): the suite in 1,000 s, 23 unnamed lines, the other four harnesses
of the first: DeskCheck 9, PlanCheck 7, EquipmentCheck 4, RailDrawCheck 3 (exits and counts included). Main's
export (`git archive origin/main`, built beside) runs EquipmentCheck, PlanCheck and RailDrawCheck green. Each traced
by a probe on both trees (not committed):

- EquipmentCheck 3 and PlanCheck 1 and 10, the batch's own: fixtures that read what PW2 moved, mended (§4) and
  green alone.
- DeskCheck: FloatLinkCheck's town and line, a known red with FloatLinkCheck's (HT1-H).
- RailDrawCheck 29 and PlanCheck 14 (g): main's faults, read for the first time in fixture cities PW2 moved. Known
  reds, each with its queued fix in the ship file, PW2-T and PW2-B (§4).

The third (`cbs-gate-PW2-FZfbf78Z`, 7f5c8d1ea): the suite in 996 s, exit 0, no unnamed line; known red only in 14
harnesses (BankCheck, CurrencyCheck, DeathRecordCheck, DeskCheck, FloatLinkCheck, Forms1900Check, FundCheck,
HoldersCheck, InvestCheck, KeptCheck, PlanCheck, RailDrawCheck, RoadCheck, ScaleCheck); timing soft (MapCheck 2).
REV-PW2b (SHIP, no P1) ran alongside; its three folds came after it, on the orchestrator's word ("the gate's suite
covers the fold"): EquipmentCheck clears the held job, PlanCheck holds only 10 (b) on the old ground (and SQUARED's
roads, traced above), the DeskCheck row names readyCity(). EquipmentCheck green and PlanCheck known red only, alone.
