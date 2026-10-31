package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;

/**
 * The Rankings tab: the active city on a board of the real world's nations and city regions (or of the
 * nations of its own world), measure by measure, in the money and at the prices the player picked, with the
 * sentence that says exactly what is on screen. The screen draws what {@link Rankings} and {@link CityMeasures}
 * hand it and computes none of it.
 *
 * WHY. Players ask where their city stands, and the answer is only as honest as the money it is in; the
 * model (Rankings, LB2's first slice) turns every figure into real 2015 dollars once and shows it in the money
 * picked, so this screen's job is to keep the choice in front of the player: four controls, one sentence under
 * them, three columns. It builds spec-leaderboards 9 (the screen), 7 (the (i) and the marks) and 3 (the
 * sentence, the lock on Prices) and Jerus' calls of 2026-10-21: a rail tab after History with Lucide's trophy in
 * money blue (D5), the (i) and a mark on every row (D6), it opens on GDP and the full list (D9), a measure no
 * real place publishes ranks only your cities and its real rows are "-" (D10), and the boards are named Real
 * world 2025 and Your world (D16).
 *
 * LB3 puts the player's other saved cities on the board (Your cities, Your cities at Now / Your age, a row and a drawer of their
 * own, Prices open beside them: D2, D3, D5), read off the screen's thread by SavedCities, and the city's rank over the
 * years on its card (D12).
 */
final class RankingsScreen {

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE STATE

       Held in the screen and not saved: what the player picked on this visit.
       The model has no say in it, and a new game starts the tab from the
       opening again (Settings.OPENING, GDP, the full list).

       THE CITY IS COMPUTED ONCE A MONTH. Rankings.City.of(game) reads the
       history a series at a time, about a millisecond; a click redraws the
       whole page, and the page is redrawn on the clock as well, so the city is
       kept with the month and the game it was made for and made again only
       when either moves. WorldMeasures.load() is cached by the model already.
       ===================================================================== */

    /** The measure the tab opens on (D9): GDP. */
    static final String OPENS_ON = "gdp";

    /** The measures pinned the first time the tab opens: the six a player most often asks of a city. */
    static final List<String> PINNED_AT_FIRST = List.of("gdp", "gdp_pp", "growth_real", "people", "unemployment", "inflation");

    /** The ten groups of the file, by their key, as the spec's table names them (spec-leaderboards 6). */
    static final Map<String, String> GROUPS = Map.of(
            "economy", "Economy", "work", "Work & pay", "prices", "Prices & money", "public", "Public money",
            "banks", "Banks & markets", "people", "People", "housing", "Housing", "safety", "Safety",
            "health", "Health & care", "land", "Land, power & travel");

    /**
     * The lists the filter chips keep; the city's own row stays in each, so the player is always on the board they are looking at.
     * YOUR_CITIES (LB3) keeps the city and the saved cities; the saved rows are in All, Your size (by their people) and Your cities,
     * and in neither Nations nor Cities, which are the real world's places.
     */
    enum Filter {
        ALL("All"), NATIONS("Nations"), CITIES("Cities"), YOUR_SIZE("Your size"), YOUR_CITIES("Your cities");

        final String label;

        Filter(String label) { this.label = label; }
    }

    private final UserInterface ui;

    private Rankings.Settings settings = Rankings.Settings.OPENING;
    private String measure = OPENS_ON;
    private boolean aroundYou;
    private Filter filter = Filter.ALL;
    private final Set<String> pinned = new LinkedHashSet<>(PINNED_AT_FIRST);
    private String drawer;
    private boolean browseAll;

    private Rankings.City city;
    private Game cityOf;
    private String cityNamed;
    private int cityMonth = -1;

    RankingsScreen(UserInterface ui) { this.ui = ui; }

    /** The active city at its month: made again only when the game, the city or the month is not the one it was made for. */
    private Rankings.City cityNow() {
        Game g = ui.game;
        int month = g.getMonth();
        if (city == null || cityOf != g || cityMonth != month || !g.getCityName().equals(cityNamed)) {
            city = Rankings.City.of(g);
            cityOf = g;
            cityMonth = month;
            cityNamed = g.getCityName();
        }
        return city;
    }

    private void with(Rankings.Board board, Rankings.Money money, Rankings.Prices prices, Rankings.Convert convert) {
        settings = new Rankings.Settings(board, money, prices, convert);
    }

    /* =====================================================================
       THE RANKINGS TAB (LB3): YOUR SAVED CITIES

       The other slots, 1 to 10, rank beside the active city (SavedCities, the
       autosave left out). A history file is up to 26 MB and nine of them are
       200 MB of numbers, and showRankingsMenu() runs on every monthly redraw,
       so the files are NEVER read on the FX thread:

         - SavedCities.refresh(files) runs on WORKER, one daemon thread, once
           per ENTRY to the tab (the screen shown before clearMenu is not this
           one) and once for openSaved(). One read in flight at a time. The
           worker is handed the GameFiles and the last snapshot and hands back a
           snapshot; it touches no node, no Game and none of this screen's state.
         - The snapshot comes back through the volatile `saved` and
           Platform.runLater, which draws the tab again only if it is still the
           screen shown (and not at all when the refresh found every slot as the
           last read had it).
         - Until the first read has come back `saved` is null: the chip says
           "Your cities (reading…)" and the board has no saved rows.
         - SavedCities.at(age) is cheap (a month's figures off the kept series)
           and runs on the FX thread, but only when the snapshot, the active
           city's month or the Now / Your age choice is not the one it was made
           for (placeSaved), not at each redraw.
       ===================================================================== */

    /** The one thread the saved cities are read on, away from the screen; it dies with the window. */
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "rankings-saved-cities");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    /** The saved cities as the last read found them; null until the first read has come back. Written on the FX thread, from the worker's answer. */
    private volatile SavedCities saved;
    /** A read is on WORKER (the FX thread's flag: set when it is handed over, cleared when its answer arrives). */
    private boolean reading;
    /** Your cities at: the saved cities as they were when the active city was as old as it is now (D3), else as last saved. */
    private boolean atAge;

    /** The saved cities on the board, by the snapshot, the active city's month and Now / Your age they were made for; the lines about them are made with them. */
    private SavedCities onBoardOf;
    private int onBoardMonth = -1;
    private boolean onBoardAtAge;
    private List<Rankings.Saved> onBoard = List.of();
    private Map<String, Rankings.Saved> onBoardById = Map.of();
    private boolean savedReadable;
    private String notAtAge, leftOut;

    /** The rank over the years, kept for the measure and the conversion it was worked out for; worked out again on entering the tab. */
    private List<Rankings.RankPoint> overYears = List.of();
    private String overKey = "";

    /** Starts a read of the saved cities on WORKER unless one is in flight; the answer redraws the tab if it is still on screen. */
    private void readSaved() {
        if (reading) return;
        reading = true;
        GameFiles files = ui.game.getGameFiles();
        SavedCities was = saved;
        WORKER.execute(() -> {
            SavedCities next = was;
            try {
                next = (was == null ? SavedCities.NONE : was).refresh(files);
            } catch (RuntimeException | OutOfMemoryError e) {
                GameLog.note("Rankings: could not read the saved cities: " + e);
            } finally {
                SavedCities answer = next == null ? SavedCities.NONE : next;
                Platform.runLater(() -> arrived(answer, was != null));
            }
        });
    }

    /** A read's answer, on the FX thread: kept, and the tab drawn again if it is on screen and the answer is news. */
    private void arrived(SavedCities answer, boolean hadOne) {
        reading = false;
        saved = answer;
        if (hadOne && answer.reads() == 0) return;
        if (ui.isShowing("showRankingsMenu")) showRankingsMenu();
    }

    /**
     * The saved cities on the board for the active city's month, and the lines under the table about the ones that are not: made again only when the
     * snapshot, the month or Now / Your age is not what they were made for.
     */
    private void placeSaved(Rankings.City now) {
        SavedCities snap = saved == null ? SavedCities.NONE : saved;
        if (snap == onBoardOf && now.month() == onBoardMonth && atAge == onBoardAtAge) return;
        onBoardOf = snap;
        onBoardMonth = now.month();
        onBoardAtAge = atAge;
        onBoard = atAge ? snap.at(now.month()) : snap.now();
        Map<String, Rankings.Saved> byId = new HashMap<>();
        for (Rankings.Saved sv : onBoard) byId.put(sv.id(), sv);
        onBoardById = byId;
        savedReadable = !snap.now().isEmpty();

        notAtAge = null;
        if (atAge) {
            List<String> parts = new ArrayList<>();
            Set<Integer> named = new HashSet<>();
            for (Rankings.Saved sv : onBoard) named.add(sv.slot());
            for (SavedCities.Stopped st : snap.stoppedAt(now.month())) {
                named.add(st.slot());
                parts.add(st.name() + " (slot " + st.slot() + ") stopped at " + CityCalendar.formatShort(st.lastMonth()));
            }
            for (Rankings.Saved sv : snap.now()) {
                if (!named.contains(sv.slot())) parts.add(sv.city().name() + " (slot " + sv.slot() + ") keeps no figures for that month");
            }
            if (!parts.isEmpty()) notAtAge = "Not at " + Rankings.ageWords(now.month()) + ": " + String.join("; ", parts) + ".";
        }
        leftOut = leftOutLine(snap.left());
    }

    /** The quiet line about the saves the board left out: "2 saved cities from older builds are left out."; null when none was. */
    private static String leftOutLine(SavedCities.LeftOut left) {
        int[] counts = {left.olderBuild(), left.newerBuild(), left.damaged(), left.noHistory()};
        String[] one = {"from an older build", "from a newer build", "that could not be read", "with no history"};
        String[] many = {"from older builds", "from newer builds", "that could not be read", "with no history"};
        List<String> parts = new ArrayList<>();
        int kinds = 0, last = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 0) continue;
            kinds++;
            last = i;
            parts.add(counts[i] + " " + (counts[i] == 1 ? one[i] : many[i]));
        }
        if (kinds == 0) return null;
        if (kinds == 1) {
            int n = counts[last];
            return n + " saved " + (n == 1 ? "city " + one[last] + " is" : "cities " + many[last] + " are") + " left out.";
        }
        return left.total() + " saved cities are left out: " + String.join(", ", parts) + ".";
    }

    /**
     * The door in from the main menu's Compare (D5): the tab on the Your cities filter, the board and the money as the player had them. The tab is
     * entered by the draw below, which asks for a read as any entry does; the read is asked for here first so that it is on WORKER before the page is built.
     */
    void openSaved() {
        filter = Filter.YOUR_CITIES;
        readSaved();
        showRankingsMenu();
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME

       THE FRAME DOES NOT SCROLL, as City History's does not: the head, the
       four controls and the sentence stay in view, because the sentence is
       what every figure under it has to be read against; the three columns
       are the body, through ui.scrolled(), whose position the shell keeps
       through a month's redraw and a click (clearMenu's AND THE SCROLL STAYS
       WHERE YOU PUT IT). Nothing here moves it.
       ===================================================================== */

    /** The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. */
    static final double FRAME_CHROME = 210;

    /** What the stage keeps under the scroller once the frame is laid out: City History's. */
    static final double STAGE_REST = 36;

    /** The page's width before the scroller says it, and the widths of the columns. */
    static final double FIRST_WIDTH = 1100, MEASURES_W = 250, CARD_W = 400, TABLE_MIN = 560, COLUMN_GAP = 16;

    private double pageWidth = FIRST_WIDTH;
    private VBox columns, measuresBox, tableBox, cardBox;

    void showRankingsMenu() {
        // Entering the tab (the screen shown is another) and a redraw of it (the clock, a click) are told apart before clearMenu records this one.
        boolean entering = !ui.isShowing("showRankingsMenu");
        ui.clearMenu("showRankingsMenu", () -> showRankingsMenu());
        if (entering) {
            overKey = "";
            readSaved();
        }
        WorldMeasures wm = WorldMeasures.load();
        Nations nations = ui.game.getNations();
        if (nations == null && settings.board() == Rankings.Board.YOUR_WORLD) {
            with(Rankings.Board.REAL_2025, settings.money(), settings.prices(), settings.convert());
        }
        if (wm.measure(measure) == null) measure = OPENS_ON;
        Rankings.City now = cityNow();
        placeSaved(now);
        WorldMeasures.Measure m = wm.measure(measure);
        Rankings.Table table = Rankings.table(wm, nations, now, onBoard, settings, measure);

        VBox frame = new VBox(Palette.GAP, head(wm, now), controls(now, m, nations != null), sentenceLine(now, m, nations));

        measuresBox = measuresColumn(wm, m);
        tableBox = tableColumn(wm, now, table, m);
        cardBox = cardColumn(wm, nations, now, table, m);
        columns = new VBox(COLUMN_GAP);
        arrange(pageWidth);

        VBox body = Pieces.widePage();
        body.getChildren().add(columns);
        frameOver(frame, body);
    }

    /**
     * The nation's card on the map is the door in (spec-leaderboards 9, "All its measures"): the tab opens on Real
     * world 2025 with that place's drawer open, the measure kept. A place the file does not know opens no drawer.
     */
    void openOn(String placeId) {
        with(Rankings.Board.REAL_2025, settings.money(), settings.prices(), settings.convert());
        drawer = placeId != null && WorldMeasures.load().place(placeId) != null ? placeId : null;
        showRankingsMenu();
    }

    /** The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame (City History's frameOver). */
    private void frameOver(VBox frame, VBox body) {
        frame.setMaxWidth(Pieces.PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        ScrollPane scroller = ui.scrolled(body, FRAME_CHROME, false);
        scroller.prefHeightProperty().unbind();
        scroller.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(260, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)),
                ui.menuScroller.heightProperty(), frame.heightProperty()));
        // The frame as wide as the page under it (not as wide as its widest line): the four controls sit in one row where they fit, the sentence runs the page's width, and the head and the columns share a left edge.
        javafx.beans.binding.DoubleBinding pageGiven = javafx.beans.binding.Bindings.createDoubleBinding(
                () -> given(scroller.getViewportBounds().getWidth()), scroller.viewportBoundsProperty());
        body.prefWidthProperty().bind(pageGiven);
        frame.prefWidthProperty().bind(pageGiven);
        scroller.viewportBoundsProperty().addListener((o, was, now) -> {
            if (now != null && now.getWidth() > 0) follow(given(now.getWidth()) - 36);
        });
        ui.rootMenu.getChildren().addAll(frame, scroller);
    }

    /** The page's width for a viewport this wide: all of it, from 320 up to PAGE_WIDE. */
    private static double given(double viewport) {
        return Math.min(Pieces.PAGE_WIDE, Math.max(320, viewport));
    }

    /** The page laid out at a new width, without a redraw: the columns go side by side, or stack. */
    private void follow(double width) {
        if (!(width > 0) || Math.abs(width - pageWidth) < 1) return;
        pageWidth = width;
        arrange(width);
    }

    /**
     * The three columns for a page this wide: side by side from MEASURES_W + TABLE_MIN + CARD_W and the gaps; below
     * that the card goes over the other two (it is the answer, and a player on a small window should not have to
     * scroll past 259 rows to find it), and below that the three stack.
     */
    private void arrange(double inner) {
        if (columns == null) return;
        double gap = COLUMN_GAP;
        if (inner >= MEASURES_W + TABLE_MIN + CARD_W + 2 * gap) {
            width(measuresBox, MEASURES_W);
            width(tableBox, inner - MEASURES_W - CARD_W - 2 * gap);
            width(cardBox, CARD_W);
            HBox across = new HBox(gap, measuresBox, tableBox, cardBox);
            across.setAlignment(Pos.TOP_LEFT);
            across.setFillHeight(false);
            columns.getChildren().setAll(across);
        } else if (inner >= MEASURES_W + TABLE_MIN + gap) {
            width(cardBox, Math.min(inner, CARD_W));
            width(measuresBox, MEASURES_W);
            width(tableBox, inner - MEASURES_W - gap);
            HBox pair = new HBox(gap, measuresBox, tableBox);
            pair.setAlignment(Pos.TOP_LEFT);
            pair.setFillHeight(false);
            columns.getChildren().setAll(cardBox, pair);
        } else {
            width(cardBox, inner);
            width(measuresBox, inner);
            width(tableBox, inner);
            columns.getChildren().setAll(cardBox, measuresBox, tableBox);
        }
    }

    private static void width(Region box, double w) {
        box.setMinWidth(w);
        box.setPrefWidth(w);
        box.setMaxWidth(w);
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE

       The title with its swatch and its (i), and under it the city's name,
       its month and its people. Then the four controls of spec-leaderboards
       9, each a labelled strip of chips (Pieces.chipStrip):

         Board       Real world 2025 | Your world
         Money       the city's money | game US$ | real US$
         Prices      per year | static
         Convert at  this month's rate | constant 2015 | PPP

       A choice that is not open is greyed and says why in its tooltip: Your
       world when the game has no nations to rank, and Prices whenever the
       model locks it (Rankings.pricesLocked: real US$, Constant, a measure
       that is not money, and while no saved city is on the board, as it moves
       only the saved rows: LB3 opens it with the first).
       A greyed chip is not disabled: a disabled node gets no pointer events,
       so its tooltip would never show.
       ===================================================================== */

    private Node head(WorldMeasures wm, Rankings.City now) {
        HBox title = Pieces.pageHead("Rankings", Palette.MONEY, null, null, info(wm));
        long people = ui.game.getPopulationManager().getPopulation();
        Label line = words(now.name() + " · " + CityCalendar.format(now.month()) + " · " + Money.people(people) + " people",
                Palette.SIZE_BODY, Palette.TEXT_LABEL);
        return new VBox(2, title, line);
    }

    private Node controls(Rankings.City now, WorldMeasures.Measure m, boolean haveNations) {
        FlowPane row = new FlowPane(22, 6);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] boards = {"Real world 2025", "Your world"};
        HBox board = strip(boards, boards[settings.board().ordinal()],
                i -> with(Rankings.Board.values()[i], settings.money(), settings.prices(), settings.convert()));
        if (!haveNations) {
            grey(board, 1, "Your world needs the nations of the game's world, and this city has none to show.");
        }

        String[] moneys = {now.currency(), "Game US$", "Real US$"};
        HBox money = strip(moneys, moneys[settings.money().ordinal()],
                i -> with(settings.board(), Rankings.Money.values()[i], settings.prices(), settings.convert()));

        String[] prices = {"Per year", "Static"};
        HBox price = strip(prices, prices[settings.prices().ordinal()],
                i -> with(settings.board(), settings.money(), Rankings.Prices.values()[i], settings.convert()));
        // Prices moves only the saved rows (D2): it is open when one is on the board, locked otherwise (and by the model's other reasons).
        if (Rankings.pricesLocked(settings, m, !onBoard.isEmpty())) {
            String why = Rankings.pricesLockedWhy(settings, m, !onBoard.isEmpty());
            for (int i = 0; i < prices.length; i++) grey(price, i, why);
        }

        String[] converts = {"This month's rate", "Constant 2015", "PPP"};
        HBox convert = strip(converts, converts[settings.convert().ordinal()],
                i -> with(settings.board(), settings.money(), settings.prices(), Rankings.Convert.values()[i]));

        row.getChildren().addAll(control("BOARD", board), control("MONEY", money), control("PRICES", price),
                control("CONVERT AT", convert));
        return row;
    }

    /**
     * A strip of chips that hands the pick's index back and draws the page again. The chips are Pieces.chipStrip's, moved
     * into a row of their own: a FlowPane's preferred width is never less than its wrap length (560 for that strip), so
     * four of them would not sit side by side where there is room.
     */
    private HBox strip(String[] names, String current, Consumer<Integer> pick) {
        FlowPane chips = Pieces.chipStrip(names, current, Palette.SIZE_BODY, name -> {
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(name)) {
                    pick.accept(i);
                    break;
                }
            }
            showRankingsMenu();
        });
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(new ArrayList<>(chips.getChildren()));
        return row;
    }

    /** A chip greyed with its reason as its tooltip: it keeps its place and its mark if it is the one chosen, and a click does nothing. */
    private static void grey(HBox strip, int index, String why) {
        Button chip = (Button) strip.getChildren().get(index);
        chip.setOnAction(e -> { });
        chip.setStyle(chip.getStyle() + " -fx-text-fill: " + Palette.TEXT_SPENT + "; -fx-cursor: default;");
        HistoryScreen.tip(chip, why);
    }

    private static HBox control(String name, HBox strip) {
        Label label = words(name, Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        label.setMinWidth(Region.USE_PREF_SIZE);
        HBox box = new HBox(8, label, strip);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /** The sentence under the controls (spec-leaderboards 3: "the most important piece of the UI"), at body size in the head's ink. */
    private Node sentenceLine(Rankings.City now, WorldMeasures.Measure m, Nations nations) {
        Label says = new Label(Rankings.sentence(now, settings, m, nations == null ? null : nations.path(), onBoard, atAge));
        says.setWrapText(true);
        says.setMaxWidth(Double.MAX_VALUE);
        says.setMinHeight(Region.USE_PREF_SIZE);
        says.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_HEAD) + " -fx-padding: 2 0 4 0;");
        return says;
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE (i)

       Spec-leaderboards 7: the three kinds of figure in a sentence each, the
       bridge between the game's dollar and the real one, and the data build's
       date and counts. The marks' symbols and names are the file's own
       (WorldMeasures.marks()).
       ===================================================================== */

    private String info(WorldMeasures wm) {
        StringBuilder b = new StringBuilder("Every real figure carries a mark that says where it comes from.\n");
        b.append('\n').append(mark(wm, 3)).append(": read from a published source, in its latest year.");
        b.append('\n').append(mark(wm, 2)).append(": a figure for a later year that its source projects.");
        b.append('\n').append(mark(wm, 1)).append(": made by a rule of the game where nothing is published, such as a city region's share of its nation.");
        b.append("\n\nA founding game dollar counts as a 2015 dollar; the game's prices were set from 2025 figures.");
        b.append("\n\nThis build of the data is ").append(wm.name()).append(", made ").append(wm.made()).append(": ")
                .append(count(wm.countNations())).append(" nations, ").append(count(wm.countRegions())).append(" city regions and ")
                .append(count(wm.countFigures())).append(" figures, ").append(count(wm.countEstimates())).append(" of them the game's estimates.");
        return b.toString();
    }

    private static String count(int n) { return String.format(Locale.CANADA, "%,d", n); }

    /** A mark as the file writes it: its symbol and its name. */
    private static String mark(WorldMeasures wm, int mark) {
        WorldMeasures.Mark k = wm.marks().get(mark);
        return k == null ? "" : k.symbol() + " " + k.name();
    }

    /** A row's mark: the symbol alone, nothing for the city (mark 0). */
    private static String glyph(WorldMeasures wm, int mark) {
        WorldMeasures.Mark k = wm.marks().get(mark);
        return k == null ? "" : k.symbol();
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): FIGURES AS WORDS

       Every figure comes from Rankings or CityMeasures already in the unit it
       is shown in: Row.shown is in WHOLE units of the money picked (the model's
       thousands are converted inside Rankings), so a money figure goes to
       Money.tightMoney as it is and takes the money's own mark, "US$" for the
       two dollars and the city's qualified symbol for its own (Money.marked).
       A figure that is not money is written by its unit.
       ===================================================================== */

    private String symbol() {
        return settings.money() == Rankings.Money.CITY ? ui.game.getCurrency().qualifiedSymbol() : Currency.FOREIGN_SYMBOL;
    }

    /** A measure's figure as the screen writes it; "—" where there is none. */
    private String figure(WorldMeasures.Measure m, double v) {
        if (!Double.isFinite(v)) return "—";
        if (m.money()) return Money.marked(symbol(), Money.tightMoney(v, false)).replace('-', '−');
        return plain(m.unit(), v);
    }

    /** A figure that is not money, by its unit: a percent to a place, a ratio to two, anything else abbreviated from a million. */
    static String plain(String unit, double v) {
        double a = Math.abs(v);
        String text;
        if (unit.startsWith("%")) {
            text = String.format(Locale.CANADA, "%." + (a > 0 && a < 0.1 ? 2 : 1) + "f%%", Money.unsigned0(v, a > 0 && a < 0.1 ? 2 : 1));
        } else if (unit.equals("ratio")) {
            text = String.format(Locale.CANADA, "%.2f", Money.unsigned0(v, 2));
        } else if (a >= 1e12) {
            text = String.format(Locale.CANADA, "%.1fT", v / 1e12);
        } else if (a >= 1e9) {
            text = String.format(Locale.CANADA, "%.1fB", v / 1e9);
        } else if (a >= 1e6) {
            text = String.format(Locale.CANADA, "%.1fM", v / 1e6);
        } else if (a >= 1e3) {
            text = String.format(Locale.CANADA, "%,.0f", v);
        } else if (a >= 100) {
            text = String.format(Locale.CANADA, "%.0f", v);
        } else if (a >= 10) {
            text = String.format(Locale.CANADA, "%.1f", Money.unsigned0(v, 1));
        } else {
            text = String.format(Locale.CANADA, "%.2f", Money.unsigned0(v, 2));
        }
        return text.replace('-', '−');
    }

    /** A measure's unit as the screen names it: the money picked in place of "US$". */
    private String unitText(WorldMeasures.Measure m) {
        return m.money() ? m.unit().replace(Currency.FOREIGN_SYMBOL, symbol()) : m.unit();
    }

    private static String arrowWords(WorldMeasures.Measure m) {
        return switch (m.arrow()) {
            case UP -> "higher is better";
            case DOWN -> "lower is better";
            case NONE -> "neither is better; the largest ranks first";
        };
    }

    /** A figure with its unit after it, as a sentence wants it: "US$30.8T", "12.3% of GDP", "78.4 years". */
    private String withUnit(WorldMeasures.Measure m, double v) {
        if (m.money() || m.unit().equals("ratio")) return figure(m, v);
        if (m.unit().startsWith("%")) return figure(m, v) + m.unit().substring(1);
        return figure(m, v) + " " + m.unit();
    }

    /** A gap between two figures of a measure: money as money, a percent in points, any other figure with its unit. */
    private String gapText(WorldMeasures.Measure m, double gap) {
        if (m.money()) return figure(m, gap);
        if (m.unit().startsWith("%")) return plain("", gap) + " points";
        return plain(m.unit(), gap) + " " + m.unit();
    }

    private static String up(String s) {
        return s == null || s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static Label words(String text, int size, String colour) {
        Label l = new Label(text);
        l.setStyle(Palette.words(size, colour));
        return l;
    }

    private static Label wrapped(String text, int size, String colour) {
        Label l = words(text, size, colour);
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setMinHeight(Region.USE_PREF_SIZE);
        return l;
    }

    /** A column's block: the panel ground, padded. */
    private static VBox panel() {
        VBox box = new VBox(Palette.GAP);
        box.setStyle("-fx-padding: 10 12 12 12;" + Palette.block(Palette.PANEL));
        return box;
    }

    /** A small heading inside a column. */
    private static Label caption(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 6 0 0 0;");
        return l;
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE MEASURES COLUMN

       The measures the player pinned first, then the ten groups in the file's
       order, each measure a line that shows it (the one shown lit). The
       column is folded to the pinned and the group of the measure shown until
       "Browse all 70" opens every group. A pin toggles on each line; on Your
       world the measures that board does not rank (all but ten) are greyed.
       ===================================================================== */

    private VBox measuresColumn(WorldMeasures wm, WorldMeasures.Measure shown) {
        VBox box = panel();
        box.setSpacing(2);
        box.getChildren().add(Pieces.sectionHead("Measures", null));
        boolean yours = settings.board() == Rankings.Board.YOUR_WORLD;

        List<WorldMeasures.Measure> pins = new ArrayList<>();
        for (String key : pinned) {
            WorldMeasures.Measure p = wm.measure(key);
            if (p != null) pins.add(p);
        }
        if (!pins.isEmpty()) {
            box.getChildren().add(caption("PINNED"));
            for (WorldMeasures.Measure p : pins) box.getChildren().add(measureLine(p, shown, yours));
        }

        Map<String, List<WorldMeasures.Measure>> groups = new LinkedHashMap<>();
        for (WorldMeasures.Measure p : wm.measures()) groups.computeIfAbsent(p.group(), k -> new ArrayList<>()).add(p);
        for (Map.Entry<String, List<WorldMeasures.Measure>> g : groups.entrySet()) {
            if (!browseAll && !g.getKey().equals(shown.group())) continue;
            box.getChildren().add(caption(GROUPS.getOrDefault(g.getKey(), up(g.getKey())).toUpperCase(Locale.ROOT)));
            for (WorldMeasures.Measure p : g.getValue()) box.getChildren().add(measureLine(p, shown, yours));
        }

        Label browse = Pieces.stepChip(browseAll ? "Fold to the pinned" : "Browse all " + wm.measures().size(), () -> {
            browseAll = !browseAll;
            showRankingsMenu();
        }, true);
        VBox.setMargin(browse, new javafx.geometry.Insets(8, 0, 0, 0));
        box.getChildren().add(browse);
        return box;
    }

    private Node measureLine(WorldMeasures.Measure p, WorldMeasures.Measure shown, boolean yourWorldBoard) {
        boolean lit = p.key().equals(shown.key());
        boolean greyed = yourWorldBoard && !p.yourWorld();
        boolean pin = pinned.contains(p.key());

        Label name = words(up(p.name()), Palette.SIZE_BODY,
                lit ? Palette.TEXT_HEAD : greyed ? Palette.TEXT_SPENT : Palette.TEXT_BODY);
        name.setMinWidth(0);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);

        StackPane toggle = new StackPane(Pieces.icon(Icons.PIN, pin ? Palette.ACCENT : Palette.TEXT_SPENT, 11));
        toggle.setPadding(new javafx.geometry.Insets(0, 2, 0, 4));
        toggle.setStyle("-fx-cursor: hand;");
        HistoryScreen.tip(toggle, pin ? "Unpin" : "Pin it to the top of the list and to your card");
        toggle.setOnMouseClicked(e -> {
            e.consume();
            if (!pinned.remove(p.key())) pinned.add(p.key());
            showRankingsMenu();
        });

        HBox line = new HBox(4, name, toggle);
        line.setAlignment(Pos.CENTER_LEFT);
        String base = "-fx-padding: 3 4 3 8; -fx-cursor: hand; -fx-background-radius: " + Palette.RADIUS + ";";
        String ground = lit
                ? base + " -fx-background-color: " + Palette.RAISED + "; -fx-border-color: transparent transparent transparent " + Palette.ACCENT + "; -fx-border-width: 0 0 0 2;"
                : base;
        line.setStyle(ground);
        if (!lit) {
            line.setOnMouseEntered(e -> line.setStyle(base + " -fx-background-color: " + Palette.HOVER + ";"));
            line.setOnMouseExited(e -> line.setStyle(ground));
        }
        if (greyed) HistoryScreen.tip(line, "Ranked on Real world 2025 only");
        line.setOnMouseClicked(e -> {
            measure = p.key();
            showRankingsMenu();
        });
        return line;
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE TABLE

       The filter chips with their counts (All, Nations, Cities, Your size) and
       Full list | Around you; a header line; a row per place - its rank (a tie
       shows its shared rank on each row), a glyph for what it is, its name, what
       it is of (a region's nation; "your city"), the year the figure is of (the
       city's month), the figure, a bar and the mark of where the figure comes
       from. A row is a click that opens that place's drawer. Under it the legend
       of the marks and the count of the game's estimates.

       THE RANK IS THE BOARD'S, NOT THE LIST'S. A filter keeps the rows of a
       kind and the city's, and Around you cuts the list round the city, but each
       row keeps the rank the whole board gave it: Nations does not renumber, so
       "rank 41" means the same in every list and on the card.

       THE BAR is the row's shown figure over the largest in the list; where
       a figure can be negative the bar runs from a zero line in the middle, both
       ways. A measure with no real figure (D10) is the city's row alone and a
       line that says so; a measure Your world does not rank is a line saying
       which board has it.
       ===================================================================== */

    static final double RANK_W = 40, KIND_W = 16, OF_W = 84, ASOF_W = 54, VALUE_W = 80, BAR_W = 52, MARK_W = 16, BAR_H = 6;

    private VBox tableColumn(WorldMeasures wm, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m) {
        VBox box = panel();
        String note = m.note();
        box.getChildren().add(Pieces.sectionHead(up(m.name()), note == null || note.isBlank() ? null : note, null));
        box.getChildren().add(words(unitText(m) + " · " + arrowWords(m), Palette.SIZE_LABEL, Palette.TEXT_MUTED));

        if (!t.ranked()) {
            box.getChildren().add(wrapped("Your world ranks ten measures, the ones its nations carry; this one is on Real world 2025.",
                    Palette.SIZE_BODY, Palette.TEXT_MUTED));
            return box;
        }

        if (t.realFigures()) {
            List<Rankings.Row> kept = filtered(t);
            box.getChildren().add(filters(t, now));
            List<Rankings.Row> shown = aroundYou ? Rankings.around(listOf(t, kept)) : kept;
            if (shown.isEmpty()) {
                box.getChildren().add(wrapped(filter == Filter.YOUR_CITIES ? "None of your cities has a figure for this one."
                        : "Nothing near your size to show.", Palette.SIZE_BODY, Palette.TEXT_MUTED));
            } else {
                rows(box, wm, now, m, kept, shown);
            }
        } else {
            // No filters here (the real world has no rows for this one), but the saved cities are the point of it: Now / Your age stays.
            if (savedReadable) {
                FlowPane age = new FlowPane(22, 6, ageControl(now));
                age.setAlignment(Pos.CENTER_LEFT);
                box.getChildren().add(age);
            }
            rows(box, wm, now, m, t.rows(), t.rows());
            box.getChildren().add(wrapped("The real world publishes no comparable figure for this one, so only your cities rank on it.",
                    Palette.SIZE_BODY, Palette.TEXT_MUTED));
        }
        savedNotes(box);
        box.getChildren().add(legend(wm, t));
        return box;
    }

    /** The rows a filter keeps: a kind's, the city's own among them (the saved cities are in Your cities and not in Nations or Cities: they are not the real world's places). */
    private List<Rankings.Row> filtered(Rankings.Table t) {
        if (filter == Filter.YOUR_SIZE) return Rankings.yourSize(t);
        if (filter == Filter.ALL) return t.rows();
        List<Rankings.Row> out = new ArrayList<>();
        for (Rankings.Row r : t.rows()) {
            if (filter == Filter.YOUR_CITIES) {
                if (r.kind() == Rankings.Kind.CITY || r.kind() == Rankings.Kind.SAVED) out.add(r);
                continue;
            }
            if (r.kind() == Rankings.Kind.SAVED) continue;
            boolean nation = r.kind() == Rankings.Kind.NATION;
            if (r.kind() == Rankings.Kind.CITY || (filter == Filter.NATIONS) == nation) out.add(r);
        }
        return out;
    }

    /** The quiet lines under the rows about the saved cities: the ones that stopped short of the active city's age, and the saves the board left out. */
    private void savedNotes(VBox box) {
        for (String line : new String[] {notAtAge, leftOut}) {
            if (line == null) continue;
            Label note = wrapped(line, Palette.SIZE_LABEL, Palette.TEXT_MUTED);
            note.setStyle(note.getStyle() + " -fx-padding: 6 8 0 8;");
            box.getChildren().add(note);
        }
    }

    /** The board's table cut to a list of its rows, for Rankings.around(): the city's place in it found again. */
    private static Rankings.Table listOf(Rankings.Table t, List<Rankings.Row> rows) {
        int you = -1;
        for (int i = 0; i < rows.size(); i++) if (rows.get(i).kind() == Rankings.Kind.CITY) you = i;
        return new Rankings.Table(t.measure(), t.settings(), rows, you, t.ranked(), t.realFigures(), t.estimates(), t.figures());
    }

    /** The filter chips with the number of places each keeps, and Full list | Around you beside them. */
    private Node filters(Rankings.Table t, Rankings.City now) {
        int nations = 0, cities = 0, yours = 0;
        for (Rankings.Row r : t.rows()) {
            switch (r.kind()) {
                case NATION -> nations++;
                case REGION -> cities++;
                case CITY -> { cities++; yours++; }
                case SAVED -> yours++;
            }
        }
        int[] counts = {t.rows().size(), nations, cities, Rankings.yourSize(t).size(), yours};
        Filter[] all = Filter.values();
        String[] names = new String[all.length];
        for (int i = 0; i < all.length; i++) names[i] = all[i].label + " " + counts[i];
        // The first read of the saved cities is still on WORKER: no count yet, and no saved row on the board.
        if (saved == null) names[Filter.YOUR_CITIES.ordinal()] = Filter.YOUR_CITIES.label + " (reading…)";
        HBox kinds = strip(names, names[filter.ordinal()], i -> filter = all[i]);

        String[] views = {"Full list", "Around you"};
        HBox view = strip(views, views[aroundYou ? 1 : 0], i -> aroundYou = i == 1);

        FlowPane both = new FlowPane(22, 6);
        both.getChildren().add(kinds);
        if (savedReadable) both.getChildren().add(ageControl(now));
        both.getChildren().add(view);
        both.setAlignment(Pos.CENTER_LEFT);
        return both;
    }

    /** Your cities at: Now | Your age (D3), beside the Your cities chip; shown only when a saved city is readable. Your age is the active city's, in years and months. */
    private Node ageControl(Rankings.City now) {
        String[] names = {"Now", "Your age"};
        HBox chips = strip(names, names[atAge ? 1 : 0], i -> atAge = i == 1);
        HistoryScreen.tip(chips.getChildren().get(0), "Each saved city as you last saved it.");
        HistoryScreen.tip(chips.getChildren().get(1), "Each saved city when it was as old as " + now.name() + " is now, " + Rankings.ageWords(now.month())
                + ". A city that never got that far is no row.");
        return control("YOUR CITIES AT", chips);
    }

    private void rows(VBox box, WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m,
                      List<Rankings.Row> list, List<Rankings.Row> shown) {
        double largest = 0;
        boolean signed = false;
        for (Rankings.Row r : shown) {
            if (!Double.isFinite(r.shown())) continue;
            largest = Math.max(largest, Math.abs(r.shown()));
            if (r.shown() < 0) signed = true;
        }

        box.getChildren().add(header());
        if (m.arrow() == WorldMeasures.Arrow.DOWN) {
            Label lowest = words("lowest ranks first", Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
            lowest.setStyle(lowest.getStyle() + " -fx-padding: 0 0 2 8;");
            box.getChildren().add(lowest);
        }
        List<Entry> entries = new ArrayList<>();
        int before = -2;
        for (Rankings.Row r : shown) {
            int at = list.indexOf(r);
            if (before >= 0 && at > before + 1) entries.add(Entry.GAP);
            before = at;
            entries.add(entry(wm, now, m, r, largest));
        }
        box.getChildren().add(new RowsPicture(entries, signed, id -> {
            drawer = id.equals(drawer) ? null : id;
            showRankingsMenu();
        }, drawer));
    }

    /** One row of the table as the picture draws it: the figures already written. */
    private Entry entry(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, Rankings.Row r, double largest) {
        boolean you = r.kind() == Rankings.Kind.CITY;
        // A saved city (LB3): "slot 3" where a region shows its nation, and the month its figure is of (the city's own, as the active city's row has).
        Rankings.Saved sv = r.kind() == Rankings.Kind.SAVED ? onBoardById.get(r.id()) : null;
        String of = you ? "your city" : sv != null ? "slot " + sv.slot()
                : r.nation() != null && wm.place(r.nation()) != null ? wm.place(r.nation()).name() : "";
        String asOf = you ? CityCalendar.formatShort(now.month()) : sv != null ? CityCalendar.formatShort(sv.city().month())
                : r.year() > 0 ? String.valueOf(r.year()) : "—";
        double bar = Double.isFinite(r.shown()) && largest > 0 ? r.shown() / largest : Double.NaN;
        return new Entry(r.id(), String.valueOf(r.rank()), kindIcon(r.kind()), you, r.name(), of, asOf, figure(m, r.shown()), bar, glyph(wm, r.mark()));
    }

    private static String kindIcon(Rankings.Kind kind) {
        return switch (kind) {
            case NATION -> Icons.TRADE;
            case REGION -> Icons.BUILD;
            case CITY -> Icons.HOMES;
            case SAVED -> Icons.SAVE;
        };
    }

    private Node header() {
        HBox head = new HBox(6);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setStyle("-fx-padding: 4 8 4 8; -fx-border-color: transparent transparent " + Palette.EDGE + " transparent; -fx-border-width: 0 0 1 0;");
        Label name = words("Place", Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        name.setMinWidth(0);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        head.getChildren().addAll(
                fixed("Rank", RANK_W, Pos.CENTER_RIGHT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("", KIND_W, Pos.CENTER, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                name,
                fixed("Of", OF_W, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("As of", ASOF_W, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("Value", VALUE_W, Pos.CENTER_RIGHT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("", BAR_W, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("", MARK_W, Pos.CENTER, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)));
        return head;
    }

    /** A label of one width, its text clipped to it. */
    private static Label fixed(String text, double w, Pos at, String style) {
        Label l = new Label(text);
        l.setMinWidth(w);
        l.setPrefWidth(w);
        l.setMaxWidth(w);
        l.setAlignment(at);
        l.setStyle(style);
        return l;
    }

    /** The legend under the table: the marks, each a few words, and how many of the figures are the game's own estimates. */
    private Node legend(WorldMeasures wm, Rankings.Table t) {
        FlowPane key = new FlowPane(16, 4);
        key.setAlignment(Pos.CENTER_LEFT);
        key.setStyle("-fx-padding: 8 8 0 8;");
        for (int n = 3; n >= 1; n--) key.getChildren().add(words(mark(wm, n), Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        if (t.figures() > 0) {
            key.getChildren().add(words(t.estimates() + " of " + t.figures() + " figures here are the game's estimates",
                    Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        }
        return key;
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE ROWS, DRAWN

       The rows of a full list are not nodes. A row of labels is some twenty
       nodes (a Label is two, an icon four), a full list is 259 rows, a click
       redraws the page and so does a month, and UiProbe measured that page's
       redraw at 142 ms, 119 of them the CSS pass, which costs by the node
       and not by the style. The rows are painted on a few canvases instead
       (TILE_ROWS to a canvas, so none is taller than a texture may be): the
       table costs the page no CSS at all.

       Hover is one region under the canvases (they are clear where no row is
       raised), a click finds its row by its height (every row, and the "· · ·"
       Around you leaves between its stretches, is ROW_H tall), and the
       columns are laid from the right edge so the header, which is nodes, and
       the rows, which are paint, line up at any width: the picture draws again
       when its width moves.
       ===================================================================== */

    static final double ROW_H = 24, PAD = 8, SPACE = 6;

    /** Rows to a canvas: 40 of 24 pixels is under a thousand, well inside any texture. */
    static final int TILE_ROWS = 40;

    /** A row of the table as the picture draws it, its figures already written; GAP stands for the rows a list skips. */
    private record Entry(String id, String rank, String kindIcon, boolean you, String name, String of, String asOf,
                          String value, double bar /* the figure over the largest in the list, -1 to 1; NaN for none */, String mark) {
        static final Entry GAP = new Entry(null, "", "", false, "", "", "", "", Double.NaN, "");

        boolean gap() { return id == null; }
    }

    private static final class RowsPicture extends Pane {

        private static final Color TEXT = Color.web(Palette.TEXT), HEAD = Color.web(Palette.TEXT_HEAD), BODY = Color.web(Palette.TEXT_BODY),
                LABEL = Color.web(Palette.TEXT_LABEL), MUTED = Color.web(Palette.TEXT_MUTED), RAISED = Color.web(Palette.RAISED),
                PINNED = Color.web(Palette.PINNED), ACCENT = Color.web(Palette.ACCENT), MONEY = Color.web(Palette.MONEY),
                BAR = Color.web(Palette.MONEY, .5);

        /** Measures a string against a column; one node, on the FX thread, where the drawing is. */
        private static final javafx.scene.text.Text RULER = new javafx.scene.text.Text();

        private final List<Entry> entries;
        private final boolean signed;
        private final String open;
        private final List<Canvas> tiles = new ArrayList<>();
        private final Region hover = new Region();
        private final javafx.scene.text.Font words = Palette.Fonts.sansFont(Palette.SIZE_BODY),
                strong = Palette.Fonts.sansStrongFont(Palette.SIZE_BODY), small = Palette.Fonts.sansFont(Palette.SIZE_LABEL),
                figures = Palette.Fonts.monoFont(Palette.SIZE_BODY);

        /** The columns' left edges and the name's room, laid from the right at the width drawn. */
        private double nameX, nameW, ofX, asOfX, valueX, barX, markX;

        RowsPicture(List<Entry> entries, boolean signed, Consumer<String> pick, String open) {
            this.entries = entries;
            this.signed = signed;
            this.open = open;
            double total = entries.size() * ROW_H;
            setMinHeight(total);
            setPrefHeight(total);
            setMaxHeight(total);
            setPrefWidth(10);
            setCursor(Cursor.HAND);
            hover.setStyle("-fx-background-color: " + Palette.HOVER + ";");
            hover.setMouseTransparent(true);
            hover.setManaged(false);
            hover.setVisible(false);
            getChildren().add(hover);
            for (int from = 0; from < entries.size(); from += TILE_ROWS) {
                Canvas tile = new Canvas(0, Math.min(TILE_ROWS, entries.size() - from) * ROW_H);
                tile.setLayoutY(from * ROW_H);
                tile.setManaged(false);
                tiles.add(tile);
                getChildren().add(tile);
            }
            widthProperty().addListener((o, was, now) -> draw(now.doubleValue()));
            setOnMouseMoved(e -> lightRowAt(e.getY()));
            setOnMouseExited(e -> hover.setVisible(false));
            setOnMouseClicked(e -> {
                Entry at = rowAt(e.getY());
                if (at != null && !at.gap()) pick.accept(at.id());
            });
        }

        private Entry rowAt(double y) {
            int i = (int) Math.floor(y / ROW_H);
            return i >= 0 && i < entries.size() ? entries.get(i) : null;
        }

        /** The row under the pointer lit, unless it is the city's or the open one, which are raised already. */
        private void lightRowAt(double y) {
            Entry at = rowAt(y);
            boolean lit = at != null && !at.gap() && !at.you() && !at.id().equals(open);
            hover.setVisible(lit);
            if (lit) hover.resizeRelocate(0, Math.floor(y / ROW_H) * ROW_H, getWidth(), ROW_H);
        }

        private void draw(double w) {
            if (!(w > 0)) return;
            markX = w - PAD - MARK_W;
            barX = markX - SPACE - BAR_W;
            valueX = barX - SPACE - VALUE_W;
            asOfX = valueX - SPACE - ASOF_W;
            ofX = asOfX - SPACE - OF_W;
            nameX = PAD + RANK_W + SPACE + KIND_W + SPACE;
            nameW = Math.max(40, ofX - SPACE - nameX);
            for (int t = 0; t < tiles.size(); t++) {
                Canvas tile = tiles.get(t);
                tile.setWidth(w);
                GraphicsContext gc = tile.getGraphicsContext2D();
                gc.clearRect(0, 0, w, tile.getHeight());
                gc.setTextBaseline(VPos.CENTER);
                int from = t * TILE_ROWS;
                for (int i = from; i < Math.min(entries.size(), from + TILE_ROWS); i++) paint(gc, entries.get(i), (i - from) * ROW_H, w);
            }
        }

        private void paint(GraphicsContext gc, Entry e, double y, double w) {
            double mid = y + ROW_H / 2;
            if (e.gap()) {
                gc.setFont(words);
                gc.setFill(MUTED);
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText("· · ·", PAD + RANK_W, mid);
                return;
            }
            if (e.you()) {
                gc.setFill(RAISED);
                gc.fillRect(0, y, w, ROW_H);
                gc.setFill(ACCENT);
                gc.fillRect(0, y, 3, ROW_H);
            } else if (e.id().equals(open)) {
                gc.setFill(PINNED);
                gc.fillRect(0, y, w, ROW_H);
            }

            gc.setFont(figures);
            gc.setFill(e.you() ? HEAD : LABEL);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(e.rank(), PAD + RANK_W, mid, RANK_W);

            // The kind: a Lucide icon on its 24-unit grid at half size, stroked in a pixel as the rail's are in two.
            gc.save();
            gc.translate(PAD + RANK_W + SPACE + (KIND_W - 12) / 2, mid - 6);
            gc.scale(0.5, 0.5);
            gc.setStroke(e.you() ? ACCENT : MUTED);
            gc.setLineWidth(2);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.beginPath();
            gc.appendSVGPath(e.kindIcon());
            gc.stroke();
            gc.restore();

            javafx.scene.text.Font nameFont = e.you() ? strong : words;
            gc.setFont(nameFont);
            gc.setFill(e.you() ? HEAD : BODY);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(fit(e.name(), nameFont, nameW), nameX, mid);

            gc.setFont(small);
            gc.setFill(MUTED);
            gc.fillText(fit(e.of(), small, OF_W), ofX, mid);

            gc.setFont(figures);
            gc.fillText(e.asOf(), asOfX, mid, ASOF_W);

            gc.setFill(e.you() ? HEAD : TEXT);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(e.value(), valueX + VALUE_W, mid, VALUE_W);

            // The bar: as wide as the figure is of the largest in the list; from a line in the middle both ways where a figure can be below zero.
            if (Double.isFinite(e.bar()) && e.bar() != 0) {
                double room = signed ? BAR_W / 2 : BAR_W;
                double bar = Math.max(1, room * Math.abs(e.bar()));
                gc.setFill(e.you() ? MONEY : BAR);
                gc.fillRoundRect(!signed ? barX : e.bar() >= 0 ? barX + room : barX + room - bar, mid - BAR_H / 2, bar, BAR_H, 3, 3);
            }

            gc.setFont(words);
            gc.setFill(MUTED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(e.mark(), markX + MARK_W / 2, mid);
        }

        /** A string cut to a column's room with an ellipsis, measured in the font it is drawn in. */
        private static String fit(String text, javafx.scene.text.Font font, double room) {
            if (text.isEmpty() || widthOf(text, font) <= room) return text;
            int lo = 0, hi = text.length();
            while (lo < hi) {
                int mid = (lo + hi + 1) / 2;
                if (widthOf(text.substring(0, mid) + "…", font) <= room) lo = mid;
                else hi = mid - 1;
            }
            return text.substring(0, lo) + "…";
        }

        private static double widthOf(String text, javafx.scene.text.Font font) {
            RULER.setFont(font);
            RULER.setText(text);
            return RULER.getLayoutBounds().getWidth();
        }
    }

    /* =====================================================================
       THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER

       The city's card: its rank of the places on the board, the place above
       and the one below it with their figures, the gap to pass the next one up
       (in the money or the unit shown), and its ranks on the pinned measures,
       each a click that shows that measure. Above it, when a place's drawer is
       open: every measure for that place - its Real world 2025 figure with its
       mark and its source, and its Your world figure with its mark where that
       board ranks the measure, "—" where there is none (Rankings.drawer). A
       saved city's row opens its own drawer (LB3: "saved:" + its slot), and
       under the pinned ranks the card draws the city's rank over the years.
       ===================================================================== */

    /** The drawer's columns: the measure, the real figure and its mark and source, Your world's figure and its mark. */
    static final double D_NAME = 128, D_REAL = 66, D_MARK = 12, D_SOURCE = 66, D_YOURS = 60;

    private VBox cardColumn(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m) {
        VBox col = new VBox(Palette.GAP);
        if (drawer != null) {
            Node open = drawerCard(wm, nations, now, t);
            if (open != null) col.getChildren().add(open);
        }
        col.getChildren().add(rankCard(wm, nations, now, t, m));
        return col;
    }

    private static VBox raised() {
        VBox card = new VBox(6);
        card.setStyle("-fx-padding: 12;" + Palette.block(Palette.RAISED, Palette.EDGE));
        return card;
    }

    private Node rankCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m) {
        VBox card = raised();
        Label title = wrapped(now.name() + " on " + m.name(), Palette.SIZE_HEADING, Palette.TEXT_HEAD);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        card.getChildren().add(title);

        if (t.you() < 0) {
            card.getChildren().add(wrapped(!t.ranked() ? "Your world does not rank this measure."
                    : now.name() + " has no figure for this one yet.", Palette.SIZE_BODY, Palette.TEXT_MUTED));
        } else if (!t.realFigures()) {
            card.getChildren().add(wrapped("Only your cities rank on this one.", Palette.SIZE_BODY, Palette.TEXT_MUTED));
        } else {
            Rankings.Row me = t.rows().get(t.you());
            Label place = new Label("Rank " + me.rank() + " of " + t.rows().size());
            place.setStyle(Palette.strong(Palette.SIZE_LEAD, Palette.TEXT_HEAD));
            card.getChildren().addAll(place, words(withUnit(m, me.shown()), Palette.SIZE_BODY, Palette.TEXT_LABEL));

            Rankings.Row above = null, below = null;
            for (int i = t.you() - 1; i >= 0; i--) {
                if (t.rows().get(i).rank() < me.rank()) {
                    above = t.rows().get(i);
                    break;
                }
            }
            for (int i = t.you() + 1; i < t.rows().size(); i++) {
                if (t.rows().get(i).rank() > me.rank()) {
                    below = t.rows().get(i);
                    break;
                }
            }
            card.getChildren().add(neighbour("Above you", above, m, "Nobody: you lead the board."));
            card.getChildren().add(neighbour("Below you", below, m, "Nobody: you are last."));
            if (above != null) {
                boolean down = m.arrow() == WorldMeasures.Arrow.DOWN;
                card.getChildren().add(wrapped("To pass " + above.name() + ", " + m.name() + " has to " + (down ? "fall" : "rise")
                        + " by " + gapText(m, Math.abs(above.shown() - me.shown())) + ".", Palette.SIZE_BODY, Palette.TEXT_BODY));
            }
        }

        card.getChildren().add(caption("YOUR RANKS"));
        for (String key : pinned) {
            WorldMeasures.Measure p = wm.measure(key);
            if (p != null) card.getChildren().add(pinnedRank(wm, nations, now, t, p));
        }
        card.getChildren().add(rankOverYears(wm, now, m));
        return card;
    }

    /** The place above or below the city: what it is, its name and its figure; a click opens its drawer. */
    private Node neighbour(String which, Rankings.Row r, WorldMeasures.Measure m, String none) {
        Label label = fixed(which, 62, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox line;
        if (r == null) {
            Label nobody = words(none, Palette.SIZE_LABEL, Palette.TEXT_MUTED);
            nobody.setMinWidth(0);
            line = new HBox(6, label, nobody);
        } else {
            Label name = words(r.name(), Palette.SIZE_BODY, Palette.TEXT_BODY);
            name.setMinWidth(0);
            name.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(name, Priority.ALWAYS);
            Label value = words(figure(m, r.shown()), Palette.SIZE_BODY, Palette.TEXT);
            value.setStyle(Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT));
            value.setMinWidth(Region.USE_PREF_SIZE);
            line = new HBox(6, label, name, value);
            line.setStyle("-fx-cursor: hand;");
            line.setOnMouseClicked(e -> {
                drawer = r.id();
                showRankingsMenu();
            });
        }
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }

    /** The city's rank on a pinned measure, "5 of 259", on the board and the money shown; a click shows that measure. */
    private Node pinnedRank(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table shownTable, WorldMeasures.Measure p) {
        Rankings.Table pt = p.key().equals(shownTable.measure().key()) ? shownTable : Rankings.table(wm, nations, now, onBoard, settings, p.key());
        String text = "—";
        String why = null;
        if (!pt.ranked()) why = "Ranked on Real world 2025 only";
        else if (!pt.realFigures()) why = "No real place publishes this one";
        else if (pt.you() < 0) why = now.name() + " has no figure for this one yet";
        else text = pt.rows().get(pt.you()).rank() + " of " + pt.rows().size();

        boolean lit = p.key().equals(measure);
        Label name = words(up(p.name()), Palette.SIZE_BODY, lit ? Palette.TEXT_HEAD : Palette.TEXT_BODY);
        name.setMinWidth(0);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        Label rank = words(text, Palette.SIZE_BODY, why == null ? Palette.TEXT : Palette.TEXT_MUTED);
        rank.setStyle(Palette.figureRegular(Palette.SIZE_BODY, why == null ? Palette.TEXT : Palette.TEXT_MUTED));
        rank.setMinWidth(Region.USE_PREF_SIZE);
        HBox line = new HBox(6, name, rank);
        line.setAlignment(Pos.CENTER_LEFT);
        line.setStyle("-fx-cursor: hand;");
        if (why != null) HistoryScreen.tip(line, why);
        line.setOnMouseClicked(e -> {
            measure = p.key();
            showRankingsMenu();
        });
        return line;
    }

    /* ----- rank over the years (LB3, D12) ----- */

    /**
     * The city's rank on the measure shown at each December of its history, against Real world 2025 (which holds still, so the line moves only because the
     * city did): one point a December, rank 1 at the top, the years along the bottom, the first and last rank labelled. Fewer than two points, or a measure
     * no real place publishes, is a line of words. The points are Rankings.rankOverYears' and are worked out once for a measure and a conversion, on entering
     * the tab and when either changes, never at the clock's monthly redraw (a December that comes while the tab is open has its point at the next entry).
     */
    private Node rankOverYears(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m) {
        VBox box = new VBox(4);
        box.getChildren().add(caption("RANK OVER THE YEARS"));
        if (!m.sourced()) {
            box.getChildren().add(wrapped("No real place publishes this one, so there is nothing to rank it against.", Palette.SIZE_BODY, Palette.TEXT_MUTED));
            return box;
        }
        String key = m.key() + "|" + settings.convert();
        if (!key.equals(overKey)) {
            overYears = Rankings.rankOverYears(wm, now, ui.game.getHistorySave(), settings, m.key());
            overKey = key;
        }
        if (overYears.size() < 2) {
            box.getChildren().add(wrapped(overYears.isEmpty() ? "One point each December; none yet." : "One point each December; one so far.",
                    Palette.SIZE_BODY, Palette.TEXT_MUTED));
            return box;
        }
        RankLine line = new RankLine(overYears);
        Rankings.RankPoint first = overYears.get(0), last = overYears.get(overYears.size() - 1);
        HistoryScreen.tip(line, "Rank " + first.rank() + " of " + first.of() + " in " + CityCalendar.yearOf(first.month())
                + ", rank " + last.rank() + " of " + last.of() + " in " + CityCalendar.yearOf(last.month()) + ". One point each December.");
        box.getChildren().addAll(line, words("Against Real world 2025, which holds still.", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return box;
    }

    /** The rank over the years drawn on one canvas: no nodes to the point, and it draws again when its width moves. */
    private static final class RankLine extends Pane {

        static final double H = 76, SIDE = 36, TOP = 12, BOTTOM = 18, YEAR_GAP = 40;

        private static final Color LINE = Color.web(Palette.MONEY), FLOOR = Color.web(Palette.EDGE), RANK = Color.web(Palette.TEXT_HEAD),
                YEAR = Color.web(Palette.TEXT_MUTED);

        private final List<Rankings.RankPoint> points;
        private final Canvas canvas = new Canvas(0, H);
        private final javafx.scene.text.Font ranks = Palette.Fonts.monoFont(Palette.SIZE_LABEL), years = Palette.Fonts.sansFont(Palette.SIZE_CAPTION);

        RankLine(List<Rankings.RankPoint> points) {
            this.points = points;
            setMinHeight(H);
            setPrefHeight(H);
            setMaxHeight(H);
            setPrefWidth(10);
            canvas.setManaged(false);
            getChildren().add(canvas);
            widthProperty().addListener((o, was, now) -> draw(now.doubleValue()));
        }

        private void draw(double w) {
            if (!(w > 0)) return;
            canvas.setWidth(w);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, w, H);
            int n = points.size();
            int firstMonth = points.get(0).month(), lastMonth = points.get(n - 1).month();
            int best = Integer.MAX_VALUE, worst = 0;
            for (Rankings.RankPoint p : points) {
                best = Math.min(best, p.rank());
                worst = Math.max(worst, p.rank());
            }
            double left = SIDE, right = Math.max(left + 1, w - SIDE), top = TOP, bottom = H - BOTTOM;
            double[] xs = new double[n], ys = new double[n];
            for (int i = 0; i < n; i++) {
                Rankings.RankPoint p = points.get(i);
                xs[i] = left + (right - left) * (p.month() - firstMonth) / (double) Math.max(1, lastMonth - firstMonth);
                // Rank 1 at the top: the best rank the city had is the top of the plot, the worst its foot; a flat line is drawn in the middle.
                ys[i] = worst == best ? (top + bottom) / 2 : top + (bottom - top) * (p.rank() - best) / (double) (worst - best);
            }

            gc.setStroke(FLOOR);
            gc.setLineWidth(1);
            gc.strokeLine(left, bottom + 4, right, bottom + 4);

            gc.setStroke(LINE);
            gc.setLineWidth(1.5);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.beginPath();
            gc.moveTo(xs[0], ys[0]);
            for (int i = 1; i < n; i++) gc.lineTo(xs[i], ys[i]);
            gc.stroke();
            gc.setFill(LINE);
            for (int i = 0; i < n; i++) {
                double r = i == n - 1 ? 3.5 : 2.5;
                gc.fillOval(xs[i] - r, ys[i] - r, 2 * r, 2 * r);
            }

            gc.setFont(ranks);
            gc.setFill(RANK);
            // The first and the last rank, beside their dots (outside the line, which runs between them).
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText("#" + points.get(0).rank(), xs[0] - 6, ys[0]);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("#" + points.get(n - 1).rank(), xs[n - 1] + 6, ys[n - 1]);

            // The years along the bottom: every few points so that two labels are never closer than YEAR_GAP, and always the last.
            gc.setFont(years);
            gc.setFill(YEAR);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.TOP);
            int stride = Math.max(1, (int) Math.ceil(YEAR_GAP / Math.max(1, (right - left) / (n - 1))));
            for (int i = 0; i < n - 1; i += stride) {
                if (xs[n - 1] - xs[i] < YEAR_GAP) continue;
                gc.fillText(String.valueOf(CityCalendar.yearOf(points.get(i).month())), xs[i], bottom + 7);
            }
            gc.fillText(String.valueOf(CityCalendar.yearOf(points.get(n - 1).month())), xs[n - 1], bottom + 7);
        }
    }

    private Node drawerCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t) {
        boolean mine = Rankings.PLACE_CITY.equals(drawer);
        // A saved city's row opens its own figures (LB3); its id names a city that is not on the board now (the snapshot is still being read, or Your age leaves it out): no card.
        Rankings.Saved opened = onBoardById.get(drawer);
        if (opened == null && drawer.startsWith(Rankings.SAVED_PREFIX)) return null;
        Rankings.City owner = mine ? now : opened != null ? opened.city() : null;
        WorldMeasures.Place place = mine || opened != null ? null : wm.place(drawer);
        VBox card = raised();

        Label name = new Label(placeName(wm, now, t));
        name.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        name.setMinWidth(0);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        Label close = words("×", Palette.SIZE_SECTION, Palette.TEXT_MUTED);
        close.setStyle(close.getStyle() + " -fx-cursor: hand;");
        close.setMinWidth(Region.USE_PREF_SIZE);
        HistoryScreen.tip(close, "Close");
        close.setOnMouseClicked(e -> {
            drawer = null;
            showRankingsMenu();
        });
        HBox head = new HBox(8, name, close);
        head.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(head);

        String kind = mine ? "Your city"
                : opened != null ? "Saved city · slot " + opened.slot() + " · " + (atAge ? "at " + Rankings.ageWords(now.month()) + ", " : "")
                        + "as of " + CityCalendar.formatShort(opened.city().month())
                : place == null ? "Place"
                : place.kind() == WorldMeasures.Kind.NATION ? "Nation"
                : "City region" + (place.nation() != null && wm.place(place.nation()) != null ? " of " + wm.place(place.nation()).name() : "");
        card.getChildren().add(words(kind, Palette.SIZE_LABEL, Palette.TEXT_MUTED));

        if (owner == null && place == null) {
            card.getChildren().add(wrapped("The real world's file has no figures for this place.", Palette.SIZE_BODY, Palette.TEXT_MUTED));
            return card;
        }

        HBox columnsHead = new HBox(4,
                fixed("", D_NAME, Pos.CENTER_LEFT, ""),
                fixed("Real world 2025", D_REAL + D_MARK + 4 + D_SOURCE, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)),
                fixed("Your world", D_YOURS + D_MARK + 4, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)));
        card.getChildren().add(columnsHead);

        String group = null;
        for (Rankings.Line line : Rankings.drawer(wm, nations, now, onBoard, settings, drawer)) {
            if (!line.measure().group().equals(group)) {
                group = line.measure().group();
                card.getChildren().add(caption(GROUPS.getOrDefault(group, up(group)).toUpperCase(Locale.ROOT)));
            }
            card.getChildren().add(drawerLine(wm, owner, line));
        }
        return card;
    }

    /** A place's name: the city's, a saved city's, the file's, or the row's on the board (a nation of the game's world the file does not know). */
    private String placeName(WorldMeasures wm, Rankings.City now, Rankings.Table t) {
        if (Rankings.PLACE_CITY.equals(drawer)) return now.name();
        Rankings.Saved sv = onBoardById.get(drawer);
        if (sv != null) return sv.city().name();
        WorldMeasures.Place place = wm.place(drawer);
        if (place != null) return place.name();
        for (Rankings.Row r : t.rows()) if (r.id().equals(drawer)) return r.name();
        return drawer;
    }

    /** One measure in the drawer; owner is the city whose figure it is (the active city or a saved one: its own words for how it was worked out), null for a place of the file. */
    private Node drawerLine(WorldMeasures wm, Rankings.City owner, Rankings.Line line) {
        boolean mine = owner != null;
        WorldMeasures.Measure p = line.measure();
        Label name = fixed(up(p.name()), D_NAME, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_LABEL, p.key().equals(measure) ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
        String figureStyle = Palette.figureRegular(Palette.SIZE_LABEL, Palette.TEXT);
        String quiet = Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED);

        Label real = fixed(figure(p, line.real2025()), D_REAL, Pos.CENTER_RIGHT, Double.isFinite(line.real2025()) ? figureStyle : quiet);
        Label realMark = fixed(glyph(wm, line.realMark()), D_MARK, Pos.CENTER, quiet);
        String how = null;
        if (mine) {
            CityMeasures.Figure f = owner.figures().get(p.key());
            how = f == null ? null : f.how();
        }
        String sourceText = !Double.isFinite(line.real2025()) ? "" : mine && how != null && !how.isBlank() ? how : sourceName(wm, line.realSource());
        Label source = fixed(sourceText, D_SOURCE, Pos.CENTER_LEFT, Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        if (!sourceText.isEmpty()) HistoryScreen.tip(source, mine && how != null ? how : sourceFull(wm, line.realSource()));
        Label yours = fixed(figure(p, line.yourWorld()), D_YOURS, Pos.CENTER_RIGHT, Double.isFinite(line.yourWorld()) ? figureStyle : quiet);
        Label yoursMark = fixed(glyph(wm, line.yourMark()), D_MARK, Pos.CENTER, quiet);

        HBox row = new HBox(4, name, real, realMark, source, yours, yoursMark);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-cursor: hand;");
        row.setOnMouseClicked(e -> {
            measure = p.key();
            showRankingsMenu();
        });
        return row;
    }

    /** A source's name in a few words: the game's world and the city by what they are, a published source by its name up to the first comma. */
    private static String sourceName(WorldMeasures wm, String id) {
        if (id == null) return "";
        if (Rankings.SOURCE_WORLD.equals(id)) return "the game's world, its path";
        if (Rankings.SOURCE_CITY.equals(id)) return "your city";
        if (Rankings.SOURCE_SAVE.equals(id)) return "your saved city";
        WorldMeasures.Source s = wm.sources().get(id);
        if (s == null) return id;
        int comma = s.name().indexOf(", ");
        return comma > 0 ? s.name().substring(0, comma) : s.name();
    }

    /** ...and in full, for the tooltip: its name and its licence. */
    private static String sourceFull(WorldMeasures wm, String id) {
        if (id == null) return "";
        if (Rankings.SOURCE_WORLD.equals(id)) return "The nations of the game's world, on their path, at the city's month";
        if (Rankings.SOURCE_SAVE.equals(id)) return "A city you saved, worked out from its own save, converted by its own rate and prices";
        WorldMeasures.Source s = wm.sources().get(id);
        return s == null ? id : s.name() + " (" + s.licence() + ")";
    }
}
