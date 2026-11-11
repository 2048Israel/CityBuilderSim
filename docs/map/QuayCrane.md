# QuayCrane.java - 173 lines · 11 methods · 14 constants · model

`ham/citybuildersim/QuayCrane.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The cranes that work a boat at its terminal's quay (HB2, 2026-11-09), as lines and a hook in world plots: where each lies, where its
> hook is and what hangs from it at a share of the boat's time at the quay - pure, with no toolkit, which the map draws over a berth from
> MapFrame.DETAIL_FROM in.
> 
> WHY: Jerus's idea of 2026-10-31, "a small crane at the near view: a pixel line moving an orange box between ship, quay and train",
> picked as look 3a from renders on 2026-11-09 over a standing gantry and a travelling one (the render tool in docs/queue/HB2/ is the
> reference). It is a function of the boat's time at the quay (BoatSchedule.atQuay()) and nothing else, so it keeps no state and the
> boats' frame gains next to nothing: measured in the renders at 0.06 to 0.09 ms a frame for 28 berths.

**Uses:** [CityShore](CityShore.md) (20), [TileRaster](TileRaster.md) (3), [Ports](Ports.md) (3)

**Used by (2):** [MapView](MapView.md), [QuayCraneCheck](QuayCraneCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `QuayCrane.MOVES` | `2` | How many moves a crane makes in a boat's time at its quay: each is a load carried one way and the hook back bare. |
| 26 | `QuayCrane.TWO_CRANES_FROM` | `12` | How long a berth must be, in plots along the shore, to work a boat with two cranes: one unloading, one loading. |
| 29 | `QuayCrane.BARE` | `0, BOX = 1, GRAB = 2, ARM = 3` | What hangs on the hook: nothing, a box, a grab of ore, or (a tanker's loading arm, a line with no hook) the arm. |
| 32 | `QuayCrane.DARK` | `0xff2b2f36` | The render's dark, the box's edge and the line's other shade. |
| 35 | `QuayCrane.LINE` | `TileRaster.blend(TileRaster.STEEL, DARK, 0.3), BOX_FILL = 0xffff9a2e, BOX_EDG...` | The line's colour, the box's, its edge's and the bare hook's, ARGB: the render's LIGHT, ORANGE, DARK and TileRaster.STEEL (a grab's fill is TileRaster.ORE_HEAP). |
| 38 | `QuayCrane.APRON_IN` | `0.5` | How far inside the box's sea edge the apron point lies, in plots: half a plot, the middle of its sea-most row. |
| 41 | `QuayCrane.LINE_LANDWARD` | `0.25` | How far the line runs landward of the apron point, in plots: a quarter. |
| 44 | `QuayCrane.LINE_SEAWARD` | `0.375` | How far the line runs seaward of the ship point, in plots: three eighths. |
| 47 | `QuayCrane.TWO_APART` | `2.0` | How far either side of the boat's place along the shore a crane stands when a berth has two, in plots. |
| 50 | `QuayCrane.ARM_APART` | `1.5` | How far either side of the boat's place along the shore a tanker's loading arm stands, in plots. |
| 53 | `QuayCrane.OUTREACH` | `2.0` | How far out from the quay wall's outer edge a crane reaches for its ship, in plots: a boat moored stern-to has its middle farther out than any boom, and is worked at its stern. |
| 56 | `QuayCrane.END_MARGIN` | `1.0` | How far a crane stands inside the box's two ends, in plots. |
| 59 | `QuayCrane.SECOND_ON` | `0.25` | How far on in its moves the second crane is from the first, in the share of the boat's time at the quay, so that one loads as the other unloads. |
| 62 | `QuayCrane.CARRIED` | `0.42, BACK_STARTS = 0.50, BACK = 0.92` | The phase of a move at which the hook has carried its load across, at which it starts back bare, and at which it has come back (a pause after each). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 156 | **type** `public final class QuayCrane` | The cranes that work a boat at its terminal's quay (HB2, 2026-11-09), as lines and a hook in world plots: where each lies, where its hook is and what hangs from it at a share of the boat's time at the quay - pure, wit... |
| 20 | 1 | `private QuayCrane()` |  |
| 69 | 1 | **type** `public record Reach(double x0, double y0, double x1, double y1, double hx, double hy, int load)` | One crane at a moment: its line from over the ship (x0, y0) to over the apron (x1, y1) - the ends of the line as drawn, a quarter plot landward of the apron point and three eighths of a plot seaward of the ship point ... |
| 76 | 26 | `public static List<Reach> reaches(CityShore.Work w, Ports.Cargo cargo, double boatX, double boatY, double share)` | The cranes working a boat that lies at (boatX, boatY) off the quay of work w, at `share` of its time there (BoatSchedule.atQuay()): none for a work with no harbour quay (a pier of its own has no wall to stand on), for... |
| 104 | 6 | `private static double[] hook(double p, double from, double to)` | Where a crane's hook is at phase p of a move, and whether it carries: {v, 1 or 0}. |
| 112 | 4 | `private static double ease(double x)` | x held to 0..1 and eased in and out. |
| 118 | 9 | `public static Map<Long, CityShore.Work> worksByBerth(CityShore shore)` | The berthed works by their berth's point (CityShore.berths()' x and y, floor of Work.berth()), keyed by berthKey(x, y): how a route's berth finds its terminal. |
| 129 | 3 | `public static long berthKey(long x, long y)` | The key worksByBerth() holds a berth under: its x in the high 32 bits, its y in the low. |
| 140 | 3 | `private static double lengthOf(CityShore.Work w)` | How long the box is along the shore, in plots. |
| 145 | 3 | `private static double depthOf(CityShore.Work w)` | How deep the box is from its land edge to its sea edge, in plots. |
| 150 | 5 | `private static double[] worldOf(CityShore.Work w, double u, double v)` | The world point (x, y) of the work's frame at (u, v) in plots. |
| 157 | 6 | `private static double[] localOf(CityShore.Work w, double x, double y)` | The inverse: the work's frame's {u, v} of the world point (x, y), in plots. |
| 165 | 8 | `private static double[] originOf(CityShore.Work w)` | The world point of the frame's origin: the corner of the box on its land side where u starts. |

