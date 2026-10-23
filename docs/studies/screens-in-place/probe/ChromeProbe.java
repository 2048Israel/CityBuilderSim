package ham.citybuildersim.ui;

import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.UiGrowProbe;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Scratch probe for the study UP4, never in src/: the chrome's redraw part by part - the city panel,
 * the construction panel, the header, the rail and the inbox, each rebuilt alone and its CSS and
 * layout timed - with the page on show and with the page taken off, so a part whose rebuild restyles
 * the page under it shows as the difference.
 */
public final class ChromeProbe {

    public static void main(String[] args) throws Exception {
        int month = args.length > 0 ? Integer.parseInt(args[0]) : 4000;
        int n = args.length > 1 ? Integer.parseInt(args[1]) : 12;
        java.io.PrintStream out = System.out;
        System.setOut(UiGrowProbe.QUIET);
        InPlaceProbe.out = out;
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        up.await();
        GameFiles files = GameFiles.scratch("chrome");
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);
        InPlaceProbe.g = InPlaceProbe.fx(() -> UiGrowProbe.found(files));
        InPlaceProbe.fx(() -> {
            InPlaceProbe.ui = new UserInterface(InPlaceProbe.g);
            InPlaceProbe.stage = new Stage();
            InPlaceProbe.ui.start(InPlaceProbe.stage);
            InPlaceProbe.stage.setFullScreen(false);
            InPlaceProbe.stage.setMaximized(false);
            InPlaceProbe.stage.setWidth(1600);
            InPlaceProbe.stage.setHeight(900);
            InPlaceProbe.call("anotherCity");
            InPlaceProbe.call("openCity");
            InPlaceProbe.sc = (Scene) InPlaceProbe.f("scene").get(InPlaceProbe.ui);
            return null;
        });
        UserInterface ui = InPlaceProbe.ui;
        Scene sc = InPlaceProbe.sc;
        InPlaceProbe.fx(() -> { UiGrowProbe.play(InPlaceProbe.g, month); return null; });
        out.printf("month %d%n", InPlaceProbe.g.getMonth());

        Map<String, Runnable> parts = new LinkedHashMap<>();
        parts.put("city panel", () -> ui.summaryScreen.refreshCityPanel());
        parts.put("construction", () -> run("refreshConstructionPanel"));
        parts.put("header", () -> run("refreshHeader"));
        parts.put("rail", () -> run("refreshTabRail"));
        parts.put("inbox", () -> run("refreshInbox"));

        for (String screen : List.of("People", "Build", "Policy")) {
            Runnable show = switch (screen) {
                case "People" -> ui.peopleScreen::showPopulationInfoMenu;
                case "Build" -> ui.buildScreen::showBuildMenu;
                default -> ui.policyScreen::showPolicyMenu;
            };
            for (int mode = 0; mode < 3; mode++) {
                final boolean withPage = mode != 1, parked = mode == 2;
                Object r = InPlaceProbe.fx(() -> {
                    show.run();
                    InPlaceProbe.pulse();
                    // Mode 2: the focus parked on a node no redraw takes away (the page's own box), so it never moves.
                    if (parked) {
                        ui.rootMenu.setFocusTraversable(true);
                        ui.rootMenu.requestFocus();
                        InPlaceProbe.pulse();
                    }
                    String focusBefore = describe(sc.getFocusOwner());
                    List<Node> page = new ArrayList<>(ui.rootMenu.getChildren());
                    if (!withPage) {
                        ui.rootMenu.getChildren().clear();
                        InPlaceProbe.pulse();
                    }
                    int all = TreePatch.countAll(sc.getRoot());
                    int pageNodes = 0;
                    for (Node x : ui.rootMenu.getChildren()) pageNodes += TreePatch.countAll(x);
                    StringBuilder b = new StringBuilder(String.format("%-7s %-9s scene %5d nodes, page %5d:", screen,
                            parked ? "parked" : withPage ? "page on" : "page off", all, pageNodes));
                    for (var e : parts.entrySet()) {
                        List<Double> bu = new ArrayList<>(), cs = new ArrayList<>(), la = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            InPlaceProbe.g.toggleNextMonth();
                            long a0 = System.nanoTime();
                            e.getValue().run();
                            long a1 = System.nanoTime();
                            double[] pp = InPlaceProbe.pulse();
                            bu.add((a1 - a0) / 1e6); cs.add(pp[0]); la.add(pp[1]);
                        }
                        b.append(String.format("  %s %.1f/%.1f/%.1f", e.getKey(), InPlaceProbe.median(bu),
                                InPlaceProbe.median(cs), InPlaceProbe.median(la)));
                    }
                    if (!withPage) ui.rootMenu.getChildren().setAll(page);
                    b.append("\n          focus before: ").append(focusBefore).append(", after: ").append(describe(sc.getFocusOwner()));
                    if (parked) ui.rootMenu.setFocusTraversable(false);
                    return b.toString();
                });
                out.println(r);
            }
        }
        out.println("(each part: build / CSS / layout, ms, median of " + n + ")");
        Platform.exit();
        System.exit(0);
    }

    static String describe(Node n) {
        if (n == null) return "none";
        String t = n instanceof javafx.scene.control.Labeled l ? " \"" + l.getText() + "\"" : "";
        return n.getClass().getSimpleName() + t + (n.getScene() == null ? " (off the scene)" : "");
    }

    static void run(String name) {
        try {
            InPlaceProbe.call(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
