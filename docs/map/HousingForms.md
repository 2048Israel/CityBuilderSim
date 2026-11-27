# HousingForms.java - 112 lines · 11 methods · 3 constants · model

`ham/citybuildersim/HousingForms.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The fourteen fixed forms, geometry and capital partitions of the RES1 catalogue.
> WHY (RES3): mixed homes need one actual project quote, partitioned without charging
> optional parking twice. Constants are the study's disclosed starting recipes.

**Uses:** [HousingRecipe](HousingRecipe.md) (47), [BuildingsTemplate](BuildingsTemplate.md) (3), [BuildingType](BuildingType.md) (1), [Good](Good.md) (1), [JobType](JobType.md) (1)

**Used by (7):** [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [DenominationCheck](DenominationCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [MonetaryCheck](MonetaryCheck.md), [ResearchCheck](ResearchCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 14 | `HousingForms.SQFT_PER_M2` | `10.7639104167` | Square feet in a square metre, used for site geometry only. |
| 16 | `HousingForms.BAY_SQFT` | `350` | Area including access for one surface parking bay. |
| 37 | `HousingForms.ALL` | `catalogue()` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 102 | **type** `public final class HousingForms` | The fourteen fixed forms, geometry and capital partitions of the RES1 catalogue. |
| 12 | 1 | `private HousingForms()` |  |
| 17 | 19 | **type** `public record Form(String code, int id, String name, int floors, double grossArea, double plot, int since, ...` |  |
| 21 | 1 | `public double footprint()` _(in HousingForms.Form)_ |  |
| 23 | 1 | `public double basementFootprint()` _(in HousingForms.Form)_ | Fixed basement geometry from the selected catalogue, in square feet. |
| 25 | 1 | `public int basementLevels()` _(in HousingForms.Form)_ | Basement levels belonging to the same fixed parking recipe. |
| 26 | 2 | `public double garden()` _(in HousingForms.Form)_ |  |
| 29 | 1 | `public boolean stepFree()` _(in HousingForms.Form)_ | Only explicit ground-level homes have step-free layouts in this catalogue. |
| 30 | 5 | `public double homeCapital(String dwelling, double structure, double land)` _(in HousingForms.Form)_ |  |
| 36 | 1 | `private static void add(List<Form> out, Form form)` |  |
| 38 | 60 | `private static List<Form> catalogue()` |  |
| 98 | 1 | `public static Form of(int id)` |  |
| 100 | 12 | `static void installFallback(List<BuildingsTemplate> templates)` | The built-in fallback carries exactly the same physical definitions as buildings.json. |

