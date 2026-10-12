package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectangle - what lies under a plot (its ground, whose it is, the fields there), the box the land office opens on, and the hover card's words; pure, so MapCheck holds the hit-testing.
 *
 * WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
 * 2.8). The land office's map is how a player now chooses ground: a click
 * on a side selects it, a click on an offer selects that offer, a hover
 * names what is under the pointer. Each of those is a question about the
 * land - which side and place a point lies in, whose it is - that CityLand
 * already answers for the model; this class asks it the view's way and keeps
 * the answers out of the toolkit, so a harness can check that the map and
 * the model agree on every plot.
 *
 * ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.4).
 * A plot's holding is the grid's owner (CityLand.holdingOf()); an offer is a
 * rectangle, so pick() is the owner and then 24 rectangle tests; the city's
 * edge is the horizontal and vertical runs where owned ground meets unowned,
 * read from the grid's leaves (outline()). Since 0.7.69 (batch M5) it also
 * says which of an offer's rectangle is the city's own ground (ownedIn()),
 * so the map hatches only what is on offer, and where on the offer its
 * number stands (labelBlock()). Until 0.7.66 the land was a centre and
 * forty lanes of wedges, and these were radii and bands.
 *
 * COORDINATES: the world's plots. A plot x covers x to x + 1, so a rectangle
 * [x0, x1) is drawn from x0 to x1 and the outlines lie on plot edges.
 */
public final class LandMap {

    private LandMap() { }

    /** Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. */
    public static final int OUTSIDE = 0, CENTRE = 1, BOUGHT = 2, OFFER = 3;

    /** What lies under a plot: its ground (a World class), whose it is, its side and place, the purchase (from 0, in the order bought) or the offer. */
    public record Pick(long x, long y, byte ground, int owner, int side, int place, int purchase, LandParcel offer) { }

    /** The plot (x, y) on this land, against these offers: its holding on the grid, else the offer whose rectangle holds it. */
    public static Pick pick(CityLand land, LandMarket market, long x, long y) {
        byte ground = World.of(land.key()).terrainAt(x, y);
        int sp = sidePlaceOf(land, x, y), side = sp / GridOffers.PLACES, place = sp % GridOffers.PLACES;
        int h = land.holdingOf(x, y);
        if (h == CityLand.CENTRE) return new Pick(x, y, ground, CENTRE, side, place, -1, null);
        if (h > CityLand.CENTRE) {
            LandParcel o = land.purchases().get(h - 1).offer();
            return new Pick(x, y, ground, BOUGHT, o.getSide(), o.getPlace(), h - 1, o);
        }
        if (market != null) {
            for (LandParcel o : market.getListing()) {
                if (o.contains(x, y)) return new Pick(x, y, ground, OFFER, o.getSide(), o.getPlace(), -1, o);
            }
        }
        return new Pick(x, y, ground, OUTSIDE, side, place, -1, null);
    }

    /** The side and place a plot lies in, seen from the site: side x GridOffers.PLACES + place (GridOffers.sidePlace()). */
    public static int sidePlaceOf(CityLand land, long x, long y) {
        return GridOffers.sidePlace(x - land.siteX(), y - land.siteY());
    }

    /* ----------------------------- the outlines ----------------------------- */

    /** An offer's rectangle as its four corners in plots, x then y: north-west, north-east, south-east, south-west. */
    public static double[] rect(LandParcel o) {
        return new double[] { o.getX0(), o.getY0(), o.getX1(), o.getY0(), o.getX1(), o.getY1(), o.getX0(), o.getY1() };
    }

    /** The centre's box: {x0, y0, x1, y1} in plots, the extent of its blocks. */
    public static double[] centre(CityLand land) {
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        for (LandGrid.Fill f : land.centreRects()) {
            x0 = Math.min(x0, f.x0());
            y0 = Math.min(y0, f.y0());
            x1 = Math.max(x1, f.x1());
            y1 = Math.max(y1, f.y1());
        }
        if (x0 > x1) return new double[] { land.siteX(), land.siteY(), land.siteX() + 1, land.siteY() + 1 };
        return new double[] { x0, y0, x1, y1 };
    }

    /** The four edges a run of the outline lies on, by the side of the city's ground it bounds: its north edge, east, south and west. */
    static final int NORTH_EDGE = 0, EAST_EDGE = 1, SOUTH_EDGE = 2, WEST_EDGE = 3;

    /**
     * The city's edge (spec-grid 2.4): every run along plot edges with owned
     * ground on one side and none on the other, from the grid's leaves - each
     * FULL leaf's four edges, the unowned stretches beyond them - joined
     * where they meet end to end on one line, so a run is the whole straight
     * stretch of edge it lies on (0.7.69, batch M5: one per leaf's side
     * until then). As {x0s, y0s, x1s, y1s}, each run horizontal or vertical,
     * in plots, and running with the city's ground on its right: east along
     * a north edge, south down an east one, west along a south one, north up
     * a west one (clockwise, y south) - MapFrame.runOnScreen() draws it on
     * the pixels just inside that ground, crisp.
     */
    public static double[][] outline(CityLand land) {
        LandGrid g = land.grid();
        List<long[]> raw = new ArrayList<>();
        g.leaves((level, x, y, h) -> {
            long b = 1L << level;
            edge(g, raw, NORTH_EDGE, x, y - 1, b, true, y);     // the row above
            edge(g, raw, SOUTH_EDGE, x, y + b, b, true, y + b); // the row below
            edge(g, raw, WEST_EDGE, x - 1, y, b, false, x);     // the column left
            edge(g, raw, EAST_EDGE, x + b, y, b, false, x + b); // the column right
        });
        // {edge, line, from, to}: by edge, then line, then where it starts, so stretches that meet are neighbours.
        raw.sort((a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : a[1] != b[1] ? Long.compare(a[1], b[1]) : Long.compare(a[2], b[2]));
        List<long[]> runs = new ArrayList<>();
        for (long[] r : raw) {
            long[] last = runs.isEmpty() ? null : runs.get(runs.size() - 1);
            if (last != null && last[0] == r[0] && last[1] == r[1] && last[3] == r[2]) last[3] = r[3];
            else runs.add(r.clone());
        }
        double[][] out = new double[4][runs.size()];
        for (int i = 0; i < runs.size(); i++) {
            long[] r = runs.get(i);
            long line = r[1], from = r[2], to = r[3];
            double[] run;
            switch ((int) r[0]) {
                case NORTH_EDGE: run = new double[] { from, line, to, line }; break;   // east
                case EAST_EDGE:  run = new double[] { line, from, line, to }; break;   // south
                case SOUTH_EDGE: run = new double[] { to, line, from, line }; break;   // west
                default:         run = new double[] { line, to, line, from }; break;   // north
            }
            for (int k = 0; k < 4; k++) out[k][i] = run[k];
        }
        return out;
    }

    /** The unowned stretches of a strip one plot thick and `n` long beside a leaf (a row at (x, y) along x, or a column along y), as {edge, the line `at`, from, to}. */
    private static void edge(LandGrid g, List<long[]> runs, int side, long x, long y, long n, boolean row, long at) {
        long un = row ? g.unowned(x, y, x + n, y + 1) : g.unowned(x, y, x + 1, y + n);
        if (un == 0) return;
        if (un == n) {
            runs.add(row ? new long[] { side, at, x, x + n } : new long[] { side, at, y, y + n });
            return;
        }
        long half = n / 2;
        if (row) {
            edge(g, runs, side, x, y, half, true, at);
            edge(g, runs, side, x + half, y, n - half, true, at);
        } else {
            edge(g, runs, side, x, y, half, false, at);
            edge(g, runs, side, x, y + half, n - half, false, at);
        }
    }

    /**
     * The city's own ground inside an offer's rectangle (0.7.69, batch M5):
     * a coarse offer takes in the finer steps beside it (spec-grid 2.1), so
     * its rectangle may cover ground the city owns, which is not on offer.
     * The grid's leaves there, clipped to the rectangle, as {x0, y0, x1, y1}
     * each in plots - they never overlap; none for most offers. The map
     * hatches the rectangle less these: exactly the offer's free plots.
     */
    public static List<double[]> ownedIn(CityLand land, LandParcel o) {
        List<double[]> out = new ArrayList<>();
        LandGrid g = land.grid();
        long ox0 = o.getX0(), oy0 = o.getY0(), ox1 = o.getX1(), oy1 = o.getY1();
        if (g.owned(ox0, oy0, ox1, oy1) == 0) return out;
        g.leaves((level, x, y, h) -> {
            long b = 1L << level;
            long x0 = Math.max(x, ox0), y0 = Math.max(y, oy0), x1 = Math.min(x + b, ox1), y1 = Math.min(y + b, oy1);
            if (x0 < x1 && y0 < y1) out.add(new double[] { x0, y0, x1, y1 });
        });
        return out;
    }

    /** What the map writes on an offer: its place, 1 to 6 from the left as you face out, as its row in the land office reads it. */
    public static String placeLabel(LandParcel o) {
        return String.valueOf(o.getPlace() + 1);
    }

    /**
     * Where the map writes it (0.7.69): the offer's whole rectangle when it
     * is all free; else - a coarse rectangle takes in the finer steps of the
     * city's own ground beside it - the block of it with the most free
     * ground, the farthest from the site on a tie, and while that holds some
     * of the city's ground the freest of its quarters, and so down, as long
     * as a quarter is still `leastPlots` across (what holds a number at the
     * view's scale). So the number stands on what is on offer. As {x0, y0,
     * x1, y1} in plots.
     */
    public static double[] labelBlock(CityLand land, LandParcel o, double leastPlots) {
        LandGrid g = land.grid();
        if (g.owned(o.getX0(), o.getY0(), o.getX1(), o.getY1()) == 0) return new double[] { o.getX0(), o.getY0(), o.getX1(), o.getY1() };
        long b = 1L << o.getLevel();
        long bestFree = -1, bestReach = -1, bx = o.getX0(), by = o.getY0();
        for (long y = o.getY0(); y + b <= o.getY1(); y += b) {
            for (long x = o.getX0(); x + b <= o.getX1(); x += b) {
                long free = g.unowned(x, y, x + b, y + b), reach = reach(land, x, y, b);
                if (free > bestFree || (free == bestFree && reach > bestReach)) {
                    bestFree = free;
                    bestReach = reach;
                    bx = x;
                    by = y;
                }
            }
        }
        if (bestFree < 0) return new double[] { o.getX0(), o.getY0(), o.getX1(), o.getY1() };
        long side = b;
        while (side > 1 && side / 2 >= leastPlots && g.owned(bx, by, bx + side, by + side) > 0) {
            long h = side / 2, qx = bx, qy = by;
            bestFree = -1;
            bestReach = -1;
            for (int q = 0; q < 4; q++) {
                long x = bx + (q & 1) * h, y = by + (q >> 1) * h;
                long free = g.unowned(x, y, x + h, y + h), reach = reach(land, x, y, h);
                if (free > bestFree || (free == bestFree && reach > bestReach)) {
                    bestFree = free;
                    bestReach = reach;
                    qx = x;
                    qy = y;
                }
            }
            bx = qx;
            by = qy;
            side = h;
        }
        return new double[] { bx, by, bx + side, by + side };
    }

    /** How far a square of plots lies from the site: twice the L-infinity reach of its middle from the site plot's middle, in plots (a whole number). */
    private static long reach(CityLand land, long x, long y, long side) {
        return Math.max(Math.abs(2 * x + side - 2 * land.siteX() - 1), Math.abs(2 * y + side - 2 * land.siteY() - 1));
    }

    /** The farthest the city's own ground reaches from the site (L-infinity), in plots: its owned box's farthest edge. */
    public static double ownedReach(CityLand land) {
        LandGrid g = land.grid();
        if (g.ownedPlots() == 0) return 0;
        return Math.max(Math.max(land.siteX() - g.minX(), g.maxX() - land.siteX()),
                Math.max(land.siteY() - g.minY(), g.maxY() - land.siteY()));
    }

    /** ...and the farthest any offer standing reaches. */
    public static double offersReach(CityLand land, LandMarket market) {
        double r = ownedReach(land);
        if (market != null) {
            for (LandParcel p : market.getListing()) {
                r = Math.max(r, Math.max(Math.max(land.siteX() - p.getX0(), p.getX1() - land.siteX()),
                        Math.max(land.siteY() - p.getY0(), p.getY1() - land.siteY())));
            }
        }
        return r;
    }

    /** The box the land office opens on: the city's own ground - its owned box - with MapFrame.OPENING_MARGIN of its half-size round it on each axis, {x0, y0, x1, y1}. */
    public static double[] openingBox(CityLand land) {
        LandGrid g = land.grid();
        double x0 = g.ownedPlots() == 0 ? land.siteX() : g.minX(), y0 = g.ownedPlots() == 0 ? land.siteY() : g.minY();
        double x1 = g.ownedPlots() == 0 ? land.siteX() + 1 : g.maxX(), y1 = g.ownedPlots() == 0 ? land.siteY() + 1 : g.maxY();
        double mx = MapFrame.OPENING_MARGIN * (x1 - x0) / 2 + 1, my = MapFrame.OPENING_MARGIN * (y1 - y0) / 2 + 1;
        return new double[] { x0 - mx, y0 - my, x1 + mx, y1 + my };
    }

    /**
     * Opens a view on the city as the land office does (0.7.79, star O3-2):
     * openingBox() fitted (MapFrame.fit()), then drawn closer if the smallest
     * offer standing would be drawn under MapFrame.OPENING_OFFER_PX across -
     * to that size, about the same centre, so every offer the view shows
     * whole holds its number. ui/MapView opens both its maps here, and
     * MapCheck its mirror.
     *
     * A CITY TALLER THAN ITS SMALLEST OFFERS ALLOW then opens on its middle:
     * a town whose offers are still one block of 120 m across while its own
     * ground is more than 125 plots tall or 188 wide does not fit the 600 x
     * 400 map at 3 px a plot, FIT_MARGIN of it (the playtest's town of month
     * 420, 80 x 192 plots, fits at 1.96 - runs/fixO3-notes.md). The real
     * cities measured open at their fit.
     *
     * WHY. The number's size was set on MapCheck's town of ten years, which
     * opened at 2.98 px a plot (0.7.69); from 0.7.78 the same town grew to
     * open at 2.74, where its offers one block of 120 m across were 10.96 px
     * and one was drawn 10 px - whole and unnumbered. Any town opening under
     * 2.75 px a plot with offers that small did the same (runs/fixO2-notes.md).
     */
    public static void open(MapFrame f, CityLand land, LandMarket market) {
        double[] box = openingBox(land);
        f.fit(box[0], box[1], box[2], box[3]);
        long side = smallestOfferSide(market);
        if (side <= 0) return;
        double wanted = MapFrame.OPENING_OFFER_PX / side;
        if (wanted > f.scale()) f.set(f.centreX(), f.centreY(), wanted);
    }

    /** The shorter side of the smallest offer standing, in plots: the least of every listed offer's width and height (0.7.79); 0 with none listed. */
    public static long smallestOfferSide(LandMarket market) {
        long least = 0;
        if (market == null) return 0;
        for (LandParcel p : market.getListing()) {
            long side = Math.min(p.getX1() - p.getX0(), p.getY1() - p.getY0());
            if (side > 0 && (least == 0 || side < least)) least = side;
        }
        return least;
    }

    /* ----------------------------- the fields ----------------------------- */

    /** A field one of whose sites lies under a plot, and which site. */
    public record FieldAt(Deposit field, int site) { }

    /** The fields with a site on plot (x, y): the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. */
    public static List<FieldAt> fieldsAt(CityLand land, long x, long y) {
        List<FieldAt> out = new ArrayList<>();
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double reach = Deposit.mostReach(r);
            List<Deposit> near = new ArrayList<>();
            for (int cell : CityLand.cellsUnder(x - reach, y - reach, x + reach, y + reach)) near.addAll(land.fieldsIn(cell, r));
            if (r == Resource.IRON && land.legacySites() > 0 && land.legacyX() >= 0) {
                near.add(new Deposit(Resource.IRON, -1, 0, land.legacyX(), land.legacyY(),
                        Math.min(World.LEGACY_MAX_SITES, land.legacySites()), land.centreAmount(Resource.IRON)));
            }
            double w = Deposit.siteWidth(r);
            for (Deposit f : near) {
                double o = f.reach();
                if (Math.abs(f.x() - x) > o + 1 || Math.abs(f.y() - y) > o + 1) continue;
                for (int s = 0; s < f.sites(); s++) {
                    double[] at = f.siteAt(s);
                    double cx = f.x() + at[0], cy = f.y() + at[1];
                    // A site's square of plots, as the map draws it (CityMap.siteList()).
                    long sx0 = Math.round(cx - w / 2), sy0 = Math.round(cy - w / 2);
                    long sx1 = Math.max(sx0, Math.round(cx + w / 2) - 1), sy1 = Math.max(sy0, Math.round(cy + w / 2) - 1);
                    if (x >= sx0 && x <= sx1 && y >= sy0 && y <= sy1) {
                        out.add(new FieldAt(f, s));
                        break;
                    }
                }
            }
        }
        return out;
    }

    /** The fields of a resource whose centres lie in a box of plots - the deposits the far views mark - at most `most` of them: the world's as the city sees them (CityLand.fieldsIn(), 0.7.99). */
    public static List<Deposit> fieldsIn(CityLand land, Resource r, double x0, double y0, double x1, double y1, int most) {
        List<Deposit> out = new ArrayList<>();
        if (!r.inFields()) return out;
        for (int cell : CityLand.cellsUnder(x0, y0, x1, y1)) {
            for (Deposit f : land.fieldsIn(cell, r)) {
                if (f.x() < x0 || f.x() > x1 || f.y() < y0 || f.y() > y1) continue;
                out.add(f);
                if (out.size() >= most) return out;
            }
        }
        if (r == Resource.IRON && land.legacySites() > 0 && land.legacyX() >= x0 && land.legacyX() <= x1
                && land.legacyY() >= y0 && land.legacyY() <= y1) {
            out.add(new Deposit(Resource.IRON, -1, 0, land.legacyX(), land.legacyY(),
                    Math.min(World.LEGACY_MAX_SITES, land.legacySites()), land.centreAmount(Resource.IRON)));
        }
        return out;
    }

    /* ----------------------------- the words ----------------------------- */

    /** A World class as the hover card names it; since 0.7.127 ice and the desert by their own names (spec-globe 3.9). */
    public static String groundWords(byte ground) {
        switch (ground) {
            case World.FOREST: return "Forest";
            case World.FRESH:  return "Lake or river";
            case World.SALT:   return "Sea";
            case World.SAND:   return "Beach";
            case World.ICE:    return "Ice";
            case World.DESERT: return "Desert";
            default:           return "Grass";
        }
    }

    /** Tonnes to three figures in their unit: "449 Mt", "12.8 Mt", "150 kt", "200 t", "1.12 Pt". */
    public static String tonnes(double t) {
        double a = Math.abs(t);
        double[] at = { 1e15, 1e12, 1e9, 1e6, 1e3 };
        String[] unit = { " Pt", " Tt", " Gt", " Mt", " kt" };
        for (int i = 0; i < at.length; i++) if (a >= at[i]) return three(t / at[i]) + unit[i];
        return String.format("%,.0f t", t);
    }

    /** A figure to three significant figures, no trailing zeros: 449, 12.8, 1.12. */
    static String three(double v) {
        if (v == 0) return "0";
        return new java.math.BigDecimal(v).round(new java.math.MathContext(3)).stripTrailingZeros().toPlainString();
    }

    /** What a resource's amount is, in its unit: tonnes for the fields, cubic metres for forest. */
    public static String amountWords(Resource r, double amount) {
        return r == Resource.FOREST ? String.format("%,.0f m³", amount) : tonnes(amount);
    }

    /** Thousands of US dollars as the screens write them: "US$12.1M". */
    public static String usd(double thousands) {
        return Formats.INSTANCE.amount(thousands).replace("$", Currency.FOREIGN_SYMBOL);
    }

    /** An area given in km2, as the player reads it (LandManager.areaWords(), since 0.7.68): "0.842 km²", "7,200 m²". */
    public static String area(double km2) {
        return LandManager.areaWords(LandManager.sqFt(km2));
    }

    /** Whose a plot is, in the hover card's words: "The city's centre", "The city's: North 3, bought in month 1,204", "On offer: North 3 · 0.842 km², 0.79 dry · US$12.1M", "Not the city's". */
    public static String ownerWords(CityLand land, Pick p) {
        switch (p.owner()) {
            case CENTRE:
                return "The city's centre, its land at the founding";
            case BOUGHT: {
                if (p.purchase() < 0) return "The city's: " + CityLand.sideName(p.side()) + " " + (p.place() + 1);
                CityLand.Purchase bought = land.purchases().get(p.purchase());
                return String.format("The city's: %s, bought in month %,d", bought.offer().where(), bought.month());
            }
            case OFFER: {
                LandParcel o = p.offer();
                return "On offer: " + o.where() + " · " + area(o.getKm2()) + ", "
                        + LandManager.partFigure(LandManager.sqFt(o.getDryKm2()), LandManager.sqFt(o.getKm2())) + " dry · " + usd(o.getPriceUsd());
            }
            default:
                return "Not the city's";
        }
    }

    /**
     * Whose a field is (0.7.64, batch L): it goes whole with the piece of
     * ground holding its centre plot (CityLand, THE FIELDS ON A PIECE OF
     * GROUND), wherever the site under the pointer lies - "the city's,
     * whole", "all of it with South 3, on offer", or "" for a field no offer
     * holds yet. A converted centre's legacy field is the city's. A field a
     * converted city holds only part of (0.7.67) is the city's site by site:
     * "6 of its 8 sites the city's".
     */
    public static String fieldOwnerWords(CityLand land, LandMarket market, Deposit d) {
        if (d.cell() < 0) return "the city's, whole";
        if (land.isPart(d)) {
            int mine = 0;
            for (int k = 0; k < d.sites(); k++) if (land.siteHolding(d, k) >= 0) mine++;
            return String.format("%,d of its %,d sites the city's", mine, d.sites());
        }
        Pick p = pick(land, market, d.x(), d.y());
        switch (p.owner()) {
            case CENTRE:
            case BOUGHT: return "the city's, whole";
            case OFFER:  return "all of it with " + p.offer().where() + ", on offer";
            default:     return "";
        }
    }

    /** ...and the hover card's line for it: fieldWords() and, when it has one, whose it is - "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt · all of it with South 3, on offer". */
    public static String fieldWords(FieldAt f, CityLand land, LandMarket market) {
        String whose = fieldOwnerWords(land, market, f.field());
        return fieldWords(f) + (whose.isEmpty() ? "" : " · " + whose);
    }

    /** A field under the pointer: "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt". */
    public static String fieldWords(FieldAt f) {
        Deposit d = f.field();
        return d.kind().label() + ": a field of " + d.sites() + (d.sites() == 1 ? " site, " : " sites, ")
                + amountWords(d.kind(), d.amount()) + " · this site " + amountWords(d.kind(), d.siteAmount(f.site()));
    }

    /** The hover card's lines for a plot: its ground, whose it is, on Earth where it is and its geology (earthWords(), 0.7.119), and each field with a site on it. */
    public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y) {
        return hoverWords(land, market, x, y, Integer.MAX_VALUE);
    }

    /** The most fields the expanded map's hover card lists: 3 (MapView's since 0.7.61; the model's since 0.7.135, its card being hoverWords()'s). */
    public static final int CARD_FIELDS = 3;

    /** ...with no more than `fieldsMost` fields (the expanded map's card lists CARD_FIELDS; since 0.7.135 its card is this). */
    public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y, int fieldsMost) {
        Pick p = pick(land, market, x, y);
        List<String> lines = new ArrayList<>();
        lines.add(groundWords(p.ground()));
        lines.add(ownerWords(land, p));
        lines.addAll(earthWords(land, x, y));
        List<FieldAt> under = fieldsAt(land, x, y);
        for (int i = 0; i < under.size() && i < fieldsMost; i++) lines.add(fieldWords(under.get(i), land, market));
        return lines;
    }

    /* ----------------------------- on Earth (0.7.119, batch E5) ----------------------------- */

    /**
     * The hover card's lines for a plot on the real Earth (spec-earth 2 and
     * 4; Jerus's answer to the study's sixth question, 2026-10-10: names for
     * finding a place, and no borders): where it is - its distance and
     * direction from the nearest of the data's places, by name alone
     * (placeWords()) - and the geologic province under it with its class
     * (provinceWords()); past the planet's rim OFF_PLANET_WORDS; none on a
     * generated world, and no province line where the data has none. The
     * province's deposit multipliers are the founding card's (EarthSite, 0.7.121; ★E5).
     */
    public static List<String> earthWords(CityLand land, long x, long y) {
        List<String> out = new ArrayList<>();
        World w = World.of(land.key());
        if (!w.isEarth()) return out;
        EarthTerrain e = w.earthTerrain();
        double[] ll = new double[2];
        if (!e.projection().latLonOfPlot(x + 0.5, y + 0.5, ll)) {
            out.add(OFF_PLANET_WORDS);
            return out;
        }
        return earthWords(e, ll[0], ll[1]);
    }

    /**
     * ...by latitude and longitude (0.7.135, batch E9; spec-globe 3.7): the
     * nearest place and the province under the point, the plot's lines at
     * its centre's - what the globe's hover says wherever the pointer is on
     * the planet, beyond the world's square too.
     */
    public static List<String> earthWords(EarthTerrain e, double lat, double lon) {
        List<String> out = new ArrayList<>();
        EarthData.Place near = nearestPlace(e.data(), lat, lon);
        if (near != null) out.add(placeWords(near, lat, lon));
        Geology geo = e.geology();
        int row = geo.provinceAt(lat, lon);
        if (row > 0) out.add(provinceWords(e.data().province(row), geo.kindOf(row)));
        return out;
    }

    /** Whose a point on the globe is beyond the city's plane and its reach (spec-globe 3.7). */
    public static final String NOT_THE_CITYS = "Not the city's";

    /**
     * The hover card's lines on the globe (0.7.135, batch E9; spec-globe
     * 3.7) at a screen point of the view `f`: where the globe draws the
     * city's plane - within the world's square and GlobeFrame.planeDrawsKm()
     * of the city, the reach and more (GlobeFrame.planeHolds(), ★E9) - the
     * plane's own card for the plot under it (hoverWords()), so at the
     * handoff the globe and the plane say the same of the same point;
     * elsewhere on the planet the ground drawn there (`drawn`, a World
     * class), NOT_THE_CITYS, and the nearest place and the province by its
     * latitude and longitude (earthWords()). None off the planet. No
     * more than `fieldsMost` fields.
     */
    public static List<String> globeHoverWords(GlobeFrame f, CityLand land, LandMarket market, double sx, double sy, byte drawn, int fieldsMost) {
        double[] ll = f.latLonAt(sx, sy);
        if (ll == null) return new ArrayList<>();
        double[] xy = new double[2];
        if (f.planeHolds(sx, sy) && f.plotAt(sx, sy, xy)) return hoverWords(land, market, (long) Math.floor(xy[0]), (long) Math.floor(xy[1]), fieldsMost);
        List<String> lines = new ArrayList<>();
        lines.add(groundWords(drawn));
        lines.add(NOT_THE_CITYS);
        World w = World.of(land.key());
        if (w.isEarth()) lines.addAll(earthWords(w.earthTerrain(), ll[0], ll[1]));
        return lines;
    }

    /* ----------------------------- the legend by tier (0.7.135, batch E9) ----------------------------- */

    /** A legend's entry: its swatch's colour (0xAARRGGBB) and its words. */
    public record Entry(int colour, String words) { }

    /** The city's ring on the globe: the founding globe's site mark's light blue (Palette.ACCENT_LIGHT, #8cc4ff). */
    public static final int RING = 0xff8cc4ff;

    /** The ring's legend words. */
    public static final String CITY_RING_WORDS = "The city";

    /** The city's edge's legend words (its swatch white, as it is drawn). */
    public static final String EDGE_WORDS = "The city's edge";

    /** The offers' legend words (their swatch the offers' pink, #f6a6c9). */
    public static final String OFFER_WORDS = "On offer";

    /** The offers' pink: the mockup's band edge (rgba(246, 166, 201)). */
    public static final int OFFER_EDGE = 0xfff6a6c9;

    /**
     * The expanded map's legend in a tier (spec-globe 3.7: "the legend on the
     * globe lists the kinds of ground and the city's ring; the whole legend
     * is back from the handoff in"): on the globe (GlobeFrame.PICTURE and
     * GROUND) the seven kinds of ground, the city's ring and its edge - and
     * in the GROUND tier, where the city's plane draws within the reach, the
     * offers; on the plane (GlobeFrame.PLANE, and a generated world at every
     * scale) the plane's legend, planeLegend().
     */
    public static List<Entry> legend(int tier) {
        if (tier == GlobeFrame.PLANE) return planeLegend();
        List<Entry> out = new ArrayList<>();
        for (byte g : new byte[] { World.GRASS, World.FOREST, World.DESERT, World.ICE, World.SAND, World.FRESH, World.SALT }) {
            out.add(new Entry(TileRaster.GROUND[g], groundWords(g)));
        }
        out.add(new Entry(RING, CITY_RING_WORDS));
        out.add(new Entry(0xffffffff, EDGE_WORDS));
        if (tier == GlobeFrame.GROUND) out.add(new Entry(OFFER_EDGE, OFFER_WORDS));
        return out;
    }

    /** The plane's legend (the expanded map's since 0.7.61; ui/MapView's until 0.7.135): the ten classes and flats, the three roads, (0.7.88) the tracks and (0.7.72) the railway, (0.7.97) the nine of the shore and the sea, (0.7.127) ice and the desert, the land, the six resources in fields. */
    public static List<Entry> planeLegend() {
        List<Entry> out = new ArrayList<>();
        for (int c = 0; c < BuildingVisual.CLASSES; c++) {
            out.add(new Entry(BuildingVisual.FILL[c], BuildingVisual.CLASS_NAMES[c]));
            if (c == BuildingVisual.HOME) out.add(new Entry(BuildingVisual.FLATS_FILL, "Flats"));
        }
        out.add(new Entry(TileRaster.ROAD[BuildingVisual.GRAVEL], "Gravel road"));
        out.add(new Entry(TileRaster.ROAD[BuildingVisual.PAVED], "Paved road"));
        out.add(new Entry(TileRaster.TRACK_DASH, TRACK_ENTRY));
        out.add(new Entry(TileRaster.ROAD[BuildingVisual.HIGHWAY], "Highway"));
        out.add(new Entry(TileRaster.RAIL_LINE, "Railway"));
        // At sea and on the shore (0.7.97).
        out.add(new Entry(BuildingVisual.CAMPUS_FILL, "Refinery"));
        out.add(new Entry(BuildingVisual.TERMINAL_FILL, "Sea terminal"));
        out.add(new Entry(TileRaster.QUAY_DECK, "Quay"));
        out.add(new Entry(BuildingVisual.TANKS_FILL, "Tank farm"));
        out.add(new Entry(TileRaster.OIL_TAN, "Platform, pipeline"));
        out.add(new Entry(ShipShapes.IMPORT, "Ship in, full"));
        out.add(new Entry(ShipShapes.EXPORT, "Ship out, full"));
        out.add(new Entry(ShipShapes.EMPTY, "Ship, empty leg"));
        out.add(new Entry(ShipShapes.BOXES, "Box ship, both ways"));
        // The two kinds of ground the real Earth has and the map's colours alone would not name (0.7.127; spec-globe 3.9).
        out.add(new Entry(TileRaster.GROUND[World.ICE], groundWords(World.ICE)));
        out.add(new Entry(TileRaster.GROUND[World.DESERT], groundWords(World.DESERT)));
        out.add(new Entry(0xffffffff, EDGE_WORDS));
        out.add(new Entry(OFFER_EDGE, OFFER_WORDS));
        for (Resource r : Resource.values()) if (r.inFields()) out.add(new Entry(0xff000000 | r.colour(), r.label()));
        return out;
    }

    /** The legend's tracks (0.7.88; spec 5): a street the city has bought no road for. */
    public static final String TRACK_ENTRY = "Track";

    /** The hover past the planet's rim, where the map's square runs on beyond the antipode (spec-earth 2: the far node draws it as the void). */
    public static final String OFF_PLANET_WORDS = "Off the planet: the map runs on past the antipode, the point opposite the city";

    /** The eight directions a place is named from, clockwise from north. */
    static final String[] COMPASS = { "north", "north-east", "east", "south-east", "south", "south-west", "west", "north-west" };

    /** A geologic class in the hover's words, in Geology.Kind's order: ocean, shield, fold belt, rift, shelf, basin, other. */
    static final String[] KIND_WORDS = { "oceanic", "a shield", "a fold belt", "a rift", "a shelf", "a basin", "unclassed" };

    /**
     * Where a point lies from a place: "4.1 km south-west of Rotterdam" - the
     * great-circle distance (EarthProj.distanceM()), to a tenth of a km under
     * ten and to the km from there, and the direction from the place, of the
     * eight; the place's name alone at under a twentieth of a km, where the
     * tenth would read 0.0.
     */
    public static String placeWords(EarthData.Place p, double lat, double lon) {
        double km = EarthProj.distanceM(p.lat(), p.lon(), lat, lon) / 1000;
        String name = p.name().trim();
        if (km < 0.05) return name;
        double f1 = StrictMath.toRadians(p.lat()), f2 = StrictMath.toRadians(lat), dl = StrictMath.toRadians(EarthProj.normaliseLon(lon - p.lon()));
        double bearing = StrictMath.toDegrees(StrictMath.atan2(StrictMath.sin(dl) * StrictMath.cos(f2),
                StrictMath.cos(f1) * StrictMath.sin(f2) - StrictMath.sin(f1) * StrictMath.cos(f2) * StrictMath.cos(dl)));
        String toward = COMPASS[(int) Math.floorMod(Math.round(bearing / 45), 8L)];
        return (km < 10 ? String.format("%.1f km", km) : String.format("%,.0f km", km)) + " " + toward + " of " + name;
    }

    /** The province under a point and its class: "Geology: Anglo-Dutch Basin, a basin" (Geology's class, the hand table's over the rules'). */
    public static String provinceWords(EarthData.Province p, Geology.Kind kind) {
        return "Geology: " + p.name().trim() + ", " + KIND_WORDS[kind.ordinal()];
    }

    /** A reader's places as unit vectors, for the nearest (nearestPlace()): built once a reader. */
    private record Places(EarthData data, List<EarthData.Place> list, double[] xyz) {
        static Places of(EarthData d) {
            List<EarthData.Place> list = d.places();
            double[] xyz = new double[3 * list.size()];
            for (int i = 0; i < list.size(); i++) unit(list.get(i).lat(), list.get(i).lon(), xyz, 3 * i);
            return new Places(d, list, xyz);
        }
    }

    private static volatile Places places;

    /** A point's unit vector on the sphere into v at k. */
    private static void unit(double lat, double lon, double[] v, int k) {
        double f = StrictMath.toRadians(lat), l = StrictMath.toRadians(lon), c = StrictMath.cos(f);
        v[k] = c * StrictMath.cos(l);
        v[k + 1] = c * StrictMath.sin(l);
        v[k + 2] = StrictMath.sin(f);
    }

    /** The nearest of a reader's places to a point - the least angle between them, the first in the data's order (by population) on a tie; null when it has none. For naming only: the place a point is nearest, never whose ground it is. */
    public static EarthData.Place nearestPlace(EarthData d, double lat, double lon) {
        Places p = places;
        if (p == null || p.data() != d) {
            p = Places.of(d);
            places = p;
        }
        double[] v = new double[3];
        unit(lat, lon, v, 0);
        int best = -1;
        double most = Double.NEGATIVE_INFINITY;
        double[] xyz = p.xyz();
        for (int i = 0, k = 0; i < p.list().size(); i++, k += 3) {
            double c = xyz[k] * v[0] + xyz[k + 1] * v[1] + xyz[k + 2] * v[2];
            if (c > most) {
                most = c;
                best = i;
            }
        }
        return best < 0 ? null : p.list().get(best);
    }

    /** One building of each class, as the hover card names a type it has no name for. */
    public static final String[] CLASS_ONE = { "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School",
            "Health", "Safety", "Mine" };

    /**
     * What a painted plot holds, in the hover card's words, or null for bare
     * ground: a building by its type's name (nameOf, by type id; its class
     * when null) - since 0.7.64 every drawn building is one of the model's -
     * and since 0.7.88 "packed without a street" when the city had no room
     * for it by one (R7); a street by its surface, its width and its role
     * (spec-roads-and-ports.md 5: "Paved street, 15 m · arterial"), a track
     * as one the city has bought no road for, "a bridge" over water and
     * "crossing the railway" across the track; a highway, a street beneath
     * it, its ramp and (0.7.89) the railway bridging it; the railway's track.
     */
    public static String plotWords(TilePainter.Input in, TilePainter.Painted p, int plot,
                                   java.util.function.IntFunction<String> nameOf) {
        if (plot < 0 || plot >= TilePainter.PLOTS) return null;
        if (p.use[plot] == TilePainter.BUILDING) {
            int b = p.bld[plot] - 1;
            if (b < 0 || b >= p.buildings || p.btype[b] < 0 || p.btype[b] >= in.types.length) return null;
            BuildingVisual.Type t = in.types[p.btype[b]];
            if (t == null) return null;
            String name = nameOf == null ? null : nameOf.apply(t.id());
            String what = name != null ? name : CLASS_ONE[t.cls()];
            return p.bpacked[b] ? what + PACKED_WORDS : what;
        }
        boolean road = p.use[plot] == TilePainter.ROAD || (p.use[plot] == TilePainter.RAIL && p.road[plot] != 0);
        if (road && p.road[plot] == BuildingVisual.HIGHWAY && p.role[plot] == 0) {
            String w = p.bridge[plot] ? "Highway, a bridge" : "Highway";
            if (p.beneath[plot]) w += " · a street beneath";
            if ((in.marks[plot] & CityRuns.M_RAMP) != 0 && p.beneath[plot]) w += RAMP_WORDS;
            if (p.run[plot] == TilePainter.RAIL_OVER) w += RAIL_OVER_WORDS;
            return w;
        }
        if (road) {
            String role = p.role[plot] == TilePainter.BOULEVARD ? " · boulevard" : p.role[plot] == TilePainter.ARTERIAL ? " · arterial" : "";
            String more = (p.bridge[plot] ? " · a bridge" : "") + (p.use[plot] == TilePainter.RAIL ? " · crossing the railway" : "");
            if (p.road[plot] == TilePainter.TRACK) return TRACK_WORDS + role + more;
            String kind = p.road[plot] == BuildingVisual.PAVED ? "Paved street" : p.road[plot] == BuildingVisual.GRAVEL ? "Gravel street"
                    : "Street of highway plots";
            return kind + ", " + (p.width[plot] >= 2 ? FULL_WORDS : HALF_WORDS) + role + more;
        }
        if (p.use[plot] == TilePainter.RAIL) return p.bridge[plot] ? "Railway, a bridge" : "Railway";
        // The works at sea (0.7.97): a quay, a platform and its wells, its safety zone's ring, a pipe.
        int sea = p.sea[plot];
        switch (sea & TilePainter.SEA_WORK) {
            case TilePainter.QUAY:   return QUAY_WORDS;
            case TilePainter.JACKET: return JACKET_WORDS;
            case TilePainter.WELL:   return WELL_WORDS;
            default: break;
        }
        if ((sea & TilePainter.SEA_PIPE) != 0) return PIPE_WORDS;
        if ((sea & TilePainter.SEA_RING) != 0) return RING_WORDS;
        return null;
    }

    /** A terminal's quay in the hover (0.7.97): where its ships come alongside. */
    public static final String QUAY_WORDS = "Quay: a terminal's berth, where its ships come alongside";

    /** An offshore platform's jacket in the hover (0.7.97). */
    public static final String JACKET_WORDS = "Offshore platform: its wells drilled from the jacket";

    /** ...a platform well, on its sea site. */
    public static final String WELL_WORDS = "Platform well, on its sea site";

    /** ...the ring of a platform's safety zone (the research's 3.3 [W32]). */
    public static final String RING_WORDS = "A platform's 500 m safety zone";

    /** ...a crude pipeline, buried (spec-roads-and-ports.md 2.8). */
    public static final String PIPE_WORDS = "Crude pipeline, buried: the platforms' crude ashore without tankers";

    /** A highway's ramp in the hover (0.7.89; spec 2.7). */
    public static final String RAMP_WORDS = " · a ramp";

    /** ...and the railway bridging it (0.7.89; spec 2.8). */
    public static final String RAIL_OVER_WORDS = " · the railway over it";

    /** A track's words in the hover (0.7.88; spec 5). */
    public static final String TRACK_WORDS = "Track: the city has bought no road here";

    /** A street's widths in the hover: half (15 m) and full (30 m), a plot's 30 m right of way (spec 2.1). */
    public static final String HALF_WORDS = "15 m", FULL_WORDS = "30 m";

    /** A building packed without a street, in the hover (R7). */
    public static final String PACKED_WORDS = " · packed without a street";

    /* ----------------------------- buying from the map (0.7.137, batch M6) -----------------------------
     *
     * Jerus (2026-10-08): "click brings up menu, and to issue bond if doesnt
     * have cash"; "you should be able to drag and select in the map to buy
     * all the land parcel or specific ones" (docs/studies/spec-earth.md 3,
     * decisions B1 to B7). The expanded map's card and the box that picks
     * offers are ui/MapView's and ui/LandScreen's; what they work out is
     * here, so MapCheck 14 holds it: the picks (Picks - the view's, never
     * saved, dropped on a month and a load, a pick no longer listed falling
     * out); the offers a box on the screen meets on their free ground
     * (boxPicks(), through a ToScreen: the plane's MapFrame, the globe's
     * camera); and the card's figures (card()) - what the land office's rows
     * and "Buy the best N" read from the model, summed, the cash before and
     * after in the money the toggle pays in, and when the city is short the
     * funding page's two loans for the gap, the bond first: the funding page
     * takes its quotes from here (LandScreen.offers()), so the card and the
     * page issue the same paper.
     * --------------------------------------------------------------------------------------------------- */

    /**
     * The offers picked on the expanded map, in the order picked (B1 a click
     * takes one, B3 shift adds or drops one, B7 a box or Ctrl+A adds what it
     * takes): the view's own, never saved (spec-earth 3.2, "no new model
     * state"). They belong to one city's month: live() drops them all once
     * the month has turned or another city has been founded or loaded since
     * - into another Game, or into this one, which builds its world again
     * and so its land office (Game.loadGameSave(): a new LandManager) - and
     * drops a pick no longer listed, bought here or elsewhere.
     */
    public static final class Picks {
        private final List<Integer> ids = new ArrayList<>();
        private LandManager office;
        private int month;

        /** The picks for this city as it stands, in order: none once its month has turned or another city was founded or loaded, and only those still listed. */
        public List<Integer> live(Game g) {
            LandManager now = g == null ? null : g.getLandManager();
            if (now == null || now != office || g.getMonth() != month) {
                ids.clear();
                office = now;
                month = g == null ? 0 : g.getMonth();
            }
            if (g != null) {
                LandMarket market = g.getLandManager().getMarket();
                ids.removeIf(id -> market.find(id) == null);
            }
            return List.copyOf(ids);
        }

        /** A plain click: this offer alone. */
        public void only(Game g, int id) {
            live(g);
            ids.clear();
            ids.add(id);
        }

        /** Shift and a click: the offer added at the end, or dropped when it is picked already; true when added. */
        public boolean toggle(Game g, int id) {
            live(g);
            if (ids.remove(Integer.valueOf(id))) return false;
            ids.add(id);
            return true;
        }

        /** A box or Ctrl+A: these added at the end in their order, those picked already kept where they stand. */
        public void add(Game g, List<Integer> more) {
            live(g);
            for (int id : more) if (!ids.contains(id)) ids.add(id);
        }

        /** A chip's ×: this one dropped. */
        public void drop(int id) { ids.remove(Integer.valueOf(id)); }

        /** Esc, a click off the offers, the map collapsed: none. */
        public void clear() { ids.clear(); }
    }

    /** A point of the city's plane on a screen, {x, y} in pixels, or null where it is not seen: the plane's MapFrame (onScreen()), the globe's camera (GlobeFrame.screenOfPlot()). */
    public interface ToScreen { double[] at(double x, double y); }

    /** ...the plane's: a MapFrame's. */
    public static ToScreen onScreen(MapFrame f) {
        return (x, y) -> new double[] { f.screenX(x), f.screenY(y) };
    }

    /**
     * An offer's free ground as rectangles {x0, y0, x1, y1} in plots: its
     * rectangle cut on the lines of the city's own ground in it (ownedIn()),
     * the cuts the city owns left out - its whole rectangle when it takes in
     * none. Together exactly the plots pick() names the offer on.
     */
    public static List<double[]> freeCells(CityLand land, LandParcel o) {
        List<double[]> own = ownedIn(land, o);
        List<double[]> out = new ArrayList<>();
        if (own.isEmpty()) {
            out.add(new double[] { o.getX0(), o.getY0(), o.getX1(), o.getY1() });
            return out;
        }
        java.util.TreeSet<Double> xs = new java.util.TreeSet<>(), ys = new java.util.TreeSet<>();
        xs.add((double) o.getX0());
        xs.add((double) o.getX1());
        ys.add((double) o.getY0());
        ys.add((double) o.getY1());
        for (double[] r : own) {
            xs.add(r[0]);
            xs.add(r[2]);
            ys.add(r[1]);
            ys.add(r[3]);
        }
        Double[] cx = xs.toArray(new Double[0]), cy = ys.toArray(new Double[0]);
        for (int i = 0; i + 1 < cx.length; i++) {
            for (int j = 0; j + 1 < cy.length; j++) {
                double mx = (cx[i] + cx[i + 1]) / 2, my = (cy[j] + cy[j + 1]) / 2;
                boolean owned = false;
                for (double[] r : own) owned |= mx > r[0] && mx < r[2] && my > r[1] && my < r[3];
                if (!owned) out.add(new double[] { cx[i], cy[j], cx[i + 1], cy[j + 1] });
            }
        }
        return out;
    }

    /**
     * Whether a convex polygon - its corners xs, ys in order, either way
     * round - and the box [x0, x1] x [y0, y1] share some area: no axis of the
     * box's or of the polygon's edges parts them (the separating axis
     * theorem). A touch along an edge or at a corner is not a meeting.
     */
    public static boolean meets(double[] xs, double[] ys, double x0, double y0, double x1, double y1) {
        int n = xs.length;
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, minY = minX, maxY = maxX;
        for (int k = 0; k < n; k++) {
            minX = Math.min(minX, xs[k]);
            maxX = Math.max(maxX, xs[k]);
            minY = Math.min(minY, ys[k]);
            maxY = Math.max(maxY, ys[k]);
        }
        if (maxX <= x0 || minX >= x1 || maxY <= y0 || minY >= y1) return false;
        double[] bx = { x0, x1, x1, x0 }, by = { y0, y0, y1, y1 };
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double nx = ys[j] - ys[i], ny = xs[i] - xs[j];
            if (nx == 0 && ny == 0) continue;
            double pMin = Double.POSITIVE_INFINITY, pMax = Double.NEGATIVE_INFINITY, bMin = pMin, bMax = pMax;
            for (int k = 0; k < n; k++) {
                double v = nx * xs[k] + ny * ys[k];
                pMin = Math.min(pMin, v);
                pMax = Math.max(pMax, v);
            }
            for (int k = 0; k < 4; k++) {
                double v = nx * bx[k] + ny * by[k];
                bMin = Math.min(bMin, v);
                bMax = Math.max(bMax, v);
            }
            if (pMax <= bMin || bMax <= pMin) return false;
        }
        return true;
    }

    /**
     * The offers a box on the screen takes (B1b and B7: "every offer whose
     * free ground it touches"), in the order given: each whose free ground
     * (freeCells()) shares some area with the box between (sx0, sy0) and
     * (sx1, sy1) on the screen - each cell taken through `screen` as the
     * quadrilateral of its corners (on the plane a rectangle; through the
     * globe's camera near enough one over an offer), a cell with a corner
     * not seen not met. The city's own ground inside an offer's rectangle
     * is not the offer's, so a box over it alone takes nothing.
     */
    public static List<Integer> boxPicks(CityLand land, List<LandParcel> offers, ToScreen screen,
                                         double sx0, double sy0, double sx1, double sy1) {
        double x0 = Math.min(sx0, sx1), x1 = Math.max(sx0, sx1), y0 = Math.min(sy0, sy1), y1 = Math.max(sy0, sy1);
        List<Integer> out = new ArrayList<>();
        for (LandParcel o : offers) {
            for (double[] c : freeCells(land, o)) {
                double[] a = screen.at(c[0], c[1]), b = screen.at(c[2], c[1]), d = screen.at(c[2], c[3]), e = screen.at(c[0], c[3]);
                if (a == null || b == null || d == null || e == null) continue;
                if (meets(new double[] { a[0], b[0], d[0], e[0] }, new double[] { a[1], b[1], d[1], e[1] }, x0, y0, x1, y1)) {
                    out.add(o.getId());
                    break;
                }
            }
        }
        return out;
    }

    /**
     * The fields an offer holds whole (board 7: the card rings their sites
     * wherever they lie): those of every resource in fields centred on its
     * free ground - a field goes whole with the ground holding its centre
     * plot (fieldOwnerWords()) - but a converted city's field held site by
     * site, which is the city's.
     */
    public static List<Deposit> offerFields(CityLand land, LandParcel o) {
        List<Deposit> out = new ArrayList<>();
        for (Resource r : Resource.values()) {
            for (Deposit f : fieldsIn(land, r, o.getX0(), o.getY0(), o.getX1() - 1, o.getY1() - 1, Integer.MAX_VALUE)) {
                if (f.cell() >= 0 && !land.isPart(f) && !land.ownsPlot(f.x(), f.y())) out.add(f);
            }
        }
        return out;
    }

    /** A field's sites as the map draws them (fieldsAt(), CityMap's site squares): each a square of plots, {x0, y0, x1, y1}. */
    public static List<double[]> siteSquares(Deposit f) {
        double w = Deposit.siteWidth(f.kind());
        List<double[]> out = new ArrayList<>();
        for (int k = 0; k < f.sites(); k++) {
            double[] at = f.siteAt(k);
            double cx = f.x() + at[0], cy = f.y() + at[1];
            long sx0 = Math.round(cx - w / 2), sy0 = Math.round(cy - w / 2);
            long sx1 = Math.max(sx0, Math.round(cx + w / 2) - 1), sy1 = Math.max(sy0, Math.round(cy + w / 2) - 1);
            out.add(new double[] { sx0, sy0, sx1 + 1, sy1 + 1 });
        }
        return out;
    }

    /**
     * The card's figures for some picked offers (spec-earth 3.1, boards 5, 7
     * and 8): those still listed, in the order given, once each, and what the
     * land office's rows and "Buy the best N" read from the model for them,
     * summed - their ground by kind in km2 and the dry ground in square feet
     * (what "room for N houses" divides), each resource's sites and amount,
     * their price listed in US dollars and at today's rate in local money
     * (Game.landPriceUsd(), landPriceLocal()); paid the way the toggle says,
     * what the city holds of that money before and after (converting the
     * treasury's cash, from the vault its dollars), whether that needs a loan
     * and the gap (Game.landNeedsFunding(), landCashGap(), landVaultGapUsd());
     * and then the funding page's two loans for the gap, the bond first -
     * converting Game.BUILD_BOND_YEARS' bond for the cash, its face rounded
     * up to BUILD_BOND_GRANULE, and BUILD_NOTE_MONTHS' note; from the vault
     * the same two in dollars, issued abroad, or none while the window
     * abroad is shut. Books nothing.
     */
    public record Card(List<Integer> ids, double km2, double dryKm2, double freshKm2, double seaKm2, double drySqFt,
                       int[] sites, double[] amounts, double usd, double local, boolean vault, double before, double after,
                       boolean funding, double gap, DebtQuote bond, DebtQuote note) {

        /** The price in the money the toggle pays in: US dollars from the vault, local money converting. */
        public double price() { return vault ? usd : local; }
    }

    /** The card's figures for these picks, as the city stands. */
    public static Card card(Game g, List<Integer> picks) {
        LandMarket market = g.getLandManager().getMarket();
        List<Integer> ids = new ArrayList<>();
        double km2 = 0, dry = 0, fresh = 0, sea = 0, sqFt = 0;
        int[] sites = new int[Resource.values().length];
        double[] amounts = new double[Resource.values().length];
        for (int id : picks) {
            LandParcel p = market.find(id);
            if (p == null || ids.contains(id)) continue;
            ids.add(id);
            km2 += p.getKm2();
            dry += p.getKm2(CityLand.DRY);
            fresh += p.getKm2(CityLand.FRESH);
            sea += p.getKm2(CityLand.SEA);
            sqFt += p.getSizeSqFt();
            for (Resource r : Resource.values()) {
                sites[r.ordinal()] += p.getSites(r);
                amounts[r.ordinal()] += p.getAmount(r);
            }
        }
        boolean vault = g.isLandPaidFromVault();
        double usd = g.landPriceUsd(ids), local = g.landPriceLocal(ids);
        double before = vault ? g.getForeignAccounts().getReservesUsd() : g.getCash();
        boolean funding = !ids.isEmpty() && g.landNeedsFunding(ids);
        double gap = vault ? g.landVaultGapUsd(ids) : g.landCashGap(ids);
        DebtQuote bond = null, note = null;
        if (funding && !vault) {
            bond = g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            note = g.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        } else if (funding && g.foreignWindowOpen()) {
            bond = g.quoteForeignForCash("Term", gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            note = g.quoteForeignForCash("Note", gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        }
        return new Card(List.copyOf(ids), km2, dry, fresh, sea, sqFt, sites, amounts, usd, local, vault, before,
                before - (vault ? usd : local), funding, gap, bond, note);
    }
}
