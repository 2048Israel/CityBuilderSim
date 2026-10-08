# MapView.java - 1,075 lines · 46 methods · 18 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> coloured on the FX thread. Nothing on WORKER touches a node of the scene.
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

**Uses:** [Palette](Palette.md) (46), [MapFrame](MapFrame.md) (44), [LandMap](LandMap.md) (27), [World](World.md) (13), [MapTiles](MapTiles.md) (12), [Game](Game.md) (8), [BuildingVisual](BuildingVisual.md) (8), [CityLand](CityLand.md) (7), [LandParcel](LandParcel.md) (7), [TilePainter](TilePainter.md) (6), [TileRaster](TileRaster.md) (4), [LandMarket](LandMarket.md) (4), [Resource](Resource.md) (4), [Deposit](Deposit.md) (3), [UserInterface](UserInterface.md) (2), [Icons](Icons.md) (2), [CityMap](CityMap.md) (1), [HistoryScreen](HistoryScreen.md) (1), [LandGrid](LandGrid.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (1):** [LandScreen](LandScreen.md)

## Sections

| line | section |
|---:|---|
| 138 | · what it draws from |
| 171 | · what the player is doing |
| 287 | A FRAME OF THE CLOCK (tick()) |
| 497 | THE DRAWING |
| 790 | THE POINTER AND THE KEYS |
| 925 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 92 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 95 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 98 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 101 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 104 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 107 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 110 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 116 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground. |
| 226 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 229 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag to pan, scroll to zoom, Esc to come back."` | The Expand button's tooltip. |
| 232 | `MapView.HINT` | `"drag to pan · scroll to zoom · 0 fits · Esc closes"` | The expanded map's hint, at the right of its head. |
| 561 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 564 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 567 | `MapView.EDGE_ALPHA` | `0.55` | The city's edge's white, its alpha: 0.55, as since 0.7.61. |
| 767 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 917 | `MapView.CARD_FIELDS` | `3` | The most fields a hover card lists: 3. |
| 1050 | `MapView.LEGEND_ROWS` | `11` | Rows a column of the legend holds. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 123 | `private final UserInterface ui` |  |
| 124 | `private final Picker picker` |  |
| 125 | `private final Runnable onClose` |  |
| 126 | `private final Canvas canvas` |  |
| 127 | `private final StackPane smallBox` |  |
| 128 | `private final Label readout` |  |
| 129 | `private final MapFrame small` |  |
| 130 | `private MapFrame big` |  |
| 131 | `private boolean expanded` |  |
| 133 | `private VBox bigPane` |  |
| 134 | `private StackPane bigBox` |  |
| 135 | `private VBox card` |  |
| 136 | `private Label clockLine, bigLine` |  |
| 140 | `private CityMap shownMap` |  |
| 141 | `private MapTiles tiles` |  |
| 142 | `private Game.MapDraft draft` |  |
| 143 | `private int draftsFailed` |  |
| 144 | `private long version` |  |
| 145 | `private long fittedFor` |  |
| 146 | `private final TilePainter.Input in` |  |
| 147 | `private int[] buffer` |  |
| 151 | `WritableImage image` |  |
| 152 | `long stamp, version` |  |
| 153 | `byte[] ground` |  |
| 158 | `long most` |  |
| 163 | `private final Pictures l0small` |  |
| 165 | `private final Set<Long> nodesAsked` |  |
| 168 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 169 | `private long fieldsFor` |  |
| 173 | `private int side` | the side the office shows, and the offer picked |
| 174 | `private int litSide` | the side the office shows, and the offer picked |
| 175 | `private int overSide` | a row hovered in the office |
| 176 | `private double pressX, pressY, lastX, lastY, moved` | the offer under the pointer |
| 177 | `private boolean pressed` |  |
| 178 | `private double[] hoverAt` |  |
| 180 | `private boolean running, dirty` |  |
| 181 | `private final AnimationTimer pump` |  |
| 185 | `private final ImagePattern hatch` |  |
| 657 | `private double[][] edge` | The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... |
| 658 | `private final Map<Integer, List<double[]>> ownedIn` |  |
| 659 | `private long shapesFor` |  |
| 662 | `private final Map<Integer, double[]> labelAt` | ...and the square each offer's number stands on, for those and the view's scale. |
| 663 | `private long labelsFor` |  |
| 664 | `private double labelsAtScale` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 990 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 113 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's place on it or -1. |
| 113 | 1 | `void picked(int side, int place)` _(in MapView.Picker)_ |  |

### what it draws from (lines 138-170)

| line | len | member | says |
|---:|---:|---|---|
| 150 | 5 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground. |
| 157 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 159 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 160 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 171-286)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 37 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 235 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 238 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 241 | 7 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 249 | 14 | `private void reset()` |  |
| 265 | 5 | `void refresh(int side, int place)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 272 | 6 | `void light(int side, int place)` | An offer lit from its row in the office (-1 for none). |
| 279 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 287-496)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 23 | `private void tick()` |  |
| 322 | 46 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 370 | 8 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 380 | 8 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 389 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 404 | 33 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - stamped again, and painted only when the stamp moved - from the middle of the view out; at FAR ... |
| 439 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 449 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 454 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 458 | 38 | `private boolean paintNodes(MapFrame f, long deadline)` |  |

### THE DRAWING (lines 497-789)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 13 | `private void draw()` |  |
| 515 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 545 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 585 | 70 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines, faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the offers as hatched rectangles - each its free ground only, the city's own ground... |
| 666 | 11 | `private void shapes(CityLand land, LandMarket market)` |  |
| 679 | 10 | `private void blockLines(GraphicsContext gc, MapFrame f, int level)` | The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. |
| 691 | 26 | `private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing)` | Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small... |
| 719 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 746 | 19 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 770 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 775 | 3 | `static String css(int argb)` | ...as CSS. |
| 780 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |

### THE POINTER AND THE KEYS (lines 790-924)

| line | len | member | says |
|---:|---:|---|---|
| 794 | 57 | `private void handlers()` |  |
| 853 | 11 | `private void click(double sx, double sy)` | A click: an offer's free ground picks it, anywhere else picks the side it lies on. |
| 866 | 49 | `private void hover(double sx, double sy)` | What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. |
| 920 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 925-1075)

| line | len | member | says |
|---:|---:|---|---|
| 929 | 14 | `void expand()` |  |
| 945 | 12 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked. |
| 958 | 37 | `private void buildPane()` |  |
| 997 | 7 | `private void picked()` | The picked side and offer on the expanded map's head. |
| 1006 | 18 | `private void fitPane()` | The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). |
| 1026 | 6 | `private void fitBig()` | The expanded map on the city, as the small map opens. |
| 1034 | 14 | `static List<String[]> legendEntries()` | The legend's entries: the ten classes and flats, the three roads, the land, the six resources in fields. |
| 1052 | 13 | `private static GridPane legend()` |  |
| 1067 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

