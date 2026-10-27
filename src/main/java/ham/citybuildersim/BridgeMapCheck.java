package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A long bridge that stands is laid by the runs, kept clear by the plans and drawn with its deck (ET6; docs/studies/spec-earth2-terrain.md 6.5 and 6.6).
 *
 * WHAT THIS HAS TO PROVE (the sections below are numbered to match; each has its part on the runs alone, on ground of two dry islands and a
 * channel the harness makes, and its part on a real CityMap, on a channel of fresh water in the default world):
 *
 *   1. A HIGHWAY BRIDGE ACROSS A CHANNEL NO SHORT CROSSING SPANS IS LAID WHOLE.
 *      The channel is wider than the highway's and the railway's reach
 *      (TilePainter.MAX_BRIDGE, RAIL_BRIDGE: the premise; laid alone the net
 *      puts no plot across it). A bridge handed to the runs
 *      (CityRuns.setBridges(); to the map, CityMap.bridgePicture() and the
 *      month's reconcile()) has every plot of its path, from landing to
 *      landing, a highway plot (fill(), at(), the painter's Input.fixed), the
 *      far landing too; a diagonal leg is laid as the nets' staircase, no ramp
 *      is marked, and the net's plots and what it is short of are what they
 *      were (the bridge spends none of its growth). On the map the plots of it
 *      over the water are the painter's bridge plots, its landings are not.
 *
 *   2. A RAIL BRIDGE LIKEWISE; A ROAD BRIDGE DRAWS A PAVED DECK AND JOINS NO
 *      NET. A rail bridge's plots are the railway's (a railway over a highway
 *      where they meet), drawn with TileRaster.DECK beside the bed over the
 *      water and as ordinary track on its landings. A road bridge is no plot of
 *      any net (at() sees none, the tile's runs hold none, no net's plots
 *      move) and is a paved street a whole plot wide, its deck under it over
 *      the water and none on its landings; its ground is a plan's, kept as a
 *      reserve.
 *
 *   3. ITS LANDINGS AND DECK HOLD NO BUILDING AFTER THE PLANS ARE DRAWN. The
 *      premise: in the same city drawn with no bridge a building stands on
 *      most of the landings (the fixture chooses the rows whose landings are
 *      built). With the bridges handed, the plans of the districts they cross
 *      see them (their input holds the highway's, the railway's and the road's
 *      reserve on the landing) and not one building or field stands on a plot
 *      of any path.
 *
 *   4. THE BRIDGE DOES NOT MOVE. The net grown, taken back and grown again with
 *      the bridge standing: every plot of the bridge is where it was and the
 *      net's plots are the model's every time (the bridge spends none of the
 *      budget). On the map a month with more highway plots in the model, then
 *      fewer, then more: the same, every arm a prefix of the next month's;
 *      canonical() and reconcile() lay the same bridges, and the journal of the
 *      runs (CityMap.journalDropped()) is at none.
 *
 *   5. NO BRIDGE, NO CHANGE. Runs and maps handed no bridge, a null and an empty
 *      list are the same: the same fill, frame hash and signature of the runs;
 *      on the map the same runs, the same raster bytes on a sample of tiles and
 *      the same sidecar, byte for byte. A bridge handed and taken back leaves
 *      the same runs, plot for plot.
 *
 *   6. THE STAMP MOVES WHEN A BRIDGE IS HANDED. The same bridges in any order
 *      are the one stamp, another list another; a run that is no path is not
 *      laid and is counted; the witness is told the districts a bridge comes
 *      to. On the map the sidecar's land stamp moves with it, so a sidecar
 *      written before the bridge is not read back under it, one written after
 *      is read back only under the list it was written with and is then the same
 *      map with the bridge laid; the runs' version and the plan stamp of a
 *      district the bridge crosses move.
 *
 * WHY THIS EXISTS: the bridge the model builds was nowhere on the map: no run met it, a plan was free to put a building where it came
 * ashore, and the raster drew water. A fixture CAUSES each case: a channel wider than any short crossing, landings with buildings on
 * them, a month that grows the net, a sidecar written under another list.
 */
public class BridgeMapCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        // Through Timing (TimingCheck 6): a timing miss beside another JVM says SOFT and is not counted.
        System.out.printf("%-110s %s%n", label, Timing.verdict(ok, label));
    }

    /** The founding site of the runs' ground: a highway line crossing, so that the hub is the site itself. */
    static final long SITE = IslandNetCheck.SITE;

    /** The kinds, short. */
    static final int ROAD = CityMap.BridgeRun.ROAD, HIGHWAY = CityMap.BridgeRun.HIGHWAY, RAIL = CityMap.BridgeRun.RAIL;

    /** The widest water a short crossing spans, in plots: the highway's reach and the railway's, as the painter has them. */
    static int shortest() {
        return Math.max(TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY], TilePainter.RAIL_BRIDGE);
    }

    static long key(long x, long y) {
        return (x << 32) ^ (y & 0xffffffffL);
    }

    static long kx(long k) {
        return k >> 32;
    }

    static long ky(long k) {
        return (int) k;
    }

    /**
     * The plots of a bridge's path, worked out here and not by CityRuns: the first corner, then every leg step by step, a diagonal's
     * step with the plot beside it (x + dx, y) that keeps the run joined.
     */
    static Set<Long> pathOf(long[] c) {
        Set<Long> out = new HashSet<>();
        out.add(key(c[0], c[1]));
        for (int i = 0; i + 3 < c.length; i += 2) {
            long x = c[i], y = c[i + 1];
            long dx = Long.signum(c[i + 2] - x), dy = Long.signum(c[i + 3] - y);
            long m = Math.max(Math.abs(c[i + 2] - x), Math.abs(c[i + 3] - y));
            for (long k = 1; k <= m; k++) {
                out.add(key(x + dx * k, y + dy * k));
                if (dx != 0 && dy != 0) out.add(key(x + dx * k, y + dy * (k - 1)));
            }
        }
        return out;
    }

    static CityMap.BridgeRun run(int kind, long... corners) {
        return new CityMap.BridgeRun(kind, corners);
    }

    /** The runs' codes over a box, row by row: fill() with the plan's ground or the tile's. */
    static byte[] fillOf(CityRuns runs, long x0, long y0, int w, int h, boolean plan, byte[] marks) {
        byte[] f = new byte[w * h];
        runs.fill(x0, y0, w, h, f, marks, plan, plan);
        return f;
    }

    /** Whether every plot of a path is `code` on the runs. */
    static boolean stands(CityRuns runs, Set<Long> path, byte code) {
        for (long k : path) if (runs.at(kx(k), ky(k)) != code) return false;
        return true;
    }

    public static void main(String[] args) {
        CityRuns.linkArms = true;
        RunsFx r = new RunsFx();
        System.out.println("--- 1. a highway bridge across a channel no short crossing spans is laid whole, and draws over the water ---");
        r.highway();
        MapFixture f = MapFixture.build();
        if (f != null) f.highway();
        System.out.println("\n--- 2. a rail bridge likewise; a road bridge draws a paved deck and joins no net ---");
        r.railAndRoad();
        if (f != null) f.railAndRoad();
        System.out.println("\n--- 3. its landings and deck hold no building after the plans are drawn ---");
        if (f != null) f.clearance();
        else check("fixture: no channel, no map", false);
        System.out.println("\n--- 4. the bridge does not move ---");
        r.unmoved();
        if (f != null) f.unmoved();
        System.out.println("\n--- 5. no bridge, no change ---");
        r.unchanged();
        if (f != null) f.unchanged();
        System.out.println("\n--- 6. the stamp moves when a bridge is handed ---");
        r.stamps();
        if (f != null) f.stamps();
        System.out.println(fails == 0 ? "\nThe bridges stand." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* =====================================================================
       THE RUNS: two islands, a channel, three bridges
       ===================================================================== */

    /** The runs' ground: the west island (to x = SITE + 150), a channel of 70 plots unowned between, the east island from SITE + 221. */
    static final long W0 = SITE - 700, W1 = SITE + 150, E0 = SITE + 221, E1 = SITE + 721, Y0 = SITE - 450, Y1 = SITE + 450;

    /** The highway bridge: out of the west island's shore plot, east, a 45-degree stretch, east into the east island's first column. */
    static final long[] HW = { W1, SITE + 40, SITE + 170, SITE + 40, SITE + 182, SITE + 52, E0, SITE + 52 };
    /** The rail bridge: out of the same landing, north-east, then east. */
    static final long[] RL = { W1, SITE + 40, SITE + 160, SITE + 30, E0 + 1, SITE + 30 };
    /** The road bridge: straight across, a row above. */
    static final long[] RD = { W1, SITE - 60, E0, SITE - 60 };

    static final class RunsFx {
        final IslandNetCheck.Land g = new IslandNetCheck.Land(W0 - 20, Y0 - 20, E1 + 20, Y1 + 20, false)
                .add(IslandNetCheck.Land.rect(W0, Y0, W1, Y1))
                .add(IslandNetCheck.Land.rect(E0, Y0, E1, Y1));
        final long target = 900;
        final int w = (int) (E1 - W0 + 1), h = (int) (Y1 - Y0 + 1);
        final Set<Long> hwPath = pathOf(HW), rlPath = pathOf(RL), rdPath = pathOf(RD);
        CityRuns runs;
        byte[] alone;

        CityRuns fresh() {
            CityRuns r = IslandNetCheck.lay(g, target, CityRuns.ONE_PIECE);
            r.setGateway(CityRuns.NO_GATEWAY);
            return r;
        }

        /** Whether a window of the box is what the whole box holds there. */
        boolean windows(CityRuns r, byte[] whole, boolean plan, long[]... wins) {
            for (long[] win : wins) {
                byte[] part = fillOf(r, win[0], win[1], (int) win[2], (int) win[3], plan, null);
                for (int y = 0; y < win[3]; y++) {
                    for (int x = 0; x < win[2]; x++) {
                        if (part[(int) (y * win[2] + x)] != whole[(int) ((win[1] + y - Y0) * w + (win[0] + x - W0))]) return false;
                    }
                }
            }
            return true;
        }

        void highway() {
            int wide = (int) (E0 - W1 - 1);
            check("fixture: the channel (" + wide + " plots) is wider than the highway's and the railway's reach (" + shortest() + ") and a street's ("
                    + TilePainter.STREET_BRIDGE + ")", wide > shortest() && wide > TilePainter.STREET_BRIDGE);
            runs = fresh();
            long plots = runs.highways.plots, signature = runs.signature(), version = runs.version();
            long onEast = IslandNetCheck.coveredIn(runs.highways, E0, Y0, E1, Y1);
            check("premise: the net laid alone puts no plot on the east island - an arm crosses no water of this width", onEast == 0 && plots == target && runs.highwayShort() == 0);
            alone = fillOf(runs, W0, Y0, w, h, false, null);
            long frameAlone = runs.frameHash(SITE + 100, SITE, 200, 100);

            check("fixture: the bridge is a path of three legs, one a 45-degree stretch, a hundred plots of it from dry plot to dry plot, " + wide + " of them over the channel",
                    HW.length == 8 && hwPath.size() > wide && g.at(HW[0], HW[1], -1) == World.GRASS && g.at(E0, SITE + 52, -1) == World.GRASS && g.at(SITE + 175, SITE + 45, -1) == -1);
            check("a bridge handed to the runs is a change (setBridges() answers true), a list of the same bridge is none",
                    runs.setBridges(List.of(run(HIGHWAY, HW))) && !runs.setBridges(List.of(run(HIGHWAY, HW))) && runs.bridgeCount() == 1 && runs.bridgeCount(HIGHWAY) == 1);
            byte[] marks = new byte[w * h];
            byte[] with = fillOf(runs, W0, Y0, w, h, false, marks);
            boolean all = true, others = true, ramps = true;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * w + x;
                    long wx = W0 + x, wy = Y0 + y;
                    if (hwPath.contains(key(wx, wy))) {
                        all &= with[i] == CityRuns.F_HIGHWAY && runs.at(wx, wy) == CityRuns.F_HIGHWAY;
                        ramps &= (marks[i] & CityRuns.M_RAMP) == 0;
                    } else others &= with[i] == alone[i];
                }
            }
            boolean stair = true;
            for (long k = 1; k <= 12; k++) stair &= (marks[(int) ((SITE + 40 + k - Y0) * w + (SITE + 170 + k - W0))] & CityRuns.M_DIAG) != 0;
            check("every plot of its path, from landing to landing, is a highway plot of the runs: fill() and at() (" + hwPath.size() + " plots)", all);
            check("...and no other plot of the box is other than it was before the bridge: the net's own plots, the water, the east island", others);
            check("...the far landing, the east island's first column, is a highway plot", with[(int) ((SITE + 52 - Y0) * w + (E0 - W0))] == CityRuns.F_HIGHWAY && runs.at(E0, SITE + 52) == CityRuns.F_HIGHWAY);
            check("...the 45-degree stretch is laid as the nets' staircase and marked a diagonal; no ramp is marked on the bridge", stair && ramps);
            check("the net's plots are what they were, and what it is short of: the bridge is no plot of the net, so none of its growth is spent on it",
                    runs.highways.plots == plots && runs.highwayShort() == 0 && IslandNetCheck.coveredIn(runs.highways, E0, Y0, E1, Y1) == 0);
            check("the runs' signature and version moved with it", runs.signature() != signature && runs.version() != version);
            check("a window of the plots the bridge crosses has another frame hash; a window it does not touch has the one it had",
                    runs.frameHash(SITE + 100, SITE, 200, 100) != frameAlone && runs.frameHash(W0, Y0, 100, 100) == fresh().frameHash(W0, Y0, 100, 100));
            check("a window of the box holds what the whole box does there, at the bridge's landings and at its bend",
                    windows(runs, with, false, new long[] { SITE + 140, SITE + 30, 40, 40 }, new long[] { SITE + 160, SITE + 20, 70, 50 }, new long[] { SITE + 200, SITE + 40, 33, 33 },
                            new long[] { SITE + 181, SITE + 51, 3, 3 }));
        }

        void railAndRoad() {
            long plots = runs.highways.plots;
            byte[] alonePlan = fillOf(fresh(), W0, Y0, w, h, true, null);
            check("a rail bridge and a road bridge handed with the highway's: three bridges, one of each kind",
                    runs.setBridges(List.of(run(HIGHWAY, HW), run(RAIL, RL), run(ROAD, RD))) && runs.bridgeCount() == 3 && runs.bridgeCount(RAIL) == 1 && runs.bridgeCount(ROAD) == 1 && runs.roadBridges());
            byte[] marks = new byte[w * h];
            byte[] tile = fillOf(runs, W0, Y0, w, h, false, marks), plan = fillOf(runs, W0, Y0, w, h, true, null);
            boolean rail = true, over = true, others = true, roadTile = true, roadPlan = true, atRoad = true;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * w + x;
                    long wx = W0 + x, wy = Y0 + y;
                    long k = key(wx, wy);
                    boolean hw = hwPath.contains(k), rl = rlPath.contains(k), rd = rdPath.contains(k);
                    if (hw || rl) {
                        // The tile's runs and a plan's ground hold the highway's and the rail's alike, a railway over a highway where they meet.
                        byte want = hw && rl ? CityRuns.F_RAIL_OVER : hw ? CityRuns.F_HIGHWAY : CityRuns.F_RAIL;
                        rail &= tile[i] == want && plan[i] == want;
                        over &= runs.at(wx, wy) == want;
                    } else if (rd) {
                        // ...the road's never in the tile's, a reserve in a plan's where nothing else is.
                        roadTile &= tile[i] == alone[i];
                        roadPlan &= plan[i] == (alonePlan[i] != 0 ? alonePlan[i] : CityRuns.F_RESERVE);
                        atRoad &= runs.at(wx, wy) == alone[i];
                    } else others &= tile[i] == alone[i] && plan[i] == alonePlan[i];
                }
            }
            check("every plot of the rail bridge's path is a railway plot of the runs, and the railway over the highway on the plots they share, the shore landing among them (fill() in a tile's ground and a plan's, at())",
                    rail && over && runs.at(RL[0], RL[1]) == CityRuns.F_RAIL_OVER && runs.at(RL[4], RL[5]) == CityRuns.F_RAIL);
            check("...the road bridge's plots are no plot of the tile's runs and no plot of any net (at() sees none); a plan's ground holds them as a reserve (F_RESERVE) where nothing else is",
                    roadTile && roadPlan && atRoad && plan[(int) ((RD[1] - Y0) * w + RD[0] - W0)] == CityRuns.F_RESERVE && plan[(int) ((RD[3] - Y0) * w + RD[2] - W0)] == CityRuns.F_RESERVE);
            check("...no other plot of the box is other than it was: the net's own plots, the water, the east island", others);
            check("the nets' plots are what they were: the highways' and the railway's, the road bridge and the rail bridge spend none of the growth",
                    runs.highways.plots == plots && runs.rail.plots == 0 && runs.highwayShort() == 0);
            check("the rail bridge stands whether or not the railway has started; the highway bridge whether or not the highways reach a street (fill() lays both)",
                    !runs.rail.started && runs.at(RL[2], RL[3]) == CityRuns.F_RAIL);
            // A window of the plan's ground holds the road bridge's plots as the whole does.
            check("a window of the box holds what the whole box does there, in a plan's ground as in a tile's",
                    windows(runs, tile, false, new long[] { SITE + 140, SITE + 20, 60, 60 }, new long[] { SITE + 190, SITE - 70, 40, 40 })
                            && windows(runs, plan, true, new long[] { SITE + 140, SITE - 70, 60, 150 }, new long[] { SITE + 200, SITE - 62, 33, 33 }));
        }

        void unmoved() {
            // The net grown, taken back and grown again with the bridges standing.
            Map<String, List<long[]>> before = IslandNetCheck.armsOf(runs.highways);
            IslandNetCheck.again(runs, g, 1500, CityRuns.ONE_PIECE);
            boolean grown = runs.highways.plots == 1500 && runs.highwayShort() == 0 && IslandNetCheck.prefix(before, IslandNetCheck.armsOf(runs.highways));
            boolean there = onTheRuns();
            IslandNetCheck.again(runs, g, 200, CityRuns.ONE_PIECE);
            boolean trimmed = runs.highways.plots == 200;
            there &= onTheRuns();
            IslandNetCheck.again(runs, g, 0, CityRuns.ONE_PIECE);
            boolean none = runs.highways.plots == 0;
            there &= onTheRuns();
            IslandNetCheck.again(runs, g, target, CityRuns.ONE_PIECE);
            there &= onTheRuns();
            check("the net grown to 1,500 plots, taken back to 200 and to none and grown again with the bridges standing: its plots are the model's every time, every arm a prefix of the next",
                    grown && trimmed && none && runs.highways.plots == target);
            check("...and every plot of every bridge is where it was each time: the highway's, the railway's, the road's ground", there);
        }

        /** The three bridges on the runs as the section 2 left them. */
        boolean onTheRuns() {
            boolean ok = true;
            for (long k : hwPath) ok &= runs.at(kx(k), ky(k)) == CityRuns.F_HIGHWAY || runs.at(kx(k), ky(k)) == CityRuns.F_RAIL_OVER;
            for (long k : rlPath) ok &= runs.at(kx(k), ky(k)) == CityRuns.F_RAIL || runs.at(kx(k), ky(k)) == CityRuns.F_RAIL_OVER;
            byte[] plan = fillOf(runs, SITE + 140, SITE - 70, 100, 30, true, null);
            for (long k : rdPath) ok &= plan[(int) ((ky(k) - (SITE - 70)) * 100 + kx(k) - (SITE + 140))] != 0;
            return ok;
        }

        void unchanged() {
            CityRuns never = fresh(), nulled = fresh(), emptied = fresh(), taken = fresh();
            nulled.setBridges(null);
            emptied.setBridges(new ArrayList<>());
            taken.setBridges(List.of(run(HIGHWAY, HW), run(ROAD, RD)));
            boolean moved = taken.setBridges(null);
            boolean none = true;
            for (CityRuns r : new CityRuns[] { nulled, emptied, taken }) {
                none &= r.same(never) && r.signature() == never.signature() && r.bridgeStamp() == 0 && r.bridgeCount() == 0 && !r.roadBridges()
                        && Arrays.equals(fillOf(r, W0, Y0, w, h, false, null), fillOf(never, W0, Y0, w, h, false, null))
                        && Arrays.equals(fillOf(r, W0, Y0, w, h, true, null), fillOf(never, W0, Y0, w, h, true, null))
                        && r.frameHash(SITE + 100, SITE, 200, 100) == never.frameHash(SITE + 100, SITE, 200, 100);
            }
            check("runs handed no bridge, a null, an empty list, or a bridge and then none are the same runs: same(), signature(), fill() in both grounds and frameHash() alike", none && moved);
            check("...and the runs with the bridges are not the same as the runs without", !runs.same(never) && runs.signature() != never.signature());
        }

        void stamps() {
            List<CityMap.BridgeRun> list = List.of(run(HIGHWAY, HW), run(RAIL, RL), run(ROAD, RD)), back = List.of(run(ROAD, RD), run(RAIL, RL), run(HIGHWAY, HW));
            long stamp = CityRuns.stampOf(list);
            check("the stamp of no bridge is 0, of any list never 0; the same bridges in another order have the stamp they had",
                    CityRuns.stampOf(null) == 0 && CityRuns.stampOf(List.of()) == 0 && stamp != 0 && CityRuns.stampOf(back) == stamp && CityRuns.stampOf(List.of(run(HIGHWAY, HW))) != 0);
            check("...another list has another: one bridge fewer, a corner moved a plot, another kind over the same corners",
                    CityRuns.stampOf(list.subList(0, 2)) != stamp && CityRuns.stampOf(List.of(run(HIGHWAY, HW), run(RAIL, RL), run(ROAD, RD[0], RD[1], RD[2] + 1, RD[3]))) != stamp
                            && CityRuns.stampOf(List.of(run(RAIL, HW), run(RAIL, RL), run(ROAD, RD))) != stamp);
            CityRuns a = fresh(), b = fresh();
            a.setBridges(list);
            b.setBridges(back);
            check("runs handed the same bridges in another order are the same runs: same(), signature() and fill()", a.same(b) && a.signature() == b.signature() && a.bridgeStamp() == stamp
                    && Arrays.equals(fillOf(a, W0, Y0, w, h, true, null), fillOf(b, W0, Y0, w, h, true, null)));
            CityRuns refusing = fresh();
            refusing.setBridges(List.of(run(HIGHWAY, W1, SITE + 40, W1 + 5, SITE + 43), run(7, HW), run(HIGHWAY, W1, SITE + 40), run(RAIL, W1, SITE + 40, W1, SITE + 40), run(HIGHWAY, HW),
                    run(HIGHWAY, new long[] { 1, 2, 3 })));
            check("a run that is no path (a leg off the eight headings, a kind of none, one corner, a leg that stands still, an odd count) is not laid and is counted; the bridge beside them stands",
                    refusing.bridgesRefused() == 5 && refusing.bridgeCount() == 1 && stands(refusing, hwPath, CityRuns.F_HIGHWAY));
            CityRuns told = fresh();
            Set<Long> seen = new HashSet<>(), seenRoad = new HashSet<>();
            told.setWitness((dx, dy) -> seen.add(key(dx, dy)));
            told.setBridges(List.of(run(HIGHWAY, HW)));
            Set<Long> want = new HashSet<>();
            for (long k : hwPath) want.add(key(Math.floorDiv(kx(k), CityMap.DISTRICT), Math.floorDiv(ky(k), CityMap.DISTRICT)));
            check("the witness is told, before the bridge comes, every district a highway bridge lies in (" + want.size() + ")", seen.containsAll(want));
            told.setWitness((dx, dy) -> seenRoad.add(key(dx, dy)));
            told.setBridges(List.of(run(ROAD, RD)));
            check("...and of a road bridge's none: its ground is not dated, only the highway bridge going is told (the districts it was in)", !seenRoad.isEmpty() && seenRoad.size() <= want.size());
        }
    }

    /* =====================================================================
       THE MAP: a channel of fresh water in the default world
       ===================================================================== */

    /** The most plots wide a channel may be for the fixture: the bridges are paths of a hundred plots, not thousands. */
    static final int MOST_WIDE = 140;

    /** Rows of the band either side of its middle row. */
    static final int BAND = 24;

    /** The world's ground as the map reads it: its tiles' (World.tileTerrain(), which the painter and the runs' ground use), not a plot's own (terrainAt()), which differs along a shore. */
    static final class Ground {
        final World world;
        private final Map<Long, byte[]> tiles = new HashMap<>();

        Ground(World world) {
            this.world = world;
        }

        byte at(long x, long y) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            byte[] t = tiles.computeIfAbsent(key(tx, ty), k -> {
                byte[] b = new byte[TilePainter.PLOTS];
                world.tileTerrain(tx, ty, b);
                return b;
            });
            return t[index(x, y)];
        }
    }

    /** A tile of the map as the painter has it. */
    record Tile(TilePainter.Input in, TilePainter.Painted p) { }

    /** A tile painted, kept in `drawn`. */
    static Tile tile(CityMap map, Map<Long, Tile> drawn, long tx, long ty) {
        return drawn.computeIfAbsent(key(tx, ty), k -> {
            TilePainter.Input in = new TilePainter.Input();
            map.tileInput(tx, ty, in);
            TilePainter.Painted p = new TilePainter.Painted();
            TilePainter.paint(in, p);
            return new Tile(in, p);
        });
    }

    /** Plot (x, y)'s tile and its index in it. */
    static Tile at(CityMap map, Map<Long, Tile> drawn, long x, long y) {
        return tile(map, drawn, Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
    }

    static int index(long x, long y) {
        return (int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE));
    }

    /** A colour as the raster draws it on plot i of a tile: dimmed off the city's ground as every deck and surface is. */
    static int shade(Tile t, int i, int colour) {
        return t.in().owned[i] ? colour : TileRaster.blend(colour, TileRaster.VOID, TileRaster.UNOWNED_DIM);
    }

    /** The fixture: a land of two banks parted by a channel, the city on it drawn as the model draws it, the three bridges. */
    static final class MapFixture {
        World world;
        Ground ground;
        double[][] rects;
        CityLand land;
        BuildingVisual.Type[] types;
        long[] counts;
        int hwType, cellsPer;
        long[][] rows;                 // {y, first fresh x, last fresh x} for each row of the band
        long[] hw, rl, rd;             // the three bridges' corners
        List<CityMap.BridgeRun> bridges;
        long sx, sy;
        int built;                     // how many of the six landings stand on a building in the city drawn with no bridge
        CityMap live;                  // drawn with no bridge and handed them
        final Map<Long, Tile> liveTiles = new HashMap<>(), bareTiles = new HashMap<>();
        CityMap bare;
        Set<Long> hwPath, rlPath, rdPath;

        CityMap draw(List<CityMap.BridgeRun> list) {
            return draw(list, counts);
        }

        /** The city drawn canonically at the counts given, with the bridges (null for none). */
        CityMap draw(List<CityMap.BridgeRun> list, long[] at) {
            return CityMap.canonical(land, r -> land.amountsInOrder(r), types, at.clone(), 0, null, list);
        }

        /** The counts with every type but the highways' scaled (rounded down), so that a city drawn at them has room for the full counts. */
        long[] scaled(double by) {
            long[] out = counts.clone();
            for (int t = 0; t < out.length; t++) if (t != hwType) out[t] = (long) Math.floor(out[t] * by);
            return out;
        }

        static MapFixture build() {
            MapFixture f = new MapFixture();
            f.types = BuildingVisual.table(new BuildingCatalog().load());
            long seed = Founding.DEFAULT_WORLD_SEED;
            f.world = World.of(seed);
            long[] place = MapCheck.dryPlace(f.world);
            f.ground = new Ground(f.world);
            f.rows = channel(f.ground, place, shortest() + 6);
            check("fixture: the default world has a channel of fresh water wider than a short crossing (" + shortest() + " plots), 49 rows of it with dry land either side", f.rows != null);
            if (f.rows == null) return null;
            long aMax = Long.MIN_VALUE, bMin = Long.MAX_VALUE, yc = f.rows[BAND][0];
            long narrow = Long.MAX_VALUE, wideRow = 0;
            for (long[] r : f.rows) {
                aMax = Math.max(aMax, r[1]);
                bMin = Math.min(bMin, r[2]);
                narrow = Math.min(narrow, r[2] - r[1] + 1);
                wideRow = Math.max(wideRow, r[2] - r[1] + 1);
            }
            // The west bank to the first water, the east from the last: whole rows of the band owned, a few water plots among them where the shore bends.
            double[][] rects = f.rects = new double[][] {
                { aMax - 520, yc - 256, aMax, yc + 256 },
                { bMin + 1, yc - 256, bMin + 521, yc + 256 } };
            // The founding site: the highway line crossing nearest the west bank's middle that is dry.
            long cx = (long) (rects[0][0] + 256), cy = yc;
            long best = Long.MAX_VALUE;
            for (long ty = Math.floorDiv(cy, World.TILE) - 10; ty <= Math.floorDiv(cy, World.TILE) + 10; ty++) {
                for (long tx = Math.floorDiv(cx, World.TILE) - 10; tx <= Math.floorDiv(cx, World.TILE) + 10; tx++) {
                    long x = tx * World.TILE + CityRuns.HIGHWAY_AT, y = ty * World.TILE + CityRuns.HIGHWAY_AT;
                    if (x < rects[0][0] + 40 || x > rects[0][2] - 40 || y < yc - 200 || y > yc + 200 || !dryAround(f.ground, x, y)) continue;
                    long d = (x - cx) * (x - cx) + (y - cy) * (y - cy);
                    if (d < best) { best = d; f.sx = x; f.sy = y; }
                }
            }
            double[] centre = new double[CityLand.CENTRE_FIELDS];
            centre[CityLand.CENTRE_FIELDS - 5] = -1;
            centre[CityLand.CENTRE_FIELDS - 4] = -1;
            centre[CityLand.CENTRE_FIELDS - 2] = f.sx;
            centre[CityLand.CENTRE_FIELDS - 1] = f.sy;
            f.land = CityLand.restore(World.Key.generated(seed), centre, rects, null, null, null);
            f.hwType = GatewayCheck.highwayType(f.types);
            f.counts = new long[f.types.length];
            for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < f.counts.length; t++) {
                BuildingVisual.Type ty = f.types[t];
                if (ty == null || ty.site() != null || ty.track() || ty.sea() || ty.road() != BuildingVisual.NOT_A_ROAD) continue;
                f.counts[t] = Math.round(MapCheck.JERUS_COUNTS[t] * 5);
            }
            f.cellsPer = BuildingVisual.cells(f.types[f.hwType]);
            f.counts[f.hwType] = 40;
            System.out.printf("  a channel of %d to %d plots across, rows %d to %d, the west bank to x %d, the east from x %d; the founding site (%d, %d)%n",
                    narrow, wideRow, f.rows[0][0], f.rows[2 * BAND][0], aMax, bMin + 1, f.sx, f.sy);

            // The landings: rows whose two landings stand on a building in the city drawn with no bridge, a few rows apart.
            f.bare = f.draw(null);
            // Each row's landing on each bank: the plot nearest the shore, within APPROACH plots of it, that the city drawn with no bridge builds on - 0 for none. A landing on the
            // shore's own plot is built on in a few rows only (the lots start a few plots in), and a bridge that lands there leaves the premise of section 3 to chance, so the
            // fixture's bridges land on the building plot and carry their approach over the land between it and the water.
            int[][] landing = new int[f.rows.length][2];
            for (int i = 0; i < f.rows.length; i++) {
                long[] r = f.rows[i];
                for (int d = APPROACH; d >= 1; d--) {
                    if (f.builtAt(r[1] - d, r[0])) landing[i][0] = d;
                    if (f.builtAt(r[2] + d, r[0])) landing[i][1] = d;
                }
            }
            int[] pick = { -1, -1, -1 };
            int need = 0;
            for (int pass = 0; pass < 3 && need < 3; pass++) {
                for (int i = 2; i < f.rows.length - 2 && need < 3; i++) {
                    boolean apart = true;
                    for (int j = 0; j < need; j++) apart &= Math.abs(pick[j] - i) >= 7;
                    boolean fit = pass == 0 ? landing[i][0] > 0 && landing[i][1] > 0 : pass == 1 ? landing[i][0] > 0 || landing[i][1] > 0 : true;
                    if (apart && fit) pick[need++] = i;
                }
            }
            if (need < 3) {
                check("fixture: three rows a few plots apart whose landings can be chosen", false);
                return null;
            }
            long[][] all = new long[3][];
            for (int k = 0; k < 3; k++) {
                long[] r = f.rows[pick[k]];
                int dw = Math.max(1, landing[pick[k]][0]), de = Math.max(1, landing[pick[k]][1]);
                all[k] = new long[] { r[1] - dw, r[0], r[2] + de, r[0] };
            }
            f.hw = all[0];
            f.rl = all[1];
            f.rd = all[2];
            f.bridges = List.of(run(HIGHWAY, f.hw), run(RAIL, f.rl), run(ROAD, f.rd));
            f.hwPath = pathOf(f.hw);
            f.rlPath = pathOf(f.rl);
            f.rdPath = pathOf(f.rd);
            boolean dry = true, wet = true;
            long across = Long.MAX_VALUE;
            for (int k = 0; k < 3; k++) {
                long[] b = all[k], r = f.rows[pick[k]];
                dry &= World.dry(f.ground.at(b[0], b[1])) && World.dry(f.ground.at(b[2], b[3]));
                for (long x = r[1]; x <= r[2]; x++) wet &= f.ground.at(x, b[1]) == World.FRESH;
                for (long x = b[0] + 1; x < r[1]; x++) dry &= World.dry(f.ground.at(x, b[1]));
                for (long x = r[2] + 1; x < b[2]; x++) dry &= World.dry(f.ground.at(x, b[1]));
                across = Math.min(across, r[2] - r[1] + 1);
            }
            for (Set<Long> path : List.of(f.hwPath, f.rlPath, f.rdPath)) for (long k : path) if (f.builtAt(kx(k), ky(k))) f.built++;
            check("fixture: each bridge runs along a row, from a dry landing on the west bank over the fresh water of the channel to a dry landing on the east, the channel wider than a short crossing",
                    dry && wet && across > shortest());
            System.out.printf("  bridges: highway %d plots from landing to landing along row %d, rail %d along row %d, road %d along row %d; the channel %d plots across at the narrowest; "
                    + "%d plots of their paths are built on in the city drawn without them%n",
                    f.hw[2] - f.hw[0] - 1, f.hw[1], f.rl[2] - f.rl[0] - 1, f.rl[1], f.rd[2] - f.rd[0] - 1, f.rd[1], across, f.built);
            return f;
        }

        /** How far inland of the shore a bridge's landing may be chosen, in plots: the city builds on a bank's first plots in a few rows only. */
        static final int APPROACH = 14;

        /** Whether plot (x, y) stands under a building in the city drawn with no bridge. */
        boolean builtAt(long x, long y) {
            return at(bare, bareTiles, x, y).p().use[index(x, y)] == TilePainter.BUILDING;
        }

        /** The map with the bridges handed and the month laid, drawn from the city with none. */
        void handed() {
            if (live != null) return;
            live = draw(null);
            live.bridgePicture(bridges);
            live.reconcile(counts, 1);
        }

        /** The tiles' plots of a path that are water (fresh), and the dry ones. */
        List<long[]> plots(Set<Long> path, boolean water) {
            List<long[]> out = new ArrayList<>();
            for (long k : path) {
                boolean wet = at(live, liveTiles, kx(k), ky(k)).in().terrain[index(kx(k), ky(k))] == World.FRESH;
                if (wet == water) out.add(new long[] { kx(k), ky(k) });
            }
            return out;
        }

        void highway() {
            handed();
            CityRuns runs = live.runs();
            check("the map lays the three bridges at the month's reconcile: one of each kind, none refused, the picture and its stamp kept",
                    runs.bridgeCount() == 3 && runs.bridgeCount(HIGHWAY) == 1 && runs.bridgeCount(RAIL) == 1 && runs.bridgeCount(ROAD) == 1 && runs.bridgesRefused() == 0
                            && live.bridgePicture().size() == 3 && live.bridgeStamp() == CityRuns.stampOf(bridges) && runs.bridgeStamp() == live.bridgeStamp());
            boolean laid = true, fixed = true, bridging = true, landing = true, dryNot = true;
            int water = 0;
            for (long k : hwPath) {
                long x = kx(k), y = ky(k);
                Tile t = at(live, liveTiles, x, y);
                int i = index(x, y);
                laid &= runs.at(x, y) == CityRuns.F_HIGHWAY;
                fixed &= t.in().fixed[i] == CityRuns.F_HIGHWAY && t.p().run[i] == CityRuns.F_HIGHWAY;
                boolean wet = t.in().terrain[i] == World.FRESH;
                if (wet) water++;
                bridging &= t.p().bridge[i] == wet;
            }
            landing = runs.at(hw[0], hw[1]) == CityRuns.F_HIGHWAY && runs.at(hw[2], hw[3]) == CityRuns.F_HIGHWAY
                    && !at(live, liveTiles, hw[0], hw[1]).p().bridge[index(hw[0], hw[1])] && !at(live, liveTiles, hw[2], hw[3]).p().bridge[index(hw[2], hw[3])];
            check("every plot of the highway bridge's path is a highway plot of the map's runs, from the near landing to the far (" + hwPath.size() + " plots, " + water + " of them over the water)",
                    laid && water == freshAlong(hw) && landing);
            check("...it is the tile's highway plot too (the painter's Input.fixed and Painted.run)", fixed);
            check("...the plots of it over the water are the painter's bridge plots, its landings are not", bridging && landing);
            // The raster: the highway's surface on every plot of the path; over the water the deck beside it (a highway's own width hides the deck below 24 px a plot, where it is a pixel
            // either side of the casing at 32), none on the landings and approaches.
            boolean surface = true, deck = true, plain = true;
            for (long k : hwPath) {
                long x = kx(k), y = ky(k);
                Tile t = at(live, liveTiles, x, y);
                int i = index(x, y);
                if (t.p().use[i] == TilePainter.BUILDING) continue;
                surface &= draws(x, y, TileRaster.ROAD[BuildingVisual.HIGHWAY], 8);
                if (t.in().terrain[i] == World.FRESH) deck &= draws(x, y, TileRaster.DECK, 32);
                else plain &= !draws(x, y, TileRaster.DECK, 32);
            }
            check("...the raster draws the highway's surface on every plot of it, TileRaster.DECK beside the highway's casing over the water and none on its landings and approaches", surface && deck && plain);
        }

        /** Tile images at a width of px pixels a plot, kept by tile and width. */
        final Map<String, int[]> images = new HashMap<>();

        int[] image(Tile t, int px) {
            return images.computeIfAbsent(System.identityHashCode(t) + ":" + px, k -> {
                int[] img = new int[World.TILE * px * World.TILE * px];
                TileRaster.raster(t.in(), t.p(), px, img);
                return img;
            });
        }

        /** Whether plot (x, y) of the tile it lies in draws `colour` (dimmed as the plot's ownership has it) at px pixels a plot. */
        boolean draws(long x, long y, int colour, int px) {
            Tile t = at(live, liveTiles, x, y);
            int i = index(x, y);
            return JunctionCheck.hasColour(image(t, px), World.TILE * px, (int) Math.floorMod(x, World.TILE), (int) Math.floorMod(y, World.TILE), px, shade(t, i, colour));
        }

        void railAndRoad() {
            handed();
            CityRuns runs = live.runs();
            // The railway's bridge.
            boolean laid = true, fixed = true, track = true, bridging = true, deck = true, landings = true, plain = true;
            int water = 0;
            for (long k : rlPath) {
                long x = kx(k), y = ky(k);
                Tile t = at(live, liveTiles, x, y);
                int i = index(x, y);
                boolean wet = t.in().terrain[i] == World.FRESH;
                laid &= runs.at(x, y) == CityRuns.F_RAIL;
                fixed &= t.in().fixed[i] == CityRuns.F_RAIL && t.p().run[i] == CityRuns.F_RAIL;
                if (t.p().use[i] != TilePainter.BUILDING) track &= t.p().use[i] == TilePainter.RAIL;
                bridging &= t.p().bridge[i] == wet;
                if (wet) {
                    water++;
                    deck &= draws(x, y, TileRaster.DECK, 8);
                } else plain &= !draws(x, y, TileRaster.DECK, 8);
            }
            check("every plot of the rail bridge's path is a railway plot of the map's runs, the tile's Input.fixed and the painter's track (" + rlPath.size() + " plots, " + water + " over the water)",
                    laid && fixed && track && water == freshAlong(rl) && runs.at(rl[0], rl[1]) == CityRuns.F_RAIL && runs.at(rl[2], rl[3]) == CityRuns.F_RAIL);
            check("...the plots of it over the water are the painter's bridge plots and draw TileRaster.DECK beside the bed; its landings are ordinary track: no bridge plot, no deck", bridging && deck && plain);
            // The road bridge.
            laid = true;
            fixed = true;
            bridging = true;
            deck = true;
            plain = true;
            boolean paved = true, joins = true;
            water = 0;
            for (long k : rdPath) {
                long x = kx(k), y = ky(k);
                Tile t = at(live, liveTiles, x, y);
                int i = index(x, y);
                boolean wet = t.in().terrain[i] == World.FRESH;
                joins &= runs.at(x, y) == 0 && t.in().fixed[i] == 0 && t.p().run[i] == 0;
                if (t.p().use[i] != TilePainter.BUILDING) paved &= t.p().use[i] == TilePainter.ROAD && t.p().road[i] == BuildingVisual.PAVED && t.p().width[i] == 1;
                bridging &= t.p().bridge[i] == wet;
                if (wet) {
                    water++;
                    deck &= draws(x, y, TileRaster.DECK, 8) && draws(x, y, TileRaster.ROAD[BuildingVisual.PAVED], 8);
                } else plain &= !draws(x, y, TileRaster.DECK, 8);
            }
            check("every plot of the road bridge's path is a paved street of the painter (a half plot wide, the surface of a street) and none is a plot of any net: the runs' at() and the tile's Input.fixed hold none",
                    paved && joins && water > TilePainter.STREET_BRIDGE);
            check("...the plots of it over the water are bridge plots and draw TileRaster.DECK beside the paved surface; its landings draw the street and no deck", bridging && deck && plain);
            CityRuns bare = this.bare.runs();
            check("the nets are what they were with the city drawn without the bridges: the highways' plots, the railway's, and what the highways are short of",
                    runs.highways.plots == bare.highways.plots && runs.rail.plots == bare.rail.plots && runs.highwayShort() == bare.highwayShort() && runs.trackSurplus() == bare.trackSurplus());
        }

        /** How many plots of a bridge's row between its landings are fresh water, by the ground the map reads. */
        long freshAlong(long[] b) {
            long n = 0;
            for (long x = b[0] + 1; x < b[2]; x++) if (ground.at(x, b[1]) == World.FRESH) n++;
            return n;
        }

        /** The buildings and fields on the plots of the three paths in a map's tiles (kept in `tiles`): each as its tile, box and type; fields as "field" and the plot. */
        Set<String> onPaths(CityMap map, Map<Long, Tile> tiles) {
            Set<String> out = new HashSet<>();
            for (Set<Long> path : List.of(hwPath, rlPath, rdPath)) {
                for (long k : path) {
                    Tile t = at(map, tiles, kx(k), ky(k));
                    int i = index(kx(k), ky(k));
                    long tx = Math.floorDiv(kx(k), World.TILE), ty = Math.floorDiv(ky(k), World.TILE);
                    if (t.p().use[i] == TilePainter.BUILDING || t.p().bld[i] != 0) {
                        int b = t.p().bld[i] - 1;
                        out.add(b < 0 ? "building at " + k : "type " + t.p().btype[b] + " box " + (tx * World.TILE + t.p().bx[b]) + "," + (ty * World.TILE + t.p().by[b]));
                    }
                    if (t.p().use[i] == TilePainter.FIELD) out.add("field at " + k);
                }
            }
            return out;
        }

        /** How many buildings the tiles the three paths run through hold (each counted on the tile it is counted on). */
        int buildingsAlong(Map<Long, Tile> tiles, CityMap map) {
            Set<Long> seen = new HashSet<>();
            int n = 0;
            for (Set<Long> path : List.of(hwPath, rlPath, rdPath)) {
                for (long k : path) {
                    long tx = Math.floorDiv(kx(k), World.TILE), ty = Math.floorDiv(ky(k), World.TILE);
                    if (seen.add(key(tx, ty))) n += tile(map, tiles, tx, ty).p().buildings;
                }
            }
            return n;
        }

        void clearance() {
            handed();
            CityRuns runs = live.runs();
            // The bridges stand before the city builds (the city drawn with them): the plans never place a building or a field on a plot of any path.
            CityMap cano = draw(bridges);
            Map<Long, Tile> canoTiles = new HashMap<>();
            Set<String> placed = onPaths(cano, canoTiles);
            int plots = hwPath.size() + rlPath.size() + rdPath.size();
            check("premise: in the same city drawn with no bridge " + built + " plots of their paths stand under a building", built >= 3);
            check("with the bridges handed before the city is built, the plans drawn: no building and no field stands on a plot of any path - landings, approaches and decks (" + plots + " plots)",
                    placed.isEmpty());
            System.out.println("  buildings on the paths' plots: " + built + " without the bridges, " + placed.size() + " with them");
            // The bridges handed to a city already built (H4: what stands stays, as under a highway laid later); what comes after goes round them.
            long[] smaller = scaled(0.5);
            CityMap grow = draw(null, smaller);
            grow.bridgePicture(bridges);
            grow.reconcile(smaller, 1);
            Map<Long, Tile> growTiles = new HashMap<>();
            Set<String> before = onPaths(grow, growTiles);
            int alongBefore = buildingsAlong(growTiles, grow);
            grow.reconcile(counts, 2);
            growTiles.clear();
            Set<String> after = onPaths(grow, growTiles);
            int alongAfter = buildingsAlong(growTiles, grow);
            System.out.println("  the city grown to twice its size with the bridges standing: " + alongBefore + " buildings along the paths, then " + alongAfter + "; on their plots " + before.size() + ", then " + after.size());
            CityMap twin = draw(null, smaller);
            twin.reconcile(smaller, 1);
            twin.reconcile(counts, 2);
            Set<String> without = onPaths(twin, new HashMap<>());
            without.removeIf(s -> s.startsWith("field"));
            check("premise: the growth placed buildings in the tiles the paths run through (" + alongBefore + " to " + alongAfter + "), and the same growth of the city with no bridge puts "
                    + without.size() + " buildings on the plots of their paths", alongAfter > alongBefore && without.size() > before.size());
            check("...and with them not one on a plot of any path that was not there before: what stood stays, what comes goes round (H4)", before.containsAll(after));
            // The plans' input: the district holding a landing sees the bridge there as a highway's, the railway's or a reserve.
            boolean sees = true;
            long[][] ends = { { hw[0], hw[1], DistrictPlan.FIXED_HIGHWAY }, { hw[2], hw[3], DistrictPlan.FIXED_HIGHWAY }, { rl[0], rl[1], DistrictPlan.FIXED_RAIL }, { rl[2], rl[3], DistrictPlan.FIXED_RAIL },
                { rd[0], rd[1], DistrictPlan.FIXED_RESERVE }, { rd[2], rd[3], DistrictPlan.FIXED_RESERVE } };
            for (long[] e : ends) {
                CityMap.District d = live.districtOfTile(Math.floorDiv(e[0], World.TILE), Math.floorDiv(e[1], World.TILE));
                if (d == null) { sees = false; continue; }
                byte[] fixed = new byte[DistrictPlan.FRAME * DistrictPlan.FRAME];
                live.runsInto(d, fixed);
                long x0 = (live.baseDX() + d.dx) * CityMap.DISTRICT, y0 = (live.baseDY() + d.dy) * CityMap.DISTRICT;
                sees &= fixed[(int) ((e[1] - y0) * DistrictPlan.FRAME + e[0] - x0)] == (byte) e[2];
            }
            check("...the plan's input holds the landings as the highway's, the railway's and a road's reserve (runsInto(): FIXED_HIGHWAY, FIXED_RAIL, FIXED_RESERVE)", sees);
            check("...and the plans of the districts a bridge crosses were drawn again: such a district's plan stamp moved with the bridge handed, a district it does not cross has the one it had",
                    stampMoved());
        }

        /** Whether the plan stamp of the district holding the highway's near landing differs from the city's drawn without the bridges, and a district no bridge crosses has the one it had. */
        boolean stampMoved() {
            long fx = (long) rects[0][0] + 30, fy = (long) ((rects[0][1] + rects[0][3]) / 2);
            CityMap.District near = live.districtOfTile(Math.floorDiv(hw[0], World.TILE), Math.floorDiv(hw[1], World.TILE));
            CityMap.District nearBare = bare.districtOfTile(Math.floorDiv(hw[0], World.TILE), Math.floorDiv(hw[1], World.TILE));
            CityMap.District far = live.districtOfTile(Math.floorDiv(fx, World.TILE), Math.floorDiv(fy, World.TILE));
            CityMap.District farBare = bare.districtOfTile(Math.floorDiv(fx, World.TILE), Math.floorDiv(fy, World.TILE));
            return near != null && nearBare != null && far != null && farBare != null && live.ownStamp(near) != bare.ownStamp(nearBare) && live.ownStamp(far) == bare.ownStamp(farBare);
        }

        /** The city drawn with the bridges standing before it is built: kept. */
        CityMap cano;

        CityMap canonical() {
            if (cano == null) cano = draw(bridges);
            return cano;
        }

        /** Whether every plot of every bridge is on the map's runs as it was laid: the highway's, the railway's, the road's (by its own kind: it is no plot of the runs). */
        boolean bridgesStand(CityMap map) {
            CityRuns runs = map.runs();
            boolean ok = runs.bridgeCount() == 3;
            for (long k : hwPath) ok &= runs.at(kx(k), ky(k)) == CityRuns.F_HIGHWAY && runs.onBridge(HIGHWAY, kx(k), ky(k));
            for (long k : rlPath) ok &= runs.at(kx(k), ky(k)) == CityRuns.F_RAIL && runs.onBridge(RAIL, kx(k), ky(k));
            for (long k : rdPath) ok &= runs.at(kx(k), ky(k)) == 0 && runs.onBridge(ROAD, kx(k), ky(k));
            return ok;
        }

        void unmoved() {
            handed();
            CityRuns runs = live.runs();
            long[] c = counts.clone();
            boolean prefix = true, budget = true, stand = bridgesStand(live);
            long plots = runs.highways.plots;
            int month = 2;
            StringBuilder trace = new StringBuilder();
            for (int step : new int[] { 30, -45, 60 }) {
                Map<String, List<long[]>> was = IslandNetCheck.armsOf(runs.highways);
                c[hwType] += step;
                live.reconcile(c, month++);
                Map<String, List<long[]>> now = IslandNetCheck.armsOf(runs.highways);
                prefix &= step > 0 ? IslandNetCheck.prefix(was, now) : IslandNetCheck.prefix(now, was);
                budget &= runs.highways.plots == c[hwType] * cellsPer && runs.highwayShort() == 0;
                stand &= bridgesStand(live);
                trace.append(String.format(" %+d: %,d plots", step, runs.highways.plots));
            }
            System.out.println("  the highways' plots month by month, from " + plots + ":" + trace);
            check("months with more highway plots in the model, then fewer, then more: the net grows from its newest end and is taken back from it, every arm a prefix of the next month's",
                    prefix && runs.highways.plots == c[hwType] * cellsPer);
            check("...the model's plots are all laid every month: the bridge spends none of the budget (highwayShort() is 0)", budget);
            check("...and every plot of every bridge is where it was, month after month: the highway's, the railway's, the road's", stand);
            // The plans' tiles drawn again after the months: the bridges are what the painter reads, and the journal of the runs holds.
            liveTiles.clear();
            images.clear();
            boolean fixed = true;
            for (long k : hwPath) fixed &= at(live, liveTiles, kx(k), ky(k)).in().fixed[index(kx(k), ky(k))] == CityRuns.F_HIGHWAY;
            for (long k : rlPath) fixed &= at(live, liveTiles, kx(k), ky(k)).in().fixed[index(kx(k), ky(k))] == CityRuns.F_RAIL;
            check("...the tiles drawn after the months hold them in the painter's input, and the journal of the runs ends on the runs as they stand (journalDropped() is " + live.journalDropped() + ")",
                    fixed && live.journalDropped() == 0);
            // canonical() lays the bridges the reconcile laid.
            CityMap cano = canonical();
            boolean same = cano.runs().bridgeStamp() == bridgesStamp() && bridgesStand(cano);
            check("a city drawn with the bridges standing (canonical()) has them laid as the month's reconcile laid them: the same stamp, the same plots", same);
        }

        long bridgesStamp() {
            return CityRuns.stampOf(bridges);
        }

        /** The tiles a sample of the raster checks look at: those of each path's landings and middle. */
        List<long[]> sample() {
            List<long[]> out = new ArrayList<>();
            for (long[] b : new long[][] { hw, rl, rd }) {
                for (long x : new long[] { b[0], (b[0] + b[2]) / 2, b[2] }) out.add(new long[] { Math.floorDiv(x, World.TILE), Math.floorDiv(b[1], World.TILE) });
            }
            return out;
        }

        /** A hash of the raster of the sample of tiles, 4 px a plot. */
        long rasterHash(CityMap map) {
            Map<Long, Tile> tiles = new HashMap<>();
            long h = 17;
            for (long[] t : sample()) {
                Tile tl = tile(map, tiles, t[0], t[1]);
                int[] img = new int[World.TILE * 4 * World.TILE * 4];
                TileRaster.raster(tl.in(), tl.p(), 4, img);
                h = h * 31 + Arrays.hashCode(img);
            }
            return h;
        }

        void unchanged() {
            CityMap a = draw(null), b = draw(null), c = draw(null), d = draw(null);
            a.reconcile(counts, 1);
            b.bridgePicture(null);
            b.reconcile(counts, 1);
            c.bridgePicture(List.of());
            c.reconcile(counts, 1);
            d.bridgePicture(bridges);
            d.reconcile(counts, 1);
            d.bridgePicture(null);
            d.reconcile(counts, 2);
            byte[] sa = a.writeSidecar(1), sb = b.writeSidecar(1), sc = c.writeSidecar(1);
            long ra = rasterHash(a), rb = rasterHash(b), rc = rasterHash(c);
            check("a city handed no picture, a null one and an empty list is the same map: the same runs, none of it a bridge's", a.runs().same(b.runs()) && a.runs().same(c.runs())
                    && a.bridgeStamp() == 0 && b.bridgeStamp() == 0 && c.bridgeStamp() == 0 && a.runs().bridgeCount() == 0 && c.bridgePicture().isEmpty());
            check("...the same sidecar, byte for byte, and so the same stamp (" + String.format("%016x", CityMap.stampIn(sa)) + ")", Arrays.equals(sa, sb) && Arrays.equals(sa, sc) && CityMap.stampIn(sa) == CityMap.stampIn(sb));
            check("...the same raster on the tiles of the landings and the middle of the channel (" + sample().size() + " tiles at 4 px a plot)", ra == rb && ra == rc);
            // A bridge handed and taken back.
            boolean plots = true;
            for (Set<Long> path : List.of(hwPath, rlPath, rdPath)) for (long k : path) plots &= d.runs().at(kx(k), ky(k)) == a.runs().at(kx(k), ky(k)) && !d.runs().onBridge(HIGHWAY, kx(k), ky(k));
            check("a city handed the bridges and then none has the runs it had: the same runs, plot for plot, and no bridge among them", d.runs().same(a.runs()) && d.runs().bridgeCount() == 0 && plots
                    && d.runs().bridgeStamp() == 0 && d.bridgeStamp() == 0 && d.bridgePicture().isEmpty());
            CityRuns with = live.runs();
            check("...and the city with the bridges is not the same map", !live.runs().same(a.runs()) && with.bridgeCount() == 3);
        }

        void stamps() {
            handed();
            List<CityMap.BridgeRun> back = new ArrayList<>(bridges);
            Collections.reverse(back);
            CityMap p = draw(null);
            p.reconcile(counts, 1);
            long versionWas = p.runs().version();
            byte[] s0 = p.writeSidecar(1);
            long stamp0 = CityMap.stampIn(s0);
            CityMap.District near = p.districtOfTile(Math.floorDiv(hw[0], World.TILE), Math.floorDiv(hw[1], World.TILE));
            long planWas = p.ownStamp(near);
            p.bridgePicture(bridges);
            p.reconcile(counts, 1);
            byte[] s1 = p.writeSidecar(1);
            long stamp1 = CityMap.stampIn(s1);
            check("the stamp moves when a bridge is handed: the map's bridge stamp, the runs' version, the district's plan stamp and the sidecar's stamp",
                    p.bridgeStamp() == bridgesStamp() && p.bridgeStamp() != 0 && p.runs().version() != versionWas && p.ownStamp(near) != planWas && stamp1 != stamp0 && !Arrays.equals(s0, s1));
            // The same bridges in another order, a stale picture: the same stamp, the same file.
            p.bridgePicture(back);
            p.reconcile(counts, 1);
            check("the same bridges in another order are the one stamp and the one sidecar, byte for byte", p.bridgeStamp() == bridgesStamp() && Arrays.equals(p.writeSidecar(1), s1));
            // The sidecars read back.
            java.util.function.Function<Resource, double[]> rem = r -> land.amountsInOrder(r);
            CityMap before = CityMap.readSidecar(s0, land, rem, types, 1, stamp0, null, null);
            CityMap beforeUnder = CityMap.readSidecar(s0, land, rem, types, 1, stamp0, null, bridges);
            CityMap after = CityMap.readSidecar(s1, land, rem, types, 1, stamp1, null, bridges);
            CityMap afterBack = CityMap.readSidecar(s1, land, rem, types, 1, stamp1, null, back);
            CityMap afterNone = CityMap.readSidecar(s1, land, rem, types, 1, stamp1, null, null);
            CityMap afterOther = CityMap.readSidecar(s1, land, rem, types, 1, stamp1, null, List.of(bridges.get(0), bridges.get(1)));
            CityMap afterMoved = CityMap.readSidecar(s1, land, rem, types, 1, stamp1, null, List.of(bridges.get(0), bridges.get(1), run(ROAD, rd[0], rd[1], rd[2] + 1, rd[3])));
            check("a sidecar written before the bridges is read back with none and is not read back under them (the land stamp moved)", before != null && before.runs().bridgeCount() == 0 && beforeUnder == null);
            check("...one written after is read back under the list it was written with (in any order) and under no other: none, one bridge fewer, a corner moved",
                    after != null && afterBack != null && afterNone == null && afterOther == null && afterMoved == null);
            check("...and what is read back is the map it was written from, the bridges laid on its runs: the same map, the same stamp, every plot of every bridge where it was",
                    after != null && after.same(p) && after.bridgeStamp() == p.bridgeStamp() && bridgesStand(after) && afterBack.same(p));
            check("...with the highways, the railway and the yards what they were: the runs read from the file are laid with the bridges, not from them",
                    after != null && after.runs().highways.plots == p.runs().highways.plots && after.runs().signature() == p.runs().signature());
        }
    }

    /** Whether the plots about (x, y), 20 each way, are all dry ground. */
    static boolean dryAround(Ground world, long x, long y) {
        for (long dy = -20; dy <= 20; dy += 4) for (long dx = -20; dx <= 20; dx += 4) if (!World.dry(world.at(x + dx, y + dy))) return false;
        return true;
    }

    /**
     * The first channel of fresh water in the world near `place`: 2 * BAND + 1 rows, each {y, first fresh x, last fresh x} of the run through the
     * middle row's centre, every run at least `minWide` plots and at most MOST_WIDE, eight dry plots in a line beyond each end of it, and
     * dry ground (nine in ten of the plots sampled) on a bank 520 plots across either side. Null for none.
     */
    static long[][] channel(Ground ground, long[] place, int minWide) {
        World world = ground.world;
        for (long y = place[1] - 6000; y <= place[1] + 6000; y += 32) {
            long runStart = -1;
            for (long x = place[0] - 6000; x <= place[0] + 6000; x += 2) {
                boolean fresh = world.terrainAt(x, y) == World.FRESH;
                if (fresh) {
                    if (runStart < 0) runStart = x;
                    continue;
                }
                if (runStart < 0) continue;
                long len = x - runStart, mid = runStart + len / 2;
                runStart = -1;
                if (len < minWide - 4 || len > MOST_WIDE) continue;
                long[][] rows = band(ground, y, mid, minWide);
                if (rows != null) return rows;
            }
        }
        return null;
    }

    static long[][] band(Ground world, long yc, long xMid, int minWide) {
        long[][] rows = new long[2 * BAND + 1][];
        long aMax = Long.MIN_VALUE, bMin = Long.MAX_VALUE;
        for (int i = 0; i < rows.length; i++) {
            long y = yc - BAND + i;
            if (world.at(xMid, y) != World.FRESH) return null;
            long a = xMid, b = xMid;
            while (world.at(a - 1, y) == World.FRESH && xMid - a < MOST_WIDE) a--;
            while (world.at(b + 1, y) == World.FRESH && b - xMid < MOST_WIDE) b++;
            if (b - a + 1 < minWide || b - a + 1 > MOST_WIDE) return null;
            for (long k = 1; k <= 8; k++) if (!World.dry(world.at(a - k, y)) || !World.dry(world.at(b + k, y))) return null;
            rows[i] = new long[] { y, a, b };
            aMax = Math.max(aMax, a);
            bMin = Math.min(bMin, b);
        }
        int dry = 0, seen = 0;
        for (long y = yc - 256; y < yc + 256; y += 8) {
            for (long k = 8; k <= 520; k += 8) {
                seen += 2;
                if (World.dry(world.at(aMax - k, y))) dry++;
                if (World.dry(world.at(bMin + k, y))) dry++;
            }
        }
        return dry >= 0.9 * seen ? rows : null;
    }
}
