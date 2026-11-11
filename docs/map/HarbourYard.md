# HarbourYard.java - 508 lines · 31 methods · 28 constants · model

`ham/citybuildersim/HarbourYard.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The picture of one work on the shore at the near view (HB2, 2026-11-09): its
> open cargo yard by what it handles, the stock on it at a level, and the quay
> tracks and wagons of a terminal the railway reaches - ARGB pixels at PX a
> plot, with no toolkit, which the map draws over the work's box from
> MapFrame.DETAIL_FROM in.
> 
> WHY: until HB2 a terminal was the tile's flat box at every zoom. HB1's
> renders settled the near look (the open yard, its render (b)) and Jerus
> picked HB2's three details from renders (docs/queue/HB2.md: 1a the yard
> fills, 2a tracks on the quay, 3a a pixel-line crane). The yard is a picture
> of its own and not tile paint because the tile draws from 3.2 px a plot and
> his rule is detail from DETAIL_FROM (the 500 m bar), because a level that
> steps with the month would dirty tiles every month, and because the look was
> drawn at 8 px a plot where the tile is 4 below zoom 6. The reference look is
> the render tool beside the pictures in docs/queue/HB2/.

**Uses:** [TileRaster](TileRaster.md) (19), [Ports](Ports.md) (4), [CityShore](CityShore.md) (4), [BoatSchedule](BoatSchedule.md) (3), [ShipShapes](ShipShapes.md) (3), [BuildingVisual](BuildingVisual.md) (1)

**Used by (4):** [MapView](MapView.md), [PortCheck](PortCheck.md), [QuayRail](QuayRail.md), [QuayRailCheck](QuayRailCheck.md)

## Sections

| line | section |
|---:|---|
| 63 | · the look's colours: the game's own where it has one, the render's literals else |
| 107 | · what a work is, and how full it shows |
| 131 | · the picture |
| 189 | · the track's geometry, and small tools |
| 222 | · the canvas |
| 316 | · the yards |
| 420 | · the rail |

## Enum constants

| line | constant | says |
|---:|---|---|
| 37 | `HarbourYard.Kind.CONTAINER` |  |
| 37 | `HarbourYard.Kind.DRY_BULK` |  |
| 37 | `HarbourYard.Kind.LIQUID` |  |
| 37 | `HarbourYard.Kind.GENERAL` |  |
| 37 | `HarbourYard.Kind.TANKS` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `HarbourYard.PX` | `8` | How many pixels a plot the picture is painted at: the near view's tile at its finest, and what the renders were drawn at. |
| 34 | `HarbourYard.LEVELS` | `8` | How many steps of stock a yard shows above empty: level 0 is bare ground and its sheds, LEVELS a full yard. |
| 45 | `HarbourYard.RAIL` | `1` | The quay track: one track along the box's whole sea-side row (and at a large berth a second, a loop off it). |
| 47 | `HarbourYard.OPEN_LOW` | `2` | The quay track runs on past the box's LOW end, to a neighbour on the same quay line; without it the track ends short of that end at a buffer stop. |
| 49 | `HarbourYard.OPEN_HIGH` | `4` | ...and past its HIGH end. |
| 51 | `HarbourYard.LEAD_LOW` | `8` | The lead: a track from the box's land edge up its LOW end's column to the quay track, where the railway's quay spur comes in from behind. |
| 53 | `HarbourYard.LEAD_HIGH` | `16` | ...up its HIGH end's column. |
| 55 | `HarbourYard.TRAIN` | `32` | A train stands on the quay track. |
| 58 | `HarbourYard.LOOP_PLOTS` | `10` | How many plots deep a box must be to have a second track, a loop off the quay track by a turnout (and, with TRAIN, a second train on it). |
| 61 | `HarbourYard.PICTURE_MS` | `0.5` | What painting one work's picture may take, in ms: the largest box (16 plots by 15) of the costliest kind, filled and with its train - a map redraw paints a few, once, and keeps them while the month stands. |
| 66 | `HarbourYard.SLATE` | `0xff2e3640` | The dark slate HB1's (b) yard was drawn on, which the ground is mixed from. |
| 69 | `HarbourYard.GROUND` | `mix(SLATE, TileRaster.YARD_GROUND, 0.32)` | A yard's bare ground: the slate drawn a third of the way to the tile's YARD_GROUND. |
| 72 | `HarbourYard.EDGE` | `0xff4a5866` | The 1 px line round the box. |
| 75 | `HarbourYard.PAD` | `mix(GROUND, 0xffffffff, 0.07)` | The pad an empty container slot or crate stands on: the ground a shade lighter. |
| 78 | `HarbourYard.HEAP_PAD` | `mix(GROUND, 0xff000000, 0.25)` | The pad a heap grows from: the ground darkened. |
| 81 | `HarbourYard.TANK_RIM` | `0xff7f8ca6` | A tank's rim, and the hatch on a full tank's roof. |
| 84 | `HarbourYard.TANK_EMPTY` | `0xff1d252d` | An empty tank's roof, near black. |
| 87 | `HarbourYard.TANK_FULL` | `0xffa6b6d2` | A full tank's roof, which a floating roof rides high and light. |
| 90 | `HarbourYard.TANK_BARE` | `mix(0xff2a3540, TANK_RIM, 0.4)` | The roof of a tank farm's tank, which holds no level the map shows. |
| 93 | `HarbourYard.SHED_ROOF` | `mix(TileRaster.STEEL, SLATE, 0.55)` | A shed's roof: steel darkened toward the slate. |
| 96 | `HarbourYard.LOCO` | `0xff2b2f36` | A locomotive's body. |
| 99 | `HarbourYard.STOP` | `0xffa8442f` | A buffer stop's red. |
| 102 | `HarbourYard.CRATES` | `{ 0xff7b4b3a, mix(TileRaster.ORE_HEAP, SLATE, 0.35), mix(TileRaster.STEEL, SL...` | The colours of a crate: a red-brown timber, the ore colour and the steel darkened to the slate, and the timber toward the ore. |
| 105 | `HarbourYard.BOX` | `boxColours(60)` | A container's colours by its place in the yard: a box ship's own palette, ShipShapes.boxColour, of one call. |
| 192 | `HarbourYard.QUAY_FROM_SEA` | `5` | How far the quay track's middle lies from the sea side, in pixels: the bed is 5 across, so it stops 2 pixels short of the sea side. |
| 195 | `HarbourYard.LOOP_FROM_SEA` | `12` | How far a loop's middle lies from the sea side, in pixels. |
| 198 | `HarbourYard.STOP_SHORT` | `3` | How far short of the box's end a track that does not run on stops, in pixels, its buffer stop in the last of them. |
| 201 | `HarbourYard.LEAD_FROM_END` | `5` | A lead's middle, in pixels from the end of the box it stands at: the bed is 5 across, with 2 pixels of ground outside it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 236 | `final int pw, ph, ax, ay, dx, dy, bx, by, L, D` |  |
| 237 | `final int[] px` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 26 | 483 | **type** `public final class HarbourYard` | The picture of one work on the shore at the near view (HB2, 2026-11-09): its open cargo yard by what it handles, the stock on it at a level, and the quay tracks and wagons of a terminal the railway reaches - ARGB pixe... |
| 28 | 1 | `private HarbourYard()` |  |
| 37 | 1 | **type** `public enum Kind` | What a work on the shore handles, which is what its yard holds: boxes, heaps, tanks behind a tanker berth, crates and sheds, or a tank farm's tanks. |

### the look's colours: the game's own where it has one, the render's literals else (lines 63-106)

### what a work is, and how full it shows (lines 107-130)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 14 | `public static Kind kindOf(BuildingVisual.Type t)` | What a work's box is drawn as: a terminal by the cargo of its berth, a tank farm as TANKS, null for a type that is neither (a work these pictures leave as it is). |
| 126 | 4 | `public static int step(double busy)` | The step of stock a yard shows for how busy its kind was, 0 to LEVELS: 0 for none, else at least 1 - any cargo at all shows - and at most LEVELS. |

### the picture (lines 131-188)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 23 | `public static int[] picture(CityShore.Work w, Kind kind, int level, int rail)` | A work's box painted as opaque ARGB pixels, w.w() * PX wide by w.h() * PX high, row-major, in the world's orientation (x east, y south, pixel (0, 0) the box's north-west corner): its yard at `level` (0 to LEVELS, more... |
| 168 | 7 | `public static int[] gapCell(int side)` | One plot of the quay track between two neighbouring boxes of a quay line whose sea side is `side` (0 north to 3 west), PX by PX in the world's orientation: the bed, sleepers and rails at the offset across the plot tha... |
| 180 | 8 | `public static int[] leadCell(int side, boolean high)` | One plot of the lead behind a box whose sea side is `side`, PX by PX in the world's orientation: the track across the shore edge to edge, at the offset across the plot that the box's LEAD_LOW column's track has (`high... |

### the track's geometry, and small tools (lines 189-221)

| line | len | member | says |
|---:|---:|---|---|
| 203 | 1 | `private static int sideOf(int side)` |  |
| 206 | 6 | `private static int mix(int a, int b, double t)` | t of b over a, per channel, opaque. |
| 213 | 1 | `private static double clamp(double x)` |  |
| 215 | 6 | `private static int[] boxColours(int n)` |  |

### the canvas (lines 222-315)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 4 | **type** `private record Zone(int u0, int v0, int u1, int v1, int apron)` | The part of a box its stock may stand on, in the box's own pixels, and the apron left clear at its sea end. |
| 226 | 1 | `int W()` _(in HarbourYard.Zone)_ |  |
| 227 | 1 | `int H()` _(in HarbourYard.Zone)_ |  |
| 235 | 80 | **type** `private static final class Canvas` | A picture's pixels and a work's box in its own frame: u along the sea side (as the sea is faced, left to right), v from the land side (0) out to the sea side (D), in pixels, so a yard is painted once for the four sides. |
| 239 | 14 | `Canvas(int plotsW, int plotsH, int side)` _(in HarbourYard.Canvas)_ |  |
| 254 | 1 | `double x(double u, double v)` _(in HarbourYard.Canvas)_ |  |
| 256 | 1 | `double y(double u, double v)` _(in HarbourYard.Canvas)_ |  |
| 259 | 9 | `void rect(double u0, double v0, double u1, double v1, int argb)` _(in HarbourYard.Canvas)_ | A rectangle of the frame, filled. |
| 270 | 26 | `void quad(double[] us, double[] vs, int argb)` _(in HarbourYard.Canvas)_ | A convex quadrilateral (or any convex polygon) of the frame, filled. |
| 298 | 16 | `void ellipse(double cu, double cv, double ru, double rv, int argb)` _(in HarbourYard.Canvas)_ | An ellipse of the frame, its edge blended by how much of the pixel it holds. |

### the yards (lines 316-419)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 24 | `private static void containers(Canvas c, Zone z, double level, Random r)` | Rows of small coloured boxes in blocks with lanes between, an apron left by the quay; each slot has its turn to fill, from the land side toward the quay, the front ragged. |
| 345 | 17 | `private static void heaps(Canvas c, Zone z, double level, Random r)` | Two or three heaps, ochre or grey, each a darker ring and a lighter crown, on a pad: the heap has the area the level gives. |
| 364 | 16 | `private static void tanks(Canvas c, Zone z, double level, boolean filling)` | Round tanks on a grid, each a rim, a roof and a hatch: `filling`, so many tanks full and the rest empty (a floating roof rides high and light on a full tank); else a tank farm's, no level. |
| 382 | 29 | `private static void crates(Canvas c, Zone z, double level, Random r)` | One shed along the land side, stacked crates in groups between it and the apron: a group stacks two high where the room allows, and the crates fill in a shuffled order, one at a time. |
| 413 | 6 | `private static void shedRoof(Canvas c, double u0, double v0, double u1, double v1)` | A shed's roof: a framed rectangle with a lighter ridge down its middle. |

### the rail (lines 420-508)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 28 | `private static void rails(Canvas c, Kind kind, boolean big, boolean open0, boolean openL, boolean lead0, boolean leadL, boolean...` | The quay track along the sea-most row, behind the wall: at a large box a second, a loop off it by a turnout. |
| 458 | 4 | `private static void bed(Canvas c, int uA, int uB, int v)` | A track along the quay from uA to uB, its middle at v: the bed and a sleeper every third pixel. |
| 464 | 4 | `private static void steel(Canvas c, int landA, int landB, int seaA, int seaB, int v)` | ...and its two steel rails, the land side's from landA to landB and the sea side's from seaA to seaB. |
| 470 | 5 | `private static void lead(Canvas c, boolean atU0, int vB)` | A lead across the frame from the land side (v = 0) to vB, up the column at the u = 0 end or the u = L end: its bed and sleepers. |
| 477 | 5 | `private static void leadSteel(Canvas c, boolean atU0, int outerTo, int innerTo)` | A lead's two rails up to the quay track: the outer to outerTo, the inner to innerTo (the sea side's rail where the track turns, else the land side's). |
| 484 | 6 | `private static void diag(Canvas c, double uA, double vA, double uB, double vB)` | The turnout between two tracks along the quay. |
| 492 | 1 | `private static void stop(Canvas c, int u, int v)` | A buffer stop across a track along the quay, at u. |
| 495 | 13 | `private static void trainU(Canvas c, Kind kind, int uA, int uB, int v, boolean loco)` | A train standing on a track along the quay: a locomotive (if asked) and a wagon every 4 px (TileRaster.WAGON_LENGTH, half a plot); a wagon is white for boxes, else the import's blue, with a mark by the cargo. |

