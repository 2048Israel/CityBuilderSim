# LicenceFees.java - 169 lines · 11 methods · 4 constants · model

`ham/citybuildersim/LicenceFees.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The patent rent on discoveries imported by a city founded the 1900 way.
> 
> WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
> gain to firms' accounts. A licence now buys an imported service in the same
> strike as every other input: Sector banks it, MoneyAudit names it as trade,
> and ForeignAccounts takes that reconciled month. DataSave/SectorState keeps
> the statement's named input; HistorySave keeps the monthly series.

**Uses:** [Recipes](Recipes.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [Recipe](Recipe.md) (6), [Game](Game.md) (6), [Sector](Sector.md) (5), [Research](Research.md) (4), [ResearchCatalog](ResearchCatalog.md) (4), [EquipmentFit](EquipmentFit.md) (4), [Equipment](Equipment.md) (3), [Refining](Refining.md) (2), [RefineryFlow](RefineryFlow.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (7):** [EquipmentFit](EquipmentFit.md), [Game](Game.md), [HistorySave](HistorySave.md), [LicenceFeesCheck](LicenceFeesCheck.md), [MoneyAudit](MoneyAudit.md), [Recipes](Recipes.md), [Sector](Sector.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 21 | `LicenceFees.SHARE` | `.25` | The world's share of the positive monthly gain from a patented entry. |
| 23 | `LicenceFees.TERM_MONTHS` | `20 * 12` | Patent life from history's month, in months; adoption never restarts it. |
| 25 | `LicenceFees.IP_CHARGES` | `"IP charges"` | The imported service's name on the firm's saved statement and the audit. |
| 66 | `LicenceFees.pricing` | `ThreadLocal.withInitial(() -> false)` | A building's gain is its sector's estimate, including special outputs (refinery streams, retail service). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 153 | **type** `public final class LicenceFees` | The patent rent on discoveries imported by a city founded the 1900 way. |
| 18 | 1 | `private LicenceFees()` |  |
| 28 | 6 | `public static boolean payable(String code, int month, Research found)` | A patent is payable from history's month, excluding its twentieth anniversary. |
| 36 | 3 | `public static double onGain(String code, int month, Research found, double gain)` | A loss, year, unknown code or unpatented entry costs nothing. |
| 45 | 11 | `public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The gain over the earlier way this entry replaces, at the same prices. |
| 58 | 3 | `public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The recipe's spread after the licence, for selection and fitting alike. |
| 68 | 12 | `private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing)` |  |
| 87 | 9 | `private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref)` | What this installed conversion unit adds on the feed it actually takes. |
| 98 | 16 | `private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted, boolean standing)` | One building with this recipe and these pieces, at its owner's run rate. |
| 116 | 12 | `public static double forNew(Game game, BuildingsTemplate t)` | A new building's licence cost, included in every planner's running cost. |
| 134 | 28 | `public static void charge(Game game)` | Bill standing firms immediately before their statements strike. |
| 164 | 5 | `public static double paid(Game game)` | Read the settled flow from saved statements, never from end-of-month prices. |

