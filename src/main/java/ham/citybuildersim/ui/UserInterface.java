package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Palette.Fonts;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The window: the stage and its theme, the header - the clock and the speed,
 * the money block, five headline tiles, the "Needs you" chip, the rating and
 * the inbox - the rail down the left, the City overview's drawer and the
 * construction panel with its tab (0.7.24), the main menu over its backdrop,
 * the save and settings dialogs, the time-skip dialog, and the scroller every
 * screen draws into. Everything a tab SHOWS is a class of
 * its own in this package since 2026-09-18 - one screen per class, listed
 * below in rail order - and this is what is left once they are out: what the
 * screens share, and the shell that holds them.
 *
 * WHY. This file was 25,000 lines, every screen in one class, and a session
 * working on the bank had to carry the policy tab's text in the same window.
 * The split is mechanical and the screens' text is verbatim; see the
 * project's splitting-the-interface.md for how it was done and what moved
 * where. A screen reaches the window through the ui it is constructed with
 * (ui.game, ui.rootMenu, ui.clearMenu()), and the window reaches a screen
 * through its field (policyScreen.showPolicyMenu()). Nothing in the model
 * imports this package.
 *
 * @author Jerus
 */
public class UserInterface extends Application {

    Game game;   // package-private since the split: the screens read it as ui.game

    /* ---------------------------------------------------------------------
       THE SCREENS, one class each since 2026-09-18, in the order the rail
       lists them. Each is given this window and reaches its game, its root
       and clearMenu() through it; what every screen shares stays here. The
       banner sections each class carries are named in its own header, and the
       block below says where each one sat in this file.
       --------------------------------------------------------------------- */
    final BuildScreen      buildScreen      = new BuildScreen(this);
    final LandScreen       landScreen       = new LandScreen(this);
    final PeopleScreen     peopleScreen     = new PeopleScreen(this);
    final ServicesScreen   servicesScreen   = new ServicesScreen(this);
    final InfrastructureScreen infrastructureScreen = new InfrastructureScreen(this);  // the Infrastructure tab (0.7.28; ServicesScreen's until then)
    final SectorScreen     sectorScreen     = new SectorScreen(this);
    final GovernmentScreen governmentScreen = new GovernmentScreen(this);
    final FinancesScreen   financesScreen   = new FinancesScreen(this);
    final BankScreen       bankScreen       = new BankScreen(this);
    final TradeScreen      tradeScreen      = new TradeScreen(this);
    final PolicyScreen     policyScreen     = new PolicyScreen(this);
    final HistoryScreen    historyScreen    = new HistoryScreen(this);
    final SummaryScreen    summaryScreen    = new SummaryScreen(this);   // the left panel's content, not a tab
    final FoundingScreen   foundingScreen   = new FoundingScreen(this);  // New city's screen (0.7.10; Start New Game's until 0.7.21)
    final ConstructionScreen constructionScreen = new ConstructionScreen(this);  // the construction page (0.7.22), the Build tab's

    /* ---------------------------------------------------------------------
       WHERE THE SCREENS WENT. Each line is a run of this file's banner
       sections, in the order they sat here, and the class that holds them now,
       verbatim (the count is of top-level banners):

         BUILD: THE CATEGORY SCREEN IS GONE TOO. / THE HALF OF THE CATALOGUE      BuildScreen
         THE LAND OFFICE. / THE STATEMENT.                                       LandScreen
         THE GOODS ROW                                                           HistoryScreen
         THE POLICY TAB ... PROMISES - the standing subsidies (18)               PolicyScreen
         TRADE & THE WORLD ... THE THREE QUIET GAUGES (7)                        TradeScreen
         THE BANK ... ITS HISTORY (10)                                           BankScreen
         FINANCES ... BORROW (11), and THE DEBT RESULT                           FinancesScreen
         THE BUILD MENU ... THE STAT CARD (3)                                    BuildScreen
         THE SECTOR ECONOMY. ... A STATEMENT LINE THAT OPENS (3)                 SectorScreen
         SERVICES - WHAT THE CITY PROVIDES ... THE BOOKS. (8)                    ServicesScreen
         PEOPLE. ... THE TIER TABLE, AS A TABLE. (6)                             PeopleScreen
         HEADROOM, NOT SATISFACTION                                              SummaryScreen
         THE GOVERNMENT. ... WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON (4) GovernmentScreen
         THE HISTORY SCREEN / THE RECORD                                         HistoryScreen
         CITY OVERVIEW PANEL / THE LEFT PANEL / SUMMARY, OR DASHBOARD            SummaryScreen
         THE SUMMARY IS A PROBLEM LIST NOW. / SEATS AGAINST WHO WOULD COME.      SummaryScreen

       BankScreen was rebuilt in 0.7.9 and redrawn in 0.7.33, and holds none
       of those ten now: its sections run THE BANK (0.7.33): THE FRAME ...
       HISTORY (10).
       LandScreen was redrawn in 0.7.26 and holds neither of its two now:
       its sections run THE LAND OFFICE (0.7.26) ... WHEN THE CITY IS
       SHORT (4).
       PeopleScreen was redrawn in 0.7.27 and holds two of its six as they
       were, THE MONEY BLOCKS BOTH PANELS SHARE and THE TIER TABLE, AS A
       TABLE (a third is HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD
       TO LIVE IN IT? now): its sections run PEOPLE (0.7.27) ... THE TIER
       TABLE, AS A TABLE. (8).
       ServicesScreen was redrawn in 0.7.28 and holds seven of its eight
       under their old titles, each grown or rewritten; the eighth,
       INFRASTRUCTURE, moved whole to InfrastructureScreen. Its sections
       run SERVICES - WHAT THE CITY PROVIDES ... THE BOOKS. (9).
       InfrastructureScreen was redrawn in 0.7.29 and holds INFRASTRUCTURE
       under its old title, rewritten: its sections run INFRASTRUCTURE ...
       FREIGHT (5).
       SectorScreen was redrawn in 0.7.30 and holds its three under their
       old titles, the first two grown or rewritten and A STATEMENT LINE THAT
       OPENS as it was, followed now by a banner for each of the five pages:
       its sections run THE SECTOR ECONOMY. ... OPERATIONS (8).
       GovernmentScreen was redrawn in 0.7.31 and holds three of its four
       under their old titles, rewritten - THE GOVERNMENT., THE TWO LISTS
       (0.7.31: ranked bars). and WHAT THE DEBT COSTS, BY THE PAPER IT IS
       OWED ON, which changed only where principal repaid is shown; THE
       SCREEN went, and a banner came for the Overview, for Revenue and
       Spending and for the Output: its sections run THE GOVERNMENT. ...
       THE OUTPUT (0.7.31) (6).
       FinancesScreen was redrawn in 0.7.32 and holds seven of its eleven
       under their old titles, rewritten - FINANCES, THE POSITION, DEBT
       SERVICE, HOME AND ABROAD, YOUR RATE, TAKEN APART, THE BOOK and
       BORROW; THE LANDING became THE HUB (0.7.32), and ONE SUBJECT, ITS
       OWN STRIP, THE LADDER and BUY BACK went into FINANCES, the hub and
       THE BOOK: its sections run FINANCES ... THE DEBT RESULT (12).
       TradeScreen was redrawn in 0.7.35 and holds four of its seven under
       their old titles, rewritten - TRADE & THE WORLD (the frame), WHAT WE
       TRADE, THE CURRENCY and THE RESERVES; THE MONTH, AS A RIVER became THE
       MONTH, ONE SUBJECT, ITS OWN STRIP went into the frame, and THE THREE
       QUIET GAUGES into THE OVERVIEW (their band meter to Pieces): its
       sections run TRADE & THE WORLD (0.7.35): THE FRAME ... THE RESERVES (6).
       PolicyScreen was redrawn in 0.7.36 and holds ten under their old
       titles - THE STAGED SET., THE LADDER, WAGES - the floor, the two
       MONEY and the five PROMISES; THE POLICY TAB became POLICY (0.7.36):
       THE FRAME, THE LANDING became THE HUB and THE FOUR TAXES became TAXES,
       and the rest went in: WHAT THE WHOLE BATCH WOULD DO into PolicyPreview
       and the tray, ONE SUBJECT, ITS OWN STRIP into the frame, THE PIECES A
       LEVER IS MADE OF into THE STAGED SET and Levers' dial card, and the
       three TAXES pages into TAXES: its sections run POLICY (0.7.36): THE
       FRAME ... PROMISES - the standing subsidies (13).
       HistoryScreen was redrawn in 0.7.37 and holds THE HISTORY SCREEN and
       THE RECORD under their old titles; THE GOODS ROW became PRICES THIS
       MONTH: its sections run THE HISTORY SCREEN ... PRICES THIS MONTH
       (0.7.37) (12).

       What stayed is in the sections below. The figures, the statement rows,
       the shared pieces and the lever pieces went to Money, Statement, Pieces
       and Levers the same day, in two passes, as public statics behind an
       import static - no call site changed for them.
       --------------------------------------------------------------------- */

    /**
     * JavaFX needs the no-arg one; a harness needs one bound to a city.
     *
     * The info card quotes live figures - today's wages, today's materials
     * price, today's ore price - which is the whole point of deriving it rather
     * than writing it down, and it means the card cannot be checked without a
     * game behind it. Rather than a setter that exists only for the test, the
     * dependency is stated: this window shows THAT city.
     */
    public UserInterface() { }

    public UserInterface(Game game) { this.game = game; }

    private Stage stage;
    VBox rootMenu;

    /** The navigation rail down the window's left edge (0.7.21; it was the panel's right edge); see start(). */
    private VBox tabRail;
    /** The middle of the window, with the City overview's drawer and the construction panel (both since 0.7.24), the inbox's list and the toasts on top of it. */
    private StackPane stagePane;
    /** The inbox's list, dropped down at the stage's top right while it is open (its envelope is the header's since 0.7.21). */
    private VBox inboxCorner;
    /** The toasts, stacked at the stage's bottom right (0.7.20); see TOASTS. */
    private VBox toastStack;
    /** The whole window, with a dialog laid over it when one is open (0.7.20). */
    private StackPane windowStack;
    /** The dialog that is up - Quit's, or since 0.7.22 a confirmation (confirm()) - or null. */
    private StackPane quitDialog;
    /**
     * Whether a city has been founded or loaded this session (0.7.20;
     * anotherCity()). Not Game.isRunning(): the world the game builds at
     * start-up already says it is running, so on a cold start Resume opened
     * that empty world instead of loading the autosave - and the empty world,
     * once played, autosaved over the city the player meant to resume.
     */
    private boolean cityOpen;
    /** The middle column's scroller. A field, so clearMenu can put it back. */
    javafx.scene.control.ScrollPane menuScroller;
    /** The construction panel: over the stage's right edge while open, folded to constructionTab otherwise (0.7.24). */
    private VBox constructionPanel;
    /** The slim tab on the stage's right edge the construction panel folds to (0.7.24). */
    private VBox constructionTab;
    /** The City overview's content, drawn by SummaryScreen. */
    VBox cityPanel;
    /** The drawer the City overview opens in, over the stage's left (0.7.24); see THE FRAME FOLDS AWAY. */
    private VBox drawer;
    /** Whether the drawer is open. Not saved: the pin is (GamePrefs.isDrawerPinned()). */
    private boolean drawerOpen;

    /* =====================================================================
       THE RECEIPT INDICATOR

       Jerus: "the screen is so occupied you dont get to see the last
       transaction receipt". The receipt used to be appended to the BOTTOM of
       the build menu, which is fine for a menu three buttons long and useless
       for healthcare's fourteen buttons under five headings - the confirmation
       that a purchase went through landed off the bottom of the screen, so the
       one message that answers "did that work?" was the one you could not see.

       It is now a dot in the top-right corner of the menu, which flashes while
       there is a purchase you have not looked at and opens the receipt when
       clicked. Two fields, because "have you looked at it" is a question about
       this window and not about the game: the serial the player last opened,
       and whether the card is currently showing. Since 0.7.20 the card is a
       popover over the page with the last five purchases (BuildScreen,
       receiptCorner()), so opening it moves nothing.
       ===================================================================== */
    int receiptSeen = 0;
    boolean receiptOpen = false;

    /**
     * The pulse, held so it can be stopped.
     *
     * A FadeTransition goes on running after its node leaves the scene graph,
     * and this menu rebuilds itself on every click - so starting one per build
     * and forgetting it would leave a new animation ticking on a detached node
     * every time the player pressed anything. clearMenu() stops whichever one is
     * running, which is the single point every screen change already goes
     * through.
     */
    FadeTransition receiptPulse;

    /* =====================================================================
       THE THEME

       Every colour in this file was picked to be read against light grey,
       because until now the middle of the window was light grey. Jerus:
       "make the main screen blackish blue... make the Eye get Orgasms".

       TWO HALVES, AND THE SECOND IS THE ONE THAT MATTERS.

       The first is mechanical - every -fx-text-fill in the file was remapped
       from its light-background value to a dark-background one. A hundred and
       eighty edits and no judgement in any of them.

       The second is this stylesheet, and without it the remap would have been
       useless: most of the buttons and labels in the game set no colour at all
       and were relying on JavaFX's defaults, which are black text on light
       grey. There are a hundred screens of them. Styling each by hand is not a
       project, it is a decade - so the defaults themselves are replaced, once,
       here, and every inline style in the file still wins over it wherever a
       colour was deliberately chosen.

       Loaded as a data URI rather than a .css on the classpath because a
       resource is one more thing that can fail to reach the jar, and a theme
       that silently does not load leaves the game unreadable rather than merely
       unstyled.

       THE MOCKUPS' PALETTE AND IBM PLEX (0.7.21). Every colour below is a
       Palette constant now - the sheet carried its own copies of the old
       greys - and the root names the words' face (Fonts.sans(), read after
       Fonts.load() in start()), so every label that names no family of its
       own is in Plex Sans. The axes' tick labels are figures, so Plex Mono.
       ===================================================================== */

    /** The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). */
    private static final String STAGE = Palette.STAGE;

    private void applyTheme(Scene target) {

        String css =
            ".root {"
          + "  -fx-font-family: " + Fonts.sans() + ";"
          + "  -fx-base: " + Palette.CONTROL + ";"
          + "  -fx-background: " + Palette.STAGE + ";"
          + "  -fx-control-inner-background: " + Palette.FIELD + ";"
          + "  -fx-text-background-color: " + Palette.TEXT_HEAD + ";"
          + "  -fx-accent: " + Palette.ACCENT_FILL + ";"
          + "  -fx-focus-color: " + Palette.ACCENT + ";"
          + "  -fx-faint-focus-color: rgba(90,169,255,0.15);"
          + "}"
          + ".label { -fx-text-fill: " + Palette.TEXT_BODY + "; }"

          + ".button {"
          + "  -fx-background-color: " + Palette.CONTROL + ";"
          + "  -fx-text-fill: " + Palette.TEXT_HEAD + ";"
          + "  -fx-background-radius: 4;"
          + "  -fx-border-color: " + Palette.EDGE + ";"
          + "  -fx-border-radius: 4;"
          + "  -fx-padding: 6 14 6 14;"
          + "  -fx-cursor: hand;"
          + "}"
          + ".button:hover    { -fx-background-color: " + Palette.HOVER + "; -fx-border-color: " + Palette.ACCENT + "; }"
          + ".button:pressed  { -fx-background-color: " + Palette.RAISED + "; }"
          + ".button:disabled { -fx-opacity: 0.4; }"

          + ".text-field {"
          + "  -fx-background-color: " + Palette.FIELD + ";"
          + "  -fx-text-fill: " + Palette.TEXT_HEAD + ";"
          + "  -fx-prompt-text-fill: " + Palette.TEXT_SPENT + ";"
          + "  -fx-border-color: " + Palette.EDGE + ";"
          + "  -fx-border-radius: 3;"
          + "  -fx-background-radius: 3;"
          + "}"
          + ".text-field:focused { -fx-border-color: " + Palette.ACCENT + "; }"

          // A link that reads as one - the founding screen's two (0.7.21).
          + ".hyperlink, .hyperlink:visited { -fx-text-fill: " + Palette.ACCENT + ";"
          + "  -fx-border-color: transparent; -fx-padding: 0; -fx-underline: false; }"
          + ".hyperlink:hover { -fx-underline: true; }"

          + ".scroll-pane { -fx-background-color: transparent; -fx-background: transparent; }"
          + ".scroll-pane > .viewport { -fx-background-color: transparent; }"
          + ".scroll-bar { -fx-background-color: transparent; }"
          + ".scroll-bar > .thumb { -fx-background-color: " + Palette.CONTROL_EDGE + "; -fx-background-radius: 6; }"
          + ".scroll-bar > .thumb:hover { -fx-background-color: " + Palette.TEXT_SPENT + "; }"
          + ".scroll-bar > .increment-button,"
          + ".scroll-bar > .decrement-button { -fx-opacity: 0; -fx-padding: 0; }"

          // The construction panel's sites, in the building colour (0.7.21).
          + ".progress-bar > .track { -fx-background-color: " + Palette.FIELD + "; }"
          + ".progress-bar > .bar   { -fx-background-color: " + Palette.BUILDING + "; }"

          // The Policy tab's levers. A default Slider is a pale track and a
          // pale thumb, which on this ground reads as a disabled control.
          + ".slider > .track {"
          + "  -fx-background-color: " + Palette.FIELD + ";"
          + "  -fx-border-color: " + Palette.EDGE + ";"
          + "  -fx-border-radius: 4;"
          + "  -fx-background-radius: 4;"
          + "  -fx-pref-height: 8;"
          + "}"
          + ".slider > .thumb {"
          + "  -fx-background-color: " + Palette.ACCENT + ";"
          + "  -fx-background-radius: 9;"
          + "  -fx-padding: 8;"
          + "  -fx-effect: null;"
          + "}"
          + ".slider > .thumb:hover   { -fx-background-color: " + Palette.ACCENT_LIGHT + "; }"
          + ".slider > .thumb:pressed { -fx-background-color: " + Palette.TEXT_MAX + "; }"

          /*
           * ONE TOOLTIP, EVERYWHERE (0.7.20). This line made every tooltip
           * transparent, so the header's price tooltip was drawn straight over
           * the cards under it and the receipt's ran into the right panel. A
           * dark panel, an edge, padding and wrapped text; the width every
           * tooltip wraps at is TIP_WIDTH (dressTooltips()). On the raised
           * ground since 0.7.21, the popovers' (Pieces.infoButton()).
           */
          + ".tooltip {"
          + "  -fx-background-color: " + Palette.PINNED + ";"
          + "  -fx-background-radius: " + Palette.RADIUS + ";"
          + "  -fx-border-color: " + Palette.CONTROL_EDGE + ";"
          + "  -fx-border-radius: " + Palette.RADIUS + ";"
          + "  -fx-border-width: 1;"
          + "  -fx-padding: 7 10 7 10;"
          + "  -fx-text-fill: " + Palette.TEXT_HEAD + ";"
          + "  -fx-font-family: " + Fonts.sans() + ";"
          + "  -fx-font-size: 12px;"
          + "  -fx-wrap-text: true;"
          + "  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 10, 0, 0, 2);"
          + "}"

          + ".chart { -fx-background-color: transparent; -fx-padding: 4; }"
          + ".chart-plot-background { -fx-background-color: " + Palette.PANEL + "; }"
          + ".chart-title { -fx-text-fill: " + Palette.TEXT_HEAD + "; }"
          + ".chart-legend { -fx-background-color: transparent; }"
          + ".chart-vertical-grid-lines, .chart-horizontal-grid-lines { -fx-stroke: " + Palette.EDGE + "; }"
          + ".chart-alternative-row-fill { -fx-fill: transparent; -fx-stroke: transparent; }"
          + ".chart-pie-label { -fx-fill: " + Palette.TEXT_LABEL + "; }"
          + ".chart-pie-label-line { -fx-stroke: " + Palette.CONTROL_EDGE + "; }"
          + ".axis {"
          + "  -fx-tick-label-fill: " + Palette.TEXT_MUTED + ";"
          + "  -fx-tick-label-font: 10px " + Palette.mono() + ";"
          + "  -fx-tick-mark-stroke: " + Palette.CONTROL_EDGE + ";"
          + "  -fx-minor-tick-mark-stroke: " + Palette.EDGE + ";"
          + "}"
          + ".axis-label { -fx-text-fill: " + Palette.TEXT_MUTED + "; }";

        target.getStylesheets().add("data:text/css;base64,"
                + java.util.Base64.getEncoder().encodeToString(
                        css.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    /** The always-visible strip on the BorderPane's top edge: the header (0.7.21; the date bar until then). The debt bar on the bottom edge went to the Finances hub in 0.7.24. */
    private HBox header;
    private Scene scene;

    /* =====================================================================
       THE CLOCK

       Jerus, 2026-09-14: "instead of being next month, make it so that its just
       play/pause, aka every 5 seconds is a month, and you can increase it to
       10x speed or 0.1x speed or pause, and have it show days so that you know
       whats happening even tho everything still only updates monthly".

       THE FIRST CONTINUOUS THING IN THE GAME. Every other control here is a
       click that makes something happen and stops; this one runs on its own,
       which means for the first time the simulation can move while the player
       is reading a screen. That is the whole point and it is also the whole
       risk, so two rules fall out of it:

       THE MONTH IS STILL THE ONLY UNIT. The clock does not advance anything by
       a day, because there is nothing in this game that happens in a day. It
       accumulates a FRACTION of a month and calls the same nextMonth() the
       button called when the fraction reaches one. Days are drawn from that
       fraction and are read by nothing - see CityCalendar.dayOf().

       AND IT IS CHEAP ENOUGH TO DO ON THE FX THREAD. Measured before building
       it: a month costs a median of 0.5-0.8ms in a city of 100-200,000 and
       1.5ms in a young one, against a 16ms frame. Ten times speed is a month
       every half second, so the simulation is under one percent of the budget
       and a background thread would be complexity bought for nothing.
       ===================================================================== */

    /** Real seconds a month takes at 1x. */
    public static final double SECONDS_PER_MONTH = 5.0;

    /**
     * The ladder the speed steps along: a rung a click on the clock's two
     * arrows since 0.7.21, and the stops the speed slider stuck to before.
     *
     * A slider that snaps rather than a row of buttons or a free drag - Jerus's
     * call: "a slider that sticks if you get what i mean, aka the discrete one,
     * but its a sticky ladder". Dragging is the natural gesture for a speed and
     * landing between two stops is not a speed anybody meant to pick. The
     * header's arrows (0.7.21) keep the stops and have no drag.
     */
    private static final double[] SPEEDS = {0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50};
    private static final int NORMAL_SPEED = 3;              // 1x

    private int speedIndex = NORMAL_SPEED;
    private boolean clockRunning;
    private double monthProgress;
    private long lastFrame;

    /*
     * HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS.
     *
     * The clock redraws the open screen once per frame in which a month landed,
     * which was free at 10x: a month arrives every 500ms there, so the limit
     * never bound. At 50x one lands every 100ms, and a heavy page - the
     * property tax screen is a ten-row grid and ten dials - rebuilt ten times a
     * second is a page that stutters and fights the pointer.
     *
     * So the SIMULATION runs free and the REDRAW is capped. 120ms is under the
     * gap at 20x, so nothing below the top rung behaves any differently, and at
     * 50x the screen lags the city by at most a tenth of a second - which is
     * one frame's worth of figures on a screen advancing ten months a second.
     */
    private static final double REDRAW_EVERY = .12;
    private double sinceRedraw;
    private boolean redrawPending;
    private javafx.animation.AnimationTimer clock;

    /*
     * A PRESS IS NEVER REBUILT AWAY (0.7.40). Jerus: "when your going at 20x
     * speed you have to click buttons several times in order to actually
     * click go through". A button here fires on MOUSE_CLICKED, which needs
     * the press and the release on the same node; at 20x a month lands every
     * 250 ms and the open screen is rebuilt for it, so a rebuild between the
     * two put a new node under the release and the click was nobody's.
     *
     * So while any mouse button is down anywhere in the window (a filter on
     * the scene, holdPresses()) the clock runs the months and only marks the
     * redraw pending, and the first frame after the release draws it, month
     * or no month. A slider's drag and a chart's are held still the same way,
     * and a TimeChart's settle waits for the release too (PRESS_HELD).
     */
    private boolean pressHeld;
    /** The buttons down, each counted from its press to its release, so one let go while another is held still holds. */
    private final java.util.EnumSet<javafx.scene.input.MouseButton> buttonsDown =
            java.util.EnumSet.noneOf(javafx.scene.input.MouseButton.class);
    /** Set when a release has been delivered, and spent by the next frame: the first frame after a release. */
    private boolean letGo;
    /** The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). */
    static final String PRESS_HELD = "UserInterface.pressHeld";

    /** The date line, kept so a day can be repainted without a whole redraw. */
    private Label dayLabel;

    /** Why the clock stopped itself, shown until it is started again. */
    private String pausedBecause;

    /** The notice that last stopped it, so one condition interrupts once. */
    private String pausedOnKey = "";

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        if (game == null) game = new Game();

        // IBM Plex, before anything is styled (0.7.21): every style built from
        // here on reads the families it loaded. See Fonts.
        Fonts.load();

        //Initialize the core UI once
        this.rootMenu = new VBox(10);
        /*
         * TOP-ALIGNED (0.7.20), on every screen. The column was centred
         * vertically, so a short page jumped whenever its height changed -
         * switching build tabs, the hint line appearing under the grid, the
         * receipt opening, a tab row wrapping differently - and a click aimed
         * at a card landed on the tab row that had moved under it. A page
         * that starts at the top stays where it was put.
         */
        this.rootMenu.setAlignment(Pos.TOP_CENTER);
        // A little air top and bottom. Nothing floats over the foot of the
        // stage since 0.7.21 - the clock is in the header - so nothing has to
        // be left clear for it.
        this.rootMenu.setPadding(new javafx.geometry.Insets(8, 0, 14, 0));
        // A page torn down hears nothing (0.7.50; see clearMenu): while it is emptied, the pointer's
        // exits from what it removes stop here, before any handler on the old page runs.
        this.rootMenu.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_EXITED_TARGET, e -> {
            if (tearingDown) e.consume();
        });

        // Persistent construction panel down the right-hand side. The menu system
        // swaps the contents of rootMenu constantly, so the panel lives outside it
        // and survives every screen change. Since 0.7.24 it is folded to a tab
        // on the stage's right edge and opens over the stage (THE FRAME FOLDS AWAY).
        this.constructionPanel = new VBox(8);
        this.constructionPanel.setPrefWidth(280);
        this.constructionPanel.setMaxWidth(Region.USE_PREF_SIZE);
        /*
         * The right-hand strip, dressed to match the left.
         *
         * Same ground, same border weight, mirrored - so the two read as one
         * frame around the stage rather than as two unrelated sidebars. Jerus:
         * "the right tab make it match the other two".
         */
        this.constructionPanel.setStyle(
                "-fx-padding: 12 12 12 10; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 0 0 0 1;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 16, 0, -3, 0);");

        // City overview down the left. Same reasoning as the construction panel:
        // it lives outside rootMenu so the menu system can't clear it away.
        this.cityPanel = new VBox(8);
        /*
         * WIDER THAN IT WAS, because health went in it.
         *
         * 250 was set when every row here was a label and a short number.
         * Health's rows carry two figures apiece - coverage AND what it buys,
         * places AND the people who need them - and at 250 they wrapped or got
         * cut by shorten(). Thirty pixels buys the whole section.
         */
        this.cityPanel.setPrefWidth(290);
        /*
         * DARK, and the same dark as the top strip.
         *
         * The panel was #f4f4f4 with #333 text, which is a document. It is not
         * a document - it is an instrument cluster (on screen at all times
         * until 0.7.24, a drawer the header's chip opens since), and the thing
         * it most needs to do is let a figure that has gone wrong catch the
         * eye of somebody who is looking at something else. Red on light grey
         * does not; red on near-black does.
         */
        this.cityPanel.setStyle(
                "-fx-padding: 4 10 12 12; -fx-background-color: " + Palette.PANEL + ";");
        VBox.setVgrow(cityPanel, Priority.ALWAYS);
        // A drawer since 0.7.24: the header's "Needs you" chip opens it over
        // the left of the stage. See THE FRAME FOLDS AWAY.
        this.drawer = new VBox(0, drawerBar(), cityPanel);
        this.drawer.setMaxWidth(Region.USE_PREF_SIZE);
        this.drawer.setStyle("-fx-background-color: " + Palette.PANEL + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 0 1 0 0;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 16, 0, 3, 0);");

        /*
         * The header across the top - the clock, the money block, five
         * headline tiles, the "Needs you" chip, the rating and the inbox
         * (0.7.21, 0.7.24; see THE HEADER). The next debt maturities ran
         * across the bottom until 0.7.24; they are NEXT DUE, beside the
         * ladder at the top of the Finances hub, now
         * (FinancesScreen.nextDueCard(); a card of its own until 0.7.32).
         *
         * It lives outside rootMenu for exactly the reason the side panels
         * do: the menu system clears its own children on every screen change,
         * so anything meant to be always-visible has to hang off the
         * BorderPane instead.
         */
        this.header = new HBox(10);
        this.header.setAlignment(Pos.CENTER_LEFT);
        this.header.setPrefHeight(Palette.HEADER);
        this.header.setMinHeight(Palette.HEADER);
        this.header.setStyle(
                "-fx-padding: 10 14 10 14; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 0 0 1 0;");


        /* =====================================================================
           THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start.

           rootMenu was the BorderPane's centre directly, so a menu taller than
           the window simply had its bottom cut off - and the bottom of a menu
           is where the Back button lives. Healthcare came close at fourteen
           buttons under five headings; Education went over, and Jerus lost the
           only way out of the screen.

           WHY THE MIN-HEIGHT BINDING. A ScrollPane top-aligns its content,
           which would have un-centred every menu in the game - forty screens
           changed to fix one. Pinning the VBox to at least the viewport height
           kept it centred while it fitted and let it grow past that when it
           did not. Since 0.7.20 the column is top-aligned (see rootMenu), and
           the binding stays: it is what the redraw holds the page's height
           against (clearMenu). Four pixels of slack, or the binding fights the
           scrollbar it just caused.
           ===================================================================== */
        menuScroller = new javafx.scene.control.ScrollPane(rootMenu);
        menuScroller.setFitToWidth(true);
        menuScroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        menuScroller.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        menuScroller.setVbarPolicy(
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rootMenu.minHeightProperty().bind(menuScroller.heightProperty().subtract(4));

        /*
         * A FILTER ON THE SCROLLER ITSELF, so the page steps the same whether
         * the pointer is over it or not.
         *
         * wheelToPage() catches what nothing else wanted, which is the pointer
         * ANYWHERE BUT the page. Over the page the ScrollPane handles the wheel
         * itself, with the accelerating ramp above - so the two halves of the
         * window scrolled at different speeds and only one of them was ours. A
         * filter runs before the control, which is the only way to get in front
         * of a skin's own handler.
         */
        menuScroller.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;
            // A wheel over a chart that zooms is the chart's (0.7.23; TimeChart.WHEEL_OWNER).
            if (overAChart(e.getTarget())) return;
            javafx.scene.Node page = menuScroller.getContent();
            if (page == null) return;
            double span = pageSpan(page);
            if (span <= 1) return;      // nothing to scroll; let it through
            scrollPageBy(e.getDeltaY(), span);
            e.consume();
        });

        /* =====================================================================
           WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED.

           Jerus, having watched three fixes miss: "i need it so that when
           scrolling, it must remember exactly where youre at and stay there
           unless you continue scrolling regardless if the month passes."

           It used to read the position off the scroller at the top of
           clearMenu and put that same number back a pulse later. Two holes in
           that, and both of them are what he kept seeing. A ScrollPane CLAMPS
           against content it has not laid out, so the number read back could
           already be wrong; and a wheel turn between the read and the write was
           simply overwritten - every month, for as long as the clock ran.

           Now the player's own scrolling is the only thing that writes this
           field, the rebuild is fenced off with settlingScroll so a clamp
           cannot pretend to be a player, and the restore reads the field rather
           than a number captured before the screen was thrown away. Continue
           scrolling and the newest value is what the next month restores.
           ===================================================================== */
        menuScroller.vvalueProperty().addListener((o, was, now) -> {
            /*
             * PUT BACK AT ONCE, IN THIS PULSE - which is the whole of the
             * glitch.
             *
             * The trace caught it exactly: a rebuild clamps vvalue to zero, and
             * the settle a pulse later puts it back. One frame gets painted at
             * the top in between, and that frame is what Jerus saw - "the
             * system reverts back to top, but then UI kicks in and goes like
             * no, and it reverts where you were, all very fast".
             *
             * Holding the page's height was meant to stop the clamp happening
             * and does not: the trace shows minH pinned at 2115.2 and vvalue
             * going to zero regardless. So instead of preventing it, undo it
             * immediately - synchronously, inside the listener, before anything
             * is rendered. The value never spends a frame wrong.
             *
             * correcting guards the re-entry, since setting vvalue here fires
             * this listener again.
             */
            if (settlingScroll) {
                if (!correcting && pageScrollAt > 0
                        && Math.abs(now.doubleValue() - pageScrollAt) > 1e-9) {
                    correcting = true;
                    try { menuScroller.setVvalue(pageScrollAt); }
                    finally { correcting = false; }
                }
            } else {
                pageScrollAt = now.doubleValue();
            }
        });

        /* =====================================================================
           THE RAIL, AND WHY IT WAS PART OF THE PANEL.

           Jerus: "right where that city overview section ends and the blue box
           starts, add a sort of tab list, which will be the buttons we already
           have in the center, that way for easier navigation."

           Every top-level screen in this game used to be reached from ONE place
           - a column of buttons on a main screen you had to go back to first.
           Land was two clicks from the bank; the graphs were three from
           anywhere. The rail is the same list of destinations, on screen from
           every screen, so the cost of looking at something is one click from
           wherever you are.

           It is painted on the PANEL's ground and not the stage's, deliberately:
           it is the panel's right-hand edge, so the eye reads one instrument
           cluster with a spine down it rather than a floating strip of icons in
           the middle of the window. The panel got wider to pay for it - the
           numbers keep the 290 they already needed, and the rail's 46 is
           additional rather than taken.

           AT THE WINDOW'S EDGE SINCE 0.7.21, as the mockups draw it: 76 wide,
           each button an icon over its name, the selected one in its area's
           colour (see THE RAIL). A rail of names reads on its own, and the
           window's edge is where a player's eye goes for the way round.
           ===================================================================== */
        this.tabRail = new VBox(0);
        this.tabRail.setPrefWidth(RAIL_WIDTH);
        this.tabRail.setMinWidth(RAIL_WIDTH);
        this.tabRail.setMaxWidth(RAIL_WIDTH);
        this.tabRail.setAlignment(Pos.TOP_CENTER);
        this.tabRail.setStyle(
                "-fx-padding: 6 0 0 0; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 0 1 0 0;");

        // The rail alone at the left since 0.7.24: the City overview panel is a
        // drawer over the stage (THE FRAME FOLDS AWAY).

        /* =====================================================================
           THE STAGE IS A STACK NOW.

           Three things had to float over the middle rather than sit in the
           column with the menus: the inbox in the top right, the time controls
           in the bottom right, and the income dome at the bottom. All three are
           true of the CITY rather than of whatever screen is open, so putting
           them in rootMenu would mean every one of fifty screens drawing them,
           and a screen that forgot would silently lose the player's ability to
           advance a month. Since 0.7.21 the clock and the month's income are
           in the header and the envelope is the header's; what floats is the
           inbox's list and the toasts - and since 0.7.24 the City overview's
           drawer and the construction panel (THE FRAME FOLDS AWAY).

           Each overlay is pinned to USE_PREF_SIZE. A StackPane stretches its
           children to fill by default, and a stretched transparent overlay would
           swallow every click meant for the menu underneath it.
           ===================================================================== */
        this.stagePane = new StackPane();
        this.stagePane.setStyle("-fx-background-color: " + STAGE + ";");
        this.stagePane.getChildren().add(menuScroller);

        /*
         * THE DRAWER AND THE PANEL, over the stage (0.7.24): the City overview
         * at the left, opened by the header's chip, and the construction panel
         * at the right, opened by its tab. Each takes the stage's height and
         * no more width than its own; under the inbox's list and the toasts.
         */
        StackPane.setAlignment(drawer, Pos.TOP_LEFT);
        drawer.setMaxHeight(Double.MAX_VALUE);
        showIf(drawer, false);
        this.stagePane.getChildren().add(drawer);
        StackPane.setAlignment(constructionPanel, Pos.TOP_RIGHT);
        constructionPanel.setMaxHeight(Double.MAX_VALUE);
        showIf(constructionPanel, false);
        this.stagePane.getChildren().add(constructionPanel);
        this.constructionTab = new VBox(8);

        /*
         * The inbox's list FLOATS too, and it earns it: it is shut almost
         * always, it is opened deliberately (the envelope in the header), and
         * while it is open covering the top corner of the screen is the point.
         */
        this.inboxCorner = new VBox(0);
        this.inboxCorner.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(inboxCorner, Pos.TOP_RIGHT);
        StackPane.setMargin(inboxCorner, new javafx.geometry.Insets(6, 14, 0, 0));
        this.stagePane.getChildren().add(inboxCorner);

        // The toasts, at the stage's bottom right, clear of the strips and the
        // inbox (0.7.20) - the corner the clock sat over until 0.7.21. Not
        // picked on its bounds, so the gaps between toasts are the page's.
        this.toastStack = new VBox(6);
        this.toastStack.setAlignment(Pos.BOTTOM_RIGHT);
        this.toastStack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.toastStack.setPickOnBounds(false);
        StackPane.setAlignment(toastStack, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toastStack, new javafx.geometry.Insets(0, 24, 16, 0));
        this.stagePane.getChildren().add(toastStack);

        /* =====================================================================
           AND THE TIME CONTROLS DID NOT FLOAT, WHICH WAS A CORRECTION.

           They were overlaid on the stage with the dome, and play-testing the
           healthcare build menu showed exactly what that costs: the dome sat on
           top of the Memorial Cemetery row and hid its price. They moved to a
           strip of their own below the scroller, and since 0.7.21 they are in
           the header with the date (see THE HEADER): the play button, the
           month and the speed, and the dome's figure on the TREASURY tile
           (the money block's since 0.7.24). So the stage is the scroller
           alone, to the window's foot.
           ===================================================================== */
        VBox stageColumn = new VBox(stagePane);
        VBox.setVgrow(stagePane, Priority.ALWAYS);
        stageColumn.setStyle("-fx-background-color: " + STAGE + ";");

        /* =====================================================================
           THE WHEEL WORKS WHERE THE POINTER ALREADY IS.

           Jerus: "in the buildings section, if you scroll, and then click next
           month, and then try to scroll it doesn't let you, it locks."

           It was not locked. Clicking Next Month leaves the pointer ON the Next
           Month button, and that button lived in the bottom strip, which was a
           SIBLING of the scroller rather than inside it - so the wheel event
           went to the button, bubbled up to a parent with nothing to say about
           it, and died. Moving the pointer back over the page fixed it, which
           is why clicking another window and coming back looked like the cure:
           the mouse travelled.

           The player's model is that the wheel scrolls the page, and the page
           is the whole middle of the window. So the middle of the window listens
           for what nothing else wanted. A HANDLER, not a filter, and on the
           column rather than the scroller: it runs on the way back UP, after the
           real target and any inner scroller have had their turn and consumed
           what they used, so a wheel over a sector statement still scrolls the
           statement and only the leftovers land here.
           ===================================================================== */
        stageColumn.addEventHandler(javafx.scene.input.ScrollEvent.SCROLL, this::wheelToPage);

        BorderPane root = new BorderPane();
        // The stage, and deliberately DARKER than the strips around it (the
        // header, the rail and the construction tab; four, with the debt bar
        // along the foot, until 0.7.24).
        // Chrome frames content; the same colour on both would make the window
        // one flat field with things floating in it.
        root.setStyle("-fx-background-color: " + STAGE + ";");
        menuScroller.setStyle("-fx-background-color: " + STAGE + ";"
                + " -fx-background: " + STAGE + ";");
        root.setCenter(stageColumn);
        root.setLeft(tabRail);
        root.setRight(constructionTab);
        root.setTop(header);
        // A stack over the whole window, so a dialog can be laid over all of it
        // and take every click while it is up (0.7.20; see showQuitDialog) -
        // and, since 0.7.21, the main menu and the founding screen over their
        // backdrop (see THE MAIN MENU).
        this.windowStack = new StackPane(root);
        this.scene = new Scene(windowStack);
        applyTheme(scene);
        dressTooltips();


        // The window title is the cheapest possible bug report: whatever a
        // player screenshots now says which build produced it. It is invisible
        // full screen, which is the other reason the version is in the log too.
        stage.setTitle(GameVersion.title());
        stage.setMaximized(true);

        /* =================================================================
           FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT
           WANT.

           Jerus: "when you play the game its full scren, like not top white
           bar with game name and not bottom windows bar." setFullScreen(true)
           gives exactly that - JavaFX takes the whole display, the decorations
           and the taskbar included. Both of the lines under it are corrections.

           ESCAPE IS THE PAUSE MENU IN THIS GAME. JavaFX's default full-screen
           exit key is also Escape, so left alone the two fight: the first press
           drops the window out of full screen instead of opening the menu, and
           a player who wanted the menu gets a title bar back. NO_MATCH takes
           Escape away from the platform and leaves it to the filter below. F11
           is the way out instead, which is what every other game uses.

           AND THE HINT IS AN OVERLAY. JavaFX paints "Press ESC to exit full
           screen" across the top of the window for a few seconds on entry -
           over the header (the date bar then), saying the wrong key, on every
           single launch.
           ================================================================= */
        prefs = GamePrefs.load(game.getGameFiles());
        stage.setFullScreenExitKeyCombination(
                javafx.scene.input.KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(prefs.isFullScreen());

        /*
         * COMING BACK OUT LANDS MAXIMISED, and this has to be a listener.
         *
         * Play-tested: setMaximized(true) on the line after setFullScreen(false)
         * did nothing, and F11 dropped the game into a 600px window in the
         * corner. JavaFX restores the window's pre-full-screen bounds when it
         * leaves, and it does that AFTER the call returns - so the restore
         * lands on top of the maximise rather than the other way round. Worse,
         * the bounds it restores are the ones the window had before it was ever
         * maximised, because this game goes full screen during start().
         *
         * The listener fires once the state has actually changed, and runLater
         * puts the maximise after the restore that follows it.
         */
        stage.fullScreenProperty().addListener((o, was, now) -> {
            if (!now) javafx.application.Platform.runLater(() -> stage.setMaximized(true));
        });

        stage.setScene(scene);
        stage.show();
        startClock();

        // Esc is the pause menu, from anywhere. See the Menu button at the foot
        // of the rail for the half of this a new player can find on their own -
        // a shortcut nothing announces is a shortcut for people who already
        // know the game.
        /*
         * A FILTER, NOT A HANDLER, and the difference is the whole fix.
         *
         * setOnKeyPressed is the bubbling phase: the event reaches the focus
         * owner first and only travels up to the Scene if nothing swallowed it.
         * Something does - clicking any button in this game leaves focus inside
         * the menu column, and Escape never arrived. Play-tested: pressing it
         * did nothing at all.
         *
         * An event FILTER runs on the way DOWN, before any control can consume
         * it, so Escape means the same thing on every screen no matter what was
         * last clicked.
         */
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {

            /*
             * NOT WHILE SOMEBODY IS TYPING. A filter runs before the control
             * that has focus, so a single-letter shortcut would eat the P out
             * of a save name. Escape and F11 are safe anywhere; P is not, and
             * the guard costs one line.
             */
            boolean typing = scene.getFocusOwner() instanceof javafx.scene.control.TextInputControl;

            /*
             * AN OPEN DIALOG KEEPS ESCAPE FOR CLOSING ITSELF (0.7.20), and
             * the shortcuts wait until it is gone: every other key goes to the
             * dialog's own buttons, as on any dialog.
             */
            if (quitDialog != null) {
                if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    closeQuitDialog();
                    e.consume();
                }
                return;
            }

            /*
             * THE CHART'S FULL SCREEN KEEPS ESCAPE FOR LEAVING IT (0.7.23):
             * the first Esc comes back to City History, the next opens the
             * menu as everywhere. P leaves it and opens the menu, since the
             * menu is drawn over the window too. F11 is not this - it is the
             * window's own full screen, and passes through untouched.
             */
            if (chartFull != null) {
                if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    leaveChartFullScreen(true);
                    e.consume();
                    return;
                }
                if (!typing && e.getCode() == javafx.scene.input.KeyCode.P) leaveChartFullScreen(false);
            }

            /*
             * ESC PUTS AWAY THE INBOX'S LIST, THEN THE DRAWER (0.7.24), one
             * a press, before it is the menu: each is over the page, and Esc
             * is how anything over the page goes away. The list stayed down
             * through the main menu until this, a fix.
             */
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE && (inboxOpen || drawerOpen)
                    && !isGameMenu(currentScreen)) {
                if (inboxOpen) {
                    inboxOpen = false;
                    refreshInbox();
                } else {
                    drawerOpen = false;
                    placeFrame();
                }
                e.consume();
                return;
            }

            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE
                    || (!typing && e.getCode() == javafx.scene.input.KeyCode.P)) {
                // A tooltip still up would hang over the menu.
                hideTooltips();
                showMainMenu();
                e.consume();
            } else if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                toggleFullScreen();
                e.consume();
            } else if (FoundingScreen.SCREEN.equals(currentScreen)
                    && e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                /*
                 * ENTER FOUNDS, on the founding screen (0.7.10) - from the
                 * name field too, which is where a player's hands are - and
                 * only when Found is lit; otherwise it falls through to the
                 * focused control. Esc is already the way back: it is the
                 * main menu from anywhere, and the main menu is where this
                 * screen came from.
                 */
                if (foundingScreen.foundIfReady()) e.consume();
            } else if (!typing && e.getCode() == javafx.scene.input.KeyCode.SPACE
                    && !isGameMenu(currentScreen)) {
                /*
                 * SPACE IS PLAY/PAUSE, and it HAS to be consumed here.
                 *
                 * Space is also how JavaFX activates a focused button, and in
                 * this game something is always focused - clicking anything at
                 * all leaves focus on it. Without the consume, space would
                 * toggle the clock AND press whatever was last clicked, which
                 * on a build screen means buying a second one of something.
                 *
                 * Only in the city, and passed through on the menus, where
                 * there is no clock to toggle and space activating the button
                 * under the cursor is the behaviour a player expects.
                 */
                setClockRunning(!clockRunning);
                redrawScreen.run();
                e.consume();
            } else if (!typing && !isGameMenu(currentScreen)
                    && (e.getCode() == javafx.scene.input.KeyCode.LEFT
                     || e.getCode() == javafx.scene.input.KeyCode.RIGHT)) {
                /*
                 * LEFT AND RIGHT ARE THE LADDER, one rung a press. Jerus: "when
                 * you press the right and left keys the speed decreases /
                 * increases."
                 *
                 * Consumed for the same reason SPACE is, and more so: an arrow
                 * key is how JavaFX moves focus between controls and how it
                 * drags a focused Slider. Unconsumed, a right-arrow on the
                 * policy screen would move the speed AND the tax dial under the
                 * pointer.
                 */
                int pick = Math.max(0, Math.min(SPEEDS.length - 1, speedIndex
                        + (e.getCode() == javafx.scene.input.KeyCode.RIGHT ? 1 : -1)));
                if (pick != speedIndex) {
                    speedIndex = pick;
                    redrawScreen.run();
                }
                e.consume();
            } else if (!typing && "handleAllBuildingMenus".equals(currentScreen)
                    && (e.getCode() == javafx.scene.input.KeyCode.ENTER
                     || e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE
                     || e.getCode() == javafx.scene.input.KeyCode.DELETE)) {
                /*
                 * ENTER BUILDS, BACKSPACE CLEARS, on the build page. Jerus: "in
                 * the building rail, when you have lets say 3 ready to build, i
                 * want to be able to press enter to build, instead of having to
                 * click the green button, you can still click it, but just a
                 * short cut, and backspace/delete to reset it."
                 *
                 * Only on the category page itself, not the refusal screens the
                 * build tab also owns (tabFor): there the orders are not on
                 * the screen to be built or cleared.
                 *
                 * Consumed only when it did something, the other way round
                 * from SPACE: with nothing pending, Enter on a focused button
                 * should still press it, and Backspace means nothing here.
                 * With orders pending it is consumed, so Enter does not also
                 * press whatever was last clicked. What the two keys do is
                 * BuildScreen's (buildPending, clearPending); the page says
                 * them in a caption under the grid and the Build button's
                 * tooltip, for the reason Escape's comment gives above.
                 */
                boolean acted = e.getCode() == javafx.scene.input.KeyCode.ENTER
                        ? buildScreen.buildPending()
                        : buildScreen.clearPending();
                if (acted) e.consume();
            }
        });

        /*
         * A CLICK OUTSIDE THE DRAWER CLOSES IT (0.7.24), unless it is pinned:
         * a filter, so it sees the press before whatever was pressed, and
         * lets it through - the click still does what it was aimed at. The
         * chip is its own door and is left to its own handler.
         */
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            if (!drawerOpen || (prefs != null && prefs.isDrawerPinned())) return;
            if (e.getTarget() instanceof javafx.scene.Node n
                    && (inside(n, drawer) || (needsChipNode != null && inside(n, needsChipNode)))) return;
            drawerOpen = false;
            placeFrame();
        });
        holdPresses();

        // button actions
        showMainMenu();


    }

    /**
     * Clears the menu area and refreshes the construction panel. Every screen
     * calls this instead of rootMenu.getChildren().clear() directly, so the panel
     * is always showing current data no matter which screen you're on.
     */
    void clearMenu(String screen, Runnable again) {
        // See receiptPulse: an animation on a node that is about to be thrown
        // away keeps running forever otherwise.
        if (receiptPulse != null) {
            receiptPulse.stop();
            receiptPulse = null;
        }
        // The receipt is a popover on the build page (0.7.20); it goes when the page does.
        if (buildScreen != null && !"handleAllBuildingMenus".equals(screen)) buildScreen.closeReceipt();

        /* =================================================================
           AND THE SCROLL STAYS WHERE YOU PUT IT.

           Jerus: "when you click next month or press some button and you had
           something half scrolled it reset, which is well annoying."

           It reset because every screen in this game is drawn by throwing its
           contents away and building them again, and a ScrollPane whose content
           changes goes back to the top. That is right when the content is a
           DIFFERENT screen and wrong when it is the same one redrawn - and
           until now nothing here knew the difference, because nothing recorded
           which screen was on show.

           So the screen names itself. Every caller passes the method it is in,
           which costs one argument and buys the only distinction that matters:
           the same screen redrawn keeps its position, a new screen starts at the
           top. Pressing Next Month, buying a building, opening a section - all
           of those redraw the screen you are already on, and all of them now
           leave the view alone.

           innerScrollAt is cleared on a real screen change for the same reason:
           the scrollers INSIDE a screen (the land listing, the tax dials, a
           sector report) are keyed by screen name and would otherwise restore a
           position from the last time you visited, which is not what "go to the
           top of a new screen" means.
           ================================================================= */
        // The chart's full screen is City History's, and goes when another screen comes (0.7.23) -
        // without History redrawing itself in the middle of this one's drawing. Since 0.7.61 the pane is
        // the screen's that laid it (the land office's map too), and its own redraws on the clock keep it.
        if (chartFull != null && !screen.equals(chartFullOwner)) leaveChartFullScreen(false);
        // ...and City History, left, lets go of its chart's drag and its pending redraw (after the docs pass).
        if ("showHistoryMenu".equals(currentScreen) && !"showHistoryMenu".equals(screen)) historyScreen.leaving();
        boolean sameScreen = screen.equals(currentScreen) && !railJump;
        railJump = false;
        // Anything vvalue does from here until the settle below is the rebuild
        // clamping, not the player.
        settlingScroll = true;
        if (!sameScreen) pageScrollAt = 0;
        // The page's position is no longer read off the scroller here - see
        // pageScrollAt, which the player alone writes.
        if (!sameScreen) innerScrollAt.clear();
        // An (i)'s popover belongs to the page it was opened on (0.7.21).
        if (!sameScreen) Pieces.closePopover();
        /*
         * ...AND SO DO THE INBOX'S LIST AND THE DRAWER (0.7.24, a fix). The
         * list stayed down across screens and through the main menu - seen
         * in the 0.7.23 walkthrough - because nothing but the envelope ever
         * put it away. A real screen change puts it away now, as the main menu
         * (a screen change too) and Esc do; the City overview's drawer goes
         * the same way unless the player has pinned it.
         */
        if (!sameScreen) {
            inboxOpen = false;
            if (prefs == null || !prefs.isDrawerPinned()) drawerOpen = false;
        }
        currentScreen = screen;

        /*
         * HOW TO DRAW THIS SCREEN AGAIN.
         *
         * The clock advances a month from wherever the player happens to be
         * (the header's play button, since 0.7.21), so after the month has run it has to redraw
         * whatever was on show - and a string cannot be called. Every screen
         * hands over a lambda that calls itself with the arguments it was given,
         * which is why the call reads clearMenu("showMiningMenu", () ->
         * showMiningMenu()) rather than something cleverer: the screen that
         * knows its own arguments is the only thing that can supply them.
         */
        redrawScreen = again;

        /* =================================================================
           HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is
           never lost and never has to be put back.

           Jerus, on the glitch: "when something requires scrolling, and the
           next month passes, the system reverts back to top, but then UI kicks
           in and goes like no, and it reverts where you were, all very fast, so
           you just see the screen glitch."

           That is exactly what was happening, and it is one line below.
           getChildren().clear() empties the page; rootMenu's minHeight is bound
           to the viewport, so the page shrinks to the viewport; a ScrollPane
           with nothing to scroll CLAMPS vvalue to zero; a frame gets painted at
           the top; and the settle below then puts the position back. Revert,
           repaint, revert - every month, on every screen with more content than
           fits.

           No amount of restoring it faster fixes that, because restoring is the
           problem. So the height is PINNED to what it already was for the
           length of the rebuild: the scrollable range does not collapse, vvalue
           is never clamped, and the position simply never moves. The settle
           releases it once the new content is in.

           ONLY ON A REDRAW OF THE SAME SCREEN. A real screen change is supposed
           to go to the top, and holding a short new screen at a tall old one's
           height would leave the player looking at blank space.
           ================================================================= */
        boolean heldPage = false;
        // Guarded on the scroller too: the release lives inside its block, so a
        // hold taken without one would never come off.
        if (sameScreen && menuScroller != null && rootMenu.getHeight() > 0) {
            rootMenu.minHeightProperty().unbind();
            rootMenu.setMinHeight(rootMenu.getHeight());
            heldPage = true;
        }
        final boolean releasePage = heldPage;

        /* =================================================================
           A PAGE TORN DOWN HEARS NOTHING (0.7.50).

           Removing a node the pointer is over makes JavaFX tell it the
           pointer left - from INSIDE the children list's change, child by
           child: each child before it has already been taken off the scene,
           and the list is emptied only after the last. Jerus's game, 0.7.49:
           a chart on Government redrew on that exit from data the month had
           moved under it and threw, the change stopped half way, and the
           children it had taken off stayed in the list with no scene. Picking
           threw on every move of the mouse after that, and a Parent whose
           books disagree with its list is the likeliest cause of the freeze
           that came later.

           The page is being thrown away, so nothing on it needs telling:
           while the flag is up, rootMenu's filter (start()) swallows every
           exit from what is being removed, before any handler on the old page
           runs, whatever that handler would have done. The charts guard
           their own drawing as well (TimeChart.draw()).
           ================================================================= */
        tearingDown = true;
        try {
            rootMenu.getChildren().clear();
        } finally {
            tearingDown = false;
        }
        // The main menu and the founding screen are drawn over the whole window
        // (0.7.21); every other screen takes the layer away. See THE MAIN MENU.
        // ...and so are the menu's own Settings, Load and Save while no city
        // is open (0.7.22): on a cold start there is nothing of a city to
        // show around them. See menuPage().
        boolean overBackdrop = !cityOpen && coldMenu(screen);
        showTitleLayer("showMainMenu".equals(screen) || FoundingScreen.SCREEN.equals(screen) || overBackdrop,
                FoundingScreen.SCREEN.equals(screen) || overBackdrop);
        summaryScreen.refreshCityPanel();
        refreshConstructionPanel();
        refreshHeader();
        refreshTabRail();
        refreshInbox();
        placeFrame();

        if (menuScroller != null) {
            if (!sameScreen) menuScroller.setVvalue(0);
            /*
             * LAID OUT ALWAYS, AND PUT BACK FROM THE FIELD - which is the
             * newest thing the player did, not a number read before the screen
             * was torn down. settlingScroll comes off at the end so the clamps
             * this pass causes cannot be mistaken for somebody scrolling.
             */
            javafx.application.Platform.runLater(() -> {
                menuScroller.applyCss();
                menuScroller.layout();
                if (pageScrollAt > 0) menuScroller.setVvalue(pageScrollAt);
                /*
                 * ...AND THE HOLD COMES OFF LAST, after the new content is in
                 * and measured. Released any earlier and the page would shrink
                 * to the viewport for a frame, which is the collapse this was
                 * put in to prevent.
                 */
                if (releasePage) {
                    rootMenu.minHeightProperty().bind(
                            menuScroller.heightProperty().subtract(4));
                }
                settlingScroll = false;
            });
        }
    }

    /** Where the player has scrolled the page to. Written only by them. */
    private double pageScrollAt;

    /** True while a rebuild is in flight, so its clamps are not mistaken for a hand. */
    private boolean settlingScroll;

    /** Guards the re-entry when the listener corrects a clamp of its own. */
    private boolean correcting;

    /* =====================================================================
       A WHEEL NOTCH IS WORTH THE SAME EVERY TIME

       The trace, on one unbroken scroll of the healthcare list:

           0.0004  0.0016  0.0048  0.0027  0.0050  0.0064  0.0091
           0.0126  0.0141  0.0175  0.0183  0.0209  0.0596

       That is JavaFX's own smooth-scroll ramp, and against a page of 2,115
       pixels the first notch is worth about six of them. Keep turning and it
       accelerates a hundred and fifty fold. Jerus: "the top is a magnetic, if
       you touch it, you can seperate but it requires force lol" - and it is not
       the top. Every gesture starts that slowly; the top is simply where a
       gesture usually starts.

       So the wheel is taken over outright. A floor under the distance, not a
       cap: small platform deltas get raised to something a person can feel, and
       a genuine fling still travels as far as it asked to.
       ===================================================================== */

    /**
     * The least the first wheel event of a gesture may move the page, in pixels
     * (since 2026-09-18 the first only; see scrollPageBy).
     *
     * Seventy-two first, which Jerus called "very sensitive" - fair, since the
     * platform was sending six-pixel deltas and that is a twelvefold lift.
     * Forty-eight is the three lines a wheel notch conventionally means, and
     * still eight times what a first notch was worth before.
     */
    private static final double WHEEL_STEP = 48;

    /** Which show*Menu drew what is on screen; see clearMenu. */
    String currentScreen = "";

    /** True while clearMenu empties the page: rootMenu swallows the pointer's exits from what it removes (0.7.50). */
    private boolean tearingDown;

    /** How to draw it again after a month passes; see clearMenu. */
    private Runnable redrawScreen = () -> { };

    /**
     * Draw the screen that is showing, again, in place - the same call the
     * clock makes after a month. For a screen piece that lives on more than
     * one tab (the staged-lever slider is on Policy and on Services) and
     * cannot know which screen to redraw by name.
     */
    void redraw() { redrawScreen.run(); }

    /** The screen Esc was pressed on, so Continue (Resume until 0.7.21) can go back to it. */
    private Runnable resumeTo;

    /**
     * The screens that are ABOUT the game rather than in it.
     *
     * The rail, the inbox and the time controls are all statements about a city
     * - which one you are looking at, what it has to say, and how to move it
     * forward. On the title screen and the save menus there is no city being
     * looked at, and a Next Month button on a title screen is a button that
     * advances a game the player has not opened yet.
     */
    private static boolean isGameMenu(String screen) {
        switch (screen) {
            case "showMainMenu": case "showSavingMenu": case "showSaveSlotConfirm":
            case "showLoadMenu": case "loadSlot": case "showSaveResult":
            case "showSettingsMenu": case FoundingScreen.SCREEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Where a scroller inside the CURRENT screen was left, by name.
     *
     * Emptied whenever the screen actually changes, so a position is only ever
     * restored to the screen that produced it.
     */
    final java.util.Map<String, Double> innerScrollAt = new java.util.HashMap<>();

    /**
     * The same, for the two side panels - and this one is NEVER emptied.
     *
     * The panels are not screens. They are on show the whole game, they rebuild
     * on every single click because they carry live figures, and a player who
     * has scrolled the city panel down to the buildings list means to keep
     * looking at the buildings list.
     */
    private final java.util.Map<String, Double> panelScrollAt = new java.util.HashMap<>();

    /** A scroller inside a screen, which remembers where it was while you stay. */
    private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content) {
        return remembering(innerScrollAt, key, content, false);
    }

    /**
     * The same, remembering the distance from the BOTTOM rather than the
     * fraction - for a page whose top half changes height under the player.
     *
     * City History is the case (Jerus, 2026-09-18: "when you click some
     * buttons it sometimes moves"). Its legend sits under the chart and grows
     * a block for every line picked, and the picker that picks them sits
     * below the legend - so ticking a line inserted a block ABOVE the pointer,
     * and a position kept as a fraction of a taller page put the picker
     * somewhere else. Everything under the picker keeps its height, so
     * measured from the bottom the picker does not move at all. Kept as the
     * fraction too, for the first restore before the page has a height.
     */
    private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content) {
        return remembering(innerScrollAt, key, content, true);
    }

    /** A scroller in one of the side panels, which always remembers. */
    javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content) {
        return remembering(panelScrollAt, key, content, false);
    }

    /**
     * A ScrollPane that files its own position under a name and comes back to it.
     *
     * The position is read into a local BEFORE the listener goes on, because
     * laying the new content out fires the listener with a clamped zero - so
     * reading the map later would read the value the layout just destroyed.
     */
    private javafx.scene.control.ScrollPane remembering(
            java.util.Map<String, Double> where, String key, javafx.scene.Node content,
            boolean fromBottom) {

        javafx.scene.control.ScrollPane scroller =
                new javafx.scene.control.ScrollPane(content);
        double was = where.getOrDefault(key, 0.0);
        // ...and the same position in pixels up from the bottom, read now for the
        // same reason: the layout below writes a clamped value over it
        double bottom = where.getOrDefault(key + ":bottom", -1.0);
        scroller.vvalueProperty().addListener((o, from, to) -> {
            where.put(key, to.doubleValue());
            double span = scrollSpan(scroller);
            if (span > 1) where.put(key + ":bottom", (1 - to.doubleValue()) * span);
        });
        if (fromBottom && was > 0 && bottom >= 0) {
            // at the top stays at the top; anywhere else the distance from the
            // bottom is turned into a fraction of whatever height the page has
            // by the time each attempt runs
            restoreScroll(scroller, () -> {
                double span = scrollSpan(scroller);
                return span > 1 ? Math.max(0, Math.min(1, 1 - bottom / span)) : was;
            });
        } else {
            restoreScroll(scroller, was);
        }
        return scroller;
    }

    /** How far a scroller's content can travel, in pixels, as laid out right now. */
    private static double scrollSpan(javafx.scene.control.ScrollPane scroller) {
        javafx.scene.Node content = scroller.getContent();
        javafx.geometry.Bounds view = scroller.getViewportBounds();
        if (content == null || view == null || view.getHeight() <= 0) return 0;
        return content.getLayoutBounds().getHeight() - view.getHeight();
    }

    /**
     * Put it back, and put it back BEFORE anything is painted.
     *
     * A ScrollPane clamps vvalue against its content, and at the moment new
     * content is handed over that content has not been laid out - so setting
     * the value now can set it against a height of zero and land at the top
     * anyway. The old answer was runLater, and it worked, but runLater is a
     * whole pulse late: the frame in between got drawn, so picking another line
     * on the graph screen flashed to the top and snapped back.
     *
     * Jerus: "if you scroll down and click for another thing it works and it
     * doesn't scroll up but for a few milliseconds it scrolls up and then back
     * where it should be at."
     *
     * Three attempts, cheapest first, and they cost nothing when an earlier one
     * has already worked because setting a property to the value it holds is
     * not a change:
     *
     *   1. If the scroller is already measured - which every scroller that
     *      stays on screen is, menuScroller included - set it now, inside this
     *      pulse. Nothing has been drawn yet, so there is nothing to flash.
     *   2. A brand new scroller has no viewport until it is laid out. Catch the
     *      layout pass that gives it one and set it there, ONE SHOT, removing
     *      the listener the moment it fires. It has to be one shot: a listener
     *      left on would drag the view back every time the window resized.
     *   3. runLater as the backstop, for whatever the first two miss. This is
     *      the old behaviour and the old flicker, now only in the cases that
     *      would have flickered anyway.
     */
    private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to) {
        restoreScroll(scroller, () -> to);
    }

    /**
     * The same, with the position asked for afresh at each of the three
     * attempts - so a caller keeping pixels rather than a fraction can turn
     * them into a fraction of the height the page actually has by then.
     */
    private static void restoreScroll(javafx.scene.control.ScrollPane scroller,
                                      java.util.function.DoubleSupplier target) {

        /*
         * IT LAYS THE SCROLLER OUT EVEN WHEN to IS ZERO, and that is not a
         * detail - it is the whole of the dead wheel.
         *
         * This used to open `if (to <= 0) return;`. So a screen arriving at the
         * TOP - a new screen, or the same one redrawn while the player had not
         * scrolled - got no layout pass at all, and a ScrollPane that has not
         * been laid out against its new content does not yet know the content
         * is taller than the viewport. Nothing to scroll, as far as it is
         * concerned, so the wheel did nothing: not over the list, not over the
         * bottom strip, not anywhere. wheelToPage's own `if (span <= 1) return`
         * read the same stale measurement and agreed.
         *
         * Dragging the scrollbar forces the layout, which is why one drag fixed
         * it for the rest of the session, and why it only ever bit on arrival.
         * And it is why scrolling once made it work for ever after: from then on
         * to was positive, so the pass below ran on every rebuild.
         *
         * Jerus, twice, and the second description is the one that solved it:
         * "in the buildings menu ... you cant scroll for a few seconds then it
         * lets you scroll", then "scroll is locked, unless you use the manual
         * scroll then it unlocks itself" - dead everywhere, from the moment the
         * menu opens. Everywhere ruled out event routing; from the moment it
         * opens ruled out anything the month does.
         */
        /*
         * ALL THREE, EVERY TIME, AND THE THIRD IS NOT OPTIONAL (2026-09-14).
         *
         * This was "fixed" on the reading that the runLater was a backstop that
         * only needed to fire when the first two missed, because applyCss() and
         * layout() are not free and setVvalue() a pulse late can overrule a
         * player who has since scrolled. Both of those observations are true
         * and the conclusion was wrong: the third attempt is doing REAL WORK in
         * the ordinary case, not standing by.
         *
         * A ScrollPane clamps vvalue against its content. Attempts 1 and 2 fire
         * while the new content is measured but NOT yet laid out, so the value
         * they set is clamped against a height that is not the real one and
         * lands at or near the top. The runLater's applyCss() + layout() is
         * what gives the clamp something true to work against, and its
         * setVvalue() is the one that sticks.
         *
         * Measured by shipping it: with the runLater made conditional, the
         * build menu went to the top on every month. Jerus: "i cant scroll in
         * the buildings menu and it goes immediately to the top when the next
         * month passes."
         *
         * So it stays. The flicker it costs is real and is the lesser problem;
         * fixing it means not needing three attempts, which means laying the
         * content out before the value is set rather than after - a bigger
         * change than a guard.
         */
        /*
         * WHAT WE LAST PUT THERE OURSELVES, so the pass below can tell its own
         * handiwork from the player's. NaN means we have not set anything yet.
         */
        final double[] ours = { Double.NaN };

        if (target.getAsDouble() > 0) {
            javafx.geometry.Bounds view = scroller.getViewportBounds();
            if (view != null && view.getHeight() > 0) {
                scroller.setVvalue(target.getAsDouble());
                ours[0] = scroller.getVvalue();
            } else {
                scroller.viewportBoundsProperty().addListener(
                        new javafx.beans.value.ChangeListener<javafx.geometry.Bounds>() {
                    @Override
                    public void changed(
                            javafx.beans.value.ObservableValue<? extends javafx.geometry.Bounds> o,
                            javafx.geometry.Bounds was, javafx.geometry.Bounds now) {
                        if (now == null || now.getHeight() <= 0) return;
                        scroller.viewportBoundsProperty().removeListener(this);
                        scroller.setVvalue(target.getAsDouble());
                        ours[0] = scroller.getVvalue();
                    }
                });
            }
        }

        /*
         * THE LAYOUT ALWAYS; THE POSITION ONLY IF THE PLAYER HAS NOT MOVED IT.
         *
         * Reading the value back after setting it is the whole trick, because a
         * ScrollPane CLAMPS: ask for 0.8 against content it has not laid out
         * yet and it keeps 0.3. So `ours` is what actually landed, not what was
         * asked for, and anything different a pulse later was somebody's hand.
         *
         * Without this the month clobbers the player. The clock redraws the
         * screen every few seconds, each redraw captures the position at the
         * TOP of clearMenu and re-applies it here a pulse later, and a wheel
         * turn in that window is simply undone - over and over, for as long as
         * time is running. Jerus: "say healthcare tab in the building rail, if
         * months are playing... you cant scroll... unless you put the mouse in
         * the scrolling thing and then it does scroll". Inside the scroll area
         * he could hold the bar and keep winning the argument; anywhere else the
         * wheel lost it every month.
         *
         * The layout stays unconditional. That half is load-bearing - see the
         * note above on what happens to a scroller that never gets laid out.
         */
        javafx.application.Platform.runLater(() -> {
            scroller.applyCss();
            scroller.layout();
            double to = target.getAsDouble();   // after the layout, so pixels convert against the real height
            if (to <= 0) return;
            boolean playerMoved = !Double.isNaN(ours[0])
                    && Math.abs(scroller.getVvalue() - ours[0]) > 1e-6;
            if (!playerMoved) scroller.setVvalue(to);
        });
    }

    /**
     * How far the page can travel: what the content WANTS to be, less the
     * viewport.
     *
     * ASKED, NOT MEASURED, and that is the fix rather than a refinement.
     *
     * getBoundsInLocal() reports the last layout, and rootMenu's minHeight is
     * BOUND to the scroller's height less four - so a page that has not been
     * laid out against its new content reports the viewport height and this
     * comes out at MINUS FOUR. The wheel then refused, every time, and the
     * previous attempt at this tried to cure it by forcing a layout first.
     * That did not work either: layout() is a no-op on a node the toolkit does
     * not currently consider dirty, so there was no guarantee the pass ever
     * reached rootMenu.
     *
     * prefHeight() needs no layout and no dirty flag. A VBox computes it from
     * its children on the spot, which is precisely the question being asked -
     * "is there more content here than fits" - and it is right on the first
     * frame a screen exists.
     *
     * It correlated with being at the top because that is when you have just
     * arrived, and arriving from a shorter screen is what leaves the stale
     * reading behind. Jerus: "there is still an issue where if its at the top
     * then it locks, and since you start at the top its locked."
     */
    private double pageSpan(javafx.scene.Node page) {

        javafx.geometry.Bounds view = menuScroller.getViewportBounds();
        if (view == null || view.getHeight() <= 0) return 0;

        /*
         * THE REAL HEIGHT FIRST, AND IF IT IS USABLE THAT IS THE ANSWER.
         *
         * The previous version took max(bounds, pref) every time, and the
         * arithmetic below divides a wheel notch BY the span - so an inflated
         * span makes every notch tiny. That is what the magnet was: at the top
         * the span came out far too large, each turn moved a hair, and it took
         * a dozen of them to get clear. Once the page had been laid out for
         * real the two agreed and it behaved. Jerus: "the top is a magnetic, if
         * you touch it, you can seperate but it requires force lol".
         *
         * The inflation comes from asking prefHeight() at a width that is not
         * the real one - a viewport reporting zero width wraps every line of
         * text to nothing and returns an enormous height - so the width is
         * guarded too.
         *
         * Pref is now only consulted in the one case it was ever needed for:
         * when the laid-out bounds claim there is nothing to scroll. That is
         * the stale reading that locked the wheel, and it is the only time a
         * second opinion is worth having.
         */
        double span = page.getBoundsInLocal().getHeight() - view.getHeight();
        if (span > 1) return span;

        if (page instanceof javafx.scene.layout.Region region) {
            double width = view.getWidth() > 0 ? view.getWidth() : menuScroller.getWidth();
            if (width > 0) return region.prefHeight(width) - view.getHeight();
        }
        return span;
    }

    /**
     * A wheel turn nothing else wanted, spent on the page.
     *
     * The arithmetic is the one a ScrollPane does for itself: vvalue is a
     * fraction of the distance the content can travel, so a wheel notch is
     * worth its pixels divided by that distance. Doing it by hand rather than
     * forwarding the event is deliberate - a copied event re-enters the same
     * bubble and comes straight back here.
     */
    private void wheelToPage(javafx.scene.input.ScrollEvent wheel) {

        if (menuScroller == null || wheel.getDeltaY() == 0) return;

        javafx.scene.Node page = menuScroller.getContent();
        if (page == null) return;

        double span = pageSpan(page);

        if (span <= 1) return;   // genuinely nothing to scroll; leave the event alone

        scrollPageBy(wheel.getDeltaY(), span);
        wheel.consume();
    }

    /**
     * Move the page by one wheel event's worth, with a floor under the FIRST
     * event of a gesture and the rest taken as they come.
     *
     * The floor used to go under every event, and that is what made the build
     * tab "way too fast" (Jerus, 2026-09-18): a wheel or a touchpad that sends
     * its notch as a burst of a dozen small, accelerating deltas had each of
     * the dozen raised to forty-eight pixels, so one gesture travelled twice
     * what it asked for. The build tab is the one screen whose content sits
     * straight in the page rather than inside a scrolled() column, so it was
     * the one screen this arithmetic ran on; every other tab was scrolling its
     * inner ScrollPane with the platform's own deltas, and the two speeds
     * disagreed. The magnet at the top was only ever the first event or two
     * being tiny, so the floor is applied to the first event after a pause and
     * to nothing else: the page moves the moment the wheel is touched, and a
     * gesture then travels exactly the distance the device sent.
     *
     * @param deltaY the event's own distance, in pixels; positive is upward
     * @param span   how far the page can travel, in pixels
     */
    private void scrollPageBy(double deltaY, double span) {
        if (span <= 1 || deltaY == 0) return;
        long now = System.nanoTime();
        boolean firstOfGesture = now - lastWheelNanos > WHEEL_GESTURE_GAP_NANOS;
        lastWheelNanos = now;
        double pixels = firstOfGesture
                ? Math.signum(deltaY) * Math.max(WHEEL_STEP, Math.abs(deltaY))
                : deltaY;
        double at = menuScroller.getVvalue() - pixels / span;
        menuScroller.setVvalue(Math.max(0, Math.min(1, at)));
    }

    /** A wheel event this long after the last one starts a new gesture; a burst is closer than this. */
    private static final long WHEEL_GESTURE_GAP_NANOS = 150_000_000L;

    /** When the last wheel event moved the page; see scrollPageBy. */
    private long lastWheelNanos;

    /* =====================================================================
       THE HEADER (0.7.21)

       Jerus, on the play-through of 0.7.19, asked for GDP always in view, and
       the plan he agreed (the project's playing-0-7-19-ui-notes.md, section
       6) put five or six headline numbers across the top - each with this
       month's value, its change and a ten-year sparkline - and docked the
       clock beside them. The mockups drew it (section 7, Main.dc.html) and
       he said "that is damn pretty, go for it".

       So the date bar is a header: the clock at the left - the play button,
       the date, the month and the speed - then six tiles, then the credit
       rating and the inbox. It replaces three things: the date bar, the
       floating time controls at the stage's foot, and the net-income dome,
       which is the TREASURY tile's change line now. With nothing floating
       over the foot of the stage, the room every page kept for it
       (PAGE_FOOT) is a margin again.

       EVERY FIGURE IS A GETTER, EVERY LINE A SERIES THE MODEL KEEPS. A
       tile's sparkline is the line City History draws for it -
       HistoryScreen.historyValues(), the history's own series and YearBook's
       definitions - over the last SPARK_MONTHS, or everything recorded if
       less; the header recomputes nothing. A click opens City History with
       that line picked, its big chart on the same ten years (openHistory(),
       since 0.7.23's chart rebuild).

       WHERE THE DATE BAR'S CONTENTS WENT:
         the date and the month number    the clock
         the treasury and its trend       TREASURY and its change line
         Pop and its four flow chips      POPULATION and the month's net; born, died, in, out on its tooltip
         the outbreak and "% sick" chips  POPULATION's tooltip
         prices: the index and the rate   INFLATION; the index on its tooltip, the bands behind its (i)
         the rate both ways, and parity   RATE: the dollar rate as its change line, both ways and parity behind its (i)
         central, bank and city rates     RATE: the central bank's is its figure, all three behind its (i)
         the rating                       the chip at the right
       and from the stage's foot: the play button and the speed slider (the
       clock's button and "< 10x >"), the dome's earned and banked (TREASURY's
       change line, and its (i)), why the clock stopped itself (a line under
       the date), and the year's twelve pips, which went nowhere: the date is
       beside the button now, and the month's figure pops as a month lands.

       IT FITS A 1366-PIXEL WINDOW. The tiles share what the clock and the
       right-hand block leave, equally, except that a tile never gets less
       than its figure needs (HeadlineRow): a number is never cut. A tile with
       less room than its figure, a gap and SPARK_WIDTH shrinks its sparkline,
       and draws none under SPARK_MIN; the label and the change line end in an
       ellipsis before the figure is touched.

       ...AND SINCE 0.7.24 NOTHING ENDS IN AN ELLIPSIS. Jerus's window is
       1,389 points wide, and the walkthrough read "TREA...", "OUT OF W...",
       "RATE ·..." and "5,119 this m...". TREASURY is its own block by the
       clock (THE MONEY BLOCK) and the five tiles keep their label and a
       sparkline on one row, the figure under them and the change line
       across the tile: a tile's least width is its label, its figure or its
       change line in its shortest wording, whichever is widest, and a change
       line too long for its tile is shown in a shorter wording - each tile's
       wordings are written beside it in headlines(), longest first - rather
       than cut. Laid out for 1,280, 1,389 and 1,920 px: the sparklines come
       and go with the room, as they always did; nothing else does.
       ===================================================================== */

    /*
     * WHERE THE HEADER'S PRICES AND RATE CHANGE COLOUR (2026-09-21; verdicts
     * since 0.7.21). Inflation reads good near the player's target, amber
     * when it drifts and red when it has gone; the currency's rate is
     * secondary grey when nothing needs saying, amber when it is drifting and
     * red when it has gone.
     */

    /** Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. */
    static final double STRIP_INFLATION_QUIET = .03;

    /** Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. Jerus's number (0.7.15, round 3: "Red at target + 5 points"), in place of red above 10% flat. */
    static final double STRIP_INFLATION_OVER_TARGET = .05;

    /** Deflation past this reads red, whatever the target: prices collapsing. The lower half of the 10% alarm the strip had until 0.7.15; Jerus ruled on the upper half only. */
    static final double STRIP_DEFLATION_ALARM = .10;

    /*
     * The rate line's colour has no thresholds of its own since 0.7.35: it
     * takes ForeignAccounts.parityLevel(), the one parity rule the Trade tab
     * and the drawer's THE CURRENCY read too (PARITY_WATCH, PARITY_FAR). It
     * was grey within 5% of parity or stronger by any amount, amber weaker
     * past 5% and red past 25% - three verdicts for one rate (the Trade
     * spec's B9).
     */

    /** How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five tiles fit a 1,280 window; it was 182 at 19). */
    static final double DATE_WIDTH = 164;

    /** The date's size in the clock (0.7.24; it was 19). */
    static final double DATE_SIZE = 17;

    /** How many months a tile's sparkline draws: ten years, or everything recorded if less. */
    static final int SPARK_MONTHS = 120;

    /** A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). */
    static final double SPARK_WIDTH = 72;
    /** ...and its height, the label's row (0.7.24; it was 30). */
    static final double SPARK_HEIGHT = 16;

    /** Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. */
    static final double SPARK_MIN = 30;

    /** The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). */
    static final double TILE_FIGURE = 15;
    /** The size of a tile's label, and of the money block's. */
    static final double TILE_LABEL = 10.5;
    /** The size of a tile's change line (0.7.24; it was 11). */
    static final double TILE_CHANGE = 10.5;

    /** The month's figure in the clock, kept so it pops when a month lands. */
    private Label monthFigure;

    /** The speed's reading between its two arrows, kept so a click changes it in place. */
    private Label speedReading;

    /** The month the clock last showed, so the month's figure pops only when it moves. */
    private int clockAt = -1;

    /** Where the inbox's envelope is drawn in the header, so a notice read elsewhere can redraw its count. */
    private StackPane inboxHolder;

    /**
     * The header, rebuilt: the clock, the money block, the five tiles, the
     * "Needs you" chip, the rating and the inbox.
     * Every redraw calls it, so it reads the month that has just landed.
     */
    private void refreshHeader() {
        header.getChildren().clear();
        boolean inCity = !isGameMenu(currentScreen);

        HeadlineRow tiles = new HeadlineRow(Palette.GAP);
        for (Headline h : headlines()) tiles.getChildren().add(headlineTile(h));
        HBox.setHgrow(tiles, Priority.ALWAYS);

        HBox clock = clockBlock(inCity);
        // TREASURY left the tile row in 0.7.24: its own block, by the clock (THE MONEY BLOCK).
        Region money = moneyBlock();
        HBox right = ratingAndInbox(inCity);
        header.getChildren().addAll(clock, money, tiles, right);
    }

    /**
     * One tile, as the header draws it.
     *
     * @param area   its area's colour: the label and the sparkline
     * @param tip    what the tooltip says, short; the long text is `more`
     * @param more   the (i)'s popover, or null for a tile with none
     * @param keys   the lines City History draws when the tile is clicked
     */
    private record Headline(String label, String area, String value, String valueTone,
                            String[] change, String changeTone, double[] series,
                            String tip, String more, String[] keys) { }

    /**
     * A change line, longest wording first: the tile shows the longest that
     * fits its width (HeadlineTile), so a line is shortened and never cut
     * (0.7.24). The shortenings are written beside each tile's line below.
     */
    private static String[] words(String... longestFirst) { return longestFirst; }

    /** The five, in the mockups' order: people, output, prices, work, the price of money - TREASURY is its own block since 0.7.24 (moneyBlock()). */
    private List<Headline> headlines() {
        List<Headline> out = new ArrayList<>();
        HistorySave h = game.getHistorySave();
        String click = "Click for its line in City History.";

        /* ---------------- POPULATION, and the month's net ---------------- */
        PopulationManager pm = game.getPopulationManager();
        PopulationCohorts pyramid = game.getCohorts();
        Migration flows = game.getMigration();
        Health illness = game.getHealth();
        long population = pm.getPopulation();
        double born = pyramid.getLastBirths(), died = pyramid.getLastDeaths();
        double in = flows.getLastArrivals(), left = flows.getLastDepartures();
        double net = in - left + born - died;
        long whole = Math.round(Math.abs(net));
        String[] netLine;
        String netTone;
        if (whole == 0) {
            netLine = words("no change this month", "no change");
            netTone = Palette.TEXT_MUTED;
        } else if (net > 0) {
            netLine = words("▲ " + formatter.format(whole) + " this month", "▲ " + formatter.format(whole) + " this mo",
                    "▲ " + formatter.format(whole));
            netTone = Palette.GOOD;
        } else {
            netLine = words("▼ " + formatter.format(whole) + " this month", "▼ " + formatter.format(whole) + " this mo",
                    "▼ " + formatter.format(whole));
            // The left panel's PEOPLE watch: amber when the city shrinks, red
            // past half a percent a month (SummaryScreen.citySymptoms()).
            netTone = net < -Math.max(1, population * .005) ? Palette.BAD : Palette.WARN;
        }
        StringBuilder peopleTip = new StringBuilder(String.format(
                "%s people live in the city.%nThis month: %s born, %s died, %s moved in, %s moved out.",
                formatter.format(population), flowText(born), flowText(died),
                flowText(in), flowText(left)));
        if (illness.isOutbreak()) {
            peopleTip.append(String.format("%nAn outbreak is running - month %d of it.",
                    Math.max(1, game.getMonth() - illness.getOutbreakStarted() + 1)));
        }
        if (illness.getSickRate() > Health.WELL_SERVED_RATE + 1e-9) {
            peopleTip.append(String.format("%n%.0f%% of the city is off sick.", illness.getSickRate() * 100));
        }
        peopleTip.append("\n").append(click);
        out.add(new Headline("POPULATION", Palette.PEOPLE, formatter.format(population), Palette.TEXT_HEAD,
                netLine, netTone, historyScreen.historyValues(h, "population"),
                peopleTip.toString(), null, new String[] {"population"}));

        /* ------------- GDP, annualised from month 1, and real growth ------------- */
        NationalAccounts na = game.getEconomyManager().getNationalAccounts();
        int recorded = na.getMonthsRecorded();
        double[] growth = YearBook.realGrowth(h);
        double grew = growth.length == 0 ? Double.NaN : growth[growth.length - 1];
        String[] gdpLine;
        String gdpTone = Palette.TEXT_MUTED;
        if (recorded < 12) {
            gdpLine = words("annualised · first year", "first year");
        } else if (Double.isNaN(grew)) {
            // A year recorded but not two: growth needs a year to compare with.
            gdpLine = words("growth from month 24", "growth from m24");
        } else if (Math.abs(grew) < .0005) {
            gdpLine = words("flat, real, 12 mo", "flat, real", "flat");
        } else {
            String arrow = grew > 0 ? "▲" : "▼";
            gdpLine = words(String.format("%s %.1f%% real, 12 mo", arrow, Math.abs(grew) * 100),
                    String.format("%s %.1f%% real", arrow, Math.abs(grew) * 100),
                    String.format("%s %.1f%%", arrow, Math.abs(grew) * 100));
            gdpTone = grew > 0 ? Palette.GOOD : Palette.BAD;
        }
        out.add(new Headline("GDP", Palette.BUSINESS, money(annualGdp(na)) + " / yr", Palette.TEXT_HEAD,
                gdpLine, gdpTone, historyScreen.historyValues(h, "gdp"),
                String.format("What the city made in a year: the last twelve months of output%s.%n"
                        + "Under it, real growth: the last twelve months against the twelve before,"
                        + " with prices taken out%s.%nThe sparkline is the month's output. %s",
                        recorded > 0 && recorded < 12
                                ? ", scaled up from the " + recorded + " month" + (recorded == 1 ? "" : "s")
                                  + " recorded so far, since the city has not lived a year"
                                : "",
                        Double.isNaN(grew) ? " - it needs two years of output" : "", click),
                null, new String[] {"gdp"}));

        /* ---------------- INFLATION, against the player's target ---------------- */
        PriceIndex prices = game.getPriceIndex();
        double target = game.getDebtManager().getInflationTarget();
        String inflValue, inflTone;
        String[] inflLine;
        if (prices.hasRate()) {
            double inflation = prices.inflation();
            double off = inflation - target;
            inflValue = String.format("%.1f%% / yr", unsigned0(inflation * 100, 1));
            String side = off > 0 ? "over" : "under";
            inflLine = Math.abs(off) < .0005
                    ? words("on the " + DebtManager.targetWords(target) + " target", "on target")
                    : words(String.format("%.1f points %s the target", Math.abs(off) * 100, side),
                            String.format("%.1f pts %s target", Math.abs(off) * 100, side),
                            String.format("%.1f %s target", Math.abs(off) * 100, side),
                            String.format("%s%.1f pts", off > 0 ? "+" : "−", Math.abs(off) * 100));
            inflTone = inflationColour(inflation, target);
        } else {
            /*
             * WHEN THE FIRST RATE COMES (0.7.20; Jerus kept the countdown for
             * 0.7.21). The basket is fixed only after PriceIndex.SETTLING_MONTHS
             * of real shopping, and the twelve-month rate follows a year of
             * readings after that; until then every screen reads it as zero,
             * so the tile shows no figure of its own. Exact once the basket is
             * fixed; before that the least it can be, since a month with no
             * shopping does not count towards it.
             */
            inflValue = (prices.isBased() ? "rate in " : "rate in ~") + prices.monthsUntilRate() + " mo";
            inflLine = words("target " + DebtManager.targetWords(target));
            inflTone = Palette.TEXT_MUTED;
        }
        /*
         * THE ANCHOR ON THE LINE (0.7.45; the UI spec's D1). The figure takes
         * the verdict the line carried - the year's rate against the target,
         * Jerus's 3 and 5 points - and the line says what people expect and
         * how far they believe the bank, in NEEDS YOU's PRICES colour: the
         * figure is where prices were, the line where they are going, since
         * expected inflation is what the wages and every money constant move
         * on next month. "N points over the target" is in the tooltip.
         */
        out.add(new Headline("INFLATION", Palette.MONEY, inflValue,
                prices.hasRate() ? inflTone : Palette.TEXT_HEAD, anchorLine(game), anchorTone(game),
                historyScreen.historyValues(h, "priceIndex"),
                inflationTip(game, prices.hasRate() ? inflLine[0] : null),
                inflationMore(game),
                new String[] {"inflation", "priceIndex"}));

        /* -------------- OUT OF WORK, with a shortage read as watch -------------- */
        double jobless = pm.getUnemploymentRate();
        long unfilled = 0;
        for (long v : pm.getJobVacancy()) unfilled += Math.max(0, v);
        String workTone = PeopleScreen.outOfWorkTone(jobless);
        String[] workLine = jobless > PeopleScreen.OUT_OF_WORK_FAR ? words("far too many idle", "too many idle", "idle")
                : jobless > PeopleScreen.OUT_OF_WORK_HIGH ? words("high")
                : jobless < PeopleScreen.OUT_OF_WORK_SHORT
                        ? (unfilled > 0 ? words("jobs going unfilled", "jobs unfilled", "unfilled")
                                        : words("nobody spare", "none spare"))
                : words("healthy slack", "slack");
        out.add(new Headline("OUT OF WORK", Palette.PEOPLE,
                String.format("%.1f%%", unsigned0(jobless * 100, 1)), workTone, workLine, workTone,
                historyScreen.historyValues(h, "unemployment"),
                String.format("%s people out of work%s.%nAmber under %.0f%% - nobody spare, so new jobs"
                        + " go unfilled - and over %.0f%%; red over %.0f%%.%n%s",
                        people(pm.getUnemployed()),
                        unfilled > 0 ? ", and " + formatter.format(unfilled) + " posts going unfilled" : "",
                        PeopleScreen.OUT_OF_WORK_SHORT * 100, PeopleScreen.OUT_OF_WORK_HIGH * 100,
                        PeopleScreen.OUT_OF_WORK_FAR * 100, click),
                null, new String[] {"unemployment"}));

        /* TREASURY is its own block by the clock since 0.7.24: see moneyBlock(). */

        /* --------------- RATE and the currency: the price of money --------------- */
        DebtManager market = game.getDebtManager();
        ForeignAccounts fx = game.getForeignAccounts();
        double rate = fx.getRate();
        String here = game.getCurrency().qualifiedSymbol();
        String perDollar = here + (rate < .1 ? fxRate(rate) : String.format("%,.2f", rate));
        out.add(new Headline("RATE · " + here, Palette.MONEY, pct2(market.getPolicyRate()), Palette.TEXT_HEAD,
                words(perDollar + " per " + Currency.FOREIGN_SYMBOL, perDollar + "/" + Currency.FOREIGN_SYMBOL),
                rateColour(fx), historyScreen.historyValues(h, "policyRate"),
                String.format("The central bank's policy rate - your dial on the Policy tab - and what a"
                        + " US dollar costs in %s.%n%s, and the city borrows at %s.%n"
                        + "Click for both lines in City History.",
                        game.getCurrency().plural(),
                        game.getBank().getBranches() <= 0 ? "There is no bank yet"
                                : game.getBank().isInsolvent() ? "The bank has failed"
                                : "The bank lends at " + pct2(game.getBank().lendingRate(market.getPolicyRate()))
                                        + " (its prime)",
                        pct2(market.getRate())),
                moneyWhy(market, fx, here),
                new String[] {"policyRate", "fxRate"}));
        return out;
    }

    /**
     * The money block's (i) - the TREASURY tile's until 0.7.24: the three
     * figures, EARNED, SURPLUS and BANKED (0.7.31's names, the same on the
     * Government tab), and why they differ. Jerus: "show how much was the
     * actual month change ... sometimes cause of land buybacks or sales it was
     * actually more or less." What the month EARNED (the tax take less the
     * running programmes, at today's tax rates) is not the budget's SURPLUS,
     * which adds land, buildings and the smaller lines (Game.getEarnedToBudget(),
     * each step by name - it named three of the eleven until 0.7.31, and the
     * fares were a step of their own until 0.7.49), and neither is what the treasury BANKED,
     * which counts its borrowing and the player's own moves too.
     */
    private String treasuryWhy(double income, double moved) {
        StringBuilder why = new StringBuilder(String.format(
                "EARNED %s: the month's taxes and fees less the running programmes - interest, pensions, EI,"
                + " the grants, care, schools, the police and transit - plus the utilities' net, read at today's tax"
                + " rates.", signedTight(income, false)));
        java.util.List<String> adds = new java.util.ArrayList<>();
        for (TreasuryJournal.Entry e : game.getEarnedToBudget()) {
            if (Math.abs(e.amount()) >= .5) adds.add(e.label().toLowerCase() + " " + signedTight(e.amount(), false));
        }
        double dials = game.getEarnedResidual();
        why.append(String.format("%n%nSURPLUS %s: the budget. %s.%s",
                signedTight(game.getEconomyManager().getNationalAccounts().getBalance(), false),
                adds.isEmpty() ? "It has nothing EARNED leaves out this month"
                        : "It adds what EARNED leaves out - " + String.join(", ", adds),
                Math.abs(dials) >= .5 ? String.format(" A dial moved since the last press moves EARNED and not"
                        + " the budget: %s.", signedTight(dials, false)) : ""));
        if (!Double.isNaN(moved)) {
            why.append(String.format("%n%nBANKED %s: what the balance actually did this month - your land,"
                    + " your buildings and your borrowing included. On top of the budget, paper issued raised"
                    + " %s - borrowed, not earned - and %s went back to lenders as principal, which shrinks a"
                    + " debt rather than buying anything.",
                    signedTight(moved, false), money(game.getTreasuryRaised()), money(game.getTreasuryRepaid())));
        }
        why.append(String.format("%n%nGovernment's Overview walks from one to the next, step by step."));
        return why.toString();
    }

    /* ---------------------------------------------------------------------
       THE MONEY BLOCK (0.7.24)

       Jerus, on 0.7.23: "the money one has is barely visible to see as well
       as ones income, it should really be visible (a T if trillion)" - the
       tile read "TREA..." and "+$1.5B thi...". He chose "Own block by the
       clock". TREASURY left the tile row: an icon, its label in the money
       colour, the cash in 28 px mono in the compact form (Money.money(), which
       has had the T since 0.7.20) - red only when overdrawn, as the tile was
       - and under it the month's income as "+$1.5B a month", green, red
       when it is negative and grey at nothing. A click opens Finances.

       THE LINE UNDER IT IS THE TILE'S LINE: Game.getIncome(), what the month
       EARNED (its "net income" until 0.7.31) - the tax take less the running
       programmes (interest, pensions, EI, the grants, care, schools and the
       police) plus the utilities' net, at today's tax rates. It is not the
       budget's SURPLUS, and not the change in the cash (Game.
       getTreasuryChange(), what Finances' TREASURY cell reads), which counts
       the city's buildings and land, its borrowing raised and repaid, and
       the rest of what the treasury did: in Jerus's city the tile said +$1.5B
       while Finances said the cash grew $2.3B. The figure is unchanged; the
       tooltip names it in a line, and the (i) had the two side by side
       (treasuryWhy()). Since 0.7.31 the line says what it is - "+$1.5B
       earned a month" - and the (i) names all three, EARNED, SURPLUS and
       BANKED, as the Government tab does.
       --------------------------------------------------------------------- */

    /** The cash at its own size, what the month EARNED under it (not how far the month moved the cash: see above), and a door to Finances. */
    private Region moneyBlock() {
        double cash = game.getCash();
        double income = game.getIncome();
        boolean banked = game.hasTreasuryMonth();
        double moved = banked ? game.getTreasuryChange() : Double.NaN;

        Region coin = iconSquare(Icons.COIN, Palette.MONEY, 36, 20);

        Label label = new Label("TREASURY");
        label.setStyle(Fonts.sansSemiBold() + " -fx-font-size: " + TILE_LABEL + "px; -fx-text-fill: " + Palette.MONEY + ";");
        label.setMinWidth(Region.USE_PREF_SIZE);
        HBox labelRow = new HBox(4, label, infoButton(treasuryWhy(income, moved), false));
        labelRow.setAlignment(Pos.CENTER_LEFT);

        Label figure = new Label(money(cash));
        figure.setStyle(Fonts.monoSemiBold() + " -fx-font-size: " + MONEY_FIGURE + "px;"
                + " -fx-text-fill: " + (cash < 0 ? Palette.BAD : Palette.TEXT_HEAD) + ";");
        figure.setMinWidth(Region.USE_PREF_SIZE);

        Label month = new Label(signedTight(income, false) + " earned a month");
        month.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 11.5px; -fx-text-fill: "
                + (Math.abs(income) < .5 ? Palette.TEXT_MUTED : income > 0 ? Palette.GOOD : Palette.BAD) + ";");
        month.setMinWidth(Region.USE_PREF_SIZE);

        // Packed close: the 28 px figure's own line height carries the air, and the
        // block stays inside the header's 64 px between its paddings.
        VBox words = new VBox(-4, labelRow, figure, month);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setMinWidth(Region.USE_PREF_SIZE);

        HBox block = new HBox(10, coin, words);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setMinWidth(Region.USE_PREF_SIZE);
        String rest = "-fx-padding: 1 14 1 10; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;";
        block.setStyle(rest + " -fx-border-color: " + Palette.EDGE + ";");
        block.setOnMouseEntered(e -> block.setStyle(rest + " -fx-border-color: " + Palette.MONEY + ";"));
        block.setOnMouseExited(e -> block.setStyle(rest + " -fx-border-color: " + Palette.EDGE + ";"));
        Tooltip tip = new Tooltip(String.format("The city's cash%s.%n"
                        + "Under it: what the month EARNED, at today's tax rates - not the budget's surplus, nor the change in the cash%s.%n"
                        + "Click for Finances.",
                cash < 0 ? " - overdrawn: the central bank advances it at the policy rate" : "",
                banked ? ", which was " + signedTight(moved, false) : ""));
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(block, tip);
        block.setOnMouseClicked(e -> {
            resetSection("finances");
            railJump = true;            // clearMenu: arriving, not redrawing
            financesScreen.showFinanceMenu();
        });
        return block;
    }

    /** The cash in the money block: the mockups' 28 px. */
    static final double MONEY_FIGURE = 28;

    // The icon in a tinted square beside a heading is Pieces.iconSquare() since 0.7.26.

    /**
     * The rate tile's (i): the price of money three ways (0.7.4) - Jerus: "the
     * bank rate, the central bank rate, both should be shown, as well as the
     * rate you borrow in" - and the currency both ways, against its parity.
     * Every figure a public getter, formatted here and not recomputed.
     */
    private String moneyWhy(DebtManager market, ForeignAccounts fx, String here) {
        Bank lender = game.getBank();
        double policy = market.getPolicyRate();
        boolean noBank = lender.getBranches() <= 0;
        boolean bankFailed = !noBank && lender.isInsolvent();
        double oneLocal = fx.toUsd(1);
        double rate = fx.getRate();
        return String.format(
                "Central bank %s - the policy rate: your dial, and the floor under every rate in the city.%n"
                + "Bank %s - its prime, what it lends a business at before that business's own spread.%n"
                + "City %s - what the treasury pays to borrow short: the dial plus what the city's own"
                + " debt costs it.%s%n%n"
                + "%s1 = %s%s, and %s. Parity - where a basket costs the same here and abroad - is"
                + " %s%.2f; the rate is %s.",
                pct2(policy),
                noBank ? "none yet" : bankFailed ? "failed" : pct2(lender.lendingRate(policy)),
                pct2(market.getRate()),
                noBank ? String.format("%nThere is no bank in this city yet: what it borrows is lent from"
                        + " outside it, priced as the central bank's window money.")
                : bankFailed ? String.format("%nThe bank has failed: it may lend nothing until it is"
                        + " recapitalised or earns its way back.") : "",
                Currency.FOREIGN_SYMBOL, here, rate < .1 ? fxRate(rate) : String.format("%,.2f", rate),
                oneLocal >= 1
                        ? here + "1 = " + Currency.FOREIGN_SYMBOL + String.format("%,.2f", oneLocal)
                        : oneLocal * 100 >= .01
                        ? here + "1 = " + Currency.FOREIGN_CENT_SYMBOL + String.format("%.2f", oneLocal * 100)
                        : Currency.FOREIGN_CENT_SYMBOL + "1 = " + here + String.format("%,.0f", 1 / (oneLocal * 100)),
                here, fx.getParity(),
                Math.abs(fx.deviationFromParity()) * 100 < .5 ? "at parity"
                        : String.format("%.0f%% %s than it", Math.abs(fx.deviationFromParity()) * 100,
                                fx.deviationFromParity() > 0 ? "weaker" : "stronger"));
    }

    /**
     * One tile (0.7.24, the mockups' shape): its label in its area's colour
     * and a small sparkline on the top row, the figure under them, and the
     * change line across the tile's whole width - in the longest of its
     * wordings that fits (HeadlineTile). No label, figure or change line is
     * ever cut: each is held at its own width, and the tile's least width is
     * the widest of them, its change line in its shortest wording.
     */
    private Region headlineTile(Headline t) {
        Label label = new Label(t.label());
        label.setStyle(Fonts.sansSemiBold() + " -fx-font-size: " + TILE_LABEL + "px;"
                + " -fx-text-fill: " + t.area() + ";");
        label.setMinWidth(Region.USE_PREF_SIZE);
        HBox labelRow = new HBox(4, label);
        labelRow.setAlignment(Pos.CENTER_LEFT);
        labelRow.setMinWidth(Region.USE_PREF_SIZE);
        if (t.more() != null) labelRow.getChildren().add(infoButton(t.more(), false));

        Label figure = new Label(t.value());
        figure.setStyle(Fonts.monoSemiBold() + " -fx-font-size: " + TILE_FIGURE + "px;"
                + " -fx-text-fill: " + t.valueTone() + ";");
        // The figure is the one thing a tile never cuts.
        figure.setMinWidth(Region.USE_PREF_SIZE);

        List<Label> changes = new ArrayList<>();
        for (String wording : t.change()) {
            Label change = new Label(wording);
            change.setStyle("-fx-font-size: " + TILE_CHANGE + "px; -fx-text-fill: " + t.changeTone() + ";");
            change.setMinWidth(Region.USE_PREF_SIZE);
            changes.add(change);
        }

        HeadlineTile tile = new HeadlineTile(labelRow, figure, changes,
                new Sparkline(t.series(), game.getHistorySave().getMonth(), t.area()));
        // 6 and 10 (0.7.24; 8 and 12 until then): the five, the money block and the clock in 1,280 px.
        String rest = "-fx-padding: 6 10 6 10; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;";
        tile.setStyle(rest + " -fx-border-color: " + Palette.EDGE + ";");
        tile.setOnMouseEntered(e -> tile.setStyle(rest + " -fx-border-color: " + t.area() + ";"));
        tile.setOnMouseExited(e -> tile.setStyle(rest + " -fx-border-color: " + Palette.EDGE + ";"));
        Tooltip tip = new Tooltip(t.tip());
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(tile, tip);
        tile.setOnMouseClicked(e -> openHistory(t.keys()));
        return tile;
    }

    /**
     * Opens City History with these lines picked: the tiles' click. The
     * page's picks are replaced by the tile's line (and its seed spent, so
     * the first visit's preset does not come back over them), every line
     * shown, and since 0.7.23 the big chart on its last ten years - the
     * sparkline the tile was showing, drawn large - at the top of the page.
     * Since 0.7.37 that is HistoryScreen.openOn(), the one door into History,
     * with the arrival marked here; on History itself it lands at the chart.
     */
    private void openHistory(String[] keys) {
        railJump = true;            // clearMenu: arriving, not redrawing - the page opens at its top
        historyScreen.openOn(keys);
    }

    /**
     * The tiles, side by side: equal shares of the row, except that a tile
     * never gets less than its least width - its figure until 0.7.24, the
     * widest of its label, its figure and its shortest change line since
     * (HeadlineTile) - and the rest share what is left. An HBox gives its
     * spare room equally from each child's preferred width, which leaves
     * tiles of five different widths; this is the mockups' "flex: 1 1 0"
     * with a floor.
     */
    private static final class HeadlineRow extends javafx.scene.layout.Pane {
        private final double gap;
        HeadlineRow(double gap) { this.gap = gap; }

        @Override protected double computeMinWidth(double height) {
            double sum = 0;
            for (javafx.scene.Node n : getManagedChildren()) sum += n.minWidth(-1);
            return snappedLeftInset() + sum + gap * Math.max(0, getManagedChildren().size() - 1) + snappedRightInset();
        }

        @Override protected double computePrefWidth(double height) {
            double sum = 0;
            for (javafx.scene.Node n : getManagedChildren()) sum += n.prefWidth(-1);
            return snappedLeftInset() + sum + gap * Math.max(0, getManagedChildren().size() - 1) + snappedRightInset();
        }

        @Override protected double computePrefHeight(double width) {
            double tall = 0;
            for (javafx.scene.Node n : getManagedChildren()) tall = Math.max(tall, n.prefHeight(-1));
            return snappedTopInset() + tall + snappedBottomInset();
        }

        @Override protected void layoutChildren() {
            List<javafx.scene.Node> tiles = getManagedChildren();
            int n = tiles.size();
            if (n == 0) return;
            double x = snappedLeftInset(), y = snappedTopInset();
            double room = getWidth() - x - snappedRightInset() - gap * (n - 1);
            double h = getHeight() - y - snappedBottomInset();
            // Floors first: any tile whose least width is more than an equal
            // share takes it, and the others share the rest.
            double[] width = new double[n];
            boolean[] fixed = new boolean[n];
            boolean changed = true;
            while (changed) {
                changed = false;
                double left = room;
                int free = 0;
                for (int i = 0; i < n; i++) { if (fixed[i]) left -= width[i]; else free++; }
                double share = free == 0 ? 0 : left / free;
                for (int i = 0; i < n; i++) {
                    if (fixed[i]) continue;
                    double floor = tiles.get(i).minWidth(-1);
                    if (floor > share) { width[i] = floor; fixed[i] = true; changed = true; }
                    else width[i] = share;
                }
            }
            for (int i = 0; i < n; i++) {
                tiles.get(i).resizeRelocate(snapPositionX(x), y, snapSizeX(width[i]), h);
                x += width[i] + gap;
            }
        }
    }

    /**
     * One tile's inside (0.7.24): the label at the top left and the
     * sparkline at the top right in what the label leaves, up to SPARK_WIDTH
     * - none at all under SPARK_MIN; the figure under them; and the change
     * line across the whole width, the longest of its wordings that fits.
     * The least width is the widest of the label, the figure and the
     * shortest wording, so none of the three is ever cut; HeadlineRow gives
     * every tile at least that.
     */
    private static final class HeadlineTile extends Region {
        private final HBox labelRow;
        private final Label figure;
        private final List<Label> changes;
        private final Sparkline spark;

        HeadlineTile(HBox labelRow, Label figure, List<Label> changes, Sparkline spark) {
            this.labelRow = labelRow;
            this.figure = figure;
            this.changes = changes;
            this.spark = spark;
            getChildren().addAll(labelRow, spark, figure);
            getChildren().addAll(changes);
        }

        private double shortest() {
            double least = 0;
            for (int i = 0; i < changes.size(); i++) {
                double wide = changes.get(i).prefWidth(-1);
                least = i == 0 ? wide : Math.min(least, wide);
            }
            return least;
        }

        private double changeHeight() {
            return changes.isEmpty() ? 0 : changes.get(0).prefHeight(-1);
        }

        @Override protected double computeMinWidth(double height) {
            return snappedLeftInset() + Math.max(labelRow.prefWidth(-1), Math.max(figure.prefWidth(-1), shortest()))
                    + snappedRightInset();
        }

        @Override protected double computePrefWidth(double height) {
            double longest = changes.isEmpty() ? 0 : changes.get(0).prefWidth(-1);
            return snappedLeftInset() + Math.max(labelRow.prefWidth(-1) + Palette.GAP + SPARK_WIDTH,
                    Math.max(figure.prefWidth(-1), longest)) + snappedRightInset();
        }

        @Override protected double computeMinHeight(double width) {
            return computePrefHeight(width);
        }

        @Override protected double computePrefHeight(double width) {
            return snappedTopInset() + Math.max(labelRow.prefHeight(-1), SPARK_HEIGHT) + figure.prefHeight(-1)
                    + changeHeight() + snappedBottomInset();
        }

        @Override protected void layoutChildren() {
            double x = snappedLeftInset(), y = snappedTopInset();
            double w = getWidth() - x - snappedRightInset();
            double top = Math.max(labelRow.prefHeight(-1), SPARK_HEIGHT);
            double labelWide = labelRow.prefWidth(-1);
            labelRow.resizeRelocate(x, y + (top - labelRow.prefHeight(-1)) / 2, labelWide, labelRow.prefHeight(-1));
            double sparkWide = Math.min(SPARK_WIDTH, w - labelWide - Palette.GAP);
            boolean drawn = sparkWide >= SPARK_MIN;
            spark.setVisible(drawn);
            if (drawn) {
                spark.resizeRelocate(snapPositionX(x + w - sparkWide), snapPositionY(y + (top - SPARK_HEIGHT) / 2),
                        snapSizeX(sparkWide), SPARK_HEIGHT);
            }
            double fh = figure.prefHeight(-1);
            figure.resizeRelocate(x, y + top, Math.max(w, figure.prefWidth(-1)), fh);
            // The longest wording that fits; the shortest if none does (the tile's least width is that).
            Label shown = null;
            for (Label c : changes) {
                if (c.prefWidth(-1) <= w + .5) { shown = c; break; }
            }
            if (shown == null && !changes.isEmpty()) {
                shown = changes.get(0);
                for (Label c : changes) if (c.prefWidth(-1) < shown.prefWidth(-1)) shown = c;
            }
            for (Label c : changes) {
                c.setVisible(c == shown);
                if (c == shown) c.resizeRelocate(x, y + top + fh, Math.max(w, c.prefWidth(-1)), c.prefHeight(-1));
            }
        }
    }

    /**
     * A tile's sparkline: the last SPARK_MONTHS of its series, a month a
     * point, the months recorded as nothing (NaN) left out; a faint fill
     * under the line and a dot on the latest month, in its area's colour.
     * Drawn again only when its size changes. Since 0.7.23 each point sits
     * at its month - a stretch with nothing recorded keeps its width, the
     * line crossing it straight, rather than being squeezed out - and a short
     * mark at the foot is each January: the ten years, counted.
     */
    static final class Sparkline extends Region {
        private final double[] points;
        private final int[] at;
        private final javafx.scene.paint.Color colour;
        private final javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas();
        private double drawnW = -1, drawnH = -1;
        private int firstMonth, lastMonth;

        Sparkline(double[] series, List<Integer> months, String colour) {
            List<Double> finite = new ArrayList<>();
            List<Integer> when = new ArrayList<>();
            if (series != null) {
                int from = Math.max(0, series.length - SPARK_MONTHS);
                boolean dated = months != null && months.size() == series.length;
                firstMonth = dated && series.length > 0 ? months.get(from) : from;
                lastMonth = dated && series.length > 0 ? months.get(series.length - 1) : series.length - 1;
                for (int i = from; i < series.length; i++) {
                    if (!Double.isFinite(series[i])) continue;
                    finite.add(series[i]);
                    when.add(dated ? months.get(i) : i);
                }
            }
            this.points = new double[finite.size()];
            this.at = new int[finite.size()];
            for (int i = 0; i < points.length; i++) { points[i] = finite.get(i); at[i] = when.get(i); }
            this.colour = javafx.scene.paint.Color.web(colour);
            getChildren().add(canvas);
            setMinSize(0, 0);
            setPrefSize(SPARK_WIDTH, SPARK_HEIGHT);
            setMaxSize(SPARK_WIDTH, SPARK_HEIGHT);
            setMouseTransparent(true);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            if (w == drawnW && h == drawnH) return;
            drawnW = w;
            drawnH = h;
            canvas.setWidth(w);
            canvas.setHeight(h);
            javafx.scene.canvas.GraphicsContext g = canvas.getGraphicsContext2D();
            g.clearRect(0, 0, w, h);
            int n = points.length;
            if (n < 2 || w < 4) return;
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (double v : points) { lo = Math.min(lo, v); hi = Math.max(hi, v); }
            double span = hi - lo;
            double[] xs = new double[n], ys = new double[n];
            double months = Math.max(1, lastMonth - firstMonth);
            for (int i = 0; i < n; i++) {
                xs[i] = 1 + (w - 2) * (at[i] - firstMonth) / months;
                ys[i] = span <= 0 ? h / 2 : 2 + (h - 4) - (h - 4) * (points[i] - lo) / span;
            }
            // Each January, a short mark at the foot (0.7.23).
            g.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_3, .6));
            g.setLineWidth(1);
            for (int m = firstMonth; m <= lastMonth; m++) {
                if (m < 1 || CityCalendar.monthOfYear(m) != 1) continue;
                double x = Math.floor(1 + (w - 2) * (m - firstMonth) / months) + .5;
                g.strokeLine(x, h - 3, x, h);
            }
            g.setFill(colour.deriveColor(0, 1, 1, .12));
            g.beginPath();
            g.moveTo(xs[0], ys[0]);
            for (int i = 1; i < n; i++) g.lineTo(xs[i], ys[i]);
            g.lineTo(w - 1, h);
            g.lineTo(1, h);
            g.closePath();
            g.fill();
            g.setStroke(colour);
            g.setLineWidth(1.6);
            g.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            g.beginPath();
            g.moveTo(xs[0], ys[0]);
            for (int i = 1; i < n; i++) g.lineTo(xs[i], ys[i]);
            g.stroke();
            g.setFill(colour);
            g.fillOval(xs[n - 1] - 2.4, ys[n - 1] - 2.4, 4.8, 4.8);
        }
    }

    /**
     * The clock, docked at the header's left (0.7.21): the play button, the
     * date, the month's figure and the speed - and why the clock stopped
     * itself, when it did. The button and the speed are there only in the
     * city; on the menus there is no clock to run.
     */
    private HBox clockBlock(boolean inCity) {
        int month = game.getMonth();

        javafx.scene.shape.SVGPath glyph = new javafx.scene.shape.SVGPath();
        glyph.setContent(clockRunning ? "M8 5h3v14H8z M13 5h3v14h-3z" : "M8 5v14l11-7z");
        glyph.setFill(javafx.scene.paint.Color.web(Palette.ON_FILL));
        Button play = new Button();
        play.setGraphic(glyph);
        play.setMinSize(44, 44);
        play.setPrefSize(44, 44);
        play.setMaxSize(44, 44);
        play.setPadding(javafx.geometry.Insets.EMPTY);
        play.setStyle("-fx-background-color: " + Palette.GOOD + "; -fx-background-radius: 22;"
                + " -fx-border-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        Tooltip playTip = new Tooltip(clockRunning ? "Pause  (Space)" : "Run the clock  (Space)");
        playTip.setShowDelay(Duration.millis(250));
        play.setTooltip(playTip);
        play.setOnAction(e -> {
            setClockRunning(!clockRunning);
            redrawScreen.run();
        });
        play.setVisible(inCity);

        /*
         * THE DAY, which is the only thing in the header that moves between
         * months. Kept in a field because the clock repaints it every frame and
         * must not rebuild the screen to do it - see paintDay(). Held at
         * DATE_WIDTH so the tiles beside it do not shift as the day changes.
         */
        Label date = new Label(CityCalendar.formatDay(month, monthProgress));
        date.setStyle(Fonts.sansSemiBold() + " -fx-font-size: " + DATE_SIZE + "px; -fx-text-fill: " + Palette.TEXT_MAX + ";");
        date.setMinWidth(DATE_WIDTH);
        date.setPrefWidth(DATE_WIDTH);
        dayLabel = date;

        /*
         * THE MONTH NUMBER STAYS, under the date rather than instead of it.
         * Every save, log entry, bond maturity and report in this game is
         * keyed to the integer month, so a player cross-referencing anything -
         * "the demolition log says 14 months ago", "this bond matures in month
         * 340" - needs the number the game actually counts in.
         */
        monthFigure = new Label("month " + formatter.format(month));
        monthFigure.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");

        speedReading = new Label(speedLabel(speedIndex));
        speedReading.setMinWidth(34);
        speedReading.setAlignment(Pos.CENTER);
        speedReading.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        HBox speed = new HBox(0, speedArrow("‹", -1), speedReading, speedArrow("›", 1));
        speed.setAlignment(Pos.CENTER_LEFT);
        Tooltip.install(speed, new Tooltip("How fast a month passes: " + (int) SECONDS_PER_MONTH
                + " seconds a month at 1×. ← and → change it too."));
        speed.setVisible(inCity);

        HBox second = new HBox(8, monthFigure, speed);
        second.setAlignment(Pos.CENTER_LEFT);
        VBox words = new VBox(0, date, second);
        words.setAlignment(Pos.CENTER_LEFT);

        /*
         * AND WHY IT STOPPED, when it stopped itself. A clock that halts with
         * no explanation is a bug as far as the player is concerned - they did
         * not press anything and time stopped. One line, under the date,
         * beside the button they are about to press to start it again.
         */
        if (inCity && pausedBecause != null && !clockRunning) {
            Label why = new Label(pausedBecause);
            why.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.WARN + ";");
            why.setMaxWidth(DATE_WIDTH + 40);
            Tooltip.install(why, new Tooltip("The clock stopped itself: " + pausedBecause
                    + "\nSettings, \"Stop for anything important\", turns this off."));
            words.getChildren().add(why);
        }

        HBox clock = new HBox(12, play, words);
        clock.setAlignment(Pos.CENTER_LEFT);
        clock.setMinWidth(Region.USE_PREF_SIZE);
        clock.setStyle("-fx-padding: 0 16 0 0; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-border-width: 0 1 0 0;");

        // The month landing, under the date: a quarter second of "that
        // happened" where the pips used to pop (TWELVE PIPS, below).
        if (clockAt >= 0 && clockAt != month) popPip(monthFigure);
        clockAt = month;
        return clock;
    }

    /** One of the speed's two arrows: a rung down or up the ladder, the reading changed in place. */
    private Label speedArrow(String glyph, int step) {
        Label arrow = new Label(glyph);
        String rest = "-fx-font-size: 14px; -fx-padding: 0 4 0 4; -fx-cursor: hand; -fx-text-fill: ";
        arrow.setStyle(rest + Palette.TEXT_LABEL + ";");
        arrow.setOnMouseEntered(e -> arrow.setStyle(rest + Palette.TEXT_HEAD + ";"));
        arrow.setOnMouseExited(e -> arrow.setStyle(rest + Palette.TEXT_LABEL + ";"));
        arrow.setOnMouseClicked(e -> {
            int pick = Math.max(0, Math.min(SPEEDS.length - 1, speedIndex + step));
            if (pick == speedIndex) return;
            speedIndex = pick;
            if (speedReading != null) speedReading.setText(speedLabel(pick));
        });
        return arrow;
    }

    /** The "Needs you" chip, the credit rating's chip and the inbox's envelope, at the header's right. */
    private HBox ratingAndInbox(boolean inCity) {
        // The rating, which the rate curve already knew; a letter is how a
        // borrower actually experiences its own credit.
        String rating = game.getCreditRating();
        Label chip = new Label(rating);
        chip.setStyle(Fonts.monoSemiBold() + " -fx-font-size: 12px; -fx-text-fill: " + Palette.ON_FILL + ";"
                + " -fx-background-color: " + ratingColour(rating) + "; -fx-background-radius: 4;"
                + " -fx-padding: 3 6 3 6;");
        chip.setMinWidth(Region.USE_PREF_SIZE);
        Tooltip.install(chip, new Tooltip("The city's credit rating, " + rating
                + ": what lenders make of its debt, and part of what it pays to borrow."));

        inboxHolder = new StackPane();
        refreshInboxButton();
        inboxHolder.setVisible(inCity);

        // "Needs you" and its count by the rating and the envelope since 0.7.24: the drawer's door.
        Region needs = needsChip();
        needs.setVisible(inCity);

        HBox box = new HBox(10, needs, chip, inboxHolder);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(Region.USE_PREF_SIZE);
        box.setStyle("-fx-padding: 0 0 0 14; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-border-width: 0 0 0 1;");
        return box;
    }

    /** A rating's verdict: investment grade is good, BB and B a watch, below that bad. */
    static String ratingColour(String rating) {
        return switch (rating) {
            case "AAA", "AA", "A", "BBB" -> Palette.GOOD;
            case "BB", "B"               -> Palette.WARN;
            default                      -> Palette.BAD;
        };
    }

    /* ----------------- THE INFLATION TILE'S WORDS (0.7.45), pure for the probe ----------------- */

    /**
     * The INFLATION tile's line, longest first: what people expect inflation
     * to be and how far they believe the bank - "expect 2.2% · trust 82%",
     * "exp 2.2% · 82%", "exp 2.2%" - and before the basket is based, when
     * everyone expects the target, "expect 2.0% (the target) · trust 80%".
     */
    static String[] anchorLine(Game g) {
        Expectations e = g.getExpectations();
        String expect = rate1(e.getExpectedInflation());
        String trust = trust(e.getCredibility());
        if (!g.getPriceIndex().isBased()) {
            return words("expect " + expect + " (the target) · trust " + trust, "exp " + expect + " · " + trust,
                    "exp " + expect);
        }
        return words("expect " + expect + " · trust " + trust, "exp " + expect + " · " + trust, "exp " + expect);
    }

    /** ...its tone: NEEDS YOU's PRICES row - plain at rest, amber in a month trust fell, red when it fell under half. */
    static String anchorTone(Game g) {
        int level = CityNeeds.prices(g).level();
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_MUTED;
    }

    /** ...its tooltip: the year's rate, how far off the target (`off`, the old line, or null with no rate yet), what people expect and why, and the level. */
    static String inflationTip(Game g, String off) {
        PriceIndex prices = g.getPriceIndex();
        Expectations e = g.getExpectations();
        double target = g.getDebtManager().getInflationTarget();
        double index = prices.isBased() ? prices.getIndex() : 1;
        StringBuilder s = new StringBuilder(String.format("Inflation: what a household's month costs against twelve"
                + " months before. The target is %s a year - yours, on the Policy tab's money page.",
                DebtManager.targetWords(target)));
        if (off != null) s.append("\n").append(Character.toUpperCase(off.charAt(0))).append(off.substring(1)).append('.');
        s.append(String.format("%nPeople expect prices to rise %s a year. They believe the bank %s: expected inflation"
                        + " is that share the target, the rest recent prices.",
                rate1(e.getExpectedInflation()), trust(e.getCredibility())));
        s.append(String.format("%nPrices are %s the city's first basket, chained; the sparkline is that price level.",
                String.format(index < 100 ? "×%.2f" : "×%,.0f", index)));
        s.append("\nClick for both lines in City History.");
        return s.toString();
    }

    /** ...and its (i): the figure's colours, the trust paragraph, and before the first rate when it comes. */
    static String inflationMore(Game g) {
        PriceIndex prices = g.getPriceIndex();
        return String.format("Good within %.0f points of your target, amber further off, red more than"
                        + " %.0f points over it or with prices falling more than %.0f%% a year.%n%n"
                        + "The line under it is what people expect inflation to be, and how far they trust the"
                        + " central bank: from %s at the least to %s at the most. Trust grows a %s of the way"
                        + " to %s each month inflation, smoothed over a year, stays within %.0f point of the"
                        + " target, and falls while it misses by more, up to a %s of the way to %s a month at"
                        + " %.0f points off - unless the policy"
                        + " rate leans against the miss: set at the rule's advice - the Standard rule's, however"
                        + " strict the bank - it costs no trust at all."
                        + " The line is amber in a month trust fell, red when it fell under %s.%s",
                STRIP_INFLATION_QUIET * 100, STRIP_INFLATION_OVER_TARGET * 100,
                STRIP_DEFLATION_ALARM * 100,
                trust(Expectations.KMIN), trust(Expectations.KMAX), ordinal(Expectations.GAIN_MONTHS),
                trust(Expectations.KMAX), Expectations.TOLERANCE * 100, ordinal(Expectations.LOSS_MONTHS),
                trust(Expectations.KMIN), (Expectations.TOLERANCE + Expectations.MISS_SCALE) * 100,
                trust(CityNeeds.TRUST_RED),
                prices.hasRate() ? ""
                : String.format("%n%nNo rate yet. The basket is fixed after %d months of the"
                        + " households' real shopping, so its mix is a"
                        + " settled city's; the first rate comes a year of readings after"
                        + " that, about %d months from now. The index reads ×1.00 until"
                        + " the basket is fixed, and inflation is taken as zero everywhere"
                        + " in the game until the first rate.",
                        PriceIndex.SETTLING_MONTHS, prices.monthsUntilRate()));
    }

    /** "sixtieth", "twenty-fourth": a month's share of the way, as the (i) says it. */
    static String ordinal(int n) {
        return switch (n) {
            case 12 -> "twelfth";
            case 24 -> "twenty-fourth";
            case 60 -> "sixtieth";
            default -> n + "th";
        };
    }

    /**
     * Good near the player's target, amber off it, red once prices run past
     * it or collapse. Since 0.7.15, every colour is read from the target:
     *   - within STRIP_INFLATION_QUIET of it, either side: good;
     *   - over it by more than that, up to STRIP_INFLATION_OVER_TARGET:
     *     amber; past that: red (Jerus's "Red at target + 5 points");
     *   - under it by more than the quiet band: amber, and red once prices
     *     fall faster than STRIP_DEFLATION_ALARM a year, whatever the target.
     *
     * NEAR THE TARGET IS FIRST (0.7.15). Until then the red test came first,
     * against 10% flat, and a city on a target past 10% - which the dial now
     * allows - would have read red for doing what it was told. Near the
     * target read the strip's quiet grey until 0.7.21, and reads good now,
     * as the mockups' "on the 2% target" does.
     */
    private static String inflationColour(double inflation, double target) {
        if (Math.abs(inflation - target) <= STRIP_INFLATION_QUIET) return Palette.GOOD;
        if (inflation - target > STRIP_INFLATION_OVER_TARGET) return Palette.BAD;
        if (inflation < -STRIP_DEFLATION_ALARM) return Palette.BAD;
        return Palette.WARN;
    }

    /** Secondary grey near parity, amber past ForeignAccounts.PARITY_WATCH either side, red past PARITY_FAR (0.7.35: the one parity rule; a pinned rate is grey). */
    private static String rateColour(ForeignAccounts fx) {
        if (fx.isPinned()) return Palette.TEXT_LABEL;
        int level = ForeignAccounts.parityLevel(fx.deviationFromParity());
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_LABEL;
    }

    /*
     * THE NEXT FIVE CITY DEBTS TO COME DUE ran across the window's foot from
     * the debt bar's beginning until 0.7.24, on every screen. Jerus chose
     * "panels fold away", and the strip became a card at the top of the
     * Finances hub, and since 0.7.32 is NEXT DUE beside the hub's ladder, the
     * same five: FinancesScreen.nextDueCard(). Its red
     * maturity - three months or less - is NEEDS YOU's FALLS DUE row
     * (CityNeeds.fallsDue()).
     */

    /* =====================================================================
       THE MAIN MENU (0.7.21)

       Jerus: the main menu was "bland as hell" - "thats the first thing
       players see". It was six grey buttons at the top of the middle column,
       with the rail hidden and the panels, the strips and an empty city's
       "Needs you: the bank - there is none" around them, before any game.

       So the menu is drawn OVER THE WHOLE WINDOW, as the mockups have it
       (Menu.dc.html): the game's name and a line under it at the left, the
       buttons under them, the cities saved last at the bottom right, the
       version at the bottom left - over a backdrop the game draws itself, a
       skyline of flat blocks with a few windows lit in the four area colours
       and the city's line rising behind it (drawBackdrop()). It is still, on
       purpose: a menu that moves is a menu that asks to be watched.

       ONE LAYER, TWO SCREENS. The layer (titleLayer) sits in windowStack over
       the whole window - the rail, the panels and the header under it - and
       clearMenu() shows it for this screen and the founding screen
       (FoundingScreen, dimmed) and takes it away for every other - but for
       the menu's own Load, Save and Settings only in a city: with no city
       open they draw on it too since 0.7.22, a panel over the dimmed
       backdrop (COLD-START SETTINGS, LOAD AND SAVE GO OVER THE BACKDROP).
       In a city they are drawn in the middle column as before, with the
       header and the panels around them. Esc and P still open this from
       anywhere, so the in-game menu is this one, over the city: Continue is
       the way back to it.
       ===================================================================== */

    /** The layer the main menu and the founding screen draw on - and, with no city open, Settings, Load and Save (0.7.22): the backdrop, a dimmer, and what the screen puts on it. */
    private StackPane titleLayer;
    private javafx.scene.canvas.Canvas backdrop;
    private Region titleDim;
    private StackPane titleContent;

    /** The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. */
    static final String BACKDROP_BLOCK = "#121c28";
    static final String BACKDROP_EDGE = "#1d2b3c";
    static final String BACKDROP_WINDOW = "#22344a";

    /** How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. The cold-start Settings, Load and Save are dimmed by it too (0.7.22). */
    static final double FOUNDING_DIM = .72;

    /** Up to this many of the cities saved last, as cards at the menu's bottom right. */
    static final int MENU_CITIES = 3;

    /**
     * The title layer shown or taken away, and emptied for the screen about
     * to draw on it. Called by clearMenu() on every screen change.
     */
    private void showTitleLayer(boolean show, boolean dim) {
        if (windowStack == null) return;
        if (!show) {
            if (titleLayer != null) windowStack.getChildren().remove(titleLayer);
            return;
        }
        if (titleLayer == null) {
            backdrop = new javafx.scene.canvas.Canvas();
            javafx.scene.layout.Pane canvasHost = new javafx.scene.layout.Pane(backdrop) {
                private double drawnW = -1, drawnH = -1;
                @Override protected void layoutChildren() {
                    double w = getWidth(), h = getHeight();
                    if (w == drawnW && h == drawnH) return;
                    drawnW = w;
                    drawnH = h;
                    backdrop.setWidth(w);
                    backdrop.setHeight(h);
                    drawBackdrop(backdrop.getGraphicsContext2D(), w, h);
                }
            };
            titleDim = new Region();
            titleDim.setStyle("-fx-background-color: " + Palette.STAGE + ";");
            titleDim.setOpacity(FOUNDING_DIM);
            titleContent = new StackPane();
            // One listener for the life of the window: the menu's column is
            // placed again when the window's height changes under it.
            titleContent.heightProperty().addListener((o, was, now) -> {
                if (placeMenu != null && "showMainMenu".equals(currentScreen)) placeMenu.run();
            });
            titleLayer = new StackPane(canvasHost, titleDim, titleContent);
            titleLayer.setStyle("-fx-background-color: " + Palette.STAGE + ";");
        }
        titleDim.setVisible(dim);
        titleContent.getChildren().clear();
        if (!windowStack.getChildren().contains(titleLayer)) {
            // Over the window, under a dialog that might already be up.
            windowStack.getChildren().add(1, titleLayer);
        }
    }

    /** Where the main menu and the founding screen put what they draw, and menuPage() its panel (0.7.22). */
    StackPane titleContent() { return titleContent; }

    /*
     * COLD-START SETTINGS, LOAD AND SAVE GO OVER THE BACKDROP (0.7.22), as
     * the main menu has since 0.7.21. Before a city is founded or loaded
     * they drew in the middle column with the empty start-up world's header
     * and panels around them - an empty city's "Needs you" and a header of
     * zeros, before any game. On a cold start they are a panel on the dimmed
     * backdrop, the founding screen's ground; in a city they stay where they
     * were, with the city around them.
     */

    /** The menu's own screens, which draw over the backdrop while no city is open. */
    private static boolean coldMenu(String screen) {
        switch (screen) {
            case "showSettingsMenu": case "showLoadMenu": case "loadSlot":
            case "showSavingMenu": case "showSaveSlotConfirm": case "showSaveResult":
                return true;
            default:
                return false;
        }
    }

    /** Where one of those screens draws: the middle column in a city; a panel over the backdrop, centred and scrolling if it must, when none is open. */
    private VBox menuPage() {
        if (cityOpen || titleContent == null || titleLayer == null
                || !windowStack.getChildren().contains(titleLayer)) {
            return rootMenu;
        }
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setMinWidth(420);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setStyle("-fx-padding: 26 30 26 30; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-background-radius: 14; -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 14;");
        StackPane page = new StackPane(panel);
        page.setStyle("-fx-padding: 16;");
        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(page);
        scroller.setFitToWidth(true);
        scroller.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        page.minHeightProperty().bind(scroller.heightProperty().subtract(2));
        titleContent.getChildren().add(scroller);
        return panel;
    }

    /** How the menu's column is placed for the window's height; run again when it changes. */
    private Runnable placeMenu;

    /**
     * The backdrop: the city's line rising behind a skyline of flat blocks,
     * a few of their windows lit in the four area colours. The mockups'
     * recipe (gen_menu.py, backdrop()), drawn to the window's size: the line
     * a long climb with a ripple and two dips across the right seven tenths,
     * the blocks from a fixed seed so it is the same skyline every time,
     * scaled to the window's height.
     */
    static void drawBackdrop(javafx.scene.canvas.GraphicsContext g, double w, double h) {
        g.setFill(javafx.scene.paint.Color.web(Palette.STAGE));
        g.fillRect(0, 0, w, h);
        if (w < 2 || h < 2) return;

        // The line: 160 points, a logistic climb with a ripple and two dips.
        int n = 160;
        double[] v = new double[n];
        double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            double t = i / (double) (n - 1);
            v[i] = .15 + .7 / (1 + Math.exp(-7 * (t - .5))) + .03 * Math.sin(i / 5.0)
                    - .05 * Math.exp(-Math.pow(i - 70, 2) / 40) - .06 * Math.exp(-Math.pow(i - 112, 2) / 30);
            lo = Math.min(lo, v[i]);
            hi = Math.max(hi, v[i]);
        }
        double x0 = w * .30, y0 = h * .12, lw = w * .70, lh = h * .48;
        javafx.scene.paint.Color money = javafx.scene.paint.Color.web(Palette.MONEY);
        g.setStroke(money.deriveColor(0, 1, 1, .75));
        g.setLineWidth(2.5);
        g.beginPath();
        for (int i = 0; i < n; i++) {
            double x = x0 + lw * i / (n - 1), y = y0 + lh - lh * (v[i] - lo) / (hi - lo);
            if (i == 0) g.moveTo(x, y); else g.lineTo(x, y);
        }
        g.stroke();
        g.setLineWidth(2);
        g.setStroke(money);
        g.setFill(javafx.scene.paint.Color.web(Palette.STAGE));
        for (int k : new int[] {40, 70, 112, 150}) {
            double x = x0 + lw * k / (n - 1), y = y0 + lh - lh * (v[k] - lo) / (hi - lo);
            g.fillOval(x - 5, y - 5, 10, 10);
            g.strokeOval(x - 5, y - 5, 10, 10);
        }

        // The skyline, from a fixed seed, at the mockups' sizes for a 900-high window.
        java.util.Random pick = new java.util.Random(7);
        double s = h / 900;
        int[] widths = {46, 58, 70, 84, 104};
        int[] heights = {120, 170, 210, 260, 320, 380};
        javafx.scene.paint.Color[] lit = {
            javafx.scene.paint.Color.web(Palette.PEOPLE, .75), javafx.scene.paint.Color.web(Palette.MONEY, .75),
            javafx.scene.paint.Color.web(Palette.BUSINESS, .75), javafx.scene.paint.Color.web(Palette.BUILDING, .75)
        };
        javafx.scene.paint.Color block = javafx.scene.paint.Color.web(BACKDROP_BLOCK);
        javafx.scene.paint.Color edge = javafx.scene.paint.Color.web(BACKDROP_EDGE);
        javafx.scene.paint.Color dark = javafx.scene.paint.Color.web(BACKDROP_WINDOW);
        g.setLineWidth(1);
        double x = w * .28;
        int[] gaps = {0, 4, 10};
        while (x < w) {
            double bw = widths[pick.nextInt(widths.length)] * s;
            double bh = heights[pick.nextInt(heights.length)] * s;
            g.setFill(block);
            g.fillRect(x, h - bh, bw, bh);
            g.setStroke(edge);
            g.strokeRect(x + .5, h - bh + .5, bw - 1, bh - 1);
            for (double wy = h - bh + 14 * s; wy < h - 10 * s; wy += 18 * s) {
                for (double wx = x + 8 * s; wx < x + bw - 10 * s; wx += 14 * s) {
                    double r = pick.nextDouble();
                    if (r < .18) {
                        g.setFill(lit[pick.nextInt(lit.length)]);
                        g.fillRect(wx, wy, 6 * s, 8 * s);
                    } else if (r < .5) {
                        g.setFill(dark);
                        g.fillRect(wx, wy, 6 * s, 8 * s);
                    }
                }
            }
            x += bw + gaps[pick.nextInt(gaps.length)] * s;
        }
    }

    /** A save's header, kept while its file is unchanged, so the menu can read eleven slots without parsing eleven files every time it opens. */
    private record KnownSave(long modified, long size, SaveHeader header) { }
    private final java.util.Map<Integer, KnownSave> knownSaves = new java.util.HashMap<>();

    /** A slot's header, read again only when its file has changed; null for an empty or unreadable slot. */
    private SaveHeader headerOf(int slot) {
        try {
            java.nio.file.Path file = game.getGameFiles().saveFile(slot);
            if (!java.nio.file.Files.isRegularFile(file)) { knownSaves.remove(slot); return null; }
            long modified = java.nio.file.Files.getLastModifiedTime(file).toMillis();
            long size = java.nio.file.Files.size(file);
            KnownSave known = knownSaves.get(slot);
            if (known != null && known.modified() == modified && known.size() == size) return known.header();
            SaveHeader header = game.getGameFiles().readHeader(slot);
            knownSaves.put(slot, new KnownSave(modified, size, header));
            return header;
        } catch (java.io.IOException | RuntimeException e) {
            return game.getGameFiles().readHeader(slot);
        }
    }

    /** "saved 20:38" today, "saved 29 Sep 20:38" this year, with the year before that. */
    private static String savedWhen(long savedAt) {
        java.time.ZonedDateTime when = java.time.Instant.ofEpochMilli(savedAt).atZone(java.time.ZoneId.systemDefault());
        java.time.LocalDate today = java.time.LocalDate.now();
        String pattern = when.toLocalDate().equals(today) ? "HH:mm"
                : when.getYear() == today.getYear() ? "d MMM HH:mm" : "d MMM yyyy HH:mm";
        return "saved " + when.format(java.time.format.DateTimeFormatter.ofPattern(pattern));
    }

    void showMainMenu() {   // package-private since 0.7.10: the founding screen's Back
        /*
         * WHERE CONTINUE GOES, and it is not "the start of the game".
         *
         * Esc opens this screen from anywhere, so Continue has to put the player
         * back on the screen they pressed it on - otherwise saving mid-game
         * costs you your place. clearMenu is about to overwrite redrawScreen
         * with this menu's own, so the screen underneath is captured first.
         */
        if (!currentScreen.isEmpty() && !"showMainMenu".equals(currentScreen)
                && !isGameMenu(currentScreen)) {
            resumeTo = redrawScreen;
        }
        clearMenu("showMainMenu", () -> showMainMenu());
        StackPane layer = titleContent();

        Label name = new Label("CityBuilderSim");
        name.setStyle("-fx-font-size: 54px; -fx-font-weight: bold; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        Label line = new Label("A city, its money, and everyone in it.");
        line.setStyle("-fx-font-size: 17px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        VBox.setMargin(line, new javafx.geometry.Insets(0, 0, 30, 0));

        /* =================================================================
           WHAT "CONTINUE" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO CONTINUE
           -----------------------------------------------------------------
           Resume used to do one thing in all three situations: call
           resumeGame(), which only calls initialize(), and then draw a screen.
           Pressed from the title screen with no city ever started, that drew
           the city view over an empty world - a game board with no game on it,
           and no way to tell that from a real one.

           There are three cases and the button answers all three. It is
           Continue since 0.7.21, the mockups' word, and it says under it which
           city it goes back to.

             a city in play -> back to the screen Esc was pressed on. This is
                the case it was written for and it is unchanged.
             no city, but an autosave on disk -> load the autosave. This is
                what a player means by it on a cold start: carry on from where
                the game last saved itself, without going through the load
                list to find the one slot they were never going to pick
                anything but.
             no city and no autosave -> greyed out. A first run has nothing to
                continue, and a button that does nothing is worse than one
                that says so.

           The autosave is deliberately the ONLY slot this reaches. Continue
           is "carry on", not "choose"; choosing is what Load a city is for.
           ================================================================= */
        // A city founded or loaded this session, not Game.isRunning(), which the
        // start-up world already sets (0.7.20; see cityOpen).
        SaveHeader autosave = cityOpen ? null : headerOf(GameFiles.AUTOSAVE_SLOT);
        boolean autosaveWaiting = autosave != null && autosave.isReadableHere();
        String where = cityOpen
                ? game.getCityName() + " · month " + formatter.format(game.getMonth()) + " · in play"
                : autosaveWaiting
                ? autosave.getCityName() + " · month " + formatter.format(autosave.getMonth())
                        + " · " + savedWhen(autosave.getSavedAt())
                : "nothing to continue yet";
        Button resume = menuButton("Continue", where, true);
        resume.setDisable(!cityOpen && !autosaveWaiting);
        resume.setOnAction(e -> {
            if (cityOpen) {
                game.resumeGame();
                if (resumeTo != null) resumeTo.run(); else openCity();
            } else {
                // loadSlot() owns the failure path and draws its own screen,
                // so a damaged autosave says why rather than dropping the
                // player into an empty city.
                loadSlot(GameFiles.AUTOSAVE_SLOT);
            }
        });

        /*
         * START A NEW CITY FOUNDS ONE (0.7.10): the founding screen first -
         * the city's name, its money, what it starts with, and the world -
         * and game.newGame() only when the player founds it there
         * (foundCity()). The world used to be set from Settings, just after
         * newGame(); it is one of the founding's choices now, applied inside
         * it (Game.buildWorld()).
         */
        Button found = menuButton("New city", null, false);
        found.setOnAction(e -> foundingScreen.show());
        Button load = menuButton("Load a city", null, false);
        load.setOnAction(e -> showLoadMenu());
        Button settings = menuButton("Settings", null, false);
        settings.setOnAction(e -> showSettingsMenu());
        Button quit = menuButton("Quit", null, false);
        // Asks first (0.7.20); see showQuitDialog.
        quit.setOnAction(e -> showQuitDialog());

        VBox column = new VBox(12, name, line, resume);
        // A city in play still needs saving to a slot: Save keeps its place,
        // under Continue, and is not there when there is nothing to save.
        if (cityOpen) {
            Button save = menuButton("Save", null, false);
            save.setOnAction(e -> showSavingMenu());
            column.getChildren().add(save);
        }
        column.getChildren().addAll(found, load, settings, quit);
        column.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(column, Pos.TOP_LEFT);

        // Small, grey, always there; the log's path is its tooltip. A bug
        // report that arrives with that file attached is worth ten that do not.
        Label version = new Label(GameVersion.VERSION);
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        Tooltip.install(version, new Tooltip(GameVersion.title() + "\n" + (GameLog.file() == null
                ? "Logging is not running."
                : "Its log: " + GameLog.file())));
        StackPane.setAlignment(version, Pos.BOTTOM_LEFT);
        StackPane.setMargin(version, new javafx.geometry.Insets(0, 0, 36, MENU_LEFT));

        layer.getChildren().addAll(column, version);

        // The column's distance from the top: the mockups' 110 on a tall
        // window, less on a short one, so the last button clears the foot.
        Runnable place = () -> {
            column.applyCss();      // its sizes are the stylesheet's, before the first layout
            double tall = layer.getHeight() > 0 ? layer.getHeight() : stage.getHeight();
            double top = Math.max(24, Math.min(110, (tall - column.prefHeight(-1)) / 2.4));
            StackPane.setMargin(column, new javafx.geometry.Insets(top, 0, 0, MENU_LEFT));
        };
        place.run();
        placeMenu = place;

        VBox cities = yourCities(layer.getWidth() > 0 ? layer.getWidth() : stage.getWidth());
        if (cities != null) {
            StackPane.setAlignment(cities, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(cities, new javafx.geometry.Insets(0, 56, 40, 0));
            layer.getChildren().add(cities);
        }
        resume.requestFocus();
    }

    /** How far in from the window's left the menu's column and version sit. */
    static final double MENU_LEFT = 96;

    /** The menu's buttons' width, as the mockups draw them. */
    static final double MENU_BUTTON = 360;

    /** One of the menu's buttons: a word, and under the primary one what it goes back to. */
    private Button menuButton(String text, String under, boolean primary) {
        Label word = new Label(text);
        word.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 17px; -fx-text-fill: "
                + (primary ? Palette.ON_FILL : Palette.TEXT_HEAD) + ";");
        VBox face = new VBox(2, word);
        if (under != null) {
            Label sub = new Label(under);
            sub.setStyle(Fonts.sansMedium() + " -fx-font-size: 12px; -fx-text-fill: "
                    + (primary ? Palette.ON_FILL : Palette.TEXT_MUTED) + ";");
            sub.setOpacity(primary ? .8 : 1);
            face.getChildren().add(sub);
        }
        Button button = new Button();
        button.setGraphic(face);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(MENU_BUTTON);
        button.setMinWidth(MENU_BUTTON);
        button.setMinHeight(56);
        String ground = primary ? Palette.MONEY : Palette.RAISED;
        String lit = primary ? Palette.ACCENT_LIGHT : Palette.PINNED;
        String rest = "-fx-padding: 12 20 12 20; -fx-background-radius: 10; -fx-border-radius: 10;"
                + " -fx-cursor: hand; -fx-border-color: " + (primary ? Palette.MONEY : Palette.EDGE) + ";";
        button.setStyle(rest + " -fx-background-color: " + ground + ";");
        button.setOnMouseEntered(e -> button.setStyle(rest + " -fx-background-color: " + lit + ";"));
        button.setOnMouseExited(e -> button.setStyle(rest + " -fx-background-color: " + ground + ";"));
        return button;
    }

    /**
     * YOUR CITIES: the slots saved last, newest first, as cards - the
     * city's name, its slot, its people and its month, and when it was
     * saved. A click loads it. As many as fit beside the menu's column, up
     * to MENU_CITIES; null when no numbered slot holds a city.
     *
     * NO SPARKLINE, though the mockups draw one. A save's header carries
     * this month's figures only; a line would mean reading the slot's
     * history file, which for a big city is the largest file in the folder,
     * every time the menu opens. When it was saved stands in its place.
     */
    private VBox yourCities(double wide) {
        List<int[]> recent = new ArrayList<>();          // {slot}, sorted below by when it was saved
        java.util.Map<Integer, SaveHeader> headers = new java.util.HashMap<>();
        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            SaveHeader header = headerOf(slot);
            if (header == null || !header.isReadableHere()) continue;
            headers.put(slot, header);
            recent.add(new int[] {slot});
        }
        if (recent.isEmpty()) return null;
        recent.sort((a, b) -> Long.compare(headers.get(b[0]).getSavedAt(), headers.get(a[0]).getSavedAt()));

        // As many as fit to the right of the column, with a margin between.
        int fit = (int) Math.floor((wide - 56 - MENU_LEFT - MENU_BUTTON - 40 + 12) / (CITY_CARD + 12));
        int shown = Math.max(1, Math.min(MENU_CITIES, Math.min(fit, recent.size())));

        HBox cards = new HBox(12);
        for (int i = 0; i < shown; i++) {
            int slot = recent.get(i)[0];
            cards.getChildren().add(cityCard(slot, headers.get(slot)));
        }
        Label head = new Label("YOUR CITIES");
        head.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 12px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        HBox headRow = new HBox(6, head, infoButton("The cities saved most recently, from slots 1 to "
                + GameFiles.SLOT_COUNT + ". A click loads one. Every slot, and the autosave, is under"
                + " Load a city.", false));
        headRow.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(10, headRow, cards);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return box;
    }

    /** A city card's width, as the mockups draw it. */
    static final double CITY_CARD = 250;

    /** One saved city: its name and slot, its people and month, when it was saved; a click loads it. */
    private VBox cityCard(int slot, SaveHeader header) {
        Label city = new Label(header.getCityName());
        city.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 14px; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        city.setMinWidth(0);
        Label which = new Label("slot " + slot);
        which.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        which.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(8, city, gap, which);
        top.setAlignment(Pos.BASELINE_LEFT);

        Label figures = new Label(formatter.format(header.getPopulation()) + " people · month "
                + formatter.format(header.getMonth()));
        figures.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 13px;"
                + " -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        Label when = new Label(savedWhen(header.getSavedAt()));
        when.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");

        VBox card = new VBox(4, top, figures, when);
        card.setPrefWidth(CITY_CARD);
        card.setMinWidth(CITY_CARD);
        card.setMaxWidth(CITY_CARD);
        String rest = "-fx-padding: 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand; -fx-border-color: ";
        card.setStyle(rest + Palette.EDGE + ";");
        card.setOnMouseEntered(e -> card.setStyle(rest + Palette.ACCENT + ";"));
        card.setOnMouseExited(e -> card.setStyle(rest + Palette.EDGE + ";"));
        Tooltip.install(card, new Tooltip("Load " + header.getCityName() + " from slot " + slot
                + (header.hasName() ? " (“" + header.getSlotName() + "”)" : "") + "."));
        card.setOnMouseClicked(e -> loadSlot(slot));
        return card;
    }

    /* =========================================================================
       THE SAVE SYSTEM

       Ten numbered slots and an autosave. Every slot is labelled from the city
       inside it - month, people, money, when it was written - because that is
       what a player actually recognises a save by. A name is optional on top,
       for the ones worth remembering ("before the steel mill").

       The autosave appears on the load list and not the save list. That is the
       point of it: it cannot be spent on the city you were about to abandon.
       ========================================================================= */

    private static final java.time.format.DateTimeFormatter SAVED_AT =
            java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm");

    /** One line describing what is in a slot, or that it is empty. */
    private String slotSummary(int slot) {

        GameFiles files = game.getGameFiles();

        // Empty and unreadable are different things, and saying "Empty" for
        // both was the bug: the button stayed enabled because the FILE existed,
        // so clicking Load on a slot labelled Empty did nothing at all.
        if (files.slotIsEmpty(slot)) return "Empty";

        SaveHeader header = files.readHeader(slot);
        if (header == null) return "Damaged - this file cannot be read";

        if (header.isFromNewerBuild()) {
            return "From a newer version (" + header.getGameVersion() + ")";
        }
        if (header.isFromBeforeSectors()) {
            return "From before the sector redesign (" + header.getGameVersion() + ") - cannot be loaded";
        }

        String when = java.time.Instant.ofEpochMilli(header.getSavedAt())
                .atZone(java.time.ZoneId.systemDefault())
                .format(SAVED_AT);

        // The city's name first (0.7.10) - Danzik on a save from before it.
        return String.format("%s  -  Month %d  -  %s people  -  %s  -  %s",
                header.getCityName(),
                header.getMonth(),
                formatter.format(header.getPopulation()),
                money(header.getCash()),
                when);
    }

    private String slotTitle(int slot) {
        SaveHeader header = game.getGameFiles().readHeader(slot);
        String base = GameFiles.slotLabel(slot);
        if (header == null || !header.hasName()) return base;
        /*
         * "Autosave · month 13", not "Autosave - Autosave - month 13"
         * (0.7.20): the autosave names itself "Autosave - <why>"
         * (Game.autosave()), and the label was put in front of it again.
         */
        String name = header.getSlotName();
        if (name.startsWith(base + " - ")) name = name.substring(base.length() + 3);
        return base + " \u00b7 " + name;
    }

    /** A slot row: the label on the button, the city underneath it. */
    private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty) {

        VBox row = new VBox(1);
        row.setAlignment(Pos.CENTER);

        GameFiles files = game.getGameFiles();
        boolean empty = files.slotIsEmpty(slot);
        boolean broken = files.slotIsUnreadable(slot);

        Button pick = new Button(slotTitle(slot));
        pick.setMaxWidth(300);

        // On the LOAD list, a slot is clickable only if it can actually be
        // loaded. A damaged file is not an empty slot and must not offer a
        // button that silently does nothing.
        pick.setDisable(disableEmpty && !files.slotIsLoadable(slot));
        pick.setOnAction(e -> onPick.accept(slot));

        Label detail = new Label(slotSummary(slot));
        detail.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (broken ? Palette.BAD : empty ? Palette.TEXT_FAINT : Palette.TEXT_MUTED) + ";");

        row.getChildren().addAll(pick, detail);
        return row;
    }

    private void showSavingMenu() {
        clearMenu("showSavingMenu", () -> showSavingMenu());
        VBox page = menuPage();

        Label heading = new Label("SAVE GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label hint = new Label("Choose a slot. The autosave is not in this list "
                + "on purpose - it is written for you.");
        hint.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        hint.setWrapText(true);
        hint.setMaxWidth(320);

        page.getChildren().addAll(heading, hint);

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            page.getChildren().add(slotRow(slot, this::showSaveSlotConfirm, false));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        page.getChildren().add(cancel);
    }

    /**
     * Confirms one slot, and takes the optional name.
     *
     * The name field is prefilled with whatever the slot already carried, so
     * overwriting a save keeps its name unless the player chooses otherwise.
     */
    private void showSaveSlotConfirm(int slot) {
        clearMenu("showSaveSlotConfirm", () -> showSaveSlotConfirm(slot));
        VBox page = menuPage();

        boolean occupied = !game.getGameFiles().slotIsEmpty(slot);
        SaveHeader header = game.getGameFiles().readHeader(slot);

        Label heading = new Label("SAVE TO " + GameFiles.slotLabel(slot).toUpperCase());
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label current = new Label(slotSummary(slot));
        current.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");

        TextField name = new TextField(
                (header != null && header.hasName()) ? header.getSlotName() : "");
        name.setPromptText("Name this save (optional)");
        name.setMaxWidth(300);

        page.getChildren().addAll(heading, current, name);

        if (occupied) {
            Label warn = new Label("This slot already has a city in it. Saving replaces it.");
            warn.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Palette.WARN + ";");
            warn.setWrapText(true);
            warn.setMaxWidth(320);
            page.getChildren().add(warn);
        }

        Button confirm = new Button(occupied ? "Overwrite" : "Save");
        Button cancel = new Button("Back");

        confirm.setOnAction(e -> {
            String typed = name.getText();
            showSaveResult(game.saveGame(slot,
                    (typed == null || typed.isBlank()) ? null : typed.trim()));
        });
        cancel.setOnAction(e -> showSavingMenu());

        page.getChildren().addAll(confirm, cancel);
    }

    private void showLoadMenu() {
        clearMenu("showLoadMenu", () -> showLoadMenu());
        VBox page = menuPage();

        Label heading = new Label("LOAD GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        page.getChildren().add(heading);

        // Autosave first: it is the most recent thing the game wrote, so it is
        // what someone recovering from a crash is looking for.
        page.getChildren().add(
                slotRow(GameFiles.AUTOSAVE_SLOT, this::loadSlot, true));

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            page.getChildren().add(slotRow(slot, this::loadSlot, true));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        page.getChildren().add(cancel);
    }

    private void loadSlot(int slot) {

        game.loadGameSave(slot);

        String failure = game.getLoadFailure();
        if (failure != null) {
            clearMenu("loadSlot", () -> loadSlot(slot));
            VBox page = menuPage();
            Label outcome = new Label("Could not load.");
            outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;"
                    + " -fx-text-fill: " + Palette.BAD + ";");
            Label why = new Label(failure);
            why.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Palette.BAD + ";");
            why.setWrapText(true);
            why.setMaxWidth(320);
            Button back = new Button("Back");
            back.setOnAction(e -> showLoadMenu());
            page.getChildren().addAll(outcome, why, back);
            return;
        }

        // A load replaces the city - Continue's autosave on a cold start included.
        anotherCity();
        openCity();
    }

    /**
     * Where a city opens.
     *
     * Buildings, because it is the one screen a player who does not yet know
     * what they are doing can do something on - and because the rail is on
     * screen from here, every other screen is one click away rather than three.
     */
    private void openCity() {
        /*
         * THE CITY'S NAME IN THE WINDOW'S TITLE (0.7.10), beside the build -
         * set here because this is where every city opens, founded or loaded.
         * A name is at most Founding.MAX_CITY_NAME_LENGTH, which the title
         * has room for.
         */
        stage.setTitle(game.getCityName() + " - " + GameVersion.title());
        buildScreen.showBuildMenu();
    }

    /**
     * Another city has just replaced the one on screen - founded, or loaded
     * (0.7.20): its toasts and its purchases are not this one's. Called from
     * foundCity() and a load that went through, and NOT from openCity(),
     * which is also how the player gets back to the SAME city - the time
     * skip's Cancel and Done, and Continue with no screen to go back to - where
     * clearing them toasted every unread urgent notice again and forgot the
     * session's purchases.
     */
    private void anotherCity() {
        cityOpen = true;
        toasted.clear();
        if (toastStack != null) toastStack.getChildren().clear();
        buildScreen.forgetReceipts();
        // A new city or a load opens Build on its Overview (0.7.24), every ring on its worst.
        buildScreen.buildCategory = BuildScreen.BUILD_HOME;
        buildScreen.measurePicked.clear();
        // ...and the land office forgets what it last showed: nothing on a new shelf is NEW (0.7.26).
        landScreen.forget();
        // ...and People: no month has just landed on it (0.7.27).
        peopleScreen.forget();
    }

    /** The founding screen's last step: found the city as chosen, and open it. */
    void foundCity(Founding choices) {
        game.newGame(choices);
        anotherCity();
        openCity();
    }

    private void showSaveResult(GameFiles.Result result) {
        clearMenu("showSaveResult", () -> showSaveResult(result));
        VBox page = menuPage();

        Label outcome = new Label(result.ok ? "Saved." : "Save failed.");
        outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: "
                + (result.ok ? Palette.GOOD : Palette.BAD) + ";");

        Label detail = new Label(result.ok
                ? result.file.toString()
                : result.error);
        detail.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (result.ok ? Palette.TEXT_MUTED : Palette.BAD) + ";");
        detail.setWrapText(true);
        detail.setMaxWidth(320);

        Button back = new Button("Back");
        back.setOnAction(e -> showMainMenu());

        page.getChildren().addAll(outcome, detail, back);

        if (!result.ok) {
            // Worth saying out loud: the usual causes are a full disk or a
            // folder the player has no permission to write to, and neither is
            // something the game can fix for them.
            Label advice = new Label(
                    "The city is still running - nothing has been lost yet. "
                    + "Check there is free disk space, then try again.");
            advice.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            advice.setWrapText(true);
            advice.setMaxWidth(320);
            page.getChildren().add(2, advice);
        }
    }

    /** How the player likes the window. Loaded once in start(). */
    GamePrefs prefs = new GamePrefs();

    /**
     * In and out of full screen, and remembered.
     *
     * Written to disk on every toggle rather than on quit, because a game that
     * only saves your preferences when you close it politely forgets them the
     * first time it crashes - and the file is forty bytes.
     */
    private void toggleFullScreen() {
        boolean on = !stage.isFullScreen();
        stage.setFullScreen(on);
        prefs.setFullScreen(on);
        prefs.save(game.getGameFiles());
        if (currentScreen != null && currentScreen.equals("showSettingsMenu")) {
            showSettingsMenu();
        }
    }

    /* =====================================================================
       QUIT ASKS FIRST (0.7.20)

       Quit went straight to the desktop: one click, no question. The
       autosave was written, so nothing was lost - but the autosave it
       overwrote was, and a menu button next to Save and Load is an easy one
       to hit by mistake. So it asks: "Quit to the desktop? This city is
       autosaved.", Quit and Cancel. Esc and Cancel close it (the key filter
       in start() hands Esc to an open dialog first).

       A DIALOG OVER THE WHOLE WINDOW, not a JavaFX Alert. An Alert is a
       window of its own, in the platform's light theme, and a second window
       over a full-screen one is the thing full screen does worst. This is a
       card on a dimmed layer over windowStack: it takes every click while it
       is up, so nothing behind it can be pressed by accident.
       ===================================================================== */
    private void showQuitDialog() {
        if (quitDialog != null) return;

        Label ask = new Label("Quit to the desktop?");
        ask.setStyle(Palette.words(Palette.SIZE_LEAD, Palette.TEXT_HEAD) + " -fx-font-weight: bold;");
        // Only a city in play is autosaved (Game.autosave() writes nothing before one).
        Label why = new Label(cityOpen ? "This city is autosaved." : "There is no city open.");
        why.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));

        Button yes = new Button("Quit");
        yes.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ALERT_EDGE + ";");
        yes.setOnAction(e -> game.toggleQuit());
        Button no = new Button("Cancel");
        no.setOnAction(e -> closeQuitDialog());
        HBox buttons = new HBox(Palette.GAP, no, yes);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(Palette.GAP, ask, why, buttons);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-padding: 18 20 16 20;" + Palette.block(Palette.PANEL, Palette.CONTROL_EDGE));
        VBox.setMargin(buttons, new javafx.geometry.Insets(8, 0, 0, 0));

        StackPane layer = new StackPane(card);
        layer.setStyle("-fx-background-color: rgba(5, 10, 15, 0.62);");
        // The layer swallows a click anywhere outside the card; Cancel is the way out.
        layer.setOnMouseClicked(javafx.scene.input.MouseEvent::consume);
        quitDialog = layer;
        windowStack.getChildren().add(layer);
        // Cancel holds the focus, so Space or Enter on it is the safe answer.
        no.requestFocus();
    }

    /** Close the dialog that is up, Quit's or a confirmation: Cancel, or Esc. */
    private void closeQuitDialog() {
        if (quitDialog == null) return;
        windowStack.getChildren().remove(quitDialog);
        quitDialog = null;
    }

    /* =====================================================================
       A CONFIRMATION, WITH THE MONEY IN IT (0.7.22)

       The construction page's cancel, demolition and buy-out each ask first,
       in the Quit dialog's style (0.7.20): a card on a dimmed layer over the
       whole window - the question, a line on what follows, the money in a
       column of figures, Cancel and the action. Esc and Cancel close it: it
       is the one dialog the key filter hands Esc to (quitDialog holds
       whichever is up). Cancel holds the focus, and the clock stands still
       while it is up, so the figures in it are the ones the action meets.
       With no action it is a notice, and its one button closes it.
       ===================================================================== */
    void confirm(String ask, String why, java.util.List<String[]> lines, String yesText, Runnable yes) {
        if (quitDialog != null) return;

        Label question = new Label(ask);
        question.setWrapText(true);
        question.setMaxWidth(440);
        question.setStyle(Palette.words(Palette.SIZE_LEAD, Palette.TEXT_HEAD) + " -fx-font-weight: bold;");
        Label what = new Label(why);
        what.setWrapText(true);
        what.setMaxWidth(440);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));

        VBox figures = new VBox(4);
        if (lines != null) {
            for (String[] line : lines) {
                Label name = new Label(line[0]);
                name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));
                Region gap = new Region();
                HBox.setHgrow(gap, Priority.ALWAYS);
                Label figure = new Label(line[1]);
                figure.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
                HBox row = new HBox(Palette.GAP, name, gap, figure);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPrefWidth(440);
                figures.getChildren().add(row);
            }
        }

        Button no = new Button(yes == null ? "Close" : "Cancel");
        no.setOnAction(e -> closeQuitDialog());
        HBox buttons = new HBox(Palette.GAP, no);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        if (yes != null) {
            Button go = new Button(yesText);
            go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                    + " -fx-background-color: " + Palette.ALERT_EDGE + ";");
            go.setOnAction(e -> {
                closeQuitDialog();
                yes.run();
            });
            buttons.getChildren().add(go);
        }

        VBox card = new VBox(Palette.GAP, question, what);
        if (!figures.getChildren().isEmpty()) card.getChildren().add(figures);
        card.getChildren().add(buttons);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-padding: 18 20 16 20;" + Palette.block(Palette.PANEL, Palette.CONTROL_EDGE));
        VBox.setMargin(buttons, new javafx.geometry.Insets(8, 0, 0, 0));

        StackPane layer = new StackPane(card);
        layer.setStyle("-fx-background-color: rgba(5, 10, 15, 0.62);");
        layer.setOnMouseClicked(javafx.scene.input.MouseEvent::consume);
        quitDialog = layer;
        windowStack.getChildren().add(layer);
        no.requestFocus();
    }

    /* =====================================================================
       EVERY TOOLTIP, DRESSED ONCE (0.7.20)

       Tooltips are made in some thirty places across the screens, each where
       it is needed. The stylesheet gives every one the same look (applyTheme's
       .tooltip); two things it cannot set are done here, on each tooltip as it
       is shown, by watching the window list:

         - A WIDTH TO WRAP AT, TIP_WIDTH, unless the tooltip set its own. A
           long tooltip ran across the panels on one line.
         - ESCAPE IS NOT ITS TO TAKE. A showing tooltip is a popup window,
           and a popup hides on Escape and CONSUMES it - in its owner window's
           dispatcher, before the scene's key filter ever sees the key. On the
           Build tab the pointer is nearly always over a card's tooltip, so
           Esc did nothing there while P opened the menu: Jerus's play-through,
           twice. With hideOnEscape off, Esc goes through to the filter, which
           opens the menu and puts the tooltips away (hideTooltips()).
       ===================================================================== */

    /** The width a tooltip's text wraps at, unless it asked for its own. */
    static final double TIP_WIDTH = 420;

    private void dressTooltips() {
        javafx.stage.Window.getWindows().addListener(
                (javafx.collections.ListChangeListener<javafx.stage.Window>) change -> {
            while (change.next()) {
                for (javafx.stage.Window w : change.getAddedSubList()) {
                    if (!(w instanceof Tooltip tip)) continue;
                    tip.setHideOnEscape(false);
                    tip.setWrapText(true);
                    if (tip.getMaxWidth() < 0) tip.setMaxWidth(TIP_WIDTH);   // USE_COMPUTED_SIZE: not set
                }
            }
        });
    }

    /** Every tooltip showing, put away - so one is not left hanging over the menu Esc opens. */
    private static void hideTooltips() {
        for (javafx.stage.Window w : new ArrayList<>(javafx.stage.Window.getWindows())) {
            if (w instanceof Tooltip tip && tip.isShowing()) tip.hide();
        }
    }

    /* =====================================================================
       SETTINGS.

       Four toggles, the keys and a way back, and it had been three raw grey
       buttons since before the palette existed - which mattered less when it
       was buried behind a menu and matters now, because the menu at the foot
       of the rail is a click from here, from anywhere.
       ===================================================================== */
    private void showSettingsMenu() {
        clearMenu("showSettingsMenu", () -> showSettingsMenu());
        VBox page = menuPage();

        Label title = pageTitle("SETTINGS");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        column.getChildren().add(statementHead("The window"));
        column.getChildren().add(toggleRow("Full screen", stage.isFullScreen(),
                "No title bar and no taskbar — F11 from anywhere, at any time.",
                this::toggleFullScreen));

        column.getChildren().add(statementHead("The clock"));
        column.getChildren().add(toggleRow("Stop for anything important",
                prefs.isPauseOnEvents(),
                "Off: the clock runs until you stop it. On: it halts when the city "
                + "has something urgent to say - a bank failing, a family with "
                + "nowhere to sleep - and tells you what. Worth turning on when you "
                + "are crossing centuries at 10x and do not want to miss the month "
                + "it went wrong.",
                () -> {
                    prefs.setPauseOnEvents(!prefs.isPauseOnEvents());
                    prefs.save(game.getGameFiles());
                    showSettingsMenu();
                }));

        column.getChildren().add(statementHead("What the city prints"));
        column.getChildren().add(toggleRow("Graphs", game.isGraphsEnabled(),
                "The month-by-month charts under City History.",
                () -> { game.toggleGraphs(); showSettingsMenu(); }));
        column.getChildren().add(toggleRow("Reports", game.isReportsEnabled(),
                "The written month report in the console behind the window.",
                () -> { game.toggleReports(); showSettingsMenu(); }));
        column.getChildren().add(statementNote(
                "These two are kept with the city and travel in its save file. "
                + "Full screen is kept with the game and is the same for every city "
                + "on this machine."));

        /*
         * THE WORLD THE NEXT CITY IS FOUNDED INTO was a block here until
         * 0.7.10: five chips and a note, the one setting on this screen that
         * did not take effect while you looked at it. It is chosen on the
         * founding screen now (FoundingScreen), with the city's name and its
         * money, which is the only moment it ever acted.
         */

        column.getChildren().add(statementHead("Keys"));
        column.getChildren().add(statementLine("Esc  ·  P", "the game menu (Esc first closes full-screen chart, inbox, City overview)"));
        column.getChildren().add(statementLine("F11", "the window's full screen on and off"));
        // ...and the four the list never carried (0.7.5): a shortcut nothing
        // announces is a shortcut for people who already know the game.
        column.getChildren().add(statementLine("Space", "the clock: run and pause"));
        column.getChildren().add(statementLine("\u2190  \u00b7  \u2192", "the speed, one rung a press"));
        column.getChildren().add(statementLine("Enter", "on the build tab: place every pending order on the page"));
        column.getChildren().add(statementLine("Backspace  \u00b7  Delete", "on the build tab: clear them"));

        Button back = new Button("Back");
        back.setOnAction(e -> showMainMenu());

        // Over the backdrop the panel scrolls itself; in a city the column scrolls in the page.
        page.getChildren().addAll(title, page == rootMenu ? scrolled(column) : column, back);
    }

    /*
     * settledLevelAt() and worldChip() lived here until 0.7.10: the first is
     * a model figure and is WorldEconomy.settledLevelAt() now, the second is
     * the founding screen's (FoundingScreen.worldChip()).
     */

    /**
     * A setting: what it is, what it does, and a switch that says which way
     * it is set without having to read the word next to it.
     */
    private VBox toggleRow(String label, boolean on, String what, Runnable flip) {

        Label name = new Label(label);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Button state = new Button(on ? "ON" : "OFF");
        state.setStyle(Palette.figure(Palette.SIZE_LABEL,
                    on ? "white" : Palette.TEXT_MUTED)
                + " -fx-background-color: "
                + (on ? Palette.CONFIRM : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-border-color: " + (on ? "transparent" : Palette.CONTROL_EDGE) + ";"
                + " -fx-border-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-min-width: 52; -fx-cursor: hand;");
        state.setOnAction(e -> flip.run());

        HBox row = new HBox(Palette.GAP_LOOSE, name, gap, state);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 4 0 2 0;");

        VBox box = new VBox(0, row, statementNote(what));
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /* =====================================================================
       THE MAIN SCREEN IS GONE, AND THAT IS THE POINT.

       showStartMenu() was seven buttons in a column - Buildings, Economy,
       Policy, Population, Next Month, Simulate, Back - plus whichever of four
       red banners applied. Every one of those seven is now somewhere it can be
       reached from ANY screen instead of only from this one: the first four are
       tabs on the rail down the left, the two time controls are the header's
       clock (since 0.7.21; they were round buttons at the bottom right), and
       Back was only ever a way to the game menu, which is Menu at the foot of
       the rail.

       The four banners moved further than that. They are Notices now - raised
       by the city once a month whether or not anybody is looking, kept after
       they are read, and kept greyed for two years after they are fixed. See
       Inbox, and inboxCorner() below for what draws them.

       So there is no screen left to return to, and nothing calls this. A new
       or loaded city opens on Buildings, because it is the one screen where a
       player who does not yet know what to do can do something.
       ===================================================================== */


    /* =====================================================================
       ECONOMY IS GONE, AND IT SPLIT IN TWO.

       It was a column of seven buttons: Finance, Restructure, Debt Info, Sector
       Info, Government & National Accounts, The Bank, Trade & The World. Every
       one of those is now a place on the rail or a link on the screen that owns
       it, so this screen had become a list of shortcuts to the rail sitting
       three inches to its left.

       The split follows the line the game itself draws and the one Jerus asked
       for: GOVERNMENT ECONOMY is what the state takes and spends, SECTOR
       ECONOMY is what the businesses earn. Finance absorbed Restructure, Debt
       Info and the Bank, because they are the same subject - what the city owes
       and what money costs it.
       ===================================================================== */


    /* =====================================================================
       THE SCROLLER

       The scroll pane every screen sits in, and the one that remembers where
       the player was through a redraw. It reads the shell's current screen
       and scroller, so it is the shell's; its own section since 2026-09-18
       so the policy screen could leave without taking it.
       ===================================================================== */

    /** Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). */
    static final double PAGE_FOOT = 24;

    /** A left-aligned column inside a scroll pane, which these screens all want. */
    javafx.scene.control.ScrollPane scrolled(VBox column) {
        return scrolled(column, 150);
    }

    /**
     * @param chrome how much of the stage the screen has already spent above
     *               this scroller - a title alone is about 150; a title with a
     *               vitals bar and two strips pinned over it is a good deal
     *               more, and getting it wrong is a scrollbar that appears when
     *               there is nothing to scroll.
     */
    javafx.scene.control.ScrollPane scrolled(VBox column, double chrome) {
        return scrolled(column, chrome, false);
    }

    /**
     * The same, with the position kept from the bottom of the page instead of
     * as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the
     * controls the player is using; see keptScrollerFromBottom.
     */
    javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom) {
        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        /*
         * ROOM UNDER THE END OF THE PAGE (0.7.20). The end of a scrolled page
         * - the Middle School's Build button, the last row of plots, the foot
         * of the History chart - sat at the bottom edge of the stage where the
         * income dome was, and read as covered by it. The dome went in 0.7.21
         * (its figure was the header's TREASURY tile, and is the money
         * block's since 0.7.24), and PAGE_FOOT is a margin again.
         */
        content.setPadding(new javafx.geometry.Insets(0, 0, PAGE_FOOT, 0));
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = fromBottom
                ? keptScrollerFromBottom(currentScreen + ":body", content)
                : keptScroller(currentScreen + ":body", content);
        scroll.setFitToWidth(true);
        /*
         * AS TALL AS THE WINDOW ALLOWS, rather than 400px.
         *
         * 400 was set when this wrapped a policy screen of eight buttons. It
         * now wraps the People screen (until 0.7.27), the bank's books and the new Services
         * screen, and on a maximised window it was showing about half of each
         * inside a box with three hundred pixels of empty stage under it - so
         * the player scrolled a small window while a large one sat unused.
         *
         * The 150 is the title, the lead line and the padding above it; the
         * floor of 320 is for a window small enough that the arithmetic would
         * otherwise go negative.
         */
        scroll.prefHeightProperty().bind(javafx.beans.binding.Bindings.max(
                260, menuScroller.heightProperty().subtract(chrome)));
        scroll.setStyle("-fx-background-color:transparent;");
        return scroll;
    }

    /* ---------------------------------------------------------------------
       WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and
       cannot see BuildScreen, so the three card methods it checks are still
       answered here, by forwarding - and since 0.7.24 the Build tab's own
       pages, the page it opens on and the strip after it, so the harness can
       hold the window to the model's list (BuildAdvice.categories()).
       --------------------------------------------------------------------- */

    public List<String> whatItDoes(BuildingsTemplate t) { return buildScreen.whatItDoes(t); }
    public List<String> whatCareItGives(BuildingsTemplate t) { return buildScreen.whatCareItGives(t); }
    public String jobLabel(JobType job) { return buildScreen.jobLabel(job); }

    /** Build's pages as the window names them: where it opens (BuildScreen.BUILD_HOME), then the strip's categories in order. */
    public List<String> buildPages() {
        List<String> pages = new ArrayList<>();
        pages.add(BuildScreen.BUILD_HOME);
        for (BuildScreen.BuildCategory c : BuildScreen.buildCategories()) pages.add(c.name());
        return pages;
    }

    /** The page Build opens on in this window now: the Overview, until the player opens a category (0.7.24). */
    public String buildOpensOn() { return buildScreen.buildCategory; }


    /* =====================================================================
       SIMULATE MULTIPLE MONTHS

       The terminal version asked "How many months?" and read the answer from
       getInput(), which is stubbed to return 0 - so the loop never ran. This is
       the same increment-button pattern the build and debt screens use.
       ===================================================================== */

    private void showSimulateMonthsMenu() {
        clearMenu("showSimulateMonthsMenu", () -> showSimulateMonthsMenu());

        final int[] months = {0};

        Label title = new Label("SIMULATE MULTIPLE MONTHS");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label info = new Label("Month " + game.getMonth()
                + " | Cash: " + money(game.getCash()));

        Label totalLabel = new Label("Months to simulate: 0");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label note = new Label("Reports and graphs are muted while fast-forwarding.");
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");

        javafx.scene.layout.FlowPane grid = new javafx.scene.layout.FlowPane(10, 10);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(320);

        int[] increments = {1, 5, 10, 25, 50, 100};
        for (int amount : increments) {
            final int step = amount;
            Button button = new Button("+" + amount);
            button.setPrefWidth(60);
            button.setOnAction(e -> {
                months[0] += step;
                totalLabel.setText("Months to simulate: " + months[0]);
            });
            grid.getChildren().add(button);
        }

        Button reset = new Button("Reset");
        reset.setOnAction(e -> {
            months[0] = 0;
            totalLabel.setText("Months to simulate: 0");
        });

        Button run = new Button("Run");
        run.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;");
        run.setOnAction(e -> {
            if (months[0] > 0) {
                int completed = game.simulateMonths(months[0]);
                showSimulateResultMenu(months[0], completed);
            }
        });

        Button back = new Button("Cancel");
        back.setOnAction(e -> openCity());

        rootMenu.getChildren().addAll(title, info, totalLabel, grid, note, reset, run, back);
    }

    /**
     * What happened while the player was not watching.
     *
     * Fast-forwarding is how this game is actually played, and this screen used
     * to say "100 of 100 months simulated" and nothing else. Anything that
     * reports itself and then expires - the demolition log keeps entries for
     * two years - could happen and vanish entirely inside one skip, which is
     * exactly how a demolition went unnoticed through several test runs.
     *
     * Ordered worst-first: what went wrong, then what changed, then the detail.
     */
    private void showSimulateResultMenu(int requested, int completed) {
        clearMenu("showSimulateResultMenu", () -> showSimulateResultMenu(requested, completed));

        TimeSkipReport skip = game.getSkipReport();

        Label title = new Label("SIMULATION COMPLETE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label result = new Label(String.format("Month %,d to %,d  -  %d month%s",
                skip.getStartMonth(), game.getMonth(),
                completed, completed == 1 ? "" : "s"));
        result.setStyle("-fx-font-size: 14px;");

        VBox column = new VBox(0);

        /*
         * If the simulation itself broke, say so first and say where to look.
         *
         * Before this the window simply stopped responding partway through a
         * skip: the exception reached the FX thread's default handler and a
         * stderr that does not exist in a packaged build, so the player got a
         * month counter that stopped and no explanation at all.
         */
        String failure = game.takeSkipFailure();
        if (failure != null) {
            VBox problem = reportSection("SOMETHING WENT WRONG");
            for (String line : failure.split("\n")) {
                Label item = monoLabel("  " + line);
                item.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.BAD + ";");
                problem.getChildren().add(item);
            }
            Label advice = monoLabel("  The months that did run are real. "
                    + "Save or reload before continuing.");
            advice.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            problem.getChildren().add(advice);
            column.getChildren().add(problem);
        }

        /* ---------------------------- headlines ---------------------------- */
        VBox headlines = reportSection("WHAT HAPPENED");

        for (String line : skip.getHeadlines()) {

            // The only cheerful headline is the "nothing went wrong" one, which
            // is the single line the list contains when it contains nothing else.
            boolean good = line.startsWith("Nothing went wrong");

            Label item = monoLabel("  " + line);
            item.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: "
                    + (good ? Palette.GOOD : Palette.BAD) + ";");
            headlines.getChildren().add(item);
        }
        column.getChildren().add(headlines);

        /* ------------------------------ deltas ------------------------------ */
        VBox changes = reportSection("THE CITY");
        addSkipLine(changes, "Population", skip.getStartPopulation(), skip.getEndPopulation(),
                skip.getPopulationChange(), false);
        addSkipLine(changes, "Cash", skip.getStartCash(), skip.getEndCash(),
                skip.getCashChange(), true);
        changes.getChildren().add(monoLabel(String.format("%-20s%+,.1f a month",
                "", skip.getCashPerMonth())));

        // Only when there was something to grow FROM. A city that went 0 -> 192
        // has no meaningful rate, and printing "+0.0% a year" beside "+192"
        // reads like a contradiction rather than a division guard.
        if (skip.getStartPopulation() > 0 && skip.getPopulationChange() != 0) {
            changes.getChildren().add(monoLabel(String.format("%-20s%+.1f%% a year",
                    "Growth", skip.getPopulationGrowthRate() * 100)));
        }
        column.getChildren().add(changes);

        VBox econ = reportSection("ECONOMY");
        addChangeLine(econ, "Monthly GDP", skip.getMonthlyGdpChange(), true, true);
        addChangeLine(econ, "Jobs", skip.getJobsChange(), false, true);
        addChangeLine(econ, "Housing", skip.getHousingChange(), false, true);

        // Debt is the one place where up is bad. Negating the VALUE to get the
        // colour right would print "-$48,074" on a city whose debt rose by
        // exactly that much, so the sign stays honest and only the colour flips.
        addChangeLine(econ, "City debt", skip.getCityDebtChange(), true, false);
        addChangeLine(econ, "Business debt", skip.getBusinessDebtChange(), true, false);

        // What the central bank advanced the treasury over the skip (0.7.15): a
        // skip runs through an empty treasury on its advances, as play does.
        if (skip.getAdvancedDuringSkip() > 0 || skip.getAdvancesOwedAtEnd() > 0) {
            Label adv = monoLabel(String.format("%-20s%s advanced, %s owed at the end",
                    "Central bank", money(skip.getAdvancedDuringSkip()), money(skip.getAdvancesOwedAtEnd())));
            adv.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.WARN + ";");
            econ.getChildren().add(adv);
        }

        if (skip.getWriteOffsDuringSkip() > 0) {
            // Firms default a slice at a time since 0.7.8, so there is a
            // figure most skips; amber only when it was more than a sound
            // book loses (TimeSkipReport.defaultsWereNews()).
            Label wo = monoLabel(String.format("%-20s%s written off by lenders",
                    "Defaults", money(skip.getWriteOffsDuringSkip())));
            wo.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: "
                    + (skip.defaultsWereNews() ? Palette.WARN : Palette.TEXT_MUTED) + ";");
            econ.getChildren().add(wo);
        }
        column.getChildren().add(econ);

        /* ------------------------------- land ------------------------------- */
        VBox land = reportSection("LAND",
                String.format("%-20s%s%s", "Bought", skip.getLandBlocksBought() < 0 ? "-" : "+",
                        LandManager.areaWords(Math.abs(skip.getLandBlocksBought()) * LandManager.BLOCK_SQ_FT)),
                String.format("%-20s%.1f%% used at the end",
                        "Utilisation", skip.getEndLandUtilisation() * 100));
        column.getChildren().add(land);

        /* ----------------------------- buildings ----------------------------- */
        java.util.List<TimeSkipReport.BuildingChange> built = skip.getBuildingChanges();

        VBox buildings = reportSection("BUILDINGS");
        if (built.isEmpty()) {
            buildings.getChildren().add(monoLabel("  nothing was built or lost"));
        } else {
            for (TimeSkipReport.BuildingChange change : built) {
                Label line = monoLabel(String.format("  %+d  %s", change.change, change.name));
                line.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: "
                        + (change.isGain() ? Palette.GOOD : Palette.BAD) + ";");
                buildings.getChildren().add(line);
            }
        }
        column.getChildren().add(buildings);

        /* ---------------------------- demolitions ---------------------------- */
        // Pulled from the log rather than the snapshots, because only the log
        // knows WHEN each one happened - and a hundred-month skip is long enough
        // that they would otherwise have aged off the side panel unseen.
        java.util.List<DemolitionLog.Entry> lost = new java.util.ArrayList<>();
        for (DemolitionLog.Entry entry : game.getDemolitionLog().all()) {
            if (entry.month > skip.getStartMonth()) {
                lost.add(entry);
            }
        }

        if (!lost.isEmpty()) {
            VBox demolished = reportSection("DEMOLISHED DURING THE SKIP");
            for (DemolitionLog.Entry entry : lost) {
                Label line = monoLabel(String.format("  month %,d: %,d x %s (%s)",
                        entry.month, entry.quantity, entry.building, entry.sector));
                line.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.BAD + ";");
                demolished.getChildren().add(line);
            }
            demolished.getChildren().add(monoLabel(
                    "  their owners could not afford to keep them, or the city pulled them down"));
            column.getChildren().add(demolished);
        }

        /* ------------------------------ health ------------------------------ */
        /*
         * An epidemic lasts three or four months and then decays to nothing, so
         * a city that lost a quarter of its output to one in the middle of a
         * twenty-month skip looks identical at both ends to a city that was well
         * throughout. That is the same argument the power and water samples were
         * added for, and a sharper case of it - a brownout at least tends to
         * persist long enough to still be there when the skip stops.
         */
        if (skip.getOutbreaks() > 0 || skip.getMonthsSick() > 0
                || skip.getPeakUnburied() > 0) {

            VBox illness = reportSection("HEALTH DURING THE SKIP");

            if (skip.getOutbreaks() > 0) {
                Label epidemic = monoLabel(String.format(
                        "%-20s%d, running for %d months in total",
                        "Outbreaks", skip.getOutbreaks(), skip.getMonthsInOutbreak()));
                epidemic.setStyle("-fx-font-family: " + Palette.mono() + ";"
                        + " -fx-font-weight: bold; -fx-text-fill: " + Palette.BAD + ";");
                illness.getChildren().add(epidemic);
            } else {
                illness.getChildren().add(monoLabel(
                        String.format("%-20s%s", "Outbreaks", "none")));
            }

            illness.getChildren().add(monoLabel(String.format(
                    "%-20s%.0f%% of the workforce, in the worst month",
                    "Worst absence", (1 - skip.getWorstWorkRatio()) * 100)));
            illness.getChildren().add(monoLabel(String.format(
                    "%-20s%d of %d", "Months below full", skip.getMonthsSick(),
                    skip.getCompleted())));

            if (skip.getPeakUnburied() > 0) {
                Label dead = monoLabel(String.format(
                        "%-20s%,.0f at its worst - build a cemetery or a crematorium",
                        "Left unburied", skip.getPeakUnburied()));
                dead.setStyle("-fx-font-family: " + Palette.mono() + ";"
                        + " -fx-font-weight: bold; -fx-text-fill: " + Palette.BAD + ";");
                illness.getChildren().add(dead);
            }
            column.getChildren().add(illness);
        }

        /* ---------------------------- households ---------------------------- */
        column.getChildren().add(reportSection("HOUSEHOLDS",
                String.format("%-20s%.1f%% of take-home", "Rent",
                        skip.getEndRentBurden() * 100),
                String.format("%-20s%.1f%%  (was %.1f%%)", "Saving rate",
                        skip.getEndSavingRate() * 100, skip.getStartSavingRate() * 100)));

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = keptScroller("showSimulateResultMenu", content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(520);
        scroll.setStyle("-fx-background-color:transparent;");

        Button again = new Button("Simulate more");
        again.setOnAction(e -> showSimulateMonthsMenu());

        Button done = new Button("Done");
        done.setOnAction(e -> openCity());

        rootMenu.getChildren().addAll(title, result, scroll, again, done);
    }

    /** "Population  192 -> 664  (+472)", coloured by direction. */
    private void addSkipLine(VBox section, String label,
                             double start, double end, double change, boolean isMoney) {

        String text = isMoney
                ? String.format("%-20s%s -> %s", label, money(start), money(end))
                : String.format("%-20s%,.0f -> %,.0f", label, start, end);

        section.getChildren().add(monoLabel(text));
        addChangeLine(section, "", change, isMoney, true);
    }

    /**
     * A signed change, coloured by whether it is good news.
     *
     * The sign always tells the truth about the direction; `higherIsBetter`
     * only decides the colour. Debt is the case that forces the distinction -
     * more of it is worse, but a line reading "-$48,074" on a city whose debt
     * went UP by that much is simply a lie in service of a colour.
     */
    private void addChangeLine(VBox section, String label, double change,
                               boolean isMoney, boolean higherIsBetter) {

        // Anything that rounds away to nothing IS nothing. Without this a change
        // of -0.004 prints as a red "-$0", which reads like a problem rather
        // than the rounding artefact it is.
        if (Math.abs(change) < .005) {
            change = 0;
        }

        String value = isMoney
                ? (change >= 0 ? "+" : "\u2212") + money(Math.abs(change))
                : String.format("%+,.0f", change);

        boolean good = higherIsBetter ? change > 0 : change < 0;
        boolean bad = higherIsBetter ? change < 0 : change > 0;

        Label line = monoLabel(String.format("%-20s%s", label, value));
        line.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: "
                + (bad ? Palette.BAD : good ? Palette.GOOD : Palette.TEXT_MUTED) + ";");
        section.getChildren().add(line);
    }

    /** Shared scaffolding for the sector report screens - of which none is left: Household money, the last, became a page of its own in 0.7.27, and nothing calls this now. */
    void showSectorReport(String title, VBox column, Runnable back) {
        showSectorReport(title, column, back, null);
    }

    /**
     * @param extra an optional button shown above Back, for screens that lead
     *              somewhere else - e.g. the industrial report linking to its
     *              financial statements. rootMenu is a VBox, so the buttons
     *              stack, which matches every other menu in the game.
     */
    void showSectorReport(String title, VBox column, Runnable back, Button extra) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scrollPane =
                keptScroller(currentScreen + ":report", content);
        scrollPane.setFitToWidth(true);
        /*
         * AS TALL AS THE WINDOW ALLOWS, for the reason scrolled() gives: 500
         * was set against a smaller window and now leaves a strip of empty
         * stage under every report while the report itself is scrolled in a
         * box. The 150 is the heading and the Back button; the floor of 320 is
         * for a window small enough that the arithmetic would go negative.
         *
         * KEYED TO THE SCREEN, not to the literal "showSectorReport". Fifteen
         * screens come through here and they were sharing one remembered
         * position, so leaving one of them half way down and opening another
         * put the second one half way down too.
         */
        scrollPane.prefHeightProperty().bind(javafx.beans.binding.Bindings.max(
                320, menuScroller.heightProperty().subtract(150)));
        scrollPane.setStyle("-fx-background-color:transparent;");

        /*
         * AND, IF SOMETHING ASKED TO BE SEEN, put it in view once.
         *
         * A screen that opens a panel below the fold has opened it where the
         * player cannot see it, and the click reads as having done nothing.
         *
         * TWO NODES, NOT ONE. What wants revealing is usually a stretch - the
         * grid you clicked and the panel that opened under it - and the two
         * ends want different things. The top wants to sit at the top of the
         * view; the bottom just has to be ON screen. When both fit, the top
         * wins and the panel lands underneath it; when they do not, the bottom
         * wins, because the bottom of a statement is its total and the top of a
         * grid is a row of column headings.
         *
         * ONE-SHOT on purpose: it fires on the click that opened the panel and
         * not on the monthly redraw, so advancing time does not yank a screen
         * the player has scrolled somewhere deliberately.
         */
        if (peopleScreen.revealTop != null) {
            javafx.scene.Node first = peopleScreen.revealTop;
            javafx.scene.Node last = peopleScreen.revealBottom == null ? peopleScreen.revealTop : peopleScreen.revealBottom;
            peopleScreen.revealTop = null;
            peopleScreen.revealBottom = null;
            javafx.application.Platform.runLater(() -> {
                scrollPane.applyCss();
                scrollPane.layout();
                double tall = content.getBoundsInLocal().getHeight();
                double view = scrollPane.getViewportBounds().getHeight();
                if (tall <= view) return;
                double base = content.localToScene(0, 0).getY();
                double topY = first.localToScene(0, 0).getY() - base - 8;
                double botY = last.localToScene(0, 0).getY() - base
                        + last.getBoundsInLocal().getHeight() + 8;
                double want = Math.max(topY, botY - view);
                scrollPane.setVvalue(Math.max(0, Math.min(1, want / (tall - view))));
            });
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> back.run());

        rootMenu.getChildren().addAll(heading, scrollPane);
        if (extra != null) {
            rootMenu.getChildren().add(extra);
        }
        rootMenu.getChildren().add(backButton);
    }


    /* =====================================================================
       THE FRAME FOLDS AWAY (0.7.24)

       Jerus chose "B: panels fold away", for every screen. On the 0.7.23
       walkthrough the three fixed things round every page - the City
       overview at the left, Under construction at the right and NEXT DUE
       along the foot - left the page about 55% of the window. Now:

         THE CITY OVERVIEW IS A DRAWER. The header's chip, "Needs you" and
           the count NEEDS YOU lists (CityNeeds, the panel's own list), amber
           or red by the worst of them as the panel colours them, "Nothing
           needs you" when there is none, opens the panel over the left of
           the stage - its Summary/Dashboard switch and every section as they
           were, opened on Summary's NEEDS YOU whatever mode is stored, since
           the chip counts that list (openOnNeeds(), after the PC check). It
           closes on the chip, its x or Esc, and on a click outside it or a
           change of screen unless it is pinned; the pin is kept
           (GamePrefs.isDrawerPinned()).
         UNDER CONSTRUCTION IS A TAB, 44 px down the stage's right edge: a
           crane, the count of sites (the panel's own "N site(s)"), and the
           words down it. A click opens the panel as it was, over the stage;
           its "Open ›" still goes to the construction page. Open or folded
           is kept (GamePrefs.isConstructionOpen()), folded by default.
         NEXT DUE is a card at the top of the Finances hub
           (FinancesScreen.nextDueCard(); beside the hub's ladder since
           0.7.32), and its red maturity a NEEDS YOU row, FALLS DUE.

       The stage gains the width: at 1,389 px it was about 743 px wide and is
       about 1,269 now, with the drawer and the tab closed.
       ===================================================================== */

    /** The chip, kept so a click outside the drawer can repaint it without rebuilding the header under the pointer. */
    private Region needsChipNode;
    private Runnable restyleNeedsChip;

    /**
     * The drawer's own bar: the pin and the close, refilled as the pin
     * changes. Made in start(), not here: a harness constructs this window
     * without the toolkit (BuildMenuCheck), and a node cannot be made
     * without it.
     */
    private HBox drawerBarBox;

    /** "Needs you" and the count, or "Nothing needs you": the drawer's door. */
    private Region needsChip() {
        List<CityNeeds.Need> biting = CityNeeds.biting(CityNeeds.measure(game, SummaryScreen.WORDS));
        int n = biting.size();
        int worst = n == 0 ? 0 : biting.get(0).level();
        // The panel's own colours: a red row is past its line, an amber one near it.
        String tone = worst >= 2 ? Palette.BAD : worst == 1 ? Palette.WARN : Palette.GOOD;

        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setMinWidth(Region.USE_PREF_SIZE);
        if (n == 0) {
            Label none = new Label("Nothing needs you");
            none.setStyle(Palette.words(12, Palette.TEXT_LABEL));
            none.setMinWidth(Region.USE_PREF_SIZE);
            chip.getChildren().addAll(Pieces.icon(Icons.TICK, tone, 14), none);
        } else {
            Label words = new Label("Needs you");
            words.setStyle(Palette.words(12, Palette.TEXT_HEAD));
            words.setMinWidth(Region.USE_PREF_SIZE);
            Label count = new Label(String.valueOf(n));
            count.setStyle(Fonts.monoSemiBold() + " -fx-font-size: 11px; -fx-text-fill: " + Palette.ON_FILL + ";"
                    + " -fx-background-color: " + tone + "; -fx-background-radius: 9; -fx-padding: 0 6 0 6;");
            count.setMinSize(18, 18);
            count.setAlignment(Pos.CENTER);
            chip.getChildren().addAll(Pieces.icon(Icons.ALERT, tone, 15), words, count);
        }
        String rest = "-fx-padding: 6 10 6 10; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        restyleNeedsChip = () -> chip.setStyle(rest + " -fx-background-color: "
                + (drawerOpen ? Palette.PINNED : Palette.RAISED) + "; -fx-border-color: "
                + (drawerOpen ? Palette.ACCENT : Palette.EDGE) + ";");
        restyleNeedsChip.run();

        StringBuilder said = new StringBuilder(n == 0 ? "Nothing is near a limit." : "NEEDS YOU:");
        for (int i = 0; i < Math.min(n, 8); i++) {
            said.append("\n").append(biting.get(i).label().toLowerCase()).append(" · ").append(biting.get(i).reading());
        }
        if (n > 8) said.append("\n...and ").append(n - 8).append(" more");
        said.append("\nClick for the City overview.");
        Tooltip tip = new Tooltip(said.toString());
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(chip, tip);
        chip.setOnMouseClicked(e -> {
            drawerOpen = !drawerOpen;
            if (drawerOpen) openOnNeeds();
            placeFrame();
        });
        needsChipNode = chip;
        return chip;
    }

    /**
     * The drawer as the chip opens it: on NEEDS YOU, the list the chip
     * counts - Summary whatever mode is stored (SummaryScreen.needsView, the
     * stored mode untouched), at the top of its scroller, where NEEDS YOU is
     * under the vitals and any alerts. Found on the PC: on Dashboard the chip
     * opened a panel with no NEEDS YOU in it.
     */
    private void openOnNeeds() {
        summaryScreen.needsView = true;
        panelScrollAt.put("city", 0.0);
        panelScrollAt.remove("city:bottom");
        summaryScreen.refreshCityPanel();
    }

    /** The drawer's bar: pin it open across screens, or close it. */
    private HBox drawerBar() {
        if (drawerBarBox == null) drawerBarBox = new HBox(6);
        drawerBarBox.setAlignment(Pos.CENTER_RIGHT);
        drawerBarBox.setStyle("-fx-padding: 8 10 0 12; -fx-background-color: " + Palette.PANEL + ";");
        return drawerBarBox;
    }

    private void refreshDrawerBar() {
        if (drawerBarBox == null) return;
        boolean pinned = prefs != null && prefs.isDrawerPinned();
        Label pin = new Label(pinned ? "Pinned" : "Pin");
        pin.setGraphic(Pieces.icon(Icons.PIN, pinned ? Palette.ACCENT : Palette.TEXT_LABEL, 14));
        pin.setGraphicTextGap(5);
        pin.setStyle(Palette.words(Palette.SIZE_LABEL, pinned ? Palette.ACCENT : Palette.TEXT_LABEL)
                + " -fx-cursor: hand; -fx-padding: 2 6 2 6;");
        Tooltip.install(pin, new Tooltip(pinned ? "Pinned: it stays open across screens. Click to unpin."
                : "Keep it open across screens"));
        pin.setOnMouseClicked(e -> {
            if (prefs == null) return;
            prefs.setDrawerPinned(!prefs.isDrawerPinned());
            prefs.save(game.getGameFiles());
            refreshDrawerBar();
        });
        Label close = new Label();
        close.setGraphic(Pieces.icon(Icons.CLOSE, Palette.TEXT_LABEL, 14));
        close.setStyle("-fx-cursor: hand; -fx-padding: 2 2 2 4;");
        Tooltip.install(close, new Tooltip("Close  (Esc)"));
        close.setOnMouseClicked(e -> {
            drawerOpen = false;
            placeFrame();
        });
        drawerBarBox.getChildren().setAll(pin, close);
    }

    /** The drawer, the construction panel and its tab, shown as the player left them - and none of it on the menus. */
    private void placeFrame() {
        if (drawer == null) return;
        boolean inCity = !isGameMenu(currentScreen);
        refreshDrawerBar();
        showIf(drawer, inCity && drawerOpen);
        if (restyleNeedsChip != null) restyleNeedsChip.run();
        boolean open = prefs != null && prefs.isConstructionOpen();
        showIf(constructionPanel, inCity && open);
        refreshConstructionTab(inCity, open);
    }

    /** Whether a node is this one or inside it. */
    private static boolean inside(javafx.scene.Node node, javafx.scene.Node in) {
        for (javafx.scene.Node n = node; n != null; n = n.getParent()) if (n == in) return true;
        return false;
    }

    /** How wide the construction panel's tab is. */
    static final double CONSTRUCTION_TAB = 44;

    /** The tab the construction panel folds to: a crane, how many sites, and the words down it. */
    private void refreshConstructionTab(boolean inCity, boolean open) {
        constructionTab.getChildren().clear();
        showIf(constructionTab, inCity);
        constructionTab.setPrefWidth(CONSTRUCTION_TAB);
        constructionTab.setMinWidth(CONSTRUCTION_TAB);
        constructionTab.setMaxWidth(CONSTRUCTION_TAB);
        constructionTab.setAlignment(Pos.TOP_CENTER);
        String rest = "-fx-padding: 10 0 10 0; -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 0 0 0 1;"
                + " -fx-cursor: hand; -fx-background-color: ";
        constructionTab.setStyle(rest + (open ? Palette.RAISED : Palette.PANEL) + ";");
        constructionTab.setOnMouseEntered(e -> constructionTab.setStyle(rest + Palette.RAISED + ";"));
        constructionTab.setOnMouseExited(e -> constructionTab.setStyle(rest + (open ? Palette.RAISED : Palette.PANEL) + ";"));

        Label chevron = new Label(open ? "›" : "‹");
        chevron.setStyle(Palette.words(14, Palette.TEXT_LABEL));

        // The panel's own count: the kinds of building on site ("N site(s)").
        int sites = game.getBuildingManager().getUnderConstruction();
        Label count = new Label(String.valueOf(sites));
        count.setStyle(Fonts.monoSemiBold() + " -fx-font-size: 11px; -fx-text-fill: " + Palette.ON_FILL + ";"
                + " -fx-background-color: " + (sites > 0 ? Palette.BUILDING : Palette.TEXT_SPENT) + ";"
                + " -fx-background-radius: 9; -fx-padding: 0 5 0 5;");
        count.setMinSize(20, 18);
        count.setAlignment(Pos.CENTER);

        Label words = new Label("UNDER CONSTRUCTION");
        words.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 10.5px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        words.setRotate(90);
        javafx.scene.Group down = new javafx.scene.Group(words);

        constructionTab.getChildren().addAll(chevron, Pieces.icon(Icons.CRANE, Palette.BUILDING, 20), count, down);
        Tooltip tip = new Tooltip(sites == 0 ? "Under construction: nothing on site. Click to " + (open ? "fold the panel." : "open the panel.")
                : "Under construction: " + sites + " site" + (sites == 1 ? "" : "s") + ". Click to "
                        + (open ? "fold the panel." : "open the panel."));
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(constructionTab, tip);
        constructionTab.setOnMouseClicked(e -> {
            if (prefs == null) return;
            prefs.setConstructionOpen(!prefs.isConstructionOpen());
            prefs.save(game.getGameFiles());
            placeFrame();
        });
    }

    /* =====================================================================
       CONSTRUCTION PANEL

       Ports the terminal build's per-stack construction readout - the
       "0/1 Coal Power Plant(s) finished construction. 177 month(s)." line - into
       a panel that's visible from every screen - since 0.7.24 behind a tab
       on the stage's right edge, open or folded as the player left it (THE
       FRAME FOLDS AWAY).

       It also surfaces something that was previously invisible: how the
       builders' output is divided between sites. It was divided evenly between
       *sites* (stacks), so every extra building type you queued slowed down
       everything already in progress; since 0.7.17 what the sites are left
       after the repairs goes to each building by the crew it can use
       (BuildingManager, EVERY BUILDING GETS THE CREW IT CAN USE), and since
       0.7.20 each site's months are the wait the quote reads
       (Game.onSiteMonths()), "at today's queue". The "N site(s), M
       building(s)" line and each site's months make that legible.
       ===================================================================== */

    private void refreshConstructionPanel() {
        constructionPanel.getChildren().clear();

        Label header = new Label("UNDER CONSTRUCTION");
        header.setGraphic(areaSwatch(Palette.BUILDING, 8));
        header.setGraphicTextGap(8);
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + "; -fx-padding: 0 0 4 2;");
        /*
         * OPEN, TO THE CONSTRUCTION PAGE (0.7.22): the panel keeps its summary,
         * and the page behind it has every site's order, crews, time and
         * money, and the player's hand on them (ConstructionScreen).
         */
        Label open = new Label("Open ›");
        open.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand; -fx-padding: 0 2 4 0;");
        open.setOnMouseClicked(e -> constructionScreen.show());
        Region headGap = new Region();
        HBox.setHgrow(headGap, Priority.ALWAYS);
        HBox headRow = new HBox(4, header, headGap, open);
        headRow.setAlignment(Pos.CENTER_LEFT);
        headRow.setMaxWidth(PANEL_TEXT + 8);
        constructionPanel.getChildren().add(headRow);

        BuildingManager buildingManager = game.getBuildingManager();
        List<BuildingsStacks> sites = buildingManager.getStacksUnderConstruction();

        int output = game.getConstructionOutput();
        int siteCount = buildingManager.getUnderConstruction();
        // What the sites are left after the repairs, shared by the crew each
        // building can use (0.7.17): each site's own share, which a site owed
        // less than its crews could do is capped at. See BuildingManager,
        // EVERY BUILDING GETS THE CREW IT CAN USE.
        int forSites = game.getBuildingOutput();
        int buildingsOnSite = 0;
        for (BuildingsStacks s : sites) buildingsOnSite += s.getUnderConstruction();

        // ...and the crews kept on, when the builders have laid some off
        // (sectors.Construction, THE CREWS THE WORK NEEDS).
        ham.citybuildersim.sectors.Construction builders = game.getSectors().construction();
        long postsStanding = builders.getPostsStanding(), postsKept = builders.getPostsOffered();
        Label capacity = monoLabel("Output: " + formatter.format(output) + " pts/mo"
                + (postsStanding > 0 && postsKept < postsStanding
                        ? " (crews kept on: " + formatter.format(postsKept) + " of " + formatter.format(postsStanding) + " posts)"
                        : ""));
        capacity.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        // Wrapped, not cut (0.7.20): the panel is narrower than these lines.
        capacity.setWrapText(true);
        capacity.setMaxWidth(PANEL_TEXT);
        // Its full height, or the panel squeezes it back to one cut line once
        // the build log below needs the room (0.7.20, seen on the PC at month 5).
        capacity.setMinHeight(Region.USE_PREF_SIZE);
        constructionPanel.getChildren().add(capacity);

        if (sites.isEmpty()) {
            ConstructionControl idleHand = buildingManager.getControl();
            boolean handOnIt = !idleHand.demolitions().isEmpty() || !idleHand.shells().isEmpty();
            Label idle = new Label(handOnIt
                    ? String.format("Nothing being built. %d demolition(s) on site, %d shell(s) stopped.",
                            idleHand.demolitions().size(), idleHand.shells().size())
                    : "Nothing being built.");
            idle.setWrapText(true);
            idle.setMaxWidth(PANEL_TEXT);
            idle.setStyle("-fx-text-fill: " + Palette.TEXT_FAINT + "; -fx-padding: 8 0 0 0;");
            if (handOnIt) {
                idle.setStyle(idle.getStyle() + " -fx-cursor: hand;");
                idle.setOnMouseClicked(e -> constructionScreen.show());
            }
            constructionPanel.getChildren().add(idle);
            addBuildLog();
            addDemolitionLog();
            return;
        }

        Label split = monoLabel(siteCount + " site(s), " + formatter.format(buildingsOnSite)
                + " building(s): " + formatter.format(forSites) + " after repairs, crews by what each building can use");
        split.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        split.setWrapText(true);
        split.setMaxWidth(PANEL_TEXT);
        split.setMinHeight(Region.USE_PREF_SIZE);
        constructionPanel.getChildren().add(split);

        VBox list = new VBox(12);
        list.setStyle("-fx-padding: 10 0 0 0;");

        // This month's crews, site by site, as the month will apply them (0.7.22).
        BuildingManager.Plan sitePlan = buildingManager.plan(forSites);

        for (BuildingsStacks site : sites) {

            int remaining = site.getUnderConstruction();
            int built = site.getQuantity();
            int pointsEach = site.getBuilding().getConstructionPoints();
            double progress = site.getConstructionProgress();

            // fraction of the NEXT building that's complete
            double fraction = (pointsEach > 0)
                    ? Math.max(0, Math.min(progress / pointsEach, 1.0))
                    : 0;

            Label name = new Label(site.getName());
            // The site's name in the text's colour, its bar in the building
            // area's (the stylesheet's progress bar) since 0.7.21 - and a
            // click opens the construction page at it (0.7.22).
            name.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 12px; -fx-text-fill: " + Palette.TEXT_HEAD + ";"
                    + " -fx-cursor: hand;");
            String siteKey = ConstructionControl.keyOf(site.getBuilding());
            name.setOnMouseClicked(e -> constructionScreen.showSite(siteKey));

            ProgressBar bar = new ProgressBar(fraction);
            bar.setPrefWidth(240);

            // Stalled when the site gets nothing this month - this month's
            // share as the month will apply it (BuildingManager.plan(): the
            // crews' rule, and with the player's hand on the queue the city's
            // order and its rushes) - and, when the rule would have given it
            // crews and the order gave them to a site ahead of it, waiting
            // for them; otherwise the one wait every screen reads.
            String eta;
            double perSite = sitePlan.shareOf(siteKey);
            if (perSite <= 0 && sitePlan.ruleOf(siteKey) > 0) {
                double monthsLeft = game.onSiteMonths(site.getBuilding());
                eta = "waiting for crews, "
                        + (Double.isNaN(monthsLeft) ? "stalled" : atTodaysQueue(monthsLeft));
            } else if (perSite <= 0) {
                // Two things can stall a site now, and they want different
                // words: nobody to do the work, or nothing able to reach it.
                eta = game.getInfrastructureManager().isCongested()
                        ? "stalled - gridlocked"
                        : "stalled - no workers";
            } else {
                /*
                 * THE SAME WAIT THE CARD AND THE QUOTE READ (0.7.20):
                 * Game.onSiteMonths(), so the panel, the build card's "N on
                 * site" and the order's quote cannot give one site three
                 * different times. It is a reading of today's queue, and
                 * says so: orders placed after it take crews from it. Since
                 * 0.7.22 (after the docs pass) it is the construction page's
                 * wait too - Game.siteMonths(), BuildingManager, ONE WAIT FOR
                 * A SITE: the rule's with the player's hand off the site, and
                 * with an order set or a rush on, the order's and the
                 * overtime's.
                 */
                double monthsLeft = game.onSiteMonths(site.getBuilding());
                eta = Double.isNaN(monthsLeft) ? "stalled - no output" : atTodaysQueue(monthsLeft);
            }

            Label detail = monoLabel(remaining + " left / " + built + " built - " + eta);
            detail.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            detail.setWrapText(true);
            detail.setMaxWidth(PANEL_TEXT);

            VBox row = new VBox(3);
            row.getChildren().addAll(name, bar, detail);
            list.getChildren().add(row);
        }

        // ...and the player's own hand on the queue, in a line (0.7.22): the
        // demolitions on site and the shells stopped, which the page lists.
        ConstructionControl control = buildingManager.getControl();
        if (!control.demolitions().isEmpty() || !control.shells().isEmpty()) {
            Label hand = monoLabel(String.format("%d demolition(s) on site, %d shell(s) stopped",
                    control.demolitions().size(), control.shells().size()));
            hand.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px; -fx-text-fill: "
                    + Palette.TEXT_MUTED + "; -fx-cursor: hand;");
            hand.setWrapText(true);
            hand.setMaxWidth(PANEL_TEXT);
            hand.setOnMouseClicked(e -> constructionScreen.show());
            list.getChildren().add(hand);
        }

        javafx.scene.control.ScrollPane scroller = keptPanelScroller("construction", list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(560);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);

        addBuildLog();
        addDemolitionLog();
    }

    /** A small square in an area's colour, before a heading (0.7.21): the pages' titles' swatch, smaller. */
    static Region areaSwatch(String colour, double size) {
        Region swatch = new Region();
        swatch.setMinSize(size, size);
        swatch.setPrefSize(size, size);
        swatch.setMaxSize(size, size);
        swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        return swatch;
    }

    /** How wide the construction panel's lines wrap: the panel less its padding. */
    private static final double PANEL_TEXT = 256;

    /**
     * What the city has lost lately, under what it is building.
     *
     * Deliberately in the same panel as construction rather than on a screen of
     * its own: these are the two halves of the same thing, and a player watching
     * their city go up should see it come down in the same place. Entries stay
     * for two years of turns because someone fast-forwarding fifty months
     * otherwise has to reconstruct what happened from a building count that went
     * down while they were not looking.
     */
    private void addDemolitionLog() {

        java.util.List<DemolitionLog.Entry> lost =
                game.getDemolitionLog().recent(game.getMonth());

        if (lost.isEmpty()) {
            return;
        }

        Label header = new Label("RECENTLY DEMOLISHED");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-text-fill: " + Palette.BAD + "; -fx-padding: 14 0 2 0;");
        constructionPanel.getChildren().add(header);

        VBox list = new VBox(6);

        for (DemolitionLog.Entry entry : lost) {

            Label what = new Label(String.format("%,d x %s", entry.quantity, entry.building));
            what.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Palette.BAD_SOFT + ";");

            // Fading with age, so the eye goes to what just happened without the
            // older entries disappearing entirely.
            int ago = entry.monthsAgo(game.getMonth());
            String shade = (ago <= 1) ? Palette.BAD : (ago <= 6) ? Palette.TEXT_LABEL : Palette.TEXT_FAINT;

            Label when = monoLabel("  " + entry.sector + ", " + entry.when(game.getMonth()));
            when.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px;"
                    + " -fx-text-fill: " + shade + ";");

            // The player's own demolitions (0.7.22) are the city's order, not a
            // plot given back - a bought one's ground was paid for in the buy-out.
            boolean ordered = entry.sector != null && entry.sector.startsWith("City");
            Label how = monoLabel(ordered
                    ? (entry.wasPaidFor()
                            ? String.format("  bought out, its ground %s", money(entry.proceeds))
                            : "  demolished by the city's order")
                    : entry.wasPaidFor()
                    ? String.format("  plot sold back for %s", money(entry.proceeds))
                    : "  plot abandoned");
            how.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px;"
                    + " -fx-text-fill: " + Palette.TEXT_FAINT + ";");

            VBox row = new VBox(1);
            row.getChildren().addAll(what, when, how);
            list.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane scroller = keptPanelScroller("demolition", list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(Math.min(190, 62 * lost.size() + 8));
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);
    }

    /**
     * What the city has GAINED lately, above what it has lost.
     *
     * Deliberately the same panel, the same two-year window and the same fading
     * as the demolition log, because they are two halves of one question - what
     * changed while I was not watching - and a player comparing them should not
     * have to hold two different clocks in their head.
     *
     * Above rather than below: things going up is the more common event and the
     * more expected one, so it reads in the order the eye already travels -
     * under construction, then opened, then lost.
     */
    private void addBuildLog() {

        java.util.List<BuildLog.Entry> built =
                game.getBuildLog().recent(game.getMonth());

        if (built.isEmpty()) {
            return;
        }

        // In the building colour since 0.7.21: a building opening is a log
        // entry, not a verdict, and green is for a verdict.
        Label header = new Label("RECENTLY BUILT");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-text-fill: " + Palette.BUILDING + "; -fx-padding: 14 0 2 0;");
        constructionPanel.getChildren().add(header);

        VBox list = new VBox(6);

        for (BuildLog.Entry entry : built) {

            Label what = new Label(String.format("%,d x %s", entry.quantity, entry.building));
            what.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Palette.TEXT_HEAD + ";");

            // Fading with age, same as demolitions, so the eye goes to what just
            // happened without older entries disappearing entirely.
            int ago = entry.monthsAgo(game.getMonth());
            String shade = (ago <= 1) ? Palette.BUILDING : (ago <= 6) ? Palette.TEXT_LABEL : Palette.TEXT_FAINT;

            Label when = monoLabel("  opened " + entry.when(game.getMonth())
                    + " (" + CityCalendar.formatShort(entry.month) + ")");
            when.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px;"
                    + " -fx-text-fill: " + shade + ";");

            VBox row = new VBox(1);
            row.getChildren().addAll(what, when);
            list.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane scroller = keptPanelScroller("build", list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(Math.min(190, 44 * built.size() + 8));
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);
    }


    /* =====================================================================
       THE RAIL

       0.7.21, AS THE MOCKUPS DRAW IT: 76 wide at the window's left edge,
       each button an icon over its name, so the rail reads without learning
       its glyphs. The one that is showing is on the raised ground, with a
       bar down its edge and its icon in its AREA's colour - building and
       land pink, people teal, business violet, money blue (areaOf()) - the
       same colour as its page title's swatch and its header tile. The
       names are the game's own, short enough for the button: "Land office",
       "Infrastructure" and "Finances" where the mockups had Land, Transport
       and Finance; "Sectors", "Trade" and "History" stand for the pages
       titled Sector economy, Trade & the world and City History, whose full
       names are the tooltips. At the foot, Menu: the game menu, which the
       gear there opened and which was all it did; Settings is on it.
       ===================================================================== */

    /** The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). */
    private static final double RAIL_WIDTH = Palette.RAIL;

    /** A rail button's height, and the least it may shrink to on a short window. */
    private static final double RAIL_BUTTON = 54;
    private static final double RAIL_BUTTON_MIN = 40;

    /** How big a rail icon is drawn: its 24-unit grid at 20 pixels. */
    private static final double RAIL_ICON = 20;

    /**
     * One destination.
     *
     * @param key    what the highlight matches on
     * @param svg    a drawn outline, because the alternative was emoji and a
     *               colour cartoon in the middle of an instrument panel is
     *               the one thing that would look wrong in a Steam screenshot
     * @param label  the name under the icon, short enough for the button
     * @param name   what the tooltip says: the page's full name
     */
    private record Tab(String key, String svg, String label, String name, Runnable go) { }

    /**
     * Which of the four areas a rail key is in (0.7.21): building and land,
     * people and services, business and trade, money and policy. Its colour
     * is on the rail button, the page title's swatch and the header tile.
     */
    static String areaOf(String key) {
        switch (key) {
            case "build": case "land":
                return Palette.BUILDING;
            case "population": case "services": case "infrastructure":
                return Palette.PEOPLE;
            case "sector": case "trade":
                return Palette.BUSINESS;
            case "government": case "finances": case "bank": case "policy": case "reports":
                return Palette.MONEY;
            default:
                return null;
        }
    }

    /**
     * A page's title, with its area's swatch before it (0.7.21): a small
     * square in the colour of the rail tab that owns the screen showing, as
     * the mockups set the title. A page no tab owns - Settings, the save
     * menus - has no swatch. Every screen's title is made here.
     */
    Label pageTitle(String text) {
        Label title = new Label(text);
        title.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
        String area = areaOf(tabFor(currentScreen));
        if (area != null) {
            Region swatch = new Region();
            swatch.setMinSize(10, 10);
            swatch.setPrefSize(10, 10);
            swatch.setMaxSize(10, 10);
            swatch.setStyle("-fx-background-color: " + area + "; -fx-background-radius: 3;");
            title.setGraphic(swatch);
            title.setGraphicTextGap(10);
        }
        return title;
    }

    /**
     * The rail, in the order a city is actually run.
     *
     * Build first because it is what a new player does first, then the ground
     * to build on, then the money, then the state, then the two screens that
     * only report. The graph is last because it is the one you go to when you
     * have already decided something is wrong.
     *
     * The rail, and every entry is a PLACE.
     *
     * The first version had eleven tabs of which two - Economy and Sector -
     * were menus of other menus, and three real systems of the game had no home
     * at all: healthcare was behind Buildings > Services, the schools were
     * reachable only through a tuition policy, and the construction authority
     * that decides whether anything gets built was three clicks inside "sector
     * info". A rail whose entries are menus is the old main screen turned
     * sideways.
     *
     * So: no tab opens a list of other tabs. Economy split in two along the
     * line the game itself draws - what the STATE takes and spends, and what
     * the BUSINESSES earn - which is the division Jerus asked for and the one
     * an accountant would make. Finance absorbed the bank, because credit is
     * the price of money and the bank is one screen. Trade stayed separate,
     * because foreign exchange, the balance of payments and foreign debt are a
     * subsystem with three screens of their own.
     *
     * Ordered by what a city needs in the order it needs it: build the place,
     * buy the ground, count the people, serve them, then the two economies,
     * then the money, then the world, then the rules, then the record.
     */
    private Tab[] tabs() {
        return new Tab[] {
            new Tab("build",      Icons.BUILD,      "Build",       "Build",              buildScreen::showBuildMenu),
            new Tab("land",       Icons.LAND,       "Land office", "Land office",        landScreen::showLandMenu),
            new Tab("population", Icons.POPULATION, "People",      "People",             peopleScreen::showPopulationInfoMenu),
            new Tab("services",   Icons.SERVICES,   "Services",    "Services",           servicesScreen::showServicesStatsMenu),
            /*
             * INFRASTRUCTURE IS ITS OWN TAB (2026-09-17). Jerus: "add a tab
             * called infrastruture or transporation and basically it shows all
             * the info about transportation and buses, and even a sub tab for
             * specific info about the rail sector system."
             *
             * Nine batches of transport shipped before this tab existed and
             * not one of them was on a screen. The road's throughput was a
             * percentage on a panel with no page behind it; the three streams
             * it is made of could not be seen at all; the fare was a dial in
             * TaxPolicy with NOTHING anywhere that could turn it; the railway
             * had a sector page like any other sector and nothing that said
             * what it was doing to the band; and the fleets a sector's
             * operating rate now depends on were invisible, so a player whose
             * mill was running at 65% had no way to find out why.
             *
             * It sits after Services because that is where the eye goes
             * looking for it - roads and buses are things the city builds for
             * people - and before the two economies, because the railway is a
             * business and this tab is where a player finds out whether to
             * care about it.
             */
            new Tab("infrastructure", Icons.INFRASTRUCTURE, "Infrastructure", "Infrastructure",
                    infrastructureScreen::showInfrastructureMenu),
            new Tab("sector",     Icons.SECTOR,     "Sectors",     "Sector economy",     sectorScreen::showSectorMenu),
            new Tab("government", Icons.GOVERNMENT, "Government",  "Government economy", governmentScreen::showGovernmentMenu),
            new Tab("finances",   Icons.FINANCES,   "Finances",    "Finances",           financesScreen::showFinanceMenu),
            /*
             * THE BANK IS ITS OWN TAB NOW. Jerus: "i think we are going to have
             * 11 rails, bank is its own thing."
             *
             * It was a button at the foot of Finances, and that was defensible
             * while it was one screen of statements a player consulted before
             * borrowing. It is not: the bank is the counterparty to every loan
             * in the city, it has its own capital, its own funding, its own
             * failure mode and its own bailout - and what it costs to run and
             * to fund is in the price of every loan in the city (since 0.7.7;
             * until then, when it was strained every borrower paid for it).
             * That is a subsystem, and a
             * subsystem reached by scrolling to the bottom of another screen is
             * a subsystem the player finds once and never again.
             */
            new Tab("bank",       Icons.BANK,       "Bank",        "The bank",           bankScreen::showBankMenu),
            new Tab("trade",      Icons.TRADE,      "Trade",       "Trade & the world",  tradeScreen::showForeignMenu),
            new Tab("policy",     Icons.POLICY,     "Policy",      "Policy",             policyScreen::showPolicyMenu),
            new Tab("reports",    Icons.REPORTS,    "History",     "City History",       historyScreen::showHistoryMenu),
        };
    }

    /**
     * Which tab owns the screen that is showing.
     *
     * Keyed off currentScreen, which clearMenu already records - so a screen
     * three levels down inside Economy still lights the Economy tab, and the
     * player can see where they are rather than only where they can go.
     *
     * A screen not in here lights nothing, which is correct: the save menu and
     * the debt-issuance flow are not places on the rail.
     */
    private String tabFor(String screen) {
        switch (screen) {
            case "handleAllBuildingMenus": case ConstructionScreen.SCREEN:
            case "showNoDepositMenu": case "showNoLandMenu":
            case "showBuildFunding": case "showBuildFellShort":
            case "showNoLicenceMenu": case "showNoCoastMenu":
                return "build";
            /*
             * ...AND ITS FUNDING PAGE AND THE PAGE AFTER IT (0.7.26): the
             * office's own pages lit nothing on the rail and had no swatch
             * on their title, as the build tab's refusal pages always had.
             */
            case "showLandMenu": case "showLandFunding": case "showLandFellShort":
                return "land";
            case "showPopulationInfoMenu": case "showHouseholdMenu":
                return "population";
            case "showServicesStatsMenu":
                return "services";
            case "showInfrastructureMenu":
                return "infrastructure";
            /*
             * THE UTILITIES' BOOKS AND THE BUILDERS' BOOKS ARE BUSINESS, and
             * light the business tab even though Services links to them.
             * Jerus: "construction goes in the business, not service, in this
             * game construction is private." Both of them have revenue, cash
             * and a net income; what belongs on Services is whether the lights
             * are on, which is a different question with a different answer.
             */
            /*
             * ONE SCREEN NOW. Seven of these were separate destinations - a
             * commercial screen, its financials, an industrial screen, its
             * financials, mining, heavy industry, construction - and every one
             * of them printed its own version of a set of books in its own
             * shape. They are one screen with a strip across the top, so this
             * case is one name instead of eight.
             */
            case "showSectorMenu":
                return "sector";
            case "showGovernmentMenu":
                return "government";
            // The default page is Finances' own since 0.7.32, reached from
            // Borrow › Abroad; it lit Trade (the Finances spec's B7).
            case "showFinanceMenu": case "showDebtResultMenu": case "showForeignDefaultMenu":
                return "finances";
            case "showBankMenu":
                return "bank";
            case "showForeignMenu":
                return "trade";
            case "showPolicyMenu":
                return "policy";
            case "showHistoryMenu":
                return "reports";
            default:
                return "";
        }
    }

    private void refreshTabRail() {

        if (tabRail == null) return;
        showIf(tabRail, !isGameMenu(currentScreen));
        tabRail.getChildren().clear();

        String active = tabFor(currentScreen);

        for (Tab tab : tabs()) {
            final Tab t = tab;
            tabRail.getChildren().add(railButton(t.svg(), t.label(), t.name(), areaOf(t.key()),
                    t.key().equals(active), () -> goHome(t)));
        }

        // Menu sits apart from the destinations because it is not one - it
        // leaves the city rather than moving around inside it. It replaced
        // the gear (0.7.21), which only ever opened this same menu.
        Region gap = new Region();
        gap.setMinHeight(0);
        VBox.setVgrow(gap, Priority.ALWAYS);
        tabRail.getChildren().add(gap);
        tabRail.getChildren().add(railButton(Icons.MENU, "Menu", "The game menu: save, load, settings, quit  (Esc)",
                Palette.TEXT_HEAD, "showMainMenu".equals(currentScreen), this::showMainMenu));
    }

    /* =====================================================================
       THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF

       Jerus, 2026-09-14: "for the rail system, i need it so that if you press
       teh rail again, it takes you to that main area for that rail if you get
       what i mean".

       WHAT IT DID INSTEAD, AND WHY IT LOOKED LIKE NOTHING. Every section
       remembers its own sub-page - openSector, sectorPage, financePage,
       bankPage, tradePage, policyPage, servicePage, buildCategory - and its
       entry point honours that memory. showSectorMenu() opens with

           if (openSector != null) { drawSectorScreen(); return; }

       so pressing Sector while the mining screen was up ran the method, took
       the early return, and redrew the mining screen. The rail was working
       perfectly and doing nothing, which is the worst way for a control to
       fail: the player presses it twice, sees no change, and concludes it is
       broken.

       That memory is RIGHT for coming back from somewhere else and wrong for
       the rail, because the rail is the one control that means "take me to the
       top of this part of the game". Everything else - the strips across the
       tops of these screens, the links between them - still lands wherever it
       is pointed. See resetSection().

       BUILD IS THE EXCEPTION since 0.7.20: it keeps its category for the
       session - Jerus's play-through came back to Residential every time -
       and the rail still lands at the top of the page.
       ===================================================================== */

    /**
     * Press a tab: forget where you were inside it - Build's category apart,
     * since 0.7.20 - and land at the top.
     *
     * `tab.go().run()`, AND THE .run() IS THE WHOLE THING. Tab is a record, so
     * tab.go() is the ACCESSOR - it hands back the Runnable and does not call
     * it. The first version of this said `tab.go();`, which compiles without a
     * murmur because discarding a return value is legal Java, and shipped a
     * rail where every button did nothing at all. Jerus, one build later: "now
     * i cant press anything other than the building rail button" - he was not
     * stuck on Build, he was stuck FULL STOP, on whatever screen he happened to
     * be on when it landed.
     *
     * The old line read `railButton(..., tab.go())` and was correct for the
     * opposite reason: it was PASSING the Runnable, not calling it. Moving the
     * call one level up turned a correct accessor into a silent no-op.
     */
    private void goHome(Tab tab) {
        resetSection(tab.key());
        railJump = true;            // clearMenu: treat this as arriving, not redrawing
        tab.go().run();
    }

    /**
     * A section's own idea of where you were, forgotten.
     *
     * TWO LAYERS, NOT ONE, and the first draft of this only cleared the second.
     * Five of these sections have an AREA as well as a page - financeArea,
     * bankArea, tradeArea (since 0.7.35 only a page named from outside,
     * read once), policyArea, serviceArea, and openSector doing the
     * same job for Sector - and the area is what the entry point checks:
     *
     *     if (bankArea == null) overviewPage(page, fresh); else ...pages
     *
     * Resetting only bankPage would have reset which chip was lit inside an
     * area the player was still stuck in. Four of the five use null for the
     * landing, Services uses a named one, and Build keyed off its category
     * until 0.7.20, when it began keeping it (its case below).
     *
     * Each case restores the field to the SAME constant its declaration uses,
     * so the two cannot drift apart - a reset that said "Overview" in one place
     * and an initialiser that said "Summary" in the other would be a bug nobody
     * would find for months.
     */
    private void resetSection(String key) {
        switch (key) {
            // Build keeps its category for the session (0.7.20): Jerus's play-through
            // came back to Residential every time. The rail still lands at the top.
            case "build":      break;
            case "sector":     sectorScreen.openSector  = null;
                               sectorScreen.sectorPage  = SectorScreen.SECTOR_HOME;    break;
            case "finances":   financesScreen.financeArea = null;
                               financesScreen.financePage = FinancesScreen.FINANCE_HOME;   break;
            case "bank":       bankScreen.bankArea    = null;
                               bankScreen.bankPage    = BankScreen.BANK_HOME;      break;
            case "trade":      tradeScreen.tradeArea   = null;
                               tradeScreen.tradePage   = TradeScreen.TRADE_HOME;     break;
            case "policy":     policyScreen.policyArea  = null;
                               policyScreen.policyPage  = PolicyScreen.POLICY_HOME;    break;
            case "services":   servicesScreen.serviceArea = ServicesScreen.SERVICE_AREA_HOME;
                               servicesScreen.servicePage = ServicesScreen.SERVICE_HOME;   break;
            default: break;     // land, population, government, reports hold none
        }
    }

    /**
     * Set for exactly one clearMenu, by goHome().
     *
     * The scroll-keeping rule is "the same screen redrawn keeps its position",
     * and pressing the rail on the section you are already in IS the same
     * screen - so without this you would reset the sub-page and then be left
     * looking at the middle of it. Arriving from the rail is arriving.
     */
    private boolean railJump;

    /**
     * One button on the rail: its icon over its name (0.7.21).
     *
     * DRAWN, NOT TYPED. This used to be a Label carrying a Unicode glyph, with
     * the font family named explicitly because the platform default has no gear
     * at U+2699 and would have drawn an empty box. An SVGPath has no font, so
     * it cannot be missing a character, and it takes its colour from the stroke
     * rather than from a text fill - which is what lets the same marks be
     * grey, in their area's colour when showing, and paler on hover without
     * three copies of anything.
     *
     * STROKED, NOT FILLED. These are outline icons: fill null, 2px stroke on
     * the 24-unit grid, round caps and joins, exactly as Lucide draws them.
     * Filling them would turn every one into a solid blob.
     */
    private VBox railButton(String svg, String label, String name, String area, boolean active, Runnable go) {

        javafx.scene.shape.SVGPath mark = new javafx.scene.shape.SVGPath();
        mark.setContent(svg);
        mark.setFill(null);
        mark.setStrokeWidth(2);
        mark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        mark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        mark.setStroke(javafx.scene.paint.Color.web(active ? area : Palette.TEXT_LABEL));
        /*
         * PINNED TO THE 24-GRID, in a Pane of exactly that size.
         *
         * An SVGPath's layout bounds are the bounds of the INK, not of the
         * drawing it was designed on - so a globe that fills its 24 square and a
         * graph that uses two thirds of one would be centred to different sizes
         * and sit at different heights, and a rail of those never lines up. A
         * Pane does not resize or move its children, so each icon draws at the
         * coordinates Lucide gave it inside a box the same size for all of them.
         */
        javafx.scene.layout.Pane box = new javafx.scene.layout.Pane(mark);
        box.setPrefSize(24, 24);
        box.setMinSize(24, 24);
        box.setMaxSize(24, 24);
        box.setScaleX(RAIL_ICON / 24);
        box.setScaleY(RAIL_ICON / 24);

        Label word = new Label(label);
        String wordRest = "-fx-font-size: 10.5px; -fx-text-fill: ";
        word.setStyle(wordRest + (active ? Palette.TEXT_HEAD : Palette.TEXT_MUTED) + ";");
        word.setMinWidth(Region.USE_PREF_SIZE);

        VBox cell = new VBox(1, box, word);
        cell.setAlignment(Pos.CENTER);
        cell.setPrefSize(RAIL_WIDTH, RAIL_BUTTON);
        cell.setMinSize(RAIL_WIDTH, RAIL_BUTTON_MIN);
        cell.setMaxSize(RAIL_WIDTH, RAIL_BUTTON);
        // The one showing is raised, with a bar down its outer edge in its
        // area's colour - the page it is showing is that area's.
        String bar = "-fx-border-width: 0 0 0 3; -fx-border-color: transparent transparent transparent ";
        cell.setStyle(active
                ? "-fx-background-color: " + Palette.RAISED + "; " + bar + area + ";"
                : bar + "transparent; -fx-cursor: hand;");

        Tooltip tip = new Tooltip(name);
        tip.setShowDelay(Duration.millis(400));
        Tooltip.install(cell, tip);

        cell.setOnMouseClicked(e -> go.run());
        if (!active) {
            cell.setOnMouseEntered(e -> {
                cell.setStyle("-fx-background-color: " + Palette.CONTROL + "; " + bar + "transparent; -fx-cursor: hand;");
                mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_HEAD));
                word.setStyle(wordRest + Palette.TEXT_LABEL + ";");
            });
            cell.setOnMouseExited(e -> {
                cell.setStyle(bar + "transparent; -fx-cursor: hand;");
                mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_LABEL));
                word.setStyle(wordRest + Palette.TEXT_MUTED + ";");
            });
        }
        return cell;
    }

    /* =====================================================================
       THE INBOX

       Jerus: "the messages about issues, i think they should be in an inbox...
       and when a new one urgent pops it displays only the title until you
       click, and then it stays in the inbox."

       Which is three separate behaviours, and the third is the one that makes
       it an inbox rather than a banner with extra steps:

       1. AT REST it is an envelope with a count. Nothing else. A warning system
          that occupies the screen when there is nothing wrong is a warning
          system players stop reading.
       2. A NEW URGENT NOTICE shows its title, and only its title - loud
          enough to be seen from another screen, quiet enough not to be a
          dialog box in the way of what you were doing. Since 0.7.20 it is a
          toast in the bottom-right corner that fades (see TOASTS); it hung
          under the envelope, over the strips, until then.
       3. IT STAYS. Read notices stay in the list. Resolved ones stay, greyed,
          for two years. The old banners were gone the moment they were
          dismissed, so "what was that warning three months ago" had no answer.
       ===================================================================== */

    /** See refreshInbox: sized to the notice bodies, not to the corner. */
    private static final double INBOX_WIDTH = 530;

    /** Whether the list is dropped down. Not saved: it is about this window. */
    private boolean inboxOpen = false;

    /** Which notice's body is unfolded, by key. Empty for none. */
    private String inboxExpanded = "";

    private void refreshInbox() {

        refreshInboxButton();
        if (inboxCorner == null) return;
        showIf(inboxCorner, !isGameMenu(currentScreen) && inboxOpen);
        inboxCorner.getChildren().clear();
        inboxCorner.setAlignment(Pos.TOP_RIGHT);

        /* ----------------- what an urgent notice gets -----------------
         *
         * A toast, bottom right, since 0.7.20 - not a line under the envelope,
         * which sat over every page's strip for as long as it was unread. See
         * TOASTS.
         */
        refreshToasts();

        if (!inboxOpen) return;

        /* --------------------------- the list --------------------------- */
        VBox list = new VBox(3);
        list.setStyle("-fx-background-color: " + Palette.FIELD + "; -fx-background-radius: 4;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 4;"
                + " -fx-padding: 8 8 8 8;");
        /*
         * WIDE ENOUGH FOR THE MESSAGE, which is the only constraint that
         * matters here. The notice bodies are hand-wrapped at about 62
         * characters - they were written for a banner - so the box has to be
         * whatever 62 characters of the body face come to, plus the padding and
         * the coloured rule down the left. At 360 the lines came out as
         * "infants die at 1.2x the..." which is a warning with its argument cut
         * off.
         *
         * Jerus, 2026-09-09: "the inbox, i think the letters should be a tad
         * bigger." So the face went from 10px to 11.5px Courier - about 6.9px a
         * character against 6 - and 62 characters went from ~375px to ~430px.
         * The width is that plus the chrome, which is why it moved with the
         * type rather than staying where it was: leaving it at 470 would have
         * traded a readable warning for a clipped one.
         */
        list.setPrefWidth(INBOX_WIDTH);
        list.setMaxWidth(INBOX_WIDTH);

        Label heading = new Label("INBOX");
        heading.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + Palette.TEXT_MUTED + "; -fx-padding: 0 0 4 2;");
        list.getChildren().add(heading);

        if (game.getInbox().size() == 0) {
            Label none = new Label("Nothing has gone wrong yet.");
            none.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Palette.TEXT_FAINT + "; -fx-padding: 2 0 2 2;");
            list.getChildren().add(none);
        }

        for (Notice notice : game.getInbox().newestFirst()) {
            list.getChildren().add(noticeRow(notice));
        }

        javafx.scene.control.ScrollPane scroller =
                new javafx.scene.control.ScrollPane(list);
        scroller.setFitToWidth(true);
        scroller.setMaxHeight(460);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        inboxCorner.getChildren().add(scroller);
    }

    /* ------------------------- the envelope -------------------------
     *
     * AT REST IT IS AN ENVELOPE WITH A COUNT (rule 1 above), at the header's
     * right since 0.7.21: the mockups' button, the unread count as a badge on
     * its corner - red while something urgent is unread, grey otherwise. A
     * click drops the list down at the stage's top right, or puts it away.
     */
    private void refreshInboxButton() {
        if (inboxHolder == null || game.getInbox() == null) return;
        Inbox inbox = game.getInbox();
        Notice urgent = inbox.urgent();
        int unread = inbox.unread();

        javafx.scene.shape.SVGPath mark = new javafx.scene.shape.SVGPath();
        mark.setContent(Icons.MAIL);
        mark.setFill(null);
        mark.setStrokeWidth(2);
        mark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        mark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        mark.setStroke(javafx.scene.paint.Color.web(urgent != null ? Palette.BAD : Palette.TEXT_LABEL));
        javafx.scene.layout.Pane glyph = new javafx.scene.layout.Pane(mark);
        glyph.setPrefSize(24, 24);
        glyph.setMinSize(24, 24);
        glyph.setMaxSize(24, 24);
        glyph.setScaleX(.75);
        glyph.setScaleY(.75);

        StackPane envelope = new StackPane(glyph);
        envelope.setMinSize(40, 40);
        envelope.setPrefSize(40, 40);
        envelope.setMaxSize(40, 40);
        envelope.setStyle("-fx-background-color: " + (inboxOpen ? Palette.PINNED : Palette.RAISED) + ";"
                + " -fx-background-radius: 8; -fx-border-color: " + (inboxOpen ? Palette.ACCENT : Palette.EDGE) + ";"
                + " -fx-border-radius: 8; -fx-cursor: hand;");
        if (unread > 0) {
            Label count = new Label(unread > 99 ? "99+" : String.valueOf(unread));
            count.setStyle(Fonts.sansSemiBold() + " -fx-font-size: 11px; -fx-text-fill: white;"
                    + " -fx-background-color: " + (urgent != null ? Palette.BAD : Palette.TEXT_SPENT) + ";"
                    + " -fx-background-radius: 9; -fx-padding: 0 5 0 5;");
            count.setMinSize(18, 18);
            count.setAlignment(Pos.CENTER);
            StackPane.setAlignment(count, Pos.TOP_RIGHT);
            count.setTranslateX(6);
            count.setTranslateY(-6);
            count.setMouseTransparent(true);
            envelope.getChildren().add(count);
        }
        envelope.setOnMouseClicked(e -> {
            inboxOpen = !inboxOpen;
            refreshInbox();
        });
        Tooltip.install(envelope, new Tooltip(inbox.size() == 0 ? "The inbox: nothing to report"
                : "The inbox: " + inbox.size() + (unread > 0 ? ", " + unread + " unread" : "")));
        inboxHolder.getChildren().setAll(envelope);
    }

    /**
     * One notice: its title, and its body when it is unfolded.
     *
     * THE MESSAGE IS THE REAL ONE. It says the same thing the banner said, with
     * this month's figures in it, because a warning that has been softened into
     * a headline is a warning that cannot be acted on - the numbers were the
     * argument.
     *
     * ONE BUTTON, and it goes to the screen that fixes it rather than fixing it
     * from here. The old banners each carried two or three - pay, manage,
     * dismiss - and dismiss is now what closing the inbox means, while pay and
     * manage are both on the screen the button opens.
     */
    private VBox noticeRow(Notice notice) {

        boolean open = notice.getKey().equals(inboxExpanded);
        boolean done = notice.isResolved();

        Label title = new Label((done ? "✓  " : notice.isRead() ? "     " : "[!]  ")
                + notice.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(INBOX_WIDTH - 40);
        title.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: "
                + (done ? Palette.TEXT_SPENT : notice.isRead() ? Palette.TEXT_BODY : Palette.BAD_SOFT) + ";");

        Label when = new Label(done
                ? CityCalendar.format(notice.getRaised()) + "  ·  settled "
                        + CityCalendar.format(notice.getResolved())
                : CityCalendar.format(notice.getRaised()));
        when.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px;"
                + " -fx-text-fill: " + Palette.TEXT_SPENT + ";");

        VBox head = new VBox(1, title, when);
        head.setStyle("-fx-padding: 5 4 5 4; -fx-cursor: hand;"
                + (open ? " -fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: 3;" : ""));
        head.setOnMouseClicked(e -> {
            game.getInbox().markRead(notice, game.getMonth());
            inboxExpanded = open ? "" : notice.getKey();
            refreshInbox();
        });

        VBox row = new VBox(0, head);
        row.setStyle(done ? "" : "-fx-border-color: "
                + (notice.isRead() ? Palette.EDGE : Palette.ALERT_EDGE)
                + "; -fx-border-width: 0 0 0 2;");

        if (!open) return row;

        VBox body = new VBox(0);
        body.setStyle("-fx-padding: 2 4 8 10;");
        for (String line : notice.getBody()) {
            Label text = new Label(line.isEmpty() ? " " : line);
            text.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11.5px;"
                    + " -fx-text-fill: " + (done ? Palette.TEXT_SPENT : Palette.TEXT_BODY) + ";");
            body.getChildren().add(text);
        }

        // A settled notice gets no button. There is nothing to deal with, and
        // an enabled button that fixes something already fixed is a lie.
        if (!done) {
            Button act = new Button(dealLabel(notice.getKey()));
            act.setStyle("-fx-font-size: 11px; -fx-background-color: " + Palette.CONFIRM + ";"
                    + " -fx-text-fill: white;");
            act.setOnAction(e -> deal(notice));
            VBox.setMargin(act, new javafx.geometry.Insets(6, 0, 0, 0));
            body.getChildren().add(act);
        }

        row.getChildren().add(body);
        return row;
    }

    private String dealLabel(String key) {
        switch (key) {
            case "shedding":   return "Go to the builders →";
            case "landlock":   return "Go to the land office →";
            case "bank":       return "Go to the bank →";
            case "preferred":  return "Answer the bank →";
            case "resolved":   return "See the bank's rescue →";
            case "defaults":   return "See who owes the bank →";
            case "healthcare": return "Go and build healthcare →";
            case "overtime":   return "Go to the construction page →";
            case "demolished": return "See the construction page →";
            default:           return "Deal with this →";
        }
    }

    /**
     * Take the player to the control that answers it.
     *
     * The two acknowledgements are here rather than on the screens because they
     * are what "I have seen this and I am dealing with it" means, and the game
     * already used them for exactly that - isConstructionShedding() and
     * isPrivateInvestmentLandLocked() both go quiet once acknowledged, which is
     * what resolves the notice next month.
     */
    private void deal(Notice notice) {
        inboxOpen = false;
        switch (notice.getKey()) {
            case "shedding":
                game.acknowledgeConstructionShedding();
                sectorScreen.openSectorBooks(game.getSectors().construction(), "Investors");
                break;
            case "landlock":
                game.acknowledgeLandLock();
                landScreen.showLandMenu();
                break;
            case "bank":
                bankScreen.showBankMenu();
                break;
            case "preferred":
                // The offer's Accept and Decline are at the top of the landing (0.7.14).
                bankScreen.bankArea = null;
                bankScreen.showBankMenu();
                break;
            case "resolved":
                bankScreen.openPage("Capital & owners");
                break;
            case "defaults":
                bankScreen.openPage("Lending");
                break;
            case "healthcare":
                buildScreen.openCategory(BuildAdvice.HEALTHCARE);
                break;
            // The player's hand on the queue (0.7.22): the rushed site's row
            // has Stop rush on it; the finished demolition is off the list.
            case "overtime":
            case "demolished":
                constructionScreen.show();
                break;
            default:
                refreshInbox();
        }
    }

    /* =====================================================================
       TOASTS (0.7.20)

       An urgent notice's title used to hang under the envelope, top right,
       for as long as it was unread - over the strip at the top of every page.
       "[!] Crime is running above Canada's" sat over the stats strip on every
       tab and hid the People page's "Off sick" card.

       Now each one is a toast: stacked in the stage's bottom-right corner,
       clear of the strips and the inbox (above the clock until 0.7.21, when
       the clock went to the header); three at most, a newer one
       pushing the oldest out; each fades after TOAST_SECONDS, and holds while
       the pointer is on it. A click opens the inbox at that notice, as the
       old line did. The envelope - the header's, since 0.7.21 - still goes
       red, with its count, for as long as anything urgent is unread - a toast
       is the interruption, not the record.

       A notice is toasted once, by its key and the month it was raised; the
       set is emptied when another city is founded or loaded (anotherCity()),
       and not when the player returns to the same one.
       ===================================================================== */

    /** How long a toast stays before it fades, in seconds. */
    static final double TOAST_SECONDS = 8;

    /** How many toasts at once. */
    static final int TOAST_MAX = 3;

    /** How wide a toast's text wraps. */
    static final double TOAST_WIDTH = 340;

    /** The notices already toasted, as key@raised. */
    private final java.util.Set<String> toasted = new java.util.HashSet<>();

    /** New urgent notices become toasts; read or settled ones leave the stack. */
    private void refreshToasts() {
        if (toastStack == null) return;
        showIf(toastStack, !isGameMenu(currentScreen));
        if (game.getInbox() == null) return;
        toastStack.getChildren().removeIf(n ->
                n.getUserData() instanceof Notice was && !was.isUrgent());
        for (Notice notice : game.getInbox().all()) {          // oldest first: the newest lands lowest, in the corner
            if (!notice.isUrgent()) continue;
            if (!toasted.add(notice.getKey() + "@" + notice.getRaised())) continue;
            toastStack.getChildren().add(toast(notice));
        }
        while (toastStack.getChildren().size() > TOAST_MAX) toastStack.getChildren().remove(0);
    }

    /** One toast: the notice's title, a timer, and a door to the inbox. */
    private Label toast(Notice notice) {
        Label toast = new Label("[!]  " + notice.getTitle());
        toast.setUserData(notice);
        toast.setWrapText(true);
        toast.setMaxWidth(TOAST_WIDTH);
        toast.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + Palette.BAD_TEXT + "; -fx-background-color: " + Palette.ALERT_GROUND_LOUD + ";"
                + " -fx-background-radius: 4; -fx-border-color: " + Palette.ALERT_EDGE + ";"
                + " -fx-border-radius: 4; -fx-padding: 7 10 7 10; -fx-cursor: hand;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);");

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.millis(700), toast);
        fade.setFromValue(1);
        fade.setToValue(0);
        javafx.animation.SequentialTransition life = new javafx.animation.SequentialTransition(
                new javafx.animation.PauseTransition(Duration.seconds(TOAST_SECONDS)), fade);
        life.setOnFinished(e -> toastStack.getChildren().remove(toast));
        // Held while the pointer is on it, so it cannot fade under a click.
        toast.setOnMouseEntered(e -> { life.stop(); toast.setOpacity(1); });
        toast.setOnMouseExited(e -> life.playFromStart());

        toast.setOnMouseClicked(e -> {
            life.stop();
            toastStack.getChildren().remove(toast);
            game.getInbox().markRead(notice, game.getMonth());
            inboxExpanded = notice.getKey();
            inboxOpen = true;
            refreshInbox();
        });
        life.play();
        return toast;
    }

    /* =====================================================================
       TIME, AND WHAT THE MONTH IS WORTH

       The two things that used to be buttons in the column - Next Month and
       Simulate Multiple Months - became one round play button pinned to the
       bottom right, because it is the one control in the game that is true of
       every screen. A player reading the household books should not have to
       leave to advance a month. Since 0.7.21 it is in the header, beside the
       date (THE HEADER, clockBlock()).
       ===================================================================== */

    /**
     * Starts the frame loop. Called once, after the stage is up.
     *
     * An AnimationTimer rather than a Timeline because the DAY has to move
     * smoothly and the MONTH has to land exactly: a Timeline firing every
     * frame would do the same job with a KeyFrame in the way, and one firing
     * every month could not draw a day at all.
     */
    private void startClock() {
        if (clock != null) return;
        clock = new javafx.animation.AnimationTimer() {
            @Override public void handle(long now) {
                if (lastFrame == 0) { lastFrame = now; return; }
                double dt = (now - lastFrame) / 1e9;
                lastFrame = now;
                sinceRedraw += dt;
                // The first frame after a release (0.7.40): it draws what the press held back.
                boolean released = letGo;
                letGo = false;

                // ...and while a dialog is up (0.7.22): a confirmation's figures
                // are the ones its action will meet.
                if (!clockRunning || game == null || isGameMenu(currentScreen) || quitDialog != null) {
                    // A month landed and its redraw was throttled away; the
                    // clock has since stopped, so nothing else will draw it.
                    if (redrawPending && game != null && !isGameMenu(currentScreen) && !pressHeld) {
                        redrawPending = false;
                        sinceRedraw = 0;
                        redrawScreen.run();
                    }
                    return;
                }

                /*
                 * CLAMPED, because a stalled frame is not elapsed game time.
                 * Dragging the window, opening a menu or a long garbage
                 * collection can hand this a dt of several seconds, and at 10x
                 * that would silently run a year while the player was not
                 * looking at the screen. A quarter of a second is the most any
                 * single frame is allowed to be worth.
                 */
                /*
                 * AND THE FRAME CLAMP EARNS ITS KEEP AT THE TOP OF THE LADDER.
                 * A quarter of a second is the most any single frame is worth,
                 * which at 50x is 2.5 months - so a dragged window or a long
                 * collection costs a couple of months rather than a couple of
                 * years. It was written for 10x and it is what makes 20x and
                 * 50x safe to offer.
                 */
                monthProgress += Math.min(dt, .25) * SPEEDS[speedIndex] / SECONDS_PER_MONTH;

                boolean landed = false;
                while (monthProgress >= 1) {
                    monthProgress -= 1;
                    game.toggleNextMonth();
                    landed = true;
                    if (stopIfSomethingHappened()) { monthProgress = 0; break; }
                }
                if (!pressHeld && ((landed && sinceRedraw >= REDRAW_EVERY) || (released && redrawPending))) {
                    sinceRedraw = 0;
                    redrawPending = false;
                    redrawScreen.run();
                } else {
                    // The day still moves every frame - it is one label's text
                    // and it is what makes a paused-looking screen look alive.
                    if (landed) redrawPending = true;
                    paintDay();
                }
            }
        };
        clock.start();
    }

    /**
     * A PRESS IS NEVER REBUILT AWAY (0.7.40; see the clock's fields): a press
     * anywhere in the window holds the clock's redraw, and the release lets
     * it go once the release - and the click the scene makes of it - has
     * been delivered (a runLater, after both). Filters, so a node that
     * consumes the event still counts. A move with no button down, or the
     * window losing the focus, lets go as well, so a release the window
     * never saw cannot hold the screen still for ever.
     */
    private void holdPresses() {
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            buttonsDown.add(e.getButton());
            setPressHeld(true);
        });
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_RELEASED, e -> {
            javafx.scene.input.MouseButton up = e.getButton();
            javafx.application.Platform.runLater(() -> {
                buttonsDown.remove(up);
                if (buttonsDown.isEmpty()) setPressHeld(false);
            });
        });
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, e -> letAllGo());
        stage.focusedProperty().addListener((o, was, now) -> { if (!now) letAllGo(); });
    }

    /** No button is down after all: a move without one, or the window gone from under the pointer. */
    private void letAllGo() {
        buttonsDown.clear();
        if (pressHeld) setPressHeld(false);
    }

    /** Holds the redraw, or lets it go - and says so on the scene, for TimeChart. */
    private void setPressHeld(boolean held) {
        if (pressHeld && !held) letGo = true;
        pressHeld = held;
        scene.getProperties().put(PRESS_HELD, held);
    }

    /**
     * The open screen redrawn on the next frame no button is held on (0.7.40):
     * for a change made as a press takes the focus - Finances' ask, set as
     * its box is left - which a redraw at once would take from under the
     * release.
     */
    void redrawSoon() {
        redrawPending = true;
        letGo = true;
    }

    /** Whether a mouse button is down in the scene a node is in (0.7.40): what a redraw on a timer waits for. */
    static boolean pressHeld(javafx.scene.Node node) {
        javafx.scene.Scene s = node == null ? null : node.getScene();
        return s != null && Boolean.TRUE.equals(s.getProperties().get(PRESS_HELD));
    }

    /**
     * The day, repainted in place.
     *
     * Deliberately NOT a redraw. At 1x this runs sixty times a second and the
     * month behind it has not changed, so rebuilding the screen for it would be
     * rebuilding the same screen sixty times a second. One label's text.
     */
    private void paintDay() {
        if (chartFullClock != null) chartFullClock.setText(clockWords());
        if (dayLabel == null) return;
        dayLabel.setText(CityCalendar.formatDay(game.getMonth(), monthProgress));
    }

    /* =====================================================================
       THE CHART OVER THE WHOLE WINDOW (0.7.23)

       City History's full screen (HistoryScreen, FULL SCREEN): a pane laid
       over the whole window, as a dialog and the main menu are, so the rail,
       the panels and the header are under it. The clock is left as it was -
       running or paused - and its day ticks on the pane's own line, since
       the header's is covered. Esc, the pane's button, and any other screen
       taking over (clearMenu()) close it. F11 is separate: the window's own
       full screen, which this does not read or change. Since 0.7.61 the land
       office's map expands into the same pane (MapView), and "any other
       screen" is any but the one that laid it, whose redraws on the clock
       go on under the pane.
       ===================================================================== */

    /** The chart's pane while it has the window, its clock line, and what closes it; null otherwise. */
    private javafx.scene.Node chartFull;
    private Label chartFullClock;
    private java.util.function.Consumer<Boolean> chartFullLeave;

    /** The screen that laid the pane (a clearMenu() name): its own redraws keep it, any other screen closes it (0.7.61: City History's chart, the land office's map). */
    private String chartFullOwner;

    /**
     * Lays the chart's pane over the whole window. `leave` is the screen's
     * own way back, which Esc calls with true - come back and redraw the
     * page - and P or another screen with false: the page is not what is
     * drawn next.
     */
    void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave) {
        if (chartFull != null) windowStack.getChildren().remove(chartFull);
        hideTooltips();
        chartFull = pane;
        chartFullClock = clockLine;
        chartFullLeave = leave;
        chartFullOwner = currentScreen;
        if (clockLine != null) clockLine.setText(clockWords());
        windowStack.getChildren().add(pane);
    }

    /** Takes the pane off the window; the screen redraws its page. */
    void closeChartFullScreen() {
        if (chartFull != null) windowStack.getChildren().remove(chartFull);
        chartFull = null;
        chartFullClock = null;
        chartFullLeave = null;
        chartFullOwner = null;
    }

    /** Esc, P or another screen: the screen's own way back, which calls closeChartFullScreen(). */
    private void leaveChartFullScreen(boolean redraw) {
        java.util.function.Consumer<Boolean> leave = chartFullLeave;
        if (leave != null) leave.accept(redraw); else closeChartFullScreen();
    }

    /** Whether a chart has the whole window. */
    boolean isChartFullScreen() { return chartFull != null; }

    /** Whether this screen - a clearMenu() name, "showHistoryMenu" - is the one on show: a late redraw asks before it draws. */
    boolean isShowing(String screen) { return screen != null && screen.equals(currentScreen); }

    /** The date and what the clock is doing, for a line that stands in for the header: "14 February 2151 · paused". */
    String clockWords() {
        return CityCalendar.formatDay(game.getMonth(), monthProgress) + "  ·  "
                + (clockRunning ? "running at " + speedLabel(speedIndex) : "paused");
    }

    /** Whether a wheel event's target is inside a chart that takes the wheel (TimeChart.WHEEL_OWNER). */
    private static boolean overAChart(Object target) {
        javafx.scene.Node n = target instanceof javafx.scene.Node node ? node : null;
        while (n != null) {
            if (n.getProperties().containsKey(TimeChart.WHEEL_OWNER)) return true;
            n = n.getParent();
        }
        return false;
    }

    /**
     * Stops the clock when the city has something to say, if the player wants
     * that. Returns true if it stopped.
     *
     * THE INBOX ALREADY DECIDES WHAT IS WORTH INTERRUPTING FOR - urgent() is
     * "the newest unread notice whose condition still holds", and its own note
     * says it is null most of the time by design. Re-deciding that here would
     * be a second opinion about severity that drifts away from the first.
     *
     * ONE CONDITION INTERRUPTS ONCE. Keyed on the notice, so a player who
     * presses play again is not stopped by the same bank on the same month for
     * ever - which is what turns a useful stop into an unusable one.
     */
    private boolean stopIfSomethingHappened() {
        if (prefs == null || !prefs.isPauseOnEvents()) return false;
        Notice urgent = game.getInbox() == null ? null : game.getInbox().urgent();
        if (urgent == null) return false;
        String key = urgent.getKey() + "@" + urgent.getRaised();
        if (key.equals(pausedOnKey)) return false;
        pausedOnKey = key;
        pausedBecause = urgent.getTitle();
        clockRunning = false;
        return true;
    }

    /** Play, or pause. The one control the whole game now runs on. */
    private void setClockRunning(boolean run) {
        clockRunning = run;
        if (run) {
            pausedBecause = null;
            lastFrame = 0;          // do not bank the time spent paused
        }
    }

    /** "0.25×", "1×", "10×" - no trailing zeros on the round ones; the header's speed reads it (0.7.21: "×", the mockups', for "x"). */
    private static String speedLabel(int index) {
        double s = SPEEDS[index];
        return (s == Math.floor(s) ? String.valueOf((int) s) : String.valueOf(s)) + "×";
    }

    /* =====================================================================
       TWELVE PIPS, AND ONE OF THEM MOVED.

       Jerus: "in the next month button, perhaps add something so that the
       player knows that a month passed or something, perhaps a small count of
       the month of the year."

       The date bar had said the date all along and it was not enough,
       because nothing about it MOVED, forty pixels from where the player was
       looking: the click was at the bottom right and the confirmation at the
       top left. So twelve pips sat beside the button, filled up to this
       month, and the current one popped when the month landed.

       Since 0.7.21 the clock is in the header and the date is beside the
       button, so the confirmation is at the click again; the pips went, and
       the month's figure under the date pops instead (clockBlock()). It pops
       only when the month CHANGES - the header is rebuilt on every redraw,
       and a figure that flashed on all of those would be noise.
       ===================================================================== */

    /** A quarter second of "that landed", on the figure the month just changed - and since 0.7.26 on the land office's figures the month moved. */
    static void popPip(Region pip) {
        javafx.animation.ScaleTransition pop =
                new javafx.animation.ScaleTransition(Duration.millis(130), pip);
        pop.setFromX(1);
        pop.setFromY(1);
        pop.setToX(1.25);
        pop.setToY(1.25);
        pop.setCycleCount(2);
        pop.setAutoReverse(true);
        pop.play();
    }
}
