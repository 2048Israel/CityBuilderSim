# MapView.java - 6,330 lines · 267 methods · 76 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
> into UserInterface's pane over the window (City History's, 0.7.23; since
> MS1 under the window's own header, which stays in view - the clock, the
> money block, the five tiles - rather than over it): a drag from anywhere
> but ground that may be picked pans (an offer's free ground, a block that
> may be picked or is, BL1; and a right or middle drag from
> anywhere), the wheel zooms by MapFrame.ZOOM_STEP a notch at the pointer,
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
> stamps the tiles it shows again, and repaints only those whose stamp moved;
> the far nodes, since batch UP3, only those whose key moved
> (MapTiles.nodeKey()), and the minimap's stamp at most every
> MiniMap.STAMP_EVERY_MS, from its kept layers.
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
> ... (142 more lines in the source)

**Uses:** [Palette](Palette.md) (164), [MapFrame](MapFrame.md) (120), [LandMap](LandMap.md) (87), [NationsMap](NationsMap.md) (54), [GlobeFrame](GlobeFrame.md) (43), [World](World.md) (36), [Game](Game.md) (34), [CityLand](CityLand.md) (27), [LandParcel](LandParcel.md) (26), [CityMap](CityMap.md) (23), [MapTiles](MapTiles.md) (21), [TradeLanes](TradeLanes.md) (19), [LandMarket](LandMarket.md) (18), [HarbourYard](HarbourYard.md) (17), [GlobePicture](GlobePicture.md) (17), [CameraPath](CameraPath.md) (15), [ShipShapes](ShipShapes.md) (14), [BoatSchedule](BoatSchedule.md) (13), [TrainSchedule](TrainSchedule.md) (13), [Nations](Nations.md) (13), [QuayCrane](QuayCrane.md) (12), [CityShore](CityShore.md) (11), [ReplayCamera](ReplayCamera.md) (11), [CallPartners](CallPartners.md) (10), [TruckSchedule](TruckSchedule.md) (10), [TileRaster](TileRaster.md) (9), [TrainShapes](TrainShapes.md) (9), [DepositMarks](DepositMarks.md) (8), [Ports](Ports.md) (7), [TilePainter](TilePainter.md) (6)... and 26 more

**Used by (6):** [InputTimeline](InputTimeline.md), [LandScreen](LandScreen.md), [MiniMap](MiniMap.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 329 | · what it draws from |
| 403 | · what the player is doing |
| 416 | · buying from the map (0.7.137, batch M6) |
| 444 | · the boats (0.7.97) |
| 502 | · the globe (0.7.135, batch E9) |
| 686 | A FRAME OF THE CLOCK (tick()) |
| 1112 | THE DRAWING |
| 1168 | THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) |
| 1253 | THE BOATS (0.7.97, batch O13) |
| 1362 | · the harbour's quay and its boats alongside (HB1, 2026-11-08) |
| 1391 | · the harbour's yards, their stock and the quay's rail (HB2, 2026-11-09) |
| 1475 | · the quay's crane (HB2, 2026-11-09) |
| 1542 | THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 1612 | · a boat or a train names its partner (WN7) |
| 1692 | · the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) |
| 1993 | · the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") |
| 2020 | · the ground no port links (ET5, slice D; the ET5 design, section 5) |
| 2440 | THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) |
| 3315 | · the hover's quote, away from the screen's thread (WN5b, 2026-11-02) |
| 3402 | · the keys (0.7.135; spec-globe 3.11) |
| 3444 | THE POINTER AND THE KEYS |
| 3792 | BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 4236 | THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) |
| 4480 | · the city's trade (WN3) |
| 4557 | · the chips |
| 4635 | · the legend |
| 4663 | · drawing |
| 4903 | · the pointer |
| 4944 | · the card |
| 5088 | EXPAND: THE MAP OVER THE WHOLE WINDOW |
| 5480 | REPLAY: CINEMATIC (TR1, 2026-10-31) |
| 5555 | REPLAY: THE CAMERA, THE PAINT BUDGET AND THE QUICK STEP (TR1, 2026-10-31) |
| 5729 | · the move loaded before it runs (CM1) |
| 6134 | · CM1: what a camera move shows unpainted (MoveTally) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 249 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 252 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 255 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 258 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 261 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 264 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 267 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 270 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 276 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 279 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 282 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 285 | `MapView.HIT_PX` | `4` | How near the pointer must be to a boat's hull or a train's vehicle, in pixels, for its label to show (WN7): 4. |
| 288 | `MapView.HIT_ALPHA` | `0.3` | A boat or a vehicle fainter than this, out in the abyss or past the world line's far end, is not named (WN7): 0.3. |
| 291 | `MapView.LABEL_OFF_PX` | `10` | A boat's or a train's label stands this many pixels off it, up and to the right, or the other way where the view's edge is in the road (WN7): 10. |
| 297 | `MapView.WORKER` | `new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQue...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), ... |
| 305 | `MapView.QUOTES` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "land-quote...` | The thread a hover's block is quoted on (WN5b): its own, not WORKER's, because one quote can take seconds at a big level near a foreign coast and would hold the map's own jobs (plans, nodes, the minimap) behind it. |
| 313 | `MapView.QUOTES_KEPT` | `16` | How many blocks' quotes the hover keeps, the latest asked: 16 - a pointer that comes back to a block shows its price at once. |
| 473 | `MapView.YARD_IMAGES_KEPT` | `256` | The most yard pictures kept at once (HB2): each is one box of pixels, so the cap bounds what a pan over a big harbour holds. |
| 475 | `MapView.YARDS_A_FRAME` | `12` | The most new yard pictures painted in one frame (HB2): a first frame over a big harbour paints them over several, the rest left as the tile drew them. |
| 531 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 584 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 587 | `MapView.EXPAND_TIP` | `"The map under the window's header: drag along the city's edge to pick the ex...` | The Expand button's tooltip (since MS1 it says the drag picks; since BL1 the ground it picks is the player's own: a drag along the city's edge picks the exact ground, one from elsewhere pans). |
| 591 | `MapView.HINT` | `"drag along the city's edge to pick the exact ground · drag elsewhere to pan"` | The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), since MS1 by a click or a drag, since BL1 of the exact ground - blocks of the city's grid, the offers i... |
| 594 | `MapView.KEYS_TIP` | `"Click an offer to open its card beside it, or a free block along the city's ...` | ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. |
| 604 | `MapView.SELECT_EVERY` | `"Select every offer"` | The head's chip that picks every offer listed (B7). |
| 1864 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1867 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 1870 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1873 | `MapView.WORLD_LINE_ALPHA` | `LandMap.WORLD_ALPHA` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. |
| 2186 | `MapView.DASH_ON` | `5, DASH_PERIOD = 9` | A run's dashes: 5 px on and 4 off, as the offers' edge has been drawn since 0.7.61. |
| 2247 | `MapView.PICKED_ALPHA` | `0.22` | The tint of a picked offer's ground and of picked blocks: the building pink at 0.22 (the picked offer's since 0.7.137). |
| 2304 | `MapView.NEW_TAG` | `"NEW"` | The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. |
| 2351 | `MapView.ICON_EDGE` | `Color.rgb(11, 17, 24, 0.85), ICON_DARK_INK = Color.rgb(11, 17, 24)` | An icon's edge, the dots' dark (11, 17, 24) at 0.85, and the mark written dark on a light colour: the same, whole. |
| 2445 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe, by code - a class and its relief (ET1 slice D): the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 2448 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 2451 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 2454 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 2594 | `MapView.CHART_NODE_OFFSET` | `1L<<19` | A chart node's indexes run negative west and north of the chart's anchor: this many are added to each to key it. |
| 2953 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 3141 | `MapView.BLOCK_LINE_SAG_PX` | `0.25` | The most the camera may bend a block line from its chord before the line is split in two, in pixels: a quarter (the line is drawn a pixel wide, so a quarter is not seen). |
| 3144 | `MapView.BLOCK_LINE_SPLITS` | `6` | ...and how many times a line is split at most: 6 (64 pieces - far more than the 64 px a line bends by at the widest near scale 12,000 km from the city needs). |
| 3255 | `MapView.CLICK_TO_DROP` | `"Shift-click to drop it · Enter buys"` | The hover card's last line over a block that is picked already (BL1), in blue as the offers' is: how to drop it. |
| 3751 | `MapView.CLICK_TO_BUY` | `"Click to buy it here"` | The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. |
| 3784 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 3828 | `MapView.CARD_GAP` | `14` | The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). |
| 3831 | `MapView.CARD_MARGIN` | `10` | ...and at least this far inside the view: 10, the hover card's (showCard()). |
| 3834 | `MapView.BOX_FILL_ALPHA` | `0.12` | The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. |
| 4257 | `MapView.NATIONS_TIP` | `"A dot at each nation's largest port or city, by its output; its name, output...` | The Nations chip's tooltip (WN2). |
| 4261 | `MapView.BORDERS_TIP` | `"The nations' borders, dashed white, at the far views; up close only where on...` | ...and the Borders chip's. |
| 4264 | `MapView.RESOURCES_TIP` | `"The deposits: zoomed out, an icon at each one in its resource's colour with ...` | ...and the Resources chip's (LM1): what it shows, on every world, and how wide the map may be for it. |
| 4276 | `MapView.TRADE_SELL_WORDS` | `"What we sell, a month", TRADE_BUY_WORDS = "What we buy"` | The legend's words for the two kinds of route (the board's, less its "it"). |
| 4279 | `MapView.TRADE_ALPHA` | `0.78` | A route's colours: the business violet for what we sell and the ore's sand for what we buy (Palette's), at the board's stroke opacity, .78. |
| 4280 | `MapView.TRADE_SELL` | `Color.web(Palette.BUSINESS, TRADE_ALPHA), TRADE_BUY = Color.web(Palette.ORE, ...` |  |
| 4283 | `MapView.ARROW_LEN_PX` | `34` | A generated world's arrow is this many pixels long from its tip inward... |
| 4286 | `MapView.ARROW_HEAD_PX` | `9, ARROW_HEAD_WIDE_PX = 3` | ...with a head this many pixels long, and this much wider than the shaft either side. |
| 4289 | `MapView.NO_FIGURES` | `new double [ 0 ]` | A stand-in for a feed that gave no figures. |
| 4292 | `MapView.LABEL_NAME_PX` | `11` | A dot's label, the name: sans semibold, 11 px (the board's .lbl)... |
| 4295 | `MapView.LABEL_FIGURES_PX` | `9.5` | ...and the figures under it, mono, 9.5 px (the board's .lbl2). |
| 4298 | `MapView.DOT_EDGE_PX` | `1.2` | A dot's edge, in pixels: 1.2, in NationsMap.DOT_EDGE_ARGB (the board's). |
| 4301 | `MapView.HALO_PX` | `3, HALO_ALPHA = 0.75` | A label's halo, drawn under its words: 3 px wide in NationsMap.DOT_EDGE_ARGB at 0.75 (the board's .lbl stroke). |
| 4304 | `MapView.WIDTHS_MOST` | `600` | The widths of label words kept, by string (the names never change and the figures change once a year): the 600 most recently used. |
| 4307 | `MapView.NATION_CARD_W` | `330` | A nation's card is this wide, in pixels: 330 (the board's .card)... |
| 4310 | `MapView.NATION_CARD_GAP` | `4` | ...and its head, its distance line and its rows stand this many pixels apart: 4. |
| 5311 | `MapView.LEGEND_ROWS` | `17` | Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 the transport class, t... |
| 5314 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 5317 | `MapView.TRUCK_WORDS` | `"Trucks: one per %s"` | The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). |
| 5325 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 5343 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |
| 5398 | `MapView.LEGEND_FOLD_WORDS` | `"· click to fold"` | The head line's words when the legend is open, after its chevron and "Legend" (the board's .lh). |
| 5574 | `MapView.REPLAY_FRAME_MS` | `24` | The most a frame spends painting tiles in a replay, in ms: 24 - a replay computes no month, so a frame's whole share is the picture's, and a view reached by the camera comes in over two or three frames, not eight. |
| 5577 | `MapView.CAMERA_PAUSE_S` | `0.25` | A gap between two camera frames longer than this, in seconds, is not time the camera moved in: 0.25 - the pump stops when the view is idle and a glide or a fly must not find the minutes gone by in its first frame. |
| 5744 | `MapView.STUCK_S` | `2` | How long the worker may sit idle while a view of the load still lacks something before the load passes it, in seconds: 2 - a far node or chart the worker cannot ground never comes back, and the load is not held by it;... |
| 5747 | `MapView.LOAD_HEAP_SHARE` | `0.8` | The share of the most the heap may hold at which a load stops, saying so: 0.8 - a move too long or too wide to hold in memory is refused, not run half loaded. |
| 5750 | `MapView.LOAD_WORDS_PX` | `28` | The height of the words written over the map while a move loads or waits for its click, in pixels: 28. |
| 5910 | `MapView.NO_MOVE` | `new MoveLoad(0, 0, false)` | A move with nothing passed and nothing kept, for viewSettled(). |
| 6165 | `MapView.MoveTally.WHERES` | `{ "L0", "L1", "L2", "FAR", "GROUND", "PICTURE" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 273 | `static Game.MapDraft drafting` | The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. |
| 315 | `private final UserInterface ui` |  |
| 316 | `private final Picker picker` |  |
| 317 | `private final Runnable onClose` |  |
| 318 | `private final Canvas canvas` |  |
| 319 | `private final StackPane smallBox` |  |
| 320 | `private final Label readout` |  |
| 321 | `private final MapFrame small` |  |
| 322 | `private MapFrame big` |  |
| 323 | `private boolean expanded` |  |
| 325 | `private VBox bigPane` |  |
| 326 | `private StackPane bigBox` |  |
| 327 | `private VBox card` |  |
| 331 | `private CityMap shownMap` |  |
| 332 | `private volatile MapTiles tiles` | (TR1) volatile: WORKER's queue skips the plans of a map a replay has stepped past (ask()) |
| 333 | `private Game.MapDraft draft` | (TR1) volatile: WORKER's queue skips the plans of a map a replay has stepped past (ask()) |
| 334 | `private int draftsFailed` |  |
| 335 | `private long version` |  |
| 336 | `private long fittedFor` |  |
| 338 | `private long farRecoloured` | How many far nodes have been coloured (batch UP3): a node whose version moved but whose key did not (MapTiles.nodeKey()) is not counted. |
| 339 | `private final TilePainter.Input in` |  |
| 340 | `private int[] buffer` |  |
| 344 | `WritableImage image` |  |
| 345 | `long stamp, version` |  |
| 347 | `long key` | A tile's key (MapTiles.tileKey(), batch UP5) at the moment `stamp` was taken, and whether it was: while the key holds the stamp does, and the tile's inputs are not read again. |
| 348 | `boolean keyed` |  |
| 349 | `byte[] ground` |  |
| 351 | `byte[] relief` | The node's relief beside its ground (ET1 slice D): null on a generated world, whose ground is all flat. |
| 352 | `boolean partial` |  |
| 353 | `long pictures` |  |
| 355 | `int[] pixels` | A far node's colours as drawn (0.7.135): what the globe reprojects near the city. |
| 360 | `long most` |  |
| 362 | `long evicted` | How many pictures this level has let go for want of room, since it was made (CM1: the camera tally's evictions). |
| 364 | `boolean pinned` | Whether a replay's loaded move holds this level whole (CM1): nothing is let go for room until it is taken off (MapView.pin()). |
| 383 | `private final Pictures l0small` |  |
| 385 | `private final Set<Long> nodesAsked` |  |
| 391 | `private final Pictures charts` | The globe's chart nodes (MS3; GlobeFrame.chart()) kept as the plane's far nodes are - their colours (Img.pixels), classes and relief (for the hover) - and those asked of WORKER and not back. |
| 392 | `private final Set<Long> chartsAsked` |  |
| 394 | `private final Set<CityMap.District> picturesAsked` | The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. |
| 397 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 398 | `private long fieldsFor` |  |
| 401 | `private final DepositMarks.Search farFields` | ...and (LM1) those the FAR view's icons are drawn from: found for a box twice the view and kept until the view leaves it (DepositMarks.Search), thinned each frame. |
| 405 | `private int side` | the side the office shows, and the offer picked |
| 406 | `private int litSide` | the side the office shows, and the offer picked |
| 407 | `private int overSide` | a row hovered in the office |
| 408 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 409 | `private boolean pressed` |  |
| 411 | `private javafx.scene.input.MouseButton pressButton` | The button the press was made with (MS1): only the primary clicks or draws a box; the others pan. |
| 412 | `private double[] hoverAt` |  |
| 414 | `private boolean hoverShift` | Whether Shift was held when the pointer last moved over the map: the cursor says a press would box from anywhere (MS1). |
| 419 | `private final LandMap.Picks picks` | The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a load and a collapse - and, since MS1, not on a month: each is keyed to its ground and lost only when the city ... |
| 421 | `private VBox buyBox` | The card in the pane (the land office's: LandScreen.mapCardNode(), mapReceiptNode()) and what it was drawn for - null for a receipt. |
| 422 | `private String cardFor` |  |
| 424 | `private List<double[]> rings` | The sites of the fields the card's offers hold, each its square of plots and its resource's colour, {x0, y0, x1, y1, argb}: ringed wherever they lie (board 7). |
| 426 | `private double[] cardAt` | The box of plots the card stands beside: the offer clicked, what a box took, the shelf's first for Ctrl+A, the ground just bought. |
| 428 | `private Bought bought` | A purchase from the card and its receipt: what is being bought - ids, places, the ground - shown by the ground once it is the city's. |
| 429 | `private boolean receiptShown` |  |
| 431 | `private boolean backFromFunding` | The card sent to the funding page (B5): the map comes back over the window at the view it left when the page is done. |
| 433 | `private boolean boxing` | A drag that boxes (B7; since MS1 a plain drag from an offer's free ground, or Shift and a drag from anywhere - LandMap.pressBoxes()): a box drawn from the press to (boxX, boxY), in the screen's pixels. |
| 434 | `private double boxX, boxY` |  |
| 436 | `private Label headMoney` | The head's money and its pay toggle (land's own, in the money the toggle pays in: the window's header, kept in view over this pane since MS1, says neither), what they were drawn for, and the note of what the picks los... |
| 437 | `private HBox headPay` |  |
| 438 | `private String headFor` |  |
| 439 | `private Label headNote` |  |
| 440 | `private javafx.scene.control.Tooltip headNoteTip` |  |
| 442 | `private boolean running, dirty` |  |
| 447 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 449 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 450 | `private int scheduleMonth` |  |
| 451 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 453 | `private TrainSchedule trainSchedule` | The trains' schedule drawn from (0.8.2, batch FR1): its boat-month, the routes and the game month it was made on. |
| 454 | `private int trainMonth` |  |
| 455 | `private List<TrainSchedule.Route> trainRoutes` |  |
| 457 | `private CallPartners callPartners` | The partners the month's boats and trains go to (WN7; partnersNow()), and those each schedule was made with: a schedule is made again when they change. |
| 459 | `private double[] pointerAt` | The pointer over the canvas in pixels, or null when it is off the map or pressed: the boat or train it is on is named (partnerLabel()). |
| 465 | `private Object labelCall` | The call (a BoatSchedule.Call or a TrainSchedule.Call) of the boat or vehicle nearest the pointer in the frame being drawn, within HIT_PX, how near (labelScore: its distance from the pointer in plots to its hull or bo... |
| 466 | `private double labelScore, labelX, labelY, hitX, hitY, hitTol` |  |
| 467 | `private final Map<String, Double> callWidths` |  |
| 469 | `private boolean boatsLive` | Whether boats - or since 0.8.2 trains, and since 0.8.8 trucks - are in view (the frame goes on), and the clock they were last drawn at. |
| 470 | `private double drawnClock` |  |
| 479 | `private final Map<YardKey, WritableImage> yardImages` | The yard pictures, the latest used kept (YARD_IMAGES_KEPT): the screen's thread's. |
| 483 | `private final WritableImage[] gapImages` | The one-plot pictures of the quay track between two boxes, by sea side, and of a lead behind a box, by sea side and end (side * 2, one more for the high end); made when first drawn. |
| 485 | `private double[] yardBerths` | The tonnes a month the terminals standing handle by kind (Ports.berths()), the game month and the shore (and its version) they were found for: found once a month, not once a work. |
| 486 | `private int yardBerthsMonth` |  |
| 487 | `private CityShore yardBerthsShore` |  |
| 488 | `private long yardBerthsVersion` |  |
| 490 | `private Map<Long, CityShore.Work> craneWorks` | The works by their berth (QuayCrane.worksByBerth()), and the shore (and its version) they were found for: found again only when the shore moves. |
| 491 | `private CityShore craneShore` |  |
| 492 | `private long craneVersion` |  |
| 493 | `private final AnimationTimer pump` |  |
| 497 | `private final ImagePattern hatch` |  |
| 500 | `private final ImagePattern islandHatch` | The amber hatch over the city's ground that no port links (ET5): the offers' hatch drawn in the warning colour. |
| 505 | `private GlobeFrame globe` | The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. |
| 506 | `private World.Key globeKey` |  |
| 507 | `private EarthData globeData` |  |
| 509 | `private long reachFor` | The land and offers its reach was found for. |
| 512 | `private WritableImage globeImg` | The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. |
| 513 | `private int[] globePx` |  |
| 514 | `private GlobeFrame globeDrawn` |  |
| 515 | `private int globeMode` |  |
| 516 | `private long globeInputs` |  |
| 518 | `private long inputs` | Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. |
| 520 | `private double[] patch` | The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. |
| 523 | `private byte[] pieceCls` | The GROUND tier's pieces past the plane, for one view at rest: its codes - class and relief (GlobePicture.code()) - a pixel (-1 not in yet), the view and its number. |
| 524 | `private GlobeFrame pieceView` |  |
| 525 | `private volatile int pieceGen` |  |
| 527 | `private int[] carryPx` | The last whole picture at rest, carried pixel by pixel while the next is drawn. |
| 528 | `private GlobeFrame carryView` |  |
| 534 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when the keys last moved the view (0 when none is held). |
| 535 | `private long keyNanos` |  |
| 538 | `private int legendTier` | The tier the legend was laid out for, and whether with the relief's entries (LandMap.reliefDrawn()). |
| 539 | `private boolean legendRelief` |  |
| 1068 | `private volatile long[] nodesWanted` | The far nodes the view being painted wants - {plots a pixel, nx0, nx1, ny0, ny1} - which WORKER's queue reads from its own thread (MS2). |
| 1173 | `private javafx.scene.text.Font worldLabelFont` | The world line's label as measured: its font, its width (and the shared label's) and its height above the baseline, kept until the font changes (FX2, FX5). |
| 1174 | `private double worldLabelW, worldLabelBothW, worldLabelH` |  |
| 1698 | `private final Map<Long, DistrictTrucks> districtTrucks` | The districts' schedules by CityMap.DistrictTrips.key(): thrown out with the shares when the month, the game month or the trips turn. |
| 1701 | `private int truckMonth` | The boat-month and the game month the schedules were made on, and the trips (CityMap.tripsDrawn()) they were made against. |
| 1702 | `private List<CityMap.DistrictTrips> truckTrips` |  |
| 1705 | `private double[][] truckPerBuilding` | What one building of each type carries in the month billed and the city's truck tonnes by load (TruckSchedule.perBuilding(), Freight.truckedIn() and the others): what a district's share is made of, kept with the sched... |
| 1706 | `private double[] truckCityTonnes` |  |
| 1708 | `private Map<Long, double[]> truckShares` | ...and each district with trips' catchment's shares of them by CityMap.DistrictTrips.key() (TruckSchedule.sharesByTrips()): its own works' tonnes and those of the districts about it that have no trips. |
| 1711 | `private int trucksStand` | Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the largest of the districts' drawn, which is the city's N. |
| 1984 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 1985 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 1986 | `private long shapesFor` |  |
| 1989 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 1990 | `private long labelsFor` |  |
| 1991 | `private double labelsAtScale` |  |
| 1996 | `private List<CityMap.Tinted> tints` | The zones' districts as last worked out (CityMap.zoneTint()), and what from: the map, its changes and the zones. |
| 1997 | `private CityMap tintsMap` |  |
| 1998 | `private long tintsAt` |  |
| 1999 | `private String tintsKey` |  |
| 2023 | `private List<long[]> islandRuns` | The runs of owned dry ground on landmasses no port links, as LandMap.unlinkedRuns() gave them, the chips beside them, and what they were worked out for: the linked set's version and the land's stamp. |
| 2024 | `private List<LandMap.Chip> islandChips` |  |
| 2025 | `private long islandFor` |  |
| 2068 | `private final Map<String, Double> chipWidths` | The chip lines' widths kept by string, as nameW keeps the names' (the plate is IslandChip's). |
| 2132 | `private final Map<Integer, List<double[]>> freeCells` | Each offer's free ground as rectangles of plots (LandMap.freeCells()) and its edge as runs (LandMap.freeOutline()), by offer id, for the land and listing above: found with the holes, not every frame. |
| 2133 | `private final Map<Integer, double[][]> freeEdge` |  |
| 2136 | `private java.util.Set<Long> groundFor` | The picked blocks' free ground as cells and its edge as runs (LandMap.freeCells(), freeOutline() of the blocks): worked out again only when the pick's blocks (the set picks.blocks() hands out is a new one at every cha... |
| 2137 | `private long groundShapesFor` |  |
| 2138 | `private List<double[]> groundCells` |  |
| 2139 | `private double[][] groundEdge` |  |
| 2142 | `private String previewFor` | ...and the blocks a box being drawn would take: its cells and edge, for the box of plots, the pick and the land it was found for. |
| 2143 | `private List<double[]> previewCells` |  |
| 2144 | `private double[][] previewEdge` |  |
| 2457 | `private boolean globeHalf` | The globe drawn while it was half its resolution (the view on the move). |
| 2555 | `private GlobeFrame globeNodesFor` |  |
| 2556 | `private long[] globeNodesAt` |  |
| 2590 | `private GlobeFrame chartNodesFor` |  |
| 2591 | `private long[] chartNodesAt` |  |
| 2602 | `private volatile long[] chartsWanted` | The chart nodes WORKER's queue reads from its own thread (chartNodes()'s range for the view being painted): a node asked for a view that has moved on is dropped unread. |
| 2605 | `private int chartGen` | The globe's generation as far as chart nodes go: moved by resetGlobe(), so a node that comes back from WORKER after it is dropped. |
| 3110 | `private final Map<Integer, double[]> globeLabelAt` | The squares the globe's numbers stand on, by offer id, for the land they were found for and the scale (their last element). |
| 3111 | `private long globeLabelsFor` |  |
| 3318 | `private volatile String quoteWanted` | The block the hover wants quoted now: its key (block, level, ground, month and office), or "" while the pointer is over none. |
| 3321 | `private final Map<String, LandParcel> quotes` | The quotes the hover has had (LandMarket.quote(), a null for a block with none), by key, the latest asked kept (QUOTES_KEPT): the screen's thread's. |
| 3326 | `private final Set<String> quotesAsked` | The quotes on QUOTES' queue or running and not back: a block asked once, not again for each frame the pointer stays on it. |
| 3329 | `private LandMarket.Quoter quoter` | The office as it stood (LandMarket.quoter()) for the office, level, ground and month in quoterFor, and what it was made for; null where the office has none. |
| 3330 | `private String quoterFor` |  |
| 4317 | `private static volatile CompletableFuture<NationsMap> nationsBuild` | The nations' map, built once for the process on WORKER from the world file, which is kept open for the game's life - the map's exact ownerAt() reads it, so it is never closed. |
| 4350 | `private Nations nations` | The nations' figures of the game on the screen (Game.getNations(), asked on WORKER: its first build is the path to 2100), and the game they are of; and the game they were asked for. |
| 4351 | `private Game nationsOf, nationsAskedOf` |  |
| 4353 | `private boolean nationsWaiting` | Whether this view waits for the map's build to come back (it draws again when it does). |
| 4356 | `private List<NationsMap.Mark> marks` | The dots and labels of the last view drawn, and what they were made for: the view (a copy), the year, the figures and the map. |
| 4357 | `private GlobeFrame marksView` |  |
| 4358 | `private int marksYear` |  |
| 4359 | `private Nations marksFrom` |  |
| 4360 | `private NationsMap marksOf` |  |
| 4363 | `private javafx.scene.text.Font nameFont, figuresFont` | The label fonts, and the width of words in them kept by string (the names never change, the figures once a year). |
| 4364 | `private final javafx.scene.text.Text measure` |  |
| 4365 | `private final Map<String, Double> nameWidths` |  |
| 4366 | `private final java.util.function.ToDoubleFunction<String> nameW` |  |
| 4370 | `private EarthProj planeProj` | The plane's projection round the city's spot, kept per world (the borders that cross the city's ground are found on it). |
| 4371 | `private World.Key planeProjKey` |  |
| 4374 | `private TradeLanes lanes` | The city's trade as the screen holds it (WN3): the lanes of the last figures and the map they were made for, a generated world's arrows. |
| 4375 | `private NationsMap lanesOf` |  |
| 4376 | `private TradeLanes.Arrows arrows` |  |
| 4379 | `private Label nationsChip, bordersChip, tradeChip, resourcesChip` | The head's chips and their group, and what they were last styled for. |
| 4380 | `private HBox nationsGroup` |  |
| 4381 | `private String chipsFor` |  |
| 4384 | `private VBox nationBox` | A nation's card in the pane, the nation it is of (0 for none), what it was drawn for, and the point on the screen it stands beside. |
| 4385 | `private int nationShown` |  |
| 4386 | `private String nationKey` |  |
| 4387 | `private double nationX, nationY` |  |
| 5345 | `private final Label legendNote` |  |
| 5347 | `private String legendZones` | The zones the legend was laid out with (0.8.0): their words, joined. |
| 5348 | `private final GridPane legendGrid` |  |
| 5349 | `private final Label shipsLine` |  |
| 5351 | `private final Label trucksLine` | The legend's trucks line: shown while trucks are drawn this frame (legendTrucks()). |
| 5393 | `private HBox legendHead` | The legend's head line, its chevron and its "click to fold" (WN2), and its body. |
| 5394 | `private VBox legendBody` |  |
| 5395 | `private final Label legendChevron` |  |
| 5497 | `private boolean cinematic` | Whether the replay's cinematic mode is on for this view: the scale bar is not drawn, the head and the legend are hidden, the cursor is none. |
| 5500 | `private String paddedStyle` | The pane's style as buildPane() set it, kept to put back what cinematic mode took out (its padding). |
| 5503 | `private final javafx.beans.value.ChangeListener<javafx.scene.Cursor> cursorOff` | Holds the canvas's cursor at none while cinematic mode is on, whatever cursorFor() or a press asks for. |
| 5580 | `private boolean keepPictures` | Set while reset() runs for a replay's quick step: the pictures are kept, stale (staleAll()), not thrown out. |
| 5583 | `private long camNanos` | When the camera's last frame was taken, for its dt (0 when it has not run). |
| 5760 | `final int serial, of` |  |
| 5761 | `final boolean months` |  |
| 5763 | `int next, passed` | The plan's next view, the views passed, whether the view {@code next} is taken, and since when the worker has sat idle on it (0 for not idle). |
| 5764 | `boolean applied` |  |
| 5765 | `long idleSince` |  |
| 5767 | `boolean running` | From the click's wait to the move's end: the run takes pictures from the store. |
| 5769 | `final Set<Long> stuckNodes` | The far nodes and chart nodes that were asked and never came back, when their view was passed. |
| 5771 | `final HashMap<Integer, HashMap<Long, TileCopy>> tilesAt` | Month, then (cache index and tile key) to the tile's picture; and (cache index and tile key) to the copies by stamp. |
| 5772 | `final HashMap<Long, List<TileCopy>> tileCopies` |  |
| 5774 | `final HashMap<Integer, HashMap<Long, NodeCopy>> nodesAt` | Month, then node key to the node's picture; and node key to the copies. |
| 5775 | `final HashMap<Long, List<NodeCopy>> nodeCopies` |  |
| 5777 | `final Set<CameraPath.View> planned` | Every view the load painted, so the run's tally can count a frame that showed any other (MoveTally.offPlan). |
| 5787 | `private MoveLoad move` | The loaded move, or null (always, outside a replay's glide or fly). |
| 5925 | `private long framesDrawn` |  |
| 6153 | `long frames, unpaintedFrames, halfFrames` | Frames tallied; of them the ones with anything black, coarse or stale; and those a globe drew at half resolution. |
| 6155 | `long black, coarse, stale` | Cells drawn as nothing, from a stand-in, from an older version: each summed over the frames. |
| 6157 | `long asks, evicted` | WORKER tasks submitted, and pictures let go by the caches, during the frames (from the camera's frame to the tally's, summed). |
| 6159 | `long offPlan` | Frames of a loaded move's run that showed a view its load never painted (CM1: the run stays on the plan, at any frame rate). |
| 6161 | `long frameNanosSum, worstFrameNanos` | The gaps between consecutive tallied frames, summed and the worst, in nanoseconds (the first frame after reset() has no gap). |
| 6163 | `final Map<String, long[]> byWhere` | By where the frame drew: {frames, unpainted frames, black, coarse, stale}. |
| 6166 | `private long lastNanos` |  |
| 6167 | `private boolean timed` |  |
| 6223 | `private final MoveTally moveTally` |  |
| 6229 | `private boolean tallyThisFrame` | Set by replayCamera() for a frame of a glide or fly, and cleared by tick() when it has tallied the frame; WORKER's task count and the caches' evictions as that frame began. |
| 6230 | `private long tallyTasks, tallyEvicted` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 246 | 6085 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 294 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 294 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 329-402)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 14 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count ... |
| 359 | 23 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 365 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 366 | 6 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |
| 373 | 8 | `void trim()` _(in MapView.Pictures)_ | The pictures over `most` let go, the eldest first (what a pin that is taken off leaves to do). |

### what the player is doing (lines 403-415)

### buying from the map (0.7.137, batch M6) (lines 416-443)

### the boats (0.7.97) (lines 444-501)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 1 | **type** `private record YardKey(long x0, long y0, int w, int h, int type, int side, HarbourYard.Kind kind, int level...` | What a yard picture is made from (HB2): the box, its side and type, and the kind, stock level and rail bits it is drawn at. |

### the globe (0.7.135, batch E9) (lines 502-685)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 41 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 607 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 610 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 613 | 18 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 632 | 30 | `private void reset()` |  |
| 664 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 671 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 678 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 686-1111)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 52 | `private void tick()` |  |
| 752 | 57 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 811 | 9 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 822 | 9 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 832 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 848 | 55 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - keyed (MapTiles.tileKey(), batch UP5), stamped again only if the key moved, and painted only wh... |
| 905 | 18 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 925 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 935 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 940 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 944 | 8 | `private boolean paintNodes(MapFrame f, long deadline)` |  |
| 963 | 31 | `private void askPictures(MapFrame f)` | The district pictures the far nodes drawn this frame lacked (0.7.128, batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order, while the view's districts fit wha... |
| 1005 | 61 | `private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept, MapFrame asker)` | The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). |
| 1077 | 31 | `private void askNodes(List<long[]> asking, long plots, long span, World.Key world, long[] wanted)` | The far nodes a call of paintNodes() found it lacked, each {nx, ny, key}, their ground asked of WORKER from the middle of `wanted` out (MS2). |
| 1110 | 1 | `long farRecoloured()` | How many far nodes this view has coloured (batch UP3): the figure a probe reads against the nodes in view. |

### THE DRAWING (lines 1112-1167)

| line | len | member | says |
|---:|---:|---|---|
| 1116 | 51 | `private void draw()` |  |

### THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) (lines 1168-1252)

| line | len | member | says |
|---:|---:|---|---|
| 1188 | 40 | `private void worldLines(GraphicsContext gc, MapFrame f)` | The highway's and the main line's faded runs past the city's ground (CityMap.worldLines()): each from its point along its way out over dry ground, as far as the land runs (CityMap.WorldLine.reach(), at most CityMap.WO... |
| 1230 | 22 | `private static double[] rayRect(double x, double y, double dx, double dy, double tMax, double w, double h)` | The part of the segment (x, y) + t (dx, dy), 0 <= t <= tMax, that lies on the canvas, {x0, y0, x1, y1}; null when it misses it. |

### THE BOATS (0.7.97, batch O13) (lines 1253-1361)

| line | len | member | says |
|---:|---:|---|---|
| 1258 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 1275 | 86 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: the lanes on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), a... |

### the harbour's quay and its boats alongside (HB1, 2026-11-08) (lines 1362-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1370 | 20 | `private void harbourQuay(GraphicsContext gc, MapFrame f)` | The harbour's quays as walls (CityShore.walls()) over the tiles and under the boats, from MapFrame.DETAIL_FROM in and not below it (HB1; Jerus, 2026-10-31, HB2's "at 500m is where the detail would be"): in the colour ... |

### the harbour's yards, their stock and the quay's rail (HB2, 2026-11-09) (lines 1391-1474)

| line | len | member | says |
|---:|---:|---|---|
| 1403 | 64 | `private void harbourYards(GraphicsContext gc, MapFrame f)` | The terminals' yards and the quay's rail as pictures over the tiles' flat boxes and under the walls and the boats, from MapFrame.DETAIL_FROM in and not below it: each work of the shore that meets the frame as HarbourY... |
| 1469 | 5 | `private static WritableImage imageOf(int[] argb, int w, int h)` | An image of w by h ARGB pixels (non-premultiplied, row by row) for the canvas to draw: a yard's picture or a plot of the quay's rail. |

### the quay's crane (HB2, 2026-11-09) (lines 1475-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1485 | 49 | `private void cranes(GraphicsContext gc, MapFrame f, List<BoatSchedule.Boat> boats, List<BoatSchedule.Route> routes, double t)` | The cranes over the boats docked in the frame (the caller's test is MapView's DETAIL_FROM: not below it), over their ships: for each docked boat the terminal at the berth its route ends in (QuayCrane.worksByBerth(), f... |
| 1536 | 5 | `private static void fillSpan(GraphicsContext gc, boolean vertical, double across0, double across1, double along0, double along1)` | Fills, in the colour set, the rectangle from across0 to across1 and along0 to along1 (the along pair either way round): x is across for a vertical line and y along it, the other way for a horizontal one. |

### THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 1542-1611)

| line | len | member | says |
|---:|---:|---|---|
| 1555 | 56 | `private void trains(GraphicsContext gc, MapFrame f, int level)` | The trains over the tiles, from L1 in, as the boats: the month's schedule (TrainSchedule, from the railway's tonnes by kind) on the map's routes (CityMap.trainRoutes()) at the boats' clock. |

### a boat or a train names its partner (WN7) (lines 1612-1691)

| line | len | member | says |
|---:|---:|---|---|
| 1618 | 12 | `private void aimLabel(MapFrame f)` | The pointer and the reach of it in plots for the frame about to be drawn, and nothing named yet: boats() and trains() offer the boats and vehicles they draw to nameable() as they draw them, so no second walk of the sc... |
| 1635 | 9 | `private void nameable(Object call, double gap, double middle, double sx, double sy)` | A boat or a vehicle the pointer may be on, `gap` plots from its hull or body (0 inside), its middle `middle` plots off, drawn at (sx, sy) on the canvas: kept if it is within the reach and nearer than the one kept, the... |
| 1646 | 9 | `private static double polygonGap(double px, double py, double[] xs, double[] ys)` | How far a point is from a polygon's outline in plots: 0 inside it. |
| 1657 | 5 | `private static double segmentGap(double px, double py, double ax, double ay, double bx, double by)` | How far a point is from the segment (ax, ay) to (bx, by). |
| 1668 | 23 | `private void partnerLabel(GraphicsContext gc, MapFrame f)` | The label of the boat or train under the pointer, in the world lines' style (the mono font at Palette.SIZE_BODY, white at WORLD_LINE_ALPHA, a halo under it to read on the tiles): CallPartners.words() of its call, up a... |

### the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) (lines 1692-1992)

| line | len | member | says |
|---:|---:|---|---|
| 1695 | 1 | **type** `private record DistrictTrucks(List<TruckSchedule.Route> trips, TruckSchedule schedule)` | A district's trucks' schedule, with the trips it was made on: made again when its district's trips are another list. |
| 1723 | 37 | `private void trucks(GraphicsContext gc, MapFrame f, int level)` | The trucks over the tiles, at the near view alone and while the setting is on (TruckSchedule.drawnAt()): each district's trips (CityMap.tripsDrawn(), a truck's whole way from the world line to a works' gate and back, ... |
| 1762 | 7 | `private int fillTrucks(GraphicsContext gc, MapFrame f, TruckSchedule schedule, long x0, long y0, long x1, long y1, double t)` | One schedule's trucks in the box filled in their colours, or nothing when it has none or no route of it crosses the view; the loads a truck of it stands for, or 0 when none was drawn. |
| 1771 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 1776 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 1782 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 1789 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 1794 | 23 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 1818 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 1848 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 1895 | 87 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched shapes - each its free ground only, the city's own ground in ... |

### the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") (lines 1993-2019)

| line | len | member | says |
|---:|---:|---|---|
| 2002 | 17 | `private void zoneTints(GraphicsContext gc, MapFrame f, Game g)` | The city's zones over the ground, under the offers and the city's edge: each zone's districts in its sector's colour at LandMap.ZONE_ALPHA, the districts the map picks for it (CityMap's ZONES BY DISTRICT); nothing wit... |

### the ground no port links (ET5, slice D; the ET5 design, section 5) (lines 2020-2439)

| line | len | member | says |
|---:|---:|---|---|
| 2033 | 33 | `private void islandHatch(GraphicsContext gc, MapFrame f, Game g, CityLand land)` | The city's ground that no port links, hatched in amber over the ground (under the offers and the city's edge) and named on a chip at the middle of each landmass: "Island 2 · 12.0 km² · not linked: needs a port here (h... |
| 2076 | 15 | `private void islandChips(GraphicsContext gc, MapFrame f, Landmasses lms)` | Each such landmass's chip (LandmassView.chipLines()): two lines of amber mono caption on a dark rounded plate the width of the longer, centred on the middle of its owned tiles, kept inside the view (MT1, BK1-L). |
| 2093 | 7 | `private List<String[]> islandEntries()` | The amber legend entry for that hatch (LandMap.unlinkedLegend()) while any landmass is unlinked, as the zones' entries are given: {css colour, words}. |
| 2102 | 10 | `private List<String[]> zoneEntries()` | The zones' legend entries on the plane (0.8.0): each zone set, by its sector's label. |
| 2113 | 17 | `private void shapes(CityLand land, LandMarket market)` |  |
| 2147 | 13 | `private void groundShapes(CityLand land)` | The picked blocks' shapes, kept (groundCells, groundEdge) for what is picked and the land it lies on. |
| 2165 | 19 | `private boolean previewShapes(CityLand land)` | The blocks the box being drawn would take, as cells and an edge (previewCells, previewEdge): worked out when the plots it covers, the pick or the land change, not every frame. |
| 2193 | 6 | `private static void edgeRuns(GraphicsContext gc, MapFrame f, double[][] runs, int width, boolean dashed, Color colour)` | Runs of an outline on whole pixels as one shape (BL1), `width` px deep inside the ground they bound - one the city's edge's, two the picked ground's - solid or dashed (DASH_ON on and the rest off, from each run's own ... |
| 2201 | 29 | `private static void runRects(GraphicsContext gc, MapFrame f, double x0, double y0, double x1, double y1, int width, boolean das...` | One run of an outline added to the path as its pixels' boxes (MapFrame.runOnScreen()), `width` deep inside the ground it bounds and cut into dashes when asked. |
| 2232 | 13 | `private void pickedGround(GraphicsContext gc, MapFrame f)` | The picked blocks (BL1): their free ground tinted as a picked offer's is and its edge solid in the offers' pink, two px deep, as one shape - the city's ground inside a block it owns part of left clear. |
| 2250 | 11 | `private void boxPreview(GraphicsContext gc, MapFrame f)` | The blocks a box being drawn would take, tinted under it and edged a pixel wide (BL1), so the snap to the block grid is seen before the release. |
| 2263 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 2275 | 27 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 2307 | 10 | `private void newTag(GraphicsContext gc, LandParcel p, double x, double y, double boxH)` | ...under the number at (x, y), where the offer's box on the screen is at least two numbers tall (MapFrame.PLACE_LABEL_FROM each). |
| 2319 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 2346 | 3 | `private static long landKey(CityLand land, LandMarket market)` | What the land's deposits are found again by (deposits(), depositIcons()): its purchases, its stamp and the next offer's number - any of which moving makes the fields in and about it a new list. |
| 2359 | 22 | `private void depositIcons(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | (LM1) The far view's deposits: an icon DepositMarks.ICON_PX across at each deposit in view's centre plot, in its resource's colour, edged dark like the dots, with its mark in the caption font - dark on a light colour,... |
| 2383 | 5 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in MapFrame.SCALE_BAR_PX, its length in words. |
| 2390 | 17 | `private static void scaleBar(GraphicsContext gc, double h, double px, String words)` | ...drawn: `px` long at the bottom left of a view `h` high, and its words. |
| 2409 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 2414 | 3 | `static String css(int argb)` | ...as CSS. |
| 2419 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |
| 2430 | 9 | `private static ImagePattern islandHatchPattern()` | The hatch over the ground no port links (ET5): the offers' diagonals drawn in the warning amber (Palette.WARN, #e3b341) at the same weights. |

### THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) (lines 2440-3314)

| line | len | member | says |
|---:|---:|---|---|
| 2460 | 1 | `private boolean onGlobe()` | Whether the expanded map draws the globe: on an Earth world, wider than the handoff - or, since MS1, closer than it with the view's centre past the city's reach, where the plane is not drawn (GlobeFrame.tier()). |
| 2463 | 1 | `private boolean globeShown()` | ...or lays its picture over the plane's in the crossfade's notch (GlobeFrame.crossfade(), the view's: whole where there is no plane under it). |
| 2466 | 13 | `private void resetGlobe()` | A new city, or another world: no globe until the map is over the window on Earth again - and no chart nodes (MS3): those in flight come back to another generation and are dropped. |
| 2481 | 14 | `private void resetGlobePicture()` | What the globe drew from and drew: another map, or another view of the world. |
| 2497 | 24 | `private void globeUp(CityLand land, LandMarket market, long landKey)` | The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. |
| 2523 | 4 | `private void follow()` | The view moved on the globe: where it draws the plane (the plane's scales, its centre within the reach) the plane's frame follows it - past the reach, closer than the handoff, there is no plane to follow (MS1); the vi... |
| 2529 | 25 | `private long[] globeNodes(GlobeFrame v)` | The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws (GlobeFrame.drawsHold(): since MS2 at the plane's scales the part of it the world's square hold... |
| 2565 | 24 | `private long[] chartNodes(GlobeFrame v)` | The chart nodes the globe draws for a view (MS3) - {plots a pixel, the chart's id, nx0, nx1, ny0, ny1} - over the part of it the view's chart draws (GlobeFrame.chartHolds(): at the plane's scales the ground past Globe... |
| 2597 | 3 | `private static long chartKey(long id, long plots, long nx, long ny)` | A chart node's key (MS3): the chart's id (GlobeFrame.chartFor()'s row and column, 13 bits) in the top 20 bits, the stride's power of two in 4, and the node's two indexes in 20 bits each (a chart reaches some 400 nodes... |
| 2612 | 18 | `private boolean paintGlobe(long deadline)` | A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack; and the chart nodes past the... |
| 2638 | 52 | `private void askCharts(long[] r)` | The chart nodes `r` (chartNodes()) the cache lacks asked of WORKER, each once, from the middle of the range out (MS3; askNodes()'s rule for the plane's far nodes): WORKER makes the node's classes and relief (MapTiles.... |
| 2692 | 18 | `private void chartColoured(int id, long plots, long nx, long ny)` | A chart node come in: where the globe's still picture shows it, laid over again next frame - nodeColoured()'s rule for the plane's, the node's corners and mid-points from its chart (east and south of the anchor on its... |
| 2712 | 14 | `private void nodeColoured(long x0, long y0, long span)` | A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. |
| 2728 | 9 | `private void addPatch(double x0, double y0, double x1, double y1)` | The still picture's box to be drawn again, grown to take in another. |
| 2746 | 32 | `private void composeGlobe(int mode)` | The globe's picture of the view, drawn again when the view, the way it is drawn or what it is drawn from moved: at half resolution while the view moves (its frame a copy of half the size at twice the km a pixel), at i... |
| 2780 | 48 | `private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1)` | The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image: the plane's nodes and, past the hand-over at the plane's scales, the view's chart nodes... |
| 2830 | 18 | `private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic)` | Past the plane and the charts, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. |
| 2850 | 7 | `private void drawGlobeImage(GraphicsContext gc)` | The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). |
| 2870 | 39 | `private void askPieces(GlobeFrame view)` | The GROUND tier's pieces past the plane for a view come to rest: the far rule through the camera (GlobeFrame.groundCodes()) a PIECE_PX square at a time on WORKER, from the middle out, each one with some of its pixels ... |
| 2911 | 7 | `private void pieceIn(int gen, int[] q, byte[] b)` | A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. |
| 2920 | 31 | `private void drawGlobe()` | The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its block lines past the reach at the plane's scales, its edge through the camera in the PI... |
| 2956 | 10 | `private double[][] onGlobe(double[] xs, double[] ys)` | A shape of plots through the camera: its corners on the screen, or null where one is not seen. |
| 2975 | 84 | `private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market)` | The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - each offer its free ground (its cells, LandMap.freeCells(), the city's own ground ... |
| 3061 | 4 | `private double plotPx(double x, double y, long span)` | Pixels a plot spans on the globe where the plane's plot (x, y) is: the camera's step over `span` plots east of it over the span, 0 where either end is not seen. |
| 3067 | 15 | `private boolean cellsPath(GraphicsContext gc, List<double[]> cells)` | Cells of plots (rectangles {x0, y0, x1, y1}) as one path through the camera, each a quadrilateral of its four corners - a cell with a corner not seen left out, as boxPicks() does; false when none is in the path. |
| 3084 | 10 | `private void runsOnGlobe(GraphicsContext gc, double[][] runs, int width, boolean dashed, Color colour)` | The runs of an outline through the camera, a line each between its ends where both are seen: `width` px, solid or dashed as the plane's offers' edges are. |
| 3096 | 12 | `private double[] labelSquare(CityLand land, LandParcel p, double plotPx)` | Where an offer's number stands on the globe, the square of plots (LandMap.labelBlock()) found for the camera's scale - a number's PLACE_LABEL_FROM px in plots, to a power of two - and kept until the land moves; null w... |
| 3122 | 17 | `private void globeBlockLines(GraphicsContext gc, int level)` | The city's block lines through the camera past the reach at the plane's scales (MS2): the plane draws them faint over its whole ground where a block is at least MapFrame.BLOCK_LINES_FROM px across (blockLines()), and ... |
| 3147 | 17 | `private boolean blockLine(GraphicsContext gc, double ax, double ay, double bx, double by, int depth)` | One block line from plot (ax, ay) to (bx, by) through the camera onto the path: its ends, and its middle's plot against the chord, in two where it strays more than BLOCK_LINE_SAG_PX. |
| 3166 | 4 | `static double distanceFromChord(double[] m, double[] a, double[] b)` | How far a point is from the chord a to b, in pixels. |
| 3172 | 12 | `private void globeEdge(GraphicsContext gc, CityLand land)` | The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. |
| 3186 | 11 | `private void ring(GraphicsContext gc, CityLand land)` | The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. |
| 3199 | 5 | `private void globeScaleBar(GraphicsContext gc)` | The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. |
| 3206 | 47 | `private void globeHover(double sx, double sy)` | The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it (drawnAt(): past the hand-over, MS3, the cha... |
| 3267 | 47 | `private List<String> groundLines(long x, long y)` | What the hover card says over free ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: over a block that may be picked, "Your ground: one block", its area by kind and its pric... |

### the hover's quote, away from the screen's thread (WN5b, 2026-11-02) (lines 3315-3401)

| line | len | member | says |
|---:|---:|---|---|
| 3337 | 23 | `private void askQuote(LandMarket.Quoter use, String quoteKey, long[] r)` | Asks QUOTES for a block's quote, `r` the block's {x0, y0, x1, y1}. |
| 3362 | 7 | `private static void groundInto(List<String> lines, List<String> ground)` | The ground lines in the hover card where "Not the city's" stood (after its first line when the card has not that line), their last line, the blue one, at the card's foot. |
| 3375 | 14 | `private byte drawnAt(double sx, double sy)` | The ground drawn at a screen point on the globe, past the plane, as a code - its class and relief (GlobePicture.code()): where the view's chart draws it (GlobeFrame.chartHolds(), MS3: at the plane's scales the ground ... |
| 3391 | 10 | `private byte chartCode(double sx, double sy)` | The code (GlobePicture.code()) of the pixel of the view's chart node under a screen point - the node ground() lays there (GlobeFrame.chartAt()) - or -1 where the node is not in yet. |

### the keys (0.7.135; spec-globe 3.11) (lines 3402-3443)

| line | len | member | says |
|---:|---:|---|---|
| 3405 | 24 | `private boolean keys()` | A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not h... |
| 3431 | 12 | `boolean panKey(KeyCode k, boolean down)` | W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. |

### THE POINTER AND THE KEYS (lines 3444-3791)

| line | len | member | says |
|---:|---:|---|---|
| 3448 | 98 | `private void handlers()` |  |
| 3548 | 7 | `private void zoomBig(double sx, double sy, double notches)` | The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. |
| 3561 | 4 | `private long[] plotAt(double sx, double sy)` | The plot of the city's plane under a screen point as the map draws it - the plane's own frame, or on the globe the camera's, where the plane is drawn (GlobeFrame.planeHolds()) - or null where there is none (the globe'... |
| 3567 | 9 | `private double[] plotXY(double sx, double sy)` | ...as the plane's own fractions of plots {x, y} (what a box's corners are read by: LandMap.plotBox()), or null where there is none. |
| 3582 | 12 | `private long[] boxPlots(double sx0, double sy0, double sx1, double sy1)` | The plots a box between two screen points covers (LandMap.plotBox()), its four corners read through plotXY() - the plane's frame, or on the globe the camera where the plane is drawn, a corner it does not hold left out... |
| 3601 | 6 | `private boolean pressBoxes(double sx, double sy, boolean shift)` | Whether a left press at a screen point draws a box rather than pans (MS1, widened by BL1): where the offers are drawn to be picked (canPick()), Picks.pressBoxes() of the plot under it - an offer's free ground, a picke... |
| 3617 | 37 | `private void click(double sx, double sy, boolean shift)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on - on the globe only where the city's plane is drawn (0.7.135). |
| 3662 | 23 | `private boolean blockClick(long x, long y, boolean shift)` | A click on ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: a free block that may be picked is picked alone, and with Shift toggled; a block already picked keeps the picks ... |
| 3687 | 50 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 3744 | 5 | `private void cursorFor(boolean boxHere)` | The cursor says what a left press would do (MS1, BL1): the crosshair where it would draw a box - over an offer's free ground or ground that is picked or may be (`boxHere`, LandMap.pressBoxes() without Shift), or anywh... |
| 3754 | 3 | `private static boolean isHint(String line)` | Whether a hover card's line is the blue last line telling the player what a click does. |
| 3759 | 23 | `private void showCard(List<String> lines, double sx, double sy, double vw, double vh)` | The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - the hint last (CLICK_TO_BUY, CLICK_TO_PICK, CLICK_TO_DROP, and a nation's NationsMap.CLICK_FOR_CARD) in the accent's light... |
| 3787 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 3792-4235)

| line | len | member | says |
|---:|---:|---|---|
| 3825 | 1 | **type** `record Bought(List<Integer> ids, List<int[]> places, String title, double[] at, double[][] edge)` | A purchase from the card: the offers and drafts found listed when it was asked for, their places ({side, place, 1 for a draft or 0} each - a draft's place is where its middle lies, which no "next ground" line is about... |
| 3837 | 3 | `private boolean canPick()` | Whether the offers are drawn to be picked where the expanded map looks (star M6): on the plane at every scale, and on Earth in the globe's GROUND tier, where globeOffers() draws them - not its PICTURE tier, which draw... |
| 3842 | 7 | `private LandMap.ToScreen toScreen()` | A plot of the city's plane on the screen, as the expanded map draws it now: through the globe's camera, or the plane's frame. |
| 3851 | 3 | `private static double[] box(LandParcel p)` | An offer's box in plots, {x0, y0, x1, y1}. |
| 3856 | 11 | `private static double[] bounds(List<LandParcel> ps)` | ...and the bounds of several, or null with none. |
| 3869 | 9 | `private List<LandParcel> listed(List<Integer> ids)` | The listed offers among these ids, in their order. |
| 3884 | 10 | `private void boxed(double x0, double y0, double x1, double y1)` | A drag let go (B7, MS1; BL1: a box takes ground, not offers): the blocks the box covers that join the city added to the picks (Picks.addBox(), the box's corners through plotXY(): the snap to the block grid the preview... |
| 3896 | 13 | `void selectAll()` | "Select every offer" in the head, or Ctrl+A (B7): every offer listed onto the card, the office's best first, the card beside the best (star M6). |
| 3911 | 24 | `private void showBuyCard()` | The card drawn again for the picks as they stand, or put away with none listed. |
| 3937 | 8 | `private void hideBuyCard()` | The card put away. |
| 3947 | 5 | `private String cardKey(List<Integer> ids)` | What the card was drawn from: the picks, the toggle, the cash and the vault, the month and the listing. |
| 3954 | 16 | `private void syncCard()` | A frame's look at the card (tick()): drawn again when what it says moved - the month dropping the picks, an offer bought elsewhere falling out of them, the cash - and the receipt put away with its month. |
| 3972 | 5 | `private void layoutBuyCard()` | The card sized to what it says, then placed. |
| 3979 | 16 | `private void placeBuyCard()` | The card beside its box of plots as the view moves (each draw): at its right, else at its left, its top level with the box's, inside the view. |
| 3997 | 15 | `private double[] screenBox(double[] b)` | A box of plots on the screen, {x0, y0, x1, y1} round its corners as drawn now, or null when none is seen. |
| 4014 | 16 | `private void drawRings(GraphicsContext gc)` | The card's fields' sites, ringed wherever they lie (board 7): each a ring in its resource's colour on a dark halo, as the city's ring is drawn (ring()), round its square - RING_PX at least. |
| 4032 | 11 | `private void drawBox(GraphicsContext gc)` | The box Shift and a drag draws (B7): the offers' pink, faint inside, dashed round. |
| 4045 | 7 | `void closeCard()` | The card's ×, Esc, a click off the offers: no picks, no card. |
| 4054 | 5 | `void dropPick(int id)` | A chip's × on a card of several: that one dropped. |
| 4061 | 5 | `void dropGround(List<Integer> drafts)` | The "Your ground" chip's ×: every draft of the picked blocks dropped, which drops the blocks they were cut from - one chip for all of them, as the card names them as one (blocks that joined through them go too, said i... |
| 4068 | 5 | `void paidOtherwise()` | The toggle moved, on the card or the head: both drawn again in the other money. |
| 4075 | 12 | `private Bought boughtOf(List<Integer> ids)` | What a purchase from the card is of, asked for now. |
| 4089 | 9 | `void buy(List<Integer> ids)` | The card's Buy, or Enter (B2): the offers bought as the office's Buy buys them (LandScreen.buyOrFund()) - the office drawn again under the map, which stays - and the receipt by the ground; short after all, the funding... |
| 4100 | 9 | `void buyOnLoan(List<Integer> ids, Runnable issue)` | The card's bond or note (B1): issued as the funding page issues it, the offers bought on it (LandScreen.offers()' own action), and the receipt; what falls short shows the office's page, which comes back here. |
| 4111 | 10 | `void toFunding(List<Integer> ids)` | "All the ways to pay" (B5): the funding page for the picks, the map put away until the page is done and brought back at the view it left (back()). |
| 4123 | 6 | `void toOffice(int s, int p)` | "Open in the land office": the map put away on the offer's row, the office's side and pick. |
| 4137 | 11 | `void back(boolean fromFunding)` | The office drawn (LandScreen.showLandMenu()), `fromFunding` when the funding page or what it fell short with is what it comes back from: a purchase the card sent there brings the map back over the window at the view i... |
| 4150 | 27 | `private void showBought()` | The receipt by the ground just bought (board 6): the card's purchase, those of it no longer listed - "2 of 3 offers" when it fell short - in the office's words (LandScreen.mapReceiptNode()); with none bought, the card... |
| 4185 | 26 | `boolean cardKey(javafx.scene.input.KeyEvent e)` | Esc, Enter and Ctrl+A while the map is over the window, from UserInterface's key filter (0.7.137): Esc closes the card - a second Esc, not taken here, the map - Enter presses the card's main button (B2: Buy, or short ... |
| 4218 | 17 | `private void syncHead()` | The head's money, pay toggle and note, drawn again when the toggle, the cash, the vault, the month or the note moved. |

### THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) (lines 4236-4479)

| line | len | member | says |
|---:|---:|---|---|
| 4269 | 5 | `static String tradeTip()` | ...and the Trade routes chip's (WN3). |
| 4320 | 4 | `private static synchronized CompletableFuture<NationsMap> askNationsBuild()` | The build asked of WORKER the first time it is wanted. |
| 4326 | 22 | `private static NationsMap buildNations()` | The world file opened and the map made of it, on WORKER; null (and a line in the log) where there is no file or it will not open. |
| 4390 | 5 | `private static Map<String, Double> widths()` | A bounded map of widths by string, the most recently used kept. |
| 4397 | 10 | `private double widthOf(String words, javafx.scene.text.Font font, Map<String, Double> kept)` | The width of words in a font, kept: a Text measured once per string. |
| 4409 | 11 | `private void forgetNations()` | Another city or another game: the nations of the last one are not this one's. |
| 4427 | 35 | `private void nationsUp(CityLand land)` | Asked every frame (keepUp()): on an Earth world, once a chip is on or the map is over the window - the chips are shown only once the map is built, so a player who turned both off can turn them on - the nations' map is... |
| 4464 | 6 | `private NationsMap nationsMap()` | The nations' map when it is built and this city stands on the real Earth; null on a generated world, before the build and where the world file is not here. |
| 4472 | 1 | `private Nations nationsNow()` | The game's nations when they are in for the game on the screen, else null. |
| 4475 | 4 | `private NationsMap builtMap()` | The nations' map when it is built, whatever world this is: a generated world's arrows stand by its nodes though it draws no nations. |

### the city's trade (WN3) (lines 4480-4556)

| line | len | member | says |
|---:|---:|---|---|
| 4487 | 4 | `private TradeLanes.Feed tradeFeed()` | The city's trade by partner - THE ONE SEAM the wiring filled (WN3): the game's feed over last month's trade by partner and the year to date (Game.getTradeFeed(), an immutable snapshot published through a volatile, so ... |
| 4498 | 29 | `private CallPartners partnersNow()` | The partners the month's boats and trains go to (WN7): CallPartners over the feed's snapshot when it is one (PartnerTrade.Snapshot), the city's place - the real Earth's, the spot the globe is centred on, else a genera... |
| 4529 | 6 | `private boolean standsIn()` | Whether this is a generated world whose feed names the place the city stands in, so its partners can be drawn as arrows. |
| 4537 | 9 | `private TradeLanes lanesNow(NationsMap nm)` | The lanes of the feed's figures now: the ones kept while the month, the city, the map and every figure are the same, else made again (a fraction of a millisecond; never in a frame that has them). |
| 4548 | 8 | `private TradeLanes.Arrows arrowsNow()` | A generated world's arrows of the feed's figures now, kept the same way; null where the map is not built or the feed names no stand-in. |

### the chips (lines 4557-4634)

| line | len | member | says |
|---:|---:|---|---|
| 4560 | 16 | `private Label layerChip(String words, String tip, Runnable flip)` | A toggle chip of the head: a dot before its word, lit or not (styleChip()); a click runs `flip`. |
| 4578 | 5 | `private static void styleChip(Label chip, boolean lit)` | The board's .chip and .chip.lit: lit, the pinned ground, the accent's edge and dot and the full text; not, the panel, the edge, the label's text and the grey dot. |
| 4589 | 17 | `private void syncChips()` | The chips of the head, lit as the settings stand (each frame of the head). |
| 4607 | 4 | `private void flipResources()` |  |
| 4612 | 4 | `private void flipNations()` |  |
| 4617 | 4 | `private void flipBorders()` |  |
| 4622 | 4 | `private void flipTrade()` |  |
| 4628 | 6 | `private void layersFlipped()` | A chip flipped: saved at once (the settings are the player's, beside the saves), the card put away if its nations are off, the chips restyled and the view drawn again - which lays the legend out again (legendFor() key... |

### the legend (lines 4635-4662)

| line | len | member | says |
|---:|---:|---|---|
| 4642 | 20 | `private List<String[]> nationsEntries()` | The legend's nations entries - {colour, words} - on an Earth world with the map built: the border's white while Borders is on, the dot's colour while Nations is on and the view is a globe's, and (WN3) the violet and t... |

### drawing (lines 4663-4902)

| line | len | member | says |
|---:|---:|---|---|
| 4666 | 19 | `private static void strokeRuns(GraphicsContext gc, NationsMap.Runs r)` | Runs of border stroked as ONE path - the runs are many and short, about 6,000 on the planet - 1 px, dashed NationsMap.BORDER_DASH_PX, white at BORDER_ALPHA; the width and dashes put back. |
| 4692 | 31 | `private void tradeLanes(GraphicsContext gc, NationsMap nm)` | The city's trade lanes on the globe, between the borders and the dots (WN3, board 1): TradeLanes.runs() of the view - the edges in view, each shared lane once - stroked a path to each colour and whole width (at most T... |
| 4729 | 35 | `private void tradeArrows(GraphicsContext gc)` | A generated world's partners as arrows at the edge of its map (WN3, the study 6.4): the biggest TradeLanes.ARROWS_MAX over the floor, each at the edge on its bearing from the stand-in place the feed names - a violet a... |
| 4769 | 18 | `private static void arrow(GraphicsContext gc, double tipX, double tipY, double ux, double uy, double width, double off, boolean...` | One arrow of the edge: a shaft ARROW_LEN_PX long inward from its tip, a head at the outer end if it points out (what we sell) and at the inner end if it points in (what we buy), the whole moved `off` pixels sideways (... |
| 4789 | 8 | `private void globeNations(GraphicsContext gc)` | The globe's nations (after its picture, under the city's own drawing): the borders with Borders on, the trade's lanes with Trade routes on, the dots and labels with Nations on - each only when the map is built, and th... |
| 4802 | 19 | `private List<NationsMap.Mark> nationMarks(NationsMap nm, Nations ns)` | The dots and labels of the globe's view now (NationsMap.marks()), made again only when the view is not the one they were made for (GlobeFrame.sameView()), the year moved, the figures or the map are another's, or a fon... |
| 4823 | 35 | `private void drawMarks(GraphicsContext gc, List<NationsMap.Mark> list)` | The dots (NationsMap.DOT_ARGB at DOT_ALPHA, edged), then the labels of those that fit: the name as the board's .lbl and the figures as its .lbl2, centred, each on its halo. |
| 4865 | 15 | `private void planeBorders(GraphicsContext gc, MapFrame f)` | The borders on the city's plane (the plane's tier on Earth, and the land office's small map), after the tiles and what runs over them and before the world lines and the overlays: only the lines that cross the box of p... |
| 4882 | 20 | `private static double[] crossingBox(CityLand land, LandMarket market)` | The box of plots {x0, y0, x1, y1} of the city's land and its listed offers, as the reach is found from them (GlobeFrame.reachKm()); null with neither. |

### the pointer (lines 4903-4943)

| line | len | member | says |
|---:|---:|---|---|
| 4910 | 8 | `private List<String> nationHoverWords(double sx, double sy)` | What hovering a dot says (NationsMap.hoverWords(), its last line the blue CLICK_FOR_CARD), or null where the pointer is over no dot, Nations is off, or the city's plane is drawn under it: there the land office's hover... |
| 4920 | 6 | `private String ownerWords(double sx, double sy)` | Whose land is under the pointer, where the city's plane is not drawn: a nation's or an area's name (NationsMap.ownerName() of the exact ownerAt()); null elsewhere or with Nations off. |
| 4933 | 10 | `private boolean nationClick(double sx, double sy)` | A click on the expanded globe with Nations on, where the city's plane is not drawn under the pointer (plotAt() is null): on a dot or on a nation's land (NationsMap.nationAt(): the dot, else the exact owner) opens that... |

### the card (lines 4944-5087)

| line | len | member | says |
|---:|---:|---|---|
| 4947 | 7 | `private void showNationCard(int nation, double sx, double sy)` | A nation's card opened beside the point clicked: the buying card (and its picks) goes, as there is only ever one. |
| 4956 | 1 | `private String nationCardKey()` | What the card was drawn for: the nation and the month, which moves the year and the D$ it is struck in. |
| 4959 | 95 | `private void fillNationCard()` | The card made (again) for the nation shown, from NationsMap.card(): its head - the name, the region as a tag, a "×" - the distance under it, and its rows in a grid of three columns. |
| 5056 | 12 | `private void placeNationCard()` | The card beside the point clicked, as the buying card stands beside its offer: CARD_GAP off it, at its right else its left, level with it and CARD_MARGIN at least inside the view. |
| 5070 | 7 | `private void hideNationCard()` | The card put away. |
| 5079 | 8 | `private void syncNationCard()` | A frame's look at the card (tick()): made again when the month moved - the year's figures and the D$ it is struck in - and put away if its nation is not shown any more (Nations off, another game). |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 5088-5479)

| line | len | member | says |
|---:|---:|---|---|
| 5092 | 1 | `void expand()` |  |
| 5095 | 26 | `private void expand(boolean keepView)` | ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. |
| 5128 | 11 | `void expandOn(MiniMapFrame.Box box)` | Expanded on a box of plots (0.7.134, batch MM2): the minimap's click, the map over the window fitted on what the stamp frames (MiniMapFrame.open()) rather than on the land office's opening; Fit goes back to that. |
| 5141 | 24 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked - the card's picks dropped (0.7.137), and its purchase forgotten unless the funding page has it (B5). |
| 5166 | 81 | `private void buildPane()` |  |
| 5250 | 22 | `private void fitPane()` | The canvas sized to the pane: the window's width, and its height under the header (MS1: the pane lies below it, ui.chartFullTop()), inside the padding and under the head (City History's rule, read off the window). |
| 5274 | 10 | `private void openBig()` | The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. |
| 5286 | 10 | `private void fitBig()` | Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. |
| 5298 | 1 | `static List<String[]> legendEntries(int tier)` | The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. |
| 5301 | 5 | `static List<String[]> legendEntries(int tier, boolean relief)` | ...on an Earth world with earth-2's layers (`relief`, LandMap.reliefDrawn(); ET1 slice D) also the three greys, forest on a slope and, on the plane, the shallow sea. |
| 5308 | 1 | `static List<String[]> legendEntries()` | ...the plane's, at every scale on a generated world. |
| 5320 | 3 | `static String trucksWords(int loads)` | ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. |
| 5328 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed at its edge. |
| 5333 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 5338 | 3 | `static String trackWords(long plots)` | ...and (FR7 slice 3) when the model has track the railway's ways do not use - its main line, its sidings and its branches - how much, in plots; the model's words (LandMap.trackSurplusWords()). |
| 5353 | 38 | `private VBox legend()` |  |
| 5401 | 9 | `private void foldLegend()` | The legend folded or open, as the player left it (GamePrefs.isMapLegendFolded()): the body shown or not, the chevron pointing down or right, and the head's ground rounded all round when it stands alone. |
| 5412 | 31 | `private void legendFor(int tier)` | The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. |
| 5445 | 1 | `private int shownTier()` | The tier the legend and the hover are for (MS2): the globe's on Earth - the plane at every scale closer than the handoff, past the reach too, where the plane's own ground is what is drawn - and the plane at every scal... |
| 5448 | 5 | `private void legendTrucks()` | The trucks' line: how many loads a truck stands for while any is drawn this frame, on the plane; hidden otherwise. |
| 5455 | 14 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. |
| 5471 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

### REPLAY: CINEMATIC (TR1, 2026-10-31) (lines 5480-5554)

| line | len | member | says |
|---:|---:|---|---|
| 5508 | 3 | `private double padded(double px)` | A pane measure as it is now: the padding's and the head's room, none in cinematic mode. |
| 5517 | 21 | `void setCinematic(boolean on)` | Cinematic mode on or off for the expanded map: the head, the legend, the hover card and the padding gone and the cursor none, or all of it back as it was. |
| 5544 | 5 | `double[] replayView()` | The expanded map's view for the input log (InputRecorder): {1 on Earth else 0, a, b, scale, roll} - the globe's centre latitude and longitude, its km a pixel and its roll, or the plane's centre x and y, its scale and ... |
| 5551 | 3 | `void replayChanged()` | The city's map is another object (a replay's quick step swapped it): the view draws again, and the frame's keepUp() takes the new map, so a small map in the office and the expanded one both follow. |

### REPLAY: THE CAMERA, THE PAINT BUDGET AND THE QUICK STEP (TR1, 2026-10-31) (lines 5555-5728)

| line | len | member | says |
|---:|---:|---|---|
| 5590 | 3 | `private boolean takingSameLand(CityMap next)` | Whether the city's new map is a replay's quick step on the land on show: a replay is on and the new map is of the same world as the one shown. |
| 5601 | 12 | `private void staleAll()` | Every kept picture made stale against the new map, none thrown out: a tile picture's version and key forgotten, so the next paint() reads its inputs again and repaints it only if their stamp (a hash of the content, th... |
| 5615 | 5 | `CameraPath.View cameraView()` | The expanded map's view as a camera view (month 0: the camera puts the month on), or null when it is not expanded: on Earth the globe's centre and km a pixel, elsewhere the plane's centre and pixels a plot. |
| 5622 | 5 | `CameraPath.View cameraWhole()` | The widest view as a camera view, what Fit shows: on Earth the whole planet facing the city, on a generated world the whole world from its middle; null when the map is not expanded. |
| 5629 | 12 | `private void applyCameraView(CameraPath.View v)` | A view taken whole (a glide's, a fly's, or one of a move's load): the globe set to it and the plane following, or the plane set. |
| 5643 | 4 | `void replayShowView(CameraPath.View v)` | A view put on the expanded map by hand (TR2: Follow's logged view), as the camera's is; nothing when the map is not over the window or the view is of the other kind of ground. |
| 5649 | 13 | `private void applyMove(CameraPath.Move m)` | The smooth hand's step: the zoom about its anchor as the wheel's notch is applied, then the pan as a drag's. |
| 5668 | 49 | `private boolean replayCamera()` | One frame of the replay's camera (tick()): W A S D's velocity to the smooth hand, then the camera's Shot applied. |
| 5719 | 9 | `private boolean replayWheel(double sx, double sy, double notches)` | The wheel's notches in a replay: eased in by the smooth hand about the pointer, and a glide in flight let go. |

### the move loaded before it runs (CM1) (lines 5729-6133)

| line | len | member | says |
|---:|---:|---|---|
| 5753 | 1 | **type** `private record TileCopy(long stamp, WritableImage image)` | A tile's picture as a month showed it: the stamp it was painted at and its own copy of the image (the cache paints a picture in place when its stamp moves). |
| 5756 | 1 | **type** `private record NodeCopy(long stamp, boolean partial, WritableImage image, int[] pixels)` | A far node as a month showed it: the stamp and whether it was partial, and its own copies of the image and the colours. |
| 5759 | 26 | **type** `private static final class MoveLoad` | What a loaded move holds: where the walk is, whether the run has begun, the views given up, and (for a move across months) the pictures each month showed. |
| 5779 | 5 | `MoveLoad(int serial, int of, boolean months)` _(in MapView.MoveLoad)_ |  |
| 5790 | 1 | `private boolean moveRunning()` | Whether a loaded move is running across months: the pictures come from its store. |
| 5792 | 4 | `private void beginMove(ReplayCamera cam)` |  |
| 5798 | 6 | `private void endMove()` | The move ran out, was let go, cancelled, failed or replaced: the caches are let go of for room again, down to their sizes. |
| 5806 | 6 | `private void backToStart(ReplayCamera cam)` | A load that did not reach its click, ended: the view and the month back to where the move begins. |
| 5813 | 6 | `private void pin(boolean on)` |  |
| 5826 | 70 | `private boolean loadStep(ReplayCamera cam)` | One frame of the load, within REPLAY_FRAME_MS: the plan's views from the next one on, each taken (and its month shown), painted with the real paint() and left only when loadSettled() says it lacks nothing. |
| 5898 | 7 | `private static boolean heapOver()` | Whether the heap is over LOAD_HEAP_SHARE of what the game may have, after a collection if it looked so. |
| 5907 | 1 | `boolean viewSettled()` | The view as it stands lacks nothing, outside a move: loadSettled() with nothing passed (ReplayMoveProbe's and ReplayCheck's wait for a still view). |
| 5913 | 1 | `boolean moveHeld()` | Whether a loaded move is held (CM1: ReplayMoveProbe.check(), which ReplayCheck 5d runs). |
| 5916 | 5 | `int cachesPinned()` | How many of the six picture caches are pinned (CM1: as moveHeld(); 6 while a move is loaded, 0 otherwise). |
| 5923 | 1 | `long framesDrawn()` | The frames tick() has drawn since this view was made (CM1: ReplayCheck 5d's "the normal game draws its map"). |
| 5934 | 7 | `private boolean loadSettled(MoveLoad m)` | Whether the view as it stands lacks nothing: no far node, district picture or chart node is asked of WORKER and not back (but those a passed view left stuck), and everything the draw would read is in and current - on ... |
| 5943 | 35 | `private boolean scanView(MoveLoad m, boolean record, int month)` | The cells of the view being loaded, as loadSettled() reads them; with {@code record} (the view settled, a move across months) each is copied into the month's store. |
| 5980 | 18 | `private boolean nodesHold(MoveLoad m, long plots, long nx0, long nx1, long ny0, long ny1, MapFrame asker, boolean record, int m...` | The far nodes nx0 to nx1 and ny0 to ny1 at `plots` a pixel all coloured and current (and, with `record`, copied into the month's store); `asker` is the frame the pictures they lack are asked for by (paintNodes()). |
| 5999 | 1 | `private int cacheIndex(Pictures p)` |  |
| 6002 | 19 | `private void storeTile(MoveLoad m, int month, int cache, long key, Img img)` | A tile's picture kept as the month showed it: one copy for each stamp the tile was painted at, whatever month showed it. |
| 6023 | 19 | `private void storeNode(MoveLoad m, int month, long key, Img img)` | A far node kept as the month showed it: one copy for each stamp (and partial or not) the node was coloured at. |
| 6044 | 12 | `private boolean takeTile(Img img, int cacheIndex, long key)` | A stale tile picture given the month's copy from the store, current against the map; false where the store has none for it (the month on screen is the camera's). |
| 6058 | 18 | `private boolean takeNode(Img img, long key, long picturesKept)` | ...and a stale far node given the month's colours; on a miss its image and colours are let go (they may be the store's, not to be painted over) and the node is coloured the normal way. |
| 6081 | 12 | `private boolean drawLoading()` | The map while a move loads: nothing but the words (ReplayCamera.overlay()) on the plain ground - the walk jumps through the move's views and what it paints is not for the eye. |
| 6095 | 6 | `private void drawReady()` | The map as usual, and over it the words of a loaded move waiting for its click ("Ready - click to run"). |
| 6103 | 19 | `private void overWords(GraphicsContext gc, String words)` | Words in a box in the middle of the canvas. |
| 6124 | 9 | `private boolean replayRunsOnClick()` | A press on the map while a loaded move waits for it runs the move; one while it loads does nothing (the map under the words is not the eye's; Escape cancels); nothing outside a replay. |

### CM1: what a camera move shows unpainted (MoveTally) (lines 6134-6330)

| line | len | member | says |
|---:|---:|---|---|
| 6151 | 71 | **type** `static final class MoveTally` | What each frame of a replay's glide or fly showed that was not painted, counted (CM1, 2026-11-02): per frame the cells (a plane tile or far node, a globe plane node or chart node, a sampled screen point on the globe) ... |
| 6170 | 6 | `void reset()` _(in MapView.MoveTally)_ | Back to nothing counted: a move is about to begin. |
| 6178 | 24 | `void frame(long now, String where, long blackCells, long coarseCells, long staleCells, boolean half, long asked, long letGo)` _(in MapView.MoveTally)_ | One frame taken at {@code now} (System.nanoTime()): what it drew from, what it showed unpainted, and what it cost the worker and the caches. |
| 6204 | 17 | `String report()` _(in MapView.MoveTally)_ | The tally in a few lines: the totals, a line for each place the frames drew, and the frame rate. |
| 6226 | 1 | `MoveTally moveTally()` | The camera move tally (CM1), for ReplayMoveProbe: reset it before a move, read it after. |
| 6233 | 3 | `private long evictedNow()` | Every cache's evictions summed (Pictures.evicted). |
| 6245 | 85 | `private void tallyMove()` | The frame tick() has just painted and drawn, counted into the move tally (CM1): its cells looked up as the draw looked them up (drawTiles(), drawNodes(), drawGlobePart()), in the draw's order, so the caches' access or... |

