# FB13Check.java - 600 lines · 13 methods · 2 constants · harnesses

`ham/citybuildersim/FB13Check.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
>   6. THE GOVERNMENT SCREEN'S COUPONS. The row's note says D$ and, held now, the moneys the foreign paper is written in, from the debt's own
>      books (US dollars, euros, yen), and says "in dollars" for none of them; with no foreign paper it says D$ alone; the screen's line places that note.
>   7. THE BUILD CARD'S LICENCE LINE. It is the planners' licence, template by template (LicenceFees.rentOnNew(): the patents forNew() and
>      forNewWork() are made of - the building's, its recipe's, a piece's - with the same fee to the bit): UNDER wherever one is due, saying the date its
>      patent ends or how many there are and when the first and the last end; never none where the planners charge something. With none due, the
>      building's own discovery says it: found here (none, ever), found here since the world had it, not patented, ended. A house and every card of a
>      city founded the default way have no line.
>   8. THE SCREENS CALL THE MODEL. A screen cannot be drawn without the toolkit, so the line of each that hands the model's link, note or licence to the
>      words (LandScreen, MapView, UserInterface, Inbox, BuildScreen, GovernmentScreen) is held as text: putting one back fails here.
> 
> Every premise is asserted before what rests on it.

**Uses:** [LandmassView](LandmassView.md) (77), [BuildAdvice](BuildAdvice.md) (75), [ResearchView](ResearchView.md) (44), [LandmassCheck](LandmassCheck.md) (27), [LicenceFees](LicenceFees.md) (24), [BridgeCheck](BridgeCheck.md) (23), [World](World.md) (13), [Game](Game.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (10), [DebtMoneysCheck](DebtMoneysCheck.md) (9), [ForeignAccounts](ForeignAccounts.md) (9), [ResearchViewCheck](ResearchViewCheck.md) (9), [LandMap](LandMap.md) (8), [ResearchCheck](ResearchCheck.md) (8), [Landmasses](Landmasses.md) (6), [ResearchCatalog](ResearchCatalog.md) (6), [CityCalendar](CityCalendar.md) (6), [ForeignCouponView](ForeignCouponView.md) (5), [EarthCheck](EarthCheck.md) (3), [BridgeView](BridgeView.md) (3), [Good](Good.md) (3), [LandManager](LandManager.md) (2), [EarthData](EarthData.md) (2), [EarthTerrain](EarthTerrain.md) (1), [Notice](Notice.md) (1), [BridgeWorks](BridgeWorks.md) (1), [Bridges](Bridges.md) (1), [DebtManager](DebtManager.md) (1), [WorldMoneys](WorldMoneys.md) (1), [PolicyDates](PolicyDates.md) (1)

## Sections

| line | section |
|---:|---|
| 107 | 1. THE ISLAND'S WORDS, A CROSSING ALLOWED |
| 190 | 2. A BRIDGE UNDER WAY |
| 221 | 3. DEEP SEA AND NO LINK |
| 279 | 5. A SCREEN'S READ MOVES NOTHING |
| 356 | 6. THE GOVERNMENT SCREEN'S COUPONS ABROAD |
| 406 | 7. THE BUILD CARD'S LICENCE LINE |
| 555 | 8. THE SCREENS CALL THE MODEL'S READS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `FB13Check.out` | `System.out` |  |
| 105 | `FB13Check.OLD_NEEDS` | `"no: needs a port here and one at home"` | "no: needs a port here and one at home", the words as they were before a link was given (no terminal stands anywhere in these fixtures). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 550 | **type** `public class FB13Check` | FB13's three texts, each asked of the model that says it: the island's words (AL1-W), the Government screen's coupons abroad (CD9-G) and the build card's licence line (R5-L). |
| 56 | 4 | `static void check(String label, boolean ok)` |  |
| 61 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 66 | 1 | `static void quietly(Runnable r)` |  |
| 69 | 4 | `static LandmassView.Line oldRow(LandManager land, int island)` | The island's row as the one-argument rows have it, with no link given. |
| 74 | 29 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ISLAND'S WORDS, A CROSSING ALLOWED (lines 107-189)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 78 | `static void islandWithACrossing(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 2. A BRIDGE UNDER WAY (lines 190-220)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 26 | `static void islandUnderWay(BridgeCheck.Island isle)` |  |

### 3. DEEP SEA AND NO LINK (lines 221-278)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 53 | `static void islandOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry)` |  |

### 5. A SCREEN'S READ MOVES NOTHING (lines 279-355)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 72 | `static void screensMoveNothing(LandmassCheck.Fixtures f, BridgeCheck.Island isle)` |  |

### 6. THE GOVERNMENT SCREEN'S COUPONS ABROAD (lines 356-405)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 45 | `static void couponsAbroad()` |  |

### 7. THE BUILD CARD'S LICENCE LINE (lines 406-554)

| line | len | member | says |
|---:|---:|---|---|
| 410 | 144 | `static void licenceLine() throws Exception` |  |

### 8. THE SCREENS CALL THE MODEL'S READS (lines 555-600)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 19 | `static boolean sourceHas(String file, String...pieces)` | Whether a source file holds every one of these pieces of text (a screen cannot be drawn without the toolkit, so the line that places the model's words is read as text). |
| 580 | 20 | `static void screensCallTheModel()` |  |

