# PublicLicenceCheck.java - 786 lines · 34 methods · 0 constants · harnesses

`ham/citybuildersim/PublicLicenceCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the
> service it delivers with that upgrade over what it would deliver without it, in a city founded the 1900 way, while the patent runs.
> 
> WHY (R2c-P). R2c charged the firms only: the city's patented utilities (the water works' rapid sand, the coal plant's steam turbine and
> pulverised coal, the desalination and wind building entries) used an imported discovery for nothing, and the public planners counted the
> whole gain. Each fixture here stands a real shortage that the patented piece relieves and asserts the expected fee, worked out
> independently from the handler's own water rule, before and after it crosses the strike, the audit, the national accounts, the history, a
> save and a load. A guard (the catalogue's premise, a city founded today, a local find, a berth, a gain of nothing) is asserted beside
> the thing it guards, so a fee that is wrong in either direction fails by name.

**Uses:** [LicenceFees](LicenceFees.md) (52), [UtilitiesHandler](UtilitiesHandler.md) (34), [BuildAdvice](BuildAdvice.md) (30), [Game](Game.md) (28), [ResearchCatalog](ResearchCatalog.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (17), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (16), [ResearchCheck](ResearchCheck.md) (11), [RecipeCheck](RecipeCheck.md) (7), [BuildingsStacks](BuildingsStacks.md) (7), [NationalAccounts](NationalAccounts.md) (6), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (5), [Recipes](Recipes.md) (5), [LicenceFeesCheck](LicenceFeesCheck.md) (5), [EquipmentFit](EquipmentFit.md) (5), [RecipeChange](RecipeChange.md) (4), [Equipment](Equipment.md) (4), [Ports](Ports.md) (4), [MoneyAudit](MoneyAudit.md) (4), [RecipeChanges](RecipeChanges.md) (4), [HistorySave](HistorySave.md) (4), [PowerDispatch](PowerDispatch.md) (3), [AutoBuilder](AutoBuilder.md) (3), [FittingCheck](FittingCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [BuildingType](BuildingType.md) (2), [GameFiles](GameFiles.md) (2), [DataSave](DataSave.md) (2), [NewGameCheck](NewGameCheck.md) (2)... and 5 more

**Used by (3):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [WaterPowerCheck](WaterPowerCheck.md)

## Sections

| line | section |
|---:|---|
| 56 | the fixtures |
| 169 | 1. the catalogue's premise |
| 211 | 2. a patented piece pays the patent share of what it delivers |
| 273 | 3. the patent ends |
| 317 | 4. a local discovery pays nothing |
| 341 | 5. an unpatented berth |
| 393 | 6. no gain, no fee |
| 480 | 7. the planners net the fee |
| 544 | 7b. the term a new plant's licence is weighed by |
| 593 | 7c. an order of plants is billed as one |
| 622 | 7d. what the auto-builder reads of a utility's cost |
| 660 | 8. the month both ways |
| 685 | 9. a city founded today |
| 714 | SaveFileCheck and ReadPathCheck |

## Fields (state)

| line | field | says |
|---:|---|---|
| 20 | `private static int checks, fails` |  |
| 60 | `Game g` |  |
| 61 | `BuildingsTemplate plant` |  |
| 62 | `BuildingsStacks stack` |  |
| 63 | `RecipeChange change` |  |
| 64 | `long mills` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 768 | **type** `public final class PublicLicenceCheck` | The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the service it delivers with that upgrade over what it would deliver without it, in a cit... |
| 22 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 28 | 3 | `static boolean near(double a, double b)` |  |
| 33 | 6 | `static void wire()` | The helper harnesses this one borrows narrate through their own streams; wired as HistoricalLicenceCheck.closedSteamInvoice() does, for a run from SaveFileCheck or ReadPathCheck too. |
| 40 | 1 | `static void months(Game g, int n)` |  |
| 43 | 7 | `static void allExcept(Game g, String...except)` | Research with everything found at home but these codes (HistoricalRecipeCheck.allExcept() takes one). |
| 52 | 3 | `static double firmBill(Game g)` | The statement's imported patent service, of the sector that would carry a firm's bill. |

### the fixtures (lines 56-168)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 10 | **type** `static final class Works` | A 1900 town whose one water works carries rapid sand (PE5) from outside, with water-hungry works standing beside it. |
| 65 | 1 | `UtilitiesHandler u()` _(in PublicLicenceCheck.Works)_ |  |
| 67 | 1 | `double delta()` _(in PublicLicenceCheck.Works)_ | The nameplate rapid sand adds to the stack that is fitted, by the catalogue's own undo. |
| 75 | 10 | `static Works waterWorks(String label, String abroad, boolean overload) throws Exception` | Built at PE5's own month; one month played so the city's draw and staffing are read as they stand; then as many Pulp Mills (11600 water a building) as the works could not serve at full staffing: without the rapid sand... |
| 87 | 12 | `static Works plantOnly(String label, String abroad) throws Exception` | A 1900 town at PE5's own month with one water works carrying rapid sand, one month played and nothing yet drawing on it (waterWorks() and smallShortage() stand the mills). |
| 104 | 11 | `static Works smallShortage(String label) throws Exception` | The least shortage the works can be made short by (REV-R2c-P-1): Pulp Mills one at a time, a month played after each, until the city asks more water than the works make. |
| 121 | 11 | `static double newFee(Works w, long units)` | The patent share of what `units` new plants of this works' template add to the water delivered, over `units` lacking the rapid sand, at the planners' tariff (REV-R2c-P-1): worked from the handler's own water rule at r... |
| 137 | 6 | `static double share(Game g, int termLeft)` | The share of a building's life a patent with `termLeft` months to run covers (REV-R2c-P-1), worked here from the city's life rate: the level annuity over the lesser of LIFE_MONTHS and the term left, over the annuity o... |
| 145 | 8 | `static double licenceOfOrder(Game g, BuildingsTemplate plant, long units, double monthsAhead)` | What the works' fitting adds to the running cost of an order of plants `monthsAhead` ahead: the order's change with the discovery abroad less the same with it found at home (REV-R2c-P-1). |
| 155 | 1 | **type** `record Water(double demand, double output, double with, double without, double tariff, double gain, double ...` | What the works deliver with and without the piece, and the patent share of the difference at the planners' tariff: read at rest, independently. |
| 157 | 11 | `static Water water(Works w)` |  |

### 1. the catalogue's premise (lines 169-210)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 5 | `static boolean payableEntry(String code)` |  |
| 177 | 33 | `static void catalogue(Game g)` |  |

### 2. a patented piece pays the patent share of what it delivers (lines 211-272)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 59 | `static Works payment() throws Exception` |  |

### 3. the patent ends (lines 273-316)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 41 | `static void expiry() throws Exception` |  |

### 4. a local discovery pays nothing (lines 317-340)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 21 | `static void local(Works imported) throws Exception` |  |

### 5. an unpatented berth (lines 341-392)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 9 | `static long crowd(Game g, double desalPlate)` | Pulp Mills enough to take the city's demand past what its water makes without this much desalination at full staffing; how many. |
| 355 | 10 | `static double[] desalReading(Game d, BuildingsTemplate desal, BuildingsStacks sea)` | The desalination stack's month, read at rest and worked independently: [demand, delivered with, delivered without, gain over running cost]. |
| 366 | 26 | `static void berth() throws Exception` |  |

### 6. no gain, no fee (lines 393-479)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 84 | `static void noGain() throws Exception` |  |

### 7. the planners net the fee (lines 480-543)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 61 | `static void planners(Works w) throws Exception` |  |

### 7b. the term a new plant's licence is weighed by (lines 544-592)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 46 | `static void term() throws Exception` |  |

### 7c. an order of plants is billed as one (lines 593-621)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 26 | `static void units() throws Exception` |  |

### 7d. what the auto-builder reads of a utility's cost (lines 622-659)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 35 | `static void costs(Works w)` |  |

### 8. the month both ways (lines 660-684)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 22 | `static void bothWays() throws Exception` |  |

### 9. a city founded today (lines 685-713)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 26 | `static void today() throws Exception` |  |

### SaveFileCheck and ReadPathCheck (lines 714-786)

| line | len | member | says |
|---:|---:|---|---|
| 717 | 27 | `static boolean saveChecks() throws Exception` | The bill across DataSave, HistorySave and the load: nonzero pending and paid, scratch slot 10 only. |
| 746 | 13 | `static boolean readChecks() throws Exception` | Reading the bill and its planners again and again changes no money pool and no figure. |
| 760 | 5 | `private static double[] figures(Game g, Works w, double raw)` |  |
| 766 | 20 | `public static void main(String[] args) throws Exception` |  |

