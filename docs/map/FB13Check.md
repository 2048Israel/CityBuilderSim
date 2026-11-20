# FB13Check.java - 458 lines · 11 methods · 2 constants · harnesses

`ham/citybuildersim/FB13Check.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> FB13's three texts, each asked of the model that says it: the island's words (AL1-W), the Government screen's coupons abroad (CD9-G) and the
> build card's licence line (R5-L). Not part of the game.
> 
> WHY. Three screens kept words from before the model changed under them. The Land tab, the map's chip and the island's notice said "needs a
> port here" where AL1 had made a bridge the way (BuildAdvice.linkFor() orders one wherever a crossing is allowed), and the Build tab had no card
> for the link; the Government screen's "...of which paid abroad" row said "in dollars" for coupons CD8 lets the city pay in any of eight moneys,
> its figure being in D$; and the build card drew no licence line for a patented building from abroad, whose fee R2c and R2c-P built after R5
> waited. Each text is the model's: a public getter's figure through a pure words class (LandmassView, ForeignCouponView, ResearchView), the
> screen only putting it where it goes. So each is proved here from the model's side, against its own constants and getters, from the states that
> cause it and the states that do not - and, where a screen cannot be drawn without the toolkit, from the screen's source line that places it.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE ISLAND'S WORDS, A CROSSING ALLOWED. With a shallow channel the survey can cross, the screens' read of the island
>      (BuildAdvice.islandLink()) is the rule's own answer (BuildAdvice.linkFor()): a BRIDGE, the template, the price. The table's
>      cell, its tooltip, the map's chip (a third line), the notice's body and its button (a door to the bridge) name that bridge and
>      the crossing it found, and the port after it; with no link given the words are the old ones.
>   2. ...A BRIDGE UNDER WAY. With the bridge ordered, the island's words say one is being built, and the notice's door is the
>      bridge's, to see it.
>   3. ...DEEP SEA AND NO LINK. Where the survey finds deep water the link is a PORT and every word is the old one (two lines on the
>      chip, the port card's door); the dry island with no shore and no crossing is NONE, the same words, and the Build tab's card says
>      why nothing links it, with no door.
>   4. THE BUILD TAB'S CARD. For a crossing the card is the rule's link in words: the island and its ground, the bridge and its
>      crossing, the price and months of the quote, the door to the bridge's card; for ports, the two terminals and a door to the first.
>   5. A SCREEN'S READ MOVES NOTHING. Reading the island for a screen leaves the bridge card's chosen landmass alone, does not count as
>      one of the rule's surveys, leaves the rule's own memory of a survey that found no crossing as it was (so the rule surveys when it
>      would have), and is kept for the month: a second read with nothing changed reads nothing.
>   6. THE GOVERNMENT SCREEN'S COUPONS. The row's note names D$ and the moneys the foreign paper is written in, from the debt's own
>      books (US dollars, euros, yen), and says "in dollars" for none of them; with no foreign paper it says D$ alone; the screen's line
>      places that note and no other.
>   7. THE BUILD CARD'S LICENCE LINE. In a city founded the 1900 way a building the city found itself says none ever, one the world had
>      first and still patented says a quarter of what it adds goes abroad until the month its patent ends (history's date and
>      LicenceFees.TERM_MONTHS) and about what the licence costs a month (LicenceFees.forNew()), one whose patent has ended or that
>      the city has found since says none now, an unpatented one none; a building known by a year, a house and every card of a city
>      founded the default way have no line.
> 
> Every premise is asserted before what rests on it.

**Uses:** [LandmassView](LandmassView.md) (77), [BuildAdvice](BuildAdvice.md) (52), [ResearchView](ResearchView.md) (32), [LandmassCheck](LandmassCheck.md) (23), [BridgeCheck](BridgeCheck.md) (20), [Game](Game.md) (11), [World](World.md) (10), [DebtMoneysCheck](DebtMoneysCheck.md) (9), [ForeignAccounts](ForeignAccounts.md) (9), [ResearchViewCheck](ResearchViewCheck.md) (9), [LandMap](LandMap.md) (8), [LicenceFees](LicenceFees.md) (8), [Landmasses](Landmasses.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (6), [ForeignCouponView](ForeignCouponView.md) (5), [ResearchCatalog](ResearchCatalog.md) (5), [ResearchCheck](ResearchCheck.md) (5), [CityCalendar](CityCalendar.md) (4), [EarthCheck](EarthCheck.md) (3), [BridgeView](BridgeView.md) (3), [Good](Good.md) (3), [LandManager](LandManager.md) (2), [EarthData](EarthData.md) (2), [EarthTerrain](EarthTerrain.md) (1), [Notice](Notice.md) (1), [BridgeWorks](BridgeWorks.md) (1), [Bridges](Bridges.md) (1), [DebtManager](DebtManager.md) (1), [WorldMoneys](WorldMoneys.md) (1)

## Sections

| line | section |
|---:|---|
| 105 | 1. THE ISLAND'S WORDS, A CROSSING ALLOWED |
| 188 | 2. A BRIDGE UNDER WAY |
| 219 | 3. DEEP SEA AND NO LINK |
| 277 | 5. A SCREEN'S READ MOVES NOTHING |
| 322 | 6. THE GOVERNMENT SCREEN'S COUPONS ABROAD |
| 372 | 7. THE BUILD CARD'S LICENCE LINE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `FB13Check.out` | `System.out` |  |
| 103 | `FB13Check.OLD_NEEDS` | `"no: needs a port here and one at home"` | "no: needs a port here and one at home", the words as they were before a link was given (no terminal stands anywhere in these fixtures). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 409 | **type** `public class FB13Check` | FB13's three texts, each asked of the model that says it: the island's words (AL1-W), the Government screen's coupons abroad (CD9-G) and the build card's licence line (R5-L). |
| 55 | 4 | `static void check(String label, boolean ok)` |  |
| 60 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 65 | 1 | `static void quietly(Runnable r)` |  |
| 68 | 4 | `static LandmassView.Line oldRow(LandManager land, int island)` | The island's row as the one-argument rows have it, with no link given. |
| 73 | 28 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ISLAND'S WORDS, A CROSSING ALLOWED (lines 105-187)

| line | len | member | says |
|---:|---:|---|---|
| 109 | 78 | `static void islandWithACrossing(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 2. A BRIDGE UNDER WAY (lines 188-218)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 26 | `static void islandUnderWay(BridgeCheck.Island isle)` |  |

### 3. DEEP SEA AND NO LINK (lines 219-276)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 53 | `static void islandOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry)` |  |

### 5. A SCREEN'S READ MOVES NOTHING (lines 277-321)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 40 | `static void screensMoveNothing(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 6. THE GOVERNMENT SCREEN'S COUPONS ABROAD (lines 322-371)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 45 | `static void couponsAbroad()` |  |

### 7. THE BUILD CARD'S LICENCE LINE (lines 372-458)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 82 | `static void licenceLine() throws Exception` |  |

