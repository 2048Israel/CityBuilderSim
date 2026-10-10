package ham.citybuildersim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over owned sea, and each tank farm's box at the water, laid city-wide in the order the city builds them - never moved, the newest of a type taken first - what CityMap fixes in its districts' plans, the tile painter draws and the boats come alongside; kept in the map's sidecar.
 *
 * WHY THIS EXISTS (0.7.97, batch O13; the project's spec-roads-and-ports.md
 * 2.8: "Ports and tank farms: estate cells on the shore, the berths over owned
 * sea"; runs/spec-oil.md 2.12: "terminals stand on owned coast with a
 * quay"). Until 0.7.96 a sea terminal was one more industry building in its
 * district's plan, wherever that district's estate cells had room: the
 * playtest's 23 General Cargo Terminals stood in five districts, inland as
 * often as not, and the boats ran from points with no water under them. A
 * district's plan does not know the city's shore, and a boat's route starts
 * at its quay, which a frame needs whether that district is planned or not.
 * So the works are laid as the railway's yards are (CityRuns, YARDS):
 * city-wide, before any plan, kept, and every plan drawn round them.
 *
 * THE RULE (star O13-3). A work goes on the city's shore nearest the founding
 * site: district by district in the map's order (CityMap.DISTRICT_ORDER),
 * each district's tiles nearest the site first, each tile's plots in rows.
 * A plot of owned dry ground with owned salt water beside it is a shore plot;
 * the work's box stands inland of it with the plot at the middle of its sea
 * side, either way round, inside one cell's interior (as every box of a plan
 * and every yard is: off the arterials, on one tile). Its box is owned dry
 * ground on no run of the city's (highway, track or yard), on no mined site,
 * a plot clear of every other work, and no highway touches it, corners
 * included (H5). A terminal's quay runs from that plot's side QUAY_PLOTS of
 * its cargo straight out, every plot owned salt water and no other work's.
 * Of the spots a district holds, one within TilePainter.REACH of its cell's
 * ring - where the cell's arterials run when the plan opens it - comes first
 * (star O13-4); the first district with a spot has the work. A city with no
 * spot, or past SEARCH_TILES_MOST tiles looked at, counts the work short;
 * its district's plan draws it among its industry, as a yard with no place
 * is drawn, and the search waits for the ground to change.
 *
 * NEVER MOVED (H4, as the runs). The works are laid in the order the model's
 * counts grow, kept in the sidecar (CityMap FORMAT 6); one fewer of a type
 * takes its newest. A FORMAT 5 sidecar, which has none, has them laid from its
 * counts as it is read.
 */
public final class CityShore {

    /**
     * A terminal's quay out over the sea, in plots, by Ports.Cargo's ordinal:
     * the research's quay a berth (4.1) at its middle, in whole plots of 30 m -
     * a tanker berth's 270 to 345 m [P1] 10, a bulk berth's about 300 m 10, a
     * container berth's 350 to 475 m [P6][P7] 14, a general cargo berth's
     * 180 m [P12] 6.
     */
    public static final int[] QUAY_PLOTS = { 10, 10, 14, 6 };

    /** Plots kept clear between two works, and between a work and a highway (H5's verge): one. */
    public static final int CLEAR = 1;

    /** The most tiles one search looks at before it counts the work short: 4,096 - 64 districts' worth, about 0.3 s, so a city of billions with no free shore never pays more. */
    public static final int SEARCH_TILES_MOST = 4096;

    /** North, east, south, west: the sea's side of a work, its quay's heading. */
    static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /**
     * One work: its box {x0, y0, w, h} in world plots, its type id, the side
     * the sea is on (0 north to 3 west), and its quay's length in plots (0
     * for a tank farm, which has none).
     */
    public record Work(long x0, long y0, int w, int h, int type, int side, int quay) {
        /** Whether a boat comes alongside it: a terminal's quay. */
        public boolean berthed() { return quay > 0; }

        /** The plot of the box at the middle of its sea side, {x, y}: where its quay starts from. */
        public long[] shorePlot() {
            switch (side) {
                case 0:  return new long[] { x0 + w / 2, y0 };
                case 1:  return new long[] { x0 + w - 1, y0 + h / 2 };
                case 2:  return new long[] { x0 + w / 2, y0 + h - 1 };
                default: return new long[] { x0, y0 + h / 2 };
            }
        }

        /** The quay's k-th plot out (k from 1 to quay), {x, y}. */
        public long[] quayPlot(int k) {
            long[] s = shorePlot();
            return new long[] { s[0] + (long) DX[side] * k, s[1] + (long) DY[side] * k };
        }

        /** Where a boat lies at it: the middle of the plot just past its quay's end, {x, y} in plots - its route's first point. */
        public double[] berth() {
            long[] e = quayPlot(quay + 1);
            return new double[] { e[0] + 0.5, e[1] + 0.5 };
        }

        /** Whether world plot (x, y) is its box's. */
        public boolean holds(long x, long y) { return x >= x0 && y >= y0 && x < x0 + w && y < y0 + h; }

        /** Whether world plot (x, y) is one of its quay's plots. */
        public boolean quayAt(long x, long y) {
            if (quay <= 0) return false;
            long[] s = shorePlot();
            long ax = x - s[0], ay = y - s[1];
            if (DX[side] != 0) return ay == 0 && ax * DX[side] >= 1 && ax * DX[side] <= quay;
            return ax == 0 && ay * DY[side] >= 1 && ay * DY[side] <= quay;
        }
    }

    /** The works, in the order laid. */
    final List<Work> works = new ArrayList<>();

    /** Moves with every work laid or taken: what the plans and the routes are kept against. */
    private long version;

    /** The works, in the order laid. */
    public List<Work> works() { return java.util.Collections.unmodifiableList(works); }

    /** Whether there are none. */
    public boolean isEmpty() { return works.isEmpty(); }

    /** A number that moves with every work laid or taken. */
    public long version() { return version; }

    /** How many works of type t are laid. */
    public int count(int t) {
        int n = 0;
        for (Work w : works) if (w.type() == t) n++;
        return n;
    }

    /** A work laid (CityMap's THE SHORE finds its place). */
    void add(Work w) {
        works.add(w);
        version++;
    }

    /** The newest work of type t taken away; false when there is none. */
    boolean removeNewest(int t) {
        for (int i = works.size() - 1; i >= 0; i--) {
            if (works.get(i).type() != t) continue;
            works.remove(i);
            version++;
            return true;
        }
        return false;
    }

    /** The work whose box holds world plot (x, y), or null. */
    public Work at(long x, long y) {
        for (Work w : works) if (w.holds(x, y)) return w;
        return null;
    }

    /** Whether world plot (x, y) is a work's box or within CLEAR of one, or a quay's: where the runs may not go and no other work may stand. */
    public boolean blocks(long x, long y) {
        for (Work w : works) {
            if (x >= w.x0() - CLEAR && y >= w.y0() - CLEAR && x < w.x0() + w.w() + CLEAR && y < w.y0() + w.h() + CLEAR) return true;
            if (w.quayAt(x, y)) return true;
        }
        return false;
    }

    /**
     * The works' boxes over the w x h plots from world plot (x0, y0), row by
     * row: `code` over every plot of a box (a plan's ground: DistrictPlan's
     * FIXED_YARD, no street crosses it, no building stands on it). Returns a
     * hash of what it laid, which a plan's stamp reads.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte code) {
        long hash = World.mix(0x53484F52L);
        for (Work k : works) {
            if (k.x0() >= x0 + w || k.y0() >= y0 + h || k.x0() + k.w() <= x0 || k.y0() + k.h() <= y0) continue;
            for (long py = Math.max(k.y0(), y0); py < Math.min(k.y0() + k.h(), y0 + h); py++) {
                for (long px = Math.max(k.x0(), x0); px < Math.min(k.x0() + k.w(), x0 + w); px++) fixed[(int) ((py - y0) * w + (px - x0))] = code;
            }
            hash = World.mix(hash ^ k.x0() * 0x9E3779B97F4A7C15L ^ k.y0() ^ ((long) k.w() << 40) ^ ((long) k.h() << 48) ^ ((long) k.type() << 20));
        }
        return hash;
    }

    /** ...the same hash without drawing them: what a district's plan is kept against. */
    public long frameHash(long x0, long y0, int w, int h) {
        long hash = World.mix(0x53484F52L);
        for (Work k : works) {
            if (k.x0() >= x0 + w || k.y0() >= y0 + h || k.x0() + k.w() <= x0 || k.y0() + k.h() <= y0) continue;
            hash = World.mix(hash ^ k.x0() * 0x9E3779B97F4A7C15L ^ k.y0() ^ ((long) k.w() << 40) ^ ((long) k.h() << 48) ^ ((long) k.type() << 20));
        }
        return hash;
    }

    /** The works that are berths: each terminal's quay end as BoatSchedule's berth, in the order laid. */
    public List<BoatSchedule.Berth> berths(BuildingVisual.Type[] types) {
        List<BoatSchedule.Berth> out = new ArrayList<>();
        for (Work w : works) {
            if (!w.berthed() || w.type() < 0 || w.type() >= types.length || types[w.type()] == null || types[w.type()].berth() < 0) continue;
            double[] b = w.berth();
            out.add(new BoatSchedule.Berth(Ports.Cargo.values()[types[w.type()].berth()], (long) Math.floor(b[0]), (long) Math.floor(b[1])));
        }
        return out;
    }

    /* ----- the sidecar ----- */

    /** Writes the works (CityMap FORMAT 6): their count, then each one's box, type, side and quay. */
    void write(DataOutputStream out) throws IOException {
        out.writeInt(works.size());
        for (Work w : works) {
            out.writeLong(w.x0());
            out.writeLong(w.y0());
            out.writeInt(w.w());
            out.writeInt(w.h());
            out.writeInt(w.type());
            out.writeInt(w.side());
            out.writeInt(w.quay());
        }
    }

    /** The works read back, or null when they do not add up. */
    static CityShore read(DataInputStream in) throws IOException {
        int n = in.readInt();
        if (n < 0) return null;
        CityShore s = new CityShore();
        for (int i = 0; i < n; i++) {
            long x0 = in.readLong(), y0 = in.readLong();
            int w = in.readInt(), h = in.readInt(), type = in.readInt(), side = in.readInt(), quay = in.readInt();
            if (w <= 0 || h <= 0 || type < 0 || side < -1 || side > 3 || quay < 0) return null;
            s.works.add(new Work(x0, y0, w, h, type, side, quay));
        }
        return s;
    }

    /** Whether two cities' works are the same, in the same order. */
    public boolean same(CityShore o) {
        return o != null && works.equals(o.works);
    }

    @Override public String toString() {
        return "CityShore" + Arrays.toString(works.toArray());
    }
}
