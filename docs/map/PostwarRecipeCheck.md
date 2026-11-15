# PostwarRecipeCheck.java - 720 lines · 19 methods · 0 constants · harnesses

`ham/citybuildersim/PostwarRecipeCheck.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The postwar engineering and business discoveries run their sourced earlier
> figures, compose in either order, retain saved decisions and leave default
> founding modern.
> WHY (R2e): knowing a catalogue date did not remove its modern productivity
> from an unfitted city. Every transition stands the affected template and
> crosses the real entry month. The identical fixture also runs on main's
> compiled model and data, using this branch's source JSON as its oracle.

**Uses:** [Good](Good.md) (53), [EquipmentFit](EquipmentFit.md) (31), [Game](Game.md) (26), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (25), [LicenceFees](LicenceFees.md) (22), [JobType](JobType.md) (21), [Recipes](Recipes.md) (21), [ResearchCatalog](ResearchCatalog.md) (20), [BuildingsStacks](BuildingsStacks.md) (13), [RecipeCheck](RecipeCheck.md) (12), [RecipeChange](RecipeChange.md) (11), [Equipment](Equipment.md) (8), [RecipeChanges](RecipeChanges.md) (5), [DataSave](DataSave.md) (4), [FittingCheck](FittingCheck.md) (3), [PolicyDates](PolicyDates.md) (2), [Recipe](Recipe.md) (2), [RefineryFlow](RefineryFlow.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [Founding](Founding.md) (1), [Sector](Sector.md) (1)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 695 | **type** `public final class PostwarRecipeCheck` | The postwar engineering and business discoveries run their sourced earlier figures, compose in either order, retain saved decisions and leave default founding modern. |
| 27 | 1 | `private PostwarRecipeCheck()` |  |
| 29 | 6 | `static void knownExcept(Game game, Set<String> except)` |  |
| 36 | 1 | `static boolean postwar(String code)` |  |
| 38 | 45 | `static void transitions(JsonObject data) throws Exception` |  |
| 84 | 31 | `static void pair(String name, String firstCode, String firstPiece, String secondCode, String secondPiece) throws Exception` |  |
| 116 | 47 | `static void stages() throws Exception` |  |
| 164 | 30 | `static void catalogueAndDefaults(JsonObject data) throws Exception` |  |
| 195 | 42 | `static void clockAndFlows() throws Exception` |  |
| 238 | 51 | `static void licenceCapture(String code) throws Exception` |  |
| 290 | 69 | `static void scopedStages() throws Exception` |  |
| 360 | 10 | `static double entryGain(Game game, BuildingsTemplate template, RecipeChange entry) throws Exception` |  |
| 371 | 5 | `static double actualValue(BuildingsTemplate template, Recipes.Prices prices)` |  |
| 377 | 50 | `static void everyScopedGain(JsonObject data) throws Exception` |  |
| 428 | 57 | `static void kilnAccounting() throws Exception` |  |
| 486 | 28 | `static void newCementPlant() throws Exception` |  |
| 516 | 11 | `static Set<String> proposedPieces(Game game, BuildingsTemplate template) throws Exception` |  |
| 528 | 4 | `static boolean paysAt(Equipment piece, BuildingsTemplate template, Game game, double gain, EquipmentFit.Verdict quote)` |  |
| 533 | 157 | `static void chosenSetAndRefit(JsonObject data) throws Exception` |  |
| 691 | 29 | `public static void main(String[] args) throws Exception` |  |

