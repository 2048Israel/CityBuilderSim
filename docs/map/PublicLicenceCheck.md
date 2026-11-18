# PublicLicenceCheck.java - 561 lines · 26 methods · 0 constants · harnesses

`ham/citybuildersim/PublicLicenceCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the
> service it delivers with that upgrade over what it would deliver without it, in a city founded the 1900 way, while the patent runs.
> 
> WHY (R2c-P). R2c charged the firms only: the city's patented utilities (the water works' rapid sand, the coal plant's steam turbine and
> pulverised coal, the desalination and wind building entries) used an imported discovery for nothing, and the public planners counted the
> whole gain. Each fixture here stands a real shortage that the patented piece relieves and asserts the expected fee, worked out
> independently from the handler's own water rule, before and after it crosses the strike, the audit, the national accounts, the history, a
> save and a load. A guard (the catalogue's premise, a city founded today, a local find, a berth, a gain of nothing) is asserted beside
> the thing it guards, so a fee that is wrong in either direction fails by name.

**Uses:** [LicenceFees](LicenceFees.md) (32), [UtilitiesHandler](UtilitiesHandler.md) (24), [Game](Game.md) (23), [ResearchCatalog](ResearchCatalog.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (15), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (15), [RecipeCheck](RecipeCheck.md) (7), [BuildingsStacks](BuildingsStacks.md) (7), [ResearchCheck](ResearchCheck.md) (6), [BuildAdvice](BuildAdvice.md) (6), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (5), [LicenceFeesCheck](LicenceFeesCheck.md) (5), [NationalAccounts](NationalAccounts.md) (5), [RecipeChange](RecipeChange.md) (4), [Recipes](Recipes.md) (4), [Ports](Ports.md) (4), [HistorySave](HistorySave.md) (4), [FittingCheck](FittingCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [Equipment](Equipment.md) (2), [BuildingType](BuildingType.md) (2), [MoneyAudit](MoneyAudit.md) (2), [RecipeChanges](RecipeChanges.md) (2), [EquipmentFit](EquipmentFit.md) (2), [GameFiles](GameFiles.md) (2), [DataSave](DataSave.md) (2), [NewGameCheck](NewGameCheck.md) (2), [Recipe](Recipe.md) (1), [Sector](Sector.md) (1)... and 1 more

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 56 | the fixtures |
| 109 | 1. the catalogue's premise |
| 151 | 2. a patented piece pays the patent share of what it delivers |
| 208 | 3. the patent ends |
| 252 | 4. a local discovery pays nothing |
| 276 | 5. an unpatented berth |
| 328 | 6. no gain, no fee |
| 397 | 7. the planners net the fee |
| 438 | 8. the month both ways |
| 463 | 9. a city founded today |
| 492 | SaveFileCheck and ReadPathCheck |

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
| 19 | 543 | **type** `public final class PublicLicenceCheck` | The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the service it delivers with that upgrade over what it would deliver without it, in a cit... |
| 22 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 28 | 3 | `static boolean near(double a, double b)` |  |
| 33 | 6 | `static void wire()` | The helper harnesses this one borrows narrate through their own streams; wired as HistoricalLicenceCheck.closedSteamInvoice() does, for a run from SaveFileCheck or ReadPathCheck too. |
| 40 | 1 | `static void months(Game g, int n)` |  |
| 43 | 7 | `static void allExcept(Game g, String...except)` | Research with everything found at home but these codes (HistoricalRecipeCheck.allExcept() takes one). |
| 52 | 3 | `static double firmBill(Game g)` | The statement's imported patent service, of the sector that would carry a firm's bill. |

### the fixtures (lines 56-108)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 10 | **type** `static final class Works` | A 1900 town whose one water works carries rapid sand (PE5) from outside, with water-hungry works standing beside it. |
| 65 | 1 | `UtilitiesHandler u()` _(in PublicLicenceCheck.Works)_ |  |
| 67 | 1 | `double delta()` _(in PublicLicenceCheck.Works)_ | The nameplate rapid sand adds to the stack that is fitted, by the catalogue's own undo. |
| 75 | 18 | `static Works waterWorks(String label, String abroad, boolean overload) throws Exception` | Built at PE5's own month; one month played so the city's draw and staffing are read as they stand; then as many Pulp Mills (11600 water a building) as the works could not serve at full staffing: without the rapid sand... |
| 95 | 1 | **type** `record Water(double demand, double output, double with, double without, double tariff, double gain, double ...` | What the works deliver with and without the piece, and the patent share of the difference at the planners' tariff: read at rest, independently. |
| 97 | 11 | `static Water water(Works w)` |  |

### 1. the catalogue's premise (lines 109-150)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 5 | `static boolean payableEntry(String code)` |  |
| 117 | 33 | `static void catalogue(Game g)` |  |

### 2. a patented piece pays the patent share of what it delivers (lines 151-207)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 54 | `static Works payment() throws Exception` |  |

### 3. the patent ends (lines 208-251)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 41 | `static void expiry() throws Exception` |  |

### 4. a local discovery pays nothing (lines 252-275)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 21 | `static void local(Works imported) throws Exception` |  |

### 5. an unpatented berth (lines 276-327)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 9 | `static long crowd(Game g, double desalPlate)` | Pulp Mills enough to take the city's demand past what its water makes without this much desalination at full staffing; how many. |
| 290 | 10 | `static double[] desalReading(Game d, BuildingsTemplate desal, BuildingsStacks sea)` | The desalination stack's month, read at rest and worked independently: [demand, delivered with, delivered without, gain over running cost]. |
| 301 | 26 | `static void berth() throws Exception` |  |

### 6. no gain, no fee (lines 328-396)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 66 | `static void noGain() throws Exception` |  |

### 7. the planners net the fee (lines 397-437)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 38 | `static void planners(Works w) throws Exception` |  |

### 8. the month both ways (lines 438-462)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 22 | `static void bothWays() throws Exception` |  |

### 9. a city founded today (lines 463-491)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 26 | `static void today() throws Exception` |  |

### SaveFileCheck and ReadPathCheck (lines 492-561)

| line | len | member | says |
|---:|---:|---|---|
| 495 | 27 | `static boolean saveChecks() throws Exception` | The bill across DataSave, HistorySave and the load: nonzero pending and paid, scratch slot 10 only. |
| 524 | 13 | `static boolean readChecks() throws Exception` | Reading the bill and its planners again and again changes no money pool and no figure. |
| 538 | 5 | `private static double[] figures(Game g, Works w, double raw)` |  |
| 544 | 17 | `public static void main(String[] args) throws Exception` |  |

