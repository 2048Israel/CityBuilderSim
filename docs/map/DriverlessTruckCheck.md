# DriverlessTruckCheck.java - 419 lines · 23 methods · 1 constants · harnesses

`ham/citybuildersim/DriverlessTruckCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The driverless truck (R6b, FE1): the Truck Depot's second piece of equipment, the kit that ends the driver's cab, and what a depot fitted with it does
> to Haulage: a truck that carries more tonnes a month and a depot that keeps oversight posts where it kept drivers.
> 
> WHY. The kit changes three things at once that a month's figures would hide if one were wrong: how many tonnes a truck carries (so how many trucks the
> same need asks for), how many driver posts the depots offer (so the labour market and the wage bill), and what the kit is worth to a depot (so whether
> it is ever fitted and what the patent rent is billed on). And it must change none of them where the piece does not exist: a city founded today's way has
> every depot standing "fitted" only because every count there is, so its month is today's, to the bit. Each figure is therefore held to the model's own
> constants (TONNES_A_TRUCK_MONTH, SHIFT_HOURS, DRIVERLESS_HOURS, DRIVERS_A_TRUCK, OVERSIGHT_A_TRUCK) and getters, never a number typed here, and each
> condition is caused from both sides: the month before and the month of FE1's date, a depot lacking the kit and a depot with it, a city founded the 1900 way
> and one founded today's.
> 
> What this has to prove:
> 
>   1. THE DIALS: a truck carries TONNES_A_TRUCK_MONTH itself, to the bit, while every truck is driven and TONNES_A_TRUCK_MONTH x DRIVERLESS_HOURS /
>      SHIFT_HOURS with none, between them in a straight line; a truck keeps DRIVERS_A_TRUCK driver posts driven and OVERSIGHT_A_TRUCK driverless; the
>      share is clamped to [0, 1] and one that is not a number reads 1
>   2. BEFORE FE1: in a city founded the 1900 way with depots, the month before FE1's date, the kit is NOT_KNOWN on the depot and the driven share is 1
>   3. AT FE1: the kit is known; its gross gain is driverGain() at the city's own room, wage and truck capital, positive, and the verdict is the one
>      EquipmentFit's rule gives (PAYS or NOT_YET) with the patent rent on that gain taken off; a city whose research FOUND FE1 earlier knows the kit from
>      the month it found it, and owes no rent on it from then
>   4. EVERY DEPOT FITTED: the driven share is 0, a truck carries the driverless figure, the same need asks for fewer trucks by that factor, and the depots
>      offer fewer DIPLOMA posts by exactly the driverless trucks times (DRIVERS_A_TRUCK - OVERSIGHT_A_TRUCK)
>   5. HALF THE DEPOTS FITTED: the share is the room-weighted share of the depots lacking the kit, and it stands after a month is played
>   6. A DEFAULT CITY: founded today's way, with depots, past FE1's month, the share is exactly 1 and a truck carries TONNES_A_TRUCK_MONTH, even where a
>      count of the kit is put on its depot by hand
>   7. A PLAYED COUNT: the 1900 village played 24 months from FE1's month, every other piece in it put fitted (a village has no builders' crews to work a
>      queue with six points in it), the months its driven share was below 1, which is more than none if the
>      verdict of section 3 was PAYS
> 
> Every fixture is FittingCheck.village() (placed without construction, in a GameFiles.scratch() directory: no save slot is written), said over by
> unfitTheFounding() in a city founded the 1900 way, with its month set by reflection where a date is the premise (ResearchCheck.atMonth()).

**Uses:** [Haulage](Haulage.md) (61), [Game](Game.md) (20), [EquipmentFit](EquipmentFit.md) (19), [BuildingsStacks](BuildingsStacks.md) (9), [Equipment](Equipment.md) (6), [Good](Good.md) (6), [ResearchCheck](ResearchCheck.md) (6), [JobType](JobType.md) (5), [LicenceFees](LicenceFees.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (3), [Forms1900Check](Forms1900Check.md) (2), [DiscoveryCheck](DiscoveryCheck.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [Sector](Sector.md) (2), [Founding](Founding.md) (1), [Freight](Freight.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [ResearchViewCheck](ResearchViewCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 149 | 1. the dials |
| 185 | 2. before FE1 |
| 207 | 3. at FE1 |
| 257 | 4. every depot fitted |
| 329 | 5. half the depots fitted |
| 358 | 6. a default city |
| 381 | 7. a played count |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `DriverlessTruckCheck.TRUCK` | `EquipmentCheck.TRUCK, KIT = "driverless-kit"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 46 | `static int passes` |  |
| 48 | `static PrintStream quiet` |  |
| 210 | `static EquipmentFit.Kind verdictAtFe1` | Whether the kit paid at FE1's month in the village of section 3, for section 7's label. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 377 | **type** `public class DriverlessTruckCheck` | The driverless truck (R6b, FE1): the Truck Depot's second piece of equipment, the kit that ends the driver's cab, and what a depot fitted with it does to Haulage: a truck that carries more tonnes a month and a depot t... |
| 52 | 4 | `static void check(String label, boolean ok)` |  |
| 58 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 63 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 67 | 5 | `static void quietly(Runnable r)` |  |
| 74 | 7 | `static void ensureQuiet()` | The streams the borrowed fixtures are quiet through, set once and never over another harness's. |
| 83 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly (the player's button: the auto-builder's pass first). |
| 88 | 3 | `static int fe1()` | FE1's month in history: the model's own, never a literal. |
| 92 | 3 | `static Haulage haulage(Game g)` |  |
| 96 | 3 | `static BuildingsStacks depots(Game g)` |  |
| 100 | 3 | `static Equipment kit(Game g)` |  |
| 105 | 6 | `static Game village(String label, boolean founded1900)` | A village with two Truck Depots, founded the 1900 way (stood up as that founding leaves it: every building without every piece) or today's. |
| 113 | 10 | `static double lackingShare(Game g)` | The share of the depots' truck room lacking the kit, written out from the stacks' own counts and the templates' rooms. |
| 125 | 3 | `static double driverWage(Game g)` | What a DIPLOMA driver post costs a month, as the economy pays it. |
| 130 | 5 | `static double truckCapitalMonth(Game g)` | What a truck's capital costs a month: its wear and its return on what it cost, at the market's price. |
| 136 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the dials (lines 149-184)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 33 | `static Game one()` |  |

### 2. before FE1 (lines 185-206)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 19 | `static void two(Game g) throws Exception` |  |

### 3. at FE1 (lines 207-256)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 44 | `static void three(Game g) throws Exception` |  |

### 4. every depot fitted (lines 257-328)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 63 | `static void four()` |  |
| 324 | 4 | `static double dieselPrice(Game g)` | The diesel's price Haulage's running estimate reads: the market's landed, the world's before one. |

### 5. half the depots fitted (lines 329-357)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 26 | `static void five()` |  |

### 6. a default city (lines 358-380)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 20 | `static void six() throws Exception` |  |

### 7. a played count (lines 381-419)

| line | len | member | says |
|---:|---:|---|---|
| 383 | 36 | `static void seven() throws Exception` |  |

