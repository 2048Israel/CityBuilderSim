# LicenceFees.java - 335 lines · 25 methods · 4 constants · model

`ham/citybuildersim/LicenceFees.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The patent rent on discoveries imported by a city founded the 1900 way.
> 
> WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
> gain to firms' accounts. A licence now buys an imported service in the same
> strike as every other input. Production captures the bill before the next
> run or mix is chosen; Sector banks that saved bill at the next strike,
> MoneyAudit names it as trade, and ForeignAccounts takes the same month.
> DataSave/SectorState keeps pending and settled inputs and the standing-cost
> memo; HistorySave keeps paid and accrued monthly series.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (19), [Sector](Sector.md) (15), [Game](Game.md) (13), [Recipes](Recipes.md) (11), [Recipe](Recipe.md) (9), [BuildingsStacks](BuildingsStacks.md) (8), [ResearchCatalog](ResearchCatalog.md) (6), [SpreadPlanner](SpreadPlanner.md) (6), [ExtractionSector](ExtractionSector.md) (5), [Research](Research.md) (4), [EquipmentFit](EquipmentFit.md) (4), [Refining](Refining.md) (3), [ProcessingSector](ProcessingSector.md) (3), [Equipment](Equipment.md) (3), [BusinessInvestment](BusinessInvestment.md) (2), [JobType](JobType.md) (2), [RecipeChanges](RecipeChanges.md) (2), [RefineryFlow](RefineryFlow.md) (1), [Resource](Resource.md) (1), [Deposit](Deposit.md) (1)

**Used by (11):** [EquipmentFit](EquipmentFit.md), [Freight](Freight.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistorySave](HistorySave.md), [LicenceFeesCheck](LicenceFeesCheck.md), [Markets](Markets.md), [MoneyAudit](MoneyAudit.md), [RecipeChanges](RecipeChanges.md), [Recipes](Recipes.md), [Sector](Sector.md)

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
| 208 | `private final Game game` |  |
| 209 | `private final Map<Sector, List<BuildingsStacks>> owned` |  |
| 210 | `private final Map<Ground, Double> beforeGround` |  |
| 211 | `private final List<Bill> bills` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 310 | **type** `public final class LicenceFees` | The patent rent on discoveries imported by a city founded the 1900 way. |
| 27 | 1 | `private LicenceFees()` |  |
| 37 | 6 | `public static boolean payable(String code, int month, Research found)` | A patent is payable from history's month, excluding its twentieth anniversary. |
| 45 | 3 | `public static double onGain(String code, int month, Research found, double gain)` | A loss, year, unknown code or unpatented entry costs nothing. |
| 54 | 11 | `public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The gain over the earlier way this entry replaces, at the same prices. |
| 67 | 3 | `public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The recipe's spread after the licence, for selection and fitting alike. |
| 77 | 14 | `private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing, double rate)` |  |
| 93 | 12 | `private static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner, double rate)` | Every installed kind uses removal/standing values, never an additional-building appraisal. |
| 107 | 9 | `private static double productionRate(BuildingsTemplate t, Sector owner)` | The common dispatch share of these outputs, including direct exports and captive production. |
| 118 | 8 | `private static double standingRecipeValue(Recipe recipe, Recipes.Prices p, SpreadPlanner.Values values, double rate)` | A running recipe's value under the standing output/input price roles. |
| 127 | 12 | `private static double standingRecipeFee(Game game, BuildingsTemplate t, Recipe recipe, Recipes.Prices p, double rate)` |  |
| 146 | 9 | `private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref)` | What this installed conversion unit adds on the feed it actually takes. |
| 157 | 20 | `private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted, boolean standing, do...` | One building with this recipe and these pieces, at its owner's run rate. |
| 179 | 12 | `public static double forNew(Game game, BuildingsTemplate t)` | A new building's licence cost, included in every planner's running cost. |
| 193 | 5 | `public static double forStanding(Game game, BuildingsTemplate t)` | The installed building's saved share of the last production's captured royalty. |
| 205 | 78 | **type** `static final class Production` | One market pass's production record, confined to that pass. |
| 206 | 1 | **type** `private record Ground(Resource resource, Deposit.FieldType type)` _(in LicenceFees.Production)_ |  |
| 207 | 1 | **type** `private record Bill(Sector owner, int id, int quantity, double amount)` _(in LicenceFees.Production)_ |  |
| 213 | 7 | `Production(Game game)` _(in LicenceFees.Production)_ |  |
| 222 | 3 | `private static boolean stockPhase(BuildingsTemplate t)` _(in LicenceFees.Production)_ | A conversion unit makes streams in the refinery's stock phase. |
| 226 | 4 | `private Ground ground(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ |  |
| 232 | 10 | `void before(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Flow mines/cutters remove the ground inside production. |
| 244 | 16 | `private double flowExtractionRate(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ | The real typed lift divided among the works that made it, including a field's last dispatch. |
| 262 | 9 | `void made(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Called directly after this sector's output loop; capture all money inputs before any other producer or next-month choice. |
| 273 | 9 | `void finish()` _(in LicenceFees.Production)_ | Book immutable production bills once, into the pending imported-service ledger for the next strike. |
| 284 | 9 | `private static double stackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 294 | 27 | `private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 323 | 5 | `public static double paid(Game game)` | Read the settled flow from saved statements, never from end-of-month prices. |
| 330 | 5 | `public static double accrued(Game game)` | Production's accrued bill, already saved in the pending imported-service ledger. |

