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

Final gate pending; focused evidence is recorded below.

## Row mapping and estimates

Every change's source string in buildings.json gives its arithmetic and the
study's source keys. R1c's legacy nine recipes and their sourcing stay in place.
The additions are 36 undos on 28 templates:

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

## First review and its mends (REV-R2d-1)

Orchestrator's first stranger reviewed 4343a815..8e7a2676 before readiness.
All three findings are mended, with causing fixtures:
1. Feed-weighted modern cracker yields could average to 0.5500000000000002
   and violate the solver's hard maximum. Bound the average to its actual
   input yields; a homogeneous input is exact. Do not loosen yields().
   One small plus five large modern units now produce exactly 0.55.
2. Seven underground mines, three without cutters and two without loaders,
   produced 328 rather than 329 posts from rounding the process first.
   Compose raw process and independent contributions, then round once.
   The fixture proves single and double rounding differ on its actual data.
3. The thermal cracker's export discount used modern petrol output. Pass
   the actual process into madeOf/madeAlone, preserving the old modern
   overload. The fixture has full positive feed, no home petrol room,
   and a profitable thermal order; the old appraisal wrongly rejects it.

Self-review's PE7 follow-up: main's site.dig is copper-in-ground per tonne of
concentrate. It rises from main's coefficient by the inverse 0.75 recovery,
so the lower output still depletes the same ore feed. Both mine sizes carry
the derived dig undo in the data. A mill on ample owned sulphide ground
actually extracts at both recoveries; field withdrawals match and modern
dig is restored exactly.

HistoricalRecipeCheck is 100/100 green. RecipeCheck, CopperMiningCheck and
RefineryCheck are green together: review-1-check.log, exit 0; zero firm Stale.
The exact final fixtures on all nine model classes from 8e7a2676 produce
7 FAILED of 100: the two PE7 per-row dig assertions, water-unit gain, early
rounding (328 vs 329), actual depletion (562.65 vs 750.2), the rejected
0.5500000000000002 flow, and thermal appraisal (-5087351.54 vs +673666.14).
Evidence: target/R2d-evidence/review-before/{compile,check}.log.

## Default traces

Main 4343a815 and branch f6c3996e each ran the full default 2405-month trace,
with earth-2.bin and world-1.bin at the repository data paths in the brief.
All eight CSVs are byte-identical (cmp): bank, bonds, borrow, build, house,
labour, month and pop. Main is target/R2d-evidence/main; the branch and
compare.txt are target/R2d-evidence/branch-f6c3996e. Main SHA256SUMS is there.
No ensemble: no default trace moved. After RT2 lands, compare the final gate
trace against its kept main trace too.

## Explicit slate mapping gap

Orchestrator confirmed this call on 2026-11-15: PE11/23/25/27 keep R2b's
conversion-unit dates; pre-PE11 crude units keep main's straight-run cuts.
Study 8.5's shell still has 63% kerosene, 15% fuel oil, 8% lube stock and
14% light products. The study does not split the light fraction or map lube
stock to the current stream types. No invented subfractions. This mapping,
like PE3, PE12 and PB3 above, is a call left for Jerus.

## Remaining integration checkpoint

No ship file yet. R2c and RT2 have not landed. Once they do: merge main,
retain R2c Recipes.gainOf unchanged, delegate independent equipment gross gain
inside EquipmentFit's founding guard and subtract R2c onGain on the result.
Add the agreed small LicenceFees.one hook for independent recipe fees, net
those fees in the independent price choice, and test both gross/net and
production capture. For PE24 capture, use the installed cracker's share of
the feed actually run, not its unused nameplate feed. R2c already uses that
basis for a conversion building's own fee. Default cities pay none.
Then regenerate maps, push final code, send "R2d mends at <commit>", publish a
provisional ship file, ask "R2d dry run?" and wait for go before the launcher
ALL dry run. Use a kept gate run and its final eight CSVs for comparison.

## Second review and its mends (REV-R2d-2)

The first model mends held. The next stranger found three remaining modern
readers: the build card, the city-margin models, and the refinery ribbons.
RefineryFlow now exposes the immutable resolved Process carried by each
solved Flow. Cards use the template's resolved process, the two margin models
value that process, and trace() reads the solved process's yields. The oil
table's yield words use the same getter. The modern standalone overloads are
preserved and retain the original yield list.

The requested grep also found OilView's planner solve without the process
parameter; its spare-feed read now uses the actual outlook process. The
remaining Kind.LUBE.yields() is the unchanged fixed lube yield constant;
other raw Kind readers are the modern definition or harness oracles.
R2c's installedUnitGain is the one remaining integration reader, explicitly
authorized for the post-merge hook; it must use the actual flow's process.

Thermal fixtures cause positive real feed and check the card's products and
value, both margin models, the oil table's 24% words, the actual petrol ribbon
and whole fuel-oil closure. A second price vector makes the thermal spread
negative and the modern definition positive, proving both city margins and
OilView's spare-feed appraisal keep the actual process. All 110 assertions
pass (review-2-fixture.log). The identical fixture on all eleven model classes
from e34200be fails exactly eight assertions, covering every reader above
(review-2-before/check.log). HistoricalRecipeCheck, OilViewCheck,
RefineryViewCheck and BuildCardCheck are green together: review-2-check.log,
exit 0, zero firm Stale.

The second complete default comparison, after all first-review model mends,
also kept all eight CSVs byte-identical to main 4343a815. Evidence:
target/R2d-evidence/branch-e34200be/compare.txt. No ensemble.

## Research-reader audit

ResearchView.recipesInUse used only base ids, so horses~PE20 was saved and
running but absent from the page. It now resolves saved selected variants,
while retaining known unchosen alternatives with zero running. Composite
names come from the catalogue's human discovery names; no internal save id
is shown. HistoricalRecipeCheck proves full and partial running counts,
the old crop figures and the human name; it also proves the known 1900
binder is listed while the unknown tractor and combine stay hidden.

On the prior 3a71398a ResearchView/RecipeChanges classes the same fixture
has exactly three failures of 115 (research-reader-before/check.log).
On the branch all 115 pass. DiscoveryCheck and BuildingDataCheck pass;
ResearchViewCheck has only the recorded sector-filter fault:
ResearchViewCheck: 1 FAILED of 217. Combined readers-mended-check.log exits 3.

Orchestrator explicitly approved the following narrow fixture repairs:
- The label "a piece not known at the month is not shown: the farms' tractor"
  formerly used noneMatch(template is Mixed Farm or Grain Farm), selecting
  every piece on those farms. It now uses the same template predicate AND
  piece id == tractor. A known binder is not an unknown tractor.
- The label "the tractor's two rows appear" formerly collected every piece
  on either farm and expected two rows. It now collects the same templates
  AND piece id == tractor, retaining the two-row, provenance, unfitted count
  and verdict assertions. The new binder/unknown-combine observation above
  covers the independent pieces rather than hiding them.
The labels are unchanged. From the sector-filter comment through section 6's
end, the block is byte-identical to 3a71398a (checked before writing).
No assertion was removed; ResearchViewCheck's count stays 217, so all three
existing R2b-V/R2b-J known-reds parts still match and need no replacement row.
DiscoveryCheck now permits E1 to supersede the old furnace after an early
local find, keeping both parts blocked the month before and without the
find. Its old history-only hearth/Bessemer gain comparison remains intact.

The data's readme now documents signed undos and their physical units.
A local provisional ship draft records PE3, PE12, PB3 and the slate gap;
it is deliberately not staged or pushed while fee integration is pending.

## Fee integration prototype (waiting for R2c and RT2 to ship)

Main remains 4343a815. An ignored target/R2d-fee-prototype overlay combines
R2d 7510fc00 with R2c 66708ce2; it changes no tracked model source. The
orchestrator approved the standing-value design on 2026-11-15 at 09:18.
The proposed EquipmentFit hooks preserve its default guard, use the
independent delta as gross gain and subtract onGain exactly once. The small
LicenceFees.one hook delegates independent free recipes to RecipeChanges.
Standing goods and consumables follow captured dispatch, fixed wages and
utilities follow R2c's standing roles, and PE24 uses only its template's
share of the feed actually dispatched. The building's own PE11 rent reads
the solved thermal Process. Recipes.gainOf remains R2c's unchanged method.

The scratch HistoricalLicenceCheck has 17 assertions, all green with the
hooks. The identical fixture against the three pre-hook model classes has
11 failures of 17 (before-17.log). It causes actual carbide-equipped machine
production, a flotation mill's production and settlement, and direct real
refinery stock production at the producer boundary. Flotation paid
441995.607166905 with a closed money audit and matching history/current
account. A local find and patent expiry both stop further bills. With two
thermal crackers the dispatched-feed building bill is 7600748.465522626;
the old modern-yield reader instead bills 17418536.955372340. Six catalytic
crackers process 2320680 litres against 13937946 installed; PE24's exact
incremental dispatched-feed bill is 9817788.489849713. No unused nameplate
feed is charged. All jobs used the explicit earth-2/world-1 paths.

The overlay's HistoricalRecipeCheck remains green. R2c's LicenceFeesCheck
has four integration assertions requiring an explicit fixture-scope call:
the expiry fixture compares the total of all firms, now including another
independent patented recipe; its default-fitting fixture assumes a hearth
still enables basic oxygen after E1. R2d's accepted E1 rule makes oxygen
available to either fitted status. These observations were sent to the
orchestrator before changing any tracked or scratch fixture premise.
The initially suggested Bakery replacement was withdrawn: that drive is
free in 1900 and cannot cause the required positive-patent condition.

Orchestrator's 09:26 fixture-scope instruction is implemented in the scratch
overlay for the post-merge commit: PE1 expiry gives all other entries local
knowledge and keeps total bills across all sectors. The real E1 case now
asserts that both fitted statuses can run oxygen and the hearth adds zero.
There is no other finite-gain required patented recipe: the only dated
primary piece is the tractor, whose FARM_HORSES gain is still NaN pending
R1d. A separate, explicitly synthetic required E1 process on the same
equipped steel-shop template supplies a positive gross/imported-fee control.
Both original default-gross assertions and the expiry assertion remain.
LicenceFeesCheck is green, 43/43. HistoricalLicenceCheck is now 20/20,
including positive real thermal production rejecting imported PE24 with no
fee. Against the pre-hook classes it has 11 failures of 20. RecipeCheck,
EquipmentCheck and DiscoveryCheck are green on the same overlay.

## Final rail coverage and producer boundary

RT2 shipped as 0.9.113, main 6b898500, and was merged in 3ee38970. Fast
check passed, zero firm Stale. Its protected Freight retirement methods were
retained. The latest main baseline's eight CSVs are
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RT2-ready2-20261115-094038/trace-*.csv.
R2c follows; final fee integration still waits for its shipped main.

The capacity audit found Rail Depot (id 141) besides the three templates in
PE21's catalogue touches text. It has 50000 railCapacity in 1900, so leaving
it unchanged supplied a diesel share before the entry. It now has the same
sourced PE21 change as Spur, Line and Terminal. Station capacity and all
top-level modern figures stay unchanged. Capital follows the same R1c call
10 estimate: quarter cash as machinery, 1860 work points (one tenth), 300
months. The current total is 36 undos on 28 templates. The exact new fixture
against the retained old 35-row payload fails twice of 120: Depot's date
crossing and its real pre-PE21 diesel invoice. Evidence:
target/R2d-depot-before/check.log and classes/buildings.json.

The idle money audit also caught the PE21 capture gap: rail templates make
capacity, not a Good, so R2c's goods-based productionRate is zero. The
orchestrator confirmed that private Freight pays; the public-berth exemption
does not apply. The prepared railService hook is at the common haul invoice,
after its tonnes/revenue are known and before its fuel draw changes marginal
prices. It freezes the actual boundary and city-leg fuel equivalent, the
capacity-weighted fitted share, and that invoice's per-litre saving; onGain
takes the same 25%. It books through Sector.bookLicenceFee into R2c's pending
imported-service ledger. No new payment route or Game field.

★ Prospective estimates keep R2c's rule: net 25% of their own estimated
gain, since dispatch is not yet known. Standing planners read the exact
saved bill. The orchestrator explicitly said to preserve the gross
estimator on 2026-11-15 at 10:23. Goods Production.finish keeps the rail
service's saved cost memo through the later goods capture; it never books
that memo a second time. Existing DataSave and sector JSON fields carry it.

Month-order proof: Game.nextMonth calls startOfMonthUpdate exactly once
(step 63), whose step 25 calls chargeFreight; that calls the common
Freight.haul(sectors, ports). Its one-argument overload delegates there too.
Step 30 then strikes/banks the sectors, including the frozen rail IP bill.
SimulationEngine follows; its later goods capture preserves the rail memo.
The clock-button and simulateMonths fixtures each run three real months,
requiring positive haul tonnes and revenue, a positive bill equal to the
saved per-building total exactly once, and closed audit/current account.

The prepared fee fixture is now 43/43, versus 18 FAILED of 43 with the three
pre-rail model classes (before-rail-43.log). It also has a mixed fleet
(partially fitted Line), positive city and boundary hauls, the exact
quarter-share formula, the partial stack's own memo, and a JSON round trip
of the actual pending bill and standing memo. This found two additional
details: capture before the fuel draw, and city diesel litres excluding the
steam share. Before those mends, four of 43 fail (mixed-rail-before.log).
The corrected overlay passes all 43 (rail-mended-43.log). Source snapshots
and the frozen initial fee review patch are in target/R2d-fee-prototype.
The later rail delta is reviewed separately; it preserves both RT2
retirement methods and changes no Game/Sector method.

The merged branch's HistoricalRecipeCheck (120), EquipmentCheck and
BuildingDataCheck all pass together; depot-check.log exits 0, with zero firm
Stale. The original data payload and before log remain retained.

After merging the final RT2 Sector/BusinessInvestment/Freight source into
the overlay, HistoricalLicenceCheck (43), HistoricalRecipeCheck (120),
LicenceFeesCheck (43), RailCheck and HomeRailCheck all pass serially.
Logs: target/R2d-fee-prototype/RT2-*.log. The rail delta review artifact is
rail-fee-review.patch, SHA256
d997eacf48f1b21008c6c84c6c2375817f2b1a324fa2db5c7a5b922410a00a5b.
The initial prepared-fee-review.patch (9ca4a328...) remains frozen.

## Default trace after all 36 rows

Branch 4ffacbc7 (model source d1ec65ee), on main 6b898500, ran the complete
default LongPlaytest with earth-2/world-1: exit 0. All eight CSVs are
byte-identical to main's RT2-ready2 retained trace. Evidence:
target/R2d-evidence/branch-4ffacbc7/playtest.log, compare.txt and SHA256SUMS.
No ensemble. PE21's catalogue touches text now includes Rail Depot too;
this is a display label, matching the fourth actual rail-capacity template.

## Fourth review: demolition and the municipal exemption

REV-R2d-4 found that closeDemolished can remove an unfitted unit after
recipes cached the engine mix, before the invoice. Two equal units, one
unfitted, become one fitted unit while the fuel draw still uses 50% steam.
Reading the new fitted count for rent would bill twice the actual benefit.

The prepared hook now receives the very steam share Freight uses for its
fuel draw. It allocates that actual diesel-equivalent quantity among the
templates by their cached diesel-capacity share, never reconstructing the
invoice from later fitted counts. No Game or Sector method changes.
A real buy-out/demolition fixture supplies working builders, applies the
mixed recipe, orders buyOutAndDemolish, calls the actual closeDemolished,
and requires one fitted unit left with the cached half-steam invoice. It
draws exactly half diesel; rent and its saved standing memo both follow
that same actual draw. On the reviewed d997eacf model it bills
6116.445225065157 against an actual benefit fee of 3058.2226125325783.

The same audit found that the independent fitting hook netted fees from
public PE5/PE6/PE16 pieces while R2c deliberately bills private firms only.
The orchestrator confirmed the municipal exemption: non-Sector templates
keep their full gross estimate, with no bill. R2c-P's public payment route
takes these water/power templates too. The three controls require positive
gains during active patents, full-gross verdicts, and no prospective or
actual firm bill. A private PE21 control retains its 25% estimated rent.

HistoricalLicenceCheck is now 57/57. The exact same fixture on the reviewed
pre-mend model classes fails five of 57: the three public net estimates,
the demolition bill and its memo. Evidence:
target/R2d-fee-prototype/review-4-before/check.log and review-4-mended.log.
The delta is review-4-mends.patch, SHA256
31f9f19ddbefc187120bb2b8641d7fbcc8ee95b7a8dba8ef92e18477dd120634.
These remain prepared model files, to be applied after R2c reaches main.

The label follow-up passes ResearchCheck and DiscoveryCheck; ResearchView
has only its existing filter fault. depot-research-check.log exits 3, zero
firm Stale. No assertion or filter premise changed.

The adjacent last-depot fixture closes the sole depot through the real
buy-out/demolition path, then requires Freight's positive prior-month
invoice to pay even though no current asset can receive a cost memo.
The allocation guard had dropped this final bill: 0 against an actual
7303.653242228116 (two failures of 60). The helper now computes PE21's bill
once from the passed invoice diesel snapshot first. Only its standing memo
is allocated among remaining cached diesel capacity; allocation is not a
condition for owing the actual imported service. This is the same PE21
gain/25% rule, with no new figure or payment route. The orchestrator accepted
this case in the same seam. HistoricalLicenceCheck is 60/60;
all-closed-before/check.log and all-closed-mended.log retain the evidence.
