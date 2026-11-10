# HarbourRenders.java - 983 lines · 53 methods · 8 constants · tools

`ham/citybuildersim/tools/HarbourRenders.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> Renders a Bergen city's harbour for review, headless: HB1's placement before and after, and three ways to draw a terminal's box
> under the quay walls and the docked boats HB1 draws from the detail zoom, by the game's own painters.
> 
>     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.HarbourRenders [outDir]
> 
> outDir is docs/queue/HB1 unless named. The earth file is -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); -Drun.months=N
> plays each city N months (2,400 unless named). Two cities are founded at Bergen (LongPlaytest.earthCity(), each in a
> GameFiles.scratch() folder deleted when the JVM exits - never the real saves), the first with CityMap.bundleHarbour false (MF1's
> order), the second with it true (HB1's), and each map is read tile by tile through CityMap.tileInput(), TilePainter.paint() and
> TileRaster.raster(), as the screen reads it. It writes:
> 
>   hb1-0-mf1.png        the city's largest harbour, MF1's order, at 2 px a plot: the window round all its shore works plus 2 tiles
>                        of margin, at most 2,048 px a side
>   hb1-0-bundled.png    the same, HB1's order
>   hb1-a-boxes.png      from the bundled city, the window of up to 6 x 4 tiles (1,536 x 1,024 px, 8 px a plot) holding the most
>                        shore works: the game's tiles, the quay walls (CityShore.walls()), a boat docked alongside at each berth
>                        (laid by BoatSchedule.berthed(): alongside where it fits, else stern-to, else as the schedule places it); each terminal's box as the game draws it now
>   hb1-b-yard.png       ...the same window, each box an open cargo yard by what its terminal handles: rows of boxes, heaps,
>                        round tanks, crates and a shed
>   hb1-c-cranes.png     ...the same window, each terminal's box with a long transit shed set back from the wall and, on the
>                        wall, 2 or 3 quay cranes with their booms out over the ship
>   hb1-x-4px.png        an extra: the same window as a, at 4 px a plot (just past MapFrame.DETAIL_FROM; boats are true size there since HB1 slice E)
>   hb1-d-kinds.png      an extra: a specimen sheet, every kind of terminal and a tank farm (a row each) in the drawings a, b and c (a
>                        column each) on a plain shore, for the kinds the Bergen window lacks
>   README.md            one line a picture, the runs (months, seconds, works a harbour) and what the pictures show of HB1's drawing
> 
> WHY (HB1): the harbour's look is Jerus's call, put to him with renders. Not a harness: nothing here asserts, and nothing under
> docs/ but its outDir is written. CityMap.bundleHarbour is a public test switch, set for each city's play and put back.

**Uses:** [CityShore](CityShore.md) (56), [BoatSchedule](BoatSchedule.md) (29), [World](World.md) (24), [Ports](Ports.md) (20), [BuildingVisual](BuildingVisual.md) (15), [TileRaster](TileRaster.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (10), [ShipShapes](ShipShapes.md) (10), [CityMap](CityMap.md) (9), [Game](Game.md) (7), [TilePainter](TilePainter.md) (5), [EarthData](EarthData.md) (3), [BuildingCatalog](BuildingCatalog.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1), [CityLand](CityLand.md) (1)

## Sections

| line | section |
|---:|---|
| 237 | · the cities |
| 347 | · the window |
| 409 | · the berths and their boats |
| 468 | · the drawing |
| 635 | · b: the yards |
| 730 | · c: the sheds and the cranes |
| 772 | · the specimen sheet |
| 846 | · what the drawing shows |

## Enum constants

| line | constant | says |
|---:|---|---|
| 90 | `HarbourRenders.Kind.CONTAINER` |  |
| 90 | `HarbourRenders.Kind.DRY_BULK` |  |
| 90 | `HarbourRenders.Kind.LIQUID` |  |
| 90 | `HarbourRenders.Kind.GENERAL` |  |
| 90 | `HarbourRenders.Kind.TANKS` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `HarbourRenders.PX` | `8, ACROSS = 6, DOWN = 4` | Pixels a plot in the detail pictures, and their window in tiles: 8 x 32 x 6 by 8 x 32 x 4, 1,536 x 1,024. |
| 81 | `HarbourRenders.HARBOUR_PX` | `2, MARGIN_TILES = 2, TILES_MOST = 32` | Pixels a plot in the harbour pictures, the margin round the works in tiles, and the most tiles a side: 2 x 32 x 32 = 2,048 px. |
| 84 | `HarbourRenders.SEAWARD` | `10` | How far past a box's sea side, in plots, a window must hold a work for it to count whole: its quay and the boat alongside. |
| 87 | `HarbourRenders.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west: CityShore.DX and DY, which are package-private. |
| 106 | `HarbourRenders.ALONGSIDE` | `0, STERN_TO = 1, AS_PLACED = 2` | The places BoatSchedule.berthed() gives a docked boat: alongside its quay, stern-to it, or as the schedule placed it, centred on the berth point. |
| 240 | `HarbourRenders.TIMES` | `new HashMap<>()` | The seconds each city took to play, by its label: one number in an array. |
| 638 | `HarbourRenders.GROUND` | `mix(0xff2e3640, TileRaster.YARD_GROUND, 0.32)` | A yard's ground: the terminal's slate warmed a little towards the rail yard's ballast. |
| 775 | `HarbourRenders.SEA` | `0xff4f87b9, LAND = 0xff8aa07a` | The sea and the land of the specimen sheet: plain and muted, not the game's tiles. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `long x, y` |  |
| 98 | `int landmass` |  |
| 99 | `final List<CityShore.Work> works` |  |
| 556 | `final int px, ax, ay, dx, dy, L, D, quay` |  |
| 557 | `final double bx, by, ox, oy` |  |
| 558 | `final long x0, y0` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 911 | **type** `public final class HarbourRenders` | Renders a Bergen city's harbour for review, headless: HB1's placement before and after, and three ways to draw a terminal's box under the quay walls and the docked boats HB1 draws from the detail zoom, by the game's o... |
| 75 | 1 | `private HarbourRenders()` |  |
| 90 | 1 | **type** `private enum Kind` | What a terminal's box is drawn as: a kind of cargo yard. |
| 93 | 1 | **type** `private record Frame(long x0, long y0, int px)` | A world, a window on it: the plot at its top left and the pixels a plot. |
| 96 | 5 | **type** `private static final class Harbour` | A harbour as CityShore.harbours() makes it: its first work's shore plot, its landmass and its works. |
| 103 | 1 | **type** `private record Plot(long x, long y)` | A world plot. |
| 109 | 8 | **type** `private record Docked(CityShore.Work work, BoatSchedule.Berth berth, BoatSchedule.Boat boat, int place)` | A berth, the work it belongs to, the boat docked at it as it is drawn, and its place (ALONGSIDE, STERN_TO or AS_PLACED). |
| 110 | 1 | `Docked(CityShore.Work work, BoatSchedule.Berth berth, BoatSchedule.Boat boat)` _(in HarbourRenders.Docked)_ |  |
| 113 | 1 | `boolean laid()` _(in HarbourRenders.Docked)_ | Whether it lies alongside or stern-to, not as the schedule placed it. |
| 115 | 1 | `String name()` _(in HarbourRenders.Docked)_ |  |
| 119 | 1 | **type** `private record Run(String label, int months, long seconds, List<Harbour> harbours, String overview, List<St...` | What one city's run came to: for the README. |
| 121 | 115 | `public static void main(String[] args) throws IOException` |  |

### the cities (lines 237-346)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 8 | `private static Game play(String label, int months, PrintStream silent)` | A Bergen city played `months` months in a scratch folder. |
| 253 | 44 | `private static Run harbourPicture(Game g, String label, Path outDir, String name, Map<Integer, BuildingsTemplate> templates) th...` | The city's harbours, its largest drawn at 2 px a plot to `name`, and what its drawing shows. |
| 299 | 16 | `private static String spacing(Harbour h)` | How near a harbour's works lie: the distance from each work's shore plot to its nearest neighbour's, in plots, mean and greatest. |
| 317 | 10 | `private static String kinds(Harbour h, Map<Integer, BuildingsTemplate> templates)` | A harbour's works by the name of their building, in the order first laid: "6 Bulk Berth, 3 General Cargo Terminal". |
| 329 | 17 | `private static List<Harbour> harbours(CityShore shore, World world)` | CityShore.harbours() asked of what it returns: each work, in the order laid, in the first harbour on its landmass within the reach of that harbour's first work. |

### the window (lines 347-408)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 1 | `private static long tileOf(long plot)` |  |
| 352 | 5 | `private static boolean inside(CityShore.Work w, long tx, long ty, int seaward)` | Whether the work's box, and `seaward` plots past its sea side, lie wholly in the window of ACROSS x DOWN tiles from tile (tx, ty). |
| 359 | 3 | `private static boolean meets(CityShore.Work w, long tx, long ty)` | Whether the work's box meets the window. |
| 367 | 23 | `private static long[] bestWindow(List<CityShore.Work> works)` | The top-left tile of the window of ACROSS x DOWN tiles with the most works wholly in it (box and seaward run), then the most touching it, then the middle of its whole works nearest its own middle; null with no work. |
| 392 | 16 | `private static BufferedImage tiles(CityMap map, long tx0, long ty0, int across, int down, int px)` | A window of across x down tiles from tile (tx0, ty0), as the screen draws it at px a plot. |

### the berths and their boats (lines 409-467)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 6 | `private static Ports.Ship shipFor(BuildingsTemplate t, Ports.Cargo cargo)` | The ship a terminal's boats are: its first class (Ports.shipsOf()), else the first of its cargo. |
| 425 | 30 | `private static List<Docked> docked(Game g, CityMap map, Map<Integer, BuildingsTemplate> templates, double scale)` | The city's berths in CityShore.berths(types)'s order, each with its work (the berthed works whose type has a berth, in the order laid) and its boat: a laden call of the ship the game's schedule would bring (a dry bulk... |
| 457 | 5 | `private static BoatSchedule.Boat bowOut(BoatSchedule.Berth b, Ports.Ship ship, int i)` | A laden call of `ship` coming in on route i, docked at the berth's point (+0.5, +0.5) as the schedule places it, bow out (heading out to sea, the way the berth's side points); i varies a box ship's boxes' colours and ... |
| 464 | 3 | `private static BoatSchedule.Boat dock(BoatSchedule.Berth b, Ports.Ship ship, int i, double scale)` | ...and a lone boat of the specimen sheet put alongside at `scale` (BoatSchedule.alongside(): what berthed() gives a boat with nothing near it). |

### the drawing (lines 468-634)

| line | len | member | says |
|---:|---:|---|---|
| 470 | 1 | `private static Color col(int argb)` |  |
| 472 | 3 | `private static Color col(int argb, double alpha)` |  |
| 477 | 6 | `private static int mix(int a, int b, double t)` | t of b over a, per channel. |
| 484 | 4 | `private static void aa(Graphics2D g, boolean on)` |  |
| 490 | 20 | `private static void paint(Graphics2D g, Frame f, CityShore shore, BuildingVisual.Type[] types, List<Docked> docked, char variant)` | The window's overlay: the quay walls, then by variant (a: nothing; b: the yards; c: the sheds), the boats, and for c the cranes. |
| 512 | 8 | `private static void walls(Graphics2D g, Frame f, List<CityShore.Wall> walls)` | The harbour's quays as MapView.harbourQuay() draws them: the walls filled QUAY_DECK and, from TileRaster.EDGE_FROM px a plot, edged QUAY_EDGE. |
| 522 | 18 | `private static void boat(Graphics2D g, Frame f, BoatSchedule.Boat b, double scale)` | A boat as MapView.boats() draws it above LEAST_PX: hull, deck marks (a box ship's boxes in their colours), bridge, and the hull's outline. |
| 541 | 9 | `private static Path2D poly(Frame f, double[][] pts)` |  |
| 555 | 64 | **type** `private static final class Local` | A work's box in its own frame, in pixels: u along the sea side (left to right as the sea is faced), v from the land side (0) out to the sea side (D). |
| 560 | 25 | `Local(CityShore.Work w, Frame f)` _(in HarbourRenders.Local)_ |  |
| 586 | 1 | `double X(double u, double v)` _(in HarbourRenders.Local)_ |  |
| 588 | 1 | `double Y(double u, double v)` _(in HarbourRenders.Local)_ |  |
| 591 | 1 | `double uOf(double wx, double wy)` _(in HarbourRenders.Local)_ | The u of a point given in world plots. |
| 593 | 11 | `void rect(Graphics2D g, double u0, double v0, double u1, double v1, int argb)` _(in HarbourRenders.Local)_ |  |
| 606 | 4 | `void framed(Graphics2D g, double u0, double v0, double u1, double v1, int fill, int edge)` _(in HarbourRenders.Local)_ | A rectangle with a 1 px edge. |
| 611 | 7 | `void ellipse(Graphics2D g, double cu, double cv, double ru, double rv, int argb)` _(in HarbourRenders.Local)_ |  |
| 621 | 13 | `private static Kind kindOf(CityShore.Work w, BuildingVisual.Type[] types)` | What a work's box is drawn as, or null for a work this tool leaves as it is. |

### b: the yards (lines 635-729)

| line | len | member | says |
|---:|---:|---|---|
| 641 | 12 | `private static void yard(Graphics2D g, CityShore.Work w, Frame f, Kind kind)` | A yard drawn over its box. |
| 655 | 4 | `private static int boxColour(int k)` | The six colours of a box ship's boxes, from ShipShapes.boxColour(). |
| 661 | 14 | `private static void containers(Graphics2D g, Local q, Random r)` | Rows of small coloured boxes in blocks with lanes between, an apron left by the quay. |
| 677 | 11 | `private static void heaps(Graphics2D g, Local q, Random r)` | Two or three heaps, ochre or grey, each a darker ring and a lighter crown. |
| 690 | 14 | `private static void tanks(Graphics2D g, Local q)` | Round tanks on a grid, each a rim, a roof and a hatch. |
| 706 | 16 | `private static void crates(Graphics2D g, Local q, Random r)` | One shed along the land side, stacked crates in groups between it and the apron. |
| 723 | 6 | `private static void shedRoof(Graphics2D g, Local q, double u0, double v0, double u1, double v1)` |  |

### c: the sheds and the cranes (lines 730-771)

| line | len | member | says |
|---:|---:|---|---|
| 733 | 4 | `private static void shed(Graphics2D g, Local q)` | A long transit shed along the sea side of the box, set back 1.5 plots from the wall. |
| 739 | 13 | `private static void cranes(Graphics2D g, Docked d, Frame f)` | A terminal's 2 or 3 quay cranes on its wall, about its boat. |
| 757 | 14 | `private static void crane(Graphics2D g, Local q, double u)` | One gantry crane, seen from above: its portal across the wall - four legs and the beams between them, dark - and its boom, a dark girder running out over the water to the ship (and a short back-reach over the box), wi... |

### the specimen sheet (lines 772-845)

| line | len | member | says |
|---:|---:|---|---|
| 782 | 63 | `private static BufferedImage kindsSheet(CityMap map, Map<Integer, BuildingsTemplate> templates, List<String> lines)` | A sheet of every kind of terminal (and one tank farm), a row each, in the three drawings (a, b, c across): each box at its real size (BuildingVisual.footprint()) on a straight shore with the sea to the north, with its... |

### what the drawing shows (lines 846-983)

| line | len | member | says |
|---:|---:|---|---|
| 855 | 42 | `private static List<String> audit(Game g, CityMap map, Map<Integer, BuildingsTemplate> templates, double scale)` | What a city's boats and walls come to at `scale` px a plot, after BoatSchedule.berthed(): how many boats lie alongside, how many stern-to and how many are as the schedule placed them (centred on the berth point); how ... |
| 898 | 3 | `private static String cargoPair(Docked a, Docked b)` |  |
| 902 | 3 | `private static String listed(List<String> l)` |  |
| 907 | 5 | `private static boolean overlap(Area a, Area b)` | Whether two shapes go deeper into one another than BoatSchedule.CROSS_TOLERANCE: the narrowest width of the part they share (thickness()) is more than it. |
| 914 | 22 | `private static double thickness(Area c)` | The narrowest width of a convex area (the part two convex shapes share): across each of its edges, the spread of its points along the edge's normal, the least. |
| 942 | 35 | `private static List<String> wallLines(CityShore shore)` | The walls against the quays they stand for. |
| 978 | 5 | `private static void write(Path dir, String name, BufferedImage img) throws IOException` |  |

