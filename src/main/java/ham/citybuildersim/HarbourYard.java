package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The picture of one work on the shore at the near view (HB2, 2026-11-09): its
 * open cargo yard by what it handles, the stock on it at a level, and the quay
 * tracks and wagons of a terminal the railway reaches - ARGB pixels at PX a
 * plot, with no toolkit, which the map draws over the work's box from
 * MapFrame.DETAIL_FROM in.
 *
 * WHY: until HB2 a terminal was the tile's flat box at every zoom. HB1's
 * renders settled the near look (the open yard, its render (b)) and Jerus
 * picked HB2's three details from renders (docs/queue/HB2.md: 1a the yard
 * fills, 2a tracks on the quay, 3a a pixel-line crane). The yard is a picture
 * of its own and not tile paint because the tile draws from 3.2 px a plot and
 * his rule is detail from DETAIL_FROM (the 500 m bar), because a level that
 * steps with the month would dirty tiles every month, and because the look was
 * drawn at 8 px a plot where the tile is 4 below zoom 6. The reference look is
 * the render tool beside the pictures in docs/queue/HB2/.
 */
public final class HarbourYard {

    private HarbourYard() { }

    /** How many pixels a plot the picture is painted at: the near view's tile at its finest, and what the renders were drawn at. */
    public static final int PX = 8;

    /** How many steps of stock a yard shows above empty: level 0 is bare ground and its sheds, LEVELS a full yard. */
    public static final int LEVELS = 8;

    /** What a work on the shore handles, which is what its yard holds: boxes, heaps, tanks behind a tanker berth, crates and sheds, or a tank farm's tanks. */
    public enum Kind { CONTAINER, DRY_BULK, LIQUID, GENERAL, TANKS }

    /*
     * A picture's rail bits. LOW is the end of the box at the smaller world coordinate along the shore (west for a work whose sea side is north or
     * south, north for one whose sea side is east or west), HIGH the other end.
     */

    /** The quay track: one track along the box's whole sea-side row (and at a large berth a second, a loop off it). */
    public static final int RAIL = 1;
    /** The quay track runs on past the box's LOW end, to a neighbour on the same quay line; without it the track ends short of that end at a buffer stop. */
    public static final int OPEN_LOW = 2;
    /** ...and past its HIGH end. */
    public static final int OPEN_HIGH = 4;
    /** The lead: a track from the box's land edge up its LOW end's column to the quay track, where the railway's quay spur comes in from behind. */
    public static final int LEAD_LOW = 8;
    /** ...up its HIGH end's column. */
    public static final int LEAD_HIGH = 16;
    /** A train stands on the quay track. */
    public static final int TRAIN = 32;

    /** How many plots deep a box must be to have a second track, a loop off the quay track by a turnout (and, with TRAIN, a second train on it). */
    public static final int LOOP_PLOTS = 10;

    /** What painting one work's picture may take, in ms: the largest box (16 plots by 15) of the costliest kind, filled and with its train - a map redraw paints a few, once, and keeps them while the month stands. */
    public static final double PICTURE_MS = 0.5;

    /* ----- the look's colours: the game's own where it has one, the render's literals else ----- */

    /** The dark slate HB1's (b) yard was drawn on, which the ground is mixed from. */
    private static final int SLATE = 0xff2e3640;

    /** A yard's bare ground: the slate drawn a third of the way to the tile's YARD_GROUND. */
    private static final int GROUND = mix(SLATE, TileRaster.YARD_GROUND, 0.32);

    /** The 1 px line round the box. */
    private static final int EDGE = 0xff4a5866;

    /** The pad an empty container slot or crate stands on: the ground a shade lighter. */
    private static final int PAD = mix(GROUND, 0xffffffff, 0.07);

    /** The pad a heap grows from: the ground darkened. */
    private static final int HEAP_PAD = mix(GROUND, 0xff000000, 0.25);

    /** A tank's rim, and the hatch on a full tank's roof. */
    private static final int TANK_RIM = 0xff7f8ca6;

    /** An empty tank's roof, near black. */
    private static final int TANK_EMPTY = 0xff1d252d;

    /** A full tank's roof, which a floating roof rides high and light. */
    private static final int TANK_FULL = 0xffa6b6d2;

    /** The roof of a tank farm's tank, which holds no level the map shows. */
    private static final int TANK_BARE = mix(0xff2a3540, TANK_RIM, 0.4);

    /** A shed's roof: steel darkened toward the slate. */
    private static final int SHED_ROOF = mix(TileRaster.STEEL, SLATE, 0.55);

    /** A locomotive's body. */
    private static final int LOCO = 0xff2b2f36;

    /** A buffer stop's red. */
    private static final int STOP = 0xffa8442f;

    /** The colours of a crate: a red-brown timber, the ore colour and the steel darkened to the slate, and the timber toward the ore. */
    private static final int[] CRATES = { 0xff7b4b3a, mix(TileRaster.ORE_HEAP, SLATE, 0.35), mix(TileRaster.STEEL, SLATE, 0.5), mix(0xff7b4b3a, TileRaster.ORE_HEAP, 0.5) };

    /** A container's colours by its place in the yard: a box ship's own palette, ShipShapes.boxColour, of one call. */
    private static final int[] BOX = boxColours(60);

    /* ----- what a work is, and how full it shows ----- */

    /** What a work's box is drawn as: a terminal by the cargo of its berth, a tank farm as TANKS, null for a type that is neither (a work these pictures leave as it is). */
    public static Kind kindOf(BuildingVisual.Type t) {
        if (t == null) return null;
        Ports.Cargo[] cargos = Ports.Cargo.values();
        if (t.berth() >= 0) {
            if (t.berth() >= cargos.length) return null;
            switch (cargos[t.berth()]) {
                case CONTAINER: return Kind.CONTAINER;
                case DRY_BULK: return Kind.DRY_BULK;
                case LIQUID: return Kind.LIQUID;
                default: return Kind.GENERAL;
            }
        }
        return t.shore() ? Kind.TANKS : null;
    }

    /** The step of stock a yard shows for how busy its kind was, 0 to LEVELS: 0 for none, else at least 1 - any cargo at all shows - and at most LEVELS. */
    public static int step(double busy) {
        if (!(busy > 0)) return 0;
        return (int) Math.min((long) LEVELS, Math.max(1L, Math.round(busy * LEVELS)));
    }

    /* ----- the picture ----- */

    /**
     * A work's box painted as opaque ARGB pixels, w.w() * PX wide by w.h() * PX high, row-major, in the world's orientation (x east, y south,
     * pixel (0, 0) the box's north-west corner): its yard at `level` (0 to LEVELS, more stock for more) and, with RAIL in `rail`, its quay
     * track and what else the bits say. The layout is the work's own (seeded by its place and type) and does not move with the level, so a
     * higher level only adds stock; a tank farm shows no level and no rail. A null kind is bare ground.
     */
    public static int[] picture(CityShore.Work w, Kind kind, int level, int rail) {
        int side = sideOf(w.side());
        Canvas c = new Canvas(w.w(), w.h(), side);
        Random r = new Random(w.x0() * 7919L + w.y0() * 104_729L + w.type());
        c.rect(0, 0, c.L, c.D, EDGE);
        c.rect(1, 1, c.L - 1, c.D - 1, GROUND);
        if (kind == null) return c.px;
        double lv = Math.max(0, Math.min(LEVELS, level)) / (double) LEVELS;
        boolean tracks = kind != Kind.TANKS && (rail & RAIL) != 0, big = c.D >= LOOP_PLOTS * PX;
        boolean low0 = side < 2;                  // LOW is the end at u = 0 for a sea side of north or east, at u = L for the others
        boolean open0 = tracks && (rail & (low0 ? OPEN_LOW : OPEN_HIGH)) != 0, openL = tracks && (rail & (low0 ? OPEN_HIGH : OPEN_LOW)) != 0;
        boolean lead0 = tracks && (rail & (low0 ? LEAD_LOW : LEAD_HIGH)) != 0, leadL = tracks && (rail & (low0 ? LEAD_HIGH : LEAD_LOW)) != 0;
        Zone z = tracks ? new Zone(lead0 ? PX : 0, 0, c.L - (leadL ? PX : 0), c.D - (big ? 16 : 9), 3) : new Zone(0, 0, c.L, c.D, 10);
        switch (kind) {
            case CONTAINER: containers(c, z, lv, r); break;
            case DRY_BULK: heaps(c, z, lv, r); break;
            case LIQUID: tanks(c, z, lv, true); break;
            case TANKS: tanks(c, z, lv, false); break;
            default: crates(c, z, lv, r);
        }
        if (tracks) rails(c, kind, big, open0, openL, lead0, leadL, (rail & TRAIN) != 0);
        return c.px;
    }

    /**
     * One plot of the quay track between two neighbouring boxes of a quay line whose sea side is `side` (0 north to 3 west), PX by PX in the
     * world's orientation: the bed, sleepers and rails at the offset across the plot that picture()'s quay track has in its row, running
     * along the shore edge to edge, 0 (transparent) off the track.
     */
    public static int[] gapCell(int side) {
        Canvas c = new Canvas(1, 1, sideOf(side));
        int v = c.D - QUAY_FROM_SEA;
        bed(c, 0, c.L, v);
        steel(c, 0, c.L, 0, c.L, v);
        return c.px;
    }

    /**
     * One plot of the lead behind a box whose sea side is `side`, PX by PX in the world's orientation: the track across the shore edge to
     * edge, at the offset across the plot that the box's LEAD_LOW column's track has (`high`: its LEAD_HIGH column's), 0 off the track.
     */
    public static int[] leadCell(int side, boolean high) {
        int s = sideOf(side);
        Canvas c = new Canvas(1, 1, s);
        boolean atU0 = (s < 2) != high;
        lead(c, atU0, c.D);
        leadSteel(c, atU0, c.D, c.D);
        return c.px;
    }

    /* ----- the track's geometry, and small tools ----- */

    /** How far the quay track's middle lies from the sea side, in pixels: the bed is 5 across, so it stops 2 pixels short of the sea side. */
    private static final int QUAY_FROM_SEA = 5;

    /** How far a loop's middle lies from the sea side, in pixels. */
    private static final int LOOP_FROM_SEA = 12;

    /** How far short of the box's end a track that does not run on stops, in pixels, its buffer stop in the last of them. */
    private static final int STOP_SHORT = 3;

    /** A lead's middle, in pixels from the end of the box it stands at: the bed is 5 across, with 2 pixels of ground outside it. */
    private static final int LEAD_FROM_END = 5;

    private static int sideOf(int side) { return side >= 0 && side < CityShore.DX.length ? side : 0; }

    /** t of b over a, per channel, opaque. */
    private static int mix(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) Math.round((a & 255) * (1 - t) + (b & 255) * t);
        return 0xff000000 | r << 16 | g << 8 | bl;
    }

    private static double clamp(double x) { return Math.max(0, Math.min(1, x)); }

    private static int[] boxColours(int n) {
        BoatSchedule.Boat any = new BoatSchedule.Boat(new BoatSchedule.Call(Ports.Cargo.CONTAINER, Ports.Ship.FEEDER, true, 0, 0), 0, 0, true, true, 0, 1);
        int[] out = new int[n];
        for (int k = 0; k < n; k++) out[k] = ShipShapes.boxColour(any, k);
        return out;
    }

    /* ----- the canvas ----- */

    /** The part of a box its stock may stand on, in the box's own pixels, and the apron left clear at its sea end. */
    private record Zone(int u0, int v0, int u1, int v1, int apron) {
        int W() { return Math.max(0, u1 - u0); }
        int H() { return Math.max(0, v1 - v0); }
    }

    /**
     * A picture's pixels and a work's box in its own frame: u along the sea side (as the sea is faced, left to right), v from the land
     * side (0) out to the sea side (D), in pixels, so a yard is painted once for the four sides. Filled shapes take the pixels whose centre
     * they hold, an ellipse blends its edge.
     */
    private static final class Canvas {
        final int pw, ph, ax, ay, dx, dy, bx, by, L, D;
        final int[] px;

        Canvas(int plotsW, int plotsH, int side) {
            pw = plotsW * PX;
            ph = plotsH * PX;
            px = new int[pw * ph];
            dx = CityShore.DX[side];
            dy = CityShore.DY[side];
            ax = -dy;
            ay = dx;
            boolean ns = side % 2 == 0;
            L = ns ? pw : ph;
            D = ns ? ph : pw;
            bx = side >= 2 ? pw : 0;                       // the frame's origin: the land side's end that u runs from
            by = side == 0 || side == 3 ? ph : 0;
        }

        double x(double u, double v) { return bx + ax * u + dx * v; }

        double y(double u, double v) { return by + ay * u + dy * v; }

        /** A rectangle of the frame, filled. */
        void rect(double u0, double v0, double u1, double v1, int argb) {
            double xa = x(u0, v0), xb = x(u1, v1), ya = y(u0, v0), yb = y(u1, v1);
            int xs = Math.max(0, (int) Math.ceil(Math.min(xa, xb) - 0.5)), xe = Math.min(pw, (int) Math.ceil(Math.max(xa, xb) - 0.5));
            int ys = Math.max(0, (int) Math.ceil(Math.min(ya, yb) - 0.5)), ye = Math.min(ph, (int) Math.ceil(Math.max(ya, yb) - 0.5));
            for (int j = ys; j < ye; j++) {
                int at = j * pw;
                for (int i = xs; i < xe; i++) px[at + i] = argb;
            }
        }

        /** A convex quadrilateral (or any convex polygon) of the frame, filled. */
        void quad(double[] us, double[] vs, int argb) {
            int n = us.length;
            double[] xs = new double[n], ys = new double[n];
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                xs[i] = x(us[i], vs[i]);
                ys[i] = y(us[i], vs[i]);
                lo = Math.min(lo, ys[i]);
                hi = Math.max(hi, ys[i]);
            }
            int j1 = Math.min(ph, (int) Math.ceil(hi - 0.5));
            for (int j = Math.max(0, (int) Math.ceil(lo - 0.5)); j < j1; j++) {
                double yc = j + 0.5, left = Double.MAX_VALUE, right = -Double.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    int k = (i + 1) % n;
                    if ((ys[i] <= yc && yc < ys[k]) || (ys[k] <= yc && yc < ys[i])) {
                        double at = xs[i] + (yc - ys[i]) * (xs[k] - xs[i]) / (ys[k] - ys[i]);
                        left = Math.min(left, at);
                        right = Math.max(right, at);
                    }
                }
                if (left > right) continue;
                int i1 = Math.min(pw, (int) Math.ceil(right - 0.5));
                for (int i = Math.max(0, (int) Math.ceil(left - 0.5)); i < i1; i++) px[j * pw + i] = argb;
            }
        }

        /** An ellipse of the frame, its edge blended by how much of the pixel it holds. */
        void ellipse(double cu, double cv, double ru, double rv, int argb) {
            double ex = Math.abs(ax) * ru + Math.abs(dx) * rv, ey = Math.abs(ay) * ru + Math.abs(dy) * rv;
            if (ex <= 0 || ey <= 0) return;
            double cx = x(cu, cv), cy = y(cu, cv), soft = Math.min(ex, ey);
            int i0 = Math.max(0, (int) Math.floor(cx - ex - 1)), i1 = Math.min(pw - 1, (int) Math.ceil(cx + ex + 1));
            int j0 = Math.max(0, (int) Math.floor(cy - ey - 1)), j1 = Math.min(ph - 1, (int) Math.ceil(cy + ey + 1));
            for (int j = j0; j <= j1; j++) {
                double ny = (j + 0.5 - cy) / ey;
                for (int i = i0; i <= i1; i++) {
                    double nx = (i + 0.5 - cx) / ex;
                    double cover = (1 - Math.sqrt(nx * nx + ny * ny)) * soft + 0.5;     // the pixel's depth inside the edge, as a share of itself
                    if (cover <= 0) continue;
                    px[j * pw + i] = cover >= 1 ? argb : mix(px[j * pw + i], argb, cover);
                }
            }
        }
    }

    /* ----- the yards ----- */

    /** Rows of small coloured boxes in blocks with lanes between, an apron left by the quay; each slot has its turn to fill, from the land side toward the quay, the front ragged. */
    private static void containers(Canvas c, Zone z, double level, Random r) {
        int margin = 4, back = 5, boxU = 5, boxV = 3, rows = 3, lane = 4, blockV = rows * (boxV + 1) - 1;
        int k = r.nextInt(1000);
        int[] su = new int[(z.W() / (boxU + 1) + 2) * rows * (z.H() / (blockV + lane) + 2)], sv = new int[su.length];
        int n = 0;
        for (int v = z.v0() + back; v + blockV <= z.v1() - z.apron(); v += blockV + lane) {
            for (int row = 0; row < rows; row++) {
                int slot = 0;
                for (int u = z.u0() + margin; u + boxU <= z.u1() - margin; u += boxU + 1, slot++) {
                    if (slot % 7 == 6) continue;                                     // a lane across the rows
                    r.nextInt(14);                                                   // the render's stream, drawn so that the layout is the one picked
                    su[n] = u;
                    sv[n++] = v + row * (boxV + 1);
                }
            }
        }
        double[] turn = new double[n];
        for (int i = 0; i < n; i++) turn[i] = 0.72 * i / Math.max(1, n) + 0.28 * r.nextDouble();
        double[] sorted = turn.clone();
        Arrays.sort(sorted);
        int filled = level > 0 ? Math.max(1, (int) Math.round(level * n)) : 0;
        double cut = filled >= n ? 2 : sorted[filled];
        for (int i = 0; i < n; i++) c.rect(su[i], sv[i], su[i] + boxU, sv[i] + boxV, turn[i] < cut ? BOX[(k + i) % BOX.length] : PAD);
    }

    /** Two or three heaps, ochre or grey, each a darker ring and a lighter crown, on a pad: the heap has the area the level gives. */
    private static void heaps(Canvas c, Zone z, double level, Random r) {
        int W = z.W(), H = z.H(), n = W >= 12 * PX ? 3 : 2, flip = r.nextInt(2);
        for (int i = 0; i < n; i++) {
            double cu = z.u0() + W * (i + 0.5) / n + (r.nextDouble() - 0.5) * 4;
            double cv = z.v0() + H * 0.46 + (r.nextDouble() - 0.5) * Math.min(8, H * 0.12);
            double ru = Math.min(W / (2.0 * n) - 3, H * 0.3) * (0.85 + 0.15 * r.nextDouble()), rv = ru * 0.8;
            int col = (i + flip) % 2 == 0 ? TileRaster.ORE_HEAP : TileRaster.WORKED_GREY;
            double part = 0.8 + 0.4 * r.nextDouble();
            if (ru < 1) continue;
            c.ellipse(cu, cv, ru, rv, HEAP_PAD);
            if (level <= 0) continue;
            double s = Math.min(1, Math.max(Math.sqrt(clamp(level * part)), 1.5 / ru));      // never under a pixel and a half, so any cargo shows
            c.ellipse(cu, cv, ru * s, rv * s, mix(col, 0xff000000, 0.3));
            c.ellipse(cu - 1, cv - 1, ru * s * 0.82, rv * s * 0.82, col);
            if (s * ru >= 4) c.ellipse(cu - ru * s * 0.2, cv - rv * s * 0.2, ru * s * 0.35, rv * s * 0.35, mix(col, 0xffffffff, 0.25));
        }
    }

    /** Round tanks on a grid, each a rim, a roof and a hatch: `filling`, so many tanks full and the rest empty (a floating roof rides high and light on a full tank); else a tank farm's, no level. */
    private static void tanks(Canvas c, Zone z, double level, boolean filling) {
        int W = z.W(), H = z.H(), pitch = 3 * PX;
        int cols = Math.max(1, (W - 4) / pitch), rows = Math.max(1, (H - 4) / pitch), n = cols * rows;
        double rad = Math.min(pitch * 0.42, Math.min(W, H) / 2.0 - 2);
        if (rad < 1) return;
        double u0 = z.u0() + (W - cols * pitch) / 2.0 + pitch / 2.0, v0 = z.v0() + (H - rows * pitch) / 2.0 + pitch / 2.0;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                double cu = u0 + i * pitch, cv = v0 + j * pitch;
                double fill = filling ? clamp(level * n - (j * cols + i)) : -1;
                c.ellipse(cu, cv, rad, rad, TANK_RIM);
                c.ellipse(cu, cv, rad - 1, rad - 1, fill < 0 ? TANK_BARE : mix(TANK_EMPTY, TANK_FULL, 0.06 + 0.86 * fill));
                if (fill < 0 || fill > 0.5) c.ellipse(cu, cv, 2, 2, TANK_RIM);
            }
        }
    }

    /** One shed along the land side, stacked crates in groups between it and the apron: a group stacks two high where the room allows, and the crates fill in a shuffled order, one at a time. */
    private static void crates(Canvas c, Zone z, double level, Random r) {
        int margin = 4, crate = 4, apron = Math.min(8, z.apron());
        double shedV = Math.min(14, z.H() * 0.28);
        shedRoof(c, z.u0() + margin, z.v0() + 4, z.u1() - margin, z.v0() + 4 + shedV);
        double vTop = z.v0() + 4 + shedV + 4, vEnd = z.v1() - apron;
        int high = vEnd - vTop >= 2 * (crate + 1) ? 2 : 1;
        List<double[]> groups = new ArrayList<>();
        for (int u = z.u0() + margin + 2; u + 3 * (crate + 1) <= z.u1() - margin; u += 3 * (crate + 1) + 3 + r.nextInt(3)) {
            for (double v = vTop; v + high * (crate + 1) <= vEnd; v += high * (crate + 1) + 2) groups.add(new double[] { u, v, 2 + r.nextInt(2) });
        }
        List<Integer> order = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < groups.size(); i++) {
            order.add(i);
            total += (int) groups.get(i)[2] * high;
        }
        Collections.shuffle(order, r);
        int shown = level > 0 ? Math.max(1, (int) Math.round(level * total)) : 0, at = 0;
        for (int g : order) {
            double[] gr = groups.get(g);
            for (int i = 0; i < gr[2]; i++) {
                for (int j = 0; j < high; j++, at++) {
                    int col = CRATES[r.nextInt(CRATES.length)];
                    double u = gr[0] + i * (crate + 1), v = gr[1] + j * (crate + 1);
                    c.rect(u, v, u + crate, v + crate, at < shown ? col : PAD);
                }
            }
        }
    }

    /** A shed's roof: a framed rectangle with a lighter ridge down its middle. */
    private static void shedRoof(Canvas c, double u0, double v0, double u1, double v1) {
        c.rect(u0, v0, u1, v1, mix(SHED_ROOF, 0xff000000, 0.45));
        c.rect(u0 + 1, v0 + 1, u1 - 1, v1 - 1, SHED_ROOF);
        double mid = Math.floor((v0 + v1) / 2);
        c.rect(u0 + 2, mid, u1 - 2, mid + 1, mix(SHED_ROOF, 0xffffffff, 0.3));
    }

    /* ----- the rail ----- */

    /**
     * The quay track along the sea-most row, behind the wall: at a large box a second, a loop off it by a turnout. Each end of the quay track
     * runs to the box's edge where open, else stops short at a buffer; a lead comes up its end's column from the land edge and joins it at a
     * corner (at a T where the end is open too), and the loop keeps a plot clear of it. A train stands on them, a locomotive first.
     * Ends are in the frame's u: 0 and L.
     */
    private static void rails(Canvas c, Kind kind, boolean big, boolean open0, boolean openL, boolean lead0, boolean leadL, boolean train) {
        int L = c.L, v1 = c.D - QUAY_FROM_SEA, v2 = c.D - LOOP_FROM_SEA;
        int turn = 6 + (lead0 ? PX : 0), loop0 = 2 * PX + turn, loopEnd = L - STOP_SHORT - (leadL ? PX : 0);
        if (big) {
            diag(c, turn, v1, loop0, v2);
            bed(c, loop0, loopEnd, v2);
            steel(c, loop0, loopEnd, loop0, loopEnd, v2);
            stop(c, loopEnd, v2);
        }
        if (lead0) lead(c, true, v1 + 3);
        if (leadL) lead(c, false, v1 + 3);
        int l0 = LEAD_FROM_END - 1, lL = L - LEAD_FROM_END;                        // the leads' middles
        int from = open0 ? 0 : lead0 ? l0 - 2 : STOP_SHORT, to = openL ? L : leadL ? lL + 3 : L - STOP_SHORT;
        bed(c, from, to, v1);
        if (!open0 && !lead0) stop(c, STOP_SHORT - 1, v1);
        if (!openL && !leadL) stop(c, L - STOP_SHORT, v1);
        // a corner's two rails turn from the lead's, the outer to the sea side and the inner to the land side; a T's both end at the track
        int landFrom = lead0 && !open0 ? l0 + 1 : from, seaFrom = lead0 && !open0 ? l0 - 1 : from;
        int landTo = leadL && !openL ? lL : to, seaTo = leadL && !openL ? lL + 2 : to;
        steel(c, landFrom, landTo, seaFrom, seaTo, v1);
        if (lead0) leadSteel(c, true, open0 ? v1 : v1 + 2, v1);
        if (leadL) leadSteel(c, false, openL ? v1 : v1 + 2, v1);
        if (train) {
            int a = (big ? 3 * PX : 2) + (lead0 ? PX : 0), b = L - 5 - (leadL ? PX : 0);
            trainU(c, kind, a, b, v1, true);
            if (big) trainU(c, kind, L / 2, b, v2, false);
        }
    }

    /** A track along the quay from uA to uB, its middle at v: the bed and a sleeper every third pixel. */
    private static void bed(Canvas c, int uA, int uB, int v) {
        c.rect(uA, v - 2, uB, v + 3, TileRaster.BALLAST);
        for (int u = uA + 1; u + 1 <= uB; u += 3) c.rect(u, v - 2, u + 1, v + 3, TileRaster.SLEEPER);
    }

    /** ...and its two steel rails, the land side's from landA to landB and the sea side's from seaA to seaB. */
    private static void steel(Canvas c, int landA, int landB, int seaA, int seaB, int v) {
        c.rect(landA, v - 1, landB, v, TileRaster.STEEL);
        c.rect(seaA, v + 1, seaB, v + 2, TileRaster.STEEL);
    }

    /** A lead across the frame from the land side (v = 0) to vB, up the column at the u = 0 end or the u = L end: its bed and sleepers. */
    private static void lead(Canvas c, boolean atU0, int vB) {
        int u = atU0 ? LEAD_FROM_END - 1 : c.L - LEAD_FROM_END;
        c.rect(u - 2, 0, u + 3, vB, TileRaster.BALLAST);
        for (int v = 1; v + 1 <= vB; v += 3) c.rect(u - 2, v, u + 3, v + 1, TileRaster.SLEEPER);
    }

    /** A lead's two rails up to the quay track: the outer to outerTo, the inner to innerTo (the sea side's rail where the track turns, else the land side's). */
    private static void leadSteel(Canvas c, boolean atU0, int outerTo, int innerTo) {
        int u = atU0 ? LEAD_FROM_END - 1 : c.L - LEAD_FROM_END, outer = atU0 ? u - 1 : u + 1, inner = atU0 ? u + 1 : u - 1;
        c.rect(outer, 0, outer + 1, outerTo, TileRaster.STEEL);
        c.rect(inner, 0, inner + 1, innerTo, TileRaster.STEEL);
    }

    /** The turnout between two tracks along the quay. */
    private static void diag(Canvas c, double uA, double vA, double uB, double vB) {
        double[] us = { uA, uB, uB, uA };
        c.quad(us, new double[] { vA - 2, vB - 2, vB + 3, vA + 3 }, TileRaster.BALLAST);
        c.quad(us, new double[] { vA - 1, vB - 1, vB, vA }, TileRaster.STEEL);
        c.quad(us, new double[] { vA + 1, vB + 1, vB + 2, vA + 2 }, TileRaster.STEEL);
    }

    /** A buffer stop across a track along the quay, at u. */
    private static void stop(Canvas c, int u, int v) { c.rect(u, v - 2, u + 1, v + 3, STOP); }

    /** A train standing on a track along the quay: a locomotive (if asked) and a wagon every 4 px (TileRaster.WAGON_LENGTH, half a plot); a wagon is white for boxes, else the import's blue, with a mark by the cargo. */
    private static void trainU(Canvas c, Kind kind, int uA, int uB, int v, boolean loco) {
        int body = kind == Kind.CONTAINER ? ShipShapes.BOXES : ShipShapes.IMPORT;
        int mark = kind == Kind.DRY_BULK ? TileRaster.ORE_HEAP : kind == Kind.LIQUID ? mix(body, 0xffffffff, 0.4) : kind == Kind.GENERAL ? mix(body, 0xff000000, 0.3) : 0;
        int u = uA;
        if (loco) {
            c.rect(u, v - 1, u + 5, v + 2, LOCO);
            u += 6;
        }
        for (; u + 3 <= uB; u += 4) {
            c.rect(u, v - 1, u + 3, v + 2, body);
            if (mark != 0) c.rect(u + (kind == Kind.DRY_BULK ? 1 : 0), v, u + (kind == Kind.DRY_BULK ? 2 : 3), v + 1, mark);
        }
    }
}
