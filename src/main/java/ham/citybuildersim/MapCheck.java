package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;

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
 *      track; and (0.8.3, batch FX2) in every one of those months no line to
 *      the world starts on a plot the city owns or on water, none runs out
 *      over ground that is not dry (at least TrainSchedule.WORLD_PLOTS, at
 *      most CityMap.WORLD_REACH, ending where the land does), and no train
 *      position of any route is on water off the track - the months that had
 *      a line, and trains, are printed;
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
 *   9. at sea and on the shore (0.7.97, batch O13; docs/studies/spec-oil.md
 *      2.12 and 5's O13 row, spec-roads-and-ports.md 2.8 and 4), on a square
 *      city on the default world's coast, a district west of its founding site:
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
 *      frame's boats in no more than BOAT_FRAME_MS. Since 0.8.2 (batch FR1;
 *      spec-freight 4.4) the trains, on the yard city: each yard's routes
 *      from the world line onto its sidings, on track all the way in; the
 *      railed tonnes over a train's payload, sent to the yards by kind; a
 *      train pure in (call, t), a frame finding every train on screen; at
 *      10B a frame's trains in no more than TRAIN_FRAME_MS; and the played
 *      city's station no container yard, its railway having moved no boxes.
 *  10. the map on Earth (0.7.119, batch E5; the project's spec-earth.md 3's
 *      "Far views" and 8's E5 row), on EarthCheck's fixture: a generated
 *      world's far node is its region at every stride, as before; an Earth
 *      world's far node over each of the study's windows strokes every river
 *      at least FAR_RIVER_HALF_PX a side - every pixel whose centre lies that
 *      near a segment is fresh (or sea) - draws every small lake over the
 *      pixels its blob always holds, keeps the data's sea and, dithered, its
 *      forest (the undithered region's printed beside it), is the same twice
 *      and from a world made afresh, and is drawn in no more than SCREEN_MS;
 *      across the 180th meridian (Fiji's window) nodes overlapping by half
 *      agree pixel for pixel and each side's land is the data's; past the
 *      planet's rim the node's pixels are the void and nowhere else; the
 *      hover card names the nearest of the data's places - the window's own
 *      city - by distance and direction, the province under the spot with
 *      its class (EarthTerrainCheck 11's), and past the rim says so; a
 *      generated city's card has no such lines. On the full file, when it is
 *      named: earth-1's longest river segment is EarthTerrain.LONGEST_SEGMENT_M;
 *      every stride's node over Rotterdam within SCREEN_MS, and the whole
 *      world's view timed; the small lakes' discs at a stride hold their own
 *      area within EarthTerrainCheck.LAKE_AREA; and on the meridian in
 *      Chukotka, rivers and lakes drawn on both sides and the nodes agreeing.
 *  11. the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md
 *      8.2): each built district's picture is its plan's, pixel for pixel -
 *      each pixel the plot its fixed hash ranks among its 64, read here from
 *      the plan as the painter reads it - its kinds' shares within
 *      PICTURE_SIGMAS binomial spreads of its plots', and its plan holding the
 *      district's counts; a plan drawn again to the same plots keeps its
 *      picture, the same object; the played city on PICTURE_MONTHS more
 *      months, every picture asked each month, redraws the pictures whose
 *      plots moved and only those, the rest the same objects; a far node with
 *      districts MapTiles.PICTURE_FROM_PX pixels across asks for the pictures
 *      it lacks and samples them pixel for pixel, smaller asks for none and
 *      dithers by the shares, within PICTURE_SIGMAS spreads; a far node in no
 *      more than SCREEN_MS; a view of more built districts than
 *      CityMap.PICTURES_KEPT asks for none; at the widest view the highways,
 *      the track and the city's edge on every pixel they hold, a city under a
 *      pixel drawn one; and the pictures not in the sidecar.
 *  12. the minimap (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3),
 *      MiniMapFrame: on a new city as founded, the played city, Jerus's city
 *      x 1 and x 10,000, and the played city's ground with a block bought
 *      FAR_DISTRICTS out - the stamp frames the ground owned in the districts
 *      with buildings, found again here from the grid's leaves, with the
 *      city's centre and MARGIN round it, and leaves out owned ground with
 *      nothing built on it (the far block, a month on, frames as before it);
 *      a city of one district frames its founding ground whole, smaller than
 *      its district, and every city at the least whole stride that holds the
 *      frame, at least half the stamp but for the margin; the stamp drawn
 *      with no plan drawn and no picture kept, asking for the pictures it
 *      would sample, then sampling them pixel for pixel; at x 10,000's stride
 *      dithered, asking none; the stamp - its frame and its node - in no more
 *      than MiniMapFrame.STAMP_MS of the thread's CPU, as section 5's month
 *      is timed (A28), on Earth too, the plane and not the void; and a click
 *      opens the expanded map on the frame, where the land office's opening
 *      takes in the far block.
 *  13. the globe on screen (0.7.135, batch E9; docs/studies/spec-globe.md
 *      3, 3.8 and 3.11), on EarthCheck's fixture and Jerus's city x 1 at
 *      Rotterdam: W, A, S and D held a second move the ground
 *      MapFrame.KEY_PX_A_SECOND pixels at every zoom on the plane - each
 *      way, two at once diagonally at that speed, opposite keys cancelling,
 *      a frame held up moving KEY_STEP_MOST_S's worth, the world's square
 *      stopping it - and turn the globe as much under its middle in every
 *      tier, stopping at the reach on the plane; the hover's words on the
 *      globe at the handoff are the plane's for the same point everywhere
 *      the plane draws, the city's own ground among them, and past it "Not
 *      the city's" with the place and the province by latitude and
 *      longitude, which are the plot's at its centre; the legend's entries
 *      by tier; the GROUND tier near the city is the plane's own far nodes,
 *      pixel for pixel through the camera, the composed matrix within a
 *      millionth of a plot of the camera's and the plane's projections and
 *      the last picture carried to its own pixels; the crossfade whole at
 *      the handoff and gone a notch in, the roll eased past the reach; and
 *      the founding screen's globe the model's frame - its pixels the
 *      planet's picture's, then the far rule's through the camera - handing
 *      off at half a pixel.
 *  14. buying from the map (0.7.137, batch M6; docs/studies/spec-earth.md
 *      3 and its decisions B1 to B7), on the played city: the expanded
 *      map's card for one offer has the figures the land office's row reads
 *      for it - its price listed and at today's rate, its km2 by kind, its
 *      dry ground, its price a dry km2, its sites and tonnes - with the cash
 *      before and after in the money the toggle pays in, converting and
 *      from the vault; the fields it rings an offer's own, their sites and
 *      tonnes its row's and each site's square the hover's for the field -
 *      the city first buying toward an iron field until an offer holds one;
 *      for several, their sums, the office's "Buy the best N" totals, in the
 *      order picked, an offer no longer listed or picked twice left out; what the card says the cash will be is what it is
 *      once they are bought, converting and from the vault; short, the
 *      card's bond is the funding page's first offer for the same gap -
 *      Game.BUILD_BOND_YEARS' term for the cash, its face on
 *      BUILD_BOND_GRANULE - and issued, it buys them all, and from the vault
 *      the dollar bond, or none with the window shut; a box on the screen
 *      takes exactly the offers whose free ground it meets, every plot under
 *      it tried, the city's own ground inside an offer's rectangle taking
 *      nothing, and through the globe's camera every offer a point of its
 *      free ground puts inside the box; and the picks drop on a month and
 *      on a load into the same Game, an offer bought falling out of them.
 *  15. the materials drawn as themselves (0.8.0, batch I14;
 *      docs/studies/spec-materials.md 5's I14 row): every building of the
 *      nine industries has a look of its own, never a plain mine's or
 *      industry's box, and one on a resource's sites stands on its
 *      template's; on Jerus's city x 1 with one or two of every materials
 *      kind, on a square of MATERIALS_SIDE districts holding sites enough of
 *      every type of field, the districts sum to the counts, every building
 *      is drawn once - a works on its sites once for all of them - a works of
 *      twenty sites on twenty, each site under a works of its type of field;
 *      one more Iron Mine takes one more hematite site and moves no works;
 *      each industry's works stand in one district and its plan places them
 *      in touching cells; each look is drawn in its own colours; with no
 *      zone no district is tinted, and each zone's districts cover its ground
 *      and one fewer would not, none twice, its first holding its sector's
 *      buildings; and a screen over the industry paints and rasters within
 *      SCREEN_MS of the thread's CPU, every tile within it.
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
        out.printf("%-78s %s%n", label, Timing.verdict(ok));
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

    /** The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). */
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
        trains(sq, city);
        onEarth(sq.types);
        pictures(city, sq);
        miniMap(city, sq);
        globeOnScreen(sq);
        buyingFromTheMap(city);
        materials(sq.seed);
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
        int lineMonths = 0, routeMonths = 0, wayMonthsBad = 0;
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
                    // ...handed the mine's price as it is handed the field's: a D$45M mine since 0.7.120 (batch I3) is more than a young city carries.
                    BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
                    g.setCashForTest(g.getCash() + g.getBusinessInvestment().getCostOf(mine, 1));
                    LongPlaytest.build(g, mine.getName(), 1);
                }
                LongPlaytest.run(g, 1);
                CityMap map = g.getCityMap();
                int[] ways = new int[2];
                if (!worldWaysOk(map, ways)) wayMonthsBad++;
                lineMonths += ways[0];
                routeMonths += ways[1];
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
        out.printf("      the lines to the world and the trains (FX2): the map had a world line in %d of %d months and train routes in %d;"
                + " %d months had a line or a train where it should not be%n", lineMonths, MONTHS, routeMonths, wayMonthsBad);
        check("no world line point and no train position on water, over every month played", wayMonthsBad == 0);
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

    /**
     * Whether a month's lines to the world and trains lie on the ground they
     * should (FX2; GatewayCheck holds the whole list on its fixtures, this is
     * the cheap half, the points and the routes and never the box): each
     * world line's point is on a plot the city does not own and that is dry;
     * the way out along line.way() is dry ground at every CityMap.WORLD_STEP
     * short of its reach, which is TrainSchedule.WORLD_PLOTS at least and
     * CityMap.WORLD_REACH at most, and where it is less than that the next
     * point is not dry (the line ends where the land does); and every quarter
     * plot of every train route is dry ground or a plot the railway carries
     * (CityRuns.at(): track, track over a highway, a yard). Sets seen[0] to 1
     * when the map has a world line and seen[1] to 1 when it has train routes.
     */
    static boolean worldWaysOk(CityMap m, int[] seen) {
        boolean ok = true;
        World w = World.of(m.worldKey());
        List<CityMap.WorldLine> lines = m.worldLines();
        if (!lines.isEmpty()) seen[0] = 1;
        for (CityMap.WorldLine l : lines) {
            long px = (long) Math.floor(l.x()), py = (long) Math.floor(l.y());
            ok &= m.runGround().at(px, py, -1) == -1 && World.dry(w.terrainAt(px, py));
            ok &= l.reach() >= TrainSchedule.WORLD_PLOTS && l.reach() <= CityMap.WORLD_REACH;
            double c = Math.cos(Math.toRadians(l.way())), s = Math.sin(Math.toRadians(l.way()));
            for (int i = 0; i * CityMap.WORLD_STEP < l.reach(); i++) {
                double t = i * CityMap.WORLD_STEP;
                ok &= World.dry(w.terrainAt((long) Math.floor(l.x() + c * t), (long) Math.floor(l.y() + s * t)));
            }
            if (l.reach() < CityMap.WORLD_REACH) {
                ok &= !World.dry(w.terrainAt((long) Math.floor(l.x() + c * l.reach()), (long) Math.floor(l.y() + s * l.reach())));
            }
        }
        List<TrainSchedule.Route> routes = m.trainRoutes();
        if (!routes.isEmpty()) seen[1] = 1;
        CityRuns runs = m.runs();
        java.util.Set<Long> plots = new java.util.HashSet<>();
        for (TrainSchedule.Route rt : routes) {
            double[] xs = rt.xs(), ys = rt.ys();
            for (int i = 0; i + 1 < xs.length; i++) {
                int n = Math.max(1, (int) Math.ceil(Math.hypot(xs[i + 1] - xs[i], ys[i + 1] - ys[i]) * 4));
                for (int k = 0; k <= n; k++) {
                    long qx = (long) Math.floor(xs[i] + (xs[i + 1] - xs[i]) * k / n), qy = (long) Math.floor(ys[i] + (ys[i + 1] - ys[i]) * k / n);
                    if (!plots.add((qx << 32) ^ (qy & 0xffffffffL))) continue;
                    byte at = runs.at(qx, qy);
                    ok &= at == CityRuns.F_RAIL || at == CityRuns.F_RAIL_OVER || at == CityRuns.F_YARD || World.dry(w.terrainAt(qx, qy));
                }
            }
        }
        return ok;
    }

    /* ---------------------------------------------------------------------
       WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64)
       --------------------------------------------------------------------- */

    /** What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). */
    static final class Drawn {
        /** Buildings by type id: a mine or well on a site once, however many tiles its site spans - since 0.8.0 a works once for all the sites it takes; packed ones among them. */
        long[] buildings;
        /** The sites under each type's works, by type id (0.8.0), each once; and the types whose sites are not a whole number of its works. */
        long[] siteCount;
        long partWorks;
        /** The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). */
        final double[] surface = new double[4], leftover = new double[4];
        /** The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay; buildings packed at the city's edge (R7); tiles painted; districts. */
        long rail, trackSurplus, packed, tiles, districts;

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
            return same && rail + trackSurplus == track;
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
        d.siteCount = new long[m.types().length];
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
                        if (sites.add(in.siteKey[p.bsite[b]])) d.siteCount[p.btype[b]]++;
                    } else d.buildings[p.btype[b]]++;
                }
            }
        }
        // ...a works on its sites once, for as many as it takes (0.8.0).
        for (int t = 0; t < d.siteCount.length; t++) {
            if (d.siteCount[t] == 0) continue;
            int each = m.types()[t] == null ? 1 : Math.max(1, m.types()[t].sites());
            d.buildings[t] += d.siteCount[t] / each;
            if (d.siteCount[t] % each != 0) d.partWorks++;
        }
        double[] left = m.leftoverByKind();
        System.arraycopy(left, 0, d.leftover, 0, 4);
        d.trackSurplus = m.runs().trackSurplus();
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

    /** Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. */
    static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share) {
        long each = BuildingVisual.cells(type);
        if (type.road() != BuildingVisual.NOT_A_ROAD) return d.freeRoad() >= each;
        long free = d.freeCells();
        double other = d.usedCells - d.usedRoad;
        double k = Math.min(free / (double) each, (free + d.usedRoad - share * other) / (each * (1 + share)));
        return Math.max(0, (long) Math.floor(k + 1e-9)) >= 1;
    }

    /** The sites a mine type may stand on (0.8.0, batch I14): its resource's of its type of field - the game's own rule since I5 (Game.committedOn(r, type), LandManager.fits()), an Iron Mine on hematite. */
    static long mineSites(CityMap m, int mine) {
        BuildingVisual.Type t = m.types()[mine];
        return m.ownedSites(t.site(), t.field());
    }

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
                // ...at the road share the change's month strikes (CityMap.reconcile(): the share of the counts with it), not
                // the canonical map's, which strikes none (0.7.106: the founding lake's 77 plots of bank in a district of
                // their own on this world have room for a Mixed Farm at a share of 0 and none at the month's .307).
                long[] after = counts.clone();
                after[t.getId()] += sign;
                double share = m.roadShareOf(after);
                for (int i = 0; i < ds.size(); i++) {
                    // Added: the first district in its direction with room for its whole plots, keeping room for the road its
                    // buildings need (0.7.72, CityMap.hasRoomFor()). Taken away: the last holding one.
                    CityMap.District d = ds.get(outer == (sign > 0) ? ds.size() - 1 - i : i);
                    boolean fits = sign > 0 ? roomAt(d, types[t.getId()], share) : d.count(t.getId()) > 0;
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
            for (BuildingVisual.Type t : types) if (t != null && t.id() == Mining.IRON_MINE) mine = t.id();
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
                // No more mines than the square's sites of their type (since 0.8.0 an Iron Mine's hematite, mineSites()): the game's own rule (Game.hasDepositFor()).
                if (mine >= 0) {
                    c[mine] = Math.min(wantMines, mineSites(m, mine));
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
                c[mine] = Math.min(wantMines, mineSites(ironX1, mine));
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
        long rail, crossings, packed, siding;
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
                    siding += p.sidingLaid;
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
        interchangesAndAccess(sq);
        sidingsDealt(sq);
    }

    /* =====================================================================
       8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4;
       spec-freight 4.5, 4.6 and 7's MapCheck 8 items: "access roads reach a
       ramp or arterial, interchange reserves clear of buildings")
       ===================================================================== */

    /** The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. */
    static final int ACCESS_HIGHWAYS_ADDED = 200, ACCESS_DEPOTS_ADDED = 3;

    /** The type id of the Truck Depot, the one building of the category HAULAGE. */
    static int depotType(BuildingVisual.Type[] types) {
        int id = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.category() == BuildingType.HAULAGE) id = t.id();
        return id;
    }

    /**
     * The yard city (his city x 1 where its square holds iron, with two Rail Terminals and a freight line) with highways added to cross and
     * Truck Depots to place; with `highways` false his own highways taken out too: none, so no ramp for a road to end on.
     */
    static CityMap accessCity(Squares sq, boolean highways) {
        return accessCity(sq, highways, null);
    }

    /** ...with `extra` counts added (batch FR7 slice 5: spurs and bulk works for the sidings' deal): a track type's to the railway, any other's to the buildings. */
    static CityMap accessCity(Squares sq, boolean highways, long[] extra) {
        long[] c = sq.ironCounts.clone();
        int yard = -1, freight = -1, mine = -1, hw = -1;
        for (BuildingVisual.Type t : sq.types) {
            if (t == null) continue;
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
            if (t.id() == Mining.IRON_MINE) mine = t.id();
            if (t.road() == BuildingVisual.HIGHWAY) hw = t.id();
        }
        long[] rail = new long[c.length];
        for (int t = 0; t < c.length; t++) if (sq.types[t] != null && (sq.types[t].track() || sq.types[t].terminal())) { rail[t] = c[t]; c[t] = 0; }
        rail[yard] += YARDS_ADDED;
        rail[freight] += FREIGHT_ADDED;
        if (extra != null) {
            for (int t = 0; t < c.length && t < extra.length; t++) {
                if (sq.types[t] != null && (sq.types[t].track() || sq.types[t].terminal())) rail[t] += extra[t];
                else c[t] += extra[t];
            }
        }
        c[hw] = highways ? c[hw] + ACCESS_HIGHWAYS_ADDED : 0;
        c[depotType(sq.types)] += ACCESS_DEPOTS_ADDED;
        long wantMines = c[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(sq.seed, sq.ironX, sq.ironY, sq.types, side, capacity, c);
        c[mine] = Math.min(wantMines, mineSites(m, mine));
        m.reconcile(c);
        for (int t = 0; t < c.length; t++) c[t] += rail[t];
        m.reconcile(c);
        return m;
    }

    /**
     * What a map's plans gave its works' access roads: {roads, on a ramp, on an arterial, already on one, gates not beside their box, ends neither a
     * ramp's street nor an arterial, the map's works (yards and sea terminals) given a road, the map's works}.
     */
    static int[] accessFigures(CityMap m) {
        int[] f = new int[8];
        for (CityMap.District d : m.districts()) {
            DistrictPlan p = m.plan(d);
            List<int[]> works = m.planInputHere(d).works;
            f[7] += works.size();
            for (int[] r : p.accessRoads) {
                f[0]++;
                for (int[] wk : works) if (wk[0] == r[0] && wk[1] == r[1] && wk[2] == r[2] && wk[3] == r[3]) f[6]++;
                if (r[8] == DistrictPlan.ACCESS_HAD) { f[3]++; continue; }
                if (r[8] == DistrictPlan.ACCESS_RAMP) f[1]++; else f[2]++;
                if (!JunctionCheck.beside(new int[] { r[0], r[1], r[2], r[3] }, r[4], r[5])) f[4]++;
                int end = r[7] * DistrictPlan.FRAME + r[6];
                boolean onRamp = r[8] == DistrictPlan.ACCESS_RAMP && p.kindAt(end) == DistrictPlan.UNDER;
                boolean onArterial = r[8] == DistrictPlan.ACCESS_ARTERIAL && (r[9] == DistrictPlan.ROLE_ARTERIAL || r[9] == DistrictPlan.ROLE_BOULEVARD || r[9] == DistrictPlan.ROLE_ACCESS);
                if (!onRamp && !onArterial) f[5]++;
            }
        }
        return f;
    }

    static void interchangesAndAccess(Squares sq) {
        out.println("      the interchanges' ground and the access roads (batch FR7): his city x 1 where its square holds iron, " + ACCESS_HIGHWAYS_ADDED
                + " highways, " + YARDS_ADDED + " Rail Terminals and " + ACCESS_DEPOTS_ADDED + " Truck Depots added:");
        CityMap m = accessCity(sq, true);
        CityRuns runs = m.runs();
        Raster r = Raster.of(m);
        List<InterchangeGround.Site> sites = runs.reserves(), laid = runs.reservesLaid();
        out.printf("      %d interchange sites, %d with their reserve (%d cloverleaf, %d trumpet), the rest stacks%n", sites.size(), laid.size(),
                laid.stream().filter(s -> s.kind() == InterchangeGround.CLOVERLEAF).count(), laid.stream().filter(s -> s.kind() == InterchangeGround.TRUMPET).count());
        check("fixture: its highways cross, and some of its interchanges have their reserve", runs.highways().interchanges() > 0 && !sites.isEmpty() && !laid.isEmpty());
        boolean sizes = true, clear = true, owned = true;
        for (InterchangeGround.Site s : laid) {
            int side = s.kind() == InterchangeGround.CLOVERLEAF ? InterchangeGround.CLOVERLEAF_SIDE : InterchangeGround.TRUMPET_SIDE;
            sizes &= s.w() == side && s.h() == side && (s.kind() != InterchangeGround.CLOVERLEAF || (s.x0() + side / 2 == s.x() && s.y0() + side / 2 == s.y()));
            for (long y = s.y0(); y < s.y0() + s.h(); y++) {
                for (long x = s.x0(); x < s.x0() + s.w(); x++) {
                    if (CityRuns.covered(runs.highways(), x, y)) continue;
                    long gx = x - r.px0, gy = y - r.py0;
                    if (gx < 0 || gy < 0 || gx >= r.w || gy >= r.h) { clear = false; continue; }
                    int g = (int) (gy * r.w + gx);
                    clear &= r.use[g] != TilePainter.BUILDING && r.use[g] != TilePainter.ROAD && r.use[g] != TilePainter.RAIL;
                    owned &= r.owned[g] && !r.wet[g];
                }
            }
        }
        check("every reserve laid is CLOVERLEAF_SIDE plots square, centred on its crossing, or TRUMPET_SIDE plots square beside its T", sizes);
        check("...holds no building, street or track on any plot but the highways' own, as the map draws it", clear);
        check("...and is owned dry ground throughout", owned);
        boolean justified = true;
        List<long[]> mined = m.minedSites();
        for (InterchangeGround.Site s : sites) {
            if (s.got()) continue;
            boolean shortOf = false;
            for (long y = s.y0(); y < s.y0() + s.h() && !shortOf; y++) {
                for (long x = s.x0(); x < s.x0() + s.w() && !shortOf; x++) {
                    if (CityRuns.covered(runs.highways(), x, y)) continue;
                    long gx = x - r.px0, gy = y - r.py0;
                    boolean off = gx < 0 || gy < 0 || gx >= r.w || gy >= r.h;
                    int g = off ? 0 : (int) (gy * r.w + gx);
                    shortOf |= off || !r.owned[g] || r.wet[g] || CityRuns.covered(runs.rail(), x, y) || Math.floorMod(x, CityRuns.CELL) == 0 || Math.floorMod(y, CityRuns.CELL) == 0;
                }
            }
            for (long[] yd : runs.yards()) shortOf |= yd[0] < s.x0() + s.w() && yd[1] < s.y0() + s.h() && yd[0] + yd[2] > s.x0() && yd[1] + yd[3] > s.y0();
            for (long[] ms : mined) shortOf |= ms[0] < s.x0() + s.w() && ms[1] < s.y0() + s.h() && ms[2] >= s.x0() && ms[3] >= s.y0();
            justified &= shortOf;
        }
        check("...and a crossing that got none is short of ground: unowned or wet, track, a cell's line, a yard or a mine in its box (a stack)", justified);

        int depot = depotType(sq.types);
        int works = runs.yards().size() + (int) m.totals()[depot];
        for (CityShore.Work w : m.shore().works()) if (w.berthed()) works++;
        int[] a = accessFigures(m);
        long drawn = 0;
        for (int g = 0; g < r.w * r.h; g++) if (r.role[g] == TilePainter.ACCESS && r.use[g] == TilePainter.ROAD) drawn++;
        out.printf("      %d works (%d yards, %d depots), %d access roads: %d to a ramp, %d to an arterial, %d beside another's; %,d plots of it drawn%n",
                works, runs.yards().size(), m.totals()[depot], a[0], a[1], a[2], a[3], drawn);
        check("every yard and every Truck Depot has an access road, or stands on another's", works > 0 && a[0] == works);
        check("...each starts beside its work's box and ends on a highway ramp's street or on an arterial", a[4] == 0 && a[5] == 0);
        check("...with highways in the frame, some end on a ramp", a[1] > 0);
        check("...and the painter draws the access role on road plots", drawn > 0);

        CityMap bare = accessCity(sq, false);
        int[] b = accessFigures(bare);
        int bareWorks = bare.runs().yards().size() + (int) bare.totals()[depot];
        check("the same city with no highway: every road ends on an arterial of the grid, none on a ramp",
                !bare.runs().highways().started && b[0] == bareWorks && b[1] == 0 && b[4] == 0 && b[5] == 0 && b[2] + b[3] == b[0]);

        // A sea terminal is a work too: the coast's, with highways and depots, each given its road.
        World w = World.of(sq.seed);
        long[] counts = shoreCounts(sq.types);
        int hw = -1;
        for (BuildingVisual.Type t : sq.types) if (t != null && t.road() == BuildingVisual.HIGHWAY) hw = t.id();
        counts[hw] = 150;
        counts[depot] = ACCESS_DEPOTS_ADDED;
        CityMap coast = coastal(sq.seed, w.foundingX() - CityMap.DISTRICT, w.foundingY(), sq.types, SHORE_SIDE, counts);
        int terminals = 0;
        for (CityShore.Work k : coast.shore().works()) if (k.berthed()) terminals++;
        int[] c = accessFigures(coast);
        out.printf("      the coast: %d sea terminals (%d given a road) and %d depots, %d access roads (%d to a ramp, %d to an arterial)%n", terminals, c[6],
                ACCESS_DEPOTS_ADDED, c[0], c[1], c[2]);
        check("the sea terminals on the shore are works that get an access road where their district has a grid to go to: some do, each beside its box and ending on a ramp's street or an arterial",
                terminals > 0 && c[7] == terminals && c[6] > 0 && c[4] == 0 && c[5] == 0);
    }

    /* =====================================================================
       8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec-
       freight 4.3, D14: "one siding for each spur, into its works' estate
       cell")
       ===================================================================== */

    /** The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). */
    static final int SPURS_ADDED = 12, BULK_WORKS_ADDED = 90;

    /** The spurs the runs' sidings left, as the map deals them: each dealt district is one the main line crosses with bulk works of its own, no more than it holds, and the plans lay at most their share, drawn on the raster; the legend's surplus falls by a dealt spur's plots; the deal is the same from the same counts. */
    static void sidingsDealt(Squares sq) {
        out.println("      the sidings the plans lay (batch FR7 slice 5, D14): the iron city with " + SPURS_ADDED + " Rail Spurs and " + BULK_WORKS_ADDED + " bulk works added:");
        BuildingVisual.Type[] types = sq.types;
        int spur = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.siding()) spur = t.id();
        long[] extra = new long[types.length];
        extra[spur] = SPURS_ADDED;
        extra[JunctionCheck.BIG] = BULK_WORKS_ADDED * 2 / 3;
        extra[JunctionCheck.SMALL] = BULK_WORKS_ADDED / 3;
        CityMap m = accessCity(sq, true, extra);
        CityRuns runs = m.runs();
        int f = DistrictPlan.FRAME, left = m.spursLeft();
        long dealt = m.sidingsDealt();
        java.util.Set<Long> line = runs.mainLineCells(CityMap.DISTRICT);
        long sum = 0, planned = 0, planPlots = 0, noRail = 0;
        boolean crossed = true, rail = true, holds = true, within = true, undealt = true;
        List<long[]> plots = new ArrayList<>();
        for (CityMap.District d : m.districts()) {
            int share = m.sidingsOf(d);
            // The plan the painter draws (with what the districts before it carried to it), not a standalone one.
            CityMap.Drawn dr = m.drawnOf(d);
            DistrictPlan.Input in = m.planInputHere(d);
            if (share == 0) {
                undealt &= dr.siding == null;
                continue;
            }
            DistrictPlan p = m.plan(d);
            sum += share;
            crossed &= line.contains((Math.floorDiv(in.x0, CityMap.DISTRICT) << 32) ^ (Math.floorDiv(in.y0, CityMap.DISTRICT) & 0xffffffffL));
            boolean any = false;
            for (int q = 0; q < f * f && !any; q++) any = in.fixed[q] == DistrictPlan.FIXED_RAIL && q % f >= 1 && q / f >= 1 && q % f < DistrictPlan.SIDE && q / f < DistrictPlan.SIDE;
            rail &= any;
            long bulk = 0;
            for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].bulk() && types[t].drawn() && !types[t].terminal() && t < in.counts.length) bulk += in.counts[t];
            holds &= share <= bulk;
            int sidings = 0;
            if (dr.siding != null) {
                for (int e : dr.siding) {
                    int q = e & (CityMap.SIDING_END_BIT - 1);
                    if ((e & CityMap.SIDING_END_BIT) != 0) sidings++;
                    planPlots++;
                    plots.add(new long[] { in.x0 + q % f, in.y0 + q / f });
                }
            }
            within &= sidings <= share;
            planned += sidings;
            noRail += p.sidingsNoRail;
        }
        out.printf("      %d Rail Spurs, %d laid by the runs into mined sites, %d left; %d dealt to %d district(s) the main line crosses; the plans lay %d sidings of %,d plots%n",
                m.runs().sidingsLaid() + left, m.runs().sidingsLaid(), left, dealt, m.districts().stream().filter(d -> m.sidingsOf(d) > 0).count(), planned, planPlots);
        check("fixture: spurs are left to deal, and the main line crosses districts holding bulk works to deal them to", left > 0 && dealt > 0);
        check("the spurs dealt never exceed the spurs the runs' sidings left, and the districts' shares make them", sum == dealt && dealt <= left);
        check("every district dealt a share is one the main line crosses (so within a district of it), with a plot of the runs' railway in its frame", crossed && rail);
        check("...holds at least that many bulk works of its own for its plan to place, and its plan lays no more sidings than its share", holds && within);
        check("...and a district with no share lays none, nor any track", undealt);
        check("the plans lay some of them, and none is for want of a railway in the frame", planned > 0 && noRail == 0);
        Raster r = Raster.of(m);
        boolean drawn = true;
        for (long[] pl : plots) {
            long gx = pl[0] - r.px0, gy = pl[1] - r.py0;
            drawn &= gx >= 0 && gy >= 0 && gx < r.w && gy < r.h && r.use[(int) (gy * r.w + gx)] == TilePainter.RAIL;
        }
        out.printf("      %,d plots of siding painted on the raster of %,d the plans laid; the runs' track %,d plots%n", r.siding, planPlots, r.rail);
        check("every plot of every siding the plans laid is painted as track, each once, counted apart from the runs' track", drawn && r.siding == planPlots);
        long per = BuildingVisual.cells(types[spur]), want = Math.max(0, runs.trackSurplus() - dealt * per);
        long[] legend = m.legendFiguresIfDrawn();
        out.printf("      the legend's \"plots of track more than the line uses\": %,d (the runs' %,d less %d spurs of %d plots)%n", legend == null ? -1 : legend[2], runs.trackSurplus(), dealt, per);
        check("the legend's surplus of track falls by a dealt spur's plots: the runs' less the dealt spurs' track, to no less than none", legend != null && legend[2] == want && m.dealtSpurTrack() == dealt * per);
        check("...and a city whose spurs were all taken by the runs' sidings or no district can take loses none of it", runs.trackSurplus() == 0 || dealt == 0 || legend[2] < runs.trackSurplus());
        CityMap again = accessCity(sq, true, extra);
        boolean same = again.sidingsDealt() == dealt && again.districts().size() == m.districts().size();
        for (int i = 0; i < m.districts().size() && same; i++) same &= again.sidingsOf(again.districts().get(i)) == m.sidingsOf(m.districts().get(i));
        check("the deal is a pure function of the counts and the runs: the same city built again deals the same share to each district", same);
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
            if (t.id() == Mining.IRON_MINE) mine = t.id();
        }
        if (kind == BuildingVisual.GRAVEL) { c[gravel] += c[paved]; c[paved] = 0; } else { c[paved] += c[gravel]; c[gravel] = 0; }
        c[highway] = 0;
        long wantMines = JERUS_COUNTS[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(sq.seed, (long) Math.floor(river.xs()[at]), (long) Math.floor(river.ys()[at]), sq.types, side,
                capacity, c);
        c[mine] = Math.min(wantMines, mineSites(m, mine));
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
        long shortBy = net.map.runs().trackSurplus();
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
            if (t.id() == Mining.IRON_MINE) mine = t.id();
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
        c[mine] = Math.min(wantMines, mineSites(m, mine));
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
            long raw = (long) m.districts().size() * (2 + 2 + 4 + 8 + 4 * CityMap.SLOTS + 4L * m.types().length);
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
     * wall clock, and the output and the label say so. So too where its clock
     * steps coarser than CPU_STEP_MS (2026-10-13): Windows counts a thread's
     * CPU only at its 15.6 ms clock tick, so on Jerus's PC every month and
     * every stamp read 0.00 - the month's bound passed without measuring
     * anything and MapCheck 12's stamp, 0.4 to 2.7 ms on the wall clock,
     * failed its "more than nothing". A28 holds wherever the CPU can be read.
     */
    /** The JVM's threads, read for the main thread's CPU time. */
    static final java.lang.management.ThreadMXBean THREADS = java.lang.management.ManagementFactory.getThreadMXBean();

    /** The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. */
    static final double CPU_STEP_MS = 0.1;

    /** Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. */
    static final boolean CPU_TIMED = cpuTimed();

    static boolean cpuTimed() {
        if (!THREADS.isCurrentThreadCpuTimeSupported()) return false;
        try {
            if (!THREADS.isThreadCpuTimeEnabled()) THREADS.setThreadCpuTimeEnabled(true);
        } catch (UnsupportedOperationException | SecurityException e) {
            return false;
        }
        return THREADS.isThreadCpuTimeEnabled() && cpuStepMs() <= CPU_STEP_MS;
    }

    /** The least step the main thread's CPU clock takes, in ms: five times, spinning until it moves (200 ms of the wall clock at most each); infinite if it never does. */
    static double cpuStepMs() {
        double least = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 5; i++) {
            long w0 = System.nanoTime(), c0 = THREADS.getCurrentThreadCpuTime(), c1 = c0;
            while (c1 == c0 && System.nanoTime() - w0 < 200_000_000L) c1 = THREADS.getCurrentThreadCpuTime();
            if (c1 == c0) return Double.POSITIVE_INFINITY;
            least = Math.min(least, (c1 - c0) / 1e6);
        }
        return least;
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
            under &= Timing.within(best[i], SCREEN_MS);
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
            for (BuildingVisual.Type t : m.types()) if (t != null && t.id() == Mining.IRON_MINE) mine = t.id();
            long sites = mine >= 0 ? mineSites(m, mine) : 0;
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
            String clock = CPU_TIMED ? "the main thread's CPU" : "the wall clock (this JVM cannot read a thread's CPU finely enough)";
            out.printf("      x %,.0f, in ms of %s / on the wall clock: +0.05%% in %.2f/%.2f, then a year of +0.01%%:%s,"
                    + " then -0.05%% in %.2f/%.2f; the bounded months' worst %.2f (its wall clock %.2f), the wall clock's worst %.2f%n",
                    TIMES[i], clock, firstMs, firstWall, months, downMs, downWall, worst, worstsWall, worstWall);
            check("at " + size + " billion a month's change takes no more than 5 ms of "
                    + (CPU_TIMED ? "the main thread's CPU" : "the wall clock"), Timing.within(worst, RECONCILE_MS));
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
        check("the view's own path for a 1,389 x 868 L0 screen takes no more than 80 ms, x 1 and x 10,000", Timing.within(view[0], SCREEN_MS) && Timing.within(view[1], SCREEN_MS));
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
                Timing.within(best[0], BOAT_FRAME_MS) && Timing.within(best[1], BOAT_FRAME_MS) && Timing.within(best[2], BOAT_FRAME_MS));
    }

    /** The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). */
    static final double TRAIN_FRAME_MS = BOAT_FRAME_MS;

    /** Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. */
    static final int TRAIN_YARDS_A_SIDE = 40;

    /**
     * Section 9's trains (0.8.2, batch FR1; spec-freight 4.4, D16): on the
     * yard city (yardCity()), the routes run from the world line along the
     * track into each yard and onto its sidings; the trains are its railed
     * tonnes over a train's payload, pure in (call, t), sent to the yards by
     * kind; a frame finds every train on screen and only those; and at 10
     * billion a frame's trains take no more than TRAIN_FRAME_MS. The played
     * city's railway moved no boxes, so its freight station is no container
     * yard (RailCheck 7 holds the other way, on a city whose railway did).
     */
    static void trains(Squares sq, Game city) {
        out.println("      the trains (0.8.2, batch FR1):");
        CityMap m = yardCity(sq);
        CityRuns runs = m.runs();
        CityMap.WorldLine line = null;
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) line = l;
        long[] station = runs.freightStation();
        check("fixture: the yard city has a gateway, its railway's world line, yards and a freight station",
                m.gateway() >= 0 && line != null && runs.yards().size() >= YARDS_ADDED && station != null);
        if (line == null || station == null) return;

        // ---- THE ROUTES: from the world line onto each yard's sidings, on track all the way in.
        long t0 = System.nanoTime();
        List<TrainSchedule.Route> routes = m.trainRoutes();
        double routesMs = (System.nanoTime() - t0) / 1e6;
        // FR7 slice 3: a Rail Depot's yard has TileRaster.DEPOT_TRACKS sidings, a Terminal's YARD_TRACKS (the yard city's yards are
        // depots: the last terminal type), and each of the works' sidings has one more route, a bulk unit train's (its own rule: MainLineCheck 13).
        int reached = 0, expectedRoutes = 0, worksRoutes = 0;
        for (int i = 0; i < runs.yards().size(); i++) {
            if (runs.wayTo(runs.yards().get(i)) == null) continue;
            reached++;
            expectedRoutes += TileRaster.tracksOf(m.types()[m.yardTypeOf(i)]);
        }
        boolean starts = true, onTrack = true, intoYard = true, sidings = true, stationOnes = true;
        double r = Math.toRadians(line.way());
        java.util.Map<Integer, java.util.Set<Integer>> sidingsOf = new java.util.HashMap<>();
        for (TrainSchedule.Route rt : routes) {
            if (rt.works()) { worksRoutes++; continue; }
            double[] xs = rt.xs(), ys = rt.ys();
            long[] yard = runs.yards().get(rt.yard());
            starts &= Math.abs(xs[0] - (line.x() + Math.cos(r) * TrainSchedule.WORLD_PLOTS)) < 1e-9
                    && Math.abs(ys[0] - (line.y() + Math.sin(r) * TrainSchedule.WORLD_PLOTS)) < 1e-9
                    && xs[1] == line.x() && ys[1] == line.y() && Math.abs(rt.fadeTo() - TrainSchedule.WORLD_PLOTS) < 1e-9;
            // From the gate arm's end to where it enters its yard's box, every quarter step on a plot the railway carries.
            int last = rt.siding() < 0 ? xs.length - 1 : xs.length - 3;
            for (int i = 2; i < last; i++) {
                double len = Math.hypot(xs[i + 1] - xs[i], ys[i + 1] - ys[i]);
                int n = (int) Math.ceil(len * 4);
                for (int k = 0; k <= n; k++) {
                    double x = xs[i] + (xs[i + 1] - xs[i]) * k / n, y = ys[i] + (ys[i + 1] - ys[i]) * k / n;
                    byte at = runs.at((long) Math.floor(x), (long) Math.floor(y));
                    onTrack &= at == CityRuns.F_RAIL || at == CityRuns.F_RAIL_OVER || at == CityRuns.F_YARD;
                }
            }
            // ...and its ladder and siding inside the box.
            for (int i = last; i < xs.length; i++) {
                intoYard &= xs[i] >= yard[0] && xs[i] <= yard[0] + yard[2] && ys[i] >= yard[1] && ys[i] <= yard[1] + yard[3];
            }
            sidingsOf.computeIfAbsent(rt.yard(), k -> new java.util.HashSet<>()).add(rt.siding());
            stationOnes &= rt.station() == java.util.Arrays.equals(yard, station);
        }
        for (java.util.Map.Entry<Integer, java.util.Set<Integer>> e : sidingsOf.entrySet()) {
            sidings &= e.getValue().size() == TileRaster.tracksOf(m.types()[m.yardTypeOf(e.getKey())]);
        }
        out.printf("      %d of %d yards reached, %d routes found in %.1f ms (%d to a works' siding), the longest %.0f plots%n", reached, runs.yards().size(),
                routes.size(), routesMs, worksRoutes, routes.stream().mapToDouble(TrainSchedule.Route::length).max().orElse(0));
        check("THE TRAINS' ROUTES: one a yard the railway's way reaches and a siding of its own (a depot's, a terminal's), every yard reached",
                reached == runs.yards().size() && sidingsOf.size() == reached && routes.size() == expectedRoutes + worksRoutes && sidings);
        check("...each from WORLD_PLOTS out along the world line, faded in to where the line leaves the city's ground",
                starts && !routes.isEmpty());
        check("...then on track every quarter plot, from the gate arm's end to its yard's box (CityRuns.wayTo())", onTrack);
        check("...and its ladder and siding inside the yard's box, the freight station's routes marked so", intoYard && stationOnes);
        check("...kept against the map's changes: asked again, the same list", m.trainRoutes() == routes);

        // ---- CALLS: tonnes over a payload, past MOST_A_MONTH each drawn train standing for several.
        int kinds = Ports.Cargo.values().length;
        double[] in = new double[kinds], out0 = new double[kinds];
        in[Ports.Cargo.DRY_BULK.ordinal()] = 200_000;
        TrainSchedule worked = TrainSchedule.of(7, in, out0, routes);
        check("the spec's worked example: 200,000 t of bulk is 25 unit trains, one for one",
                worked.callCount() == 25 && worked.callsOf(TrainSchedule.Kind.HOPPER) == 25 && worked.stands() == 1);
        double[] boxIn = new double[kinds], boxOut = new double[kinds];
        boxIn[Ports.Cargo.CONTAINER.ordinal()] = 9 * TrainSchedule.Kind.INTERMODAL.tonnes();
        boxOut[Ports.Cargo.CONTAINER.ordinal()] = 4 * TrainSchedule.Kind.INTERMODAL.tonnes();
        TrainSchedule both = TrainSchedule.of(7, boxIn, boxOut, routes);
        check("...boxes go both ways in one train: 9 trains' boxes in and 4 out is 9 intermodal trains",
                both.callsOf(TrainSchedule.Kind.INTERMODAL) == 9 && both.boxes());
        double[] big = new double[kinds];
        big[Ports.Cargo.DRY_BULK.ordinal()] = 1_000_000;
        TrainSchedule busy = TrainSchedule.of(7, big, out0, routes);
        check("...1,000,000 t is 125 trains, so each of the 25 drawn stands for 5 (MOST_A_MONTH 30)",
                busy.stands() == 5 && busy.callCount() == 25);
        check("...and a city that rails nothing has no trains", TrainSchedule.of(7, new double[kinds], new double[kinds], routes).callCount() == 0
                && TrainSchedule.of(7, (ham.citybuildersim.sectors.Freight) null, routes).callCount() == 0);

        // ---- WHERE THEY GO: boxes and general cargo to the station; bulk elsewhere while the station handles boxes.
        double[] mixIn = new double[kinds], mixOut = new double[kinds];
        mixIn[Ports.Cargo.DRY_BULK.ordinal()] = 6 * TrainSchedule.UNIT_TONNES;
        mixOut[Ports.Cargo.LIQUID.ordinal()] = 3 * TrainSchedule.UNIT_TONNES;
        mixIn[Ports.Cargo.CONTAINER.ordinal()] = 5 * TrainSchedule.Kind.INTERMODAL.tonnes();
        mixOut[Ports.Cargo.GENERAL.ordinal()] = 4 * TrainSchedule.MIXED_TONNES;
        TrainSchedule mix = TrainSchedule.of(7, mixIn, mixOut, routes);
        boolean byKind = true;
        boolean others = routes.stream().anyMatch(rt -> !rt.station());
        for (TrainSchedule.Call c : mix.calls()) {
            boolean atStation = c.route() >= 0 && routes.get(c.route()).station();
            byKind &= c.route() >= 0 && (c.kind().toStation() ? atStation : !others || !atStation);
        }
        double[] bulkOnly = mixIn.clone();
        bulkOnly[Ports.Cargo.CONTAINER.ordinal()] = 0;
        TrainSchedule noBoxes = TrainSchedule.of(7, bulkOnly, new double[kinds], routes);
        boolean stationToo = false;
        for (TrainSchedule.Call c : noBoxes.calls()) stationToo |= routes.get(c.route()).station();
        out.printf("      a month of every kind: %d trains, %d of them bulk; with no boxes the bulk trains use the station too: %b%n",
                mix.callCount(), mix.callsOf(TrainSchedule.Kind.HOPPER) + mix.callsOf(TrainSchedule.Kind.TANK), stationToo);
        check("...boxes and general cargo to the freight station, bulk to the other yards while the station is a container yard",
                mix.callCount() == 18 && mix.boxes() && byKind);
        check("...and with no boxes, to every yard the station among them", !noBoxes.boxes() && stationToo);

        // ---- PURE IN (call, t): fading in along the world line, whole on its siding, gone at either end of its visit.
        double leg = TrainSchedule.LEG_SECONDS / TrainSchedule.MONTH_SECONDS, dwell = TrainSchedule.DWELL_SECONDS / TrainSchedule.MONTH_SECONDS;
        boolean pure = true, alpha = true, whole = true, gone = true, loaded = true;
        for (TrainSchedule.Call c : mix.calls()) {
            TrainSchedule.Route rt = routes.get(c.route());
            for (double t = 0; t <= 1; t += 1 / 97.0) {
                TrainSchedule.Train a = TrainSchedule.position(c, rt, t), b = TrainSchedule.position(c, rt, t);
                pure &= java.util.Objects.equals(a, b) && java.util.Objects.equals(TrainSchedule.cars(a, rt), TrainSchedule.cars(b, rt));
                for (TrainSchedule.Car car : TrainSchedule.cars(a, rt)) alpha &= car.alpha() >= 0 && car.alpha() <= 1;
            }
            TrainSchedule.Train standing = TrainSchedule.position(c, rt, c.arrives() + dwell / 2);
            List<TrainSchedule.Car> cars = TrainSchedule.cars(standing, rt);
            whole &= standing != null && standing.standing() && cars.size() == c.kind().wagons() + 2;
            for (TrainSchedule.Car car : cars) whole &= car.alpha() == 1 || rt.length() - c.kind().length() < rt.fadeTo();
            gone &= TrainSchedule.cars(TrainSchedule.position(c, rt, c.arrives() - leg), rt).isEmpty()
                    && TrainSchedule.cars(TrainSchedule.position(c, rt, c.arrives() + dwell + leg), rt).isEmpty()
                    && TrainSchedule.position(c, rt, c.arrives() - leg - 1e-6) == null;
            boolean in0 = TrainSchedule.position(c, rt, c.arrives() - leg / 2).loaded(), out1 = TrainSchedule.position(c, rt, c.arrives() + dwell + leg / 2).loaded();
            loaded &= c.kind() == TrainSchedule.Kind.INTERMODAL ? in0 && out1 : in0 == c.inbound() && out1 == !c.inbound();
        }
        check("TRAINS: pure in (call, t), each vehicle's fade within [0, 1]", pure && alpha);
        check("...on its siding every vehicle there, whole past the world line's part", whole);
        check("...nothing of it on the line at either end of its visit, so none appears or vanishes", gone);
        check("...an import in full and out empty, an export the other way, boxes loaded both ways", loaded);

        // ---- A FRAME: every train with a vehicle on screen, looking at only those whose visit spans t.
        long sx = station[0] + station[2] / 2, sy = station[1] + station[3] / 2;
        long l0w = Math.round(SCREEN_W_PX / MapFrame.L0_FROM), l0h = Math.round(SCREEN_H_PX / MapFrame.L0_FROM);
        boolean frames = true;
        int shown = 0, spanned = 0;
        for (double t = 0.05; t < 1; t += 0.0377) {
            long vx0 = sx - l0w / 2, vy0 = sy - l0h / 2, vx1 = vx0 + l0w, vy1 = vy0 + l0h;
            List<TrainSchedule.Train> got = mix.frame(vx0, vy0, vx1, vy1, t);
            int want = 0, live = 0;
            for (TrainSchedule.Call c : mix.calls()) {
                TrainSchedule.Train tr = TrainSchedule.position(c, routes.get(c.route()), t);
                if (c.arrives() - leg <= t && t <= c.arrives() + dwell + leg) live++;
                if (tr == null) continue;
                for (TrainSchedule.Car car : TrainSchedule.cars(tr, routes.get(c.route()))) {
                    double w = TrainSchedule.LOCO_PLOTS;
                    if (car.x() + w >= vx0 && car.x() - w < vx1 && car.y() + w >= vy0 && car.y() - w < vy1) { want++; break; }
                }
            }
            frames &= got.size() == want && mix.touched() <= live;
            shown += got.size();
            spanned += live;
        }
        out.printf("      an L0 screen about the freight station through the month: %d trains drawn in 26 frames, of %d on the line%n", shown, spanned);
        check("...a frame finds every train with a vehicle on screen, looking at no train off the line", frames && shown > 0);

        // ---- 10 BILLION: the prototype's trade scaled by people, all of it railed, on 1,600 yards of one main line.
        double tonnes10B = PROTO_TONNES * 1e10 / PROTO_PEOPLE;
        double[] huge = new double[kinds];
        for (Ports.Cargo c : Ports.Cargo.values()) huge[c.ordinal()] = tonnes10B / kinds;
        List<TrainSchedule.Route> many = syntheticYards(sx, sy, TRAIN_YARDS_A_SIDE);
        TrainSchedule ten = TrainSchedule.of(7, huge, huge, many);
        long[][] views = {
                { Math.round(SCREEN_W_PX / MapFrame.L0_FROM), Math.round(SCREEN_H_PX / MapFrame.L0_FROM) },
                { Math.round(SCREEN_W_PX / MapFrame.L1_FROM), Math.round(SCREEN_H_PX / MapFrame.L1_FROM) } };
        double[] best = { Double.MAX_VALUE, Double.MAX_VALUE };
        int[] seen = new int[views.length];
        ten.frame(sx, sy, sx + 1, sy + 1, .5);
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int v = 0; v < views.length; v++) {
                long ax = sx - views[v][0] / 2, ay = sy - views[v][1] / 2, bx = ax + views[v][0], by = ay + views[v][1];
                long f0 = System.nanoTime();
                int n = 0;
                for (int k = 0; k < BOAT_FRAMES; k++) {
                    double t = (k + 0.5) / BOAT_FRAMES;
                    if (!ten.crosses(ax, ay, bx, by)) continue;
                    for (TrainSchedule.Train tr : ten.frame(ax, ay, bx, by, t)) n += TrainSchedule.cars(tr, many.get(tr.call().route())).size();
                }
                double ms = (System.nanoTime() - f0) / 1e6 / BOAT_FRAMES;
                if (round >= WARM_ROUNDS) best[v] = Math.min(best[v], ms);
                seen[v] = n / BOAT_FRAMES;
            }
        }
        out.printf("      at 10B (%,.0f t a month railed, %d trains drawn each standing for %,d, on %,d routes): a frame of an L0 screen %.3f ms"
                + " (%d vehicles), of an L1 screen %.3f ms (%d vehicles)%n", tonnes10B * 2, ten.callCount(), ten.stands(), many.size(),
                best[0], seen[0], best[1], seen[1]);
        check("...at 10 billion a frame's trains take no more than TRAIN_FRAME_MS (0.5 ms): an L0 screen and an L1 one",
                Timing.within(best[0], TRAIN_FRAME_MS) && Timing.within(best[1], TRAIN_FRAME_MS) && ten.callCount() <= TrainSchedule.MOST_A_MONTH + 2 * kinds);

        // ---- THE STATION'S LOOK: a railway that moved no boxes draws its station as a bulk yard (RailCheck 7 holds the other way).
        ham.citybuildersim.sectors.Freight rail = city.getSectors().freight();
        double boxes = rail.railedIn(Ports.Cargo.CONTAINER) + rail.railedOut(Ports.Cargo.CONTAINER);
        double railed = 0;
        for (Ports.Cargo c : Ports.Cargo.values()) railed += rail.railedIn(c) + rail.railedOut(c);
        out.printf("      the played city's railway moved %,.0f t last month, %,.0f t of it in boxes; its yard's picture says goods: %b%n",
                railed, boxes, city.getCityMap().railPicture().goodsByRail());
        check("...and the played city's railway moved no boxes, so its freight station is no container yard (TrainSchedule.boxesByRail())",
                boxes == 0 && !city.getCityMap().railPicture().goodsByRail() && !city.mapRail().goodsByRail());
    }

    /**
     * A city of yards about (cx, cy) for the frame at 10 billion: side x side
     * yards on a grid four cells apart, each with its YARD_TRACKS routes in
     * from one gateway WORLD_PLOTS west - along the row to its column, down
     * the column to the yard, onto a siding - so every route shares the main
     * line, as a real city's would.
     */
    static List<TrainSchedule.Route> syntheticYards(long cx, long cy, int side) {
        List<TrainSchedule.Route> out = new ArrayList<>();
        double gx = cx - side * 2.0 * World.TILE - TrainSchedule.WORLD_PLOTS, gy = cy + 0.5;
        int yard = 0;
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++, yard++) {
                double x = cx + (i - side / 2.0) * 4 * World.TILE + 0.5, y = cy + (j - side / 2.0) * 4 * World.TILE + 0.5;
                for (int s = 0; s < TileRaster.YARD_TRACKS; s++) {
                    double off = (s - (TileRaster.YARD_TRACKS - 1) / 2.0) * TileRaster.YARD_TRACK_APART;
                    double[] xs = { gx, gx + TrainSchedule.WORLD_PLOTS, x, x, x + off, x + off };
                    double[] ys = { gy, gy, gy, y, y + TileRaster.YARD_LADDER, y + 24 - TileRaster.YARD_LADDER };
                    out.add(TrainSchedule.Route.through(yard, yard == 0, s, xs, ys, TrainSchedule.WORLD_PLOTS));
                }
            }
        }
        return out;
    }

    /** The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. */
    static final int SCREEN_W_PX = 1389, SCREEN_H_PX = 868;

    /** BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. */
    static final double PROTO_TONNES = 896_308, PROTO_PEOPLE = 469_092;

    /** BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). */
    static final int PROTO_LANES = 20_000;
    /** ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). */
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

    /* =====================================================================
       10. THE MAP ON EARTH (0.7.119, batch E5)
       ===================================================================== */

    /** The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTerrain.WATER_STRIDE, DITHER_STRIDE). */
    static final int WINDOW_STRIDE = EarthTerrainCheck.WINDOW / MapFrame.NODE_PX;

    /** The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. */
    static final int[] CLOSE_STRIDES = { 2, 4, 8 };

    /** How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. */
    static final int SEAM_SHIFT_PX = MapFrame.NODE_PX / 2;

    /** The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. */
    static final double CHUKOTKA_LAT = 66, CHUKOTKA_LON = 180;

    /** ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. */
    static final int CHUKOTKA_STRIDE = EarthTerrain.FAR_TILE_BLEND_STRIDE;

    /** The full file's small lakes: Finland's lake district, 62 N 26 E. */
    static final double LAKES_LAT = 62, LAKES_LON = 26;

    /** ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. */
    static final int LAKES_NODES = 3;

    /** Section 10. */
    static void onEarth(BuildingVisual.Type[] types) throws Exception {
        out.println("\n--- 10. the map on Earth: the far nodes from the data's vectors, the seam, the void past the rim, the hover's place and geology ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null && expect != null);
        if (fixture == null || expect == null) return;
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        Map<String, EarthTerrainCheck.Spot> spots = new java.util.LinkedHashMap<>();
        for (String line : new String(expect, java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length > 4 && f[1].equals("window")) spots.put(f[2], new EarthTerrainCheck.Spot(f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4])));
        }
        long seed = EarthTerrainCheck.SEED;
        int n = MapFrame.NODE_PX;
        // A generated world's far node is its region, at every stride a view draws.
        int widest = (int) new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H).nodePlots();
        boolean same = true;
        World gen = World.of(seed);
        for (int s = 1; s <= widest; s *= 2) {
            long span = (long) n * s, x0 = Math.floorDiv(gen.foundingX(), span) * span, y0 = Math.floorDiv(gen.foundingY(), span) * span;
            byte[] want = new byte[n * n];
            gen.regionTerrain(x0, y0, s, n, want);
            same &= Arrays.equals(want, MapTiles.nodeTerrain(World.Key.generated(seed), x0, y0, s));
        }
        check("a generated world's far node is its region, at every stride from 1 to " + widest + " plots a pixel, as before", same);

        // The study's three windows, each one node at WINDOW_STRIDE, and closer.
        for (String name : List.of("Rotterdam", "Perth", "Paris")) {
            EarthTerrainCheck.Spot s = spots.get(name);
            World w = World.of(World.Key.earth(seed, s.lat(), s.lon(), id));
            farWindow(name, w, data);
        }

        // Twice, and from a world made afresh.
        EarthTerrainCheck.Spot rot = spots.get("Rotterdam");
        World.Key rk = World.Key.earth(seed, rot.lat(), rot.lon(), id);
        long wx0 = EarthTerrain.spotPlot() - EarthTerrainCheck.WINDOW / 2;
        byte[] a = MapTiles.nodeTerrain(rk, wx0, wx0, WINDOW_STRIDE), b = MapTiles.nodeTerrain(rk, wx0, wx0, WINDOW_STRIDE), c = new byte[n * n];
        new World(rk).earthTerrain().farTerrain(wx0, wx0, WINDOW_STRIDE, n, c);
        check("Rotterdam's far node is the same twice, and from a world made afresh from its key", Arrays.equals(a, b) && Arrays.equals(a, c));

        // Its cost: the window's node, its data read, against SCREEN_MS.
        double best = Double.MAX_VALUE;
        for (int r = 0; r < WARM_ROUNDS + TIMED_ROUNDS; r++) {
            long t0 = System.nanoTime();
            MapTiles.nodeTerrain(rk, wx0, wx0, WINDOW_STRIDE);
            double ms = (System.nanoTime() - t0) / 1e6;
            if (r >= WARM_ROUNDS) best = Math.min(best, ms);
        }
        out.printf("      Rotterdam's window as a far node (%d plots a pixel): %.1f ms (the study measured 1.4 to 3.7 ms for a 128-pixel node)%n", WINDOW_STRIDE, best);
        check("...drawn in no more than SCREEN_MS (" + (int) SCREEN_MS + " ms), on the worker away from the screen", Timing.within(best, SCREEN_MS));

        // The 180th meridian: Fiji's window, nodes overlapping by half.
        EarthTerrainCheck.Spot fiji = spots.get("Fiji");
        seam("Fiji's window", World.of(World.Key.earth(seed, fiji.lat(), fiji.lon(), id)), WINDOW_STRIDE, data, false);

        // The void past the rim: the far node at the world's corner, painted.
        World rw = World.of(rk);
        CityMap map = CityMap.square(rk, rw.foundingX(), rw.foundingY(), types, 1, (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT),
                new long[types.length]);
        MapTiles tiles = new MapTiles(map);
        byte[] corner = MapTiles.nodeTerrain(rk, 0, 0, widest);
        int[] px = new int[n * n];
        tiles.nodePixels(0, 0, widest, corner, px);
        int voids = 0, offPlanet = 0, wrong = 0;
        for (int k = 0; k < px.length; k++) {
            double x = (k % n + 0.5) * widest, y = (k / n + 0.5) * widest;
            boolean off = !EarthProj.onPlanet(EarthProj.metresX(x), EarthProj.metresY(y));
            if (off) offPlanet++;
            if (px[k] == TileRaster.VOID) voids++;
            if (off != (px[k] == TileRaster.VOID)) wrong++;
        }
        out.printf("      the world's corner node (%d plots a pixel): %,d of its %,d pixels past the rim, %,d drawn as the void%n", widest, offPlanet, px.length, voids);
        check("past the planet's rim the far node's pixels are the void, and nowhere else", offPlanet > 0 && wrong == 0);

        // The hover card on Earth: the place, the province, and past the rim.
        List<EarthTerrainCheck.GeoSpot> geo = EarthTerrainCheck.geoSpots(new java.util.LinkedHashMap<>(spots), id);
        boolean places = true, provinces = true;
        for (EarthTerrainCheck.GeoSpot g : geo.subList(0, 3)) {
            World.Key key = World.Key.earth(seed, g.lat(), g.lon(), id);
            CityLand land = LandManager.foundingLand(key);
            long x = EarthTerrain.spotPlot(), y = EarthTerrain.spotPlot();
            List<String> words = LandMap.earthWords(land, x, y);
            double[] ll = new double[2];
            World.of(key).earthTerrain().projection().latLonOfPlot(x + 0.5, y + 0.5, ll);
            EarthData.Place near = null;
            double least = Double.MAX_VALUE;
            for (EarthData.Place p : data.places()) {
                double m = EarthProj.distanceM(p.lat(), p.lon(), ll[0], ll[1]);
                if (m < least) { least = m; near = p; }
            }
            List<String> card = LandMap.hoverWords(land, null, x, y);
            out.printf("      %s's spot: %s%n", g.name(), String.join(" | ", card));
            places &= near != null && words.size() == 2 && words.get(0).equals(LandMap.placeWords(near, ll[0], ll[1])) && near.name().trim().equals(g.name())
                    && card.size() >= 4 && card.get(2).equals(words.get(0)) && card.get(3).equals(words.get(1));
            provinces &= words.size() == 2 && words.get(1).equals("Geology: " + g.province() + ", " + LandMap.KIND_WORDS[g.kind().ordinal()]);
        }
        check("the hover names the nearest of the data's places by distance and direction - the window's own city - after whose the ground is", places);
        check("...and the province under the spot with its class (EarthTerrainCheck 11's), after it", provinces);
        CityLand rland = LandManager.foundingLand(rk);
        check("...past the rim it says the map runs off the planet; and a generated city's card has no such lines",
                LandMap.earthWords(rland, 0, 0).equals(List.of(LandMap.OFF_PLANET_WORDS))
                        && LandMap.earthWords(LandManager.foundingLand(World.Key.generated(seed)), gen.foundingX(), gen.foundingY()).isEmpty());

        fullFile(widest, rot);
    }

    /**
     * One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES:
     * closer, the region but for its rivers stroked wider; at the window's
     * stride every river stroked at least FAR_RIVER_HALF_PX a side, every
     * small lake's blob's sure part fresh, the sea the data's and the
     * forest, dithered, the data's.
     */
    static void farWindow(String name, World w, EarthData data) {
        EarthTerrain e = w.earthTerrain();
        int n = MapFrame.NODE_PX;
        boolean close = true;
        StringBuilder closeWords = new StringBuilder();
        for (int s : CLOSE_STRIDES) {
            long x0 = EarthTerrain.spotPlot() - (long) n * s / 2;
            byte[] far = new byte[n * n], reg = new byte[n * n];
            e.farTerrain(x0, x0, s, n, far);
            e.regionTerrain(x0, x0, s, n, reg);
            Water water = new Water(e, data, x0, x0, s);
            int wider = 0;
            for (int k = 0; k < far.length; k++) {
                if (far[k] == reg[k]) continue;
                boolean ok = far[k] == World.FRESH && water.inStroke(k % n, k / n, false);
                close &= ok;
                if (ok) wider++;
            }
            closeWords.append(String.format("%s%d: %,d", closeWords.length() == 0 ? "" : ", ", s, wider));
        }
        out.printf("      %s closer: the region's pixels but those its rivers' strokes widen - %s plots a pixel%n", name, closeWords);
        check(name + ": under WATER_STRIDE the far node is E2's region but for its rivers, stroked wider", close);
        int s = WINDOW_STRIDE;
        long x0 = EarthTerrain.spotPlot() - EarthTerrainCheck.WINDOW / 2;
        byte[] far = new byte[n * n], reg = new byte[n * n];
        e.farTerrain(x0, x0, s, n, far);
        e.regionTerrain(x0, x0, s, n, reg);
        long[] counts = EarthTerrain.FAR_COUNTS.get().clone();
        Water water = new Water(e, data, x0, x0, s);
        int banded = 0, bandMissed = 0, cores = 0, coreMissed = 0;
        for (int k = 0; k < far.length; k++) {
            if (far[k] == World.SALT) continue;
            if (water.inStroke(k % n, k / n, true)) {
                banded++;
                if (far[k] != World.FRESH) bandMissed++;
            }
            if (water.inCore(k % n, k / n)) {
                cores++;
                if (far[k] != World.FRESH) coreMissed++;
            }
        }
        // The sea and the forest against the data's shares at the pixels' centres.
        double[] ll = new double[2];
        double seaData = 0, forestData = 0;
        int dry = 0, forest = 0, sea = 0, regForest = 0, regDry = 0;
        EarthProj proj = e.projection();
        for (int k = 0; k < far.length; k++) {
            proj.latLonOfPlot(x0 + (k % n + 0.5) * s, x0 + (k / n + 0.5) * s, ll);
            seaData += data.at("sea", ll[0], ll[1]) / (double) EarthData.SHARE_LEVELS;
            if (far[k] == World.SALT) sea++;
            if (World.dry(far[k])) {
                dry++;
                if (far[k] == World.FOREST) forest++;
                forestData += (data.at("landcover", ll[0], ll[1]) & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
            }
            if (World.dry(reg[k])) {
                regDry++;
                if (reg[k] == World.FOREST) regForest++;
            }
        }
        double seaPts = 100.0 * Math.abs(sea / (double) far.length - seaData / far.length);
        double forestPts = dry > 0 ? 100.0 * Math.abs(forest / (double) dry - forestData / dry) : 0;
        out.printf("      %s's window at %d plots a pixel: %,d segments and %,d small lakes drawn from %,d tiles; %,d pixels within a river's least"
                        + " half-width, %,d in a small lake's sure part; sea %.2f%% against the data's %.2f%%; forest %.1f%% of the dry against the"
                        + " data's %.1f%% (the region, undithered, %.1f%%)%n", name, s, counts[0], counts[1], counts[2], banded, cores,
                100.0 * sea / far.length, 100 * seaData / far.length, 100.0 * forest / Math.max(1, dry), 100 * forestData / Math.max(1, dry),
                100.0 * regForest / Math.max(1, regDry));
        check(name + ": every pixel within FAR_RIVER_HALF_PX of a river's line is fresh (or sea) - stroked unbroken, at least a pixel wide",
                banded > 0 && bandMissed == 0);
        check(name + ": every pixel in a small lake's sure part (BLOB_FRAY) is fresh (or sea)", (cores > 0 || counts[1] == 0) && coreMissed == 0);
        check(name + ": the far node's sea, and its forest of the dry ground, are the data's within EarthTerrainCheck.SHARE_POINTS",
                seaPts <= EarthTerrainCheck.SHARE_POINTS && forestPts <= EarthTerrainCheck.SHARE_POINTS);
    }

    /**
     * The rivers and small lakes under a far node, as the check reads them
     * from the data itself: each run the node would stroke (its median width
     * at least FAR_RIVER_SHARE of a pixel), its segments projected exactly,
     * and each small lake's point and radius.
     */
    static final class Water {
        final List<double[]> segs = new ArrayList<>();
        final List<double[]> lakes = new ArrayList<>();
        final long x0, y0;
        final int stride;

        Water(EarthTerrain e, EarthData data, long x0, long y0, int stride) {
            this.x0 = x0;
            this.y0 = y0;
            this.stride = stride;
            EarthProj proj = e.projection();
            double side = (double) stride * MapFrame.NODE_PX, narrowest = EarthTerrain.FAR_RIVER_SHARE * stride * World.PLOT_M;
            double least = EarthTerrain.FAR_RIVER_HALF_PX * stride;
            double[] p = new double[2], q = new double[2];
            for (int t : e.nodeTiles(x0, y0, side)) {
                for (EarthData.Run run : data.rivers(t)) {
                    int m = run.size();
                    if (m < 2) continue;
                    int[] w = new int[m];
                    for (int k = 0; k < m; k++) w[k] = run.width(k);
                    Arrays.sort(w);
                    if (w[m / 2] < narrowest) continue;
                    for (int k = 0; k + 1 < m; k++) {
                        if (!proj.plotOf(run.lat(k), run.lon(k), p) || !proj.plotOf(run.lat(k + 1), run.lon(k + 1), q)) continue;
                        segs.add(new double[] { p[0], p[1], q[0], q[1], Math.max(least, run.width(k) / 2.0 / World.PLOT_M),
                                Math.max(least, run.width(k + 1) / 2.0 / World.PLOT_M) });
                    }
                }
                EarthData.SmallLakes sl = data.smallLakes(t);
                for (int k = 0; k < sl.size(); k++) {
                    if (!proj.plotOf(sl.lat(k), sl.lon(k), p)) continue;
                    lakes.add(new double[] { p[0], p[1], Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / World.PLOT_M });
                }
            }
        }

        /** Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. */
        boolean inStroke(int i, int j, boolean least) {
            double x = x0 + (i + 0.5) * stride, y = y0 + (j + 0.5) * stride, floor = EarthTerrain.FAR_RIVER_HALF_PX * stride;
            for (double[] g : segs) {
                double h = least ? floor : Math.max(g[4], g[5]);
                if (x < Math.min(g[0], g[2]) - h || x > Math.max(g[0], g[2]) + h || y < Math.min(g[1], g[3]) - h || y > Math.max(g[1], g[3]) + h) continue;
                double vx = g[2] - g[0], vy = g[3] - g[1], l2 = vx * vx + vy * vy;
                double t = l2 > 0 ? Math.max(0, Math.min(1, ((x - g[0]) * vx + (y - g[1]) * vy) / l2)) : 0;
                double dx = x - g[0] - t * vx, dy = y - g[1] - t * vy, w = least ? floor : g[4] + (g[5] - g[4]) * t;
                if (dx * dx + dy * dy < w * w) return true;
            }
            return false;
        }

        /** Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. */
        boolean inCore(int i, int j) {
            double x = x0 + (i + 0.5) * stride, y = y0 + (j + 0.5) * stride;
            for (double[] l : lakes) {
                double dx = x - l[0], dy = y - l[1];
                if ((dx * dx + dy * dy) / (l[2] * l[2]) <= 1 - EarthTerrain.BLOB_FRAY / 2) return true;
            }
            return false;
        }
    }

    /**
     * A node holding the 180th meridian (its middle on the spot, which is on
     * it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half
     * each shares with it, pixel for pixel the same; each side of the line's
     * land the data's within EarthTerrainCheck.SHARE_POINTS; and (`water`)
     * fresh water drawn from the data's vectors on both sides.
     */
    static void seam(String where, World w, int stride, EarthData data, boolean water) {
        EarthTerrain e = w.earthTerrain();
        int n = MapFrame.NODE_PX, h = SEAM_SHIFT_PX;
        long x0 = EarthTerrain.spotPlot() - (long) n * stride / 2, y0 = x0;
        byte[] mid = new byte[n * n], west = new byte[n * n], east = new byte[n * n];
        e.farTerrain(x0, y0, stride, n, mid);
        e.farTerrain(x0 - (long) h * stride, y0, stride, n, west);
        e.farTerrain(x0 + (long) h * stride, y0, stride, n, east);
        boolean agree = true;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n - h; i++) {
                agree &= mid[j * n + i] == west[j * n + i + h];
                agree &= mid[j * n + i + h] == east[j * n + i];
            }
        }
        double[] ll = new double[2], land = new double[2], landData = new double[2], cells = new double[2], fresh = new double[2], region = new double[2];
        byte[] reg = new byte[n * n];
        e.regionTerrain(x0, y0, stride, n, reg);
        for (int k = 0; k < mid.length; k++) {
            if (!e.projection().latLonOfPlot(x0 + (k % n + 0.5) * stride, y0 + (k / n + 0.5) * stride, ll)) continue;
            int side = ll[1] < 0 ? 1 : 0;
            cells[side]++;
            if (mid[k] != World.SALT) land[side]++;
            if (mid[k] == World.FRESH && reg[k] != World.FRESH) fresh[side]++;
            landData[side] += 1 - data.at("sea", ll[0], ll[1]) / (double) EarthData.SHARE_LEVELS;
        }
        double wPts = 100 * Math.abs(land[0] - landData[0]) / Math.max(1, cells[0]), ePts = 100 * Math.abs(land[1] - landData[1]) / Math.max(1, cells[1]);
        out.printf("      %s at %d plots a pixel: %,.0f pixels west of the 180th meridian, %,.0f east; land %.2f%% and %.2f%% against the data's"
                        + " %.2f%% and %.2f%%; fresh water from the data's vectors on %,.0f and %,.0f%n", where, stride, cells[0], cells[1],
                100 * land[0] / Math.max(1, cells[0]), 100 * land[1] / Math.max(1, cells[1]), 100 * landData[0] / Math.max(1, cells[0]),
                100 * landData[1] / Math.max(1, cells[1]), fresh[0], fresh[1]);
        check(where + ": no seam at 180 degrees - nodes overlapping by half agree pixel for pixel, each side's land the data's"
                + (water ? ", rivers and lakes drawn on both" : ""),
                agree && cells[0] > 0 && cells[1] > 0 && wPts <= EarthTerrainCheck.SHARE_POINTS && ePts <= EarthTerrainCheck.SHARE_POINTS
                        && (!water || (fresh[0] > 0 && fresh[1] > 0)));
    }

    /** Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. */
    static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception {
        Path file = EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            out.println("      earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's far views were not drawn");
            return;
        }
        EarthData d = EarthData.open(file);
        String id = EarthTerrain.use(d);
        long t0 = System.nanoTime();
        double longest = 0;
        for (int t = 0; t < EarthData.TILES; t++) {
            if (d.entry("rivers", t) <= 0) continue;
            for (EarthData.Run r : d.rivers(t)) {
                for (int k = 0; k + 1 < r.size(); k++) longest = Math.max(longest, EarthProj.distanceM(r.lat(k), r.lon(k), r.lat(k + 1), r.lon(k + 1)));
            }
        }
        out.printf("      the full file: its longest river segment %,.0f m (%.0f ms); RIM_M %.1f m%n", longest, (System.nanoTime() - t0) / 1e6, EarthTerrain.RIM_M);
        check("the full file's longest river segment is EarthTerrain.LONGEST_SEGMENT_M, which RIM_M is drawn from", Math.round(longest) == EarthTerrain.LONGEST_SEGMENT_M);
        // Every stride over Rotterdam, cold and warm.
        long seed = EarthTerrainCheck.SEED;
        World.Key rk = World.Key.earth(seed, rot.lat(), rot.lon(), id);
        EarthTerrain e = World.of(rk).earthTerrain();
        int n = MapFrame.NODE_PX;
        StringBuilder times = new StringBuilder();
        boolean within = true;
        double worst = 0;
        for (int s = 2; s <= widest; s *= 2) {
            long span = (long) n * s, x0 = Math.floorDiv(EarthTerrain.spotPlot(), span) * span;
            byte[] g = new byte[n * n];
            long t1 = System.nanoTime();
            e.farTerrain(x0, x0, s, n, g);
            double cold = (System.nanoTime() - t1) / 1e6, best = Double.MAX_VALUE;
            for (int r = 0; r < WARM_ROUNDS + TIMED_ROUNDS; r++) {
                t1 = System.nanoTime();
                e.farTerrain(x0, x0, s, n, g);
                if (r >= WARM_ROUNDS) best = Math.min(best, (System.nanoTime() - t1) / 1e6);
            }
            within &= Timing.within(best, SCREEN_MS);
            worst = Math.max(worst, best);
            times.append(String.format("%s%d: %.1f (%.0f)", times.length() == 0 ? "" : ", ", s, best, cold));
        }
        out.printf("      Rotterdam's far node, ms at each stride (the first time): %s%n", times);
        // The whole world's view at the widest stride.
        long span = (long) n * widest;
        int across = (int) ((World.SIDE + span - 1) / span);
        t0 = System.nanoTime();
        byte[] g = new byte[n * n];
        long fresh = 0, land = 0;
        for (int j = 0; j < across; j++) for (int i = 0; i < across; i++) {
            e.farTerrain(i * span, j * span, widest, n, g);
            for (byte b : g) {
                if (b != World.SALT) land++;
                if (b == World.FRESH) fresh++;
            }
        }
        out.printf("      the whole world at %d plots a pixel: %d nodes in %.0f ms, fresh water %.1f%% of what is not sea%n", widest, across * across,
                (System.nanoTime() - t0) / 1e6, 100.0 * fresh / Math.max(1, land));
        check("on the full file, every stride's far node over Rotterdam is drawn in no more than SCREEN_MS", within && worst > 0);
        // Finland's small lakes: their discs' pixels against their area.
        EarthTerrain f = World.of(World.Key.earth(seed, LAKES_LAT, LAKES_LON, id)).earthTerrain();
        int s = WINDOW_STRIDE;
        double area = 0, centres = 0;
        int lakes = 0, coreMissed = 0;
        long c0 = EarthTerrain.spotPlot() - (long) LAKES_NODES * n * s / 2;
        for (int j = 0; j < LAKES_NODES; j++) for (int i = 0; i < LAKES_NODES; i++) {
            long x0 = c0 + (long) i * n * s, y0 = c0 + (long) j * n * s;
            f.farTerrain(x0, y0, s, n, g);
            Water w = new Water(f, d, x0, y0, s);
            for (double[] l : w.lakes) {
                if (l[0] < x0 || l[1] < y0 || l[0] >= x0 + (double) n * s || l[1] >= y0 + (double) n * s) continue;
                lakes++;
                area += Math.PI * l[2] * l[2] / ((double) s * s);
                int i0 = (int) Math.max(0, Math.floor((l[0] - l[2] - x0) / s - 1)), i1 = (int) Math.min(n - 1, Math.ceil((l[0] + l[2] - x0) / s));
                int j0 = (int) Math.max(0, Math.floor((l[1] - l[2] - y0) / s - 1)), j1 = (int) Math.min(n - 1, Math.ceil((l[1] + l[2] - y0) / s));
                for (int jj = j0; jj <= j1; jj++) for (int ii = i0; ii <= i1; ii++) {
                    double dx = x0 + (ii + 0.5) * s - l[0], dy = y0 + (jj + 0.5) * s - l[1], t = (dx * dx + dy * dy) / (l[2] * l[2]);
                    if (t < 1) centres++;
                    if (t <= 1 - EarthTerrain.BLOB_FRAY / 2 && g[jj * n + ii] != World.FRESH && g[jj * n + ii] != World.SALT) coreMissed++;
                }
            }
        }
        double off = area > 0 ? centres / area - 1 : Double.NaN;
        out.printf("      Finland's lake district, %d x %d nodes at %d plots a pixel: %,d small lakes, their discs on %,.0f pixel centres for %,.0f"
                + " pixels of area (%+.1f%%)%n", LAKES_NODES, LAKES_NODES, s, lakes, centres, area, 100 * off);
        check("a far node's small lakes, discs at their pixels' centres, hold their own area within EarthTerrainCheck.LAKE_AREA, each drawn",
                lakes > 0 && Math.abs(off) <= EarthTerrainCheck.LAKE_AREA && coreMissed == 0);
        seam("Chukotka", World.of(World.Key.earth(seed, CHUKOTKA_LAT, CHUKOTKA_LON, id)), CHUKOTKA_STRIDE, d, true);
    }

    /* =====================================================================
       11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2)
       ===================================================================== */

    /** Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. */
    static final int PICTURE_MONTHS = MONTHS;

    /** How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. */
    static final double PICTURE_SIGMAS = 4;

    /** Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. */
    static final int NODE_ROUNDS = TIMED_ROUNDS;

    static void pictures(Game city, Squares sq) {
        out.println("\n--- 11. the district pictures (0.7.128, batch MM1): each district from its plan, kept by its plots; the far nodes from them ---");
        BuildingVisual.Type[] types = sq.types;

        // ----- A picture is its plan's, pixel for pixel, and its shares its plots' -----
        CityMap played = city.getCityMap(), x1 = sq.maps[0], iron = sq.ironX1, dense = sq.maps[DENSE];
        List<CityMap.District> denseOnes = new ArrayList<>();
        for (CityMap.District d : dense.tileDistricts(Math.floorDiv(sq.x, World.TILE), Math.floorDiv(sq.y, World.TILE))) if (denseOnes.size() < 2) denseOnes.add(d);
        int pictured = 0, exact = 0, sharesWithin = 0, countsAgree = 0, kinds = 0;
        double worstZ = 0;
        String worstAt = "";
        Object[][] fixtures = { { "the played city", played, played.districts() }, { "Jerus's city x 1", x1, x1.districts() },
                { "x 1 on its iron", iron, iron.districts() }, { "x 10,000's screen", dense, denseOnes } };
        for (Object[] fx : fixtures) {
            CityMap m = (CityMap) fx[1];
            @SuppressWarnings("unchecked") List<CityMap.District> ds = (List<CityMap.District>) fx[2];
            int here = 0;
            double hereZ = 0;
            int[] px = new int[DistrictPicture.CODES], pl = new int[DistrictPicture.CODES];
            for (CityMap.District d : ds) {
                if (d.usedCells() <= 0) continue;
                DistrictPicture pic = m.picture(d);
                int[] boxPlots = new int[DistrictPicture.CODES];
                byte[] src = pictureSource(m, d, boxPlots);
                long x0 = (m.baseDX() + d.dx) * World.DISTRICT, y0 = (m.baseDY() + d.dy) * World.DISTRICT;
                pictured++;
                here++;
                if (pic != null && pic.stamp == DistrictPicture.stampOf(src, m.seed(), x0, y0)
                        && pic.samePixels(DistrictPicture.of(src, pic.stamp, m.seed(), x0, y0))) exact++;
                // Its shares of each kind of building against its plots', within PICTURE_SIGMAS of a pixel's binomial spread.
                int[] pc = pic == null ? new int[DistrictPicture.CODES] : pic.counts(), qc = new int[DistrictPicture.CODES];
                for (byte b : src) qc[b]++;
                boolean within = true;
                for (int c = 0; c < DistrictPicture.CODES; c++) {
                    px[c] += pc[c];
                    pl[c] += qc[c];
                    if (!DistrictPicture.isBuilding(c) || qc[c] == 0) continue;
                    kinds++;
                    double ps = pc[c] / (double) DistrictPicture.PIXELS, qs = qc[c] / (double) src.length, sd = DistrictPicture.spread(src, c);
                    double z = sd > 0 ? Math.abs(ps - qs) / sd : 0;
                    within &= Math.abs(ps - qs) <= PICTURE_SIGMAS * sd + 1.0 / DistrictPicture.PIXELS;
                    if (z > worstZ) { worstZ = z; worstAt = fx[0] + " (" + d.dx + ", " + d.dy + ")"; }
                    hereZ = Math.max(hereZ, z);
                }
                if (within) sharesWithin++;
                // ...and its plan's buildings the district's counts as its plan takes them: its own (less those on sites, the runs'
                // yards and the shore's works), with what is carried to it and less what it carries on - each on its whole plots.
                long[] boxes = m.drawn(d).buildingsByType(types.length);
                int[][] carried = m.carried(d);
                int[] own = m.planInput(d).counts;
                long[] want = new long[DistrictPicture.CODES];
                boolean agree = true;
                for (int t = 0; t < types.length; t++) {
                    long w = (t < own.length ? own[t] : 0) + (carried[0] == null || t >= carried[0].length ? 0 : carried[0][t])
                            - (carried[1] == null || t >= carried[1].length ? 0 : carried[1][t]);
                    agree &= boxes[t] == w;
                    if (types[t] != null && w > 0) want[DistrictPicture.codeOf(types[t])] += w * BuildingVisual.cells(types[t]);
                }
                for (int c = 0; c < DistrictPicture.CODES; c++) if (DistrictPicture.isBuilding(c)) agree &= want[c] == boxPlots[c];
                if (agree) countsAgree++;
            }
            if (here == 0) continue;
            StringBuilder sh = new StringBuilder();
            for (int c = 0; c < DistrictPicture.CODES; c++) {
                if (!DistrictPicture.isBuilding(c) || pl[c] == 0) continue;
                double q = 100.0 * pl[c] / ((double) here * World.DISTRICT * World.DISTRICT), p = 100.0 * px[c] / ((double) here * DistrictPicture.PIXELS);
                if (q >= 0.5) sh.append(String.format(" %s %.1f/%.1f", codeName(c), q, p));
            }
            out.printf("      %s, %d built district(s): plots/pixels %%:%s; worst %.2f spreads%n", fx[0], here, sh, hereZ);
        }
        out.printf("      %d pictures, %d kinds of building in them; the worst share %.2f spreads from its plots' (%s)%n", pictured, kinds, worstZ, worstAt);
        check("each built district's picture is its plan's, pixel for pixel: each pixel the plot its fixed hash ranks among its "
                + DistrictPicture.PLOTS_IN, pictured > 0 && exact == pictured);
        check("...each kind of building's share of its pixels within " + (int) PICTURE_SIGMAS + " binomial spreads (and a pixel) of its plots'",
                kinds > 0 && sharesWithin == pictured);
        check("...and the plan it is drawn from holds the district's counts, each on its whole plots (its own, carried in and on)",
                countsAgree == pictured);

        // ----- An unchanged district keeps its picture -----
        CityMap.District first = dense.districts().get(0);
        DistrictPicture kept = dense.picture(first);
        CityMap.Drawn planBefore = dense.drawn(first);
        int drawnOthers = 0;
        for (CityMap.District d : dense.districts()) {
            if (drawnOthers > CityMap.PLANS_KEPT) break;
            if (d == first) continue;
            dense.drawn(d);
            drawnOthers++;
        }
        long keptBefore = dense.picturesKept();
        CityMap.Drawn planAfter = dense.drawn(first);
        boolean sameObject = dense.picture(first) == kept;
        out.printf("      x 10,000's first district: its plan let go past PLANS_KEPT (%d others drawn) and drawn again (%s); pictures kept %d -> %d%n",
                drawnOthers, planAfter != planBefore ? "a new plan" : "the same plan", keptBefore, dense.picturesKept());
        check("a district's plan drawn again to the same plots keeps its picture - the same object, none drawn",
                planAfter != planBefore && sameObject && dense.picturesKept() == keptBefore);

        // ----- A real run: each month redraws the districts whose plots moved, and only those (README, the checks' third rule) -----
        CityMap map = city.getCityMap();
        Map<CityMap.District, DistrictPicture> before = picturesOf(map);
        int months = 0, afresh = 0, exactMonths = 0, missing = 0, districtMonths = 0, moved = 0, unchanged = 0, keptSame = 0, countsMoved = 0;
        long plans = 0, drawn = 0;
        double monthMs = 0, worstMonthMs = 0, picMs = 0;
        PrintStream real = System.out, was = LongPlaytest.out;
        try {
            for (int mm = 0; mm < PICTURE_MONTHS; mm++) {
                LongPlaytest.out = QUIET;
                System.setOut(QUIET);
                if (mm % 6 == 5) {
                    LongPlaytest.advise(city);
                    LongPlaytest.ensureSchools(city);
                }
                Map<CityMap.District, long[]> countsBefore = new java.util.HashMap<>();
                for (CityMap.District d : map.districts()) countsBefore.put(d, countsOf(d, types.length));
                LongPlaytest.run(city, 1);
                System.setOut(real);
                LongPlaytest.out = was;
                months++;
                CityMap now = city.getCityMap();
                if (now != map) {
                    afresh++;
                    map = now;
                    before = picturesOf(map);
                    continue;
                }
                // As a far view asks for them: every district's picture jobs, run and kept here.
                long k0 = map.picturesKept(), t0 = System.nanoTime();
                for (CityMap.District d : map.districts()) {
                    for (CityMap.Job j : map.pictureJobs(d)) {
                        j.run();
                        if (j instanceof CityMap.PlanJob pj) {
                            plans++;
                            if (pj.drewPicture()) picMs += pj.pictureMs();
                        }
                        map.adopt(j);
                    }
                }
                double ms = (System.nanoTime() - t0) / 1e6;
                monthMs += ms;
                worstMonthMs = Math.max(worstMonthMs, ms);
                long d0 = map.picturesKept() - k0;
                drawn += d0;
                int movedNow = 0;
                Map<CityMap.District, DistrictPicture> after = new java.util.HashMap<>();
                for (CityMap.District d : map.districts()) {
                    DistrictPicture p = map.pictureIfCurrent(d);
                    districtMonths++;
                    if (p == null) { missing++; continue; }
                    after.put(d, p);
                    DistrictPicture b = before.get(d);
                    if (b == null || b.stamp != p.stamp) movedNow++;
                    else {
                        unchanged++;
                        if (p == b) keptSame++;
                    }
                    long[] cb = countsBefore.get(d);
                    if (cb == null || !Arrays.equals(cb, countsOf(d, types.length))) countsMoved++;
                }
                moved += movedNow;
                if (d0 == movedNow) exactMonths++;
                before = after;
            }
        } finally {
            System.setOut(real);
            LongPlaytest.out = was;
        }
        out.printf("      the played city on %d months to month %d (%d drawn afresh): %,d district-months, %,d with counts moved; %,d plans drawn"
                        + " again, %,d pictures redrawn - their plots moved - and %,d kept, the same object; %.1f ms a month asking every"
                        + " picture (worst %.1f), the pictures themselves %.2f ms in all%n",
                months, city.getMonth(), afresh, districtMonths, countsMoved, plans, drawn, keptSame, monthMs / Math.max(1, months - afresh),
                worstMonthMs, picMs);
        check("every month redraws the pictures of the districts whose plots moved, and only those", months > afresh && exactMonths == months - afresh
                && moved > 0 && missing == 0);
        check("...and every district whose plots did not move keeps its picture, the same object", unchanged > 0 && keptSame == unchanged);

        // ----- The far node: a district PICTURE_FROM_PX pixels across samples its picture; smaller, it is dithered by its shares -----
        long at = World.DISTRICT / MapTiles.PICTURE_FROM_PX;
        x1.forgetPainted();
        MapTiles x1Tiles = new MapTiles(x1);
        long[] site = x1.site();
        long span = MapFrame.NODE_PX * at, nx = Math.floorDiv(site[0], span) * span, ny = Math.floorDiv(site[1], span) * span;
        byte[] ground = MapTiles.nodeTerrain(x1.worldKey(), nx, ny, at);
        int n = MapFrame.NODE_PX;
        int[] img = new int[n * n];
        int asked = x1Tiles.nodePixels(nx, ny, at, ground, img);
        List<CityMap.District> wantedFirst = new ArrayList<>(x1Tiles.wanted());
        for (CityMap.District d : wantedFirst) x1.picture(d);
        int askedAfter = x1Tiles.nodePixels(nx, ny, at, ground, img);
        int sampled = 0, sampledRight = 0;
        for (int k = 0; k < img.length; k++) {
            if (x1Tiles.lastEdge()[k] || x1Tiles.lastRun()[k]) continue;
            long x = nx + (k % n) * at + at / 2, y = ny + (k / n) * at + at / 2;
            CityMap.District d = x1.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
            if (d == null || d.usedCells() <= 0) continue;
            DistrictPicture pic = x1.pictureIfCurrent(d);
            if (pic == null) continue;
            int code = pic.codeAtPlot((int) (x - (x1.baseDX() + d.dx) * World.DISTRICT), (int) (y - (x1.baseDY() + d.dy) * World.DISTRICT));
            int c = TileRaster.GROUND[ground[k]];
            if (!x1.land().ownsPlot(x, y)) c = TileRaster.blend(c, TileRaster.VOID, TileRaster.UNOWNED_DIM);
            if (code != DistrictPicture.GROUND) c = DistrictPicture.colour(code);
            sampled++;
            if (img[k] == c) sampledRight++;
        }
        x1.forgetPainted();
        int askedSmall = x1Tiles.nodePixels(Math.floorDiv(site[0], 2 * span) * 2 * span, Math.floorDiv(site[1], 2 * span) * 2 * span, 2 * at,
                MapTiles.nodeTerrain(x1.worldKey(), Math.floorDiv(site[0], 2 * span) * 2 * span, Math.floorDiv(site[1], 2 * span) * 2 * span, 2 * at), img);
        out.printf("      Jerus's city x 1 at %d plots a pixel (a district %d pixels): %d district(s) asked for before their pictures, %d after;"
                + " %,d pixels sampled, %,d their picture's; at %d plots a pixel %d asked%n", at, MapTiles.PICTURE_FROM_PX, asked, askedAfter,
                sampled, sampledRight, 2 * at, askedSmall);
        check("a far node with districts PICTURE_FROM_PX of its pixels across asks for the pictures it lacks, and once drawn samples them"
                + " pixel for pixel", asked > 0 && askedAfter == 0 && sampled > 0 && sampledRight == sampled);
        check("...and with districts smaller asks for none", askedSmall == 0);

        // The dither: over x 10,000's owned dry ground at twice that, each kind's pixels within PICTURE_SIGMAS spreads of its shares'.
        MapTiles denseTiles = new MapTiles(dense);
        long far = 2 * at, fspan = MapFrame.NODE_PX * far;
        long fx0 = Math.floorDiv(sq.x, fspan) * fspan, fy0 = Math.floorDiv(sq.y, fspan) * fspan;
        byte[] fg = MapTiles.nodeTerrain(dense.worldKey(), fx0, fy0, far);
        denseTiles.nodePixels(fx0, fy0, far, fg, img);
        double[] expect = new double[DistrictPicture.CODES], var = new double[DistrictPicture.CODES];
        int[] seen = new int[DistrictPicture.CODES];
        Map<CityMap.District, double[]> shares = new java.util.HashMap<>();
        int dithered = 0;
        for (int k = 0; k < img.length; k++) {
            if (denseTiles.lastEdge()[k] || denseTiles.lastRun()[k]) continue;
            long x = fx0 + (k % n) * far + far / 2, y = fy0 + (k / n) * far + far / 2;
            if (!dense.land().ownsPlot(x, y) || !World.dry(fg[k])) continue;
            CityMap.District d = dense.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
            if (d == null || d.usedCells() <= 0) continue;
            double[] s = shares.computeIfAbsent(d, dd -> DistrictPicture.shares(dd, types));
            dithered++;
            for (int c = 1; c < DistrictPicture.CODES; c++) { expect[c] += s[c]; var[c] += s[c] * (1 - s[c]); }
            for (int c = 1; c < DistrictPicture.CODES; c++) if (img[k] == DistrictPicture.colour(c)) { seen[c]++; break; }
        }
        boolean ditherWithin = dithered > 0;
        double ditherZ = 0;
        for (int c = 1; c < DistrictPicture.CODES; c++) {
            if (expect[c] <= 0) continue;
            double sd = Math.sqrt(var[c]);
            ditherWithin &= Math.abs(seen[c] - expect[c]) <= PICTURE_SIGMAS * sd + 1;
            ditherZ = Math.max(ditherZ, sd > 0 ? Math.abs(seen[c] - expect[c]) / sd : 0);
        }
        out.printf("      x 10,000 at %d plots a pixel: %,d pixels of its owned dry ground dithered by %d districts' shares, the worst kind %.2f spreads"
                + " from them%n", far, dithered, shares.size(), ditherZ);
        check("a district under PICTURE_FROM_PX pixels is dithered by its shares: each kind's pixels within " + (int) PICTURE_SIGMAS
                + " spreads of them", ditherWithin);

        // ----- The cost: a picture, and a far node within SCREEN_MS -----
        dense.forgetPainted();
        long nspan = MapFrame.NODE_PX * 16L, n16x = Math.floorDiv(sq.x, nspan) * nspan, n16y = Math.floorDiv(sq.y, nspan) * nspan;
        byte[] g16 = MapTiles.nodeTerrain(dense.worldKey(), n16x, n16y, 16);
        denseTiles.nodePixels(n16x, n16y, 16, g16, img);
        double planMs = 0, pictureMs = 0, worstPicture = 0;
        int pictures = 0, planJobs = 0;
        for (CityMap.District d : new ArrayList<>(denseTiles.wanted())) {
            for (CityMap.Job j : dense.pictureJobs(d)) {
                j.run();
                if (j instanceof CityMap.PlanJob pj) {
                    planJobs++;
                    planMs += pj.ms();
                    if (pj.drewPicture()) {
                        pictures++;
                        pictureMs += pj.pictureMs();
                        worstPicture = Math.max(worstPicture, pj.pictureMs());
                    }
                }
                dense.adopt(j);
            }
        }
        double[] nodeMs = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
        long[] strides = { 16, at, far };
        for (int r = 0; r < WARM_ROUNDS + NODE_ROUNDS; r++) {
            for (int s = 0; s < strides.length; s++) {
                long sp = MapFrame.NODE_PX * strides[s], ox = Math.floorDiv(sq.x, sp) * sp, oy = Math.floorDiv(sq.y, sp) * sp;
                byte[] gg = s == 0 ? g16 : s == 2 ? fg : MapTiles.nodeTerrain(dense.worldKey(), ox, oy, strides[s]);
                long t0 = System.nanoTime();
                denseTiles.nodePixels(ox, oy, strides[s], gg, img);
                if (r >= WARM_ROUNDS) nodeMs[s] = Math.min(nodeMs[s], (System.nanoTime() - t0) / 1e6);
            }
        }
        int left16 = denseTiles.nodePixels(n16x, n16y, 16, g16, img);
        out.printf("      a picture: %d drawn with %d plans for x 10,000's node at 16 plots a pixel - the pictures %.2f ms each (worst %.2f), their"
                        + " plans %.1f ms each; %d left wanting%n", pictures, planJobs, pictureMs / Math.max(1, pictures), worstPicture,
                planMs / Math.max(1, planJobs), left16);
        out.printf("      a far node over x 10,000: %.1f ms at 16 plots a pixel (its pictures), %.1f at %d, %.1f at %d (its shares)%n",
                nodeMs[0], nodeMs[1], at, nodeMs[2], far);
        check("a far node over the city - its pictures sampled, or its shares dithered - drawn in no more than SCREEN_MS ("
                + (int) SCREEN_MS + " ms)", pictures > 0 && left16 == 0 && Timing.within(Math.max(nodeMs[0], Math.max(nodeMs[1], nodeMs[2])), SCREEN_MS));
        MapFrame whole = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H), small = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        LandGrid dg = dense.land().grid(), xg = x1.land().grid();
        whole.fit(dg.minX(), dg.minY(), dg.maxX(), dg.maxY());
        small.fit(xg.minX(), xg.minY(), xg.maxX(), xg.maxY());
        check("a far view of more built districts than the map keeps pictures of (PICTURES_KEPT) asks for none; of fewer, it asks",
                !denseTiles.picturesFit(whole) && x1Tiles.picturesFit(small));

        // ----- The runs and the edge at least a pixel wide at the widest view -----
        long widest = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H).nodePlots();
        Object[][] wide = { { "x 10,000", dense, new long[] { sq.x, sq.y } }, { "the played city", map, map.site() } };
        boolean runsExact = true, edgesExact = true, edgesBright = true, noneLost = true;
        int runPixels = 0, edgePixels = 0, playedEdge = 0;
        for (Object[] w : wide) {
            CityMap m = (CityMap) w[1];
            long[] at0 = (long[]) w[2];
            long wspan = MapFrame.NODE_PX * widest, wx = Math.floorDiv(at0[0], wspan) * wspan, wy = Math.floorDiv(at0[1], wspan) * wspan;
            MapTiles t = new MapTiles(m);
            int[] wi = new int[n * n];
            t.nodePixels(wx, wy, widest, MapTiles.nodeTerrain(m.worldKey(), wx, wy, widest), wi);
            boolean[] run = runPixelsOf(m, wx, wy, widest), edge = new boolean[n * n];
            LandGrid gr = m.land().grid();
            boolean[] cityPx = new boolean[(n + 2) * (n + 2)];
            long[] some = new long[(n + 2) * (n + 2)];
            for (int j = -1; j <= n; j++) for (int i = -1; i <= n; i++) {
                long ax = wx + i * widest, ay = wy + j * widest;
                cityPx[(j + 1) * (n + 2) + i + 1] = m.land().ownsPlot(ax + widest / 2, ay + widest / 2);
                some[(j + 1) * (n + 2) + i + 1] = gr.owned(ax, ay, ax + widest, ay + widest);
            }
            int ww = n + 2;
            for (int j = 0; j < n; j++) for (int i = 0; i < n; i++) {
                int a = (j + 1) * ww + i + 1, k = j * n + i;
                boolean nb8 = cityPx[a - 1] || cityPx[a + 1] || cityPx[a - ww] || cityPx[a + ww] || cityPx[a - ww - 1] || cityPx[a - ww + 1] || cityPx[a + ww - 1] || cityPx[a + ww + 1];
                edge[k] = cityPx[a] ? !cityPx[a - 1] || !cityPx[a + 1] || !cityPx[a - ww] || !cityPx[a + ww]
                        : some[a] > 0 && some[a] < widest * widest && !nb8;
                // No ground of the city lost: a pixel holding some of it is the city's, its edge, or beside a pixel of the city's.
                if (some[a] > 0 && !cityPx[a] && !edge[k] && !nb8) noneLost = false;
            }
            for (int k = 0; k < n * n; k++) {
                runsExact &= run[k] == t.lastRun()[k];
                edgesExact &= edge[k] == t.lastEdge()[k];
                if (t.lastEdge()[k]) {
                    int c = wi[k];
                    int least = (int) Math.floor(255 * MapTiles.EDGE_ALPHA);
                    edgesBright &= ((c >> 16) & 255) >= least && ((c >> 8) & 255) >= least && (c & 255) >= least;
                }
                if (run[k]) runPixels++;
                if (edge[k]) {
                    edgePixels++;
                    if (m == map) playedEdge++;
                }
            }
        }
        long playedPlots = Math.max(map.land().grid().maxX() - map.land().grid().minX(), map.land().grid().maxY() - map.land().grid().minY());
        out.printf("      at the widest view, %d plots a pixel: x 10,000's and the played city's nodes - %d pixels of highway and track, %d of"
                + " edge (the played city, %d plots across, %d)%n", widest, runPixels, edgePixels, playedPlots, playedEdge);
        check("at the widest view every pixel a highway or the track crosses is drawn as it, and no other", runPixels > 0 && runsExact);
        check("...the city's edge on every pixel of its own beside one that is not, and a pixel where it holds ground smaller than one",
                edgePixels > 0 && edgesExact && edgesBright && noneLost);
        check("...so a city smaller than a pixel there is still drawn a pixel", playedPlots < widest && playedEdge > 0);

        // ----- Not saved: the sidecar is the same bytes with the pictures and without -----
        byte[] withPictures = map.writeSidecar(city.getMonth());
        int held = map.picturesHeld();
        map.forgetPainted();
        byte[] without = map.writeSidecar(city.getMonth());
        out.printf("      the played city's sidecar: %,d bytes with its %d picture(s) kept and without; %d pictures kept at most, %,d bytes each%n",
                withPictures.length, held, CityMap.PICTURES_KEPT, DistrictPicture.BYTES);
        check("the pictures are not saved: the map's sidecar is the same bytes with them and without", held > 0 && Arrays.equals(withPictures, without));
    }

    /** A district's pictures, every one asked for: each district of the map to its picture (null where it has none). */
    static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m) {
        Map<CityMap.District, DistrictPicture> out = new java.util.HashMap<>();
        for (CityMap.District d : m.districts()) out.put(d, m.picture(d));
        return out;
    }

    /** A district's counts by type id. */
    static long[] countsOf(CityMap.District d, int types) {
        long[] c = new long[types];
        for (int t = 0; t < types; t++) c[t] = d.count(t);
        return c;
    }

    /** A picture's code's name, for the prints. */
    static String codeName(int c) {
        if (c >= DistrictPicture.FIRST_CLASS && c < DistrictPicture.FIRST_CLASS + BuildingVisual.CLASSES) return BuildingVisual.CLASS_NAMES[c - DistrictPicture.FIRST_CLASS];
        return c == DistrictPicture.FLATS ? "Flats" : c == DistrictPicture.CAMPUS ? "Refinery" : c == DistrictPicture.TERMINAL ? "Terminals"
                : c == DistrictPicture.TANKS ? "Tanks" : "code " + c;
    }

    /**
     * The plots a district's picture is drawn from, read here from its plan
     * as the painter reads it (CityMap.Drawn: its streets, its boxes by tile)
     * and its own plan's inputs (the runs over its frame, the mines on their
     * sites) and the boxes the runs and the shore stand on it - not from the
     * job's plan the picture was drawn from - each plot's code; and into
     * boxPlots, the plots its plan's boxes take by code.
     */
    static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots) {
        CityMap.Drawn dr = m.drawn(d);
        DistrictPlan.Input in = m.planInput(d);
        BuildingVisual.Type[] types = m.types();
        int n = World.DISTRICT, f = DistrictPlan.FRAME;
        byte[] src = new byte[n * n];
        byte mine = (byte) (DistrictPicture.FIRST_CLASS + BuildingVisual.MINE);
        for (int y = 0; y < n; y++) for (int x = 0; x < n; x++) {
            byte c = DistrictPicture.streetCode(dr.codeAt(x, y) & TilePainter.S_KIND);
            if (in.fixed[y * f + x] == DistrictPlan.FIXED_HIGHWAY) c = DistrictPicture.HIGHWAY;
            else if (in.fixed[y * f + x] == DistrictPlan.FIXED_RAIL) c = DistrictPicture.RAIL;
            if (in.site[y * f + x] == DistrictPlan.SITE_MINED) c = mine;
            src[y * n + x] = c;
        }
        int[] ex = m.extrasIn(d);
        for (int e = 0; e + 4 < ex.length; e += 5) fill(src, ex[e], ex[e + 1], ex[e + 2], ex[e + 3], DistrictPicture.codeOf(types[ex[e + 4]]), null);
        for (int k = 0; k < CityMap.TILES; k++) {
            if (dr.boxes[k] == null) continue;
            for (int b : dr.boxes[k]) {
                int x = (k % CityMap.TILES_A_SIDE) * World.TILE + (b & 31), y = (k / CityMap.TILES_A_SIDE) * World.TILE + ((b >> 5) & 31);
                int w = ((b >> 10) & 31) + 1, h = ((b >> 15) & 31) + 1, t = b >>> CityMap.BOX_TYPE_SHIFT;
                fill(src, x, y, w, h, DistrictPicture.codeOf(t < types.length ? types[t] : null), boxPlots);
            }
        }
        return src;
    }

    private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted) {
        int n = World.DISTRICT;
        for (int y = Math.max(0, y0); y < Math.min(n, y0 + h); y++) for (int x = Math.max(0, x0); x < Math.min(n, x0 + w); x++) src[y * n + x] = code;
        if (counted != null) counted[code] += w * h;
    }

    /** Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()). */
    static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots) {
        int n = MapFrame.NODE_PX;
        boolean[] out = new boolean[n * n];
        for (CityRuns.Net net : List.of(m.runs().highways(), m.runs().rail())) {
            for (long[] s : net.stretches()) {
                int h = (int) s[2], dx = CityRuns.DX[h], dy = CityRuns.DY[h];
                for (long k = 0; k <= s[3]; k++) {
                    long x = s[0] + dx * k, y = s[1] + dy * k;
                    markPixel(out, x, y, x0, y0, plots);
                    if (dx != 0 && dy != 0 && k >= 1) markPixel(out, x, y - dy, x0, y0, plots);
                }
            }
        }
        return out;
    }

    private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots) {
        int n = MapFrame.NODE_PX;
        long p = Math.floorDiv(x - x0, plots), q = Math.floorDiv(y - y0, plots);
        if (p >= 0 && q >= 0 && p < n && q < n) out[(int) (q * n + p)] = true;
    }

    /* =====================================================================
       12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3)
       ===================================================================== */

    /** How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. */
    static final int FAR_DISTRICTS = 4;

    /** Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; section 11's WARM_ROUNDS left a far node half compiled, 3.9 to 5.6 ms on x 10,000 from run to run). */
    static final int STAMP_WARM = 20, STAMP_TIMED = 10;

    static void miniMap(Game city, Squares sq) throws Exception {
        out.println("\n--- 12. the minimap (0.7.134, batch MM2): the stamp frames the built-up ground, from the pictures, in a few milliseconds ---");
        int W = MiniMapFrame.STAMP_W, H = MiniMapFrame.STAMP_H;

        // ----- The fixtures: one district, a few, a great many, and owned ground with nothing built on it -----
        PrintStream real = System.out;
        Game fresh;
        System.setOut(QUIET);
        try {
            fresh = new Game(GameFiles.scratch("mapcheck-mini"));
            fresh.newGame();
        } finally {
            System.setOut(real);
        }
        CityMap founded = fresh.getCityMap(), played = city.getCityMap(), x1 = sq.maps[0], dense = sq.maps[DENSE];
        // The played city's ground, its map drawn canonically on a copy; then a block of a district bought FAR_DISTRICTS out, and a month.
        CityLand copy = city.getCityLand().copy();
        long[] counts = city.getMapCounts();
        CityMap far = CityMap.canonical(copy, r -> copy.amountsInOrder(r), city.getMapTypes(), counts);
        MiniMapFrame.Box before = MiniMapFrame.frame(far);
        long D = World.DISTRICT, bx = (Math.floorDiv(before.x1(), D) + FAR_DISTRICTS) * D, by = Math.floorDiv(copy.siteY(), D) * D;
        int districtsBefore = far.districts().size();
        copy.extend(new LandParcel(-3, 1, 0, 8, bx, by, bx + D, by + D, null, null, null, 0, 0), city.getMonth(), 0);
        boolean reconciled = far.reconcile(counts);
        MiniMapFrame.Box after = MiniMapFrame.frame(far);

        Object[][] fixtures = { { "a new city as founded", founded }, { "the played city", played }, { "Jerus's city x 1", x1 },
                { "x 10,000", dense }, { "the played city with its far block", far } };
        boolean byLeaves = true, margins = true, least = true, holds = true, half = true;
        // Half the stamp but for the margin: at a stride s of 2 or more the frame is over (s - 1) of the stamp's side, so at least half
        // of s, and the margin of the built-up ground's longer side, on the stamp's shorter, is up to W / H of it - and a plot each way.
        double share = 0.5 / (1 + 2 * MiniMapFrame.MARGIN * W / H) - 1.0 / H;
        for (Object[] fx : fixtures) {
            CityMap m = (CityMap) fx[1];
            int built = 0;
            for (CityMap.District d : m.districts()) if (d.usedCells() > 0) built++;
            MiniMapFrame.Box bu = MiniMapFrame.builtUp(m), fr = MiniMapFrame.frame(m), leaves = builtUpByLeaves(m);
            byLeaves &= bu != null && bu.equals(leaves);
            MiniMapFrame.Box with = MiniMapFrame.union(bu, MiniMapFrame.centre(m.land()));
            long mg = (long) Math.ceil(MiniMapFrame.MARGIN * Math.max(with.width(), with.height()));
            margins &= fr.equals(new MiniMapFrame.Box(with.x0() - mg, with.y0() - mg, with.x1() + mg, with.y1() + mg));
            MiniMapFrame.View st = MiniMapFrame.stamp(fr), pv = MiniMapFrame.preview(fr);
            for (MiniMapFrame.View v : List.of(st, pv)) {
                long s = v.stride();
                // The least: one plot a pixel fewer and no view on its multiples holds the frame.
                least &= s >= 1 && (s == 1 || MiniMapFrame.corner(fr.x0(), fr.x1(), v.w(), s - 1) == MiniMapFrame.NO_CORNER
                        || MiniMapFrame.corner(fr.y0(), fr.y1(), v.h(), s - 1) == MiniMapFrame.NO_CORNER);
                holds &= v.shown().holds(fr) && Math.floorMod(v.x0(), s) == 0 && Math.floorMod(v.y0(), s) == 0;
            }
            double across = Math.max(with.width() / (double) (st.stride() * W), with.height() / (double) (st.stride() * H));
            half &= st.stride() == 1 || across >= share;
            LandGrid g = m.land().grid();
            MiniMapFrame.Box ce = MiniMapFrame.centre(m.land());
            out.printf("      %s: %,d district(s), %,d built; owned %,d x %,d plots, built-up %,d x %,d, its centre %,d x %,d, framed %,d x %,d; the stamp"
                            + " at %,d plots a pixel (%.0f m), the city %.0f x %.0f px of it; the preview at %,d%n", fx[0], m.districts().size(), built,
                    g.maxX() - g.minX(), g.maxY() - g.minY(), bu.width(), bu.height(), ce == null ? 0 : ce.width(), ce == null ? 0 : ce.height(),
                    fr.width(), fr.height(), st.stride(), st.stride() * World.PLOT_M,
                    with.width() / (double) st.stride(), with.height() / (double) st.stride(), pv.stride());
        }
        check("the stamp's built-up ground is the ground owned in each district with buildings - found again from the grid's leaves", byLeaves);
        check("...framed with the city's centre and MARGIN of their longer side round them each way", margins);
        MiniMapFrame.Box fb = MiniMapFrame.builtUp(founded), ff = MiniMapFrame.frame(founded), fc = MiniMapFrame.centre(founded.land());
        int foundedBuilt = 0;
        for (CityMap.District d : founded.districts()) if (d.usedCells() > 0) foundedBuilt++;
        check("a city of one district frames its founding ground whole, smaller than its district, not the district's square",
                foundedBuilt == 1 && fb != null && fc != null && ff.holds(fc) && ff.width() < World.DISTRICT && ff.height() < World.DISTRICT);
        check("every view at the least whole number of plots a pixel whose multiples hold the frame, its corner on one, holding it whole", least && holds);
        check("...so the city - its built-up ground and centre - is at least half the stamp across but for the margin, or drawn a plot a pixel", half);
        // Owned ground with nothing built on it: the far block, a month after it was bought.
        LandGrid cg = copy.grid();
        MiniMapFrame.Box xf = MiniMapFrame.frame(x1), cg1 = new MiniMapFrame.Box(cg.minX(), cg.minY(), cg.maxX(), cg.maxY());
        CityMap.District block = far.district((int) (Math.floorDiv(bx, D) - Math.floorDiv(copy.siteX(), D)), (int) (Math.floorDiv(by, D) - Math.floorDiv(copy.siteY(), D)));
        out.printf("      the far block bought %,d plots east of the frame, a district of its own (%d districts -> %d), nothing built on it (%s);"
                        + " the owned ground now %,d x %,d plots, the frame %s%n", bx - before.x1(), districtsBefore, far.districts().size(),
                block == null ? "no district" : block.usedCells() + " plots built", cg1.width(), cg1.height(),
                after.equals(before) ? "as before it, " + after.width() + " x " + after.height() : "moved, " + after);
        check("fixture: the far block is a district of the played city's own, nothing built on it, its owned ground past the frame",
                reconciled && block != null && block.usedCells() == 0 && !after.holds(cg1));
        check("owned ground with nothing built on it is left out: the far block's city framed as before it", after.equals(before));

        // ----- From the pictures and the dither, no plan drawn on the screen's thread -----
        x1.forgetPainted();
        MiniMapFrame.View v1 = MiniMapFrame.stamp(xf);
        byte[][] g1 = MiniMapFrame.ground(x1.worldKey(), v1);
        MapTiles t1 = new MapTiles(x1);
        int[] px = new int[W * H];
        java.util.Set<CityMap.District> wanted = new java.util.LinkedHashSet<>();
        long kept0 = x1.picturesKept(), plans0 = x1.plansBytes();
        MiniMapFrame.pixels(t1, v1, g1, px, wanted);
        boolean noPlan = x1.picturesKept() == kept0 && x1.plansBytes() == plans0 && x1.picturesHeld() == 0;
        java.util.Set<CityMap.District> builtInView = new java.util.LinkedHashSet<>();
        MiniMapFrame.Box shown = v1.shown();
        for (CityMap.District d : x1.districts()) {
            if (d.usedCells() <= 0) continue;
            noPlan &= x1.pictureIfCurrent(d) == null;
            MiniMapFrame.Box b = MiniMapFrame.districtBox(x1, d);
            if (b.x1() > shown.x0() && b.x0() < shown.x1() && b.y1() > shown.y0() && b.y0() < shown.y1()) builtInView.add(d);
        }
        boolean asksThem = v1.stride() * MapTiles.PICTURE_FROM_PX <= World.DISTRICT && wanted.equals(builtInView);
        // Their pictures drawn as the map's worker draws them, then the stamp again: each pixel its district's picture's at its plot.
        for (CityMap.District d : wanted) for (CityMap.Job j : x1.pictureJobs(d)) { j.run(); x1.adopt(j); }
        java.util.Set<CityMap.District> after1 = new java.util.LinkedHashSet<>();
        MiniMapFrame.pixels(t1, v1, g1, px, after1);
        int n = MapFrame.NODE_PX, sampled = 0, right = 0;
        for (int k = 0; k < W * H; k++) {
            int i = k % W, j = k / W;
            if (t1.lastEdge()[j * n + i] || t1.lastRun()[j * n + i]) continue;
            long s1 = v1.stride(), ax = v1.x0() + i * s1, ay = v1.y0() + j * s1, x = ax + s1 / 2, y = ay + s1 / 2;
            CityMap.District d = x1.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
            // A pixel holding none of the city's ground is the world's, dimmed; one holding some is its district's.
            if (d == null || d.usedCells() <= 0 || x1.land().grid().owned(ax, ay, ax + s1, ay + s1) == 0) continue;
            DistrictPicture pic = x1.pictureIfCurrent(d);
            if (pic == null) continue;
            MiniMapFrame.Box b = MiniMapFrame.districtBox(x1, d);
            int code = pic.codeAtPlot((int) (x - b.x0()), (int) (y - b.y0()));
            int c = TileRaster.GROUND[g1[0][j * n + i]];
            if (!x1.land().ownsPlot(x, y)) c = TileRaster.blend(c, TileRaster.VOID, TileRaster.UNOWNED_DIM);
            if (code != DistrictPicture.GROUND) c = DistrictPicture.colour(code);
            sampled++;
            if (px[k] == c) right++;
        }
        // x 10,000's stamp: districts a pixel or two, dithered by their shares - none asked.
        MiniMapFrame.Box df = MiniMapFrame.frame(dense);
        MiniMapFrame.View vd = MiniMapFrame.stamp(df);
        byte[][] gd = MiniMapFrame.ground(dense.worldKey(), vd);
        MapTiles td = new MapTiles(dense);
        java.util.Set<CityMap.District> wantedDense = new java.util.LinkedHashSet<>();
        long keptD = dense.picturesKept(), plansD = dense.plansBytes();
        MiniMapFrame.pixels(td, vd, gd, new int[W * H], wantedDense);
        out.printf("      x 1's stamp at %d plots a pixel, no plan drawn: %d district(s) asked for, of %d built in view; their pictures drawn aside,"
                        + " %,d pixels sampled, %,d their picture's; x 10,000's at %,d plots a pixel asks %d%n", v1.stride(), wanted.size(), builtInView.size(),
                sampled, right, vd.stride(), wantedDense.size());
        check("the stamp draws no plan on the screen's thread: none held, no picture kept; it asks for the pictures of the built districts it shows",
                noPlan && asksThem && !wanted.isEmpty());
        check("...and once they are drawn aside it samples them, pixel for pixel, asking for none", after1.isEmpty() && sampled > 0 && right == sampled);
        check("...and at a stride where a district is under PICTURE_FROM_PX pixels it dithers, asking for none and drawing nothing",
                vd.stride() * MapTiles.PICTURE_FROM_PX > World.DISTRICT && wantedDense.isEmpty() && dense.picturesKept() == keptD && dense.plansBytes() == plansD);

        // ----- The cost: the stamp - its frame and its node - on the screen's thread at a month's end, on its CPU (A28) -----
        CityMap earth = earthX1(sq);
        Object[][] timed = { { "a new city", founded }, { "the played city", played }, { "x 1", x1 }, { "x 10,000", dense }, { "x 1 on Earth", earth } };
        double worst = 0, worstPreview = 0;
        String worstAt = "";
        boolean noVoid = true;
        StringBuilder costs = new StringBuilder();
        for (Object[] fx : timed) {
            CityMap m = (CityMap) fx[1];
            if (m == null) continue;
            MapTiles t = new MapTiles(m);
            MiniMapFrame.View v = MiniMapFrame.stamp(MiniMapFrame.frame(m)), pv = MiniMapFrame.preview(MiniMapFrame.frame(m));
            byte[][] g = MiniMapFrame.ground(m.worldKey(), v), gp = MiniMapFrame.ground(m.worldKey(), pv);
            int[] out1 = new int[W * H], outP = new int[pv.w() * pv.h()], buf = new int[n * n];
            // As the screen draws it once its pictures are in: those it lacks drawn first.
            java.util.Set<CityMap.District> lack = new java.util.LinkedHashSet<>();
            MiniMapFrame.pixels(t, v, g, out1, lack);
            if (t.picturesFit(v.asMapFrame())) for (CityMap.District d : lack) for (CityMap.Job j : m.pictureJobs(d)) { j.run(); m.adopt(j); }
            double best = Double.MAX_VALUE, bestWall = Double.MAX_VALUE, bestNode = Double.MAX_VALUE;
            for (int r = 0; r < STAMP_WARM + STAMP_TIMED; r++) {
                long c0 = monthClock(), w0 = System.nanoTime();
                MiniMapFrame.View vv = MiniMapFrame.stamp(MiniMapFrame.frame(m));
                MiniMapFrame.pixels(t, vv, g, out1, null);
                long c1 = monthClock(), w1 = System.nanoTime();
                double nodeMost = 0;
                for (int k = 0; k < pv.nodesAcross() * pv.nodesDown(); k++) {
                    long p0 = monthClock();
                    MiniMapFrame.node(t, pv, k, gp[k], buf, outP, null);
                    nodeMost = Math.max(nodeMost, (monthClock() - p0) / 1e6);
                }
                if (r >= STAMP_WARM) {
                    best = Math.min(best, (c1 - c0) / 1e6);
                    bestWall = Math.min(bestWall, (w1 - w0) / 1e6);
                    bestNode = Math.min(bestNode, nodeMost);
                }
            }
            if (m == earth) for (int c : out1) noVoid &= c != TileRaster.VOID;
            if (best > worst) { worst = best; worstAt = (String) fx[0]; }
            worstPreview = Math.max(worstPreview, bestNode);
            costs.append(String.format(" %s %.2f (%.2f wall), its preview's slowest node %.2f;", fx[0], best, bestWall, bestNode));
        }
        out.printf("      the stamp, ms of the thread's %s:%s the worst %s%n", CPU_TIMED ? "CPU" : "wall clock (no fine thread CPU here)", costs, worstAt);
        check("fixture: the Earth's fixture is beside the classes, and x 1 stands on it", earth != null);
        check("the stamp - its frame and its node - drawn in no more than MiniMapFrame.STAMP_MS (" + (int) MiniMapFrame.STAMP_MS + " ms) of the "
                + (CPU_TIMED ? "thread's CPU" : "wall clock"), worst > 0 && Timing.within(worst, MiniMapFrame.STAMP_MS));
        check("...on Earth the city's plane round it: no pixel the void past the planet's rim", earth != null && noVoid);

        // ----- A click: the expanded map on the frame, where the land office's opening takes in the far block -----
        MapFrame big = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H), office = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        MiniMapFrame.open(big, after);
        LandMap.open(office, copy, null);
        boolean onFrame = big.plotX(0) <= after.x0() && big.plotY(0) <= after.y0() && big.plotX(big.width()) >= after.x1()
                && big.plotY(big.height()) >= after.y1() && Math.abs(big.centreX() - after.centreX()) < 1e-6 && Math.abs(big.centreY() - after.centreY()) < 1e-6;
        out.printf("      a click on the far block's city: the expanded map at %.1f m a pixel on its frame; the land office's opening at %.1f m%n",
                World.PLOT_M / big.scale(), World.PLOT_M / office.scale());
        check("a click opens the expanded map on the stamp's frame, centred and whole, where the land office's opening shrinks it for the far block",
                onFrame && big.scale() > office.scale());
    }

    /**
     * The built-up ground found another way, for the check: every leaf of
     * the grid cut to each district with buildings it lies in, their bounds;
     * a built district with none owned its whole square.
     */
    static MiniMapFrame.Box builtUpByLeaves(CityMap m) {
        java.util.Set<Long> built = new java.util.HashSet<>();
        for (CityMap.District d : m.districts()) if (d.usedCells() > 0) built.add(((long) d.dx << 32) ^ (d.dy & 0xffffffffL));
        if (built.isEmpty()) return null;
        long[] b = { Long.MAX_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE };
        java.util.Set<Long> owning = new java.util.HashSet<>();
        long bdx = m.baseDX(), bdy = m.baseDY(), D = World.DISTRICT;
        m.land().grid().leaves((level, x, y, holding) -> {
            long s = 1L << level;
            for (long dy = Math.floorDiv(y, D) - bdy; dy <= Math.floorDiv(y + s - 1, D) - bdy; dy++) {
                for (long dx = Math.floorDiv(x, D) - bdx; dx <= Math.floorDiv(x + s - 1, D) - bdx; dx++) {
                    long key = (dx << 32) ^ (dy & 0xffffffffL);
                    if (!built.contains(key)) continue;
                    owning.add(key);
                    long ax = (bdx + dx) * D, ay = (bdy + dy) * D;
                    b[0] = Math.min(b[0], Math.max(x, ax));
                    b[1] = Math.min(b[1], Math.max(y, ay));
                    b[2] = Math.max(b[2], Math.min(x + s, ax + D));
                    b[3] = Math.max(b[3], Math.min(y + s, ay + D));
                }
            }
        });
        for (long key : built) {
            if (owning.contains(key)) continue;
            long ax = (bdx + (key >> 32)) * D, ay = (bdy + (int) key) * D;
            b[0] = Math.min(b[0], ax);
            b[1] = Math.min(b[1], ay);
            b[2] = Math.max(b[2], ax + D);
            b[3] = Math.max(b[3], ay + D);
        }
        return new MiniMapFrame.Box(b[0], b[1], b[2], b[3]);
    }

    /* =====================================================================
       13. THE GLOBE ON SCREEN (0.7.135, batch E9)
       ===================================================================== */

    /** Frames of the keys' second: 60, the pump's (a sixtieth each). */
    static final int KEY_FRAMES = 60;

    /** How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. */
    static final double KEY_SLOP_PX = 1e-3;

    /** The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). */
    static final double MATRIX_PLOTS = 1e-6;

    static void globeOnScreen(Squares sq) throws Exception {
        out.println("\n--- 13. the globe on screen: the keys, the hover at the handoff, the legend by tier, the plane's nodes through the camera, the founding globe ---");
        int w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H;
        double frame = 1.0 / KEY_FRAMES;

        // The keys on the plane: each way, two at once, opposite ones, at every zoom a second of them crosses within the world.
        boolean[][] sets = { { true, false, false, false }, { false, true, false, false }, { false, false, true, false }, { false, false, false, true },
                { true, false, false, true }, { false, true, true, false }, { true, true, true, true } };
        double[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }, { -Math.sqrt(0.5), Math.sqrt(0.5) }, { Math.sqrt(0.5), -Math.sqrt(0.5) }, { 0, 0 } };
        boolean plane = true;
        double worst = 0;
        double least = 2 * MapFrame.KEY_PX_A_SECOND / World.SIDE;
        for (double s : new double[] { least, 0.01, 0.1, 1, MapFrame.L0_FROM, MapFrame.MOST_PX_A_PLOT }) {
            for (int k = 0; k < sets.length; k++) {
                MapFrame f = new MapFrame(w, h);
                f.set(World.SIDE / 2.0, World.SIDE / 2.0, s);
                double x0 = f.centreX(), y0 = f.centreY();
                double[] v = MapFrame.keyVelocity(sets[k][0], sets[k][1], sets[k][2], sets[k][3]);
                for (int i = 0; i < KEY_FRAMES; i++) f.keyPan(v, frame);
                double dx = f.screenX(x0) - w / 2.0, dy = f.screenY(y0) - h / 2.0;
                double miss = Math.hypot(dx - dirs[k][0] * MapFrame.KEY_PX_A_SECOND, dy - dirs[k][1] * MapFrame.KEY_PX_A_SECOND);
                worst = Math.max(worst, miss);
                plane &= miss < KEY_SLOP_PX;
            }
        }
        MapFrame held = new MapFrame(w, h);
        held.set(World.SIDE / 2.0, World.SIDE / 2.0, 1);
        double hx = held.centreX();
        held.keyPan(MapFrame.keyVelocity(false, false, false, true), 0.5);
        boolean heldUp = Math.abs((held.centreX() - hx) * held.scale() - MapFrame.KEY_PX_A_SECOND * MapFrame.KEY_STEP_MOST_S) < 1e-9;
        MapFrame edge = new MapFrame(w, h);
        edge.set(World.SIDE - 10, World.SIDE / 2.0, 1);
        for (int i = 0; i < KEY_FRAMES; i++) edge.keyPan(MapFrame.keyVelocity(false, false, false, true), frame);
        out.printf("      the plane: a second of each key, two at once and all four, at %d scales from %.4f to %.0f px a plot: %.2g px from the dial's %.0f at worst%n",
                6, least, MapFrame.MOST_PX_A_PLOT, worst, MapFrame.KEY_PX_A_SECOND);
        check("W, A, S and D held a second move the plane's ground KEY_PX_A_SECOND px, the way each looks, at every zoom", plane);
        check("...two at once diagonally at that speed, opposite keys cancelling; a frame held up moves KEY_STEP_MOST_S's worth; the square stops it",
                heldUp && edge.centreX() == World.SIDE);

        // The fixture's Earth, and Jerus's city x 1 on it at Rotterdam.
        CityMap x1 = earthX1(sq);
        check("EarthCheck's fixture is beside the classes, for the globe", x1 != null);
        if (x1 == null) return;
        CityLand land = x1.land();
        World.Key key = land.key();
        World world = World.of(key);
        EarthTerrain e = world.earthTerrain();

        // The keys on the globe: a second of them moves the ground under the view's middle the dial's pixels in every tier - from
        // the equator wide, so that no second runs into the pole's clamp (POLE_LAT), and on the plane from the city, its reach.
        boolean globeKeys = true;
        double globeWorst = 0;
        GlobeFrame g = new GlobeFrame(w, h, key.lat(), key.lon());
        g.setReach(GlobeFrame.reachKm(land, null));
        StringBuilder tiers = new StringBuilder();
        double wideKm = (GlobeFrame.POLE_LAT - 1) * EarthTerrain.M_A_DEGREE / 1000 / MapFrame.KEY_PX_A_SECOND;
        for (double km : new double[] { wideKm, 2, GlobeFrame.HANDOFF_KM, 0.1 }) {
            for (int k = 0; k < sets.length - 1; k++) {
                g.set(GlobeFrame.tierOf(km) == GlobeFrame.PLANE ? key.lat() : 0, key.lon(), km);
                double moved = 0;
                double[] v = MapFrame.keyVelocity(sets[k][0], sets[k][1], sets[k][2], sets[k][3]);
                for (int i = 0; i < KEY_FRAMES; i++) {
                    double la = g.centreLat(), lo = g.centreLon();
                    g.keyPan(v, frame);
                    double[] p = g.at(la, lo);
                    moved += p == null ? Double.NaN : Math.hypot(p[0] - w / 2.0, p[1] - h / 2.0);
                }
                globeWorst = Math.max(globeWorst, Math.abs(moved - MapFrame.KEY_PX_A_SECOND));
                globeKeys &= Math.abs(moved - MapFrame.KEY_PX_A_SECOND) < KEY_SLOP_PX;
            }
            tiers.append(String.format(" %.2f km %s;", km, new String[] { "PICTURE", "GROUND", "PLANE" }[GlobeFrame.tierOf(km)]));
        }
        out.printf("      the globe:%s the ground under the middle %.2g px from the dial at worst%n", tiers, globeWorst);
        check("...and turn the globe as much under its middle, in every tier", globeKeys);
        g.set(key.lat(), key.lon(), 0.1);
        for (int i = 0; i < 40 * KEY_FRAMES; i++) g.keyPan(MapFrame.keyVelocity(false, false, false, true), frame);
        double stopped = g.cityKm(g.centreLat(), g.centreLon());
        out.printf("      D held forty seconds at 0.1 km a pixel: stopped %.3f km from the city, its reach %.1f km%n", stopped, g.reach());
        check("...and on the plane D held stops at the reach, the view still the plane's", g.tier() == GlobeFrame.PLANE && stopped <= g.reach() && stopped > g.reach() - 1);

        // The legend by tier.
        List<LandMap.Entry> pic = LandMap.legend(GlobeFrame.PICTURE), gro = LandMap.legend(GlobeFrame.GROUND), pla = LandMap.legend(GlobeFrame.PLANE);
        boolean kinds = pic.size() == World.CLASSES + 2 && gro.size() == World.CLASSES + 3;
        java.util.Set<String> ground = new java.util.HashSet<>();
        for (byte c = 0; c < World.CLASSES; c++) ground.add(LandMap.groundWords(c));
        for (int i = 0; kinds && i < World.CLASSES; i++) {
            LandMap.Entry en = pic.get(i);
            kinds = ground.remove(en.words()) && en.colour() == TileRaster.GROUND[groundClass(en.words())] && gro.get(i).equals(en);
        }
        kinds &= ground.isEmpty() && pic.get(World.CLASSES).words().equals(LandMap.CITY_RING_WORDS) && pic.get(World.CLASSES).colour() == LandMap.RING
                && pic.get(World.CLASSES + 1).words().equals(LandMap.EDGE_WORDS) && gro.subList(0, World.CLASSES + 2).equals(pic)
                && gro.get(World.CLASSES + 2).words().equals(LandMap.OFFER_WORDS);
        long fieldKinds = Arrays.stream(Resource.values()).filter(Resource::inFields).count();
        // The roads' five, and since 0.8.1 (batch FR0, spec-freight 4.9) the gateway, the main line, the freight yard and the level crossing;
        // since 0.8.2 (batch FR1) the train.
        boolean planeLegend = pla.equals(LandMap.planeLegend()) && pla.size() == BuildingVisual.CLASSES + 1 + 5 + 4 + 9 + 1
                + (BuildingVisual.GROUPS - BuildingVisual.STEEL) + (BuildingVisual.STAND - BuildingVisual.PIT + 1) + 2 + 2 + fieldKinds
                && pla.get(0).words().equals(BuildingVisual.CLASS_NAMES[0]);
        out.printf("      the legend: %d entries on the PICTURE tier, %d on the GROUND, %d on the plane%n", pic.size(), gro.size(), pla.size());
        check("the legend on the globe is the seven kinds of ground, the city's ring and its edge, the GROUND tier's its offers too", kinds);
        check("...and from the handoff in, the plane's whole legend, as before", planeLegend);

        // The hover at the handoff: the same words for the same point, wherever the plane draws.
        double[] c = LandMap.centre(land);
        GlobeFrame at = new GlobeFrame(w, h, key.lat(), key.lon());
        at.setReach(GlobeFrame.reachKm(land, null));
        at.fromPlane((c[0] + c[2]) / 2, (c[1] + c[3]) / 2, 1.0 / GlobeFrame.HANDOFF_PLOTS);
        int asked = 0, same = 0, own = 0, planet = 0;
        double[] xy = new double[2];
        for (double sy = 0.5; sy < h; sy += 23) {
            for (double sx = 0.5; sx < w; sx += 29) {
                double[] ll = at.latLonAt(sx, sy);
                if (ll == null) continue;
                planet++;
                if (!at.plane().plotOf(ll[0], ll[1], xy)) continue;
                long x = (long) Math.floor(xy[0]), y = (long) Math.floor(xy[1]);
                asked++;
                List<String> onPlane = LandMap.hoverWords(land, null, x, y, LandMap.CARD_FIELDS);
                List<String> onGlobe = LandMap.globeHoverWords(at, land, null, sx, sy, World.SALT, LandMap.CARD_FIELDS);
                if (onPlane.equals(onGlobe)) same++;
                if (LandMap.pick(land, null, x, y).owner() != LandMap.OUTSIDE) own++;
            }
        }
        out.printf("      at the handoff over the city: %,d points on the planet, %,d asked of both, %,d alike, %,d of them the city's own ground%n", planet, asked, same, own);
        check("the hover's words on the globe at the handoff are the plane's for the same point, wherever the plane draws", at.tier() == GlobeFrame.GROUND
                && asked == planet && same == asked && own > 0);
        double[] far = GlobeFrame.destination(key.lat(), key.lon(), 135, 2000);
        GlobeFrame away = new GlobeFrame(w, h, key.lat(), key.lon());
        away.set(far[0], far[1], 0.1);
        int beyond = 0, beyondRight = 0;
        for (double sy = 0.5; sy < h; sy += 101) {
            for (double sx = 0.5; sx < w; sx += 131) {
                double[] ll = away.latLonAt(sx, sy);
                if (ll == null) continue;
                beyond++;
                List<String> words = LandMap.globeHoverWords(away, land, null, sx, sy, World.DESERT, LandMap.CARD_FIELDS);
                List<String> want = new ArrayList<>(List.of(LandMap.groundWords(World.DESERT), LandMap.NOT_THE_CITYS));
                want.addAll(LandMap.earthWords(e, ll[0], ll[1]));
                if (words.equals(want)) beyondRight++;
            }
        }
        boolean byPlot = true;
        for (int k = 0; k < 50; k++) {
            long x = EarthTerrain.spotPlot() + (k - 25) * 997L, y = EarthTerrain.spotPlot() + (k % 7 - 3) * 1931L;
            double[] ll = new double[2];
            e.projection().latLonOfPlot(x + 0.5, y + 0.5, ll);
            byPlot &= LandMap.earthWords(land, x, y).equals(LandMap.earthWords(e, ll[0], ll[1]));
        }
        GlobeFrame whole = new GlobeFrame(w, h, key.lat(), key.lon());
        out.printf("      2,000 km south-east, the wheel's stop: %d points, %d said as past the plane%n", beyond, beyondRight);
        check("...past it the ground drawn there, \"Not the city's\", and the place and the province by latitude and longitude", away.tier() == GlobeFrame.GROUND
                && beyond > 0 && beyondRight == beyond);
        check("...which are the plot's own at its centre; and off the planet it says nothing", byPlot
                && LandMap.globeHoverWords(whole, land, null, 1, 1, World.SALT, LandMap.CARD_FIELDS).isEmpty() && whole.latLonAt(1, 1) == null);

        // The composed matrix, and the GROUND tier near the city from the plane's own nodes.
        java.util.Random r = new java.util.Random(13);
        double matrix = 0;
        for (int t = 0; t < 60; t++) {
            GlobeFrame v = new GlobeFrame(w, h, key.lat(), key.lon());
            double[] d = GlobeFrame.destination(key.lat(), key.lon(), 360 * r.nextDouble(), 3000 * r.nextDouble());
            v.set(d[0], d[1], GlobeFrame.HANDOFF_KM * Math.pow(GlobeFrame.PICTURE_FROM_KM * 4 / GlobeFrame.HANDOFF_KM, r.nextDouble()));
            double[] a = new double[2], b = new double[2];
            for (int k = 0; k < 100; k++) {
                double sx = w * r.nextDouble(), sy = h * r.nextDouble();
                if (v.planeAt(sx, sy, a) != v.plotAt(sx, sy, b)) matrix = Double.MAX_VALUE;
                else if (v.plotAt(sx, sy, b)) matrix = Math.max(matrix, Math.hypot(a[0] - b[0], a[1] - b[1]));
            }
        }
        check("the composed matrix finds the plot under a pixel within MATRIX_PLOTS of the camera's and the plane's projections", matrix < MATRIX_PLOTS);
        MapTiles tiles = new MapTiles(x1);
        for (double[] view : new double[][] { { 384, 256, GlobeFrame.HANDOFF_KM }, { w, h, 2 } }) {
            GlobeFrame v = new GlobeFrame(view[0], view[1], key.lat(), key.lon());
            v.setReach(GlobeFrame.reachKm(land, null));
            v.set(at.centreLat(), at.centreLon(), view[2]);
            long plots = MapFrame.nodePlotsAt(v.planeScale());
            long span = MapFrame.NODE_PX * plots;
            Map<Long, int[]> nodes = new java.util.HashMap<>();
            int vw = (int) view[0], vh = (int) view[1];
            int[] px = new int[vw * vh];
            int missed = v.ground(plots, (nx, ny) -> {
                long nk = (nx << 32) ^ (ny & 0xffffffffL);
                int[] got = nodes.get(nk);
                if (got == null && !nodes.containsKey(nk)) {
                    byte[] gr = MapTiles.nodeTerrain(key, nx * span, ny * span, plots);
                    got = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
                    tiles.nodePixels(nx * span, ny * span, plots, gr, got);
                    nodes.put(nk, got);
                }
                return got;
            }, (i, j, vx, vy, vz) -> 1, 2, px, vw, 0, vw, 0, vh);
            int drawn = 0, alike = 0, edgeOnly = 0, onPlane = 0;
            for (int j = 0; j < vh; j++) {
                for (int i = 0; i < vw; i++) {
                    if (!v.drawsHold(i + 0.5, j + 0.5)) continue;
                    onPlane++;
                    if (!v.plotAt(i + 0.5, j + 0.5, xy)) continue;
                    drawn++;
                    long nx = Math.floorDiv((long) Math.floor(xy[0]), span), ny = Math.floorDiv((long) Math.floor(xy[1]), span);
                    int[] node = nodes.get((nx << 32) ^ (ny & 0xffffffffL));
                    if (node == null) continue;
                    int u = (int) ((xy[0] - nx * span) / plots), q = (int) ((xy[1] - ny * span) / plots);
                    if (px[j * vw + i] == node[Math.min(MapFrame.NODE_PX - 1, q) * MapFrame.NODE_PX + Math.min(MapFrame.NODE_PX - 1, u)]) alike++;
                    else {
                        double fx = xy[0] / plots, fy = xy[1] / plots;
                        if (Math.abs(fx - Math.rint(fx)) * plots < MATRIX_PLOTS || Math.abs(fy - Math.rint(fy)) * plots < MATRIX_PLOTS) edgeOnly++;
                    }
                }
            }
            out.printf("      %.0f x %.0f at %.2f km a pixel (%d plots a node pixel, %d nodes): %,d pixels where the plane draws, %,d alike its node's,"
                    + " %d on a node pixel's edge; %d without a node%n", view[0], view[1], view[2], plots, nodes.size(), drawn, alike, edgeOnly, missed);
            check("the GROUND tier at " + view[2] + " km a pixel is the plane's own far nodes, pixel for pixel through the camera",
                    drawn > 0 && drawn == onPlane && alike + edgeOnly == drawn && missed == 0);
        }

        // The last picture carried: a point's pixel in an older view is where that view puts it.
        GlobeFrame old = new GlobeFrame(w, h, key.lat(), key.lon());
        old.set(at.centreLat(), at.centreLon(), 3);
        GlobeFrame now = new GlobeFrame(w, h, key.lat(), key.lon());
        now.set(at.centreLat() + 1, at.centreLon() - 2, 2.5);
        GlobeFrame.Carry carry = now.carryFrom(old);
        int carried = 0, carriedRight = 0;
        double[] sp = new double[2];
        for (int k = 0; k < 2000; k++) {
            double sx = w * r.nextDouble(), sy = h * r.nextDouble();
            double[] ll = now.latLonAt(sx, sy);
            if (ll == null) continue;
            double[] o = old.at(ll[0], ll[1]);
            // The unit vector Beyond is handed, in the camera's frame: Ortho.inverse()'s, from the point's latitude and longitude.
            double la = Math.toRadians(ll[0]), dl = Math.toRadians(ll[1] - now.centreLon());
            double vx = Math.cos(la) * Math.cos(dl), vy = Math.cos(la) * Math.sin(dl), vz = Math.sin(la);
            boolean seen = carry.screen(vx, vy, vz, sp);
            carried++;
            if (o == null ? !seen : seen && Math.hypot(sp[0] - o[0], sp[1] - o[1]) < 1e-6) carriedRight++;
        }
        check("the last picture is carried to its own pixels: a point's place on an older view's screen, by the cameras' turn alone", carried > 0 && carriedRight == carried);

        // The crossfade, and the roll past the reach.
        double notchIn = GlobeFrame.HANDOFF_KM / Math.pow(GlobeFrame.ZOOM_STEP, GlobeFrame.CROSSFADE_NOTCHES);
        check("the swap crossfades: the globe whole at the handoff and wider, gone CROSSFADE_NOTCHES notches in, half way between",
                GlobeFrame.crossfade(GlobeFrame.HANDOFF_KM) == 1 && GlobeFrame.crossfade(1) == 1 && GlobeFrame.crossfade(notchIn) < 1e-12
                        && GlobeFrame.crossfade(notchIn / GlobeFrame.ZOOM_STEP) == 0
                        && Math.abs(GlobeFrame.crossfade(GlobeFrame.HANDOFF_KM / Math.sqrt(Math.pow(GlobeFrame.ZOOM_STEP, GlobeFrame.CROSSFADE_NOTCHES))) - 0.5) < 1e-9);
        GlobeFrame rf = new GlobeFrame(w, h, key.lat(), key.lon());
        double reach = rf.reach();
        double[] in = GlobeFrame.destination(key.lat(), key.lon(), 90, reach - 1e-6), out1 = GlobeFrame.destination(key.lat(), key.lon(), 90, reach + 1e-6);
        double[] half = GlobeFrame.destination(key.lat(), key.lon(), 90, reach + GlobeFrame.ROLL_EASE_KM / 2), gone = GlobeFrame.destination(key.lat(), key.lon(), 90, reach + GlobeFrame.ROLL_EASE_KM);
        double rIn = rf.rollFor(in[0], in[1], GlobeFrame.HANDOFF_KM), rOut = rf.rollFor(out1[0], out1[1], GlobeFrame.HANDOFF_KM);
        double rHalf = rf.rollFor(half[0], half[1], GlobeFrame.HANDOFF_KM), up = rf.planeRollAt(half[0], half[1]);
        out.printf("      the roll at the handoff east of the city: %.4f degrees at the reach's edge inside, %.4f outside, %.4f half way out of %.0f km (the plane's up there %.4f), %.4f past it%n",
                rIn, rOut, rHalf, GlobeFrame.ROLL_EASE_KM, up, rf.rollFor(gone[0], gone[1], GlobeFrame.HANDOFF_KM));
        check("...and the roll eases back to north up over ROLL_EASE_KM past the reach, with no step at its edge", Math.abs(rIn) > 1 && Math.abs(rIn - rOut) < 1e-4
                && Math.abs(rHalf - up / 2) < 1e-9 && rf.rollFor(gone[0], gone[1], GlobeFrame.HANDOFF_KM) == 0);

        // The founding screen's globe: the model's frame and the planet's picture, then the far rule through the camera.
        EarthData data = e.data();
        long t0 = System.nanoTime();
        GlobePicture picture = GlobePicture.of(data, GlobePicture.FIRST_ARCMIN);
        double madeMs = (System.nanoTime() - t0) / 1e6;
        int fw = (int) GlobeFrame.FOUNDING_W, fh = (int) GlobeFrame.FOUNDING_H;
        GlobeFrame fg = new GlobeFrame(fw, fh, key.lat(), key.lon());
        int[] colours = GlobePicture.colours(false), shot = new int[fw * fh];
        picture.view(fg, key, colours, GlobePicture.SPACE, shot);
        int pictured = 0, space = 0, wrong = 0;
        for (int j = 0; j < fh; j++) {
            for (int i = 0; i < fw; i++) {
                double[] ll = fg.latLonAt(i + 0.5, j + 0.5);
                int want = ll == null ? GlobePicture.SPACE : colours[picture.at(ll[0], ll[1])];
                if (ll == null) space++; else pictured++;
                if (shot[j * fw + i] != want) wrong++;
            }
        }
        fg.set(key.lat(), key.lon(), (GlobeFrame.PICTURE_FROM_KM + GlobeFrame.FOUNDING_HANDOFF_KM) / 2);
        picture.view(fg, key, colours, GlobePicture.SPACE, shot);
        byte[] cls = fg.groundPiece(key, 0, 0, fw, fh);
        int groundWrong = 0;
        for (int k = 0; k < shot.length; k++) {
            int i = k % fw, j = k / fw;
            int want = fg.onPlanet(i + 0.5, j + 0.5) ? colours[cls[k]] : GlobePicture.SPACE;
            if (shot[k] != want) groundWrong++;
        }
        out.printf("      the founding globe, %d x %d: whole, %,d pixels of the picture (made in %.0f ms at %d') and %,d of space, %d unlike;"
                + " at %.2f km a pixel the far rule through the camera, %d unlike%n", fw, fh, pictured, madeMs, GlobePicture.FIRST_ARCMIN, space, wrong,
                fg.kmPx(), groundWrong);
        check("the founding screen's globe is the model's frame: its pixels the planet's picture's, space off the disc", fg.tier() == GlobeFrame.GROUND
                && pictured > 0 && space > 0 && wrong == 0);
        check("...and closer, the far rule's through the globe's camera (GlobeFrame.groundPiece()), as the map's GROUND tier draws", groundWrong == 0);
        // Its handoff: half a pixel at the canvas's corner, against the ground drawn round the view's centre.
        double handoff = GlobeFrame.FOUNDING_HANDOFF_KM, parting = foundingParting(key.lat(), key.lon(), handoff), e4 = foundingParting(key.lat(), key.lon(), 4);
        out.printf("      its handoff %.4f km a pixel (spec-globe 3.2: 2.3): the globe and the ground round its centre part by %.6f px at the corners; at E4's 4 km by %.2f (the study 1.50); the expanded map's %.3f (the study 0.58)%n",
                handoff, parting, e4, GlobeFrame.halfPixelKm(w, h));
        check("...handing off to the ground round its centre at FOUNDING_HANDOFF_KM, half a pixel at its corners (halfPixelKm())",
                handoff == GlobeFrame.halfPixelKm(fw, fh) && Math.abs(parting - 0.5) < 1e-6 && e4 > 1);

        // A frame's cost in each tier (printed: the screen's, by eye on the PC).
        int[] big = new int[w * h];
        GlobeFrame cost = new GlobeFrame(w, h, key.lat(), key.lon());
        double[] best = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
        int[] node = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            cost.fit();
            long a0 = System.nanoTime();
            picture.frame(cost, colours, GlobePicture.SPACE, big, GlobePicture.cores());
            double pMs = (System.nanoTime() - a0) / 1e6;
            cost.set(key.lat(), key.lon(), GlobeFrame.HANDOFF_KM * 1.2);
            GlobeFrame cc = cost.copy(w, h, cost.kmPx());
            a0 = System.nanoTime();
            GlobePicture.bands(h, GlobePicture.cores(), (j0, j1) -> cc.ground(32, (nx, ny) -> node, (i, j, vx, vy, vz) -> 1, 2, big, w, 0, w, j0, j1));
            double gMs = (System.nanoTime() - a0) / 1e6;
            a0 = System.nanoTime();
            cost.groundPiece(key, w / 2 - 64, h / 2 - 64, 128, 128);
            double qMs = (System.nanoTime() - a0) / 1e6;
            if (round >= WARM_ROUNDS) {
                best[0] = Math.min(best[0], pMs);
                best[1] = Math.min(best[1], gMs);
                best[2] = Math.min(best[2], qMs);
            }
        }
        out.printf("      a %d x %d frame on %d cores: the PICTURE tier %.1f ms; the GROUND tier from the plane's nodes %.1f ms (a quarter of it on the move);"
                + " a %d-pixel piece of the far rule through the camera %.1f ms on the worker%n", w, h, GlobePicture.cores(), best[0], best[1], 128, best[2]);
    }

    /** The class a legend's ground words name. */
    static byte groundClass(String words) {
        for (byte c = 0; c < World.CLASSES; c++) if (LandMap.groundWords(c).equals(words)) return c;
        return -1;
    }

    /** How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, as FoundingScreen's close tier draws it). */
    static double foundingParting(double lat, double lon, double km) {
        GlobeFrame f = new GlobeFrame(GlobeFrame.FOUNDING_W, GlobeFrame.FOUNDING_H, lat, lon);
        f.set(lat, lon, km);
        EarthProj round = new EarthProj(f.centreLat(), f.centreLon());
        double worst = 0, m = km * 1000;
        double[] ll = new double[2];
        for (double sx : new double[] { 0, GlobeFrame.FOUNDING_W / 2, GlobeFrame.FOUNDING_W }) {
            for (double sy : new double[] { 0, GlobeFrame.FOUNDING_H / 2, GlobeFrame.FOUNDING_H }) {
                if (!round.inverse((sx - GlobeFrame.FOUNDING_W / 2) * m, (GlobeFrame.FOUNDING_H / 2 - sy) * m, ll)) continue;
                double[] p = f.at(ll[0], ll[1]);
                if (p != null) worst = Math.max(worst, Math.hypot(p[0] - sx, p[1] - sy));
            }
        }
        return worst;
    }

    /** Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. */
    static CityMap earthX1(Squares sq) throws Exception {
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        if (fixture == null || expect == null) return null;
        String id = EarthTerrain.use(EarthData.of(fixture));
        for (String line : new String(expect, java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length <= 4 || !f[1].equals("window") || !f[2].equals("Rotterdam")) continue;
            World w = World.of(World.Key.earth(EarthTerrainCheck.SEED, Double.parseDouble(f[3]), Double.parseDouble(f[4]), id));
            long[] c = sq.counts[0].clone();
            if (Mining.IRON_MINE < c.length) c[Mining.IRON_MINE] = 0;
            return CityMap.square(w.key(), w.foundingX(), w.foundingY(), sq.types, x1Side(sq.types),
                    (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), c);
        }
        return null;
    }

    /* =====================================================================
       14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3)
       ===================================================================== */

    /** Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. */
    static final int BOXES = 200;

    /** ...and through the globe's camera, each against a point at every free plot's middle: 60. */
    static final int GLOBE_BOXES = 60;

    /** The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. */
    static final int FIELD_BUYS_MOST = 200;

    /** The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. */
    static final long BOX_PLOTS_MOST = 200_000;

    /** Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. */
    static final double GLOBE_LAT = 51.90625, GLOBE_LON = 4.48125;

    static void buyingFromTheMap(Game g) throws Exception {
        out.println("\n--- 14. buying from the map: the card's figures are the office row's, its bond the funding page's first offer,"
                + " a box takes the offers whose free ground it meets, and the picks drop on a month and a load ---");
        CityLand land = g.getCityLand();
        LandMarket market = g.getLandManager().getMarket();
        // A field on offer (board 7's case): the city buys toward the nearest iron field it does not own, as MiningCheck.makeRoom()
        // does, the offer nearest it each time, until an offer holds one - handed each price, so the field is what is tested.
        int towardField = 0;
        PrintStream was = System.out;
        System.setOut(QUIET);
        try {
            while (towardField < FIELD_BUYS_MOST && market.richest(Resource.IRON) == null) {
                LandParcel step = MiningCheck.towardIron(g);
                if (step == null) break;
                if (!g.canAffordParcel(step)) g.setCashForTest(g.getCash() + step.localPrice(g.getForeignAccounts().getRate()));
                if (!g.buyLandParcel(step.getId())) break;
                towardField++;
            }
        } finally {
            System.setOut(was);
        }
        out.printf("      %d offers bought toward the nearest iron field until one holds it%n", towardField);
        List<LandParcel> listing = market.getListing();
        double rate = g.getForeignAccounts().getRate();
        boolean wasVault = g.isLandPaidFromVault();
        check("fixture: the played city lists four offers or more", listing.size() >= 4);
        if (listing.size() < 4) return;

        // The card for one offer: the figures the office's row reads for it, converting and from the vault.
        int rowsSame = 0, rowsTried = 0, rowsShort = 0;
        for (boolean vault : new boolean[] { false, true }) {
            g.setLandPaidFromVault(vault);
            for (LandParcel p : listing) {
                rowsTried++;
                List<Integer> just = List.of(p.getId());
                LandMap.Card c = LandMap.card(g, just);
                boolean same = c.ids().equals(just) && c.vault() == vault
                        && c.usd() == p.getPriceUsd() && c.local() == p.localPrice(rate)
                        && c.price() == (vault ? p.getPriceUsd() : p.localPrice(rate))
                        && c.km2() == p.getKm2() && c.dryKm2() == p.getKm2(CityLand.DRY) && c.freshKm2() == p.getKm2(CityLand.FRESH)
                        && c.seaKm2() == p.getKm2(CityLand.SEA) && c.drySqFt() == p.getSizeSqFt()
                        && (p.getDryKm2() > 0 ? c.usd() / c.dryKm2() == p.getPriceUsd() / p.getDryKm2() : c.dryKm2() == 0);
                for (Resource r : Resource.values()) same &= c.sites()[r.ordinal()] == p.getSites(r) && c.amounts()[r.ordinal()] == p.getAmount(r);
                double before = vault ? g.getForeignAccounts().getReservesUsd() : g.getCash();
                same &= c.before() == before && c.after() == before - c.price() && c.funding() == g.landNeedsFunding(just)
                        && c.gap() == (vault ? g.landVaultGapUsd(just) : g.landCashGap(just));
                if (c.funding()) rowsShort++;
                if (same) rowsSame++;
            }
        }
        g.setLandPaidFromVault(false);
        out.printf("      %d offers' cards against their rows, converting and from the vault: %d the same (%d short)%n", rowsTried, rowsSame, rowsShort);
        check("the card for one offer has its row's figures - its price listed and at today's rate, its km2 by kind, its dry ground,"
                + " a dry km2, its sites and tonnes - and the cash before and after it, for every offer, both ways of paying",
                rowsTried == 2 * listing.size() && rowsSame == rowsTried);

        // The fields the card rings (board 7): each offer's own, its row's sites and tonnes, each site's square where the hover names it.
        int fieldsSame = 0, withFields = 0, ringed = 0;
        for (LandParcel p : listing) {
            int[] sites = new int[Resource.values().length];
            double[] tonnes = new double[Resource.values().length];
            boolean squares = true;
            for (Deposit f : LandMap.offerFields(land, p)) {
                sites[f.kind().ordinal()] += f.sites();
                tonnes[f.kind().ordinal()] += f.amount();
                List<double[]> sq = LandMap.siteSquares(f);
                squares &= sq.size() == f.sites();
                for (double[] s : sq) {
                    boolean named = false;
                    for (LandMap.FieldAt at : LandMap.fieldsAt(land, (long) s[0], (long) s[1])) named |= at.field().equals(f);
                    squares &= named;
                    ringed++;
                }
            }
            boolean same = squares, any = false;
            for (Resource r : Resource.values()) {
                same &= sites[r.ordinal()] == p.getSites(r) && sameFigure(tonnes[r.ordinal()], p.getAmount(r));
                any |= p.getSites(r) > 0;
            }
            if (any) withFields++;
            if (same) fieldsSame++;
        }
        out.printf("      %d of %d offers hold fields, %d sites ringed; %d offers' fields their rows' sites and tonnes%n", withFields,
                listing.size(), ringed, fieldsSame);
        check("fixture: some offers hold fields", withFields > 0);
        check("the fields the card rings are the offer's own: their sites and tonnes its row's, each site's square one the hover names"
                + " for its field, for every offer", fieldsSame == listing.size());

        // Several: the sums, "Buy the best N"'s totals, in the order picked; one picked twice and an id not on offer left out.
        List<Integer> best = g.nextLandParcels(3);
        List<Integer> order = List.of(best.get(2), best.get(0), best.get(1));
        List<Integer> asked = new ArrayList<>(order);
        asked.add(best.get(0));
        asked.add(market.getNextOfferId());
        LandMap.Card several = LandMap.card(g, asked);
        double usd = 0, local = 0, km2 = 0, sqFt = 0;
        for (int id : order) {
            LandParcel p = market.find(id);
            usd += p.getPriceUsd();
            local += p.localPrice(rate);
            km2 += p.getKm2();
            sqFt += p.getSizeSqFt();
        }
        out.printf("      three picked: %s, US$%,.1fk, %.4f km2, cash D$%,.1fk -> D$%,.1fk%n", several.ids(), several.usd(), several.km2(),
                several.before(), several.after());
        check("for several: their sums, \"Buy the best N\"'s totals, in the order picked, one picked twice and an id not on offer left out",
                several.ids().equals(order) && sameFigure(several.usd(), usd) && sameFigure(several.local(), local)
                        && sameFigure(several.usd(), g.landPriceUsd(best)) && sameFigure(several.local(), g.landPriceLocal(best))
                        && sameFigure(several.km2(), km2) && sameFigure(several.drySqFt(), sqFt)
                        && several.after() == several.before() - several.local());

        // A box on the plane: exactly the offers whose free ground it meets, every plot under it tried.
        MapFrame f = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        LandMap.open(f, land, market);
        java.util.Random rnd = new java.util.Random(BOXES);
        int boxesExact = 0, boxesTaking = 0, boxesSeveral = 0;
        java.util.Set<Integer> takenBy = new java.util.HashSet<>(), missedBy = new java.util.HashSet<>();
        for (int b = 0; b < BOXES; b++) {
            double w, h, sx0, sy0;
            do {
                w = f.width() * (0.01 + 0.3 * rnd.nextDouble());
                h = f.height() * (0.01 + 0.3 * rnd.nextDouble());
                sx0 = rnd.nextDouble() * (f.width() - w);
                sy0 = rnd.nextDouble() * (f.height() - h);
            } while ((w / f.scale() + 2) * (h / f.scale() + 2) > BOX_PLOTS_MOST);
            List<Integer> got = LandMap.boxPicks(land, listing, LandMap.onScreen(f), sx0, sy0, sx0 + w, sy0 + h);
            java.util.Set<Integer> plots = boxByPlots(land, listing, f.plotX(sx0), f.plotY(sy0), f.plotX(sx0 + w), f.plotY(sy0 + h));
            if (got.size() == plots.size() && plots.containsAll(got) && inOrder(got, listing)) boxesExact++;
            if (!got.isEmpty()) boxesTaking++;
            if (got.size() > 1) boxesSeveral++;
            for (LandParcel p : listing) (got.contains(p.getId()) ? takenBy : missedBy).add(p.getId());
        }
        // ...a box inside one free plot of each offer takes it alone.
        int alone = 0;
        for (LandParcel p : listing) {
            double[] c = LandMap.freeCells(land, p).get(0);
            List<Integer> got = LandMap.boxPicks(land, listing, LandMap.onScreen(f), f.screenX(c[0] + 0.25), f.screenY(c[1] + 0.25),
                    f.screenX(c[0] + 0.75), f.screenY(c[1] + 0.75));
            if (got.equals(List.of(p.getId()))) alone++;
        }
        out.printf("      %d boxes at %.2f px a plot: %d as every plot under them says, %d taking an offer, %d several; %d of %d offers taken by"
                + " some and %d missed by others; a box in one free plot of each takes it alone for %d%n", BOXES, f.scale(), boxesExact,
                boxesTaking, boxesSeveral, takenBy.size(), listing.size(), missedBy.size(), alone);
        check("fixture: the boxes take offers, several at once, and every offer is taken by some and missed by others",
                boxesTaking > 0 && boxesSeveral > 0 && takenBy.size() == listing.size() && missedBy.size() == listing.size());
        check("a box takes exactly the offers whose free ground it meets - every plot under it tried - in the order given",
                boxesExact == BOXES);
        check("...and a box inside one free plot of an offer takes that offer alone, for every offer", alone == listing.size());

        // The city's own ground inside an offer's rectangle: a rectangle of two blocks across its east edge, as section 7's.
        long ex = land.siteX(), ey = land.siteY(), kb = 1L << land.level();
        while (land.ownsPlot(ex + 1, ey)) ex++;
        LandParcel across = new LandParcel(-1, 1, 0, land.level(), ex + 1 - kb, ey, ex + 1 + kb, ey + kb, null, null, null, 0, 0);
        List<double[]> cells = LandMap.freeCells(land, across);
        long freeArea = 0, unowned = 0;
        boolean cellsFree = true;
        for (double[] c : cells) {
            freeArea += (long) ((c[2] - c[0]) * (c[3] - c[1]));
            for (long x = (long) c[0]; x < c[2]; x++) for (long y = (long) c[1]; y < c[3]; y++) cellsFree &= !land.ownsPlot(x, y);
        }
        for (long x = across.getX0(); x < across.getX1(); x++) for (long y = across.getY0(); y < across.getY1(); y++) if (!land.ownsPlot(x, y)) unowned++;
        List<LandParcel> justAcross = List.of(across);
        List<Integer> onOwned = LandMap.boxPicks(land, justAcross, LandMap.onScreen(f), f.screenX(ex + 0.25), f.screenY(ey + 0.25),
                f.screenX(ex + 0.75), f.screenY(ey + 0.75));
        List<Integer> onFree = LandMap.boxPicks(land, justAcross, LandMap.onScreen(f), f.screenX(ex + 1.25), f.screenY(ey + 0.25),
                f.screenX(ex + 1.75), f.screenY(ey + 0.75));
        out.printf("      a rectangle of %d x %d plots across the city's east edge: %d free plots in %d cells; a box on its owned plot takes %s,"
                + " one on the free plot beside it %s%n", 2 * kb, kb, freeArea, cells.size(), onOwned, onFree);
        check("fixture: the rectangle takes in some of the city's ground", unowned < 2 * kb * kb && unowned > 0 && land.ownsPlot(ex, ey));
        check("an offer's free cells are exactly its free plots, and a box over the city's own ground inside its rectangle takes nothing",
                cellsFree && freeArea == unowned && onOwned.isEmpty() && onFree.equals(List.of(-1)));

        // Through the globe's camera, in the GROUND tier: every offer a point of its free ground puts inside the box is taken, and
        // nothing whose free ground's corners all fall outside the box's reach.
        GlobeFrame globe = new GlobeFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H, GLOBE_LAT, GLOBE_LON);
        globe.set(GLOBE_LAT, GLOBE_LON, GlobeFrame.HANDOFF_KM * 1.2);
        double shiftX = World.SIDE / 2.0 - land.siteX(), shiftY = World.SIDE / 2.0 - land.siteY();
        LandMap.ToScreen camera = (x, y) -> globe.screenOfPlot(x + shiftX, y + shiftY);
        double[] lo = { Double.MAX_VALUE, Double.MAX_VALUE }, hi = { -Double.MAX_VALUE, -Double.MAX_VALUE };
        for (LandParcel p : listing) {
            for (double[] q : new double[][] { camera.at(p.getX0(), p.getY0()), camera.at(p.getX1(), p.getY1()) }) {
                if (q == null) continue;
                lo[0] = Math.min(lo[0], q[0]);
                lo[1] = Math.min(lo[1], q[1]);
                hi[0] = Math.max(hi[0], q[0]);
                hi[1] = Math.max(hi[1], q[1]);
            }
        }
        int globeHeld = 0, globeTaking = 0;
        for (int b = 0; b < GLOBE_BOXES; b++) {
            double bx0 = lo[0] + rnd.nextDouble() * (hi[0] - lo[0]), by0 = lo[1] + rnd.nextDouble() * (hi[1] - lo[1]);
            double bx1 = bx0 + rnd.nextDouble() * (hi[0] - lo[0]) / 2, by1 = by0 + rnd.nextDouble() * (hi[1] - lo[1]) / 2;
            List<Integer> got = LandMap.boxPicks(land, listing, camera, bx0, by0, bx1, by1);
            boolean held = inOrder(got, listing);
            for (LandParcel p : listing) {
                boolean inside = false, near = false;
                for (double[] c : LandMap.freeCells(land, p)) {
                    for (long x = (long) c[0]; x < c[2]; x++) {
                        for (long y = (long) c[1]; y < c[3]; y++) {
                            double[] s = camera.at(x + 0.5, y + 0.5);
                            inside |= s != null && s[0] > bx0 && s[0] < bx1 && s[1] > by0 && s[1] < by1;
                        }
                    }
                    double[] a = camera.at(c[0], c[1]), d = camera.at(c[2], c[3]), e = camera.at(c[2], c[1]), k = camera.at(c[0], c[3]);
                    near |= a != null && d != null && e != null && k != null
                            && Math.max(Math.max(a[0], d[0]), Math.max(e[0], k[0])) > bx0 && Math.min(Math.min(a[0], d[0]), Math.min(e[0], k[0])) < bx1
                            && Math.max(Math.max(a[1], d[1]), Math.max(e[1], k[1])) > by0 && Math.min(Math.min(a[1], d[1]), Math.min(e[1], k[1])) < by1;
                }
                boolean taken = got.contains(p.getId());
                held &= (!inside || taken) && (!taken || near);
            }
            if (held) globeHeld++;
            if (!got.isEmpty()) globeTaking++;
        }
        out.printf("      %d boxes through the globe's camera at %.2f km a pixel (the %s tier), the offers %.1f x %.1f px: %d held, %d taking an offer%n",
                GLOBE_BOXES, globe.kmPx(), globe.tier() == GlobeFrame.GROUND ? "GROUND" : "other", hi[0] - lo[0], hi[1] - lo[1], globeHeld, globeTaking);
        check("fixture: the globe's view is in the GROUND tier and its boxes take offers", globe.tier() == GlobeFrame.GROUND && globeTaking > 0);
        check("through the globe's camera a box takes every offer a point of its free ground puts inside it, and none it cannot reach",
                globeHeld == GLOBE_BOXES);

        // The picks: a click takes one, shift adds and drops, a box adds in order, a chip drops one.
        List<Integer> four = g.nextLandParcels(4);
        int a = four.get(0), b2 = four.get(1), c3 = four.get(2), d4 = four.get(3);
        LandMap.Picks picks = new LandMap.Picks();
        picks.only(g, d4);
        picks.only(g, a);
        boolean added = picks.toggle(g, b2);
        picks.add(g, List.of(c3, b2, d4));
        List<Integer> afterAdd = picks.live(g);
        boolean dropped = !picks.toggle(g, a);
        picks.drop(c3);
        List<Integer> afterDrop = picks.live(g);
        check("the picks: a click takes one alone, shift adds one and drops it again, a box adds what it takes in order, a chip's x drops one",
                added && afterAdd.equals(List.of(a, b2, c3, d4)) && dropped && afterDrop.equals(List.of(b2, d4)));

        // What the card says the cash will be is what it is once bought - converting, then from the vault - and a pick bought
        // falls out of the picks.
        picks.clear();
        picks.add(g, List.of(a, b2, c3, d4));
        PrintStream real = System.out;
        System.setOut(QUIET);
        LandMap.Card convert, vaultCard;
        double cashAfter, cashBefore, reservesAfter, cashFromVault;
        int boughtA, boughtB;
        try {
            LandParcel pa = market.find(a);
            if (g.landNeedsFunding(List.of(a))) g.setCashForTest(pa.localPrice(rate) * 2);
            convert = LandMap.card(g, List.of(a));
            boughtA = g.buyLandParcels(List.of(a));
            cashAfter = g.getCash();
            g.setLandPaidFromVault(true);
            LandParcel pb = market.find(b2);
            double reserves = g.getForeignAccounts().getReservesUsd();
            if (reserves < pb.getPriceUsd()) g.getForeignAccounts().buyReserves((pb.getPriceUsd() - reserves) * 2 * rate);
            vaultCard = LandMap.card(g, List.of(b2));
            cashBefore = g.getCash();
            boughtB = g.buyLandParcels(List.of(b2));
            reservesAfter = g.getForeignAccounts().getReservesUsd();
            cashFromVault = g.getCash();
            g.setLandPaidFromVault(false);
        } finally {
            System.setOut(real);
        }
        List<Integer> afterBuying = picks.live(g);
        out.printf("      bought %s converting: the card said cash D$%,.3fk after, the treasury holds D$%,.3fk; %s from the vault: the card said"
                + " US$%,.3fk after, the vault holds US$%,.3fk; the picks now %s%n", a, convert.after(), cashAfter, b2, vaultCard.after(),
                reservesAfter, afterBuying);
        check("fixture: neither needed a loan, and both were bought", !convert.funding() && !vaultCard.funding() && boughtA == 1 && boughtB == 1);
        check("what the card says the cash will be is the treasury's once bought, and the vault's from the vault, no cash moving",
                sameFigure(convert.after(), cashAfter) && sameFigure(vaultCard.after(), reservesAfter) && cashFromVault == cashBefore);
        check("...and an offer bought falls out of the picks, the rest kept in order", afterBuying.equals(List.of(c3, d4)));

        // Short, converting: the card's bond is the funding page's first offer for the same gap, and issued it buys them all.
        List<Integer> three = g.nextLandParcels(3);
        g.setCashForTest(g.landPriceLocal(three) * 0.4);
        LandMap.Card shortCard = LandMap.card(g, three);
        DebtQuote bond = shortCard.bond(), note = shortCard.note();
        DebtQuote again = g.quoteLongBondForCash(g.landCashGap(three), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        boolean bondRight = bond != null && bond.equals(again) && bond.duration() == Game.BUILD_BOND_YEARS && bond.requested() == shortCard.gap()
                && Math.abs(bond.faceValue() / Game.BUILD_BOND_GRANULE - Math.rint(bond.faceValue() / Game.BUILD_BOND_GRANULE)) < 1e-9
                && bond.cashReceived() >= shortCard.gap();
        boolean noteRight = note != null && note.duration() == Game.BUILD_NOTE_MONTHS && note.cashReceived() >= shortCard.gap();
        System.setOut(QUIET);
        int boughtShort;
        try {
            g.handleLongBondForCash(shortCard.gap(), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            boughtShort = g.buyLandParcels(shortCard.ids());
        } finally {
            System.setOut(real);
        }
        out.printf("      three short by D$%,.1fk: the card's bond %s, face D$%,.1fk for D$%,.1fk; its note %s months; bought %d once issued%n",
                shortCard.gap(), bond == null ? "none" : bond.duration() + " years", bond == null ? 0 : bond.faceValue(),
                bond == null ? 0 : bond.cashReceived(), note == null ? "none" : String.valueOf(note.duration()), boughtShort);
        check("fixture: three offers the treasury is short of", shortCard.funding() && shortCard.gap() > 0
                && sameFigure(shortCard.gap(), shortCard.local() - shortCard.before()));
        check("short, the card's bond is the funding page's first: the " + Game.BUILD_BOND_YEARS + "-year term for the gap, its face on the granule,"
                + " its cash covering it - and the note beside it", bondRight && noteRight);
        check("...and issued, the card's bond buys them all", boughtShort == three.size());

        // Short from the vault: the dollar bond and note for the vault's gap, or none while the window abroad is shut.
        g.setLandPaidFromVault(true);
        List<Integer> all = g.nextLandParcels(LandMarket.OFFERS);
        LandMap.Card vaultShort = LandMap.card(g, all);
        boolean open = g.foreignWindowOpen();
        boolean dollars = open
                ? vaultShort.bond() != null && vaultShort.bond().equals(g.quoteForeignForCash("Term", vaultShort.gap(), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE))
                        && vaultShort.note() != null && vaultShort.note().equals(g.quoteForeignForCash("Note", vaultShort.gap(), Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE))
                : vaultShort.bond() == null && vaultShort.note() == null;
        g.setLandPaidFromVault(false);
        out.printf("      every offer from the vault: short by US$%,.1fk, the window abroad %s%n", vaultShort.gap(), open ? "open" : "shut");
        check("fixture: the vault is short of every offer", vaultShort.funding() && vaultShort.gap() > 0);
        check("...and the card's loans from the vault are the dollar bond and note for its gap, or none while the window abroad is shut", dollars);

        // A month drops the picks; so does a load into the same Game, though its month and its offers are the same.
        // ...from none: the picks above are left standing - the card steps' c3 and d4, less the offers the short card bought - and
        // two more on top of one that survived stood as three (FR7: the played city's offers moved, and one of them did).
        picks.clear();
        picks.add(g, g.nextLandParcels(2));
        List<Integer> beforeMonth = picks.live(g);
        System.setOut(QUIET);
        try {
            LongPlaytest.run(g, 1);
        } finally {
            System.setOut(real);
        }
        List<Integer> afterMonth = picks.live(g);
        picks.add(g, g.nextLandParcels(2));
        List<Integer> beforeLoad = picks.live(g);
        int month = g.getMonth();
        System.setOut(QUIET);
        try {
            g.saveGame(8, "mapcheck picks");
            g.loadGameSave(8);
        } finally {
            System.setOut(real);
        }
        boolean stillListed = !beforeLoad.isEmpty();
        for (int id : beforeLoad) stillListed &= g.getLandManager().getMarket().find(id) != null;
        List<Integer> afterLoad = picks.live(g);
        picks.add(g, g.nextLandParcels(1));
        boolean cleared = !picks.live(g).isEmpty();
        picks.clear();
        cleared &= picks.live(g).isEmpty();
        out.printf("      picks %s before a month, %s after; %s before a load in month %,d, %s after it (the month %,d, the offers %s)%n",
                beforeMonth, afterMonth, beforeLoad, month, afterLoad, g.getMonth(), stillListed ? "still listed" : "gone");
        check("fixture: picks stood before the month and the load, and the load kept the month and the offers",
                beforeMonth.size() == 2 && beforeLoad.size() == 2 && g.getMonth() == month && stillListed);
        check("the picks drop on a month, and on a load into the same Game; clear() - Esc, a click off the offers, the map collapsed -"
                + " empties them", afterMonth.isEmpty() && afterLoad.isEmpty() && cleared);
        g.setLandPaidFromVault(wasVault);
    }

    /** The offers a box of plots meets, plot by plot: every plot under (x0, y0)-(x1, y1) in some part, not the city's, inside an offer's rectangle. */
    static java.util.Set<Integer> boxByPlots(CityLand land, List<LandParcel> offers, double x0, double y0, double x1, double y1) {
        java.util.Set<Integer> out = new java.util.HashSet<>();
        for (long x = (long) Math.floor(Math.min(x0, x1)); x < Math.max(x0, x1); x++) {
            for (long y = (long) Math.floor(Math.min(y0, y1)); y < Math.max(y0, y1); y++) {
                if (land.ownsPlot(x, y)) continue;
                for (LandParcel o : offers) if (o.contains(x, y)) out.add(o.getId());
            }
        }
        return out;
    }

    /** Whether these ids stand in the listing's order. */
    static boolean inOrder(List<Integer> ids, List<LandParcel> listing) {
        int at = -1;
        for (int id : ids) {
            int i = -1;
            for (int k = 0; k < listing.size(); k++) if (listing.get(k).getId() == id) i = k;
            if (i <= at) return false;
            at = i;
        }
        return true;
    }

    /** Two money or area figures the same to a billionth of the larger. */
    static boolean sameFigure(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /* =====================================================================
       15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14;
       docs/studies/spec-materials.md 5's I14 row)
       ===================================================================== */

    /** The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. */
    static final int MATERIALS_SIDE = 10;

    /** The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. */
    static final String[] MATERIALS_SECTORS = { Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRYING,
            Sectors.CEMENT_CONCRETE, Sectors.COPPER_MINING, Sectors.COPPER_REFINING, Sectors.FORESTRY, Sectors.WOOD_PRODUCTS };

    /** Whether a template is one of the nine industries'. */
    static boolean materialsOf(BuildingsTemplate t) {
        return Arrays.asList(MATERIALS_SECTORS).contains(t.getSector());
    }

    /** The fixture's count of a materials type: one of a works on more than one site, two of anything else - so each slot holds two kinds and the larger is laid first. */
    static long materialsCount(BuildingVisual.Type t) {
        return t.sites() > 1 ? 1 : 2;
    }

    static void materials(long seed) {
        out.println("\n--- 15. the materials drawn as themselves (0.8.0): each kind its own look, every works on its own sites by type, each industry's works one campus, each thing drawn once, the zones tinted where they are, the screen within 80 ms ---");
        List<BuildingsTemplate> templates = new BuildingCatalog().load();
        BuildingVisual.Type[] types = BuildingVisual.table(templates);
        // Every building of the nine industries: a look of its own, and on its template's sites.
        int kinds = 0, own = 0, sited = 0, sitesRight = 0;
        int[] byLook = new int[BuildingVisual.LOOKS];
        StringBuilder plain = new StringBuilder();
        for (BuildingsTemplate t : templates) {
            if (!materialsOf(t)) continue;
            kinds++;
            BuildingVisual.Type ty = types[t.getId()];
            boolean mine = ty.materials() && ty.fill() != BuildingVisual.FILL[BuildingVisual.MINE] && ty.fill() != BuildingVisual.FILL[BuildingVisual.INDUSTRY];
            if (mine) own++;
            else plain.append(' ').append(t.getName());
            byLook[ty.look()]++;
            BuildingsTemplate.Site s = t.site();
            if (s != null && s.sites() > 0) {
                sited++;
                if (ty.site() == s.resource() && ty.sites() == s.sites() && ty.field() == s.type()) sitesRight++;
            }
        }
        out.printf("      the nine industries' %d kinds: %d with a look of their own (open pits %d, shafts %d, quarries %d, leach plants %d,"
                + " wash plants %d, camps %d, stands %d, works %d)%s; %d on a resource's sites, %d of them as their templates say%n",
                kinds, own, byLook[BuildingVisual.PIT], byLook[BuildingVisual.SHAFT], byLook[BuildingVisual.QUARRY], byLook[BuildingVisual.LEACH],
                byLook[BuildingVisual.WASH], byLook[BuildingVisual.CAMP], byLook[BuildingVisual.STAND], byLook[BuildingVisual.WORKS],
                plain.length() == 0 ? "" : "; plain:" + plain, sited, sitesRight);
        check("every building of the nine materials industries has a look of its own - none a plain mine's or industry's box", kinds > 0 && own == kinds);
        check("...and every one on a resource's sites stands on its template's: the resource, its type of field, as many sites as it takes",
                sited > 0 && sitesRight == sited);

        // The fixture: Jerus's city x 1 on a square of MATERIALS_SIDE districts holding fields of every typed slot, and of each
        // materials kind one works of many sites or two of any other.
        World w = World.of(seed);
        long[] c = new long[types.length];
        for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = JERUS_COUNTS[t];
        int[] need = new int[CityMap.SLOTS];
        for (BuildingsTemplate t : templates) {
            if (!materialsOf(t)) continue;
            BuildingVisual.Type ty = types[t.getId()];
            c[t.getId()] = materialsCount(ty);
            if (ty.site() == null) continue;
            BuildingVisual.Type x = ty;
            Deposit.FieldType f = x.field() != null ? x.field() : x.site().typesOf().get(0);
            need[CityMap.slotOf(x.site(), f)] += (int) (materialsCount(x) * x.sites());
        }
        long[] place = materialsPlace(w, MATERIALS_SIDE, need);
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap m = CityMap.square(seed, place[0], place[1], types, MATERIALS_SIDE, capacity, c);
        StringBuilder slots = new StringBuilder();
        for (int k = 0; k < CityMap.SLOTS; k++) {
            if (CityMap.SLOT_RESOURCE[k] == Resource.OIL) continue;
            slots.append(String.format(" %s %d of %d needed;", CityMap.SLOT_TYPE[k].name().toLowerCase(java.util.Locale.ROOT),
                    m.ownedSites(CityMap.SLOT_RESOURCE[k], CityMap.SLOT_TYPE[k]), need[k]));
        }
        out.printf("      the fixture: plot (%d, %d), %d cells out; %d districts; its sites by type:%s %,d buildings%n", place[0], place[1], place[2],
                m.districts().size(), slots, Arrays.stream(c).sum());
        boolean room = place[2] >= 0;
        for (int k = 0; k < CityMap.SLOTS; k++) room &= m.ownedSites(CityMap.SLOT_RESOURCE[k], CityMap.SLOT_TYPE[k]) >= need[k];
        check("fixture: its square holds sites enough of every type for all its works", room);
        check("its districts sum to its counts exactly, every type on its sites among them (coal's, stone's and copper's first types were placed nowhere until 0.8.0)",
                Arrays.equals(m.totals(), c));

        // Each thing drawn once: a works on sites once for all its sites, the rest box by box.
        Drawn d = drawn(m);
        out.printf("      drawn: %,d buildings of the model's %,d, %d works drawn on only some of their sites%n", d.total(), modelBuildings(c, types), d.partWorks);
        check("every building drawn once - each type as many as the model has, a works on its sites once for all of them, none twice, none missing",
                d.sameAs(c, types) && d.partWorks == 0);

        // A works on as many sites as it takes, each of its own type of field.
        boolean typed = true;
        long[] onSites = new long[types.length];
        for (CityMap.District dd : m.districts()) {
            for (int k = 0; k < CityMap.SLOTS; k++) {
                int[] rs = dd.siteRuns[k];
                if (rs == null) continue;
                for (int a = 0; a + 2 < rs.length; a += 3) {
                    BuildingVisual.Type ty = types[rs[a + 1]];
                    typed &= ty.site() == CityMap.SLOT_RESOURCE[k] && (ty.field() == null || ty.field() == CityMap.SLOT_TYPE[k]);
                    onSites[rs[a + 1]] += rs[a + 2];
                }
            }
        }
        boolean twenty = true, any20 = false;
        StringBuilder works = new StringBuilder();
        for (BuildingsTemplate t : templates) {
            BuildingVisual.Type ty = types[t.getId()];
            if (ty == null || ty.site() == null || c[t.getId()] <= 0) continue;
            long stood = d.siteCount[t.getId()] / Math.max(1, ty.sites());
            twenty &= d.siteCount[t.getId()] == stood * ty.sites() && onSites[t.getId()] == d.siteCount[t.getId()];
            if (ty.sites() >= 20 && stood > 0) any20 = true;
            if (ty.sites() > 1) works.append(String.format(" %s %d on %d sites;", t.getName(), stood, d.siteCount[t.getId()]));
        }
        out.printf("      the works of many sites:%s%n", works);
        check("a works of twenty sites is drawn on twenty sites - every works on all of its sites - and each site under a works is of its type of field",
                any20 && twenty && typed);

        // Nothing placed moves: one more Iron Mine, the last laid on hematite, and every other works keeps its sites.
        java.util.Map<Long, Integer> before = sitesUnder(m), after;
        long[] more = c.clone();
        more[Mining.IRON_MINE]++;
        m.reconcile(more);
        after = sitesUnder(m);
        int moved = 0, added = 0;
        for (java.util.Map.Entry<Long, Integer> e : after.entrySet()) {
            Integer was = before.get(e.getKey());
            if (was == null) { if (e.getValue() == Mining.IRON_MINE) added++; else moved++; }
            else if (!was.equals(e.getValue())) moved++;
        }
        for (Long k : before.keySet()) if (!after.containsKey(k)) moved++;
        out.printf("      one more Iron Mine: %d site(s) newly under one, %d site(s) under another works or none than before%n", added, moved);
        check("...one more Iron Mine takes one more hematite site, and no works placed before it moves", added == 1 && moved == 0
                && Arrays.equals(m.totals(), more));
        m.reconcile(c);

        // Each industry's works one campus: one district, cells that touch one another, first of its industry after those before it.
        for (int g = BuildingVisual.STEEL; g < BuildingVisual.GROUPS; g++) {
            int want = 0;
            for (BuildingVisual.Type ty : types) if (ty != null && ty.campusGroup() == g) want += (int) c[ty.id()];
            CityMap.District home = null;
            int holding = 0;
            for (CityMap.District dd : m.districts()) if (m.campusCells(dd, g) > 0) { holding++; home = dd; }
            java.util.Set<Integer> cells = new java.util.HashSet<>();
            int placed = 0;
            if (home != null) {
                DistrictPlan p = m.plan(home);
                for (int b = 0; b < p.buildings; b++) {
                    BuildingVisual.Type ty = types[p.btype[b]];
                    if (ty == null || ty.campusGroup() != g) continue;
                    cells.add((p.by[b] / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE + p.bx[b] / DistrictPlan.CELL);
                    placed++;
                }
            }
            out.printf("      %s: %d works, in %d district(s); its plan places %d in %d cell(s)%n", BuildingVisual.GROUP_NAMES[g], want, holding, placed,
                    cells.size());
            check("...the " + BuildingVisual.GROUP_NAMES[g].toLowerCase(java.util.Locale.ROOT) + " stand in one district, every one placed, in cells that touch: one campus",
                    want > 0 && holding == 1 && placed == want && connected(cells));
        }

        // The looks drawn: on the tiles holding each look, its own colours.
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int px = 8;
        int[] img = new int[(World.TILE * px) * (World.TILE * px)];
        int[] seen = new int[BuildingVisual.LOOKS], drawnLook = new int[BuildingVisual.LOOKS];
        // A works on a site is seen once for the site, whichever tiles it spans, and shows its look on any of them.
        java.util.Map<Long, Integer> siteLook = new java.util.HashMap<>();
        java.util.Set<Long> siteShows = new java.util.HashSet<>();
        long[] box = tilesOf(m);
        for (long ty = box[1]; ty < box[1] + box[3]; ty++) {
            for (long tx = box[0]; tx < box[0] + box[2]; tx++) {
                m.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                boolean anyLook = false;
                for (int b = 0; b < p.buildings; b++) anyLook |= in.types[p.btype[b]].materials();
                if (!anyLook) continue;
                TileRaster.raster(in, p, px, img);
                for (int b = 0; b < p.buildings; b++) {
                    BuildingVisual.Type t = in.types[p.btype[b]];
                    boolean onSite = p.bsite[b] >= 0;
                    if (!t.materials() || p.bpacked[b] || (!onSite && (p.bw[b] < 2 || p.bh[b] < 2))) continue;
                    int sign = lookSign(t);
                    boolean found = false;
                    for (int y = p.by[b] * px; y < (p.by[b] + p.bh[b]) * px && !found; y++) {
                        for (int x = p.bx[b] * px; x < (p.bx[b] + p.bw[b]) * px && !found; x++) found = img[y * World.TILE * px + x] == sign;
                    }
                    if (onSite) {
                        long key = in.siteKey[p.bsite[b]];
                        siteLook.put(key, t.look());
                        if (found) siteShows.add(key);
                        continue;
                    }
                    seen[t.look()]++;
                    if (found) drawnLook[t.look()]++;
                }
            }
        }
        for (java.util.Map.Entry<Long, Integer> e : siteLook.entrySet()) {
            seen[e.getValue()]++;
            if (siteShows.contains(e.getKey())) drawnLook[e.getValue()]++;
        }
        boolean looks = true;
        StringBuilder lw = new StringBuilder();
        for (int k = 1; k < BuildingVisual.LOOKS; k++) {
            lw.append(String.format(" %s %d of %d;", BuildingVisual.LOOK_NAMES[k].toLowerCase(java.util.Locale.ROOT), drawnLook[k], seen[k]));
            looks &= seen[k] > 0 && drawnLook[k] == seen[k];
        }
        out.printf("      rastered at %d px a plot, the boxes showing their look's own colour:%s%n", px, lw);
        check("each look is drawn on its tiles in its own colours - a pit's benches, a shaft's headframe, a quarry's stone, a leach plant's pond,"
                + " the wash plant's conveyor, a camp's logs, planted forest's crowns, a works' industry", looks);

        // The zones tinted by district, where they are and nowhere else.
        check("with no zone set no district is tinted", m.zoneTint(List.of()).isEmpty());
        double districtSqFt = capacity * BuildingVisual.SQ_FT_PER_PLOT;
        List<CityMap.ZoneArea> zones = List.of(new CityMap.ZoneArea(Sectors.COAL_MINING, 1.5 * districtSqFt),
                new CityMap.ZoneArea(Sectors.FORESTRY, 0.4 * districtSqFt));
        List<CityMap.Tinted> tint = m.zoneTint(zones);
        boolean covers = true, least = true, once = true, firstHolds = true;
        java.util.Set<Long> tinted = new java.util.HashSet<>();
        for (int z = 0; z < zones.size(); z++) {
            double got = 0, smallest = Double.MAX_VALUE;
            // The district holding the most of its sector's ground: the first the map picks for it.
            CityMap.District most = null;
            double mostGround = 0;
            for (CityMap.District dd : m.districts()) {
                double ground = 0;
                for (BuildingVisual.Type ty : types) if (ty != null && zones.get(z).sector().equals(ty.sector())) ground += (double) dd.count(ty.id()) * ty.sqFt();
                if (dd.owned() > 0 && ground > mostGround) { mostGround = ground; most = dd; }
            }
            boolean mostTinted = false;
            for (CityMap.Tinted t : tint) {
                if (t.zone() != z) continue;
                CityMap.District dd = m.districtOfTile(Math.floorDiv(t.x0(), World.TILE), Math.floorDiv(t.y0(), World.TILE));
                once &= tinted.add(t.x0() * 1_000_003L + t.y0());
                mostTinted |= dd == most;
                got += dd.owned() * BuildingVisual.SQ_FT_PER_PLOT;
                smallest = Math.min(smallest, dd.owned() * BuildingVisual.SQ_FT_PER_PLOT);
            }
            firstHolds &= most != null && mostTinted;
            covers &= got >= zones.get(z).sqFt();
            least &= got - smallest < zones.get(z).sqFt();
            out.printf("      zoned for %s, %.1f km2: %d district(s), %.1f km2 of free ground%n", zones.get(z).sector(),
                    zones.get(z).sqFt() / LandManager.SQ_FT_PER_KM2, countZone(tint, z), got / LandManager.SQ_FT_PER_KM2);
        }
        check("each zone's districts cover its ground, and one fewer would not: the tint where a zone is and nowhere else, no district twice",
                covers && least && once && tint.size() == tinted.size());
        check("...the district holding the most of a zone's sector's buildings is the zone's (the map picks where they stand)", firstHolds);
        check("...and each zone in its sector's own colour", LandMap.zoneColour(Sectors.COAL_MINING) != LandMap.zoneColour(Sectors.FORESTRY));

        // The screen over the industry, on the thread's CPU (section 5's clock, A28): within SCREEN_MS.
        CityMap.District at = m.districts().get(0);
        long most = -1;
        for (CityMap.District dd : m.districts()) {
            long g = 0;
            for (BuildingVisual.Type ty : types) if (ty != null && ty.materials()) g += (long) dd.count(ty.id()) * BuildingVisual.cells(ty);
            if (g > most) { most = g; at = dd; }
        }
        long cx = (m.baseDX() + at.dx) * CityMap.TILES_A_SIDE + CityMap.TILES_A_SIDE / 2, cy = (m.baseDY() + at.dy) * CityMap.TILES_A_SIDE + CityMap.TILES_A_SIDE / 2;
        long tx0 = cx - SCREEN_ACROSS / 2, ty0 = cy - SCREEN_DOWN / 2;
        int[] img4 = new int[(World.TILE * PX) * (World.TILE * PX)];
        double best = Double.MAX_VALUE, worstTile = 0;
        long built = 0;
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            long t0 = monthClock();
            double tileWorst = 0;
            built = 0;
            for (int j = 0; j < SCREEN_DOWN; j++) {
                for (int i = 0; i < SCREEN_ACROSS; i++) {
                    long a = monthClock();
                    m.tileInput(tx0 + i, ty0 + j, in);
                    TilePainter.paint(in, p);
                    TileRaster.raster(in, p, PX, img4);
                    tileWorst = Math.max(tileWorst, (monthClock() - a) / 1e6);
                    built += p.buildings;
                }
            }
            double ms = (monthClock() - t0) / 1e6;
            if (round >= WARM_ROUNDS && ms < best) { best = ms; worstTile = tileWorst; }
        }
        out.printf("      the screen over its industry, district (%d, %d): %d tiles, %,d buildings, in %.1f ms of %s (%.3f ms a tile, its slowest %.2f)%n",
                at.dx, at.dy, SCREEN_TILES, built, best, CPU_TIMED ? "the main thread's CPU" : "the wall clock", best / SCREEN_TILES, worstTile);
        check("a 1,389 x 868 L0 screen over the industry paints and rasters in no more than 80 ms of " + (CPU_TIMED ? "the main thread's CPU" : "the wall clock")
                + " - every tile of it well within its 80 ms", Timing.within(best, SCREEN_MS) && Timing.within(worstTile, SCREEN_MS));
    }

    /** How many districts a zone's tint takes. */
    static long countZone(List<CityMap.Tinted> tint, int z) {
        return tint.stream().filter(t -> t.zone() == z).count();
    }

    /** Each site under a works on a map, by its key, and the works' type. */
    static java.util.Map<Long, Integer> sitesUnder(CityMap m) {
        java.util.Map<Long, Integer> out = new java.util.HashMap<>();
        for (CityMap.District d : m.districts()) {
            for (CityMap.DrawnSite s : m.siteList(d.dx, d.dy)) if (s.mine() >= 0) out.put((s.x0() << 32) | (s.y0() & 0xffffffffL), s.mine());
        }
        return out;
    }

    /** The colour a look shows on a box of it rastered (TileRaster's THE MATERIALS AS THEMSELVES): a pit's benches, a shaft's headframe, a quarry's stone, the leach plant's pond, the wash plant's conveyor, a camp's logs, planted forest's crowns, a works' industry fill. */
    static int lookSign(BuildingVisual.Type t) {
        switch (t.look()) {
            case BuildingVisual.PIT:    return BuildingVisual.pitColours(t.site())[1];
            case BuildingVisual.SHAFT:  return TileRaster.HEADFRAME;
            case BuildingVisual.QUARRY: return BuildingVisual.LOOK_FILL[BuildingVisual.QUARRY];
            case BuildingVisual.LEACH:  return TileRaster.POND;
            case BuildingVisual.WASH:   return TileRaster.CONVEYOR;
            case BuildingVisual.CAMP:   return TileRaster.LOGS;
            case BuildingVisual.STAND:  return TileRaster.CROWN;
            default:                    return BuildingVisual.GROUP_FILL[t.group()];
        }
    }

    /**
     * The world cell nearest the founding site's whose square of `side`
     * districts (as CityMap.square() lays it round the cell's middle) is
     * DRY_PLACE dry in its middle 3 x 3 districts and holds fields centred in
     * it with at least `need` sites of each slot: {x, y, rings out}, or the
     * founding site and -1.
     */
    static long[] materialsPlace(World w, int side, int[] need) {
        int n = 3 * CityMap.TILES_A_SIDE;
        byte[] buf = new byte[n * n];
        long sx = Math.floorDiv(w.foundingX(), World.CELL), sy = Math.floorDiv(w.foundingY(), World.CELL);
        for (int ring = 0; ring < MATERIALS_RINGS; ring++) {
            for (long dy = -ring; dy <= ring; dy++) {
                for (long dx = -ring; dx <= ring; dx++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != ring) continue;
                    long cx = (sx + dx) * World.CELL + World.CELL / 2, cy = (sy + dy) * World.CELL + World.CELL / 2;
                    int[] got = slotSites(w, cx, cy, side);
                    boolean enough = true;
                    for (int k = 0; k < need.length; k++) enough &= got[k] >= need[k];
                    if (!enough) continue;
                    w.regionTerrain(cx - 3 * CityMap.DISTRICT / 2, cy - 3 * CityMap.DISTRICT / 2, World.TILE, n, buf);
                    int dry = 0;
                    for (byte b : buf) if (b != World.SALT && b != World.FRESH) dry++;
                    if (dry >= DRY_PLACE * buf.length) return new long[] { cx, cy, ring };
                }
            }
        }
        return new long[] { w.foundingX(), w.foundingY(), -1 };
    }

    /** How far out materialsPlace() looks, in cells: 24. */
    static final int MATERIALS_RINGS = 24;

    /** The sites of each slot of the fields centred in the square of `side` districts CityMap.square() lays round plot (x, y). */
    static int[] slotSites(World w, long x, long y, int side) {
        long bx = Math.floorDiv(x, CityMap.DISTRICT), by = Math.floorDiv(y, CityMap.DISTRICT);
        int h = side / 2;
        long x0 = (bx - h) * CityMap.DISTRICT, x1 = (bx - h + side) * CityMap.DISTRICT;
        long y0 = (by - h) * CityMap.DISTRICT, y1 = (by - h + side) * CityMap.DISTRICT;
        int[] got = new int[CityMap.SLOTS];
        for (int cell : CityLand.cellsUnder(x0, y0, x1 - 1, y1 - 1)) {
            for (Resource r : CityMap.SITED) {
                for (Deposit d : w.fieldsInCell(cell, r)) {
                    if (d.x() < x0 || d.x() >= x1 || d.y() < y0 || d.y() >= y1) continue;
                    int k = CityMap.slotOf(r, d.type());
                    if (k >= 0) got[k] += d.sites();
                }
            }
        }
        return got;
    }
}
