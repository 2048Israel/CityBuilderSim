package ham.citybuildersim.ui;

import ham.citybuildersim.BuildAdvice;
import ham.citybuildersim.BuildingType;
import ham.citybuildersim.CityNeeds;
import ham.citybuildersim.Founding;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LongPlaytest;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * PT1's renders and one measurement (a scratch class, not in the build; docs/queue/PT1/).
 * Run twice, once on the classes as they were (a tag "before") and once as they are ("after"):
 *   java -cp [target/render-before;]target/classes;<classpath.txt>;target/render-pt1 ham.citybuildersim.ui.RenderPT1 <tag> <outDir> [toastClearance]
 * It writes founding-cards, people-bridge, drawer-summary and (after only) build-overview PNGs, and prints
 * where the Build overview's "Turn automatic building off" button sits against the toast's corner.
 */
public final class RenderPT1 {

    static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    static Object field(Object o, String name) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }

    static void png(WritableImage img, Path to) throws Exception {
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        int[] px = new int[w * h];
        img.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), px, 0, w);
        BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        b.setRGB(0, 0, w, h, px, 0, w);
        javax.imageio.ImageIO.write(b, "png", to.toFile());
        System.out.println("  wrote " + to + " (" + w + "x" + h + ")");
    }

    static List<Node> walk(Node n, List<Node> out) {
        out.add(n);
        if (n instanceof Parent p) for (Node k : p.getChildrenUnmodifiable()) walk(k, out);
        if (n instanceof ScrollPane sp && sp.getContent() != null) walk(sp.getContent(), out);
        return out;
    }

    static Labeled findText(Node root, String starts) {
        for (Node n : walk(root, new ArrayList<>())) {
            if (n instanceof Labeled l && l.getText() != null && l.getText().startsWith(starts)) return l;
        }
        return null;
    }

    static void settle(Scene sc, Parent root) {
        root.applyCss();
        root.layout();
        sc.getRoot().applyCss();
        sc.getRoot().layout();
    }

    public static void main(String[] args) throws Exception {
        String tag = args[0];
        Path out = Path.of(args[1]);
        double clearance = args.length > 2 ? Double.parseDouble(args[2]) : 0;
        Files.createDirectories(out);
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        up.await(60, TimeUnit.SECONDS);

        GameFiles files = GameFiles.scratch("renderpt1");
        Game g = fx(() -> LongPlaytest.probeCity(files, null));
        fx(() -> { LongPlaytest.probePlay(g, 40); return null; });
        System.out.println("city at month " + g.getMonth());
        KeptBench bench = fx(() -> new KeptBench(g));
        UserInterface ui = (UserInterface) field(bench, "ui");
        VBox column = (VBox) field(bench, "column");
        Scene benchScene = (Scene) field(bench, "scene");

        /* A: the four starts' cards, at the Earth page's left column (520) and the plain page's (680). */
        fx(() -> {
            FoundingScreen fs = ui.foundingScreen;
            setField(fs, "preset", Founding.Preset.STANDARD);
            setField(fs, "cityName", Founding.DEFAULT_CITY_NAME);
            Method m = FoundingScreen.class.getDeclaredMethod("presetCard", Founding.Preset.class);
            m.setAccessible(true);
            for (double width : new double[]{FoundingScreen.LEFT, FoundingScreen.PANEL - 2 * FoundingScreen.PANEL_PAD}) {
                HBox cards = new HBox(10);
                for (Founding.Preset p : Founding.Preset.values()) {
                    if (p != Founding.Preset.CUSTOM) cards.getChildren().add((Node) m.invoke(fs, p));
                }
                cards.setPadding(new Insets(14));
                cards.setStyle("-fx-background-color: " + Palette.STAGE + ";");
                Scene sc = new Scene(cards, width + 28, 190);
                ui.applyTheme(sc);
                settle(sc, cards);
                for (Node n : walk(cards, new ArrayList<>())) {
                    if (n instanceof Labeled l && ("Standard".equals(l.getText()) || "default".equals(l.getText()))) {
                        boolean cut = l.getWidth() < l.prefWidth(-1) - 0.5;
                        System.out.printf("  width %.0f: \"%s\" laid %.1f wide, wants %.1f%s%n", width, l.getText(),
                                l.getWidth(), l.prefWidth(-1), cut ? "  CUT" : "");
                    }
                }
                png(cards.snapshot(null, null), out.resolve("founding-cards-" + (int) width + "-" + tag + ".png"));
            }
            return null;
        });

        /* B: the People page's bridge card. */
        fx(() -> {
            bench.drawKept();
            bench.passes();
            Labeled box = findText(column, "homes for");
            if (box == null) box = findText(column, "room for");
            if (box == null) { System.out.println("  no bridge box (the card before the first month?)"); return null; }
            Node card = box;
            while (card.getParent() != null && card.getLayoutBounds().getWidth() < 1000) card = card.getParent();
            System.out.println("  bridge box: \"" + box.getText() + "\"");
            png(card.snapshot(null, null), out.resolve("people-bridge-" + tag + ".png"));
            return null;
        });

        /* C: the overview drawer in Summary. */
        fx(() -> {
            bench.cityMode(false, false);
            bench.drawer(true, false);
            bench.drawPanelsKept();
            bench.panelPasses();
            png(ui.cityPanel.snapshot(null, null), out.resolve("drawer-summary-" + tag + ".png"));
            return null;
        });

        /* D: the Build overview under a toast. */
        for (double h : new double[]{760, 820, 880}) {
            final double height = h;
            fx(() -> {
                BuildScreen bs = ui.buildScreen;
                VBox root = new VBox(10);
                root.setAlignment(Pos.TOP_CENTER);
                root.setPadding(new Insets(8, 0, 14 + clearance, 0));
                root.setStyle("-fx-background-color: " + Palette.STAGE + ";");
                ScrollPane sp = new ScrollPane(root);
                sp.setFitToWidth(true);
                sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                root.minHeightProperty().bind(sp.heightProperty().subtract(4));
                ui.menuScroller = sp;
                ui.rootMenu = root;
                List<CityNeeds.Need> all = CityNeeds.measure(g, SummaryScreen.WORDS);
                List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(g, all);
                VBox page = Pieces.widePage();
                HBox head = bs.buildHead(BuildAdvice.OVERVIEW, EnumSet.noneOf(BuildingType.class));
                head.setStyle("-fx-padding: 0 0 4 0;");
                page.getChildren().addAll(head, bs.overviewLead(),
                        Pieces.sectionHead("THE CITY'S JOB", Pieces.hint("tap one to build for it")),
                        bs.cityJob(all),
                        Pieces.sectionHead("WHAT WOULD HELP MOST", bs.adviceTotal(advice)),
                        bs.adviceRow(advice),
                        Pieces.sectionHead("AUTOMATIC BUILDING", BuildScreen.AUTO_INFO, Pieces.hint(BuildScreen.autoHint(g.getAutoBuilder()))),
                        bs.autoRow());
                page.getChildren().addAll(bs.autoAsks());
                page.getChildren().addAll(Pieces.sectionHead("THE MARKET BUILDS THESE", Pieces.hint("investors decide; you can add")),
                        bs.marketRow());
                root.getChildren().add(page);

                // The toast, as UserInterface.toast() styles it, in the stage's bottom right corner (margin 0 24 16 0).
                Label toast = new Label("[!]  Your businesses have nowhere to build");
                toast.setWrapText(true);
                toast.setMaxWidth(UserInterface.TOAST_WIDTH);
                toast.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold;"
                        + " -fx-text-fill: " + Palette.BAD_TEXT + "; -fx-background-color: " + Palette.ALERT_GROUND_LOUD + ";"
                        + " -fx-background-radius: 4; -fx-border-color: " + Palette.ALERT_EDGE + ";"
                        + " -fx-border-radius: 4; -fx-padding: 7 10 7 10;");
                VBox stack = new VBox(6, toast);
                stack.setAlignment(Pos.BOTTOM_RIGHT);
                stack.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
                stack.setPickOnBounds(false);
                StackPane.setAlignment(stack, Pos.BOTTOM_RIGHT);
                StackPane.setMargin(stack, new Insets(0, 24, 16, 0));
                StackPane stage = new StackPane(sp, stack);
                stage.setStyle("-fx-background-color: " + Palette.STAGE + ";");
                Scene sc = new Scene(stage, 1460, height);
                ui.applyTheme(sc);
                // The scroll offset that puts the button's middle at the toast's middle, found from the page at the top.
                sp.setVvalue(0);
                settle(sc, stage);
                Labeled first = findText(root, "Turn automatic building");
                Bounds b0 = first.localToScene(first.getBoundsInLocal());
                Bounds t0 = toast.localToScene(toast.getBoundsInLocal());
                double offset = (b0.getMinY() + b0.getMaxY()) / 2 - (t0.getMinY() + t0.getMaxY()) / 2;
                double scroll = root.getHeight() - sp.getViewportBounds().getHeight();
                double cover = scroll > 0 ? Math.max(0, Math.min(1, offset / scroll)) : 0;
                for (double v : new double[]{0, cover, 1}) {
                    sp.setVvalue(v);
                    settle(sc, stage);
                    Labeled button = findText(root, "Turn automatic building");
                    Bounds b = button == null ? null : button.localToScene(button.getBoundsInLocal());
                    Bounds t = toast.localToScene(toast.getBoundsInLocal());
                    double content = root.getHeight();
                    System.out.printf("  window %.0f, clearance %.0f, vvalue %.2f: page %.0f tall in a %.0f viewport (scrolls %.0f); "
                                    + "button x %.0f-%.0f y %.0f-%.0f; toast x %.0f-%.0f y %.0f-%.0f -> %s%n",
                            height, clearance, v, content, sp.getViewportBounds().getHeight(),
                            Math.max(0, content - sp.getViewportBounds().getHeight()),
                            b == null ? -1 : b.getMinX(), b == null ? -1 : b.getMaxX(), b == null ? -1 : b.getMinY(), b == null ? -1 : b.getMaxY(),
                            t.getMinX(), t.getMaxX(), t.getMinY(), t.getMaxY(),
                            b == null ? "no button" : b.intersects(t) ? "COVERED" : "clear");
                    if (height == 820) png(stage.snapshot(null, null), out.resolve("build-overview-" + tag + "-v" + (int) Math.round(v * 100) + ".png"));
                }
                return null;
            });
        }

        /* E: Build > Shops, landed on the Commercial Bank's card (openOnBuilding's bringToTop), laid out as showMarketCategory does. */
        fx(() -> {
            BuildScreen bs = ui.buildScreen;
            BuildAdvice.Category c = BuildAdvice.category(BuildAdvice.SHOPS);
            VBox root = new VBox(10);
            root.setAlignment(Pos.TOP_CENTER);
            root.setPadding(new Insets(8, 0, 14, 0));
            root.setStyle("-fx-background-color: " + Palette.STAGE + ";");
            ScrollPane sp = new ScrollPane(root);
            sp.setFitToWidth(true);
            sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            root.minHeightProperty().bind(sp.heightProperty().subtract(4));
            ui.menuScroller = sp;
            ui.rootMenu = root;
            setField(bs, "landOn", "Commercial Bank");
            bs.pageCards.clear();
            setField(bs, "orderMeasure", null);
            setField(bs, "orderMarket", true);
            VBox page = Pieces.widePage();
            HBox head = bs.buildHead(c.name(), c.types());
            javafx.scene.layout.FlowPane strip = bs.buildStrip(c.name());
            page.getChildren().addAll(head, strip, bs.constraintsBar(), bs.whoBuildsThis(c.name(), c.types()));
            setField(bs, "orderBar", new VBox());
            for (ham.citybuildersim.BuildCard.Group group : ham.citybuildersim.BuildCard.groups(g, c.name(), null)) {
                page.getChildren().add(bs.groupHead(group));
                javafx.scene.layout.FlowPane cards = new javafx.scene.layout.FlowPane(10, 10);
                cards.setAlignment(Pos.TOP_LEFT);
                cards.prefWrapLengthProperty().bind(sp.widthProperty().subtract(60));
                for (ham.citybuildersim.BuildCard.Figures f : group.cards()) cards.getChildren().add(bs.card(f, group, c, null));
                page.getChildren().add(cards);
            }
            root.getChildren().add(page);
            StackPane stage = new StackPane(sp);
            Scene sc = new Scene(stage, 1460, 820);
            ui.applyTheme(sc);
            settle(sc, stage);
            Node card = (Node) field(bs, "landCard");
            setField(bs, "landOn", null);
            System.out.println("  Shops page: " + page.getHeight() + " tall; the Commercial Bank's card " + (card == null ? "NOT FOUND" : "found"));
            if (card != null) {
                Bounds before = card.localToScene(card.getBoundsInLocal());
                Method m = BuildScreen.class.getDeclaredMethod("bringToTop", Node.class);
                m.setAccessible(true);
                m.invoke(bs, card);
                settle(sc, stage);
                Bounds after = card.localToScene(card.getBoundsInLocal());
                System.out.printf("  at the top of the page the card sits at y %.0f-%.0f; after bringToTop at y %.0f-%.0f (vvalue %.2f) in a %.0f viewport%n",
                        before.getMinY(), before.getMaxY(), after.getMinY(), after.getMaxY(), sp.getVvalue(), sp.getViewportBounds().getHeight());
                png(stage.snapshot(null, null), out.resolve("build-shops-bank-" + tag + ".png"));
            }
            return null;
        });
        System.exit(0);
    }

    static void setField(Object o, String name, Object value) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(o, value);
    }
}
