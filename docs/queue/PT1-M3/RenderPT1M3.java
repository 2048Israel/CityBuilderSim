package ham.citybuildersim.ui;

import ham.citybuildersim.BuildAdvice;
import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.CityNeeds;
import ham.citybuildersim.EducationType;
import ham.citybuildersim.Game;
import ham.citybuildersim.TempDirs;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * PT1-M3's render (a scratch class, not in the build; docs/queue/PT1-M3/): the Build overview's
 * WHAT WOULD HELP MOST and AUTOMATIC BUILDING rows on AutoBuildCheck section 6's fixture -
 * ChildcareCheck's town with its elementary schools retired, automatic building on, a month
 * played, so the pass holds back the first school as not paying back while the advice offers it.
 * Run twice, on main's classes ("before") and on the batch's ("after"):
 *   javac -d target/render-pt1m3 -cp "target/classes;<classpath.txt>" docs/queue/PT1-M3/RenderPT1M3.java
 *   java -cp "[target/render-before;]target/classes;<classpath.txt>;target/render-pt1m3" ham.citybuildersim.ui.RenderPT1M3 <tag> <outDir>
 * It writes advice-<tag>.png. The town is reached by reflection (ChildcareCheck.town is package-private).
 */
public final class RenderPT1M3 {

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

    static void settle(Scene sc, Parent root) {
        root.applyCss();
        root.layout();
        sc.getRoot().applyCss();
        sc.getRoot().layout();
    }

    public static void main(String[] args) throws Exception {
        String tag = args[0];
        Path out = Path.of(args[1]);
        Files.createDirectories(out);
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        up.await(60, TimeUnit.SECONDS);

        // ChildcareCheck's town, its streams quiet, as AutoBuildCheck.main() sets them.
        Class<?> cc = Class.forName("ham.citybuildersim.ChildcareCheck");
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        for (String s : new String[] { "out", "quiet" }) {
            Field f = cc.getDeclaredField(s);
            f.setAccessible(true);
            f.set(null, quiet);
        }
        Method town = cc.getDeclaredMethod("town", Path.class, String.class);
        town.setAccessible(true);
        Path root = TempDirs.create("renderpt1m3");
        PrintStream real = System.out;
        Game g = fx(() -> {
            System.setOut(quiet);
            try {
                Game c = (Game) town.invoke(null, root, "card-held");
                for (BuildingsTemplate t : c.getBuildingManager().getTemplates()) {
                    if (t.getTeaches() != EducationType.ELEMENTARY) continue;
                    int had = c.getBuildingManager().getQuantity(t.getId());
                    if (had > 0) c.getBuildingManager().retire(t, had);
                }
                c.getAutoBuilder().setOn(true, null);
                c.simulateMonths(1);
                return c;
            } finally {
                System.setOut(real);
            }
        });
        System.out.println("town at month " + g.getMonth() + "; held: " + g.getAutoBuilder().held());
        KeptBench bench = fx(() -> new KeptBench(g));
        UserInterface ui = (UserInterface) field(bench, "ui");

        fx(() -> {
            BuildScreen bs = ui.buildScreen;
            List<CityNeeds.Need> all = CityNeeds.measure(g, SummaryScreen.WORDS);
            List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(g, all);
            for (BuildAdvice.Suggestion s : advice) System.out.println("  card: " + s.count() + " x " + s.template().getName());
            VBox page = Pieces.widePage();
            HBox head = bs.buildHead(BuildAdvice.OVERVIEW, EnumSet.noneOf(BuildingType.class));
            head.setStyle("-fx-padding: 0 0 4 0;");
            page.getChildren().addAll(head,
                    Pieces.sectionHead("WHAT WOULD HELP MOST", bs.adviceTotal(advice)),
                    bs.adviceRow(advice),
                    Pieces.sectionHead("AUTOMATIC BUILDING", BuildScreen.AUTO_INFO, Pieces.hint(BuildScreen.autoHint(g.getAutoBuilder()))),
                    bs.autoRow());
            VBox frame = new VBox(10, page);
            frame.setAlignment(Pos.TOP_CENTER);
            frame.setPadding(new Insets(8, 0, 14, 0));
            frame.setStyle("-fx-background-color: " + Palette.STAGE + ";");
            Scene sc = new Scene(frame, 1460, 900);
            ui.applyTheme(sc);
            settle(sc, frame);
            double h = Math.ceil(frame.prefHeight(1460));
            frame.resize(1460, h);
            settle(sc, frame);
            png(frame.snapshot(null, null), out.resolve("advice-" + tag + ".png"));
            return null;
        });
        System.exit(0);
    }
}
