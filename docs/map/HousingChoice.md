# HousingChoice.java - 389 lines · 30 methods · 12 constants · model

`ham/citybuildersim/HousingChoice.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> A bounded household-cell to dwelling-class allocation, retaining valid leases first.
> WHY (RES3): size-only packing hid premium vacancy and let an enum's first cell
> win a rationed home. Residual paths maximize suitable placements before preferences.

**Uses:** [HousingInventory](HousingInventory.md) (13), [FamilyModel](FamilyModel.md) (12)

**Used by (11):** [DenominationCheck](DenominationCheck.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HousingChoiceCheck](HousingChoiceCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [RealEstate](RealEstate.md), [Village1900Check](Village1900Check.md)

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
| 35 | `HousingChoice.PERSONS_PER_ROOM` | `2` | The census line: the census of England and Wales called a home overcrowded past two persons a room (1891, and so to 1931), its rooms its bedrooms and a kitchen. |
| 39 | `HousingChoice.QUALITY_PER_TIER` | `.20` | Each tier's extra preference for finish quality. |
| 41 | `HousingChoice.GARDEN_AREA` | `100` | Outdoor square metres at which the family garden term saturates. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 382 | `final int to, cost` |  |
| 382 | `double left` |  |
| 382 | `Edge reverse` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 10 | 380 | **type** `public final class HousingChoice` | A bounded household-cell to dwelling-class allocation, retaining valid leases first. |
| 11 | 1 | `private HousingChoice()` |  |
| 37 | 1 | `public static int line(int bedrooms)` | The most people a door of so many bedrooms holds at the census line (H55 4, D90 6, H120 8). |
| 42 | 5 | **type** `public record Demand(String key, double count, int size, int children, boolean retired, int tier, boolean y...` |  |
| 44 | 1 | `{ ... }` _(in HousingChoice.Demand)_ |  |
| 45 | 1 | `double targetArea()` _(in HousingChoice.Demand)_ |  |
| 48 | 14 | **type** `public record Offer(HousingInventory.Key key, double count, int capacity, int bedrooms, double area, double...` | people is the most one household placed here may bring: a whole door's census line, or the line less who shares it. |
| 51 | 5 | `{ ... }` _(in HousingChoice.Offer)_ |  |
| 57 | 4 | `public Offer(HousingInventory.Key key, double count, int capacity, int bedrooms, double area, double quality, double garden, bo...` _(in HousingChoice.Offer)_ | A whole door, open to a household up to its census line. |
| 63 | 7 | **type** `public record Funding(double full, double half, double sponsored)` | Contract quantities, independent of donated physical room and the cell's preference vector. |
| 64 | 1 | `{ ... }` _(in HousingChoice.Funding)_ |  |
| 65 | 1 | `public double households()` _(in HousingChoice.Funding)_ |  |
| 66 | 1 | `public double doors()` _(in HousingChoice.Funding)_ |  |
| 67 | 1 | `public Funding scale(double factor)` _(in HousingChoice.Funding)_ |  |
| 68 | 1 | `public Funding plus(Funding other)` _(in HousingChoice.Funding)_ |  |
| 71 | 12 | **type** `public record Lease(String cell, HousingInventory.Key home, double count, double bill, double households, F...` | count is occupied door-equivalents; funding preserves the actual contracts through coalescence and saves. |
| 72 | 1 | `public Lease(String cell, HousingInventory.Key home, double count, double bill)` _(in HousingChoice.Lease)_ |  |
| 73 | 7 | `{ ... }` _(in HousingChoice.Lease)_ |  |
| 80 | 1 | `public Lease scale(double factor)` _(in HousingChoice.Lease)_ |  |
| 81 | 1 | `public Lease plus(Lease other)` _(in HousingChoice.Lease)_ |  |
| 83 | 2 | **type** `public record Result(List<Lease> leases, double kept, double suitable, double crowded, double unaffordable,...` |  |
| 85 | 1 | `private static void quantity(double v)` |  |
| 87 | 4 | `public static boolean suitable(Demand d, Offer o, boolean emergency)` | Crowding may pass the door's built capacity, never the census line its people bound carries. |
| 92 | 1 | `public static boolean halves(Demand d, Offer o)` | Two of this household to a share stay within the door's census line. |
| 93 | 1 | `public static boolean affordable(Demand d, Offer o)` |  |
| 94 | 8 | `public static double score(Demand d, Offer o)` |  |
| 103 | 3 | `public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month)` | Equivalent nodes share their allocation pro rata; input order confers no priority. |
| 107 | 3 | `public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month, boolean emergency)` | Planning can reserve suitable homes without assuming emergency crowding in a new project. |
| 111 | 51 | `public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month, boolean emergency, Set<...` | sitting: the cells whose incumbents keep their doors whatever their budget, until they can be evicted (tenure). |
| 167 | 36 | `private static Result retainAndClear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month, boolean emerg...` | Existing funded door shares precede new movers; their household quantity is not a whole-door quantity. |
| 204 | 1 | **type** `private record DemandSignature(int size, int children, boolean retired, int tier, boolean young, double bud...` |  |
| 205 | 1 | **type** `record Completion(List<Lease> leases, double moved)` |  |
| 206 | 1 | **type** `private record ContractKey(String cell, HousingInventory.Key home)` |  |
| 207 | 5 | `static List<Lease> coalesce(List<Lease> leases)` |  |
| 213 | 58 | `static Completion completePaidExcess(List<Demand> demand, List<Offer> offers, List<Lease> old, int month)` | After all movers, keep old claims exact and move only paid-half excess to unused normal doors. |
| 272 | 15 | `public static Result summarize(List<Demand> demand, List<Offer> offers, List<Lease> leases, double kept, double normal, double ...` | Books the outcome of internal emergency phases without clearing or pricing again. |
| 287 | 1 | **type** `private record OfferSignature(int capacity, int bedrooms, double area, double quality, double garden, boole...` |  |
| 289 | 1 | `private static double residual(double left, double source)` | A subtraction residue is measured against its original source, before it can create a graph node. |
| 290 | 33 | `private static double allocate(List<Demand> ds, List<Offer> os, double[] need, double[] free, double[][] x, int month, boolean ...` |  |
| 329 | 53 | `private static double completeEmergency(List<Demand> ds, List<Offer> os, double[] need, double[] free, double[][] x, double[][]...` | Complete the maximum normal matching through its residual paths. |
| 382 | 1 | **type** `private static final class Edge` |  |
| 382 | 1 | `Edge(int to, double left, int cost)` _(in HousingChoice.Edge)_ |  |
| 383 | 4 | `private static long rotatedKey(String key, int month)` |  |
| 387 | 1 | `private static Edge edge(List<List<Edge>> g, int u, int v, double cap)` |  |
| 388 | 1 | `private static Edge costEdge(List<List<Edge>> g, int u, int v, double cap, int cost)` |  |

