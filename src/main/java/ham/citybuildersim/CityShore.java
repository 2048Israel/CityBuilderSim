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
 * sea"; docs/studies/spec-oil.md 2.12: "terminals stand on owned coast with a
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
 * its cargo straight out, every plot owned salt water and no other work's,
 * and the water just past its end opening to the sea (SeaRoutes.opensToSea()).
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
 *
 * THE INDEX (SL1, 2026-10-28). blocks() is asked of every plot a search, a lay
 * or a plan reads (CityMap's RunGround.at()): a shoreSpot() reads a 96 x 96
 * window for each tile it looks at, and each plot asked every work on the
 * shore - 300 terminals took a canonical draw 183 s (14.6 s at 100, 1.2 s at
 * 10), a late city's 2,200 would take hours. So the works are kept by cell too:
 * a square of 8 plots a side (CELL_SHIFT) holds each work whose box with its
 * CLEAR verge, or whose quay, touches the cell, in the order laid, and blocks()
 * and at() ask only the plot's cell. The answers are exactly the loop's over
 * every work (MapCheck, 9: THE SHORE's reference loop, at every plot about
 * every work). The index is built when it is first asked, added to by add() and
 * dropped by a removal (a demolition, a month's few) to be built again when
 * next asked; its count is checked against the works' each time, which also
 * catches a list edited by hand. It is a cache of `works`, never saved: the
 * sidecar is as it was.
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

    /** How many works have been taken since this shore was made: a search that remembers where it found room (CityMap.shoreSpot()) starts again from the nearest when it moves, for a work taken may open room anywhere. */
    private long removals;

    /** Plots on the index's cell, as a shift: 8 - a General Cargo Terminal's box with its verge (5 x 6) touches four cells at most, the largest work's (a container terminal's 16 x 15) a dozen. */
    static final int CELL_SHIFT = 3;

    /** The most cells one work may be entered in; a work that spans more (a box or a quay no layout makes: a corrupt sidecar's) is kept apart and asked of every plot, as the loop asked it. */
    static final int CELLS_MOST = 64;

    /** The works by cell (SL1), or null when it is to be built again: a reader takes the index it finds and asks that one, so a rebuild beside it costs it a stale answer, never a failed one. */
    private Index index;

    /** The works, in the order laid. */
    public List<Work> works() { return java.util.Collections.unmodifiableList(works); }

    /** Whether there are none. */
    public boolean isEmpty() { return works.isEmpty(); }

    /** A number that moves with every work laid or taken. */
    public long version() { return version; }

    /** How many works have been taken, ever: moves with a removal only. */
    public long removals() { return removals; }

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
        // The index, when there is one and it was current, takes the work in; else it is built again when asked.
        Index ix = index;
        if (ix != null && ix.indexed == works.size() - 1) {
            ix.enter(w);
            ix.indexed++;
        }
    }

    /** The newest work of type t taken away; false when there is none. */
    boolean removeNewest(int t) {
        for (int i = works.size() - 1; i >= 0; i--) {
            if (works.get(i).type() != t) continue;
            works.remove(i);
            version++;
            index = null;
            removals++;
            return true;
        }
        return false;
    }

    /**
     * The newest work of type t that `which` accepts taken away; false when there is none (ET5, 2026-10-25: a city that
     * stands on more than one landmass takes a landmass's terminal from that landmass, CityMap's layShore()). With a
     * predicate that accepts every work it is removeNewest(t).
     */
    boolean removeNewest(int t, java.util.function.Predicate<Work> which) {
        for (int i = works.size() - 1; i >= 0; i--) {
            if (works.get(i).type() != t || !which.test(works.get(i))) continue;
            works.remove(i);
            version++;
            index = null;
            removals++;
            return true;
        }
        return false;
    }

    /** The work whose box holds world plot (x, y), or null: the first laid, from the plot's cell (SL1; the loop over every work, when some work is wide). */
    public Work at(long x, long y) {
        Index ix = index();
        if (ix.wide.length > 0) {
            for (Work w : works) if (w.holds(x, y)) return w;
            return null;
        }
        Cell c = ix.cellAt(x >> CELL_SHIFT, y >> CELL_SHIFT);
        if (c != null) for (Work w : c.works) if (w.holds(x, y)) return w;
        return null;
    }

    /** Whether world plot (x, y) is a work's box or within CLEAR of one, or a quay's: where the runs may not go and no other work may stand. Asks the works of the plot's cell only (SL1); the answer is the loop over every work's. */
    public boolean blocks(long x, long y) {
        Index ix = index();
        Cell c = ix.cellAt(x >> CELL_SHIFT, y >> CELL_SHIFT);
        if (c != null) for (Work w : c.works) if (blocksAt(w, x, y)) return true;
        for (Work w : ix.wide) if (blocksAt(w, x, y)) return true;
        return false;
    }

    /** Whether one work blocks world plot (x, y): its box with its verge, or its quay. */
    private static boolean blocksAt(Work w, long x, long y) {
        if (x >= w.x0() - CLEAR && y >= w.y0() - CLEAR && x < w.x0() + w.w() + CLEAR && y < w.y0() + w.h() + CLEAR) return true;
        return w.quayAt(x, y);
    }

    /* ----- the index (SL1) ----- */

    /** One cell of the index: its key and the works that touch it, in the order laid (replaced, never edited, so a reader holds a whole list). */
    private static final class Cell {
        final long key;
        Work[] works;

        Cell(long key, Work first) {
            this.key = key;
            this.works = new Work[] { first };
        }
    }

    /** The cells that hold any work, open addressing by the cell's key (a null slot is empty), and the works too wide for cells. */
    private static final class Index {
        Cell[] cells;
        int count;
        /** The works' count it was built to. */
        int indexed;
        /** The works that span more than CELLS_MOST cells, in the order laid: asked of every plot. */
        Work[] wide = new Work[0];

        Index(int works) {
            int cap = 16;
            while (cap < works * 8) cap <<= 1;
            cells = new Cell[cap];
        }

        private static int slot(long key) {
            return (int) ((key * 0x9E3779B97F4A7C15L) >>> 32);
        }

        /** The cell with key (cx, cy), or null when no work touches it. */
        Cell cellAt(long cx, long cy) {
            Cell[] t = cells;
            long key = (cy << 32) ^ (cx & 0xffffffffL);
            int mask = t.length - 1;
            for (int i = slot(key) & mask; t[i] != null; i = (i + 1) & mask) if (t[i].key == key) return t[i];
            return null;
        }

        /** Enters work w in every cell its box with its verge, or its quay, touches (after the works laid before it, so a cell's list is in the order laid). */
        void enter(Work w) {
            long cx0 = (w.x0() - CLEAR) >> CELL_SHIFT, cx1 = (w.x0() + w.w() + CLEAR - 1) >> CELL_SHIFT;
            long cy0 = (w.y0() - CLEAR) >> CELL_SHIFT, cy1 = (w.y0() + w.h() + CLEAR - 1) >> CELL_SHIFT;
            long qx0 = 1, qx1 = 0, qy0 = 1, qy1 = 0;   // empty: no quay
            if (w.quay() > 0) {
                long[] a = w.quayPlot(1), b = w.quayPlot(w.quay());
                qx0 = Math.min(a[0], b[0]) >> CELL_SHIFT;
                qx1 = Math.max(a[0], b[0]) >> CELL_SHIFT;
                qy0 = Math.min(a[1], b[1]) >> CELL_SHIFT;
                qy1 = Math.max(a[1], b[1]) >> CELL_SHIFT;
            }
            long span = (cx1 - cx0 + 1) * (cy1 - cy0 + 1) + Math.max(0, qx1 - qx0 + 1) * Math.max(0, qy1 - qy0 + 1);
            if (span > CELLS_MOST) {
                Work[] more = Arrays.copyOf(wide, wide.length + 1);
                more[wide.length] = w;
                wide = more;
                return;
            }
            for (long cy = cy0; cy <= cy1; cy++) for (long cx = cx0; cx <= cx1; cx++) put(cx, cy, w);
            for (long cy = qy0; cy <= qy1; cy++) {
                for (long cx = qx0; cx <= qx1; cx++) {
                    if (cx < cx0 || cx > cx1 || cy < cy0 || cy > cy1) put(cx, cy, w);
                }
            }
        }

        /** Appends w to the cell (cx, cy), the cell made if it is the first work in it. */
        private void put(long cx, long cy, Work w) {
            long key = (cy << 32) ^ (cx & 0xffffffffL);
            int mask = cells.length - 1;
            int i = slot(key) & mask;
            while (cells[i] != null) {
                if (cells[i].key == key) {
                    Work[] o = cells[i].works, n = Arrays.copyOf(o, o.length + 1);
                    n[o.length] = w;
                    cells[i].works = n;
                    return;
                }
                i = (i + 1) & mask;
            }
            cells[i] = new Cell(key, w);
            if (++count * 2 > cells.length) {
                // Twice the slots, filled before they are the table.
                Cell[] grown = new Cell[cells.length * 2];
                int m2 = grown.length - 1;
                for (Cell c : cells) {
                    if (c == null) continue;
                    int j = slot(c.key) & m2;
                    while (grown[j] != null) j = (j + 1) & m2;
                    grown[j] = c;
                }
                cells = grown;
            }
        }
    }

    /** The index as it stands: built to the works as they are when it is not current. */
    private Index index() {
        Index ix = index;
        if (ix != null && ix.indexed == works.size()) return ix;
        ix = new Index(works.size());
        for (Work w : works) ix.enter(w);
        ix.indexed = works.size();
        index = ix;
        return ix;
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
