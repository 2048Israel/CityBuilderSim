package ham.citybuildersim;

import ham.citybuildersim.ui.FollowBench;
import ham.citybuildersim.ui.SteadyBench;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * The window holds still: a redraw with nothing changed changes nothing on screen, and a month keeps the player's place.
 *
 * WHY (UX1, 2026-11-27). Jerus, on the interface rework: "the ui work watches out for scrolling issues, and issues
 * were the screen flickers or expands without stopping". KeptCheck holds each kept page against the same page built
 * new; this holds the whole window - the header, the rail, the page and its panels, as the player has them - on every
 * page and sub-page the picture tool walks (ui.PagePictures), and it is the guard of every UI batch after UX1. Its
 * reds on the pages as UX1 found them are the audit's stability faults, each a known red owned by the UX row that
 * mends that page (docs/queue/UX1-notes.md).
 *
 * WHAT THIS HAS TO PROVE, on the playtest's young town in the real window at 1920x1080:
 *
 *   0. THE MEASURE SEES A FAULT. On a scene of its own, a still redraw shows nothing, and each fault caused once - a
 *      node put in another's place, a box made taller, the scroller moved - is seen. Without this a green below
 *      could be a measure that sees nothing.
 *
 *   1. AN UNCHANGED REDRAW MOVES NOTHING. Every page drawn again with no month between keeps every node where it
 *      was, to half a pixel, and every scroller where it stood. A node that moves when nothing changed is the
 *      flicker the player sees.
 *
 *   2. AN UNCHANGED REDRAW MAKES NOTHING NEW. The same node objects are on the scene after as before: none made,
 *      none let go. A node rebuilt is a node styled and painted from nothing, which is a flicker even where it
 *      lands in the same place (Kept's rules).
 *
 *   3. NOTHING GROWS. Three unchanged redraws in a row leave the page's own content the same size, and the
 *      window's root is never wider or taller than the window: "expands without stopping".
 *
 *   4. A MONTH KEEPS THE PLACE. The page's scroller set a third of the way down, a month played and the page
 *      redrawn as the clock does, the scroller is at the same pixel (Kept: "the scroll never moves").
 *
 * Every page is named on its line, so a red is one page's and a known red is scoped to it.
 */
public class UiSteadyCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s%s%n", label, ok ? "OK" : "FAIL", detail.isEmpty() ? "" : "  " + detail);
    }

    /** The fraction of its run a page's scroller is set to before the month (section 4). */
    static final double PLACE = 1.0 / 3;

    /** Unchanged redraws in a row for section 3. */
    static final int REDRAWS = 3;

    public static void main(String[] args) throws Exception {
        String cannot = SteadyBench.startJavaFx();
        if (cannot != null) {
            out.println("UiSteadyCheck: skipped, JavaFX cannot start here (" + cannot + ")");
            System.exit(0);
        }
        try {
            run();
        } catch (Throwable t) {
            t.printStackTrace(out);
            fails++;
        }
        out.println(fails == 0 ? "UiSteadyCheck: all OK" : "UiSteadyCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void run() throws Exception {
        out.println("--- 0. the measure sees a fault: a still redraw shows nothing, each fault caused is seen ---");
        int[][] f = SteadyBench.fixtures();
        check("a still redraw: no node made, lost or moved, no scroller moved, nothing grew", f[0][0] + f[0][1] + f[0][2] + f[0][3] + f[0][4] == 0);
        check("a label put in another's place: as many nodes made as lost, and some", f[1][0] > 0 && f[1][0] == f[1][1]);
        check("a box made taller moves the nodes under it", f[1][2] > 0);
        check("the scroller moved is seen", f[1][3] == 1);
        check("the page grew is seen", f[1][4] == 1);

        GameFiles files = GameFiles.scratch("uisteady");
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        Game g;
        SteadyBench bench;
        try {
            g = FollowBench.fx(() -> LongPlaytest.probeTodayCity(files, null));
            bench = new SteadyBench(g);
        } finally {
            System.setOut(real);
        }
        List<String> pages = bench.pages();
        out.println("the window at " + bench.size() + ", the playtest's young town at month " + g.getMonth() + ", " + pages.size() + " pages");

        int[] movedPages = {0}, madePages = {0}, grewPages = {0}, placePages = {0};
        StringBuilder s1 = new StringBuilder(), s2 = new StringBuilder(), s3 = new StringBuilder(), s4 = new StringBuilder();
        PrintStream keep = out;
        for (String page : pages) {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            try {
                bench.open(page);
                SteadyBench.Shape a = bench.shape();
                bench.redraw();
                SteadyBench.Shape b = bench.shape();
                SteadyBench.Diff d = SteadyBench.compare(a, b);
                s1.append(line("1. " + page + ": an unchanged redraw moves no node and no scroller",
                        d.moved.isEmpty() && d.scrolled.isEmpty(),
                        d.moved.size() + " moved, " + d.scrolled.size() + " scrollers: " + SteadyBench.Diff.few(d.moved.isEmpty() ? d.scrolled : d.moved)));
                s2.append(line("2. " + page + ": an unchanged redraw makes no node new and lets none go (" + a.count() + " nodes)",
                        d.made.isEmpty() && d.lost.isEmpty(),
                        d.made.size() + " made, " + d.lost.size() + " lost: " + SteadyBench.Diff.few(d.made.isEmpty() ? d.lost : d.made)));

                SteadyBench.Shape last = b;
                double tallest = b.pageHeight, widest = b.pageWidth;
                boolean grew = false, overflow = b.rootWidth > b.windowWidth + SteadyBench.STILL || b.rootHeight > b.windowHeight + SteadyBench.STILL;
                for (int i = 0; i < REDRAWS; i++) {
                    bench.redraw();
                    SteadyBench.Shape c = bench.shape();
                    if (c.pageHeight > last.pageHeight + SteadyBench.STILL || c.pageWidth > last.pageWidth + SteadyBench.STILL) grew = true;
                    overflow |= c.rootWidth > c.windowWidth + SteadyBench.STILL || c.rootHeight > c.windowHeight + SteadyBench.STILL;
                    tallest = Math.max(tallest, c.pageHeight);
                    widest = Math.max(widest, c.pageWidth);
                    last = c;
                }
                s3.append(line("3. " + page + ": " + REDRAWS + " unchanged redraws grow nothing, the root within the window",
                        !grew && !overflow,
                        String.format("page %.1fx%.1f then up to %.1fx%.1f, root %.1fx%.1f in %.1fx%.1f",
                                b.pageWidth, b.pageHeight, widest, tallest, last.rootWidth, last.rootHeight, last.windowWidth, last.windowHeight)));

                double before = bench.placePage(PLACE);
                if (before >= 0) {
                    bench.month();
                    double[] after = bench.pageHold();
                    // Held: at the same pixel, or, where the month made the page shorter than the place it stood at, at its new end.
                    boolean shorter = after[0] >= 0 && after[1] < before - SteadyBench.STILL;
                    boolean ok = after[0] >= 0 && (Math.abs(after[0] - before) <= SteadyBench.STILL
                            || shorter && Math.abs(after[0] - after[1]) <= SteadyBench.STILL);
                    s4.append(line("4. " + page + ": a month keeps the page's scroller at its pixel", ok,
                            after[0] < 0 ? String.format("%.1f px before, no page scroller after", before)
                                    : String.format("%.1f px before, %.1f after, of %.1f", before, after[0], after[1])));
                    if (!ok) placePages[0]++;
                }
                if (!d.moved.isEmpty() || !d.scrolled.isEmpty()) movedPages[0]++;
                if (!d.made.isEmpty() || !d.lost.isEmpty()) madePages[0]++;
                if (grew || overflow) grewPages[0]++;
            } catch (Exception e) {
                Throwable t = e.getCause() == null ? e : e.getCause();
                s1.append(line("1. " + page + ": the page opens and redraws", false, t.toString()));
            } finally {
                System.setOut(keep);
            }
        }
        out.println("--- 1. an unchanged redraw moves nothing: every node to half a pixel, every scroller ---");
        out.print(s1);
        out.println("--- 2. an unchanged redraw makes nothing new: the same nodes on the scene after as before ---");
        out.print(s2);
        out.println("--- 3. nothing grows: " + REDRAWS + " unchanged redraws, the page's content and the window's root ---");
        out.print(s3);
        out.println("--- 4. a month keeps the place: the page's scroller a third down, a month played and redrawn ---");
        out.print(s4);
        out.printf("pages that move %d, rebuild %d, grow %d, lose their place %d, of %d%n",
                movedPages[0], madePages[0], grewPages[0], placePages[0], pages.size());
        bench.close();
    }

    /** One assertion's line, counted, for a section printed after the walk. */
    static String line(String label, boolean ok, String detail) {
        if (!ok) fails++;
        return String.format("%-96s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : "  " + detail);
    }
}
