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
 *      agree and no road meets a border away from a port, every tile lays
 *      exactly the road plots of each kind dealt to it and no bridge longer
 *      than its kind allows nor over the sea, the same inputs paint the same
 *      pixels (and the far view's blocks), and one more model building in a
 *      tile with room for it moves none of those placed before it (0.7.64; a
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
 *      world - agrees with the model's owns() and sideLane() on every plot
 *      tried and with the bands the map outlines; the map drawn on a copy of
 *      the land on another thread is the map drawn on the city, and one with
 *      a month between is caught up; a tile is restamped only when what it
 *      is painted from moved; the view's own per-tile path holds section 5's
 *      bounds; and every cache fits MapTiles.BUDGET_MB at the design's size.
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
     * the design's own fixture for the map's sizes.
     */
    static final long[] JERUS_COUNTS = { 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 6079, 0, 170, 114,
            1, 2, 0, 27, 12, 0, 8, 1, 1, 5, 385, 6, 47, 47, 40, 7, 6, 4, 2, 1, 2, 7, 37, 0, 2, 0, 2, 0, 0, 0, 6, 113, 106,
            1, 5, 0, 0, 14, 114, 148, 27, 11, 2, 11, 0, 0, 7, 114, 0, 0, 133, 0, 1120, 0 };

    /** ...his people. */
    static final long JERUS_PEOPLE = 509_455;

    /** ...and his dry ground, in km2. */
    static final double JERUS_KM2 = 89.63;

    /** His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. */
    static final double JERUS_FILL = 0.92;

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
                     * the band holding its centre, so a young city as the
                     * playtest plays it seldom buys one this early (the
                     * playtest's first came in month 135 while a site could
                     * be bought alone, 0.7.58-0.7.63). The fixture buys one
                     * as MiningCheck's does - the richest offer in iron, or
                     * the lane of the nearest field pushed out until its
                     * offer holds it, handed its price - and orders a mine,
                     * so the purchase's sites, wherever its field lays them,
                     * are on the map the months after.
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
                + " each kind, a mine once on its site", drawnSamples == MONTHS / DRAWN_EVERY && drawnExact == drawnSamples);
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
        /** Buildings dealt and drawn on no plot; drawn smaller than their own land; road plots dealt and not laid; tiles painted. */
        long dropped, shrunk, roadShort, tiles;

        long total() { return Arrays.stream(buildings).sum(); }
        int kinds() { int n = 0; for (long b : buildings) if (b > 0) n++; return n; }
        long roadPlots() { return roads[1] + roads[2] + roads[3]; }

        /** Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn), every road kind's plots, none without a plot. */
        boolean sameAs(long[] model, BuildingVisual.Type[] types) {
            boolean same = dropped == 0 && roadShort == 0;
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null) continue;
                long want = t < model.length ? model[t] : 0;
                if (types[t].drawn()) same &= buildings[t] == want;
            }
            for (int kind = BuildingVisual.GRAVEL; kind <= BuildingVisual.HIGHWAY; kind++) same &= roads[kind] == roadPlotsOf(model, types, kind);
            return same;
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
                    // Added: the first district in its direction with room for its whole plots. Taken away: the last holding one.
                    CityMap.District d = ds.get(outer == (sign > 0) ? ds.size() - 1 - i : i);
                    boolean fits = sign > 0 ? d.freeCells() >= each : d.count(t.getId()) > 0;
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
                int side = (int) Math.ceil(Math.sqrt(JERUS_KM2 * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
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
        int[] pts = new int[TilePainter.PORTS_MOST], pts2 = new int[TilePainter.PORTS_MOST];
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
                // Ports: each edge's are the shared edge's, read from either side.
                int nN = TilePainter.ports(seed, tx, ty, false, pts), nN2 = TilePainter.ports(seed, tx, ty, false, pts2);
                if (nN != nN2 || !Arrays.equals(Arrays.copyOf(pts, nN), Arrays.copyOf(pts2, nN2))) portsDiffer++;
                int[][] edges = { portsOf(seed, tx, ty, false), portsOf(seed, tx, ty + 1, false), portsOf(seed, tx, ty, true), portsOf(seed, tx + 1, ty, true) };
                int[] east = portsOf(seed, tx + 1, ty, true), eastsWest = portsOf(seed, tx + 1, ty, true);
                if (!Arrays.equals(east, eastsWest)) portsDiffer++;
                int T = World.TILE;
                for (int k = 0; k < T; k++) {
                    if (road(p, k) && !has(edges[0], k)) borderOff++;
                    if (road(p, (T - 1) * T + k) && !has(edges[1], k)) borderOff++;
                    if (road(p, k * T) && !has(edges[2], k)) borderOff++;
                    if (road(p, k * T + T - 1) && !has(edges[3], k)) borderOff++;
                }
                // Bridges: over fresh water only, never gravel, never longer than the kind allows.
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
        out.printf("      %.0f%% of buildings beside a road, %.0f%% in the row behind; %d mines on their sites%n",
                100.0 * besideRoad / Math.max(1, placed), 100.0 * rowTwo / Math.max(1, placed), mines);
        check("shared ports agree, read from either tile", portsDiffer == 0);
        check("no road meets a tile's border away from a port", borderOff == 0);
        check("every tile lays exactly the road plots of each kind the model's roads dealt it, a highway only where one was",
                notExact == 0 && laid == budget);
        check("no bridge is gravel, none crosses more fresh water than its kind may, and no road is on the sea",
                gravelBridge == 0 && longBridge == 0 && onSea == 0);
        check("every mine stands on its own site", minesOff == 0);
        check("the same inputs paint the same pixels, from a twin map drawn the same way", notSame == 0);
        bridges(m, tx0, ty0);
        moves(m, tx0, ty0);
    }

    /**
     * Bridges, where there is fresh water to cross: the default world's
     * founding river, every tile it runs through painted owned with the
     * dense screen's middle tile's buildings and its whole road budget paved,
     * no more than the tile's dry plots inside its edge ring hold (a fixture,
     * so roads that can bridge meet the river).
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
        int T = World.TILE, bridges = 0, wet = 0, gravel = 0, tooLong = 0, onSea = 0, borderOff = 0, crossings = 0;
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
            in.roadBudget[BuildingVisual.PAVED] = Math.min(innerDry, dense.roadBudget[1] + dense.roadBudget[2] + dense.roadBudget[3]);
            Arrays.fill(in.neighbourRoads, true);
            in.clearSites();
            TilePainter.paint(in, p);
            int[][] edges = { portsOf(w.seed(), tx, ty, false), portsOf(w.seed(), tx, ty + 1, false), portsOf(w.seed(), tx, ty, true), portsOf(w.seed(), tx + 1, ty, true) };
            for (int k = 0; k < T; k++) {
                if (road(p, k) && !has(edges[0], k)) borderOff++;
                if (road(p, (T - 1) * T + k) && !has(edges[1], k)) borderOff++;
                if (road(p, k * T) && !has(edges[2], k)) borderOff++;
                if (road(p, k * T + T - 1) && !has(edges[3], k)) borderOff++;
            }
            for (int q = 0; q < TilePainter.PLOTS; q++) {
                if (in.terrain[q] == World.FRESH) wet++;
                if (p.use[q] != TilePainter.ROAD) continue;
                if (in.terrain[q] == World.SALT) onSea++;
                if (!p.bridge[q]) continue;
                bridges++;
                if (p.road[q] == BuildingVisual.GRAVEL) gravel++;
                int x = q % T, y = q / T;
                boolean startX = x == 0 || !p.bridge[q - 1], startY = y == 0 || !p.bridge[q - T];
                int runX = 0, runY = 0;
                if (startX) for (int dx = 0; x + dx < T && p.bridge[q + dx]; dx++) runX++;
                if (startY) for (int dy = 0; y + dy < T && p.bridge[q + dy * T]; dy++) runY++;
                if (startX && runX > 1 || startY && runY > 1 || startX && startY) crossings++;
                if (Math.max(runX, runY) > TilePainter.MAX_BRIDGE[p.road[q]]) tooLong++;
            }
        }
        out.printf("      the founding river's %d tiles, paved: %,d plots of fresh water, %d bridge plots (gravel %d, too long %d, on the sea %d, at a border off a port %d)%n",
                tiles.size(), wet, bridges, gravel, tooLong, onSea, borderOff);
        check("fixture: roads meet the founding river and cross it on bridges", bridges > 0);
        check("...none of gravel, none longer than its kind may cross, none on the sea, none at a border off a port",
                gravel == 0 && tooLong == 0 && onSea == 0 && borderOff == 0);
    }

    /** A road plot. */
    static boolean road(TilePainter.Painted p, int i) { return p.use[i] == TilePainter.ROAD; }

    static int[] portsOf(long seed, long ax, long ay, boolean vertical) {
        int[] p = new int[TilePainter.PORTS_MOST];
        int n = TilePainter.ports(seed, ax, ay, vertical, p);
        return Arrays.copyOf(p, n);
    }

    static boolean has(int[] a, int v) {
        for (int x : a) if (x == v) return true;
        return false;
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
                // The tile's room as the deal reckons it: its free plots less the whole plots and road plots dealt it.
                long left = 0;
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
        double[] box = LandMap.openingBox(land);
        MapFrame small = new MapFrame(SMALL_W, SMALL_H);
        small.fit(box[0], box[1], box[2], box[3]);
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
