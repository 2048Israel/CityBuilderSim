package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
 *      likewise; and (2b, CL3) a district takes buildings month by month
 *      until its used cells are no more than its room, PLAN_FILL of its
 *      owned plots, and within one building's cells of it, the next going to
 *      the next district; and a spill keeps its time: two districts' inputs
 *      chained by hand, the second carried the pairs {month, type} the first
 *      leaves, and when the first's log gains a later month's arrivals that
 *      overflow too, the second's carry keeps its earlier pairs as they were
 *      and every box the second had placed stands where it stood; and (2d,
 *      FX7) a run laid across a district's buildings - a Rail Depot's yard and
 *      main line at the hub, the yard gone and laid again, Elevated Highways
 *      drawn across them - moves none of them, every box of the plan before
 *      standing in the plan after, and the arrivals after a run stand on none
 *      of its plots;
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
 *      drawn afresh lays them; and (CL3) the district logs ride along: a map
 *      read back plans every district box for box as the map saved did and
 *      its logs are the same record by record, a log that does not add up to
 *      its district's counts is not read (the map is drawn again), and a
 *      FORMAT 8 sidecar, which has no logs, starts each as one batch at the
 *      save's month and plans as today; and at five and ten billion it is
 *      written and read back the same, the log's records counted and the
 *      sidecar within SIDECAR_BYTES, written and read in SIDECAR_MS;
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
 *      moving (H4); every plot of the model's track drawn or counted short
 *      (since FR7 slice 3, in the surplus no train's way uses),
 *      one railway network; and its Rail Terminals as yards on the track
 *      in the cells nearest the mines - and (0.7.98, batch O14) the runs'
 *      ramps and 45-degree bands reach the screen: every tile they mark
 *      rastered through the view's own path as the model rasters it. On
 *      0.7.86's painter the network's checks fail on Jerus's save and the
 *      playtest (runs/fixRD2-notes.md): N3's passed because they measured
 *      the deal's x 1 fixture. And (batch FR7, 8b and 8c) on his city x 1
 *      where its square holds iron: with highways and Truck Depots added,
 *      every interchange reserve laid is its side's square, clear of
 *      buildings, streets and track, on owned dry ground, a crossing with
 *      none short of ground; every yard, Truck Depot and sea terminal with a
 *      grid to go to has an access road from beside its box to a ramp's
 *      street or an arterial, and with no highway to an arterial; with Rail
 *      Spurs and bulk works added, the spurs the runs' sidings left are dealt
 *      to districts the main line crosses, no more than their bulk works,
 *      the plans' sidings painted as track counted apart from the runs', the
 *      legend's surplus less them, the deal the same from the same counts;
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
 *      Since FX5 (0.8.18) the trains on the curves (trainsOnTheCurves()): no
 *      two neighbouring vehicles of a train differ in heading by more than
 *      MOST_TURN_DEG, and every vehicle shown whole is on the rail the tiles
 *      draw at 8 px a plot.
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
 *  16. (batch CL4) THE AVENUES DRAWN: the four 45-degree avenues the district plans lay are drawn as smooth bands - on the dense city
 *      along every arm's line between two open cells, across the tile edges and the district seams, every sample the street's colour
 *      and the band's two sides as far out as the street is wide (no gap, no step); a tile rastered twice, from the view's kept copy
 *      (MapTiles.copy, its stamp moving with the marks) and from a twin map built alike, the same pixels; no building under a staircase
 *      or an outer plot, no building's colour on the line; on hand-built tiles a staircase alone no junction, an avenue across an
 *      arterial a signal (the avenue's two directions among the roads), through a cell's corner and at the origin with a street along
 *      each axis one ring, the origin's four arms alone a signal; and the dense screen at the founding site, the arms across it,
 *      within SCREEN_MS.
 *  17. (batch CL4, slice D) THE CURVES DRAWN: the crescents and the streets along a cut the district plans lay as curves are drawn as
 *      smooth bands - on the dense city's far districts (three to eight east and south of the founding site) along every curve's line
 *      from end to end, at 4 px a plot and at 8, every sample the street's colour and no building's, the band's middle within a pixel
 *      of the line and the band as wide as its street (no staircase step, across the tile edges) and its turn between samples no
 *      sharper than the line's; a tile rastered twice, from the view's kept copy (MapTiles.copy, its stamp moving with the marks) and
 *      from a twin map built alike, the same pixels; no building under a plot a curve is laid on or reaches, none changed in a
 *      pixel by the curve; on hand-built tiles a crescent alone no junction, the street that crosses it running on across the band,
 *      a crossing beside it the signal it was; the curves' bytes in the plans' Drawn a small share; and the screen over the far
 *      districts, curves across it, within SCREEN_MS.
 *  18. (batch UP3) A MONTH REDRAWS ONLY WHAT MOVED: on Jerus's city x 1, at his own district count (x 80), at 5B and on the played city, a month
 *      grown as the game grows one and the screens drawn as ui/MiniMap and ui/MapView draw them, the minimap's stamp and preview from their kept
 *      nodes (MapTiles.KeptNode) equal a full redraw pixel for pixel and ask the same districts for their pictures; a far node whose key
 *      (MapTiles.nodeKey()) did not move holds the pixels a fresh one gives; a month that places in one district moves the keys of the far nodes
 *      over it and its neighbours and no more, fewer than today's every node in view (5B with no pictures; x 80 with them, where the nodes
 *      over the districts whose pictures the chain re-plans move too, and no others); the stamp's runs
 *      layer is drawn again in the months the runs moved and in no other, its land layer by a land purchase and by no other month; every link's
 *      kept stamp is the one worked out afresh after each of 24 months (x 1, x 80); and a month's FX-side map work at 5B - the stamp, the far
 *      view's screen, an L0 screen re-stamped and the chain's handing out and adopting - is within MAP_MONTH_MS, the median month.
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
        Path root = TempDirs.create("mapcheck");
        Game city = playedCity(root);
        nothingMoves(city);
        districtsHoldAndSpill(city);
        districtBuysLandInside(city);
        runsLaidAcross(city);
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
        pictureRelief(sq.types);
        miniMap(city, sq);
        globeOnScreen(sq);
        buyingFromTheMap(city);
        materials(sq.seed);
        avenuesDrawn(sq);
        curvesDrawn(sq);
        redrawsOnlyWhatMoved(city, sq);
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
        /** The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay - since FR7 slice 3 the plots no train's way uses (CityRuns.trackSurplus()); buildings packed at the city's edge (R7); tiles painted; districts. */
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
       2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3)
       ===================================================================== */

    /*
     * CL3 gave each district a log of its count changes, month by month, and a
     * room of PLAN_FILL of its owned plots (a plan draws no more than that):
     * 2b is the map's side of it - the districts fill to their room and spill
     * to the next - and the plan's side of the spill, two inputs chained by
     * hand as the map's chain chains them (PlanJob: the carry of the district
     * before is {month, type} pairs, the month each came in). Section 4 holds
     * the log across a save and a load.
     */

    /** The cells across the window of whole cells a spill's first district owns and its second: 2 and 6 of a district's 8 - the first draws little, the second all it is carried. */
    static final int SPILL_FIRST_CELLS = 2, SPILL_SECOND_CELLS = 6;

    static void districtsHoldAndSpill(Game g) {
        out.println("\n--- 2b. a district is full at what its plan can draw (PLAN_FILL), and a spill keeps its time (CL3) ---");
        BuildingVisual.Type[] types = g.getMapTypes();
        holdsWhatItCanDraw(types, g.getBuildingManager().getTemplateByName("Low-Rise Apartments").getId());
        spillKeepsItsTime(types, g.getBuildingManager().getTemplateByName("House").getId(),
                g.getBuildingManager().getTemplateByName("Convenience Store").getId());
    }

    /**
     * A district holds what its plan can draw: on a square city of four districts, a few apartment blocks added month by month (reconcile) until the
     * nearest district stops taking them. Its used cells are no more than its room, floor(owned x PLAN_FILL), and within one block's cells of it
     * (the next would not have fitted); the next block went to the next district, and it takes no more after.
     */
    static void holdsWhatItCanDraw(BuildingVisual.Type[] types, int flats) {
        int each = BuildingVisual.cells(types[flats]);
        // A capacity whose room is no whole number of the blocks, so "within one building's cells" is a remainder to see, not a tie.
        int capacity = 1000;
        while ((long) Math.floor(capacity * CityMap.PLAN_FILL) % each == 0) capacity++;
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] place = dryPlace(World.of(seed));
        CityMap m = CityMap.square(seed, place[0], place[1], types, 2, capacity, new long[types.length]);
        List<CityMap.District> ds = m.districts();
        CityMap.District first = ds.get(0), second = ds.get(1), third = ds.get(2);
        long[] model = new long[types.length];
        int step = 5, month = 10;
        boolean reconciled = true;
        int guard = (int) (first.room() / each / step) + 10;
        while (second.count(flats) == 0 && guard-- > 0) {
            model[flats] += step;
            reconciled &= m.reconcile(model, month++);
        }
        int spillMonth = month - 1;
        long room = first.room(), used = first.usedCells();
        out.printf("      %d blocks of %d cells added %d a month to a square of 4 districts of %d plots: the first holds %d (%,d of its room of %,d cells,"
                + " %,d to spare), the next from month %d%n", model[flats], each, step, capacity, first.count(flats), used, room, room - used, spillMonth);
        check("fixture: a room that is no whole number of blocks (floor(owned x PLAN_FILL) = " + room + ", " + each + " cells a block), and a month's reconcile that took",
                room % each != 0 && first.owned() == capacity && reconciled);
        check("a district takes buildings month by month until it is full: the first stops taking them and the next is given them", second.count(flats) > 0
                && first.count(flats) > 0);
        check("...its used cells no more than its room, floor(owned x PLAN_FILL) of its owned plots (and so short of the plots it owns)",
                room == (long) Math.floor(first.owned() * CityMap.PLAN_FILL) && used <= room && used <= first.owned() * CityMap.PLAN_FILL && used < first.owned());
        check("...and within one building's cells of its room: the next block would not have fitted", room - used < each && used == first.count(flats) * (long) each
                && first.freeCells() == room - used);
        check("...the next went to the next district, from the month the first was full, and to no district beyond it", second.logSize() > 0
                && second.logMonth[0] == spillMonth && third.count(flats) == 0 && first.logMonth[first.logSize() - 1] <= spillMonth);
        int held = first.count(flats), given = second.count(flats);
        model[flats] += step;
        reconciled = m.reconcile(model, month++);
        check("...and the first takes no more after: the month after, all of it is the next's", reconciled && first.count(flats) == held
                && second.count(flats) == given + step && first.usedCells() == used);
        check("...every district's log sums to its counts, the blocks' records one to a district and month", logsAddUp(m));
    }

    /**
     * A spill keeps its time, at the plan's level: the first district's input is given more than its window of ground can draw, the second's carry is the
     * pairs the first leaves ({month, type}, the month each came in) - and when the first's log gains a later month's arrivals that overflow too, the
     * second's carry keeps the pairs it had, as they were, a prefix, and every box the second had placed stands where it stood.
     */
    static void spillKeepsItsTime(BuildingVisual.Type[] types, int house, int store) {
        // What the first district can draw: houses given it, doubling, till some are left without a place.
        int n = 2000, capacity;
        while (true) {
            DistrictPlan p = DistrictPlan.make(spillInput(types, SPILL_FIRST_CELLS, new int[][] { { 1, house, n } }, 1, null));
            capacity = p.buildings;
            if (p.overflowCount() > 0 || n > 200_000) break;
            n *= 2;
        }
        int c = capacity;
        // Months 1 and 2 (.6 and .25 of it) it draws; month 3 (.43) fills it and some are left over; month 4 (.25) is all over; month 5 is the later one.
        int[][] early = { { 1, house, c / 2 }, { 1, store, c / 10 }, { 2, house, c / 4 } };
        int[][] over = { { 3, house, c / 3 }, { 3, store, c / 10 }, { 4, house, c / 4 } };
        int[][] later = { { 5, house, c / 4 }, { 5, store, c / 10 } };
        int[][] log = java.util.stream.Stream.concat(Arrays.stream(early), Arrays.stream(over)).toArray(int[][]::new);
        int[][] logLater = java.util.stream.Stream.concat(Arrays.stream(log), Arrays.stream(later)).toArray(int[][]::new);
        int firstBatch = 2;
        DistrictPlan drew = DistrictPlan.make(spillInput(types, SPILL_FIRST_CELLS, early, firstBatch, null));
        DistrictPlan a1 = DistrictPlan.make(spillInput(types, SPILL_FIRST_CELLS, log, firstBatch, null));
        DistrictPlan a2 = DistrictPlan.make(spillInput(types, SPILL_FIRST_CELLS, logLater, firstBatch, null));
        int[] carry1 = a1.overflowLog, carry2 = a2.overflowLog;
        // The second district: its own two months of houses, then what it is carried.
        int[][] own = { { 1, house, c / 8 }, { 2, house, c / 8 } };
        int ownCount = 2 * (c / 8);
        DistrictPlan b1 = DistrictPlan.make(spillInput(types, SPILL_SECOND_CELLS, own, 1, carry1));
        DistrictPlan b2 = DistrictPlan.make(spillInput(types, SPILL_SECOND_CELLS, own, 1, carry2));
        java.util.TreeSet<Integer> months1 = new java.util.TreeSet<>(), months2 = new java.util.TreeSet<>();
        for (int k = 0; carry1 != null && k < carry1.length; k += 2) months1.add(carry1[k]);
        for (int k = 0; carry2 != null && k < carry2.length; k += 2) months2.add(carry2[k]);
        out.printf("      the first district draws %,d of the %,d it is given (%,d of months 1 and 2 whole), leaves %,d to the next, from months %s; with month 5's, %,d of %,d, from months %s;"
                + " the second places %,d (its own %,d and the carry), then %,d%n", a1.buildings, a1.buildings + a1.overflowCount(), drew.buildings, a1.overflowCount(), months1,
                a2.overflowCount(), a2.buildings + a2.overflowCount(), months2, b1.buildings, ownCount, b2.buildings);
        check("fixture: the first district draws what months 1 and 2 bring it whole, and leaves some of what months 3 and 4 bring without a place",
                drew.overflowCount() == 0 && a1.overflowCount() > 0 && carry1 != null);
        check("...and more with month 5's, which overflows too", a2.overflowCount() > a1.overflowCount() && carry2 != null);
        check("fixture: the second district draws all it is carried, own and carried, in the first case and the second", b1.overflowCount() == 0
                && b2.overflowCount() == 0 && carry1 != null && b1.buildings == ownCount + a1.overflowCount() && b2.buildings == ownCount + a2.overflowCount());
        check("the spill is a {month, type} pair to each building left without a place, in the months they came in (3 and 4), not the month of the last",
                carry1 != null && carry1.length == 2 * a1.overflowCount() && months1.equals(new java.util.TreeSet<>(List.of(3, 4))));
        boolean prefix = carry1 != null && carry2 != null && carry2.length > carry1.length
                && Arrays.equals(Arrays.copyOf(carry2, carry1.length), carry1);
        boolean added5 = prefix;
        for (int k = carry1 == null ? 0 : carry1.length; prefix && k < carry2.length; k += 2) added5 &= carry2[k] == 5;
        check("a later month's arrivals that also overflow add to the spill at its end: the second's carry keeps the pairs it had, as they were (a prefix)", prefix);
        check("...and the pairs added are the later month's (5)", added5);
        boolean stand = b2.buildings >= b1.buildings;
        for (int i = 0; stand && i < b1.buildings; i++) {
            stand = b1.btype[i] == b2.btype[i] && b1.bx[i] == b2.bx[i] && b1.by[i] == b2.by[i] && b1.bw[i] == b2.bw[i] && b1.bh[i] == b2.bh[i];
        }
        check("every box the second district had placed stands where it stood (" + b1.buildings + " of them)", stand && b1.buildings > 0);
        check("...and it places the later month's arrivals besides", carry1 != null && carry2 != null && b2.buildings - b1.buildings == (carry2.length - carry1.length) / 2);
    }

    /**
     * A hand-made district for 2b's spill: flat ground, a square window of whole cells round the hub owned (the rest not), its types, its log
     * (records {month, type, change}, the first `firstBatch` one batch; its counts the log's sums) and the carry in ({month, type} pairs), copied.
     */
    static DistrictPlan.Input spillInput(BuildingVisual.Type[] types, int cellsAcross, int[][] records, int firstBatch, int[] carry) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        in.types = types;
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = in.hubY = DistrictPlan.FRAME / 2;
        int lo = (DistrictPlan.CELLS_A_SIDE - cellsAcross) / 2 * DistrictPlan.CELL, hi = lo + cellsAcross * DistrictPlan.CELL;
        for (int y = lo; y < hi; y++) for (int x = lo; x < hi; x++) in.owned[y * DistrictPlan.FRAME + x] = true;
        in.counts = new int[types.length];
        in.logMonth = new int[records.length];
        in.logType = new int[records.length];
        in.logChange = new int[records.length];
        for (int k = 0; k < records.length; k++) {
            in.logMonth[k] = records[k][0];
            in.logType[k] = records[k][1];
            in.logChange[k] = records[k][2];
            in.counts[records[k][1]] += records[k][2];
        }
        in.firstBatch = firstBatch;
        in.carry = carry == null ? null : carry.clone();
        return in;
    }

    /* =====================================================================
       2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6)
       ===================================================================== */

    /*
     * CL3 made a building stay where it was put: a district keeps a log of its arrivals by month and its plan replays the log on the district's
     * ground. But the plan read the ground as the district owns it today, so land the city bought INSIDE a district that already had buildings
     * made the plan replay every arrival on the larger ground: the cells' growth order changed and most of the district's buildings moved (on the
     * playtest's city at month 3,998, district (-2,-2) bought 8 whole cells and 2,069 of its 2,946 buildings moved). What is right is the
     * other way: land bought inside a district, full or not, adds room and never re-places what already stands; only the arrivals after it go
     * on the new ground. 2c is that, on the real map and the real land: the played city's ground (a copy of it, its own purchases dated before the
     * fixture's months), a block bought in the next district so that a full one has somewhere to send its houses, the district that holds the
     * founding site given houses month by month, and a block of whole land blocks bought inside its frame as near the site as one can be - first
     * while it has room to spare, then when it is full and takes no more - the plan drawn before each purchase and after the month that follows
     * it, every box of the one against the other. The fixture's checks hold on any code; the finding's
     * hold once the plan grows its ground month by month from the dated purchases (CityLand.purchases()), the arrivals of each month on the ground as that
     * month had it.
     */

    /** The side of a bought block, at least: 64 plots (rounded up to whole blocks of the land's level), a few cells of a district's 8 across. */
    static final int BOUGHT_PLOTS = 64;

    /** One building of a plan: its type and its box in the frame's plots. */
    record PlanBox(int type, int x, int y, int w, int h) { }

    /** A rectangle of plots bought inside a district, [x0, x1) x [y0, y1) in the world, and how many of its plots are dry. */
    record Bought(long x0, long y0, long x1, long y1, int dry) {
        /** How far its nearest plot is from plot (sx, sy), in plots. */
        double nearest(long sx, long sy) {
            return Math.hypot(Math.max(Math.max(x0 - sx, 0), sx - (x1 - 1)), Math.max(Math.max(y0 - sy, 0), sy - (y1 - 1)));
        }

        /** Whether the box lies wholly on this block, a box in the frame whose first plot is (fx, fy) in the world. */
        boolean holds(PlanBox b, long fx, long fy) {
            return fx + b.x() >= x0 && fy + b.y() >= y0 && fx + b.x() + b.w() <= x1 && fy + b.y() + b.h() <= y1;
        }
    }

    /** A harness city's months: houses added to the model a month at a time and the map reconciled to them, each in its month. */
    static final class Months {
        final CityMap map;
        final long[] model;
        final int house;
        /** The month the next one is run in. */
        int month;
        boolean reconciled = true;

        Months(CityMap map, long[] model, int house, int month) {
            this.map = map;
            this.model = model;
            this.house = house;
            this.month = month;
        }

        /** The month that last ran: what a purchase made between months is dated. */
        int last() { return month - 1; }

        /** One month of `houses` more houses. */
        void run(int houses) {
            model[house] += houses;
            reconciled &= map.reconcile(model, month++);
        }
    }

    /**
     * Whether a district that buys land inside itself keeps its buildings where they stand (batch FX6): on the played city's ground, a block is bought in
     * the next district, and the district holding the founding site is given houses month by month; with a third of its room used a block is bought inside
     * its frame (the plan's boxes before it and after the month that follows), then more houses until it is full and takes no more, a second block inside
     * the frame (the plan's boxes before it, and after six months of houses). The district's plan is not kept: it is drawn from the map at each of the four moments.
     */
    static void districtBuysLandInside(Game g) {
        out.println("\n--- 2c. a district that buys land inside itself keeps its buildings where they stand (FX6) ---");
        BuildingVisual.Type[] types = g.getMapTypes();
        int house = g.getBuildingManager().getTemplateByName("House").getId();
        int each = BuildingVisual.cells(types[house]);
        // The played city's ground, a copy, drawn canonically with nothing built; the fixture's months follow the played city's own, so every purchase it made is before them.
        CityLand land = g.getCityLand().copy();
        long[] model = new long[types.length];
        CityMap m = CityMap.canonical(land, r -> land.amountsInOrder(r), types, new long[types.length], g.getMonth());
        Months h = new Months(m, model, house, g.getMonth() + 1);
        int firstMonth = h.month;
        // The district whose frame holds the founding site, and its ground (a plan's input reads it from the world).
        CityMap.District d = null;
        DistrictPlan.Input frame = null;
        for (CityMap.District e : m.districts()) {
            DistrictPlan.Input in = m.planInput(e);
            if (land.siteX() >= in.x0 && land.siteX() < in.x0 + DistrictPlan.SIDE && land.siteY() >= in.y0 && land.siteY() < in.y0 + DistrictPlan.SIDE) {
                d = e;
                frame = in;
            }
        }
        // A district beside it with room to spare, so that when it is full the next houses have somewhere to go (a city whose every district is full gives them to the first):
        // a block bought in the nearest other district that has one, between the played city's months and the fixture's first.
        CityMap.District other = null;
        DistrictPlan.Input otherFrame = null;
        Bought spill = null;
        for (CityMap.District e : m.districts()) {
            if (e == d || spill != null) continue;
            otherFrame = m.planInput(e);
            spill = nearestFreeBlock(land, otherFrame);
            other = spill == null ? null : e;
        }
        boolean soundSpill = spill != null && soundBlock(land, otherFrame, spill);
        int otherOwned0 = other == null ? 0 : other.owned();
        if (spill != null) land.extend(new LandParcel(-5, 1, 0, land.level(), spill.x0(), spill.y0(), spill.x1(), spill.y1(), null, null, null, 0, 0), h.last(), 0);
        long blocks = 1L << land.level();
        int step = Math.max(2, (int) (d.room() / each / 24));
        int ownedAtFirst = d.owned();
        long roomAtFirst = d.room();
        int guard = 400;

        // ----- S1: a third of the room used, room left -----
        while (d.usedCells() * 3 < d.room() && guard-- > 0) h.run(step);
        int month1 = h.last(), count1 = d.count(house);
        boolean spillRoom = other != null && other.owned() - otherOwned0 == spill.dry() && other.freeCells() > 0;
        long free1 = d.freeCells();
        DistrictPlan p1 = m.plan(d);
        java.util.Set<PlanBox> s1 = boxesOf(p1);

        // ----- the first block, bought between months, and the month after -----
        Bought b1 = nearestFreeBlock(land, frame);
        boolean sound1 = b1 != null && soundBlock(land, frame, b1);
        int ownedBefore1 = d.owned();
        int boughtMonth1 = h.last();
        if (b1 != null) land.extend(new LandParcel(-5, 1, 0, land.level(), b1.x0(), b1.y0(), b1.x1(), b1.y1(), null, null, null, 0, 0), boughtMonth1, 0);
        boolean dated1 = land.purchases().get(land.purchases().size() - 1).month() == boughtMonth1 && boughtMonth1 > g.getMonth();
        h.run(step);
        int ownedAfter1 = d.owned(), count2 = d.count(house);
        DistrictPlan p2 = m.plan(d);
        java.util.Set<PlanBox> s2 = boxesOf(p2);
        int moved12 = movedOf(s1, s2);

        // ----- until it is full, and a month in which it takes none -----
        while (d.freeCells() >= each && guard-- > 0) h.run(step);
        int heldFull = d.count(house), elsewhere = housesElsewhere(m, d, house);
        h.run(step);
        int month3 = h.last(), count3 = d.count(house);
        boolean fullTakesNone = count3 == heldFull && housesElsewhere(m, d, house) > elsewhere;
        long free3 = d.freeCells();
        DistrictPlan p3 = m.plan(d);
        java.util.Set<PlanBox> s3 = boxesOf(p3);

        // ----- the second block, and six months of houses -----
        Bought b2 = nearestFreeBlock(land, frame);
        boolean sound2 = b2 != null && soundBlock(land, frame, b2);
        int ownedBefore2 = d.owned();
        int boughtMonth2 = h.last();
        if (b2 != null) land.extend(new LandParcel(-5, 1, 0, land.level(), b2.x0(), b2.y0(), b2.x1(), b2.y1(), null, null, null, 0, 0), boughtMonth2, 0);
        boolean dated2 = land.purchases().get(land.purchases().size() - 1).month() == boughtMonth2 && boughtMonth2 > boughtMonth1;
        for (int k = 0; k < 6; k++) h.run(step);
        int ownedAfter2 = d.owned(), count4 = d.count(house);
        DistrictPlan p4 = m.plan(d);
        java.util.Set<PlanBox> s4 = boxesOf(p4);
        int moved34 = movedOf(s3, s4);
        int onNew = 0;
        if (b2 != null) {
            for (PlanBox b : s4) if (!s3.contains(b) && b2.holds(b, frame.x0, frame.y0)) onNew++;
        }

        out.printf("      the founding site's district (%d,%d): %,d owned plots, a room of %,d cells (a house takes %d), %d houses a month from month %d; the land's blocks are %d plots,"
                + " a bought block %d across; the block beside, in district (%d,%d), %,d dry plots, %.0f plots from the site; the first block %.0f, the second %.0f%n", d.dx, d.dy, ownedAtFirst, roomAtFirst, each,
                step, firstMonth, blocks, b1 == null ? 0 : b1.x1() - b1.x0(), other == null ? 0 : other.dx, other == null ? 0 : other.dy, spill == null ? 0 : spill.dry(),
                spill == null ? -1 : spill.nearest(land.siteX(), land.siteY()), b1 == null ? -1 : b1.nearest(land.siteX(), land.siteY()),
                b2 == null ? -1 : b2.nearest(land.siteX(), land.siteY()));
        out.printf("      S1 (month %d): %,d houses, %,d cells free, the plan places %,d; first block %s bought in month %d, %,d dry plots (owned %,d -> %,d);"
                + " S2: %,d houses, the plan places %,d, %,d of S1's moved%n", month1, count1, free1, p1.buildings, b1 == null ? "none" : "[" + b1.x0() + "," + b1.x1() + ") x ["
                + b1.y0() + "," + b1.y1() + ")", boughtMonth1, b1 == null ? 0 : b1.dry(), ownedBefore1, ownedAfter1, count2, p2.buildings, moved12);
        out.printf("      S3 (month %d, full): %,d houses, %,d cells free, the plan places %,d; second block %s bought in month %d, %,d dry plots (owned %,d -> %,d);"
                + " S4 (month %d): %,d houses, the plan places %,d, %,d of S3's %,d moved, %,d new ones on the second block%n", month3, count3, free3, p3.buildings,
                b2 == null ? "none" : "[" + b2.x0() + "," + b2.x1() + ") x [" + b2.y0() + "," + b2.y1() + ")", boughtMonth2, b2 == null ? 0 : b2.dry(), ownedBefore2,
                ownedAfter2, h.last(), count4, p4.buildings, moved34, s3.size(), onNew);

        check("fixture: the founding site's district found, and the land's blocks smaller than a district, so a bought block fits in one",
                d != null && frame != null && blocks < World.DISTRICT && h.reconciled);
        check("fixture: another district has a block bought in it, of whole blocks, wholly unowned, at least half dry, and room for the houses a full district cannot take", soundSpill && spillRoom);
        check("fixture: the district holds some houses and has room to spare, and its plan places every one of them",
                count1 > 0 && free1 >= each && s1.size() == count1 && p1.overflowCount() == 0);
        check("fixture: the first block is of whole blocks of the land's level, inside the frame, wholly unowned, at least half dry", sound1);
        check("fixture: the first block, bought between months (dated the month that ran), adds its dry plots to the district's owned ground and the month after it takes houses",
                b1 != null && dated1 && ownedAfter1 - ownedBefore1 == b1.dry() && count2 > count1 && s2.size() > s1.size());
        check("a district that is not full and buys land inside itself keeps what stands where it stands: every box of the plan before stands in the plan after (" + s1.size() + " of them, "
                + moved12 + " moved)", moved12 == 0 && s1.size() > 0);
        check("fixture: the district is full - it takes no house in the month that follows, another district takes them all", free3 < each && fullTakesNone && s3.size() > s2.size());
        check("fixture: the second block is of whole blocks of the land's level, inside the frame, wholly unowned, at least half dry", sound2);
        check("fixture: the second block, bought between months, adds its dry plots to the district's owned ground, and the full district takes houses again (the room the land added)",
                b2 != null && dated2 && ownedAfter2 - ownedBefore2 == b2.dry() && count4 > count3 && ownedAfter2 > ownedBefore2);
        check("a district that is full and buys land inside itself keeps what stands where it stands: every box of the plan before stands in the plan after (" + s3.size() + " of them, "
                + moved34 + " moved)", moved34 == 0 && s3.size() > 0);
        check("...and holds more after it: the houses that came after the purchase are placed (" + s4.size() + " boxes, " + s3.size() + " before)", s4.size() > s3.size());
        check("...the arrivals after the purchase go on the new ground: " + onNew + " of the new boxes lie wholly on the second block", onNew > 0);
        check("...every district's log still sums to its counts, the blocks' records one to a district and month", logsAddUp(m));
    }

    /** The plan's buildings as boxes, a set: a building stands where it did when its box is in the plan again. */
    static java.util.Set<PlanBox> boxesOf(DistrictPlan p) {
        java.util.Set<PlanBox> set = new java.util.HashSet<>();
        for (int i = 0; i < p.buildings; i++) set.add(new PlanBox(p.btype[i], p.bx[i], p.by[i], p.bw[i], p.bh[i]));
        return set;
    }

    /** How many of the boxes before are not among the boxes after: the buildings that moved. */
    static int movedOf(java.util.Set<PlanBox> before, java.util.Set<PlanBox> after) {
        int n = 0;
        for (PlanBox b : before) if (!after.contains(b)) n++;
        return n;
    }

    /** The houses the map holds in the districts but d. */
    static int housesElsewhere(CityMap m, CityMap.District d, int house) {
        int n = 0;
        for (CityMap.District e : m.districts()) if (e != d) n += e.count(house);
        return n;
    }

    /**
     * The block of whole land blocks (BOUGHT_PLOTS a side at the least, rounded up to the level's blocks) the city could buy inside a district's frame
     * (not on its last row or column) that lies wholly unowned, is at least half dry, and is nearest the founding site - its nearest plot, then its middle
     * - so it lands early in the plan's growth order. Null when there is none.
     */
    static Bought nearestFreeBlock(CityLand land, DistrictPlan.Input frame) {
        long b = 1L << land.level(), side = (BOUGHT_PLOTS + b - 1) / b * b;
        long sx = land.siteX(), sy = land.siteY();
        Bought best = null;
        double bestNear = 0, bestMid = 0;
        for (long y0 = Math.floorDiv(frame.y0 + b - 1, b) * b; y0 + side <= frame.y0 + DistrictPlan.SIDE; y0 += b) {
            for (long x0 = Math.floorDiv(frame.x0 + b - 1, b) * b; x0 + side <= frame.x0 + DistrictPlan.SIDE; x0 += b) {
                long x1 = x0 + side, y1 = y0 + side;
                if (land.grid().owned(x0, y0, x1, y1) != 0) continue;
                int dry = dryPlots(frame, x0, y0, x1, y1);
                if (2L * dry < side * side) continue;
                Bought c = new Bought(x0, y0, x1, y1, dry);
                double near = c.nearest(sx, sy), mid = Math.hypot((x0 + x1) / 2.0 - sx, (y0 + y1) / 2.0 - sy);
                if (best == null || near < bestNear || near == bestNear && mid < bestMid) {
                    best = c;
                    bestNear = near;
                    bestMid = mid;
                }
            }
        }
        return best;
    }

    /** The dry plots of [x0, x1) x [y0, y1) in the world, read from a plan input's ground (the world's, over the frame). */
    static int dryPlots(DistrictPlan.Input frame, long x0, long y0, long x1, long y1) {
        int n = 0;
        for (long y = y0; y < y1; y++) {
            for (long x = x0; x < x1; x++) if (World.dry(frame.terrain[(int) ((y - frame.y0) * DistrictPlan.FRAME + (x - frame.x0))])) n++;
        }
        return n;
    }

    /** Whether a block about to be bought is what 2c says it is, checked on the land as it stands: whole blocks of its level, inside the frame, wholly unowned, at least half dry. */
    static boolean soundBlock(CityLand land, DistrictPlan.Input frame, Bought b) {
        long blocks = 1L << land.level();
        boolean whole = b.x0() % blocks == 0 && b.y0() % blocks == 0 && b.x1() % blocks == 0 && b.y1() % blocks == 0
                && (b.x1() - b.x0()) >= BOUGHT_PLOTS && (b.y1() - b.y0()) >= BOUGHT_PLOTS;
        boolean inside = b.x0() >= frame.x0 && b.y0() >= frame.y0 && b.x1() <= frame.x0 + DistrictPlan.SIDE && b.y1() <= frame.y0 + DistrictPlan.SIDE;
        boolean free = land.grid().owned(b.x0(), b.y0(), b.x1(), b.y1()) == 0;
        boolean dry = b.dry() == dryPlots(frame, b.x0(), b.y0(), b.x1(), b.y1()) && 2L * b.dry() >= (b.x1() - b.x0()) * (b.y1() - b.y0());
        return whole && inside && free && dry;
    }

    /* =====================================================================
       2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7)
       ===================================================================== */

    /*
     * FX6 dated the plan's ground, so land bought later only adds room. The plan's other input from the city was still read as it stands today for
     * the plan's whole history: Input.fixed, the city's highways, railway, rail yards and interchange reserves (CityRuns, laid month by month by
     * CityMap.layRuns() inside reconcile), and the shore's works. So when the runs lay something new across a district that already has buildings -
     * a Rail Depot's yard at the rail hub, a highway arm growing through, the highways first drawn once they reach a street - the plan replays every
     * arrival since its log began round the new plots: the growth order, the cells' free rows and the first fits all change, and most of the
     * district's buildings move (on the default playtest the founding district's Rail Depot yard, 8 x 7 plots, came and went a dozen times
     * between months 1,128 and 3,934, each time moving 100 to 260 of its 300 boxes; the highway plots laid at months 2,131 and 2,485 to 2,834 moved
     * 10 to 100 each; the last month's fixed swapped into this month's input removed every move). What is right is the other way, as for the
     * ground: what the plan placed stands, and a run laid later takes only the ground nothing stands on. 2d is that, on the real map and the real
     * land: the played city's ground (a copy of it, nothing built, nothing mined), the district that holds the founding site given houses and a few
     * buildings of several sizes a month until it uses half its room, then a Rail Depot (the runs start the railway at its hub, the lattice crossing
     * nearest the site when nothing is mined, and lay the depot's yard and the main line there), more months, the depot gone, more months, the depot
     * again, more months, then Elevated Highways until some are laid in the district - the plan drawn before each change and after the month of it,
     * every box of the one against the other (printed beside it, the plan made with the earlier month's fixed ground in the new input, which moved none
     * when the finding was made: the run is what moves them). A highway rides the plan's street line, so on this city it takes no box's own plots; what it takes is the
     * ground beside them (no building touches a highway plot, corners included), and those boxes move too. The fixture's checks hold on any code;
     * the finding's hold once the plan dates the runs as it dates the ground.
     */

    /** What the founding district is given month by month besides houses, in turn (an apartment block, shops, a school, works): buildings of several sizes for its plan to place. */
    static final String[] MIX = { "Low-Rise Apartments", "Small Grocery Store", "Bakery", "Elementary School", "Industrial Bakery", "Bottling Plant", "Snack & Oils Plant" };

    /** How much of its room the founding district is brought to before a run is laid across it: half of the cells its plan may fill (CityMap.PLAN_FILL of its owned plots). */
    static final double USED_SHARE = 0.5;

    /** The buildings of the mix the founding district is given each month, beside its houses. */
    static final int BUILDINGS_A_MONTH = 3;

    /** Months of arrivals after each change of the runs, before the next plan is drawn: the buildings that must go on the ground as the run left it. */
    static final int MONTHS_BETWEEN = 4;

    /** The most months the highways are given to reach the district, and the Elevated Highways added a month while they do. */
    static final int HIGHWAY_MONTHS = 60, HIGHWAY_STEP = 8;

    /** A building keeps this far from a highway plot (batch H5: no building touches one, corners included): the reach, in plots, within which a highway laid takes a box's place. */
    static final int HIGHWAY_CLEAR = 1;

    /** The arrivals of the founding district, month by month: `step` houses and BUILDINGS_A_MONTH buildings of the mix, in turn. */
    static final class Arrivals {
        final Months h;
        final int[] mix;
        final int step;
        int next;

        Arrivals(Months h, int[] mix, int step) {
            this.h = h;
            this.mix = mix;
            this.step = step;
        }

        /** One month: the mix's next buildings and the houses. */
        void month() {
            for (int k = 0; k < BUILDINGS_A_MONTH; k++) h.model[mix[next++ % mix.length]]++;
            h.run(step);
        }

        /** `n` months. */
        void months(int n) {
            for (int i = 0; i < n; i++) month();
        }
    }

    /** The founding district's plan at one moment: its boxes, the plots its input fixes, the city's runs alone in its frame, and the ramps. */
    static final class Snap {
        final DistrictPlan plan;
        final java.util.Set<PlanBox> boxes;
        final byte[] fixed, runs;
        final boolean[] ramp;
        /** The map's month when the snapshot was taken, and how many records the district's journal of the runs held then. */
        final int month, journal;

        Snap(CityMap m, CityMap.District d) {
            DistrictPlan.Input in = m.planInput(d);
            month = m.month();
            journal = d.journalSize();
            fixed = in.fixed.clone();
            ramp = in.ramp == null ? null : in.ramp.clone();
            runs = new byte[DistrictPlan.AREA];
            m.runsInto(d, runs);
            plan = DistrictPlan.make(in);
            boxes = boxesOf(plan);
        }

        /** How many of this plan's boxes have a plot within `reach` plots of a plot the runs hold in `later` and did not hold here: the buildings a run laid in between lies across. */
        int under(Snap later, int reach) {
            int n = 0;
            for (PlanBox b : boxes) if (anyPlot(b, reach, p -> later.runs[p] != 0 && runs[p] == 0)) n++;
            return n;
        }

        /** How many of `later`'s boxes that are not among this plan's lie on a plot fixed in `later`, in its input or among the runs: the arrivals that stand on a run. */
        int arrivalsOnRuns(Snap later) {
            int n = 0;
            for (PlanBox b : later.boxes) if (!boxes.contains(b) && anyPlot(b, 0, p -> later.fixed[p] != 0 || later.runs[p] != 0)) n++;
            return n;
        }

        /** The plots of kind `kind` in the runs. */
        int plots(byte kind) {
            int n = 0;
            for (byte f : runs) if (f == kind) n++;
            return n;
        }

        /** The plots of kind `kind` the runs hold in `later` and did not hold here. */
        int newPlots(Snap later, byte kind) {
            int n = 0;
            for (int p = 0; p < runs.length; p++) if (later.runs[p] == kind && runs[p] != kind) n++;
            return n;
        }

        /**
         * How many of this plan's boxes are not in the plan the map makes now when its input is given this snapshot's fixed ground and ramps: the buildings
         * that move with the arrivals alone, the runs left as they were. The control: the run is what moved the rest.
         */
        int movedWithTheseRuns(CityMap m, CityMap.District d) {
            DistrictPlan.Input in = m.planInput(d);
            System.arraycopy(fixed, 0, in.fixed, 0, in.fixed.length);
            in.ramp = ramp == null ? null : ramp.clone();
            return movedOf(boxes, boxesOf(DistrictPlan.make(in)));
        }
    }

    /** Whether any plot within `reach` plots of the box (in the frame's plots, clipped to the frame) satisfies the test, a plot's index in the frame. */
    static boolean anyPlot(PlanBox b, int reach, java.util.function.IntPredicate test) {
        for (int y = Math.max(0, b.y() - reach); y < Math.min(DistrictPlan.FRAME, b.y() + b.h() + reach); y++) {
            for (int x = Math.max(0, b.x() - reach); x < Math.min(DistrictPlan.FRAME, b.x() + b.w() + reach); x++) if (test.test(y * DistrictPlan.FRAME + x)) return true;
        }
        return false;
    }

    /**
     * How many plots of `later`'s fixed ground, worked back through its frame's records (Input.fixedLog) dated after the snapshot's month, differ from the
     * fixed ground the snapshot's input held (the interchanges' reserves, undated, read as 0 on both sides): the journal's own check, THE RUNS DATED.
     */
    static int journalDiffers(DistrictPlan.Input later, Snap s) {
        byte[] back = later.fixed.clone();
        if (later.fixedLog != null) {
            for (int i = later.fixedLog.length - 1; i >= 0 && later.fixedLogMonth[i] > s.month; i--) back[later.fixedLog[i] >>> 8] = (byte) ((later.fixedLog[i] >> 4) & 15);
        }
        int n = 0;
        for (int p = 0; p < back.length; p++) {
            byte a = back[p] == DistrictPlan.FIXED_RESERVE ? 0 : back[p], b = s.fixed[p] == DistrictPlan.FIXED_RESERVE ? 0 : s.fixed[p];
            if (a != b) n++;
        }
        return n;
    }

    /** The kinds of fixed plot the runs lay, in the order a line prints them. */
    static final byte[] RUN_KINDS = { DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_HIGHWAY, DistrictPlan.FIXED_RESERVE };
    /** ...and their names, in the same order. */
    static final String[] RUN_NAMES = { "yard", "rail", "highway", "reserve" };

    /** What the runs laid between two snapshots, by kind: "yard 56, rail 19". */
    static String laidOf(Snap before, Snap after) {
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < RUN_KINDS.length; k++) {
            int n = before.newPlots(after, RUN_KINDS[k]);
            if (n > 0) s.append(s.length() > 0 ? ", " : "").append(RUN_NAMES[k]).append(' ').append(n);
        }
        return s.length() == 0 ? "none" : s.toString();
    }

    /** The district's plan after one month of the model as it stands, nothing added: the month a run is laid in. */
    static Snap quietMonth(Months h, CityMap.District d) {
        h.run(0);
        return new Snap(h.map, d);
    }

    /** The id of the building template of that name. */
    static int idOf(Game g, String name) {
        return g.getBuildingManager().getTemplateByName(name).getId();
    }

    /**
     * Whether a run laid across a district's buildings moves none of them (batch FX7): on the played city's ground the district holding the founding
     * site is given houses and BUILDINGS_A_MONTH buildings of the mix a month until half its room is used; a Rail Depot is added and a month run (the runs' yard and main
     * line), four months of arrivals, the depot taken away, four months, the depot again, four months, then Elevated Highways a month until they lie in the
     * district. The district's plan is not kept: it is drawn from the map at each moment.
     */
    static void runsLaidAcross(Game g) {
        out.println("\n--- 2d. a run laid across a district's buildings moves none of them (FX7) ---");
        BuildingVisual.Type[] types = g.getMapTypes();
        int house = idOf(g, "House"), depot = idOf(g, "Rail Depot"), highway = idOf(g, "Elevated Highway");
        int[] mix = new int[MIX.length];
        for (int i = 0; i < mix.length; i++) mix[i] = idOf(g, MIX[i]);
        CityLand land = g.getCityLand().copy();
        long[] model = new long[types.length];
        CityMap m = CityMap.canonical(land, r -> land.amountsInOrder(r), types, new long[types.length], g.getMonth());
        Months h = new Months(m, model, house, g.getMonth() + 1);
        int firstMonth = h.month;
        CityMap.District d = null;
        DistrictPlan.Input frame = null;
        for (CityMap.District e : m.districts()) {
            DistrictPlan.Input in = m.planInput(e);
            if (land.siteX() >= in.x0 && land.siteX() < in.x0 + DistrictPlan.SIDE && land.siteY() >= in.y0 && land.siteY() < in.y0 + DistrictPlan.SIDE) {
                d = e;
                frame = in;
            }
        }
        int each = BuildingVisual.cells(types[house]), step = Math.max(2, (int) (d.room() / each / 24));
        long roomAtFirst = d.room();
        boolean noRunsYet = m.runs().yards().isEmpty() && !m.runs().rail().started && !m.runs().highways().started && m.minedSites().isEmpty();
        Arrivals in = new Arrivals(h, mix, step);

        // ----- S1: most of the room used, room left -----
        int guard = 400;
        while (d.usedCells() < d.room() * USED_SHARE && guard-- > 0) in.month();
        int month1 = h.last();
        Snap s1 = new Snap(m, d);
        int placed1 = d.count(house);
        boolean allKinds1 = d.count(house) > 0;
        for (int t : mix) {
            placed1 += d.count(t);
            allKinds1 &= d.count(t) > 0;
        }
        long used1 = d.usedCells(), room1 = d.room(), free1 = d.freeCells();

        // ----- the yard: a Rail Depot, and the month that follows it -----
        model[depot] = 1;
        Snap s2 = quietMonth(h, d);
        int yardMonth = h.last();
        int under12 = s1.under(s2, 0), moved12 = movedOf(s1.boxes, s2.boxes), yardPlots = s2.plots(DistrictPlan.FIXED_YARD), railPlots = s2.plots(DistrictPlan.FIXED_RAIL);
        int control12 = s1.movedWithTheseRuns(m, d);
        List<long[]> yards = new ArrayList<>(m.runs().yards());
        long[] railHub = m.runs().rail().hub();

        // ----- four months of arrivals on the ground the yard left -----
        in.months(MONTHS_BETWEEN);
        Snap s2b = new Snap(m, d);
        int moved2b = movedOf(s2.boxes, s2b.boxes);

        // ----- the yard goes: no Rail Depot, and the month that follows -----
        model[depot] = 0;
        Snap s3 = quietMonth(h, d);
        int goneMonth = h.last();
        int moved3 = movedOf(s2b.boxes, s3.boxes), yardLeft = s3.plots(DistrictPlan.FIXED_YARD), freed = s2b.plots(DistrictPlan.FIXED_YARD) - yardLeft;
        boolean yardGone = model[depot] == 0 && m.runs().yards().isEmpty();

        // ----- four months, and the depot again -----
        in.months(MONTHS_BETWEEN);
        Snap s3b = new Snap(m, d);
        model[depot] = 1;
        Snap s4 = quietMonth(h, d);
        int backMonth = h.last();
        int under34 = s3b.under(s4, 0), moved4 = movedOf(s3b.boxes, s4.boxes), yardBack = s4.plots(DistrictPlan.FIXED_YARD);
        int control34 = s3b.movedWithTheseRuns(m, d);
        int sameSpot = 0;
        for (int p = 0; p < s4.runs.length; p++) if (s4.runs[p] == DistrictPlan.FIXED_YARD && s2.runs[p] == DistrictPlan.FIXED_YARD) sameSpot++;
        in.months(MONTHS_BETWEEN);
        Snap s4b = new Snap(m, d);

        // ----- the highways: Elevated Highways a month until some lie in the district, the plan of that month against the month before -----
        Snap before = s4b, hwBefore = null, s5 = null;
        int hwMonth = -1, hwNew = 0, hwNear = 0, hwMoved = 0, hwControl = 0, hwUnder = 0, hwOnRuns = 0, hwMonths = 0;
        boolean reached = false;
        for (int i = 0; i < HIGHWAY_MONTHS && s5 == null; i++) {
            model[highway] += HIGHWAY_STEP;
            Snap after = quietMonth(h, d);
            hwMonths++;
            hwOnRuns += before.arrivalsOnRuns(after);
            hwNew = before.newPlots(after, DistrictPlan.FIXED_HIGHWAY);
            if (hwNew > 0) {
                s5 = after;
                hwBefore = before;
                hwMonth = h.last();
                hwUnder = before.under(after, 0);
                hwNear = before.under(after, HIGHWAY_CLEAR);
                hwMoved = movedOf(before.boxes, after.boxes);
                hwControl = before.movedWithTheseRuns(m, d);
                reached = m.runs().highwaysReach();
            }
            before = after;
        }
        int hwBoxes = hwBefore == null ? 0 : hwBefore.boxes.size();
        Snap s6 = before;
        if (s5 != null) {
            in.months(MONTHS_BETWEEN);
            s6 = new Snap(m, d);
        }
        long[] highwayHub = m.runs().highways().hub();

        // ----- the arrivals after each run stand on none of its plots; every log adds up; the sidecar -----
        int onRuns = s1.arrivalsOnRuns(s2) + s2.arrivalsOnRuns(s2b) + s2b.arrivalsOnRuns(s3) + s3.arrivalsOnRuns(s3b) + s3b.arrivalsOnRuns(s4) + s4.arrivalsOnRuns(s4b) + hwOnRuns
                + (s5 == null ? 0 : s5.arrivalsOnRuns(s6));
        boolean adds = logsAddUp(m);
        byte[] side = m.writeSidecar(h.last(), CityMap.FORMAT);
        CityMap back = CityMap.readSidecar(side, land, r -> land.amountsInOrder(r), types, h.last(), CityMap.stampIn(side));
        CityMap.District same = null;
        if (back != null) for (CityMap.District e : back.districts()) if (e.dx == d.dx && e.dy == d.dy) same = e;
        java.util.Set<PlanBox> read = same == null ? null : boxesOf(back.plan(same));
        int readDiffer = read == null ? -1 : movedOf(s6.boxes, read) + movedOf(read, s6.boxes);

        // ----- the journal of the runs (THE RUNS DATED): the frame's fixed ground worked back from today's to each stage's month; the sidecar's copy -----
        DistrictPlan.Input lastIn = m.planInput(d);
        List<Snap> stages = new ArrayList<>(List.of(s1, s2, s2b, s3, s3b, s4, s4b));
        if (s5 != null) stages.add(s5);
        stages.add(s6);
        int plotsDiffer = 0, stagesDiffer = 0;
        StringBuilder counts = new StringBuilder();
        for (Snap sn : stages) {
            int diff = journalDiffers(lastIn, sn);
            plotsDiffer += diff;
            if (diff > 0) stagesDiffer++;
            counts.append(counts.length() > 0 ? ", " : "").append(sn.journal);
        }
        DistrictPlan.Input writtenIn = m.planInputHere(d), readIn = same == null ? null : back.planInputHere(same);
        boolean journalRead = readIn != null && writtenIn.fixedLog != null && Arrays.equals(readIn.fixedLogMonth, writtenIn.fixedLogMonth)
                && Arrays.equals(readIn.fixedLog, writtenIn.fixedLog) && same.journalSize() == d.journalSize();
        int frameRecords = lastIn.fixedLog == null ? 0 : lastIn.fixedLog.length;

        out.printf("      the founding site's district (%d,%d): %,d owned plots, a room of %,d cells, %d houses a month (a house takes %d) and one of %d kinds of building (%d to %d cells) from month %d;"
                + " the founding site at (%d,%d) in its frame, no mine on the map%n", d.dx, d.dy, d.owned(), roomAtFirst, step, each, mix.length, cellsMin(types, mix), cellsMax(types, mix), firstMonth,
                land.siteX() - frame.x0, land.siteY() - frame.y0);
        out.printf("      S1 (month %d): %,d buildings placed, %,d of %,d cells used, the plan places %,d%n", month1, placed1, used1, room1, s1.plan.buildings);
        out.printf("      the yard (month %d): %s laid in the frame, %,d of S1's %,d boxes lie under them, %,d moved (%,d when the plan is made with S1's fixed ground); the railway's hub (%s) and the yards: %s%n",
                yardMonth, laidOf(s1, s2), under12, s1.boxes.size(), moved12, control12, hubText(railHub, frame), yardsText(yards, frame));
        out.printf("      %d months on: %,d of the %,d boxes moved%n", MONTHS_BETWEEN, moved2b, s2.boxes.size());
        out.printf("      the yard gone (month %d): %,d yard plots left in the frame, %,d freed, %,d of %,d boxes moved%n", goneMonth, yardLeft, freed, moved3, s2b.boxes.size());
        out.printf("      the yard again (month %d): %s laid, %,d of %,d boxes lie under them, %,d moved (%,d with the fixed ground before); %,d of its %,d yard plots on the first yard's ground%n", backMonth,
                laidOf(s3b, s4), under34, s3b.boxes.size(), moved4, control34, sameSpot, yardBack);
        out.printf("      the highways (hub (%s), %d more Elevated Highways a month, laid in the district in the month %d of them, month %d): %,d plots, reaching a street: %b; %,d of the %,d boxes lie under them,"
                + " %,d within %d plot, %,d moved (%,d with the fixed ground before)%n", hubText(highwayHub, frame), HIGHWAY_STEP, hwMonths, hwMonth, hwNew, reached, hwUnder, hwBoxes, hwNear,
                HIGHWAY_CLEAR, hwMoved, hwControl);
        out.printf("      the journal of the runs: %,d records in the district's after the stages (%s), %,d in its frame's, %,d plots of the stages' fixed ground differ when worked back,"
                + " %,d plots' records left out%n", d.journalSize(), counts, frameRecords, plotsDiffer, m.journalDropped());

        check("fixture: the founding site's district found, and no run, mine or highway on the map before the fixture's months", d != null && frame != null && h.reconciled && noRunsYet);
        check("fixture: the district holds houses and every kind of the mix, uses half its room and has room left, and its plan places every one of them",
                allKinds1 && used1 >= room1 * USED_SHARE && s1.boxes.size() == placed1 && s1.plan.overflowCount() == 0 && free1 >= each);
        check("fixture: the Rail Depot's month lays a yard in the district (" + yardPlots + " plots) and a main line (" + railPlots + " plots of rail), and the model has one depot",
                yardPlots > 0 && railPlots > 0 && !yards.isEmpty() && model[depot] == 1);
        check("fixture: the yard's month lays plots under buildings that stand: " + under12 + " of S1's " + s1.boxes.size() + " boxes have a plot the runs hold now and did not before", under12 > 0);
        check("a yard laid across a district's buildings moves none of them: every box of the plan before stands in the plan after (" + s1.boxes.size() + " of them, " + moved12 + " moved)",
                moved12 == 0 && s1.boxes.size() > 0);
        check("...and the arrivals after it go on with the yard's ground left as it is: every box stands four months on (" + s2.boxes.size() + " of them, " + moved2b + " moved)", moved2b == 0);
        check("fixture: the depot taken away, the yard is gone from the district's frame (" + freed + " plots freed)", yardGone && yardLeft == 0 && freed > 0);
        check("a yard that goes moves none of the buildings: every box of the plan before stands in the plan after (" + s2b.boxes.size() + " of them, " + moved3 + " moved)", moved3 == 0 && s2b.boxes.size() > 0);
        check("fixture: the depot again, a yard is laid in the frame (" + yardBack + " plots, " + sameSpot + " on the first yard's ground) and lies under " + under34 + " boxes of the plan before",
                yardBack > 0 && under34 > 0);
        check("a yard laid again across the buildings that stand moves none of them: every box of the plan before stands in the plan after (" + s3b.boxes.size() + " of them, " + moved4 + " moved)",
                moved4 == 0 && s3b.boxes.size() > 0);
        check("fixture: Elevated Highways are laid in the district and reach a street (" + hwNew + " plots, month " + hwMonth + "), among buildings that stand: " + hwNear + " of the " + hwBoxes
                + " boxes within " + HIGHWAY_CLEAR + " plot of them", hwNew > 0 && reached && hwNear > 0);
        check("highways laid in a district's buildings move none of them: every box of the plan before the month stands in the plan after (" + hwBoxes + " of them, " + hwMoved + " moved)",
                hwMoved == 0 && hwBoxes > 0);
        check("the arrivals after a run stand on none of its plots: " + onRuns + " of the boxes the later plans hold that the earlier did not lie on a plot fixed in the input or among the runs", onRuns == 0);
        check("...every district's log still sums to its counts after the runs came and went", adds);
        check("the journal dates the runs: the frame's fixed ground worked back from today's to each stage's month is what the plan's input held that month ("
                + stages.size() + " stages from the one of month " + s1.month + ", " + frameRecords + " records, " + plotsDiffer + " plots differ in " + stagesDiffer + ")",
                plotsDiffer == 0 && frameRecords > 0 && d.journalSize() > 0);
        check("...and no plot's records were left out for ending on other than the runs as they stand (" + m.journalDropped() + " left out)", m.journalDropped() == 0);
        check("the sidecar read back carries the same journal: the district's records and its plan's input frame's records are the written map's (" + frameRecords + " of them)", journalRead);
        check("the sidecar written after them and read back plans the district box for box as the map it was written from (" + s6.boxes.size() + " boxes, " + readDiffer + " differ)",
                back != null && same != null && readDiffer == 0 && s6.boxes.size() > 0);
    }

    /** The smallest and the largest footprint, in cells, of the types listed. */
    static int cellsMin(BuildingVisual.Type[] types, int[] ids) {
        int n = Integer.MAX_VALUE;
        for (int t : ids) n = Math.min(n, BuildingVisual.cells(types[t]));
        return n;
    }

    static int cellsMax(BuildingVisual.Type[] types, int[] ids) {
        int n = 0;
        for (int t : ids) n = Math.max(n, BuildingVisual.cells(types[t]));
        return n;
    }

    /** A net's hub in the frame's plots, for a line: "40,72". */
    static String hubText(long[] hub, DistrictPlan.Input frame) {
        return hub == null ? "none" : (hub[0] - frame.x0) + "," + (hub[1] - frame.y0);
    }

    /** The runs' yards in the frame's plots, for a line: "[x,y 8x7]". */
    static String yardsText(List<long[]> yards, DistrictPlan.Input frame) {
        StringBuilder s = new StringBuilder();
        for (long[] y : yards) s.append(s.length() > 0 ? ", " : "").append('[').append(y[0] - frame.x0).append(',').append(y[1] - frame.y0).append(' ').append(y[2]).append('x').append(y[3]).append(']');
        return s.length() == 0 ? "none" : s.toString();
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
                    for (byte b : buf) if (!World.salt(b) && b != World.FRESH) dry++;
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
     * a year on is exactly what it holds - as founded, its Commercial Bank
     * (Game.foundingBank()) and its Builders' Yard (Game.foundingYard(), PT1-M4: the
     * works department that built for nobody is retired) on bare ground, no road and no highway, where J3b
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
                    // ...and the Builders' Yard the city founds with since PT1-M4: the founding is the two, and nothing else.
                    BuildingsTemplate yard = g.getBuildingManager().getTemplateByName(Game.FOUNDING_YARD);
                    bare = bank != null && yard != null && d.total() == 2 && d.buildings[bank.getId()] == 1 && model[bank.getId()] == 1
                            && d.buildings[yard.getId()] == 1 && model[yard.getId()] == 1
                            && modelBuildings(model, map.types()) == 2 && d.surfaced() == 0 && r.count(r::isHighway) == 0;
                }
            }
        } finally {
            System.setOut(real);
        }
        out.print(lines);
        check("a new default city is drawn as what it holds, as founded, a month on and a year on: each type's buildings, no more, no less",
                exact);
        check("...as founded, its Commercial Bank and its Builders' Yard: no other building, no road (its streets tracks, the model's base streets: spec 2.2), no highway", bare);
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
                    if (in.owned[q] && !World.salt(gr) && gr != World.FRESH && p.site[q] == 0) free++;
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
                        sea[g] = World.salt(in.terrain[k]);
                        wet[g] = sea[g] || in.terrain[k] == World.FRESH;
                        bridge[g] = p.bridge[k];
                        beneath[g] = p.beneath[k];
                        run[g] = p.run[k];
                    }
                    for (int b = 0; b < p.buildings; b++) {
                        // (FX5 Y2) A yard longer than a tile is a building on every tile its box reaches, counted on the tile holding its
                        // first plot: its part on another tile (Painted.bpart) is the same building, as a site's mine is by its key below.
                        if (p.bpart[b]) continue;
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
        networkOn(m, model, what, true);
    }

    /**
     * ...and whether the city must have + junctions for the + rule to bite (`crosses`): not the played city since CL3, whose
     * few cells each opened while its road was short - a cell's squares are decided as it opens, never after (DistrictPlan,
     * THE ORDER OF ARRIVAL) - so its junctions are T's; the + rule's fixture is Jerus's city's, x 1.
     */
    static void networkOn(CityMap m, long[] model, String what, boolean crosses) {
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
        if (!crosses) out.printf("      (%s: %,d + junctions - its cells opened while its road was short, so its junctions are T's: the + rule bites on Jerus's city x 1)%n", what, cr[0]);
        check(what + ": fixture: the city has streets" + (crosses ? " and + junctions" : ""), roadPlots > 0 && (cr[0] > 0 || !crosses));
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
        networkOn(city.getCityMap(), city.getMapCounts(), "the played city", false);
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
        Path work = TempDirs.create("mapcheck-save");
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

    /** The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay (since FR7 slice 3 the surplus no train's way uses, CityRuns.trackSurplus()), its streets crossing it level. */
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
        Game back, next, stale, plain, badLog;
        byte[] first, second, goodBytes, badBytes;
        long stampInSave;
        boolean nextSame, readSame;
        String plainJson;
        // CL3's log across the reload: the plan of every district on both maps, box for box, and the logs record by record.
        int districtsPlanned = 0, plansDiffer = 0, logsDiffer = 0, logsPastBatch = 0;
        try {
            g.saveGame(3, "mapcheck");
            first = Files.readAllBytes(files.mapFile(3));
            stampInSave = new com.google.gson.Gson().fromJson(Files.readString(files.saveFile(3)), DataSave.class).getMapStamp();
            back = new Game(files);
            back.loadGameSave(3);
            readSame = back.hasCityMap() && back.getCityMap().same(g.getCityMap());
            if (back.hasCityMap()) {
                CityMap was = g.getCityMap(), read = back.getCityMap();
                for (int i = 0; i < was.districts().size() && i < read.districts().size(); i++) {
                    CityMap.District a = was.districts().get(i), b = read.districts().get(i);
                    districtsPlanned++;
                    // The plan each map draws from the input it hands the planner, its log and all.
                    if (!sameBoxes(DistrictPlan.make(was.planInput(a)), DistrictPlan.make(read.planInput(b)))) plansDiffer++;
                    if (!a.sameLog(b)) logsDiffer++;
                    if (a.logSize() > a.firstBatch) logsPastBatch++;
                }
            }
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
            // A log made not to add up (CL3): the newest record of the first district holding any moved by one in the map in memory, saved under
            // its own stamp - so slot 6's sidecar differs from slot 5's, the same month's, in that one change - and put back at once.
            goodBytes = Files.readAllBytes(files.mapFile(5));
            CityMap.District victim = null;
            for (CityMap.District d : g.getCityMap().districts()) if (victim == null && d.logSize() > 0) victim = d;
            int newest = victim.logSize() - 1;
            victim.logChange[newest] += 1;
            try {
                g.saveGame(6, "mapcheck a log that does not add up");
            } finally {
                victim.logChange[newest] -= 1;
            }
            badBytes = Files.readAllBytes(files.mapFile(6));
            badLog = new Game(files);
            badLog.loadGameSave(6);
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
        // THE SAME CITY AFTER A RELOAD (CL3): the log is a flow, so what the plan is drawn by comes back with the map.
        out.printf("      the log: %d of the played city's %d districts hold records past their first batch%n", logsPastBatch, districtsPlanned);
        check("fixture: the played city's logs go on past their first batch (a log worth a round trip)", logsPastBatch > 0);
        check("a map saved and read back plans every district box for box as the map it was saved from (each plan made from its input, log and all)",
                districtsPlanned > 0 && plansDiffer == 0);
        check("...and each district's log round-trips: its records and its first batch, which same() compares (the check above)", logsDiffer == 0);
        CityMap canonical = CityMap.canonical(stale.getCityLand(), stale.getLandManager()::remainingByHolding,
                stale.getMapTypes(), stale.getMapCounts(), stale.getMonth());
        check("a stale sidecar is not read: the load draws the map again canonically", stale.hasCityMap()
                && stale.getCityMap().same(canonical));
        // A STALE LOG IS NOT READ (CL3): a log that does not add up to its district's counts is no log of that district.
        CityMap goodRead = CityMap.readSidecar(goodBytes, badLog.getCityLand(), badLog.getLandManager()::remainingByHolding, badLog.getMapTypes(),
                badLog.getMonth(), CityMap.stampIn(goodBytes));
        CityMap badRead = CityMap.readSidecar(badBytes, badLog.getCityLand(), badLog.getLandManager()::remainingByHolding, badLog.getMapTypes(),
                badLog.getMonth(), CityMap.stampIn(badBytes));
        check("fixture: the same month's sidecar with one record moved by one is another file, its stamp its own, and the one beside it reads back",
                !Arrays.equals(goodBytes, badBytes) && CityMap.stampIn(goodBytes) != CityMap.stampIn(badBytes) && goodRead != null);
        check("...every log of a sidecar read back sums, type by type, to its district's counts", logsAddUp(goodRead) && logsAddUp(back.getCityMap()));
        check("a log that does not sum to its district's counts is not read: readSidecar answers null, stamp and month right", badRead == null);
        CityMap canonicalBad = CityMap.canonical(badLog.getCityLand(), badLog.getLandManager()::remainingByHolding,
                badLog.getMapTypes(), badLog.getMapCounts(), badLog.getMonth());
        check("...and the load draws the map again canonically", badLog.hasCityMap() && badLog.getCityMap().same(canonicalBad));
        boolean oneBatchNow = true;
        for (CityMap.District d : badLog.getCityMap().districts()) {
            oneBatchNow &= d.firstBatch == d.logSize();
            for (int k = 0; k < d.logSize(); k++) oneBatchNow &= d.logMonth[k] == badLog.getMonth();
        }
        check("...each district's log then one batch of its counts at the month it was drawn", oneBatchNow && logsAddUp(badLog.getCityMap()));
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
                fromOld != null && fromOld.sameDistricts(live, false) && fromOld.runs().same(afresh.runs()));
        check("...and 0.7.88's FORMAT 5, which wrote no runs, likewise", fromNone != null && fromNone.sameDistricts(live, false)
                && fromNone.runs().same(afresh.runs()));
        // THE OLD SAVE (CL3): a sidecar of FORMAT 8 has no logs. Each district's log starts as its counts, one batch at the save's month, and its plan is
        // today's: the plan drawn from the log is the plan drawn from the counts alone (logMonth, logType and logChange set to null), box for box.
        byte[] eight = live.writeSidecar(back.getMonth(), 8);
        CityMap fromEight = CityMap.readSidecar(eight, back.getCityLand(), back.getLandManager()::remainingByHolding, back.getMapTypes(),
                back.getMonth(), CityMap.stampIn(eight));
        int eightDistricts = 0, eightOneBatch = 0, eightAtSave = 0, eightToday = 0, eightPlaced = 0;
        if (fromEight != null) {
            for (CityMap.District d : fromEight.districts()) {
                eightDistricts++;
                if (d.firstBatch == d.logSize()) eightOneBatch++;
                boolean atSave = true;
                for (int k = 0; k < d.logSize(); k++) atSave &= d.logMonth[k] == back.getMonth();
                if (atSave) eightAtSave++;
                DistrictPlan.Input withLog = fromEight.planInput(d), fromCounts = fromEight.planInput(d);
                fromCounts.logMonth = null;
                fromCounts.logType = null;
                fromCounts.logChange = null;
                DistrictPlan logPlan = DistrictPlan.make(withLog), countsPlan = DistrictPlan.make(fromCounts);
                eightPlaced += logPlan.buildings;
                if (sameBoxes(logPlan, countsPlan)) eightToday++;
            }
        }
        out.printf("      as FORMAT 8 (no logs): %,d bytes, read back to %d districts - %d each one batch at the save's month, %d planned as today's, %,d buildings placed%n",
                eight.length, eightDistricts, eightOneBatch, eightToday, eightPlaced);
        check("a FORMAT 8 sidecar still loads, into the same districts (their logs begun at the save, so not compared)", fromEight != null
                && fromEight.sameDistricts(live, false));
        check("...each district's log one batch (its first batch is all its records) at the save's month, adding up to its counts", eightDistricts > 0
                && eightOneBatch == eightDistricts && eightAtSave == eightDistricts && logsAddUp(fromEight));
        check("...and its plan is today's, box for box: made from its log it is the plan made from its counts", eightToday == eightDistricts && eightPlaced > 0);
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
            // THE LOG'S COST (CL3): its records over the city, and the sidecar with them against the sidecar of FORMAT 8 without.
            long records = 0;
            for (CityMap.District d : m.districts()) records += d.logSize();
            int without = m.writeSidecar(1, 8).length;
            out.printf("      ...the log: %,d records over %,d districts (%.1f a district), %,d bytes deflated with it against %,d without (FORMAT 8): %,d for the log;"
                    + " bounds %,d bytes, %.0f ms a write and a read%n", records, m.districts().size(), records / (double) m.districts().size(), bytes.length,
                    without, bytes.length - without, SIDECAR_BYTES, SIDECAR_MS);
            check("at " + (TIMES[i] < 10_000 ? "5" : "10") + " billion the sidecar, log and all, is no more than SIDECAR_BYTES (" + SIDECAR_BYTES + ") deflated",
                    records > 0 && bytes.length <= SIDECAR_BYTES);
            check("...and is written and read back in no more than SIDECAR_MS (" + (int) SIDECAR_MS + " ms) each", Timing.within(wms, SIDECAR_MS)
                    && Timing.within(rms, SIDECAR_MS));
        }
    }

    /**
     * The most a sidecar may be at 5 and 10 billion people, log and all, in deflated bytes (CL3): the 10B copy's measured 333,776 (294,101 without the log, in
     * FORMAT 8; its 1,457,418 records the canonical deal's, one batch, about 50 a district) and a half again. The study's 1 to 2 MB at 10B was a log of years of
     * months; this copy has the deal's alone, so the bound holds the log's size per record, not a played city's.
     */
    static final int SIDECAR_BYTES = 500_000;

    /** The most the 5B and 10B copies' sidecar may take to write, and to read, in ms (CL3): measured 271 and 267 at 10B (240 and 161 at 5B), and near twice that. */
    static final double SIDECAR_MS = 500;

    /** Whether two plans place the same boxes: the same count of buildings, each one's type and its box {x, y, w, h}, in the same order, and the same buildings left without a place (by type). */
    static boolean sameBoxes(DistrictPlan a, DistrictPlan b) {
        if (a.buildings != b.buildings || !Arrays.equals(a.overflow, b.overflow)) return false;
        for (int i = 0; i < a.buildings; i++) {
            if (a.btype[i] != b.btype[i] || a.bx[i] != b.bx[i] || a.by[i] != b.by[i] || a.bw[i] != b.bw[i] || a.bh[i] != b.bh[i]) return false;
        }
        return true;
    }

    /** Whether every district's log sums, type by type, to its counts: what a log is for, and what a sidecar read back is held to (CL3). */
    static boolean logsAddUp(CityMap m) {
        if (m == null) return false;
        for (CityMap.District d : m.districts()) {
            int[] sums = new int[d.counts.length];
            for (int k = 0; k < d.logSize(); k++) sums[d.logType[k]] += d.logChange[k];
            if (!Arrays.equals(sums, d.counts)) return false;
        }
        return true;
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
            CityMap here = CityMap.canonical(g.getCityLand(), g.getLandManager()::remainingByHolding, g.getMapTypes(), g.getMapCounts(), g.getMonth());
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
            if (owned && !World.salt(g) && g != World.FRESH && px[k] != bare) tinted++;
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
        // CL3: the plan places in the order of arrival - the deal's units first of its industry, as THE CAMPUS sorts a first
        // batch, and the unit a month on when it came, after the works standing, among the campus's cells (never ahead of them,
        // which would move every works placed before it).
        int unitsFirst = 0;
        boolean seenOther = false;
        java.util.Set<Integer> cells = new java.util.HashSet<>();
        int placedUnits = 0;
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = types[p.btype[b]];
            if (t == null || t.outer() || t.cls() != BuildingVisual.INDUSTRY) continue;
            if (t.campus()) {
                if (!seenOther) unitsFirst++;
                cells.add((p.by[b] / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE + p.bx[b] / DistrictPlan.CELL);
                placedUnits++;
            } else seenOther = true;
        }
        int wantUnits = 0;
        for (BuildingVisual.Type t : types) if (t != null && t.campus()) wantUnits += campus.count(t.id());
        boolean firstOfIndustry = unitsFirst >= wantUnits - 1;
        int overUnits = 0;
        for (BuildingVisual.Type t : types) if (t != null && t.campus() && t.id() < p.overflow.length) overUnits += p.overflow[t.id()];
        out.printf("      its plan, district (%d, %d): %d units placed of %d (%d left out), in %d cell(s) %s%n", campus.dx, campus.dy, placedUnits,
                wantUnits, overUnits, cells.size(), cells);
        check("...its plan places the deal's units first of its industry (the unit a month on as it came, CL3), every one, in cells that touch one another: one campus",
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
                    onDry &= m.land().ownsPlot(x, y) && !World.salt(tg) && tg != World.FRESH;
                    if (r != 0) clear = false;
                    for (CityShore.Work o : shore.works()) if (o != k && (o.holds(x, y) || o.quayAt(x, y))) clear = false;
                }
            }
            for (int q = 1; q <= k.quay(); q++) {
                long[] at = k.quayPlot(q);
                quays &= m.land().ownsPlot(at[0], at[1]) && World.salt(tileGround(w, at[0], at[1]));
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
            // FX5, Jerus 2026-10-20: ("Queue the fix" for a train standing over the yard's wagons) one route a yard and CLEAR siding of its own,
            // not a siding the yard's standing wagons keep; none stand in this picture, so every siding is clear here, and a yard always keeps one.
            expectedRoutes += TileRaster.tracksOf(m.types()[m.yardTypeOf(i)]) - standingOn(m, i);
        }
        boolean starts = true, onTrack = true, intoYard = true, sidings = true, stationOnes = true;
        double r = Math.toRadians(line.way());
        java.util.Map<Integer, java.util.Set<Integer>> sidingsOf = new java.util.HashMap<>();
        for (TrainSchedule.Route rt : routes) {
            if (rt.works()) { worksRoutes++; continue; }
            double[] xs = rt.xs(), ys = rt.ys();
            long[] yard = runs.yards().get(rt.yard());
            // FX5: the routes are rounded at every turn (TrackCurves.RADIUS), the world line's join among them, so the route passes by
            // the line's point within the radius and its faded part ends no more than a radius short of WORLD_PLOTS.
            starts &= Math.abs(xs[0] - (line.x() + Math.cos(r) * TrainSchedule.WORLD_PLOTS)) < 1e-9
                    && Math.abs(ys[0] - (line.y() + Math.sin(r) * TrainSchedule.WORLD_PLOTS)) < 1e-9
                    && GatewayCheck.nearestOnRoute(rt, line.x(), line.y()) <= TrackCurves.RADIUS
                    && rt.fadeTo() <= TrainSchedule.WORLD_PLOTS + 1e-9 && rt.fadeTo() >= TrainSchedule.WORLD_PLOTS - TrackCurves.RADIUS;
            // From past the world line's join to where it enters its yard's box, every quarter step on a plot the railway carries -
            // or (FX5) beside one, where a curve cuts its corner: an arc strays at most RADIUS (1 - 1/sqrt 2) plots from its legs.
            double[] al = rt.along();
            int from = 0, last = xs.length - 1;
            while (from < xs.length - 1 && al[from] < rt.fadeTo() + 2 * TrackCurves.RADIUS) from++;
            if (rt.siding() >= 0) {
                last = from;
                while (last < xs.length - 1 && !(xs[last] >= yard[0] && xs[last] <= yard[0] + yard[2] && ys[last] >= yard[1] && ys[last] <= yard[1] + yard[3])) last++;
            }
            for (int i = from; i < last; i++) {
                double len = Math.hypot(xs[i + 1] - xs[i], ys[i + 1] - ys[i]);
                int n = Math.max(1, (int) Math.ceil(len * 4));
                for (int k = 0; k <= n; k++) {
                    double x = xs[i] + (xs[i + 1] - xs[i]) * k / n, y = ys[i] + (ys[i + 1] - ys[i]) * k / n;
                    boolean near = false;
                    for (int dy = -1; dy <= 1 && !near; dy++) {
                        for (int dx = -1; dx <= 1 && !near; dx++) {
                            if ((dx != 0 || dy != 0) && Math.hypot(x - (Math.floor(x) + dx + 0.5), y - (Math.floor(y) + dy + 0.5)) > 0.5 + TrackCurves.RADIUS * (1 - Math.sqrt(0.5)) + 1e-9) continue;
                            byte at = runs.at((long) Math.floor(x) + dx, (long) Math.floor(y) + dy);
                            near = at == CityRuns.F_RAIL || at == CityRuns.F_RAIL_OVER || at == CityRuns.F_YARD;
                        }
                    }
                    onTrack &= near;
                }
            }
            // ...and its ladder and siding inside the box, from the first point in it.
            for (int i = last; i < xs.length && rt.siding() >= 0; i++) {
                intoYard &= xs[i] >= yard[0] && xs[i] <= yard[0] + yard[2] && ys[i] >= yard[1] && ys[i] <= yard[1] + yard[3];
            }
            sidingsOf.computeIfAbsent(rt.yard(), k -> new java.util.HashSet<>()).add(rt.siding());
            stationOnes &= rt.station() == java.util.Arrays.equals(yard, station);
        }
        for (java.util.Map.Entry<Integer, java.util.Set<Integer>> e : sidingsOf.entrySet()) {
            sidings &= e.getValue().size() == TileRaster.tracksOf(m.types()[m.yardTypeOf(e.getKey())]) - standingOn(m, e.getKey());
        }
        out.printf("      %d of %d yards reached, %d routes found in %.1f ms (%d to a works' siding), the longest %.0f plots%n", reached, runs.yards().size(),
                routes.size(), routesMs, worksRoutes, routes.stream().mapToDouble(TrainSchedule.Route::length).max().orElse(0));
        check("THE TRAINS' ROUTES: one a yard the railway's way reaches and a clear siding of its own (a depot's, a terminal's; the yard's wagons keep the others), every yard reached",
                reached == runs.yards().size() && sidingsOf.size() == reached && routes.size() == expectedRoutes + worksRoutes && sidings);
        check("...each from WORLD_PLOTS out along the world line, faded in to where the line leaves the city's ground",
                starts && !routes.isEmpty());
        check("...then on track every quarter plot, or beside it where a curve cuts a corner (FX5), from the gate arm's end to its yard's box (CityRuns.wayTo())", onTrack);
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
            // FX5 (slice T): a train's leg is its route's (Route.legSeconds()) and its stand its kind's (Kind.standSeconds()), not one pace for all.
            double lc = rt.legSeconds() / TrainSchedule.MONTH_SECONDS, dc = c.kind().standSeconds() / TrainSchedule.MONTH_SECONDS;
            TrainSchedule.Train standing = TrainSchedule.position(c, rt, c.arrives() + dc / 2);
            List<TrainSchedule.Car> cars = TrainSchedule.cars(standing, rt);
            whole &= standing != null && standing.standing() && cars.size() == c.kind().wagons() + 2;
            for (TrainSchedule.Car car : cars) whole &= car.alpha() == 1 || rt.length() - c.kind().length() < rt.fadeTo();
            gone &= TrainSchedule.cars(TrainSchedule.position(c, rt, c.arrives() - lc), rt).isEmpty()
                    && TrainSchedule.cars(TrainSchedule.position(c, rt, c.arrives() + dc + lc), rt).isEmpty()
                    && TrainSchedule.position(c, rt, c.arrives() - lc - 1e-6) == null;
            boolean in0 = TrainSchedule.position(c, rt, c.arrives() - lc / 2).loaded(), out1 = TrainSchedule.position(c, rt, c.arrives() + dc + lc / 2).loaded();
            loaded &= c.kind() == TrainSchedule.Kind.INTERMODAL ? in0 && out1 : in0 == c.inbound() && out1 == !c.inbound();
        }
        check("TRAINS: pure in (call, t), each vehicle's fade within [0, 1]", pure && alpha);
        check("...on its siding every vehicle there, whole past the world line's part", whole);
        check("...nothing of it on the line at either end of its visit, so none appears or vanishes", gone);
        check("...an import in full and out empty, an export the other way, boxes loaded both ways", loaded);

        // ---- THE CURVES (FX5): no train swings on the spot, and every vehicle is on the rail it is drawn on.
        trainsOnTheCurves(m, routes, mix);

        // ---- THEIR LENGTH, THE SIDINGS THAT HOLD THEM AND THEIR PACE (FX5, slice T).
        trainsByLength(routes, mix);

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
                double lc = routes.get(c.route()).legSeconds() / TrainSchedule.MONTH_SECONDS, dc = c.kind().standSeconds() / TrainSchedule.MONTH_SECONDS;
                if (c.arrives() - lc <= t && t <= c.arrives() + dc + lc) live++;
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

        // ---- THE WAGONS STANDING IN THE YARDS (FX5, slice T): the routes skip the sidings they stand on.
        trainsAndTheWagons(m, kinds);

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

    /* ----- FX5, slice T: real-length trains, onto clear sidings, at a pace that reads ----- */

    /** The plots of train a route's siding holds: Route.holds(). */
    static double holdsOf(TrainSchedule.Route rt) {
        return rt.holds();
    }

    /** How far along a route its siding's ladder begins, in plots: Route.enters(). */
    static double ladderOf(TrainSchedule.Route rt) {
        return rt.enters();
    }

    /** Seconds a leg of a route `length` plots long takes: its length over the pace, no less than the shortest leg and no more than the longest. */
    static double legSecondsOf(double length) {
        return Math.max(TrainSchedule.LEG_SECONDS, Math.min(TrainSchedule.LEG_MOST, length / TrainSchedule.TRAIN_PLOTS_A_SECOND));
    }

    /** Seconds a train of a kind stands on its siding: a base and so much a wagon. */
    static double standSecondsOf(TrainSchedule.Kind k) {
        return TrainSchedule.DWELL_SECONDS + TrainSchedule.DWELL_A_WAGON * k.wagons();
    }

    /**
     * A hand-made yard (FX5 T): three sidings off a ladder of 3 plots from a main line 64 plots long, the first holding `first` plots of
     * train and the other two `second`. A siding is a route's last leg, its ladder the leg before.
     */
    static List<TrainSchedule.Route> handYard(double first, double second) {
        List<TrainSchedule.Route> out = new ArrayList<>();
        double[] holds = { first, second, second };
        for (int s = 0; s < 3; s++) out.add(handRoute(s, 67 + holds[s]));
        return out;
    }

    /** A hand-made route `length` plots long: the world line 64 plots, a ladder of 3, and the siding the rest, all on one line. */
    static TrainSchedule.Route handRoute(int siding, double length) {
        return TrainSchedule.Route.through(0, true, siding, new double[] { -64, 0, 3, length - 64 }, new double[] { 0, 0, 0, 0 }, 64, length - 67, 64);
    }

    /** How far along its route a call's train has its front at t, in plots; NaN while it is not on the line. */
    static double frontAt(TrainSchedule.Call c, TrainSchedule.Route rt, double t) {
        TrainSchedule.Train tr = TrainSchedule.position(c, rt, t);
        return tr == null ? Double.NaN : tr.front();
    }

    /** Whether a call's train is on its route's siding at t: from its front reaching the ladder in to its rear leaving it out. */
    static boolean onSiding(TrainSchedule.Call c, TrainSchedule.Route rt, double t) {
        TrainSchedule.Train tr = TrainSchedule.position(c, rt, t);
        if (tr == null) return false;
        double d0 = ladderOf(rt);
        return tr.standing() || (t < c.arrives() ? tr.front() > d0 : tr.front() - c.kind().length() > d0);
    }

    /** The most trains on one siding at once, over a month sampled 2,000 times. */
    static int mostOnASiding(TrainSchedule month, List<TrainSchedule.Route> routes) {
        int most = 0;
        for (int i = 0; i <= 2000; i++) {
            double t = i / 2000.0;
            int[] on = new int[routes.size()];
            for (TrainSchedule.Call c : month.calls()) if (c.route() >= 0 && onSiding(c, routes.get(c.route()), t)) most = Math.max(most, ++on[c.route()]);
        }
        return most;
    }

    /** Whether a standing train's rear vehicle lies on the route's last leg, its siding: the rear locomotive's middle between the leg's start and its end. */
    static boolean rearOnSiding(TrainSchedule.Call c, TrainSchedule.Route rt) {
        List<TrainSchedule.Car> cars = TrainSchedule.cars(TrainSchedule.position(c, rt, c.arrives()), rt);
        TrainSchedule.Car last = cars.get(cars.size() - 1);
        double[] xs = rt.xs(), ys = rt.ys();
        int n = xs.length;
        double sx = xs[n - 2], sy = ys[n - 2], ex = xs[n - 1] - sx, ey = ys[n - 1] - sy, len = Math.hypot(ex, ey);
        double along = ((last.x() - sx) * ex + (last.y() - sy) * ey) / len;
        return along >= -1e-9 && along <= len + 1e-9;
    }

    /**
     * Section 9's trains at their real length, on sidings that hold them, at a pace that reads (FX5, slice T; Jerus, 2026-10-20: "real
     * trains are really long", "the rail depots would have to be redesigned to accomdoate such large trains, as well as teh animation for
     * them"): a unit train is 80 wagons and 41.4 plots, no cap; no train is longer than the siding its route holds; a standing train's rear
     * vehicle lies on its siding; no two trains are on one siding at once; a train eases in to a stop and away from one, its leg and its
     * stand set by its length; and every visit lies inside the month.
     */
    static void trainsByLength(List<TrainSchedule.Route> routes, TrainSchedule mix) {
        out.println("      the trains' length, the sidings that hold them and their pace (FX5, slice T):");
        TrainSchedule.Kind hopper = TrainSchedule.Kind.HOPPER, tank = TrainSchedule.Kind.TANK, box = TrainSchedule.Kind.INTERMODAL, mixed = TrainSchedule.Kind.MIXED;
        int unit = (int) Math.ceil(TrainSchedule.UNIT_TONNES / TrainSchedule.WAGON_TONNES);
        check("a train is drawn at its real length: a unit train 80 wagons and 41.4 plots (its payload over WAGON_TONNES, no cap), an intermodal train 50 and 26.4, a mixed train 15 and 8.9",
                hopper.wagons() == 80 && tank.wagons() == 80 && unit == 80 && Math.abs(hopper.length() - (2 * TrainSchedule.LOCO_PLOTS + unit * TrainSchedule.CAR_PLOTS)) < 1e-9
                && Math.abs(hopper.length() - 41.4) < 1e-9 && box.wagons() == TrainSchedule.INTERMODAL_BOXES / TrainSchedule.BOXES_A_WAGON && box.wagons() == 50
                && Math.abs(box.length() - 26.4) < 1e-9 && mixed.wagons() == 15 && Math.abs(mixed.length() - 8.9) < 1e-9);

        // ---- A YARD OF THREE SIDINGS, one holding 18 plots and two 45: a unit train (41.4) goes on the two.
        int kinds = Ports.Cargo.values().length;
        double[] none = new double[kinds], some = new double[kinds], heavy = new double[kinds];
        some[Ports.Cargo.DRY_BULK.ordinal()] = 6 * TrainSchedule.UNIT_TONNES;
        heavy[Ports.Cargo.DRY_BULK.ordinal()] = 25 * TrainSchedule.UNIT_TONNES;
        List<TrainSchedule.Route> hand = handYard(18, 45);
        boolean fits = true, rear = true;
        int drawn = 0;
        TrainSchedule month = TrainSchedule.of(7, heavy, none, hand);
        for (TrainSchedule.Call c : month.calls()) {
            if (c.route() < 0) continue;
            drawn++;
            TrainSchedule.Route rt = hand.get(c.route());
            fits &= c.kind().length() <= holdsOf(rt) + 1e-9;
            rear &= rearOnSiding(c, rt);
        }
        out.printf("      25 unit trains on a yard of sidings holding 18, 45 and 45 plots: %d drawn%n", drawn);
        check("no train is longer than the siding of its route (a unit train, 41.4 plots, is not drawn on a siding of 18)", drawn > 0 && fits);
        check("...a standing train's rear vehicle lies on its siding, between the siding's start and its end", drawn > 0 && rear);
        check("...no two trains are on one siding at once, at 2,001 moments through the month", mostOnASiding(month, hand) <= 1);
        TrainSchedule fewer = TrainSchedule.of(7, some, none, hand);
        check("...nor with fewer trains (a train that finds its siding busy takes another or waits)", mostOnASiding(fewer, hand) <= 1);
        boolean onlyShort = true;
        for (TrainSchedule.Call c : TrainSchedule.of(7, some, none, handYard(18, 18)).calls()) onlyShort &= c.route() < 0;
        TrainSchedule none18 = TrainSchedule.of(7, some, none, handYard(18, 18));
        check("...and a kind no siding holds is not drawn: a unit train on a yard of sidings of 18 plots, counted by unfit()", onlyShort && none18.unfit() == none18.callCount() && none18.unfit() > 0);
        check("...every train is drawn or counted unfit, none twice: " + drawn + " drawn and " + month.unfit() + " unfit of " + month.callCount(),
                drawn + month.unfit() == month.callCount() && month.unfit() >= 0);

        // ---- ON THE YARD CITY'S OWN ROUTES.
        boolean city = true, cityRear = true;
        int onYards = 0;
        for (TrainSchedule.Call c : mix.calls()) {
            TrainSchedule.Route rt = routes.get(c.route());
            city &= c.kind().length() <= holdsOf(rt) + 1e-9;
            if (rt.siding() < 0 || rt.works()) continue;
            onYards++;
            cityRear &= rearOnSiding(c, rt);
        }
        check("...the yard city's trains, every kind: none longer than the siding of its route, each standing rear on its siding", onYards > 0 && city && cityRear);
        check("...and no two of the yard city's trains on one siding at once", mostOnASiding(mix, routes) <= 1);

        // ---- THE PACE: a leg of the route's length over the pace, easing in to a stop and away from it.
        double second = 1 / TrainSchedule.MONTH_SECONDS, a = 0.5;
        TrainSchedule.Call unitCall = new TrainSchedule.Call(hopper, true, 0, a);
        boolean legs = true;
        for (double length : new double[] { 100, 400, 700 }) {
            TrainSchedule.Route rt = handRoute(0, length);
            double leg = legSecondsOf(length), stand = standSecondsOf(hopper);
            legs &= TrainSchedule.position(unitCall, rt, a - (leg - 0.1) * second) != null && TrainSchedule.position(unitCall, rt, a - (leg + 0.1) * second) == null;
            legs &= TrainSchedule.position(unitCall, rt, a + (stand + leg - 0.1) * second) != null && TrainSchedule.position(unitCall, rt, a + (stand + leg + 0.1) * second) == null;
        }
        check("a leg takes the route's length over TRAIN_PLOTS_A_SECOND seconds, no less than LEG_SECONDS and no more than LEG_MOST: 100 plots 6 s, 400 plots 8, 700 plots 10", legs);
        TrainSchedule.Route four = handRoute(0, 400);
        double leg4 = legSecondsOf(400), stand4 = standSecondsOf(hopper);
        double firstIn = frontAt(unitCall, four, a - (leg4 - 1) * second) - frontAt(unitCall, four, a - leg4 * second + 1e-9);
        double lastIn = frontAt(unitCall, four, a) - frontAt(unitCall, four, a - second);
        double firstOut = frontAt(unitCall, four, a + stand4 * second) - frontAt(unitCall, four, a + (stand4 + 1) * second);
        double lastOut = frontAt(unitCall, four, a + (stand4 + leg4 - 1) * second) - frontAt(unitCall, four, a + (stand4 + leg4) * second);
        out.printf("      on a route of 400 plots: in, the first second runs %.1f plots and the last %.1f; out, the first %.1f and the last %.1f%n", firstIn, lastIn, firstOut, lastOut);
        check("...a train eases in: its front moves less in a leg's last second than in its first", firstIn > lastIn && lastIn > 0);
        check("...and eases out: it moves less in its first second away from the stand than in its last", firstOut < lastOut && firstOut > 0);
        boolean stands = true;
        for (TrainSchedule.Kind k : TrainSchedule.Kind.values()) {
            TrainSchedule.Call call = new TrainSchedule.Call(k, true, 0, a);
            double stand = standSecondsOf(k);
            stands &= TrainSchedule.position(call, four, a + (stand - 0.05) * second).standing() && !TrainSchedule.position(call, four, a + (stand + 0.05) * second).standing();
        }
        check("...it stands DWELL_SECONDS and so much a wagon (DWELL_A_WAGON): a unit train 5 s, an intermodal 4.25, a mixed 3.375", stands);
        boolean inside = true;
        for (TrainSchedule month0 : new TrainSchedule[] { mix, month, fewer }) {
            List<TrainSchedule.Route> rs = month0 == mix ? routes : hand;
            for (TrainSchedule.Call c : month0.calls()) {
                if (c.route() < 0) continue;
                double leg = legSecondsOf(rs.get(c.route()).length()) / TrainSchedule.MONTH_SECONDS, dwell = standSecondsOf(c.kind()) / TrainSchedule.MONTH_SECONDS;
                inside &= c.arrives() - leg >= 0 && c.arrives() + dwell + leg <= 1;
            }
        }
        check("...and every train's visit, its leg in, its stand and its leg out, lies inside the month", inside);
    }

    /** The sidings of yard yi of the map that wagons stand on in the picture the map has: the tiles' count, scaled for the yard's sidings, never all of them. */
    static int standingOn(CityMap m, int yi) {
        int tracks = TileRaster.tracksOf(m.types()[m.yardTypeOf(yi)]);
        int w = CityMap.wagonTracksOf(m.railPicture().wagonShare());
        return TileRaster.wagonTracksOn(w, tracks);
    }

    /**
     * Section 9's routes and the wagons standing in the yards (FX5, slice T; FX3's star 6: a train stood over the yard's own wagons): with
     * none, half and all of the fleet's wagon sets standing, no route is laid to a siding the tiles stand wagons on, a yard keeps one
     * siding clear, and no train stands on a siding the wagons stand on.
     */
    static void trainsAndTheWagons(CityMap m, int kinds) {
        out.println("      the trains and the wagons standing in the yards (FX5, slice T):");
        CityMap.RailPicture before = m.railPicture();
        double[] bulk = new double[kinds], none = new double[kinds];
        bulk[Ports.Cargo.DRY_BULK.ordinal()] = 25 * TrainSchedule.UNIT_TONNES;
        for (double share : new double[] { 0, 0.5, 1 }) {
            m.railPicture(new CityMap.RailPicture(false, share));
            List<TrainSchedule.Route> rs = m.trainRoutes();
            boolean clear = true, keeps = true;
            int[] left = new int[m.runs().yards().size()];
            for (TrainSchedule.Route rt : rs) {
                if (rt.works() || rt.siding() < 0) continue;
                clear &= rt.siding() >= standingOn(m, rt.yard());
                left[rt.yard()]++;
            }
            for (int i = 0; i < left.length; i++) {
                int tracks = TileRaster.tracksOf(m.types()[m.yardTypeOf(i)]);
                keeps &= left[i] == tracks - standingOn(m, i) && left[i] >= 1;
            }
            TrainSchedule month = TrainSchedule.of(7, bulk, none, rs);
            boolean over = false;
            int drawn = 0;
            for (TrainSchedule.Call c : month.calls()) {
                if (c.route() < 0) continue;
                drawn++;
                TrainSchedule.Route rt = rs.get(c.route());
                over |= !rt.works() && rt.siding() < standingOn(m, rt.yard());
            }
            out.printf("      with %d of %d wagon tracks standing (a depot's %d of %d): %d routes, %d of 25 unit trains drawn%n", CityMap.wagonTracksOf(share),
                    TileRaster.YARD_TRACKS, standingOn(m, 0), TileRaster.DEPOT_TRACKS, rs.size(), drawn);
            check("a yard's wagons standing (" + share + " of the fleet): no route is laid to a siding they stand on, and a yard keeps one siding clear", clear && keeps && !rs.isEmpty());
            check("...so no train stands on a siding wagons stand on", drawn > 0 && !over);
            check("...the routes kept against the map's changes and its picture: asked again, the same list", m.trainRoutes() == rs);
        }
        m.railPicture(before);
    }

    /** The most two neighbouring vehicles of a train may differ in heading, in degrees: 30 (FX5; on 0.8.14 a corner turned them 90 and a ladder 60). */
    static final double MOST_TURN_DEG = 30;

    /** ...and on a yard's ladder, the tightest curve the railway has: 45 degrees (FX5: an S of up to a few plots across over YARD_LADDER, 3 plots, turns a depot's neighbours 40; the long yards slice Y lays will lengthen the ladder and tighten this). */
    static final double LADDER_TURN_DEG = 45;

    /**
     * Section 9's trains on the curves (FX5, 0.8.18; Jerus, 2026-10-20: "do
     * trains make 90 degree turns?"): on the yard city's routes, at every
     * sampled moment of the month's trains and at fronts all along every
     * route, (a) no two neighbouring vehicles of a train differ in heading
     * by more than MOST_TURN_DEG, and (b) every vehicle shown whole, from
     * the first plot of the railway in, has its middle on track as the tiles
     * draw it at 8 px a plot: the pixel under it, or one of its eight
     * neighbours, is BALLAST, a SLEEPER or STEEL (dimmed toward the void
     * where the ground is not the city's). The vehicles before the first
     * plot of the railway run the world line's gap, where no track is drawn.
     */
    static void trainsOnTheCurves(CityMap m, List<TrainSchedule.Route> routes, TrainSchedule mix) {
        out.println("      the trains on the curves (FX5):");
        CityRuns runs = m.runs();
        double leg = TrainSchedule.LEG_SECONDS / TrainSchedule.MONTH_SECONDS, dwell = TrainSchedule.DWELL_SECONDS / TrainSchedule.MONTH_SECONDS;
        double worst = 0, awayFromEnd = 0;
        long pairs = 0;
        int where = -1;
        // The month's trains at their moments, and a hopper train (the longest) at fronts all along every route.
        for (TrainSchedule.Call c : mix.calls()) {
            TrainSchedule.Route rt = routes.get(c.route());
            for (int k = 0; k <= 48; k++) {
                double t = c.arrives() - leg + (2 * leg + dwell) * k / 48.0;
                TrainSchedule.Train tr = TrainSchedule.position(c, rt, t);
                if (tr == null) continue;
                List<TrainSchedule.Car> cars = TrainSchedule.cars(tr, rt);
                double d = tr.front();
                for (int i = 1; i < cars.size(); i++) {
                    double apart = headingApart(cars.get(i - 1).heading(), cars.get(i).heading());
                    double at = rt.length() - (d - (cars.get(i - 1).loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS) / 2);
                    d -= cars.get(i - 1).loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS;
                    pairs++;
                    if (apart > worst) { worst = apart; where = c.route(); }
                    if (!onLadder(rt, at) && apart > awayFromEnd) awayFromEnd = apart;
                }
            }
        }
        TrainSchedule.Call hopper = new TrainSchedule.Call(TrainSchedule.Kind.HOPPER, true, 0, 0.5);
        for (int r = 0; r < routes.size(); r++) {
            TrainSchedule.Route rt = routes.get(r);
            for (double front = 0.3; front <= rt.length() + 1; front += 0.37) {
                TrainSchedule.Train tr = new TrainSchedule.Train(hopper, front, true, false);
                List<TrainSchedule.Car> cars = TrainSchedule.cars(tr, rt);
                double d = tr.front();
                for (int i = 1; i < cars.size(); i++) {
                    double apart = headingApart(cars.get(i - 1).heading(), cars.get(i).heading());
                    double at = rt.length() - (d - (cars.get(i - 1).loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS) / 2);
                    d -= cars.get(i - 1).loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS;
                    pairs++;
                    if (apart > worst) { worst = apart; where = r; }
                    if (!onLadder(rt, at) && apart > awayFromEnd) awayFromEnd = apart;
                }
            }
        }
        out.printf("      %,d neighbouring pairs of vehicles on %d routes: the sharpest turn between two is %.1f degrees (route %d), %.1f degrees off the yards' ladders (onLadder())%n",
                pairs, routes.size(), worst, where, awayFromEnd);
        check("(a) no two neighbouring vehicles of a train differ in heading by more than " + (int) MOST_TURN_DEG + " degrees, at any sampled moment"
                + " and along every route, off its yard's ladder (on 0.8.14, 35 at the world line's join and 90 at a corner)", pairs > 0 && awayFromEnd <= MOST_TURN_DEG);
        check("...and on a yard's ladder no more than " + (int) LADDER_TURN_DEG + " (an S over YARD_LADDER plots, until the yards are as long as their trains)",
                pairs > 0 && worst <= LADDER_TURN_DEG);

        // (b) on the rail as drawn: the tiles at 8 px a plot, kept as they are first wanted.
        java.util.Map<Long, int[]> tiles = new java.util.HashMap<>();
        int px = 8, w = World.TILE * px;
        long checked = 0, off = 0;
        List<String> examples = new ArrayList<>();
        for (int r = 0; r < routes.size(); r++) {
            TrainSchedule.Route rt = routes.get(r);
            // The first point of the route on the railway's own plots: before it the route runs the world line's gap.
            double from = rt.length() + 1;
            for (int i = 1; i < rt.xs().length && from > rt.length(); i++) {
                byte f = runs.at((long) Math.floor(rt.xs()[i]), (long) Math.floor(rt.ys()[i]));
                if (f == CityRuns.F_RAIL || f == CityRuns.F_RAIL_OVER || f == CityRuns.F_YARD) from = rt.along()[i];
            }
            for (double front = from; front <= rt.length() + TrainSchedule.LOCO_PLOTS; front += 3.1) {
                TrainSchedule.Train tr = new TrainSchedule.Train(hopper, Math.min(front, rt.length()), true, false);
                List<TrainSchedule.Car> cars = TrainSchedule.cars(tr, rt);
                double d = tr.front();
                for (int k = 0; k < cars.size(); k++) {
                    TrainSchedule.Car car = cars.get(k);
                    double len = car.loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS, mid = d - len / 2;
                    d -= len;
                    if (car.alpha() < 1 || mid < from) continue;
                    checked++;
                    if (!onDrawnTrack(m, tiles, car.x(), car.y(), px, w)) {
                        off++;
                        if (examples.size() < 4) examples.add(String.format("route %d at (%.2f, %.2f), %.1f plots in", r, car.x(), car.y(), mid));
                    }
                }
            }
        }
        out.printf("      %,d vehicles shown whole on the railway, %,d of them off the drawn rail%s%n", checked, off, examples.isEmpty() ? "" : ": " + examples);
        check("(b) every vehicle shown whole has its middle on the track the tiles draw: its pixel or one of its eight neighbours is BALLAST, a SLEEPER or STEEL",
                checked > 0 && off == 0);
    }

    /**
     * Whether a point `fromEnd` plots before a route's end lies on its yard's ladder (FX5): the S between the fan's apex and the
     * siding's start, the siding being the route's last leg - within a locomotive's length either side. A route with no siding
     * has none. Since the long yards (slice Y1) the siding is most of a unit train long, so the ladder lies that far from the end.
     */
    static boolean onLadder(TrainSchedule.Route rt, double fromEnd) {
        if (rt.siding() < 0 || rt.xs().length < 2) return false;
        double[] al = rt.along();
        double siding = al[al.length - 1] - al[al.length - 2];
        return fromEnd >= siding - TrainSchedule.LOCO_PLOTS && fromEnd <= siding + TileRaster.YARD_LADDER + TrainSchedule.LOCO_PLOTS;
    }

    /** How far apart two headings are, in degrees, 0 to 180. */
    static double headingApart(double a, double b) {
        return Math.toDegrees(Math.abs(Math.atan2(Math.sin(a - b), Math.cos(a - b))));
    }

    /** Whether the pixel under plot point (x, y), or one of its eight neighbours, is the railway's as the tile at 8 px a plot draws it (its tile rastered once into `tiles`). */
    static boolean onDrawnTrack(CityMap m, java.util.Map<Long, int[]> tiles, double x, double y, int px, int w) {
        long tx = Math.floorDiv((long) Math.floor(x), World.TILE), ty = Math.floorDiv((long) Math.floor(y), World.TILE);
        int[] img = tiles.computeIfAbsent(tx * 1_000_003L + ty, k -> {
            TilePainter.Input in = new TilePainter.Input();
            m.tileInput(tx, ty, in);
            TilePainter.Painted p = new TilePainter.Painted();
            TilePainter.paint(in, p);
            int[] pixels = new int[w * w];
            TileRaster.raster(in, p, px, pixels);
            return pixels;
        });
        int cx = (int) Math.floor((x - tx * World.TILE) * px), cy = (int) Math.floor((y - ty * World.TILE) * px);
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int X = cx + dx, Y = cy + dy;
                if (X < 0 || Y < 0 || X >= w || Y >= w) continue;
                int c = img[Y * w + X];
                if (c == TileRaster.BALLAST || c == TileRaster.SLEEPER || c == TileRaster.STEEL) return true;
                if (c == TileRaster.blend(TileRaster.BALLAST, TileRaster.VOID, TileRaster.UNOWNED_DIM)
                        || c == TileRaster.blend(TileRaster.SLEEPER, TileRaster.VOID, TileRaster.UNOWNED_DIM)
                        || c == TileRaster.blend(TileRaster.STEEL, TileRaster.VOID, TileRaster.UNOWNED_DIM)) return true;
            }
        }
        return false;
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

        reliefOnMap(types);
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
            if (World.salt(far[k])) continue;
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
            if (World.salt(far[k])) sea++;
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
            if (!World.salt(mid[k])) land[side]++;
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
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's far views were not drawn");
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
                if (!World.salt(b)) land++;
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
                    if (t <= 1 - EarthTerrain.BLOB_FRAY / 2 && g[jj * n + ii] != World.FRESH && !World.salt(g[jj * n + ii])) coreMissed++;
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
                List<String> want = new ArrayList<>(List.of(LandMap.groundWords(World.DESERT, World.FLAT, -1), LandMap.NOT_THE_CITYS));
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
                int want = ll == null ? GlobePicture.SPACE : colours[picture.codeAt(ll[0], ll[1])];
                if (ll == null) space++; else pictured++;
                if (shot[j * fw + i] != want) wrong++;
            }
        }
        fg.set(key.lat(), key.lon(), (GlobeFrame.PICTURE_FROM_KM + GlobeFrame.FOUNDING_HANDOFF_KM) / 2);
        picture.view(fg, key, colours, GlobePicture.SPACE, shot);
        byte[] cls = fg.groundCodes(key, 0, 0, fw, fh), plainCls = fg.groundPiece(key, 0, 0, fw, fh);
        int groundWrong = 0, coverWrong = 0;
        for (int k = 0; k < shot.length; k++) {
            int i = k % fw, j = k / fw;
            int want = fg.onPlanet(i + 0.5, j + 0.5) ? colours[cls[k]] : GlobePicture.SPACE;
            if (shot[k] != want) groundWrong++;
            if (GlobePicture.classOf(cls[k]) != plainCls[k]) coverWrong++;
        }
        out.printf("      the founding globe, %d x %d: whole, %,d pixels of the picture (made in %.0f ms at %d') and %,d of space, %d unlike;"
                + " at %.2f km a pixel the far rule through the camera, %d unlike%n", fw, fh, pictured, madeMs, GlobePicture.FIRST_ARCMIN, space, wrong,
                fg.kmPx(), groundWrong);
        check("the founding screen's globe is the model's frame: its pixels the planet's picture's, space off the disc", fg.tier() == GlobeFrame.GROUND
                && pictured > 0 && space > 0 && wrong == 0);
        check("...and closer, the far rule's through the globe's camera (GlobeFrame.groundCodes(): its classes groundPiece()'s, its relief the far rule's), as the map's GROUND tier draws",
                groundWrong == 0 && coverWrong == 0);
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
            cost.groundCodes(key, w / 2 - 64, h / 2 - 64, 128, 128);
            double qMs = (System.nanoTime() - a0) / 1e6;
            if (round >= WARM_ROUNDS) {
                best[0] = Math.min(best[0], pMs);
                best[1] = Math.min(best[1], gMs);
                best[2] = Math.min(best[2], qMs);
            }
        }
        out.printf("      a %d x %d frame on %d cores: the PICTURE tier %.1f ms; the GROUND tier from the plane's nodes %.1f ms (a quarter of it on the move);"
                + " a %d-pixel piece of the far rule through the camera, with its relief (GlobeFrame.groundCodes()), %.1f ms on the worker%n", w, h, GlobePicture.cores(), best[0], best[1], 128, best[2]);
        reliefWordsAndLegend(sq.types);
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
                // ...from nothing when the treasury is in the red (PT1-M4: the played city's months before this section
                // no longer leave it in credit; Game.landPayable() reads max(0, cash), so cash + price stayed short of it).
                if (!g.canAffordParcel(step)) g.setCashForTest(Math.max(0, g.getCash()) + step.localPrice(g.getForeignAccounts().getRate()));
                if (!g.buyLandParcel(step.getId())) break;
                towardField++;
            }
        } finally {
            System.setOut(was);
        }
        out.printf("      %d offers bought toward the nearest iron field until one holds it%n", towardField);
        check("fixture: the city bought toward the iron field until an offer held it", market.richest(Resource.IRON) != null);
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
                    for (byte b : buf) if (!World.salt(b) && b != World.FRESH) dry++;
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

    /* =====================================================================
       16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2)
       ===================================================================== */

    /*
     * The district plans lay four avenues at 45 degrees from the lattice node nearest the founding site (DistrictPlan, AVENUES:
     * street plots of the role ARTERIAL in a staircase, their marks in DistrictPlan.avenue) and the painter draws each as one
     * smooth band along its arm's line through the steps' middles, not as the staircase's bars (TileRaster, THE AVENUES). On the
     * dense city, whose arms cross district seams and tile edges (its plans open every cell), what this proves is what the
     * eye sees: along each arm's line, between two open cells, every pixel is the street's - no gap where the staircase was
     * jagged, no step where the band crosses a tile's edge or a district's seam, its two sides as far from the line as the
     * street is wide; the same pixels from the same inputs, a tile rastered twice, from the kept copy the view paints from
     * (MapTiles.copy) and from a map built alike; no building on the band or on an outer plot, the ground the band crosses; and
     * a crossing by its roads, on hand-built tiles that cause each case - a staircase alone is none, an avenue across an
     * arterial a signal (the avenue's two directions among the roads that leave it), across a cell's corner or at the origin
     * where the four arms meet with a street along each axis one ring (ROUNDABOUT_ROADS) - and the screen over the city's own
     * origin, the four arms across it, within SCREEN_MS.
     */

    /** How many samples a step of an avenue's line is sampled at: four, a quarter step apart. */
    static final int AV_SAMPLES = 4;

    /** The farthest an arm is followed from the origin, in steps: 400, past a district and a half. */
    static final int AV_STEPS = 400;

    /**
     * The city the avenues are drawn on: Jerus's city x 9 on a square of 6 districts a side (his density, as the copies', every district
     * full) whose founding site is the dry place's moved AV_SHIFT_X plots east and AV_SHIFT_Y south - so the origin stands inside a
     * district, not on the corner of four (the copies' dry place is on one: the arms' district seams are then where the plans leave
     * their last cells closed), and the arms cross the district seams and the tiles' edges in open cells.
     */
    static final int AV_TIMES = 9, AV_SIDE = 6, AV_SHIFT_X = 104, AV_SHIFT_Y = 56;

    /** The avenues' city, built from the copies' constants (Squares): twice alike gives a twin. */
    static CityMap avenueCity(Squares sq) {
        long[] c = new long[sq.types.length];
        for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(JERUS_COUNTS[t] * AV_TIMES);
        for (BuildingVisual.Type t : sq.types) if (t != null && t.id() == Mining.IRON_MINE) c[t.id()] = 0;
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        return CityMap.square(sq.seed, sq.x + AV_SHIFT_X, sq.y + AV_SHIFT_Y, sq.types, AV_SIDE, capacity, c);
    }

    /** One map's tiles, kept as the section samples them: the rastered image at PX and what the painter was given and made of each plot. */
    static final class AvTiles {
        final CityMap m;
        final TilePainter.Input in = new TilePainter.Input();
        final TilePainter.Painted p = new TilePainter.Painted();
        final java.util.Map<Long, int[]> img = new java.util.HashMap<>();
        final java.util.Map<Long, byte[]> street = new java.util.HashMap<>(), mark = new java.util.HashMap<>(), use = new java.util.HashMap<>(),
                terrain = new java.util.HashMap<>(), flags = new java.util.HashMap<>();
        /** Flag bits of a plot: it is owned, a street passes beneath a highway on it, it is a bridge's. */
        static final int OWNED = 1, BENEATH = 2, BRIDGE = 4;

        AvTiles(CityMap m) { this.m = m; }

        private static long key(long tx, long ty) { return tx * 1_000_003L + ty; }

        /** The tile (tx, ty) as the painter makes it, loaded on first use. */
        private void load(long tx, long ty) {
            long k = key(tx, ty);
            if (img.containsKey(k)) return;
            m.tileInput(tx, ty, in);
            TilePainter.paint(in, p);
            int[] pixels = new int[(World.TILE * PX) * (World.TILE * PX)];
            TileRaster.raster(in, p, PX, pixels);
            byte[] f = new byte[TilePainter.PLOTS];
            for (int i = 0; i < f.length; i++) f[i] = (byte) ((in.owned[i] ? OWNED : 0) | (p.beneath[i] ? BENEATH : 0) | (p.bridge[i] ? BRIDGE : 0));
            img.put(k, pixels);
            street.put(k, in.street.clone());
            mark.put(k, in.avenue.clone());
            use.put(k, p.use.clone());
            terrain.put(k, in.terrain.clone());
            flags.put(k, f);
        }

        private int at(java.util.Map<Long, byte[]> what, long x, long y) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            load(tx, ty);
            return what.get(key(tx, ty))[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))] & 0xff;
        }

        /** The plan's street code, the avenue's mark, the painter's use, the ground and the flags of world plot (x, y). */
        int code(long x, long y) { return at(street, x, y); }
        int mark(long x, long y) { return at(mark, x, y); }
        int use(long x, long y) { return at(use, x, y); }
        int ground(long x, long y) { return at(terrain, x, y); }
        int flag(long x, long y) { return at(flags, x, y); }

        /** The pixel of the near view's raster at world pixel (X, Y). */
        int pixel(long X, long Y) {
            long tx = Math.floorDiv(X, World.TILE * PX), ty = Math.floorDiv(Y, World.TILE * PX);
            load(tx, ty);
            return img.get(key(tx, ty))[(int) (Math.floorMod(Y, World.TILE * PX) * (World.TILE * PX) + Math.floorMod(X, World.TILE * PX))];
        }
    }

    /** The lattice node the avenues run out from, nearest the founding site (DistrictPlan's avSetup), in world plots: {x, y}. */
    static long[] avenueOrigin(CityMap m) {
        return new long[] { Math.floorDiv(m.land().siteX() + DistrictPlan.LATTICE / 2, (long) DistrictPlan.LATTICE) * DistrictPlan.LATTICE,
                Math.floorDiv(m.land().siteY() + DistrictPlan.LATTICE / 2, (long) DistrictPlan.LATTICE) * DistrictPlan.LATTICE };
    }

    /** Whether the avenue is open at step k of arm a, as the near view draws it: the staircase's plots S_k, C_(k+1) and S_(k+1) laid as a surfaced street on the ground, and the outer plot O_(k+1) between them owned dry ground no building stands on - open, or a street the avenue crosses. */
    static boolean avenueOpen(AvTiles t, long ox, long oy, int a, int k) {
        int dx = DistrictPlan.AVENUE_DX[a], dy = DistrictPlan.AVENUE_DY[a];
        long[][] laid = { { ox + (long) dx * k, oy + (long) dy * k }, { ox + (long) dx * (k + 1), oy + (long) dy * k }, { ox + (long) dx * (k + 1), oy + (long) dy * (k + 1) } };
        for (long[] q : laid) {
            int kind = TilePainter.kindOf(t.code(q[0], q[1]));
            if (t.mark(q[0], q[1]) == 0 || (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) || t.use(q[0], q[1]) != TilePainter.ROAD
                    || t.flag(q[0], q[1]) != AvTiles.OWNED) return false;
        }
        long x = ox + (long) dx * k, y = oy + (long) dy * (k + 1);
        int ground = t.ground(x, y);
        int kind = TilePainter.kindOf(t.code(x, y)), use = t.use(x, y);
        boolean street = use == TilePainter.ROAD && (kind == BuildingVisual.GRAVEL || kind == BuildingVisual.PAVED) && (t.flag(x, y) & (AvTiles.BENEATH | AvTiles.BRIDGE)) == 0;
        return (use == TilePainter.EMPTY || street) && ground != World.FRESH && !World.salt(ground) && t.mark(x, y) == 0 && (t.flag(x, y) & AvTiles.OWNED) != 0;
    }

    /** Whether a pixel is a surfaced street's: the paved colour, the gravel's, or its grain. */
    static boolean streetColour(int c) {
        return c == TileRaster.ROAD[BuildingVisual.PAVED] || c == TileRaster.ROAD[BuildingVisual.GRAVEL] || c == TileRaster.GRAVEL_GRAIN;
    }

    /** Where a hand-built tile's avenues stand: the avenue plots of the arms `arms` (bit a) from origin (ox, oy) for `len` steps - a plot two arms share the first arm's mark, as DistrictPlan lays it - and 0 for none. */
    static int handMark(int ox, int oy, int arms, int len, int x, int y) {
        for (int a = 0; a < 4; a++) {
            if ((arms & 1 << a) == 0) continue;
            int dx = DistrictPlan.AVENUE_DX[a], dy = DistrictPlan.AVENUE_DY[a];
            int step = CityRuns.M_DIAG | (a << CityRuns.M_DIAG_SHIFT);
            for (int k = 0; k <= len; k++) {
                if (x == ox + dx * k && y == oy + dy * k) return step;
                if (k >= 1 && x == ox + dx * k && y == oy + dy * (k - 1)) return step | CityRuns.M_CORNER;
            }
        }
        return 0;
    }

    /** A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: the avenue plots of the arms `arms` from the origin laid as arterial streets with their marks, and the streets `extra` besides; the origin given when `withOrigin`. */
    static int[] handAvenue(int ox, int oy, int arms, int len, JunctionCheck.Streets extra, boolean withOrigin) {
        JunctionCheck.Streets f = (x, y) -> handMark(ox, oy, arms, len, x, y) != 0 ? JunctionCheck.ARTERIAL : extra == null ? 0 : extra.at(x, y);
        TilePainter.Input in = JunctionCheck.inputFor(0, 0, f);
        int r = TilePainter.REACH;
        for (int y = -r; y < World.TILE + r; y++) for (int x = -r; x < World.TILE + r; x++) in.setAvenue(x, y, handMark(ox, oy, arms, len, x, y));
        if (withOrigin) { in.avenueOx = ox; in.avenueOy = oy; }
        return JunctionCheck.raster(in, JunctionCheck.paint(in), 8);
    }

    static void avenuesDrawn(Squares sq) {
        out.println("\n--- 16. the avenues drawn (CL4): four diagonal avenues as smooth bands - one line across the district seams and tile edges,"
                + " the same pixels from the same inputs, no building on the band, a crossing by its roads - and the screen within 80 ms ---");
        CityMap m = avenueCity(sq);
        out.printf("      the avenues' city: x %d on %d districts a side, its founding site (%d, %d), %,d buildings%n", AV_TIMES, AV_SIDE, m.land().siteX(), m.land().siteY(),
                Arrays.stream(m.totals()).sum());
        AvTiles t = new AvTiles(m);
        long[] o = avenueOrigin(m);
        long ox = o[0], oy = o[1];
        int tile = World.TILE;

        // ---- THE LINE: along each arm, every step open, every sample the street's colour, its sides as far out as the street is wide
        int samples = 0, bad = 0, sidesBad = 0, runs = 0, longest = 0, seams = 0, edges = 0, seamSamples = 0, edgeSamples = 0, steps = 0;
        int[] perArm = new int[4];
        java.util.Set<Integer> fills = new java.util.HashSet<>();
        for (BuildingVisual.Type ty : sq.types) if (ty != null) { fills.add(ty.fill()); fills.add(ty.edge()); }
        int onBuilding = 0;
        for (int a = 0; a < 4; a++) {
            int dx = DistrictPlan.AVENUE_DX[a], dy = DistrictPlan.AVENUE_DY[a];
            int run = 0;
            for (int k = 1; k <= AV_STEPS + 1; k++) {
                boolean open = k <= AV_STEPS && avenueOpen(t, ox, oy, a, k);
                if (!open) {
                    if (run > 0) { runs++; longest = Math.max(longest, run); }
                    run = 0;
                    continue;
                }
                run++;
                steps++;
                perArm[a]++;
                long sx = ox + (long) dx * k, sy = oy + (long) dy * k;
                int code = t.code(sx, sy), half2 = Math.max(1, (int) Math.round(TileRaster.SURFACE[Math.max(1, Math.min(2, (code >> TilePainter.S_WIDTH_SHIFT) & 3))] * PX));
                // The seam of two districts and the edge of two tiles, crossed between this step and the next.
                boolean seam = Math.floorDiv(sx, CityMap.DISTRICT) != Math.floorDiv(sx + dx, CityMap.DISTRICT) || Math.floorDiv(sy, CityMap.DISTRICT) != Math.floorDiv(sy + dy, CityMap.DISTRICT);
                boolean edge = Math.floorDiv(sx, tile) != Math.floorDiv(sx + dx, tile) || Math.floorDiv(sy, tile) != Math.floorDiv(sy + dy, tile);
                if (seam) seams++;
                if (edge) edges++;
                for (int s = 0; s < AV_SAMPLES; s++) {
                    double f = s / (double) AV_SAMPLES, wx = sx + 0.5 + dx * f, wy = sy + 0.5 + dy * f;
                    int c = t.pixel((long) Math.floor(wx * PX), (long) Math.floor(wy * PX));
                    samples++;
                    if (seam || edge) { if (seam) seamSamples++; if (edge) edgeSamples++; }
                    if (!streetColour(c)) bad++;
                    if (fills.contains(c)) onBuilding++;
                    // Its two sides, as far out as the band is wide (less a pixel): the street's colour too - no step, no narrowing.
                    double inner = Math.max(0, half2 / 2.0 - 1.0) / PX / Math.sqrt(2);
                    for (int side = -1; side <= 1; side += 2) {
                        double qx = wx + side * -dy * inner, qy = wy + side * dx * inner;
                        if (!streetColour(t.pixel((long) Math.floor(qx * PX), (long) Math.floor(qy * PX)))) sidesBad++;
                    }
                }
            }
        }
        out.printf("      the avenues from the origin (%d, %d) on the dense city: %,d open steps in %d runs (the longest %d), arms %s; %,d samples, %d of them across a district's seam and %d across a tile's edge%n",
                ox, oy, steps, runs, longest, Arrays.toString(perArm), samples, seamSamples, edgeSamples);
        check("fixture: the avenues run open for steps across a district's seam and a tile's edge, on every arm", steps > 0 && seams > 0 && edges > 0 && Arrays.stream(perArm).allMatch(n -> n > 0));
        check("THE LINE: along every arm's line between two open cells, every sample is the street's colour - across the tile edges and the seams, no gap and no staircase", samples > 0 && bad == 0);
        check("...and the line's two sides, as far out as the street is wide less a pixel, are the street's too: no step where the band crosses an edge or a seam, no narrowing", sidesBad == 0);

        // ---- THE SAME PIXELS: a tile twice, from the kept copy the view paints from, and from a map built alike
        TilePainter.Input in = new TilePainter.Input(), kept = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted(), pk = new TilePainter.Painted();
        int[] img = new int[(tile * PX) * (tile * PX)], again = img.clone(), copy = img.clone();
        long tx0 = Math.floorDiv(ox, tile), ty0 = Math.floorDiv(oy, tile);
        int tilesWithAvenue = 0, twice = 0, copies = 0, stamps = 0, moved = 0;
        for (int j = -2; j <= 2; j++) {
            for (int i = -2; i <= 2; i++) {
                m.tileInput(tx0 + i, ty0 + j, in);
                if (in.avenueMarks == 0) continue;
                tilesWithAvenue++;
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, PX, img);
                TileRaster.raster(in, p, PX, again);
                if (Arrays.equals(img, again)) twice++;
                MapTiles.copy(in, kept);
                // (MapTiles.copy leaves the interchanges' reserves out, as it always has: handed over here, so the stamp and the pixels are the avenues' alone.)
                kept.reserves = in.reserves;
                kept.reserve = in.reserve.clone();
                TilePainter.paint(kept, pk);
                TileRaster.raster(kept, pk, PX, copy);
                if (Arrays.equals(img, copy)) copies++;
                if (MapTiles.stamp(in) == MapTiles.stamp(kept)) stamps++;
                // ...and a tile whose avenue plot beside it moves repaints: its stamp moves with the marks.
                kept.setAvenue(-1, 0, kept.avenueAt(-1, 0) == 0 ? CityRuns.M_DIAG : 0);
                if (MapTiles.stamp(in) != MapTiles.stamp(kept)) moved++;
            }
        }
        check("fixture: the tiles about the origin have avenues about them", tilesWithAvenue >= 9);
        check("THE SAME PIXELS: each such tile rastered twice paints the same; its kept copy (MapTiles.copy) the same, with the same stamp", twice == tilesWithAvenue && copies == tilesWithAvenue && stamps == tilesWithAvenue);
        check("...and a copy whose mark beside the tile moved has another stamp: a tile repaints when an avenue plot beside it is laid", moved == tilesWithAvenue);
        CityMap twin = avenueCity(sq);
        TilePainter.Input in2 = new TilePainter.Input();
        TilePainter.Painted p2 = new TilePainter.Painted();
        int[] other = img.clone();
        int alike = 0, compared = 0;
        for (int j = -2; j <= 2; j++) {
            for (int i = -2; i <= 2; i++) {
                long tx = tx0 + i, ty = ty0 + j;
                m.tileInput(tx, ty, in);
                if (in.avenueMarks == 0) continue;
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, PX, img);
                twin.tileInput(tx, ty, in2);
                TilePainter.paint(in2, p2);
                TileRaster.raster(in2, p2, PX, other);
                compared++;
                if (Arrays.equals(img, other)) alike++;
            }
        }
        out.printf("      the avenues' city and its twin built the same way: %d tiles with an avenue about them compared%n", compared);
        check("...and a tile of an avenue from a twin map built alike paints the same pixels", compared >= 9 && alike == compared);

        // ---- NO BUILDING ON THE BAND: not on a staircase plot, not on an outer plot, and no building's colour on the line
        int staircase = 0, outerPlots = 0, builtOn = 0, builtOuter = 0;
        for (int a = 0; a < 4; a++) {
            int dx = DistrictPlan.AVENUE_DX[a], dy = DistrictPlan.AVENUE_DY[a];
            for (int k = 1; k <= AV_STEPS; k++) {
                long sx = ox + (long) dx * k, sy = oy + (long) dy * k;
                long[][] plots = { { sx, sy }, { sx, sy - dy } };
                for (int q = 0; q < 2; q++) {
                    if (t.mark(plots[q][0], plots[q][1]) == 0) continue;
                    staircase++;
                    if (t.use(plots[q][0], plots[q][1]) == TilePainter.BUILDING) builtOn++;
                }
                // The outer plot O_k = S_(k-1) + (0, dy), between the steps S_(k-1) and S_k when both are laid.
                long ux = sx - dx, uy = sy;
                if (t.mark(ux, uy) == 0 && t.mark(sx, sy) != 0 && t.mark(sx - dx, sy - dy) != 0) {
                    outerPlots++;
                    if (t.use(ux, uy) == TilePainter.BUILDING) builtOuter++;
                }
            }
        }
        out.printf("      the band's plots on the dense city: %,d staircase plots and %,d outer plots, %d of them under a building; %d samples of the line in a building's colour%n",
                staircase, outerPlots, builtOn + builtOuter, onBuilding);
        check("NO BUILDING ON THE BAND: not one staircase plot and not one outer plot (the ground the band crosses) is under a building, and no sample of the line is a building's fill or edge",
                staircase > 0 && outerPlots > 0 && builtOn == 0 && builtOuter == 0 && onBuilding == 0);

        // ---- THE CROSSINGS: hand-built tiles at 8 px a plot, each causing its case
        int px = 8, w = World.TILE * px, paved = TileRaster.ROAD[BuildingVisual.PAVED];
        JunctionCheck.Streets col16 = (x, y) -> x == 16 ? JunctionCheck.ARTERIAL : 0;
        JunctionCheck.Streets cross16 = (x, y) -> x == 16 || y == 16 ? JunctionCheck.ARTERIAL : 0;
        JunctionCheck.Streets axes16 = (x, y) -> x == 16 || y == 16 ? JunctionCheck.STREET : 0;
        // A staircase alone: the avenue's arm out from (4, 4), no street across it.
        int[] alone = handAvenue(4, 4, 1, 24, null, true);
        boolean none = JunctionCheck.noneOf(alone, TileRaster.ISLAND, TileRaster.ZEBRA, TileRaster.JUNCTION_BOX);
        check("CROSSINGS: a staircase's plots alone are no junction - an avenue's arm with no street across it draws no box, no zebra, no ring (and is drawn: its paved band is there)",
                none && JunctionCheck.hasColour(alone, w, 10, 10, px, paved));
        // An avenue across an arterial: a signal - one box over the avenue's plots in the arterial's column, a zebra on the arterial's two arms.
        int[] signal = handAvenue(4, 4, 1, 24, col16, true);
        boolean boxOnlyThere = true;
        for (int y = 0; y < World.TILE; y++) for (int x = 0; x < World.TILE; x++)
            if ((x < 15 || x > 17 || y < 14 || y > 17) && JunctionCheck.hasColour(signal, w, x, y, px, TileRaster.JUNCTION_BOX)) boxOnlyThere = false;
        check("...an avenue across an arterial is a signal: the box on the avenue's two plots in the arterial's column, a zebra on each of the arterial's arms, no ring (four roads leave it, the avenue's two directions two of them)",
                JunctionCheck.hasColour(signal, w, 16, 15, px, TileRaster.JUNCTION_BOX) && JunctionCheck.hasColour(signal, w, 16, 16, px, TileRaster.JUNCTION_BOX)
                        && JunctionCheck.hasColour(signal, w, 16, 14, px, TileRaster.ZEBRA) && JunctionCheck.hasColour(signal, w, 16, 17, px, TileRaster.ZEBRA)
                        && JunctionCheck.noneOf(signal, TileRaster.ISLAND) && boxOnlyThere);
        // The control: two arterials crossing alone are a signal; the avenue through their corner, the cell's, makes it a ring.
        int[] corner = handAvenue(4, 4, 0, 0, cross16, false);
        check("...two arterials crossing with no avenue: a signal, the box at the corner, no ring",
                JunctionCheck.hasColour(corner, w, 16, 16, px, TileRaster.JUNCTION_BOX) && JunctionCheck.noneOf(corner, TileRaster.ISLAND));
        int[] ring = handAvenue(4, 4, 1, 24, cross16, true);
        double inner = TileRaster.ROUNDABOUT_ISLAND + (2 - 1) / 2.0;
        check("...the avenue through that corner (" + TileRaster.ROUNDABOUT_ROADS + " or more roads: its two directions with the arterials' four): one ring about the crossing's middle, ISLAND exactly within ROUNDABOUT_ISLAND + half a plot of it, no box, no zebra",
                JunctionCheck.islandIsRound(ring, w, px, 17.0, 16.0, inner) && JunctionCheck.hasColour(ring, w, 16, 16, px, TileRaster.ISLAND) && JunctionCheck.noneOf(ring, TileRaster.JUNCTION_BOX, TileRaster.ZEBRA));
        // The origin: four arms laid; with a street along each axis eight roads, one ring about the origin's plot; with none, four, a signal.
        int[] eight = handAvenue(16, 16, 15, 12, axes16, true);
        double innerO = TileRaster.ROUNDABOUT_ISLAND + (3 - 1) / 2.0;
        check("...the origin where the four arms meet and a street runs along each axis (eight roads): one ring about the origin's plot, ISLAND exactly within ROUNDABOUT_ISLAND + one plot of its middle, no box",
                JunctionCheck.islandIsRound(eight, w, px, 16.5, 16.5, innerO) && JunctionCheck.hasColour(eight, w, 16, 16, px, TileRaster.ISLAND) && JunctionCheck.noneOf(eight, TileRaster.JUNCTION_BOX));
        int[] four = handAvenue(16, 16, 15, 12, null, true);
        check("...and the four arms alone (four roads): a signal, the box on the origin's plot, no ring",
                JunctionCheck.hasColour(four, w, 16, 16, px, TileRaster.JUNCTION_BOX) && JunctionCheck.noneOf(four, TileRaster.ISLAND));
        // The second line through the origin comes from the geometry: a plot two arms share carries the first arm's mark only, so the outer plots
        // beside the origin, which no step flanks with its arm's mark, are crossed by the arms' other diagonal from the origin given.
        check("...and the arms' second diagonal is drawn across the origin's plots from the origin's place (the plots a step of the other arm flanks: (16, 15) and (16, 17), the box on both)",
                JunctionCheck.hasColour(four, w, 16, 15, px, TileRaster.JUNCTION_BOX) && JunctionCheck.hasColour(four, w, 16, 17, px, TileRaster.JUNCTION_BOX));

        // ---- THE SCREEN: the founding site's, the four arms across it, within SCREEN_MS
        TilePainter.Input sin = new TilePainter.Input();
        TilePainter.Painted sp = new TilePainter.Painted();
        int[] simg = new int[(tile * PX) * (tile * PX)];
        long stx0 = Math.floorDiv(sq.x, tile) - SCREEN_ACROSS / 2, sty0 = Math.floorDiv(sq.y, tile) - SCREEN_DOWN / 2;
        int avenueTiles = 0;
        double bestWith = Double.MAX_VALUE, bestWithout = Double.MAX_VALUE;
        System.gc();
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int variant = 0; variant < 2; variant++) {
                int n = 0;
                long t0 = System.nanoTime();
                for (int j = 0; j < SCREEN_DOWN; j++) {
                    for (int c = 0; c < SCREEN_ACROSS; c++) {
                        m.tileInput(stx0 + c, sty0 + j, sin);
                        if (sin.avenueMarks != 0) n++;
                        // The second variant is the same screen with its avenues' marks cleared, as it was drawn before CL4: the avenues' share of the time.
                        if (variant == 1) sin.clearAvenues();
                        TilePainter.paint(sin, sp);
                        TileRaster.raster(sin, sp, PX, simg);
                    }
                }
                double ms = (System.nanoTime() - t0) / 1e6;
                if (variant == 0) avenueTiles = n;
                if (round >= WARM_ROUNDS) { if (variant == 0) bestWith = Math.min(bestWith, ms); else bestWithout = Math.min(bestWithout, ms); }
            }
        }
        out.printf("      x %,.0f: the screen's %d tiles at the founding site, %d with an avenue about them - terrain, the plans' tiles, paint and raster at %d px - in %.1f ms (%.3f ms a tile); the same screen without the avenues' marks %.1f ms%n",
                TIMES[DENSE], SCREEN_TILES, avenueTiles, PX, bestWith, bestWith / SCREEN_TILES, bestWithout);
        check("THE SCREEN: the dense screen at the founding site, the four avenues across it (tiles with avenues about them: " + avenueTiles + "), paints and rasters in no more than " + (int) SCREEN_MS + " ms",
                avenueTiles > 0 && Timing.within(bestWith, SCREEN_MS));
    }

    /* =====================================================================
       17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2)
       ===================================================================== */

    /*
     * The district plans lay two kinds of street as curves (DistrictPlan, CURVES): a crescent, the cross street of a long-block homes
     * cell 16 cells or more from the founding site, a quarter of a circle between its rings, and the street a cut lays along water, a
     * path of plots from a dead end to the street it meets, its line the simplified middles of the plots with its corners rounded
     * (StreetCurves); and the painter draws each as one smooth band along its line, not as the staircase's bars (TileRaster, THE
     * CURVES). On the dense city's districts three to eight districts east and south of the founding site, where the crescents are,
     * what this proves is what the eye sees: along each curve's line, from end to end, every sample is the street's colour, the band's
     * middle stays within CV_CENTRE_PX of the line and the band as wide as the street (no staircase step, however many plots the
     * line crosses), and the band turns between neighbouring samples no sharper than the line does; the same pixels from the same
     * inputs, a tile rastered twice, from the view's kept copy and from a map built alike; no building under the curve's plots or
     * over its pixels; and, on hand-built tiles that cause each case, a crescent alone no junction, the street that crosses it
     * running on across the band, a crossing beside it the signal it was; and the screen over the districts, within SCREEN_MS.
     */

    /** The districts the curves are looked for in, in districts east and south of the founding site's: 3 to 8 - past CRESCENT_FROM (16 cells, two districts) wherever the cell lies. */
    static final int CV_FROM = 3, CV_TO = 8;

    /** The most crescents and shore curves the audit follows: 8 and 16 (the shore curves that cross a tile's edge, and those that bend, first). */
    static final int CV_CRESCENTS = 8, CV_SHORES = 16;

    /** How far apart, in plots, the turn audit's samples are: 4 - at a crescent's radius (22.6 plots) a turn of 0.18 radian between neighbours. */
    static final int CV_TURN_SPAN = 4;

    /** How far the band's middle, measured across it from the pixels, may lie from the line: a pixel - the least a raster can place a band, and less than the 2.8 pixels at 4 px a plot a staircase's bars lie from the arc through them. */
    static final double CV_CENTRE_PX = 1.0;

    /** How much narrower than its street the band, measured across it, may measure: 1.5 pixels (a pixel's rounding at either end and a half). */
    static final double CV_NARROW_PX = 1.5;

    /** How much the line may turn within a plot and a quarter either side of a sample for the band to be measured across it there: 0.2 radian - a crescent turns 0.06 over that; a shore street's corner (rounded to as little as half a plot, where its legs are short) turns more, and the band beside its other leg lies across the normal. */
    static final double CV_GENTLE = 0.2;

    /** The share of a kind's samples the band must have been measured across (crescents, shore curves): a half, and 35% - a shore's line turns a corner every few plots, and the band is not measured within a plot and a quarter of one. */
    static final double[] CV_MEASURED = { 0.5, 0.35 };

    /** A curve found in a Drawn, with where its district's frame lies in the world. */
    record CvFound(CityMap.District d, CityMap.DrawnCurve c, long ox, long oy) {
        /** Its line in the world's plots: {xs, ys}. */
        double[][] line() {
            double[][] l = TileRaster.curveLine(c.kind, c.geom, 0, c.geom.length);
            for (int k = 0; k < l[0].length; k++) { l[0][k] += ox; l[1][k] += oy; }
            return l;
        }

        /** Plot k of its list in the world's plots: {x, y}. */
        long[] plot(int k) {
            int q = c.plots[k] & (CityMap.CURVE_BAND_BIT - 1);
            return new long[] { ox + q % DistrictPlan.FRAME, oy + q / DistrictPlan.FRAME };
        }
    }

    /** One map's tiles as the curves section samples them at `px` a plot: the rastered image, and what the painter was given and made of each plot. */
    static final class CvTiles {
        final CityMap m;
        final int px;
        final TilePainter.Input in = new TilePainter.Input();
        final TilePainter.Painted p = new TilePainter.Painted();
        final java.util.Map<Long, int[]> img = new java.util.HashMap<>();
        final java.util.Map<Long, byte[]> street = new java.util.HashMap<>(), use = new java.util.HashMap<>(), curve = new java.util.HashMap<>(), flags = new java.util.HashMap<>();
        final java.util.Map<Long, short[]> types = new java.util.HashMap<>();

        /** Whether the tiles are drawn without the curves (the control: as they were before CL4). */
        final boolean bare;

        CvTiles(CityMap m, int px) { this(m, px, false); }

        CvTiles(CityMap m, int px, boolean bare) { this.m = m; this.px = px; this.bare = bare; }

        private static long key(long tx, long ty) { return tx * 1_000_003L + ty; }

        private void load(long tx, long ty) {
            long k = key(tx, ty);
            if (img.containsKey(k)) return;
            m.tileInput(tx, ty, in);
            // The control tiles are drawn as before CL4: no curve about them, the staircases' bars.
            if (bare) in.clearCurves();
            TilePainter.paint(in, p);
            int[] pixels = new int[(World.TILE * px) * (World.TILE * px)];
            TileRaster.raster(in, p, px, pixels);
            byte[] f = new byte[TilePainter.PLOTS];
            for (int i = 0; i < f.length; i++) f[i] = (byte) ((in.owned[i] ? 1 : 0) | (p.bridge[i] ? 2 : 0) | (p.beneath[i] ? 4 : 0));
            img.put(k, pixels);
            street.put(k, in.street.clone());
            use.put(k, p.use.clone());
            curve.put(k, in.curve.clone());
            flags.put(k, f);
            short[] bt = new short[TilePainter.PLOTS];
            for (int i = 0; i < bt.length; i++) bt[i] = (short) (p.use[i] == TilePainter.BUILDING && p.bld[i] > 0 ? p.btype[p.bld[i] - 1] : -1);
            types.put(k, bt);
        }

        private int at(java.util.Map<Long, byte[]> what, long x, long y) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            load(tx, ty);
            return what.get(key(tx, ty))[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))] & 0xff;
        }

        /** The plan's street code, the painter's use, the curve mark and the flags (1 owned, 2 bridge, 4 beneath) of world plot (x, y). */
        int code(long x, long y) { return at(street, x, y); }
        int use(long x, long y) { return at(use, x, y); }
        int mark(long x, long y) { return at(curve, x, y); }
        int flag(long x, long y) { return at(flags, x, y); }

        /** The type id of the building on world plot (x, y), or -1 for none. */
        int type(long x, long y) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            load(tx, ty);
            return types.get(key(tx, ty))[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
        }

        /** The pixel of the raster at world pixel (X, Y). */
        int pixel(long X, long Y) {
            long tx = Math.floorDiv(X, World.TILE * px), ty = Math.floorDiv(Y, World.TILE * px);
            load(tx, ty);
            return img.get(key(tx, ty))[(int) (Math.floorMod(Y, World.TILE * px) * (World.TILE * px) + Math.floorMod(X, World.TILE * px))];
        }
    }

    /** Whether a pixel is a surfaced street's, owned or dimmed as ground the city has not bought is: the paved colour, the gravel's or its grain. */
    static boolean cvStreetColour(int c) {
        for (int k : new int[] { TileRaster.ROAD[BuildingVisual.PAVED], TileRaster.ROAD[BuildingVisual.GRAVEL], TileRaster.GRAVEL_GRAIN }) {
            if (c == k || c == TileRaster.blend(k, TileRaster.VOID, TileRaster.UNOWNED_DIM)) return true;
        }
        return false;
    }

    /** What an audit of one curve's drawn line found. */
    static final class CvAudit {
        int samples, badColour, onBuilding, edges, audited, narrow, off, turns, turnsBad, tracks;
        double worstCentre, worstExcess, worstIdeal;
        /** The first sample that was not the street's colour or lay off the line, for the line a failure prints: where, what. */
        String firstBad = "";
    }

    /** The own plots of a curve (the first `own` of its list) as a set of world plot keys. */
    static java.util.Set<Long> cvPlotSet(CvFound f) {
        java.util.Set<Long> set = new java.util.HashSet<>();
        for (int k = 0; k < f.c().own; k++) { long[] q = f.plot(k); set.add(q[0] * 2_000_003L + q[1]); }
        return set;
    }

    /**
     * One curve's line audited on the raster t (THE CURVES DRAWN): samples every pixel along it, from its first point to its last. Each
     * sample is the street's colour and no building's (fills). Where no other street lies within a plot of the sample (the plots about
     * it are the curve's own or ground) and the street's kind and width are the same on the plots it is drawn on, the band is
     * measured across the line, from the sample's pixel out along the normal until the colour changes: its middle within
     * CV_CENTRE_PX of the line, its width within CV_NARROW_PX of the street's (and not a plot's width more). Then, every
     * CV_TURN_SPAN plots along it, the middles measured are joined and the turn at each is no more than the line's own turn there
     * with the error two measured middles allow a chord of that length.
     */
    static CvAudit cvAudit(CvTiles t, CvFound f, java.util.Set<Integer> fills) {
        CvAudit a = new CvAudit();
        int px = t.px;
        double[][] line = f.line();
        java.util.Set<Long> mine = cvPlotSet(f);
        // The samples: a pixel apart along the line, each with its heading.
        java.util.List<double[]> pts = new java.util.ArrayList<>();
        double carry = 0, step1 = 1.0 / px;
        for (int s = 0; s + 1 < line[0].length; s++) {
            double ax = line[0][s], ay = line[1][s], dx = line[0][s + 1] - ax, dy = line[1][s + 1] - ay, len = Math.hypot(dx, dy);
            if (len < 1e-12) continue;
            double hx = dx / len, hy = dy / len, at = carry;
            while (at <= len + 1e-9) {
                pts.add(new double[] { ax + hx * at, ay + hy * at, hx, hy });
                at += step1;
            }
            carry = at - len;
        }
        int step = CV_TURN_SPAN * px, n = pts.size();
        java.util.List<double[]> kept = new java.util.ArrayList<>();
        long prevTx = Long.MIN_VALUE, prevTy = Long.MIN_VALUE;
        for (int index = 0; index < n; index++) {
            double[] pt = pts.get(index);
            double wx = pt[0], wy = pt[1], hx = pt[2], hy = pt[3];
            long X = (long) Math.floor(wx * px), Y = (long) Math.floor(wy * px), fx = (long) Math.floor(wx), fy = (long) Math.floor(wy);
            a.samples++;
            int c = t.pixel(X, Y);
            // (A plot of a street the city has not bought a road for is a track, dashes and no band: it is no sample of the band's colour.)
            if (TilePainter.kindOf(t.code(fx, fy)) == TilePainter.TRACK) a.tracks++;
            else if (!cvStreetColour(c)) {
                a.badColour++;
                if (a.firstBad.isEmpty()) a.firstBad = String.format("colour %08x at (%.2f, %.2f), plot code %02x use %d", c, wx, wy, t.code(fx, fy), t.use(fx, fy));
            }
            if (fills.contains(c)) a.onBuilding++;
            long tx = Math.floorDiv(fx, World.TILE), ty = Math.floorDiv(fy, World.TILE);
            if (prevTx != Long.MIN_VALUE && (tx != prevTx || ty != prevTy)) a.edges++;
            prevTx = tx;
            prevTy = ty;
            // The band measured across the line where it is measurable: nothing else a street about it, the street the same on the plots about, and the line
            // gentle - its heading within CV_GENTLE (as the distance between unit headings, near enough the angle) of itself at every quarter plot out to a plot and a quarter either way (at a tight corner the band of the leg beside it lies across the normal).
            boolean clean = true;
            int code = -1;
            for (long y = fy - 1; y <= fy + 1 && clean; y++) {
                for (long x = fx - 1; x <= fx + 1; x++) {
                    int cd = t.code(x, y);
                    if (TilePainter.kindOf(cd) == 0) continue;
                    if (!mine.contains(x * 2_000_003L + y)) { clean = false; break; }
                    if (code >= 0 && (cd & 0x7f) != (code & 0x7f)) { clean = false; break; }
                    code = cd;
                }
            }
            int kind = code < 0 ? 0 : TilePainter.kindOf(code);
            if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) clean = false;
            for (int d = px / 4; d <= px * 5 / 4 && clean; d += Math.max(1, px / 4)) {
                double[] before = pts.get(Math.max(0, index - d)), after = pts.get(Math.min(n - 1, index + d));
                if (Math.hypot(before[2] - hx, before[3] - hy) > CV_GENTLE) clean = false;
                if (Math.hypot(after[2] - hx, after[3] - hy) > CV_GENTLE) clean = false;
            }
            double centre = Double.NaN;
            if (clean) {
                double nx = -hy, ny = hx, lim = px + 2, pos = 0, neg = 0;
                while (pos < lim && cvStreetColour(t.pixel((long) Math.floor(wx * px + nx * (pos + 0.25)), (long) Math.floor(wy * px + ny * (pos + 0.25))))) pos += 0.25;
                while (neg < lim && cvStreetColour(t.pixel((long) Math.floor(wx * px - nx * (neg + 0.25)), (long) Math.floor(wy * px - ny * (neg + 0.25))))) neg += 0.25;
                int want = Math.max(1, (int) Math.round(TileRaster.SURFACE[Math.max(1, Math.min(2, (code >> TilePainter.S_WIDTH_SHIFT) & 3))] * px));
                centre = (pos - neg) / 2;
                a.audited++;
                a.worstCentre = Math.max(a.worstCentre, Math.abs(centre));
                if ((Math.abs(centre) > CV_CENTRE_PX || pos + neg < want - CV_NARROW_PX || pos + neg > want + px) && a.firstBad.isEmpty()) {
                    a.firstBad = String.format("middle %.2f px off at (%.2f, %.2f), width %.2f of %d, plot code %02x", centre, wx, wy, pos + neg, want, code);
                }
                if (Math.abs(centre) > CV_CENTRE_PX) a.off++;
                if (pos + neg < want - CV_NARROW_PX || pos + neg > want + px) a.narrow++;
            }
            if (index % step == 0) kept.add(new double[] { wx, wy, Double.isNaN(centre) ? Double.NaN : wx - hy * centre / px, Double.isNaN(centre) ? Double.NaN : wy + hx * centre / px });
        }
        // The turn at each kept sample, from the middles measured, against the line's own.
        double err = Math.asin(Math.min(1, 2 * (CV_CENTRE_PX / px) / CV_TURN_SPAN));
        for (int j = 1; j + 1 < kept.size(); j++) {
            double[] p0 = kept.get(j - 1), p1 = kept.get(j), p2 = kept.get(j + 1);
            if (Double.isNaN(p0[2]) || Double.isNaN(p1[2]) || Double.isNaN(p2[2])) continue;
            double ideal = Math.abs(cvTurn(p0[0], p0[1], p1[0], p1[1], p2[0], p2[1]));
            double drawn = Math.abs(cvTurn(p0[2], p0[3], p1[2], p1[3], p2[2], p2[3]));
            a.turns++;
            a.worstIdeal = Math.max(a.worstIdeal, ideal);
            a.worstExcess = Math.max(a.worstExcess, drawn - ideal);
            if (drawn - ideal > 2 * err + 1e-9) a.turnsBad++;
        }
        return a;
    }

    /** The signed turn, in radians, from the chord (x0, y0)-(x1, y1) to (x1, y1)-(x2, y2). */
    static double cvTurn(double x0, double y0, double x1, double y1, double x2, double y2) {
        double ux = x1 - x0, uy = y1 - y0, vx = x2 - x1, vy = y2 - y1;
        return Math.atan2(ux * vy - uy * vx, ux * vx + uy * vy);
    }

    /** Whether the line passes from one tile to another. */
    static boolean cvCrossesTile(double[][] line) {
        for (int k = 1; k < line[0].length; k++) {
            if (Math.floorDiv((long) Math.floor(line[0][k]), World.TILE) != Math.floorDiv((long) Math.floor(line[0][k - 1]), World.TILE)
                    || Math.floorDiv((long) Math.floor(line[1][k]), World.TILE) != Math.floorDiv((long) Math.floor(line[1][k - 1]), World.TILE)) return true;
        }
        return false;
    }

    /** The tiles about a curve's plots: its bounding box in tiles, a tile more on each side, as {tx, ty} pairs. */
    static java.util.List<long[]> cvTiles(CvFound f) {
        long x0 = f.ox + f.c().x0, y0 = f.oy + f.c().y0, x1 = f.ox + f.c().x1, y1 = f.oy + f.c().y1;
        java.util.List<long[]> out = new java.util.ArrayList<>();
        for (long ty = Math.floorDiv(y0, World.TILE) - 1; ty <= Math.floorDiv(y1, World.TILE) + 1; ty++) {
            for (long tx = Math.floorDiv(x0, World.TILE) - 1; tx <= Math.floorDiv(x1, World.TILE) + 1; tx++) out.add(new long[] { tx, ty });
        }
        return out;
    }

    /** A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a crescent in the cell whose interior's north-west plot is (0, 1), bulging toward +x, its staircase laid as street plots with its curve, and the streets `extra` besides. */
    static int[] handCrescent(JunctionCheck.Streets extra) {
        DistrictPlan.ArcShape arc = DistrictPlan.arc(1);
        int x0 = 0, y0 = 1;
        java.util.Set<Long> stairs = new java.util.HashSet<>();
        for (int k = 0; k < arc.stairU.length; k++) if (arc.stairV[k] >= 0 && arc.stairV[k] < DistrictPlan.INTERIOR) stairs.add((long) (y0 + arc.stairV[k]) * 1000 + x0 + arc.stairU[k]);
        JunctionCheck.Streets f = (x, y) -> stairs.contains((long) y * 1000 + x) ? JunctionCheck.STREET : extra == null ? 0 : extra.at(x, y);
        TilePainter.Input in = JunctionCheck.inputFor(0, 0, f);
        // The circle in the frame (DistrictPlan's recordCrescent): the plan's cell is across (u along x).
        double cx = x0 + arc.cu, cy = y0 + arc.cv;
        double p0x = x0 + arc.a + 0.5, p0y = y0 - 0.5, p1x = x0 + arc.a + 0.5, p1y = y0 + DistrictPlan.INTERIOR + 0.5;
        double a0 = StrictMath.atan2(p0y - cy, p0x - cx), cross = (p0x - cx) * (p1y - cy) - (p0y - cy) * (p1x - cx);
        double sweep = cross > 0 ? StrictMath.PI / 2 : -StrictMath.PI / 2;
        DistrictPlan.Curve cv = new DistrictPlan.Curve(DistrictPlan.Curve.CRESCENT, new double[] { cx, cy, DistrictPlan.ARC_RADIUS, a0, sweep }, DistrictPlan.ARC_RADIUS, null, null, x0, y0, true, (byte) 1);
        in.addCurve(cv.kind, cv.radius, cv.geom, cv.geom.length);
        int[] own = cv.plots(), band = cv.band();
        for (int k = 0; k < own.length; k++) in.setCurvePlot(own[k] % DistrictPlan.FRAME, own[k] / DistrictPlan.FRAME, TilePainter.Input.CV_OWN | (k == 0 || k == own.length - 1 ? TilePainter.Input.CV_END : 0));
        for (int q : band) in.setCurvePlot(q % DistrictPlan.FRAME, q / DistrictPlan.FRAME, TilePainter.Input.CV_BAND);
        return JunctionCheck.raster(in, JunctionCheck.paint(in), 8);
    }

    /** A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a street along a cut, a path of street plots along row 10 from column 2 to 20 with its curve (StreetCurves' line, its band beside it) - with `bridge`, the plots 9 to 12 over fresh water as a bridge. */
    static int[] handShore(boolean bridge) {
        int y = 10, x0 = 2, x1 = 20;
        int[] path = new int[x1 - x0 + 1];
        for (int x = x0; x <= x1; x++) path[x - x0] = y * DistrictPlan.FRAME + x;
        JunctionCheck.Streets f = (x, yy) -> yy == y && x >= x0 && x <= x1 ? (bridge && x >= 9 && x <= 12 ? JunctionCheck.STREET | TilePainter.S_BRIDGE : JunctionCheck.STREET) : 0;
        TilePainter.Input in = JunctionCheck.inputFor(0, 0, f);
        if (bridge) for (int x = 9; x <= 12; x++) in.terrain[y * World.TILE + x] = World.FRESH;
        double[][] fit = StreetCurves.fit(path, path.length, DistrictPlan.FRAME);
        double[][] line = StreetCurves.line(fit[0], fit[1]);
        int[] reached = StreetCurves.reached(line[0], line[1], DistrictPlan.FRAME);
        java.util.List<Integer> band = new java.util.ArrayList<>();
        for (int q : reached) if (Arrays.binarySearch(path, q) < 0) band.add(q);
        double[] geom = new double[2 * fit[0].length];
        for (int k = 0; k < fit[0].length; k++) { geom[2 * k] = fit[0][k]; geom[2 * k + 1] = fit[1][k]; }
        in.addCurve(DistrictPlan.Curve.SHORE, StreetCurves.CURVE_RADIUS, geom, geom.length);
        for (int k = 0; k < path.length; k++) in.setCurvePlot(path[k] % DistrictPlan.FRAME, path[k] / DistrictPlan.FRAME, TilePainter.Input.CV_OWN | (k == 0 || k == path.length - 1 ? TilePainter.Input.CV_END : 0));
        for (int q : band) in.setCurvePlot(q % DistrictPlan.FRAME, q / DistrictPlan.FRAME, TilePainter.Input.CV_BAND);
        return JunctionCheck.raster(in, JunctionCheck.paint(in), 8);
    }

    static void curvesDrawn(Squares sq) {
        out.println("\n--- 17. the curves drawn (CL4): a crescent and a street along a cut as smooth lines - every sample the street's, no staircase step,"
                + " the same pixels from the same inputs, no building under or over them, no junction of their own - and the screen within 80 ms ---");
        CityMap m = sq.maps[DENSE];
        int tile = World.TILE;
        long sdx = Math.floorDiv(m.land().siteX(), CityMap.DISTRICT), sdy = Math.floorDiv(m.land().siteY(), CityMap.DISTRICT);
        // ---- THE FIXTURE: the curves of the districts CV_FROM to CV_TO east and south of the founding site's
        java.util.List<CvFound> crescents = new java.util.ArrayList<>(), shores = new java.util.ArrayList<>();
        long drawnBytes = 0, curveBytes = 0;
        int withCurves = 0, districts = 0;
        for (CityMap.District d : m.districts()) {
            long ddx = m.baseDX() + d.dx - sdx, ddy = m.baseDY() + d.dy - sdy;
            if (ddx < CV_FROM || ddx > CV_TO || ddy < CV_FROM || ddy > CV_TO) continue;
            CityMap.Drawn dr = m.drawn(d);
            districts++;
            drawnBytes += dr.bytes();
            if (dr.curves == null) continue;
            withCurves++;
            long ox = (m.baseDX() + d.dx) * (long) CityMap.DISTRICT, oy = (m.baseDY() + d.dy) * (long) CityMap.DISTRICT;
            for (CityMap.DrawnCurve c : dr.curves) {
                curveBytes += c.bytes();
                (c.kind == DistrictPlan.Curve.CRESCENT ? crescents : shores).add(new CvFound(d, c, ox, oy));
            }
        }
        // The shore curves that cross a tile's edge, and those that bend, first.
        shores.sort(java.util.Comparator.comparingInt((CvFound f) -> (cvCrossesTile(f.line()) ? 0 : 2) + (f.c().geom.length / 2 >= 3 ? 0 : 1)));
        int crossing = 0, bending = 0;
        for (CvFound f : shores) { if (cvCrossesTile(f.line())) crossing++; if (f.c().geom.length / 2 >= 3) bending++; }
        out.printf("      the districts %d to %d east and %d to %d south of the founding site's: %d, %d with curves - %d crescents, %d shore curves (%d cross a tile's edge, %d bend); their Drawn bytes %,d, the curves' %,d (%.1f%%)%n",
                CV_FROM, CV_TO, CV_FROM, CV_TO, districts, withCurves, crescents.size(), shores.size(), crossing, bending, drawnBytes, curveBytes, 100.0 * curveBytes / Math.max(1, drawnBytes));
        check("fixture: the far districts hold crescents and shore curves, shore curves that cross a tile's edge and some that bend, and their plans are drawn (a Drawn counts them)",
                crescents.size() >= CV_CRESCENTS && shores.size() >= CV_SHORES && crossing > 0 && bending > 0 && curveBytes > 0);
        check("...the curves' bytes are a small share of the plans' (compact: the geometry as numbers, the plots as one list of ints): at most 15%", curveBytes * 100 <= drawnBytes * 15);
        java.util.Set<Integer> fills = new java.util.HashSet<>();
        for (BuildingVisual.Type ty : sq.types) if (ty != null) { fills.add(ty.fill()); fills.add(ty.edge()); }

        // ---- THE LINE: at 4 px a plot (the near view's least) and 8 (its most)
        for (int px : new int[] { 4, 8 }) {
            CvTiles t = new CvTiles(m, px);
            int[][] total = new int[2][9];
            double[][] worst = new double[2][3];
            int[] followed = new int[2];
            String[] firstBad = { "", "" };
            for (int kind = 0; kind < 2; kind++) {
                java.util.List<CvFound> list = kind == 0 ? crescents : shores;
                int want = kind == 0 ? CV_CRESCENTS : CV_SHORES;
                for (int n = 0; n < list.size() && followed[kind] < want; n++) {
                    CvFound f = list.get(n);
                    // A street the city has not bought a road for is a track, dashes and no band: left to the dashes' own rule.
                    boolean track = false;
                    for (int k = 1; k + 1 < f.c().own; k++) { long[] q = f.plot(k); if (TilePainter.kindOf(t.code(q[0], q[1])) == TilePainter.TRACK) track = true; }
                    if (track) continue;
                    CvAudit a = cvAudit(t, f, fills);
                    followed[kind]++;
                    int[] tot = total[kind];
                    tot[0] += a.samples; tot[1] += a.badColour; tot[2] += a.onBuilding; tot[3] += a.edges; tot[4] += a.audited; tot[5] += a.narrow; tot[6] += a.off; tot[7] += a.turns; tot[8] += a.turnsBad;
                    worst[kind][0] = Math.max(worst[kind][0], a.worstCentre);
                    worst[kind][1] = Math.max(worst[kind][1], a.worstExcess);
                    worst[kind][2] = Math.max(worst[kind][2], a.worstIdeal);
                    if (firstBad[kind].isEmpty()) firstBad[kind] = a.firstBad;
                }
                int[] tot = total[kind];
                String what = kind == 0 ? "crescents" : "shore curves";
                out.printf("      at %d px a plot, %d %s followed end to end: %,d samples (%d across a tile's edge), %,d of them measured across the band (worst middle %.2f px from the line), %d turns audited (the line's sharpest %.3f rad, the band's worst excess %.3f)%n",
                        px, followed[kind], what, tot[0], tot[3], tot[4], worst[kind][0], tot[7], worst[kind][2], worst[kind][1]);
                if (!firstBad[kind].isEmpty()) out.println("      the first sample off: " + firstBad[kind]);
                check("THE LINE at " + px + " px a plot: along " + what + " from end to end every sample is the street's colour, and no sample a building's - across the tile edges, no gap",
                        followed[kind] >= (kind == 0 ? CV_CRESCENTS / 2 : CV_SHORES / 2) && tot[0] > 0 && tot[1] == 0 && tot[2] == 0 && (kind == 0 || tot[3] > 0));
                check("...the band's middle stays within " + CV_CENTRE_PX + " px of the line and the band is as wide as its street (less " + CV_NARROW_PX + " px, no more than a plot over), measured across it on at least " + (int) (100 * CV_MEASURED[kind]) + "% of the samples (the rest are by another street, at a corner or where the street changes): no staircase step",
                        tot[4] >= CV_MEASURED[kind] * tot[0] && tot[5] == 0 && tot[6] == 0);
                check("...and between samples " + CV_TURN_SPAN + " plots apart the band turns no sharper than the line does (a crescent's line no sharper than " + CV_TURN_SPAN + " / ARC_RADIUS), within what two measured middles allow",
                        tot[7] > 0 && tot[8] == 0 && (kind == 1 || worst[kind][2] <= CV_TURN_SPAN / DistrictPlan.ARC_RADIUS + 1e-3));
            }
        }

        // ---- THE CONTROL: the same lines audited on tiles drawn as before CL4 (no curve about them, the staircases' bars) - the audit can tell
        CvTiles bare = new CvTiles(m, 8, true);
        int bareSamples = 0, bareOff = 0, bareNarrow = 0, bareBad = 0, bareFollowed = 0;
        for (int n = 0; n < crescents.size() && bareFollowed < CV_CRESCENTS; n++) {
            CvFound f = crescents.get(n);
            CvAudit a = cvAudit(bare, f, fills);
            bareFollowed++;
            bareSamples += a.audited;
            bareOff += a.off;
            bareNarrow += a.narrow;
            bareBad += a.badColour;
        }
        out.printf("      the %d crescents again at 8 px a plot on tiles drawn without the curves (the staircase's bars): %,d samples measured across, %d off the line by more than %.1f px, %d narrower or wider than the street, %d not the street's colour%n",
                bareFollowed, bareSamples, bareOff, CV_CENTRE_PX, bareNarrow, bareBad);
        check("THE CONTROL: the same crescents drawn without the curves, as the staircase of bars they were, fail the audit - the band's middle off the line, its width wrong or a sample not the street's colour - so the audit can tell the smooth line from the staircase",
                bareFollowed > 0 && bareOff + bareNarrow + bareBad > 0);

        // ---- THE SAME PIXELS: a tile twice, from the kept copy the view paints from, and from a map built alike
        TilePainter.Input in = new TilePainter.Input(), kept = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted(), pk = new TilePainter.Painted();
        int[] img = new int[(tile * PX) * (tile * PX)], again = img.clone(), copy = img.clone();
        java.util.List<long[]> tiles = new java.util.ArrayList<>();
        for (CvFound f : java.util.List.of(crescents.get(0), crescents.get(1), shores.get(0), shores.get(1), shores.get(2))) {
            for (long[] q : cvTiles(f)) {
                boolean have = false;
                for (long[] o : tiles) if (o[0] == q[0] && o[1] == q[1]) have = true;
                if (!have) tiles.add(q);
            }
        }
        int withCurve = 0, twice = 0, copies = 0, stamps = 0, moved = 0;
        for (long[] q : tiles) {
            m.tileInput(q[0], q[1], in);
            if (in.curveMarks == 0) continue;
            withCurve++;
            TilePainter.paint(in, p);
            TileRaster.raster(in, p, PX, img);
            TileRaster.raster(in, p, PX, again);
            if (Arrays.equals(img, again)) twice++;
            MapTiles.copy(in, kept);
            // (MapTiles.copy leaves the interchanges' reserves out, as it always has: handed over here, so the stamp and the pixels are the curves' alone.)
            kept.reserves = in.reserves;
            kept.reserve = in.reserve.clone();
            TilePainter.paint(kept, pk);
            TileRaster.raster(kept, pk, PX, copy);
            if (Arrays.equals(img, copy)) copies++;
            if (MapTiles.stamp(in) == MapTiles.stamp(kept)) stamps++;
            // ...and a tile with a curve's mark added beside it repaints: its stamp moves with the marks.
            int free = 0;
            while (free < TilePainter.RING * TilePainter.RING && kept.curveRing[free] != 0) free++;
            if (free < TilePainter.RING * TilePainter.RING) {
                kept.setCurvePlot(free % TilePainter.RING - TilePainter.REACH, free / TilePainter.RING - TilePainter.REACH, TilePainter.Input.CV_BAND);
                if (MapTiles.stamp(in) != MapTiles.stamp(kept)) moved++;
            }
        }
        check("fixture: the tiles about the first crescents and shore curves have curves about them", withCurve >= 9);
        check("THE SAME PIXELS: each such tile rastered twice paints the same; its kept copy (MapTiles.copy) the same, with the same stamp", twice == withCurve && copies == withCurve && stamps == withCurve);
        check("...and a copy with a curve mark added beside the tile has another stamp: a tile repaints when a curve plot beside it is laid", moved == withCurve);
        long[] noMines = m.totals();
        for (int ty = 0; ty < noMines.length; ty++) if (m.types()[ty] != null && m.types()[ty].site() != null) noMines[ty] = 0;
        CityMap twin = CityMap.square(m.seed(), m.land().siteX(), m.land().siteY(), m.types(), (int) Math.round(Math.sqrt(m.districts().size())), m.districts().get(0).owned(), noMines);
        twin.reconcile(m.totals());
        TilePainter.Input in2 = new TilePainter.Input();
        TilePainter.Painted p2 = new TilePainter.Painted();
        int[] other = img.clone();
        int alike = 0, compared = 0;
        for (long[] q : tiles) {
            m.tileInput(q[0], q[1], in);
            if (in.curveMarks == 0) continue;
            TilePainter.paint(in, p);
            TileRaster.raster(in, p, PX, img);
            twin.tileInput(q[0], q[1], in2);
            TilePainter.paint(in2, p2);
            TileRaster.raster(in2, p2, PX, other);
            compared++;
            if (Arrays.equals(img, other)) alike++;
        }
        out.printf("      the dense city and its twin built the same way: %d tiles with a curve about them compared%n", compared);
        check("...and a tile of a curve from a twin map built alike paints the same pixels", compared >= 9 && alike == compared);

        // ---- NO BUILDING UNDER OR OVER: not on a plot a curve is laid on or reaches, and not a pixel of a building's changed by the curve
        CvTiles t8 = new CvTiles(m, 8);
        int plots = 0, builtOn = 0, onWorks = 0, overBuilding = 0, buildingPixels = 0, tilesCompared = 0;
        java.util.List<CvFound> all = new java.util.ArrayList<>(crescents);
        all.addAll(shores);
        for (CvFound f : all) {
            for (int k = 0; k < f.c().plots.length; k++) {
                long[] q = f.plot(k);
                plots++;
                if (t8.use(q[0], q[1]) != TilePainter.BUILDING) continue;
                // A works at the water (CityShore) and a mine on its site stand where the map deals them, after the plan, which cannot keep their ground free: counted apart.
                boolean apart = false;
                for (CityShore.Work wk : m.shore().works()) if (q[0] >= wk.x0() && q[0] < wk.x0() + wk.w() && q[1] >= wk.y0() && q[1] < wk.y0() + wk.h()) apart = true;
                int type = t8.type(q[0], q[1]);
                if (type >= 0 && type < m.types().length && m.types()[type] != null && m.types()[type].site() != null) apart = true;
                if (apart) onWorks++; else builtOn++;
            }
        }
        java.util.List<long[]> seenTiles = new java.util.ArrayList<>();
        int[] with = new int[(tile * 8) * (tile * 8)], without = with.clone();
        for (CvFound f : all.subList(0, Math.min(all.size(), 24))) {
            for (long[] q : cvTiles(f)) {
                boolean have = false;
                for (long[] o : seenTiles) if (o[0] == q[0] && o[1] == q[1]) have = true;
                if (have) continue;
                seenTiles.add(q);
                m.tileInput(q[0], q[1], in);
                if (in.curveMarks == 0) continue;
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, 8, with);
                in.clearCurves();
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, 8, without);
                tilesCompared++;
                // The buildings' pixels - their whole plots, the inset border among them - are the same with the curve and without it.
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    if (p.use[i] != TilePainter.BUILDING) continue;
                    for (int y = 0; y < 8; y++) {
                        for (int x = 0; x < 8; x++) {
                            int o = ((i / tile) * 8 + y) * (tile * 8) + (i % tile) * 8 + x;
                            buildingPixels++;
                            if (with[o] != without[o]) overBuilding++;
                        }
                    }
                }
            }
        }
        out.printf("      the curves' plots on the dense city: %,d plots (own and band) of %d curves, %d of them under a building of the plan and %d under a works at the water or a mine on its site (the map's deals, placed after the plan); %,d pixels of buildings on %d tiles with and without the curves, %d changed%n",
                plots, all.size(), builtOn, onWorks, buildingPixels, tilesCompared, overBuilding);
        check("NO BUILDING UNDER OR OVER A CURVE: not one plot a curve is laid on or reaches is under a building of the plan (a works at the water, which CityShore places on the shore after the plan, or a mine on its site may stand on a plot of a band: the drawing leaves its pixels alone), and the pixels of the buildings on the tiles about them (their insets too) are the same with the curves drawn and without",
                plots > 0 && builtOn == 0 && tilesCompared >= 9 && buildingPixels > 0 && overBuilding == 0);

        // ---- THE CROSSINGS: hand-built tiles at 8 px a plot, each causing its case
        int px = 8, w = World.TILE * px, paved = TileRaster.ROAD[BuildingVisual.PAVED];
        JunctionCheck.Streets rings = (x, y) -> y == 0 || y == 32 ? JunctionCheck.ARTERIAL : 0;
        JunctionCheck.Streets longs = (x, y) -> y == 0 || y == 32 ? JunctionCheck.ARTERIAL : x >= 0 && x <= 30 && (y == 8 || y == 16 || y == 24) ? JunctionCheck.STREET : 0;
        int[] alone = handCrescent(rings);
        boolean none = JunctionCheck.noneOf(alone, TileRaster.ISLAND, TileRaster.ZEBRA, TileRaster.JUNCTION_BOX);
        DistrictPlan.ArcShape arc = DistrictPlan.arc(1);
        int apexRow = DistrictPlan.INTERIOR / 2, apexU = arc.stairU[0];
        for (int k = 0; k < arc.stairU.length; k++) if (arc.stairV[k] == apexRow) apexU = Math.max(apexU, arc.stairU[k]);
        check("CROSSINGS: a crescent's plots alone are no junction - between its two rings it draws no box, no zebra, no ring (and is drawn: its paved band is on its apex plot)",
                none && JunctionCheck.hasColour(alone, w, apexU, 1 + apexRow, px, paved));
        int[] crossed = handCrescent(longs);
        // The long street at row 16 crosses the arc: over the plots the staircase holds in its row and three each side, the street's colour at its middle line at every plot's two edges.
        int rowU0 = Integer.MAX_VALUE, rowU1 = Integer.MIN_VALUE;
        for (int k = 0; k < arc.stairU.length; k++) if (arc.stairV[k] == 15) { rowU0 = Math.min(rowU0, arc.stairU[k]); rowU1 = Math.max(rowU1, arc.stairU[k]); }
        int streetAt = (px - Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px))) / 2;
        boolean across = true;
        for (int u = Math.max(0, rowU0 - 3); u <= rowU1 + 3; u++) {
            int y = 16 * px + streetAt + 1;
            across &= cvStreetColour(crossed[y * w + u * px]) && cvStreetColour(crossed[y * w + u * px + px - 1]);
        }
        check("...a street that crosses it runs on across the band: along the long street's row, over the plots the arc crosses and three each side, its colour at the middle of the street at every plot's edge, and no box, no zebra, no ring",
                across && JunctionCheck.noneOf(crossed, TileRaster.ISLAND, TileRaster.ZEBRA, TileRaster.JUNCTION_BOX));
        // A crossing beside it is the signal it was: two arterials crossing away from the arc, the crescent in the tile, the box only in their plot.
        JunctionCheck.Streets twoArterials = (x, y) -> (x == 3 && y >= 0 && y <= 32) || (y == 24 && x >= 0 && x <= 8) ? JunctionCheck.ARTERIAL : 0;
        int[] signal = handCrescent(twoArterials);
        boolean boxOnlyThere = true;
        for (int y = 0; y < World.TILE; y++) for (int x = 0; x < World.TILE; x++)
            if ((x < 2 || x > 4 || y < 23 || y > 25) && JunctionCheck.hasColour(signal, w, x, y, px, TileRaster.JUNCTION_BOX)) boxOnlyThere = false;
        check("...and an arterial crossing another with the crescent in the tile is a signal as it was: the box on the crossing's plot, nowhere else, no ring",
                JunctionCheck.hasColour(signal, w, 3, 24, px, TileRaster.JUNCTION_BOX) && boxOnlyThere && JunctionCheck.noneOf(signal, TileRaster.ISLAND));
        // A street along a cut over a bridge: the plots that bridge fresh water keep their staircase (the deck and the bars of FX5's decks), the band on the plots beside them stopping at their edges.
        int[] straight = handShore(false), bridged = handShore(true);
        boolean runs = true, decked = true, dry = true;
        int rowPx = 10 * px + (px - Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px))) / 2 + 1;
        for (int X = 2 * px + px / 2; X < 20 * px + px / 2; X++) runs &= cvStreetColour(bridged[rowPx * w + X]) && cvStreetColour(straight[rowPx * w + X]);
        for (int x = 9; x <= 12; x++) decked &= JunctionCheck.hasColour(bridged, w, x, 10, px, TileRaster.DECK);
        for (int x = 3; x <= 19; x++) if (x < 9 || x > 12) dry &= !JunctionCheck.hasColour(bridged, w, x, 10, px, TileRaster.DECK);
        check("...and over a bridge a street along a cut is drawn through: its colour along the whole line from its first plot's middle to its last's, the deck on the four plots that bridge and on no other, and the same line without the bridge",
                runs && decked && dry);

        // ---- THE SCREEN: over the far districts where the most tiles have a curve about them, within SCREEN_MS
        TilePainter.Input sin = new TilePainter.Input();
        int perDistrict = CityMap.DISTRICT / tile;
        long tx0 = Math.floorDiv(m.land().siteX(), (long) tile) + CV_FROM * perDistrict, ty0 = Math.floorDiv(m.land().siteY(), (long) tile) + CV_FROM * perDistrict;
        int mostTiles = -1;
        long bestX = tx0, bestY = ty0;
        for (long sy = Math.floorDiv(m.land().siteY(), (long) tile) + CV_FROM * perDistrict; sy + SCREEN_DOWN <= Math.floorDiv(m.land().siteY(), (long) tile) + (CV_TO + 1) * perDistrict; sy += 3) {
            for (long sx = Math.floorDiv(m.land().siteX(), (long) tile) + CV_FROM * perDistrict; sx + SCREEN_ACROSS <= Math.floorDiv(m.land().siteX(), (long) tile) + (CV_TO + 1) * perDistrict; sx += 3) {
                int count = 0;
                for (int j = 0; j < SCREEN_DOWN; j++) for (int c = 0; c < SCREEN_ACROSS; c++) { m.tileInput(sx + c, sy + j, sin); if (sin.curveMarks != 0) count++; }
                if (count > mostTiles) { mostTiles = count; bestX = sx; bestY = sy; }
            }
        }
        tx0 = bestX;
        ty0 = bestY;
        TilePainter.Painted sp = new TilePainter.Painted();
        int[] simg = new int[(tile * PX) * (tile * PX)];
        int curveTiles = 0;
        double bestWith = Double.MAX_VALUE, bestWithout = Double.MAX_VALUE;
        System.gc();
        for (int round = 0; round < WARM_ROUNDS + TIMED_ROUNDS; round++) {
            for (int variant = 0; variant < 2; variant++) {
                int n = 0;
                long t0 = System.nanoTime();
                for (int j = 0; j < SCREEN_DOWN; j++) {
                    for (int c = 0; c < SCREEN_ACROSS; c++) {
                        m.tileInput(tx0 + c, ty0 + j, sin);
                        if (sin.curveMarks != 0) n++;
                        // The second variant is the same screen with its curves' marks cleared, as it was drawn before CL4: the curves' share of the time.
                        if (variant == 1) sin.clearCurves();
                        TilePainter.paint(sin, sp);
                        TileRaster.raster(sin, sp, PX, simg);
                    }
                }
                double ms = (System.nanoTime() - t0) / 1e6;
                if (variant == 0) curveTiles = n;
                if (round >= WARM_ROUNDS) { if (variant == 0) bestWith = Math.min(bestWith, ms); else bestWithout = Math.min(bestWithout, ms); }
            }
        }
        out.printf("      x %,.0f: the screen's %d tiles over the far districts where curves are thickest, %d with a curve about them - terrain, the plans' tiles, paint and raster at %d px - in %.1f ms (%.3f ms a tile); the same screen without the curves' marks %.1f ms%n",
                TIMES[DENSE], SCREEN_TILES, curveTiles, PX, bestWith, bestWith / SCREEN_TILES, bestWithout);
        check("THE SCREEN: the dense screen over the far districts where curves are thickest, curves across it (tiles with curves about them: " + curveTiles + "), paints and rasters in no more than " + (int) SCREEN_MS + " ms",
                curveTiles > 0 && Timing.within(bestWith, SCREEN_MS));
    }

    /* =====================================================================
       18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md)
       ===================================================================== */

    /*
     * WHY (batch UP3, 2026-10-23; Jerus, 2026-10-22: "the map doesn't
     * regenerate every single building"). The model does not: reconcile()
     * places each type's change (section 5 holds it to RECONCILE_MS). What
     * was redone whole each month was the screens: the minimap's stamp (every
     * district twice, every highway and rail stretch struck again), every far
     * node in view, and stampOf() for each tile each frame. UP3 keeps what a
     * month leaves alone - MapTiles.KeptNode under the stamp, MapTiles
     * .nodeKey() under the far view, CityMap.Link.stamp() under the chain -
     * and this section holds it to what matters: the picture is the one a
     * full redraw gives, only what moved is redrawn, and a month's FX-side
     * work at 5B is half what it was measured before UP3.
     *
     * A month is played on a fixture as the game plays one: reconcile(), the
     * screens drawn as ui/MiniMap and ui/MapView draw them, the map's
     * worker's jobs run and adopted, the screens drawn again where the
     * pictures that came change them (up to CHAIN_ROUNDS), then an L0 screen
     * re-stamped. The screens are drawn two ways, today's whole on one
     * MapTiles and now's kept on another, and held equal every time. The FX
     * thread's work is timed on monthClock() and summed over the stamp, the
     * far view's screen, the L0 screen and the chain's handing out and
     * adopting; the worker's run() is not it. The first draw after a month
     * pays the map's lazily kept checks (CityMap.checkBand()), which today's
     * way and now's both need, so a pass of today's is made first untimed
     * (printed as "the lazy checks") and the two timed passes find the same
     * state. The fixtures are grown here and left grown: this section is
     * last.
     */

    /** The copy at Jerus's own district count: x 80, 125 districts, 11 a side (CL3's notes cite 115 for his cities), between x 1's 4 districts and 5B's 14,661. */
    static final double HIS_TIMES = 80;

    /** The months grown at x 1 and at x 80, and at 5B (its months are slower): 24 and 12. */
    static final int REDRAW_MONTHS = 24, REDRAW_MONTHS_5B = 12;

    /** The first months of each fixture are drawn, compared and not counted in its figures: 3, the JIT's compiling done before a month is read. */
    static final int REDRAW_WARM = 3;

    /** A month's growth as a share of each type's count: 1/1,000 (+0.1%) at x 1 and x 80, 1/10,000 (+0.01%) at 5B, as section 5's, at least one building each. */
    static final int GROW_PER_MILLE = 1000, GROW_PER_TEN_THOUSAND = 10_000;

    /** The runs' types (a highway's, a track's) are grown only in every 4th month, so most months lay no run, as in play. */
    static final int RUNS_EVERY = 4;

    /** The times a month's screens are drawn: the month's, then again after each round of the chain's jobs (handed out, run, adopted) until none is left, at most 4. */
    static final int CHAIN_ROUNDS = 4;

    /** The months the played city is grown before its land purchase and after it: 3 and 3. */
    static final int PLAYED_BEFORE = 3, PLAYED_AFTER = 3;

    /** The side of the block the played city buys, in plots: 32, a hole in its built-up ground's box (the played city's ground is irregular). */
    static final int PURCHASE_SIDE = 32;

    /** The most a month's FX-side map work may take at 5B, in ms, its median month - the stamp, the far view's screen, an L0 screen re-stamped and the chain's handing out and adopting: 58, nine tenths of the 65 this section measured on the PC the old way (today's paths, the median month, the mean of four runs, 63.4 to 67.2: the stamp 2.3, the far screen 29.6, the L0 screen 32.5, the chain 0.2; docs/queue/UP3-notes.md) - the stamp and the far view's unmoved nodes taken off it; the L0 screen's band check, half of what is left, is UP3-C's. */
    static final double MAP_MONTH_MS = 58;

    /** The kinds of month: the grown one, one built type raised by one, and the first, which only draws. */
    static final int GROW = 0, ONE = 1, SETTLE = 2;

    /** One far screen of section 18: a MapFrame fitted on the city's frame and zoomed out to FAR (or on to a level that draws no pictures), its nodes, their ground, and what each was last drawn from and as - the way ui/MapView keeps an Img. */
    static final class FarScreen {
        final String name;
        final MapFrame f = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        final long plots, span;
        final boolean pictures;
        final int count;
        final long[] nx, ny;
        final byte[][] ground;
        /** Now's: the key each node was drawn from, its pixels, whether it was drawn, and short of a picture. */
        final long[] key;
        final int[][] pix;
        final boolean[] drawn, partial;
        /** Today's: whether it would be drawn again after a picture came. */
        final boolean[] todayPartial;
        /** The nodes held (not drawn again) in the month under way. */
        final boolean[] held;
        final int[] buf = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];

        FarScreen(String name, CityMap m, MiniMapFrame.Box frame, boolean wide) {
            this.name = name;
            MiniMapFrame.open(f, frame);
            for (int i = 0; i < 400 && (f.level() != MapFrame.FAR || wide && World.DISTRICT / f.nodePlots() >= MapTiles.PICTURE_FROM_PX); i++) {
                double was = f.scale();
                f.set(f.centreX(), f.centreY(), was / MapFrame.ZOOM_STEP);
                if (f.scale() == was) break;
            }
            plots = f.nodePlots();
            span = f.nodeSpan();
            pictures = World.DISTRICT / plots >= MapTiles.PICTURE_FROM_PX;
            java.util.List<long[]> at = new ArrayList<>();
            for (long ny = f.nodeY0(); ny <= f.nodeY1(); ny++) for (long nx = f.nodeX0(); nx <= f.nodeX1(); nx++) at.add(new long[] { nx, ny });
            count = at.size();
            nx = new long[count];
            ny = new long[count];
            ground = new byte[count][];
            for (int i = 0; i < count; i++) {
                nx[i] = at.get(i)[0];
                ny[i] = at.get(i)[1];
                ground[i] = MapTiles.nodeTerrain(m.worldKey(), nx[i] * span, ny[i] * span, plots);
            }
            key = new long[count];
            pix = new int[count][MapFrame.NODE_PX * MapFrame.NODE_PX];
            drawn = new boolean[count];
            partial = new boolean[count];
            todayPartial = new boolean[count];
            held = new boolean[count];
        }

        /** Whether node i lies over any of the box x0 to x1 and y0 to y1 (in plots). */
        boolean overlaps(int i, long x0, long y0, long x1, long y1) {
            return nx[i] * span < x1 && (nx[i] + 1) * span > x0 && ny[i] * span < y1 && (ny[i] + 1) * span > y0;
        }
    }

    /** What one month did to one fixture's screens, as Redraw.month() found it; times in ms. */
    static final class RedrawMonth {
        int kind, number, moved, rounds, astray, kept, built = -1, stale;
        /** The built districts whose pictures were no longer current right after a one-type month (`stale` of them). */
        final java.util.List<CityMap.District> staleList = new ArrayList<>();
        boolean timed, reconciled, viewChanged, runsMoved, settled;
        CityMap.District onlyMoved;
        /** The stamp's kept nodes in the month: land layers drawn, runs layers drawn, districts repainted (all 0 when the view changed and they were made anew), and the districts they list. */
        long lands, runsDrawn, repainted, onStamp;
        double stampNow, stampToday, farNow, farToday, l0, chain, keys, lazy;
        /** Per far screen: its nodes drawn this month (the month's round and the rounds after), and the partial ones before it. */
        int[] recoloured, partialBefore;
        /** Per far screen: the nodes drawn in the month's own round because the key they were drawn from had moved. */
        final java.util.List<java.util.List<Integer>> keyMoved = new ArrayList<>();

        double nowSum() { return stampNow + farNow + l0 + chain; }
        double todaySum() { return stampToday + farToday + l0 + chain; }
    }

    /** A fixture of section 18: a map grown a month at a time, its screens drawn two ways (today's whole on `tr`, now's kept on `tk`) and held equal. */
    static final class Redraw {
        final String name;
        final CityMap m;
        final long[] c;
        final int divisor, mine;
        final long sites;
        final BuildingVisual.Type[] tys;
        final MapTiles tk, tr;
        final MapFrame l0 = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        final boolean withL0, checkLinks;
        final java.util.List<FarScreen> fars = new ArrayList<>();
        final java.util.List<RedrawMonth> all = new ArrayList<>();
        final TilePainter.Input in = new TilePainter.Input();
        MiniMapFrame.Box frame;
        MiniMapFrame.View stampV, previewV;
        byte[][] stampG, previewG;
        MapTiles.KeptNode[] stampK, previewK;
        int[] stampNow, stampToday, previewNow, previewToday;
        int viewChanges;
        /** What was compared: stamps and previews drawn two ways (their pixels counted), and far nodes whose key was unmoved against a fresh one (those held in the month among them); whether every one was equal. */
        int stampsDrawn, previewsDrawn, keyUnmoved, heldCompared;
        long pixelsCompared;
        boolean stampEqual = true, previewEqual = true, heldEqual = true, allReconciled = true;
        double tookS;
        /** Run before the next month's reconcile (a land purchase). */
        Runnable beforeReconcile;

        Redraw(String name, CityMap m, long[] counts, int divisor, boolean withFar, boolean wide, boolean withL0, boolean checkLinks) {
            this.name = name;
            this.m = m;
            this.c = counts.clone();
            this.divisor = divisor;
            this.tys = m.types();
            this.withL0 = withL0;
            this.checkLinks = checkLinks;
            int mn = -1;
            for (BuildingVisual.Type t : tys) if (t != null && t.id() == Mining.IRON_MINE) mn = t.id();
            this.mine = mn;
            this.sites = mn >= 0 ? mineSites(m, mn) : 0;
            tk = new MapTiles(m);
            tr = new MapTiles(m);
            frame = MiniMapFrame.frame(m);
            if (withFar) {
                fars.add(new FarScreen(name + " far", m, frame, false));
                if (wide) fars.add(new FarScreen(name + " wide", m, frame, true));
            }
            l0.set(frame.centreX(), frame.centreY(), 3.3);
        }

        boolean runType(int t) {
            return t < tys.length && tys[t] != null && (tys[t].road() == BuildingVisual.HIGHWAY || tys[t].track());
        }

        /** The built type with the most buildings that is no run's and no mine: the one a single-district month raises. */
        int biggest() {
            int one = -1;
            for (int t = 0; t < c.length; t++) if (c[t] > 0 && !runType(t) && t != mine && (one < 0 || c[t] > c[one])) one = t;
            return one;
        }

        static long sum(MapTiles.KeptNode[] keeps, int what) {
            long s = 0;
            if (keeps != null) for (MapTiles.KeptNode k : keeps) s += what == 0 ? k.lands() : what == 1 ? k.runsDrawn() : what == 2 ? k.districtsRepainted() : k.districts();
            return s;
        }

        /** Plays one month and draws it; `timed` months are the ones whose figures are read. */
        RedrawMonth month(int kind, int number, boolean timed) {
            long began = System.nanoTime();
            RedrawMonth r = new RedrawMonth();
            r.kind = kind;
            r.number = number;
            r.timed = timed;
            if (kind == GROW) {
                for (int t = 0; t < c.length; t++) {
                    if (c[t] <= 0 || runType(t) && number % RUNS_EVERY != RUNS_EVERY - 1) continue;
                    c[t] += Math.max(1, c[t] / divisor);
                }
                if (mine >= 0) c[mine] = Math.min(c[mine], sites);
            } else if (kind == ONE) {
                c[biggest()]++;
            }
            java.util.Map<CityMap.District, Long> before = new HashMap<>();
            for (CityMap.District d : m.districts()) before.put(d, d.version);
            long runsBefore = m.runs().version();
            if (beforeReconcile != null) {
                beforeReconcile.run();
                beforeReconcile = null;
            }
            r.reconciled = m.reconcile(c);
            allReconciled &= r.reconciled;
            for (CityMap.District d : m.districts()) {
                Long v0 = before.get(d);
                if (v0 == null || v0 != d.version) {
                    r.moved++;
                    r.onlyMoved = d;
                }
            }
            r.runsMoved = m.runs().version() != runsBefore;
            // Where the pictures stand right after a month that raised one type (a city of a few districts, as a far view asks for them): how many are no longer current.
            if (kind == ONE && m.districts().size() <= CityMap.PICTURES_KEPT) {
                r.built = 0;
                for (CityMap.District d : m.districts()) {
                    if (d.usedCells() <= 0) continue;
                    r.built++;
                    if (m.pictureIfCurrent(d) == null) {
                        r.stale++;
                        r.staleList.add(d);
                    }
                }
            }
            r.recoloured = new int[fars.size()];
            r.partialBefore = new int[fars.size()];
            for (int s = 0; s < fars.size(); s++) {
                FarScreen fs = fars.get(s);
                r.keyMoved.add(new ArrayList<>());
                Arrays.fill(fs.held, false);
                for (int i = 0; i < fs.count; i++) if (fs.partial[i]) r.partialBefore[s]++;
            }
            MapTiles.KeptNode[] k0 = stampK;
            long lands0 = sum(k0, 0), runs0 = sum(k0, 1), rep0 = sum(k0, 2);
            long stampNowNs = 0, stampTodayNs = 0, farNowNs = 0, farTodayNs = 0, chainNs = 0;
            for (int round = 0; round < CHAIN_ROUNDS; round++) {
                java.util.Set<CityMap.District> wanted = new LinkedHashSet<>();
                long[] t = drawStamp(r, round, wanted);
                stampNowNs += t[0];
                stampTodayNs += t[1];
                for (int s = 0; s < fars.size(); s++) {
                    long[] f = drawFar(fars.get(s), s, r, round, wanted);
                    if (s == 0) {
                        farNowNs += f[0];
                        farTodayNs += f[1];
                    }
                }
                r.rounds = round + 1;
                if (round == CHAIN_ROUNDS - 1) break;
                // The chain: the L0 screen's tiles whose plans are not drawn, and the pictures the screens lack (when they fit), handed out;
                // run - the worker's, not timed; adopted.
                java.util.List<CityMap.Job> jobs = new ArrayList<>();
                long a0 = monthClock();
                if (withL0) {
                    for (long ty = l0.tileY0(); ty <= l0.tileY1(); ty++) for (long tx = l0.tileX0(); tx <= l0.tileX1(); tx++) if (!tk.ready(tx, ty)) jobs.addAll(tk.jobs(tx, ty));
                }
                boolean fit = tk.picturesFit(stampV.asMapFrame());
                for (FarScreen fs : fars) if (fs.pictures) fit &= tk.picturesFit(fs.f);
                if (fit) for (CityMap.District d : wanted) jobs.addAll(m.pictureJobs(d));
                chainNs += monthClock() - a0;
                if (jobs.isEmpty()) {
                    r.settled = true;
                    break;
                }
                for (CityMap.Job j : jobs) j.run();
                long b0 = monthClock();
                for (CityMap.Job j : jobs) m.adopt(j);
                chainNs += monthClock() - b0;
            }
            long s0 = monthClock();
            if (withL0) {
                for (long ty = l0.tileY0(); ty <= l0.tileY1(); ty++) for (long tx = l0.tileX0(); tx <= l0.tileX1(); tx++) if (tk.ready(tx, ty)) tk.input(tx, ty, in);
            }
            r.l0 = (monthClock() - s0) / 1e6;
            r.stampNow = stampNowNs / 1e6;
            r.stampToday = stampTodayNs / 1e6;
            r.farNow = farNowNs / 1e6;
            r.farToday = farTodayNs / 1e6;
            r.chain = chainNs / 1e6;
            r.viewChanged = stampK != k0;
            if (!r.viewChanged) {
                r.lands = sum(stampK, 0) - lands0;
                r.runsDrawn = sum(stampK, 1) - runs0;
                r.repainted = sum(stampK, 2) - rep0;
            }
            r.onStamp = sum(stampK, 3);
            for (FarScreen fs : fars) verify(fs);
            if (checkLinks) {
                r.astray = m.linksAstray();
                r.kept = m.linksKept();
            }
            all.add(r);
            tookS += (System.nanoTime() - began) / 1e9;
            return r;
        }

        /**
         * The stamp and the preview drawn two ways, equal or not noted: returns the ns now's took and today's. The first
         * pass after a month or a round of the chain pays the map's lazily kept checks (CityMap.checkBand(), the runs'
         * reach), which both ways need and neither changes: so a pass of today's is made first, untimed (r.lazy), and
         * the two timed passes find the same state - the figures are what UP3 changes, not who asked first.
         */
        long[] drawStamp(RedrawMonth r, int round, java.util.Set<CityMap.District> wanted) {
            long n0 = monthClock();
            MiniMapFrame.View v;
            if (round == 0) {
                frame = MiniMapFrame.frame(m);
                v = MiniMapFrame.stamp(frame);
            } else {
                v = stampV;
            }
            long n1 = monthClock();
            if (!v.equals(stampV)) {
                // Aimed at a new view, as ui/MiniMap does: its ground, new kept nodes.
                stampV = v;
                stampG = MiniMapFrame.ground(m.worldKey(), v);
                stampK = new MapTiles.KeptNode[v.nodesAcross() * v.nodesDown()];
                for (int k = 0; k < stampK.length; k++) stampK[k] = new MapTiles.KeptNode();
                stampNow = new int[v.w() * v.h()];
                stampToday = new int[v.w() * v.h()];
                viewChanges++;
            }
            long w0 = monthClock();
            MiniMapFrame.pixels(tr, stampV, stampG, stampToday, new LinkedHashSet<>());
            r.lazy += (monthClock() - w0) / 1e6;
            long n2 = monthClock();
            java.util.Set<CityMap.District> wn = new LinkedHashSet<>();
            MiniMapFrame.pixels(tk, stampV, stampG, stampK, stampNow, wn);
            long n3 = monthClock();
            long u0 = monthClock();
            if (round == 0) MiniMapFrame.stamp(MiniMapFrame.frame(m));
            java.util.Set<CityMap.District> wt = new LinkedHashSet<>();
            MiniMapFrame.pixels(tr, stampV, stampG, stampToday, wt);
            long u1 = monthClock();
            stampsDrawn++;
            pixelsCompared += stampNow.length;
            if (!Arrays.equals(stampNow, stampToday) || !new ArrayList<>(wn).equals(new ArrayList<>(wt))) {
                stampEqual = false;
                out.printf("      FAIL %s month %d round %d: the kept stamp differs from the full one in %d pixels, asks %d districts against %d%n", name, r.number, round,
                        differ(stampNow, stampToday), wn.size(), wt.size());
            }
            wanted.addAll(wn);
            // The preview, six nodes: compared, not timed.
            MiniMapFrame.View pv = MiniMapFrame.preview(frame);
            if (!pv.equals(previewV)) {
                previewV = pv;
                previewG = MiniMapFrame.ground(m.worldKey(), pv);
                previewK = new MapTiles.KeptNode[pv.nodesAcross() * pv.nodesDown()];
                for (int k = 0; k < previewK.length; k++) previewK[k] = new MapTiles.KeptNode();
                previewNow = new int[pv.w() * pv.h()];
                previewToday = new int[pv.w() * pv.h()];
            }
            java.util.Set<CityMap.District> pn = new LinkedHashSet<>(), pt = new LinkedHashSet<>();
            MiniMapFrame.pixels(tk, previewV, previewG, previewK, previewNow, pn);
            MiniMapFrame.pixels(tr, previewV, previewG, previewToday, pt);
            previewsDrawn++;
            pixelsCompared += previewNow.length;
            if (!Arrays.equals(previewNow, previewToday) || !new ArrayList<>(pn).equals(new ArrayList<>(pt))) {
                previewEqual = false;
                out.printf("      FAIL %s month %d round %d: the kept preview differs from the full one in %d pixels%n", name, r.number, round, differ(previewNow, previewToday));
            }
            return new long[] { (n1 - n0) + (n3 - n2), u1 - u0 };
        }

        /** Today's draw of a far screen: every node in the month's round, the partial ones after; ns. A `warm` pass only draws, leaving which nodes are partial as the last timed one found them. */
        long todayPass(FarScreen fs, int round, boolean warm) {
            long today = 0;
            for (int i = 0; i < fs.count; i++) {
                if (round > 0 && !fs.todayPartial[i]) continue;
                long t0 = monthClock();
                boolean shortOfPicture = tr.nodePixels(fs.nx[i] * fs.span, fs.ny[i] * fs.span, fs.plots, fs.ground[i], fs.buf) > 0;
                if (!warm) fs.todayPartial[i] = shortOfPicture;
                today += monthClock() - t0;
            }
            return today;
        }

        /**
         * A far screen drawn two ways, the ns of each: now's as ui/MapView.paintNodes() draws it (nodeKey() for every
         * node in the month's round and for the partial ones after; drawn when the key moved, or it was drawn partial
         * and the view asks for pictures - picturesFit()),
         * today's every node in the month's round and the partial ones after. The first screen is the one the figures are
         * of: it has a pass of today's first, untimed, as drawStamp() does; the others are drawn for the comparison.
         */
        long[] drawFar(FarScreen fs, int s, RedrawMonth r, int round, java.util.Set<CityMap.District> wanted) {
            if (s == 0) r.lazy += todayPass(fs, round, true) / 1e6;
            long now = 0;
            // A partial node is held with the rest where the view asks for no pictures (MapView.paintNodes(): picturesFit() of its frame), found once a pass.
            Boolean fits = null;
            for (int i = 0; i < fs.count; i++) {
                if (round > 0 && !fs.partial[i]) continue;
                long x0 = fs.nx[i] * fs.span, y0 = fs.ny[i] * fs.span;
                long t0 = monthClock();
                long key = tk.nodeKey(x0, y0, fs.plots);
                if (s == 0) r.keys += (monthClock() - t0) / 1e6;
                boolean hold = fs.drawn[i] && fs.key[i] == key && !fs.partial[i];
                if (fs.drawn[i] && fs.key[i] == key && fs.partial[i]) {
                    if (fits == null) fits = tk.picturesFit(fs.f);
                    hold = !fits;
                }
                if (hold) {
                    if (round == 0) fs.held[i] = true;
                    now += monthClock() - t0;
                    continue;
                }
                if (round == 0 && fs.drawn[i] && fs.key[i] != key) r.keyMoved.get(s).add(i);
                fs.key[i] = key;
                fs.partial[i] = tk.nodePixels(x0, y0, fs.plots, fs.ground[i], fs.buf) > 0;
                wanted.addAll(tk.wanted());
                System.arraycopy(fs.buf, 0, fs.pix[i], 0, fs.buf.length);
                fs.drawn[i] = true;
                fs.held[i] = false;
                r.recoloured[s]++;
                now += monthClock() - t0;
            }
            return new long[] { now, todayPass(fs, round, false) };
        }

        /** Every node of the screen drawn from the key it has now holds the pixels a fresh nodePixels() gives it: those held through the month are the ones that count. */
        void verify(FarScreen fs) {
            int[] fresh = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
            for (int i = 0; i < fs.count; i++) {
                if (!fs.drawn[i] || fs.key[i] != tk.nodeKey(fs.nx[i] * fs.span, fs.ny[i] * fs.span, fs.plots)) continue;
                tr.nodePixels(fs.nx[i] * fs.span, fs.ny[i] * fs.span, fs.plots, fs.ground[i], fresh);
                keyUnmoved++;
                if (fs.held[i]) heldCompared++;
                if (!Arrays.equals(fresh, fs.pix[i])) {
                    heldEqual = false;
                    out.printf("      FAIL %s node %d (%d, %d) at %d plots a pixel: %s, its key unmoved, differs from a fresh one in %d pixels%n", fs.name, i, fs.nx[i], fs.ny[i], fs.plots,
                            fs.held[i] ? "held" : "drawn", differ(fresh, fs.pix[i]));
                }
            }
        }
    }

    static int differ(int[] a, int[] b) {
        int n = 0;
        for (int i = 0; i < a.length && i < b.length; i++) if (a[i] != b[i]) n++;
        return n;
    }

    static double median18(java.util.List<Double> l) {
        if (l.isEmpty()) return Double.NaN;
        java.util.List<Double> s = new ArrayList<>(l);
        Collections.sort(s);
        return s.get(s.size() / 2);
    }

    static double worst18(java.util.List<Double> l) {
        double w = 0;
        for (double d : l) w = Math.max(w, d);
        return w;
    }

    static int medianInt(java.util.List<Integer> l) {
        if (l.isEmpty()) return 0;
        java.util.List<Integer> s = new ArrayList<>(l);
        Collections.sort(s);
        return s.get(s.size() / 2);
    }

    /** Jerus's city at `times` on the design's square city at the dry place, as Squares builds its copies. */
    static CityMap hisCopy(Squares sq, double times) {
        long[] c = new long[sq.types.length];
        for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(JERUS_COUNTS[t] * times);
        int mine = -1;
        for (BuildingVisual.Type t : sq.types) if (t != null && t.id() == Mining.IRON_MINE) mine = t.id();
        long wantMines = mine >= 0 ? c[mine] : 0;
        if (mine >= 0) c[mine] = 0;
        int side = (int) Math.ceil(Math.sqrt(groundKm2(sq.types) * times / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        int capacity = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap m = CityMap.square(sq.seed, sq.x, sq.y, sq.types, side, capacity, c);
        if (mine >= 0) {
            c[mine] = Math.min(wantMines, mineSites(m, mine));
            m.reconcile(c);
        }
        return m;
    }

    /**
     * The block of PURCHASE_SIDE plots to buy: wholly inside the box of the city's built-up ground (so the stamp's frame
     * is the one it was, however the district's owned ground grows), none of it owned, and the most ground owned on the
     * ring a plot wide about it - next to the built-up ground: {x, y}, or null where there is none.
     */
    static long[] purchaseBlock(CityMap m, CityLand land) {
        MiniMapFrame.Box bu = MiniMapFrame.builtUp(m);
        long[] best = null;
        long most = 0;
        for (long y = bu.y0(); y + PURCHASE_SIDE <= bu.y1(); y += 2) {
            for (long x = bu.x0(); x + PURCHASE_SIDE <= bu.x1(); x += 2) {
                if (land.grid().owned(x, y, x + PURCHASE_SIDE, y + PURCHASE_SIDE) != 0) continue;
                long ring = land.grid().owned(x - 1, y - 1, x + PURCHASE_SIDE + 1, y + PURCHASE_SIDE + 1);
                if (ring > most) {
                    most = ring;
                    best = new long[] { x, y };
                }
            }
        }
        return best;
    }

    static void redrawsOnlyWhatMoved(Game city, Squares sq) {
        out.println("\n--- 18. a month redraws only what moved (batch UP3): the minimap's kept stamp and the far view's kept nodes equal a full redraw,"
                + " redraw only what moved, and a month's FX-side map work at 5B is within MAP_MONTH_MS ---");
        long begun = System.nanoTime();
        CityMap his = hisCopy(sq, HIS_TIMES);
        Redraw x1 = new Redraw("x 1", sq.maps[0], sq.maps[0].totals(), GROW_PER_MILLE, true, false, true, true);
        Redraw x80 = new Redraw("x " + (int) HIS_TIMES, his, his.totals(), GROW_PER_MILLE, true, false, true, true);
        Redraw b5 = new Redraw("5B", sq.maps[1], sq.maps[1].totals(), GROW_PER_TEN_THOUSAND, true, true, true, false);
        Redraw[] fx = { x1, x80, b5 };
        RedrawMonth oneX80 = null, oneB5 = null;
        for (Redraw w : fx) {
            int months = w == b5 ? REDRAW_MONTHS_5B : REDRAW_MONTHS;
            w.month(SETTLE, -1, false);
            for (int i = 0; i < months; i++) w.month(GROW, i, i >= REDRAW_WARM);
            // A month that raises one built type by one, for check 3: at x 80 its far screen (pictures on, drawn aside by the months before), at 5B the one wide enough to draw none.
            if (w == x80) oneX80 = w.month(ONE, months, false);
            if (w == b5) oneB5 = w.month(ONE, months, false);
        }
        for (Redraw w : fx) report18(w);

        // ----- The played city: a land purchase inside its stamp's frame -----
        CityLand copy = city.getCityLand().copy();
        long[] counts = city.getMapCounts();
        CityMap far = CityMap.canonical(copy, r -> copy.amountsInOrder(r), city.getMapTypes(), counts);
        Redraw pl = new Redraw("the played city", far, counts, GROW_PER_MILLE, false, false, false, false);
        pl.month(SETTLE, -1, false);
        for (int i = 0; i < PLAYED_BEFORE; i++) pl.month(GROW, i, false);
        boolean equalBefore = pl.stampEqual && pl.previewEqual;
        MiniMapFrame.Box frameBefore = MiniMapFrame.frame(far);
        long[] block = purchaseBlock(far, copy);
        RedrawMonth bought = null;
        if (block != null) {
            pl.beforeReconcile = () -> copy.extend(new LandParcel(-3, 1, 0, 8, block[0], block[1], block[0] + PURCHASE_SIDE, block[1] + PURCHASE_SIDE, null, null, null, 0, 0), city.getMonth(), 0);
            bought = pl.month(GROW, PLAYED_BEFORE, false);
        }
        for (int i = 0; i < PLAYED_AFTER; i++) pl.month(GROW, PLAYED_BEFORE + 1 + i, false);
        boolean blockOwned = block != null && copy.grid().owned(block[0], block[1], block[0] + PURCHASE_SIDE, block[1] + PURCHASE_SIDE) == (long) PURCHASE_SIDE * PURCHASE_SIDE;
        out.printf("      the played city: %d districts, the stamp at %d plots a pixel; a block of %d x %d plots bought %s inside the built-up ground's box, the frame %s; the stamp's land layer drawn %s times in its months (the first month made it)%n",
                far.districts().size(), pl.stampV.stride(), PURCHASE_SIDE, PURCHASE_SIDE, block == null ? "nowhere" : "at (" + block[0] + ", " + block[1] + ")",
                frameBefore.equals(MiniMapFrame.frame(far)) ? "as before" : "moved", pl.all.stream().map(r -> String.valueOf(r.lands)).toList());

        // ----- 1. The picture after a month equals a full redraw -----
        long stamps = 0, previews = 0, pixels = 0, monthsCompared = 0;
        boolean equal = true;
        for (Redraw w : new Redraw[] { x1, x80, b5, pl }) {
            stamps += w.stampsDrawn;
            previews += w.previewsDrawn;
            pixels += w.pixelsCompared;
            monthsCompared += w.all.size();
            equal &= w.stampEqual && w.previewEqual;
        }
        out.printf("      compared: %d months, %d stamps and %d previews drawn kept and whole, %,d pixels, every one's asked-for districts as lists%n", monthsCompared, stamps, previews, pixels);
        check("fixture: every month reconciled (the map was not restated), on x 1, x 80, 5B and the played city", x1.allReconciled && x80.allReconciled && b5.allReconciled && pl.allReconciled);
        int unsettled = 0;
        for (Redraw w : new Redraw[] { x1, x80, b5, pl }) for (RedrawMonth r : w.all) if (!r.settled) unsettled++;
        check("fixture: the chain settled - no job left to hand out - within " + CHAIN_ROUNDS + " rounds in every month, so the screens are drawn with every picture they can have", unsettled == 0);
        check("the minimap's picture after a month - the stamp and the preview, from the kept nodes - equals a full redraw of the same map, pixel for pixel, asking the same districts"
                + " for their pictures, in every month of x 1, x 80, 5B and the played city", equal && stamps > 0 && previews > 0);
        check("...the played city before its land purchase and after it", equalBefore && pl.stampEqual && pl.previewEqual && bought != null);

        // ----- 2. The far view's nodes -----
        boolean heldEqual = true;
        int unmoved = 0, held = 0;
        for (Redraw w : fx) {
            heldEqual &= w.heldEqual;
            unmoved += w.keyUnmoved;
            held += w.heldCompared;
        }
        out.printf("      far nodes: %d with a key unmoved since they were drawn compared with a fresh nodePixels(), %d of them held through their month%n", unmoved, held);
        check("fixture: months left far nodes held (their key unmoved) on x 1, x 80 and 5B, to compare", x1.heldCompared > 0 && x80.heldCompared > 0 && b5.heldCompared > 0);
        check("the far view's nodes whose key did not move hold the pixels a fresh nodePixels() gives them, every month on x 1, x 80 and 5B", heldEqual);

        // ----- 3. A month that places in one district -----
        oneDistrict(x80, oneX80, 0, true);
        oneDistrict(b5, oneB5, 1, false);

        // ----- 4. The stamp's runs layer -----
        boolean runsOnlyWhenMoved = true;
        for (Redraw w : fx) {
            int still = 0, stillDrawn = 0, laid = 0, laidDrawn = 0;
            for (RedrawMonth r : w.all) {
                if (r.kind == SETTLE || r.viewChanged) continue;
                if (r.runsMoved) {
                    laid++;
                    if (r.runsDrawn == 1) laidDrawn++;
                } else {
                    still++;
                    if (r.runsDrawn == 0) stillDrawn++;
                }
            }
            out.printf("      %s: %d months added no run, the stamp's runs layer left alone in %d; %d months moved the runs, it was drawn once in %d%n", w.name, still, stillDrawn, laid, laidDrawn);
            check("fixture: " + w.name + " has months that add no run and months that lay one (the runs' version did not move / did), the stamp's view the same", still > 0 && laid > 0);
            runsOnlyWhenMoved &= stillDrawn == still && laidDrawn == laid;
        }
        check("the stamp's static layer is not redrawn by a month that adds no run, and a month that lays a highway or a stretch of track redraws it once (its pixels equal a full redraw's, 1)", runsOnlyWhenMoved);

        // ----- 5. The land layer -----
        boolean landOnlyByPurchase = bought != null && !bought.viewChanged && bought.lands == 1;
        for (RedrawMonth r : pl.all) if (r.kind != SETTLE && r != bought) landOnlyByPurchase &= !r.viewChanged && r.lands == 0;
        check("fixture: the block lies inside the built-up ground's box, so inside the stamp's frame, the city owns it after its purchase and the frame is the one it was",
                block != null && blockOwned && frameBefore.equals(MiniMapFrame.frame(far)));
        check("the land layer is drawn again by a purchase and only then: once in the month of the purchase, never in the " + PLAYED_BEFORE + " before it or the " + PLAYED_AFTER
                + " after (the pixels equal a full redraw's, 1)", landOnlyByPurchase);

        // ----- 6. The links' kept stamps -----
        for (Redraw w : new Redraw[] { x1, x80 }) {
            int grown = 0, astray = 0, noLinks = 0;
            for (RedrawMonth r : w.all) {
                if (r.kind == GROW) grown++;
                astray += r.astray;
                if (r.kept <= 0) noLinks++;
            }
            check("stampOf from the cache equals a fresh one for every link kept after each of " + grown + " months on " + w.name + " (none astray, links kept every month)",
                    grown == REDRAW_MONTHS && astray == 0 && noLinks == 0);
        }

        // ----- 7. The budget -----
        java.util.List<Double> nows = new ArrayList<>(), todays = new ArrayList<>();
        for (RedrawMonth r : b5.all) {
            if (!r.timed) continue;
            nows.add(r.nowSum());
            todays.add(r.todaySum());
        }
        double worstNow = worst18(nows), worstToday = worst18(todays);
        out.printf("      5B, %d months read: a month's FX-side map work, worst %.1f ms (median %.1f) against today's %.1f (median %.1f) and the budget %.0f ms%n", nows.size(), worstNow, median18(nows), worstToday, median18(todays), MAP_MONTH_MS);
        // The median month: a month that lays a run moves every far node's key (the runs' key is the city's) and draws the screen whole, as today, plus its keys.
        check("a month's FX-side map work at 5B - the stamp, the far view's screen, an L0 screen re-stamped and the chain's handing out and adopting - takes no more than "
                + (int) MAP_MONTH_MS + " ms of " + (CPU_TIMED ? "the main thread's CPU" : "the wall clock") + ", the median month", nows.size() == REDRAW_MONTHS_5B - REDRAW_WARM && Timing.within(median18(nows), MAP_MONTH_MS));
        out.printf("      section 18 took %.1f s%n", (System.nanoTime() - begun) / 1e9);
    }

    /** Check 3 on one month that raised one built type by one: the far nodes whose key moved against the 3 x 3 districts round the one district that moved, and against every node in view. */
    static void oneDistrict(Redraw w, RedrawMonth one, int s, boolean picturesOn) {
        FarScreen fs = w.fars.get(s);
        CityMap.District d = one.onlyMoved;
        long D = World.DISTRICT;
        long x0 = d == null ? 0 : (w.m.baseDX() + d.dx - 1) * D, y0 = d == null ? 0 : (w.m.baseDY() + d.dy - 1) * D, x1 = x0 + 3 * D, y1 = y0 + 3 * D;
        java.util.List<Integer> moved = one.keyMoved.get(s);
        int outside = 0, unexplained = 0;
        for (int i : moved) {
            if (fs.overlaps(i, x0, y0, x1, y1)) continue;
            outside++;
            // ...where the pictures are drawn, a node over a district whose picture the chain no longer holds current moves its key too (it is dithered until the picture comes back).
            boolean overStale = false;
            for (CityMap.District sd : one.staleList) {
                long sx = (w.m.baseDX() + sd.dx) * D, sy = (w.m.baseDY() + sd.dy) * D;
                overStale |= fs.overlaps(i, sx, sy, sx + D, sy + D);
            }
            if (!picturesOn || !overStale) unexplained++;
        }
        out.printf("      %s, one built type raised by one: %d district(s) moved%s; the far screen of %d nodes at %d plots a pixel (pictures %s): %d nodes' keys moved, %d of them off the 3 x 3 districts round the one, against today's %d (every node in view)%s%n",
                w.name, one.moved, d == null ? "" : " (index " + d.index + " of " + w.m.districts().size() + " in the map's order)", fs.count, fs.plots, fs.pictures ? "on" : "off", moved.size(), outside, fs.count,
                one.built < 0 ? "" : "; the pictures of " + one.stale + " of the " + one.built + " built districts were no longer current after the month (the chain re-plans from the first link that broke)");
        check("fixture: " + w.name + "'s month moved exactly one district's version, and the far screen's nodes were all settled before it (none drawn short of a picture), pictures "
                + (picturesOn ? "on" : "off") + " (" + World.DISTRICT + " / " + fs.plots + " plots a pixel " + (picturesOn ? ">=" : "<") + " " + MapTiles.PICTURE_FROM_PX + ")",
                one.moved == 1 && one.partialBefore[s] == 0 && (World.DISTRICT / fs.plots >= MapTiles.PICTURE_FROM_PX) == picturesOn);
        if (!picturesOn) {
            check("a month that places in one district re-colours only the far nodes over that district and its neighbours - at least one, all within the 3 x 3 districts round it, fewer than today's every node in view - on "
                    + w.name, !moved.isEmpty() && outside == 0 && moved.size() < fs.count);
        } else {
            // Where the far view draws the pictures, a month in one district moves the pictures of every district after it in its band
            // until the chain has re-planned them (CityMap's chain: a band's links hold up to the first that broke; its early exit is
            // UP3's Pending): what is asserted is that no node moves its key but over the district, its neighbours or a picture gone.
            out.printf("      ...%s, pictures on: %d of the %d nodes whose keys moved lie off the 3 x 3 districts, every one over a district whose picture the chain no longer holds current but %d%n",
                    w.name, outside, moved.size(), unexplained);
            check("...where the pictures are drawn (" + w.name + "), a month in one district re-colours only the nodes over it, its neighbours and the districts whose pictures the chain re-plans - at least one",
                    !moved.isEmpty() && unexplained == 0);
        }
    }

    /** The figures section 18 prints for a fixture. */
    static void report18(Redraw w) {
        java.util.List<Double> sn = new ArrayList<>(), st = new ArrayList<>(), fn = new ArrayList<>(), ft = new ArrayList<>(), l0 = new ArrayList<>(), ch = new ArrayList<>(),
                ns = new ArrayList<>(), ts = new ArrayList<>(), keys = new ArrayList<>(), lazy = new ArrayList<>();
        java.util.List<Integer> moved = new ArrayList<>(), rep = new ArrayList<>(), recol = new ArrayList<>(), onStamp = new ArrayList<>();
        int unsettled = 0, runsMoved = 0, runsDrawn = 0;
        for (RedrawMonth r : w.all) {
            if (r.kind == SETTLE) continue;
            if (r.runsMoved) runsMoved++;
            if (r.runsDrawn > 0) runsDrawn++;
            if (!r.timed) continue;
            sn.add(r.stampNow);
            st.add(r.stampToday);
            fn.add(r.farNow);
            ft.add(r.farToday);
            l0.add(r.l0);
            ch.add(r.chain);
            ns.add(r.nowSum());
            ts.add(r.todaySum());
            keys.add(r.keys);
            lazy.add(r.lazy);
            moved.add(r.moved);
            rep.add((int) r.repainted);
            onStamp.add((int) r.onStamp);
            if (r.recoloured.length > 0) recol.add(r.recoloured[0]);
            if (!r.settled) unsettled++;
        }
        out.printf("      %s (%,d districts, %.0f s): in ms, median and worst of %d months, now's / today's - stamp %.2f (%.2f) / %.2f (%.2f); far screen %.2f (%.2f) / %.2f (%.2f), its %d node keys %.2f; L0 re-stamped %.2f (%.2f); chain %.2f (%.2f);"
                        + " the month's sum %.2f (%.2f) / %.2f (%.2f); the lazy checks %.2f%n", w.name, w.m.districts().size(), w.tookS, ns.size(), median18(sn), worst18(sn), median18(st), worst18(st),
                median18(fn), worst18(fn), median18(ft), worst18(ft), w.fars.isEmpty() ? 0 : w.fars.get(0).count, median18(keys), median18(l0), worst18(l0), median18(ch), worst18(ch),
                median18(ns), worst18(ns), median18(ts), worst18(ts), median18(lazy));
        out.printf("      %s: far nodes in view %s, drawn a month %s (median; today's every node, and the partial ones again); districts moved a month %d, the stamp's repainted %d of %d on it;"
                        + " the runs' layer drawn in %d of the %d months the runs moved; %d months not settled in %d rounds; the stamp's view made %d times%n", w.name,
                w.fars.stream().map(f -> f.count + " at " + f.plots + " plots a px").toList(), recol.isEmpty() ? "-" : String.valueOf(medianInt(recol)), medianInt(moved), medianInt(rep),
                medianInt(onStamp), runsDrawn, runsMoved, unsettled, CHAIN_ROUNDS, w.viewChanges);
    }

    /* =====================================================================
       THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md
       9.3 and earth2/mockup-report.md boards 1 and 2): on the fixture's
       Medellin, whose hills are foothill, steep and very steep over grass and
       forest, and the Halifax Narrows, whose channel is shallow sea
       ===================================================================== */

    /**
     * The colour a plot of cover `c` and relief `r` is painted, said again from TileRaster's constants (D4, D5, D19): flat
     * ground, water and ice (which have no relief, and white wins over a slope) in their cover's colour; open dry ground (grass,
     * the beach, the desert) on a slope its grey; forest on a slope its green shaded toward that grey by FOREST_MIX.
     */
    static int reliefColour(int c, int r) {
        if (r == World.FLAT || !World.dry(c)) return TileRaster.GROUND[c];
        return c == World.FOREST ? TileRaster.blend(TileRaster.GROUND[World.FOREST], TileRaster.RELIEF_GREY[r], TileRaster.FOREST_MIX[r]) : TileRaster.RELIEF_GREY[r];
    }

    /** A map's relief on EarthCheck's fixture's key at (lat, lon): the data in use (EarthTerrain.use()) and the key of the Earth world there. */
    static World.Key fixtureKey(double[] at) throws Exception {
        EarthData data = EarthData.of(EarthCheck.resource(EarthCheck.FIXTURE));
        return World.Key.earth(EarthTerrainCheck.SEED, at[0], at[1], EarthTerrain.use(data));
    }

    /** Section 10: the plane's plots and the far node over Medellin painted by their relief; a generated world painted as it was. */
    static void reliefOnMap(BuildingVisual.Type[] types) throws Exception {
        if (EarthCheck.resource(EarthCheck.FIXTURE) == null) return;
        out.println("      the relief (ET1 slice D, board 1): each plot's grey by the relief plane, forest shaded toward it, ice white, a generated world as it was");
        int tile = TilePainter.TILE, px = 4;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        java.util.Arrays.fill(in.owned, true);
        // The rule on every cover at every relief, a plot each: the painter's colour and the raster's pixel.
        int[] img = new int[tile * px * tile * px];
        for (int r = 0; r < World.RELIEFS; r++) for (int c = 0; c < World.CLASSES; c++) { in.terrain[r * World.CLASSES + c] = (byte) c; in.relief[r * World.CLASSES + c] = (byte) r; }
        TilePainter.paint(in, p);
        TileRaster.raster(in, p, px, img);
        boolean rule = true, raster = true, greys = true, shaded = true, white = true, water = true, flat = true;
        for (int r = 0; r < World.RELIEFS; r++) {
            for (int c = 0; c < World.CLASSES; c++) {
                int i = r * World.CLASSES + c, want = reliefColour(c, r);
                rule &= TileRaster.groundColour(in, p, i) == want;
                raster &= img[(i % tile) * px] == want;
                if (r == World.FLAT) flat &= want == TileRaster.GROUND[c];
                else if (c == World.GRASS || c == World.SAND || c == World.DESERT) greys &= want == TileRaster.RELIEF_GREY[r];
                else if (c == World.FOREST) shaded &= want != TileRaster.GROUND[World.FOREST] && want != TileRaster.RELIEF_GREY[r];
                else if (c == World.ICE) white &= want == TileRaster.GROUND[World.ICE];
                else water &= want == TileRaster.GROUND[c];
            }
        }
        int[] grey = TileRaster.RELIEF_GREY;
        boolean order = EarthTerrainCheck.luma(grey[World.FOOTHILL]) > EarthTerrainCheck.luma(grey[World.STEEP])
                && EarthTerrainCheck.luma(grey[World.STEEP]) > EarthTerrainCheck.luma(grey[World.VERY_STEEP])
                && EarthTerrainCheck.luma(reliefColour(World.FOREST, World.FOOTHILL)) > EarthTerrainCheck.luma(reliefColour(World.FOREST, World.STEEP))
                && EarthTerrainCheck.luma(reliefColour(World.FOREST, World.STEEP)) > EarthTerrainCheck.luma(reliefColour(World.FOREST, World.VERY_STEEP));
        check("a plot is painted by its cover and relief: flat ground its cover's colour; grass, the beach and the desert on a slope its grey, foothill lightest to very steep darkest",
                rule && raster && flat && greys && order);
        check("...forest on a slope its green shaded toward its slope's grey (neither forest's own nor the grey), ice white whatever the slope, water as it is", shaded && white && water);

        // Medellin's hills in the fixture: every plot of a 21 x 21 tile screen painted by its relief; the three greys and the shaded forest all drawn.
        World.Key vk = fixtureKey(EarthTerrainCheck.VALLEY);
        World vw = World.of(vk);
        long c0 = vw.foundingX() / World.TILE, r0 = vw.foundingY() / World.TILE;
        long[][] seen = new long[World.RELIEFS][2];
        long plots = 0, wrong = 0;
        for (long ty = r0 - 10; ty <= r0 + 10; ty++) {
            for (long tx = c0 - 10; tx <= c0 + 10; tx++) {
                in.tx = tx;
                in.ty = ty;
                vw.tileTerrain(tx, ty, in.terrain);
                vw.tileRelief(tx, ty, in.relief);
                TilePainter.paint(in, p);
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    int c = in.terrain[i], r = in.relief[i];
                    plots++;
                    if (TileRaster.groundColour(in, p, i) != reliefColour(c, r)) wrong++;
                    if (c == World.FOREST) seen[r][1]++;
                    else if (World.dry(c)) seen[r][0]++;
                }
            }
        }
        out.printf("      Medellin's 21 x 21 tiles (%,d plots): open ground flat %,d, foothill %,d, steep %,d, very steep %,d; forest flat %,d, on foothill %,d, steep %,d, very steep %,d%n", plots,
                seen[0][0], seen[1][0], seen[2][0], seen[3][0], seen[0][1], seen[1][1], seen[2][1], seen[3][1]);
        boolean hills = true;
        for (int r = World.FOOTHILL; r < World.RELIEFS; r++) hills &= seen[r][0] > 0 && seen[r][1] > 0;
        check("Medellin's hills in the fixture: every plot painted by its relief, and the three greys and the three shades of forest all on the screen", wrong == 0 && hills);

        // The seam: the map's input carries the world's relief; a generated map's is flat and paints its cover's colours, as it did.
        int cap = (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap vm = CityMap.square(vk, vw.foundingX(), vw.foundingY(), types, 1, cap, new long[types.length]);
        byte[] want = new byte[TilePainter.PLOTS];
        boolean seam = true, anyRelief = false;
        MapTiles vt = new MapTiles(vm);
        for (long ty = r0 - 1; ty <= r0 + 1; ty++) {
            for (long tx = c0 - 1; tx <= c0 + 1; tx++) {
                vw.tileRelief(tx, ty, want);
                vm.tileInput(tx, ty, in);
                seam &= Arrays.equals(in.relief, want);
                vt.input(tx, ty, in);
                seam &= Arrays.equals(in.relief, want);
                for (byte b : want) anyRelief |= b != World.FLAT;
            }
        }
        check("the map's tile input carries the world's relief (CityMap.tileInput(), MapTiles.input()), and the plots about Medellin's centre hold some", seam && anyRelief);
        World gw = World.of(EarthTerrainCheck.SEED);
        CityMap gm = CityMap.square(World.Key.generated(EarthTerrainCheck.SEED), gw.foundingX(), gw.foundingY(), types, 1, cap, new long[types.length]);
        boolean genFlat = true, genSame = true;
        for (long ty = gw.foundingY() / World.TILE - 1; ty <= gw.foundingY() / World.TILE + 1; ty++) {
            for (long tx = gw.foundingX() / World.TILE - 1; tx <= gw.foundingX() / World.TILE + 1; tx++) {
                gm.tileInput(tx, ty, in);
                for (byte b : in.relief) genFlat &= b == World.FLAT;
                java.util.Arrays.fill(in.owned, true);
                TilePainter.paint(in, p);
                for (int i = 0; i < TilePainter.PLOTS; i++) genSame &= TileRaster.groundColour(in, p, i) == TileRaster.GROUND[in.terrain[i]];
            }
        }
        check("a generated world is flat everywhere and paints its cover's colour at every plot, byte for byte as before (D19)", genFlat && genSame);

        // The far node over Medellin: the relief beside the cover, the pixels painted by it, and the kept node equal; timed.
        int n = MapFrame.NODE_PX;
        long x0 = EarthTerrain.spotPlot() - EarthTerrainCheck.WINDOW / 2;
        byte[] relief = new byte[n * n], ground = MapTiles.nodeTerrain(vk, x0, x0, WINDOW_STRIDE, relief), plain = MapTiles.nodeTerrain(vk, x0, x0, WINDOW_STRIDE);
        boolean dryOnly = true;
        long[] shares = new long[World.RELIEFS];
        for (int k = 0; k < ground.length; k++) {
            shares[relief[k]]++;
            dryOnly &= relief[k] == World.FLAT || World.dry(ground[k]);
        }
        MapTiles tiles = new MapTiles(vm);
        int[] withRelief = new int[n * n], without = new int[n * n], flatRelief = new int[n * n], kept = new int[n * n];
        tiles.nodePixels(x0, x0, WINDOW_STRIDE, ground, relief, withRelief);
        boolean[] edge = tiles.lastEdge().clone();
        tiles.nodePixels(x0, x0, WINDOW_STRIDE, ground, null, without);
        tiles.nodePixels(x0, x0, WINDOW_STRIDE, ground, new byte[n * n], flatRelief);
        tiles.nodePixels(new MapTiles.KeptNode(), x0, x0, WINDOW_STRIDE, ground, relief, kept);
        int farWrong = 0, flatWrong = 0, greyed = 0;
        for (int k = 0; k < withRelief.length; k++) {
            int i = k % n, j = k / n;
            if (edge[k]) continue;   // the city's edge, blended over its ground
            int base = reliefColour(ground[k], relief[k]), flatBase = TileRaster.GROUND[ground[k]];
            // The city owns the district at its centre, undimmed there; the rest of the node is dimmed (TileRaster.UNOWNED_DIM).
            boolean cityLand = Math.abs(i - n / 2) < World.DISTRICT / WINDOW_STRIDE && Math.abs(j - n / 2) < World.DISTRICT / WINDOW_STRIDE;
            if (withRelief[k] != TileRaster.blend(base, TileRaster.VOID, TileRaster.UNOWNED_DIM) && !(cityLand && withRelief[k] == base)) farWrong++;
            if (without[k] != TileRaster.blend(flatBase, TileRaster.VOID, TileRaster.UNOWNED_DIM) && !(cityLand && without[k] == flatBase)) flatWrong++;
            if (relief[k] != World.FLAT && withRelief[k] != without[k]) greyed++;
        }
        out.printf("      Medellin's far node (%d plots a pixel): flat %,d, foothill %,d, steep %,d, very steep %,d of its %,d pixels; %,d painted other than flat's colour; %d unlike the rule's, %d of the flat's, kept %s, flat-asked %s%n",
                WINDOW_STRIDE, shares[0], shares[1], shares[2], shares[3], ground.length, greyed, farWrong, flatWrong, Arrays.equals(withRelief, kept), Arrays.equals(without, flatRelief));
        check("a far node's cover is the same with its relief asked for, and its relief is on dry ground alone, all four classes present over Medellin",
                Arrays.equals(ground, plain) && dryOnly && shares[World.FOOTHILL] > 0 && shares[World.STEEP] > 0 && shares[World.VERY_STEEP] > 0 && shares[World.FLAT] > 0);
        check("...its pixels painted by cover and relief (TileRaster.colour()), the city's ground and edge aside; with no relief, or all flat, the pixels as before; the kept node the same",
                farWrong == 0 && flatWrong == 0 && greyed > 0 && Arrays.equals(withRelief, kept) && Arrays.equals(without, flatRelief));
        double best = Double.MAX_VALUE;
        for (int r = 0; r < WARM_ROUNDS + TIMED_ROUNDS; r++) {
            long t0 = System.nanoTime();
            byte[] g = MapTiles.nodeTerrain(vk, x0, x0, WINDOW_STRIDE, relief);
            tiles.nodePixels(x0, x0, WINDOW_STRIDE, g, relief, withRelief);
            double ms = (System.nanoTime() - t0) / 1e6;
            if (r >= WARM_ROUNDS) best = Math.min(best, ms);
        }
        out.printf("      Medellin's far node with its relief, ground and pixels: %.1f ms%n", best);
        check("...the node and its pixels drawn in no more than SCREEN_MS (" + (int) SCREEN_MS + " ms)", Timing.within(best, SCREEN_MS));
        // A generated world's far node asked for its relief is flat, and paints as before.
        World.Key gk = World.Key.generated(EarthTerrainCheck.SEED);
        byte[] grel = new byte[n * n], gground = MapTiles.nodeTerrain(gk, gw.foundingX() - 512, gw.foundingY() - 512, 8, grel);
        boolean genNode = Arrays.equals(gground, MapTiles.nodeTerrain(gk, gw.foundingX() - 512, gw.foundingY() - 512, 8));
        for (byte b : grel) genNode &= b == World.FLAT;
        check("a generated world's far node asked for its relief is all flat, its cover the same", genNode);
    }

    /** Section 11: a built district's far node over Medellin's hills: the greys on the city's own ground, undimmed, and nowhere else but the plan's own colours. */
    static void pictureRelief(BuildingVisual.Type[] types) throws Exception {
        if (EarthCheck.resource(EarthCheck.FIXTURE) == null) return;
        out.println("\n--- 11 (ET1 slice D). the district pictures over relief: the far node's own ground under a built city is painted by its relief ---");
        World.Key vk = fixtureKey(EarthTerrainCheck.VALLEY);
        World vw = World.of(vk);
        long[] c = new long[types.length];
        for (int t = 0; t < JERUS_COUNTS.length && t < c.length; t++) c[t] = JERUS_COUNTS[t];
        for (BuildingVisual.Type t : types) if (t != null && t.id() == Mining.IRON_MINE) c[t.id()] = 0;
        int side = (int) Math.ceil(Math.sqrt(groundKm2(types) / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(vk, vw.foundingX(), vw.foundingY(), types, side, (int) Math.round(JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), c);
        MapTiles tiles = new MapTiles(m);
        int n = MapFrame.NODE_PX, plots = 16;
        long span = (long) n * plots, x0 = Math.floorDiv(vw.foundingX(), span) * span, y0 = Math.floorDiv(vw.foundingY(), span) * span;
        byte[] relief = new byte[n * n], ground = MapTiles.nodeTerrain(vk, x0, y0, plots, relief);
        int[] px = new int[n * n];
        // The pictures it lacks are drawn from the plans (the view asks CityMap.pictureJobs()); here as the pictures section does, so a district is a picture when its node asks.
        tiles.nodePixels(x0, y0, plots, ground, relief, px);
        for (CityMap.District d : new ArrayList<>(tiles.wanted())) for (CityMap.Job j : m.pictureJobs(d)) { j.run(); m.adopt(j); }
        tiles.nodePixels(x0, y0, plots, ground, relief, px);
        boolean[] edge = tiles.lastEdge().clone(), run = tiles.lastRun().clone();
        java.util.Set<Integer> buildings = new java.util.HashSet<>();
        for (int code = 1; code < DistrictPicture.CODES; code++) buildings.add(DistrictPicture.colour(code));
        int own = 0, ownGrey = 0, other = 0, owned = 0;
        for (int k = 0; k < px.length; k++) {
            int base = reliefColour(ground[k], relief[k]), dim = TileRaster.blend(base, TileRaster.VOID, TileRaster.UNOWNED_DIM);
            if (edge[k] || run[k] || buildings.contains(px[k])) continue;
            if (px[k] == base) { own++; if (relief[k] != World.FLAT) ownGrey++; }
            else if (px[k] != dim) other++;
        }
        out.printf("      the node over the city (%d districts, %d plots a pixel): %,d pixels of ground the city owns, %,d of them on a slope, %,d of no ground colour%n",
                m.districts().size(), plots, own, ownGrey, other);
        check("a far node over a built city on Medellin's hills draws the city's own bare ground by its relief, undimmed, and the rest dimmed - no pixel but a building's, a run's or its edge another colour",
                other == 0 && ownGrey > 0 && own > ownGrey);
    }

    /** Section 13: the words and the legend by relief; the globe's hover and its picture's codes. */
    static void reliefWordsAndLegend(BuildingVisual.Type[] types) throws Exception {
        if (EarthCheck.resource(EarthCheck.FIXTURE) == null) return;
        out.println("      the relief's words (ET1 slice D, board 2): the hover's first line by relief and depth, the legend's greys, only on an Earth world with relief");
        World.Key vk = fixtureKey(EarthTerrainCheck.VALLEY), hk = fixtureKey(EarthTerrainCheck.NARROWS);
        World vw = World.of(vk), hw = World.of(hk);
        CityLand vland = LandManager.foundingLand(vk), hland = LandManager.foundingLand(hk);
        // The hover on land: Medellin's plots by relief, words from the constants; the first line says the grade.
        long c0 = vw.foundingX(), r0 = vw.foundingY();
        int[] said = new int[World.RELIEFS];
        boolean land = true;
        for (long y = r0 - 320; y <= r0 + 320 && land; y += 7) {
            for (long x = c0 - 320; x <= c0 + 320; x += 7) {
                byte g = vw.terrainAt(x, y);
                int r = vw.reliefAt(x, y);
                String first = LandMap.hoverWords(vland, null, x, y).get(0);
                if (World.salt(g) || !World.dry(g)) { land &= first.equals(LandMap.groundWords(g)) || World.salt(g); continue; }
                String name = g == World.FOREST ? LandMap.FOREST_ON_WORDS[r] : r == World.FLAT ? LandMap.groundWords(g) : LandMap.RELIEF_NAMES[r];
                land &= first.equals(name + " · " + LandMap.GRADE_WORDS[r]) && first.equals(LandMap.plotGroundWords(vland, g, x, y)) && first.equals(LandMap.groundWords(g, r, -1));
                said[r]++;
            }
        }
        out.printf("      Medellin's hover first lines over 2,000 plots: flat %,d, foothill %,d, steep %,d, very steep %,d%n", said[0], said[1], said[2], said[3]);
        check("on an Earth world with relief the hover's first line is the plot's ground and grade: \"Foothill\" and its grade on open ground, \"Forest on steep ground\" and so on, flat \"Grass\" with \"flat\"",
                land && said[World.FLAT] > 0 && said[World.FOOTHILL] > 0 && said[World.STEEP] > 0 && said[World.VERY_STEEP] > 0
                        && LandMap.groundWords(World.GRASS, World.FOOTHILL, -1).startsWith(LandMap.RELIEF_NAMES[World.FOOTHILL])
                        && LandMap.groundWords(World.GRASS, World.FOOTHILL, -1).contains(LandMap.GRADE_WORDS[World.FOOTHILL])
                        && LandMap.groundWords(World.ICE, World.VERY_STEEP, -1).equals(LandMap.groundWords(World.ICE)));
        // ...on the sea: the Narrows' shallow channel and Bedford Basin's deep, each with its depth.
        double[] ll = { EarthTerrainCheck.NARROWS[0], EarthTerrainCheck.NARROWS[1] }, xy = new double[2];
        hw.earthTerrain().projection().plotOf(ll[0], ll[1], xy);
        long hx = (long) Math.floor(xy[0]), hy = (long) Math.floor(xy[1]);
        hw.earthTerrain().projection().plotOf(EarthTerrainCheck.BASIN[0], EarthTerrainCheck.BASIN[1], xy);
        long bx = (long) Math.floor(xy[0]), by = (long) Math.floor(xy[1]);
        byte narrows = hw.terrainAt(hx, hy), basin = hw.terrainAt(bx, by);
        String nw = LandMap.hoverWords(hland, null, hx, hy).get(0), bw = LandMap.hoverWords(hland, null, bx, by).get(0);
        out.printf("      the Narrows: %s%n      Bedford Basin: %s%n", nw, bw);
        check("on the sea the first line carries the depth: the Narrows \"Shallow sea\" and its metres, Bedford Basin \"Sea\" and its",
                narrows == World.SHALLOW && basin == World.SALT && nw.equals(LandMap.seaWords(World.SHALLOW, hw.depthAt(hx, hy)))
                        && bw.equals(LandMap.seaWords(World.SALT, hw.depthAt(bx, by))) && nw.startsWith(LandMap.SHALLOW_WORDS) && bw.startsWith(LandMap.groundWords(World.SALT))
                        && hw.depthAt(hx, hy) <= 30);
        // A generated world's hover is as it was; the globe's, past the plane, says the code's ground and grade, and the sea's depth where the point is.
        long gseed = EarthTerrainCheck.SEED;
        World.Key gk = World.Key.generated(gseed);
        World gw = World.of(gk);
        CityLand gland = LandManager.foundingLand(gk);
        boolean generated = !LandMap.reliefDrawn(gk) && LandMap.reliefDrawn(vk) && LandMap.reliefDrawn(hk);
        for (long y = gw.foundingY() - 100; y <= gw.foundingY() + 100; y += 9) {
            for (long x = gw.foundingX() - 100; x <= gw.foundingX() + 100; x += 9) generated &= LandMap.hoverWords(gland, null, x, y).get(0).equals(LandMap.groundWords(gw.terrainAt(x, y)));
        }
        check("a generated world's hover first line is its ground's words, as before, and only the fixture's Earth worlds are drawn with relief (LandMap.reliefDrawn())", generated);
        GlobeFrame away = new GlobeFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H, EarthTerrainCheck.VALLEY[0], EarthTerrainCheck.VALLEY[1]);
        double[] far = GlobeFrame.destination(EarthTerrainCheck.VALLEY[0], EarthTerrainCheck.VALLEY[1], 135, 2000);
        away.set(far[0], far[1], 0.1);
        byte[] drawn = { GlobePicture.code(World.FOREST, World.STEEP), GlobePicture.code(World.GRASS, World.VERY_STEEP), GlobePicture.code(World.ICE, World.FLAT), World.SALT };
        boolean globe = true;
        double sx = MapTiles.DESIGN_W / 2.0, sy = MapTiles.DESIGN_H / 2.0;
        double[] at = away.latLonAt(sx, sy);
        for (byte d : drawn) {
            List<String> words = LandMap.globeHoverWords(away, vland, null, sx, sy, d, LandMap.CARD_FIELDS);
            String first = LandMap.pointGroundWords(vw.earthTerrain(), d, at[0], at[1]);
            globe &= words.get(0).equals(first) && words.get(1).equals(LandMap.NOT_THE_CITYS);
        }
        globe &= LandMap.pointGroundWords(vw.earthTerrain(), drawn[0], at[0], at[1]).equals(LandMap.FOREST_ON_WORDS[World.STEEP] + " · " + LandMap.GRADE_WORDS[World.STEEP])
                && LandMap.pointGroundWords(vw.earthTerrain(), drawn[1], at[0], at[1]).equals(LandMap.RELIEF_NAMES[World.VERY_STEEP] + " · " + LandMap.GRADE_WORDS[World.VERY_STEEP])
                && LandMap.pointGroundWords(vw.earthTerrain(), drawn[2], at[0], at[1]).equals(LandMap.groundWords(World.ICE))
                && LandMap.pointGroundWords(vw.earthTerrain(), drawn[3], at[0], at[1]).startsWith(LandMap.groundWords(World.SALT));
        check("the globe's hover past the plane says the ground it drew by its code: the class and its grade, ice as it is, the sea with its depth", globe);
        // The legend: the greys, forest on a slope and the shallow sea where the world has relief; the legend as it was where it has none.
        List<LandMap.Entry> pic = LandMap.legend(GlobeFrame.PICTURE, true), gro = LandMap.legend(GlobeFrame.GROUND, true), pla = LandMap.legend(GlobeFrame.PLANE, true);
        List<LandMap.Entry> pic0 = LandMap.legend(GlobeFrame.PICTURE), pla0 = LandMap.legend(GlobeFrame.PLANE);
        boolean legend = pic.size() == pic0.size() + World.RELIEFS && gro.size() == pic.size() + 1 && pic0.equals(LandMap.legend(GlobeFrame.PICTURE, false))
                && pla0.equals(LandMap.planeLegend()) && pla.equals(LandMap.planeLegend(true)) && pla.size() == pla0.size() + World.RELIEFS + 1 && gro.subList(0, pic.size()).equals(pic);
        for (int r = World.FOOTHILL; r < World.RELIEFS; r++) {
            final int rr = r;
            legend &= pic.contains(new LandMap.Entry(TileRaster.RELIEF_GREY[r], LandMap.RELIEF_NAMES[r])) && pla.contains(new LandMap.Entry(TileRaster.RELIEF_GREY[r], LandMap.RELIEF_NAMES[r]))
                    && pic0.stream().noneMatch(en -> en.words().equals(LandMap.RELIEF_NAMES[rr]));
        }
        LandMap.Entry shaded = new LandMap.Entry(reliefColour(World.FOREST, World.STEEP), LandMap.FOREST_SLOPE_WORDS);
        legend &= pic.contains(shaded) && pla.contains(shaded) && !pic0.contains(shaded);
        // The globe's legend keeps the shallow sea (and its words) and the plane's gains it.
        LandMap.Entry shallow = new LandMap.Entry(TileRaster.GROUND[World.SHALLOW], LandMap.SHALLOW_WORDS);
        legend &= pic0.contains(shallow) && pic.contains(shallow) && gro.contains(shallow) && pla.contains(shallow) && !pla0.contains(shallow);
        check("the legend on an Earth world with relief has the three greys, forest on a slope and the shallow sea, in the globe's tiers and the plane's; without, as it was", legend);
        // The picture's colours: a table of a code each, its FLAT row the classes' colours, the relief's rows the map's.
        int[] colours = GlobePicture.colours(false);
        boolean table = colours.length == GlobePicture.CODES && GlobePicture.CODES == World.RELIEFS * World.CLASSES;
        for (int r = 0; r < World.RELIEFS; r++) for (int cl = 0; cl < World.CLASSES; cl++) table &= colours[GlobePicture.code(cl, r)] == reliefColour(cl, r)
                && GlobePicture.classOf(GlobePicture.code(cl, r)) == cl && GlobePicture.reliefOf(GlobePicture.code(cl, r)) == r;
        int[] dimmed = GlobePicture.colours(true);
        for (int k = 0; k < colours.length; k++) table &= dimmed[k] == TileRaster.blend(colours[k], TileRaster.VOID, TileRaster.UNOWNED_DIM);
        check("the globe's colour table is a colour for every code - a class and its relief - the map's own, and the world off the city's land dimmed as the far nodes dim it", table);
    }
}
