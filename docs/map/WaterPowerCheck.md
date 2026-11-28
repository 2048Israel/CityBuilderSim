# WaterPowerCheck.java - 435 lines · 18 methods · 2 constants · harnesses

`ham/citybuildersim/WaterPowerCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's
> power read from the dispatch (PW1-L) and a recipe's own power weighed at the prices the strike pays it (PW1-G), through the game's
> own month. PublicLicenceCheck holds the patented desalination plant that earns its licence; this holds the price, the bills and the
> save on both sides of each edge.
> 
> WHY: the water works drew their power billed to nobody and the water sold at a constant tariff, so once PW1 priced power at the
> market a Desalination Plant's water no longer covered its power (REV-PW1c); a licence's power was the flat rule's output over demand,
> which the dispatch no longer serves; and a works' surplus power was weighed at its class's price, which the strike never pays it.
> 
> What this has to prove:
>   1. A TOWN IN SURPLUS PRICES ITS WATER AT ITS TARIFF: the founding wells serve the demand with a desalination plant standing, so
>      the sea delivers nothing and the price is the tariff exactly, which pays the land's works' power; the plant's gain over its
>      running cost is negative, and the water's books still pay the power's for all the works' draw.
>   2. A TOWN SHORT OF WATER PRICES IT AT ITS SEA WORKS' POWER: its demand past what every works makes at full staffing, the sea
>      works deliver their whole output, past SEA_MARGIN_SHARE of it, and the water's price is the tariff and their draw at the struck
>      commerce price over that output, recomputed from the stacks, the book and UtilitiesHandler.waterOutput(); the sea's water pays
>      the sea works' power. "At the struck commerce price" is on the kWh served throughout (PW1-R): the price a kW of the year's
>      mean draw times the season the book's month was dispatched at (UtilitiesHandler.Book.season), as every class is billed.
>   3. EVERY BILL ONCE: the water's books pay the power's the month's works' draw at the struck commerce price; the electricity's
>      income gains it and the water's loses it, the services' net and the treasury's line are unmoved by it, the audit's utility
>      revenue is the sectors' bills, and the money audit closes.
>   4. THE SAVE: a city saved and loaded prices its water, holds its works' record and its payment as it did; a save without them
>      (the build before) loads with the record its load rebuilds from the same stock and nothing paid, and prices by the same rule. The same save
>      with its book struck at July's season and at April's (PW1-R, REV-PW1-R): the price a planner reads is the same to the bit,
>      the bills' price and every sector's water bill move by the seasons' ratio.
>   5. A LICENCE'S POWER IS THE DISPATCH'S ENERGY SERVED (PW1-L): the dispatch run again on the month's rows serves the month's
>      demand; in the copy in surplus in every half-block a plant moved adds nothing; in the copy short in every half-block by more
>      than the move it adds the move's kWh over the kWh asked of the demand, which is not the flat rule's move.
>   6. A RECIPE'S OWN POWER IS WEIGHED AT WHAT THE STRIKE PAYS IT (PW1-G): in the short copy, whose works price is not its industry
>      price, a Pulp Mill's surplus at the works price, what Wood Products' statement is paid for it, and not at the industry price;
>      an Oxygen Steel Shop's own power netted at its class's.
>   7. THE SEA ON A SLIVER: the two copies, each its fresh works retired for a desalination plant and its demand a little past the
>      wells, so the sea delivers under SEA_MARGIN_SHARE of its output and the water served is within that share: the price is the
>      tariff and the sea works' draw spread over the water served, bounded below their draw over the sliver; the water served at it
>      recovers their power once and the bills no more; and the copy whose homes ask past its plant, its commerce price the higher,
>      prices the dearer water.
>   8. THE SEA ON A SLIVER OF A CITY THAT SERVES PAST THAT SHARE: the cheap copy's fresh works stood again and its demand a little
>      past the land's water: the price is the sea works' draw over that share of their output, what a plant delivering just the
>      share prices its water at, still below their draw over the sliver.
> 
> Every fixture is a scratch city (HistoricalLicenceCheck.town(), PowerPriceCheck.town()) and CAUSES its condition through the
> model's inputs: Pulp Mills for the water's demand, the homes' draw for the power's price. Every expected figure is a getter or a
> constant of the model.

**Uses:** [Game](Game.md) (28), [UtilitiesHandler](UtilitiesHandler.md) (24), [PublicLicenceCheck](PublicLicenceCheck.md) (13), [Recipes](Recipes.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (8), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (6), [BuildingsStacks](BuildingsStacks.md) (5), [PowerDispatch](PowerDispatch.md) (5), [PowerPriceCheck](PowerPriceCheck.md) (4), [Recipe](Recipe.md) (3), [Sector](Sector.md) (3), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (3), [MoneyAudit](MoneyAudit.md) (2), [BuildingType](BuildingType.md) (1), [ResearchCheck](ResearchCheck.md) (1), [ResearchCatalog](ResearchCatalog.md) (1), [PriceLevel](PriceLevel.md) (1), [Sectors](Sectors.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 169 | · 1 |
| 196 | · 2 |
| 220 | · 7 |
| 254 | · 8 |
| 275 | · 5 |
| 307 | · 6 |
| 336 | · 3 |
| 360 | · 4 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `WaterPowerCheck.MOVE_KW` | `1000` | kW a plant is moved by in section 5: well inside every short half-block's shortfall in the dear copy. |
| 58 | `WaterPowerCheck.SLOT` | `10` | The save slot the scratch cities are saved in: past the player's nine. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 386 | **type** `public class WaterPowerCheck` | The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's power read from the dispatch (PW1-L) and a recipe's own power weighed at the prices the... |
| 60 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 66 | 3 | `static boolean near(double a, double b, double rel)` |  |
| 70 | 1 | `static String f(double v)` |  |
| 73 | 4 | **type** `record Works(double freshKw, double seaKw, double served, double withoutSea, double wellsAlone, double seaO...` | What the water works stand and deliver, worked from the stacks, the templates and waterOutput(): independent of the handler's record. |
| 74 | 1 | `double landDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 75 | 1 | `double seaDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 79 | 3 | `static double kwPerUnit(double seaKw, double land, double sea, double seaOutput)` | The sea works' draw a unit, the rule stated from a record's fields: over the sea's water, or the water served under SEA_MARGIN_SHARE of their output. |
| 83 | 16 | `static Works works(Game g)` |  |
| 101 | 4 | `static int mills(Game g, boolean pastEveryWorks)` | Pulp Mills past what the city's water works make at full staffing, or past the founding wells alone: how many stood. |
| 107 | 9 | `static int millsPast(Game g, double desal, double fresh)` | Pulp Mills past what the given sea and fresh nameplates make with the wells at full staffing: how many more stood. |
| 117 | 51 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 169-195)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 24 | `static void surplus(Game d, BuildingsTemplate desal, BuildingsStacks sea)` |  |

### 2 (lines 196-219)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 21 | `static void short_(Game d, int n)` |  |

### 7 (lines 220-253)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 31 | `static void dearPower(Game lo, Game hi, int nlo, int nhi)` |  |

### 8 (lines 254-274)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 18 | `static void wideSliver(Game g, int n)` |  |

### 5 (lines 275-306)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 29 | `static void licence(Game lo, Game hi)` |  |

### 6 (lines 307-335)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 26 | `static void recipes(Game d, int n)` |  |

### 3 (lines 336-359)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 21 | `static void money(Game d)` |  |

### 4 (lines 360-435)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 73 | `static void save(Game d) throws Exception` |  |

