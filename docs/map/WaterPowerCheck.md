# WaterPowerCheck.java - 312 lines · 15 methods · 2 constants · harnesses

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
>      no works delivers and the price is the tariff exactly; the plant's gain over its running cost is negative, and the water's
>      books still pay the power's for all the works' draw.
>   2. A TOWN SHORT OF WATER PRICES IT AT ITS SEA WORKS' POWER: its demand past what every works makes at full staffing, the sea
>      works deliver their whole output, and the water's price is the tariff and their draw at the struck commerce price over that
>      output, recomputed from the stacks, the book and UtilitiesHandler.waterOutput(); the sea's water pays the sea works' power.
>   3. EVERY BILL ONCE: the water's books pay the power's the month's works' draw at the struck commerce price; the electricity's
>      income gains it and the water's loses it, the services' net and the treasury's line are unmoved by it, the audit's utility
>      revenue is the sectors' bills, and the money audit closes.
>   4. THE SAVE: a city saved and loaded prices its water, holds its works' record and its payment as it did; a save without them
>      (the build before) loads with the record its load rebuilds from the same stock and nothing paid, and prices by the same rule.
>   5. THE PRICE OF POWER MOVES THE PRICE OF WATER: two copies of one town whose fresh works are the margin, one with its homes asking
>      past its plant: its struck commerce price is the higher, and so is its water's, each the tariff and the fresh works' draw at
>      its own commerce price over the water they deliver.
>   6. A LICENCE'S POWER IS THE DISPATCH'S ENERGY SERVED (PW1-L): the dispatch run again on the month's rows serves the month's
>      demand; in the copy in surplus in every half-block a plant moved adds nothing; in the copy short in every half-block by more
>      than the move it adds the move's kWh over the kWh asked of the demand, which is not the flat rule's move.
>   7. A RECIPE'S OWN POWER IS WEIGHED AT WHAT THE STRIKE PAYS IT (PW1-G): in the short copy, whose works price is not its industry
>      price, a Pulp Mill's surplus at the works price, what Wood Products' statement is paid for it, and not at the industry price;
>      an Oxygen Steel Shop's own power netted at its class's.
> 
> Every fixture is a scratch city (HistoricalLicenceCheck.town(), PowerPriceCheck.town()) and CAUSES its condition through the
> model's inputs: Pulp Mills for the water's demand, the homes' draw for the power's price. Every expected figure is a getter or a
> constant of the model.

**Uses:** [Game](Game.md) (18), [UtilitiesHandler](UtilitiesHandler.md) (17), [PublicLicenceCheck](PublicLicenceCheck.md) (10), [Recipes](Recipes.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (6), [PowerPriceCheck](PowerPriceCheck.md) (4), [BuildingsStacks](BuildingsStacks.md) (3), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (3), [PowerDispatch](PowerDispatch.md) (3), [Recipe](Recipe.md) (3), [MoneyAudit](MoneyAudit.md) (2), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (2), [BuildingType](BuildingType.md) (1), [ResearchCheck](ResearchCheck.md) (1), [ResearchCatalog](ResearchCatalog.md) (1), [PriceLevel](PriceLevel.md) (1), [Sector](Sector.md) (1), [Sectors](Sectors.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 130 | · 1 |
| 155 | · 2 |
| 174 | · 5 |
| 193 | · 6 |
| 224 | · 7 |
| 253 | · 3 |
| 277 | · 4 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `WaterPowerCheck.MOVE_KW` | `1000` | kW a plant is moved by in section 4: well inside every short half-block's shortfall in the dear copy. |
| 49 | `WaterPowerCheck.SLOT` | `10` | The save slot the scratch cities are saved in: past the player's nine. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 272 | **type** `public class WaterPowerCheck` | The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's power read from the dispatch (PW1-L) and a recipe's own power weighed at the prices the... |
| 51 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 57 | 3 | `static boolean near(double a, double b, double rel)` |  |
| 61 | 1 | `static String f(double v)` |  |
| 64 | 4 | **type** `record Works(double freshKw, double seaKw, double served, double withoutSea, double wellsAlone)` | What the water works stand and deliver, worked from the stacks, the templates and waterOutput(): independent of the handler's record. |
| 65 | 1 | `double freshDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 66 | 1 | `double seaDelivered()` _(in WaterPowerCheck.Works)_ |  |
| 69 | 16 | `static Works works(Game g)` |  |
| 87 | 10 | `static int mills(Game g, boolean pastEveryWorks)` | Pulp Mills past what the city's water works make at full staffing, or past the founding wells alone: how many stood. |
| 98 | 31 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 130-154)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 22 | `static void surplus(Game d, BuildingsTemplate desal, BuildingsStacks sea)` |  |

### 2 (lines 155-173)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 16 | `static void short_(Game d, int n)` |  |

### 5 (lines 174-192)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 16 | `static void dearPower(Game lo, Game hi, int nlo, int nhi)` |  |

### 6 (lines 193-223)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 28 | `static void licence(Game lo, Game hi)` |  |

### 7 (lines 224-252)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 26 | `static void recipes(Game d, int n)` |  |

### 3 (lines 253-276)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 21 | `static void money(Game d)` |  |

### 4 (lines 277-312)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 33 | `static void save(Game d) throws Exception` |  |

