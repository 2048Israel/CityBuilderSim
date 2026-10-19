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
 * The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's map by levels of detail - painted tiles a few at a time each frame, the far nodes from the world's ground and the district pictures (0.7.128), and expanded on the real Earth a globe (0.7.135) - with the city's block lines, its edge, its offers hatched and numbered by place, and its deposits over it, a scale bar, and what is under the pointer.
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
 * + and - zoom, 0 fits, Esc closes, and since 0.7.135 W, A, S and D pan
 * (the arrows stay the clock's speed, as on City History's full screen);
 * a legend and a hover
 * card (the ground, whose it is - the centre, a purchase, an offer and its
 * price - the fields and their tonnes, and at L0 the building or road; on
 * Earth, since 0.7.119, how far and which way from the nearest place, and
 * the geologic province: LandMap.earthWords()).
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
 * coloured on the FX thread; and since 0.7.88 (batch RD2) the district plans
 * the tiles are painted from: a tile whose plans are not drawn
 * (MapTiles.ready()) hands out their jobs (MapTiles.jobs(): the inputs read
 * here, the ground read and the plan drawn on WORKER, in order) and shows
 * another level's picture until they come back and are kept here
 * (CityMap.adopt()); and since 0.7.128 (batch MM1) the far nodes' district
 * pictures, drawn with those plans: a node drawn without a picture it would
 * sample (MapTiles.nodePixels()) hands out its district's plan jobs
 * (CityMap.pictureJobs()) while the view's districts fit what the map keeps
 * (MapTiles.picturesFit()), the frame's in one task on WORKER, and is drawn
 * again once they are kept. Nothing on WORKER touches a node of the scene.
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
 *
 * THE BOATS (0.7.97, batch O13; docs/studies/spec-oil.md 2.10,
 * spec-roads-and-ports.md 4): over the tiles, each terminal's lane out to sea -
 * its route (CityMap's
 * THE SEA ROUTES, found on WORKER and kept here) - from L2 in, and from L1 in
 * the boats on it: the month's calls (BoatSchedule) where the game's clock
 * puts them, a boat-month BOAT_GAME_MONTHS of the game's, so a month of
 * ships plays as BoatSchedule.MONTH_SECONDS at 1x and stands while the clock
 * is paused; each boat's shape and colours ShipShapes'. While any is in view
 * the frame goes on and draws again whenever the clock moves them.
 *
 * THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13): on the plane,
 * over the tiles, the city's highway and main line run on from where they
 * leave its ground along their way out over dry ground, as far as the land
 * runs (at most CityMap.WORLD_REACH; CityMap.worldLines()), faded
 * (WORLD_LINE_ALPHA) and labelled LandMap.WORLD_WORDS, the label kept inside
 * the view.
 *
 * THE TRAINS (0.8.2, batch FR1; spec-freight 4.4): the boats' way, on land -
 * the month's trains (TrainSchedule, from the railway's tonnes by kind) on
 * the map's routes from the world line to each yard's sidings
 * (CityMap.trainRoutes()), on the boats' clock, from L1 in: a dash of the
 * train's colour at the middle view, each vehicle's body and mark
 * (TrainShapes) at the near view.
 *
 * THE TRUCKS (0.8.8, batch FR5; spec-freight 4.7): the same on the roads, at
 * the near view alone and while the player's setting is on (Settings,
 * "Trucks on the map", on by default) - the month's trucks
 * (TruckSchedule, from Haulage's loads) on the highways' routes from the
 * world line to the gateways (CityMap.truckRoutes()) and on the access roads
 * of the districts in view (CityMap.accessRoutesIn()), each one standing for
 * the schedule's N loads, which the legend gives (TRUCK_WORDS); the body and
 * colour are TruckShapes'.
 *
 * THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3, Jerus's
 * answers of 2026-10-11, "yes"): on an Earth world the expanded map is a
 * view of the planet (GlobeFrame) drawn in one of three tiers by its scale
 * - PICTURE, the planet's picture (GlobePicture, made once on a thread of
 * its own, 12' then 4'), each pixel through the camera to its texel;
 * GROUND, within GlobeFrame.planeDrawsKm() of the city the plane's own far
 * nodes reprojected through the camera (GlobeFrame.ground(), ★E9), and
 * past it the far rule through the camera (GlobeFrame.groundPiece()) in
 * pieces on WORKER once the view holds still, the last whole picture
 * carried pixel by pixel and the planet's picture under it meanwhile;
 * PLANE, the map as before, its MapFrame following the globe's view. A
 * view on the move is drawn at half its resolution and a still one at its
 * own, in bands on the frames' pool (GlobePicture.bands()); the swap
 * crossfades over a notch inside the handoff (GlobeFrame.crossfade()).
 * Drag, the wheel, + and - and the keys move the globe's view; Fit shows
 * the whole planet facing the city, north up, and the map still opens on
 * the city. Over it the city's ring at every scale, its edge through the
 * camera in the PICTURE tier once it is EDGE_FROM_PX across (the plane's
 * nodes draw it in the GROUND tier), and its offers and centre where the
 * plane draws; the hover is LandMap.globeHoverWords(), the legend
 * LandMap.legend() by tier, the scale bar true at the centre and saying so
 * (GlobeFrame.scaleBar()). A generated world keeps the flat map at every
 * scale (Q8), and the small map never leaves the plane.
 *
 * THE KEYS (0.7.135; spec-globe 3.11): W, A, S and D held pan the expanded
 * map, on the plane and the globe, at every scale - UserInterface's key
 * filter hands them here while the map is over the window and nobody is
 * typing - each frame of the pump by MapFrame.keyVelocity() over the time
 * since the last (MapFrame.keyPan(), GlobeFrame.keyPan()). Not on the small
 * map, which never pans.
 *
 * BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3):
 * on the expanded map, wherever the offers are drawn, a click on one opens
 * its card beside it - the land office's (LandScreen.mapCardNode()), its
 * figures LandMap.card()'s - shift adds one or drops it, Shift and a drag
 * draws a box that takes every offer whose free ground it meets, and
 * Ctrl+A or the head's "Select every offer" takes them all; Buy or Enter
 * buys them, short the card issues the funding page's bond, the funding
 * page comes back to the map, and the head shows the cash and the pay
 * toggle the pane covers. The small map clicks as before.
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

    /** The draft of the city's map on WORKER, this view's or the minimap's (0.7.134), until it comes back: the other waits for it rather than draw the same map aside twice. The screen's thread's. */
    static Game.MapDraft drafting;

    /** Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step with the game's clock, and stands while it is paused (star O13-7). */
    static final double BOAT_GAME_MONTHS = BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH;

    /** A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... */
    static final double LANE_BAND_ALPHA = 0.10, LANE_BAND_PX = 8;

    /** ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). */
    static final double LANE_DASH_ALPHA = 0.45;

    /** Who hears a click: a side, and an offer's place on it or -1. */
    interface Picker { void picked(int side, int place); }

    /** The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the minimap's (MiniMap), so the map's jobs run on one thread in the order handed out. Low priority; it dies with the window. */
    static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
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
    private Label clockLine;

    /* ----------------------------- what it draws from ----------------------------- */

    private CityMap shownMap;
    private MapTiles tiles;
    private Game.MapDraft draft;
    private int draftsFailed;
    private long version = Long.MIN_VALUE;
    private long fittedFor = Long.MIN_VALUE;
    private final TilePainter.Input in = new TilePainter.Input();
    private int[] buffer = new int[0];

    /** A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count of pictures kept then (CityMap.picturesKept()): drawn again when that moves. */
    private static final class Img {
        WritableImage image;
        long stamp, version = Long.MIN_VALUE;
        byte[] ground;
        boolean partial;
        long pictures;
        /** A far node's colours as drawn (0.7.135): what the globe reprojects near the city. */
        int[] pixels;
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
    /** The districts whose pictures' jobs are on WORKER (0.7.128), and those the far nodes drawn this frame lacked. */
    private final Set<CityMap.District> picturesAsked = new HashSet<>(), picturesWanted = new java.util.LinkedHashSet<>();

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

    /* ----------------------------- buying from the map (0.7.137, batch M6) ----------------------------- */

    /** The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a month, a load and a collapse. */
    private final LandMap.Picks picks = new LandMap.Picks();
    /** The card in the pane (the land office's: LandScreen.mapCardNode(), mapReceiptNode()) and what it was drawn for - null for a receipt. */
    private VBox buyBox;
    private String cardFor;
    /** The sites of the fields the card's offers hold, each its square of plots and its resource's colour, {x0, y0, x1, y1, argb}: ringed wherever they lie (board 7). */
    private List<double[]> rings = List.of();
    /** The box of plots the card stands beside: the offer clicked, what a box took, the shelf's first for Ctrl+A, the ground just bought. */
    private double[] cardAt;
    /** A purchase from the card and its receipt: what is being bought - ids, places, the ground - shown by the ground once it is the city's. */
    private Bought bought;
    private boolean receiptShown;
    /** The card sent to the funding page (B5): the map comes back over the window at the view it left when the page is done. */
    private boolean backFromFunding;
    /** Shift and a drag (B7): a box drawn from the press to (boxX, boxY), in the screen's pixels. */
    private boolean boxing;
    private double boxX, boxY;
    /** The head's money and its pay toggle (the pane covers the window's header), and what they were drawn for. */
    private Label headMoney;
    private HBox headPay;
    private String headFor;

    private boolean running, dirty;

    /* ----------------------------- the boats (0.7.97) ----------------------------- */

    /** The routes being found on WORKER, or null. */
    private CityMap.RoutesJob routesAsked;
    /** The schedule drawn from: its boat-month, the routes and the game month it was made on. */
    private BoatSchedule schedule;
    private int scheduleMonth = Integer.MIN_VALUE, scheduleGameMonth = Integer.MIN_VALUE;
    private List<BoatSchedule.Route> scheduleRoutes;
    /** The trains' schedule drawn from (0.8.2, batch FR1): its boat-month, the routes and the game month it was made on. */
    private TrainSchedule trainSchedule;
    private int trainMonth = Integer.MIN_VALUE, trainGameMonth = Integer.MIN_VALUE;
    private List<TrainSchedule.Route> trainRoutes;
    /** Whether boats - or since 0.8.2 trains - are in view (the frame goes on), and the clock they were last drawn at. */
    private boolean boatsLive;
    private double drawnClock = Double.NaN;
    private final AnimationTimer pump = new AnimationTimer() {
        @Override public void handle(long now) { tick(); }
    };

    private final ImagePattern hatch;

    /* ----------------------------- the globe (0.7.135, batch E9) ----------------------------- */

    /** The expanded map's view on an Earth world, and the world and data it is of; null on a generated world. */
    private GlobeFrame globe;
    private World.Key globeKey;
    private EarthData globeData;
    /** The land and offers its reach was found for. */
    private long reachFor = Long.MIN_VALUE;

    /** The globe's picture of the view: its pixels, the view it was drawn for (a copy, its size the picture's), how, and what moved since. */
    private WritableImage globeImg;
    private int[] globePx = new int[0];
    private GlobeFrame globeDrawn;
    private int globeMode = -1;
    private long globeInputs = Long.MIN_VALUE;
    /** Bumped when a far node is coloured or the planet's picture kept: a still view is drawn again from them. */
    private long inputs;
    /** The screen's box a still picture is to be drawn again over (a piece in, a node coloured), or null. */
    private double[] patch;

    /** The GROUND tier's pieces past the plane, for one view at rest: its classes a pixel (-1 not in yet), the view and its number. */
    private byte[] pieceCls;
    private GlobeFrame pieceView;
    private volatile int pieceGen;
    /** The last whole picture at rest, carried pixel by pixel while the next is drawn. */
    private int[] carryPx;
    private GlobeFrame carryView;

    /** How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. */
    private static final int MODE_PICTURE = 0, MODE_GROUND = 1, MODE_FADE = 2;

    /** W, A, S and D held, and when the keys last moved the view (0 when none is held). */
    private boolean keyW, keyA, keyS, keyD;
    private long keyNanos;

    /** The tier the legend was laid out for. */
    private int legendTier = Integer.MIN_VALUE;

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
        // Within the small map (0.7.88): a street's words after whose it is may run past it, so it wraps there.
        readout.setWrapText(true);
        readout.setMaxWidth(SMALL_W - 2 * Palette.GAP);
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
    static final String EXPAND_TIP = "The map over the whole window: drag or W, A, S and D to pan, scroll to zoom, Esc to come back.";

    /** The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), every key in its tooltip (KEYS_TIP). */
    static final String HINT = "click an offer to buy it";

    /** ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. */
    static final String KEYS_TIP = "Click an offer to open its card beside it; shift-click adds an offer to the card or drops it;"
            + " Shift and a drag picks every offer in a box; Ctrl+A picks them all. Enter buys the card's offers; Esc closes the"
            + " card, then the map.\nDrag or W, A, S and D to pan · scroll, + or - to zoom · 0 fits.";

    /** The head's chip that picks every offer listed (B7). */
    static final String SELECT_EVERY = "Select every offer";

    /** The small map, its Expand button and its readout: what the land office lays in its hero row. */
    Region smallNode() { return smallBox; }

    /** The map's frame on screen now: the expanded one while expanded. */
    private MapFrame frame() { return expanded && big != null ? big : small; }

    /** A new city or a load: everything drawn is another city's. */
    void forget() {
        backFromFunding = false;
        if (expanded) collapse(false);
        picks.clear();
        bought = null;
        hideBuyCard();
        reset();
        resetGlobe();
        side = place = litSide = litPlace = overSide = overPlace = -1;
        fittedFor = Long.MIN_VALUE;
        draftsFailed = 0;
    }

    private void reset() {
        resetGlobePicture();
        shownMap = null;
        tiles = null;
        draft = null;
        version = Long.MIN_VALUE;
        for (Pictures p : List.of(l0small, l0big, l1, l2, nodes)) p.clear();
        nodesAsked.clear();
        picturesAsked.clear();
        picturesWanted.clear();
        fields = List.of();
        fieldsFor = Long.MIN_VALUE;
        edge = new double[4][0];
        ownedIn.clear();
        labelAt.clear();
        shapesFor = labelsFor = Long.MIN_VALUE;
        routesAsked = null;
        schedule = null;
        scheduleRoutes = null;
        scheduleMonth = scheduleGameMonth = Integer.MIN_VALUE;
        trainSchedule = null;
        trainRoutes = null;
        trainMonth = trainGameMonth = Integer.MIN_VALUE;
        truckSchedule = null;
        truckRoutes = null;
        truckMonth = truckGameMonth = Integer.MIN_VALUE;
        districtTrucks.clear();
        trucksStand = 0;
        boatsLive = false;
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
        more |= keys();
        // The head's money and the card caught up with the city (0.7.137): a month, a purchase, the toggle.
        if (expanded) {
            syncHead();
            syncCard();
        }
        if (tiles != null) more |= paint(deadline);
        if (hoverAt != null) {
            hover(hoverAt[0], hoverAt[1]);
            hoverAt = null;
        }
        // The boats (0.7.97): while any is in view the frame goes on, and draws again whenever the clock moves them.
        if (boatsLive) {
            more = true;
            if (boatClock() != drawnClock) dirty = true;
        }
        if (dirty) {
            dirty = false;
            draw();
            if (expanded) {
                legendNote();
                legendFor(shownTier());
                legendTrucks();
            }
        }
        // A globe drawn on the move at half resolution is drawn again at its own the frame after it holds still; a piece or a
        // node come in is laid over the still picture the next frame.
        if (globeShown() && (globeHalf || patch != null) && globeImg != null) {
            dirty = true;
            more = true;
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
     * land did. Never more to do this frame - a draft back from WORKER asks -
     * but while the minimap's draft is there (0.7.134: MiniMap, drafting),
     * which keeps the frames coming until it is kept.
     */
    private boolean keepUp() {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        boolean waiting = false;
        if (!g.hasCityMap()) {
            if (shownMap != null) reset();
            if (draft == null || draft.game() != g) {
                if (drafting != null && drafting.game() == g && drafting != draft) {
                    // The minimap's draft on WORKER (0.7.134): this frame and the next look again until it is kept.
                    waiting = true;
                } else if (draftsFailed >= DRAFTS_MOST) {
                    g.getCityMap();
                } else {
                    Game.MapDraft d = g.mapDraft();
                    draft = d;
                    drafting = d;
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
        globeUp(land, market, landKey);
        if (tiles != null) {
            long v = MapTiles.version(shownMap, land);
            if (v != version) {
                version = v;
                dirty = true;
            }
        }
        size();
        return waiting;
    }

    /** A draft drawn on WORKER, back on the FX thread: kept as the city's map when it still is the city's. */
    private void kept(Game.MapDraft d) {
        if (drafting == d) drafting = null;
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
        if (onGlobe()) return paintGlobe(deadline);
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
            // Its district plans drawn away from the screen first (0.7.88): it waits, another level's picture standing in.
            if (!tiles.ready(tx, ty)) {
                ask(tx, ty);
                continue;
            }
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

    /** A tile's plans' jobs handed to WORKER, each kept on the FX thread when it has run, and the view drawn again. */
    private void ask(long tx, long ty) {
        final MapTiles askedFor = tiles;
        final CityMap map = shownMap;
        for (CityMap.Job j : tiles.jobs(tx, ty)) {
            WORKER.submit(() -> {
                try {
                    j.run();
                } catch (RuntimeException e) {
                    System.out.println("A district's plan could not be drawn: " + e);
                }
                Platform.runLater(() -> {
                    if (tiles == askedFor) map.adopt(j);
                    requestDraw();
                });
            });
        }
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
        picturesWanted.clear();
        try {
            return paintNodes(f.nodePlots(), f.nodeX0(), f.nodeX1(), f.nodeY0(), f.nodeY1(), deadline, shownMap.picturesKept());
        } finally {
            askPictures(f);
        }
    }

    /**
     * The district pictures the far nodes drawn this frame lacked (0.7.128,
     * batch MM1; MapTiles.wanted()): their plans' jobs (CityMap.pictureJobs())
     * handed to WORKER as one task, in order, while the view's districts fit
     * what the map keeps (MapTiles.picturesFit()) - each district once until
     * they come back, kept here (CityMap.adopt()), and the nodes that waited
     * drawn again (their count of pictures kept moved), which ask for the
     * rest: a frame hands out jobs for FRAME_MS at most, their inputs read
     * here.
     */
    private void askPictures(MapFrame f) {
        if (picturesWanted.isEmpty() || !tiles.picturesFit(f)) return;
        final MapTiles askedFor = tiles;
        final CityMap map = shownMap;
        final List<CityMap.District> asked = new ArrayList<>();
        final List<CityMap.Job> jobs = new ArrayList<>();
        long until = System.nanoTime() + (long) (FRAME_MS * 1e6);
        for (CityMap.District d : picturesWanted) {
            if (System.nanoTime() > until) break;
            if (!picturesAsked.add(d)) continue;
            asked.add(d);
            jobs.addAll(map.pictureJobs(d));
        }
        if (asked.isEmpty()) return;
        WORKER.submit(() -> {
            for (CityMap.Job j : jobs) {
                try {
                    j.run();
                } catch (RuntimeException e) {
                    System.out.println("A district's picture could not be drawn: " + e);
                }
            }
            Platform.runLater(() -> {
                if (tiles != askedFor) return;
                for (CityMap.Job j : jobs) map.adopt(j);
                picturesAsked.removeAll(asked);
                requestDraw();
            });
        });
    }

    /** The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). True when there is more to do. */
    private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept) {
        long span = MapFrame.NODE_PX * plots;
        World.Key world = shownMap.worldKey();
        int n = MapFrame.NODE_PX;
        if (buffer.length < n * n) buffer = new int[n * n];
        for (long ny = ny0; ny <= ny1; ny++) {
            for (long nx = nx0; nx <= nx1; nx++) {
                long key = nodeKey(plots, nx, ny);
                Img img = nodes.get(key);
                if (img == null) {
                    // Its ground asked of WORKER once; the answer asks for a frame.
                    if (nodesAsked.add(key)) {
                        final long x0 = nx * span, y0 = ny * span;
                        final MapTiles askedFor = tiles;
                        WORKER.submit(() -> {
                            byte[] ground = MapTiles.nodeTerrain(world, x0, y0, plots);
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
                if (img.version == version && !(img.partial && img.pictures != picturesKept)) continue;
                if (System.nanoTime() > deadline) return true;
                img.partial = tiles.nodePixels(nx * span, ny * span, plots, img.ground, buffer) > 0;
                img.pictures = picturesKept;
                picturesWanted.addAll(tiles.wanted());
                if (img.image == null) img.image = new WritableImage(n, n);
                img.image.getPixelWriter().setPixels(0, 0, n, n, PixelFormat.getIntArgbInstance(), buffer, 0, n);
                if (img.pixels == null) img.pixels = new int[n * n];
                System.arraycopy(buffer, 0, img.pixels, 0, n * n);
                img.version = version;
                dirty = true;
                nodeColoured(nx * span, ny * span, span);
            }
        }
        return false;
    }

    /* =====================================================================
       THE DRAWING
       ===================================================================== */

    private void draw() {
        if (onGlobe()) {
            drawGlobe();
            return;
        }
        MapFrame f = frame();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = f.width(), h = f.height();
        gc.setFill(colour(TileRaster.VOID));
        gc.fillRect(0, 0, w, h);
        boatsLive = false;
        trucksStand = 0;
        if (tiles != null) {
            int level = f.level();
            if (level == MapFrame.FAR) drawNodes(gc, f);
            else {
                drawTiles(gc, f, level);
                boats(gc, f, level);
                trains(gc, f, level);
                trucks(gc, f, level);
            }
        }
        // The swap (0.7.135): a notch inside the handoff the globe's picture of the plane's own nodes is laid over them, whole at
        // the handoff and gone a notch in (GlobeFrame.crossfade()).
        if (expanded && globe != null && tiles != null) {
            double a = GlobeFrame.crossfade(globe.kmPx());
            if (a > 0) {
                composeGlobe(MODE_FADE);
                gc.setGlobalAlpha(a);
                drawGlobeImage(gc);
                gc.setGlobalAlpha(1);
            }
        }
        worldLines(gc, f);
        overlays(gc, f);
        drawRings(gc);
        drawBox(gc);
        placeBuyCard();
    }

    /* =====================================================================
       THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13)
       ===================================================================== */

    /** The world line's label as measured: its font, its width and its height above the baseline, kept until the font changes (FX2). */
    private javafx.scene.text.Font worldLabelFont;
    private double worldLabelW, worldLabelH;

    /**
     * The highway's and the main line's faded runs past the city's ground
     * (CityMap.worldLines()): each from its point along its way out over dry
     * ground, as far as the land runs (CityMap.WorldLine.reach(), at most
     * CityMap.WORLD_REACH) and no farther, clipped to the canvas, the highway
     * in its road colour at WORLD_LINE_ALPHA, the railway dashed in RAIL_DARK;
     * labelled once near the far end of the part in view, the label kept
     * inside the view (LandMap.worldLabelAt, FX2).
     */
    private void worldLines(GraphicsContext gc, MapFrame f) {
        if (shownMap == null) return;
        List<CityMap.WorldLine> lines = shownMap.worldLines();
        if (lines.isEmpty()) return;
        double scale = f.scale(), w = f.width(), h = f.height();
        javafx.scene.text.Font labelFont = Palette.Fonts.monoFont(Palette.SIZE_BODY);
        if (!labelFont.equals(worldLabelFont)) {
            javafx.scene.text.Text probe = new javafx.scene.text.Text(LandMap.WORLD_WORDS);
            probe.setFont(labelFont);
            worldLabelW = probe.getLayoutBounds().getWidth();
            worldLabelH = probe.getBaselineOffset();
            worldLabelFont = labelFont;
        }
        for (CityMap.WorldLine line : lines) {
            boolean highway = line.net() == CityRuns.HIGHWAYS;
            double sx = f.screenX(line.x()), sy = f.screenY(line.y());
            double r = Math.toRadians(line.way());
            double dx = Math.cos(r), dy = Math.sin(r);
            double[] cut = rayRect(sx, sy, dx, dy, line.reach() * scale, w, h);
            if (cut == null) continue;
            gc.setStroke(colour(highway ? TileRaster.ROAD[BuildingVisual.HIGHWAY] : TileRaster.RAIL_DARK, WORLD_LINE_ALPHA));
            gc.setLineWidth(highway ? Math.max(2, 0.86 * scale) : Math.max(1.5, 0.52 * scale));
            if (highway) gc.setLineDashes((double[]) null);
            else gc.setLineDashes(6, 5);
            gc.strokeLine(cut[0], cut[1], cut[2], cut[3]);
            gc.setLineDashes((double[]) null);
            double[] at = LandMap.worldLabelAt(cut[0], cut[1], cut[2], cut[3], worldLabelW, worldLabelH, w, h);
            if (at == null) continue;
            gc.setFont(labelFont);
            gc.setFill(Color.rgb(255, 255, 255, WORLD_LINE_ALPHA));
            gc.fillText(LandMap.WORLD_WORDS, at[0], at[1]);
        }
    }

    /** The part of the segment (x, y) + t (dx, dy), 0 <= t <= tMax, that lies on the canvas, {x0, y0, x1, y1}; null when it misses it. */
    private static double[] rayRect(double x, double y, double dx, double dy, double tMax, double w, double h) {
        double t0 = 0, t1 = tMax;
        if (Math.abs(dx) < 1e-12) {
            if (x < 0 || x > w) return null;
        } else {
            double a = (0 - x) / dx, b = (w - x) / dx;
            if (a > b) { double q = a; a = b; b = q; }
            t0 = Math.max(t0, a);
            t1 = Math.min(t1, b);
        }
        if (Math.abs(dy) < 1e-12) {
            if (y < 0 || y > h) return null;
        } else {
            double a = (0 - y) / dy, b = (h - y) / dy;
            if (a > b) { double q = a; a = b; b = q; }
            t0 = Math.max(t0, a);
            t1 = Math.min(t1, b);
        }
        if (t0 > t1 || t1 < 0) return null;
        double from = Math.max(0, t0);
        return new double[] { x + dx * from, y + dy * from, x + dx * t1, y + dy * t1 };
    }

    /* =====================================================================
       THE BOATS (0.7.97, batch O13)
       ===================================================================== */

    /** The boats' clock, in boat-months: the game's clock (UserInterface.clockMonths()) over BOAT_GAME_MONTHS. */
    private double boatClock() {
        return ui.clockMonths() / BOAT_GAME_MONTHS;
    }

    /**
     * The lanes and the boats over the tiles: each route on screen as a
     * band and dashes (from L2 in), and from L1 in the boats the month's
     * schedule puts on them at the clock - each one's hull, deck and bridge
     * (ShipShapes), a dart of its colour when it is small, fading in the
     * abyss. The routes are found on WORKER the first time; nothing is drawn
     * until they are.
     */
    private void boats(GraphicsContext gc, MapFrame f, int level) {
        if (shownMap == null || shownMap.shore().isEmpty()) return;
        List<BoatSchedule.Route> routes = shownMap.seaRoutesIfFound();
        if (routes == null) {
            askRoutes();
            return;
        }
        if (routes.isEmpty()) return;
        double clock = boatClock();
        int m = (int) Math.floor(clock);
        double t = clock - m;
        Game g = ui.game;
        if (schedule == null || scheduleMonth != m || scheduleRoutes != routes || scheduleGameMonth != g.getMonth()) {
            schedule = BoatSchedule.of(m, g.getPorts(), routes, Ports.bulkShipsStanding(g.getBuildingManager()));
            scheduleMonth = m;
            scheduleRoutes = routes;
            scheduleGameMonth = g.getMonth();
        }
        double margin = ShipShapes.lengthM(Ports.Ship.VLCC) / World.PLOT_M * ShipShapes.BOOST;
        long x0 = (long) Math.floor(f.plotX(0) - margin), y0 = (long) Math.floor(f.plotY(0) - margin);
        long x1 = (long) Math.ceil(f.plotX(f.width()) + margin), y1 = (long) Math.ceil(f.plotY(f.height()) + margin);
        int crossing = 0;
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        for (BoatSchedule.Route r : routes) {
            if (!BoatSchedule.crosses(r, x0, y0, x1, y1)) continue;
            crossing++;
            int n = r.xs().length;
            double[] sx = new double[n], sy = new double[n];
            for (int i = 0; i < n; i++) { sx[i] = f.screenX(r.xs()[i]); sy[i] = f.screenY(r.ys()[i]); }
            gc.setStroke(colour(ShipShapes.IMPORT, LANE_BAND_ALPHA));
            gc.setLineWidth(LANE_BAND_PX);
            gc.setLineDashes((double[]) null);
            gc.strokePolyline(sx, sy, n);
            gc.setStroke(colour(ShipShapes.IMPORT, LANE_DASH_ALPHA));
            gc.setLineWidth(1);
            gc.setLineDashes(5, 6);
            gc.strokePolyline(sx, sy, n);
        }
        gc.setLineDashes((double[]) null);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.SQUARE);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.MITER);
        drawnClock = clock;
        if (level == MapFrame.L2 || crossing == 0 || schedule.callCount() == 0) return;
        boatsLive = true;
        double s = f.scale();
        for (BoatSchedule.Boat b : schedule.frame(x0, y0, x1, y1, t)) {
            double bx = f.screenX(b.x()), by = f.screenY(b.y());
            if (bx < -margin * s || by < -margin * s || bx > f.width() + margin * s || by > f.height() + margin * s) continue;
            int c = ShipShapes.colour(b);
            double[][] hull = ShipShapes.hull(b, s);
            if (ShipShapes.drawnLength(b, s) * s <= ShipShapes.LEAST_PX + 1e-9) {
                fillShape(gc, f, hull, colour(c, b.alpha()));
                continue;
            }
            fillShape(gc, f, hull, colour(b.loaded() ? ShipShapes.HULL_LADEN : ShipShapes.HULL_EMPTY, b.alpha()));
            double[][][] deck = ShipShapes.deck(b, s);
            boolean boxes = b.call().cargo() == Ports.Cargo.CONTAINER;
            for (int k = 0; k < deck.length; k++) {
                fillShape(gc, f, deck[k], boxes ? colour(ShipShapes.boxColour(b, k), b.alpha()) : colour(c, 0.6 * b.alpha()));
            }
            fillShape(gc, f, ShipShapes.bridge(b, s), colour(ShipShapes.BRIDGE, 0.85 * b.alpha()));
            gc.setStroke(colour(c, b.alpha()));
            gc.setLineWidth(1.2);
            gc.strokePolygon(screenXs(f, hull[0]), screenYs(f, hull[1]), hull[0].length);
        }
    }

    /* =====================================================================
       THE TRAINS (0.8.2, batch FR1; spec-freight 4.4, D16)
       ===================================================================== */

    /**
     * The trains over the tiles, from L1 in, as the boats: the month's
     * schedule (TrainSchedule, from the railway's tonnes by kind) on the
     * map's routes (CityMap.trainRoutes()) at the boats' clock. At the near
     * view each vehicle's body and from TrainShapes.MARKS_FROM its kind's
     * mark (TrainShapes); at the middle view a dash of the train's colour
     * along the line. Each vehicle fades in along the world line. While a
     * route crosses the view the frame goes on.
     */
    private void trains(GraphicsContext gc, MapFrame f, int level) {
        if (shownMap == null || (level != MapFrame.L0 && level != MapFrame.L1)) return;
        List<TrainSchedule.Route> routes = shownMap.trainRoutes();
        if (routes.isEmpty()) return;
        double clock = boatClock();
        int m = (int) Math.floor(clock);
        double t = clock - m;
        Game g = ui.game;
        if (trainSchedule == null || trainMonth != m || trainRoutes != routes || trainGameMonth != g.getMonth()) {
            trainSchedule = TrainSchedule.of(m, g.getSectors().freight(), routes);
            trainMonth = m;
            trainRoutes = routes;
            trainGameMonth = g.getMonth();
        }
        if (trainSchedule.callCount() == 0) return;
        double margin = TrainSchedule.Kind.HOPPER.length();
        long x0 = (long) Math.floor(f.plotX(0) - margin), y0 = (long) Math.floor(f.plotY(0) - margin);
        long x1 = (long) Math.ceil(f.plotX(f.width()) + margin), y1 = (long) Math.ceil(f.plotY(f.height()) + margin);
        if (!trainSchedule.crosses(x0, y0, x1, y1)) return;
        drawnClock = clock;
        boatsLive = true;
        double s = f.scale();
        for (TrainSchedule.Train tr : trainSchedule.frame(x0, y0, x1, y1, t)) {
            List<TrainSchedule.Car> cars = TrainSchedule.cars(tr, routes.get(tr.call().route()));
            int c = TrainShapes.colour(tr);
            if (s < TrainShapes.CARS_FROM) {
                // The middle view: a dash along the line, each vehicle's step at its own fade.
                gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                gc.setLineWidth(Math.max(2, TrainShapes.WIDTH * s));
                for (int i = 0; i + 1 < cars.size(); i++) {
                    TrainSchedule.Car a = cars.get(i), b = cars.get(i + 1);
                    gc.setStroke(colour(c, Math.min(a.alpha(), b.alpha())));
                    gc.strokeLine(f.screenX(a.x()), f.screenY(a.y()), f.screenX(b.x()), f.screenY(b.y()));
                }
                gc.setLineCap(javafx.scene.shape.StrokeLineCap.SQUARE);
                continue;
            }
            int mc = TrainShapes.markColour(tr);
            for (TrainSchedule.Car car : cars) {
                fillShape(gc, f, TrainShapes.body(car), colour(car.loco() ? TrainShapes.LOCO : c, car.alpha()));
                if (s < TrainShapes.MARKS_FROM) continue;
                double[][] mark = TrainShapes.mark(car, tr);
                if (mark != null) fillShape(gc, f, mark, colour(mc, car.alpha()));
            }
        }
    }

    /* ----- the trucks (0.8.8, batch FR5; spec-freight 4.7, D21) ----- */

    /** The highways' schedule's salt: apart from every district's, which is its key (CityMap.DistrictRoutes.key()), so two schedules of one month do not hash alike. */
    static final long HIGHWAYS_SALT = 0x7472_7563_6b73_0001L;

    /** The highways' trucks' schedule drawn from: its boat-month, the routes and the game month it was made on. */
    private TruckSchedule truckSchedule;
    private int truckMonth = Integer.MIN_VALUE, truckGameMonth = Integer.MIN_VALUE;
    private List<TruckSchedule.Route> truckRoutes;

    /** An access road's trucks' schedule, with the routes it was made on: made again when its district's routes are another list. */
    private record DistrictTrucks(List<TruckSchedule.Route> routes, TruckSchedule schedule) { }

    /** The districts' schedules by CityMap.DistrictRoutes.key(): thrown out with the highways' when the month, the game month or the map turns. */
    private final Map<Long, DistrictTrucks> districtTrucks = new HashMap<>();

    /** Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the highways' schedule's, else the largest of the districts' drawn. */
    private int trucksStand;

    /**
     * The trucks over the tiles, at the near view alone and while the setting
     * is on (TruckSchedule.drawnAt()): the month's schedule on the highways
     * (CityMap.truckRoutes()) and, for each district in view, on its access
     * roads (CityMap.accessRoutesIn()), a district's schedule taking its
     * routes' share of the city's access roads (CityMap.accessRoadCount()).
     * Each truck is one body of TruckShapes in its colour; while any route
     * crosses the view the frame goes on, as the trains'. Each schedule is
     * made once a month and kept.
     */
    private void trucks(GraphicsContext gc, MapFrame f, int level) {
        if (shownMap == null || !TruckSchedule.drawnAt(level, ui.prefs.isTrucksOnMap())) return;
        double clock = boatClock();
        int m = (int) Math.floor(clock);
        double t = clock - m;
        Game g = ui.game;
        List<TruckSchedule.Route> routes = shownMap.truckRoutes();
        if (truckMonth != m || truckGameMonth != g.getMonth() || truckRoutes != routes) {
            truckSchedule = null;
            districtTrucks.clear();
            truckMonth = m;
            truckGameMonth = g.getMonth();
            truckRoutes = routes;
        }
        // A truck's length past the view's edge, as it is drawn (TruckShapes.MIN_PX), so none pops in at the edge.
        double margin = Math.max(TruckShapes.LENGTH, TruckShapes.MIN_PX / f.scale());
        long x0 = (long) Math.floor(f.plotX(0) - margin), y0 = (long) Math.floor(f.plotY(0) - margin);
        long x1 = (long) Math.ceil(f.plotX(f.width()) + margin), y1 = (long) Math.ceil(f.plotY(f.height()) + margin);
        int highways = 0;
        if (!routes.isEmpty()) {
            if (truckSchedule == null) truckSchedule = TruckSchedule.of(m, g.getSectors().freight(), routes, 1, HIGHWAYS_SALT);
            highways = fillTrucks(gc, f, truckSchedule, x0, y0, x1, y1, t);
        }
        int districts = 0;
        for (CityMap.DistrictRoutes dr : shownMap.accessRoutesIn(x0, y0, x1, y1)) {
            DistrictTrucks dt = districtTrucks.get(dr.key());
            if (dt == null || dt.routes() != dr.routes()) {
                double share = dr.routes().size() / (double) shownMap.accessRoadCount();
                dt = new DistrictTrucks(dr.routes(), TruckSchedule.of(m, g.getSectors().freight(), dr.routes(), share, dr.key()));
                districtTrucks.put(dr.key(), dt);
            }
            districts = Math.max(districts, fillTrucks(gc, f, dt.schedule(), x0, y0, x1, y1, t));
        }
        trucksStand = highways > 0 ? highways : districts;
        if (trucksStand > 0) {
            drawnClock = clock;
            boatsLive = true;
        }
    }

    /** One schedule's trucks in the box filled in their colours, or nothing when it has none or no route of it crosses the view; the loads a truck of it stands for, or 0 when none was drawn. */
    private int fillTrucks(GraphicsContext gc, MapFrame f, TruckSchedule schedule, long x0, long y0, long x1, long y1, double t) {
        if (schedule.callCount() == 0 || !schedule.crosses(x0, y0, x1, y1)) return 0;
        for (TruckSchedule.Truck tk : schedule.frame(x0, y0, x1, y1, t)) {
            fillShape(gc, f, TruckShapes.body(tk, f.scale()), colour(TruckShapes.colour(tk), tk.alpha()));
        }
        return Math.max(1, schedule.stands());
    }

    /** A shape in plots filled on the screen. */
    private static void fillShape(GraphicsContext gc, MapFrame f, double[][] pts, Color c) {
        gc.setFill(c);
        gc.fillPolygon(screenXs(f, pts[0]), screenYs(f, pts[1]), pts[0].length);
    }

    private static double[] screenXs(MapFrame f, double[] xs) {
        double[] out = new double[xs.length];
        for (int i = 0; i < xs.length; i++) out[i] = f.screenX(xs[i]);
        return out;
    }

    private static double[] screenYs(MapFrame f, double[] ys) {
        double[] out = new double[ys.length];
        for (int i = 0; i < ys.length; i++) out[i] = f.screenY(ys[i]);
        return out;
    }

    /** A colour from the model's 0xAARRGGBB at an opacity. */
    static Color colour(int argb, double alpha) {
        return Color.rgb((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, Math.max(0, Math.min(1, alpha)));
    }

    /** The sea routes found on WORKER (CityMap.routesJob()), kept on the FX thread when they are still the city's, and the view drawn again. */
    private void askRoutes() {
        if (routesAsked != null || shownMap == null) return;
        CityMap.RoutesJob j = shownMap.routesJob();
        if (j == null) return;
        routesAsked = j;
        final CityMap map = shownMap;
        WORKER.submit(() -> {
            try {
                j.run();
            } catch (RuntimeException e) {
                System.out.println("The sea routes could not be found: " + e);
            }
            Platform.runLater(() -> {
                if (routesAsked == j) routesAsked = null;
                if (shownMap == map) map.adoptRoutes(j);
                requestDraw();
            });
        });
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

    /** The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). */
    static final double EDGE_ALPHA = MapTiles.EDGE_ALPHA;

    /** The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. */
    static final double WORLD_LINE_ALPHA = LandMap.WORLD_ALPHA;

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
        zoneTints(gc, f, g);
        blockLines(gc, f, market.getLevel());
        List<LandParcel> listing = market.getListing();
        double ax = Math.floorMod((long) Math.floor(f.screenX(0)), 8), ay = Math.floorMod((long) Math.floor(f.screenY(0)), 8);
        ImagePattern pattern = new ImagePattern(hatch.getImage(), ax, ay, 8, 8, false);
        gc.setFillRule(FillRule.EVEN_ODD);
        List<Integer> chosen = expanded ? picks.live(g) : List.of();
        for (int i = 0; i < listing.size(); i++) {
            LandParcel p = listing.get(i);
            double[] box = f.onScreen(p.getX0(), p.getY0(), p.getX1(), p.getY1());
            if (box == null) continue;
            boolean mine = p.getSide() == side;
            boolean lit = (p.getSide() == overSide && p.getPlace() == overPlace) || (p.getSide() == litSide && p.getPlace() == litPlace);
            // The office's pick, and on the expanded map the card's (0.7.137): edged solid.
            boolean picked = (p.getSide() == side && p.getPlace() == place) || chosen.contains(p.getId());
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
        // The city's edge: its runs as one shape, so no pixel is drawn twice where they meet (0.7.69; LandMap.outline()). At the
        // far nodes they draw it themselves since 0.7.128, a pixel wide at every scale (MapTiles.nodePixels()), where a run under
        // a pixel long would drop here.
        if (f.level() != MapFrame.FAR) {
            gc.beginPath();
            for (int i = 0; i < edge[0].length; i++) {
                double[] r = f.runOnScreen(edge[0][i], edge[1][i], edge[2][i], edge[3][i]);
                if (r != null) gc.rect(r[0], r[1], r[2], r[3]);
            }
            gc.setFill(Color.rgb(255, 255, 255, EDGE_ALPHA));
            gc.fill();
        }
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

    /* ----- the zones by district (0.8.0, batch I14; spec-zoning 4: "the map tints a zone's districts") ----- */

    /** The zones' districts as last worked out (CityMap.zoneTint()), and what from: the map, its changes and the zones. */
    private List<CityMap.Tinted> tints = List.of();
    private CityMap tintsMap;
    private long tintsAt = -1;
    private String tintsKey = "";

    /** The city's zones over the ground, under the offers and the city's edge: each zone's districts in its sector's colour at LandMap.ZONE_ALPHA, the districts the map picks for it (CityMap's ZONES BY DISTRICT); nothing with no zone set. */
    private void zoneTints(GraphicsContext gc, MapFrame f, Game g) {
        List<CityMap.ZoneArea> zones = LandMap.zoneAreas(g.getLandManager());
        if (shownMap == null || zones.isEmpty()) return;
        String key = zones.toString();
        if (tintsMap != shownMap || tintsAt != shownMap.changes() || !key.equals(tintsKey)) {
            tints = shownMap.zoneTint(zones);
            tintsMap = shownMap;
            tintsAt = shownMap.changes();
            tintsKey = key;
        }
        for (CityMap.Tinted t : tints) {
            double[] box = f.onScreen(t.x0(), t.y0(), t.x0() + World.DISTRICT, t.y0() + World.DISTRICT);
            if (box == null || t.zone() >= zones.size()) continue;
            gc.setFill(colour(LandMap.zoneColour(zones.get(t.zone()).sector()), LandMap.ZONE_ALPHA));
            gc.fillRect(box[0], box[1], box[2] - box[0], box[3] - box[1]);
        }
    }

    /** The zones' legend entries on the plane (0.8.0): each zone set, by its sector's label. */
    private List<String[]> zoneEntries() {
        Game g = ui.game;
        List<String[]> out = new ArrayList<>();
        if (g == null) return out;
        for (LandMap.Entry e : LandMap.zoneLegend(LandMap.zoneAreas(g.getLandManager()), k -> {
            Sector sec = g.getSectors().byKey(k);
            return sec == null ? k : sec.label();
        })) out.add(new String[] { css(e.colour()), e.words() });
        return out;
    }

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
            newTag(gc, p, x, y, b[3] - b[1]);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
    }

    /** The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. */
    static final String NEW_TAG = "NEW";

    /** ...under the number at (x, y), where the offer's box on the screen is at least two numbers tall (MapFrame.PLACE_LABEL_FROM each). */
    private void newTag(GraphicsContext gc, LandParcel p, double x, double y, double boxH) {
        if (!expanded || boxH < 2 * MapFrame.PLACE_LABEL_FROM || !ui.landScreen.isNew(p)) return;
        double below = Math.rint(y + MapFrame.PLACE_LABEL_FROM);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_CAPTION));
        gc.setStroke(Color.rgb(11, 17, 24, 0.85));
        gc.strokeText(NEW_TAG, x, below);
        gc.setFill(Color.web(Palette.BUILDING));
        gc.fillText(NEW_TAG, x, below);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
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
        scaleBar(gc, f.height(), bar[1], MapFrame.scaleWords(bar[0]));
    }

    /** ...drawn: `px` long at the bottom left of a view `h` high, and its words. */
    private static void scaleBar(GraphicsContext gc, double h, double px, String words) {
        double x = Palette.GAP + 2, y = h - Palette.GAP - 4;
        gc.setStroke(Color.rgb(11, 17, 24, 0.8));
        gc.setLineWidth(4);
        gc.strokeLine(x, y, x + px, y);
        gc.setStroke(Color.rgb(255, 255, 255, 0.9));
        gc.setLineWidth(1.5);
        gc.strokeLine(x, y, x + px, y);
        gc.strokeLine(x, y - 4, x, y + 1);
        gc.strokeLine(x + px, y - 4, x + px, y + 1);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_LABEL));
        gc.setLineWidth(3);
        gc.setStroke(Color.rgb(11, 17, 24, 0.85));
        gc.strokeText(words, x + px + 6, y + 4);
        gc.setFill(Color.rgb(255, 255, 255, 0.95));
        gc.fillText(words, x + px + 6, y + 4);
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
       THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3)
       ===================================================================== */

    /** The ground's colours on the globe: the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). */
    private static final int[] DIMMED = GlobePicture.colours(true);

    /** A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). */
    static final int PIECE_PX = MapFrame.NODE_PX;

    /** The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). */
    static final double RING_PX = 6;

    /** The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. */
    static final int NODE_SAMPLE_PX = 16;

    /** The globe drawn while it was half its resolution (the view on the move). */
    private boolean globeHalf;

    /** Whether the expanded map draws the globe: on an Earth world, wider than the handoff. */
    private boolean onGlobe() { return expanded && globe != null && globe.tier() != GlobeFrame.PLANE; }

    /** ...or lays its picture over the plane's in the crossfade's notch. */
    private boolean globeShown() { return onGlobe() || (expanded && globe != null && GlobeFrame.crossfade(globe.kmPx()) > 0); }

    /** A new city, or another world: no globe until the map is over the window on Earth again. */
    private void resetGlobe() {
        resetGlobePicture();
        globe = null;
        globeKey = null;
        globeData = null;
        reachFor = Long.MIN_VALUE;
    }

    /** What the globe drew from and drew: another map, or another view of the world. */
    private void resetGlobePicture() {
        globeImg = null;
        globeDrawn = null;
        globeMode = -1;
        globeHalf = false;
        patch = null;
        pieceCls = null;
        pieceView = null;
        pieceGen++;
        carryPx = null;
        carryView = null;
        globeNodesFor = null;
    }

    /** The globe made for the city's world while the map is over the window on Earth (none on a generated world), its reach found again when the land or the offers move, and the planet's picture asked for. */
    private void globeUp(CityLand land, LandMarket market, long landKey) {
        World.Key key = land.key();
        if (!key.earth()) {
            if (globe != null) resetGlobe();
            return;
        }
        if (!expanded || big == null) return;
        if (globe == null || !key.equals(globeKey)) {
            resetGlobe();
            globe = new GlobeFrame(big.width(), big.height(), key.lat(), key.lon());
            globeKey = key;
            globeData = GlobePicture.dataOf(key);
            globe.fromPlane(big.centreX(), big.centreY(), big.scale());
            GlobePicture.prepare(globeData, () -> Platform.runLater(() -> {
                inputs++;
                requestDraw();
            }));
        }
        if (landKey != reachFor) {
            reachFor = landKey;
            globe.setReach(GlobeFrame.reachKm(land, market));
            follow();
        }
    }

    /** The view moved on the globe: at the plane's scales the plane's frame follows it; the view drawn again. */
    private void follow() {
        if (globe != null && big != null && globe.tier() == GlobeFrame.PLANE) big.set(globe.planeX(), globe.planeY(), globe.planeScale());
        requestDraw();
    }

    /** The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws; null where it draws none. Kept for the view. */
    private long[] globeNodes(GlobeFrame v) {
        if (globeNodesFor != null && globeNodesFor.sameView(v)) return globeNodesAt;
        globeNodesFor = v.copy(v.width(), v.height(), v.kmPx());
        long plots = MapFrame.nodePlotsAt(v.planeScale());
        double span = MapFrame.NODE_PX * (double) plots, x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        double[] xy = new double[2];
        double w = v.width(), h = v.height();
        for (double sy = 0.5; sy < h + NODE_SAMPLE_PX; sy += NODE_SAMPLE_PX) {
            for (double sx = 0.5; sx < w + NODE_SAMPLE_PX; sx += NODE_SAMPLE_PX) {
                double px = Math.min(sx, w - 0.5), py = Math.min(sy, h - 0.5);
                if (!v.drawsHold(px, py) || !v.planeAt(px, py, xy)) continue;
                x0 = Math.min(x0, xy[0]);
                y0 = Math.min(y0, xy[1]);
                x1 = Math.max(x1, xy[0]);
                y1 = Math.max(y1, xy[1]);
            }
        }
        if (x0 > x1) return globeNodesAt = null;
        double pad = NODE_SAMPLE_PX / v.planeScale();
        long most = (World.SIDE - 1) / (long) span;
        globeNodesAt = new long[] { plots,
                Math.max(0, (long) Math.floor((x0 - pad) / span)), Math.min(most, (long) Math.floor((x1 + pad) / span)),
                Math.max(0, (long) Math.floor((y0 - pad) / span)), Math.min(most, (long) Math.floor((y1 + pad) / span)) };
        return globeNodesAt;
    }

    private GlobeFrame globeNodesFor;
    private long[] globeNodesAt;

    /** A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here until the deadline, and the district pictures they lack. True while there is more. */
    private boolean paintGlobe(long deadline) {
        if (globe.tier() != GlobeFrame.GROUND || shownMap == null) return false;
        long[] r = globeNodes(globe);
        if (r == null) return false;
        nodes.most = Math.max(nodes.most, (r[2] - r[1] + 1) * (r[4] - r[3] + 1));
        MapFrame pf = new MapFrame(globe.width(), globe.height());
        pf.set(globe.planeX(), globe.planeY(), globe.planeScale());
        picturesWanted.clear();
        try {
            return paintNodes(r[0], r[1], r[2], r[3], r[4], deadline, shownMap.picturesKept());
        } finally {
            askPictures(pf);
        }
    }

    /** A far node coloured (the plane's or the globe's): where the globe's still picture shows it, laid over again next frame. */
    private void nodeColoured(long x0, long y0, long span) {
        if (globe == null || !expanded || globeDrawn == null || globeHalf) return;
        double[] box = null;
        for (int k = 0; k < 9; k++) {
            double[] p = globe.screenOfPlot(x0 + (k % 3) * span / 2.0, y0 + (k / 3) * span / 2.0);
            if (p == null) {
                box = new double[] { 0, 0, globe.width(), globe.height() };
                break;
            }
            box = box == null ? new double[] { p[0], p[1], p[0], p[1] }
                    : new double[] { Math.min(box[0], p[0]), Math.min(box[1], p[1]), Math.max(box[2], p[0]), Math.max(box[3], p[1]) };
        }
        addPatch(box[0] - 2, box[1] - 2, box[2] + 2, box[3] + 2);
    }

    /** The still picture's box to be drawn again, grown to take in another. */
    private void addPatch(double x0, double y0, double x1, double y1) {
        x0 = Math.max(0, Math.floor(x0));
        y0 = Math.max(0, Math.floor(y0));
        x1 = Math.min(globe.width(), Math.ceil(x1));
        y1 = Math.min(globe.height(), Math.ceil(y1));
        if (x1 <= x0 || y1 <= y0) return;
        patch = patch == null ? new double[] { x0, y0, x1, y1 }
                : new double[] { Math.min(patch[0], x0), Math.min(patch[1], y0), Math.max(patch[2], x1), Math.max(patch[3], y1) };
    }

    /**
     * The globe's picture of the view, drawn again when the view, the way it
     * is drawn or what it is drawn from moved: at half resolution while the
     * view moves (its frame a copy of half the size at twice the km a
     * pixel), at its own once it holds still - in the GROUND tier then the
     * pieces past the plane asked for, and the last whole picture kept to
     * carry. A piece or a node come in is laid over the still picture alone.
     */
    private void composeGlobe(int mode) {
        GlobeFrame v = globe;
        int w = (int) v.width(), h = (int) v.height();
        boolean moved = globeDrawn == null || globeMode != mode || !globeDrawn.sameView(v);
        if (moved) {
            if (globeDrawn != null && globeMode == MODE_GROUND && !globeHalf && globeImg != null) {
                carryPx = globePx.clone();
                carryView = globeDrawn;
            }
            globeDrawn = v.copy(w, h, v.kmPx());
            globeMode = mode;
            globeHalf = true;
            patch = null;
            globeInputs = inputs;
            int hw = (w + 1) / 2, hh = (h + 1) / 2;
            drawGlobePart(v.copy(hw, hh, v.kmPx() * 2), mode, 0, hw, 0, hh);
            return;
        }
        if (globeHalf || globeInputs != inputs) {
            globeHalf = false;
            patch = null;
            globeInputs = inputs;
            drawGlobePart(globeDrawn, mode, 0, w, 0, h);
            if (mode == MODE_GROUND) askPieces(globeDrawn);
            return;
        }
        if (patch != null) {
            double[] b = patch;
            patch = null;
            drawGlobePart(globeDrawn, mode, (int) b[0], (int) b[2], (int) b[1], (int) b[3]);
        }
    }

    /** The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image. */
    private void drawGlobePart(GlobeFrame t, int mode, int i0, int i1, int j0, int j1) {
        int w = (int) t.width(), h = (int) t.height();
        boolean whole = i0 == 0 && j0 == 0 && i1 == w && j1 == h;
        if (globePx.length != w * h) globePx = new int[w * h];
        if (globeImg == null || (int) globeImg.getWidth() != w || (int) globeImg.getHeight() != h) {
            globeImg = new WritableImage(w, h);
            if (!whole) {
                i0 = 0; j0 = 0; i1 = w; j1 = h;
            }
        }
        final int[] px = globePx;
        GlobePicture pic = globeData == null ? null : GlobePicture.best(globeData);
        if (mode == MODE_PICTURE) {
            if (pic == null) java.util.Arrays.fill(px, GlobePicture.SPACE);
            else pic.frame(t, DIMMED, GlobePicture.SPACE, px, GlobePicture.cores());
            i0 = 0; j0 = 0; i1 = w; j1 = h;
        } else {
            long[] r = globeNodes(globe);
            long plots = MapFrame.nodePlotsAt(globe.planeScale());
            Map<Long, int[]> snap = new HashMap<>();
            if (r != null) {
                for (long ny = r[3]; ny <= r[4]; ny++) {
                    for (long nx = r[1]; nx <= r[2]; nx++) {
                        Img img = nodes.get(nodeKey(plots, nx, ny));
                        if (img != null && img.pixels != null) snap.put(nodeKey(plots, nx, ny), img.pixels);
                    }
                }
            }
            GlobeFrame.NodePixels np = (nx, ny) -> snap.get(nodeKey(plots, nx, ny));
            GlobeFrame.Beyond beyond = mode == MODE_FADE ? (i, j, vx, vy, vz) -> 0 : beyond(t, pic);
            final int a0 = i0, a1 = i1, b0 = j0;
            GlobePicture.bands(j1 - j0, GlobePicture.cores(), (k0, k1) -> t.ground(plots, np, beyond, GlobePicture.SPACE, px, w, a0, a1, b0 + k0, b0 + k1));
        }
        globeImg.getPixelWriter().setPixels(i0, j0, i1 - i0, j1 - j0, PixelFormat.getIntArgbInstance(), px, j0 * w + i0, w);
    }

    /** Past the plane, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. */
    private GlobeFrame.Beyond beyond(GlobeFrame t, GlobePicture pic) {
        final int w = (int) t.width();
        final byte[] pcs = pieceView != null && pieceView.sameView(t) ? pieceCls : null;
        final int[] cpx = carryPx;
        final GlobeFrame.Carry carry = cpx != null && carryView != null ? t.carryFrom(carryView) : null;
        return (i, j, vx, vy, vz) -> {
            if (pcs != null) {
                byte c = pcs[j * w + i];
                if (c >= 0) return DIMMED[c];
            }
            if (carry != null) {
                int k = carry.pixel(vx, vy, vz);
                if (k >= 0 && cpx[k] != 0) return cpx[k];
            }
            if (pic == null) return GlobePicture.SPACE;
            return DIMMED[pic.at(GlobeFrame.latOf(vx, vy, vz), t.lonOf(vx, vy))];
        };
    }

    /** The globe's picture on the canvas: at its own size, or doubled while it was drawn at half (no smoothing - the far nodes' way). */
    private void drawGlobeImage(GraphicsContext gc) {
        if (globeImg == null) return;
        gc.setImageSmoothing(false);
        double f = globeHalf ? 2 : 1;
        gc.drawImage(globeImg, 0, 0, globeImg.getWidth() * f, globeImg.getHeight() * f);
        gc.setImageSmoothing(true);
    }

    /**
     * The GROUND tier's pieces past the plane for a view come to rest: the
     * far rule through the camera (GlobeFrame.groundPiece()) a PIECE_PX
     * square at a time on WORKER, from the middle out, each one with some
     * of its pixels on the planet and past where the plane draws - and only
     * while the view is still that one.
     */
    private void askPieces(GlobeFrame view) {
        if (pieceView != null && pieceView.sameView(view)) return;
        int w = (int) view.width(), h = (int) view.height();
        final GlobeFrame pv = view.copy(w, h, view.kmPx());
        pieceView = pv;
        pieceCls = new byte[w * h];
        java.util.Arrays.fill(pieceCls, (byte) -1);
        final int gen = ++pieceGen;
        final World.Key key = globeKey;
        List<int[]> asked = new ArrayList<>();
        for (int y0 = 0; y0 < h; y0 += PIECE_PX) {
            for (int x0 = 0; x0 < w; x0 += PIECE_PX) {
                int pw = Math.min(PIECE_PX, w - x0), ph = Math.min(PIECE_PX, h - y0);
                boolean plane = true, planet = false;
                for (int k = 0; k < 9; k++) {
                    double sx = x0 + 0.5 + (k % 3) * (pw - 1) / 2.0, sy = y0 + 0.5 + (k / 3) * (ph - 1) / 2.0;
                    plane &= pv.drawsHold(sx, sy);
                    planet |= pv.onPlanet(sx, sy);
                }
                if (planet && !plane) asked.add(new int[] { x0, y0, pw, ph });
            }
        }
        double cx = w / 2.0, cy = h / 2.0;
        asked.sort((a, b) -> Double.compare(Math.hypot(a[0] + a[2] / 2.0 - cx, a[1] + a[3] / 2.0 - cy),
                Math.hypot(b[0] + b[2] / 2.0 - cx, b[1] + b[3] / 2.0 - cy)));
        for (int[] q : asked) {
            WORKER.submit(() -> {
                if (gen != pieceGen) return;
                byte[] b;
                try {
                    b = pv.groundPiece(key, q[0], q[1], q[2], q[3]);
                } catch (RuntimeException e) {
                    System.out.println("The globe's ground could not be drawn: " + e);
                    return;
                }
                Platform.runLater(() -> pieceIn(gen, q, b));
            });
        }
    }

    /** A piece back from WORKER: kept while its view is the one shown, and laid over the still picture. */
    private void pieceIn(int gen, int[] q, byte[] b) {
        if (gen != pieceGen || pieceCls == null || globe == null) return;
        int w = (int) pieceView.width();
        for (int r = 0; r < q[3]; r++) System.arraycopy(b, r * q[2], pieceCls, (q[1] + r) * w + q[0], q[2]);
        if (globeDrawn != null && !globeHalf && globeDrawn.sameView(pieceView)) addPatch(q[0], q[1], q[0] + q[2], q[1] + q[3]);
        requestDraw();
    }

    /** The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its edge through the camera in the PICTURE tier once it is EDGE_FROM_PX across, its ring - and the scale bar. */
    private void drawGlobe() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = globe.width(), h = globe.height();
        gc.setFill(colour(TileRaster.VOID));
        gc.fillRect(0, 0, w, h);
        boatsLive = false;
        trucksStand = 0;
        int tier = globe.tier();
        composeGlobe(tier == GlobeFrame.PICTURE ? MODE_PICTURE : MODE_GROUND);
        drawGlobeImage(gc);
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        shapes(land, market);
        if (tier == GlobeFrame.GROUND) globeOffers(gc, land, market);
        else globeEdge(gc, land);
        ring(gc, land);
        if (globeData != null && GlobePicture.best(globeData) == null) {
            gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
            gc.setFill(Color.web(Palette.TEXT_MUTED));
            gc.fillText(DRAWING_EARTH, Palette.GAP + 2, Palette.GAP + 14);
        }
        globeScaleBar(gc);
        drawRings(gc);
        drawBox(gc);
        placeBuyCard();
    }

    /** The note while the planet's picture is made, the first time an Earth map is expanded. */
    static final String DRAWING_EARTH = "Drawing the Earth…";

    /** A shape of plots through the camera: its corners on the screen, or null where one is not seen. */
    private double[][] onGlobe(double[] xs, double[] ys) {
        double[][] out = new double[2][xs.length];
        for (int k = 0; k < xs.length; k++) {
            double[] p = globe.screenOfPlot(xs[k], ys[k]);
            if (p == null) return null;
            out[0][k] = p[0];
            out[1][k] = p[1];
        }
        return out;
    }

    /** The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its far level - hatched, edged in pink, the office's side bright, the one lit or picked, numbered where the box holds a number. */
    private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market) {
        ImagePattern pattern = new ImagePattern(hatch.getImage(), 0, 0, 8, 8, false);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
        List<Integer> chosen = picks.live(ui.game);
        for (LandParcel p : market.getListing()) {
            double[][] q = onGlobe(new double[] { p.getX0(), p.getX1(), p.getX1(), p.getX0() }, new double[] { p.getY0(), p.getY0(), p.getY1(), p.getY1() });
            if (q == null) continue;
            double bx0 = Math.min(Math.min(q[0][0], q[0][1]), Math.min(q[0][2], q[0][3])), bx1 = Math.max(Math.max(q[0][0], q[0][1]), Math.max(q[0][2], q[0][3]));
            double by0 = Math.min(Math.min(q[1][0], q[1][1]), Math.min(q[1][2], q[1][3])), by1 = Math.max(Math.max(q[1][0], q[1][1]), Math.max(q[1][2], q[1][3]));
            if (bx1 < 0 || by1 < 0 || bx0 > globe.width() || by0 > globe.height()) continue;
            boolean mine = p.getSide() == side;
            boolean lit = (p.getSide() == overSide && p.getPlace() == overPlace) || (p.getSide() == litSide && p.getPlace() == litPlace);
            boolean picked = (p.getSide() == side && p.getPlace() == place) || chosen.contains(p.getId());
            gc.setFill(Color.rgb(12, 18, 26, 0.24));
            gc.fillPolygon(q[0], q[1], 4);
            gc.setFill(pattern);
            gc.fillPolygon(q[0], q[1], 4);
            if (lit || picked) {
                gc.setFill(Color.web(Palette.BUILDING, lit ? 0.34 : 0.22));
                gc.fillPolygon(q[0], q[1], 4);
            }
            gc.setStroke(Color.web(OFFER_EDGE, mine || lit ? 0.95 : 0.4));
            gc.setLineWidth(picked ? 2 : 1);
            if (picked) gc.setLineDashes((double[]) null);
            else gc.setLineDashes(5, 4);
            gc.strokePolygon(q[0], q[1], 4);
            gc.setLineDashes((double[]) null);
            if (bx1 - bx0 >= MapFrame.PLACE_LABEL_FROM && by1 - by0 >= MapFrame.PLACE_LABEL_FROM) {
                String words = LandMap.placeLabel(p);
                double x = Math.rint((bx0 + bx1) / 2), y = Math.rint((by0 + by1) / 2);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.setLineWidth(3);
                gc.setStroke(Color.rgb(11, 17, 24, 0.85));
                gc.strokeText(words, x, y);
                gc.setFill(Color.rgb(255, 255, 255, p.getSide() == side ? 0.95 : 0.6));
                gc.fillText(words, x, y);
                newTag(gc, p, x, y, by1 - by0);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BASELINE);
            }
        }
        double[] c = LandMap.centre(land);
        double[][] q = onGlobe(new double[] { c[0], c[2], c[2], c[0] }, new double[] { c[1], c[1], c[3], c[3] });
        if (q != null) {
            gc.setStroke(Color.rgb(255, 255, 255, 0.3));
            gc.setLineWidth(1);
            gc.setLineDashes(3, 4);
            gc.strokePolygon(q[0], q[1], 4);
            gc.setLineDashes((double[]) null);
        }
    }

    /** The city's edge through the camera in the PICTURE tier (spec-globe 3.7), once the city's ground is EDGE_FROM_PX across on the screen: each run of LandMap.outline() a line, white at the edge's alpha. */
    private void globeEdge(GraphicsContext gc, CityLand land) {
        LandGrid grid = land.grid();
        if (grid.ownedPlots() == 0 || edge[0].length == 0) return;
        double[] a = globe.screenOfPlot(grid.minX(), grid.minY()), b = globe.screenOfPlot(grid.maxX(), grid.maxY());
        if (a == null || b == null || Math.hypot(b[0] - a[0], b[1] - a[1]) < GlobeFrame.EDGE_FROM_PX) return;
        gc.setStroke(Color.rgb(255, 255, 255, EDGE_ALPHA));
        gc.setLineWidth(1);
        for (int i = 0; i < edge[0].length; i++) {
            double[] p = globe.screenOfPlot(edge[0][i], edge[1][i]), q = globe.screenOfPlot(edge[2][i], edge[3][i]);
            if (p != null && q != null) gc.strokeLine(p[0], p[1], q[0], q[1]);
        }
    }

    /** The city's ring at its centre, at every scale on the globe (spec-globe 3.7): RING_PX round, the founding globe's light blue on a dark halo. */
    private void ring(GraphicsContext gc, CityLand land) {
        double[] c = LandMap.centre(land);
        double[] p = globe.screenOfPlot((c[0] + c[2]) / 2, (c[1] + c[3]) / 2);
        if (p == null) return;
        gc.setStroke(Color.rgb(11, 17, 24, 0.85));
        gc.setLineWidth(3.5);
        gc.strokeOval(p[0] - RING_PX, p[1] - RING_PX, 2 * RING_PX, 2 * RING_PX);
        gc.setStroke(colour(LandMap.RING));
        gc.setLineWidth(1.5);
        gc.strokeOval(p[0] - RING_PX, p[1] - RING_PX, 2 * RING_PX, 2 * RING_PX);
    }

    /** The scale bar on the globe: GlobeFrame.scaleBar() at the view's centre, its words saying so. */
    private void globeScaleBar(GraphicsContext gc) {
        double[] bar = globe.scaleBar(SCALE_BAR_PX);
        scaleBar(gc, globe.height(), bar[1], GlobeFrame.scaleWords(bar[0]));
    }

    /** The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it. */
    private void globeHover(double sx, double sy) {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        int os = -1, op = -1;
        double[] xy = new double[2];
        if (globe.planeHolds(sx, sy) && globe.plotAt(sx, sy, xy)) {
            LandMap.Pick p = LandMap.pick(land, market, (long) Math.floor(xy[0]), (long) Math.floor(xy[1]));
            if (p.owner() == LandMap.OFFER) {
                os = p.side();
                op = p.place();
            }
        }
        if (os != overSide || op != overPlace) {
            overSide = os;
            overPlace = op;
            dirty = true;
        }
        List<String> lines = new ArrayList<>(LandMap.globeHoverWords(globe, land, market, sx, sy, drawnAt(sx, sy), CARD_FIELDS));
        // An offer drawn here - the GROUND tier's - is bought with a click (0.7.137).
        if (os >= 0 && canPick()) lines.add(CLICK_TO_BUY);
        showCard(lines, sx, sy, globe.width(), globe.height());
    }

    /** The ground drawn at a screen point on the globe, past the plane: the GROUND tier's piece where one is in, else the planet's picture's texel. */
    private byte drawnAt(double sx, double sy) {
        int i = (int) sx, j = (int) sy, w = (int) globe.width(), h = (int) globe.height();
        if (pieceCls != null && pieceView != null && pieceView.sameView(globe) && i >= 0 && j >= 0 && i < w && j < h) {
            byte c = pieceCls[j * w + i];
            if (c >= 0) return c;
        }
        GlobePicture pic = globeData == null ? null : GlobePicture.best(globeData);
        double[] ll = globe.latLonAt(sx, sy);
        return pic != null && ll != null ? pic.at(ll[0], ll[1]) : World.SALT;
    }

    /* ----- the keys (0.7.135; spec-globe 3.11) ----- */

    /** A frame of the keys held: the expanded map panned by their velocity over the time since the last frame (MapFrame.keyPan(), GlobeFrame.keyPan()); all let go when the window loses the focus, whose key-ups it would not hear. True while one is held. */
    private boolean keys() {
        if (!expanded || big == null || !(keyW || keyA || keyS || keyD)) {
            keyNanos = 0;
            return false;
        }
        javafx.stage.Window win = canvas.getScene() == null ? null : canvas.getScene().getWindow();
        if (win != null && !win.isFocused()) {
            keyW = keyA = keyS = keyD = false;
            keyNanos = 0;
            return false;
        }
        long now = System.nanoTime();
        double dt = keyNanos == 0 ? 0 : (now - keyNanos) / 1e9;
        keyNanos = now;
        double[] v = MapFrame.keyVelocity(keyW, keyA, keyS, keyD);
        if (globe != null) {
            globe.keyPan(v, dt);
            follow();
        } else big.keyPan(v, dt);
        if (card != null) card.setVisible(false);
        dirty = true;
        return true;
    }

    /** W, A, S or D pressed (`down`) or let go while the map is over the window: from UserInterface's key filter. True when it was one of them. */
    boolean panKey(KeyCode k, boolean down) {
        switch (k) {
            case W: keyW = down; break;
            case A: keyA = down; break;
            case S: keyS = down; break;
            case D: keyD = down; break;
            default: return false;
        }
        if (down && keyNanos == 0) keyNanos = System.nanoTime();
        requestDraw();
        return true;
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
            pressX = lastX = boxX = e.getX();
            pressY = lastY = boxY = e.getY();
            moved = 0;
            // Shift held, a drag draws a box rather than panning (0.7.137, B7), where the offers are drawn to be picked.
            boxing = expanded && e.isShiftDown() && canPick();
            if (expanded) canvas.requestFocus();
        });
        canvas.setOnMouseDragged(e -> {
            if (!pressed) return;
            moved = Math.max(moved, Math.hypot(e.getX() - pressX, e.getY() - pressY));
            if (boxing) {
                boxX = e.getX();
                boxY = e.getY();
                requestDraw();
            } else if (expanded) {
                if (globe != null) {
                    globe.drag(lastX, lastY, e.getX(), e.getY());
                    follow();
                } else big.pan(e.getX() - lastX, e.getY() - lastY);
                requestDraw();
            }
            lastX = e.getX();
            lastY = e.getY();
        });
        canvas.setOnMouseReleased(e -> {
            boolean click = pressed && moved < CLICK_SLOP, box = pressed && boxing && !click;
            pressed = false;
            boxing = false;
            if (box) boxed(pressX, pressY, e.getX(), e.getY());
            else if (click) click(e.getX(), e.getY(), e.isShiftDown());
        });
        canvas.setOnScroll(e -> {
            if (!expanded) return;
            double notches = e.getDeltaY() / 40.0;
            if (notches == 0) return;
            if (globe != null) {
                globe.zoomNotches(e.getX(), e.getY(), notches);
                follow();
            } else big.zoomNotches(e.getX(), e.getY(), notches);
            if (card != null) card.setVisible(false);
            requestDraw();
            e.consume();
        });
        canvas.setOnKeyPressed(e -> {
            if (!expanded) return;
            KeyCode k = e.getCode();
            double cx = big.width() / 2, cy = big.height() / 2;
            if (k == KeyCode.PLUS || k == KeyCode.EQUALS || k == KeyCode.ADD) zoomBig(cx, cy, 1);
            else if (k == KeyCode.MINUS || k == KeyCode.SUBTRACT) zoomBig(cx, cy, -1);
            else if (k == KeyCode.DIGIT0 || k == KeyCode.NUMPAD0) fitBig();
            else return;
            requestDraw();
            e.consume();
        });
    }

    /** The wheel's or + and -'s notches at a point of the expanded map: the globe's view on Earth, the plane's elsewhere. */
    private void zoomBig(double sx, double sy, double notches) {
        if (globe != null) {
            globe.zoomNotches(sx, sy, notches);
            follow();
        } else big.zoomNotches(sx, sy, notches);
    }

    /**
     * A click: an offer's free ground picks it, anywhere else picks the side
     * it lies on - on the globe only where the city's plane is drawn
     * (0.7.135). On the expanded map, where the offers are drawn to be
     * picked (canPick(), since 0.7.137): a click on an offer opens its card
     * beside it (B1), shift adds it to the card's picks or drops it (B3) and
     * leaves the office's pick alone; a click on the city or bare ground
     * closes the card, and shift there does nothing.
     */
    private void click(double sx, double sy, boolean shift) {
        MapFrame f = frame();
        Game g = ui.game;
        long px = (long) Math.floor(f.plotX(sx)), py = (long) Math.floor(f.plotY(sy));
        if (onGlobe()) {
            double[] xy = new double[2];
            if (!globe.planeHolds(sx, sy) || !globe.plotAt(sx, sy, xy)) {
                if (expanded && !shift) closeCard();
                return;
            }
            px = (long) Math.floor(xy[0]);
            py = (long) Math.floor(xy[1]);
        }
        LandMap.Pick p = LandMap.pick(g.getCityLand(), g.getLandManager().getMarket(), px, py);
        boolean offer = p.owner() == LandMap.OFFER;
        if (expanded) {
            if (offer && canPick()) {
                bought = null;
                if (shift) {
                    if (picks.toggle(g, p.offer().getId())) cardAt = box(p.offer());
                    showBuyCard();
                    requestDraw();
                    return;
                }
                picks.only(g, p.offer().getId());
                cardAt = box(p.offer());
                showBuyCard();
            } else if (shift) {
                return;
            } else {
                closeCard();
            }
        }
        side = p.side();
        place = offer ? p.place() : -1;
        picker.picked(side, place);
        requestDraw();
    }

    /** What the pointer is over: the offer it lights, and the words - one line on the small map, a card on the expanded one. */
    private void hover(double sx, double sy) {
        if (onGlobe()) {
            globeHover(sx, sy);
            return;
        }
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
        if (tiles != null && f.level() == MapFrame.L0 && tiles.ready(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE))) {
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
        // The model's card (since 0.7.135 LandMap.hoverWords() itself, as the globe's is): the ground, whose, on Earth (0.7.119)
        // where it is by the nearest place and its geology, the fields - with at L0 the building or road first.
        List<String> lines = new ArrayList<>();
        if (what != null) lines.add(what);
        lines.addAll(LandMap.hoverWords(land, market, x, y, CARD_FIELDS));
        // ...and over an offer, how to buy it (0.7.137, board 4).
        if (os >= 0 && canPick()) lines.add(CLICK_TO_BUY);
        showCard(lines, sx, sy, f.width(), f.height());
    }

    /** The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. */
    static final String CLICK_TO_BUY = "Click to buy it here";

    /** The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - CLICK_TO_BUY in the accent's light blue. */
    private void showCard(List<String> lines, double sx, double sy, double vw, double vh) {
        if (lines.isEmpty()) {
            card.setVisible(false);
            return;
        }
        card.getChildren().clear();
        for (int i = 0; i < lines.size(); i++) {
            Label l = new Label(lines.get(i));
            l.setStyle(i == 0 ? Palette.strong(Palette.SIZE_BODY, Palette.TEXT)
                    : CLICK_TO_BUY.equals(lines.get(i)) ? Palette.strong(Palette.SIZE_LABEL, Palette.ACCENT_LIGHT)
                    : Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            card.getChildren().add(l);
        }
        card.applyCss();
        card.autosize();
        double cw = card.prefWidth(-1), ch = card.prefHeight(-1);
        double left = sx + 14, top = sy + 14;
        if (left + cw > vw - 10) left = Math.max(10, sx - cw - 14);
        if (top + ch > vh - 10) top = Math.max(10, vh - ch - 10);
        card.setTranslateX(left);
        card.setTranslateY(top);
        card.setVisible(true);
    }

    /** The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). */
    static final int CARD_FIELDS = LandMap.CARD_FIELDS;

    /** A model building's name, by its type id: the catalogue's. */
    private String nameOf(int id) {
        BuildingsTemplate t = ui.game.getBuildingManager().getTemplate(id);
        return t == null ? null : t.getName();
    }

    /* =====================================================================
       BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3)

       Jerus (2026-10-08): "click brings up menu, and to issue bond if
       doesnt have cash"; "you should be able to drag and select in the map
       to buy all the land parcel or specific ones". On the expanded map,
       where the offers are drawn to be picked - the plane at every scale,
       and on Earth the globe's GROUND tier, not its PICTURE tier, which
       draws none (canPick()) - a click on an offer opens its card beside it
       (B1), shift adds one or drops it (B3), Shift and a drag draws a box
       that takes every offer whose free ground it meets (B7:
       LandMap.boxPicks(), through the plane's frame or the globe's camera),
       and "Select every offer" or Ctrl+A takes them all. Only listed offers
       (B4). The card is the land office's (LandScreen.mapCardNode()), its
       figures LandMap.card()'s, which MapCheck 14 holds against the office's
       rows. Buy, or Enter (B2), buys them as the office's Buy does
       (LandScreen.buyOrFund()): the office drawn again under the map, which
       stays. Short, the card's bond is the funding page's first offer
       (LandScreen.offers()), issued, then bought on; "All the ways to pay"
       opens the funding page, which comes back to the map at the view it
       left (B5, back()). Bought, the card becomes the receipt by the ground,
       the place's next offer drawn NEW and the head's cash moved. The picks
       are the view's (LandMap.Picks): a month, a load and a collapse drop
       them. Esc closes the card, a second Esc the map. The small map in the
       office stays as it was (B6).
       ===================================================================== */

    /** A purchase from the card: the offers found listed when it was asked for, their places ({side, place} each), the card's title for them and the ground they stand on, in plots. */
    record Bought(List<Integer> ids, List<int[]> places, String title, double[] at) { }

    /** The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). */
    static final double CARD_GAP = 14;

    /** ...and at least this far inside the view: 10, the hover card's (showCard()). */
    static final double CARD_MARGIN = 10;

    /** The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. */
    static final double BOX_FILL_ALPHA = 0.12;

    /** Whether the offers are drawn to be picked where the expanded map looks (star M6): on the plane at every scale, and on Earth in the globe's GROUND tier, where globeOffers() draws them - not its PICTURE tier, which draws none. */
    private boolean canPick() {
        return expanded && (globe == null || globe.tier() != GlobeFrame.PICTURE);
    }

    /** A plot of the city's plane on the screen, as the expanded map draws it now: through the globe's camera, or the plane's frame. */
    private LandMap.ToScreen toScreen() {
        if (onGlobe()) {
            GlobeFrame view = globe;
            return view::screenOfPlot;
        }
        return LandMap.onScreen(frame());
    }

    /** An offer's box in plots, {x0, y0, x1, y1}. */
    private static double[] box(LandParcel p) {
        return new double[] { p.getX0(), p.getY0(), p.getX1(), p.getY1() };
    }

    /** ...and the bounds of several, or null with none. */
    private static double[] bounds(List<LandParcel> ps) {
        if (ps.isEmpty()) return null;
        double[] b = box(ps.get(0));
        for (LandParcel p : ps) {
            b[0] = Math.min(b[0], p.getX0());
            b[1] = Math.min(b[1], p.getY0());
            b[2] = Math.max(b[2], p.getX1());
            b[3] = Math.max(b[3], p.getY1());
        }
        return b;
    }

    /** The listed offers among these ids, in their order. */
    private List<LandParcel> listed(List<Integer> ids) {
        LandMarket market = ui.game.getLandManager().getMarket();
        List<LandParcel> out = new ArrayList<>();
        for (int id : ids) {
            LandParcel p = market.find(id);
            if (p != null) out.add(p);
        }
        return out;
    }

    /** Shift and a drag let go (B7): every offer whose free ground the box meets added to the card's picks, in the office's order - the cheapest dry ground first - the card beside them. */
    private void boxed(double x0, double y0, double x1, double y1) {
        Game g = ui.game;
        List<Integer> took = LandMap.boxPicks(g.getCityLand(), g.landShelf(), toScreen(), x0, y0, x1, y1);
        if (!took.isEmpty()) {
            bought = null;
            picks.add(g, took);
            cardAt = bounds(listed(took));
            showBuyCard();
        }
        requestDraw();
    }

    /** "Select every offer" in the head, or Ctrl+A (B7): every offer listed onto the card, the office's best first, the card beside the best (star M6). */
    void selectAll() {
        if (!expanded) return;
        Game g = ui.game;
        List<LandParcel> shelf = g.landShelf();
        if (shelf.isEmpty()) return;
        List<Integer> ids = new ArrayList<>();
        for (LandParcel p : shelf) ids.add(p.getId());
        bought = null;
        picks.add(g, ids);
        cardAt = box(shelf.get(0));
        showBuyCard();
        requestDraw();
    }

    /** The card drawn again for the picks as they stand, or put away with none listed. */
    private void showBuyCard() {
        if (buyBox == null) return;
        List<Integer> ids = picks.live(ui.game);
        LandScreen.MapCard m = ids.isEmpty() ? null : ui.landScreen.mapCard(ids);
        if (m == null) {
            hideBuyCard();
            return;
        }
        receiptShown = false;
        cardFor = cardKey(ids);
        List<double[]> sites = new ArrayList<>();
        CityLand land = ui.game.getCityLand();
        for (LandParcel p : listed(m.ids())) {
            for (Deposit f : LandMap.offerFields(land, p)) {
                for (double[] q : LandMap.siteSquares(f)) sites.add(new double[] { q[0], q[1], q[2], q[3], 0xff000000 | f.kind().colour() });
            }
        }
        rings = sites;
        buyBox.getChildren().setAll(ui.landScreen.mapCardNode(m, this));
        buyBox.setVisible(true);
        layoutBuyCard();
    }

    /** The card put away. */
    private void hideBuyCard() {
        cardFor = null;
        rings = List.of();
        receiptShown = false;
        if (buyBox == null) return;
        buyBox.getChildren().clear();
        buyBox.setVisible(false);
    }

    /** What the card was drawn from: the picks, the toggle, the cash and the vault, the month and the listing. */
    private String cardKey(List<Integer> ids) {
        Game g = ui.game;
        return ids + "|" + g.isLandPaidFromVault() + "|" + g.getCash() + "|" + g.getForeignAccounts().getReservesUsd() + "|"
                + g.getMonth() + "|" + g.getLandManager().getMarket().getNextOfferId();
    }

    /** A frame's look at the card (tick()): drawn again when what it says moved - the month dropping the picks, an offer bought elsewhere falling out of them, the cash - and the receipt put away with its month. */
    private void syncCard() {
        if (buyBox == null || !buyBox.isVisible()) return;
        Game g = ui.game;
        if (receiptShown) {
            if (g.getLastLandReceipt().isEmpty()) {
                bought = null;
                hideBuyCard();
                dirty = true;
            }
            return;
        }
        if (!cardKey(picks.live(g)).equals(cardFor)) {
            showBuyCard();
            dirty = true;
        }
    }

    /** The card sized to what it says, then placed. */
    private void layoutBuyCard() {
        buyBox.applyCss();
        buyBox.autosize();
        placeBuyCard();
    }

    /** The card beside its box of plots as the view moves (each draw): at its right, else at its left, its top level with the box's, inside the view. */
    private void placeBuyCard() {
        if (buyBox == null || !buyBox.isVisible()) return;
        double vw = canvas.getWidth(), vh = canvas.getHeight();
        double cw = buyBox.getWidth(), ch = buyBox.getHeight();
        double[] s = cardAt == null ? null : screenBox(cardAt);
        double left = vw - cw - CARD_MARGIN, top = CARD_MARGIN;
        if (s != null) {
            left = s[2] + CARD_GAP;
            if (left + cw > vw - CARD_MARGIN) left = s[0] - CARD_GAP - cw;
            top = s[1];
        }
        left = Math.max(CARD_MARGIN, Math.min(left, vw - cw - CARD_MARGIN));
        top = Math.max(CARD_MARGIN, Math.min(top, vh - ch - CARD_MARGIN));
        buyBox.setTranslateX(Math.rint(left));
        buyBox.setTranslateY(Math.rint(top));
    }

    /** A box of plots on the screen, {x0, y0, x1, y1} round its corners as drawn now, or null when none is seen. */
    private double[] screenBox(double[] b) {
        LandMap.ToScreen at = toScreen();
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        boolean any = false;
        for (double[] c : new double[][] { { b[0], b[1] }, { b[2], b[1] }, { b[2], b[3] }, { b[0], b[3] } }) {
            double[] p = at.at(c[0], c[1]);
            if (p == null) continue;
            any = true;
            x0 = Math.min(x0, p[0]);
            y0 = Math.min(y0, p[1]);
            x1 = Math.max(x1, p[0]);
            y1 = Math.max(y1, p[1]);
        }
        return any ? new double[] { x0, y0, x1, y1 } : null;
    }

    /** The card's fields' sites, ringed wherever they lie (board 7): each a ring in its resource's colour on a dark halo, as the city's ring is drawn (ring()), round its square - RING_PX at least. */
    private void drawRings(GraphicsContext gc) {
        if (rings.isEmpty() || buyBox == null || !buyBox.isVisible()) return;
        for (double[] q : rings) {
            double[] s = screenBox(q);
            if (s == null) continue;
            double cx = (s[0] + s[2]) / 2, cy = (s[1] + s[3]) / 2;
            double r = Math.max(RING_PX, Math.hypot(s[2] - s[0], s[3] - s[1]) / 2);
            if (cx + r < 0 || cy + r < 0 || cx - r > canvas.getWidth() || cy - r > canvas.getHeight()) continue;
            gc.setStroke(Color.rgb(11, 17, 24, 0.85));
            gc.setLineWidth(3.5);
            gc.strokeOval(cx - r, cy - r, 2 * r, 2 * r);
            gc.setStroke(colour((int) (long) q[4]));
            gc.setLineWidth(1.5);
            gc.strokeOval(cx - r, cy - r, 2 * r, 2 * r);
        }
    }

    /** The box Shift and a drag draws (B7): the offers' pink, faint inside, dashed round. */
    private void drawBox(GraphicsContext gc) {
        if (!boxing || moved < CLICK_SLOP) return;
        double x = Math.min(pressX, boxX), y = Math.min(pressY, boxY), w = Math.abs(boxX - pressX), h = Math.abs(boxY - pressY);
        gc.setFill(Color.web(OFFER_EDGE, BOX_FILL_ALPHA));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.web(OFFER_EDGE, 0.95));
        gc.setLineWidth(1);
        gc.setLineDashes(5, 4);
        gc.strokeRect(Math.floor(x) + 0.5, Math.floor(y) + 0.5, Math.floor(w), Math.floor(h));
        gc.setLineDashes((double[]) null);
    }

    /** The card's ×, Esc, a click off the offers: no picks, no card. */
    void closeCard() {
        picks.clear();
        bought = null;
        hideBuyCard();
        requestDraw();
    }

    /** A chip's × on a card of several: that one dropped. */
    void dropPick(int id) {
        picks.drop(id);
        showBuyCard();
        requestDraw();
    }

    /** The toggle moved, on the card or the head: both drawn again in the other money. */
    void paidOtherwise() {
        headFor = null;
        syncHead();
        if (!receiptShown) showBuyCard();
    }

    /** What a purchase from the card is of, asked for now. */
    private Bought boughtOf(List<Integer> ids) {
        List<LandParcel> ps = listed(ids);
        List<Integer> found = new ArrayList<>();
        List<int[]> places = new ArrayList<>();
        for (LandParcel p : ps) {
            found.add(p.getId());
            places.add(new int[] { p.getSide(), p.getPlace() });
        }
        return new Bought(found, places, ps.size() == 1 ? ps.get(0).where() : ps.size() + " offers", bounds(ps));
    }

    /** The card's Buy, or Enter (B2): the offers bought as the office's Buy buys them (LandScreen.buyOrFund()) - the office drawn again under the map, which stays - and the receipt by the ground; short after all, the funding page, which comes back here. */
    void buy(List<Integer> ids) {
        bought = boughtOf(ids);
        backFromFunding = true;
        ui.landScreen.buyOrFund(ids);
        if (expanded) {
            backFromFunding = false;
            showBought();
        }
    }

    /** The card's bond or note (B1): issued as the funding page issues it, the offers bought on it (LandScreen.offers()' own action), and the receipt; what falls short shows the office's page, which comes back here. */
    void buyOnLoan(List<Integer> ids, Runnable issue) {
        bought = boughtOf(ids);
        backFromFunding = true;
        issue.run();
        if (expanded) {
            backFromFunding = false;
            showBought();
        }
    }

    /** "All the ways to pay" (B5): the funding page for the picks, the map put away until the page is done and brought back at the view it left (back()). */
    void toFunding(List<Integer> ids) {
        bought = boughtOf(ids);
        backFromFunding = true;
        ui.landScreen.showLandFunding(ids);
        if (expanded) {
            // Nothing to fund after all: the office stayed under the map.
            backFromFunding = false;
            bought = null;
        }
    }

    /** "Open in the land office": the map put away on the offer's row, the office's side and pick. */
    void toOffice(int s, int p) {
        side = s;
        place = p;
        picker.picked(s, p);
        collapse(true);
    }

    /**
     * The office drawn (LandScreen.showLandMenu()), `fromFunding` when the
     * funding page or what it fell short with is what it comes back from: a
     * purchase the card sent there brings the map back over the window at
     * the view it left, with the receipt when it was bought (B5). Reached
     * any other way, the map stays put away and the purchase is forgotten.
     */
    void back(boolean fromFunding) {
        if (!backFromFunding || expanded) return;
        backFromFunding = false;
        if (!fromFunding) {
            bought = null;
            return;
        }
        expand(true);
        showBought();
    }

    /** The receipt by the ground just bought (board 6): the card's purchase, those of it no longer listed - "2 of 3 offers" when it fell short - in the office's words (LandScreen.mapReceiptNode()); with none bought, the card as it stands. */
    private void showBought() {
        Bought b = bought;
        if (b == null || buyBox == null) return;
        LandMarket market = ui.game.getLandManager().getMarket();
        List<int[]> places = new ArrayList<>();
        for (int i = 0; i < b.ids().size(); i++) if (market.find(b.ids().get(i)) == null) places.add(b.places().get(i));
        if (places.isEmpty()) {
            bought = null;
            showBuyCard();
            return;
        }
        picks.clear();
        cardFor = null;
        rings = List.of();
        receiptShown = true;
        cardAt = b.at();
        String title = places.size() == b.ids().size() ? b.title() : places.size() + " of " + b.title();
        buyBox.getChildren().setAll(ui.landScreen.mapReceiptNode(title, places, this));
        buyBox.setVisible(true);
        layoutBuyCard();
        requestDraw();
    }

    /**
     * Esc, Enter and Ctrl+A while the map is over the window, from
     * UserInterface's key filter (0.7.137): Esc closes the card - a second
     * Esc, not taken here, the map - Enter presses the card's main button
     * (B2: Buy, or short the bond, or the funding page), Ctrl+A picks every
     * offer (B7). True when it took the key.
     */
    boolean cardKey(javafx.scene.input.KeyEvent e) {
        if (!expanded) return false;
        KeyCode k = e.getCode();
        if (k == KeyCode.ESCAPE && buyBox != null && buyBox.isVisible()) {
            closeCard();
            return true;
        }
        if (k == KeyCode.ENTER && !receiptShown) {
            List<Integer> ids = picks.live(ui.game);
            LandScreen.MapCard m = ids.isEmpty() ? null : ui.landScreen.mapCard(ids);
            if (m == null) return false;
            ui.landScreen.mapCardPress(m, this);
            return true;
        }
        if (k == KeyCode.A && e.isShortcutDown()) {
            selectAll();
            return true;
        }
        return false;
    }

    /** The head's money and pay toggle, drawn again when the toggle, the cash, the vault or the month moved. */
    private void syncHead() {
        if (headMoney == null) return;
        Game g = ui.game;
        String key = g.isLandPaidFromVault() + "|" + g.getCash() + "|" + g.getForeignAccounts().getReservesUsd() + "|" + g.getMonth();
        if (key.equals(headFor)) return;
        headFor = key;
        headMoney.setText(ui.landScreen.mapHeadMoney());
        // The office's chips on one line, the chip strip's 6 px apart: a FlowPane would ask for its whole wrap length in the head.
        headPay.getChildren().setAll(new ArrayList<>(ui.landScreen.mapPayChips(this).getChildren()));
    }

    /* =====================================================================
       EXPAND: THE MAP OVER THE WHOLE WINDOW
       ===================================================================== */

    void expand() { expand(false); }

    /** ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. */
    private void expand(boolean keepView) {
        if (expanded) return;
        if (bigPane == null) buildPane();
        expanded = true;
        smallBox.getChildren().remove(canvas);
        bigBox.getChildren().add(0, canvas);
        showIf(readout, false);
        clockLine.setText(ui.clockWords());
        headFor = null;
        syncHead();
        ui.showChartFullScreen(bigPane, clockLine, this::collapse);
        ui.panKeysTo(this);
        boolean before = big != null;
        fitPane();
        if (keepView && before) requestDraw();
        else openBig();
        canvas.requestFocus();
    }

    /**
     * Expanded on a box of plots (0.7.134, batch MM2): the minimap's click,
     * the map over the window fitted on what the stamp frames
     * (MiniMapFrame.open()) rather than on the land office's opening; Fit
     * goes back to that. With no box, as Expand opens.
     */
    void expandOn(MiniMapFrame.Box box) {
        expand();
        if (big != null && box != null) {
            MiniMapFrame.open(big, box);
            if (globe != null) {
                globe.fromPlane(big.centreX(), big.centreY(), big.scale());
                follow();
            }
            requestDraw();
        }
    }

    /** Esc, the button, or another screen: the canvas back in the office, and the office drawn again when asked - the card's picks dropped (0.7.137), and its purchase forgotten unless the funding page has it (B5). */
    void collapse(boolean redraw) {
        if (!expanded) return;
        expanded = false;
        ui.panKeysTo(null);
        keyW = keyA = keyS = keyD = false;
        keyNanos = 0;
        boxing = false;
        picks.clear();
        if (!backFromFunding) bought = null;
        hideBuyCard();
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
        // The cash and the pay toggle (0.7.137, spec-earth 6.1): the pane covers the window's header, so the head says them.
        headMoney = new Label();
        headMoney.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_HEAD));
        headMoney.setMinWidth(Region.USE_PREF_SIZE);
        headPay = new HBox(6);
        headPay.setAlignment(Pos.CENTER_LEFT);
        headPay.setMinWidth(Region.USE_PREF_SIZE);
        Label hint = new Label(HINT);
        hint.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        hint.setMinWidth(Region.USE_PREF_SIZE);
        HistoryScreen.tip(hint, KEYS_TIP);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label every = stepChip(SELECT_EVERY, this::selectAll, true);
        every.setMinWidth(Region.USE_PREF_SIZE);
        HistoryScreen.tip(every, "Picks every offer listed, the office's best first, onto the card · Ctrl+A");
        Label fit = stepChip("Fit", this::fitBig, true);
        Label close = stepChip("Back to the land office · Esc", () -> collapse(true), true);
        close.setMinWidth(Region.USE_PREF_SIZE);
        HBox head = new HBox(Palette.GAP_LOOSE, title, clockLine, headMoney, headPay, gap, hint, every, fit, close);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMinHeight(PANE_HEAD - Palette.GAP);
        card = new VBox(2);
        card.setStyle("-fx-background-color: #0b1118e6; -fx-background-radius: 6; -fx-border-color: " + Palette.EDGE
                + "; -fx-border-radius: 6; -fx-padding: 6 9 7 9;");
        // Laid out by hand at the pointer: never managed, so the pane never stretches it.
        card.setMouseTransparent(true);
        card.setManaged(false);
        card.setVisible(false);
        VBox legend = legend();
        StackPane.setAlignment(legend, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(legend, new Insets(Palette.GAP));
        // The buying card (0.7.137): laid out by hand beside its offer, under the hover card, never managed either.
        buyBox = new VBox();
        buyBox.setManaged(false);
        buyBox.setVisible(false);
        bigBox = new StackPane(legend, buyBox, card);
        StackPane.setAlignment(card, Pos.TOP_LEFT);
        StackPane.setAlignment(buyBox, Pos.TOP_LEFT);
        bigBox.setStyle("-fx-background-color: " + Palette.STAGE + ";");
        clip(bigBox);
        bigPane = new VBox(Palette.GAP, head, bigBox);
        bigPane.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-padding: " + PANE_TOP + " " + PANE_SIDE + " "
                + PANE_BOTTOM + " " + PANE_SIDE + ";");
        bigPane.widthProperty().addListener((o, was, now) -> fitPane());
        bigPane.heightProperty().addListener((o, was, now) -> fitPane());
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
        if (globe != null) {
            globe.resize(w, h);
            follow();
        }
        requestDraw();
    }

    /** The expanded map on the city, as the small map opens - on Earth too (spec-globe 3.7), the globe's view on the plane's. */
    private void openBig() {
        if (big == null) return;
        LandMap.open(big, ui.game.getCityLand(), ui.game.getLandManager().getMarket());
        globeUp(ui.game.getCityLand(), ui.game.getLandManager().getMarket(), fittedFor);
        if (globe != null) {
            globe.fromPlane(big.centreX(), big.centreY(), big.scale());
            follow();
        }
        requestDraw();
    }

    /** Fit (0): on Earth the whole planet, facing the city, north up (spec-globe 3.7); on a generated world the city, as it opens. */
    private void fitBig() {
        if (big == null) return;
        if (globe == null) {
            openBig();
            return;
        }
        globe.fit();
        follow();
        requestDraw();
    }

    /** The legend's entries in a tier (since 0.7.135 the model's, LandMap.legend(): on the globe the kinds of ground, the city's ring and edge, in the GROUND tier its offers; on the plane the plane's), as CSS colours and words. */
    static List<String[]> legendEntries(int tier) {
        List<String[]> out = new ArrayList<>();
        for (LandMap.Entry e : LandMap.legend(tier)) out.add(new String[] { css(e.colour()), e.words() });
        return out;
    }

    /** ...the plane's, at every scale on a generated world. */
    static List<String[]> legendEntries() { return legendEntries(GlobeFrame.PLANE); }

    /** Rows a column of the legend holds: 17, its 51 entries in three columns and the zones set after them (since 0.8.1 the transport class, the railway's four and the gateway, 46 in three columns of 16 before; since 0.8.0 the four industries' works and the seven looks of the materials, 35 in three columns of 12 before; since 0.7.127 ice and the desert, 33 before; since 0.7.97, nine for the shore and the sea; 24 in two before; 11 until 0.7.72 added the railway to its 22; 0.7.88 the tracks). */
    static final int LEGEND_ROWS = 17;

    /** The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). */
    static final String SHIP_SIZE_WORDS = "Ships are drawn 1.6\u00d7 their size until 6 px a plot";

    /** The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). */
    static final String TRUCK_WORDS = "Trucks: one per %s";

    /** ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. */
    static String trucksWords(int loads) {
        return String.format(TRUCK_WORDS, loads <= 1 ? "load" : String.format("%,d loads", loads));
    }

    /** The legend's tracks (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. */
    static final String TRACK_ENTRY = LandMap.TRACK_ENTRY;

    /** The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed without a street. */
    static String packedWords(int packed) {
        return String.format("%,d building%s packed without a street: the city is short of ground for streets", packed, packed == 1 ? "" : "s");
    }

    /** ...and when the city has road its streets have no room for (star RD2-4): how much, in plots. */
    static String surplusWords(long plots) {
        return String.format("%,d plots of road more than its streets carry", plots);
    }

    /** ...and (FR7 slice 3) when the model has track the railway's ways do not use - its main line, its sidings and its branches - how much, in plots; the model's words (LandMap.trackSurplusWords()). */
    static String trackWords(long plots) {
        return LandMap.trackSurplusWords(plots);
    }

    /** The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. */
    static final double LEGEND_NOTE_WIDTH = 300;

    private final Label legendNote = new Label();
    /** The zones the legend was laid out with (0.8.0): their words, joined. */
    private String legendZones = "";
    private final GridPane legendGrid = new GridPane();
    private final Label shipsLine = new Label(SHIP_SIZE_WORDS);
    /** The legend's trucks line: shown while trucks are drawn this frame (legendTrucks()). */
    private final Label trucksLine = new Label();

    private VBox legend() {
        legendGrid.setHgap(Palette.GAP_LOOSE);
        legendGrid.setVgap(2);
        legendTier = Integer.MIN_VALUE;
        legendFor(GlobeFrame.PLANE);
        legendNote.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        legendNote.setWrapText(true);
        legendNote.setMaxWidth(LEGEND_NOTE_WIDTH);
        showIf(legendNote, false);
        shipsLine.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        trucksLine.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        showIf(trucksLine, false);
        VBox box = new VBox(6, legendGrid, shipsLine, trucksLine, legendNote);
        box.setStyle("-fx-background-color: #0b1118d9; -fx-background-radius: 6; -fx-padding: 8 10 8 10;");
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.setMouseTransparent(true);
        return box;
    }

    /** The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. */
    private void legendFor(int tier) {
        // ...and (0.8.0) on the plane the zones set, drawn again when they move.
        List<String[]> zones = tier == GlobeFrame.PLANE ? zoneEntries() : List.of();
        String zoneKey = String.join("|", zones.stream().map(z -> z[1]).toList());
        if (tier == legendTier && zoneKey.equals(legendZones)) return;
        legendTier = tier;
        legendZones = zoneKey;
        legendGrid.getChildren().clear();
        List<String[]> entries = new ArrayList<>(legendEntries(tier));
        entries.addAll(zones);
        for (int i = 0; i < entries.size(); i++) {
            legendGrid.add(keySwatch(entries.get(i)[0], entries.get(i)[1]), i / LEGEND_ROWS, i % LEGEND_ROWS);
        }
        showIf(shipsLine, tier == GlobeFrame.PLANE);
    }

    /** The tier the expanded map shows: the globe's on Earth, the plane at every scale on a generated world. */
    private int shownTier() { return globe != null ? globe.tier() : GlobeFrame.PLANE; }

    /** The trucks' line: how many loads a truck stands for while any is drawn this frame, on the plane; hidden otherwise. */
    private void legendTrucks() {
        boolean on = trucksStand > 0 && shownTier() == GlobeFrame.PLANE;
        if (on) trucksLine.setText(trucksWords(trucksStand));
        showIf(trucksLine, on);
    }

    /** The legend's note, from the map's packing at the city's edge once it is drawn (never planned here): what the city has no room for - on the plane. */
    private void legendNote() {
        if (shownMap == null) return;
        if (shownTier() != GlobeFrame.PLANE) {
            showIf(legendNote, false);
            return;
        }
        long[] f = shownMap.legendFiguresIfDrawn();
        String words = null;
        if (f != null && f[0] > 0) words = packedWords((int) f[0]);
        if (f != null && f[1] > 0) words = words == null ? surplusWords(f[1]) : words + "; " + surplusWords(f[1]);
        if (f != null && f[2] > 0) words = words == null ? trackWords(f[2]) : words + "; " + trackWords(f[2]);
        if (words != null) legendNote.setText(words);
        showIf(legendNote, words != null);
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
