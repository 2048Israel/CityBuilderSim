# FX5 - the map's rail and roads, finished

Jerus, 2026-10-20, at the review desk, looking at FX3's renders of his
Halifax city (`docs/queue/FX3/after-*.png`): "do trains make 90 degree
turns? ... a long train will be out of the depot. also real trains are
really long, that could help with the fact that in big cities not too many
trains can be shown ... the vertical highway or sloped is good but once it
finishes its still a tad wierd, same with the slanted rails, also some
intersections have 9 roundabouts, perhaps something that if a roundabout is
connected to another the result is a roundabout of that shape? alse why so
much roundabouts? interstctions better?" He chose all four drawing fixes
offered, "Mostly signals", "Queue the fix" for trains over parked wagons,
and for the legend "railway or whatever you think is best"; and: "i think
the rail depots would have to be redesigned to accomdoate such large
trains, as well as teh animation for them right?" - yes, both are in.

The map's alone: nothing in the model reads it, the playtest's trace stays
byte-identical. Bump `GameVersion.VERSION` (player-visible). Light on the
frame: every part is drawing; keep MapCheck's screen budgets and MapCheck
9's 0.5 ms a frame for the trains at 10 billion.

## What to build

1. **Curved rail corners.** Track drawn with a curve where it bends (a
   radius of a few plots at the near view, eased at the middle view), and
   the trains' routes (`CityMap.trainRoutes()`, `CityRuns.wayTo()`) follow
   the same curve, each car along it, so no train swings 90 degrees on the
   spot. The yard's ladders and the sidings' throats likewise.
2. **Real-length trains.** Draw a train at its real length: a unit train
   of 8,000 t at 100 t a wagon is 80 wagons (`TrainSchedule.WAGON_TONNES`),
   about 1.2-1.5 km with its locomotives, North American bulk trains up to
   2-3 km; `MOST_WAGONS` (40) rises to what the payload says, and past
   `MOST_A_MONTH` fewer, longer trains stand for the month's traffic rather
   than more short ones. Sources for the lengths in the dial's sentence.
3. **Yards and depots that hold them.** No train sticks out of its yard:
   the freight station's and the yards' sidings, and the Rail Depot's
   (`DEPOT_TRACKS`), as long as the longest train sent to them, laid along
   the main line where the ground allows (`TileRaster.yardLayout()`); a
   yard too short for a unit train gets only the trains that fit (mixed,
   intermodal), and the routes say so.
4. **The trains' animation for long trains.** The pace (`LEG_SECONDS`,
   `DWELL_SECONDS`) set by length so a long train does not blur; a train
   enters and leaves its siding whole; trains go only onto sidings the
   standing wagons leave clear (FX3's ★6, FR1's one route a yard and
   siding changed, MapCheck 9 moved with it and saying why).
5. **One roundabout the size of the crossing.** Where wide roads cross
   (FX3's 3 x 3 plots of boulevard), one junction drawn over the whole
   crossing, not one a plot (`TileRaster`, JUNCTION_RUN's rule).
6. **Mostly signals** (Jerus: "Mostly signals"): boulevard crossings get
   signals, as arterials do; a roundabout only where five or more roads
   meet (spec-freight §4.6's table and D19 amended in the same commit).
7. **Tidy diagonal ends.** Where a 45-degree highway or a slanted rail
   line ends, or meets the grid, a clean join (a bend onto the grid's axis,
   or the carriageway's end squared) instead of the stub his city shows.
8. **The legend's "Track" becomes "Railway"** (FX3's ★5).

## Harnesses

RailDrawCheck and JunctionCheck sections whose fixtures cause each case on
main first (a bend, a 3 x 3 crossing, five roads meeting, a diagonal's end);
MapCheck 9: every car on track at every sampled moment, no train longer
than its siding, no train on a siding with standing wagons, the frame bound.

## Done when

The suite green but what was already red, the playtests byte-identical, the
maps regenerated, before/after renders of his city beside the notes (FX2's
`RenderFx2.java` on a scratch copy, never his slots), the changelog's block
in `docs/notes/changelog.md`, this row out of `QUEUE.md`.
