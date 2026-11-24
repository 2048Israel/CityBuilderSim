# ModernEngineeringCheck.java - 594 lines · 28 methods · 6 constants · harnesses

`ham/citybuildersim/ModernEngineeringCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> E21's crop-only historical undo, its actual farm production and patent bill,
> and the saved physical state on either side of the discovery.
> <p>
> WHY (R6e): a dated catalogue entry alone would leave old farms modern; a
> saved recipe without the new missing-change flag could do the same on load.
> The 22% is the approved crop-mix estimate, not a universal measured effect.
> <p>
> What this proves:
> 1. Only the two farms gain the sourced signed CROPS undo; modern anchors,
>    all other figures, catalogue dates, lag and work rule remain exact.
> 2. PE20/E10/E21 compose independently in either discovery order, at zero,
>    one and mixed quantities, with no per-building output rounding.
> 3. Both farms actually dispatch positive crops before and after history's
>    month, and native research work can find E21 earlier but not before science.
> 4. Imported E21 pays the native patent share on actual partial production;
>    local knowledge/expiry exempt it and later state cannot erase its capture.
> 5. Current saved choices restore immediately and across the next month;
>    an exact .181-shaped missing-flag record must be correct or refused.
> <p>
> The established licence-fixture factory plays three startup months; its
> actual returned clock and advance count are recorded, never a probeCity age.
> Every subsequent normal player-month advance is counted from its actual
> before/after clock, separately from explicit date jumps. Nothing here is
> an observation waiver: a missing positive dispatch or transition fails.

**Uses:** [Good](Good.md) (85), [LicenceFees](LicenceFees.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (19), [ResearchCatalog](ResearchCatalog.md) (16), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (15), [Game](Game.md) (12), [RecipeCheck](RecipeCheck.md) (11), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (4), [Forms1900Check](Forms1900Check.md) (4), [Sector](Sector.md) (4), [SpreadPlanner](SpreadPlanner.md) (4), [Research](Research.md) (3), [SaveWriter](SaveWriter.md) (3), [PolicyDates](PolicyDates.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [GameFiles](GameFiles.md) (2), [GameVersion](GameVersion.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [RecipeChange](RecipeChange.md) (1), [Recipes](Recipes.md) (1), [Equipment](Equipment.md) (1), [RecipeChanges](RecipeChanges.md) (1), [ReadPathCheck](ReadPathCheck.md) (1), [DataSave](DataSave.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [BuildingManager](BuildingManager.md) (1), [SaveHeader](SaveHeader.md) (1)

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `ModernEngineeringCheck.OUT` | `System.out` |  |
| 40 | `ModernEngineeringCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 42 | `ModernEngineeringCheck.GM_FACTOR` | `1.22` | Study/root's deliberately rounded abstract result; body21% is disclosed in the data and credits. |
| 44 | `ModernEngineeringCheck.LEGACY_FORMAT` | `58` | The exact pre-R6e source-plan base, .181, wrote format58; this is a legacy fixture, not a new format decision. |
| 46 | `ModernEngineeringCheck.PRE_E21_FORMAT` | `59` | HT1's shipped .184 still predates E21's physical recipe flag. |
| 47 | `ModernEngineeringCheck.FARMS` | `List.of("Mixed Farm", "Grain Farm")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private static int checks, fails` |  |
| 81 | `final Game game` |  |
| 82 | `final String label` |  |
| 83 | `final int factoryMonth` |  |
| 84 | `final int factoryAdvances` |  |
| 85 | `int advances, oldActive, crossings, adoptedActive` |  |
| 86 | `double oldUnits, adoptedUnits` |  |
| 87 | `boolean previousOldPositive` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 557 | **type** `public final class ModernEngineeringCheck` | E21's crop-only historical undo, its actual farm production and patent bill, and the saved physical state on either side of the discovery. |
| 50 | 1 | `private ModernEngineeringCheck()` |  |
| 52 | 5 | `static void check(String label, boolean ok, Object detail)` |  |
| 58 | 4 | `static boolean near(double a, double b)` |  |
| 63 | 3 | `static void near(String label, double actual, double expected)` |  |
| 67 | 1 | `static int date()` |  |
| 69 | 10 | `static void quietHelpers()` |  |
| 80 | 49 | **type** `static final class Fixture` |  |
| 89 | 9 | `Fixture(String label, boolean historical)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 99 | 1 | `void jump(int month) throws Exception` _(in ModernEngineeringCheck.Fixture)_ |  |
| 101 | 27 | `void advance(BuildingsTemplate farm)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 130 | 6 | `static void knowledge(Game game, Set<String> absent)` |  |
| 137 | 1 | `static void apply(Game game)` |  |
| 139 | 7 | `static void advanceLoaded(Game game, String label)` |  |
| 147 | 3 | `static BuildingsTemplate farm(Game game, String name)` |  |
| 151 | 7 | `static void quantity(Game game, BuildingsTemplate t, int wanted)` |  |
| 159 | 6 | `static BuildingsTemplate isolate(Fixture fixture, String name, int wanted)` |  |
| 166 | 4 | `static boolean missing(Game game, BuildingsTemplate t)` |  |
| 171 | 5 | `static Map<String, Double> nonCrop(BuildingsTemplate t)` |  |
| 177 | 39 | `static void catalogueAndDefaults() throws Exception` |  |
| 217 | 53 | `static void composition() throws Exception` |  |
| 271 | 26 | `static void actualProductionAndFiringCounts() throws Exception` |  |
| 298 | 27 | `static void localFind() throws Exception` |  |
| 326 | 58 | `static void licenceCapture() throws Exception` |  |
| 385 | 90 | `static void mixedFleetLicenceCapture() throws Exception` |  |
| 476 | 10 | `static void physicalReaders(Game game, String name)` |  |
| 487 | 78 | `static void savedStates(boolean readPaths) throws Exception` |  |
| 567 | 6 | `static boolean saveChecks() throws Exception` | Native current-save twins and both actual pre-E21 format refusals, shared by SaveFileCheck. |
| 575 | 6 | `static boolean readChecks() throws Exception` | The same real saves/refusals with repeated screen reads and refusal into a running twin. |
| 582 | 12 | `public static void main(String[] args) throws Exception` |  |

