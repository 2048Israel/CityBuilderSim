package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they are counted on, the place search and Random spot, the sea's depth on Earth, and an Earth city founded, played and drilling at sea.
 *
 * WHY. Until E4 only harnesses could found a city on the Earth (E2's door,
 * Founding.withWorldKey()), and nothing a player saw was asked of it: the
 * card the founding screen shows is the player's whole view of a spot before
 * a city is founded on it for good, so every figure on it must be the one
 * the city will stand on - the snap's site, the plots the land office sells,
 * the geology the fields are drawn by. And the Earth has no heights, so the
 * sea's depth, which the land wells' count and every platform ask, threw
 * (E2's star 7): an Earth city with oil at sea would have stopped at its
 * first purchase off the coast.
 *
 * THE DATA. EarthCheck's fixture (the three windows, Rotterdam, Perth and
 * Paris, and Fiji's) for everything a player can cause anywhere; the full
 * file, when it is named (-Dcbs.earth=<path>, EarthData.locate()), for what
 * only the whole planet holds - an ice sheet, a desert, the shelf's width
 * measured over the Earth's sea - and says so in one line when it is not.
 * The save's four fields are SaveFileCheck's, NewGameCheck's and
 * ReadPathCheck's.
 *
 * What this has to prove (spec-earth 8, E4):
 *   1. THE DATA, OR WHY NOT: a reader made available is available and an
 *      identity nothing holds is not; the words for a missing file name where
 *      it goes and (0.7.125) how it gets there - EarthSite.GET_EARTH and Play
 *      CityBuilderSim.bat in a copy of the repository, the zip in the built
 *      game - for a file that is not this build's what replaces it, the
 *      load's the same; for another identity both identities, for a save that
 *      does not say where that it does not; the game's Earth is here when the
 *      full file is named, its identity EARTH_2.
 *   2. THE CARD IS THE PLOTS': on each window, the card's site is the world's
 *      founding site, the snap's; its disc of CARD_PLOTS counted again plot
 *      by plot adds up and agrees, and so do the nearest fresh water and sea;
 *      its province and multipliers are World's and Geology's (since 0.8.12,
 *      batch EG1, the multiplier at the site's point, Geology.multiplierAt():
 *      a recorded iron district shows); the nearest iron is the nearest of
 *      every field in the cells round it; printed against the study's
 *      figures (spec-earth 2).
 *   3. A SPOT IN THE SEA IS REFUSED: the card says NO_SITE_WORDS, the
 *      founding's problem() the same, and the game will not found it; on the
 *      full file, Greenland's sheet is refused ICE_WORDS and the Sahara warned
 *      DESERT_WORDS, a spot with no water near warned.
 *   4. SEARCH AND RANDOM SPOT: a place's own name finds it first, accents and
 *      case aside; the hits are ranked whole, begun, a word begun, within, each
 *      group in the data's order, at most SEARCH_HITS, nothing for nothing;
 *      the same seed picks the same place, which passes the site test, and
 *      seeds pick more than one.
 *   5. THE SEA'S DEPTH: on the plots a line out to sea crosses, nothing where
 *      a plot is not the sea's, and where it is World.SHELF_BREAK_M x its
 *      distance from the coast - a brute force over every cell within reach -
 *      over EarthTerrain.SHELF_WIDTH_M, at least a plot's; on the full file
 *      the width measured again is the constant, to its rounding.
 *   6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA: Rotterdam's founded through
 *      the game stands on its Earth world, its land the snap's; it buys the
 *      ground to an oil field in the sea, the field's sites the sea's, its
 *      depth World's and a platform's to stand on; a platform and its wells
 *      are built, the city plays two months, the investors plan at sea, and
 *      the oil's page reads its depth - nothing throws. Since 0.8.12 (batch
 *      EG1) the nearest oil field a platform can stand in is 55 km out (it was
 *      10), so the fixture buys toward it until it lies in the city's ground,
 *      bounded by its distance in plots, not by a constant (Jerus, 2026-10-20:
 *      a harness that asserted the old deposits' outcome has its fixture
 *      remade to cause its condition on the new ones); every assertion of the
 *      section is as it was.
 */
public class EarthFoundingCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;
    /** Where the game's own narration goes while a city is founded or played here (quietly()): nowhere. */
    static final PrintStream quiet = new PrintStream(java.io.OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** The fixture's windows: name, latitude, longitude. */
    record Spot(String name, double lat, double lon) { }

    /** The seed every world here is drawn with: the default world's. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    /** The study's card for each window (spec-earth 2): dry %, river and lake %, sea %, fresh water km, sea km - NaN where it gave none. */
    static final Map<String, double[]> STUDY = Map.of(
            "Rotterdam", new double[] { 88, 9.2, Double.NaN, 0.6, 3.0 },
            "Perth", new double[] { Double.NaN, Double.NaN, 42, 4.5, Double.NaN },
            "Paris", new double[] { Double.NaN, Double.NaN, 0, 0.1, Double.NaN });

    public static void main(String[] args) throws Exception {
        out.println("--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        check("EarthCheck's fixture and its expected figures are beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null && expect != null);
        if (fixture == null || expect == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        Map<String, Spot> spots = new LinkedHashMap<>();
        for (String line : new String(expect, StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length > 4 && f[1].equals("window")) spots.put(f[2], new Spot(f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4])));
        }
        check("fixture: the windows Rotterdam, Perth and Paris", spots.containsKey("Rotterdam") && spots.containsKey("Perth") && spots.containsKey("Paris"));

        EarthData file = theData(id);
        theCard(id, spots);
        refused(id, spots, file);
        search(data, id);
        depth(id, spots.get("Rotterdam"), file);
        aCity(id, spots.get("Rotterdam"));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE DATA, OR WHY NOT
       ===================================================================== */
    static EarthData theData(String id) throws Exception {
        out.println("\n--- 1. the data, or why not ---");
        String nobody = "earth-1:" + "0".repeat(64);
        check("a reader made available is available (the fixture's), and an identity nothing holds is not",
                EarthSite.available(id) && !EarthSite.available(nobody) && !EarthSite.available(null) && !EarthSite.available(""));
        String missing = EarthSite.dataWords(null);
        report("the words for a missing file say it is not here and where it goes", missing.contains("not here") && missing.contains(EarthData.DATA_FILE), missing);
        // How it gets there (0.7.125, batch C2): the fetch where the script is, a copy of the repository; the zip in the built game.
        String fetch = EarthSite.dataWords(null, true), zip = EarthSite.dataWords(null, false);
        report("...and how it gets there: in a copy of the repository the script, which Play CityBuilderSim.bat runs; in the built game its zip",
                fetch.contains(EarthData.DATA_FILE) && fetch.contains(EarthSite.GET_EARTH) && fetch.contains("Play CityBuilderSim.bat")
                && zip.contains(EarthData.DATA_FILE) && zip.contains("zip") && !zip.contains(".bat"), fetch + " | " + zip);
        Path bad = TempDirs.create("earthfounding-words").resolve("earth-2.bin");
        Files.write(bad, new byte[] { 1, 2, 3 });
        String replace = EarthSite.dataWords(bad, true), unzip = EarthSite.dataWords(bad, false);
        report("...for a file that is not this build's, the file, this build's identity and what replaces it: the script, or the zip",
                replace.contains(bad.toString()) && replace.contains(EarthSite.shortId(EarthData.EARTH_2)) && replace.contains(EarthSite.GET_EARTH)
                && unzip.contains(bad.toString()) && unzip.contains("zip") && !unzip.contains(".bat"), replace + " | " + unzip);
        String[] load = { EarthSite.missingWords(null, true), EarthSite.missingWords(null, false), EarthSite.missingWords(bad, true), EarthSite.missingWords(bad, false) };
        boolean loadSays = load[0].contains(EarthSite.GET_EARTH) && load[1].contains("zip") && load[2].contains(EarthSite.GET_EARTH) && load[3].contains("zip");
        for (int i = 0; i < load.length; i++) loadSays &= load[i].contains("load the city again") && load[i].contains(i < 2 ? EarthData.DATA_FILE : bad.toString());
        report("...and a save on this build's Earth without it, the same ways to it, then load the city again", loadSays, load[0] + " | " + load[1]);
        String other = EarthSite.loadProblem(nobody, 51.9, 4.5);
        report("...for a save on other data, both identities and the build that opens it", other != null && other.contains(EarthSite.shortId(nobody))
                && other.contains(EarthSite.shortId(EarthData.EARTH_2)) && other.contains("still opens it"), other);
        String where = EarthSite.loadProblem(id, null, 4.5);
        report("...for a save that does not say where, that it does not", where != null && where.contains("does not say where"), where);
        check("...and nothing for a save whose data is here", EarthSite.loadProblem(id, 51.9, 4.5) == null);
        check("an identity reads as a person reads it: its name and the hash's first eight", EarthSite.shortId(EarthData.EARTH_2).equals("earth-2, c897b242"));

        Path p = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && (p == null || !Files.isRegularFile(p))) {
            report("the file -Dcbs.earth names is there", false, named);
            return null;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (p == null) {
            report("no file named nor at " + EarthData.DATA_FILE + ": the game's Earth is not here, and says where the file goes",
                    !game.here() && game.words().contains(EarthData.DATA_FILE), game.words());
            out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's sections were not run");
            return null;
        }
        report("the game's Earth is here, the file named, EARTH_2", game.here() && game.reader().identity().equals(EarthData.EARTH_2)
                && EarthSite.available(EarthData.EARTH_2) && EarthSite.loadProblem(EarthData.EARTH_2, 51.9, 4.5) == null, String.valueOf(p));
        return game.reader();
    }

    /* =====================================================================
       2. THE CARD IS THE PLOTS'
       ===================================================================== */
    static void theCard(String id, Map<String, Spot> spots) {
        out.println("\n--- 2. the card is the plots': the site, the ground within 5 km, the province and the nearest iron ---");
        for (String name : List.of("Rotterdam", "Perth", "Paris")) {
            Spot s = spots.get(name);
            World.Key key = World.Key.earth(SEED, s.lat(), s.lon(), id);
            long t0 = System.nanoTime();
            EarthSite.Card c = EarthSite.measure(key);
            double ms = (System.nanoTime() - t0) / 1e6;
            World w = World.of(key);
            check(name + ": a site, and it is the world's founding site, the snap's",
                    c.hasSite() && c.siteX() == w.foundingX() && c.siteY() == w.foundingY() && c.foundable()
                            && c.snappedM() == Math.hypot(c.siteX() + 0.5 - World.SIDE / 2.0, c.siteY() + 0.5 - World.SIDE / 2.0) * World.PLOT_M);
            // The disc again, plot by plot through World.terrainAt().
            long r = EarthSite.CARD_PLOTS, plots = 0, dry = 0, fresh = 0, sea = 0, f2 = Long.MAX_VALUE, s2 = Long.MAX_VALUE;
            for (long dy = -r; dy <= r; dy++) {
                for (long dx = -r; dx <= r; dx++) {
                    long d2 = dx * dx + dy * dy;
                    if (d2 > r * r) continue;
                    byte g = w.terrainAt(c.siteX() + dx, c.siteY() + dy);
                    plots++;
                    if (g == World.SALT) { sea++; s2 = Math.min(s2, d2); }
                    else if (g == World.FRESH) { fresh++; f2 = Math.min(f2, d2); }
                    else dry++;
                }
            }
            check(name + ": the disc counted again plot by plot is the card's, and dry, fresh and sea add up to it",
                    plots == c.plots() && dry == c.dry() && fresh == c.fresh() && sea == c.sea() && c.dry() + c.fresh() + c.sea() == c.plots());
            boolean near = (f2 == Long.MAX_VALUE ? Double.isNaN(c.freshKm()) : c.freshKm() == Math.sqrt(f2) * World.PLOT_M / 1000)
                    && (s2 == Long.MAX_VALUE ? Double.isNaN(c.seaKm()) : c.seaKm() == Math.sqrt(s2) * World.PLOT_M / 1000);
            check(name + ": ...and so are the nearest fresh water and sea (none within reach: none)", near);
            int row = w.provinceAt(c.siteX(), c.siteY());
            boolean mult = row == c.provinceRow() && w.geology().kindOf(row) == c.kind();
            for (Resource res : Resource.values()) {
                mult &= res.inFields() ? c.multiplier(res) == w.geology().multiplierAt(res, row, c.siteLat(), c.siteLon()) : Double.isNaN(c.multiplier(res));
            }
            check(name + ": its province and every multiplier are World's and Geology's, forest none", mult);
            double[] iron = bruteIron(w, c.siteX(), c.siteY(), c.ironKm());
            report(name + ": the nearest iron is the nearest of every field in the cells round it, and its way the compass's", iron != null
                            && iron[0] * World.PLOT_M / 1000 == c.ironKm() && c.ironWay() != null,
                    String.format("%.1f km %s", c.ironKm(), c.ironWay()));
            double[] study = STUDY.get(name);
            out.printf("      %s: %.0f m from the spot, %s; %.1f%% dry, %.1f%% river and lake, %.1f%% sea within 5 km (the study %s / %s / %s);"
                            + " fresh water %s km, the sea %s km (the study %s / %s); %s, a %s: iron x%.2f, oil x%.2f, coal x%.2f; iron %.1f km %s; %.0f ms%n",
                    name, c.snappedM(), c.near(), 100 * c.dryShare(), 100 * c.freshShare(), 100 * c.seaShare(),
                    figure(study[0]), figure(study[1]), figure(study[2]), km(c.freshKm()), km(c.seaKm()), figure(study[3]), figure(study[4]),
                    c.provinceName(), c.kind().label(), c.multiplier(Resource.IRON), c.multiplier(Resource.OIL), c.multiplier(Resource.COAL),
                    c.ironKm(), c.ironWay(), ms);
        }
    }

    static String figure(double v) { return Double.isNaN(v) ? "-" : String.format("%.1f", v); }

    static String km(double v) { return Double.isNaN(v) ? "none" : String.format("%.2f", v); }

    /** The nearest iron field's centre to a plot by brute force: every field in every cell within the card's distance and two cells more, {plots away}; null for none. */
    static double[] bruteIron(World w, long x, long y, double cardKm) {
        int c0 = World.cellOf(x, y), cx = c0 % World.CELLS, cy = c0 / World.CELLS;
        int k = Double.isNaN(cardKm) ? EarthSite.IRON_RINGS : (int) Math.ceil(cardKm * 1000 / World.PLOT_M / World.CELL) + 2;
        double best = Double.POSITIVE_INFINITY;
        for (int j = cy - k; j <= cy + k; j++) {
            for (int i = cx - k; i <= cx + k; i++) {
                if (i < 0 || j < 0 || i >= World.CELLS || j >= World.CELLS) continue;
                for (Deposit f : w.fieldsInCell(j * World.CELLS + i, Resource.IRON)) {
                    best = Math.min(best, Math.hypot(f.x() + 0.5 - (x + 0.5), f.y() + 0.5 - (y + 0.5)));
                }
            }
        }
        return Double.isInfinite(best) ? null : new double[] { best };
    }

    /* =====================================================================
       3. A SPOT IN THE SEA IS REFUSED (and on the full file: ice, a desert)
       ===================================================================== */
    static void refused(String id, Map<String, Spot> spots, EarthData file) {
        out.println("\n--- 3. a spot in the sea, or on ice, is refused; a desert, or no water near, is warned of ---");
        // The North Sea west of Rotterdam, 45 km off the coast, inside the fixture's tiles.
        Spot r = spots.get("Rotterdam");
        World.Key sea = World.Key.earth(SEED, r.lat() + 0.25, r.lon() - 1.2, id);
        EarthSite.Card c = EarthSite.measure(sea);
        report("fixture: a spot in the North Sea - the card has no site, and says so", !c.hasSite() && EarthSite.NO_SITE_WORDS.equals(c.refusal()) && !c.foundable(),
                String.valueOf(c.refusal()));
        Founding f = Founding.defaults().withWorldKey(sea);
        check("...the founding's problem() says the same", EarthSite.NO_SITE_WORDS.equals(f.problem()));
        Game g = new Game(GameFiles.scratch("earthfounding-sea"));
        boolean refusedIt = false;
        PrintStream real = System.out;
        System.setOut(quiet);
        try {
            g.newGame(f);
        } catch (IllegalArgumentException e) {
            refusedIt = e.getMessage().contains(EarthSite.NO_SITE_WORDS);
        } finally {
            System.setOut(real);
        }
        check("...and the game will not found it", refusedIt);
        World.Key paris = World.Key.earth(SEED, spots.get("Paris").lat(), spots.get("Paris").lon(), id);
        EarthSite.Card p = EarthSite.measure(paris);
        check("fixture: Paris is neither ice nor desert, and has the Seine: no warning", !p.ice() && !p.desert() && p.warnings().isEmpty() && p.foundable());
        if (file == null) {
            out.println("earth-2.bin not named: ice and desert, which the fixture's windows hold none of, were not tried");
            return;
        }
        EarthSite.Card ice = EarthSite.measure(World.Key.earth(SEED, 72, -40, EarthData.EARTH_2));
        report("the full file: the middle of Greenland's sheet is refused, on ice", ice.hasSite() && ice.ice() && EarthSite.ICE_WORDS.equals(ice.refusal())
                && EarthSite.ICE_WORDS.equals(Founding.defaults().onEarth(72, -40).problem()), String.valueOf(ice.refusal()));
        EarthSite.Card sand = EarthSite.measure(World.Key.earth(SEED, 23, 10, EarthData.EARTH_2));
        report("...the Sahara is foundable, warned that it is a desert and has no water near", sand.foundable() && sand.desert()
                && sand.warnings().contains(EarthSite.DESERT_WORDS) && sand.warnings().contains(EarthSite.NO_WATER_WORDS), String.valueOf(sand.warnings()));
        EarthSite.Card fiji = EarthSite.measure(World.Key.earth(SEED, -17.0, 180.0, EarthData.EARTH_2));
        report("...and a spot by the sea with no river or lake within 5 km is warned it drinks the sea", fiji.foundable()
                && Double.isNaN(fiji.freshKm()) && !Double.isNaN(fiji.seaKm()) && fiji.warnings().contains(EarthSite.NO_FRESH_WORDS),
                String.valueOf(fiji.warnings()));
        EarthSite.Card mid = EarthSite.measure(World.Key.earth(SEED, 30, -30, EarthData.EARTH_2));
        check("...and the middle of the Atlantic is refused", !mid.hasSite() && EarthSite.NO_SITE_WORDS.equals(mid.refusal()));
    }

    /* =====================================================================
       4. SEARCH AND RANDOM SPOT
       ===================================================================== */
    static void search(EarthData data, String id) {
        out.println("\n--- 4. search, and the spot the seed picks ---");
        List<EarthData.Place> places = data.places();
        check("fixture: the fixture carries the data's places whole", places.size() > 7000);
        boolean own = true;
        int tried = 0;
        for (int i = 0; i < places.size(); i += 97) {
            EarthData.Place p = places.get(i);
            List<EarthData.Place> hits = EarthSite.search(data, p.name().toUpperCase(java.util.Locale.ROOT), EarthSite.SEARCH_HITS);
            own &= !hits.isEmpty() && EarthSite.fold(hits.get(0).name()).equals(EarthSite.fold(p.name())) && hits.contains(p) == hits.indexOf(p) >= 0;
            tried++;
        }
        check("a place's own name, in capitals, finds a place of that name first (" + tried + " places)", own);
        EarthData.Place accented = null;
        for (EarthData.Place p : places) {
            if (!p.name().equals(p.ascii()) && !EarthSite.fold(p.name()).equals(p.name().toLowerCase(java.util.Locale.ROOT))) { accented = p; break; }
        }
        boolean plain = accented != null && EarthSite.search(data, accented.ascii(), EarthSite.SEARCH_HITS).contains(accented)
                && EarthSite.search(data, EarthSite.fold(accented.name()), EarthSite.SEARCH_HITS).contains(accented);
        report("fixture: a name with accents is found by its plain spelling and without them", plain, accented == null ? "none" : accented.name() + " / " + accented.ascii());
        boolean ranked = true;
        for (String q : List.of("new", "san", "port", "york", "x", "berg")) {
            List<EarthData.Place> hits = EarthSite.search(data, q, EarthSite.SEARCH_HITS);
            int last = -1, lastIndex = -1;
            ranked &= hits.size() <= EarthSite.SEARCH_HITS && !hits.isEmpty();
            for (EarthData.Place p : hits) {
                int rank = EarthSite.rankOf(p, EarthSite.fold(q)), at = places.indexOf(p);
                ranked &= rank < 4 && (rank > last || (rank == last && at > lastIndex));
                last = rank;
                lastIndex = at;
            }
        }
        check("the hits are ranked whole, begun, a word begun, within, each group in the data's order, at most SEARCH_HITS", ranked);
        check("...and nothing typed finds nothing", EarthSite.search(data, "  ", 8).isEmpty() && EarthSite.search(data, null, 8).isEmpty());

        java.util.Set<String> picked = new java.util.HashSet<>();
        boolean same = true, pass = true;
        for (long seed = 1; seed <= 8; seed++) {
            EarthData.Place a = EarthSite.randomPlace(data, id, seed), b = EarthSite.randomPlace(data, id, seed);
            same &= a != null && a == b;
            pass &= a != null && EarthSite.passes(World.Key.earth(seed, a.lat(), a.lon(), id));
            if (a != null) picked.add(a.name());
        }
        check("Random spot: the same seed picks the same place, and it passes the site test", same && pass);
        report("...and eight seeds pick more than one place", picked.size() > 1, picked.size() + " places");
    }

    /* =====================================================================
       5. THE SEA'S DEPTH
       ===================================================================== */
    static void depth(String id, Spot r, EarthData file) {
        out.println("\n--- 5. the sea's depth on Earth: the coast's distance for the elevation it lacks ---");
        World w = World.of(World.Key.earth(SEED, r.lat(), r.lon(), id));
        EarthTerrain e = w.earthTerrain();
        EarthData data = e.data();
        long y = w.foundingY();
        int seaPlots = 0, landPlots = 0;
        boolean rule = true;
        double deepest = 0;
        String worst = "";
        for (int km = 0; km <= 80; km += 4) {
            long x = w.foundingX() - Math.round(km * 1000.0 / World.PLOT_M);
            double depth = w.depthAt(x, y);
            if (w.terrainAt(x, y) != World.SALT) {
                landPlots++;
                rule &= depth == 0;
                continue;
            }
            seaPlots++;
            double[] ll = new double[2];
            e.projection().latLonOfPlot(x + 0.5, y + 0.5, ll);
            double d = bruteCoast(data, ll[0], ll[1]);
            double want = World.SHELF_BREAK_M * Math.max(World.PLOT_M, d) / EarthTerrain.SHELF_WIDTH_M;
            boolean ok = depth > 0 && Math.abs(depth - want) <= 1e-9 * want;
            if (!ok) worst = String.format("%d km: %.6f m for %.6f", km, depth, want);
            rule &= ok;
            deepest = Math.max(deepest, depth);
        }
        report("fixture: west of Rotterdam's site, nothing where the plot is not the sea's, and at sea SHELF_BREAK_M x the coast's distance"
                        + " (every cell within reach, by brute force) over SHELF_WIDTH_M, at least a plot's",
                rule && seaPlots >= 5 && landPlots >= 1, seaPlots + " at sea, " + landPlots + " on land, the deepest "
                        + String.format("%.1f m", deepest) + (worst.isEmpty() ? "" : "; " + worst));
        check("the rule's own arithmetic: SHELF_BREAK_M at SHELF_WIDTH_M from the coast, so a platform's 150 m within 58 km",
                World.SHELF_BREAK_M * EarthTerrain.SHELF_WIDTH_M / EarthTerrain.SHELF_WIDTH_M == World.SHELF_BREAK_M
                        && ham.citybuildersim.sectors.Oil.PLATFORM_MAX_DEPTH_M / World.SHELF_BREAK_M * EarthTerrain.SHELF_WIDTH_M / 1000 > 58
                        && ham.citybuildersim.sectors.Oil.PLATFORM_MAX_DEPTH_M / World.SHELF_BREAK_M * EarthTerrain.SHELF_WIDTH_M / 1000 < 59);
        boolean threw = false;
        try { w.shelfTheta(); } catch (UnsupportedOperationException x) { threw = true; }
        check("...and the elevation field's shelf, which Earth has none of, is still refused", threw);
        if (file == null) {
            out.println("earth-2.bin not named: the shelf's width was not measured again");
            return;
        }
        boolean far = true;
        double[][] points = { { 52.5, 3.0 }, { 55.0, 3.0 }, { -32.0, 115.5 }, { 20, -40 } };
        for (double[] p : points) far &= EarthTerrain.coastDistanceM(file, p[0], p[1], Double.POSITIVE_INFINITY) == bruteCoast(file, p[0], p[1]);
        check("the full file: the coast's distance is the brute force's at four points, the North Sea's to the mid-Atlantic's", far);
        long t0 = System.nanoTime();
        double width = EarthTerrain.measureShelfWidthM(file);
        int seaSamples = EarthTerrain.shelfSeaSamples(file);
        report("...and the shelf's width measured again over the Earth's sea is SHELF_WIDTH_M, to the metre it is rounded to",
                Math.abs(width - EarthTerrain.SHELF_WIDTH_M) <= 0.5,
                String.format("%.1f m over %,d of %,d samples the sea's (%.1f%%), %.1f s", width, seaSamples, EarthTerrain.SHELF_SAMPLES,
                        100.0 * seaSamples / EarthTerrain.SHELF_SAMPLES, (System.nanoTime() - t0) / 1e9));
    }

    /** How far past the search's answer the brute force reads, degrees: 2, so a cell the search missed nearer than its answer lies well inside what is read. */
    static final double BRUTE_MARGIN_DEG = 2;

    /**
     * The great-circle distance to the centre of the nearest 15" cell whose
     * sea share is under the coast's level, by brute force: every cell of
     * every 1-degree tile whose centre lies within the search's answer and
     * BRUTE_MARGIN_DEG more - a nearer cell than the search found would lie
     * in one of them, so the two agree only if the search missed none.
     */
    static double bruteCoast(EarthData d, double lat, double lon) {
        double within = EarthTerrain.coastDistanceM(d, lat, lon, Double.POSITIVE_INFINITY) / EarthProj.RADIUS_M + StrictMath.toRadians(BRUTE_MARGIN_DEG);
        EarthData.Layer sea = d.layer("sea");
        int cpd = sea.cellsPerDegree;
        double f0 = StrictMath.toRadians(lat), l0 = StrictMath.toRadians(lon);
        double px = StrictMath.cos(f0) * StrictMath.cos(l0), py = StrictMath.cos(f0) * StrictMath.sin(l0), pz = StrictMath.sin(f0);
        double most = -2;
        double[] cl = new double[cpd], sl = new double[cpd];
        for (int t = 0; t < EarthData.TILES; t++) {
            int lat0 = EarthData.tileLat0(t), lon0 = EarthData.tileLon0(t);
            if (EarthProj.distanceM(lat, lon, lat0 + 0.5, lon0 + 0.5) / EarthProj.RADIUS_M > within + StrictMath.toRadians(1)) continue;
            int e = sea.entries[t];
            if (e <= 0 && -e >= EarthTerrain.COAST_BELOW) continue;
            byte[] g = e > 0 ? d.raw("sea", t) : null;
            for (int c = 0; c < cpd; c++) {
                double l = StrictMath.toRadians(lon0 + (c + 0.5) / cpd);
                cl[c] = StrictMath.cos(l);
                sl[c] = StrictMath.sin(l);
            }
            for (int r = 0; r < cpd; r++) {
                double f = StrictMath.toRadians(lat0 + 1 - (r + 0.5) / cpd), cf = StrictMath.cos(f), sf = StrictMath.sin(f);
                for (int c = 0; c < cpd; c++) {
                    if (g != null && (g[r * cpd + c] & 0xFF) >= EarthTerrain.COAST_BELOW) continue;
                    double dot = cf * (cl[c] * px + sl[c] * py) + sf * pz;
                    if (dot > most) most = dot;
                }
            }
        }
        return StrictMath.acos(Math.min(1, most)) * EarthProj.RADIUS_M;
    }

    /* =====================================================================
       6. AN EARTH CITY, FOUNDED, PLAYED AND AT SEA
       ===================================================================== */
    static void aCity(String id, Spot r) {
        out.println("\n--- 6. an Earth city founded, played, and drilling at sea ---");
        World.Key key = World.Key.earth(SEED, r.lat(), r.lon(), id);
        Founding f = Founding.defaults().withWorldKey(key);
        check("fixture: Rotterdam's spot is a founding with no problem, on Earth", f.problem() == null && f.isOnEarth()
                && f.getWorldKind() == World.Kind.EARTH && f.getEarthLat() == r.lat() && f.getEarthLon() == r.lon() && id.equals(f.getEarthData()));
        GameFiles files = GameFiles.scratch("earthfounding-city");
        Game g = new Game(files);
        String[] threw = new String[1];
        quietly(() -> {
            try {
                g.newGame(f);
                g.setCashForTest(Founding.WEALTHY_CASH);
                BuildingManager b = g.getBuildingManager();
                String[] names = { "House", "Convenience Store", "Paved Road", "Coal Power Plant", "Water Treatment Plant", "Construction Depot" };
                int[] counts = { 200, 4, 10, 1, 1, 2 };
                for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
                g.simulateMonths(2);
            } catch (RuntimeException e) {
                threw[0] = e.toString();
            }
        });
        World w = World.of(key);
        report("the game founds it: its World the Earth's of that key, its land the snap's site, two months played",
                threw[0] == null && g.getWorld() == w && w.isEarth() && g.getCityLand().key().equals(key)
                        && g.getCityLand().siteX() == w.foundingX() && g.getCityLand().siteY() == w.foundingY() && g.getMonth() == 3,
                threw[0] == null ? "month " + g.getMonth() : threw[0]);
        if (threw[0] != null) return;

        // The nearest oil field in the sea a platform can stand in.
        long sx = w.foundingX(), sy = w.foundingY(), cx = sx / World.CELL, cy = sy / World.CELL;
        Deposit field = null;
        double far = Double.MAX_VALUE;
        for (long yy = cy - 1; yy <= cy + 1; yy++) {
            for (long xx = cx - 1; xx <= cx + 1; xx++) {
                for (Deposit d : w.fieldsInCell((int) (yy * World.CELLS + xx), Resource.OIL)) {
                    double depth = w.depthAt(d.x(), d.y()), dist = Math.hypot(d.x() - sx, d.y() - sy);
                    if (depth > 0 && depth <= ham.citybuildersim.sectors.Oil.PLATFORM_MAX_DEPTH_M && dist < far) {
                        far = dist;
                        field = d;
                    }
                }
            }
        }
        Deposit sea = field;
        report("fixture: an oil field in the North Sea near the site, shallow enough for a platform", sea != null,
                sea == null ? "none" : String.format("%d sites, %,.0f t, %.1f m deep, %.1f km out", sea.sites(), sea.amount(),
                        w.depthAt(sea.x(), sea.y()), far * World.PLOT_M / 1000));
        if (sea == null) return;
        LandManager lm = g.getLandManager();
        int[] bought = new int[1];
        // Since 0.8.12 (batch EG1, Jerus 2026-10-20: a harness that asserted the old deposits' outcome has its fixture remade to cause its
        // condition on the new ones) the nearest oil field a platform can stand in is 55 km out, not 10 (EG1 draws the Earth's fields a third as
        // many and three times wider, and oil by the known petroleum), and the 200 purchases the loop was bounded by no longer reach it. It
        // buys toward the field until the field is in the city's ground, bounded by the field's distance from the site in plots - the
        // edge moves toward it by far more than a plot a purchase (242 purchases, blocks of 4 plots to 512, carry it 55 km) - so a
        // field the loop cannot reach is still a FAIL and never a hang.
        long reach = Math.max(Math.abs(sea.x() - sx), Math.abs(sea.y() - sy));
        quietly(() -> {
            try {
                while (!g.getCityLand().ownsPlot(sea.x(), sea.y()) && bought[0] < reach) {
                    LandParcel p = MiningCheck.nearestOffer(lm.getMarket(), sea.x(), sea.y());
                    if (p == null) break;
                    g.setCashForTest(g.getCash() + p.localPrice(g.getForeignAccounts().getRate()));
                    if (!g.buyLandParcel(p.getId())) break;
                    bought[0]++;
                }
            } catch (RuntimeException e) {
                threw[0] = e.toString();
            }
        });
        long seaSites = 0;
        for (List<CityLand.Held> hs : g.getCityLand().heldFields(Resource.OIL)) {
            for (CityLand.Held h : hs) if (w.depthAt(h.field().x(), h.field().y()) > 0) seaSites += h.sites();
        }
        List<LandManager.SeaField> listed = threw[0] == null ? lm.seaFields(Resource.OIL) : List.of();
        boolean onList = false;
        for (LandManager.SeaField sf : listed) onList |= sf.field().x() == sea.x() && sf.field().y() == sea.y() && sf.depth() == w.depthAt(sea.x(), sea.y());
        report("the city buys the ground to it: its sites the sea's, the field on the sea's list at World's depth",
                threw[0] == null && g.getCityLand().ownsPlot(sea.x(), sea.y()) && lm.getSites(Resource.OIL, false) == seaSites
                        && seaSites >= sea.sites() && onList,
                threw[0] != null ? threw[0] : bought[0] + " purchase(s) of at most " + reach + " (the field's distance in plots), "
                        + lm.getSites(Resource.OIL, false) + " sea sites, " + lm.getSites(Resource.OIL, true) + " dry");
        if (threw[0] != null) return;
        BuildingsTemplate jacket = g.getBuildingManager().getTemplateByName("Offshore Platform"),
                well = g.getBuildingManager().getTemplateByName("Platform Well");
        Game.BuildResult[] built = new Game.BuildResult[2];
        String[] page = new String[1];
        quietly(() -> {
            try {
                g.setCashForTest(Founding.WEALTHY_CASH);
                built[0] = g.buildStack(jacket, 1, true);
                built[1] = g.buildStack(well, Math.min(sea.sites(), 4), true);
                g.simulateMonths(2);
                OilView.View v = OilView.of(g);
                List<String> lines = new ArrayList<>();
                for (OilView.PlatformLine pl : OilView.platformLines(v)) lines.add(pl.name() + ": " + pl.words());
                page[0] = String.join(" | ", lines);
            } catch (RuntimeException e) {
                threw[0] = e.toString();
            }
        });
        int inSlots = threw[0] == null ? g.getSectors().oil().platformWellsInSlots() : 0;
        report("a platform and its wells stand on it, the city plays two months more, the investors planning at sea, and the oil's page"
                        + " reads the platform's depth - nothing throws",
                threw[0] == null && built[0] == Game.BuildResult.SUCCESS && built[1] == Game.BuildResult.SUCCESS && inSlots > 0
                        && page[0] != null && page[0].contains(String.format("%.0f m deep", w.depthAt(sea.x(), sea.y()))),
                threw[0] != null ? threw[0] : inSlots + " well(s) in slots; " + page[0]);
        LongPlaytest.cleanUp(files.getDirectory().getParent());
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }
}
