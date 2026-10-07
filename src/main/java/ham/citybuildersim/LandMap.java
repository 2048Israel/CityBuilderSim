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
 * read from the grid's leaves (outline()). Until 0.7.66 the land was a
 * centre and forty lanes of wedges, and these were radii and bands.
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
        byte ground = World.of(land.seed()).terrainAt(x, y);
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

    /**
     * The city's edge (spec-grid 2.4): every run along a plot edge with
     * owned ground on one side and none on the other, from the grid's leaves
     * - each FULL leaf's four edges, the unowned stretches beyond them -
     * as {x0s, y0s, x1s, y1s}, each run horizontal or vertical, in plots.
     */
    public static double[][] outline(CityLand land) {
        LandGrid g = land.grid();
        List<double[]> runs = new ArrayList<>();
        g.leaves((level, x, y, h) -> {
            long b = 1L << level;
            edge(g, runs, x, y - 1, b, true, y);            // north: the row above
            edge(g, runs, x, y + b, b, true, y + b);        // south: the row below
            edge(g, runs, x - 1, y, b, false, x);           // west: the column left
            edge(g, runs, x + b, y, b, false, x + b);       // east: the column right
        });
        double[][] out = new double[4][runs.size()];
        for (int i = 0; i < runs.size(); i++) for (int k = 0; k < 4; k++) out[k][i] = runs.get(i)[k];
        return out;
    }

    /** The unowned stretches of a strip one plot thick and `n` long beside a leaf (a row at (x, y) along x, or a column along y), as runs on the line `at`. */
    private static void edge(LandGrid g, List<double[]> runs, long x, long y, long n, boolean row, long at) {
        long un = row ? g.unowned(x, y, x + n, y + 1) : g.unowned(x, y, x + 1, y + n);
        if (un == 0) return;
        if (un == n) {
            runs.add(row ? new double[] { x, at, x + n, at } : new double[] { at, y, at, y + n });
            return;
        }
        long half = n / 2;
        if (row) {
            edge(g, runs, x, y, half, true, at);
            edge(g, runs, x + half, y, n - half, true, at);
        } else {
            edge(g, runs, x, y, half, false, at);
            edge(g, runs, x, y + half, n - half, false, at);
        }
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

    /* ----------------------------- the fields ----------------------------- */

    /** A field one of whose sites lies under a plot, and which site. */
    public record FieldAt(Deposit field, int site) { }

    /** The fields with a site on plot (x, y): the world's, and a converted centre's legacy iron field. */
    public static List<FieldAt> fieldsAt(CityLand land, long x, long y) {
        List<FieldAt> out = new ArrayList<>();
        World world = World.of(land.seed());
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double reach = Deposit.mostReach(r);
            List<Deposit> near = new ArrayList<>();
            for (int cell : CityLand.cellsUnder(x - reach, y - reach, x + reach, y + reach)) near.addAll(CityLand.fields(world, cell, r));
            if (r == Resource.IRON && land.legacySites() > 0 && land.legacyX() >= 0) {
                near.add(new Deposit(Resource.IRON, -1, 0, land.legacyX(), land.legacyY(),
                        Math.min(World.MAX_SITES, land.legacySites()), land.centreAmount(Resource.IRON)));
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

    /** The fields of a resource whose centres lie in a box of plots - the deposits the far views mark - at most `most` of them. */
    public static List<Deposit> fieldsIn(CityLand land, Resource r, double x0, double y0, double x1, double y1, int most) {
        List<Deposit> out = new ArrayList<>();
        if (!r.inFields()) return out;
        World world = World.of(land.seed());
        for (int cell : CityLand.cellsUnder(x0, y0, x1, y1)) {
            for (Deposit f : CityLand.fields(world, cell, r)) {
                if (f.x() < x0 || f.x() > x1 || f.y() < y0 || f.y() > y1) continue;
                out.add(f);
                if (out.size() >= most) return out;
            }
        }
        if (r == Resource.IRON && land.legacySites() > 0 && land.legacyX() >= x0 && land.legacyX() <= x1
                && land.legacyY() >= y0 && land.legacyY() <= y1) {
            out.add(new Deposit(Resource.IRON, -1, 0, land.legacyX(), land.legacyY(),
                    Math.min(World.MAX_SITES, land.legacySites()), land.centreAmount(Resource.IRON)));
        }
        return out;
    }

    /* ----------------------------- the words ----------------------------- */

    /** A World class as the hover card names it. */
    public static String groundWords(byte ground) {
        switch (ground) {
            case World.FOREST: return "Forest";
            case World.FRESH:  return "Lake or river";
            case World.SALT:   return "Sea";
            case World.SAND:   return "Beach";
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

    /** The hover card's lines for a plot: its ground, whose it is, and each field with a site on it. */
    public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y) {
        Pick p = pick(land, market, x, y);
        List<String> lines = new ArrayList<>();
        lines.add(groundWords(p.ground()));
        lines.add(ownerWords(land, p));
        for (FieldAt f : fieldsAt(land, x, y)) lines.add(fieldWords(f, land, market));
        return lines;
    }

    /** One building of each class, as the hover card names a type it has no name for. */
    public static final String[] CLASS_ONE = { "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School",
            "Health", "Safety", "Mine" };

    /**
     * What a painted plot holds, in the hover card's words, or null for bare
     * ground: a building by its type's name (nameOf, by type id; its class
     * when null) - since 0.7.64 every drawn building is one of the model's -
     * or a road's kind, "a bridge" over water.
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
            return name != null ? name : CLASS_ONE[t.cls()];
        }
        if (p.use[plot] == TilePainter.ROAD) {
            String kind = p.road[plot] == BuildingVisual.HIGHWAY ? "Highway" : p.road[plot] >= BuildingVisual.PAVED ? "Paved road" : "Gravel road";
            return p.bridge[plot] ? kind + ", a bridge" : kind;
        }
        return null;
    }
}
