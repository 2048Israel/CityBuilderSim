# SubstationCheck.java - 156 lines · 13 methods · 3 constants · harnesses

`ham/citybuildersim/SubstationCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
> WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
> the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.

**Uses:** [Game](Game.md) (12), [PowerDispatch](PowerDispatch.md) (11), [UtilitiesHandler](UtilitiesHandler.md) (10), [Substations](Substations.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (3), [BuildAdvice](BuildAdvice.md) (3), [GameVersion](GameVersion.md) (2), [GameFiles](GameFiles.md) (1), [PriceLevel](PriceLevel.md) (1), [JobType](JobType.md) (1), [BuildCard](BuildCard.md) (1), [AutoBuilder](AutoBuilder.md) (1), [CityCalendar](CityCalendar.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `SubstationCheck.CARRY` | `Substations.SUBSTATION_MW * 1000` |  |
| 18 | `SubstationCheck.JSON` | `new Gson()` |  |
| 19 | `SubstationCheck.OUT` | `System.out, QUIET = new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 142 | **type** `public final class SubstationCheck` | Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5). |
| 21 | 1 | `static void check(String label, boolean ok)` |  |
| 22 | 1 | `static boolean near(double a, double b)` |  |
| 23 | 1 | `static void quietly(Runnable work)` |  |
| 25 | 13 | `static Game city(String label, int substations)` |  |
| 40 | 14 | `static UtilitiesHandler dispatch(Game g, double peak)` | A fully staffed coal row removes generation scarcity; the city stock still supplies the physical wires. |
| 55 | 3 | `static double generated(UtilitiesHandler u, int half)` |  |
| 59 | 22 | `static void carry()` |  |
| 82 | 4 | `static JsonObject costs(UtilitiesHandler.Book book)` |  |
| 86 | 1 | `static double number(JsonObject o, String key)` |  |
| 88 | 23 | `static void tariff()` |  |
| 112 | 22 | `static void order()` |  |
| 135 | 14 | `static void saved() throws Exception` |  |
| 150 | 6 | `public static void main(String[] args) throws Exception` |  |

