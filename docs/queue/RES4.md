# RES4 - Aggregate parking capacity and bills

## Starts after

RES3 is on main. Supply the complete parking and recurring-cost interfaces HO1
needs. Coordinate Motoring/household changes with the active owners of those files.

## Expected source scope

Housing recipes/stock/quotes, `HouseholdBalance`, `Motoring`, shared parking
allocation/flows, `sectors/RealEstate`, `DataSave`/history/load, MoneyAudit and
CarCheck/HousingCheck/building/save harnesses. No individual bays or cars.

## Chosen rules

Private driveway/garage belongs in the housing asset/rent and private costs.
An included shared right is reserved even unused. Other available shared bays
are separately let and charged once. Extras require actual extra demand and
physical supply; the existing one-car ceiling is unchanged.
For each pool S=I+L+V. Private bays are outside that identity. Acquisition/release
of a home, ownership and transferable rights must be atomic. The initial shared
facility follows the common-cost pool when homes become condominiums; optional
receipts reduce its call. There is no separate speculative parking-owner class.

Use section 12's bay counts, 350 square feet including access, private/surface/
underground cost/work assumptions as initial inputs, then validate through actual
quotes and plots. Land, structure, upkeep and receipts have exactly one payer.
Keep included and optional capital allocations reconciled to the whole project.

Keep existing vehicles when bays are short and report unserved demand. This batch
adds no new no-bay prohibition to the car-purchase rule. Optional leases are paid
only when allocated; no imaginary free street-parking stock or lease income.
Actual quoted/paid parking belongs in household affordability and cash statements.

## Caused acceptance

Private/included/optional/extra billing; unused included reservation; full/empty
pool; partial rights transfer; no-car household; low/high demand; shortage without
deleted cars; exact capital and S=I+L+V; construction site/level capacity; allocated
recurring income and costs; common pool transfer; fractional quantities; reload.
Run the changed Motoring/housing/building/money/save checks and trace ensemble.
Commit measured lease demand, occupancy, fee effects and actual recipe variants;
100% bay occupancy is a sensitivity rather than an assumed revenue forecast.

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
