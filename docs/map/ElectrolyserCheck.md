# ElectrolyserCheck.java - 391 lines · 21 methods · 3 constants · harnesses

`ham/citybuildersim/ElectrolyserCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> Electrolysis at 46 kWh a kilogram (R6d, FE10: the US energy department's target for a PEM system, 55 today; projected 2034, window 2031-2037): the Hydrogen
> Steel Works' piece of equipment, the 46 kWh electrolyser, and what a works that has it draws from the grid.
> 
> WHY. The row changes one figure of FE4's works, the grid's power a tonne of steel, and nothing else, so everything it can get wrong is a number: which
> works carry the piece (EquipmentForms counts them from the stacks), how much each saves (the chemistry's 54 kg of hydrogen a tonne of iron at 9 kWh less
> each), and where that saving is taken off (HeavyIndustry's draw, which EconomyManager sets each month from the templates and which has to keep the saving
> across that setting). It must also change nothing where the piece does not exist: a city founded today's way never knows a near-future row, and a 1900
> city before the row's date has no works with it. Each figure is held to the model's own constants (HeavyIndustry.HYDROGEN_KG_A_TONNE,
> ELECTROLYSIS_KWH_A_KG and ELECTROLYSIS_KWH_A_KG_FE10, HydroSites.KWH_PER_UNIT, LicenceFees.SHARE), never a number typed here, and each condition is
> caused from both sides: the month before and the month of the row's date, a works with the piece and a works without it, power dear and power cheap, a
> city founded the 1900 way and one founded today's.
> 
> What this has to prove:
> 
>   1. THE ROW AND ITS DIALS: the works carries the "electrolyser" piece, since FE10 and ending ELECTROLYSERS; the saving a tonne is the chemistry's kilograms
>      at the nine kilowatt-hours the target takes off each, 0.486 MWh, less than the works' whole 3.48; what a works saves in the game's units is its steel a
>      month at that saving; the template's own draw is HYDROGEN_MWH_A_TONNE's, unchanged by the piece
>   2. THE ENTRY: FE10 is a projected engineering equipment row, its date inside its window and after history's end, needing FE4 (which is dated before it),
>      a near-future row, patented; the piece is known from its month and not before, a month inside the window
>   3. KNOWN IN ITS WINDOW AND NOT BEFORE: in a 1900 city with works standing, the month before FE10 the piece is NOT_KNOWN and no works carries it; at
>      FE10's month it is known and priced; a city whose research found FE10 sooner knows it from the month it found it
>   4. ITS kWh READ BY FE4'S WORKS: with some of the works fitted, Heavy Industry's draw is the works' draws less the saving of each fitted works, the
>      same after EconomyManager sets the draw again, and after months are played; a works fitted draws HYDROGEN_MWH_A_TONNE less the saving a tonne of its
>      steel; none fitted, the buildings' own figure to the bit; a count that comes back takes the saving off again
>   5. THE SAVING AGAINST THE MODEL'S OWN CONSTANTS: the piece's gross gain is the units it saves at the unit's price; its verdict is PAYS exactly where
>      the gain earns the cost back over the piece's life and carries the interest hurdle, the break-even tariff read off the verdict's own cost, factor and
>      hurdle: past it the verdict is PAYS, short of it NOT_YET
>   6. A CITY FOUNDED BEFORE THE WINDOW, AND ONE FOUNDED TODAY'S WAY, ARE UNTOUCHED: the 1900 city a month before FE10 draws the buildings' figure to
>      the bit; a city founded today's way, past FE10's month, with works standing and every count reading fitted, draws it too and carries no works with the
>      piece, even where a count is put on its stack by hand
> 
> Every fixture is HydrogenSteelCheck.town1900(), a city founded the 1900 way with its clock at FE4's month, its works placed by hand and said over by
> unfitTheFounding(), in a GameFiles.scratch() directory (no save slot is written); its clock is moved by ResearchCheck.atMonth() where a date is the premise.

**Uses:** [HeavyIndustry](HeavyIndustry.md) (37), [EquipmentFit](EquipmentFit.md) (28), [Game](Game.md) (18), [ResearchCatalog](ResearchCatalog.md) (12), [BuildingsStacks](BuildingsStacks.md) (7), [Equipment](Equipment.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (7), [BuildingManager](BuildingManager.md) (5), [Good](Good.md) (4), [PolicyDates](PolicyDates.md) (4), [LicenceFees](LicenceFees.md) (4), [HydrogenSteelCheck](HydrogenSteelCheck.md) (3), [HydroSites](HydroSites.md) (3), [ResearchCheck](ResearchCheck.md) (2), [CityCalendar](CityCalendar.md) (1), [ResearchViewCheck](ResearchViewCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 141 | 1. the row and its dials |
| 176 | 2. the entry |
| 197 | 3. known in its window and not before |
| 235 | 4. its kWh read by FE4's works |
| 278 | 5. the saving against the model's own constants |
| 335 | 6. before the window, and today's way |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `ElectrolyserCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 52 | `ElectrolyserCheck.PIECE` | `"electrolyser"` |  |
| 54 | `ElectrolyserCheck.WORKS` | `3` | The works section 3 to 5 stand: enough that "some of them" is more than none and less than all. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |
| 49 | `static int passes` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 346 | **type** `public class ElectrolyserCheck` | Electrolysis at 46 kWh a kilogram (R6d, FE10: the US energy department's target for a PEM system, 55 today; projected 2034, window 2031-2037): the Hydrogen Steel Works' piece of equipment, the 46 kWh electrolyser, and... |
| 56 | 4 | `static void check(String label, boolean ok)` |  |
| 61 | 4 | `static void check(String label, boolean ok, String found)` |  |
| 66 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 70 | 5 | `static void quietly(Runnable r)` |  |
| 77 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly. |
| 82 | 3 | `static int fe10()` | FE10's month in history: the model's own, never a literal. |
| 86 | 3 | `static void at(Game g, int month)` |  |
| 90 | 3 | `static BuildingsStacks works(Game g)` |  |
| 94 | 3 | `static Equipment piece(Game g)` |  |
| 99 | 11 | `static Game city(String label, int n)` | A 1900 city at FE4's month with `n` Hydrogen Steel Works placed by hand and every piece unfitted on them; the sector billed one unit of power a unit at the grid's whole ratio, so that its bill is its draw. |
| 111 | 5 | `static void billAtOne(Game g)` |  |
| 118 | 3 | `static double raw(Game g)` | What the buildings' templates sum to for Heavy Industry, in units: every works it has standing at the draw its template bills (EconomyManager's own sum), whatever the played months have added. |
| 123 | 4 | `static double draw(Game g)` | Heavy Industry's draw at nameplate in units: its bill at one unit of power a unit and the grid's whole ratio. |
| 128 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the row and its dials (lines 141-175)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 32 | `static void one()` |  |

### 2. the entry (lines 176-196)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 18 | `static void two()` |  |

### 3. known in its window and not before (lines 197-234)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 35 | `static void three(Game g)` |  |

### 4. its kWh read by FE4's works (lines 235-277)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 40 | `static void four(Game g)` |  |

### 5. the saving against the model's own constants (lines 278-334)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 47 | `static void five()` |  |
| 328 | 6 | `static double twice(Game g, BuildingsTemplate t, Equipment e, double price)` |  |

### 6. before the window, and today's way (lines 335-391)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 54 | `static void six()` |  |

