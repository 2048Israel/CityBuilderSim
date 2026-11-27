# WaterCheck.java - 533 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/WaterCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Sanity harness for water production, demand, throttling and billing - and,
> since 0.7.59, the fresh water limit and the desalination plant (sections
> 9 to 12; batch J2, spec-land 2.3). Not part of the game.
> 
> WHY ONE HARNESS: the spec named a new WaterCheck for the limit, and this
> one was already the water's - sections 1 to 8 build a bare handler, told
> no land, which has no limit, and every one of their figures is the old
> formula still; the new sections build the limit on top of them.

**Uses:** [Game](Game.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (11), [CityLand](CityLand.md) (11), [BuildAdvice](BuildAdvice.md) (9), [UtilitiesHandler](UtilitiesHandler.md) (8), [GameFiles](GameFiles.md) (6), [BuildingManager](BuildingManager.md) (5), [LandParcel](LandParcel.md) (5), [ServicesManager](ServicesManager.md) (4), [LandManager](LandManager.md) (4), [BuildCard](BuildCard.md) (4), [CityNeeds](CityNeeds.md) (3), [Sector](Sector.md) (2), [World](World.md) (2), [MiningCheck](MiningCheck.md) (2), [PriceLevel](PriceLevel.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Retail](Retail.md) (1), [SaveFileCheck](SaveFileCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 64 | · 1. template draws |
| 73 | · 2. demand = buildings + people |
| 95 | · 3. the ratio bites |
| 111 | · 4. a plant fixes it |
| 123 | · 5. split books |
| 167 | · 6. the ratio throttles output |
| 181 | · 7. billing is symmetric with power |
| 206 | · 8. household affordability |
| 222 | 9-10. the fresh water limit, on a handler (0.7.59) |
| 322 | 11. on a city: the limit is its land's, and the coast |
| 460 | 12. the rights on a load |

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 13 | 521 | **type** `public class WaterCheck` | Sanity harness for water production, demand, throttling and billing - and, since 0.7.59, the fresh water limit and the desalination plant (sections 9 to 12; batch J2, spec-land 2.3). |
| 17 | 6 | `static void check(String label, double actual, double expected)` |  |
| 24 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 30 | 5 | `static void near(String label, double actual, double expected)` | A check against a figure, to a part in a billion of it - for sums of land areas that are floating point. |
| 36 | 5 | `static void quietly(Runnable work)` |  |
| 42 | 5 | `static double[] wages()` |  |
| 48 | 173 | `public static void main(String[] args) throws Exception` |  |

### 9-10. the fresh water limit, on a handler (0.7.59) (lines 222-321)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 98 | `static void freshWaterLimit(double[] fullFill)` |  |

### 11. on a city: the limit is its land's, and the coast (lines 322-459)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 130 | `static void onACity()` |  |
| 454 | 5 | `static<T> T quiet(java.util.function.Supplier<T> work)` |  |

### 12. the rights on a load (lines 460-533)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 72 | `static void rightsOnALoad() throws Exception` |  |

