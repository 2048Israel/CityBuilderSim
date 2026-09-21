# SafetyType.java - 68 lines · 4 methods · 0 constants · model

`ham/citybuildersim/SafetyType.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> What a safety building does: police, or prison cells.
> 
> Jerus, 2026-09-11: "we need police, and also prison". The same shape as
> CareType and EducationType, and for the same reason those exist - a rule
> that reads "Police Station" off the button breaks the first time somebody
> renames it, so what the capacity is FOR is data on the template and in
> buildings.json, and BuildingDataCheck compares it.
> 
> WHAT CAPACITY COUNTS
> 
>   POLICE  sworn officers. Coverage is officers against FULL_PER_100K per
>           hundred thousand people - twice Canada's 180, Jerus's call - and
>           coverage is what deters and what catches. See Crime.
>   PRISON  cells. A cell holds one prisoner for the six months of a
>           sentence; a city whose police catch more than its cells can hold
>           has people caught and not held.
> 
> Both are cut by staffing like everything else the city runs: a police
> station with no officers patrols nothing, and a jail with no guards holds
> nobody.

**Uses:** [Healthcare](Healthcare.md) (1), [Crime](Crime.md) (1)

**Used by (8):** [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [CrimeCheck](CrimeCheck.md), [Game](Game.md), [ServicesScreen](ServicesScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 28 | `SafetyType.NONE` | Everything that is not a safety building. |
| 31 | `SafetyType.POLICE` | Police stations and headquarters: officers. |
| 34 | `SafetyType.PRISON` | Jails and penitentiaries: cells. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final String label` |  |
| 37 | `private final String unit` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 44 | **type** `public enum SafetyType` | What a safety building does: police, or prison cells. |
| 39 | 4 | `SafetyType(String label, String unit)` |  |
| 44 | 1 | `public String getLabel()` |  |
| 47 | 1 | `public String getUnit()` | What one unit of capacity is: "officers", "cells". |
| 62 | 6 | `public double foundingCapacity()` | What the city has before it builds anything. |

