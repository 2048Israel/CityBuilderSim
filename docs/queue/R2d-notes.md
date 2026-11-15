# R2d - The 1900-1950 entries undone

Started from main 4343a815 on batch/R2d, 2026-11-15.

## Design calls (★)

- ★ Preserve the default path: application is behind foundedUnfitted().
  Top-level figures stay today's. Compare all eight default traces to main.
- ★ Independent discoveries are signed undo changes to immutable modern
  figures, alongside R1c's alternative process recipes. Sum in code order
  and round once. Equipment weights use each piece's own unfitted count;
  additive changes need no invented correlation between pieces.
- ★ Keep the tractor recipe and horse form. PE17's combine and the 1900
  binder it replaces are independent, as R1c ★4/★8 require.
- ★ Save recipe decisions as suffixes on the existing saved recipe ids.
  Loads replay decisions; equipment counts are applied separately.
- R2c owns licence fees. Orchestrator authorizes a small EquipmentFit hook:
  gross independent-piece gain feeds its fee netting only in a 1900 city.
  Recipes.gainOf remains untouched.

## Sourcing gaps to show Jerus

- PE3 supplies field access, no comparable well-productivity ratio; oil fields
  lack a rock-hardness axis. Do not use Spindletop as an average-well multiplier.
- PE9's typhoid mechanism and the PM rows belong to R2f.
- PE12 has no FERTILISER good or works; study Q11 is still needed.
- PB3 sources WQ/R2 give store counts, explicitly no cost per sale.

## Checks

Pending.

## Row mapping and estimates

Every change's source string in buildings.json gives its arithmetic and the
study's source keys. R1c's legacy nine recipes and their sourcing stay in place.
The additions are 35 undos on 27 templates:

| rows | mapping |
|---|---|
| PE1, PE8, PE11, PE23, PE25, PE27, PB1 | R2/R2b already date the new buildings; no existing 1900 template to downgrade |
| PE2, PE22 | P2.24's 30→90 ft/min and P2.25's x4: carbon/HSS/carbide cutting times 12/4/1. Independent missing-stage deltas +8 and +3 modern cutting crews. ★ DIPLOMA posts proxy the cutting crew; no source partitions a template's jobs. Vehicle Works has only PE22. |
| PE3 | no numeric effect invented; source/model gap above, confirmed by orchestrator |
| PE4 | §8.5's 30% coal/clinker estimate on the short kilns. ★ Cement/lime tonnes proxy clinker. Subtract R1c's separate shaft-kiln penalty on Cement Plant so all missing together, rather than twice, give 30%. Large Cement Plant remains unavailable until PE4. |
| PE5 | study's 60x rate at fixed footprint: slow sand makes 1/60 of modern output. ★ Equivalent fixed-land throughput, rather than inventing land for an already standing works. Medical part R2f. |
| PE6, PE16 | study's 3.6→24.3% efficiency and PE16's 40% fuel estimate. ★ At fixed heat input, output stages 3.6/24.3, 0.6, 1; separate changes add to exactly the first stage. Municipal electricity has no existing coal-purchase invoice; no new one added. |
| PE7 | midpoint 67.5% of the sourced 60–75% recovery / 90% flotation = 0.75 modern concentrate, fixed ore input and grade (estimate) |
| PE9, PM1–PM6 | medical mechanisms, R2f |
| PE10 | a crop-fed Snack & Oils Plant makes no FATS before hydrogenation; source gives no allocation of its shared crop input, so keep crop input and SNACKS |
| PE12 | no good/works; study Q11, no figure invented |
| PE13 | 12.5/1.55 chassis-hours, applied to unskilled assembly posts only (mapping estimate); engineers unchanged |
| PE14 | existing tractor recipe and horse form; its form gain remains R1d's pending call (R2 notes), not silently changed |
| PE15 | sourced one-colliery 8.5/5.5 tons/man-day, mapped to unskilled loading posts; the cutter remains independent |
| PE17, binder | sourced midpoints 45/17.5 hours, on unskilled harvesting posts; R1c row 7's known-1900 binder undo +4.5 modern crews. ★ Additive stage estimates, independent of tractor and each other. |
| PE18 | 30/10 cows per milker-hour, applied to unskilled farm posts (mapping estimate; no separate milking crew is sourced) |
| PE19 | study estimate 8/0.5 sheet tons/shift; DIPLOMA posts proxy rolling crew, independent of converter (mapping estimate) |
| PE20 | midpoint +14.5% of the sourced 9–20%; old output modern/1.145, across CROPS (crop-mix estimate). Main has no separate modern seed invoice, so none invented. |
| PE21 | 28/7.5 efficiency ratio, actual coal replacing diesel at 36 MJ/L and 24 GJ/t (heating-value estimates), on the same tonnes moved. Locomotive Works has one undifferentiated ROLLING_STOCK good; no product-type multiplier invented. |
| PE24 | thermal petrol 0.24 vs main's catalytic 0.55 L/L; 0.31 stays fuel oil (mass-balance estimate). Restore main's 0.55, not the study's rounded 0.555, to preserve today's figures. Both sizes share the process. |
| PE26 | study's 2→up to4 cords/worker-day, old output half, same crew (upper-bound estimate) |
| PB2 | study's named delivery 3% + accounts 2% of bill estimate; support crew equivalent = 5% of template food bill at Good's import prices / unskilled wage, then whole-post rounding. No source supplies posts/sale; this conversion is explicitly an estimate. |
| PB3 | store counts do not supply cost/sale; no figure invented, confirmed by orchestrator |

★ New equipment costs follow R1c call 10's estimate: a quarter of plant cash
as MACHINERY at its 13.4 modern import price, a tenth of construction points,
300 months life. These are model estimates, not sourced historical prices.
Rows with dubious mappings above are shown to Jerus; no adoption count becomes
a productivity number.

★E3 clarification from orchestrator (2026-11-15 07:20): keep R1c's economic
choice. With every entry known, the modern recipe is available at exactly
today's figures (zero undo), and runs where prices favour it. No forced-modern
rule. R1c's canRun had excluded E1's modern converter from an open-hearth-fitted
stack forever; allow a later default process without an equipment requirement
to supersede that old piece. The fixture makes producer-gas coal dear and
asserts both availability and exact modern figures.

Before-change evidence: main 4343a815, target/R2d-evidence/before/check.log,
40 failed of 67; all 35 individual transition assertions failed, plus the
binder/tractor and real-clock observations. Initial main's default trace
completed 2405 months in 621s, eight CSVs kept under target/R2d-evidence/main.
It has main's layout movement observation (12 boxes), no R2d model code.

FittingCheck's diagnostic preserved its assertion and showed exactly what
violated its implied bakery-only premise: Machine Works id 50 finished with
carbide-tools=1; Industry steam remained 0 and both Bakery drives came fitted.
Orchestrator authorized holding unrelated planners before settlement, retaining
the global !anyUnfitted law, and adding a per-month observation that no other
building completed while the Bakeries opened. A separate real Machine Works
construction/refit fixture proves the newly unknown piece's intended behavior.
The data laws in EquipmentCheck now allow multiple independently needed pieces,
but still verify every field of every piece against the file and each original
form/cost/lifetime rule. RecipeCheck retains the process ranking and weighted
mix laws and adds the independent contributions to their expected figures.
ResearchCheck retains both date boundaries and now permits E1 to supersede
the old furnace, as required by the all-known availability call.

Focused checks through 1266adaa plus the fixture changes:
- HistoricalRecipeCheck: 83/83 green, including every row, both fitting orders,
  partial counts, saved choices in memory, actual clock, actual rail invoices,
  actual refinery flows, modern oxygen availability, and a Machine Works
  actually constructed before PE22 and later refitted through fitForCity.
- The exact same final harness, with all seven changed model classes restored
  byte-for-byte from main 4343a815 ahead of the remaining unchanged classes:
  48 FAILED of 83. All 35 row transitions fail; the real coal invoice, cracking
  yield, oxygen availability and actual completion/refit assertions fail too.
  Logs: target/R2d-evidence/before/final-{compile,check}.log.
- EquipmentCheck, RecipeCheck and ResearchCheck green in selected-2; its sole
  red was the oxygen fixture's uncaused price premise. The corrected fixture
  asserts the actual ranking price and both old-process comparisons first.
- HistoricalRecipeCheck and FittingCheck: selected-3 exit 0; no other building
  completed during either default or 1900 Bakery wait.
- RefineryCheck and RailCheck green in selected-1. No verdict or premise floor
  was lowered. No R2d entry existed in known-reds at the fork.

Added the independent thermal/catalytic price decision's real DataSave JSON
round-trip, changing actual ranking prices on both sides. Restored choices
stay thermal until choose() runs again. HistoricalRecipeCheck is 86/86 green
(check-4.log), and the identical final fixture on main model classes is
50 FAILED of 86 (before/final-check.log). Fast-4 is green, zero firm Stale.

Self-review found one dimension error before the first review: rapid-sand
production is water, so its fitting gain must use the water unit price.
The new fixture sets positive distinct utility prices and checks all three
utility pieces at their own unit. HistoricalRecipeCheck is 90/90 green
(check-6.log); restoring only the previous RecipeChanges class reproduces
exactly the rapid-sand failure, 1 FAILED of 90 (utility-before/check.log).
