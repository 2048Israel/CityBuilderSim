# PowerDispatchCheck.java - 413 lines · 24 methods · 1 constants · harnesses

`ham/citybuildersim/PowerDispatchCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The month's merit order (PW1, 2026-11-11; PowerDispatch, UtilitiesHandler's struck book): the load cut in six half-blocks, the
> works first, the wind at its weather, the hydro levelling the load, the fuelled rows by marginal cost, the price each half-block
> clears at, and the tariff struck from it. Pure: no Game, no I/O but the shipped rows of buildings.json.
> 
> WHY. The utility used to set the month's power by one sum, the coal plant burned nothing, the wind never stopped and the tariff
> was a constant. Every expected figure here is computed from the model's own constants (BLOCK_HOURS, BLOCK_LOAD, WIND_BLOCK,
> WIND_SWING, GRID_LOSSES, halfHours()), never typed in, and every fixture builds the rows that CAUSE its condition.
> 
> What this has to prove:
>   1. THE BLOCKS SUM TO THE MONTH: the shares of hours sum to 1, the load over the hours to 1, the half-blocks' hours to the
>      month's, the twelve seasons average about 1.
>   2. GENERATED + UNSERVED = LOAD in every half-block, on a short fuelled row, wind with a fuelled row, hydro with a fuelled row;
>      and the energy served and unserved is the energy asked.
>   3. A SHORT HALF-BLOCK CLEARS AT VOLL, the ratio is the energy served over the energy asked, to the bit.
>   4. WIND PAST A BLOCK'S LOAD is curtailed and counted; no half-block is served twice.
>   5. THE CALM HALF: wind alone at the month's mean draw leaves the calm halves short and the windy ones not.
>   6. HYDRO LEVELS THE LOAD: its water used is its water, its peak output is at least its base's, it is held to its cap, and
>      it never runs more than the residual.
>   7. THE MERIT ORDER BY MARGINAL COST: the cheaper runs first; when the fuel price moves the order and the prices swap.
>   8. FUEL = kWh x FUEL A kWh, to the bit, from the Coal Power Plant's own row of buildings.json (and the shipped rows reach
>      the template through the catalog).
>   9. THE WORKS' SURPLUS IS TAKEN FIRST, and a half-block it alone covers clears at 0.
>  10. THE STRUCK BOOK (UtilitiesHandler, no Game): the prices, the works' price, the founding tariff before a strike, the save's
>      round trip, and the income falling by the fuel bill.

**Uses:** [PowerDispatch](PowerDispatch.md) (140), [UtilitiesHandler](UtilitiesHandler.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (12), [BuildingType](BuildingType.md) (3), [Good](Good.md) (3), [BuildingCatalog](BuildingCatalog.md) (1), [PriceLevel](PriceLevel.md) (1)

## Sections

| line | section |
|---:|---|
| 90 | · 1 |
| 122 | · 2 |
| 149 | · 3 |
| 170 | · 4 |
| 188 | · 5 |
| 203 | · 6 |
| 234 | · 7 |
| 264 | · 8 |
| 322 | · 9 |
| 345 | · 10 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `PowerDispatchCheck.DRAW` | `100_000` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 374 | **type** `public class PowerDispatchCheck` | The month's merit order (PW1, 2026-11-11; PowerDispatch, UtilitiesHandler's struck book): the load cut in six half-blocks, the works first, the wind at its weather, the hydro levelling the load, the fuelled rows by ma... |
| 44 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 49 | 1 | `static void check(String label, boolean ok)` |  |
| 51 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 58 | 3 | `static double loadAt(double draw, double season, int s)` | The load asked of half-block s at this draw and season, from the model's constants. |
| 62 | 5 | `static double generated(List<PowerDispatch.Row> rows, int s)` |  |
| 68 | 1 | `static double marginal()` |  |
| 70 | 3 | `static PowerDispatch.Row fuelled(String name, double kw, double marginal)` |  |
| 74 | 15 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 90-121)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 29 | `static void blocks()` |  |

### 2 (lines 122-148)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 10 | `static void conserved(String what, List<PowerDispatch.Row> rows, PowerDispatch.Month m)` |  |
| 135 | 13 | `static void conservation()` |  |

### 3 (lines 149-169)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 18 | `static void shortAtVoll()` |  |

### 4 (lines 170-187)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 15 | `static void windCurtailed()` |  |

### 5 (lines 188-202)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 12 | `static void calmHalf()` |  |

### 6 (lines 203-233)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 28 | `static void hydroLevels()` |  |

### 7 (lines 234-263)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 27 | `static void meritOrder()` |  |

### 8 (lines 264-321)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 11 | `static JsonObject jsonRow(String name) throws Exception` | A row of the shipped buildings.json, as the file says it (the catalog's own lookup order: the working directory, then the jar). |
| 279 | 1 | `static double num(JsonObject o, String key)` |  |
| 281 | 1 | `static String text(JsonObject o, String key)` |  |
| 284 | 5 | `static BuildingsTemplate fromRow(String name) throws Exception` | A template built from a row's own keys, whatever the catalog reads of them. |
| 290 | 5 | `static BuildingsTemplate catalogRow(String name)` |  |
| 296 | 25 | `static void fuelPerKwh() throws Exception` |  |

### 9 (lines 322-344)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 20 | `static void worksFirst()` |  |

### 10 (lines 345-413)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 66 | `static void struckBook() throws Exception` |  |

