# MapView.java - 1,338 lines · 58 methods · 24 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, the far nodes from the world's ground - with the city's block lines, its edge, its offers hatched and numbered by place, and its deposits over it, a scale bar, and what is under the pointer.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
> 2.8). Jerus: click a side to see its ten offers (six since 0.7.67), the
> map always in view in the land office, an Expand that fills the window
> and pans and zooms.
> The model's map (CityMap, J3 and J3b) counts the city's buildings by
> district and paints any tile from them; this draws it. What it draws by -
> the transform, the level of detail, the tiles a screen asks for, what a
> click picks, a tile's stamp - is arithmetic in the model's package
> (MapFrame, LandMap, MapTiles), which MapCheck holds; this class is the
> toolkit's half, checked by eye on the PC.
> 
> TWO VIEWS, ONE CANVAS AND ONE SET OF CACHES (spec-land 2.6): the small map
> opens on the city's ground with MapFrame.OPENING_MARGIN round it and stays
> there (star: the page scrolls under the wheel, so the small map neither
> pans nor zooms; a click selects a side or an offer, a hover names what is
> under the pointer and lights an offer). Expand moves the canvas
> into UserInterface's pane over the whole window (City History's, 0.7.23):
> drag pans, the wheel zooms by MapFrame.ZOOM_STEP a notch at the pointer,
> + and - zoom, 0 fits, Esc closes (the arrows stay the clock's speed, as
> on City History's full screen); a legend and a hover
> card (the ground, whose it is - the centre, a purchase, an offer and its
> price - the fields and their tonnes, and at L0 the building or road).
> 
> PAINTING IN THE BACKGROUND ON THE FX THREAD (spec-land 2.6): each frame
> paints the tiles in view that have no picture, or whose stamp moved, from
> the middle out, for at most FRAME_MS; a tile not painted yet shows another
> level's picture of it meanwhile, the coarser first. A month, or a
> purchase, changes the map's version (MapTiles.version()); the view then
> stamps the tiles it shows again, and repaints only those whose stamp moved.
> 
> OFF THE FX THREAD, ONLY ARITHMETIC (the orchestrator's brief): CityMap is
> not thread-safe, so the first draw of a city's map is a draft - the land
> copied, the counts as they stood (Game.mapDraft()) - drawn on WORKER and
> kept back on the FX thread (Game.adoptMap()); the far nodes' ground is
> read from the world (MapTiles.nodeTerrain(), World is safe) on WORKER and
> coloured on the FX thread; and since 0.7.88 (batch RD2) the district plans
> the tiles are painted from: a tile whose plans are not drawn
> (MapTiles.ready()) hands out their jobs (MapTiles.jobs(): the inputs read
> here, the ground read and the plan drawn on WORKER, in order) and shows
> another level's picture until they come back and are kept here
> (CityMap.adopt()). Nothing on WORKER touches a node of the scene.
> 
> THE OVERLAY ON THE BLOCK GRID (0.7.69, batch M5; the project's
> spec-grid.md 2.4): the offers are rectangles of whole blocks, hatched on
> their free ground and numbered by place as the office's rows are; the
> city's edge is runs along block lines, drawn crisp and stepped on the
> pixels just inside it; and the city's block grid is drawn faint where a
> block is big enough to see. Where each falls on the screen's pixels is
> MapFrame's arithmetic (onScreen(), runOnScreen(), blockLines()), so
> MapCheck holds it; the edge and each offer's cut-outs are found once a
> purchase or a listing, not every frame.
> 
> THE BOATS (0.7.97, batch O13; runs/spec-oil.md 2.10, spec-roads-and-ports.md
> 4): over the tiles, each terminal's lane out to sea - its route (CityMap's
> THE SEA ROUTES, found on WORKER and kept here) - from L2 in, and from L1 in
> the boats on it: the month's calls (BoatSchedule) where the game's clock
> puts them, a boat-month BOAT_GAME_MONTHS of the game's, so a month of
> ships plays as BoatSchedule.MONTH_SECONDS at 1x and stands while the clock
> ... (2 more lines in the source)

**Uses:** [Palette](Palette.md) (53), [MapFrame](MapFrame.md) (49), [LandMap](LandMap.md) (28), [ShipShapes](ShipShapes.md) (18), [World](World.md) (16), [MapTiles](MapTiles.md) (13), [BuildingVisual](BuildingVisual.md) (11), [Game](Game.md) (9), [BoatSchedule](BoatSchedule.md) (8), [TileRaster](TileRaster.md) (8), [CityLand](CityLand.md) (7), [LandParcel](LandParcel.md) (7), [CityMap](CityMap.md) (6), [TilePainter](TilePainter.md) (6), [LandMarket](LandMarket.md) (5), [Resource](Resource.md) (4), [UserInterface](UserInterface.md) (3), [Deposit](Deposit.md) (3), [Icons](Icons.md) (2), [Ports](Ports.md) (2), [HistoryScreen](HistoryScreen.md) (1), [LandGrid](LandGrid.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (1):** [LandScreen](LandScreen.md)

## Sections

| line | section |
|---:|---|
| 161 | · what it draws from |
| 194 | · what the player is doing |
| 205 | · the boats (0.7.97) |
| 330 | A FRAME OF THE CLOCK (tick()) |
| 571 | THE DRAWING |
| 593 | THE BOATS (0.7.97, batch O13) |
| 997 | THE POINTER AND THE KEYS |
| 1132 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 103 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 106 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 109 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 112 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 115 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 118 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 121 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 124 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 127 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 130 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 133 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 139 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground. |
| 264 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 267 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag to pan, scroll to zoom, Esc to come back."` | The Expand button's tooltip. |
| 270 | `MapView.HINT` | `"drag to pan · scroll to zoom · 0 fits · Esc closes"` | The expanded map's hint, at the right of its head. |
| 768 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 771 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 774 | `MapView.EDGE_ALPHA` | `0.55` | The city's edge's white, its alpha: 0.55, as since 0.7.61. |
| 974 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 1124 | `MapView.CARD_FIELDS` | `3` | The most fields a hover card lists: 3. |
| 1268 | `MapView.LEGEND_ROWS` | `12` | Rows a column of the legend holds: 12, its 33 entries in three columns (since 0.7.97, nine for the shore and the sea; 24 in two before; 11 until 0.7.72 added the railway to its 22; 0.7.88 the tracks). |
| 1271 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 1274 | `MapView.TRACK_ENTRY` | `"Track"` | The legend's tracks (0.7.88; spec 5): a street the city has bought no road for. |
| 1292 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 146 | `private final UserInterface ui` |  |
| 147 | `private final Picker picker` |  |
| 148 | `private final Runnable onClose` |  |
| 149 | `private final Canvas canvas` |  |
| 150 | `private final StackPane smallBox` |  |
| 151 | `private final Label readout` |  |
| 152 | `private final MapFrame small` |  |
| 153 | `private MapFrame big` |  |
| 154 | `private boolean expanded` |  |
| 156 | `private VBox bigPane` |  |
| 157 | `private StackPane bigBox` |  |
| 158 | `private VBox card` |  |
| 159 | `private Label clockLine, bigLine` |  |
| 163 | `private CityMap shownMap` |  |
| 164 | `private MapTiles tiles` |  |
| 165 | `private Game.MapDraft draft` |  |
| 166 | `private int draftsFailed` |  |
| 167 | `private long version` |  |
| 168 | `private long fittedFor` |  |
| 169 | `private final TilePainter.Input in` |  |
| 170 | `private int[] buffer` |  |
| 174 | `WritableImage image` |  |
| 175 | `long stamp, version` |  |
| 176 | `byte[] ground` |  |
| 181 | `long most` |  |
| 186 | `private final Pictures l0small` |  |
| 188 | `private final Set<Long> nodesAsked` |  |
| 191 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 192 | `private long fieldsFor` |  |
| 196 | `private int side` | the side the office shows, and the offer picked |
| 197 | `private int litSide` | the side the office shows, and the offer picked |
| 198 | `private int overSide` | a row hovered in the office |
| 199 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 200 | `private boolean pressed` |  |
| 201 | `private double[] hoverAt` |  |
| 203 | `private boolean running, dirty` |  |
| 208 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 210 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 211 | `private int scheduleMonth` |  |
| 212 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 214 | `private boolean boatsLive` | Whether boats are in view (the frame goes on), and the clock they were last drawn at. |
| 215 | `private double drawnClock` |  |
| 216 | `private final AnimationTimer pump` |  |
| 220 | `private final ImagePattern hatch` |  |
| 864 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 865 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 866 | `private long shapesFor` |  |
| 869 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 870 | `private long labelsFor` |  |
| 871 | `private double labelsAtScale` |  |
| 1294 | `private final Label legendNote` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 100 | 1239 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 136 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 136 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 161-193)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 5 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground. |
| 180 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 182 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 183 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 194-204)

### the boats (0.7.97) (lines 205-329)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 40 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 273 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 276 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 279 | 7 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 287 | 19 | `private void reset()` |  |
| 308 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 315 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 322 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 330-570)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 29 | `private void tick()` |  |
| 371 | 47 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 420 | 8 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 430 | 8 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 439 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 454 | 38 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - stamped again, and painted only when the stamp moved - from the middle of the view out; at FAR ... |
| 494 | 17 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 513 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 523 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 528 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 532 | 38 | `private boolean paintNodes(MapFrame f, long deadline)` |  |

### THE DRAWING (lines 571-592)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 17 | `private void draw()` |  |

### THE BOATS (0.7.97, batch O13) (lines 593-996)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 610 | 67 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: each route on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), ... |
| 679 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 684 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 690 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 697 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 702 | 19 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 722 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 752 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 792 | 70 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched rectangles - each its free ground only, the city's own ground... |
| 873 | 11 | `private void shapes(CityLand land, LandMarket market)` |  |
| 886 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 898 | 26 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 926 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 953 | 19 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 977 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 982 | 3 | `static String css(int argb)` | ...as CSS. |
| 987 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |

### THE POINTER AND THE KEYS (lines 997-1131)

| line | len | member | says |
|---:|---:|---|---|
| 1001 | 57 | `private void handlers()` |  |
| 1060 | 11 | `private void click(double sx, double sy)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on. |
| 1073 | 49 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 1127 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 1132-1338)

| line | len | member | says |
|---:|---:|---|---|
| 1136 | 14 | `void expand()` |  |
| 1152 | 12 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked. |
| 1165 | 37 | `private void buildPane()` |  |
| 1204 | 7 | `private void picked()` | The picked side and offer on the expanded map's head. |
| 1213 | 18 | `private void fitPane()` | The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). |
| 1233 | 5 | `private void fitBig()` | The expanded map on the city, as the small map opens. |
| 1240 | 26 | `static List<String[]> legendEntries()` | The legend's entries: the ten classes and flats, the three roads, (0.7.88) the tracks and (0.7.72) the railway, the land, the six resources in fields. |
| 1277 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed without a street. |
| 1282 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 1287 | 3 | `static String trackWords(long plots)` | ...and (0.7.89) when the railway's runs found no ground for some of its track: how much, in plots. |
| 1296 | 20 | `private VBox legend()` |  |
| 1318 | 10 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for. |
| 1330 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

