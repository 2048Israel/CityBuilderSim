package ham.citybuildersim.ui;

import ham.citybuildersim.CameraPath;
import ham.citybuildersim.CityCalendar;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameVersion;
import ham.citybuildersim.Recording;
import ham.citybuildersim.ReplayCity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own
 * window, with a time bar across the bottom to move through the months - play either way, step, drag, at 1 to 16 months a
 * second - and a cinematic mode that hides everything round the picture for the screen recorder. This is the screen's half:
 * the list of recordings, putting the real game aside and giving it back, the replay's own menu, the keys, the bar, the
 * window's size for the recorder. ReplayCity does the loading, UserInterface the little of itself this reaches (its
 * replay... methods), and every figure on screen is the copy's. The camera's moves are ReplayCamera's (marks A and B, the
 * glide, the fly, the drift; its arithmetic CameraPath) and the faint graphs behind the city ReplayGraphLayer's: this takes their
 * keys, puts their buttons in the bar's second row, and is the month a glide moves (showMonth()).
 *
 * <p>Nothing is written. The copy lives in a scratch folder of its own; the player's save slots, the autosave and the recording
 * are never touched; the clock never runs (UserInterface treats a replay as paused, and the time skip does nothing); Save,
 * Load, New city and Continue are not in the replay's menu. "Nothing sticks": any click may change the copy, and the next
 * whole load of a month replaces it.
 *
 * <p>Moving in time is {@link ReplayCity#stepQuick} - only the map - and, when that cannot be (the land changed between the
 * months), a whole load. When the month on screen has stood still for {@link #REST_MS}, and it was not loaded whole, it is
 * loaded whole ({@link ReplayCity#loadWhole}): the same reset a load does, the open screen drawn again, so every screen shows
 * exactly that month, its charts up to it.
 *
 * WHY IT EXISTS (TR1): Jerus films the game's trailer from a recorded city, as often as he likes, and clicks around it in
 * navigation mode. A live take cannot be repeated (a peg breaks by chance, a collapse comes when it comes); a recording can.
 * Everything here runs on the FX thread: the copy loads on the calling thread and the screens read it as it loads.
 */
final class ReplayMode {

    /** The list of recordings, as a screen's name (clearMenu()): a screen of the menu's own, like the Load list. */
    static final String LIST = "showReplayList";

    /** How long the month on screen must have stood still before it is loaded whole, in milliseconds: long enough that a drag, a held key or a play never pays for a load it will not show, short enough that a bar that has stopped answers at once. */
    static final long REST_MS = 300;

    /** The bar's speeds, in recorded months a second. */
    static final double[] SPEEDS = {1, 2, 4, 8, 16};

    /** The speed the bar starts at: an index into {@link #SPEEDS}, 4 months a second. */
    static final int START_SPEED = 2;

    /** How long a note stays on the bar, in milliseconds. */
    static final long NOTE_MS = 4_000;

    /** The scene the screen recorder is given, in pixels: the bar's "1920 x 1080" button sets the window to exactly it. */
    static final int FILM_W = 1920, FILM_H = 1080;

    private final UserInterface ui;
    private boolean on;

    /** The camera's moves (the marks, the glide, the fly, the smooth hand's drift) and the faint graphs behind the city: this mode's keys and bar drive them. */
    private final ReplayCamera camera;
    private final ReplayGraphLayer graphs;
    private boolean graphsLoaded;

    // The real game, put aside while the replay runs, and what the window held of it.
    private Game realGame;
    private boolean realCityOpen;
    private String realTitle;
    private Runnable realResume;

    // The replay.
    private ReplayCity city;
    private int[] months;
    private int at;
    private boolean playing;
    private int dir = 1;
    private int speed = START_SPEED;
    private double acc;
    private long lastMoveNs;
    private long beganNs;
    private int restFailed = -1;
    private String note;
    private long noteUntilNs;
    private long lastFrame;

    // The bar.
    private VBox bar;
    private Slider slider;
    private boolean syncing;
    private Button bStepBack, bPlayBack, bPlay, bPlayFwd, bStepFwd;
    // The bar's camera group (TR1, camera tools).
    private Button bMarkA, bMarkB, bGraphs;
    private TextField glideField;
    private Label driftLabel;
    private CameraPath.View shownA, shownB;
    private boolean shownGraphs;
    private String shownDrift = "";
    private SVGPath playGlyph;
    private final List<Label> speedChips = new ArrayList<>();
    private Label readout, noteLabel;
    private String shownReadout = "", shownNote = "";
    private int shownSpeed = -1, shownDir = 0;
    private boolean shownPlaying;
    private double paddedBy = -1;

    /** The replay's frame, made when the first replay opens: an AnimationTimer needs the toolkit, which a harness's UserInterface does not have. */
    private AnimationTimer timer;

    ReplayMode(UserInterface ui) {
        this.ui = ui;
        this.camera = new ReplayCamera(this, ui);
        this.graphs = new ReplayGraphLayer(this, ui);
    }

    boolean isOn() { return on; }

    /** The replay's camera: MapView asks for it each frame (UserInterface.replayCamera()). */
    ReplayCamera camera() { return camera; }

    /** The recorded month on screen. */
    int shownMonth() { return months[at]; }

    /**
     * The camera asks for a month (a glide of views with different months moves time with it): the recorded month nearest it, by
     * quick steps; the bar's play stops. The month on screen is loaded whole when the glide ends and it rests (frame()).
     */
    void showMonth(int month) {
        if (!on || city == null) return;
        int index = Arrays.binarySearch(months, city.nearest(month));
        if (index < 0 || index == at) return;
        playing = false;
        go(index);
    }

    /** The window's title in a replay: "Replay - Medellin - Civic Ledger 0.9.0". */
    String title() {
        return "Replay - " + ui.game.getCityName() + " - " + GameVersion.title();
    }

    /* ------------------------------- the menu's entries ------------------------------- */

    /**
     * The main menu's two entries (TR1): "Record this city" - only while a city is open - which says under it what it keeps,
     * and "Replay a recording", always.
     */
    List<Node> menuEntries() {
        List<Node> entries = new ArrayList<>();
        Game g = ui.game;
        if (ui.replayCityOpen() && g != null && !g.isReplayCopy()) {
            boolean recording = g.isRecording();
            String under = "every month kept for replay";
            if (recording) {
                Recording r = Recording.at(g.recordingFolder());
                int kept = r.months().length;
                under = (kept == 0 ? "nothing written yet" : kept + (kept == 1 ? " month" : " months") + " kept") + " · " + megabytes(r.bytes());
            }
            Button record = ui.replayButton("Record this city: " + (recording ? "on" : "off"), under, false);
            record.setOnAction(e -> {
                g.setRecording(!g.isRecording());
                System.out.println("Recording " + (g.isRecording() ? "on" : "off") + " for " + g.getCityName() + ".");
                ui.showMainMenu();
            });
            entries.add(record);
        }
        Button replay = ui.replayButton("Replay a recording", "film a recorded city again, month by month", false);
        replay.setOnAction(e -> showList(null));
        entries.add(replay);
        return entries;
    }

    private static String megabytes(long bytes) {
        return String.format("%.1f MB", bytes / 1_048_576.0);
    }

    /* ----------------------------------- the list ----------------------------------- */

    /** The list of recordings, drawn as the Load list is; {@code failure} is shown at its head (a recording that would not open). */
    void showList(String failure) {
        ui.clearMenu(LIST, () -> showList(null));
        VBox page = ui.replayPage();

        Label heading = new Label("REPLAY A RECORDING");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        page.getChildren().add(heading);

        if (failure != null) {
            Label bad = new Label(failure);
            bad.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.BAD + ";");
            bad.setWrapText(true);
            bad.setMaxWidth(360);
            page.getChildren().add(bad);
        }

        // The real game's folder, read and not written.
        List<Recording> all = Recording.all(ui.game.getGameFiles().recordingsDirectory());
        if (all.isEmpty()) {
            Label none = new Label("No recordings yet. Open a city, choose \"Record this city\" in the Menu, and play: every month"
                    + " is kept, and the recording appears here.");
            none.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            none.setWrapText(true);
            none.setMaxWidth(360);
            page.getChildren().add(none);
        }
        for (Recording r : all) page.getChildren().add(row(r));

        Button back = new Button("Back");
        back.setOnAction(e -> ui.showMainMenu());
        page.getChildren().add(back);
    }

    /** One recording: the city's name on the button, and under it its months, how many are kept, its size and when it was started. */
    private VBox row(Recording r) {
        VBox row = new VBox(1);
        row.setAlignment(Pos.CENTER);
        Recording.Meta meta = r.meta();
        String name = meta == null || meta.cityName() == null || meta.cityName().isBlank() ? r.id() : meta.cityName();
        Button pick = new Button(name);
        pick.setMaxWidth(300);

        int[] ms = r.months();
        String detail;
        if (ms.length == 0) {
            detail = "no months kept · " + megabytes(r.bytes());
            pick.setDisable(true);
        } else {
            detail = CityCalendar.formatShort(ms[0]) + " to " + CityCalendar.formatShort(ms[ms.length - 1])
                    + " · months " + ms[0] + "-" + ms[ms.length - 1] + " (" + ms.length + " kept) · " + megabytes(r.bytes());
        }
        if (meta != null && meta.startedAt() > 0) {
            detail += " · started " + java.time.Instant.ofEpochMilli(meta.startedAt()).atZone(java.time.ZoneId.systemDefault())
                    .format(java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"));
        }
        pick.setOnAction(e -> enter(r));

        Label line = new Label(detail);
        line.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (ms.length == 0 ? Palette.TEXT_FAINT : Palette.TEXT_MUTED) + ";");
        line.setWrapText(true);
        line.setMaxWidth(360);
        row.getChildren().addAll(pick, line);
        return row;
    }

    /* ------------------------------ entering and leaving ------------------------------ */

    /**
     * Opens a recording at its first month. The month is loaded first, so a recording that will not load leaves everything as
     * it was and says why on the list; then the real game is put aside (its city, whether one was open, its title, the screen
     * Continue goes back to, the clock stopped), the copy takes its place in the window, the per-city reset a load does is
     * done, the city opened and the bar shown.
     */
    private void enter(Recording rec) {
        int[] ms = rec.months();
        if (ms.length == 0) {
            showList("That recording holds no months.");
            return;
        }
        ReplayCity rc;
        String failure;
        try {
            rc = new ReplayCity(rec);
            failure = wholeLoad(rc, ms[0]);
        } catch (RuntimeException e) {
            showList("Could not open that recording: " + e.getMessage());
            return;
        }
        if (failure != null) {
            showList("Could not open that recording: " + failure);
            return;
        }
        realGame = ui.game;
        realCityOpen = ui.replayCityOpen();
        realTitle = ui.replayStage().getTitle();
        realResume = ui.replayResumeTo();
        ui.replayStopClock();

        city = rc;
        months = ms;
        at = 0;
        playing = false;
        dir = 1;
        speed = START_SPEED;
        acc = 0;
        restFailed = -1;
        note = null;
        camera.reset();
        graphs.leave();
        graphsLoaded = false;
        beganNs = System.nanoTime();
        lastMoveNs = beganNs;
        on = true;
        ui.game = rc.game();
        ui.replaySetResumeTo(null);
        ui.replayAnotherCity();
        buildBar();
        showBar();
        lastFrame = 0;
        if (timer == null) {
            timer = new AnimationTimer() {
                @Override public void handle(long now) { frame(now); }
            };
        }
        timer.start();
        ui.replayOpenCity();
    }

    /**
     * Leaves the replay: cinematic mode off, the bar gone, the real game back on screen (the per-city reset done for it, whether
     * a city was open as it was, the screen Continue goes back to and the window's title as they were), then the title screen,
     * where Continue goes back to the real city if one was open. The copy is dropped.
     */
    void leave() {
        if (!on) return;
        setCinematic(false);
        if (timer != null) timer.stop();
        playing = false;
        camera.reset();
        graphs.leave();
        hideBar();
        on = false;
        ui.game = realGame;
        ui.replayAnotherCity();
        ui.replaySetCityOpen(realCityOpen);
        ui.replayStage().setTitle(realTitle);
        System.out.println("Replay: left " + (city == null ? "" : city.recording().id()) + "; the real game is back on screen.");
        city = null;
        months = null;
        realGame = null;
        ui.showMainMenu();
        // Continue goes back to the real city's own screen, not to the replay's last, which the menu has just taken as its.
        ui.replaySetResumeTo(realResume);
        realResume = null;
    }

    /** A month loaded whole (ReplayCity.loadWhole), its time in the log; a load that threw is a failure with the reason, as one that returned it is. */
    private static String wholeLoad(ReplayCity rc, int month) {
        String failure;
        try {
            failure = rc.loadWhole(month);
        } catch (RuntimeException e) {
            failure = "the month would not load (" + e + ")";
        }
        System.out.println(String.format("Replay: %s, month %d loaded in full in %.0f ms%s.", rc.recording().id(), month, rc.lastLoadMs(),
                failure == null ? "" : " - FAILED: " + failure));
        return failure;
    }

    /* --------------------------------- the replay's menu --------------------------------- */

    /**
     * In a replay the main menu is this one: Back to the replay, Leave the replay, Quit - no Save, Load, New city or Continue.
     * Drawn on the title layer as the main menu is. False, and nothing drawn, when no replay is on.
     */
    boolean showMenu() {
        if (!on) return false;
        setCinematic(false);
        playing = false;
        ui.replayHideTooltips();
        ui.clearMenu("showMainMenu", () -> ui.showMainMenu());
        StackPane layer = ui.titleContent();

        javafx.scene.layout.Pane name = Branding.title(GameVersion.NAME, 54);
        Label line = new Label("A city, its money, and everyone in it.");
        line.setStyle("-fx-font-size: 17px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        VBox.setMargin(line, new Insets(0, 0, 30, 0));

        String where = ui.game.getCityName() + " · month " + months[at] + " of " + months[0] + "-" + months[months.length - 1] + " · replay";
        Button back = ui.replayButton("Back to the replay", where, true);
        back.setOnAction(e -> {
            Runnable again = ui.replayResumeTo();
            if (again != null) again.run(); else ui.replayOpenCity();
        });
        Button leave = ui.replayButton("Leave the replay", "back to the title screen; the copy is dropped", false);
        leave.setOnAction(e -> leave());
        Button quit = ui.replayButton("Quit", null, false);
        // Leaves the replay first, so the real city is the one the quit asks about (and autosaves).
        quit.setOnAction(e -> {
            leave();
            ui.replayQuit();
        });

        VBox column = new VBox(12, name, line, back, leave, quit);
        column.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(column, Pos.TOP_LEFT);

        Label version = new Label(GameVersion.VERSION);
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        StackPane.setAlignment(version, Pos.BOTTOM_LEFT);
        StackPane.setMargin(version, new Insets(0, 0, 36, UserInterface.MENU_LEFT));

        layer.getChildren().addAll(column, version);
        Runnable place = () -> {
            column.applyCss();
            double tall = layer.getHeight() > 0 ? layer.getHeight() : ui.replayStage().getHeight();
            double top = Math.max(24, Math.min(110, (tall - column.prefHeight(-1)) / 2.4));
            StackPane.setMargin(column, new Insets(top, 0, 0, UserInterface.MENU_LEFT));
        };
        place.run();
        ui.replayPlaceMenu(place);
        back.requestFocus();
        return true;
    }

    /* ------------------------------------- the keys ------------------------------------- */

    /**
     * The replay's keys, taken at the top of the window's key filter (UserInterface); true when one was taken, and the event is
     * consumed there, so none reaches the clock's Space or the speed ladder's arrows. Not while someone is typing in a text
     * field, not with Ctrl, Alt or Meta held (those are the map card's, and the platform's), and not while the replay's menu
     * or the list is up. Esc is cinematic mode's first: it brings everything back, and only then is Esc the card's, the map's
     * and the menu's as everywhere.
     *
     * <p>Space play or pause; B the direction; Left and Right a month back or forward; Up and Down the speed; Home and End the
     * first and the last month; C cinematic mode.
     *
     * <p>The camera's, with the expanded map over the window (M opens it; any other says so on the bar): 1 marks view A and 2 view
     * B (the map's view and the month on screen), 3 glides from A to B over the seconds the bar's field says, time moving with it
     * when their months differ; O flies out to the whole Earth and I flies back in to where it began; Shift+W, A, S, D each add a
     * step of drift that way (DRIFT_STEP_PX a second more), R and F drift the zoom in and out, X stops every drift and move; G
     * the graphs behind the city on and off. Esc stops a glide or fly where it is, before it is cinematic mode's.
     */
    boolean key(KeyEvent e, boolean typing) {
        if (!on) return false;
        KeyCode code = e.getCode();
        boolean menuUp = ui.replayGameMenuUp();
        if (code == KeyCode.ESCAPE) {
            if (menuUp) return false;
            if (camera.busy()) {
                camera.stop();
                return true;
            }
            if (!ui.isCinematic()) return false;
            setCinematic(false);
            return true;
        }
        if (typing || menuUp || e.isControlDown() || e.isAltDown() || e.isMetaDown()) return false;
        switch (code) {
            case SPACE -> togglePlay();
            case B -> turn();
            case LEFT -> step(-1);
            case RIGHT -> step(1);
            case UP -> setSpeed(speed + 1);
            case DOWN -> setSpeed(speed - 1);
            case HOME -> jump(0);
            case END -> jump(months.length - 1);
            case C -> setCinematic(!ui.isCinematic());
            case DIGIT1, NUMPAD1 -> camera.mark(true);
            case DIGIT2, NUMPAD2 -> camera.mark(false);
            case DIGIT3, NUMPAD3 -> glide();
            case O -> camera.flyOut();
            case I -> camera.flyIn();
            case X -> camera.stillAll();
            case G -> toggleGraphs();
            case M -> ui.replayOpenMap();
            case R -> drift(0, 0, CameraPath.DRIFT_ZOOM_STEP);
            case F -> drift(0, 0, -CameraPath.DRIFT_ZOOM_STEP);
            case W, A, S, D -> {
                // Plain W, A, S, D pan the map (MapView); with Shift each adds a step of drift. The ground moves the other way from the view.
                if (!e.isShiftDown()) return false;
                double step = CameraPath.DRIFT_STEP_PX;
                switch (code) {
                    case W -> drift(0, step, 0);
                    case S -> drift(0, -step, 0);
                    case A -> drift(step, 0, 0);
                    default -> drift(-step, 0, 0);
                }
            }
            default -> { return false; }
        }
        return true;
    }

    /** A step of drift, with the map over the window; with none, the bar says so. */
    private void drift(double across, double down, double zoom) {
        if (ui.expandedMapCameraView() == null) {
            say("open the map first (M)");
            return;
        }
        camera.driftBy(across, down, zoom);
    }

    /** The glide, from the key or the bar's button: the seconds the field says first. */
    private void glide() {
        commitGlideSeconds();
        camera.glide();
    }

    /** G or the bar's button: the faint GDP and population lines behind the city on or off. */
    private void toggleGraphs() {
        if (!graphsLoaded) {
            graphs.load(city);
            graphsLoaded = true;
        }
        say("graphs behind the city: " + (graphs.toggle() ? "on" : "off"));
    }

    /** The bar's field of seconds read into the camera, and written back as it took it. */
    private void commitGlideSeconds() {
        if (glideField == null) return;
        try {
            camera.setGlideSeconds(Double.parseDouble(glideField.getText().trim().replace(',', '.')));
        } catch (NumberFormatException bad) {
            camera.setGlideSeconds(Double.NaN);
        }
        double s = camera.glideSeconds();
        glideField.setText(s == Math.rint(s) ? String.valueOf((long) s) : String.format("%.1f", s));
    }

    /* ------------------------------------ the clock ------------------------------------ */

    /**
     * The clock the map's boats, trains and trucks sail on (UserInterface.clockMonths()): the copy's month and the real
     * seconds since the replay began, over the seconds a month takes - growing without wrapping, so they keep sailing with the
     * game's clock stopped. MapView divides it by BOAT_GAME_MONTHS (12) and takes the whole part as the schedule's number and
     * the rest as the time in it (boatClock(), boats(), trains(), trucks()); nothing compares it with the game's month, so a
     * clock many months past the copy's is drawn as any other: a new schedule each boat-month (a minute), as in play. A month
     * stepped moves it by that month, so the boats jump forward or back by the step (a twelfth of their month) while the bar
     * plays; at rest they sail on real time.
     */
    double clockMonths() {
        return ui.game.getMonth() + (System.nanoTime() - beganNs) / 1e9 / UserInterface.SECONDS_PER_MONTH;
    }

    /* ----------------------------------- moving in time ----------------------------------- */

    private void togglePlay() {
        playing = !playing;
        acc = 0;
    }

    private void play(int direction) {
        dir = direction;
        playing = true;
        acc = 0;
    }

    private void turn() {
        dir = -dir;
        acc = 0;
    }

    private void step(int by) {
        playing = false;
        go(at + by);
    }

    private void jump(int index) {
        playing = false;
        go(index);
    }

    private void setSpeed(int index) {
        speed = Math.max(0, Math.min(SPEEDS.length - 1, index));
    }

    /**
     * Shows the month at {@code index} of the recording's months (a gap in the recording is stepped over: the index is into
     * the months that are there): only the map, by ReplayCity.stepQuick; when the land changed between the months and that
     * cannot be, the month whole. The map is asked to draw again. A month already on screen is left alone.
     */
    private void go(int index) {
        int to = Math.max(0, Math.min(months.length - 1, index));
        if (to == at) return;
        at = to;
        lastMoveNs = System.nanoTime();
        restFailed = -1;
        int month = months[at];
        if (city.stepQuick(month)) {
            ui.replayMapChanged();
            return;
        }
        // The map could not be swapped (the land changed between the months, or the month has no map): the whole month, now.
        String failure = wholeLoad(city, month);
        if (failure != null) {
            restFailed = month;
            say("Month " + month + " could not be loaded: " + failure);
            return;
        }
        say("The land changed between these months: loaded in full.");
        afterWhole();
    }

    /** The month on screen has stood still: it is loaded whole, and every screen shows exactly that month. */
    private void rest() {
        int month = months[at];
        String failure = wholeLoad(city, month);
        if (failure != null) {
            restFailed = month;
            say("Month " + month + " could not be loaded in full: " + failure);
            return;
        }
        afterWhole();
    }

    /** A month loaded whole: the per-city reset a load does (the expanded map and its view kept), the open screen drawn again. */
    private void afterWhole() {
        ui.replayAnotherCity();
        ui.redraw();
    }

    void say(String words) {
        note = words;
        noteUntilNs = System.nanoTime() + NOTE_MS * 1_000_000L;
    }

    /* ------------------------------------- the frame ------------------------------------- */

    private void frame(long now) {
        if (!on || city == null) return;
        double dt = lastFrame == 0 ? 0 : Math.min(0.1, (now - lastFrame) / 1e9);
        lastFrame = now;
        keepBarAbove();
        // A hand on the bar's time (play, the slider) takes it from a glide.
        if (camera.busy() && (playing || (slider != null && slider.isValueChanging()))) camera.hand();
        if (ui.replayGameMenuUp()) {
            playing = false;
        } else if (playing) {
            advance(dt);
        } else if (!city.wholeMonth() && months[at] != restFailed && !ui.replayPressHeld() && !camera.busy()
                && System.nanoTime() - lastMoveNs >= REST_MS * 1_000_000L) {
            rest();
        }
        refreshBar();
        graphs.frame(months[at], paddedBy > 0 ? paddedBy : 0);
    }

    /** Play: the months due since the last frame, in the direction chosen; the end of the recording either way ends the play. */
    private void advance(double dt) {
        acc += dir * SPEEDS[speed] * dt;
        int steps = (int) acc;
        if (steps == 0) return;
        acc -= steps;
        int to = at + steps;
        if (to <= 0 || to >= months.length - 1) {
            to = Math.max(0, Math.min(months.length - 1, to));
            playing = false;
            acc = 0;
        }
        go(to);
    }

    /* -------------------------------------- the bar -------------------------------------- */

    private void buildBar() {
        slider = new Slider(0, Math.max(1, months.length - 1), 0);
        slider.setBlockIncrement(1);
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.setFocusTraversable(false);
        slider.valueProperty().addListener((o, was, now) -> {
            if (syncing) return;
            playing = false;
            go((int) Math.round(now.doubleValue()));
        });
        Tooltip.install(slider, new Tooltip("Drag to any recorded month  (Home first month · End last month)"));

        bStepBack = barButton(null, "A month back  (Left)", () -> step(-1));
        bStepBack.setGraphic(glyph("M6 6h2v12H6z M18 6v12L9.5 12z"));
        bPlayBack = barButton(null, "Play backward  (B turns the direction)", () -> play(-1));
        bPlayBack.setGraphic(glyph("M17 6v12L7 12z"));
        playGlyph = glyph("M7 5h3.5v14H7z M13.5 5h3.5v14h-3.5z");
        bPlay = barButton(null, "Play or pause  (Space)", this::togglePlay);
        bPlay.setGraphic(playGlyph);
        bPlayFwd = barButton(null, "Play forward  (B turns the direction)", () -> play(1));
        bPlayFwd.setGraphic(glyph("M7 6v12l10-6z"));
        bStepFwd = barButton(null, "A month forward  (Right)", () -> step(1));
        bStepFwd.setGraphic(glyph("M16 6h2v12h-2z M6 6v12l8.5-6z"));
        HBox transport = new HBox(4, bStepBack, bPlayBack, bPlay, bPlayFwd, bStepFwd);
        transport.setAlignment(Pos.CENTER_LEFT);

        HBox speeds = new HBox(4);
        speeds.setAlignment(Pos.CENTER_LEFT);
        speedChips.clear();
        for (int i = 0; i < SPEEDS.length; i++) {
            final int index = i;
            Label chip = new Label(String.valueOf((int) SPEEDS[i]));
            chip.setMinWidth(Region.USE_PREF_SIZE);
            chip.setOnMouseClicked(e -> setSpeed(index));
            Tooltip.install(chip, new Tooltip((int) SPEEDS[i] + " months a second  (Up and Down)"));
            speedChips.add(chip);
            speeds.getChildren().add(chip);
        }
        Label perSecond = new Label("months a second");
        perSecond.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        perSecond.setMinWidth(Region.USE_PREF_SIZE);
        speeds.getChildren().add(perSecond);

        readout = new Label();
        readout.setStyle(Palette.figure(13, Palette.TEXT_HEAD));
        readout.setMinWidth(Region.USE_PREF_SIZE);

        noteLabel = new Label();
        noteLabel.setStyle(Palette.words(Palette.SIZE_BODY, Palette.WARN));
        noteLabel.setMinWidth(0);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Button cinematic = barButton("Cinematic", "Hide everything but the picture  (C, and Esc brings it back)", () -> setCinematic(true));
        Button film = barButton(FILM_W + " x " + FILM_H, "Set the window so its picture is exactly " + FILM_W + " x " + FILM_H
                + ", for the screen recorder", this::filmSize);
        Button leave = barButton("Leave", "Leave the replay and go back to the title screen", this::leave);

        HBox row = new HBox(14, transport, speeds, readout, gap, noteLabel, cinematic, film, leave);
        row.setAlignment(Pos.CENTER_LEFT);

        bar = new VBox(6, slider, row, cameraRow());
        bar.setMaxHeight(Region.USE_PREF_SIZE);
        bar.setStyle("-fx-padding: 8 14 10 14; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 1 0 0 0;");
        StackPane.setAlignment(bar, Pos.BOTTOM_CENTER);
        shownReadout = shownNote = "";
        shownSpeed = -1;
        shownDir = 0;
        paddedBy = -1;
    }

    /** A bar button: it stops a glide or fly in flight where it is, and then does what it says. */
    private Button barButton(String text, String tip, Runnable act) {
        return camButton(text, tip, () -> {
            camera.hand();
            act.run();
        });
    }

    private Button camButton(String text, String tip, Runnable act) {
        Button b = new Button(text);
        b.setStyle("-fx-padding: 5 10 5 10;");
        b.setFocusTraversable(false);
        b.setOnAction(e -> act.run());
        Tooltip.install(b, new Tooltip(tip));
        return b;
    }

    /**
     * The bar's camera group (TR1): the map button, A and B (lit when marked, their tooltips say the mark), Glide with its field
     * of seconds, Fly out and Fly in, Graphs (lit when on), Stop - and, when a drift is set, its reading. These do not stop a move
     * in flight themselves: each starts its own, which replaces it.
     */
    private HBox cameraRow() {
        Label name = new Label("CAMERA");
        name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        name.setMinWidth(Region.USE_PREF_SIZE);
        Button map = camButton("Map", "Open the map over the window on the city  (M)", ui::replayOpenMap);
        bMarkA = camButton("A", "Mark the map's view and the month on screen as view A  (1)", () -> camera.mark(true));
        bMarkB = camButton("B", "Mark the map's view and the month on screen as view B  (2)", () -> camera.mark(false));
        glideField = new TextField(String.valueOf((long) camera.glideSeconds()));
        glideField.setPrefColumnCount(3);
        glideField.setMaxWidth(52);
        glideField.setOnAction(e -> {
            commitGlideSeconds();
            ui.replayScene().getRoot().requestFocus();
        });
        glideField.focusedProperty().addListener((o, was, now) -> {
            if (!now) commitGlideSeconds();
        });
        Tooltip.install(glideField, new Tooltip("Seconds the glide takes, " + (int) ReplayCamera.GLIDE_S_LEAST + " to " + (int) ReplayCamera.GLIDE_S_MOST
                + "  (Enter sets it)"));
        Button glide = camButton("Glide", "Glide from view A to view B over the seconds in the field, time moving with it when their months differ"
                + "  (3; Esc stops it)", this::glide);
        Label seconds = new Label("s");
        seconds.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Button out = camButton("Fly out", "Pull out from the view to the whole Earth  (O)", camera::flyOut);
        Button in = camButton("Fly in", "Fly back in to where the fly out began  (I)", camera::flyIn);
        bGraphs = camButton("Graphs", "GDP and population lines behind the city, rising as the months play  (G)", this::toggleGraphs);
        Button stop = camButton("Stop", "Stop every glide, fly, drift and move  (X)", camera::stillAll);
        driftLabel = new Label();
        driftLabel.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));
        driftLabel.setMinWidth(0);
        shownA = shownB = null;
        shownGraphs = false;
        shownDrift = "";
        HBox row = new HBox(8, name, map, bMarkA, bMarkB, glide, glideField, seconds, out, in, bGraphs, stop, driftLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static SVGPath glyph(String content) {
        SVGPath p = new SVGPath();
        p.setContent(content);
        p.setFill(Color.web(Palette.TEXT_HEAD));
        return p;
    }

    /** The bar laid over the bottom of the window, and the page above it given room so the bar covers no part of it. */
    private void showBar() {
        ui.windowStack.getChildren().add(bar);
        keepBarAbove();
    }

    private void hideBar() {
        ui.windowStack.getChildren().remove(bar);
        BorderPane root = ui.replayRoot();
        if (root != null) root.setPadding(Insets.EMPTY);
        paddedBy = -1;
        bar = null;
    }

    /**
     * The bar's place in the window's stack, kept every frame: above the window and the expanded map's pane (which is added
     * to the stack when it expands, after the bar), below the menu's layer and a dialog. Done in the frame's start, before
     * the pulse draws, so a layer added by a click never shows over the bar for a frame.
     */
    private void keepBarAbove() {
        if (bar == null) return;
        List<Node> kids = ui.windowStack.getChildren();
        int cur = kids.indexOf(bar);
        int want = kids.size() - (cur >= 0 ? 1 : 0);
        Node title = ui.replayTitleLayer(), dialog = ui.replayDialog();
        for (int i = 0; i < kids.size(); i++) {
            Node k = kids.get(i);
            if (k == bar || (k != title && k != dialog)) continue;
            want = Math.min(want, cur >= 0 && cur < i ? i - 1 : i);
        }
        if (cur != want) {
            if (cur >= 0) kids.remove(cur);
            kids.add(Math.min(want, kids.size()), bar);
        }
        graphs.keepUnder(bar);      // the faint graphs (G) lie just under the bar: over the content and the expanded map
    }

    /** The bar's figures and lit buttons, set only when they change; the window's room for the bar. */
    private void refreshBar() {
        if (bar == null) return;
        int month = months[at];
        String text = CityCalendar.format(month) + "  ·  month " + month + " of " + months[0] + "-" + months[months.length - 1];
        if (!text.equals(shownReadout)) {
            shownReadout = text;
            readout.setText(text);
        }
        if (!slider.isValueChanging() && (int) Math.round(slider.getValue()) != at) {
            syncing = true;
            slider.setValue(at);
            syncing = false;
        }
        String doing = camera.status();
        String say = doing != null ? doing : note != null && System.nanoTime() < noteUntilNs ? note : "";
        if (!say.equals(shownNote)) {
            shownNote = say;
            noteLabel.setText(say);
        }
        refreshCamera();
        if (shownSpeed != speed) {
            shownSpeed = speed;
            for (int i = 0; i < speedChips.size(); i++) {
                boolean lit = i == speed;
                speedChips.get(i).setStyle("-fx-padding: 2 7 2 7; -fx-cursor: hand;" + Palette.block(lit ? Palette.PINNED : Palette.PANEL, lit ? Palette.ACCENT : Palette.EDGE)
                        + Palette.words(Palette.SIZE_LABEL, lit ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
            }
        }
        if (shownPlaying != playing || shownDir != dir) {
            shownPlaying = playing;
            shownDir = dir;
            playGlyph.setContent(playing ? "M7 5h3.5v14H7z M13.5 5h3.5v14h-3.5z" : dir > 0 ? "M7 6v12l10-6z" : "M17 6v12L7 12z");
            bPlayBack.setStyle("-fx-padding: 5 10 5 10;" + (playing && dir < 0 ? " -fx-border-color: " + Palette.ACCENT + ";" : ""));
            bPlayFwd.setStyle("-fx-padding: 5 10 5 10;" + (playing && dir > 0 ? " -fx-border-color: " + Palette.ACCENT + ";" : ""));
        }
        // The page ends above the bar, not under it; none of it while the bar is hidden (cinematic mode).
        BorderPane root = ui.replayRoot();
        double room = bar.isVisible() ? Math.ceil(bar.getHeight()) : 0;
        if (root != null && room != paddedBy) {
            paddedBy = room;
            root.setPadding(new Insets(0, 0, room, 0));
        }
    }

    /** The camera group's lit buttons, their tooltips and the drift's reading, set only when they change. */
    private void refreshCamera() {
        CameraPath.View a = camera.markA(), b = camera.markB();
        boolean g = graphs.isOn();
        if (!java.util.Objects.equals(a, shownA)) {
            shownA = a;
            litButton(bMarkA, a != null);
            if (a != null) Tooltip.install(bMarkA, new Tooltip("View A: " + ReplayCamera.words(a) + "  (1 marks it again)"));
        }
        if (!java.util.Objects.equals(b, shownB)) {
            shownB = b;
            litButton(bMarkB, b != null);
            if (b != null) Tooltip.install(bMarkB, new Tooltip("View B: " + ReplayCamera.words(b) + "  (2 marks it again)"));
        }
        if (g != shownGraphs) {
            shownGraphs = g;
            litButton(bGraphs, g);
        }
        String drift = camera.driftReading();
        if (drift == null) drift = "";
        if (!drift.equals(shownDrift)) {
            shownDrift = drift;
            driftLabel.setText(drift);
        }
    }

    private static void litButton(Button b, boolean lit) {
        b.setStyle("-fx-padding: 5 10 5 10;" + (lit ? " -fx-border-color: " + Palette.ACCENT + ";" : ""));
    }

    /* ----------------------------- cinematic mode, the window ----------------------------- */

    /**
     * Cinematic mode on or off: the window's chrome (UserInterface.replayCinematic()) and the time bar. Off brings everything
     * back as it was; the page on show stays either way.
     */
    private void setCinematic(boolean want) {
        if (want == ui.isCinematic() || bar == null) return;
        ui.replayCinematic(want);
        bar.setVisible(!want);
        bar.setManaged(!want);
    }

    /**
     * The window set so its scene is exactly 1920 x 1080 for the screen recorder: out of full screen and not maximised, the
     * stage sized to the picture plus the window's decoration (the stage's size less the scene's), then looked at again and
     * corrected. When the screen's visual bounds cannot hold it the bar says so - what the screen is, and that F11 full
     * screen is the screen's own size - and the window is put back as it was.
     */
    private void filmSize() {
        Stage stage = ui.replayStage();
        boolean wasFull = stage.isFullScreen();
        if (wasFull) stage.setFullScreen(false);
        stage.setMaximized(false);
        // The window's own restore after leaving full screen lands after this call (UserInterface.start() maximises on it): wait for it.
        PauseTransition wait = new PauseTransition(Duration.millis(400));
        wait.setOnFinished(e -> fitFilm(wasFull, 0));
        wait.play();
    }

    private void fitFilm(boolean wasFull, int pass) {
        if (!on) return;
        Stage stage = ui.replayStage();
        Scene scene = ui.replayScene();
        stage.setMaximized(false);
        double decoW = stage.getWidth() - scene.getWidth(), decoH = stage.getHeight() - scene.getHeight();
        List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), Math.max(1, stage.getWidth()), Math.max(1, stage.getHeight()));
        Screen screen = screens.isEmpty() ? Screen.getPrimary() : screens.get(0);
        Rectangle2D visual = screen.getVisualBounds(), full = screen.getBounds();
        if (FILM_W + decoW > visual.getWidth() + 0.5 || FILM_H + decoH > visual.getHeight() + 0.5) {
            say(String.format("this screen is %d x %d: F11 full screen is %d x %d", (int) visual.getWidth(), (int) visual.getHeight(),
                    (int) full.getWidth(), (int) full.getHeight()));
            if (wasFull) stage.setFullScreen(true); else stage.setMaximized(true);
            return;
        }
        stage.setWidth(FILM_W + decoW);
        stage.setHeight(FILM_H + decoH);
        stage.setX(Math.max(visual.getMinX(), Math.min(stage.getX(), visual.getMaxX() - stage.getWidth())));
        stage.setY(Math.max(visual.getMinY(), Math.min(stage.getY(), visual.getMaxY() - stage.getHeight())));
        PauseTransition look = new PauseTransition(Duration.millis(200));
        look.setOnFinished(e -> {
            if (!on) return;
            boolean exact = Math.abs(scene.getWidth() - FILM_W) < 0.5 && Math.abs(scene.getHeight() - FILM_H) < 0.5;
            if (exact) {
                say("the window's picture is " + FILM_W + " x " + FILM_H);
            } else if (pass < 2) {
                fitFilm(wasFull, pass + 1);
            } else {
                say(String.format("the window's picture is %d x %d (%d x %d was asked for)", (int) scene.getWidth(), (int) scene.getHeight(), FILM_W, FILM_H));
            }
        });
        look.play();
    }
}
