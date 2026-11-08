# MapView.java - 6,048 lines · 257 methods · 74 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

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
> ... (136 more lines in the source)

**Uses:** [Palette](Palette.md) (162), [MapFrame](MapFrame.md) (110), [LandMap](LandMap.md) (87), [NationsMap](NationsMap.md) (54), [GlobeFrame](GlobeFrame.md) (43), [World](World.md) (34), [Game](Game.md) (33), [LandParcel](LandParcel.md) (26), [CityLand](CityLand.md) (25), [CityMap](CityMap.md) (23), [MapTiles](MapTiles.md) (21), [TradeLanes](TradeLanes.md) (19), [GlobePicture](GlobePicture.md) (17), [LandMarket](LandMarket.md) (16), [ShipShapes](ShipShapes.md) (14), [CameraPath](CameraPath.md) (14), [TrainSchedule](TrainSchedule.md) (13), [Nations](Nations.md) (13), [ReplayCamera](ReplayCamera.md) (11), [CallPartners](CallPartners.md) (10), [TruckSchedule](TruckSchedule.md) (10), [TrainShapes](TrainShapes.md) (9), [BoatSchedule](BoatSchedule.md) (7), [TilePainter](TilePainter.md) (6), [TileRaster](TileRaster.md) (5), [Deposit](Deposit.md) (4), [HistoryScreen](HistoryScreen.md) (4), [TruckShapes](TruckShapes.md) (4), [LandGrid](LandGrid.md) (4), [EarthProj](EarthProj.md) (4)... and 18 more

**Used by (4):** [LandScreen](LandScreen.md), [MiniMap](MiniMap.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 323 | · what it draws from |
| 394 | · what the player is doing |
| 407 | · buying from the map (0.7.137, batch M6) |
| 435 | · the boats (0.7.97) |
| 471 | · the globe (0.7.135, batch E9) |
| 652 | A FRAME OF THE CLOCK (tick()) |
| 1078 | THE DRAWING |
| 1132 | THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) |
| 1217 | THE BOATS (0.7.97, batch O13) |
| 1322 | THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 1392 | · a boat or a train names its partner (WN7) |
| 1472 | · the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) |
| 1769 | · the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") |
| 1796 | · the ground no port links (ET5, slice D; the ET5 design, section 5) |
| 2181 | THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) |
| 3056 | · the hover's quote, away from the screen's thread (WN5b, 2026-11-02) |
| 3143 | · the keys (0.7.135; spec-globe 3.11) |
| 3185 | THE POINTER AND THE KEYS |
| 3533 | BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 3977 | THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) |
| 4213 | · the city's trade (WN3) |
| 4290 | · the chips |
| 4360 | · the legend |
| 4388 | · drawing |
| 4628 | · the pointer |
| 4669 | · the card |
| 4813 | EXPAND: THE MAP OVER THE WHOLE WINDOW |
| 5204 | REPLAY: CINEMATIC (TR1, 2026-10-31) |
| 5279 | REPLAY: THE CAMERA, THE PAINT BUDGET AND THE QUICK STEP (TR1, 2026-10-31) |
| 5447 | · the move loaded before it runs (CM1) |
| 5852 | · CM1: what a camera move shows unpainted (MoveTally) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 246 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 249 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 252 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 255 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 258 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 261 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 264 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 270 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 273 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 276 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 279 | `MapView.HIT_PX` | `4` | How near the pointer must be to a boat's hull or a train's vehicle, in pixels, for its label to show (WN7): 4. |
| 282 | `MapView.HIT_ALPHA` | `0.3` | A boat or a vehicle fainter than this, out in the abyss or past the world line's far end, is not named (WN7): 0.3. |
| 285 | `MapView.LABEL_OFF_PX` | `10` | A boat's or a train's label stands this many pixels off it, up and to the right, or the other way where the view's edge is in the road (WN7): 10. |
| 291 | `MapView.WORKER` | `new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQue...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), ... |
| 299 | `MapView.QUOTES` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "land-quote...` | The thread a hover's block is quoted on (WN5b): its own, not WORKER's, because one quote can take seconds at a big level near a foreign coast and would hold the map's own jobs (plans, nodes, the minimap) behind it. |
| 307 | `MapView.QUOTES_KEPT` | `16` | How many blocks' quotes the hover keeps, the latest asked: 16 - a pointer that comes back to a block shows its price at once. |
| 500 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 553 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 556 | `MapView.EXPAND_TIP` | `"The map under the window's header: drag along the city's edge to pick the ex...` | The Expand button's tooltip (since MS1 it says the drag picks; since BL1 the ground it picks is the player's own: a drag along the city's edge picks the exact ground, one from elsewhere pans). |
| 560 | `MapView.HINT` | `"drag along the city's edge to pick the exact ground · drag elsewhere to pan"` | The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), since MS1 by a click or a drag, since BL1 of the exact ground - blocks of the city's grid, the offers i... |
| 563 | `MapView.KEYS_TIP` | `"Click an offer to open its card beside it, or a free block along the city's ...` | ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. |
| 573 | `MapView.SELECT_EVERY` | `"Select every offer"` | The head's chip that picks every offer listed (B7). |
| 1644 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1647 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 1650 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1653 | `MapView.WORLD_LINE_ALPHA` | `LandMap.WORLD_ALPHA` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. |
| 1961 | `MapView.DASH_ON` | `5, DASH_PERIOD = 9` | A run's dashes: 5 px on and 4 off, as the offers' edge has been drawn since 0.7.61. |
| 2022 | `MapView.PICKED_ALPHA` | `0.22` | The tint of a picked offer's ground and of picked blocks: the building pink at 0.22 (the picked offer's since 0.7.137). |
| 2079 | `MapView.NEW_TAG` | `"NEW"` | The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. |
| 2147 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 2186 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe, by code - a class and its relief (ET1 slice D): the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 2189 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 2192 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 2195 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 2335 | `MapView.CHART_NODE_OFFSET` | `1L<<19` | A chart node's indexes run negative west and north of the chart's anchor: this many are added to each to key it. |
| 2694 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 2882 | `MapView.BLOCK_LINE_SAG_PX` | `0.25` | The most the camera may bend a block line from its chord before the line is split in two, in pixels: a quarter (the line is drawn a pixel wide, so a quarter is not seen). |
| 2885 | `MapView.BLOCK_LINE_SPLITS` | `6` | ...and how many times a line is split at most: 6 (64 pieces - far more than the 64 px a line bends by at the widest near scale 12,000 km from the city needs). |
| 2996 | `MapView.CLICK_TO_DROP` | `"Shift-click to drop it · Enter buys"` | The hover card's last line over a block that is picked already (BL1), in blue as the offers' is: how to drop it. |
| 3492 | `MapView.CLICK_TO_BUY` | `"Click to buy it here"` | The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. |
| 3525 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 3569 | `MapView.CARD_GAP` | `14` | The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). |
| 3572 | `MapView.CARD_MARGIN` | `10` | ...and at least this far inside the view: 10, the hover card's (showCard()). |
| 3575 | `MapView.BOX_FILL_ALPHA` | `0.12` | The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. |
| 3998 | `MapView.NATIONS_TIP` | `"A dot at each nation's largest port or city, by its output; its name, output...` | The Nations chip's tooltip (WN2). |
| 4002 | `MapView.BORDERS_TIP` | `"The nations' borders, dashed white, at the far views; up close only where on...` | ...and the Borders chip's. |
| 4005 | `MapView.TRADE_TIP` | `"This month's trade as routes from the city: violet what we sell, sand what w...` | ...and the Trade routes chip's (WN3). |
| 4009 | `MapView.TRADE_SELL_WORDS` | `"What we sell, a month", TRADE_BUY_WORDS = "What we buy"` | The legend's words for the two kinds of route (the board's, less its "it"). |
| 4012 | `MapView.TRADE_ALPHA` | `0.78` | A route's colours: the business violet for what we sell and the ore's sand for what we buy (Palette's), at the board's stroke opacity, .78. |
| 4013 | `MapView.TRADE_SELL` | `Color.web(Palette.BUSINESS, TRADE_ALPHA), TRADE_BUY = Color.web(Palette.ORE, ...` |  |
| 4016 | `MapView.ARROW_LEN_PX` | `34` | A generated world's arrow is this many pixels long from its tip inward... |
| 4019 | `MapView.ARROW_HEAD_PX` | `9, ARROW_HEAD_WIDE_PX = 3` | ...with a head this many pixels long, and this much wider than the shaft either side. |
| 4022 | `MapView.NO_FIGURES` | `new double [ 0 ]` | A stand-in for a feed that gave no figures. |
| 4025 | `MapView.LABEL_NAME_PX` | `11` | A dot's label, the name: sans semibold, 11 px (the board's .lbl)... |
| 4028 | `MapView.LABEL_FIGURES_PX` | `9.5` | ...and the figures under it, mono, 9.5 px (the board's .lbl2). |
| 4031 | `MapView.DOT_EDGE_PX` | `1.2` | A dot's edge, in pixels: 1.2, in NationsMap.DOT_EDGE_ARGB (the board's). |
| 4034 | `MapView.HALO_PX` | `3, HALO_ALPHA = 0.75` | A label's halo, drawn under its words: 3 px wide in NationsMap.DOT_EDGE_ARGB at 0.75 (the board's .lbl stroke). |
| 4037 | `MapView.WIDTHS_MOST` | `600` | The widths of label words kept, by string (the names never change and the figures change once a year): the 600 most recently used. |
| 4040 | `MapView.NATION_CARD_W` | `330` | A nation's card is this wide, in pixels: 330 (the board's .card)... |
| 4043 | `MapView.NATION_CARD_GAP` | `4` | ...and its head, its distance line and its rows stand this many pixels apart: 4. |
| 5035 | `MapView.LEGEND_ROWS` | `17` | Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 the transport class, t... |
| 5038 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 5041 | `MapView.TRUCK_WORDS` | `"Trucks: one per %s"` | The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). |
| 5049 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 5067 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |
| 5122 | `MapView.LEGEND_FOLD_WORDS` | `"· click to fold"` | The head line's words when the legend is open, after its chevron and "Legend" (the board's .lh). |
| 5298 | `MapView.REPLAY_FRAME_MS` | `24` | The most a frame spends painting tiles in a replay, in ms: 24 - a replay computes no month, so a frame's whole share is the picture's, and a view reached by the camera comes in over two or three frames, not eight. |
| 5301 | `MapView.CAMERA_PAUSE_S` | `0.25` | A gap between two camera frames longer than this, in seconds, is not time the camera moved in: 0.25 - the pump stops when the view is idle and a glide or a fly must not find the minutes gone by in its first frame. |
| 5462 | `MapView.STUCK_S` | `2` | How long the worker may sit idle while a view of the load still lacks something before the load passes it, in seconds: 2 - a far node or chart the worker cannot ground never comes back, and the load is not held by it;... |
| 5465 | `MapView.LOAD_HEAP_SHARE` | `0.8` | The share of the most the heap may hold at which a load stops, saying so: 0.8 - a move too long or too wide to hold in memory is refused, not run half loaded. |
| 5468 | `MapView.LOAD_WORDS_PX` | `28` | The height of the words written over the map while a move loads or waits for its click, in pixels: 28. |
| 5628 | `MapView.NO_MOVE` | `new MoveLoad(0, 0, false)` | A move with nothing passed and nothing kept, for viewSettled(). |
| 5883 | `MapView.MoveTally.WHERES` | `{ "L0", "L1", "L2", "FAR", "GROUND", "PICTURE" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 267 | `static Game.MapDraft drafting` | The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. |
| 309 | `private final UserInterface ui` |  |
| 310 | `private final Picker picker` |  |
| 311 | `private final Runnable onClose` |  |
| 312 | `private final Canvas canvas` |  |
| 313 | `private final StackPane smallBox` |  |
| 314 | `private final Label readout` |  |
| 315 | `private final MapFrame small` |  |
| 316 | `private MapFrame big` |  |
| 317 | `private boolean expanded` |  |
| 319 | `private VBox bigPane` |  |
| 320 | `private StackPane bigBox` |  |
| 321 | `private VBox card` |  |
| 325 | `private CityMap shownMap` |  |
| 326 | `private volatile MapTiles tiles` | (TR1) volatile: WORKER's queue skips the plans of a map a replay has stepped past (ask()) |
| 327 | `private Game.MapDraft draft` | (TR1) volatile: WORKER's queue skips the plans of a map a replay has stepped past (ask()) |
| 328 | `private int draftsFailed` |  |
| 329 | `private long version` |  |
| 330 | `private long fittedFor` |  |
| 332 | `private long farRecoloured` | How many far nodes have been coloured (batch UP3): a node whose version moved but whose key did not (MapTiles.nodeKey()) is not counted. |
| 333 | `private final TilePainter.Input in` |  |
| 334 | `private int[] buffer` |  |
| 338 | `WritableImage image` |  |
| 339 | `long stamp, version` |  |
| 341 | `long key` | A tile's key (MapTiles.tileKey(), batch UP5) at the moment `stamp` was taken, and whether it was: while the key holds the stamp does, and the tile's inputs are not read again. |
| 342 | `boolean keyed` |  |
| 343 | `byte[] ground` |  |
| 345 | `byte[] relief` | The node's relief beside its ground (ET1 slice D): null on a generated world, whose ground is all flat. |
| 346 | `boolean partial` |  |
| 347 | `long pictures` |  |
| 349 | `int[] pixels` | A far node's colours as drawn (0.7.135): what the globe reprojects near the city. |
| 354 | `long most` |  |
| 356 | `long evicted` | How many pictures this level has let go for want of room, since it was made (CM1: the camera tally's evictions). |
| 358 | `boolean pinned` | Whether a replay's loaded move holds this level whole (CM1): nothing is let go for room until it is taken off (MapView.pin()). |
| 377 | `private final Pictures l0small` |  |
| 379 | `private final Set<Long> nodesAsked` |  |
| 385 | `private final Pictures charts` | The globe's chart nodes (MS3; GlobeFrame.chart()) kept as the plane's far nodes are - their colours (Img.pixels), classes and relief (for the hover) - and those asked of WORKER and not back. |
| 386 | `private final Set<Long> chartsAsked` |  |
| 388 | `private final Set<CityMap.District> picturesAsked` | The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. |
| 391 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 392 | `private long fieldsFor` |  |
| 396 | `private int side` | the side the office shows, and the offer picked |
| 397 | `private int litSide` | the side the office shows, and the offer picked |
| 398 | `private int overSide` | a row hovered in the office |
| 399 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 400 | `private boolean pressed` |  |
| 402 | `private javafx.scene.input.MouseButton pressButton` | The button the press was made with (MS1): only the primary clicks or draws a box; the others pan. |
| 403 | `private double[] hoverAt` |  |
| 405 | `private boolean hoverShift` | Whether Shift was held when the pointer last moved over the map: the cursor says a press would box from anywhere (MS1). |
| 410 | `private final LandMap.Picks picks` | The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a load and a collapse - and, since MS1, not on a month: each is keyed to its ground and lost only when the city ... |
| 412 | `private VBox buyBox` | The card in the pane (the land office's: LandScreen.mapCardNode(), mapReceiptNode()) and what it was drawn for - null for a receipt. |
| 413 | `private String cardFor` |  |
| 415 | `private List<double[]> rings` | The sites of the fields the card's offers hold, each its square of plots and its resource's colour, {x0, y0, x1, y1, argb}: ringed wherever they lie (board 7). |
| 417 | `private double[] cardAt` | The box of plots the card stands beside: the offer clicked, what a box took, the shelf's first for Ctrl+A, the ground just bought. |
| 419 | `private Bought bought` | A purchase from the card and its receipt: what is being bought - ids, places, the ground - shown by the ground once it is the city's. |
| 420 | `private boolean receiptShown` |  |
| 422 | `private boolean backFromFunding` | The card sent to the funding page (B5): the map comes back over the window at the view it left when the page is done. |
| 424 | `private boolean boxing` | A drag that boxes (B7; since MS1 a plain drag from an offer's free ground, or Shift and a drag from anywhere - LandMap.pressBoxes()): a box drawn from the press to (boxX, boxY), in the screen's pixels. |
| 425 | `private double boxX, boxY` |  |
| 427 | `private Label headMoney` | The head's money and its pay toggle (land's own, in the money the toggle pays in: the window's header, kept in view over this pane since MS1, says neither), what they were drawn for, and the note of what the picks los... |
| 428 | `private HBox headPay` |  |
| 429 | `private String headFor` |  |
| 430 | `private Label headNote` |  |
| 431 | `private javafx.scene.control.Tooltip headNoteTip` |  |
| 433 | `private boolean running, dirty` |  |
| 438 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 440 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 441 | `private int scheduleMonth` |  |
| 442 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 444 | `private TrainSchedule trainSchedule` | The trains' schedule drawn from (0.8.2, batch FR1): its boat-month, the routes and the game month it was made on. |
| 445 | `private int trainMonth` |  |
| 446 | `private List<TrainSchedule.Route> trainRoutes` |  |
| 448 | `private CallPartners callPartners` | The partners the month's boats and trains go to (WN7; partnersNow()), and those each schedule was made with: a schedule is made again when they change. |
| 450 | `private double[] pointerAt` | The pointer over the canvas in pixels, or null when it is off the map or pressed: the boat or train it is on is named (partnerLabel()). |
| 456 | `private Object labelCall` | The call (a BoatSchedule.Call or a TrainSchedule.Call) of the boat or vehicle nearest the pointer in the frame being drawn, within HIT_PX, how near (labelScore: its distance from the pointer in plots to its hull or bo... |
| 457 | `private double labelScore, labelX, labelY, hitX, hitY, hitTol` |  |
| 458 | `private final Map<String, Double> callWidths` |  |
| 460 | `private boolean boatsLive` | Whether boats - or since 0.8.2 trains, and since 0.8.8 trucks - are in view (the frame goes on), and the clock they were last drawn at. |
| 461 | `private double drawnClock` |  |
| 462 | `private final AnimationTimer pump` |  |
| 466 | `private final ImagePattern hatch` |  |
| 469 | `private final ImagePattern islandHatch` | The amber hatch over the city's ground that no port links (ET5): the offers' hatch drawn in the warning colour. |
| 474 | `private GlobeFrame globe` | The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. |
| 475 | `private World.Key globeKey` |  |
| 476 | `private EarthData globeData` |  |
| 478 | `private long reachFor` | The land and offers its reach was found for. |
| 481 | `private WritableImage globeImg` | The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. |
| 482 | `private int[] globePx` |  |
| 483 | `private GlobeFrame globeDrawn` |  |
| 484 | `private int globeMode` |  |
| 485 | `private long globeInputs` |  |
| 487 | `private long inputs` | Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. |
| 489 | `private double[] patch` | The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. |
| 492 | `private byte[] pieceCls` | The GROUND tier's pieces past the plane, for one view at rest: its codes - class and relief (GlobePicture.code()) - a pixel (-1 not in yet), the view and its number. |
| 493 | `private GlobeFrame pieceView` |  |
| 494 | `private volatile int pieceGen` |  |
| 496 | `private int[] carryPx` | The last whole picture at rest, carried pixel by pixel while the next is drawn. |
| 497 | `private GlobeFrame carryView` |  |
| 503 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when the keys last moved the view (0 when none is held). |
| 504 | `private long keyNanos` |  |
| 507 | `private int legendTier` | The tier the legend was laid out for, and whether with the relief's entries (LandMap.reliefDrawn()). |
| 508 | `private boolean legendRelief` |  |
| 1034 | `private volatile long[] nodesWanted` | The far nodes the view being painted wants - {plots a pixel, nx0, nx1, ny0, ny1} - which WORKER's queue reads from its own thread (MS2). |
| 1137 | `private javafx.scene.text.Font worldLabelFont` | The world line's label as measured: its font, its width (and the shared label's) and its height above the baseline, kept until the font changes (FX2, FX5). |
| 1138 | `private double worldLabelW, worldLabelBothW, worldLabelH` |  |
| 1478 | `private final Map<Long, DistrictTrucks> districtTrucks` | The districts' schedules by CityMap.DistrictTrips.key(): thrown out with the shares when the month, the game month or the trips turn. |
| 1481 | `private int truckMonth` | The boat-month and the game month the schedules were made on, and the trips (CityMap.tripsDrawn()) they were made against. |
| 1482 | `private List<CityMap.DistrictTrips> truckTrips` |  |
| 1485 | `private double[][] truckPerBuilding` | What one building of each type carries in the month billed and the city's truck tonnes by load (TruckSchedule.perBuilding(), Freight.truckedIn() and the others): what a district's share is made of, kept with the sched... |
| 1486 | `private double[] truckCityTonnes` |  |
| 1488 | `private Map<Long, double[]> truckShares` | ...and each district with trips' catchment's shares of them by CityMap.DistrictTrips.key() (TruckSchedule.sharesByTrips()): its own works' tonnes and those of the districts about it that have no trips. |
| 1491 | `private int trucksStand` | Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the largest of the districts' drawn, which is the city's N. |
| 1760 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 1761 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 1762 | `private long shapesFor` |  |
| 1765 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 1766 | `private long labelsFor` |  |
| 1767 | `private double labelsAtScale` |  |
| 1772 | `private List<CityMap.Tinted> tints` | The zones' districts as last worked out (CityMap.zoneTint()), and what from: the map, its changes and the zones. |
| 1773 | `private CityMap tintsMap` |  |
| 1774 | `private long tintsAt` |  |
| 1775 | `private String tintsKey` |  |
| 1799 | `private List<long[]> islandRuns` | The runs of owned dry ground on landmasses no port links, as LandMap.unlinkedRuns() gave them, the chips beside them, and what they were worked out for: the linked set's version and the land's stamp. |
| 1800 | `private List<LandMap.Chip> islandChips` |  |
| 1801 | `private long islandFor` |  |
| 1907 | `private final Map<Integer, List<double[]>> freeCells` | Each offer's free ground as rectangles of plots (LandMap.freeCells()) and its edge as runs (LandMap.freeOutline()), by offer id, for the land and listing above: found with the holes, not every frame. |
| 1908 | `private final Map<Integer, double[][]> freeEdge` |  |
| 1911 | `private java.util.Set<Long> groundFor` | The picked blocks' free ground as cells and its edge as runs (LandMap.freeCells(), freeOutline() of the blocks): worked out again only when the pick's blocks (the set picks.blocks() hands out is a new one at every cha... |
| 1912 | `private long groundShapesFor` |  |
| 1913 | `private List<double[]> groundCells` |  |
| 1914 | `private double[][] groundEdge` |  |
| 1917 | `private String previewFor` | ...and the blocks a box being drawn would take: its cells and edge, for the box of plots, the pick and the land it was found for. |
| 1918 | `private List<double[]> previewCells` |  |
| 1919 | `private double[][] previewEdge` |  |
| 2198 | `private boolean globeHalf` | The globe drawn while it was half its resolution (the view on the move). |
| 2296 | `private GlobeFrame globeNodesFor` |  |
| 2297 | `private long[] globeNodesAt` |  |
| 2331 | `private GlobeFrame chartNodesFor` |  |
| 2332 | `private long[] chartNodesAt` |  |
| 2343 | `private volatile long[] chartsWanted` | The chart nodes WORKER's queue reads from its own thread (chartNodes()'s range for the view being painted): a node asked for a view that has moved on is dropped unread. |
| 2346 | `private int chartGen` | The globe's generation as far as chart nodes go: moved by resetGlobe(), so a node that comes back from WORKER after it is dropped. |
| 2851 | `private final Map<Integer, double[]> globeLabelAt` | The squares the globe's numbers stand on, by offer id, for the land they were found for and the scale (their last element). |
| 2852 | `private long globeLabelsFor` |  |
| 3059 | `private volatile String quoteWanted` | The block the hover wants quoted now: its key (block, level, ground, month and office), or "" while the pointer is over none. |
| 3062 | `private final Map<String, LandParcel> quotes` | The quotes the hover has had (LandMarket.quote(), a null for a block with none), by key, the latest asked kept (QUOTES_KEPT): the screen's thread's. |
| 3067 | `private final Set<String> quotesAsked` | The quotes on QUOTES' queue or running and not back: a block asked once, not again for each frame the pointer stays on it. |
| 3070 | `private LandMarket.Quoter quoter` | The office as it stood (LandMarket.quoter()) for the office, level, ground and month in quoterFor, and what it was made for; null where the office has none. |
| 3071 | `private String quoterFor` |  |
| 4050 | `private static volatile CompletableFuture<NationsMap> nationsBuild` | The nations' map, built once for the process on WORKER from the world file, which is kept open for the game's life - the map's exact ownerAt() reads it, so it is never closed. |
| 4083 | `private Nations nations` | The nations' figures of the game on the screen (Game.getNations(), asked on WORKER: its first build is the path to 2100), and the game they are of; and the game they were asked for. |
| 4084 | `private Game nationsOf, nationsAskedOf` |  |
| 4086 | `private boolean nationsWaiting` | Whether this view waits for the map's build to come back (it draws again when it does). |
| 4089 | `private List<NationsMap.Mark> marks` | The dots and labels of the last view drawn, and what they were made for: the view (a copy), the year, the figures and the map. |
| 4090 | `private GlobeFrame marksView` |  |
| 4091 | `private int marksYear` |  |
| 4092 | `private Nations marksFrom` |  |
| 4093 | `private NationsMap marksOf` |  |
| 4096 | `private javafx.scene.text.Font nameFont, figuresFont` | The label fonts, and the width of words in them kept by string (the names never change, the figures once a year). |
| 4097 | `private final javafx.scene.text.Text measure` |  |
| 4098 | `private final Map<String, Double> nameWidths` |  |
| 4099 | `private final java.util.function.ToDoubleFunction<String> nameW` |  |
| 4103 | `private EarthProj planeProj` | The plane's projection round the city's spot, kept per world (the borders that cross the city's ground are found on it). |
| 4104 | `private World.Key planeProjKey` |  |
| 4107 | `private TradeLanes lanes` | The city's trade as the screen holds it (WN3): the lanes of the last figures and the map they were made for, a generated world's arrows. |
| 4108 | `private NationsMap lanesOf` |  |
| 4109 | `private TradeLanes.Arrows arrows` |  |
| 4112 | `private Label nationsChip, bordersChip, tradeChip` | The head's chips and their group, and what they were last styled for. |
| 4113 | `private HBox nationsGroup` |  |
| 4114 | `private String chipsFor` |  |
| 4117 | `private VBox nationBox` | A nation's card in the pane, the nation it is of (0 for none), what it was drawn for, and the point on the screen it stands beside. |
| 4118 | `private int nationShown` |  |
| 4119 | `private String nationKey` |  |
| 4120 | `private double nationX, nationY` |  |
| 5069 | `private final Label legendNote` |  |
| 5071 | `private String legendZones` | The zones the legend was laid out with (0.8.0): their words, joined. |
| 5072 | `private final GridPane legendGrid` |  |
| 5073 | `private final Label shipsLine` |  |
| 5075 | `private final Label trucksLine` | The legend's trucks line: shown while trucks are drawn this frame (legendTrucks()). |
| 5117 | `private HBox legendHead` | The legend's head line, its chevron and its "click to fold" (WN2), and its body. |
| 5118 | `private VBox legendBody` |  |
| 5119 | `private final Label legendChevron` |  |
| 5221 | `private boolean cinematic` | Whether the replay's cinematic mode is on for this view: the scale bar is not drawn, the head and the legend are hidden, the cursor is none. |
| 5224 | `private String paddedStyle` | The pane's style as buildPane() set it, kept to put back what cinematic mode took out (its padding). |
| 5227 | `private final javafx.beans.value.ChangeListener<javafx.scene.Cursor> cursorOff` | Holds the canvas's cursor at none while cinematic mode is on, whatever cursorFor() or a press asks for. |
| 5304 | `private boolean keepPictures` | Set while reset() runs for a replay's quick step: the pictures are kept, stale (staleAll()), not thrown out. |
| 5307 | `private long camNanos` | When the camera's last frame was taken, for its dt (0 when it has not run). |
| 5478 | `final int serial, of` |  |
| 5479 | `final boolean months` |  |
| 5481 | `int next, passed` | The plan's next view, the views passed, whether the view {@code next} is taken, and since when the worker has sat idle on it (0 for not idle). |
| 5482 | `boolean applied` |  |
| 5483 | `long idleSince` |  |
| 5485 | `boolean running` | From the click's wait to the move's end: the run takes pictures from the store. |
| 5487 | `final Set<Long> stuckNodes` | The far nodes and chart nodes that were asked and never came back, when their view was passed. |
| 5489 | `final HashMap<Integer, HashMap<Long, TileCopy>> tilesAt` | Month, then (cache index and tile key) to the tile's picture; and (cache index and tile key) to the copies by stamp. |
| 5490 | `final HashMap<Long, List<TileCopy>> tileCopies` |  |
| 5492 | `final HashMap<Integer, HashMap<Long, NodeCopy>> nodesAt` | Month, then node key to the node's picture; and node key to the copies. |
| 5493 | `final HashMap<Long, List<NodeCopy>> nodeCopies` |  |
| 5495 | `final Set<CameraPath.View> planned` | Every view the load painted, so the run's tally can count a frame that showed any other (MoveTally.offPlan). |
| 5505 | `private MoveLoad move` | The loaded move, or null (always, outside a replay's glide or fly). |
| 5643 | `private long framesDrawn` |  |
| 5871 | `long frames, unpaintedFrames, halfFrames` | Frames tallied; of them the ones with anything black, coarse or stale; and those a globe drew at half resolution. |
| 5873 | `long black, coarse, stale` | Cells drawn as nothing, from a stand-in, from an older version: each summed over the frames. |
| 5875 | `long asks, evicted` | WORKER tasks submitted, and pictures let go by the caches, during the frames (from the camera's frame to the tally's, summed). |
| 5877 | `long offPlan` | Frames of a loaded move's run that showed a view its load never painted (CM1: the run stays on the plan, at any frame rate). |
| 5879 | `long frameNanosSum, worstFrameNanos` | The gaps between consecutive tallied frames, summed and the worst, in nanoseconds (the first frame after reset() has no gap). |
| 5881 | `final Map<String, long[]> byWhere` | By where the frame drew: {frames, unpainted frames, black, coarse, stale}. |
| 5884 | `private long lastNanos` |  |
| 5885 | `private boolean timed` |  |
| 5941 | `private final MoveTally moveTally` |  |
| 5947 | `private boolean tallyThisFrame` | Set by replayCamera() for a frame of a glide or fly, and cleared by tick() when it has tallied the frame; WORKER's task count and the caches' evictions as that frame began. |
| 5948 | `private long tallyTasks, tallyEvicted` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 240 | 5809 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 288 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 288 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 323-393)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 14 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count ... |
| 353 | 23 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 359 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 360 | 6 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |
| 367 | 8 | `void trim()` _(in MapView.Pictures)_ | The pictures over `most` let go, the eldest first (what a pin that is taken off leaves to do). |

### what the player is doing (lines 394-406)

### buying from the map (0.7.137, batch M6) (lines 407-434)

### the boats (0.7.97) (lines 435-470)

### the globe (0.7.135, batch E9) (lines 471-651)

| line | len | member | says |
|---:|---:|---|---|
| 510 | 41 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 576 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 579 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 582 | 16 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 599 | 29 | `private void reset()` |  |
| 630 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 637 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 644 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 652-1077)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 52 | `private void tick()` |  |
| 718 | 57 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 777 | 9 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 788 | 9 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 798 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 814 | 55 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - keyed (MapTiles.tileKey(), batch UP5), stamped again only if the key moved, and painted only wh... |
| 871 | 18 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 891 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 901 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 906 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 910 | 8 | `private boolean paintNodes(MapFrame f, long deadline)` |  |
| 929 | 31 | `private void askPictures(MapFrame f)` | The district pictures the far nodes drawn this frame lacked (0.7.128, batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order, while the view's districts fit wha... |
| 971 | 61 | `private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept, MapFrame asker)` | The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). |
| 1043 | 31 | `private void askNodes(List<long[]> asking, long plots, long span, World.Key world, long[] wanted)` | The far nodes a call of paintNodes() found it lacked, each {nx, ny, key}, their ground asked of WORKER from the middle of `wanted` out (MS2). |
| 1076 | 1 | `long farRecoloured()` | How many far nodes this view has coloured (batch UP3): the figure a probe reads against the nodes in view. |

### THE DRAWING (lines 1078-1131)

| line | len | member | says |
|---:|---:|---|---|
| 1082 | 49 | `private void draw()` |  |

### THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) (lines 1132-1216)

| line | len | member | says |
|---:|---:|---|---|
| 1152 | 40 | `private void worldLines(GraphicsContext gc, MapFrame f)` | The highway's and the main line's faded runs past the city's ground (CityMap.worldLines()): each from its point along its way out over dry ground, as far as the land runs (CityMap.WorldLine.reach(), at most CityMap.WO... |
| 1194 | 22 | `private static double[] rayRect(double x, double y, double dx, double dy, double tMax, double w, double h)` | The part of the segment (x, y) + t (dx, dy), 0 <= t <= tMax, that lies on the canvas, {x0, y0, x1, y1}; null when it misses it. |

### THE BOATS (0.7.97, batch O13) (lines 1217-1321)

| line | len | member | says |
|---:|---:|---|---|
| 1222 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 1239 | 82 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: the lanes on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), a... |

### THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 1322-1391)

| line | len | member | says |
|---:|---:|---|---|
| 1335 | 56 | `private void trains(GraphicsContext gc, MapFrame f, int level)` | The trains over the tiles, from L1 in, as the boats: the month's schedule (TrainSchedule, from the railway's tonnes by kind) on the map's routes (CityMap.trainRoutes()) at the boats' clock. |

### a boat or a train names its partner (WN7) (lines 1392-1471)

| line | len | member | says |
|---:|---:|---|---|
| 1398 | 12 | `private void aimLabel(MapFrame f)` | The pointer and the reach of it in plots for the frame about to be drawn, and nothing named yet: boats() and trains() offer the boats and vehicles they draw to nameable() as they draw them, so no second walk of the sc... |
| 1415 | 9 | `private void nameable(Object call, double gap, double middle, double sx, double sy)` | A boat or a vehicle the pointer may be on, `gap` plots from its hull or body (0 inside), its middle `middle` plots off, drawn at (sx, sy) on the canvas: kept if it is within the reach and nearer than the one kept, the... |
| 1426 | 9 | `private static double polygonGap(double px, double py, double[] xs, double[] ys)` | How far a point is from a polygon's outline in plots: 0 inside it. |
| 1437 | 5 | `private static double segmentGap(double px, double py, double ax, double ay, double bx, double by)` | How far a point is from the segment (ax, ay) to (bx, by). |
| 1448 | 23 | `private void partnerLabel(GraphicsContext gc, MapFrame f)` | The label of the boat or train under the pointer, in the world lines' style (the mono font at Palette.SIZE_BODY, white at WORLD_LINE_ALPHA, a halo under it to read on the tiles): CallPartners.words() of its call, up a... |

### the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) (lines 1472-1768)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 1 | **type** `private record DistrictTrucks(List<TruckSchedule.Route> trips, TruckSchedule schedule)` | A district's trucks' schedule, with the trips it was made on: made again when its district's trips are another list. |
| 1503 | 37 | `private void trucks(GraphicsContext gc, MapFrame f, int level)` | The trucks over the tiles, at the near view alone and while the setting is on (TruckSchedule.drawnAt()): each district's trips (CityMap.tripsDrawn(), a truck's whole way from the world line to a works' gate and back, ... |
| 1542 | 7 | `private int fillTrucks(GraphicsContext gc, MapFrame f, TruckSchedule schedule, long x0, long y0, long x1, long y1, double t)` | One schedule's trucks in the box filled in their colours, or nothing when it has none or no route of it crosses the view; the loads a truck of it stands for, or 0 when none was drawn. |
| 1551 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 1556 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 1562 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 1569 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 1574 | 23 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 1598 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 1628 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 1675 | 83 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched shapes - each its free ground only, the city's own ground in ... |

### the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") (lines 1769-1795)

| line | len | member | says |
|---:|---:|---|---|
| 1778 | 17 | `private void zoneTints(GraphicsContext gc, MapFrame f, Game g)` | The city's zones over the ground, under the offers and the city's edge: each zone's districts in its sector's colour at LandMap.ZONE_ALPHA, the districts the map picks for it (CityMap's ZONES BY DISTRICT); nothing wit... |

### the ground no port links (ET5, slice D; the ET5 design, section 5) (lines 1796-2180)

| line | len | member | says |
|---:|---:|---|---|
| 1809 | 33 | `private void islandHatch(GraphicsContext gc, MapFrame f, Game g, CityLand land)` | The city's ground that no port links, hatched in amber over the ground (under the offers and the city's edge) and named on a chip at the middle of each landmass: "Island 2 · 12.0 km² · not linked: needs a port here (h... |
| 1844 | 22 | `private void islandChips(GraphicsContext gc, MapFrame f, Landmasses lms)` | Each such landmass's chip (LandmassView.chipWords()), mono caption on a dark stroke, as newTag() writes NEW: centred on the middle of its owned tiles, kept inside the view. |
| 1868 | 7 | `private List<String[]> islandEntries()` | The amber legend entry for that hatch (LandMap.unlinkedLegend()) while any landmass is unlinked, as the zones' entries are given: {css colour, words}. |
| 1877 | 10 | `private List<String[]> zoneEntries()` | The zones' legend entries on the plane (0.8.0): each zone set, by its sector's label. |
| 1888 | 17 | `private void shapes(CityLand land, LandMarket market)` |  |
| 1922 | 13 | `private void groundShapes(CityLand land)` | The picked blocks' shapes, kept (groundCells, groundEdge) for what is picked and the land it lies on. |
| 1940 | 19 | `private boolean previewShapes(CityLand land)` | The blocks the box being drawn would take, as cells and an edge (previewCells, previewEdge): worked out when the plots it covers, the pick or the land change, not every frame. |
| 1968 | 6 | `private static void edgeRuns(GraphicsContext gc, MapFrame f, double[][] runs, int width, boolean dashed, Color colour)` | Runs of an outline on whole pixels as one shape (BL1), `width` px deep inside the ground they bound - one the city's edge's, two the picked ground's - solid or dashed (DASH_ON on and the rest off, from each run's own ... |
| 1976 | 29 | `private static void runRects(GraphicsContext gc, MapFrame f, double x0, double y0, double x1, double y1, int width, boolean das...` | One run of an outline added to the path as its pixels' boxes (MapFrame.runOnScreen()), `width` deep inside the ground it bounds and cut into dashes when asked. |
| 2007 | 13 | `private void pickedGround(GraphicsContext gc, MapFrame f)` | The picked blocks (BL1): their free ground tinted as a picked offer's is and its edge solid in the offers' pink, two px deep, as one shape - the city's ground inside a block it owns part of left clear. |
| 2025 | 11 | `private void boxPreview(GraphicsContext gc, MapFrame f)` | The blocks a box being drawn would take, tinted under it and edged a pixel wide (BL1), so the snap to the block grid is seen before the release. |
| 2038 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 2050 | 27 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 2082 | 10 | `private void newTag(GraphicsContext gc, LandParcel p, double x, double y, double boxH)` | ...under the number at (x, y), where the offer's box on the screen is at least two numbers tall (MapFrame.PLACE_LABEL_FROM each). |
| 2094 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 2121 | 5 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 2128 | 17 | `private static void scaleBar(GraphicsContext gc, double h, double px, String words)` | ...drawn: `px` long at the bottom left of a view `h` high, and its words. |
| 2150 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 2155 | 3 | `static String css(int argb)` | ...as CSS. |
| 2160 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |
| 2171 | 9 | `private static ImagePattern islandHatchPattern()` | The hatch over the ground no port links (ET5): the offers' diagonals drawn in the warning amber (Palette.WARN, #e3b341) at the same weights. |

### THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) (lines 2181-3055)

| line | len | member | says |
|---:|---:|---|---|
| 2201 | 1 | `private boolean onGlobe()` | Whether the expanded map draws the globe: on an Earth world, wider than the handoff - or, since MS1, closer than it with the view's centre past the city's reach, where the plane is not drawn (GlobeFrame.tier()). |
| 2204 | 1 | `private boolean globeShown()` | ...or lays its picture over the plane's in the crossfade's notch (GlobeFrame.crossfade(), the view's: whole where there is no plane under it). |
| 2207 | 13 | `private void resetGlobe()` | A new city, or another world: no globe until the map is over the window on Earth again - and no chart nodes (MS3): those in flight come back to another generation and are dropped. |
| 2222 | 14 | `private void resetGlobePicture()` | What the globe drew from and drew: another map, or another view of the world. |
| 2238 | 24 | `private void globeUp(CityLand land, LandMarket market, long landKey)` | The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. |
| 2264 | 4 | `private void follow()` | The view moved on the globe: where it draws the plane (the plane's scales, its centre within the reach) the plane's frame follows it - past the reach, closer than the handoff, there is no plane to follow (MS1); the vi... |
| 2270 | 25 | `private long[] globeNodes(GlobeFrame v)` | The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws (GlobeFrame.drawsHold(): since MS2 at the plane's scales the part of it the world's square hold... |
| 2306 | 24 | `private long[] chartNodes(GlobeFrame v)` | The chart nodes the globe draws for a view (MS3) - {plots a pixel, the chart's id, nx0, nx1, ny0, ny1} - over the part of it the view's chart draws (GlobeFrame.chartHolds(): at the plane's scales the ground past Globe... |
| 2338 | 3 | `private static long chartKey(long id, long plots, long nx, long ny)` | A chart node's key (MS3): the chart's id (GlobeFrame.chartFor()'s row and column, 13 bits) in the top 20 bits, the stride's power of two in 4, and the node's two indexes in 20 bits each (a chart reaches some 400 nodes... |
| 2353 | 18 | `private boolean paintGlobe(long deadline)` | A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack; and the chart nodes past the... |
| 2379 | 52 | `private void askCharts(long[] r)` | The chart nodes `r` (chartNodes()) the cache lacks asked of WORKER, each once, from the middle of the range out (MS3; askNodes()'s rule for the plane's far nodes): WORKER makes the node's classes and relief (MapTiles.... |
| 2433 | 18 | `private void chartColoured(int id, long plots, long nx, long ny)` | A chart node come in: where the globe's still picture shows it, laid over again next frame - nodeColoured()'s rule for the plane's, the node's corners and mid-points from its chart (east and south of the anchor on its... |
| 2453 | 14 | `private void nodeColoured(long x0, long y0, long span)` | A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. |
| 2469 | 9 | `private void addPatch(double x0, double y0, double x1, double y1)` | The still picture's box to be drawn again, grown to take in another. |
| 2487 | 32 | `private void composeGlobe(int mode)` | The globe's picture of the view, drawn again when the view, the way it is drawn or what it is drawn from moved: at half resolution while the view moves (its frame a copy of half the size at twice the km a pixel), at i... |
| 2521 | 48 | `private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1)` | The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image: the plane's nodes and, past the hand-over at the plane's scales, the view's chart nodes... |
| 2571 | 18 | `private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic)` | Past the plane and the charts, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. |
| 2591 | 7 | `private void drawGlobeImage(GraphicsContext gc)` | The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). |
| 2611 | 39 | `private void askPieces(GlobeFrame view)` | The GROUND tier's pieces past the plane for a view come to rest: the far rule through the camera (GlobeFrame.groundCodes()) a PIECE_PX square at a time on WORKER, from the middle out, each one with some of its pixels ... |
| 2652 | 7 | `private void pieceIn(int gen, int[] q, byte[] b)` | A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. |
| 2661 | 31 | `private void drawGlobe()` | The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its block lines past the reach at the plane's scales, its edge through the camera in the PI... |
| 2697 | 10 | `private double[][] onGlobe(double[] xs, double[] ys)` | A shape of plots through the camera: its corners on the screen, or null where one is not seen. |
| 2716 | 84 | `private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market)` | The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - each offer its free ground (its cells, LandMap.freeCells(), the city's own ground ... |
| 2802 | 4 | `private double plotPx(double x, double y, long span)` | Pixels a plot spans on the globe where the plane's plot (x, y) is: the camera's step over `span` plots east of it over the span, 0 where either end is not seen. |
| 2808 | 15 | `private boolean cellsPath(GraphicsContext gc, List<double[]> cells)` | Cells of plots (rectangles {x0, y0, x1, y1}) as one path through the camera, each a quadrilateral of its four corners - a cell with a corner not seen left out, as boxPicks() does; false when none is in the path. |
| 2825 | 10 | `private void runsOnGlobe(GraphicsContext gc, double[][] runs, int width, boolean dashed, Color colour)` | The runs of an outline through the camera, a line each between its ends where both are seen: `width` px, solid or dashed as the plane's offers' edges are. |
| 2837 | 12 | `private double[] labelSquare(CityLand land, LandParcel p, double plotPx)` | Where an offer's number stands on the globe, the square of plots (LandMap.labelBlock()) found for the camera's scale - a number's PLACE_LABEL_FROM px in plots, to a power of two - and kept until the land moves; null w... |
| 2863 | 17 | `private void globeBlockLines(GraphicsContext gc, int level)` | The city's block lines through the camera past the reach at the plane's scales (MS2): the plane draws them faint over its whole ground where a block is at least MapFrame.BLOCK_LINES_FROM px across (blockLines()), and ... |
| 2888 | 17 | `private boolean blockLine(GraphicsContext gc, double ax, double ay, double bx, double by, int depth)` | One block line from plot (ax, ay) to (bx, by) through the camera onto the path: its ends, and its middle's plot against the chord, in two where it strays more than BLOCK_LINE_SAG_PX. |
| 2907 | 4 | `static double distanceFromChord(double[] m, double[] a, double[] b)` | How far a point is from the chord a to b, in pixels. |
| 2913 | 12 | `private void globeEdge(GraphicsContext gc, CityLand land)` | The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. |
| 2927 | 11 | `private void ring(GraphicsContext gc, CityLand land)` | The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. |
| 2940 | 5 | `private void globeScaleBar(GraphicsContext gc)` | The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. |
| 2947 | 47 | `private void globeHover(double sx, double sy)` | The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it (drawnAt(): past the hand-over, MS3, the cha... |
| 3008 | 47 | `private List<String> groundLines(long x, long y)` | What the hover card says over free ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: over a block that may be picked, "Your ground: one block", its area by kind and its pric... |

### the hover's quote, away from the screen's thread (WN5b, 2026-11-02) (lines 3056-3142)

| line | len | member | says |
|---:|---:|---|---|
| 3078 | 23 | `private void askQuote(LandMarket.Quoter use, String quoteKey, long[] r)` | Asks QUOTES for a block's quote, `r` the block's {x0, y0, x1, y1}. |
| 3103 | 7 | `private static void groundInto(List<String> lines, List<String> ground)` | The ground lines in the hover card where "Not the city's" stood (after its first line when the card has not that line), their last line, the blue one, at the card's foot. |
| 3116 | 14 | `private byte drawnAt(double sx, double sy)` | The ground drawn at a screen point on the globe, past the plane, as a code - its class and relief (GlobePicture.code()): where the view's chart draws it (GlobeFrame.chartHolds(), MS3: at the plane's scales the ground ... |
| 3132 | 10 | `private byte chartCode(double sx, double sy)` | The code (GlobePicture.code()) of the pixel of the view's chart node under a screen point - the node ground() lays there (GlobeFrame.chartAt()) - or -1 where the node is not in yet. |

### the keys (0.7.135; spec-globe 3.11) (lines 3143-3184)

| line | len | member | says |
|---:|---:|---|---|
| 3146 | 24 | `private boolean keys()` | A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not h... |
| 3172 | 12 | `boolean panKey(KeyCode k, boolean down)` | W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. |

### THE POINTER AND THE KEYS (lines 3185-3532)

| line | len | member | says |
|---:|---:|---|---|
| 3189 | 98 | `private void handlers()` |  |
| 3289 | 7 | `private void zoomBig(double sx, double sy, double notches)` | The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. |
| 3302 | 4 | `private long[] plotAt(double sx, double sy)` | The plot of the city's plane under a screen point as the map draws it - the plane's own frame, or on the globe the camera's, where the plane is drawn (GlobeFrame.planeHolds()) - or null where there is none (the globe'... |
| 3308 | 9 | `private double[] plotXY(double sx, double sy)` | ...as the plane's own fractions of plots {x, y} (what a box's corners are read by: LandMap.plotBox()), or null where there is none. |
| 3323 | 12 | `private long[] boxPlots(double sx0, double sy0, double sx1, double sy1)` | The plots a box between two screen points covers (LandMap.plotBox()), its four corners read through plotXY() - the plane's frame, or on the globe the camera where the plane is drawn, a corner it does not hold left out... |
| 3342 | 6 | `private boolean pressBoxes(double sx, double sy, boolean shift)` | Whether a left press at a screen point draws a box rather than pans (MS1, widened by BL1): where the offers are drawn to be picked (canPick()), Picks.pressBoxes() of the plot under it - an offer's free ground, a picke... |
| 3358 | 37 | `private void click(double sx, double sy, boolean shift)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on - on the globe only where the city's plane is drawn (0.7.135). |
| 3403 | 23 | `private boolean blockClick(long x, long y, boolean shift)` | A click on ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: a free block that may be picked is picked alone, and with Shift toggled; a block already picked keeps the picks ... |
| 3428 | 50 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 3485 | 5 | `private void cursorFor(boolean boxHere)` | The cursor says what a left press would do (MS1, BL1): the crosshair where it would draw a box - over an offer's free ground or ground that is picked or may be (`boxHere`, LandMap.pressBoxes() without Shift), or anywh... |
| 3495 | 3 | `private static boolean isHint(String line)` | Whether a hover card's line is the blue last line telling the player what a click does. |
| 3500 | 23 | `private void showCard(List<String> lines, double sx, double sy, double vw, double vh)` | The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - the hint last (CLICK_TO_BUY, CLICK_TO_PICK, CLICK_TO_DROP, and a nation's NationsMap.CLICK_FOR_CARD) in the accent's light... |
| 3528 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 3533-3976)

| line | len | member | says |
|---:|---:|---|---|
| 3566 | 1 | **type** `record Bought(List<Integer> ids, List<int[]> places, String title, double[] at, double[][] edge)` | A purchase from the card: the offers and drafts found listed when it was asked for, their places ({side, place, 1 for a draft or 0} each - a draft's place is where its middle lies, which no "next ground" line is about... |
| 3578 | 3 | `private boolean canPick()` | Whether the offers are drawn to be picked where the expanded map looks (star M6): on the plane at every scale, and on Earth in the globe's GROUND tier, where globeOffers() draws them - not its PICTURE tier, which draw... |
| 3583 | 7 | `private LandMap.ToScreen toScreen()` | A plot of the city's plane on the screen, as the expanded map draws it now: through the globe's camera, or the plane's frame. |
| 3592 | 3 | `private static double[] box(LandParcel p)` | An offer's box in plots, {x0, y0, x1, y1}. |
| 3597 | 11 | `private static double[] bounds(List<LandParcel> ps)` | ...and the bounds of several, or null with none. |
| 3610 | 9 | `private List<LandParcel> listed(List<Integer> ids)` | The listed offers among these ids, in their order. |
| 3625 | 10 | `private void boxed(double x0, double y0, double x1, double y1)` | A drag let go (B7, MS1; BL1: a box takes ground, not offers): the blocks the box covers that join the city added to the picks (Picks.addBox(), the box's corners through plotXY(): the snap to the block grid the preview... |
| 3637 | 13 | `void selectAll()` | "Select every offer" in the head, or Ctrl+A (B7): every offer listed onto the card, the office's best first, the card beside the best (star M6). |
| 3652 | 24 | `private void showBuyCard()` | The card drawn again for the picks as they stand, or put away with none listed. |
| 3678 | 8 | `private void hideBuyCard()` | The card put away. |
| 3688 | 5 | `private String cardKey(List<Integer> ids)` | What the card was drawn from: the picks, the toggle, the cash and the vault, the month and the listing. |
| 3695 | 16 | `private void syncCard()` | A frame's look at the card (tick()): drawn again when what it says moved - the month dropping the picks, an offer bought elsewhere falling out of them, the cash - and the receipt put away with its month. |
| 3713 | 5 | `private void layoutBuyCard()` | The card sized to what it says, then placed. |
| 3720 | 16 | `private void placeBuyCard()` | The card beside its box of plots as the view moves (each draw): at its right, else at its left, its top level with the box's, inside the view. |
| 3738 | 15 | `private double[] screenBox(double[] b)` | A box of plots on the screen, {x0, y0, x1, y1} round its corners as drawn now, or null when none is seen. |
| 3755 | 16 | `private void drawRings(GraphicsContext gc)` | The card's fields' sites, ringed wherever they lie (board 7): each a ring in its resource's colour on a dark halo, as the city's ring is drawn (ring()), round its square - RING_PX at least. |
| 3773 | 11 | `private void drawBox(GraphicsContext gc)` | The box Shift and a drag draws (B7): the offers' pink, faint inside, dashed round. |
| 3786 | 7 | `void closeCard()` | The card's ×, Esc, a click off the offers: no picks, no card. |
| 3795 | 5 | `void dropPick(int id)` | A chip's × on a card of several: that one dropped. |
| 3802 | 5 | `void dropGround(List<Integer> drafts)` | The "Your ground" chip's ×: every draft of the picked blocks dropped, which drops the blocks they were cut from - one chip for all of them, as the card names them as one (blocks that joined through them go too, said i... |
| 3809 | 5 | `void paidOtherwise()` | The toggle moved, on the card or the head: both drawn again in the other money. |
| 3816 | 12 | `private Bought boughtOf(List<Integer> ids)` | What a purchase from the card is of, asked for now. |
| 3830 | 9 | `void buy(List<Integer> ids)` | The card's Buy, or Enter (B2): the offers bought as the office's Buy buys them (LandScreen.buyOrFund()) - the office drawn again under the map, which stays - and the receipt by the ground; short after all, the funding... |
| 3841 | 9 | `void buyOnLoan(List<Integer> ids, Runnable issue)` | The card's bond or note (B1): issued as the funding page issues it, the offers bought on it (LandScreen.offers()' own action), and the receipt; what falls short shows the office's page, which comes back here. |
| 3852 | 10 | `void toFunding(List<Integer> ids)` | "All the ways to pay" (B5): the funding page for the picks, the map put away until the page is done and brought back at the view it left (back()). |
| 3864 | 6 | `void toOffice(int s, int p)` | "Open in the land office": the map put away on the offer's row, the office's side and pick. |
| 3878 | 11 | `void back(boolean fromFunding)` | The office drawn (LandScreen.showLandMenu()), `fromFunding` when the funding page or what it fell short with is what it comes back from: a purchase the card sent there brings the map back over the window at the view i... |
| 3891 | 27 | `private void showBought()` | The receipt by the ground just bought (board 6): the card's purchase, those of it no longer listed - "2 of 3 offers" when it fell short - in the office's words (LandScreen.mapReceiptNode()); with none bought, the card... |
| 3926 | 26 | `boolean cardKey(javafx.scene.input.KeyEvent e)` | Esc, Enter and Ctrl+A while the map is over the window, from UserInterface's key filter (0.7.137): Esc closes the card - a second Esc, not taken here, the map - Enter presses the card's main button (B2: Buy, or short ... |
| 3959 | 17 | `private void syncHead()` | The head's money, pay toggle and note, drawn again when the toggle, the cash, the vault, the month or the note moved. |

### THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) (lines 3977-4212)

| line | len | member | says |
|---:|---:|---|---|
| 4053 | 4 | `private static synchronized CompletableFuture<NationsMap> askNationsBuild()` | The build asked of WORKER the first time it is wanted. |
| 4059 | 22 | `private static NationsMap buildNations()` | The world file opened and the map made of it, on WORKER; null (and a line in the log) where there is no file or it will not open. |
| 4123 | 5 | `private static Map<String, Double> widths()` | A bounded map of widths by string, the most recently used kept. |
| 4130 | 10 | `private double widthOf(String words, javafx.scene.text.Font font, Map<String, Double> kept)` | The width of words in a font, kept: a Text measured once per string. |
| 4142 | 11 | `private void forgetNations()` | Another city or another game: the nations of the last one are not this one's. |
| 4160 | 35 | `private void nationsUp(CityLand land)` | Asked every frame (keepUp()): on an Earth world, once a chip is on or the map is over the window - the chips are shown only once the map is built, so a player who turned both off can turn them on - the nations' map is... |
| 4197 | 6 | `private NationsMap nationsMap()` | The nations' map when it is built and this city stands on the real Earth; null on a generated world, before the build and where the world file is not here. |
| 4205 | 1 | `private Nations nationsNow()` | The game's nations when they are in for the game on the screen, else null. |
| 4208 | 4 | `private NationsMap builtMap()` | The nations' map when it is built, whatever world this is: a generated world's arrows stand by its nodes though it draws no nations. |

### the city's trade (WN3) (lines 4213-4289)

| line | len | member | says |
|---:|---:|---|---|
| 4220 | 4 | `private TradeLanes.Feed tradeFeed()` | The city's trade by partner - THE ONE SEAM the wiring filled (WN3): the game's feed over last month's trade by partner and the year to date (Game.getTradeFeed(), an immutable snapshot published through a volatile, so ... |
| 4231 | 29 | `private CallPartners partnersNow()` | The partners the month's boats and trains go to (WN7): CallPartners over the feed's snapshot when it is one (PartnerTrade.Snapshot), the city's place - the real Earth's, the spot the globe is centred on, else a genera... |
| 4262 | 6 | `private boolean standsIn()` | Whether this is a generated world whose feed names the place the city stands in, so its partners can be drawn as arrows. |
| 4270 | 9 | `private TradeLanes lanesNow(NationsMap nm)` | The lanes of the feed's figures now: the ones kept while the month, the city, the map and every figure are the same, else made again (a fraction of a millisecond; never in a frame that has them). |
| 4281 | 8 | `private TradeLanes.Arrows arrowsNow()` | A generated world's arrows of the feed's figures now, kept the same way; null where the map is not built or the feed names no stand-in. |

### the chips (lines 4290-4359)

| line | len | member | says |
|---:|---:|---|---|
| 4293 | 16 | `private Label layerChip(String words, String tip, Runnable flip)` | A toggle chip of the head: a dot before its word, lit or not (styleChip()); a click runs `flip`. |
| 4311 | 5 | `private static void styleChip(Label chip, boolean lit)` | The board's .chip and .chip.lit: lit, the pinned ground, the accent's edge and dot and the full text; not, the panel, the edge, the label's text and the grey dot. |
| 4321 | 15 | `private void syncChips()` | The chips shown only on an Earth world whose nations' map is built, and lit as the settings stand (each frame of the head). |
| 4337 | 4 | `private void flipNations()` |  |
| 4342 | 4 | `private void flipBorders()` |  |
| 4347 | 4 | `private void flipTrade()` |  |
| 4353 | 6 | `private void layersFlipped()` | A chip flipped: saved at once (the settings are the player's, beside the saves), the card put away if its nations are off, the chips restyled and the view drawn again - which lays the legend out again (legendFor() key... |

### the legend (lines 4360-4387)

| line | len | member | says |
|---:|---:|---|---|
| 4367 | 20 | `private List<String[]> nationsEntries()` | The legend's nations entries - {colour, words} - on an Earth world with the map built: the border's white while Borders is on, the dot's colour while Nations is on and the view is a globe's, and (WN3) the violet and t... |

### drawing (lines 4388-4627)

| line | len | member | says |
|---:|---:|---|---|
| 4391 | 19 | `private static void strokeRuns(GraphicsContext gc, NationsMap.Runs r)` | Runs of border stroked as ONE path - the runs are many and short, about 6,000 on the planet - 1 px, dashed NationsMap.BORDER_DASH_PX, white at BORDER_ALPHA; the width and dashes put back. |
| 4417 | 31 | `private void tradeLanes(GraphicsContext gc, NationsMap nm)` | The city's trade lanes on the globe, between the borders and the dots (WN3, board 1): TradeLanes.runs() of the view - the edges in view, each shared lane once - stroked a path to each colour and whole width (at most T... |
| 4454 | 35 | `private void tradeArrows(GraphicsContext gc)` | A generated world's partners as arrows at the edge of its map (WN3, the study 6.4): the biggest TradeLanes.ARROWS_MAX over the floor, each at the edge on its bearing from the stand-in place the feed names - a violet a... |
| 4494 | 18 | `private static void arrow(GraphicsContext gc, double tipX, double tipY, double ux, double uy, double width, double off, boolean...` | One arrow of the edge: a shaft ARROW_LEN_PX long inward from its tip, a head at the outer end if it points out (what we sell) and at the inner end if it points in (what we buy), the whole moved `off` pixels sideways (... |
| 4514 | 8 | `private void globeNations(GraphicsContext gc)` | The globe's nations (after its picture, under the city's own drawing): the borders with Borders on, the trade's lanes with Trade routes on, the dots and labels with Nations on - each only when the map is built, and th... |
| 4527 | 19 | `private List<NationsMap.Mark> nationMarks(NationsMap nm, Nations ns)` | The dots and labels of the globe's view now (NationsMap.marks()), made again only when the view is not the one they were made for (GlobeFrame.sameView()), the year moved, the figures or the map are another's, or a fon... |
| 4548 | 35 | `private void drawMarks(GraphicsContext gc, List<NationsMap.Mark> list)` | The dots (NationsMap.DOT_ARGB at DOT_ALPHA, edged), then the labels of those that fit: the name as the board's .lbl and the figures as its .lbl2, centred, each on its halo. |
| 4590 | 15 | `private void planeBorders(GraphicsContext gc, MapFrame f)` | The borders on the city's plane (the plane's tier on Earth, and the land office's small map), after the tiles and what runs over them and before the world lines and the overlays: only the lines that cross the box of p... |
| 4607 | 20 | `private static double[] crossingBox(CityLand land, LandMarket market)` | The box of plots {x0, y0, x1, y1} of the city's land and its listed offers, as the reach is found from them (GlobeFrame.reachKm()); null with neither. |

### the pointer (lines 4628-4668)

| line | len | member | says |
|---:|---:|---|---|
| 4635 | 8 | `private List<String> nationHoverWords(double sx, double sy)` | What hovering a dot says (NationsMap.hoverWords(), its last line the blue CLICK_FOR_CARD), or null where the pointer is over no dot, Nations is off, or the city's plane is drawn under it: there the land office's hover... |
| 4645 | 6 | `private String ownerWords(double sx, double sy)` | Whose land is under the pointer, where the city's plane is not drawn: a nation's or an area's name (NationsMap.ownerName() of the exact ownerAt()); null elsewhere or with Nations off. |
| 4658 | 10 | `private boolean nationClick(double sx, double sy)` | A click on the expanded globe with Nations on, where the city's plane is not drawn under the pointer (plotAt() is null): on a dot or on a nation's land (NationsMap.nationAt(): the dot, else the exact owner) opens that... |

### the card (lines 4669-4812)

| line | len | member | says |
|---:|---:|---|---|
| 4672 | 7 | `private void showNationCard(int nation, double sx, double sy)` | A nation's card opened beside the point clicked: the buying card (and its picks) goes, as there is only ever one. |
| 4681 | 1 | `private String nationCardKey()` | What the card was drawn for: the nation and the month, which moves the year and the D$ it is struck in. |
| 4684 | 95 | `private void fillNationCard()` | The card made (again) for the nation shown, from NationsMap.card(): its head - the name, the region as a tag, a "×" - the distance under it, and its rows in a grid of three columns. |
| 4781 | 12 | `private void placeNationCard()` | The card beside the point clicked, as the buying card stands beside its offer: CARD_GAP off it, at its right else its left, level with it and CARD_MARGIN at least inside the view. |
| 4795 | 7 | `private void hideNationCard()` | The card put away. |
| 4804 | 8 | `private void syncNationCard()` | A frame's look at the card (tick()): made again when the month moved - the year's figures and the D$ it is struck in - and put away if its nation is not shown any more (Nations off, another game). |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 4813-5203)

| line | len | member | says |
|---:|---:|---|---|
| 4817 | 1 | `void expand()` |  |
| 4820 | 26 | `private void expand(boolean keepView)` | ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. |
| 4853 | 11 | `void expandOn(MiniMapFrame.Box box)` | Expanded on a box of plots (0.7.134, batch MM2): the minimap's click, the map over the window fitted on what the stamp frames (MiniMapFrame.open()) rather than on the land office's opening; Fit goes back to that. |
| 4866 | 24 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked - the card's picks dropped (0.7.137), and its purchase forgotten unless the funding page has it (B5). |
| 4891 | 80 | `private void buildPane()` |  |
| 4974 | 22 | `private void fitPane()` | The canvas sized to the pane: the window's width, and its height under the header (MS1: the pane lies below it, ui.chartFullTop()), inside the padding and under the head (City History's rule, read off the window). |
| 4998 | 10 | `private void openBig()` | The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. |
| 5010 | 10 | `private void fitBig()` | Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. |
| 5022 | 1 | `static List<String[]> legendEntries(int tier)` | The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. |
| 5025 | 5 | `static List<String[]> legendEntries(int tier, boolean relief)` | ...on an Earth world with earth-2's layers (`relief`, LandMap.reliefDrawn(); ET1 slice D) also the three greys, forest on a slope and, on the plane, the shallow sea. |
| 5032 | 1 | `static List<String[]> legendEntries()` | ...the plane's, at every scale on a generated world. |
| 5044 | 3 | `static String trucksWords(int loads)` | ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. |
| 5052 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed at its edge. |
| 5057 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 5062 | 3 | `static String trackWords(long plots)` | ...and (FR7 slice 3) when the model has track the railway's ways do not use - its main line, its sidings and its branches - how much, in plots; the model's words (LandMap.trackSurplusWords()). |
| 5077 | 38 | `private VBox legend()` |  |
| 5125 | 9 | `private void foldLegend()` | The legend folded or open, as the player left it (GamePrefs.isMapLegendFolded()): the body shown or not, the chevron pointing down or right, and the head's ground rounded all round when it stands alone. |
| 5136 | 31 | `private void legendFor(int tier)` | The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. |
| 5169 | 1 | `private int shownTier()` | The tier the legend and the hover are for (MS2): the globe's on Earth - the plane at every scale closer than the handoff, past the reach too, where the plane's own ground is what is drawn - and the plane at every scal... |
| 5172 | 5 | `private void legendTrucks()` | The trucks' line: how many loads a truck stands for while any is drawn this frame, on the plane; hidden otherwise. |
| 5179 | 14 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. |
| 5195 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

### REPLAY: CINEMATIC (TR1, 2026-10-31) (lines 5204-5278)

| line | len | member | says |
|---:|---:|---|---|
| 5232 | 3 | `private double padded(double px)` | A pane measure as it is now: the padding's and the head's room, none in cinematic mode. |
| 5241 | 21 | `void setCinematic(boolean on)` | Cinematic mode on or off for the expanded map: the head, the legend, the hover card and the padding gone and the cursor none, or all of it back as it was. |
| 5268 | 5 | `double[] replayView()` | The expanded map's view for the input log (InputRecorder): {1 on Earth else 0, a, b, scale, roll} - the globe's centre latitude and longitude, its km a pixel and its roll, or the plane's centre x and y, its scale and ... |
| 5275 | 3 | `void replayChanged()` | The city's map is another object (a replay's quick step swapped it): the view draws again, and the frame's keepUp() takes the new map, so a small map in the office and the expanded one both follow. |

### REPLAY: THE CAMERA, THE PAINT BUDGET AND THE QUICK STEP (TR1, 2026-10-31) (lines 5279-5446)

| line | len | member | says |
|---:|---:|---|---|
| 5314 | 3 | `private boolean takingSameLand(CityMap next)` | Whether the city's new map is a replay's quick step on the land on show: a replay is on and the new map is of the same world as the one shown. |
| 5325 | 12 | `private void staleAll()` | Every kept picture made stale against the new map, none thrown out: a tile picture's version and key forgotten, so the next paint() reads its inputs again and repaints it only if their stamp (a hash of the content, th... |
| 5339 | 5 | `CameraPath.View cameraView()` | The expanded map's view as a camera view (month 0: the camera puts the month on), or null when it is not expanded: on Earth the globe's centre and km a pixel, elsewhere the plane's centre and pixels a plot. |
| 5346 | 5 | `CameraPath.View cameraWhole()` | The widest view as a camera view, what Fit shows: on Earth the whole planet facing the city, on a generated world the whole world from its middle; null when the map is not expanded. |
| 5353 | 12 | `private void applyCameraView(CameraPath.View v)` | A view taken whole (a glide's, a fly's, or one of a move's load): the globe set to it and the plane following, or the plane set. |
| 5367 | 13 | `private void applyMove(CameraPath.Move m)` | The smooth hand's step: the zoom about its anchor as the wheel's notch is applied, then the pan as a drag's. |
| 5386 | 49 | `private boolean replayCamera()` | One frame of the replay's camera (tick()): W A S D's velocity to the smooth hand, then the camera's Shot applied. |
| 5437 | 9 | `private boolean replayWheel(double sx, double sy, double notches)` | The wheel's notches in a replay: eased in by the smooth hand about the pointer, and a glide in flight let go. |

### the move loaded before it runs (CM1) (lines 5447-5851)

| line | len | member | says |
|---:|---:|---|---|
| 5471 | 1 | **type** `private record TileCopy(long stamp, WritableImage image)` | A tile's picture as a month showed it: the stamp it was painted at and its own copy of the image (the cache paints a picture in place when its stamp moves). |
| 5474 | 1 | **type** `private record NodeCopy(long stamp, boolean partial, WritableImage image, int[] pixels)` | A far node as a month showed it: the stamp and whether it was partial, and its own copies of the image and the colours. |
| 5477 | 26 | **type** `private static final class MoveLoad` | What a loaded move holds: where the walk is, whether the run has begun, the views given up, and (for a move across months) the pictures each month showed. |
| 5497 | 5 | `MoveLoad(int serial, int of, boolean months)` _(in MapView.MoveLoad)_ |  |
| 5508 | 1 | `private boolean moveRunning()` | Whether a loaded move is running across months: the pictures come from its store. |
| 5510 | 4 | `private void beginMove(ReplayCamera cam)` |  |
| 5516 | 6 | `private void endMove()` | The move ran out, was let go, cancelled, failed or replaced: the caches are let go of for room again, down to their sizes. |
| 5524 | 6 | `private void backToStart(ReplayCamera cam)` | A load that did not reach its click, ended: the view and the month back to where the move begins. |
| 5531 | 6 | `private void pin(boolean on)` |  |
| 5544 | 70 | `private boolean loadStep(ReplayCamera cam)` | One frame of the load, within REPLAY_FRAME_MS: the plan's views from the next one on, each taken (and its month shown), painted with the real paint() and left only when loadSettled() says it lacks nothing. |
| 5616 | 7 | `private static boolean heapOver()` | Whether the heap is over LOAD_HEAP_SHARE of what the game may have, after a collection if it looked so. |
| 5625 | 1 | `boolean viewSettled()` | The view as it stands lacks nothing, outside a move: loadSettled() with nothing passed (ReplayMoveProbe's and ReplayCheck's wait for a still view). |
| 5631 | 1 | `boolean moveHeld()` | Whether a loaded move is held (CM1: ReplayMoveProbe.check(), which ReplayCheck 5d runs). |
| 5634 | 5 | `int cachesPinned()` | How many of the six picture caches are pinned (CM1: as moveHeld(); 6 while a move is loaded, 0 otherwise). |
| 5641 | 1 | `long framesDrawn()` | The frames tick() has drawn since this view was made (CM1: ReplayCheck 5d's "the normal game draws its map"). |
| 5652 | 7 | `private boolean loadSettled(MoveLoad m)` | Whether the view as it stands lacks nothing: no far node, district picture or chart node is asked of WORKER and not back (but those a passed view left stuck), and everything the draw would read is in and current - on ... |
| 5661 | 35 | `private boolean scanView(MoveLoad m, boolean record, int month)` | The cells of the view being loaded, as loadSettled() reads them; with {@code record} (the view settled, a move across months) each is copied into the month's store. |
| 5698 | 18 | `private boolean nodesHold(MoveLoad m, long plots, long nx0, long nx1, long ny0, long ny1, MapFrame asker, boolean record, int m...` | The far nodes nx0 to nx1 and ny0 to ny1 at `plots` a pixel all coloured and current (and, with `record`, copied into the month's store); `asker` is the frame the pictures they lack are asked for by (paintNodes()). |
| 5717 | 1 | `private int cacheIndex(Pictures p)` |  |
| 5720 | 19 | `private void storeTile(MoveLoad m, int month, int cache, long key, Img img)` | A tile's picture kept as the month showed it: one copy for each stamp the tile was painted at, whatever month showed it. |
| 5741 | 19 | `private void storeNode(MoveLoad m, int month, long key, Img img)` | A far node kept as the month showed it: one copy for each stamp (and partial or not) the node was coloured at. |
| 5762 | 12 | `private boolean takeTile(Img img, int cacheIndex, long key)` | A stale tile picture given the month's copy from the store, current against the map; false where the store has none for it (the month on screen is the camera's). |
| 5776 | 18 | `private boolean takeNode(Img img, long key, long picturesKept)` | ...and a stale far node given the month's colours; on a miss its image and colours are let go (they may be the store's, not to be painted over) and the node is coloured the normal way. |
| 5799 | 12 | `private boolean drawLoading()` | The map while a move loads: nothing but the words (ReplayCamera.overlay()) on the plain ground - the walk jumps through the move's views and what it paints is not for the eye. |
| 5813 | 6 | `private void drawReady()` | The map as usual, and over it the words of a loaded move waiting for its click ("Ready - click to run"). |
| 5821 | 19 | `private void overWords(GraphicsContext gc, String words)` | Words in a box in the middle of the canvas. |
| 5842 | 9 | `private boolean replayRunsOnClick()` | A press on the map while a loaded move waits for it runs the move; one while it loads does nothing (the map under the words is not the eye's; Escape cancels); nothing outside a replay. |

### CM1: what a camera move shows unpainted (MoveTally) (lines 5852-6048)

| line | len | member | says |
|---:|---:|---|---|
| 5869 | 71 | **type** `static final class MoveTally` | What each frame of a replay's glide or fly showed that was not painted, counted (CM1, 2026-11-02): per frame the cells (a plane tile or far node, a globe plane node or chart node, a sampled screen point on the globe) ... |
| 5888 | 6 | `void reset()` _(in MapView.MoveTally)_ | Back to nothing counted: a move is about to begin. |
| 5896 | 24 | `void frame(long now, String where, long blackCells, long coarseCells, long staleCells, boolean half, long asked, long letGo)` _(in MapView.MoveTally)_ | One frame taken at {@code now} (System.nanoTime()): what it drew from, what it showed unpainted, and what it cost the worker and the caches. |
| 5922 | 17 | `String report()` _(in MapView.MoveTally)_ | The tally in a few lines: the totals, a line for each place the frames drew, and the frame rate. |
| 5944 | 1 | `MoveTally moveTally()` | The camera move tally (CM1), for ReplayMoveProbe: reset it before a move, read it after. |
| 5951 | 3 | `private long evictedNow()` | Every cache's evictions summed (Pictures.evicted). |
| 5963 | 85 | `private void tallyMove()` | The frame tick() has just painted and drawn, counted into the move tally (CM1): its cells looked up as the draw looked them up (drawTiles(), drawNodes(), drawGlobePart()), in the draw's order, so the caches' access or... |

