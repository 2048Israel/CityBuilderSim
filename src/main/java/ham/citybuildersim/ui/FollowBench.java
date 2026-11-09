package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.InputLog;
import ham.citybuildersim.InputTimeline;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.Recording;
import ham.citybuildersim.ReplayCheck;
import ham.citybuildersim.TempDirs;
import ham.citybuildersim.ui.FollowRules.Verdict;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an
 * undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged events played into it, with
 * what each one did read back. {@link #check} gives the findings as {label, "true"/"false"} pairs for a harness to print, and
 * must be called on the FX thread; {@link #main} makes a small city of its own in a scratch folder and prints them. {@link #followCheck}
 * (ReplayCheck section 10) enters a copy of a recording in the window and follows a sitting logged over it, frame by frame.
 *
 * <p>WHY (TR2): the Reenactor fires events into the scene by hand, and whether the scene answers them as it answers a pointer
 * - a tab switches on a click, a handler sees the pointer enter, a wheel moves a pane, a key reaches the game's filter - can only
 * be known by doing it to the real window. Run: {@code java -cp "target/classes;$(cat target/classpath.txt)"
 * ham.citybuildersim.ui.FollowBench}; the exit code is the number of failures.
 */
public final class FollowBench {

    private FollowBench() { }

    /** The window's width the bench lays the scene out at. */
    private static final double WIDTH = 1600;

    /** The window's height the bench lays the scene out at. */
    private static final double HEIGHT = 900;

    /** The findings that failed: what a bench prints as the exit code. */
    private static int failures;

    /**
     * The bench on a small city of its own; {@code args[0]}, if given, is a recording's folder (the copy of one is followed in the
     * window too: {@link #followCheck}).
     */
    public static void main(String[] args) throws Exception {
        long t0 = System.nanoTime();
        System.out.println("=== FollowBench: logged input re-enacted in the window (TR2) ===");
        String cannot = startJavaFx();
        if (cannot != null) {
            System.out.println("skipped: " + cannot);
            return;
        }
        GameFiles files = GameFiles.scratch("followbench");
        Game game = fx(() -> LongPlaytest.probeCity(files, null));
        System.out.println("a city of month " + game.getMonth() + " on the scratch folder " + files.getDirectory());
        List<String[]> found = fx(() -> check(game));
        if (args.length > 0) found.addAll(fx(() -> followCheck(game, Path.of(args[0]))));
        for (String[] f : found) {
            boolean ok = "true".equals(f[1]);
            if (!ok) failures++;
            System.out.println((ok ? "  ok   " : "  FAIL ") + f[0]);
        }
        System.out.printf("Node.setHover by reflection: %s; Node.setPressed: %s%n", Reenactor.setsHover() ? "yes" : "no", Reenactor.setsPressed() ? "yes" : "no");
        System.out.printf("%d findings, %d failed, %.1f s%n", found.size(), failures, (System.nanoTime() - t0) / 1e9);
        System.exit(failures);
    }

    /**
     * Why JavaFX cannot start here, in a few words, or null when it is up. It starts the toolkit if no check before this one has
     * (ReplayMoveProbe's way: a second start is told the toolkit is already there, and that is an answer of yes).
     */
    public static String startJavaFx() {
        try {
            if (GraphicsEnvironment.isHeadless()) return "this JVM is headless (no display)";
            Platform.setImplicitExit(false);
            CountDownLatch up = new CountDownLatch(1);
            try {
                Platform.startup(up::countDown);
            } catch (IllegalStateException alreadyUp) {      // the harness process started it before
                Platform.runLater(up::countDown);
            }
            if (!up.await(60, TimeUnit.SECONDS)) return "the toolkit did not come up in 60 s";
            return null;
        } catch (Throwable t) {
            String why = t.getMessage() == null ? "" : ": " + t.getMessage().replaceAll("\\s+", " ").trim();
            return t.getClass().getSimpleName() + why;
        }
    }

    /** Runs on the FX thread and waits for the answer; an exception in it comes back here. */
    public static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    /** The findings, on the FX thread, for the city {@code game}: the window is built on it and hidden again at the end. */
    public static List<String[]> check(Game game) {
        List<String[]> out = new ArrayList<>();
        UserInterface ui = new UserInterface(game);
        Stage stage = invisibleStage();
        try {
            startWindow(ui, stage);
            run(ui, out);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            out.add(new String[]{"the window was made and played into without an error", "false"});
        } finally {
            stage.hide();
        }
        return out;
    }

    /** A stage as the game's, undecorated, that nobody sees: invisible and off every screen. */
    private static Stage invisibleStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOpacity(0);
        stage.setX(-5000);
        stage.setY(0);
        return stage;
    }

    /** The interface started on the stage as the game starts it, the city opened and the scene laid out at the bench's size. */
    private static void startWindow(UserInterface ui, Stage stage) {
        ui.start(stage);
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setX(-5000);
        ui.replayAnotherCity();
        ui.replayOpenCity();
        layout(ui);
    }

    /** The scene's CSS and layout at the bench's size, as a pulse would do them. */
    private static void layout(UserInterface ui) {
        StackPane root = ui.windowStack;
        root.resize(WIDTH, HEIGHT);
        root.applyCss();
        root.layout();
    }

    private static void add(List<String[]> out, String label, boolean ok) {
        out.add(new String[]{label, Boolean.toString(ok)});
    }

    private static void run(UserInterface ui, List<String[]> out) {
        Reenactor re = ui.reenactor();
        long ms = 0;
        String cityScreen = ui.currentScreen;
        add(out, "the window opens on a city screen, not a game menu", !cityScreen.isEmpty() && !UserInterface.isGameMenu(cityScreen));

        // The key names the rules know are the toolkit's.
        add(out, "the rules' key codes are the toolkit's names for Space, Left, Right, F11, Esc and P",
                FollowRules.SPACE == code(KeyCode.SPACE) && FollowRules.LEFT == code(KeyCode.LEFT) && FollowRules.RIGHT == code(KeyCode.RIGHT)
                        && FollowRules.F11 == code(KeyCode.F11) && FollowRules.ESC == code(KeyCode.ESCAPE) && FollowRules.P == code(KeyCode.P));

        // (a) a click on the rail's People tab
        double[] people = centre(railCell(ui, "People"));
        Verdict move = re.play(InputLog.Event.move(ms += 20, people[0], people[1]), cityScreen);
        Verdict down = re.play(InputLog.Event.press(ms += 20, people[0], people[1], 1, 1, 0), cityScreen);
        Verdict up = re.play(InputLog.Event.release(ms += 80, people[0], people[1], 1, 0), cityScreen);
        add(out, "a press and release at the rail's People tab are re-enacted and take the copy to the People screen",
                move == Verdict.KEEP && down == Verdict.KEEP && up == Verdict.KEEP && "showPopulationInfoMenu".equals(ui.currentScreen));
        layout(ui);

        // (b) a click on the rail's Menu button
        String before = ui.currentScreen;
        double[] menu = centre(railCell(ui, "Menu"));
        re.play(InputLog.Event.move(ms += 20, menu[0], menu[1]), before);
        down = re.play(InputLog.Event.press(ms += 20, menu[0], menu[1], 1, 1, 0), before);
        up = re.play(InputLog.Event.release(ms += 80, menu[0], menu[1], 1, 0), before);
        add(out, "a press and release at the rail's Menu button are dropped as a node the ghost never clicks, and the screen does not change",
                down == Verdict.DROP_NODE && up == Verdict.DROP_NODE && before.equals(ui.currentScreen));

        // The clock block is dropped the same way.
        Node play = clockButton(ui);
        double[] clockAt = play == null ? new double[]{0, 0} : centre(play);
        down = re.play(InputLog.Event.press(ms += 20, clockAt[0], clockAt[1], 1, 1, 0), before);
        up = re.play(InputLog.Event.release(ms += 80, clockAt[0], clockAt[1], 1, 0), before);
        add(out, "a press and release at the clock's play button are dropped and the clock does not run",
                play != null && down == Verdict.DROP_NODE && up == Verdict.DROP_NODE && !ui.clockRunning);

        // (c) the player was on a game menu
        double[] policy = centre(railCell(ui, "Policy"));
        down = re.play(InputLog.Event.press(ms += 20, policy[0], policy[1], 1, 1, 0), "showSavingMenu");
        up = re.play(InputLog.Event.release(ms += 80, policy[0], policy[1], 1, 0), "showSavingMenu");
        add(out, "with the logged screen a game menu every press and release is dropped and nothing is clicked",
                down == Verdict.DROP_LOGGED_MENU && up == Verdict.DROP_LOGGED_MENU && before.equals(ui.currentScreen));

        // (d) the clock's keys, the window's key and Esc
        int speed = ui.speedIndex;
        boolean running = ui.clockRunning;
        boolean full = ui.replayStage().isFullScreen();
        Verdict space = re.play(key(ms += 10, true, KeyCode.SPACE, " "), before);
        Verdict left = re.play(key(ms += 10, true, KeyCode.LEFT, null), before);
        Verdict right = re.play(key(ms += 10, true, KeyCode.RIGHT, null), before);
        Verdict f11 = re.play(key(ms += 10, true, KeyCode.F11, null), before);
        add(out, "a re-enacted Space, Left, Right and F11 are dropped, and the clock, its speed and the window's full screen do not change",
                space == Verdict.DROP_CLOCK_KEY && left == Verdict.DROP_CLOCK_KEY && right == Verdict.DROP_CLOCK_KEY && f11 == Verdict.DROP_WINDOW_KEY
                        && ui.speedIndex == speed && ui.clockRunning == running && ui.replayStage().isFullScreen() == full);
        boolean opens = ui.escapeOpensMenu();
        Verdict esc = re.play(key(ms += 10, true, KeyCode.ESCAPE, null), before);
        Verdict p = re.play(key(ms += 10, true, KeyCode.P, "p"), before);
        add(out, "Esc and P are dropped while they would open the game menu, and the screen does not change",
                opens && esc == Verdict.DROP_MENU_KEY && p == Verdict.DROP_MENU_KEY && before.equals(ui.currentScreen));

        // Esc that closes the inbox's list is kept and reaches the game's own handler.
        ui.inboxOpen = true;
        boolean opensThen = ui.escapeOpensMenu();
        Verdict closing = re.play(key(ms += 10, true, KeyCode.ESCAPE, null), before);
        add(out, "Esc with the inbox's list down is kept and reaches the game's key handler, which puts the list away",
                !opensThen && closing == Verdict.KEEP && !ui.inboxOpen && before.equals(ui.currentScreen));
        ui.inboxOpen = false;

        // The probes: a pane with enter and exit handlers, a button, a scroll pane and a text field, over the window.
        int[] entered = {0}, exited = {0}, moved = {0}, acted = {0};
        boolean[] firingInside = {false};
        Pane pane = new Pane();
        sized(pane, 120, 120, Pos.CENTER_RIGHT, new Insets(0, 520, 0, 0));
        pane.setStyle("-fx-background-color: #335;");
        pane.setOnMouseEntered(e -> entered[0]++);
        pane.setOnMouseExited(e -> exited[0]++);
        pane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> { moved[0]++; firingInside[0] = re.isFiring(); });
        Button button = new Button("probe");
        sized(button, 100, 40, Pos.BOTTOM_RIGHT, new Insets(0, 520, 100, 0));
        button.setOnAction(e -> acted[0]++);
        Pane content = new Pane();
        content.setPrefSize(180, 2000);
        ScrollPane scroll = new ScrollPane(content);
        sized(scroll, 200, 200, Pos.TOP_RIGHT, new Insets(100, 100, 0, 0));
        TextField field = new TextField();
        sized(field, 160, 30, Pos.TOP_RIGHT, new Insets(400, 100, 0, 0));
        ui.windowStack.getChildren().addAll(pane, button, scroll, field);
        layout(ui);
        try {
            // (e) the pointer enters and leaves a node
            double[] in = centre(pane);
            double[] off = {in[0] - 300, in[1]};
            re.play(InputLog.Event.move(ms += 20, off[0], off[1]), before);
            re.play(InputLog.Event.move(ms += 20, in[0], in[1]), before);
            boolean enteredOnce = entered[0] == 1 && exited[0] == 0 && moved[0] == 1;
            boolean hoverOn = !Reenactor.setsHover() || pane.isHover();
            re.play(InputLog.Event.move(ms += 20, off[0], off[1]), before);
            boolean hoverOff = !Reenactor.setsHover() || !pane.isHover();
            add(out, "a move onto a node with an onMouseEntered handler fires it, and a move off fires its exited handler",
                    enteredOnce && exited[0] == 1 && entered[0] == 1);
            add(out, "the node under the pointer answers isHover() and stops when the pointer leaves (where Node.setHover can be reached)", hoverOn && hoverOff);

            // (g) firing is true inside a fired handler and false after
            add(out, "isFiring() is true inside a fired handler and false after", firingInside[0] && !re.isFiring());

            // a click presses the button and fires its action
            double[] at = centre(button);
            re.play(InputLog.Event.move(ms += 20, at[0], at[1]), before);
            re.play(InputLog.Event.press(ms += 20, at[0], at[1], 1, 1, 0), before);
            boolean armed = button.isArmed();
            re.play(InputLog.Event.release(ms += 60, at[0], at[1], 1, 0), before);
            add(out, "a press and release on a button arm it and fire its action once", armed && acted[0] == 1 && !button.isArmed());

            // (h) a press dragged off a button: the scene keeps telling the node under the pointer enter and exit while the button is down (TR2's review),
            // and a Button's behaviour disarms on exit and arms again on enter while it is pressed, so one released off it does not fire and one back on it does
            double[] away = {at[0] - 150, at[1]};
            int actedBefore = acted[0];
            re.play(InputLog.Event.move(ms += 20, at[0], at[1]), before);
            re.play(InputLog.Event.press(ms += 20, at[0], at[1], 1, 1, 0), before);
            boolean armedOn = button.isArmed();
            re.play(InputLog.Event.drag(ms += 20, away[0], away[1], 1), before);
            boolean armedOff = button.isArmed();
            re.play(InputLog.Event.release(ms += 60, away[0], away[1], 1, 0), before);
            add(out, "a press on a button dragged to a point outside it and released there leaves it disarmed and does not fire its action",
                    armedOn && !armedOff && acted[0] == actedBefore && !button.isArmed());
            re.play(InputLog.Event.move(ms += 20, at[0], at[1]), before);
            re.play(InputLog.Event.press(ms += 20, at[0], at[1], 1, 1, 0), before);
            re.play(InputLog.Event.drag(ms += 20, away[0], away[1], 1), before);
            boolean offAgain = !button.isArmed();
            re.play(InputLog.Event.drag(ms += 20, at[0], at[1], 1), before);
            boolean armedBack = button.isArmed();
            re.play(InputLog.Event.release(ms += 60, at[0], at[1], 1, 0), before);
            add(out, "a press on a button dragged off it and back on and released there arms it again and fires its action once",
                    offAgain && armedBack && acted[0] == actedBefore + 1 && !button.isArmed());

            // (i) a key still down when Follow stops: reset() lets go of it, once (TR2's review)
            int[] released = {0};
            javafx.event.EventHandler<KeyEvent> counting = e -> {
                if (e.getCode() == KeyCode.W) released[0]++;
            };
            ui.replayScene().addEventFilter(KeyEvent.KEY_RELEASED, counting);
            try {
                re.play(key(ms += 10, true, KeyCode.W, "w"), before);
                int beforeReset = released[0];
                re.reset();
                int afterReset = released[0];
                re.reset();
                add(out, "a re-enacted W down and then reset() fires one KEY_RELEASED of W into the scene, and a second reset() fires none",
                        beforeReset == 0 && afterReset == 1 && released[0] == 1);
                released[0] = 0;
                re.play(key(ms += 10, true, KeyCode.W, "w"), before);
                re.play(key(ms += 10, false, KeyCode.W, "w"), before);
                re.reset();
                add(out, "a W let go by its own re-enacted key-up is not released again by reset()", released[0] == 1);
            } finally {
                ui.replayScene().removeEventFilter(KeyEvent.KEY_RELEASED, counting);
            }

            // (f) the wheel moves a scroll pane
            double[] over = centre(scroll);
            double v0 = scroll.getVvalue();
            re.play(InputLog.Event.move(ms += 20, over[0], over[1]), before);
            Verdict wheel = re.play(InputLog.Event.wheel(ms += 20, over[0], over[1], 0, -80, 0), before);
            add(out, "a wheel over a scroll pane moves its vvalue", wheel == Verdict.KEEP && scroll.getVvalue() > v0);

            // a drag on the scroll bar's pane is delivered to the press target
            // keys into a text field are kept, even Space, and the text is typed
            double[] fieldAt = centre(field);
            re.play(InputLog.Event.move(ms += 20, fieldAt[0], fieldAt[1]), before);
            re.play(InputLog.Event.press(ms += 20, fieldAt[0], fieldAt[1], 1, 1, 0), before);
            re.play(InputLog.Event.release(ms += 60, fieldAt[0], fieldAt[1], 1, 0), before);
            boolean focused = ui.replayScene().getFocusOwner() == field;
            boolean wasRunning = ui.clockRunning;
            Verdict x = re.play(key(ms += 10, true, KeyCode.X, "x"), before);
            re.play(key(ms += 10, false, KeyCode.X, "x"), before);
            Verdict spaceTyped = re.play(key(ms += 10, true, KeyCode.SPACE, " "), before);
            re.play(key(ms += 10, false, KeyCode.SPACE, " "), before);
            add(out, "a click on a text field focuses it, and the keys typed there are kept: Space types a space and does not run the clock",
                    focused && x == Verdict.KEEP && spaceTyped == Verdict.KEEP && "x ".equals(field.getText()) && ui.clockRunning == wasRunning);
        } finally {
            ui.windowStack.getChildren().removeAll(pane, button, scroll, field);
            ui.replayScene().getRoot().requestFocus();
            re.reset();
        }
    }

    private static int code(KeyCode k) {
        return InputLog.keyCode(k.getName());
    }

    private static InputLog.Event key(long ms, boolean down, KeyCode k, String text) {
        return InputLog.Event.key(ms, down, code(k), 0, text);
    }

    /** A probe node of a fixed size, set in the window's stack at a corner or an edge. */
    private static void sized(javafx.scene.layout.Region n, double w, double h, Pos at, Insets margin) {
        n.setMinSize(w, h);
        n.setPrefSize(w, h);
        n.setMaxSize(w, h);
        StackPane.setAlignment(n, at);
        StackPane.setMargin(n, margin);
    }

    /** The scene point at the middle of a node. */
    private static double[] centre(Node n) {
        Bounds b = n.localToScene(n.getBoundsInLocal());
        return new double[]{(b.getMinX() + b.getMaxX()) / 2, (b.getMinY() + b.getMaxY()) / 2};
    }

    /** The rail's button with this word under its icon. */
    private static Node railCell(UserInterface ui, String word) {
        Node cell = railCellOrNull(ui, word);
        if (cell == null) throw new IllegalStateException("the rail has no button \"" + word + "\"");
        return cell;
    }

    /** The rail's button with this word under its icon, or null when the rail has none. */
    private static Node railCellOrNull(UserInterface ui, String word) {
        for (Node n : ui.tabRail.getChildren()) {
            if (n instanceof VBox cell) {
                for (Node k : cell.getChildren()) if (k instanceof Label l && word.equals(l.getText())) return cell;
            }
        }
        return null;
    }

    /** The header's play button, the first button in its clock block. */
    private static Node clockButton(UserInterface ui) {
        for (Node n : ui.header.getChildren()) {
            if (Boolean.TRUE.equals(n.getProperties().get(FollowRules.DROP)) && n instanceof javafx.scene.Parent p) {
                for (Node k : p.getChildrenUnmodifiable()) if (k instanceof Button) return k;
            }
        }
        return null;
    }

    /* ---------------------------------- a sitting followed in the window ---------------------------------- */

    /** The rail's words the sitting clicks, in the order tried: the first few the window has, whose clicks open different screens. */
    private static final String[] RAIL_WORDS = {"People", "Policy", "Services", "Bank", "Trade", "Finances", "Sectors"};

    /** How many of the recording's months the sitting logs, one after the other from its fourth. */
    private static final int LOGGED_MONTHS = 4;

    /** How long a month lasts in the sitting's own time, ms. */
    private static final long MONTH_MS = 2_400;

    /** The month of the sitting (counting from 0) in which the player also clicked the rail's Menu. */
    private static final int MENU_MONTH = 1;

    /** The month of the sitting (counting from 0) in which Follow is stopped and joined again. */
    private static final int REJOIN_MONTH = 2;

    /** The frames of the rejoin's first stretch: 990 ms into the month, after the click (560 ms) and its SCREEN marker (760 ms). */
    private static final int REJOIN_STOP_FRAMES = 30;

    /** The frames from the month's start to where the rejoin and the run without a stop are compared: 1,980 ms, after the month's last move (1,800 ms). */
    private static final int REJOIN_END_FRAMES = 60;

    /** One Follow frame, in ms of the player's time (about 30 a second); the window check steps by it. */
    private static final double FRAME_MS = 33;

    /** The most frames one stretch of stepping may take: a Follow that never gets going ends the check rather than hangs it. */
    private static final int MAX_FRAMES = 3_000;

    /** The epoch of the sitting's start in its file's name: any, but one no real sitting has. */
    private static final long SITTING_AT = 1_790_000_000_000L;

    /** How near, in pixels, the ghost's tip has to be to the logged pointer. */
    private static final double GHOST_PX = 2;

    /** A rail tab the sitting clicks: its word, its middle in the scene, and the screen a click on it opens. */
    private record Tab(String word, double[] at, String screen) { }

    /** What one month of the sitting logged, in the sitting's own ms: the click on a tab, its SCREEN marker, the month's last move and the Menu click (-1: none). */
    private record Plan(int month, long start, Tab tab, long pressMs, long releaseMs, long markerMs, long lastMoveMs, double[] lastMove,
                        long menuPressMs, long menuReleaseMs) { }

    /** One frame of Follow in the window: the player's time, what the copy shows, what the log has open and what the ghost does. */
    private record Frame(double t, String screen, String logged, int month, int gameMonth, boolean following, boolean ghost, double[] ghostAt) { }

    /**
     * A sitting followed in the real window, on the FX thread, for the city {@code game}: a copy of the recording's folder (its own
     * input logs left out) gets one synthetic sitting written with the log writer, in which the player clicks the rail's tabs, once
     * the Menu button, and moves the pointer, over {@value #LOGGED_MONTHS} months; the window enters the copy and Follow plays it by
     * frames of {@value #FRAME_MS} ms, as the window's frame would (the replay's own timer cannot step too: this whole call holds the
     * FX thread, and no pulse comes). The findings are {label, "true"/"false"} pairs.
     */
    public static List<String[]> followCheck(Game game, Path recording) {
        List<String[]> out = new ArrayList<>();
        UserInterface ui = new UserInterface(game);
        Stage stage = invisibleStage();
        try {
            startWindow(ui, stage);
            followSitting(game, ui, recording, out);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            add(out, "the window was made and the sitting followed in it without an error", false);
        } finally {
            ui.replayMode().leave();
            stage.hide();
        }
        return out;
    }

    private static void followSitting(Game game, UserInterface ui, Path recording, List<String[]> out) throws Exception {
        ReplayMode mode = ui.replayMode();
        int[] have = Recording.at(recording).months();
        if (have.length < 3 + LOGGED_MONTHS) {
            add(out, "the recording holds " + (3 + LOGGED_MONTHS) + " months or more to log a sitting over (it holds " + have.length + ")", false);
            return;
        }
        int[] logged = Arrays.copyOfRange(have, 3, 3 + LOGGED_MONTHS);

        // The copy of the recording's folder, without the input logs it holds, is what the window enters.
        Path copy = TempDirs.create("followwindow").resolve(recording.getFileName().toString());
        copyWithoutLogs(recording, copy);
        Recording rec = Recording.at(copy);

        // Where the rail's buttons are, and what a click on each opens, measured on the window as it stands.
        List<Tab> tabs = learnTabs(ui, 3);
        double[] menu = centre(railCell(ui, "Menu"));
        add(out, "fixture: the rail has " + tabs.size() + " tabs whose clicks open screens of their own (" + tabs.stream().map(t -> t.word + " " + t.screen).toList()
                + ") and a Menu button the rules know", tabs.size() >= 2 && Reenactor.onDropNode(railCell(ui, "Menu")));
        if (tabs.size() < 2) return;

        // The sitting: the window as it is, then for each month its marker, moves, a click on a tab and the SCREEN marker 200 ms after it.
        Scene scene = ui.replayScene();
        double width = scene.getWidth(), height = scene.getHeight();
        List<InputLog.Event> script = new ArrayList<>();
        List<Plan> plans = new ArrayList<>();
        script.add(InputLog.Event.window(0, width, height));
        for (int i = 0; i < logged.length; i++) {
            long t = i * MONTH_MS;
            Tab tab = tabs.get(i % tabs.size());
            double[] last = {720 + 20 * i, 470 + 10 * i};
            script.add(InputLog.Event.month(t, logged[i]));
            script.add(InputLog.Event.move(t + 100, tab.at[0] + 160, tab.at[1] - 60));
            script.add(InputLog.Event.move(t + 200, tab.at[0] + 60, tab.at[1] - 20));
            script.add(InputLog.Event.move(t + 300, tab.at[0], tab.at[1]));
            script.add(InputLog.Event.press(t + 500, tab.at[0], tab.at[1], 1, 1, 0));
            script.add(InputLog.Event.release(t + 560, tab.at[0], tab.at[1], 1, 0));
            script.add(InputLog.Event.screen(t + 760, tab.screen));
            script.add(InputLog.Event.move(t + 900, 700 + 20 * i, 450));
            long menuPress = -1, menuRelease = -1;
            if (i == MENU_MONTH) {
                menuPress = t + 1_300;
                menuRelease = t + 1_360;
                script.add(InputLog.Event.move(t + 1_200, menu[0], menu[1]));
                script.add(InputLog.Event.press(menuPress, menu[0], menu[1], 1, 1, 0));
                script.add(InputLog.Event.release(menuRelease, menu[0], menu[1], 1, 0));
                script.add(InputLog.Event.move(t + 1_500, 700 + 20 * i, 450));
            }
            script.add(InputLog.Event.move(t + 1_800, last[0], last[1]));
            plans.add(new Plan(logged[i], t, tab, t + 500, t + 560, t + 760, t + 1_800, last, menuPress, menuRelease));
        }
        long endMs = logged.length * MONTH_MS - 400;
        script.add(InputLog.Event.clock(endMs, 1, false));     // the last event: a marker, so the log ends after the last move has been played
        InputLog.Writer log = InputLog.open(copy, rec.id(), SITTING_AT);
        for (InputLog.Event e : script) log.add(e);
        log.close();
        add(out, "fixture: a sitting of " + logged.length + " months and " + script.size() + " events is logged in the copy, window " + width + " x " + height
                + ", and reads back as written", InputLog.read(log.path()).equals(script) && InputLog.sittings(copy).size() == 1);

        Map<String, String> copyBefore = ReplayCheck.hashes(copy);
        Map<String, String> playerBefore = ReplayCheck.hashes(game.getGameFiles().getDirectory());

        mode.enterFolder(copy);
        layout(ui);
        boolean steady = true;
        for (Tab tab : tabs) {
            double[] now = centre(railCell(ui, tab.word));
            steady &= Math.abs(now[0] - tab.at[0]) < 1 && Math.abs(now[1] - tab.at[1]) < 1;
            steady &= within(ui.reenactor().pick(tab.at[0], tab.at[1]), railCell(ui, tab.word));     // the bar lies over nothing the clicks aim at
        }
        steady &= Math.abs(centre(railCell(ui, "Menu"))[1] - menu[1]) < 1;
        steady &= Reenactor.onDropNode(ui.reenactor().pick(menu[0], menu[1])) && within(ui.reenactor().pick(menu[0], menu[1]), railCell(ui, "Menu"));
        InputTimeline tl = mode.timeline();
        add(out, "fixture: the replay entered the copy, read the one sitting, and left the rail's buttons where they were measured, with the Reenactor's pick on each of them",
                tl != null && tl.segments().size() == 1 && tl.startOf(logged[0]) != null && tl.startOf(logged[LOGGED_MONTHS - 1]) != null && steady);

        // ---- the whole sitting, from the first logged month
        mode.showMonth(logged[0]);
        mode.setFollow(true);
        String startScreen = ui.currentScreen;
        boolean on = mode.following();
        List<Frame> all = new ArrayList<>();
        double ended = stepTo(ui, mode, 0, Double.MAX_VALUE, all);
        System.out.println(String.format("      Follow in the window: %d frames of %.0f ms to %.0f ms of the sitting, which began on %s", all.size(), FRAME_MS, ended, startScreen));
        add(out, "Follow starts at once (the window is already the logged size, so no async fit) and plays the sitting to its end, where it stops itself",
                on && all.size() > 10 && !all.get(all.size() - 1).following && ended >= endMs && ended < endMs + 2 * FRAME_MS);

        List<Integer> seen = new ArrayList<>();
        boolean atMarkers = true;
        for (int i = 0; i < plans.size(); i++) {
            Frame after = firstAtOrAfter(all, i == 0 ? FRAME_MS : plans.get(i).start);
            Frame before = i == 0 ? null : lastBefore(all, plans.get(i).start);
            seen.add(after == null ? -1 : after.month);
            atMarkers &= after != null && after.month == logged[i] && after.gameMonth == logged[i]
                    && (i == 0 || (before != null && before.month == logged[i - 1] && before.gameMonth == logged[i - 1]));
        }
        add(out, "the copy is at each logged month as its MONTH marker passes, and at the month before until it does (" + seen + " for " + Arrays.toString(logged) + ")", atMarkers);

        StringBuilder clicked = new StringBuilder();
        boolean clicks = true;
        for (Plan p : plans) {
            Frame before = lastBefore(all, p.pressMs);
            Frame after = firstAtOrAfter(all, p.releaseMs);
            Frame marked = firstAtOrAfter(all, p.markerMs);
            boolean ok = before != null && after != null && marked != null && after.t < p.markerMs
                    && after.screen.equals(p.tab.screen) && !before.screen.equals(p.tab.screen) && !after.logged.equals(p.tab.screen)
                    && marked.logged.equals(p.tab.screen) && marked.screen.equals(p.tab.screen);
            clicked.append(' ').append(p.tab.word).append(ok ? "" : "(!)");
            clicks &= ok;
        }
        add(out, "each rail click itself took the copy to its tab's screen in the frame after it, before its SCREEN marker came (the log then had the screen before it open):" + clicked, clicks);

        List<String> shown = new ArrayList<>(), wanted = new ArrayList<>(), loggedSeq = new ArrayList<>(), wantedLogged = new ArrayList<>();
        shown.add(startScreen);
        wanted.add(startScreen);
        for (Frame f : all) {
            shown.add(f.screen);
            if (!f.logged.isEmpty()) loggedSeq.add(f.logged);
        }
        for (Plan p : plans) {
            wanted.add(p.tab.screen);
            wantedLogged.add(p.tab.screen);
        }
        add(out, "the screens arrive in the logged order, none between them (" + collapsed(shown) + ")", collapsed(shown).equals(collapsed(wanted)) && collapsed(loggedSeq).equals(collapsed(wantedLogged)));

        // The Menu click is dropped; the copy is never on a game menu, and Follow carries on through every press.
        Plan menuPlan = plans.get(MENU_MONTH);
        boolean menuStays = true, anyMenu = false, stillOn = true, atMenu = false;
        for (Frame f : all) {
            anyMenu |= UserInterface.isGameMenu(f.screen);
            if (f.t >= menuPlan.menuPressMs - FRAME_MS && f.t < menuPlan.start + 1_700) {
                menuStays &= f.screen.equals(menuPlan.tab.screen);
                stillOn &= f.following;
                if (f.t >= menuPlan.menuPressMs && f.ghostAt != null && near(f.ghostAt, menu[0], menu[1])) atMenu = true;
            }
        }
        add(out, "the press and release at the rail's Menu button were played (the ghost went there) and dropped: the copy stayed on " + menuPlan.tab.screen
                + ", never on a game menu, and Follow was still on", atMenu && menuStays && !anyMenu && stillOn);

        boolean pressesKeep = true;
        for (Frame f : all) if (f.t < endMs) pressesKeep &= f.following;
        add(out, "a re-enacted press never ends Follow: it was on in every frame up to the sitting's last event, through " + (plans.size() + 1) + " clicks", pressesKeep && !all.isEmpty());

        StringBuilder ghosts = new StringBuilder();
        boolean ghostRight = true;
        for (Plan p : plans) {
            Frame f = firstAtOrAfter(all, p.lastMoveMs);
            boolean ok = f != null && f.screen.equals(f.logged) && f.ghost && f.ghostAt != null && near(f.ghostAt, p.lastMove[0], p.lastMove[1]);
            ghosts.append(' ').append(f == null || f.ghostAt == null ? "none" : Math.round(f.ghostAt[0]) + "," + Math.round(f.ghostAt[1]));
            ghostRight &= ok;
        }
        add(out, "the ghost shows while the copy is on the logged screen, and sits at the logged pointer (within " + (int) GHOST_PX + " px) at each month's last move:" + ghosts, ghostRight);

        // ---- a rejoin in the middle of a month, against the run without a stop
        double monthStart = REJOIN_MONTH * MONTH_MS;
        double joinEnd = monthStart + REJOIN_END_FRAMES * FRAME_MS;
        mode.showMonth(logged[REJOIN_MONTH]);
        mode.setFollow(true);
        List<Frame> straight = new ArrayList<>();
        double at = stepTo(ui, mode, monthStart, joinEnd, straight);
        Frame straightAt = straight.isEmpty() ? null : straight.get(straight.size() - 1);
        stepTo(ui, mode, at, Double.MAX_VALUE, new ArrayList<>());      // to the end of the log, so that the next Follow begins at a month's start
        mode.showMonth(logged[REJOIN_MONTH]);
        mode.setFollow(true);
        List<Frame> first = new ArrayList<>(), second = new ArrayList<>();
        double stopAt = stepTo(ui, mode, monthStart, monthStart + REJOIN_STOP_FRAMES * FRAME_MS, first);
        mode.setFollow(false);
        boolean stoppedInMonth = !mode.following() && mode.shownMonth() == logged[REJOIN_MONTH];
        mode.setFollow(true);
        boolean rejoined = mode.following();
        double rejoinedEnd = stepTo(ui, mode, stopAt, joinEnd, second);
        Frame joinedAt = second.isEmpty() ? null : second.get(second.size() - 1);
        Tab rejoinTab = plans.get(REJOIN_MONTH).tab;
        boolean same = straightAt != null && joinedAt != null && straightAt.following && joinedAt.following
                && joinedAt.screen.equals(straightAt.screen) && joinedAt.logged.equals(straightAt.logged) && joinedAt.month == straightAt.month
                && joinedAt.gameMonth == straightAt.gameMonth && joinedAt.ghost == straightAt.ghost
                && (!straightAt.ghost || near(joinedAt.ghostAt, straightAt.ghostAt[0], straightAt.ghostAt[1]));
        add(out, "stopped at " + Math.round(stopAt - monthStart) + " ms into a month and joined again, Follow reaches " + Math.round(rejoinedEnd - monthStart)
                        + " ms on the screen and the month that a run from the month's start reaches (" + (joinedAt == null ? "?" : joinedAt.screen + ", month " + joinedAt.month) + ")",
                stoppedInMonth && rejoined && same && Math.abs(rejoinedEnd - at) < 1e-6 && joinedAt.screen.equals(rejoinTab.screen) && joinedAt.month == logged[REJOIN_MONTH]);

        // ---- the viewer's own mouse
        Pane probe = new Pane();
        sized(probe, 200, 200, Pos.CENTER_RIGHT, new Insets(0, 520, 0, 0));
        ui.windowStack.getChildren().add(probe);
        layout(ui);
        double[] c = centre(probe);
        mode.setFollow(false);
        long begun = System.nanoTime();
        mode.setFollow(true);
        boolean live = mode.following();
        fireMouse(ui, probe, MouseEvent.MOUSE_MOVED, c[0] - 60, c[1]);
        fireMouse(ui, probe, MouseEvent.MOUSE_MOVED, c[0] - 20, c[1]);
        long inGraceMs = (System.nanoTime() - begun) / 1_000_000L;
        boolean graceHeld = mode.following();
        Thread.sleep(ReplayMode.VIEWER_GRACE_MS + 200);
        fireMouse(ui, probe, MouseEvent.MOUSE_MOVED, c[0] - 17, c[1]);
        boolean jitterHeld = mode.following();
        fireMouse(ui, probe, MouseEvent.MOUSE_MOVED, c[0] + 30, c[1]);
        boolean moveEnded = !mode.following();
        add(out, "a viewer's move of more than " + (int) ReplayMode.VIEWER_MOVE_PX + " px inside the " + ReplayMode.VIEWER_GRACE_MS + " ms grace does not end Follow (it had been on "
                        + inGraceMs + " ms), nor one of 3 px after it, and one of 47 px after it does",
                live && inGraceMs < ReplayMode.VIEWER_GRACE_MS && graceHeld && jitterHeld && moveEnded);

        mode.setFollow(true);
        boolean onAgain = mode.following();
        fireMouse(ui, probe, MouseEvent.MOUSE_PRESSED, c[0], c[1]);
        add(out, "a viewer's real press on the window, away from the bar, ends Follow (a press the Reenactor fires does not: see above)", onAgain && !mode.following());
        ui.windowStack.getChildren().remove(probe);

        // ---- out again
        mode.leave();
        boolean back = ui.game == game && !mode.following();
        String hurtCopy = ReplayCheck.firstDifference(copyBefore, ReplayCheck.hashes(copy));
        String hurtPlayer = ReplayCheck.firstDifference(playerBefore, ReplayCheck.hashes(game.getGameFiles().getDirectory()));
        add(out, "leaving the replay gives the window its own city back, and the copy of the recording and the game's save folder are byte for byte as they were"
                + (hurtCopy == null ? "" : " - " + hurtCopy) + (hurtPlayer == null ? "" : " - " + hurtPlayer), back && hurtCopy == null && hurtPlayer == null);
    }

    /** The recording's folder copied to {@code to}, its input logs left out. */
    private static void copyWithoutLogs(Path from, Path to) throws IOException {
        try (var walk = Files.walk(from)) {
            for (Path p : (Iterable<Path>) walk::iterator) {
                Path target = to.resolve(from.relativize(p).toString());
                if (Files.isDirectory(p)) {
                    Files.createDirectories(target);
                    continue;
                }
                String name = p.getFileName().toString();
                if (name.startsWith(Recording.INPUT_PREFIX) && name.endsWith(Recording.INPUT_SUFFIX)) continue;
                Files.createDirectories(target.getParent());
                Files.copy(p, target);
            }
        }
    }

    /** The first rail tabs the window has, in {@link #RAIL_WORDS}' order, that a click opens a screen of its own from, found by clicking each through the Reenactor. */
    private static List<Tab> learnTabs(UserInterface ui, int want) {
        Reenactor re = ui.reenactor();
        List<Tab> found = new ArrayList<>();
        long ms = 0;
        for (String word : RAIL_WORDS) {
            if (found.size() >= want) break;
            Node cell = railCellOrNull(ui, word);
            if (cell == null) continue;
            double[] at = centre(cell);
            String here = ui.currentScreen;
            re.play(InputLog.Event.move(ms += 20, at[0], at[1]), here);
            re.play(InputLog.Event.press(ms += 20, at[0], at[1], 1, 1, 0), here);
            re.play(InputLog.Event.release(ms += 60, at[0], at[1], 1, 0), here);
            layout(ui);
            String screen = ui.currentScreen;
            if (!UserInterface.isGameMenu(screen) && !screen.equals(here) && found.stream().noneMatch(t -> t.screen.equals(screen))) found.add(new Tab(word, at, screen));
        }
        re.reset();
        return found;
    }

    /**
     * Follow stepped by frames from the player's time {@code from} while it is on and the next frame ends by {@code to}: the window
     * stepped by the frame, then laid out as a pulse would, each frame kept. Returns the time reached.
     */
    private static double stepTo(UserInterface ui, ReplayMode mode, double from, double to, List<Frame> out) {
        double t = from;
        for (int n = 0; n < MAX_FRAMES && mode.following() && t + FRAME_MS <= to + 1e-9; n++) {
            mode.followStep(FRAME_MS);
            t += FRAME_MS;
            layout(ui);
            out.add(new Frame(t, ui.currentScreen == null ? "" : ui.currentScreen, mode.loggedScreen(), mode.shownMonth(), ui.game.getMonth(), mode.following(),
                    mode.ghostShown(), mode.ghostAt()));
        }
        return t;
    }

    /** The first frame at or after the time, or null. */
    private static Frame firstAtOrAfter(List<Frame> frames, double t) {
        for (Frame f : frames) if (f.t >= t - 1e-9) return f;
        return null;
    }

    /** The last frame before the time, or null. */
    private static Frame lastBefore(List<Frame> frames, double t) {
        Frame found = null;
        for (Frame f : frames) {
            if (f.t < t - 1e-9) found = f; else break;
        }
        return found;
    }

    /** The list with each run of the same word as one. */
    private static List<String> collapsed(List<String> words) {
        List<String> out = new ArrayList<>();
        for (String w : words) if (out.isEmpty() || !out.get(out.size() - 1).equals(w)) out.add(w);
        return out;
    }

    /** Whether the node is the ancestor or a node inside it. */
    private static boolean within(Node n, Node ancestor) {
        for (Node p = n; p != null; p = p.getParent()) if (p == ancestor) return true;
        return false;
    }

    private static boolean near(double[] at, double x, double y) {
        return at != null && Math.hypot(at[0] - x, at[1] - y) <= GHOST_PX;
    }

    /** A real mouse event of the viewer's, not the Reenactor's (it is not counted as firing), sent to the node through the scene. */
    private static void fireMouse(UserInterface ui, Node target, javafx.event.EventType<MouseEvent> type, double x, double y) {
        boolean pressed = type == MouseEvent.MOUSE_PRESSED;
        MouseEvent e = new MouseEvent(ui.replayScene(), target, type, x, y, x, y, pressed ? MouseButton.PRIMARY : MouseButton.NONE, pressed ? 1 : 0,
                false, false, false, false, pressed, false, false, false, false, true, new PickResult(target, x, y));
        javafx.event.Event.fireEvent(target, e);
    }
}
