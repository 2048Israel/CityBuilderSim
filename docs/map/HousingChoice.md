# HousingChoice.java - 357 lines · 26 methods · 11 constants · model

`ham/citybuildersim/HousingChoice.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> A bounded household-cell to dwelling-class allocation, retaining valid leases first.
> WHY (RES3): size-only packing hid premium vacancy and let an enum's first cell
> win a rationed home. Residual paths maximize suitable placements before preferences.

**Uses:** [HousingInventory](HousingInventory.md) (12), [FamilyModel](FamilyModel.md) (12)

**Used by (10):** [DenominationCheck](DenominationCheck.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HousingChoiceCheck](HousingChoiceCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [RealEstate](RealEstate.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 13 | `HousingChoice.QUALITY_WEIGHT` | `.35` | Quality's base utility weight; richer tiers value finish more. |
| 15 | `HousingChoice.BEDROOM_PENALTY` | `.10` | A missing bedroom's soft layout penalty, from the numerical prototype. |
| 17 | `HousingChoice.GARDEN_WEIGHT` | `.35` | A family's utility for actual outdoor space, saturating at 100 square metres. |
| 19 | `HousingChoice.ACCESS_WEIGHT` | `.60` | Retired households' utility for an explicitly step-free dwelling. |
| 21 | `HousingChoice.BUDGET_WEIGHT` | `.20` | Remaining-budget sensitivity, after all recurring necessities are reserved. |
| 23 | `HousingChoice.BASE_AREA` | `30` | Base desired private floor area, in square metres. |
| 25 | `HousingChoice.AREA_PER_PERSON` | `20` | Desired private floor area added per household member. |
| 27 | `HousingChoice.AREA_PER_TIER` | `.15` | Each tier's increment in the desired-area vector. |
| 29 | `HousingChoice.YOUNG_AREA` | `.9` | Young adults' desired area relative to established households. |
| 31 | `HousingChoice.QUALITY_PER_TIER` | `.20` | Each tier's extra preference for finish quality. |
| 33 | `HousingChoice.GARDEN_AREA` | `100` | Outdoor square metres at which the family garden term saturates. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 350 | `final int to, cost` |  |
| 350 | `double left` |  |
| 350 | `Edge reverse` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 10 | 348 | **type** `public final class HousingChoice` | A bounded household-cell to dwelling-class allocation, retaining valid leases first. |
| 11 | 1 | `private HousingChoice()` |  |
| 34 | 5 | **type** `public record Demand(String key, double count, int size, int children, boolean retired, int tier, boolean y...` |  |
| 36 | 1 | `{ ... }` _(in HousingChoice.Demand)_ |  |
| 37 | 1 | `double targetArea()` _(in HousingChoice.Demand)_ |  |
| 39 | 9 | **type** `public record Offer(HousingInventory.Key key, double count, int capacity, int bedrooms, double area, double...` |  |
| 42 | 5 | `{ ... }` _(in HousingChoice.Offer)_ |  |
| 49 | 7 | **type** `public record Funding(double full, double half, double sponsored)` | Contract quantities, independent of donated physical room and the cell's preference vector. |
| 50 | 1 | `{ ... }` _(in HousingChoice.Funding)_ |  |
| 51 | 1 | `public double households()` _(in HousingChoice.Funding)_ |  |
| 52 | 1 | `public double doors()` _(in HousingChoice.Funding)_ |  |
| 53 | 1 | `public Funding scale(double factor)` _(in HousingChoice.Funding)_ |  |
| 54 | 1 | `public Funding plus(Funding other)` _(in HousingChoice.Funding)_ |  |
| 57 | 10 | **type** `public record Lease(String cell, HousingInventory.Key home, double count, double bill, double households, F...` | count is occupied door-equivalents; funding preserves the actual contracts through coalescence and saves. |
| 58 | 1 | `public Lease(String cell, HousingInventory.Key home, double count, double bill)` _(in HousingChoice.Lease)_ |  |
| 59 | 5 | `{ ... }` _(in HousingChoice.Lease)_ |  |
| 64 | 1 | `public Lease scale(double factor)` _(in HousingChoice.Lease)_ |  |
| 65 | 1 | `public Lease plus(Lease other)` _(in HousingChoice.Lease)_ |  |
| 67 | 2 | **type** `public record Result(List<Lease> leases, double kept, double suitable, double crowded, double unaffordable,...` |  |
| 69 | 1 | `private static void quantity(double v)` |  |
| 70 | 4 | `public static boolean suitable(Demand d, Offer o, boolean emergency)` |  |
| 74 | 1 | `public static boolean affordable(Demand d, Offer o)` |  |
| 75 | 8 | `public static double score(Demand d, Offer o)` |  |
| 84 | 3 | `public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month)` | Equivalent nodes share their allocation pro rata; input order confers no priority. |
| 88 | 51 | `public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month, boolean emergency)` | Planning can reserve suitable homes without assuming emergency crowding in a new project. |
| 140 | 34 | `private static Result retainAndClear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month, boolean emerg...` | Existing funded door shares precede new movers; their household quantity is not a whole-door quantity. |
| 175 | 1 | **type** `private record DemandSignature(int size, int children, boolean retired, int tier, boolean young, double bud...` |  |
| 176 | 1 | **type** `record Completion(List<Lease> leases, double moved)` |  |
| 177 | 1 | **type** `private record ContractKey(String cell, HousingInventory.Key home)` |  |
| 178 | 5 | `static List<Lease> coalesce(List<Lease> leases)` |  |
| 184 | 55 | `static Completion completePaidExcess(List<Demand> demand, List<Offer> offers, List<Lease> old, int month)` | After all movers, keep old claims exact and move only paid-half excess to unused normal doors. |
| 240 | 15 | `public static Result summarize(List<Demand> demand, List<Offer> offers, List<Lease> leases, double kept, double normal, double ...` | Books the outcome of internal emergency phases without clearing or pricing again. |
| 255 | 1 | **type** `private record OfferSignature(int capacity, int bedrooms, double area, double quality, double garden, boole...` |  |
| 257 | 1 | `private static double residual(double left, double source)` | A subtraction residue is measured against its original source, before it can create a graph node. |
| 258 | 33 | `private static double allocate(List<Demand> ds, List<Offer> os, double[] need, double[] free, double[][] x, int month, boolean ...` |  |
| 297 | 53 | `private static double completeEmergency(List<Demand> ds, List<Offer> os, double[] need, double[] free, double[][] x, double[][]...` | Complete the maximum normal matching through its residual paths. |
| 350 | 1 | **type** `private static final class Edge` |  |
| 350 | 1 | `Edge(int to, double left, int cost)` _(in HousingChoice.Edge)_ |  |
| 351 | 4 | `private static long rotatedKey(String key, int month)` |  |
| 355 | 1 | `private static Edge edge(List<List<Edge>> g, int u, int v, double cap)` |  |
| 356 | 1 | `private static Edge costEdge(List<List<Edge>> g, int u, int v, double cap, int cost)` |  |

