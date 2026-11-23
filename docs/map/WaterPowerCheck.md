# WaterPowerCheck.java - 385 lines · 18 methods · 2 constants · harnesses

`ham/citybuildersim/WaterPowerCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
>      the sea works' power.
>   3. EVERY BILL ONCE: the water's books pay the power's the month's works' draw at the struck commerce price; the electricity's
>      income gains it and the water's loses it, the services' net and the treasury's line are unmoved by it, the audit's utility
>      revenue is the sectors' bills, and the money audit closes.
>   4. THE SAVE: a city saved and loaded prices its water, holds its works' record and its payment as it did; a save without them
>      (the build before) loads with the record its load rebuilds from the same stock and nothing paid, and prices by the same rule.
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

**Uses:** [Game](Game.md) (24), [UtilitiesHandler](UtilitiesHandler.md) (23), [PublicLicenceCheck](PublicLicenceCheck.md) (13), [Recipes](Recipes.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (8), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (6), [BuildingsStacks](BuildingsStacks.md) (5), [PowerPriceCheck](PowerPriceCheck.md) (4), [PowerDispatch](PowerDispatch.md) (3), [Recipe](Recipe.md) (3), [MoneyAudit](MoneyAudit.md) (2), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (2), [BuildingType](BuildingType.md) (1), [ResearchCheck](ResearchCheck.md) (1), [ResearchCatalog](ResearchCatalog.md) (1), [PriceLevel](PriceLevel.md) (1), [Sector](Sector.md) (1), [Sectors](Sectors.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 166 | · 1 |
| 193 | · 2 |
| 212 | · 7 |
| 246 | · 8 |
| 266 | · 5 |
| 297 | · 6 |
| 326 | · 3 |
| 350 | · 4 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `WaterPowerCheck.MOVE_KW` | `1000` | kW a plant is moved by in section 5: well inside every short half-block's shortfall in the dear copy. |
| 55 | `WaterPowerCheck.SLOT` | `10` | The save slot the scratch cities are saved in: past the player's nine. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 339 | **type** `public class WaterPowerCheck` | The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's power read from the dispatch (PW1-L) and a recipe's own power weighed at the prices the... |
| 57 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 63 | 3 | `static boolean near(double a, double b, double rel)` |  |
| 67 | 1 | `static String f(double v)` |  |
| 70 | 4 | **type** `record Works(double freshKw, double seaKw, double served, double withoutSea, double wellsAlone, double seaO...` | What the water works stand and deliver, worked from the stacks, the templates and waterOutput(): independent of the handler's record. |
| 71 | 1 | `double landDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 72 | 1 | `double seaDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 76 | 3 | `static double kwPerUnit(double seaKw, double land, double sea, double seaOutput)` | The sea works' draw a unit, the rule stated from a record's fields: over the sea's water, or the water served under SEA_MARGIN_SHARE of their output. |
| 80 | 16 | `static Works works(Game g)` |  |
| 98 | 4 | `static int mills(Game g, boolean pastEveryWorks)` | Pulp Mills past what the city's water works make at full staffing, or past the founding wells alone: how many stood. |
| 104 | 9 | `static int millsPast(Game g, double desal, double fresh)` | Pulp Mills past what the given sea and fresh nameplates make with the wells at full staffing: how many more stood. |
| 114 | 51 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 166-192)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 24 | `static void surplus(Game d, BuildingsTemplate desal, BuildingsStacks sea)` |  |

### 2 (lines 193-211)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 16 | `static void short_(Game d, int n)` |  |

### 7 (lines 212-245)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 31 | `static void dearPower(Game lo, Game hi, int nlo, int nhi)` |  |

### 8 (lines 246-265)

| line | len | member | says |
|---:|---:|---|---|
| 248 | 17 | `static void wideSliver(Game g, int n)` |  |

### 5 (lines 266-296)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 28 | `static void licence(Game lo, Game hi)` |  |

### 6 (lines 297-325)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 26 | `static void recipes(Game d, int n)` |  |

### 3 (lines 326-349)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 21 | `static void money(Game d)` |  |

### 4 (lines 350-385)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 33 | `static void save(Game d) throws Exception` |  |

