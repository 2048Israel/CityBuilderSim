package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.MiniMapFrame;
import ham.citybuildersim.Sector;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * The picture tool (UX1): a copied city's every rail page and sub-page as the real window draws it at
 * 1920x1080, one PNG each, for the interface rework's study and every UI batch's before-and-after.
 *
 * <p>WHY. Until UX1 a picture of a page meant a player at the screen, or a scratch driver written for one
 * screen (the desk chat's CbShot, the central bank's places) and thrown away. The rework changes every page,
 * so each batch of it needs the same pages of the same cities drawn before and after, by one walk of the
 * rail that a harness can share: {@link #pages} is that walk, and UiSteadyCheck redraws the same list.
 *
 * <p>USAGE (Git Bash, from the repository root, after a compile):
 * <pre>
 *   java -Dprism.order=sw -cp "target/classes;$(cat target/classpath.txt)" \
 *        -Dcbs.earth=C:/.../data/earth-2.bin -Dcbs.world=C:/.../data/world-1.bin \
 *        ham.citybuildersim.ui.PagePictures FOLDER SLOT MONTHS OUT [PREFIX...]
 * </pre>
 * FOLDER "playtest" grows the playtest's city to month SLOT instead (openPlaytest), then plays MONTHS more.
 * Otherwise FOLDER holds a copy of slot-NN.json, slot-NN-history.json and slot-NN-map.bin (copied there by hand, never
 * the saves folder itself); the tool copies them again into a {@link GameFiles#scratch} folder and loads that,
 * so neither the player's slots nor FOLDER is written. It plays MONTHS months (0 for the city as saved),
 * then writes OUT/NN-page.png for every page of {@link #pages}, and OUT/NN-page-whole.png beside it where the
 * page's own scroller holds more than the window shows. PREFIX limits the walk to pages whose names start with
 * one of them ("bank", "finances-"). -Dpictures.sector=SimpleName picks the sector whose books are drawn
 * (else the one with the most workers). Needs a display, or the gate box's Monocle (FollowBench.startJavaFx).
 */
public final class PagePictures {

    private PagePictures() { }

    /** The window's size every picture is drawn at: the full-HD screen most players have (UX1's brief). */
    static final int WIDTH = 1920, HEIGHT = 1080;

    /** How long a page is left after it opens before it is drawn: the layout's pulses, and any fade (ms). */
    static final int SETTLE_MS = 400;

    /** ...and a page with the map on it: MapView paints its tiles a few a frame, from the middle out (ms). */
    static final int MAP_SETTLE_MS = 4000;

    /** One page of the walk: its file name and the hand that opens it, run on the FX thread. */
    public record Page(String name, Runnable open) { }

    /**
     * Every rail page and its sub-pages, in rail order: the city as it lands, the map over the window, then
     * each rail tab's home and the sub-pages its chips open. Each opener lands the page as the player's
     * click does (pressRail, then the screen's own open method), so the pages are the window's, not copies.
     */
    public static List<Page> pages(UserInterface ui, Game g) {
        List<Page> out = new ArrayList<>();
        out.add(new Page("city", () -> ui.pressRail("build")));
        out.add(new Page("city-drawer", () -> { ui.pressRail("build"); ui.drawerOpen = true; ui.redraw(); }));
        out.add(new Page("city-inbox", () -> { ui.drawerOpen = false; ui.inboxOpen = true; ui.redraw(); }));
        out.add(new Page("map", () -> { ui.inboxOpen = false; ui.openMapOn(MiniMapFrame.frame(g.getCityMap())); }));

        for (String c : KeptBench.buildPages()) {
            out.add(new Page("build-" + slug(c), () -> {
                ui.pressRail("build");
                if (!c.equals("Overview")) ui.buildScreen.openCategory(c);
            }));
        }
        for (String t : new String[] {"Sites", "Timeline", "Demolish"}) {
            out.add(new Page("construction-" + slug(t), () -> { ui.pressRail("build"); ui.constructionScreen.showTab(t); }));
        }
        out.add(new Page("land", () -> ui.pressRail("land")));
        out.add(new Page("people", () -> ui.pressRail("population")));
        out.add(new Page("people-household", () -> { ui.pressRail("population"); ui.peopleScreen.showHouseholdMenu(); }));
        for (String[] sp : KeptBench.servicesPages()) {
            out.add(new Page("services-" + slug(sp[0]) + "-" + slug(sp[1]),
                    () -> { ui.pressRail("services"); ui.servicesScreen.open(sp[0], sp[1]); }));
        }
        for (String p : InfrastructureScreen.INFRA_PAGES) {
            out.add(new Page("infrastructure-" + slug(p), () -> { ui.pressRail("infrastructure"); ui.infrastructureScreen.open(p); }));
        }
        out.add(new Page("sectors", () -> ui.pressRail("sector")));
        Sector books = booksSector(g);
        if (books != null) {
            for (String p : SectorScreen.SECTOR_PAGES) {
                out.add(new Page("sectors-" + slug(books.getClass().getSimpleName()) + "-" + slug(p),
                        () -> { ui.pressRail("sector"); ui.sectorScreen.openSectorBooks(books, p); }));
            }
        }
        for (String p : GovernmentScreen.GOV_PAGES) {
            out.add(new Page("government-" + slug(p), () -> { ui.pressRail("government"); ui.governmentScreen.open(p, null); }));
        }
        out.add(new Page("finances", () -> ui.pressRail("finances")));
        for (String a : FinancesScreen.AREAS) {
            for (String p : KeptBench.financePages(a)) {
                out.add(new Page("finances-" + slug(a) + "-" + slug(p),
                        () -> { ui.pressRail("finances"); ui.financesScreen.open(a, p, null); }));
            }
        }
        out.add(new Page("bank", () -> ui.pressRail("bank")));
        out.add(new Page("bank-commercial", () -> { ui.pressRail("bank"); ui.bankScreen.openOverview(); }));
        for (String p : KeptBench.bankPages()) {
            out.add(new Page("bank-commercial-" + slug(p), () -> { ui.pressRail("bank"); ui.bankScreen.openPage(p); }));
        }
        for (String p : CentralBankScreen.PAGES) {
            out.add(new Page("bank-central-" + slug(p), () -> { ui.pressRail("bank"); ui.bankScreen.openCentral(p); }));
        }
        for (String p : TradeScreen.PAGES) {
            out.add(new Page("trade-" + slug(p), () -> { ui.pressRail("trade"); ui.tradeScreen.open(p); }));
        }
        out.add(new Page("policy", () -> { ui.pressRail("policy"); ui.policyScreen.openHub(); }));
        for (String a : PolicyScreen.AREAS) {
            for (String p : policyPages(a)) {
                out.add(new Page("policy-" + slug(a) + "-" + slug(p), () -> { ui.pressRail("policy"); ui.policyScreen.open(a, p); }));
            }
        }
        out.add(new Page("history", () -> ui.pressRail("reports")));
        out.add(new Page("rankings", () -> ui.pressRail("rankings")));
        for (int i = 0; i < out.size(); i++) {
            Page p = out.get(i);
            out.set(i, new Page(String.format("%03d-%s", i, p.name()), p.open()));
        }
        return out;
    }

    /** A Policy area's pages, in its chip strip's order. */
    static String[] policyPages(String area) {
        return switch (area) {
            case PolicyScreen.TAXES -> PolicyScreen.POLICY_TAX_PAGES;
            case PolicyScreen.WAGES -> PolicyScreen.POLICY_WAGE_PAGES;
            case PolicyScreen.MONEY -> PolicyScreen.POLICY_MONEY_PAGES;
            case PolicyScreen.PROMISES -> PolicyScreen.POLICY_PROMISE_PAGES;
            case PolicyScreen.ZONING -> ZoningScreen.PAGES;
            case PolicyScreen.RESEARCH -> ResearchScreen.PAGES;
            default -> new String[0];
        };
    }

    /** The sector whose books are drawn: -Dpictures.sector's, else the one with the most workers. */
    static Sector booksSector(Game g) {
        String wanted = System.getProperty("pictures.sector", "");
        Sector best = null;
        for (Sector s : g.getSectors().all()) {
            if (!wanted.isEmpty()) {
                if (s.getClass().getSimpleName().equalsIgnoreCase(wanted)) return s;
                continue;
            }
            if (best == null || s.getWorkers() > best.getWorkers()) best = s;
        }
        return best;
    }

    /** A page's words as a file name: "Home & abroad" is "home-abroad". */
    static String slug(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    /* ----- the run ----- */

    static Game g;
    static UserInterface ui;
    static Stage stage;

    static <T> T fx(Callable<T> c) throws Exception {
        return FollowBench.fx(c);
    }

    /** A private method of the window run, as UiProbe runs anotherCity and openCity. */
    static void call(UserInterface on, String name) throws Exception {
        Method m = UserInterface.class.getDeclaredMethod(name);
        m.setAccessible(true);
        m.invoke(on);
    }

    static Scene scene(UserInterface on) throws Exception {
        Field f = UserInterface.class.getDeclaredField("scene");
        f.setAccessible(true);
        return (Scene) f.get(on);
    }

    /**
     * A copy of a slot loaded from a scratch folder and shown in the real window, at 1920x1080, not
     * full-screen: the city and the window every picture and UiSteadyCheck's walk are drawn on.
     */
    static void openCopy(Path folder, int slot) throws Exception {
        GameFiles files = GameFiles.scratch("pictures");
        Files.createDirectories(files.saveFile(slot).getParent());
        String stem = String.format("slot-%02d", slot);
        Files.copy(folder.resolve(stem + ".json"), files.saveFile(slot), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(folder.resolve(stem + "-history.json"), files.historyFile(slot), StandardCopyOption.REPLACE_EXISTING);
        Path map = folder.resolve(stem + "-map.bin");
        if (Files.exists(map)) Files.copy(map, files.mapFile(slot), StandardCopyOption.REPLACE_EXISTING);
        prefsFor(files);
        fx(() -> {
            g = new Game(files);
            g.loadGame(slot);
            return null;
        });
        openWindow();
    }

    /**
     * ...or, with FOLDER "playtest", the playtest's city (LongPlaytest.probeCity, the city UiProbe and the
     * nightly grow) played to a month: a mid or a big city when no slot of today's format holds one (UX1: his
     * slots 2 and 9 were format 66, and a copy patched to load drew 0 people).
     */
    static void openPlaytest(int toMonth) throws Exception {
        GameFiles files = GameFiles.scratch("pictures");
        prefsFor(files);
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            g = fx(() -> LongPlaytest.probeCity(files, null));
            fx(() -> { LongPlaytest.probePlay(g, toMonth); return null; });
        } finally {
            System.setOut(real);
        }
        openWindow();
    }

    /** The window reads its preferences from the city's folder: not full-screen. */
    private static void prefsFor(GameFiles files) {
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);
        System.out.println("scratch " + files.getDirectory());
    }

    /** The game's own window on the city {@link #g}, at 1920x1080. */
    private static void openWindow() throws Exception {
        fx(() -> {
            ui = new UserInterface(g);
            stage = new Stage();
            ui.start(stage);
            stage.setFullScreen(false);
            stage.setMaximized(false);
            stage.setWidth(WIDTH);
            stage.setHeight(HEIGHT);
            call(ui, "anotherCity");
            call(ui, "openCity");
            return null;
        });
    }

    /** The window as it shows now, as a PNG; and, where the page's own scroller holds more, its whole content. */
    static void shoot(String name, File dir) throws Exception {
        Thread.sleep(name.contains("map") || name.contains("land") ? MAP_SETTLE_MS : SETTLE_MS);
        fx(() -> {
            Scene sc = scene(ui);
            sc.getRoot().applyCss();
            sc.getRoot().layout();
            png(sc.snapshot(null), new File(dir, name + ".png"));
            ScrollPane page = pageScroller(sc.getRoot());
            if (page != null && page.getContent().getLayoutBounds().getHeight() > page.getHeight() + 40) {
                png(page.getContent().snapshot(null, null), new File(dir, name + "-whole.png"));
            }
            return null;
        });
    }

    /** The largest scroller on show: the page's own. */
    static ScrollPane pageScroller(Node root) {
        List<ScrollPane> all = new ArrayList<>();
        collect(root, all);
        ScrollPane best = null;
        double area = 0;
        for (ScrollPane sp : all) {
            if (!sp.isVisible() || sp.getContent() == null || sp.getScene() == null) continue;
            double a = sp.getWidth() * sp.getHeight();
            if (a > area) { area = a; best = sp; }
        }
        return best;
    }

    static void collect(Node n, List<ScrollPane> out) {
        if (n instanceof ScrollPane sp) out.add(sp);
        if (n instanceof Parent p) for (Node c : p.getChildrenUnmodifiable()) collect(c, out);
    }

    static void png(WritableImage img, File file) throws Exception {
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        PixelReader r = img.getPixelReader();
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) b.setRGB(x, y, r.getArgb(x, y));
        ImageIO.write(b, "png", file);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("PagePictures FOLDER SLOT MONTHS OUT [PREFIX...]; see the class comment.");
            return;
        }
        if (System.getProperty("prism.order") == null) System.setProperty("prism.order", "sw");
        String cannot = FollowBench.startJavaFx();
        if (cannot != null) {
            System.out.println("PagePictures: JavaFX cannot start here (" + cannot + ").");
            System.exit(1);
        }
        boolean playtest = args[0].equals("playtest");
        Path folder = playtest ? null : Path.of(args[0]);
        int slot = playtest ? 0 : Integer.parseInt(args[1]);
        int months = Integer.parseInt(args[2]);
        File dir = new File(args[3]);
        dir.mkdirs();
        List<String> only = List.of(args).subList(4, args.length);
        try {
            if (playtest) openPlaytest(Integer.parseInt(args[1]));
            else openCopy(folder, slot);
            System.out.println("loaded month " + fx(() -> g.getMonth()));
            for (int i = 0; i < months; i++) fx(() -> { g.toggleNextMonth(); return null; });
            fx(() -> { ui.redraw(); return null; });
            System.out.println("drawn at month " + fx(() -> g.getMonth()));
            List<Page> walk = fx(() -> pages(ui, g));
            int shot = 0, failed = 0;
            for (Page p : walk) {
                String bare = p.name().substring(4);
                if (!only.isEmpty() && only.stream().noneMatch(bare::startsWith)) continue;
                try {
                    fx(() -> { p.open().run(); return null; });
                    shoot(p.name(), dir);
                    shot++;
                } catch (Exception e) {
                    failed++;
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    System.out.println("FAILED " + p.name() + ": " + t);
                }
            }
            System.out.println(shot + " pictures in " + dir + (failed > 0 ? ", " + failed + " pages failed" : "")
                    + ", month " + fx(() -> g.getMonth()));
            fx(() -> { stage.close(); return null; });
            System.exit(failed > 0 ? 1 : 0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
