# TrainShapes.java - 92 lines · 6 methods · 6 constants · model

`ham/citybuildersim/TrainShapes.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> How the map draws a train: each vehicle's body and its load's mark as
> points in plots about where it is and the way it heads, and their colours
> by what the train carries and which way - what ui/MapView fills, and a
> Java2D mirror can draw the same; pure (0.8.2, batch FR1; spec-freight 4.4).
> 
> WHY THIS EXISTS. ShipShapes' reason: the view is JavaFX, which cannot draw
> here, so the shapes are worked out in the model and the view only fills
> them.
> 
> SIZE. Each vehicle's length to scale (TrainSchedule.CAR_PLOTS and
> LOCO_PLOTS) less a coupling's GAP, and WIDTH wide, about the track bed's:
> a true wagon, about 3 m, is a tenth of a plot, under a pixel at the near
> view. Vehicle by vehicle from CARS_FROM px a plot, the near view; below it
> (the middle view) a train is a dash of its colour along the line.
> 
> COLOURS (the boats', spec-freight 4.4): a full import blue, a full export
> orange, an empty train grey, boxes white - ShipShapes' four; the
> locomotives dark. From MARKS_FROM px a plot each wagon shows its kind: a
> hopper's heap of ore, a tank's barrel, the gap between a flat's two boxes,
> a van's roof.

**Uses:** [TrainSchedule](TrainSchedule.md) (10), [TileRaster](TileRaster.md) (4), [ShipShapes](ShipShapes.md) (4), [MapFrame](MapFrame.md) (1)

**Used by (3):** [LandMap](LandMap.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `TrainShapes.LOCO` | `0xff2b2f36` | A locomotive, dark (spec-freight 4.4). |
| 33 | `TrainShapes.WIDTH` | `0.42` | A vehicle's drawn width in plots: 0.42, about the track bed's (TileRaster.BED 0.52), so the train covers its rails. |
| 36 | `TrainShapes.GAP` | `0.08` | The gap left between two vehicles, in plots: 0.08, so the wagons read one by one from MARKS_FROM in. |
| 39 | `TrainShapes.CARS_FROM` | `MapFrame.L0_FROM` | Pixels a plot from which a train is drawn vehicle by vehicle: MapFrame.L0_FROM, the near view; below it, a dash. |
| 42 | `TrainShapes.MARKS_FROM` | `8` | Pixels a plot from which a wagon shows its kind's mark: 8, where a wagon is 4 px long. |
| 45 | `TrainShapes.HEAP` | `TileRaster.ORE_HEAP` | A hopper's heap of ore: TileRaster.ORE_HEAP, the yards' stockpiles' sand. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 68 | **type** `public final class TrainShapes` | How the map draws a train: each vehicle's body and its load's mark as points in plots about where it is and the way it heads, and their colours by what the train carries and which way - what ui/MapView fills, and a Ja... |
| 27 | 1 | `private TrainShapes()` |  |
| 48 | 5 | `public static int colour(TrainSchedule.Train t)` | A train's colour by what it carries and which way: an empty train grey, boxes white, a full import blue, a full export orange. |
| 55 | 4 | `public static double[][] body(TrainSchedule.Car c)` | A vehicle's body: its outline as {xs, ys} in plots, a rectangle along its heading. |
| 61 | 10 | `public static double[][] mark(TrainSchedule.Car c, TrainSchedule.Train t)` | A wagon's mark of its kind, {xs, ys} in plots, or null: a locomotive's, an empty flat's. |
| 73 | 9 | `public static int markColour(TrainSchedule.Train t)` | The mark's colour: a full hopper's heap, an empty one's hold, a tank's light barrel, a van's dark roof, the dark gap between two boxes. |
| 84 | 8 | `static double[][] rect(TrainSchedule.Car c, double hl, double hw)` | A rectangle about a vehicle's middle: hl along its heading either way, hw across. |

