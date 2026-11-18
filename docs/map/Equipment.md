# Equipment.java - 162 lines · 16 methods · 2 constants · model

`ham/citybuildersim/Equipment.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends (if any), what fitting it buys and
> how long it lasts. Immutable; read from buildings.json's "equipment" (BuildingCatalog.readEquipment()) and held by the template
> (BuildingsTemplate.getEquipment()).
> 
> WHY (R1, 2026-11-05; docs/studies/research-design.md 2.1-2.2, research-1900.md 3). It replaced nothing: before R1 a building had one way to run,
> and R0c's 1900 forms (Sector.setSteamShare(), Agriculture.setHorseShare(), Haulage.setHorseShare()) were shares of a sector that nobody set.
> A piece says which form it ends, so that the count of a stack's buildings still without it (BuildingsStacks.getUnfitted()) is what sets the
> form's share (EquipmentForms) and "the forms read one number" (R0c's call 3); and it says what fitting it costs, in goods and in
> BUILDING_WORK, and how long it lasts, which is what R1b's payback rule weighs against what it saves. Data and not code, so that the next piece
> is a line in the file and not a class.
> 
> A PIECE MAY END NO FORM (R1c, 2026-11-05; research-design 2.1): "ends" is then null, and the piece exists to enable a recipe (Recipe's
> needsEquipment): the by-product ovens that replace the beehive ovens, which no 1900 form is the share of. The catalogue keeps such a piece
> only where a recipe of its template needs it. A piece that ends a form may be the one a recipe needs too (the tractor will be).
> 
> "since" is a year, as text ("1900"), or a catalogue code ("PE14"). A year is known from its January; a code from its entry's month in
> research.json (R2: ResearchCatalog.knownFrom(); before R2 a code was never known, R1-notes call 7), so the tractor is known from PE14's month,
> June 1917, and not before; a code that is not in the catalogue is never known. knownAt() asks by the game month, knownIn() by the year.

**Uses:** [Good](Good.md) (6), [ResearchCatalog](ResearchCatalog.md) (1), [Research](Research.md) (1), [CityCalendar](CityCalendar.md) (1)

**Used by (23):** [BuildingCatalog](BuildingCatalog.md), [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [DiscoveryCheck](DiscoveryCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [FittingCheck](FittingCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [KeptCheck](KeptCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [Recipe](Recipe.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [ResearchCheck](ResearchCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [SectorScreen](SectorScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 36 | `Equipment.Form.STEAM` | A factory turning its own steam engine in place of drawing power from the grid: Sector.setSteamShare(). |
| 38 | `Equipment.Form.FARM_HORSES` | Farms on horses: Agriculture.setHorseShare(). |
| 40 | `Equipment.Form.CART_HORSES` | Freight's road leg on horse and wagon: Haulage.setHorseShare(). |
| 46 | `Equipment.Form.CAB_DRIVERS` | Freight's trucks with a driver in the cab, held to the driver's hours (R6b, FE1): Haulage.setDriverShare(). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `Equipment.DRIVE_WORK_PER_TONNE` | `10` | Estimated installation points per tonne of motor, shafting and switchgear; replaces a tenth of an unrelated building's work (R1d). |
| 31 | `Equipment.DRIVE_SETUP_POINTS` | `1` | A crew's fixed setup for an electric-drive installation, in BUILDING_WORK points (R1d, estimate). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `private final String id` |  |
| 50 | `private final String name` |  |
| 51 | `private final String since` |  |
| 52 | `private final boolean year` |  |
| 53 | `private final int knownFrom` |  |
| 54 | `private final Form ends` |  |
| 55 | `private final Map<Good, Double> buys` |  |
| 56 | `private final double work` |  |
| 57 | `private final int lifeMonths` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 136 | **type** `public final class Equipment` | A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends (if any), what fitting it buys and how long it lasts. |
| 34 | 14 | **type** `public enum Form` | The 1900 form a piece ends: the share of a sector (or of the carts) that still runs the old way, EquipmentForms sets it from the count of buildings unfitted. |
| 69 | 25 | `public Equipment(String id, String name, String since, Form ends, Map<Good, Double> buys, double work, int lifeMonths)` |  |
| 96 | 7 | `static Integer yearOf(String since)` | A "since" read as a year: the year when it parses as a whole number, null for a catalogue code. |
| 105 | 1 | `public String getId()` | The piece's permanent key on its template. |
| 108 | 1 | `public String getName()` | What the screens call it. |
| 111 | 1 | `public String getSince()` | The year it is known from, as text, or the catalogue code that says (research.json). |
| 114 | 1 | `public Form getEnds()` | The 1900 form it ends, or null for a piece that ends none (it exists to enable a recipe): everything that reads it takes null. |
| 117 | 1 | `public Map<Good, Double> getBuys()` | The goods fitting one building buys, units at nameplate; unmodifiable, and empty for a piece that buys none. |
| 120 | 1 | `public double getWork()` | The BUILDING_WORK points it takes to fit one building. |
| 123 | 1 | `public int getLifeMonths()` | How long it lasts, in months. |
| 126 | 1 | `public boolean isYear()` | Whether "since" is a year (it parses as a whole number) and not a catalogue code. |
| 129 | 1 | `public int knownFrom()` | The game month it is known from: a year's January, a code's entry's month (ResearchCatalog.knownFrom()); Integer.MAX_VALUE for a code not in the catalogue. |
| 132 | 3 | `public boolean knownAt(int gameMonth)` | Whether it is known at a game month (month 1 = January 1900): from the month it is known from on. |
| 141 | 3 | `public boolean knownAt(int gameMonth, Research found)` | Whether it is known at a game month in a city whose research may have found it first (R3): from history's month, or from the month the city found its code, whichever is first (Research.foundMonth()). |
| 146 | 3 | `public boolean knownIn(int calendarYear)` | Whether it is known by a calendar year's January: knownAt() of that month (januaryOf()), so that a screen that asks by the year agrees with the model that asks by the month. |
| 154 | 3 | `static int januaryOf(int calendarYear)` | The game month of a calendar year's January: PolicyDates.monthOf(year, 1), the game's one formula, but not clamped at month 1, so that the year before the start (1899) is before it and a piece dated 1900 is not known ... |
| 159 | 3 | `public String toString()` |  |

