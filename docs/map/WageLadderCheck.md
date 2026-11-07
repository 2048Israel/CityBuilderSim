# WageLadderCheck.java - 419 lines · 14 methods · 2 constants · harnesses

`ham/citybuildersim/WageLadderCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The wage ladder a city is founded on (R0d): today's PayTier ladder in every city but one founded the 1900 way, which pays 1901's, the
> census's. Not part of the game.
> 
> WHAT THIS HAS TO PROVE (R0d, 2026-11-06; docs/studies/research-1900.md 5.2 and 12.1; docs/queue/R0-notes.md call 2). A city founded in 1900
> pays about a quarter of today's real wage with the 1901 census's ratios between the skills, and only that city: the default way is
> today's ladder to the bit, so every default trace stays what it was. What is checkable:
> 
>   1. THE TABLE: one 1901 rung per PayTier rung, in PayTier's order, each with a comparator, a positive year's earnings and a source key or an
>      estimate flag (the tiers the census does not price are flagged), rising up the ladder; the 1901 anchor is its unskilled rung through
>      the class's one conversion
>   2. TODAY'S LADDER IS PayTier'S TO THE BIT: every ratio and every rung equal, no tolerance; a city founded the default way is on it, its
>      minimum wage, its dial's bounds, its wages, its rents, its grant and its pension's base the ones it always opened on
>   3. A CITY FOUNDED THE 1900 WAY STARTS ON 1901'S LADDER: the ladder, the minimum wage at the anchor, every wage the anchor times the
>      ladder's ratio (the population manager's too) before any month, the bounds the anchor's multiples, the rents, the grant and the
>      pension's base struck on the anchor, and an unskilled wage about a quarter of a default city's
>   4. THE MARKET STILL MOVES A 1901 LADDER: on a bare market founded on it, posts above supply raise a band's wage above its base, posts
>      equal to supply leave it walking to base times the curve's multiple at one at the damping rate, the cost of living lifts the base,
>      and the dial is bounded by the 1901 anchor's multiples (a floor under today's lowest setting can be set)
>   5. SAVED AND LOADED: a 1900 city keeps its ladder, its minimum wage (one under today's lowest setting, which only the ladder's bounds let
>      back), its wages, its bounds and its rents; a default city loads today's, into a fresh Game and into one that played a 1900 city
>   6. PLAYED: a 1900 village plays 24 months with every wage positive and finite and the allocator's band bases the 1901 ladder's
>      (the lowest ungated ratio of the band), which differ from today's
> 
> Every fixture CAUSES its condition: the 1900 city is founded through newGame(Founding.withUnfitted(true)) as the player's door is; the
> floor set for 5 is below today's lowest setting, so a load that clamped it to today's bounds would show; the market's tight and even
> months are posts and supply handed to advanceMonth(). Figures are asserted against the model's own constants and methods.

**Uses:** [WageLadder](WageLadder.md) (66), [LabourMarket](LabourMarket.md) (36), [JobType](JobType.md) (34), [Game](Game.md) (18), [PayTier](PayTier.md) (16), [TaxPolicy](TaxPolicy.md) (11), [RealEstate](RealEstate.md) (7), [Founding](Founding.md) (6), [WageBand](WageBand.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [PopulationManager](PopulationManager.md) (4), [GameFiles](GameFiles.md) (3), [FittingCheck](FittingCheck.md) (3), [PolicyDates](PolicyDates.md) (3)

## Sections

| line | section |
|---:|---|
| 98 | 1. the table |
| 130 | 2. today's ladder is PayTier's to the bit |
| 176 | 3. a 1900 city starts on 1901's ladder |
| 233 | 4. the market still moves a 1901 ladder |
| 310 | 5. saved and loaded |
| 374 | 6. played |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `WageLadderCheck.DEFAULT` | `Founding.defaults()` |  |
| 79 | `WageLadderCheck.THE_1900_WAY` | `Founding.defaults().withUnfitted(true)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 384 | **type** `public class WageLadderCheck` | The wage ladder a city is founded on (R0d): today's PayTier ladder in every city but one founded the 1900 way, which pays 1901's, the census's. |
| 42 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 47 | 1 | `static void check(String label, boolean ok)` |  |
| 50 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance. |
| 55 | 5 | `static void quietly(Runnable r)` |  |
| 62 | 5 | `static Game found(String label, Founding founding)` | A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. |
| 69 | 3 | `static void step(Game g, int months)` | One press of the clock, as the player's button makes it, quietly. |
| 74 | 3 | `static Game village(String label, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()). |
| 81 | 16 | `public static void main(String[] args) throws Exception` |  |

### 1. the table (lines 98-129)

| line | len | member | says |
|---:|---:|---|---|
| 100 | 29 | `static void sectionOne()` |  |

### 2. today's ladder is PayTier's to the bit (lines 130-175)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 43 | `static void sectionTwo()` |  |

### 3. a 1900 city starts on 1901's ladder (lines 176-232)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 54 | `static void sectionThree()` |  |

### 4. the market still moves a 1901 ladder (lines 233-309)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 74 | `static void sectionFour()` |  |

### 5. saved and loaded (lines 310-373)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 61 | `static void sectionFive()` |  |

### 6. played (lines 374-419)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 43 | `static void sectionSix()` |  |

