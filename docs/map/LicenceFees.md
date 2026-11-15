# LicenceFees.java - 271 lines · 19 methods · 4 constants · model

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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (12), [Game](Game.md) (12), [Recipes](Recipes.md) (11), [Sector](Sector.md) (11), [Recipe](Recipe.md) (9), [ResearchCatalog](ResearchCatalog.md) (6), [SpreadPlanner](SpreadPlanner.md) (6), [Research](Research.md) (4), [EquipmentFit](EquipmentFit.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [Refining](Refining.md) (3), [ProcessingSector](ProcessingSector.md) (3), [Equipment](Equipment.md) (3), [BusinessInvestment](BusinessInvestment.md) (2), [ExtractionSector](ExtractionSector.md) (2), [JobType](JobType.md) (2), [RefineryFlow](RefineryFlow.md) (2)

**Used by (8):** [EquipmentFit](EquipmentFit.md), [Game](Game.md), [HistorySave](HistorySave.md), [LicenceFeesCheck](LicenceFeesCheck.md), [Markets](Markets.md), [MoneyAudit](MoneyAudit.md), [Recipes](Recipes.md), [Sector](Sector.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `LicenceFees.SHARE` | `.25` | The world's share of the positive monthly gain from a patented entry. |
| 28 | `LicenceFees.TERM_MONTHS` | `20 * 12` | Patent life from history's month, in months; adoption never restarts it. |
| 30 | `LicenceFees.IP_CHARGES` | `"IP charges"` | The imported service's name on the firm's saved statement and the audit. |
| 71 | `LicenceFees.pricing` | `ThreadLocal.withInitial(() -> false)` | A new building's gain is its sector's estimate, including special outputs (refinery streams, retail service). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 250 | **type** `public final class LicenceFees` | The patent rent on discoveries imported by a city founded the 1900 way. |
| 23 | 1 | `private LicenceFees()` |  |
| 33 | 6 | `public static boolean payable(String code, int month, Research found)` | A patent is payable from history's month, excluding its twentieth anniversary. |
| 41 | 3 | `public static double onGain(String code, int month, Research found, double gain)` | A loss, year, unknown code or unpatented entry costs nothing. |
| 50 | 11 | `public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The gain over the earlier way this entry replaces, at the same prices. |
| 63 | 3 | `public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The recipe's spread after the licence, for selection and fitting alike. |
| 73 | 14 | `private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing)` |  |
| 89 | 14 | `private static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner)` | Every installed kind uses removal/standing values, never an additional-building appraisal. |
| 105 | 9 | `private static double productionRate(BuildingsTemplate t, Sector owner)` | The common dispatch share of these outputs, including direct exports and captive production. |
| 116 | 8 | `private static double standingRecipeValue(Recipe recipe, Recipes.Prices p, SpreadPlanner.Values values, double rate)` | A running recipe's value under the standing output/input price roles. |
| 125 | 12 | `private static double standingRecipeFee(Game game, BuildingsTemplate t, Recipe recipe, Recipes.Prices p, double rate)` |  |
| 144 | 9 | `private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref)` | What this installed conversion unit adds on the feed it actually takes. |
| 155 | 19 | `private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted, boolean standing)` | One building with this recipe and these pieces, at its owner's run rate. |
| 176 | 12 | `public static double forNew(Game game, BuildingsTemplate t)` | A new building's licence cost, included in every planner's running cost. |
| 190 | 5 | `public static double forStanding(Game game, BuildingsTemplate t)` | The installed building's saved share of the last production's captured royalty. |
| 204 | 15 | `public static void charge(Game game)` | Capture the production's bill before endOfMonth chooses the next run, mix or prices. |
| 220 | 9 | `private static double stackBill(Game game, BuildingsStacks stack, Sector owner)` |  |
| 230 | 27 | `private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner)` |  |
| 259 | 5 | `public static double paid(Game game)` | Read the settled flow from saved statements, never from end-of-month prices. |
| 266 | 5 | `public static double accrued(Game game)` | Production's accrued bill, already saved in the pending imported-service ledger. |

