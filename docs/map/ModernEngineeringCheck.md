# ModernEngineeringCheck.java - 430 lines · 24 methods · 5 constants · harnesses

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
> The village factory advances no month (unlike LongPlaytest.probeCity).
> Every subsequent normal player-month advance is counted from its actual
> before/after clock, separately from explicit date jumps. Nothing here is
> an observation waiver: a missing positive dispatch or transition fails.

**Uses:** [Good](Good.md) (47), [LicenceFees](LicenceFees.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (14), [ResearchCatalog](ResearchCatalog.md) (13), [RecipeCheck](RecipeCheck.md) (11), [Game](Game.md) (10), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (8), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (4), [Forms1900Check](Forms1900Check.md) (4), [Sector](Sector.md) (3), [Research](Research.md) (3), [PolicyDates](PolicyDates.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [GameFiles](GameFiles.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [RecipeChange](RecipeChange.md) (1), [Recipes](Recipes.md) (1), [SaveWriter](SaveWriter.md) (1), [DataSave](DataSave.md) (1), [GameVersion](GameVersion.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `ModernEngineeringCheck.OUT` | `System.out` |  |
| 39 | `ModernEngineeringCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 41 | `ModernEngineeringCheck.GM_FACTOR` | `1.22` | Study/root's deliberately rounded abstract result; body21% is disclosed in the data and credits. |
| 43 | `ModernEngineeringCheck.LEGACY_FORMAT` | `58` | The exact pre-R6e source-plan base, .181, wrote format58; this is a legacy fixture, not a new format decision. |
| 44 | `ModernEngineeringCheck.FARMS` | `List.of("Mixed Farm", "Grain Farm")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private static int checks, fails` |  |
| 78 | `final Game game` |  |
| 79 | `final String label` |  |
| 80 | `final int factoryMonth` |  |
| 81 | `int advances, oldActive, crossings, adoptedActive` |  |
| 82 | `double oldUnits, adoptedUnits` |  |
| 83 | `boolean previousOldPositive` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 394 | **type** `public final class ModernEngineeringCheck` | E21's crop-only historical undo, its actual farm production and patent bill, and the saved physical state on either side of the discovery. |
| 47 | 1 | `private ModernEngineeringCheck()` |  |
| 49 | 5 | `static void check(String label, boolean ok, Object detail)` |  |
| 55 | 4 | `static boolean near(double a, double b)` |  |
| 60 | 3 | `static void near(String label, double actual, double expected)` |  |
| 64 | 1 | `static int date()` |  |
| 66 | 10 | `static void quietHelpers()` |  |
| 77 | 45 | **type** `static final class Fixture` |  |
| 85 | 8 | `Fixture(String label, boolean historical)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 94 | 1 | `void jump(int month) throws Exception` _(in ModernEngineeringCheck.Fixture)_ |  |
| 96 | 25 | `void advance(BuildingsTemplate farm)` _(in ModernEngineeringCheck.Fixture)_ |  |
| 123 | 6 | `static void knowledge(Game game, Set<String> absent)` |  |
| 130 | 1 | `static void apply(Game game)` |  |
| 132 | 7 | `static void advanceLoaded(Game game, String label)` |  |
| 140 | 3 | `static BuildingsTemplate farm(Game game, String name)` |  |
| 144 | 7 | `static void quantity(Game game, BuildingsTemplate t, int wanted)` |  |
| 152 | 6 | `static BuildingsTemplate isolate(Fixture fixture, String name, int wanted)` |  |
| 159 | 4 | `static boolean missing(Game game, BuildingsTemplate t)` |  |
| 164 | 5 | `static Map<String, Double> nonCrop(BuildingsTemplate t)` |  |
| 170 | 39 | `static void catalogueAndDefaults() throws Exception` |  |
| 210 | 50 | `static void composition() throws Exception` |  |
| 261 | 25 | `static void actualProductionAndFiringCounts() throws Exception` |  |
| 287 | 27 | `static void localFind() throws Exception` |  |
| 315 | 50 | `static void licenceCapture() throws Exception` |  |
| 366 | 52 | `static void savedStates() throws Exception` |  |
| 419 | 11 | `public static void main(String[] args) throws Exception` |  |

