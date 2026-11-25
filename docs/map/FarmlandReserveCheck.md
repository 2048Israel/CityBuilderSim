# FarmlandReserveCheck.java - 366 lines · 15 methods · 6 constants · harnesses

`ham/citybuildersim/FarmlandReserveCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The farmland reserve, the model (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that also lets farms crops as well be cheaper"): the
> fields' zone at the sellers' farmland price, outside the crowding premium, holding ground for every farm - Agriculture's fields and
> the city's Wind and Solar Farms - against the ground as it was priced without it.
> 
> WHY. REV-PW3b (item 4) found a Wind Farm's ground 87% of its capital and a Solar Farm's 96% at the going price on the default run's
> grown city, so it built none, and the fields have run on the same clock. Zoning's WHY has the real reserves the policy is (British
> Columbia's Agricultural Land Reserve, Oregon's Exclusive Farm Use zones, Ontario's Greenbelt); this harness causes both paths.
> 
> What this has to prove:
>   1. THE FARMLAND PRICE is the sellers' own figure: Canada's published farmland a hectare (LandFactors' table) carried to 2025's
>      dollars over the base, a figure inside real farmland's (US pasture to cropland, LandMarket's THE CROWDING PREMIUM), times the
>      host's land factor; and it stands outside the crowding premium - a crowded city's going price rises, the farmland price does not.
>   2. WITHOUT THE POLICY, TODAY'S PRICES: no zone, a field's ground at the going price and a Wind Farm on its own key; the fields'
>      zone at a share sells at that share, holds nothing for a Wind Farm, and the planner's capital is the ground as before.
>   3. WITH IT, A FARM'S GROUND COSTS THE FARMLAND PRICE: the reserve's share is the farmland price over the going price, its flat
>      ground's price the farmland price; the dial is kept and comes back when the reserve is lifted; only the fields' zone may be the
>      reserve, and the reserve never sells above the going price.
>   4. THE MONEY: a field bought in the reserve through the businesses' door pays the farmland price, the city has exactly that, and
>      the month's money audit closes: no new payer, no new payee.
>   5. THE CITY'S OWN FARMS: a Wind Farm the city builds takes the reserve's ground, which it uses as the fields would, and gives it
>      back when it is demolished; with no reserve it leaves the fields' zone alone.
>   6. THE PLANNER BUILDS FARMS WHERE THE DEFAULT BUILT NONE: PowerPlanCheck 6's dear town orders wind on its ground; crowded, a Wind
>      Farm's ground is most of its capital and it orders none - nor with the fields' zone at a share; with the reserve the farm's
>      ground is at the farmland price, a sliver of its capital, and it orders wind again. A Solar Farm's capital the same.
>   7. THE SAVE: a city with the reserve saved and loaded is the same city, a month on too; an older save's zone, which has no
>      reserve key, reads as a zone at its share.

**Uses:** [LandManager](LandManager.md) (20), [Zoning](Zoning.md) (19), [Game](Game.md) (16), [BuildAdvice](BuildAdvice.md) (11), [PowerPlanCheck](PowerPlanCheck.md) (10), [LandMarket](LandMarket.md) (9), [Sectors](Sectors.md) (6), [PriceLevel](PriceLevel.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [GameFiles](GameFiles.md) (4), [Formats](Formats.md) (4), [LandFactors](LandFactors.md) (3), [MoneyAudit](MoneyAudit.md) (3), [LongPlaytest](LongPlaytest.md) (2), [Founding](Founding.md) (1), [EssentialWorksCheck](EssentialWorksCheck.md) (1), [TempDirs](TempDirs.md) (1), [AutoBuilder](AutoBuilder.md) (1)

## Sections

| line | section |
|---:|---|
| 107 | 1. THE FARMLAND PRICE |
| 138 | 2. WITHOUT IT |
| 167 | 3. WITH IT |
| 197 | 4. THE MONEY |
| 233 | 5. THE CITY'S OWN FARMS |
| 267 | 6. THE PLANNER |
| 322 | 7. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `FarmlandReserveCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 59 | `FarmlandReserveCheck.FIELDS` | `Zoning.RESERVE_SECTOR` |  |
| 62 | `FarmlandReserveCheck.PASTURE_USD` | `0.044, CROPLAND_USD = 0.134` | Real ground a square foot, US$ (LandMarket's THE CROWDING PREMIUM): US pasture, and cropland - what a farm's ground costs. |
| 68 | `FarmlandReserveCheck.RESERVE_KM2` | `2` | The reserve the fixtures zone: square kilometres of the fields' zone, enough for a Solar Farm's ground and more. |
| 71 | `FarmlandReserveCheck.DIAL` | `0.5` | The dial the fields' zone is set at before it is the reserve: a share, to tell the two prices apart. |
| 270 | `FarmlandReserveCheck.CROWDED_FREE_KM2` | `RESERVE_KM2 + 1` | The free ground the crowded town keeps past what it has built, square kilometres: room for the reserve and a little for anyone. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static PrintStream out` |  |
| 42 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 328 | **type** `public final class FarmlandReserveCheck` | The farmland reserve, the model (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that also lets farms crops as well be cheaper"): the fields' zone at the sellers' farmland price, outside the crowding premium, holding gro... |
| 44 | 4 | `static void check(String label, boolean ok)` |  |
| 49 | 3 | `static boolean close(double a, double b, double tol)` |  |
| 53 | 5 | `static void quietly(Runnable r)` |  |
| 65 | 1 | `static double baseUsdToday()` | The base's dollars a square foot today (LandMarket.BASE_PRICE_PER_SQ_FT reads .0007 in thousands, at the founding's level). |
| 78 | 13 | `static Game city(Path root, String name)` | ZoningCheck.city()'s fixture with no months played: a founded scratch city, the defaults put back, the rate pinned, cash and 200 million square feet more ground, the fields held out of the investment engine (they are ... |
| 92 | 14 | `public static void main(String[] args) throws Exception` |  |

### 1. THE FARMLAND PRICE (lines 107-137)

| line | len | member | says |
|---:|---:|---|---|
| 109 | 28 | `static void thePrice(Path root)` |  |

### 2. WITHOUT IT (lines 138-166)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 26 | `static void withoutIt(Path root)` |  |

### 3. WITH IT (lines 167-196)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 27 | `static void withIt()` |  |

### 4. THE MONEY (lines 197-232)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 33 | `static void theMoney(Game g)` |  |

### 5. THE CITY'S OWN FARMS (lines 233-266)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 31 | `static void theCitysFarms(Game g)` |  |

### 6. THE PLANNER (lines 267-321)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 41 | `static void thePlanner(Path root)` |  |
| 315 | 4 | `static double share(Game g, BuildingsTemplate t)` | One `t`'s ground as a share of its capital at the planner's (BuildAdvice.capitalOf() on the ground free for anyone). |
| 320 | 1 | `static String named(BuildAdvice.Suggestion s)` |  |

### 7. THE SAVE (lines 322-366)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 42 | `static void theSave(Path root, Game g)` |  |

