# QN1 — new-building proposals beside canonical templates — notes

Builder: Claude (Opus 5.5), started by the orchestrator 2026-11-19; branch `batch/QN1` from origin/main 3c4661a9d (0.9.155).
Brief: `docs/queue/QN1.md`, from R2e's fifth review (`docs/queue/R2e-notes.md`, "Fifth review and complete prospective-reader
audit"); commit 4c33a68d read as evidence only (its fixtures, not its copied templates).

## What was built

- **`NewUnit`** (new, `src/main/java/ham/citybuildersim/NewUnit.java`): one new building of a template as it would run: the
  recipe, posts, draw, gen, water, consumables, makes and uses, and RecipeChanges' extras (production1, steamRail,
  crackerPetrol, dig), held as a VALUE beside the template it is of. `NewUnit.of(game, t)` resolves it from the shared pair:
  the pieces `EquipmentFit.fittedForNew()` keeps, on the process `RecipeChanges.newChoices()` gives (the saved choice once a
  recipe list has run, a fresh one before), summed by `RecipeChanges.newUnit()` with `apply()`'s own arithmetic for a stack of
  one. A city founded the default way, a template with one way to run, no game, or a call from inside a pricing gets
  `NewUnit.standing(t)`: the template's live figures, the same numbers in the same arithmetic as main.
- **Every future-building reader reads it** (inventory below); every order, stack, site, pending-map key, Suggestion,
  Appraisal, Candidate, Decision and kept callback keeps the canonical template, and installed production keeps its values.
- **The Bank's new-branch gate**: `feesWouldCoverAnother(N)` is `fees + E >= N*C + C_new`, `C = laterBranchCost()`,
  `C_new = newBranchCost() = C + newBranchExtra()` (the new branch's extra clerks at today's wages, from BusinessInvestment);
  C_new once, standing branches at C, closures (`branchesTheFeesCover`) unchanged. The hold and open reasons print C_new.
- **`BuildAdvice.needs(Map)` kept**, `needs(Game, Map)` added for the Build screen's order bar (additions resolved, removals as
  they stand), one body for both.
- **`NewUnitCheck`** (new harness, registered in AllChecks): the brief's regressions and controls, nine sections.
- No saved field changes meaning; `SAVE_FORMAT` unchanged.

## Reader inventory

R = reads the resolved new unit; S = keeps the figures the building stands with (main's values); I = identity, the template.

| reader | verdict |
|---|---|
| BusinessInvestment.estimatedMonthlyProfit(key, t), estimatedMakerProfit, runningCostOf, wageBillFor | R: resolved once (`newUnit(t)`), the unit passed down; `standingRunningCostOf` S |
| BusinessInvestment's bank estimate | R: a later branch's cost plus `newBranchExtra()` when the new branch's crew differs |
| Bank.feesWouldCoverAnother, branchHoldReason, branchOpenReason | R: N*C + C_new; closures and booked costs S |
| Sector.estimatedMonthlyProfit | R: the template door is final and resolves; the 15 sector overrides take NewUnit |
| LicenceFees.forNew | R: the new building's rent struck on its own estimate; the pricing inside runs S (`pricing()`), the standing rent S |
| Sector.staffing, staffableCount, staffingWithOwnSites, staffingWithCrewsSpare; Game.noteStaffingHold (WantedPosts' holds) | R |
| Game.licencesNeededFor, hasLicencesFor | R (and a `(NewUnit, long)` overload) |
| SpreadPlanner candidates: ProcessingSector.estimate/weigh/appraise, Refining.appraise/unitEstimate/packageEstimate, SpreadPlanner.earns | R through `City.unit(t)` (the game's frame resolves; a check's frame stands) |
| ProcessingSector.lastEarns / lastOneEarns (idle, then shed), lastEarnsAt | S |
| Sector plans: BusinessInvestment.planMaker (order size), Agriculture.worthAtTheFloor, Automotive (steel wall), Construction (points), Oil.planOnLand, ExtractionSector plan/appraise, CoalMining.planWash, Forestry plan (roomFor, eWords, groundRefusal), Manufacturing, FoodProcessing (fillFor) | R (sweep, `target/QN1-sweep.md`) |
| Forestry.salvageCostPerM3 / crewRunning | S: the crews that stand |
| "is this a mine" tests (lifts, isCrew, tonnesOf > 0, isLandWell), Refining.sizeOf, the plant-wide cracker yield | I: a kind or size class among templates |
| BuildingManager.postsOnSite, getJobsUnderConstruction, Sector.getPipeline; on-site getters of Automotive, CoalMining, Forestry, Haulage, ProcessingSector, Refining (`underConstructionOfNew`) | R, per stack on site |
| on-site getters of template-only figures (rail, stations, coverage, capacity, feed, pumps, old metal) | unchanged |
| CityNeeds.hires (posts on site) | R |
| CityNeeds' "power ahead" (no method of that name: BuildAdvice.figure's on-site additions, the jobs on site) | R |
| Game.consider and its slices | R through the doors above; Game's own direct reads (retire, quoteDemolition, depots, overtime, fresh rights) S |
| BuildCard.of, investors, gate, appraise, weighed, power, valueAdded, runningCost, worksValue, verdict | R, one unit per door; `Figures.template()` I, `Figures.newUnit()` beside it |
| BuildAdvice.unit, built, staffing/unfilledPosts/hasPosts (unit overloads) | R at the doors; template overloads S |
| BuildAdvice.figure, supplyDemand and their helpers (carePlaces, unburiedNext, school, officers, cells) | R for additions (n > 0), S for removals (the paving's gravel) |
| BuildAdvice.running, lifetime, orderLife, licenceOf, runningChange | R for additions; `running(..., added=false)` S (a standing gravel road) |
| BuildAdvice.rawRunningCostAt(Game, BuildingsTemplate, double) | S: LicenceFees and RecipeChanges.feesAt read it; the future callers pass the unit |
| BuildAdvice suggestions | I: the candidate is the template given (BuildAdviceCheck's custom clinics) |
| WorksValue.of; pendingLoads; pendingPowerLoad | R; the map keyed by the stack's own template, the unit's draw at its completion |
| AutoBuilder | R through BuildAdvice and WorksValue; the measure's own standing spend `standingRunning` S |
| EquipmentFit.choose (fitting against a new building) | R through the investors' estimate; fittedForNew/priced S (`standingWhile`) |
| BuildScreen statCover (posts, power, water, its sentences `whatItDoes(t, unit)`), needsWords, the order bar | R through `Figures.newUnit()` and `needs(game, order)` |
| BuildScreen scarceWords; `whatItDoes(t)` (BuildMenuCheck) | S |
| SectorScreen's proposal row | R through BuildCard's getters; no edit |
| Land, GroundPlan, ConstructionControl, quotes and invoices | unchanged: no recipe change moves land (NewUnitCheck 1's data guard), cash, materials or points |
| RealEstate, Mortgage | no housing recipe; RealEstate's override changes its signature only (approved by mail) |

## The calls (★ = a design decision a reviewer should weigh)

- ★ **A value, not a template.** R2e's copied templates leaked into everything keyed by identity (getStack, WorksValue's pending
  map, the card's kept order callbacks, BuildAdvice's custom clinics by id). `NewUnit` cannot be ordered, stacked or used as a
  key; it carries the template's figure names, so a reader changes only the receiver of its figure reads.
- ★ **One resolution per door.** A public future-building door (the investors' estimate, the card's `Figures`, BuildAdvice's
  doors, WorksValue.of, the staffing gate, licencesNeededFor) resolves once and passes the unit down; a helper's NewUnit
  overload holds the body and its template overload delegates with `NewUnit.standing(t)`, so every caller off the future path
  (shed tests, the fleet's spend, the harnesses) reads main's values.
- ★ **Pricings read standing.** Inside fittedForNew()'s verdicts and the process choice (`standingWhile`) and inside a
  licence's pricing (`LicenceFees.pricing()`), `NewUnit.of` returns the template as it stands: those pricings price the states
  they are given, as on main, and the loop licence → estimate → unit → fitting price → licence cannot close.
- ★ **Sector's template door is final.** `Sector.estimatedMonthlyProfit(BuildingsTemplate, plans)` resolves and calls the
  NewUnit door; the fifteen overrides take NewUnit, so no sector can skip the resolution.
- ★ **The bank: (N+1)·C + Δ = N·C + C_new.** Δ is the new branch's extra clerks at today's wages; C_new is counted once.
  NewUnitCheck 5 shows the (N+1)·(C+Δ) reading would refuse a branch the fees cover.
- **Extras as apply() sums them**: production1, steamRail, crackerPetrol and dig come from RecipeChanges' own sum for a stack
  of one, so a Coal Power Plant that rejects its turbine is quoted at its own generation (NewUnitCheck 9).
- **Land is not carried**: no recipe change moves land (NewUnitCheck 1 guards the 63 "without" blocks); a later one that does
  would need the unit to carry it.
- **Performance**: `of()` is evaluated afresh per call, but only in a city founded the 1900 way and for the 42 templates with a
  recipe choice; everywhere else it is a wrapper over the template, no pricing.
- ★ **BuildScreen**: three read-only hunks beyond R2e's two: statCover's posts, power and water and its sentences
  (`whatItDoes(t, unit)`), needsWords' posts, and the order bar's `needs(game, order)`; `whatItDoes(t)` and scarceWords stand.
- **Left as they are** (the sweep's AMBIGUOUS rows): the "is this a mine" identity tests, Refining.sizeOf (a rank among
  templates), the plant-wide cracker yield (a weighted mean over what stands), Forestry's salvage cost (the crews that stand).
- **No save change**: no saved field; SAVE_FORMAT unchanged.

## The slices and what review changed

- **B1** (Sonnet: planners, order sizes, on site). Review found the planner candidates' template doors (ProcessingSector
  estimate/weigh/appraise, Refining's, SpreadPlanner.earns) reading the fleet's figures; repaired through `City.unit(t)` (the
  game's frame resolves, a check's frame stands) and `plans.newUnit(t)`; lastEarns and Forestry.crewRunning pinned standing.
- **B2** (Sonnet: the sector plans' sweep, `target/QN1-sweep.md`): thirteen NEW rows taken as swept; the seven AMBIGUOUS rows
  decided as above.
- **A** (Sonnet: the card, the advice, the works' value, the order bar). Review changed: AutoBuilder's own standing spend had
  begun valuing standing buildings at the new unit (`standingRunning`, cached per month); `BuildAdvice.running(..., added=false)`
  resolved for a standing gravel road (now standing); the card's unit is `Figures.newUnit()` (`unit` was taken); BuildScreen's
  needsWords and whatItDoes read the template (now the card's unit).
- **C** (Sonnet: NewUnitCheck), reviewed against the oracle rule: every expected figure measured without NewUnit (the fleet's
  recipe, the chosen recipe's own posts, a city with the piece's input cheap again).

## Checks

- The repair switched off (NewUnit.of returning standing, reverted): NewUnitCheck 17 of 43 red, in sections 2, 3, 5, 7, 8 and 9
  (the regressions) and the fixtures of guards 4 and 6, which read a resolved unit; section 1 (the default city) green.
- `./batch-check.sh --fast` and the 23 named harnesses one at a time (NewUnitCheck, PostwarRecipeCheck, BuildAdviceCheck,
  AutoBuildCheck, WantedPostsCheck, StaffingDrawCheck, LicenceFeesCheck, FittingCheck, HistoricalRecipeCheck,
  HistoricalLicenceCheck, RecipeCheck, EquipmentCheck, ResearchCheck, DiscoveryCheck, BuildingDataCheck, BankCheck,
  BuildCardCheck, KeptCheck, HaulageCheck, InvestCheck, BuildMenuCheck, PublicLicenceCheck, IndustryViewCheck): green but
  BuildAdviceCheck's known red (main's) and PostwarRecipeCheck, which reflects on `LicenceFees.buildingFee`'s five-argument
  signature: kept beside the unit's, green.
- The eight default traces (LongPlaytest, `-Dplaytest.trace`) on 3c4661a9d before the change and on the final classes
  (200b069de): all eight byte for byte (sha256: bank 965167c4, bonds 36b99679, borrow 6638435e, build 7ce87992, house
  948bb33e, labour 8870a6aa, month ff22d492, pop 7b15aa27). No ensemble: the default trace does not move.

## Left for Jerus

Nothing is held. The ★ calls above are the ones to weigh; the one a later batch may want is the plant-wide cracker yield
(Refining.crackerPetrol()), which with no cracker standing falls back to the Cracking Unit template's yield rather than a new
cracker's own: blending a new unit into a weighted mean over what stands is a design question, left as main reads it.

(Added to Checks.) main merged at ddcb3400e (HL1-R, 0.9.156; conflicts only in the generated docs): `--fast` green and
HL1-R's BusinessServicesCheck green on the merge. `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed); review needed,
money: Bank.java.
