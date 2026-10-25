# ZoningViewCheck.java - 606 lines · 21 methods · 3 constants · harnesses

`ham/citybuildersim/ZoningViewCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words
> zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the
> build card's land gate, an order's quote - held to the model's own reads.
> 
> WHY. The screens Z2 draws (ui/ZoningScreen) paint ZoningView's figures
> and nothing else, so a figure the view worked out wrong is a figure the
> player decides by. A bar whose parts did not add up to the dry ground, a
> held ground that was not what the zone keeps from everyone else, a staged
> zone whose "after" was not what the city reads once it is applied, or a
> build card that refused a zoned sector's building the ground its own zone
> holds would each be a screen that says one thing while the model does
> another. Nothing in the default playtest sets a zone, so this is the
> only place the screens' figures are played.
> 
> What this has to prove:
>   1. NOTHING SET: the four figures read nothing zoned, nothing held,
>      nothing given up and the forest kept "none"; the bar is the built
>      ground and the bare ground and open forest free for anyone, adding
>      up to the dry ground; no zone has a row.
>   2. THE HUB'S FIGURES ARE THE MODEL'S: zoned is the zones' sizes,
>      held is what they hold, the price given up and the relief are
>      PolicyPreview's, the forest kept is the protected and working forest.
>   3. THE GROUND BAR adds up to the dry ground: each zone's used and held
>      ground its own, the bare ground and open forest free for anyone
>      LandManager.freeForAnyone(), the built ground the allocated less the
>      zones' used, the working and protected forest the law's - and built
>      past what the law keeps, still the dry ground, no part below nothing.
>   4. A ZONE'S ROW is its zone: size, share, used, held, its price a
>      square foot, its three rates and offsets (one dial, two doors: a
>      move through the zone's door reads on its row and on Policy › Taxes),
>      what each tax raised last month; a zone bigger than the free ground
>      reads short.
>   5. A SECTOR'S PAGE, STAGED, MOVES NOTHING, and applied reads as staged:
>      what the zone holds, the ground free for anyone, what it carries
>      and a plot at its price - for a new zone, a re-sized one and one
>      taken off.
>   6. THE FOREST LAW'S PAGE, the same: the three kinds, the wood kept
>      standing for ever and the wood that may be cut.
>   7. ZONING HAS A DECISION KIND OF ITS OWN: a zone set, re-priced or taken
>      off and the law each record one ZONING line, nothing when nothing
>      moves; a zone's tax row records TAX, being the sector's offset.
>   8. THE WORDS ELSEWHERE: listed, NEEDS YOU's GROUND row names what the
>      zones hold (not the homes' own zone); the build card's land gate and
>      an order's quote count a zoned sector's zone, and an unzoned
>      building's gate says what is zoned for others.
>   9. WHAT IS BITING (FW8): a zone short of what it wants - one, or two
>      sharing the free ground alike - raises the Policy hub's zoning flag,
>      its figures the zones' rows and its words saying what it holds of
>      what it wants, and none is free for anyone else; the hub's own list
>      carries it, its door on Zoning; a zone the free ground covers, small
>      or large, and no zone at all raise none; with no free ground the
>      zone holds none of its want.
> 
> Every fixture causes its condition.

**Uses:** [ZoningView](ZoningView.md) (126), [Sectors](Sectors.md) (84), [LandManager](LandManager.md) (33), [Zoning](Zoning.md) (31), [BuildCard](BuildCard.md) (22), [Game](Game.md) (17), [CityNeeds](CityNeeds.md) (9), [DecisionLog](DecisionLog.md) (7), [LongPlaytest](LongPlaytest.md) (5), [PolicyPreview](PolicyPreview.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (3), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [TempDirs](TempDirs.md) (1), [EconomyManager](EconomyManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Resource](Resource.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 145 | 1. NOTHING SET |
| 182 | 2. THE HUB'S FIGURES |
| 210 | 3. THE GROUND BAR |
| 264 | 4. THE ROWS |
| 309 | 5. A SECTOR'S PAGE |
| 362 | 6. THE FOREST LAW |
| 394 | 7. THE DECISION KIND |
| 421 | 8. THE WORDS ELSEWHERE |
| 475 | 9. WHAT IS BITING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `ZoningViewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 93 | `ZoningViewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | ZoningCheck's orders: a small city with homes, shops, diners and works. |
| 99 | `ZoningViewCheck.CITY_KM2` | `80` | The ground the city is drawn to round the default world's founding site: ZoningCheck's land office's 80 km², forest and bare ground both. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |
| 68 | `static PrintStream out` |  |
| 69 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 542 | **type** `public class ZoningViewCheck` | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the build card's land gate, an order's quote - held to the model's own reads. |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 79 | 1 | `static boolean near(double a, double b)` |  |
| 82 | 1 | `static boolean same(double a, double b)` | ...and two ground figures the same to a part in a billion of the larger, or of the dry ground they are cut from: one rounding, not a square foot. |
| 84 | 1 | `static boolean same(double a, double b, double whole)` |  |
| 86 | 5 | `static void quietly(Runnable r)` |  |
| 102 | 16 | `static Game city(Path root, String name, int months)` | A played city, ZoningCheck's: the orders put up finished out of a Wealthy treasury, its ground drawn to CITY_KM2, then `months` played. |
| 119 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. NOTHING SET (lines 145-181)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 20 | `static void nothingSet(Game g)` |  |
| 169 | 12 | `static void zoned(Game g)` | Two zones - Mining's a share at 40% of the going price, Restaurants' in km² for nothing, a Diner built in it - and a law. |

### 2. THE HUB'S FIGURES (lines 182-209)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 25 | `static void theHubsFigures(Game g)` |  |

### 3. THE GROUND BAR (lines 210-263)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 51 | `static void theGroundBar(Game g)` |  |

### 4. THE ROWS (lines 264-308)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 42 | `static void theRows(Game g)` |  |

### 5. A SECTOR'S PAGE (lines 309-361)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 6 | `static boolean samePage(ZoningView.Page a, ZoningView.Page b)` |  |
| 318 | 43 | `static void aSectorsPage(Game g)` |  |

### 6. THE FOREST LAW (lines 362-393)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 29 | `static void theForestLaw(Game g)` |  |

### 7. THE DECISION KIND (lines 394-420)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 6 | `static boolean records(Game g, String kind, Runnable hand)` | The hand, then exactly one new entry of this kind. |
| 404 | 16 | `static void theDecisionKind(Game g)` |  |

### 8. THE WORDS ELSEWHERE (lines 421-474)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 51 | `static void theWordsElsewhere(Game g)` |  |

### 9. WHAT IS BITING (lines 475-606)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 24 | `static List<String[]> hubFlags(Game g)` | The Policy hub's WHAT IS BITING flags as the hub lists them - area, heading, body, page, door - from its own policyFlags() on an interface bound to the city (no window: BuildMenuCheck's binding), by reflection, becaus... |
| 508 | 5 | `static List<String[]> zoningFlags(Game g)` | ...the ones in Zoning's area. |
| 514 | 92 | `static void whatIsBiting(Game g)` |  |

