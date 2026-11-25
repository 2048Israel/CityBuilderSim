# ModernRowsCheck.java - 868 lines · 24 methods · 9 constants · harnesses

`ham/citybuildersim/ModernRowsCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> R6f's three research rows and R6e-rest's one, each undone in a city founded the 1900 way until it is known:
> robot-assisted surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10), Basel
> III's conservation buffer (L8) and the Desalination Plant's electricity before the pressure exchanger (E22),
> on both sides of their months, in a default city and across a save.
> <p>
> WHY (R6f): a row in research.json alone changes nothing a city does. Each of these three needed a figure
> the model reads by the city's own knowledge (a stays factor, a recipe change, a bank's floor), and B10's
> undo is a recipe choice whose id is saved: a format-63 save lacked the flag, so a held city would have
> loaded modern. A city founded the default way knows every row from its first month and must be unchanged.
> WHY (R6e-rest): E22 is the same kind of undo on a figure the water's books read, and a recipe change that
> moved a template's electricity without moving the sea works' draw would charge a held city for power the
> plant no longer draws (or not charge it for what it does); the draw is read through the model's own getters.
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
> 4. E22: the catalogue's row (1997, June placeholder, equipment, needs E6, fee, the rule's work) and buildings.json's
>    one undo, carried by the Desalination Plant's pressure exchanger, on its electricity alone and the sourced
>    arithmetic's kW (Stover 2010's 3.49 to 2.68 kWh a m3 over the DOE's 2.70 kWh a m3 RO step); a 1900 city's plant
>    that finishes before June 1997 is recorded as lacking the exchanger (the stack's unfitted count) and runs
>    Math.round(10,880 + undo) = 13,454 kW with every other figure today's, and still does in June 1997 (knowing a
>    piece fits none, by a recipe call and by a real month); a plant that finishes after it, where the exchanger pays,
>    comes fitted at 10,880; the sea works' draw the water's books pay is the plants' count times the template's
>    powerBought() after a real month, more than today's while a plant lacks the exchanger; no sector fits a city
>    work's piece, and the player's Fit (Game.fitForCity()) does, after which the plant and the draw are today's; the
>    imported exchanger's public gain inside its term is the power it saves at the commerce price, and the plant's licence
>    bill SHARE of it (it serves no more water; LicenceFees.entryGain() adds the saving); a
>    default city runs 10,880 across the month; a held city's save loads the unfitted count and the draw at once and
>    the next month keeps them; a plant with no record of the count (a format-65 save's) is read as fitted from June 1997.
> <p>
> Every city is the existing fixtures': the licence factory's (three months played) or the recipe
> checks' on GameFiles.scratch, and every save and load is slot 10 of the fixture's own scratch folder.
> The clock is set by HistoricalRecipeCheck.at(), which the month hears only on the next step.

**Uses:** [Bank](Bank.md) (40), [Game](Game.md) (39), [Sickness](Sickness.md) (39), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (39), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (30), [RecipeCheck](RecipeCheck.md) (28), [BuildingsTemplate](BuildingsTemplate.md) (26), [ResearchCatalog](ResearchCatalog.md) (23), [JobType](JobType.md) (21), [GameFiles](GameFiles.md) (9), [Research](Research.md) (8), [LicenceFees](LicenceFees.md) (7), [EquipmentFit](EquipmentFit.md) (6), [Forms1900Check](Forms1900Check.md) (5), [MedicalRows](MedicalRows.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [FittingCheck](FittingCheck.md) (4), [PolicyDates](PolicyDates.md) (4), [Good](Good.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [DiscoveryCheck](DiscoveryCheck.md) (2), [CareType](CareType.md) (2), [BuildingManager](BuildingManager.md) (2), [RecipeChange](RecipeChange.md) (2), [BankCheck](BankCheck.md) (1), [HydroSites](HydroSites.md) (1), [Sector](Sector.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Equipment](Equipment.md) (1)

## Sections

| line | section |
|---:|---|
| 107 | · 1. M12 |
| 282 | · 2. B10 |
| 410 | · 3. L8 |
| 559 | · 4. E22 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `ModernRowsCheck.OUT` | `System.out` |  |
| 69 | `ModernRowsCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 71 | `ModernRowsCheck.AGENTS_GAIN` | `.14` | Brynjolfsson, Li and Raymond's 14% more issues resolved an hour, the ratio buildings.json's B10 undo gives the agents before the assistant. |
| 73 | `ModernRowsCheck.COVERAGES` | `{.25,.5, 1 }` | The general-care coverages (0, 1] the recovery is read at. |
| 562 | `ModernRowsCheck.PELTON_KWH` | `3.49` | Stover (Desalination and Water Treatment 13, 2010, pp38-39): Las Palmas III's SWRO process drew 3.49 kWh a m3 with its Pelton turbines, before the pressure-exchanger retrofit. |
| 564 | `ModernRowsCheck.PX_KWH` | `2.68` | Stover (2010, pp38-39): the same SWRO process drew 2.68 kWh a m3 after the pressure exchangers replaced the turbines. |
| 566 | `ModernRowsCheck.RO_STEP_KWH` | `2.70` | The US DOE's Seawater Desalination Bandwidth Study (2017, pp16 and 24): the RO step with energy recovery (Carlsbad) draws 2.70 kWh a m3, the part of the plant's draw the exchanger changes. |
| 568 | `ModernRowsCheck.LITRES_A_WATER_UNIT` | `37_854.11784` | The game's unit of water in litres: 10,000 US gallons of 3.785411784 litres each, the gallon's exact definition (UtilitiesHandler's units; SulphideLeachCheck's 37,854 is this rounded). |
| 584 | `ModernRowsCheck.EXCHANGER` | `"pressure-exchanger"` | The id of the pressure exchanger in buildings.json, the piece E22's change on the Desalination Plant is carried by (a piece's change is a count of the stack's unfitted, not a recipe flag). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `private static int checks, fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 802 | **type** `public final class ModernRowsCheck` | R6f's three research rows and R6e-rest's one, each undone in a city founded the 1900 way until it is known: robot-assisted surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10), Basel I... |
| 76 | 1 | `private ModernRowsCheck()` |  |
| 78 | 5 | `static void check(String label, boolean ok, Object detail)` |  |
| 84 | 4 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 89 | 10 | `static void quietHelpers()` |  |
| 101 | 5 | `static void step(Game g, String label)` | One real month of the player's button, said by what it did. |

### 1. M12 (lines 107-281)

| line | len | member | says |
|---:|---:|---|---|
| 109 | 125 | `static void robotSurgery() throws Exception` |  |
| 240 | 41 | `static void medicineBeforeM1AndM2() throws Exception` | The M12 group's month has M1 and M2 known, where the long recovery is 1 and the death lift 0, the fresh defaults, so it cannot tell a load that derives them from one that does not. |

### 2. B10 (lines 282-409)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 6 | `static JsonObject contactCentre() throws Exception` | The Contact Centre's definition in buildings.json, read as the file has it. |
| 292 | 5 | `static JsonObject b10(JsonObject centre)` |  |
| 299 | 4 | `static boolean missing(Game g, BuildingsTemplate t)` | Whether the template's saved recipe choice carries B10's missing flag (the "~B10" of its id). |
| 305 | 7 | `static BuildingsTemplate centreAt(Game g, int month, boolean heldBack) throws Exception` | A Contact Centre stood in the city at the month, its choice struck and applied, B10 the only discovery unknown when `heldBack`. |
| 313 | 5 | `static Map<String, Double> restOf(BuildingsTemplate t)` |  |
| 319 | 90 | `static void agents() throws Exception` |  |

### 3. L8 (lines 410-558)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 146 | `static void basel3() throws Exception` |  |

### 4. E22 (lines 559-868)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 6 | `static JsonObject building(String name) throws Exception` | One building's definition in buildings.json, read as the file has it. |
| 579 | 3 | `static double undoKw(double unitsAMonth)` | The kW the exchanger's step adds to a works of this many water units a month: the RO step's kWh a m3 over the plant's m3 a month, over the kWh a kW-month, times the step's rise from PX_KWH back to PELTON_KWH. |
| 587 | 4 | `static long unfitted(Game g, BuildingsTemplate t)` | How many of the stack's plants lack the pressure exchanger (BuildingsStacks.getUnfitted()). |
| 597 | 8 | `static BuildingsTemplate plantAt(Game g, int month, boolean heldBack) throws Exception` | A Desalination Plant that finishes in the city at the month: placed as the fixtures place a village (fitted), then the model's own finish rule said over it (Game.fitWhatFinished(): a piece not known, or not paying, at... |
| 607 | 5 | `static Map<String, Double> plantRest(BuildingsTemplate t)` | Every figure of the plant but its electricity, the one the exchanger changes. |
| 614 | 4 | `static double steamShare(Game g, BuildingsTemplate t)` | The share of the plant's sector the grid does not draw for (ServicesManager.gridDraw()'s steam share): 0 for a plant in no sector. |
| 620 | 4 | `static double seaKw(Game g)` | The sea works' draw the water's books pay at the last update (UtilitiesHandler.getLatestWaterDraw()), NaN before any update. |
| 626 | 4 | `static double standing(Game g, BuildingsTemplate t)` | How many plants of this template stand in the city. |
| 631 | 227 | `static void exchanger() throws Exception` |  |
| 859 | 9 | `public static void main(String[] args) throws Exception` |  |

