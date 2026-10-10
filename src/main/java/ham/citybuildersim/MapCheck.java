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
 * The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the drawn raster itself is one street network with + junctions apart and every building within reach, the sidecar comes back byte for byte, and a screen costs what the screen holds, never what the city does - at Jerus's size and at five and ten billion people.
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
 *      comes off the outermost district holding the type; and at every
 *      DRAWN_EVERY-th month, every tile painted, the map draws exactly what
 *      the city has - each type's buildings as many as the model's (since
 *      0.7.88 those its plans could not hold packed at the city's edge), a
 *      mine once on its site, its streets' surface kind by kind with what the
 *      plans have no street for the model's road plots, every plot of its
 *      track;
 *   2. nothing placed moves: one more building changes one district's count
 *      by one, inner types in the first district with room, outer types in
 *      the last; and (0.7.88, spec-roads-and-ports.md 8.4; one tile's count
 *      by one while the deal stood) in that district's plan it moves only the
 *      buildings placed after it - where the plan's ladder takes the same
 *      step and its first band is dealt round as many cells - and one fewer
 *      likewise;
 *   3. the painter draws the plan (0.7.88; the mockup's rules on the deal's
 *      tiles before): on the dense screen every plot a district's plan
 *      surfaces is painted as that street - kind and width - its seams once;
 *      no bridge crosses more fresh water than its line may (a street
 *      STREET_BRIDGE, an arterial ARTERIAL_BRIDGE) and no road is on the sea;
 *      every mine on its own site; the same inputs paint the same pixels,
 *      and the far view's blocks, from a twin map drawn the same way;
 *   4. the sidecar round-trips byte for byte through a save and a load, the
 *      next month plays to the same map, a stale or missing sidecar draws the
 *      map again canonically, a city with no map saves none, and (0.7.88) a
 *      FORMAT 4 sidecar still loads into the same map - and (0.7.89) it and
 *      0.7.88's FORMAT 5, which wrote no runs, with their runs laid as a map
 *      drawn afresh lays them; and at five and ten billion it is written and
 *      read back the same;
 *   5. the cost follows the screen: a 1,389 x 868 L0 screen (SCREEN_TILES)
 *      paints and rasters from its districts' plans in no more than SCREEN_MS
 *      at Jerus's city x 1, x 9,814 (5B), x 10,000 and x 19,629 (10B), each
 *      within SCREEN_RATIO of the 5B copy's, the first whose screen is all
 *      city; the plans themselves are made away from the screen (PlanCheck
 *      holds their PLAN_MS); and a month's change at 5B and 10B in no more
 *      than RECONCILE_MS (the design's 5 ms; measured 0.76) of the main
 *      thread's CPU - since 0.7.100 (A28) the thread's CPU, not the wall
 *      clock: on the two-core cloud machine the JIT's compiler threads took
 *      the main thread's core and a month's wall time jumped 3 to 4.5 ms over
 *      its CPU, failing the bound about one run in ten with no change in the
 *      work (runs/fixRD7-notes.md 4 and its star 4, and 7; runs/fixO11-notes.md
 *      5); the wall clock is printed beside it, and a JVM that cannot measure
 *      a thread's CPU is timed on the wall clock and says so;
 *   6. the city is drawn as what it has (0.7.64, batch L2): a new default
 *      city as founded, a month on and a year on draws its own buildings and
 *      nothing else - at its founding the bank and bare ground, no road, no
 *      highway; on the dense screen every building is drawn on its own
 *      type's land;
 *   7. the view's pure half (J4: MapFrame, LandMap, MapTiles, Game's draft):
 *      a screen point and its plot go back and forth, a notch of the wheel
 *      zooms by ZOOM_STEP with the plot under the pointer kept, the zoom
 *      stops at 16 px a plot and the whole world, the levels change at the
 *      mockup's thresholds and a screen asks for the tiles it shows; what a
 *      click picks on the played city - the centre, a purchase, an offer, the
 *      world - agrees with the model's grid (CityLand.holdingOf()) and
 *      GridOffers' sides and places on every plot tried and with the
 *      rectangles the map outlines; the map drawn on a copy of
 *      the land on another thread is the map drawn on the city, and one with
 *      a month between is caught up; a tile is restamped only when a plan it
 *      is painted from moved; the view's own per-tile path holds section 5's
 *      bounds; and every cache - since 0.7.88 the plans among them - fits
 *      MapTiles.BUDGET_MB at the design's size; and (0.7.69) the overlay on
 *      the block grid: the city's edge as whole straight runs with its
 *      ground on their right, drawn crisp on whole pixels just inside it
 *      with no gap at a corner; an offer's box on its pixel lines, shared
 *      with an offer beside it, drawn over all of a view it is larger than;
 *      its hatch on its free plots exactly; the block lines from 6 px a
 *      block; every offer at the opening zoom numbered;
 *   8. the network ON THE DRAWN RASTER (0.7.88, batch RD2; Jerus, of his
 *      screenshot: "does that look like highways that prefer going straight
 *      and must be connected? roads that arent doing endless + juncitons,
 *      and all roads connected, no stray roads?"), every tile of every
 *      district and a tile round them painted and put together - on Jerus's
 *      city x 1, the played city, and any save named on the command line:
 *      its streets one network, every other piece one the city's own ground
 *      does not join to it (the sea, water past an arterial's bridge, ground
 *      it does not own); no plot of road alone; no two + junctions of drawn
 *      street nearer than JUNCTION_APART (about eight houses' length);
 *      every building within REACH of a street, but those packed at the
 *      city's edge (R7) and the mines on their sites; the streets' surface
 *      the model's road plots kind by kind, with what the plans have no
 *      street for; every building drawn, one for one; and a river parts
 *      gravel streets no more than paved ones, its bridges no longer than
 *      their lines may span. And the city's RUNS (0.7.89, batch RD3;
 *      spec-roads-and-ports.md 2.7, 2.8): its highways drawn plot for plot
 *      as the runs lay them, one network, no building beside one (H5) and
 *      their junctions few; spec 2.7's table on the design's square city,
 *      each stage straight but where the sea, water past its bridge or the
 *      city's edge stopped it, and grown month by month without a plot
 *      moving (H4); every plot of the model's track drawn or counted short,
 *      one railway network; and its Rail Terminals as yards on the track
 *      in the cells nearest the mines - and (0.7.98, batch O14) the runs'
 *      ramps and 45-degree bands reach the screen: every tile they mark
 *      rastered through the view's own path as the model rasters it. On
 *      0.7.86's painter the network's checks fail on Jerus's save and the
 *      playtest (runs/fixRD2-notes.md): N3's passed because they measured
 *      the deal's x 1 fixture;
 *   9. at sea and on the shore (0.7.97, batch O13; runs/spec-oil.md 2.12
 *      and 5's O13 row, spec-roads-and-ports.md 2.8 and 4), on a square city
 *      on the default world's coast, a district west of its founding site:
 *      the refinery's units in one district, a unit more a month on there
 *      too, its plan placing them first of its industry in cells that touch
 *      (a side or a corner), and an older sidecar's units gathered
 *      once; each terminal and tank farm at the water - its box on owned dry
 *      ground in one cell's interior, no run or highway beside it, a
 *      terminal's quay over owned salt water that opens to the sea - laid one
 *      a month without one moving, the newest taken first, through a FORMAT 6
 *      sidecar and laid alike from a FORMAT 5; each drawn once on the tiles,
 *      its quay's plots once; a platform's jacket, wells, ring and pipe drawn
 *      once from the game's own platform (WellCheck's sea town); each route
 *      on the sea's cells to its offing past the city and on into the abyss,
 *      fading; a boat pure in (call, t), a frame touching only the routes on
 *      screen; and at 10B (the oil spec's prototype's trade and lanes) a
 *      frame's boats in no more than BOAT_FRAME_MS.
 *
 * Since 0.7.99 (batch W1, fewer and bigger deposits, in clusters) the
 * default world's site has no iron within ten kilometres, nor its dry place's
 * square for his city x 1: so 1's played city stands on MiningCheck.IRON_SEED's
 * world (its iron a kilometre out, as the default world's was), and the
 * fixtures that need his mines - 8's runs' marks on his city x 1 and its
 * rail yards - on his city x 1 again where its square holds iron
 * (Squares.ironX1, dryPlace(w, side)). The copies stand at the dry place.
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

    /** The design's bound on a month's change at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). */
    static final double RECONCILE_MS = 5;

    /** Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). */
    static final int PX = 4;

    /** Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. */
    static final int WARM_ROUNDS = 3, TIMED_ROUNDS = 5;

    /** How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. */
    static final double DRY_PLACE = 0.97;

    /** The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). */
    static final int DRAWN_EVERY = 30;

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
        String[] saves = Arrays.stream(args).filter(a -> !a.startsWith("-")).toArray(String[]::new);
        network(sq, city, saves);
        sidecar(city, root, sq);
        cost(sq);
        viewHalf(city, sq, root);
        atSea(sq.types, sq.seed);
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
        // On MiningCheck.IRON_SEED's world (0.7.99): its site has an iron field a kilometre out, as the default world's had; the
        // default world's nearest lies twelve kilometres out since the deposits are fewer and bigger.
        Game g = new Game(files, LongPlaytest.founding().withWorldSeed(MiningCheck.IRON_SEED));
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
                    drawnLines.append(String.format("      month %d: %,d buildings of %d kinds drawn (the model %,d; %d packed at the city's edge),"
                                    + " %,.1f plots of road as the streets' surface and %,.1f the streets do not carry (the model %,d)%n", g.getMonth(),
                            dr.total(), dr.kinds(), modelBuildings(model, map.types()), dr.packed, dr.surfaced(), dr.left(), modelRoadPlots(model, map.types())));
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
        check("at every " + DRAWN_EVERY + "th month the map draws exactly what the city has: each type's buildings, its road kind by kind"
                + " as its streets' surface and what they do not carry, every plot of its track, a mine once on its site",
                drawnSamples == MONTHS / DRAWN_EVERY && drawnExact == drawnSamples);
        return g;
    }

    /* ---------------------------------------------------------------------
       WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64)
       --------------------------------------------------------------------- */

    /** What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). */
    static final class Drawn {
        /** Buildings by type id: a mine or well on a site once, however many tiles its site spans; packed ones among them. */
        long[] buildings;
        /** The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). */
        final double[] surface = new double[4], leftover = new double[4];
        /** The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay; buildings packed at the city's edge (R7); tiles painted; districts. */
        long rail, railShort, packed, tiles, districts;

        long total() { return Arrays.stream(buildings).sum(); }
        int kinds() { int n = 0; for (long b : buildings) if (b > 0) n++; return n; }
        double surfaced() { return surface[1] + surface[2] + surface[3]; }
        double left() { return leftover[1] + leftover[2] + leftover[3]; }

        /** Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district (a street's least surface: DistrictPlan.HALF); every plot of its railway's track (0.7.72). */
        boolean sameAs(long[] model, BuildingVisual.Type[] types) {
            boolean same = true;
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null) continue;
                long want = t < model.length ? model[t] : 0;
                if (types[t].drawn()) same &= buildings[t] == want;
            }
            for (int kind = BuildingVisual.GRAVEL; kind <= BuildingVisual.HIGHWAY; kind++) {
                same &= Math.abs(surface[kind] + leftover[kind] - roadPlotsOf(model, types, kind)) <= DistrictPlan.HALF * Math.max(1, districts);
            }
            long track = 0;
            for (int t = 0; t < types.length && t < model.length; t++) if (types[t] != null && types[t].track()) track += model[t] * BuildingVisual.cells(types[t]);
            return same && rail + railShort == track;
        }
    }

    /** The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. */
    static long[] tilesOf(CityMap m) {
        long x0 = Long.MAX_VALUE, y0 = Long.MAX_VALUE, x1 = Long.MIN_VALUE, y1 = Long.MIN_VALUE;
        for (CityMap.District d : m.districts()) {
            long ax = (m.baseDX() + d.dx) * CityMap.TILES_A_SIDE, ay = (m.baseDY() + d.dy) * CityMap.TILES_A_SIDE;
            x0 = Math.min(x0, ax); y0 = Math.min(y0, ay);
            x1 = Math.max(x1, ax + CityMap.TILES_A_SIDE - 1); y1 = Math.max(y1, ay + CityMap.TILES_A_SIDE - 1);
        }
        return new long[] { x0 - 1, y0 - 1, x1 - x0 + 3, y1 - y0 + 3 };
    }

    /** Paints every tile of every district on a map, and a tile round them, and counts what is drawn. */
    static Drawn drawn(CityMap m) {
        Drawn d = new Drawn();
        d.buildings = new long[m.types().length];
        d.districts = m.districts().size();
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        java.util.Set<Long> sites = new java.util.HashSet<>();
        long[] box = tilesOf(m);
        for (long ty = box[1]; ty < box[1] + box[3]; ty++) {
            for (long tx = box[0]; tx < box[0] + box[2]; tx++) {
                m.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                d.tiles++;
                d.packed += p.packed;
                for (int kind = 1; kind < 4; kind++) d.surface[kind] += p.halves[kind] / 2.0;
                d.surface[BuildingVisual.HIGHWAY] += p.highwayPlots;
                d.rail += p.railLaid;
                for (int b = 0; b < p.buildings; b++) {
                    if (p.bsite[b] >= 0) {
                        if (sites.add(in.siteKey[p.bsite[b]])) d.buildings[p.btype[b]]++;
                    } else d.buildings[p.btype[b]]++;
                }
            }
        }
        double[] left = m.leftoverByKind();
        System.arraycopy(left, 0, d.leftover, 0, 4);
        d.railShort = m.runs().railShort();
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
        out.println("\n--- 2. nothing placed moves: one more building, and one fewer, on a copy of the city's map - and in its district's plan ---");
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
                DistrictPlan[] plans = new DistrictPlan[ds.size()];
                int expected = -1;
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
                    plans[d.index] = m.plan(d);
                }
                long[] changedCounts = counts.clone();
                changedCounts[t.getId()] += sign;
                m.reconcile(changedCounts);
                int changed = 0, by = 0, where = -1;
                for (CityMap.District d : m.districts()) {
                    for (int k = 0; k < d.counts.length; k++) {
                        if (d.counts[k] != before[d.index][k]) {
                            changed++;
                            by += d.counts[k] - before[d.index][k];
                            where = d.index;
                        }
                    }
                }
                String what = (sign > 0 ? "one more " : "one fewer ") + name;
                out.printf("      %s: %d district count(s) changed by %d, in district %d of %d (expected %d)%n", what, changed, by, where, ds.size(), expected);
                check(what + " changes one district's count, by " + sign + ", and no other", changed == 1 && by == sign);
                check(sign > 0 ? "...in the " + way + " district with room for it"
                               : "...from the " + (outer ? "nearest" : "outermost") + " district holding one", where == expected);
                // Its district's plan (spec 8.4): the first building placed differently is the one added (or, one fewer, the one
                // taken) - none placed before it moves - where the plan's ladder takes the same step and its first band is dealt
                // round as many cells; else the ladder relays the cells it changes (spec 2.9).
                if (where < 0) continue;
                DistrictPlan p0 = plans[where], p1 = m.plan(m.districts().get(where));
                int f = 0;
                while (f < p0.buildings && f < p1.buildings && p0.btype[f] == p1.btype[f] && p0.bx[f] == p1.bx[f] && p0.by[f] == p1.by[f]
                        && p0.bw[f] == p1.bw[f] && p0.bh[f] == p1.bh[f]) f++;
                boolean sameStep = p0.squares == p1.squares && p0.boulevards == p1.boulevards && p0.mixedSlots == p1.mixedSlots;
                DistrictPlan more = sign > 0 ? p1 : p0;
                boolean first = f >= more.buildings || more.btype[f] == t.getId();
                int moved = 0;
                for (int k = f; k < Math.min(p0.buildings, p1.buildings); k++) {
                    if (p0.btype[k] != p1.btype[k] || p0.bx[k] != p1.bx[k] || p0.by[k] != p1.by[k]) moved++;
                }
                out.printf("      ...its plan: %,d buildings placed; the first placed otherwise is number %,d (%s), %,d after it placed otherwise;"
                        + " the ladder's step %d/%d -> %d/%d, the first band's slots %d -> %d%n", p0.buildings, f,
                        f < more.buildings ? (more.btype[f] == t.getId() ? "the " + name.toLowerCase(java.util.Locale.ROOT) : "type " + more.btype[f]) : "none",
                        moved, p0.squares, p0.boulevards, p1.squares, p1.boulevards, p0.mixedSlots, p1.mixedSlots);
                check("...and in its district's plan it moves none placed before it, while the ladder takes the same step (spec 8.4)",
                        !sameStep || first);
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
        /** His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits are fewer and bigger. */
        final long ironX, ironY;
        final CityMap ironX1;
        final long[] ironCounts;

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
            long[] iron = dryPlace(w, x1Side(types));
            ironX = iron[0];
            ironY = iron[1];
            long[] c = counts[0].clone();
            long wantMines = mine >= 0 ? Math.round(JERUS_COUNTS[mine] * TIMES[0]) : 0;
            if (mine >= 0) c[mine] = 0;
            ironX1 = CityMap.square(seed, ironX, ironY, types, x1Side(types), (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), c);
            if (mine >= 0) {
                c[mine] = Math.min(wantMines, ironX1.ownedSites(Resource.IRON));
                ironX1.reconcile(c);
            }
            ironCounts = c;
            out.printf("      x 1 where its square holds iron: plot (%d, %d), %d cells from the founding site's; %,d mines on %,d sites%n",
                    ironX, ironY, iron[2], mine >= 0 ? c[mine] : 0, ironX1.ownedSites(Resource.IRON));
        }
    }

    /** The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). */
    static int x1Side(BuildingVisual.Type[] types) {
        return (int) Math.ceil(Math.sqrt(groundKm2(types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
    }

    /** The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. */
    static long[] dryPlace(World w) {
        return dryPlace(w, 0);
    }

    /**
     * ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's
     * city x 1 - `side` districts round its middle, as CityMap.square() lays
     * it - holds an iron field's centre, so his mines have sites and his
     * railway its mines: where Squares' ironX1 stands. Until 0.7.98 every
     * such square held iron; since the deposits are fewer and bigger, in
     * clusters, the dry place itself holds none on the default world.
     */
    static long[] dryPlace(World w, int side) {
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
                    if (dry >= DRY_PLACE * buf.length && (side <= 0 || holdsIron(w, cx, cy, side))) return new long[] { cx, cy, ring };
                }
            }
        }
        return new long[] { w.foundingX(), w.foundingY(), -1 };
    }

    /** Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. */
    static boolean holdsIron(World w, long x, long y, int side) {
        long bx = Math.floorDiv(x, CityMap.DISTRICT), by = Math.floorDiv(y, CityMap.DISTRICT);
        int h = side / 2;
        long x0 = (bx - h) * CityMap.DISTRICT, x1 = (bx - h + side) * CityMap.DISTRICT;
        long y0 = (by - h) * CityMap.DISTRICT, y1 = (by - h + side) * CityMap.DISTRICT;
        for (int cell : CityLand.cellsUnder(x0, y0, x1 - 1, y1 - 1)) {
            for (Deposit d : w.fieldsInCell(cell, Resource.IRON)) {
                if (d.x() >= x0 && d.x() < x1 && d.y() >= y0 && d.y() < y1) return true;
            }
        }
        return false;
    }

    /* =====================================================================
       3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on
       the deal's tiles until 0.7.87)
       ===================================================================== */

    static void painterRules(CityMap m) {
        out.println("\n--- 3. the painter draws the plan: the screen at the site of Jerus's city x 10,000 ---");
        long tx0 = Math.floorDiv(m.land().siteX(), World.TILE) - SCREEN_ACROSS / 2;
        long ty0 = Math.floorDiv(m.land().siteY(), World.TILE) - SCREEN_DOWN / 2;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int notSame = 0, mines = 0, minesOff = 0;
        long drawn = 0;
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
                for (int k = 0; k < p.buildings; k++) {
                    if (p.bsite[k] < 0) continue;
                    mines++;
                    int s = p.bsite[k];
                    if (p.bx[k] < in.siteX0[s] || p.by[k] < in.siteY0[s] || p.bx[k] + p.bw[k] - 1 > in.siteX1[s]
                            || p.by[k] + p.bh[k] - 1 > in.siteY1[s]) minesOff++;
                }
            }
        }
        Raster r = new Raster(m, tx0, ty0, SCREEN_ACROSS, SCREEN_DOWN);
        int[] asPlanned = r.asPlanned();
        int[] spans = r.bridges();
        out.printf("      %d tiles: %,d buildings drawn (%d mines on their sites); %,d street plots, %,d of them bridges%n", SCREEN_TILES, drawn, mines,
                r.streetPlots(), spans[2]);
        out.printf("      every plot the screen's districts' plans surface: %,d, painted as planned %,d; bridges too long %d, roads on the sea %d%n",
                asPlanned[0], asPlanned[0] - asPlanned[1], spans[0], spans[1]);
        check("fixture: the screen draws streets, and bridges", r.streetPlots() > 0 && spans[2] >= 0);
        check("every plot a district's plan surfaces is painted as that street, its kind and its width - a seam once, the district's"
                + " that surfaces it", asPlanned[0] > 0 && asPlanned[1] == 0);
        check("no bridge crosses more fresh water than its line may - a street STREET_BRIDGE, an arterial ARTERIAL_BRIDGE, a"
                + " highway MAX_BRIDGE - and no road is on the sea", spans[0] == 0 && spans[1] == 0);
        check("every mine stands on its own site", minesOff == 0);
        check("the same inputs paint the same pixels, from a twin map drawn the same way", notSame == 0);
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
     * highways; and on the dense screen every building is drawn on its own
     * type's land where its district's plan placed it (0.7.88; until 0.7.87
     * the deal's, a tenth of them drawn smaller), none packed.
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
                Raster r = Raster.of(map);
                long tracks = 0;
                for (int q = 0; q < r.w * r.h; q++) if (r.isStreet(q) && r.road[q] == TilePainter.TRACK) tracks++;
                lines.append(String.format("      month %d (%d months on), %,d people: it holds %s; the map draws %s, %,.1f plots of road, %,d of track%n",
                        g.getMonth(), months, g.getPopulationManager().getPopulation(), has.length() == 0 ? "nothing" : has,
                        what.length() == 0 ? "nothing" : what, d.surfaced(), tracks));
                if (months == 0) {
                    BuildingsTemplate bank = g.getBuildingManager().getTemplateByName("Commercial Bank");
                    bare = bank != null && d.total() == 1 && d.buildings[bank.getId()] == 1 && model[bank.getId()] == 1
                            && modelBuildings(model, map.types()) == 1 && d.surfaced() == 0 && r.count(r::isHighway) == 0;
                }
            }
        } finally {
            System.setOut(real);
        }
        out.print(lines);
        check("a new default city is drawn as what it holds, as founded, a month on and a year on: each type's buildings, no more, no less",
                exact);
        check("...as founded, its one Commercial Bank: no other building, no road (its streets tracks, the model's base streets: spec 2.2), no highway", bare);
        // The dense screen, painted: every building on its own type's land, where its district's plan put it.
        long tx0 = Math.floorDiv(dense.land().siteX(), World.TILE) - SCREEN_ACROSS / 2;
        long ty0 = Math.floorDiv(dense.land().siteY(), World.TILE) - SCREEN_DOWN / 2;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        BuildingVisual.Type[] types = dense.types();
        long drawnPlots = 0, buildings = 0, offLand = 0, packed = 0, free = 0, streets = 0;
        java.util.Set<CityMap.District> shown = new java.util.LinkedHashSet<>();
        for (int j = 0; j < SCREEN_DOWN; j++) {
            for (int i = 0; i < SCREEN_ACROSS; i++) {
                dense.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                shown.add(dense.districtOfTile(tx0 + i, ty0 + j));
                packed += p.packed;
                for (int b = 0; b < p.buildings; b++) {
                    if (p.bsite[b] >= 0) continue;
                    buildings++;
                    int[] f = BuildingVisual.footprint(types[p.btype[b]]);
                    boolean own = (p.bw[b] == f[0] && p.bh[b] == f[1]) || (p.bw[b] == f[1] && p.bh[b] == f[0]);
                    if (!own) offLand++;
                    drawnPlots += (long) p.bw[b] * p.bh[b];
                }
                for (int q = 0; q < TilePainter.PLOTS; q++) {
                    byte gr = in.terrain[q];
                    if (in.owned[q] && gr != World.SALT && gr != World.FRESH && p.site[q] == 0) free++;
                    if (p.isRoad(q)) streets++;
                }
            }
        }
        long placed = 0, refused = 0;
        for (CityMap.District d : shown) {
            if (d == null) continue;
            CityMap.Drawn dr = dense.drawn(d);
            placed += dr.placed();
            refused += dr.refused();
        }
        out.printf("      the dense screen's %d tiles, %d districts: %,d buildings drawn, %d off their own land, %d packed; buildings %.1f%% and streets %.1f%%"
                        + " of the free ground; its districts' plans place %,d and carry on %,d they cannot hold (R7)%n",
                SCREEN_TILES, shown.size(), buildings, offLand, packed, 100.0 * drawnPlots / Math.max(1, free), 100.0 * streets / Math.max(1, free), placed, refused);
        check("on the dense screen every building is drawn on its own type's land, turned or not, where its district's plan put it",
                buildings > 0 && offLand == 0);
        check("...none packed without a street there: the city's edge holds what it has no room for, not its middle", packed == 0);
    }

    /* =====================================================================
       8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its
       deal's x 1 fixture from 0.7.72)

       Jerus, of his screenshot of 0.7.84: "does that look like highways that
       prefer going straight and must be connected? roads that arent doing
       endless + juncitons, and all roads connected, no stray roads?". N3's
       checks passed on that map because they measured the deal's square
       fixture, not what a real city drew. These measure the raster itself -
       every tile of every district and a tile round them, painted and put
       together, plot by plot as the screen shows them - on Jerus's city x 1,
       on the played city of section 1, and on any save named on the command
       line (MapCheck <save folder> ...). On 0.7.86's painter they fail on his
       save and the playtest (runs/fixRD2-notes.md, the probe RD2Raster).
       ===================================================================== */

    /** A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. */
    static final class Raster {
        final CityMap map;
        final long px0, py0;
        final int w, h;
        final byte[] use, road, width, role, run;
        final boolean[] owned, sea, wet, bridge, beneath, mined;
        /** Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. */
        final List<int[]> boxes = new ArrayList<>();
        long rail, crossings, packed;
        final double[] surface = new double[4];
        final long[] buildings;
        final java.util.Set<Long> sites = new java.util.HashSet<>();

        Raster(CityMap map, long tx0, long ty0, int tw, int th) {
            this.map = map;
            px0 = tx0 * World.TILE;
            py0 = ty0 * World.TILE;
            w = tw * World.TILE;
            h = th * World.TILE;
            use = new byte[w * h];
            road = new byte[w * h];
            width = new byte[w * h];
            role = new byte[w * h];
            run = new byte[w * h];
            owned = new boolean[w * h];
            sea = new boolean[w * h];
            wet = new boolean[w * h];
            bridge = new boolean[w * h];
            beneath = new boolean[w * h];
            mined = new boolean[w * h];
            buildings = new long[map.types().length];
            TilePainter.Input in = new TilePainter.Input();
            TilePainter.Painted p = new TilePainter.Painted();
            for (int j = 0; j < th; j++) {
                for (int i = 0; i < tw; i++) {
                    map.tileInput(tx0 + i, ty0 + j, in);
                    TilePainter.paint(in, p);
                    crossings += p.crossings;
                    rail += p.railLaid;
                    packed += p.packed;
                    for (int k = 1; k < 4; k++) surface[k] += p.halves[k] / 2.0;
                    surface[BuildingVisual.HIGHWAY] += p.highwayPlots;
                    int ox = i * World.TILE, oy = j * World.TILE;
                    for (int k = 0; k < TilePainter.PLOTS; k++) {
                        int g = (oy + k / World.TILE) * w + ox + k % World.TILE;
                        use[g] = p.use[k];
                        road[g] = p.road[k];
                        width[g] = p.width[k];
                        role[g] = p.role[k];
                        owned[g] = in.owned[k];
                        sea[g] = in.terrain[k] == World.SALT;
                        wet[g] = sea[g] || in.terrain[k] == World.FRESH;
                        bridge[g] = p.bridge[k];
                        beneath[g] = p.beneath[k];
                        run[g] = p.run[k];
                    }
                    for (int b = 0; b < p.buildings; b++) {
                        boxes.add(new int[] { ox + p.bx[b], oy + p.by[b], p.bw[b], p.bh[b], p.btype[b], p.bsite[b], p.bpacked[b] ? 1 : 0 });
                        if (p.bsite[b] >= 0) {
                            if (sites.add(in.siteKey[p.bsite[b]])) buildings[p.btype[b]]++;
                            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) mined[(oy + y) * w + ox + x] = true;
                        } else buildings[p.btype[b]]++;
                    }
                }
            }
        }

        /** Every tile of every district of a map, and a tile round them. */
        static Raster of(CityMap map) {
            long[] b = tilesOf(map);
            return new Raster(map, b[0], b[1], (int) b[2], (int) b[3]);
        }

        /** A road plot: a street (a track among them, the track's level crossings among them), or a highway's. */
        boolean isRoad(int g) { return use[g] == TilePainter.ROAD || (use[g] == TilePainter.RAIL && road[g] != 0); }

        /** A street plot: a road plot with a street's role - not a highway's own. */
        boolean isStreet(int g) { return isRoad(g) && role[g] != 0; }

        /** A highway's own plot (its runs'). */
        boolean isHighway(int g) { return use[g] == TilePainter.ROAD && road[g] == BuildingVisual.HIGHWAY && role[g] == 0; }

        boolean isRail(int g) { return use[g] == TilePainter.RAIL; }

        /** A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. */
        boolean isRunHighway(int g) { return run[g] == BuildingVisual.HIGHWAY || run[g] == TilePainter.RAIL_OVER; }

        /** ...of its railway's track, under a yard or a mine too, or bridging a highway. */
        boolean isRunRail(int g) { return run[g] == TilePainter.RAIL || run[g] == TilePainter.RAIL_OVER; }

        long streetPlots() {
            long n = 0;
            for (int g = 0; g < w * h; g++) if (isStreet(g)) n++;
            return n;
        }

        int beside(int g, java.util.function.IntPredicate is) {
            int x = g % w, y = g / w, n = 0;
            if (x > 0 && is.test(g - 1)) n++;
            if (x < w - 1 && is.test(g + 1)) n++;
            if (y > 0 && is.test(g - w)) n++;
            if (y < h - 1 && is.test(g + w)) n++;
            return n;
        }

        /** The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. */
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

        /**
         * The road's pieces and those the city's own ground joins to the
         * largest: {pieces, plots of the largest, pieces the ground joins to
         * it} - from the largest piece, four ways over owned ground that is not
         * the sea or a mine's site, fresh water as far as an arterial's bridge
         * (ARTERIAL_BRIDGE) spans it along the way, a building's plots among
         * the ground (a piece buildings wall in is the plans' to join, as
         * PlanCheck's rule has it): another piece reached is one the ground
         * does not part.
         */
        int[] network() {
            int[] piece = pieces(this::isRoad);
            int n = piece[0];
            if (n <= 1) return new int[] { n, n == 0 ? 0 : (int) count(this::isRoad), 0 };
            int[] size = new int[n + 1];
            for (int g = 0; g < w * h; g++) if (piece[g + 1] > 0) size[piece[g + 1]]++;
            int main = 1;
            for (int k = 2; k <= n; k++) if (size[k] > size[main]) main = k;
            boolean[] seen = new boolean[w * h];
            int[] q = new int[w * h];
            int qh = 0, qt = 0;
            for (int g = 0; g < w * h; g++) if (piece[g + 1] == main) { seen[g] = true; q[qt++] = g; }
            java.util.Set<Integer> reached = new java.util.HashSet<>();
            while (qh < qt) {
                int u = q[qh++], ux = u % w, uy = u / w;
                for (int d = 0; d < 4; d++) {
                    int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                    if (ax < 0 || ay < 0 || ax >= w || ay >= h) continue;
                    int v = ay * w + ax;
                    if (seen[v]) continue;
                    if (piece[v + 1] != 0 && piece[v + 1] != main) { reached.add(piece[v + 1]); seen[v] = true; continue; }
                    if (!owned[v] || sea[v] || mined[v]) continue;
                    if (wet[v]) {
                        int run = 1;
                        for (int k = 1; k <= DistrictPlan.ARTERIAL_BRIDGE; k++) {
                            int bx = ax + TilePainter.DX[d] * k, by = ay + TilePainter.DY[d] * k;
                            if (bx < 0 || by < 0 || bx >= w || by >= h || !wet[by * w + bx]) break;
                            run++;
                        }
                        if (run > DistrictPlan.ARTERIAL_BRIDGE) continue;
                    }
                    seen[v] = true;
                    q[qt++] = v;
                }
            }
            return new int[] { n, size[main], reached.size() };
        }

        long count(java.util.function.IntPredicate is) {
            long n = 0;
            for (int g = 0; g < w * h; g++) if (is.test(g)) n++;
            return n;
        }

        /** Road plots with no road beside them, four ways: a stray. */
        long alone() {
            long n = 0;
            for (int g = 0; g < w * h; g++) if (isRoad(g) && beside(g, this::isRoad) == 0) n++;
            return n;
        }

        /** A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). */
        boolean crossing(int g) {
            if (!isStreet(g) || beside(g, this::isStreet) != 4) return false;
            int x = g % w, y = g / w;
            for (int dy = -1; dy <= 1; dy += 2) for (int dx = -1; dx <= 1; dx += 2) {
                int xx = x + dx, yy = y + dy;
                if (xx >= 0 && yy >= 0 && xx < w && yy < h && isStreet(yy * w + xx)) return false;
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

        /** Each plot's distance from a street, across corners (a street 0), to `most` + 1. */
        int[] reach(int most) {
            int[] dist = new int[w * h];
            Arrays.fill(dist, most + 1);
            int[] q = new int[w * h];
            int qh = 0, qt = 0;
            for (int g = 0; g < w * h; g++) if (isStreet(g)) { dist[g] = 0; q[qt++] = g; }
            while (qh < qt) {
                int g = q[qh++], x = g % w, y = g / w;
                if (dist[g] >= most) continue;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    int xx = x + dx, yy = y + dy;
                    if (xx < 0 || yy < 0 || xx >= w || yy >= h) continue;
                    int m = yy * w + xx;
                    if (dist[m] > dist[g] + 1) { dist[m] = dist[g] + 1; q[qt++] = m; }
                }
            }
            return dist;
        }

        /** A box's nearest plot's distance. */
        static int boxReach(int[] dist, int w, int[] b) {
            int best = Integer.MAX_VALUE;
            for (int y = b[1]; y < b[1] + b[3]; y++) for (int x = b[0]; x < b[0] + b[2]; x++) best = Math.min(best, dist[y * w + x]);
            return best;
        }

        /** The district a plot of the box is in. */
        CityMap.District districtAt(int g) {
            return map.districtOfTile(Math.floorDiv(px0 + g % w, World.TILE), Math.floorDiv(py0 + g / w, World.TILE));
        }

        /**
         * Every plot the plans of the districts in the box surface (Drawn's
         * codes, its frame's east column and south row among them) against
         * the painted plot: {plots, those not painted as that street - its
         * kind and its width}.
         */
        int[] asPlanned() {
            int plots = 0, off = 0, T = World.TILE;
            java.util.Set<CityMap.District> seen = new java.util.HashSet<>();
            for (int g = 0; g < w * h; g += 1) {
                CityMap.District d = districtAt(g);
                if (d == null || !seen.add(d)) continue;
                CityMap.Drawn dr = map.drawn(d);
                long dx0 = (map.baseDX() + d.dx) * CityMap.DISTRICT, dy0 = (map.baseDY() + d.dy) * CityMap.DISTRICT;
                for (int y = 0; y <= CityMap.DISTRICT; y++) {
                    for (int x = 0; x <= CityMap.DISTRICT; x++) {
                        long ax = dx0 + x - px0, ay = dy0 + y - py0;
                        if (ax < 0 || ay < 0 || ax >= w || ay >= h) continue;
                        byte c = dr.codeAt(x, y);
                        int kind = c & TilePainter.S_KIND, wd = (c >> TilePainter.S_WIDTH_SHIFT) & 3;
                        if (kind != DistrictPlan.GRAVEL && kind != DistrictPlan.PAVED && kind != DistrictPlan.HIGHWAY) continue;
                        if (wd == 0) continue;
                        plots++;
                        int gg = (int) (ay * w + ax);
                        if (!(isStreet(gg) && road[gg] == TilePainter.kindOf(c) && width[gg] == wd)) off++;
                    }
                }
            }
            return new int[] { plots, off };
        }

        /** The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. */
        int[] bridges() {
            int tooLong = 0, onSea = 0, plots = 0;
            for (int g = 0; g < w * h; g++) {
                if (!isRoad(g)) continue;
                if (sea[g]) onSea++;
                if (!bridge[g] || !wet[g]) continue;
                plots++;
                int x = g % w, y = g / w;
                // A run starting here along each axis, of this plot's kind of line.
                for (int axis = 0; axis < 2; axis++) {
                    int dx = axis == 0 ? 1 : 0, dy = 1 - dx;
                    int bx = x - dx, by = y - dy;
                    if (bx >= 0 && by >= 0 && wet[by * w + bx] && isRoad(by * w + bx)) continue;
                    int run = 0;
                    for (int k = 0; ; k++) {
                        int ax = x + dx * k, ay = y + dy * k;
                        if (ax >= w || ay >= h || !wet[ay * w + ax] || !isRoad(ay * w + ax)) break;
                        run++;
                    }
                    if (run < 2) continue;
                    int most = isHighway(g) ? TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY]
                            : role[g] == TilePainter.ARTERIAL || role[g] == TilePainter.BOULEVARD ? DistrictPlan.ARTERIAL_BRIDGE : DistrictPlan.STREET_BRIDGE;
                    if (run > most) tooLong++;
                }
            }
            return new int[] { tooLong, onSea, plots };
        }
    }

    /**
     * The network's rules on a map's drawn raster (0.7.88): one network but
     * where the ground parts it, no road alone, + junctions apart, every
     * building within reach, the streets' surface the model's road kind by
     * kind, every building drawn - `what` names the city.
     */
    static void networkOn(CityMap m, long[] model, String what) {
        Raster r = Raster.of(m);
        int[] net = r.network();
        long roadPlots = r.count(r::isRoad), alone = r.alone();
        long[] cr = r.crossings(TilePainter.JUNCTION_APART);
        int[] dist = r.reach(TilePainter.REACH);
        long near = 0, far = 0, mines = 0, packed = 0;
        for (int[] b : r.boxes) {
            if (b[5] >= 0) { mines++; continue; }
            if (b[6] == 1) { packed++; continue; }
            if (Raster.boxReach(dist, r.w, b) <= TilePainter.REACH) near++; else far++;
        }
        BuildingVisual.Type[] types = m.types();
        double[] left = m.leftoverByKind();
        double worst = 0;
        StringBuilder sf = new StringBuilder();
        for (int k = BuildingVisual.GRAVEL; k <= BuildingVisual.HIGHWAY; k++) {
            long want = roadPlotsOf(model, types, k);
            worst = Math.max(worst, Math.abs(r.surface[k] + left[k] - want));
            sf.append(String.format("%s%s %,.1f + %,.1f of %,d", k == 1 ? "" : "; ", k == 1 ? "gravel" : k == 2 ? "paved" : "highway", r.surface[k], left[k], want));
        }
        long off = 0, modelN = 0, drawnN = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || !types[t].drawn()) continue;
            long want = t < model.length ? model[t] : 0;
            modelN += want;
            drawnN += r.buildings[t];
            off += Math.abs(want - r.buildings[t]);
        }
        int[] pk = m.packedCounts();
        out.printf("      %s, %d districts, every tile and a tile round them: %,d road plots in %d piece(s) (the largest %,d), %d of them the city's"
                + " own ground joins to it; %,d alone; %,d + junctions, %d pairs nearer than %d%n", what, m.districts().size(), roadPlots, net[0], net[1],
                net[2], alone, cr[0], cr[1], TilePainter.JUNCTION_APART);
        out.printf("      ...%,d buildings, %,d within %d plots of a street, %d farther; %d mines on their sites, %d packed at the city's edge (%d drawn"
                + " smaller, %d with no ground); surface: %s; %,d buildings drawn of the model's %,d%n", near + far, near, TilePainter.REACH, far, mines,
                packed, pk[1], pk[2], sf, drawnN, modelN);
        check(what + ": fixture: the city has streets and + junctions", roadPlots > 0 && cr[0] > 0);
        check("...its streets one network: every other piece one the city's own ground does not join to it (the sea, water wider than an"
                + " arterial's bridge, ground it does not own)", net[2] == 0);
        check("...no plot of road alone, no stray road", alone == 0);
        check("...no two + junctions of drawn street nearer than JUNCTION_APART plots, either way: about eight houses' length apart", cr[1] == 0);
        check("...every building within REACH plots of a street, but those packed at the city's edge and the mines on their sites", far == 0);
        check("...its streets' surface the model's road plots kind by kind, with what the plans have no street for (half a plot a district)",
                worst <= DistrictPlan.HALF * Math.max(1, m.districts().size()));
        check("...and every building drawn, one for one", off == 0 && pk[2] == 0);
    }

    static void network(Squares sq, Game city, String[] saves) throws Exception {
        out.println("\n--- 8. the network on the drawn raster: one network, no road alone, + junctions apart, every building near a street, the surface,"
                + " one for one; highways, rail, the river ---");
        CityMap m = sq.maps[0];
        networkOn(m, m.totals(), "Jerus's city x 1");
        networkOn(city.getCityMap(), city.getMapCounts(), "the played city");
        for (String dir : saves) {
            Game g = loadCopy(Path.of(dir));
            if (g == null) {
                check("the save " + dir + " loads", false);
                continue;
            }
            networkOn(g.getCityMap(), g.getMapCounts(), "the save " + Path.of(dir).getFileName() + " (month " + g.getMonth() + ")");
        }
        Raster net = Raster.of(m);
        highways(net, "Jerus's city x 1");
        // The runs' marks on his city x 1 where its square holds iron (0.7.99): his railway runs from his mines.
        Raster ironNet = Raster.of(sq.ironX1);
        marksReachTheView(sq.ironX1, Math.floorDiv(ironNet.px0, World.TILE), Math.floorDiv(ironNet.py0, World.TILE), ironNet.w / World.TILE,
                ironNet.h / World.TILE, "Jerus's city x 1 where its square holds iron", false);
        marksReachTheView(sq.maps[DENSE], Math.floorDiv(sq.x, World.TILE) - SCREEN_ACROSS / 2, Math.floorDiv(sq.y, World.TILE)
                - SCREEN_DOWN / 2, SCREEN_ACROSS, SCREEN_DOWN, "the dense screen (x 10,000)", false);
        corridors(sq);
        rail(net, "Jerus's city x 1");
        CityMap yards = yardCity(sq);
        Raster yn = Raster.of(yards);
        rail(yn, "...with " + YARDS_ADDED + " Rail Terminals and " + FREIGHT_ADDED + " Freight Line added");
        yardsNearMines(yards, yn);
        riverCrossed(sq);
    }

    /** A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. */
    static Game loadCopy(Path src) throws Exception {
        Path work = Files.createTempDirectory("mapcheck-save");
        GameFiles files = new GameFiles(work.resolve("data"), work.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        boolean any = false;
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = src.resolve("autosave" + suffix);
            if (!Files.exists(f)) continue;
            Files.copy(f, files.savesDirectory().resolve("autosave" + suffix), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            any |= suffix.equals(".json");
        }
        if (!any) return null;
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            Game g = new Game(files);
            g.loadGameSave(GameFiles.AUTOSAVE_SLOT);
            g.getCityMap();
            return g;
        } finally {
            System.setOut(real);
        }
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
     * twin's, and its gravel streets across the river on gravel bridges, none
     * longer than its line may span (since 0.7.88 the plan's: a street's
     * STREET_BRIDGE, an arterial's ARTERIAL_BRIDGE).
     */
    static void riverCrossed(Squares sq) {
        World.River river = World.of(sq.seed).river();
        int[][] found = new int[2][];
        int[] kinds = { BuildingVisual.GRAVEL, BuildingVisual.PAVED };
        int tooLong = 0, onSea = 0;
        for (int i = 0; i < 2; i++) {
            Raster net = Raster.of(riverCity(sq, kinds[i]));
            int[] pieces = net.pieces(net::isRoad);
            int[] spans = net.bridges();
            tooLong += spans[0];
            onSea += spans[1];
            java.util.Set<Integer> near = new java.util.HashSet<>();
            int[] banks = new int[2];
            int[] laid = new int[5], bridged = new int[5];
            for (int g = 0; g < net.w * net.h; g++) {
                if (!net.isRoad(g)) continue;
                laid[net.road[g]]++;
                if (net.use[g] == TilePainter.ROAD && net.bridge[g] && net.wet[g]) bridged[net.road[g]]++;
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
            out.printf("      the river fixture, its streets all %s: %,d road plots in %d piece(s) (%,d tracks); within a tile of the river %,d and %,d on"
                            + " its banks, in %d piece(s); bridge plots gravel %d, paved %d%n", kinds[i] == BuildingVisual.GRAVEL ? "gravel" : "paved",
                    laid[1] + laid[2] + laid[3] + laid[4], pieces[0], laid[4], banks[0], banks[1], near.size(), bridged[1], bridged[2]);
        }
        int[] g = found[0], p = found[1];
        check("fixture: the river city stands on both banks of the founding river, its streets all gravel",
                g[2] > 0 && g[3] > 0 && g[4] > 0 && g[5] == 0 && g[6] == 0);
        check("its gravel streets cross the river on gravel bridges (since 0.7.77; gravel never bridged before)", g[7] > 0);
        check(String.format("the river parts gravel streets no more than paved: %d piece(s) near it against the paved twin's %d, %d in"
                + " the city against %d", g[1], p[1], g[0], p[0]), g[1] <= p[1] && g[0] <= p[0]);
        check("...and no bridge is longer than its line may span (a street STREET_BRIDGE, an arterial ARTERIAL_BRIDGE), none on the sea",
                tooLong == 0 && onSea == 0);
    }

    /**
     * The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7):
     * drawn plot for plot as its runs lay them, and with what they could not
     * lay the model's Elevated Highways; one network; no building plot
     * touching a highway plot, corners included (H5) - but a mine standing on
     * its site beneath one; and few junctions - every plot where highways
     * meet is an interchange of the runs or an arm's end against one.
     */
    static void highways(Raster net, String what) {
        CityMap m = net.map;
        CityRuns.Net hw = m.runs().highways();
        int W = net.w;
        long plots = net.count(net::isRunHighway), model = roadPlotsOf(m.totals(), m.types(), BuildingVisual.HIGHWAY);
        int[] pieces = net.pieces(net::isRunHighway);
        long junctions = 0;
        for (int g = 0; g < W * net.h; g++) if (net.isRunHighway(g) && net.beside(g, net::isRunHighway) >= 3) junctions++;
        boolean[] hwy = new boolean[W * net.h];
        for (int g = 0; g < W * net.h; g++) hwy[g] = net.isRunHighway(g);
        long beside = 0, minesUnder = 0;
        for (int[] b : net.boxes) {
            boolean touches = false;
            for (int y = b[1] - 1; y <= b[1] + b[3] && !touches; y++) {
                for (int x = b[0] - 1; x <= b[0] + b[2] && !touches; x++) {
                    if (x >= 0 && y >= 0 && x < W && y < net.h && hwy[y * W + x]) touches = true;
                }
            }
            if (!touches) continue;
            if (b[5] >= 0) minesUnder++;
            else beside++;
        }
        out.printf("      %s's highways: %,d plots drawn of the runs' %,d (%,d the runs could not lay; the model %,d), %d piece(s); %d junction plot(s) for %d"
                + " interchange(s) and %d arm(s); %d building(s) beside one, %d mine(s) on their sites beneath one%n", what, plots, hw.plots(),
                m.runs().highwayShort(), model, pieces[0], junctions, hw.interchanges(), hw.arms(), beside, minesUnder);
        check(what + ": fixture: the city has highways", plots > 0);
        check("...drawn plot for plot as its runs lay them, and with what they could not lay, the model's Elevated Highways",
                plots == hw.plots() && plots + m.runs().highwayShort() == model);
        check("...one highway network: \"highways that prefer going straight and must be connected\"", pieces[0] == 1);
        check("...no building plot touching a highway plot, corners included (H5), but a mine standing on its site beneath one", beside == 0);
        check("...junctions few: every plot where highways meet an interchange of the runs or an arm's end against one", junctions <= hw.interchanges() + hw.arms());
    }

    /**
     * The runs' marks reach the picture a screen draws (0.7.98, batch O14): a
     * screen rasters a tile from the view's kept copy of its inputs
     * (MapTiles.painted(), paintedInput()), and until then that copy held none
     * of the runs' marks (MapTiles.copy()) - so no ramp and no 45-degree band
     * was drawn, nor a ramp named on hover. Every tile of `tw` x `th` from
     * (tx0, ty0) the runs mark, through the view's own path (MapTiles.pixels())
     * against the model's (CityMap.tileInput(), TilePainter.paint(),
     * TileRaster.raster()): the same marks kept, the same pixels. The fixture:
     * tiles whose raster the marks change - drawn again with them blanked, as
     * the kept copy had them - and, with `bands`, 45-degree stretches among
     * them. Returns how many tiles the marks change.
     */
    static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands) {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input(), direct = new TilePainter.Input(), blank = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted(), pb = new TilePainter.Painted();
        int n = MapTiles.tileImagePixels(PX);
        int[] viewed = new int[n], drawn = new int[n], blanked = new int[n];
        long ramps = 0, diagonal = 0;
        int marked = 0, changed = 0, changedByBands = 0, keptAlike = 0, drawnAlike = 0;
        for (int j = 0; j < th; j++) {
            for (int i = 0; i < tw; i++) {
                m.tileInput(tx0 + i, ty0 + j, direct);
                long r = 0, b = 0, beyond = 0;
                for (byte k : direct.marks) {
                    if ((k & CityRuns.M_RAMP) != 0) r++;
                    if ((k & CityRuns.M_DIAG) != 0) b++;
                }
                for (byte k : direct.marksBeyond) if (k != 0) beyond++;
                if (r + b + beyond == 0) continue;
                marked++;
                ramps += r;
                diagonal += b;
                TilePainter.paint(direct, p);
                TileRaster.raster(direct, p, PX, drawn);
                m.tileInput(tx0 + i, ty0 + j, blank);
                Arrays.fill(blank.marks, (byte) 0);
                Arrays.fill(blank.marksBeyond, (byte) 0);
                TilePainter.paint(blank, pb);
                TileRaster.raster(blank, pb, PX, blanked);
                if (!Arrays.equals(drawn, blanked)) {
                    changed++;
                    if (b > 0) changedByBands++;
                }
                tiles.pixels(tx0 + i, ty0 + j, MapFrame.L0, PX, in, viewed);
                TilePainter.Input kept = tiles.paintedInput(tx0 + i, ty0 + j);
                if (kept != null && Arrays.equals(kept.marks, direct.marks) && Arrays.equals(kept.marksBeyond, direct.marksBeyond)) keptAlike++;
                if (Arrays.equals(viewed, drawn)) drawnAlike++;
            }
        }
        out.printf("      %s: %d tile(s) the runs mark (%,d ramp plot(s), %,d of 45-degree stretches), %d whose raster the marks change"
                + " (%d with a 45-degree stretch); through the view's path %d kept with their marks, %d rastered as the model rasters them%n",
                what, marked, ramps, diagonal, changed, changedByBands, keptAlike, drawnAlike);
        check(what + ": fixture: the runs' marks change the raster of some of its tiles" + (bands ? ", 45-degree stretches among them" : ""),
                changed > 0 && (!bands || changedByBands > 0));
        check("...and the view keeps every marked tile's marks and rasters it pixel for pixel as the model does: its ramps and"
                + " 45-degree bands drawn (MapTiles.copy(), 0.7.98)", keptAlike == marked && drawnAlike == marked);
        return changed;
    }

    /** Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototype's 6, 92, 400 and 1,385. */
    static final int[][] CORRIDOR_STAGES = { { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } };

    /**
     * The highways on corridors on the game's own ground (0.7.89; spec 2.7):
     * each stage of spec 2.7's table on a square city of highways alone at
     * the dry place, laid at once - its plots each laid or counted, one
     * network, straight but where its way was stopped (an arm bends only
     * where the sea, water wider than its bridge or the city's edge stopped
     * it within LOOK plots, and turns back only where its way home runs
     * TURN_COST), its junctions its interchanges and arms' ends; and on the
     * largest, grown stage by stage, no plot laid ever moved and a
     * demolition taking from the newest end (H4).
     */
    static void corridors(Squares sq) {
        out.println("      spec 2.7's table on the game's own ground (the dry place, a square city of highways alone):");
        out.printf("      %10s %9s %7s %8s %10s %13s %6s %6s%n", "radius km", "highways", "plots", "km", "corridors", "interchanges", "bends", "short");
        int hwType = -1;
        for (BuildingVisual.Type t : sq.types) if (t != null && t.road() == BuildingVisual.HIGHWAY) hwType = t.id();
        int each = BuildingVisual.cells(sq.types[hwType]);
        boolean laid = true, oneNet = true, straight = true, few = true;
        CityMap last = null;
        for (int[] st : CORRIDOR_STAGES) {
            long[] c = new long[sq.types.length];
            c[hwType] = st[1];
            CityMap m = CityMap.square(sq.seed, sq.x, sq.y, sq.types, st[0], CityMap.DISTRICT * CityMap.DISTRICT, c);
            CityRuns.Net hw = m.runs().highways();
            out.printf("      %10.1f %9d %7d %8.1f %10d %13d %6d %6d%n", st[0] * CityMap.DISTRICT * World.PLOT_M / 2000.0, st[1], hw.plots(),
                    hw.plots() * World.PLOT_M / 1000.0, hw.corridors(), hw.interchanges(), hw.bends(), m.runs().highwayShort());
            laid &= hw.plots() + m.runs().highwayShort() == (long) st[1] * each;
            Box b = Box.of(m);
            oneNet &= b.pieces() <= 1;
            straight &= bendsStopped(m, hw) == 0;
            few &= b.junctions() <= hw.interchanges() + hw.arms();
            last = m;
        }
        check("each stage: its highways' plots laid, or counted as what the runs could not lay", laid);
        check("...one highway network", oneNet);
        check("...straight but where its way was stopped: an arm bends only where the sea, water wider than its bridge or the city's"
                + " edge stopped it within LOOK plots, and turns back where its way home runs TURN_COST", straight);
        check("...few junctions: its interchanges and arms' ends against one", few);
        // ...and the largest stage's 45-degree stretches reach the screen (0.7.98).
        long[] lt = tilesOf(last);
        marksReachTheView(last, lt[0], lt[1], (int) lt[2], (int) lt[3], "the largest stage (" + CORRIDOR_STAGES[CORRIDOR_STAGES.length - 1][1] + " highways)", true);
        // Grown stage by stage on the largest square's ground, then taken back.
        CityRuns grown = new CityRuns();
        long[] site = last.site();
        byte[][] at = new byte[CORRIDOR_STAGES.length][];
        boolean kept = true;
        for (int i = 0; i < CORRIDOR_STAGES.length; i++) {
            grown.layTo((long) CORRIDOR_STAGES[i][1] * each, 0, 0, 0, 0, last.runGround(), site[0], site[1], site[0], site[1], new ArrayList<>());
            at[i] = Box.of(last, grown).fixed;
            if (i > 0) for (int g = 0; g < at[i].length; g++) if (at[i - 1][g] != 0 && at[i][g] == 0) kept = false;
        }
        grown.layTo((long) CORRIDOR_STAGES[1][1] * each, 0, 0, 0, 0, last.runGround(), site[0], site[1], site[0], site[1], new ArrayList<>());
        boolean back = Arrays.equals(Box.of(last, grown).fixed, at[1]);
        out.printf("      grown on the %d-district square's ground from %d to %d highways and taken back to %d: %s; %s%n", CORRIDOR_STAGES[3][0],
                CORRIDOR_STAGES[0][1], CORRIDOR_STAGES[3][1], CORRIDOR_STAGES[1][1], kept ? "no plot moved" : "plots moved", back ? "back where it was" : "not as it was");
        check("grown month by month, no highway plot laid ever moves (H4)", kept);
        check("...and a demolition takes from the newest end: taken back, the same plots as when it was that size", back);
    }

    /** The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. */
    static final class Box {
        final long x0, y0;
        final int w, h;
        final byte[] fixed;

        Box(long x0, long y0, int w, int h, CityRuns r) {
            this.x0 = x0; this.y0 = y0; this.w = w; this.h = h;
            fixed = new byte[w * h];
            r.fill(x0, y0, w, h, fixed, null, false);
        }

        static Box of(CityMap m) { return of(m, m.runs()); }

        static Box of(CityMap m, CityRuns r) {
            long[] t = tilesOf(m);
            return new Box(t[0] * World.TILE, t[1] * World.TILE, (int) t[2] * World.TILE, (int) t[3] * World.TILE, r);
        }

        boolean hw(int g) { return fixed[g] == CityRuns.F_HIGHWAY || fixed[g] == CityRuns.F_RAIL_OVER; }

        /** The highways' pieces, four ways joined. */
        int pieces() {
            int[] q = new int[w * h];
            boolean[] seen = new boolean[w * h];
            int n = 0;
            for (int s = 0; s < w * h; s++) {
                if (!hw(s) || seen[s]) continue;
                n++;
                int qh = 0, qt = 0;
                q[qt++] = s;
                seen[s] = true;
                while (qh < qt) {
                    int g = q[qh++], x = g % w, y = g / w;
                    int[] nb = { x > 0 ? g - 1 : -1, x < w - 1 ? g + 1 : -1, y > 0 ? g - w : -1, y < h - 1 ? g + w : -1 };
                    for (int k : nb) if (k >= 0 && hw(k) && !seen[k]) { seen[k] = true; q[qt++] = k; }
                }
            }
            return n;
        }

        /** Highway plots with highways on three or four sides: where they meet. */
        long junctions() {
            long n = 0;
            for (int g = 0; g < w * h; g++) {
                if (!hw(g)) continue;
                int x = g % w, y = g / w, k = 0;
                if (x > 0 && hw(g - 1)) k++;
                if (x < w - 1 && hw(g + 1)) k++;
                if (y > 0 && hw(g - w)) k++;
                if (y < h - 1 && hw(g + w)) k++;
                if (k >= 3) n++;
            }
            return n;
        }
    }

    /** The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_COST. */
    static int bendsStopped(CityMap m, CityRuns.Net n) {
        CityRuns.Ground g = m.runGround();
        int bad = 0;
        int[] why = new int[1];
        for (CityRuns.Arm a : n.arms) {
            for (int s = 0; s < a.segs; s++) {
                int before = s == 0 ? a.home : a.segHead[s - 1], now = a.segHead[s];
                if (now == before) continue;
                long x = a.segX[s], y = a.segY[s];
                if (now != a.home) {
                    int run = CityRuns.clear(n, g, x, y, before, CityRuns.LOOK, why);
                    if (run >= CityRuns.LOOK || why[0] == CityRuns.OPEN) bad++;
                } else if (CityRuns.clear(n, g, x, y, a.home, CityRuns.TURN_COST, why) < CityRuns.TURN_COST) bad++;
            }
        }
        return bad;
    }

    /** The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay, its streets crossing it level. */
    static void rail(Raster net, String what) {
        int W = net.w;
        int[] pieces = net.pieces(net::isRunRail);
        long model = 0;
        long[] totals = net.map.totals();
        for (int t = 0; t < totals.length; t++) if (net.map.types()[t] != null && net.map.types()[t].track()) model += totals[t] * BuildingVisual.cells(net.map.types()[t]);
        long plots = net.count(net::isRunRail), over = 0;
        for (int g = 0; g < W * net.h; g++) if (net.run[g] == TilePainter.RAIL_OVER) over++;
        long shortBy = net.map.runs().railShort();
        out.printf("      %s: %,d plots of track drawn of the model's %,d (%,d the runs could not lay), %d piece(s), %d crossed by streets, %d bridging a highway%n",
                what, net.rail, model, shortBy, pieces[0], net.crossings, over);
        check(what + ": fixture: the city has a railway", model > 0);
        check("...every plot of its track drawn, as the model has it, or counted as what its runs could not lay", net.rail + shortBy == model && plots == net.rail);
        check("...one railway network, no piece apart", pieces[0] == 1);
    }

    /** The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). */
    static final int YARDS_ADDED = 2, FREIGHT_ADDED = 1;

    /** The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding site and its yards are laid with the mines there. */
    static CityMap yardCity(Squares sq) {
        long[] c = sq.ironCounts.clone();
        int yard = -1, freight = -1, mine = -1;
        for (BuildingVisual.Type t : sq.types) {
            if (t == null) continue;
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
            if (t.site() == Resource.IRON) mine = t.id();
        }
        long[] rail = new long[c.length];
        for (int t = 0; t < c.length; t++) if (sq.types[t] != null && (sq.types[t].track() || sq.types[t].terminal())) { rail[t] = c[t]; c[t] = 0; }
        rail[yard] += YARDS_ADDED;
        rail[freight] += FREIGHT_ADDED;
        long wantMines = c[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        // Where his square holds iron (0.7.99, Squares.ironX1): the yards are laid with his mines.
        CityMap m = CityMap.square(sq.seed, sq.ironX, sq.ironY, sq.types, side, capacity, c);
        c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON));
        m.reconcile(c);
        for (int t = 0; t < c.length; t++) c[t] += rail[t];
        m.reconcile(c);
        return m;
    }

    /**
     * Every rail yard on its track, in the cell along it nearest a mine that
     * had room (spec 2.8: "laid as runs from the rail terminals, which sit in
     * estate cells nearest the mines"; 0.7.72's rule, restored): the track
     * runs through its box on its line, and no box along the track nearer a
     * mine had room for it when it was laid; each drawn once, as a building.
     */
    static void yardsNearMines(CityMap m, Raster net) {
        CityRuns r = m.runs();
        List<long[]> mines = m.minedSites();
        int terminal = -1;
        for (BuildingVisual.Type t : m.types()) if (t != null && t.terminal()) terminal = t.id();
        int[] fp = BuildingVisual.footprint(m.types()[terminal]);
        List<long[]> spots = r.yardSpots(fp[0], fp[1], mines);
        int onTrack = 0, nearest = 0, drawn = 0;
        List<long[]> before = new ArrayList<>();
        for (long[] y : r.yards()) {
            // On its track: its line's plots across the box all track.
            long lineY = Math.floorDiv(y[1], World.TILE) * World.TILE + CityRuns.RAIL_AT, lineX = Math.floorDiv(y[0], World.TILE) * World.TILE + CityRuns.RAIL_AT;
            boolean alongRow = lineY >= y[1] && lineY < y[1] + y[3], alongCol = lineX >= y[0] && lineX < y[0] + y[2];
            boolean all = false;
            if (alongRow) {
                all = true;
                for (long x = y[0]; x < y[0] + y[2]; x++) all &= CityRuns.covered(r.rail(), x, lineY);
            }
            if (!all && alongCol) {
                all = true;
                for (long yy = y[1]; yy < y[1] + y[3]; yy++) all &= CityRuns.covered(r.rail(), lineX, yy);
            }
            if (all) onTrack++;
            // The nearest with room: every box ranked before it had none, the yards before it laid.
            boolean first = true;
            for (long[] s : spots) {
                if (Arrays.equals(s, y)) break;
                if (r.yardFits(s, m.runGround(), mines, before)) { first = false; break; }
            }
            if (first) nearest++;
            before.add(y);
        }
        for (int[] b : net.boxes) if (b[4] == terminal) drawn++;
        out.printf("      %d rail yard(s) for the city's %d Rail Terminals: %d on their track, %d in the cell along it nearest a mine that had room; %d drawn%n",
                r.yards().size(), m.totals()[terminal], onTrack, nearest, drawn);
        check("fixture: the city has rail yards, and mines", r.yards().size() == YARDS_ADDED && !mines.isEmpty());
        check("every rail yard stands on its track, across its line", onTrack == r.yards().size());
        check("...in the cell along it nearest a mine that had room: rail terminals near mines", nearest == r.yards().size());
        check("...each drawn once, as the Rail Terminal it is", drawn == m.totals()[terminal]);
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
        // An older sidecar (FORMAT 4, 0.7.72 to 0.7.87: the districts, no runs; or 0.7.88's FORMAT 5, its runs' count none) still read
        // into the same districts (0.7.88), its runs laid from its counts on its ground (0.7.89) - as a map drawn afresh lays them.
        CityMap live = back.getCityMap();
        byte[] old = live.writeSidecar(back.getMonth(), CityMap.OLDEST_READ);
        CityMap fromOld = CityMap.readSidecar(old, back.getCityLand(), back.getLandManager()::remainingByHolding, back.getMapTypes(),
                back.getMonth(), CityMap.stampIn(old));
        byte[] none = live.writeSidecar(back.getMonth(), 5, false);
        CityMap fromNone = CityMap.readSidecar(none, back.getCityLand(), back.getLandManager()::remainingByHolding, back.getMapTypes(),
                back.getMonth(), CityMap.stampIn(none));
        CityMap afresh = CityMap.canonical(back.getCityLand(), back.getLandManager()::remainingByHolding, back.getMapTypes(), back.getMapCounts());
        out.printf("      the same map as a FORMAT %d sidecar: %,d bytes; as FORMAT %d %,d (its runs: %d arms, %,d highway and %,d track plots)%n",
                CityMap.OLDEST_READ, old.length, CityMap.FORMAT, live.writeSidecar(back.getMonth()).length, live.runs().runs(),
                live.runs().highways().plots(), live.runs().rail().plots());
        check("a FORMAT " + CityMap.OLDEST_READ + " sidecar (0.7.72 to 0.7.87) still loads, into the same districts, its runs laid from its counts",
                fromOld != null && fromOld.sameDistricts(live) && fromOld.runs().same(afresh.runs()));
        check("...and 0.7.88's FORMAT 5, which wrote no runs, likewise", fromNone != null && fromNone.sameDistricts(live)
                && fromNone.runs().same(afresh.runs()));
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

    /*
     * THE MONTH IS TIMED ON THE MAIN THREAD'S CPU (0.7.100, batch P1; Jerus's
     * A28 of 2026-10-10: "time it as thread CPU (not a loosening)"). On the
     * wall clock the 5 ms bound failed about one run in ten on the two-core
     * cloud machine with no change in the work: a month's wall time jumped 3
     * to 4.5 ms over its thread's CPU (5.43 ms for 0.97 in RD7's probe; 5.82
     * in a lone run of 0.7.95 and 5.17 on 0.7.94's classes, its control),
     * with no GC pause, safepoint or deopt in the window - the JIT's compiler
     * threads, compiling CityMap's month methods, took the main thread's core
     * (runs/fixRD7-notes.md 4 and its star 4, and 7; runs/fixO11-notes.md 5).
     * The design's bound is on the month's own work, which is the thread's
     * CPU; its value stays 5 ms, and the wall clock is printed beside it.
     * Where the JVM cannot measure a thread's CPU the month is timed on the
     * wall clock, and the output and the label say so.
     */
    /** The JVM's threads, read for the main thread's CPU time. */
    static final java.lang.management.ThreadMXBean THREADS = java.lang.management.ManagementFactory.getThreadMXBean();

    /** Whether a month is timed on the main thread's CPU: this JVM measures it and it is on (turned on here if it is off). */
    static final boolean CPU_TIMED = cpuTimed();

    static boolean cpuTimed() {
        if (!THREADS.isCurrentThreadCpuTimeSupported()) return false;
        try {
            if (!THREADS.isThreadCpuTimeEnabled()) THREADS.setThreadCpuTimeEnabled(true);
        } catch (UnsupportedOperationException | SecurityException e) {
            return false;
        }
        return THREADS.isThreadCpuTimeEnabled();
    }

    /** The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. */
    static long monthClock() {
        return CPU_TIMED ? THREADS.getCurrentThreadCpuTime() : System.nanoTime();
    }

    static void cost(Squares sq) {
        out.println("\n--- 5. the cost follows the screen, never the population ---");
        int k = TIMES.length;
        double[] best = new double[k];
        Arrays.fill(best, Double.MAX_VALUE);
        long[] drawn = new long[k];
        double[] cold = new double[k];
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
                // The first round makes the screen's plans (away from the screen in the game: MapView's worker), the rounds after
                // paint from them, as a screen does once they are drawn.
                if (round == 0) cold[i] = ms;
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
            out.printf("      x %,.0f: the screen's %d tiles - terrain, the plans' tiles, paint and raster at %d px - in %.1f ms (%.3f ms a tile),"
                    + " %,d buildings; %.2f x the 5B copy's; the first time, its plans made, %.0f ms%n", TIMES[i], SCREEN_TILES, PX, best[i],
                    best[i] / SCREEN_TILES, drawn[i], ratio, cold[i]);
            under &= best[i] <= SCREEN_MS;
            if (i != base) near &= ratio <= SCREEN_RATIO;
        }
        check("a 1,389 x 868 L0 screen paints and rasters from its districts' plans in no more than 80 ms, at every size", under);
        check("...and x 1, x 10,000 and 10B within 1.5 times the 5B copy's, the first all city: it follows the screen", near);
        // A month's change at 5B and 10B: +0.05%, a year of +0.01%, then -0.05%.
        for (int i = 0; i < k; i++) {
            if (TIMES[i] != 9_814 && TIMES[i] != 19_629) continue;
            CityMap m = sq.maps[i];
            long[] c = sq.counts[i].clone();
            int mine = -1;
            for (BuildingVisual.Type t : m.types()) if (t != null && t.site() == Resource.IRON) mine = t.id();
            long sites = m.ownedSites(Resource.IRON);
            // Each month on the bound's clock (monthClock(): the main thread's CPU) and on the wall clock; the
            // bound reads the year of +0.01% and the -0.05%, the worst of them.
            double worst = 0, worstsWall = 0, worstWall = 0, firstMs, firstWall, downMs, downWall;
            for (int t = 0; t < c.length; t++) c[t] += c[t] / 2000;
            if (mine >= 0) c[mine] = Math.min(c[mine], sites);
            long w0 = System.nanoTime(), t0 = monthClock();
            m.reconcile(c);
            firstMs = (monthClock() - t0) / 1e6;
            firstWall = (System.nanoTime() - w0) / 1e6;
            StringBuilder months = new StringBuilder();
            for (int month = 0; month < 12; month++) {
                for (int t = 0; t < c.length; t++) c[t] += c[t] / 10_000;
                if (mine >= 0) c[mine] = Math.min(c[mine], sites);
                w0 = System.nanoTime();
                t0 = monthClock();
                m.reconcile(c);
                double ms = (monthClock() - t0) / 1e6;
                double wall = (System.nanoTime() - w0) / 1e6;
                if (ms > worst) { worst = ms; worstsWall = wall; }
                worstWall = Math.max(worstWall, wall);
                months.append(String.format(" %.2f/%.2f", ms, wall));
            }
            for (int t = 0; t < c.length; t++) c[t] -= c[t] / 2000;
            w0 = System.nanoTime();
            t0 = monthClock();
            m.reconcile(c);
            downMs = (monthClock() - t0) / 1e6;
            downWall = (System.nanoTime() - w0) / 1e6;
            if (downMs > worst) { worst = downMs; worstsWall = downWall; }
            worstWall = Math.max(worstWall, downWall);
            String size = TIMES[i] < 10_000 ? "5" : "10";
            String clock = CPU_TIMED ? "the main thread's CPU" : "the wall clock (this JVM measures no thread's CPU)";
            out.printf("      x %,.0f, in ms of %s / on the wall clock: +0.05%% in %.2f/%.2f, then a year of +0.01%%:%s,"
                    + " then -0.05%% in %.2f/%.2f; the bounded months' worst %.2f (its wall clock %.2f), the wall clock's worst %.2f%n",
                    TIMES[i], clock, firstMs, firstWall, months, downMs, downWall, worst, worstsWall, worstWall);
            check("at " + size + " billion a month's change takes no more than 5 ms of "
                    + (CPU_TIMED ? "the main thread's CPU" : "the wall clock"), worst <= RECONCILE_MS);
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
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            stamps[j * SCREEN_ACROSS + i] = tiles.input(tx0 + i, ty0 + j, in);
        }
        // Each district the screen is drawn from, and its plan's stamp (0.7.88: a tile is painted from its plans).
        java.util.Map<CityMap.District, Long> planStamps = new java.util.HashMap<>();
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            for (CityMap.District d : m.tileDistricts(tx0 + i, ty0 + j)) planStamps.computeIfAbsent(d, x -> m.drawn(x).stamp);
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
        int restamped = 0, fromMoved = 0, unexplained = 0;
        java.util.Set<CityMap.District> plansMoved = new java.util.HashSet<>();
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            int k = j * SCREEN_ACROSS + i;
            long s = tiles.input(tx0 + i, ty0 + j, in);
            boolean moved = false;
            for (CityMap.District d : m.tileDistricts(tx0 + i, ty0 + j)) {
                Long was = planStamps.get(d);
                if (was == null || was != m.drawn(d).stamp || m.isEdge(d)) { moved = true; plansMoved.add(d); }
            }
            if (moved) fromMoved++;
            if (s != stamps[k]) {
                restamped++;
                if (!moved) unexplained++;
            }
        }
        out.printf("      one more %s: the map's changes %d -> %d; %d district plan(s) the screen is drawn from moved, %d of its %d tiles drawn"
                + " from them, %d restamped%n", m.types()[house] == null ? "home" : "home (type " + house + ")", changesBefore, m.changes(),
                plansMoved.size(), fromMoved, SCREEN_TILES, restamped);
        check("one more building moves the map's changes, which the view stamps its tiles again on", m.changes() > changesBefore);
        check("...and restamps only tiles drawn from a district plan it moved (its district's, those its chain carries on to, their"
                + " neighbours' edges; an edge district's packing)", unexplained == 0 && restamped > 0);
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
        long kept = 0, plan = 0;
        for (int j = 0; j < SCREEN_DOWN; j++) for (int i = 0; i < SCREEN_ACROSS; i++) {
            // Each tile painted and kept (PAINTED_KEPT is the hover's one), and weighed as kept.
            long stamp = each[1].input(tx1 + i, ty0 + j, in);
            TilePainter.Painted got = each[1].painted(tx1 + i, ty0 + j, in, stamp);
            TilePainter.Input k = each[1].paintedInput(tx1 + i, ty0 + j);
            if (k != null) kept = Math.max(kept, MapTiles.paintedBytes(k, got));
            CityMap.District d = sq.maps[DENSE].districtOfTile(tx1 + i, ty0 + j);
            if (d != null) plan = Math.max(plan, sq.maps[DENSE].drawn(d).bytes());
        }
        MapFrame design = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        double mb = MapTiles.budgetBytes(design, kept, plan) / 1e6, own = MapTiles.budgetBytes(design, kept) / 1e6;
        out.printf("      every cache of the expanded map at 1,389 x 868: %.1f MB - the view's %.1f and %d district plans of at most %,d bytes"
                + " (a painted tile kept at x 10,000: %,d bytes)%n", mb, own, CityMap.PLANS_KEPT, plan, kept);
        check("every cache of the view, the district plans it paints from among them, fits the design's 48 MB at 1,389 x 868",
                kept > 0 && plan > 0 && mb <= MapTiles.BUDGET_MB);
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


    /* =====================================================================
       9. AT SEA AND ON THE SHORE (0.7.97, batch O13)
       ===================================================================== */

    /** The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default world's founding site (atSea()). */
    static final int SHORE_SIDE = 3;

    /** A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured. */
    static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts) {
        int h = side / 2;
        long dx = Math.floorDiv(sx, CityMap.DISTRICT), dy = Math.floorDiv(sy, CityMap.DISTRICT);
        double[] centre = new double[CityLand.CENTRE_FIELDS];
        centre[CityLand.CENTRE_FIELDS - 5] = -1;
        centre[CityLand.CENTRE_FIELDS - 4] = -1;
        centre[CityLand.CENTRE_FIELDS - 2] = sx;
        centre[CityLand.CENTRE_FIELDS - 1] = sy;
        double[][] rects = { { (dx - h) * CityMap.DISTRICT, (dy - h) * CityMap.DISTRICT, (dx - h + side) * CityMap.DISTRICT, (dy - h + side) * CityMap.DISTRICT } };
        CityLand land = CityLand.restore(seed, centre, rects, null, null, null);
        return CityMap.canonical(land, r -> land.amountsInOrder(r), types, counts);
    }

    /** The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). */
    static final double BOAT_FRAME_MS = 0.5;

    /** Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. */
    static final int BOAT_FRAMES = 200;

    /** The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. */
    static long[] shoreCounts(BuildingVisual.Type[] types) {
        long[] c = new long[types.length];
        for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(JERUS_COUNTS[t] / 10.0);
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            if (t.site() != null) c[t.id()] = 0;
            if (t.campus()) c[t.id()] = 1;
            if (t.berth() >= 0) c[t.id()] = 2;
            else if (t.shore()) c[t.id()] = 1;
        }
        return c;
    }

    static void atSea(BuildingVisual.Type[] types, long seed) {
        out.println("\n--- 9. at sea and on the shore: the refinery's campus, the terminals at the water, the oil at sea, the routes and the boats ---");
        World w = World.of(seed);
        // The site one district west of the founding site's: that district is mostly sea, too little dry ground for the campus and a
        // unit more; the one west of it holds the campus with room to spare, and the 3 x 3 still takes in the coast's terminals.
        long sx = w.foundingX() - CityMap.DISTRICT, sy = w.foundingY();
        long[] counts = shoreCounts(types);
        CityMap m = coastal(seed, sx, sy, types, SHORE_SIDE, counts);

        // ---- THE CAMPUS
        int holding = 0, unitKinds = 0;
        CityMap.District campus = null;
        for (CityMap.District d : m.districts()) if (m.campusCells(d) > 0) { holding++; campus = d; }
        for (BuildingVisual.Type t : types) if (t != null && t.campus()) unitKinds++;
        out.printf("      a square city on the playtest's coast, %d districts a side: %,d buildings; the refinery's %d kinds of unit in %d district(s)%n",
                SHORE_SIDE, Arrays.stream(counts).sum(), unitKinds, holding);
        check("THE CAMPUS: drawn canonically, the refinery's units all stand in one district", holding == 1 && unitKinds > 0);
        int cracker = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.campus() && cracker < 0 && t.plots() < 100 && t.plots() > 50) cracker = t.id();
        long[] more = counts.clone();
        more[cracker]++;
        int had = campus.count(cracker);
        m.reconcile(more);
        check("...one more unit, a month on, goes to the district holding the refinery's ground", campus.count(cracker) == had + 1
                && m.campusCells(campus) > 0 && Arrays.equals(m.totals(), more));
        DistrictPlan p = m.plan(campus);
        boolean firstOfIndustry = true, seenOther = false;
        java.util.Set<Integer> cells = new java.util.HashSet<>();
        int placedUnits = 0;
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = types[p.btype[b]];
            if (t == null || t.outer() || t.cls() != BuildingVisual.INDUSTRY) continue;
            if (t.campus()) {
                firstOfIndustry &= !seenOther;
                cells.add((p.by[b] / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE + p.bx[b] / DistrictPlan.CELL);
                placedUnits++;
            } else seenOther = true;
        }
        int wantUnits = 0;
        for (BuildingVisual.Type t : types) if (t != null && t.campus()) wantUnits += campus.count(t.id());
        int overUnits = 0;
        for (BuildingVisual.Type t : types) if (t != null && t.campus() && t.id() < p.overflow.length) overUnits += p.overflow[t.id()];
        out.printf("      its plan, district (%d, %d): %d units placed of %d (%d left out), in %d cell(s) %s%n", campus.dx, campus.dy, placedUnits,
                wantUnits, overUnits, cells.size(), cells);
        check("...its plan places them first of its industry, every one, in cells that touch one another: one campus",
                firstOfIndustry && placedUnits == wantUnits && connected(cells));
        // An older sidecar's units, spread by hand into another district, gathered once as it is read; a FORMAT 6 one read as it is.
        CityMap.District away = null;
        for (CityMap.District d : m.districts()) if (d != campus && (away == null || d.index < away.index)) away = d;
        campus.counts[cracker]--;
        away.counts[cracker]++;
        for (CityMap.District d : new CityMap.District[] { campus, away }) { d.usedHalf = m.usedOf(d); d.usedCells = m.cellsOf(d); d.usedRoad = m.roadOf(d); }
        byte[] five = m.writeSidecar(1, 5), six = m.writeSidecar(1);
        CityMap fromFive = CityMap.readSidecar(five, m.land(), r -> m.land().amountsInOrder(r), types, 1, CityMap.stampIn(five));
        CityMap fromSix = CityMap.readSidecar(six, m.land(), r -> m.land().amountsInOrder(r), types, 1, CityMap.stampIn(six));
        int[] spread = new int[2];
        if (fromFive != null) for (CityMap.District d : fromFive.districts()) if (fromFive.campusCells(d) > 0) spread[0]++;
        if (fromSix != null) for (CityMap.District d : fromSix.districts()) if (fromSix.campusCells(d) > 0) spread[1]++;
        check("...a FORMAT 5 sidecar's units in two districts are gathered into the campus once as it is read; a FORMAT 6 one's stay",
                fromFive != null && spread[0] == 1 && fromSix != null && spread[1] == 2);
        campus.counts[cracker]++;
        away.counts[cracker]--;
        for (CityMap.District d : new CityMap.District[] { campus, away }) { d.usedHalf = m.usedOf(d); d.usedCells = m.cellsOf(d); d.usedRoad = m.roadOf(d); }

        // ---- THE SHORE
        CityShore shore = m.shore();
        int wantWorks = 0;
        for (int t : m.shoreTypes()) wantWorks += (int) more[t];
        SeaRoutes.Grid grid = new SeaRoutes.Grid(w);
        java.util.Set<Long> shut = new java.util.HashSet<>();
        boolean onDry = true, inCell = true, quays = true, clear = true, opens = true;
        for (CityShore.Work k : shore.works()) {
            long cx = Math.floorDiv(k.x0(), World.TILE) * World.TILE, cy = Math.floorDiv(k.y0(), World.TILE) * World.TILE;
            inCell &= k.x0() > cx && k.y0() > cy && k.x0() + k.w() <= cx + World.TILE && k.y0() + k.h() <= cy + World.TILE;
            for (long y = k.y0() - CityShore.CLEAR; y < k.y0() + k.h() + CityShore.CLEAR; y++) {
                for (long x = k.x0() - CityShore.CLEAR; x < k.x0() + k.w() + CityShore.CLEAR; x++) {
                    byte r = m.runs().at(x, y);
                    if (r == CityRuns.F_HIGHWAY || r == CityRuns.F_RAIL_OVER) clear = false;
                    if (!k.holds(x, y)) continue;
                    int tg = tileGround(w, x, y);
                    onDry &= m.land().ownsPlot(x, y) && tg != World.SALT && tg != World.FRESH;
                    if (r != 0) clear = false;
                    for (CityShore.Work o : shore.works()) if (o != k && (o.holds(x, y) || o.quayAt(x, y))) clear = false;
                }
            }
            for (int q = 1; q <= k.quay(); q++) {
                long[] at = k.quayPlot(q);
                quays &= m.land().ownsPlot(at[0], at[1]) && tileGround(w, at[0], at[1]) == World.SALT;
            }
            if (k.berthed()) {
                long[] e = k.quayPlot(k.quay() + 1);
                opens &= SeaRoutes.opensToSea(grid, e[0], e[1], shut);
            }
        }
        out.printf("      the shore: %d works of the model's %d laid (%d short): %s%n", shore.works().size(), wantWorks, m.shoreShort(), worksWords(shore, types));
        check("THE SHORE: every terminal and tank farm the model has is laid at the water, or counted short", shore.works().size() + m.shoreShort() == wantWorks
                && shore.works().size() > 0);
        check("...each box on owned dry ground, inside one cell's interior, no run on it, no highway beside it, a plot clear of every other work",
                onDry && inCell && clear);
        check("...each terminal's quay of its cargo's QUAY_PLOTS out over owned water, that opens to the sea", quays && opens
                && shore.works().stream().allMatch(k -> k.quay() == (types[k.type()].berth() >= 0 ? CityShore.QUAY_PLOTS[types[k.type()].berth()] : 0)));
        // Never moved: one more terminal a month, then one fewer.
        int gct = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.berth() == Ports.Cargo.GENERAL.ordinal()) gct = t.id();
        List<CityShore.Work> before = new ArrayList<>(shore.works());
        long[] next = more.clone();
        next[gct]++;
        m.reconcile(next);
        List<CityShore.Work> grown = new ArrayList<>(shore.works());
        next[gct] -= 2;
        m.reconcile(next);
        List<CityShore.Work> shrunk = new ArrayList<>(shore.works());
        boolean newestGone = true;
        int gone = 0;
        for (CityShore.Work k : grown) if (!shrunk.contains(k)) { gone++; newestGone &= k.type() == gct; }
        CityShore.Work lastGct = null, nextLast = null;
        for (CityShore.Work k : grown) if (k.type() == gct) { nextLast = lastGct; lastGct = k; }
        check("...laid month by month, nothing laid moves: a terminal more is laid and the rest stand; two fewer take the newest two",
                grown.size() == before.size() + 1 && grown.subList(0, before.size()).equals(before) && gone == 2 && newestGone
                        && !shrunk.contains(lastGct) && !shrunk.contains(nextLast));
        m.reconcile(more);
        byte[] fmt6 = m.writeSidecar(1), fmt5 = m.writeSidecar(1, 5);
        CityMap r6 = CityMap.readSidecar(fmt6, m.land(), r -> m.land().amountsInOrder(r), types, 1, CityMap.stampIn(fmt6));
        CityMap r5 = CityMap.readSidecar(fmt5, m.land(), r -> m.land().amountsInOrder(r), types, 1, CityMap.stampIn(fmt5));
        check("...kept in the sidecar: FORMAT 6 reads back the same works and writes the same bytes; a FORMAT 5 one has them laid alike from its counts",
                r6 != null && r6.same(m) && Arrays.equals(r6.writeSidecar(1), fmt6) && r5 != null && r5.shore().works().size() == shore.works().size());
        // Each drawn once: its type's plots and its quay's over every tile about the works.
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        long bx0 = Long.MAX_VALUE, by0 = Long.MAX_VALUE, bx1 = Long.MIN_VALUE, by1 = Long.MIN_VALUE;
        for (CityShore.Work k : shore.works()) {
            bx0 = Math.min(bx0, k.x0() - 20); by0 = Math.min(by0, k.y0() - 20);
            bx1 = Math.max(bx1, k.x0() + k.w() + 20); by1 = Math.max(by1, k.y0() + k.h() + 20);
        }
        Map<Integer, Long> plotsOf = new java.util.HashMap<>();
        long quayPlots = 0;
        for (long ty = Math.floorDiv(by0, World.TILE); ty <= Math.floorDiv(by1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(bx0, World.TILE); tx <= Math.floorDiv(bx1, World.TILE); tx++) {
                long stamp = tiles.input(tx, ty, in);
                TilePainter.Painted pt = tiles.painted(tx, ty, in, stamp);
                quayPlots += pt.quayPlots;
                for (int b = 0; b < pt.buildings; b++) {
                    BuildingVisual.Type t = types[pt.btype[b]];
                    if (t == null || !t.shore()) continue;
                    plotsOf.merge(t.id(), (long) Math.min(pt.bw[b], World.TILE - pt.bx[b]) * Math.min(pt.bh[b], World.TILE - pt.by[b]), Long::sum);
                }
            }
        }
        boolean once = true;
        long wantQuay = 0;
        Map<Integer, Long> wantOf = new java.util.HashMap<>();
        for (CityShore.Work k : shore.works()) { wantOf.merge(k.type(), (long) k.w() * k.h(), Long::sum); wantQuay += k.quay(); }
        for (Map.Entry<Integer, Long> e : wantOf.entrySet()) once &= plotsOf.getOrDefault(e.getKey(), 0L) >= e.getValue();
        for (Map.Entry<Integer, Long> e : plotsOf.entrySet()) once &= wantOf.getOrDefault(e.getKey(), 0L) >= e.getValue();
        out.printf("      drawn: %s plots of works (the shore's %s), %d of quay (%d)%n", plotsOf, wantOf, quayPlots, wantQuay);
        check("...each work drawn once on the tiles, on its own box, and every plot of its quay once", once && quayPlots == wantQuay);

        // ---- THE OIL AT SEA: the game's own platform (WellCheck's sea town).
        oilAtSea();

        // ---- THE ROUTES
        long t0 = System.nanoTime();
        List<BoatSchedule.Route> routes = m.seaRoutes();
        double routesMs = (System.nanoTime() - t0) / 1e6;
        double want = m.cityRadius() + SeaRoutes.OFFING_M / World.PLOT_M, abyss = SeaRoutes.ABYSS_M / World.PLOT_M;
        boolean onSea = true, offing = true, fades = true, spreadOk = true;
        int spreadN = 0, legs = 0, shortAbyss = 0;
        for (BoatSchedule.Route r : routes) {
            double bx = r.xs()[0], by = r.ys()[0];
            for (int i = 0; i + 1 < r.xs().length; i++, legs++) {
                double len = Math.hypot(r.xs()[i + 1] - r.xs()[i], r.ys()[i + 1] - r.ys()[i]);
                int n = (int) Math.ceil(len / SeaRoutes.CELL * 3) + 1;
                for (int k = 0; k <= n; k++) {
                    double x = r.xs()[i] + (r.xs()[i + 1] - r.xs()[i]) * k / n, y = r.ys()[i] + (r.ys()[i + 1] - r.ys()[i]) * k / n;
                    if (Math.hypot(x - bx, y - by) < 1.5 * SeaRoutes.CELL * Math.sqrt(2) + SeaRoutes.NEAR_CELLS * SeaRoutes.CELL && i == 0) continue;
                    onSea &= grid.sea(Math.floorDiv((long) Math.floor(x), SeaRoutes.CELL), Math.floorDiv((long) Math.floor(y), SeaRoutes.CELL));
                }
            }
            double out1 = Math.hypot(r.xs()[r.offing()] - bx, r.ys()[r.offing()] - by);
            offing &= out1 >= Math.min(want, SeaRoutes.RAY_CELLS * SeaRoutes.CELL) - 2 * SeaRoutes.CELL;
            double tail = r.length() - r.fadeFrom();
            fades &= tail >= SeaRoutes.CELL / 3.0 - 1e-6 && tail <= abyss + 1e-6;
            if (tail < abyss - 1e-6) shortAbyss++;
            if (r.spread() > 0) spreadN++;
            spreadOk &= r.spread() >= 0 && r.spread() <= Math.toRadians(SeaRoutes.SPREAD_DEG) + 1e-12;
        }
        out.printf("      the routes: %d found in %.0f ms, %d legs, the offing %.1f km past the city's radius %.1f km; %d of them spread, %d with land"
                + " short of ABYSS_M past the offing%n", routes.size(), routesMs, legs, SeaRoutes.OFFING_M / 1000, m.cityRadius() * World.PLOT_M / 1000,
                spreadN, shortAbyss);
        check("THE ROUTES: one a terminal, each leg on the sea's cells (past its berth's own)", routes.size() == shore.berths(types).size() && onSea);
        check("...each to its offing, the city's radius plus OFFING_M out from its berth (this coast is open), and on into the abyss: ABYSS_M,"
                + " or as far as the sea goes", offing && fades);
        check("...a call's last leg turned no more than SPREAD_DEG, where the fan keeps to the sea", spreadOk);

        // ---- THE BOATS: pure in (call, t), a frame touching only the routes on screen, and at 10B a frame's cost.
        BoatSchedule s = BoatSchedule.of(7, tradeAtSea(PROTO_TONNES / 4, PROTO_TONNES / 4, PROTO_TONNES / 4, PROTO_TONNES / 4), routes);
        boolean pure = true, alpha = true;
        double leg = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS;
        for (BoatSchedule.Call c : s.calls()) {
            BoatSchedule.Route r = routes.get(c.route());
            for (double t = 0; t <= 1; t += 1 / 97.0) {
                BoatSchedule.Boat a = BoatSchedule.position(c, r, t), b = BoatSchedule.position(c, r, t);
                pure &= java.util.Objects.equals(a, b);
                if (a != null) alpha &= a.alpha() >= 0 && a.alpha() <= 1;
            }
            // Coming in from the abyss's end it shows nothing; at the quay, whole.
            BoatSchedule.Boat far = BoatSchedule.position(c, r, c.arrives() - leg), docked = BoatSchedule.position(c, r, c.arrives());
            alpha &= far != null && far.alpha() < 1e-9 && docked != null && docked.docked() && docked.alpha() == 1;
        }
        // An L0 screen about the first terminal's berth.
        long hx = routes.isEmpty() ? sx : routes.get(0).berth().x(), hy = routes.isEmpty() ? sy : routes.get(0).berth().y();
        long l0w = Math.round(SCREEN_W_PX / MapFrame.L0_FROM), l0h = Math.round(SCREEN_H_PX / MapFrame.L0_FROM);
        long vx0 = hx - l0w / 2, vy0 = hy - l0h / 2, vx1 = vx0 + l0w, vy1 = vy0 + l0h;
        int crossing = 0, expect = 0;
        List<BoatSchedule.Boat> got = s.frame(vx0, vy0, vx1, vy1, .5);
        for (int r = 0; r < routes.size(); r++) {
            if (!BoatSchedule.crosses(routes.get(r), vx0, vy0, vx1, vy1)) continue;
            crossing++;
            for (BoatSchedule.Call c : s.calls()) if (c.route() == r && BoatSchedule.position(c, routes.get(r), .5) != null) expect++;
        }
        out.printf("      a month of the prototype's trade at m4000, a quarter of each kind: %d calls; an L0 screen about a berth crosses %d route(s),"
                + " %d boat(s) on them at mid-month%n", s.callCount(), crossing, expect);
        check("THE BOATS: a boat is pure in (call, t), fading only in the abyss; a frame finds every boat on the routes on screen, touching only those",
                pure && alpha && got.size() == expect && s.touched() == crossing && crossing > 0 && s.callCount() > 0);
        // 10B: BoatProto's case - its trade scaled to 10 billion, half of it in MR tankers and half in Capesizes (about its mixed 60,000 t
        // a call), on its 20,000 lanes along its coast; timed on an L0 screen and an L1 one at the lanes' middle, and on its own frame,
        // 8 districts wide over the lanes' whole length.
        double tonnes10B = PROTO_TONNES * 1e10 / PROTO_PEOPLE;
        BoatSchedule huge = BoatSchedule.of(7, tradeAtSea(tonnes10B / 2, tonnes10B / 2, 0, 0), protoLanes(sx, sy));
        long midX = sx + PROTO_LANE_DX / 2, midY = sy + PROTO_LANE_DY / 2;
        long[][] views = {
                { Math.round(SCREEN_W_PX / MapFrame.L0_FROM), Math.round(SCREEN_H_PX / MapFrame.L0_FROM) },
                { Math.round(SCREEN_W_PX / MapFrame.L1_FROM), Math.round(SCREEN_H_PX / MapFrame.L1_FROM) },
                { 8L * World.DISTRICT, Math.abs(PROTO_LANE_DY) + 2 } };
        double[] best = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
        int[] seen = new int[views.length];
        huge.frame(vx0, vy0, vx1, vy1, .5);
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int v = 0; v < views.length; v++) {
                long ax = midX - views[v][0] / 2, ay = midY - views[v][1] / 2, bx = ax + views[v][0], by = ay + views[v][1];
                long f0 = System.nanoTime();
                int n = 0;
                for (int k = 0; k < BOAT_FRAMES; k++) n += huge.frame(ax, ay, bx, by, (k + 0.5) / BOAT_FRAMES).size();
                double ms = (System.nanoTime() - f0) / 1e6 / BOAT_FRAMES;
                if (round >= WARM_ROUNDS) best[v] = Math.min(best[v], ms);
                seen[v] = n / BOAT_FRAMES;
            }
        }
        out.printf("      at 10B (%,d calls a month on %,d lanes): a frame of an L0 screen %.3f ms (%,d boats), of an L1 screen %.3f ms (%,d boats),"
                + " of the prototype's 8 districts %.3f ms (%,d boats; it measured 0.38)%n", huge.callCount(), PROTO_LANES, best[0], seen[0], best[1],
                seen[1], best[2], seen[2]);
        check("...at 10 billion a frame's boats take no more than BOAT_FRAME_MS (0.5 ms): an L0 screen, an L1 one and the prototype's 8 districts",
                best[0] <= BOAT_FRAME_MS && best[1] <= BOAT_FRAME_MS && best[2] <= BOAT_FRAME_MS);
    }

    /** The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. */
    static final int SCREEN_W_PX = 1389, SCREEN_H_PX = 868;

    /** BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. */
    static final double PROTO_TONNES = 896_308, PROTO_PEOPLE = 469_092;

    /** BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). */
    static final int PROTO_LANES = 20_000;
    static final long PROTO_LANE_DX = 200, PROTO_LANE_DY = -1_500;

    /**
     * BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along
     * its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span
     * in plots (rough)" - every other one liquid bulk's, the rest dry bulk's.
     */
    static List<BoatSchedule.Route> protoLanes(long cx, long cy) {
        long span = (long) (Math.sqrt(1e10 / 5000.0) * 33);
        List<BoatSchedule.Route> out = new ArrayList<>(PROTO_LANES);
        long h = 12345;
        for (int i = 0; i < PROTO_LANES; i++) {
            h = World.mix(h);
            long x = cx - span / 2 + (long) (World.unit(h) * span);
            BoatSchedule.Berth b = new BoatSchedule.Berth(i % 2 == 0 ? Ports.Cargo.LIQUID : Ports.Cargo.DRY_BULK, x, cy);
            out.add(new BoatSchedule.Route(b, x + PROTO_LANE_DX, cy + PROTO_LANE_DY));
        }
        return out;
    }

    /** The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. */
    static int tileGround(World w, long x, long y) {
        byte[] t = new byte[TilePainter.PLOTS];
        w.tileTerrain(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE), t);
        return t[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
    }

    /** Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. */
    static boolean connected(java.util.Set<Integer> cells) {
        if (cells.isEmpty()) return false;
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        int first = cells.iterator().next();
        q.add(first);
        seen.add(first);
        while (!q.isEmpty()) {
            int c = q.poll(), ci = c % DistrictPlan.CELLS_A_SIDE, cj = c / DistrictPlan.CELLS_A_SIDE;
            int[][] steps = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
            for (int[] st : steps) {
                int ni = ci + st[0], nj = cj + st[1];
                if (ni < 0 || nj < 0 || ni >= DistrictPlan.CELLS_A_SIDE || nj >= DistrictPlan.CELLS_A_SIDE) continue;
                int n = ni + nj * DistrictPlan.CELLS_A_SIDE;
                if (cells.contains(n) && seen.add(n)) q.add(n);
            }
        }
        return seen.size() == cells.size();
    }

    /** The shore's works by type, in words. */
    static String worksWords(CityShore shore, BuildingVisual.Type[] types) {
        Map<Integer, Integer> n = new java.util.TreeMap<>();
        for (CityShore.Work k : shore.works()) n.merge(k.type(), 1, Integer::sum);
        return n.toString();
    }

    /** A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. */
    static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes) {
        Ports p = new Ports();
        p.beginMonth();
        double[] by = new double[Ports.Cargo.values().length];
        by[Ports.Cargo.LIQUID.ordinal()] = liquid;
        by[Ports.Cargo.DRY_BULK.ordinal()] = dryBulk;
        by[Ports.Cargo.GENERAL.ordinal()] = general;
        by[Ports.Cargo.CONTAINER.ordinal()] = boxes;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            for (Good g : Good.values()) {
                if (g == Good.CRUDE || g.cargo() != c) continue;
                p.tally(g, 0, by[c.ordinal()], 1);
                break;
            }
        }
        return p;
    }

    /**
     * The game's own platform, its wells and its pipe on the map (WellCheck's
     * sea town): Game.mapAtSea() puts the jacket in the middle of its slotted
     * sites and its wells on them, the pipe from the jacket to the founding
     * site; and the tiles about them draw the jacket's plots, each well's,
     * the ring's and the pipe's each once.
     */
    static void oilAtSea() {
        if (WellCheck.quiet == null) WellCheck.quiet = QUIET;
        Game g = WellCheck.seaTown("mapcheck-sea");
        if (g == null) {
            check("fixture: WellCheck's sea town buys the ground over its sea field", false);
            return;
        }
        Deposit d = WellCheck.seaField();
        BuildingsTemplate jacket = WellCheck.template(g, "Offshore Platform"), well = WellCheck.template(g, "Platform Well"),
                pipe = WellCheck.template(g, "Crude Pipeline");
        int km = ham.citybuildersim.sectors.Oil.lengthKm(d, g.getCityLand().siteX(), g.getCityLand().siteY());
        WellCheck.quietly(() -> {
            g.buildStack(jacket, 1, true);
            g.simulateMonths(1);
            g.buildStack(well, d.sites(), true);
            g.buildStack(pipe, km, true);
            g.simulateMonths(1);
        });
        CityMap.AtSea a = g.mapAtSea();
        CityMap m = g.getCityMap();
        int slots = Math.min(12, d.sites());
        double cx = 0, cy = 0;
        java.util.List<Long> wellsAt = new java.util.ArrayList<>();
        for (int k = 0; k < slots; k++) {
            double[] at = d.siteAt(k);
            cx += d.x() + at[0] + 0.5;
            cy += d.y() + at[1] + 0.5;
            wellsAt.add(((long) Math.floor(d.x() + at[0]) << 32) | ((long) Math.floor(d.y() + at[1]) & 0xffffffffL));
        }
        cx /= slots;
        cy /= slots;
        CityMap.Jacket j = a.jackets().isEmpty() ? null : a.jackets().get(0);
        CityMap.Pipe pp = a.pipes().isEmpty() ? null : a.pipes().get(0);
        out.printf("      WellCheck's sea town: a jacket at (%.1f, %.1f) with %d wells, a pipe of %d km to (%.1f, %.1f)%n",
                j == null ? 0 : j.x(), j == null ? 0 : j.y(), j == null ? 0 : j.wells(), km, pp == null ? 0 : pp.x1(), pp == null ? 0 : pp.y1());
        check("THE OIL AT SEA: the game hands the map its platform - the jacket amid its slotted sites, its wells on them - and its pipe from the"
                        + " jacket to the founding site, whole", j != null && a.jackets().size() == 1 && Math.abs(j.x() - cx) < 1e-9
                && Math.abs(j.y() - cy) < 1e-9 && j.wellPlots().equals(wellsAt.subList(0, Math.min(j.wells(), slots))) && pp != null
                && pp.x0() == j.x() && pp.y0() == j.y() && Math.abs(pp.x1() - (g.getCityLand().siteX() + 0.5)) < 1e-9
                && Math.abs(pp.y1() - (g.getCityLand().siteY() + 0.5)) < 1e-9 && m.atSea().equals(a));
        // Every tile the jacket's ring and the pipe cross, painted: each drawn once.
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        double r = TilePainter.RING_PLOTS + 2;
        long x0 = (long) Math.floor(Math.min(j.x() - r, Math.min(pp.x0(), pp.x1()))), x1 = (long) Math.ceil(Math.max(j.x() + r, Math.max(pp.x0(), pp.x1())));
        long y0 = (long) Math.floor(Math.min(j.y() - r, Math.min(pp.y0(), pp.y1()))), y1 = (long) Math.ceil(Math.max(j.y() + r, Math.max(pp.y0(), pp.y1())));
        long jacketPlots = 0, wellPlots = 0, ringPlots = 0, pipePlots = 0;
        for (long ty = Math.floorDiv(y0, World.TILE); ty <= Math.floorDiv(y1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(x0, World.TILE); tx <= Math.floorDiv(x1, World.TILE); tx++) {
                long stamp = tiles.input(tx, ty, in);
                TilePainter.Painted pt = tiles.painted(tx, ty, in, stamp);
                jacketPlots += pt.jacketPlots;
                wellPlots += pt.wellPlots;
                ringPlots += pt.ringPlots;
                pipePlots += pt.pipePlots;
            }
        }
        long wantRing = 0, wantPipe = 0;
        for (long y = y0 - 1; y <= y1 + 1; y++) {
            for (long x = x0 - 1; x <= x1 + 1; x++) {
                if (TilePainter.ringCrosses(j.x(), j.y(), x, y)) wantRing++;
                if (TilePainter.segmentCrosses(pp.x0(), pp.y0(), pp.x1(), pp.y1(), x, y)) wantPipe++;
            }
        }
        long jx0 = Math.round(j.x()) - TilePainter.JACKET_PLOTS / 2, jy0 = Math.round(j.y()) - TilePainter.JACKET_PLOTS / 2;
        long wellsOff = j.wellPlots().stream().filter(wp -> { long x = wp >> 32, y = (int) (long) wp;
            return !(x >= jx0 && y >= jy0 && x < jx0 + TilePainter.JACKET_PLOTS && y < jy0 + TilePainter.JACKET_PLOTS); }).distinct().count();
        out.printf("      drawn: %d jacket plots, %d well plots (%d off the jacket), %,d plots the ring crosses (%,d), %,d the pipe (%,d)%n",
                jacketPlots, wellPlots, wellsOff, ringPlots, wantRing, pipePlots, wantPipe);
        check("...on the tiles the jacket drawn once, each well once on its site, the 500 m ring and the pipe through every plot they cross once",
                jacketPlots == (long) TilePainter.JACKET_PLOTS * TilePainter.JACKET_PLOTS && wellPlots == wellsOff && ringPlots == wantRing
                        && pipePlots == wantPipe && wantRing > 0 && wantPipe > 0);
    }
}
