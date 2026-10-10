# ShipShapes.java - 165 lines · 10 methods · 12 constants · model

`ham/citybuildersim/ShipShapes.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> DRAWN. True size from BIG_TILES_FROM px a plot; below it BOOST times true
> (the research's 4.4: "draw boats at 1.6x, and at true size from close zoom
> in"), and never shorter than LEAST_PX, where it is a dart of its colour.
> 
> COLOURS (the research's 4.4, mockup 3's): an import coming in full blue,
> an export going out full orange, an empty leg grey, a box ship - loaded
> both ways - white; the hull dark, darker laden.

**Uses:** [BoatSchedule](BoatSchedule.md) (7), [Ports](Ports.md) (2), [World](World.md) (2), [MapFrame](MapFrame.md) (1)

**Used by (1):** [MapView](MapView.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `ShipShapes.LENGTH_M` | `245` | The Aframax's length, the research's one ship drawn to scale (4.4; mockup 3): 245 m. |
| 34 | `ShipShapes.AFRAMAX_T` | `75_000` | ...and its cargo, the class's (Ports.Ship.AFRAMAX): what the cube root is taken of. |
| 37 | `ShipShapes.BEAM` | `42.0 / 245` | A hull's beam over its length: mockup 3's Aframax, 42 m on 245. |
| 40 | `ShipShapes.BOOST` | `1.6` | Boats are drawn this many times their size below BIG_TILES_FROM px a plot: 1.6 (the research's 4.4, mockup 3's legend). |
| 43 | `ShipShapes.TRUE_FROM` | `MapFrame.BIG_TILES_FROM` | ...and at true size from here, px a plot: MapFrame.BIG_TILES_FROM, 6 - the close zoom. |
| 46 | `ShipShapes.LEAST_PX` | `7` | The shortest a boat is drawn whole, in px: 7 - BEAM of it is 1.2 px, its deck and bridge under a pixel each; shorter, its hull is a dart of its colour (spec-oil 6's fallback, "dots at L1"; star O13-9). |
| 49 | `ShipShapes.IMPORT` | `0xff5aa9ff` | An import coming in full: the research's blue, mockup 3's #5aa9ff. |
| 52 | `ShipShapes.EXPORT` | `0xfff2a65a` | An export going out full: mockup 3's orange, #f2a65a. |
| 55 | `ShipShapes.EMPTY` | `0xff8496ab` | An empty leg, riding high: mockup 3's grey, #8496ab. |
| 58 | `ShipShapes.BOXES` | `0xffd6dde4` | A box ship, loaded both ways: mockup 3's white, #d6dde4. |
| 61 | `ShipShapes.HULL_LADEN` | `0xff1a2836, HULL_EMPTY = 0xff22303e` | A hull's fill, laden and empty: mockup 3's #1a2836 and #22303e. |
| 64 | `ShipShapes.BRIDGE` | `0xffc3ccd3` | The bridge at the stern: mockup 3's #c3ccd3. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 140 | **type** `public final class ShipShapes` | How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, its colours by what it carries and which way, and how much bigger than ... |
| 28 | 1 | `private ShipShapes()` |  |
| 67 | 3 | `public static double lengthM(Ports.Ship ship)` | A class's length in metres: LENGTH_M x (cargo / AFRAMAX_T)^(1/3). |
| 72 | 3 | `public static double boost(double scale)` | How many times its size a boat is drawn at a scale (px a plot): BOOST below TRUE_FROM, else 1. |
| 77 | 5 | `public static int colour(BoatSchedule.Boat b)` | A boat's colour: white for boxes, else blue coming in full, orange going out full, grey empty. |
| 84 | 4 | `public static double drawnLength(BoatSchedule.Boat b, double scale)` | A boat's drawn length in plots at a scale: its class's, boosted, at least LEAST_PX on screen. |
| 95 | 6 | `public static double[][] hull(BoatSchedule.Boat b, double scale)` | A boat's outline in plots, five points from the stern's port corner round to the bow's point: a hull of its drawn length, BEAM of it wide, the bow its last beam drawn to a point, along its heading about where it is. |
| 103 | 4 | `public static double[][] bridge(BoatSchedule.Boat b, double scale)` | Its bridge at the stern, a box {xs, ys} of four points: 7% of its length from the stern, three quarters of its beam. |
| 113 | 34 | `public static double[][][] deck(BoatSchedule.Boat b, double scale)` | Its deck's marks, by kind, as boxes of four points each: a tanker's centre line (one long thin box), a bulk carrier's hatches, a box ship's rows of boxes, a general cargo ship's two holds. |
| 149 | 5 | `public static int boxColour(BoatSchedule.Boat b, int k)` | The colour of a box ship's k-th box: mockup 3's six (blue, red, green, ochre, violet, steel), by the box and its call. |
| 156 | 9 | `static double[][] place(BoatSchedule.Boat b, double[] u, double[] v)` | Points along (u) and across (v) a boat, in plots, turned to its heading about where it is. |

