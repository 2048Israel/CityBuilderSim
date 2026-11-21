# Real Estate: homes, buildings and the households choosing them

RES1, opened 2026-11-20. **Working draft: scope discussion, not ready to ship.**
Branch `study/real-estate`, cut from `origin/main` at `a9bfcd070`.
`sh tools/batch-id.sh RES1` passed. Jerus is describing the upgrade; neither
the economic design nor the implementation batches have been settled.

## 1. What the model does now

These are source findings, not new playtest measurements. The catalogue at
this base has three residential buildings:

| Building | Dwellings | People per dwelling |
|---|---:|---:|
| House | 1 | 6 |
| Studio Apartments | 80 | 2 |
| Low-Rise Apartments | 63 | 4 |

Source: `src/main/resources/buildings.json`, residential ids 0, 1 and 6;
`BuildingsTemplate.homeSize()` divides capacity by dwellings. A building
currently supplies a single dwelling size.

`FamilyModel.match()` places larger households first, in the smallest available
home that fits. Households with dependants cannot enter studios, even when
their headcount fits. A small household can take a larger dwelling; a household
can crowd into a smaller family dwelling when its fitting homes are gone.
This placement pools income tiers by household shape: it does not choose a
better home for a richer household.

`RealEstate.mayRetire()` prevents retirement while households are unplaced or
doubled up and otherwise asks whether the building's segment has spare doors.
When an allowed decision reaches `Game.retire()`, the building is physically
scrapped, its land is released, materials are sold and financing is settled.
The message saying "Sold" therefore does not describe a standing home
transferred to a household. This is the disposal path Jerus now wants replaced
for Real Estate after home ownership exists.

`RealEstate` owns the homes. Its two rents cover studios and family dwellings,
per person of dwelling capacity. `rentRequired()` uses the cheapest new supply
cost and `LANDLORD_YIELD`; `rentBreakEven()` measures the standing stock's
carry. `targetFor()` combines segment scarcity and the company's carry lift,
waiving the lift in a glut. `repriceRent()` moves prices over `LEASE_MONTHS`.
The planner orders quantities against jobs, coming jobs, family-door shortages,
and homes already on site. More residential types alone do not establish what
currently holds completions back.

`EconomyManager.repriceHousingCosts()` selects the cheapest cost per person
overall and within the two size segments; `RealEstate.priceForSegment()`
returns only the studio or family price. Adding a luxury building to the
catalogue therefore does not give its dwellings a distinct quality market.

Cars already have new and used markets, borrowing, fuel, road effects and a
transit alternative (`Motoring`). `Household.cars` is capped at one car per
household. Residential parking supply is not a tracked capacity; the comment
beside that stock explicitly leaves garages and additional cars for later work.

The initial `./batch-check.sh --fast` passed (compile, maps, zero firm Stale
findings). Generated date-only changes were restored. No measuring JVM or
ensemble has been started by this study.

## 2. Design being discussed

Jerus asks for more residential forms, including bigger and taller buildings;
household fit and preferences, including the elite's; and garage/parking
provision. Exact forms, quantities, costs, heights, preference rules, parking
rules and price formation still require discussion and measurement.

The distinction accepted so far is between a building and the dwellings it
contains. Some apartment buildings may contain multiple dwelling types. A
studio, a family apartment and a penthouse were examples, not an approved
catalogue or a measured unit mix. Size, quality and height are separate ideas;
no income group has been assigned an exclusive building type.

### Questions the study should examine next

These are recommendations for discussion, not accepted mechanics or calibrated
rules:

- Home layout and space as well as headcount: which households fit, which
  merely prefer a home, and how affordability limits the choice.
- Prices by the housing actually supplied and demanded: quality and size must
  have an economic meaning beyond the existing two rents. Included parking
  and separately rented parking must be counted once in household and landlord
  books, and consistently in HO1's purchase valuation and mortgage test.
- Density's full cost: land and surface parking saved, construction work and
  time, upkeep, staffing, power and water. Measure the tradeoff before choosing
  building quantities, heights or a cheapest form.
- Builders' choice and vacancies: a shortage of affordable family dwellings
  can coexist with empty dwellings of another kind. Distinguish unsuitable,
  unaffordable and still-under-construction supply, coordinating with OG1.
- Stable homes and tenure: mixed buildings may contain rented and owned homes.
  Household changes need defined stay/move/sell outcomes that preserve capacity,
  equity and mortgage balances; coordinate with HO1/HO2 rather than taking over
  their household-transition work without an explicit decision.
- Historical availability and costs: tie forms and features to the existing
  year/technology framework where justified, and retain the wage-following
  1900-home instruction measured against take-home pay.

## 3. Jerus's calls

**★1 Mixed dwellings in some buildings — accepted.** Jerus asked whether some
buildings should have mixed dwelling types, and answered the explanation with
"thats a great idea". Recommendation given: permit a mix in some apartment
buildings while retaining buildings that supply one type. The mixes are open.

**★2 Household preferences — open.** His words: "the elite may prefer different
types". Recommendation discussed: households can compromise on preferences
when money or availability limits them. Whether the elite prefer large houses,
luxury apartments, penthouses or combinations remains unanswered. Existing
hard suitability rules must be distinguished from preferences.

**★3 Garages and parking — requested scope, mechanics open.** His words:
"garage/parking lot factored in, cause cars are a thing right". The discussion
distinguished driveways, private garages, surface lots and underground parking.
He accepted the recommended billing arrangement with "ok": private provision
belonging to a dwelling is included in its housing price; shared spaces are
rented separately unless a space is included with that dwelling, and extra
spaces are separately charged. Quantities, allocation and prices remain open.
Recommendation: measure the existing car stock and its one-car ceiling before
settling parking quantities or changing car ownership; charge physical provision
through the model's land, construction and upkeep flows.

**★4 Real Estate pictogram UI — accepted as later work.** His words:
"after this also a ui update for real estate right? such that you know how
industyr has pictograms and stuff, i feel like it would be good to implement
one for real estate as well, but thats later after this study". Recommendation:
a separate follow-up batch, REU1, after the study and the model figures it will
display are settled. Use the industry's pictogram presentation as a reference;
the screen must read the housing model's figures. No UI is being implemented
in this study.

**★5 The 1900 home — standing instruction.** The initiating brief chooses costs
that follow wages, measured against take-home pay too, revisited after HO1.
`batch/R1g:docs/queue/R1g-notes.md` sections 2, 3 and 5 are the prior evidence;
its `homeScale` of 0.289185 is a branch finding, not this study's measurement
or a calibration for every new building.

**★6 Sell housing instead of demolishing it — requested after home ownership.**
Jerus: "after HO real estate instead of demolishing it should just sell either
the units of the apartment and charge a monthly fee like a condo but not rent,
or sell its houses"; "if no one then lower and lower prices, but not demolish".
This is the required outcome for the company's disposal of housing: list houses
or apartment dwellings for sale, reduce unsold asking prices over time, and
preserve the standing homes rather than scrap them to exit a holding.
Apartment buyers become owners and pay a condo-style shared-cost fee instead
of rent. Houses are sold as homes; their owners bear their ownership costs.

Recommendation: account for common building costs explicitly across owned and
rented units, preserving the agreed parking billing. The fee's recipient and
any management remuneration must be specified; an owner's existing upkeep
must not also pay the same shared repairs. An unsold holding still has an
owner and carrying costs. Markdown pace is to be measured, not a literal
chosen in this discussion. Listing/asking prices must be distinguished from
cleared transaction prices. Reconcile intended disposal with HO1's rule that
the company buys below its rental value, so it does not buy back the stock it
is trying to sell. The outcome for an occupied rental being offered for sale
also needs an explicit rule, not an accidental displacement by the transfer.

## 4. Batches and their proofs

This order is provisional; model batches and their harnesses are not yet
allocated. First settle the scope and measure the current model, then prototype
the proposed mechanics beside the study and specify the model batches and
their caused harnesses. New catalogue ids must be appended, not renumbered.

REU1 is the requested later UI batch (`sh tools/batch-id.sh REU1` passed).
Its scope is a Real Estate pictogram in the spirit of the industry picture,
with its exact figures and placement derived from the completed study. Its
model/view checks must reconcile pictured quantities with the model, and its
screen must preserve the existing kept redraw behaviour. Its final brief and
position relative to HO1 will be completed with the implementation order.

The later instruction "after HO" makes the sale/condo-fee disposal mechanic
depend on home ownership. Separate changes that must precede HO1 from changes
that use HO1; the initiating instruction to queue the study's batches before
HO1 cannot place this dependent implementation ahead of ownership itself.
Its harness must cause both ordinary and distressed disposal, exercise an
unsold listing's markdown, and prove that a sale preserves dwellings/capacity,
transfers ownership and relevant debt correctly, conserves money, replaces
rent by the proper owner/shared costs, and survives a save/load. A mixed
building and the last unit sold must be included so common costs always have
an accountable payer.

## 5. HO1 and work already in flight

HO1's home-price anchor is the landlord's net monthly rent times twelve over
the cap rate. It also assumes builders build for sale beyond
`RealEstate.BUILD_MARGIN`, owners pay `MAINTENANCE_PER_YEAR`, and renters alone
pay rent. New dwelling types, preferences and parking can change those costs,
the rental segments and the landlord's value. Exactly which parts this study
takes over remains open; do not silently implement HO1 here.

HO1's brief will also need condo/shared-cost obligations in owner budgets and
mortgage eligibility, a way to preserve dwelling identity/type within a mixed
building, and an explicit interface for the later sale-instead-of-demolition
rule. HO2's owner transitions must use actual sale proceeds and timing.

The study has recommended through the bus that HO1 wait until those assumptions
are settled. The orchestrator owns `QUEUE.md` and decides the queue action.
HT1, HS3 and HC1 precede HO1 at this base; HO4, HO2 and LP1 follow it.

Coordinate with FB15's order pricing/equity funding, MC2's mortgage headroom,
RN2's unsaved `RealEstate.ownedCapacity`, and BO1-H's business-owner housing
match. OG1 already investigates quantity planners and the builders' backlog:
housing completions held by that queue must not be attributed to a lack of
residential forms. No files belonging to these model batches have been edited.
