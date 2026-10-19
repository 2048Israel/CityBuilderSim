# TruckShapes.java - 95 lines · 4 methods · 7 constants · model

`ham/citybuildersim/TruckShapes.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> How the map draws a truck: its body as points in plots about where it is
> and the way it heads, and its colour by what it carries - what ui/MapView
> fills, and a Java2D mirror can draw the same; pure (0.8.8, batch FR5;
> spec-freight 4.7).
> 
> WHY THIS EXISTS. TrainShapes' reason: the view is JavaFX, which cannot draw
> here, so the shape is worked out in the model and the view only fills it.
> A truck is the smallest thing the map moves, so its shape is the least:
> one silhouette, a trailer with a narrower cab ahead of it, that reads as a
> truck and not a dot from the near view, where trucks are drawn (TruckSchedule.drawnAt()),
> and nothing finer - at the pixels a plot a truck has, a door or a wheel is
> under a pixel.
> 
> SIZE. LENGTH by WIDTH plots: the length is a lorry's own (16.5 m
> articulated, on a 30 m plot), the width is drawn about 2.6 times true
> (6.6 m for 2.5 m) so that it is more than a line at the near view, where a
> plot is MapFrame.L0_FROM px or more; and never under MIN_PX long, where it
> is drawn larger with its shape kept.
> 
> COLOURS (the boats' and the trains', spec-freight 4.7): a loaded import
> blue, a loaded export orange, an empty truck grey - ShipShapes' three, the
> same numbers. A tonne bought at home, hauled from one works to another and
> never across the city's edge, is neither an import nor an export and has a
> colour of its own, HOME.

**Uses:** [TruckSchedule](TruckSchedule.md) (4), [ShipShapes](ShipShapes.md) (3)

**Used by (1):** [MapView](MapView.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `TruckShapes.LENGTH` | `TruckSchedule.TRUCK_PLOTS` | A truck's length in plots: the schedule's (TruckSchedule.TRUCK_PLOTS, 0.55, a 16.5 m articulated lorry), which frames the trucks by it. |
| 42 | `TruckShapes.MIN_PX` | `4` | The fewest pixels a truck is drawn long: 4 (the review, FR5). |
| 45 | `TruckShapes.WIDTH` | `0.22` | A truck's drawn width in plots: 0.22, a lorry's proportions on the road bed, narrower than a train's (TrainShapes.WIDTH 0.42). |
| 48 | `TruckShapes.CAB_SHARE` | `0.30` | The cab's share of the truck's length: 0.30, the tractor ahead of its trailer. |
| 51 | `TruckShapes.CAB_NARROW` | `0.80` | The cab's width over the trailer's: 0.80, so the silhouette steps in where the tractor starts and reads as a tractor and a trailer. |
| 59 | `TruckShapes.LANE` | `0.2` | How far right of the road's middle a truck drives, in plots: 0.2 (the review, FR5), so the trucks going in and those coming out pass side by side and do not draw over each other on the centre line (the renders' first ... |
| 62 | `TruckShapes.HOME` | `0xffa78bfa` | A tonne bought at home (works to works, loaded), neither an import nor an export: Palette.BUSINESS, the business area's violet, #a78bfa - the works' own colour, and no other mark on the map's freight. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 67 | **type** `public final class TruckShapes` | How the map draws a truck: its body as points in plots about where it is and the way it heads, and its colour by what it carries - what ui/MapView fills, and a Java2D mirror can draw the same; pure (0.8.8, batch FR5; ... |
| 31 | 1 | `private TruckShapes()` |  |
| 65 | 8 | `public static int colour(TruckSchedule.Truck t)` | A truck's colour by what it carries: an empty truck grey, a loaded import blue, a loaded export orange (ShipShapes'), a load bought at home HOME. |
| 75 | 1 | `public static double[][] body(TruckSchedule.Truck t)` | A truck's body: its outline as {xs, ys} in plots, a trailer with the cab ahead of it, at its heading, true to size. |
| 78 | 17 | `public static double[][] body(TruckSchedule.Truck t, double px)` | ...drawn at px pixels a plot: true to size, or larger, its shape kept, where that would be under MIN_PX long. |

