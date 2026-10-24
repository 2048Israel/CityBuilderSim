# CareType.java - 170 lines · 7 methods · 3 constants · model

`ham/citybuildersim/CareType.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> What a healthcare building actually does.
> 
> WHY THIS EXISTS AT ALL
> 
> The fourteen healthcare buildings shipped knowing their capacity but not what
> that capacity was FOR. A Nursing Home's 220 and a Walk-in Clinic's 2,500 are
> both "capacity", and the only thing distinguishing them was the name on the
> button - which is to say, nothing a line of code could read. The comment in
> BuildingManager said "capacity is the age band it serves"; that sentence was
> true and it lived in a comment, which meant every future caller would have had
> to re-derive it by matching strings. A rule stated only in prose is a rule
> nobody can check.
> 
> So the care type is data now, on the template and in buildings.json, and
> BuildingDataCheck compares it like every other field.
> 
> THE DENOMINATOR IS THE POINT
> 
> Each type names the slice of the population it has to cover, and that slice is
> what coverage divides by. Childcare beds serve babies and children, so a city
> with a baby boom needs more of them without anybody deciding so; senior care
> serves seniors, which is the ageing problem stated as arithmetic; general care
> serves everyone, and it is general care that decides how much of the workforce
> is off sick this month.
> 
> BURIAL AND CREMATION SERVE THE DEAD, which is not a band of the living, so
> servedBy() is false for every band and populationServed() returns zero. Their
> capacity is measured against DEATHS - plots consumed permanently in the burial
> case, throughput per month in the cremation case - and that is a different
> denominator with a different unit, so it deliberately does not pretend to
> share this one. NONE is the same shape for the opposite reason: every
> non-healthcare building in the game has it.

**Uses:** [AgeBand](AgeBand.md) (8), [PopulationCohorts](PopulationCohorts.md) (1)

**Used by (29):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConstructionScreen](ConstructionScreen.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [Inbox](Inbox.md), [InfrastructureScreen](InfrastructureScreen.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `CareType.NONE` | Everything that is not a healthcare building. |
| 43 | `CareType.CHILDCARE` | Childcare centres (the daycares until 0.7.71): babies and children. |
| 46 | `CareType.GENERAL` | Clinics and hospitals: the whole city, and the sick rate comes from here. |
| 49 | `CareType.SENIOR` | Home care through long-term care: seniors. |
| 52 | `CareType.BURIAL` | Cemeteries. |
| 55 | `CareType.CREMATION` | Crematoria. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 143 | `CareType.SENIOR_NEED_AGAINST_AN_ELDER` | `.19` | 5.5% of 70-84 in care against 29.6% of the over-85s - see placesPerHead. |
| 146 | `CareType.CHILDCARE_UNDER_SIX` | `.32` | A baby's place in childcare: the 32% of Canada's children aged 0 to 5 in centre-based child care in 2025 (Statistics Canada, Child care arrangements, 2025, The Daily 2025-10-21). |
| 149 | `CareType.CHILDCARE_SIX_TO_TWELVE` | `.35 *.52` | A child's place: 35% of school-attending 6-to-12s in child care, 52% of them in before- or after-school programmes (Statistics Canada, The Daily 2026-03-12), so 0.35 x 0.52 = 18.2% - derived here, the release gives no... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final String label` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 134 | **type** `public enum CareType` | What a healthcare building actually does. |
| 59 | 3 | `CareType(String label)` |  |
| 63 | 3 | `public String getLabel()` |  |
| 68 | 3 | `public boolean servesTheLiving()` | True for the types whose coverage is measured against living residents. |
| 73 | 8 | `public boolean servedBy(AgeBand band)` | True if a resident in this band is somebody this type has to have room for. |
| 88 | 3 | `public boolean countsPlaces()` | True for the types whose need is counted in places rather than heads: each band wants a share of a place (senior care since 2026-09-15, childcare since 0.7.139), so a screen says "places needed", not "people to serve". |
| 135 | 6 | `public double placesPerHead(AgeBand band)` | How much of a place one person in this band needs. |
| 161 | 9 | `public double populationServed(PopulationCohorts cohorts)` | How many people this type is on the hook for, given the pyramid - in places: a head a place for general care, each band at its weight for senior care and childcare (placesPerHead()). |

