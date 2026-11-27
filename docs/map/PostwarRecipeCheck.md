# PostwarRecipeCheck.java - 944 lines · 21 methods · 1 constants · harnesses

`ham/citybuildersim/PostwarRecipeCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The postwar engineering and business discoveries run their sourced earlier
> figures, compose in either order, retain saved decisions and leave default
> founding modern.
> WHY (R2e): knowing a catalogue date did not remove its modern productivity
> from an unfitted city. Every transition stands the affected template and
> crosses the real entry month. The identical fixture also runs on main's
> compiled model and data, using this branch's source JSON as its oracle.
> R2e-PE4: the Cement Plant's PE4 is defined against its rotary kiln, so a
> plant with the rotary kiln and without PE4 burns the study's 30% and every
> kiln step saves on the one before; sections 7-10 and 13 hold it, and each
> fails on the earlier calibration (rotary without PE4 at 6,160 t, PE4 a loss).

**Uses:** [Good](Good.md) (85), [EquipmentFit](EquipmentFit.md) (49), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (43), [LicenceFees](LicenceFees.md) (37), [Game](Game.md) (36), [BuildingsTemplate](BuildingsTemplate.md) (30), [Recipes](Recipes.md) (30), [ResearchCatalog](ResearchCatalog.md) (24), [JobType](JobType.md) (22), [BuildingsStacks](BuildingsStacks.md) (19), [Equipment](Equipment.md) (13), [RecipeCheck](RecipeCheck.md) (12), [RecipeChange](RecipeChange.md) (11), [RecipeChanges](RecipeChanges.md) (5), [DataSave](DataSave.md) (4), [FittingCheck](FittingCheck.md) (4), [Sector](Sector.md) (4), [Recipe](Recipe.md) (3), [MaterialsCheck](MaterialsCheck.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [PolicyDates](PolicyDates.md) (2), [RefineryFlow](RefineryFlow.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 840 | `PostwarRecipeCheck.OLD_PLANT_MORE_HOUSES` | `250` | The Houses stood beside the village's 400 for section 13's crews (RES3): a door holds one household, so the village's 400 Houses hold 1,534 people where main's held 2,139, and the builders' fill (0.19) leaves the kiln... |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 915 | **type** `public final class PostwarRecipeCheck` | The postwar engineering and business discoveries run their sourced earlier figures, compose in either order, retain saved decisions and leave default founding modern. |
| 31 | 1 | `private PostwarRecipeCheck()` |  |
| 33 | 6 | `static void knownExcept(Game game, Set<String> except)` |  |
| 40 | 1 | `static boolean postwar(String code)` |  |
| 42 | 45 | `static void transitions(JsonObject data) throws Exception` |  |
| 88 | 31 | `static void pair(String name, String firstCode, String firstPiece, String secondCode, String secondPiece) throws Exception` |  |
| 120 | 47 | `static void stages() throws Exception` |  |
| 168 | 30 | `static void catalogueAndDefaults(JsonObject data) throws Exception` |  |
| 199 | 42 | `static void clockAndFlows() throws Exception` |  |
| 242 | 51 | `static void licenceCapture(String code) throws Exception` |  |
| 294 | 80 | `static void scopedStages() throws Exception` |  |
| 375 | 10 | `static double entryGain(Game game, BuildingsTemplate template, RecipeChange entry) throws Exception` |  |
| 386 | 5 | `static double actualValue(BuildingsTemplate template, Recipes.Prices prices)` |  |
| 392 | 55 | `static void everyScopedGain(JsonObject data) throws Exception` |  |
| 448 | 66 | `static void kilnAccounting() throws Exception` |  |
| 515 | 29 | `static void newCementPlant() throws Exception` |  |
| 546 | 11 | `static Set<String> proposedPieces(Game game, BuildingsTemplate template) throws Exception` |  |
| 558 | 4 | `static boolean paysAt(Equipment piece, BuildingsTemplate template, Game game, double gain, EquipmentFit.Verdict quote)` |  |
| 563 | 158 | `static void chosenSetAndRefit(JsonObject data) throws Exception` |  |
| 722 | 112 | `static void wholeNetAndSavedProcess() throws Exception` |  |
| 842 | 70 | `static void oldPlantRefits() throws Exception` |  |
| 913 | 31 | `public static void main(String[] args) throws Exception` |  |

