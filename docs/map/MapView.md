# MapView.java - 2,996 lines · 132 methods · 40 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> ... (76 more lines in the source)

**Uses:** [Palette](Palette.md) (74), [MapFrame](MapFrame.md) (70), [LandMap](LandMap.md) (54), [GlobeFrame](GlobeFrame.md) (36), [World](World.md) (24), [Game](Game.md) (21), [CityMap](CityMap.md) (20), [LandParcel](LandParcel.md) (19), [GlobePicture](GlobePicture.md) (16), [MapTiles](MapTiles.md) (15), [ShipShapes](ShipShapes.md) (14), [CityLand](CityLand.md) (13), [LandMarket](LandMarket.md) (11), [TrainSchedule](TrainSchedule.md) (10), [TruckSchedule](TruckSchedule.md) (10), [BoatSchedule](BoatSchedule.md) (8), [TrainShapes](TrainShapes.md) (8), [TilePainter](TilePainter.md) (6), [Deposit](Deposit.md) (4), [TileRaster](TileRaster.md) (4), [TruckShapes](TruckShapes.md) (4), [UserInterface](UserInterface.md) (3), [HistoryScreen](HistoryScreen.md) (3), [Ports](Ports.md) (3), [Icons](Icons.md) (2), [LandGrid](LandGrid.md) (2), [Resource](Resource.md) (2), [LandScreen](LandScreen.md) (2), [MiniMapFrame](MiniMapFrame.md) (2), [EarthData](EarthData.md) (1)... and 4 more

**Used by (3):** [LandScreen](LandScreen.md), [MiniMap](MiniMap.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 238 | · what it draws from |
| 277 | · what the player is doing |
| 286 | · buying from the map (0.7.137, batch M6) |
| 312 | · the boats (0.7.97) |
| 333 | · the globe (0.7.135, batch E9) |
| 503 | A FRAME OF THE CLOCK (tick()) |
| 827 | THE DRAWING |
| 871 | THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) |
| 956 | THE BOATS (0.7.97, batch O13) |
| 1041 | THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 1101 | · the trucks (0.8.8, batch FR5; spec-freight 4.7, D21) |
| 1384 | · the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") |
| 1567 | THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) |
| 2052 | · the keys (0.7.135; spec-globe 3.11) |
| 2093 | THE POINTER AND THE KEYS |
| 2304 | BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 2704 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 177 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 180 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 183 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 186 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 189 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 192 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 195 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 198 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 204 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 207 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 210 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 216 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), ... |
| 362 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 413 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 416 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag or W, A, S and D to pan, scroll to zoom,...` | The Expand button's tooltip. |
| 419 | `MapView.HINT` | `"click an offer to buy it"` | The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), every key in its tooltip (KEYS_TIP). |
| 422 | `MapView.KEYS_TIP` | `"Click an offer to open its card beside it; shift-click adds an offer to the ...` | ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. |
| 427 | `MapView.SELECT_EVERY` | `"Select every offer"` | The head's chip that picks every offer listed (B7). |
| 1104 | `MapView.HIGHWAYS_SALT` | `0x7472_7563_6b73_0001L` | The highways' schedule's salt: apart from every district's, which is its key (CityMap.DistrictRoutes.key()), so two schedules of one month do not hash alike. |
| 1269 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1272 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 1275 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1278 | `MapView.WORLD_LINE_ALPHA` | `LandMap.WORLD_ALPHA` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. |
| 1477 | `MapView.NEW_TAG` | `"NEW"` | The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. |
| 1544 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 1572 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe: the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 1575 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 1578 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 1581 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 1914 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 2268 | `MapView.CLICK_TO_BUY` | `"Click to buy it here"` | The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. |
| 2296 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 2335 | `MapView.CARD_GAP` | `14` | The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). |
| 2338 | `MapView.CARD_MARGIN` | `10` | ...and at least this far inside the view: 10, the hover card's (showCard()). |
| 2341 | `MapView.BOX_FILL_ALPHA` | `0.12` | The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. |
| 2883 | `MapView.LEGEND_ROWS` | `17` | Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 the transport class, t... |
| 2886 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 2889 | `MapView.TRUCK_WORDS` | `"Trucks: one per %s"` | The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). |
| 2897 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 2915 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 201 | `static Game.MapDraft drafting` | The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. |
| 223 | `private final UserInterface ui` |  |
| 224 | `private final Picker picker` |  |
| 225 | `private final Runnable onClose` |  |
| 226 | `private final Canvas canvas` |  |
| 227 | `private final StackPane smallBox` |  |
| 228 | `private final Label readout` |  |
| 229 | `private final MapFrame small` |  |
| 230 | `private MapFrame big` |  |
| 231 | `private boolean expanded` |  |
| 233 | `private VBox bigPane` |  |
| 234 | `private StackPane bigBox` |  |
| 235 | `private VBox card` |  |
| 236 | `private Label clockLine` |  |
| 240 | `private CityMap shownMap` |  |
| 241 | `private MapTiles tiles` |  |
| 242 | `private Game.MapDraft draft` |  |
| 243 | `private int draftsFailed` |  |
| 244 | `private long version` |  |
| 245 | `private long fittedFor` |  |
| 246 | `private final TilePainter.Input in` |  |
| 247 | `private int[] buffer` |  |
| 251 | `WritableImage image` |  |
| 252 | `long stamp, version` |  |
| 253 | `byte[] ground` |  |
| 254 | `boolean partial` |  |
| 255 | `long pictures` |  |
| 257 | `int[] pixels` | A far node's colours as drawn (0.7.135): what the globe reprojects near the city. |
| 262 | `long most` |  |
| 267 | `private final Pictures l0small` |  |
| 269 | `private final Set<Long> nodesAsked` |  |
| 271 | `private final Set<CityMap.District> picturesAsked` | The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. |
| 274 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 275 | `private long fieldsFor` |  |
| 279 | `private int side` | the side the office shows, and the offer picked |
| 280 | `private int litSide` | the side the office shows, and the offer picked |
| 281 | `private int overSide` | a row hovered in the office |
| 282 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 283 | `private boolean pressed` |  |
| 284 | `private double[] hoverAt` |  |
| 289 | `private final LandMap.Picks picks` | The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a month, a load and a collapse. |
| 291 | `private VBox buyBox` | The card in the pane (the land office's: LandScreen.mapCardNode(), mapReceiptNode()) and what it was drawn for - null for a receipt. |
| 292 | `private String cardFor` |  |
| 294 | `private List<double[]> rings` | The sites of the fields the card's offers hold, each its square of plots and its resource's colour, {x0, y0, x1, y1, argb}: ringed wherever they lie (board 7). |
| 296 | `private double[] cardAt` | The box of plots the card stands beside: the offer clicked, what a box took, the shelf's first for Ctrl+A, the ground just bought. |
| 298 | `private Bought bought` | A purchase from the card and its receipt: what is being bought - ids, places, the ground - shown by the ground once it is the city's. |
| 299 | `private boolean receiptShown` |  |
| 301 | `private boolean backFromFunding` | The card sent to the funding page (B5): the map comes back over the window at the view it left when the page is done. |
| 303 | `private boolean boxing` | Shift and a drag (B7): a box drawn from the press to (boxX, boxY), in the screen's pixels. |
| 304 | `private double boxX, boxY` |  |
| 306 | `private Label headMoney` | The head's money and its pay toggle (the pane covers the window's header), and what they were drawn for. |
| 307 | `private HBox headPay` |  |
| 308 | `private String headFor` |  |
| 310 | `private boolean running, dirty` |  |
| 315 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 317 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 318 | `private int scheduleMonth` |  |
| 319 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 321 | `private TrainSchedule trainSchedule` | The trains' schedule drawn from (0.8.2, batch FR1): its boat-month, the routes and the game month it was made on. |
| 322 | `private int trainMonth` |  |
| 323 | `private List<TrainSchedule.Route> trainRoutes` |  |
| 325 | `private boolean boatsLive` | Whether boats - or since 0.8.2 trains, and since 0.8.8 trucks - are in view (the frame goes on), and the clock they were last drawn at. |
| 326 | `private double drawnClock` |  |
| 327 | `private final AnimationTimer pump` |  |
| 331 | `private final ImagePattern hatch` |  |
| 336 | `private GlobeFrame globe` | The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. |
| 337 | `private World.Key globeKey` |  |
| 338 | `private EarthData globeData` |  |
| 340 | `private long reachFor` | The land and offers its reach was found for. |
| 343 | `private WritableImage globeImg` | The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. |
| 344 | `private int[] globePx` |  |
| 345 | `private GlobeFrame globeDrawn` |  |
| 346 | `private int globeMode` |  |
| 347 | `private long globeInputs` |  |
| 349 | `private long inputs` | Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. |
| 351 | `private double[] patch` | The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. |
| 354 | `private byte[] pieceCls` | The GROUND tier's pieces past the plane, for one view at rest: its classes a pixel (-1 not in yet), the view and its number. |
| 355 | `private GlobeFrame pieceView` |  |
| 356 | `private volatile int pieceGen` |  |
| 358 | `private int[] carryPx` | The last whole picture at rest, carried pixel by pixel while the next is drawn. |
| 359 | `private GlobeFrame carryView` |  |
| 365 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when the keys last moved the view (0 when none is held). |
| 366 | `private long keyNanos` |  |
| 369 | `private int legendTier` | The tier the legend was laid out for. |
| 876 | `private javafx.scene.text.Font worldLabelFont` | The world line's label as measured: its font, its width (and the shared label's) and its height above the baseline, kept until the font changes (FX2, FX5). |
| 877 | `private double worldLabelW, worldLabelBothW, worldLabelH` |  |
| 1107 | `private TruckSchedule truckSchedule` | The highways' trucks' schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 1108 | `private int truckMonth` |  |
| 1109 | `private List<TruckSchedule.Route> truckRoutes` |  |
| 1115 | `private final Map<Long, DistrictTrucks> districtTrucks` | The districts' schedules by CityMap.DistrictRoutes.key(): thrown out with the highways' when the month, the game month or the map turns. |
| 1118 | `private int trucksStand` | Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the highways' schedule's, else the largest of the districts' drawn. |
| 1375 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 1376 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 1377 | `private long shapesFor` |  |
| 1380 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 1381 | `private long labelsFor` |  |
| 1382 | `private double labelsAtScale` |  |
| 1387 | `private List<CityMap.Tinted> tints` | The zones' districts as last worked out (CityMap.zoneTint()), and what from: the map, its changes and the zones. |
| 1388 | `private CityMap tintsMap` |  |
| 1389 | `private long tintsAt` |  |
| 1390 | `private String tintsKey` |  |
| 1584 | `private boolean globeHalf` | The globe drawn while it was half its resolution (the view on the move). |
| 1675 | `private GlobeFrame globeNodesFor` |  |
| 1676 | `private long[] globeNodesAt` |  |
| 2917 | `private final Label legendNote` |  |
| 2919 | `private String legendZones` | The zones the legend was laid out with (0.8.0): their words, joined. |
| 2920 | `private final GridPane legendGrid` |  |
| 2921 | `private final Label shipsLine` |  |
| 2923 | `private final Label trucksLine` | The legend's trucks line: shown while trucks are drawn this frame (legendTrucks()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 174 | 2823 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 213 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 213 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 238-276)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 9 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count ... |
| 261 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 263 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 264 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 277-285)

### buying from the map (0.7.137, batch M6) (lines 286-311)

### the boats (0.7.97) (lines 312-332)

### the globe (0.7.135, batch E9) (lines 333-502)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 40 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 430 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 433 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 436 | 12 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 449 | 30 | `private void reset()` |  |
| 481 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 488 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 495 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 503-826)

| line | len | member | says |
|---:|---:|---|---|
| 507 | 45 | `private void tick()` |  |
| 562 | 53 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 617 | 9 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 628 | 8 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 637 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 652 | 39 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - stamped again, and painted only when the stamp moved - from the middle of the view out; at FAR ... |
| 693 | 17 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 712 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 722 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 727 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 731 | 8 | `private boolean paintNodes(MapFrame f, long deadline)` |  |
| 750 | 30 | `private void askPictures(MapFrame f)` | The district pictures the far nodes drawn this frame lacked (0.7.128, batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order, while the view's districts fit wha... |
| 782 | 44 | `private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept)` | The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). |

### THE DRAWING (lines 827-870)

| line | len | member | says |
|---:|---:|---|---|
| 831 | 39 | `private void draw()` |  |

### THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) (lines 871-955)

| line | len | member | says |
|---:|---:|---|---|
| 891 | 40 | `private void worldLines(GraphicsContext gc, MapFrame f)` | The highway's and the main line's faded runs past the city's ground (CityMap.worldLines()): each from its point along its way out over dry ground, as far as the land runs (CityMap.WorldLine.reach(), at most CityMap.WO... |
| 933 | 22 | `private static double[] rayRect(double x, double y, double dx, double dy, double tMax, double w, double h)` | The part of the segment (x, y) + t (dx, dy), 0 <= t <= tMax, that lies on the canvas, {x0, y0, x1, y1}; null when it misses it. |

### THE BOATS (0.7.97, batch O13) (lines 956-1040)

| line | len | member | says |
|---:|---:|---|---|
| 961 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 973 | 67 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: each route on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), ... |

### THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 1041-1100)

| line | len | member | says |
|---:|---:|---|---|
| 1054 | 46 | `private void trains(GraphicsContext gc, MapFrame f, int level)` | The trains over the tiles, from L1 in, as the boats: the month's schedule (TrainSchedule, from the railway's tonnes by kind) on the map's routes (CityMap.trainRoutes()) at the boats' clock. |

### the trucks (0.8.8, batch FR5; spec-freight 4.7, D21) (lines 1101-1383)

| line | len | member | says |
|---:|---:|---|---|
| 1112 | 1 | **type** `private record DistrictTrucks(List<TruckSchedule.Route> routes, TruckSchedule schedule)` | An access road's trucks' schedule, with the routes it was made on: made again when its district's routes are another list. |
| 1130 | 39 | `private void trucks(GraphicsContext gc, MapFrame f, int level)` | The trucks over the tiles, at the near view alone and while the setting is on (TruckSchedule.drawnAt()): the month's schedule on the highways (CityMap.truckRoutes()) and, for each district in view, on its access roads... |
| 1171 | 7 | `private int fillTrucks(GraphicsContext gc, MapFrame f, TruckSchedule schedule, long x0, long y0, long x1, long y1, double t)` | One schedule's trucks in the box filled in their colours, or nothing when it has none or no route of it crosses the view; the loads a truck of it stands for, or 0 when none was drawn. |
| 1180 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 1185 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 1191 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 1198 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 1203 | 19 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 1223 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 1253 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 1296 | 77 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched rectangles - each its free ground only, the city's own ground... |

### the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") (lines 1384-1566)

| line | len | member | says |
|---:|---:|---|---|
| 1393 | 17 | `private void zoneTints(GraphicsContext gc, MapFrame f, Game g)` | The city's zones over the ground, under the offers and the city's edge: each zone's districts in its sector's colour at LandMap.ZONE_ALPHA, the districts the map picks for it (CityMap's ZONES BY DISTRICT); nothing wit... |
| 1412 | 10 | `private List<String[]> zoneEntries()` | The zones' legend entries on the plane (0.8.0): each zone set, by its sector's label. |
| 1423 | 11 | `private void shapes(CityLand land, LandMarket market)` |  |
| 1436 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 1448 | 27 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 1480 | 10 | `private void newTag(GraphicsContext gc, LandParcel p, double x, double y, double boxH)` | ...under the number at (x, y), where the offer's box on the screen is at least two numbers tall (MapFrame.PLACE_LABEL_FROM each). |
| 1492 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 1519 | 4 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 1525 | 17 | `private static void scaleBar(GraphicsContext gc, double h, double px, String words)` | ...drawn: `px` long at the bottom left of a view `h` high, and its words. |
| 1547 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 1552 | 3 | `static String css(int argb)` | ...as CSS. |
| 1557 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |

### THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) (lines 1567-2051)

| line | len | member | says |
|---:|---:|---|---|
| 1587 | 1 | `private boolean onGlobe()` | Whether the expanded map draws the globe: on an Earth world, wider than the handoff. |
| 1590 | 1 | `private boolean globeShown()` | ...or lays its picture over the plane's in the crossfade's notch. |
| 1593 | 7 | `private void resetGlobe()` | A new city, or another world: no globe until the map is over the window on Earth again. |
| 1602 | 13 | `private void resetGlobePicture()` | What the globe drew from and drew: another map, or another view of the world. |
| 1617 | 24 | `private void globeUp(CityLand land, LandMarket market, long landKey)` | The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. |
| 1643 | 4 | `private void follow()` | The view moved on the globe: at the plane's scales the plane's frame follows it; the view drawn again. |
| 1649 | 25 | `private long[] globeNodes(GlobeFrame v)` | The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws; null where it draws none. |
| 1679 | 14 | `private boolean paintGlobe(long deadline)` | A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack. |
| 1695 | 14 | `private void nodeColoured(long x0, long y0, long span)` | A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. |
| 1711 | 9 | `private void addPatch(double x0, double y0, double x1, double y1)` | The still picture's box to be drawn again, grown to take in another. |
| 1729 | 32 | `private void composeGlobe(int mode)` | The globe's picture of the view, drawn again when the view, the way it is drawn or what it is drawn from moved: at half resolution while the view moves (its frame a copy of half the size at twice the km a pixel), at i... |
| 1763 | 35 | `private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1)` | The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image. |
| 1800 | 18 | `private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic)` | Past the plane, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. |
| 1820 | 7 | `private void drawGlobeImage(GraphicsContext gc)` | The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). |
| 1835 | 39 | `private void askPieces(GlobeFrame view)` | The GROUND tier's pieces past the plane for a view come to rest: the far rule through the camera (GlobeFrame.groundPiece()) a PIECE_PX square at a time on WORKER, from the middle out, each one with some of its pixels ... |
| 1876 | 7 | `private void pieceIn(int gen, int[] q, byte[] b)` | A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. |
| 1885 | 27 | `private void drawGlobe()` | The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its edge through the camera in the PICTURE tier once it is EDGE_FROM_PX across, its ring - ... |
| 1917 | 10 | `private double[][] onGlobe(double[] xs, double[] ys)` | A shape of plots through the camera: its corners on the screen, or null where one is not seen. |
| 1929 | 52 | `private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market)` | The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - hatched, edged in pink, the office's side bright, the one lit or picked, numbered ... |
| 1983 | 12 | `private void globeEdge(GraphicsContext gc, CityLand land)` | The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. |
| 1997 | 11 | `private void ring(GraphicsContext gc, CityLand land)` | The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. |
| 2010 | 4 | `private void globeScaleBar(GraphicsContext gc)` | The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. |
| 2016 | 23 | `private void globeHover(double sx, double sy)` | The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it. |
| 2041 | 10 | `private byte drawnAt(double sx, double sy)` | The ground drawn at a screen point on the globe, past the plane: the GROUND tier's piece where one is in, else the planet's picture's texel. |

### the keys (0.7.135; spec-globe 3.11) (lines 2052-2092)

| line | len | member | says |
|---:|---:|---|---|
| 2055 | 23 | `private boolean keys()` | A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not h... |
| 2080 | 12 | `boolean panKey(KeyCode k, boolean down)` | W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. |

### THE POINTER AND THE KEYS (lines 2093-2303)

| line | len | member | says |
|---:|---:|---|---|
| 2097 | 71 | `private void handlers()` |  |
| 2170 | 6 | `private void zoomBig(double sx, double sy, double notches)` | The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. |
| 2186 | 38 | `private void click(double sx, double sy, boolean shift)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on - on the globe only where the city's plane is drawn (0.7.135). |
| 2226 | 40 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 2271 | 23 | `private void showCard(List<String> lines, double sx, double sy, double vw, double vh)` | The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - CLICK_TO_BUY in the accent's light blue. |
| 2299 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 2304-2703)

| line | len | member | says |
|---:|---:|---|---|
| 2332 | 1 | **type** `record Bought(List<Integer> ids, List<int[]> places, String title, double[] at)` | A purchase from the card: the offers found listed when it was asked for, their places ({side, place} each), the card's title for them and the ground they stand on, in plots. |
| 2344 | 3 | `private boolean canPick()` | Whether the offers are drawn to be picked where the expanded map looks (star M6): on the plane at every scale, and on Earth in the globe's GROUND tier, where globeOffers() draws them - not its PICTURE tier, which draw... |
| 2349 | 7 | `private LandMap.ToScreen toScreen()` | A plot of the city's plane on the screen, as the expanded map draws it now: through the globe's camera, or the plane's frame. |
| 2358 | 3 | `private static double[] box(LandParcel p)` | An offer's box in plots, {x0, y0, x1, y1}. |
| 2363 | 11 | `private static double[] bounds(List<LandParcel> ps)` | ...and the bounds of several, or null with none. |
| 2376 | 9 | `private List<LandParcel> listed(List<Integer> ids)` | The listed offers among these ids, in their order. |
| 2387 | 11 | `private void boxed(double x0, double y0, double x1, double y1)` | Shift and a drag let go (B7): every offer whose free ground the box meets added to the card's picks, in the office's order - the cheapest dry ground first - the card beside them. |
| 2400 | 13 | `void selectAll()` | "Select every offer" in the head, or Ctrl+A (B7): every offer listed onto the card, the office's best first, the card beside the best (star M6). |
| 2415 | 22 | `private void showBuyCard()` | The card drawn again for the picks as they stand, or put away with none listed. |
| 2439 | 8 | `private void hideBuyCard()` | The card put away. |
| 2449 | 5 | `private String cardKey(List<Integer> ids)` | What the card was drawn from: the picks, the toggle, the cash and the vault, the month and the listing. |
| 2456 | 16 | `private void syncCard()` | A frame's look at the card (tick()): drawn again when what it says moved - the month dropping the picks, an offer bought elsewhere falling out of them, the cash - and the receipt put away with its month. |
| 2474 | 5 | `private void layoutBuyCard()` | The card sized to what it says, then placed. |
| 2481 | 16 | `private void placeBuyCard()` | The card beside its box of plots as the view moves (each draw): at its right, else at its left, its top level with the box's, inside the view. |
| 2499 | 15 | `private double[] screenBox(double[] b)` | A box of plots on the screen, {x0, y0, x1, y1} round its corners as drawn now, or null when none is seen. |
| 2516 | 16 | `private void drawRings(GraphicsContext gc)` | The card's fields' sites, ringed wherever they lie (board 7): each a ring in its resource's colour on a dark halo, as the city's ring is drawn (ring()), round its square - RING_PX at least. |
| 2534 | 11 | `private void drawBox(GraphicsContext gc)` | The box Shift and a drag draws (B7): the offers' pink, faint inside, dashed round. |
| 2547 | 6 | `void closeCard()` | The card's ×, Esc, a click off the offers: no picks, no card. |
| 2555 | 5 | `void dropPick(int id)` | A chip's × on a card of several: that one dropped. |
| 2562 | 5 | `void paidOtherwise()` | The toggle moved, on the card or the head: both drawn again in the other money. |
| 2569 | 10 | `private Bought boughtOf(List<Integer> ids)` | What a purchase from the card is of, asked for now. |
| 2581 | 9 | `void buy(List<Integer> ids)` | The card's Buy, or Enter (B2): the offers bought as the office's Buy buys them (LandScreen.buyOrFund()) - the office drawn again under the map, which stays - and the receipt by the ground; short after all, the funding... |
| 2592 | 9 | `void buyOnLoan(List<Integer> ids, Runnable issue)` | The card's bond or note (B1): issued as the funding page issues it, the offers bought on it (LandScreen.offers()' own action), and the receipt; what falls short shows the office's page, which comes back here. |
| 2603 | 10 | `void toFunding(List<Integer> ids)` | "All the ways to pay" (B5): the funding page for the picks, the map put away until the page is done and brought back at the view it left (back()). |
| 2615 | 6 | `void toOffice(int s, int p)` | "Open in the land office": the map put away on the offer's row, the office's side and pick. |
| 2629 | 10 | `void back(boolean fromFunding)` | The office drawn (LandScreen.showLandMenu()), `fromFunding` when the funding page or what it fell short with is what it comes back from: a purchase the card sent there brings the map back over the window at the view i... |
| 2641 | 22 | `private void showBought()` | The receipt by the ground just bought (board 6): the card's purchase, those of it no longer listed - "2 of 3 offers" when it fell short - in the office's words (LandScreen.mapReceiptNode()); with none bought, the card... |
| 2671 | 20 | `boolean cardKey(javafx.scene.input.KeyEvent e)` | Esc, Enter and Ctrl+A while the map is over the window, from UserInterface's key filter (0.7.137): Esc closes the card - a second Esc, not taken here, the map - Enter presses the card's main button (B2: Buy, or short ... |
| 2693 | 10 | `private void syncHead()` | The head's money and pay toggle, drawn again when the toggle, the cash, the vault or the month moved. |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 2704-2996)

| line | len | member | says |
|---:|---:|---|---|
| 2708 | 1 | `void expand()` |  |
| 2711 | 18 | `private void expand(boolean keepView)` | ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. |
| 2736 | 11 | `void expandOn(MiniMapFrame.Box box)` | Expanded on a box of plots (0.7.134, batch MM2): the minimap's click, the map over the window fitted on what the stamp frames (MiniMapFrame.open()) rather than on the land office's opening; Fit goes back to that. |
| 2749 | 19 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked - the card's picks dropped (0.7.137), and its purchase forgotten unless the funding page has it (B5). |
| 2769 | 53 | `private void buildPane()` |  |
| 2825 | 22 | `private void fitPane()` | The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). |
| 2849 | 10 | `private void openBig()` | The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. |
| 2861 | 10 | `private void fitBig()` | Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. |
| 2873 | 5 | `static List<String[]> legendEntries(int tier)` | The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. |
| 2880 | 1 | `static List<String[]> legendEntries()` | ...the plane's, at every scale on a generated world. |
| 2892 | 3 | `static String trucksWords(int loads)` | ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. |
| 2900 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed without a street. |
| 2905 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 2910 | 3 | `static String trackWords(long plots)` | ...and (FR7 slice 3) when the model has track the railway's ways do not use - its main line, its sidings and its branches - how much, in plots; the model's words (LandMap.trackSurplusWords()). |
| 2925 | 18 | `private VBox legend()` |  |
| 2945 | 15 | `private void legendFor(int tier)` | The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. |
| 2962 | 1 | `private int shownTier()` | The tier the expanded map shows: the globe's on Earth, the plane at every scale on a generated world. |
| 2965 | 5 | `private void legendTrucks()` | The trucks' line: how many loads a truck stands for while any is drawn this frame, on the plane; hidden otherwise. |
| 2972 | 14 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. |
| 2988 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

