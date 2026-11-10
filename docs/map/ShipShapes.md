# ShipShapes.java - 169 lines · 10 methods · 12 constants · model

`ham/citybuildersim/ShipShapes.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, its colours by what it carries and which way, and how much bigger than true it is drawn at a zoom - what ui/MapView fills and strokes, and a Java2D mirror can draw the same; pure.
> 
> WHY THIS EXISTS (0.7.97, batch O13; the research's 4.4, mockup 3). The
> view is JavaFX, which cannot draw here, so the shapes are worked out in the
> model - as RefineryView's picture is - and the view only fills them.
> 
> SIZE. The research gives one ship's length, a 245 m Aframax (4.4; mockup 3
> draws it 245 x 42 m), and the classes' cargo [P30][P40][P42][P46]: a hull
> of one shape scales its length with the cube root of what it carries, so
> a class is LENGTH_M x (cargo / AFRAMAX_T)^(1/3) long (star O13-8): an MR
> 194 m (mockup 3's 183), a VLCC 373, a Capesize 319, a feeder 174, a 5,500 t
> general cargo ship 102 - each within about a tenth of its class's real
> length - and BEAM of it wide (the mockup's 42 / 245).
> 
> DRAWN. True size from TRUE_FROM px a plot - since HB1 (2026-11-08) the
> detail zoom, MapFrame.DETAIL_FROM, where the harbour is drawn as built and a
> docked boat lies alongside its quay (BoatSchedule.alongside()), which a hull
> 1.6 times true cannot do beside a quay drawn at true size; below it BOOST
> times true (the research's 4.4: "draw boats at 1.6x, and at true size from
> close zoom in"), and never shorter than LEAST_PX, where it is a dart of its
> colour.
> 
> COLOURS (the research's 4.4, mockup 3's): an import coming in full blue,
> an export going out full orange, an empty leg grey, a box ship - loaded
> both ways - white; the hull dark, darker laden.

**Uses:** [BoatSchedule](BoatSchedule.md) (7), [Ports](Ports.md) (2), [World](World.md) (2), [MapFrame](MapFrame.md) (1)

**Used by (8):** [BoatSchedule](BoatSchedule.md), [HarbourRenders](HarbourRenders.md), [HarbourYard](HarbourYard.md), [LandMap](LandMap.md), [MapView](MapView.md), [PortCheck](PortCheck.md), [TrainShapes](TrainShapes.md), [TruckShapes](TruckShapes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `ShipShapes.LENGTH_M` | `245` | The Aframax's length, the research's one ship drawn to scale (4.4; mockup 3): 245 m. |
| 38 | `ShipShapes.AFRAMAX_T` | `75_000` | ...and its cargo, the class's (Ports.Ship.AFRAMAX): what the cube root is taken of. |
| 41 | `ShipShapes.BEAM` | `42.0 / 245` | A hull's beam over its length: mockup 3's Aframax, 42 m on 245. |
| 44 | `ShipShapes.BOOST` | `1.6` | Boats are drawn this many times their size below BIG_TILES_FROM px a plot: 1.6 (the research's 4.4, mockup 3's legend). |
| 47 | `ShipShapes.TRUE_FROM` | `MapFrame.DETAIL_FROM` | ...and at true size from here, px a plot: MapFrame.DETAIL_FROM, 3.6 (HB1; it was BIG_TILES_FROM, 6) - where the harbour is drawn as built, so a boat lying alongside its quay is the size of the quay it lies by. |
| 50 | `ShipShapes.LEAST_PX` | `7` | The shortest a boat is drawn whole, in px: 7 - BEAM of it is 1.2 px, its deck and bridge under a pixel each; shorter, its hull is a dart of its colour (spec-oil 6's fallback, "dots at L1"; star O13-9). |
| 53 | `ShipShapes.IMPORT` | `0xff5aa9ff` | An import coming in full: the research's blue, mockup 3's #5aa9ff. |
| 56 | `ShipShapes.EXPORT` | `0xfff2a65a` | An export going out full: mockup 3's orange, #f2a65a. |
| 59 | `ShipShapes.EMPTY` | `0xff8496ab` | An empty leg, riding high: mockup 3's grey, #8496ab. |
| 62 | `ShipShapes.BOXES` | `0xffd6dde4` | A box ship, loaded both ways: mockup 3's white, #d6dde4. |
| 65 | `ShipShapes.HULL_LADEN` | `0xff1a2836, HULL_EMPTY = 0xff22303e` | A hull's fill, laden and empty: mockup 3's #1a2836 and #22303e. |
| 68 | `ShipShapes.BRIDGE` | `0xffc3ccd3` | The bridge at the stern: mockup 3's #c3ccd3. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 140 | **type** `public final class ShipShapes` | How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, its colours by what it carries and which way, and how much bigger than ... |
| 32 | 1 | `private ShipShapes()` |  |
| 71 | 3 | `public static double lengthM(Ports.Ship ship)` | A class's length in metres: LENGTH_M x (cargo / AFRAMAX_T)^(1/3). |
| 76 | 3 | `public static double boost(double scale)` | How many times its size a boat is drawn at a scale (px a plot): BOOST below TRUE_FROM, else 1. |
| 81 | 5 | `public static int colour(BoatSchedule.Boat b)` | A boat's colour: white for boxes, else blue coming in full, orange going out full, grey empty. |
| 88 | 4 | `public static double drawnLength(BoatSchedule.Boat b, double scale)` | A boat's drawn length in plots at a scale: its class's, boosted, at least LEAST_PX on screen. |
| 99 | 6 | `public static double[][] hull(BoatSchedule.Boat b, double scale)` | A boat's outline in plots, five points from the stern's port corner round to the bow's point: a hull of its drawn length, BEAM of it wide, the bow its last beam drawn to a point, along its heading about where it is. |
| 107 | 4 | `public static double[][] bridge(BoatSchedule.Boat b, double scale)` | Its bridge at the stern, a box {xs, ys} of four points: 7% of its length from the stern, three quarters of its beam. |
| 117 | 34 | `public static double[][][] deck(BoatSchedule.Boat b, double scale)` | Its deck's marks, by kind, as boxes of four points each: a tanker's centre line (one long thin box), a bulk carrier's hatches, a box ship's rows of boxes, a general cargo ship's two holds. |
| 153 | 5 | `public static int boxColour(BoatSchedule.Boat b, int k)` | The colour of a box ship's k-th box: mockup 3's six (blue, red, green, ochre, violet, steel), by the box and its call. |
| 160 | 9 | `static double[][] place(BoatSchedule.Boat b, double[] u, double[] v)` | Points along (u) and across (v) a boat, in plots, turned to its heading about where it is. |

