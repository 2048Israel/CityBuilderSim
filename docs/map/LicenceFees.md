# LicenceFees.java - 593 lines · 49 methods · 5 constants · model

`ham/citybuildersim/LicenceFees.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> The patent rent on discoveries imported by a city founded the 1900 way.
> 
> WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
> gain to firms' accounts. A licence now buys an imported service in the same
> strike as every other input. Production captures the bill before the next
> run or mix is chosen; Sector banks that saved bill at the next strike,
> MoneyAudit names it as trade, and ForeignAccounts takes the same month.
> DataSave/SectorState keeps pending and settled inputs and the standing-cost
> memo; HistorySave keeps paid and accrued monthly series.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (33), [Game](Game.md) (31), [Sector](Sector.md) (18), [RecipeChanges](RecipeChanges.md) (15), [Recipes](Recipes.md) (14), [BuildingsStacks](BuildingsStacks.md) (13), [Recipe](Recipe.md) (11), [ResearchCatalog](ResearchCatalog.md) (9), [NewUnit](NewUnit.md) (8), [EquipmentFit](EquipmentFit.md) (7), [SpreadPlanner](SpreadPlanner.md) (6), [BuildingType](BuildingType.md) (6), [BuildAdvice](BuildAdvice.md) (6), [Research](Research.md) (5), [ExtractionSector](ExtractionSector.md) (5), [UtilitiesHandler](UtilitiesHandler.md) (4), [Refining](Refining.md) (3), [ProcessingSector](ProcessingSector.md) (3), [Equipment](Equipment.md) (3), [BusinessInvestment](BusinessInvestment.md) (2), [JobType](JobType.md) (2), [RefineryFlow](RefineryFlow.md) (1), [Resource](Resource.md) (1), [Deposit](Deposit.md) (1)

**Used by (24):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentFit](EquipmentFit.md), [FB13Check](FB13Check.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [Fitout](Fitout.md), [FittingCheck](FittingCheck.md), [Freight](Freight.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistorySave](HistorySave.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [Markets](Markets.md), [MoneyAudit](MoneyAudit.md), [NewUnit](NewUnit.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RecipeChanges](RecipeChanges.md), [Recipes](Recipes.md), [ResearchView](ResearchView.md), [Sector](Sector.md)

## Sections

| line | section |
|---:|---|
| 359 | THE CITY'S OWN WORKS (R2c-P) |
| 551 | WHAT A BUILD CARD SAYS OF ITS LICENCE (FB13, R5-L) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `LicenceFees.SHARE` | `.25` | The world's share of the positive monthly gain from a patented entry. |
| 33 | `LicenceFees.TERM_MONTHS` | `20 * 12` | Patent life from history's month, in months; adoption never restarts it. |
| 35 | `LicenceFees.IP_CHARGES` | `"IP charges"` | The imported service's name on the firm's saved statement and the audit. |
| 76 | `LicenceFees.pricing` | `ThreadLocal.withInitial(() -> false)` | A new building's gain is its sector's estimate, including special outputs (refinery streams, retail service). |
| 563 | `LicenceFees.Rent.NONE` | `new Rent(0, List.of())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 230 | `private final Game game` |  |
| 231 | `private final Map<Sector, List<BuildingsStacks>> owned` |  |
| 232 | `private final Map<Ground, Double> beforeGround` |  |
| 233 | `private final List<Bill> bills` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 567 | **type** `public final class LicenceFees` | The patent rent on discoveries imported by a city founded the 1900 way. |
| 28 | 1 | `private LicenceFees()` |  |
| 38 | 6 | `public static boolean payable(String code, int month, Research found)` | A patent is payable from history's month, excluding its twentieth anniversary. |
| 46 | 3 | `public static double onGain(String code, int month, Research found, double gain)` | A loss, year, unknown code or unpatented entry costs nothing. |
| 55 | 11 | `public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The gain over the earlier way this entry replaces, at the same prices. |
| 68 | 3 | `public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found)` | The recipe's spread after the licence, for selection and fitting alike. |
| 79 | 1 | `static boolean pricing()` | Whether this thread is inside a licence's pricing: NewUnit.of() reads the templates as they stand there. |
| 83 | 3 | `private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing, double rate)` | One building's fee as main read it: a new one on the template as it stands (PostwarRecipeCheck reads this one). |
| 87 | 14 | `private static double buildingFee(Game game, BuildingsTemplate t, NewUnit unit, Sector owner, boolean standing, double rate)` |  |
| 103 | 13 | `static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner, double rate)` | Every installed kind uses removal/standing values, never an additional-building appraisal. |
| 118 | 9 | `private static double productionRate(BuildingsTemplate t, Sector owner)` | The common dispatch share of these outputs, including direct exports and captive production. |
| 129 | 8 | `private static double standingRecipeValue(Recipe recipe, Recipes.Prices p, SpreadPlanner.Values values, double rate)` | A running recipe's value under the standing output/input price roles. |
| 138 | 12 | `private static double standingRecipeFee(Game game, BuildingsTemplate t, Recipe recipe, Recipes.Prices p, double rate)` |  |
| 157 | 9 | `private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref)` | What this installed conversion unit adds on the feed it actually takes. |
| 168 | 3 | `private static double one(Game game, BuildingsTemplate t, NewUnit unit, Sector owner, Recipe recipe, Set<String> fitted, boolea...` | One building with this recipe and these pieces, at its owner's run rate. |
| 173 | 20 | `private static double one(Game game, BuildingsTemplate t, NewUnit unit, Sector owner, Recipe recipe, Set<String> fitted, boolea...` | ...with the codes it struck a licence on, or looked at to (FB13: the card names the patents the bill is made of): `charged` takes the building's own, the recipe's, the recipe changes' and each fitted piece's, in that ... |
| 195 | 4 | `public static double forNew(Game game, BuildingsTemplate t)` | A new building's licence cost, included in every planner's running cost. |
| 204 | 9 | `public static double forNew(Game game, NewUnit unit)` | ...for one new building already resolved (QN1): the pieces it is fitted with and the process it runs, and its building's rent struck on its own estimate. |
| 215 | 5 | `public static double forStanding(Game game, BuildingsTemplate t)` | The installed building's saved share of the last production's captured royalty. |
| 227 | 78 | **type** `static final class Production` | One market pass's production record, confined to that pass. |
| 228 | 1 | **type** `private record Ground(Resource resource, Deposit.FieldType type)` _(in LicenceFees.Production)_ |  |
| 229 | 1 | **type** `private record Bill(Sector owner, int id, long quantity, double amount)` _(in LicenceFees.Production)_ |  |
| 235 | 7 | `Production(Game game)` _(in LicenceFees.Production)_ |  |
| 244 | 3 | `private static boolean stockPhase(BuildingsTemplate t)` _(in LicenceFees.Production)_ | A conversion unit makes streams in the refinery's stock phase. |
| 248 | 4 | `private Ground ground(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ |  |
| 254 | 10 | `void before(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Flow mines/cutters remove the ground inside production. |
| 266 | 16 | `private double flowExtractionRate(ExtractionSector owner, BuildingsTemplate t)` _(in LicenceFees.Production)_ | The real typed lift divided among the works that made it, including a field's last dispatch. |
| 284 | 9 | `void made(Sector owner, boolean stock)` _(in LicenceFees.Production)_ | Called directly after this sector's output loop; capture all money inputs before any other producer or next-month choice. |
| 295 | 9 | `void finish()` _(in LicenceFees.Production)_ | Book immutable production bills once, into the pending imported-service ledger for the next strike. |
| 306 | 9 | `private static double stackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 316 | 28 | `private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner, double rate)` |  |
| 346 | 5 | `public static double paid(Game game)` | Read the settled flow from saved statements, never from end-of-month prices. |
| 353 | 5 | `public static double accrued(Game game)` | Production's accrued bill, already saved in the pending imported-service ledger. |

### THE CITY'S OWN WORKS (R2c-P) (lines 359-550)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 3 | `static boolean citysWork(Game game, BuildingsTemplate t)` | Whether this template is one of the city's own works, the stacks no sector owns (the bank's branch excepted), which pay their licences too (R2c-P). |
| 367 | 3 | `static double powerDelivered(UtilitiesHandler u, double nameplate)` | What the grid delivers with its nameplate moved, by UtilitiesHandler.updateEnergyRatio()'s rule: output, capped by what the city asks (R2c-P). |
| 372 | 5 | `static double waterDelivered(Game game, UtilitiesHandler u, double freshMove, double desalMove)` | What the water works deliver with the fresh and the sea nameplates moved, by UtilitiesHandler.waterOutput()'s rule, capped by what the city asks (R2c-P). |
| 384 | 7 | **type** `record Fitting(Map<String, Long> counts, long n, boolean standing, String[] ids)` | One city utility in a state of its fitting, the thing a bill is struck on (R2c-P, REV-R2c-P-1). |
| 386 | 1 | `static Fitting standing(Map<String, Long> counts, long quantity, String[] ids)` _(in LicenceFees.Fitting)_ | The stack of `quantity` buildings with these unfitted counts, the grid's nameplate moved from the stack as it stands to this state (R2c-P, REV-R2c-P-1). |
| 389 | 1 | `static Fitting added(Map<String, Long> counts, long units, String[] ids)` _(in LicenceFees.Fitting)_ | `units` new buildings, each with these unfitted counts, added to the city as it stands (R2c-P, REV-R2c-P-1). |
| 393 | 4 | `private static boolean utilityWork(Game game, BuildingsTemplate t)` | Whether this template is a city utility the licences are struck on: a city work in a city founded the 1900 way, electricity or water (R2c-P, REV-R2c-P-1). |
| 402 | 9 | `private static double served(Game game, BuildingsTemplate t, double move, double drop)` | The service delivered with the grid's or the works' nameplate moved by `move`, less that delivered with it moved by `move - drop`, in money at the tariff the planners price it at (Recipes.pricesOf()): surplus capacity... |
| 418 | 14 | `private static double entryGain(Game game, BuildingsTemplate t, Fitting f, String pieceId, double running)` | The month's public gain of one entry of a city utility in a state of its fitting, in money (R2c-P, REV-R2c-P-1): the service delivered with it less that delivered without it, at the planners' tariff. |
| 434 | 6 | `private static double lifeShare(String code, int atMonth, double i)` | The share of a life's weight (BuildAdvice.LIFE_MONTHS' annuity at the monthly rate i) that a patent's term left at atMonth covers (R2c-P, REV-R2c-P-1): 0 once it has ended, 1 while a whole life fits inside it. |
| 442 | 3 | `private static double annuity(double i, double months)` | What a month of 1 is worth over `months` at the monthly rate i, as BuildAdvice.lifeFactor() reads it: the level annuity's factor, the months themselves at no rate (R2c-P, REV-R2c-P-1). |
| 447 | 6 | `private static double fee(Game game, String code, int atMonth, double gain, boolean overLife, double[] rate)` | One entry's fee on its gain (R2c-P, REV-R2c-P-1): onGain() at atMonth, over a life weighted by lifeShare(); the life's rate (rate[0], NaN till asked) is read only for a fee above nothing. |
| 461 | 3 | `static double billOf(Game game, BuildingsTemplate t, Fitting f, double running, int atMonth, boolean overLife)` | The ONE bill of a city utility in a state of its fitting (R2c-P, REV-R2c-P-1): every patented entry once a code (the building's own, and each piece fitted on some building of the state and known at atMonth), SHARE of ... |
| 466 | 16 | `static double billOf(Game game, BuildingsTemplate t, Fitting f, double running, int atMonth, boolean overLife, Set<String> char...` | ...with the codes it struck a licence on, or looked at to (FB13): `charged` takes the building's own and each piece's. |
| 489 | 8 | `public static double publicGain(Game game, BuildingsStacks stack, String pieceId)` | The month's public gain of one entry on one standing stack of a city utility, in money (R2c-P): the service delivered with it less the service delivered without it, at the tariff the planners price it at (Recipes.pric... |
| 499 | 6 | `private static double standingBill(Game game, BuildingsStacks stack)` | One standing stack's bill at the month's state, as billOf() strikes it, before any finiteness guard (R2c-P, REV-R2c-P-1). |
| 507 | 5 | `public static double publicBillOf(Game game, BuildingsStacks stack)` | What the capture would bill this one stack this month (R2c-P, REV-R2c-P-1): its part of capturePublic(); 0 for no stack, a stack that is not a city utility's, or a city not founded the 1900 way. |
| 518 | 6 | `public static double capturePublic(Game game)` | The month's bill on the city's own patented works (R2c-P), struck once the utilities hold the month's output and prices: the sum of billOf() over the standing stacks at their own state (REV-R2c-P-1), each entry once a... |
| 530 | 7 | `public static double forNewWork(Game game, BuildingsTemplate t, double running)` | The planners' licence cost of ONE more building of a city utility (R2c-P, REV-R2c-P-1), added to its running cost: billOf() for one new building with the pieces it would be fitted with, today, over a life (each fee we... |
| 543 | 7 | `public static double forNewWork(Game game, BuildingsTemplate t, double running, long units, int monthsAhead)` | The planners' licence cost of `units` more buildings of a city utility, `monthsAhead` months from now (R2c-P, REV-R2c-P-1): billOf() for the units together beside the city as it stands (the bill of the whole order, no... |

### WHAT A BUILD CARD SAYS OF ITS LICENCE (FB13, R5-L) (lines 551-593)

| line | len | member | says |
|---:|---:|---|---|
| 554 | 1 | **type** `public record Patent(String code, int endsMonth)` | A patent a licence is struck on: its catalogue code and the month it ends (history's date and TERM_MONTHS). |
| 562 | 3 | **type** `public record Rent(double perMonth, List<Patent> patents)` | What one more of a building pays for its patents each month, and the patents that are running among those the bill is made of (FB13). |
| 570 | 23 | `public static Rent rentOnNew(Game game, BuildingsTemplate t)` | The rent on one more of a building (FB13): none in a city not founded the 1900 way, inside another licence pricing, and for a work that is neither a sector's building nor a city utility. |

