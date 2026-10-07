# MapView.java - 964 lines · 43 methods · 16 constants · interface

`ham/citybuildersim/ui/MapView.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, the far nodes from the world's ground - with the city's edge, its forty offers hatched and its deposits over it, a scale bar, and what is under the pointer.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
> 2.8). Jerus: click a side to see its ten offers, the map always in view
> in the land office, an Expand that fills the window and pans and zooms.
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
> under the pointer and lights an offer's band). Expand moves the canvas
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

**Uses:** [Palette](Palette.md) (44), [MapFrame](MapFrame.md) (39), [LandMap](LandMap.md) (25), [World](World.md) (13), [MapTiles](MapTiles.md) (12), [Game](Game.md) (8), [BuildingVisual](BuildingVisual.md) (8), [TilePainter](TilePainter.md) (6), [CityLand](CityLand.md) (5), [TileRaster](TileRaster.md) (4), [Resource](Resource.md) (4), [Deposit](Deposit.md) (3), [LandMarket](LandMarket.md) (3), [UserInterface](UserInterface.md) (2), [Icons](Icons.md) (2), [LandParcel](LandParcel.md) (2), [CityMap](CityMap.md) (1), [HistoryScreen](HistoryScreen.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (1):** [LandScreen](LandScreen.md)

## Sections

| line | section |
|---:|---|
| 123 | · what it draws from |
| 156 | · what the player is doing |
| 268 | A FRAME OF THE CLOCK (tick()) |
| 478 | THE DRAWING |
| 679 | THE POINTER AND THE KEYS |
| 814 | EXPAND: THE MAP OVER THE WHOLE WINDOW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 77 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 80 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 83 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 86 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. |
| 89 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 92 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 95 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 101 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map")...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground. |
| 211 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 214 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag to pan, scroll to zoom, Esc to come back."` | The Expand button's tooltip. |
| 217 | `MapView.HINT` | `"drag to pan · scroll to zoom · 0 fits · Esc closes"` | The expanded map's hint, at the right of its head. |
| 542 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 656 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 806 | `MapView.CARD_FIELDS` | `3` | The most fields a hover card lists: 3. |
| 939 | `MapView.LEGEND_ROWS` | `11` | Rows a column of the legend holds. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 108 | `private final UserInterface ui` |  |
| 109 | `private final Picker picker` |  |
| 110 | `private final Runnable onClose` |  |
| 111 | `private final Canvas canvas` |  |
| 112 | `private final StackPane smallBox` |  |
| 113 | `private final Label readout` |  |
| 114 | `private final MapFrame small` |  |
| 115 | `private MapFrame big` |  |
| 116 | `private boolean expanded` |  |
| 118 | `private VBox bigPane` |  |
| 119 | `private StackPane bigBox` |  |
| 120 | `private VBox card` |  |
| 121 | `private Label clockLine, bigLine` |  |
| 125 | `private CityMap shownMap` |  |
| 126 | `private MapTiles tiles` |  |
| 127 | `private Game.MapDraft draft` |  |
| 128 | `private int draftsFailed` |  |
| 129 | `private long version` |  |
| 130 | `private long fittedFor` |  |
| 131 | `private final TilePainter.Input in` |  |
| 132 | `private int[] buffer` |  |
| 136 | `WritableImage image` |  |
| 137 | `long stamp, version` |  |
| 138 | `byte[] ground` |  |
| 143 | `long most` |  |
| 148 | `private final Pictures l0small` |  |
| 150 | `private final Set<Long> nodesAsked` |  |
| 153 | `private List<Deposit> fields` | The deposits the far views mark, and the land they were found for. |
| 154 | `private long fieldsFor` |  |
| 158 | `private int side` | the side the office shows, and the offer picked |
| 159 | `private int litSide` | the side the office shows, and the offer picked |
| 160 | `private int overSide` | a row hovered in the office |
| 161 | `private double pressX, pressY, lastX, lastY, moved` | the band under the pointer |
| 162 | `private boolean pressed` |  |
| 163 | `private double[] hoverAt` |  |
| 165 | `private boolean running, dirty` |  |
| 166 | `private final AnimationTimer pump` |  |
| 170 | `private final ImagePattern hatch` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 894 | **type** `final class MapView` | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, th... |
| 98 | 1 | **type** `interface Picker` | Who hears a click: a side, and an offer's lane on it or -1. |
| 98 | 1 | `void picked(int side, int lane)` _(in MapView.Picker)_ |  |

### what it draws from (lines 123-155)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 5 | **type** `private static final class Img` | A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground. |
| 142 | 5 | **type** `private static final class Pictures extends LinkedHashMap<Long, Img>` | A level's pictures, the most recently drawn kept: `most` of them. |
| 144 | 1 | `Pictures()` _(in MapView.Pictures)_ |  |
| 145 | 1 | `protected boolean removeEldestEntry(Map.Entry<Long, Img> e)` _(in MapView.Pictures)_ |  |

### what the player is doing (lines 156-267)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 37 | `MapView(UserInterface ui, Picker picker, Runnable onClose)` |  |
| 220 | 1 | `Region smallNode()` | The small map, its Expand button and its readout: what the land office lays in its hero row. |
| 223 | 1 | `private MapFrame frame()` | The map's frame on screen now: the expanded one while expanded. |
| 226 | 7 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 234 | 10 | `private void reset()` |  |
| 246 | 5 | `void refresh(int side, int lane)` | The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. |
| 253 | 6 | `void light(int side, int lane)` | An offer's band lit from its row in the office (-1 for none). |
| 260 | 7 | `private void requestDraw()` |  |

### A FRAME OF THE CLOCK (tick()) (lines 268-477)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 23 | `private void tick()` |  |
| 303 | 46 | `private boolean keepUp()` | The map caught up with the city: drawn away from the screen the first time (a draft, kept when drawn), a new map (a load, the land drawn again) thrown out with its pictures, the small map fitted again when the land gr... |
| 351 | 8 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. |
| 361 | 8 | `private void size()` | Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. |
| 370 | 7 | `private Pictures picturesFor(int level, int px)` |  |
| 385 | 33 | `private boolean paint(long deadline)` | The frame's painting, until the deadline: the tiles in view with no picture at this level, or one from an older version - stamped again, and painted only when the stamp moved - from the middle of the view out; at FAR ... |
| 420 | 9 | `private static List<long[]> tilesInView(MapFrame f)` | The tiles in view, nearest the view's middle first. |
| 430 | 4 | `private static double dist(long[] t, double mx, double my)` |  |
| 435 | 3 | `private static long nodeKey(long plots, long nx, long ny)` |  |
| 439 | 38 | `private boolean paintNodes(MapFrame f, long deadline)` |  |

### THE DRAWING (lines 478-678)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 13 | `private void draw()` |  |
| 496 | 29 | `private void drawTiles(GraphicsContext gc, MapFrame f, int level)` |  |
| 526 | 14 | `private void drawNodes(GraphicsContext gc, MapFrame f)` |  |
| 552 | 54 | `private void overlays(GraphicsContext gc, MapFrame f)` | Over the ground: the forty offers hatched and edged in pink - the office's side bright, the others faint, the one under the pointer or a hovered row lit, the picked one edged solid - the city's edge in white, the cent... |
| 608 | 25 | `private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market)` | The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). |
| 635 | 19 | `private void scaleBar(GraphicsContext gc, MapFrame f)` | The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. |
| 659 | 3 | `static Color colour(int argb)` | A colour from the model's 0xAARRGGBB. |
| 664 | 3 | `static String css(int argb)` | ...as CSS. |
| 669 | 9 | `private static ImagePattern hatchPattern()` | The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). |

### THE POINTER AND THE KEYS (lines 679-813)

| line | len | member | says |
|---:|---:|---|---|
| 683 | 57 | `private void handlers()` |  |
| 742 | 11 | `private void click(double sx, double sy)` | A click: an offer's band picks it, anywhere else picks the side it lies on. |
| 755 | 49 | `private void hover(double sx, double sy)` | What the pointer is over: the band it lights, and the words - one line on the small map, a card on the expanded one. |
| 809 | 4 | `private String nameOf(int id)` | A model building's name, by its type id: the catalogue's. |

### EXPAND: THE MAP OVER THE WHOLE WINDOW (lines 814-964)

| line | len | member | says |
|---:|---:|---|---|
| 818 | 14 | `void expand()` |  |
| 834 | 12 | `void collapse(boolean redraw)` | Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked. |
| 847 | 37 | `private void buildPane()` |  |
| 886 | 7 | `private void picked()` | The picked side and offer on the expanded map's head. |
| 895 | 18 | `private void fitPane()` | The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). |
| 915 | 6 | `private void fitBig()` | The expanded map on the city, as the small map opens. |
| 923 | 14 | `static List<String[]> legendEntries()` | The legend's entries: the ten classes and flats, the three roads, the land, the six resources in fields. |
| 941 | 13 | `private static GridPane legend()` |  |
| 956 | 8 | `private static void clip(Region box)` | A box's corners rounded off its contents. |

