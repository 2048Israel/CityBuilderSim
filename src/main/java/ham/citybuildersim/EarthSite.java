package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the seed picks, and the founding card - the site the snap finds, the ground within 5 km measured on the world's own plots, the province's deposit multipliers, the nearest iron, and why a spot is refused or warned of.
 *
 * WHY THIS EXISTS (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8;
 * Jerus's answers to the study, 2026-10-10, "all rec"). E1 to E5 built the
 * real Earth under the game - its data, its ground, its geology and its map -
 * and only harnesses could found a city on it. The founding screen offers it
 * now, and a player choosing a spot from a globe needs to know what is there
 * before the city is founded on it for good: the screen draws, and every
 * figure it shows is worked out here, on the world the city would stand on.
 *
 * WHAT JERUS DECIDED, and what this holds to:
 *   - Earth for new cities, the default where the data is here; the
 *     generated world stays as an option (gameData(): the game runs without
 *     the file, and says where it goes - dataWords()).
 *   - A spot in the sea or on ice is refused; a desert, or no fresh water
 *     within 5 km, is warned of (Card.refusal(), Card.warnings()).
 *   - No iron field is promised: the card names the nearest (Card.ironKm()).
 *   - Place names for searching only (search()), and no borders: a place is
 *     its name and its latitude and longitude, never a country.
 *
 * THE CARD (measure()) is the city's own world, World.of(key): the site is
 * its snap (EarthTerrain.siteNear(), the founding's test 1 within 5 km of
 * the spot), the ground within CARD_PLOTS of the site counted plot by plot
 * as the land office will sell it, the province the geology under the site
 * (E3's multipliers, w x Z, oil's with its petroleum), and the nearest iron
 * field's centre among the world's own fields. Nothing here is a model
 * change: the city founded is the one the card describes.
 *
 * RANDOM SPOT (randomPlace()): the seed picks among the data's places that
 * pass the site test - a site within the snap and not on ice - trying them in
 * an order its hash gives, so the same seed picks the same place.
 *
 * EarthFoundingCheck holds it, on the fixture's windows and on the full file.
 */
public final class EarthSite {

    private EarthSite() { }

    /** The card's reach, in plots: EarthTerrain.SNAP_PLOTS, 167 (5 km, spec-earth 2) - the snap's and the ground's. */
    public static final int CARD_PLOTS = EarthTerrain.SNAP_PLOTS;

    /** Places a search offers at most: 8, as many as the list under the field shows. */
    public static final int SEARCH_HITS = 8;

    /** Places Random spot tries, in its seed's order, before it gives up: 64 (the data's 7,342 are towns and cities, nearly all on dry ground). */
    public static final int RANDOM_TRIES = 64;

    /** How far the nearest iron is looked for, in world cells round the site's: 16 (983 km); past it the card says there is none that near. */
    public static final int IRON_RINGS = 16;

    /** The stream Random spot draws its order from. */
    private static final long RANDOM_SALT = 0x5B07A11DL;

    /* =====================================================================
       THE GAME'S EARTH: is the file here?
       ===================================================================== */

    /** What the game found when it looked for the Earth's data: the reader, or null and why not, in the player's words. */
    public record Data(EarthData reader, String words) {
        /** Whether Earth can be offered. */
        public boolean here() { return reader != null; }
    }

    private static Data looked;
    private static String lookedAt;

    /**
     * The game's Earth (EarthData.EARTH_1): the file EarthData.locate() finds
     * (-Dcbs.earth, or EarthData.DATA_FILE in the folder the game runs from)
     * when its identity is the one this build reads, or a reader a harness
     * made available with EarthTerrain.use(). Looked for again when the file
     * found, its size or its time changes, so a file put in place while the
     * game runs is seen the next time the screen opens.
     */
    public static synchronized Data gameData() {
        Path p = EarthData.locate();
        String at;
        try {
            at = p == null ? "none" : p.toAbsolutePath() + "|" + (Files.isRegularFile(p) ? Files.size(p) + "|" + Files.getLastModifiedTime(p).toMillis() : "missing");
        } catch (java.io.IOException e) {
            at = String.valueOf(p);
        }
        if (looked != null && at.equals(lookedAt)) return looked;
        lookedAt = at;
        try {
            looked = new Data(EarthTerrain.source(EarthData.EARTH_1), null);
        } catch (RuntimeException e) {
            looked = new Data(null, dataWords(p));
        }
        return looked;
    }

    /** Why the game's Earth is not here, in the player's words: where the file goes, or that the one there is not this build's. */
    static String dataWords(Path found) {
        String where = EarthData.DATA_FILE;
        if (found == null || !Files.isRegularFile(found)) {
            return "The real Earth needs its data, earth-1.bin (about 53 MB), which is not here. Put it in a folder named data"
                    + " beside the game, as " + where + ", and open this page again.";
        }
        return "The file at " + found + " is not the Earth's data this build reads (" + shortId(EarthData.EARTH_1)
                + "). Put the right earth-1.bin there and open this page again.";
    }

    /** An identity as a person reads it: "earth-1, b754a28f". */
    public static String shortId(String identity) {
        if (identity == null) return "none";
        int colon = identity.indexOf(':');
        String name = colon < 0 ? identity : identity.substring(0, colon);
        String hash = colon < 0 ? "" : identity.substring(colon + 1);
        return hash.isEmpty() ? name : name + ", " + hash.substring(0, Math.min(8, hash.length()));
    }

    /** Whether the data of an identity can be read here: a reader EarthTerrain.use() made available, or the file found when it is that one. */
    public static boolean available(String identity) {
        if (identity == null || identity.isBlank()) return false;
        try {
            EarthTerrain.source(identity);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Why a city saved on the real Earth cannot be loaded here, or null when
     * it can (spec-earth 6: the data is frozen per version, and a save names
     * its own): its data is not this build's, or this build's is not here.
     * Game's load refuses it with these words, nothing of it applied.
     */
    public static String loadProblem(String identity, Double lat, Double lon) {
        if (identity == null || identity.isBlank() || lat == null || lon == null || !(lat >= -90 && lat <= 90) || !Double.isFinite(lon)) {
            return "This city stands on the real Earth, and its save does not say where.";
        }
        if (available(identity)) return null;
        if (identity.equals(EarthData.EARTH_1)) {
            return "This city stands on the real Earth, and the Earth's data is not here: put earth-1.bin in a folder named data"
                    + " beside the game, as " + EarthData.DATA_FILE + ".";
        }
        return "This city stands on the real Earth as other data drew it (" + shortId(identity) + "), and this build reads "
                + shortId(EarthData.EARTH_1) + ": its coasts and rivers would not be the ones it was built on. The build that"
                + " saved it still opens it.";
    }

    /* =====================================================================
       SEARCHING THE PLACES (names for finding a spot, no borders)
       ===================================================================== */

    /** Words folded for matching: lower case, accents off, runs of anything but letters and digits one space. */
    static String fold(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{Nd}]+", " ").trim();
    }

    /**
     * The places a typed name finds, at most `limit`: the name or its plain
     * spelling whole first, then those it begins, then those with a word it
     * begins, then those it is anywhere in - each group most populous first
     * (the data's order). Accents and case do not matter. Nothing for
     * nothing typed.
     */
    public static List<EarthData.Place> search(EarthData d, String typed, int limit) {
        String q = fold(typed);
        List<EarthData.Place> out = new ArrayList<>();
        if (q.isEmpty() || limit <= 0) return out;
        List<EarthData.Place> places = d.places();
        for (int rank = 0; rank < 4 && out.size() < limit; rank++) {
            for (EarthData.Place p : places) {
                if (out.size() >= limit) break;
                if (rankOf(p, q) == rank) out.add(p);
            }
        }
        return out;
    }

    /** How well a place matches a folded query: 0 whole, 1 begun, 2 a word begun, 3 within, 4 not at all - the better of its name and plain spelling. */
    static int rankOf(EarthData.Place p, String q) {
        return Math.min(rankIn(fold(p.name()), q), rankIn(fold(p.ascii()), q));
    }

    private static int rankIn(String n, String q) {
        if (n.equals(q)) return 0;
        if (n.startsWith(q)) return 1;
        if (n.contains(" " + q)) return 2;
        if (n.contains(q)) return 3;
        return 4;
    }

    /* =====================================================================
       RANDOM SPOT
       ===================================================================== */

    /**
     * The place the seed picks (spec-earth 2's Random spot): the data's places
     * tried in the order the seed's hash gives them, RANDOM_TRIES at most, the
     * first that passes the site test (passes()) - so the same seed and data
     * pick the same place. Null when none of them does.
     */
    public static EarthData.Place randomPlace(EarthData d, String identity, long seed) {
        List<EarthData.Place> places = d.places();
        if (places.isEmpty()) return null;
        for (int k = 0; k < RANDOM_TRIES; k++) {
            int i = (int) (World.unit(World.mix(seed ^ RANDOM_SALT ^ (k * 0x9E3779B97F4A7C15L))) * places.size());
            EarthData.Place p = places.get(Math.min(places.size() - 1, i));
            if (passes(World.Key.earth(seed, p.lat(), p.lon(), identity))) return p;
        }
        return null;
    }

    /** The site test (spec-earth 2): the snap finds dry ground within CARD_PLOTS of the spot, and it is not on ice. */
    public static boolean passes(World.Key key) {
        EarthTerrain e = World.of(key).earthTerrain();
        long[] s = e.siteNear();
        return s[0] >= 0 && !onIce(e, s[0], s[1]);
    }

    /**
     * Why a city cannot be founded on the world an Earth key names, or null
     * when it can (Founding.problem()): its data not here, no site within the
     * snap (NO_SITE_WORDS), or ice under it (ICE_WORDS) - the card's
     * refusals, without the rest of the card.
     */
    public static String foundingProblem(World.Key key) {
        if (!available(key.data())) {
            return key.data().equals(EarthData.EARTH_1) ? dataWords(EarthData.locate())
                    : "The real Earth as " + shortId(key.data()) + " draws it is not here; this build reads " + shortId(EarthData.EARTH_1) + ".";
        }
        EarthTerrain e = World.of(key).earthTerrain();
        long[] s = e.siteNear();
        if (s[0] < 0) return NO_SITE_WORDS;
        return onIce(e, s[0], s[1]) ? ICE_WORDS : null;
    }

    /** Whether a plot's 2' land-cover cell is ice: snow and ice on half its land or more (EarthData.LC_ICE) - Greenland's and Antarctica's sheets, and a glacier's heart. */
    static boolean onIce(EarthTerrain e, long x, long y) {
        return (cover(e, x, y) & EarthData.LC_ICE) != 0;
    }

    /** ...and a desert: bare ground on half its land or more (EarthData.LC_DESERT). */
    static boolean inDesert(EarthTerrain e, long x, long y) {
        return (cover(e, x, y) & EarthData.LC_DESERT) != 0;
    }

    private static int cover(EarthTerrain e, long x, long y) {
        double[] ll = new double[2];
        if (!e.projection().latLonOfPlot(x + 0.5, y + 0.5, ll)) return 0;
        return e.data().at("landcover", ll[0], ll[1]);
    }

    /* =====================================================================
       THE CARD
       ===================================================================== */

    /**
     * What the founding card says of a spot (measure()). Distances in km,
     * NaN where there is none within reach; the ground's counts in plots of
     * the disc CARD_PLOTS round the site's centre; the multipliers by
     * Resource's ordinal, NaN for one not in fields (forest).
     *
     * @param snappedM    how far the site lies from the spot, metres
     * @param near        where the site is, from the nearest of the data's places ("0.4 km north of Rotterdam")
     * @param provinceRow the geologic province under the site, its row in the data (0 for none)
     * @param ironWay     which way the nearest iron lies from the site, of the eight
     * @param refusal     why the city cannot be founded here, or null
     * @param warnings    what to know before founding here; empty for nothing
     */
    public record Card(World.Key key, boolean hasSite, long siteX, long siteY, double siteLat, double siteLon,
                       double snappedM, String near, long plots, long dry, long fresh, long sea, double freshKm,
                       double seaKm, int provinceRow, String provinceName, Geology.Kind kind, double[] multipliers,
                       double ironKm, String ironWay, boolean ice, boolean desert, String refusal,
                       List<String> warnings) {

        /** Whether a city can be founded on it. */
        public boolean foundable() { return refusal == null; }

        /** The dry ground's share of the disc. */
        public double dryShare()   { return plots == 0 ? 0 : dry / (double) plots; }

        /** Rivers' and lakes' share of the disc. */
        public double freshShare() { return plots == 0 ? 0 : fresh / (double) plots; }

        /** The sea's share of the disc. */
        public double seaShare()   { return plots == 0 ? 0 : sea / (double) plots; }

        /** A resource's deposit multiplier under the site (Geology.multiplier()), NaN for one not in fields. */
        public double multiplier(Resource r) { return multipliers[r.ordinal()]; }
    }

    /** The card's words for a spot with no site. */
    public static final String NO_SITE_WORDS = "No dry ground within 5 km to found a city on: the spot is in the sea, or on an island too small.";

    /** ...on ice. */
    public static final String ICE_WORDS = "On ice: snow and ice cover half the ground here or more, and no city is founded on an ice sheet.";

    /** The warning for a desert. */
    public static final String DESERT_WORDS = "A desert: bare ground covers half the land here or more.";

    /** The warning where there is no fresh water within the card's reach and the sea is near. */
    public static final String NO_FRESH_WORDS = "No river or lake within 5 km: a water plant needs one, so the city's first water is from the sea, by desalination.";

    /** ...and where there is neither. */
    public static final String NO_WATER_WORDS = "No river, lake or sea within 5 km: the city has nowhere near to draw its water from.";

    /**
     * The founding card for the world a key names (spec-earth 2): the snap's
     * site, the ground within CARD_PLOTS of it plot by plot - dry, rivers and
     * lakes, sea - and the nearest fresh water and sea to its centre, where it
     * is from the nearest place, its province's multipliers, the nearest iron
     * field, and the refusals (no site, ice) and warnings (a desert, no fresh
     * water within reach). Builds the world when there is a site (the cells
     * pass, about half a second, for the iron); off the screen's thread.
     */
    public static Card measure(World.Key key) {
        if (!key.earth()) throw new IllegalArgumentException("not an Earth world: " + key);
        World w = World.of(key);
        EarthTerrain e = w.earthTerrain();
        EarthData d = e.data();
        long[] s = e.siteNear();
        double[] multipliers = new double[Resource.values().length];
        java.util.Arrays.fill(multipliers, Double.NaN);
        if (s[0] < 0) {
            return new Card(key, false, -1, -1, Double.NaN, Double.NaN, Double.NaN, null, 0, 0, 0, 0, Double.NaN,
                    Double.NaN, 0, null, null, multipliers, Double.NaN, null, false, false, NO_SITE_WORDS, List.of());
        }
        long sx = s[0], sy = s[1];
        double[] ll = new double[2];
        e.projection().latLonOfPlot(sx + 0.5, sy + 0.5, ll);
        double snapped = Math.hypot(sx + 0.5 - World.SIDE / 2.0, sy + 0.5 - World.SIDE / 2.0) * World.PLOT_M;
        EarthData.Place place = LandMap.nearestPlace(d, ll[0], ll[1]);
        String near = place == null ? null : LandMap.placeWords(place, ll[0], ll[1]);

        // The ground within the reach, plot by plot from the tiles it is drawn in.
        long r = CARD_PLOTS, r2 = r * r, plots = 0, dry = 0, fresh = 0, sea = 0;
        long fresh2 = Long.MAX_VALUE, sea2 = Long.MAX_VALUE;
        int n = World.TILE;
        for (long ty = Math.floorDiv(sy - r, n); ty <= Math.floorDiv(sy + r, n); ty++) {
            for (long tx = Math.floorDiv(sx - r, n); tx <= Math.floorDiv(sx + r, n); tx++) {
                byte[] t = null;
                for (int py = 0; py < n; py++) {
                    long y = ty * n + py, dy = y - sy;
                    if (dy * dy > r2) continue;
                    for (int px = 0; px < n; px++) {
                        long x = tx * n + px, dx = x - sx, d2 = dx * dx + dy * dy;
                        if (d2 > r2) continue;
                        if (t == null) t = x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE ? null : e.keptTile(tx, ty);
                        byte g = t == null ? World.SALT : t[py * n + px];
                        plots++;
                        if (g == World.SALT) {
                            sea++;
                            sea2 = Math.min(sea2, d2);
                        } else if (g == World.FRESH) {
                            fresh++;
                            fresh2 = Math.min(fresh2, d2);
                        } else {
                            dry++;
                        }
                    }
                }
            }
        }
        double freshKm = fresh2 == Long.MAX_VALUE ? Double.NaN : Math.sqrt(fresh2) * World.PLOT_M / 1000;
        double seaKm = sea2 == Long.MAX_VALUE ? Double.NaN : Math.sqrt(sea2) * World.PLOT_M / 1000;

        // The province under the site and its multipliers (E3).
        Geology geo = w.geology();
        int row = w.provinceAt(sx, sy);
        for (Resource res : Resource.values()) if (res.inFields()) multipliers[res.ordinal()] = geo.multiplier(res, row);
        EarthData.Province prov = row > 0 ? d.province(row) : null;

        boolean ice = onIce(e, sx, sy), desert = inDesert(e, sx, sy);
        List<String> warnings = new ArrayList<>();
        if (desert) warnings.add(DESERT_WORDS);
        if (Double.isNaN(freshKm)) warnings.add(Double.isNaN(seaKm) ? NO_WATER_WORDS : NO_FRESH_WORDS);

        // The nearest iron, among the world's own fields (Jerus: no field promised, the nearest shown).
        double ironKm = Double.NaN;
        String ironWay = null;
        if (!ice) {
            double[] iron = nearestIron(w, sx, sy);
            if (iron != null) {
                ironKm = iron[0] * World.PLOT_M / 1000;
                ironWay = LandMap.COMPASS[(int) Math.floorMod(Math.round(StrictMath.toDegrees(StrictMath.atan2(iron[1], -iron[2])) / 45), 8L)];
            }
        }
        return new Card(key, true, sx, sy, ll[0], ll[1], snapped, near, plots, dry, fresh, sea, freshKm, seaKm, row,
                prov == null ? null : prov.name().trim(), geo.kindOf(row), multipliers, ironKm, ironWay, ice, desert,
                ice ? ICE_WORDS : null, List.copyOf(warnings));
    }

    /**
     * The nearest iron field's centre to a plot, {plots away, east, south}, or
     * null when none lies within IRON_RINGS cells: the world's fields cell by
     * cell, ring by ring round the plot's cell, until no ring left can hold a
     * nearer one.
     */
    static double[] nearestIron(World w, long x, long y) {
        int c0 = World.cellOf(x, y);
        if (c0 < 0) return null;
        int cx = c0 % World.CELLS, cy = c0 / World.CELLS;
        double best = Double.POSITIVE_INFINITY, bx = 0, by = 0;
        for (int k = 0; k <= IRON_RINGS; k++) {
            if (best <= (double) (k - 1) * World.CELL) break;
            for (int j = cy - k; j <= cy + k; j++) {
                for (int i = cx - k; i <= cx + k; i++) {
                    if (Math.max(Math.abs(i - cx), Math.abs(j - cy)) != k) continue;
                    if (i < 0 || j < 0 || i >= World.CELLS || j >= World.CELLS) continue;
                    for (Deposit f : w.fieldsInCell(j * World.CELLS + i, Resource.IRON)) {
                        double dx = f.x() + 0.5 - (x + 0.5), dy = f.y() + 0.5 - (y + 0.5), dd = Math.hypot(dx, dy);
                        if (dd < best) {
                            best = dd;
                            bx = dx;
                            by = dy;
                        }
                    }
                }
            }
        }
        return Double.isInfinite(best) ? null : new double[] { best, bx, by };
    }

    /** A latitude and longitude as the card and the search write them: "51.92° N, 4.48° E". */
    public static String latLonWords(double lat, double lon) {
        double l = EarthProj.normaliseLon(lon);
        return String.format(Locale.ROOT, "%.2f° %s, %.2f° %s", Math.abs(lat), lat >= 0 ? "N" : "S", Math.abs(l), l >= 0 ? "E" : "W");
    }
}
