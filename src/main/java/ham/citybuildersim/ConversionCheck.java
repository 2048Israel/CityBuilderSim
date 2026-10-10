package ham.citybuildersim;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Saved cities put on the block grid (0.7.66, batch M2): the five saves the design was measured on, each converted - a format-31 save's lanes snapped to blocks with no field changing hands and its part fields' sites on the right side, an older save's figure drawn to the plot - with the books the plots drawn and the sites and tonnes the save's to the bit.
 *
 * WHY THIS EXISTS (the project's spec-grid.md 2.6 and 3, batch M2). From batch
 * M3 every city saved before the grid is put on it once, at load, by
 * GridConversion; a conversion that handed the city a field it never bought,
 * dropped one it did, or left its books saying other than its map would be a
 * player's city quietly changed. So the conversion is held here first, pure,
 * on copies of the five saves (the resource conversion-saves.json, copied key
 * for key from them): Jerus's live city and his slot 3, written by 0.7.63,
 * which held fields site by site; his older city, 0.7.49; and the research
 * cities city600 and city2400, 0.7.38.
 *
 * What it has to prove:
 *   1. LegacyLand reads a save's centre, lanes and purchases as CityLand
 *      did until 0.7.66 (its figures frozen in the fixture by that build's
 *      lane code, cityLand): the same totals to the bit and the same plots
 *      owned, and its count of a block's owned plots the plot-by-plot count;
 *   2. a format-31 save that held fields site by site (Jerus's live city),
 *      snapped one level finer than its offers: its sites and tonnes the
 *      save's to the bit, and the world's fields on the converted ground
 *      recount them to the bit; no field held whole changes hands; every site
 *      of a field held in part on the right side of the ground; a block
 *      decided by the save's half unless a field's point lay in it, and split
 *      only where points of both kinds did; what it took out (E) as saved;
 *      the books the drawn plots, counted again plot by plot; the ground's
 *      rectangles rebuilding it node for node; offers listed round it apart,
 *      each against it, whole blocks;
 *   3. his slot 3, the same land at month 212, converts to the same ground,
 *      books and fields, with its own E;
 *   4. a city whose fields go whole (written by 0.7.66), founded on the
 *      default world and bought evenly, its bands measured as that land
 *      office measured them, then one lane pushed out exactly to a field's
 *      centre so the field decides its block - frozen in the fixture
 *      (wholeFieldCity) before batch M3 took the lane-selling code out: the
 *      same as 2, with no field held in part;
 *   5. the three older saves (format 30): rings of blocks of the city's level
 *      round J1b's site, the one block split down to the plot, holding the
 *      save's dry ground to within a plot and never less; the iron the
 *      save's, at least its mines, its tonnes exact, nothing taken out; a
 *      legacy iron field on its dry ground a kilometre or more from the site
 *      where the world laid no iron on it; every other resource the world's
 *      fields centred on it; the books, the rectangles and the offers as in 2;
 *   6. the game itself converting a format-31 save at load (0.7.67): Jerus's
 *      live land written into a fresh city's save as 0.7.63 wrote it, loaded
 *      to exactly the conversion's ground, books, sites and part fields, its
 *      figure its dry plots, E and its purchase history as saved, no money
 *      moving, the world's totals kept; every field near it held once, whole
 *      or by sites, the offers' contents exactly the fields on their free
 *      plots; saved again as format 32 and loaded, the same, and both play
 *      their next month alike.
 *
 * Since 0.7.99 (batch W1, fewer and bigger deposits) "the world's fields" in
 * 2 to 5 are the old world's (World.legacyFieldsInCell()), which every one
 * of these saves was written on and which its converted ground keeps
 * (CityLand.oldWorldHoldings()); in 6 the fields as the loaded city sees
 * them - the old world's on its ground, the world's on the offers' free plots.
 */
public class ConversionCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-66s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The fixture: the five saves' land, copied key for key (src/main/resources). */
    static final String FIXTURE = "conversion-saves.json";

    /** Purchases the whole-field city of section 4 is bought to: 133, as many as Jerus's live city had made. */
    static final int WHOLE_CITY_PURCHASES = 133;

    /** One save's land as the fixture holds it. */
    record Save(String name, int format, String version, int month, long seed, double landOwned, int mines,
                double[] centre, double[] lanes, double[][] purchases, double[] depletion, int ironSites, double ironTonnes) { }

    public static void main(String[] args) throws Exception {
        List<Save> saves = load();
        check("fixture: the five saves are on the classpath", saves.size() == 5);
        List<Save> lanes = new ArrayList<>(), older = new ArrayList<>();
        for (Save s : saves) (s.format() > LandConversion.LAST_FORMAT_BEFORE ? lanes : older).add(s);
        legacyIsCityLand(lanes);
        GridConversion.Result live = null;
        for (Save s : lanes) {
            System.out.println("\n--- " + (live == null ? "2" : "3") + ". " + s.name() + " (" + s.version() + ", month " + s.month() + "), snapped ---");
            LegacyLand old = LegacyLand.restore(s.seed(), s.centre(), s.lanes(), s.purchases());
            GridConversion.Result r = snapped(s.name(), old, s.version(), s.depletion(), s.mines(), true);
            if (live == null) {
                live = r;
                continue;
            }
            check("its land is the autosave's: the same ground, node for node", r.grid().sameAs(live.grid()));
            check("...the same books", java.util.Arrays.equals(r.km2, live.km2));
            check("...the same sites and amounts", java.util.Arrays.equals(r.sites, live.sites) && java.util.Arrays.equals(r.amounts, live.amounts));
            check("...the same fields held in part", partsOf(r).equals(partsOf(live)));
            check("...and its own E: the depletion its save has", java.util.Arrays.equals(r.extracted, java.util.Arrays.copyOf(s.depletion(), LegacyLand.KINDS))
                    && !java.util.Arrays.equals(r.extracted, live.extracted));
        }
        wholeFields();
        System.out.println("\n--- 5. the older saves: a centre of blocks to the plot ---");
        boolean legacySeen = false;
        for (Save s : older) legacySeen |= centred(s);
        check("fixture: one of them has no iron the world laid on its ground (a legacy field)", legacySeen);
        if (!lanes.isEmpty()) atLoad(lanes.get(0));
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       THE FIXTURE
       ===================================================================== */

    static List<Save> load() throws Exception {
        List<Save> out = new ArrayList<>();
        String text;
        try (InputStream in = ConversionCheck.class.getResourceAsStream("/" + FIXTURE)) {
            if (in == null) return out;
            text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        com.google.gson.JsonArray all = com.google.gson.JsonParser.parseString(text).getAsJsonObject().getAsJsonArray("saves");
        Map<String, com.google.gson.JsonObject> byName = new HashMap<>();
        for (com.google.gson.JsonElement e : all) {
            com.google.gson.JsonObject o = e.getAsJsonObject();
            byName.put(o.get("name").getAsString(), o);
        }
        for (com.google.gson.JsonElement e : all) {
            com.google.gson.JsonObject o = e.getAsJsonObject();
            com.google.gson.JsonObject land = o.has("landAs") ? byName.get(o.get("landAs").getAsString()) : o;
            String city = o.has("cityName") && !o.get("cityName").isJsonNull() ? o.get("cityName").getAsString() : null;
            double cash = o.get("foundingCash").getAsDouble(), owned = o.get("landOwned").getAsDouble();
            int month = o.get("month").getAsInt();
            long seed = o.has("worldSeed") ? o.get("worldSeed").getAsLong() : Founding.derivedWorldSeed(city, cash, owned, month);
            out.add(new Save(o.get("name").getAsString(), o.get("saveFormat").getAsInt(), o.get("gameVersion").getAsString(), month, seed,
                    owned, o.get("minesCommitted").getAsInt(), doubles(land, "landCentre"), doubles(land, "landLanes"), rows(land, "landPurchases"),
                    doubles(o, "depletion"), o.has("ironDeposits") ? o.get("ironDeposits").getAsInt() : 0,
                    o.has("ironReserveTonnes") ? o.get("ironReserveTonnes").getAsDouble() : 0));
        }
        return out;
    }

    static double[] doubles(com.google.gson.JsonObject o, String key) {
        if (o == null || !o.has(key)) return null;
        com.google.gson.JsonArray a = o.getAsJsonArray(key);
        double[] out = new double[a.size()];
        for (int i = 0; i < out.length; i++) out[i] = a.get(i).getAsDouble();
        return out;
    }

    static double[][] rows(com.google.gson.JsonObject o, String key) {
        if (o == null || !o.has(key)) return null;
        com.google.gson.JsonArray a = o.getAsJsonArray(key);
        double[][] out = new double[a.size()][];
        for (int i = 0; i < out.length; i++) {
            com.google.gson.JsonArray r = a.get(i).getAsJsonArray();
            out[i] = new double[r.size()];
            for (int j = 0; j < r.size(); j++) out[i][j] = r.get(j).getAsDouble();
        }
        return out;
    }

    /* =====================================================================
       1. LEGACYLAND IS CITYLAND'S LANES
       ===================================================================== */

    static void legacyIsCityLand(List<Save> lanes) throws Exception {
        System.out.println("--- 1. LegacyLand reads a save's lanes as CityLand did ---");
        com.google.gson.JsonObject frozen = fixture().getAsJsonObject("cityLand");
        check("the centre's record and a purchase's are the format-31 widths, 25 and 28",
                LegacyLand.CENTRE_FIELDS == 25 && LegacyLand.PURCHASE_FIELDS == 28);
        for (Save s : lanes) {
            com.google.gson.JsonObject c = frozen == null ? null : frozen.getAsJsonObject(s.name());
            LegacyLand l = LegacyLand.restore(s.seed(), s.centre(), s.lanes(), s.purchases());
            boolean totals = c != null && l != null;
            for (int a = 0; totals && a < LegacyLand.AREAS; a++) totals = bits(l.totalKm2(a)) == hex(c, "totalKm2Bits", a);
            for (Resource r : Resource.values()) {
                if (!totals) break;
                totals = l.totalSites(r) == c.getAsJsonArray("totalSites").get(r.ordinal()).getAsLong()
                        && bits(l.totalAmount(r)) == hex(c, "totalAmountBits", r.ordinal());
            }
            boolean frontiers = totals && l.centreHalf() == c.get("centreHalf").getAsDouble()
                    && l.purchases().size() == c.get("purchases").getAsInt();
            for (int k = 0; frontiers && k < LegacyLand.SIDES * LegacyLand.LANES; k++) {
                frontiers = l.frontier(k / LegacyLand.LANES, k % LegacyLand.LANES) == c.getAsJsonArray("frontiers").get(k).getAsDouble();
            }
            System.out.printf(Locale.ROOT, "   %s: %d purchases, %.6f km2 (%.6f dry), iron %d sites %,.0f t%n", s.name(), l.purchases().size(),
                    l.totalKm2(CityLand.TOTAL), l.totalKm2(CityLand.DRY), l.totalSites(Resource.IRON), l.totalAmount(Resource.IRON));
            check("(" + s.name() + ") its totals CityLand's, to the bit (as 0.7.66 read them)", totals);
            check("...its centre, frontiers and purchases CityLand's", frontiers);
            if (!totals) continue;
            long reach = c.get("reach").getAsLong(), plots = 0, owned = 0, h = 0xcbf29ce484222325L;
            for (long y = l.siteY() - reach; y <= l.siteY() + reach; y++) {
                for (long x = l.siteX() - reach; x <= l.siteX() + reach; x++) {
                    plots++;
                    boolean o = l.ownsPlot(x, y);
                    if (o) owned++;
                    h ^= o ? 1 : 0;
                    h *= 0x100000001b3L;
                }
            }
            check("...the same plots owned, every one within its reach (" + plots + ")", plots == c.get("plots").getAsLong()
                    && owned == c.get("owned").getAsLong() && Long.toHexString(h).equals(c.get("ownedHash").getAsString()) && owned > 0);
            long blocks = 0, counted = 0;
            for (int level = 0; level <= 5; level++) {
                long b = 1L << level;
                for (long by = Math.floorDiv(l.siteY() - reach, b); by <= Math.floorDiv(l.siteY() + reach, b); by++) {
                    for (long bx = Math.floorDiv(l.siteX() - reach, b); bx <= Math.floorDiv(l.siteX() + reach, b); bx++) {
                        long n = 0;
                        for (long y = by * b; y < by * b + b; y++) for (long x = bx * b; x < bx * b + b; x++) if (l.ownsPlot(x, y)) n++;
                        blocks++;
                        if (n == l.ownedPlots(bx * b, by * b, bx * b + b, by * b + b)) counted++;
                    }
                }
            }
            check("...and a block's owned plots its count plot by plot (" + blocks + " blocks)", counted == blocks);
        }
    }

    /** A frozen double's bits, as M3Freeze wrote them (hex). */
    static long hex(com.google.gson.JsonObject o, String key, int i) {
        return Long.parseUnsignedLong(o.getAsJsonArray(key).get(i).getAsString(), 16);
    }

    /** The fixture's whole JSON object. */
    static com.google.gson.JsonObject fixture() throws Exception {
        try (InputStream in = ConversionCheck.class.getResourceAsStream("/" + FIXTURE)) {
            if (in == null) return new com.google.gson.JsonObject();
            return com.google.gson.JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    static long bits(double v) { return Double.doubleToLongBits(v); }

    /* =====================================================================
       2 TO 4. A FORMAT-31 SAVE SNAPPED
       ===================================================================== */

    /** A field by its kind, cell and index. */
    static String fieldKey(Resource r, int cell, int index) { return r.ordinal() + ":" + cell + ":" + index; }

    static Set<String> partsOf(GridConversion.Result r) {
        Set<String> out = new HashSet<>();
        for (GridConversion.PartField p : r.parts()) out.add(fieldKey(p.kind(), p.cell(), p.index()) + java.util.Arrays.toString(p.owned()));
        return out;
    }

    /** Converts a format-31 save's land and checks it; `causes` asks that the save's fields decided at least one block. */
    static GridConversion.Result snapped(String name, LegacyLand old, String version, double[] depletion, int mines, boolean causes) {
        World w = World.of(old.seed());
        long t0 = System.nanoTime();
        GridConversion.Result r = GridConversion.fromLanes(old, version, depletion);
        double drawMs = (System.nanoTime() - t0) / 1e6;
        int level = r.level(), offersLevel = LandGrid.levelFor(old.totalKm2(CityLand.TOTAL) / World.KM2_PER_PLOT);
        long b = 1L << level, sx = old.siteX(), sy = old.siteY();
        System.out.printf(Locale.ROOT, "   %s: %d purchases, %.4f km2 (%.4f dry); snapped at level %d (%.0f m), its offers' level %d;"
                        + " fields site by site: %b%n", name, old.purchases().size(), old.totalKm2(CityLand.TOTAL), old.totalKm2(CityLand.DRY),
                level, b * World.PLOT_M, offersLevel, r.shared());
        check("snapped one level finer than its offers", level == offersLevel - 1);

        // The points that decide, as spec-grid star 11 states them: every field centred in the box, owned by the save or not,
        // or every site of one it held in part, and the save's legacy field.
        long reach = (long) Math.ceil(old.reach()) + b;
        long x0 = Math.floorDiv(sx - reach, b) * b, y0 = Math.floorDiv(sy - reach, b) * b;
        long x1 = (Math.floorDiv(sx + reach, b) + 1) * b, y1 = (Math.floorDiv(sy + reach, b) + 1) * b;
        Map<String, GridConversion.PartField> parts = new HashMap<>();
        for (GridConversion.PartField p : r.parts()) parts.put(fieldKey(p.kind(), p.cell(), p.index()), p);
        List<long[]> points = new ArrayList<>();
        long[] wholeSites = new long[LegacyLand.KINDS];
        double[] wholeAmounts = new double[LegacyLand.KINDS];
        int fields = 0, fieldsOwned = 0, handsSame = 0, partSites = 0, partOwn = 0, partRight = 0, partsFound = 0;
        boolean partsSound = true;
        for (Resource res : Resource.values()) {
            if (!res.inFields()) continue;
            double pad = Deposit.mostReach(res) + 1;
            for (int cell : CityLand.cellsUnder(x0 - pad, y0 - pad, x1 - 1 + pad, y1 - 1 + pad)) {
                // The old world's fields (0.7.99): the ground the save held, which it keeps.
                for (Deposit d : CityLand.legacyFields(w, cell, res)) {
                    GridConversion.PartField p = parts.get(fieldKey(res, cell, d.index()));
                    boolean[] mine = new boolean[d.sites()];
                    int in = 0;
                    for (int k = 0; k < d.sites(); k++) {
                        double[] at = d.siteAt(k);
                        mine[k] = r.shared() ? old.owns(d.x() + at[0] - sx, d.y() + at[1] - sy) : old.ownsPlot(d.x(), d.y());
                        if (mine[k]) in++;
                    }
                    boolean part = in > 0 && in < d.sites();
                    if (part != (p != null)) partsSound = false;
                    if (part) {
                        partsFound++;
                        int n = 0;
                        for (int k = 0; k < d.sites(); k++) {
                            if (mine[k] && (n >= p.owned().length || p.owned()[n++] != k)) partsSound = false;
                            long[] at = GridConversion.sitePlot(d, k);
                            boolean drawn = r.grid().owner(at[0], at[1]) == GridConversion.CONVERTED;
                            partSites++;
                            if (mine[k]) {
                                partOwn++;
                                wholeSites[res.ordinal()]++;
                                wholeAmounts[res.ordinal()] += d.siteAmount(k);
                            }
                            if (drawn == mine[k]) partRight++;
                            if (at[0] >= x0 && at[0] < x1 && at[1] >= y0 && at[1] < y1) points.add(new long[] { at[0], at[1], mine[k] ? 1 : 0 });
                        }
                        if (n != p.owned().length) partsSound = false;
                        continue;
                    }
                    boolean before = in == d.sites();
                    boolean after = r.grid().owner(d.x(), d.y()) == GridConversion.CONVERTED;
                    if (after) {
                        wholeSites[res.ordinal()] += d.sites();
                        wholeAmounts[res.ordinal()] += d.amount();
                    }
                    if (d.x() < x0 || d.x() >= x1 || d.y() < y0 || d.y() >= y1) continue;
                    fields++;
                    if (before) fieldsOwned++;
                    if (before == after) handsSame++;
                    points.add(new long[] { d.x(), d.y(), before ? 1 : 0 });
                }
            }
        }
        if (old.legacySites() > 0) points.add(new long[] { old.legacyX(), old.legacyY(), 1 });
        boolean bit = true, recount = old.legacySites() == 0;
        for (Resource res : Resource.values()) {
            if (!res.inFields()) continue;
            bit &= r.sites(res) == old.totalSites(res) && bits(r.amount(res)) == bits(old.totalAmount(res));
            recount &= wholeSites[res.ordinal()] == old.totalSites(res) && bits(wholeAmounts[res.ordinal()]) == bits(old.totalAmount(res));
        }
        System.out.printf(Locale.ROOT, "   fields centred near it: %d, the save's %d; held in part: %d (%d of %d sites its own); iron %d sites %,.0f t, %d mines%n",
                fields, fieldsOwned, partsFound, partOwn, partSites, r.sites(Resource.IRON), r.amount(Resource.IRON), mines);
        check("its sites and amounts of every resource the save's, to the bit", bit);
        check("...recounted from the world's fields on its ground, to the bit", recount);
        check("...at least as many iron sites as its mines", r.sites(Resource.IRON) >= mines);
        check("no field held whole changes hands (" + fields + " fields)", handsSame == fields && fields > 0);
        check("its fields held in part are the save's, each with its own sites", partsSound && partsFound == r.parts().size());
        check("...every site on the right side of the ground (" + partSites + " sites)", partRight == partSites);
        if (r.shared()) check("fixture: it holds fields in part", partsFound > 0);
        else check("...and it holds none in part, its fields whole", partsFound == 0);

        // Each block of the snap's level: the save's half decides it, unless a point lies in it.
        int blocks = 0, ruled = 0, byPoint = 0;
        Map<Long, int[]> kinds = new HashMap<>();
        for (long[] p : points) {
            int[] k = kinds.computeIfAbsent(Math.floorDiv(p[0], b) * World.SIDE + Math.floorDiv(p[1], b), z -> new int[2]);
            k[(int) p[2]]++;
        }
        for (long by = y0 / b; by < y1 / b; by++) {
            for (long bx = x0 / b; bx < x1 / b; bx++) {
                int cover = r.grid().cover(level, bx, by);
                long o = old.ownedPlots(bx * b, by * b, bx * b + b, by * b + b);
                int[] k = kinds.getOrDefault(bx * World.SIDE + by, new int[2]);
                boolean own = k[1] > 0, not = k[0] > 0, most = 2 * o >= b * b;
                boolean ok;
                if (own && not) ok = true;                          // split: checked below, plot by plot
                else if (own) ok = cover == LandGrid.ALL;
                else if (not) ok = cover == LandGrid.NONE;
                else ok = cover == (most && o > 0 ? LandGrid.ALL : LandGrid.NONE);
                if (cover == LandGrid.SOME && !(own && not)) ok = false;
                if ((own || not) && (cover == LandGrid.ALL) != most) byPoint++;
                blocks++;
                if (ok) ruled++;
            }
        }
        System.out.printf(Locale.ROOT, "   %d blocks of %.0f m: %d taken for a field the save owns, %d left out for another's, %d split; %d decided by a point%n",
                blocks, b * World.PLOT_M, r.takenForOwn(), r.leftOutForOther(), r.splits(), byPoint);
        check("each block the save's by its half, or by the field in it", ruled == blocks);
        check("...and no plot holds fields of both kinds", r.conflicts() == 0);
        if (causes) check("fixture: its fields decide some of its blocks", byPoint > 0 && r.takenForOwn() + r.leftOutForOther() + r.splits() > 0);
        double[] e = depletion == null ? new double[LegacyLand.KINDS] : java.util.Arrays.copyOf(depletion, LegacyLand.KINDS);
        check("what it took out (E) as saved", java.util.Arrays.equals(r.extracted, e));
        books(w, r, old.totalKm2(CityLand.TOTAL), old.totalKm2(CityLand.DRY), old.totalKm2(CityLand.FRESH), old.totalKm2(CityLand.SEA),
                old.totalKm2(CityLand.FOREST), drawMs);
        System.out.printf(Locale.ROOT, "   paid for its %d purchases: US$%.1fM listed, D$%.1fM here - kept on record; no money moves%n",
                old.purchases().size(), old.paidUsd() / 1000, old.paidLocal() / 1000);
        return r;
    }

    /**
     * What every conversion must hold, whatever the save: the books the
     * ground's plots counted again plot by plot, its forest's timber its
     * area's; its rectangles rebuilding the ground node for node; and offers
     * listed round it, apart, each against it, whole blocks of their level.
     */
    static void books(World w, GridConversion.Result r, double all, double dry, double fresh, double sea, double forest, double drawMs) {
        LandGrid g = r.grid();
        long[] cls = new long[5];
        byte[] tile = new byte[World.TILE * World.TILE];
        long lastTile = Long.MIN_VALUE;
        for (long ty = Math.floorDiv(g.minY(), World.TILE); ty <= Math.floorDiv(g.maxY() - 1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(g.minX(), World.TILE); tx <= Math.floorDiv(g.maxX() - 1, World.TILE); tx++) {
                boolean loaded = false;
                for (int j = 0; j < World.TILE; j++) {
                    for (int i = 0; i < World.TILE; i++) {
                        long x = tx * World.TILE + i, y = ty * World.TILE + j;
                        if (g.owner(x, y) != GridConversion.CONVERTED) continue;
                        if (!loaded) {
                            w.tileTerrain(tx, ty, tile);
                            loaded = true;
                        }
                        cls[tile[j * World.TILE + i]]++;
                    }
                }
            }
        }
        double[] drawn = GridConversion.km2Of(cls);
        boolean same = true;
        for (int a = 0; a < LegacyLand.AREAS; a++) same &= bits(drawn[a]) == bits(r.km2(a));
        long plots = cls[0] + cls[1] + cls[2] + cls[3] + cls[4];
        String[] names = { "all", "dry", "fresh", "sea", "forest" };
        double[] saved = { all, dry, fresh, sea, forest };
        for (int a = 0; a < LegacyLand.AREAS; a++) {
            System.out.printf(Locale.ROOT, "      %-6s saved %.4f km2, drawn %.4f (%+.4f km2, %+.2f%%)%n", names[a], saved[a], r.km2(a), r.km2(a) - saved[a],
                    saved[a] > 0 ? 100 * (r.km2(a) - saved[a]) / saved[a] : 0);
        }
        check("its books the plots drawn, counted again plot by plot (" + plots + ")", same && plots == g.ownedPlots());
        check("...its timber its forest's area's", r.amount(Resource.FOREST) == Math.rint(r.km2(CityLand.FOREST) * World.FOREST_M3_PER_KM2));
        check("...and its rectangles rebuild the ground node for node", LandGrid.replay(r.fills()).sameAs(g));
        long t0 = System.nanoTime();
        GridOffers offers = new GridOffers(g, r.siteX(), r.siteY());
        offers.listMissing();
        double listMs = (System.nanoTime() - t0) / 1e6;
        List<GridOffers.Rect> standing = new ArrayList<>();
        for (int s = 0; s < GridOffers.SIDES; s++) for (int j = 0; j < GridOffers.PLACES; j++) if (offers.offer(s, j) != null) standing.add(offers.offer(s, j));
        boolean apart = true, against = true, whole = true;
        for (int i = 0; i < standing.size(); i++) {
            GridOffers.Rect a = standing.get(i);
            for (int k = i + 1; k < standing.size(); k++) if (a.meets(standing.get(k))) apart = false;
            if (!GridCheck.touches(g, a)) against = false;
            long ob = 1L << a.level();
            if (a.x0() % ob != 0 || a.y0() % ob != 0 || a.x1() % ob != 0 || a.y1() % ob != 0) whole = false;
        }
        long[] census = g.census();
        System.out.printf(Locale.ROOT, "   on the grid: %d plots, %d nodes, %d leaves (%d rectangles); %d offers at level %d listed round it; %.0f ms + %.0f ms%n",
                g.ownedPlots(), census[0], census[1], r.fills().size(), standing.size(), g.level(), drawMs, listMs);
        check("its offers listed round it stand apart, each against it, whole blocks", apart && against && whole && !standing.isEmpty());
    }

    /* =====================================================================
       4. A CITY WHOSE FIELDS GO WHOLE
       ===================================================================== */

    static void wholeFields() throws Exception {
        System.out.println("\n--- 4. a city whose fields go whole (0.7.66, frozen), bought evenly ---");
        com.google.gson.JsonObject w = fixture().getAsJsonObject("wholeFieldCity");
        check("fixture: the whole-field city 0.7.66's lane code built is on the classpath", w != null);
        if (w == null) return;
        LegacyLand old = LegacyLand.restore(w.get("worldSeed").getAsLong(), doubles(w, "landCentre"), doubles(w, "landLanes"),
                rows(w, "landPurchases"));
        String version = w.get("gameVersion").getAsString();
        check("fixture: its land reads, " + WHOLE_CITY_PURCHASES + " purchases bought evenly round the forty lanes and one pushed",
                old != null && old.purchases().size() == WHOLE_CITY_PURCHASES + 1);
        if (old == null) return;
        System.out.println("   " + w.get("pushed").getAsString());
        int owned = 0;
        for (Resource r : Resource.values()) if (r.inFields()) owned += (int) Math.min(Integer.MAX_VALUE, old.totalSites(r));
        check("fixture: it owns fields, whole", owned > 0 && !GridConversion.sharesFields(version));
        snapped("whole-field city", old, version, null, 0, true);
    }

    /* =====================================================================
       6. THE GAME CONVERTS A SAVE AT LOAD, ONCE (0.7.67, batch M3)

       A format-31 save loaded by this build: a fresh city's save with
       Jerus's live land written into it as 0.7.63 wrote it (its centre,
       lanes, purchases, depletion, build and world) - the load snaps it
       (LandConversion.convertLanes()) to exactly what GridConversion gives
       the save's land, its figure its dry plots, E and its purchase history
       as saved, no money moving, the world's totals kept to the tonne, and
       every field near it held once, whole, or by sites - none counted by
       both the city and an offer. Saved again (format 32) and loaded, it is
       the same land, offers and figure, and both play their next month
       alike.
       ===================================================================== */

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static void atLoad(Save s) throws Exception {
        System.out.println("\n--- 6. the game puts " + s.name() + " on the grid at load, once ---");
        GameFiles files = GameFiles.scratch("conversioncheck-load");
        Game fresh = new Game(files);
        quietly(() -> { fresh.newGame(); fresh.saveGame(4, "to convert"); });
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(files.saveFile(4))).getAsJsonObject();
        for (String key : new String[] { "worldSeaTheta", "worldTotals", "landCentre", "landCentreRects", "landHoldings", "landPartFields",
                "landConverted", "landOffers", "nextOfferId", "depletion", "mapStamp" }) json.remove(key);
        com.google.gson.Gson gson = new com.google.gson.Gson();
        json.addProperty("saveFormat", s.format());
        json.addProperty("gameVersion", s.version());
        json.addProperty("worldSeed", s.seed());
        json.addProperty("landOwned", s.landOwned());
        json.addProperty("landBlocksPurchased", s.purchases().length);
        json.add("landCentre", gson.toJsonTree(s.centre()));
        json.add("landLanes", gson.toJsonTree(s.lanes()));
        json.add("landPurchases", gson.toJsonTree(s.purchases()));
        json.add("depletion", gson.toJsonTree(s.depletion()));
        java.nio.file.Files.writeString(files.saveFile(4), json.toString());
        double cash = fresh.getCash();

        Game[] back = new Game[1];
        long t0 = System.nanoTime();
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        double ms = (System.nanoTime() - t0) / 1e6;
        Game g = back[0];
        check("the format-31 save loads", g.getLoadFailure() == null);
        if (g.getLoadFailure() != null) return;
        LandManager lm = g.getLandManager();
        CityLand land = g.getCityLand();
        LegacyLand old = LegacyLand.restore(s.seed(), s.centre(), s.lanes(), s.purchases());
        GridConversion.Result r = GridConversion.fromLanes(old, s.version(), s.depletion());
        System.out.printf(Locale.ROOT, "   loaded and converted in %.0f ms: %.4f km2 (%.4f dry) at level %d, %d blocks, %d fields held in part;"
                        + " %d offers, %d places waiting%n", ms, land.totalKm2(CityLand.TOTAL), land.totalKm2(CityLand.DRY), land.level(),
                land.centreRects().size(), land.partFields().size(), lm.getListing().size(), LandMarket.OFFERS - lm.getListing().size());
        boolean same = land.grid().sameAs(r.grid()) && land.purchases().isEmpty();
        for (int a = 0; same && a < LegacyLand.AREAS; a++) same = bits(land.totalKm2(a)) == bits(r.km2(a));
        for (Resource res : Resource.values()) same &= land.totalSites(res) == r.sites(res) && bits(land.totalAmount(res)) == bits(r.amount(res));
        check("its land is the conversion's: the ground node for node, the books and every site and tonne to the bit", same);
        check("...its fields held in part the save's", partsOf(r).equals(partsOfLand(land)));
        check("...its figure its dry plots, the books following the map", lm.getOwnedSqFt() == lm.getLandDrySqFt()
                && lm.getOwnedSqFt() == LandManager.sqFt(r.km2(CityLand.DRY)));
        check("...what it took out (E) as saved", java.util.Arrays.equals(lm.getDepletionState(), java.util.Arrays.copyOf(s.depletion(), LegacyLand.KINDS)));
        check("...its purchases kept as history, record for record, and its count", java.util.Arrays.deepEquals(land.convertedHistory(), s.purchases())
                && lm.getBlocksPurchased() == s.purchases().length);
        check("no money moves: the treasury's cash as saved", g.getCash() == cash);
        double[] world = World.of(s.seed()).totals();
        boolean kept = java.util.Arrays.equals(lm.getWorldTotalsState(), world);
        for (Resource res : Resource.values()) {
            double sum = lm.getUnowned(res) + lm.getRemaining(res) + lm.getExtracted(res);
            kept &= Math.abs(sum - world[res.ordinal()]) <= 1e-15 * Math.abs(world[res.ordinal()]) || lm.getRemaining(res) == 0 && lm.getOwnedAmount(res) < lm.getExtracted(res);
        }
        check("the world's totals (W) kept: unowned, remaining and extracted add up to them, every resource", kept);
        heldOnce(g);
        quietly(() -> g.saveGame(5, "converted"));
        Game[] again = new Game[1];
        quietly(() -> { again[0] = new Game(files); again[0].loadGameSave(5); });
        Game h = again[0];
        boolean reloaded = h.getLoadFailure() == null && h.getCityLand().same(land)
                && java.util.Arrays.deepEquals(h.getLandManager().getMarket().getOffersState(), lm.getMarket().getOffersState())
                && h.getLandManager().getOwnedSqFt() == lm.getOwnedSqFt()
                && java.util.Arrays.equals(h.getLandManager().getDepletionState(), lm.getDepletionState());
        check("saved again (format " + GameVersion.SAVE_FORMAT + ") and loaded: the same land, offers, figure and E, converted once", reloaded);
        quietly(g::toggleNextMonth);
        quietly(h::toggleNextMonth);
        check("...and both play their next month alike: land, offers and cash", h.getCityLand().same(g.getCityLand())
                && java.util.Arrays.deepEquals(h.getLandManager().getMarket().getOffersState(), g.getLandManager().getMarket().getOffersState())
                && h.getCash() == g.getCash());
    }

    static Set<String> partsOfLand(CityLand land) {
        Set<String> out = new HashSet<>();
        for (GridConversion.PartField p : land.partFields()) out.add(fieldKey(p.kind(), p.cell(), p.index()) + java.util.Arrays.toString(p.owned()));
        return out;
    }

    /**
     * Every field near a city held at most once (spec-grid 2.6): a whole field
     * by the holding whose ground holds its centre plot or by the offer whose
     * free plots do, never both; a field held in part, each site the same; and
     * the offers' sites and amounts exactly the fields so given them.
     */
    static void heldOnce(Game g) {
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        World world = World.of(land.seed());
        LandGrid gr = land.grid();
        double pad = 0;
        for (Resource r : Resource.values()) if (r.inFields()) pad = Math.max(pad, Deposit.mostReach(r));
        double x0 = gr.minX() - 64 - pad, y0 = gr.minY() - 64 - pad, x1 = gr.maxX() + 64 + pad, y1 = gr.maxY() + 64 + pad;
        long[] offerSites = new long[LegacyLand.KINDS];
        double[] offerAmounts = new double[LegacyLand.KINDS];
        int fields = 0, parts = 0, twice = 0;
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            for (int cell : CityLand.cellsUnder(x0, y0, x1, y1)) {
                // The fields as the city sees them (0.7.99): the old world's on its converted ground, the world's elsewhere;
                // then the old world's it holds in part.
                List<Deposit> seen = new ArrayList<>();
                for (Deposit d : land.fieldsIn(cell, r)) if (!land.isPart(d)) seen.add(d);
                for (Deposit d : CityLand.legacyFields(world, cell, r)) if (land.isPart(d)) seen.add(d);
                for (Deposit d : seen) {
                    if (!land.isPart(d)) {
                        int holders = land.ownsPlot(d.x(), d.y()) ? 1 : 0;
                        for (LandParcel p : market.getListing()) {
                            if (p.contains(d.x(), d.y()) && !land.ownsPlot(d.x(), d.y())) {
                                holders++;
                                offerSites[r.ordinal()] += d.sites();
                                offerAmounts[r.ordinal()] += d.amount();
                            }
                        }
                        fields++;
                        if (holders > 1) twice++;
                        continue;
                    }
                    parts++;
                    for (int k = 0; k < d.sites(); k++) {
                        long[] at = GridConversion.sitePlot(d, k);
                        int holders = land.siteHolding(d, k) >= 0 ? 1 : 0;
                        for (LandParcel p : market.getListing()) {
                            if (p.contains(at[0], at[1]) && !land.ownsPlot(at[0], at[1])) {
                                holders++;
                                offerSites[r.ordinal()]++;
                                offerAmounts[r.ordinal()] += d.siteAmount(k);
                            }
                        }
                        if (holders > 1) twice++;
                    }
                }
            }
        }
        boolean listed = true;
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            long sites = 0;
            double amount = 0;
            for (LandParcel p : market.getListing()) {
                sites += p.getSites(r);
                amount += p.getAmount(r);
            }
            listed &= sites == offerSites[r.ordinal()] && amount == offerAmounts[r.ordinal()];
        }
        System.out.printf(Locale.ROOT, "   %d whole fields and %d held in part near it; held twice: %d%n", fields, parts, twice);
        check("every field held once at most - whole by its centre plot, or a site at a time - never by the city and an offer both",
                twice == 0 && fields > 0);
        check("...and the offers' sites and tonnes exactly the fields and sites on their free plots", listed);
    }

    /* =====================================================================
       5. AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT
       ===================================================================== */

    /** J1b's centre for an older save, as 0.7.66 drew it (frozen in the fixture): all, dry, fresh, sea and forest km2 - what the books are printed against. */
    static double[] j1bKm2(String name) {
        double[] out = new double[LegacyLand.AREAS];
        try {
            com.google.gson.JsonObject j = fixture().getAsJsonObject("j1bKm2");
            if (j != null && j.has(name)) for (int a = 0; a < out.length; a++) out[a] = j.getAsJsonArray(name).get(a).getAsDouble();
        } catch (Exception e) {
            // printed as zeros
        }
        return out;
    }

    /** Converts a format-30 save and checks it; true when the world laid no iron on its ground, so it stands a legacy field. */
    static boolean centred(Save s) {
        World w = World.of(s.seed());
        double dry = LandManager.km2(s.landOwned());
        long[] site = LandConversion.site(w, dry);
        long t0 = System.nanoTime();
        GridConversion.Result r = GridConversion.fromFigure(w, site[0], site[1], dry, s.ironSites(), s.ironTonnes(), s.mines());
        double drawMs = (System.nanoTime() - t0) / 1e6;
        double[] j1b = j1bKm2(s.name());
        System.out.printf(Locale.ROOT, "   %s (%s, month %d): world %d, site (%d, %d); %.4f km2 dry; centre of blocks of level %d (%.0f m)%n",
                s.name(), s.version(), s.month(), s.seed(), site[0], site[1], dry, r.level(), (1L << r.level()) * World.PLOT_M);
        long target = (long) Math.ceil(dry / World.KM2_PER_PLOT - GridConversion.WHOLE_PLOT);
        long[] cls = GridConversion.drawnClasses(w, r.grid());
        long dryPlots = cls[World.GRASS] + cls[World.FOREST] + cls[World.SAND];
        check("its dry plots the save's dry ground, whole plots (" + target + ")", dryPlots == target);
        check("...so its dry ground the save's to within a plot, and never less",
                r.km2(CityLand.DRY) < dry + World.KM2_PER_PLOT && r.km2(CityLand.DRY) >= dry - GridConversion.WHOLE_PLOT * World.KM2_PER_PLOT);
        int level = r.level();
        check("its blocks of the city's level", level == LandGrid.levelFor(dry / World.KM2_PER_PLOT));
        long b = 1L << level;
        long[] split = { Long.MIN_VALUE, Long.MIN_VALUE };
        boolean[] one = { true };
        int[] fine = { 0 };
        r.grid().leaves((lv, x, y, h) -> {
            if (lv >= level) return;
            fine[0]++;
            long bx = Math.floorDiv(x, b), by = Math.floorDiv(y, b);
            if (split[0] == Long.MIN_VALUE) { split[0] = bx; split[1] = by; }
            else if (split[0] != bx || split[1] != by) one[0] = false;
        });
        check("...the last split down to the plot, in one block (" + fine[0] + " finer pieces)", one[0] && fine[0] <= 3 * level + 1);
        int iron = Math.max(s.ironSites(), s.mines());
        check("its iron sites the save's, or its mines where they are more", r.sites(Resource.IRON) == iron);
        check("...its tonnes the save's, exactly", r.amount(Resource.IRON) == s.ironTonnes());
        boolean none = true;
        for (Resource res : Resource.values()) none &= r.extracted(res) == 0;
        check("...and nothing taken out yet", none);
        // The world's fields centred on its ground: the other resources' figures, and whether any is iron.
        long[] sites = new long[LegacyLand.KINDS];
        double[] amounts = new double[LegacyLand.KINDS];
        LandGrid g = r.grid();
        for (Resource res : Resource.values()) {
            if (!res.inFields()) continue;
            for (int cell : CityLand.cellsUnder(g.minX(), g.minY(), g.maxX() - 1, g.maxY() - 1)) {
                // The old world's fields (0.7.99): an older save's centre is drawn on them.
                for (Deposit d : CityLand.legacyFields(w, cell, res)) {
                    if (g.owner(d.x(), d.y()) != GridConversion.CONVERTED) continue;
                    sites[res.ordinal()] += d.sites();
                    amounts[res.ordinal()] += d.amount();
                }
            }
        }
        boolean others = true;
        for (Resource res : Resource.values()) {
            if (!res.inFields() || res == Resource.IRON) continue;
            others &= r.sites(res) == sites[res.ordinal()] && r.amount(res) == amounts[res.ordinal()];
        }
        check("every other resource the world's fields centred on it, whole", others);
        boolean legacy = sites[Resource.IRON.ordinal()] == 0 && iron > 0;
        if (legacy) {
            double near = LandConversion.LEGACY_FIELD_KM * 1000 / World.PLOT_M;
            double lx = r.legacyX() - site[0], ly = r.legacyY() - site[1];
            byte c = w.terrainAt(r.legacyX(), r.legacyY());
            System.out.printf(Locale.ROOT, "   no iron field of the world's on it: a legacy field of %d sites at (%d, %d), %.1f plots from the site%n",
                    r.legacySites(), r.legacyX(), r.legacyY(), Math.sqrt(lx * lx + ly * ly));
            check("...a legacy iron field of its sites, as the world laid none on it", r.legacySites() == iron);
            check("...on its own dry ground, a kilometre or more from the site",
                    g.owner(r.legacyX(), r.legacyY()) == GridConversion.CONVERTED && c != World.SALT && c != World.FRESH
                            && Math.sqrt(lx * lx + ly * ly) >= near - 1);
        } else {
            check("...and no legacy field, the world's iron being on it", r.legacyX() == -1 && r.legacySites() == 0);
        }
        books(w, r, j1b[CityLand.TOTAL], j1b[CityLand.DRY], j1b[CityLand.FRESH], j1b[CityLand.SEA], j1b[CityLand.FOREST], drawMs);
        return legacy;
    }
}
