# RES2 - Aggregate housing inventory

## Starts after

The finalized RES1 design is published. This is the first new housing foundation;
HO1 cannot use the former two-segment capacity-share contract after this chain.
Coordinate RN2's reload proof and BO1-H with their owners; neither finding is dropped.

## Result

Separate completed structures from their dwelling mix and ownership/occupancy.
Keep the existing three residential templates compatible before activating new forms.
BuildingManager/BuildingsStacks remain physical authority; a completion adds its
mix once. The founding 100 places remain a separately named non-saleable endowment.
Derive old size totals and owned-capacity reports from the registry.

## Expected source scope

`BuildingsTemplate`, `BuildingCatalog`, `BuildingManager`, `BuildingsStacks`,
aggregate housing stock/assignment classes as needed, `sectors/RealEstate`,
`DataSave`, the `Game` load rebuild, and focused model/save harnesses.
No new sector, no renumbered building IDs, no luxury-form activation or UI here.

## Contracts supplied

- Immutable recipe metadata: form, dwelling mix, capacity, area/bedrooms/quality,
  private/included/shared rights and fixed cost weights.
- Physical stock and pipeline by recipe/cohort; occupied/vacant and ownership
  quantities by class; listings are subsets rather than additional homes.
- Pure compatibility getters, atomic quantity transfers, aggregate completion
  events and save/rebuild. Quotes/loading never advance the economy.
- Asset/collateral book allocation interface that a partial sale can consume;
  HO1/RES5 decide and settle sales. No sale changes land/material/structure.

## Caused acceptance

Reproduce existing doors/capacity including founding capacity; mixed completion;
zero and fractional quantities; one completion event; 1000x represented stock
with fixed state dimensions; ownership/occupancy conservation; split/merge;
save/reload and RN2's changed-capacity first-month debt allocation. Include
HousingCheck, HousingDoorCheck, BuildingDataCheck, SaveFileCheck, ReadPathCheck
and affected reload checks. Measure whether compatibility preserves the trace;
only claim byte identity if observed. Publish the actual getters for RES3/RES4/HO1.

## Authority and bounds

Jerus, 2026-11-21: "ok so now do what you must to finish up the study, so that ho1 is green lighted".
This authorizes closure of the study and its recommended implementation direction.
The controlling design is [RES1 sections 8, 10 and 14](../studies/spec-real-estate.md).
The numerical catalogue is an initial implementation baseline; completed game outcomes
and historical/rate calibration are proved in the named model batches.
Everything is aggregate: bounded physical stocks, household-cell assignments,
ownership, loan/listing cohorts, parking capacities and common-cost pools. No
per-person, per-home, per-building, per-mortgage or per-bay identities.
Reuse existing household cells (141 in the measured snapshot); roughly 180 cells
and 32 form/dwelling classes across 14 forms are the initial dimension budget.
Temporary disjoint funding regions are solver workspace, not saved household cells.
Equivalent cohorts coalesce; count scaling cannot create more records.

Read the current maps and contract before source. Cut from then-current main after
predecessors ship. Refresh overlapping batches before selecting files. The model,
save, caused checks, money/save review and ensemble precede UI. Use scratch cities;
old saves get no conversion code. Measure actual row counts and integrated cost;
the 0.864 ms synthetic kernel is a reference fixture, not a final speed guarantee.
