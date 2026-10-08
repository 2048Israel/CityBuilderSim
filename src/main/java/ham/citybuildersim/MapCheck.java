package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter keeps the mockup's rules and paints the same pixels from the same inputs, the sidecar comes back byte for byte, and a screen costs what the screen holds, never what the city does - at Jerus's size and at five and ten billion people.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5, 2.6 and
 * 3). The map is the one part of the city nothing in the model reads, so
 * nothing else would notice it drifting: districts that stopped adding up to
 * the buildings, a building that jumped across the city when another was
 * built, a tile that painted differently twice, a sidecar from another save
 * read as this one's, or a screen that slowed as the city grew - the failure
 * the design was drawn to avoid; and (J3b) a city of half a million drawn as
 * scattered dots on a field, one dot for each apartment block, which is what
 * J3's first render of Jerus's city showed. Headless: no JavaFX (the view is
 * J4's).
 *
 * What it has to prove:
 *   1. a city's map adds up: every month of a 120-month city played as the
 *      playtest plays it (land bought, built, demolished), its districts sum
 *      to the model's count of every type and its pyramid's top to its
 *      districts; a district recounted after a purchase holds what a map
 *      drawn afresh measures - a whole iron field's sites among it, wherever
 *      they lie (0.7.64); its mines stand on owned iron sites; a demolition
 *      comes off the outermost district holding the type; and (0.7.64) at
 *      every DRAWN_EVERY-th month, every tile painted, the map draws exactly
 *      what the city has - each type's buildings as many as the model's, a
 *      mine once on its site, every road plot of every kind - none without
 *      a plot;
 *   2. nothing placed moves: one more building changes one district's count
 *      by one, inner types in the first district with room, outer types in
 *      the last - and one tile of the map by one; one fewer takes its own
 *      kind off one tile and moves nothing else but a smaller kind into the
 *      room it frees (since 0.7.64 the deal keeps a district on its own
 *      tiles; J3b spread it over the tiles about it);
 *   3. the painter keeps the mockup's rules on Jerus's city: shared ports
 *      agree - since 0.7.72 each edge's crossing, the same pair of plots read
 *      from either tile - and no road meets a tile's border but where the
 *      next tile's road meets it, every tile lays
 *      exactly the road plots of each kind dealt to it and no bridge longer
 *      than its kind allows nor over the sea - across the founding river a
 *      street's of its own kind, paved and (since 0.7.77) gravel - the same
 *      inputs paint the same pixels (and the far view's blocks), and one more model building in a
 *      tile with room for it (as the deal reckons room: since 0.7.72 less its
 *      track) moves none of those placed before it (0.7.64; a
 *      median of none of the others before, at J3b's 30% built) and never
 *      draws fewer of anything;
 *   4. the sidecar round-trips byte for byte through a save and a load, the
 *      next month plays to the same map, a stale or missing sidecar draws the
 *      map again canonically, a city with no map saves none; and at five and
 *      ten billion it is written and read back the same;
 *   5. the cost follows the screen: a 1,389 x 868 L0 screen (SCREEN_TILES)
 *      paints and rasters in no more than SCREEN_MS (the design's 80 ms,
 *      derived from 38 at its measured 0.19 ms a tile) at Jerus's city x 1,
 *      x 9,814 (5B), x 10,000 and x 19,629 (10B), each within SCREEN_RATIO of
 *      the 5B copy's, the first whose screen is all city (until 0.7.64, of
 *      x 1's: section 5's note); and a month's change at 5B and 10B in no
 *      more than RECONCILE_MS (the design's 5 ms; measured 0.76);
 *   6. the city is drawn as what it has (0.7.64, batch L2; J3b's homes from
 *      people and workplaces from jobs gone): a new default city as founded,
 *      a month on and a year on draws its own buildings and nothing else -
 *      at its founding the bank and bare ground, no road, no highway; on the
 *      dense screen every building dealt is drawn, on its own type's land
 *      but for the few no free box of the tile could hold (SHRUNK_MOST),
 *      and the screen is built as full as its districts are used;
 *   7. the view's pure half (J4: MapFrame, LandMap, MapTiles, Game's draft):
 *      a screen point and its plot go back and forth, a notch of the wheel
 *      zooms by ZOOM_STEP with the plot under the pointer kept, the zoom
 *      stops at 16 px a plot and the whole world, the levels change at the
 *      mockup's thresholds and a screen asks for the tiles it shows; what a
 *      click picks on the played city - the centre, a purchase, an offer, the
 *      world - agrees with the model's grid (CityLand.holdingOf()) and
 *      GridOffers' sides and places on every plot tried and with the
 *      rectangles the map outlines (the lanes' owns() and sideLane() until
 *      0.7.66); the map drawn on a copy of
 *      the land on another thread is the map drawn on the city, and one with
 *      a month between is caught up; a tile is restamped only when what it
 *      is painted from moved; the view's own per-tile path holds section 5's
 *      bounds; and every cache fits MapTiles.BUDGET_MB at the design's size;
 *      and (0.7.69) the overlay on the block grid: the city's edge as whole
 *      straight runs with its ground on their right, drawn crisp on whole
 *      pixels just inside it with no gap at a corner; an offer's box on its
 *      pixel lines, shared with an offer beside it, drawn over all of a view
 *      it is larger than; its hatch on its free plots exactly; the block
 *      lines from 6 px a block; every offer at the opening zoom numbered;
 *   8. the network (0.7.72, batch N3; Jerus: "to make the map generation
 *      prettier"), on Jerus's city x 1, every tile: its roads one network,
 *      no road apart; no two + junctions - streets crossing, road on four
 *      sides and none on the corners - nearer than JUNCTION_APART (about
 *      eight houses' length); each district's highways one straight run,
 *      turning only where the sea, the city's edge or a mine stops it,
 *      crossing at most once and meeting a district's edge on its main
 *      line; every building within REACH of a road but where the ground
 *      near its own tile's roads is all built, none on a tile without road;
 *      every plot of the model's track drawn, each district's track one
 *      line; and with rail yards added, each beside its track on the tile of
 *      it nearest a mine that had room for it; and (0.7.77, batch N5) a river
 *      parts gravel streets no more than paved ones - Jerus's city x 1 at the
 *      founding river, its streets all gravel, against its paved twin.
 */
public class MapCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-78s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Months the city of section 1 is played: 120 (the design's). */
    static final int MONTHS = 120;

    /** The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. */
    static final int IRON_AT = MONTHS / 3;

    /**
     * Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2
     * of dry ground): every building type's count, by id - 14,214 buildings,
     * the design's own fixture for the map's sizes; 8,204 since 0.7.71, its
     * childcare restated (ids 15 to 17): his 6,079 Home Daycares and 170
     * Childcare Centres held 86,032 places, which the 0.7.71 advice puts in
     * Large Childcare Centres at that size - 239 of them, 86,040 places (kept
     * as their type, the 6,079 would be Small Childcare Centres, 486,320
     * places for 509,455 people, on more ground than he owns; runs/fixN2-notes.md).
     */
    static final long[] JERUS_COUNTS = { 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114,
            1, 2, 0, 27, 12, 0, 8, 1, 1, 5, 385, 6, 47, 47, 40, 7, 6, 4, 2, 1, 2, 7, 37, 0, 2, 0, 2, 0, 0, 0, 6, 113, 106,
            1, 5, 0, 0, 14, 114, 148, 27, 11, 2, 11, 0, 0, 7, 114, 0, 0, 133, 0, 1120, 0 };

    /** ...his people. */
    static final long JERUS_PEOPLE = 509_455;

    /** ...and his dry ground, in km2. */
    static final double JERUS_KM2 = 89.63;

    /** His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. */
    static final double JERUS_FILL = 0.92;

    /** ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). */
    static final double JERUS_FOOTPRINT_KM2 = 82.273;

    /**
     * The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his
     * buildings' land on this catalogue over theirs then, so the copies keep
     * his density (JERUS_FILL) whatever the catalogue: with his childcare
     * restated (JERUS_COUNTS) 87.2 km2; at 0.7.70's catalogue and his own
     * counts, his 89.63.
     */
    static double groundKm2(BuildingVisual.Type[] types) {
        double sqFt = 0;
        for (int id = 0; id < JERUS_COUNTS.length && id < types.length; id++) {
            if (types[id] != null) sqFt += JERUS_COUNTS[id] * (double) types[id].sqFt();
        }
        return JERUS_KM2 * (sqFt / LandManager.SQ_FT_PER_KM2) / JERUS_FOOTPRINT_KM2;
    }

    /** The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). */
    static final double[] TIMES = { 1, 9_814, 10_000, 19_629 };

    /** The copy whose screen is all city, at his density, that section 3 paints: x 10,000. */
    static final int DENSE = 2;

    /** The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". */
    static final int SCREEN_ACROSS = 18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN;

    /** The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). */
    static final double SCREEN_MS = 80;

    /** The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). */
    static final double SCREEN_RATIO = 1.5;

    /** The design's bound on a month's change at 10B, in ms (measured 0.76). */
    static final double RECONCILE_MS = 5;

    /** Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). */
    static final int PX = 4;

    /** Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. */
    static final int WARM_ROUNDS = 3, TIMED_ROUNDS = 5;

    /** How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. */
    static final double DRY_PLACE = 0.97;

    /** The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). */
    static final int DRAWN_EVERY = 30;

    /**
     * The most of the dense screen's buildings drawn smaller than their own
     * land, because no free box of their tile held it: a tenth (star, 0.7.64).
     * The screen is dealt about 95% of its free plots in whole plots - Jerus's
     * buildings at 92% of his ground (JERUS_FILL), the small ones rounded up
     * to a plot - its roads a fifth of them, and what no box holds is drawn
     * as the largest square left: 9.2% to 9.6% measured (L2's scratch;
     * Jerus's own city at month 416, 7 of 2,340; his city of 0.7.49, 4.7%).
     */
    static final double SHRUNK_MOST = 0.10;

    public static void main(String[] args) throws Exception {
        boolean quietRun = Arrays.asList(args).contains("-q");
        if (quietRun) out = new PrintStream(new OutputStream() { @Override public void write(int b) { } }) {
            @Override public PrintStream printf(String f, Object... a) {
                String s = String.format(f, a);
                if (s.endsWith("FAIL\n")) System.out.print(s);
                return this;
            }
        };
        Path root = Files.createTempDirectory("mapcheck");
        Game city = playedCity(root);
        nothingMoves(city);
        Squares sq = new Squares();
        painterRules(sq.maps[DENSE]);
        drawnAsItIs(sq.maps[DENSE]);
        network(sq);
        sidecar(city, root, sq);
        cost(sq);
        viewHalf(city, sq, root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    /* =====================================================================
       1. A CITY'S MAP ADDS UP
       ===================================================================== */

    static Game playedCity(Path root) throws Exception {
        out.println("\n--- 1. a city's map adds up every month: " + MONTHS + " months played as the playtest plays ---");
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        cityFiles = files;
        Game g = new Game(files, LongPlaytest.founding());
        PrintStream real = System.out, was = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        int monthsSummed = 0, pyramidSummed = 0, bought = 0, ironSites = 0, drawnSamples = 0, drawnExact = 0;
        StringBuilder drawnLines = new StringBuilder();
        int startDistricts;
        long sitedWorst = 0, ownedAtFounding = 0;
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            CityMap first = g.getCityMap();
            startDistricts = first.districts().size();
            for (CityMap.District d : first.districts()) ownedAtFounding += d.owned();
            int purchases0 = g.getCityLand().purchases().size();
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            BuildingsTemplate house = g.getBuildingManager().getTemplateByName("House");
            for (int m = 0; m < MONTHS; m++) {
                if (m % 6 == 5) {
                    LongPlaytest.advise(g);
                    LongPlaytest.ensureSchools(g);
                }
                if (m == MONTHS / 2 && house != null) {
                    // A demolition: ten houses retired between two months.
                    g.getBuildingManager().retire(house, 10);
                }
                if (m == IRON_AT && g.getLandManager().getIronDeposits() == 0) {
                    /*
                     * IRON, AND A MINE ON IT (0.7.64). A field goes whole with
                     * the ground holding its centre, so a young city as the
                     * playtest plays it seldom buys one this early (the
                     * playtest's first came in month 135 while a site could
                     * be bought alone, 0.7.58-0.7.63). The fixture buys one
                     * as MiningCheck's does - the richest offer in iron, or
                     * toward the nearest field, the offer nearest it each
                     * time, until an offer holds it, handed its price - and
                     * orders a mine, so the purchase's sites, wherever its
                     * field lays them, are on the map the months after.
                     */
                    MiningCheck.makeRoom(g, 0, true);
                    ironSites = g.getLandManager().getIronDeposits();
                    LongPlaytest.build(g, "Iron Mine", 1);
                }
                LongPlaytest.run(g, 1);
                CityMap map = g.getCityMap();
                if (Arrays.equals(map.totals(), g.getMapCounts())) monthsSummed++;
                long[] top = map.pyramid().root(), sum = new long[CityMap.NODE_WIDTH];
                for (CityMap.District d : map.districts()) {
                    long[] v = map.pyramid().vector(d);
                    for (int c = 0; c < v.length; c++) sum[c] += v[c];
                }
                if (Arrays.equals(top, sum)) pyramidSummed++;
                long sited = 0;
                for (CityMap.District d : map.districts()) sited += d.sited == null ? 0 : Arrays.stream(d.sited).sum();
                sitedWorst = Math.max(sitedWorst, sited - map.ownedSites(Resource.IRON));
                if ((m + 1) % DRAWN_EVERY == 0) {
                    // Every tile painted: what the map draws, against what the city has (0.7.64).
                    Drawn dr = drawn(map);
                    long[] model = g.getMapCounts();
                    boolean exact = dr.sameAs(model, map.types());
                    drawnSamples++;
                    if (exact) drawnExact++;
                    drawnLines.append(String.format("      month %d: %,d buildings of %d kinds drawn (the model %,d), %,d road plots (the model %,d);"
                                    + " %d without a plot, %d drawn smaller%n", g.getMonth(), dr.total(), dr.kinds(), modelBuildings(model, map.types()),
                            dr.roadPlots(), modelRoadPlots(model, map.types()), dr.dropped, dr.shrunk));
                }
            }
            bought = g.getCityLand().purchases().size() - purchases0;
        } finally {
            System.setOut(real);
            LongPlaytest.out = was;
        }
        CityMap map = g.getCityMap();
        out.printf("      month %d: %,d people, %d purchases, %d districts (%d at founding), %,d buildings%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), bought, map.districts().size(), startDistricts,
                Arrays.stream(map.totals()).sum());
        long ownedNow = 0;
        for (CityMap.District d : map.districts()) ownedNow += d.owned();
        out.printf("      owned dry plots on the map: %,d at founding, %,d now%n", ownedAtFounding, ownedNow);
        check("fixture: the city bought land in those months, and its districts were recounted larger",
                bought > 0 && ownedNow > ownedAtFounding);
        check("its districts sum to the model's count of every type, every month", monthsSummed == MONTHS);
        check("...and its pyramid's top to its districts, every month", pyramidSummed == MONTHS);
        check("...and no month dropped the map", g.getMapFailures() == 0);
        CityMap fresh = CityMap.canonical(g.getCityLand(), g.getLandManager()::remainingByHolding, g.getMapTypes(),
                g.getMapCounts());
        boolean sameGround = fresh.districts().size() == map.districts().size();
        for (CityMap.District d : map.districts()) {
            CityMap.District e = fresh.district(d.dx, d.dy);
            sameGround &= e != null && e.owned() == d.owned() && e.sites(Resource.IRON) == d.sites(Resource.IRON);
        }
        check("every district recounted after a purchase holds what a map drawn afresh measures", sameGround);
        long mines = g.getMapCounts()[mineId(g)];
        out.printf("      %d mines on %d owned iron sites (%d bought in month %d)%n", mines, map.ownedSites(Resource.IRON),
                ironSites, IRON_AT);
        check("fixture: the city owns iron sites and stands mines on them", mines > 0 && map.ownedSites(Resource.IRON) > 0);
        check("the map's owned iron sites are the land's: a whole field to the holding of its centre",
                map.ownedSites(Resource.IRON) == g.getLandManager().getIronDeposits());
        check("its mines stand on owned iron sites, never more of them on sites than there are", sitedWorst <= 0);
        out.print(drawnLines);
        check("at every " + DRAWN_EVERY + "th month the map draws exactly what the city has: each type's buildings, every road plot of"
                + " each kind, every plot of its track, a mine once on its site", drawnSamples == MONTHS / DRAWN_EVERY && drawnExact == drawnSamples);
        return g;
    }

    /* ---------------------------------------------------------------------
       WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64)
       --------------------------------------------------------------------- */

    /** What the painted map draws over every tile of a map's districts. */
    static final class Drawn {
        /** Buildings by type id: a mine or well on a site once, however many tiles its site spans. */
        long[] buildings;
        /** Road plots laid by kind [0, gravel, paved, highway]. */
        final long[] roads = new long[4];
        /** The railway's track laid, in plots (0.7.72). */
        long rail;
        /** Buildings dealt and drawn on no plot; drawn smaller than their own land; road plots dealt and not laid; tiles painted. */
        long dropped, shrunk, roadShort, tiles;

        long total() { return Arrays.stream(buildings).sum(); }
        int kinds() { int n = 0; for (long b : buildings) if (b > 0) n++; return n; }
        long roadPlots() { return roads[1] + roads[2] + roads[3]; }

        /** Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn), every road kind's plots, every plot of its railway's track (0.7.72), none without a plot. */
        boolean sameAs(long[] model, BuildingVisual.Type[] types) {
            boolean same = dropped == 0 && roadShort == 0;
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null) continue;
                long want = t < model.length ? model[t] : 0;
                if (types[t].drawn()) same &= buildings[t] == want;
            }
            for (int kind = BuildingVisual.GRAVEL; kind <= BuildingVisual.HIGHWAY; kind++) same &= roads[kind] == roadPlotsOf(model, types, kind);
            long track = 0;
            for (int t = 0; t < types.length && t < model.length; t++) if (types[t] != null && types[t].track()) track += model[t] * BuildingVisual.cells(types[t]);
            return same && rail == track;
        }
    }

    /** Paints every tile of every district on a map and counts what is drawn. */
    static Drawn drawn(CityMap m) {
        Drawn d = new Drawn();
        d.buildings = new long[m.types().length];
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        java.util.Set<Long> sites = new java.util.HashSet<>();
        for (CityMap.District dist : m.districts()) {
            for (int k = 0; k < CityMap.TILES; k++) {
                long tx = (m.baseDX() + dist.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE;
                long ty = (m.baseDY() + dist.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                m.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                d.tiles++;
                d.dropped += p.dropped;
                d.shrunk += p.shrunk;
                d.roadShort += p.roadShort;
                for (int kind = 1; kind < 4; kind++) d.roads[kind] += p.laid[kind];
                d.rail += p.railLaid;
                for (int b = 0; b < p.buildings; b++) {
                    if (p.bsite[b] >= 0) {
                        if (sites.add(in.siteKey[p.bsite[b]])) d.buildings[p.btype[b]]++;
                    } else d.buildings[p.btype[b]]++;
                }
            }
        }
        return d;
    }

    /** The model's buildings the map draws: every type but the roads. */
    static long modelBuildings(long[] model, BuildingVisual.Type[] types) {
        long n = 0;
        for (int t = 0; t < types.length && t < model.length; t++) if (types[t] != null && types[t].drawn()) n += model[t];
        return n;
    }

    /** The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). */
    static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind) {
        long n = 0;
        for (int t = 0; t < types.length && t < model.length; t++) {
            if (types[t] != null && types[t].road() == kind) n += model[t] * BuildingVisual.cells(types[t]);
        }
        return n;
    }

    /** ...of every kind. */
    static long modelRoadPlots(long[] model, BuildingVisual.Type[] types) {
        return roadPlotsOf(model, types, BuildingVisual.GRAVEL) + roadPlotsOf(model, types, BuildingVisual.PAVED)
                + roadPlotsOf(model, types, BuildingVisual.HIGHWAY);
    }

    /** The played city's save folder. */
    static GameFiles cityFiles;

    static int mineId(Game g) {
        for (BuildingVisual.Type t : g.getMapTypes()) if (t != null && t.site() == Resource.IRON) return t.id();
        return 0;
    }

    /* =====================================================================
       2. NOTHING PLACED MOVES
       ===================================================================== */

    static void nothingMoves(Game g) {
        out.println("\n--- 2. nothing placed moves: one more building, and one fewer, on a copy of the city's map ---");
        long[] counts = g.getMapCounts();
        BuildingVisual.Type[] types = g.getMapTypes();
        for (String name : new String[] { "House", "Mixed Farm" }) {
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
            boolean outer = types[t.getId()].outer();
            String way = outer ? "outermost" : "nearest";
            for (int sign : new int[] { 1, -1 }) {
                CityMap m = CityMap.canonical(g.getCityLand(), g.getLandManager()::remainingByHolding, types, counts);
                List<CityMap.District> ds = m.districts();
                int[][] before = new int[ds.size()][];
                int[][][] deals = new int[ds.size()][][];
                int expected = -1;
                long each = BuildingVisual.cells(types[t.getId()]);
                for (int i = 0; i < ds.size(); i++) {
                    // Added: the first district in its direction with room for its whole plots, keeping room for the road its
                    // buildings need (0.7.72, CityMap.hasRoomFor()). Taken away: the last holding one.
                    CityMap.District d = ds.get(outer == (sign > 0) ? ds.size() - 1 - i : i);
                    boolean fits = sign > 0 ? m.hasRoomFor(d, t.getId()) : d.count(t.getId()) > 0;
                    if (fits && expected < 0) expected = d.index;
                }
                if (sign > 0 && expected < 0) expected = m.roomiest().index;
                for (CityMap.District d : ds) {
                    before[d.index] = d.counts.clone();
                    int[][] deal = m.deal(d);
                    deals[d.index] = new int[deal.length][];
                    for (int i = 0; i < deal.length; i++) deals[d.index][i] = deal[i].clone();
                }
                long[] changedCounts = counts.clone();
                changedCounts[t.getId()] += sign;
                m.reconcile(changedCounts);
                int changed = 0, by = 0, where = -1, tilesChanged = 0, tileBy = 0, ownTiles = 0, ownBy = 0, othersNet = 0, othersLarger = 0;
                int[] rank = BuildingVisual.placeRanks(types);
                java.util.Map<Integer, Integer> net = new java.util.HashMap<>();
                for (CityMap.District d : m.districts()) {
                    for (int k = 0; k < d.counts.length; k++) {
                        if (d.counts[k] != before[d.index][k]) {
                            changed++;
                            by += d.counts[k] - before[d.index][k];
                            where = d.index;
                        }
                    }
                    int[][] deal = m.deal(d);
                    for (int i = 0; i < deal.length; i++) {
                        for (int k = 0; k < deal[i].length; k++) {
                            if (deal[i][k] == deals[d.index][i][k]) continue;
                            int dk = deal[i][k] - deals[d.index][i][k];
                            tilesChanged++;
                            tileBy += dk;
                            if (k == t.getId()) { ownTiles++; ownBy += dk; }
                            else {
                                net.merge(k, dk, Integer::sum);
                                if (rank[k] >= 0 && rank[k] < rank[t.getId()]) othersLarger++;
                            }
                        }
                    }
                }
                String what = (sign > 0 ? "one more " : "one fewer ") + name;
                for (int v : net.values()) othersNet += Math.abs(v);
                out.printf("      %s: %d district count(s) changed by %d, in district %d of %d (expected %d); %d tile count(s) by %d"
                        + " (its own kind in %d tile(s) by %d, %d of smaller kinds moved)%n",
                        what, changed, by, where, ds.size(), expected, tilesChanged, tileBy, ownTiles, ownBy, tilesChanged - ownTiles);
                check(what + " changes one district's count, by " + sign + ", and no other", changed == 1 && by == sign);
                check(sign > 0 ? "...in the " + way + " district with room for it"
                               : "...from the " + (outer ? "nearest" : "outermost") + " district holding one", where == expected);
                if (sign > 0) check("...and one tile of the map, by " + sign, tilesChanged == 1 && tileBy == sign);
                else check("...its own kind one tile of the map, by " + sign + "; anything else a smaller kind the room it frees takes, moved"
                        + " and not lost", ownTiles == 1 && ownBy == sign && othersNet == 0 && othersLarger == 0);
            }
        }
    }

    /* =====================================================================
       THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY
       ===================================================================== */

    /** His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. */
    static final class Squares {
        final BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        final long seed = Founding.DEFAULT_WORLD_SEED;
        final long x, y;
        final CityMap[] maps = new CityMap[TIMES.length];
        final long[][] counts = new long[TIMES.length][];
        final double[] buildMs = new double[TIMES.length];

        Squares() {
            out.println("\n--- the copies: Jerus's city x 1, x 9,814 (5B), x 10,000 and x 19,629 (10B) on the design's square city ---");
            World w = World.of(seed);
            long[] place = dryPlace(w);
            x = place[0];
            y = place[1];
            out.printf("      the dry place: plot (%d, %d), %d cells from the founding site's%n", x, y, place[2]);
            int mine = -1;
            for (BuildingVisual.Type t : types) if (t != null && t.site() == Resource.IRON) mine = t.id();
            for (int i = 0; i < TIMES.length; i++) {
                double k = TIMES[i];
                long[] c = new long[types.length];
                for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(JERUS_COUNTS[t] * k);
                long wantMines = mine >= 0 ? c[mine] : 0;
                if (mine >= 0) c[mine] = 0;
                int side = (int) Math.ceil(Math.sqrt(groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
                int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
                long t0 = System.nanoTime();
                CityMap m = CityMap.square(seed, x, y, types, side, capacity, c);
                buildMs[i] = (System.nanoTime() - t0) / 1e6;
                // No more mines than the square's iron sites: the game's own rule (Game.hasDepositFor()).
                if (mine >= 0) {
                    c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON));
                    m.reconcile(c);
                }
                maps[i] = m;
                counts[i] = c;
                out.printf("      x %,.0f: %,d people, %,d buildings, %d a side, %,d districts, drawn in %.0f ms; pyramid %d levels, %,d nodes; %,d mines on %,d sites%n",
                        k, Math.round(JERUS_PEOPLE * k), Arrays.stream(c).sum(), side, m.districts().size(), buildMs[i],
                        m.pyramid().levels(), m.pyramid().nodes(), mine >= 0 ? c[mine] : 0, m.ownedSites(Resource.IRON));
            }
            boolean exact = true;
            for (int i = 0; i < TIMES.length; i++) exact &= Arrays.equals(maps[i].totals(), counts[i]);
            check("the copies' districts sum to their counts exactly (the canonical allocation)", exact);
        }
    }

    /** The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. */
    static long[] dryPlace(World w) {
        int n = 3 * CityMap.TILES_A_SIDE;
        byte[] buf = new byte[n * n];
        long sx = Math.floorDiv(w.foundingX(), World.CELL), sy = Math.floorDiv(w.foundingY(), World.CELL);
        for (int ring = 0; ring < World.CELLS; ring++) {
            for (long dy = -ring; dy <= ring; dy++) {
                for (long dx = -ring; dx <= ring; dx++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != ring) continue;
                    long cx = (sx + dx) * World.CELL + World.CELL / 2, cy = (sy + dy) * World.CELL + World.CELL / 2;
                    w.regionTerrain(cx - 3 * CityMap.DISTRICT / 2, cy - 3 * CityMap.DISTRICT / 2, World.TILE, n, buf);
                    int dry = 0;
                    for (byte b : buf) if (b != World.SALT && b != World.FRESH) dry++;
                    if (dry >= DRY_PLACE * buf.length) return new long[] { cx, cy, ring };
                }
            }
        }
        return new long[] { w.foundingX(), w.foundingY(), -1 };
    }

    /* =====================================================================
       3. THE PAINTER'S RULES
       ===================================================================== */

    static void painterRules(CityMap m) {
        out.println("\n--- 3. the painter keeps the mockup's rules: Jerus's density, the screen at the site of his city x 10,000 ---");
        long tx0 = Math.floorDiv(m.land().siteX(), World.TILE) - SCREEN_ACROSS / 2;
        long ty0 = Math.floorDiv(m.land().siteY(), World.TILE) - SCREEN_DOWN / 2;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        long seed = m.seed();
        int borderOff = 0, portsDiffer = 0, notExact = 0, longBridge = 0, gravelBridge = 0, onSea = 0, notSame = 0;
        long drawn = 0, dropped = 0, shrunk = 0, laid = 0, budget = 0, filled = 0, besideRoad = 0, rowTwo = 0, bridges = 0, mines = 0, minesOff = 0;
        long[] laidKind = new long[4];
        int[] img = new int[(World.TILE * PX) * (World.TILE * PX)], again = img.clone();
        int[] far = new int[World.TILE * World.TILE], far2 = far.clone();
        TilePainter.Input in2 = new TilePainter.Input();
        TilePainter.Painted p2 = new TilePainter.Painted();
        // The twin drawn the same way as the copies: the square without its mines, then the mines on their sites.
        long[] noMines = m.totals();
        for (int t = 0; t < noMines.length; t++) if (m.types()[t] != null && m.types()[t].site() != null) noMines[t] = 0;
        CityMap twin = CityMap.square(m.seed(), m.land().siteX(), m.land().siteY(), m.types(),
                (int) Math.round(Math.sqrt(m.districts().size())), m.districts().get(0).owned(), noMines);
        twin.reconcile(m.totals());
        for (int j = 0; j < SCREEN_DOWN; j++) {
            for (int i = 0; i < SCREEN_ACROSS; i++) {
                long tx = tx0 + i, ty = ty0 + j;
                m.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, PX, img);
                // The same inputs, painted again, and from a twin map drawn the same way: the same pixels.
                twin.tileInput(tx, ty, in2);
                TilePainter.paint(in2, p2);
                TileRaster.raster(in2, p2, PX, again);
                if (!Arrays.equals(img, again)) notSame++;
                // ...and the far view's blocks the same.
                TileRaster.blocks(in, p, 1, TileRaster.BLOCK_FAR, false, far);
                TileRaster.blocks(in2, p2, 1, TileRaster.BLOCK_FAR, false, far2);
                if (!Arrays.equals(far, far2)) notSame++;
                drawn += p.buildings;
                dropped += p.dropped;
                shrunk += p.shrunk;
                laid += p.roadPlots;
                filled += p.filledPlots;
                int b = in.roadBudget[1] + in.roadBudget[2] + in.roadBudget[3];
                budget += b;
                // Every road plot dealt is laid, kind by kind, and none more (0.7.64; within its budget before).
                for (int kind = 1; kind < 4; kind++) {
                    laidKind[kind] += p.laid[kind];
                    if (p.laid[kind] != in.roadBudget[kind]) notExact++;
                }
                // Ports (0.7.72): each edge's crossing, where a main street meets it, read from either tile's side - the
                // plot on its edge and the plot across it, the same pair.
                portsDiffer += crossingsDiffer(m, tx, ty);
                int T = World.TILE;
                // Bridges: over fresh water only, never longer than the kind allows - gravel's as paved's since 0.7.77.
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    if (p.use[q] != TilePainter.ROAD) continue;
                    if (in.terrain[q] == World.SALT) onSea++;
                    if (p.bridge[q]) {
                        bridges++;
                        if (p.road[q] == BuildingVisual.GRAVEL) gravelBridge++;
                        int run = 1, x = q % T, y = q / T;
                        for (int dx = 1; x + dx < T && p.bridge[q + dx]; dx++) run++;
                        for (int dy = 1; y + dy < T && p.bridge[q + dy * T]; dy++) run = Math.max(run, dy + 1);
                        if (run > TilePainter.MAX_BRIDGE[p.road[q]]) longBridge++;
                    }
                }
                for (int k = 0; k < p.buildings; k++) {
                    if (p.bsite[k] >= 0) {
                        mines++;
                        int s = p.bsite[k];
                        if (p.bx[k] < in.siteX0[s] || p.by[k] < in.siteY0[s] || p.bx[k] + p.bw[k] - 1 > in.siteX1[s]
                                || p.by[k] + p.bh[k] - 1 > in.siteY1[s]) minesOff++;
                        continue;
                    }
                    int row = 99;
                    for (int y = p.by[k]; y < p.by[k] + p.bh[k]; y++) for (int x = p.bx[k]; x < p.bx[k] + p.bw[k]; x++) row = Math.min(row, p.row[y * T + x]);
                    if (row == 1) besideRoad++;
                    if (row == 2) rowTwo++;
                }
            }
        }
        long placed = drawn - mines;
        out.printf("      %d tiles: %.1f buildings drawn a tile (%.2f drawn smaller than their land, %.2f found no place); road plots %.1f laid of"
                        + " %.1f dealt (gravel %d, paved %d, highway %d; %d laid beside a road after growth stopped), %d bridge plots%n",
                SCREEN_TILES, (double) drawn / SCREEN_TILES, (double) shrunk / SCREEN_TILES, (double) dropped / SCREEN_TILES,
                (double) laid / SCREEN_TILES, (double) budget / SCREEN_TILES, laidKind[1], laidKind[2], laidKind[3], filled, bridges);
        out.printf("      %d of the bridge plots gravel (a street's bridge, gravel or paved, since 0.7.77)%n", gravelBridge);
        out.printf("      %.0f%% of buildings beside a road, %.0f%% in the row behind; %d mines on their sites%n",
                100.0 * besideRoad / Math.max(1, placed), 100.0 * rowTwo / Math.max(1, placed), mines);
        // A road at a tile's edge goes on in the next tile (0.7.72): every road plot on an edge between two of the
        // screen's tiles has a road across it.
        Net screen = new Net(m, tx0, ty0, SCREEN_ACROSS, SCREEN_DOWN);
        for (int g = 0; g < screen.w * screen.h; g++) {
            int x = g % screen.w, y = g / screen.w;
            if (x % World.TILE == World.TILE - 1 && x + 1 < screen.w && screen.isRoad(g) != screen.isRoad(g + 1)) borderOff++;
            if (y % World.TILE == World.TILE - 1 && y + 1 < screen.h && screen.isRoad(g) != screen.isRoad(g + screen.w)) borderOff++;
        }
        check("shared ports agree, read from either tile: each edge's crossing the same pair of plots", portsDiffer == 0);
        check("no road meets a tile's border but where the next tile's road meets it, the same street going on", borderOff == 0);
        check("every tile lays exactly the road plots of each kind the model's roads dealt it, a highway only where one was",
                notExact == 0 && laid == budget);
        check("no bridge crosses more fresh water than its kind may - a street STREET_BRIDGE, gravel or paved (gravel none"
                + " until 0.7.77), a highway more - and no road is on the sea", longBridge == 0 && onSea == 0);
        check("every mine stands on its own site", minesOff == 0);
        check("the same inputs paint the same pixels, from a twin map drawn the same way", notSame == 0);
        bridges(m, tx0, ty0);
        moves(m, tx0, ty0);
    }

    /**
     * Bridges, where there is fresh water to cross: the default world's
     * founding river, every tile it runs through painted owned with the
     * dense screen's middle tile's buildings and its whole road budget of one
     * kind, no more than the tile's dry plots inside its edge ring hold (a
     * fixture, so roads that can bridge meet the river) - paved, and since
     * 0.7.77 again all gravel (Jerus: "gravel road bridge rivers sure").
     */
    static void bridges(CityMap m, long tx0, long ty0) {
        TilePainter.Input dense = new TilePainter.Input();
        m.tileInput(tx0 + SCREEN_ACROSS / 2, ty0 + SCREEN_DOWN / 2, dense);
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        World.River river = w.river();
        java.util.Set<Long> tiles = new java.util.LinkedHashSet<>();
        for (int k = 0; k < river.points(); k++) {
            tiles.add(CityMap.key((int) Math.floorDiv((long) river.xs()[k], World.TILE), (int) Math.floorDiv((long) river.ys()[k], World.TILE)));
        }
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int T = World.TILE;
        for (int kind : new int[] { BuildingVisual.PAVED, BuildingVisual.GRAVEL }) {
            int bridges = 0, wet = 0, otherKind = 0, tooLong = 0, onSea = 0, borderOff = 0, crossings = 0;
            for (long key : tiles) {
                long tx = key >> 32, ty = (int) key;
                in.seed = w.seed();
                in.tx = tx;
                in.ty = ty;
                in.types = m.types();
                w.tileTerrain(tx, ty, in.terrain);
                Arrays.fill(in.owned, true);
                in.counts = dense.counts.clone();
                in.model = dense.model.clone();
                Arrays.fill(in.roadBudget, 0);
                // ...no more than its dry plots inside the edge ring hold, as the deal gives a tile (0.7.64).
                int innerDry = 0;
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    int x = q % T, y = q / T;
                    if (x >= 1 && y >= 1 && x < T - 1 && y < T - 1 && in.terrain[q] != World.SALT && in.terrain[q] != World.FRESH) innerDry++;
                }
                in.roadBudget[kind] = Math.min(innerDry, dense.roadBudget[1] + dense.roadBudget[2] + dense.roadBudget[3]);
                Arrays.fill(in.neighbourRoads, true);
                in.clearSites();
                TilePainter.paint(in, p);
                // Painted alone, with no deal's crossing to meet, a tile lays no road on its edge (0.7.72; off a hashed port before).
                for (int k = 0; k < T; k++) {
                    if (road(p, k)) borderOff++;
                    if (road(p, (T - 1) * T + k)) borderOff++;
                    if (road(p, k * T)) borderOff++;
                    if (road(p, k * T + T - 1)) borderOff++;
                }
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    if (in.terrain[q] == World.FRESH) wet++;
                    if (p.use[q] != TilePainter.ROAD) continue;
                    if (in.terrain[q] == World.SALT) onSea++;
                    if (!p.bridge[q]) continue;
                    bridges++;
                    if (p.road[q] != kind) otherKind++;
                    int x = q % T, y = q / T;
                    boolean startX = x == 0 || !p.bridge[q - 1], startY = y == 0 || !p.bridge[q - T];
                    int runX = 0, runY = 0;
                    if (startX) for (int dx = 0; x + dx < T && p.bridge[q + dx]; dx++) runX++;
                    if (startY) for (int dy = 0; y + dy < T && p.bridge[q + dy * T]; dy++) runY++;
                    if (startX && runX > 1 || startY && runY > 1 || startX && startY) crossings++;
                    if (Math.max(runX, runY) > TilePainter.MAX_BRIDGE[p.road[q]]) tooLong++;
                }
            }
            String name = kind == BuildingVisual.PAVED ? "paved" : "gravel";
            out.printf("      the founding river's %d tiles, %s: %,d plots of fresh water, %d bridge plots in %d crossing(s) (of another kind %d,"
                            + " too long %d, on the sea %d, at a border with no crossing %d)%n",
                    tiles.size(), name, wet, bridges, crossings, otherKind, tooLong, onSea, borderOff);
            check("fixture: " + name + " roads meet the founding river and cross it on bridges"
                    + (kind == BuildingVisual.GRAVEL ? " - gravel too since 0.7.77" : ""), bridges > 0);
            check("...each " + name + ", none longer than a street may cross (STREET_BRIDGE), none on the sea, none at a border it"
                    + " has no crossing on", otherKind == 0 && tooLong == 0 && onSea == 0 && borderOff == 0);
        }
    }

    /** A road plot. */
    static boolean road(TilePainter.Painted p, int i) { return p.use[i] == TilePainter.ROAD; }

    /**
     * How many of tile (tx, ty)'s four edges its deal and the next tile's
     * read differently (0.7.72): each side's main street to its crossing ends
     * on the edge, and where both are laid the two plots face each other
     * across it - the plot along the edge the same.
     */
    static int crossingsDiffer(CityMap m, long tx, long ty) {
        CityMap.District d = m.districtOfTile(tx, ty);
        if (d == null) return 0;
        CityMap.RoadTiles rt = m.roadTilesOf(d);
        int k = (int) (Math.floorMod(ty, CityMap.TILES_A_SIDE) * CityMap.TILES_A_SIDE + Math.floorMod(tx, CityMap.TILES_A_SIDE)), differ = 0;
        for (int dir = 0; dir < 4; dir++) {
            long ox = tx + TilePainter.DX[dir], oy = ty + TilePainter.DY[dir];
            CityMap.District od = m.districtOfTile(ox, oy);
            if (od == null || rt.arm[k][dir] == null) continue;
            CityMap.RoadTiles ort = m.roadTilesOf(od);
            int ok = (int) (Math.floorMod(oy, CityMap.TILES_A_SIDE) * CityMap.TILES_A_SIDE + Math.floorMod(ox, CityMap.TILES_A_SIDE));
            short[] mine = rt.arm[k][dir], theirs = ort.arm[ok][(dir + 2) % 4];
            if (theirs == null) continue;
            // ...an arm of no plots: the hub stands on the edge itself.
            int a = mine.length == 0 ? rt.hub[k] : mine[mine.length - 1], b = theirs.length == 0 ? ort.hub[ok] : theirs[theirs.length - 1];
            boolean ns = dir == 0 || dir == 2;
            if (ns ? a % World.TILE != b % World.TILE : a / World.TILE != b / World.TILE) differ++;
        }
        return differ;
    }

    /**
     * The city grown by one building in a tile (0.7.64: one more of each
     * model type the tile holds, painted again, where the tile has room for
     * its whole plots as the deal reckons room - its free plots less what was
     * dealt it, CityMap.deal(), which gives a full tile none) - none placed
     * before it moved, how many of the others did, and whether any kind was
     * drawn fewer; and one more Paved Road's plots, laid again.
     */
    static void moves(CityMap m, long tx0, long ty0) {
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        List<Integer> moved = new ArrayList<>();
        double stepMoved = 0;
        int stepCases = 0, fewer = 0, earlierMoved = 0;
        BuildingVisual.Type[] types = m.types();
        int[] rank = BuildingVisual.placeRanks(types);
        int paved = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.road() == BuildingVisual.PAVED) { paved = t.id(); break; }
        for (int j = 0; j < SCREEN_DOWN; j++) {
            for (int i = 0; i < SCREEN_ACROSS; i++) {
                long tx = tx0 + i, ty = ty0 + j;
                m.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                if (p.buildings < 2) continue;
                java.util.Map<Long, Long> before = positions(p);
                long[] drawnBefore = drawnByType(p, types.length);
                int[] counts = in.counts.clone();
                // The tile's room as the deal reckons it: its free plots less the whole plots and road plots dealt it, and
                // (0.7.72) its railway's track, which the deal charges the tile as it does a road.
                long left = -m.tileRail(tx, ty);
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    byte gr = in.terrain[q];
                    if (in.owned[q] && gr != World.SALT && gr != World.FRESH && p.site[q] == 0) left++;
                }
                for (int t = 0; t < counts.length; t++) if (counts[t] > 0 && types[t] != null && types[t].drawn()) left -= (long) counts[t] * BuildingVisual.cells(types[t]);
                for (int k = 1; k < 4; k++) left -= in.roadBudget[k];
                for (int t = 0; t < counts.length; t++) {
                    if (counts[t] == 0 || types[t] == null || !types[t].drawn() || BuildingVisual.cells(types[t]) > left) continue;
                    in.counts[t]++;
                    TilePainter.paint(in, p);
                    long[] after = drawnByType(p, types.length);
                    for (int k = 0; k < after.length; k++) if (after[k] < drawnBefore[k]) fewer++;
                    java.util.Map<Long, Long> now = positions(p);
                    moved.add(movedOf(before, now));
                    // Those placed before it - the larger, and its own type's earlier numbers - stand where they stood.
                    for (java.util.Map.Entry<Long, Long> e : before.entrySet()) {
                        int bt = (int) (e.getKey() >>> 32), bj = (int) (long) e.getKey();
                        boolean earlier = rank[bt] < rank[t] || (bt == t && bj < counts[t]);
                        if (earlier && !e.getValue().equals(now.get(e.getKey()))) earlierMoved++;
                    }
                    in.counts[t]--;
                }
                // One more Paved Road's plots, as the model builds one: the tile's roads laid again.
                if (paved >= 0) {
                    in.roadBudget[BuildingVisual.PAVED] += BuildingVisual.cells(types[paved]);
                    TilePainter.paint(in, p);
                    stepMoved += movedOf(before, positions(p));
                    stepCases++;
                    in.roadBudget[BuildingVisual.PAVED] -= BuildingVisual.cells(types[paved]);
                }
            }
        }
        Collections.sort(moved);
        int n = moved.size();
        long zero = moved.stream().filter(v -> v == 0).count();
        double mean = moved.stream().mapToInt(Integer::intValue).average().orElse(0);
        out.printf("      %,d cases of one more model building in a tile with room for it: a median of %d others moved, 75th %d, 90th %d, 99th %d, mean %.2f;"
                        + " none in %.0f%%; of those placed before it, %d; one more Paved Road's plots moves %.1f%n", n, n == 0 ? 0 : moved.get(n / 2),
                n == 0 ? 0 : moved.get(n * 3 / 4), n == 0 ? 0 : moved.get(n * 9 / 10), n == 0 ? 0 : moved.get(n * 99 / 100), mean,
                100.0 * zero / Math.max(1, n), earlierMoved, stepCases == 0 ? 0 : stepMoved / stepCases);
        check("fixture: the screen holds buildings to add to", n > 100);
        check("one more building moves none placed before it - the larger, and its own type's earlier - only the smaller after it", n > 0 && earlierMoved == 0);
        check("...and the city grown by it is never drawn with fewer of anything", fewer == 0);
    }

    /** A painted tile's buildings by type id. */
    static long[] drawnByType(TilePainter.Painted p, int types) {
        long[] n = new long[types];
        for (int b = 0; b < p.buildings; b++) n[p.btype[b]]++;
        return n;
    }

    /** A tile's owned dry plots. */
    static int dryOwned(TilePainter.Input in) {
        int n = 0;
        for (int i = 0; i < TilePainter.PLOTS; i++) if (in.owned[i] && in.terrain[i] != World.SALT && in.terrain[i] != World.FRESH) n++;
        return n;
    }

    /** Each building's place, keyed by its type and number. */
    static java.util.Map<Long, Long> positions(TilePainter.Painted p) {
        java.util.Map<Long, Long> m = new java.util.HashMap<>();
        for (int b = 0; b < p.buildings; b++) {
            if (p.bj[b] < 0) continue;
            m.put(((long) p.btype[b] << 32) | p.bj[b], ((long) p.by[b] << 16) | p.bx[b]);
        }
        return m;
    }

    static int movedOf(java.util.Map<Long, Long> before, java.util.Map<Long, Long> after) {
        int n = 0;
        for (java.util.Map.Entry<Long, Long> e : before.entrySet()) if (!e.getValue().equals(after.get(e.getKey()))) n++;
        return n;
    }

    /* =====================================================================
       6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2)
       ===================================================================== */

    /** The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). */
    static final int[] NEW_CITY_MONTHS = { 0, 1, 12 };

    /**
     * Jerus, 2026-10-07: "the generation should only put what the city has,
     * not more not less". A new default city drawn as founded, a month on and
     * a year on is exactly what it holds - as founded, its one Commercial Bank
     * (Game.foundingBank()) on bare ground, no road and no highway, where J3b
     * drew it as a shop of the shops' colour beside the mockup's two world
     * highways; and on the dense screen every building dealt is drawn, each
     * on its own type's land but the few no free box of its tile could hold,
     * its buildings and roads covering exactly the plots the model gives them
     * less only what was drawn smaller.
     */
    static void drawnAsItIs(CityMap dense) {
        out.println("\n--- 6. the city drawn as what it has: a new city as founded, a month and a year on, and the dense screen ---");
        PrintStream real = System.out;
        boolean exact = true, bare = false;
        StringBuilder lines = new StringBuilder();
        System.setOut(QUIET);
        try {
            Game g = new Game(GameFiles.scratch("mapcheck-new"));
            g.newGame();
            Map<Integer, String> names = new java.util.HashMap<>();
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) names.put(t.getId(), t.getName());
            int at = 0;
            for (int months : NEW_CITY_MONTHS) {
                if (months > at) g.simulateMonths(months - at);
                at = months;
                CityMap map = g.getCityMap();
                Drawn d = drawn(map);
                long[] model = g.getMapCounts();
                exact &= d.sameAs(model, map.types());
                StringBuilder what = new StringBuilder(), has = new StringBuilder();
                for (int t = 0; t < model.length; t++) if (model[t] > 0) has.append(has.length() == 0 ? "" : ", ").append(model[t]).append(" ").append(names.get(t));
                for (int t = 0; t < d.buildings.length; t++) if (d.buildings[t] > 0) what.append(what.length() == 0 ? "" : ", ").append(d.buildings[t]).append(" ").append(names.get(t));
                lines.append(String.format("      month %d (%d months on), %,d people: it holds %s; the map draws %s, %d road plots%n",
                        g.getMonth(), months, g.getPopulationManager().getPopulation(), has.length() == 0 ? "nothing" : has,
                        what.length() == 0 ? "nothing" : what, d.roadPlots()));
                if (months == 0) {
                    BuildingsTemplate bank = g.getBuildingManager().getTemplateByName("Commercial Bank");
                    bare = bank != null && d.total() == 1 && d.buildings[bank.getId()] == 1 && model[bank.getId()] == 1
                            && modelBuildings(model, map.types()) == 1 && d.roadPlots() == 0 && d.roads[BuildingVisual.HIGHWAY] == 0;
                }
            }
        } finally {
            System.setOut(real);
        }
        out.print(lines);
        check("a new default city is drawn as what it holds, as founded, a month on and a year on: each type's buildings, no more, no less",
                exact);
        check("...as founded, its one Commercial Bank on bare ground: no other building, no road, no highway", bare);
        // The dense screen, painted: every building dealt drawn, on its own land, covering what the model gives it.
        long tx0 = Math.floorDiv(dense.land().siteX(), World.TILE) - SCREEN_ACROSS / 2;
        long ty0 = Math.floorDiv(dense.land().siteY(), World.TILE) - SCREEN_DOWN / 2;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        BuildingVisual.Type[] types = dense.types();
        long dealt = 0, drawnPlots = 0, lost = 0, buildings = 0, shrunk = 0, dropped = 0, offLand = 0, free = 0, dealtBuildings = 0;
        for (int j = 0; j < SCREEN_DOWN; j++) {
            for (int i = 0; i < SCREEN_ACROSS; i++) {
                dense.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                dropped += p.dropped;
                shrunk += p.shrunk;
                for (int t = 0; t < in.counts.length; t++) {
                    if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn()) continue;
                    dealt += (long) in.counts[t] * BuildingVisual.cells(types[t]);
                    dealtBuildings += in.counts[t];
                }
                for (int k = 1; k < 4; k++) dealt += in.roadBudget[k];
                drawnPlots += p.roadPlots;
                for (int b = 0; b < p.buildings; b++) {
                    if (p.bsite[b] >= 0) continue;
                    buildings++;
                    int[] f = BuildingVisual.footprint(types[p.btype[b]]);
                    boolean own = (p.bw[b] == f[0] && p.bh[b] == f[1]) || (p.bw[b] == f[1] && p.bh[b] == f[0]);
                    if (!own) {
                        offLand++;
                        lost += (long) f[0] * f[1] - (long) p.bw[b] * p.bh[b];
                    }
                    drawnPlots += (long) p.bw[b] * p.bh[b];
                }
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    byte gr = in.terrain[q];
                    if (in.owned[q] && gr != World.SALT && gr != World.FRESH && p.site[q] == 0) free++;
                }
            }
        }
        out.printf("      the dense screen's %d tiles: %,d buildings dealt, %,d drawn, %d with no plot, %d drawn smaller than their own land (%.2f%%);"
                        + " their buildings and roads %,d plots of %,d the model gives them (%,d lost to the smaller), %.1f%% of the free ground%n",
                SCREEN_TILES, dealtBuildings, buildings, dropped, shrunk, 100.0 * shrunk / Math.max(1, buildings), drawnPlots, dealt, lost,
                100.0 * drawnPlots / Math.max(1, free));
        check("on the dense screen every building dealt is drawn, none without a plot", dropped == 0 && buildings == dealtBuildings);
        check("...each on its own type's land, turned or not, but those no free box of its tile held (at most "
                + Math.round(100 * SHRUNK_MOST) + "%)", offLand == shrunk && shrunk <= SHRUNK_MOST * buildings);
        check("...its buildings and roads covering exactly the plots the model gives them, less only what was drawn smaller",
                drawnPlots + lost == dealt);
    }

    /* =====================================================================
       8. THE NETWORK (0.7.72, batch N3)

       Jerus, "to make the map generation prettier": highways connect to each
       other, prefer straight lines and curve only when they hit the ocean,
       with T and + junctions used very sparingly; ordinary roads "love being
       a continuous + junction", about eight houses' length apart at the
       least; all roads connect to one network, no road standing alone; every
       building is near a road; rail connects to the rail network and its
       terminals prefer to sit near mines. Measured on Jerus's city x 1, every
       tile of its districts painted (the copies' own fixture: his counts at
       his density), and on it with two rail yards and a freight line added.
       ===================================================================== */

    /** The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard). */
    static final int YARDS_ADDED = 2, FREIGHT_ADDED = 1;

    /** A map painted whole over a box of tiles and put together: what each plot is, every building's box, what holds the network's rules. */
    static final class Net {
        final CityMap map;
        final long px0, py0;
        final int w, h;
        final byte[] use, road;
        final boolean[] owned, sea, wet;
        /** A road plot (or track) that bridges fresh water (0.7.77). */
        final boolean[] bridge;
        /** Each building: {x0, y0, w, h, type, site} in the box's plots. */
        final List<int[]> boxes = new ArrayList<>();
        long dropped, filled, crossings, rail;

        Net(CityMap map, long tx0, long ty0, int tw, int th) {
            this.map = map;
            px0 = tx0 * World.TILE;
            py0 = ty0 * World.TILE;
            w = tw * World.TILE;
            h = th * World.TILE;
            use = new byte[w * h];
            road = new byte[w * h];
            owned = new boolean[w * h];
            sea = new boolean[w * h];
            wet = new boolean[w * h];
            bridge = new boolean[w * h];
            TilePainter.Input in = new TilePainter.Input();
            TilePainter.Painted p = new TilePainter.Painted();
            for (int j = 0; j < th; j++) {
                for (int i = 0; i < tw; i++) {
                    map.tileInput(tx0 + i, ty0 + j, in);
                    TilePainter.paint(in, p);
                    dropped += p.dropped;
                    filled += p.filledPlots;
                    crossings += p.crossings;
                    rail += p.railLaid;
                    int ox = i * World.TILE, oy = j * World.TILE;
                    for (int k = 0; k < TilePainter.PLOTS; k++) {
                        int g = (oy + k / World.TILE) * w + ox + k % World.TILE;
                        use[g] = p.use[k];
                        road[g] = p.road[k];
                        owned[g] = in.owned[k];
                        sea[g] = in.terrain[k] == World.SALT;
                        wet[g] = sea[g] || in.terrain[k] == World.FRESH;
                        bridge[g] = p.bridge[k];
                    }
                    for (int b = 0; b < p.buildings; b++) boxes.add(new int[] { ox + p.bx[b], oy + p.by[b], p.bw[b], p.bh[b], p.btype[b], p.bsite[b] });
                }
            }
        }

        /** Every tile of every district of a map. */
        static Net of(CityMap map) {
            long x0 = Long.MAX_VALUE, y0 = Long.MAX_VALUE, x1 = Long.MIN_VALUE, y1 = Long.MIN_VALUE;
            for (CityMap.District d : map.districts()) {
                long ax = (map.baseDX() + d.dx) * CityMap.TILES_A_SIDE, ay = (map.baseDY() + d.dy) * CityMap.TILES_A_SIDE;
                x0 = Math.min(x0, ax); y0 = Math.min(y0, ay);
                x1 = Math.max(x1, ax + CityMap.TILES_A_SIDE - 1); y1 = Math.max(y1, ay + CityMap.TILES_A_SIDE - 1);
            }
            return new Net(map, x0, y0, (int) (x1 - x0 + 1), (int) (y1 - y0 + 1));
        }

        /** A road plot: a road, or the track where a road crosses it. */
        boolean isRoad(int g) { return use[g] == TilePainter.ROAD || (use[g] == TilePainter.RAIL && road[g] != 0); }

        boolean isHighway(int g) { return isRoad(g) && road[g] == BuildingVisual.HIGHWAY; }

        boolean isRail(int g) { return use[g] == TilePainter.RAIL; }

        int beside(int g, java.util.function.IntPredicate is) {
            int x = g % w, y = g / w, n = 0;
            if (x > 0 && is.test(g - 1)) n++;
            if (x < w - 1 && is.test(g + 1)) n++;
            if (y > 0 && is.test(g - w)) n++;
            if (y < h - 1 && is.test(g + w)) n++;
            return n;
        }

        /** The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), and [0] = how many pieces. */
        int[] pieces(java.util.function.IntPredicate is) {
            int[] piece = new int[w * h + 1];
            int[] q = new int[w * h];
            int n = 0;
            for (int s = 0; s < w * h; s++) {
                if (!is.test(s) || piece[s + 1] != 0) continue;
                n++;
                int qh = 0, qt = 0;
                q[qt++] = s;
                piece[s + 1] = n;
                while (qh < qt) {
                    int g = q[qh++], x = g % w, y = g / w;
                    int[] nb = { x > 0 ? g - 1 : -1, x < w - 1 ? g + 1 : -1, y > 0 ? g - w : -1, y < h - 1 ? g + w : -1 };
                    for (int m : nb) if (m >= 0 && is.test(m) && piece[m + 1] == 0) { piece[m + 1] = n; q[qt++] = m; }
                }
            }
            piece[0] = n;
            return piece;
        }

        /** A crossing of two streets: a road plot with road on its four sides and none on its four corners (a square's middle, road on its corners too, is not one). */
        boolean crossing(int g) {
            if (!isRoad(g) || beside(g, this::isRoad) != 4) return false;
            int x = g % w, y = g / w;
            for (int dy = -1; dy <= 1; dy += 2) for (int dx = -1; dx <= 1; dx += 2) {
                int xx = x + dx, yy = y + dy;
                if (xx >= 0 && yy >= 0 && xx < w && yy < h && isRoad(yy * w + xx)) return false;
            }
            return true;
        }

        /** {crossings, pairs of them nearer than `apart` either way}. */
        long[] crossings(int apart) {
            boolean[] c = new boolean[w * h];
            long n = 0, near = 0;
            for (int g = 0; g < w * h; g++) if (crossing(g)) { c[g] = true; n++; }
            for (int g = 0; g < w * h; g++) {
                if (!c[g]) continue;
                int x = g % w, y = g / w;
                for (int yy = y; yy <= Math.min(h - 1, y + apart - 1); yy++) {
                    for (int xx = Math.max(0, x - apart + 1); xx <= Math.min(w - 1, x + apart - 1); xx++) {
                        int o = yy * w + xx;
                        if (o > g && c[o]) near++;
                    }
                }
            }
            return new long[] { n, near };
        }

        /** Each plot's distance from a road, either way and across corners (a road 0), to `most` + 1; with `ownTile`, from its own tile's roads only, as its tile was painted. */
        int[] reach(int most) {
            return reach(most, false);
        }

        int[] reach(int most, boolean ownTile) {
            int[] dist = new int[w * h];
            Arrays.fill(dist, most + 1);
            int[] q = new int[w * h];
            int qh = 0, qt = 0;
            for (int g = 0; g < w * h; g++) if (isRoad(g)) { dist[g] = 0; q[qt++] = g; }
            while (qh < qt) {
                int g = q[qh++], x = g % w, y = g / w;
                if (dist[g] >= most) continue;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    int xx = x + dx, yy = y + dy;
                    if (xx < 0 || yy < 0 || xx >= w || yy >= h) continue;
                    if (ownTile && (xx / World.TILE != x / World.TILE || yy / World.TILE != y / World.TILE)) continue;
                    int m = yy * w + xx;
                    if (dist[m] > dist[g] + 1) { dist[m] = dist[g] + 1; q[qt++] = m; }
                }
            }
            return dist;
        }

        /** A box's nearest plot's distance from a road. */
        static int boxReach(int[] dist, int w, int[] b) {
            int best = Integer.MAX_VALUE;
            for (int y = b[1]; y < b[1] + b[3]; y++) for (int x = b[0]; x < b[0] + b[2]; x++) best = Math.min(best, dist[y * w + x]);
            return best;
        }

        /** The district a plot of the box is in. */
        CityMap.District districtAt(int g) {
            return map.districtOfTile(Math.floorDiv(px0 + g % w, World.TILE), Math.floorDiv(py0 + g / w, World.TILE));
        }
    }

    static void network(Squares sq) {
        out.println("\n--- 8. the network: one road network, + junctions apart, highways straight, buildings near a road, rail joined, yards near mines ---");
        CityMap m = sq.maps[0];
        Net net = Net.of(m);
        int W = net.w;
        // One network.
        int[] roads = net.pieces(net::isRoad);
        long roadPlots = 0;
        for (int g = 0; g < W * net.h; g++) if (net.isRoad(g)) roadPlots++;
        out.printf("      Jerus's city x 1, %d districts, every tile: %,d road plots in %d piece(s) (%,d laid as a fill beside the roads), %d crossings of the track%n",
                m.districts().size(), roadPlots, roads[0], net.filled, net.crossings);
        check("fixture: the city has roads, a fill among them, and its track crossed by roads", roadPlots > 0 && net.filled > 0 && net.crossings > 0);
        check("its roads are one network: every road plot joins every other through roads, no road stands alone", roads[0] == 1);
        // The junction floor.
        long[] cr = net.crossings(TilePainter.JUNCTION_APART);
        long plazas = 0;
        for (int g = 0; g < W * net.h; g++) if (net.isRoad(g) && net.beside(g, net::isRoad) == 4 && !net.crossing(g)) plazas++;
        out.printf("      %,d + junctions (streets crossing: road on four sides, none on the corners), %d of them nearer than %d plots to another; %,d plots inside a square of road%n",
                cr[0], cr[1], TilePainter.JUNCTION_APART, plazas);
        check("fixture: its streets cross in + junctions", cr[0] > 100);
        check("no two + junctions nearer than JUNCTION_APART plots, either way: about eight houses' length apart at the least", cr[1] == 0);
        // Highways.
        highways(net);
        // Every building near a road.
        // A building's reach from any road; the room near a road its tile has left, from the tile's own roads - a tile is
        // painted from its own inputs, the next tile's fill unseen.
        int[] dist = net.reach(TilePainter.REACH), own = net.reach(TilePainter.REACH, true);
        int near = 0, far = 0, farWithRoom = 0, onNoRoad = 0, mines = 0;
        for (int[] b : net.boxes) {
            if (b[5] >= 0) { mines++; continue; }
            int r = Net.boxReach(dist, W, b);
            if (r <= TilePainter.REACH) { near++; continue; }
            far++;
            if (roomNear(net, own, b)) farWithRoom++;
            if (!m.tileHasRoads(Math.floorDiv(net.px0 + b[0], World.TILE), Math.floorDiv(net.py0 + b[1], World.TILE))) onNoRoad++;
        }
        out.printf("      %,d buildings (and %d mines on their sites): %,d within %d plots of a road (%.1f%%), %d farther - %d of them where their tile had room for them near its roads, %d on a tile without road%n",
                near + far, mines, near, TilePainter.REACH, 100.0 * near / Math.max(1, near + far), far, farWithRoom, onNoRoad);
        check("every building stands within REACH plots of a road, but where its tile has no room for it near its own roads", farWithRoom == 0);
        check("...and none on a tile without road: a district with road deals its buildings to its road tiles", onNoRoad == 0);
        check("...the most of them near: at least 99% (no building of a tile with room left farther)", near >= 0.99 * (near + far));
        // Rail, and the yards near the mines.
        rail(net, "Jerus's city x 1");
        CityMap yards = yardCity(sq);
        Net yn = Net.of(yards);
        rail(yn, "...with " + YARDS_ADDED + " Rail Terminals and " + FREIGHT_ADDED + " Freight Line added");
        yardsNearMines(yn);
        riverCrossed(sq);
    }

    /**
     * The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's
     * square city sited at the default world's founding river - its point
     * nearest the founding site - with its streets all of one kind: his Paved
     * Roads restated as Gravel Roads, or his Gravel Roads as Paved Roads (the
     * twin), his highways none (a highway bridges either way). Its mines on
     * their sites, as the copies' are.
     */
    static CityMap riverCity(Squares sq, int kind) {
        World w = World.of(sq.seed);
        World.River river = w.river();
        int at = 0;
        double best = Double.MAX_VALUE;
        for (int k = 0; k < river.points(); k++) {
            double dx = river.xs()[k] - w.foundingX(), dy = river.ys()[k] - w.foundingY();
            if (dx * dx + dy * dy < best) { best = dx * dx + dy * dy; at = k; }
        }
        long[] c = sq.counts[0].clone();
        int gravel = -1, paved = -1, highway = -1, mine = -1;
        for (BuildingVisual.Type t : sq.types) {
            if (t == null) continue;
            if (t.road() == BuildingVisual.GRAVEL) gravel = t.id();
            else if (t.road() == BuildingVisual.PAVED) paved = t.id();
            else if (t.road() == BuildingVisual.HIGHWAY) highway = t.id();
            if (t.site() == Resource.IRON) mine = t.id();
        }
        if (kind == BuildingVisual.GRAVEL) { c[gravel] += c[paved]; c[paved] = 0; } else { c[paved] += c[gravel]; c[gravel] = 0; }
        c[highway] = 0;
        long wantMines = JERUS_COUNTS[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(sq.seed, (long) Math.floor(river.xs()[at]), (long) Math.floor(river.ys()[at]), sq.types, side,
                capacity, c);
        c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON));
        m.reconcile(c);
        return m;
    }

    /**
     * A river parts gravel streets no more than paved ones (0.7.77; Jerus,
     * 2026-10-08: "gravel road bridge rivers sure"): the river fixture all
     * gravel against its paved twin - the road pieces with a plot within a
     * tile of the river's line, and the city's in all, no more than the
     * twin's, and its gravel streets across the river on gravel bridges. On
     * 0.7.76, when gravel never bridged, the gravel city's streets near the
     * river lay in 16 pieces against the twin's 4 (runs/fixN5-notes.md).
     */
    static void riverCrossed(Squares sq) {
        World.River river = World.of(sq.seed).river();
        int[][] found = new int[2][];
        int[] kinds = { BuildingVisual.GRAVEL, BuildingVisual.PAVED };
        for (int i = 0; i < 2; i++) {
            Net net = Net.of(riverCity(sq, kinds[i]));
            int[] pieces = net.pieces(net::isRoad);
            java.util.Set<Integer> near = new java.util.HashSet<>();
            int[] banks = new int[2];
            int[] laid = new int[4], bridged = new int[4];
            for (int g = 0; g < net.w * net.h; g++) {
                if (!net.isRoad(g)) continue;
                laid[net.road[g]]++;
                if (net.use[g] == TilePainter.ROAD && net.bridge[g]) bridged[net.road[g]]++;
                // Its nearest stretch of the river's line, and the bank it lies on (the side of that stretch).
                double x = net.px0 + g % net.w + .5, y = net.py0 + g / net.w + .5, d2 = Double.MAX_VALUE, side = 0;
                for (int k = 0; k + 1 < river.points(); k++) {
                    double ax = river.xs()[k], ay = river.ys()[k], vx = river.xs()[k + 1] - ax, vy = river.ys()[k + 1] - ay;
                    double t = Math.max(0, Math.min(1, ((x - ax) * vx + (y - ay) * vy) / (vx * vx + vy * vy)));
                    double qx = ax + t * vx - x, qy = ay + t * vy - y;
                    if (qx * qx + qy * qy < d2) { d2 = qx * qx + qy * qy; side = vx * (y - ay) - vy * (x - ax); }
                }
                if (d2 > World.TILE * World.TILE) continue;
                near.add(pieces[g + 1]);
                banks[side > 0 ? 1 : 0]++;
            }
            found[i] = new int[] { pieces[0], near.size(), banks[0], banks[1], laid[1], laid[2], laid[3], bridged[1], bridged[2] };
            out.printf("      the river fixture, its streets all %s: %,d road plots in %d piece(s); within a tile of the river %,d and %,d on"
                            + " its banks, in %d piece(s); bridge plots gravel %d, paved %d%n", kinds[i] == BuildingVisual.GRAVEL ? "gravel" : "paved",
                    laid[1] + laid[2] + laid[3], pieces[0], banks[0], banks[1], near.size(), bridged[1], bridged[2]);
        }
        int[] g = found[0], p = found[1];
        check("fixture: the river city stands on both banks of the founding river, its streets all gravel",
                g[2] > 0 && g[3] > 0 && g[4] > 0 && g[5] == 0 && g[6] == 0);
        check("its gravel streets cross the river on gravel bridges (since 0.7.77; gravel never bridged before)", g[7] > 0);
        check(String.format("the river parts gravel streets no more than paved: %d piece(s) near it against the paved twin's %d, %d in"
                + " the city against %d", g[1], p[1], g[0], p[0]), g[1] <= p[1] && g[0] <= p[0]);
    }

    /**
     * Whether building box b's tile has room for it near its own roads: a box
     * of b's drawn size, either way round, on the tile's free owned dry
     * ground (a site's plots among it), one of its plots within REACH of a
     * road of that tile - where b could have stood near a road and did not.
     */
    static boolean roomNear(Net net, int[] own, int[] b) {
        int W = net.w, T = World.TILE, tx = b[0] / T * T, ty = b[1] / T * T;
        for (int turn = 0; turn < 2; turn++) {
            int bw = turn == 0 ? b[2] : b[3], bh = turn == 0 ? b[3] : b[2];
            for (int y0 = ty; y0 + bh <= ty + T; y0++) {
                for (int x0 = tx; x0 + bw <= tx + T; x0++) {
                    boolean fits = true, nearOne = false;
                    for (int y = y0; y < y0 + bh && fits; y++) {
                        for (int x = x0; x < x0 + bw && fits; x++) {
                            int g = y * W + x;
                            byte u = net.use[g];
                            fits = (u == TilePainter.EMPTY || u == TilePainter.FIELD) && net.owned[g] && !net.wet[g];
                            nearOne |= own[g] <= TilePainter.REACH;
                        }
                    }
                    if (fits && nearOne) return true;
                }
            }
        }
        return false;
    }

    /** Each district's highways: one straight run, turning only beside the sea or ground the city does not own, crossing itself at most once; meeting the district's edge only on its main line. */
    static void highways(Net net) {
        int W = net.w;
        int[] runs = net.pieces(net::isHighway);
        java.util.Map<CityMap.District, java.util.Set<Integer>> perDistrict = new java.util.HashMap<>();
        java.util.Map<CityMap.District, Integer> junctions = new java.util.HashMap<>();
        long plots = 0, turns = 0, dryTurns = 0, offLine = 0;
        for (int g = 0; g < W * net.h; g++) {
            if (!net.isHighway(g)) continue;
            plots++;
            CityMap.District d = net.districtAt(g);
            perDistrict.computeIfAbsent(d, k -> new java.util.HashSet<>()).add(runs[g + 1]);
            int x = g % W, y = g / W;
            boolean n = y > 0 && net.isHighway(g - W), s = y < net.h - 1 && net.isHighway(g + W), e = x < W - 1 && net.isHighway(g + 1), wv = x > 0 && net.isHighway(g - 1);
            int k = (n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (wv ? 1 : 0);
            if (k >= 3) junctions.merge(d, 1, Integer::sum);
            if (k == 2 && !(n && s) && !(e && wv)) {
                turns++;
                // ...beside the sea or ground the city does not own, which stopped it: the way on, from where it came, within a plot.
                boolean stopped = false;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    int xx = x + dx, yy = y + dy;
                    if (xx >= 0 && yy >= 0 && xx < W && yy < net.h && (net.sea[yy * W + xx] || !net.owned[yy * W + xx] || net.use[yy * W + xx] == TilePainter.BUILDING)) stopped = true;
                }
                if (!stopped) dryTurns++;
            }
            // At a district's edge: on the main line of the tile, which the next district's runs along.
            long ax = net.px0 + x, ay = net.py0 + y;
            boolean ew = Math.floorMod(ax, (long) CityMap.DISTRICT) == 0 || Math.floorMod(ax, (long) CityMap.DISTRICT) == CityMap.DISTRICT - 1;
            boolean ns = Math.floorMod(ay, (long) CityMap.DISTRICT) == 0 || Math.floorMod(ay, (long) CityMap.DISTRICT) == CityMap.DISTRICT - 1;
            if (ew && Math.floorMod(ay, (long) World.TILE) != TilePainter.mainY(net.map.seed(), Math.floorDiv(ay, World.TILE))) offLine++;
            if (ns && Math.floorMod(ax, (long) World.TILE) != TilePainter.mainX(net.map.seed(), Math.floorDiv(ax, World.TILE))) offLine++;
        }
        int many = 0, crossed = 0;
        for (java.util.Set<Integer> r : perDistrict.values()) if (r.size() > 1) many++;
        for (int j : junctions.values()) if (j > 1) crossed++;
        out.printf("      highways: %d plots in %d district(s), %d run(s); %d turn(s), %d of them away from the sea and the city's edge; %d junction plot(s); %d at a district's edge off its main line%n",
                plots, perDistrict.size(), runs[0], turns, dryTurns, junctions.values().stream().mapToInt(Integer::intValue).sum(), offLine);
        check("fixture: the city has highways", plots > 0);
        check("each district's highways are one run, joined", many == 0);
        check("...straight, turning only where the sea, the city's edge or a mine stopped it", dryTurns == 0);
        check("...crossing at most once a district (the cross at its hub): T and + junctions very sparingly", crossed == 0);
        check("...and meeting a district's edge only on its main line, where the next district's highway runs on", offLine == 0);
    }

    /** Each district's track one line; every plot of the model's track drawn. */
    static void rail(Net net, String what) {
        int W = net.w;
        int[] pieces = net.pieces(net::isRail);
        java.util.Map<CityMap.District, java.util.Set<Integer>> per = new java.util.HashMap<>();
        for (int g = 0; g < W * net.h; g++) if (net.isRail(g)) per.computeIfAbsent(net.districtAt(g), k -> new java.util.HashSet<>()).add(pieces[g + 1]);
        long model = 0;
        long[] totals = net.map.totals();
        for (int t = 0; t < totals.length; t++) if (net.map.types()[t] != null && net.map.types()[t].track()) model += totals[t] * BuildingVisual.cells(net.map.types()[t]);
        int split = 0;
        for (java.util.Set<Integer> s : per.values()) if (s.size() > 1) split++;
        out.printf("      %s: %,d plots of track drawn of the model's %,d, in %d district(s), %d piece(s)%n", what, net.rail, model, per.size(), pieces[0]);
        check(what + ": fixture: the city has a railway", model > 0);
        check("...every plot of its track drawn, as the model has it", net.rail == model);
        check("...and each district's track one line joined to its railway, no piece apart", split == 0);
    }

    /** The x 1 copy with rail yards and a freight line added, its mines on their sites. */
    static CityMap yardCity(Squares sq) {
        long[] c = sq.counts[0].clone();
        int yard = -1, freight = -1, mine = -1;
        for (BuildingVisual.Type t : sq.types) {
            if (t == null) continue;
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
            if (t.site() == Resource.IRON) mine = t.id();
        }
        c[yard] += YARDS_ADDED;
        c[freight] += FREIGHT_ADDED;
        long wantMines = c[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(sq.seed, sq.x, sq.y, sq.types, side, capacity, c);
        c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON));
        m.reconcile(c);
        return m;
    }

    /**
     * Every rail yard beside its track, on the tile of it nearest a mine that
     * had room for it: no tile of its district's track nearer a mine had room
     * for one more, as the deal reckons room - its free plots less its mines'
     * sites (a mine stands on its own field, which no other building takes),
     * its roads and its track - less the yards already on it.
     */
    static void yardsNearMines(Net net) {
        int W = net.w, yards = 0, beside = 0, nearest = 0;
        CityMap m = net.map;
        // Each tile's plots the mines stand on, owned and dry, and the yards on it.
        int tw = W / World.TILE;
        int[] mined = new int[tw * (net.h / World.TILE)], yardsOn = new int[mined.length];
        for (int[] b : net.boxes) {
            int tile = (b[1] / World.TILE) * tw + b[0] / World.TILE;
            BuildingVisual.Type t = m.types()[b[4]];
            if (t != null && t.terminal()) yardsOn[tile]++;
            if (b[5] < 0) continue;
            for (int y = b[1]; y < b[1] + b[3]; y++) for (int x = b[0]; x < b[0] + b[2]; x++) if (net.owned[y * W + x] && !net.wet[y * W + x]) mined[tile]++;
        }
        for (int[] b : net.boxes) {
            BuildingVisual.Type t = m.types()[b[4]];
            if (t == null || !t.terminal()) continue;
            yards++;
            boolean touches = false;
            for (int y = b[1] - 1; y <= b[1] + b[3]; y++) for (int x = b[0] - 1; x <= b[0] + b[2]; x++) {
                if (x < 0 || y < 0 || x >= W || y >= net.h) continue;
                if (net.isRail(y * W + x)) touches = true;
            }
            if (touches) beside++;
            // Its tile, and every tile its district's track crosses nearer a mine (as the deal measures it, to the mines'
            // sites' middles): had any of them room for one more yard?
            long tx = Math.floorDiv(net.px0 + b[0], World.TILE), ty = Math.floorDiv(net.py0 + b[1], World.TILE);
            CityMap.District d = m.districtOfTile(tx, ty);
            List<CityMap.DrawnSite> mines = m.minesNear(d);
            double mine = nearestMine(mines, tx, ty);
            short[] free = m.tileFreeOf(d);
            boolean ok = true;
            for (int k = 0; k < CityMap.TILES; k++) {
                long kx = (m.baseDX() + d.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE, ky = (m.baseDY() + d.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                if (m.tileRail(kx, ky) <= 0 || nearestMine(mines, kx, ky) >= mine) continue;
                int tile = (int) ((ky - Math.floorDiv(net.py0, World.TILE)) * tw + (kx - Math.floorDiv(net.px0, World.TILE)));
                int[] roadsK = m.roads(d)[k];
                long used = roadsK[1] + roadsK[2] + roadsK[3] + m.tileRail(kx, ky) + mined[tile] + (long) yardsOn[tile] * BuildingVisual.cells(t);
                if (free[k] - used >= BuildingVisual.cells(t)) ok = false;
            }
            if (ok) nearest++;
        }
        out.printf("      %d rail yard(s): %d beside their track, %d on the tile of it nearest a mine that had room%n", yards, beside, nearest);
        check("fixture: the city has rail yards and mines", yards == YARDS_ADDED);
        check("every rail yard stands beside its track", beside == yards);
        check("...on the tile of the track nearest a mine that had room for it: rail terminals near mines", nearest == yards);
    }

    /** The squared distance from tile (tx, ty)'s middle to the nearest mine's site's middle, as the deal measures it (CityMap.yardOrder()). */
    static double nearestMine(List<CityMap.DrawnSite> mines, long tx, long ty) {
        double cx = tx * World.TILE + World.TILE / 2.0, cy = ty * World.TILE + World.TILE / 2.0, best = Double.MAX_VALUE;
        for (CityMap.DrawnSite s : mines) {
            double mx = (s.x0() + s.x1()) / 2.0 - cx, my = (s.y0() + s.y1()) / 2.0 - cy;
            best = Math.min(best, mx * mx + my * my);
        }
        return best;
    }

    /* =====================================================================
       4. THE SIDECAR
       ===================================================================== */

    static void sidecar(Game g, Path root, Squares sq) throws Exception {
        out.println("\n--- 4. the sidecar: a save and a load, byte for byte, and a stale one drawn again ---");
        GameFiles files = cityFiles;
        PrintStream real = System.out;
        System.setOut(QUIET);
        Game back, next, stale, plain;
        byte[] first, second;
        long stampInSave;
        boolean nextSame, readSame;
        String plainJson;
        try {
            g.saveGame(3, "mapcheck");
            first = Files.readAllBytes(files.mapFile(3));
            stampInSave = new com.google.gson.Gson().fromJson(Files.readString(files.saveFile(3)), DataSave.class).getMapStamp();
            back = new Game(files);
            back.loadGameSave(3);
            readSame = back.hasCityMap() && back.getCityMap().same(g.getCityMap());
            back.saveGame(4, "mapcheck again");
            second = Files.readAllBytes(files.mapFile(4));
            // The next month, played by the city and by the one loaded from its save.
            LongPlaytest.run(g, 1);
            LongPlaytest.run(back, 1);
            nextSame = back.hasCityMap() && back.getCityMap().same(g.getCityMap());
            // A stale sidecar: the next month's, under the save's stamp.
            g.saveGame(5, "mapcheck a month on");
            Files.write(files.mapFile(3), Files.readAllBytes(files.mapFile(5)));
            stale = new Game(files);
            stale.loadGameSave(3);
            // A city never asked for its map saves none.
            GameFiles bare = new GameFiles(root.resolve("bare"), root.resolve("bare-no-legacy"));
            plain = new Game(bare, LongPlaytest.founding());
            plain.saveGame(1, "no map");
            plainJson = Files.readString(bare.saveFile(1));
            Game plainBack = new Game(bare);
            plainBack.loadGameSave(1);
            next = plainBack;
        } finally {
            System.setOut(real);
        }
        out.printf("      the city's sidecar: %,d bytes for %d districts%n", first.length, g.getCityMap().districts().size());
        check("the save carries the sidecar's stamp as mapStamp", stampInSave == CityMap.stampIn(first));
        check("a load reads the map back from its sidecar: the same map", readSame);
        check("...and saved again, the sidecar is the same file byte for byte", Arrays.equals(first, second));
        check("...and the next month plays to the same map in both", nextSame);
        CityMap canonical = CityMap.canonical(stale.getCityLand(), stale.getLandManager()::remainingByHolding,
                stale.getMapTypes(), stale.getMapCounts());
        check("a stale sidecar is not read: the load draws the map again canonically", stale.hasCityMap()
                && stale.getCityMap().same(canonical));
        check("a city never asked for its map saves no stamp, and loads with none", !plainJson.contains("\"mapStamp\"")
                && !next.hasCityMap());
        check("...and a sidecar that is not one is read as none", CityMap.readSidecar(new byte[] { 1, 2, 3 },
                back.getCityLand(), back.getLandManager()::remainingByHolding, back.getMapTypes(), back.getMonth(), stampInSave) == null);
        for (int i = 1; i < TIMES.length; i++) {
            if (TIMES[i] != 9_814 && TIMES[i] != 19_629) continue;
            CityMap m = sq.maps[i];
            long t0 = System.nanoTime();
            byte[] bytes = m.writeSidecar(1);
            double wms = (System.nanoTime() - t0) / 1e6;
            t0 = System.nanoTime();
            CityMap read = CityMap.readSidecar(bytes, m.land(), r -> m.land().amountsInOrder(r), m.types(), 1, CityMap.stampIn(bytes));
            double rms = (System.nanoTime() - t0) / 1e6;
            long raw = (long) m.districts().size() * (2 + 2 + 4 + 8 + 4 * CityMap.SITED.length + 4L * m.types().length);
            out.printf("      x %,.0f: %,d districts, %.2f MB raw, %.2f MB deflated, written in %.0f ms, read in %.0f ms%n",
                    TIMES[i], m.districts().size(), raw / 1e6, bytes.length / 1e6, wms, rms);
            check("at " + (TIMES[i] < 10_000 ? "5" : "10") + " billion it is read back the same and written again byte for byte",
                    read != null && read.same(m) && Arrays.equals(read.writeSidecar(1), bytes));
        }
    }

    /* =====================================================================
       5. THE COST FOLLOWS THE SCREEN
       ===================================================================== */

    static void cost(Squares sq) {
        out.println("\n--- 5. the cost follows the screen, never the population ---");
        int k = TIMES.length;
        double[] best = new double[k];
        Arrays.fill(best, Double.MAX_VALUE);
        long[] drawn = new long[k];
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] img = new int[(World.TILE * PX) * (World.TILE * PX)];
        long tx0 = Math.floorDiv(sq.x, World.TILE) - SCREEN_ACROSS / 2, ty0 = Math.floorDiv(sq.y, World.TILE) - SCREEN_DOWN / 2;
        System.gc();
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int i = 0; i < k; i++) {
                CityMap m = sq.maps[i];
                long n = 0;
                long t0 = System.nanoTime();
                for (int j = 0; j < SCREEN_DOWN; j++) {
                    for (int c = 0; c < SCREEN_ACROSS; c++) {
                        m.tileInput(tx0 + c, ty0 + j, in);
                        TilePainter.paint(in, p);
                        TileRaster.raster(in, p, PX, img);
                        n += p.buildings;
                    }
                }
                double ms = (System.nanoTime() - t0) / 1e6;
                drawn[i] = n;
                if (round >= WARM_ROUNDS) best[i] = Math.min(best[i], ms);
            }
        }
        /*
         * THE RATIO'S BASE (0.7.64). Until 0.7.64 each copy was held within
         * SCREEN_RATIO of the city x 1's screen. J3b drew homes from people and
         * workplaces from jobs, capped at a share of a tile, so every copy's
         * screen drew about the same; since 0.7.64 every model building is
         * drawn on its own land, and the city x 1 - 14,214 buildings, under
         * half of what the screen holds at his density - fills part of its
         * screen (8,186 drawn) where the others fill all of it (20,546). So
         * the copies are held to the 5B copy's, the first whose screen is all
         * city: past the screen's size the cost does not grow with the
         * population (5B, x 10,000 and 10B within a few percent), and the
         * city x 1 within it too. The bound, 1.5, stands.
         */
        int base = 1;
        boolean under = true, near = true;
        for (int i = 0; i < k; i++) {
            double ratio = best[i] / best[base];
            out.printf("      x %,.0f: the screen's %d tiles - terrain, the deal, paint and raster at %d px - in %.1f ms (%.3f ms a tile),"
                    + " %,d buildings; %.2f x the 5B copy's%n", TIMES[i], SCREEN_TILES, PX, best[i], best[i] / SCREEN_TILES, drawn[i], ratio);
            under &= best[i] <= SCREEN_MS;
            if (i != base) near &= ratio <= SCREEN_RATIO;
        }
        check("a 1,389 x 868 L0 screen paints and rasters in no more than 80 ms, at every size", under);
        check("...and x 1, x 10,000 and 10B within 1.5 times the 5B copy's, the first all city: it follows the screen", near);
        // A month's change at 5B and 10B: +0.05%, a year of +0.01%, then -0.05%.
        for (int i = 0; i < k; i++) {
            if (TIMES[i] != 9_814 && TIMES[i] != 19_629) continue;
            CityMap m = sq.maps[i];
            long[] c = sq.counts[i].clone();
            int mine = -1;
            for (BuildingVisual.Type t : m.types()) if (t != null && t.site() == Resource.IRON) mine = t.id();
            long sites = m.ownedSites(Resource.IRON);
            double worst = 0, firstMs, downMs;
            for (int t = 0; t < c.length; t++) c[t] += c[t] / 2000;
            if (mine >= 0) c[mine] = Math.min(c[mine], sites);
            long t0 = System.nanoTime();
            m.reconcile(c);
            firstMs = (System.nanoTime() - t0) / 1e6;
            StringBuilder months = new StringBuilder();
            for (int month = 0; month < 12; month++) {
                for (int t = 0; t < c.length; t++) c[t] += c[t] / 10_000;
                if (mine >= 0) c[mine] = Math.min(c[mine], sites);
                t0 = System.nanoTime();
                m.reconcile(c);
                double ms = (System.nanoTime() - t0) / 1e6;
                worst = Math.max(worst, ms);
                months.append(String.format(" %.2f", ms));
            }
            for (int t = 0; t < c.length; t++) c[t] -= c[t] / 2000;
            t0 = System.nanoTime();
            m.reconcile(c);
            downMs = (System.nanoTime() - t0) / 1e6;
            worst = Math.max(worst, downMs);
            String size = TIMES[i] < 10_000 ? "5" : "10";
            out.printf("      x %,.0f: +0.05%% in %.2f ms, then a year of +0.01%%:%s ms, then -0.05%% in %.2f ms%n", TIMES[i], firstMs, months, downMs);
            check("at " + size + " billion a month's change takes no more than 5 ms", worst <= RECONCILE_MS);
            check("...and its districts still sum to the counts exactly", Arrays.equals(m.totals(), c));
        }
    }

    /* =====================================================================
       7. THE VIEW'S PURE HALF (0.7.61, batch J4)
       ===================================================================== */

    /** The land office's small map, in pixels (spec-land 2.8). */
    static final int SMALL_W = 600, SMALL_H = 400;

    /** Points across the screen the transforms are tried at. */
    static final double[][] SCREEN_POINTS = { { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } };

    static void viewHalf(Game city, Squares sq, Path root) throws Exception {
        out.println("\n--- 7. the view's pure half: where it looks, the level it draws, what a click picks, what a tile repaints ---");
        frames();
        picks(city);
        overlay(city);
        draft(root);
        tiles(sq);
    }

    /** MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. */
    static void frames() {
        MapFrame f = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        f.set(370_000.25, 368_123.5, 2.0);
        boolean round = true;
        for (double[] s : SCREEN_POINTS) {
            round &= Math.abs(f.screenX(f.plotX(s[0])) - s[0]) < 1e-6 && Math.abs(f.screenY(f.plotY(s[1])) - s[1]) < 1e-6;
        }
        check("a screen point and its plot go back and forth exactly", round);
        boolean still = true, step = true;
        for (double[] s : SCREEN_POINTS) {
            for (int notches : new int[] { 1, -1, 3, -2 }) {
                double px = f.plotX(s[0]), py = f.plotY(s[1]), was = f.scale();
                f.zoomNotches(s[0], s[1], notches);
                still &= Math.abs(f.plotX(s[0]) - px) < 1e-6 && Math.abs(f.plotY(s[1]) - py) < 1e-6;
                step &= Math.abs(f.scale() / was - Math.pow(MapFrame.ZOOM_STEP, notches)) < 1e-12;
            }
        }
        check("a notch of the wheel zooms by 1.25 and the plot under the pointer stays under it", still && step);
        double cx = f.centreX(), cy = f.centreY();
        f.pan(40, -25);
        check("a drag moves the ground with the pointer", Math.abs(f.centreX() - (cx - 40 / f.scale())) < 1e-9
                && Math.abs(f.centreY() - (cy + 25 / f.scale())) < 1e-9);
        f.zoomNotches(600, 400, 100);
        double most = f.scale();
        f.zoomNotches(600, 400, -200);
        double least = f.scale();
        out.printf("      zoom runs from %.5f px a plot (%.1f km a pixel, the whole world across %d px) to %.0f px a plot%n",
                least, World.PLOT_M / least / 1000, MapTiles.DESIGN_H, most);
        check("zoom stops at 16 px a plot, and at the whole world across the view", most == MapFrame.MOST_PX_A_PLOT
                && Math.abs(least - MapTiles.DESIGN_H / (double) World.SIDE) < 1e-15);
        f.pan(1e12, -1e12);
        check("...and a drag never takes the view's centre off the world", f.centreX() == 0 && f.centreY() == World.SIDE);
        check("the levels change at the mockup's thresholds: L0 from 3.2 px a plot, L1 from 1.4, L2 below",
                MapFrame.levelOf(3.2) == MapFrame.L0 && MapFrame.levelOf(3.1999) == MapFrame.L1
                        && MapFrame.levelOf(1.4) == MapFrame.L1 && MapFrame.levelOf(1.3999) == MapFrame.L2);
        check("...L0's tiles at 4 px a plot, at 8 past 6 px a plot; L1's blocks of 4, L2's of 8",
                MapFrame.tilePx(MapFrame.L0, 6) == 4 && MapFrame.tilePx(MapFrame.L0, 6.0001) == 8
                        && MapFrame.blockOf(MapFrame.L1) == TileRaster.BLOCK_MIDDLE && MapFrame.blockOf(MapFrame.L2) == TileRaster.BLOCK_FAR
                        && MapFrame.blockOf(MapFrame.L0) == 0);
        f.set(370_000, 368_000, 1.2);
        int near = f.level();
        long nearTiles = f.tilesInView();
        f.set(370_000, 368_000, 0.3);
        int far = f.level();
        out.printf("      at 1.2 px a plot the expanded map holds %d tiles (L%d); at 0.3, %d (%s)%n", nearTiles, near, f.tilesInView(),
                far == MapFrame.FAR ? "the far nodes" : "L" + far);
        check("L2 draws its tiles while the view holds no more than 1,024 of them, the far nodes past that",
                near == MapFrame.L2 && far == MapFrame.FAR);
        MapFrame design = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        long l0 = design.tilesAt(MapFrame.L0_FROM);
        out.printf("      the expanded map at 1,389 x 868 (%d x %d): %d tiles at L0's least, %d at L1's; %d far nodes at most%n",
                MapTiles.DESIGN_W, MapTiles.DESIGN_H, l0, design.tilesAt(MapFrame.L1_FROM), design.nodesAtMost());
        check("a 1,389 x 868 L0 screen asks for no more tiles than section 5 times", l0 <= SCREEN_TILES);
        boolean nodes = true;
        for (double s : new double[] { 1.3, 0.9, 0.5, 0.11, 0.0011 }) {
            f.set(370_000, 368_000, s);
            double shown = f.nodePlots() * f.scale();
            nodes &= Long.bitCount(f.nodePlots()) == 1 && shown >= 1 - 1e-12 && shown < 2;
        }
        check("a far node's pixel is a power of two plots, shown 1 to 2 screen pixels wide", nodes);
        boolean bar = true;
        for (double s : new double[] { 16, 3.3, 1.0, 0.07, 0.0011 }) {
            f.set(370_000, 368_000, s);
            double[] b = f.scaleBar(120);
            double m = b[0];
            while (m >= 10) m /= 10;
            bar &= (Math.abs(m - 1) < 1e-9 || Math.abs(m - 2) < 1e-9 || Math.abs(m - 5) < 1e-9) && b[1] <= 120 && b[1] > 120 / 2.5;
        }
        check("the scale bar is 1, 2 or 5 times a power of ten metres, between 48 and 120 px", bar);
        check("...and says so: \"500 m\", \"2 km\", \"1,000 km\"", MapFrame.scaleWords(500).equals("500 m")
                && MapFrame.scaleWords(2000).equals("2 km") && MapFrame.scaleWords(1e6).equals("1,000 km"));
    }

    /** LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLane() before). */
    static void picks(Game g) {
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        double reach = LandMap.offersReach(land, market) + 4;
        long sx = land.siteX(), sy = land.siteY();
        int stride = Math.max(1, (int) (2 * reach / 240));
        int n = 0, owner = 0, placeOk = 0, inRect = 0, offers = 0, bought = 0, purchaseOk = 0, outside = 0, offRect = 0;
        java.util.Set<Integer> offerIds = new java.util.HashSet<>();
        for (long y = sy - (long) reach; y <= sy + (long) reach; y += stride) {
            for (long x = sx - (long) reach; x <= sx + (long) reach; x += stride) {
                LandMap.Pick p = LandMap.pick(land, market, x, y);
                n++;
                boolean owns = land.ownsPlot(x, y);
                if (owns == (p.owner() == LandMap.CENTRE || p.owner() == LandMap.BOUGHT)) owner++;
                int sp = GridOffers.sidePlace(x - sx, y - sy);
                LandParcel named = p.offer();
                boolean placed = named != null ? p.side() == named.getSide() && p.place() == named.getPlace()
                        : p.side() * GridOffers.PLACES + p.place() == sp;
                if (placed) placeOk++;
                if (p.owner() == LandMap.OFFER) {
                    offers++;
                    offerIds.add(named.getId());
                    if (inQuad(LandMap.rect(named), x + 0.5, y + 0.5) > 0 && !owns) inRect++;
                    else offRect++;
                }
                if (p.owner() == LandMap.BOUGHT) {
                    bought++;
                    if (p.purchase() >= 0) {
                        LandParcel o = land.purchases().get(p.purchase()).offer();
                        if (o == named && o.contains(x, y) && land.holdingOf(x, y) == p.purchase() + 1) purchaseOk++;
                    }
                }
                if (p.owner() == LandMap.OUTSIDE) outside++;
            }
        }
        out.printf("      %,d plots tried over the city and its offers: %,d owned (%,d bought), %,d in %d offers, %,d outside%n",
                n, n - offers - outside, bought, offers, offerIds.size(), outside);
        check("fixture: the plots tried cover owned ground, bought ground, offers and the world", bought > 0 && offers > 0 && outside > 0);
        check("a plot picks as the city's exactly when the model says it owns it", owner == n);
        check("...in its offer's side and place, else the side and place GridOffers.sidePlace() puts it in", placeOk == n);
        check("...a bought plot names the purchase whose rectangle holds it, its holding on the grid", purchaseOk == bought);
        check("...and a plot picked as an offer is a free plot inside that offer's rectangle as the map outlines it",
                offRect == 0 && inRect == offers);
        // Every offer's rectangle, the other way: a free plot of it picks it.
        int tried = 0, agreed = 0;
        for (LandParcel o : market.getListing()) {
            long fx = -1, fy = -1;
            for (long y = o.getY0(); y < o.getY1() && fx < 0; y++) {
                for (long x = o.getX0(); x < o.getX1() && fx < 0; x++) if (!land.ownsPlot(x, y)) { fx = x; fy = y; }
            }
            if (fx < 0) continue;
            tried++;
            LandMap.Pick p = LandMap.pick(land, market, fx, fy);
            if (p.owner() == LandMap.OFFER && p.offer().getId() == o.getId()) agreed++;
        }
        check("a free plot of every offer's rectangle picks that offer", tried == market.getListing().size() && agreed == tried);
        // The outline: every run on a plot edge between owned and free ground, and every such edge on a run.
        double[][] edge = LandMap.outline(land);
        LandGrid gr = land.grid();
        boolean between = true;
        double runLength = 0;
        for (int k = 0; k < edge[0].length; k++) {
            double x0 = edge[0][k], y0 = edge[1][k], x1 = edge[2][k], y1 = edge[3][k];
            runLength += Math.abs(x1 - x0) + Math.abs(y1 - y0);
            boolean flat = y0 == y1;
            for (double t = 0.5; t < Math.abs(flat ? x1 - x0 : y1 - y0); t += 1) {
                long ax = flat ? (long) (Math.min(x0, x1) + t) : (long) x0 - 1, ay = flat ? (long) y0 - 1 : (long) (Math.min(y0, y1) + t);
                long bx = flat ? ax : (long) x0, by = flat ? (long) y0 : ay;
                between &= land.ownsPlot(ax, ay) != land.ownsPlot(bx, by);
            }
        }
        long edges = 0;
        for (long y = gr.minY() - 1; y <= gr.maxY(); y++) {
            for (long x = gr.minX() - 1; x <= gr.maxX(); x++) {
                boolean o = land.ownsPlot(x, y);
                if (o != land.ownsPlot(x + 1, y)) edges++;
                if (o != land.ownsPlot(x, y + 1)) edges++;
            }
        }
        out.printf("      the outline: %,d runs, %,.0f plot edges long; %,d edges between owned and free plots%n", edge[0].length, runLength, edges);
        check("the city's outline runs along plot edges between its ground and the world's, every such edge once", between
                && runLength == edges && edge[0].length > 0);
        MapFrame small = new MapFrame(SMALL_W, SMALL_H);
        LandMap.open(small, land, market);
        out.printf("      the land office opens on it at %.2f px a plot (L%d), %d tiles%n", small.scale(), small.level(), small.tilesInView());
        boolean whole = true;
        for (int i = 0; i < edge[0].length; i++) {
            for (int e = 0; e < 2; e++) {
                double ex = edge[2 * e][i], ey = edge[2 * e + 1][i];
                whole &= small.screenX(ex) >= 0 && small.screenX(ex) <= SMALL_W && small.screenY(ey) >= 0 && small.screenY(ey) <= SMALL_H;
            }
        }
        check("the land office's small map opens on the whole city: every point of its outline in view", whole);
        // The hover's words, three places.
        List<String> centre = LandMap.hoverWords(land, market, sx, sy);
        LandParcel some = market.getListing().get(0);
        long ox = -1, oy = -1;
        for (long y = some.getY0(); y < some.getY1() && ox < 0; y++) {
            for (long x = some.getX0(); x < some.getX1() && ox < 0; x++) if (!land.ownsPlot(x, y)) { ox = x; oy = y; }
        }
        List<String> offer = LandMap.hoverWords(land, market, ox, oy);
        List<String> far = LandMap.hoverWords(land, market, sx + (long) (3 * reach), sy);
        out.println("      at the site: " + String.join(" | ", centre));
        out.println("      in an offer: " + String.join(" | ", offer));
        out.println("      out in the world: " + String.join(" | ", far));
        check("the hover names the centre, an offer by its side and place, and ground not the city's",
                centre.get(1).startsWith("The city's centre") && offer.get(1).startsWith("On offer: " + some.where() + " ")
                        && far.get(1).equals("Not the city's"));
        // A site under the pointer: the first owned iron site's plot.
        String field = null;
        for (int cell : CityLand.cellsUnder(sx - reach, sy - reach, sx + reach, sy + reach)) {
            for (Deposit d : CityLand.fields(World.of(land.seed()), cell, Resource.IRON)) {
                if (field != null) break;
                double[] at = d.siteAt(0);
                long x = Math.round(d.x() + at[0]), y = Math.round(d.y() + at[1]);
                for (LandMap.FieldAt fa : LandMap.fieldsAt(land, x, y)) if (fa.field().equals(d)) field = LandMap.fieldWords(fa);
            }
        }
        out.println("      on a site: " + field);
        check("...and a field under the pointer, its sites and tonnes", field != null && field.startsWith("Iron ore: a field of "));
    }

    /**
     * The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what
     * MapView draws by: the city's edge as maximal runs with its ground on
     * their right, drawn crisp - whole pixels, a pixel thick, just inside
     * the ground, each run meeting the next at a corner pixel; an offer's
     * rectangle on its pixel lines, clipped to the view, drawn over all of a
     * view it is larger than, and sharing a pixel line with an offer beside
     * it; the hatch on an offer's free plots exactly; the block lines at
     * MapFrame.BLOCK_LINES_FROM px and every block's width apart; and every
     * offer the opening zoom shows numbered by its place, on its free
     * ground.
     */
    static void overlay(Game g) {
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        LandGrid gr = land.grid();
        double[][] edge = LandMap.outline(land);
        int n = edge[0].length;
        // Its ground on each run's right, the world's on its left, every plot edge along it.
        boolean right = true;
        java.util.Set<String> starts = new java.util.HashSet<>();
        for (int k = 0; k < n; k++) {
            double x0 = edge[0][k], y0 = edge[1][k], x1 = edge[2][k], y1 = edge[3][k];
            starts.add(dir(x0, y0, x1, y1) + ":" + (long) x0 + "," + (long) y0);
            boolean flat = y0 == y1;
            long a = (long) (flat ? Math.min(x0, x1) : Math.min(y0, y1)), b = (long) (flat ? Math.max(x0, x1) : Math.max(y0, y1));
            for (long t = a; t < b; t++) {
                boolean in, out;
                if (flat) {
                    long y = (long) y0;
                    in = land.ownsPlot(t, x1 > x0 ? y : y - 1);
                    out = land.ownsPlot(t, x1 > x0 ? y - 1 : y);
                } else {
                    long x = (long) x0;
                    in = land.ownsPlot(y1 > y0 ? x - 1 : x, t);
                    out = land.ownsPlot(y1 > y0 ? x : x - 1, t);
                }
                right &= in && !out;
            }
        }
        int joinable = 0;
        for (int k = 0; k < n; k++) {
            if (starts.contains(dir(edge[0][k], edge[1][k], edge[2][k], edge[3][k]) + ":" + (long) edge[2][k] + "," + (long) edge[3][k])) joinable++;
        }
        out.printf("      the outline: %,d runs, each the whole straight stretch it lies on%n", n);
        check("each run of the city's edge has the city's ground on its right and the world's on its left (clockwise)", right && n > 0);
        check("...and is the whole straight stretch: no run goes on where another the same way ends", joinable == 0);
        // Drawn crisp at the land office's opening zoom (LandMap.open(), as ui/MapView opens it).
        double[] box = LandMap.openingBox(land);
        MapFrame fitted = new MapFrame(SMALL_W, SMALL_H), small = new MapFrame(SMALL_W, SMALL_H);
        fitted.fit(box[0], box[1], box[2], box[3]);
        LandMap.open(small, land, market);
        long leastSide = LandMap.smallestOfferSide(market);
        double opens = Math.max(fitted.scale(), MapFrame.OPENING_OFFER_PX / leastSide);
        out.printf("      the land office's fit is %.3f px a plot; its smallest offer, %d plots across, is %.0f px at %.3f: it opens at %.3f%n",
                fitted.scale(), leastSide, MapFrame.OPENING_OFFER_PX, MapFrame.OPENING_OFFER_PX / leastSide, small.scale());
        check("the land office opens at its fit, or closer while its smallest offer would be drawn under OPENING_OFFER_PX - to that, on the"
                + " fit's centre", leastSide > 0 && small.scale() == opens && small.centreX() == fitted.centreX() && small.centreY() == fitted.centreY());
        boolean whole = true, inside = true, met = true;
        int drawn = 0, corners = 0, long1 = 0;
        java.util.Map<String, List<double[]>> byStart = new java.util.HashMap<>();
        double[][] boxes = new double[n][];
        for (int k = 0; k < n; k++) {
            double x0 = edge[0][k], y0 = edge[1][k], x1 = edge[2][k], y1 = edge[3][k];
            double[] r = small.runOnScreen(x0, y0, x1, y1);
            boxes[k] = r;
            byStart.computeIfAbsent((long) x0 + "," + (long) y0, key -> new ArrayList<>()).add(r);
            boolean flat0 = y0 == y1;
            if ((flat0 ? Math.abs(small.pixelX(x1) - small.pixelX(x0)) : Math.abs(small.pixelY(y1) - small.pixelY(y0))) >= 1) long1++;
            if (r == null) continue;
            drawn++;
            for (double v : r) whole &= v == Math.rint(v);
            boolean flat = y0 == y1;
            whole &= flat ? r[3] == 1 && r[2] >= 1 : r[2] == 1 && r[3] >= 1;
            // The pixels just inside: the row after a run east's pixel line, the row before a run west's, and so on.
            double line = flat ? small.pixelY(y0) : small.pixelX(x0);
            double at = flat ? r[1] : r[0];
            inside &= flat ? at == (x1 > x0 ? line : line - 1) : at == (y1 > y0 ? line - 1 : line);
        }
        for (int k = 0; k < n; k++) {
            if (boxes[k] == null) continue;
            for (double[] next : byStart.getOrDefault((long) edge[2][k] + "," + (long) edge[3][k], List.of())) {
                if (next == null) continue;
                corners++;
                met &= touch(boxes[k], next);
            }
        }
        out.printf("      at the opening zoom (%.2f px a plot): %,d runs drawn (%,d a pixel long or more), %,d corners met%n", small.scale(),
                drawn, long1, corners);
        check("drawn crisp at the opening zoom: every run a pixel long on whole pixels, a pixel thick, on the pixels just inside the city's ground",
                drawn == long1 && drawn > 0 && whole && inside);
        check("...and each run meets the next at a corner pixel, shared or touching: a stepped line with no gap", corners >= drawn && met);
        // The offers' rectangles in the frame.
        boolean onLines = true;
        int pairs = 0, shared = 0;
        List<LandParcel> listing = market.getListing();
        for (LandParcel o : listing) {
            double[] b = small.onScreen(o.getX0(), o.getY0(), o.getX1(), o.getY1());
            if (b == null) continue;
            onLines &= b[0] == Math.max(small.pixelX(o.getX0()), -MapFrame.CLIP_PX) && b[1] == Math.max(small.pixelY(o.getY0()), -MapFrame.CLIP_PX)
                    && b[2] == Math.min(small.pixelX(o.getX1()), SMALL_W + MapFrame.CLIP_PX) && b[3] == Math.min(small.pixelY(o.getY1()), SMALL_H + MapFrame.CLIP_PX);
            for (LandParcel q : listing) {
                double[] c = small.onScreen(q.getX0(), q.getY0(), q.getX1(), q.getY1());
                if (q == o || c == null) continue;
                boolean besideX = o.getX1() == q.getX0() && o.getY0() < q.getY1() && q.getY0() < o.getY1();
                boolean besideY = o.getY1() == q.getY0() && o.getX0() < q.getX1() && q.getX0() < o.getX1();
                if (!besideX && !besideY) continue;
                pairs++;
                if (besideX ? b[2] == c[0] : b[3] == c[1]) shared++;
            }
        }
        out.printf("      %d offers; %d pairs side by side%n", listing.size(), pairs);
        check("an offer's box lies on its edges' pixel lines, clipped to the view, and two offers side by side share theirs",
                onLines && pairs > 0 && shared == pairs);
        MapFrame away = new MapFrame(SMALL_W, SMALL_H);
        away.set(land.siteX() + 20_000, land.siteY(), 1);
        int seenAway = 0;
        for (LandParcel o : listing) if (away.onScreen(o.getX0(), o.getY0(), o.getX1(), o.getY1()) != null) seenAway++;
        // An offer of 2 x 4 blocks of 15.36 km (level 9: a city of about 3,000 km2 of dry ground), seen from as close as the view comes.
        long b9 = 1L << BIG_OFFER_LEVEL, gx = Math.floorDiv(land.siteX(), b9) * b9, gy = Math.floorDiv(land.siteY(), b9) * b9;
        LandParcel big = new LandParcel(-2, 0, 0, BIG_OFFER_LEVEL, gx, gy, gx + 2 * b9, gy + 4 * b9, null, null, null, 0, 0);
        MapFrame near = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        near.set(gx + b9, gy + 2 * b9, MapFrame.MOST_PX_A_PLOT);
        double[] q = LandMap.rect(big), all = near.onScreen(big.getX0(), big.getY0(), big.getX1(), big.getY1());
        boolean cornersOff = true;
        for (int i = 0; i < 4; i++) {
            double sx = near.screenX(q[2 * i]), sy = near.screenY(q[2 * i + 1]);
            cornersOff &= sx < 0 || sx > near.width() || sy < 0 || sy > near.height();
        }
        // 0.7.61-0.7.68 drew an offer only when a corner lay within twice the view: this one has none there.
        boolean oldSeen = false;
        for (int i = 0; i < 4; i++) {
            double sx = near.screenX(q[2 * i]), sy = near.screenY(q[2 * i + 1]);
            oldSeen |= sx > -near.width() && sx < 2 * near.width() && sy > -near.height() && sy < 2 * near.height();
        }
        out.printf("      zoomed to %.0f px a plot on 2 x 4 blocks of %.2f km: its corners %s; the box %s%n", near.scale(), b9 * World.PLOT_M / 1000,
                cornersOff ? "all off the view" : "in view", all == null ? "none" : String.format("%.0f..%.0f x %.0f..%.0f", all[0], all[2], all[1], all[3]));
        check("an offer out of view is not drawn; one larger than the view is drawn over all of it, as the corners' test did not",
                seenAway == 0 && cornersOff && !oldSeen && all != null && all[0] <= 0 && all[1] <= 0 && all[2] >= near.width() && all[3] >= near.height());
        // The hatch: an offer's rectangle less the city's ground in it is exactly its free plots.
        boolean exact = true;
        int withOwned = 0;
        List<LandParcel> tried = new ArrayList<>(listing);
        // A rectangle of two blocks across the city's east edge on the site's row: its last owned plot and the free one past it.
        long ex = land.siteX(), ey = land.siteY(), kb = 1L << land.level();
        while (land.ownsPlot(ex + 1, ey)) ex++;
        tried.add(new LandParcel(-1, 1, 0, land.level(), ex + 1 - kb, ey, ex + 1 + kb, ey + kb, null, null, null, 0, 0));
        for (LandParcel o : tried) {
            List<double[]> own = LandMap.ownedIn(land, o);
            if (!own.isEmpty()) withOwned++;
            long ownedArea = 0;
            for (double[] r : own) {
                exact &= r[0] >= o.getX0() && r[1] >= o.getY0() && r[2] <= o.getX1() && r[3] <= o.getY1() && r[0] < r[2] && r[1] < r[3];
                ownedArea += (long) ((r[2] - r[0]) * (r[3] - r[1]));
            }
            long area = (o.getX1() - o.getX0()) * (o.getY1() - o.getY0());
            exact &= ownedArea == gr.owned(o.getX0(), o.getY0(), o.getX1(), o.getY1()) && area - ownedArea == gr.unowned(o.getX0(), o.getY0(), o.getX1(), o.getY1());
        }
        out.printf("      %d rectangles tried (the listing and one across the city's east edge); %d hold some of the city's ground%n", tried.size(), withOwned);
        check("fixture: a rectangle across the city's edge holds some of its ground", withOwned > 0);
        check("an offer's hatch is its rectangle less the city's ground in it - inside it, never overlapping: exactly its free plots", exact);
        // The block lines.
        int level = land.level();
        long block = 1L << level;
        MapFrame lines = new MapFrame(SMALL_W, SMALL_H);
        lines.set(land.siteX() + 0.5, land.siteY() + 0.5, MapFrame.BLOCK_LINES_FROM / block * 0.99);
        boolean noneUnder = lines.blockLines(level, true).length == 0 && lines.blockLines(level, false).length == 0;
        lines.set(land.siteX() + 0.5, land.siteY() + 0.5, MapFrame.BLOCK_LINES_FROM / block);
        double[] cols = lines.blockLines(level, true), rows = lines.blockLines(level, false);
        double px = block * lines.scale();
        boolean spaced = cols.length > 1 && rows.length > 1 && Math.abs(cols.length - SMALL_W / px) <= 1 && Math.abs(rows.length - SMALL_H / px) <= 1;
        for (double[] ls : new double[][] { cols, rows }) {
            for (int i = 1; i < ls.length; i++) spaced &= Math.abs(ls[i] - ls[i - 1] - px) <= 1;
        }
        boolean onBlocks = true;
        for (double c : cols) onBlocks &= Math.abs(lines.plotX(c) / block - Math.rint(lines.plotX(c) / block)) * px <= 0.5 && c >= 0 && c <= SMALL_W;
        MapFrame edgeOfWorld = new MapFrame(SMALL_W, SMALL_H);
        edgeOfWorld.set(0, 0, MapFrame.BLOCK_LINES_FROM / block);
        double[] fromWorld = edgeOfWorld.blockLines(level, true);
        out.printf("      the block lines at %.0f m: none at %.2f px a block, %d x %d at %.0f px%n", block * World.PLOT_M,
                MapFrame.BLOCK_LINES_FROM * 0.99, cols.length, rows.length, block * lines.scale());
        check("the city's block lines show from 6 px a block, a block's width apart, on the blocks' own lines, and none off the world",
                noneUnder && spaced && onBlocks && fromWorld.length > 0 && fromWorld[0] == edgeOfWorld.pixelX(0));
        // Every offer the opening zoom shows whole numbered by its place, on its freest square.
        int shown = 0, whole2 = 0, numbered = 0, onFree = 0, freeBlocks = 0, descended = 0, allFree = 0;
        boolean placeWords = true, freest = true;
        double least = MapFrame.PLACE_LABEL_FROM / small.scale();
        for (LandParcel o : tried) {
            double[] at = LandMap.labelBlock(land, o, least);
            if (gr.owned(o.getX0(), o.getY0(), o.getX1(), o.getY1()) == 0) {
                // All free: the whole rectangle.
                allFree++;
                freest &= at[0] == o.getX0() && at[1] == o.getY0() && at[2] == o.getX1() && at[3] == o.getY1();
                continue;
            }
            long ax0 = (long) at[0], ay0 = (long) at[1], side = (long) (at[2] - at[0]);
            long bk = 1L << o.getLevel(), most = 0, holder = -1;
            for (long y = o.getY0(); y < o.getY1(); y += bk) {
                for (long x = o.getX0(); x < o.getX1(); x += bk) {
                    long free = gr.unowned(x, y, x + bk, y + bk);
                    most = Math.max(most, free);
                }
            }
            for (long y = o.getY0(); y < o.getY1(); y += bk) {
                for (long x = o.getX0(); x < o.getX1(); x += bk) {
                    if (gr.unowned(x, y, x + bk, y + bk) == most && ax0 >= x && ay0 >= y && ax0 + side <= x + bk && ay0 + side <= y + bk) holder = x;
                }
            }
            boolean wholeFree = gr.owned(ax0, ay0, ax0 + side, ay0 + side) == 0;
            // In a freest block; a square; the whole block when it is wholly free; else as free as halving allows while a number still fits.
            freest &= holder >= 0 && at[3] - at[1] == side && Long.bitCount(side) == 1
                    && (most < bk * bk || side == bk) && (wholeFree || side == 1 || side / 2 < least || side == bk && bk < least);
            if (side < bk) descended++;
            if (most == bk * bk) {
                freeBlocks++;
                if (!land.ownsPlot(ax0 + side / 2, ay0 + side / 2)) onFree++;
            }
        }
        for (LandParcel o : listing) {
            placeWords &= LandMap.placeLabel(o).equals(String.valueOf(o.getPlace() + 1)) && o.where().endsWith(" " + LandMap.placeLabel(o));
            double[] b = small.onScreen(o.getX0(), o.getY0(), o.getX1(), o.getY1());
            if (b == null) continue;
            shown++;
            double[] at = LandMap.labelBlock(land, o, least), lb = small.onScreen(at[0], at[1], at[2], at[3]);
            boolean label = MapFrame.labelFits(lb) || MapFrame.labelFits(b);
            if (label) numbered++;
            if (b[0] >= 0 && b[1] >= 0 && b[2] <= SMALL_W && b[3] <= SMALL_H) {
                whole2++;
                placeWords &= label;
            }
        }
        out.printf("      the opening zoom shows %d of %d offers, %d of them whole, and numbers %d; of %d rectangles %d are all free, %d more"
                + " have a wholly free block, where the number stands on a free plot in %d; %d went down into a block's quarters%n", shown,
                listing.size(), whole2, numbered, tried.size(), allFree, freeBlocks, onFree, descended);
        check("an offer's number stands on its whole rectangle when all of it is free, else in its block with the most free ground - on a"
                + " free plot where that block is wholly free, else on the freest square in it that still holds a number",
                freest && allFree > 0 && freeBlocks > 0 && onFree == freeBlocks);
        check("the opening zoom numbers every offer it shows whole by its place, 1 to 6, as its row and its name read it",
                whole2 > 0 && placeWords);
    }

    /** The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners' test dropped; a city of about 3,000 km2 of dry ground is at level 9 (spec-grid 2.1's table). */
    static final int BIG_OFFER_LEVEL = 9;

    /** A run's way: E, S, W or N. */
    static char dir(double x0, double y0, double x1, double y1) {
        return y0 == y1 ? (x1 > x0 ? 'E' : 'W') : (y1 > y0 ? 'S' : 'N');
    }

    /** Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. */
    static boolean touch(double[] a, double[] b) {
        return a[0] <= b[0] + b[2] && b[0] <= a[0] + a[2] && a[1] <= b[1] + b[3] && b[1] <= a[1] + a[3];
    }

    /** Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. */
    static int inQuad(double[] q, double x, double y) {
        double sign = 0;
        boolean edge = false;
        for (int i = 0; i < 4; i++) {
            double ax = q[2 * i], ay = q[2 * i + 1], bx = q[(2 * i + 2) % 8], by = q[(2 * i + 3) % 8];
            double cross = (bx - ax) * (y - ay) - (by - ay) * (x - ax);
            double len = Math.hypot(bx - ax, by - ay);
            if (Math.abs(cross) <= 1e-9 * Math.max(1, len)) { edge = true; continue; }
            if (sign == 0) sign = Math.signum(cross);
            else if (Math.signum(cross) != sign) return -1;
        }
        return edge ? 0 : 1;
    }

    /** Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. */
    static void draft(Path root) throws Exception {
        GameFiles files = new GameFiles(root.resolve("draft"), root.resolve("draft-no-legacy"));
        Game g = new Game(files, LongPlaytest.founding());
        PrintStream real = System.out, was = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        boolean same, caughtUp, measured, otherGame, restated, sameAgain;
        int purchasesBetween;
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            for (int m = 0; m < DRAFT_MONTHS; m++) {
                if (m % 6 == 5) LongPlaytest.advise(g);
                LongPlaytest.run(g, 1);
            }
            // Saved with no map, so each city loaded from it has none until asked.
            g.saveGame(6, "draft");
            Game.MapDraft d = g.mapDraft();
            Thread t = new Thread(d::draw, "mapcheck-draft");
            t.start();
            t.join();
            CityMap here = CityMap.canonical(g.getCityLand(), g.getLandManager()::remainingByHolding, g.getMapTypes(), g.getMapCounts());
            same = g.adoptMap(d) && g.hasCityMap() && g.getCityMap().same(here);
            // Again with a month and a purchase between the draft and its keeping.
            Game g2 = new Game(files);
            g2.loadGameSave(6);
            Game.MapDraft d2 = g2.mapDraft();
            int before = g2.getCityLand().purchases().size();
            LandParcel best = g2.bestOffer(Game.LandNeed.room());
            if (best != null) g2.buyLandParcel(best.getId());
            LongPlaytest.villageBuild(g2, "House", 20);
            LongPlaytest.run(g2, 1);
            purchasesBetween = g2.getCityLand().purchases().size() - before;
            Thread t2 = new Thread(d2::draw, "mapcheck-draft-2");
            t2.start();
            t2.join();
            caughtUp = g2.adoptMap(d2) && Arrays.equals(g2.getCityMap().totals(), g2.getMapCounts());
            CityMap fresh = CityMap.canonical(g2.getCityLand(), g2.getLandManager()::remainingByHolding, g2.getMapTypes(), g2.getMapCounts());
            boolean ground = fresh.districts().size() == g2.getCityMap().districts().size();
            for (CityMap.District e : g2.getCityMap().districts()) {
                CityMap.District f = fresh.district(e.dx, e.dy);
                ground &= f != null && f.owned() == e.owned();
            }
            measured = ground;
            // Another city's draft, and a draft for land drawn again since, are not kept.
            Game g3 = new Game(files);
            g3.loadGameSave(6);
            Game.MapDraft d3 = g3.mapDraft();
            d3.draw();
            otherGame = !g.adoptMap(d3);
            g3.getLandManager().setOwnedSqFt(g3.getLandManager().getOwnedSqFt() * 2);
            restated = !g3.adoptMap(d3) && !g3.hasCityMap();
            // A draft drawn here and one drawn on another thread are the same map.
            Game g4 = new Game(files);
            g4.loadGameSave(6);
            Game.MapDraft a = g4.mapDraft(), b = g4.mapDraft();
            a.draw();
            Thread t4 = new Thread(b::draw, "mapcheck-draft-4");
            t4.start();
            t4.join();
            sameAgain = b.drawn() && a.map().same(b.map()) && a.map().same(here) && g4.adoptMap(a) && g4.adoptMap(b)
                    && g4.getCityMap() == a.map();
        } finally {
            System.setOut(real);
            LongPlaytest.out = was;
        }
        out.printf("      a %d-month town's map drawn on another thread and kept; a second drawn with %d purchase(s) and a month between%n",
                DRAFT_MONTHS, purchasesBetween);
        check("a map drawn on a copy of the land on another thread, kept, is the map drawn on the city", same);
        check("...one with a month and a purchase between the draft and its keeping sums to the month's counts", caughtUp
                && purchasesBetween > 0);
        check("...and holds the purchase's ground as a map drawn afresh measures it", measured);
        check("another city's draft is not kept, nor one for land drawn again since", otherGame && restated);
        check("a draft drawn on another thread and one drawn here are the same map; the second kept is the first", sameAgain);
    }

    /** Months the draft's town is played before its map is drawn: 24. */
    static final int DRAFT_MONTHS = 24;

    /** MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. */
    static void tiles(Squares sq) {
        CityMap m = sq.maps[0];
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        long tx0 = Math.floorDiv(sq.x, World.TILE) - SCREEN_ACROSS / 2, ty0 = Math.floorDiv(sq.y, World.TILE) - SCREEN_DOWN / 2;
        long[] stamps = new long[SCREEN_TILES];
        int[][] model = new int[SCREEN_TILES][];
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            stamps[j * SCREEN_ACROSS + i] = tiles.input(tx0 + i, ty0 + j, in);
            model[j * SCREEN_ACROSS + i] = in.model.clone();
        }
        boolean steady = true;
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            steady &= tiles.input(tx0 + i, ty0 + j, in) == stamps[j * SCREEN_ACROSS + i];
        }
        check("a tile's stamp holds while nothing it is painted from moves", steady);
        long[] c = sq.counts[0].clone();
        int house = -1;
        for (BuildingVisual.Type t : m.types()) if (t != null && t.category() == BuildingType.RESIDENTIAL && t.drawn()) { house = t.id(); break; }
        long changesBefore = m.changes();
        c[house] += 1;
        m.reconcile(c);
        int restamped = 0, modelMoved = 0, unexplained = 0;
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            int k = j * SCREEN_ACROSS + i;
            long s = tiles.input(tx0 + i, ty0 + j, in);
            boolean moved = !Arrays.equals(in.model, model[k]);
            if (moved) modelMoved++;
            if (s != stamps[k]) {
                restamped++;
                if (!moved) {
                    // A neighbour's first building gives this tile a port: its neighbours' roads moved.
                    boolean neighbour = false;
                    for (int[] o : new int[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } }) {
                        int a = i + o[0], b = j + o[1];
                        if (a >= 0 && b >= 0 && a < SCREEN_ACROSS && b < SCREEN_DOWN) {
                            neighbour |= !Arrays.equals(model[b * SCREEN_ACROSS + a], tilesModel(tiles, tx0 + a, ty0 + b));
                        }
                    }
                    if (!neighbour) unexplained++;
                }
            }
        }
        out.printf("      one more %s: the map's changes %d -> %d; %d of %d tiles' model counts moved, %d restamped%n",
                m.types()[house] == null ? "home" : "home (type " + house + ")", changesBefore, m.changes(), modelMoved, SCREEN_TILES, restamped);
        check("one more building moves the map's changes, which the view stamps its tiles again on", m.changes() > changesBefore);
        check("...and restamps only the tiles whose counts it changed, or a neighbour's road", unexplained == 0 && restamped <= 5 * Math.max(1, modelMoved));
        // The view's own path a frame takes, the ground kept, against section 5's (the ground read from the world), in turn:
        // at Jerus's city x 1 and x 10,000. Too few painted tiles are kept for a screen, so every round paints all 198.
        double[] view = { Double.MAX_VALUE, Double.MAX_VALUE }, modelMs = { Double.MAX_VALUE, Double.MAX_VALUE };
        int[] which = { 0, DENSE };
        MapTiles[] each = { new MapTiles(sq.maps[0]), new MapTiles(sq.maps[DENSE]) };
        int[] img = new int[MapTiles.tileImagePixels(PX)];
        TilePainter.Painted p = new TilePainter.Painted();
        long tx1 = Math.floorDiv(sq.x, World.TILE) - SCREEN_ACROSS / 2;
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int w = 0; w < which.length; w++) {
                long t0 = System.nanoTime();
                for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
                    each[w].pixels(tx1 + i, ty0 + j, MapFrame.L0, PX, in, img);
                }
                double ms = (System.nanoTime() - t0) / 1e6;
                t0 = System.nanoTime();
                CityMap mm = sq.maps[which[w]];
                for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
                    mm.tileInput(tx1 + i, ty0 + j, in);
                    TilePainter.paint(in, p);
                    TileRaster.raster(in, p, PX, img);
                }
                double mms = (System.nanoTime() - t0) / 1e6;
                if (round >= WARM_ROUNDS) {
                    view[w] = Math.min(view[w], ms);
                    modelMs[w] = Math.min(modelMs[w], mms);
                }
            }
        }
        out.printf("      the view's path (the ground kept, stamped, painted, rastered at %d px): x 1 %.1f ms, x %,.0f %.1f ms (%.2f x);"
                        + " section 5's path in turn %.1f and %.1f ms (%.2f x)%n", PX, view[0], TIMES[DENSE], view[1], view[1] / view[0],
                modelMs[0], modelMs[1], modelMs[1] / modelMs[0]);
        check("the view's own path for a 1,389 x 868 L0 screen takes no more than 80 ms, x 1 and x 10,000", view[0] <= SCREEN_MS && view[1] <= SCREEN_MS);
        check("...and no more than section 5's at either size: the view adds nothing a tile", view[0] <= modelMs[0] && view[1] <= modelMs[1]);
        long kept = 0;
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            TilePainter.Input k = each[1].paintedInput(tx1 + i, ty0 + j);
            if (k == null) continue;
            each[1].input(tx1 + i, ty0 + j, in);
            kept = Math.max(kept, MapTiles.paintedBytes(k, each[1].painted(tx1 + i, ty0 + j, in, MapTiles.stamp(in))));
        }
        MapFrame design = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        double mb = MapTiles.budgetBytes(design, kept) / 1e6;
        out.printf("      every cache of the expanded map at 1,389 x 868: %.1f MB (a painted tile kept at x 10,000: %,d bytes)%n", mb, kept);
        check("every cache of the view fits the design's 48 MB at 1,389 x 868", kept > 0 && mb <= MapTiles.BUDGET_MB);
        // A far node over the city x 1: its owned dry ground tinted by its districts, the rest dimmed.
        long plots = 16, span = MapFrame.NODE_PX * plots;
        long nx = Math.floorDiv(sq.x, span) * span, ny = Math.floorDiv(sq.y, span) * span;
        long t0 = System.nanoTime();
        byte[] ground = MapTiles.nodeTerrain(m.seed(), nx, ny, plots);
        double terrainMs = (System.nanoTime() - t0) / 1e6;
        int[] px = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        t0 = System.nanoTime();
        tiles.nodePixels(nx, ny, plots, ground, px);
        double pixelsMs = (System.nanoTime() - t0) / 1e6;
        int tinted = 0, dimmed = 0;
        for (int k = 0; k < px.length; k++) {
            long x = nx + (k % MapFrame.NODE_PX) * plots + plots / 2, y = ny + (k / MapFrame.NODE_PX) * plots + plots / 2;
            byte g = ground[k];
            boolean owned = m.land().ownsPlot(x, y);
            int bare = TileRaster.GROUND[g];
            if (!owned && px[k] == TileRaster.blend(bare, TileRaster.VOID, TileRaster.UNOWNED_DIM)) dimmed++;
            if (owned && g != World.SALT && g != World.FRESH && px[k] != bare) tinted++;
        }
        out.printf("      a far node (%d plots a pixel): its ground in %.1f ms on the worker, its pixels in %.1f ms; %,d tinted, %,d dimmed%n",
                plots, terrainMs, pixelsMs, tinted, dimmed);
        check("a far node tints the city's built ground by its districts and dims the rest", tinted > 0 && dimmed > 0);
    }

    /** A tile's model counts, as the map deals them now. */
    static int[] tilesModel(MapTiles tiles, long tx, long ty) {
        TilePainter.Input in = new TilePainter.Input();
        tiles.input(tx, ty, in);
        return in.model.clone();
    }
}
