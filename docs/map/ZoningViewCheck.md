# ZoningViewCheck.java - 467 lines · 18 methods · 3 constants · harnesses

`ham/citybuildersim/ZoningViewCheck.java` - generated 2026-10-17 by CodeMap; line numbers are as of that run.

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
> 
> Every fixture causes its condition.

**Uses:** [ZoningView](ZoningView.md) (105), [Sectors](Sectors.md) (68), [Zoning](Zoning.md) (27), [LandManager](LandManager.md) (25), [BuildCard](BuildCard.md) (22), [Game](Game.md) (14), [CityNeeds](CityNeeds.md) (9), [DecisionLog](DecisionLog.md) (7), [LongPlaytest](LongPlaytest.md) (5), [PolicyPreview](PolicyPreview.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [EconomyManager](EconomyManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Resource](Resource.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 138 | 1. NOTHING SET |
| 175 | 2. THE HUB'S FIGURES |
| 203 | 3. THE GROUND BAR |
| 257 | 4. THE ROWS |
| 302 | 5. A SECTOR'S PAGE |
| 355 | 6. THE FOREST LAW |
| 387 | 7. THE DECISION KIND |
| 414 | 8. THE WORDS ELSEWHERE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `ZoningViewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 87 | `ZoningViewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | ZoningCheck's orders: a small city with homes, shops, diners and works. |
| 93 | `ZoningViewCheck.CITY_KM2` | `80` | The ground the city is drawn to round the default world's founding site: ZoningCheck's land office's 80 km², forest and bare ground both. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |
| 62 | `static PrintStream out` |  |
| 63 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 409 | **type** `public class ZoningViewCheck` | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the build card's land gate, an order's quote - held to the model's own reads. |
| 65 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 73 | 1 | `static boolean near(double a, double b)` |  |
| 76 | 1 | `static boolean same(double a, double b)` | ...and two ground figures the same to a part in a billion of the larger, or of the dry ground they are cut from: one rounding, not a square foot. |
| 78 | 1 | `static boolean same(double a, double b, double whole)` |  |
| 80 | 5 | `static void quietly(Runnable r)` |  |
| 96 | 16 | `static Game city(Path root, String name, int months)` | A played city, ZoningCheck's: the orders put up finished out of a Wealthy treasury, its ground drawn to CITY_KM2, then `months` played. |
| 113 | 24 | `public static void main(String[] args) throws Exception` |  |

### 1. NOTHING SET (lines 138-174)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 20 | `static void nothingSet(Game g)` |  |
| 162 | 12 | `static void zoned(Game g)` | Two zones - Mining's a share at 40% of the going price, Restaurants' in km² for nothing, a Diner built in it - and a law. |

### 2. THE HUB'S FIGURES (lines 175-202)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 25 | `static void theHubsFigures(Game g)` |  |

### 3. THE GROUND BAR (lines 203-256)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 51 | `static void theGroundBar(Game g)` |  |

### 4. THE ROWS (lines 257-301)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 42 | `static void theRows(Game g)` |  |

### 5. A SECTOR'S PAGE (lines 302-354)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 6 | `static boolean samePage(ZoningView.Page a, ZoningView.Page b)` |  |
| 311 | 43 | `static void aSectorsPage(Game g)` |  |

### 6. THE FOREST LAW (lines 355-386)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 29 | `static void theForestLaw(Game g)` |  |

### 7. THE DECISION KIND (lines 387-413)

| line | len | member | says |
|---:|---:|---|---|
| 390 | 6 | `static boolean records(Game g, String kind, Runnable hand)` | The hand, then exactly one new entry of this kind. |
| 397 | 16 | `static void theDecisionKind(Game g)` |  |

### 8. THE WORDS ELSEWHERE (lines 414-467)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 51 | `static void theWordsElsewhere(Game g)` |  |

