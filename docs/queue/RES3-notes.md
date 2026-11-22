# RES3 — home choice, typed rents and the 14-form catalogue

Work in progress on batch/RES3, based on shipped RES2 (7e484c4da).
No user city or save slot 1–9 is opened or written.

The implementation adds the section 12 recipe catalogue, aggregate cell/class
assignment and class rental ledgers. The existing builder quotes, work queue,
funding and mortgage path remain authoritative; no finish timer is introduced.
The model and its caused checks precede the final calibration report.

★ The initial area/quality/layout utility follows the numerical prototype.
Actual garden geometry and explicitly ground-level access add family/retired
terms. Unknown equipment supplies no extra amenity. Equivalent demand and offer
nodes coalesce and their allocation splits pro rata. Residual paths maximize
compatible affordable placements, retaining valid affordable incumbents first.

★ R1g's selected residential wage scaler is ported, including its saved twelve
monthly pay indices and its load rebuild, from origin/batch/R1g. Its demographic,
founding, research, advice and blocked GDP changes are excluded. Land is not scaled.
BO1-H remains a seam; this batch adds no business-owner seeker group. OG1's real
construction queue and work proofs are retained.

★ Saved class quotes, actual trade quantities/values, staleness and assignments
are flows that cannot be reconstructed from closing stock. They pass through
DataSave and the load path. A format bump and matching oldest-loadable floor
are owed under the old-saves rule: old class IDs/mixes and missing assignments
would read wrongly. The final format is set from shipped main after HC1/PW5
integration. No conversion code is added.

Initial baseline --fast passed before source edits (52 s compile; no firm Stale
finding). The first integrated compile exposed and corrected a pooled-weight
getter typo. Focused acceptance, integrated profiles and the final report follow.

## Findings while integrating

HousingChoiceCheck passed its caused cardinality, adults-only, affordability,
premium vacancy, family garden, retired accessibility, incumbency, fractional
demand, equivalent-tie/input-reversal and billion-fold count cases.
HousingInventoryCheck passed its existing native inventory/title/listing and
RN2 first-month continuation proofs with the new catalogue.

The first BuildingDataCheck run retained its exact dated-ID assertion and
correctly found the new housing dates. The expected date map now additionally
contains all fourteen forms; every existing pinned date and equality remains.

The integrated fixture initially ordered the entire catalogue onto an empty
city's yard at once: no project completed in 24 months. This is the real shared
work queue, not a finish timer. Its premise was corrected to stage the existing
founding playtest's houses/jobs first, then measure the catalogue-wide orders.
Both historical and present-day cases now cause paid starts, actual occupied
rent, conserved monthly pool/household money, exact class-state reload and exact
next-month parity. A final repeat will incorporate the cash-strike budget split.

HousingCheck caught two model integration defects. Quote-only classes with no
physical row were erroneously sent to the inventory's explicit occupancy setter;
only physical rows now receive assignments. Its strict conservation check then
caught 4.000000000000001 assigned to four Houses. The allocation now rounds only
within FamilyModel.packingRounding, caps the sequential sum to actual supply,
and leaves a genuine excess an error. Neither the strict title/occupancy guard
nor a conservation/save assertion was relaxed. Typed monthly matching clamps
listing subsets as the existing legacy match did, retaining non-RE occupants.

★ Quote-only unavailable classes are reported but cannot match, start or trade.
Their asks freeze until the existing research/calendar gate opens them. The
initial year cutoffs (1900 through 12 storeys, 1931 for ordinary/luxury towers,
1969 for the 80-storey residential form) are conservative catalogue calibration
choices, not claims about the first building of each height. Their reference
points are the owner's [Empire State Building history](https://www.esbnyc.com/about/history)
(1931) and the contemporary AISC [Architect-Engineer, 1971](https://www.aisc.org/media/l3mkj3lx/aae_1971.pdf)
description of the Hancock mixed office/apartment tower. No modern equipment
attribute is inferred from either; only explicit ground-floor access is scored.
These are pointers to evidence, not redistributed source data or illustrations.

★ Asks use Game.doorPriceOf and its actual taxed builder/work/material/land path,
including the order queue. Separately let parking's capital is excluded from
housing's initial gross-return anchor, while all actual project costs still
have a payer. Candidates receive one-count eligible demand, after standing
assignments and the funded pipeline; quantities use the same bounded allocation
and mortgage forecasts use its attainable income per requested structure.

★ Existing debt is still a sector obligation. Fixed capital weights, including
sites, allocate its carried interest once; class rent targets then cover only
their own allocated carry when tight. A glut class cannot pass its shortfall
to another class. The 0.058 rate is an initialization comparator, not a forced
actual return. BO1-H's absent owner-family seeker group remains its queued seam.

## Integration mends

The prospective-worker forecast now uses actual unfilled/funded jobs and the
current wage/tax schedule, rather than the budget of a current unemployed or
retired household. Standing homes and funded sites reserve eligible demand
once. Each candidate alternative reads the same remaining demand snapshot;
requested quantities and the mortgage desk use its attainable occupied income.
No candidate receives full-capacity proceeds merely because it has capacity.

★ The live pooled-price sharing passes are retired. A single monthly quote
feeds normal and emergency allocation phases. Only unplaced singles whose
pooled recurring resources fund a real remaining door enter sharing groups.
Working singles move into their existing shape/tier/stage shared cells;
seekers retain explicit sharing flows. The existing financial census moves
stocks, without resetting debts or balances. Saved bounded transfer records
retain pre-transfer counts/resources for prospective normal-home substitution.
There is no new permanent household preference axis.

HousingMarketCheck now causes both sides: affordable singles remain normal,
while five otherwise unaffordable budgets fund an actual home. People, bills,
sharing counts/resources and the native snapshot reconcile. Quote-only classes
retain their cost comparator until built; an unbuilt class is not vacant stock.
Equal residual preferences split one-count rent pressure without catalogue-order
priority. Zero posted asks remain valid instead of silently reinitializing.

InvestCheck passes with the new forecast. Its grouped-thousands hold-reason
fixture now supplies 10,000 House doors and explicitly posts their class ask at
zero: with typed affordability, spare unaffordable houses do not cause the
original no-beneficial-order premise. All original assertions remain. The
controlled HD1/RN1-H fixtures retain their original three-form menu; independent
catalogue checks exercise all fourteen forms. No conservation or save assertion
has been removed or weakened.

The catalogue fixture observes BuildLog completion events every month, including
other funded work completing in the window. Its initial workforce, supporting
infrastructure, land and equity are disclosed fixture inputs; all residential
work and settlement use the paid builder path. All fourteen present-day orders
and all historically available orders complete through actual work, and both
cities close the monthly money identities and native continuation exactly.
Final focused checks and the committed report follow.
