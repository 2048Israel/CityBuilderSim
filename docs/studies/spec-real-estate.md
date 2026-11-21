# Real Estate: buildings, dwellings and the households choosing them

RES1, opened 2026-11-20; extended during review on 2026-11-21. **Design
study and numerical proposal; catalogue calibration remains unfinished.**
The earlier completed-study label overstated that work. Section 12 now puts
the complete numerical proposal, assumptions and reproducer together. Branch
`study/real-estate`, model base `a9bfcd070`; fetched `origin/main` through
`e9dadb540` before measuring. The later queue clarification at `4f1056f11`
records RES1 as docs only and carries the planner finding into OG1. Only
this study and its index change here.
RES1 names the docs study; RES2-RES5 below are proposed implementation
batches. The earlier REU1 note called RES1 the future model batch; update
that dependency rather than reuse a shipped study's ID.

## 1. The recommendation

**Aggregation is a requirement throughout.** Jerus clarified on 2026-11-21:
"everything should be handled in aggregates no?" Buildings, dwelling types,
household occupancy, ownership, mortgages, listings, parking and common-cost
accounts are counts, shares and money stocks/flows in bounded cohorts. There
are no individual building, dwelling, household or parking-space identities.
The earlier draft's stable building keys and per-building account splits
were too granular and are replaced below.

Separate a physical building from its homes, and a home's owner from its
occupant. Offer houses, attached homes, walk-ups and taller apartments, with
several layouts and quality levels. Some apartment forms have a mix of homes;
a penthouse is a limited top-floor home, not a synonym for every expensive
apartment. Space, private outdoor space, quality, amenities and parking can
matter differently to households that can afford them. No tier is confined
to a form.

First establish that inventory, then fit households and price the actual
homes, then account for parking. HO1 must buy and sell those homes and fund
their common costs from its first apartment sale. After HO1, intentional
disposal lists standing homes for sale and progressively lowers unsold asks.
It never scraps housing to leave an investment. Keep tenant occupancy counts
while the stock is marketed. Keep the cohort's shared-cost pool after RE's
owned quantity reaches zero.

This changes the interfaces in HO1 substantially, but keeps its accepted
credit rules and rate experiment. It does not solve the builders' backlog by
adding building pictures: the current planner already orders quantities.
OG1 owns that investigation. REU1 follows the model and draws its saved
figures, as Jerus requested.

Accepted instructions are separated from recommendations in section 8.
The proposed catalogue is concrete enough to test, but its quantities,
areas, heights, quality premiums and parking ratios are **candidates, not
Jerus's decisions or calibrated game data**.

## 2. What was observed in the model

### 2.1 Inventory and construction inputs

The shipped residential IDs are 0, 1 and 6. `BuildingsTemplate.homeSize()`
divides capacity by dwellings; each template supplies one size. There is no
bedroom count, mixed-unit schedule, dwelling quality market or residential
parking capacity in this base.

The first table is measured from `BuildingCatalog.load()` and template
getters. Structure is cash plus materials at `Good.MATERIALS.worldImportPrice()`;
the second money column divides out the model's founding price-level factor
to show the catalogue's present-day benchmark. It is not a live city quote:
land, local wages/material prices, construction tax/rebates, finance and
waiting time are excluded.

| Form | Doors | Capacity per door | Structure per door, model thousands | Present-day benchmark dollars | Lot sq ft per door | Work points per door |
|---|---:|---:|---:|---:|---:|---:|
| House, id 0 | 1 | 6 | 11.544025 | 446,000 | 8,000.00 | 10.00 |
| Studio Apartments, id 1 | 80 | 2 | 4.655141 | 179,850 | 312.50 | 8.50 |
| Low-Rise Apartments, id 6 | 63 | 4 | 6.977031 | 269,556 | 952.38 | 38.10 |

The model's delivered materials price in this probe was 0.465902349 world
thousands; its present-day table value is 18. The different money units must
not be mixed when proposing new costs. Power per door is respectively
1.0000, 0.1000 and 0.3968 model units; water is 0.2000, 0.0250 and 0.0952.
These are model inputs, not measured household kWh or water efficiency.

An apartment is cheaper per door here, but the low-rise consumes 3.81 times
a House's construction points per door. That is evidence to examine its work
recipe, not proof that every real apartment takes longer. Construction points
also compete for the shared queue; no template has a guaranteed completion
time. Existing lot figures alone reveal neither floor area nor storeys.

`BuildingManager` keeps quantities in `BuildingsStacks`, not an object for
every standing home. Its total capacity also includes 100 founding places
per endowment, which its `homesBySize()` door census does not contain. Do not
turn that difference into saleable houses or duplicate it in a new registry.

### 2.2 Caused placement and pricing probes

These call the current `FamilyModel.house()` after restoring explicit counts
through its public named-shape save interface. They are small model fixtures,
not a reimplementation of matching. All use one household unless stated.

| Fixture | Current result |
|---|---|
| Single parent and child; one size-2 door | 1 unplaced; 1 refused by the studio rule; no rent weight |
| Same household; one size-3 door | Fits; family rent weight 3 |
| Same household; one size-4 door | Fits; family rent weight 4 |
| Six-person large family; one size-4 door | Fits in the crowding pass; 1 crowded household; rent weight 4 |
| 200 single adults, all lowest tier; 100 size-2 and 100 size-6 doors | Studio weight 200, family weight 600; none unplaced |
| Same stock, all elite, or 100 lowest and 100 elite | Exactly the same weights and placement totals |

The source explains why: `FamilyModel.match()` pools each household SHAPE's
tiers, places larger shapes first, takes the smallest fitting home and then
crowds into remaining homes. Dependants cannot enter size <= 2 in either
pass. The probe does not show which individual tier got a particular door;
the current matching does not retain that assignment.

With the family per-capacity rent set to 2 solely to identify the pricing
path, `RealEstate.priceForSegment()` returned 2 for the House, Low-Rise and a
synthetic expensive four-place family template alike. The latter is a probe
object, not a new catalogue entry. The studio returned its separate price.
An expensive or luxury template alone therefore creates no quality rent.

### 2.3 A scratch city: shortages, affordability and cars

One bounded measuring JVM, `-Xmx1024m`, ran after the orchestrator cleared
the laptop and committed memory was 72.2%, below its 75% start limit. It used
`GameFiles.scratch("res1-housing")`, the supplied main data paths,
`LongPlaytest.probeCity()` and `probePlay()` with today's founding and default
path seed 48,213. The helper's advice/extra months pass its requested targets
60/240/600, so the actual sample months are **65/245/605**. This observation
run is not the default full playtest trace or an ensemble. Exit 0 says the
probe completed; it is not a suite verdict.

| Actual month | People | Door seekers | Finished doors | Doors on site | Doubled households | Crowded households | Cars | Rent / aggregate take-home | Proposed build quantity |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 65 | 568 | 187.34 | 139 | 0 | 48.34 | 0 | 6.45 | 24.02% | 1 |
| 245 | 2,027 | 600.12 | 520 | 0 | 107.77 | 0 | 23.38 | 26.15% | 2 |
| 605 | 7,441 | 2,771.06 | 2,867 | 273 | 0 | 84.48 | 522.26 | 27.85% | 142 |

Unplaced after the current valves was zero at all three samples. The finished
size census at 605 was 960 size-2, 1,701 size-4 and 206 size-6 doors. It can
have more doors than seekers and still crowd some large households: one city
total is not a sufficient measure of fit. At 245 the corresponding counts
were 80, 252 and 188.

The last column calls the actual `RealEstate.plan()` on each snapshot. It is
**proposed demand, not an accepted, financed or completed order**. At 605 its
reason was 850 capacity needed for demand plus headroom against 10,060
capacity. The 142 proposal disproves the supposed one-building housing cap;
it does not identify which downstream gate would accept that proposal.

At 605 the working unskilled row's rent/take-home was 25.58%, the skilled
row's 21.87%, college's 9.19% and elite's 2.91%. These are ratios of row
totals, not medians or the share of families in hardship. Aggregate take-home
is `HouseholdAccounts.getDisposableIncome()`: wages less tax/contributions/
premiums plus pensions, benefits and grants. Neither this ratio nor a gross
income threshold replaces a household's actual residual budget.

Cars were 3.44, 3.90 and 18.85 per 100 door-seeking households at these
samples. `Household.cars` remains capped at one per household. The city
already has new/used car trading, car credit, fuel, transit choice and road
effects, but no residential bay stock to compare with those cars. One growing
city at 605 cannot calibrate mature parking demand. Extra parking capacity
does not authorize inventing extra cars to fill it.

### 2.4 Rent and disposal paths read from source

`EconomyManager.repriceHousingCosts()` selects the cheapest per-capacity
new supply overall and within studio/family segments. `rentRequired()` uses
`LANDLORD_YIELD = .058`; carry includes maintenance, property tax, interest
and payroll over owned capacity. `targetFor()` applies scarcity and a carry
lift, except that a glut can waive the lift; `LEASE_MONTHS = 12` moves rents
gradually. `BUILD_MARGIN = .33` and `MAINTENANCE_PER_YEAR = .01` already exist.
None is a new calibration from this study.

`RealEstate.mayRetire()` blocks while families are unplaced or doubled and
otherwise checks spare doors in the holding's segment. `Game.retire()` then
scraps the building, releases/sells its land, sells material and settles its
financing. Its "Sold" message does not mean a standing house changed owner.
The orchestrator queued the wording correction separately as SD1; this study
specifies the later change in economic behavior.

## 3. Outside evidence and its limits

These sources ground distinctions, not universal prices or preferences.
They were checked on 2026-11-21. No external dataset or copied publication is
added to game data by this docs branch.

| Source | What it supports here | What it does not establish |
|---|---|---|
| [CMHC, National Occupancy Standard](https://www.cmhc-schl.gc.ca/professionals/industry-innovation-and-leadership/industry-expertise/affordable-housing/provincial-territorial-agreements/investment-in-affordable-housing/national-occupancy-standard) | Bedrooms, relationships and ages affect suitability; a lone parent normally needs a bedroom separate from children. CMHC explicitly treats this as a suitability indicator, not a rule excluding tenants. | An exact NOS for model families that have no children's sex or individual room-sharing data; a new legal studio ban |
| [Statistics Canada, shelter costs and affordability](https://www150.statcan.gc.ca/n1/daily-quotidien/171025/dq171025c-eng.htm?indgeo=0&indid=14429-2) | The Canadian 30% indicator uses total before-tax income and includes housing utilities, taxes and condo fees. | A rent ceiling, a 30% take-home target, or a mortgage approval rule |
| [CMHC, GDS/TDS calculation](https://www.cmhc-schl.gc.ca/professionals/project-funding-and-mortgage-financing/mortgage-loan-insurance/calculating-gds-tds) | Its limits are 39%/44%; qualifying payments include insurance-financed principal, taxes, heat and 50% of condo fees. | Permission to charge only half the actual fee to owners, or to double-count heating included in a fee |
| [Nova Scotia, condominium owners](https://www.novascotia.ca/condominium-owners-your-rights-and-responsibilities) | Common property and exclusive-use rights differ from the unit; fees go to the condominium corporation, assessments follow declared shares, and a manager is a separate service. | An automatic permanent rent to the developer after sale, or a management profit percentage for the game |
| [Vancouver, 2023 parking study, appendix E section 4.2](https://council.vancouver.ca/20231115/documents/cfsc1.pdf) | Its underground parking construction estimate is CAD 60,000-80,000 per stall including aisles but excluding other service areas; CAD 80,000-120,000 includes those areas. Ramps and layout make costs nonlinear. | A 2026 universal garage price, the cost of a driveway, or a reason to count storage/plant rooms twice |
| [Reserve Bank of Australia, The Apartment Shortage, section 4](https://www.rba.gov.au/publications/rdp/2020/2020-04/full.html) | Its Australian 2013-18 building data show a modest positive average cost/height relationship; they do not support making height alone overwhelmingly expensive. | A Canadian tower recipe, a universal height multiplier, or the economics of every luxury tower |
| [432 Park Avenue, sponsor's residence schedule](https://www.432parkavenue.com/assets/data/432_availability.pdf) | The published full-floor residence has approximately 751 sq m of interior space (8,088 sq ft). Individual apartments can substantially exceed the first 110/140 sq m examples. | A normal apartment size, a recommended luxury share, or a game construction/management price |
| [NYC-hosted, 2020 Lower East Side planning submission, pages 2-3](https://www.nyc.gov/html/mancb3/downloads/calendar/2020/RWCDS-form-3rd%20draft-7.9.20.pdf) | It describes One Manhattan Square as 72 storeys and 815 dwellings. The previous 20-storey/160-home example is not a maximum residential form. | A universal relationship between height and unit count; marketing floor numbers; a recipe for an 815-home game template |

The NOS and mortgage figures above are facts about their sources, not new
rules being imposed on this game. CMHC's actual cash budget and its qualifying
ratio must remain distinct. If later batches import data, check and record
the dataset's commercial-use licence then; a citation here is not a licence
for redistributing tables or plans.

## 4. The proposed housing model

### 4.1 Aggregate buildings, homes, ownership and occupancy

Extend the current aggregate stacks with a housing stock table.
`BuildingManager` remains authoritative for quantities of completed structures
and construction. A building cohort groups a template/form, a bounded vintage
or cost band and shared-service regime. Its building count times the template's
unit-mix vector gives dwelling counts by layout, capacity, area/quality and
included-parking category. Height describes the form; a penthouse is a limited
category in its mix. Catalogue geometry does not create runtime floor objects.

Use sparse counts connecting household cells (shape, tier, stage and a small
preference/eligibility band where needed) with housing categories and tenure.
Keep RE-owned, household-owned, occupied, vacant and listed quantities, plus
the associated cost/debt values. Ownership and occupancy remain distinct
aggregate dimensions. A listed rental is still owned and may still be
occupied; listing it never adds another home. Before HO1, ordinary housing
is RE-owned; business-owner families are occupants, not homeowners.

A sale transfers a quantity from RE's ownership bucket to household ownership,
with matching money/debt flows. It does not allocate an address, a serial
number or an apartment object. "Sell individual apartment units" describes
the economic unit of sale, not the storage model: a cohort can contain 1,000
identical apartments, 600 household-owned and 400 RE-owned. Partial ownership
does not split that into 1,000 records or require locating each owner in a
particular physical building. Fractional counts keep the current aggregate
model's interpretation as expected household/unit quantities.

Use fixed, bounded category definitions and coalesce equivalent state. Do not
let a unique closing date, exact quote or account identifier create a new
per-transaction/per-building cohort indefinitely. Mortgage/arrears and listing
age/ask bands may retain relevant distributions; save their counts and money
sums, with controlled banding. A matching flow is between cohorts, not a loop
over their represented households. Incumbent retention preserves occupied
counts and their housing category; no resident's identity is required.

Required invariants, also asserted after save/load:

- Sum of homes by type equals building count times the cohort's mix; capacity is
  the sum of each type's count times its normal capacity. The founding
  endowment remains a separately named, non-saleable capacity adjustment.
- RE-owned plus household/estate-owned homes equals physical homes. Occupied
  plus vacant equals physical homes. Listed is a subset of those states.
- Assignments consume one home per household, apart from the explicitly
  recorded sharing/crowding valves. A home or parking right cannot be used
  by two matches.
- A sale changes owner counts/shares, allocated asset/debt books and fee
  contributions. It changes no building quantity, capacity, land or material.
- Common-cost weights cover all units in a cohort, including RE's unsold
  quantity. A sale transfers the relevant weight between owner groups; it
  never enlarges the remaining units' per-unit weight.
- Splitting then merging otherwise equivalent aggregate records preserves
  counts, rights, carrying costs, debt and settlement flows. State size and
  work scale with the bounded categories/occupied cells, not represented homes.

The existing two size totals become derived compatibility/reporting views.
Do not keep an independently mutable owned-capacity total beside a registry
that can disagree with it. Retain RN2's first-month-after-load debt-share
regression when replacing that field's authority.

### 4.2 Forms and layouts worth offering

Recommended first design menu below. Areas are internal home areas; storeys
and counts are experiment candidates. The same size may have ordinary or
premium finishes. No new catalogue IDs are assigned by a study: append IDs
when building the accepted recipes, and never renumber existing ones.

| Form | Candidate envelope | Homes and intended distinction |
|---|---|---|
| Compact house | 1-2 storeys; one 45-75 sq m home | A small household can buy private space without buying a six-place house |
| Family house | 1-2 storeys; one 90-160 sq m home | Two/three bedrooms, garden/driveway choice; retain the existing House as the measured comparator |
| Large house | 2-3 storeys; one 180-280 sq m home | More bedrooms/private space and larger plot; high comfort does not have to mean more occupants |
| Duplex / terrace | 2-3 storeys; 2 or 6 homes | Attached family housing with less land per door; unit schedules can remain uniform |
| Walk-up | 3-4 storeys; test 12-24 homes | Lower common-service burden; family-only and mixed variants |
| Mid-rise apartment | Test 8 storeys, 64 homes | A mixed variant: 16 compact, 40 family and 8 large homes |
| Mixed tower example | Test 20 storeys, 160 homes | A worked mix: 48 compact, 96 family, 12 large and 4 penthouses; one ordinary-sized tower variant, not the top of the catalogue |
| Large apartment block | Test 8-15 storeys, 200-500 homes | A wider footprint can provide many homes without extreme height; mix and site area still to test |
| Larger high-rise | Test 30-50 storeys, 250-600 homes | Higher-capacity variants, plus lower-unit-count variants with larger homes |
| Very tall residential tower | Test 60-90 storeys, 500-1,000 homes for denser mixes | A larger-scale option requiring its own core, services, construction/maintenance and site costing; large-apartment variants may contain fewer homes |

Jerus pointed out that larger apartments exist after reviewing the initial
table. Expand both axes: the number/height of homes in a building and the
interior size of a dwelling. Neither 160 homes nor 140 sq m is a cap. The new
ranges are candidate envelopes for discussion, not approved/calibrated recipes
or measured model capacities. The worked 64/160-home mixes below remain
ordinary examples whose geometry was checked; the larger forms need the same
physical and economic checks before activation.

| Dwelling size/variant | Broader candidate internal area | Role in the menu |
|---|---:|---|
| Small apartment | 35-60 sq m | Compact accommodation; the 40 sq m worked example remains available |
| Family apartment | 70-110 sq m | Family layouts with distinct bedroom/room-sharing targets |
| Large apartment | 110-180 sq m | More family or personal space; ordinary and premium finishes can differ |
| Very large apartment | 180-300 sq m | Spacious homes, including premium variants; not restricted to one household tier |
| Full-floor / exceptionally large apartment | 300-800+ sq m | A low-count option where the building's floorplate and costs support it |
| Penthouse / top-floor variant | For example 140-800+ sq m | Floor position/amenities are separate from size; can span several size bands and, where designed, more than one floor |

These are bounded catalogue categories represented by aggregate counts and
representative sizes. They do not create custom floorplans or a record per
home. Bigger area is not automatically more residents or better finishes;
normal occupancy, layout and quality stay separate. Penthouses need not be
larger than every other apartment. The source examples establish existence,
not that exceptional sizes should become common or universally cheapest.

For the original two mixed examples, start geometric probes with compact 40 sq m /
2 normal places, family 75 / 4, large 110 / 6, penthouse 140 / 6. The mid-rise
has 4,520 sq m of private home area; at 80% net/gross efficiency it needs
5,650 sq m gross, about 706 sq m footprint across eight floors. The taller
example has 11,000 sq m private area. A 760 sq m gross floorplate with at most
75% private area leaves 570 sq m per floor: four 140 sq m penthouses fit on
the top floor. A small packing prototype fits the other homes into nineteen
floors, maximum private area 565 sq m: two floors with five large homes;
thirteen with seven family and one compact; one with two large, four family
and one compact; one with one family and twelve compact; and two with fourteen
and eight compact respectively. It is an area-feasible schedule, not a
structural, access or building-code design. The mid-rise can repeat one large,
five family and two compact homes per floor, exactly 565 sq m private area.

Every accepted recipe must reconcile unit area + circulation/core/plant,
floor-by-floor capacity where constrained, building footprint, plot and open
space. Surface parking must fit the remaining plot including aisles/access;
underground parking needs excavated levels, ramps and construction work.
"Height x footprint" cannot silently become all rentable floor area.

Bedrooms distinguish normal suitability from a simple headcount. Recommend
studio, one-, two-, three- and four-plus-bedroom categories, with room-sharing
targets inferred only as far as `FamilyStructure` supports them. A couple
and two children need a different layout from four unrelated adults. Report
known bedroom shortfalls and uncertain cases; do not invent sexes or exact
NOS compliance. Keep the existing adults-only studio rule until its
replacement is explicitly accepted. A less suitable home can remain a
recorded emergency compromise, as current crowding already is.

### 4.3 Choice, affordability and stability

Match by household cell (shape, tier and available stage), retaining the
assignment's dwelling type and tenure. First keep valid incumbent tenants
and owners. New households and required movers then compete for available
homes; maximize placements with suitable affordable homes before using
preferences to distinguish alternatives. Continue explicit sharing/crowding
outcomes and their reasons. Pro-rata ties and deterministic keys must not
make enum order or a rich tier's early pass a hidden entitlement.

For a voluntary move, compare the FULL recurring housing bill with regular
take-home left after the household's essential basket and existing fixed
obligations/debt service. Count separately charged parking and utilities;
avoid counting anything already in rent or the common fee again. Wealth
can supply a down payment and a finite cash buffer; it is not permanent
monthly income. Existing shortfalls use the household funding/arrears paths,
not a rule that evicts every household on the first unaffordable month.

Among feasible homes, preferences trade space/layout, private garden,
quality, accessibility and shared amenities against cost. Recommend a small
set of preference profiles within cells rather than an elite-only housing
class. Higher resources permit more space or quality; a wealthy retired
couple can prefer an accessible apartment while an affluent family prefers
a garden. A penthouse preference can fall back to a luxury apartment or
ordinary home. Profile shares, utility weights and voluntary move pace are
open parameters; none has been fitted here.

Owner-occupier counts retain their housing category when their tier or stage
changes. A household change may create mover demand; it does not automatically
sell capacity and credit cash. HO2 carries aggregate ownership and mortgage
stocks with the household flows and closes a sold quantity only against
funded buying demand. The unsold quantity keeps its carrying obligations.

At death or departure, transfer unsold quantities, debt and carrying cash to
estate/outbound-seller cohorts until settlement. Do not leave those stocks
in an empty household cell where normal folding would hand them to unrelated
residents. Actual net proceeds flow to heirs or across the border when paid.
A cohort without cash accrues the proper arrears/default claim; its unpaid
fees are not invented income for the common-cost pool. No separate record
for each deceased or departing household is introduced.

Measure unmatched demand separately as no suitable layout, no affordable
offer, no physical vacancy, or a home still on site. Also show crowding,
voluntary movers and vacant premium stock. A household considering three
alternatives is one household's demand, not three shortages.

### 4.4 Rents and sale values by the home offered

Use a bounded set of comparable dwelling classes, including size/layout,
quality and included rights. Buildings can contain several classes. Quote
rent and sale price **per dwelling**, with per-capacity figures as derived
comparisons; charging by residents would make babies pay rent again.

Allocate structure, land, shared facilities and included parking to homes
with explicit, reconciled weights. Replacement-cost comparisons must retain
costly quality and yard differences. The current gross rent anchor is cost
times `.058 / 12`; preserve it as the initial comparator, rather than silently
redefining `.058` as a net yield and adding every existing operating cost on
top. Fit any replacement anchor against the measured current burdens first.

Each comparable class has its own effective demand/vacancy signal and lagged
rent, with substitution through the choice model. Keep the current principle
that scarcity can raise rents beyond affordability and a glut can lower them
below cost. Actual budgets limit purchases and voluntary moves; they do not
cap the landlord's asking rent. Check any carry lift by comparable stock and
retain the glut waiver, so empty premium units cannot force unrelated
ordinary tenants to cover their target return.

For HO1's value, use the rent the particular home could earn less its actual
operating burden, property tax, vacancy allowance and allocated management
cost. Deduct shared costs once. Do not subtract mortgage interest before
capitalizing this net operating income; the cap rate already prices the
investment and its rate sensitivity. Separate optional parking cash flows
from the dwelling unless the relevant space/right is transferred with it.
Keep HO1's chosen cap-rate pass-through of 0.4 as its experiment baseline.

Recommend settlement between aggregated buyer cells and listed quantities
in housing/ask bands, respecting credit, budgets, preferences, incumbency and
parking rights. Unmatched quantities stay listed; allocation must not count
the same buyer demand against several classes. Each matched flow settles at
its accepted class offer; the transaction index uses traded quantity and
value. There is no individual bid or contract object for every buyer/home.
HO1's supply/demand calculation can guide normal offer formation, but must
not force all offered stock to sell. Its estimated twelve-month price lag
belongs to normal quote updates, not to changing an agreed closing price or
raising an unsold exit ask. Re-measure HO1's rate experiment with this market;
the old homogeneous prototype's elasticity is not a new model result.

### 4.5 What builders choose, and what density costs

Start with finite, physically checked unit-mix and parking variants, not an
optimizer free to create an arbitrary number of tiny homes. Evaluate how many
homes of each type could actually fill, including competing existing vacant
homes, tenure and the pipeline at expected opening. Price the complete mix's
expected occupied rent or sale proceeds against the complete project's cost.
Count an unlet penthouse as carrying stock, not full rent at completion.

Cost every variant through the real builder quote, materials, local wages,
land, construction points, queue wait/interest, staffing, utilities, tax/
rebates and maintenance. Check lifts, pumps, corridors and shared facilities
where a recipe includes them; do not add a generic tower penalty on top of
costs already in those inputs. The density experiment in section 7 shows
why neither "tall is cheapest" nor "tall is always uneconomic" follows.

Retain quantity planning. If a single template selection per month becomes
a measured barrier to serving different shortages, coordinate a portfolio
decision with OG1 instead of rewriting the common order API speculatively.
OG1 traces queue, depot-on-site, backlog, staff, land and foreign crews;
FB15 owns door-price order sizing/funding, and MC2 mortgage headroom. A study
of forms must not claim their fixes as its own.

### 4.6 Year, technology and the wage-following home

Use the existing `since`/research and recipe framework for availability;
do not create a second year system. Distinguish whether a form can be built
from what equipment/amenities it currently includes. A taller building does
not automatically mean luxury; a modern calendar does not retrofit every
old home's plumbing, lift or finish for free.

Jerus's controlling choice is housing costs following wages, measured against
take-home too, then revisited after HO1. Apply the selected R1g path to new
residential recipes without a second scaling pass. Land remains the land
market's price. Keep physical vintage/features distinct from indexed
replacement costs.

Prior evidence, not a measurement rerun here: `batch/R1g` notes sections 2,
3 and 5 derive founding `homeScale` 0.289185, scaling residential cash,
materials, points and upkeep, **not land**. Section 3 describes the held
founding scale; section 5 also compares a calendar walk and a posted-wage
walk. The latter's documented run stopped at month 2292 (exit 143), had
roughly .32 rent/take-home at 60, and warns that posted wages ran ahead of
households' pay. Those alternatives and the incomplete run do not override
Jerus's wage-following choice or establish a final take-home calibration.
This study inspected R1g at `6e6834c47`; later branch results must be refreshed
before implementing recipes.

## 5. Parking: physical supply and the agreed bill

Track private bays belonging to a home, included exclusive rights to shared
bays, and separately let shared bays. A driveway/garage uses plot or built
space and construction/repair inputs. A shared garage/lot also has access,
operating costs and a payer. Neither building height nor a car automatically
creates a parking space.

All these are aggregate capacities and occupied/reserved quantities by
housing/parking category. Move the corresponding entitlement counts with
household flows. There are no bay IDs, per-car parking records or lease
objects per space.

| Provision | Housing/parking bill | Capacity treatment |
|---|---|---|
| Private driveway/garage belonging to a home | Included in rent or purchase price; owner bears its ownership costs | Attached to that home; cannot also be let from the shared pool |
| Shared space explicitly included with a dwelling | Right included in its housing price; no second space rent | Reserved from physical shared supply whether used or not |
| Other shared space | Separately rented | Allocated from the remaining pool |
| Additional space | Separately rented if demanded and available | Uses another physical bay; current one-car stock supplies no automatic extra-car demand |

For a shared physical supply S, included reservations I, separately leased
spaces L and unallocated spaces V, require **S = I + L + V**, all nonnegative.
Private bays are a separate stock. An unused included right remains reserved
unless an explicit release/subletting policy is later chosen. Never sell or
lease that same right twice.

In a wholly rented building, RE owns the shared facility and receives its
optional parking rent. On condominium formation, the shared facility and
those receipts follow the common account unless a separately owned parking
asset was explicitly created and priced. Recommend common ownership first;
do not leave RE collecting all parking income after the owners inherit all
its expenses.

Rent parking against actual shared capacity/demand with its own carrying
cost and construction alternatives. A scarce bay can be dear; a vacant bay
still costs its owner. Test parking variants at several demand levels, not
one obligatory free bay per apartment. No parking ratio, stall footprint or
garage price is accepted yet. A builder can provide a surface lot only if it
fits the site's remaining land; already purchased plot area is paid for once.

Feed the recurring parking quote into housing and Motoring's affordability
comparisons, and reserve a bay atomically when a new car or move needs one.
Keep the one-car ceiling in this work. Do not delete existing cars when bays
are insufficient: report unserved parking demand and let ordinary household
choices/stock turnover adjust. Whether to refuse a new car without secured
parking is an **open behavior call**, not a hidden extension of RES4. It needs
a caused car/parking/transit fixture if selected. A street-parking market or
additional-car market would be further scope, not free overflow capacity.

## 6. Ownership, common costs and disposal after HO1

### 6.1 The common account from the first apartment sale

Recommend a ring-fenced common-cost pool per building/service cohort,
representing all equivalent buildings and their owners together. It is an
aggregate book in the housing model, not an account per physical building
and not a new sector in `Sectors.KEYS`. Scale expenses, facilities and
management work by the cohort's physical building count. RE can manage it
for a disclosed fee. Common expense receipts are not all RE's income;
only the management service paid to RE is its management revenue.

Allocate common cost with fixed declared shares, initially based on unit
area and explicitly assigned rights/cost groups. Ordinary upkeep of the
building is split into common structure/facilities and private unit work;
their sum must equal the physical repair order. Private garages/interiors
stay with their home. Do not charge HO1's `.01 * entire unit value` plus a
fee paying for the same roof, lift or shared garage. Property tax similarly
has one assessed asset and one payment path, with common land allocated once.

The account pays shared repair orders, common utilities and the contracted
management service. Dedicated variable parking costs can be assigned to the
parking cost group. Other shared parking receipts reduce the remaining
common call. For dwelling category t, let N_t be its physical count, w_t its
per-unit cost weight and W = sum(N_t * w_t). A simple first version is:

`total owner fees = max(0, shared expense budget + management - parking receipts)`

`per-unit fee_t = w_t / W * total owner fees`.

`owner-group contribution = sum(ownedCount[group,t] * per-unit fee_t)`.

For example, 100 equivalent four-unit buildings have 400 homes, a common
budget for 100 buildings and ownership counts by unit category. Their fees
can be computed without knowing which physical building contains each sold
unit. Different physical/service cost regimes use different bounded cohorts;
sales alone do not create accounts. Identical cohorts can be merged while
preserving all cost weights, balances and contributions.

Show gross expenses, parking receipts, fees and account cash separately.
If receipts exceed the call, retain the surplus in the account and lower
subsequent calls; do not silently pay it to RE. A reserve contribution can
later be a distinct saved line; the numerical prototype uses zero reserve
accumulation. Existing monthly repairs are not a second reserve contribution.
Start management remuneration from the actual service/staffing cost; a profit
markup is an open calibration, not an invented permanent fraction of value.

Every ownership group pays its weighted contribution, including RE for
**all unsold quantities**, vacant or rented. Renter cells pay RE gross rent;
RE pays the common contribution for its rented stock. Owner cells pay their
common fees, category-specific tax/private work, mortgage and applicable
optional parking, with zero rent. House ownership includes the corresponding
plot/private provision and its owner costs.

"The last unit sold" becomes the aggregate boundary where RE's owned count
in a cohort reaches zero. The common-cost pool and its physical building/
parking stock remain; owner groups fund the whole call and management work
still has a payer. RE's owner contribution and rent for that stock reach zero.
No building account or individually identified last apartment is required.
If RE ceases managing, obligations transfer to another explicit payer/operator
before removing its management payroll. Unpaid common fees remain receivables
and cash shortfalls; do not pretend an unpaid assessment funded a repair.

### 6.2 Sale instead of scrapping

Give financial retirement a distinct disposal action. It moves housing
quantities into listing cohorts by housing category, seller group, occupancy,
reason, age/ask band and exit intent. Track offered quantity and value,
new inflows, markdowns and cleared quantity/value, without a list of individual
properties. Ordinary and distressed exits go through it. Neither an asking
reduction nor an unsuccessful sale releases land, produces salvage or changes
physical homes.

Initialize an ask from comparable offers/trades and the home's attributes,
not a mandatory rental-value floor. Reduce an unsold ask on successive
marketing reviews. An illustrative rule is `ask[t+1] = ask[t] * (1 - m)`;
the 0.5/1/2% monthly experiments below are sensitivities, **not a selected
markdown rate**. An inflation update must not keep raising an exiting unit's
ask while the policy says lower it. Currency redenomination scales its money
and tick together. At the smallest positive transaction tick an unsold home
remains held and maintained; there is no demolition timeout or salvage floor.
Even a very low ask does not remove a buyer's common-fee burden or credit
constraint, so the market must permit the holding to remain unsold.

Listing asks and cleared transaction prices are different series. A month
with no trade has zero transactions and no new transaction-price observation.
An ask markdown alone cannot lower every homeowner's cleared price, increase
an estate's cash, or pay a bank. Use an explicit stale-price indicator rather
than inventing a trade in a thin segment.

New listings and sales can change a band's average ask through composition.
Record markdowns on continuing listed stock separately from inflow/outflow
value. Test that its ask falls with no inflow or trades; an aggregate mean
changing because dearer new supply entered is not a reversal of old markdowns.

Keep HO1's normal landlord buying below net rental value and selling above it
for investment decisions. An **intentional exit** may accept less than that
value; it must not bid against its own disposal flow. Suppress acquisitions
from an active exit cohort and same-clearing round trips. Keep the exit
quantity's intent until cleared or a separate, explained strategy decision
changes it. Other-category investment remains possible. A cohort acquisition
rule and gross sell/buy flow checks enforce this without property identities.
This deliberately pauses purchases of the whole exit category while its
exit intent remains in force. A narrower ban on only previously sold
addresses would require identities and is not proposed.

Seller proceeds pay the released secured debt first. A sold quantity releases
its allocated collateral/book value and corresponding secured debt from the
seller's aggregate book. Group loans by the relevant product/rate/term/risk
bands and retain balances/weighted sums, rather than a mortgage per apartment.
Remove the seller's corresponding asset/liability once. A short sale needs
seller cash or the lender's agreed loss/default resolution. **A voluntary
markdown is not automatically a city insurance claim.** Existing insured-loss
eligibility must be checked. Remaining RE debt cannot become a condo fee or
an undisclosed liability of unit buyers. The last sale closes the last
collateral allocation without deleting shared assets.

Existing-home sale payments are asset transfers, not new building output.
Management is a service; mortgage principal and reserve transfers are not
rent or profit. Include new accounts/flows in `MoneyAudit` and the saved
statements. Do not manufacture cash rent for owner-occupiers to keep the old
housing sales total unchanged.

### 6.3 Occupied homes and monthly timing

Recommended initial scope: eligible incumbent renter counts can convert to
ownership in their current housing category. Other renter counts retain their
tenancy; external owner-occupier demand uses vacant quantities. Occupied
listing cohorts can keep marking down while incumbent demand cannot buy.
RE keeps the carrying bills. Occupancy/tenure flow constraints prevent a sale
from silently evicting renters or consuming housing twice. Selling tenanted
stock to new private landlords would require another ownership category;
HO1 does not already provide it. These outcomes require aggregate occupancy
links, not a named tenant or unit record.

For monthly accounting, bill the opening tenure/parking positions for that
period. Close purchases/moves at the housing-market seam after Motoring and
before luxury; the resulting positions face the next period's recurring
bills. Verify the actual call graph when implementing: in this base that
cash seam is inside `Game.startOfMonthUpdate()`, reached by `nextMonth()`.
`nextMonth()` then calls `SimulationEngine.simulateMonth(this)`: these are
consecutive phases of one month, so do **not** put a second purchase settlement
in the engine. Update its housing synchronization/derived figures and the
load rebuild consistently. The old HO1 brief's instruction to add the action
in both places needs that correction. A load rebuilds state, never settles a
sale, advances a markdown or renews a lease.

Reserve/release housing and parking together at closing. A tenant who buys
their home stops rent and starts owner bills at the same boundary. Use the
same common budget for owners and RE; do not bill both for the same unit-month.
HO1 must disable physical financial demolition of residential holdings on
shipping, even if RES5's full listing policy follows in the next batch. During
that short interval, hold the housing and its bills. Do not leave a shipping
gap where ownership exists but financial exit still scraps people's homes.

## 7. Arithmetic prototypes and what they establish

These are separate from the model probes. They check the proposed accounting
and sensitivities with explicit inputs; they predict neither city growth nor
market-clearing prices.

### 7.1 A density tradeoff, not a fitted tower

Use the current Low-Rise's present-day structure cost per door, 269,555.56,
and plot per door, 952.38 sq ft, as a comparator. Assume another form needs
200 sq ft of plot per door and costs 25% more structure per door. These tower
inputs are estimates for the experiment, not sourced recipes. Exclude tax,
waiting/finance, utilities and operating cost from both columns.

| Land dollars per sq ft | Low-rise comparator | Denser form, +25% structure | Comparator with 0.5 assumed surface bays/home | Denser form with 0.5 assumed underground bays/home |
|---:|---:|---:|---:|---:|
| 0 | 269,556 | 336,944 | 272,056 | 371,944 |
| 50 | 317,175 | 346,944 | 319,675 | 381,944 |
| 150 | 412,413 | 366,944 | 414,913 | 401,944 |

Parking columns assume 5,000 per surface bay and 70,000 per underground bay;
the surface figure is an uncalibrated input. Both variants must still fit
their site geometrically; the current catalogue does not establish that.
Existing plot area is not bought again in the calculation. The underground
example lies within the 2023 Vancouver parking-only range, which does not
make it a transferable current construction quote.

Without parking, equal capital cost occurs at land 89.57/sq ft. With these
parking assumptions it moves to 132.76. At zero/50% structure premium, the
no-parking crossover is respectively 0/179.14. The result is the formula
`extra structure cost / land saved`, not evidence for a particular premium.
Full game quotes, lead times and operating bills must decide the recipe.
In particular, the toy 25% structure premium does not price the newly proposed
30-90-storey forms or exceptionally large apartments.

### 7.2 Fees across sold and unsold units

Invent four homes of areas 40, 40, 80 and 120. Common expenses are 280/month,
management service 28 and separate shared-parking receipts 16; no reserve is
accumulated. The fee call is 292, divided by fixed area shares. The same
users' 16 parking payment is not also billed as an included-space charge.

This is one aggregate mix: two 40-area units, one 80 and one 120. The rows
transfer owned counts by category, not four identified apartment records.

| Homes sold, in that order | Household owner fees | RE fees for unsold homes | Parking receipts | Paid to manager | Common-account net after 280 expenses |
|---:|---:|---:|---:|---:|---:|
| 0 | 0 | 292 | 16 | 28 | 0 |
| 1 | 41.714286 | 250.285714 | 16 | 28 | 0 |
| 2 | 83.428571 | 208.571429 | 16 | 28 | 0 |
| 3 | 166.857143 | 125.142857 | 16 | 28 | 0 |
| 4 | 292 | 0 | 16 | 28 | 0 |

The final RE-owned quantity shifts its 125.142857 contribution to household
ownership; other categories' per-unit fees do not jump. The manager's 28
remains a service receipt, not 292 of
rent. If it costs the manager 28 to provide the service, that receipt is not
28 of profit. The common account pays each underlying bill once.

Repeating the mix across 100 equivalent buildings uses the same three
dwelling-category rows: physical counts 200, 100 and 100, a 29,200 fee call,
1,600 parking receipts, 28,000 expenses and 2,800 management cost. Counts and
values scale; the number of building/common-account records does not. An
additional exact-rational arithmetic check splits this cohort into 40 and
60 buildings with different ownership mixes and then merges it: owner fees,
RE fees, cost weights and total cash remain identical. When RE-owned counts
all reach zero, the fee call still totals 29,200 and the pool balances.

An illustrative unit sale at 120, buyer cash 6, new loan 114, released seller
loan 100 and book value 80 gives buyer cash -6, bank cash -14 and seller cash
+20: sum zero. Seller gain is 40; net proceeds after debt are 20. One dwelling
exists before and after. This identity does not prove the game's insurance,
tax or collateral release paths; HO1/RES5 must cause those in the model.

### 7.3 Unsold asks

| Illustrative monthly markdown | Initial ask | After 6 months | After 12 | After 24 |
|---|---:|---:|---:|---:|
| 0.5% | 100 | 97.0373 | 94.1623 | 88.6654 |
| 1% | 100 | 94.1480 | 88.6385 | 78.5678 |
| 2% | 100 | 88.5842 | 78.4717 | 61.5780 |

With no buyer, all have zero trades, unchanged physical homes, no realized
sale proceeds and no fresh transaction price. Holding costs continue. Select
a pace from a caused and played listing experiment in RES5; the table alone
does not show a buyer's willingness, the right starting ask or a safe fee.

## 8. Jerus's calls, accepted and open

**★1 Mixed dwellings in some buildings — accepted.** Jerus answered the
explanation with "thats a great idea". Permit some mixed apartment forms and
some uniform ones. The schedules and counts in section 4.2 remain candidates.

**★2 Preferences including the elite — requested; exact rule open.** His
words: "the elite may prefer different types". Recommend flexible preferences
for space/garden, quality and amenities subject to real budgets, with several
profiles within tiers. No exclusive elite type. Neither "elite always buys a
mansion" nor fixed profile shares has been accepted or measured.

**★3 Garages/parking — scope and billing accepted; quantities/effects open.**
His words: "garage/parking lot factored in, cause cars are a thing right";
he accepted the explained billing with "ok". Private provision is in the
home's price; shared spaces are separately let except explicit included
rights; extras separately charged. Recommend the conserved rights ledger,
physical costing and one-car ceiling in section 5. Ratios, stall dimensions,
prices, overflow and a no-parking refusal for new car purchases remain open.

**★4 The pictogram UI — accepted later.** Jerus: "after this also a ui update
for real estate right? such that you know how industyr has pictograms and
stuff, i feel like it would be good to implement one for real estate as well,
but thats later after this study". REU1 follows the settled model. No UI work
is included in this docs branch.

**★5 The 1900 home — standing choice.** Costs follow wages, measured against
take-home too; revisit after HO1. Recommend retaining that choice and R1g's
unscaled land distinction, not substituting its held/calendar alternatives
or fitting every new form to the historical 0.289185 founding figure.

**★6 Disposal after home ownership — accepted requirement.** Jerus: "after
HO real estate instead of demolishing it should just sell either the units
of the apartment and charge a monthly fee like a condo but not rent, or sell
its houses"; "if no one then lower and lower prices, but not demolish".
List standing houses/units, mark down unsold asks and preserve them. Owners
pay owner/common bills, not rent. Exact exit triggers, listing pace and
financial resolution are recommendations to calibrate, not accepted literals.

**★7 Layout, forms, density and year — authorized study questions; mechanics
open.** Recommend the explicit room/area/quality model and finite form menu,
with physically checked mixes and the current recipe/year framework. Treat
bedroom suitability as a target where data supports it, distinct from the
current studio eligibility rule. Calibrate construction/work/utilities through
model quotes. A geographic neighbourhood simulation is optional further
scope: current zoning allocates amounts, not neighbourhood locations.

On reviewing the first catalogue table, Jerus noted that bigger apartments
exist. Section 4.2 now expands both building capacity/height and dwelling
area, keeping the original geometric examples as examples rather than caps.
The additional numerical ranges remain candidates, not accepted recipes.

**★8 Occupied units offered for sale — open; recommended tenant retention.**
Offer the sitting tenant a purchase; keep renting to them if they cannot or
do not buy. External owner-occupiers buy vacant homes. No forced move merely
to facilitate disposal. The consequence is that an occupied holding may stay
on RE's books for a long time. A private buy-to-let buyer would require more
ownership scope than HO1 currently has.

**★9 Common-fee recipient and last sale — open; recommended common account.**
Owners, including RE on unsold quantities, fund fixed weighted shares of a
common-cost pool for equivalent buildings. It pays costs and an explicit RE
management service. Owners
do not keep paying rent after purchase; the manager does not take the whole
common fee as profit. Start from actual service cost and make any profit
markup a separately justified parameter. Section 7 proves the bookkeeping,
not an accepted management price or reserve policy.

**★10 Implementation boundary — recommended.** RES2-RES4 establish the
foundations before HO1. HO1 includes the first owner's actual common-cost
bills and the residential demolition guard. RES5 then supplies deliberate
disposal/markdown policy; HO2 uses its real listing/closing outcomes. This
honors the later "after HO" instruction. Review this order with the design,
including the proposed posted-offer closing rule in section 4.4; these are
not instructions to start HO1 or implementation tonight.

**★11 Aggregate simulation — confirmed requirement.** Jerus: "everything
should be handled in aggregates no?" Yes: use building/form stock cohorts,
dwelling-type counts, household-cell occupancy/ownership shares, aggregate
mortgage/listing bands, parking capacities and common-cost pools. No per-person,
per-building, per-apartment or per-bay identities/accounts. Partial sales
and the last RE-owned quantity are count/weight boundaries. Split/merge
invariance and bounded state growth are required proofs, not optional
optimizations. The earlier stable-building-key proposal is superseded.

For morning review, the main new choices are ★8 tenant retention, ★9 the
common account/manager, and the candidate form/layout direction in ★2/★7.
Exact mixes, costs, preference shares and markdown rates should come back
with their model calibration evidence; they are not silently approved here.

## 9. Ordered implementation and caused proofs

IDs RES1, REU1 and RES2-RES5 pass `tools/batch-id.sh`; no implementation row is
being inserted by this study branch. The orchestrator owns the queue, gates
and global lanes. Before starting, refresh the listed moving branches and
the applicable baseline, and resolve overlaps by the contract.

| Order / batch | Scope and likely source changes | What its caused harness must prove |
|---|---|---|
| **RES1, this study** | This file and `docs/studies/README.md`; docs only, no trace change | Source/probe provenance, physical/accounting examples, dependency and acceptance consistency; docs fast checks |
| **RES2, aggregate housing foundations** | `BuildingsTemplate`, `BuildingCatalog`, `BuildingManager`/`BuildingsStacks`; housing stock/cell cohorts; `DataSave`/load; `RealEstate` compatibility getters | Existing three schedules reproduce exact door/capacity censuses including the separate founding adjustment; mixed-type counts survive completion and save/load; ownership transfers move no structure/land/material; equivalent cohort split/merge preserves all quantities/values; scaling represented buildings does not create building/unit records; RN2's first-month debt share cannot diverge |
| **RES3, home choice, typed rents and forms** | `FamilyModel`, `HouseholdBalance`/accounts, `RealEstate`, `EconomyManager`, finite new `buildings.json` recipes and their actual builder/utility inputs; aggregate cell assignments | Same headcount/different layout outcomes; scarce family stock with empty compact homes; unaffordable premium vacancy; a high-resource household choosing quality and compromising when unavailable; incumbent stability; fair ties; no demand counted across alternatives twice; per-class rent differs where justified and falls in glut; an empty premium project does not earn full rent; mixed project's inputs/area/floor limits reconcile |
| **RES4, parking supply and bills** | Housing registry/templates/recipes; `HouseholdBalance`, `Motoring` budget inputs, shared-parking quote/leases, saved flows and audit | Private/included/shared/extra cases charge once; unused included rights cannot also be rented; exact S=I+L+V; full lot refuses another lease; bay acquisition/release on move or sale is atomic; construction consumes land/work/cash; recurring parking reaches budgets and optional receipts the owner; existing car stock is conserved and the one-car cap unchanged; low/high car demand and no-car households |
| **HO1, owning these homes** | Existing HO1 classes plus aggregate ownership/occupancy, loan bands and common-cost pool interfaces | Canadian credit tests on category prices and bills; owners pay zero rent and all owner/common obligations; normal landlord trades around category net rental value; first/partial/zero-RE-owned quantity boundaries; aggregate collateral release and gain/loss; common costs still paid; no financial housing demolition; exact money/save-reload and split/merge invariance |
| **RES5, deliberate disposal after HO1** | `RealEstate` exit decisions, `HomeMarket` listings/clearing, central financial-disposal paths in `Game`/credit, aggregate fee/parking/ownership books and history | Ordinary and distressed exits preserve buildings/land/capacity and produce no salvage; unsold asks fall without fictitious trades; tenant purchase/refusal/vacancy outcomes; no own-listing bid/buyback loop; a sale below rental value still closes when funded; a short sale cannot silently use insurance or pass RE debt to owners; last-unit costs and management survive; reload during unsold marketing gives the same next markdown and transaction |
| **HO4, HO2, LP1** | Existing credit levers, aggregate owner life transitions/screens, then loan packages | Keep accepted order constraints; HO2 estate/outbound cohorts wait for funded clearing and pay mortgage first; uncleared quantities retain owner/carry obligations; moving/folding household cells preserves associated counts and loan sums without individual records; LP1 packages the resulting loan cohorts |
| **REU1, housing pictogram** | Pure housing view/picture and `ui/SectorScreen` kept Real Estate card/pane, using `IndustryView`/`IndustryPicture` and `industryCard`/`IndustryPane` as references | Public model figures reconcile to registry, cash statements and saved flows; empty, mixed, parking-short and fully sold fixtures; kept nodes/scroll survive month redraw; render/page adds no simulation changes |

RES2 builds data/compatibility first; activate new forms only with RES3's
matching and prices, not as luxury pictures earning the old undifferentiated
rent. RES3 and RES4 move the trace. RES2 must measure whether its compatibility
path is identical, and say so rather than assume it. HO1/RES5 also move the
trace and require their appropriate money/save review and ensemble. Run one
measurement JVM at a time, obey local gate/dry-run restrictions and memory
limits, and send gates/ensembles to the box under the contract.

Each fixture must cause both sides of its threshold against the model's own
constants. Include `HousingCheck`, `HousingDoorCheck`, `BuildingDataCheck`,
`HouseholdCheck`, `CarCheck`, `MortgageCheck`, `InvestCheck`, `MoneyAudit`,
`SaveFileCheck`, `ReadPathCheck` and RN2's reload premise where affected;
actual picks and nightly requirements follow the final diff. A 142-building
proposal is not a replacement for proving funded completion through OG1.

Use sparse aggregate assignments and a bounded number of stock/service and
financial bands. Splitting/merging identical cohorts must preserve the outcome;
multiply represented counts by 1,000 and verify no individual records appear
and bookkeeping scales correctly. Measure allocation, pricing, fee settlement,
save size and reload on small and large model cities before choosing a solver
or more preference profiles. Do not add a loop per represented dwelling or
resident. No runtime improvement is claimed by the arithmetic prototypes.

### REU1's concrete replacement brief

The `4f1056f11` queue note already distinguishes the docs study from the
future model batches. Once reviewed, make its tentative dependencies precise:
wait for this reviewed study, RES2-RES4, HO1 and RES5, then show the whole
requested housing model.
It need not wait on LP1. Coordinate the common screen file with HO2's screens;
its position among unrelated queue lanes remains the orchestrator's.

Draw buildings containing the actual dwelling-type mix and tenure split,
with proportional/count-labelled home pictograms; distinguish vacant,
occupied, listed and on-site homes without counting listings as extra doors.
Show layout shortages, affordability exclusions and crowding, class rents,
actual sale transactions separately from unsold asks/age, owner common fees
and who receives them, plus private/included/let/free parking. Explain costs
with the actual receipts/expenses and manager income, including the last
unit sold. Avoid a tile per resident/door in a large city: aggregate counts
and provide the exact legend. Every number comes from a model/view getter;
`Kept` changes the month on the existing nodes. No neighbourhood map or new
economic formula belongs in the screen.

## 10. Precise amendments and takeovers in HO1

The existing authority is [spec-credit-housing section 2.3 and section 7](spec-credit-housing.md#23-owning-a-home-household-householdbalance-new-homemarket-realestate-bank).
The following is a replacement contract for the affected parts, not an edit
to that brief or an implementation in this docs branch.

| Existing HO1 assumption | Required replacement / ownership of the change |
|---|---|
| An owned home is a studio/family segment capacity share; physical doors stay solely the landlord's portfolio | Keep aggregation, with richer bounded categories: RES2 supplies building counts and dwelling-mix vectors; HO1 carries ownership and occupancy quantities by housing class and household cell. All homes remain in physical totals; only RE-owned shares/cash flows remain its investment portfolio. No building or dwelling IDs. |
| One home price per capacity in each of two segments | RES3 supplies comparable dwelling classes; HO1 offers/clears actual homes with their included rights and common obligations. Preserve a city transaction index only as an observed summary, not another independently traded price. |
| P* makes buyers' capacity equal everything offered; the price lags toward it | Recommend matched posted offers: only eligible, funded buyers close, at accepted asks. The old equilibrium calculation can guide ordinary quotes; its lag is not a fictitious transaction and does not make every listing sell. Actual transactions alone update the cleared-price index. Re-measure the rate response. |
| Household cells gain seven averaged owner slots | Keep count/balance/rate-weight/month-weight/arrears sums, disaggregated into bounded housing/loan categories where fees/rights/credit differ. Move aggregate quantities and sums with household flows, then sum for cell reporting. Overall cell averages alone can hide differences between housing classes; that does not require individual mortgages or titles. Do not duplicate authoritative debt. |
| Owners pay `.01 * home value` upkeep, and landlord bills fall by owned capacity | Partition aggregate private/common repair and payroll/utility costs once. HO1 funds a shared-cost pool per equivalent building/service cohort; contribution weights follow RE/household ownership counts. No account per building. Renters pay gross rent only; RES5 extends exit/listing policy, not the first existence of common fees. |
| `V_L = net monthly rent * 12 / cap rate` | Keep the formula and chosen pass-through, but use the actual comparable home's net operating income, correctly allocated expenses/tax and transferable included parking rights. Do not capitalize a free common-cost liability or subtract financing interest twice. |
| Landlord sells above and buys below V_L | HO1 keeps normal investment behavior; add an explicit exit-intent interface. RES5 allows intentional exits below V_L and suppresses self-bidding/reacquisition of that exiting holding. |
| Builders sell beyond cheapest cost plus BUILD_MARGIN, finishing in 18 months | Use the actual mix/parking quote and profit test with the existing margin as the baseline; completed, funded physical pipeline only. The 18-month estimate is superseded by real builder work/queue completion, coordinated with OG1/FB15. Never create a second set of finished homes by a timer. |
| GDS/TDS on payment, tax and heat | Keep accepted 5% down, insurance under 20%, 39%/44%, +2 stress and the agreed term rules. Add 50% of the actual condo fee to qualifying ratios per CMHC; the household CASH budget pays 100%, plus full applicable optional parking/private costs. Identify heat included in a fee so the same heat is not also billed separately. Do not reinterpret gross income as take-home. |
| Selling a home credits the landlord's till / estate cash at the month's price | Clear matched quantities between seller cohorts and eligible buyer cells; release the allocated collateral/debt, settle gain/loss and move ownership/parking quantities and net money once. Keep traded quantity/value totals; there is no individual title record. A listing or markdown pays nobody. |
| Owners' exits trade capacity immediately on ageing, death or leaving | HO2 carries unsold counts, loans and costs in estate/outbound cohorts until funded clearing; aggregate proceeds repay debt before heirs/leavers receive net cash. No per-estate object or invented cash for unsold stock. |
| HO1 requires no save bump because old owners are zero | Reassess against the new inventory, assignments, common/parking accounts and saved monthly flows. Zero owners alone no longer proves correct reading. Follow the existing rule: bump SAVE_FORMAT and OLDEST_LOADABLE_FORMAT if the new schema would read an old save wrongly; no conversion code. |
| After `motoring.month()` at the old Game.nextMonth step 34; also add the action to SimulationEngine | Settle once inside `startOfMonthUpdate()`, after cars and before luxury. `nextMonth()` then runs `SimulationEngine`, which must synchronize derived housing data without a second trade. Save/rebuild only restore state. Opening-period bills and closing transfers follow section 6.3. |

### Work already queued or in flight

- **HT1, HS3, HC1** are the queue's existing prerequisites before HO1. HS1/HS2
  household/stage machinery already exists in this source base; use available
  stages rather than restoring the old study's temporary shape-only model.
  HO4 and HO2 still follow HO1, LP1 follows its loan/lifecycle prerequisites.
- **BO1-H:** include business-owner families in RES3's actual housing match
  and rental bills. It is not home ownership. If BO1-H ships first, preserve
  its seeker group; otherwise explicitly agree the takeover with its owner.
- **RN2:** let its owned-capacity persistence fix ship independently if ready.
  RES2 retains its reload finding and replaces/derives that total coherently;
  never erase its first-month debt-allocation proof as redundant.
- **FB15:** local tip `5eb55e7c5` changes Game order pricing/equity funding,
  door refusal trimming and its credit/harness support, not RealEstate. Its
  door quote is the price the later builder-mix experiments must use.
- **MC2:** inspected tip `a7ab9dfba` changes Bank/BusinessDebtManager growth
  headroom and BankFailureCheck, not RealEstate. Do not fit housing recipes
  around a mortgage refusal this batch is already repairing.
- **R1g:** refresh its selected wage-following cost path and new founding
  default before recipe/era calibration; this study's city probe deliberately
  pins today's founding. Its stopped posted-wage run is prior evidence.
- **OG1:** owns the backlog/depot/staff/land/quantity bottleneck investigation.
  Share typed gaps, project quotes and measured completion reasons with it;
  do not independently alter the builders' queue from a housing-form study.
- **SD1:** owns the misleading generic "Sold" message. It is separate from
  RES5's real housing transfer and must not run beside its disposal edits.

Do not run overlapping model batches on the shared month, matching, credit
or screen paths. The dependency order here is architectural, not permission
to edit main, QUEUE.md, versions, changelog or gate records. The complete
study is for Jerus's satisfaction first; then request docs-only untagged
shipping with `gate --no-tag` and the agreed ordering. No model, UI, ensemble
or user save has been changed by this study.

## 11. Reproduction and validation record

The originating session's `./batch-check.sh --fast` passed on this worktree
(compile, maps, zero firm Stale findings); only generated date changes were
restored. No code change justified repeating that baseline. Study probes
compiled separately into `target/res1/classes`, ahead of existing
`target/classes` on the runtime classpath, without replacing model classes.
Raw short-row logs are `target/res1/pure.txt` and `city.txt`; they are not
tracked deliverables. The portable model probe below is included so the
findings survive cleanup of scratch files. It uses no user save slots.

Save as `target/res1/RealEstateProbe.java`, compile with the existing
`target/classes` and jars from `target/classpath.txt`, and run its no-argument
mode for fixtures or `city` for the observation run. On Windows quote each
`-D` argument in PowerShell. Before EACH compiler/measuring JVM, check
`Get-Counter '\Memory\% Committed Bytes In Use'` is below 75%; wait otherwise.
Use only one probe JVM, never beside a local gate/dry run or ensemble.

```powershell
New-Item -ItemType Directory -Force target/res1/classes | Out-Null
function Assert-ProbeMemory {
  if ((Get-Counter '\Memory\% Committed Bytes In Use').CounterSamples.CookedValue -ge 75) {
    throw 'Wait for committed memory below 75% before starting a JVM'
  }
}
$cp = 'target/classes;' + (Get-Content target/classpath.txt -Raw).Trim()
Assert-ProbeMemory
javac -J-Xmx256m -cp $cp -d target/res1/classes target/res1/RealEstateProbe.java
if ($LASTEXITCODE -ne 0) { throw 'Probe compilation failed' }
$runCp = 'target/res1/classes;' + $cp
Assert-ProbeMemory
java -Xmx256m -cp $runCp ham.citybuildersim.RealEstateProbe > target/res1/pure.txt
if ($LASTEXITCODE -ne 0) { throw 'Pure probe failed' }
Assert-ProbeMemory
java -Xmx1024m '-Dplaytest.founded=today' '-Dplaytest.pathSeed=48213' `
  '-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin' `
  '-Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin' `
  -cp $runCp ham.citybuildersim.RealEstateProbe city > target/res1/city.txt
if ($LASTEXITCODE -ne 0) { throw 'City probe failed' }
```

```java
package ham.citybuildersim;

import ham.citybuildersim.sectors.RealEstate;
import java.util.*;

/** Study only. No user saves; current model calls, no replacement model. */
public class RealEstateProbe {
    static void row(String format, Object... xs) {
        System.out.printf(Locale.ROOT, "RES1 " + format + "%n", xs);
    }
    static FamilyModel fixture(FamilyStructure shape, double poor, double rich) {
        String[] shapes = FamilyModel.saveShapes();
        int tiers = PayTier.values().length;
        double[] saved = new double[shapes.length * tiers + 2];
        int index = Arrays.asList(shapes).indexOf(shape.name());
        saved[index * tiers] = poor;
        saved[index * tiers + tiers - 1] = rich;
        FamilyModel f = new FamilyModel();
        f.restore(PopulationCohorts.saveBands(), shapes, saved);
        return f;
    }
    static void match(String label, FamilyStructure shape, double poor, double rich,
                      int smallSize, long smallCount, int bigSize, long bigCount) {
        FamilyModel f = fixture(shape, poor, rich);
        long[] homes = new long[Math.max(7, Math.max(smallSize, bigSize) + 1)];
        homes[smallSize] += smallCount;
        homes[bigSize] += bigCount;
        double left = f.house(homes);
        row("match,%s,heads=%d,households=%.3f,unplaced=%.3f,refusedStudio=%.3f,crowded=%.3f,studioWeight=%.3f,familyWeight=%.3f",
            label, shape.size(), f.totalHouseholds(), left, f.getRefusedByStudio(),
            f.getCrowdedHouseholds(), f.studioRentWeight(), f.familyRentWeight());
    }
    static void pure() {
        List<BuildingsTemplate> all = new BuildingCatalog().load();
        double m = Good.MATERIALS.worldImportPrice();
        row("material,worldImport=%.9f,todaysImport=%.9f", m, Good.MATERIALS.todaysImportPrice());
        RealEstate re = new RealEstate();
        re.setRentPrice(2);
        for (BuildingsTemplate t : all) if (t.getSector().equals(re.key())) {
            double structure = t.getCashCost() + t.getConstructionMaterials() * m;
            row("catalogue,%d,%s,doors=%d,capacity=%d,size=%d,cash=%.6f,materials=%d,structureDoor=%.6f,landDoor=%.6f,pointsDoor=%.6f,powerDoor=%.6f,waterDoor=%.6f,segmentRent=%.6f",
                t.getId(), t.getName(), t.getDwellings(), t.getCapacity(), t.homeSize(),
                t.getCashCost(), t.getConstructionMaterials(), structure / t.getDwellings(),
                t.getLandSqFt() / t.getDwellings(), (double)t.getConstructionPoints() / t.getDwellings(),
                (double)t.getElectricityConsumption() / t.getDwellings(),
                t.getWaterConsumption() / t.getDwellings(), re.priceForSegment(t));
        }
        BuildingsTemplate premium = new BuildingsTemplate("Study premium", BuildingType.RESIDENTIAL)
            .setCapacity(4).setDwellings(1).setCashCost(2000);
        row("premium,segmentRent=%.6f", re.priceForSegment(premium));
        match("parent-two", FamilyStructure.SINGLE_PARENT, 1, 0, 2, 1, 6, 0);
        match("parent-three", FamilyStructure.SINGLE_PARENT, 1, 0, 3, 1, 6, 0);
        match("parent-four", FamilyStructure.SINGLE_PARENT, 1, 0, 4, 1, 6, 0);
        match("large-four", FamilyStructure.LARGE_FAMILY, 1, 0, 4, 1, 6, 0);
        match("poor-only", FamilyStructure.SINGLE_ADULT, 200, 0, 2, 100, 6, 100);
        match("elite-only", FamilyStructure.SINGLE_ADULT, 0, 200, 2, 100, 6, 100);
        match("poor-elite", FamilyStructure.SINGLE_ADULT, 100, 100, 2, 100, 6, 100);
    }
    static void city() {
        Game g = LongPlaytest.probeCity(GameFiles.scratch("res1-housing"), null);
        for (int target : new int[]{60, 240, 600}) {
            LongPlaytest.probePlay(g, target);
            FamilyModel f = g.getFamilies();
            BuildingManager b = g.getBuildingManager();
            RealEstate re = g.getSectors().realEstate();
            HouseholdAccounts a = g.getHouseholds();
            BusinessInvestment.Decision decision = re.plan(g.getBusinessInvestment(), g);
            row("city,month=%d,pop=%d,households=%.6f,seekers=%.6f,doors=%d,capacity=%d,pipelineDoors=%d,unplaced=%.6f,doubled=%.6f,crowded=%.6f,cars=%.6f,rent=%.6f,disposable=%.6f,rentRatio=%.6f,planQty=%d,build=%s",
                g.getMonth(), g.getPopulationManager().getPopulation(), f.totalHouseholds(), f.householdsSeekingDoors(),
                b.getTotalHomes(), b.getTotalHouseCapacity(), b.getHomesUnderConstruction(),
                f.getStillUnplaced(), f.getDoubledUpHouseholds(), f.getCrowdedHouseholds(),
                g.getHouseholdBalance().totalCars(), a.getRent(), a.getDisposableIncome(),
                a.getRent() / a.getDisposableIncome(), decision == null ? 0 : decision.quantity,
                decision != null && decision.build);
            row("sizeCensus,month=%d,%s", g.getMonth(), Arrays.toString(b.homesBySize()));
            for (int r = 0; r < PayTier.values().length; r++)
                row("tier,month=%d,tier=%s,households=%.6f,rent=%.6f,disposable=%.6f,ratio=%.6f",
                    g.getMonth(), PayTier.values()[r], a.getRowHouseholds(r), a.getRowRent(r),
                    a.getRowDisposable(r), a.getRowRent(r) / a.getRowDisposable(r));
            row("planReason,month=%d,%s", g.getMonth(), decision == null ? "null" : decision.reason);
        }
    }
    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        if (args.length > 0 && args[0].equals("city")) city(); else pure();
    }
}
```

The arithmetic tables can be reproduced independently, without a game or JVM:

```python
from decimal import Decimal as D
K = (D(10196) + D(377)*18)*1000/63  # catalogue id 6, present-day dollars
L = D(60000)/63
for p in (D(0), D(50), D(150)):
    print(p, K+p*L, K*D('1.25')+200*p,
          K+2500+p*L, K*D('1.25')+35000+200*p)
for premium in (D(0), D('.25'), D('.50')):
    print('land crossover', K*premium/(L-200))
print('parking crossover', (K*D('.25')+32500)/(L-200))
areas = list(map(D, (40,40,80,120)))
for sold in range(5):
    owners = D(292)*sum(areas[:sold])/sum(areas)
    re = D(292)-owners
    assert owners+re+16-280-28 == 0
    print(sold, owners, re)
for rate in (D('.005'), D('.01'), D('.02')):
    print(rate, [D(100)*(1-rate)**m for m in (0,6,12,24)])
assert -D(6)+(D(100)-D(114))+(D(120)-D(100)) == 0
```

### Aggregate accounting check

This exact-rational prototype checks cohort conservation and split/merge
invariance; it does not measure a new game implementation.

```python
"""Aggregate arithmetic only; no model changes or per-property records."""
from fractions import Fraction as F

MIX = (2, 1, 1)
WEIGHTS = (40, 80, 120)

def fees(buildings, owner_counts):
    stock = tuple(buildings * count for count in MIX)
    assert all(0 <= owned <= count for owned, count in zip(owner_counts, stock))
    weight = sum(count * w for count, w in zip(stock, WEIGHTS))
    call = 292 * buildings
    per_unit = tuple(F(call * w, weight) for w in WEIGHTS)
    household = sum(n * fee for n, fee in zip(owner_counts, per_unit))
    landlord = sum((n - owned) * fee for n, owned, fee in zip(stock, owner_counts, per_unit))
    assert household + landlord + 16 * buildings - 280 * buildings - 28 * buildings == 0
    return household, landlord, per_unit

a = fees(40, (40, 20, 10))
b = fees(60, (90, 40, 25))
merged = fees(100, (130, 60, 35))
assert merged[0] == a[0] + b[0]
assert merged[1] == a[1] + b[1]
assert merged[2] == a[2] == b[2]
all_owned = fees(100, (200, 100, 100))
assert all_owned[:2] == (29200, 0)
scaled = fees(100000, (130000, 60000, 35000))
assert scaled[0] == 1000 * merged[0] and scaled[1] == 1000 * merged[1]
assert len(scaled[2]) == len(merged[2]) == 3
fees(1, (F(1, 2), 0, 0))
print('PASS: aggregate common-cost cash conservation')
print('PASS: split/merge with different ownership mixes')
print('PASS: zero RE ownership preserves shared costs')
print('PASS: 1000x represented stock uses the same three category rows')
```

Validation completed for this draft: both model probe modes exited 0; the
separate arithmetic prototype balances the common account for 0-4 sales and
the sale cash flows; new IDs passed the repository checker. Review checks
also verify candidate floor-area arithmetic, cited amounts, accepted/open
labels, the HO1 prerequisite split and the two-file-only diff. The final
Stale scan exited 0 with zero firm findings at `-Xmx1024m`; its first 256 MB
attempts exhausted heap while parsing the source tree, so it was rerun after
checking committed memory (64.2%) with the larger permitted heap. Whitespace
and LF checks passed. No ensemble was owed by this docs-only change. Shipping
awaits Jerus's review. The subsequent aggregate-only amendment passed the
exact-rational split/merge, zero-RE-ownership and 1,000x quantity checks above;
no model code changed and the model probes were not rerun.

## 12. Complete numerical catalogue proposal

The earlier sections established the existing model, aggregate architecture,
accounting rules and dependency split. They did not calibrate a complete
building catalogue. The tables below are a new explicit numerical draft,
not measurements of implemented buildings or a finished balance pass.


**Status: numerical design draft for review; not calibrated game outcomes.**
All prices below are present-day benchmark model dollars. Land is $50/sq ft; material is $18,000/unit; ordinary demand pressure is 1. No inflation, finance, scarcity or rebate is added to the reference rents.

C01-C14 are comparison labels, not permanent game building IDs or batch IDs. Existing catalogue IDs remain unchanged.

The existing templates anchor the cost calculation. New area, height, layout, quality, parking, staff and work budgets are proposals. A cost-derived reference quote is not an observed rent or a promised transaction price.

### Dwelling types

| Code | Internal m² | Bedrooms | Normal places | Finish factor |
|---|---:|---:|---:|---:|
| H55 | 55 | 1 | 2 | 1.00 |
| H120 | 120 | 3 | 6 | 1.00 |
| H240 | 240 | 4 | 6 | 1.25 |
| D90 | 90 | 2 | 4 | 1.00 |
| T100 | 100 | 3 | 6 | 1.00 |
| S | 40 | 0 | 2 | 1.00 |
| F | 75 | 2 | 4 | 1.00 |
| L | 110 | 3 | 6 | 1.00 |
| A | 160 | 3 | 6 | 1.25 |
| V | 240 | 4 | 6 | 1.40 |
| X | 600 | 5 | 6 | 1.60 |
| P140 | 140 | 3 | 6 | 1.40 |
| P180 | 180 | 3 | 6 | 1.50 |
| P360 | 360 | 4 | 6 | 1.60 |
| P600 | 600 | 5 | 6 | 1.75 |

### Full building list

| ID | Form | Floors | Homes | Normal capacity | Unit mix | Residential gross m² | Plot sq ft |
|---|---|---:|---:|---:|---|---:|---:|
| C01 | Compact house | 1 | 1 | 2 | 1 H55 | 69 | 4,000 |
| C02 | Family house | 2 | 1 | 6 | 1 H120 | 150 | 8,000 |
| C03 | Large house | 2 | 1 | 6 | 1 H240 | 300 | 12,000 |
| C04 | Duplex | 2 | 2 | 8 | 2 D90 | 200 | 10,000 |
| C05 | Terrace | 3 | 6 | 36 | 6 T100 | 667 | 18,000 |
| C06 | Studio block | 4 | 80 | 160 | 80 S | 4,000 | 25,000 |
| C07 | Mixed walk-up | 3 | 24 | 88 | 8 S + 12 F + 4 L | 2,160 | 22,000 |
| C08 | Family low-rise | 3 | 63 | 252 | 63 F | 5,906 | 60,000 |
| C09 | Mixed mid-rise | 8 | 64 | 240 | 16 S + 40 F + 8 L | 5,650 | 24,000 |
| C10 | Mixed tower | 20 | 160 | 576 | 48 S + 96 F + 12 L + 4 P140 | 15,200 | 24,000 |
| C11 | Large apartment block | 12 | 360 | 1248 | 120 S + 216 F + 24 L | 29,550 | 100,000 |
| C12 | Large high-rise | 40 | 400 | 1456 | 120 S + 232 F + 44 L + 4 P180 | 40,000 | 40,000 |
| C13 | Very tall tower | 80 | 800 | 2880 | 240 S + 480 F + 76 L + 4 P360 | 80,000 | 50,000 |
| C14 | Large-home luxury tower | 36 | 120 | 720 | 61 A + 48 V + 8 X + 3 P600 | 43,200 | 30,000 |

### Capital and reference rents

| ID | Whole project incl. land/parking $m | Cost per home equivalent $k | Average reference home rent $/month | Reference rent range $/month | Repair budget/home/month | Default tax/home/month |
|---|---:|---:|---:|---:|---:|---:|
| C01 | 0.44 | 439.8 | 2,126 | 2,126–2,126 | 200 | 550 |
| C02 | 0.85 | 846.0 | 4,089 | 4,089–4,089 | 372 | 1,058 |
| C03 | 1.59 | 1,586.5 | 7,668 | 7,668–7,668 | 822 | 1,983 |
| C04 | 1.11 | 557.4 | 2,694 | 2,694–2,694 | 256 | 697 |
| C05 | 2.76 | 460.2 | 2,224 | 2,224–2,224 | 258 | 575 |
| C06 | 15.74 | 196.7 | 924 | 924–924 | 151 | 246 |
| C07 | 7.44 | 310.2 | 1,463 | 993–2,186 | 220 | 388 |
| C08 | 20.14 | 319.7 | 1,490 | 1,490–1,490 | 227 | 400 |
| C09 | 18.26 | 285.4 | 1,339 | 913–1,964 | 222 | 357 |
| C10 | 54.79 | 342.4 | 1,520 | 1,013–3,503 | 279 | 428 |
| C11 | 98.21 | 272.8 | 1,271 | 916–1,974 | 216 | 341 |
| C12 | 157.79 | 394.5 | 1,728 | 1,123–5,011 | 325 | 493 |
| C13 | 373.58 | 467.0 | 2,037 | 1,303–11,317 | 387 | 584 |
| C14 | 153.56 | 1,279.6 | 6,185 | 4,236–16,900 | 1,056 | 1,600 |

### Capital breakdown

| ID | Building structure/fitout $m | Parking construction $m | Land $m | Nonmaterial construction cash $m |
|---|---:|---:|---:|---:|
| C01 | 0.235 | 0.005 | 0.200 | 0.150 |
| C02 | 0.421 | 0.025 | 0.400 | 0.266 |
| C03 | 0.937 | 0.050 | 0.600 | 0.627 |
| C04 | 0.565 | 0.050 | 0.500 | 0.345 |
| C05 | 1.831 | 0.030 | 0.900 | 1.069 |
| C06 | 14.388 | 0.100 | 1.250 | 8.692 |
| C07 | 6.285 | 0.060 | 1.100 | 3.825 |
| C08 | 16.982 | 0.160 | 3.000 | 10.284 |
| C09 | 16.905 | 0.160 | 1.200 | 10.297 |
| C10 | 47.988 | 5.600 | 1.200 | 30.998 |
| C11 | 92.306 | 0.900 | 5.000 | 56.756 |
| C12 | 137.307 | 18.480 | 2.000 | 87.945 |
| C13 | 327.400 | 43.680 | 2.500 | 205.984 |
| C14 | 143.658 | 8.400 | 1.500 | 85.386 |

Cost per home equivalent includes the whole physical project divided by homes; it is not the sale price. Reference home rent excludes capital allocated to separately rented parking. Private/included parking is in home rent; separately let capacity has its own quote below. Owner sale values depend on HO1 actual prices/credit and transferred common interests.

### Reference rent by apartment type

| ID | S 40 m² | F 75 m² | L 110 m² | Penthouse variant / monthly rent |
|---|---:|---:|---:|---:|
| C06 | 924 | — | — | — |
| C07 | 993 | 1,535 | 2,186 | — |
| C08 | — | 1,490 | — | — |
| C09 | 913 | 1,384 | 1,964 | — |
| C10 | 1,013 | 1,572 | 2,469 | 140 m² / 3,503 |
| C11 | 916 | 1,391 | 1,974 | — |
| C12 | 1,123 | 1,779 | 2,807 | 180 m² / 5,011 |
| C13 | 1,303 | 2,116 | 3,369 | 360 m² / 11,317 |

C14 luxury-tower rents: A, 160 m²: 4,236/month (61 homes); V, 240 m²: 6,355/month (48 homes); X, 600 m²: 16,008/month (8 homes); P600, 600 m²: 16,900/month (3 homes).

### Parking

| ID | Private included | Shared physical | Included shared rights | Separately rentable capacity | Type / basement levels | Separate reference $/space/month |
|---|---:|---:|---:|---:|---|---:|
| C01 | 1 | 0 | 0 | 0 | driveway | — |
| C02 | 1 | 0 | 0 | 0 | garage | — |
| C03 | 2 | 0 | 0 | 0 | garage | — |
| C04 | 2 | 0 | 0 | 0 | garage | — |
| C05 | 6 | 0 | 0 | 0 | driveway | — |
| C06 | 0 | 20 | 0 | 20 | surface | 109 |
| C07 | 0 | 12 | 4 | 8 | surface | 109 |
| C08 | 0 | 32 | 0 | 32 | surface | 109 |
| C09 | 0 | 32 | 8 | 24 | surface | 109 |
| C10 | 0 | 80 | 16 | 64 | underground / 2 levels | 338 |
| C11 | 0 | 180 | 24 | 156 | surface | 109 |
| C12 | 0 | 240 | 48 | 192 | underground / 3 levels | 372 |
| C13 | 0 | 480 | 80 | 400 | underground / 5 levels | 440 |
| C14 | 0 | 120 | 120 | 0 | underground / 2 levels | — |

Rentable capacity is not occupied leases or income. No lease receipts are recognized until someone actually rents a bay. Large-house two-bay supply does not change the current one-car household cap.

### Work, materials, staff and utilities

| ID | Proposed work points | Materials units | Skilled posts | Building power units | Building water units | Building utility bill/month |
|---|---:|---:|---:|---:|---:|---:|
| C01 | 5 | 5 | 0 | 1 | 0.09 | 15 |
| C02 | 10 | 10 | 0 | 1 | 0.20 | 20 |
| C03 | 24 | 20 | 0 | 3 | 0.40 | 50 |
| C04 | 86 | 15 | 0 | 1 | 0.23 | 21 |
| C05 | 277 | 44 | 0 | 4 | 0.76 | 78 |
| C06 | 700 | 322 | 2 | 8 | 2.00 | 180 |
| C07 | 890 | 140 | 1 | 9 | 2.11 | 195 |
| C08 | 2,432 | 381 | 2 | 25 | 6.00 | 550 |
| C09 | 2,402 | 376 | 2 | 26 | 5.74 | 547 |
| C10 | 7,768 | 1,255 | 6 | 69 | 13.97 | 1,388 |
| C11 | 12,957 | 2,025 | 12 | 135 | 30.02 | 2,851 |
| C12 | 23,336 | 3,769 | 14 | 199 | 35.25 | 3,753 |
| C13 | 57,073 | 9,172 | 28 | 503 | 70.35 | 8,547 |
| C14 | 23,249 | 3,704 | 12 | 263 | 35.40 | 4,400 |

Water unit = 10,000 gallons/month. Power is the model template/grid unit; this prototype does not claim an engineering energy audit. Building draws exclude residents' separate direct demand. Work points become duration through funded builder throughput and backlog, not a fixed finish timer. The legacy work differences are not independently calibrated by fitting the capital costs.

### Illustrative apartment common-fee budget

| ID | Average common call: no optional parking receipts | Average common call: every optional bay let at reference quote | Private repairs outside fee/home/month |
|---|---:|---:|---:|
| C01 | — | — | 200 |
| C02 | — | — | 372 |
| C03 | — | — | 822 |
| C04 | — | — | 256 |
| C05 | — | — | 258 |
| C06 | 221 | 194 | 45 |
| C07 | 350 | 314 | 65 |
| C08 | 311 | 256 | 67 |
| C09 | 305 | 265 | 66 |
| C10 | 382 | 246 | 75 |
| C11 | 310 | 262 | 64 |
| C12 | 406 | 227 | 86 |
| C13 | 452 | 232 | 102 |
| C14 | 1,243 | 1,243 | 299 |

These are fee BUDGET SCENARIOS, not final condo charges or a forecast that all parking rents. They allocate 70% of bare apartment repair cost to common costs and 30% to private costs, plus shared parking repairs, building utilities and skilled staffing at $4,500/post/month. That 70/30 split is an uncalibrated accounting assumption. Actual fees use fixed category weights, not equal charges in a mixed building. RE pays unsold shares. Property tax, mortgage payments and private repairs are outside this common call. Employer payroll additions, reserves/insurance not yet represented, management profit and household-specific bills are not included. Parking surplus remains common cash; it is not fictitious manager profit.

### Financing sensitivity

The reference rents are gross capital-return anchors for a neutral, unlevered comparison. They are not a forecast for a debt-funded landlord. The following is the mean monthly home rent needed merely to meet repairs, base staff pay, building utilities, default property tax and INTEREST if 85% of the whole project were borrowed at an assumed 6%, with no optional parking receipts. Principal repayments, tax relief and profit are excluded. This is an accounting sensitivity, not the game's calibrated rent target; glut behavior may allow losses.

| ID | Reference mean rent | Mean cash-cost-plus-interest requirement, assumed 85% loan at 6% |
|---|---:|---:|
| C01 | 2,126 | 2,634 |
| C02 | 4,089 | 5,045 |
| C03 | 7,668 | 9,598 |
| C04 | 2,694 | 3,332 |
| C05 | 2,224 | 2,802 |
| C06 | 924 | 1,348 |
| C07 | 1,463 | 2,122 |
| C08 | 1,490 | 2,137 |
| C09 | 1,339 | 1,941 |
| C10 | 1,520 | 2,340 |
| C11 | 1,271 | 1,874 |
| C12 | 1,728 | 2,661 |
| C13 | 2,037 | 3,123 |
| C14 | 6,185 | 8,581 |

### Inputs and reproducibility

The benchmark price level is normalized to the catalogue's present-day figures, not the simulated city's current nominal money. The read-only base-model getter probe returned factor 0.025883463843, wage SKILLED 4,500/month, power tariff 10/unit and water tariff 50/unit after normalization. It ran with -Xmx128m after committed-memory checks (51.7% before compilation).

1. Shipped structural anchors before land: House 446,000, Studio block 14,388,000, Low-Rise 16,982,000. The House budget is provisionally decomposed into 421,000 nonparking structure and 25,000 garage; that decomposition is a design assumption, not an observed garage in the current model.
2. Assign the existing apartment comparators 40/75 m² homes and 80% net/gross efficiency. Solving their two cost totals gives a fixed service cost per home of 77,329.365079 and 2,050.412698 per gross m². Those areas are assumptions; fitting two costs does not independently validate them.
3. Above four storeys, the area-based height multiplier is H = 1 + 0.008 × (floors - 4). It is a proposed cost slope, not a new measured constant. Quality factors apply to private fitout; common floor area is charged once.
4. Apartment bare cost = 77329.365079 × homes + 2050.412698 × [gross area × H + sum(unit count × private area × (finish factor - 1))]. Detached-house area cost is 2,863.921958/private m² plus the same fixed service cost and included parking.
5. Private driveway capital 5,000/bay; private garage 25,000/bay; surface shared parking 5,000/bay; underground 70,000/bay through two levels, plus 10% for each deeper level. These are explicit draft budgets. Each surface/private bay reserves 350 sq ft including access. Basement floor area/levels are checked independently.
6. Land is paid once. Surface-parking land value is allocated to parking; underground parking does not buy the same overlapping plot again. Included bay capital/land goes into the relevant home quote; optional bay capital/land stays in the separately rented asset pool. An assertion checks home capital plus optional-parking capital equals total project capital.
7. Reference home rent = allocated home capital × 0.058 / 12; separate bay rent uses its allocated parking capital with the same anchor. Final class prices, scarcity, carrying debt and lease lag still require market calibration. Sale prices and mortgage payments are not construction costs and cannot be fixed by this table.
8. Materials/work: retain the existing House/Studio/Low-Rise budgets as anchors, with explicit added surface-parking inputs for the latter two. Other apartment budgets scale the Low-Rise materials and work by gross area and H; detached houses scale the House. Private driveway/garage/underground draft work budgets are 1/2/10 points per bay. These are proposed recipes, especially the extrapolated work points; real construction-path calibration is unfinished.
9. New ordinary apartment utility budgets scale the Low-Rise's building draws by private area; power also uses H/finish and a garage allowance. Existing three utility budgets are retained. Water units are 10,000 gallons/month. Skilled post counts are explicit proposals at the measured base wage, not a staffing study.
10. The 70/30 common/private repair split is a provisional allocation, not another repair charge. Total common + private repairs equal 1% of structure/parking cost per year. All fees remain aggregate cohort flows.

Checks establish template counts, stated floor/site/garage area limits and financial allocation. Funding, actual construction completion, household affordability, occupancy and class-price clearing have not yet been tested in the full game. The two measured apartment cost anchors are reproduced before parking; materials/cash/land allocations conserve the capital budget.

### Affordability and parking limits visible in this draft

At the measured base-model wage ladder, monthly gross pay per worker is 3,460 unskilled, 4,500 skilled, 5,500 college, 8,200 professional, 9,600 senior professional and 15,600 elite. Household earners, tax, benefits, investment income and savings still matter. A P600 reference quote of about 16,900 exceeds even one elite worker's gross wage: those homes cannot be assumed occupied merely because they were built. Wealth and other income, a lower price or vacancy must resolve that.

The earlier scratch city had about 19 cars per 100 door-seeking households at month 605. This proposal supplies around 25-100 shared bays per 100 apartment homes, depending on form. That is not evidence they would all rent. Mature-city demand and alternative parking ratios remain calibration work; the fee table deliberately shows zero/all optional leases as scenarios.

The model's existing low-rise construction workload is much higher per door than the House/Studio budgets. New point budgets borrowed from it are exposed here as draft inputs, not validated build durations. They need a caused builder-path comparison alongside OG1.

### Reproducing the complete numerical proposal

Save this standard-library Python prototype under `target/res1/` and run
it with the study worktree's source inputs. It writes a report and its
input/result JSON beside itself. It does not instantiate a game or write
saves. Its small offline floor-packing check verifies static templates;
the proposed simulation still stores only aggregate stock/cell cohorts.

```python
"""Complete candidate catalogue, a benchmark prototype, NOT game implementation.

Anchor ordinary building costs to the two shipped apartment templates, after
explicitly ASSUMING 40/75 m2 homes and 80% net efficiency. All new geometry,
parking, quality, staff and height budgets are proposals for review.
"""
from pathlib import Path
from math import ceil
from collections import Counter
import json

HERE = Path(__file__).resolve().parent
REPO = Path(r'C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-real-estate-study')
raw = json.loads((REPO/'src/main/resources/buildings.json').read_text())['buildings']
old = {b['id']: b for b in raw if b['id'] in (0, 1, 6)}
KSTUDIO = (old[1]['cashCost'] + 18*old[1]['constructionMaterials'])*1000
KLOW = (old[6]['cashCost'] + 18*old[6]['constructionMaterials'])*1000
KHOUSE = (old[0]['cashCost'] + 18*old[0]['constructionMaterials'])*1000
G_STUDIO, G_LOW = 80*40/.8, 63*75/.8
AREA_RATE = (KLOW - 63/80*KSTUDIO)/(G_LOW-63/80*G_STUDIO)
FIXED = (KSTUDIO-AREA_RATE*G_STUDIO)/80
HOUSE_RATE = (KHOUSE-25000-FIXED)/120
LAND = 50                 # benchmark model dollars per square foot, not a forecast
YIELD = .058              # existing RealEstate.LANDLORD_YIELD, gross anchor
PROPERTY_TAX = .015       # model default; no special relief in this comparison
REPAIR = .01              # existing MAINTENANCE_PER_YEAR
WAGE, POWER_PRICE, WATER_PRICE = 4500, 10, 50 # actual base-model getters
SF_PER_M2, STALL_SF = 10.7639104167, 350

# key: area, bedrooms, normal capacity, private-finish factor
U = {
 'H55':(55,1,2,1), 'H120':(120,3,6,1), 'H240':(240,4,6,1.25),
 'D90':(90,2,4,1), 'T100':(100,3,6,1),
 'S':(40,0,2,1), 'F':(75,2,4,1), 'L':(110,3,6,1),
 'A':(160,3,6,1.25), 'V':(240,4,6,1.4), 'X':(600,5,6,1.6),
 'P140':(140,3,6,1.4), 'P180':(180,3,6,1.5),
 'P360':(360,4,6,1.6), 'P600':(600,5,6,1.75),
}

def b(code, name, floors, mix, lot, *, efficiency=.8, gross=None,
      house=False, private=0, private_kind='garage', shared=0,
      park_kind='surface', included=None, basement_levels=0, basement_sf=0,
      staff=0, legacy=None, floor_plan=None):
    return dict(code=code,name=name,floors=floors,mix=mix,lot=lot,
      efficiency=efficiency,gross=gross,house=house,private=private,
      private_kind=private_kind,shared=shared,park_kind=park_kind,
      included=included or {},basement_levels=basement_levels,
      basement_sf=basement_sf,staff=staff,legacy=legacy,floor_plan=floor_plan)

BUILDINGS = [
 b('C01','Compact house',1,{'H55':1},4000,house=True,private=1,private_kind='driveway'),
 b('C02','Family house',2,{'H120':1},8000,house=True,private=1,legacy=0),
 b('C03','Large house',2,{'H240':1},12000,house=True,private=2),
 b('C04','Duplex',2,{'D90':2},10000,efficiency=.9,private=2),
 b('C05','Terrace',3,{'T100':6},18000,efficiency=.9,private=6,private_kind='driveway'),
 b('C06','Studio block',4,{'S':80},25000,shared=20,staff=2,legacy=1),
 b('C07','Mixed walk-up',3,{'S':8,'F':12,'L':4},22000,gross=2160,
   shared=12,included={'L':1},staff=1,
   floor_plan=[(1,{'L':2,'F':3,'S':3}),(1,{'L':1,'F':5,'S':2}),(1,{'L':1,'F':4,'S':3})]),
 b('C08','Family low-rise',3,{'F':63},60000,shared=32,staff=2,legacy=6,
   floor_plan=[(3,{'F':21})]),
 b('C09','Mixed mid-rise',8,{'S':16,'F':40,'L':8},24000,shared=32,included={'L':1},staff=2,
   floor_plan=[(8,{'S':2,'F':5,'L':1})]),
 b('C10','Mixed tower',20,{'S':48,'F':96,'L':12,'P140':4},24000,gross=760*20,
   shared=80,park_kind='underground',included={'L':1,'P140':1},
   basement_levels=2,basement_sf=15000,staff=6,
   floor_plan=[(2,{'L':5}),(13,{'S':1,'F':7}),(1,{'S':1,'F':4,'L':2}),
               (1,{'S':12,'F':1}),(1,{'S':14}),(1,{'S':8}),(1,{'P140':4})]),
 b('C11','Large apartment block',12,{'S':120,'F':216,'L':24},100000,
   shared=180,included={'L':1},staff=12,
   floor_plan=[(12,{'S':10,'F':18,'L':2})]),
 b('C12','Large high-rise',40,{'S':120,'F':232,'L':44,'P180':4},40000,gross=1000*40,
   shared=240,park_kind='underground',included={'L':1,'P180':1},
   basement_levels=3,basement_sf=30000,staff=14),
 b('C13','Very tall tower',80,{'S':240,'F':480,'L':76,'P360':4},50000,gross=1000*80,
   shared=480,park_kind='underground',included={'L':1,'P360':1},
   basement_levels=5,basement_sf=40000,staff=28),
 b('C14','Large-home luxury tower',36,{'A':61,'V':48,'X':8,'P600':3},30000,gross=1200*36,
   shared=120,park_kind='underground',included={'A':1,'V':1,'X':1,'P600':1},
   basement_levels=2,basement_sf=22000,staff=12,
   floor_plan=[(9,{'A':4,'V':1}),(13,{'A':1,'V':3}),(3,{'A':4}),(8,{'X':1}),
               (2,{'P600':1.5}),(1,{})]),
]

def pack(items, capacity):
    """Offline recipe area check only. Not simulation state per home/floor."""
    bins=[]
    for key, count in sorted(items.items(), key=lambda p: -U[p[0]][0]):
        for _ in range(count):
            a=U[key][0]
            for z in bins:
                if sum(U[k][0]*n for k,n in z.items())+a<=capacity+1e-6:
                    z[key]+=1; break
            else: bins.append(Counter({key:1}))
    return bins

results=[]
for z in BUILDINGS:
    mix=z['mix']; n=sum(mix.values()); net=sum(U[k][0]*v for k,v in mix.items())
    gross=z['gross'] or net/z['efficiency']; footprint=gross/z['floors']*SF_PER_M2
    height=1+.008*max(0,z['floors']-4)
    quality_extra=sum(U[k][0]*(U[k][3]-1)*v for k,v in mix.items())
    q_area=sum(U[k][0]*U[k][3]*v for k,v in mix.items())/net
    if z['house']:
        bare=FIXED*n + HOUSE_RATE*sum(U[k][0]*U[k][3]*v for k,v in mix.items())
    else: bare=FIXED*n+AREA_RATE*(gross*height+quality_extra)
    if z['legacy']==1: assert abs(bare-KSTUDIO)<.01
    if z['legacy']==6: assert abs(bare-KLOW)<.01
    private_price=5000 if z['private_kind']=='driveway' else 25000
    private_cost=z['private']*private_price
    if z['park_kind']=='surface': bay=5000; parking_land=STALL_SF*LAND
    else:
        bay=70000*(1+.1*max(0,z['basement_levels']-2)); parking_land=0
    shared_cost=z['shared']*bay
    inc=sum(mix[k]*p for k,p in z['included'].items()); optional=z['shared']-inc
    assert optional>=0
    surface=z['shared'] if z['park_kind']=='surface' else 0
    assert footprint+(z['private']+surface)*STALL_SF<=z['lot']+1e-6,(z['code'],'site')
    if z['basement_levels']:
        assert z['basement_sf']<=z['lot']
        assert z['basement_levels']*z['basement_sf']>=z['shared']*STALL_SF
    land_all=z['lot']*LAND
    land_housing=land_all-z['shared']*parking_land
    cost=bare+private_cost+shared_cost+land_all
    park_asset=bay+parking_land
    unit_quotes={}
    for key, count in mix.items():
        a, bedrooms, capacity, q=U[key]
        if z['house']: private_bare=FIXED+HOUSE_RATE*a*q
        else:
            common_bare=bare-sum((FIXED+AREA_RATE*U[k][0]*U[k][3])*v for k,v in mix.items())
            private_bare=FIXED+AREA_RATE*a*q+common_bare*a/net
        home_asset=private_bare+land_housing*a/net+private_cost*a/net+z['included'].get(key,0)*park_asset
        unit_quotes[key]=dict(count=count,asset=home_asset,rent=home_asset*YIELD/12)
    housing_asset=sum(x['count']*x['asset'] for x in unit_quotes.values())
    assert abs(housing_asset+optional*park_asset-cost)<.01,(z['code'],'capital double count')
    rent=sum(x['count']*x['rent'] for x in unit_quotes.values())/n
    if z['legacy'] is not None:
        omat=old[z['legacy']]['constructionMaterials']; work=old[z['legacy']]['constructionPoints']
        if z['legacy']==0:
            mats=omat; work=work
        else:
            mats=ceil(omat+surface*.1);work+=surface
    elif z['house']:
        mats=ceil(net*9/120+z['private']*(.1 if z['private_kind']=='driveway' else 1))
        work=ceil(net*8/120*q_area+z['private']*(1 if z['private_kind']=='driveway' else 2))
    else:
        mats=ceil(gross*height*377/G_LOW+surface*.1+(z['shared']-surface)*2+
                  z['private']*(.1 if z['private_kind']=='driveway' else 1))
        work=ceil(gross*height*2400/G_LOW+surface+(z['shared']-surface)*10+
                  z['private']*(1 if z['private_kind']=='driveway' else 2))
    cash=bare+private_cost+shared_cost-mats*18000
    assert cash>=0
    if z['legacy']==0: assert abs(bare+private_cost-KHOUSE)<.01
    if z['legacy'] is not None:
        power=old[z['legacy']]['electricity'];water=old[z['legacy']]['water']
    elif z['house']:
        power=max(1,ceil(net/120*q_area));water=.2*net/120
    else:
        power=max(1,ceil(25*net/(63*75)*height*q_area+
                         .01*z['shared']*max(1,z['basement_levels'])))
        water=6*net/(63*75)
    util=POWER_PRICE*power+WATER_PRICE*water
    repair_total=REPAIR*(bare+private_cost+shared_cost)/12
    common_repair=REPAIR*(.7*bare+shared_cost)/12 if z['shared'] or z['staff'] else 0
    private_repair=repair_total-common_repair
    fee_zero_park=(common_repair+z['staff']*WAGE+util)/n if common_repair else 0
    fee_full_park=max(0,(common_repair+z['staff']*WAGE+util-optional*park_asset*YIELD/12)/n) if common_repair else 0
    operating=repair_total+z['staff']*WAGE+util+cost*PROPERTY_TAX/12
    # An explicitly hypothetical financing sensitivity, not a measured loan rate.
    carry_85_at_6=(operating+cost*.85*.06/12)/n
    item={**z,'N':n,'net':net,'gross_actual':gross,'footprint_sf':footprint,
        'capacity':sum(U[k][2]*v for k,v in mix.items()),'height_factor':height,
        'bare':bare,'private_cost':private_cost,'shared_cost':shared_cost,
        'cost':cost,'cost_home':cost/n,'rent':rent,
        'rent_min':min(x['rent'] for x in unit_quotes.values()),
        'rent_max':max(x['rent'] for x in unit_quotes.values()),
        'included':inc,'optional':optional,'park_rent':park_asset*YIELD/12,
        'mats':mats,'cash':cash,'work':work,'power':power,'water':water,
        'utility_bill':util,'upkeep_home':repair_total/n,
        'private_upkeep_home':private_repair/n,'tax_home':cost*PROPERTY_TAX/12/n,
        'fee_zero_park':fee_zero_park,'fee_full_park':fee_full_park,'quotes':unit_quotes}
    item['carry_85_at_6']=carry_85_at_6
    # Floor area checks for explicit schedules; fractional P rows represent
    # a duplex's share on each of two floors in the static template only.
    if z['floor_plan']:
        total=Counter(); levels=0
        for count, level_mix in z['floor_plan']:
            floor_net_limit=.75 if z['code'] in ('C10','C12','C13','C14') else .9 if z['code'] in ('C04','C05') else .8
            assert sum(U[k][0]*v for k,v in level_mix.items())<=gross/z['floors']*floor_net_limit+1e-6
            total.update({k:count*v for k,v in level_mix.items()});levels+=count
        assert total==Counter(mix) and levels==z['floors'],z['code']
    elif z['code']=='C12':
        normal=Counter(mix);normal.pop('P180')
        assert len(pack(normal,750))<=39
        assert 4*180<=750
    elif z['code']=='C13':
        normal=Counter(mix);normal.pop('P360')
        assert len(pack(normal,750))<=78
        assert 4*360/2<=750
    elif z['code']=='C11':
        assert len(pack(mix,gross/z['floors']*.8))<=z['floors']
    results.append(item)

data={'status':'UNCALIBRATED NUMERICAL PROPOSAL, not observed game outcomes',
      'assumptions':dict(land_per_sf=LAND,area_rate=AREA_RATE,fixed_per_home=FIXED,
       house_area_rate=HOUSE_RATE,gross_yield=YIELD,property_tax=PROPERTY_TAX,
       maintenance=REPAIR,skilled_wage=WAGE,power_price=POWER_PRICE,water_price=WATER_PRICE),
      'unit_types':U,'buildings':results}
(HERE/'catalogue-data.json').write_text(json.dumps(data,indent=2),encoding='utf-8')

def money(x):return f'{x:,.0f}'
def mix_str(z):return ' + '.join(f'{n} {k}' for k,n in z['mix'].items())
out=[]
out += ['# Complete numerical catalogue proposal','',
 '**Status: numerical design draft for review; not calibrated game outcomes.**',
 'All prices below are present-day benchmark model dollars. Land is $50/sq ft; material is $18,000/unit; ordinary demand pressure is 1. No inflation, finance, scarcity or rebate is added to the reference rents.', '',
 'C01-C14 are comparison labels, not permanent game building IDs or batch IDs. Existing catalogue IDs remain unchanged.', '',
 'The existing templates anchor the cost calculation. New area, height, layout, quality, parking, staff and work budgets are proposals. A cost-derived reference quote is not an observed rent or a promised transaction price.','']
out+=['## Dwelling types','', '| Code | Internal m² | Bedrooms | Normal places | Finish factor |', '|---|---:|---:|---:|---:|']
for k,(a,bd,cap,q) in U.items():out.append(f'| {k} | {a} | {bd} | {cap} | {q:.2f} |')
out+=['','## Full building list','', '| ID | Form | Floors | Homes | Normal capacity | Unit mix | Residential gross m² | Plot sq ft |', '|---|---|---:|---:|---:|---|---:|---:|']
for z in results:out.append(f"| {z['code']} | {z['name']} | {z['floors']} | {z['N']} | {z['capacity']} | {mix_str(z)} | {money(z['gross_actual'])} | {money(z['lot'])} |")
out+=['','## Capital and reference rents','', '| ID | Whole project incl. land/parking $m | Cost per home equivalent $k | Average reference home rent $/month | Reference rent range $/month | Repair budget/home/month | Default tax/home/month |', '|---|---:|---:|---:|---:|---:|---:|']
for z in results:out.append(f"| {z['code']} | {z['cost']/1e6:.2f} | {z['cost_home']/1000:,.1f} | {money(z['rent'])} | {money(z['rent_min'])}–{money(z['rent_max'])} | {money(z['upkeep_home'])} | {money(z['tax_home'])} |")
out+=['','## Capital breakdown','', '| ID | Building structure/fitout $m | Parking construction $m | Land $m | Nonmaterial construction cash $m |', '|---|---:|---:|---:|---:|']
for z in results:out.append(f"| {z['code']} | {z['bare']/1e6:.3f} | {(z['private_cost']+z['shared_cost'])/1e6:.3f} | {z['lot']*LAND/1e6:.3f} | {z['cash']/1e6:.3f} |")
out+=['','Cost per home equivalent includes the whole physical project divided by homes; it is not the sale price. Reference home rent excludes capital allocated to separately rented parking. Private/included parking is in home rent; separately let capacity has its own quote below. Owner sale values depend on HO1 actual prices/credit and transferred common interests.','',
 '## Reference rent by apartment type','',
 '| ID | S 40 m² | F 75 m² | L 110 m² | Penthouse variant / monthly rent |', '|---|---:|---:|---:|---:|']
for z in results:
    if z['code'] in ('C06','C07','C08','C09','C10','C11','C12','C13'):
        vals=[money(z['quotes'][k]['rent']) if k in z['quotes'] else '—' for k in ('S','F','L')]
        p=[f"{k[1:]} m² / {money(v['rent'])}" for k,v in z['quotes'].items() if k.startswith('P')]
        out.append(f"| {z['code']} | {vals[0]} | {vals[1]} | {vals[2]} | {', '.join(p) or '—'} |")
z=next(x for x in results if x['code']=='C14')
out+=['','C14 luxury-tower rents: '+ '; '.join(f"{k}, {U[k][0]} m²: {money(v['rent'])}/month ({v['count']} homes)" for k,v in z['quotes'].items())+'.','',
 '## Parking','', '| ID | Private included | Shared physical | Included shared rights | Separately rentable capacity | Type / basement levels | Separate reference $/space/month |', '|---|---:|---:|---:|---:|---|---:|']
for z in results:
    kind=('driveway' if z['private_kind']=='driveway' else 'garage') if z['private'] else (f"underground / {z['basement_levels']} levels" if z['basement_levels'] else 'surface')
    out.append(f"| {z['code']} | {z['private']} | {z['shared']} | {z['included']} | {z['optional']} | {kind} | {money(z['park_rent']) if z['optional'] else '—'} |")
out+=['','Rentable capacity is not occupied leases or income. No lease receipts are recognized until someone actually rents a bay. Large-house two-bay supply does not change the current one-car household cap.','',
 '## Work, materials, staff and utilities','', '| ID | Proposed work points | Materials units | Skilled posts | Building power units | Building water units | Building utility bill/month |', '|---|---:|---:|---:|---:|---:|---:|']
for z in results:out.append(f"| {z['code']} | {z['work']:,} | {z['mats']:,} | {z['staff']} | {z['power']} | {z['water']:.2f} | {money(z['utility_bill'])} |")
out+=['','Water unit = 10,000 gallons/month. Power is the model template/grid unit; this prototype does not claim an engineering energy audit. Building draws exclude residents\' separate direct demand. Work points become duration through funded builder throughput and backlog, not a fixed finish timer. The legacy work differences are not independently calibrated by fitting the capital costs.','',
 '## Illustrative apartment common-fee budget','', '| ID | Average common call: no optional parking receipts | Average common call: every optional bay let at reference quote | Private repairs outside fee/home/month |', '|---|---:|---:|---:|']
for z in results:out.append(f"| {z['code']} | {money(z['fee_zero_park']) if z['fee_zero_park'] else '—'} | {money(z['fee_full_park']) if z['fee_zero_park'] else '—'} | {money(z['private_upkeep_home'])} |")
out+=['','These are fee BUDGET SCENARIOS, not final condo charges or a forecast that all parking rents. They allocate 70% of bare apartment repair cost to common costs and 30% to private costs, plus shared parking repairs, building utilities and skilled staffing at $4,500/post/month. That 70/30 split is an uncalibrated accounting assumption. Actual fees use fixed category weights, not equal charges in a mixed building. RE pays unsold shares. Property tax, mortgage payments and private repairs are outside this common call. Employer payroll additions, reserves/insurance not yet represented, management profit and household-specific bills are not included. Parking surplus remains common cash; it is not fictitious manager profit.','']
out+=['## Financing sensitivity','',
 'The reference rents are gross capital-return anchors for a neutral, unlevered comparison. They are not a forecast for a debt-funded landlord. The following is the mean monthly home rent needed merely to meet repairs, base staff pay, building utilities, default property tax and INTEREST if 85% of the whole project were borrowed at an assumed 6%, with no optional parking receipts. Principal repayments, tax relief and profit are excluded. This is an accounting sensitivity, not the game\'s calibrated rent target; glut behavior may allow losses.','',
 '| ID | Reference mean rent | Mean cash-cost-plus-interest requirement, assumed 85% loan at 6% |', '|---|---:|---:|']
for z in results:out.append(f"| {z['code']} | {money(z['rent'])} | {money(z['carry_85_at_6'])} |")
out+=['','## Inputs and reproducibility','',
 'The benchmark price level is normalized to the catalogue\'s present-day figures, not the simulated city\'s current nominal money. The read-only base-model getter probe returned factor 0.025883463843, wage SKILLED 4,500/month, power tariff 10/unit and water tariff 50/unit after normalization. It ran with -Xmx128m after committed-memory checks (51.7% before compilation).', '',
 f'1. Shipped structural anchors before land: House {KHOUSE:,.0f}, Studio block {KSTUDIO:,.0f}, Low-Rise {KLOW:,.0f}. The House budget is provisionally decomposed into 421,000 nonparking structure and 25,000 garage; that decomposition is a design assumption, not an observed garage in the current model.',
 f'2. Assign the existing apartment comparators 40/75 m² homes and 80% net/gross efficiency. Solving their two cost totals gives a fixed service cost per home of {FIXED:,.6f} and {AREA_RATE:,.6f} per gross m². Those areas are assumptions; fitting two costs does not independently validate them.',
 '3. Above four storeys, the area-based height multiplier is H = 1 + 0.008 × (floors - 4). It is a proposed cost slope, not a new measured constant. Quality factors apply to private fitout; common floor area is charged once.',
 f'4. Apartment bare cost = {FIXED:.6f} × homes + {AREA_RATE:.6f} × [gross area × H + sum(unit count × private area × (finish factor - 1))]. Detached-house area cost is {HOUSE_RATE:,.6f}/private m² plus the same fixed service cost and included parking.',
 '5. Private driveway capital 5,000/bay; private garage 25,000/bay; surface shared parking 5,000/bay; underground 70,000/bay through two levels, plus 10% for each deeper level. These are explicit draft budgets. Each surface/private bay reserves 350 sq ft including access. Basement floor area/levels are checked independently.',
 '6. Land is paid once. Surface-parking land value is allocated to parking; underground parking does not buy the same overlapping plot again. Included bay capital/land goes into the relevant home quote; optional bay capital/land stays in the separately rented asset pool. An assertion checks home capital plus optional-parking capital equals total project capital.',
 '7. Reference home rent = allocated home capital × 0.058 / 12; separate bay rent uses its allocated parking capital with the same anchor. Final class prices, scarcity, carrying debt and lease lag still require market calibration. Sale prices and mortgage payments are not construction costs and cannot be fixed by this table.',
 '8. Materials/work: retain the existing House/Studio/Low-Rise budgets as anchors, with explicit added surface-parking inputs for the latter two. Other apartment budgets scale the Low-Rise materials and work by gross area and H; detached houses scale the House. Private driveway/garage/underground draft work budgets are 1/2/10 points per bay. These are proposed recipes, especially the extrapolated work points; real construction-path calibration is unfinished.',
 '9. New ordinary apartment utility budgets scale the Low-Rise\'s building draws by private area; power also uses H/finish and a garage allowance. Existing three utility budgets are retained. Water units are 10,000 gallons/month. Skilled post counts are explicit proposals at the measured base wage, not a staffing study.',
 '10. The 70/30 common/private repair split is a provisional allocation, not another repair charge. Total common + private repairs equal 1% of structure/parking cost per year. All fees remain aggregate cohort flows.', '',
 'Checks establish template counts, stated floor/site/garage area limits and financial allocation. Funding, actual construction completion, household affordability, occupancy and class-price clearing have not yet been tested in the full game. The two measured apartment cost anchors are reproduced before parking; materials/cash/land allocations conserve the capital budget.','',
 '## Affordability and parking limits visible in this draft','',
 'At the measured base-model wage ladder, monthly gross pay per worker is 3,460 unskilled, 4,500 skilled, 5,500 college, 8,200 professional, 9,600 senior professional and 15,600 elite. Household earners, tax, benefits, investment income and savings still matter. A P600 reference quote of about 16,900 exceeds even one elite worker\'s gross wage: those homes cannot be assumed occupied merely because they were built. Wealth and other income, a lower price or vacancy must resolve that.', '',
 'The earlier scratch city had about 19 cars per 100 door-seeking households at month 605. This proposal supplies around 25-100 shared bays per 100 apartment homes, depending on form. That is not evidence they would all rent. Mature-city demand and alternative parking ratios remain calibration work; the fee table deliberately shows zero/all optional leases as scenarios.', '',
 'The model\'s existing low-rise construction workload is much higher per door than the House/Studio budgets. New point budgets borrowed from it are exposed here as draft inputs, not validated build durations. They need a caused builder-path comparison alongside OG1.','']
(HERE/'numerical-catalogue.md').write_text('\n'.join(out),encoding='utf-8',newline='\n')
print(f'PASS: {len(results)} building candidates; cost allocation, counts, site/garage area and specified floor schedules')
print(f'ANCHORS: fixed/home={FIXED:.6f}, apartment gross-m2 rate={AREA_RATE:.6f}, detached private-m2 rate={HOUSE_RATE:.6f}')
for z in results: print(z['code'],z['name'],f"cost={z['cost']/1e6:.3f}m avgRent={z['rent']:.0f} fee(noPark)={z['fee_zero_park']:.0f}")
```

The small read-only getter probe for the wage/utility reference inputs
is below. Compile/run with section 11's classpath and memory guard, one
JVM at a time (`-Xmx128m`). It exited 0; no game or user save is opened.

```java
package ham.citybuildersim;
import java.util.Locale;
public class ReferenceInputs {
  public static void main(String[] args) {
    UtilitiesHandler u = new UtilitiesHandler();
    double scale = Good.MATERIALS.worldImportPrice() / Good.MATERIALS.todaysImportPrice();
    System.out.printf(Locale.ROOT, "scale=%.12f%n", scale);
    for (PayTier tier : PayTier.values())
      System.out.printf(Locale.ROOT, "wage.%s=%.6f%n", tier.name(), tier.getTodaysWage()*1000);
    System.out.printf(Locale.ROOT, "power.price=%.6f%nwater.price=%.6f%n", u.getPricerPerWatt()*1000/scale, u.getPricePerWaterUnit()*1000/scale);
  }
}
```

Validation: fourteen template counts and capacities, nonnegative cash
components, exact capital allocation without double-counted parking,
plot/basement capacity and specified floor schedules pass the prototype.
Existing cost anchors are reproduced before new parking. Cohort
split/merge checks remain green. Full-game funding, completions,
household budget/tenure behavior, actual prices/parking use and historical
availability still require calibration. No model/UI implementation or
shipping approval is implied by this numerical draft.
