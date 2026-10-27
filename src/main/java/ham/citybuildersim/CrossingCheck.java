package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Short crossings of bridgeable water (ET6; spec-earth2-terrain 6.2, D12): the shallow sea is water a road or a railway
 * may bridge, exactly as fresh water is, and a highway or a railway reaches 34 plots (1,020 m) across it.
 *
 * WHAT THIS HAS TO PROVE (the sections below are numbered to match):
 *
 *   1. A HIGHWAY AND A RAILWAY SPAN THE SHALLOW SEA UP TO THEIR REACH. A strait of SHALLOW plots exactly as wide as the
 *      net bridges (TilePainter.MAX_BRIDGE[HIGHWAY], TilePainter.RAIL_BRIDGE), the city's dry ground beyond it: CityRuns
 *      clear() takes the whole crossing, along a row and along a diagonal; one plot wider it stops at the sea, and the
 *      same strait of fresh water is judged alike. A strait with no owned ground beyond it is not crossed (all or
 *      nothing: the stop is the city's edge). A highway net grown on a strait of its reach lays the plots on the water
 *      and the ground beyond; on one plot wider it lays nothing beyond; and the painter draws a run's plot on
 *      shallow water as a bridge (and a dry plot's not), a street's plot too, and the raster its deck.
 *
 *   2. THE SPAN IS BRIDGEABLE WATER ALONE. A span of fresh and shallow plots in any mix is one crossing under the same
 *      reach (and one plot wider stops); a single plot of deep sea (SALT) anywhere in the span - the first plot, the
 *      middle, the last, or the landing - stops it, the premise being the same span with the plot shallow. On a
 *      45-degree step a deep corner in the span stops the run and a shallow one does not; onto dry ground a shallow
 *      corner stops it, as it always did (a highway never shaves a shallow coast), and a fresh one does not.
 *
 *   3. A STREET AND AN ARTERIAL KEEP THEIR OWN REACH. Over a district plan whose ground is a band of shallow water across
 *      it: a street is laid across STREET_BRIDGE plots of it and not one plot more, the plots on the water bridges; an
 *      arterial crosses ARTERIAL_BRIDGE plots and not one more - ARTERIAL_BRIDGE is no longer a highway's - and a
 *      band of deep sea is crossed by neither. An avenue (the diagonal arterial) bridges shallow water as it does fresh,
 *      AVENUE_BRIDGE_STEPS and not one step more, and its counts follow its plots where a stray piece is taken up. The
 *      plan over shallow water is the plan over fresh water, plot for plot, and a tile of it is drawn as the fresh one
 *      is (the water's colour apart); the sea has no deck.
 *
 *   4. THE DIALS. A highway and a railway span 34 plots, an arterial 14, a street 6 - the decision (D12) pinned by name,
 *      the one place the numbers are typed.
 *
 * WHY THIS EXISTS. A run stopped at every sea (World.salt()), so a Halifax city that owned both shores of the Narrows
 * laid no highway across the Macdonald's 820 m or the MacKay's 805 m, and the 14-plot reach (420 m) refused them even
 * across fresh water. ET1 made the shallow sea a class of its own and left every rule that asks "is this the sea?"
 * alone; this is the rule that asks "can a bridge cross it?", and a fixture CAUSES each case: a strait of a given width
 * and kind, a plan with a band of water across it. The map alone is changed (the model reads nothing of it), so the
 * default playtest's trace cannot move.
 */
public class CrossingCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A strip of ground the runs read: the owned box (x0..x1 by y0..y1), grass by default, with bands of water across it and single plots set apart. */
    static final class Cells implements CityRuns.Ground {
        final long x0, y0, x1, y1;
        final List<long[]> bands = new ArrayList<>();
        final Map<Long, Integer> over = new HashMap<>();

        Cells(long x0, long y0, long x1, long y1) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }

        /** Columns xa..xb of every row of the box are class cls. */
        Cells band(long xa, long xb, int cls) {
            bands.add(new long[] { xa, xb, cls });
            return this;
        }

        /** The one plot (x, y) is class cls, whatever the bands say. */
        Cells set(long x, long y, int cls) {
            over.put(x * 100_003L + y, cls);
            return this;
        }

        @Override public int at(long x, long y, int dir) {
            if (x < x0 || x > x1 || y < y0 || y > y1) return -1;
            Integer o = over.get(x * 100_003L + y);
            if (o != null) return o;
            for (long[] b : bands) if (x >= b[0] && x <= b[1]) return (int) b[2];
            return World.GRASS;
        }

        @Override public long version() { return 1; }

        @Override public long[] box() { return new long[] { x0, y0, x1, y1 }; }
    }

    /** A strait running north to south across a box, `width` plots of class cls from column 1, grass on both banks (column 0 and below; column width + 1 and above), owned from column -30 to width + 60. */
    static Cells strait(int width, int cls) {
        return new Cells(-30, -10, width + 60, 200).band(1, width, cls);
    }

    /** What clear() makes of the way from (0, 0) along heading d, looking up to the strait's width and twenty plots more: {the steps it takes, what stops it}. */
    static int[] go(CityRuns.Net n, Cells g, int d) {
        int[] why = new int[1];
        long wet = 0;
        for (long[] b : g.bands) wet = Math.max(wet, b[1]);
        int look = (int) wet + 20;
        int s = CityRuns.clear(n, g, 0, 0, d, look, why);
        return new int[] { s, why[0], look };
    }

    /** Whether the way crosses the whole strait and goes on to the look's end, nothing stopping it. */
    static boolean crosses(CityRuns.Net n, Cells g, int d) {
        int[] r = go(n, g, d);
        return r[0] == r[2] && r[1] == CityRuns.OPEN;
    }

    /** Whether the way stops at the shore, before the first plot of water, the sea (or the city's edge) being what stops it. */
    static boolean stopsAtShore(CityRuns.Net n, Cells g, int d, int why) {
        int[] r = go(n, g, d);
        return r[0] == 0 && r[1] == why;
    }

    /** One tile of the painter's input: dry grass owned all over, the row y of it a run of kind `run` from the first column to the last, its columns xa to the end class cls. */
    static TilePainter.Input rowTile(int y, int xa, byte cls, byte run) {
        TilePainter.Input in = new TilePainter.Input();
        java.util.Arrays.fill(in.owned, true);
        int t = TilePainter.TILE;
        for (int x = 0; x < t; x++) {
            if (x >= xa) in.terrain[y * t + x] = cls;
            in.fixed[y * t + x] = run;
        }
        return in;
    }

    /** ...with a paved street along the row instead of a run, its code carrying no bridge bit. */
    static TilePainter.Input streetTile(int y, int xa, byte cls) {
        TilePainter.Input in = rowTile(y, xa, cls, (byte) 0);
        int code = DistrictPlan.PAVED | (1 << TilePainter.S_WIDTH_SHIFT);
        for (int x = 0; x < TilePainter.TILE; x++) in.street[y * TilePainter.TILE + x] = (byte) code;
        return in;
    }

    static TilePainter.Painted paint(TilePainter.Input in) {
        TilePainter.Painted p = new TilePainter.Painted();
        TilePainter.paint(in, p);
        return p;
    }

    /** How many plots of the tile's row y, columns xa to xb, are drawn as bridge plots. */
    static int bridged(TilePainter.Painted p, int y, int xa, int xb) {
        int n = 0;
        for (int x = xa; x <= xb; x++) if (p.bridge[y * TilePainter.TILE + x]) n++;
        return n;
    }

    /** How many pixels of the tile drawn at px a plot are the bridge deck's colour (TileRaster.DECK). */
    static int decks(TilePainter.Input in, int px) {
        TilePainter.Painted p = paint(in);
        int[] img = new int[TilePainter.TILE * px * TilePainter.TILE * px];
        TileRaster.raster(in, p, px, img);
        int n = 0;
        for (int c : img) if (c == TileRaster.DECK) n++;
        return n;
    }

    /** The plan's frame row (or column) at which a band of water starts across it (PlanCheck 10 (e)'s: south of the hub, with district to spare either side). */
    static final int TOP = 160;

    /** How many lines of the plan's frame are an arterial's: the multiples of CELL (a district's lines), the frame's first and last among them. */
    static final int ARTERIAL_LINES = (DistrictPlan.FRAME - 1) / DistrictPlan.CELL + 1;

    /** Whether frame plot (x, y) lies in the band of water, `width` rows (or, `columns`, columns) from TOP. */
    static boolean inBand(int x, int y, int width, boolean columns) {
        int k = columns ? x : y;
        return k >= TOP && k < TOP + width;
    }

    /** A plan and the input it was made from (its ground is what a tile is drawn on). */
    static final class Planned {
        final DistrictPlan.Input in;
        final DistrictPlan plan;

        Planned(DistrictPlan.Input in, DistrictPlan plan) {
            this.in = in;
            this.plan = plan;
        }
    }

    /** A flat district at his fill (PlanCheck's) with a band of class cls `width` rows (or columns) wide across the whole frame, and its plan. */
    static DistrictPlan planOver(BuildingVisual.Type[] types, int width, byte cls, boolean columns) {
        return planOver(types, width, cls, cls, columns);
    }

    /** ...the band's first half of class cls and the rest of class rest (a span of two kinds of water). */
    static DistrictPlan planOver(BuildingVisual.Type[] types, int width, byte cls, byte rest, boolean columns) {
        DistrictPlan.Input in = PlanCheck.flatMix(types, PlanCheck.FULL_FILL, true, false);
        int n = DistrictPlan.FRAME;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (inBand(x, y, width, columns)) in.terrain[y * n + x] = (columns ? x : y) - TOP < width / 2 ? cls : rest;
            }
        }
        return DistrictPlan.make(in);
    }

    /** A flat district at his fill with rows of class cls where `wet` says so (by the row's distance from `top`), and its plan. */
    static Planned planRows(BuildingVisual.Type[] types, int top, java.util.function.IntPredicate wet, byte cls) {
        DistrictPlan.Input in = PlanCheck.flatMix(types, PlanCheck.FULL_FILL, true, false);
        int n = DistrictPlan.FRAME;
        for (int y = 0; y < n; y++) {
            if (!wet.test(y - top)) continue;
            for (int x = 0; x < n; x++) in.terrain[y * n + x] = cls;
        }
        return new Planned(in, DistrictPlan.make(in));
    }

    /** The tile (tx, ty) of a plan's frame as the map hands it to the painter: the plan's ground, its streets (and the ring of them about the tile) and its avenue marks, the origin the avenue's arms run out from; the runs and the buildings left out. */
    static TilePainter.Input tileOf(Planned pl, int tx, int ty) {
        TilePainter.Input in = new TilePainter.Input();
        java.util.Arrays.fill(in.owned, true);
        int t = TilePainter.TILE, f = DistrictPlan.FRAME, r = TilePainter.REACH;
        for (int y = -r; y < t + r; y++) {
            for (int x = -r; x < t + r; x++) {
                int fx = tx * t + x, fy = ty * t + y;
                if (fx < 0 || fy < 0 || fx >= f || fy >= f) continue;
                int q = fy * f + fx;
                byte code = CityMap.paintCode(pl.plan.street[q]);
                in.ring[(y + r) * TilePainter.RING + x + r] = code;
                if (pl.plan.avenue != null && pl.plan.avenue[q] != 0) in.setAvenue(x, y, pl.plan.avenue[q] & 0xff);
                if (x >= 0 && y >= 0 && x < t && y < t) {
                    in.street[y * t + x] = code;
                    in.terrain[y * t + x] = pl.in.terrain[q];
                }
            }
        }
        // The avenue's origin: the lattice node nearest the hub (PlanCheck.flatMix puts the hub at the frame's middle), in the tile's plots.
        int l = DistrictPlan.LATTICE, node = (int) Math.floorDiv((long) (f / 2 + l / 2), (long) l) * l;
        in.avenueOx = node - tx * t;
        in.avenueOy = node - ty * t;
        return in;
    }

    /** The tile drawn at px pixels a plot. */
    static int[] imageOf(TilePainter.Input in, int px) {
        TilePainter.Painted p = paint(in);
        int[] img = new int[TilePainter.TILE * px * TilePainter.TILE * px];
        TileRaster.raster(in, p, px, img);
        return img;
    }

    /** How many pixels of two tiles differ once the shallow sea's colour in the first is read as fresh water's. */
    static int differ(int[] shallow, int[] fresh) {
        int cs = TileRaster.colour(World.SHALLOW, World.FLAT), cf = TileRaster.colour(World.FRESH, World.FLAT);
        int n = 0;
        for (int i = 0; i < shallow.length; i++) if ((shallow[i] == cs ? cf : shallow[i]) != fresh[i]) n++;
        return n;
    }

    /** How many pixels of an image are the bridge deck's colour. */
    static int deckPixels(int[] img) {
        int n = 0;
        for (int c : img) if (c == TileRaster.DECK) n++;
        return n;
    }

    /**
     * What a plan lays on the band: {street plots, of them bridges, of them on an arterial's line (a multiple of CELL across
     * the band), on another line and not an avenue's, on an avenue}. An avenue is the diagonal arterial's staircase
     * (DistrictPlan.avenue marks it); it bridges by its own steps (AVENUE_BRIDGE_STEPS).
     */
    static int[] onWater(DistrictPlan p, int width, boolean columns) {
        int n = DistrictPlan.FRAME;
        int[] r = new int[5];
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (!inBand(x, y, width, columns)) continue;
                int q = y * n + x;
                if (p.kindAt(q) == DistrictPlan.NONE) continue;
                r[0]++;
                if ((p.street[q] & DistrictPlan.BRIDGE) != 0) r[1]++;
                if (p.avenue != null && p.avenue[q] != 0) r[4]++;
                else if ((columns ? y : x) % DistrictPlan.CELL == 0) r[2]++;
                else r[3]++;
            }
        }
        return r;
    }

    public static void main(String[] args) {
        CityRuns runs = new CityRuns();
        CityRuns.Net hw = runs.highways, rail = runs.rail;
        int[] caps = { TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY], TilePainter.RAIL_BRIDGE };
        CityRuns.Net[] nets = { hw, rail };
        String[] names = { "highway", "railway" };

        /* ============ 1. a highway and a railway span the shallow sea up to their reach ============ */
        System.out.println("--- 1 a highway and a railway span the shallow sea up to their reach ---");
        check("the highways bridge MAX_BRIDGE[HIGHWAY] plots and the railway RAIL_BRIDGE (the nets are made with the dials)", hw.bridge == caps[0] && rail.bridge == caps[1]);
        for (int k = 0; k < 2; k++) {
            CityRuns.Net n = nets[k];
            int cap = caps[k];
            for (int d = 0; d <= 1; d++) {
                String way = d == 0 ? "along a row" : "on the diagonal";
                check("a " + names[k] + " crosses a strait of " + cap + " shallow plots (its reach) " + way + ", the dry ground beyond it, nothing stopping it",
                        crosses(n, strait(cap, World.SHALLOW), d));
                check("...and stops at the sea one plot wider (" + (cap + 1) + ") " + way, stopsAtShore(n, strait(cap + 1, World.SHALLOW), d, CityRuns.SEA));
                check("...fresh water is judged alike: " + cap + " plots crossed, " + (cap + 1) + " not " + way,
                        crosses(n, strait(cap, World.FRESH), d) && stopsAtShore(n, strait(cap + 1, World.FRESH), d, CityRuns.SEA));
            }
            Cells noLanding = new Cells(-30, -10, cap, 200).band(1, cap, World.SHALLOW);
            check("...a strait of its reach with no owned ground beyond is not crossed: all or nothing, stopped at the city's edge (" + names[k] + ")",
                    stopsAtShore(n, noLanding, 0, CityRuns.EDGE));
        }
        check("a strait of one plot is crossed (the shortest bridge, ground beyond it)", crosses(hw, strait(1, World.SHALLOW), 0));

        // A highway net grown on a strait: dry square round the hub, the strait east of it, ground beyond.
        long site = IslandNetCheck.SITE;
        long shore = site + 100;
        for (int w = hw.bridge; w <= hw.bridge + 1; w++) {
            boolean wide = w > hw.bridge;
            Cells land = new Cells(site - 200, site - 200, shore + w + 300, site + 200).band(shore + 1, shore + w, World.SHALLOW);
            CityRuns net = new CityRuns();
            net.setGateway(CityRuns.NO_GATEWAY);
            net.layTo(2000, 0, new ArrayList<>(), 0, land, site, site, site, site, new ArrayList<>(), new ArrayList<>(), CityRuns.ONE_PIECE);
            long onWater = 0, beyond = 0, onSea = 0;
            long wx = -1, wy = -1;
            for (long y = site - 200; y <= site + 200; y++) {
                for (long x = shore + 1; x <= shore + w + 300; x++) {
                    if (!CityRuns.covered(net.highways, x, y)) continue;
                    if (x <= shore + w) { onWater++; if (wx < 0) { wx = x; wy = y; } } else beyond++;
                }
            }
            System.out.printf("  a strait %d plots wide (reach %d): the net lays %,d plots (%,d short), %,d on the water, %,d beyond it%n", w, hw.bridge, net.highways.plots, net.highwayShort(), onWater, beyond);
            if (!wide) {
                check("a highway net grown on a strait of its reach (" + w + " shallow plots) lays plots across the water and beyond it (" + onWater + " on the water, " + beyond + " beyond)", onWater > 0 && beyond > 0);
                // The painter: the tile holding a plot of the run on the water.
                TilePainter.Input in = new TilePainter.Input();
                java.util.Arrays.fill(in.owned, true);
                long tx = Math.floorDiv(wx, (long) TilePainter.TILE), ty = Math.floorDiv(wy, (long) TilePainter.TILE);
                for (int y = 0; y < TilePainter.TILE; y++) {
                    for (int x = 0; x < TilePainter.TILE; x++) {
                        long gx = tx * TilePainter.TILE + x, gy = ty * TilePainter.TILE + y;
                        in.terrain[y * TilePainter.TILE + x] = (byte) Math.max(0, land.at(gx, gy, -1));
                        if (CityRuns.covered(net.highways, gx, gy)) in.fixed[y * TilePainter.TILE + x] = (byte) BuildingVisual.HIGHWAY;
                    }
                }
                TilePainter.Painted p = paint(in);
                int wet = 0, wetBridge = 0, dry = 0, dryBridge = 0;
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    if (in.fixed[i] == 0) continue;
                    if (in.terrain[i] == World.SHALLOW) { wet++; if (p.bridge[i]) wetBridge++; } else { dry++; if (p.bridge[i]) dryBridge++; }
                }
                check("...and the painter draws each of the run's plots on the water as a bridge (" + wetBridge + " of " + wet + ") and none on dry ground (" + dryBridge + " of " + dry + ")",
                        wet > 0 && wetBridge == wet && dryBridge == 0);
            } else {
                check("a highway net grown on a strait one plot wider than its reach (" + w + " shallow plots) lays no plot on the water or beyond it (" + onWater + " and " + beyond + ")", onWater == 0 && beyond == 0);
            }
        }
        {
            // The painter reads a run's plot on shallow water as a bridge for a highway and for the track alike, a street's plot too; dry ground is none.
            int y = 6, xa = 8;
            TilePainter.Painted ph = paint(rowTile(y, xa, World.SHALLOW, (byte) BuildingVisual.HIGHWAY));
            TilePainter.Painted pr = paint(rowTile(y, xa, World.SHALLOW, TilePainter.RAIL));
            TilePainter.Painted pd = paint(rowTile(y, xa, World.GRASS, (byte) BuildingVisual.HIGHWAY));
            int last = TilePainter.TILE - 1;
            check("the painter draws a highway's plots over shallow water as bridges (" + bridged(ph, y, xa, last) + " of " + (last - xa + 1) + ") and its plots on the bank not (" + bridged(ph, y, 0, xa - 1) + ")",
                    bridged(ph, y, xa, last) == last - xa + 1 && bridged(ph, y, 0, xa - 1) == 0);
            check("...a railway's track alike (" + bridged(pr, y, xa, last) + " of " + (last - xa + 1) + ")", bridged(pr, y, xa, last) == last - xa + 1 && bridged(pr, y, 0, xa - 1) == 0);
            check("...and no plot of a highway on dry ground is a bridge (" + bridged(pd, y, 0, last) + ")", bridged(pd, y, 0, last) == 0);
            // The raster: the deck is drawn along the same plots (TileRaster.DECK), and the shallow sea's deck is the fresh water's.
            int px = 32;   // the deck shows from about 20 px a plot: below it a highway fills its plot
            int decksShallow = decks(rowTile(y, xa, World.SHALLOW, (byte) BuildingVisual.HIGHWAY), px);
            int decksFresh = decks(rowTile(y, xa, World.FRESH, (byte) BuildingVisual.HIGHWAY), px);
            int decksDry = decks(rowTile(y, xa, World.GRASS, (byte) BuildingVisual.HIGHWAY), px);
            int decksRail = decks(rowTile(y, xa, World.SHALLOW, TilePainter.RAIL), px);
            check("the raster draws the deck under a highway over shallow water as it does over fresh (" + decksShallow + " pixels against " + decksFresh + "), and none on dry ground (" + decksDry + ")",
                    decksShallow > 0 && decksShallow == decksFresh && decksDry == 0);
            check("...and under a railway's track over shallow water (" + decksRail + " pixels)", decksRail > 0);
            TilePainter.Painted ps = paint(streetTile(y, xa, World.SHALLOW)), pf = paint(streetTile(y, xa, World.FRESH)), pg = paint(streetTile(y, xa, World.GRASS));
            check("a street's plots over shallow water are bridges though their codes carry no bridge bit, as over fresh water (" + bridged(ps, y, xa, last) + " and " + bridged(pf, y, xa, last) + " of " + (last - xa + 1) + "), on the bank and on grass none ("
                    + bridged(ps, y, 0, xa - 1) + ", " + bridged(pg, y, 0, last) + ")",
                    bridged(ps, y, xa, last) == last - xa + 1 && bridged(pf, y, xa, last) == last - xa + 1 && bridged(ps, y, 0, xa - 1) == 0 && bridged(pg, y, 0, last) == 0);
        }

        /* ============ 2. the span is bridgeable water alone ============ */
        System.out.println("\n--- 2 the span is bridgeable water alone: fresh and shallow in any mix, never the deep sea ---");
        {
            int cap = hw.bridge;
            Cells mixed = new Cells(-30, -10, cap + 60, 200);
            mixed.band(1, cap / 2, World.SHALLOW).band(cap / 2 + 1, cap, World.FRESH);
            check("a span of " + cap / 2 + " shallow plots and then " + (cap - cap / 2) + " fresh ones (" + cap + " in all) is one crossing", crosses(hw, mixed, 0));
            Cells alt = new Cells(-30, -10, cap + 60, 200).band(1, cap, World.SHALLOW);
            for (int x = 2; x <= cap; x += 2) alt.set(x, 0, World.FRESH);
            check("...so is one of fresh and shallow plots alternating (" + cap + " in all)", crosses(hw, alt, 0));
            Cells over = new Cells(-30, -10, cap + 60, 200);
            over.band(1, cap / 2, World.SHALLOW).band(cap / 2 + 1, cap + 1, World.FRESH);
            check("...and the same mix one plot wider (" + (cap + 1) + ") stops at the sea", stopsAtShore(hw, over, 0, CityRuns.SEA));
            Cells railMix = new Cells(-30, -10, cap + 60, 200);
            railMix.band(1, rail.bridge / 2, World.FRESH).band(rail.bridge / 2 + 1, rail.bridge, World.SHALLOW);
            check("the railway crosses a mix of its own reach (" + rail.bridge + " plots)", crosses(rail, railMix, 0));

            // One plot of the deep sea, in a span of the net's reach (the premise: the same span, all shallow, is crossed).
            int[] where = { 1, cap / 2, cap };
            String[] at = { "the first plot", "the middle", "the last plot of the water" };
            for (int i = 0; i < where.length; i++) {
                Cells deep = strait(cap, World.SHALLOW).set(where[i], 0, World.SALT);
                check("one plot of deep sea at " + at[i] + " (column " + where[i] + ") of a span of " + cap + " stops the run (premise: the same span all shallow is crossed)",
                        crosses(hw, strait(cap, World.SHALLOW), 0) && stopsAtShore(hw, deep, 0, CityRuns.SEA));
            }
            Cells landing = new Cells(-30, -10, cap + 60, 200).band(1, cap, World.SHALLOW).band(cap + 1, cap + 1, World.SALT);
            check("deep sea at the landing (the plot after the span) stops it", stopsAtShore(hw, landing, 0, CityRuns.SEA));
            Cells seaAtShore = new Cells(-30, -10, 60, 200).band(1, 3, World.SALT);
            check("deep sea next to the shore, a strait of it, stops the run as it always did", stopsAtShore(hw, seaAtShore, 0, CityRuns.SEA));
            Cells railDeep = new Cells(-30, -10, rail.bridge + 60, 200).band(1, rail.bridge, World.SHALLOW).set(rail.bridge / 2, 0, World.SALT);
            check("...and the railway's span with one deep plot in it stops alike", stopsAtShore(rail, railDeep, 0, CityRuns.SEA));
        }
        {
            // 45 degrees (heading 1: dx = +1, dy = +1). Step m of a diagonal from (0, 0) stands on (m, m) and its corner on (m, m - 1).
            Cells dry = new Cells(-30, -10, 80, 120);
            check("premise: a diagonal over dry grass is clear to its end", crosses(hw, dry, 1));
            check("onto dry ground a shallow corner stops the run, as it always did: a highway never shaves a shallow coast",
                    stopsAtShore(hw, new Cells(-30, -10, 80, 120).set(1, 0, World.SHALLOW), 1, CityRuns.SEA));
            check("...a deep corner stops it too", stopsAtShore(hw, new Cells(-30, -10, 80, 120).set(1, 0, World.SALT), 1, CityRuns.SEA));
            check("...a fresh-water corner does not (today's rule)", crosses(hw, new Cells(-30, -10, 80, 120).set(1, 0, World.FRESH), 1));
            int w = 5;
            Cells band = new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW);
            check("premise: a band of " + w + " shallow plots on the diagonal is crossed", crosses(hw, band, 1));
            check("a deep corner in the span (the plot beside step 3) stops the run", stopsAtShore(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(3, 2, World.SALT), 1, CityRuns.SEA));
            check("...a shallow corner among fresh plots does not: the span may be water of either kind",
                    crosses(hw, new Cells(-30, -10, 80, 120).band(1, w, World.FRESH).set(3, 2, World.SHALLOW), 1));
            check("...and a fresh one among shallow plots does not", crosses(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(3, 2, World.FRESH), 1));
            check("a shallow corner beside the landing step (the plot (" + (w + 1) + ", " + w + ")) stops it: the landing is a step onto dry ground",
                    stopsAtShore(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(w + 1, w, World.SHALLOW), 1, CityRuns.SEA));
            check("...a fresh one does not", crosses(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(w + 1, w, World.FRESH), 1));
            check("a deep corner at the first step of the span (the plot beside step 1) stops the run", stopsAtShore(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(1, 0, World.SALT), 1, CityRuns.SEA));
            check("...a shallow one there does not (the step is on the water)", crosses(hw, new Cells(-30, -10, 80, 120).band(1, w, World.SHALLOW).set(1, 0, World.SHALLOW), 1));
        }

        /* ============ 3. a street and an arterial keep their own reach ============ */
        System.out.println("\n--- 3 a street and an arterial keep their own reach over shallow water ---");
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        for (int o = 0; o < 2; o++) {
            boolean columns = o == 1;
            String band = columns ? "a band of columns" : "a band of rows";
            int street = DistrictPlan.STREET_BRIDGE, arterial = DistrictPlan.ARTERIAL_BRIDGE, steps = DistrictPlan.AVENUE_BRIDGE_STEPS;
            DistrictPlan pS = planOver(types, street, World.SHALLOW, columns), pS1 = planOver(types, street + 1, World.SHALLOW, columns);
            DistrictPlan pA = planOver(types, arterial, World.SHALLOW, columns), pA1 = planOver(types, arterial + 1, World.SHALLOW, columns);
            int[] rS = onWater(pS, street, columns), rS1 = onWater(pS1, street + 1, columns), rA = onWater(pA, arterial, columns), rA1 = onWater(pA1, arterial + 1, columns);
            System.out.printf("  %s of shallow water: %d wide %d street plots (%d off the arterials), %d wide %d (%d), %d wide %d, %d wide %d%n",
                    band, street, rS[0], rS[3], street + 1, rS1[0], rS1[3], arterial, rA[0], arterial + 1, rA1[0]);
            check("3 " + band + " " + street + " plots (STREET_BRIDGE) wide of shallow water: streets cross it, off the arterials too (" + rS[3] + " plots), every plot on the water a bridge (" + rS[1] + " of " + rS[0] + ")",
                    rS[3] > 0 && rS[2] > 0 && rS[1] == rS[0]);
            check("3 ...one plot wider (" + (street + 1) + "): no street but the arterials crosses (" + rS1[3] + " other plots, " + rS1[2] + " on the arterials' lines), all bridges",
                    rS1[3] == 0 && rS1[2] > 0 && rS1[1] == rS1[0]);
            check("3 ...ARTERIAL_BRIDGE (" + arterial + ") wide: every arterial's line is laid across it whole (" + rA[2] + " of " + arterial * ARTERIAL_LINES + " plots) and nothing else is, all bridges",
                    rA[2] == arterial * ARTERIAL_LINES && rA[3] == 0 && rA[1] == rA[0]);
            check("3 ...one plot wider (" + (arterial + 1) + "): nothing is laid on the water (" + rA1[0] + " street plots)", rA1[0] == 0);
            boolean same = true;
            for (int w : new int[] { street, street + 1, steps, steps + 1, arterial, arterial + 1 }) {
                DistrictPlan sh = planOver(types, w, World.SHALLOW, columns), fr = planOver(types, w, World.FRESH, columns);
                same &= java.util.Arrays.equals(sh.street, fr.street) && java.util.Arrays.equals(sh.avenue, fr.avenue);
            }
            check("3 ...the plan over a band of shallow water is the plan over fresh water, plot for plot, at every width tried (streets and avenue)", same);
            DistrictPlan mix = planOver(types, arterial, World.SHALLOW, World.FRESH, columns), mix1 = planOver(types, arterial + 1, World.FRESH, World.SHALLOW, columns);
            int[] rM = onWater(mix, arterial, columns), rM1 = onWater(mix1, arterial + 1, columns);
            check("3 ...a band of " + arterial + " plots, shallow and then fresh, is one crossing: every arterial's line laid across it (" + rM[2] + " of " + arterial * ARTERIAL_LINES + " plots)", rM[2] == arterial * ARTERIAL_LINES && rM[1] == rM[0]);
            check("3 ...and one of " + (arterial + 1) + ", fresh and then shallow, is one span too wide for any: nothing is laid on it (" + rM1[0] + " street plots)", rM1[0] == 0);
            for (int w : new int[] { street, steps, arterial }) {
                DistrictPlan sea = planOver(types, w, World.SALT, columns);
                int[] r = onWater(sea, w, columns);
                check("3 ...a band of deep sea " + w + " wide is crossed by no street and no avenue (" + r[0] + " plots)", r[0] == 0 && sea.avenueBridged == 0);
            }
            DistrictPlan aV = planOver(types, steps, World.SHALLOW, columns), aV1 = planOver(types, steps + 1, World.SHALLOW, columns);
            int[] rV = onWater(aV, steps, columns), rV1 = onWater(aV1, steps + 1, columns);
            check("3 ...an avenue bridges " + steps + " steps (AVENUE_BRIDGE_STEPS) of shallow water: " + aV.avenueBridged + " of its plots bridges, " + aV.avenueWater + " counted too wide (" + rV[4] + " on the water)",
                    aV.avenueBridged > 0 && aV.avenueWater == 0 && rV[4] == aV.avenueBridged);
            check("3 ...and not one step more (" + (steps + 1) + "): none laid on the water (" + rV1[4] + "), " + aV1.avenueWater + " counted too wide", rV1[4] == 0 && aV1.avenueBridged == 0 && aV1.avenueWater > 0);
        }

        // The avenue's counters follow its plots on the water where a stray piece of it is taken up: AVENUE_BRIDGE_STEPS + 1 rows of
        // shallow water either side of a one-plot strip with a one-plot bridge in it - the avenue's pieces there touch no street.
        {
            int wide = DistrictPlan.AVENUE_BRIDGE_STEPS + 1;
            Planned pl = planRows(types, TOP, k -> k >= 0 && (k < wide || k == wide + 1 || k >= wide + 3 && k < wide + 13), World.SHALLOW);
            int laidWet = 0, n = DistrictPlan.FRAME;
            for (int q = 0; q < n * n; q++) if (pl.plan.avenue != null && pl.plan.avenue[q] != 0 && pl.plan.kindAt(q) != DistrictPlan.NONE && World.bridgeable(pl.in.terrain[q])) laidWet++;
            check("3 ...the avenue's bridged count is the avenue's plots on the water (" + laidWet + " laid, " + pl.plan.avenueBridged + " counted) where its stray pieces over a one-plot bridge between waters too wide are taken up",
                    laidWet == pl.plan.avenueBridged && pl.plan.avenueWater > 0);
        }

        // The raster draws a plan's tile over shallow water as it draws it over fresh water: the same pixels but the ground's colour. Two bands of
        // AVENUE_BRIDGE_STEPS rows: one the avenue's arms cross, one across the avenue's origin; and the sea draws no deck.
        {
            int steps = DistrictPlan.AVENUE_BRIDGE_STEPS, px = 16;
            int[][] tiles = { { 2, 5 }, { 3, 5 }, { 5, 5 }, { 3, 3 }, { 4, 3 }, { 5, 3 }, { 3, 4 }, { 4, 4 }, { 5, 4 } };
            int[] tops = { TOP, 124 };
            for (int top : tops) {
                Planned sh = planRows(types, top, k -> k >= 0 && k < steps, World.SHALLOW), fr = planRows(types, top, k -> k >= 0 && k < steps, World.FRESH);
                Planned sea = planRows(types, top, k -> k >= 0 && k < steps, World.SALT);
                int differing = 0, decksShallow = 0, decksSea = 0, inspected = 0;
                for (int[] t : tiles) {
                    int[] a = imageOf(tileOf(sh, t[0], t[1]), px), b = imageOf(tileOf(fr, t[0], t[1]), px), c = imageOf(tileOf(sea, t[0], t[1]), px);
                    differing += differ(a, b);
                    decksShallow += deckPixels(a);
                    decksSea += deckPixels(c);
                    inspected++;
                }
                check("3 ...a band of " + steps + " rows of shallow water from row " + top + " is drawn as one of fresh water, tile for tile, " + inspected + " tiles at " + px + " px a plot (" + differing + " pixels differ, " + decksShallow + " of deck)",
                        differing == 0 && decksShallow > 0);
                check("3 ...and a band of deep sea there has no bridge, no deck (" + decksSea + " pixels)", decksSea == 0);
            }
        }

        /* ============ 4. the dials ============ */
        System.out.println("\n--- 4 the dials: 34 plots for a highway and a railway, 14 for an arterial, 6 for a street (D12) ---");
        check("a highway spans 34 plots (1,020 m): TilePainter.MAX_BRIDGE[HIGHWAY]", TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY] == 34);
        check("a railway spans 34 plots: TilePainter.RAIL_BRIDGE", TilePainter.RAIL_BRIDGE == 34);
        check("an arterial still spans 14 plots (420 m): DistrictPlan.ARTERIAL_BRIDGE, no longer a highway's", DistrictPlan.ARTERIAL_BRIDGE == 14);
        check("a street still spans 6 plots (180 m): TilePainter.STREET_BRIDGE, and DistrictPlan's is the same", TilePainter.STREET_BRIDGE == 6 && DistrictPlan.STREET_BRIDGE == 6);
        check("a gravel and a paved street's reach in MAX_BRIDGE is STREET_BRIDGE", TilePainter.MAX_BRIDGE[BuildingVisual.GRAVEL] == TilePainter.STREET_BRIDGE && TilePainter.MAX_BRIDGE[BuildingVisual.PAVED] == TilePainter.STREET_BRIDGE);
        check("the highway's and the railway's reach is 1,020 m: 34 plots of World.PLOT_M", Math.abs(34 * World.PLOT_M - 1020) < 1e-9);

        System.out.println(fails == 0 ? "\nThe crossings hold." : "\n" + fails + " FAILED");
        System.exit(fails);
    }
}
