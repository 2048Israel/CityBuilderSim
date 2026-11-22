# ModernEngineeringCheck.java - 447 lines · 24 methods · 5 constants · harnesses

`ham/citybuildersim/ModernEngineeringCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (53), [ResearchCatalog](ResearchCatalog.md) (15), [LicenceFees](LicenceFees.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (14), [RecipeCheck](RecipeCheck.md) (10), [Game](Game.md) (10), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (8), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (4), [Forms1900Check](Forms1900Check.md) (4), [Sector](Sector.md) (3), [Research](Research.md) (3), [PolicyDates](PolicyDates.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [GameFiles](GameFiles.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [RecipeChange](RecipeChange.md) (1), [Recipes](Recipes.md) (1), [SaveWriter](SaveWriter.md) (1), [DataSave](DataSave.md) (1), [GameVersion](GameVersion.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `ModernEngineeringCheck.OUT` | `System.out` |  |
| 40 | `ModernEngineeringCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 42 | `ModernEngineeringCheck.GM_FACTOR` | `1.22` | Study/root's deliberately rounded abstract result; body21% is disclosed in the data and credits. |
| 44 | `ModernEngineeringCheck.LEGACY_FORMAT` | `58` | The exact pre-R6e source-plan base, .181, wrote format58; this is a legacy fixture, not a new format decision. |
| 45 | `ModernEngineeringCheck.FARMS` | `List.of("Mixed Farm", "Grain Farm")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `private static int checks, fails` |  |
| 79 | `final Game game` |  |
| 80 | `final String label` |  |
| 81 | `final int factoryMonth` |  |
| 82 | `final int factoryAdvances` |  |
| 83 | `int advances, oldActive, crossings, adoptedActive` |  |
| 84 | `double oldUnits, adoptedUnits` |  |
| 85 | `boolean previousOldPositive` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 410 | **type** `public final class ModernEngineeringCheck` | E21's crop-only historical undo, its actual farm production and patent bill, and the saved physical state on either side of the discovery. |
| 48 | 1 | `private ModernEngineeringCheck()` |  |
| 50 | 5 | `static void check(String label, boolean ok, Object detail)` |  |
| 56 | 4 | `static boolean near(double a, double b)` |  |
| 61 | 3 | `static void near(String label, double actual, double expected)` |  |
| 65 | 1 | `static int date()` |  |
| 67 | 10 | `static void quietHelpers()` |  |
| 78 | 49 | **type** `static final class Fixture` |  |
| 87 | 9 | `Fixture(String label, boolean historical)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 97 | 1 | `void jump(int month) throws Exception` _(in ModernEngineeringCheck.Fixture)_ |  |
| 99 | 27 | `void advance(BuildingsTemplate farm)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 128 | 6 | `static void knowledge(Game game, Set<String> absent)` |  |
| 135 | 1 | `static void apply(Game game)` |  |
| 137 | 7 | `static void advanceLoaded(Game game, String label)` |  |
| 145 | 3 | `static BuildingsTemplate farm(Game game, String name)` |  |
| 149 | 7 | `static void quantity(Game game, BuildingsTemplate t, int wanted)` |  |
| 157 | 6 | `static BuildingsTemplate isolate(Fixture fixture, String name, int wanted)` |  |
| 164 | 4 | `static boolean missing(Game game, BuildingsTemplate t)` |  |
| 169 | 5 | `static Map<String, Double> nonCrop(BuildingsTemplate t)` |  |
| 175 | 39 | `static void catalogueAndDefaults() throws Exception` |  |
| 215 | 53 | `static void composition() throws Exception` |  |
| 269 | 26 | `static void actualProductionAndFiringCounts() throws Exception` |  |
| 296 | 27 | `static void localFind() throws Exception` |  |
| 324 | 58 | `static void licenceCapture() throws Exception` |  |
| 383 | 52 | `static void savedStates() throws Exception` |  |
| 436 | 11 | `public static void main(String[] args) throws Exception` |  |

