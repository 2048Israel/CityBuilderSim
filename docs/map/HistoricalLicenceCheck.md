# HistoricalLicenceCheck.java - 590 lines · 20 methods · 0 constants · harnesses

`ham/citybuildersim/HistoricalLicenceCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> R2d's independent discoveries meet R2c's captured patent service.
> WHY (R2d): a net fitting estimate could charge for a patent whose real
> production bill stayed zero. Staffed works and rail invoices must pay,
> preserve their captured bills, and close the same month's money audit.

**Uses:** [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (80), [LicenceFees](LicenceFees.md) (61), [Good](Good.md) (49), [Game](Game.md) (31), [ResearchCatalog](ResearchCatalog.md) (17), [RecipeChanges](RecipeChanges.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (14), [ResearchCheck](ResearchCheck.md) (12), [FittingCheck](FittingCheck.md) (11), [EquipmentFit](EquipmentFit.md) (8), [SectorState](SectorState.md) (8), [LicenceFeesCheck](LicenceFeesCheck.md) (7), [Recipes](Recipes.md) (7), [RecipeCheck](RecipeCheck.md) (6), [Founding](Founding.md) (6), [MoneyAudit](MoneyAudit.md) (6), [RefineryFlow](RefineryFlow.md) (5), [BuildingsStacks](BuildingsStacks.md) (5), [Resource](Resource.md) (4), [Freight](Freight.md) (4), [EquipmentCheck](EquipmentCheck.md) (4), [Forms1900Check](Forms1900Check.md) (4), [Equipment](Equipment.md) (3), [DataSave](DataSave.md) (3), [MaterialsCheck](MaterialsCheck.md) (2), [Trade](Trade.md) (2), [NewGameCheck](NewGameCheck.md) (2), [LandManager](LandManager.md) (1), [Refining](Refining.md) (1), [BuildAdvice](BuildAdvice.md) (1)... and 4 more

**Used by (8):** [FitoutCheck](FitoutCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [NewUnitCheck](NewUnitCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `private static int checks, fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 577 | **type** `public final class HistoricalLicenceCheck` | R2d's independent discoveries meet R2c's captured patent service. |
| 16 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 21 | 3 | `static boolean near(double a, double b)` |  |
| 24 | 5 | `static Game town(String label, boolean historical)` |  |
| 29 | 10 | `static BuildingsTemplate stand(Game g, String name, int count)` |  |
| 39 | 27 | `static void tools() throws Exception` |  |
| 66 | 38 | `static void copper() throws Exception` |  |
| 104 | 53 | `static void cracker(boolean catalytic, boolean reject) throws Exception` |  |
| 157 | 31 | `static void publicPieces() throws Exception` |  |
| 189 | 50 | `static void demolishedRail() throws Exception` |  |
| 240 | 38 | `static void lastRailClosed() throws Exception` |  |
| 279 | 55 | `static void lastUnfittedRailClosed() throws Exception` |  |
| 335 | 46 | `static void railEligibility() throws Exception` |  |
| 382 | 7 | `static double railMemo(Game g)` |  |
| 390 | 100 | `static void rail() throws Exception` |  |
| 492 | 24 | `static Game closedSteamInvoice(String label) throws Exception` | A real steam-service period, then its last depot closed before the invoice. |
| 517 | 3 | `static double savedSteam(Game g)` |  |
| 521 | 11 | `static Game loadSteamInvoice(Game from)` |  |
| 534 | 18 | `static boolean saveChecks() throws Exception` | SaveFileCheck: the full writer/loader retains a steam period after its asset is gone. |
| 554 | 26 | `static boolean readChecks() throws Exception` | ReadPathCheck: live and loaded reads cannot reconstruct the saved period from no remaining assets. |
| 581 | 9 | `public static void main(String[] args) throws Exception` |  |

