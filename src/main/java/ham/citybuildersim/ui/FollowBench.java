package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.InputLog;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.ui.FollowRules.Verdict;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an
 * undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged events played into it, with
 * what each one did read back. {@link #check} gives the findings as {label, "true"/"false"} pairs for a harness to print, and
 * must be called on the FX thread; {@link #main} makes a small city of its own in a scratch folder and prints them.
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

    public static void main(String[] args) throws Exception {
        long t0 = System.nanoTime();
        System.out.println("=== FollowBench: logged input re-enacted in the window (TR2) ===");
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("skipped: this JVM is headless (no display), and the window needs one");
            return;
        }
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        if (!up.await(60, TimeUnit.SECONDS)) {
            System.out.println("skipped: the JavaFX toolkit did not come up in 60 s");
            return;
        }
        GameFiles files = GameFiles.scratch("followbench");
        Game game = fx(() -> LongPlaytest.probeCity(files, null));
        System.out.println("a city of month " + game.getMonth() + " on the scratch folder " + files.getDirectory());
        List<String[]> found = fx(() -> check(game));
        for (String[] f : found) {
            boolean ok = "true".equals(f[1]);
            if (!ok) failures++;
            System.out.println((ok ? "  ok   " : "  FAIL ") + f[0]);
        }
        System.out.printf("Node.setHover by reflection: %s; Node.setPressed: %s%n", Reenactor.setsHover() ? "yes" : "no", Reenactor.setsPressed() ? "yes" : "no");
        System.out.printf("%d findings, %d failed, %.1f s%n", found.size(), failures, (System.nanoTime() - t0) / 1e9);
        System.exit(failures);
    }

    private static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    /** The findings, on the FX thread, for the city {@code game}: the window is built on it and hidden again at the end. */
    public static List<String[]> check(Game game) {
        List<String[]> out = new ArrayList<>();
        UserInterface ui = new UserInterface(game);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOpacity(0);
        stage.setX(-5000);
        stage.setY(0);
        try {
            ui.start(stage);
            stage.setFullScreen(false);
            stage.setMaximized(false);
            stage.setX(-5000);
            ui.replayAnotherCity();
            ui.replayOpenCity();
            layout(ui);
            run(ui, out);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            out.add(new String[]{"the window was made and played into without an error", "false"});
        } finally {
            stage.hide();
        }
        return out;
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
        for (Node n : ui.tabRail.getChildren()) {
            if (n instanceof VBox cell) {
                for (Node k : cell.getChildren()) if (k instanceof Label l && word.equals(l.getText())) return cell;
            }
        }
        throw new IllegalStateException("the rail has no button \"" + word + "\"");
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
}
