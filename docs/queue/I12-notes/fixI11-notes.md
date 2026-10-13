# I11 - the materials pictograms' model (0.7.141)

## 0. Steps done
- Read the brief, the shared rules, CLAUDE.md; worktree clean at c2cee14. Scratch scripts copied and repointed.
- Base model build silent; baseline suite: 107 harnesses, 107 passed (964 s).
- IndustryView + IndustryViewCheck written; sector getters added; check green (5 s); committed 27d12f3.
- GameVersion 0.7.141, CLAUDE.md row + 108 harnesses, README 108/109, QUEUE.md I11 row deleted.
- Gate run (scratch-i11/gate.sh, gate.log): see section 5.

## 1. What I built
- `IndustryView` (new, model, pure): `of(Game, Sector)` for the nine (`INDUSTRIES`, `covers()`), `all(Game)`; null for any
  other sector. A `View` holds:
  - `fields`: `Vessel(resource, type, sites, worked, onAny, owned, remaining)` per field type the city holds -
    `LandManager.getSites/getOwnedAmount/getRemaining(r, type)`, `Game.committedOn(r, type)`, and `onAny` the sites the
    works on ANY take of the type where `LandManager.packed()` places them (the quarries); the forest as one vessel.
  - `forest` (Forestry): owned, standing, protected wood, cuttable, cleared, the cut, crews at nameplate and with those on
    site, salvage, regrowth, the planting line, planted growth, the law's share, `settlesWithLaw` (= `fo.settlesAt()`) and
    `settlesWithout` (= `Forestry.settlesAt(..., 0)`, 240 x the cut), stands, the planting word.
  - `oldMetal` (Iron & Steel, Copper Refining): the pool's last month - arose (by source), bought, landfill, price, intake.
  - `wash` (Coal Mining): `CoalMining.lift()`; `carried` (Quarrying): `Quarrying.carried()`.
  - `feeds`: per good used (VANS out) - needed (`Input.needed`), captive (`Output.captive`), old metal (Copper Refining's
    old copper on COPPER_SCRAP), home, imported, the lorries' diesel, and the sellers by party off `GoodsMarket.getTrades()`.
  - `units`: every kind the plan weighs (and Forestry's stands): standing, on site, rate, feedShare (run = rate x share),
    workedOut, lastEarns (the idle book's), idleMonths, growth (stands), `oneMore` - the sector's own appraisal
    (`ProcessingSector.appraise()`, `ExtractionSector.appraise()`), every gate asked; tonnes in/out at the run, loss (works).
  - `products`: made, captive, home, exported, carried in/out, lost, idled, stock, buyers by party; `intoStock()`.
  - `power`: made, drawn, bought from the grid, sold, sold's value; `lorries`: vans, needed, diesel.
  - Words (`headline`, `unitWords`, `lastWords`, `gateWords` - the plan's own format, `vesselWords`, `partWords`,
    `metalWords`, `powerWords`, `forestWords`, `figure`/`unitsWords` in t, m3, L), colours (`colour(Good)` for the 29
    goods the nine touch, `colour(Origin)`, `colour(Resource, FieldType)`, `hatched(WORLD)`), `Roof` per industry.
- Getters added (no behaviour): `Sector.getPowerMade()`, `getPowerDrawn()`, `getGridDraw()` (new banner at the end);
  `ProcessingSector.isWeighed(t)`, `lastEarns(t)` (end banner; `noteIdleWorks()` now reads its last-one figure through
  the same private `lastOneEarns()`, ★3); `ExtractionSector.isWeighed(t)`, `appraise(t, plans)`, `isWorkedOut(t)`,
  protected `groundRefusal(t)` (end banner); overrides at the end of `CoalMining` (wash plant weighed, its ground the raw
  coal) and `Forestry` (its ground E's).
- `IndustryViewCheck` (new, the 108th; `AllChecks` after WoodProductsCheck): 11 sections - the nine and roofs; vessels;
  every good balancing against its warehouse's measured move (units and tonnes) and every feed's received = needed (+ the
  lorries' diesel); every figure the rows', fills', appraisals' and idle book's; from where (imported concentrate, captive
  coke/scrap/anode/cathode/residues, old steel and old copper, ore/coal/limestone/acid/sawlogs sold across industries,
  limestone carried, the wash); power balance (coke ovens, pulp mill 13,500 kW); the forest and 40% protected; words and
  colours; pure (two reads equal, no row made, save before/after equal but `savedAt`); after a load; the plan ordering
  the kind the view would (6 orders in three towns at founding). Three played towns: works (coal town + hematite +
  2 limestone by fiat; 11 works), copper (6 works + old copper), wood (forest + camp + 3 mills). Runs in about 5 s.

## 2. Departures, with reasons
- No laid-out picture (RefineryView's `Picture`, ribbons to scale): the brief asks for figures, words and colours, and
  I12 draws (★1).
- "Every unit balances in tonnes" built as the brief's parenthesis reads: per good, per industry - in = out + stock
  change + loss, the stock change measured by the harness over the month. Each kind's tonnes in/out/loss at its run are
  exposed (`Unit.tonnesIn/Out/loss`) but not asserted per kind: the rows are per good, so no model figure holds one
  kind's run.

## 3. Premises changed (before -> after) and the new assertions
- None moved. New: IndustryViewCheck's 11 sections (above), all against the model's own reads.

## 4. Trace movement
- None: both playtests byte-identical to pt07140 and pte07140 (8 of 8 traces each). Reads only; nothing in the month
  calls the view or the new getters, and `noteIdleWorks()` makes the same calls in the same order.

## 5. The gate, and the final commit
- Both builds silent (model and UI, 0 bytes each). Suite: 108 harnesses run, 108 passed, 0 failed (969 s), HealthCheck
  and MapCheck included. BuildMenuCheck PASS, 139 of 139 buildings. tools.Stale: 0 firm, 467 soft (mine are only the
  map pages left to the docs pass). Default playtest 8/8 identical, findings "Nothing"; Earth playtest 8/8 identical.
  `git ls-files --eol`: 0 w/crlf outside .bat. Scratch 958 MB.
- Final commit: 27d12f3 on batch/I11 (the tree the gate ran on; nothing changed after it).

## 6. ★ calls
- ★1 (in place of a part of the spec's picture): IndustryView is figures, words and colours; the to-scale layout is
  I12's. RefineryView keeps its layout in the model so its scale is checked; if I12 wants the same it adds
  `IndustryView.picture()` and a section to IndustryViewCheck.
- ★2 Roof: a fifth, CAMP, for Forestry (the spec names four - furnace, mill, kiln, pit - and a forest has no pit). Flip:
  one line in `roofOf()`.
- ★3 `ProcessingSector.noteIdleWorks()` reads the last one's earnings through a private `lastOneEarns()` that the new
  getter `lastEarns(t)` also calls - outside "a getter only" by one refactored line, so the picture's figure is the rule's
  own and cannot drift from it; same calls, same order, the playtests byte-identical.
- ★4 `ExtractionSector.appraise()` is new: plan() asks its gates inline and keeps one refusal. It judges with
  `SpreadPlanner.judge()` - its ground as FEED (no deposit, worked out, no field fits - `noField()`; Forestry's E; the wash
  plant's raw coal), then LAND, STAFF, MONEY on the whole cost with its lorries - where plan() asks fits, staff, money and
  then land for the best only. Same refusals, same words; held by section 11 (the plan orders what `best()` says).
- ★5 One new phrase: the wash plant's FEED refusal "no raw coal goes unwashed" (planWash() says nothing there, it hands
  the plan to the mines).
- ★6 A kind's run = the sector's rate x its feed share from the month's own figures: a yard's old steel bought over its
  intake at the rate (not the planner's `feedShareStanding()`), the crews' cut over their ask, the wash plant's washed over
  its wash, a worked-out mine 0, every other kind 1. A mine short of ground near exhaustion shows the rate.
- ★7 Vessels count the works on ANY on the type `LandManager.packed()` places them (`onAny`), so two quarries on ANY read
  "2 of 2 sites worked" on limestone where `committedOn(STONE, LIMESTONE)` says 0.
- ★8 Quarrying: limestone's `made` is what was dug (the row's produced plus the carry), aggregate's what was dug of it
  (less the carry), the carry its own part (`Origin.CARRIED`) out of the one and into the other.
- ★9 The planting line is `regrowthDue(plantedGrowth(true))`, what `Forestry.plant()` weighs the cut against; read after
  the month's end it is the next month's. The page's "Regrowth" is `regrowthDue(plantedGrowth(false))`.
- ★10 `wash` is `CoalMining.lift()` read after the month's dig left the ground - the next month's lift at today's rate;
  the check holds its coking coal to the month's (made + idled) where the ground holds plenty.
- ★11 The fills are shown as they are: Iron & Steel buys a sliver of its own scrap (a captive good's bid rounding to a
  tonne's fraction), so a seller "Iron & Steel 0 t" appears on its scrap; I12 may hide parts under a pixel.
- ★12 Fixture rates are low (staffed ~20%: towns of ~700 people under big works); the figures are right but small, and
  nothing passes the gates in the played towns, so section 11 tests the plan's order in towns at their founding.
- ★13 `Sector.getGridDraw()`, not "getPowerBought" (UtilitiesHandler's, for money).

## 7. For the next batch
- I12 (the screens): `IndustryView.of(game, sector)` / `covers()`; draw `built()` units (all kinds are listed, with
  their gate words, for "why no second one"); `Feed.parts()` and `Product.parts()` are drawing order; `hatched(WORLD)`;
  `Origin.CARRIED` is Quarrying's limestone ribbon into aggregate; `wash` is coal's internal stream (raw -> washed ->
  coking + middlings + spoil); `forestWords()` gives four lines; `counted()` false after a load ("a month must run").
- I13 (beside, cards): `ExtractionSector.appraise(t, plans)` now gives a mine's every gate in the plan's words - the
  build card could read it rather than re-deriving; `noField()` is called by `groundRefusal()` (I13 changes its words).
- Docs pass: maps (IndustryView, IndustryViewCheck; Sector, ProcessingSector, ExtractionSector, CoalMining, Forestry
  banners), harnesses.md (IndustryViewCheck), docs/notes counts if any.

## 8. For Jerus to check on the PC
- Nothing to see yet: the model only. I12 draws it.

## 9. Outside my files
- `ProcessingSector.noteIdleWorks()` (one call rewritten, ★3); otherwise only end-of-class banners in the sector classes.
