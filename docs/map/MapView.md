# MapView.java - 4,893 lines · 207 methods · 62 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (151), [MapFrame](MapFrame.md) (91), [LandMap](LandMap.md) (84), [NationsMap](NationsMap.md) (51), [GlobeFrame](GlobeFrame.md) (41), [Game](Game.md) (32), [World](World.md) (31), [CityLand](CityLand.md) (25), [LandParcel](LandParcel.md) (23), [CityMap](CityMap.md) (22), [TradeLanes](TradeLanes.md) (19), [MapTiles](MapTiles.md) (17), [GlobePicture](GlobePicture.md) (17), [LandMarket](LandMarket.md) (14), [ShipShapes](ShipShapes.md) (14), [Nations](Nations.md) (13), [TrainSchedule](TrainSchedule.md) (10), [TruckSchedule](TruckSchedule.md) (10), [TrainShapes](TrainShapes.md) (8), [BoatSchedule](BoatSchedule.md) (6), [TilePainter](TilePainter.md) (6), [Deposit](Deposit.md) (4), [HistoryScreen](HistoryScreen.md) (4), [TileRaster](TileRaster.md) (4), [TruckShapes](TruckShapes.md) (4), [LandGrid](LandGrid.md) (4), [UserInterface](UserInterface.md) (3), [Ports](Ports.md) (3), [EarthProj](EarthProj.md) (3), [WorldData](WorldData.md) (3)... and 12 more

**Used by (3):** [LandScreen](LandScreen.md), [MiniMap](MiniMap.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 300 | · what it draws from |
| 353 | · what the player is doing |
| 366 | · buying from the map (0.7.137, batch M6) |
| 394 | · the boats (0.7.97) |
| 418 | · the globe (0.7.135, batch E9) |
| 599 | A FRAME OF THE CLOCK (tick()) |
| 1005 | THE DRAWING |
| 1052 | THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) |
| 1137 | THE BOATS (0.7.97, batch O13) |
| 1231 | THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 1291 | · the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) |
| 1588 | · the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") |
| 1615 | · the ground no port links (ET5, slice D; the ET5 design, section 5) |
| 1999 | THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) |
| 2903 | · the keys (0.7.135; spec-globe 3.11) |
| 2944 | THE POINTER AND THE KEYS |
| 3280 | BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 3724 | THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) |
| 3959 | · the city's trade (WN3) |
| 4000 | · the chips |
| 4070 | · the legend |
| 4098 | · drawing |
| 4338 | · the pointer |
| 4379 | · the card |
| 4504 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 240 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 243 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 246 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 249 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 252 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 255 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 258 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 261 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 267 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock,... |
| 270 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 273 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 279 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), ... |
| 447 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 500 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 503 | `MapView.EXPAND_TIP` | `"The map under the window's header: drag along the city's edge to pick the ex...` | The Expand button's tooltip (since MS1 it says the drag picks; since BL1 the ground it picks is the player's own: a drag along the city's edge picks the exact ground, one from elsewhere pans). |
| 507 | `MapView.HINT` | `"drag along the city's edge to pick the exact ground · drag elsewhere to pan"` | The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), since MS1 by a click or a drag, since BL1 of the exact ground - blocks of the city's grid, the offers i... |
| 510 | `MapView.KEYS_TIP` | `"Click an offer to open its card beside it, or a free block along the city's ...` | ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. |
| 520 | `MapView.SELECT_EVERY` | `"Select every offer"` | The head's chip that picks every offer listed (B7). |
| 1463 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1466 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 1469 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1472 | `MapView.WORLD_LINE_ALPHA` | `LandMap.WORLD_ALPHA` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. |
| 1780 | `MapView.DASH_ON` | `5, DASH_PERIOD = 9` | A run's dashes: 5 px on and 4 off, as the offers' edge has been drawn since 0.7.61. |
| 1841 | `MapView.PICKED_ALPHA` | `0.22` | The tint of a picked offer's ground and of picked blocks: the building pink at 0.22 (the picked offer's since 0.7.137). |
| 1898 | `MapView.NEW_TAG` | `"NEW"` | The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. |
| 1965 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 2004 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe, by code - a class and its relief (ET1 slice D): the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 2007 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 2010 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 2013 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 2153 | `MapView.CHART_NODE_OFFSET` | `1L<<19` | A chart node's indexes run negative west and north of the chart's anchor: this many are added to each to key it. |
| 2512 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 2700 | `MapView.BLOCK_LINE_SAG_PX` | `0.25` | The most the camera may bend a block line from its chord before the line is split in two, in pixels: a quarter (the line is drawn a pixel wide, so a quarter is not seen). |
| 2703 | `MapView.BLOCK_LINE_SPLITS` | `6` | ...and how many times a line is split at most: 6 (64 pieces - far more than the 64 px a line bends by at the widest near scale 12,000 km from the city needs). |
| 2813 | `MapView.CLICK_TO_DROP` | `"Shift-click to drop it · Enter buys"` | The hover card's last line over a block that is picked already (BL1), in blue as the offers' is: how to drop it. |
| 3239 | `MapView.CLICK_TO_BUY` | `"Click to buy it here"` | The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. |
| 3272 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 3316 | `MapView.CARD_GAP` | `14` | The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). |
| 3319 | `MapView.CARD_MARGIN` | `10` | ...and at least this far inside the view: 10, the hover card's (showCard()). |
| 3322 | `MapView.BOX_FILL_ALPHA` | `0.12` | The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. |
| 3745 | `MapView.NATIONS_TIP` | `"A dot at each nation's largest port or city, by its output; its name, output...` | The Nations chip's tooltip (WN2). |
| 3749 | `MapView.BORDERS_TIP` | `"The nations' borders, dashed white, at the far views; up close only where on...` | ...and the Borders chip's. |
| 3752 | `MapView.TRADE_TIP` | `"This month's trade as routes from the city: violet what we sell, sand what w...` | ...and the Trade routes chip's (WN3). |
| 3756 | `MapView.TRADE_SELL_WORDS` | `"What we sell, a month", TRADE_BUY_WORDS = "What we buy"` | The legend's words for the two kinds of route (the board's, less its "it"). |
| 3759 | `MapView.TRADE_ALPHA` | `0.78` | A route's colours: the business violet for what we sell and the ore's sand for what we buy (Palette's), at the board's stroke opacity, .78. |
| 3760 | `MapView.TRADE_SELL` | `Color.web(Palette.BUSINESS, TRADE_ALPHA), TRADE_BUY = Color.web(Palette.ORE, ...` |  |
| 3763 | `MapView.ARROW_LEN_PX` | `34` | A generated world's arrow is this many pixels long from its tip inward... |
| 3766 | `MapView.ARROW_HEAD_PX` | `9, ARROW_HEAD_WIDE_PX = 3` | ...with a head this many pixels long, and this much wider than the shaft either side. |
| 3769 | `MapView.NO_FIGURES` | `new double [ 0 ]` | A stand-in for a feed that gave no figures. |
| 3772 | `MapView.LABEL_NAME_PX` | `11` | A dot's label, the name: sans semibold, 11 px (the board's .lbl)... |
| 3775 | `MapView.LABEL_FIGURES_PX` | `9.5` | ...and the figures under it, mono, 9.5 px (the board's .lbl2). |
| 3778 | `MapView.DOT_EDGE_PX` | `1.2` | A dot's edge, in pixels: 1.2, in NationsMap.DOT_EDGE_ARGB (the board's). |
| 3781 | `MapView.HALO_PX` | `3, HALO_ALPHA = 0.75` | A label's halo, drawn under its words: 3 px wide in NationsMap.DOT_EDGE_ARGB at 0.75 (the board's .lbl stroke). |
| 3784 | `MapView.WIDTHS_MOST` | `600` | The widths of label words kept, by string (the names never change and the figures change once a year): the 600 most recently used. |
| 3787 | `MapView.NATION_CARD_W` | `330` | A nation's card is this wide, in pixels: 330 (the board's .card)... |
| 3790 | `MapView.NATION_CARD_GAP` | `4` | ...and its head, its distance line and its rows stand this many pixels apart: 4. |
| 4725 | `MapView.LEGEND_ROWS` | `17` | Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 the transport class, t... |
| 4728 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 4731 | `MapView.TRUCK_WORDS` | `"Trucks: one per %s"` | The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). |
| 4739 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 4757 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |
| 4812 | `MapView.LEGEND_FOLD_WORDS` | `"· click to fold"` | The head line's words when the legend is open, after its chevron and "Legend" (the board's .lh). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 264 | `static Game.MapDraft drafting` | The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. |
| 286 | `private final UserInterface ui` |  |
| 287 | `private final Picker picker` |  |
| 288 | `private final Runnable onClose` |  |
| 289 | `private final Canvas canvas` |  |
| 290 | `private final StackPane smallBox` |  |
| 291 | `private final Label readout` |  |
| 292 | `private final MapFrame small` |  |
| 293 | `private MapFrame big` |  |
| 294 | `private boolean expanded` |  |
| 296 | `private VBox bigPane` |  |
| 297 | `private StackPane bigBox` |  |
| 298 | `private VBox card` |  |
| 302 | `private CityMap shownMap` |  |
| 303 | `private MapTiles tiles` |  |
| 304 | `private Game.MapDraft draft` |  |
| 305 | `private int draftsFailed` |  |
| 306 | `private long version` |  |
| 307 | `private long fittedFor` |  |
| 309 | `private long farRecoloured` | How many far nodes have been coloured (batch UP3): a node whose version moved but whose key did not (MapTiles.nodeKey()) is not counted. |
| 310 | `private final TilePainter.Input in` |  |
| 311 | `private int[] buffer` |  |
| 315 | `WritableImage image` |  |
| 316 | `long stamp, version` |  |
| 318 | `long key` | A tile's key (MapTiles.tileKey(), batch UP5) at the moment `stamp` was taken, and whether it was: while the key holds the stamp does, and the tile's inputs are not read again. |
| 319 | `boolean keyed` |  |
| 320 | `byte[] ground` |  |
| 322 | `byte[] relief` | The node's relief beside its ground (ET1 slice D): null on a generated world, whose ground is all flat. |
| 323 | `boolean partial` |  |
| 324 | `long pictures` |  |
| 326 | `int[] pixels` | A far node's colours as drawn (0.7.135): what the globe reprojects near the city. |
| 331 | `long most` |  |
| 336 | `private final Pictures l0small` |  |
| 338 | `private final Set<Long> nodesAsked` |  |
| 344 | `private final Pictures charts` | The globe's chart nodes (MS3; GlobeFrame.chart()) kept as the plane's far nodes are - their colours (Img.pixels), classes and relief (for the hover) - and those asked of WORKER and not back. |
| 345 | `private final Set<Long> chartsAsked` |  |
| 347 | `private final Set<CityMap.District> picturesAsked` | The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. |
| 350 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 351 | `private long fieldsFor` |  |
| 355 | `private int side` | the side the office shows, and the offer picked |
| 356 | `private int litSide` | the side the office shows, and the offer picked |
| 357 | `private int overSide` | a row hovered in the office |
| 358 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 359 | `private boolean pressed` |  |
| 361 | `private javafx.scene.input.MouseButton pressButton` | The button the press was made with (MS1): only the primary clicks or draws a box; the others pan. |
| 362 | `private double[] hoverAt` |  |
| 364 | `private boolean hoverShift` | Whether Shift was held when the pointer last moved over the map: the cursor says a press would box from anywhere (MS1). |
| 369 | `private final LandMap.Picks picks` | The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a load and a collapse - and, since MS1, not on a month: each is keyed to its ground and lost only when the city ... |
| 371 | `private VBox buyBox` | The card in the pane (the land office's: LandScreen.mapCardNode(), mapReceiptNode()) and what it was drawn for - null for a receipt. |
| 372 | `private String cardFor` |  |
| 374 | `private List<double[]> rings` | The sites of the fields the card's offers hold, each its square of plots and its resource's colour, {x0, y0, x1, y1, argb}: ringed wherever they lie (board 7). |
| 376 | `private double[] cardAt` | The box of plots the card stands beside: the offer clicked, what a box took, the shelf's first for Ctrl+A, the ground just bought. |
| 378 | `private Bought bought` | A purchase from the card and its receipt: what is being bought - ids, places, the ground - shown by the ground once it is the city's. |
| 379 | `private boolean receiptShown` |  |
| 381 | `private boolean backFromFunding` | The card sent to the funding page (B5): the map comes back over the window at the view it left when the page is done. |
| 383 | `private boolean boxing` | A drag that boxes (B7; since MS1 a plain drag from an offer's free ground, or Shift and a drag from anywhere - LandMap.pressBoxes()): a box drawn from the press to (boxX, boxY), in the screen's pixels. |
| 384 | `private double boxX, boxY` |  |
| 386 | `private Label headMoney` | The head's money and its pay toggle (land's own, in the money the toggle pays in: the window's header, kept in view over this pane since MS1, says neither), what they were drawn for, and the note of what the picks los... |
| 387 | `private HBox headPay` |  |
| 388 | `private String headFor` |  |
| 389 | `private Label headNote` |  |
| 390 | `private javafx.scene.control.Tooltip headNoteTip` |  |
| 392 | `private boolean running, dirty` |  |
| 397 | `private CityMap.RoutesJob routesAsked` | The routes being found on WORKER, or null. |
| 399 | `private BoatSchedule schedule` | The schedule drawn from: its boat-month, the routes and the game month it was made on. |
| 400 | `private int scheduleMonth` |  |
| 401 | `private List<BoatSchedule.Route> scheduleRoutes` |  |
| 403 | `private TrainSchedule trainSchedule` | The trains' schedule drawn from (0.8.2, batch FR1): its boat-month, the routes and the game month it was made on. |
| 404 | `private int trainMonth` |  |
| 405 | `private List<TrainSchedule.Route> trainRoutes` |  |
| 407 | `private boolean boatsLive` | Whether boats - or since 0.8.2 trains, and since 0.8.8 trucks - are in view (the frame goes on), and the clock they were last drawn at. |
| 408 | `private double drawnClock` |  |
| 409 | `private final AnimationTimer pump` |  |
| 413 | `private final ImagePattern hatch` |  |
| 416 | `private final ImagePattern islandHatch` | The amber hatch over the city's ground that no port links (ET5): the offers' hatch drawn in the warning colour. |
| 421 | `private GlobeFrame globe` | The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. |
| 422 | `private World.Key globeKey` |  |
| 423 | `private EarthData globeData` |  |
| 425 | `private long reachFor` | The land and offers its reach was found for. |
| 428 | `private WritableImage globeImg` | The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. |
| 429 | `private int[] globePx` |  |
| 430 | `private GlobeFrame globeDrawn` |  |
| 431 | `private int globeMode` |  |
| 432 | `private long globeInputs` |  |
| 434 | `private long inputs` | Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. |
| 436 | `private double[] patch` | The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. |
| 439 | `private byte[] pieceCls` | The GROUND tier's pieces past the plane, for one view at rest: its codes - class and relief (GlobePicture.code()) - a pixel (-1 not in yet), the view and its number. |
| 440 | `private GlobeFrame pieceView` |  |
| 441 | `private volatile int pieceGen` |  |
| 443 | `private int[] carryPx` | The last whole picture at rest, carried pixel by pixel while the next is drawn. |
| 444 | `private GlobeFrame carryView` |  |
| 450 | `private boolean keyW, keyA, keyS, keyD` | W, A, S and D held, and when the keys last moved the view (0 when none is held). |
| 451 | `private long keyNanos` |  |
| 454 | `private int legendTier` | The tier the legend was laid out for, and whether with the relief's entries (LandMap.reliefDrawn()). |
| 455 | `private boolean legendRelief` |  |
| 961 | `private volatile long[] nodesWanted` | The far nodes the view being painted wants - {plots a pixel, nx0, nx1, ny0, ny1} - which WORKER's queue reads from its own thread (MS2). |
| 1057 | `private javafx.scene.text.Font worldLabelFont` | The world line's label as measured: its font, its width (and the shared label's) and its height above the baseline, kept until the font changes (FX2, FX5). |
| 1058 | `private double worldLabelW, worldLabelBothW, worldLabelH` |  |
| 1297 | `private final Map<Long, DistrictTrucks> districtTrucks` | The districts' schedules by CityMap.DistrictTrips.key(): thrown out with the shares when the month, the game month or the trips turn. |
| 1300 | `private int truckMonth` | The boat-month and the game month the schedules were made on, and the trips (CityMap.tripsDrawn()) they were made against. |
| 1301 | `private List<CityMap.DistrictTrips> truckTrips` |  |
| 1304 | `private double[][] truckPerBuilding` | What one building of each type carries in the month billed and the city's truck tonnes by load (TruckSchedule.perBuilding(), Freight.truckedIn() and the others): what a district's share is made of, kept with the sched... |
| 1305 | `private double[] truckCityTonnes` |  |
| 1307 | `private Map<Long, double[]> truckShares` | ...and each district with trips' catchment's shares of them by CityMap.DistrictTrips.key() (TruckSchedule.sharesByTrips()): its own works' tonnes and those of the districts about it that have no trips. |
| 1310 | `private int trucksStand` | Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the largest of the districts' drawn, which is the city's N. |
| 1579 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 1580 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 1581 | `private long shapesFor` |  |
| 1584 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 1585 | `private long labelsFor` |  |
| 1586 | `private double labelsAtScale` |  |
| 1591 | `private List<CityMap.Tinted> tints` | The zones' districts as last worked out (CityMap.zoneTint()), and what from: the map, its changes and the zones. |
| 1592 | `private CityMap tintsMap` |  |
| 1593 | `private long tintsAt` |  |
| 1594 | `private String tintsKey` |  |
| 1618 | `private List<long[]> islandRuns` | The runs of owned dry ground on landmasses no port links, as LandMap.unlinkedRuns() gave them, the chips beside them, and what they were worked out for: the linked set's version and the land's stamp. |
| 1619 | `private List<LandMap.Chip> islandChips` |  |
| 1620 | `private long islandFor` |  |
| 1726 | `private final Map<Integer, List<double[]>> freeCells` | Each offer's free ground as rectangles of plots (LandMap.freeCells()) and its edge as runs (LandMap.freeOutline()), by offer id, for the land and listing above: found with the holes, not every frame. |
| 1727 | `private final Map<Integer, double[][]> freeEdge` |  |
| 1730 | `private java.util.Set<Long> groundFor` | The picked blocks' free ground as cells and its edge as runs (LandMap.freeCells(), freeOutline() of the blocks): worked out again only when the pick's blocks (the set picks.blocks() hands out is a new one at every cha... |
| 1731 | `private long groundShapesFor` |  |
| 1732 | `private List<double[]> groundCells` |  |
| 1733 | `private double[][] groundEdge` |  |
| 1736 | `private String previewFor` | ...and the blocks a box being drawn would take: its cells and edge, for the box of plots, the pick and the land it was found for. |
| 1737 | `private List<double[]> previewCells` |  |
| 1738 | `private double[][] previewEdge` |  |
| 2016 | `private boolean globeHalf` | The globe drawn while it was half its resolution (the view on the move). |
| 2114 | `private GlobeFrame globeNodesFor` |  |
| 2115 | `private long[] globeNodesAt` |  |
| 2149 | `private GlobeFrame chartNodesFor` |  |
| 2150 | `private long[] chartNodesAt` |  |
| 2161 | `private volatile long[] chartsWanted` | The chart nodes WORKER's queue reads from its own thread (chartNodes()'s range for the view being painted): a node asked for a view that has moved on is dropped unread. |
| 2164 | `private int chartGen` | The globe's generation as far as chart nodes go: moved by resetGlobe(), so a node that comes back from WORKER after it is dropped. |
| 2669 | `private final Map<Integer, double[]> globeLabelAt` | The squares the globe's numbers stand on, by offer id, for the land they were found for and the scale (their last element). |
| 2670 | `private long globeLabelsFor` |  |
| 2859 | `private String quotedFor` | The block the hover last quoted (LandMarket.quote()) and what it was for: the block, the city's level, its ground and the month. |
| 2860 | `private LandParcel quoted` |  |
| 3797 | `private static volatile CompletableFuture<NationsMap> nationsBuild` | The nations' map, built once for the process on WORKER from the world file, which is kept open for the game's life - the map's exact ownerAt() reads it, so it is never closed. |
| 3830 | `private Nations nations` | The nations' figures of the game on the screen (Game.getNations(), asked on WORKER: its first build is the path to 2100), and the game they are of; and the game they were asked for. |
| 3831 | `private Game nationsOf, nationsAskedOf` |  |
| 3833 | `private boolean nationsWaiting` | Whether this view waits for the map's build to come back (it draws again when it does). |
| 3836 | `private List<NationsMap.Mark> marks` | The dots and labels of the last view drawn, and what they were made for: the view (a copy), the year, the figures and the map. |
| 3837 | `private GlobeFrame marksView` |  |
| 3838 | `private int marksYear` |  |
| 3839 | `private Nations marksFrom` |  |
| 3840 | `private NationsMap marksOf` |  |
| 3843 | `private javafx.scene.text.Font nameFont, figuresFont` | The label fonts, and the width of words in them kept by string (the names never change, the figures once a year). |
| 3844 | `private final javafx.scene.text.Text measure` |  |
| 3845 | `private final Map<String, Double> nameWidths` |  |
| 3846 | `private final java.util.function.ToDoubleFunction<String> nameW` |  |
| 3850 | `private EarthProj planeProj` | The plane's projection round the city's spot, kept per world (the borders that cross the city's ground are found on it). |
| 3851 | `private World.Key planeProjKey` |  |
| 3854 | `private TradeLanes lanes` | The city's trade as the screen holds it (WN3): the lanes of the last figures and the map they were made for, a generated world's arrows. |
| 3855 | `private NationsMap lanesOf` |  |
| 3856 | `private TradeLanes.Arrows arrows` |  |
| 3859 | `private Label nationsChip, bordersChip, tradeChip` | The head's chips and their group, and what they were last styled for. |
| 3860 | `private HBox nationsGroup` |  |
| 3861 | `private String chipsFor` |  |
| 3864 | `private VBox nationBox` | A nation's card in the pane, the nation it is of (0 for none), what it was drawn for, and the point on the screen it stands beside. |
| 3865 | `private int nationShown` |  |
| 3866 | `private String nationKey` |  |
| 3867 | `private double nationX, nationY` |  |
| 4759 | `private final Label legendNote` |  |
| 4761 | `private String legendZones` | The zones the legend was laid out with (0.8.0): their words, joined. |
| 4762 | `private final GridPane legendGrid` |  |
| 4763 | `private final Label shipsLine` |  |
| 4765 | `private final Label trucksLine` | The legend's trucks line: shown while trucks are drawn this frame (legendTrucks()). |
| 4807 | `private HBox legendHead` | The legend's head line, its chevron and its "click to fold" (WN2), and its body. |
| 4808 | `private VBox legendBody` |  |
| 4809 | `private final Label legendChevron` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 237 | 4657 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 276 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 276 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 300-352)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 14 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count ... |
| 330 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 332 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 333 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 353-365)

### buying from the map (0.7.137, batch M6) (lines 366-393)

### the boats (0.7.97) (lines 394-417)

### the globe (0.7.135, batch E9) (lines 418-598)

| line | len | member | says |
|---:|---:|---|---|
| 457 | 41 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 523 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 526 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 529 | 16 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 546 | 29 | `private void reset()` |  |
| 577 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 584 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 591 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 599-1004)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 46 | `private void tick()` |  |
| 659 | 54 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 715 | 9 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 726 | 9 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 736 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 752 | 50 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - keyed (MapTiles.tileKey(), batch UP5), stamped again only if the key moved, and painted only wh... |
| 804 | 17 | `private void ask(long tx, long ty)` | A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. |
| 823 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 833 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 838 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 842 | 8 | `private boolean paintNodes(MapFrame f, long deadline)` |  |
| 861 | 30 | `private void askPictures(MapFrame f)` | The district pictures the far nodes drawn this frame lacked (0.7.128, batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order, while the view's districts fit wha... |
| 902 | 57 | `private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept, MapFrame asker)` | The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). |
| 970 | 31 | `private void askNodes(List<long[]> asking, long plots, long span, World.Key world, long[] wanted)` | The far nodes a call of paintNodes() found it lacked, each {nx, ny, key}, their ground asked of WORKER from the middle of `wanted` out (MS2). |
| 1003 | 1 | `long farRecoloured()` | How many far nodes this view has coloured (batch UP3): the figure a probe reads against the nodes in view. |

### THE DRAWING (lines 1005-1051)

| line | len | member | says |
|---:|---:|---|---|
| 1009 | 42 | `private void draw()` |  |

### THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13) (lines 1052-1136)

| line | len | member | says |
|---:|---:|---|---|
| 1072 | 40 | `private void worldLines(GraphicsContext gc, MapFrame f)` | The highway's and the main line's faded runs past the city's ground (CityMap.worldLines()): each from its point along its way out over dry ground, as far as the land runs (CityMap.WorldLine.reach(), at most CityMap.WO... |
| 1114 | 22 | `private static double[] rayRect(double x, double y, double dx, double dy, double tMax, double w, double h)` | The part of the segment (x, y) + t (dx, dy), 0 <= t <= tMax, that lies on the canvas, {x0, y0, x1, y1}; null when it misses it. |

### THE BOATS (0.7.97, batch O13) (lines 1137-1230)

| line | len | member | says |
|---:|---:|---|---|
| 1142 | 3 | `private double boatClock()` | The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. |
| 1159 | 71 | `private void boats(GraphicsContext gc, MapFrame f, int level)` | The lanes and the boats over the tiles: the lanes on screen as a band and dashes (from L2 in), and from L1 in the boats the month's schedule puts on them at the clock - each one's hull, deck and bridge (ShipShapes), a... |

### THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 1231-1290)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 46 | `private void trains(GraphicsContext gc, MapFrame f, int level)` | The trains over the tiles, from L1 in, as the boats: the month's schedule (TrainSchedule, from the railway's tonnes by kind) on the map's routes (CityMap.trainRoutes()) at the boats' clock. |

### the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) (lines 1291-1587)

| line | len | member | says |
|---:|---:|---|---|
| 1294 | 1 | **type** `private record DistrictTrucks(List<TruckSchedule.Route> trips, TruckSchedule schedule)` | A district's trucks' schedule, with the trips it was made on: made again when its district's trips are another list. |
| 1322 | 37 | `private void trucks(GraphicsContext gc, MapFrame f, int level)` | The trucks over the tiles, at the near view alone and while the setting is on (TruckSchedule.drawnAt()): each district's trips (CityMap.tripsDrawn(), a truck's whole way from the world line to a works' gate and back, ... |
| 1361 | 7 | `private int fillTrucks(GraphicsContext gc, MapFrame f, TruckSchedule schedule, long x0, long y0, long x1, long y1, double t)` | One schedule's trucks in the box filled in their colours, or nothing when it has none or no route of it crosses the view; the loads a truck of it stands for, or 0 when none was drawn. |
| 1370 | 4 | `private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c)` | A shape in plots filled on the screen. |
| 1375 | 5 | `private static double[] screenXs(MapFrame f, double[] xs)` |  |
| 1381 | 5 | `private static double[] screenYs(MapFrame f, double[] ys)` |  |
| 1388 | 3 | `static Color colour(int argb, double alpha)` | A colour from the model's 0xAARRGGBB at an opacity. |
| 1393 | 23 | `private void askRoutes()` | The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. |
| 1417 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 1447 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 1494 | 83 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched shapes - each its free ground only, the city's own ground in ... |

### the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") (lines 1588-1614)

| line | len | member | says |
|---:|---:|---|---|
| 1597 | 17 | `private void zoneTints(GraphicsContext gc, MapFrame f, Game g)` | The city's zones over the ground, under the offers and the city's edge: each zone's districts in its sector's colour at LandMap.ZONE_ALPHA, the districts the map picks for it (CityMap's ZONES BY DISTRICT); nothing wit... |

### the ground no port links (ET5, slice D; the ET5 design, section 5) (lines 1615-1998)

| line | len | member | says |
|---:|---:|---|---|
| 1628 | 33 | `private void islandHatch(GraphicsContext gc, MapFrame f, Game g, CityLand land)` | The city's ground that no port links, hatched in amber over the ground (under the offers and the city's edge) and named on a chip at the middle of each landmass: "Island 2 · 12.0 km² · not linked: needs a port here (h... |
| 1663 | 22 | `private void islandChips(GraphicsContext gc, MapFrame f, Landmasses lms)` | Each such landmass's chip (LandmassView.chipWords()), mono caption on a dark stroke, as newTag() writes NEW: centred on the middle of its owned tiles, kept inside the view. |
| 1687 | 7 | `private List<String[]> islandEntries()` | The amber legend entry for that hatch (LandMap.unlinkedLegend()) while any landmass is unlinked, as the zones' entries are given: {css colour, words}. |
| 1696 | 10 | `private List<String[]> zoneEntries()` | The zones' legend entries on the plane (0.8.0): each zone set, by its sector's label. |
| 1707 | 17 | `private void shapes(CityLand land, LandMarket market)` |  |
| 1741 | 13 | `private void groundShapes(CityLand land)` | The picked blocks' shapes, kept (groundCells, groundEdge) for what is picked and the land it lies on. |
| 1759 | 19 | `private boolean previewShapes(CityLand land)` | The blocks the box being drawn would take, as cells and an edge (previewCells, previewEdge): worked out when the plots it covers, the pick or the land change, not every frame. |
| 1787 | 6 | `private static void edgeRuns(GraphicsContext gc, MapFrame f, double[][] runs, int width, boolean dashed, Color colour)` | Runs of an outline on whole pixels as one shape (BL1), `width` px deep inside the ground they bound - one the city's edge's, two the picked ground's - solid or dashed (DASH_ON on and the rest off, from each run's own ... |
| 1795 | 29 | `private static void runRects(GraphicsContext gc, MapFrame f, double x0, double y0, double x1, double y1, int width, boolean das...` | One run of an outline added to the path as its pixels' boxes (MapFrame.runOnScreen()), `width` deep inside the ground it bounds and cut into dashes when asked. |
| 1826 | 13 | `private void pickedGround(GraphicsContext gc, MapFrame f)` | The picked blocks (BL1): their free ground tinted as a picked offer's is and its edge solid in the offers' pink, two px deep, as one shape - the city's ground inside a block it owns part of left clear. |
| 1844 | 11 | `private void boxPreview(GraphicsContext gc, MapFrame f)` | The blocks a box being drawn would take, tinted under it and edged a pixel wide (BL1), so the snap to the block grid is seen before the release. |
| 1857 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 1869 | 27 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 1901 | 10 | `private void newTag(GraphicsContext gc, LandParcel p, double x, double y, double boxH)` | ...under the number at (x, y), where the offer's box on the screen is at least two numbers tall (MapFrame.PLACE_LABEL_FROM each). |
| 1913 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 1940 | 4 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 1946 | 17 | `private static void scaleBar(GraphicsContext gc, double h, double px, String words)` | ...drawn: `px` long at the bottom left of a view `h` high, and its words. |
| 1968 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 1973 | 3 | `static String css(int argb)` | ...as CSS. |
| 1978 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |
| 1989 | 9 | `private static ImagePattern islandHatchPattern()` | The hatch over the ground no port links (ET5): the offers' diagonals drawn in the warning amber (Palette.WARN, #e3b341) at the same weights. |

### THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3) (lines 1999-2902)

| line | len | member | says |
|---:|---:|---|---|
| 2019 | 1 | `private boolean onGlobe()` | Whether the expanded map draws the globe: on an Earth world, wider than the handoff - or, since MS1, closer than it with the view's centre past the city's reach, where the plane is not drawn (GlobeFrame.tier()). |
| 2022 | 1 | `private boolean globeShown()` | ...or lays its picture over the plane's in the crossfade's notch (GlobeFrame.crossfade(), the view's: whole where there is no plane under it). |
| 2025 | 13 | `private void resetGlobe()` | A new city, or another world: no globe until the map is over the window on Earth again - and no chart nodes (MS3): those in flight come back to another generation and are dropped. |
| 2040 | 14 | `private void resetGlobePicture()` | What the globe drew from and drew: another map, or another view of the world. |
| 2056 | 24 | `private void globeUp(CityLand land, LandMarket market, long landKey)` | The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. |
| 2082 | 4 | `private void follow()` | The view moved on the globe: where it draws the plane (the plane's scales, its centre within the reach) the plane's frame follows it - past the reach, closer than the handoff, there is no plane to follow (MS1); the vi... |
| 2088 | 25 | `private long[] globeNodes(GlobeFrame v)` | The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws (GlobeFrame.drawsHold(): since MS2 at the plane's scales the part of it the world's square hold... |
| 2124 | 24 | `private long[] chartNodes(GlobeFrame v)` | The chart nodes the globe draws for a view (MS3) - {plots a pixel, the chart's id, nx0, nx1, ny0, ny1} - over the part of it the view's chart draws (GlobeFrame.chartHolds(): at the plane's scales the ground past Globe... |
| 2156 | 3 | `private static long chartKey(long id, long plots, long nx, long ny)` | A chart node's key (MS3): the chart's id (GlobeFrame.chartFor()'s row and column, 13 bits) in the top 20 bits, the stride's power of two in 4, and the node's two indexes in 20 bits each (a chart reaches some 400 nodes... |
| 2171 | 18 | `private boolean paintGlobe(long deadline)` | A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack; and the chart nodes past the... |
| 2197 | 52 | `private void askCharts(long[] r)` | The chart nodes `r` (chartNodes()) the cache lacks asked of WORKER, each once, from the middle of the range out (MS3; askNodes()'s rule for the plane's far nodes): WORKER makes the node's classes and relief (MapTiles.... |
| 2251 | 18 | `private void chartColoured(int id, long plots, long nx, long ny)` | A chart node come in: where the globe's still picture shows it, laid over again next frame - nodeColoured()'s rule for the plane's, the node's corners and mid-points from its chart (east and south of the anchor on its... |
| 2271 | 14 | `private void nodeColoured(long x0, long y0, long span)` | A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. |
| 2287 | 9 | `private void addPatch(double x0, double y0, double x1, double y1)` | The still picture's box to be drawn again, grown to take in another. |
| 2305 | 32 | `private void composeGlobe(int mode)` | The globe's picture of the view, drawn again when the view, the way it is drawn or what it is drawn from moved: at half resolution while the view moves (its frame a copy of half the size at twice the km a pixel), at i... |
| 2339 | 48 | `private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1)` | The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image: the plane's nodes and, past the hand-over at the plane's scales, the view's chart nodes... |
| 2389 | 18 | `private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic)` | Past the plane and the charts, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. |
| 2409 | 7 | `private void drawGlobeImage(GraphicsContext gc)` | The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). |
| 2429 | 39 | `private void askPieces(GlobeFrame view)` | The GROUND tier's pieces past the plane for a view come to rest: the far rule through the camera (GlobeFrame.groundCodes()) a PIECE_PX square at a time on WORKER, from the middle out, each one with some of its pixels ... |
| 2470 | 7 | `private void pieceIn(int gen, int[] q, byte[] b)` | A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. |
| 2479 | 31 | `private void drawGlobe()` | The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its block lines past the reach at the plane's scales, its edge through the camera in the PI... |
| 2515 | 10 | `private double[][] onGlobe(double[] xs, double[] ys)` | A shape of plots through the camera: its corners on the screen, or null where one is not seen. |
| 2534 | 84 | `private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market)` | The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - each offer its free ground (its cells, LandMap.freeCells(), the city's own ground ... |
| 2620 | 4 | `private double plotPx(double x, double y, long span)` | Pixels a plot spans on the globe where the plane's plot (x, y) is: the camera's step over `span` plots east of it over the span, 0 where either end is not seen. |
| 2626 | 15 | `private boolean cellsPath(GraphicsContext gc, List<double[]> cells)` | Cells of plots (rectangles {x0, y0, x1, y1}) as one path through the camera, each a quadrilateral of its four corners - a cell with a corner not seen left out, as boxPicks() does; false when none is in the path. |
| 2643 | 10 | `private void runsOnGlobe(GraphicsContext gc, double[][] runs, int width, boolean dashed, Color colour)` | The runs of an outline through the camera, a line each between its ends where both are seen: `width` px, solid or dashed as the plane's offers' edges are. |
| 2655 | 12 | `private double[] labelSquare(CityLand land, LandParcel p, double plotPx)` | Where an offer's number stands on the globe, the square of plots (LandMap.labelBlock()) found for the camera's scale - a number's PLACE_LABEL_FROM px in plots, to a power of two - and kept until the land moves; null w... |
| 2681 | 17 | `private void globeBlockLines(GraphicsContext gc, int level)` | The city's block lines through the camera past the reach at the plane's scales (MS2): the plane draws them faint over its whole ground where a block is at least MapFrame.BLOCK_LINES_FROM px across (blockLines()), and ... |
| 2706 | 17 | `private boolean blockLine(GraphicsContext gc, double ax, double ay, double bx, double by, int depth)` | One block line from plot (ax, ay) to (bx, by) through the camera onto the path: its ends, and its middle's plot against the chord, in two where it strays more than BLOCK_LINE_SAG_PX. |
| 2725 | 4 | `static double distanceFromChord(double[] m, double[] a, double[] b)` | How far a point is from the chord a to b, in pixels. |
| 2731 | 12 | `private void globeEdge(GraphicsContext gc, CityLand land)` | The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. |
| 2745 | 11 | `private void ring(GraphicsContext gc, CityLand land)` | The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. |
| 2758 | 4 | `private void globeScaleBar(GraphicsContext gc)` | The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. |
| 2764 | 47 | `private void globeHover(double sx, double sy)` | The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it (drawnAt(): past the hand-over, MS3, the cha... |
| 2822 | 35 | `private List<String> groundLines(long x, long y)` | What the hover card says over free ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: over a block that may be picked, "Your ground: one block", its area by kind and its pric... |
| 2863 | 7 | `private static void groundInto(List<String> lines, List<String> ground)` | The ground lines in the hover card where "Not the city's" stood (after its first line when the card has not that line), their last line, the blue one, at the card's foot. |
| 2876 | 14 | `private byte drawnAt(double sx, double sy)` | The ground drawn at a screen point on the globe, past the plane, as a code - its class and relief (GlobePicture.code()): where the view's chart draws it (GlobeFrame.chartHolds(), MS3: at the plane's scales the ground ... |
| 2892 | 10 | `private byte chartCode(double sx, double sy)` | The code (GlobePicture.code()) of the pixel of the view's chart node under a screen point - the node ground() lays there (GlobeFrame.chartAt()) - or -1 where the node is not in yet. |

### the keys (0.7.135; spec-globe 3.11) (lines 2903-2943)

| line | len | member | says |
|---:|---:|---|---|
| 2906 | 23 | `private boolean keys()` | A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not h... |
| 2931 | 12 | `boolean panKey(KeyCode k, boolean down)` | W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. |

### THE POINTER AND THE KEYS (lines 2944-3279)

| line | len | member | says |
|---:|---:|---|---|
| 2948 | 88 | `private void handlers()` |  |
| 3038 | 6 | `private void zoomBig(double sx, double sy, double notches)` | The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. |
| 3050 | 4 | `private long[] plotAt(double sx, double sy)` | The plot of the city's plane under a screen point as the map draws it - the plane's own frame, or on the globe the camera's, where the plane is drawn (GlobeFrame.planeHolds()) - or null where there is none (the globe'... |
| 3056 | 9 | `private double[] plotXY(double sx, double sy)` | ...as the plane's own fractions of plots {x, y} (what a box's corners are read by: LandMap.plotBox()), or null where there is none. |
| 3071 | 12 | `private long[] boxPlots(double sx0, double sy0, double sx1, double sy1)` | The plots a box between two screen points covers (LandMap.plotBox()), its four corners read through plotXY() - the plane's frame, or on the globe the camera where the plane is drawn, a corner it does not hold left out... |
| 3090 | 6 | `private boolean pressBoxes(double sx, double sy, boolean shift)` | Whether a left press at a screen point draws a box rather than pans (MS1, widened by BL1): where the offers are drawn to be picked (canPick()), Picks.pressBoxes() of the plot under it - an offer's free ground, a picke... |
| 3106 | 37 | `private void click(double sx, double sy, boolean shift)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on - on the globe only where the city's plane is drawn (0.7.135). |
| 3151 | 23 | `private boolean blockClick(long x, long y, boolean shift)` | A click on ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: a free block that may be picked is picked alone, and with Shift toggled; a block already picked keeps the picks ... |
| 3176 | 49 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 3232 | 5 | `private void cursorFor(boolean boxHere)` | The cursor says what a left press would do (MS1, BL1): the crosshair where it would draw a box - over an offer's free ground or ground that is picked or may be (`boxHere`, LandMap.pressBoxes() without Shift), or anywh... |
| 3242 | 3 | `private static boolean isHint(String line)` | Whether a hover card's line is the blue last line telling the player what a click does. |
| 3247 | 23 | `private void showCard(List<String> lines, double sx, double sy, double vw, double vh)` | The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - the hint last (CLICK_TO_BUY, CLICK_TO_PICK, CLICK_TO_DROP, and a nation's NationsMap.CLICK_FOR_CARD) in the accent's light... |
| 3275 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 3280-3723)

| line | len | member | says |
|---:|---:|---|---|
| 3313 | 1 | **type** `record Bought(List<Integer> ids, List<int[]> places, String title, double[] at, double[][] edge)` | A purchase from the card: the offers and drafts found listed when it was asked for, their places ({side, place, 1 for a draft or 0} each - a draft's place is where its middle lies, which no "next ground" line is about... |
| 3325 | 3 | `private boolean canPick()` | Whether the offers are drawn to be picked where the expanded map looks (star M6): on the plane at every scale, and on Earth in the globe's GROUND tier, where globeOffers() draws them - not its PICTURE tier, which draw... |
| 3330 | 7 | `private LandMap.ToScreen toScreen()` | A plot of the city's plane on the screen, as the expanded map draws it now: through the globe's camera, or the plane's frame. |
| 3339 | 3 | `private static double[] box(LandParcel p)` | An offer's box in plots, {x0, y0, x1, y1}. |
| 3344 | 11 | `private static double[] bounds(List<LandParcel> ps)` | ...and the bounds of several, or null with none. |
| 3357 | 9 | `private List<LandParcel> listed(List<Integer> ids)` | The listed offers among these ids, in their order. |
| 3372 | 10 | `private void boxed(double x0, double y0, double x1, double y1)` | A drag let go (B7, MS1; BL1: a box takes ground, not offers): the blocks the box covers that join the city added to the picks (Picks.addBox(), the box's corners through plotXY(): the snap to the block grid the preview... |
| 3384 | 13 | `void selectAll()` | "Select every offer" in the head, or Ctrl+A (B7): every offer listed onto the card, the office's best first, the card beside the best (star M6). |
| 3399 | 24 | `private void showBuyCard()` | The card drawn again for the picks as they stand, or put away with none listed. |
| 3425 | 8 | `private void hideBuyCard()` | The card put away. |
| 3435 | 5 | `private String cardKey(List<Integer> ids)` | What the card was drawn from: the picks, the toggle, the cash and the vault, the month and the listing. |
| 3442 | 16 | `private void syncCard()` | A frame's look at the card (tick()): drawn again when what it says moved - the month dropping the picks, an offer bought elsewhere falling out of them, the cash - and the receipt put away with its month. |
| 3460 | 5 | `private void layoutBuyCard()` | The card sized to what it says, then placed. |
| 3467 | 16 | `private void placeBuyCard()` | The card beside its box of plots as the view moves (each draw): at its right, else at its left, its top level with the box's, inside the view. |
| 3485 | 15 | `private double[] screenBox(double[] b)` | A box of plots on the screen, {x0, y0, x1, y1} round its corners as drawn now, or null when none is seen. |
| 3502 | 16 | `private void drawRings(GraphicsContext gc)` | The card's fields' sites, ringed wherever they lie (board 7): each a ring in its resource's colour on a dark halo, as the city's ring is drawn (ring()), round its square - RING_PX at least. |
| 3520 | 11 | `private void drawBox(GraphicsContext gc)` | The box Shift and a drag draws (B7): the offers' pink, faint inside, dashed round. |
| 3533 | 7 | `void closeCard()` | The card's ×, Esc, a click off the offers: no picks, no card. |
| 3542 | 5 | `void dropPick(int id)` | A chip's × on a card of several: that one dropped. |
| 3549 | 5 | `void dropGround(List<Integer> drafts)` | The "Your ground" chip's ×: every draft of the picked blocks dropped, which drops the blocks they were cut from - one chip for all of them, as the card names them as one (blocks that joined through them go too, said i... |
| 3556 | 5 | `void paidOtherwise()` | The toggle moved, on the card or the head: both drawn again in the other money. |
| 3563 | 12 | `private Bought boughtOf(List<Integer> ids)` | What a purchase from the card is of, asked for now. |
| 3577 | 9 | `void buy(List<Integer> ids)` | The card's Buy, or Enter (B2): the offers bought as the office's Buy buys them (LandScreen.buyOrFund()) - the office drawn again under the map, which stays - and the receipt by the ground; short after all, the funding... |
| 3588 | 9 | `void buyOnLoan(List<Integer> ids, Runnable issue)` | The card's bond or note (B1): issued as the funding page issues it, the offers bought on it (LandScreen.offers()' own action), and the receipt; what falls short shows the office's page, which comes back here. |
| 3599 | 10 | `void toFunding(List<Integer> ids)` | "All the ways to pay" (B5): the funding page for the picks, the map put away until the page is done and brought back at the view it left (back()). |
| 3611 | 6 | `void toOffice(int s, int p)` | "Open in the land office": the map put away on the offer's row, the office's side and pick. |
| 3625 | 11 | `void back(boolean fromFunding)` | The office drawn (LandScreen.showLandMenu()), `fromFunding` when the funding page or what it fell short with is what it comes back from: a purchase the card sent there brings the map back over the window at the view i... |
| 3638 | 27 | `private void showBought()` | The receipt by the ground just bought (board 6): the card's purchase, those of it no longer listed - "2 of 3 offers" when it fell short - in the office's words (LandScreen.mapReceiptNode()); with none bought, the card... |
| 3673 | 26 | `boolean cardKey(javafx.scene.input.KeyEvent e)` | Esc, Enter and Ctrl+A while the map is over the window, from UserInterface's key filter (0.7.137): Esc closes the card - a second Esc, not taken here, the map - Enter presses the card's main button (B2: Buy, or short ... |
| 3706 | 17 | `private void syncHead()` | The head's money, pay toggle and note, drawn again when the toggle, the cash, the vault, the month or the note moved. |

### THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8) (lines 3724-3958)

| line | len | member | says |
|---:|---:|---|---|
| 3800 | 4 | `private static synchronized CompletableFuture<NationsMap> askNationsBuild()` | The build asked of WORKER the first time it is wanted. |
| 3806 | 22 | `private static NationsMap buildNations()` | The world file opened and the map made of it, on WORKER; null (and a line in the log) where there is no file or it will not open. |
| 3870 | 5 | `private static Map<String, Double> widths()` | A bounded map of widths by string, the most recently used kept. |
| 3877 | 10 | `private double widthOf(String words, javafx.scene.text.Font font, Map<String, Double> kept)` | The width of words in a font, kept: a Text measured once per string. |
| 3889 | 10 | `private void forgetNations()` | Another city or another game: the nations of the last one are not this one's. |
| 3906 | 35 | `private void nationsUp(CityLand land)` | Asked every frame (keepUp()): on an Earth world, once a chip is on or the map is over the window - the chips are shown only once the map is built, so a player who turned both off can turn them on - the nations' map is... |
| 3943 | 6 | `private NationsMap nationsMap()` | The nations' map when it is built and this city stands on the real Earth; null on a generated world, before the build and where the world file is not here. |
| 3951 | 1 | `private Nations nationsNow()` | The game's nations when they are in for the game on the screen, else null. |
| 3954 | 4 | `private NationsMap builtMap()` | The nations' map when it is built, whatever world this is: a generated world's arrows stand by its nodes though it draws no nations. |

### the city's trade (WN3) (lines 3959-3999)

| line | len | member | says |
|---:|---:|---|---|
| 3966 | 4 | `private TradeLanes.Feed tradeFeed()` | The city's trade by partner - THE ONE SEAM the wiring filled (WN3): the game's feed over last month's trade by partner and the year to date (Game.getTradeFeed(), an immutable snapshot published through a volatile, so ... |
| 3972 | 6 | `private boolean standsIn()` | Whether this is a generated world whose feed names the place the city stands in, so its partners can be drawn as arrows. |
| 3980 | 9 | `private TradeLanes lanesNow(NationsMap nm)` | The lanes of the feed's figures now: the ones kept while the month, the city, the map and every figure are the same, else made again (a fraction of a millisecond; never in a frame that has them). |
| 3991 | 8 | `private TradeLanes.Arrows arrowsNow()` | A generated world's arrows of the feed's figures now, kept the same way; null where the map is not built or the feed names no stand-in. |

### the chips (lines 4000-4069)

| line | len | member | says |
|---:|---:|---|---|
| 4003 | 16 | `private Label layerChip(String words, String tip, Runnable flip)` | A toggle chip of the head: a dot before its word, lit or not (styleChip()); a click runs `flip`. |
| 4021 | 5 | `private static void styleChip(Label chip, boolean lit)` | The board's .chip and .chip.lit: lit, the pinned ground, the accent's edge and dot and the full text; not, the panel, the edge, the label's text and the grey dot. |
| 4031 | 15 | `private void syncChips()` | The chips shown only on an Earth world whose nations' map is built, and lit as the settings stand (each frame of the head). |
| 4047 | 4 | `private void flipNations()` |  |
| 4052 | 4 | `private void flipBorders()` |  |
| 4057 | 4 | `private void flipTrade()` |  |
| 4063 | 6 | `private void layersFlipped()` | A chip flipped: saved at once (the settings are the player's, beside the saves), the card put away if its nations are off, the chips restyled and the view drawn again - which lays the legend out again (legendFor() key... |

### the legend (lines 4070-4097)

| line | len | member | says |
|---:|---:|---|---|
| 4077 | 20 | `private List<String[]> nationsEntries()` | The legend's nations entries - {colour, words} - on an Earth world with the map built: the border's white while Borders is on, the dot's colour while Nations is on and the view is a globe's, and (WN3) the violet and t... |

### drawing (lines 4098-4337)

| line | len | member | says |
|---:|---:|---|---|
| 4101 | 19 | `private static void strokeRuns(GraphicsContext gc, NationsMap.Runs r)` | Runs of border stroked as ONE path - the runs are many and short, about 6,000 on the planet - 1 px, dashed NationsMap.BORDER_DASH_PX, white at BORDER_ALPHA; the width and dashes put back. |
| 4127 | 31 | `private void tradeLanes(GraphicsContext gc, NationsMap nm)` | The city's trade lanes on the globe, between the borders and the dots (WN3, board 1): TradeLanes.runs() of the view - the edges in view, each shared lane once - stroked a path to each colour and whole width (at most T... |
| 4164 | 35 | `private void tradeArrows(GraphicsContext gc)` | A generated world's partners as arrows at the edge of its map (WN3, the study 6.4): the biggest TradeLanes.ARROWS_MAX over the floor, each at the edge on its bearing from the stand-in place the feed names - a violet a... |
| 4204 | 18 | `private static void arrow(GraphicsContext gc, double tipX, double tipY, double ux, double uy, double width, double off, boolean...` | One arrow of the edge: a shaft ARROW_LEN_PX long inward from its tip, a head at the outer end if it points out (what we sell) and at the inner end if it points in (what we buy), the whole moved `off` pixels sideways (... |
| 4224 | 8 | `private void globeNations(GraphicsContext gc)` | The globe's nations (after its picture, under the city's own drawing): the borders with Borders on, the trade's lanes with Trade routes on, the dots and labels with Nations on - each only when the map is built, and th... |
| 4237 | 19 | `private List<NationsMap.Mark> nationMarks(NationsMap nm, Nations ns)` | The dots and labels of the globe's view now (NationsMap.marks()), made again only when the view is not the one they were made for (GlobeFrame.sameView()), the year moved, the figures or the map are another's, or a fon... |
| 4258 | 35 | `private void drawMarks(GraphicsContext gc, List<NationsMap.Mark> list)` | The dots (NationsMap.DOT_ARGB at DOT_ALPHA, edged), then the labels of those that fit: the name as the board's .lbl and the figures as its .lbl2, centred, each on its halo. |
| 4300 | 15 | `private void planeBorders(GraphicsContext gc, MapFrame f)` | The borders on the city's plane (the plane's tier on Earth, and the land office's small map), after the tiles and what runs over them and before the world lines and the overlays: only the lines that cross the box of p... |
| 4317 | 20 | `private static double[] crossingBox(CityLand land, LandMarket market)` | The box of plots {x0, y0, x1, y1} of the city's land and its listed offers, as the reach is found from them (GlobeFrame.reachKm()); null with neither. |

### the pointer (lines 4338-4378)

| line | len | member | says |
|---:|---:|---|---|
| 4345 | 8 | `private List<String> nationHoverWords(double sx, double sy)` | What hovering a dot says (NationsMap.hoverWords(), its last line the blue CLICK_FOR_CARD), or null where the pointer is over no dot, Nations is off, or the city's plane is drawn under it: there the land office's hover... |
| 4355 | 6 | `private String ownerWords(double sx, double sy)` | Whose land is under the pointer, where the city's plane is not drawn: a nation's or an area's name (NationsMap.ownerName() of the exact ownerAt()); null elsewhere or with Nations off. |
| 4368 | 10 | `private boolean nationClick(double sx, double sy)` | A click on the expanded globe with Nations on, where the city's plane is not drawn under the pointer (plotAt() is null): on a dot or on a nation's land (NationsMap.nationAt(): the dot, else the exact owner) opens that... |

### the card (lines 4379-4503)

| line | len | member | says |
|---:|---:|---|---|
| 4382 | 7 | `private void showNationCard(int nation, double sx, double sy)` | A nation's card opened beside the point clicked: the buying card (and its picks) goes, as there is only ever one. |
| 4391 | 1 | `private String nationCardKey()` | What the card was drawn for: the nation and the month, which moves the year and the D$ it is struck in. |
| 4394 | 76 | `private void fillNationCard()` | The card made (again) for the nation shown, from NationsMap.card(): its head - the name, the region as a tag, a "×" - the distance under it, and its rows in a grid of three columns. |
| 4472 | 12 | `private void placeNationCard()` | The card beside the point clicked, as the buying card stands beside its offer: CARD_GAP off it, at its right else its left, level with it and CARD_MARGIN at least inside the view. |
| 4486 | 7 | `private void hideNationCard()` | The card put away. |
| 4495 | 8 | `private void syncNationCard()` | A frame's look at the card (tick()): made again when the month moved - the year's figures and the D$ it is struck in - and put away if its nation is not shown any more (Nations off, another game). |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 4504-4893)

| line | len | member | says |
|---:|---:|---|---|
| 4508 | 1 | `void expand()` |  |
| 4511 | 25 | `private void expand(boolean keepView)` | ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. |
| 4543 | 11 | `void expandOn(MiniMapFrame.Box box)` | Expanded on a box of plots (0.7.134, batch MM2): the minimap's click, the map over the window fitted on what the stamp frames (MiniMapFrame.open()) rather than on the land office's opening; Fit goes back to that. |
| 4556 | 24 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked - the card's picks dropped (0.7.137), and its purchase forgotten unless the funding page has it (B5). |
| 4581 | 80 | `private void buildPane()` |  |
| 4664 | 22 | `private void fitPane()` | The canvas sized to the pane: the window's width, and its height under the header (MS1: the pane lies below it, ui.chartFullTop()), inside the padding and under the head (City History's rule, read off the window). |
| 4688 | 10 | `private void openBig()` | The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. |
| 4700 | 10 | `private void fitBig()` | Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. |
| 4712 | 1 | `static List<String[]> legendEntries(int tier)` | The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. |
| 4715 | 5 | `static List<String[]> legendEntries(int tier, boolean relief)` | ...on an Earth world with earth-2's layers (`relief`, LandMap.reliefDrawn(); ET1 slice D) also the three greys, forest on a slope and, on the plane, the shallow sea. |
| 4722 | 1 | `static List<String[]> legendEntries()` | ...the plane's, at every scale on a generated world. |
| 4734 | 3 | `static String trucksWords(int loads)` | ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. |
| 4742 | 3 | `static String packedWords(int packed)` | The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed at its edge. |
| 4747 | 3 | `static String surplusWords(long plots)` | ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. |
| 4752 | 3 | `static String trackWords(long plots)` | ...and (FR7 slice 3) when the model has track the railway's ways do not use - its main line, its sidings and its branches - how much, in plots; the model's words (LandMap.trackSurplusWords()). |
| 4767 | 38 | `private VBox legend()` |  |
| 4815 | 9 | `private void foldLegend()` | The legend folded or open, as the player left it (GamePrefs.isMapLegendFolded()): the body shown or not, the chevron pointing down or right, and the head's ground rounded all round when it stands alone. |
| 4826 | 31 | `private void legendFor(int tier)` | The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. |
| 4859 | 1 | `private int shownTier()` | The tier the legend and the hover are for (MS2): the globe's on Earth - the plane at every scale closer than the handoff, past the reach too, where the plane's own ground is what is drawn - and the plane at every scal... |
| 4862 | 5 | `private void legendTrucks()` | The trucks' line: how many loads a truck stands for while any is drawn this frame, on the plane; hidden otherwise. |
| 4869 | 14 | `private void legendNote()` | The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. |
| 4885 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

