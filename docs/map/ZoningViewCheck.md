# ZoningViewCheck.java - 730 lines · 26 methods · 3 constants · harnesses

`ham/citybuildersim/ZoningViewCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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
>  10. ONE PALETTE FOR ZONES (ZT1): Policy > Zoning paints a zone in its
>      sector's colour, the map's own (LandMap.zoneColour), for every sector
>      of the registry; a zone's colour does not change when an earlier zone
>      comes off; the map's legend and the screen give a zone the same
>      colour; and no sector's colour is within ZONE_GROUND_APART of the
>      ground the screen draws its zones against.
> 
> Every fixture causes its condition.

**Uses:** [ZoningView](ZoningView.md) (126), [Sectors](Sectors.md) (106), [Zoning](Zoning.md) (35), [LandManager](LandManager.md) (33), [BuildCard](BuildCard.md) (22), [Game](Game.md) (19), [LandMap](LandMap.md) (14), [CityNeeds](CityNeeds.md) (9), [DecisionLog](DecisionLog.md) (7), [UserInterface](UserInterface.md) (6), [LongPlaytest](LongPlaytest.md) (5), [PolicyPreview](PolicyPreview.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [TempDirs](TempDirs.md) (1), [EconomyManager](EconomyManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Resource](Resource.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 152 | 1. NOTHING SET |
| 189 | 2. THE HUB'S FIGURES |
| 217 | 3. THE GROUND BAR |
| 271 | 4. THE ROWS |
| 316 | 5. A SECTOR'S PAGE |
| 369 | 6. THE FOREST LAW |
| 401 | 7. THE DECISION KIND |
| 428 | 8. THE WORDS ELSEWHERE |
| 482 | 9. WHAT IS BITING |
| 614 | 10. ONE PALETTE FOR ZONES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ZoningViewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 99 | `ZoningViewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | ZoningCheck's orders: a small city with homes, shops, diners and works. |
| 105 | `ZoningViewCheck.CITY_KM2` | `80` | The ground the city is drawn to round the default world's founding site: ZoningCheck's land office's 80 km², forest and bare ground both. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `static int fails` |  |
| 74 | `static PrintStream out` |  |
| 75 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 660 | **type** `public class ZoningViewCheck` | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the build card's land gate, an order's quote - held to the model's own reads. |
| 77 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 85 | 1 | `static boolean near(double a, double b)` |  |
| 88 | 1 | `static boolean same(double a, double b)` | ...and two ground figures the same to a part in a billion of the larger, or of the dry ground they are cut from: one rounding, not a square foot. |
| 90 | 1 | `static boolean same(double a, double b, double whole)` |  |
| 92 | 5 | `static void quietly(Runnable r)` |  |
| 108 | 16 | `static Game city(Path root, String name, int months)` | A played city, ZoningCheck's: the orders put up finished out of a Wealthy treasury, its ground drawn to CITY_KM2, then `months` played. |
| 125 | 26 | `public static void main(String[] args) throws Exception` |  |

### 1. NOTHING SET (lines 152-188)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 20 | `static void nothingSet(Game g)` |  |
| 176 | 12 | `static void zoned(Game g)` | Two zones - Mining's a share at 40% of the going price, Restaurants' in km² for nothing, a Diner built in it - and a law. |

### 2. THE HUB'S FIGURES (lines 189-216)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 25 | `static void theHubsFigures(Game g)` |  |

### 3. THE GROUND BAR (lines 217-270)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 51 | `static void theGroundBar(Game g)` |  |

### 4. THE ROWS (lines 271-315)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 42 | `static void theRows(Game g)` |  |

### 5. A SECTOR'S PAGE (lines 316-368)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 6 | `static boolean samePage(ZoningView.Page a, ZoningView.Page b)` |  |
| 325 | 43 | `static void aSectorsPage(Game g)` |  |

### 6. THE FOREST LAW (lines 369-400)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 29 | `static void theForestLaw(Game g)` |  |

### 7. THE DECISION KIND (lines 401-427)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 6 | `static boolean records(Game g, String kind, Runnable hand)` | The hand, then exactly one new entry of this kind. |
| 411 | 16 | `static void theDecisionKind(Game g)` |  |

### 8. THE WORDS ELSEWHERE (lines 428-481)

| line | len | member | says |
|---:|---:|---|---|
| 430 | 51 | `static void theWordsElsewhere(Game g)` |  |

### 9. WHAT IS BITING (lines 482-613)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 24 | `static List<String[]> hubFlags(Game g)` | The Policy hub's WHAT IS BITING flags as the hub lists them - area, heading, body, page, door - from its own policyFlags() on an interface bound to the city (no window: BuildMenuCheck's binding), by reflection, becaus... |
| 515 | 5 | `static List<String[]> zoningFlags(Game g)` | ...the ones in Zoning's area. |
| 521 | 92 | `static void whatIsBiting(Game g)` |  |

### 10. ONE PALETTE FOR ZONES (lines 614-730)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 13 | `static Object zoningScreen(Game g)` | Policy > Zoning's screen on the interface bound to the city (no window), by reflection, as hubFlags reads the hub's. |
| 632 | 9 | `static String screenColour(Object zoningScreen, String sector)` | The colour the screen paints a sector's zone in ("#rrggbb"): the screen's own zoneColour. |
| 643 | 9 | `static String screenConstant(Object zoningScreen, String name)` | One of the screen's static colour constants ("#rrggbb"): the ground it draws its zones against. |
| 654 | 1 | `static int argb(String hex)` | "#rrggbb" as the opaque ARGB int the map keeps its colours in. |
| 656 | 74 | `static void onePalette(Game g)` |  |

