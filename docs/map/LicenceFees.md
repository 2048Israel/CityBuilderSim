# LicenceFees.java - 441 lines · 32 methods · 4 constants · model

`ham/citybuildersim/LicenceFees.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The patent rent on discoveries imported by a city founded the 1900 way.
> 
> WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
> gain to firms' accounts. A licence now buys an imported service in the same
> strike as every other input. Production captures the bill before the next
> run or mix is chosen; Sector banks that saved bill at the next strike,
> MoneyAudit names it as trade, and ForeignAccounts takes the same month.
> DataSave/SectorState keeps pending and settled inputs and the standing-cost
> memo; HistorySave keeps paid and accrued monthly series.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (24), [Game](Game.md) (19), [Sector](Sector.md) (15), [Recipes](Recipes.md) (13), [BuildingsStacks](BuildingsStacks.md) (10), [Recipe](Recipe.md) (9), [RecipeChanges](RecipeChanges.md) (8), [BuildingType](BuildingType.md) (7), [Research](Research.md) (6), [ResearchCatalog](ResearchCatalog.md) (6), [SpreadPlanner](SpreadPlanner.md) (6), [ExtractionSector](ExtractionSector.md) (5), [UtilitiesHandler](UtilitiesHandler.md) (5), [Equipment](Equipment.md) (4), [EquipmentFit](EquipmentFit.md) (4), [Refining](Refining.md) (3), [ProcessingSector](ProcessingSector.md) (3), [BusinessInvestment](BusinessInvestment.md) (2), [JobType](JobType.md) (2), [RefineryFlow](RefineryFlow.md) (1), [Resource](Resource.md) (1), [Deposit](Deposit.md) (1), [BuildAdvice](BuildAdvice.md) (1)

**Used by (17):** [BuildAdvice](BuildAdvice.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [EquipmentFit](EquipmentFit.md), [FittingCheck](FittingCheck.md), [Freight](Freight.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistorySave](HistorySave.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [Markets](Markets.md), [MoneyAudit](MoneyAudit.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RecipeChanges](RecipeChanges.md), [Recipes](Recipes.md), [Sector](Sector.md)

## Sections

| line | section |
|---:|---|
| 335 | THE CITY'S OWN WORKS (R2c-P) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `LicenceFees.SHARE` | `.25` | The world's share of the positive monthly gain from a patented entry. |
| 32 | `LicenceFees.TERM_MONTHS` | `20 * 12` | Patent life from history's month, in months; adoption never restarts it. |
| 34 | `LicenceFees.IP_CHARGES` | `"IP charges"` | The imported service's name on the firm's saved statement and the audit. |
| 75 | `LicenceFees.pricing` | `ThreadLocal.withInitial(() -> false)` | A new building's gain is its sector's estimate, including special outputs (refinery streams, retail service). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 206 | `private final Game game` |  |
| 207 | `private final Map<Sector, List<BuildingsStacks>> owned` |  |
| 208 | `private final Map<Ground, Double> beforeGround` |  |
| 209 | `private final List<Bill> bills` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 26 | 416 | **type** `public final class LicenceFees` | The patent rent on discoveries imported by a city founded the 1900 way. |
| 27 | 1 | `private LicenceFees()` |  |
| 37 | 6 | `public static boolean payable(String code, int month, Research found)` | A patent is payable from history's month, excluding its twentieth anniversary. |
| 45 | 3 | `public static double onGain(String code, int month, Research found, double gain)` | A loss, year, unknown code or unpatented entry costs nothing. |
| 54 | 11 | `public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The gain over the earlier way this entry replaces, at the same prices. |
| 67 | 3 | `public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The recipe's spread after the licence, for selection and fitting alike. |
| 78 | 14 | `private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing, double rate)` |  |
| 94 | 12 | `private static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner, double rate)` | Every installed kind uses removal/standing values, never an additional-building appraisal. |
| 108 | 9 | `private static double productionRate(BuildingsTemplate t, Sector owner)` | The common dispatch share of these outputs, including direct exports and captive production. |
| 119 | 8 | `private static double standingRecipeValue(Recipe recipe, Recipes.Prices p, SpreadPlanner.Values values, double rate)` | A running recipe's value under the standing output/input price roles. |
| 128 | 12 | `private static double standingRecipeFee(Game game, BuildingsTemplate t, Recipe recipe, Recipes.Prices p, double rate)` |  |
| 147 | 9 | `private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref)` | What this installed conversion unit adds on the feed it actually takes. |
| 158 | 21 | `private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted, boolean standing, do...` | One building with this recipe and these pieces, at its owner's run rate. |
| 181 | 8 | `public static double forNew(Game game, BuildingsTemplate t)` | A new building's licence cost, included in every planner's running cost. |
| 191 | 5 | `public static double forStanding(Game game, BuildingsTemplate t)` | The installed building's saved share of the last production's captured royalty. |
| 203 | 78 | **type** `static final class Production` | One market pass's production record, confined to that pass. |
| 204 | 1 | **type** `private record Ground(Resource resource, Deposit.FieldType type)` _(in LicenceFees.Production)_ |  |
| 205 | 1 | **type** `private record Bill(Sector owner, int id, long quantity, double amount)` _(in LicenceFees.Production)_ |  |
| 211 | 7 | `Production(Game game)` _(in LicenceFees.Production)_ |  |
| 220 | 3 | `private static boolean stockPhase(BuildingsTemplate t)` _(in LicenceFees.Production)_ | A conversion unit makes streams in the refinery's stock phase. |
| 224 | 4 | `private Ground ground(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ |  |
| 230 | 10 | `void before(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Flow mines/cutters remove the ground inside production. |
| 242 | 16 | `private double flowExtractionRate(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ | The real typed lift divided among the works that made it, including a field's last dispatch. |
| 260 | 9 | `void made(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Called directly after this sector's output loop; capture all money inputs before any other producer or next-month choice. |
| 271 | 9 | `void finish()` _(in LicenceFees.Production)_ | Book immutable production bills once, into the pending imported-service ledger for the next strike. |
| 282 | 9 | `private static double stackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 292 | 28 | `private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 322 | 5 | `public static double paid(Game game)` | Read the settled flow from saved statements, never from end-of-month prices. |
| 329 | 5 | `public static double accrued(Game game)` | Production's accrued bill, already saved in the pending imported-service ledger. |

### THE CITY'S OWN WORKS (R2c-P) (lines 335-441)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 3 | `static boolean citysWork(Game game, BuildingsTemplate t)` | Whether this template is one of the city's own works, the stacks no sector owns (the bank's branch excepted), which pay their licences too (R2c-P). |
| 343 | 3 | `static double powerDelivered(UtilitiesHandler u, double nameplate)` | What the grid delivers with its nameplate moved, by UtilitiesHandler.updateEnergyRatio()'s rule: output, capped by what the city asks (R2c-P). |
| 348 | 5 | `static double waterDelivered(Game game, UtilitiesHandler u, double freshMove, double desalMove)` | What the water works deliver with the fresh and the sea nameplates moved, by UtilitiesHandler.waterOutput()'s rule, capped by what the city asks (R2c-P). |
| 360 | 19 | `public static double publicGain(Game game, BuildingsStacks stack, String pieceId)` | The month's public gain of one entry on one standing stack of a city utility, in money (R2c-P): the service delivered with it less the service delivered without it, at the tariff the planners price it at (Recipes.pric... |
| 384 | 20 | `public static double capturePublic(Game game)` | The month's bill on the city's own patented works (R2c-P), struck once the utilities hold the month's output and prices: each entry once a stack, SHARE of its positive public gain while its patent runs. |
| 406 | 8 | `private static double added(Game game, BuildingsTemplate t, double plate)` | The service one more building of this nameplate adds to what the grid or the works deliver, in money, at the planners' tariff (R2c-P). |
| 420 | 21 | `public static double forNewWork(Game game, BuildingsTemplate t, double running)` | The planners' licence cost of ONE more building of a city utility (R2c-P), added to its running cost: SHARE of the positive gain of each patented entry it would carry (the building's own over its running cost, each pi... |

