# ModernRowsCheck.java - 550 lines · 15 methods · 4 constants · harnesses

`ham/citybuildersim/ModernRowsCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> R6f's three research rows, each undone in a city founded the 1900 way until it is known: robot-assisted
> surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10) and Basel
> III's conservation buffer (L8), on both sides of their months, in a default city and across a save.
> <p>
> WHY (R6f): a row in research.json alone changes nothing a city does. Each of these three needed a figure
> the model reads by the city's own knowledge (a stays factor, a recipe change, a bank's floor), and B10's
> undo is a recipe choice whose id is saved: a format-63 save lacked the flag, so a held city would have
> loaded modern. A city founded the default way knows every row from its first month and must be unchanged.
> <p>
> What this proves:
> 1. M12: the stays factor is Sickness.STAYS_BEFORE_M12 alone the month before July 2000 and 1 from it, in
>    the unit and in a played 1900 city (recovery too); a default city reads 1; a find ahead of the world
>    lifts it early; a 1900 city held before M12 and saved loads the medicine it ran (the stays factor, the
>    long recovery, the death lift and the recovery at every coverage) at once, before any month, since
>    the medicine is derived and not saved (REV-R6f P2); a city whose Medical School's faculty complete M12
>    in a played month ahead of history runs the stays factor 1 in that same month, not the next, and a
>    reload of that month reads the same; and a 1900 city played before M1 and M2 are known, whose general
>    care covers part of its people, loads the long recovery and the death lift (a figure times that
>    coverage) it ran at once, which a month with both known cannot tell (they are 1 and 0 there; REV-R6f P2).
> 2. B10: the Contact Centre's NO_DIPLOMA posts are today's plus buildings.json's undo, rounded once, with
>    every other figure and the output unchanged, before November 2020 and today's from it (by a recipe
>    call and by a real month); a default city runs today's; a held city's save loads the same posts and
>    "~B10" id at once, and the next month's choice keeps it.
> 3. L8: the bank's floor is Bank.CONSERVATION_BUFFER or Bank.BUFFER_BEFORE_L8 by the rule it hears, which
>    moves its target, its reason and its lending limit; a 1900 city hears it by the month (founding,
>    each month, a load) and a default city always the buffer; a city whose Law School's faculty complete L8
>    in a played month ahead of history hears it in that same month, not the next, and a reload of that
>    month hears the same (REV-R6f P1).
> <p>
> Every city is the existing fixtures': the licence factory's (three months played) or the recipe
> checks' on GameFiles.scratch, and every save and load is slot 10 of the fixture's own scratch folder.
> The clock is set by HistoricalRecipeCheck.at(), which the month hears only on the next step.

**Uses:** [Bank](Bank.md) (40), [Sickness](Sickness.md) (39), [Game](Game.md) (25), [RecipeCheck](RecipeCheck.md) (21), [JobType](JobType.md) (20), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (19), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (18), [ResearchCatalog](ResearchCatalog.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (9), [Research](Research.md) (8), [GameFiles](GameFiles.md) (7), [MedicalRows](MedicalRows.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (4), [Forms1900Check](Forms1900Check.md) (4), [PolicyDates](PolicyDates.md) (3), [DiscoveryCheck](DiscoveryCheck.md) (2), [CareType](CareType.md) (2), [Good](Good.md) (2), [BankCheck](BankCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 90 | · 1. M12 |
| 265 | · 2. B10 |
| 393 | · 3. L8 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `ModernRowsCheck.OUT` | `System.out` |  |
| 52 | `ModernRowsCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 54 | `ModernRowsCheck.AGENTS_GAIN` | `.14` | Brynjolfsson, Li and Raymond's 14% more issues resolved an hour, the ratio buildings.json's B10 undo gives the agents before the assistant. |
| 56 | `ModernRowsCheck.COVERAGES` | `{.25,.5, 1 }` | The general-care coverages (0, 1] the recovery is read at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private static int checks, fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 501 | **type** `public final class ModernRowsCheck` | R6f's three research rows, each undone in a city founded the 1900 way until it is known: robot-assisted surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10) and Basel III's conservatio... |
| 59 | 1 | `private ModernRowsCheck()` |  |
| 61 | 5 | `static void check(String label, boolean ok, Object detail)` |  |
| 67 | 4 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 72 | 10 | `static void quietHelpers()` |  |
| 84 | 5 | `static void step(Game g, String label)` | One real month of the player's button, said by what it did. |

### 1. M12 (lines 90-264)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 125 | `static void robotSurgery() throws Exception` |  |
| 223 | 41 | `static void medicineBeforeM1AndM2() throws Exception` | The M12 group's month has M1 and M2 known, where the long recovery is 1 and the death lift 0, the fresh defaults, so it cannot tell a load that derives them from one that does not. |

### 2. B10 (lines 265-392)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 6 | `static JsonObject contactCentre() throws Exception` | The Contact Centre's definition in buildings.json, read as the file has it. |
| 275 | 5 | `static JsonObject b10(JsonObject centre)` |  |
| 282 | 4 | `static boolean missing(Game g, BuildingsTemplate t)` | Whether the template's saved recipe choice carries B10's missing flag (the "~B10" of its id). |
| 288 | 7 | `static BuildingsTemplate centreAt(Game g, int month, boolean heldBack) throws Exception` | A Contact Centre stood in the city at the month, its choice struck and applied, B10 the only discovery unknown when `heldBack`. |
| 296 | 5 | `static Map<String, Double> restOf(BuildingsTemplate t)` |  |
| 302 | 90 | `static void agents() throws Exception` |  |

### 3. L8 (lines 393-550)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 146 | `static void basel3() throws Exception` |  |
| 542 | 8 | `public static void main(String[] args) throws Exception` |  |

