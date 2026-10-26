package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The long bridges checked (ET6, 2026-10-26; docs/queue/ET6-notes.md): the survey of a crossing on grids of its own making and
 * on the Earth, what a span costs, the order in a played city on two landmasses, the bridge that links, the bridge nobody
 * can take down, the save, a generated world that has no far end, and the real strait. Not part of the game.
 *
 * WHY. Until ET6 a landmass was linked by two ports and nothing else, and a bridge was the map's affair alone: a highway
 * crossed 1,020 m of fresh or shallow water because CityRuns drew it so. A long bridge is the model's: an order between two
 * landmasses that the model finds a crossing for, prices by length and depth, builds as one contract and then links. Every
 * rule of that is caused here on ground a fixture lays out plot by plot (GridGround: the survey is a function of a small
 * interface, so the water, the banks, the depths and the ownership are the harness's), from both sides of each threshold
 * and against the model's own constants, never numbers typed.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SURVEY. A straight SHALLOW channel W plots wide between the city's own shore and an owned island gives the chain
 *      the step arithmetic says, (W + 1) steps of 30 m and two corner plots; a 45-degree channel likewise, in diagonal steps;
 *      fresh and shallow water mixed is one chain; one deep plot in the only channel is refusal 1, with that plot's depth and
 *      its distance from the far shore; a deep plot the chain can go round gives the longer detour; a near shore of steep or
 *      unowned ground, or ground on a landmass no one has linked, is no landing (and foothill is); refusal 2 both ways (no owned
 *      ground on the far landmass, and owned ground that touches no water); refusal 3 both ways (found, but over the cap, at the
 *      cap's own plot; and cut by the search window); refusal 4 on one landmass; the corner list replays to the chain; the
 *      words of all four are the card's, word for word. The worst case, a bay as wide as a bridge can be, is timed.
 *   2. THE PRICE. The deck rate is the base rate in the shallows and on fresh water, the Confederation Bridge's derived rate at
 *      its 35 m, linear between and beyond and never under the base; a metre costs more in rail than in highway than in road at
 *      every depth, and more in deeper water; a span is its steps' lengths at the rate of their ends' depths; points are
 *      dollars over the thousands the file counts in and the founding price a point. The three records of the notes are printed.
 *   3. THE ORDER, in a played city on two landmasses (Rotterdam with Goeree-Overflakkee's square, the fixture of LandmassCheck;
 *      the crossing is handed to the survey through Game.setBridgeGroundForTest(), because the strait the fixture holds
 *      between the two squares is not a crossing the harness can cause): with no path the order is NO_BRIDGE before land and
 *      money, no bond offered, nothing moved; the quote's span points are BridgeWorks' arithmetic at the template's founding
 *      price; ordered, the island stays unlinked every month the bridge is on site, which takes the builders months; stood,
 *      the island is linked, its free ground counts as room and the linked set's version moved; an order of two is two spans
 *      on one survey.
 *   4. NO DEMOLITION. A bridge that stands cannot be demolished (demolishable() 0, the words), and the island is still linked.
 *   5. THE SAVE. A standing bridge and one on site survive a save and a load with the island linked, the resave is the same
 *      bytes but for its time and name, the key is absent with no bridge, and a save with the key stripped loads with none.
 *   6. A GENERATED WORLD. Every bridge card refuses with refusal 4 and nothing else in the city moves (a month played with and
 *      without asking is the same).
 *   7. THE FULL FILE, when earth-2.bin is named (-Dcbs.earth=<path>): a city at Borden-Carleton, Prince Edward Island, that buys
 *      Cape Jourimain, New Brunswick, across the Northumberland Strait is surveyed - its length against the real 12.9 km, its
 *      depths, or the refusal and why - and so is the Rotterdam delta's strait to Goeree-Overflakkee; the survey answers inside
 *      SURVEY_BUDGET_MS.
 *
 * Every premise is asserted before what rests on it.
 *
 * @author Jerus
 */
public class BridgeCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /** The seed every world here is drawn with: the default world's. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    /**
     * How long a survey may take on the Earth, milliseconds: 10,000. Measured on the PC (section 7's and section 3's printed times):
     * the Northumberland Strait 0.2 s (459,000 plots over 550 tiles), the Rotterdam delta 0.03 s, and the fixture's window run out
     * to the 45 km limit over 3.3 million plots of open water 1.1 to 1.7 s - the worst a search can be, since it stops at the
     * limit - so a machine several times slower passes and a search that wandered without a limit does not.
     */
    static final long SURVEY_BUDGET_MS = 10_000;

    /** The worst case of a synthetic bay a bridge can be as long as, milliseconds: the survey of 1.7 million plots of open shallow water. */
    static final long BAY_BUDGET_MS = 10_000;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-112s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-112s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Two figures are one within a hundredth of a metre or a part in a billion. */
    static boolean same(double a, double b) { return Math.abs(a - b) <= Math.max(0.01, 1e-9 * Math.abs(b)); }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(real); }
    }

    /* =====================================================================
       THE GROUND - a grid of the harness's own making
       ===================================================================== */

    /** The depth the grid gives a plot nobody set: deep sea. */
    static final double DEEP_M = 100;

    /**
     * A BridgeSurvey.Ground laid out plot by plot: what is not set is deep sea, unowned. Home is landmass 1 and the island 2 by
     * convention here, the linked set whatever `linked` says. Sparse, a tile at a time.
     */
    static class GridGround implements BridgeSurvey.Ground {
        final int home;
        int[] linked;
        final Map<Long, BridgeSurvey.Tile> tiles = new HashMap<>();
        final Map<Long, double[]> depths = new HashMap<>();
        long tileReads;

        GridGround(int home, int... linked) {
            this.home = home;
            this.linked = linked;
        }

        private BridgeSurvey.Tile tileOf(long x, long y, boolean make) {
            long k = BridgeSurvey.key(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE));
            BridgeSurvey.Tile t = tiles.get(k);
            if (t == null && make) {
                t = new BridgeSurvey.Tile();
                t.clear();
                tiles.put(k, t);
                double[] d = new double[World.TILE * World.TILE];
                Arrays.fill(d, DEEP_M);
                depths.put(k, d);
            }
            return t;
        }

        /** Sets a plot. */
        GridGround plot(long x, long y, byte cover, byte relief, int landmass, boolean owned, double depth) {
            BridgeSurvey.Tile t = tileOf(x, y, true);
            long k = BridgeSurvey.key(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE));
            int i = (int) (Math.floorMod(y, (long) World.TILE) * World.TILE + Math.floorMod(x, (long) World.TILE));
            t.cover[i] = cover;
            t.relief[i] = relief;
            t.landmass[i] = landmass;
            t.owned[i] = owned;
            depths.get(k)[i] = depth;
            return this;
        }

        /** Owned or unowned dry ground of a landmass and relief over [x0, x1) x [y0, y1). */
        GridGround dry(long x0, long y0, long x1, long y1, int landmass, byte relief, boolean owned) {
            for (long y = y0; y < y1; y++) for (long x = x0; x < x1; x++) plot(x, y, World.GRASS, relief, landmass, owned, 0);
            return this;
        }

        /** Water over [x0, x1) x [y0, y1): FRESH (depth 0), SHALLOW or SALT, `depth` metres. */
        GridGround water(long x0, long y0, long x1, long y1, byte cover, double depth) {
            for (long y = y0; y < y1; y++) for (long x = x0; x < x1; x++) plot(x, y, cover, World.FLAT, 0, false, cover == World.FRESH ? 0 : depth);
            return this;
        }

        /** The cover of a plot as set (SALT where nobody did). */
        byte coverAt(long x, long y) {
            BridgeSurvey.Tile t = tileOf(x, y, false);
            return t == null ? World.SALT : t.cover[(int) (Math.floorMod(y, (long) World.TILE) * World.TILE + Math.floorMod(x, (long) World.TILE))];
        }

        @Override public int home() { return home; }

        @Override public int[] linkedIds() { return linked; }

        @Override public long[] ownedTiles() {
            List<Long> keys = new ArrayList<>();
            for (Map.Entry<Long, BridgeSurvey.Tile> e : tiles.entrySet()) {
                for (boolean o : e.getValue().owned) if (o) { keys.add(e.getKey()); break; }
            }
            long[] out = new long[keys.size()];
            for (int i = 0; i < out.length; i++) out[i] = keys.get(i);
            java.util.Arrays.sort(out);
            return out;
        }

        @Override public void tile(long tx, long ty, BridgeSurvey.Tile out) {
            tileReads++;
            out.clear();
            long k = BridgeSurvey.key(tx, ty);
            BridgeSurvey.Tile t = tiles.get(k);
            if (t == null) return;
            System.arraycopy(t.cover, 0, out.cover, 0, t.cover.length);
            System.arraycopy(t.relief, 0, out.relief, 0, t.relief.length);
            System.arraycopy(t.landmass, 0, out.landmass, 0, t.landmass.length);
            System.arraycopy(t.owned, 0, out.owned, 0, t.owned.length);
        }

        @Override public double depthM(long x, long y) {
            long k = BridgeSurvey.key(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE));
            double[] d = depths.get(k);
            return d == null ? DEEP_M : d[(int) (Math.floorMod(y, (long) World.TILE) * World.TILE + Math.floorMod(x, (long) World.TILE))];
        }
    }

    /** The far island's landmass id and home's, in every grid here. */
    static final int ISLAND = 2, HOME = 1;

    /**
     * A straight channel of `w` plots along x between the island (west) and home (east), `rows` rows wide (y from 0), banks of
     * dry unowned ground on its north and south so that the water is the only way across. `cover` and `depth` are the water's.
     */
    static GridGround channel(int w, int rows, byte cover, double depth) {
        return channelInto(new GridGround(HOME, HOME), HOME, ISLAND, w, rows, cover, depth);
    }

    /** ...laid on a ground already made, with the landmasses' ids as that ground's game has them. */
    static GridGround channelInto(GridGround g, int homeId, int islandId, int w, int rows, byte cover, double depth) {
        g.dry(-4, -1, 0, rows + 1, islandId, World.FLAT, true);
        g.dry(0, -1, w, 0, 0, World.FLAT, false);
        g.dry(0, rows, w, rows + 1, 0, World.FLAT, false);
        g.water(0, 0, w, rows, cover, depth);
        g.dry(w, -1, w + 4, rows + 1, homeId, World.FLAT, true);
        return g;
    }

    /** The ground the played city's survey is handed (Game.setBridgeGroundForTest()): the harness's terrain and ownership, the game's own home and linked set, read live. */
    static final class LiveGround extends GridGround {
        final Landmasses lms;

        LiveGround(Landmasses lms) {
            super(lms.home(), lms.home());
            this.lms = lms;
        }

        @Override public int home() { return lms.home(); }

        @Override public int[] linkedIds() { return lms.linkedIds(); }
    }

    /** The chain a result's corner list names, plot by plot: each leg walked in its direction one plot at a time. */
    static List<long[]> replay(long[] corners) {
        List<long[]> path = new ArrayList<>();
        for (int c = 0; c + 3 < corners.length; c += 2) {
            long x = corners[c], y = corners[c + 1], x1 = corners[c + 2], y1 = corners[c + 3];
            long dx = Long.signum(x1 - x), dy = Long.signum(y1 - y);
            if (path.isEmpty()) path.add(new long[] { x, y });
            while (x != x1 || y != y1) {
                x += dx;
                y += dy;
                path.add(new long[] { x, y });
            }
        }
        return path;
    }

    /** The length of a replayed chain in metres: 30 a step along a row or column, 30 x sqrt(2) a diagonal. */
    static double lengthOf(List<long[]> path) {
        double m = 0;
        for (int i = 1; i < path.size(); i++) {
            boolean diagonal = path.get(i)[0] != path.get(i - 1)[0] && path.get(i)[1] != path.get(i - 1)[1];
            m += diagonal ? World.PLOT_M * Math.sqrt(2) : World.PLOT_M;
        }
        return m;
    }

    /** The names a harness sentence calls the landmasses: "Island 2" for the island, home's own words for home. */
    static String namer(int id) { return id == HOME ? BridgeView.landmassWords(true, "Home") : BridgeView.landmassWords(false, "Island " + id); }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        if (args.length > 0 && args[0].equals("--earth")) {
            // Section 7 alone (not part of the suite): for measuring the full file.
            theEarth();
            out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
            System.exit(fails == 0 ? 0 : 1);
        }
        theSurvey();
        thePrice();
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        out.println("\n--- the fixture ---");
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null);
        if (fixture != null) {
            EarthData data = EarthData.of(fixture);
            String id = EarthTerrain.use(data);
            World.Key rotterdam = World.Key.earth(SEED, LandmassCheck.RLAT, LandmassCheck.RLON, id);
            Island island = islandFor(rotterdam);
            theOrder(island);
            noDemolition(island);
            theSave(island);
        }
        generatedWorld();
        theEarth();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE SURVEY
       ===================================================================== */

    static void theSurvey() {
        out.println("--- 1. the survey: the chain on grids of the harness's own making, and the four refusals in the card's words ---");
        double step = World.PLOT_M, diagonal = World.PLOT_M * Math.sqrt(2);

        // A straight channel.
        int w = 7;
        GridGround straight = channel(w, 3, World.SHALLOW, 8);
        BridgeSurvey.Result r = BridgeSurvey.survey(straight, ISLAND);
        report("a straight SHALLOW channel 7 plots wide between the island and home: a chain of W + 1 steps of 30 m, found",
                r.ok() && same(r.lengthM(), (w + 1) * step) && r.stepM().length == w + 1, String.format("%.2f m, %d steps", r.lengthM(), r.stepM().length));
        List<long[]> path = replay(r.corners());
        check("...two corner plots only - the two landings - for a straight channel, not a staircase: the heading tie goes to the heading already taken",
                r.corners().length == 4 && r.farX() == r.corners()[0] && r.farY() == r.corners()[1] && r.nearX() == r.corners()[2] && r.nearY() == r.corners()[3]);
        check("...its far landing on the island's last column and its near landing on home's first, the same row",
                r.farX() == -1 && r.nearX() == w && r.farY() == r.nearY() && r.nearLandmass() == HOME && r.far() == ISLAND);
        check("...the corner list replays to the chain: one plot more than steps, every plot between the landings water, none deep or dry",
                path.size() == r.stepM().length + 1 && same(lengthOf(path), r.lengthM())
                        && path.subList(1, path.size() - 1).stream().allMatch(p -> straight.coverAt(p[0], p[1]) == World.SHALLOW));
        report("...it crosses water of 8 m: the deepest and the mean are the water's, the landings 0", same(r.deepestM(), 8) && same(r.meanM(), 8)
                && r.plotDepthM()[0] == 0 && r.plotDepthM()[r.plotDepthM().length - 1] == 0 && r.plotDepthM()[1] == 8, String.format("deepest %.1f, mean %.1f", r.deepestM(), r.meanM()));
        check("...and the words of a crossing that can go ahead say the length and the depth",
                BridgeView.crossingWords(r, BridgeCheck::namer).equals("To Island 2: 240 m over water up to 8 m deep (mean 8 m)")
                        && BridgeView.refusal(r, BridgeCheck::namer) == null);

        // The same channel in fresh water.
        BridgeSurvey.Result fresh = BridgeSurvey.survey(channel(w, 3, World.FRESH, 0), ISLAND);
        check("the same channel of FRESH water is a chain just the same, with no depth to cross: the words say fresh water",
                fresh.ok() && same(fresh.lengthM(), (w + 1) * step) && fresh.deepestM() == 0 && fresh.meanM() == 0
                        && BridgeView.crossingWords(fresh, BridgeCheck::namer).equals("To Island 2: 240 m over fresh water"));

        // Fresh and shallow mixed.
        GridGround mixed = channel(8, 3, World.SHALLOW, 12);
        mixed.water(0, 0, 4, 3, World.FRESH, 0);
        BridgeSurvey.Result m = BridgeSurvey.survey(mixed, ISLAND);
        report("a channel half FRESH and half SHALLOW is one chain of the same length; the mean depth counts the fresh plots as none",
                m.ok() && same(m.lengthM(), 9 * step) && same(m.deepestM(), 12) && same(m.meanM(), 6), String.format("%.2f m, deepest %.1f, mean %.1f", m.lengthM(), m.deepestM(), m.meanM()));

        // A 45-degree channel: s = x + y; the island is s <= 20, home s >= 30, water between, on a 40 x 40 grid.
        GridGround diag = new GridGround(HOME, HOME);
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                int s = x + y;
                if (s <= 20) diag.plot(x, y, World.GRASS, World.FLAT, ISLAND, true, 0);
                else if (s >= 30) diag.plot(x, y, World.GRASS, World.FLAT, HOME, true, 0);
                else diag.plot(x, y, World.SHALLOW, World.FLAT, 0, false, 6);
            }
        }
        BridgeSurvey.Result d = BridgeSurvey.survey(diag, ISLAND);
        report("a 45-degree channel (ten plots of x + y across): five diagonal steps of 30 x sqrt(2) m, found",
                d.ok() && same(d.lengthM(), 5 * diagonal) && d.stepM().length == 5, String.format("%.2f m, %d steps", d.lengthM(), d.stepM().length));
        check("...two corner plots only, and a chain that replays to the length", d.corners().length == 4 && same(lengthOf(replay(d.corners())), d.lengthM()));

        // One deep plot in the only channel: refusal 1.
        int k = 69, len = 140;
        GridGround blocked = channel(len, 1, World.SHALLOW, 8);
        blocked.plot(k, 0, World.SALT, World.FLAT, 0, false, 41);
        BridgeSurvey.Result b = BridgeSurvey.survey(blocked, ISLAND);
        report("one deep plot (41 m) in a channel one plot wide, 70 plots out: refused for deep water, at that plot's depth and its distance from the far shore",
                b.why() == BridgeSurvey.Why.DEEP && same(b.deepM(), 41) && same(b.deepOutM(), (k + 1) * step), String.format("%.1f m, %.1f m out", b.deepM(), b.deepOutM()));
        String words = BridgeView.refusal(b, BridgeCheck::namer);
        check("...in refusal 1's words, word for word: the depth, how far out, from which landmass, and the 30 m the shallow sea is counted to",
                ("No bridgeable path: deep water (41 m) 2.1 km out from Island 2. A bridge needs water 30 m deep or less all the way across.").equals(words)
                        && EarthData.SHALLOW_M == 30);
        // The same channel without the deep plot is a chain, so the one plot is the whole reason.
        check("...and the channel without that plot is a chain of 141 steps: the deep plot alone refused it", BridgeSurvey.survey(channel(len, 1, World.SHALLOW, 8), ISLAND).ok());

        // A deep column with one gap, the far shore owned at one plot only: the chain goes round, longer.
        int ww = 40, col = 20;
        GridGround detour = new GridGround(HOME, HOME);
        detour.dry(-4, -3, 0, 4, ISLAND, World.FLAT, false);
        detour.plot(-1, 0, World.GRASS, World.FLAT, ISLAND, true, 0);
        detour.dry(0, -3, ww, -2, 0, World.FLAT, false);
        detour.dry(0, 3, ww, 4, 0, World.FLAT, false);
        detour.water(0, -2, ww, 3, World.SHALLOW, 6);
        for (int y = -2; y <= 1; y++) detour.plot(col, y, World.SALT, World.FLAT, 0, false, 33);
        detour.dry(ww, -3, ww + 4, 4, HOME, World.FLAT, true);
        BridgeSurvey.Result dt = BridgeSurvey.survey(detour, ISLAND);
        double expect = (ww + 1) * step + 2 * (diagonal - step);
        List<long[]> detoured = replay(dt.corners());
        report("a column of deep plots with one gap two rows off the only owned far landing: the chain goes round, two diagonal steps longer than the straight way",
                dt.ok() && same(dt.lengthM(), expect) && dt.lengthM() > (ww + 1) * step, String.format("%.2f m against %.2f straight", dt.lengthM(), (ww + 1) * step));
        check("...the corner list replays to a chain that touches no deep plot, passes the column at its gap, and is as long as the result says",
                same(lengthOf(detoured), dt.lengthM()) && detoured.stream().noneMatch(p -> detour.coverAt(p[0], p[1]) == World.SALT)
                        && detoured.stream().anyMatch(p -> p[0] == col && p[1] == 2));

        // Landings: relief, ownership, linking.
        GridGround steep = channel(5, 3, World.SHALLOW, 4);
        steep.dry(5, -1, 9, 4, HOME, World.STEEP, true);
        BridgeSurvey.Result st = BridgeSurvey.survey(steep, ISLAND);
        check("a near shore of STEEP ground, owned and on a linked landmass, is no landing: refused, nothing found", st.why() == BridgeSurvey.Why.NO_LANDING && !st.found());
        GridGround foothill = channel(5, 3, World.SHALLOW, 4);
        foothill.dry(5, -1, 9, 4, HOME, World.FOOTHILL, true);
        check("...FOOTHILL is a landing", BridgeSurvey.survey(foothill, ISLAND).ok());
        GridGround verySteep = channel(5, 3, World.SHALLOW, 4);
        verySteep.dry(5, -1, 9, 4, HOME, World.VERY_STEEP, true);
        check("...and VERY_STEEP is none", BridgeSurvey.survey(verySteep, ISLAND).why() == BridgeSurvey.Why.NO_LANDING);
        GridGround unowned = channel(5, 3, World.SHALLOW, 4);
        unowned.dry(5, -1, 9, 4, HOME, World.FLAT, false);
        check("a near shore of flat ground the city does not own is no landing", BridgeSurvey.survey(unowned, ISLAND).why() == BridgeSurvey.Why.NO_LANDING);
        GridGround unlinked = channel(5, 3, World.SHALLOW, 4);
        unlinked.dry(5, -1, 9, 4, 3, World.FLAT, true);
        check("...nor owned flat ground on a landmass nobody has linked (landmass 3)", BridgeSurvey.survey(unlinked, ISLAND).why() == BridgeSurvey.Why.NO_LANDING);
        unlinked.linked = new int[] { HOME, 3 };
        check("...but it is one once that landmass is linked", BridgeSurvey.survey(unlinked, ISLAND).ok());
        String landing = BridgeView.refusal(st, BridgeCheck::namer);
        check("...and the refusal says what a landing is", landing != null && landing.startsWith("No landing:") && landing.contains("flat or foothill ground the city owns on a landmass it has linked"));

        // Refusal 2.
        GridGround notOwned = channel(5, 3, World.SHALLOW, 4);
        notOwned.dry(-4, -1, 0, 4, ISLAND, World.FLAT, false);
        BridgeSurvey.Result no = BridgeSurvey.survey(notOwned, ISLAND);
        check("the island's ground is not the city's: refused, 'The far side is not dry land.'", no.why() == BridgeSurvey.Why.NOT_DRY && "The far side is not dry land.".equals(BridgeView.refusal(no, BridgeCheck::namer)));
        GridGround inland = new GridGround(HOME, HOME);
        inland.dry(-10, -10, 10, 10, ISLAND, World.FLAT, false);
        inland.dry(-2, -2, 2, 2, ISLAND, World.FLAT, true);
        inland.dry(20, -2, 24, 2, HOME, World.FLAT, true);
        check("the island's owned ground touches no water (it is ringed with ground the city does not own): refused the same way", BridgeSurvey.survey(inland, ISLAND).why() == BridgeSurvey.Why.NOT_DRY);
        check("no landmass at all on the far side: refused the same way", BridgeSurvey.survey(new GridGround(HOME, HOME), 9).why() == BridgeSurvey.Why.NOT_DRY);

        // Refusal 3: the cap.
        int atCap = (int) (BridgeSurvey.MAX_LENGTH_M / step) - 1;
        BridgeSurvey.Result cap = BridgeSurvey.survey(channel(atCap, 1, World.SHALLOW, 5), ISLAND);
        report("a chain of exactly the cap (" + (atCap + 1) + " steps = MAX_LENGTH_M) is a bridge", cap.ok() && same(cap.lengthM(), BridgeSurvey.MAX_LENGTH_M), String.format("%.2f m", cap.lengthM()));
        BridgeSurvey.Result over = BridgeSurvey.survey(channel(atCap + 1, 1, World.SHALLOW, 5), ISLAND);
        report("...one plot more is found but refused as too long, with its length", over.why() == BridgeSurvey.Why.TOO_LONG && !over.over() && over.found() && same(over.lengthM(), (atCap + 2) * step),
                String.format("%.2f m", over.lengthM()));
        BridgeSurvey.Result longer = BridgeSurvey.survey(channel(1300, 1, World.SHALLOW, 5), ISLAND);
        check("refusal 3's words, word for word, with the length found: 'Longer than a bridge can be: 39.0 km against 36 km.'",
                longer.why() == BridgeSurvey.Why.TOO_LONG && "Longer than a bridge can be: 39.0 km against 36 km.".equals(BridgeView.refusal(longer, BridgeCheck::namer)));
        int beyond = (int) (BridgeSurvey.SEARCH_LIMIT_M / step) + 100;
        BridgeSurvey.Result cut = BridgeSurvey.survey(channel(beyond, 1, World.SHALLOW, 5), ISLAND);
        report("a channel past the search window (SEARCH_FACTOR x the cap) is cut: nothing found, 'over 45.0 km against 36 km.'",
                cut.why() == BridgeSurvey.Why.TOO_LONG && cut.over() && !cut.found() && same(cut.lengthM(), BridgeSurvey.SEARCH_LIMIT_M)
                        && "Longer than a bridge can be: over 45.0 km against 36 km.".equals(BridgeView.refusal(cut, BridgeCheck::namer)), String.format("%.0f m", cut.lengthM()));

        // Refusal 4.
        GridGround one = channel(5, 3, World.SHALLOW, 4);
        BridgeSurvey.Result same1 = BridgeSurvey.survey(one, HOME);
        check("the far landmass is the only one linked (home, bridged toward itself): refusal 4, in its words",
                same1.why() == BridgeSurvey.Why.SAME_LANDMASS && ("Both ends are on the city's own landmass: a long bridge must link two landmasses (a short crossing is laid by the highways themselves).").equals(BridgeView.refusal(same1, BridgeCheck::namer)));
        GridGround islandOnly = channel(5, 3, World.SHALLOW, 4);
        islandOnly.linked = new int[] { ISLAND };
        BridgeSurvey.Result same2 = BridgeSurvey.survey(islandOnly, ISLAND);
        check("...or an island the city has linked and nothing else: named, 'Both ends are on Island 2: ...'",
                same2.why() == BridgeSurvey.Why.SAME_LANDMASS && BridgeView.refusal(same2, BridgeCheck::namer).startsWith("Both ends are on Island 2: a long bridge must link two landmasses"));
        check("a data name takes 'landmass', as the spec's example has it (Halifax's landmass); home and an island do not",
                BridgeView.landmassWords(false, "Halifax's").equals("Halifax's landmass") && BridgeView.landmassWords(false, "Island 2").equals("Island 2")
                        && BridgeView.landmassWords(true, "Home").equals("the city's own landmass"));
        BridgeSurvey.Result again = BridgeSurvey.survey(straight, ISLAND);
        check("the chain is the same surveyed twice (a pure function of the ground)", again.why() == r.why() && Arrays.equals(again.corners(), r.corners()) && again.lengthM() == r.lengthM()
                && Arrays.equals(again.stepM(), r.stepM()) && Arrays.equals(again.plotDepthM(), r.plotDepthM()));

        // The worst case: a bay as wide as a bridge can be, the far shore owned all along it.
        int side = (int) (BridgeSurvey.MAX_LENGTH_M / step) - 1;
        GridGround bay = new GridGround(HOME, HOME);
        bay.dry(-4, 0, 0, side, ISLAND, World.FLAT, true);
        bay.water(0, 0, side, side, World.SHALLOW, 12);
        bay.dry(side, 0, side + 4, side, HOME, World.FLAT, true);
        long t0 = System.nanoTime();
        BridgeSurvey.Result wide = BridgeSurvey.survey(bay, ISLAND);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        report("the worst case: a bay of open shallow water a full bridge across and as long, its far shore owned along it all - found, and inside BAY_BUDGET_MS",
                wide.ok() && same(wide.lengthM(), (side + 1) * step) && ms < BAY_BUDGET_MS,
                String.format("%.0f m; %,d plots expanded over %d tiles read in %,d ms (budget %,d)", wide.lengthM(), wide.plotsExpanded(), wide.tiles(), ms, BAY_BUDGET_MS));
    }

    /* =====================================================================
       2. THE PRICE
       ===================================================================== */

    static void thePrice() {
        out.println("\n--- 2. the price: the deck's rate by depth, a metre by kind, a span by its steps, and the notes' three records ---");
        double base = BridgeWorks.BASE_USD_PER_M2;
        check("the base rate is RunWorks's deck, a m2: US DOT's $250 a sq ft", same(base, RunWorks.DECK_USD_PER_M2) && same(base, 250 / LandManager.SQ_M_PER_SQ_FT));
        check("fresh water (depth 0) and any depth to BASE_DEPTH_M cost the base rate a m2", BridgeWorks.deckUsdPerM2(0) == base && BridgeWorks.deckUsdPerM2(BridgeWorks.BASE_DEPTH_M) == base
                && BridgeWorks.deckUsdPerM2(2) == base);
        double conf = BridgeWorks.CONFEDERATION_COST_CAD * BridgeWorks.CAD_CPI_1997_TO_2025 * BridgeWorks.USD_PER_CAD_2025
                / (BridgeWorks.CONFEDERATION_LENGTH_M * BridgeWorks.CONFEDERATION_DECK_M);
        report("the rate at the Confederation's depth is its cost (C$1.3 billion x 1.79 x 0.72) over its deck (12.9 km x 11 m): about US$11,800 a m2",
                same(BridgeWorks.deckUsdPerM2(BridgeWorks.CONFEDERATION_DEPTH_M), conf) && Math.abs(conf - 11_800) < 100, String.format("$%,.0f a m2", conf));
        check("...between, linear in depth from the base at 5 m to the Confederation's at 35 m, halfway at 20 m",
                same(BridgeWorks.deckUsdPerM2(20), (base + conf) / 2));
        check("...beyond 35 m the line goes on (taller piers in deeper water)", BridgeWorks.deckUsdPerM2(50) > conf && same(BridgeWorks.deckUsdPerM2(50), base + (conf - base) * 45 / 30));
        double[] depths = { 0, 3, 5, 10, 20, 30, 35, 60 };
        boolean up = true, order = true, widths = true;
        for (int i = 0; i < depths.length; i++) {
            double h = BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, depths[i]), rl = BridgeWorks.usdPerM(BridgeWorks.Kind.RAIL, depths[i]), rd = BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, depths[i]);
            order &= rl > h && h > rd;
            if (i > 0) up &= BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, depths[i]) >= BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, depths[i - 1])
                    && BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, depths[i]) >= BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, depths[i - 1])
                    && BridgeWorks.usdPerM(BridgeWorks.Kind.RAIL, depths[i]) >= BridgeWorks.usdPerM(BridgeWorks.Kind.RAIL, depths[i - 1]);
            widths &= same(h, BridgeWorks.deckUsdPerM2(depths[i]) * BridgeWorks.HIGHWAY_DECK_WIDTH_M) && same(rl, h * BridgeWorks.RAIL_OVER_HIGHWAY)
                    && same(rd, BridgeWorks.deckUsdPerM2(depths[i]) * BridgeWorks.ROAD_DECK_WIDTH_M);
        }
        check("a metre of span is dearer in rail than in highway than in road, at every depth from fresh water to 60 m", order);
        check("...and never dearer in shallower water, in each kind", up);
        check("...a highway's is the deck a m2 times its 23.2 m (RunWorks), a road's times 2 x 3.66 + 2 x 2.44 = 12.2 m, a rail's the highway's times RAIL_OVER_HIGHWAY (1.42, the Freight Line over the Elevated Highway)",
                widths && same(BridgeWorks.ROAD_DECK_WIDTH_M, 12.2) && same(BridgeWorks.HIGHWAY_DECK_WIDTH_M, RunWorks.DECK_WIDTH_M) && BridgeWorks.RAIL_OVER_HIGHWAY == 1.42);

        // A span is its steps, at the rate of the depths at their ends.
        double[] steps = { 30, 30 * Math.sqrt(2), 30 };
        double[] depth = { 0, 12, 40, 0 };
        double hand = 0;
        for (int i = 0; i < steps.length; i++) hand += steps[i] * (BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, depth[i]) + BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, depth[i + 1])) / 2;
        check("a span is the sum, over its steps, of the step's length at the mean of its two ends' rates (a landing is depth 0)", same(BridgeWorks.spanUsd(BridgeWorks.Kind.ROAD, steps, depth), hand));
        double[] longer = { 30, 30, 30, 30 };
        double[] longerDepth = { 0, 12, 12, 12, 0 };
        double[] shorter = { 30, 30, 30 };
        double[] shorterDepth = { 0, 12, 12, 0 };
        check("...longer costs more at the same depth, and deeper costs more over the same length",
                BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, longer, longerDepth) > BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, shorter, shorterDepth)
                        && BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, shorter, new double[] { 0, 25, 25, 0 }) > BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, shorter, shorterDepth));
        double flatSpan = BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, new double[] { 30, 30, 30 }, new double[] { 0, 0, 0, 0 });
        check("a span over fresh water is its length times the base metre exactly", same(flatSpan, 90 * BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, 0)));
        check("a survey that found nothing has no span", BridgeWorks.spanUsd(BridgeWorks.Kind.ROAD, (BridgeSurvey.Result) null) == 0
                && BridgeWorks.spanUsd(BridgeWorks.Kind.ROAD, BridgeSurvey.survey(new GridGround(HOME, HOME), HOME)) == 0);

        // Points.
        check("points are the dollars in thousands over the founding price a point; none for no dollars or no price",
                same(BridgeWorks.points(2_000_000, 0.5), 4_000) && BridgeWorks.points(0, 0.5) == 0 && BridgeWorks.points(1e6, 0) == 0 && BridgeWorks.points(1e6, Double.NaN) == 0);

        // The records of the notes, printed.
        double confedRoad = 12_900 * BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, 30);
        out.printf("   for the record: a 12.9 km Road Bridge at 30 m is US$%,.0f against the Confederation Bridge's US$%,.0f (C$1.3 billion in 2025 US dollars, %+.1f%%)%n",
                confedRoad, BridgeWorks.CONFEDERATION_COST_CAD * BridgeWorks.CAD_CPI_1997_TO_2025 * BridgeWorks.USD_PER_CAD_2025,
                100 * (confedRoad / (BridgeWorks.CONFEDERATION_COST_CAD * BridgeWorks.CAD_CPI_1997_TO_2025 * BridgeWorks.USD_PER_CAD_2025) - 1));
        out.printf("   for the record: a 1 km Highway Bridge at 5 m is US$%,.0f; at 5 m a Road Bridge US$%,.0f and a Rail Bridge US$%,.0f%n",
                1000 * BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, 5), 1000 * BridgeWorks.usdPerM(BridgeWorks.Kind.ROAD, 5), 1000 * BridgeWorks.usdPerM(BridgeWorks.Kind.RAIL, 5));
        out.printf("   for the record: a 36 km Highway Bridge at 10 m is US$%,.0f, against Hangzhou Bay's US$1.5 to 1.7 billion in 2008 Chinese costs (a 36 km, six-lane deck): ours is %.1f times as much - 2025 US costs and a 23.2 m deck, not fitted%n",
                36_000 * BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, 10), 36_000 * BridgeWorks.usdPerM(BridgeWorks.Kind.HIGHWAY, 10) / 1.6e9);
        check("the three records are in the order the model should give: a road bridge's 12.9 km at 30 m is within a quarter of the Confederation's cost, the point of the Confederation rate",
                Math.abs(confedRoad / (BridgeWorks.CONFEDERATION_COST_CAD * BridgeWorks.CAD_CPI_1997_TO_2025 * BridgeWorks.USD_PER_CAD_2025) - 1) < 0.25);
    }

    /* =====================================================================
       THE PLAYED CITY - Rotterdam with Goeree-Overflakkee's square, two landmasses
       ===================================================================== */

    /** The fixture: the world, and the island square found by search with the model's own reader (LandmassCheck's). */
    record Island(World.Key key, LandmassCheck.Square square) {
        int id() { return square.id(); }
    }

    static Island islandFor(World.Key key) {
        out.println("\n--- the fixture city: Rotterdam, and Goeree-Overflakkee's square bought across the water ---");
        World w = World.of(key);
        long[] at = LandmassCheck.plotOf(w, 51.848, 3.998);
        LandmassCheck.Square sq = LandmassCheck.squareWith(w, 648, at[0], at[1], 40, s -> s.pure() && s.onId() >= 2000 && s.shore() >= 20);
        report("fixture: an island square of landmass 648 with dry ground and shore, pure (the island's north-west tip)", sq != null,
                sq == null ? "none" : String.format("at (%d, %d): %d dry plots of 648", sq.x0(), sq.y0(), sq.onId()));
        return new Island(key, sq);
    }

    /** A city of forty houses and two builders' depots that owns the island square (unlinked, no bridge, nothing placed). */
    static LandmassCheck.City bridgeCity(Island f, String label) {
        LandmassCheck.City c = new LandmassCheck.City(label, f.key(), 40);
        LandmassCheck.quietly(() -> c.g.buildStack(c.g.getBuildingManager().getTemplateByName("Construction Depot"), 2, true));
        c.buy(f.square());
        return c;
    }

    /** Hands the city's survey a channel of `w` plots between the island and home (the game's own ids and linked set), `cover` water `depth` metres deep. */
    static GridGround seam(LandmassCheck.City c, Island f, int w, byte cover, double depth) {
        LiveGround g = new LiveGround(c.books());
        channelInto(g, c.books().home(), f.id(), w, 3, cover, depth);
        c.g.setBridgeGroundForTest(g);
        return g;
    }

    /** An order of a bridge, quietly, by the card's path: the far landmass picked, then the order. */
    static Game.BuildResult orderBridge(LandmassCheck.City c, BuildingsTemplate t, int n, boolean instant, int toward) {
        c.g.chooseBridgeLandmass(LandmassCheck.own(c, t), toward);
        return LandmassCheck.order(c, t, n, instant, Landmasses.CHOSEN);
    }

    /* =====================================================================
       3. THE ORDER
       ===================================================================== */

    static void theOrder(Island f) {
        out.println("\n--- 3. the order: NO_BRIDGE before land and money, the span in the quote, the bridge on site links nothing, standing it links the island ---");
        if (f.square() == null) { check("fixtures were found", false); return; }
        LandmassCheck.City c = bridgeCity(f, "bridge-order");
        Game g = c.g;
        Landmasses lms = c.books();
        LandManager lm = c.lm;
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge"), house = LandmassCheck.tpl(c, "House");
        int id = road.getId(), island = f.id(), home = lms.home();
        report("premise: the city owns the island square - unlinked - and has no bridge; the Road Bridge is a bridge template with no capacity, no power and no water, and no highway or run",
                lms.anyUnlinked() && !lms.linked(island) && g.getBridges().size() == 0 && road.isBridge() && road.bridgeKind() == BridgeWorks.Kind.ROAD
                        && road.getCapacity() == 0 && road.getElectricityConsumption() == 0 && road.getWaterConsumption() == 0 && !road.isHighway() && !Game.isRun(road)
                        && road.getCategory() == BuildingType.INFRASTRUCTURE && !house.isBridge() && house.bridgeKind() == null,
                "island " + island + ", home " + home);
        check("a building that is no bridge has no crossing to ask: bridgeSurvey() null, bridgeChoices() empty, hasBridgeFor() true", g.bridgeSurvey(house) == null
                && g.bridgeChoices(house).isEmpty() && g.hasBridgeFor(house, 1) && g.bridgeSpan(house, 1) == null && g.bridgeLandmass(house) == Landmasses.NO_LANDMASS);
        check("the card offers the landmasses the city owns but home: the island alone; and picks the island by the rule too (the first landmass no port links)",
                g.bridgeChoices(road).size() == 1 && g.bridgeChoices(road).get(0).id() == island && g.bridgeLandmass(road) == island);

        // The real strait the fixture holds, asked of the real ground (no crossing is caused here; the answer is printed).
        long t0 = System.nanoTime();
        BridgeSurvey.Result real = g.bridgeSurvey(road);
        long realMs = (System.nanoTime() - t0) / 1_000_000;
        report("for the record: the survey of the real ground between the island square and Rotterdam's own ground answers, inside the budget",
                real != null && realMs < SURVEY_BUDGET_MS, (real == null ? "null" : real.why() + (real.found() ? String.format(" %.1f km", real.lengthM() / 1000) : "")
                        + String.format(", %,d plots expanded, %d tiles, %,d ms", real.plotsExpanded(), real.tiles(), realMs)));

        // Deep water between, after a shallow fringe at the island: no path.
        GridGround deep = seam(c, f, 12, World.SALT, 45);
        deep.water(0, 0, 3, 3, World.SHALLOW, 6);
        BridgeSurvey.Result none = g.bridgeSurvey(road);
        report("with a shallow fringe at the island and deep water (45 m) beyond it the survey finds no path: refusal 1, in the card's words, naming Island 2",
                none.why() == BridgeSurvey.Why.DEEP && same(none.deepM(), 45) && same(none.deepOutM(), 4 * World.PLOT_M) && BridgeView.refusal(none, lms) != null
                        && BridgeView.refusal(none, lms).startsWith("No bridgeable path: deep water (45 m) ") && BridgeView.refusal(none, lms).contains("out from " + lms.name(island) + ". A bridge needs water 30 m deep or less all the way across."),
                BridgeView.refusal(none, lms));
        double cash0 = g.getCash();
        LandmassCheck.quietly(() -> g.setCashForTest(0));
        Game.BuildResult noMoney = LandmassCheck.order(c, road, 1, false, Landmasses.CHOSEN);
        LandmassCheck.quietly(() -> g.setCashForTest(cash0));
        check("an order with no path is NO_BRIDGE - and with no cash too, not NEEDS_FUNDING: it comes before land and money, so no bond is offered for an impossible bridge",
                noMoney == Game.BuildResult.NO_BRIDGE && LandmassCheck.order(c, road, 1, false, Landmasses.CHOSEN) == Game.BuildResult.NO_BRIDGE);
        check("...nothing moved: the cash, the bridges, the stack on site, the land allocated and the island's link are as they were",
                g.getCash() == cash0 && g.getBridges().size() == 0 && LandmassCheck.onSite(c, road) == 0 && !lms.linked(island) && g.quoteBuild(road, 1).bridgePoints == 0);
        check("the card's verdict is NO_BRIDGE, and a run of the order stops there with NO_BRIDGE",
                BuildCard.verdict(g, road, 1).kind() == BuildCard.VerdictKind.NO_BRIDGE
                        && g.buildRunStop(new java.util.LinkedHashMap<>(java.util.Map.of(LandmassCheck.own(c, road), 1))) == Game.BuildResult.NO_BRIDGE);

        // A channel the survey can cross: shallow, ten metres deep.
        int w = 14;
        GridGround channel = seam(c, f, w, World.SHALLOW, 10);
        BridgeSurvey.Result ok = g.bridgeSurvey(road);
        report("with a shallow channel of 14 plots (10 m deep) between them the survey finds the chain: W + 1 steps, the near landing on home, the far on the island",
                ok.ok() && same(ok.lengthM(), (w + 1) * World.PLOT_M) && ok.nearLandmass() == home && ok.far() == island && same(ok.deepestM(), 10),
                String.format("%.0f m over water %.0f m deep", ok.lengthM(), ok.deepestM()));
        check("...and the survey is kept: asked again, with nothing changed, it is the same object (no search a frame)", g.bridgeSurvey(road) == ok);
        Game.BuildQuote q1 = g.quoteBuild(road, 1), q2 = g.quoteBuild(road, 2);
        double usd = BridgeWorks.spanUsd(BridgeWorks.Kind.ROAD, ok);
        double perPoint = road.getFoundingCashCost() / (double) road.getConstructionPoints();
        double wantPoints = BridgeWorks.points(usd, perPoint);
        double nonMaterial = g.getBuildingManager().nonMaterialCost(road);
        report("the quote's span points are BridgeWorks' arithmetic at the template's founding price a point: the span's dollars in thousands over cash a point",
                wantPoints > 0 && same(q1.bridgePoints, wantPoints) && same(q1.bridgeLengthM, ok.lengthM()), String.format("%,.1f points for $%,.0f", q1.bridgePoints, usd));
        check("...its money is those points at the builders' price a point today, and it is in the sticker: the sticker is the landings' labour and the span",
                same(q1.bridgeCost, q1.bridgePoints * nonMaterial / road.getConstructionPoints()) && same(q1.sticker, nonMaterial + q1.bridgeCost) && q1.total > q1.sticker);
        check("...an order of two is two spans on the one survey: twice the points, twice the money", same(q2.bridgePoints, 2 * q1.bridgePoints) && same(q2.bridgeCost, 2 * q1.bridgeCost)
                && same(q2.bridgeLengthM, q1.bridgeLengthM));
        Game.BridgeSpan span = g.bridgeSpan(road, 1);
        check("...and Game.bridgeSpan() says the same", span != null && same(span.points(), q1.bridgePoints) && same(span.cost(), q1.bridgeCost) && same(span.lengthM(), ok.lengthM()) && same(span.usd(), usd));
        BuildCard.Bridge card = BuildCard.bridge(g, road);
        check("...the card's figures: the survey, the island's name and the landings' price - the quote's sticker less the span",
                card != null && card.survey() == ok && card.farName().equals(lms.name(island)) && same(card.landings(), q1.sticker - q1.bridgeCost) && card.span() != null);
        check("the Highway Bridge's span costs more than the Road Bridge's and the Rail Bridge's more than the Highway's over the same crossing, in the quote's points",
                g.quoteBuild(LandmassCheck.tpl(c, "Highway Bridge"), 1).bridgePoints > 0
                        && BridgeWorks.spanUsd(BridgeWorks.Kind.RAIL, ok) > BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, ok) && BridgeWorks.spanUsd(BridgeWorks.Kind.HIGHWAY, ok) > usd);

        // The order.
        int v0 = lms.version();
        double unlinkedFree0 = lm.getUnlinkedFreeSqFt();
        Game.BuildResult placed = orderBridge(c, road, 1, false, island);
        check("the order is placed (SUCCESS) with the card's pick, which it spends", placed == Game.BuildResult.SUCCESS && g.getBridges().chosen(id) == Bridges.NONE_CHOSEN);
        report("it is on site, not standing: one record on site on the survey's chain, the cash paid, the island still unlinked with its ground still no room",
                LandmassCheck.onSite(c, road) == 1 && LandmassCheck.standing(c, road) == 0 && g.getBridges().count(Bridges.ON_SITE) == 1 && g.getBridges().count(Bridges.STANDING) == 0
                        && !lms.linked(island) && lms.anyUnlinked() && lm.getUnlinkedFreeSqFt() == unlinkedFree0 && lms.version() == v0 && g.getCash() < cash0,
                String.format("on site %d, unlinked free ground %,.0f sq ft", LandmassCheck.onSite(c, road), lm.getUnlinkedFreeSqFt()));
        check("...and the map is handed nothing while it is on site (Game.getBridgePicture() null): a bridge is laid when it stands", g.getBridgePicture() == null);
        Bridges.Row row = g.getBridges().all().get(0);
        check("...its record: template, kind, both landings and landmasses, the length and depths of the survey, the corner plots, on site",
                row.template == id && row.kind() == BridgeWorks.Kind.ROAD && row.state == Bridges.ON_SITE && row.nearLandmass == home && row.farLandmass == island
                        && row.nearX == ok.nearX() && row.nearY == ok.nearY() && row.farX == ok.farX() && row.farY == ok.farY() && same(row.lengthM, ok.lengthM())
                        && same(row.deepestM, ok.deepestM()) && same(row.meanM, ok.meanM()) && Arrays.equals(row.path, ok.corners()));
        boolean early = false;
        int months = 0;
        while (LandmassCheck.standing(c, road) < 1 && months < 600) {
            LandmassCheck.quietly(() -> g.simulateMonths(1));
            months++;
            early |= lms.linked(island) && LandmassCheck.standing(c, road) < 1;
        }
        report("the builders take months, and the island stayed unlinked every month the bridge was on site", LandmassCheck.standing(c, road) == 1 && months >= 2 && !early,
                "stood after " + months + " months (" + String.format("%,.0f points", q1.bridgePoints + road.getConstructionPoints()) + ")");
        report("the month it stood the island was linked: the linked set moved once, nothing is unlinked, the record is standing, and the island's ground is room",
                lms.linked(island) && !lms.anyUnlinked() && lms.version() == v0 + 1 && lm.getUnlinkedFreeSqFt() == 0 && g.getBridges().count(Bridges.STANDING) == 1
                        && g.getBridges().count(Bridges.ON_SITE) == 0 && lm.getLinkedOwnedSqFt() == lm.getOwnedSqFt() && g.getBridges().joins(island) && g.getBridges().joins(home),
                "month " + g.getMonth() + ", version " + lms.version());
        java.util.List<CityMap.BridgeRun> pic = g.getBridgePicture();
        long[] cs = pic == null || pic.isEmpty() ? null : pic.get(0).corners();
        check("...and the map is handed it (Game.getBridgePicture()): one road bridge on the survey's corner plots, its landings first and last",
                pic != null && pic.size() == 1 && pic.get(0).kind() == CityMap.BridgeRun.ROAD && Arrays.equals(cs, ok.corners()) && cs.length >= 4
                        && (cs[0] == ok.farX() && cs[1] == ok.farY() && cs[cs.length - 2] == ok.nearX() && cs[cs.length - 1] == ok.nearY()
                            || cs[0] == ok.nearX() && cs[1] == ok.nearY() && cs[cs.length - 2] == ok.farX() && cs[cs.length - 1] == ok.farY()));

        // A second order, of two: two parallel spans on the same survey.
        Game.BuildQuote two = g.quoteBuild(road, 2);
        Game.BuildResult both = orderBridge(c, road, 2, false, island);
        java.util.List<Bridges.Row> rows = g.getBridges().all();
        check("a second order of two is placed: three records, one standing and two on site, the two on one survey (the same chain, length and landmasses)",
                both == Game.BuildResult.SUCCESS && rows.size() == 3 && g.getBridges().count(Bridges.STANDING) == 1 && g.getBridges().count(Bridges.ON_SITE) == 2
                        && Arrays.equals(rows.get(1).path, rows.get(2).path) && rows.get(1).lengthM == rows.get(2).lengthM && rows.get(1).nearLandmass == rows.get(2).nearLandmass
                        && rows.get(1).state == Bridges.ON_SITE && rows.get(2).state == Bridges.ON_SITE && LandmassCheck.onSite(c, road) == 2);
        check("...billed twice: the quote it was placed at had twice a bridge's points", same(two.bridgePoints, 2 * q1.bridgePoints));
        boolean allStand = LandmassCheck.until(c, () -> LandmassCheck.standing(c, road) >= 3, 1200);
        check("...and the builders finish them one after another: three stand, all three records standing, the island still linked",
                allStand && g.getBridges().count(Bridges.STANDING) == 3 && g.getBridges().count(Bridges.ON_SITE) == 0 && lms.linked(island));

        // The exact arithmetic of the link: built at once, so that nothing else moves between the two readings.
        LandmassCheck.City d = bridgeCity(f, "bridge-order-exact");
        seam(d, f, w, World.SHALLOW, 10);
        Landmasses ld = d.books();
        LandManager lmd = d.lm;
        BuildingsTemplate rd = LandmassCheck.tpl(d, "Road Bridge");
        double availBefore = lmd.getAvailableSqFt(), islandFree = ld.unlinkedFreeSqFt(f.id()), ownedBefore = lmd.getOwnedSqFt();
        int dv = ld.version();
        check("a bridge built at once links the island the moment it stands (the same order, no months)",
                orderBridge(d, rd, 1, true, f.id()) == Game.BuildResult.SUCCESS && ld.linked(f.id()) && ld.version() == dv + 1 && !ld.anyUnlinked() && d.g.getBridges().count(Bridges.STANDING) == 1);
        report("the free ground rose by the island's unbuilt ground, less the bridge's own two landing plots taken from the linked pool",
                LandmassCheck.near(lmd.getAvailableSqFt(), availBefore + islandFree - rd.getLandSqFt()) && LandmassCheck.near(lmd.getOwnedSqFt(), ownedBefore) && islandFree > 0,
                String.format("%,.1f -> %,.1f; the island's free %,.1f; landings %,.0f", availBefore, lmd.getAvailableSqFt(), islandFree, rd.getLandSqFt()));

        // A stopped order is a shell and never links.
        LandmassCheck.City s = bridgeCity(f, "bridge-shell");
        seam(s, f, w, World.SHALLOW, 10);
        BuildingsTemplate rs = LandmassCheck.tpl(s, "Road Bridge");
        check("the island's bridge is ordered and stopped the same month: its site is a shell, nothing on site",
                orderBridge(s, rs, 1, false, f.id()) == Game.BuildResult.SUCCESS && s.g.cancelSite(ConstructionControl.keyOf(rs), true));
        LandmassCheck.quietly(() -> s.g.simulateMonths(1));
        ConstructionControl.Shell shell = s.g.getBuildingManager().getControl().shellOf(rs.getId());
        check("...the record fits the buildings' counts: a shell of one, nothing on site, nothing standing, and a shell never links the island",
                shell != null && shell.buildings == 1 && s.g.getBridges().count(Bridges.SHELL) == 1 && s.g.getBridges().count(Bridges.ON_SITE) == 0 && !s.books().linked(f.id()));
        boolean restarted = quiet(() -> s.g.restartShell(rs.getId()));
        check("...restarted, its stack is on site again and the island is still unlinked", restarted && LandmassCheck.onSite(s, rs) == 1 && !s.books().linked(f.id()));
        LandmassCheck.quietly(() -> s.g.simulateMonths(1));
        check("...after a month the record is on site, none a shell", s.g.getBridges().count(Bridges.ON_SITE) == 1 && s.g.getBridges().count(Bridges.SHELL) == 0 && !s.books().linked(f.id()));
        boolean stands = LandmassCheck.until(s, () -> LandmassCheck.standing(s, rs) >= 1, 800);
        check("...and when it stands the island is linked", stands && s.books().linked(f.id()) && s.g.getBridges().count(Bridges.STANDING) == 1);
        bridgeCityForSection4 = c;
    }

    static <T> T quiet(java.util.function.Supplier<T> work) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { return work.get(); } finally { System.setOut(real); }
    }

    /** An order of the city's, quietly. */
    static Game.BuildResult quietBuild(Game g, BuildingsTemplate t, int n) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(t, n, false));
        return r[0];
    }

    /** The city section 3 played, for section 4. */
    static LandmassCheck.City bridgeCityForSection4;

    /* =====================================================================
       4. NO DEMOLITION
       ===================================================================== */

    static void noDemolition(Island f) {
        out.println("\n--- 4. no demolition: a bridge that stands is for good ---");
        LandmassCheck.City c = bridgeCityForSection4;
        if (c == null) { check("section 3's city was played", false); return; }
        Game g = c.g;
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        Landmasses lms = c.books();
        int standing = LandmassCheck.standing(c, road);
        report("premise: three bridges stand and the island they link is linked", standing == 3 && lms.linked(f.id()) && g.getBridges().count(Bridges.STANDING) == 3, standing + " standing");
        double cash = g.getCash();
        boolean done = g.demolish(road, 1);
        check("a demolition of one is refused, with the words, and nothing is paid", !done && Game.BRIDGE_FOR_GOOD.equals(g.getLastHandRefusal()) && g.getCash() == cash
                && Game.BRIDGE_FOR_GOOD.startsWith("A bridge stands for good: taking one down is not built yet"));
        check("...demolishable() is 0 for each kind of bridge, and a house is still demolishable", g.demolishable(road) == 0 && g.demolishable(LandmassCheck.tpl(c, "Highway Bridge")) == 0
                && g.demolishable(LandmassCheck.tpl(c, "Rail Bridge")) == 0 && g.demolishable(LandmassCheck.tpl(c, "House")) > 0);
        LandmassCheck.quietly(() -> g.simulateMonths(2));
        check("...a month or two on, the bridges still stand and the island is still linked", LandmassCheck.standing(c, road) == 3 && lms.linked(f.id()) && g.getBridges().count(Bridges.STANDING) == 3);
    }

    /* =====================================================================
       5. THE SAVE
       ===================================================================== */

    static void theSave(Island f) throws Exception {
        out.println("\n--- 5. the save: the bridges under one key, absent with none; a standing and one on site read back, resaved, played on ---");
        if (f.square() == null) { check("fixtures were found", false); return; }
        LandmassCheck.City plain = bridgeCity(f, "bridge-plain");
        LandmassCheck.assertSaved(plain, 1);
        com.google.gson.JsonObject plainJson = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(plain.files.saveFile(1))).getAsJsonObject();
        check("a city with no bridge writes no such key (its save is the old bytes) and bridgeState() is null", !plainJson.has("bridges") && plain.lm.bridgeState() == null);

        LandmassCheck.City c = bridgeCity(f, "bridge-save");
        seam(c, f, 14, World.SHALLOW, 10);
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge"), rail = LandmassCheck.tpl(c, "Rail Bridge");
        int island = f.id(), home = c.books().home();
        boolean built = orderBridge(c, road, 1, true, island) == Game.BuildResult.SUCCESS && orderBridge(c, rail, 1, false, island) == Game.BuildResult.SUCCESS;
        // ...two months, not one: a game saved in its first month holds no spending figures for the shops that a load re-derives, which would be the only thing a resave moves.
        LandmassCheck.quietly(() -> c.g.simulateMonths(2));
        Bridges.State st = c.lm.bridgeState();
        report("fixture: a Road Bridge standing, built at once, and a Rail Bridge on site over the same crossing, the island linked",
                built && st != null && st.bridges.size() == 2 && st.bridges.get(0).state == Bridges.STANDING && st.bridges.get(1).state == Bridges.ON_SITE && c.books().linked(island),
                LandmassCheck.GSON.toJson(st).length() + " bytes of state");
        LandmassCheck.assertSaved(c, 1);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        check("the save carries the key \"bridges\" with a row for each bridge, and the same format as ever (no bump)",
                json.has("bridges") && json.getAsJsonObject("bridges").getAsJsonArray("bridges").size() == 2 && json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT);
        Game back = new Game(c.files);
        LandmassCheck.quietly(() -> back.loadGameSave(1));
        Landmasses lb = back.getLandManager().getLandmasses();
        report("read back: the same bridges, the island linked, nothing unlinked, the free ground as it was",
                back.getLoadFailure() == null && LandmassCheck.GSON.toJson(back.getLandManager().bridgeState()).equals(LandmassCheck.GSON.toJson(st)) && lb.linked(island) && !lb.anyUnlinked()
                        && lb.rows().equals(c.books().rows()) && LandmassCheck.near(back.getLandManager().getAvailableSqFt(), c.lm.getAvailableSqFt()) && back.getBridges().count(Bridges.STANDING) == 1
                        && back.getBridges().count(Bridges.ON_SITE) == 1, back.getLoadFailure() == null ? "" : back.getLoadFailure());
        LandmassCheck.resaved(c, "a standing bridge and one on site");
        // Played on: the reload and the city played on are one, the bridge on site completing in both.
        ReloadCheck.Point p = ReloadCheck.reload(c.files, c.g, "bridge city");
        report("a reload equals the city played on: loaded into a second game and both played a month and two, every figure of the save and of the history is the same",
                p.problem == null && p.save1 != null && p.save1.differing == 0 && p.save2.differing == 0 && p.history1.differing == 0 && p.history2.differing == 0,
                p.problem != null ? p.problem : p.save1.differing + " and " + p.save2.differing + " differ");
        // Without the key: none, and the island unlinked.
        com.google.gson.JsonObject stripped = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        stripped.remove("bridges");
        java.nio.file.Files.writeString(c.files.saveFile(4), LandmassCheck.GSON.toJson(stripped));
        java.nio.file.Files.copy(c.files.historyFile(1), c.files.historyFile(4), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Game bare = new Game(c.files);
        LandmassCheck.quietly(() -> bare.loadGameSave(4));
        check("a save with the key stripped loads - it is not refused - with no bridge recorded: the island is unlinked, the standing bridge joins nothing",
                bare.getLoadFailure() == null && bare.getBridges().size() == 0 && bare.getLandManager().bridgeState() == null && !bare.getLandManager().getLandmasses().linked(island)
                        && bare.getLandManager().getLandmasses().linked(home));
        // A bad row.
        com.google.gson.JsonObject bad = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        com.google.gson.JsonObject nonsense = new com.google.gson.JsonObject();
        nonsense.addProperty("template", LandmassCheck.tpl(c, "House").getId());
        nonsense.addProperty("kind", "ROAD");
        nonsense.addProperty("state", 0);
        bad.getAsJsonObject("bridges").getAsJsonArray("bridges").add(nonsense);
        com.google.gson.JsonObject unknownKind = new com.google.gson.JsonObject();
        unknownKind.addProperty("template", road.getId());
        unknownKind.addProperty("kind", "FERRY");
        unknownKind.addProperty("state", 0);
        bad.getAsJsonObject("bridges").getAsJsonArray("bridges").add(unknownKind);
        java.nio.file.Files.writeString(c.files.saveFile(5), LandmassCheck.GSON.toJson(bad));
        java.nio.file.Files.copy(c.files.historyFile(1), c.files.historyFile(5), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Game odd = new Game(c.files);
        LandmassCheck.quietly(() -> odd.loadGameSave(5));
        check("a bad row is passed over, not refused: a template that is no bridge is skipped, and so is a kind the game does not know",
                odd.getLoadFailure() == null && odd.getBridges().size() == 2 && odd.getLandManager().getLandmasses().linked(island));
    }

    /* =====================================================================
       6. A GENERATED WORLD
       ===================================================================== */

    static void generatedWorld() {
        out.println("\n--- 6. a generated world: every bridge card refuses with refusal 4, and asking moves nothing ---");
        GameFiles filesA = GameFiles.scratch("bridge-generated-a"), filesB = GameFiles.scratch("bridge-generated-b");
        Game a = new Game(filesA), b = new Game(filesB);
        LandmassCheck.quietly(() -> {
            a.newGame(Founding.defaults());
            b.newGame(Founding.defaults());
            a.setCashForTest(Founding.WEALTHY_CASH);
            b.setCashForTest(Founding.WEALTHY_CASH);
        });
        String[] names = { "Highway Bridge", "Rail Bridge", "Road Bridge" };
        boolean all = true, words = true, verdicts = true, orders = true, choices = true;
        String refusal = "Both ends are on the city's own landmass: a long bridge must link two landmasses (a short crossing is laid by the highways themselves).";
        for (String n : names) {
            BuildingsTemplate t = a.getBuildingManager().getTemplateByName(n);
            BridgeSurvey.Result r = a.bridgeSurvey(t);
            all &= t != null && r != null && r.why() == BridgeSurvey.Why.SAME_LANDMASS;
            words &= refusal.equals(BridgeView.refusal(r, a.getLandManager().getLandmasses()));
            verdicts &= BuildCard.verdict(a, t, 1).kind() == BuildCard.VerdictKind.NO_BRIDGE;
            double cash = a.getCash();
            orders &= quietBuild(a, t, 1) == Game.BuildResult.NO_BRIDGE && a.getCash() == cash;
            choices &= a.bridgeChoices(t).size() == 1 && a.bridgeChoices(t).get(0).home() && a.bridgeLandmass(t) == a.getLandManager().getLandmasses().home();
        }
        check("each of the three bridge cards: the survey says refusal 4", all);
        check("...in the words, word for word", words);
        check("...the card's verdict is NO_BRIDGE", verdicts);
        check("...an order is NO_BRIDGE and moves no cash", orders);
        check("...the card offers home alone, and picks it", choices);
        // Month by month, with and without asking.
        for (int m = 0; m < 6; m++) {
            for (String n : names) {
                BuildingsTemplate t = a.getBuildingManager().getTemplateByName(n);
                a.bridgeSurvey(t);
                a.quoteBuild(t, 3);
                BuildCard.verdict(a, t, 1);
                BuildCard.bridge(a, t);
                BuildCard.groups(a, BuildAdvice.ROADS, BuildAdvice.measuresOf(BuildAdvice.ROADS).get(0));
            }
            LandmassCheck.quietly(() -> {
                a.simulateMonths(1);
                b.simulateMonths(1);
            });
        }
        boolean saved = LandmassCheck.quietlyResult(() -> a.saveGame(1, "bridge")).ok & LandmassCheck.quietlyResult(() -> b.saveGame(1, "bridge")).ok;
        java.util.List<String> moved = new java.util.ArrayList<>();
        try {
            com.google.gson.JsonObject ja = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(filesA.saveFile(1))).getAsJsonObject();
            com.google.gson.JsonObject jb = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(filesB.saveFile(1))).getAsJsonObject();
            moved = SaveFileCheck.jsonDiff(ja, jb, java.util.Set.of("savedAt", "slotName", "mapStamp"));
            check("...and the city that was asked about the bridges every month for six is the same city, saved, as the one that was not (no key \"bridges\" in either)",
                    saved && moved.isEmpty() && !ja.has("bridges") && !jb.has("bridges"));
        } catch (java.io.IOException e) {
            report("...the two cities' saves were read", false, e.toString());
        }
        if (!moved.isEmpty()) out.println("   the saves differ at " + moved);
    }

    /* =====================================================================
       7. THE FULL FILE
       ===================================================================== */

    /** Plots a side of the squares the full-file crossings buy at each shore. */
    static final int SHORE_SQUARE = 128;

    /**
     * A city founded at `founding`, owning a square of ground about each of the two points - its own shore (`home`) and the
     * far one - and the survey of a Road Bridge toward the far landmass (the real ground, no seam): printed with its time.
     * Returns the survey, null if the squares were not both land.
     */
    static BridgeSurvey.Result crossing(World.Key key, String label, double[] founding, double[] home, double[] farPoint, double realKm) {
        World w = World.of(key);
        LandmassCheck.City c = new LandmassCheck.City(label, key, 0);
        Landmasses lms = c.books();
        long[] h = LandmassCheck.plotOf(w, home[0], home[1]), f = LandmassCheck.plotOf(w, farPoint[0], farPoint[1]);
        LandmassCheck.quietly(() -> {
            c.lm.buyRectForTest(h[0] - SHORE_SQUARE / 2, h[1] - SHORE_SQUARE / 2, h[0] + SHORE_SQUARE / 2, h[1] + SHORE_SQUARE / 2, c.g.getMonth());
            c.lm.buyRectForTest(f[0] - SHORE_SQUARE / 2, f[1] - SHORE_SQUARE / 2, f[0] + SHORE_SQUARE / 2, f[1] + SHORE_SQUARE / 2, c.g.getMonth());
        });
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        long t0 = System.nanoTime();
        BridgeSurvey.Result r = c.g.bridgeSurvey(road);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        report(label + ": the city owns ground on " + lms.rows().size() + " landmasses and the survey of a Road Bridge toward " + (r == null ? "?" : lms.name(r.far())) + " answers inside SURVEY_BUDGET_MS",
                r != null && lms.rows().size() >= 2 && ms < SURVEY_BUDGET_MS,
                r == null ? "no survey" : String.format("%s%s; %,d plots expanded over %d tiles, %,d ms", r.why(),
                        r.found() ? String.format(" %.2f km (real: %.1f km), deepest %.0f m, mean %.0f m, %d corner plots", r.lengthM() / 1000, realKm, r.deepestM(), r.meanM(), r.corners().length / 2)
                                : r.why() == BridgeSurvey.Why.DEEP ? String.format(": %.0f m deep, %.2f km out", r.deepM(), r.deepOutM() / 1000) : "",
                        r.plotsExpanded(), r.tiles(), ms));
        if (r != null) out.println("      " + (BridgeView.refusal(r, lms) != null ? BridgeView.refusal(r, lms) : BridgeView.crossingWords(r, lms)));
        if (r != null && r.found()) {
            BridgeWorks.Kind kind = BridgeWorks.Kind.ROAD;
            out.printf("      its span as a Road Bridge: US$%,.0f (the Confederation: US$%,.0f)%n", BridgeWorks.spanUsd(kind, r),
                    BridgeWorks.CONFEDERATION_COST_CAD * BridgeWorks.CAD_CPI_1997_TO_2025 * BridgeWorks.USD_PER_CAD_2025);
        }
        return r;
    }

    static void theEarth() {
        out.println("\n--- 7. the full file: the Northumberland Strait from Borden-Carleton to Cape Jourimain, and the Rotterdam delta's strait to Goeree-Overflakkee ---");
        java.nio.file.Path file = EarthData.locate();
        if (file == null) {
            out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": section 7 (the full file) was not run");
            return;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (!game.here() || !game.reader().identity().equals(EarthData.EARTH_2)) {
            out.println("the game's Earth is not earth-2 (" + game.words() + "): section 7 (the full file) was not run");
            return;
        }
        EarthData data = game.reader();
        String id = EarthTerrain.use(data);
        World.Key pei = World.Key.earth(SEED, 46.25, -63.70, id);
        crossing(pei, "bridge-pei", new double[] { 46.25, -63.70 }, new double[] { 46.24, -63.69 }, new double[] { 46.16, -63.81 }, 12.9);
        World.Key delta = World.Key.earth(SEED, 51.92, 4.48, id);
        World wd = World.of(delta);
        long[] goeree = LandmassCheck.plotOf(wd, 51.77, 4.15);
        int goereeId = wd.landmassAt(goeree[0], goeree[1]);
        report("the Rotterdam delta: Goeree-Overflakkee is a landmass of its own, not home's", goereeId > 0 && goereeId != wd.landmassAt(wd.foundingX(), wd.foundingY()), "landmass " + goereeId);
        crossing(delta, "bridge-delta", new double[] { 51.92, 4.48 }, new double[] { 51.83, 4.20 }, new double[] { 51.77, 4.15 }, 1.39);
    }
}
