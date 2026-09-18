# CareType.java - 133 lines · 6 methods · 1 constants · model

`ham/citybuildersim/CareType.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (7), [PopulationCohorts](PopulationCohorts.md) (1)

**Used by (14):** [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [Inbox](Inbox.md), [PeopleScreen](PeopleScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `CareType.NONE` | Everything that is not a healthcare building. |
| 43 | `CareType.CHILDCARE` | Daycare and nurseries: babies and children. |
| 46 | `CareType.GENERAL` | Clinics and hospitals: the whole city, and the sick rate comes from here. |
| 49 | `CareType.SENIOR` | Home care through long-term care: seniors. |
| 52 | `CareType.BURIAL` | Cemeteries. |
| 55 | `CareType.CREMATION` | Crematoria. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 114 | `CareType.SENIOR_NEED_AGAINST_AN_ELDER` | `.19` | 5.5% of 70-84 in care against 29.6% of the over-85s - see placesPerHead. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final String label` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 97 | **type** `public enum CareType` | What a healthcare building actually does. |
| 59 | 3 | `CareType(String label)` |  |
| 63 | 3 | `public String getLabel()` |  |
| 68 | 3 | `public boolean servesTheLiving()` | True for the types whose coverage is measured against living residents. |
| 73 | 8 | `public boolean servedBy(AgeBand band)` | True if a resident in this band is somebody this type has to have room for. |
| 107 | 5 | `public double placesPerHead(AgeBand band)` | How much of a place one person in this band needs. |
| 124 | 9 | `public double populationServed(PopulationCohorts cohorts)` | How many people this type is on the hook for, given the pyramid. |

