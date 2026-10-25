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
 *     the file, and says where it goes and, since 0.7.125, how it gets there -
 *     dataWords()).
 *   - A spot in the sea or on ice is refused - and, since ET2, one among
 *     slopes no building may stand on, none within 5 km; a desert, or no fresh
 *     water within 5 km, is warned of (Card.refusal(), Card.warnings()).
 *   - No iron field is promised: the card names the nearest (Card.ironKm()).
 *   - Place names for searching only (search()), and no borders: a place is
 *     its name and its latitude and longitude, never a country.
 *
 * THE CARD (measure()) is the city's own world, World.of(key): the site is
 * its snap (EarthTerrain.siteNear(), the founding's test 1 within 5 km of
 * the spot), the ground within CARD_PLOTS of the site counted plot by plot
 * as the land office will sell it, the province the geology under the site
 * (E3's multipliers, w x Z, since EG1 oil's by its known petroleum and iron's
 * lifted in a recorded district), and the nearest iron
 * field's centre among the world's own fields. Nothing here is a model
 * change: the city founded is the one the card describes. Since 0.7.127
 * (batch E7) ice is a kind of ground, never dry: the card's ice is the
 * plot's class (onIce()), so the snap never finds a site on it, and a spot
 * on an ice sheet is refused on its own plot.
 *
 * THE CARD'S THREE LINES OF ET1 (the project's spec-earth2-terrain.md 3.4;
 * Jerus 2026-10-12, D21): the dry ground within 5 km by relief (the same
 * plots the walk counts, read beside the cover: flat, foothill, steep, very
 * steep kept apart in the Card's numbers and folded in its words), the
 * nearest shallow water with how deep the sea gets within 5 km, and the
 * landmass the site stands on, by its place's name and its area. The snap and
 * the refusal on ground no building may stand on are ET2's (slice C, the
 * founding on buildable ground): the snap's test 1 reads World.buildable() -
 * dry, and flat or foothill - so a spot among steep slopes finds its site on
 * the nearest buildable ground within 5 km, or none; with none the card is
 * refused in noSiteWords(), which names the nearest buildable ground within
 * 50 km (EarthTerrain.siteBeyond()), or that there is none. A save from before
 * is refused (SLOPE_FORMAT, BEFORE_SLOPE_WORDS): the world's founding site
 * is found again from the spot and the data each time, and would not be the
 * one the city was built round.
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

    /** The save format a city on the real Earth is first written in: 38 (GameVersion's A CITY ON THE REAL EARTH) - a build before it would put the city on its seed's generated ground, so it refuses the save as a newer build's; a save before it is a generated city's, and loads as one. */
    public static final int EARTH_FORMAT = 38;

    /**
     * The save format the real Earth is first drawn in with ice and desert:
     * 39 (0.7.127, batch E7; GameVersion's ICE AND DESERT ON THE REAL EARTH).
     * An Earth city saved before it kept books that counted any ground now
     * drawn as ice as dry, so its save is refused (loadProblem()); a
     * generated city's ground is as it was, and its save loads.
     */
    public static final int ICE_FORMAT = 39;

    /**
     * The save format the real Earth's fields are first drawn on its geology
     * in: 41 (0.8.12, batch EG1; GameVersion's DEPOSITS WHERE THE GEOLOGY
     * SAYS). An Earth city saved before it names fields - by their cell and
     * their place in its list - that now lie elsewhere or hold other tonnes,
     * so its save is refused (loadProblem()); a generated city's fields are as
     * they were, and its save loads.
     */
    public static final int DEPOSITS_FORMAT = 41;

    /**
     * The save format the real Earth's founding site is first snapped to
     * buildable ground in: 46 (ET2, spec-earth2-terrain 3.4; GameVersion's
     * BUILDABLE GROUND, FOUNDED ON). The world's founding site is found again
     * from the city's spot and the data each time the world is built - the
     * boats' (BoatSchedule) and any new founding's - so an Earth city saved
     * before it, whose site was snapped where steep ground counted as dry,
     * would load into a world whose founding site is not the one it was built
     * round: its save is refused (loadProblem()); a generated city's founding
     * does not read relief, and its save loads.
     */
    public static final int SLOPE_FORMAT = 46;

    /**
     * The save format the city's ground is first counted by landmass in: 47 (ET5; Landmasses, GameVersion's LANDMASSES).
     * An Earth city saved before it that owns ground across the water would load that ground as room for any building,
     * and every sea terminal it holds as standing at home - wrongly, not merely incompletely - so its save is refused
     * (loadProblem()); a generated city has one landmass, always home, and its save loads.
     */
    public static final int LANDMASS_FORMAT = 47;

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
     * The game's Earth (EarthData.EARTH_2): the file EarthData.locate() finds
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
            looked = new Data(EarthTerrain.source(EarthData.EARTH_2), null);
        } catch (RuntimeException e) {
            looked = new Data(null, dataWords(p));
        }
        return looked;
    }

    /* ----- how the file gets there (0.7.125, batch C2) -----
       Since f99d62c (earth-2 since ET1) the file is the GitHub release earth-2's asset:
       GET_EARTH checks DATA_FILE against tools/earth/earth-2.sha256 and
       downloads it when it is missing or bad, Play CityBuilderSim.bat calls
       it before it starts the game, and Build EXE.bat carries the file into
       the built game and its zip. So the words say the fetch where the
       script is - a copy of the repository - and the zip where it is not:
       the built game's folder has no tools/, and came with the file. */

    /** The script that fetches the file, from the folder the game runs in (the folder DATA_FILE is looked for from): there in a copy of the repository, not in the built game, which carries the file in its zip. */
    public static final String GET_EARTH = "tools/earth/get-earth.bat";

    /** Whether the game runs from a copy of the repository - GET_EARTH is there - rather than as the built game: which of the two ways to the file the words say. */
    static boolean fromRepository() {
        return Files.isRegularFile(Path.of(GET_EARTH));
    }

    /** Why the game's Earth is not here, in the player's words: where the file goes and how it gets there, or that the one there is not this build's and what replaces it. */
    static String dataWords(Path found) {
        return dataWords(found, fromRepository());
    }

    /** ...for a copy of the repository or the built game, said: the founding screen's words. */
    static String dataWords(Path found, boolean repository) {
        if (missing(found)) {
            return "The real Earth needs its data, earth-2.bin (about 282 MB), as " + EarthData.DATA_FILE + " beside the game,"
                    + " and it is not here. " + fetchWords(repository, "open this page again");
        }
        return "The file at " + found + " is not the Earth's data this build reads (" + shortId(EarthData.EARTH_2) + "). "
                + replaceWords(repository, "open this page again");
    }

    /** Whether no file is at the path found. */
    private static boolean missing(Path found) {
        return found == null || !Files.isRegularFile(found);
    }

    /** How a missing file gets there, then what to do: GET_EARTH and Play CityBuilderSim.bat, or the built game's zip. */
    private static String fetchWords(boolean repository, String then) {
        return repository
                ? "Play CityBuilderSim.bat fetches it before it starts the game, or run " + GET_EARTH + " now and " + then + "."
                : "The game's zip carries it, so this copy of the game is incomplete: unzip the whole zip once more and " + then + ".";
    }

    /** What replaces a file that is not this build's, then what to do: GET_EARTH, which fetches it again, or the built game's zip. */
    private static String replaceWords(boolean repository, String then) {
        return repository
                ? "Run " + GET_EARTH + ", which replaces a bad copy, and " + then + "; Play CityBuilderSim.bat runs it before it starts the game."
                : "The game's zip carries the right one: unzip the whole zip once more and " + then + ".";
    }

    /** An identity as a person reads it: "earth-2, c897b242". */
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
     * its own): its data is not this build's, or this build's is not here -
     * and then, since 0.7.125, how the file gets there (missingWords()) - or,
     * since 0.7.127, it was saved before ice was a kind of ground
     * (ICE_FORMAT), or, since 0.8.12, before its fields were drawn on the
     * geology (DEPOSITS_FORMAT), or, since ET2, before its founding site was
     * snapped to ground a building may stand on (SLOPE_FORMAT). Game's load
     * refuses it with these words, nothing of it applied. This one asks of a
     * save in this build's format.
     */
    public static String loadProblem(String identity, Double lat, Double lon) {
        return loadProblem(identity, lat, lon, GameVersion.SAVE_FORMAT);
    }

    /** ...and of a save in a given format: one before ICE_FORMAT is refused first (0.7.127), then one before DEPOSITS_FORMAT (0.8.12), then one before SLOPE_FORMAT (ET2), then one before LANDMASS_FORMAT (ET5), whatever its data (DataSave.earthProblem() passes the save's). */
    public static String loadProblem(String identity, Double lat, Double lon, int format) {
        if (identity == null || identity.isBlank() || lat == null || lon == null || !(lat >= -90 && lat <= 90) || !Double.isFinite(lon)) {
            return "This city stands on the real Earth, and its save does not say where.";
        }
        if (format < ICE_FORMAT) return BEFORE_ICE_WORDS;
        if (format < DEPOSITS_FORMAT) return BEFORE_DEPOSITS_WORDS;
        if (format < SLOPE_FORMAT) return BEFORE_SLOPE_WORDS;
        if (format < LANDMASS_FORMAT) return BEFORE_LANDMASS_WORDS;
        if (available(identity)) return null;
        if (identity.equals(EarthData.EARTH_2)) return missingWords(EarthData.locate(), fromRepository());
        return otherDataWords(identity);
    }

    /** The load's words for a city on this build's Earth whose file is not here or not this build's (0.7.125): how it gets there, then load the city again. */
    static String missingWords(Path found, boolean repository) {
        if (missing(found)) {
            return "This city stands on the real Earth, and its data is not here: earth-2.bin (about 282 MB), as " + EarthData.DATA_FILE
                    + " beside the game. " + fetchWords(repository, "load the city again");
        }
        return "This city stands on the real Earth, and the file at " + found + " is not the Earth's data this build reads ("
                + shortId(EarthData.EARTH_2) + "). " + replaceWords(repository, "load the city again");
    }

    /** The load's words for a city on the real Earth saved before ice was a kind of ground (ICE_FORMAT). */
    public static final String BEFORE_ICE_WORDS = "This city was founded on the real Earth before ice was a kind of ground (0.7.127): its books may count"
            + " ice it holds as dry ground, so this build does not load it. The build that saved it still opens it.";

    /** The load's words for a city on the real Earth saved before its fields were drawn on the geology (DEPOSITS_FORMAT). */
    public static final String BEFORE_DEPOSITS_WORDS = "This city was founded on the real Earth before its oil, iron and other fields were drawn where the geology"
            + " says (0.8.12): its holdings and mines name fields that now lie elsewhere, so this build does not load it."
            + " The build that saved it still opens it.";

    /** The load's words for a city on the real Earth saved before its founding site was snapped to ground a building may stand on (SLOPE_FORMAT). */
    public static final String BEFORE_SLOPE_WORDS = "This city was founded on the real Earth before slope decided where a building may stand: its founding site was"
            + " found where steep ground counted as buildable, and this build would find it elsewhere, so it does not load it."
            + " The build that saved it still opens it.";

    /** The load's words for a city on the real Earth saved before its ground was counted by landmass (LANDMASS_FORMAT). */
    public static final String BEFORE_LANDMASS_WORDS = "This city was founded on the real Earth before its ground was counted by landmass: ground across the"
            + " water would load as ground to build on, and its ports as standing at home, so this build does not load it."
            + " The build that saved it still opens it.";

    /** The load's words for a city on other data than this build's. */
    private static String otherDataWords(String identity) {
        return "This city stands on the real Earth as other data drew it (" + shortId(identity) + "), and this build reads "
                + shortId(EarthData.EARTH_2) + ": its coasts and rivers would not be the ones it was built on. The build that"
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

    /** The site test (spec-earth 2): the snap finds buildable ground (ET2: dry, flat or foothill) within CARD_PLOTS of the spot, and it is not on ice. */
    public static boolean passes(World.Key key) {
        EarthTerrain e = World.of(key).earthTerrain();
        long[] s = e.siteNear();
        return s[0] >= 0 && !onIce(e, s[0], s[1]);
    }

    /**
     * Why a city cannot be founded on the world an Earth key names, or null
     * when it can (Founding.problem()): its data not here, no site within the
     * snap (noSiteWords(): NO_SITE_WORDS, or since ET2 SLOPE_WORDS and where
     * the nearest buildable ground is), or ice under it (ICE_WORDS: since
     * 0.7.127 a spot with no site whose own plot is ice) - the card's
     * refusals, without the rest of the card.
     */
    public static String foundingProblem(World.Key key) {
        if (!available(key.data())) {
            return key.data().equals(EarthData.EARTH_2) ? dataWords(EarthData.locate())
                    : "The real Earth as " + shortId(key.data()) + " draws it is not here; this build reads " + shortId(EarthData.EARTH_2) + ".";
        }
        EarthTerrain e = World.of(key).earthTerrain();
        long[] s = e.siteNear();
        long spot = EarthTerrain.spotPlot();
        if (s[0] < 0) return onIce(e, spot, spot) ? ICE_WORDS : noSiteWords(e);
        return onIce(e, s[0], s[1]) ? ICE_WORDS : null;
    }

    /**
     * The words for a spot whose snap found no site and whose own plot is not ice (ET2, spec-earth2-terrain 3.4): where
     * the dry ground within the snap is steep or very steep on half of it or more, SLOPE_WORDS and where the nearest
     * buildable ground is - its distance and way from the spot, "The nearest buildable ground is 7.4 km north-east.",
     * or "There is none within 50 km." (EarthTerrain.siteBeyond()); NO_SITE_WORDS otherwise, the spot in the sea or on
     * an island too small. Ice keeps its own words (ICE_WORDS), asked first.
     */
    static String noSiteWords(EarthTerrain e) {
        long[] g = e.snapGround();
        if (g[0] == 0 || 2 * g[1] < g[0]) return NO_SITE_WORDS;
        long[] b = e.siteBeyond();
        if (b[0] < 0) return SLOPE_WORDS + " There is none within " + String.format(Locale.ROOT, "%.0f km", EarthTerrain.BEYOND_PLOTS * World.PLOT_M / 1000.0) + ".";
        double east = b[0] - EarthTerrain.spotPlot(), south = b[1] - EarthTerrain.spotPlot();
        String way = LandMap.COMPASS[(int) Math.floorMod(Math.round(StrictMath.toDegrees(StrictMath.atan2(east, -south)) / 45), 8L)];
        return SLOPE_WORDS + " The nearest buildable ground is " + distanceWords(Math.hypot(east, south) * World.PLOT_M / 1000) + " " + way + ".";
    }

    /**
     * Whether a plot is ice: its class as drawn, World.ICE (0.7.127, batch
     * E7; spec-globe 3.9) - Greenland's and Antarctica's sheets and a
     * glacier's heart, the founding card reading the map's own rule, so the
     * card and the plot agree at the site. Until then its 2' land-cover
     * cell's bit (EarthData.LC_ICE), whose edge the plots fray. Ice is not
     * dry, so the snap never lands on it: a spot is on ice when it has no
     * site and its own plot is ice (measure(), foundingProblem()).
     */
    static boolean onIce(EarthTerrain e, long x, long y) {
        return e.terrainAt(x, y) == World.ICE;
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
     * @param icePlots    the disc's plots of ice (0.7.127), neither dry nor water: dry, fresh, sea and icePlots add up to plots
     * @param ironWay     which way the nearest iron lies from the site, of the eight
     * @param refusal     why the city cannot be founded here, or null
     * @param reliefPlots the disc's dry plots by relief (ET1), World.RELIEFS of them, FLAT to VERY_STEEP: they add up to dry
     * @param shallowKm   how far the nearest shallow sea (World.SHALLOW) lies from the site's centre, km; NaN where none lies within reach
     * @param seaDepthMaxM the deepest the sea is within reach, metres: the depth layer's 1' cell under each sea plot of the disc (EarthData.depthM(), as World.depthAt() reads it: a metre at least), the greatest; NaN where there is no sea within reach
     * @param landmass    the landmass the site stands on (World.landmassAt() at the snapped site, ET5: the home landmass of the city founded there); null where the data has no landmass layer or none lies about the site
     * @param warnings    what to know before founding here; empty for nothing
     */
    public record Card(World.Key key, boolean hasSite, long siteX, long siteY, double siteLat, double siteLon,
                       double snappedM, String near, long plots, long dry, long fresh, long sea, long icePlots, double freshKm,
                       double seaKm, int provinceRow, String provinceName, Geology.Kind kind, double[] multipliers,
                       double ironKm, String ironWay, boolean ice, boolean desert, String refusal,
                       long[] reliefPlots, double shallowKm, double seaDepthMaxM, EarthData.Landmass landmass,
                       List<String> warnings) {

        /** Whether a city can be founded on it. */
        public boolean foundable() { return refusal == null; }

        /** The dry ground's share of the disc. */
        public double dryShare()   { return plots == 0 ? 0 : dry / (double) plots; }

        /** Rivers' and lakes' share of the disc. */
        public double freshShare() { return plots == 0 ? 0 : fresh / (double) plots; }

        /** The sea's share of the disc. */
        public double seaShare()   { return plots == 0 ? 0 : sea / (double) plots; }

        /** Ice's share of the disc (0.7.127). */
        public double iceShare()   { return plots == 0 ? 0 : icePlots / (double) plots; }

        /** A resource's deposit multiplier under the site (Geology.multiplierAt(), at the site's point: an iron district's shows, EG1), NaN for one not in fields. */
        public double multiplier(Resource r) { return multipliers[r.ordinal()]; }

        /** One relief's share of the disc's dry ground (ET1): World.FLAT, FOOTHILL, STEEP or VERY_STEEP; 0 where there is no dry ground. */
        public double reliefShare(int relief) { return dry == 0 ? 0 : reliefPlots[relief] / (double) dry; }

        /** The share of the dry ground that is steep or very steep (ET1): the card's words fold the two. */
        public double steepShare() { return reliefShare(World.STEEP) + reliefShare(World.VERY_STEEP); }
    }

    /**
     * THE CARD'S THREE LINES OF ET1 (spec-earth2-terrain 3.4; Jerus 2026-10-12, D21: relief, water and landmass).
     * The ground within 5 km by relief as shares of its dry ground - flat, foothill and steep, very steep folded
     * into steep, a class with no plot left out ("Dry ground within 5 km: flat 62% · foothill 30% · steep 8%").
     */
    public static String reliefWords(Card c) {
        if (c.dry() == 0) return "No dry ground within " + reachWords();
        StringBuilder out = new StringBuilder("Dry ground within ").append(reachWords());
        String sep = ": ";
        long steep = c.reliefPlots()[World.STEEP] + c.reliefPlots()[World.VERY_STEEP];
        String[] names = { "flat", "foothill", "steep" };
        long[] plots = { c.reliefPlots()[World.FLAT], c.reliefPlots()[World.FOOTHILL], steep };
        for (int i = 0; i < names.length; i++) {
            if (plots[i] == 0) continue;
            double share = plots[i] / (double) c.dry();
            out.append(sep).append(names[i]).append(' ').append(share < 0.005 ? "under 1%" : String.format(Locale.ROOT, "%.0f%%", 100 * share));
            sep = " · ";
        }
        return out.toString();
    }

    /** ...the nearest shallow water and how deep the sea gets in reach: "Shallow water 0.4 km away · the sea is up to 34 m deep within 5 km", or none within the reach (the mockup's "deepest 1' cell", the depth layer's). */
    public static String shallowWords(Card c) {
        String shallow = Double.isNaN(c.shallowKm()) ? "No shallow water within " + reachWords() : "Shallow water " + distanceWords(c.shallowKm()) + " away";
        String depth = Double.isNaN(c.seaDepthMaxM()) ? "no sea within " + reachWords()
                : String.format(Locale.ROOT, "the sea is up to %,.0f m deep within %s", Math.rint(c.seaDepthMaxM()), reachWords());
        return shallow + " · " + depth;
    }

    /** The depth layer's metres under a sea plot (World.depthAt()'s rule, read here without the world's plot cache): a metre at least, and the layer's deepest past the planet's rim. */
    private static double plotDepth(EarthTerrain e, EarthData d, long x, long y, double[] ll) {
        if (!e.projection().latLonOfPlot(x + 0.5, y + 0.5, ll)) return EarthData.DEPTH_DEEPEST_M;
        return Math.max(EarthTerrain.SEA_LEAST_DEPTH_M, d.depthM(ll[0], ll[1]));
    }

    /**
     * ...and the landmass: its place's name (the data's most populous place on it) and its area ("Its landmass: New
     * York's, 36.8 million km² (id 2)"), or null where the data has none to say.
     */
    public static String landmassWords(Card c) {
        EarthData.Landmass l = c.landmass();
        if (l == null) return null;
        String area = l.areaKm2() >= 1e6 ? String.format(Locale.ROOT, "%.1f million km²", l.areaKm2() / 1e6)
                : l.areaKm2() >= 10 ? String.format(Locale.ROOT, "%,.0f km²", l.areaKm2()) : String.format(Locale.ROOT, "%.1f km²", l.areaKm2());
        return "Its landmass: " + (l.name().isBlank() ? "unnamed" : l.name().trim() + "'s") + ", " + area + " (id " + l.id() + ")";
    }

    /** The card's reach as it writes it: "5 km". */
    private static String reachWords() { return String.format(Locale.ROOT, "%.0f km", CARD_PLOTS * World.PLOT_M / 1000.0); }

    /** A distance as the card writes it: a tenth of a km under ten, whole km from there (the founding screen's). */
    private static String distanceWords(double km) {
        return km < 10 ? String.format(Locale.ROOT, "%.1f km", km) : String.format(Locale.ROOT, "%,.0f km", km);
    }

    /**
     * The landmass a site stands on (ET1; ET5): the one World.landmassAt() reads at the snapped site's plot - the data's
     * cell at the plot's blend point, or for a plot the game draws dry on a cell the layer calls no land the nearest land
     * cell's within EarthTerrain.LANDMASS_REACH. WHY it is that call and no rule of the card's own (ET1 had the most-seen
     * id of a ring of cells at the site's exact point): the city's home landmass IS this one, read at the same plot by the
     * same function (Landmasses), so the card and the books cannot disagree. Null for none, or for data with no landmass
     * layer (an earth-1 file).
     */
    static EarthData.Landmass siteLandmass(World w, long sx, long sy) {
        return w.hasLandmasses() ? w.earthTerrain().data().landmass(w.landmassAt(sx, sy)) : null;
    }

    /** The card's words for a spot with no site: in the sea, or on an island too small (a spot among steep slopes has SLOPE_WORDS). */
    public static final String NO_SITE_WORDS = "No dry ground within 5 km to found a city on: the spot is in the sea, or on an island too small.";

    /** The card's words for a spot among steep slopes (ET2): the first sentence of noSiteWords(), the nearest buildable ground after it. */
    public static final String SLOPE_WORDS = "No ground a building may stand on within 5 km: the slopes here are steep.";

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
     * lakes, sea and (0.7.127) ice - and the nearest fresh water and sea to its
     * centre, where it is from the nearest place, its province's multipliers,
     * the nearest iron field, and the refusals (no site, ice) and warnings (a
     * desert, no fresh water within reach). A spot on an ice sheet has no
     * site, ice not being dry; where its own plot is ice the card is that
     * plot's, refused on ice (0.7.127). Builds the world when there is a site
     * (the cells pass, about half a second, for the iron); off the screen's
     * thread.
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
            long spot = EarthTerrain.spotPlot();
            if (!onIce(e, spot, spot)) {
                return new Card(key, false, -1, -1, Double.NaN, Double.NaN, Double.NaN, null, 0, 0, 0, 0, 0, Double.NaN,
                        Double.NaN, 0, null, null, multipliers, Double.NaN, null, false, false, noSiteWords(e),
                        new long[World.RELIEFS], Double.NaN, Double.NaN, null, List.of());
            }
            // On an ice sheet with no dry ground within reach (0.7.127): the card is the spot's own plot's, refused on ice.
            s = new long[] { spot, spot };
        }
        long sx = s[0], sy = s[1];
        double[] ll = new double[2];
        e.projection().latLonOfPlot(sx + 0.5, sy + 0.5, ll);
        double snapped = Math.hypot(sx + 0.5 - World.SIDE / 2.0, sy + 0.5 - World.SIDE / 2.0) * World.PLOT_M;
        EarthData.Place place = LandMap.nearestPlace(d, ll[0], ll[1]);
        String near = place == null ? null : LandMap.placeWords(place, ll[0], ll[1]);

        // The ground within the reach, plot by plot from the tiles it is drawn in.
        long r = CARD_PLOTS, r2 = r * r, plots = 0, dry = 0, fresh = 0, sea = 0, icePlots = 0;
        long fresh2 = Long.MAX_VALUE, sea2 = Long.MAX_VALUE, shallow2 = Long.MAX_VALUE;
        boolean deep = d.hasRelief();                  // an earth-1 file has no depth layer to read
        double deepest = 0;
        double[] ll2 = new double[2];
        long[] reliefPlots = new long[World.RELIEFS];
        int n = World.TILE;
        for (long ty = Math.floorDiv(sy - r, n); ty <= Math.floorDiv(sy + r, n); ty++) {
            for (long tx = Math.floorDiv(sx - r, n); tx <= Math.floorDiv(sx + r, n); tx++) {
                byte[] t = null, rt = null;
                for (int py = 0; py < n; py++) {
                    long y = ty * n + py, dy = y - sy;
                    if (dy * dy > r2) continue;
                    for (int px = 0; px < n; px++) {
                        long x = tx * n + px, dx = x - sx, d2 = dx * dx + dy * dy;
                        if (d2 > r2) continue;
                        if (t == null) t = x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE ? null : e.keptTile(tx, ty);
                        byte g = t == null ? World.SALT : t[py * n + px];
                        plots++;
                        if (World.salt(g)) {
                            sea++;
                            sea2 = Math.min(sea2, d2);
                            if (g == World.SHALLOW) shallow2 = Math.min(shallow2, d2);
                            // The deepest the sea is in reach (ET1): the depth layer's 1' cell under each sea plot, at least a metre as the world's own depth is.
                            if (deep) deepest = Math.max(deepest, plotDepth(e, d, x, y, ll2));
                        } else if (g == World.FRESH) {
                            fresh++;
                            fresh2 = Math.min(fresh2, d2);
                        } else if (World.dry(g)) {
                            dry++;
                            // Its relief beside its cover (ET1), from the tile kept for it: the same plots, no second walk.
                            if (rt == null) rt = e.keptRelief(tx, ty);
                            reliefPlots[rt[py * n + px]]++;
                        } else {
                            icePlots++;
                        }
                    }
                }
            }
        }
        double freshKm = fresh2 == Long.MAX_VALUE ? Double.NaN : Math.sqrt(fresh2) * World.PLOT_M / 1000;
        double seaKm = sea2 == Long.MAX_VALUE ? Double.NaN : Math.sqrt(sea2) * World.PLOT_M / 1000;
        double shallowKm = shallow2 == Long.MAX_VALUE ? Double.NaN : Math.sqrt(shallow2) * World.PLOT_M / 1000;
        double seaDepthMaxM = sea2 == Long.MAX_VALUE || !deep ? Double.NaN : deepest;

        // The province under the site and its multipliers (E3), at the site's point so an iron district shows (EG1).
        Geology geo = w.geology();
        int row = w.provinceAt(sx, sy);
        for (Resource res : Resource.values()) if (res.inFields()) multipliers[res.ordinal()] = geo.multiplierAt(res, row, ll[0], ll[1]);
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
        return new Card(key, true, sx, sy, ll[0], ll[1], snapped, near, plots, dry, fresh, sea, icePlots, freshKm, seaKm, row,
                prov == null ? null : prov.name().trim(), geo.kindOf(row), multipliers, ironKm, ironWay, ice, desert,
                ice ? ICE_WORDS : null, reliefPlots, shallowKm, seaDepthMaxM, siteLandmass(w, sx, sy), List.copyOf(warnings));
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
