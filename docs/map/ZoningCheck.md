# ZoningCheck.java - 536 lines · 16 methods · 3 constants · harnesses

`ham/citybuildersim/ZoningCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and
> LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury,
> the roll and the world's totals.
> 
> WHY. Jerus, 2026-10-08 (the project's spec-zoning.md): "the default is
> free, anything everywhere, but with zoning, you can allocate a % of total
> land or a specific km² for a specific industry, and even provide the
> land at a lower rate, or free" - and a forest law "of not building on the
> forest, or always keep a certain % of forest available". A zone that leaked
> its ground to another sector, a price the city charged and did not book, a
> roll that read the going price for zoned ground, or a cut that reached the
> protected wood would each be a lever that says one thing and does another.
> Nothing in the default playtest sets a zone (it stays byte-identical), so
> this is where zoning is played (and, since 0.7.109, ZoningViewCheck holds
> the screens' figures to it).
> 
> What this has to prove (spec-materials 4, ZoningCheck):
>   1. NOTHING SET, NOTHING MOVES: with no zones and the law at 0 and 0 the
>      free ground is owned less allocated, a sector's free ground and price
>      the going ones, the roll the books' - and a city whose zone was set
>      and taken off before it played plays the month the untouched one does.
>   2. A ZONE'S GROUND IS ITS SECTOR'S: what a zone holds leaves the others'
>      free ground, only its sector builds on it, it builds there first, and
>      giving the ground back hands it to the zone again.
>   3. THE CITY IS PAID THE ZONE'S PRICE AND ASSESSES AT IT: a building in a
>      zone pays the city its share of the going price for its plot, booked
>      in the month's land sales; the sector's roll reads its zoned ground at
>      that price, the planner's standing cost too; un-zoned, the going price.
>   4. ONE DIAL, TWO DOORS: a zone's tax row moves the sector's own offset
>      exactly as Policy > Taxes does, and reads the sector's rate.
>   5. A ZONE BIGGER THAN THE FREE GROUND holds what is free, two such zones
>      each the same share of their wants, and they fill as land is bought.
>   6. THE FOREST LAW: protected forest is never cut or built on, working
>      forest never built on; the cut stops at the protected wood.
>   7. BUILDING ON OPEN FOREST CLEARS IT, cut-over ground first, then
>      standing forest counted as taken out; cleared ground never regrows,
>      a demolition leaves it cleared, and the world's totals hold.
>   8. ZONED FIELDS DO NOT YIELD TO HOMES: while somebody sleeps outside the
>      fields break no ground but in their zone.
>   9. SAVED AND RELOADED, THE SAME CITY: zones, used ground, the law and the
>      forest cleared, and the month after.
> 
> Every fixture causes its condition.

**Uses:** [Sectors](Sectors.md) (100), [LandManager](LandManager.md) (39), [Zoning](Zoning.md) (21), [Game](Game.md) (17), [LongPlaytest](LongPlaytest.md) (8), [Resource](Resource.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [GameFiles](GameFiles.md) (4), [EconomyManager](EconomyManager.md) (2), [Sector](Sector.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Agriculture](Agriculture.md) (2), [Founding](Founding.md) (1), [Formats](Formats.md) (1), [World](World.md) (1), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1)

## Sections

| line | section |
|---:|---|
| 145 | 1. NOTHING SET, NOTHING MOVES |
| 196 | 2. A ZONE'S GROUND IS ITS SECTOR'S |
| 247 | 3. THE PRICE AND THE ROLL |
| 300 | 4. ONE DIAL, TWO DOORS |
| 330 | 5. BIGGER THAN THE FREE GROUND |
| 355 | 6. THE FOREST LAW |
| 385 | 7. CLEARING |
| 438 | 8. ZONED FIELDS DO NOT YIELD |
| 495 | 9. SAVED AND RELOADED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `ZoningCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 80 | `ZoningCheck.FARM_HANDS` | `100` | Spare unskilled hands section 8 hands the city past what its posts are short of, so the fields' planner gets past the staff to the zone (0.7.113): more than any farm's posts. |
| 83 | `ZoningCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders: homes, shops, a diner, builders, a bakery buying crops, power, water and roads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |
| 57 | `static PrintStream out` |  |
| 58 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 483 | **type** `public class ZoningCheck` | Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury, the roll and the world's totals. |
| 60 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 68 | 1 | `static boolean near(double a, double b)` |  |
| 71 | 1 | `static boolean same(double a, double b)` | ...and two ground or wood figures the same to a part in a billion of the larger: one rounding, not a square foot. |
| 73 | 5 | `static void quietly(Runnable r)` |  |
| 94 | 17 | `static Game city(Path root, String name, int months, java.util.function.Consumer<Game> before)` | A played city: the orders put up finished out of a Wealthy treasury on 200 million square feet more ground, the fields held out of the investment engine (section 8 asks them itself), then `months` played - `before` ru... |
| 113 | 5 | `static LandManager office(double km2)` | A land office on its own whose ground is drawn round the default world's founding site to this many square kilometres: forest and bare ground both. |
| 119 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. NOTHING SET, NOTHING MOVES (lines 145-195)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 48 | `static void nothingSet(Path root)` |  |

### 2. A ZONE'S GROUND IS ITS SECTOR'S (lines 196-246)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 48 | `static void theZonesGround(Game g)` |  |

### 3. THE PRICE AND THE ROLL (lines 247-299)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 50 | `static void thePriceAndTheRoll(Game g)` |  |

### 4. ONE DIAL, TWO DOORS (lines 300-329)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 27 | `static void oneDialTwoDoors(Path root)` |  |

### 5. BIGGER THAN THE FREE GROUND (lines 330-354)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 22 | `static void biggerThanTheFreeGround()` |  |

### 6. THE FOREST LAW (lines 355-384)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 27 | `static void theForestLaw()` |  |

### 7. CLEARING (lines 385-437)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 50 | `static void clearing()` |  |

### 8. ZONED FIELDS DO NOT YIELD (lines 438-494)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 54 | `static void theFields(Game g)` |  |

### 9. SAVED AND RELOADED (lines 495-536)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 39 | `static void savedAndReloaded(Path root, Game g)` |  |

