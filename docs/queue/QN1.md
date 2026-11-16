# QN1 — New-building proposals beside canonical templates

## Why this batch

The orchestrator split prospective quote alignment out of R2e in mail
20261115-213002-531 on 2026-11-15. Main already values a new building on
the live fleet template while EquipmentFit.verdictForNew can reject a
piece that fleet has. R2e keeps the physical research changes and fitting
mechanics. This batch aligns every future-building number and gate.

★ Carry a proposal of recipe, posts, utility draw, output, input and costs
beside the canonical template. Use a value representation. Every stack,
site, order, pending-map key and kept action keeps the canonical template.
A proposal is evaluated afresh from that call's inputs and the saved monthly
production choice. Default founding preserves its existing figures.

The attempted copied-BuildingsTemplate implementation is available on
batch/R2e at4c33a68d. Its complete reader audit is in R2e-notes under
“Fifth review and complete prospective-reader audit”. R2e removes that
implementation before readiness. The retained branch fixture and evidence
show the conditions below, with the limitations recorded here.

## Work

- Provide one shared new-unit proposal for investor estimates, Build cards,
  advice, automatic building, staffing holds, licence checks, future jobs,
  queued capacity and power demand. Preserve installed production values.
- Use the equipment selector from R2e and its saved monthly recipe rule.
  A recipe list is priced fresh before its first production application.
  Once applied, its saved choice is the process production will apply.
- Keep the template identity canonical through every boundary. Resolve a
  proposal's numbers at the point they are needed. Keep order actions and
  map keys on the real template through refreshes and month changes.
- Preserve BuildAdvice.needs(Map) and its existing callers. Add a
  Game-aware proposal route for the Build screen's order needs.
- Preserve Book/Bank standing costs, branch closures and captured royalties.
  Apply the proposed crew cost only to the additional branch in its opening
  decision. For N standing branches with cached later-branch cost C,
  proposed cost C_new and charter exemption E, the fee threshold is
  N*C + C_new - E. Existing branches retain C.

## Review traps

| Review | Condition to cause and avoid |
|---|---|
| REV-R2e-4 finding3 | Robot-fitted Vehicle Works has100 unskilled posts. A new plant rejects the robot and needs300. Investors, Build card and the fitting-versus-new comparison must all read300. On the retained positive-price control the old quote earns5.8193230255 while the actual selected new recipe loses0.8401224457 a month. |
| REV-R2e-5 finding2 | BuildCard.of and investors must use that same crew for running costs and staffing. Create a boundary where100 passes and300 fails with arrivals closed. Two new plants need600 unskilled posts in the order bar. |
| REV-R2e-6 finding1 | Figures.template must remain canonical. Page cards and kept orderControls callbacks at BuildScreen949/970 retained frozen copied templates. Refresh through a month and through changed fitting inputs, proving the action still orders the real template with current proposal numbers. |
| REV-R2e-6 finding2 | BuildCard.of and investors passed copies into identity-based BuildingManager.getStack. A standing stack with an active site must retain its quantity, construction count, wait and investor/city contracts while its proposed recipe differs. |
| REV-R2e-6 finding3 | WorksValue.pendingLoads keyed a copied template, so completionSchedule found no stack and used zero wait. A real queued non-power works with changed proposed power draw must add demand only on its actual completion schedule. |
| REV-R2e-6 finding4 | The attempted Bank gate multiplied the new clerk adjustment by N+1 and charged the standing branches for it. Apply C_new once. The removed synthetic fee-boundary fixture encoded the same mistake and must be replaced with an oracle based on N*C + C_new - E. |
| REV-R2e-6 finding5 | CityNeeds.powerAhead still took queued jobs from the fleet template. A real queued changed-crew plant must contribute its proposed jobs, and housing/services demand derived from those jobs, at the correct wait. |
| Main's saved process | Pulp Mill has no independent changes. It saves kraft using21500 residues. Move residue prices so a fresh choice favours groundwood using zero residues. Its new-unit proposal must retain the process the month's production apply will run. R2e's uniform saved-process helper supplies this rule. |
| Caller-supplied templates | BuildAdviceCheck's two custom clinics share a copied catalogue id. Retain the selected clinic's actual identity and whole-plan value. Looking up every normal candidate by id replaced it with the catalogue clinic. |

## Removed quote-only controls to rebuild as value-object controls

R2e's source at4c33a68d contains these in PostwarRecipeCheck's
wholeNetAndSavedProcess and allFutureReaders. They move out of R2e.

| Control | Figures and retained premise |
|---|---|
| Rejected new robot | Existing Vehicle Works100 unskilled, proposed300, known robot rejected at positive prices and wages. |
| Physical proposal isolation | Proposed300 leaves the installed100 intact. The replacement should expose posts as proposal data beside the canonical template. |
| Investor and card profit | Positive-price old profit5.8193230255 versus selected-new loss0.8401224457. Use the first rejecting machinery price, so huge repair costs cannot hide payroll error in relative tolerance. |
| Ordering identities | Appraisal's template and a built stack's template stay canonical. Cover a real site, contracts, waits and kept callbacks as required by the review traps. |
| Bank clerks | Cached quote loss7.2163588125 versus new-crew loss8.0020943769. Both B1/B3 machines are rejected and extra clerk wages are positive. |
| Bank fee boundary | Replace the removed (N+1)*(C+delta) oracle with N*C + C_new. Use positive fee income on both sides of the actual one-new-branch boundary. |
| Default allocation/identity | The entire default-founded catalogue previously reached zero template copies. The replacement uses canonical identities throughout and preserves all eight default traces. |
| Matching figures | A historical vehicle proposal with the same chosen recipe retains identical numbers and canonical identity. |
| Pulp monthly choice | Saved kraft21500 residues versus freshly preferred groundwood0. Both recipes are known and runnable. |
| Staffing boundary | Derive qualified heads from existing posts and the proposed plant, close arrivals, then find a workforce where100 passes and300 fails. This premise held on both309131486 and4c33a68d. |
| Main card and investor gate | Card posts300, the proposed running bill, the actual staffing share and a STAFFING refusal. |
| Order bar | Two proposed Vehicle Works600 unskilled while the installed fleet remains100. |

## Reader inventory

Start with the complete table in R2e-notes. Cover BusinessInvestment and
all sector-specific estimate/plan entry points, Sector staffing and
WantedPosts holds, SpreadPlanner candidates, BuildingManager future
capacity/jobs, CityNeeds.powerAhead, Game.consider and its slices,
licences, BuildCard's public quote doors, BuildAdvice, WorksValue,
AutoBuilder, BuildScreen and the proposal row in SectorScreen.

Distinguish standing totals and removals from future additions. Land,
construction cash/materials/points and financing terms are unchanged by
R2e's current recipe deltas. Public quote and gate routes still need one
consistent proposal when they derive requirements from jobs or recipes.

## Verification

Each regression must cause the stated condition and fail on main before
its repair. Preserve existing harness premises and the kept-node lifecycle.
Use named readers while building. This shared valuation change selects ALL,
so its final verification is the normal launcher merge dry run. Capture the
eight default traces once before changing the model and compare the final
model byte for byte. Coordinate the heavy slot with the orchestrator.
