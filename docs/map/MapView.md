# MapView.java - 2,120 lines · 94 methods · 30 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

> The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, the far nodes from the world's ground and the district pictures (0.7.128), and expanded on the real Earth a globe (0.7.135) - with the city's block lines, its edge, its offers hatched and numbered by place, and its deposits over it, a scale bar, and what is under the pointer.
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
> + and - zoom, 0 fits, Esc closes, and since 0.7.135 W, A, S and D pan
> (the arrows stay the clock's speed, as on City History's full screen);
> a legend and a hover
> card (the ground, whose it is - the centre, a purchase, an offer and its
> price - the fields and their tonnes, and at L0 the building or road; on
> Earth, since 0.7.119, how far and which way from the nearest place, and
> the geologic province: LandMap.earthWords()).
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
> (CityMap.adopt()); and since 0.7.128 (batch MM1) the far nodes' district
> pictures, drawn with those plans: a node drawn without a picture it would
> sample (MapTiles.nodePixels()) hands out its district's plan jobs
> (CityMap.pictureJobs()) while the view's districts fit what the map keeps
> (MapTiles.picturesFit()), the frame's in one task on WORKER, and is drawn
> again once they are kept. Nothing on WORKER touches a node of the scene.
> 
> THE OVERLAY ON THE BLOCK GRID (0.7.69, batch M5; the project's
> spec-grid.md 2.4): the offers are rectangles of whole blocks, hatched on
> their free ground and numbered by place as the office's rows are; the
> city's edge is runs along block lines, drawn crisp and stepped on the
> pixels just inside it; and the city's block grid is drawn faint where a
> block is big enough to see. Where each falls on the screen's pixels is
> MapFrame's arithmetic (onScreen(), runOnScreen(), blockLines()), so
> MapCheck holds it; the edge and each offer's cut-outs are found once a
> ... (42 more lines in the source)

**Uses:** [MapFrame](MapFrame.md) (61), [Palette](Palette.md) (61), [LandMap](LandMap.md) (35), [GlobeFrame](GlobeFrame.md) (32), [World](World.md) (22), [GlobePicture](GlobePicture.md) (16), [MapTiles](MapTiles.md) (15), [ShipShapes](ShipShapes.md) (14), [CityMap](CityMap.md) (13), [CityLand](CityLand.md) (13), [Game](Game.md) (12), [LandMarket](LandMarket.md) (9), [BoatSchedule](BoatSchedule.md) (8), [LandParcel](LandParcel.md) (8), [TilePainter](TilePainter.md) (6), [UserInterface](UserInterface.md) (3), [Deposit](Deposit.md) (3), [Icons](Icons.md) (2), [TileRaster](TileRaster.md) (2), [Ports](Ports.md) (2), [LandGrid](LandGrid.md) (2), [Resource](Resource.md) (2), [MiniMapFrame](MiniMapFrame.md) (2), [EarthData](EarthData.md) (1), [HistoryScreen](HistoryScreen.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (3):** [LandScreen](LandScreen.md), [MiniMap](MiniMap.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 204 | · what it draws from |
| 243 | · what the player is doing |
| 254 | · the boats (0.7.97) |
| 271 | · the globe (0.7.135, batch E9) |
| 421 | A FRAME OF THE CLOCK (tick()) |
| 739 | THE DRAWING |
| 776 | THE BOATS (0.7.97, batch O13) |
| 1188 | THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) |
| 1664 | · the keys (0.7.135; spec-globe 3.11) |
| 1705 | THE POINTER AND THE KEYS |
| 1872 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 143 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 146 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 149 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 152 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 155 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 158 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 161 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 164 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 170 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 173 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 176 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 182 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), ... |
| 300 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 351 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 354 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag or W, A, S and D to pan, scroll to zoom,...` | The Expand button's tooltip. |
| 357 | `MapView.HINT` | `"drag or W A S D to pan · scroll to zoom · 0 fits · Esc closes"` | The expanded map's hint, at the right of its head. |
| 951 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 954 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 957 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1165 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 1193 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe: the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 1196 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 1199 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 1202 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 1531 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 1864 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 2033 | `MapView.LEGEND_ROWS` | `12` | Rows a column of the legend holds: 12, its 35 entries in three columns (since 0.7.127 ice and the desert, 33 before; since 0.7.97, nine for the shore and the sea; 24 in two before; 11 until 0.7.72 added the railway to... |
| 2036 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 2039 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's tracks (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 2057 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 167 | `static Game.MapDraft drafting` | The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. |
| 189 | `private final UserInterface ui` |  |
| 190 | `private final Picker picker` |  |
| 191 | `private final Runnable onClose` |  |
| 192 | `private final Canvas canvas` |  |
| 193 | `private final StackPane smallBox` |  |
| 194 | `private final Label readout` |  |
| 195 | `private final MapFrame small` |  |
| 196 | `private MapFrame big` |  |
| 197 | `private boolean expanded` |  |
| 199 | `private VBox bigPane` |  |
| 200 | `private StackPane bigBox` |  |
| 201 | `private VBox card` |  |
| 202 | `private Label clockLine, bigLine` |  |
| 206 | `private CityMap shownMap` |  |
| 207 | `private MapTiles tiles` |  |
| 208 | `private Game.MapDraft draft` |  |
| 209 | `private int draftsFailed` |  |
| 210 | `private long version` |  |
| 211 | `private long fittedFor` |  |
| 212 | `private final TilePainter.Input in` |  |
| 213 | `private int[] buffer` |  |
| 217 | `WritableImage image` |  |
| 218 | `long stamp, version` |  |
| 219 | `byte[] ground` |  |
| 220 | `boolean partial` |  |
| 221 | `long pictures` |  |
| 223 | `int[] pixels` | A far node's colours as drawn (0.7.135): what the globe reprojects near the city. |
| 228 | `long most` |  |
| 233 | `private final Pictures l0small` |  |
| 235 | `private final Set<Long> nodesAsked` |  |
| 237 | `private final Set<CityMap.District> picturesAsked` | The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. |
| 240 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 241 | `private long fieldsFor` |  |
| 245 | `private int side` | the side the office shows, and the offer picked |
| 246 | `private int litSide` | the side the office shows, and the offer picked |
| 247 | `private int overSide` | a row hovered in the office |
| 248 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 249 | `private boolean pressed` |  |
| 250 | `private double[] hoverAt` |  |
| 252 | `private boolean running, dirty` |  |
| 257 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 259 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 260 | `private int scheduleMonth` |  |
| 261 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 263 | `private boolean boatsLive` | Whether boats are in view (the frame goes on), and the clock they were last drawn at. |
| 264 | `private double drawnClock` |  |
| 265 | `private final AnimationTimer pump` |  |
| 269 | `private final ImagePattern hatch` |  |
| 274 | `private GlobeFrame globe` | The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. |
| 275 | `private World.Key globeKey` |  |
| 276 | `private EarthData globeData` |  |
| 278 | `private long reachFor` | The land and offers its reach was found for. |
| 281 | `private WritableImage globeImg` | The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. |
| 282 | `private int[] globePx` |  |
| 283 | `private GlobeFrame globeDrawn` |  |
| 284 | `private int globeMode` |  |
| 285 | `private long globeInputs` |  |
| 287 | `private long inputs` | Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. |
| 289 | `private double[] patch` | The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. |
| 292 | `private byte[] pieceCls` | The GROUND tier's pieces past the plane, for one view at rest: its classes a pixel (-1 not in yet), the view and its number. |
| 293 | `private GlobeFrame pieceView` |  |
| 294 | `private volatile int pieceGen` |  |
| 296 | `private int[] carryPx` | The last whole picture at rest, carried pixel by pixel while the next is drawn. |
| 297 | `private GlobeFrame carryView` |  |
| 303 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when the keys last moved the view (0 when none is held). |
| 304 | `private long keyNanos` |  |
| 307 | `private int legendTier` | The tier the legend was laid out for. |
| 1051 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 1052 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 1053 | `private long shapesFor` |  |
| 1056 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 1057 | `private long labelsFor` |  |
| 1058 | `private double labelsAtScale` |  |
| 1205 | `private boolean globeHalf` | The globe drawn while it was half its resolution (the view on the move). |
| 1296 | `private GlobeFrame globeNodesFor` |  |
| 1297 | `private long[] globeNodesAt` |  |
| 2059 | `private final Label legendNote` |  |
| 2060 | `private final GridPane legendGrid` |  |
| 2061 | `private final Label shipsLine` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 140 | 1981 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 179 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 179 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 204-242)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 9 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count ... |
| 227 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 229 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 230 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 243-253)

### the boats (0.7.97) (lines 254-270)

### the globe (0.7.135, batch E9) (lines 271-420)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 40 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 360 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 363 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 366 | 8 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 375 | 22 | `private void reset()` |  |
| 399 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 406 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 413 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 421-738)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 39 | `private void tick()` |  |
| 474 | 53 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 529 | 9 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 540 | 8 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 549 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 564 | 39 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - stamped again, and painted only when the stamp moved - from the middle of the view out; at FAR ... |
| 605 | 17 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 624 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 634 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 639 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 643 | 8 | `private boolean paintNodes(MapFrame f, long deadline)` |  |
| 662 | 30 | `private void askPictures(MapFrame f)` | The district pictures the far nodes drawn this frame lacked (0.7.128, batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order, while the view's districts fit wha... |
| 694 | 44 | `private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept)` | The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). |

### THE DRAWING (lines 739-775)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 32 | `private void draw()` |  |

### THE BOATS (0.7.97, batch O13) (lines 776-1187)

| line | len | member | says |
|---:|---:|---|---|
| 781 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 793 | 67 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: each route on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), ... |
| 862 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 867 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 873 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 880 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 885 | 19 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 905 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 935 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 975 | 74 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched rectangles - each its free ground only, the city's own ground... |
| 1060 | 11 | `private void shapes(CityLand land, LandMarket market)` |  |
| 1073 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 1085 | 26 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 1113 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 1140 | 4 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 1146 | 17 | `private static void scaleBar(GraphicsContext gc, double h, double px, String words)` | ...drawn: `px` long at the bottom left of a view `h` high, and its words. |
| 1168 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 1173 | 3 | `static String css(int argb)` | ...as CSS. |
| 1178 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |

### THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) (lines 1188-1663)

| line | len | member | says |
|---:|---:|---|---|
| 1208 | 1 | `private boolean onGlobe()` | Whether the expanded map draws the globe: on an Earth world, wider than the handoff. |
| 1211 | 1 | `private boolean globeShown()` | ...or lays its picture over the plane's in the crossfade's notch. |
| 1214 | 7 | `private void resetGlobe()` | A new city, or another world: no globe until the map is over the window on Earth again. |
| 1223 | 13 | `private void resetGlobePicture()` | What the globe drew from and drew: another map, or another view of the world. |
| 1238 | 24 | `private void globeUp(CityLand land, LandMarket market, long landKey)` | The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. |
| 1264 | 4 | `private void follow()` | The view moved on the globe: at the plane's scales the plane's frame follows it; the view drawn again. |
| 1270 | 25 | `private long[] globeNodes(GlobeFrame v)` | The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws; null where it draws none. |
| 1300 | 14 | `private boolean paintGlobe(long deadline)` | A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack. |
| 1316 | 14 | `private void nodeColoured(long x0, long y0, long span)` | A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. |
| 1332 | 9 | `private void addPatch(double x0, double y0, double x1, double y1)` | The still picture's box to be drawn again, grown to take in another. |
| 1350 | 32 | `private void composeGlobe(int mode)` | The globe's picture of the view, drawn again when the view, the way it is drawn or what it is drawn from moved: at half resolution while the view moves (its frame a copy of half the size at twice the km a pixel), at i... |
| 1384 | 35 | `private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1)` | The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image. |
| 1421 | 18 | `private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic)` | Past the plane, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. |
| 1441 | 7 | `private void drawGlobeImage(GraphicsContext gc)` | The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). |
| 1456 | 39 | `private void askPieces(GlobeFrame view)` | The GROUND tier's pieces past the plane for a view come to rest: the far rule through the camera (GlobeFrame.groundPiece()) a PIECE_PX square at a time on WORKER, from the middle out, each one with some of its pixels ... |
| 1497 | 7 | `private void pieceIn(int gen, int[] q, byte[] b)` | A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. |
| 1506 | 23 | `private void drawGlobe()` | The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its edge through the camera in the PICTURE tier once it is EDGE_FROM_PX across, its ring - ... |
| 1534 | 10 | `private double[][] onGlobe(double[] xs, double[] ys)` | A shape of plots through the camera: its corners on the screen, or null where one is not seen. |
| 1546 | 50 | `private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market)` | The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - hatched, edged in pink, the office's side bright, the one lit or picked, numbered ... |
| 1598 | 12 | `private void globeEdge(GraphicsContext gc, CityLand land)` | The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. |
| 1612 | 11 | `private void ring(GraphicsContext gc, CityLand land)` | The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. |
| 1625 | 4 | `private void globeScaleBar(GraphicsContext gc)` | The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. |
| 1631 | 20 | `private void globeHover(double sx, double sy)` | The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it. |
| 1653 | 10 | `private byte drawnAt(double sx, double sy)` | The ground drawn at a screen point on the globe, past the plane: the GROUND tier's piece where one is in, else the planet's picture's texel. |

### the keys (0.7.135; spec-globe 3.11) (lines 1664-1704)

| line | len | member | says |
|---:|---:|---|---|
| 1667 | 23 | `private boolean keys()` | A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not h... |
| 1692 | 12 | `boolean panKey(KeyCode k, boolean down)` | W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. |

### THE POINTER AND THE KEYS (lines 1705-1871)

| line | len | member | says |
|---:|---:|---|---|
| 1709 | 63 | `private void handlers()` |  |
| 1774 | 6 | `private void zoomBig(double sx, double sy, double notches)` | The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. |
| 1782 | 17 | `private void click(double sx, double sy)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on - on the globe only where the city's plane is drawn (0.7.135). |
| 1801 | 38 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 1841 | 21 | `private void showCard(List<String> lines, double sx, double sy, double vw, double vh)` | The expanded map's hover card: its lines, the first bold, by the pointer and inside the view. |
| 1867 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 1872-2120)

| line | len | member | says |
|---:|---:|---|---|
| 1876 | 15 | `void expand()` |  |
| 1898 | 11 | `void expandOn(MiniMapFrame.Box box)` | Expanded on a box of plots (0.7.134, batch MM2): the minimap's click, the map over the window fitted on what the stamp frames (MiniMapFrame.open()) rather than on the land office's opening; Fit goes back to that. |
| 1911 | 15 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked. |
| 1927 | 37 | `private void buildPane()` |  |
| 1966 | 7 | `private void picked()` | The picked side and offer on the expanded map's head. |
| 1975 | 22 | `private void fitPane()` | The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). |
| 1999 | 10 | `private void openBig()` | The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. |
| 2011 | 10 | `private void fitBig()` | Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. |
| 2023 | 5 | `static List<String[]> legendEntries(int tier)` | The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. |
| 2030 | 1 | `static List<String[]> legendEntries()` | ...the plane's, at every scale on a generated world. |
| 2042 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed without a street. |
| 2047 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 2052 | 3 | `static String trackWords(long plots)` | ...and (0.7.89) when the railway's runs found no ground for some of its track: how much, in plots. |
| 2063 | 16 | `private VBox legend()` |  |
| 2081 | 10 | `private void legendFor(int tier)` | The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. |
| 2093 | 1 | `private int shownTier()` | The tier the expanded map shows: the globe's on Earth, the plane at every scale on a generated world. |
| 2096 | 14 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. |
| 2112 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

