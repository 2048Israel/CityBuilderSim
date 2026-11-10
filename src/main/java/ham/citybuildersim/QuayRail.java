package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Which terminals on the harbour's quay the railway reaches (HB2, 2026-11-09), and the plots of track between their boxes: pure, from
 * the shore and the quay spurs the railway laid, with no toolkit.
 *
 * WHY: before HB2 no port had rail - the hub is the mine nearest the founding site and sidings go to mines and works - and the map
 * needed to know, from the shore and the spurs the railway laid, which terminals to draw tracks on. CityRuns lays one spur to each quay
 * line (CityShore.quayLines()) from behind one of its boxes, at one of the plots ends() lists; the line a spur ends at is railed, and
 * every terminal on it has its quay track (HarbourYard.RAIL), open to its neighbours on the line, with the lead from the spur to the
 * track at the box the spur stands behind.
 */
public final class QuayRail {

    /** How far behind a box's land edge a quay spur ends, in plots: one past the verge the shore keeps clear (CityShore.CLEAR), the nearest plot a run may take. */
    public static final int BEHIND = CityShore.CLEAR + 1;

    /** The kinds of a plot of track between the boxes (cells()): a gap between two neighbouring boxes of a railed line... */
    public static final int GAP = 0;
    /** ...a plot of the lead behind a box's LOW column, between the spur's end and the box's land edge... */
    public static final int LEAD_AT_LOW = 1;
    /** ...and behind its HIGH column. */
    public static final int LEAD_AT_HIGH = 2;

    /** The quay rail of a shore no spur reaches. */
    private static final QuayRail NONE = new QuayRail(Map.of(), List.of());

    private final Map<CityShore.Work, Integer> bits;
    private final List<long[]> cells;

    private QuayRail(Map<CityShore.Work, Integer> bits, List<long[]> cells) {
        this.bits = bits;
        this.cells = cells;
    }

    /** One plot a spur may end at: the line it is on, the work it stands behind, which end of the work's span along the shore it is, and the plot. */
    private record End(int line, CityShore.Work work, boolean high, long x, long y) { }

    /** The plot BEHIND work k's land edge, at its first (`high` false) or last column along the shore. */
    private static End endOf(int line, CityShore.Work k, boolean high) {
        long x, y;
        switch (k.side()) {
            case 0:  x = high ? k.x0() + k.w() - 1 : k.x0(); y = k.y0() + k.h() - 1 + BEHIND; break;
            case 1:  x = k.x0() - BEHIND; y = high ? k.y0() + k.h() - 1 : k.y0(); break;
            case 2:  x = high ? k.x0() + k.w() - 1 : k.x0(); y = k.y0() - BEHIND; break;
            default: x = k.x0() + k.w() - 1 + BEHIND; y = high ? k.y0() + k.h() - 1 : k.y0(); break;
        }
        return new End(line, k, high, x, y);
    }

    /** The ends of ends(), with the work and the column each is behind. */
    private static List<End> endsOf(CityShore shore) {
        List<End> out = new ArrayList<>();
        List<CityShore.QuayLine> lines = shore.quayLines();
        for (int i = 0; i < lines.size(); i++) {
            for (CityShore.Work k : lines.get(i).works()) {
                for (int h = 0; h < 2; h++) {
                    End e = endOf(i, k, h == 1);
                    if (!shore.blocks(e.x(), e.y())) out.add(e);
                }
            }
        }
        return out;
    }

    /**
     * The plots a quay spur may end at: for each line i of shore.quayLines() and each work on it, two, {i, x, y}: BEHIND plots behind the box's land
     * edge (the edge away from its sea side), at the box's first and its last column along the shore; one that shore.blocks() is left out. In the
     * order of lines, works, low then high.
     */
    public static List<long[]> ends(CityShore shore) {
        List<long[]> out = new ArrayList<>();
        for (End e : endsOf(shore)) out.add(new long[] { e.line(), e.x(), e.y() });
        return out;
    }

    /** The railed terminals, from the shore and the last plot {x, y} of each quay spur laid (CityRuns.quaySpurs()' end). */
    public static QuayRail of(CityShore shore, List<long[]> spurEnds) {
        if (spurEnds.isEmpty() || shore.isEmpty()) return NONE;
        List<End> all = endsOf(shore);
        List<End> leads = new ArrayList<>();
        java.util.Set<Integer> railed = new java.util.TreeSet<>();
        for (long[] s : spurEnds) {
            for (End e : all) {
                if (e.x() != s[0] || e.y() != s[1]) continue;
                railed.add(e.line());
                leads.add(e);
                break;
            }
        }
        if (railed.isEmpty()) return NONE;
        List<CityShore.QuayLine> lines = shore.quayLines();
        Map<CityShore.Work, Integer> bits = new HashMap<>();
        List<long[]> cells = new ArrayList<>();
        for (int i : railed) {
            List<CityShore.Work> on = lines.get(i).works();
            int side = lines.get(i).side();
            for (int m = 0; m < on.size(); m++) {
                int b = HarbourYard.RAIL;
                if (m > 0) b |= HarbourYard.OPEN_LOW;
                if (m < on.size() - 1) b |= HarbourYard.OPEN_HIGH;
                for (End e : leads) if (e.line() == i && e.work().equals(on.get(m))) b |= e.high() ? HarbourYard.LEAD_HIGH : HarbourYard.LEAD_LOW;
                bits.merge(on.get(m), b, (p, q) -> p | q);
            }
            for (int m = 0; m + 1 < on.size(); m++) gapCells(on.get(m), on.get(m + 1), side, cells);
            for (End e : leads) if (e.line() == i) leadCells(e, side, cells);
        }
        return new QuayRail(bits, List.copyOf(cells));
    }

    /** The plots between boxes a and b (b the higher along the shore) in the row of their sea-most plots, as GAP cells added to `out`. */
    private static void gapCells(CityShore.Work a, CityShore.Work b, int side, List<long[]> out) {
        if (side == 0 || side == 2) {
            long y = side == 0 ? a.y0() : a.y0() + a.h() - 1;
            for (long x = a.x0() + a.w(); x < b.x0(); x++) out.add(new long[] { x, y, GAP, side });
        } else {
            long x = side == 3 ? a.x0() : a.x0() + a.w() - 1;
            for (long y = a.y0() + a.h(); y < b.y0(); y++) out.add(new long[] { x, y, GAP, side });
        }
    }

    /** The CLEAR plots between a spur's end and the box's land edge, as LEAD cells added to `out`. */
    private static void leadCells(End e, int side, List<long[]> out) {
        int dx = side == 1 ? -1 : side == 3 ? 1 : 0, dy = side == 0 ? 1 : side == 2 ? -1 : 0;   // from the box's land edge away from the sea
        for (int k = 1; k <= CityShore.CLEAR; k++) out.add(new long[] { e.x() - (long) dx * k, e.y() - (long) dy * k, e.high() ? LEAD_AT_HIGH : LEAD_AT_LOW, side });
    }

    /** Whether no terminal is railed. */
    public boolean isEmpty() {
        return bits.isEmpty();
    }

    /** A work's rail bits (HarbourYard.RAIL, OPEN_LOW, OPEN_HIGH, LEAD_LOW, LEAD_HIGH; never TRAIN, which is the screen's), 0 for a work on no railed line. */
    public int bits(CityShore.Work w) {
        Integer b = bits.get(w);
        return b == null ? 0 : b;
    }

    /** The plots of track between the boxes, each {x, y, kind, side}: kind GAP, LEAD_AT_LOW or LEAD_AT_HIGH; side the line's sea side. */
    public List<long[]> cells() {
        return cells;
    }
}
