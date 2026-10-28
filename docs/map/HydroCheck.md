# HydroCheck.java - 611 lines · 17 methods · 6 constants · harnesses

`ham/citybuildersim/HydroCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The Hydro Station and the river it needs (HY1, 2026-10-26; docs/queue/HY1-notes.md): the physics of a drop against
> its own constants, the sites on synthetic ground, a generated world's city that has none, a city given sites by fiat
> that orders up to its slots, and the real Earth's rivers when earth-2.bin is named. Not part of the game.
> 
> WHY. The game had only coal and wind, and Canada's power was mostly hydro from 1900. The Earth has no heights on land
> (spec-earth E8), so a river's drop is read from its banks (HydroSites): the lower bank's rise over REACH plots, each
> plot at its relief class's least grade, with the flow from the river's width. Every rule of that reading is caused
> here on ground a fixture lays out, from the model's own constants and from both sides of each threshold, and the
> Earth is asked only whether a river of the real world reads as one: a hydro town does, a flat one does not.
> 
> What this has to prove:
>   1. THE PHYSICS: the flow is (width / HG_COEFF) squared and holds at WIDTH_CAP_M; the power is EFFICIENCY x G x Q x H;
>      a bank's rise is REACH plots of PLOT_M x the class's least grade, for each class; a head under HEAD_MIN_M is none.
>   2. SITES ON SYNTHETIC GROUND (the core, a relief lambda and segments): a river with foothill on both banks gives the
>      head and the power the model says; one bank only gives nothing; the head just under HEAD_MIN_M gives nothing and
>      just over gives a site; a river across three cells gives three; the width is read at each point of a segment; a
>      cell's site is the best of every sample in it, so a best point on ground the city does not own is not the city's;
>      a tie goes to the lower y and then the lower x, the same in whatever order the segments are listed.
>   3. A GENERATED WORLD HAS NO RIVER: no site, and no river read at all; an order for a Hydro Station is refused
>      NO_RIVER, the card's verdict and a run's stop say so, the advice values it at nothing, and the automatic builder
>      run some months orders none.
>   4. SITES BY FIAT: orders up to the slots go ahead and the next is NO_RIVER (a run counts the orders ahead of it);
>      each station standing adds its production1 to the grid; it stands on steep ground a coal plant cannot; the advice
>      offers it while a slot is free, holds its count to the slots free, and offers none past them.
>   5. THE EARTH, when earth-2.bin is named: 20 km squares round hydro towns find sites and round flat towns none, and
>      a city founded at Shawinigan has the sites its ground owns, as the world reads them; ground bought round the
>      nearest site that holds a station makes it the city's, linked, and an order for its stations goes ahead up to
>      the slots and no further.

**Uses:** [HydroSites](HydroSites.md) (161), [World](World.md) (60), [Game](Game.md) (31), [BuildAdvice](BuildAdvice.md) (24), [BuildCard](BuildCard.md) (23), [BuildingsTemplate](BuildingsTemplate.md) (21), [Founding](Founding.md) (13), [EarthData](EarthData.md) (8), [LandClassCheck](LandClassCheck.md) (5), [ChildcareCheck](ChildcareCheck.md) (3), [BuildingType](BuildingType.md) (3), [GameFiles](GameFiles.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [EarthSite](EarthSite.md) (2), [BuildingManager](BuildingManager.md) (2), [LandMap](LandMap.md) (1), [TempDirs](TempDirs.md) (1), [AutoBuilder](AutoBuilder.md) (1), [LandManager](LandManager.md) (1), [CityLand](CityLand.md) (1), [LandGrid](LandGrid.md) (1)

## Sections

| line | section |
|---:|---|
| 86 | 1. THE PHYSICS |
| 140 | 2. SITES ON SYNTHETIC GROUND |
| 274 | 3. A GENERATED WORLD HAS NO RIVER |
| 330 | 4. SITES BY FIAT |
| 475 | 5. THE EARTH |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `HydroCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 145 | `HydroCheck.Y0` | `60` | The plot row of the water of every river here: the river is two plots (60 m) wide, rows Y0 and Y0 + 1, its centre the line y = Y0 + 1. |
| 148 | `HydroCheck.W` | `2 * World.PLOT_M` | The width of the fixtures' rivers, metres: two plots. |
| 166 | `HydroCheck.ALL` | `(cx, cy) -> true` |  |
| 481 | `HydroCheck.PLACES` | `{ new Place("Shawinigan QC", 46.556, - 72.748, true), new Place("Niagara Fall...` |  |
| 491 | `HydroCheck.HALF` | `333` | The half-side of the square a place is scanned in, in plots: 333 (10 km), a square of world 20 km a side. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 574 | **type** `public class HydroCheck` | The Hydro Station and the river it needs (HY1, 2026-10-26; docs/queue/HY1-notes.md): the physics of a drop against its own constants, the sites on synthetic ground, a generated world's city that has none, a city given... |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 48 | 5 | `static void near(String label, double actual, double expected)` | A check against a figure, to a part in a billion of it. |
| 54 | 5 | `static void quietly(Runnable work)` |  |
| 60 | 5 | `static<T> T quiet(java.util.function.Supplier<T> work)` |  |
| 69 | 16 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PHYSICS (lines 86-139)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 3 | `static double riseOf(byte relief)` | The rise of a bank of this relief class all the way out, metres: REACH plots at PLOT_M x the class's least grade. |
| 95 | 44 | `static void thePhysics()` |  |

### 2. SITES ON SYNTHETIC GROUND (lines 140-273)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 3 | `static HydroSites.Segment river(double x0, double x1)` | A river along the line y = Y0 + 1 from x0 to x1, W wide. |
| 156 | 7 | `static HydroSites.Relief banks(java.util.function.IntFunction<Byte> north, java.util.function.IntFunction<Byte> south)` | Ground with the banks' relief given: the water's rows flat; the plots north of it (k = Y0 - y, 1 and up) and those south (k = y - Y0 - 1) read by their number out from the water. |
| 164 | 1 | `static HydroSites.Relief valley(byte both)` |  |
| 168 | 105 | `static void syntheticGround()` |  |

### 3. A GENERATED WORLD HAS NO RIVER (lines 274-329)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 5 | `static BuildingsTemplate hydroOf(Game g)` |  |
| 285 | 3 | `static double stationKw(BuildingsTemplate t)` | The station's own mean power, kW: the units it makes a month, back through the game's hours and unit. |
| 289 | 40 | `static Game generatedWorld() throws Exception` |  |

### 4. SITES BY FIAT (lines 330-474)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 95 | `static void fiatSites(Game g)` |  |
| 431 | 43 | `static void adviceHeld()` | The advice's count for a hydro station is held to the slots free, and it is a candidate only while one is free. |

### 5. THE EARTH (lines 475-611)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 1 | **type** `record Place(String name, double lat, double lon, boolean hydro)` |  |
| 493 | 118 | `static void theEarth() throws Exception` |  |

