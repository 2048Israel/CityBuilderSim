# Equipment.java - 122 lines · 11 methods · 0 constants · model

`ham/citybuildersim/Equipment.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends, what fitting it buys and how
> long it lasts. Immutable; read from buildings.json's "equipment" (BuildingCatalog.readEquipment()) and held by the template
> (BuildingsTemplate.getEquipment()).
> 
> WHY (R1, 2026-11-05; docs/studies/research-design.md 2.1-2.2, research-1900.md 3). It replaced nothing: before R1 a building had one way to run,
> and R0c's 1900 forms (Sector.setSteamShare(), Agriculture.setHorseShare(), Haulage.setHorseShare()) were shares of a sector that nobody set.
> A piece says which form it ends, so that the count of a stack's buildings still without it (BuildingsStacks.getUnfitted()) is what sets the
> form's share (EquipmentForms) and "the forms read one number" (R0c's call 3); and it says what fitting it costs, in goods and in
> BUILDING_WORK, and how long it lasts, which is what R1b's payback rule weighs against what it saves. Data and not code, so that the next piece
> is a line in the file and not a class.
> 
> "since" is a year, as text ("1900"), or a catalogue code ("PE14"). A year is known from its January (knownIn()); a code is never known in
> this build: R2's catalogue resolves codes to their years, and until it reads one the piece is data and never pays (R1-notes call 7).

**Uses:** [Good](Good.md) (6)

**Used by (5):** [BuildingCatalog](BuildingCatalog.md), [BuildingsTemplate](BuildingsTemplate.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 27 | `Equipment.Form.STEAM` | A factory turning its own steam engine in place of drawing power from the grid: Sector.setSteamShare(). |
| 29 | `Equipment.Form.FARM_HORSES` | Farms on horses: Agriculture.setHorseShare(). |
| 31 | `Equipment.Form.CART_HORSES` | Freight's road leg on horse and wagon: Haulage.setHorseShare(). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final String id` |  |
| 35 | `private final String name` |  |
| 36 | `private final String since` |  |
| 37 | `private final boolean year` |  |
| 38 | `private final int sinceYear` |  |
| 39 | `private final Form ends` |  |
| 40 | `private final Map<Good, Double> buys` |  |
| 41 | `private final double work` |  |
| 42 | `private final int lifeMonths` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 101 | **type** `public final class Equipment` | A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends, what fitting it buys and how long it lasts. |
| 25 | 8 | **type** `public enum Form` | The 1900 form a piece ends: the share of a sector (or of the carts) that still runs the old way, EquipmentForms sets it from the count of buildings unfitted. |
| 54 | 34 | `public Equipment(String id, String name, String since, Form ends, Map<Good, Double> buys, double work, int lifeMonths)` |  |
| 90 | 1 | `public String getId()` | The piece's permanent key on its template. |
| 93 | 1 | `public String getName()` | What the screens call it. |
| 96 | 1 | `public String getSince()` | The year it is known from, as text, or the catalogue code that will say. |
| 99 | 1 | `public Form getEnds()` | The 1900 form it ends. |
| 102 | 1 | `public Map<Good, Double> getBuys()` | The goods fitting one building buys, units at nameplate; unmodifiable, and empty for a piece that buys none. |
| 105 | 1 | `public double getWork()` | The BUILDING_WORK points it takes to fit one building. |
| 108 | 1 | `public int getLifeMonths()` | How long it lasts, in months. |
| 111 | 1 | `public boolean isYear()` | Whether "since" is a year (it parses as a whole number) and not a catalogue code. |
| 114 | 3 | `public boolean knownIn(int calendarYear)` | Whether it is known in a calendar year: a year since from that year on, a code never (R2's catalogue resolves codes; until then none is known). |
| 119 | 3 | `public String toString()` |  |

