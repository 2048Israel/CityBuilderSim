package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;
import javafx.application.Platform;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each
 * month's redraw run over last month's nodes ends exactly where a page built new would, and costs
 * a fraction of it.
 *
 * WHY THIS EXISTS. A kept page (ui.Kept) runs the screen's own drawing code every month over the
 * nodes it made the month before. What can go wrong is what a node carries from one month into the
 * next: a value set under a condition that no longer holds, a list added to, a listener added
 * twice, a picture told only half its new figures. None of it shows on the month it is written;
 * all of it shows as a page that is not the page the city would draw. So this draws the page both
 * ways at every month of a played stretch and compares them - every node's words, style, the
 * values code set, handlers, tooltip and place, and at some months every pixel - and only then
 * times the kept month.
 *
 * It needs the JavaFX toolkit but no window: the page is laid out on a scene of its own
 * (ui.KeptBench). Where the toolkit cannot start (the cloud has no display) it says so in one line
 * and exits 0.
 *
 * What it has to prove:
 *   1. the page is kept: after its first draw a month's redraw hands back last month's nodes and
 *      keeps the same page in the column; the nodes made new in a month are few, and on most months
 *      none (counted over the stretch);
 *   2. it shows what a page built new shows: for MONTHS months of a played city the kept page's
 *      signature - every node's class, words, inline style, the values code set, handlers, tooltip
 *      and where layout put it - equals that of the page built new at the same month, line for line;
 *   3. and looks it: at three of those months the two pages are the same picture - no pixel more
 *      than SHADE_LEVELS apart in a channel, and at most SHADED_SHARE of them apart at all (the
 *      renderer shades a moved shape's edge a level or two apart; the count of months with the very
 *      same pixels is printed);
 *   4. what reshapes the page keeps it right: a details fold opened and closed, the month card's
 *      year view on and off - each redraw after the change equals a page built new;
 *   5. it is cheap: the kept page's month (its draw, then the CSS and layout after it) is within
 *      KEPT_MONTH_MS through Timing, and below the same month built new (both printed).
 *
 * Since UP2 the window's chrome too - the header and the tab rail, drawn by the real
 * refreshHeader() and refreshTabRail() on scenes of their own (the header at the window's width,
 * the rail at its height), the envelope drawn on its own as well:
 *   6. the chrome is kept: after its first draw a month's chrome draw keeps the header's and the
 *      rail's top nodes, hands back nine in ten of the nodes the first draw made, and makes few or
 *      none;
 *   7. it shows what a chrome built new shows: for MONTHS months the kept header's and rail's
 *      signature equals that of a chrome built new (every pool swapped for an empty one), line for
 *      line, the first difference printed with both lines, while the states change among the
 *      months - the clock running and stopped, a "stopped because" line shown and gone, the speed's
 *      rung, the screen on show (so the rail's lit tab, and a game menu with no clock), a notice
 *      raised, read and put away (the envelope's count, redrawn on its own);
 *   8. and looks it: at three of those months the header and the rail are the same pictures as the
 *      new ones (alike(): SHADE_LEVELS, SHADED_SHARE); one figure in the kept header made wrong by
 *      hand parts the signature and the picture, and the next kept draw sets it right; and what the
 *      rebuild did by being one is put back: a node that had the focus loses it, a button or tile
 *      drawn again under the pointer keeps its hover look and loses it when the pointer goes;
 *   9. a city opened forgets the chrome: the next draw makes every node new; and a draw of one pool
 *      inside a draw of another (the envelope inside the header) gives each its own nodes;
 *  10. it is cheap: the kept chrome's month (its draw, then the CSS and layout of both scenes) is
 *      within KEPT_CHROME_MS through Timing, the same month built new printed beside it.
 *
 * ...and the three panels (slice 2b), each on a scene of its own: the city panel, the construction
 * panel and the inbox's list, drawn by refreshCityPanel(), refreshConstructionPanel() and
 * refreshInboxList():
 *  11. the panels are kept: with the inbox open, the drawer open on every section and buildings on
 *      site, a month's draw keeps their top nodes, hands back nine in ten of the first draw's nodes
 *      and makes few (a row for a log entry or a notice that is new);
 *  12. they show what panels built new show, signature line for line, for MONTHS months, with the
 *      states changing: Dashboard with every section open, some, none, Summary, the chip's NEEDS YOU
 *      view, a section folded and unfolded, the drawer open and pinned, the inbox dropped down and
 *      put away, a notice raised, unfolded, read and taken away, buildings put on site and finishing,
 *      a building opened and one demolished; the same pictures at three months; one figure spoiled
 *      by hand is seen and set right; the city panel's scroller stays where it was put; a summary
 *      row keeps its hover look under a still pointer; an inbox button loses the focus;
 *  13. the whole chrome kept (header, rail and the three panels) in one month is within
 *      KEPT_ALL_CHROME_MS through Timing, the same month built new printed beside it.
 */
public class KeptCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. */
    static final int CITY_MONTHS = 600;

    /** The months watched, kept against new: two years and a month, so a January and a year's turn are among them. */
    static final int MONTHS = 25;

    /** The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. */
    static final double KEPT_MONTH_MS = 12;

    /** Where a month's log goes while month() runs it: nowhere. */
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    /** The toolkit started, or why not (then this harness has nothing to run on). */
    static String startJavaFx() {
        try {
            if (java.awt.GraphicsEnvironment.isHeadless()) return "this JVM is headless (no display)";
            Platform.setImplicitExit(false);
            CountDownLatch up = new CountDownLatch(1);
            Platform.startup(up::countDown);
            if (!up.await(60, TimeUnit.SECONDS)) return "the toolkit did not come up in 60 s";
            return null;
        } catch (Throwable t) {
            String why = t.getMessage() == null ? "" : ": " + t.getMessage().replaceAll("\\s+", " ").trim();
            return t.getClass().getSimpleName() + why;
        }
    }

    /** A month, as the clock runs it, its log kept off this harness's output. */
    static void month(Game g) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            g.toggleNextMonth();
        } finally {
            System.setOut(real);
        }
    }

    /** The first line where two signatures part, or null when they are the same. */
    static String firstDifference(List<String> kept, List<String> fresh) {
        int n = Math.min(kept.size(), fresh.size());
        for (int i = 0; i < n; i++) {
            if (!kept.get(i).equals(fresh.get(i))) {
                // Shown from where the two lines part, so a long line's difference is not cut off.
                int k = 0;
                String a = kept.get(i), b = fresh.get(i);
                while (k < a.length() && k < b.length() && a.charAt(k) == b.charAt(k)) k++;
                int from = Math.max(0, k - 60);
                String head = from > 0 ? "..." : "";
                return "line " + i + " (from character " + from + ")\n      kept:  " + head + clip(a.substring(from))
                        + "\n      fresh: " + head + clip(b.substring(from));
            }
        }
        if (kept.size() != fresh.size()) return "kept has " + kept.size() + " lines, fresh " + fresh.size();
        return null;
    }

    /** A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. */
    static void png(int[] px, java.nio.file.Path to) throws java.io.IOException {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(px[0], px[1], java.awt.image.BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, px[0], px[1], px, 2, px[0]);
        java.nio.file.Files.createDirectories(to.getParent());
        javax.imageio.ImageIO.write(img, "png", to.toFile());
    }

    /**
     * The most a pixel of the kept page may differ from the new page's, in any channel, out of 255:
     * the edge of a small shape a kept picture moved to another place on the page is shaded by the
     * renderer's cached outline a level or two apart from a shape drawn there new (People's
     * household tiles, reused by their shape wherever the sort puts them, measured up to 30 and 58
     * on single edge pixels of the half-filled dots of a moved tile at months 617 and 630). A wrong
     * figure, colour or bar is far past it in level or in count.
     */
    static final int SHADE_LEVELS = 64;

    /** ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). */
    static final double SHADED_SHARE = 0.001;

    /** Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. */
    static boolean alike(int[] a, int[] b) {
        if (a[0] != b[0] || a[1] != b[1]) return false;
        int n = 0;
        for (int i = 2; i < a.length; i++) {
            if (a[i] == b[i]) continue;
            n++;
            for (int s = 0; s < 32; s += 8) if (Math.abs(((a[i] >> s) & 255) - ((b[i] >> s) & 255)) > SHADE_LEVELS) return false;
        }
        return n <= SHADED_SHARE * (a.length - 2);
    }

    /** Where two snapshots part: their sizes, or how many pixels differ and the box round them. */
    static String pixelDifference(int[] a, int[] b) {
        if (a[0] != b[0] || a[1] != b[1]) return a[0] + "x" + a[1] + " against " + b[0] + "x" + b[1];
        int w = a[0], h = a[1], n = 0, x0 = w, y0 = h, x1 = -1, y1 = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (a[2 + y * w + x] != b[2 + y * w + x]) {
                    n++;
                    x0 = Math.min(x0, x);
                    y0 = Math.min(y0, y);
                    x1 = Math.max(x1, x);
                    y1 = Math.max(y1, y);
                }
            }
        }
        return n + " pixels, in " + x0 + "," + y0 + " to " + x1 + "," + y1;
    }

    static String clip(String s) {
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }

    static double median(List<Double> v) {
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.isEmpty() ? Double.NaN : s.get(s.size() / 2);
    }

    /** The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. */
    static final double KEPT_CHROME_MS = 4;

    /** The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). */
    static final String[] CHROME_SCREENS = {"showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",
            "showHistoryMenu", "showPolicyMenu", "showMainMenu"};

    /**
     * Sections 6 to 10: the header and the tab rail kept (UP2). The same city, played on; the bench
     * draws the real refreshHeader() and refreshTabRail() on scenes of their own, kept and with every
     * node new, beside the page.
     */
    static void chrome(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 6: the chrome is kept ---");
        fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            return null;
        });
        int[] first = fx(() -> {
            bench.drawChromeKept();
            bench.chromePasses();
            return bench.chromeCounts();
        });
        int firstMade = first[0] + first[2] + first[4];
        System.out.printf("  the first draw made %d nodes through Kept (the header's %d, the envelope's %d, the rail's %d)%n",
                firstMade, first[0], first[2], first[4]);
        int sameTops = 0, quiet = 0, madeMost = 0;
        long back = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.chromeTops();
                month(g);
                bench.drawChromeKept();
                bench.chromePasses();
                int[] c = bench.chromeCounts();
                return new Object[]{before.equals(bench.chromeTops()), c[0] + c[2] + c[4], c[1] + c[3] + c[5]};
            });
            if ((boolean) r[0]) sameTops++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            back += (int) r[2];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, back / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the nodes the header and the rail drew (the same top nodes, " + sameTops + " of " + MONTHS + ")",
                sameTops == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + back / MONTHS + " of " + firstMade + ")",
                back / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + "), and none makes more than a tenth (" + madeMost + ")",
                quiet * 2 > MONTHS && madeMost * 10 <= firstMade);

        System.out.println("\n--- 7 and 8: the kept chrome against a chrome built new, in every state ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0, envelopeDiffer = 0;
        String firstDiffer = null;
        Notice a = new Notice("keptcheck-a", "Kept check A", List.of("a"), g.getMonth());
        Notice b = new Notice("keptcheck-b", "Kept check B", List.of("b"), g.getMonth());
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                // The states change among the months: the clock running and stopped (stopped itself, or not), the speed's rung,
                // the screen on show (so the tab lit), and a notice raised, read and put away.
                boolean running = at % 4 == 0 || at % 4 == 2;
                bench.clock(running, at % 4 == 1 ? "Kept check: a notice stopped the clock" : null);
                bench.speed(at % 9);
                bench.screen(CHROME_SCREENS[at % CHROME_SCREENS.length]);
                month(g);
                bench.drawChromeKept();
                // ...and the inbox changes after the draw, as a notice read elsewhere does: the envelope is drawn on its own.
                switch (at % 6) {
                    case 1 -> g.getInbox().all().add(a);
                    case 2 -> g.getInbox().all().add(b);
                    case 3 -> g.getInbox().markRead(a, g.getMonth());
                    case 4 -> g.getInbox().markRead(b, g.getMonth());
                    case 5 -> {
                        g.getInbox().all().remove(a);
                        g.getInbox().all().remove(b);
                    }
                    default -> { }
                }
                bench.drawEnvelope();
                bench.chromePasses();
                List<String> kept = bench.chromeSignature();
                int[][] keptPx = shotAt.contains(at) ? bench.chromePixels() : null;
                bench.showFreshChrome();
                List<String> fresh = bench.chromeSignature();
                int[][] freshPx = shotAt.contains(at) ? bench.chromePixels() : null;
                bench.restoreKeptChrome();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + " (" + at + "), " + r[0];
            }
            if (r[1] != null) {
                int[][] k = (int[][]) r[1], f = (int[][]) r[2];
                for (int i = 0; i < 2; i++) {
                    if (java.util.Arrays.equals(k[i], f[i])) shotsSame++;
                    if (alike(k[i], f[i])) shotsAlike++;
                    else System.out.println("  " + (i == 0 ? "header" : "rail") + " pixels differ at month " + g.getMonth() + ": " + pixelDifference(k[i], f[i]));
                    if (!java.util.Arrays.equals(k[i], f[i]) && System.getProperty("keptcheck.out") != null) {
                        String to = System.getProperty("keptcheck.out");
                        png(k[i], java.nio.file.Path.of(to, "chrome-kept-" + i + "-m" + g.getMonth() + ".png"));
                        png(f[i], java.nio.file.Path.of(to, "chrome-new-" + i + "-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        g.getInbox().all().remove(a);
        g.getInbox().all().remove(b);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept chrome's signature equals the new chrome's at every month, through every state (" + differ + " differ)", differ == 0);

        System.out.println("\n--- 8: and its pixels ---");
        System.out.printf("  %d of %d pictures the very same pixels%n", shotsSame, shotAt.size() * 2);
        check("the kept header and rail are the same pictures as the new ones at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + " of " + shotAt.size() * 2 + ")", shotsAlike == shotAt.size() * 2);

        Object[] spoilt = fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.chromePasses();
            String was = bench.spoilHeader();
            bench.chromePasses();
            List<String> kept = bench.chromeSignature();
            int[][] keptPx = bench.chromePixels();
            bench.showFreshChrome();
            List<String> fresh = bench.chromeSignature();
            int[][] freshPx = bench.chromePixels();
            bench.restoreKeptChrome();
            bench.drawChromeKept();
            bench.chromePasses();
            List<String> mended = bench.chromeSignature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx[0], freshPx[0]), firstDifference(mended, fresh)};
        });
        check("one figure made wrong in the kept header (\"" + spoilt[0] + "\") parts its signature from the new chrome's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        // What the rebuild did by being a rebuild, put back by hand: the focus, and the look under a still pointer.
        Object[] hand = fx(() -> {
            boolean had = bench.focusPlay();
            bench.drawChromeKept();
            boolean kept = bench.headerHasFocus();
            return new Object[]{had, kept};
        });
        if (!(boolean) hand[0]) System.out.println("  the scene would not give the play button the focus here: the focus check is skipped");
        else check("a header node that had the focus loses it at the next kept draw, as the rebuild took it", !(boolean) hand[1]);

        String[] looks = fx(() -> {
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.chromePasses();
            // The rail's first button (Build; People is the lit tab) and the first tile, each with the pointer put over it.
            javafx.scene.Node button = bench.railChild(0);
            javafx.scene.Node tile = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0);
            String buttonRest = button.getStyle(), tileRest = tile.getStyle();
            button.getOnMouseEntered().handle(null);
            tile.getOnMouseEntered().handle(null);
            String buttonHover = button.getStyle(), tileHover = tile.getStyle();
            button.getOnMouseExited().handle(null);
            tile.getOnMouseExited().handle(null);
            KeptBench.hover(button, true);
            KeptBench.hover(tile, true);
            bench.drawChromeKept();
            String buttonUnder = bench.railChild(0).getStyle(), tileUnder = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0).getStyle();
            KeptBench.hover(button, false);
            KeptBench.hover(tile, false);
            bench.drawChromeKept();
            String buttonAway = bench.railChild(0).getStyle(), tileAway = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0).getStyle();
            return new String[]{buttonRest, buttonHover, buttonUnder, buttonAway, tileRest, tileHover, tileUnder, tileAway};
        });
        check("a rail button drawn again under the pointer keeps its hover look, and loses it when the pointer goes",
                !looks[0].equals(looks[1]) && looks[2].equals(looks[1]) && looks[3].equals(looks[0]));
        check("...and so does a header tile", !looks[4].equals(looks[5]) && looks[6].equals(looks[5]) && looks[7].equals(looks[4]));

        System.out.println("\n--- 9: a city opened forgets the chrome ---");
        int[] again = fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.drawChromeKept();
            int[] quietCounts = bench.chromeCounts();
            bench.forgetChrome();
            bench.drawChromeKept();
            int[] afterCounts = bench.chromeCounts();
            return new int[]{quietCounts[0] + quietCounts[2] + quietCounts[4], afterCounts[0] + afterCounts[2] + afterCounts[4],
                    afterCounts[1] + afterCounts[3] + afterCounts[5]};
        });
        System.out.printf("  a draw before: %d nodes made; the draw after the forgetting: %d made, %d handed back%n", again[0], again[1], again[2]);
        check("before the forgetting a draw makes few nodes, after it every node is new (" + again[1] + " made, " + again[2] + " handed back)",
                again[0] * 10 <= firstMade && again[2] == 0 && again[1] * 10 >= firstMade * 9L);

        boolean nested = fx(KeptBench::nestedDrawsKeepEach);
        check("a draw within another pool's draw gives each pool its own nodes (the envelope inside the header)", nested);

        System.out.println("\n--- 10: what the kept chrome costs ---");
        double[] cost = fx(() -> {
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept();
                double[] p = bench.chromePasses();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: every node made again in the header and the rail, as the redraw was before UP2.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshChrome();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptChrome();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the chrome's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_CHROME_MS);
        System.out.printf("%-72s %s%n", "the kept chrome's month is within KEPT_CHROME_MS (" + KEPT_CHROME_MS + " ms)", Timing.verdict(ok));
        if (!ok) fails++;
    }

    /** The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. */
    static final double KEPT_ALL_CHROME_MS = 8;

    /** The panels' states, month by month: the city panel's mode, and which of its sections are open. */
    static void panelStates(KeptBench bench, int at) {
        switch (at % 5) {
            case 0 -> { bench.cityMode(true, false); bench.sectionsAll(true); }
            case 1 -> bench.cityMode(false, false);
            case 2 -> { bench.cityMode(true, false); bench.sectionsAll(false); bench.section("econ", true); bench.section("labour", true); }
            case 3 -> { bench.cityMode(true, false); bench.sectionsAll(false); }
            default -> bench.cityMode(true, true);      // the header's chip opened the drawer on NEEDS YOU: Summary, whatever is stored
        }
        bench.section("land", at % 2 == 0);              // one section folded and unfolded among the months
        bench.drawer(at % 3 != 0, at % 4 == 1);
    }

    /**
     * Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice
     * 2b), each on a scene of its own, beside the header and the rail of 6 to 10.
     */
    static void panels(Game g, KeptBench bench) throws Exception {
        Notice[] notice = new Notice[2];
        // Buildings to put on site, by the model's own call: three templates that take construction points.
        List<BuildingsTemplate> picks = new ArrayList<>();
        BuildingManager buildings = g.getBuildingManager();
        for (int i = 0; i <= buildings.getMaxTemplateId() && picks.size() < 3; i++) {
            BuildingsTemplate t = buildings.getTemplate(i);
            if (t != null && t.getConstructionPoints() > 0) picks.add(t);
        }
        // The model's own calls cause what the played city may not have: a building opened and one demolished in the stretch.
        System.out.println("\n--- 11: the panels are kept ---");
        int sitesNow = g.getBuildingManager().getUnderConstruction();
        Notice panelsNotice = new Notice("keptcheck-p", "Kept check panels", List.of("a line", "another line"), g.getMonth());
        fx(() -> {
            g.getBuildLog().record("Kept Works", 2, g.getMonth());
            g.getDemolitionLog().record("Kept Mill", 3, "Industry", g.getMonth(), 12_000);
            buildings.addStack(picks.get(0), 20_000, false);
            buildings.addStack(picks.get(1), 20_000, false);
            g.getInbox().all().add(panelsNotice);
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.drawer(true, false);
            bench.inboxList(true, "keptcheck-p");
            return null;
        });
        int[] first = fx(() -> {
            bench.drawPanelsKept();
            bench.panelPasses();
            return bench.panelCounts();
        });
        int firstMade = first[0] + first[2] + first[4];
        System.out.printf("  the first draw made %d nodes through Kept (the city panel's %d, the construction panel's %d, the inbox list's %d);"
                + " %d building(s) under construction%n", firstMade, first[0], first[2], first[4], sitesNow);
        int sameTops = 0, quiet = 0, madeMost = 0, withSites = 0;
        long back = 0, madeAll = 0;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.panelTops();
                // The logs keep an entry two years: one is put in again now and then, so the stretch has them throughout.
                if (at % 12 == 0) {
                    g.getBuildLog().record("Kept Works", 2, g.getMonth());
                    g.getDemolitionLog().record("Kept Mill", 3, "Industry", g.getMonth(), 12_000);
                }
                month(g);
                bench.drawPanelsKept();
                bench.panelPasses();
                int[] c = bench.panelCounts();
                return new Object[]{before.equals(bench.panelTops()), c[0] + c[2] + c[4], c[1] + c[3] + c[5],
                        g.getBuildingManager().getUnderConstruction() > 0};
            });
            if ((boolean) r[0]) sameTops++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            madeAll += (int) r[1];
            back += (int) r[2];
            if ((boolean) r[3]) withSites++;
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d,"
                + " %d in all; months with a building under construction: %d%n", MONTHS, back / MONTHS, quiet, madeMost, madeAll, withSites);
        check("each month's redraw keeps the nodes the three panels drew (the same top nodes, " + sameTops + " of " + MONTHS + ")",
                sameTops == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + back / MONTHS + " of " + firstMade + ")",
                back / MONTHS * 10 >= firstMade * 9L);
        // A log gains a row when a building opens or is demolished, and the inbox a row when a notice is raised: the rows
        // that come are made, the rest are handed back. "Few" is a fiftieth of the first draw a month, and no month a tenth.
        check("the nodes made in a month are few (" + madeAll + " in all over " + MONTHS + " months, at most " + madeMost + " in one)",
                madeAll * 50 <= (long) firstMade * MONTHS && madeMost * 10 <= firstMade);

        System.out.println("\n--- 12: the kept panels against panels built new, in every state ---");
        List<Integer> shotAt = List.of(2, 9, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                panelStates(bench, at);
                // The inbox: dropped down and put away, a notice raised, unfolded, read and taken away.
                switch (at % 6) {
                    case 1 -> notice[0] = add(g, "keptcheck-a", "Kept check A");
                    case 2 -> notice[1] = add(g, "keptcheck-b", "Kept check B");
                    case 3 -> g.getInbox().markRead(notice[0], g.getMonth());
                    case 4 -> g.getInbox().markRead(notice[1], g.getMonth());
                    case 5 -> {
                        g.getInbox().all().remove(notice[0]);
                        g.getInbox().all().remove(notice[1]);
                    }
                    default -> { }
                }
                bench.inboxList(at % 3 != 1, at % 6 == 2 || at % 6 == 3 ? "keptcheck-a" : at % 6 == 4 ? "keptcheck-b" : "");
                // A building put on site in the stretch, and the logs: a demolition and a building opened now and then.
                if (at == 8) buildings.addStack(picks.get(2), 20_000, false);
                if (at % 7 == 3) g.getDemolitionLog().record("Kept Mill " + at, 1 + at % 3, at % 2 == 0 ? "City order" : "Industry", g.getMonth(), at % 2 == 0 ? 0 : 7_500);
                if (at % 5 == 2) g.getBuildLog().record("Kept Works " + at, 1 + at % 4, g.getMonth());
                month(g);
                bench.drawPanelsKept();
                bench.panelPasses();
                List<String> kept = bench.panelSignature();
                int[][] keptPx = shotAt.contains(at) ? bench.panelPixels() : null;
                bench.showFreshPanels();
                List<String> fresh = bench.panelSignature();
                int[][] freshPx = shotAt.contains(at) ? bench.panelPixels() : null;
                bench.restoreKeptPanels();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + " (" + at + "), " + r[0];
            }
            if (r[1] != null) {
                int[][] k = (int[][]) r[1], f = (int[][]) r[2];
                for (int i = 0; i < 3; i++) {
                    if (java.util.Arrays.equals(k[i], f[i])) shotsSame++;
                    if (alike(k[i], f[i])) shotsAlike++;
                    else System.out.println("  panel " + i + " pixels differ at month " + g.getMonth() + ": " + pixelDifference(k[i], f[i]));
                    if (!java.util.Arrays.equals(k[i], f[i]) && System.getProperty("keptcheck.out") != null) {
                        String to = System.getProperty("keptcheck.out");
                        png(k[i], java.nio.file.Path.of(to, "panel-kept-" + i + "-m" + g.getMonth() + ".png"));
                        png(f[i], java.nio.file.Path.of(to, "panel-new-" + i + "-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        g.getInbox().all().remove(notice[0]);
        g.getInbox().all().remove(notice[1]);
        g.getInbox().all().remove(panelsNotice);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept panels' signatures equal the new panels' at every month, through every state (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d pictures the very same pixels%n", shotsSame, shotAt.size() * 3);
        check("the kept panels are the same pictures as the new ones at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + " of " + shotAt.size() * 3 + ")", shotsAlike == shotAt.size() * 3);

        Object[] spoilt = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.inboxList(false, "");
            bench.drawPanelsKept();
            bench.panelPasses();
            String was = bench.spoilPanel();
            bench.panelPasses();
            List<String> kept = bench.panelSignature();
            int[][] keptPx = bench.panelPixels();
            bench.showFreshPanels();
            List<String> fresh = bench.panelSignature();
            int[][] freshPx = bench.panelPixels();
            bench.restoreKeptPanels();
            bench.drawPanelsKept();
            bench.panelPasses();
            List<String> mended = bench.panelSignature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx[0], freshPx[0]), firstDifference(mended, fresh)};
        });
        check("one figure made wrong in the kept city panel (\"" + spoilt[0] + "\") parts its signature from the new panels'",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        // What the rebuild did by being a rebuild, put back by hand: the scroll, the look under a still pointer, the focus.
        double[] scroll = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.drawPanelsKept();
            bench.panelPasses();
            javafx.scene.control.ScrollPane before = bench.cityScroller();
            before.setVvalue(.5);
            bench.panelPasses();
            double at = before.getVvalue();
            month(g);
            bench.drawPanelsKept();
            bench.panelPasses();
            return new double[]{bench.cityScroller() == before ? 1 : 0, at, bench.cityScroller().getVvalue()};
        });
        check("the city panel's scroller is kept and stays where it was put (" + String.format("%.2f", scroll[1]) + " then "
                + String.format("%.2f", scroll[2]) + ")", scroll[0] == 1 && scroll[1] > .1 && Math.abs(scroll[2] - scroll[1]) < .05);

        String[] looks = fx(() -> {
            bench.cityMode(false, false);       // Summary: rows that go to the tab that owns the number
            bench.drawPanelsKept();
            bench.panelPasses();
            javafx.scene.Node row = bench.firstHoverInCityPanel();
            if (row == null) return new String[]{"", "", "", ""};
            String rest = row.getStyle();
            row.getOnMouseEntered().handle(null);
            String hover = row.getStyle();
            row.getOnMouseExited().handle(null);
            KeptBench.hover(row, true);
            bench.drawPanelsKept();
            String under = bench.firstHoverInCityPanel().getStyle();
            KeptBench.hover(row, false);
            bench.drawPanelsKept();
            return new String[]{rest, hover, under, bench.firstHoverInCityPanel().getStyle()};
        });
        check("a summary row drawn again under the pointer keeps its hover look, and loses it when the pointer goes",
                !looks[0].isEmpty() && !looks[0].equals(looks[1]) && looks[2].equals(looks[1]) && looks[3].equals(looks[0]));

        Object[] hand = fx(() -> {
            // An unsettled notice, unfolded: it has the button that deals with it.
            Notice open = add(g, "keptcheck-f", "Kept check focus");
            bench.inboxList(true, "keptcheck-f");
            bench.drawPanelsKept();
            bench.panelPasses();
            boolean had = bench.focusInboxButton();
            bench.drawPanelsKept();
            boolean still = bench.inboxHasFocus();
            g.getInbox().all().remove(open);
            return new Object[]{had, still};
        });
        if (!(boolean) hand[0]) System.out.println("  the inbox list has no action button to take the focus here: the focus check is skipped");
        else check("an inbox button that had the focus loses it at the next kept draw, as the rebuild took it", !(boolean) hand[1]);

        System.out.println("\n--- 13: what the whole chrome costs, kept ---");
        double[] cost = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(false);
            bench.section("econ", true);
            bench.inboxList(true, "");
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.drawPanelsKept();
            bench.chromePasses();
            bench.panelPasses();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept() + bench.drawPanelsKept();
                double[] c = bench.chromePasses(), p = bench.panelPasses();
                kept.add(draw + c[0] + c[1] + p[0] + p[1]);
            }
            // The same month built new: every node of the header, the rail and the three panels made again.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] a = bench.showFreshChrome(), b = bench.showFreshPanels();
                fresh.add(a[0] + a[1] + a[2] + b[0] + b[1] + b[2]);
                bench.restoreKeptChrome();
                bench.restoreKeptPanels();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the whole chrome's month (its draws, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_ALL_CHROME_MS);
        System.out.printf("%-72s %s%n", "the whole kept chrome's month is within KEPT_ALL_CHROME_MS (" + KEPT_ALL_CHROME_MS + " ms)", Timing.verdict(ok));
        if (!ok) fails++;
    }

    /** A notice raised now, unread, in the inbox. */
    private static Notice add(Game g, String key, String title) {
        Notice n = new Notice(key, title, List.of("first line of " + key, "", "third line"), g.getMonth());
        g.getInbox().all().add(n);
        return n;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== KeptCheck: the People page and the chrome kept between redraws (study UP4, UP2) ===");
        String down = startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the page needs it to be laid out");
            System.exit(0);
        }
        GameFiles files = GameFiles.scratch("keptcheck");
        Game g = fx(() -> LongPlaytest.probeCity(files, null));
        fx(() -> {
            LongPlaytest.probePlay(g, CITY_MONTHS);
            return null;
        });
        KeptBench bench = fx(() -> new KeptBench(g));
        System.out.printf("a city played to month %d: %,d people%n", g.getMonth(), g.getPopulationManager().getPopulation());

        System.out.println("\n--- 1 and 2: the kept page, month after month, against a page built new ---");
        // The first draw makes the page; it is laid out before the months start.
        int firstMade = fx(() -> {
            bench.drawKept();
            bench.passes();
            return bench.lastMade();
        });
        System.out.printf("  the first draw made %d nodes through Kept%n", firstMade);
        int sameTop = 0, quiet = 0, differ = 0, madeMost = 0;
        long handedBack = 0;
        String firstDiffer = null;
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int shotsSame = 0, shotsAlike = 0;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                Object topBefore = bench.top();
                month(g);
                bench.drawKept();
                bench.passes();
                boolean same = bench.top() == topBefore;
                int made = bench.lastMade(), back = bench.lastKept();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFresh();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKept();
                return new Object[]{same, made, back, firstDifference(kept, fresh), keptPx, freshPx};
            });
            if ((boolean) r[0]) sameTop++;
            int made = (int) r[1];
            if (made == 0) quiet++;
            madeMost = Math.max(madeMost, made);
            handedBack += (int) r[2];
            if (r[3] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[3];
            }
            if (r[4] != null) {
                int[] a = (int[]) r[4], b = (int[]) r[5];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, "kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, "new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top node, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);

        System.out.println("\n--- 3: and its pixels ---");
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawKept();
            bench.passes();
            String was = bench.spoil();
            bench.passes();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFresh();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKept();
            bench.drawKept();
            bench.passes();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        System.out.println("\n--- 4: what reshapes the page ---");
        String[][] changes = {
                {"the households' matrix opened", "matrix", "open"},
                {"the skill ladder's table opened", "ladder", "open"},
                {"the month card's year view on", "year", "on"},
                {"the households' matrix closed", "matrix", "close"},
                {"the skill ladder's table closed", "ladder", "close"},
                {"the month card's year view off", "year", "off"},
        };
        for (String[] c : changes) {
            String d = fx(() -> {
                if (c[1].equals("year")) bench.yearView(c[2].equals("on"));
                else bench.details(c[1], c[2].equals("open"));
                month(g);
                bench.drawKept();
                bench.passes();
                List<String> kept = bench.signature();
                bench.showFresh();
                List<String> fresh = bench.signature();
                bench.restoreKept();
                // ...and a month later, the page drawn over the reshaped one.
                month(g);
                bench.drawKept();
                bench.passes();
                List<String> kept2 = bench.signature();
                bench.showFresh();
                List<String> fresh2 = bench.signature();
                bench.restoreKept();
                String a = firstDifference(kept, fresh), b = firstDifference(kept2, fresh2);
                return a != null ? a : b;
            });
            if (d != null) System.out.println("  " + c[0] + ": " + d);
            check(c[0] + ": the redraw and the next equal a page built new", d == null);
        }

        System.out.println("\n--- 5: what the kept month costs ---");
        double[] cost = fx(() -> {
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before UP4.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFresh();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKept();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_MONTH_MS);
        System.out.printf("%-72s %s%n", "the kept month is within KEPT_MONTH_MS (" + KEPT_MONTH_MS + " ms)", Timing.verdict(ok));
        if (!ok) fails++;

        chrome(g, bench);
        panels(g, bench);

        System.out.println("\n" + (fails == 0 ? "ALL OK" : fails + " FAILED"));
        Platform.exit();
        System.exit(fails);
    }
}
