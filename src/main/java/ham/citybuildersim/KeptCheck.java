package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;
import javafx.animation.Animation;
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
 *
 * ...and City History (KP1), its page drawn kept from a pool of its own, the big chart and the two pins'
 * charts the screen's own and shared by the kept page and the one built new (the bench puts them back):
 *  14. City History is kept: each month's redraw keeps the page's top nodes and hands back nine in ten
 *      of the first draw's nodes, most months make none, and the big chart, both pins' charts, the
 *      legend's chips and the range buttons are the same nodes in the same boxes, in the scene;
 *  15. it shows what a page built new shows: for MONTHS months the kept page's signature equals that of the
 *      page built new (the pool, and the big chart's chips' pool, swapped for empty ones), line for line;
 *  16. and looks it: the same pictures at three of those months (alike()); one figure made wrong by hand parts
 *      signature and picture, and the next kept draw sets it right;
 *  17. what reshapes the page keeps it right: a line picked and unpicked, a preset, a picker group closed and
 *      opened, a word typed in the filter, the hard times' and the goods' folds, the log scale, real GDP alone
 *      in layers, a line hidden in the legend, the chart's window moved (a range, an episode's span, by hand),
 *      a line pinned and a pin taken off, the chart's full screen, the year book's card - the redraw after each
 *      and the one after that equal a page built new;
 *  18. the crosshair and the window survive a month: a window on the newest month follows it and one put by
 *      hand stays; the crosshair a MOUSE_MOVED set on the big chart's plot is there after a kept redraw and gone
 *      from a page built new (which takes the chart off the scene); a legend chip under the pointer is still the
 *      same chip, under it, with the same tooltip;
 *  19. the count-up only at 1x-10x: with the clock running at each rung of the speed ladder, a month that lands on
 *      People starts LIVING HERE's count-up, and on City History the left pin's, exactly at the rungs
 *      UserInterface.countsUp() allows (COUNT_UP_TOP) and always when the clock is paused; a count-up still
 *      running when a month lands past COUNT_UP_TOP is stopped;
 *  20. it is cheap: the kept City History month (its draw, then the pulse's CSS and layout) is within
 *      KEPT_HISTORY_MS through Timing, the same month built new printed beside it.
 *
 * ...and Infrastructure, Services and the land office (KP2), each page drawn kept from the screen's own pool and built
 * new from an empty one (the land office's map, which both hold, put back after), four sections each - the kept page, what
 * it shows, what reshapes it, what it costs - and the three that cut across them:
 *  21. Infrastructure is kept: on its Roads page, with transit built (a Bus Network ordered through the model if the city
 *      has none, so that the Transit page's fare card is there), each month's redraw keeps the top nodes, hands back nine
 *      in ten of the first draw's nodes, and on most months makes none;
 *  22. it shows what a page built new shows: for MONTHS months the signatures are equal line for line, the pictures the
 *      same at three of them, one figure made wrong by hand parts both and the next kept draw sets it right;
 *  23. what reshapes it keeps it right: every page, the Freight divisions, the roads', transit, freight and railway folds
 *      opened (each has to add to the page) and closed - the redraw after each and the one after a month lands equal a
 *      page built new, and the month's redraw hands back nine in ten of the page;
 *  24. it is cheap: the kept month within KEPT_INFRASTRUCTURE_MS through Timing, the month built new printed beside it;
 *  25 to 28. the same four for Services, on every sub-page of the four systems and every fold on them (the Health overview's
 *      sick rate to the safety books);
 *  29 to 32. the same four for the land office, on its sides and places, the best-N count, its fold, the way of paying, and
 *      a plot bought (the receipt, the offers that came onto the shelf: all from the model); the page says if the city
 *      owns ground on a second landmass, whose table is made with new and is not kept, and how many sectors wait on ground;
 *  33. the land office's other pages: the city is brought to the end of its cash by buying through the model, so that the
 *      funding page exists; it and the page for when the city fell short, each an arrival, are drawn and a month later again,
 *      equal to pages built new;
 *  34. a control keeps the player's hand: on Transit's fare dial, through the screen's real show method, a slider pressed is
 *      held, and a month landing under it leaves the same node, its thumb, its reading (in the month's money, as a dial built
 *      at the thumb reads) and its ladder's shown value where the player put them; released, the fare is staged at the thumb
 *      and the page equals a page built new; after several kept months a value set outside any draw reaches the reading of
 *      the latest draw; the fare is unstaged at the end. And City History's filter box, with the focus on it (through
 *      Node.setFocused when this scene has no window to give it one - the line says which), keeps its node, text, caret and
 *      focus across a month through the real show method, and Kept.text sets a field that has none and leaves one that has;
 *  35. the hover is kept: on City History's row that moves the chart and chart card's head, on a door, a vitals cell that
 *      is a door, Services' door card and the land office's offer row and ore card, a pointer put over the node (a page may
 *      have none this month: the line says so) and the page drawn kept again leave the same node in the look its own
 *      mouse-entered gives it, and with the pointer off, the look its mouse-exited gives it.
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

    /** A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). */
    static <T> T quietly(Callable<T> call) throws Exception {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            return call.call();
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
                // Where the node is: the start of the line (its depth, class, words and style), which a long line's tail does not show.
                String start = from > 0 ? "\n      node:  " + a.substring(0, Math.min(from, 110)) + (from > 110 ? "..." : "") : "";
                return "line " + i + " (from character " + from + ")" + start + "\n      kept:  " + head + clip(a.substring(from))
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
        String timed = "the kept chrome's month is within KEPT_CHROME_MS (" + KEPT_CHROME_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
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
        String timed = "the whole kept chrome's month is within KEPT_ALL_CHROME_MS (" + KEPT_ALL_CHROME_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /**
     * The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC:
     * twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same month built new
     * 20.6 to 26.3 ms).
     */
    static final double KEPT_HISTORY_MS = 13;

    /** A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). */
    @FunctionalInterface
    interface Hand {
        String on(KeptBench bench) throws Exception;
    }

    /** One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2). */
    record Choice(String name, Hand hand, boolean opens) {
        Choice(String name, Hand hand) {
            this(name, hand, false);
        }
    }

    /** The first of these lines picked and not in the preset the page opens on: a line to tick and untick. */
    static final String TICKED = "unemployment";

    /**
     * Sections 14 to 20: City History kept (KP1). The same city, played on; the bench draws the page
     * kept from its pool and built new (the pools swapped for empty ones), the two charts shared by both.
     */
    static void history(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 14: City History is kept ---");
        // The first draw makes the page; it is laid out (to where the scroller's width settles the charts' size) before the months start.
        int firstMade = fx(() -> {
            bench.drawHistoryKept();
            for (int i = 0; i < 3; i++) bench.passes();
            return bench.historyCounts()[0];
        });
        List<javafx.scene.Node> charts = fx(bench::historyCharts);
        List<javafx.scene.Node> chartParents = fx(() -> {
            List<javafx.scene.Node> parents = new ArrayList<>();
            for (javafx.scene.Node c : charts) parents.add(c.getParent());
            return parents;
        });
        List<javafx.scene.Node> legend = fx(bench::historyLegend), ranges = fx(bench::historyRanges);
        System.out.printf("  the first draw made %d nodes through Kept; the page holds %d charts, a legend of %d chips and %d range buttons%n",
                firstMade, charts.size(), legend.size(), ranges.size());
        check("the page holds the big chart and the two pins' (" + charts.size() + " charts)", charts.size() == 3);
        int watched = fx(bench::watchGraphics);
        int sameTop = 0, quiet = 0, madeMost = 0, chartsKept = 0, chipsKept = 0, graphicMoves = 0;
        long handedBack = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.historyTop();
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                int[] c = bench.historyCounts();
                boolean chartsStay = bench.historyCharts().equals(charts);
                for (int i = 0; chartsStay && i < charts.size(); i++) {
                    chartsStay = charts.get(i).getScene() != null && charts.get(i).getParent() == chartParents.get(i);
                }
                boolean chipsStay = bench.historyLegend().equals(legend) && bench.historyRanges().equals(ranges);
                return new Object[]{before.equals(bench.historyTop()), c[0], c[1], chartsStay, chipsStay, bench.graphicMoves()};
            });
            if ((boolean) r[0]) sameTop++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            handedBack += (int) r[2];
            if ((boolean) r[3]) chartsKept++;
            if ((boolean) r[4]) chipsKept++;
            graphicMoves += (int) r[5];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top nodes, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
        check("the big chart and both pins' stay in the scene, in the same box, after every redraw (" + chartsKept + " of " + MONTHS + ")",
                chartsKept == MONTHS);
        check("the legend's chips and the range buttons are the same nodes after every redraw (" + chipsKept + " of " + MONTHS + ")",
                chipsKept == MONTHS);
        check("no label's graphic is taken off the scene and put back by a kept redraw (" + watched + " watched, " + graphicMoves + " moves)",
                watched >= 3 && graphicMoves == 0);

        System.out.println("\n--- 15 and 16: the kept page, month after month, against a page built new, and its pixels ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKeptHistory();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[0];
            }
            if (r[1] != null) {
                int[] a = (int[]) r[1], b = (int[]) r[2];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, "history-kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, "history-new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawHistoryKept();
            bench.passes();
            String was = bench.spoilHistory();
            bench.settle();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFreshHistory();
            bench.settle();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKeptHistory();
            bench.drawHistoryKept();
            bench.settle();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        System.out.println("\n--- 17: what reshapes the page ---");
        int[] bounds = fx(bench::historyBounds);
        double[] episode = fx(bench::historyEpisodeSpan);
        String population = "population";
        String peopleGroup = fx(() -> bench.historyGroupOf(population));
        List<Choice> choices = new ArrayList<>(List.of(
                new Choice("a line picked", b -> { b.historyPick(TICKED, true); return null; }),
                new Choice("a line unpicked", b -> { b.historyPick(TICKED, false); return null; }),
                new Choice("a preset (the budget)", b -> { b.historyPreset(3); return null; }),
                new Choice("another preset (the people)", b -> { b.historyPreset(2); return null; }),
                new Choice("a picker group closed", b -> { b.historyGroup(peopleGroup, false); return null; }),
                new Choice("a picker group opened", b -> { b.historyGroup(peopleGroup, true); return null; }),
                new Choice("a word typed in the filter", b -> b.historyType("rate") ? null : "the page has no filter box"),
                new Choice("the filter cleared", b -> b.historyType("") ? null : "the page has no filter box"),
                new Choice("the hard times' fold opened", b -> { b.historyFold("hard times", true); return null; }),
                new Choice("the hard times' fold closed", b -> { b.historyFold("hard times", false); return null; }),
                new Choice("the goods' fold opened", b -> { b.historyFold("goods", true); return null; }),
                new Choice("the goods' fold closed", b -> { b.historyFold("goods", false); return null; }),
                new Choice("the log scale on", b -> {
                    b.historyPickOnly(population);
                    return nullIfOffered(b.historyLog(true), "log");
                }),
                new Choice("the log scale off", b -> { b.historyLog(false); return null; }),
                new Choice("real GDP alone, in layers", b -> {
                    b.historyPickOnly("realGdp");
                    b.historyLayers(true);
                    return null;
                }),
                new Choice("the layers off", b -> { b.historyLayers(false); return null; }),
                new Choice("a line hidden in the legend", b -> {
                    // The legend shows the lines of the last draw: draw the preset's before a chip is pressed.
                    b.historyPreset(0);
                    b.drawHistoryKept();
                    b.passes();
                    return b.historyLegendClick(0) && b.historyHidden() == 1 ? null : "the legend's chip did not hide its line";
                }),
                new Choice("the line shown again", b -> b.historyLegendClick(0) && b.historyHidden() == 0 ? null : "the chip did not show its line"),
                new Choice("a range on the chart", b -> { b.historyRange(ham.citybuildersim.ChartModel.ALL); return null; }),
                new Choice("the first range on the chart", b -> { b.historyRange(ham.citybuildersim.ChartModel.RANGES[0]); return null; })));
        if (episode != null) {
            choices.add(new Choice("the window moved to an episode's span", b -> { b.historyWindow(episode[0], episode[1]); return null; }));
        } else {
            System.out.println("  this city has no named episode: the window is not moved to one");
        }
        choices.addAll(List.of(
                new Choice("the window moved by hand", b -> {
                    b.historyWindow(bounds[0] + 50, bounds[0] + 150);
                    return null;
                }),
                new Choice("a line pinned", b -> { b.historyPin(TICKED); return null; }),
                new Choice("a pin taken off", b -> { b.historyUnpin(false); return null; }),
                new Choice("the chart's full screen on", b -> { b.historyFull(true); return null; }),
                new Choice("the chart's full screen off", b -> { b.historyFull(false); return null; }),
                new Choice("the year book's card shown", b -> { b.historyBook("The year book was written (KeptCheck's card)."); return null; }),
                new Choice("the year book's card put away", b -> { b.historyBook(null); return null; })));
        for (Choice c : choices) {
            String d = fx(() -> {
                String premise = c.hand().on(bench);
                if (premise != null) return "the choice could not be made: " + premise;
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept = bench.signature();
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh = bench.signature();
                bench.restoreKeptHistory();
                // ...and a month later, the page drawn over the reshaped one.
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept2 = bench.signature();
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh2 = bench.signature();
                bench.restoreKeptHistory();
                String a = firstDifference(kept, fresh), b = firstDifference(kept2, fresh2);
                return a != null ? a : b;
            });
            if (d != null) System.out.println("  " + c.name() + ": " + d);
            check(c.name() + ": the redraw and the next equal a page built new", d == null);
        }

        crosshair(g, bench);
        countUp(g, bench);
        historyCost(g, bench);
    }

    /** Months landed until the figure has moved (at most twelve): whether it did. */
    private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved) {
        for (int i = 0; i < 12; i++) {
            month(g);
            if (moved.getAsBoolean()) return true;
        }
        return false;
    }

    /** The choice's refusal, as the premise it could not meet, or null when the chart is offered. */
    private static String nullIfOffered(String refusal, String what) {
        return refusal == null ? null : "the " + what + " scale is refused: " + refusal;
    }

    /**
     * Section 18: what a page built new loses and a kept one keeps. The window is the screen's (the
     * ChartModel), so the charts' own state is what a month could take: the crosshair under the pointer
     * (a chart clears it when its scene goes null, and a page built new takes the chart off the scene) and
     * a legend chip under it.
     */
    static void crosshair(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 18: the crosshair and the window survive a month ---");
        int range = ham.citybuildersim.ChartModel.DEFAULT_RANGE;
        // The window on its range follows the newest month, and one put by hand stays where it was put.
        Object[] win = fx(() -> {
            bench.historyPreset(0);
            bench.historyRange(range);
            bench.drawHistoryKept();
            bench.passes();
            int[] end = bench.historyBounds(), on = bench.historyWindow();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            int[] end2 = bench.historyBounds(), followed = bench.historyWindow();
            // Put by hand, a stretch ending before the newest month.
            bench.historyWindow(end2[1] - 240, end2[1] - 120);
            bench.drawHistoryKept();
            bench.passes();
            int[] put = bench.historyWindow();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            int[] end3 = bench.historyBounds(), stayed = bench.historyWindow();
            return new Object[]{end, on, end2, followed, put, end3, stayed};
        });
        int[] end = (int[]) win[0], on = (int[]) win[1], end2 = (int[]) win[2], followed = (int[]) win[3],
                put = (int[]) win[4], end3 = (int[]) win[5], stayed = (int[]) win[6];
        System.out.printf("  on its range: months %d-%d of %d, then %d-%d of %d; put by hand: %d-%d, then %d-%d of %d%n",
                on[0], on[1], end[1], followed[0], followed[1], end2[1], put[0], put[1], stayed[0], stayed[1], end3[1]);
        check("a month landed (the history grew by one)", end2[1] == end[1] + 1 && end3[1] == end2[1] + 1);
        check("a window on the newest month follows it across a kept redraw", on[1] == end[1] && followed[1] == end2[1]);
        check("a window put by hand, ending before the newest month, stays where it was across a kept redraw",
                put[1] < end2[1] && stayed[0] == put[0] && stayed[1] == put[1]);

        // The crosshair: a move of the pointer fired on the big chart's plot (a MOUSE_MOVED event, not a setter).
        Object[] cross = fx(() -> {
            bench.historyRange(range);
            bench.drawHistoryKept();
            bench.passes();
            boolean over = bench.historyHoverPlot();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            boolean afterKept = bench.historyHovering();
            bench.showFreshHistory();
            boolean afterNew = bench.historyHovering();
            bench.restoreKeptHistory();
            return new Object[]{over, afterKept, afterNew};
        });
        check("the pointer over the big chart's plot shows its crosshair", (boolean) cross[0]);
        check("the crosshair is still there after a kept redraw", (boolean) cross[1]);
        check("...and a page built new, which takes the chart off the scene, clears it", !(boolean) cross[2]);

        // A legend chip under the pointer: the same chip, the same tooltip, still under it.
        Object[] chip = fx(() -> {
            bench.drawHistoryKept();
            bench.passes();
            javafx.scene.Node under = bench.historyLegend().get(0);
            Object tip = KeptBench.tooltipOf(under);
            KeptBench.hover(under, true);
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            javafx.scene.Node now = bench.historyLegend().get(0);
            boolean kept = now == under && under.isHover() && KeptBench.tooltipOf(under) == tip && tip != null;
            bench.showFreshHistory();
            javafx.scene.Node fresh = bench.historyLegend().get(0);
            boolean freshIsNew = fresh != under && !fresh.isHover();
            bench.restoreKeptHistory();
            KeptBench.hover(under, false);
            return new Object[]{tip != null, kept, freshIsNew};
        });
        check("a legend chip has a tooltip to keep", (boolean) chip[0]);
        check("a legend chip under the pointer is still the same chip, still under it, with the same tooltip, after a kept redraw",
                (boolean) chip[1]);
        check("...and a page built new has a chip of its own, with no pointer on it", (boolean) chip[2]);
    }

    /**
     * Section 19: the count-up is for a clock that can be followed. A month that lands on a page watched
     * starts the figure's count-up when UserInterface.countsUp() allows - paused, or no faster than
     * COUNT_UP_TOP - and sets it at once above. The real show methods are driven (People's and City
     * History's), the clock's rung set on the window as the speed arrows set it, the page redrawn kept.
     */
    static void countUp(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 19: the count-up only at 1x-10x ---");
        double[] ladder = KeptBench.speedLadder();
        double top = KeptBench.countUpTop();
        StringBuilder wrongPeople = new StringBuilder(), wrongPin = new StringBuilder();
        int countingRungs = 0;
        for (double speed : ladder) if (speed <= top) countingRungs++;
        System.out.printf("  the ladder has %d rungs, %d of them at or under COUNT_UP_TOP (%.0fx)%n", ladder.length, countingRungs, top);

        // People: the headcount on LIVING HERE.
        fx(() -> {
            bench.clock(true, null);
            bench.speed(0);
            bench.screen("showHistoryMenu");
            bench.forgetPeople();
            bench.showPeople();
            return null;
        });
        for (int rung = 0; rung <= ladder.length; rung++) {
            // The last pass is the clock stopped, at the top rung: a month stepped by hand counts at any speed.
            boolean paused = rung == ladder.length;
            int at = paused ? ladder.length - 1 : rung;
            boolean expect = paused || ladder[at] <= top;
            String result = fx(() -> {
                bench.clock(!paused, null);
                bench.speed(at);
                bench.showPeople();
                bench.finishPageAnimations();
                Animation before = bench.peopleCount();
                if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
                bench.showPeople();
                Animation after = bench.peopleCount();
                boolean started = after != null && after != before;
                return started == expect ? null : (started ? "counted" : "did not count") + " at " + ladder[at] + "x"
                        + (paused ? " paused" : "") + ", where it " + (expect ? "should" : "should not");
            });
            if (result != null) wrongPeople.append(wrongPeople.length() == 0 ? "" : "; ").append(result);
        }
        if (wrongPeople.length() > 0) System.out.println("  People: " + wrongPeople);
        check("a month landing on People starts the headcount's count-up exactly at the rungs countsUp() allows, and when paused ("
                + (ladder.length + 1) + " clocks)", wrongPeople.length() == 0);

        // City History: the left pin's figure, pinned to the headcount - a line that moves most months, whose last month the redraw takes up.
        fx(() -> {
            bench.clock(true, null);
            bench.speed(0);
            bench.historyPinLeft("population");
            bench.forgetHistory();
            bench.showHistory();
            return null;
        });
        for (int rung = 0; rung <= ladder.length; rung++) {
            boolean paused = rung == ladder.length;
            int at = paused ? ladder.length - 1 : rung;
            boolean expect = paused || ladder[at] <= top;
            String result = fx(() -> {
                bench.clock(!paused, null);
                bench.speed(at);
                bench.showHistory();
                bench.finishPageAnimations();
                Animation before = bench.pinCount();
                if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
                bench.showHistory();
                Animation after = bench.pinCount();
                boolean started = after != null && after != before;
                return started == expect ? null : (started ? "counted" : "did not count") + " at " + ladder[at] + "x"
                        + (paused ? " paused" : "") + ", where it " + (expect ? "should" : "should not");
            });
            if (result != null) wrongPin.append(wrongPin.length() == 0 ? "" : "; ").append(result);
        }
        if (wrongPin.length() > 0) System.out.println("  City History: " + wrongPin);
        check("a month landing on City History starts the pin's count-up exactly at the rungs countsUp() allows, and when paused ("
                + (ladder.length + 1) + " clocks)", wrongPin.length() == 0);

        // A count-up still running when the clock has been sped past the top: the next month sets the figure at once and stops it.
        String stopped = fx(() -> {
            int slow = 0;
            while (ladder[slow] > top) slow++;
            bench.clock(true, null);
            bench.speed(slow);
            bench.showHistory();
            bench.finishPageAnimations();
            if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
            bench.showHistory();
            Animation running = bench.pinCount();
            if (running == null || running.getStatus() == Animation.Status.STOPPED) return "no count-up was running to stop";
            bench.speed(ladder.length - 1);
            if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
            bench.showHistory();
            String[] figures = bench.pinFigures();
            boolean left = running.getStatus() == Animation.Status.STOPPED && java.util.Objects.equals(figures[0], figures[1]);
            return left ? null : "the pin's count-up is " + running.getStatus() + " and the figure reads " + figures[0] + ", not " + figures[1];
        });
        if (stopped != null) System.out.println("  " + stopped);
        check("a count-up running when a month lands past COUNT_UP_TOP is stopped, and the figure is the month's", stopped == null);
    }

    /** Section 20: what the kept City History month costs, against the same month built new. */
    static void historyCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 20: what the kept City History month costs ---");
        double[] cost = fx(() -> {
            // The page as it opens: the first preset, the default range, nothing folded open.
            bench.historyPreset(0);
            bench.historyRange(ham.citybuildersim.ChartModel.DEFAULT_RANGE);
            bench.drawHistoryKept();
            for (int i = 0; i < 3; i++) bench.passes();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawHistoryKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP1.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshHistory();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptHistory();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_HISTORY_MS);
        String timed = "the kept City History month is within KEPT_HISTORY_MS (" + KEPT_HISTORY_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /* =====================================================================
       INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page
       built new shows it, kept right through what reshapes it, and cheap; then the controls and the hover.
       ===================================================================== */

    /** The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). */
    static final double HANDED_BACK = 0.9;

    /** The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. */
    static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception {
        bench.passes();
        bench.settle();
        bench.finishPageAnimations();
        List<String> kept = bench.signature();
        bench.showFresh(page);
        bench.settle();
        bench.finishPageAnimations();
        List<String> fresh = bench.signature();
        bench.restoreKeptPage();
        return firstDifference(kept, fresh);
    }

    /** Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. */
    static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception {
        System.out.println("\n--- " + n + ": " + page.name() + " is kept ---");
        int firstMade = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            for (int i = 0; i < 3; i++) bench.passes();
            return bench.counts(page)[0];
        });
        System.out.printf("  the first draw made %d nodes through Kept%n", firstMade);
        int sameTop = 0, quiet = 0, madeMost = 0;
        long handedBack = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.pageTop();
                month(g);
                bench.drawKept(page);
                bench.passes();
                int[] c = bench.counts(page);
                return new Object[]{before.equals(bench.pageTop()), c[0], c[1]};
            });
            if ((boolean) r[0]) sameTop++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            handedBack += (int) r[2];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top nodes, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
    }

    /** Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. */
    static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception {
        System.out.println("\n--- " + n + ": " + page.name() + ", the kept page against a page built new, and its pixels ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        String tag = page.name().replace(' ', '-');
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                month(g);
                bench.drawKept(page);
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFresh(page);
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKeptPage();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[0];
            }
            if (r[1] != null) {
                int[] a = (int[]) r[1], b = (int[]) r[2];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, tag + "-kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, tag + "-new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawKept(page);
            bench.passes();
            String was = bench.spoilPage();
            bench.settle();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFresh(page);
            bench.settle();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKeptPage();
            bench.drawKept(page);
            bench.settle();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);
    }

    /**
     * Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are
     * each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. A fold opened
     * has to add to the page (the premise: the fold is on it).
     */
    static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception {
        System.out.println("\n--- " + n + ": what reshapes " + page.name() + " ---");
        int size = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            bench.passes();
            bench.settle();
            return bench.signature().size();
        });
        double worst = Double.MAX_VALUE;
        String worstAt = "";
        for (Choice c : choices) {
            Object[] r = fx(() -> {
                String premise = c.hand().on(bench);
                if (premise != null) return new Object[]{"the choice could not be made: " + premise, 0, 1.0};
                bench.drawKept(page);
                String a = againstNew(bench, page);
                int grown = bench.signature().size();
                month(g);
                bench.drawKept(page);
                int[] counts = bench.counts(page);
                String b = againstNew(bench, page);
                return new Object[]{a != null ? a : b, grown, counts[1] / (double) Math.max(1, counts[0] + counts[1])};
            });
            String d = (String) r[0];
            if (d != null) System.out.println("  " + c.name() + ": " + d);
            check(c.name() + ": the redraw and the next equal a page built new", d == null);
            if (c.opens()) {
                check(c.name() + " adds to the page (" + size + " lines, then " + r[1] + ")", (int) r[1] > size);
            }
            size = (int) r[1] == 0 ? size : (int) r[1];
            if ((double) r[2] < worst) {
                worst = (double) r[2];
                worstAt = c.name();
            }
        }
        System.out.printf("  the month's redraw after a choice handed back %.1f%% of the page's nodes at the fewest (after \"%s\")%n",
                worst * 100, worstAt);
        check("a month's redraw after each choice hands back nine in ten of the page's nodes, or more", worst >= HANDED_BACK);
    }

    /** Section n: what the kept month on the page's home costs, against the same month built new. */
    static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception {
        System.out.println("\n--- " + n + ": what the kept month on " + page.name() + " costs ---");
        double[] cost = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            for (int i = 0; i < 3; i++) bench.passes();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawKept(page);
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP2.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFresh(page);
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptPage();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], budget);
        String timed = "the kept month on " + page.name() + " is within " + dial + " (" + budget + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /** A choice with no side effect on the model: a hand that changes the screen's state only. */
    static Choice choice(String name, Hand hand) {
        return new Choice(name, hand);
    }

    /**
     * The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 between them; the same month built new
     * 10.2 to 19.1 ms).
     */
    static final double KEPT_INFRASTRUCTURE_MS = 9;

    /**
     * The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 between them; the same month built new
     * 9.9 to 19.5 ms).
     */
    static final double KEPT_SERVICES_MS = 13;

    /**
     * The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS,
     * on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 between them; the same month built new
     * 9.1 to 16.0 ms).
     */
    static final double KEPT_LAND_MS = 5;

    /** Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. */
    static String ensureTransit(Game g) throws Exception {
        InfrastructureManager roads = g.getInfrastructureManager();
        if (roads.getTransitCapacity() > 0) return String.format("the city has transit already (%.0f seats, %.0f riders)", roads.getTransitCapacity(), roads.getTransitRiders());
        BuildingsTemplate bus = g.getBuildingManager().getTemplateByName("Bus Network");
        quietly(() -> g.buildStack(bus, 2, true));
        month(g);
        month(g);
        return String.format("the city had no transit: two Bus Networks ordered (%.0f seats, %.0f riders after two months)", roads.getTransitCapacity(), roads.getTransitRiders());
    }

    /** Sections 21 to 24: Infrastructure kept. */
    static void infrastructure(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Infrastructure (KP2) ===");
        System.out.println("  " + fx(() -> ensureTransit(g)));
        KeptBench.Page page = fx(bench::infrastructure);
        pageKept(g, bench, page, 21);
        pageShows(g, bench, page, 22);
        List<Choice> choices = List.of(
                new Choice("the roads' fold opened", b -> { b.infraFold("roads", true); return null; }, true),
                new Choice("the roads' fold closed", b -> { b.infraFold("roads", false); return null; }),
                choice("the Transit page", b -> { b.infraOpen("Transit", "All"); return null; }),
                new Choice("the transit fold opened", b -> { b.infraFold("transit", true); return null; }, true),
                new Choice("the transit fold closed", b -> { b.infraFold("transit", false); return null; }),
                choice("the Freight page", b -> { b.infraOpen("Freight", "All"); return null; }),
                new Choice("the freight fold opened", b -> { b.infraFold("freight", true); return null; }, true),
                new Choice("the freight fold closed", b -> { b.infraFold("freight", false); return null; }),
                choice("Freight on its Haulage division", b -> { b.infraOpen("Freight", "Haulage"); return null; }),
                choice("Freight on its Rail division", b -> { b.infraOpen("Freight", "Rail"); return null; }),
                new Choice("the railway fold opened", b -> { b.infraFold("railway", true); return null; }, true),
                new Choice("the railway fold closed", b -> { b.infraFold("railway", false); return null; }),
                choice("the Ports page", b -> { b.infraOpen("Ports", "All"); return null; }),
                choice("the Roads page again", b -> { b.infraOpen("Roads", "All"); return null; }));
        pageReshaped(g, bench, page, 23, choices);
        pageCost(g, bench, page, 24, KEPT_INFRASTRUCTURE_MS, "KEPT_INFRASTRUCTURE_MS");
    }

    /** The folds each sub-page of Services has, by "system/page" (a page not named has none). */
    static final String[][] SERVICES_FOLDS = {
            {"Health/Overview", "health:sick"}, {"Health/General care", "health:general"},
            {"Health/Childcare", "health:CHILDCARE"}, {"Health/Senior care", "health:SENIOR"},
            {"Health/Death care", "health:death"}, {"Health/Books", "books:health"},
            {"Education/Basic ladder", "education:ladder"}, {"Education/College", "education:COLLEGE"},
            {"Education/University", "education:UNIVERSITY"}, {"Education/Professions", "education:professions"},
            {"Education/Books", "books:education"},
            {"Utilities/Overview", "utilities:power", "utilities:water"}, {"Utilities/Books", "books:utilities"},
            {"Safety/Overview", "safety:causes"}, {"Safety/Police", "safety:police"}, {"Safety/Prisons", "safety:prisons"},
            {"Safety/Books", "books:safety"}};

    /** Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. */
    static void services(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Services (KP2) ===");
        KeptBench.Page page = fx(bench::services);
        pageKept(g, bench, page, 25);
        pageShows(g, bench, page, 26);
        List<Choice> choices = new ArrayList<>();
        for (String[] pageOf : KeptBench.servicesPages()) {
            String area = pageOf[0], sub = pageOf[1];
            choices.add(choice(area + ", " + sub, b -> { b.servicesOpen(area, sub); return null; }));
            for (String[] row : SERVICES_FOLDS) {
                if (!row[0].equals(area + "/" + sub)) continue;
                for (int k = 1; k < row.length; k++) {
                    String key = row[k];
                    choices.add(new Choice(area + ", " + sub + ": the fold " + key + " opened", b -> { b.servicesFold(key, true); return null; }, true));
                    choices.add(new Choice(area + ", " + sub + ": the fold " + key + " closed", b -> { b.servicesFold(key, false); return null; }));
                }
            }
        }
        pageReshaped(g, bench, page, 27, choices);
        pageCost(g, bench, page, 28, KEPT_SERVICES_MS, "KEPT_SERVICES_MS");
    }

    /** Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. */
    static void land(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The land office (KP2) ===");
        KeptBench.Page page = fx(bench::land);
        Object[] has = fx(() -> new Object[]{bench.landListed(), bench.landLandmasses(), bench.landPaidFromVault(), bench.landWaiting()});
        System.out.printf("  %d offers listed; %d sectors waiting on ground; ground on a second landmass (the landmass table, made with new in SectorScreen's cells, so rebuilt every month if it is there): %s%n",
                has[0], has[3], has[1]);
        boolean vault = (boolean) has[2];
        pageKept(g, bench, page, 29);
        pageShows(g, bench, page, 30);
        List<Choice> choices = new ArrayList<>();
        for (int side = 0; side < 4; side++) {
            final int s = side;
            choices.add(choice("side " + side + " picked", b -> { b.landSide(s); return null; }));
        }
        for (int place = 0; place < 6; place++) {
            final int p = place;
            choices.add(choice("the offer at place " + place + " picked", b -> { b.landPlace(p); return null; }));
        }
        choices.add(choice("no offer picked", b -> { b.landPlace(-1); return null; }));
        choices.add(choice("the best 1 to buy", b -> { b.landCount(1); return null; }));
        choices.add(choice("the best 2 to buy", b -> { b.landCount(2); return null; }));
        choices.add(choice("the best 5 to buy", b -> { b.landCount(5); return null; }));
        choices.add(new Choice("the details fold opened", b -> { b.landDetails(true); return null; }, true));
        choices.add(new Choice("the details fold closed", b -> { b.landDetails(false); return null; }));
        choices.add(choice("paid from the vault", b -> { b.landPay(true); return null; }));
        choices.add(choice("paid by converting cash", b -> { b.landPay(false); return null; }));
        choices.add(choice("the way of paying put back", b -> { b.landPay(vault); return null; }));
        choices.add(choice("a plot bought (its receipt, the plots that came onto the shelf)", b -> quietly(b::landBuy)));
        pageReshaped(g, bench, page, 31, choices);
        pageCost(g, bench, page, 32, KEPT_LAND_MS, "KEPT_LAND_MS");

        // The two other pages of the office, each an arrival that forgets the pool: drawn, then a month later. The funding page
        // needs a city that cannot pay for the best offer: the city is brought there by buying through the model (the last
        // of these sections, since it leaves the city poorer).
        System.out.println("\n--- 33: the land office's other pages ---");
        int bought = fx(() -> quietly(() -> bench.landBuyUntilShort(80)));
        List<Integer> ids = fx(bench::landFundingIds);
        System.out.println(bought >= 0 ? "  " + bought + " offers bought through the model until the city fell short of the best one"
                : "  80 offers bought and the city still pays for the best one");
        List<KeptBench.Page> others = new ArrayList<>();
        if (ids != null) others.add(fx(() -> bench.landFunding(ids)));
        else System.out.println("  the city can pay for the whole shelf: there is no funding page to draw");
        others.add(fx(() -> bench.landFellShort("bond", 1, 3)));
        others.add(fx(() -> bench.landFellShort(null, 0, 2)));
        for (KeptBench.Page other : others) {
            // (In the game the fell-short page is never redrawn over itself: a month landing draws the office. It is drawn so here, as every page is.)
            pageReshaped(g, bench, other, 33, List.of(choice(other.name() + " opened", b -> null)));
        }
    }

    /**
     * Section 34: a control keeps the player's hand. Transit's fare dial, through the screen's real show method: pressed, a
     * slider is held and a month landing under it leaves its thumb, its reading and its ladder's shown value where the player
     * put them; released, it stages the thumb and the page is a page built new; a value set outside any draw reaches the
     * reading of the latest draw. And City History's filter box keeps its text, its caret and its focus.
     */
    static void controls(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 34: a control keeps the player's hand ---");
        KeptBench.Page page = fx(bench::infrastructure);
        Object[] fare = fx(() -> {
            bench.home(page);
            bench.infraOpen("Transit", "All");
            bench.showInfrastructure();
            bench.passes();
            javafx.scene.control.Slider bar = bench.slider();
            if (bar == null) return null;
            double step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
            double thumb = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
            String before = KeptBench.readingOf(bar).getText();
            // (a) pressed, dragged two steps, a month lands.
            KeptBench.press(bar);
            boolean heldPressed = KeptBench.held(bar);
            bar.setValueChanging(true);
            bar.setValue(thumb);
            String dragged = KeptBench.readingOf(bar).getText();
            month(g);
            bench.showInfrastructure();
            bench.passes();
            boolean sameBar = bench.slider() == bar;
            // The reading is in the month's money (the fare dial is a price at founding prices, shown at the level the
            // month expects): what a dial built at the thumb says this month is the reference, staged for the one build.
            double stagedBefore = bench.stagedFare();
            String kept = KeptBench.readingOf(bar).getText();
            double shown = KeptBench.shownBy(bar);
            boolean heldStill = KeptBench.held(bar);
            double valueNow = bar.getValue();
            bench.stageFare(thumb);
            bench.showFresh(page);
            String atThumb = KeptBench.readingOf(bench.slider()).getText();
            bench.restoreKeptPage();
            bench.unstageFare();
            Object[] a = {heldPressed, sameBar, valueNow, thumb, kept, dragged, before, shown, heldStill, stagedBefore, atThumb};
            // (b) released: the thumb is staged, the screen redrawn, the page the page built new.
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            String differs = againstNew(bench, page);
            Object[] b = {KeptBench.held(bar), bench.stagedFare(), bench.slider() == bar, differs};
            // (c) several kept months, then a value set outside any draw: the reading of the latest draw follows.
            for (int i = 0; i < 4; i++) {
                month(g);
                bench.showInfrastructure();
                bench.passes();
            }
            javafx.scene.control.Slider later = bench.slider();
            double other = thumb - 2 * step >= bar.getMin() - 1e-9 ? thumb - 2 * step : thumb + 2 * step;
            later.setValue(other);
            String set = KeptBench.readingOf(later).getText();
            bench.stageFare(other);
            bench.showFresh(page);
            String built = KeptBench.readingOf(bench.slider()).getText();
            bench.restoreKeptPage();
            Object[] c = {later == bar, set, built, KeptBench.readingOf(later).getText()};
            bench.unstageFare();
            bench.showInfrastructure();
            bench.passes();
            return new Object[]{a, b, c, bench.stagedFare()};
        });
        if (fare == null) {
            check("the Transit page has a fare dial (a slider) to hold", false);
        } else {
            Object[] a = (Object[]) fare[0], b = (Object[]) fare[1], c = (Object[]) fare[2];
            System.out.printf("  dragged two steps: reading \"%s\" -> \"%s\"; a month later \"%s\" (a dial built at the thumb: \"%s\"), the ladder shows %.4f for a thumb at %.4f%n",
                    a[6], a[5], a[4], a[10], (double) a[7], (double) a[3]);
            check("a slider pressed is held", (boolean) a[0]);
            check("a month landing on a held slider leaves the same slider node, still held", (boolean) a[1] && (boolean) a[8]);
            check("...its value still the thumb's (" + a[2] + ")", Math.abs((double) a[2] - (double) a[3]) < 1e-9);
            check("...the reading the thumb's words in the month's money (\"" + a[4] + "\", not the staged \"" + a[6] + "\")",
                    a[4].equals(a[10]) && !a[4].equals(a[6]));
            check("...Ladder.shown() the thumb's value, not the staged one", Math.abs((double) a[7] - (double) a[3]) < 1e-9);
            check("...and nothing is staged before the release", Double.isNaN((double) a[9]));
            check("a slider released is not held", !(boolean) b[0]);
            check("...the fare is staged at the thumb (" + b[1] + ")", Math.abs((double) b[1] - (double) a[3]) < 1e-9);
            check("...and the page is the same slider, kept, equal to a page built new", (boolean) b[2] && b[3] == null);
            if (b[3] != null) System.out.println("  " + b[3]);
            check("after several kept months the one slider node still carries the dial", (boolean) c[0]);
            check("a value set outside any draw updates the reading of the latest draw (\"" + c[1] + "\", a dial built at it says \""
                    + c[2] + "\")", c[1].equals(c[2]));
            check("...and it is the reading on the page", c[3].equals(c[1]));
            check("the fare is unstaged at the end of the section", Double.isNaN((double) fare[3]));
        }

        // The filter box: City History's, through its real show method and the focus it takes back.
        String how = fx(() -> {
            bench.historyPreset(0);
            bench.showHistory();
            bench.passes();
            return bench.historyFilterFocus("rate");
        });
        System.out.println("  the filter box took the focus by: " + how);
        check("City History's filter box takes the focus (" + how + ")", how.equals("requestFocus") || how.equals("setFocused by reflection"));
        Object[] box = fx(() -> {
            Object[] was = bench.historyFilterState();
            month(g);
            bench.showHistory();
            bench.passes();
            Object[] now = bench.historyFilterState();
            return new Object[]{was, now};
        });
        Object[] was = (Object[]) box[0], now = (Object[]) box[1];
        check("a month landing keeps the filter box (the same node)", was != null && now != null && was[0] == now[0]);
        check("...its text (\"" + (now == null ? null : now[1]) + "\")", now != null && "rate".equals(now[1]));
        check("...its caret (" + (was == null ? null : was[2]) + " before, " + (now == null ? null : now[2]) + " after)",
                now != null && was != null && was[2].equals(now[2]));
        check("...and its focus", now != null && (boolean) now[3]);
        Object[] set = fx(() -> {
            // Kept.text on a field that has the focus leaves it alone, and sets one that has none.
            String focused = bench.historyFilterSet("zzz");
            bench.historyFilterBlur();
            String blurred = bench.historyFilterSet("zzz");
            bench.historyFilterSet("");
            bench.showHistory();
            bench.passes();
            return new Object[]{focused, blurred};
        });
        check("Kept.text leaves a field with the focus as the player typed it (\"" + set[0] + "\")", "rate".equals(set[0]));
        check("...and sets a field that has none (\"" + set[1] + "\")", "zzz".equals(set[1]));
    }

    /** Section 35: the hover kept. A node under a still pointer stays the same node in the look its own mouse-entered gives it, and in the rest look once the pointer is off. */
    static void hover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 35: the hover is kept ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        // City History: a row that moves the chart (the hard times open), and a chart card's head (a line pinned).
        fx(() -> {
            bench.historyPreset(0);
            bench.historyFold("hard times", true);
            bench.historyPinLeft(TICKED);
            bench.drawHistoryKept();
            bench.passes();
            return null;
        });
        for (String kind : List.of("rowGoes", "chartCardHead", "door")) {
            String d = fx(() -> bench.hoverKept(kind, bench::drawHistoryKept));
            hoverLine(found, "City History", kind, d);
        }
        fx(() -> {
            bench.historyFold("hard times", false);
            return null;
        });
        // The three pages' own.
        KeptBench.Page infra = fx(bench::infrastructure);
        fx(() -> {
            bench.home(infra);
            bench.drawKept(infra);
            bench.passes();
            return null;
        });
        for (String kind : List.of("limitCell", "door")) {
            String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(infra)));
            hoverLine(found, "Infrastructure", kind, d);
        }
        KeptBench.Page services = fx(bench::services);
        String doorCard = null;
        for (String[] sub : KeptBench.servicesPages()) {
            String d = fx(() -> {
                bench.home(services);
                bench.servicesOpen(sub[0], sub[1]);
                bench.drawKept(services);
                bench.passes();
                return bench.hoverKept("doorCard", () -> bench.drawKept(services));
            });
            if (!KeptBench.ABSENT.equals(d)) {
                hoverLine(found, "Services, " + sub[0] + ", " + sub[1], "doorCard", d);
                doorCard = d == null ? "ok" : d;
                break;
            }
        }
        if (doorCard == null) hoverLine(found, "Services", "doorCard", KeptBench.ABSENT);
        KeptBench.Page land = fx(bench::land);
        fx(() -> {
            bench.home(land);
            bench.drawKept(land);
            bench.passes();
            return null;
        });
        for (String kind : List.of("landRow", "oreCard")) {
            String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(land)));
            hoverLine(found, "the land office", kind, d);
        }
        for (String kind : List.of("rowGoes", "door", "limitCell", "chartCardHead", "landRow", "oreCard", "doorCard")) {
            check("a " + kind + " was found on a page of this city, to put the pointer on", found.getOrDefault(kind, false));
        }
    }

    /** One hover result: printed, counted as found when the page had the node, and a check that it held. */
    private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result) {
        if (KeptBench.ABSENT.equals(result)) {
            System.out.println("  " + where + " has no " + kind + " this month: not checked there");
            return;
        }
        found.put(kind, true);
        if (result != null) System.out.println("  " + where + ", " + kind + ": " + result);
        check(where + ": the " + kind + " under the pointer keeps its look across a kept redraw, and loses it when the pointer goes", result == null);
    }

    /** A notice raised now, unread, in the inbox. */
    private static Notice add(Game g, String key, String title) {
        Notice n = new Notice(key, title, List.of("first line of " + key, "", "third line"), g.getMonth());
        g.getInbox().all().add(n);
        return n;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== KeptCheck: the People page, the chrome, City History, Infrastructure, Services and the land office kept between redraws (study UP4, UP2, KP1, KP2) ===");
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
        String timed = "the kept month is within KEPT_MONTH_MS (" + KEPT_MONTH_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;

        chrome(g, bench);
        panels(g, bench);
        history(g, bench);
        infrastructure(g, bench);
        services(g, bench);
        land(g, bench);
        controls(g, bench);
        hover(g, bench);

        System.out.println("\n" + (fails == 0 ? "ALL OK" : fails + " FAILED"));
        Platform.exit();
        System.exit(fails);
    }
}
