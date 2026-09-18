package ham.citybuildersim.ui;

import ham.citybuildersim.*;
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
 * The window: the stage and its theme, the clock and the speed ladder, the two
 * strips, the rail down the left and the inbox, the left panel and the
 * construction panel, the save and settings dialogs, the time-skip dialog, and
 * the scroller every screen draws into. Everything a tab SHOWS is a class of
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
    final SectorScreen     sectorScreen     = new SectorScreen(this);
    final GovernmentScreen governmentScreen = new GovernmentScreen(this);
    final FinancesScreen   financesScreen   = new FinancesScreen(this);
    final BankScreen       bankScreen       = new BankScreen(this);
    final TradeScreen      tradeScreen      = new TradeScreen(this);
    final PolicyScreen     policyScreen     = new PolicyScreen(this);
    final HistoryScreen    historyScreen    = new HistoryScreen(this);
    final SummaryScreen    summaryScreen    = new SummaryScreen(this);   // the left panel's content, not a tab

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

    /** The navigation rail down the panel's right edge; see start(). */
    private VBox tabRail;
    /** The middle of the window, with the three overlays on top of it. */
    private StackPane stagePane;
    private VBox inboxCorner;
    private HBox timeControls;
    private StackPane incomeDome;
    /** The middle column's scroller. A field, so clearMenu can put it back. */
    javafx.scene.control.ScrollPane menuScroller;
    private VBox constructionPanel;
    VBox cityPanel;

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
       and whether the card is currently showing.
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
       ===================================================================== */

    /** The middle of the window: the blackish blue everything else sits on. */
    private static final String STAGE = "#111a24";

    private void applyTheme(Scene target) {

        String css =
            ".root {"
          + "  -fx-base: #1b2530;"
          + "  -fx-background: #111a24;"
          + "  -fx-control-inner-background: #17212c;"
          + "  -fx-text-background-color: #e3e8ec;"
          + "  -fx-accent: #2f6fa8;"
          + "  -fx-focus-color: #5cb8ff;"
          + "  -fx-faint-focus-color: rgba(92,184,255,0.15);"
          + "}"
          + ".label { -fx-text-fill: #c3ccd3; }"

          + ".button {"
          + "  -fx-background-color: #22303c;"
          + "  -fx-text-fill: #dbe4ea;"
          + "  -fx-background-radius: 4;"
          + "  -fx-border-color: #33404b;"
          + "  -fx-border-radius: 4;"
          + "  -fx-padding: 6 14 6 14;"
          + "  -fx-cursor: hand;"
          + "}"
          + ".button:hover    { -fx-background-color: #2b3c4b; -fx-border-color: #5cb8ff; }"
          + ".button:pressed  { -fx-background-color: #18222c; }"
          + ".button:disabled { -fx-opacity: 0.4; }"

          + ".text-field {"
          + "  -fx-background-color: #17212c;"
          + "  -fx-text-fill: #e3e8ec;"
          + "  -fx-prompt-text-fill: #6b7c89;"
          + "  -fx-border-color: #33404b;"
          + "  -fx-border-radius: 3;"
          + "  -fx-background-radius: 3;"
          + "}"

          + ".scroll-pane { -fx-background-color: transparent; -fx-background: transparent; }"
          + ".scroll-pane > .viewport { -fx-background-color: transparent; }"
          + ".scroll-bar { -fx-background-color: transparent; }"
          + ".scroll-bar > .thumb { -fx-background-color: #33404b; -fx-background-radius: 6; }"
          + ".scroll-bar > .thumb:hover { -fx-background-color: #4c6070; }"
          + ".scroll-bar > .increment-button,"
          + ".scroll-bar > .decrement-button { -fx-opacity: 0; -fx-padding: 0; }"

          + ".progress-bar > .track { -fx-background-color: #17212c; }"
          + ".progress-bar > .bar   { -fx-background-color: #5cb8ff; }"

          // The Policy tab's levers. A default Slider is a pale track and a
          // pale thumb, which on this ground reads as a disabled control.
          + ".slider > .track {"
          + "  -fx-background-color: #17212c;"
          + "  -fx-border-color: #33404b;"
          + "  -fx-border-radius: 4;"
          + "  -fx-background-radius: 4;"
          + "  -fx-pref-height: 8;"
          + "}"
          + ".slider > .thumb {"
          + "  -fx-background-color: #5cb8ff;"
          + "  -fx-background-radius: 9;"
          + "  -fx-padding: 8;"
          + "  -fx-effect: null;"
          + "}"
          + ".slider > .thumb:hover   { -fx-background-color: #9ad4ff; }"
          + ".slider > .thumb:pressed { -fx-background-color: #ffffff; }"

          + ".tooltip { -fx-background-color: transparent; }"

          + ".chart { -fx-background-color: transparent; -fx-padding: 4; }"
          + ".chart-plot-background { -fx-background-color: #16202a; }"
          + ".chart-title { -fx-text-fill: #dbe4ea; }"
          + ".chart-legend { -fx-background-color: transparent; }"
          + ".chart-vertical-grid-lines, .chart-horizontal-grid-lines { -fx-stroke: #24313c; }"
          + ".chart-alternative-row-fill { -fx-fill: transparent; -fx-stroke: transparent; }"
          + ".chart-pie-label { -fx-fill: #b6c2cb; }"
          + ".chart-pie-label-line { -fx-stroke: #4c6070; }"
          + ".axis {"
          + "  -fx-tick-label-fill: #8fa3b0;"
          + "  -fx-tick-mark-stroke: #4c6070;"
          + "  -fx-minor-tick-mark-stroke: #33404b;"
          + "}"
          + ".axis-label { -fx-text-fill: #8fa3b0; }";

        target.getStylesheets().add("data:text/css;base64,"
                + java.util.Base64.getEncoder().encodeToString(
                        css.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    /** Always-visible strips on the two BorderPane edges nothing else uses. */
    private HBox dateBar;
    private HBox debtBar;
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
     * The ladder the speed slider sticks to.
     *
     * A slider that snaps rather than a row of buttons or a free drag - Jerus's
     * call: "a slider that sticks if you get what i mean, aka the discrete one,
     * but its a sticky ladder". Dragging is the natural gesture for a speed and
     * landing between two stops is not a speed anybody meant to pick.
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

        //Initialize the core UI once
        this.rootMenu = new VBox(10);
        this.rootMenu.setAlignment(Pos.CENTER);
        // A little air top and bottom. The dome and the round buttons have a
        // strip of their own below this scroller, so nothing has to be left
        // clear for them - see the bottom strip in start().
        this.rootMenu.setPadding(new javafx.geometry.Insets(8, 0, 14, 0));

        // Persistent construction panel down the right-hand side. The menu system
        // swaps the contents of rootMenu constantly, so the panel lives outside it
        // in a BorderPane and survives every screen change.
        this.constructionPanel = new VBox(8);
        this.constructionPanel.setPrefWidth(280);
        /*
         * The right-hand strip, dressed to match the left.
         *
         * Same ground, same border weight, mirrored - so the two read as one
         * frame around the stage rather than as two unrelated sidebars. Jerus:
         * "the right tab make it match the other two".
         */
        this.constructionPanel.setStyle(
                "-fx-padding: 12 12 12 10; -fx-background-color: #1c262b;"
                + " -fx-border-color: #37474f; -fx-border-width: 0 0 0 2;");

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
         * a document - it is an instrument cluster that is on screen at all
         * times, and the thing it most needs to do is let a figure that has
         * gone wrong catch the eye of somebody who is looking at something
         * else. Red on light grey does not; red on near-black does.
         */
        this.cityPanel.setStyle(
                "-fx-padding: 12 10 12 12; -fx-background-color: #1c262b;"
                + " -fx-border-color: #37474f; -fx-border-width: 0 2 0 0;");

        /*
         * Date, cash and population across the top; the next debt maturities
         * across the bottom.
         *
         * Both live outside rootMenu for exactly the reason the two side panels
         * do: the menu system clears its own children on every screen change, so
         * anything meant to be always-visible has to hang off the BorderPane
         * instead. These were the two edges still unused.
         */
        this.dateBar = new HBox(20);
        this.dateBar.setAlignment(Pos.CENTER_LEFT);
        this.dateBar.setStyle(
                "-fx-padding: 10 18 10 18; -fx-background-color: #1c262b;"
                + " -fx-border-color: #37474f; -fx-border-width: 0 0 2 0;");

        this.debtBar = new HBox(16);
        this.debtBar.setAlignment(Pos.CENTER_LEFT);
        this.debtBar.setStyle(
                "-fx-padding: 6 16 6 16; -fx-background-color: #1c262b;"
                + " -fx-border-color: #33404b; -fx-border-width: 1 0 0 0;");

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
           keeps it centred while it fits and lets it grow past that when it
           does not, so a short menu looks exactly as it always did and a long
           one scrolls. Four pixels of slack, or the binding fights the
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
           THE RAIL, AND WHY IT IS PART OF THE PANEL.

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
           ===================================================================== */
        this.tabRail = new VBox(2);
        this.tabRail.setPrefWidth(RAIL_WIDTH);
        this.tabRail.setMinWidth(RAIL_WIDTH);
        this.tabRail.setAlignment(Pos.TOP_CENTER);
        this.tabRail.setStyle(
                "-fx-padding: 10 0 10 0; -fx-background-color: #1c262b;"
                + " -fx-border-color: #37474f; -fx-border-width: 0 2 0 1;");

        HBox leftEdge = new HBox(cityPanel, tabRail);
        leftEdge.setStyle("-fx-background-color: #1c262b;");

        /* =====================================================================
           THE STAGE IS A STACK NOW.

           Three things have to float over the middle rather than sit in the
           column with the menus: the inbox in the top right, the time controls
           in the bottom right, and the income dome at the bottom. All three are
           true of the CITY rather than of whatever screen is open, so putting
           them in rootMenu would mean every one of fifty screens drawing them,
           and a screen that forgot would silently lose the player's ability to
           advance a month.

           Each overlay is pinned to USE_PREF_SIZE. A StackPane stretches its
           children to fill by default, and a stretched transparent overlay would
           swallow every click meant for the menu underneath it.
           ===================================================================== */
        this.stagePane = new StackPane();
        this.stagePane.setStyle("-fx-background-color: " + STAGE + ";");
        this.stagePane.getChildren().add(menuScroller);

        /*
         * The inbox is the only thing that FLOATS, and it earns it: it is shut
         * almost always, it is opened deliberately, and while it is open
         * covering the top corner of the screen is the point.
         */
        this.inboxCorner = new VBox(0);
        this.inboxCorner.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(inboxCorner, Pos.TOP_RIGHT);
        StackPane.setMargin(inboxCorner, new javafx.geometry.Insets(12, 14, 0, 0));
        this.stagePane.getChildren().add(inboxCorner);

        /* =====================================================================
           AND THE TIME CONTROLS DO NOT FLOAT, WHICH IS A CORRECTION.

           They were overlaid on the stage with the dome, and play-testing the
           healthcare build menu showed exactly what that costs: the dome sat on
           top of the Memorial Cemetery row and hid its price. A menu can be
           scrolled out from under an overlay, but there is no scroll position
           at which the bottom of the stage is not covered - so a row could
           always be hiding under it, and the player has no way to know.

           They live in a strip of their own now, below the scroller and above
           the debt bar. The viewport genuinely ends where the strip begins, so
           nothing is ever underneath anything. Same place on screen, no dead
           zone - and rootMenu no longer needs the 86px of bottom padding it was
           carrying to let long menus clear them.
           ===================================================================== */
        this.timeControls = new HBox(10);
        this.timeControls.setAlignment(Pos.BOTTOM_RIGHT);
        this.timeControls.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(timeControls, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(timeControls, new javafx.geometry.Insets(0, 20, 8, 0));

        this.incomeDome = new StackPane();
        this.incomeDome.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(incomeDome, Pos.BOTTOM_CENTER);

        StackPane bottomStrip = new StackPane(incomeDome, timeControls);
        bottomStrip.setPrefHeight(STRIP_HEIGHT);
        bottomStrip.setMinHeight(STRIP_HEIGHT);
        bottomStrip.setStyle("-fx-background-color: " + STAGE + ";");

        VBox stageColumn = new VBox(stagePane, bottomStrip);
        VBox.setVgrow(stagePane, Priority.ALWAYS);
        stageColumn.setStyle("-fx-background-color: " + STAGE + ";");

        /* =====================================================================
           THE WHEEL WORKS WHERE THE POINTER ALREADY IS.

           Jerus: "in the buildings section, if you scroll, and then click next
           month, and then try to scroll it doesn't let you, it locks."

           It was not locked. Clicking Next Month leaves the pointer ON the Next
           Month button, and that button lives in the bottom strip, which is a
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
        // The stage, and deliberately DARKER than the four strips around it.
        // Chrome frames content; the same colour on both would make the window
        // one flat field with things floating in it.
        root.setStyle("-fx-background-color: " + STAGE + ";");
        menuScroller.setStyle("-fx-background-color: " + STAGE + ";"
                + " -fx-background: " + STAGE + ";");
        root.setCenter(stageColumn);
        root.setLeft(leftEdge);
        root.setRight(constructionPanel);
        root.setTop(dateBar);
        root.setBottom(debtBar);
        this.scene = new Scene(root);
        applyTheme(scene);


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
           over the date bar, saying the wrong key, on every single launch.
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

        // Esc is the pause menu, from anywhere. See the gear at the foot of
        // the rail for the half of this a new player can find on their own -
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
            boolean typing = scene.getFocusOwner() instanceof TextField;

            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE
                    || (!typing && e.getCode() == javafx.scene.input.KeyCode.P)) {
                showMainMenu();
                e.consume();
            } else if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                toggleFullScreen();
                e.consume();
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
            }
        });

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
        boolean sameScreen = screen.equals(currentScreen) && !railJump;
        railJump = false;
        // Anything vvalue does from here until the settle below is the rebuild
        // clamping, not the player.
        settlingScroll = true;
        if (!sameScreen) pageScrollAt = 0;
        // The page's position is no longer read off the scroller here - see
        // pageScrollAt, which the player alone writes.
        if (!sameScreen) innerScrollAt.clear();
        currentScreen = screen;

        /*
         * HOW TO DRAW THIS SCREEN AGAIN.
         *
         * The round button at the bottom right advances a month from wherever
         * the player happens to be, so after the month has run it has to redraw
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

        rootMenu.getChildren().clear();
        summaryScreen.refreshCityPanel();
        refreshConstructionPanel();
        refreshDateBar();
        refreshDebtBar();
        refreshTabRail();
        refreshInbox();
        refreshTimeControls();

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

    /** How to draw it again after a month passes; see clearMenu. */
    private Runnable redrawScreen = () -> { };

    /**
     * Draw the screen that is showing, again, in place - the same call the
     * clock makes after a month. For a screen piece that lives on more than
     * one tab (the staged-lever slider is on Policy and on Services) and
     * cannot know which screen to redraw by name.
     */
    void redraw() { redrawScreen.run(); }

    /** The screen Esc was pressed on, so Resume can go back to it. */
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
            case "showSettingsMenu":
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
     * The Reports tab is the case (Jerus, 2026-09-18: "when you click some
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
       THE TWO STRIPS
       ===================================================================== */

    /**
     * Date, month number, cash and population across the top.
     *
     * THE MONTH NUMBER STAYS, next to the date rather than instead of it. Every
     * save, log entry, bond maturity and report in this game is keyed to the
     * integer month, so a player cross-referencing anything - "the demolition
     * log says 14 months ago", "this bond matures in month 340" - needs the
     * number the game actually counts in. The date is what a person thinks in;
     * the number is what the game thinks in, and hiding the second would make
     * every other screen harder to read, not easier.
     */
    private void refreshDateBar() {

        dateBar.getChildren().clear();

        int month = game.getMonth();

        /*
         * THE TWO ANCHORS, and they are deliberately the largest text anywhere
         * in the game.
         *
         * What month is it and how much money is there are the only two figures
         * a player needs on EVERY screen, and at 15px and 13px they were the
         * same weight as the six things beside them - so the eye had to go
         * looking. They are 28px now, one at each end of the strip, each on its
         * own inset panel with a caption under it. Everything between them got
         * smaller rather than bigger, because pronouncing one thing means
         * quietening its neighbours; making all eight bold would be the same
         * screen again, louder.
         */
        /*
         * THE DAY, which is the only thing on this bar that moves between
         * months. Kept in a field because the clock repaints it every frame and
         * must not rebuild the screen to do it - see paintDay().
         */
        Label date = new Label(CityCalendar.formatDay(month, monthProgress));
        date.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;"
                + " -fx-text-fill: #ffffff;");
        dayLabel = date;

        Label counter = new Label("month " + formatter.format(month));
        counter.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-text-fill: #78909c;");

        VBox dateBox = new VBox(-2);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        dateBox.getChildren().addAll(date, counter);
        dateBox.setStyle("-fx-padding: 2 14 4 12; -fx-background-color: #26343b;"
                + " -fx-background-radius: 4;");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        double cash = game.getCash();
        double income = game.getIncome();

        /*
         * Courier, at 28px, because the digits have to stay in the same columns
         * from month to month. A proportional face makes the number jitter
         * sideways as it changes width, and a figure that moves is a figure the
         * eye has to re-find every turn - the opposite of what this is for.
         */
        Label cashLabel = new Label(money(cash));
        cashLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 28px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                // Overdrawn is not a rounding detail - it is being charged the
                // emergency rate - so it gets the same red as everything else
                // that is actively costing the player money.
                + (cash < 0 ? "#ff6b6b" : "#8fe0aa") + ";");

        // What it is doing, under what it is. A treasury of $300k falling by
        // $40k a month is a different city from one holding steady, and the
        // headline figure alone cannot tell them apart.
        Label trend = new Label((income >= 0 ? "+" : "\u2212")
                + money(Math.abs(income)) + " a month");
        trend.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-text-fill: " + (income >= 0 ? "#78909c" : "#ff9e9e") + ";");

        VBox cashBox = new VBox(-2);
        cashBox.setAlignment(Pos.CENTER_RIGHT);
        cashBox.getChildren().addAll(cashLabel, trend);
        cashBox.setStyle("-fx-padding: 2 12 4 14; -fx-background-color: #26343b;"
                + " -fx-background-radius: 4;");

        /*
         * Population, and underneath it the four flows that moved it.
         *
         * The headline number alone cannot tell a player WHY it changed, and
         * since the switch there are four different reasons - and they call for
         * opposite responses. A city losing people because nobody is being born
         * needs nothing done about it this decade; a city losing people because
         * they are leaving needs jobs, now. Putting the flows next to the stock
         * is the difference between a number that reports and one that explains.
         */
        Label popLabel = new Label("Pop  "
                + formatter.format(game.getPopulationManager().getPopulation()));
        popLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;"
                + " -fx-font-weight: bold; -fx-text-fill: #cfd8dc;");

        PopulationCohorts pyramid = game.getCohorts();
        Migration flows = game.getMigration();

        HBox flowRow = new HBox(7);
        flowRow.setAlignment(Pos.CENTER_RIGHT);
        flowRow.getChildren().addAll(
                flowChip("+" + flowText(pyramid.getLastBirths()) + " born",  "#8fe0aa"),
                flowChip("-" + flowText(pyramid.getLastDeaths()) + " died",  "#8fa3b0"),
                flowChip("+" + flowText(flows.getLastArrivals()) + " in",    "#8ed4ff"),
                flowChip("-" + flowText(flows.getLastDepartures()) + " out", "#ffb3b3"));

        /*
         * A fifth chip, and only when there is something to say.
         *
         * The four above are flows - they belong there permanently because their
         * value is the comparison between them. Sickness is a condition, and a
         * chip that reads "3% sick" every month for four hundred months is noise
         * a player learns to stop seeing. So it appears exactly when the city is
         * losing more work than a well-served one has to, which means it appears
         * for a city with no clinics and for an outbreak, and otherwise not at
         * all.
         */
        Health illness = game.getHealth();
        if (illness.isOutbreak()) {
            /*
             * NAMED, not just coloured. The chip used to go red during an
             * epidemic and otherwise amber, which told a player who already knew
             * what the colours meant and nobody else - and an outbreak is a
             * temporary event with a cause, unlike the standing rate beside it.
             * A player wants to know THAT one is running before they want to
             * know what it is costing.
             */
            int since = Math.max(1, game.getMonth() - illness.getOutbreakStarted() + 1);
            flowRow.getChildren().add(flowChip(
                    String.format("OUTBREAK - month %d", since), "#ff6b6b"));
        }
        if (illness.getSickRate() > Health.WELL_SERVED_RATE + 1e-9) {
            flowRow.getChildren().add(flowChip(
                    String.format("%.0f%% sick", illness.getSickRate() * 100),
                    illness.isOutbreak() ? "#ffb3b3" : "#ffcf9e"));
        }

        VBox popBox = new VBox(0);
        popBox.setAlignment(Pos.CENTER_RIGHT);
        popBox.getChildren().addAll(popLabel, flowRow);

        // The rating, next to the money it governs. The rate curve already knew
        // this; a letter is how a borrower actually experiences its own credit.
        String rating = game.getCreditRating();
        Label ratingLabel = new Label(rating);
        ratingLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-padding: 1 6 1 6; -fx-background-radius: 3;"
                + " -fx-text-fill: #ffffff; -fx-background-color: "
                + switch (rating) {
                    case "AAA", "AA" -> "#5fd68a";
                    case "A", "BBB"  -> "#9ccc65";
                    case "BB"        -> "#ffb454";
                    case "B"         -> "#ff8a65";
                    default          -> "#ff6b6b";
                } + ";");

        dateBar.getChildren().addAll(dateBox, gap, ratingLabel, popBox, cashBox);
    }


    /** One small coloured figure on the population strip. */
    private Label flowChip(String text, String colour) {
        Label chip = new Label(text);
        chip.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                + " -fx-text-fill: " + colour + ";");
        return chip;
    }

    /**
     * The next five city debts to come due, and what the city owes altogether.
     *
     * CITY DEBT ONLY. The business sectors borrow too, and their paper is their
     * own problem - it is serviced out of sector cash and the player cannot pay
     * it off. Mixing the two here would put numbers in front of the player that
     * they have no control over, next to numbers they very much do.
     *
     * The total is ALL outstanding city principal, not just the five shown, and
     * that distinction matters: five near maturities can look small while a
     * twenty-five-year bond sits behind them. The overdraft is called out
     * separately when there is one, because it is priced as principal by the
     * market (see DebtManager.getPricedDebt) but it is not an instrument and it
     * has no maturity date to list.
     */
    private void refreshDebtBar() {

        debtBar.getChildren().clear();

        DebtManager debtManager = game.getDebtManager();
        java.util.List<Debt> debts = new java.util.ArrayList<>(debtManager.getDebt());
        int month = game.getMonth();

        debts.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));

        Label heading = new Label("NEXT DUE");
        heading.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #8fa3b0;");
        debtBar.getChildren().add(heading);

        if (debts.isEmpty()) {
            Label none = new Label("No city debt outstanding.");
            none.setStyle("-fx-font-size: 11px; -fx-text-fill: #78909c;");
            debtBar.getChildren().add(none);
        } else {
            int shown = 0;
            for (Debt debt : debts) {
                if (shown++ >= 5) break;
                debtBar.getChildren().add(maturityChip(debt, month));
            }
            if (debts.size() > 5) {
                Label more = new Label("+" + (debts.size() - 5) + " more");
                more.setStyle("-fx-font-size: 10px; -fx-text-fill: #8fa3b0;");
                debtBar.getChildren().add(more);
            }
        }

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        debtBar.getChildren().add(gap);

        /*
         * COUPON, not "interest", and the word is load-bearing.
         *
         * This sum is exactly what the city is charged each month - it is the
         * same figure the bonds' own processMonth() puts through
         * InterestExpense(). But a T-Bill is a DISCOUNT instrument: it charges
         * nothing monthly and repays its whole face at maturity, so its
         * getMonthlyInterestExpense() is 0, correctly. Summing that under the
         * heading "INTEREST" tells a player whose only debt is bills that they
         * are borrowing for free, which is the opposite of true - the cost is
         * real, it is just sitting in the maturity chip instead.
         *
         * So the recurring figure is named for what it actually is, and when
         * there is zero-coupon paper outstanding the strip says where the rest
         * of the cost went rather than leaving a suspicious zero to interpret.
         */
        double principal = debtManager.getAllPrincipal();
        double coupon = 0;
        double discountPaper = 0;
        for (Debt debt : debtManager.getDebt()) {
            coupon += debt.getMonthlyInterestExpense();
            if (debt.getMonthlyInterestExpense() <= 0) {
                discountPaper += debt.getOustandingPrincipal();
            }
        }

        Label totals = new Label(String.format("TOTAL PRINCIPAL  %s      COUPON  %s/mo",
                money(principal), money(coupon)));
        totals.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: #c3ccd3;");
        debtBar.getChildren().add(totals);

        if (discountPaper > 0) {
            Label discount = new Label(String.format("  (%s of that pays at maturity, not monthly)",
                    money(discountPaper)));
            discount.setStyle("-fx-font-size: 10px; -fx-text-fill: #78909c;");
            debtBar.getChildren().add(discount);
        }

        double overdraft = debtManager.getOverdraft();
        if (overdraft > 0) {
            Label od = new Label(String.format("  + %s overdrawn",
                    money(overdraft)));
            od.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                    + " -fx-font-weight: bold; -fx-text-fill: #ff6b6b;");
            debtBar.getChildren().add(od);
        }
    }

    /** One maturity on the bottom strip: amount, type, date, how far off. */
    private VBox maturityChip(Debt debt, int currentMonth) {

        int due = debt.getMaturityMonth();
        int gap = due - currentMonth;

        // Urgency by colour as well as by position, so a wall of paper coming
        // due reads at a glance without anyone parsing five dates.
        String colour = (gap <= 3) ? "#ff6b6b" : (gap <= 12) ? "#ffb454" : "#c3ccd3";

        Label amount = new Label(money(debt.getOustandingPrincipal()));
        amount.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;"
                + " -fx-font-weight: bold; -fx-text-fill: " + colour + ";");

        Label when = new Label(CityCalendar.formatShort(due)
                + "  " + CityCalendar.until(currentMonth, due));
        when.setStyle("-fx-font-size: 9px; -fx-text-fill: #78909c;");

        Label type = new Label(debt.getType());
        type.setStyle("-fx-font-size: 9px; -fx-text-fill: #8fa3b0;");

        VBox chip = new VBox(0);
        chip.getChildren().addAll(amount, when, type);
        chip.setStyle("-fx-padding: 0 14 0 0;");
        return chip;
    }

    private void showMainMenu() {
        /*
         * WHERE RESUME GOES, and it is not "the start of the game".
         *
         * Esc opens this screen from anywhere, so Resume has to put the player
         * back on the screen they pressed it on - otherwise saving mid-game
         * costs you your place. clearMenu is about to overwrite redrawScreen
         * with this menu's own, so the screen underneath is captured first.
         */
        if (!currentScreen.isEmpty() && !"showMainMenu".equals(currentScreen)
                && !isGameMenu(currentScreen)) {
            resumeTo = redrawScreen;
        }
        clearMenu("showMainMenu", () -> showMainMenu());


        Button startNewGame = new Button("Start New Game");
        Button resumeGame = new Button("Resume Game");
        Button loadGameSave = new Button("Load Game");
        Button saveGame = new Button("Save Game");
        Button settings = new Button("Settings");
        Button quit = new Button("Quit");

        startNewGame.setOnAction(e -> {
            // NOTE: this used to call showStartMenu() BEFORE game.newGame(),
            // so the screen was drawn using pre-init/stale game state and
            // never refreshed again. Init the game first, then draw the screen.
            game.newGame();
            /*
             * THE WORLD IS CHOSEN HERE AND NOWHERE ELSE. newGame() rebuilds the
             * world with its default; this is the one moment the player's
             * setting is allowed to reach it, which is what makes it a founding
             * choice rather than a dial. A loaded city restores its own.
             */
            game.getWorldEconomy().setMeanInflation(prefs.getWorldInflation());
            openCity();
        });
        /* =================================================================
           WHAT "RESUME" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO RESUME
           -----------------------------------------------------------------
           The button used to do one thing in all three situations: call
           resumeGame(), which only calls initialize(), and then draw a screen.
           Pressed from the title screen with no city ever started, that drew
           the city view over an empty world - a game board with no game on it,
           and no way to tell that from a real one.

           There are three cases and the button now answers all three.

             a game in progress -> back to the screen Esc was pressed on. This
                is the case it was written for and it is unchanged.
             no game, but an autosave on disk -> load the autosave. This is
                what a player means by Resume on a cold start: carry on from
                where the game last saved itself, without going through the
                load list to find the one slot they were never going to pick
                anything but.
             no game and no autosave -> greyed out. A first run has nothing to
                resume, and a button that does nothing is worse than one that
                says so.

           The autosave is deliberately the ONLY slot this reaches. Resume is
           "carry on", not "choose"; choosing is what Load Game is for.
           ================================================================= */
        boolean playing = game.isRunning();
        boolean autosaveWaiting =
                game.getGameFiles().slotIsLoadable(GameFiles.AUTOSAVE_SLOT);

        resumeGame.setDisable(!playing && !autosaveWaiting);

        resumeGame.setOnAction(e -> {
            if (game.isRunning()) {
                game.resumeGame();
                if (resumeTo != null) resumeTo.run(); else openCity();
            } else {
                // loadSlot() owns the failure path and draws its own screen,
                // so a damaged autosave says why rather than dropping the
                // player into an empty city.
                loadSlot(GameFiles.AUTOSAVE_SLOT);
            }
        });
        loadGameSave.setOnAction(e -> showLoadMenu());
        saveGame.setOnAction(e -> showSavingMenu());
        quit.setOnAction(e -> game.toggleQuit());

        settings.setOnAction(e -> showSettingsMenu());


        // Small, grey, always there. The window title carries it too, but a
        // screenshot of the menu is what people actually send you.
        Label version = new Label(GameVersion.title());
        version.setStyle("-fx-font-size: 9px; -fx-text-fill: #7d8f9c; -fx-padding: 12 0 0 0;");

        // Where the log is, in the one place everyone can find. A bug report
        // that arrives with this file attached is worth ten that do not.
        Label logLine = new Label(GameLog.file() == null
                ? "(logging is not running)"
                : "Log: " + GameLog.file());
        logLine.setStyle("-fx-font-size: 9px; -fx-text-fill: #7d8f9c;");
        logLine.setWrapText(true);
        logLine.setMaxWidth(320);

        rootMenu.getChildren().addAll(
                startNewGame,
                resumeGame,
                loadGameSave,
                saveGame,
                settings,
                quit,
                version,
                logLine
        );


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

        return String.format("Month %d  -  %s people  -  %s  -  %s",
                header.getMonth(),
                formatter.format(header.getPopulation()),
                money(header.getCash()),
                when);
    }

    private String slotTitle(int slot) {
        SaveHeader header = game.getGameFiles().readHeader(slot);
        String base = GameFiles.slotLabel(slot);
        return (header != null && header.hasName())
                ? base + " - " + header.getSlotName()
                : base;
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
                + (broken ? "#ff6b6b" : empty ? "#7d8f9c" : "#8fa3b0") + ";");

        row.getChildren().addAll(pick, detail);
        return row;
    }

    private void showSavingMenu() {
        clearMenu("showSavingMenu", () -> showSavingMenu());

        Label heading = new Label("SAVE GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label hint = new Label("Choose a slot. The autosave is not in this list "
                + "on purpose - it is written for you.");
        hint.setStyle("-fx-font-size: 10px; -fx-text-fill: #8fa3b0;");
        hint.setWrapText(true);
        hint.setMaxWidth(320);

        rootMenu.getChildren().addAll(heading, hint);

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            rootMenu.getChildren().add(slotRow(slot, this::showSaveSlotConfirm, false));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        rootMenu.getChildren().add(cancel);
    }

    /**
     * Confirms one slot, and takes the optional name.
     *
     * The name field is prefilled with whatever the slot already carried, so
     * overwriting a save keeps its name unless the player chooses otherwise.
     */
    private void showSaveSlotConfirm(int slot) {
        clearMenu("showSaveSlotConfirm", () -> showSaveSlotConfirm(slot));

        boolean occupied = !game.getGameFiles().slotIsEmpty(slot);
        SaveHeader header = game.getGameFiles().readHeader(slot);

        Label heading = new Label("SAVE TO " + GameFiles.slotLabel(slot).toUpperCase());
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label current = new Label(slotSummary(slot));
        current.setStyle("-fx-font-size: 10px; -fx-text-fill: #8fa3b0;");

        TextField name = new TextField(
                (header != null && header.hasName()) ? header.getSlotName() : "");
        name.setPromptText("Name this save (optional)");
        name.setMaxWidth(300);

        rootMenu.getChildren().addAll(heading, current, name);

        if (occupied) {
            Label warn = new Label("This slot already has a city in it. Saving replaces it.");
            warn.setStyle("-fx-font-size: 10px; -fx-text-fill: #ffb454;");
            warn.setWrapText(true);
            warn.setMaxWidth(320);
            rootMenu.getChildren().add(warn);
        }

        Button confirm = new Button(occupied ? "Overwrite" : "Save");
        Button cancel = new Button("Back");

        confirm.setOnAction(e -> {
            String typed = name.getText();
            showSaveResult(game.saveGame(slot,
                    (typed == null || typed.isBlank()) ? null : typed.trim()));
        });
        cancel.setOnAction(e -> showSavingMenu());

        rootMenu.getChildren().addAll(confirm, cancel);
    }

    private void showLoadMenu() {
        clearMenu("showLoadMenu", () -> showLoadMenu());

        Label heading = new Label("LOAD GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        rootMenu.getChildren().add(heading);

        // Autosave first: it is the most recent thing the game wrote, so it is
        // what someone recovering from a crash is looking for.
        rootMenu.getChildren().add(
                slotRow(GameFiles.AUTOSAVE_SLOT, this::loadSlot, true));

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            rootMenu.getChildren().add(slotRow(slot, this::loadSlot, true));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        rootMenu.getChildren().add(cancel);
    }

    private void loadSlot(int slot) {

        game.loadGameSave(slot);

        String failure = game.getLoadFailure();
        if (failure != null) {
            clearMenu("loadSlot", () -> loadSlot(slot));
            Label outcome = new Label("Could not load.");
            outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;"
                    + " -fx-text-fill: #ff6b6b;");
            Label why = new Label(failure);
            why.setStyle("-fx-font-size: 10px; -fx-text-fill: #ff6b6b;");
            why.setWrapText(true);
            why.setMaxWidth(320);
            Button back = new Button("Back");
            back.setOnAction(e -> showLoadMenu());
            rootMenu.getChildren().addAll(outcome, why, back);
            return;
        }

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
        buildScreen.showBuildMenu();
    }

    private void showSaveResult(GameFiles.Result result) {
        clearMenu("showSaveResult", () -> showSaveResult(result));

        Label outcome = new Label(result.ok ? "Saved." : "Save failed.");
        outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: "
                + (result.ok ? "#5fd68a" : "#ff6b6b") + ";");

        Label detail = new Label(result.ok
                ? result.file.toString()
                : result.error);
        detail.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (result.ok ? "#8fa3b0" : "#ff6b6b") + ";");
        detail.setWrapText(true);
        detail.setMaxWidth(320);

        Button back = new Button("Back");
        back.setOnAction(e -> showMainMenu());

        rootMenu.getChildren().addAll(outcome, detail, back);

        if (!result.ok) {
            // Worth saying out loud: the usual causes are a full disk or a
            // folder the player has no permission to write to, and neither is
            // something the game can fix for them.
            Label advice = new Label(
                    "The city is still running - nothing has been lost yet. "
                    + "Check there is free disk space, then try again.");
            advice.setStyle("-fx-font-size: 10px; -fx-text-fill: #8fa3b0;");
            advice.setWrapText(true);
            advice.setMaxWidth(320);
            rootMenu.getChildren().add(2, advice);
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
       SETTINGS.

       Three toggles and a way back, and it had been three raw grey buttons
       since before the palette existed - which mattered less when it was
       buried behind a menu and matters now, because the gear at the foot of
       the rail lands here from anywhere.
       ===================================================================== */
    private void showSettingsMenu() {
        clearMenu("showSettingsMenu", () -> showSettingsMenu());

        Label title = new Label("SETTINGS");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

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
                "The month-by-month charts under Reports.",
                () -> { game.toggleGraphs(); showSettingsMenu(); }));
        column.getChildren().add(toggleRow("Reports", game.isReportsEnabled(),
                "The written month report in the console behind the window.",
                () -> { game.toggleReports(); showSettingsMenu(); }));
        column.getChildren().add(statementNote(
                "These two are kept with the city and travel in its save file. "
                + "Full screen is kept with the game and is the same for every city "
                + "on this machine."));

        /* =================================================================
           THE WORLD THE NEXT CITY IS FOUNDED INTO

           The one setting on this screen that does not take effect while you
           are looking at it, and it says so. A city's whole price history is
           struck against the world it grew up in, so this moves the next
           founding and nothing about the city on screen.
           ================================================================= */
        column.getChildren().add(statementHead("The world, for a new city"));

        HBox worldRow = new HBox(Palette.GAP_TIGHT);
        worldRow.setAlignment(Pos.CENTER_LEFT);
        for (double m : new double[] { 0, .01, .02, .0333, .05 }) {
            worldRow.getChildren().add(worldChip(m));
        }
        column.getChildren().add(worldRow);
        column.getChildren().add(statementNote(String.format(
                "How fast prices rise OUT THERE, on average. The world's own price "
                + "level settles about %.2fx founding at this setting, and the city's "
                + "currency is worth its own prices against that — so a faster world "
                + "is a stronger currency here, cheaper imports, and a harder time "
                + "selling abroad. 1%% is the default. Takes effect on the next new "
                + "city; the one you have keeps the world it grew up in.",
                settledLevelAt(prefs.getWorldInflation()))));

        column.getChildren().add(statementHead("Keys"));
        column.getChildren().add(statementLine("Esc  ·  P", "the game menu"));
        column.getChildren().add(statementLine("F11", "full screen on and off"));

        Button back = new Button("Back");
        back.setOnAction(e -> showMainMenu());

        rootMenu.getChildren().addAll(title, scrolled(column), back);
    }

    /**
     * Where the world's price level settles at a given mean.
     *
     * Closed form, because the level IS a closed form: advanceMonth() compounds
     * at the mean and pulls back toward a flat trend, so it rests where
     * `mean/12 x L = TREND_PULL x (L - 1)`. See the block at the top of
     * WorldEconomy - this is the same arithmetic that predicted 1.84, 1.37 and
     * 1.15 before any of them were measured.
     */
    private static double settledLevelAt(double mean) {
        double monthly = Math.pow(1 + Math.max(0, mean), 1.0 / 12) - 1;
        double denom = WorldEconomy.TREND_PULL - monthly;
        return denom <= 1e-9 ? 99 : WorldEconomy.TREND_PULL / denom;
    }

    /** One choice of world, shown as what it is and what it settles at. */
    private Button worldChip(double mean) {
        boolean on = Math.abs(prefs.getWorldInflation() - mean) < 1e-6;
        Button b = new Button(String.format("%.2g%%", mean * 100).replace("0.0%", "0%"));
        b.setStyle(Palette.figure(Palette.SIZE_LABEL, on ? "white" : Palette.TEXT_MUTED)
                + " -fx-background-color: " + (on ? Palette.ACCENT : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-border-color: " + (on ? "transparent" : Palette.CONTROL_EDGE) + ";"
                + " -fx-border-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-min-width: 56; -fx-cursor: hand;");
        b.setOnAction(e -> {
            prefs.setWorldInflation(mean);
            prefs.save(game.getGameFiles());
            showSettingsMenu();
        });
        return b;
    }

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
       tabs on the rail down the left, the two time controls are the round
       buttons at the bottom right, and Back was only ever a way to the game
       menu, which is the gear at the foot of the rail.

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
         * now wraps the People screen, the bank's books and the new Services
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
       answered here, by forwarding.
       --------------------------------------------------------------------- */

    public List<String> whatItDoes(BuildingsTemplate t) { return buildScreen.whatItDoes(t); }
    public List<String> whatCareItGives(BuildingsTemplate t) { return buildScreen.whatCareItGives(t); }
    public String jobLabel(JobType job) { return buildScreen.jobLabel(job); }


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
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: #8fa3b0;");

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
        run.setStyle("-fx-background-color: #2f7d52; -fx-text-fill: white;");
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
                item.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #ff6b6b;");
                problem.getChildren().add(item);
            }
            Label advice = monoLabel("  The months that did run are real. "
                    + "Save or reload before continuing.");
            advice.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #8fa3b0;");
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
            item.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                    + (good ? "#5fd68a" : "#ff6b6b") + ";");
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

        if (skip.getWriteOffsDuringSkip() > 0) {
            Label wo = monoLabel(String.format("%-20s%s written off by lenders",
                    "Restructuring", money(skip.getWriteOffsDuringSkip())));
            wo.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #ffb454;");
            econ.getChildren().add(wo);
        }
        column.getChildren().add(econ);

        /* ------------------------------- land ------------------------------- */
        VBox land = reportSection("LAND",
                String.format("%-20s%+.0f blocks", "Bought", skip.getLandBlocksBought()),
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
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                        + (change.isGain() ? "#5fd68a" : "#ff6b6b") + ";");
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
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #ff6b6b;");
                demolished.getChildren().add(line);
            }
            demolished.getChildren().add(monoLabel(
                    "  their owners could not afford to keep them"));
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
                epidemic.setStyle("-fx-font-family: 'Courier New';"
                        + " -fx-font-weight: bold; -fx-text-fill: #ff6b6b;");
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
                dead.setStyle("-fx-font-family: 'Courier New';"
                        + " -fx-font-weight: bold; -fx-text-fill: #ff6b6b;");
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
        line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                + (bad ? "#ff6b6b" : good ? "#5fd68a" : "#8fa3b0") + ";");
        section.getChildren().add(line);
    }

    /** Shared scaffolding for the sector report screens. */
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
       CONSTRUCTION PANEL

       Ports the terminal build's per-stack construction readout - the
       "0/1 Coal Power Plant(s) finished construction. 177 month(s)." line - into
       a panel that's visible from every screen.

       It also surfaces something that was previously invisible: construction
       capacity is divided evenly between *sites* (stacks), not weighted by work
       remaining, so every extra building type you queue slows down everything
       already in progress. The "N sites, X pts each" line makes that legible.
       ===================================================================== */

    private void refreshConstructionPanel() {
        constructionPanel.getChildren().clear();

        Label header = new Label("UNDER CONSTRUCTION");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-text-fill: #eceff1; -fx-padding: 0 0 4 2;");
        constructionPanel.getChildren().add(header);

        BuildingManager buildingManager = game.getBuildingManager();
        List<BuildingsStacks> sites = buildingManager.getStacksUnderConstruction();

        int output = game.getConstructionOutput();
        int siteCount = buildingManager.getUnderConstruction();
        double perSite = buildingManager.outputPerSite(output);

        Label capacity = monoLabel("Output: " + formatter.format(output) + " pts/mo");
        capacity.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #8fa3b0;");
        constructionPanel.getChildren().add(capacity);

        if (sites.isEmpty()) {
            Label idle = new Label("Nothing being built.");
            idle.setStyle("-fx-text-fill: #7d8f9c; -fx-padding: 8 0 0 0;");
            constructionPanel.getChildren().add(idle);
            addBuildLog();
            addDemolitionLog();
            return;
        }

        Label split = monoLabel(siteCount + " site(s), " + formatter.format(perSite) + " each");
        split.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #8fa3b0;");
        constructionPanel.getChildren().add(split);

        VBox list = new VBox(12);
        list.setStyle("-fx-padding: 10 0 0 0;");

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
            // explicit fill: without it the default Label colour renders almost
            // invisibly against the panel's light background
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #5cb8ff;");

            ProgressBar bar = new ProgressBar(fraction);
            bar.setPrefWidth(240);

            // Same months-remaining calculation the console prints, guarded for the
            // zero-output case (fully unstaffed construction) that would otherwise
            // divide by zero and render as 2147483647.
            String eta;
            if (perSite <= 0) {
                // Two things can stall a site now, and they want different
                // words: nobody to do the work, or nothing able to reach it.
                eta = game.getInfrastructureManager().isCongested()
                        ? "stalled - gridlocked"
                        : "stalled - no workers";
            } else {
                double monthsLeft = buildingManager.monthsLeft(site, perSite);
                eta = Double.isNaN(monthsLeft) ? "done" : "~" + (int) monthsLeft + " mo";
            }

            Label detail = monoLabel(remaining + " left / " + built + " built - " + eta);
            detail.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #8fa3b0;");

            VBox row = new VBox(3);
            row.getChildren().addAll(name, bar, detail);
            list.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane scroller = keptPanelScroller("construction", list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(560);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);

        addBuildLog();
        addDemolitionLog();
    }

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
                + " -fx-text-fill: #ff6b6b; -fx-padding: 14 0 2 0;");
        constructionPanel.getChildren().add(header);

        VBox list = new VBox(6);

        for (DemolitionLog.Entry entry : lost) {

            Label what = new Label(String.format("%,d x %s", entry.quantity, entry.building));
            what.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #ff8f8f;");

            // Fading with age, so the eye goes to what just happened without the
            // older entries disappearing entirely.
            int ago = entry.monthsAgo(game.getMonth());
            String shade = (ago <= 1) ? "#ff6b6b" : (ago <= 6) ? "#c8b0a5" : "#7d8f9c";

            Label when = monoLabel("  " + entry.sector + ", " + entry.when(game.getMonth()));
            when.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                    + " -fx-text-fill: " + shade + ";");

            Label how = monoLabel(entry.wasPaidFor()
                    ? String.format("  plot sold back for %s", money(entry.proceeds))
                    : "  plot abandoned");
            how.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                    + " -fx-text-fill: #7d8f9c;");

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

        Label header = new Label("RECENTLY BUILT");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-text-fill: #5fd68a; -fx-padding: 14 0 2 0;");
        constructionPanel.getChildren().add(header);

        VBox list = new VBox(6);

        for (BuildLog.Entry entry : built) {

            Label what = new Label(String.format("%,d x %s", entry.quantity, entry.building));
            what.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #5fd68a;");

            // Fading with age, same as demolitions, so the eye goes to what just
            // happened without older entries disappearing entirely.
            int ago = entry.monthsAgo(game.getMonth());
            String shade = (ago <= 1) ? "#5fd68a" : (ago <= 6) ? "#9ccc65" : "#7d8f9c";

            Label when = monoLabel("  opened " + entry.when(game.getMonth())
                    + " (" + CityCalendar.formatShort(entry.month) + ")");
            when.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
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
       ===================================================================== */

    /** Wide enough for a glyph and its highlight, narrow enough to be an edge. */
    private static final double RAIL_WIDTH = 46;

    /** The strip under the stage that holds the dome and the time controls. */
    private static final double STRIP_HEIGHT = 72;

    /**
     * One destination.
     *
     * @param key    what the highlight matches on
     * @param glyph  a geometric shape, because the alternative was emoji and a
     *               colour cartoon in the middle of a Courier instrument panel
     *               is the one thing that would look wrong in a Steam screenshot
     * @param name   what the tooltip says. The glyphs are learnable by position;
     *               the name is how you learn them the first time.
     */
    private record Tab(String key, String svg, String name, Runnable go) { }

    /**
     * The rail, in the order a city is actually run.
     *
     * Build first because it is what a new player does first, then the ground
     * to build on, then the money, then the state, then the two screens that
     * only report. The graph is last because it is the one you go to when you
     * have already decided something is wrong.
     */
    /**
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
            new Tab("build",      Icons.BUILD,      "Build",              buildScreen::showBuildMenu),
            new Tab("land",       Icons.LAND,       "Land office",        landScreen::showLandMenu),
            new Tab("population", Icons.POPULATION, "Population",         peopleScreen::showPopulationInfoMenu),
            new Tab("services",   Icons.SERVICES,   "Services",           servicesScreen::showServicesStatsMenu),
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
            new Tab("infrastructure", Icons.INFRASTRUCTURE, "Infrastructure",
                    servicesScreen::showInfrastructureMenu),
            new Tab("sector",     Icons.SECTOR,     "Sector economy",     sectorScreen::showSectorMenu),
            new Tab("government", Icons.GOVERNMENT, "Government economy", governmentScreen::showGovernmentMenu),
            new Tab("finances",   Icons.FINANCES,   "Finances",           financesScreen::showFinanceMenu),
            /*
             * THE BANK IS ITS OWN TAB NOW. Jerus: "i think we are going to have
             * 11 rails, bank is its own thing."
             *
             * It was a button at the foot of Finances, and that was defensible
             * while it was one screen of statements a player consulted before
             * borrowing. It is not: the bank is the counterparty to every loan
             * in the city, it has its own capital, its own funding, its own
             * failure mode and its own bailout - and when it is strained every
             * borrower in the city pays for it. That is a subsystem, and a
             * subsystem reached by scrolling to the bottom of another screen is
             * a subsystem the player finds once and never again.
             */
            new Tab("bank",       Icons.BANK,       "The bank",           bankScreen::showBankMenu),
            new Tab("trade",      Icons.TRADE,      "Trade & the world",  tradeScreen::showForeignMenu),
            new Tab("policy",     Icons.POLICY,     "Policies",           policyScreen::showPolicyMenu),
            new Tab("reports",    Icons.REPORTS,    "Reports",            historyScreen::showHistoryMenu),
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
            case "handleAllBuildingMenus":
            case "showNoDepositMenu": case "showNoLandMenu":
            case "showQuickDebtMenu": case "showFundingFellShortMenu":
                return "build";
            case "showLandMenu":
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
            case "showFinanceMenu": case "showDebtResultMenu":
                return "finances";
            case "showBankMenu":
                return "bank";
            case "showForeignMenu": case "showForeignDefaultMenu":
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
            tabRail.getChildren().add(railButton(
                    t.svg(), t.name(), t.key().equals(active), () -> goHome(t)));
        }

        // The gear sits apart from the destinations because it is not one - it
        // leaves the city rather than moving around inside it.
        Region gap = new Region();
        VBox.setVgrow(gap, Priority.ALWAYS);
        tabRail.getChildren().add(gap);
        tabRail.getChildren().add(railButton(Icons.SETTINGS, "Game menu  (Esc)",
                "showMainMenu".equals(currentScreen), this::showMainMenu));
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
       ===================================================================== */

    /**
     * Press a tab: forget where you were inside it, and land at the top.
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
     * bankArea, tradeArea, policyArea, serviceArea, and openSector doing the
     * same job for Sector - and the area is what the entry point checks:
     *
     *     if (bankArea != null) { drawBankScreen(); return; }
     *
     * Resetting only bankPage would have reset which chip was lit inside an
     * area the player was still stuck in. Four of the five use null for the
     * landing, Services uses a named one, and Build keys off its category.
     *
     * Each case restores the field to the SAME constant its declaration uses,
     * so the two cannot drift apart - a reset that said "Overview" in one place
     * and an initialiser that said "Summary" in the other would be a bug nobody
     * would find for months.
     */
    private void resetSection(String key) {
        switch (key) {
            case "build":      buildScreen.buildCategory = BuildScreen.BUILD_HOME;   break;
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
     * One icon on the rail.
     *
     * DRAWN, NOT TYPED. This used to be a Label carrying a Unicode glyph, with
     * the font family named explicitly because the platform default has no gear
     * at U+2699 and would have drawn an empty box. An SVGPath has no font, so
     * it cannot be missing a character, and it takes its colour from the stroke
     * rather than from a text fill - which is what lets the same eleven marks be
     * grey, blue when active, and paler on hover without three copies of
     * anything.
     *
     * STROKED, NOT FILLED. These are outline icons: fill null, 2px stroke,
     * round caps and joins, exactly as Lucide draws them. Filling them would
     * turn every one into a solid blob.
     */
    private StackPane railButton(String svg, String name, boolean active, Runnable go) {

        javafx.scene.shape.SVGPath mark = new javafx.scene.shape.SVGPath();
        mark.setContent(svg);
        mark.setFill(null);
        mark.setStrokeWidth(2);
        mark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        mark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        mark.setStroke(javafx.scene.paint.Color.web(
                active ? Palette.ACCENT : Palette.TEXT_FAINT));
        /*
         * PINNED TO THE 24-GRID, in a Pane of exactly that size.
         *
         * An SVGPath's layout bounds are the bounds of the INK, not of the
         * drawing it was designed on - so a globe that fills its 24 square and a
         * graph that uses two thirds of one would be centred to different sizes
         * and sit at different heights, and a rail of eleven of those never
         * lines up. A Pane does not resize or move its children, so each icon
         * draws at the coordinates Lucide gave it inside a box the same size for
         * all of them.
         */
        javafx.scene.layout.Pane box = new javafx.scene.layout.Pane(mark);
        box.setPrefSize(24, 24);
        box.setMinSize(24, 24);
        box.setMaxSize(24, 24);
        box.setScaleX(.85);
        box.setScaleY(.85);

        StackPane cell = new StackPane(box);
        cell.setPrefSize(RAIL_WIDTH, 34);
        cell.setMinSize(RAIL_WIDTH, 34);
        // The active tab is a filled block with a blue bar down its inner edge,
        // pointing at the stage - the screen it is currently showing.
        cell.setStyle(active
                ? "-fx-background-color: " + Palette.RAISED + ";"
                + " -fx-border-color: " + Palette.ACCENT + "; -fx-border-width: 0 2 0 0;"
                : "-fx-cursor: hand;");

        Tooltip tip = new Tooltip(name);
        tip.setShowDelay(Duration.millis(250));
        Tooltip.install(cell, tip);

        cell.setOnMouseClicked(e -> go.run());
        if (!active) {
            cell.setOnMouseEntered(e -> {
                cell.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-cursor: hand;");
                mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_HEAD));
            });
            cell.setOnMouseExited(e -> {
                cell.setStyle("-fx-cursor: hand;");
                mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_FAINT));
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
       2. A NEW URGENT NOTICE shows its title, and only its title, under the
          envelope - loud enough to be seen from another screen, quiet enough
          not to be a dialog box in the way of what you were doing.
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

        if (inboxCorner == null) return;
        showIf(inboxCorner, !isGameMenu(currentScreen));
        inboxCorner.getChildren().clear();
        inboxCorner.setAlignment(Pos.TOP_RIGHT);

        Inbox inbox = game.getInbox();
        Notice urgent = inbox.urgent();
        int unread = inbox.unread();

        /* ------------------------- the envelope ------------------------- */
        Label mark = new Label("✉");
        mark.setStyle("-fx-font-family: 'Segoe UI Symbol', 'Segoe UI', sans-serif;"
                + " -fx-font-size: 17px; -fx-text-fill: "
                + (urgent != null ? "#ff6b6b" : unread > 0 ? "#ffb454" : "#7d8f9c") + ";");

        HBox envelope = new HBox(6, mark);
        envelope.setAlignment(Pos.CENTER);
        if (unread > 0) {
            Label count = new Label(String.valueOf(unread));
            count.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;"
                    + " -fx-font-weight: bold; -fx-text-fill: #eceff1;"
                    + " -fx-background-color: " + (urgent != null ? "#c0392b" : "#5a6b74") + ";"
                    + " -fx-background-radius: 8; -fx-padding: 0 6 0 6;");
            envelope.getChildren().add(count);
        }
        envelope.setStyle("-fx-padding: 6 10 6 10; -fx-background-color: #1c262b;"
                + " -fx-background-radius: 4; -fx-border-color: #37474f;"
                + " -fx-border-radius: 4; -fx-cursor: hand;");
        envelope.setOnMouseClicked(e -> {
            inboxOpen = !inboxOpen;
            refreshInbox();
        });
        Tooltip.install(envelope, new Tooltip(inbox.size() == 0
                ? "Nothing to report" : inbox.size() + " in the inbox"));

        HBox topRow = new HBox(envelope);
        topRow.setAlignment(Pos.CENTER_RIGHT);
        inboxCorner.getChildren().add(topRow);

        /* ----------------- the one line an urgent notice gets -----------------
         *
         * Only when the list is shut. Once it is open the notice is in the list
         * like everything else, and saying it twice would be the banner again.
         */
        if (!inboxOpen && urgent != null) {
            Label title = new Label("[!]  " + urgent.getTitle());
            title.setWrapText(true);
            title.setMaxWidth(INBOX_WIDTH - 60);
            title.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold;"
                    + " -fx-text-fill: #ffd9d4; -fx-background-color: #3b1f1f;"
                    + " -fx-background-radius: 4; -fx-border-color: #c0392b;"
                    + " -fx-border-radius: 4; -fx-padding: 7 10 7 10; -fx-cursor: hand;");
            title.setOnMouseClicked(e -> {
                game.getInbox().markRead(urgent, game.getMonth());
                inboxExpanded = urgent.getKey();
                inboxOpen = true;
                refreshInbox();
            });
            VBox.setMargin(title, new javafx.geometry.Insets(6, 0, 0, 0));
            inboxCorner.getChildren().add(title);
        }

        if (!inboxOpen) return;

        /* --------------------------- the list --------------------------- */
        VBox list = new VBox(3);
        list.setStyle("-fx-background-color: #17212c; -fx-background-radius: 4;"
                + " -fx-border-color: #37474f; -fx-border-radius: 4;"
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
                + " -fx-text-fill: #8fa3b0; -fx-padding: 0 0 4 2;");
        list.getChildren().add(heading);

        if (game.getInbox().size() == 0) {
            Label none = new Label("Nothing has gone wrong yet.");
            none.setStyle("-fx-font-size: 12px; -fx-text-fill: #7d8f9c; -fx-padding: 2 0 2 2;");
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
        VBox.setMargin(scroller, new javafx.geometry.Insets(6, 0, 0, 0));
        inboxCorner.getChildren().add(scroller);
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
                + (done ? "#6b7a84" : notice.isRead() ? "#c3ccd3" : "#ff9e9e") + ";");

        Label when = new Label(done
                ? CityCalendar.format(notice.getRaised()) + "  ·  settled "
                        + CityCalendar.format(notice.getResolved())
                : CityCalendar.format(notice.getRaised()));
        when.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                + " -fx-text-fill: #6b7a84;");

        VBox head = new VBox(1, title, when);
        head.setStyle("-fx-padding: 5 4 5 4; -fx-cursor: hand;"
                + (open ? " -fx-background-color: #22303c; -fx-background-radius: 3;" : ""));
        head.setOnMouseClicked(e -> {
            game.getInbox().markRead(notice, game.getMonth());
            inboxExpanded = open ? "" : notice.getKey();
            refreshInbox();
        });

        VBox row = new VBox(0, head);
        row.setStyle(done ? "" : "-fx-border-color: "
                + (notice.isRead() ? "#37474f" : "#c0392b")
                + "; -fx-border-width: 0 0 0 2;");

        if (!open) return row;

        VBox body = new VBox(0);
        body.setStyle("-fx-padding: 2 4 8 10;");
        for (String line : notice.getBody()) {
            Label text = new Label(line.isEmpty() ? " " : line);
            text.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11.5px;"
                    + " -fx-text-fill: " + (done ? "#6b7a84" : "#c3ccd3") + ";");
            body.getChildren().add(text);
        }

        // A settled notice gets no button. There is nothing to deal with, and
        // an enabled button that fixes something already fixed is a lie.
        if (!done) {
            Button act = new Button(dealLabel(notice.getKey()));
            act.setStyle("-fx-font-size: 11px; -fx-background-color: #2f7d52;"
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
            case "healthcare": return "Go and build healthcare →";
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
            case "healthcare":
                buildScreen.handleAllBuildingMenus("Healthcare", EnumSet.of(BuildingType.HEALTHCARE));
                break;
            default:
                refreshInbox();
        }
    }

    /* =====================================================================
       TIME, AND WHAT THE MONTH IS WORTH

       The two things that used to be buttons in the column - Next Month and
       Simulate Multiple Months - are round now and pinned to the bottom right,
       because they are the only two controls in the game that are true of every
       screen. A player reading the household books should not have to leave to
       advance a month.
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

                if (!clockRunning || game == null || isGameMenu(currentScreen)) {
                    // A month landed and its redraw was throttled away; the
                    // clock has since stopped, so nothing else will draw it.
                    if (redrawPending && game != null && !isGameMenu(currentScreen)) {
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
                if (landed && sinceRedraw >= REDRAW_EVERY) {
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
     * The day, repainted in place.
     *
     * Deliberately NOT a redraw. At 1x this runs sixty times a second and the
     * month behind it has not changed, so rebuilding the screen for it would be
     * rebuilding the same screen sixty times a second. One label's text.
     */
    private void paintDay() {
        if (dayLabel == null) return;
        dayLabel.setText(CityCalendar.formatDay(game.getMonth(), monthProgress));
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

    private void refreshTimeControls() {

        if (timeControls == null) return;
        boolean inCity = !isGameMenu(currentScreen);
        showIf(timeControls, inCity);
        showIf(incomeDome, inCity);
        timeControls.getChildren().clear();

        /*
         * ONE BUTTON NOW, where there were two.
         *
         * "Advance one month" and "simulate several months" both went: the
         * first is what play/pause is, and the second is what 10x is. Jerus's
         * call - "play/pause only" - and the corner is better for it, because
         * three controls that all mean "time" is three ways to ask the same
         * question.
         */
        Button play = roundButton(clockRunning ? "❙❙" : "▶", 56,
                clockRunning ? "#2f6fa8" : "#3a7d44",
                clockRunning ? "Pause" : "Play");
        play.setOnAction(e -> {
            setClockRunning(!clockRunning);
            redrawScreen.run();
        });

        timeControls.setAlignment(Pos.BOTTOM_RIGHT);
        /*
         * AND WHY IT STOPPED, when it stopped itself. A clock that halts with
         * no explanation is a bug as far as the player is concerned - they did
         * not press anything and time stopped. One line, next to the button
         * they are about to press to start it again.
         */
        if (pausedBecause != null && !clockRunning) {
            Label why = new Label(pausedBecause);
            why.setStyle("-fx-font-size: 11px; -fx-text-fill: #ffb454;"
                    + " -fx-background-color: #2a2118; -fx-background-radius: 3;"
                    + " -fx-padding: 3 8 3 8;");
            why.setMaxWidth(220);
            why.setWrapText(true);
            timeControls.getChildren().add(why);
        }
        timeControls.getChildren().addAll(yearDial(), speedSlider(), play);

        refreshIncomeDome();
    }

    /**
     * The speed, as a slider that sticks to the ladder.
     *
     * SNAPPING IS THE WHOLE DESIGN. A free slider gives a player 7.3x, which is
     * not a speed anybody chose and is not a speed they can describe or get
     * back to. Snapping gives them the drag - the gesture that suits a speed -
     * and a value that is always one of seven.
     *
     * The tick marks are the stops, so the ladder is visible rather than
     * discovered, and the reading beside it is the number rather than the
     * index: a player thinks in "5x", not in "position 5 of 7".
     */
    private HBox speedSlider() {

        javafx.scene.control.Slider bar =
                new javafx.scene.control.Slider(0, SPEEDS.length - 1, speedIndex);
        bar.setMajorTickUnit(1);
        bar.setMinorTickCount(0);
        bar.setSnapToTicks(true);
        bar.setShowTickMarks(true);
        bar.setBlockIncrement(1);
        bar.setPrefWidth(132);
        bar.setTooltip(new Tooltip("How fast a month passes. "
                + (int) SECONDS_PER_MONTH + " seconds a month at 1x."));

        Label reading = new Label(speedLabel(speedIndex));
        reading.setMinWidth(38);
        reading.setAlignment(Pos.CENTER_RIGHT);
        reading.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;"
                + " -fx-font-weight: bold; -fx-text-fill: #cfd8dc;");

        bar.valueProperty().addListener((o, was, now) -> {
            int pick = (int) Math.round(now.doubleValue());
            pick = Math.max(0, Math.min(SPEEDS.length - 1, pick));
            speedIndex = pick;
            reading.setText(speedLabel(pick));
            // Snap the handle itself, so a half-dragged slider settles on a stop
            // rather than sitting between two of them looking adjustable.
            if (Math.abs(now.doubleValue() - pick) > 1e-9) bar.setValue(pick);
        });

        HBox box = new HBox(6, bar, reading);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /** "0.25x", "1x", "10x" - no trailing zeros on the round ones. */
    private static String speedLabel(int index) {
        double s = SPEEDS[index];
        return (s == Math.floor(s) ? String.valueOf((int) s) : String.valueOf(s)) + "x";
    }

    /** The month the dial is currently showing, so it only pops when it moves. */
    private int dialAt = -1;

    /* =====================================================================
       TWELVE PIPS, AND ONE OF THEM MOVES.

       Jerus: "in the next month button, perhaps add something so that the
       player knows that a month passed or something, perhaps a small count of
       the month of the year."

       The date bar has said the date all along and it was not enough, because
       nothing about it MOVES: February becomes March in the same place, in the
       same weight, forty pixels from where the player is looking. The click is
       at the bottom right and the confirmation was at the top left.

       So the confirmation moved to the click. Twelve pips beside the button,
       filled up to this month, and the current one lights and pops when the
       month lands - a quarter of a second of motion right under the pointer.
       The pips also do the counting the request asked for: how far through the
       year the city is, which is the one thing the date does not say at a
       glance, and it resets in January so a year turning over is visible as a
       row emptying rather than as a number nobody was watching.

       It pops only when the month CHANGES. refreshTimeControls runs on every
       redraw - opening a screen, buying a building, closing a section - and a
       dial that flashed on all of those would be noise, which is the opposite
       of a signal.
       ===================================================================== */
    private VBox yearDial() {

        int month = game.getMonth();
        int of = CityCalendar.monthOfYear(month);

        HBox pips = new HBox(3);
        pips.setAlignment(Pos.CENTER_RIGHT);

        Region live = null;
        for (int m = 1; m <= 12; m++) {
            Region pip = new Region();
            pip.setMinSize(5, 5);
            pip.setPrefSize(5, 5);
            pip.setMaxSize(5, 5);
            pip.setStyle("-fx-background-radius: 3; -fx-background-color: "
                    + (m == of ? Palette.ACCENT
                              : m < of ? Palette.ACCENT_FILL : "#33434d") + ";");
            if (m == of) live = pip;
            pips.getChildren().add(pip);
        }

        Label reading = new Label(CityCalendar.shortMonthName(month).toUpperCase()
                + "  \u00b7  " + of + " of 12");
        reading.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        VBox dial = new VBox(4, pips, reading);
        dial.setAlignment(Pos.CENTER_RIGHT);
        dial.setStyle("-fx-padding: 0 6 6 0;");
        Tooltip.install(dial, new Tooltip(
                "How far through " + CityCalendar.yearOf(month) + " the city is."
                + " One pip a month; the row empties in January."));

        if (live != null && dialAt >= 0 && dialAt != month) popPip(live);
        dialAt = month;
        return dial;
    }

    /** A quarter second of "that landed", on the pip the month just filled. */
    private static void popPip(Region pip) {
        javafx.animation.ScaleTransition pop =
                new javafx.animation.ScaleTransition(Duration.millis(130), pip);
        pop.setFromX(1);
        pop.setFromY(1);
        pop.setToX(2.6);
        pop.setToY(2.6);
        pop.setCycleCount(2);
        pop.setAutoReverse(true);
        pop.play();
    }

    /** A circle with a glyph in it. */
    private Button roundButton(String glyph, double size, String fill, String tip) {
        Button button = new Button(glyph);
        button.setMinSize(size, size);
        button.setPrefSize(size, size);
        button.setMaxSize(size, size);
        /*
         * PADDING ZERO, or the glyph is replaced by an ellipsis.
         *
         * A Button keeps its default 0.333em/0.667em padding even when its size
         * is pinned, so on the 40px button the two arrows had about 20px to live
         * in and JavaFX did what it does to text that will not fit: it drew
         * "...". Play-tested - the simulate button read as three dots.
         */
        button.setPadding(javafx.geometry.Insets.EMPTY);
        button.setStyle("-fx-font-family: 'Segoe UI Symbol', 'Segoe UI', sans-serif;"
                + " -fx-font-size: " + (size / 2.8) + "px; -fx-padding: 0;"
                + " -fx-background-color: " + fill + ";"
                + " -fx-text-fill: #eceff1;"
                + " -fx-background-radius: " + (size / 2) + ";"
                + " -fx-border-color: #4a5c68; -fx-border-radius: " + (size / 2) + ";"
                + " -fx-cursor: hand;");
        Tooltip tooltip = new Tooltip(tip);
        tooltip.setShowDelay(Duration.millis(250));
        button.setTooltip(tooltip);
        return button;
    }

    /**
     * The half circle at the foot of the stage.
     *
     * Jerus: "just on the bottom like above the section where the debts are
     * listed, have a half circle, where the net income is shown - later it will
     * have other features as well."
     *
     * Net income for now, and the shape is doing the work: a dome rising out of
     * the debt strip reads as a gauge rather than as another number in a row of
     * numbers, which is what it was when it lived as a caption under the cash
     * figure. That caption stays where it is - it says what the TREASURY is
     * doing, next to the treasury, and this says what the month is worth. They
     * are the same figure answering two different questions.
     *
     * Built as a stack with a body VBox so the other features have somewhere to
     * go without moving anything.
     */
    private void refreshIncomeDome() {

        if (incomeDome == null) return;
        incomeDome.getChildren().clear();

        double income = game.getIncome();
        boolean up = income >= 0;

        Label figure = new Label((up ? "+" : "\u2212") + money(Math.abs(income)));
        figure.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 17px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (up ? "#8fe0aa" : "#ff6b6b") + ";");

        Label caption = new Label("NET INCOME  /mo");
        caption.setStyle("-fx-font-size: 8px; -fx-text-fill: #78909c;");

        VBox body = new VBox(-1, figure, caption);
        body.setAlignment(Pos.CENTER);

        /* ===================================================================
           ...AND WHAT THE BALANCE ACTUALLY DID

           Jerus: "show how much was the actual month change, like in the next
           month button it shows 3k but sometimes cause of land buybacks or
           sales it was actually more or less."

           He is right, and there are TWO reasons rather than one, which is why
           this needed a second figure and not a corrected first one:

             1. The big figure is EconomyManager.getTotalIncome(): tax less
                interest, pensions, healthcare and schools. It does not include
                what the city spent on buildings or on land, and it does not
                include what land sales brought in. That is the land the player
                noticed.
             2. Issuing paper raises cash and is not income; repaying principal
                spends cash and is not an expense. Both move the balance and
                neither can appear above.

           So the dome says both: what the month EARNED, large, because that is
           what it has always said, and what the treasury actually BANKED under
           it, in the colour of the direction it went. The tooltip does the
           arithmetic; the Government Overview page does it in full.

           Only when they differ, or it is one number printed twice.
           =================================================================== */
        final String[] why = { null };

        if (game.hasTreasuryMonth()) {

            double moved = game.getTreasuryChange();

            if (Math.abs(moved - income) > .5) {

                boolean grew = moved >= 0;
                Label actual = new Label("banked " + (grew ? "+" : "\u2212")
                        + money(Math.abs(moved)));
                actual.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 9px;"
                        + " -fx-text-fill: " + (grew ? "#8fe0aa" : "#ff8f8f") + ";");

                double surplus = game.getTreasurySurplus();
                why[0] = String.format(
                        "EARNED  %s%s%n"
                        + "    what the month made: tax in, less interest, pensions, care "
                        + "and schools. It does not count what the city spent on buildings "
                        + "or land, or what land sales brought in.%n%n"
                        + "BANKED  %s%s%n"
                        + "    what the balance actually did between one press of this "
                        + "button and the next \u2014 your land, your buildings and your "
                        + "borrowing included.%n%n"
                        + "The whole budget came to %s%s. On top of it, paper issued raised "
                        + "%s \u2014 borrowed, not earned \u2014 and %s went back to lenders "
                        + "as principal, which shrinks a debt rather than buying anything.",
                        income >= 0 ? "+" : "\u2212", money(Math.abs(income)),
                        grew ? "+" : "\u2212", money(Math.abs(moved)),
                        surplus >= 0 ? "+" : "\u2212", money(Math.abs(surplus)),
                        money(game.getTreasuryRaised()),
                        money(game.getTreasuryRepaid()));

                body.getChildren().add(actual);
            }
        }

        // Taller when the second figure is there, so the dome grows to fit it
        // rather than clipping the caption - it sits on the debt strip and the
        // strip is what moves, not the numbers.
        double tall = body.getChildren().size() > 2 ? 74 : 62;

        StackPane dome = new StackPane(body);
        dome.setPrefSize(168, tall);
        dome.setMinSize(168, tall);
        dome.setMaxSize(168, tall);
        // Rounded at the top corners only, so it is a dome sitting on the debt
        // strip rather than a floating pill.
        dome.setStyle("-fx-background-color: #1c262b;"
                + " -fx-background-radius: 84 84 0 0;"
                + " -fx-border-color: " + (up ? "#37474f" : "#7a3b3b") + ";"
                + " -fx-border-width: 2 2 0 2;"
                + " -fx-border-radius: 84 84 0 0;"
                + " -fx-padding: 8 0 0 0;");

        /*
         * AND IT IS A DOOR. Jerus: "i want so that you click the semi circle in
         * the bottom it takes you to the government rail."
         *
         * The right target, too: every figure on the dome - what the month
         * earned, what the treasury banked, and the gap between them - is
         * reconciled line by line on Government -> Overview. A player who
         * notices the two numbers disagree is one click from the page that
         * explains it rather than from a rail they have to guess at.
         */
        dome.setStyle(dome.getStyle() + " -fx-cursor: hand;");
        dome.setOnMouseClicked(e -> {
            governmentScreen.govPage = "Overview";
            innerScrollAt.remove("showGovernmentMenu:body");
            governmentScreen.showGovernmentMenu();
        });

        /*
         * ONE TOOLTIP, ON THE DOME, and it carries the click hint.
         *
         * Installed on the shape rather than on the figures inside it, because
         * JavaFX shows the innermost and a hint that only appears over eight
         * pixels of padding is a hint nobody finds.
         */
        Tooltip open = new Tooltip((why[0] == null
                ? "What the city earned this month."
                : why[0]) + "\n\nClick for the books, line by line.");
        open.setShowDelay(Duration.millis(350));
        open.setWrapText(true);
        open.setMaxWidth(360);
        Tooltip.install(dome, open);

        incomeDome.getChildren().add(dome);
    }
}
