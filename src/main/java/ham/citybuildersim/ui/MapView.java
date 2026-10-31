package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
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
 * into UserInterface's pane over the window (City History's, 0.7.23; since
 * MS1 under the window's own header, which stays in view - the clock, the
 * money block, the five tiles - rather than over it): a drag from anywhere
 * but ground that may be picked pans (an offer's free ground, a block that
 * may be picked or is, BL1; and a right or middle drag from
 * anywhere), the wheel zooms by MapFrame.ZOOM_STEP a notch at the pointer,
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
 * stamps the tiles it shows again, and repaints only those whose stamp moved;
 * the far nodes, since batch UP3, only those whose key moved
 * (MapTiles.nodeKey()), and the minimap's stamp at most every
 * MiniMap.STAMP_EVERY_MS, from its kept layers.
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
 * the view and, where two lines leave together, apart from the other's or
 * shared with it (LandMap.worldLabels(), FX5).
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
 * (TruckSchedule, from Haulage's loads) on the TRIPS of the districts drawn
 * (CityMap.tripsDrawn(), FR9): a truck's whole way from the world line along
 * the highways and the streets to a works' gate and back, each district's
 * schedule taking its catchment's part of the city's loads, each truck standing
 * for the schedule's N loads, which the legend gives (TRUCK_WORDS); the body
 * and colour are TruckShapes'.
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
 * carried pixel by pixel and the planet's picture under it meanwhile - and
 * since MS1 at every scale where the view's centre is past the city's reach
 * (GlobeFrame.tier()), closer than the handoff too, where since MS2 (Jerus
 * 2026-10-26, "look at the wierd resolution by just panning a few more
 * kms") the plane's own far nodes are drawn through the camera at the
 * scale's own stride - 8, 4, 2 or 1 plots a pixel - wherever the world's
 * square holds the ground within GlobeFrame.handoverKm() of the city
 * (GlobeFrame.planeDrawnPastReach()), the ground as the plane draws it
 * inside the reach, its block lines and its legend with it; and since MS3
 * (Jerus 2026-10-27, Goose Bay from a Singapore city drawn in streaks:
 * the plane's shape distortion) past the hand-over, off the square too,
 * the view's chart (GlobeFrame.chart()) - a north-up orthographic piece of
 * the planet about the anchor nearest the view's centre, true in shape,
 * cut into nodes at the same stride and kept as the plane's far nodes are
 * (chartNodes(): asked of WORKER from the middle out, a node asked for a
 * view that has moved on dropped unread, MapTiles.chartTerrain()) - so
 * no far-rule pieces are asked at those scales, and the hover there
 * reads the chart node under the pointer (drawnAt());
 * PLANE, the map as before, its MapFrame following the globe's view while
 * its centre is within the reach. A
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
 * figures LandMap.card()'s - shift adds one or drops it, a drag that starts
 * on an offer's free ground (since MS1, no modifier: LandMap.pressBoxes()),
 * or Shift and a drag from anywhere, draws a box that takes every offer
 * whose free ground it meets, and
 * Ctrl+A or the head's "Select every offer" takes them all; Buy or Enter
 * buys them, short the card issues the funding page's bond, the funding
 * page comes back to the map, and the head shows the cash and the pay
 * toggle the header does not. The picks and the box drawn stand through a
 * month (MS1; LandMap.Picks), the head saying in the warning colour what a
 * month or a purchase took off them (Picks.dropNote()). The small map
 * clicks as before.
 *
 * THE EXACT GROUND (BL1, 2026-10-26; docs/queue/BL1-notes.md 1; Jerus: "those
 * blocks are wierd and sometimes overlap with land already bought like
 * uiwise, on map view. perhaps more freedom on the exact selection?"). An
 * offer is drawn as the ground it is - its free ground filled and edged along
 * its own outline (LandMap.freeOutline()), on the plane and through the
 * globe's camera alike, nothing of it over the city's ground - and the
 * player picks blocks of the city's grid: a click on a free block that may be
 * picked (Picks.pickable()) picks it, a drag from one - or from an offer's
 * free ground, a picked block, or with Shift from anywhere - boxes the blocks
 * the box covers that join the city (Picks.addBox(), the preview tinted
 * under the box as it is drawn), and the picked ground is one shape, tinted
 * and edged in the offers' pink, on its free ground only. The card names the
 * picked ground as "Your ground - N blocks" beside the offers.
 *
 * THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1 and 8,
 * board 1; Jerus: "a toggle on the map ui", "a map button to ... toggle
 * borders", "dot ... each nation stats on their land", "clicking would show
 * more stats", "legend should be collapsable"). On an Earth world whose world
 * file is here, the head's Nations and Borders chips (GamePrefs, kept beside
 * the saves) draw what NationsMap works out and nothing here recomputes: on
 * the globe the borders dashed white at the level of detail the view takes and
 * a dot at each nation's largest place, by its output, its name and figures
 * written where they fit; on the city's plane (the land office's small map
 * too) the borders only where one crosses the city's ground and its offers. A
 * dot's hover says its figures, a click on a dot or on a nation's land opens
 * its card beside the point, and the legend folds to its head line. The map is
 * built once on WORKER and nothing is drawn or read of it while a chip is off.
 * The trade routes are WN3's: its chip comes with its lanes.
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

    /* ----------------------------- what it draws from ----------------------------- */

    private CityMap shownMap;
    private MapTiles tiles;
    private Game.MapDraft draft;
    private int draftsFailed;
    private long version = Long.MIN_VALUE;
    private long fittedFor = Long.MIN_VALUE;
    /** How many far nodes have been coloured (batch UP3): a node whose version moved but whose key did not (MapTiles.nodeKey()) is not counted. */
    private long farRecoloured;
    private final TilePainter.Input in = new TilePainter.Input();
    private int[] buffer = new int[0];

    /** A picture: its image, the stamp it was painted at, the map's version it is current against, and a far node's ground - and (0.7.128) whether it was drawn without a district picture it would sample, and the map's count of pictures kept then (CityMap.picturesKept()): drawn again when that moves. */
    private static final class Img {
        WritableImage image;
        long stamp, version = Long.MIN_VALUE;
        /** A tile's key (MapTiles.tileKey(), batch UP5) at the moment `stamp` was taken, and whether it was: while the key holds the stamp does, and the tile's inputs are not read again. */
        long key;
        boolean keyed;
        byte[] ground;
        /** The node's relief beside its ground (ET1 slice D): null on a generated world, whose ground is all flat. */
        byte[] relief;
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
    /**
     * The globe's chart nodes (MS3; GlobeFrame.chart()) kept as the plane's far nodes are - their colours (Img.pixels), classes and relief
     * (for the hover) - and those asked of WORKER and not back. Keyed by chartKey(), kept across views, and cleared with the globe
     * (resetGlobe()): a chart node reads nothing of the city, so a new map of the same world keeps them.
     */
    private final Pictures charts = new Pictures();
    private final Set<Long> chartsAsked = new HashSet<>();
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
    /** The button the press was made with (MS1): only the primary clicks or draws a box; the others pan. */
    private javafx.scene.input.MouseButton pressButton;
    private double[] hoverAt;
    /** Whether Shift was held when the pointer last moved over the map: the cursor says a press would box from anywhere (MS1). */
    private boolean hoverShift;

    /* ----------------------------- buying from the map (0.7.137, batch M6) ----------------------------- */

    /** The offers picked on the expanded map, in the order picked (LandMap.Picks): the view's alone, dropped on a load and a collapse - and, since MS1, not on a month: each is keyed to its ground and lost only when the city bought it or nothing offers it, the head saying so (headNote). */
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
    /** A drag that boxes (B7; since MS1 a plain drag from an offer's free ground, or Shift and a drag from anywhere - LandMap.pressBoxes()): a box drawn from the press to (boxX, boxY), in the screen's pixels. A month leaves it be. */
    private boolean boxing;
    private double boxX, boxY;
    /** The head's money and its pay toggle (land's own, in the money the toggle pays in: the window's header, kept in view over this pane since MS1, says neither), what they were drawn for, and the note of what the picks lost (Picks.dropNote()). */
    private Label headMoney;
    private HBox headPay;
    private String headFor;
    private Label headNote;
    private javafx.scene.control.Tooltip headNoteTip;

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
    /** Whether boats - or since 0.8.2 trains, and since 0.8.8 trucks - are in view (the frame goes on), and the clock they were last drawn at. */
    private boolean boatsLive;
    private double drawnClock = Double.NaN;
    private final AnimationTimer pump = new AnimationTimer() {
        @Override public void handle(long now) { tick(); }
    };

    private final ImagePattern hatch;

    /** The amber hatch over the city's ground that no port links (ET5): the offers' hatch drawn in the warning colour. */
    private final ImagePattern islandHatch;

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

    /** The GROUND tier's pieces past the plane, for one view at rest: its codes - class and relief (GlobePicture.code()) - a pixel (-1 not in yet), the view and its number. */
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

    /** The tier the legend was laid out for, and whether with the relief's entries (LandMap.reliefDrawn()). */
    private int legendTier = Integer.MIN_VALUE;
    private boolean legendRelief;

    MapView(UserInterface ui, Picker picker, Runnable onClose) {
        this.ui = ui;
        this.picker = picker;
        this.onClose = onClose;
        hatch = hatchPattern();
        islandHatch = islandHatchPattern();
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

    /** The Expand button's tooltip (since MS1 it says the drag picks; since BL1 the ground it picks is the player's own: a drag along the city's edge picks the exact ground, one from elsewhere pans). */
    static final String EXPAND_TIP = "The map under the window's header: drag along the city's edge to pick the exact ground, drag from elsewhere or with"
            + " the right button (or W, A, S and D) to pan, scroll to zoom, Esc to come back.";

    /** The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), since MS1 by a click or a drag, since BL1 of the exact ground - blocks of the city's grid, the offers its suggestions - every key in its tooltip (KEYS_TIP). */
    static final String HINT = "drag along the city's edge to pick the exact ground · drag elsewhere to pan";

    /** ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. */
    static final String KEYS_TIP = "Click an offer to open its card beside it, or a free block along the city's edge to pick just that block;"
            + " shift-click adds an offer or a block to the card or drops it. Drag along the city's edge - from an offer's free ground,"
            + " a block that may be picked or one picked, no key needed, or Shift and a drag from anywhere - to pick the exact ground:"
            + " every block the box covers that joins the city, tinted under the box before you let go; an offer whose whole free"
            + " ground you cover is bought as the offer, at its price. Ctrl+A picks every offer. Enter buys the card;"
            + " Esc closes the card, then the map."
            + "\nDrag from elsewhere, or with the right or middle button from anywhere, or W, A, S and D to pan"
            + " · scroll, + or - to zoom · 0 fits. The picks stand through a month.";

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
        hideNationCard();
        forgetNations();
        reset();
        resetGlobe();
        side = place = litSide = litPlace = overSide = overPlace = -1;
        fittedFor = Long.MIN_VALUE;
        // The island hatch is keyed on the books' version, which restarts at every load (ET5): another city's key can equal this one's.
        islandFor = Long.MIN_VALUE;
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
        truckTrips = null;
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
            syncNationCard();
        }
        if (tiles != null) more |= paint(deadline);
        if (hoverAt != null) {
            hover(hoverAt[0], hoverAt[1]);
            hoverAt = null;
        }
        // The boats (0.7.97), and the trains and trucks: while any is in view the frame goes on, and draws again whenever the clock moves them.
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
        nationsUp(land);
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
        charts.most = f.nodesAtMost();
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
     * picture at this level, or one from an older version - keyed
     * (MapTiles.tileKey(), batch UP5), stamped again only if the key moved,
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
            // The version moved: its inputs are read again only if the key they are worked out from did (MapTiles.tileKey(), batch UP5: equal
            // keys, equal inputs, so the stamp the picture holds), a few lookups where reading them is the streets of a ring and the runs over a box.
            long tileKey = tiles.tileKey(tx, ty);
            if (img != null && img.keyed && img.key == tileKey) {
                img.version = version;
                continue;
            }
            long stamp = tiles.input(tx, ty, in);
            if (img != null && img.stamp == stamp) {
                img.key = tileKey;
                img.keyed = true;
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
            img.key = tileKey;
            img.keyed = true;
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
            return paintNodes(f.nodePlots(), f.nodeX0(), f.nodeX1(), f.nodeY0(), f.nodeY1(), deadline, shownMap.picturesKept(), f);
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

    /**
     * The far nodes nx0 to nx1 across and ny0 to ny1 down at `plots` plots a pixel: their ground asked of WORKER, coloured here until the deadline (0.7.135: the plane's view's, or the globe's near the city). True when there is more to do.
     *
     * Since batch UP3 a node whose map version has moved is coloured again only if the key it is drawn from moved
     * (MapTiles.nodeKey(), kept in Img.stamp: equal keys, equal pixels), so a month that places in one district
     * colours the nodes over it and no more; a node drawn short of a picture is coloured on every version move, as
     * that is what asks again for the pictures it lacks - while the view asks for pictures at all (MapTiles.picturesFit()
     * of `asker`, the frame askPictures() is handed): past that, a city of thousands of districts seen wide, it would ask
     * nothing, and is held with the rest. farRecoloured() counts the nodes coloured.
     */
    private boolean paintNodes(long plots, long nx0, long nx1, long ny0, long ny1, long deadline, long picturesKept, MapFrame asker) {
        long span = MapFrame.NODE_PX * plots;
        World.Key world = shownMap.worldKey();
        int n = MapFrame.NODE_PX;
        if (buffer.length < n * n) buffer = new int[n * n];
        // The nodes this view wants (MS2): WORKER's queue skips a node asked for in a view that has moved on, and the nodes this call
        // asks for go in from the middle of the view out - a pan at the near strides, 128 to 2,048 px a node, asks a few at a time.
        final long[] wanted = { plots, nx0, nx1, ny0, ny1 };
        nodesWanted = wanted;
        List<long[]> asking = new ArrayList<>();
        // Whether the view asks for the pictures its nodes lack (MapTiles.picturesFit()): found once a call, when a partial node's key held.
        Boolean fits = null;
        for (long ny = ny0; ny <= ny1; ny++) {
            for (long nx = nx0; nx <= nx1; nx++) {
                long key = nodeKey(plots, nx, ny);
                Img img = nodes.get(key);
                if (img == null) {
                    // Its ground asked of WORKER once; the answer asks for a frame.
                    if (nodesAsked.add(key)) asking.add(new long[] { nx, ny, key });
                    continue;
                }
                if (img.version == version && !(img.partial && img.pictures != picturesKept)) continue;
                if (System.nanoTime() > deadline) {
                    askNodes(asking, plots, span, world, wanted);
                    return true;
                }
                // The version moved: coloured again only if what the node is drawn from did (MapTiles.nodeKey()). A node drawn short of
                // a picture always is - colouring is what asks again for the pictures it lacks.
                long drawnFrom = tiles.nodeKey(nx * span, ny * span, plots, true);
                if (img.image != null && img.stamp == drawnFrom) {
                    boolean hold = !img.partial;
                    if (!hold) {
                        if (fits == null) fits = tiles.picturesFit(asker);
                        hold = !fits;
                    }
                    if (hold) {
                        img.version = version;
                        continue;
                    }
                }
                img.stamp = drawnFrom;
                farRecoloured++;
                img.partial = tiles.nodePixels(nx * span, ny * span, plots, img.ground, img.relief, true, buffer) > 0;
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
        askNodes(asking, plots, span, world, wanted);
        return false;
    }

    /** The far nodes the view being painted wants - {plots a pixel, nx0, nx1, ny0, ny1} - which WORKER's queue reads from its own thread (MS2). */
    private volatile long[] nodesWanted;

    /**
     * The far nodes a call of paintNodes() found it lacked, each {nx, ny, key}, their ground asked of WORKER from the middle of `wanted`
     * out (MS2). A node still queued when the view has moved on - a pan or a wheel notch since it was asked - is dropped unread rather than
     * read in its turn (nodesAsked forgets it and a frame is asked, so a view that comes back asks for it again): at the near strides a
     * node is 128 to 2,048 px across, a drag of a screen asks for a dozen and a fast one for a hundred, and each is 5 to 10 ms of
     * the one worker, so the queue would hold the view's own nodes behind a screen of them it left.
     */
    private void askNodes(List<long[]> asking, long plots, long span, World.Key world, long[] wanted) {
        if (asking.isEmpty()) return;
        final double cx = (wanted[1] + wanted[2]) / 2.0, cy = (wanted[3] + wanted[4]) / 2.0;
        asking.sort((a, b) -> Double.compare(Math.hypot(a[0] - cx, a[1] - cy), Math.hypot(b[0] - cx, b[1] - cy)));
        final int n = MapFrame.NODE_PX;
        for (long[] a : asking) {
            final long nx = a[0], ny = a[1], key = a[2], x0 = nx * span, y0 = ny * span;
            final MapTiles askedFor = tiles;
            WORKER.submit(() -> {
                long[] w = nodesWanted;
                if (w == null || w[0] != plots || nx < w[1] || nx > w[2] || ny < w[3] || ny > w[4]) {
                    Platform.runLater(() -> {
                        nodesAsked.remove(key);
                        requestDraw();
                    });
                    return;
                }
                byte[] relief = world.earth() ? new byte[n * n] : null;
                byte[] ground = MapTiles.nodeTerrain(world, x0, y0, plots, relief);
                Platform.runLater(() -> {
                    nodesAsked.remove(key);
                    if (tiles != askedFor) return;
                    Img got = new Img();
                    got.ground = ground;
                    got.relief = relief;
                    nodes.put(key, got);
                    requestDraw();
                });
            });
        }
    }

    /** How many far nodes this view has coloured (batch UP3): the figure a probe reads against the nodes in view. */
    long farRecoloured() { return farRecoloured; }

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
        // the handoff and gone a notch in (GlobeFrame.crossfade(), the view's: this is the plane's tier, so the scale's).
        if (expanded && globe != null && tiles != null) {
            double a = globe.crossfade();
            if (a > 0) {
                composeGlobe(MODE_FADE);
                gc.setGlobalAlpha(a);
                drawGlobeImage(gc);
                gc.setGlobalAlpha(1);
            }
        }
        planeBorders(gc, f);
        worldLines(gc, f);
        overlays(gc, f);
        tradeArrows(gc);
        drawRings(gc);
        drawBox(gc);
        placeBuyCard();
        placeNationCard();
    }

    /* =====================================================================
       THE WORLD LINE (0.8.1, batch FR0; spec-freight 4.1, D13)
       ===================================================================== */

    /** The world line's label as measured: its font, its width (and the shared label's) and its height above the baseline, kept until the font changes (FX2, FX5). */
    private javafx.scene.text.Font worldLabelFont;
    private double worldLabelW, worldLabelBothW, worldLabelH;

    /**
     * The highway's and the main line's faded runs past the city's ground
     * (CityMap.worldLines()): each from its point along its way out over dry
     * ground, as far as the land runs (CityMap.WorldLine.reach(), at most
     * CityMap.WORLD_REACH) and no farther, clipped to the canvas, the highway
     * in its road colour at WORLD_LINE_ALPHA, the railway dashed in RAIL_DARK;
     * labelled once near the far end of the part in view, the label kept
     * inside the view (LandMap.worldLabelAt, FX2); the lines' labels are placed
     * together (LandMap.worldLabels(), FX5), so two lines leaving side by side
     * do not print over each other: one slides back along its line, or the
     * two share a label.
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
            probe.setText(LandMap.WORLD_BOTH_WORDS);
            worldLabelBothW = probe.getLayoutBounds().getWidth();
            worldLabelFont = labelFont;
        }
        double[][] parts = new double[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            CityMap.WorldLine line = lines.get(i);
            boolean highway = line.net() == CityRuns.HIGHWAYS;
            double sx = f.screenX(line.x()), sy = f.screenY(line.y());
            double r = Math.toRadians(line.way());
            double dx = Math.cos(r), dy = Math.sin(r);
            double[] cut = rayRect(sx, sy, dx, dy, line.reach() * scale, w, h);
            if (cut == null) continue;
            parts[i] = cut;
            gc.setStroke(colour(highway ? TileRaster.ROAD[BuildingVisual.HIGHWAY] : TileRaster.RAIL_DARK, WORLD_LINE_ALPHA));
            gc.setLineWidth(highway ? Math.max(2, 0.86 * scale) : Math.max(1.5, 0.52 * scale));
            if (highway) gc.setLineDashes((double[]) null);
            else gc.setLineDashes(6, 5);
            gc.strokeLine(cut[0], cut[1], cut[2], cut[3]);
            gc.setLineDashes((double[]) null);
        }
        // The labels once every line is cut, so that they are placed apart (FX5).
        LandMap.WorldLabel[] labels = LandMap.worldLabels(parts, worldLabelW, worldLabelBothW, worldLabelH, w, h);
        gc.setFont(labelFont);
        gc.setFill(Color.rgb(255, 255, 255, WORLD_LINE_ALPHA));
        for (LandMap.WorldLabel at : labels) {
            if (at != null) gc.fillText(at.words(), at.x(), at.y());
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
     * The lanes and the boats over the tiles: the lanes on screen as a band
     * and dashes (from L2 in), and from L1 in the boats the month's
     * schedule puts on them at the clock - each one's hull, deck and bridge
     * (ShipShapes), a dart of its colour when it is small, fading in the
     * abyss. The routes are found on WORKER the first time, and again as
     * terminals are laid, only the new ones; nothing is drawn until they are.
     * Since SL1 (2026-10-28) the lanes are SeaLanes', each leg of all the
     * routes once - terminals share a lane (SeaRoutes) - and only the legs
     * on screen are asked for and stroked, as one path: a lane many routes
     * ride is one band, not many laid over one another. The boats are the
     * schedule's, one route a berth, riding the lane's legs.
     */
    private void boats(GraphicsContext gc, MapFrame f, int level) {
        if (shownMap == null || shownMap.shore().isEmpty()) return;
        List<BoatSchedule.Route> routes = shownMap.seaRoutesIfFound();
        SeaLanes lanes = shownMap.seaLanesIfFound();
        if (routes == null || lanes == null) {
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
        // The legs of the lanes on screen, once each, stroked as one path: the band, then the dashes.
        double[] legs = lanes.crossing(x0, y0, x1, y1);
        int crossing = legs.length / 4;
        if (crossing > 0) {
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            gc.beginPath();
            for (int i = 0; i < legs.length; i += 4) {
                gc.moveTo(f.screenX(legs[i]), f.screenY(legs[i + 1]));
                gc.lineTo(f.screenX(legs[i + 2]), f.screenY(legs[i + 3]));
            }
            gc.setStroke(colour(ShipShapes.IMPORT, LANE_BAND_ALPHA));
            gc.setLineWidth(LANE_BAND_PX);
            gc.setLineDashes((double[]) null);
            gc.stroke();
            gc.setStroke(colour(ShipShapes.IMPORT, LANE_DASH_ALPHA));
            gc.setLineWidth(1);
            gc.setLineDashes(5, 6);
            gc.stroke();
            gc.beginPath();
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

    /* ----- the trucks (0.8.8, batch FR5; spec-freight 4.7, D21; whole trips since FR9) ----- */

    /** A district's trucks' schedule, with the trips it was made on: made again when its district's trips are another list. */
    private record DistrictTrucks(List<TruckSchedule.Route> trips, TruckSchedule schedule) { }

    /** The districts' schedules by CityMap.DistrictTrips.key(): thrown out with the shares when the month, the game month or the trips turn. */
    private final Map<Long, DistrictTrucks> districtTrucks = new HashMap<>();

    /** The boat-month and the game month the schedules were made on, and the trips (CityMap.tripsDrawn()) they were made against. */
    private int truckMonth = Integer.MIN_VALUE, truckGameMonth = Integer.MIN_VALUE;
    private List<CityMap.DistrictTrips> truckTrips;

    /** What one building of each type carries in the month billed and the city's truck tonnes by load (TruckSchedule.perBuilding(), Freight.truckedIn() and the others): what a district's share is made of, kept with the schedules. */
    private double[][] truckPerBuilding;
    private double[] truckCityTonnes;
    /** ...and each district with trips' catchment's shares of them by CityMap.DistrictTrips.key() (TruckSchedule.sharesByTrips()): its own works' tonnes and those of the districts about it that have no trips. */
    private Map<Long, double[]> truckShares = new HashMap<>();

    /** Loads each truck drawn this frame stands for (the legend's N), or 0 when none is drawn: the largest of the districts' drawn, which is the city's N. */
    private int trucksStand;

    /**
     * The trucks over the tiles, at the near view alone and while the setting
     * is on (TruckSchedule.drawnAt()): each district's trips (CityMap.tripsDrawn(),
     * a truck's whole way from the world line to a works' gate and back, FR9)
     * with the month's schedule on them, taking its catchment's part of the
     * city's loads by the works' tonnes (TruckSchedule.sharesByTrips()). Each truck
     * is one body of TruckShapes in its colour; while any trip crosses the
     * view the frame goes on, as the trains'. Each schedule is made once a
     * month and kept.
     */
    private void trucks(GraphicsContext gc, MapFrame f, int level) {
        if (shownMap == null || !TruckSchedule.drawnAt(level, ui.prefs.isTrucksOnMap())) return;
        double clock = boatClock();
        int m = (int) Math.floor(clock);
        double t = clock - m;
        Game g = ui.game;
        List<CityMap.DistrictTrips> trips = shownMap.tripsDrawn();
        if (truckMonth != m || truckGameMonth != g.getMonth() || truckTrips != trips) {
            districtTrucks.clear();
            var freight = g.getSectors().freight();
            truckPerBuilding = TruckSchedule.perBuilding(s -> TruckSchedule.truckedBy(freight, s), shownMap.types(), shownMap.totals());
            truckCityTonnes = new double[] { freight.truckedIn(), freight.truckedOut(), freight.truckedHome() };
            truckShares = TruckSchedule.sharesByTrips(trips, shownMap::district, truckPerBuilding, truckCityTonnes);
            truckMonth = m;
            truckGameMonth = g.getMonth();
            truckTrips = trips;
        }
        // A truck's length past the view's edge, as it is drawn (TruckShapes.MIN_PX), so none pops in at the edge.
        double margin = Math.max(TruckShapes.LENGTH, TruckShapes.MIN_PX / f.scale());
        long x0 = (long) Math.floor(f.plotX(0) - margin), y0 = (long) Math.floor(f.plotY(0) - margin);
        long x1 = (long) Math.ceil(f.plotX(f.width()) + margin), y1 = (long) Math.ceil(f.plotY(f.height()) + margin);
        int stands = 0;
        for (CityMap.DistrictTrips dt : trips) {
            DistrictTrucks kept = districtTrucks.get(dt.key());
            if (kept == null || kept.trips() != dt.trips()) {
                double[] shares = truckShares.getOrDefault(dt.key(), new double[TruckSchedule.Load.values().length]);
                kept = new DistrictTrucks(dt.trips(), TruckSchedule.of(m, g.getSectors().freight(), dt.trips(), shares, dt.key()));
                districtTrucks.put(dt.key(), kept);
            }
            stands = Math.max(stands, fillTrucks(gc, f, kept.schedule(), x0, y0, x1, y1, t));
        }
        trucksStand = stands;
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
        if (j == null) {
            // The berths are the ones the routes were found for (only a tank farm moved the shore): they stand, and the view is drawn with them.
            if (shownMap.seaRoutesIfFound() != null) requestDraw();
            return;
        }
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
     * offers as hatched shapes - each its free ground only, the city's
     * own ground in its rectangle left clear (LandMap.ownedIn()) - edged
     * along that ground's own outline (LandMap.freeOutline(), BL1: the edge
     * the rectangle's border clipped to it was before, with none against the
     * city) in pink on whole pixels, dashed, the office's side bright, the
     * others faint, the one under the pointer or a hovered row lit, the
     * picked one solid and two px; the picked blocks (BL1) as one shape, tinted
     * and edged the same, and the blocks a box being drawn would take; the
     * city's edge in white, crisp and stepped, on the pixels just
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
        islandHatch(gc, f, g, land);
        blockLines(gc, f, market.getLevel());
        List<LandParcel> listing = market.getListing();
        double ax = Math.floorMod((long) Math.floor(f.screenX(0)), 8), ay = Math.floorMod((long) Math.floor(f.screenY(0)), 8);
        ImagePattern pattern = new ImagePattern(hatch.getImage(), ax, ay, 8, 8, false);
        List<Integer> chosen = expanded ? picks.live(g) : List.of();
        // Where a block is under BLOCK_LINES_FROM px the far views draw an offer's outline and its flat tint and no hatch (BL1),
        // which would only smear over blocks that small.
        boolean flatOnly = f.scale() * (1L << market.getLevel()) < MapFrame.BLOCK_LINES_FROM;
        for (int i = 0; i < listing.size(); i++) {
            LandParcel p = listing.get(i);
            double[] box = f.onScreen(p.getX0(), p.getY0(), p.getX1(), p.getY1());
            if (box == null) continue;
            boolean mine = p.getSide() == side;
            boolean lit = (p.getSide() == overSide && p.getPlace() == overPlace) || (p.getSide() == litSide && p.getPlace() == litPlace);
            // The office's pick, and on the expanded map the card's (0.7.137): edged solid.
            boolean picked = (p.getSide() == side && p.getPlace() == place) || chosen.contains(p.getId());
            // Its free ground: the box, less the city's own ground in it, filled even-odd.
            gc.setFillRule(FillRule.EVEN_ODD);
            gc.beginPath();
            gc.rect(box[0], box[1], box[2] - box[0], box[3] - box[1]);
            for (double[] o : ownedIn.getOrDefault(p.getId(), List.of())) {
                double[] hole = f.onScreen(o[0], o[1], o[2], o[3]);
                if (hole != null) gc.rect(hole[0], hole[1], hole[2] - hole[0], hole[3] - hole[1]);
            }
            gc.setFill(Color.rgb(12, 18, 26, 0.24));
            gc.fill();
            if (!flatOnly) {
                gc.setFill(pattern);
                gc.fill();
            }
            if (lit || picked) {
                gc.setFill(Color.web(Palette.BUILDING, lit ? 0.34 : 0.22));
                gc.fill();
            }
            gc.setFillRule(FillRule.NON_ZERO);
            // Its edge is its free ground's own: the runs of its outline on whole pixels inside it, as the city's edge is drawn
            // (BL1) - a pixel wide and dashed, two and solid when picked - so it runs along the city where the city's finer steps
            // bite into the offer's rectangle and nowhere over the city's ground.
            double[][] runs = freeEdge.get(p.getId());
            if (runs != null) edgeRuns(gc, f, runs, picked ? 2 : 1, !picked, Color.web(OFFER_EDGE, mine || lit ? 0.95 : 0.4));
        }
        gc.setFillRule(FillRule.NON_ZERO);
        if (expanded) {
            pickedGround(gc, f);
            boxPreview(gc, f);
        }
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
        // The ground just bought, outlined while its receipt stands (BL1): the picked ground's own edge, now the city's.
        if (expanded && receiptShown && bought != null && bought.edge() != null) {
            edgeRuns(gc, f, bought.edge(), 2, false, Color.web(OFFER_EDGE, 0.95));
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

    /* ----- the ground no port links (ET5, slice D; the ET5 design, section 5) ----- */

    /** The runs of owned dry ground on landmasses no port links, as LandMap.unlinkedRuns() gave them, the chips beside them, and what they were worked out for: the linked set's version and the land's stamp. Fetched again only when either moves. */
    private List<long[]> islandRuns = List.of();
    private List<LandMap.Chip> islandChips = List.of();
    private long islandFor = Long.MIN_VALUE;

    /**
     * The city's ground that no port links, hatched in amber over the ground (under the offers and the city's edge) and
     * named on a chip at the middle of each landmass: "Island 2 · 12.0 km² · not linked: needs a port here (home has one)".
     * Drawn from the model's runs (LandMap.unlinkedRuns(), plot-exact) and nothing with the city on one landmass: it returns
     * before it reads anything, and the map draws what it always drew.
     */
    private void islandHatch(GraphicsContext gc, MapFrame f, Game g, CityLand land) {
        Landmasses lms = g.getLandManager().getLandmasses();
        if (!lms.anyUnlinked()) {
            islandFor = Long.MIN_VALUE;
            return;
        }
        World world = World.of(land.key());
        long key = (lms.version() * 31L + land.stamp()) * 31L + land.purchases().size();
        if (key != islandFor) {
            islandFor = key;
            islandRuns = LandMap.unlinkedRuns(lms, world, land);
            islandChips = LandMap.unlinkedChips(lms, world, land);
        }
        if (islandRuns.isEmpty()) return;
        double ax = Math.floorMod((long) Math.floor(f.screenX(0)), 8), ay = Math.floorMod((long) Math.floor(f.screenY(0)), 8);
        ImagePattern pattern = new ImagePattern(islandHatch.getImage(), ax, ay, 8, 8, false);
        gc.setFillRule(FillRule.NON_ZERO);
        gc.beginPath();
        boolean any = false;
        for (long[] r : islandRuns) {
            double[] box = f.onScreen(r[1], r[0], r[2], r[0] + 1);
            if (box == null) continue;
            gc.rect(box[0], box[1], box[2] - box[0], box[3] - box[1]);
            any = true;
        }
        if (any) {
            gc.setFill(Color.web(Palette.WARN, 0.12));
            gc.fill();
            gc.setFill(pattern);
            gc.fill();
        }
        islandChips(gc, f, lms);
    }

    /** Each such landmass's chip (LandmassView.chipWords()), mono caption on a dark stroke, as newTag() writes NEW: centred on the middle of its owned tiles, kept inside the view. */
    private void islandChips(GraphicsContext gc, MapFrame f, Landmasses lms) {
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_CAPTION));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(3);
        for (LandMap.Chip chip : islandChips) {
            double x = f.screenX(chip.x()), y = f.screenY(chip.y());
            if (x < 0 || y < 0 || x > f.width() || y > f.height()) continue;
            String words = LandmassView.chipWords(lms, chip);
            double half = words.length() * Palette.SIZE_CAPTION * 0.31 + 4;
            x = half * 2 >= f.width() ? f.width() / 2 : Math.max(half, Math.min(f.width() - half, x));
            x = Math.rint(x);
            y = Math.rint(y);
            gc.setStroke(Color.rgb(11, 17, 24, 0.85));
            gc.strokeText(words, x, y);
            gc.setFill(Color.web(Palette.WARN));
            gc.fillText(words, x, y);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
    }

    /** The amber legend entry for that hatch (LandMap.unlinkedLegend()) while any landmass is unlinked, as the zones' entries are given: {css colour, words}. */
    private List<String[]> islandEntries() {
        Game g = ui.game;
        List<String[]> out = new ArrayList<>();
        if (g == null) return out;
        for (LandMap.Entry e : LandMap.unlinkedLegend(g.getLandManager().getLandmasses())) out.add(new String[] { css(e.colour()), e.words() });
        return out;
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
        freeCells.clear();
        freeEdge.clear();
        for (LandParcel p : market.getListing()) {
            List<double[]> own = LandMap.ownedIn(land, p);
            if (!own.isEmpty()) ownedIn.put(p.getId(), own);
            // The offer's free ground as cells and its edge as runs (BL1), with the holes: what the globe fills and edges through
            // the camera and the plane edges on whole pixels.
            freeCells.put(p.getId(), LandMap.freeCells(land, p));
            freeEdge.put(p.getId(), LandMap.freeOutline(land, p));
        }
    }

    /** Each offer's free ground as rectangles of plots (LandMap.freeCells()) and its edge as runs (LandMap.freeOutline()), by offer id, for the land and listing above: found with the holes, not every frame. */
    private final Map<Integer, List<double[]>> freeCells = new HashMap<>();
    private final Map<Integer, double[][]> freeEdge = new HashMap<>();

    /** The picked blocks' free ground as cells and its edge as runs (LandMap.freeCells(), freeOutline() of the blocks): worked out again only when the pick's blocks (the set picks.blocks() hands out is a new one at every change) or the land they lie on change. */
    private java.util.Set<Long> groundFor = java.util.Set.of();
    private long groundShapesFor = Long.MIN_VALUE;
    private List<double[]> groundCells = List.of();
    private double[][] groundEdge = new double[4][0];

    /** ...and the blocks a box being drawn would take: its cells and edge, for the box of plots, the pick and the land it was found for. */
    private String previewFor = "";
    private List<double[]> previewCells = List.of();
    private double[][] previewEdge = new double[4][0];

    /** The picked blocks' shapes, kept (groundCells, groundEdge) for what is picked and the land it lies on. */
    private void groundShapes(CityLand land) {
        java.util.Set<Long> blocks = picks.blocks();
        if (blocks == groundFor && groundShapesFor == shapesFor) return;
        groundFor = blocks;
        groundShapesFor = shapesFor;
        if (blocks.isEmpty()) {
            groundCells = List.of();
            groundEdge = new double[4][0];
            return;
        }
        groundCells = LandMap.freeCells(land, blocks, picks.blocksLevel());
        groundEdge = LandMap.freeOutline(land, blocks, picks.blocksLevel());
    }

    /**
     * The blocks the box being drawn would take, as cells and an edge (previewCells, previewEdge): worked out when the plots
     * it covers, the pick or the land change, not every frame. False when no box is being drawn, or it covers no plot.
     */
    private boolean previewShapes(CityLand land) {
        if (!boxing || moved < CLICK_SLOP || !canPick()) return false;
        long[] pb = boxPlots(pressX, pressY, boxX, boxY);
        if (pb == null) return false;
        String key = pb[0] + "," + pb[1] + "," + pb[2] + "," + pb[3] + "|" + System.identityHashCode(picks.blocks()) + "|" + shapesFor;
        if (key.equals(previewFor)) return !previewCells.isEmpty();
        previewFor = key;
        List<Long> took = picks.boxBlocks(ui.game, pb[0], pb[1], pb[2], pb[3]);
        if (took.isEmpty()) {
            previewCells = List.of();
            previewEdge = new double[4][0];
            return false;
        }
        java.util.Set<Long> set = new java.util.LinkedHashSet<>(took);
        int level = ui.game.getLandManager().getMarket().getLevel();
        previewCells = LandMap.freeCells(land, set, level);
        previewEdge = LandMap.freeOutline(land, set, level);
        return true;
    }

    /** A run's dashes: 5 px on and 4 off, as the offers' edge has been drawn since 0.7.61. */
    private static final double DASH_ON = 5, DASH_PERIOD = 9;

    /**
     * Runs of an outline on whole pixels as one shape (BL1), `width` px deep inside the ground they bound - one the city's
     * edge's, two the picked ground's - solid or dashed (DASH_ON on and the rest off, from each run's own start so the dashes
     * stand still as the view pans), filled in one colour, so no pixel is drawn twice where runs meet.
     */
    private static void edgeRuns(GraphicsContext gc, MapFrame f, double[][] runs, int width, boolean dashed, Color colour) {
        gc.beginPath();
        for (int i = 0; i < runs[0].length; i++) runRects(gc, f, runs[0][i], runs[1][i], runs[2][i], runs[3][i], width, dashed);
        gc.setFill(colour);
        gc.fill();
    }

    /** One run of an outline added to the path as its pixels' boxes (MapFrame.runOnScreen()), `width` deep inside the ground it bounds and cut into dashes when asked. */
    private static void runRects(GraphicsContext gc, MapFrame f, double x0, double y0, double x1, double y1, int width, boolean dashed) {
        double[] r = f.runOnScreen(x0, y0, x1, y1);
        if (r == null) return;
        boolean flat = y0 == y1;
        double bx = r[0], by = r[1], bw = r[2], bh = r[3];
        // The ground lies on the run's right as it runs: south of a run east and west of one south (the run's pixel is the first
        // inside it, the second beyond that), north of one west and east of one north.
        if (width > 1) {
            if (flat) {
                if (x1 < x0) by -= width - 1;
                bh = width;
            } else {
                if (y1 > y0) bx -= width - 1;
                bw = width;
            }
        }
        if (!dashed) {
            gc.rect(bx, by, bw, bh);
            return;
        }
        double from = flat ? bx : by, to = from + (flat ? bw : bh);
        double origin = flat ? f.pixelX(Math.min(x0, x1)) : f.pixelY(Math.min(y0, y1));
        for (double s = origin + Math.floor((from - origin) / DASH_PERIOD) * DASH_PERIOD; s < to; s += DASH_PERIOD) {
            double a = Math.max(s, from), b = Math.min(s + DASH_ON, to);
            if (b <= a) continue;
            if (flat) gc.rect(a, by, b - a, bh);
            else gc.rect(bx, a, bw, b - a);
        }
    }

    /** The picked blocks (BL1): their free ground tinted as a picked offer's is and its edge solid in the offers' pink, two px deep, as one shape - the city's ground inside a block it owns part of left clear. No hatch at any scale, so the far views draw it as they draw it near. */
    private void pickedGround(GraphicsContext gc, MapFrame f) {
        CityLand land = ui.game.getCityLand();
        groundShapes(land);
        if (groundCells.isEmpty()) return;
        gc.beginPath();
        for (double[] c : groundCells) {
            double[] b = f.onScreen(c[0], c[1], c[2], c[3]);
            if (b != null) gc.rect(b[0], b[1], b[2] - b[0], b[3] - b[1]);
        }
        gc.setFill(Color.web(Palette.BUILDING, PICKED_ALPHA));
        gc.fill();
        edgeRuns(gc, f, groundEdge, 2, false, Color.web(OFFER_EDGE, 0.95));
    }

    /** The tint of a picked offer's ground and of picked blocks: the building pink at 0.22 (the picked offer's since 0.7.137). */
    static final double PICKED_ALPHA = 0.22;

    /** The blocks a box being drawn would take, tinted under it and edged a pixel wide (BL1), so the snap to the block grid is seen before the release. */
    private void boxPreview(GraphicsContext gc, MapFrame f) {
        if (!previewShapes(ui.game.getCityLand())) return;
        gc.beginPath();
        for (double[] c : previewCells) {
            double[] b = f.onScreen(c[0], c[1], c[2], c[3]);
            if (b != null) gc.rect(b[0], b[1], b[2] - b[0], b[3] - b[1]);
        }
        gc.setFill(Color.web(Palette.BUILDING, PICKED_ALPHA));
        gc.fill();
        edgeRuns(gc, f, previewEdge, 1, false, Color.web(OFFER_EDGE, 0.8));
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

    /** The hatch over the ground no port links (ET5): the offers' diagonals drawn in the warning amber (Palette.WARN, #e3b341) at the same weights. */
    private static ImagePattern islandHatchPattern() {
        WritableImage img = new WritableImage(8, 8);
        int line = 0xa0e3b341, shoulder = 0x50e3b341;
        for (int i = 0; i < 8; i++) {
            img.getPixelWriter().setArgb(i, 7 - i, line);
            if (i < 7) img.getPixelWriter().setArgb(i + 1, 7 - i, shoulder);
        }
        return new ImagePattern(img, 0, 0, 8, 8, false);
    }

    /* =====================================================================
       THE GLOBE (0.7.135, batch E9; docs/studies/spec-globe.md 3)
       ===================================================================== */

    /** The ground's colours on the globe, by code - a class and its relief (ET1 slice D): the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). */
    private static final int[] DIMMED = GlobePicture.colours(true);

    /** A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). */
    static final int PIECE_PX = MapFrame.NODE_PX;

    /** The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). */
    static final double RING_PX = 6;

    /** The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. */
    static final int NODE_SAMPLE_PX = 16;

    /** The globe drawn while it was half its resolution (the view on the move). */
    private boolean globeHalf;

    /** Whether the expanded map draws the globe: on an Earth world, wider than the handoff - or, since MS1, closer than it with the view's centre past the city's reach, where the plane is not drawn (GlobeFrame.tier()). */
    private boolean onGlobe() { return expanded && globe != null && globe.tier() != GlobeFrame.PLANE; }

    /** ...or lays its picture over the plane's in the crossfade's notch (GlobeFrame.crossfade(), the view's: whole where there is no plane under it). */
    private boolean globeShown() { return onGlobe() || (expanded && globe != null && globe.crossfade() > 0); }

    /** A new city, or another world: no globe until the map is over the window on Earth again - and no chart nodes (MS3): those in flight come back to another generation and are dropped. */
    private void resetGlobe() {
        resetGlobePicture();
        globe = null;
        globeKey = null;
        globeData = null;
        reachFor = Long.MIN_VALUE;
        charts.clear();
        chartsAsked.clear();
        chartsWanted = null;
        chartGen++;
        marksView = null;
        marks = List.of();
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
        chartNodesFor = null;
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

    /** The view moved on the globe: where it draws the plane (the plane's scales, its centre within the reach) the plane's frame follows it - past the reach, closer than the handoff, there is no plane to follow (MS1); the view drawn again. */
    private void follow() {
        if (globe != null && big != null && globe.tier() == GlobeFrame.PLANE) big.set(globe.planeX(), globe.planeY(), globe.planeScale());
        requestDraw();
    }

    /** The far nodes the globe reprojects for a view - {plots a pixel, nx0, nx1, ny0, ny1} - over the part of it the plane draws (GlobeFrame.drawsHold(): since MS2 at the plane's scales the part of it the world's square holds within GlobeFrame.handoverKm() of the city, MS3: the charts draw the rest, chartNodes()); null where it draws none. Kept for the view. */
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

    /**
     * The chart nodes the globe draws for a view (MS3) - {plots a pixel, the chart's id, nx0, nx1, ny0, ny1} - over the part of it the
     * view's chart draws (GlobeFrame.chartHolds(): at the plane's scales the ground past GlobeFrame.handoverKm() of the city and off
     * the world's square), sampled every NODE_SAMPLE_PX as globeNodes() is, each sample's place on the chart (GlobeFrame.chartAt(), in
     * chart pixels at the view's stride) cut into nodes of MapFrame.NODE_PX and padded by a sample's step; null where the chart draws
     * none. Kept for the view, as globeNodes()'s is.
     */
    private long[] chartNodes(GlobeFrame v) {
        if (chartNodesFor != null && chartNodesFor.sameView(v)) return chartNodesAt;
        chartNodesFor = v.copy(v.width(), v.height(), v.kmPx());
        long plots = MapFrame.nodePlotsAt(v.planeScale());
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        double[] xy = new double[2];
        double w = v.width(), h = v.height();
        for (double sy = 0.5; sy < h + NODE_SAMPLE_PX; sy += NODE_SAMPLE_PX) {
            for (double sx = 0.5; sx < w + NODE_SAMPLE_PX; sx += NODE_SAMPLE_PX) {
                double px = Math.min(sx, w - 0.5), py = Math.min(sy, h - 0.5);
                if (!v.chartHolds(px, py) || !v.chartAt(px, py, xy)) continue;
                x0 = Math.min(x0, xy[0]);
                y0 = Math.min(y0, xy[1]);
                x1 = Math.max(x1, xy[0]);
                y1 = Math.max(y1, xy[1]);
            }
        }
        if (x0 > x1) return chartNodesAt = null;
        // A sample's step in chart pixels (a screen pixel is 1 / (planeScale x plots) of one), and one more for the orthographic chart's squeeze.
        double pad = NODE_SAMPLE_PX / (v.planeScale() * plots) + 1, n = MapFrame.NODE_PX;
        chartNodesAt = new long[] { plots, v.chart().id(),
                (long) Math.floor((x0 - pad) / n), (long) Math.floor((x1 + pad) / n), (long) Math.floor((y0 - pad) / n), (long) Math.floor((y1 + pad) / n) };
        return chartNodesAt;
    }

    private GlobeFrame chartNodesFor;
    private long[] chartNodesAt;

    /** A chart node's indexes run negative west and north of the chart's anchor: this many are added to each to key it. */
    private static final long CHART_NODE_OFFSET = 1L << 19;

    /** A chart node's key (MS3): the chart's id (GlobeFrame.chartFor()'s row and column, 13 bits) in the top 20 bits, the stride's power of two in 4, and the node's two indexes in 20 bits each (a chart reaches some 400 nodes from its anchor at a plot a pixel). */
    private static long chartKey(long id, long plots, long nx, long ny) {
        return (id << 44) ^ ((long) Long.numberOfTrailingZeros(plots) << 40) ^ (((nx + CHART_NODE_OFFSET) & 0xfffff) << 20) ^ ((ny + CHART_NODE_OFFSET) & 0xfffff);
    }

    /** The chart nodes WORKER's queue reads from its own thread (chartNodes()'s range for the view being painted): a node asked for a view that has moved on is dropped unread. */
    private volatile long[] chartsWanted;

    /** The globe's generation as far as chart nodes go: moved by resetGlobe(), so a node that comes back from WORKER after it is dropped. */
    private int chartGen;

    /**
     * A frame's work on the globe: in the GROUND tier the plane's far nodes near the city, their ground asked of WORKER and coloured here
     * until the deadline, and the district pictures they lack; and the chart nodes past the hand-over (MS3), asked beside them. True while
     * there is more.
     */
    private boolean paintGlobe(long deadline) {
        if (globe.tier() != GlobeFrame.GROUND || shownMap == null) return false;
        boolean more = false;
        long[] r = globeNodes(globe);
        if (r != null) {
            nodes.most = Math.max(nodes.most, (r[2] - r[1] + 1) * (r[4] - r[3] + 1));
            MapFrame pf = new MapFrame(globe.width(), globe.height());
            pf.set(globe.planeX(), globe.planeY(), globe.planeScale());
            picturesWanted.clear();
            try {
                more = paintNodes(r[0], r[1], r[2], r[3], r[4], deadline, shownMap.picturesKept(), pf);
            } finally {
                askPictures(pf);
            }
        }
        askCharts(chartNodes(globe));
        return more;
    }

    /**
     * The chart nodes `r` (chartNodes()) the cache lacks asked of WORKER, each once, from the middle of the range out (MS3; askNodes()'s
     * rule for the plane's far nodes): WORKER makes the node's classes and relief (MapTiles.chartTerrain()) and its colours
     * (MapTiles.chartPixels()) - nothing in a chart node reads the city - and the FX thread keeps it and asks a frame. A node still
     * queued when the view has moved on is dropped unread (chartsAsked forgets it, so a view that comes back asks for it again); one that
     * fails logs a line and is not asked again, as a piece is not.
     */
    private void askCharts(long[] r) {
        chartsWanted = r;
        if (r == null) return;
        charts.most = Math.max(charts.most, (r[3] - r[2] + 1) * (r[5] - r[4] + 1));
        final long plots = r[0], id = r[1];
        List<long[]> asking = new ArrayList<>();
        for (long ny = r[4]; ny <= r[5]; ny++) {
            for (long nx = r[2]; nx <= r[3]; nx++) {
                long key = chartKey(id, plots, nx, ny);
                if (charts.get(key) == null && chartsAsked.add(key)) asking.add(new long[] { nx, ny, key });
            }
        }
        if (asking.isEmpty()) return;
        final double cx = (r[2] + r[3]) / 2.0, cy = (r[4] + r[5]) / 2.0;
        asking.sort((a, b) -> Double.compare(Math.hypot(a[0] - cx, a[1] - cy), Math.hypot(b[0] - cx, b[1] - cy)));
        final World.Key world = globeKey;
        final GlobeFrame.Chart chart = globe.chart();
        final int gen = chartGen, n = MapFrame.NODE_PX;
        for (long[] a : asking) {
            final long nx = a[0], ny = a[1], key = a[2];
            WORKER.submit(() -> {
                long[] w = chartsWanted;
                if (w == null || w[0] != plots || w[1] != id || nx < w[2] || nx > w[3] || ny < w[4] || ny > w[5]) {
                    Platform.runLater(() -> {
                        chartsAsked.remove(key);
                        requestDraw();
                    });
                    return;
                }
                byte[] relief = new byte[n * n], ground;
                int[] pixels = new int[n * n];
                try {
                    ground = MapTiles.chartTerrain(world, chart, plots, nx, ny, relief);
                    MapTiles.chartPixels(ground, relief, pixels);
                } catch (RuntimeException e) {
                    System.out.println("A chart's ground could not be drawn: " + e);
                    return;
                }
                Platform.runLater(() -> {
                    chartsAsked.remove(key);
                    if (gen != chartGen) return;
                    Img got = new Img();
                    got.ground = ground;
                    got.relief = relief;
                    got.pixels = pixels;
                    charts.put(key, got);
                    chartColoured(chart.id(), plots, nx, ny);
                    requestDraw();
                });
            });
        }
    }

    /** A chart node come in: where the globe's still picture shows it, laid over again next frame - nodeColoured()'s rule for the plane's, the node's corners and mid-points from its chart (east and south of the anchor on its orthographic plane) to the screen. */
    private void chartColoured(int id, long plots, long nx, long ny) {
        if (globe == null || !expanded || globeDrawn == null || globeHalf) return;
        GlobeFrame.Chart chart = globe.chart();
        if (chart.id() != id || MapFrame.nodePlotsAt(globe.planeScale()) != plots) return;
        EarthProj.Ortho o = chart.ortho();
        double n = MapFrame.NODE_PX, m = plots * World.PLOT_M;
        double[] ll = new double[2], box = null;
        for (int k = 0; k < 9; k++) {
            double[] p = o.inverse((nx * n + (k % 3) * n / 2) * m, -(ny * n + (k / 3) * n / 2) * m, ll) ? globe.at(ll[0], ll[1]) : null;
            if (p == null) {
                box = new double[] { 0, 0, globe.width(), globe.height() };
                break;
            }
            box = box == null ? new double[] { p[0], p[1], p[0], p[1] }
                    : new double[] { Math.min(box[0], p[0]), Math.min(box[1], p[1]), Math.max(box[2], p[0]), Math.max(box[3], p[1]) };
        }
        addPatch(box[0] - 2, box[1] - 2, box[2] + 2, box[3] + 2);
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

    /** The picture of frame t (the view, or its half) over its pixels [i0, i1) x [j0, j1), in bands on the frames' pool, into the image: the plane's nodes and, past the hand-over at the plane's scales, the view's chart nodes (MS3) laid by GlobeFrame.ground(). */
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
            // The view's chart nodes (MS3), snapped the same way: past the hand-over the chart draws what the plane's nodes do not.
            long[] cr = mode == MODE_FADE ? null : chartNodes(globe);
            Map<Long, int[]> chartSnap = new HashMap<>();
            if (cr != null) {
                for (long ny = cr[4]; ny <= cr[5]; ny++) {
                    for (long nx = cr[2]; nx <= cr[3]; nx++) {
                        Img img = charts.get(chartKey(cr[1], plots, nx, ny));
                        if (img != null && img.pixels != null) chartSnap.put(chartKey(cr[1], plots, nx, ny), img.pixels);
                    }
                }
            }
            final long chartId = cr == null ? 0 : cr[1];
            GlobeFrame.NodePixels cp = cr == null ? null : (nx, ny) -> chartSnap.get(chartKey(chartId, plots, nx, ny));
            GlobeFrame.Beyond beyond = mode == MODE_FADE ? (i, j, vx, vy, vz) -> 0 : beyond(t, pic);
            final int a0 = i0, a1 = i1, b0 = j0;
            GlobePicture.bands(j1 - j0, GlobePicture.cores(), (k0, k1) -> t.ground(plots, np, cp, beyond, GlobePicture.SPACE, px, w, a0, a1, b0 + k0, b0 + k1));
        }
        globeImg.getPixelWriter().setPixels(i0, j0, i1 - i0, j1 - j0, PixelFormat.getIntArgbInstance(), px, j0 * w + i0, w);
    }

    /** Past the plane and the charts, in the GROUND tier: the piece drawn for this view where it is in, else the last whole picture carried, else the planet's picture; space where there is none yet. At the plane's scales (MS3) no piece is asked where the plane or a chart draws, so the carried picture and the planet's are what show where a chart node is not in yet. */
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
            return DIMMED[pic.codeAt(GlobeFrame.latOf(vx, vy, vz), t.lonOf(vx, vy))];
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
     * far rule through the camera (GlobeFrame.groundCodes()) a PIECE_PX
     * square at a time on WORKER, from the middle out, each one with some
     * of its pixels on the planet and past where the plane draws - and only
     * while the view is still that one. Since MS2 the plane draws wherever
     * the world's square holds the ground at the plane's scales, and since
     * MS3 the view's chart draws the rest past the hand-over (off the
     * square too, GlobeFrame.chartHolds()), so a pixel the chart draws is
     * treated as one the plane draws and there is no piece at the plane's
     * scales; at the handoff and wider the pieces are asked as before.
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
                    plane &= pv.drawsHold(sx, sy) || pv.chartHolds(sx, sy);
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
                    b = pv.groundCodes(key, q[0], q[1], q[2], q[3]);
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

    /** The globe on the canvas: its picture, then over it the city - its offers and centre where the plane draws in the GROUND tier, its block lines past the reach at the plane's scales, its edge through the camera in the PICTURE tier (the GROUND tier's nodes draw it) once it is EDGE_FROM_PX across, its ring - and the scale bar. */
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
        globeNations(gc);
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        shapes(land, market);
        if (tier == GlobeFrame.GROUND) {
            globeBlockLines(gc, market.getLevel());
            globeOffers(gc, land, market);
        } else globeEdge(gc, land);
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
        placeNationCard();
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

    /**
     * The offers and the centre's box through the camera, where the plane draws (spec-globe 3.7): as the plane draws them at its
     * far level - each offer its free ground (its cells, LandMap.freeCells(), the city's own ground in its rectangle left out
     * as the plane leaves it out, BL1: the globe used to fill the whole rectangle over the city's edge) hatched, tinted, edged
     * along that ground's own outline (LandMap.freeOutline()) in pink, the office's side bright, the one lit or picked solid
     * and two px - numbered on the freest square of it (LandMap.labelBlock()) where that holds a number, then the picked
     * blocks and the box's preview, and the ground just bought.
     */
    private void globeOffers(GraphicsContext gc, CityLand land, LandMarket market) {
        ImagePattern pattern = new ImagePattern(hatch.getImage(), 0, 0, 8, 8, false);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
        gc.setFillRule(FillRule.NON_ZERO);
        List<Integer> chosen = picks.live(ui.game);
        if (globeLabelsFor != shapesFor) {
            globeLabelsFor = shapesFor;
            globeLabelAt.clear();
        }
        long blockPlots = 1L << market.getLevel();
        for (LandParcel p : market.getListing()) {
            double[][] q = onGlobe(new double[] { p.getX0(), p.getX1(), p.getX1(), p.getX0() }, new double[] { p.getY0(), p.getY0(), p.getY1(), p.getY1() });
            if (q == null) continue;
            double bx0 = Math.min(Math.min(q[0][0], q[0][1]), Math.min(q[0][2], q[0][3])), bx1 = Math.max(Math.max(q[0][0], q[0][1]), Math.max(q[0][2], q[0][3]));
            double by0 = Math.min(Math.min(q[1][0], q[1][1]), Math.min(q[1][2], q[1][3])), by1 = Math.max(Math.max(q[1][0], q[1][1]), Math.max(q[1][2], q[1][3]));
            if (bx1 < 0 || by1 < 0 || bx0 > globe.width() || by0 > globe.height()) continue;
            boolean mine = p.getSide() == side;
            boolean lit = (p.getSide() == overSide && p.getPlace() == overPlace) || (p.getSide() == litSide && p.getPlace() == litPlace);
            boolean picked = (p.getSide() == side && p.getPlace() == place) || chosen.contains(p.getId());
            double plotPx = plotPx(p.getX0(), p.getY0(), blockPlots);
            // Its free ground, every cell of it through the camera as one shape; where a block is under BLOCK_LINES_FROM px, no hatch.
            if (cellsPath(gc, freeCells.getOrDefault(p.getId(), List.of()))) {
                gc.setFill(Color.rgb(12, 18, 26, 0.24));
                gc.fill();
                if (plotPx * blockPlots >= MapFrame.BLOCK_LINES_FROM) {
                    gc.setFill(pattern);
                    gc.fill();
                }
                if (lit || picked) {
                    gc.setFill(Color.web(Palette.BUILDING, lit ? 0.34 : 0.22));
                    gc.fill();
                }
            }
            double[][] runs = freeEdge.get(p.getId());
            if (runs != null) runsOnGlobe(gc, runs, picked ? 2 : 1, !picked, Color.web(OFFER_EDGE, mine || lit ? 0.95 : 0.4));
            // Its number on the freest square of its free ground, else the middle of its whole box (the plane's rule).
            double[] box = { bx0, by0, bx1, by1 };
            double[] sq = labelSquare(land, p, plotPx);
            if (sq != null) {
                double[][] s = onGlobe(new double[] { sq[0], sq[2], sq[2], sq[0] }, new double[] { sq[1], sq[1], sq[3], sq[3] });
                if (s != null) {
                    double[] sb = { Math.min(Math.min(s[0][0], s[0][1]), Math.min(s[0][2], s[0][3])), Math.min(Math.min(s[1][0], s[1][1]), Math.min(s[1][2], s[1][3])),
                            Math.max(Math.max(s[0][0], s[0][1]), Math.max(s[0][2], s[0][3])), Math.max(Math.max(s[1][0], s[1][1]), Math.max(s[1][2], s[1][3])) };
                    if (MapFrame.labelFits(sb)) box = sb;
                }
            }
            if (MapFrame.labelFits(box)) {
                String words = LandMap.placeLabel(p);
                double x = Math.rint((box[0] + box[2]) / 2), y = Math.rint((box[1] + box[3]) / 2);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.setLineWidth(3);
                gc.setStroke(Color.rgb(11, 17, 24, 0.85));
                gc.strokeText(words, x, y);
                gc.setFill(Color.rgb(255, 255, 255, p.getSide() == side ? 0.95 : 0.6));
                gc.fillText(words, x, y);
                newTag(gc, p, x, y, box[3] - box[1]);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BASELINE);
            }
        }
        // The picked blocks, one shape; the blocks a box being drawn would take under it; the ground just bought, outlined with its receipt.
        groundShapes(land);
        if (cellsPath(gc, groundCells)) {
            gc.setFill(Color.web(Palette.BUILDING, PICKED_ALPHA));
            gc.fill();
            runsOnGlobe(gc, groundEdge, 2, false, Color.web(OFFER_EDGE, 0.95));
        }
        if (previewShapes(land) && cellsPath(gc, previewCells)) {
            gc.setFill(Color.web(Palette.BUILDING, PICKED_ALPHA));
            gc.fill();
            runsOnGlobe(gc, previewEdge, 1, false, Color.web(OFFER_EDGE, 0.8));
        }
        if (receiptShown && bought != null && bought.edge() != null) runsOnGlobe(gc, bought.edge(), 2, false, Color.web(OFFER_EDGE, 0.95));
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

    /** Pixels a plot spans on the globe where the plane's plot (x, y) is: the camera's step over `span` plots east of it over the span, 0 where either end is not seen. */
    private double plotPx(double x, double y, long span) {
        double[] a = globe.screenOfPlot(x, y), b = globe.screenOfPlot(x + span, y);
        return a == null || b == null ? 0 : Math.hypot(b[0] - a[0], b[1] - a[1]) / span;
    }

    /** Cells of plots (rectangles {x0, y0, x1, y1}) as one path through the camera, each a quadrilateral of its four corners - a cell with a corner not seen left out, as boxPicks() does; false when none is in the path. */
    private boolean cellsPath(GraphicsContext gc, List<double[]> cells) {
        boolean any = false;
        gc.beginPath();
        for (double[] c : cells) {
            double[] a = globe.screenOfPlot(c[0], c[1]), b = globe.screenOfPlot(c[2], c[1]), d = globe.screenOfPlot(c[2], c[3]), e = globe.screenOfPlot(c[0], c[3]);
            if (a == null || b == null || d == null || e == null) continue;
            gc.moveTo(a[0], a[1]);
            gc.lineTo(b[0], b[1]);
            gc.lineTo(d[0], d[1]);
            gc.lineTo(e[0], e[1]);
            gc.closePath();
            any = true;
        }
        return any;
    }

    /** The runs of an outline through the camera, a line each between its ends where both are seen: `width` px, solid or dashed as the plane's offers' edges are. */
    private void runsOnGlobe(GraphicsContext gc, double[][] runs, int width, boolean dashed, Color colour) {
        gc.setStroke(colour);
        gc.setLineWidth(width);
        if (dashed) gc.setLineDashes(DASH_ON, DASH_PERIOD - DASH_ON);
        for (int i = 0; i < runs[0].length; i++) {
            double[] a = globe.screenOfPlot(runs[0][i], runs[1][i]), b = globe.screenOfPlot(runs[2][i], runs[3][i]);
            if (a != null && b != null) gc.strokeLine(a[0], a[1], b[0], b[1]);
        }
        gc.setLineDashes((double[]) null);
    }

    /** Where an offer's number stands on the globe, the square of plots (LandMap.labelBlock()) found for the camera's scale - a number's PLACE_LABEL_FROM px in plots, to a power of two - and kept until the land moves; null where no scale is seen. */
    private double[] labelSquare(CityLand land, LandParcel p, double plotPx) {
        if (!(plotPx > 0)) return null;
        double least = MapFrame.PLACE_LABEL_FROM / plotPx;
        int bits = least <= 1 ? 0 : 64 - Long.numberOfLeadingZeros((long) Math.ceil(least) - 1);
        double[] kept = globeLabelAt.get(p.getId());
        if (kept == null || kept[4] != bits) {
            double[] sq = LandMap.labelBlock(land, p, 1L << bits);
            kept = new double[] { sq[0], sq[1], sq[2], sq[3], bits };
            globeLabelAt.put(p.getId(), kept);
        }
        return kept;
    }

    /** The squares the globe's numbers stand on, by offer id, for the land they were found for and the scale (their last element). */
    private final Map<Integer, double[]> globeLabelAt = new HashMap<>();
    private long globeLabelsFor = Long.MIN_VALUE;

    /**
     * The city's block lines through the camera past the reach at the plane's scales (MS2): the plane draws them faint over its whole
     * ground where a block is at least MapFrame.BLOCK_LINES_FROM px across (blockLines()), and they must not vanish as the camera
     * crosses the reach. Each multiple of a block's plots across the part of the world's square the view shows is a line of the plane's
     * plots with its ends through GlobeFrame.screenOfPlot(), split where the camera bends it by more than BLOCK_LINE_SAG_PX (at Jerus's
     * scale a line the length of the view stays within 0.2 px of its chord to 1,000 km from the city, 0.4 at 2,500 and 0.8 at 4,500; at
     * 4 and 8 plots a pixel, where a view is longer, the bend is 1 to 2.5 px at 2,500 km), all one shape in the plane's white at
     * BLOCK_LINE_ALPHA, a pixel wide.
     */
    private void globeBlockLines(GraphicsContext gc, int level) {
        if (!globe.planeDrawnPastReach() || level < LandGrid.MIN_LEVEL) return;
        long b = 1L << level;
        if (b * globe.planeScale() < MapFrame.BLOCK_LINES_FROM) return;
        long[] r = globeNodes(globe);
        if (r == null) return;
        double span = MapFrame.NODE_PX * (double) r[0];
        double x0 = r[1] * span, x1 = (r[2] + 1) * span, y0 = r[3] * span, y1 = (r[4] + 1) * span;
        gc.beginPath();
        boolean any = false;
        for (long k = (long) Math.ceil(x0 / b); k * b <= x1; k++) any |= blockLine(gc, k * b, y0, k * b, y1, 0);
        for (long k = (long) Math.ceil(y0 / b); k * b <= y1; k++) any |= blockLine(gc, x0, k * b, x1, k * b, 0);
        if (!any) return;
        gc.setStroke(Color.rgb(255, 255, 255, BLOCK_LINE_ALPHA));
        gc.setLineWidth(1);
        gc.stroke();
    }

    /** The most the camera may bend a block line from its chord before the line is split in two, in pixels: a quarter (the line is drawn a pixel wide, so a quarter is not seen). */
    static final double BLOCK_LINE_SAG_PX = 0.25;

    /** ...and how many times a line is split at most: 6 (64 pieces - far more than the 64 px a line bends by at the widest near scale 12,000 km from the city needs). */
    static final int BLOCK_LINE_SPLITS = 6;

    /** One block line from plot (ax, ay) to (bx, by) through the camera onto the path: its ends, and its middle's plot against the chord, in two where it strays more than BLOCK_LINE_SAG_PX. False where an end is not seen. */
    private boolean blockLine(GraphicsContext gc, double ax, double ay, double bx, double by, int depth) {
        double[] a = globe.screenOfPlot(ax, ay), b = globe.screenOfPlot(bx, by);
        if (a == null || b == null) return false;
        if (depth < BLOCK_LINE_SPLITS) {
            double mx = (ax + bx) / 2, my = (ay + by) / 2;
            double[] m = globe.screenOfPlot(mx, my);
            if (m != null && distanceFromChord(m, a, b) > BLOCK_LINE_SAG_PX) {
                boolean left = blockLine(gc, ax, ay, mx, my, depth + 1), right = blockLine(gc, mx, my, bx, by, depth + 1);
                return left || right;
            }
        }
        // A line wholly off the screen is no line.
        if ((a[0] < 0 && b[0] < 0) || (a[1] < 0 && b[1] < 0) || (a[0] > globe.width() && b[0] > globe.width()) || (a[1] > globe.height() && b[1] > globe.height())) return false;
        gc.moveTo(a[0], a[1]);
        gc.lineTo(b[0], b[1]);
        return true;
    }

    /** How far a point is from the chord a to b, in pixels. */
    static double distanceFromChord(double[] m, double[] a, double[] b) {
        double dx = b[0] - a[0], dy = b[1] - a[1], len = Math.hypot(dx, dy);
        return len == 0 ? Math.hypot(m[0] - a[0], m[1] - a[1]) : Math.abs(dx * (m[1] - a[1]) - dy * (m[0] - a[0])) / len;
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

    /** The hover on the globe (spec-globe 3.7): the offer the plane lights there, and the card - LandMap.globeHoverWords(), the ground drawn there where the plane does not draw it (drawnAt(): past the hand-over, MS3, the chart node's under the pointer; the plane holds no plot there, so the card says the globe's words, "Not the city's", and no offer is lit). */
    private void globeHover(double sx, double sy) {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        int os = -1, op = -1;
        boolean boxes = false;
        List<String> ground = null;
        double[] xy = new double[2];
        if (globe.planeHolds(sx, sy) && globe.plotAt(sx, sy, xy)) {
            long x = (long) Math.floor(xy[0]), y = (long) Math.floor(xy[1]);
            LandMap.Pick p = LandMap.pick(land, market, x, y);
            if (p.owner() == LandMap.OFFER) {
                os = p.side();
                op = p.place();
            }
            // Ground the player may pick, or has picked (BL1): where a press would box, and what the card says of it.
            if (canPick()) {
                boxes = picks.pressBoxes(g, x, y, false);
                if (os < 0) ground = groundLines(x, y);
            }
        }
        // The nations (WN2): over a dot, its figures alone and no offer lit; over a nation's or an area's land, whose it is, first.
        List<String> dotWords = nationHoverWords(sx, sy);
        String owner = dotWords == null ? ownerWords(sx, sy) : null;
        if (dotWords != null) {
            os = op = -1;
            boxes = false;
            ground = null;
        }
        if (os != overSide || op != overPlace) {
            overSide = os;
            overPlace = op;
            dirty = true;
        }
        cursorFor(boxes);
        if (dotWords != null) {
            if (!pressed) canvas.setCursor(javafx.scene.Cursor.HAND);
            showCard(dotWords, sx, sy, globe.width(), globe.height());
            return;
        }
        List<String> lines = new ArrayList<>(LandMap.globeHoverWords(globe, land, market, sx, sy, drawnAt(sx, sy), CARD_FIELDS));
        // An offer drawn here - the GROUND tier's - is bought with a click (0.7.137).
        if (os >= 0 && canPick()) lines.add(CLICK_TO_BUY);
        if (ground != null) groundInto(lines, ground);
        if (owner != null) lines.add(0, owner);
        showCard(lines, sx, sy, globe.width(), globe.height());
    }

    /** The hover card's last line over a block that is picked already (BL1), in blue as the offers' is: how to drop it. */
    static final String CLICK_TO_DROP = "Shift-click to drop it · Enter buys";

    /**
     * What the hover card says over free ground that is no offer's (BL1), on the expanded map where the offers are drawn to be
     * picked: over a block that may be picked, "Your ground: one block", its area by kind and its price in the toggle's money
     * - the price the office would list it at today, LandMarket.quote() - and CLICK_TO_PICK last; over a picked block, the
     * whole pick's - its offers' and its blocks' - area and price and CLICK_TO_DROP. Null elsewhere: over the city's own ground,
     * the world's edge, and a block that is joined to nothing (the card says "Not the city's" as it did).
     */
    private List<String> groundLines(long x, long y) {
        Game g = ui.game;
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        int level = market.getLevel();
        long key = LandMap.blockAt(level, x, y);
        if (key < 0 || land.ownsPlot(x, y)) return null;
        List<String> out = new ArrayList<>();
        if (picks.blocks().contains(key)) {
            List<Integer> ids = picks.live(g);
            LandMap.Card c = LandMap.card(g, ids);
            int offers = 0;
            for (int id : ids) if (id > 0) offers++;
            out.add(LandMap.pickedWords(offers, picks.blocks().size()));
            out.add(LandMap.kindWords(c.km2(), c.dryKm2(), c.freshKm2(), c.seaKm2()));
            out.add("Price " + ui.landScreen.inToggle(c.usd()));
            out.add(CLICK_TO_DROP);
            return out;
        }
        if (!picks.pickable(g, key)) return null;
        // The office measures the block as it would an offer of it - its ground, fields and price - once for the pointer's stay over it.
        String quoteKey = key + "|" + level + "|" + shapesFor + "|" + g.getMonth();
        if (!quoteKey.equals(quotedFor)) {
            long[] r = LandMap.blockRect(level, key);
            quoted = market.quote(r[0], r[1], r[2], r[3]);
            quotedFor = quoteKey;
        }
        LandParcel q = quoted;
        if (q == null) return null;
        out.add(LandMap.ONE_BLOCK);
        out.add(LandMap.kindWords(q.getKm2(), q.getKm2(CityLand.DRY), q.getKm2(CityLand.FRESH), q.getKm2(CityLand.SEA)));
        out.add("Price " + ui.landScreen.inToggle(q.getPriceUsd()));
        out.add(LandMap.CLICK_TO_PICK);
        return out;
    }

    /** The block the hover last quoted (LandMarket.quote()) and what it was for: the block, the city's level, its ground and the month. */
    private String quotedFor = "";
    private LandParcel quoted;

    /** The ground lines in the hover card where "Not the city's" stood (after its first line when the card has not that line), their last line, the blue one, at the card's foot. */
    private static void groundInto(List<String> lines, List<String> ground) {
        int at = lines.indexOf(LandMap.NOT_THE_CITYS);
        if (at < 0) at = Math.min(1, lines.size());
        else lines.remove(at);
        lines.addAll(at, ground.subList(0, ground.size() - 1));
        lines.add(ground.get(ground.size() - 1));
    }

    /**
     * The ground drawn at a screen point on the globe, past the plane, as a code - its class and relief (GlobePicture.code()): where the
     * view's chart draws it (GlobeFrame.chartHolds(), MS3: at the plane's scales the ground past the hand-over) the chart node's class and
     * relief under the pixel, once the node is in; else the GROUND tier's piece where one is in, else the planet's picture's texel.
     */
    private byte drawnAt(double sx, double sy) {
        if (globe.chartHolds(sx, sy)) {
            byte code = chartCode(sx, sy);
            if (code >= 0) return code;
        }
        int i = (int) sx, j = (int) sy, w = (int) globe.width(), h = (int) globe.height();
        if (pieceCls != null && pieceView != null && pieceView.sameView(globe) && i >= 0 && j >= 0 && i < w && j < h) {
            byte c = pieceCls[j * w + i];
            if (c >= 0) return c;
        }
        GlobePicture pic = globeData == null ? null : GlobePicture.best(globeData);
        double[] ll = globe.latLonAt(sx, sy);
        return pic != null && ll != null ? pic.codeAt(ll[0], ll[1]) : World.SALT;
    }

    /** The code (GlobePicture.code()) of the pixel of the view's chart node under a screen point - the node ground() lays there (GlobeFrame.chartAt()) - or -1 where the node is not in yet. */
    private byte chartCode(double sx, double sy) {
        double[] xy = new double[2];
        if (!globe.chartAt(sx, sy, xy)) return -1;
        int n = MapFrame.NODE_PX;
        long nx = (long) Math.floor(xy[0] / n), ny = (long) Math.floor(xy[1] / n);
        Img img = charts.get(chartKey(globe.chart().id(), MapFrame.nodePlotsAt(globe.planeScale()), nx, ny));
        if (img == null || img.ground == null || img.relief == null) return -1;
        int u = Math.min(n - 1, (int) (xy[0] - nx * n)), v = Math.min(n - 1, (int) (xy[1] - ny * n));
        return GlobePicture.code(img.ground[v * n + u], img.relief[v * n + u]);
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
            hoverShift = e.isShiftDown();
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
            // A second button pressed over the first starts the press again as its own: only its release ends it.
            pressed = true;
            pressButton = e.getButton();
            pressX = lastX = boxX = e.getX();
            pressY = lastY = boxY = e.getY();
            moved = 0;
            // A left drag that starts on an offer's free ground draws a box (MS1, Jerus: "drag and select"), as does Shift and
            // a drag from anywhere (0.7.137, B7); a left drag from anywhere else pans, and a right or middle drag always does -
            // even from an offer - where the offers are drawn to be picked (LandMap.pressBoxes()).
            boxing = pressButton == javafx.scene.input.MouseButton.PRIMARY && pressBoxes(e.getX(), e.getY(), e.isShiftDown());
            if (expanded) {
                canvas.requestFocus();
                canvas.setCursor(boxing ? javafx.scene.Cursor.CROSSHAIR : javafx.scene.Cursor.OPEN_HAND);
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (!pressed) return;
            moved = Math.max(moved, Math.hypot(e.getX() - pressX, e.getY() - pressY));
            if (boxing) {
                boxX = e.getX();
                boxY = e.getY();
                if (card != null && moved >= CLICK_SLOP) card.setVisible(false);
                requestDraw();
            } else if (expanded) {
                canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
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
            // Only the press's own button lets it go; and a right or middle press never clicks (MS1).
            if (!pressed || e.getButton() != pressButton) return;
            boolean primary = pressButton == javafx.scene.input.MouseButton.PRIMARY;
            boolean click = primary && moved < CLICK_SLOP, box = boxing && !click;
            pressed = false;
            boxing = false;
            // The cursor is read again for where the pointer is now.
            hoverAt = new double[] { e.getX(), e.getY() };
            hoverShift = e.isShiftDown();
            if (box) boxed(pressX, pressY, e.getX(), e.getY());
            else if (click) click(e.getX(), e.getY(), e.isShiftDown());
            else requestDraw();
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
     * The plot of the city's plane under a screen point as the map draws it - the plane's own frame, or on the globe the
     * camera's, where the plane is drawn (GlobeFrame.planeHolds()) - or null where there is none (the globe's ground
     * the plane does not draw, off the planet): what a click and a press read the ground under the pointer by.
     */
    private long[] plotAt(double sx, double sy) {
        double[] xy = plotXY(sx, sy);
        return xy == null ? null : new long[] { (long) Math.floor(xy[0]), (long) Math.floor(xy[1]) };
    }

    /** ...as the plane's own fractions of plots {x, y} (what a box's corners are read by: LandMap.plotBox()), or null where there is none. */
    private double[] plotXY(double sx, double sy) {
        if (onGlobe()) {
            double[] xy = new double[2];
            if (!globe.planeHolds(sx, sy) || !globe.plotAt(sx, sy, xy)) return null;
            return xy;
        }
        MapFrame f = frame();
        return new double[] { f.plotX(sx), f.plotY(sy) };
    }

    /**
     * The plots a box between two screen points covers (LandMap.plotBox()), its four corners read through plotXY() - the
     * plane's frame, or on the globe the camera where the plane is drawn, a corner it does not hold left out - or null where no
     * corner has a plot: what a release takes (Picks.addBox()) and the preview shows (Picks.boxBlocks()).
     */
    private long[] boxPlots(double sx0, double sy0, double sx1, double sy1) {
        double[] xs = new double[4], ys = new double[4];
        int n = 0;
        for (double[] c : new double[][] { { sx0, sy0 }, { sx1, sy0 }, { sx1, sy1 }, { sx0, sy1 } }) {
            double[] xy = plotXY(c[0], c[1]);
            if (xy == null) continue;
            xs[n] = xy[0];
            ys[n] = xy[1];
            n++;
        }
        return n == 0 ? null : LandMap.plotBox(java.util.Arrays.copyOf(xs, n), java.util.Arrays.copyOf(ys, n));
    }

    /**
     * Whether a left press at a screen point draws a box rather than pans (MS1, widened by BL1): where the offers are drawn
     * to be picked (canPick()), Picks.pressBoxes() of the plot under it - an offer's free ground, a picked block or one that
     * may be picked, or Shift - and Shift alone where the plane is not drawn under the press; never on the small map or where
     * no offers are drawn.
     */
    private boolean pressBoxes(double sx, double sy, boolean shift) {
        if (!expanded || !canPick()) return false;
        long[] at = plotAt(sx, sy);
        if (at == null) return shift;
        return picks.pressBoxes(ui.game, at[0], at[1], shift);
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
        // A nation's dot, or its land where the city's plane is not drawn (WN2): its card, and nothing else.
        if (!shift && nationClick(sx, sy)) return;
        Game g = ui.game;
        long[] at = plotAt(sx, sy);
        if (at == null) {
            if (expanded && !shift) closeCard();
            return;
        }
        LandMap.Pick p = LandMap.pick(g.getCityLand(), g.getLandManager().getMarket(), at[0], at[1]);
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
            } else if (canPick() && blockClick(at[0], at[1], shift)) {
                requestDraw();
                return;
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

    /**
     * A click on ground that is no offer's (BL1), on the expanded map where the offers are drawn to be picked: a free block
     * that may be picked is picked alone, and with Shift toggled; a block already picked keeps the picks and opens the card
     * beside it, and with Shift is dropped (with the blocks that joined through it). A block that may not be picked - the
     * city's own ground, a block two away from anything joined to it, the sea past the world's edge - is not handled: it
     * closes the card as a click off the offers has, and Shift on it does nothing. True when the click was a block's.
     */
    private boolean blockClick(long x, long y, boolean shift) {
        Game g = ui.game;
        LandMarket market = g.getLandManager().getMarket();
        int level = market.getLevel();
        long key = LandMap.blockAt(level, x, y);
        if (key < 0 || g.getCityLand().ownsPlot(x, y)) return false;
        boolean held = picks.blocks().contains(key);
        if (shift) {
            if (!held && !picks.pickable(g, key)) return false;
            bought = null;
            picks.toggleBlock(g, key);
            cardAt = bounds(listed(picks.live(g)));
            showBuyCard();
            return true;
        }
        if (!held && !picks.pickable(g, key)) return false;
        bought = null;
        if (!held) picks.onlyBlock(g, key);
        long[] r = LandMap.blockRect(level, key);
        cardAt = new double[] { r[0], r[1], r[2], r[3] };
        showBuyCard();
        return true;
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
        // The crosshair where a press would box (LandMap.pressBoxes(): an offer's free ground, a block picked or one that may be).
        cursorFor(canPick() && picks.pressBoxes(g, x, y, false));
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
            String island = LandMap.islandWords(land, ui.game.getLandManager().getLandmasses(), p);
            readout.setText(LandMap.ownerWords(land, p) + " · " + (what != null ? what : LandMap.plotGroundWords(land, p.ground(), x, y))
                    + (island == null ? "" : " · " + island));
            showIf(readout, true);
            return;
        }
        // The model's card (since 0.7.135 LandMap.hoverWords() itself, as the globe's is): the ground, whose, on Earth (0.7.119)
        // where it is by the nearest place and its geology, the fields - with at L0 the building or road first.
        List<String> lines = new ArrayList<>();
        if (what != null) lines.add(what);
        lines.addAll(LandMap.hoverWords(land, market, ui.game.getLandManager().getLandmasses(), x, y, CARD_FIELDS));
        // ...and over an offer, how to buy it (0.7.137, board 4); over ground that is no offer's but may be picked, or is, the
        // ground's own words (BL1).
        if (os >= 0 && canPick()) lines.add(CLICK_TO_BUY);
        else if (canPick()) {
            List<String> ground = groundLines(x, y);
            if (ground != null) groundInto(lines, ground);
        }
        showCard(lines, sx, sy, f.width(), f.height());
    }

    /**
     * The cursor says what a left press would do (MS1, BL1): the crosshair where it would draw a box - over an offer's free
     * ground or ground that is picked or may be (`boxHere`, LandMap.pressBoxes() without Shift), or anywhere with Shift held
     * where the offers are drawn to be picked - the open hand where it would pan, the default on the small map. A press held
     * keeps its own (the closed hand panning, the crosshair boxing).
     */
    private void cursorFor(boolean boxHere) {
        if (pressed) return;
        canvas.setCursor(!expanded ? javafx.scene.Cursor.DEFAULT
                : canPick() && (boxHere || hoverShift) ? javafx.scene.Cursor.CROSSHAIR : javafx.scene.Cursor.OPEN_HAND);
    }

    /** The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. */
    static final String CLICK_TO_BUY = "Click to buy it here";

    /** Whether a hover card's line is the blue last line telling the player what a click does. */
    private static boolean isHint(String line) {
        return CLICK_TO_BUY.equals(line) || LandMap.CLICK_TO_PICK.equals(line) || CLICK_TO_DROP.equals(line) || NationsMap.CLICK_FOR_CARD.equals(line);
    }

    /** The expanded map's hover card: its lines, the first bold, by the pointer and inside the view - the hint last (CLICK_TO_BUY, CLICK_TO_PICK, CLICK_TO_DROP, and a nation's NationsMap.CLICK_FOR_CARD) in the accent's light blue. */
    private void showCard(List<String> lines, double sx, double sy, double vw, double vh) {
        if (lines.isEmpty()) {
            card.setVisible(false);
            return;
        }
        card.getChildren().clear();
        for (int i = 0; i < lines.size(); i++) {
            Label l = new Label(lines.get(i));
            l.setStyle(i == 0 ? Palette.strong(Palette.SIZE_BODY, Palette.TEXT)
                    : isHint(lines.get(i)) ? Palette.strong(Palette.SIZE_LABEL, Palette.ACCENT_LIGHT)
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

    /**
     * A purchase from the card: the offers and drafts found listed when it was asked for, their places ({side, place, 1 for a
     * draft or 0} each - a draft's place is where its middle lies, which no "next ground" line is about), the card's title for
     * them (LandMap.pickTitle()), the ground they stand on, in plots, and that ground's edge as runs (LandMap.freeOutline(),
     * worked out before the purchase made it the city's): outlined while the receipt stands (BL1).
     */
    record Bought(List<Integer> ids, List<int[]> places, String title, double[] at, double[][] edge) { }

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

    /**
     * A drag let go (B7, MS1; BL1: a box takes ground, not offers): the blocks the box covers that join the city added to the
     * picks (Picks.addBox(), the box's corners through plotXY(): the snap to the block grid the preview showed) - an offer
     * whose whole free ground they cover is the offer, bought at its listed price - the card beside what is picked.
     */
    private void boxed(double x0, double y0, double x1, double y1) {
        Game g = ui.game;
        long[] pb = boxPlots(x0, y0, x1, y1);
        if (pb != null && picks.addBox(g, pb[0], pb[1], pb[2], pb[3]) > 0) {
            bought = null;
            cardAt = bounds(listed(picks.live(g)));
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
        // One card at a time (WN2): a nation's goes when the buying card comes.
        hideNationCard();
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
        hideNationCard();
        requestDraw();
    }

    /** A chip's × on a card of several: that one dropped. */
    void dropPick(int id) {
        picks.drop(id);
        showBuyCard();
        requestDraw();
    }

    /** The "Your ground" chip's ×: every draft of the picked blocks dropped, which drops the blocks they were cut from - one chip for all of them, as the card names them as one (blocks that joined through them go too, said in the head). */
    void dropGround(List<Integer> drafts) {
        for (int id : drafts) picks.drop(id);
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
            places.add(new int[] { p.getSide(), p.getPlace(), p.getId() < 0 ? 1 : 0 });
        }
        Game g = ui.game;
        return new Bought(found, places, LandMap.pickTitle(ps, g.getLandManager().getMarket().getLevel()), bounds(ps),
                ps.isEmpty() ? null : LandMap.freeOutline(g.getCityLand(), ps));
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
            picks.clear();
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
        // "2 of 3 offers" when it fell short; "2 of 3 picks" where the card's title is not a count of offers (it names a place or the player's ground).
        String title = places.size() == b.ids().size() ? b.title()
                : places.size() + " of " + (b.title().endsWith(" offers") ? b.title() : b.ids().size() + " picks");
        // ...and only the offers' places list their next ground (a draft's is no offer's place).
        List<int[]> offerPlaces = new ArrayList<>();
        for (int[] sp : places) if (sp[2] == 0) offerPlaces.add(new int[] { sp[0], sp[1] });
        buyBox.getChildren().setAll(ui.landScreen.mapReceiptNode(title, offerPlaces, this));
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
        // A nation's card first (WN2): there is only ever one card, so this and the buying card's rule never both apply.
        if (k == KeyCode.ESCAPE && nationBox != null && nationBox.isVisible()) {
            hideNationCard();
            requestDraw();
            return true;
        }
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

    /**
     * The head's money, pay toggle and note, drawn again when the toggle, the cash, the vault, the month or the note moved. The
     * note is what the picks lost to the last month or purchase (LandMap.Picks.dropNote(), MS1): shown in the warning colour
     * while there is one, its tooltip the same line, until the player next changes the picks by hand. The picks are looked at
     * first (live() is cheap while every pick is listed), so a drop is on the head the frame it is found and not the next.
     */
    private void syncHead() {
        if (headMoney == null) return;
        syncChips();
        Game g = ui.game;
        picks.live(g);
        String said = picks.dropNote();
        String key = g.isLandPaidFromVault() + "|" + g.getCash() + "|" + g.getForeignAccounts().getReservesUsd() + "|" + g.getMonth()
                + "|" + said;
        if (key.equals(headFor)) return;
        headFor = key;
        headNote.setText(said == null ? "" : said);
        headNoteTip.setText(said == null ? "" : said);
        showIf(headNote, said != null);
        headMoney.setText(ui.landScreen.mapHeadMoney());
        // The office's chips on one line, the chip strip's 6 px apart: a FlowPane would ask for its whole wrap length in the head.
        headPay.getChildren().setAll(new ArrayList<>(ui.landScreen.mapPayChips(this).getChildren()));
    }

    /* =====================================================================
       THE NATIONS AND THEIR BORDERS (WN2; docs/studies/spec-nations.md 6.1, 8)

       Jerus: "a toggle on the map ui", "a map button to ... toggle borders",
       "dot ... each nation stats on their land ... canada would show gdp and
       ... population", "clicking would show more stats", "legend should be
       collapsable". The model half is NationsMap (the borders at a view's level
       of detail, the dots and where the names fit, what is under a point, the
       card): this is the toolkit's, and it recomputes no figure, colour or
       placement. THE MAP MUST NOT GET SLOWER (MP1 made it fast): nothing is
       drawn or read of the nations while a chip is off; the file is read on the
       FX thread only for a click's or a hover's one ownerAt(); a view at rest
       draws from NationsMap's caches and the marks kept here. THE TRADE ROUTES
       (WN3, board 1) are TradeLanes': the lanes of the city's trade by partner,
       shared and branching, cached by the month's figures, culled to the view
       and stroked as one path a width; on a generated world, the biggest
       partners as arrows at the edge. Their figures come from one seam,
       tradeFeed().
       ===================================================================== */

    /** The Nations chip's tooltip (WN2). */
    static final String NATIONS_TIP = "A dot at each nation's largest port or city, by its output; its name, output and people on its land where they fit."
            + " Click one for its card.";

    /** ...and the Borders chip's. */
    static final String BORDERS_TIP = "The nations' borders, dashed white, at the far views; up close only where one crosses the city's ground.";

    /** ...and the Trade routes chip's (WN3). */
    static final String TRADE_TIP = "This month's trade as routes from the city: violet what we sell, sand what we buy, the width by money, none under D$0.3M a month."
            + " Partners the same way share a lane. On a generated world, the biggest partners as arrows at the map's edge.";

    /** The legend's words for the two kinds of route (the board's, less its "it"). */
    static final String TRADE_SELL_WORDS = "What we sell, a month", TRADE_BUY_WORDS = "What we buy";

    /** A route's colours: the business violet for what we sell and the ore's sand for what we buy (Palette's), at the board's stroke opacity, .78. */
    static final double TRADE_ALPHA = 0.78;
    static final Color TRADE_SELL = Color.web(Palette.BUSINESS, TRADE_ALPHA), TRADE_BUY = Color.web(Palette.ORE, TRADE_ALPHA);

    /** A generated world's arrow is this many pixels long from its tip inward... */
    static final double ARROW_LEN_PX = 34;

    /** ...with a head this many pixels long, and this much wider than the shaft either side. */
    static final double ARROW_HEAD_PX = 9, ARROW_HEAD_WIDE_PX = 3;

    /** A stand-in for a feed that gave no figures. */
    private static final double[] NO_FIGURES = new double[0];

    /** A dot's label, the name: sans semibold, 11 px (the board's .lbl)... */
    static final double LABEL_NAME_PX = 11;

    /** ...and the figures under it, mono, 9.5 px (the board's .lbl2). */
    static final double LABEL_FIGURES_PX = 9.5;

    /** A dot's edge, in pixels: 1.2, in NationsMap.DOT_EDGE_ARGB (the board's). */
    static final double DOT_EDGE_PX = 1.2;

    /** A label's halo, drawn under its words: 3 px wide in NationsMap.DOT_EDGE_ARGB at 0.75 (the board's .lbl stroke). */
    static final double HALO_PX = 3, HALO_ALPHA = 0.75;

    /** The widths of label words kept, by string (the names never change and the figures change once a year): the 600 most recently used. */
    static final int WIDTHS_MOST = 600;

    /** A nation's card is this wide, in pixels: 330 (the board's .card)... */
    static final double NATION_CARD_W = 330;

    /** ...and its head, its distance line and its rows stand this many pixels apart: 4. */
    static final double NATION_CARD_GAP = 4;

    /**
     * The nations' map, built once for the process on WORKER from the world file, which is kept open for the game's life - the map's exact
     * ownerAt() reads it, so it is never closed. Null until the first Earth map asks (askNationsBuild()); it completes with null where the file
     * is not here or will not open (one println says so) and the chips are not shown.
     */
    private static volatile CompletableFuture<NationsMap> nationsBuild;

    /** The build asked of WORKER the first time it is wanted. */
    private static synchronized CompletableFuture<NationsMap> askNationsBuild() {
        if (nationsBuild == null) nationsBuild = CompletableFuture.supplyAsync(MapView::buildNations, WORKER);
        return nationsBuild;
    }

    /** The world file opened and the map made of it, on WORKER; null (and a line in the log) where there is no file or it will not open. */
    private static NationsMap buildNations() {
        java.nio.file.Path file = WorldData.locate();
        if (file == null) {
            System.out.println("The nations are not on the map: the world file is not here.");
            return null;
        }
        WorldData data = null;
        try {
            data = WorldData.open(file);
            return NationsMap.of(data);
        } catch (java.io.IOException | RuntimeException e) {
            System.out.println("The nations are not on the map: the world file " + file + " would not open: " + e);
            if (data != null) {
                try {
                    data.close();
                } catch (java.io.IOException ignored) {
                    // Nothing more to do with a file that will not close.
                }
            }
            return null;
        }
    }

    /** The nations' figures of the game on the screen (Game.getNations(), asked on WORKER: its first build is the path to 2100), and the game they are of; and the game they were asked for. */
    private Nations nations;
    private Game nationsOf, nationsAskedOf;
    /** Whether this view waits for the map's build to come back (it draws again when it does). */
    private boolean nationsWaiting;

    /** The dots and labels of the last view drawn, and what they were made for: the view (a copy), the year, the figures and the map. The hover and the click read the same list. */
    private List<NationsMap.Mark> marks = List.of();
    private GlobeFrame marksView;
    private int marksYear;
    private Nations marksFrom;
    private NationsMap marksOf;

    /** The label fonts, and the width of words in them kept by string (the names never change, the figures once a year). */
    private javafx.scene.text.Font nameFont, figuresFont;
    private final javafx.scene.text.Text measure = new javafx.scene.text.Text();
    private final Map<String, Double> nameWidths = widths(), figuresWidths = widths();
    private final java.util.function.ToDoubleFunction<String> nameW = s -> widthOf(s, nameFont, nameWidths),
            figuresW = s -> widthOf(s, figuresFont, figuresWidths);

    /** The plane's projection round the city's spot, kept per world (the borders that cross the city's ground are found on it). */
    private EarthProj planeProj;
    private World.Key planeProjKey;

    /** The city's trade as the screen holds it (WN3): the lanes of the last figures and the map they were made for, a generated world's arrows. */
    private TradeLanes lanes;
    private NationsMap lanesOf;
    private TradeLanes.Arrows arrows;

    /** The head's chips and their group, and what they were last styled for. */
    private Label nationsChip, bordersChip, tradeChip;
    private HBox nationsGroup;
    private String chipsFor;

    /** A nation's card in the pane, the nation it is of (0 for none), what it was drawn for, and the point on the screen it stands beside. */
    private VBox nationBox;
    private int nationShown;
    private String nationKey;
    private double nationX, nationY;

    /** A bounded map of widths by string, the most recently used kept. */
    private static Map<String, Double> widths() {
        return new LinkedHashMap<String, Double>(128, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, Double> e) { return size() > WIDTHS_MOST; }
        };
    }

    /** The width of words in a font, kept: a Text measured once per string. */
    private double widthOf(String words, javafx.scene.text.Font font, Map<String, Double> kept) {
        Double w = kept.get(words);
        if (w == null) {
            measure.setFont(font);
            measure.setText(words);
            w = measure.getLayoutBounds().getWidth();
            kept.put(words, w);
        }
        return w;
    }

    /** Another city or another game: the nations of the last one are not this one's. */
    private void forgetNations() {
        nations = null;
        nationsOf = null;
        nationsAskedOf = null;
        marksView = null;
        marks = List.of();
        lanes = null;
        lanesOf = null;
        arrows = null;
    }

    /**
     * Asked every frame (keepUp()): on an Earth world, once a chip is on or the map is over the window - the chips are shown only once the
     * map is built, so a player who turned both off can turn them on - the nations' map is asked of WORKER (once for the process) and, when
     * Nations is on, the game's nations (once for the game). Each comes back on the FX thread and draws the view again. Nothing is asked
     * of a generated world but, with Trade routes on and the feed naming a stand-in place (WN3), the map alone: its nodes are where the arrows stand.
     */
    private void nationsUp(CityLand land) {
        if (ui.prefs == null) return;
        boolean earth = NationsMap.drawsOn(land.key());
        if (!earth && !(expanded && ui.prefs.isTradeRoutesOnMap() && standsIn())) return;
        boolean nationsOn = earth && ui.prefs.isNationsOnMap();
        if (earth && !nationsOn && !ui.prefs.isBordersOnMap() && !ui.prefs.isTradeRoutesOnMap() && !expanded) return;
        CompletableFuture<NationsMap> build = askNationsBuild();
        if (!build.isDone() && !nationsWaiting) {
            nationsWaiting = true;
            build.whenComplete((m, e) -> Platform.runLater(() -> {
                nationsWaiting = false;
                requestDraw();
            }));
        }
        Game g = ui.game;
        if (nationsOn && nationsAskedOf != g) {
            nationsAskedOf = g;
            WORKER.submit(() -> {
                Nations made = null;
                try {
                    made = g.getNations();
                } catch (RuntimeException e) {
                    System.out.println("The nations could not be made for the map: " + e);
                }
                Nations got = made;
                Platform.runLater(() -> {
                    if (ui.game == g) {
                        nations = got;
                        nationsOf = g;
                    }
                    requestDraw();
                });
            });
        }
    }

    /** The nations' map when it is built and this city stands on the real Earth; null on a generated world, before the build and where the world file is not here. */
    private NationsMap nationsMap() {
        Game g = ui.game;
        CompletableFuture<NationsMap> build = nationsBuild;
        if (g == null || build == null || !build.isDone() || build.isCompletedExceptionally() || !NationsMap.drawsOn(g.getCityLand().key())) return null;
        return build.getNow(null);
    }

    /** The game's nations when they are in for the game on the screen, else null. */
    private Nations nationsNow() { return nationsOf == ui.game ? nations : null; }

    /** The nations' map when it is built, whatever world this is: a generated world's arrows stand by its nodes though it draws no nations. */
    private NationsMap builtMap() {
        CompletableFuture<NationsMap> build = nationsBuild;
        return build == null || !build.isDone() || build.isCompletedExceptionally() ? null : build.getNow(null);
    }

    /* ----- the city's trade (WN3) ----- */

    /**
     * The city's trade by partner - THE ONE SEAM the wiring filled (WN3): the game's feed over last month's trade by partner and the year to date
     * (Game.getTradeFeed(), an immutable snapshot published through a volatile, so this is safe on the screen's thread and costs a frame nothing);
     * no trade where the game has no world market, so nothing is drawn, no chip shows on a generated world and no card has trade rows.
     */
    private TradeLanes.Feed tradeFeed() {
        Game g = ui.game;
        return g == null ? TradeLanes.Feed.NONE : g.getTradeFeed();
    }

    /** Whether this is a generated world whose feed names the place the city stands in, so its partners can be drawn as arrows. */
    private boolean standsIn() {
        Game g = ui.game;
        if (g == null || NationsMap.drawsOn(g.getCityLand().key())) return false;
        TradeLanes.Feed f = tradeFeed();
        return Double.isFinite(f.standInLat()) && Double.isFinite(f.standInLon());
    }

    /** The lanes of the feed's figures now: the ones kept while the month, the city, the map and every figure are the same, else made again (a fraction of a millisecond; never in a frame that has them). */
    private TradeLanes lanesNow(NationsMap nm) {
        TradeLanes.Feed f = tradeFeed();
        double[] ex = f.exportsByNation() == null ? NO_FIGURES : f.exportsByNation(), im = f.importsByNation() == null ? NO_FIGURES : f.importsByNation();
        if (lanes == null || lanesOf != nm || !lanes.matches(f.month(), globe.cityLat(), globe.cityLon(), ex, im)) {
            lanes = TradeLanes.of(nm, globe.cityLat(), globe.cityLon(), f.month(), ex, im);
            lanesOf = nm;
        }
        return lanes;
    }

    /** A generated world's arrows of the feed's figures now, kept the same way; null where the map is not built or the feed names no stand-in. */
    private TradeLanes.Arrows arrowsNow() {
        NationsMap nm = builtMap();
        if (nm == null || !standsIn()) return null;
        TradeLanes.Feed f = tradeFeed();
        double[] ex = f.exportsByNation() == null ? NO_FIGURES : f.exportsByNation(), im = f.importsByNation() == null ? NO_FIGURES : f.importsByNation();
        if (arrows == null || !arrows.matches(f.month(), f.standInLat(), f.standInLon(), ex, im)) arrows = TradeLanes.Arrows.of(nm, f.standInLat(), f.standInLon(), f.month(), ex, im);
        return arrows;
    }

    /* ----- the chips ----- */

    /** A toggle chip of the head: a dot before its word, lit or not (styleChip()); a click runs `flip`. */
    private Label layerChip(String words, String tip, Runnable flip) {
        Label chip = new Label(words);
        Region dot = new Region();
        dot.setMinSize(7, 7);
        dot.setPrefSize(7, 7);
        dot.setMaxSize(7, 7);
        chip.setGraphic(dot);
        chip.setGraphicTextGap(5);
        chip.setMinWidth(Region.USE_PREF_SIZE);
        chip.setOnMouseClicked(e -> {
            flip.run();
            e.consume();
        });
        HistoryScreen.tip(chip, tip);
        return chip;
    }

    /** The board's .chip and .chip.lit: lit, the pinned ground, the accent's edge and dot and the full text; not, the panel, the edge, the label's text and the grey dot. */
    private static void styleChip(Label chip, boolean lit) {
        chip.setStyle("-fx-padding: 4 10 4 10; -fx-cursor: hand;" + Palette.block(lit ? Palette.PINNED : Palette.PANEL, lit ? Palette.ACCENT : Palette.EDGE)
                + Palette.words(Palette.SIZE_LABEL, lit ? Palette.TEXT : Palette.TEXT_LABEL));
        chip.getGraphic().setStyle("-fx-background-color: " + (lit ? Palette.ACCENT : Palette.TEXT_SPENT) + "; -fx-background-radius: 4;");
    }

    /**
     * The chips shown only on an Earth world whose nations' map is built, and lit as the settings stand (each frame of the head). On a generated
     * world the group shows the Trade routes chip alone, and only while the feed names a stand-in place (WN3): its partners are the arrows.
     */
    private void syncChips() {
        if (nationsGroup == null) return;
        boolean built = nationsMap() != null, arrowsOnly = !built && standsIn();
        showIf(nationsGroup, built || arrowsOnly);
        showIf(nationsChip, built);
        showIf(bordersChip, built);
        if (!built && !arrowsOnly) return;
        boolean n = ui.prefs.isNationsOnMap(), t = ui.prefs.isTradeRoutesOnMap(), b = ui.prefs.isBordersOnMap();
        String key = n + "|" + t + "|" + b + "|" + built;
        if (key.equals(chipsFor)) return;
        chipsFor = key;
        styleChip(nationsChip, n);
        styleChip(tradeChip, t);
        styleChip(bordersChip, b);
    }

    private void flipNations() {
        ui.prefs.setNationsOnMap(!ui.prefs.isNationsOnMap());
        layersFlipped();
    }

    private void flipBorders() {
        ui.prefs.setBordersOnMap(!ui.prefs.isBordersOnMap());
        layersFlipped();
    }

    private void flipTrade() {
        ui.prefs.setTradeRoutesOnMap(!ui.prefs.isTradeRoutesOnMap());
        layersFlipped();
    }

    /** A chip flipped: saved at once (the settings are the player's, beside the saves), the card put away if its nations are off, the chips restyled and the view drawn again - which lays the legend out again (legendFor() keys on the nations' entries). */
    private void layersFlipped() {
        ui.prefs.save(ui.game.getGameFiles());
        if (!ui.prefs.isNationsOnMap()) hideNationCard();
        syncChips();
        requestDraw();
    }

    /* ----- the legend ----- */

    /**
     * The legend's nations entries - {colour, words} - on an Earth world with the map built: the border's white while Borders is on, the dot's colour
     * while Nations is on and the view is a globe's, and (WN3) the violet and the sand of the trade while Trade routes is on and there is trade to
     * draw - lanes on a globe, arrows on a generated world's map.
     */
    private List<String[]> nationsEntries() {
        NationsMap nm = nationsMap();
        List<String[]> out = new ArrayList<>();
        if (nm != null) {
            if (ui.prefs.isBordersOnMap()) out.add(new String[] { css(NationsMap.BORDER_ARGB), NationsMap.BORDER_WORDS });
            if (ui.prefs.isNationsOnMap() && onGlobe()) out.add(new String[] { css(NationsMap.DOT_ARGB), NationsMap.NATION_WORDS });
        }
        boolean trade;
        if (!ui.prefs.isTradeRoutesOnMap()) trade = false;
        else if (nm != null) trade = onGlobe() && lanesNow(nm).partners() > 0;
        else {
            TradeLanes.Arrows a = expanded ? arrowsNow() : null;
            trade = a != null && a.count() > 0;
        }
        if (trade) {
            out.add(new String[] { Palette.BUSINESS, TRADE_SELL_WORDS });
            out.add(new String[] { Palette.ORE, TRADE_BUY_WORDS });
        }
        return out;
    }

    /* ----- drawing ----- */

    /** Runs of border stroked as ONE path - the runs are many and short, about 6,000 on the planet - 1 px, dashed NationsMap.BORDER_DASH_PX, white at BORDER_ALPHA; the width and dashes put back. */
    private static void strokeRuns(GraphicsContext gc, NationsMap.Runs r) {
        if (r.runs() == 0) return;
        double width = gc.getLineWidth();
        double[] dashes = gc.getLineDashes();
        gc.setStroke(colour(NationsMap.BORDER_ARGB, NationsMap.BORDER_ALPHA));
        gc.setLineWidth(1);
        gc.setLineDashes(NationsMap.BORDER_DASH_PX);
        gc.beginPath();
        for (int k = 0; k < r.runs(); k++) {
            int from = r.starts[k], to = r.starts[k + 1];
            if (to - from < 2) continue;
            gc.moveTo(r.xs[from], r.ys[from]);
            for (int i = from + 1; i < to; i++) gc.lineTo(r.xs[i], r.ys[i]);
        }
        gc.stroke();
        gc.beginPath();
        gc.setLineDashes(dashes);
        gc.setLineWidth(width);
    }

    /**
     * The city's trade lanes on the globe, between the borders and the dots (WN3, board 1): TradeLanes.runs() of the view - the edges in view,
     * each shared lane once - stroked a path to each colour and whole width (at most TradeLanes.BUCKETS strokes however many partners), violet
     * what we sell and sand what we buy, round ended, at TRADE_ALPHA; the line's width, cap and join put back. Nothing when the chip is off or
     * there is no trade; the lanes themselves are made only when the month's figures move (lanesNow()).
     */
    private void tradeLanes(GraphicsContext gc, NationsMap nm) {
        if (!ui.prefs.isTradeRoutesOnMap()) return;
        TradeLanes l = lanesNow(nm);
        if (l.partners() == 0) return;
        TradeLanes.Drawn d = l.runs(globe);
        NationsMap.Runs r = d.runs;
        if (r.runs() == 0) return;
        double width = gc.getLineWidth();
        javafx.scene.shape.StrokeLineCap cap = gc.getLineCap();
        javafx.scene.shape.StrokeLineJoin join = gc.getLineJoin();
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        for (int b = 0; b < TradeLanes.BUCKETS; b++) {
            int firstRun = d.bucketRuns[b], endRun = d.bucketRuns[b + 1];
            if (endRun == firstRun) continue;
            gc.setStroke(TradeLanes.Drawn.sellBucket(b) ? TRADE_SELL : TRADE_BUY);
            gc.setLineWidth(TradeLanes.Drawn.widthOf(b));
            gc.beginPath();
            for (int k = firstRun; k < endRun; k++) {
                int from = r.starts[k], to = r.starts[k + 1];
                if (to - from < 2) continue;
                gc.moveTo(r.xs[from], r.ys[from]);
                for (int i = from + 1; i < to; i++) gc.lineTo(r.xs[i], r.ys[i]);
            }
            gc.stroke();
        }
        gc.beginPath();
        gc.setLineCap(cap);
        gc.setLineJoin(join);
        gc.setLineWidth(width);
    }

    /**
     * A generated world's partners as arrows at the edge of its map (WN3, the study 6.4): the biggest TradeLanes.ARROWS_MAX over the floor, each at
     * the edge on its bearing from the stand-in place the feed names - a violet arrow pointing out for what we sell and a sand one pointing in for
     * what we buy, side by side, widths by money - with the nation's name where the feed gives it. On the expanded map only.
     */
    private void tradeArrows(GraphicsContext gc) {
        if (!expanded || ui.prefs == null || !ui.prefs.isTradeRoutesOnMap()) return;
        TradeLanes.Arrows a = arrowsNow();
        if (a == null || a.count() == 0) return;
        a.place(canvas.getWidth(), canvas.getHeight());
        TradeLanes.Feed feed = tradeFeed();
        double width = gc.getLineWidth();
        javafx.scene.shape.StrokeLineCap cap = gc.getLineCap();
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
        for (int k = 0; k < a.count(); k++) {
            if (a.sellPx(k) > 0) arrow(gc, a.x(k), a.y(k), a.dirX(k), a.dirY(k), a.sellPx(k), -a.buyPx(k) / 2.0, true, TRADE_SELL);
            if (a.buyPx(k) > 0) arrow(gc, a.x(k), a.y(k), a.dirX(k), a.dirY(k), a.buyPx(k), a.sellPx(k) / 2.0, false, TRADE_BUY);
        }
        gc.setLineCap(cap);
        gc.setLineWidth(width);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        gc.setFont(Palette.Fonts.sansStrongFont(LABEL_NAME_PX));
        for (int k = 0; k < a.count(); k++) {
            String name = feed.nameOf(a.nation(k));
            if (name == null || name.isEmpty()) continue;
            double lx = a.x(k) - a.dirX(k) * (ARROW_LEN_PX + 8 + name.length() * 2.5), ly = a.y(k) - a.dirY(k) * (ARROW_LEN_PX + 8);
            gc.setLineWidth(HALO_PX);
            gc.setStroke(colour(NationsMap.DOT_EDGE_ARGB, HALO_ALPHA));
            gc.strokeText(name, lx, ly);
            gc.setFill(Color.web(Palette.TEXT));
            gc.fillText(name, lx, ly);
        }
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.MITER);
        gc.setLineWidth(width);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
    }

    /**
     * One arrow of the edge: a shaft ARROW_LEN_PX long inward from its tip, a head at the outer end if it points out (what we sell) and at the inner
     * end if it points in (what we buy), the whole moved `off` pixels sideways (to the right of the outward way) so the pair lie side by side.
     */
    private static void arrow(GraphicsContext gc, double tipX, double tipY, double ux, double uy, double width, double off, boolean out, Color colour) {
        double nx = -uy, ny = ux, ax = tipX + nx * off, ay = tipY + ny * off;
        double bx = ax - ux * ARROW_LEN_PX, by = ay - uy * ARROW_LEN_PX;
        double head = ARROW_HEAD_PX + width / 2, wide = width / 2 + ARROW_HEAD_WIDE_PX;
        double hx = out ? ax : bx, hy = out ? ay : by, dx = out ? ux : -ux, dy = out ? uy : -uy;
        double baseX = hx - dx * head, baseY = hy - dy * head;
        gc.setStroke(colour);
        gc.setLineWidth(width);
        gc.strokeLine(out ? bx : ax, out ? by : ay, baseX, baseY);
        gc.setFill(colour);
        gc.beginPath();
        gc.moveTo(hx, hy);
        gc.lineTo(baseX + nx * wide, baseY + ny * wide);
        gc.lineTo(baseX - nx * wide, baseY - ny * wide);
        gc.closePath();
        gc.fill();
        gc.beginPath();
    }

    /** The globe's nations (after its picture, under the city's own drawing): the borders with Borders on, the trade's lanes with Trade routes on, the dots and labels with Nations on - each only when the map is built, and the dots only once the game's nations are in. */
    private void globeNations(GraphicsContext gc) {
        NationsMap nm = nationsMap();
        if (nm == null) return;
        if (ui.prefs.isBordersOnMap()) strokeRuns(gc, nm.borders(globe));
        tradeLanes(gc, nm);
        Nations ns = nationsNow();
        if (ui.prefs.isNationsOnMap() && ns != null) drawMarks(gc, nationMarks(nm, ns));
    }

    /**
     * The dots and labels of the globe's view now (NationsMap.marks()), made again only when the view is not the one they were made for
     * (GlobeFrame.sameView()), the year moved, the figures or the map are another's, or a font changed; the same list the hover and the click read.
     */
    private List<NationsMap.Mark> nationMarks(NationsMap nm, Nations ns) {
        int year = Nations.yearOf(ui.game.getMonth());
        javafx.scene.text.Font name = Palette.Fonts.sansStrongFont(LABEL_NAME_PX), figures = Palette.Fonts.monoFont(LABEL_FIGURES_PX);
        if (!name.equals(nameFont) || !figures.equals(figuresFont)) {
            nameFont = name;
            figuresFont = figures;
            nameWidths.clear();
            figuresWidths.clear();
            marksView = null;
        }
        if (marksView == null || !marksView.sameView(globe) || marksYear != year || marksFrom != ns || marksOf != nm) {
            marks = nm.marks(globe, ns, year, nameW, figuresW);
            marksView = globe.copy(globe.width(), globe.height(), globe.kmPx());
            marksYear = year;
            marksFrom = ns;
            marksOf = nm;
        }
        return marks;
    }

    /** The dots (NationsMap.DOT_ARGB at DOT_ALPHA, edged), then the labels of those that fit: the name as the board's .lbl and the figures as its .lbl2, centred, each on its halo. */
    private void drawMarks(GraphicsContext gc, List<NationsMap.Mark> list) {
        if (list.isEmpty()) return;
        double width = gc.getLineWidth();
        Color fill = colour(NationsMap.DOT_ARGB, NationsMap.DOT_ALPHA), edge = colour(NationsMap.DOT_EDGE_ARGB);
        gc.setLineWidth(DOT_EDGE_PX);
        for (NationsMap.Mark m : list) {
            gc.setFill(fill);
            gc.fillOval(m.x() - m.r(), m.y() - m.r(), 2 * m.r(), 2 * m.r());
            gc.setStroke(edge);
            gc.strokeOval(m.x() - m.r(), m.y() - m.r(), 2 * m.r(), 2 * m.r());
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BASELINE);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        gc.setLineWidth(HALO_PX);
        gc.setStroke(colour(NationsMap.DOT_EDGE_ARGB, HALO_ALPHA));
        gc.setFont(nameFont);
        gc.setFill(Color.web(Palette.TEXT));
        for (NationsMap.Mark m : list) {
            if (!m.labelled()) continue;
            gc.strokeText(m.name(), m.lx(), m.ly());
            gc.fillText(m.name(), m.lx(), m.ly());
        }
        gc.setFont(figuresFont);
        gc.setFill(Color.web(Palette.TEXT_LABEL));
        for (NationsMap.Mark m : list) {
            if (!m.labelled()) continue;
            gc.strokeText(m.figures(), m.lx(), m.ly() + NationsMap.LABEL_LINE_PX);
            gc.fillText(m.figures(), m.lx(), m.ly() + NationsMap.LABEL_LINE_PX);
        }
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.MITER);
        gc.setLineWidth(width);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Palette.Fonts.monoFont(Palette.SIZE_BODY));
    }

    /**
     * The borders on the city's plane (the plane's tier on Earth, and the land office's small map), after the tiles and what runs over them
     * and before the world lines and the overlays: only the lines that cross the box of plots of the city's land and its listed offers
     * (NationsMap.crossing(), which keeps its answer against the box, so it is found again only when the land or the offers move), cut and
     * projected by NationsMap.planeBorders(). No dots on the plane: the near view is the city's.
     */
    private void planeBorders(GraphicsContext gc, MapFrame f) {
        if (!ui.prefs.isBordersOnMap()) return;
        NationsMap nm = nationsMap();
        if (nm == null) return;
        CityLand land = ui.game.getCityLand();
        double[] box = crossingBox(land, ui.game.getLandManager().getMarket());
        if (box == null) return;
        World.Key key = land.key();
        if (planeProj == null || !key.equals(planeProjKey)) {
            planeProj = new EarthProj(key.lat(), key.lon());
            planeProjKey = key;
        }
        int[] lines = nm.crossing(planeProj, box[0], box[1], box[2], box[3]);
        if (lines.length > 0) strokeRuns(gc, nm.planeBorders(planeProj, f, lines));
    }

    /** The box of plots {x0, y0, x1, y1} of the city's land and its listed offers, as the reach is found from them (GlobeFrame.reachKm()); null with neither. */
    private static double[] crossingBox(CityLand land, LandMarket market) {
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        boolean any = false;
        LandGrid grid = land.grid();
        if (grid.ownedPlots() > 0) {
            x0 = grid.minX();
            y0 = grid.minY();
            x1 = grid.maxX();
            y1 = grid.maxY();
            any = true;
        }
        for (LandParcel p : market.getListing()) {
            x0 = Math.min(x0, p.getX0());
            y0 = Math.min(y0, p.getY0());
            x1 = Math.max(x1, p.getX1());
            y1 = Math.max(y1, p.getY1());
            any = true;
        }
        return any ? new double[] { x0, y0, x1, y1 } : null;
    }

    /* ----- the pointer ----- */

    /**
     * What hovering a dot says (NationsMap.hoverWords(), its last line the blue CLICK_FOR_CARD), or null where the pointer is over no dot,
     * Nations is off, or the city's plane is drawn under it: there the land office's hover and click come first, so a dot over a city founded
     * at its nation's largest place never hides an offer.
     */
    private List<String> nationHoverWords(double sx, double sy) {
        NationsMap nm = nationsMap();
        Nations ns = nationsNow();
        if (nm == null || ns == null || !ui.prefs.isNationsOnMap() || plotAt(sx, sy) != null) return null;
        NationsMap.Mark dot = nm.dotAt(nationMarks(nm, ns), sx, sy);
        if (dot == null) return null;
        return nm.hoverWords(ns, dot.nation(), Nations.yearOf(ui.game.getMonth()), globe.cityLat(), globe.cityLon());
    }

    /** Whose land is under the pointer, where the city's plane is not drawn: a nation's or an area's name (NationsMap.ownerName() of the exact ownerAt()); null elsewhere or with Nations off. */
    private String ownerWords(double sx, double sy) {
        NationsMap nm = nationsMap();
        if (nm == null || !ui.prefs.isNationsOnMap() || plotAt(sx, sy) != null) return null;
        double[] ll = globe.latLonAt(sx, sy);
        return ll == null ? null : nm.ownerName(nm.ownerAt(ll[0], ll[1]));
    }

    /**
     * A click on the expanded globe with Nations on, where the city's plane is not drawn under the pointer (plotAt() is null): on a dot or
     * on a nation's land (NationsMap.nationAt(): the dot, else the exact owner) opens that nation's card. False anywhere else - over the
     * sea or an area, over the plane's ground, where the land office's click comes first even under a dot - and the click runs as it did,
     * which closes the card.
     */
    private boolean nationClick(double sx, double sy) {
        if (!onGlobe() || !ui.prefs.isNationsOnMap()) return false;
        NationsMap nm = nationsMap();
        Nations ns = nationsNow();
        if (nm == null || ns == null || plotAt(sx, sy) != null) return false;
        int nation = nm.nationAt(globe, nationMarks(nm, ns), sx, sy);
        if (nation == 0) return false;
        showNationCard(nation, sx, sy);
        return true;
    }

    /* ----- the card ----- */

    /** A nation's card opened beside the point clicked: the buying card (and its picks) goes, as there is only ever one. */
    private void showNationCard(int nation, double sx, double sy) {
        closeCard();
        nationShown = nation;
        nationX = sx;
        nationY = sy;
        fillNationCard();
    }

    /** What the card was drawn for: the nation and the month, which moves the year and the D$ it is struck in. */
    private String nationCardKey() { return nationShown + "|" + ui.game.getMonth(); }

    /** The card made (again) for the nation shown, from NationsMap.card(): its head - the name, the region as a tag, a "×" - the distance under it, and its rows in a grid of three columns. */
    private void fillNationCard() {
        NationsMap nm = nationsMap();
        Nations ns = nationsNow();
        if (nationBox == null || nm == null || ns == null || globe == null || nationShown == 0) {
            hideNationCard();
            return;
        }
        Game g = ui.game;
        // The D$ a US$ is the foreign accounts' rate (Markets.getExchangeRate() is that times the world's price level).
        // WN4: the money's rate this month where the city has the world's moneys, else the file's year.
        WorldMoneys moneys = g.getWorldMoneys();
        NationsMap.Card c = nm.card(ns, nationShown, Nations.yearOf(g.getMonth()), globe.cityLat(), globe.cityLon(), g.getForeignAccounts().getRate(),
                tradeFeed().yearToDate(nationShown), moneys == null ? Double.NaN : moneys.rateOfNation(nationShown));
        nationKey = nationCardKey();
        Label name = new Label(c.name());
        name.setStyle(Palette.strong(15, Palette.TEXT));
        name.setMinWidth(Region.USE_PREF_SIZE);
        HBox head = new HBox(Palette.GAP, name);
        head.setAlignment(Pos.CENTER_LEFT);
        if (!c.region().isEmpty()) {
            Label tag = new Label(c.region());
            tag.setStyle("-fx-padding: 1 6 1 6;" + Palette.block(Palette.PINNED, Palette.EDGE) + Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            tag.setMinWidth(0);
            head.getChildren().add(tag);
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label close = new Label("×");
        close.setStyle(Palette.words(Palette.SIZE_SECTION, Palette.TEXT_MUTED) + " -fx-cursor: hand;");
        close.setMinWidth(Region.USE_PREF_SIZE);
        close.setOnMouseClicked(e -> {
            hideNationCard();
            requestDraw();
            e.consume();
        });
        head.getChildren().addAll(spacer, close);
        Label distance = new Label(c.distance());
        distance.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        GridPane grid = new GridPane();
        grid.setHgap(Palette.GAP + 2);
        grid.setVgap(3);
        ColumnConstraints labels = new ColumnConstraints(), values = new ColumnConstraints(), ranks = new ColumnConstraints();
        values.setHgrow(Priority.ALWAYS);
        values.setMinWidth(0);
        ranks.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(labels, values, ranks);
        int row = 0;
        for (NationsMap.Row r : c.rows()) {
            Label k = new Label(r.label());
            k.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            k.setMinWidth(Region.USE_PREF_SIZE);
            // The value wraps rather than widen the card (a tiny rate reads long).
            Label v = new Label(r.value());
            v.setStyle(Palette.figureRegular(Palette.SIZE_LABEL, Palette.TEXT));
            v.setWrapText(true);
            v.setMinWidth(0);
            Label rank = new Label(r.rank());
            rank.setStyle(Palette.figureRegular(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            rank.setMinWidth(Region.USE_PREF_SIZE);
            GridPane.setValignment(k, VPos.TOP);
            GridPane.setValignment(v, VPos.TOP);
            GridPane.setValignment(rank, VPos.TOP);
            grid.add(k, 0, row);
            grid.add(v, 1, row);
            grid.add(rank, 2, row);
            row++;
        }
        nationBox.setStyle("-fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8; -fx-border-color: " + Palette.EDGE
                + "; -fx-border-radius: 8; -fx-padding: 11 13 10 13; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 14, 0.2, 0, 3);");
        nationBox.setMinWidth(NATION_CARD_W);
        nationBox.setPrefWidth(NATION_CARD_W);
        nationBox.setMaxWidth(NATION_CARD_W);
        nationBox.getChildren().setAll(head, distance, grid);
        nationBox.setVisible(true);
        nationBox.applyCss();
        nationBox.autosize();
        placeNationCard();
    }

    /** The card beside the point clicked, as the buying card stands beside its offer: CARD_GAP off it, at its right else its left, level with it and CARD_MARGIN at least inside the view. */
    private void placeNationCard() {
        if (nationBox == null || !nationBox.isVisible()) return;
        double vw = canvas.getWidth(), vh = canvas.getHeight();
        double cw = nationBox.getWidth(), ch = nationBox.getHeight();
        double left = nationX + CARD_GAP;
        if (left + cw > vw - CARD_MARGIN) left = nationX - CARD_GAP - cw;
        double top = nationY - ch / 2;
        left = Math.max(CARD_MARGIN, Math.min(left, vw - cw - CARD_MARGIN));
        top = Math.max(CARD_MARGIN, Math.min(top, vh - ch - CARD_MARGIN));
        nationBox.setTranslateX(Math.rint(left));
        nationBox.setTranslateY(Math.rint(top));
    }

    /** The card put away. */
    private void hideNationCard() {
        nationShown = 0;
        nationKey = null;
        if (nationBox == null) return;
        nationBox.getChildren().clear();
        nationBox.setVisible(false);
    }

    /** A frame's look at the card (tick()): made again when the month moved - the year's figures and the D$ it is struck in - and put away if its nation is not shown any more (Nations off, another game). */
    private void syncNationCard() {
        if (nationBox == null || !nationBox.isVisible()) return;
        if (!ui.prefs.isNationsOnMap() || nationsNow() == null) {
            hideNationCard();
            return;
        }
        if (!nationCardKey().equals(nationKey)) fillNationCard();
    }

    /* =====================================================================
       EXPAND: THE MAP OVER THE WHOLE WINDOW
       ===================================================================== */

    void expand() { expand(false); }

    /** ...or (0.7.137, B5) back at the view it left - the funding page done - when `keepView` and it has been over the window before. */
    private void expand(boolean keepView) {
        if (expanded) return;
        // Over the window afresh, no picks stand and no purchase waits: the ones a funding page that was left by another way kept
        // for its purchase (collapse()) are let go (BL1).
        if (!keepView) {
            backFromFunding = false;
            picks.clear();
        }
        if (bigPane == null) buildPane();
        expanded = true;
        smallBox.getChildren().remove(canvas);
        bigBox.getChildren().add(0, canvas);
        showIf(readout, false);
        headFor = null;
        syncHead();
        // Under the window's header, which stays in view and says the clock (MS1): the head here has no clock line.
        ui.showChartFullScreen(bigPane, null, this::collapse, true);
        ui.panKeysTo(this);
        canvas.setCursor(javafx.scene.Cursor.OPEN_HAND);
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
        // The funding page buys these very ids (toFunding(), buyOnLoan() falling short), and the drafts of the picked blocks stand
        // in the office only while the picks do (BL1, Picks.clear()): through it the picks are kept, and back() or the receipt
        // lets them go.
        if (!backFromFunding) picks.clear();
        if (!backFromFunding) bought = null;
        hideBuyCard();
        hideNationCard();
        ui.closeChartFullScreen();
        if (card != null) card.setVisible(false);
        canvas.setCursor(javafx.scene.Cursor.DEFAULT);
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
        title.setMinWidth(Region.USE_PREF_SIZE);
        title.setStyle(Palette.Fonts.sansSemiBold() + " -fx-font-size: 15px; -fx-text-fill: " + Palette.TEXT + ";");
        // The window's header stays in view above this pane (MS1) with the clock and the city's money, so the head keeps only
        // land's own: the cash in the money the toggle pays in and the pay toggle (0.7.137, spec-earth 6.1), the hint, the
        // chips - and the note of what a month or a purchase took off the picks, in the warning colour.
        headMoney = new Label();
        headMoney.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_HEAD));
        headMoney.setMinWidth(Region.USE_PREF_SIZE);
        headPay = new HBox(6);
        headPay.setAlignment(Pos.CENTER_LEFT);
        headPay.setMinWidth(Region.USE_PREF_SIZE);
        // Shrinks before anything else in the head does (it ends in an ellipsis; its tooltip says the whole line).
        headNote = new Label();
        headNote.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.WARN));
        headNote.setMinWidth(0);
        headNoteTip = new javafx.scene.control.Tooltip();
        headNoteTip.setShowDelay(javafx.util.Duration.millis(200));
        headNoteTip.setWrapText(true);
        headNoteTip.setMaxWidth(480);
        javafx.scene.control.Tooltip.install(headNote, headNoteTip);
        showIf(headNote, false);
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
        nationsChip = layerChip("Nations", NATIONS_TIP, this::flipNations);
        tradeChip = layerChip("Trade routes", TRADE_TIP, this::flipTrade);
        bordersChip = layerChip("Borders", BORDERS_TIP, this::flipBorders);
        nationsGroup = new HBox(Palette.GAP_TIGHT, nationsChip, tradeChip, bordersChip);
        nationsGroup.setAlignment(Pos.CENTER_LEFT);
        nationsGroup.setMinWidth(Region.USE_PREF_SIZE);
        // The board's .grp: a hairline either side, 4 px between the chips (top, right, bottom, left).
        nationsGroup.setStyle("-fx-padding: 0 " + Palette.GAP + " 0 " + Palette.GAP + "; -fx-border-color: transparent " + Palette.EDGE
                + " transparent " + Palette.EDGE + "; -fx-border-width: 0 1 0 1;");
        showIf(nationsGroup, false);
        chipsFor = null;
        HBox head = new HBox(Palette.GAP_LOOSE, title, headMoney, headPay, headNote, nationsGroup, gap, hint, every, fit, close);
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
        // A nation's card (WN2): the same, laid out by hand beside the point clicked, under the hover card.
        nationBox = new VBox(NATION_CARD_GAP);
        nationBox.setManaged(false);
        nationBox.setVisible(false);
        bigBox = new StackPane(legend, buyBox, nationBox, card);
        StackPane.setAlignment(card, Pos.TOP_LEFT);
        StackPane.setAlignment(buyBox, Pos.TOP_LEFT);
        StackPane.setAlignment(nationBox, Pos.TOP_LEFT);
        bigBox.setStyle("-fx-background-color: " + Palette.STAGE + ";");
        clip(bigBox);
        bigPane = new VBox(Palette.GAP, head, bigBox);
        bigPane.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-padding: " + PANE_TOP + " " + PANE_SIDE + " "
                + PANE_BOTTOM + " " + PANE_SIDE + ";");
        bigPane.widthProperty().addListener((o, was, now) -> fitPane());
        bigPane.heightProperty().addListener((o, was, now) -> fitPane());
    }


    /** The canvas sized to the pane: the window's width, and its height under the header (MS1: the pane lies below it, ui.chartFullTop()), inside the padding and under the head (City History's rule, read off the window). */
    private void fitPane() {
        if (!expanded) return;
        javafx.scene.Scene window = bigPane.getScene();
        double w = (window != null ? window.getWidth() : bigPane.getWidth()) - 2 * PANE_SIDE;
        double h = (window != null ? window.getHeight() - ui.chartFullTop() : bigPane.getHeight()) - PANE_TOP - PANE_BOTTOM - PANE_HEAD;
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
    static List<String[]> legendEntries(int tier) { return legendEntries(tier, false); }

    /** ...on an Earth world with earth-2's layers (`relief`, LandMap.reliefDrawn(); ET1 slice D) also the three greys, forest on a slope and, on the plane, the shallow sea. */
    static List<String[]> legendEntries(int tier, boolean relief) {
        List<String[]> out = new ArrayList<>();
        for (LandMap.Entry e : LandMap.legend(tier, relief)) out.add(new String[] { css(e.colour()), e.words() });
        return out;
    }

    /** ...the plane's, at every scale on a generated world. */
    static List<String[]> legendEntries() { return legendEntries(GlobeFrame.PLANE); }

    /** Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 the transport class, the railway's four and the gateway, 46 in three columns of 16 before; since 0.8.0 the four industries' works and the seven looks of the materials, 35 in three columns of 12 before; since 0.7.127 ice and the desert, 33 before; since 0.7.97, nine for the shore and the sea; 24 in two before; 11 until 0.7.72 added the railway to its 22; 0.7.88 the tracks). */
    static final int LEGEND_ROWS = 17;

    /** The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). */
    static final String SHIP_SIZE_WORDS = "Ships are drawn 1.6\u00d7 their size until 6 px a plot";

    /** The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). */
    static final String TRUCK_WORDS = "Trucks: one per %s";

    /** ...and its words for N loads a truck: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. */
    static String trucksWords(int loads) {
        return String.format(TRUCK_WORDS, loads <= 1 ? "load" : String.format("%,d loads", loads));
    }

    /** The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. */
    static final String TRACK_ENTRY = LandMap.TRACK_ENTRY;

    /** The legend's note under its entries, when the city has buildings its plans hold none of (R7; spec 5): how many are packed at its edge. */
    static String packedWords(int packed) {
        return String.format("%,d building%s packed at the city's edge: the city is short of ground", packed, packed == 1 ? "" : "s");
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
        // The legend folds (WN2; Jerus: "legend should be collapsable"): its head line takes the click, and the body - the entries, the
        // ships' and trucks' lines and the note - stays mouse-transparent as the whole box was, so a drag or a click on the map under
        // them goes through. The box itself has no background and picks only through its head (setPickOnBounds(false)); the dark ground
        // is the head's and the body's, so the folded head is a small rounded box of its own.
        legendBody = new VBox(6, legendGrid, shipsLine, trucksLine, legendNote);
        legendBody.setMouseTransparent(true);
        legendChevron.setMinWidth(Region.USE_PREF_SIZE);
        legendChevron.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label legendWord = new Label("Legend");
        legendWord.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        legendFoldHint.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        legendHead = new HBox(6, legendChevron, legendWord, legendFoldHint);
        legendHead.setAlignment(Pos.CENTER_LEFT);
        legendHead.setCursor(javafx.scene.Cursor.HAND);
        legendHead.setOnMouseClicked(e -> {
            ui.prefs.setMapLegendFolded(!ui.prefs.isMapLegendFolded());
            ui.prefs.save(ui.game.getGameFiles());
            foldLegend();
            e.consume();
        });
        VBox box = new VBox(legendHead, legendBody);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.setPickOnBounds(false);
        foldLegend();
        return box;
    }

    /** The legend's head line, its chevron and its "click to fold" (WN2), and its body. */
    private HBox legendHead;
    private VBox legendBody;
    private final Label legendChevron = new Label(), legendFoldHint = new Label(LEGEND_FOLD_WORDS);

    /** The head line's words when the legend is open, after its chevron and "Legend" (the board's .lh). */
    static final String LEGEND_FOLD_WORDS = "· click to fold";

    /** The legend folded or open, as the player left it (GamePrefs.isMapLegendFolded()): the body shown or not, the chevron pointing down or right, and the head's ground rounded all round when it stands alone. */
    private void foldLegend() {
        boolean folded = ui.prefs.isMapLegendFolded();
        legendChevron.setText(folded ? "▸" : "▾");
        showIf(legendFoldHint, !folded);
        showIf(legendBody, !folded);
        legendHead.setStyle("-fx-background-color: #0b1118d9; -fx-background-radius: " + (folded ? "6" : "6 6 0 0") + "; -fx-padding: 7 10 "
                + (folded ? "8" : "0") + " 10;");
        legendBody.setStyle("-fx-background-color: #0b1118d9; -fx-background-radius: 0 0 6 6; -fx-padding: 7 10 8 10;");
    }

    /** The legend laid out for a tier (0.7.135, spec-globe 3.7): its entries (legendEntries()), and the ships' line on the plane alone. */
    private void legendFor(int tier) {
        // ...and (0.8.0) on the plane the zones set, drawn again when they move.
        List<String[]> zones = tier == GlobeFrame.PLANE ? zoneEntries() : List.of();
        // ...and (ET5) the amber hatch's entry while the city owns ground no port links, in the same list so the legend is drawn again when it comes or goes.
        if (tier == GlobeFrame.PLANE) {
            List<String[]> islands = islandEntries();
            if (!islands.isEmpty()) {
                zones = new ArrayList<>(zones);
                zones.addAll(islands);
            }
        }
        // ...and (WN2) on an Earth world the nations' entries: the border's white while Borders is on, the dot's colour while Nations is on and a globe is drawn.
        List<String[]> nationsKey = nationsEntries();
        if (!nationsKey.isEmpty()) {
            zones = new ArrayList<>(zones);
            zones.addAll(nationsKey);
        }
        String zoneKey = String.join("|", zones.stream().map(z -> z[1]).toList());
        boolean relief = shownMap != null && LandMap.reliefDrawn(shownMap.worldKey());
        if (tier == legendTier && zoneKey.equals(legendZones) && relief == legendRelief) return;
        legendTier = tier;
        legendZones = zoneKey;
        legendRelief = relief;
        legendGrid.getChildren().clear();
        List<String[]> entries = new ArrayList<>(legendEntries(tier, relief));
        entries.addAll(zones);
        for (int i = 0; i < entries.size(); i++) {
            legendGrid.add(keySwatch(entries.get(i)[0], entries.get(i)[1]), i / LEGEND_ROWS, i % LEGEND_ROWS);
        }
        showIf(shipsLine, tier == GlobeFrame.PLANE);
    }

    /** The tier the legend and the hover are for (MS2): the globe's on Earth - the plane at every scale closer than the handoff, past the reach too, where the plane's own ground is what is drawn - and the plane at every scale on a generated world. */
    private int shownTier() { return globe != null ? globe.legendTier() : GlobeFrame.PLANE; }

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
