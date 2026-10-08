package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.FillRule;
import javafx.scene.text.TextAlignment;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, the far nodes from the world's ground - with the city's block lines, its edge, its offers hatched and numbered by place, and its deposits over it, a scale bar, and what is under the pointer.
 *
 * WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
 * 2.8). Jerus: click a side to see its ten offers (six since 0.7.67), the
 * map always in view in the land office, an Expand that fills the window
 * and pans and zooms.
 * The model's map (CityMap, J3 and J3b) counts the city's buildings by
 * district and paints any tile from them; this draws it. What it draws by -
 * the transform, the level of detail, the tiles a screen asks for, what a
 * click picks, a tile's stamp - is arithmetic in the model's package
 * (MapFrame, LandMap, MapTiles), which MapCheck holds; this class is the
 * toolkit's half, checked by eye on the PC.
 *
 * TWO VIEWS, ONE CANVAS AND ONE SET OF CACHES (spec-land 2.6): the small map
 * opens on the city's ground with MapFrame.OPENING_MARGIN round it and stays
 * there (star: the page scrolls under the wheel, so the small map neither
 * pans nor zooms; a click selects a side or an offer, a hover names what is
 * under the pointer and lights an offer). Expand moves the canvas
 * into UserInterface's pane over the whole window (City History's, 0.7.23):
 * drag pans, the wheel zooms by MapFrame.ZOOM_STEP a notch at the pointer,
 * + and - zoom, 0 fits, Esc closes (the arrows stay the clock's speed, as
 * on City History's full screen); a legend and a hover
 * card (the ground, whose it is - the centre, a purchase, an offer and its
 * price - the fields and their tonnes, and at L0 the building or road).
 *
 * PAINTING IN THE BACKGROUND ON THE FX THREAD (spec-land 2.6): each frame
 * paints the tiles in view that have no picture, or whose stamp moved, from
 * the middle out, for at most FRAME_MS; a tile not painted yet shows another
 * level's picture of it meanwhile, the coarser first. A month, or a
 * purchase, changes the map's version (MapTiles.version()); the view then
 * stamps the tiles it shows again, and repaints only those whose stamp moved.
 *
 * OFF THE FX THREAD, ONLY ARITHMETIC (the orchestrator's brief): CityMap is
 * not thread-safe, so the first draw of a city's map is a draft - the land
 * copied, the counts as they stood (Game.mapDraft()) - drawn on WORKER and
 * kept back on the FX thread (Game.adoptMap()); the far nodes' ground is
 * read from the world (MapTiles.nodeTerrain(), World is safe) on WORKER and
 * coloured on the FX thread. Nothing on WORKER touches a node of the scene.
 *
 * THE OVERLAY ON THE BLOCK GRID (0.7.69, batch M5; the project's
 * spec-grid.md 2.4): the offers are rectangles of whole blocks, hatched on
 * their free ground and numbered by place as the office's rows are; the
 * city's edge is runs along block lines, drawn crisp and stepped on the
 * pixels just inside it; and the city's block grid is drawn faint where a
 * block is big enough to see. Where each falls on the screen's pixels is
 * MapFrame's arithmetic (onScreen(), runOnScreen(), blockLines()), so
 * MapCheck holds it; the edge and each offer's cut-outs are found once a
 * purchase or a listing, not every frame.
 */
final class MapView {

    /** The land office's small map, in pixels (spec-land 2.8). */
    static final double SMALL_W = 600, SMALL_H = 400;

    /** The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. */
    static final double FRAME_MS = 8;

    /** A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). */
    static final double CLICK_SLOP = 5;

    /** The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... */
    static final int FIELDS_MOST = 4000;

    /** ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron, not the city's. */
    static final int FIELD_CELLS_MOST = 64;

    /** The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... */
    static final double PANE_TOP = 14, PANE_SIDE = 22, PANE_BOTTOM = 12;

    /** ...and its head line's height with the gap under it. */
    static final double PANE_HEAD = 36;

    /** Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. */
    static final int DRAFTS_MOST = 3;

    /** Who hears a click: a side, and an offer's place on it or -1. */
    interface Picker { void picked(int side, int place); }

    /** The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground. Low priority; it dies with the window. */
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "city-map");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    private final UserInterface ui;
    private final Picker picker;
    private final Runnable onClose;
    private final Canvas canvas = new Canvas(SMALL_W, SMALL_H);
    private final StackPane smallBox = new StackPane();
    private final Label readout = new Label(), note = new Label();
    private final MapFrame small = new MapFrame(SMALL_W, SMALL_H);
    private MapFrame big;
    private boolean expanded;

    private VBox bigPane;
    private StackPane bigBox;
    private VBox card;
    private Label clockLine, bigLine;

    /* ----------------------------- what it draws from ----------------------------- */

    private CityMap shownMap;
    private MapTiles tiles;
    private Game.MapDraft draft;
    private int draftsFailed;
    private long version = Long.MIN_VALUE;
    private long fittedFor = Long.MIN_VALUE;
    private final TilePainter.Input in = new TilePainter.Input();
    private int[] buffer = new int[0];

    /** A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground. */
    private static final class Img {
        WritableImage image;
        long stamp, version = Long.MIN_VALUE;
        byte[] ground;
    }

    /** A level's pictures, the most recently drawn kept: `most` of them. */
    private static final class Pictures extends LinkedHashMap<Long, Img> {
        long most = 1;
        Pictures() { super(256, 0.75f, true); }
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Img> e) { return size() > most; }
    }

    private final Pictures l0small = new Pictures(), l0big = new Pictures(), l1 = new Pictures(), l2 = new Pictures(),
            nodes = new Pictures();
    private final Set<Long> nodesAsked = new HashSet<>();

    /** The deposits the far views mark, and the land they were found for. */
    private List<Deposit> fields = List.of();
    private long fieldsFor = Long.MIN_VALUE;

    /* ----------------------------- what the player is doing ----------------------------- */

    private int side = -1, place = -1;           // the side the office shows, and the offer picked
    private int litSide = -1, litPlace = -1;     // a row hovered in the office
    private int overSide = -1, overPlace = -1;   // the offer under the pointer
    private double pressX, pressY, lastX, lastY, moved;
    private boolean pressed;
    private double[] hoverAt;

    private boolean running, dirty;
    private final AnimationTimer pump = new AnimationTimer() {
        @Override public void handle(long now) { tick(); }
    };

    private final ImagePattern hatch;

    MapView(UserInterface ui, Picker picker, Runnable onClose) {
        this.ui = ui;
        this.picker = picker;
        this.onClose = onClose;
        hatch = hatchPattern();
        canvas.setFocusTraversable(true);
        readout.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT) + " -fx-background-color: #0b1118cc;"
                + " -fx-background-radius: 4; -fx-padding: 2 7 3 7;");
        readout.setMouseTransparent(true);
        readout.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        showIf(readout, false);
        note.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED) + " -fx-background-color: #0b1118cc;"
                + " -fx-background-radius: 6; -fx-padding: 6 12 6 12;");
        note.setMouseTransparent(true);
        note.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        note.setText(DRAWING);
        showIf(note, false);
        Label expand = new Label("Expand", icon(Icons.EXPAND, Palette.TEXT, 13));
        expand.setGraphicTextGap(6);
        expand.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT) + " -fx-background-color: #0b1118cc;"
                + " -fx-background-radius: 4; -fx-padding: 3 9 3 7; -fx-cursor: hand;");
        expand.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        expand.setOnMouseClicked(e -> { expand(); e.consume(); });
        HistoryScreen.tip(expand, EXPAND_TIP);
        StackPane.setAlignment(expand, Pos.TOP_RIGHT);
        StackPane.setAlignment(readout, Pos.TOP_LEFT);
        StackPane.setMargin(expand, new Insets(Palette.GAP));
        StackPane.setMargin(readout, new Insets(Palette.GAP));
        smallBox.getChildren().addAll(canvas, note, readout, expand);
        smallBox.setMinSize(SMALL_W, SMALL_H);
        smallBox.setPrefSize(SMALL_W, SMALL_H);
        smallBox.setMaxSize(SMALL_W, SMALL_H);
        smallBox.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-background-radius: 8;");
        clip(smallBox);
        canvas.sceneProperty().addListener((o, was, now) -> { if (now != null) requestDraw(); });
        handlers();
    }

    /** The note while the map is drawn away from the screen. */
    static final String DRAWING = "Drawing the city's map…";

    /** The Expand button's tooltip. */
    static final String EXPAND_TIP = "The map over the whole window: drag to pan, scroll to zoom, Esc to come back.";

    /** The expanded map's hint, at the right of its head. */
    static final String HINT = "drag to pan · scroll to zoom · 0 fits · Esc closes";

    /** The small map, its Expand button and its readout: what the land office lays in its hero row. */
    Region smallNode() { return smallBox; }

    /** The map's frame on screen now: the expanded one while expanded. */
    private MapFrame frame() { return expanded && big != null ? big : small; }

    /** A new city or a load: everything drawn is another city's. */
    void forget() {
        if (expanded) collapse(false);
        reset();
        side = place = litSide = litPlace = overSide = overPlace = -1;
        fittedFor = Long.MIN_VALUE;
        draftsFailed = 0;
    }

    private void reset() {
        shownMap = null;
        tiles = null;
        draft = null;
        version = Long.MIN_VALUE;
        for (Pictures p : List.of(l0small, l0big, l1, l2, nodes)) p.clear();
        nodesAsked.clear();
        fields = List.of();
        fieldsFor = Long.MIN_VALUE;
        edge = new double[4][0];
        ownedIn.clear();
        labelAt.clear();
        shapesFor = labelsFor = Long.MIN_VALUE;
    }

    /** The office drawn again: the side it shows and the offer picked, and whatever moved since - a month, a purchase. */
    void refresh(int side, int place) {
        this.side = side;
        this.place = place;
        requestDraw();
    }

    /** An offer lit from its row in the office (-1 for none). */
    void light(int side, int place) {
        if (side == litSide && place == litPlace) return;
        litSide = side;
        litPlace = place;
        requestDraw();
    }

    private void requestDraw() {
        dirty = true;
        if (!running && canvas.getScene() != null) {
            running = true;
            pump.start();
        }
    }

    /* =====================================================================
       A FRAME OF THE CLOCK (tick())
       ===================================================================== */

    private void tick() {
        if (canvas.getScene() == null || ui.game == null) {
            pump.stop();
            running = false;
            return;
        }
        long deadline = System.nanoTime() + (long) (FRAME_MS * 1e6);
        boolean more = keepUp();
        if (tiles != null) more |= paint(deadline);
        if (hoverAt != null) {
            hover(hoverAt[0], hoverAt[1]);
            hoverAt = null;
        }
        if (dirty) {
            dirty = false;
            draw();
        }
        // Idle: nothing to paint, nothing to draw. A draft or a far node coming back from WORKER asks again.
        if (!more && !dirty && hoverAt == null) {
            pump.stop();
            running = false;
        }
    }

    /**
     * The map caught up with the city: drawn away from the screen the first
     * time (a draft, kept when drawn), a new map (a load, the land drawn
     * again) thrown out with its pictures, the small map fitted again when
     * the land grows, and the pictures' version moved when the map or the
     * land did. Never more to do this frame: a draft back from WORKER asks.
     */
    private boolean keepUp() {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        if (!g.hasCityMap()) {
            if (shownMap != null) reset();
            if (draft == null || draft.game() != g) {
                if (draftsFailed >= DRAFTS_MOST) {
                    g.getCityMap();
                } else {
                    Game.MapDraft d = g.mapDraft();
                    draft = d;
                    if (d != null) WORKER.submit(() -> {
                        try {
                            d.draw();
                        } catch (RuntimeException e) {
                            System.out.println("The city map could not be drawn aside: " + e);
                        }
                        Platform.runLater(() -> kept(d));
                    });
                }
            }
        }
        if (g.hasCityMap() && g.getCityMap() != shownMap) {
            reset();
            shownMap = g.getCityMap();
            tiles = new MapTiles(shownMap);
        }
        showIf(note, tiles == null && !expanded);
        // Fitted again when the land moves: a purchase, or the land drawn again.
        // ...and when the listing's smallest offer changes, which the opening zoom is drawn close enough to number (0.7.79).
        LandMarket market = g.getLandManager().getMarket();
        long landKey = land.purchases().size() * 0x9E3779B97F4A7C15L ^ land.stamp() ^ LandMap.smallestOfferSide(market) * 0xC2B2AE3D27D4EB4FL;
        if (landKey != fittedFor) {
            fittedFor = landKey;
            LandMap.open(small, land, market);
            dirty = true;
        }
        if (tiles != null) {
            long v = MapTiles.version(shownMap, land);
            if (v != version) {
                version = v;
                dirty = true;
            }
        }
        size();
        return false;
    }

    /** A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. */
    private void kept(Game.MapDraft d) {
        if (d != draft) return;
        draft = null;
        Game g = ui.game;
        if (g == d.game() && d.drawn() && !g.hasCityMap() && !g.adoptMap(d)) draftsFailed++;
        if (!d.drawn()) draftsFailed++;
        requestDraw();
    }

    /** Each level's pictures sized to a screen of the larger view (MapTiles.tilesKept()): the small map's and the expanded one's share them. */
    private void size() {
        MapFrame f = big != null && big.width() * big.height() > small.width() * small.height() ? big : small;
        l0small.most = MapTiles.tilesKept(f, MapFrame.L0, MapFrame.SMALL_TILE_PX);
        l0big.most = MapTiles.tilesKept(f, MapFrame.L0, MapFrame.BIG_TILE_PX);
        l1.most = MapTiles.tilesKept(f, MapFrame.L1, MapFrame.MIDDLE_TILE_PX);
        l2.most = MapTiles.tilesKept(f, MapFrame.L2, MapFrame.FAR_TILE_PX);
        nodes.most = f.nodesAtMost();
    }

    private Pictures picturesFor(int level, int px) {
        switch (level) {
            case MapFrame.L0: return px >= MapFrame.BIG_TILE_PX ? l0big : l0small;
            case MapFrame.L1: return l1;
            default:          return l2;
        }
    }

    /**
     * The frame's painting, until the deadline: the tiles in view with no
     * picture at this level, or one from an older version - stamped again,
     * and painted only when the stamp moved - from the middle of the view out;
     * at FAR the far nodes, their ground asked of WORKER and coloured here.
     * True when there is more to do.
     */
    private boolean paint(long deadline) {
        MapFrame f = frame();
        int level = f.level();
        if (level == MapFrame.FAR) return paintNodes(f, deadline);
        int px = MapFrame.tilePx(level, f.scale());
        Pictures pictures = picturesFor(level, px);
        int w = World.TILE * px;
        if (buffer.length < w * w) buffer = new int[w * w];
        for (long[] t : tilesInView(f)) {
            long tx = t[0], ty = t[1], key = MapTiles.key(tx, ty);
            Img img = pictures.get(key);
            if (img != null && img.version == version) continue;
            if (System.nanoTime() > deadline) return true;
            long stamp = tiles.input(tx, ty, in);
            if (img != null && img.stamp == stamp) {
                img.version = version;
                continue;
            }
            TilePainter.Painted p = tiles.painted(tx, ty, in, stamp);
            TilePainter.Input painted = tiles.paintedInput(tx, ty);
            MapTiles.raster(painted == null ? in : painted, p, level, px, buffer);
            if (img == null) {
                img = new Img();
                img.image = new WritableImage(w, w);
                pictures.put(key, img);
            }
            img.image.getPixelWriter().setPixels(0, 0, w, w, PixelFormat.getIntArgbInstance(), buffer, 0, w);
            img.stamp = stamp;
            img.version = version;
            dirty = true;
        }
        return false;
    }

    /** The tiles in view, nearest the view's middle first. */
    private static List<long[]> tilesInView(MapFrame f) {
        List<long[]> out = new ArrayList<>();
        double mx = f.plotX(f.width() / 2) / World.TILE, my = f.plotY(f.height() / 2) / World.TILE;
        for (long ty = f.tileY0(); ty <= f.tileY1(); ty++) {
            for (long tx = f.tileX0(); tx <= f.tileX1(); tx++) out.add(new long[] { tx, ty });
        }
        out.sort((a, b) -> Double.compare(dist(a, mx, my), dist(b, mx, my)));
        return out;
    }

    private static double dist(long[] t, double mx, double my) {
        double dx = t[0] + 0.5 - mx, dy = t[1] + 0.5 - my;
        return dx * dx + dy * dy;
    }

    private static long nodeKey(long plots, long nx, long ny) {
        return ((long) Long.numberOfTrailingZeros(plots) << 48) ^ (nx << 24) ^ ny;
    }

    private boolean paintNodes(MapFrame f, long deadline) {
        long plots = f.nodePlots(), span = f.nodeSpan(), seed = shownMap.seed();
        int n = MapFrame.NODE_PX;
        if (buffer.length < n * n) buffer = new int[n * n];
        for (long ny = f.nodeY0(); ny <= f.nodeY1(); ny++) {
            for (long nx = f.nodeX0(); nx <= f.nodeX1(); nx++) {
                long key = nodeKey(plots, nx, ny);
                Img img = nodes.get(key);
                if (img == null) {
                    // Its ground asked of WORKER once; the answer asks for a frame.
                    if (nodesAsked.add(key)) {
                        final long x0 = nx * span, y0 = ny * span;
                        final MapTiles askedFor = tiles;
                        WORKER.submit(() -> {
                            byte[] ground = MapTiles.nodeTerrain(seed, x0, y0, plots);
                            Platform.runLater(() -> {
                                nodesAsked.remove(key);
                                if (tiles != askedFor) return;
                                Img got = new Img();
                                got.ground = ground;
                                nodes.put(key, got);
                                requestDraw();
                            });
                        });
                    }
                    continue;
                }
                if (img.version == version) continue;
                if (System.nanoTime() > deadline) return true;
                tiles.nodePixels(nx * span, ny * span, plots, img.ground, buffer);
                if (img.image == null) img.image = new WritableImage(n, n);
                img.image.getPixelWriter().setPixels(0, 0, n, n, PixelFormat.getIntArgbInstance(), buffer, 0, n);
                img.version = version;
                dirty = true;
            }
        }
        return false;
    }

    /* =====================================================================
       THE DRAWING
       ===================================================================== */

    private void draw() {
        MapFrame f = frame();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = f.width(), h = f.height();
        gc.setFill(colour(TileRaster.VOID));
        gc.fillRect(0, 0, w, h);
        if (tiles != null) {
            int level = f.level();
            if (level == MapFrame.FAR) drawNodes(gc, f);
            else drawTiles(gc, f, level);
        }
        overlays(gc, f);
    }

    private void drawTiles(GraphicsContext gc, MapFrame f, int level) {
        int px = MapFrame.tilePx(level, f.scale());
        Pictures here = picturesFor(level, px);
        // The next coarser picture first while a tile waits for its own (spec-land 2.6).
        List<Pictures> standIns = new ArrayList<>();
        for (Pictures p : List.of(l2, l1, l0small, l0big)) if (p != here) standIns.add(p);
        if (level == MapFrame.L0 && px >= MapFrame.BIG_TILE_PX) {
            standIns.remove(l0small);
            standIns.add(0, l0small);
        }
        for (long ty = f.tileY0(); ty <= f.tileY1(); ty++) {
            for (long tx = f.tileX0(); tx <= f.tileX1(); tx++) {
                long key = MapTiles.key(tx, ty);
                Img img = here.get(key);
                if (img == null) {
                    for (Pictures p : standIns) {
                        img = p.get(key);
                        if (img != null) break;
                    }
                }
                if (img == null || img.image == null) continue;
                double x0 = Math.floor(f.screenX(tx * World.TILE)), y0 = Math.floor(f.screenY(ty * World.TILE));
                double x1 = Math.floor(f.screenX((tx + 1) * World.TILE)), y1 = Math.floor(f.screenY((ty + 1) * World.TILE));
                gc.setImageSmoothing(x1 - x0 < img.image.getWidth());
                gc.drawImage(img.image, x0, y0, x1 - x0, y1 - y0);
            }
        }
        gc.setImageSmoothing(true);
    }

    private void drawNodes(GraphicsContext gc, MapFrame f) {
        long plots = f.nodePlots(), span = f.nodeSpan();
        gc.setImageSmoothing(false);
        for (long ny = f.nodeY0(); ny <= f.nodeY1(); ny++) {
            for (long nx = f.nodeX0(); nx <= f.nodeX1(); nx++) {
                Img img = nodes.get(nodeKey(plots, nx, ny));
                if (img == null || img.image == null) continue;
                double x0 = Math.floor(f.screenX(nx * span)), y0 = Math.floor(f.screenY(ny * span));
                double x1 = Math.floor(f.screenX((nx + 1) * span)), y1 = Math.floor(f.screenY((ny + 1) * span));
                gc.drawImage(img.image, x0, y0, x1 - x0, y1 - y0);
            }
        }
        gc.setImageSmoothing(true);
    }

    /** The offers' pink: the mockup's band edge (rgba(246, 166, 201)). */
    static final String OFFER_EDGE = "#f6a6c9";

    /** The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). */
    static final double BLOCK_LINE_ALPHA = 0.08;

    /** The city's edge's white, its alpha: 0.55, as since 0.7.61. */
    static final double EDGE_ALPHA = 0.55;

    /**
     * Over the ground (spec-grid 2.4, since 0.7.69): the city's block lines,
     * faint, where a block is at least MapFrame.BLOCK_LINES_FROM px; the
     * offers as hatched rectangles - each its free ground only, the city's
     * own ground in its rectangle left clear (LandMap.ownedIn()), its edge
     * too - edged in pink, the office's side bright, the others faint, the
     * one under the pointer or a hovered row lit, the picked one edged
     * solid; the city's edge in white, crisp and stepped, on the pixels just
     * inside it (MapFrame.runOnScreen()); the centre's box dashed, the
     * deposits at L1 and L2 (spec-land 2.6; at L0 the tiles draw the sites,
     * and past L2 a dot a field would bury the city); each offer numbered by
     * its place on its free ground (LandMap.labelBlock()), or its whole box
     * where only that holds a number (MapFrame.labelFits()), over the dots;
     * and the scale bar.
     * Every line lies on whole pixels (MapFrame.onScreen()).
     */
    private void overlays(GraphicsContext gc, MapFrame f) {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        shapes(land, market);
        blockLines(gc, f, market.getLevel());
        List<LandParcel> listing = market.getListing();
        double ax = Math.floorMod((long) Math.floor(f.screenX(0)), 8), ay = Math.floorMod((long) Math.floor(f.screenY(0)), 8);
        ImagePattern pattern = new ImagePattern(hatch.getImage(), ax, ay, 8, 8, false);
        gc.setFillRule(FillRule.EVEN_ODD);
        for (int i = 0; i < listing.size(); i++) {
            LandParcel p = listing.get(i);
            double[] box = f.onScreen(p.getX0(), p.getY0(), p.getX1(), p.getY1());
            if (box == null) continue;
            boolean mine = p.getSide() == side;
            boolean lit = (p.getSide() == overSide && p.getPlace() == overPlace) || (p.getSide() == litSide && p.getPlace() == litPlace);
            boolean picked = p.getSide() == side && p.getPlace() == place;
            // Its free ground: the box, less the city's own ground in it, filled even-odd.
            gc.beginPath();
            gc.rect(box[0], box[1], box[2] - box[0], box[3] - box[1]);
            for (double[] o : ownedIn.getOrDefault(p.getId(), List.of())) {
                double[] hole = f.onScreen(o[0], o[1], o[2], o[3]);
                if (hole != null) gc.rect(hole[0], hole[1], hole[2] - hole[0], hole[3] - hole[1]);
            }
            gc.setFill(Color.rgb(12, 18, 26, 0.24));
            gc.fill();
            gc.setFill(pattern);
            gc.fill();
            if (lit || picked) {
                gc.setFill(Color.web(Palette.BUILDING, lit ? 0.34 : 0.22));
                gc.fill();
            }
            // Its edge on the box's own outer pixels, a pixel wide (two when picked), where they lie on its free ground.
            double w = picked ? 2 : 1;
            boolean cut = ownedIn.containsKey(p.getId());
            if (cut) {
                gc.save();
                gc.clip();
            }
            gc.setStroke(Color.web(OFFER_EDGE, mine || lit ? 0.95 : 0.4));
            gc.setLineWidth(w);
            if (picked) gc.setLineDashes((double[]) null);
            else gc.setLineDashes(5, 4);
            gc.strokeRect(box[0] + w / 2, box[1] + w / 2, box[2] - box[0] - w, box[3] - box[1] - w);
            if (cut) gc.restore();
        }
        gc.setLineDashes((double[]) null);
        gc.setFillRule(FillRule.NON_ZERO);
        // The city's edge: its runs as one shape, so no pixel is drawn twice where they meet (0.7.69; LandMap.outline()).
        gc.beginPath();
        for (int i = 0; i < edge[0].length; i++) {
            double[] r = f.runOnScreen(edge[0][i], edge[1][i], edge[2][i], edge[3][i]);
            if (r != null) gc.rect(r[0], r[1], r[2], r[3]);
        }
        gc.setFill(Color.rgb(255, 255, 255, EDGE_ALPHA));
        gc.fill();
        double[] c = LandMap.centre(land);
        double[] cb = f.onScreen(c[0], c[1], c[2], c[3]);
        if (cb != null) {
            gc.setStroke(Color.rgb(255, 255, 255, 0.3));
            gc.setLineWidth(1);
            gc.setLineDashes(3, 4);
            gc.strokeRect(cb[0] + 0.5, cb[1] + 0.5, cb[2] - cb[0] - 1, cb[3] - cb[1] - 1);
            gc.setLineDashes((double[]) null);
        }
        int level = f.level();
        if (level == MapFrame.L1 || level == MapFrame.L2) deposits(gc, f, land, market);
        placeLabels(gc, f, land, listing);
        scaleBar(gc, f);
    }

    /** The city's edge and each offer's own ground cut out of its rectangle (by offer id), for the land and listing below: found once each time either moves, not every frame... */
    private double[][] edge = new double[4][0];
    private final Map<Integer, List<double[]>> ownedIn = new HashMap<>();
    private long shapesFor = Long.MIN_VALUE;

    /** ...and the square each offer's number stands on, for those and the view's scale. */
    private final Map<Integer, double[]> labelAt = new HashMap<>();
    private long labelsFor = Long.MIN_VALUE;
    private double labelsAtScale = Double.NaN;

    private void shapes(CityLand land, LandMarket market) {
        long key = land.stamp() * 31L + land.purchases().size() * 1_000_003L + market.getNextOfferId();
        if (key == shapesFor) return;
        shapesFor = key;
        edge = LandMap.outline(land);
        ownedIn.clear();
        for (LandParcel p : market.getListing()) {
            List<double[]> own = LandMap.ownedIn(land, p);
            if (!own.isEmpty()) ownedIn.put(p.getId(), own);
        }
    }

    /** The city's block lines in view, one shape: a pixel wide, faint, none where a block is under MapFrame.BLOCK_LINES_FROM px. */
    private void blockLines(GraphicsContext gc, MapFrame f, int level) {
        if (level < LandGrid.MIN_LEVEL) return;
        double[] xs = f.blockLines(level, true), ys = f.blockLines(level, false);
        if (xs.length == 0 && ys.length == 0) return;
        gc.beginPath();
        for (double x : xs) gc.rect(x, 0, 1, f.height());
        for (double y : ys) gc.rect(0, y, f.width(), 1);
        gc.setFill(Color.rgb(255, 255, 255, BLOCK_LINE_ALPHA));
        gc.fill();
    }

    /** Each offer's place, 1 to 6, at the middle of its free ground on screen - its whole box, or its freest square where it takes in the city's own (LandMap.labelBlock()) - or of its whole box where that square is too small to hold it and the box is not (MapFrame.labelFits()): white on a dark halo, the office's side's bright. */
    private void placeLabels(GraphicsContext gc, MapFrame f, CityLand land, List<LandParcel> listing) {
        if (labelsFor != shapesFor || labelsAtScale != f.scale()) {
            labelsFor = shapesFor;
            labelsAtScale = f.scale();
            labelAt.clear();
            for (LandParcel p : listing) labelAt.put(p.getId(), LandMap.labelBlock(land, p, MapFrame.PLACE_LABEL_FROM / f.scale()));
        }
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(3);
        for (LandParcel p : listing) {
            double[] at = labelAt.get(p.getId());
            double[] b = at == null ? null : f.onScreen(at[0], at[1], at[2], at[3]);
            if (!MapFrame.labelFits(b)) b = f.onScreen(p.getX0(), p.getY0(), p.getX1(), p.getY1());
            if (!MapFrame.labelFits(b)) continue;
            String words = LandMap.placeLabel(p);
            double x = Math.rint((b[0] + b[2]) / 2), y = Math.rint((b[1] + b[3]) / 2);
            gc.setStroke(Color.rgb(11, 17, 24, 0.85));
            gc.strokeText(words, x, y);
            gc.setFill(Color.rgb(255, 255, 255, p.getSide() == side ? 0.95 : 0.6));
            gc.fillText(words, x, y);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
    }

    /** The fields in and about the city's land and offers, each a dot in its resource's colour, sized by its sites - the far views' deposits (spec-land 2.6). */
    private void deposits(GraphicsContext gc, MapFrame f, CityLand land, LandMarket market) {
        long key = land.purchases().size() * 31L + land.stamp() + market.getNextOfferId();
        if (key != fieldsFor) {
            fieldsFor = key;
            double r = LandMap.offersReach(land, market) + 1;
            double ox = land.siteX() + 0.5, oy = land.siteY() + 0.5;
            List<Deposit> all = new ArrayList<>();
            double cells = Math.pow(Math.ceil(2 * r / World.CELL) + 1, 2);
            for (Resource res : Resource.values()) {
                if (!res.inFields() || all.size() >= FIELDS_MOST || cells > FIELD_CELLS_MOST) continue;
                all.addAll(LandMap.fieldsIn(land, res, ox - r, oy - r, ox + r, oy + r, FIELDS_MOST - all.size()));
            }
            fields = all;
        }
        for (Deposit d : fields) {
            double x = f.screenX(d.x() + 0.5), y = f.screenY(d.y() + 0.5);
            if (x < -10 || y < -10 || x > f.width() + 10 || y > f.height() + 10) continue;
            double r = Math.max(2.5, Math.min(6, 1.2 * Math.sqrt(d.sites())));
            gc.setFill(colour(0xff000000 | d.kind().colour()));
            gc.fillOval(x - r, y - r, 2 * r, 2 * r);
            gc.setStroke(Color.rgb(11, 17, 24, 0.85));
            gc.setLineWidth(1);
            gc.strokeOval(x - r, y - r, 2 * r, 2 * r);
        }
    }

    /** The scale bar at the bottom left: the longest of 1, 2 or 5 times a power of ten metres in SCALE_BAR_PX, its length in words. */
    private void scaleBar(GraphicsContext gc, MapFrame f) {
        double[] bar = f.scaleBar(SCALE_BAR_PX);
        double x = Palette.GAP + 2, y = f.height() - Palette.GAP - 4;
        gc.setStroke(Color.rgb(11, 17, 24, 0.8));
        gc.setLineWidth(4);
        gc.strokeLine(x, y, x + bar[1], y);
        gc.setStroke(Color.rgb(255, 255, 255, 0.9));
        gc.setLineWidth(1.5);
        gc.strokeLine(x, y, x + bar[1], y);
        gc.strokeLine(x, y - 4, x, y + 1);
        gc.strokeLine(x + bar[1], y - 4, x + bar[1], y + 1);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_LABEL));
        String words = MapFrame.scaleWords(bar[0]);
        gc.setLineWidth(3);
        gc.setStroke(Color.rgb(11, 17, 24, 0.85));
        gc.strokeText(words, x + bar[1] + 6, y + 4);
        gc.setFill(Color.rgb(255, 255, 255, 0.95));
        gc.fillText(words, x + bar[1] + 6, y + 4);
    }

    /** The scale bar's longest, in pixels: 120 (the mockup's). */
    static final double SCALE_BAR_PX = 120;

    /** A colour from the model's 0xAARRGGBB. */
    static Color colour(int argb) {
        return Color.rgb((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, ((argb >>> 24) & 255) / 255.0);
    }

    /** ...as CSS. */
    static String css(int argb) {
        return String.format("#%06x", argb & 0xffffff);
    }

    /** The offers' hatch: white diagonals at 34%, eight pixels apart (the mockup's). */
    private static ImagePattern hatchPattern() {
        WritableImage img = new WritableImage(8, 8);
        int line = 0x57ffffff;
        for (int i = 0; i < 8; i++) {
            img.getPixelWriter().setArgb(i, 7 - i, line);
            if (i < 7) img.getPixelWriter().setArgb(i + 1, 7 - i, 0x2bffffff);
        }
        return new ImagePattern(img, 0, 0, 8, 8, false);
    }

    /* =====================================================================
       THE POINTER AND THE KEYS
       ===================================================================== */

    private void handlers() {
        canvas.setOnMouseMoved(e -> {
            hoverAt = new double[] { e.getX(), e.getY() };
            requestDraw();
        });
        canvas.setOnMouseExited(e -> {
            hoverAt = null;
            showIf(readout, false);
            if (card != null) card.setVisible(false);
            if (overSide >= 0) {
                overSide = overPlace = -1;
                requestDraw();
            }
        });
        canvas.setOnMousePressed(e -> {
            pressed = true;
            pressX = lastX = e.getX();
            pressY = lastY = e.getY();
            moved = 0;
            if (expanded) canvas.requestFocus();
        });
        canvas.setOnMouseDragged(e -> {
            if (!pressed) return;
            moved = Math.max(moved, Math.hypot(e.getX() - pressX, e.getY() - pressY));
            if (expanded) {
                big.pan(e.getX() - lastX, e.getY() - lastY);
                requestDraw();
            }
            lastX = e.getX();
            lastY = e.getY();
        });
        canvas.setOnMouseReleased(e -> {
            boolean click = pressed && moved < CLICK_SLOP;
            pressed = false;
            if (click) click(e.getX(), e.getY());
        });
        canvas.setOnScroll(e -> {
            if (!expanded) return;
            double notches = e.getDeltaY() / 40.0;
            if (notches == 0) return;
            big.zoomNotches(e.getX(), e.getY(), notches);
            if (card != null) card.setVisible(false);
            requestDraw();
            e.consume();
        });
        canvas.setOnKeyPressed(e -> {
            if (!expanded) return;
            KeyCode k = e.getCode();
            double cx = big.width() / 2, cy = big.height() / 2;
            if (k == KeyCode.PLUS || k == KeyCode.EQUALS || k == KeyCode.ADD) big.zoomNotches(cx, cy, 1);
            else if (k == KeyCode.MINUS || k == KeyCode.SUBTRACT) big.zoomNotches(cx, cy, -1);
            else if (k == KeyCode.DIGIT0 || k == KeyCode.NUMPAD0) fitBig();
            else return;
            requestDraw();
            e.consume();
        });
    }

    /** A click: an offer's free ground picks it, anywhere else picks the side it lies on. */
    private void click(double sx, double sy) {
        MapFrame f = frame();
        Game g = ui.game;
        LandMap.Pick p = LandMap.pick(g.getCityLand(), g.getLandManager().getMarket(),
                (long) Math.floor(f.plotX(sx)), (long) Math.floor(f.plotY(sy)));
        side = p.side();
        place = p.owner() == LandMap.OFFER ? p.place() : -1;
        if (expanded) picked();
        picker.picked(side, place);
        requestDraw();
    }

    /** What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. */
    private void hover(double sx, double sy) {
        MapFrame f = frame();
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        long x = (long) Math.floor(f.plotX(sx)), y = (long) Math.floor(f.plotY(sy));
        LandMap.Pick p = LandMap.pick(land, market, x, y);
        int os = p.owner() == LandMap.OFFER ? p.side() : -1, op = p.owner() == LandMap.OFFER ? p.place() : -1;
        if (os != overSide || op != overPlace) {
            overSide = os;
            overPlace = op;
            dirty = true;
        }
        String what = null;
        if (tiles != null && f.level() == MapFrame.L0) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            long stamp = tiles.input(tx, ty, in);
            TilePainter.Painted painted = tiles.painted(tx, ty, in, stamp);
            TilePainter.Input pin = tiles.paintedInput(tx, ty);
            int plot = (int) ((y - ty * World.TILE) * World.TILE + (x - tx * World.TILE));
            what = LandMap.plotWords(pin == null ? in : pin, painted, plot, this::nameOf);
        }
        if (!expanded) {
            readout.setText(LandMap.ownerWords(land, p) + " · " + (what != null ? what : LandMap.groundWords(p.ground())));
            showIf(readout, true);
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add(what != null ? what : LandMap.groundWords(p.ground()));
        if (what != null) lines.add(LandMap.groundWords(p.ground()));
        lines.add(LandMap.ownerWords(land, p));
        List<LandMap.FieldAt> under = LandMap.fieldsAt(land, x, y);
        for (int i = 0; i < under.size() && i < CARD_FIELDS; i++) lines.add(LandMap.fieldWords(under.get(i), land, market));
        card.getChildren().clear();
        for (int i = 0; i < lines.size(); i++) {
            Label l = new Label(lines.get(i));
            l.setStyle(i == 0 ? Palette.strong(Palette.SIZE_BODY, Palette.TEXT) : Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            card.getChildren().add(l);
        }
        card.applyCss();
        card.autosize();
        double cw = card.prefWidth(-1), ch = card.prefHeight(-1);
        double left = sx + 14, top = sy + 14;
        if (left + cw > f.width() - 10) left = Math.max(10, sx - cw - 14);
        if (top + ch > f.height() - 10) top = Math.max(10, f.height() - ch - 10);
        card.setTranslateX(left);
        card.setTranslateY(top);
        card.setVisible(true);
    }

    /** The most fields a hover card lists: 3. */
    static final int CARD_FIELDS = 3;

    /** A model building's name, by its type id: the catalogue's. */
    private String nameOf(int id) {
        BuildingsTemplate t = ui.game.getBuildingManager().getTemplate(id);
        return t == null ? null : t.getName();
    }

    /* =====================================================================
       EXPAND: THE MAP OVER THE WHOLE WINDOW
       ===================================================================== */

    void expand() {
        if (expanded) return;
        if (bigPane == null) buildPane();
        expanded = true;
        smallBox.getChildren().remove(canvas);
        bigBox.getChildren().add(0, canvas);
        showIf(readout, false);
        clockLine.setText(ui.clockWords());
        picked();
        ui.showChartFullScreen(bigPane, clockLine, this::collapse);
        fitPane();
        fitBig();
        canvas.requestFocus();
    }

    /** Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked. */
    void collapse(boolean redraw) {
        if (!expanded) return;
        expanded = false;
        ui.closeChartFullScreen();
        if (card != null) card.setVisible(false);
        bigBox.getChildren().remove(canvas);
        smallBox.getChildren().add(0, canvas);
        canvas.setWidth(SMALL_W);
        canvas.setHeight(SMALL_H);
        requestDraw();
        if (redraw) onClose.run();
    }

    private void buildPane() {
        Label title = new Label("Land office › Map", icon(Icons.MAP, Palette.BUILDING, 16));
        title.setGraphicTextGap(8);
        title.setStyle(Palette.Fonts.sansSemiBold() + " -fx-font-size: 15px; -fx-text-fill: " + Palette.TEXT + ";");
        clockLine = new Label();
        clockLine.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        bigLine = new Label();
        bigLine.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label hint = new Label(HINT);
        hint.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label fit = stepChip("Fit", this::fitBig, true);
        Label close = stepChip("Back to the land office · Esc", () -> collapse(true), true);
        HBox head = new HBox(Palette.GAP_LOOSE, title, clockLine, bigLine, gap, hint, fit, close);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMinHeight(PANE_HEAD - Palette.GAP);
        card = new VBox(2);
        card.setStyle("-fx-background-color: #0b1118e6; -fx-background-radius: 6; -fx-border-color: " + Palette.EDGE
                + "; -fx-border-radius: 6; -fx-padding: 6 9 7 9;");
        // Laid out by hand at the pointer: never managed, so the pane never stretches it.
        card.setMouseTransparent(true);
        card.setManaged(false);
        card.setVisible(false);
        GridPane legend = legend();
        StackPane.setAlignment(legend, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(legend, new Insets(Palette.GAP));
        bigBox = new StackPane(legend, card);
        StackPane.setAlignment(card, Pos.TOP_LEFT);
        bigBox.setStyle("-fx-background-color: " + Palette.STAGE + ";");
        clip(bigBox);
        bigPane = new VBox(Palette.GAP, head, bigBox);
        bigPane.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-padding: " + PANE_TOP + " " + PANE_SIDE + " "
                + PANE_BOTTOM + " " + PANE_SIDE + ";");
        bigPane.widthProperty().addListener((o, was, now) -> fitPane());
        bigPane.heightProperty().addListener((o, was, now) -> fitPane());
    }

    /** The picked side and offer on the expanded map's head. */
    private void picked() {
        if (bigLine == null) return;
        LandParcel o = side >= 0 && place >= 0 ? ui.game.getLandManager().getMarket().offerIn(side, place) : null;
        bigLine.setText(o != null ? "picked: " + o.where() + " · " + LandMap.area(o.getKm2()) + " · "
                + LandMap.usd(o.getPriceUsd()) + " — Esc to buy it in the office"
                : side >= 0 ? "showing the " + CityLand.sideName(side).toLowerCase(java.util.Locale.ROOT) + " side's offers" : "");
    }

    /** The canvas sized to the pane: the window's width and height inside its padding and under its head (City History's rule, read off the window). */
    private void fitPane() {
        if (!expanded) return;
        javafx.scene.Scene window = bigPane.getScene();
        double w = (window != null ? window.getWidth() : bigPane.getWidth()) - 2 * PANE_SIDE;
        double h = (window != null ? window.getHeight() : bigPane.getHeight()) - PANE_TOP - PANE_BOTTOM - PANE_HEAD;
        if (w <= 0 || h <= 0) return;
        w = Math.floor(w);
        h = Math.floor(h);
        if (canvas.getWidth() == w && canvas.getHeight() == h && big != null) return;
        canvas.setWidth(w);
        canvas.setHeight(h);
        bigBox.setMinSize(w, h);
        bigBox.setPrefSize(w, h);
        bigBox.setMaxSize(w, h);
        if (big == null) big = new MapFrame(w, h);
        else big.resize(w, h);
        requestDraw();
    }

    /** The expanded map on the city, as the small map opens. */
    private void fitBig() {
        if (big == null) return;
        LandMap.open(big, ui.game.getCityLand(), ui.game.getLandManager().getMarket());
        requestDraw();
    }

    /** The legend's entries: the ten classes and flats, the three roads and (0.7.72) the railway, the land, the six resources in fields. */
    static List<String[]> legendEntries() {
        List<String[]> out = new ArrayList<>();
        for (int c = 0; c < BuildingVisual.CLASSES; c++) {
            out.add(new String[] { css(BuildingVisual.FILL[c]), BuildingVisual.CLASS_NAMES[c] });
            if (c == BuildingVisual.HOME) out.add(new String[] { css(BuildingVisual.FLATS_FILL), "Flats" });
        }
        out.add(new String[] { css(TileRaster.ROAD[BuildingVisual.GRAVEL]), "Gravel road" });
        out.add(new String[] { css(TileRaster.ROAD[BuildingVisual.PAVED]), "Paved road" });
        out.add(new String[] { css(TileRaster.ROAD[BuildingVisual.HIGHWAY]), "Highway" });
        out.add(new String[] { css(TileRaster.RAIL_LINE), "Railway" });
        out.add(new String[] { "#ffffff", "The city's edge" });
        out.add(new String[] { OFFER_EDGE, "On offer" });
        for (Resource r : Resource.values()) if (r.inFields()) out.add(new String[] { css(0xff000000 | r.colour()), r.label() });
        return out;
    }

    /** Rows a column of the legend holds: 12, its 23 entries in two columns (11 until 0.7.72 added the railway to its 22). */
    static final int LEGEND_ROWS = 12;

    private static GridPane legend() {
        GridPane grid = new GridPane();
        grid.setHgap(Palette.GAP_LOOSE);
        grid.setVgap(2);
        List<String[]> entries = legendEntries();
        for (int i = 0; i < entries.size(); i++) {
            grid.add(keySwatch(entries.get(i)[0], entries.get(i)[1]), i / LEGEND_ROWS, i % LEGEND_ROWS);
        }
        grid.setStyle("-fx-background-color: #0b1118d9; -fx-background-radius: 6; -fx-padding: 8 10 8 10;");
        grid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grid.setMouseTransparent(true);
        return grid;
    }

    /** A box's corners rounded off its contents. */
    private static void clip(Region box) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(box.widthProperty());
        clip.heightProperty().bind(box.heightProperty());
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        box.setClip(clip);
    }
}
